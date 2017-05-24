package org.webrtc;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraEnumerationAndroid
{
  private static final String TAG = "CameraEnumerationAndroid";

  public static CameraEnumerationAndroid.CaptureFormat.FramerateRange getClosestSupportedFramerateRange(List<CameraEnumerationAndroid.CaptureFormat.FramerateRange> paramList, int paramInt)
  {
    return (CameraEnumerationAndroid.CaptureFormat.FramerateRange)Collections.min(paramList, new ClosestComparator(paramInt)
    {
      private static final int MAX_FPS_DIFF_THRESHOLD = 5000;
      private static final int MAX_FPS_HIGH_DIFF_WEIGHT = 3;
      private static final int MAX_FPS_LOW_DIFF_WEIGHT = 1;
      private static final int MIN_FPS_HIGH_VALUE_WEIGHT = 4;
      private static final int MIN_FPS_LOW_VALUE_WEIGHT = 1;
      private static final int MIN_FPS_THRESHOLD = 8000;

      private int progressivePenalty(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        if (paramInt1 < paramInt2)
          return paramInt1 * paramInt3;
        return paramInt2 * paramInt3 + (paramInt1 - paramInt2) * paramInt4;
      }

      int diff(CameraEnumerationAndroid.CaptureFormat.FramerateRange paramFramerateRange)
      {
        return progressivePenalty(paramFramerateRange.min, 8000, 1, 4) + progressivePenalty(Math.abs(this.val$requestedFps * 1000 - paramFramerateRange.max), 5000, 1, 3);
      }
    });
  }

  public static Size getClosestSupportedSize(List<Size> paramList, int paramInt1, int paramInt2)
  {
    return (Size)Collections.min(paramList, new ClosestComparator(paramInt1, paramInt2)
    {
      int diff(Size paramSize)
      {
        return Math.abs(this.val$requestedWidth - paramSize.width) + Math.abs(this.val$requestedHeight - paramSize.height);
      }
    });
  }

  @Deprecated
  public static int getDeviceCount()
  {
    return new Camera1Enumerator().getDeviceNames().length;
  }

  @Deprecated
  public static String getDeviceName(int paramInt)
  {
    new Camera1Enumerator();
    return Camera1Enumerator.getDeviceName(paramInt);
  }

  @Deprecated
  public static String[] getDeviceNames()
  {
    return new Camera1Enumerator().getDeviceNames();
  }

  @Deprecated
  public static String getNameOfBackFacingDevice()
  {
    return getNameOfDevice(0);
  }

  private static String getNameOfDevice(int paramInt)
  {
    Camera.CameraInfo localCameraInfo = new Camera.CameraInfo();
    int i = 0;
    while (i < Camera.getNumberOfCameras())
      try
      {
        Camera.getCameraInfo(i, localCameraInfo);
        if (localCameraInfo.facing == paramInt)
        {
          String str = getDeviceName(i);
          return str;
        }
      }
      catch (Exception localException)
      {
        Logging.e("CameraEnumerationAndroid", "getCameraInfo() failed on index " + i, localException);
        i += 1;
      }
    return null;
  }

  @Deprecated
  public static String getNameOfFrontFacingDevice()
  {
    return getNameOfDevice(1);
  }

  public static class CaptureFormat
  {
    public final FramerateRange framerate;
    public final int height;
    public final int imageFormat = 17;
    public final int width;

    public CaptureFormat(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.framerate = new FramerateRange(paramInt3, paramInt4);
    }

    public CaptureFormat(int paramInt1, int paramInt2, FramerateRange paramFramerateRange)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.framerate = paramFramerateRange;
    }

    public static int frameSize(int paramInt1, int paramInt2, int paramInt3)
    {
      if (paramInt3 != 17)
        throw new UnsupportedOperationException("Don't know how to calculate the frame size of non-NV21 image formats.");
      return paramInt1 * paramInt2 * ImageFormat.getBitsPerPixel(paramInt3) / 8;
    }

    public boolean equals(Object paramObject)
    {
      if (!(paramObject instanceof CaptureFormat));
      do
      {
        return false;
        paramObject = (CaptureFormat)paramObject;
      }
      while ((this.width != paramObject.width) || (this.height != paramObject.height) || (!this.framerate.equals(paramObject.framerate)));
      return true;
    }

    public int frameSize()
    {
      return frameSize(this.width, this.height, 17);
    }

    public int hashCode()
    {
      return (this.width * 65497 + this.height) * 251 + 1 + this.framerate.hashCode();
    }

    public String toString()
    {
      return this.width + "x" + this.height + "@" + this.framerate;
    }

    public static class FramerateRange
    {
      public int max;
      public int min;

      public FramerateRange(int paramInt1, int paramInt2)
      {
        this.min = paramInt1;
        this.max = paramInt2;
      }

      public boolean equals(Object paramObject)
      {
        if (!(paramObject instanceof FramerateRange));
        do
        {
          return false;
          paramObject = (FramerateRange)paramObject;
        }
        while ((this.min != paramObject.min) || (this.max != paramObject.max));
        return true;
      }

      public int hashCode()
      {
        return 65537 * this.min + 1 + this.max;
      }

      public String toString()
      {
        return "[" + this.min / 1000.0F + ":" + this.max / 1000.0F + "]";
      }
    }
  }

  private static abstract class ClosestComparator<T>
    implements Comparator<T>
  {
    public int compare(T paramT1, T paramT2)
    {
      return diff(paramT1) - diff(paramT2);
    }

    abstract int diff(T paramT);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.CameraEnumerationAndroid
 * JD-Core Version:    0.6.0
 */