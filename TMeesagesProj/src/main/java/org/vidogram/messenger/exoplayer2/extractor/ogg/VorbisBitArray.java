package org.vidogram.messenger.exoplayer2.extractor.ogg;

import org.vidogram.messenger.exoplayer2.util.Assertions;

final class VorbisBitArray
{
  private int bitOffset;
  private int byteOffset;
  public final byte[] data;
  private final int limit;

  public VorbisBitArray(byte[] paramArrayOfByte)
  {
    this(paramArrayOfByte, paramArrayOfByte.length);
  }

  public VorbisBitArray(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.limit = (paramInt * 8);
  }

  public int bitsLeft()
  {
    return this.limit - getPosition();
  }

  public int getPosition()
  {
    return this.byteOffset * 8 + this.bitOffset;
  }

  public int limit()
  {
    return this.limit;
  }

  public boolean readBit()
  {
    return readBits(1) == 1;
  }

  public int readBits(int paramInt)
  {
    if (getPosition() + paramInt <= this.limit);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      if (paramInt != 0)
        break;
      return 0;
    }
    int k;
    int m;
    int i;
    int j;
    if (this.bitOffset != 0)
    {
      k = Math.min(paramInt, 8 - this.bitOffset);
      m = 255 >>> 8 - k & this.data[this.byteOffset] >>> this.bitOffset;
      this.bitOffset += k;
      i = k;
      j = m;
      if (this.bitOffset == 8)
      {
        this.byteOffset += 1;
        this.bitOffset = 0;
        j = m;
        i = k;
      }
    }
    while (true)
    {
      if (paramInt - i > 7)
      {
        m = (paramInt - i) / 8;
        k = 0;
        while (k < m)
        {
          long l = j;
          byte[] arrayOfByte = this.data;
          j = this.byteOffset;
          this.byteOffset = (j + 1);
          j = (int)(l | (arrayOfByte[j] & 0xFF) << i);
          i += 8;
          k += 1;
        }
        k = j;
        j = i;
        i = k;
      }
      while (true)
      {
        k = i;
        if (paramInt > j)
        {
          paramInt -= j;
          k = i | (255 >>> 8 - paramInt & this.data[this.byteOffset]) << j;
          this.bitOffset += paramInt;
        }
        return k;
        k = i;
        i = j;
        j = k;
      }
      i = 0;
      j = 0;
    }
  }

  public void reset()
  {
    this.byteOffset = 0;
    this.bitOffset = 0;
  }

  public void setPosition(int paramInt)
  {
    if ((paramInt < this.limit) && (paramInt >= 0));
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.byteOffset = (paramInt / 8);
      this.bitOffset = (paramInt - this.byteOffset * 8);
      return;
    }
  }

  public void skipBits(int paramInt)
  {
    if (getPosition() + paramInt <= this.limit);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.byteOffset += paramInt / 8;
      this.bitOffset += paramInt % 8;
      if (this.bitOffset > 7)
      {
        this.byteOffset += 1;
        this.bitOffset -= 8;
      }
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.VorbisBitArray
 * JD-Core Version:    0.6.0
 */