package org.webrtc;

public class RtpSender
{
  private MediaStreamTrack cachedTrack;
  final long nativeRtpSender;
  private boolean ownsTrack = true;

  public RtpSender(long paramLong)
  {
    this.nativeRtpSender = paramLong;
    paramLong = nativeGetTrack(paramLong);
    if (paramLong == 0L);
    for (MediaStreamTrack localMediaStreamTrack = null; ; localMediaStreamTrack = new MediaStreamTrack(paramLong))
    {
      this.cachedTrack = localMediaStreamTrack;
      return;
    }
  }

  private static native void free(long paramLong);

  private static native RtpParameters nativeGetParameters(long paramLong);

  private static native long nativeGetTrack(long paramLong);

  private static native String nativeId(long paramLong);

  private static native boolean nativeSetParameters(long paramLong, RtpParameters paramRtpParameters);

  private static native boolean nativeSetTrack(long paramLong1, long paramLong2);

  public void dispose()
  {
    if ((this.cachedTrack != null) && (this.ownsTrack))
      this.cachedTrack.dispose();
    free(this.nativeRtpSender);
  }

  public RtpParameters getParameters()
  {
    return nativeGetParameters(this.nativeRtpSender);
  }

  public String id()
  {
    return nativeId(this.nativeRtpSender);
  }

  public boolean setParameters(RtpParameters paramRtpParameters)
  {
    return nativeSetParameters(this.nativeRtpSender, paramRtpParameters);
  }

  public boolean setTrack(MediaStreamTrack paramMediaStreamTrack, boolean paramBoolean)
  {
    long l2 = this.nativeRtpSender;
    long l1;
    if (paramMediaStreamTrack == null)
      l1 = 0L;
    while (!nativeSetTrack(l2, l1))
    {
      return false;
      l1 = paramMediaStreamTrack.nativeTrack;
    }
    if ((this.cachedTrack != null) && (this.ownsTrack))
      this.cachedTrack.dispose();
    this.cachedTrack = paramMediaStreamTrack;
    this.ownsTrack = paramBoolean;
    return true;
  }

  public MediaStreamTrack track()
  {
    return this.cachedTrack;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.RtpSender
 * JD-Core Version:    0.6.0
 */