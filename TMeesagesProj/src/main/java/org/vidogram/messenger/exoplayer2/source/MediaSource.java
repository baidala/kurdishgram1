package org.vidogram.messenger.exoplayer2.source;

import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;

public abstract interface MediaSource
{
  public abstract MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong);

  public abstract void maybeThrowSourceInfoRefreshError();

  public abstract void prepareSource(Listener paramListener);

  public abstract void releasePeriod(MediaPeriod paramMediaPeriod);

  public abstract void releaseSource();

  public static abstract interface Listener
  {
    public abstract void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.MediaSource
 * JD-Core Version:    0.6.0
 */