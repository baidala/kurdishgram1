package org.webrtc;

public class RtpReceiver
{
  private MediaStreamTrack cachedTrack;
  final long nativeRtpReceiver;

  public RtpReceiver(long paramLong)
  {
    this.nativeRtpReceiver = paramLong;
    this.cachedTrack = new MediaStreamTrack(nativeGetTrack(paramLong));
  }

  private static native void free(long paramLong);

  private static native RtpParameters nativeGetParameters(long paramLong);

  private static native long nativeGetTrack(long paramLong);

  private static native String nativeId(long paramLong);

  private static native boolean nativeSetParameters(long paramLong, RtpParameters paramRtpParameters);

  public void dispose()
  {
    this.cachedTrack.dispose();
    free(this.nativeRtpReceiver);
  }

  public RtpParameters getParameters()
  {
    return nativeGetParameters(this.nativeRtpReceiver);
  }

  public String id()
  {
    return nativeId(this.nativeRtpReceiver);
  }

  public boolean setParameters(RtpParameters paramRtpParameters)
  {
    return nativeSetParameters(this.nativeRtpReceiver, paramRtpParameters);
  }

  public MediaStreamTrack track()
  {
    return this.cachedTrack;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.RtpReceiver
 * JD-Core Version:    0.6.0
 */