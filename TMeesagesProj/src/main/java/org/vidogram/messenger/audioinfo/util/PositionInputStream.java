package org.vidogram.messenger.audioinfo.util;

import java.io.FilterInputStream;
import java.io.InputStream;

public class PositionInputStream extends FilterInputStream
{
  private long position;
  private long positionMark;

  public PositionInputStream(InputStream paramInputStream)
  {
    this(paramInputStream, 0L);
  }

  public PositionInputStream(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream);
    this.position = paramLong;
  }

  public long getPosition()
  {
    return this.position;
  }

  public void mark(int paramInt)
  {
    monitorenter;
    try
    {
      this.positionMark = this.position;
      super.mark(paramInt);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public int read()
  {
    int i = super.read();
    if (i >= 0)
      this.position += 1L;
    return i;
  }

  public final int read(byte[] paramArrayOfByte)
  {
    return read(paramArrayOfByte, 0, paramArrayOfByte.length);
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    long l = this.position;
    paramInt1 = super.read(paramArrayOfByte, paramInt1, paramInt2);
    if (paramInt1 > 0)
      this.position = (l + paramInt1);
    return paramInt1;
  }

  public void reset()
  {
    monitorenter;
    try
    {
      super.reset();
      this.position = this.positionMark;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public long skip(long paramLong)
  {
    long l = this.position;
    paramLong = super.skip(paramLong);
    this.position = (l + paramLong);
    return paramLong;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.util.PositionInputStream
 * JD-Core Version:    0.6.0
 */