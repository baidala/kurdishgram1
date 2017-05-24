package org.vidogram.messenger.exoplayer2.upstream.cache;

public abstract interface CacheEvictor extends Cache.Listener
{
  public abstract void onCacheInitialized();

  public abstract void onStartFile(Cache paramCache, String paramString, long paramLong1, long paramLong2);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CacheEvictor
 * JD-Core Version:    0.6.0
 */