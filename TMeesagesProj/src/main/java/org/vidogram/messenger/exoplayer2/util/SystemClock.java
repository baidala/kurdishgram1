package org.vidogram.messenger.exoplayer2.util;

public final class SystemClock
  implements Clock
{
  public long elapsedRealtime()
  {
    return android.os.SystemClock.elapsedRealtime();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.SystemClock
 * JD-Core Version:    0.6.0
 */