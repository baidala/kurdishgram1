package org.vidogram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.io.IOException;
import java.util.NavigableSet;
import java.util.Set;

public abstract interface Cache
{
  public abstract NavigableSet<CacheSpan> addListener(String paramString, Listener paramListener);

  public abstract void commitFile(File paramFile);

  public abstract long getCacheSpace();

  public abstract NavigableSet<CacheSpan> getCachedSpans(String paramString);

  public abstract long getContentLength(String paramString);

  public abstract Set<String> getKeys();

  public abstract boolean isCached(String paramString, long paramLong1, long paramLong2);

  public abstract void releaseHoleSpan(CacheSpan paramCacheSpan);

  public abstract void removeListener(String paramString, Listener paramListener);

  public abstract void removeSpan(CacheSpan paramCacheSpan);

  public abstract void setContentLength(String paramString, long paramLong);

  public abstract File startFile(String paramString, long paramLong1, long paramLong2);

  public abstract CacheSpan startReadWrite(String paramString, long paramLong);

  public abstract CacheSpan startReadWriteNonBlocking(String paramString, long paramLong);

  public static class CacheException extends IOException
  {
    public CacheException(IOException paramIOException)
    {
      super();
    }

    public CacheException(String paramString)
    {
      super();
    }
  }

  public static abstract interface Listener
  {
    public abstract void onSpanAdded(Cache paramCache, CacheSpan paramCacheSpan);

    public abstract void onSpanRemoved(Cache paramCache, CacheSpan paramCacheSpan);

    public abstract void onSpanTouched(Cache paramCache, CacheSpan paramCacheSpan1, CacheSpan paramCacheSpan2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.Cache
 * JD-Core Version:    0.6.0
 */