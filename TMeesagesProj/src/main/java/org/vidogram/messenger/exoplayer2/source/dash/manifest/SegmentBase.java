package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.util.Util;

public abstract class SegmentBase
{
  final RangedUri initialization;
  final long presentationTimeOffset;
  final long timescale;

  public SegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2)
  {
    this.initialization = paramRangedUri;
    this.timescale = paramLong1;
    this.presentationTimeOffset = paramLong2;
  }

  public RangedUri getInitialization(Representation paramRepresentation)
  {
    return this.initialization;
  }

  public long getPresentationTimeOffsetUs()
  {
    return Util.scaleLargeTimestamp(this.presentationTimeOffset, 1000000L, this.timescale);
  }

  public static abstract class MultiSegmentBase extends SegmentBase
  {
    final long duration;
    final List<SegmentBase.SegmentTimelineElement> segmentTimeline;
    final int startNumber;

    public MultiSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList)
    {
      super(paramLong1, paramLong2);
      this.startNumber = paramInt;
      this.duration = paramLong3;
      this.segmentTimeline = paramList;
    }

    public int getFirstSegmentNum()
    {
      return this.startNumber;
    }

    public abstract int getLastSegmentNum(long paramLong);

    public final long getSegmentDurationUs(int paramInt, long paramLong)
    {
      if (this.segmentTimeline != null)
        return ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).duration * 1000000L / this.timescale;
      if (paramInt == getLastSegmentNum(paramLong))
        return paramLong - getSegmentTimeUs(paramInt);
      return this.duration * 1000000L / this.timescale;
    }

    public int getSegmentNum(long paramLong1, long paramLong2)
    {
      int k = getFirstSegmentNum();
      int i = getLastSegmentNum(paramLong2);
      int j;
      if (this.segmentTimeline == null)
      {
        paramLong2 = this.duration * 1000000L / this.timescale;
        j = this.startNumber;
        j = (int)(paramLong1 / paramLong2) + j;
        if (j < k)
          return k;
        if ((i != -1) && (j > i))
          return i;
        return j;
      }
      while (true)
      {
        if (j <= i)
        {
          int m = (j + i) / 2;
          paramLong2 = getSegmentTimeUs(m);
          if (paramLong2 < paramLong1)
          {
            j = m + 1;
            continue;
          }
          if (paramLong2 > paramLong1)
          {
            i = m - 1;
            continue;
          }
          return m;
        }
        if (j == k);
        while (true)
        {
          return j;
          j = i;
        }
        j = k;
      }
    }

    public final long getSegmentTimeUs(int paramInt)
    {
      long l;
      if (this.segmentTimeline != null)
        l = ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).startTime - this.presentationTimeOffset;
      while (true)
      {
        return Util.scaleLargeTimestamp(l, 1000000L, this.timescale);
        l = (paramInt - this.startNumber) * this.duration;
      }
    }

    public abstract RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt);

    public boolean isExplicit()
    {
      return this.segmentTimeline != null;
    }
  }

  public static class SegmentList extends SegmentBase.MultiSegmentBase
  {
    final List<RangedUri> mediaSegments;

    public SegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
    {
      super(paramLong1, paramLong2, paramInt, paramLong3, paramList);
      this.mediaSegments = paramList1;
    }

    public int getLastSegmentNum(long paramLong)
    {
      return this.startNumber + this.mediaSegments.size() - 1;
    }

    public RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt)
    {
      return (RangedUri)this.mediaSegments.get(paramInt - this.startNumber);
    }

    public boolean isExplicit()
    {
      return true;
    }
  }

  public static class SegmentTemplate extends SegmentBase.MultiSegmentBase
  {
    final UrlTemplate initializationTemplate;
    final UrlTemplate mediaTemplate;

    public SegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
    {
      super(paramLong1, paramLong2, paramInt, paramLong3, paramList);
      this.initializationTemplate = paramUrlTemplate1;
      this.mediaTemplate = paramUrlTemplate2;
    }

    public RangedUri getInitialization(Representation paramRepresentation)
    {
      if (this.initializationTemplate != null)
        return new RangedUri(this.initializationTemplate.buildUri(paramRepresentation.format.id, 0, paramRepresentation.format.bitrate, 0L), 0L, -1L);
      return super.getInitialization(paramRepresentation);
    }

    public int getLastSegmentNum(long paramLong)
    {
      if (this.segmentTimeline != null)
        return this.segmentTimeline.size() + this.startNumber - 1;
      if (paramLong == -9223372036854775807L)
        return -1;
      long l = this.duration * 1000000L / this.timescale;
      int i = this.startNumber;
      return (int)Util.ceilDivide(paramLong, l) + i - 1;
    }

    public RangedUri getSegmentUrl(Representation paramRepresentation, int paramInt)
    {
      long l;
      if (this.segmentTimeline != null)
        l = ((SegmentBase.SegmentTimelineElement)this.segmentTimeline.get(paramInt - this.startNumber)).startTime;
      while (true)
      {
        return new RangedUri(this.mediaTemplate.buildUri(paramRepresentation.format.id, paramInt, paramRepresentation.format.bitrate, l), 0L, -1L);
        l = (paramInt - this.startNumber) * this.duration;
      }
    }
  }

  public static class SegmentTimelineElement
  {
    final long duration;
    final long startTime;

    public SegmentTimelineElement(long paramLong1, long paramLong2)
    {
      this.startTime = paramLong1;
      this.duration = paramLong2;
    }
  }

  public static class SingleSegmentBase extends SegmentBase
  {
    final long indexLength;
    final long indexStart;

    public SingleSegmentBase(String paramString)
    {
      this(null, 1L, 0L, 0L, 0L);
    }

    public SingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
    {
      super(paramLong1, paramLong2);
      this.indexStart = paramLong3;
      this.indexLength = paramLong4;
    }

    public RangedUri getIndex()
    {
      if (this.indexLength <= 0L)
        return null;
      return new RangedUri(null, this.indexStart, this.indexLength);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.SegmentBase
 * JD-Core Version:    0.6.0
 */