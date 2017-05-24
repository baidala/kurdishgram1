package org.vidogram.messenger.exoplayer2.util;

public final class ParsableBitArray
{
  private int bitOffset;
  private int byteLimit;
  private int byteOffset;
  public byte[] data;

  public ParsableBitArray()
  {
  }

  public ParsableBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }

  public ParsableBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteLimit = paramInt;
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

  public int bitsLeft()
  {
    return (this.byteLimit - this.byteOffset) * 8 - this.bitOffset;
  }

  public int getPosition()
  {
    return this.byteOffset * 8 + this.bitOffset;
  }

  public boolean readBit()
  {
    return readBits(1) == 1;
  }

  public int readBits(int paramInt)
  {
    if (paramInt == 0)
      return 0;
    int m = paramInt / 8;
    int i = 0;
    int k = 0;
    int j = paramInt;
    paramInt = k;
    if (i < m)
    {
      if (this.bitOffset != 0);
      for (k = (this.data[this.byteOffset] & 0xFF) << this.bitOffset | (this.data[(this.byteOffset + 1)] & 0xFF) >>> 8 - this.bitOffset; ; k = this.data[this.byteOffset])
      {
        j -= 8;
        paramInt |= (k & 0xFF) << j;
        this.byteOffset += 1;
        i += 1;
        break;
      }
    }
    if (j > 0)
    {
      k = this.bitOffset + j;
      i = (byte)(255 >> 8 - j);
      if (k > 8)
      {
        paramInt = i & ((this.data[this.byteOffset] & 0xFF) << k - 8 | (this.data[(this.byteOffset + 1)] & 0xFF) >> 16 - k) | paramInt;
        this.byteOffset += 1;
        this.bitOffset = (k % 8);
      }
    }
    while (true)
    {
      assertValidOffset();
      return paramInt;
      i = i & (this.data[this.byteOffset] & 0xFF) >> 8 - k | paramInt;
      paramInt = i;
      if (k != 8)
        break;
      this.byteOffset += 1;
      paramInt = i;
      break;
    }
  }

  public void reset(byte[] paramArrayOfByte)
  {
    reset(paramArrayOfByte, paramArrayOfByte.length);
  }

  public void reset(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.byteOffset = 0;
    this.bitOffset = 0;
    this.byteLimit = paramInt;
  }

  public void setPosition(int paramInt)
  {
    this.byteOffset = (paramInt / 8);
    this.bitOffset = (paramInt - this.byteOffset * 8);
    assertValidOffset();
  }

  public void skipBits(int paramInt)
  {
    this.byteOffset += paramInt / 8;
    this.bitOffset += paramInt % 8;
    if (this.bitOffset > 7)
    {
      this.byteOffset += 1;
      this.bitOffset -= 8;
    }
    assertValidOffset();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.ParsableBitArray
 * JD-Core Version:    0.6.0
 */