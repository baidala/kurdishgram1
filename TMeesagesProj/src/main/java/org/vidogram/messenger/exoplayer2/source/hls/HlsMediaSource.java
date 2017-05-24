package org.vidogram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.Handler;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.vidogram.messenger.exoplayer2.source.MediaPeriod;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.MediaSource.Listener;
import org.vidogram.messenger.exoplayer2.source.SinglePeriodTimeline;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PrimaryPlaylistListener;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class HlsMediaSource
  implements MediaSource, HlsPlaylistTracker.PrimaryPlaylistListener
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private final DataSource.Factory dataSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Uri manifestUri;
  private final int minLoadableRetryCount;
  private HlsPlaylistTracker playlistTracker;
  private MediaSource.Listener sourceListener;

  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, int paramInt, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this.manifestUri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventDispatcher = new AdaptiveMediaSourceEventListener.EventDispatcher(paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  public HlsMediaSource(Uri paramUri, DataSource.Factory paramFactory, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, 3, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      return new HlsMediaPeriod(this.playlistTracker, this.dataSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, paramAllocator, paramLong);
    }
  }

  public void maybeThrowSourceInfoRefreshError()
  {
    this.playlistTracker.maybeThrowPrimaryPlaylistRefreshError();
  }

  public void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    boolean bool = false;
    long l2;
    long l1;
    long l3;
    if (this.playlistTracker.isLive())
    {
      l2 = paramHlsMediaPlaylist.getStartTimeUs();
      localObject = paramHlsMediaPlaylist.segments;
      if (((List)localObject).isEmpty())
      {
        l1 = 0L;
        l3 = paramHlsMediaPlaylist.durationUs;
        if (!paramHlsMediaPlaylist.hasEndTag)
          bool = true;
      }
    }
    for (Object localObject = new SinglePeriodTimeline(-9223372036854775807L, l3, l2, l1, true, bool); ; localObject = new SinglePeriodTimeline(paramHlsMediaPlaylist.durationUs, paramHlsMediaPlaylist.durationUs, 0L, 0L, true, false))
    {
      this.sourceListener.onSourceInfoRefreshed((Timeline)localObject, paramHlsMediaPlaylist);
      return;
      l1 = ((HlsMediaPlaylist.Segment)((List)localObject).get(Math.max(0, ((List)localObject).size() - 3))).startTimeUs - l2;
      break;
    }
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    if (this.playlistTracker == null);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.playlistTracker = new HlsPlaylistTracker(this.manifestUri, this.dataSourceFactory, this.eventDispatcher, this.minLoadableRetryCount, this);
      this.sourceListener = paramListener;
      this.playlistTracker.start();
      return;
    }
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((HlsMediaPeriod)paramMediaPeriod).release();
  }

  public void releaseSource()
  {
    this.playlistTracker.release();
    this.playlistTracker = null;
    this.sourceListener = null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.HlsMediaSource
 * JD-Core Version:    0.6.0
 */