package org.vidogram.messenger.exoplayer2.source.hls;

import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.source.SampleStream;

final class HlsSampleStream
  implements SampleStream
{
  public final int group;
  private final HlsSampleStreamWrapper sampleStreamWrapper;

  public HlsSampleStream(HlsSampleStreamWrapper paramHlsSampleStreamWrapper, int paramInt)
  {
    this.sampleStreamWrapper = paramHlsSampleStreamWrapper;
    this.group = paramInt;
  }

  public boolean isReady()
  {
    return this.sampleStreamWrapper.isReady(this.group);
  }

  public void maybeThrowError()
  {
    this.sampleStreamWrapper.maybeThrowError();
  }

  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    return this.sampleStreamWrapper.readData(this.group, paramFormatHolder, paramDecoderInputBuffer);
  }

  public void skipToKeyframeBefore(long paramLong)
  {
    this.sampleStreamWrapper.skipToKeyframeBefore(this.group, paramLong);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.HlsSampleStream
 * JD-Core Version:    0.6.0
 */