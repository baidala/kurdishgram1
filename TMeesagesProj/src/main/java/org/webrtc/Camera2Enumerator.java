package org.webrtc;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.util.AndroidException;
import android.util.Range;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.List<Lorg.webrtc.Size;>;
import java.util.Map;

@TargetApi(21)
public class Camera2Enumerator
  implements CameraEnumerator
{
  private static final double NANO_SECONDS_PER_SECOND = 1000000000.0D;
  private static final String TAG = "Camera2Enumerator";
  private static final Map<String, List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats = new HashMap();
  final CameraManager cameraManager;
  final Context context;

  public Camera2Enumerator(Context paramContext)
  {
    this.context = paramContext;
    this.cameraManager = ((CameraManager)paramContext.getSystemService("camera"));
  }

  static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(Range<Integer>[] paramArrayOfRange, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfRange.length;
    int i = 0;
    while (i < j)
    {
      Range<Integer> localRange = paramArrayOfRange[i];
      localArrayList.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(((Integer)localRange.getLower()).intValue() * paramInt, ((Integer)localRange.getUpper()).intValue() * paramInt));
      i += 1;
    }
    return localArrayList;
  }

  private static List<Size> convertSizes(android.util.Size[] paramArrayOfSize)
  {
    ArrayList localArrayList = new ArrayList();
    int j = paramArrayOfSize.length;
    int i = 0;
    while (i < j)
    {
      android.util.Size localSize = paramArrayOfSize[i];
      localArrayList.add(new Size(localSize.getWidth(), localSize.getHeight()));
      i += 1;
    }
    return localArrayList;
  }

  private CameraCharacteristics getCameraCharacteristics(String paramString)
  {
    try
    {
      paramString = this.cameraManager.getCameraCharacteristics(paramString);
      return paramString;
    }
    catch (AndroidException paramString)
    {
      Logging.e("Camera2Enumerator", "Camera access exception: " + paramString);
    }
    return null;
  }

  static int getFpsUnitFactor(Range<Integer>[] paramArrayOfRange)
  {
    if (paramArrayOfRange.length == 0)
      return 1000;
    if (((Integer)paramArrayOfRange[0].getUpper()).intValue() < 1000);
    for (int i = 1000; ; i = 1)
      return i;
  }

  static List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(Context paramContext, String paramString)
  {
    return getSupportedFormats((CameraManager)paramContext.getSystemService("camera"), paramString);
  }

  static List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(CameraManager paramCameraManager, String paramString)
  {
    long l3;
    Object localObject3;
    int i;
    synchronized (cachedSupportedFormats)
    {
      if (cachedSupportedFormats.containsKey(paramString))
      {
        paramCameraManager = (List)cachedSupportedFormats.get(paramString);
        return paramCameraManager;
      }
      Logging.d("Camera2Enumerator", "Get supported formats for camera index " + paramString + ".");
      l3 = SystemClock.elapsedRealtime();
      try
      {
        localObject1 = paramCameraManager.getCameraCharacteristics(paramString);
        paramCameraManager = (StreamConfigurationMap)((CameraCharacteristics)localObject1).get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        localObject2 = (Range[])((CameraCharacteristics)localObject1).get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        localObject3 = convertFramerates(localObject2, getFpsUnitFactor(localObject2));
        localObject2 = getSupportedSizes((CameraCharacteristics)localObject1);
        i = 0;
        localObject1 = ((List)localObject3).iterator();
        while (((Iterator)localObject1).hasNext())
          i = Math.max(i, ((CameraEnumerationAndroid.CaptureFormat.FramerateRange)((Iterator)localObject1).next()).max);
      }
      catch (Exception paramCameraManager)
      {
        Logging.e("Camera2Enumerator", "getCameraCharacteristics(): " + paramCameraManager);
        paramCameraManager = new ArrayList();
        return paramCameraManager;
      }
    }
    Object localObject1 = new ArrayList();
    Object localObject2 = ((List)localObject2).iterator();
    while (true)
    {
      long l1;
      if (((Iterator)localObject2).hasNext())
      {
        localObject3 = (Size)((Iterator)localObject2).next();
        l1 = 0L;
      }
      try
      {
        long l2 = paramCameraManager.getOutputMinFrameDuration(SurfaceTexture.class, new android.util.Size(((Size)localObject3).width, ((Size)localObject3).height));
        l1 = l2;
        label281: if (l1 == 0L);
        for (int j = i; ; j = (int)Math.round(1000000000.0D / l1) * 1000)
        {
          ((List)localObject1).add(new CameraEnumerationAndroid.CaptureFormat(((Size)localObject3).width, ((Size)localObject3).height, 0, j));
          Logging.d("Camera2Enumerator", "Format: " + ((Size)localObject3).width + "x" + ((Size)localObject3).height + "@" + j);
          break;
        }
        cachedSupportedFormats.put(paramString, localObject1);
        l1 = SystemClock.elapsedRealtime();
        Logging.d("Camera2Enumerator", "Get supported formats for camera index " + paramString + " done." + " Time spent: " + (l1 - l3) + " ms.");
        monitorexit;
        return localObject1;
      }
      catch (Exception localException)
      {
        break label281;
      }
    }
  }

  static List<Size> getSupportedSizes(CameraCharacteristics paramCameraCharacteristics)
  {
    Object localObject1 = (StreamConfigurationMap)paramCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    int i = ((Integer)paramCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)).intValue();
    Object localObject2 = convertSizes(((StreamConfigurationMap)localObject1).getOutputSizes(SurfaceTexture.class));
    if ((Build.VERSION.SDK_INT < 22) && (i == 2))
    {
      paramCameraCharacteristics = (Rect)paramCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
      localObject1 = new ArrayList();
      localObject2 = ((List)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        Size localSize = (Size)((Iterator)localObject2).next();
        if (paramCameraCharacteristics.width() * localSize.height != paramCameraCharacteristics.height() * localSize.width)
          continue;
        ((ArrayList)localObject1).add(localSize);
      }
      return localObject1;
    }
    return (List<Size>)(List<Size>)localObject2;
  }

  public static boolean isSupported()
  {
    return Build.VERSION.SDK_INT >= 21;
  }

  public CameraVideoCapturer createCapturer(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler)
  {
    return new Camera2Capturer(this.context, paramString, paramCameraEventsHandler);
  }

  public String[] getDeviceNames()
  {
    try
    {
      String[] arrayOfString = this.cameraManager.getCameraIdList();
      return arrayOfString;
    }
    catch (AndroidException localAndroidException)
    {
      Logging.e("Camera2Enumerator", "Camera access exception: " + localAndroidException);
    }
    return new String[0];
  }

  public boolean isBackFacing(String paramString)
  {
    paramString = getCameraCharacteristics(paramString);
    return (paramString != null) && (((Integer)paramString.get(CameraCharacteristics.LENS_FACING)).intValue() == 1);
  }

  public boolean isFrontFacing(String paramString)
  {
    paramString = getCameraCharacteristics(paramString);
    return (paramString != null) && (((Integer)paramString.get(CameraCharacteristics.LENS_FACING)).intValue() == 0);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Camera2Enumerator
 * JD-Core Version:    0.6.0
 */