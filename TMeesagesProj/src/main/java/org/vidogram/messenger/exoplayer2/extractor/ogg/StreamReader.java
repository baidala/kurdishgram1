package org.vidogram.messenger.exoplayer2.extractor.ogg;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

abstract class StreamReader
{
  private static final int STATE_END_OF_INPUT = 3;
  private static final int STATE_READ_HEADERS = 0;
  private static final int STATE_READ_PAYLOAD = 2;
  private static final int STATE_SKIP_HEADERS = 1;
  private long currentGranule;
  private ExtractorOutput extractorOutput;
  private boolean formatSet;
  private long lengthOfReadPacket;
  private OggPacket oggPacket;
  private OggSeeker oggSeeker;
  private long payloadStartPosition;
  private int sampleRate;
  private boolean seekMapSet;
  private SetupData setupData;
  private int state;
  private long targetGranule;
  private TrackOutput trackOutput;

  private int readHeaders(ExtractorInput paramExtractorInput)
  {
    int j = 1;
    while (j != 0)
    {
      if (!this.oggPacket.populate(paramExtractorInput))
      {
        this.state = 3;
        return -1;
      }
      this.lengthOfReadPacket = (paramExtractorInput.getPosition() - this.payloadStartPosition);
      boolean bool = readHeaders(this.oggPacket.getPayload(), this.payloadStartPosition, this.setupData);
      j = bool;
      if (!bool)
        continue;
      this.payloadStartPosition = paramExtractorInput.getPosition();
      j = bool;
    }
    this.sampleRate = this.setupData.format.sampleRate;
    if (!this.formatSet)
    {
      this.trackOutput.format(this.setupData.format);
      this.formatSet = true;
    }
    if (this.setupData.oggSeeker != null)
      this.oggSeeker = this.setupData.oggSeeker;
    while (true)
    {
      this.setupData = null;
      this.state = 2;
      return 0;
      if (paramExtractorInput.getLength() == -1L)
      {
        this.oggSeeker = new UnseekableOggSeeker(null);
        continue;
      }
      OggPageHeader localOggPageHeader = this.oggPacket.getPageHeader();
      long l1 = this.payloadStartPosition;
      long l2 = paramExtractorInput.getLength();
      int i = localOggPageHeader.headerSize;
      this.oggSeeker = new DefaultOggSeeker(l1, l2, this, localOggPageHeader.bodySize + i, localOggPageHeader.granulePosition);
    }
  }

  private int readPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    long l1 = this.oggSeeker.read(paramExtractorInput);
    if (l1 >= 0L)
    {
      paramPositionHolder.position = l1;
      return 1;
    }
    if (l1 < -1L)
      onSeekEnd(-l1 - 2L);
    if (!this.seekMapSet)
    {
      paramPositionHolder = this.oggSeeker.createSeekMap();
      this.extractorOutput.seekMap(paramPositionHolder);
      this.seekMapSet = true;
    }
    if ((this.lengthOfReadPacket > 0L) || (this.oggPacket.populate(paramExtractorInput)))
    {
      this.lengthOfReadPacket = 0L;
      paramExtractorInput = this.oggPacket.getPayload();
      l1 = preparePayload(paramExtractorInput);
      if ((l1 >= 0L) && (this.currentGranule + l1 >= this.targetGranule))
      {
        long l2 = convertGranuleToTime(this.currentGranule);
        this.trackOutput.sampleData(paramExtractorInput, paramExtractorInput.limit());
        this.trackOutput.sampleMetadata(l2, 1, paramExtractorInput.limit(), 0, null);
        this.targetGranule = -1L;
      }
      this.currentGranule += l1;
      return 0;
    }
    this.state = 3;
    return -1;
  }

  protected long convertGranuleToTime(long paramLong)
  {
    return 1000000L * paramLong / this.sampleRate;
  }

  protected long convertTimeToGranule(long paramLong)
  {
    return this.sampleRate * paramLong / 1000000L;
  }

  void init(ExtractorOutput paramExtractorOutput, TrackOutput paramTrackOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramTrackOutput;
    this.oggPacket = new OggPacket();
    reset(true);
  }

  protected void onSeekEnd(long paramLong)
  {
    this.currentGranule = paramLong;
  }

  protected abstract long preparePayload(ParsableByteArray paramParsableByteArray);

  final int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    switch (this.state)
    {
    default:
      throw new IllegalStateException();
    case 0:
      return readHeaders(paramExtractorInput);
    case 1:
      paramExtractorInput.skipFully((int)this.payloadStartPosition);
      this.state = 2;
      return 0;
    case 2:
    }
    return readPayload(paramExtractorInput, paramPositionHolder);
  }

  protected abstract boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, SetupData paramSetupData);

  protected void reset(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.setupData = new SetupData();
      this.payloadStartPosition = 0L;
    }
    for (this.state = 0; ; this.state = 1)
    {
      this.targetGranule = -1L;
      this.currentGranule = 0L;
      return;
    }
  }

  final void seek(long paramLong)
  {
    this.oggPacket.reset();
    boolean bool;
    if (paramLong == 0L)
      if (!this.seekMapSet)
      {
        bool = true;
        reset(bool);
      }
    do
    {
      return;
      bool = false;
      break;
    }
    while (this.state == 0);
    this.targetGranule = this.oggSeeker.startSeek();
    this.state = 2;
  }

  static class SetupData
  {
    Format format;
    OggSeeker oggSeeker;
  }

  private static final class UnseekableOggSeeker
    implements OggSeeker
  {
    public SeekMap createSeekMap()
    {
      return new SeekMap.Unseekable(-9223372036854775807L);
    }

    public long read(ExtractorInput paramExtractorInput)
    {
      return -1L;
    }

    public long startSeek()
    {
      return 0L;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.StreamReader
 * JD-Core Version:    0.6.0
 */