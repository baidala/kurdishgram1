package org.vidogram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.vidogram.messenger.exoplayer2.upstream.DataSink;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class CacheDataSink
  implements DataSink
{
  private final int bufferSize;
  private ReusableBufferedOutputStream bufferedOutputStream;
  private final Cache cache;
  private DataSpec dataSpec;
  private long dataSpecBytesWritten;
  private File file;
  private final long maxCacheFileSize;
  private OutputStream outputStream;
  private long outputStreamBytesWritten;
  private FileOutputStream underlyingFileOutputStream;

  public CacheDataSink(Cache paramCache, long paramLong)
  {
    this(paramCache, paramLong, 0);
  }

  public CacheDataSink(Cache paramCache, long paramLong, int paramInt)
  {
    this.cache = ((Cache)Assertions.checkNotNull(paramCache));
    this.maxCacheFileSize = paramLong;
    this.bufferSize = paramInt;
  }

  private void closeCurrentOutputStream()
  {
    if (this.outputStream == null)
      return;
    try
    {
      this.outputStream.flush();
      this.underlyingFileOutputStream.getFD().sync();
      Util.closeQuietly(this.outputStream);
      this.outputStream = null;
      File localFile1 = this.file;
      this.file = null;
      this.cache.commitFile(localFile1);
      return;
    }
    finally
    {
      Util.closeQuietly(this.outputStream);
      this.outputStream = null;
      File localFile2 = this.file;
      this.file = null;
      localFile2.delete();
    }
    throw localObject;
  }

  private void openNextOutputStream()
  {
    this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize));
    this.underlyingFileOutputStream = new FileOutputStream(this.file);
    if (this.bufferSize > 0)
      if (this.bufferedOutputStream == null)
        this.bufferedOutputStream = new ReusableBufferedOutputStream(this.underlyingFileOutputStream, this.bufferSize);
    for (this.outputStream = this.bufferedOutputStream; ; this.outputStream = this.underlyingFileOutputStream)
    {
      this.outputStreamBytesWritten = 0L;
      return;
      this.bufferedOutputStream.reset(this.underlyingFileOutputStream);
      break;
    }
  }

  public void close()
  {
    if ((this.dataSpec == null) || (this.dataSpec.length == -1L))
      return;
    try
    {
      closeCurrentOutputStream();
      return;
    }
    catch (IOException localIOException)
    {
    }
    throw new CacheDataSinkException(localIOException);
  }

  public void open(DataSpec paramDataSpec)
  {
    this.dataSpec = paramDataSpec;
    if (paramDataSpec.length == -1L)
      return;
    this.dataSpecBytesWritten = 0L;
    try
    {
      openNextOutputStream();
      return;
    }
    catch (IOException paramDataSpec)
    {
    }
    throw new CacheDataSinkException(paramDataSpec);
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.dataSpec.length == -1L)
      return;
    int i = 0;
    while (i < paramInt2)
      try
      {
        if (this.outputStreamBytesWritten == this.maxCacheFileSize)
        {
          closeCurrentOutputStream();
          openNextOutputStream();
        }
        int j = (int)Math.min(paramInt2 - i, this.maxCacheFileSize - this.outputStreamBytesWritten);
        this.outputStream.write(paramArrayOfByte, paramInt1 + i, j);
        i += j;
        this.outputStreamBytesWritten += j;
        this.dataSpecBytesWritten += j;
      }
      catch (IOException paramArrayOfByte)
      {
      }
    throw new CacheDataSinkException(paramArrayOfByte);
  }

  public static class CacheDataSinkException extends Cache.CacheException
  {
    public CacheDataSinkException(IOException paramIOException)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CacheDataSink
 * JD-Core Version:    0.6.0
 */