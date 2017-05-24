package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.vidogram.messenger.exoplayer2.util.UriUtil;

public final class RangedUri
{
  private int hashCode;
  public final long length;
  private final String referenceUri;
  public final long start;

  public RangedUri(String paramString, long paramLong1, long paramLong2)
  {
    String str = paramString;
    if (paramString == null)
      str = "";
    this.referenceUri = str;
    this.start = paramLong1;
    this.length = paramLong2;
  }

  public RangedUri attemptMerge(RangedUri paramRangedUri, String paramString)
  {
    long l1 = -1L;
    String str = resolveUriString(paramString);
    if ((paramRangedUri == null) || (!str.equals(paramRangedUri.resolveUriString(paramString))));
    do
    {
      return null;
      if ((this.length == -1L) || (this.start + this.length != paramRangedUri.start))
        continue;
      l2 = this.start;
      if (paramRangedUri.length == -1L);
      while (true)
      {
        return new RangedUri(str, l2, l1);
        l1 = this.length + paramRangedUri.length;
      }
    }
    while ((paramRangedUri.length == -1L) || (paramRangedUri.start + paramRangedUri.length != this.start));
    long l2 = paramRangedUri.start;
    if (this.length == -1L);
    while (true)
    {
      return new RangedUri(str, l2, l1);
      l1 = paramRangedUri.length + this.length;
    }
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (RangedUri)paramObject;
    }
    while ((this.start == paramObject.start) && (this.length == paramObject.length) && (this.referenceUri.equals(paramObject.referenceUri)));
    return false;
  }

  public int hashCode()
  {
    if (this.hashCode == 0)
      this.hashCode = ((((int)this.start + 527) * 31 + (int)this.length) * 31 + this.referenceUri.hashCode());
    return this.hashCode;
  }

  public Uri resolveUri(String paramString)
  {
    return UriUtil.resolveToUri(paramString, this.referenceUri);
  }

  public String resolveUriString(String paramString)
  {
    return UriUtil.resolve(paramString, this.referenceUri);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.RangedUri
 * JD-Core Version:    0.6.0
 */