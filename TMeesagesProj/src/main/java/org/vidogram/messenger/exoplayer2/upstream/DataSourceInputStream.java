package org.vidogram.messenger.exoplayer2.upstream;

import java.io.InputStream;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class DataSourceInputStream extends InputStream
{
  private boolean closed = false;
  private final DataSource dataSource;
  private final DataSpec dataSpec;
  private boolean opened = false;
  private final byte[] singleByteArray;
  private long totalBytesRead;

  public DataSourceInputStream(DataSource paramDataSource, DataSpec paramDataSpec)
  {
    this.dataSource = paramDataSource;
    this.dataSpec = paramDataSpec;
    this.singleByteArray = new byte[1];
  }

  private void checkOpened()
  {
    if (!this.opened)
    {
      this.dataSource.open(this.dataSpec);
      this.opened = true;
    }
  }

  public long bytesRead()
  {
    return this.totalBytesRead;
  }

  public void close()
  {
    if (!this.closed)
    {
      this.dataSource.close();
      this.closed = true;
    }
  }

  public void open()
  {
    checkOpened();
  }

  public int read()
  {
    if (read(this.singleByteArray) == -1)
      return -1;
    this.totalBytesRead += 1L;
    return this.singleByteArray[0] & 0xFF;
  }

  public int read(byte[] paramArrayOfByte)
  {
    int i = read(paramArrayOfByte, 0, paramArrayOfByte.length);
    if (i != -1)
      this.totalBytesRead += i;
    return i;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (!this.closed);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      checkOpened();
      paramInt1 = this.dataSource.read(paramArrayOfByte, paramInt1, paramInt2);
      if (paramInt1 != -1)
        break;
      return -1;
    }
    this.totalBytesRead += paramInt1;
    return paramInt1;
  }

  public long skip(long paramLong)
  {
    if (!this.closed);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      checkOpened();
      paramLong = super.skip(paramLong);
      this.totalBytesRead += paramLong;
      return paramLong;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DataSourceInputStream
 * JD-Core Version:    0.6.0
 */