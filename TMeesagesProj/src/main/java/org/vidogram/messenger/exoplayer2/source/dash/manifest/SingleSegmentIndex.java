package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import org.vidogram.messenger.exoplayer2.source.dash.DashSegmentIndex;

final class SingleSegmentIndex
  implements DashSegmentIndex
{
  private final RangedUri uri;

  public SingleSegmentIndex(RangedUri paramRangedUri)
  {
    this.uri = paramRangedUri;
  }

  public long getDurationUs(int paramInt, long paramLong)
  {
    return paramLong;
  }

  public int getFirstSegmentNum()
  {
    return 0;
  }

  public int getLastSegmentNum(long paramLong)
  {
    return 0;
  }

  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return 0;
  }

  public RangedUri getSegmentUrl(int paramInt)
  {
    return this.uri;
  }

  public long getTimeUs(int paramInt)
  {
    return 0L;
  }

  public boolean isExplicit()
  {
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.SingleSegmentIndex
 * JD-Core Version:    0.6.0
 */