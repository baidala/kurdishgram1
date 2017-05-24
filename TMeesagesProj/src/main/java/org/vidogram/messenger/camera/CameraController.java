package org.vidogram.messenger.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.media.ThumbnailUtils;
import android.os.Build;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.NotificationCenter;

public class CameraController
  implements MediaRecorder.OnInfoListener
{
  private static final int CORE_POOL_SIZE = 1;
  private static volatile CameraController Instance = null;
  private static final int KEEP_ALIVE_SECONDS = 60;
  private static final int MAX_POOL_SIZE = 1;
  protected ArrayList<String> availableFlashModes = new ArrayList();
  protected ArrayList<CameraInfo> cameraInfos = null;
  private boolean cameraInitied;
  private VideoTakeCallback onVideoTakeCallback;
  private String recordedFile;
  private MediaRecorder recorder;
  private boolean recordingSmallVideo;
  private ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 1, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue());

  public static Size chooseOptimalSize(List<Size> paramList, int paramInt1, int paramInt2, Size paramSize)
  {
    ArrayList localArrayList = new ArrayList();
    int j = paramSize.getWidth();
    int k = paramSize.getHeight();
    int i = 0;
    while (i < paramList.size())
    {
      paramSize = (Size)paramList.get(i);
      if ((paramSize.getHeight() == paramSize.getWidth() * k / j) && (paramSize.getWidth() >= paramInt1) && (paramSize.getHeight() >= paramInt2))
        localArrayList.add(paramSize);
      i += 1;
    }
    if (localArrayList.size() > 0)
      return (Size)Collections.min(localArrayList, new CompareSizesByArea());
    return (Size)Collections.max(paramList, new CompareSizesByArea());
  }

  public static CameraController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        CameraController localCameraController = Instance;
        localObject1 = localCameraController;
        if (localCameraController == null)
        {
          localObject1 = new CameraController();
          Instance = (CameraController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (CameraController)localObject2;
  }

  private static int getOrientation(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null);
    label8: label393: label396: 
    while (true)
    {
      return 0;
      int i = 0;
      int k;
      int j;
      int m;
      while (true)
      {
        k = i;
        if (i + 3 < paramArrayOfByte.length)
        {
          j = i + 1;
          if ((paramArrayOfByte[i] & 0xFF) != 255)
            break label393;
          m = paramArrayOfByte[j] & 0xFF;
          if (m == 255)
          {
            i = j;
            continue;
          }
          j += 1;
          i = j;
          if (m == 216)
            continue;
          i = j;
          if (m == 1)
            continue;
          k = j;
          if (m != 217)
          {
            if (m != 218)
              break;
            i = 0;
          }
        }
      }
      while (true)
      {
        label99: if (i <= 8)
          break label396;
        k = pack(paramArrayOfByte, j, 4, false);
        if ((k != 1229531648) && (k != 1296891946))
          break;
        boolean bool;
        if (k == 1229531648)
        {
          bool = true;
          k = pack(paramArrayOfByte, j + 4, 4, bool) + 2;
          if ((k < 10) || (k > i))
            break;
          j += k;
          m = pack(paramArrayOfByte, j - 2, 2, bool);
          i -= k;
          k = j;
          j = i;
          i = m;
        }
        while (true)
        {
          if ((i <= 0) || (j < 12))
            break label384;
          if (pack(paramArrayOfByte, k, 2, bool) == 274)
            switch (pack(paramArrayOfByte, k + 8, 2, bool))
            {
            case 1:
            case 2:
            case 4:
            case 5:
            case 7:
            default:
              return 0;
            case 3:
              return 180;
              i = pack(paramArrayOfByte, j, 2, false);
              if ((i < 2) || (j + i > paramArrayOfByte.length))
                break;
              if ((m == 225) && (i >= 8) && (pack(paramArrayOfByte, j + 2, 4, false) == 1165519206) && (pack(paramArrayOfByte, j + 6, 2, false) == 0))
              {
                j += 8;
                i -= 8;
                break label99;
              }
              i = j + i;
              break label8;
              bool = false;
              break;
            case 6:
              return 90;
            case 8:
              return 270;
            }
          k += 12;
          j -= 12;
          i -= 1;
        }
        break;
        i = 0;
        j = k;
        continue;
        i = 0;
      }
    }
  }

  private static int pack(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 1;
    int i = paramInt1;
    if (paramBoolean)
    {
      i = paramInt1 + (paramInt2 - 1);
      j = -1;
    }
    paramInt1 = 0;
    while (paramInt2 > 0)
    {
      paramInt1 = paramInt1 << 8 | paramArrayOfByte[i] & 0xFF;
      i += j;
      paramInt2 -= 1;
    }
    return paramInt1;
  }

  public void cleanup()
  {
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        if ((CameraController.this.cameraInfos == null) || (CameraController.this.cameraInfos.isEmpty()))
          return;
        int i = 0;
        while (i < CameraController.this.cameraInfos.size())
        {
          CameraInfo localCameraInfo = (CameraInfo)CameraController.this.cameraInfos.get(i);
          if (localCameraInfo.camera != null)
          {
            localCameraInfo.camera.stopPreview();
            localCameraInfo.camera.setPreviewCallbackWithBuffer(null);
            localCameraInfo.camera.release();
            localCameraInfo.camera = null;
          }
          i += 1;
        }
        CameraController.this.cameraInfos = null;
      }
    });
  }

  public void close(CameraSession paramCameraSession, Semaphore paramSemaphore, Runnable paramRunnable)
  {
    paramCameraSession.destroy();
    this.threadPool.execute(new Runnable(paramRunnable, paramCameraSession, paramSemaphore)
    {
      public void run()
      {
        if (this.val$beforeDestroyRunnable != null)
          this.val$beforeDestroyRunnable.run();
        if (this.val$session.cameraInfo.camera == null);
        while (true)
        {
          return;
          try
          {
            this.val$session.cameraInfo.camera.stopPreview();
            this.val$session.cameraInfo.camera.setPreviewCallbackWithBuffer(null);
          }
          catch (Exception localException2)
          {
            try
            {
              while (true)
              {
                this.val$session.cameraInfo.camera.release();
                this.val$session.cameraInfo.camera = null;
                if (this.val$semaphore == null)
                  break;
                this.val$semaphore.release();
                return;
                localException1 = localException1;
                FileLog.e(localException1);
              }
            }
            catch (Exception localException2)
            {
              while (true)
                FileLog.e(localException2);
            }
          }
        }
      }
    });
    if (paramSemaphore != null);
    try
    {
      paramSemaphore.acquire();
      return;
    }
    catch (Exception paramCameraSession)
    {
      FileLog.e(paramCameraSession);
    }
  }

  public ArrayList<CameraInfo> getCameras()
  {
    return this.cameraInfos;
  }

  public void initCamera()
  {
    if (this.cameraInitied)
      return;
    this.threadPool.execute(new Runnable()
    {
      public void run()
      {
        while (true)
        {
          int j;
          try
          {
            if (CameraController.this.cameraInfos != null)
              continue;
            int k = Camera.getNumberOfCameras();
            ArrayList localArrayList = new ArrayList();
            Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
            int i = 0;
            if (i >= k)
              continue;
            Camera.getCameraInfo(i, localCameraInfo);
            CameraInfo localCameraInfo1 = new CameraInfo(i, localCameraInfo);
            Camera localCamera = Camera.open(localCameraInfo1.getCameraId());
            Object localObject1 = localCamera.getParameters();
            Object localObject2 = ((Camera.Parameters)localObject1).getSupportedPreviewSizes();
            j = 0;
            if (j >= ((List)localObject2).size())
              continue;
            Camera.Size localSize = (Camera.Size)((List)localObject2).get(j);
            if ((localSize.height < 2160) && (localSize.width < 2160))
            {
              localCameraInfo1.previewSizes.add(new Size(localSize.width, localSize.height));
              break label302;
              localObject1 = ((Camera.Parameters)localObject1).getSupportedPictureSizes();
              j = 0;
              if (j >= ((List)localObject1).size())
                continue;
              localObject2 = (Camera.Size)((List)localObject1).get(j);
              if (("samsung".equals(Build.MANUFACTURER)) && ("jflteuc".equals(Build.PRODUCT)) && (((Camera.Size)localObject2).width >= 2048))
                break label309;
              localCameraInfo1.pictureSizes.add(new Size(((Camera.Size)localObject2).width, ((Camera.Size)localObject2).height));
              break label309;
              localCamera.release();
              localArrayList.add(localCameraInfo1);
              i += 1;
              continue;
              CameraController.this.cameraInfos = localArrayList;
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  CameraController.access$002(CameraController.this, true);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.cameraInitied, new Object[0]);
                }
              });
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          label302: j += 1;
          continue;
          label309: j += 1;
        }
      }
    });
  }

  public boolean isCameraInitied()
  {
    return (this.cameraInitied) && (this.cameraInfos != null) && (!this.cameraInfos.isEmpty());
  }

  public void onInfo(MediaRecorder paramMediaRecorder, int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 800) || (paramInt1 == 801) || (paramInt1 == 1))
    {
      paramMediaRecorder = this.recorder;
      this.recorder = null;
      if (paramMediaRecorder != null)
      {
        paramMediaRecorder.stop();
        paramMediaRecorder.release();
      }
      if (this.onVideoTakeCallback != null)
        AndroidUtilities.runOnUIThread(new Runnable(ThumbnailUtils.createVideoThumbnail(this.recordedFile, 1))
        {
          public void run()
          {
            if (CameraController.this.onVideoTakeCallback != null)
            {
              CameraController.this.onVideoTakeCallback.onFinishVideoRecording(this.val$bitmap);
              CameraController.access$402(CameraController.this, null);
            }
          }
        });
    }
  }

  public void open(CameraSession paramCameraSession, SurfaceTexture paramSurfaceTexture, Runnable paramRunnable1, Runnable paramRunnable2)
  {
    if ((paramCameraSession == null) || (paramSurfaceTexture == null))
      return;
    this.threadPool.execute(new Runnable(paramCameraSession, paramRunnable2, paramSurfaceTexture, paramRunnable1)
    {
      @SuppressLint({"NewApi"})
      public void run()
      {
        Object localObject3 = this.val$session.cameraInfo.camera;
        Object localObject1 = localObject3;
        Object localObject2;
        if (localObject3 == null)
          localObject2 = localObject3;
        while (true)
        {
          int i;
          try
          {
            Object localObject4 = this.val$session.cameraInfo;
            localObject2 = localObject3;
            localObject1 = Camera.open(this.val$session.cameraInfo.cameraId);
            localObject2 = localObject3;
            ((CameraInfo)localObject4).camera = ((Camera)localObject1);
            localObject2 = localObject1;
            localObject3 = ((Camera)localObject1).getParameters().getSupportedFlashModes();
            localObject2 = localObject1;
            CameraController.this.availableFlashModes.clear();
            if (localObject3 == null)
              continue;
            i = 0;
            localObject2 = localObject1;
            if (i >= ((List)localObject3).size())
              continue;
            localObject2 = localObject1;
            localObject4 = (String)((List)localObject3).get(i);
            localObject2 = localObject1;
            if (((String)localObject4).equals("off"))
              continue;
            localObject2 = localObject1;
            if (((String)localObject4).equals("on"))
              continue;
            localObject2 = localObject1;
            if (((String)localObject4).equals("auto"))
            {
              localObject2 = localObject1;
              CameraController.this.availableFlashModes.add(localObject4);
              break label282;
              localObject2 = localObject1;
              this.val$session.checkFlashMode((String)CameraController.this.availableFlashModes.get(0));
              localObject2 = localObject1;
              if (this.val$prestartCallback == null)
                continue;
              localObject2 = localObject1;
              this.val$prestartCallback.run();
              localObject2 = localObject1;
              this.val$session.configurePhotoCamera();
              localObject2 = localObject1;
              ((Camera)localObject1).setPreviewTexture(this.val$texture);
              localObject2 = localObject1;
              ((Camera)localObject1).startPreview();
              localObject2 = localObject1;
              if (this.val$callback == null)
                continue;
              localObject2 = localObject1;
              AndroidUtilities.runOnUIThread(this.val$callback);
              return;
            }
          }
          catch (Exception localException)
          {
            this.val$session.cameraInfo.camera = null;
            if (localObject2 == null)
              continue;
            localObject2.release();
            FileLog.e(localException);
            return;
          }
          label282: i += 1;
        }
      }
    });
  }

  public void recordVideo(CameraSession paramCameraSession, File paramFile, VideoTakeCallback paramVideoTakeCallback, Runnable paramRunnable, boolean paramBoolean)
  {
    if (paramCameraSession == null)
      return;
    CameraInfo localCameraInfo = paramCameraSession.cameraInfo;
    Camera localCamera = localCameraInfo.camera;
    this.threadPool.execute(new Runnable(localCamera, paramCameraSession, paramBoolean, paramFile, localCameraInfo, paramVideoTakeCallback, paramRunnable)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 33	org/vidogram/messenger/camera/CameraController$7:val$camera	Landroid/hardware/Camera;
        //   4: astore_1
        //   5: aload_1
        //   6: ifnull +323 -> 329
        //   9: aload_0
        //   10: getfield 33	org/vidogram/messenger/camera/CameraController$7:val$camera	Landroid/hardware/Camera;
        //   13: invokevirtual 58	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
        //   16: astore_2
        //   17: aload_0
        //   18: getfield 35	org/vidogram/messenger/camera/CameraController$7:val$session	Lorg/vidogram/messenger/camera/CameraSession;
        //   21: invokevirtual 64	org/vidogram/messenger/camera/CameraSession:getCurrentFlashMode	()Ljava/lang/String;
        //   24: ldc 66
        //   26: invokevirtual 72	java/lang/String:equals	(Ljava/lang/Object;)Z
        //   29: ifeq +301 -> 330
        //   32: ldc 74
        //   34: astore_1
        //   35: aload_2
        //   36: aload_1
        //   37: invokevirtual 80	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
        //   40: aload_0
        //   41: getfield 33	org/vidogram/messenger/camera/CameraController$7:val$camera	Landroid/hardware/Camera;
        //   44: aload_2
        //   45: invokevirtual 84	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
        //   48: aload_0
        //   49: getfield 33	org/vidogram/messenger/camera/CameraController$7:val$camera	Landroid/hardware/Camera;
        //   52: invokevirtual 87	android/hardware/Camera:unlock	()V
        //   55: aload_0
        //   56: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   59: aload_0
        //   60: getfield 37	org/vidogram/messenger/camera/CameraController$7:val$smallVideo	Z
        //   63: invokestatic 91	org/vidogram/messenger/camera/CameraController:access$202	(Lorg/vidogram/messenger/camera/CameraController;Z)Z
        //   66: pop
        //   67: aload_0
        //   68: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   71: new 93	android/media/MediaRecorder
        //   74: dup
        //   75: invokespecial 94	android/media/MediaRecorder:<init>	()V
        //   78: invokestatic 98	org/vidogram/messenger/camera/CameraController:access$302	(Lorg/vidogram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
        //   81: pop
        //   82: aload_0
        //   83: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   86: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   89: aload_0
        //   90: getfield 33	org/vidogram/messenger/camera/CameraController$7:val$camera	Landroid/hardware/Camera;
        //   93: invokevirtual 106	android/media/MediaRecorder:setCamera	(Landroid/hardware/Camera;)V
        //   96: aload_0
        //   97: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   100: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   103: iconst_1
        //   104: invokevirtual 110	android/media/MediaRecorder:setVideoSource	(I)V
        //   107: aload_0
        //   108: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   111: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   114: iconst_5
        //   115: invokevirtual 113	android/media/MediaRecorder:setAudioSource	(I)V
        //   118: aload_0
        //   119: getfield 35	org/vidogram/messenger/camera/CameraController$7:val$session	Lorg/vidogram/messenger/camera/CameraSession;
        //   122: iconst_1
        //   123: aload_0
        //   124: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   127: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   130: invokevirtual 117	org/vidogram/messenger/camera/CameraSession:configureRecorder	(ILandroid/media/MediaRecorder;)V
        //   133: aload_0
        //   134: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   137: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   140: aload_0
        //   141: getfield 39	org/vidogram/messenger/camera/CameraController$7:val$path	Ljava/io/File;
        //   144: invokevirtual 122	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   147: invokevirtual 125	android/media/MediaRecorder:setOutputFile	(Ljava/lang/String;)V
        //   150: aload_0
        //   151: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   154: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   157: ldc2_w 126
        //   160: invokevirtual 131	android/media/MediaRecorder:setMaxFileSize	(J)V
        //   163: aload_0
        //   164: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   167: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   170: bipush 30
        //   172: invokevirtual 134	android/media/MediaRecorder:setVideoFrameRate	(I)V
        //   175: aload_0
        //   176: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   179: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   182: iconst_0
        //   183: invokevirtual 137	android/media/MediaRecorder:setMaxDuration	(I)V
        //   186: aload_0
        //   187: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   190: invokestatic 141	org/vidogram/messenger/camera/CameraController:access$200	(Lorg/vidogram/messenger/camera/CameraController;)Z
        //   193: ifeq +157 -> 350
        //   196: new 143	org/vidogram/messenger/camera/Size
        //   199: dup
        //   200: iconst_4
        //   201: iconst_3
        //   202: invokespecial 146	org/vidogram/messenger/camera/Size:<init>	(II)V
        //   205: astore_1
        //   206: aload_0
        //   207: getfield 41	org/vidogram/messenger/camera/CameraController$7:val$info	Lorg/vidogram/messenger/camera/CameraInfo;
        //   210: invokevirtual 152	org/vidogram/messenger/camera/CameraInfo:getPictureSizes	()Ljava/util/ArrayList;
        //   213: sipush 640
        //   216: sipush 480
        //   219: aload_1
        //   220: invokestatic 156	org/vidogram/messenger/camera/CameraController:chooseOptimalSize	(Ljava/util/List;IILorg/vidogram/messenger/camera/Size;)Lorg/vidogram/messenger/camera/Size;
        //   223: astore_1
        //   224: aload_0
        //   225: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   228: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   231: ldc 157
        //   233: invokevirtual 160	android/media/MediaRecorder:setVideoEncodingBitRate	(I)V
        //   236: aload_0
        //   237: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   240: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   243: aload_1
        //   244: invokevirtual 164	org/vidogram/messenger/camera/Size:getWidth	()I
        //   247: aload_1
        //   248: invokevirtual 167	org/vidogram/messenger/camera/Size:getHeight	()I
        //   251: invokevirtual 170	android/media/MediaRecorder:setVideoSize	(II)V
        //   254: aload_0
        //   255: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   258: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   261: aload_0
        //   262: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   265: invokevirtual 174	android/media/MediaRecorder:setOnInfoListener	(Landroid/media/MediaRecorder$OnInfoListener;)V
        //   268: aload_0
        //   269: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   272: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   275: invokevirtual 177	android/media/MediaRecorder:prepare	()V
        //   278: aload_0
        //   279: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   282: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   285: invokevirtual 180	android/media/MediaRecorder:start	()V
        //   288: aload_0
        //   289: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   292: aload_0
        //   293: getfield 43	org/vidogram/messenger/camera/CameraController$7:val$callback	Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;
        //   296: invokestatic 184	org/vidogram/messenger/camera/CameraController:access$402	(Lorg/vidogram/messenger/camera/CameraController;Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;)Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;
        //   299: pop
        //   300: aload_0
        //   301: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   304: aload_0
        //   305: getfield 39	org/vidogram/messenger/camera/CameraController$7:val$path	Ljava/io/File;
        //   308: invokevirtual 122	java/io/File:getAbsolutePath	()Ljava/lang/String;
        //   311: invokestatic 188	org/vidogram/messenger/camera/CameraController:access$502	(Lorg/vidogram/messenger/camera/CameraController;Ljava/lang/String;)Ljava/lang/String;
        //   314: pop
        //   315: aload_0
        //   316: getfield 45	org/vidogram/messenger/camera/CameraController$7:val$onVideoStartRecord	Ljava/lang/Runnable;
        //   319: ifnull +10 -> 329
        //   322: aload_0
        //   323: getfield 45	org/vidogram/messenger/camera/CameraController$7:val$onVideoStartRecord	Ljava/lang/Runnable;
        //   326: invokestatic 194	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   329: return
        //   330: ldc 196
        //   332: astore_1
        //   333: goto -298 -> 35
        //   336: astore_1
        //   337: aload_1
        //   338: invokestatic 202	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   341: goto -293 -> 48
        //   344: astore_1
        //   345: aload_1
        //   346: invokestatic 202	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   349: return
        //   350: new 143	org/vidogram/messenger/camera/Size
        //   353: dup
        //   354: bipush 16
        //   356: bipush 9
        //   358: invokespecial 146	org/vidogram/messenger/camera/Size:<init>	(II)V
        //   361: astore_1
        //   362: aload_0
        //   363: getfield 41	org/vidogram/messenger/camera/CameraController$7:val$info	Lorg/vidogram/messenger/camera/CameraInfo;
        //   366: invokevirtual 152	org/vidogram/messenger/camera/CameraInfo:getPictureSizes	()Ljava/util/ArrayList;
        //   369: sipush 720
        //   372: sipush 480
        //   375: aload_1
        //   376: invokestatic 156	org/vidogram/messenger/camera/CameraController:chooseOptimalSize	(Ljava/util/List;IILorg/vidogram/messenger/camera/Size;)Lorg/vidogram/messenger/camera/Size;
        //   379: astore_1
        //   380: aload_0
        //   381: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   384: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   387: ldc 203
        //   389: invokevirtual 160	android/media/MediaRecorder:setVideoEncodingBitRate	(I)V
        //   392: goto -156 -> 236
        //   395: astore_1
        //   396: aload_0
        //   397: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   400: invokestatic 102	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   403: invokevirtual 206	android/media/MediaRecorder:release	()V
        //   406: aload_0
        //   407: getfield 31	org/vidogram/messenger/camera/CameraController$7:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   410: aconst_null
        //   411: invokestatic 98	org/vidogram/messenger/camera/CameraController:access$302	(Lorg/vidogram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
        //   414: pop
        //   415: aload_1
        //   416: invokestatic 202	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   419: return
        //
        // Exception table:
        //   from	to	target	type
        //   9	32	336	java/lang/Exception
        //   35	48	336	java/lang/Exception
        //   0	5	344	java/lang/Exception
        //   48	55	344	java/lang/Exception
        //   337	341	344	java/lang/Exception
        //   396	419	344	java/lang/Exception
        //   55	236	395	java/lang/Exception
        //   236	329	395	java/lang/Exception
        //   350	392	395	java/lang/Exception
      }
    });
  }

  public void startPreview(CameraSession paramCameraSession)
  {
    if (paramCameraSession == null)
      return;
    this.threadPool.execute(new Runnable(paramCameraSession)
    {
      @SuppressLint({"NewApi"})
      public void run()
      {
        Camera localCamera3 = this.val$session.cameraInfo.camera;
        Camera localCamera1 = localCamera3;
        Camera localCamera2;
        if (localCamera3 == null)
          localCamera2 = localCamera3;
        try
        {
          CameraInfo localCameraInfo = this.val$session.cameraInfo;
          localCamera2 = localCamera3;
          localCamera1 = Camera.open(this.val$session.cameraInfo.cameraId);
          localCamera2 = localCamera3;
          localCameraInfo.camera = localCamera1;
          localCamera2 = localCamera1;
          localCamera1.startPreview();
          return;
        }
        catch (Exception localException)
        {
          this.val$session.cameraInfo.camera = null;
          if (localCamera2 != null)
            localCamera2.release();
          FileLog.e(localException);
        }
      }
    });
  }

  public void stopVideoRecording(CameraSession paramCameraSession, boolean paramBoolean)
  {
    this.threadPool.execute(new Runnable(paramCameraSession, paramBoolean)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 27	org/vidogram/messenger/camera/CameraController$9:val$session	Lorg/vidogram/messenger/camera/CameraSession;
        //   4: getfield 42	org/vidogram/messenger/camera/CameraSession:cameraInfo	Lorg/vidogram/messenger/camera/CameraInfo;
        //   7: getfield 48	org/vidogram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
        //   10: astore_1
        //   11: aload_1
        //   12: ifnull +53 -> 65
        //   15: aload_0
        //   16: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   19: invokestatic 52	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   22: ifnull +43 -> 65
        //   25: aload_0
        //   26: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   29: invokestatic 52	org/vidogram/messenger/camera/CameraController:access$300	(Lorg/vidogram/messenger/camera/CameraController;)Landroid/media/MediaRecorder;
        //   32: astore_2
        //   33: aload_0
        //   34: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   37: aconst_null
        //   38: invokestatic 56	org/vidogram/messenger/camera/CameraController:access$302	(Lorg/vidogram/messenger/camera/CameraController;Landroid/media/MediaRecorder;)Landroid/media/MediaRecorder;
        //   41: pop
        //   42: aload_2
        //   43: invokevirtual 61	android/media/MediaRecorder:stop	()V
        //   46: aload_2
        //   47: invokevirtual 64	android/media/MediaRecorder:release	()V
        //   50: aload_1
        //   51: invokevirtual 69	android/hardware/Camera:reconnect	()V
        //   54: aload_1
        //   55: invokevirtual 72	android/hardware/Camera:startPreview	()V
        //   58: aload_0
        //   59: getfield 27	org/vidogram/messenger/camera/CameraController$9:val$session	Lorg/vidogram/messenger/camera/CameraSession;
        //   62: invokevirtual 74	org/vidogram/messenger/camera/CameraSession:stopVideoRecording	()V
        //   65: aload_1
        //   66: invokevirtual 78	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
        //   69: astore_2
        //   70: aload_2
        //   71: ldc 80
        //   73: invokevirtual 86	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
        //   76: aload_1
        //   77: aload_2
        //   78: invokevirtual 90	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
        //   81: aload_0
        //   82: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   85: invokestatic 94	org/vidogram/messenger/camera/CameraController:access$600	(Lorg/vidogram/messenger/camera/CameraController;)Ljava/util/concurrent/ThreadPoolExecutor;
        //   88: new 13	org/vidogram/messenger/camera/CameraController$9$1
        //   91: dup
        //   92: aload_0
        //   93: aload_1
        //   94: invokespecial 97	org/vidogram/messenger/camera/CameraController$9$1:<init>	(Lorg/vidogram/messenger/camera/CameraController$9;Landroid/hardware/Camera;)V
        //   97: invokevirtual 103	java/util/concurrent/ThreadPoolExecutor:execute	(Ljava/lang/Runnable;)V
        //   100: aload_0
        //   101: getfield 29	org/vidogram/messenger/camera/CameraController$9:val$abandon	Z
        //   104: ifne +94 -> 198
        //   107: aload_0
        //   108: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   111: invokestatic 107	org/vidogram/messenger/camera/CameraController:access$400	(Lorg/vidogram/messenger/camera/CameraController;)Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;
        //   114: ifnull +84 -> 198
        //   117: aload_0
        //   118: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   121: invokestatic 111	org/vidogram/messenger/camera/CameraController:access$200	(Lorg/vidogram/messenger/camera/CameraController;)Z
        //   124: ifne +84 -> 208
        //   127: aload_0
        //   128: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   131: invokestatic 115	org/vidogram/messenger/camera/CameraController:access$500	(Lorg/vidogram/messenger/camera/CameraController;)Ljava/lang/String;
        //   134: iconst_1
        //   135: invokestatic 121	android/media/ThumbnailUtils:createVideoThumbnail	(Ljava/lang/String;I)Landroid/graphics/Bitmap;
        //   138: astore_1
        //   139: new 15	org/vidogram/messenger/camera/CameraController$9$2
        //   142: dup
        //   143: aload_0
        //   144: aload_1
        //   145: invokespecial 124	org/vidogram/messenger/camera/CameraController$9$2:<init>	(Lorg/vidogram/messenger/camera/CameraController$9;Landroid/graphics/Bitmap;)V
        //   148: invokestatic 129	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   151: return
        //   152: astore_3
        //   153: aload_3
        //   154: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   157: goto -111 -> 46
        //   160: astore_1
        //   161: aload_1
        //   162: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   165: return
        //   166: astore_2
        //   167: aload_2
        //   168: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   171: goto -121 -> 50
        //   174: astore_2
        //   175: aload_2
        //   176: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   179: goto -121 -> 58
        //   182: astore_2
        //   183: aload_2
        //   184: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   187: goto -122 -> 65
        //   190: astore_2
        //   191: aload_2
        //   192: invokestatic 135	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   195: goto -114 -> 81
        //   198: aload_0
        //   199: getfield 25	org/vidogram/messenger/camera/CameraController$9:this$0	Lorg/vidogram/messenger/camera/CameraController;
        //   202: aconst_null
        //   203: invokestatic 139	org/vidogram/messenger/camera/CameraController:access$402	(Lorg/vidogram/messenger/camera/CameraController;Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;)Lorg/vidogram/messenger/camera/CameraController$VideoTakeCallback;
        //   206: pop
        //   207: return
        //   208: aconst_null
        //   209: astore_1
        //   210: goto -71 -> 139
        //
        // Exception table:
        //   from	to	target	type
        //   42	46	152	java/lang/Exception
        //   0	11	160	java/lang/Exception
        //   15	42	160	java/lang/Exception
        //   81	139	160	java/lang/Exception
        //   139	151	160	java/lang/Exception
        //   153	157	160	java/lang/Exception
        //   167	171	160	java/lang/Exception
        //   175	179	160	java/lang/Exception
        //   183	187	160	java/lang/Exception
        //   191	195	160	java/lang/Exception
        //   198	207	160	java/lang/Exception
        //   46	50	166	java/lang/Exception
        //   50	58	174	java/lang/Exception
        //   58	65	182	java/lang/Exception
        //   65	81	190	java/lang/Exception
      }
    });
  }

  public boolean takePicture(File paramFile, CameraSession paramCameraSession, Runnable paramRunnable)
  {
    if (paramCameraSession == null)
      return false;
    paramCameraSession = paramCameraSession.cameraInfo;
    Camera localCamera = paramCameraSession.camera;
    try
    {
      localCamera.takePicture(null, null, new Camera.PictureCallback(paramFile, paramCameraSession, paramRunnable)
      {
        // ERROR //
        public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera)
        {
          // Byte code:
          //   0: fconst_1
          //   1: fstore_3
          //   2: invokestatic 45	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
          //   5: i2f
          //   6: getstatic 49	org/vidogram/messenger/AndroidUtilities:density	F
          //   9: fdiv
          //   10: f2i
          //   11: istore 5
          //   13: getstatic 55	java/util/Locale:US	Ljava/util/Locale;
          //   16: ldc 57
          //   18: iconst_3
          //   19: anewarray 4	java/lang/Object
          //   22: dup
          //   23: iconst_0
          //   24: aload_0
          //   25: getfield 25	org/vidogram/messenger/camera/CameraController$4:val$path	Ljava/io/File;
          //   28: invokevirtual 63	java/io/File:getAbsolutePath	()Ljava/lang/String;
          //   31: invokestatic 69	org/vidogram/messenger/Utilities:MD5	(Ljava/lang/String;)Ljava/lang/String;
          //   34: aastore
          //   35: dup
          //   36: iconst_1
          //   37: iload 5
          //   39: invokestatic 75	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   42: aastore
          //   43: dup
          //   44: iconst_2
          //   45: iload 5
          //   47: invokestatic 75	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
          //   50: aastore
          //   51: invokestatic 81	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
          //   54: astore 6
          //   56: new 83	android/graphics/BitmapFactory$Options
          //   59: dup
          //   60: invokespecial 84	android/graphics/BitmapFactory$Options:<init>	()V
          //   63: astore_2
          //   64: aload_2
          //   65: iconst_1
          //   66: putfield 88	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
          //   69: aload_1
          //   70: iconst_0
          //   71: aload_1
          //   72: arraylength
          //   73: aload_2
          //   74: invokestatic 94	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
          //   77: pop
          //   78: aload_2
          //   79: getfield 98	android/graphics/BitmapFactory$Options:outWidth	I
          //   82: i2f
          //   83: invokestatic 45	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
          //   86: i2f
          //   87: fdiv
          //   88: aload_2
          //   89: getfield 101	android/graphics/BitmapFactory$Options:outHeight	I
          //   92: i2f
          //   93: invokestatic 45	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
          //   96: i2f
          //   97: fdiv
          //   98: invokestatic 107	java/lang/Math:max	(FF)F
          //   101: fstore 4
          //   103: fload 4
          //   105: fconst_1
          //   106: fcmpg
          //   107: ifge +275 -> 382
          //   110: aload_2
          //   111: iconst_0
          //   112: putfield 88	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
          //   115: aload_2
          //   116: fload_3
          //   117: f2i
          //   118: putfield 110	android/graphics/BitmapFactory$Options:inSampleSize	I
          //   121: aload_2
          //   122: iconst_1
          //   123: putfield 113	android/graphics/BitmapFactory$Options:inPurgeable	Z
          //   126: aload_1
          //   127: iconst_0
          //   128: aload_1
          //   129: arraylength
          //   130: aload_2
          //   131: invokestatic 94	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
          //   134: astore_2
          //   135: aload_0
          //   136: getfield 27	org/vidogram/messenger/camera/CameraController$4:val$info	Lorg/vidogram/messenger/camera/CameraInfo;
          //   139: getfield 118	org/vidogram/messenger/camera/CameraInfo:frontCamera	I
          //   142: istore 5
          //   144: iload 5
          //   146: ifeq +154 -> 300
          //   149: new 120	android/graphics/Matrix
          //   152: dup
          //   153: invokespecial 121	android/graphics/Matrix:<init>	()V
          //   156: astore 7
          //   158: aload 7
          //   160: aload_1
          //   161: invokestatic 125	org/vidogram/messenger/camera/CameraController:access$100	([B)I
          //   164: i2f
          //   165: invokevirtual 129	android/graphics/Matrix:setRotate	(F)V
          //   168: aload 7
          //   170: ldc 130
          //   172: fconst_1
          //   173: invokevirtual 134	android/graphics/Matrix:postScale	(FF)Z
          //   176: pop
          //   177: aload_2
          //   178: iconst_0
          //   179: iconst_0
          //   180: aload_2
          //   181: invokevirtual 139	android/graphics/Bitmap:getWidth	()I
          //   184: aload_2
          //   185: invokevirtual 142	android/graphics/Bitmap:getHeight	()I
          //   188: aload 7
          //   190: iconst_0
          //   191: invokestatic 148	org/vidogram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
          //   194: astore 7
          //   196: aload_2
          //   197: invokevirtual 151	android/graphics/Bitmap:recycle	()V
          //   200: new 153	java/io/FileOutputStream
          //   203: dup
          //   204: aload_0
          //   205: getfield 25	org/vidogram/messenger/camera/CameraController$4:val$path	Ljava/io/File;
          //   208: invokespecial 156	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
          //   211: astore 8
          //   213: aload 7
          //   215: getstatic 162	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
          //   218: bipush 80
          //   220: aload 8
          //   222: invokevirtual 166	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
          //   225: pop
          //   226: aload 8
          //   228: invokevirtual 169	java/io/FileOutputStream:flush	()V
          //   231: aload 8
          //   233: invokevirtual 173	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
          //   236: invokevirtual 178	java/io/FileDescriptor:sync	()V
          //   239: aload 8
          //   241: invokevirtual 181	java/io/FileOutputStream:close	()V
          //   244: aload 7
          //   246: ifnull +20 -> 266
          //   249: invokestatic 187	org/vidogram/messenger/ImageLoader:getInstance	()Lorg/vidogram/messenger/ImageLoader;
          //   252: new 189	android/graphics/drawable/BitmapDrawable
          //   255: dup
          //   256: aload 7
          //   258: invokespecial 192	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
          //   261: aload 6
          //   263: invokevirtual 196	org/vidogram/messenger/ImageLoader:putImageToCache	(Landroid/graphics/drawable/BitmapDrawable;Ljava/lang/String;)V
          //   266: aload_0
          //   267: getfield 29	org/vidogram/messenger/camera/CameraController$4:val$callback	Ljava/lang/Runnable;
          //   270: ifnull +12 -> 282
          //   273: aload_0
          //   274: getfield 29	org/vidogram/messenger/camera/CameraController$4:val$callback	Ljava/lang/Runnable;
          //   277: invokeinterface 201 1 0
          //   282: return
          //   283: astore_2
          //   284: aload_2
          //   285: invokestatic 207	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   288: aconst_null
          //   289: astore_2
          //   290: goto -155 -> 135
          //   293: astore 7
          //   295: aload 7
          //   297: invokestatic 207	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   300: new 153	java/io/FileOutputStream
          //   303: dup
          //   304: aload_0
          //   305: getfield 25	org/vidogram/messenger/camera/CameraController$4:val$path	Ljava/io/File;
          //   308: invokespecial 156	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
          //   311: astore 7
          //   313: aload 7
          //   315: aload_1
          //   316: invokevirtual 211	java/io/FileOutputStream:write	([B)V
          //   319: aload 7
          //   321: invokevirtual 169	java/io/FileOutputStream:flush	()V
          //   324: aload 7
          //   326: invokevirtual 173	java/io/FileOutputStream:getFD	()Ljava/io/FileDescriptor;
          //   329: invokevirtual 178	java/io/FileDescriptor:sync	()V
          //   332: aload 7
          //   334: invokevirtual 181	java/io/FileOutputStream:close	()V
          //   337: aload_2
          //   338: ifnull +19 -> 357
          //   341: invokestatic 187	org/vidogram/messenger/ImageLoader:getInstance	()Lorg/vidogram/messenger/ImageLoader;
          //   344: new 189	android/graphics/drawable/BitmapDrawable
          //   347: dup
          //   348: aload_2
          //   349: invokespecial 192	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
          //   352: aload 6
          //   354: invokevirtual 196	org/vidogram/messenger/ImageLoader:putImageToCache	(Landroid/graphics/drawable/BitmapDrawable;Ljava/lang/String;)V
          //   357: aload_0
          //   358: getfield 29	org/vidogram/messenger/camera/CameraController$4:val$callback	Ljava/lang/Runnable;
          //   361: ifnull -79 -> 282
          //   364: aload_0
          //   365: getfield 29	org/vidogram/messenger/camera/CameraController$4:val$callback	Ljava/lang/Runnable;
          //   368: invokeinterface 201 1 0
          //   373: return
          //   374: astore_1
          //   375: aload_1
          //   376: invokestatic 207	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   379: goto -22 -> 357
          //   382: fload 4
          //   384: fstore_3
          //   385: goto -275 -> 110
          //
          // Exception table:
          //   from	to	target	type
          //   56	103	283	java/lang/Throwable
          //   110	135	283	java/lang/Throwable
          //   149	244	293	java/lang/Throwable
          //   249	266	293	java/lang/Throwable
          //   266	282	293	java/lang/Throwable
          //   135	144	374	java/lang/Exception
          //   149	244	374	java/lang/Exception
          //   249	266	374	java/lang/Exception
          //   266	282	374	java/lang/Exception
          //   295	300	374	java/lang/Exception
          //   300	337	374	java/lang/Exception
          //   341	357	374	java/lang/Exception
        }
      });
      return true;
    }
    catch (Exception paramFile)
    {
      FileLog.e(paramFile);
    }
    return false;
  }

  static class CompareSizesByArea
    implements Comparator<Size>
  {
    public int compare(Size paramSize1, Size paramSize2)
    {
      return Long.signum(paramSize1.getWidth() * paramSize1.getHeight() - paramSize2.getWidth() * paramSize2.getHeight());
    }
  }

  public static abstract interface VideoTakeCallback
  {
    public abstract void onFinishVideoRecording(Bitmap paramBitmap);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.camera.CameraController
 * JD-Core Version:    0.6.0
 */