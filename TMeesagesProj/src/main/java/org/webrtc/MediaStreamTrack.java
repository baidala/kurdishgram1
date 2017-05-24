package org.webrtc;

public class MediaStreamTrack
{
  final long nativeTrack;

  public MediaStreamTrack(long paramLong)
  {
    this.nativeTrack = paramLong;
  }

  private static native void free(long paramLong);

  private static native boolean nativeEnabled(long paramLong);

  private static native String nativeId(long paramLong);

  private static native String nativeKind(long paramLong);

  private static native boolean nativeSetEnabled(long paramLong, boolean paramBoolean);

  private static native State nativeState(long paramLong);

  public void dispose()
  {
    free(this.nativeTrack);
  }

  public boolean enabled()
  {
    return nativeEnabled(this.nativeTrack);
  }

  public String id()
  {
    return nativeId(this.nativeTrack);
  }

  public String kind()
  {
    return nativeKind(this.nativeTrack);
  }

  public boolean setEnabled(boolean paramBoolean)
  {
    return nativeSetEnabled(this.nativeTrack, paramBoolean);
  }

  public State state()
  {
    return nativeState(this.nativeTrack);
  }

  public static enum State
  {
    static
    {
      ENDED = new State("ENDED", 1);
      $VALUES = new State[] { LIVE, ENDED };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.MediaStreamTrack
 * JD-Core Version:    0.6.0
 */