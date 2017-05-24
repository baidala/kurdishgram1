package org.vidogram.messenger.exoplayer2.extractor;

import org.vidogram.messenger.exoplayer2.util.Util;

public final class ChunkIndex
  implements SeekMap
{
  private final long durationUs;
  public final long[] durationsUs;
  public final int length;
  public final long[] offsets;
  public final int[] sizes;
  public final long[] timesUs;

  public ChunkIndex(int[] paramArrayOfInt, long[] paramArrayOfLong1, long[] paramArrayOfLong2, long[] paramArrayOfLong3)
  {
    this.sizes = paramArrayOfInt;
    this.offsets = paramArrayOfLong1;
    this.durationsUs = paramArrayOfLong2;
    this.timesUs = paramArrayOfLong3;
    this.length = paramArrayOfInt.length;
    this.durationUs = (paramArrayOfLong2[(this.length - 1)] + paramArrayOfLong3[(this.length - 1)]);
  }

  public int getChunkIndex(long paramLong)
  {
    return Util.binarySearchFloor(this.timesUs, paramLong, true, true);
  }

  public long getDurationUs()
  {
    return this.durationUs;
  }

  public long getPosition(long paramLong)
  {
    return this.offsets[getChunkIndex(paramLong)];
  }

  public boolean isSeekable()
  {
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ChunkIndex
 * JD-Core Version:    0.6.0
 */