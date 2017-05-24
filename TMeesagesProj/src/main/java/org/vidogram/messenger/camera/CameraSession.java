package org.vidogram.messenger.camera;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.WindowManager;
import java.util.ArrayList;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;

public class CameraSession
{
  public static final int ORIENTATION_HYSTERESIS = 5;
  private Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback()
  {
    public void onAutoFocus(boolean paramBoolean, Camera paramCamera)
    {
      if (paramBoolean);
    }
  };
  protected CameraInfo cameraInfo;
  private String currentFlashMode = "off";
  private int currentOrientation;
  private boolean initied;
  private boolean isVideo;
  private int jpegOrientation;
  private int lastDisplayOrientation = -1;
  private int lastOrientation = -1;
  private boolean meteringAreaSupported;
  private OrientationEventListener orientationEventListener;
  private final int pictureFormat;
  private final Size pictureSize;
  private final Size previewSize;
  private boolean sameTakePictureOrientation;

  public CameraSession(CameraInfo paramCameraInfo, Size paramSize1, Size paramSize2, int paramInt)
  {
    this.previewSize = paramSize1;
    this.pictureSize = paramSize2;
    this.pictureFormat = paramInt;
    this.cameraInfo = paramCameraInfo;
    paramSize1 = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0);
    if (this.cameraInfo.frontCamera != 0);
    for (paramCameraInfo = "flashMode_front"; ; paramCameraInfo = "flashMode")
    {
      this.currentFlashMode = paramSize1.getString(paramCameraInfo, "off");
      this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext)
      {
        public void onOrientationChanged(int paramInt)
        {
          if ((CameraSession.this.orientationEventListener == null) || (!CameraSession.this.initied) || (paramInt == -1));
          do
          {
            return;
            CameraSession.access$202(CameraSession.this, CameraSession.this.roundOrientation(paramInt, CameraSession.this.jpegOrientation));
            paramInt = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
          }
          while ((CameraSession.this.lastOrientation == CameraSession.this.jpegOrientation) && (paramInt == CameraSession.this.lastDisplayOrientation));
          if (!CameraSession.this.isVideo)
            CameraSession.this.configurePhotoCamera();
          CameraSession.access$502(CameraSession.this, paramInt);
          CameraSession.access$402(CameraSession.this, CameraSession.this.jpegOrientation);
        }
      };
      if (!this.orientationEventListener.canDetectOrientation())
        break;
      this.orientationEventListener.enable();
      return;
    }
    this.orientationEventListener.disable();
    this.orientationEventListener = null;
  }

  private int getDisplayOrientation(Camera.CameraInfo paramCameraInfo, boolean paramBoolean)
  {
    int i;
    switch (((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation())
    {
    default:
      i = 0;
    case 0:
    case 1:
    case 2:
    case 3:
    }
    while (true)
      if (paramCameraInfo.facing == 1)
      {
        int j = (360 - (i + paramCameraInfo.orientation) % 360) % 360;
        i = j;
        if (!paramBoolean)
        {
          i = j;
          if (j == 90)
            i = 270;
        }
        if ((paramBoolean) || (!"Huawei".equals(Build.MANUFACTURER)) || (!"angler".equals(Build.PRODUCT)) || (i != 270))
          break;
        return 90;
        i = 0;
        continue;
        i = 90;
        continue;
        i = 180;
        continue;
        i = 270;
        continue;
      }
      else
      {
        return (paramCameraInfo.orientation - i + 360) % 360;
      }
    return i;
  }

  private int getHigh()
  {
    if (("LGE".equals(Build.MANUFACTURER)) && ("g3_tmo_us".equals(Build.PRODUCT)))
      return 4;
    return 1;
  }

  private int roundOrientation(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (paramInt2 == -1);
    while (true)
    {
      if (i != 0)
        paramInt2 = (paramInt1 + 45) / 90 * 90 % 360;
      return paramInt2;
      int j = Math.abs(paramInt1 - paramInt2);
      if (Math.min(j, 360 - j) >= 50)
        continue;
      i = 0;
    }
  }

  public void checkFlashMode(String paramString)
  {
    if (CameraController.getInstance().availableFlashModes.contains(this.currentFlashMode))
      return;
    this.currentFlashMode = paramString;
    configurePhotoCamera();
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit();
    if (this.cameraInfo.frontCamera != 0);
    for (String str = "flashMode_front"; ; str = "flashMode")
    {
      localEditor.putString(str, paramString).commit();
      return;
    }
  }

  // ERROR //
  protected void configurePhotoCamera()
  {
    // Byte code:
    //   0: iconst_1
    //   1: istore_3
    //   2: aload_0
    //   3: getfield 58	org/vidogram/messenger/camera/CameraSession:cameraInfo	Lorg/vidogram/messenger/camera/CameraInfo;
    //   6: getfield 230	org/vidogram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   9: astore 5
    //   11: aload 5
    //   13: ifnull +396 -> 409
    //   16: new 152	android/hardware/Camera$CameraInfo
    //   19: dup
    //   20: invokespecial 231	android/hardware/Camera$CameraInfo:<init>	()V
    //   23: astore 6
    //   25: aload 5
    //   27: invokevirtual 237	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   30: astore 4
    //   32: aload_0
    //   33: getfield 58	org/vidogram/messenger/camera/CameraSession:cameraInfo	Lorg/vidogram/messenger/camera/CameraInfo;
    //   36: invokevirtual 240	org/vidogram/messenger/camera/CameraInfo:getCameraId	()I
    //   39: aload 6
    //   41: invokestatic 244	android/hardware/Camera:getCameraInfo	(ILandroid/hardware/Camera$CameraInfo;)V
    //   44: aload_0
    //   45: aload 6
    //   47: iconst_1
    //   48: invokespecial 246	org/vidogram/messenger/camera/CameraSession:getDisplayOrientation	(Landroid/hardware/Camera$CameraInfo;Z)I
    //   51: istore_2
    //   52: ldc 248
    //   54: getstatic 165	android/os/Build:MANUFACTURER	Ljava/lang/String;
    //   57: invokevirtual 171	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   60: ifeq +350 -> 410
    //   63: ldc 250
    //   65: getstatic 176	android/os/Build:PRODUCT	Ljava/lang/String;
    //   68: invokevirtual 171	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   71: ifeq +339 -> 410
    //   74: iconst_0
    //   75: istore_1
    //   76: aload_0
    //   77: iload_1
    //   78: putfield 252	org/vidogram/messenger/camera/CameraSession:currentOrientation	I
    //   81: aload 5
    //   83: iload_1
    //   84: invokevirtual 256	android/hardware/Camera:setDisplayOrientation	(I)V
    //   87: aload 4
    //   89: ifnull +320 -> 409
    //   92: aload 4
    //   94: aload_0
    //   95: getfield 52	org/vidogram/messenger/camera/CameraSession:previewSize	Lorg/vidogram/messenger/camera/Size;
    //   98: invokevirtual 261	org/vidogram/messenger/camera/Size:getWidth	()I
    //   101: aload_0
    //   102: getfield 52	org/vidogram/messenger/camera/CameraSession:previewSize	Lorg/vidogram/messenger/camera/Size;
    //   105: invokevirtual 264	org/vidogram/messenger/camera/Size:getHeight	()I
    //   108: invokevirtual 270	android/hardware/Camera$Parameters:setPreviewSize	(II)V
    //   111: aload 4
    //   113: aload_0
    //   114: getfield 54	org/vidogram/messenger/camera/CameraSession:pictureSize	Lorg/vidogram/messenger/camera/Size;
    //   117: invokevirtual 261	org/vidogram/messenger/camera/Size:getWidth	()I
    //   120: aload_0
    //   121: getfield 54	org/vidogram/messenger/camera/CameraSession:pictureSize	Lorg/vidogram/messenger/camera/Size;
    //   124: invokevirtual 264	org/vidogram/messenger/camera/Size:getHeight	()I
    //   127: invokevirtual 273	android/hardware/Camera$Parameters:setPictureSize	(II)V
    //   130: aload 4
    //   132: aload_0
    //   133: getfield 56	org/vidogram/messenger/camera/CameraSession:pictureFormat	I
    //   136: invokevirtual 276	android/hardware/Camera$Parameters:setPictureFormat	(I)V
    //   139: aload 4
    //   141: invokevirtual 280	android/hardware/Camera$Parameters:getSupportedFocusModes	()Ljava/util/List;
    //   144: ldc_w 282
    //   147: invokeinterface 285 2 0
    //   152: ifeq +11 -> 163
    //   155: aload 4
    //   157: ldc_w 282
    //   160: invokevirtual 288	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   163: aload_0
    //   164: getfield 115	org/vidogram/messenger/camera/CameraSession:jpegOrientation	I
    //   167: iconst_m1
    //   168: if_icmpeq +236 -> 404
    //   171: aload 6
    //   173: getfield 155	android/hardware/Camera$CameraInfo:facing	I
    //   176: iconst_1
    //   177: if_icmpne +166 -> 343
    //   180: aload 6
    //   182: getfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   185: aload_0
    //   186: getfield 115	org/vidogram/messenger/camera/CameraSession:jpegOrientation	I
    //   189: isub
    //   190: sipush 360
    //   193: iadd
    //   194: sipush 360
    //   197: irem
    //   198: istore_1
    //   199: aload 4
    //   201: iload_1
    //   202: invokevirtual 291	android/hardware/Camera$Parameters:setRotation	(I)V
    //   205: aload 6
    //   207: getfield 155	android/hardware/Camera$CameraInfo:facing	I
    //   210: iconst_1
    //   211: if_icmpne +155 -> 366
    //   214: sipush 360
    //   217: iload_2
    //   218: isub
    //   219: sipush 360
    //   222: irem
    //   223: iload_1
    //   224: if_icmpne +137 -> 361
    //   227: aload_0
    //   228: iload_3
    //   229: putfield 293	org/vidogram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   232: aload 4
    //   234: aload_0
    //   235: getfield 41	org/vidogram/messenger/camera/CameraSession:currentFlashMode	Ljava/lang/String;
    //   238: invokevirtual 296	android/hardware/Camera$Parameters:setFlashMode	(Ljava/lang/String;)V
    //   241: aload 5
    //   243: aload 4
    //   245: invokevirtual 300	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   248: aload 4
    //   250: invokevirtual 303	android/hardware/Camera$Parameters:getMaxNumMeteringAreas	()I
    //   253: ifle +156 -> 409
    //   256: aload_0
    //   257: iconst_1
    //   258: putfield 305	org/vidogram/messenger/camera/CameraSession:meteringAreaSupported	Z
    //   261: return
    //   262: astore 4
    //   264: aload 4
    //   266: invokestatic 311	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   269: aconst_null
    //   270: astore 4
    //   272: goto -240 -> 32
    //   275: aload 6
    //   277: getfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   280: bipush 90
    //   282: irem
    //   283: ifeq +9 -> 292
    //   286: aload 6
    //   288: iconst_0
    //   289: putfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   292: aload 6
    //   294: getfield 155	android/hardware/Camera$CameraInfo:facing	I
    //   297: iconst_1
    //   298: if_icmpne +26 -> 324
    //   301: sipush 360
    //   304: iload_1
    //   305: aload 6
    //   307: getfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   310: iadd
    //   311: sipush 360
    //   314: irem
    //   315: isub
    //   316: sipush 360
    //   319: irem
    //   320: istore_1
    //   321: goto -245 -> 76
    //   324: aload 6
    //   326: getfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   329: iload_1
    //   330: isub
    //   331: sipush 360
    //   334: iadd
    //   335: sipush 360
    //   338: irem
    //   339: istore_1
    //   340: goto -264 -> 76
    //   343: aload 6
    //   345: getfield 158	android/hardware/Camera$CameraInfo:orientation	I
    //   348: aload_0
    //   349: getfield 115	org/vidogram/messenger/camera/CameraSession:jpegOrientation	I
    //   352: iadd
    //   353: sipush 360
    //   356: irem
    //   357: istore_1
    //   358: goto -159 -> 199
    //   361: iconst_0
    //   362: istore_3
    //   363: goto -136 -> 227
    //   366: iload_2
    //   367: iload_1
    //   368: if_icmpne +18 -> 386
    //   371: iconst_1
    //   372: istore_3
    //   373: aload_0
    //   374: iload_3
    //   375: putfield 293	org/vidogram/messenger/camera/CameraSession:sameTakePictureOrientation	Z
    //   378: goto -146 -> 232
    //   381: astore 6
    //   383: goto -151 -> 232
    //   386: iconst_0
    //   387: istore_3
    //   388: goto -15 -> 373
    //   391: astore 4
    //   393: aload 4
    //   395: invokestatic 311	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   398: return
    //   399: astore 5
    //   401: goto -153 -> 248
    //   404: iconst_0
    //   405: istore_1
    //   406: goto -207 -> 199
    //   409: return
    //   410: iload_2
    //   411: tableswitch	default:+29 -> 440, 0:+34->445, 1:+39->450, 2:+45->456, 3:+52->463
    //   441: istore_1
    //   442: goto -167 -> 275
    //   445: iconst_0
    //   446: istore_1
    //   447: goto -172 -> 275
    //   450: bipush 90
    //   452: istore_1
    //   453: goto -178 -> 275
    //   456: sipush 180
    //   459: istore_1
    //   460: goto -185 -> 275
    //   463: sipush 270
    //   466: istore_1
    //   467: goto -192 -> 275
    //
    // Exception table:
    //   from	to	target	type
    //   25	32	262	java/lang/Exception
    //   199	214	381	java/lang/Exception
    //   227	232	381	java/lang/Exception
    //   373	378	381	java/lang/Exception
    //   2	11	391	java/lang/Throwable
    //   16	25	391	java/lang/Throwable
    //   25	32	391	java/lang/Throwable
    //   32	74	391	java/lang/Throwable
    //   76	87	391	java/lang/Throwable
    //   92	163	391	java/lang/Throwable
    //   163	199	391	java/lang/Throwable
    //   199	214	391	java/lang/Throwable
    //   227	232	391	java/lang/Throwable
    //   232	241	391	java/lang/Throwable
    //   241	248	391	java/lang/Throwable
    //   248	261	391	java/lang/Throwable
    //   264	269	391	java/lang/Throwable
    //   275	292	391	java/lang/Throwable
    //   292	321	391	java/lang/Throwable
    //   324	340	391	java/lang/Throwable
    //   343	358	391	java/lang/Throwable
    //   373	378	391	java/lang/Throwable
    //   241	248	399	java/lang/Exception
  }

  protected void configureRecorder(int paramInt, MediaRecorder paramMediaRecorder)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    Camera.getCameraInfo(this.cameraInfo.cameraId, localCameraInfo);
    paramMediaRecorder.setOrientationHint(getDisplayOrientation(localCameraInfo, false));
    int i = getHigh();
    boolean bool1 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, i);
    boolean bool2 = CamcorderProfile.hasProfile(this.cameraInfo.cameraId, 0);
    if ((bool1) && ((paramInt == 1) || (!bool2)))
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, i));
    while (true)
    {
      this.isVideo = true;
      return;
      if (!bool2)
        break;
      paramMediaRecorder.setProfile(CamcorderProfile.get(this.cameraInfo.cameraId, 0));
    }
    throw new IllegalStateException("cannot find valid CamcorderProfile");
  }

  public void destroy()
  {
    this.initied = false;
    if (this.orientationEventListener != null)
    {
      this.orientationEventListener.disable();
      this.orientationEventListener = null;
    }
  }

  // ERROR //
  protected void focusToRect(android.graphics.Rect paramRect1, android.graphics.Rect paramRect2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 58	org/vidogram/messenger/camera/CameraSession:cameraInfo	Lorg/vidogram/messenger/camera/CameraInfo;
    //   4: getfield 230	org/vidogram/messenger/camera/CameraInfo:camera	Landroid/hardware/Camera;
    //   7: astore 5
    //   9: aload 5
    //   11: ifnull +114 -> 125
    //   14: aload 5
    //   16: invokevirtual 349	android/hardware/Camera:cancelAutoFocus	()V
    //   19: aconst_null
    //   20: astore_3
    //   21: aload 5
    //   23: invokevirtual 237	android/hardware/Camera:getParameters	()Landroid/hardware/Camera$Parameters;
    //   26: astore 4
    //   28: aload 4
    //   30: astore_3
    //   31: aload_3
    //   32: ifnull +93 -> 125
    //   35: aload_3
    //   36: ldc_w 351
    //   39: invokevirtual 288	android/hardware/Camera$Parameters:setFocusMode	(Ljava/lang/String;)V
    //   42: new 204	java/util/ArrayList
    //   45: dup
    //   46: invokespecial 352	java/util/ArrayList:<init>	()V
    //   49: astore 4
    //   51: aload 4
    //   53: new 354	android/hardware/Camera$Area
    //   56: dup
    //   57: aload_1
    //   58: sipush 1000
    //   61: invokespecial 357	android/hardware/Camera$Area:<init>	(Landroid/graphics/Rect;I)V
    //   64: invokevirtual 360	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   67: pop
    //   68: aload_3
    //   69: aload 4
    //   71: invokevirtual 364	android/hardware/Camera$Parameters:setFocusAreas	(Ljava/util/List;)V
    //   74: aload_0
    //   75: getfield 305	org/vidogram/messenger/camera/CameraSession:meteringAreaSupported	Z
    //   78: ifeq +32 -> 110
    //   81: new 204	java/util/ArrayList
    //   84: dup
    //   85: invokespecial 352	java/util/ArrayList:<init>	()V
    //   88: astore_1
    //   89: aload_1
    //   90: new 354	android/hardware/Camera$Area
    //   93: dup
    //   94: aload_2
    //   95: sipush 1000
    //   98: invokespecial 357	android/hardware/Camera$Area:<init>	(Landroid/graphics/Rect;I)V
    //   101: invokevirtual 360	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   104: pop
    //   105: aload_3
    //   106: aload_1
    //   107: invokevirtual 367	android/hardware/Camera$Parameters:setMeteringAreas	(Ljava/util/List;)V
    //   110: aload 5
    //   112: aload_3
    //   113: invokevirtual 300	android/hardware/Camera:setParameters	(Landroid/hardware/Camera$Parameters;)V
    //   116: aload 5
    //   118: aload_0
    //   119: getfield 50	org/vidogram/messenger/camera/CameraSession:autoFocusCallback	Landroid/hardware/Camera$AutoFocusCallback;
    //   122: invokevirtual 371	android/hardware/Camera:autoFocus	(Landroid/hardware/Camera$AutoFocusCallback;)V
    //   125: return
    //   126: astore 4
    //   128: aload 4
    //   130: invokestatic 311	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   133: goto -102 -> 31
    //   136: astore_1
    //   137: aload_1
    //   138: invokestatic 311	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   141: return
    //   142: astore_1
    //   143: aload_1
    //   144: invokestatic 311	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   147: return
    //
    // Exception table:
    //   from	to	target	type
    //   21	28	126	java/lang/Exception
    //   0	9	136	java/lang/Exception
    //   14	19	136	java/lang/Exception
    //   35	110	136	java/lang/Exception
    //   128	133	136	java/lang/Exception
    //   143	147	136	java/lang/Exception
    //   110	125	142	java/lang/Exception
  }

  public String getCurrentFlashMode()
  {
    return this.currentFlashMode;
  }

  public int getCurrentOrientation()
  {
    return this.currentOrientation;
  }

  public int getDisplayOrientation()
  {
    try
    {
      Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
      Camera.getCameraInfo(this.cameraInfo.getCameraId(), localCameraInfo);
      int i = getDisplayOrientation(localCameraInfo, true);
      return i;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return 0;
  }

  public String getNextFlashMode()
  {
    ArrayList localArrayList = CameraController.getInstance().availableFlashModes;
    int i = 0;
    while (i < localArrayList.size())
    {
      if (((String)localArrayList.get(i)).equals(this.currentFlashMode))
      {
        if (i < localArrayList.size() - 1)
          return (String)localArrayList.get(i + 1);
        return (String)localArrayList.get(0);
      }
      i += 1;
    }
    return this.currentFlashMode;
  }

  protected boolean isInitied()
  {
    return this.initied;
  }

  public boolean isSameTakePictureOrientation()
  {
    return this.sameTakePictureOrientation;
  }

  public void setCurrentFlashMode(String paramString)
  {
    this.currentFlashMode = paramString;
    configurePhotoCamera();
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("camera", 0).edit();
    if (this.cameraInfo.frontCamera != 0);
    for (String str = "flashMode_front"; ; str = "flashMode")
    {
      localEditor.putString(str, paramString).commit();
      return;
    }
  }

  protected void setInitied()
  {
    this.initied = true;
  }

  protected void stopVideoRecording()
  {
    this.isVideo = false;
    configurePhotoCamera();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.camera.CameraSession
 * JD-Core Version:    0.6.0
 */