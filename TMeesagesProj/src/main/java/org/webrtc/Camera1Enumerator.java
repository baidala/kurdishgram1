package org.webrtc;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Camera1Enumerator
  implements CameraEnumerator
{
  private static final String TAG = "Camera1Enumerator";
  private static List<List<CameraEnumerationAndroid.CaptureFormat>> cachedSupportedFormats;
  private final boolean captureToTexture;

  public Camera1Enumerator()
  {
    this(true);
  }

  public Camera1Enumerator(boolean paramBoolean)
  {
    this.captureToTexture = paramBoolean;
  }

  static List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> convertFramerates(List<int[]> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      int[] arrayOfInt = (int[])paramList.next();
      localArrayList.add(new CameraEnumerationAndroid.CaptureFormat.FramerateRange(arrayOfInt[0], arrayOfInt[1]));
    }
    return localArrayList;
  }

  static List<Size> convertSizes(List<Camera.Size> paramList)
  {
    ArrayList localArrayList = new ArrayList();
    paramList = paramList.iterator();
    while (paramList.hasNext())
    {
      Camera.Size localSize = (Camera.Size)paramList.next();
      localArrayList.add(new Size(localSize.width, localSize.height));
    }
    return localArrayList;
  }

  private static List<CameraEnumerationAndroid.CaptureFormat> enumerateFormats(int paramInt)
  {
    Logging.d("Camera1Enumerator", "Get supported formats for camera index " + paramInt + ".");
    long l1 = SystemClock.elapsedRealtime();
    Camera.Parameters localParameters = null;
    Object localObject4 = null;
    Object localObject1 = localObject4;
    Object localObject3 = localParameters;
    while (true)
    {
      try
      {
        Logging.d("Camera1Enumerator", "Opening camera with index " + paramInt);
        localObject1 = localObject4;
        localObject3 = localParameters;
        localObject4 = Camera.open(paramInt);
        localObject1 = localObject4;
        localObject3 = localObject4;
        localParameters = ((Camera)localObject4).getParameters();
        if (localObject4 == null)
          continue;
        ((Camera)localObject4).release();
        localObject3 = new ArrayList();
        try
        {
          localObject1 = localParameters.getSupportedPreviewFpsRange();
          if (localObject1 != null)
          {
            localObject1 = (int[])((List)localObject1).get(((List)localObject1).size() - 1);
            i = localObject1[0];
            j = localObject1[1];
            localObject1 = localParameters.getSupportedPreviewSizes().iterator();
            if (!((Iterator)localObject1).hasNext())
              continue;
            localObject4 = (Camera.Size)((Iterator)localObject1).next();
            ((List)localObject3).add(new CameraEnumerationAndroid.CaptureFormat(((Camera.Size)localObject4).width, ((Camera.Size)localObject4).height, i, j));
            continue;
          }
        }
        catch (Exception localException)
        {
          Logging.e("Camera1Enumerator", "getSupportedFormats() failed on camera index " + paramInt, localException);
          long l2 = SystemClock.elapsedRealtime();
          Logging.d("Camera1Enumerator", "Get supported formats for camera index " + paramInt + " done." + " Time spent: " + (l2 - l1) + " ms.");
          return localObject3;
        }
      }
      catch (RuntimeException localArrayList)
      {
        localObject3 = localException;
        Logging.e("Camera1Enumerator", "Open camera failed on camera index " + paramInt, localRuntimeException);
        localObject3 = localException;
        ArrayList localArrayList = new ArrayList();
        localObject3 = localArrayList;
        if (localException == null)
          continue;
        localException.release();
        return localArrayList;
      }
      finally
      {
        if (localObject3 == null)
          continue;
        ((Camera)localObject3).release();
      }
      int j = 0;
      int i = 0;
    }
  }

  static int getCameraIndex(String paramString)
  {
    Logging.d("Camera1Enumerator", "getCameraIndex: " + paramString);
    int i = 0;
    while (i < Camera.getNumberOfCameras())
    {
      if (paramString.equals(CameraEnumerationAndroid.getDeviceName(i)))
        return i;
      i += 1;
    }
    throw new IllegalArgumentException("No such camera: " + paramString);
  }

  private static Camera.CameraInfo getCameraInfo(int paramInt)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    try
    {
      Camera.getCameraInfo(paramInt, localCameraInfo);
      return localCameraInfo;
    }
    catch (Exception localException)
    {
      Logging.e("Camera1Enumerator", "getCameraInfo failed on index " + paramInt, localException);
    }
    return null;
  }

  static String getDeviceName(int paramInt)
  {
    Camera.CameraInfo localCameraInfo = getCameraInfo(paramInt);
    if (localCameraInfo.facing == 1);
    for (String str = "front"; ; str = "back")
      return "Camera " + paramInt + ", Facing " + str + ", Orientation " + localCameraInfo.orientation;
  }

  static List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats(int paramInt)
  {
    monitorenter;
    try
    {
      if (cachedSupportedFormats == null)
      {
        cachedSupportedFormats = new ArrayList();
        int i = 0;
        while (i < CameraEnumerationAndroid.getDeviceCount())
        {
          cachedSupportedFormats.add(enumerateFormats(i));
          i += 1;
        }
      }
      List localList = (List)cachedSupportedFormats.get(paramInt);
      return localList;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public CameraVideoCapturer createCapturer(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler)
  {
    return new VideoCapturerAndroid(paramString, paramCameraEventsHandler, this.captureToTexture);
  }

  public String[] getDeviceNames()
  {
    String[] arrayOfString = new String[Camera.getNumberOfCameras()];
    int i = 0;
    while (i < Camera.getNumberOfCameras())
    {
      arrayOfString[i] = getDeviceName(i);
      i += 1;
    }
    return arrayOfString;
  }

  public boolean isBackFacing(String paramString)
  {
    return getCameraInfo(getCameraIndex(paramString)).facing == 0;
  }

  public boolean isFrontFacing(String paramString)
  {
    return getCameraInfo(getCameraIndex(paramString)).facing == 1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Camera1Enumerator
 * JD-Core Version:    0.6.0
 */