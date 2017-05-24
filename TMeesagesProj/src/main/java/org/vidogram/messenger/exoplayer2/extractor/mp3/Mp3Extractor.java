package org.vidogram.messenger.exoplayer2.extractor.mp3;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.vidogram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class Mp3Extractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp3Extractor() };
    }
  };
  private static final int HEADER_MASK = -128000;
  private static final int INFO_HEADER;
  private static final int MAX_SNIFF_BYTES = 4096;
  private static final int MAX_SYNC_BYTES = 131072;
  private static final int SCRATCH_LENGTH = 10;
  private static final int VBRI_HEADER;
  private static final int XING_HEADER = Util.getIntegerCodeForString("Xing");
  private long basisTimeUs;
  private ExtractorOutput extractorOutput;
  private final long forcedFirstSampleTimestampUs;
  private final GaplessInfoHolder gaplessInfoHolder;
  private Metadata metadata;
  private int sampleBytesRemaining;
  private long samplesRead;
  private final ParsableByteArray scratch;
  private Seeker seeker;
  private final MpegAudioHeader synchronizedHeader;
  private int synchronizedHeaderData;
  private TrackOutput trackOutput;

  static
  {
    INFO_HEADER = Util.getIntegerCodeForString("Info");
    VBRI_HEADER = Util.getIntegerCodeForString("VBRI");
  }

  public Mp3Extractor()
  {
    this(-9223372036854775807L);
  }

  public Mp3Extractor(long paramLong)
  {
    this.forcedFirstSampleTimestampUs = paramLong;
    this.scratch = new ParsableByteArray(10);
    this.synchronizedHeader = new MpegAudioHeader();
    this.gaplessInfoHolder = new GaplessInfoHolder();
    this.basisTimeUs = -9223372036854775807L;
  }

  private void peekId3Data(ExtractorInput paramExtractorInput)
  {
    int i = 0;
    paramExtractorInput.peekFully(this.scratch.data, 0, 10);
    this.scratch.setPosition(0);
    if (this.scratch.readUnsignedInt24() != Id3Decoder.ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      return;
    }
    this.scratch.skipBytes(3);
    int j = this.scratch.readSynchSafeInt();
    int k = j + 10;
    if (this.metadata == null)
    {
      byte[] arrayOfByte = new byte[k];
      System.arraycopy(this.scratch.data, 0, arrayOfByte, 0, 10);
      paramExtractorInput.peekFully(arrayOfByte, 10, j);
      this.metadata = new Id3Decoder().decode(arrayOfByte, k);
      if (this.metadata != null)
        this.gaplessInfoHolder.setFromMetadata(this.metadata);
    }
    while (true)
    {
      i += k;
      break;
      paramExtractorInput.advancePeekPosition(j);
    }
  }

  private int readSample(ExtractorInput paramExtractorInput)
  {
    int i = 0;
    if (this.sampleBytesRemaining == 0)
    {
      paramExtractorInput.resetPeekPosition();
      if (!paramExtractorInput.peekFully(this.scratch.data, 0, 4, true))
        i = -1;
    }
    do
    {
      return i;
      this.scratch.setPosition(0);
      int j = this.scratch.readInt();
      if (((j & 0xFFFE0C00) != (this.synchronizedHeaderData & 0xFFFE0C00)) || (MpegAudioHeader.getFrameSize(j) == -1))
      {
        paramExtractorInput.skipFully(1);
        this.synchronizedHeaderData = 0;
        return 0;
      }
      MpegAudioHeader.populateHeader(j, this.synchronizedHeader);
      if (this.basisTimeUs == -9223372036854775807L)
      {
        this.basisTimeUs = this.seeker.getTimeUs(paramExtractorInput.getPosition());
        if (this.forcedFirstSampleTimestampUs != -9223372036854775807L)
        {
          l1 = this.seeker.getTimeUs(0L);
          l2 = this.basisTimeUs;
          this.basisTimeUs = (this.forcedFirstSampleTimestampUs - l1 + l2);
        }
      }
      this.sampleBytesRemaining = this.synchronizedHeader.frameSize;
      j = this.trackOutput.sampleData(paramExtractorInput, this.sampleBytesRemaining, true);
      if (j == -1)
        return -1;
      this.sampleBytesRemaining -= j;
    }
    while (this.sampleBytesRemaining > 0);
    long l1 = this.basisTimeUs;
    long l2 = this.samplesRead * 1000000L / this.synchronizedHeader.sampleRate;
    this.trackOutput.sampleMetadata(l2 + l1, 1, this.synchronizedHeader.frameSize, 0, null);
    this.samplesRead += this.synchronizedHeader.samplesPerFrame;
    this.sampleBytesRemaining = 0;
    return 0;
  }

  private Seeker setupSeeker(ExtractorInput paramExtractorInput)
  {
    int i = 21;
    Object localObject1 = new ParsableByteArray(this.synchronizedHeader.frameSize);
    paramExtractorInput.peekFully(((ParsableByteArray)localObject1).data, 0, this.synchronizedHeader.frameSize);
    long l1 = paramExtractorInput.getPosition();
    long l2 = paramExtractorInput.getLength();
    if ((this.synchronizedHeader.version & 0x1) != 0)
    {
      if (this.synchronizedHeader.channels != 1)
        i = 36;
      if (((ParsableByteArray)localObject1).limit() < i + 4)
        break label377;
      ((ParsableByteArray)localObject1).setPosition(i);
    }
    label377: for (int j = ((ParsableByteArray)localObject1).readInt(); ; j = 0)
    {
      if ((j == XING_HEADER) || (j == INFO_HEADER))
      {
        localObject1 = XingSeeker.create(this.synchronizedHeader, (ParsableByteArray)localObject1, l1, l2);
        if ((localObject1 != null) && (!this.gaplessInfoHolder.hasGaplessInfo()))
        {
          paramExtractorInput.resetPeekPosition();
          paramExtractorInput.advancePeekPosition(i + 141);
          paramExtractorInput.peekFully(this.scratch.data, 0, 3);
          this.scratch.setPosition(0);
          this.gaplessInfoHolder.setFromXingHeaderValue(this.scratch.readUnsignedInt24());
        }
        paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
      }
      while (true)
      {
        Object localObject2 = localObject1;
        if (localObject1 == null)
        {
          paramExtractorInput.resetPeekPosition();
          paramExtractorInput.peekFully(this.scratch.data, 0, 4);
          this.scratch.setPosition(0);
          MpegAudioHeader.populateHeader(this.scratch.readInt(), this.synchronizedHeader);
          localObject2 = new ConstantBitrateSeeker(paramExtractorInput.getPosition(), this.synchronizedHeader.bitrate, l2);
        }
        return localObject2;
        if (this.synchronizedHeader.channels != 1)
          break;
        i = 13;
        break;
        if (((ParsableByteArray)localObject1).limit() >= 40)
        {
          ((ParsableByteArray)localObject1).setPosition(36);
          if (((ParsableByteArray)localObject1).readInt() == VBRI_HEADER)
          {
            localObject1 = VbriSeeker.create(this.synchronizedHeader, (ParsableByteArray)localObject1, l1, l2);
            paramExtractorInput.skipFully(this.synchronizedHeader.frameSize);
            continue;
          }
        }
        localObject1 = null;
      }
    }
  }

  private boolean synchronize(ExtractorInput paramExtractorInput, boolean paramBoolean)
  {
    boolean bool2 = false;
    int m;
    int n;
    int i;
    int j;
    int k;
    if (paramBoolean)
    {
      m = 4096;
      paramExtractorInput.resetPeekPosition();
      if (paramExtractorInput.getPosition() != 0L)
        break label327;
      peekId3Data(paramExtractorInput);
      n = (int)paramExtractorInput.getPeekPosition();
      if (!paramBoolean)
        paramExtractorInput.skipFully(n);
      i = 0;
      j = 0;
      k = 0;
    }
    while (true)
    {
      label63: byte[] arrayOfByte = this.scratch.data;
      boolean bool1;
      if (j > 0)
      {
        bool1 = true;
        label80: if (paramExtractorInput.peekFully(arrayOfByte, 0, 4, bool1))
          break label134;
        label95: if (!paramBoolean)
          break label318;
        paramExtractorInput.skipFully(n + k);
      }
      while (true)
      {
        this.synchronizedHeaderData = i;
        bool1 = true;
        label134: int i2;
        int i3;
        do
        {
          return bool1;
          m = 131072;
          break;
          bool1 = false;
          break label80;
          this.scratch.setPosition(0);
          i2 = this.scratch.readInt();
          if ((i == 0) || ((i2 & 0xFFFE0C00) == (i & 0xFFFE0C00)))
          {
            i3 = MpegAudioHeader.getFrameSize(i2);
            if (i3 != -1)
              break label260;
          }
          i = k + 1;
          if (k != m)
            break label211;
          bool1 = bool2;
        }
        while (paramBoolean);
        throw new ParserException("Searched too many bytes.");
        label211: if (paramBoolean)
        {
          paramExtractorInput.resetPeekPosition();
          paramExtractorInput.advancePeekPosition(n + i);
          k = i;
          j = 0;
          i = 0;
          break label63;
        }
        paramExtractorInput.skipFully(1);
        k = i;
        j = 0;
        i = 0;
        break label63;
        label260: int i1 = j + 1;
        if (i1 == 1)
        {
          MpegAudioHeader.populateHeader(i2, this.synchronizedHeader);
          j = i2;
        }
        do
        {
          paramExtractorInput.advancePeekPosition(i3 - 4);
          i = j;
          j = i1;
          break;
          j = i;
        }
        while (i1 != 4);
        break label95;
        label318: paramExtractorInput.resetPeekPosition();
      }
      label327: k = 0;
      n = 0;
      i = 0;
      j = 0;
    }
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = this.extractorOutput.track(0);
    this.extractorOutput.endTracks();
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    if (this.synchronizedHeaderData == 0);
    try
    {
      synchronize(paramExtractorInput, false);
      if (this.seeker == null)
      {
        this.seeker = setupSeeker(paramExtractorInput);
        this.extractorOutput.seekMap(this.seeker);
        this.trackOutput.format(Format.createAudioSampleFormat(null, this.synchronizedHeader.mimeType, null, -1, 4096, this.synchronizedHeader.channels, this.synchronizedHeader.sampleRate, -1, this.gaplessInfoHolder.encoderDelay, this.gaplessInfoHolder.encoderPadding, null, null, 0, null, this.metadata));
      }
      return readSample(paramExtractorInput);
    }
    catch (java.io.EOFException paramExtractorInput)
    {
    }
    return -1;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.synchronizedHeaderData = 0;
    this.basisTimeUs = -9223372036854775807L;
    this.samplesRead = 0L;
    this.sampleBytesRemaining = 0;
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    return synchronize(paramExtractorInput, true);
  }

  static abstract interface Seeker extends SeekMap
  {
    public abstract long getTimeUs(long paramLong);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp3.Mp3Extractor
 * JD-Core Version:    0.6.0
 */