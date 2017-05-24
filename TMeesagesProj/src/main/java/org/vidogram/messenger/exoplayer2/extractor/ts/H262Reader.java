package org.vidogram.messenger.exoplayer2.extractor.ts;

import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.NalUnitUtil;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class H262Reader
  implements ElementaryStreamReader
{
  private static final double[] FRAME_RATE_VALUES = { 23.976023976023978D, 24.0D, 25.0D, 29.970029970029969D, 30.0D, 50.0D, 59.940059940059939D, 60.0D };
  private static final int START_EXTENSION = 181;
  private static final int START_GROUP = 184;
  private static final int START_PICTURE = 0;
  private static final int START_SEQUENCE_HEADER = 179;
  private final CsdBuffer csdBuffer = new CsdBuffer(128);
  private boolean foundFirstFrameInGroup;
  private long frameDurationUs;
  private long framePosition;
  private long frameTimeUs;
  private boolean hasOutputFormat;
  private boolean isKeyframe;
  private TrackOutput output;
  private boolean pesPtsUsAvailable;
  private long pesTimeUs;
  private final boolean[] prefixFlags = new boolean[4];
  private long totalBytesWritten;

  private static Pair<Format, Long> parseCsdBuffer(CsdBuffer paramCsdBuffer)
  {
    byte[] arrayOfByte = Arrays.copyOf(paramCsdBuffer.data, paramCsdBuffer.length);
    int k = arrayOfByte[4];
    int i = arrayOfByte[5] & 0xFF;
    int j = arrayOfByte[6];
    k = i >> 4 | (k & 0xFF) << 4;
    i = (i & 0xF) << 8 | j & 0xFF;
    float f = 1.0F;
    switch ((arrayOfByte[7] & 0xF0) >> 4)
    {
    default:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      Format localFormat = Format.createVideoSampleFormat(null, "video/mpeg2", null, -1, -1, k, i, -1.0F, Collections.singletonList(arrayOfByte), -1, f, null);
      long l2 = 0L;
      i = (arrayOfByte[7] & 0xF) - 1;
      long l1 = l2;
      if (i >= 0)
      {
        l1 = l2;
        if (i < FRAME_RATE_VALUES.length)
        {
          double d2 = FRAME_RATE_VALUES[i];
          j = paramCsdBuffer.sequenceExtensionPosition;
          i = (arrayOfByte[(j + 9)] & 0x60) >> 5;
          j = arrayOfByte[(j + 9)] & 0x1F;
          double d1 = d2;
          if (i != j)
            d1 = d2 * ((i + 1.0D) / (j + 1));
          l1 = ()(1000000.0D / d1);
        }
      }
      return Pair.create(localFormat, Long.valueOf(l1));
      f = i * 4 / (k * 3);
      continue;
      f = i * 16 / (k * 9);
      continue;
      f = i * 121 / (k * 100);
    }
  }

  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.getPosition();
    int m = paramParsableByteArray.limit();
    byte[] arrayOfByte = paramParsableByteArray.data;
    this.totalBytesWritten += paramParsableByteArray.bytesLeft();
    this.output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
    int i = j;
    j = NalUnitUtil.findNalUnit(arrayOfByte, j, m, this.prefixFlags);
    if (j == m)
    {
      if (!this.hasOutputFormat)
        this.csdBuffer.onData(arrayOfByte, i, m);
      return;
    }
    int k = paramParsableByteArray.data[(j + 3)] & 0xFF;
    int n;
    if (!this.hasOutputFormat)
    {
      n = j - i;
      if (n > 0)
        this.csdBuffer.onData(arrayOfByte, i, j);
      if (n >= 0)
        break label308;
      i = -n;
      label137: if (this.csdBuffer.onStartCode(k, i))
      {
        Pair localPair = parseCsdBuffer(this.csdBuffer);
        this.output.format((Format)localPair.first);
        this.frameDurationUs = ((Long)localPair.second).longValue();
        this.hasOutputFormat = true;
      }
    }
    if ((this.hasOutputFormat) && ((k == 184) || (k == 0)))
    {
      n = m - j;
      if (this.foundFirstFrameInGroup)
        if (!this.isKeyframe)
          break label313;
    }
    label308: label313: for (i = 1; ; i = 0)
    {
      int i1 = (int)(this.totalBytesWritten - this.framePosition);
      this.output.sampleMetadata(this.frameTimeUs, i, i1 - n, n, null);
      this.isKeyframe = false;
      if (k != 184)
        break label318;
      this.foundFirstFrameInGroup = false;
      this.isKeyframe = true;
      k = j + 3;
      i = j;
      j = k;
      break;
      i = 0;
      break label137;
    }
    label318: long l;
    if (this.pesPtsUsAvailable)
      l = this.pesTimeUs;
    while (true)
    {
      this.frameTimeUs = l;
      this.framePosition = (this.totalBytesWritten - n);
      this.pesPtsUsAvailable = false;
      this.foundFirstFrameInGroup = true;
      break;
      l = this.frameTimeUs + this.frameDurationUs;
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
    if (paramLong != -9223372036854775807L);
    for (paramBoolean = true; ; paramBoolean = false)
    {
      this.pesPtsUsAvailable = paramBoolean;
      if (this.pesPtsUsAvailable)
        this.pesTimeUs = paramLong;
      return;
    }
  }

  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(this.prefixFlags);
    this.csdBuffer.reset();
    this.pesPtsUsAvailable = false;
    this.foundFirstFrameInGroup = false;
    this.totalBytesWritten = 0L;
  }

  private static final class CsdBuffer
  {
    public byte[] data;
    private boolean isFilling;
    public int length;
    public int sequenceExtensionPosition;

    public CsdBuffer(int paramInt)
    {
      this.data = new byte[paramInt];
    }

    public void onData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (!this.isFilling)
        return;
      paramInt2 -= paramInt1;
      if (this.data.length < this.length + paramInt2)
        this.data = Arrays.copyOf(this.data, (this.length + paramInt2) * 2);
      System.arraycopy(paramArrayOfByte, paramInt1, this.data, this.length, paramInt2);
      this.length = (paramInt2 + this.length);
    }

    public boolean onStartCode(int paramInt1, int paramInt2)
    {
      if (this.isFilling)
        if ((this.sequenceExtensionPosition == 0) && (paramInt1 == 181))
          this.sequenceExtensionPosition = this.length;
      while (true)
      {
        return false;
        this.length -= paramInt2;
        this.isFilling = false;
        return true;
        if (paramInt1 != 179)
          continue;
        this.isFilling = true;
      }
    }

    public void reset()
    {
      this.isFilling = false;
      this.length = 0;
      this.sequenceExtensionPosition = 0;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.H262Reader
 * JD-Core Version:    0.6.0
 */