package org.vidogram.messenger.exoplayer2.extractor.mp3;

import org.vidogram.messenger.exoplayer2.extractor.MpegAudioHeader;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

final class VbriSeeker
  implements Mp3Extractor.Seeker
{
  private final long durationUs;
  private final long[] positions;
  private final long[] timesUs;

  private VbriSeeker(long[] paramArrayOfLong1, long[] paramArrayOfLong2, long paramLong)
  {
    this.timesUs = paramArrayOfLong1;
    this.positions = paramArrayOfLong2;
    this.durationUs = paramLong;
  }

  public static VbriSeeker create(MpegAudioHeader paramMpegAudioHeader, ParsableByteArray paramParsableByteArray, long paramLong1, long paramLong2)
  {
    paramParsableByteArray.skipBytes(10);
    int i = paramParsableByteArray.readInt();
    if (i <= 0)
      return null;
    int j = paramMpegAudioHeader.sampleRate;
    long l1 = i;
    if (j >= 32000)
      i = 1152;
    long l2;
    long[] arrayOfLong;
    while (true)
    {
      l2 = Util.scaleLargeTimestamp(l1, i * 1000000L, j);
      int m = paramParsableByteArray.readUnsignedShort();
      int n = paramParsableByteArray.readUnsignedShort();
      int i1 = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(2);
      paramLong1 += paramMpegAudioHeader.frameSize;
      paramMpegAudioHeader = new long[m + 1];
      arrayOfLong = new long[m + 1];
      paramMpegAudioHeader[0] = 0L;
      arrayOfLong[0] = paramLong1;
      int k = 1;
      if (k >= paramMpegAudioHeader.length)
        break;
      switch (i1)
      {
      default:
        return null;
        i = 576;
        break;
      case 1:
        i = paramParsableByteArray.readUnsignedByte();
        paramLong1 += i * n;
        paramMpegAudioHeader[k] = (k * l2 / m);
        if (paramLong2 == -1L)
          l1 = paramLong1;
      case 2:
      case 3:
      case 4:
        while (true)
        {
          label172: arrayOfLong[k] = l1;
          k += 1;
          break;
          i = paramParsableByteArray.readUnsignedShort();
          break label172;
          i = paramParsableByteArray.readUnsignedInt24();
          break label172;
          i = paramParsableByteArray.readUnsignedIntToInt();
          break label172;
          l1 = Math.min(paramLong2, paramLong1);
        }
      }
    }
    return new VbriSeeker(paramMpegAudioHeader, arrayOfLong, l2);
  }

  public long getDurationUs()
  {
    return this.durationUs;
  }

  public long getPosition(long paramLong)
  {
    return this.positions[Util.binarySearchFloor(this.timesUs, paramLong, true, true)];
  }

  public long getTimeUs(long paramLong)
  {
    return this.timesUs[Util.binarySearchFloor(this.positions, paramLong, true, true)];
  }

  public boolean isSeekable()
  {
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp3.VbriSeeker
 * JD-Core Version:    0.6.0
 */