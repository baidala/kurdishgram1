package org.vidogram.messenger.exoplayer2.upstream.cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;
import org.vidogram.messenger.exoplayer2.util.Assertions;

final class CachedContent
{
  private final TreeSet<SimpleCacheSpan> cachedSpans;
  public final int id;
  public final String key;
  private long length;

  public CachedContent(int paramInt, String paramString, long paramLong)
  {
    this.id = paramInt;
    this.key = paramString;
    this.length = paramLong;
    this.cachedSpans = new TreeSet();
  }

  public CachedContent(DataInputStream paramDataInputStream)
  {
    this(paramDataInputStream.readInt(), paramDataInputStream.readUTF(), paramDataInputStream.readLong());
  }

  private SimpleCacheSpan getSpanInternal(long paramLong)
  {
    SimpleCacheSpan localSimpleCacheSpan2 = SimpleCacheSpan.createLookup(this.key, paramLong);
    SimpleCacheSpan localSimpleCacheSpan3 = (SimpleCacheSpan)this.cachedSpans.floor(localSimpleCacheSpan2);
    SimpleCacheSpan localSimpleCacheSpan1;
    if (localSimpleCacheSpan3 != null)
    {
      localSimpleCacheSpan1 = localSimpleCacheSpan3;
      if (localSimpleCacheSpan3.position + localSimpleCacheSpan3.length > paramLong);
    }
    else
    {
      localSimpleCacheSpan1 = localSimpleCacheSpan2;
    }
    return localSimpleCacheSpan1;
  }

  public void addSpan(SimpleCacheSpan paramSimpleCacheSpan)
  {
    this.cachedSpans.add(paramSimpleCacheSpan);
  }

  public long getLength()
  {
    return this.length;
  }

  public SimpleCacheSpan getSpan(long paramLong)
  {
    SimpleCacheSpan localSimpleCacheSpan2 = getSpanInternal(paramLong);
    SimpleCacheSpan localSimpleCacheSpan1 = localSimpleCacheSpan2;
    if (!localSimpleCacheSpan2.isCached)
    {
      localSimpleCacheSpan1 = (SimpleCacheSpan)this.cachedSpans.ceiling(localSimpleCacheSpan2);
      if (localSimpleCacheSpan1 == null)
        localSimpleCacheSpan1 = SimpleCacheSpan.createOpenHole(this.key, paramLong);
    }
    else
    {
      return localSimpleCacheSpan1;
    }
    return SimpleCacheSpan.createClosedHole(this.key, paramLong, localSimpleCacheSpan1.position - paramLong);
  }

  public TreeSet<SimpleCacheSpan> getSpans()
  {
    return this.cachedSpans;
  }

  public int headerHashCode()
  {
    return (this.id * 31 + this.key.hashCode()) * 31 + (int)(this.length ^ this.length >>> 32);
  }

  public boolean isCached(long paramLong1, long paramLong2)
  {
    Object localObject = getSpanInternal(paramLong1);
    if (!((SimpleCacheSpan)localObject).isCached)
      return false;
    paramLong2 = paramLong1 + paramLong2;
    paramLong1 = ((SimpleCacheSpan)localObject).position + ((SimpleCacheSpan)localObject).length;
    if (paramLong1 >= paramLong2)
      return true;
    localObject = this.cachedSpans.tailSet(localObject, false).iterator();
    while (((Iterator)localObject).hasNext())
    {
      SimpleCacheSpan localSimpleCacheSpan = (SimpleCacheSpan)((Iterator)localObject).next();
      if (localSimpleCacheSpan.position > paramLong1)
        return false;
      long l = localSimpleCacheSpan.position;
      paramLong1 = Math.max(paramLong1, localSimpleCacheSpan.length + l);
      if (paramLong1 >= paramLong2)
        return true;
    }
    return false;
  }

  public boolean isEmpty()
  {
    return this.cachedSpans.isEmpty();
  }

  public boolean removeSpan(CacheSpan paramCacheSpan)
  {
    if (this.cachedSpans.remove(paramCacheSpan))
    {
      paramCacheSpan.file.delete();
      return true;
    }
    return false;
  }

  public void setLength(long paramLong)
  {
    this.length = paramLong;
  }

  public SimpleCacheSpan touch(SimpleCacheSpan paramSimpleCacheSpan)
  {
    Assertions.checkState(this.cachedSpans.remove(paramSimpleCacheSpan));
    SimpleCacheSpan localSimpleCacheSpan = paramSimpleCacheSpan.copyWithUpdatedLastAccessTime(this.id);
    if (!paramSimpleCacheSpan.file.renameTo(localSimpleCacheSpan.file))
      throw new Cache.CacheException("Renaming of " + paramSimpleCacheSpan.file + " to " + localSimpleCacheSpan.file + " failed.");
    this.cachedSpans.add(localSimpleCacheSpan);
    return localSimpleCacheSpan;
  }

  public void writeToStream(DataOutputStream paramDataOutputStream)
  {
    paramDataOutputStream.writeInt(this.id);
    paramDataOutputStream.writeUTF(this.key);
    paramDataOutputStream.writeLong(this.length);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.CachedContent
 * JD-Core Version:    0.6.0
 */