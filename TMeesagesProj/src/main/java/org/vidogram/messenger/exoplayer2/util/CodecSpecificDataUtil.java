package org.vidogram.messenger.exoplayer2.util;

import android.util.Pair;
import java.util.ArrayList;
import java.util.List;

public final class CodecSpecificDataUtil
{
  private static final int AUDIO_OBJECT_TYPE_AAC_LC = 2;
  private static final int AUDIO_OBJECT_TYPE_ER_BSAC = 22;
  private static final int AUDIO_OBJECT_TYPE_PS = 29;
  private static final int AUDIO_OBJECT_TYPE_SBR = 5;
  private static final int AUDIO_SPECIFIC_CONFIG_CHANNEL_CONFIGURATION_INVALID = -1;
  private static final int[] AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE;
  private static final int AUDIO_SPECIFIC_CONFIG_FREQUENCY_INDEX_ARBITRARY = 15;
  private static final int[] AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE;
  private static final byte[] NAL_START_CODE = { 0, 0, 0, 1 };

  static
  {
    AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE = new int[] { 96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050, 16000, 12000, 11025, 8000, 7350 };
    AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE = new int[] { 0, 1, 2, 3, 4, 5, 6, 8, -1, -1, -1, 7, 8, -1, 8, -1 };
  }

  public static byte[] buildAacAudioSpecificConfig(int paramInt1, int paramInt2, int paramInt3)
  {
    return new byte[] { (byte)(paramInt1 << 3 & 0xF8 | paramInt2 >> 1 & 0x7), (byte)(paramInt2 << 7 & 0x80 | paramInt3 << 3 & 0x78) };
  }

  public static byte[] buildAacLcAudioSpecificConfig(int paramInt1, int paramInt2)
  {
    int k = 0;
    int i = 0;
    int j = -1;
    while (i < AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE.length)
    {
      if (paramInt1 == AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i])
        j = i;
      i += 1;
    }
    int m = -1;
    i = k;
    k = m;
    while (i < AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE.length)
    {
      if (paramInt2 == AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[i])
        k = i;
      i += 1;
    }
    if ((paramInt1 == -1) || (k == -1))
      throw new IllegalArgumentException("Invalid sample rate or number of channels: " + paramInt1 + ", " + paramInt2);
    return buildAacAudioSpecificConfig(2, j, k);
  }

  public static byte[] buildNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[NAL_START_CODE.length + paramInt2];
    System.arraycopy(NAL_START_CODE, 0, arrayOfByte, 0, NAL_START_CODE.length);
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, NAL_START_CODE.length, paramInt2);
    return arrayOfByte;
  }

  private static int findNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    int i = paramArrayOfByte.length;
    int j = NAL_START_CODE.length;
    while (paramInt <= i - j)
    {
      if (isNalStartCode(paramArrayOfByte, paramInt))
        return paramInt;
      paramInt += 1;
    }
    return -1;
  }

  private static boolean isNalStartCode(byte[] paramArrayOfByte, int paramInt)
  {
    if (paramArrayOfByte.length - paramInt <= NAL_START_CODE.length)
      return false;
    int i = 0;
    while (true)
    {
      if (i >= NAL_START_CODE.length)
        break label43;
      if (paramArrayOfByte[(paramInt + i)] != NAL_START_CODE[i])
        break;
      i += 1;
    }
    label43: return true;
  }

  public static Pair<Integer, Integer> parseAacAudioSpecificConfig(byte[] paramArrayOfByte)
  {
    boolean bool2 = true;
    paramArrayOfByte = new ParsableBitArray(paramArrayOfByte);
    int i = paramArrayOfByte.readBits(5);
    int j = paramArrayOfByte.readBits(4);
    int k;
    if (j == 15)
    {
      j = paramArrayOfByte.readBits(24);
      k = paramArrayOfByte.readBits(4);
      if ((i != 5) && (i != 29))
        break label187;
      i = paramArrayOfByte.readBits(4);
      if (i != 15)
        break label152;
      i = paramArrayOfByte.readBits(24);
      j = i;
      if (paramArrayOfByte.readBits(5) != 22)
        break label187;
    }
    for (j = paramArrayOfByte.readBits(4); ; j = k)
    {
      j = AUDIO_SPECIFIC_CONFIG_CHANNEL_COUNT_TABLE[j];
      if (j != -1);
      for (boolean bool1 = bool2; ; bool1 = false)
      {
        Assertions.checkArgument(bool1);
        return Pair.create(Integer.valueOf(i), Integer.valueOf(j));
        if (j < 13);
        for (bool1 = true; ; bool1 = false)
        {
          Assertions.checkArgument(bool1);
          j = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[j];
          break;
        }
        label152: if (i < 13);
        for (bool1 = true; ; bool1 = false)
        {
          Assertions.checkArgument(bool1);
          i = AUDIO_SPECIFIC_CONFIG_SAMPLING_RATE_TABLE[i];
          break;
        }
      }
      label187: i = j;
    }
  }

  public static byte[][] splitNalUnits(byte[] paramArrayOfByte)
  {
    if (!isNalStartCode(paramArrayOfByte, 0))
      return (byte[][])null;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    int j;
    do
    {
      localArrayList.add(Integer.valueOf(i));
      j = findNalStartCode(paramArrayOfByte, i + NAL_START_CODE.length);
      i = j;
    }
    while (j != -1);
    byte[][] arrayOfByte = new byte[localArrayList.size()][];
    i = 0;
    if (i < localArrayList.size())
    {
      int k = ((Integer)localArrayList.get(i)).intValue();
      if (i < localArrayList.size() - 1);
      for (j = ((Integer)localArrayList.get(i + 1)).intValue(); ; j = paramArrayOfByte.length)
      {
        byte[] arrayOfByte1 = new byte[j - k];
        System.arraycopy(paramArrayOfByte, k, arrayOfByte1, 0, arrayOfByte1.length);
        arrayOfByte[i] = arrayOfByte1;
        i += 1;
        break;
      }
    }
    return arrayOfByte;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.CodecSpecificDataUtil
 * JD-Core Version:    0.6.0
 */