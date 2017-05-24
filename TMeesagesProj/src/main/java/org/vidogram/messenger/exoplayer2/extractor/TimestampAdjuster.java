package org.vidogram.messenger.exoplayer2.extractor;

public final class TimestampAdjuster
{
  public static final long DO_NOT_OFFSET = 9223372036854775807L;
  private static final long MAX_PTS_PLUS_ONE = 8589934592L;
  private final long firstSampleTimestampUs;
  private volatile long lastSampleTimestamp;
  private long timestampOffsetUs;

  public TimestampAdjuster(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.lastSampleTimestamp = -9223372036854775807L;
  }

  public static long ptsToUs(long paramLong)
  {
    return 1000000L * paramLong / 90000L;
  }

  public static long usToPts(long paramLong)
  {
    return 90000L * paramLong / 1000000L;
  }

  public long adjustSampleTimestamp(long paramLong)
  {
    if (this.lastSampleTimestamp != -9223372036854775807L)
      this.lastSampleTimestamp = paramLong;
    while (true)
    {
      return this.timestampOffsetUs + paramLong;
      if (this.firstSampleTimestampUs != 9223372036854775807L)
        this.timestampOffsetUs = (this.firstSampleTimestampUs - paramLong);
      monitorenter;
      try
      {
        this.lastSampleTimestamp = paramLong;
        notifyAll();
        monitorexit;
        continue;
      }
      finally
      {
        monitorexit;
      }
    }
    throw localObject;
  }

  public long adjustTsTimestamp(long paramLong)
  {
    if (this.lastSampleTimestamp != -9223372036854775807L)
    {
      long l2 = usToPts(this.lastSampleTimestamp);
      long l3 = (4294967296L + l2) / 8589934592L;
      long l1 = (l3 - 1L) * 8589934592L + paramLong;
      paramLong = l3 * 8589934592L + paramLong;
      if (Math.abs(l1 - l2) < Math.abs(paramLong - l2))
        paramLong = l1;
    }
    while (true)
    {
      return adjustSampleTimestamp(ptsToUs(paramLong));
      continue;
    }
  }

  public void reset()
  {
    this.lastSampleTimestamp = -9223372036854775807L;
  }

  public void waitUntilInitialized()
  {
    monitorenter;
    try
    {
      if (this.lastSampleTimestamp == -9223372036854775807L)
        wait();
    }
    finally
    {
      monitorexit;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster
 * JD-Core Version:    0.6.0
 */