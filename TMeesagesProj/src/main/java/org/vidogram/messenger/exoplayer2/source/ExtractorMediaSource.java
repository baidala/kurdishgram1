package org.vidogram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.Timeline.Period;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class ExtractorMediaSource
  implements MediaSource, MediaSource.Listener
{
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_LIVE = 6;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT_ON_DEMAND = 3;
  public static final int MIN_RETRY_COUNT_DEFAULT_FOR_MEDIA = -1;
  private final DataSource.Factory dataSourceFactory;
  private final Handler eventHandler;
  private final EventListener eventListener;
  private final ExtractorsFactory extractorsFactory;
  private final int minLoadableRetryCount;
  private final Timeline.Period period;
  private MediaSource.Listener sourceListener;
  private Timeline timeline;
  private boolean timelineHasDuration;
  private final Uri uri;

  public ExtractorMediaSource(Uri paramUri, DataSource.Factory paramFactory, ExtractorsFactory paramExtractorsFactory, int paramInt, Handler paramHandler, EventListener paramEventListener)
  {
    this.uri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.extractorsFactory = paramExtractorsFactory;
    this.minLoadableRetryCount = paramInt;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.period = new Timeline.Period();
  }

  public ExtractorMediaSource(Uri paramUri, DataSource.Factory paramFactory, ExtractorsFactory paramExtractorsFactory, Handler paramHandler, EventListener paramEventListener)
  {
    this(paramUri, paramFactory, paramExtractorsFactory, -1, paramHandler, paramEventListener);
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      return new ExtractorMediaPeriod(this.uri, this.dataSourceFactory.createDataSource(), this.extractorsFactory.createExtractors(), this.minLoadableRetryCount, this.eventHandler, this.eventListener, this, paramAllocator);
    }
  }

  public void maybeThrowSourceInfoRefreshError()
  {
  }

  public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    boolean bool = false;
    if (paramTimeline.getPeriod(0, this.period).getDurationUs() != -9223372036854775807L)
      bool = true;
    if ((this.timelineHasDuration) && (!bool))
      return;
    this.timeline = paramTimeline;
    this.timelineHasDuration = bool;
    this.sourceListener.onSourceInfoRefreshed(this.timeline, null);
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    this.sourceListener = paramListener;
    this.timeline = new SinglePeriodTimeline(-9223372036854775807L, false);
    paramListener.onSourceInfoRefreshed(this.timeline, null);
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((ExtractorMediaPeriod)paramMediaPeriod).release();
  }

  public void releaseSource()
  {
    this.sourceListener = null;
  }

  public static abstract interface EventListener
  {
    public abstract void onLoadError(IOException paramIOException);
  }

  public static final class UnrecognizedInputFormatException extends ParserException
  {
    public UnrecognizedInputFormatException(Extractor[] paramArrayOfExtractor)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.ExtractorMediaSource
 * JD-Core Version:    0.6.0
 */