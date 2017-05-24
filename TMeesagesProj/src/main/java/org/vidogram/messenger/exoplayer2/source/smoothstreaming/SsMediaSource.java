package org.vidogram.messenger.exoplayer2.source.smoothstreaming;

import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import java.io.IOException;
import java.util.ArrayList;
import org.vidogram.messenger.exoplayer2.C;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.vidogram.messenger.exoplayer2.source.MediaPeriod;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.MediaSource.Listener;
import org.vidogram.messenger.exoplayer2.source.SinglePeriodTimeline;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.upstream.Loader;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Callback;
import org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower.Dummy;
import org.vidogram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class SsMediaSource
  implements MediaSource, Loader.Callback<ParsingLoadable<SsManifest>>
{
  public static final long DEFAULT_LIVE_PRESENTATION_DELAY_MS = 30000L;
  public static final int DEFAULT_MIN_LOADABLE_RETRY_COUNT = 3;
  private static final int MINIMUM_MANIFEST_REFRESH_PERIOD_MS = 5000;
  private static final long MIN_LIVE_DEFAULT_START_POSITION_US = 5000000L;
  private final SsChunkSource.Factory chunkSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final long livePresentationDelayMs;
  private SsManifest manifest;
  private DataSource manifestDataSource;
  private final DataSource.Factory manifestDataSourceFactory;
  private long manifestLoadStartTimestamp;
  private Loader manifestLoader;
  private LoaderErrorThrower manifestLoaderErrorThrower;
  private final SsManifestParser manifestParser;
  private Handler manifestRefreshHandler;
  private final Uri manifestUri;
  private final ArrayList<SsMediaPeriod> mediaPeriods;
  private final int minLoadableRetryCount;
  private MediaSource.Listener sourceListener;

  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, new SsManifestParser(), paramFactory1, paramInt, paramLong, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsChunkSource.Factory paramFactory1, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramUri, paramFactory, paramFactory1, 3, 30000L, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  public SsMediaSource(Uri paramUri, DataSource.Factory paramFactory, SsManifestParser paramSsManifestParser, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(null, paramUri, paramFactory, paramSsManifestParser, paramFactory1, paramInt, paramLong, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  private SsMediaSource(SsManifest paramSsManifest, Uri paramUri, DataSource.Factory paramFactory, SsManifestParser paramSsManifestParser, SsChunkSource.Factory paramFactory1, int paramInt, long paramLong, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    boolean bool;
    if ((paramSsManifest == null) || (!paramSsManifest.isLive))
    {
      bool = true;
      Assertions.checkState(bool);
      this.manifest = paramSsManifest;
      if (paramUri != null)
        break label101;
      paramSsManifest = null;
    }
    while (true)
    {
      this.manifestUri = paramSsManifest;
      this.manifestDataSourceFactory = paramFactory;
      this.manifestParser = paramSsManifestParser;
      this.chunkSourceFactory = paramFactory1;
      this.minLoadableRetryCount = paramInt;
      this.livePresentationDelayMs = paramLong;
      this.eventDispatcher = new AdaptiveMediaSourceEventListener.EventDispatcher(paramHandler, paramAdaptiveMediaSourceEventListener);
      this.mediaPeriods = new ArrayList();
      return;
      bool = false;
      break;
      label101: paramSsManifest = paramUri;
      if (Util.toLowerInvariant(paramUri.getLastPathSegment()).equals("manifest"))
        continue;
      paramSsManifest = Uri.withAppendedPath(paramUri, "Manifest");
    }
  }

  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, int paramInt, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramSsManifest, null, null, null, paramFactory, paramInt, 30000L, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  public SsMediaSource(SsManifest paramSsManifest, SsChunkSource.Factory paramFactory, Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
  {
    this(paramSsManifest, paramFactory, 3, paramHandler, paramAdaptiveMediaSourceEventListener);
  }

  private void processManifest()
  {
    int i = 0;
    while (i < this.mediaPeriods.size())
    {
      ((SsMediaPeriod)this.mediaPeriods.get(i)).updateManifest(this.manifest);
      i += 1;
    }
    long l1;
    long l2;
    Object localObject;
    long l4;
    long l3;
    if (this.manifest.isLive)
    {
      l1 = 9223372036854775807L;
      l2 = -9223372036854775808L;
      i = 0;
      while (i < this.manifest.streamElements.length)
      {
        localObject = this.manifest.streamElements[i];
        l4 = l2;
        l3 = l1;
        if (((SsManifest.StreamElement)localObject).chunkCount > 0)
        {
          l3 = Math.min(l1, ((SsManifest.StreamElement)localObject).getStartTimeUs(0));
          l4 = Math.max(l2, ((SsManifest.StreamElement)localObject).getStartTimeUs(((SsManifest.StreamElement)localObject).chunkCount - 1) + ((SsManifest.StreamElement)localObject).getChunkDurationUs(((SsManifest.StreamElement)localObject).chunkCount - 1));
        }
        i += 1;
        l2 = l4;
        l1 = l3;
      }
      if (l1 == 9223372036854775807L)
      {
        localObject = new SinglePeriodTimeline(-9223372036854775807L, false);
        this.sourceListener.onSourceInfoRefreshed((Timeline)localObject, this.manifest);
        return;
      }
      if ((this.manifest.dvrWindowLengthUs == -9223372036854775807L) || (this.manifest.dvrWindowLengthUs <= 0L))
        break label344;
      l1 = Math.max(l1, l2 - this.manifest.dvrWindowLengthUs);
    }
    label344: 
    while (true)
    {
      l4 = l2 - l1;
      l3 = l4 - C.msToUs(this.livePresentationDelayMs);
      l2 = l3;
      if (l3 < 5000000L)
        l2 = Math.min(5000000L, l4 / 2L);
      localObject = new SinglePeriodTimeline(-9223372036854775807L, l4, l1, l2, true, true);
      break;
      if (this.manifest.durationUs != -9223372036854775807L);
      for (boolean bool = true; ; bool = false)
      {
        localObject = new SinglePeriodTimeline(this.manifest.durationUs, bool);
        break;
      }
    }
  }

  private void scheduleManifestRefresh()
  {
    if (!this.manifest.isLive)
      return;
    long l = Math.max(0L, this.manifestLoadStartTimestamp + 5000L - SystemClock.elapsedRealtime());
    this.manifestRefreshHandler.postDelayed(new Runnable()
    {
      public void run()
      {
        SsMediaSource.this.startLoadingManifest();
      }
    }
    , l);
  }

  private void startLoadingManifest()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(this.manifestDataSource, this.manifestUri, 4, this.manifestParser);
    long l = this.manifestLoader.startLoading(localParsingLoadable, this, this.minLoadableRetryCount);
    this.eventDispatcher.loadStarted(localParsingLoadable.dataSpec, localParsingLoadable.type, l);
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    if (paramInt == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      paramAllocator = new SsMediaPeriod(this.manifest, this.chunkSourceFactory, this.minLoadableRetryCount, this.eventDispatcher, this.manifestLoaderErrorThrower, paramAllocator);
      this.mediaPeriods.add(paramAllocator);
      return paramAllocator;
    }
  }

  public void maybeThrowSourceInfoRefreshError()
  {
    this.manifestLoaderErrorThrower.maybeThrowError();
  }

  public void onLoadCanceled(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }

  public void onLoadCompleted(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    this.manifest = ((SsManifest)paramParsingLoadable.getResult());
    this.manifestLoadStartTimestamp = (paramLong1 - paramLong2);
    processManifest();
    scheduleManifestRefresh();
  }

  public int onLoadError(ParsingLoadable<SsManifest> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, paramParsingLoadable.type, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool)
      return 3;
    return 0;
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    this.sourceListener = paramListener;
    if (this.manifest != null)
    {
      this.manifestLoaderErrorThrower = new LoaderErrorThrower.Dummy();
      processManifest();
      return;
    }
    this.manifestDataSource = this.manifestDataSourceFactory.createDataSource();
    this.manifestLoader = new Loader("Loader:Manifest");
    this.manifestLoaderErrorThrower = this.manifestLoader;
    this.manifestRefreshHandler = new Handler();
    startLoadingManifest();
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    ((SsMediaPeriod)paramMediaPeriod).release();
    this.mediaPeriods.remove(paramMediaPeriod);
  }

  public void releaseSource()
  {
    this.sourceListener = null;
    this.manifest = null;
    this.manifestDataSource = null;
    this.manifestLoadStartTimestamp = 0L;
    if (this.manifestLoader != null)
    {
      this.manifestLoader.release();
      this.manifestLoader = null;
    }
    if (this.manifestRefreshHandler != null)
    {
      this.manifestRefreshHandler.removeCallbacksAndMessages(null);
      this.manifestRefreshHandler = null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource
 * JD-Core Version:    0.6.0
 */