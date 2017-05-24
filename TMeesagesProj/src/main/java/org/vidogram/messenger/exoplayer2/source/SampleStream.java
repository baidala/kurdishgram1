package org.vidogram.messenger.exoplayer2.source;

import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public abstract interface SampleStream
{
  public abstract boolean isReady();

  public abstract void maybeThrowError();

  public abstract int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer);

  public abstract void skipToKeyframeBefore(long paramLong);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.SampleStream
 * JD-Core Version:    0.6.0
 */