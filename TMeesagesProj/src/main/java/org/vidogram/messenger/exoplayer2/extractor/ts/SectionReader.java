package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.util.ParsableBitArray;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class SectionReader
  implements TsPayloadReader
{
  private static final int SECTION_HEADER_LENGTH = 3;
  private final ParsableBitArray headerScratch;
  private final SectionPayloadReader reader;
  private int sectionBytesRead;
  private final ParsableByteArray sectionData;
  private int sectionLength;

  public SectionReader(SectionPayloadReader paramSectionPayloadReader)
  {
    this.reader = paramSectionPayloadReader;
    this.sectionData = new ParsableByteArray();
    this.headerScratch = new ParsableBitArray(new byte[3]);
  }

  public void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedByte());
      paramParsableByteArray.readBytes(this.headerScratch, 3);
      paramParsableByteArray.setPosition(paramParsableByteArray.getPosition() - 3);
      this.headerScratch.skipBits(12);
      this.sectionLength = (this.headerScratch.readBits(12) + 3);
      this.sectionBytesRead = 0;
      this.sectionData.reset(this.sectionLength);
    }
    int i = Math.min(paramParsableByteArray.bytesLeft(), this.sectionLength - this.sectionBytesRead);
    paramParsableByteArray.readBytes(this.sectionData.data, this.sectionBytesRead, i);
    this.sectionBytesRead = (i + this.sectionBytesRead);
    if (this.sectionBytesRead < this.sectionLength);
    do
      return;
    while (Util.crc(this.sectionData.data, 0, this.sectionLength, -1) != 0);
    this.sectionData.setLimit(this.sectionData.limit() - 4);
    this.reader.consume(this.sectionData);
  }

  public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.reader.init(paramTimestampAdjuster, paramExtractorOutput, paramTrackIdGenerator);
  }

  public void seek()
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.SectionReader
 * JD-Core Version:    0.6.0
 */