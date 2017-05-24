package org.vidogram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

final class SimpleCacheSpan extends CacheSpan
{
  private static final Pattern CACHE_FILE_PATTERN_V1 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v1\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V2 = Pattern.compile("^(.+)\\.(\\d+)\\.(\\d+)\\.v2\\.exo$", 32);
  private static final Pattern CACHE_FILE_PATTERN_V3 = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)\\.v3\\.exo$", 32);
  private static final String SUFFIX = ".v3.exo";

  private SimpleCacheSpan(String paramString, long paramLong1, long paramLong2, long paramLong3, File paramFile)
  {
    super(paramString, paramLong1, paramLong2, paramLong3, paramFile);
  }

  public static SimpleCacheSpan createCacheEntry(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    Object localObject = paramFile.getName();
    if (!((String)localObject).endsWith(".v3.exo"))
    {
      paramFile = upgradeFile(paramFile, paramCachedContentIndex);
      if (paramFile == null)
        return null;
      localObject = paramFile.getName();
    }
    while (true)
    {
      localObject = CACHE_FILE_PATTERN_V3.matcher((CharSequence)localObject);
      if (!((Matcher)localObject).matches())
        break;
      long l = paramFile.length();
      paramCachedContentIndex = paramCachedContentIndex.getKeyForId(Integer.parseInt(((Matcher)localObject).group(1)));
      if (paramCachedContentIndex == null);
      for (paramFile = null; ; paramFile = new SimpleCacheSpan(paramCachedContentIndex, Long.parseLong(((Matcher)localObject).group(2)), l, Long.parseLong(((Matcher)localObject).group(3)), paramFile))
        return paramFile;
    }
  }

  public static SimpleCacheSpan createClosedHole(String paramString, long paramLong1, long paramLong2)
  {
    return new SimpleCacheSpan(paramString, paramLong1, paramLong2, -9223372036854775807L, null);
  }

  public static SimpleCacheSpan createLookup(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }

  public static SimpleCacheSpan createOpenHole(String paramString, long paramLong)
  {
    return new SimpleCacheSpan(paramString, paramLong, -1L, -9223372036854775807L, null);
  }

  public static File getCacheFile(File paramFile, int paramInt, long paramLong1, long paramLong2)
  {
    return new File(paramFile, paramInt + "." + paramLong1 + "." + paramLong2 + ".v3.exo");
  }

  private static File upgradeFile(File paramFile, CachedContentIndex paramCachedContentIndex)
  {
    Object localObject = paramFile.getName();
    Matcher localMatcher = CACHE_FILE_PATTERN_V2.matcher((CharSequence)localObject);
    if (localMatcher.matches())
    {
      localObject = Util.unescapeFileName(localMatcher.group(1));
      if (localObject == null)
      {
        paramCachedContentIndex = null;
        return paramCachedContentIndex;
      }
    }
    else
    {
      localMatcher = CACHE_FILE_PATTERN_V1.matcher((CharSequence)localObject);
      if (!localMatcher.matches())
        return null;
      localObject = localMatcher.group(1);
    }
    while (true)
    {
      localObject = getCacheFile(paramFile.getParentFile(), paramCachedContentIndex.assignIdForKey((String)localObject), Long.parseLong(localMatcher.group(2)), Long.parseLong(localMatcher.group(3)));
      paramCachedContentIndex = (CachedContentIndex)localObject;
      if (paramFile.renameTo((File)localObject))
        break;
      return null;
    }
  }

  public SimpleCacheSpan copyWithUpdatedLastAccessTime(int paramInt)
  {
    Assertions.checkState(this.isCached);
    long l = System.currentTimeMillis();
    File localFile = getCacheFile(this.file.getParentFile(), paramInt, this.position, l);
    return new SimpleCacheSpan(this.key, this.position, this.length, l, localFile);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.cache.SimpleCacheSpan
 * JD-Core Version:    0.6.0
 */