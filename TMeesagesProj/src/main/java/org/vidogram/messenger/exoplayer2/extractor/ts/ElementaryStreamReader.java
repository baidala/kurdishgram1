package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface ElementaryStreamReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray);

  public abstract void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator);

  public abstract void packetFinished();

  public abstract void packetStarted(long paramLong, boolean paramBoolean);

  public abstract void seek();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.ElementaryStreamReader
 * JD-Core Version:    0.6.0
 */