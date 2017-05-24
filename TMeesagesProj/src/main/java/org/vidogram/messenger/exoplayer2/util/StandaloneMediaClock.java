package org.vidogram.messenger.exoplayer2.util;

import android.os.SystemClock;

public final class StandaloneMediaClock
  implements MediaClock
{
  private long deltaUs;
  private long positionUs;
  private boolean started;

  private long elapsedRealtimeMinus(long paramLong)
  {
    return SystemClock.elapsedRealtime() * 1000L - paramLong;
  }

  public long getPositionUs()
  {
    if (this.started)
      return elapsedRealtimeMinus(this.deltaUs);
    return this.positionUs;
  }

  public void setPositionUs(long paramLong)
  {
    this.positionUs = paramLong;
    this.deltaUs = elapsedRealtimeMinus(paramLong);
  }

  public void start()
  {
    if (!this.started)
    {
      this.started = true;
      this.deltaUs = elapsedRealtimeMinus(this.positionUs);
    }
  }

  public void stop()
  {
    if (this.started)
    {
      this.positionUs = elapsedRealtimeMinus(this.deltaUs);
      this.started = false;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.StandaloneMediaClock
 * JD-Core Version:    0.6.0
 */