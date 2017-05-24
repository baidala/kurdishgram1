package org.vidogram.messenger.exoplayer2.upstream.cache;

import org.vidogram.messenger.exoplayer2.upstream.DataSink;
import org.vidogram.messenger.exoplayer2.upstream.DataSink.Factory;

public final class CacheDataSinkFactory
  implements DataSink.Factory
{
  private final Cache cache;
  private final long maxCacheFileSize;

  public CacheDataSinkFactory(Cache paramCache, long paramLong)
  {
    this.cache = paramCache;
    this.maxCacheFileSize = paramLong;
  }

  public DataSink createDataSink()
  {
    return new CacheDataSink(this.cache, this.maxCacheFileSize);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CacheDataSinkFactory
 * JD-Core Version:    0.6.0
 */