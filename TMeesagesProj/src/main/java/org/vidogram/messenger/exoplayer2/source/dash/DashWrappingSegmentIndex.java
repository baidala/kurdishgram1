package org.vidogram.messenger.exoplayer2.source.dash;

import org.vidogram.messenger.exoplayer2.extractor.ChunkIndex;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.RangedUri;

final class DashWrappingSegmentIndex
  implements DashSegmentIndex
{
  private final ChunkIndex chunkIndex;

  public DashWrappingSegmentIndex(ChunkIndex paramChunkIndex, String paramString)
  {
    this.chunkIndex = paramChunkIndex;
  }

  public long getDurationUs(int paramInt, long paramLong)
  {
    return this.chunkIndex.durationsUs[paramInt];
  }

  public int getFirstSegmentNum()
  {
    return 0;
  }

  public int getLastSegmentNum(long paramLong)
  {
    return this.chunkIndex.length - 1;
  }

  public int getSegmentNum(long paramLong1, long paramLong2)
  {
    return this.chunkIndex.getChunkIndex(paramLong1);
  }

  public RangedUri getSegmentUrl(int paramInt)
  {
    return new RangedUri(null, this.chunkIndex.offsets[paramInt], this.chunkIndex.sizes[paramInt]);
  }

  public long getTimeUs(int paramInt)
  {
    return this.chunkIndex.timesUs[paramInt];
  }

  public boolean isExplicit()
  {
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.DashWrappingSegmentIndex
 * JD-Core Version:    0.6.0
 */