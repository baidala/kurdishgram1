package org.vidogram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class SingleSampleMediaSource
  implements MediaSource
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private final DataSource.Factory dataSourceFactory;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final int eventSourceId;
  private final Format format;
  private final int minLoadableRetryCount;
  private final Timeline timeline;
  private final Uri uri;

  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, 3);
  }

  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt)
  {
    this(paramUri, paramFactory, paramFormat, paramLong, paramInt, null, null, 0);
  }

  public SingleSampleMediaSource(Uri paramUri, DataSource.Factory paramFactory, Format paramFormat, long paramLong, int paramInt1, Handler paramHandler, EventListener paramEventListener, int paramInt2)
  {
    this.uri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.format = paramFormat;
    this.minLoadableRetryCount = paramInt1;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.eventSourceId = paramInt2;
    this.timeline = new SinglePeriodTimeline(paramLong, true);
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      return new SingleSampleMediaPeriod(this.uri, this.dataSourceFactory, this.format, this.minLoadableRetryCount, this.eventHandler, this.eventListener, this.eventSourceId);
    }
  }

  public void maybeThrowSourceInfoRefreshError()
  {
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    paramListener.onSourceInfoRefreshed(this.timeline, null);
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SingleSampleMediaPeriod)paramMediaPeriod).release();
  }

  public void releaseSource()
  {
  }

  public static abstract interface EventListener
  {
    public abstract void onLoadError(int paramInt, IOException paramIOException);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.SingleSampleMediaSource
 * JD-Core Version:    0.6.0
 */