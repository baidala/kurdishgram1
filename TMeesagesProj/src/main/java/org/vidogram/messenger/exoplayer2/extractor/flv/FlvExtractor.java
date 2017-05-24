package org.vidogram.messenger.exoplayer2.extractor.flv;

import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class FlvExtractor
  implements Extractor, SeekMap
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new FlvExtractor() };
    }
  };
  private static final int FLV_HEADER_SIZE = 9;
  private static final int FLV_TAG = Util.getIntegerCodeForString("FLV");
  private static final int FLV_TAG_HEADER_SIZE = 11;
  private static final int STATE_READING_FLV_HEADER = 1;
  private static final int STATE_READING_TAG_DATA = 4;
  private static final int STATE_READING_TAG_HEADER = 3;
  private static final int STATE_SKIPPING_TO_TAG_HEADER = 2;
  private static final int TAG_TYPE_AUDIO = 8;
  private static final int TAG_TYPE_SCRIPT_DATA = 18;
  private static final int TAG_TYPE_VIDEO = 9;
  private AudioTagPayloadReader audioReader;
  private int bytesToNextTagHeader;
  private ExtractorOutput extractorOutput;
  private final ParsableByteArray headerBuffer = new ParsableByteArray(9);
  private ScriptTagPayloadReader metadataReader;
  private int parserState = 1;
  private final ParsableByteArray scratch = new ParsableByteArray(4);
  private final ParsableByteArray tagData = new ParsableByteArray();
  public int tagDataSize;
  private final ParsableByteArray tagHeaderBuffer = new ParsableByteArray(11);
  public long tagTimestampUs;
  public int tagType;
  private VideoTagPayloadReader videoReader;

  private ParsableByteArray prepareTagData(ExtractorInput paramExtractorInput)
  {
    if (this.tagDataSize > this.tagData.capacity())
      this.tagData.reset(new byte[Math.max(this.tagData.capacity() * 2, this.tagDataSize)], 0);
    while (true)
    {
      this.tagData.setLimit(this.tagDataSize);
      paramExtractorInput.readFully(this.tagData.data, 0, this.tagDataSize);
      return this.tagData;
      this.tagData.setPosition(0);
    }
  }

  private boolean readFlvHeader(ExtractorInput paramExtractorInput)
  {
    int j = 0;
    if (!paramExtractorInput.readFully(this.headerBuffer.data, 0, 9, true))
      return false;
    this.headerBuffer.setPosition(0);
    this.headerBuffer.skipBytes(4);
    int k = this.headerBuffer.readUnsignedByte();
    if ((k & 0x4) != 0);
    for (int i = 1; ; i = 0)
    {
      if ((k & 0x1) != 0)
        j = 1;
      if ((i != 0) && (this.audioReader == null))
        this.audioReader = new AudioTagPayloadReader(this.extractorOutput.track(8));
      if ((j != 0) && (this.videoReader == null))
        this.videoReader = new VideoTagPayloadReader(this.extractorOutput.track(9));
      if (this.metadataReader == null)
        this.metadataReader = new ScriptTagPayloadReader(null);
      this.extractorOutput.endTracks();
      this.extractorOutput.seekMap(this);
      this.bytesToNextTagHeader = (this.headerBuffer.readInt() - 9 + 4);
      this.parserState = 2;
      return true;
    }
  }

  private boolean readTagData(ExtractorInput paramExtractorInput)
  {
    int i = 1;
    if ((this.tagType == 8) && (this.audioReader != null))
      this.audioReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
    while (true)
    {
      this.bytesToNextTagHeader = 4;
      this.parserState = 2;
      return i;
      if ((this.tagType == 9) && (this.videoReader != null))
      {
        this.videoReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
        continue;
      }
      if ((this.tagType == 18) && (this.metadataReader != null))
      {
        this.metadataReader.consume(prepareTagData(paramExtractorInput), this.tagTimestampUs);
        continue;
      }
      paramExtractorInput.skipFully(this.tagDataSize);
      i = 0;
    }
  }

  private boolean readTagHeader(ExtractorInput paramExtractorInput)
  {
    if (!paramExtractorInput.readFully(this.tagHeaderBuffer.data, 0, 11, true))
      return false;
    this.tagHeaderBuffer.setPosition(0);
    this.tagType = this.tagHeaderBuffer.readUnsignedByte();
    this.tagDataSize = this.tagHeaderBuffer.readUnsignedInt24();
    this.tagTimestampUs = this.tagHeaderBuffer.readUnsignedInt24();
    this.tagTimestampUs = ((this.tagHeaderBuffer.readUnsignedByte() << 24 | this.tagTimestampUs) * 1000L);
    this.tagHeaderBuffer.skipBytes(3);
    this.parserState = 4;
    return true;
  }

  private void skipToTagHeader(ExtractorInput paramExtractorInput)
  {
    paramExtractorInput.skipFully(this.bytesToNextTagHeader);
    this.bytesToNextTagHeader = 0;
    this.parserState = 3;
  }

  public long getDurationUs()
  {
    return this.metadataReader.getDurationUs();
  }

  public long getPosition(long paramLong)
  {
    return 0L;
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }

  public boolean isSeekable()
  {
    return false;
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    do
    {
      do
        while (true)
          switch (this.parserState)
          {
          default:
            break;
          case 1:
            if (readFlvHeader(paramExtractorInput))
              continue;
            return -1;
          case 2:
            skipToTagHeader(paramExtractorInput);
          case 3:
          case 4:
          }
      while (readTagHeader(paramExtractorInput));
      return -1;
    }
    while (!readTagData(paramExtractorInput));
    return 0;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.parserState = 1;
    this.bytesToNextTagHeader = 0;
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    paramExtractorInput.peekFully(this.scratch.data, 0, 3);
    this.scratch.setPosition(0);
    if (this.scratch.readUnsignedInt24() != FLV_TAG);
    do
    {
      do
      {
        return false;
        paramExtractorInput.peekFully(this.scratch.data, 0, 2);
        this.scratch.setPosition(0);
      }
      while ((this.scratch.readUnsignedShort() & 0xFA) != 0);
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      this.scratch.setPosition(0);
      int i = this.scratch.readInt();
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      paramExtractorInput.peekFully(this.scratch.data, 0, 4);
      this.scratch.setPosition(0);
    }
    while (this.scratch.readInt() != 0);
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.flv.FlvExtractor
 * JD-Core Version:    0.6.0
 */