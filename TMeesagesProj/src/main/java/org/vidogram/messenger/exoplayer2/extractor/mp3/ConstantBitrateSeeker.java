package org.vidogram.messenger.exoplayer2.extractor.mp3;

final class ConstantBitrateSeeker
  implements Mp3Extractor.Seeker
{
  private static final int BITS_PER_BYTE = 8;
  private final int bitrate;
  private final long durationUs;
  private final long firstFramePosition;

  public ConstantBitrateSeeker(long paramLong1, int paramInt, long paramLong2)
  {
    this.firstFramePosition = paramLong1;
    this.bitrate = paramInt;
    if (paramLong2 == -1L)
      paramLong1 = -9223372036854775807L;
    while (true)
    {
      this.durationUs = paramLong1;
      return;
      paramLong1 = getTimeUs(paramLong2);
    }
  }

  public long getDurationUs()
  {
    return this.durationUs;
  }

  public long getPosition(long paramLong)
  {
    if (this.durationUs == -9223372036854775807L)
      return 0L;
    return this.firstFramePosition + this.bitrate * paramLong / 8000000L;
  }

  public long getTimeUs(long paramLong)
  {
    return Math.max(0L, paramLong - this.firstFramePosition) * 1000000L * 8L / this.bitrate;
  }

  public boolean isSeekable()
  {
    return this.durationUs != -9223372036854775807L;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp3.ConstantBitrateSeeker
 * JD-Core Version:    0.6.0
 */