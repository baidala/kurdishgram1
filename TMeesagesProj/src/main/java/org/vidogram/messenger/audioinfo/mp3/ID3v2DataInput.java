package org.vidogram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.InputStream;

public class ID3v2DataInput
{
  private final InputStream input;

  public ID3v2DataInput(InputStream paramInputStream)
  {
    this.input = paramInputStream;
  }

  public byte readByte()
  {
    int i = this.input.read();
    if (i < 0)
      throw new EOFException();
    return (byte)i;
  }

  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    while (i < paramInt2)
    {
      int j = this.input.read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j > 0)
      {
        i += j;
        continue;
      }
      throw new EOFException();
    }
  }

  public byte[] readFully(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    readFully(arrayOfByte, 0, paramInt);
    return arrayOfByte;
  }

  public int readInt()
  {
    return (readByte() & 0xFF) << 24 | (readByte() & 0xFF) << 16 | (readByte() & 0xFF) << 8 | readByte() & 0xFF;
  }

  public int readSyncsafeInt()
  {
    return (readByte() & 0x7F) << 21 | (readByte() & 0x7F) << 14 | (readByte() & 0x7F) << 7 | readByte() & 0x7F;
  }

  public void skipFully(long paramLong)
  {
    long l1 = 0L;
    while (l1 < paramLong)
    {
      long l2 = this.input.skip(paramLong - l1);
      if (l2 > 0L)
      {
        l1 += l2;
        continue;
      }
      throw new EOFException();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v2DataInput
 * JD-Core Version:    0.6.0
 */