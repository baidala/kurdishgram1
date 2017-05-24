package org.vidogram.messenger.audioinfo.util;

import java.io.InputStream;

public class RangeInputStream extends PositionInputStream
{
  private final long endPosition;

  public RangeInputStream(InputStream paramInputStream, long paramLong1, long paramLong2)
  {
    super(paramInputStream, paramLong1);
    this.endPosition = (paramLong1 + paramLong2);
  }

  public long getRemainingLength()
  {
    return this.endPosition - getPosition();
  }

  public int read()
  {
    if (getPosition() == this.endPosition)
      return -1;
    return super.read();
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = paramInt2;
    if (getPosition() + paramInt2 > this.endPosition)
    {
      paramInt2 = (int)(this.endPosition - getPosition());
      i = paramInt2;
      if (paramInt2 == 0)
        return -1;
    }
    return super.read(paramArrayOfByte, paramInt1, i);
  }

  public long skip(long paramLong)
  {
    long l = paramLong;
    if (getPosition() + paramLong > this.endPosition)
      l = (int)(this.endPosition - getPosition());
    return super.skip(l);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.util.RangeInputStream
 * JD-Core Version:    0.6.0
 */