package org.vidogram.messenger.exoplayer2.extractor.ogg;

import java.util.Arrays;
import java.util.Collections;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.util.FlacStreamInfo;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

final class FlacReader extends StreamReader
{
  private static final byte AUDIO_PACKET_TYPE = -1;
  private static final int FRAME_HEADER_SAMPLE_NUMBER_OFFSET = 4;
  private static final byte SEEKTABLE_PACKET_TYPE = 3;
  private FlacOggSeeker flacOggSeeker;
  private FlacStreamInfo streamInfo;

  private int getFlacFrameBlockSize(ParsableByteArray paramParsableByteArray)
  {
    int i = (paramParsableByteArray.data[2] & 0xFF) >> 4;
    switch (i)
    {
    default:
      return -1;
    case 1:
      return 192;
    case 2:
    case 3:
    case 4:
    case 5:
      return 576 << i - 2;
    case 6:
    case 7:
      paramParsableByteArray.skipBytes(4);
      paramParsableByteArray.readUtf8EncodedLong();
      if (i == 6);
      for (i = paramParsableByteArray.readUnsignedByte(); ; i = paramParsableByteArray.readUnsignedShort())
      {
        paramParsableByteArray.setPosition(0);
        return i + 1;
      }
    case 8:
    case 9:
    case 10:
    case 11:
    case 12:
    case 13:
    case 14:
    case 15:
    }
    return 256 << i - 8;
  }

  private static boolean isAudioPacket(byte[] paramArrayOfByte)
  {
    int i = 0;
    if (paramArrayOfByte[0] == -1)
      i = 1;
    return i;
  }

  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    return (paramParsableByteArray.bytesLeft() >= 5) && (paramParsableByteArray.readUnsignedByte() == 127) && (paramParsableByteArray.readUnsignedInt() == 1179402563L);
  }

  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    if (!isAudioPacket(paramParsableByteArray.data))
      return -1L;
    return getFlacFrameBlockSize(paramParsableByteArray);
  }

  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
  {
    int i = 0;
    byte[] arrayOfByte = paramParsableByteArray.data;
    if (this.streamInfo == null)
    {
      this.streamInfo = new FlacStreamInfo(arrayOfByte, 17);
      paramParsableByteArray = Arrays.copyOfRange(arrayOfByte, 9, paramParsableByteArray.limit());
      paramParsableByteArray[4] = -128;
      paramParsableByteArray = Collections.singletonList(paramParsableByteArray);
      paramSetupData.format = Format.createAudioSampleFormat(null, "audio/x-flac", null, -1, this.streamInfo.bitRate(), this.streamInfo.channels, this.streamInfo.sampleRate, paramParsableByteArray, null, 0, null);
      i = 1;
    }
    do
    {
      return i;
      if ((arrayOfByte[0] & 0x7F) == 3)
      {
        this.flacOggSeeker = new FlacOggSeeker(null);
        this.flacOggSeeker.parseSeekTable(paramParsableByteArray);
        break;
      }
      if (!isAudioPacket(arrayOfByte))
        break;
    }
    while (this.flacOggSeeker == null);
    this.flacOggSeeker.setFirstFrameOffset(paramLong);
    paramSetupData.oggSeeker = this.flacOggSeeker;
    return false;
  }

  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean)
    {
      this.streamInfo = null;
      this.flacOggSeeker = null;
    }
  }

  private class FlacOggSeeker
    implements SeekMap, OggSeeker
  {
    private static final int METADATA_LENGTH_OFFSET = 1;
    private static final int SEEK_POINT_SIZE = 18;
    private long currentGranule = -1L;
    private long firstFrameOffset = -1L;
    private long[] offsets;
    private volatile long queriedGranule;
    private long[] sampleNumbers;
    private volatile long seekedGranule;

    private FlacOggSeeker()
    {
    }

    public SeekMap createSeekMap()
    {
      return this;
    }

    public long getDurationUs()
    {
      return FlacReader.this.streamInfo.durationUs();
    }

    public long getPosition(long paramLong)
    {
      monitorenter;
      try
      {
        this.queriedGranule = FlacReader.this.convertTimeToGranule(paramLong);
        int i = Util.binarySearchFloor(this.sampleNumbers, this.queriedGranule, true, true);
        this.seekedGranule = this.sampleNumbers[i];
        paramLong = this.firstFrameOffset;
        long l = this.offsets[i];
        monitorexit;
        return l + paramLong;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }

    public boolean isSeekable()
    {
      return true;
    }

    public void parseSeekTable(ParsableByteArray paramParsableByteArray)
    {
      paramParsableByteArray.skipBytes(1);
      int j = paramParsableByteArray.readUnsignedInt24() / 18;
      this.sampleNumbers = new long[j];
      this.offsets = new long[j];
      int i = 0;
      while (i < j)
      {
        this.sampleNumbers[i] = paramParsableByteArray.readLong();
        this.offsets[i] = paramParsableByteArray.readLong();
        paramParsableByteArray.skipBytes(2);
        i += 1;
      }
    }

    public long read(ExtractorInput paramExtractorInput)
    {
      if (this.currentGranule >= 0L)
      {
        this.currentGranule = (-this.currentGranule - 2L);
        return this.currentGranule;
      }
      return -1L;
    }

    public void setFirstFrameOffset(long paramLong)
    {
      this.firstFrameOffset = paramLong;
    }

    public long startSeek()
    {
      monitorenter;
      try
      {
        this.currentGranule = this.seekedGranule;
        long l = this.queriedGranule;
        monitorexit;
        return l;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.FlacReader
 * JD-Core Version:    0.6.0
 */