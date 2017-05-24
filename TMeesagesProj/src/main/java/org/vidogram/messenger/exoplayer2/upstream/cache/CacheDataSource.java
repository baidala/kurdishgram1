package org.vidogram.messenger.exoplayer2.upstream.cache;

import android.net.Uri;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.vidogram.messenger.exoplayer2.upstream.DataSink;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSourceException;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.upstream.FileDataSource;
import org.vidogram.messenger.exoplayer2.upstream.TeeDataSource;

public final class CacheDataSource
  implements DataSource
{
  public static final long DEFAULT_MAX_CACHE_FILE_SIZE = 2097152L;
  public static final int FLAG_BLOCK_ON_CACHE = 1;
  public static final int FLAG_IGNORE_CACHE_FOR_UNSET_LENGTH_REQUESTS = 4;
  public static final int FLAG_IGNORE_CACHE_ON_ERROR = 2;
  private final boolean blockOnCache;
  private long bytesRemaining;
  private final Cache cache;
  private final DataSource cacheReadDataSource;
  private final DataSource cacheWriteDataSource;
  private DataSource currentDataSource;
  private boolean currentRequestIgnoresCache;
  private boolean currentRequestUnbounded;
  private final EventListener eventListener;
  private int flags;
  private final boolean ignoreCacheForUnsetLengthRequests;
  private final boolean ignoreCacheOnError;
  private String key;
  private CacheSpan lockedSpan;
  private long readPosition;
  private boolean seenCacheError;
  private long totalCachedBytesRead;
  private final DataSource upstreamDataSource;
  private Uri uri;

  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt)
  {
    this(paramCache, paramDataSource, paramInt, 2097152L);
  }

  public CacheDataSource(Cache paramCache, DataSource paramDataSource, int paramInt, long paramLong)
  {
    this(paramCache, paramDataSource, new FileDataSource(), new CacheDataSink(paramCache, paramLong), paramInt, null);
  }

  public CacheDataSource(Cache paramCache, DataSource paramDataSource1, DataSource paramDataSource2, DataSink paramDataSink, int paramInt, EventListener paramEventListener)
  {
    this.cache = paramCache;
    this.cacheReadDataSource = paramDataSource2;
    boolean bool1;
    if ((paramInt & 0x1) != 0)
    {
      bool1 = true;
      this.blockOnCache = bool1;
      if ((paramInt & 0x2) == 0)
        break label103;
      bool1 = true;
      label43: this.ignoreCacheOnError = bool1;
      if ((paramInt & 0x4) == 0)
        break label109;
      bool1 = bool2;
      label60: this.ignoreCacheForUnsetLengthRequests = bool1;
      this.upstreamDataSource = paramDataSource1;
      if (paramDataSink == null)
        break label115;
    }
    label103: label109: label115: for (this.cacheWriteDataSource = new TeeDataSource(paramDataSource1, paramDataSink); ; this.cacheWriteDataSource = null)
    {
      this.eventListener = paramEventListener;
      return;
      bool1 = false;
      break;
      bool1 = false;
      break label43;
      bool1 = false;
      break label60;
    }
  }

  private void closeCurrentSource()
  {
    if (this.currentDataSource == null);
    while (true)
    {
      return;
      try
      {
        this.currentDataSource.close();
        this.currentDataSource = null;
        this.currentRequestUnbounded = false;
        return;
      }
      finally
      {
        if (this.lockedSpan != null)
        {
          this.cache.releaseHoleSpan(this.lockedSpan);
          this.lockedSpan = null;
        }
      }
    }
    throw localObject;
  }

  private void handleBeforeThrow(IOException paramIOException)
  {
    if ((this.currentDataSource == this.cacheReadDataSource) || ((paramIOException instanceof Cache.CacheException)))
      this.seenCacheError = true;
  }

  private void notifyBytesRead()
  {
    if ((this.eventListener != null) && (this.totalCachedBytesRead > 0L))
    {
      this.eventListener.onCachedBytesRead(this.cache.getCacheSpace(), this.totalCachedBytesRead);
      this.totalCachedBytesRead = 0L;
    }
  }

  private boolean openNextSource(boolean paramBoolean)
  {
    Object localObject1;
    if (this.currentRequestIgnoresCache)
      localObject1 = null;
    while (true)
    {
      label52: boolean bool;
      if (localObject1 == null)
      {
        this.currentDataSource = this.upstreamDataSource;
        localObject1 = new DataSpec(this.uri, this.readPosition, this.bytesRemaining, this.key, this.flags);
        if (((DataSpec)localObject1).length != -1L)
          break label406;
        bool = true;
        this.currentRequestUnbounded = bool;
        bool = false;
      }
      try
      {
        l1 = this.currentDataSource.open((DataSpec)localObject1);
        paramBoolean = true;
        if ((this.currentRequestUnbounded) && (l1 != -1L))
        {
          this.bytesRemaining = l1;
          if (this.lockedSpan != null)
            setContentLength(((DataSpec)localObject1).position + this.bytesRemaining);
        }
        return paramBoolean;
        if (this.blockOnCache)
          try
          {
            localObject1 = this.cache.startReadWrite(this.key, this.readPosition);
          }
          catch (InterruptedException localInterruptedException)
          {
            throw new InterruptedIOException();
          }
        Object localObject2 = this.cache.startReadWriteNonBlocking(this.key, this.readPosition);
        continue;
        Object localObject3;
        long l2;
        if (((CacheSpan)localObject2).isCached)
        {
          localObject3 = Uri.fromFile(((CacheSpan)localObject2).file);
          long l3 = this.readPosition - ((CacheSpan)localObject2).position;
          l2 = ((CacheSpan)localObject2).length - l3;
          l1 = l2;
          if (this.bytesRemaining != -1L)
            l1 = Math.min(l2, this.bytesRemaining);
          localObject2 = new DataSpec((Uri)localObject3, this.readPosition, l3, l1, this.key, this.flags);
          this.currentDataSource = this.cacheReadDataSource;
          break label52;
        }
        this.lockedSpan = ((CacheSpan)localObject2);
        if (((CacheSpan)localObject2).isOpenEnded())
        {
          l1 = this.bytesRemaining;
          label311: localObject3 = new DataSpec(this.uri, this.readPosition, l1, this.key, this.flags);
          if (this.cacheWriteDataSource == null)
            break label397;
        }
        label397: for (localObject2 = this.cacheWriteDataSource; ; localObject2 = this.upstreamDataSource)
        {
          this.currentDataSource = ((DataSource)localObject2);
          localObject2 = localObject3;
          break;
          l2 = ((CacheSpan)localObject2).length;
          l1 = l2;
          if (this.bytesRemaining == -1L)
            break label311;
          l1 = Math.min(l2, this.bytesRemaining);
          break label311;
        }
        label406: bool = false;
      }
      catch (IOException localIOException1)
      {
        while (true)
        {
          IOException localIOException2 = localIOException1;
          if (!paramBoolean)
          {
            localIOException2 = localIOException1;
            if (!this.currentRequestUnbounded);
          }
          for (Object localObject4 = localIOException1; ; localObject4 = ((Throwable)localObject4).getCause())
          {
            localIOException2 = localIOException1;
            if (localObject4 != null)
            {
              if ((!(localObject4 instanceof DataSourceException)) || (((DataSourceException)localObject4).reason != 0))
                continue;
              localIOException2 = null;
            }
            if (localIOException2 == null)
              break;
            throw localIOException2;
          }
          long l1 = 0L;
          paramBoolean = bool;
        }
      }
    }
  }

  private void setContentLength(long paramLong)
  {
    this.cache.setContentLength(this.key, paramLong);
  }

  public void close()
  {
    this.uri = null;
    notifyBytesRead();
    try
    {
      closeCurrentSource();
      return;
    }
    catch (IOException localIOException)
    {
      handleBeforeThrow(localIOException);
    }
    throw localIOException;
  }

  public Uri getUri()
  {
    if (this.currentDataSource == this.upstreamDataSource)
      return this.currentDataSource.getUri();
    return this.uri;
  }

  public long open(DataSpec paramDataSpec)
  {
    boolean bool2 = true;
    while (true)
    {
      try
      {
        this.uri = paramDataSpec.uri;
        this.flags = paramDataSpec.flags;
        if (paramDataSpec.key == null)
          continue;
        String str = paramDataSpec.key;
        this.key = str;
        this.readPosition = paramDataSpec.position;
        if (!this.ignoreCacheOnError)
          continue;
        bool1 = bool2;
        if (this.seenCacheError)
          continue;
        if ((paramDataSpec.length == -1L) && (this.ignoreCacheForUnsetLengthRequests))
        {
          bool1 = bool2;
          this.currentRequestIgnoresCache = bool1;
          if ((paramDataSpec.length == -1L) && (!this.currentRequestIgnoresCache))
            continue;
          this.bytesRemaining = paramDataSpec.length;
          openNextSource(true);
          return this.bytesRemaining;
          str = this.uri.toString();
          continue;
          this.bytesRemaining = this.cache.getContentLength(this.key);
          if (this.bytesRemaining == -1L)
            continue;
          this.bytesRemaining -= paramDataSpec.position;
          continue;
        }
      }
      catch (IOException paramDataSpec)
      {
        handleBeforeThrow(paramDataSpec);
        throw paramDataSpec;
      }
      boolean bool1 = false;
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    if (paramInt2 == 0);
    do
    {
      int j;
      do
      {
        while (true)
        {
          return i;
          if (this.bytesRemaining == 0L)
            return -1;
          try
          {
            j = this.currentDataSource.read(paramArrayOfByte, paramInt1, paramInt2);
            if (j < 0)
              break;
            if (this.currentDataSource == this.cacheReadDataSource)
              this.totalCachedBytesRead += j;
            this.readPosition += j;
            i = j;
            if (this.bytesRemaining == -1L)
              continue;
            this.bytesRemaining -= j;
            return j;
          }
          catch (IOException paramArrayOfByte)
          {
            handleBeforeThrow(paramArrayOfByte);
            throw paramArrayOfByte;
          }
        }
        if (this.currentRequestUnbounded)
        {
          setContentLength(this.readPosition);
          this.bytesRemaining = 0L;
        }
        closeCurrentSource();
        if (this.bytesRemaining > 0L)
          break;
        i = j;
      }
      while (this.bytesRemaining != -1L);
      i = j;
    }
    while (!openNextSource(false));
    paramInt1 = read(paramArrayOfByte, paramInt1, paramInt2);
    return paramInt1;
  }

  public static abstract interface EventListener
  {
    public abstract void onCachedBytesRead(long paramLong1, long paramLong2);
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CacheDataSource
 * JD-Core Version:    0.6.0
 */