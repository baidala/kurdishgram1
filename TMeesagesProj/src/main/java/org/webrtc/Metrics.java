package org.webrtc;

import java.util.HashMap;
import java.util.Map;

public class Metrics
{
  public final Map<String, HistogramInfo> map = new HashMap();

  static
  {
    System.loadLibrary("jingle_peerconnection_so");
  }

  private void add(String paramString, HistogramInfo paramHistogramInfo)
  {
    this.map.put(paramString, paramHistogramInfo);
  }

  public static void enable()
  {
    nativeEnable();
  }

  public static Metrics getAndReset()
  {
    return nativeGetAndReset();
  }

  private static native void nativeEnable();

  private static native Metrics nativeGetAndReset();

  public static class HistogramInfo
  {
    public final int bucketCount;
    public final int max;
    public final int min;
    public final Map<Integer, Integer> samples = new HashMap();

    public HistogramInfo(int paramInt1, int paramInt2, int paramInt3)
    {
      this.min = paramInt1;
      this.max = paramInt2;
      this.bucketCount = paramInt3;
    }

    public void addSample(int paramInt1, int paramInt2)
    {
      this.samples.put(Integer.valueOf(paramInt1), Integer.valueOf(paramInt2));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.Metrics
 * JD-Core Version:    0.6.0
 */