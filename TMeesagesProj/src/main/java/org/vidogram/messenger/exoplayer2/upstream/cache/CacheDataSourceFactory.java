package org.vidogram.messenger.exoplayer2.upstream.cache;

import org.vidogram.messenger.exoplayer2.upstream.DataSink.Factory;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.upstream.FileDataSourceFactory;

public final class CacheDataSourceFactory
  implements DataSource.Factory
{
  private final Cache cache;
  private final DataSource.Factory cacheReadDataSourceFactory;
  private final DataSink.Factory cacheWriteDataSinkFactory;
  private final CacheDataSource.EventListener eventListener;
  private final int flags;
  private final DataSource.Factory upstreamFactory;

  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt)
  {
    this(paramCache, paramFactory, paramInt, 2097152L);
  }

  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory, int paramInt, long paramLong)
  {
    this(paramCache, paramFactory, new FileDataSourceFactory(), new CacheDataSinkFactory(paramCache, paramLong), paramInt, null);
  }

  public CacheDataSourceFactory(Cache paramCache, DataSource.Factory paramFactory1, DataSource.Factory paramFactory2, DataSink.Factory paramFactory, int paramInt, CacheDataSource.EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.upstreamFactory = paramFactory1;
    this.cacheReadDataSourceFactory = paramFactory2;
    this.cacheWriteDataSinkFactory = paramFactory;
    this.flags = paramInt;
    this.eventListener = paramEventListener;
  }

  public DataSource createDataSource()
  {
    return new CacheDataSource(this.cache, this.upstreamFactory.createDataSource(), this.cacheReadDataSourceFactory.createDataSource(), this.cacheWriteDataSinkFactory.createDataSink(), this.flags, this.eventListener);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CacheDataSourceFactory
 * JD-Core Version:    0.6.0
 */