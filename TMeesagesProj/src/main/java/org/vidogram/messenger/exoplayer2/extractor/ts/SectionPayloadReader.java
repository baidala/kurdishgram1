package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface SectionPayloadReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray);

  public abstract void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.SectionPayloadReader
 * JD-Core Version:    0.6.0
 */