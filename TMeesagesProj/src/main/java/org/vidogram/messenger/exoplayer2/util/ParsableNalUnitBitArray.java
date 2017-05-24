package org.vidogram.messenger.exoplayer2.util;

public final class ParsableNalUnitBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  private byte[] data;

  public ParsableNalUnitBitArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    reset(paramArrayOfByte, paramInt1, paramInt2);
  }

  private void assertValidOffset()
  {
    if ((this.byteOffset >= 0) && (this.bitOffset >= 0) && (this.bitOffset < 8) && ((this.byteOffset < this.byteLimit) || ((this.byteOffset == this.byteLimit) && (this.bitOffset == 0))));
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      return;
    }
  }

  private int readExpGolombCodeNum()
  {
    int j = 0;
    int i = 0;
    while (!readBit())
      i += 1;
    if (i > 0)
      j = readBits(i);
    return (1 << i) - 1 + j;
  }

  private boolean shouldSkipByte(int paramInt)
  {
    return (2 <= paramInt) && (paramInt < this.byteLimit) && (this.data[paramInt] == 3) && (this.data[(paramInt - 2)] == 0) && (this.data[(paramInt - 1)] == 0);
  }

  public boolean canReadBits(int paramInt)
  {
    int m = this.byteOffset;
    int i = this.byteOffset;
    int j = paramInt / 8 + i;
    int k = this.bitOffset + paramInt % 8;
    i = k;
    paramInt = j;
    if (k > 7)
    {
      paramInt = j + 1;
      i = k - 8;
    }
    j = m + 1;
    k = paramInt;
    paramInt = j;
    while ((paramInt <= k) && (k < this.byteLimit))
    {
      m = paramInt;
      j = k;
      if (shouldSkipByte(paramInt))
      {
        j = k + 1;
        m = paramInt + 2;
      }
      paramInt = m + 1;
      k = j;
    }
    return (k < this.byteLimit) || ((k == this.byteLimit) && (i == 0));
  }

  public boolean canReadExpGolombCodedNum()
  {
    int k = this.byteOffset;
    int m = this.bitOffset;
    int i = 0;
    while ((this.byteOffset < this.byteLimit) && (!readBit()))
      i += 1;
    if (this.byteOffset == this.byteLimit);
    for (int j = 1; ; j = 0)
    {
      this.byteOffset = k;
      this.bitOffset = m;
      if ((j != 0) || (!canReadBits(i * 2 + 1)))
        break;
      return true;
    }
    return false;
  }

  public boolean readBit()
  {
    return readBits(1) == 1;
  }

  public int readBits(int paramInt)
  {
    if (paramInt == 0)
      return 0;
    int n = paramInt / 8;
    int i = 0;
    int k = 0;
    int j = paramInt;
    paramInt = k;
    if (i < n)
    {
      if (shouldSkipByte(this.byteOffset + 1))
      {
        k = this.byteOffset + 2;
        label49: if (this.bitOffset == 0)
          break label136;
      }
      label136: for (int m = (this.data[this.byteOffset] & 0xFF) << this.bitOffset | (this.data[k] & 0xFF) >>> 8 - this.bitOffset; ; m = this.data[this.byteOffset])
      {
        j -= 8;
        paramInt |= (m & 0xFF) << j;
        this.byteOffset = k;
        i += 1;
        break;
        k = this.byteOffset + 1;
        break label49;
      }
    }
    if (j > 0)
    {
      k = this.bitOffset + j;
      j = (byte)(255 >> 8 - j);
      if (shouldSkipByte(this.byteOffset + 1))
      {
        i = this.byteOffset + 2;
        if (k <= 8)
          break label270;
        paramInt = ((this.data[this.byteOffset] & 0xFF) << k - 8 | (this.data[i] & 0xFF) >> 16 - k) & j | paramInt;
        this.byteOffset = i;
        label245: this.bitOffset = (k % 8);
      }
    }
    while (true)
    {
      assertValidOffset();
      return paramInt;
      i = this.byteOffset + 1;
      break;
      label270: j = (this.data[this.byteOffset] & 0xFF) >> 8 - k & j | paramInt;
      paramInt = j;
      if (k != 8)
        break label245;
      this.byteOffset = i;
      paramInt = j;
      break label245;
    }
  }

  public int readSignedExpGolombCodedInt()
  {
    int j = readExpGolombCodeNum();
    if (j % 2 == 0);
    for (int i = -1; ; i = 1)
      return i * ((j + 1) / 2);
  }

  public int readUnsignedExpGolombCodedInt()
  {
    return readExpGolombCodeNum();
  }

  public void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = paramInt1;
    this.byteLimit = paramInt2;
    this.bitOffset = 0;
    assertValidOffset();
  }

  public void skipBits(int paramInt)
  {
    int i = this.byteOffset;
    this.byteOffset += paramInt / 8;
    this.bitOffset += paramInt % 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    for (paramInt = i + 1; paramInt <= this.byteOffset; paramInt = i + 1)
    {
      i = paramInt;
      if (!shouldSkipByte(paramInt))
        continue;
      this.byteOffset += 1;
      i = paramInt + 2;
    }
    assertValidOffset();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.ParsableNalUnitBitArray
 * JD-Core Version:    0.6.0
 */