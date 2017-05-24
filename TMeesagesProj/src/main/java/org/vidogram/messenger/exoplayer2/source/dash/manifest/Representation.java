package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.source.dash.DashSegmentIndex;

public abstract class Representation
{
  public static final long REVISION_ID_DEFAULT = -1L;
  public final String baseUrl;
  public final String contentId;
  public final Format format;
  private final RangedUri initializationUri;
  public final long presentationTimeOffsetUs;
  public final long revisionId;

  private Representation(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase)
  {
    this.contentId = paramString1;
    this.revisionId = paramLong;
    this.format = paramFormat;
    this.baseUrl = paramString2;
    this.initializationUri = paramSegmentBase.getInitialization(this);
    this.presentationTimeOffsetUs = paramSegmentBase.getPresentationTimeOffsetUs();
  }

  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase)
  {
    return newInstance(paramString1, paramLong, paramFormat, paramString2, paramSegmentBase, null);
  }

  public static Representation newInstance(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase paramSegmentBase, String paramString3)
  {
    if ((paramSegmentBase instanceof SegmentBase.SingleSegmentBase))
      return new SingleSegmentRepresentation(paramString1, paramLong, paramFormat, paramString2, (SegmentBase.SingleSegmentBase)paramSegmentBase, paramString3, -1L);
    if ((paramSegmentBase instanceof SegmentBase.MultiSegmentBase))
      return new MultiSegmentRepresentation(paramString1, paramLong, paramFormat, paramString2, (SegmentBase.MultiSegmentBase)paramSegmentBase);
    throw new IllegalArgumentException("segmentBase must be of type SingleSegmentBase or MultiSegmentBase");
  }

  public abstract String getCacheKey();

  public abstract DashSegmentIndex getIndex();

  public abstract RangedUri getIndexUri();

  public RangedUri getInitializationUri()
  {
    return this.initializationUri;
  }

  public static class MultiSegmentRepresentation extends Representation
    implements DashSegmentIndex
  {
    private final SegmentBase.MultiSegmentBase segmentBase;

    public MultiSegmentRepresentation(String paramString1, long paramLong, Format paramFormat, String paramString2, SegmentBase.MultiSegmentBase paramMultiSegmentBase)
    {
      super(paramLong, paramFormat, paramString2, paramMultiSegmentBase, null);
      this.segmentBase = paramMultiSegmentBase;
    }

    public String getCacheKey()
    {
      return null;
    }

    public long getDurationUs(int paramInt, long paramLong)
    {
      return this.segmentBase.getSegmentDurationUs(paramInt, paramLong);
    }

    public int getFirstSegmentNum()
    {
      return this.segmentBase.getFirstSegmentNum();
    }

    public DashSegmentIndex getIndex()
    {
      return this;
    }

    public RangedUri getIndexUri()
    {
      return null;
    }

    public int getLastSegmentNum(long paramLong)
    {
      return this.segmentBase.getLastSegmentNum(paramLong);
    }

    public int getSegmentNum(long paramLong1, long paramLong2)
    {
      return this.segmentBase.getSegmentNum(paramLong1, paramLong2);
    }

    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentBase.getSegmentUrl(this, paramInt);
    }

    public long getTimeUs(int paramInt)
    {
      return this.segmentBase.getSegmentTimeUs(paramInt);
    }

    public boolean isExplicit()
    {
      return this.segmentBase.isExplicit();
    }
  }

  public static class SingleSegmentRepresentation extends Representation
  {
    private final String cacheKey;
    public final long contentLength;
    private final RangedUri indexUri;
    private final SingleSegmentIndex segmentIndex;
    public final Uri uri;

    public SingleSegmentRepresentation(String paramString1, long paramLong1, Format paramFormat, String paramString2, SegmentBase.SingleSegmentBase paramSingleSegmentBase, String paramString3, long paramLong2)
    {
      super(paramLong1, paramFormat, paramString2, paramSingleSegmentBase, null);
      this.uri = Uri.parse(paramString2);
      this.indexUri = paramSingleSegmentBase.getIndex();
      if (paramString3 != null)
      {
        this.cacheKey = paramString3;
        this.contentLength = paramLong2;
        if (this.indexUri == null)
          break label114;
      }
      label114: for (paramString1 = null; ; paramString1 = new SingleSegmentIndex(new RangedUri(null, 0L, paramLong2)))
      {
        this.segmentIndex = paramString1;
        return;
        if (paramString1 != null)
        {
          paramString3 = paramString1 + "." + paramFormat.id + "." + paramLong1;
          break;
        }
        paramString3 = null;
        break;
      }
    }

    public static SingleSegmentRepresentation newInstance(String paramString1, long paramLong1, Format paramFormat, String paramString2, long paramLong2, long paramLong3, long paramLong4, long paramLong5, String paramString3, long paramLong6)
    {
      return new SingleSegmentRepresentation(paramString1, paramLong1, paramFormat, paramString2, new SegmentBase.SingleSegmentBase(new RangedUri(null, paramLong2, 1L + (paramLong3 - paramLong2)), 1L, 0L, paramLong4, 1L + (paramLong5 - paramLong4)), paramString3, paramLong6);
    }

    public String getCacheKey()
    {
      return this.cacheKey;
    }

    public DashSegmentIndex getIndex()
    {
      return this.segmentIndex;
    }

    public RangedUri getIndexUri()
    {
      return this.indexUri;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.Representation
 * JD-Core Version:    0.6.0
 */