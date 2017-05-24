package org.webrtc;

public class MediaSource
{
  final long nativeSource;

  public MediaSource(long paramLong)
  {
    this.nativeSource = paramLong;
  }

  private static native void free(long paramLong);

  private static native State nativeState(long paramLong);

  public void dispose()
  {
    free(this.nativeSource);
  }

  public State state()
  {
    return nativeState(this.nativeSource);
  }

  public static enum State
  {
    static
    {
      ENDED = new State("ENDED", 2);
      MUTED = new State("MUTED", 3);
      $VALUES = new State[] { INITIALIZING, LIVE, ENDED, MUTED };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.MediaSource
 * JD-Core Version:    0.6.0
 */