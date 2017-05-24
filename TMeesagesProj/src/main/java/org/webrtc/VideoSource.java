package org.webrtc;

public class VideoSource extends MediaSource
{
  public VideoSource(long paramLong)
  {
    super(paramLong);
  }

  private static native void restart(long paramLong);

  private static native void stop(long paramLong);

  public void restart()
  {
    restart(this.nativeSource);
  }

  public void stop()
  {
    stop(this.nativeSource);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoSource
 * JD-Core Version:    0.6.0
 */