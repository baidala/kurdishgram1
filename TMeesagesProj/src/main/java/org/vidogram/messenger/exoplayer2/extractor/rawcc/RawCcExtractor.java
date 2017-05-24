package org.vidogram.messenger.exoplayer2.extractor.rawcc;

import java.io.IOException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class RawCcExtractor
  implements Extractor
{
  private static final int HEADER_ID = Util.getIntegerCodeForString("RCC\001");
  private static final int HEADER_SIZE = 8;
  private static final int SCRATCH_SIZE = 9;
  private static final int STATE_READING_HEADER = 0;
  private static final int STATE_READING_SAMPLES = 2;
  private static final int STATE_READING_TIMESTAMP_AND_COUNT = 1;
  private static final int TIMESTAMP_SIZE_V0 = 4;
  private static final int TIMESTAMP_SIZE_V1 = 8;
  private final ParsableByteArray dataScratch;
  private final Format format;
  private int parserState;
  private int remainingSampleCount;
  private int sampleBytesWritten;
  private long timestampUs;
  private TrackOutput trackOutput;
  private int version;

  public RawCcExtractor(Format paramFormat)
  {
    this.format = paramFormat;
    this.dataScratch = new ParsableByteArray(9);
    this.parserState = 0;
  }

  private void parseHeader(ExtractorInput paramExtractorInput)
  {
    this.dataScratch.reset();
    paramExtractorInput.readFully(this.dataScratch.data, 0, 8);
    if (this.dataScratch.readInt() != HEADER_ID)
      throw new IOException("Input not RawCC");
    this.version = this.dataScratch.readUnsignedByte();
  }

  private void parseSamples(ExtractorInput paramExtractorInput)
  {
    while (this.remainingSampleCount > 0)
    {
      this.dataScratch.reset();
      paramExtractorInput.readFully(this.dataScratch.data, 0, 3);
      this.trackOutput.sampleData(this.dataScratch, 3);
      this.sampleBytesWritten += 3;
      this.remainingSampleCount -= 1;
    }
    if (this.sampleBytesWritten > 0)
      this.trackOutput.sampleMetadata(this.timestampUs, 1, this.sampleBytesWritten, 0, null);
  }

  private boolean parseTimestampAndSampleCount(ExtractorInput paramExtractorInput)
  {
    this.dataScratch.reset();
    if (this.version == 0)
      if (!paramExtractorInput.readFully(this.dataScratch.data, 0, 5, true))
        return false;
    for (this.timestampUs = (this.dataScratch.readUnsignedInt() * 1000L / 45L); ; this.timestampUs = this.dataScratch.readLong())
    {
      this.remainingSampleCount = this.dataScratch.readUnsignedByte();
      this.sampleBytesWritten = 0;
      return true;
      if (this.version != 1)
        break label114;
      if (!paramExtractorInput.readFully(this.dataScratch.data, 0, 9, true))
        break;
    }
    label114: throw new ParserException("Unsupported version number: " + this.version);
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
    this.trackOutput = paramExtractorOutput.track(0);
    paramExtractorOutput.endTracks();
    this.trackOutput.format(this.format);
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    while (true)
      switch (this.parserState)
      {
      default:
        throw new IllegalStateException();
      case 0:
        parseHeader(paramExtractorInput);
        this.parserState = 1;
        break;
      case 1:
        if (!parseTimestampAndSampleCount(paramExtractorInput))
          break label69;
        this.parserState = 2;
      case 2:
      }
    label69: this.parserState = 0;
    return -1;
    parseSamples(paramExtractorInput);
    this.parserState = 1;
    return 0;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.parserState = 0;
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    int i = 0;
    this.dataScratch.reset();
    paramExtractorInput.peekFully(this.dataScratch.data, 0, 8);
    if (this.dataScratch.readInt() == HEADER_ID)
      i = 1;
    return i;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.rawcc.RawCcExtractor
 * JD-Core Version:    0.6.0
 */