package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceInfoSectionReader
  implements SectionPayloadReader
{
  private TrackOutput output;

  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.bytesLeft();
    this.output.sampleData(paramParsableByteArray, i);
    this.output.sampleMetadata(0L, 1, i, 0, null);
  }

  public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getNextId());
    this.output.format(Format.createSampleFormat(null, "application/x-scte35", null, -1, null));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.SpliceInfoSectionReader
 * JD-Core Version:    0.6.0
 */