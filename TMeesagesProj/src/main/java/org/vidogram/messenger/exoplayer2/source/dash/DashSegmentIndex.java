package org.vidogram.messenger.exoplayer2.source.dash;

import org.vidogram.messenger.exoplayer2.source.dash.manifest.RangedUri;

public abstract interface DashSegmentIndex
{
  public static final int INDEX_UNBOUNDED = -1;

  public abstract long getDurationUs(int paramInt, long paramLong);

  public abstract int getFirstSegmentNum();

  public abstract int getLastSegmentNum(long paramLong);

  public abstract int getSegmentNum(long paramLong1, long paramLong2);

  public abstract RangedUri getSegmentUrl(int paramInt);

  public abstract long getTimeUs(int paramInt);

  public abstract boolean isExplicit();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.DashSegmentIndex
 * JD-Core Version:    0.6.0
 */