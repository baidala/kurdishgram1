package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.audio.DtsUtil;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class DtsReader
  implements ElementaryStreamReader
{
  private static final int HEADER_SIZE = 15;
  private static final int STATE_FINDING_SYNC = 0;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_SAMPLE = 2;
  private static final int SYNC_VALUE = 2147385345;
  private static final int SYNC_VALUE_SIZE = 4;
  private int bytesRead;
  private Format format;
  private final ParsableByteArray headerScratchBytes = new ParsableByteArray(new byte[15]);
  private final String language;
  private TrackOutput output;
  private long sampleDurationUs;
  private int sampleSize;
  private int state;
  private int syncBytes;
  private long timeUs;

  public DtsReader(String paramString)
  {
    this.headerScratchBytes.data[0] = 127;
    this.headerScratchBytes.data[1] = -2;
    this.headerScratchBytes.data[2] = -128;
    this.headerScratchBytes.data[3] = 1;
    this.state = 0;
    this.language = paramString;
  }

  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    this.bytesRead = (i + this.bytesRead);
    return this.bytesRead == paramInt;
  }

  private void parseHeader()
  {
    byte[] arrayOfByte = this.headerScratchBytes.data;
    if (this.format == null)
    {
      this.format = DtsUtil.parseDtsFormat(arrayOfByte, null, this.language, null);
      this.output.format(this.format);
    }
    this.sampleSize = DtsUtil.getDtsFrameSize(arrayOfByte);
    this.sampleDurationUs = (int)(DtsUtil.parseDtsAudioSampleCount(arrayOfByte) * 1000000L / this.format.sampleRate);
  }

  private boolean skipToNextSync(ParsableByteArray paramParsableByteArray)
  {
    int j = 0;
    int i;
    while (true)
    {
      i = j;
      if (paramParsableByteArray.bytesLeft() <= 0)
        break;
      this.syncBytes <<= 8;
      this.syncBytes |= paramParsableByteArray.readUnsignedByte();
      if (this.syncBytes != 2147385345)
        continue;
      this.syncBytes = 0;
      i = 1;
    }
    return i;
  }

  public void consume(ParsableByteArray paramParsableByteArray)
  {
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      switch (this.state)
      {
      default:
        break;
      case 0:
        if (!skipToNextSync(paramParsableByteArray))
          continue;
        this.bytesRead = 4;
        this.state = 1;
        break;
      case 1:
        if (!continueRead(paramParsableByteArray, this.headerScratchBytes.data, 15))
          continue;
        parseHeader();
        this.headerScratchBytes.setPosition(0);
        this.output.sampleData(this.headerScratchBytes, 15);
        this.state = 2;
        break;
      case 2:
      }
      int i = Math.min(paramParsableByteArray.bytesLeft(), this.sampleSize - this.bytesRead);
      this.output.sampleData(paramParsableByteArray, i);
      this.bytesRead = (i + this.bytesRead);
      if (this.bytesRead != this.sampleSize)
        continue;
      this.output.sampleMetadata(this.timeUs, 1, this.sampleSize, 0, null);
      this.timeUs += this.sampleDurationUs;
      this.state = 0;
    }
  }

  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getNextId());
  }

  public void packetFinished()
  {
  }

  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.timeUs = paramLong;
  }

  public void seek()
  {
    this.state = 0;
    this.bytesRead = 0;
    this.syncBytes = 0;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.DtsReader
 * JD-Core Version:    0.6.0
 */