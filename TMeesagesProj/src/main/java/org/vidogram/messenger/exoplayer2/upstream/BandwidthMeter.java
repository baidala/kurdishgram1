package org.vidogram.messenger.exoplayer2.upstream;

public abstract interface BandwidthMeter
{
  public static final long NO_ESTIMATE = -1L;

  public abstract long getBitrateEstimate();

  public static abstract interface EventListener
  {
    public abstract void onBandwidthSample(int paramInt, long paramLong1, long paramLong2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.BandwidthMeter
 * JD-Core Version:    0.6.0
 */