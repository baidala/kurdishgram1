package org.vidogram.messenger.exoplayer2.extractor.mkv;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;

final class VarintReader
{
  private static final int STATE_BEGIN_READING = 0;
  private static final int STATE_READ_CONTENTS = 1;
  private static final long[] VARINT_LENGTH_MASKS = { 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L };
  private int length;
  private final byte[] scratch = new byte[8];
  private int state;

  public static long assembleVarint(byte[] paramArrayOfByte, int paramInt, boolean paramBoolean)
  {
    long l2 = paramArrayOfByte[0] & 0xFF;
    long l1 = l2;
    if (paramBoolean)
      l1 = l2 & (VARINT_LENGTH_MASKS[(paramInt - 1)] ^ 0xFFFFFFFF);
    int i = 1;
    while (i < paramInt)
    {
      l1 = l1 << 8 | paramArrayOfByte[i] & 0xFF;
      i += 1;
    }
    return l1;
  }

  public static int parseUnsignedVarintLength(int paramInt)
  {
    int i = 0;
    while (i < VARINT_LENGTH_MASKS.length)
    {
      if ((VARINT_LENGTH_MASKS[i] & paramInt) != 0L)
        return i + 1;
      i += 1;
    }
    return -1;
  }

  public int getLastLength()
  {
    return this.length;
  }

  public long readUnsignedVarint(ExtractorInput paramExtractorInput, boolean paramBoolean1, boolean paramBoolean2, int paramInt)
  {
    if (this.state == 0)
    {
      if (!paramExtractorInput.readFully(this.scratch, 0, 1, paramBoolean1))
        return -1L;
      this.length = parseUnsignedVarintLength(this.scratch[0] & 0xFF);
      if (this.length == -1)
        throw new IllegalStateException("No valid varint length mask found");
      this.state = 1;
    }
    if (this.length > paramInt)
    {
      this.state = 0;
      return -2L;
    }
    if (this.length != 1)
      paramExtractorInput.readFully(this.scratch, 1, this.length - 1);
    this.state = 0;
    return assembleVarint(this.scratch, this.length, paramBoolean2);
  }

  public void reset()
  {
    this.state = 0;
    this.length = 0;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mkv.VarintReader
 * JD-Core Version:    0.6.0
 */