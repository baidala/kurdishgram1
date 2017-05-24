package org.webrtc;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.Display;
import android.view.WindowManager;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class VideoCapturerAndroid
  implements Camera.PreviewCallback, CameraVideoCapturer, SurfaceTextureHelper.OnTextureFrameAvailableListener
{
  private static final int CAMERA_STOP_TIMEOUT_MS = 7000;
  private static final int MAX_OPEN_CAMERA_ATTEMPTS = 3;
  private static final int NUMBER_OF_CAPTURE_BUFFERS = 3;
  private static final int OPEN_CAMERA_DELAY_MS = 500;
  private static final String TAG = "VideoCapturerAndroid";
  private Context applicationContext;
  private Camera camera;
  private final Camera.ErrorCallback cameraErrorCallback = new Camera.ErrorCallback()
  {
    public void onError(int paramInt, Camera paramCamera)
    {
      if (paramInt == 100);
      for (paramCamera = "Camera server died!"; ; paramCamera = "Camera error: " + paramInt)
      {
        Logging.e("VideoCapturerAndroid", paramCamera);
        if (VideoCapturerAndroid.this.eventsHandler != null)
          VideoCapturerAndroid.this.eventsHandler.onCameraError(paramCamera);
        return;
      }
    }
  };
  private final Object cameraIdLock = new Object();
  private CameraVideoCapturer.CameraStatistics cameraStatistics;
  private volatile Handler cameraThreadHandler;
  private CameraEnumerationAndroid.CaptureFormat captureFormat;
  private final CameraVideoCapturer.CameraEventsHandler eventsHandler;
  private boolean firstFrameReported;
  private VideoCapturer.CapturerObserver frameObserver = null;
  private int id;
  private Camera.CameraInfo info;
  private final AtomicBoolean isCameraRunning = new AtomicBoolean();
  private final boolean isCapturingToTexture;
  private int openCameraAttempts;
  private volatile boolean pendingCameraSwitch;
  private final Object pendingCameraSwitchLock = new Object();
  private final Set<byte[]> queuedBuffers = new HashSet();
  private int requestedFramerate;
  private int requestedHeight;
  private int requestedWidth;
  private SurfaceTextureHelper surfaceHelper;

  public VideoCapturerAndroid(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler, boolean paramBoolean)
  {
    if (Camera.getNumberOfCameras() == 0)
      throw new RuntimeException("No cameras available");
    if ((paramString == null) || (paramString.equals("")));
    for (this.id = 0; ; this.id = Camera1Enumerator.getCameraIndex(paramString))
    {
      this.eventsHandler = paramCameraEventsHandler;
      this.isCapturingToTexture = paramBoolean;
      Logging.d("VideoCapturerAndroid", "VideoCapturerAndroid isCapturingToTexture : " + this.isCapturingToTexture);
      return;
    }
  }

  private void checkIsOnCameraThread()
  {
    if (this.cameraThreadHandler == null)
      Logging.e("VideoCapturerAndroid", "Camera is not initialized - can't check thread.");
    do
      return;
    while (Thread.currentThread() == this.cameraThreadHandler.getLooper().getThread());
    throw new IllegalStateException("Wrong thread");
  }

  public static VideoCapturerAndroid create(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler)
  {
    return create(paramString, paramCameraEventsHandler, false);
  }

  @Deprecated
  public static VideoCapturerAndroid create(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler, boolean paramBoolean)
  {
    try
    {
      paramString = new VideoCapturerAndroid(paramString, paramCameraEventsHandler, paramBoolean);
      return paramString;
    }
    catch (RuntimeException paramString)
    {
      Logging.e("VideoCapturerAndroid", "Couldn't create camera.", paramString);
    }
    return null;
  }

  private int getCurrentCameraId()
  {
    synchronized (this.cameraIdLock)
    {
      int i = this.id;
      return i;
    }
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

  private int getFrameOrientation()
  {
    int j = getDeviceOrientation();
    int i = j;
    if (this.info.facing == 0)
      i = 360 - j;
    return (i + this.info.orientation) % 360;
  }

  private boolean isInitialized()
  {
    return (this.applicationContext != null) && (this.frameObserver != null);
  }

  private boolean maybePostDelayedOnCameraThread(int paramInt, Runnable paramRunnable)
  {
    return (this.cameraThreadHandler != null) && (this.isCameraRunning.get()) && (this.cameraThreadHandler.postAtTime(paramRunnable, this, SystemClock.uptimeMillis() + paramInt));
  }

  private boolean maybePostOnCameraThread(Runnable paramRunnable)
  {
    return maybePostDelayedOnCameraThread(0, paramRunnable);
  }

  private void onOutputFormatRequestOnCameraThread(int paramInt1, int paramInt2, int paramInt3)
  {
    checkIsOnCameraThread();
    Logging.d("VideoCapturerAndroid", "onOutputFormatRequestOnCameraThread: " + paramInt1 + "x" + paramInt2 + "@" + paramInt3);
    this.frameObserver.onOutputFormatRequest(paramInt1, paramInt2, paramInt3);
  }

  // ERROR //
  private void startCaptureOnCameraThread(int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 296	org/webrtc/VideoCapturerAndroid:checkIsOnCameraThread	()V
    //   4: aload_0
    //   5: getfield 83	org/webrtc/VideoCapturerAndroid:isCameraRunning	Ljava/util/concurrent/atomic/AtomicBoolean;
    //   8: invokevirtual 280	java/util/concurrent/atomic/AtomicBoolean:get	()Z
    //   11: ifne +12 -> 23
    //   14: ldc 36
    //   16: ldc_w 314
    //   19: invokestatic 203	org/webrtc/Logging:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   22: return
    //   23: aload_0
    //   24: getfield 316	org/webrtc/VideoCapturerAndroid:camera	Landroid/hardware/Camera;
    //   27: ifnull +12 -> 39
    //   30: ldc 36
    //   32: ldc_w 318
    //   35: invokestatic 203	org/webrtc/Logging:e	(Ljava/lang/String;Ljava/lang/String;)V
    //   38: return
    //   39: aload_0
    //   40: iconst_0
    //   41: putfield 320	org/webrtc/VideoCapturerAndroid:firstFrameReported	Z
    //   44: aload_0
    //   45: getfield 85	org/webrtc/VideoCapturerAndroid:cameraIdLock	Ljava/lang/Object;
    //   48: astore 4
    //   50: aload 4
    //   52: monitorenter
    //   53: ldc 36
    //   55: new 128	java/lang/StringBuilder
    //   58: dup
    //   59: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   62: ldc_w 322
    //   65: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   68: aload_0
    //   69: getfield 122	org/webrtc/VideoCapturerAndroid:id	I
    //   72: invokevirtual 301	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   75: invokevirtual 142	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   78: invokestatic 148	org/webrtc/Logging:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   81: aload_0
    //   82: getfield 124	org/webrtc/VideoCapturerAndroid:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   85: ifnull +16 -> 101
    //   88: aload_0
    //   89: getfield 124	org/webrtc/VideoCapturerAndroid:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   92: aload_0
    //   93: getfield 122	org/webrtc/VideoCapturerAndroid:id	I
    //   96: invokeinterface 328 2 0
    //   101: aload_0
    //   102: aload_0
    //   103: getfield 122	org/webrtc/VideoCapturerAndroid:id	I
    //   106: invokestatic 332	android/hardware/Camera:open	(I)Landroid/hardware/Camera;
    //   109: putfield 316	org/webrtc/VideoCapturerAndroid:camera	Landroid/hardware/Camera;
    //   112: aload_0
    //   113: new 267	android/hardware/Camera$CameraInfo
    //   116: dup
    //   117: invokespecial 333	android/hardware/Camera$CameraInfo:<init>	()V
    //   120: putfield 171	org/webrtc/VideoCapturerAndroid:info	Landroid/hardware/Camera$CameraInfo;
    //   123: aload_0
    //   124: getfield 122	org/webrtc/VideoCapturerAndroid:id	I
    //   127: aload_0
    //   128: getfield 171	org/webrtc/VideoCapturerAndroid:info	Landroid/hardware/Camera$CameraInfo;
    //   131: invokestatic 337	android/hardware/Camera:getCameraInfo	(ILandroid/hardware/Camera$CameraInfo;)V
    //   134: aload 4
    //   136: monitorexit
    //   137: aload_0
    //   138: getfield 316	org/webrtc/VideoCapturerAndroid:camera	Landroid/hardware/Camera;
    //   141: aload_0
    //   142: getfield 339	org/webrtc/VideoCapturerAndroid:surfaceHelper	Lorg/webrtc/SurfaceTextureHelper;
    //   145: invokevirtual 345	org/webrtc/SurfaceTextureHelper:getSurfaceTexture	()Landroid/graphics/SurfaceTexture;
    //   148: invokevirtual 349	android/hardware/Camera:setPreviewTexture	(Landroid/graphics/SurfaceTexture;)V
    //   151: ldc 36
    //   153: new 128	java/lang/StringBuilder
    //   156: dup
    //   157: invokespecial 129	java/lang/StringBuilder:<init>	()V
    //   160: ldc_w 351
    //   163: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   166: aload_0
    //   167: getfield 171	org/webrtc/VideoCapturerAndroid:info	Landroid/hardware/Camera$CameraInfo;
    //   170: getfield 273	android/hardware/Camera$CameraInfo:orientation	I
    //   173: invokevirtual 301	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   176: ldc_w 353
    //   179: invokevirtual 135	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   182: aload_0
    //   183: invokespecial 265	org/webrtc/VideoCapturerAndroid:getDeviceOrientation	()I
    //   186: invokevirtual 301	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   189: invokevirtual 142	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   192: invokestatic 148	org/webrtc/Logging:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   195: aload_0
    //   196: getfield 316	org/webrtc/VideoCapturerAndroid:camera	Landroid/hardware/Camera;
    //   199: aload_0
    //   200: getfield 99	org/webrtc/VideoCapturerAndroid:cameraErrorCallback	Landroid/hardware/Camera$ErrorCallback;
    //   203: invokevirtual 357	android/hardware/Camera:setErrorCallback	(Landroid/hardware/Camera$ErrorCallback;)V
    //   206: aload_0
    //   207: iload_1
    //   208: iload_2
    //   209: iload_3
    //   210: invokespecial 181	org/webrtc/VideoCapturerAndroid:startPreviewOnCameraThread	(III)V
    //   213: aload_0
    //   214: getfield 89	org/webrtc/VideoCapturerAndroid:frameObserver	Lorg/webrtc/VideoCapturer$CapturerObserver;
    //   217: iconst_1
    //   218: invokeinterface 360 2 0
    //   223: aload_0
    //   224: getfield 126	org/webrtc/VideoCapturerAndroid:isCapturingToTexture	Z
    //   227: ifeq +11 -> 238
    //   230: aload_0
    //   231: getfield 339	org/webrtc/VideoCapturerAndroid:surfaceHelper	Lorg/webrtc/SurfaceTextureHelper;
    //   234: aload_0
    //   235: invokevirtual 364	org/webrtc/SurfaceTextureHelper:startListening	(Lorg/webrtc/SurfaceTextureHelper$OnTextureFrameAvailableListener;)V
    //   238: aload_0
    //   239: new 366	org/webrtc/CameraVideoCapturer$CameraStatistics
    //   242: dup
    //   243: aload_0
    //   244: getfield 339	org/webrtc/VideoCapturerAndroid:surfaceHelper	Lorg/webrtc/SurfaceTextureHelper;
    //   247: aload_0
    //   248: getfield 124	org/webrtc/VideoCapturerAndroid:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   251: invokespecial 369	org/webrtc/CameraVideoCapturer$CameraStatistics:<init>	(Lorg/webrtc/SurfaceTextureHelper;Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;)V
    //   254: putfield 371	org/webrtc/VideoCapturerAndroid:cameraStatistics	Lorg/webrtc/CameraVideoCapturer$CameraStatistics;
    //   257: return
    //   258: astore 4
    //   260: ldc 36
    //   262: ldc_w 373
    //   265: aload 4
    //   267: invokestatic 238	org/webrtc/Logging:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   270: aload_0
    //   271: iconst_1
    //   272: invokespecial 195	org/webrtc/VideoCapturerAndroid:stopCaptureOnCameraThread	(Z)V
    //   275: aload_0
    //   276: getfield 89	org/webrtc/VideoCapturerAndroid:frameObserver	Lorg/webrtc/VideoCapturer$CapturerObserver;
    //   279: iconst_0
    //   280: invokeinterface 360 2 0
    //   285: aload_0
    //   286: getfield 124	org/webrtc/VideoCapturerAndroid:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   289: ifnull -267 -> 22
    //   292: aload_0
    //   293: getfield 124	org/webrtc/VideoCapturerAndroid:eventsHandler	Lorg/webrtc/CameraVideoCapturer$CameraEventsHandler;
    //   296: ldc_w 375
    //   299: invokeinterface 378 2 0
    //   304: return
    //   305: astore 5
    //   307: aload 4
    //   309: monitorexit
    //   310: aload 5
    //   312: athrow
    //   313: astore 4
    //   315: aload_0
    //   316: aload_0
    //   317: getfield 185	org/webrtc/VideoCapturerAndroid:openCameraAttempts	I
    //   320: iconst_1
    //   321: iadd
    //   322: putfield 185	org/webrtc/VideoCapturerAndroid:openCameraAttempts	I
    //   325: aload_0
    //   326: getfield 185	org/webrtc/VideoCapturerAndroid:openCameraAttempts	I
    //   329: iconst_3
    //   330: if_icmpge +33 -> 363
    //   333: ldc 36
    //   335: ldc_w 380
    //   338: aload 4
    //   340: invokestatic 238	org/webrtc/Logging:e	(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)V
    //   343: aload_0
    //   344: sipush 500
    //   347: new 22	org/webrtc/VideoCapturerAndroid$6
    //   350: dup
    //   351: aload_0
    //   352: iload_1
    //   353: iload_2
    //   354: iload_3
    //   355: invokespecial 382	org/webrtc/VideoCapturerAndroid$6:<init>	(Lorg/webrtc/VideoCapturerAndroid;III)V
    //   358: invokespecial 294	org/webrtc/VideoCapturerAndroid:maybePostDelayedOnCameraThread	(ILjava/lang/Runnable;)Z
    //   361: pop
    //   362: return
    //   363: aload 4
    //   365: athrow
    //   366: astore 4
    //   368: goto -108 -> 260
    //
    // Exception table:
    //   from	to	target	type
    //   44	53	258	java/io/IOException
    //   137	238	258	java/io/IOException
    //   238	257	258	java/io/IOException
    //   310	313	258	java/io/IOException
    //   315	362	258	java/io/IOException
    //   363	366	258	java/io/IOException
    //   53	101	305	finally
    //   101	137	305	finally
    //   307	310	305	finally
    //   44	53	313	java/lang/RuntimeException
    //   310	313	313	java/lang/RuntimeException
    //   137	238	366	java/lang/RuntimeException
    //   238	257	366	java/lang/RuntimeException
    //   315	362	366	java/lang/RuntimeException
    //   363	366	366	java/lang/RuntimeException
  }

  private void startPreviewOnCameraThread(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    checkIsOnCameraThread();
    if ((!this.isCameraRunning.get()) || (this.camera == null))
      Logging.e("VideoCapturerAndroid", "startPreviewOnCameraThread: Camera is stopped");
    Object localObject1;
    Object localObject3;
    do
    {
      return;
      Logging.d("VideoCapturerAndroid", "startPreviewOnCameraThread requested: " + paramInt1 + "x" + paramInt2 + "@" + paramInt3);
      this.requestedWidth = paramInt1;
      this.requestedHeight = paramInt2;
      this.requestedFramerate = paramInt3;
      localObject1 = this.camera.getParameters();
      localObject2 = Camera1Enumerator.convertFramerates(((Camera.Parameters)localObject1).getSupportedPreviewFpsRange());
      Logging.d("VideoCapturerAndroid", "Available fps ranges: " + localObject2);
      localObject3 = CameraEnumerationAndroid.getClosestSupportedFramerateRange((List)localObject2, paramInt3);
      localObject2 = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(((Camera.Parameters)localObject1).getSupportedPreviewSizes()), paramInt1, paramInt2);
      localObject3 = new CameraEnumerationAndroid.CaptureFormat(((Size)localObject2).width, ((Size)localObject2).height, (CameraEnumerationAndroid.CaptureFormat.FramerateRange)localObject3);
    }
    while (((CameraEnumerationAndroid.CaptureFormat)localObject3).equals(this.captureFormat));
    Logging.d("VideoCapturerAndroid", "isVideoStabilizationSupported: " + ((Camera.Parameters)localObject1).isVideoStabilizationSupported());
    if (((Camera.Parameters)localObject1).isVideoStabilizationSupported())
      ((Camera.Parameters)localObject1).setVideoStabilization(true);
    if (((CameraEnumerationAndroid.CaptureFormat)localObject3).framerate.max > 0)
      ((Camera.Parameters)localObject1).setPreviewFpsRange(((CameraEnumerationAndroid.CaptureFormat)localObject3).framerate.min, ((CameraEnumerationAndroid.CaptureFormat)localObject3).framerate.max);
    ((Camera.Parameters)localObject1).setPreviewSize(((Size)localObject2).width, ((Size)localObject2).height);
    if (!this.isCapturingToTexture)
    {
      localObject3.getClass();
      ((Camera.Parameters)localObject1).setPreviewFormat(17);
    }
    Object localObject2 = CameraEnumerationAndroid.getClosestSupportedSize(Camera1Enumerator.convertSizes(((Camera.Parameters)localObject1).getSupportedPictureSizes()), paramInt1, paramInt2);
    ((Camera.Parameters)localObject1).setPictureSize(((Size)localObject2).width, ((Size)localObject2).height);
    if (this.captureFormat != null)
    {
      this.camera.stopPreview();
      this.camera.setPreviewCallbackWithBuffer(null);
    }
    Logging.d("VideoCapturerAndroid", "Start capturing: " + localObject3);
    this.captureFormat = ((CameraEnumerationAndroid.CaptureFormat)localObject3);
    if (((Camera.Parameters)localObject1).getSupportedFocusModes().contains("continuous-video"))
      ((Camera.Parameters)localObject1).setFocusMode("continuous-video");
    this.camera.setParameters((Camera.Parameters)localObject1);
    this.camera.setDisplayOrientation(0);
    if (!this.isCapturingToTexture)
    {
      this.queuedBuffers.clear();
      paramInt2 = ((CameraEnumerationAndroid.CaptureFormat)localObject3).frameSize();
      paramInt1 = i;
      while (paramInt1 < 3)
      {
        localObject1 = ByteBuffer.allocateDirect(paramInt2);
        this.queuedBuffers.add(((ByteBuffer)localObject1).array());
        this.camera.addCallbackBuffer(((ByteBuffer)localObject1).array());
        paramInt1 += 1;
      }
      this.camera.setPreviewCallbackWithBuffer(this);
    }
    this.camera.startPreview();
  }

  private void stopCaptureOnCameraThread(boolean paramBoolean)
  {
    checkIsOnCameraThread();
    Logging.d("VideoCapturerAndroid", "stopCaptureOnCameraThread");
    if (this.surfaceHelper != null)
      this.surfaceHelper.stopListening();
    if (paramBoolean)
    {
      this.isCameraRunning.set(false);
      this.cameraThreadHandler.removeCallbacksAndMessages(this);
    }
    if (this.cameraStatistics != null)
    {
      this.cameraStatistics.release();
      this.cameraStatistics = null;
    }
    Logging.d("VideoCapturerAndroid", "Stop preview.");
    if (this.camera != null)
    {
      this.camera.stopPreview();
      this.camera.setPreviewCallbackWithBuffer(null);
    }
    this.queuedBuffers.clear();
    this.captureFormat = null;
    Logging.d("VideoCapturerAndroid", "Release camera.");
    if (this.camera != null)
    {
      this.camera.release();
      this.camera = null;
    }
    if (this.eventsHandler != null)
      this.eventsHandler.onCameraClosed();
    Logging.d("VideoCapturerAndroid", "stopCaptureOnCameraThread done");
  }

  private void switchCameraOnCameraThread()
  {
    checkIsOnCameraThread();
    if (!this.isCameraRunning.get())
    {
      Logging.e("VideoCapturerAndroid", "switchCameraOnCameraThread: Camera is stopped");
      return;
    }
    Logging.d("VideoCapturerAndroid", "switchCameraOnCameraThread");
    stopCaptureOnCameraThread(false);
    synchronized (this.cameraIdLock)
    {
      this.id = ((this.id + 1) % Camera.getNumberOfCameras());
      startCaptureOnCameraThread(this.requestedWidth, this.requestedHeight, this.requestedFramerate);
      Logging.d("VideoCapturerAndroid", "switchCameraOnCameraThread done");
      return;
    }
  }

  public void changeCaptureFormat(int paramInt1, int paramInt2, int paramInt3)
  {
    maybePostOnCameraThread(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        VideoCapturerAndroid.this.startPreviewOnCameraThread(this.val$width, this.val$height, this.val$framerate);
      }
    });
  }

  public void dispose()
  {
    Logging.d("VideoCapturerAndroid", "dispose");
  }

  public List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats()
  {
    return Camera1Enumerator.getSupportedFormats(getCurrentCameraId());
  }

  public void initialize(SurfaceTextureHelper paramSurfaceTextureHelper, Context paramContext, VideoCapturer.CapturerObserver paramCapturerObserver)
  {
    Logging.d("VideoCapturerAndroid", "initialize");
    if (paramContext == null)
      throw new IllegalArgumentException("applicationContext not set.");
    if (paramCapturerObserver == null)
      throw new IllegalArgumentException("frameObserver not set.");
    if (isInitialized())
      throw new IllegalStateException("Already initialized");
    this.applicationContext = paramContext;
    this.frameObserver = paramCapturerObserver;
    this.surfaceHelper = paramSurfaceTextureHelper;
    if (paramSurfaceTextureHelper == null);
    for (paramSurfaceTextureHelper = null; ; paramSurfaceTextureHelper = paramSurfaceTextureHelper.getHandler())
    {
      this.cameraThreadHandler = paramSurfaceTextureHelper;
      return;
    }
  }

  public boolean isCapturingToTexture()
  {
    return this.isCapturingToTexture;
  }

  public void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3)
  {
    maybePostOnCameraThread(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        VideoCapturerAndroid.this.onOutputFormatRequestOnCameraThread(this.val$width, this.val$height, this.val$framerate);
      }
    });
  }

  public void onPreviewFrame(byte[] paramArrayOfByte, Camera paramCamera)
  {
    checkIsOnCameraThread();
    if (!this.isCameraRunning.get())
      Logging.e("VideoCapturerAndroid", "onPreviewFrame: Camera is stopped");
    do
      return;
    while (!this.queuedBuffers.contains(paramArrayOfByte));
    if (this.camera != paramCamera)
      throw new RuntimeException("Unexpected camera in callback!");
    long l = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
    if ((this.eventsHandler != null) && (!this.firstFrameReported))
    {
      this.eventsHandler.onFirstFrameAvailable();
      this.firstFrameReported = true;
    }
    this.cameraStatistics.addFrame();
    this.frameObserver.onByteBufferFrameCaptured(paramArrayOfByte, this.captureFormat.width, this.captureFormat.height, getFrameOrientation(), l);
    this.camera.addCallbackBuffer(paramArrayOfByte);
  }

  public void onTextureFrameAvailable(int paramInt, float[] paramArrayOfFloat, long paramLong)
  {
    checkIsOnCameraThread();
    if (!this.isCameraRunning.get())
    {
      Logging.e("VideoCapturerAndroid", "onTextureFrameAvailable: Camera is stopped");
      this.surfaceHelper.returnTextureFrame();
      return;
    }
    if ((this.eventsHandler != null) && (!this.firstFrameReported))
    {
      this.eventsHandler.onFirstFrameAvailable();
      this.firstFrameReported = true;
    }
    int i = getFrameOrientation();
    if (this.info.facing == 1)
      paramArrayOfFloat = RendererCommon.multiplyMatrices(paramArrayOfFloat, RendererCommon.horizontalFlipMatrix());
    while (true)
    {
      this.cameraStatistics.addFrame();
      this.frameObserver.onTextureFrameCaptured(this.captureFormat.width, this.captureFormat.height, paramInt, paramArrayOfFloat, i, paramLong);
      return;
    }
  }

  public void printStackTrace()
  {
    Object localObject = null;
    if (this.cameraThreadHandler != null)
      localObject = this.cameraThreadHandler.getLooper().getThread();
    if (localObject != null)
    {
      localObject = ((Thread)localObject).getStackTrace();
      if (localObject.length > 0)
      {
        Logging.d("VideoCapturerAndroid", "VideoCapturerAndroid stacks trace:");
        int j = localObject.length;
        int i = 0;
        while (i < j)
        {
          Logging.d("VideoCapturerAndroid", localObject[i].toString());
          i += 1;
        }
      }
    }
  }

  public void startCapture(int paramInt1, int paramInt2, int paramInt3)
  {
    Logging.d("VideoCapturerAndroid", "startCapture requested: " + paramInt1 + "x" + paramInt2 + "@" + paramInt3);
    if (!isInitialized())
      throw new IllegalStateException("startCapture called in uninitialized state");
    if (this.surfaceHelper == null)
    {
      this.frameObserver.onCapturerStarted(false);
      if (this.eventsHandler != null)
        this.eventsHandler.onCameraError("No SurfaceTexture created.");
    }
    do
    {
      return;
      if (!this.isCameraRunning.getAndSet(true))
        continue;
      Logging.e("VideoCapturerAndroid", "Camera has already been started.");
      return;
    }
    while (maybePostOnCameraThread(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        VideoCapturerAndroid.access$702(VideoCapturerAndroid.this, 0);
        VideoCapturerAndroid.this.startCaptureOnCameraThread(this.val$width, this.val$height, this.val$framerate);
      }
    }));
    this.frameObserver.onCapturerStarted(false);
    if (this.eventsHandler != null)
      this.eventsHandler.onCameraError("Could not post task to camera thread.");
    this.isCameraRunning.set(false);
  }

  public void stopCapture()
  {
    Logging.d("VideoCapturerAndroid", "stopCapture");
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    if (!maybePostOnCameraThread(new Runnable(localCountDownLatch)
    {
      public void run()
      {
        VideoCapturerAndroid.this.stopCaptureOnCameraThread(true);
        this.val$barrier.countDown();
      }
    }))
    {
      Logging.e("VideoCapturerAndroid", "Calling stopCapture() for already stopped camera.");
      return;
    }
    if (!localCountDownLatch.await(7000L, TimeUnit.MILLISECONDS))
    {
      Logging.e("VideoCapturerAndroid", "Camera stop timeout");
      printStackTrace();
      if (this.eventsHandler != null)
        this.eventsHandler.onCameraError("Camera stop timeout");
    }
    this.frameObserver.onCapturerStopped();
    Logging.d("VideoCapturerAndroid", "stopCapture done");
  }

  public void switchCamera(CameraVideoCapturer.CameraSwitchHandler paramCameraSwitchHandler)
  {
    if (Camera.getNumberOfCameras() < 2)
      if (paramCameraSwitchHandler != null)
        paramCameraSwitchHandler.onCameraSwitchError("No camera to switch to.");
    do
    {
      return;
      synchronized (this.pendingCameraSwitchLock)
      {
        if (this.pendingCameraSwitch)
        {
          Logging.w("VideoCapturerAndroid", "Ignoring camera switch request.");
          if (paramCameraSwitchHandler != null)
            paramCameraSwitchHandler.onCameraSwitchError("Pending camera switch already in progress.");
          return;
        }
      }
      this.pendingCameraSwitch = true;
      monitorexit;
    }
    while ((maybePostOnCameraThread(new Runnable(paramCameraSwitchHandler)
    {
      public void run()
      {
        boolean bool = true;
        VideoCapturerAndroid.this.switchCameraOnCameraThread();
        while (true)
        {
          synchronized (VideoCapturerAndroid.this.pendingCameraSwitchLock)
          {
            VideoCapturerAndroid.access$302(VideoCapturerAndroid.this, false);
            if (this.val$switchEventsHandler == null)
              continue;
            ??? = this.val$switchEventsHandler;
            if (VideoCapturerAndroid.this.info.facing == 1)
            {
              ((CameraVideoCapturer.CameraSwitchHandler)???).onCameraSwitchDone(bool);
              return;
            }
          }
          bool = false;
        }
      }
    })) || (paramCameraSwitchHandler == null));
    paramCameraSwitchHandler.onCameraSwitchError("Camera is stopped.");
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoCapturerAndroid
 * JD-Core Version:    0.6.0
 */