package org.vidogram.messenger.exoplayer2.extractor.ogg;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

final class OpusReader extends StreamReader
{
  private static final int DEFAULT_SEEK_PRE_ROLL_SAMPLES = 3840;
  private static final int OPUS_CODE = Util.getIntegerCodeForString("Opus");
  private static final byte[] OPUS_SIGNATURE = { 79, 112, 117, 115, 72, 101, 97, 100 };
  private static final int SAMPLE_RATE = 48000;
  private boolean headerRead;

  private long getPacketDurationUs(byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte[0] & 0xFF;
    int j;
    int k;
    switch (i & 0x3)
    {
    default:
      j = paramArrayOfByte[1] & 0x3F;
      i >>= 3;
      k = i & 0x3;
      if (i < 16)
        break;
      i = 2500 << k;
    case 0:
    case 1:
    case 2:
    }
    while (true)
    {
      return i * j;
      j = 1;
      break;
      j = 2;
      break;
      if (i >= 12)
      {
        i = 10000 << (k & 0x1);
        continue;
      }
      if (k == 3)
      {
        i = 60000;
        continue;
      }
      i = 10000 << k;
    }
  }

  private void putNativeOrderLong(List<byte[]> paramList, int paramInt)
  {
    long l = paramInt * 1000000000L / 48000L;
    paramList.add(ByteBuffer.allocate(8).order(ByteOrder.nativeOrder()).putLong(l).array());
  }

  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() < OPUS_SIGNATURE.length)
      return false;
    byte[] arrayOfByte = new byte[OPUS_SIGNATURE.length];
    paramParsableByteArray.readBytes(arrayOfByte, 0, OPUS_SIGNATURE.length);
    return Arrays.equals(arrayOfByte, OPUS_SIGNATURE);
  }

  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    return convertTimeToGranule(getPacketDurationUs(paramParsableByteArray.data));
  }

  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
  {
    if (!this.headerRead)
    {
      paramParsableByteArray = Arrays.copyOf(paramParsableByteArray.data, paramParsableByteArray.limit());
      int i = paramParsableByteArray[9];
      int j = paramParsableByteArray[11];
      int k = paramParsableByteArray[10];
      ArrayList localArrayList = new ArrayList(3);
      localArrayList.add(paramParsableByteArray);
      putNativeOrderLong(localArrayList, (j & 0xFF) << 8 | k & 0xFF);
      putNativeOrderLong(localArrayList, 3840);
      paramSetupData.format = Format.createAudioSampleFormat(null, "audio/opus", null, -1, -1, i & 0xFF, 48000, localArrayList, null, 0, null);
      this.headerRead = true;
      return true;
    }
    if (paramParsableByteArray.readInt() == OPUS_CODE);
    for (int m = 1; ; m = 0)
    {
      paramParsableByteArray.setPosition(0);
      return m;
    }
  }

  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean)
      this.headerRead = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.OpusReader
 * JD-Core Version:    0.6.0
 */