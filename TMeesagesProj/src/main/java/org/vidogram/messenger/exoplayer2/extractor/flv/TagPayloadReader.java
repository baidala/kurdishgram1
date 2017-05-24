package org.vidogram.messenger.exoplayer2.extractor.flv;

import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

abstract class TagPayloadReader
{
  protected final TrackOutput output;

  protected TagPayloadReader(TrackOutput paramTrackOutput)
  {
    this.output = paramTrackOutput;
  }

  public final void consume(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    if (parseHeader(paramParsableByteArray))
      parsePayload(paramParsableByteArray, paramLong);
  }

  protected abstract boolean parseHeader(ParsableByteArray paramParsableByteArray);

  protected abstract void parsePayload(ParsableByteArray paramParsableByteArray, long paramLong);

  public abstract void seek();

  public static final class UnsupportedFormatException extends ParserException
  {
    public UnsupportedFormatException(String paramString)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.flv.TagPayloadReader
 * JD-Core Version:    0.6.0
 */