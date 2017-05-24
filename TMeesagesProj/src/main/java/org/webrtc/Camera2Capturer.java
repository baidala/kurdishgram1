package org.webrtc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCaptureSession.CaptureCallback;
import android.hardware.camera2.CameraCaptureSession.StateCallback;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureRequest.Builder;
import android.hardware.camera2.TotalCaptureResult;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Range;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

@TargetApi(21)
public class Camera2Capturer
  implements CameraVideoCapturer, SurfaceTextureHelper.OnTextureFrameAvailableListener
{
  private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
  private static final int OPEN_CAMERA_DELAY_MS = 500;
  private static final int START_TIMEOUT = 10000;
  private static final int STOP_TIMEOUT = 10000;
  private static final Object STOP_TIMEOUT_RUNNABLE_TOKEN = new Object();
  private static final String TAG = "Camera2Capturer";
  private Context applicationContext;
  private CameraDevice cameraDevice;
  private final CameraManager cameraManager;
  private String cameraName;
  private int cameraOrientation;
  private volatile CameraState cameraState = CameraState.IDLE;
  private final Object cameraStateLock = new Object();
  private CameraVideoCapturer.CameraStatistics cameraStatistics;
  private Handler cameraThreadHandler;
  private CameraEnumerationAndroid.CaptureFormat captureFormat;
  private CameraCaptureSession captureSession;
  private VideoCapturer.CapturerObserver capturerObserver;
  private int consecutiveCameraOpenFailures;
  private final CameraVideoCapturer.CameraEventsHandler eventsHandler;
  private boolean firstFrameReported;
  private int fpsUnitFactor;
  private boolean isFrontCamera;
  private final AtomicBoolean isPendingCameraSwitch = new AtomicBoolean();
  private int requestedFramerate;
  private int requestedHeight;
  private int requestedWidth;
  private Surface surface;
  private SurfaceTextureHelper surfaceTextureHelper;
  private CameraVideoCapturer.CameraSwitchHandler switchEventsHandler;

  public Camera2Capturer(Context paramContext, String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler)
  {
    Logging.d("Camera2Capturer", "Camera2Capturer ctor, camera name: " + paramString);
    this.cameraManager = ((CameraManager)paramContext.getSystemService("camera"));
    this.eventsHandler = paramCameraEventsHandler;
    setCameraName(paramString);
  }

  private void checkIsOnCameraThread()
  {
    if (!isOnCameraThread())
      throw new IllegalStateException("Not on camera thread");
  }

  private void checkNotOnCameraThread()
  {
    if (this.cameraThreadHandler == null);
    do
      return;
    while (Thread.currentThread() != this.cameraThreadHandler.getLooper().getThread());
    throw new IllegalStateException("Method waiting for camera state to change executed on camera thread");
  }

  private void closeAndRelease()
  {
    checkIsOnCameraThread();
    Logging.d("Camera2Capturer", "Close and release.");
    setCameraState(CameraState.STOPPING);
    this.capturerObserver.onCapturerStopped();
    this.cameraThreadHandler.removeCallbacksAndMessages(this);
    if (this.cameraStatistics != null)
    {
      this.cameraStatistics.release();
      this.cameraStatistics = null;
    }
    if (this.surfaceTextureHelper != null)
      this.surfaceTextureHelper.stopListening();
    if (this.captureSession != null)
    {
      this.captureSession.close();
      this.captureSession = null;
    }
    if (this.surface != null)
    {
      this.surface.release();
      this.surface = null;
    }
    if (this.cameraDevice != null)
    {
      this.cameraThreadHandler.postAtTime(new Runnable()
      {
        public void run()
        {
          Logging.e("Camera2Capturer", "Camera failed to stop within the timeout. Force stopping.");
          Camera2Capturer.this.setCameraState(Camera2Capturer.CameraState.IDLE);
          if (Camera2Capturer.this.eventsHandler != null)
            Camera2Capturer.this.eventsHandler.onCameraError("Camera failed to stop (timeout).");
        }
      }
      , STOP_TIMEOUT_RUNNABLE_TOKEN, SystemClock.uptimeMillis() + 10000L);
      this.cameraDevice.close();
      this.cameraDevice = null;
      return;
    }
    Logging.w("Camera2Capturer", "closeAndRelease called while cameraDevice is null");
    setCameraState(CameraState.IDLE);
  }

  private int getDeviceOrientation()
  {
    switch (((WindowManager)this.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation())
    {
    default:
      return 0;
    case 1:
      return 90;
    case 2:
      return 180;
    case 3:
    }
    return 270;
  }

  private boolean isInitialized()
  {
    return (this.applicationContext != null) && (this.capturerObserver != null);
  }

  private boolean isOnCameraThread()
  {
    return Thread.currentThread() == this.cameraThreadHandler.getLooper().getThread();
  }

  private void openCamera()
  {
    try
    {
      checkIsOnCameraThread();
      if (this.cameraState != CameraState.STARTING)
        throw new IllegalStateException("Camera should be in state STARTING in openCamera.");
    }
    catch (CameraAccessException localCameraAccessException)
    {
      reportError("Failed to open camera: " + localCameraAccessException);
      return;
    }
    this.cameraManager.openCamera(this.cameraName, new CameraStateCallback(), this.cameraThreadHandler);
  }

  private void postDelayedOnCameraThread(int paramInt, Runnable paramRunnable)
  {
    synchronized (this.cameraStateLock)
    {
      if (((this.cameraState != CameraState.STARTING) && (this.cameraState != CameraState.RUNNING)) || (!this.cameraThreadHandler.postAtTime(paramRunnable, this, SystemClock.uptimeMillis() + paramInt)))
        Logging.w("Camera2Capturer", "Runnable not scheduled even though it was requested.");
      return;
    }
  }

  private void postOnCameraThread(Runnable paramRunnable)
  {
    postDelayedOnCameraThread(0, paramRunnable);
  }

  private void reportError(String paramString)
  {
    checkIsOnCameraThread();
    Logging.e("Camera2Capturer", "Error in camera at state " + this.cameraState + ": " + paramString);
    if (this.switchEventsHandler != null)
    {
      this.switchEventsHandler.onCameraSwitchError(paramString);
      this.switchEventsHandler = null;
    }
    this.isPendingCameraSwitch.set(false);
    switch (5.$SwitchMap$org$webrtc$Camera2Capturer$CameraState[this.cameraState.ordinal()])
    {
    default:
      throw new RuntimeException("Unknown camera state: " + this.cameraState);
    case 1:
      this.capturerObserver.onCapturerStarted(false);
    case 2:
      if (this.eventsHandler != null)
        this.eventsHandler.onCameraError(paramString);
      closeAndRelease();
      return;
    case 3:
    }
    setCameraState(CameraState.IDLE);
    Logging.e("Camera2Capturer", "Closing camera failed: " + paramString);
  }

  private void setCameraName(String arg1)
  {
    String str;
    try
    {
      localObject2 = this.cameraManager.getCameraIdList();
      str = ???;
      if (???.isEmpty())
      {
        str = ???;
        if (localObject2.length != 0)
          str = localObject2[0];
      }
      if (!Arrays.asList(localObject2).contains(str))
        throw new IllegalArgumentException("Camera name: " + str + " does not match any known camera device:");
    }
    catch (CameraAccessException )
    {
      throw new RuntimeException("Camera access exception: " + ???);
    }
    Object localObject2 = this.cameraManager.getCameraCharacteristics(str);
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStopIfStopping();
      if (this.cameraState != CameraState.IDLE)
        throw new RuntimeException("Changing camera name on running camera.");
    }
    this.cameraName = localObject1;
    if (((Integer)((CameraCharacteristics)localObject2).get(CameraCharacteristics.LENS_FACING)).intValue() == 0);
    for (boolean bool = true; ; bool = false)
    {
      this.isFrontCamera = bool;
      this.cameraOrientation = ((Integer)((CameraCharacteristics)localObject2).get(CameraCharacteristics.SENSOR_ORIENTATION)).intValue();
      monitorexit;
      return;
    }
  }

  private void setCameraState(CameraState paramCameraState)
  {
    if (this.cameraState != CameraState.IDLE)
      checkIsOnCameraThread();
    switch (5.$SwitchMap$org$webrtc$Camera2Capturer$CameraState[paramCameraState.ordinal()])
    {
    default:
      throw new RuntimeException("Unknown camera state: " + paramCameraState);
    case 1:
      if (this.cameraState == CameraState.IDLE)
        break;
      throw new IllegalStateException("Only stopped camera can start.");
    case 2:
      if (this.cameraState == CameraState.STARTING)
        break;
      throw new IllegalStateException("Only starting camera can go to running state.");
    case 3:
      if ((this.cameraState == CameraState.STARTING) || (this.cameraState == CameraState.RUNNING))
        break;
      throw new IllegalStateException("Only starting or running camera can stop.");
    case 4:
      if (this.cameraState == CameraState.STOPPING)
        break;
      throw new IllegalStateException("Only stopping camera can go to idle state.");
    }
    synchronized (this.cameraStateLock)
    {
      this.cameraState = paramCameraState;
      this.cameraStateLock.notifyAll();
      return;
    }
  }

  // ERROR //
  private void startCaptureOnCameraThread(int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 217	org/webrtc/Camera2Capturer:checkIsOnCameraThread	()V
    //   4: aload_0
    //   5: iconst_0
    //   6: putfield 483	org/webrtc/Camera2Capturer:firstFrameReported	Z
    //   9: aload_0
    //   10: iconst_0
    //   11: putfield 233	org/webrtc/Camera2Capturer:consecutiveCameraOpenFailures	I
    //   14: aload_0
    //   15: getfield 99	org/webrtc/Camera2Capturer:cameraStateLock	Ljava/lang/Object;
    //   18: astore 4
    //   20: aload 4
    //   22: monitorenter
    //   23: aload_0
    //   24: iload_1
    //   25: putfield 485	org/webrtc/Camera2Capturer:requestedWidth	I
    //   28: aload_0
    //   29: iload_2
    //   30: putfield 487	org/webrtc/Camera2Capturer:requestedHeight	I
    //   33: aload_0
    //   34: iload_3
    //   35: putfield 489	org/webrtc/Camera2Capturer:requestedFramerate	I
    //   38: aload 4
    //   40: monitorexit
    //   41: aload_0
    //   42: getfield 140	org/webrtc/Camera2Capturer:cameraManager	Landroid/hardware/camera2/CameraManager;
    //   45: aload_0
    //   46: getfield 360	org/webrtc/Camera2Capturer:cameraName	Ljava/lang/String;
    //   49: invokevirtual 443	android/hardware/camera2/CameraManager:getCameraCharacteristics	(Ljava/lang/String;)Landroid/hardware/camera2/CameraCharacteristics;
    //   52: astore 4
    //   54: aload 4
    //   56: getstatic 492	android/hardware/camera2/CameraCharacteristics:CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES	Landroid/hardware/camera2/CameraCharacteristics$Key;
    //   59: invokevirtual 458	android/hardware/camera2/CameraCharacteristics:get	(Landroid/hardware/camera2/CameraCharacteristics$Key;)Ljava/lang/Object;
    //   62: checkcast 494	[Landroid/util/Range;
    //   65: astore 5
    //   67: aload_0
    //   68: aload 5
    //   70: invokestatic 500	org/webrtc/Camera2Enumerator:getFpsUnitFactor	([Landroid/util/Range;)I
    //   73: putfield 182	org/webrtc/Camera2Capturer:fpsUnitFactor	I
    //   76: aload 5
    //   78: aload_0
    //   79: getfield 182	org/webrtc/Camera2Capturer:fpsUnitFactor	I
    //   82: invokestatic 504	org/webrtc/Camera2Enumerator:convertFramerates	([Landroid/util/Range;I)Ljava/util/List;
    //   85: astore 5
    //   87: aload 4
    //   89: invokestatic 508	org/webrtc/Camera2Enumerator:getSupportedSizes	(Landroid/hardware/camera2/CameraCharacteristics;)Ljava/util/List;
    //   92: astore 4
    //   94: aload 5
    //   96: invokeinterface 509 1 0
    //   101: ifne +13 -> 114
    //   104: aload 4
    //   106: invokeinterface 509 1 0
    //   111: ifeq +10 -> 121
    //   114: aload_0
    //   115: ldc_w 511
    //   118: invokespecial 228	org/webrtc/Camera2Capturer:reportError	(Ljava/lang/String;)V
    //   121: aload 5
    //   123: iload_3
    //   124: invokestatic 517	org/webrtc/CameraEnumerationAndroid:getClosestSupportedFramerateRange	(Ljava/util/List;I)Lorg/webrtc/CameraEnumerationAndroid$CaptureFormat$FramerateRange;
    //   127: astore 5
    //   129: aload 4
    //   131: iload_1
    //   132: iload_2
    //   133: invokestatic 521	org/webrtc/CameraEnumerationAndroid:getClosestSupportedSize	(Ljava/util/List;II)Lorg/webrtc/Size;
    //   136: astore 4
    //   138: aload_0
    //   139: new 523	org/webrtc/CameraEnumerationAndroid$CaptureFormat
    //   142: dup
    //   143: aload 4
    //   145: getfield 528	org/webrtc/Size:width	I
    //   148: aload 4
    //   150: getfield 531	org/webrtc/Size:height	I
    //   153: aload 5
    //   155: invokespecial 534	org/webrtc/CameraEnumerationAndroid$CaptureFormat:<init>	(IILorg/webrtc/CameraEnumerationAndroid$CaptureFormat$FramerateRange;)V
    //   158: putfield 162	org/webrtc/Camera2Capturer:captureFormat	Lorg/webrtc/CameraEnumerationAndroid$CaptureFormat;
    //   161: ldc 49
    //   163: new 111	java/lang/StringBuilder
    //   166: dup
    //   167: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   170: ldc_w 536
    //   173: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   176: aload_0
    //   177: getfield 162	org/webrtc/Camera2Capturer:captureFormat	Lorg/webrtc/CameraEnumerationAndroid$CaptureFormat;
    //   180: invokevirtual 358	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   183: invokevirtual 122	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   186: invokestatic 128	org/webrtc/Logging:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   189: ldc 49
    //   191: new 111	java/lang/StringBuilder
    //   194: dup
    //   195: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   198: ldc_w 538
    //   201: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   204: aload_0
    //   205: getfield 360	org/webrtc/Camera2Capturer:cameraName	Ljava/lang/String;
    //   208: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   211: invokevirtual 122	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   214: invokestatic 128	org/webrtc/Logging:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   217: aload_0
    //   218: getfield 142	org/webrtc/Camera2Capturer:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   221: ifnull +25 -> 246
    //   224: iconst_m1
    //   225: istore_1
    //   226: aload_0
    //   227: getfield 360	org/webrtc/Camera2Capturer:cameraName	Ljava/lang/String;
    //   230: invokestatic 542	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   233: istore_2
    //   234: iload_2
    //   235: istore_1
    //   236: aload_0
    //   237: getfield 142	org/webrtc/Camera2Capturer:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   240: iload_1
    //   241: invokeinterface 546 2 0
    //   246: aload_0
    //   247: invokespecial 238	org/webrtc/Camera2Capturer:openCamera	()V
    //   250: return
    //   251: astore 5
    //   253: aload 4
    //   255: monitorexit
    //   256: aload 5
    //   258: athrow
    //   259: astore 4
    //   261: aload_0
    //   262: new 111	java/lang/StringBuilder
    //   265: dup
    //   266: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   269: ldc_w 548
    //   272: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   275: aload 4
    //   277: invokevirtual 551	android/hardware/camera2/CameraAccessException:getMessage	()Ljava/lang/String;
    //   280: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   283: invokevirtual 122	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   286: invokespecial 228	org/webrtc/Camera2Capturer:reportError	(Ljava/lang/String;)V
    //   289: return
    //   290: astore 4
    //   292: ldc 49
    //   294: new 111	java/lang/StringBuilder
    //   297: dup
    //   298: invokespecial 112	java/lang/StringBuilder:<init>	()V
    //   301: ldc_w 553
    //   304: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   307: aload_0
    //   308: getfield 360	org/webrtc/Camera2Capturer:cameraName	Ljava/lang/String;
    //   311: invokevirtual 118	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   314: invokevirtual 122	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   317: invokestatic 128	org/webrtc/Logging:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   320: goto -84 -> 236
    //
    // Exception table:
    //   from	to	target	type
    //   23	41	251	finally
    //   253	256	251	finally
    //   41	54	259	android/hardware/camera2/CameraAccessException
    //   226	234	290	java/lang/NumberFormatException
  }

  private void waitForCameraToExitTransitionalState(CameraState paramCameraState, long paramLong)
  {
    checkNotOnCameraThread();
    synchronized (this.cameraStateLock)
    {
      long l1 = SystemClock.uptimeMillis();
      while (true)
      {
        long l2;
        if (this.cameraState == paramCameraState)
        {
          Logging.d("Camera2Capturer", "waitForCameraToExitTransitionalState waiting: " + this.cameraState);
          l2 = l1 + paramLong - SystemClock.uptimeMillis();
          if (l2 <= 0L)
            Logging.e("Camera2Capturer", "Camera failed to exit transitional state " + paramCameraState + " within the time limit.");
        }
        else
        {
          return;
        }
        try
        {
          this.cameraStateLock.wait(l2);
        }
        catch (InterruptedException localInterruptedException)
        {
          Logging.w("Camera2Capturer", "Trying to interrupt while waiting to exit transitional state " + paramCameraState + ", ignoring: " + localInterruptedException);
        }
      }
    }
  }

  private void waitForCameraToStartIfStarting()
  {
    waitForCameraToExitTransitionalState(CameraState.STARTING, 10000L);
  }

  private void waitForCameraToStopIfStopping()
  {
    waitForCameraToExitTransitionalState(CameraState.STOPPING, 10000L);
  }

  public void changeCaptureFormat(int paramInt1, int paramInt2, int paramInt3)
  {
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStartIfStarting();
      if (this.cameraState != CameraState.RUNNING)
      {
        Logging.e("Camera2Capturer", "Calling changeCaptureFormat() on stopped camera.");
        return;
      }
      this.requestedWidth = paramInt1;
      this.requestedHeight = paramInt2;
      this.requestedFramerate = paramInt3;
      stopCapture();
      startCapture(paramInt1, paramInt2, paramInt3);
      return;
    }
  }

  public void dispose()
  {
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStopIfStopping();
      if (this.cameraState != CameraState.IDLE)
        throw new IllegalStateException("Unexpected camera state for dispose: " + this.cameraState);
    }
    monitorexit;
  }

  public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats()
  {
    synchronized (this.cameraState)
    {
      List localList = Camera2Enumerator.getSupportedFormats(this.cameraManager, this.cameraName);
      return localList;
    }
  }

  public void initialize(SurfaceTextureHelper paramSurfaceTextureHelper, Context paramContext, VideoCapturer.CapturerObserver paramCapturerObserver)
  {
    Logging.d("Camera2Capturer", "initialize");
    if (paramContext == null)
      throw new IllegalArgumentException("applicationContext not set.");
    if (paramCapturerObserver == null)
      throw new IllegalArgumentException("capturerObserver not set.");
    if (isInitialized())
      throw new IllegalStateException("Already initialized");
    this.applicationContext = paramContext;
    this.capturerObserver = paramCapturerObserver;
    this.surfaceTextureHelper = paramSurfaceTextureHelper;
    if (paramSurfaceTextureHelper == null);
    for (paramSurfaceTextureHelper = null; ; paramSurfaceTextureHelper = paramSurfaceTextureHelper.getHandler())
    {
      this.cameraThreadHandler = paramSurfaceTextureHelper;
      return;
    }
  }

  public void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3)
  {
    postOnCameraThread(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        Logging.d("Camera2Capturer", "onOutputFormatRequestOnCameraThread: " + this.val$width + "x" + this.val$height + "@" + this.val$framerate);
        Camera2Capturer.this.capturerObserver.onOutputFormatRequest(this.val$width, this.val$height, this.val$framerate);
      }
    });
  }

  public void onTextureFrameAvailable(int paramInt, float[] paramArrayOfFloat, long paramLong)
  {
    checkIsOnCameraThread();
    if (this.cameraState != CameraState.RUNNING)
    {
      Logging.d("Camera2Capturer", "Texture frame received while camera was not running.");
      return;
    }
    if ((this.eventsHandler != null) && (!this.firstFrameReported))
    {
      this.eventsHandler.onFirstFrameAvailable();
      this.firstFrameReported = true;
    }
    int i;
    if (this.isFrontCamera)
    {
      i = this.cameraOrientation + getDeviceOrientation();
      paramArrayOfFloat = RendererCommon.multiplyMatrices(paramArrayOfFloat, RendererCommon.horizontalFlipMatrix());
    }
    while (true)
    {
      paramArrayOfFloat = RendererCommon.rotateTextureMatrix(paramArrayOfFloat, -this.cameraOrientation);
      this.cameraStatistics.addFrame();
      this.capturerObserver.onTextureFrameCaptured(this.captureFormat.width, this.captureFormat.height, paramInt, paramArrayOfFloat, (i % 360 + 360) % 360, paramLong);
      return;
      i = this.cameraOrientation - getDeviceOrientation();
    }
  }

  public void startCapture(int paramInt1, int paramInt2, int paramInt3)
  {
    Logging.d("Camera2Capturer", "startCapture requested: " + paramInt1 + "x" + paramInt2 + "@" + paramInt3);
    if (!isInitialized())
      throw new IllegalStateException("startCapture called in uninitialized state");
    if (this.surfaceTextureHelper == null)
    {
      this.capturerObserver.onCapturerStarted(false);
      if (this.eventsHandler != null)
        this.eventsHandler.onCameraError("No SurfaceTexture created.");
      return;
    }
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStopIfStopping();
      if (this.cameraState != CameraState.IDLE)
      {
        Logging.e("Camera2Capturer", "Unexpected camera state for startCapture: " + this.cameraState);
        return;
      }
    }
    setCameraState(CameraState.STARTING);
    monitorexit;
    postOnCameraThread(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        Camera2Capturer.this.startCaptureOnCameraThread(this.val$requestedWidth, this.val$requestedHeight, this.val$requestedFramerate);
      }
    });
  }

  public void stopCapture()
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    Logging.d("Camera2Capturer", "stopCapture");
    checkNotOnCameraThread();
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStartIfStarting();
      if (this.cameraState != CameraState.RUNNING)
      {
        Logging.w("Camera2Capturer", "stopCapture called for already stopped camera.");
        return;
      }
      postOnCameraThread(new Runnable(localCountDownLatch)
      {
        public void run()
        {
          Logging.d("Camera2Capturer", "stopCaptureOnCameraThread");
          Camera2Capturer.this.closeAndRelease();
          this.val$cameraStoppingLatch.countDown();
        }
      });
      ThreadUtils.awaitUninterruptibly(localCountDownLatch);
      Logging.d("Camera2Capturer", "stopCapture done");
      return;
    }
  }

  public void switchCamera(CameraVideoCapturer.CameraSwitchHandler paramCameraSwitchHandler)
  {
    while (true)
    {
      try
      {
        localObject2 = this.cameraManager.getCameraIdList();
        if (localObject2.length < 2)
        {
          if (paramCameraSwitchHandler == null)
            continue;
          paramCameraSwitchHandler.onCameraSwitchError("No camera to switch to.");
          return;
        }
      }
      catch (CameraAccessException localCameraAccessException)
      {
        if (paramCameraSwitchHandler == null)
          continue;
        paramCameraSwitchHandler.onCameraSwitchError("Could not get camera names: " + localCameraAccessException);
        return;
      }
      if (!this.isPendingCameraSwitch.getAndSet(true))
        break;
      Logging.w("Camera2Capturer", "Ignoring camera switch request.");
      if (paramCameraSwitchHandler == null)
        continue;
      paramCameraSwitchHandler.onCameraSwitchError("Pending camera switch already in progress.");
      return;
    }
    synchronized (this.cameraStateLock)
    {
      waitForCameraToStartIfStarting();
      if (this.cameraState != CameraState.RUNNING)
      {
        Logging.e("Camera2Capturer", "Calling swithCamera() on stopped camera.");
        if (paramCameraSwitchHandler != null)
          paramCameraSwitchHandler.onCameraSwitchError("Camera is stopped.");
        this.isPendingCameraSwitch.set(false);
        return;
      }
    }
    int i = Arrays.asList(localObject2).indexOf(this.cameraName);
    if (i == -1)
      Logging.e("Camera2Capturer", "Couldn't find current camera id " + this.cameraName + " in list of camera ids: " + Arrays.toString(localObject2));
    Object localObject2 = localObject2[((i + 1) % localObject2.length)];
    i = this.requestedWidth;
    int j = this.requestedHeight;
    int k = this.requestedFramerate;
    this.switchEventsHandler = paramCameraSwitchHandler;
    monitorexit;
    stopCapture();
    setCameraName((String)localObject2);
    startCapture(i, j, k);
  }

  final class CameraCaptureCallback extends CameraCaptureSession.CaptureCallback
  {
    static final int MAX_CONSECUTIVE_CAMERA_CAPTURE_FAILURES = 10;
    int consecutiveCameraCaptureFailures;

    CameraCaptureCallback()
    {
    }

    public void onCaptureCompleted(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, TotalCaptureResult paramTotalCaptureResult)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      this.consecutiveCameraCaptureFailures = 0;
    }

    public void onCaptureFailed(CameraCaptureSession paramCameraCaptureSession, CaptureRequest paramCaptureRequest, CaptureFailure paramCaptureFailure)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      this.consecutiveCameraCaptureFailures += 1;
      if (this.consecutiveCameraCaptureFailures > 10)
        Camera2Capturer.this.reportError("Capture failed " + this.consecutiveCameraCaptureFailures + " consecutive times.");
    }
  }

  private static enum CameraState
  {
    static
    {
      RUNNING = new CameraState("RUNNING", 2);
      STOPPING = new CameraState("STOPPING", 3);
      $VALUES = new CameraState[] { IDLE, STARTING, RUNNING, STOPPING };
    }
  }

  final class CameraStateCallback extends CameraDevice.StateCallback
  {
    CameraStateCallback()
    {
    }

    private String getErrorDescription(int paramInt)
    {
      switch (paramInt)
      {
      default:
        return "Unknown camera error: " + paramInt;
      case 4:
        return "Camera device has encountered a fatal error.";
      case 3:
        return "Camera device could not be opened due to a device policy.";
      case 1:
        return "Camera device is in use already.";
      case 5:
        return "Camera service has encountered a fatal error.";
      case 2:
      }
      return "Camera device could not be opened because there are too many other open camera devices.";
    }

    public void onClosed(CameraDevice paramCameraDevice)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Logging.d("Camera2Capturer", "Camera device closed.");
      if (Camera2Capturer.this.cameraState != Camera2Capturer.CameraState.STOPPING)
        Logging.e("Camera2Capturer", "Camera state was not STOPPING in onClosed. Most likely camera didn't stop within timelimit and this method was invoked twice.");
      do
      {
        return;
        Camera2Capturer.this.cameraThreadHandler.removeCallbacksAndMessages(Camera2Capturer.STOP_TIMEOUT_RUNNABLE_TOKEN);
        Camera2Capturer.this.setCameraState(Camera2Capturer.CameraState.IDLE);
      }
      while (Camera2Capturer.this.eventsHandler == null);
      Camera2Capturer.this.eventsHandler.onCameraClosed();
    }

    public void onDisconnected(CameraDevice paramCameraDevice)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Camera2Capturer.access$402(Camera2Capturer.this, paramCameraDevice);
      Camera2Capturer.this.reportError("Camera disconnected.");
    }

    public void onError(CameraDevice paramCameraDevice, int paramInt)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Camera2Capturer.access$402(Camera2Capturer.this, paramCameraDevice);
      if ((Camera2Capturer.this.cameraState == Camera2Capturer.CameraState.STARTING) && ((paramInt == 1) || (paramInt == 2)))
      {
        Camera2Capturer.access$708(Camera2Capturer.this);
        if (Camera2Capturer.this.consecutiveCameraOpenFailures < 3)
        {
          Logging.w("Camera2Capturer", "Opening camera failed, trying again: " + getErrorDescription(paramInt));
          Camera2Capturer.this.postDelayedOnCameraThread(500, new Runnable()
          {
            public void run()
            {
              Camera2Capturer.this.openCamera();
            }
          });
          return;
        }
        Logging.e("Camera2Capturer", "Opening camera failed too many times. Passing the error.");
      }
      Camera2Capturer.this.reportError(getErrorDescription(paramInt));
    }

    public void onOpened(CameraDevice paramCameraDevice)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Logging.d("Camera2Capturer", "Camera opened.");
      if (Camera2Capturer.this.cameraState != Camera2Capturer.CameraState.STARTING)
        throw new IllegalStateException("Unexpected state when camera opened: " + Camera2Capturer.this.cameraState);
      Camera2Capturer.access$402(Camera2Capturer.this, paramCameraDevice);
      SurfaceTexture localSurfaceTexture = Camera2Capturer.this.surfaceTextureHelper.getSurfaceTexture();
      localSurfaceTexture.setDefaultBufferSize(Camera2Capturer.this.captureFormat.width, Camera2Capturer.this.captureFormat.height);
      Camera2Capturer.access$1202(Camera2Capturer.this, new Surface(localSurfaceTexture));
      try
      {
        paramCameraDevice.createCaptureSession(Arrays.asList(new Surface[] { Camera2Capturer.access$1200(Camera2Capturer.this) }), new Camera2Capturer.CaptureSessionCallback(Camera2Capturer.this), Camera2Capturer.this.cameraThreadHandler);
        return;
      }
      catch (CameraAccessException paramCameraDevice)
      {
        Camera2Capturer.this.reportError("Failed to create capture session. " + paramCameraDevice);
      }
    }
  }

  final class CaptureSessionCallback extends CameraCaptureSession.StateCallback
  {
    CaptureSessionCallback()
    {
    }

    public void onConfigureFailed(CameraCaptureSession paramCameraCaptureSession)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Camera2Capturer.access$1502(Camera2Capturer.this, paramCameraCaptureSession);
      Camera2Capturer.this.reportError("Failed to configure capture session.");
    }

    public void onConfigured(CameraCaptureSession paramCameraCaptureSession)
    {
      Camera2Capturer.this.checkIsOnCameraThread();
      Logging.d("Camera2Capturer", "Camera capture session configured.");
      Camera2Capturer.access$1502(Camera2Capturer.this, paramCameraCaptureSession);
      try
      {
        CaptureRequest.Builder localBuilder = Camera2Capturer.this.cameraDevice.createCaptureRequest(3);
        localBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range(Integer.valueOf(Camera2Capturer.this.captureFormat.framerate.min / Camera2Capturer.this.fpsUnitFactor), Integer.valueOf(Camera2Capturer.this.captureFormat.framerate.max / Camera2Capturer.this.fpsUnitFactor)));
        localBuilder.set(CaptureRequest.CONTROL_AE_MODE, Integer.valueOf(1));
        localBuilder.set(CaptureRequest.CONTROL_AE_LOCK, Boolean.valueOf(false));
        localBuilder.addTarget(Camera2Capturer.this.surface);
        paramCameraCaptureSession.setRepeatingRequest(localBuilder.build(), new Camera2Capturer.CameraCaptureCallback(Camera2Capturer.this), Camera2Capturer.this.cameraThreadHandler);
        Logging.d("Camera2Capturer", "Camera device successfully started.");
        Camera2Capturer.this.surfaceTextureHelper.startListening(Camera2Capturer.this);
        Camera2Capturer.this.capturerObserver.onCapturerStarted(true);
        Camera2Capturer.access$1802(Camera2Capturer.this, new CameraVideoCapturer.CameraStatistics(Camera2Capturer.this.surfaceTextureHelper, Camera2Capturer.this.eventsHandler));
        Camera2Capturer.this.setCameraState(Camera2Capturer.CameraState.RUNNING);
        if (Camera2Capturer.this.switchEventsHandler != null)
        {
          Camera2Capturer.this.switchEventsHandler.onCameraSwitchDone(Camera2Capturer.this.isFrontCamera);
          Camera2Capturer.access$1902(Camera2Capturer.this, null);
        }
        Camera2Capturer.this.isPendingCameraSwitch.set(false);
        return;
      }
      catch (CameraAccessException paramCameraCaptureSession)
      {
        Camera2Capturer.this.reportError("Failed to start capture request. " + paramCameraCaptureSession);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Camera2Capturer
 * JD-Core Version:    0.6.0
 */