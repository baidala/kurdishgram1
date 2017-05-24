package org.vidogram.messenger.exoplayer2.source.hls.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class HlsMediaPlaylist extends HlsPlaylist
{
  public final long durationUs;
  public final boolean hasEndTag;
  public final Segment initializationSegment;
  public final int mediaSequence;
  public final List<Segment> segments;
  public final int version;

  public HlsMediaPlaylist(String paramString, int paramInt1, int paramInt2, boolean paramBoolean, Segment paramSegment, List<Segment> paramList)
  {
    super(paramString, 1);
    this.mediaSequence = paramInt1;
    this.version = paramInt2;
    this.hasEndTag = paramBoolean;
    this.initializationSegment = paramSegment;
    this.segments = Collections.unmodifiableList(paramList);
    if (!paramList.isEmpty())
    {
      paramString = (Segment)paramList.get(0);
      paramSegment = (Segment)paramList.get(paramList.size() - 1);
      this.durationUs = (paramSegment.startTimeUs + paramSegment.durationUs - paramString.startTimeUs);
      return;
    }
    this.durationUs = 0L;
  }

  public HlsMediaPlaylist copyWithSegments(List<Segment> paramList)
  {
    return new HlsMediaPlaylist(this.baseUri, this.mediaSequence, this.version, this.hasEndTag, this.initializationSegment, paramList);
  }

  public HlsMediaPlaylist copyWithStartTimeUs(long paramLong)
  {
    long l = getStartTimeUs();
    int j = this.segments.size();
    ArrayList localArrayList = new ArrayList(j);
    int i = 0;
    while (i < j)
    {
      Segment localSegment = (Segment)this.segments.get(i);
      localArrayList.add(localSegment.copyWithStartTimeUs(localSegment.startTimeUs + (paramLong - l)));
      i += 1;
    }
    return copyWithSegments(localArrayList);
  }

  public long getEndTimeUs()
  {
    return getStartTimeUs() + this.durationUs;
  }

  public long getStartTimeUs()
  {
    if (this.segments.isEmpty())
      return 0L;
    return ((Segment)this.segments.get(0)).startTimeUs;
  }

  public static final class Segment
    implements Comparable<Long>
  {
    public final long byterangeLength;
    public final long byterangeOffset;
    public final int discontinuitySequenceNumber;
    public final long durationUs;
    public final String encryptionIV;
    public final String encryptionKeyUri;
    public final boolean isEncrypted;
    public final long startTimeUs;
    public final String url;

    public Segment(String paramString1, long paramLong1, int paramInt, long paramLong2, boolean paramBoolean, String paramString2, String paramString3, long paramLong3, long paramLong4)
    {
      this.url = paramString1;
      this.durationUs = paramLong1;
      this.discontinuitySequenceNumber = paramInt;
      this.startTimeUs = paramLong2;
      this.isEncrypted = paramBoolean;
      this.encryptionKeyUri = paramString2;
      this.encryptionIV = paramString3;
      this.byterangeOffset = paramLong3;
      this.byterangeLength = paramLong4;
    }

    public Segment(String paramString, long paramLong1, long paramLong2)
    {
      this(paramString, 0L, -1, -9223372036854775807L, false, null, null, paramLong1, paramLong2);
    }

    public int compareTo(Long paramLong)
    {
      if (this.startTimeUs > paramLong.longValue())
        return 1;
      if (this.startTimeUs < paramLong.longValue())
        return -1;
      return 0;
    }

    public Segment copyWithStartTimeUs(long paramLong)
    {
      return new Segment(this.url, this.durationUs, this.discontinuitySequenceNumber, paramLong, this.isEncrypted, this.encryptionKeyUri, this.encryptionIV, this.byterangeOffset, this.byterangeLength);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist
 * JD-Core Version:    0.6.0
 */