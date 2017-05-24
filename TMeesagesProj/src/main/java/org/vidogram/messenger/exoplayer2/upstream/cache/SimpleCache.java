package org.vidogram.messenger.exoplayer2.upstream.cache;

import android.os.ConditionVariable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class SimpleCache
  implements Cache
{
  private final File cacheDir;
  private final CacheEvictor evictor;
  private final CachedContentIndex index;
  private Cache.CacheException initializationException;
  private final HashMap<String, ArrayList<Cache.Listener>> listeners;
  private final HashMap<String, CacheSpan> lockedSpans;
  private long totalSpace = 0L;

  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor)
  {
    this(paramFile, paramCacheEvictor, null);
  }

  public SimpleCache(File paramFile, CacheEvictor paramCacheEvictor, byte[] paramArrayOfByte)
  {
    this.cacheDir = paramFile;
    this.evictor = paramCacheEvictor;
    this.lockedSpans = new HashMap();
    this.index = new CachedContentIndex(paramFile, paramArrayOfByte);
    this.listeners = new HashMap();
    paramFile = new ConditionVariable();
    new Thread("SimpleCache.initialize()", paramFile)
    {
      public void run()
      {
        synchronized (SimpleCache.this)
        {
          this.val$conditionVariable.open();
          try
          {
            SimpleCache.this.initialize();
            SimpleCache.this.evictor.onCacheInitialized();
            return;
          }
          catch (Cache.CacheException localCacheException)
          {
            while (true)
              SimpleCache.access$102(SimpleCache.this, localCacheException);
          }
        }
      }
    }
    .start();
    paramFile.block();
  }

  private void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.index.add(paramSimpleCacheSpan.key).addSpan(paramSimpleCacheSpan);
    this.totalSpace += paramSimpleCacheSpan.length;
    notifySpanAdded(paramSimpleCacheSpan);
  }

  private SimpleCacheSpan getSpan(String paramString, long paramLong)
  {
    CachedContent localCachedContent = this.index.get(paramString);
    if (localCachedContent == null)
    {
      paramString = SimpleCacheSpan.createOpenHole(paramString, paramLong);
      return paramString;
    }
    while (true)
    {
      SimpleCacheSpan localSimpleCacheSpan = localCachedContent.getSpan(paramLong);
      paramString = localSimpleCacheSpan;
      if (!localSimpleCacheSpan.isCached)
        break;
      paramString = localSimpleCacheSpan;
      if (localSimpleCacheSpan.file.exists())
        break;
      removeStaleSpansAndCachedContents();
    }
  }

  private void initialize()
  {
    if (!this.cacheDir.exists())
      this.cacheDir.mkdirs();
    File[] arrayOfFile;
    do
    {
      return;
      this.index.load();
      arrayOfFile = this.cacheDir.listFiles();
    }
    while (arrayOfFile == null);
    int j = arrayOfFile.length;
    int i = 0;
    if (i < j)
    {
      File localFile = arrayOfFile[i];
      if (localFile.getName().equals("cached_content_index.exi"));
      while (true)
      {
        i += 1;
        break;
        if (localFile.length() > 0L);
        for (SimpleCacheSpan localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(localFile, this.index); ; localSimpleCacheSpan = null)
        {
          if (localSimpleCacheSpan == null)
            break label114;
          addSpan(localSimpleCacheSpan);
          break;
        }
        label114: localFile.delete();
      }
    }
    this.index.removeEmpty();
    this.index.store();
  }

  private void notifySpanAdded(SimpleCacheSpan paramSimpleCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanAdded(this, paramSimpleCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanAdded(this, paramSimpleCacheSpan);
  }

  private void notifySpanRemoved(CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanRemoved(this, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanRemoved(this, paramCacheSpan);
  }

  private void notifySpanTouched(SimpleCacheSpan paramSimpleCacheSpan, CacheSpan paramCacheSpan)
  {
    ArrayList localArrayList = (ArrayList)this.listeners.get(paramSimpleCacheSpan.key);
    if (localArrayList != null)
    {
      int i = localArrayList.size() - 1;
      while (i >= 0)
      {
        ((Cache.Listener)localArrayList.get(i)).onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
        i -= 1;
      }
    }
    this.evictor.onSpanTouched(this, paramSimpleCacheSpan, paramCacheSpan);
  }

  private void removeSpan(CacheSpan paramCacheSpan, boolean paramBoolean)
  {
    CachedContent localCachedContent = this.index.get(paramCacheSpan.key);
    Assertions.checkState(localCachedContent.removeSpan(paramCacheSpan));
    this.totalSpace -= paramCacheSpan.length;
    if ((paramBoolean) && (localCachedContent.isEmpty()))
    {
      this.index.removeEmpty(localCachedContent.key);
      this.index.store();
    }
    notifySpanRemoved(paramCacheSpan);
  }

  private void removeStaleSpansAndCachedContents()
  {
    Object localObject = new LinkedList();
    Iterator localIterator1 = this.index.getAll().iterator();
    while (localIterator1.hasNext())
    {
      Iterator localIterator2 = ((CachedContent)localIterator1.next()).getSpans().iterator();
      while (localIterator2.hasNext())
      {
        CacheSpan localCacheSpan = (CacheSpan)localIterator2.next();
        if (localCacheSpan.file.exists())
          continue;
        ((LinkedList)localObject).add(localCacheSpan);
      }
    }
    localObject = ((LinkedList)localObject).iterator();
    while (((Iterator)localObject).hasNext())
      removeSpan((CacheSpan)((Iterator)localObject).next(), false);
    this.index.removeEmpty();
    this.index.store();
  }

  public NavigableSet<CacheSpan> addListener(String paramString, Cache.Listener paramListener)
  {
    monitorenter;
    try
    {
      ArrayList localArrayList2 = (ArrayList)this.listeners.get(paramString);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.listeners.put(paramString, localArrayList1);
      }
      localArrayList1.add(paramListener);
      paramString = getCachedSpans(paramString);
      return paramString;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public void commitFile(File paramFile)
  {
    boolean bool2 = true;
    monitorenter;
    SimpleCacheSpan localSimpleCacheSpan;
    try
    {
      localSimpleCacheSpan = SimpleCacheSpan.createCacheEntry(paramFile, this.index);
      if (localSimpleCacheSpan != null)
      {
        bool1 = true;
        Assertions.checkState(bool1);
        Assertions.checkState(this.lockedSpans.containsKey(localSimpleCacheSpan.key));
        bool1 = paramFile.exists();
        if (bool1)
          break label57;
      }
      while (true)
      {
        return;
        bool1 = false;
        break;
        label57: if (paramFile.length() != 0L)
          break label79;
        paramFile.delete();
      }
    }
    finally
    {
      monitorexit;
    }
    label79: paramFile = Long.valueOf(getContentLength(localSimpleCacheSpan.key));
    if (paramFile.longValue() != -1L)
      if (localSimpleCacheSpan.position + localSimpleCacheSpan.length > paramFile.longValue())
        break label148;
    label148: for (boolean bool1 = bool2; ; bool1 = false)
    {
      Assertions.checkState(bool1);
      addSpan(localSimpleCacheSpan);
      this.index.store();
      notifyAll();
      break;
    }
  }

  public long getCacheSpace()
  {
    monitorenter;
    try
    {
      long l = this.totalSpace;
      monitorexit;
      return l;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public NavigableSet<CacheSpan> getCachedSpans(String paramString)
  {
    monitorenter;
    try
    {
      paramString = this.index.get(paramString);
      if (paramString == null);
      for (paramString = null; ; paramString = new TreeSet(paramString.getSpans()))
        return paramString;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public long getContentLength(String paramString)
  {
    monitorenter;
    try
    {
      long l = this.index.getContentLength(paramString);
      monitorexit;
      return l;
    }
    finally
    {
      paramString = finally;
      monitorexit;
    }
    throw paramString;
  }

  public Set<String> getKeys()
  {
    monitorenter;
    try
    {
      HashSet localHashSet = new HashSet(this.index.getKeys());
      monitorexit;
      return localHashSet;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean isCached(String paramString, long paramLong1, long paramLong2)
  {
    monitorenter;
    try
    {
      paramString = this.index.get(paramString);
      if (paramString != null)
      {
        bool = paramString.isCached(paramLong1, paramLong2);
        if (!bool);
      }
      for (boolean bool = true; ; bool = false)
        return bool;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public void releaseHoleSpan(CacheSpan paramCacheSpan)
  {
    monitorenter;
    try
    {
      if (paramCacheSpan == this.lockedSpans.remove(paramCacheSpan.key));
      for (boolean bool = true; ; bool = false)
      {
        Assertions.checkState(bool);
        notifyAll();
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramCacheSpan;
  }

  public void removeListener(String paramString, Cache.Listener paramListener)
  {
    monitorenter;
    try
    {
      ArrayList localArrayList = (ArrayList)this.listeners.get(paramString);
      if (localArrayList != null)
      {
        localArrayList.remove(paramListener);
        if (localArrayList.isEmpty())
          this.listeners.remove(paramString);
      }
      return;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public void removeSpan(CacheSpan paramCacheSpan)
  {
    monitorenter;
    try
    {
      removeSpan(paramCacheSpan, true);
      monitorexit;
      return;
    }
    finally
    {
      paramCacheSpan = finally;
      monitorexit;
    }
    throw paramCacheSpan;
  }

  public void setContentLength(String paramString, long paramLong)
  {
    monitorenter;
    try
    {
      this.index.setContentLength(paramString, paramLong);
      this.index.store();
      monitorexit;
      return;
    }
    finally
    {
      paramString = finally;
      monitorexit;
    }
    throw paramString;
  }

  public File startFile(String paramString, long paramLong1, long paramLong2)
  {
    monitorenter;
    try
    {
      Assertions.checkState(this.lockedSpans.containsKey(paramString));
      if (!this.cacheDir.exists())
      {
        removeStaleSpansAndCachedContents();
        this.cacheDir.mkdirs();
      }
      this.evictor.onStartFile(this, paramString, paramLong1, paramLong2);
      paramString = SimpleCacheSpan.getCacheFile(this.cacheDir, this.index.assignIdForKey(paramString), paramLong1, System.currentTimeMillis());
      return paramString;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public SimpleCacheSpan startReadWrite(String paramString, long paramLong)
  {
    monitorenter;
    try
    {
      while (true)
      {
        SimpleCacheSpan localSimpleCacheSpan = startReadWriteNonBlocking(paramString, paramLong);
        if (localSimpleCacheSpan != null)
          return localSimpleCacheSpan;
        wait();
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public SimpleCacheSpan startReadWriteNonBlocking(String paramString, long paramLong)
  {
    monitorenter;
    try
    {
      if (this.initializationException != null)
        throw this.initializationException;
    }
    finally
    {
      monitorexit;
    }
    SimpleCacheSpan localSimpleCacheSpan = getSpan(paramString, paramLong);
    if (localSimpleCacheSpan.isCached)
    {
      paramString = this.index.get(paramString).touch(localSimpleCacheSpan);
      notifySpanTouched(localSimpleCacheSpan, paramString);
    }
    while (true)
    {
      monitorexit;
      return paramString;
      if (!this.lockedSpans.containsKey(paramString))
      {
        this.lockedSpans.put(paramString, localSimpleCacheSpan);
        paramString = localSimpleCacheSpan;
        continue;
      }
      paramString = null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.SimpleCache
 * JD-Core Version:    0.6.0
 */