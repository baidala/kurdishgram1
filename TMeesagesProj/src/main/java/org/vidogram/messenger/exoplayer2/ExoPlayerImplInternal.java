package org.vidogram.messenger.exoplayer2;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.util.Pair;
import android.util.Pair<Lorg.vidogram.messenger.exoplayer2.Timeline;Ljava.lang.Object;>;
import org.vidogram.messenger.exoplayer2.source.MediaPeriod;
import org.vidogram.messenger.exoplayer2.source.MediaPeriod.Callback;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.MediaSource.Listener;
import org.vidogram.messenger.exoplayer2.source.SampleStream;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelector;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelector.InvalidationListener;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.MediaClock;
import org.vidogram.messenger.exoplayer2.util.PriorityHandlerThread;
import org.vidogram.messenger.exoplayer2.util.StandaloneMediaClock;
import org.vidogram.messenger.exoplayer2.util.TraceUtil;
import org.vidogram.messenger.exoplayer2.util.Util;

final class ExoPlayerImplInternal
  implements Handler.Callback, MediaPeriod.Callback, MediaSource.Listener, TrackSelector.InvalidationListener
{
  private static final int IDLE_INTERVAL_MS = 1000;
  private static final int MAXIMUM_BUFFER_AHEAD_PERIODS = 100;
  private static final int MSG_CUSTOM = 10;
  private static final int MSG_DO_SOME_WORK = 2;
  public static final int MSG_ERROR = 7;
  public static final int MSG_LOADING_CHANGED = 2;
  private static final int MSG_PERIOD_PREPARED = 7;
  public static final int MSG_POSITION_DISCONTINUITY = 5;
  private static final int MSG_PREPARE = 0;
  private static final int MSG_REFRESH_SOURCE_INFO = 6;
  private static final int MSG_RELEASE = 5;
  public static final int MSG_SEEK_ACK = 4;
  private static final int MSG_SEEK_TO = 3;
  private static final int MSG_SET_PLAY_WHEN_READY = 1;
  private static final int MSG_SOURCE_CONTINUE_LOADING_REQUESTED = 8;
  public static final int MSG_SOURCE_INFO_REFRESHED = 6;
  public static final int MSG_STATE_CHANGED = 1;
  private static final int MSG_STOP = 4;
  public static final int MSG_TRACKS_CHANGED = 3;
  private static final int MSG_TRACK_SELECTION_INVALIDATED = 9;
  private static final int PREPARING_SOURCE_INTERVAL_MS = 10;
  private static final int RENDERING_INTERVAL_MS = 10;
  private static final String TAG = "ExoPlayerImplInternal";
  private int customMessagesProcessed;
  private int customMessagesSent;
  private long elapsedRealtimeUs;
  private Renderer[] enabledRenderers;
  private final Handler eventHandler;
  private final Handler handler;
  private final HandlerThread internalPlaybackThread;
  private boolean isLoading;
  private final LoadControl loadControl;
  private MediaPeriodHolder loadingPeriodHolder;
  private MediaSource mediaSource;
  private int pendingInitialSeekCount;
  private SeekPosition pendingSeekPosition;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private PlaybackInfo playbackInfo;
  private MediaPeriodHolder playingPeriodHolder;
  private MediaPeriodHolder readingPeriodHolder;
  private boolean rebuffering;
  private boolean released;
  private final RendererCapabilities[] rendererCapabilities;
  private MediaClock rendererMediaClock;
  private Renderer rendererMediaClockSource;
  private long rendererPositionUs;
  private final Renderer[] renderers;
  private final StandaloneMediaClock standaloneMediaClock;
  private int state;
  private Timeline timeline;
  private final TrackSelector trackSelector;
  private final Timeline.Window window;

  public ExoPlayerImplInternal(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl, boolean paramBoolean, Handler paramHandler, PlaybackInfo paramPlaybackInfo)
  {
    this.renderers = paramArrayOfRenderer;
    this.trackSelector = paramTrackSelector;
    this.loadControl = paramLoadControl;
    this.playWhenReady = paramBoolean;
    this.eventHandler = paramHandler;
    this.state = 1;
    this.playbackInfo = paramPlaybackInfo;
    this.rendererCapabilities = new RendererCapabilities[paramArrayOfRenderer.length];
    int i = 0;
    while (i < paramArrayOfRenderer.length)
    {
      paramArrayOfRenderer[i].setIndex(i);
      this.rendererCapabilities[i] = paramArrayOfRenderer[i].getCapabilities();
      i += 1;
    }
    this.standaloneMediaClock = new StandaloneMediaClock();
    this.enabledRenderers = new Renderer[0];
    this.window = new Timeline.Window();
    this.period = new Timeline.Period();
    paramTrackSelector.init(this);
    this.internalPlaybackThread = new PriorityHandlerThread("ExoPlayerImplInternal:Handler", -16);
    this.internalPlaybackThread.start();
    this.handler = new Handler(this.internalPlaybackThread.getLooper(), this);
  }

  private void doSomeWork()
  {
    long l1 = SystemClock.elapsedRealtime();
    updatePeriods();
    if (this.playingPeriodHolder == null)
    {
      maybeThrowPeriodPrepareError();
      scheduleNextWork(l1, 10L);
      return;
    }
    TraceUtil.beginSection("doSomeWork");
    updatePlaybackPositions();
    boolean bool = true;
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int m = arrayOfRenderer.length;
    int j = 0;
    int i = 1;
    if (j < m)
    {
      Renderer localRenderer = arrayOfRenderer[j];
      localRenderer.render(this.rendererPositionUs, this.elapsedRealtimeUs);
      label100: int k;
      if ((i != 0) && (localRenderer.isEnded()))
      {
        i = 1;
        if ((!localRenderer.isReady()) && (!localRenderer.isEnded()))
          break label157;
        k = 1;
        label122: if (k == 0)
          localRenderer.maybeThrowStreamError();
        if ((!bool) || (k == 0))
          break label162;
      }
      label157: label162: for (bool = true; ; bool = false)
      {
        j += 1;
        break;
        i = 0;
        break label100;
        k = 0;
        break label122;
      }
    }
    if (!bool)
      maybeThrowPeriodPrepareError();
    long l2 = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
    if ((i != 0) && ((l2 == -9223372036854775807L) || (l2 <= this.playbackInfo.positionUs)) && (this.playingPeriodHolder.isLast))
    {
      setState(4);
      stopRenderers();
    }
    label412: 
    while (this.state == 2)
    {
      arrayOfRenderer = this.enabledRenderers;
      j = arrayOfRenderer.length;
      i = 0;
      while (i < j)
      {
        arrayOfRenderer[i].maybeThrowStreamError();
        i += 1;
      }
      if (this.state == 2)
      {
        if (this.enabledRenderers.length > 0)
          if ((bool) && (haveSufficientBuffer(this.rebuffering)))
            bool = true;
        while (true)
        {
          if (!bool)
            break label360;
          setState(3);
          if (!this.playWhenReady)
            break;
          startRenderers();
          break;
          bool = false;
          continue;
          bool = isTimelineReady(l2);
        }
        label360: continue;
      }
      if (this.state != 3)
        continue;
      if (this.enabledRenderers.length > 0);
      while (true)
      {
        if (bool)
          break label412;
        this.rebuffering = this.playWhenReady;
        setState(2);
        stopRenderers();
        break;
        bool = isTimelineReady(l2);
      }
    }
    if (((this.playWhenReady) && (this.state == 3)) || (this.state == 2))
      scheduleNextWork(l1, 10L);
    while (true)
    {
      TraceUtil.endSection();
      return;
      if (this.enabledRenderers.length != 0)
      {
        scheduleNextWork(l1, 1000L);
        continue;
      }
      this.handler.removeMessages(2);
    }
  }

  private void enableRenderers(boolean[] paramArrayOfBoolean, int paramInt)
  {
    this.enabledRenderers = new Renderer[paramInt];
    paramInt = 0;
    int j;
    for (int i = 0; paramInt < this.renderers.length; i = j)
    {
      Renderer localRenderer = this.renderers[paramInt];
      Object localObject = this.playingPeriodHolder.trackSelections.get(paramInt);
      j = i;
      if (localObject != null)
      {
        this.enabledRenderers[i] = localRenderer;
        if (localRenderer.getState() == 0)
        {
          if ((this.playWhenReady) && (this.state == 3))
          {
            j = 1;
            if ((paramArrayOfBoolean[paramInt] != 0) || (j == 0))
              break label152;
          }
          Format[] arrayOfFormat;
          label152: for (boolean bool = true; ; bool = false)
          {
            arrayOfFormat = new Format[((TrackSelection)localObject).length()];
            int k = 0;
            while (k < arrayOfFormat.length)
            {
              arrayOfFormat[k] = ((TrackSelection)localObject).getFormat(k);
              k += 1;
            }
            j = 0;
            break;
          }
          localRenderer.enable(arrayOfFormat, this.playingPeriodHolder.sampleStreams[paramInt], this.rendererPositionUs, bool, this.playingPeriodHolder.getRendererOffset());
          localObject = localRenderer.getMediaClock();
          if (localObject != null)
          {
            if (this.rendererMediaClock != null)
              throw ExoPlaybackException.createForUnexpected(new IllegalStateException("Multiple renderer media clocks enabled."));
            this.rendererMediaClock = ((MediaClock)localObject);
            this.rendererMediaClockSource = localRenderer;
          }
          if (j != 0)
            localRenderer.start();
        }
        j = i + 1;
      }
      paramInt += 1;
    }
  }

  private void ensureStopped(Renderer paramRenderer)
  {
    if (paramRenderer.getState() == 2)
      paramRenderer.stop();
  }

  private Pair<Integer, Long> getPeriodPosition(int paramInt, long paramLong)
  {
    return getPeriodPosition(this.timeline, paramInt, paramLong);
  }

  private Pair<Integer, Long> getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong)
  {
    return getPeriodPosition(paramTimeline, paramInt, paramLong, 0L);
  }

  private Pair<Integer, Long> getPeriodPosition(Timeline paramTimeline, int paramInt, long paramLong1, long paramLong2)
  {
    paramTimeline.getWindow(paramInt, this.window, false, paramLong2);
    paramLong2 = paramLong1;
    if (paramLong1 == -9223372036854775807L)
    {
      paramLong1 = this.window.getDefaultPositionUs();
      paramLong2 = paramLong1;
      if (paramLong1 == -9223372036854775807L)
        return null;
    }
    paramInt = this.window.firstPeriodIndex;
    paramLong2 = this.window.getPositionInFirstPeriodUs() + paramLong2;
    paramLong1 = paramTimeline.getPeriod(paramInt, this.period).getDurationUs();
    while ((paramLong1 != -9223372036854775807L) && (paramLong2 >= paramLong1) && (paramInt < this.window.lastPeriodIndex))
    {
      paramLong2 -= paramLong1;
      paramInt += 1;
      paramLong1 = paramTimeline.getPeriod(paramInt, this.period).getDurationUs();
    }
    return Pair.create(Integer.valueOf(paramInt), Long.valueOf(paramLong2));
  }

  private void handleContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    if ((this.loadingPeriodHolder == null) || (this.loadingPeriodHolder.mediaPeriod != paramMediaPeriod))
      return;
    maybeContinueLoading();
  }

  private void handlePeriodPrepared(MediaPeriod paramMediaPeriod)
  {
    if ((this.loadingPeriodHolder == null) || (this.loadingPeriodHolder.mediaPeriod != paramMediaPeriod))
      return;
    this.loadingPeriodHolder.handlePrepared();
    if (this.playingPeriodHolder == null)
    {
      this.readingPeriodHolder = this.loadingPeriodHolder;
      resetRendererPosition(this.readingPeriodHolder.startPositionUs);
      setPlayingPeriodHolder(this.readingPeriodHolder);
    }
    maybeContinueLoading();
  }

  private void handleSourceInfoRefreshed(Pair<Timeline, Object> paramPair)
  {
    Object localObject1 = this.timeline;
    this.timeline = ((Timeline)paramPair.first);
    Object localObject2 = paramPair.second;
    int j;
    if (localObject1 == null)
      if (this.pendingInitialSeekCount > 0)
      {
        paramPair = resolveSeekPosition(this.pendingSeekPosition);
        if (paramPair == null)
        {
          notifySourceInfoRefresh(localObject2, 0);
          stopInternal();
          return;
        }
        this.playbackInfo = new PlaybackInfo(((Integer)paramPair.first).intValue(), ((Long)paramPair.second).longValue());
        j = this.pendingInitialSeekCount;
        this.pendingInitialSeekCount = 0;
        this.pendingSeekPosition = null;
        if (this.playingPeriodHolder == null)
          break label189;
      }
    label189: for (paramPair = this.playingPeriodHolder; ; paramPair = this.loadingPeriodHolder)
    {
      if (paramPair != null)
        break label197;
      notifySourceInfoRefresh(localObject2, j);
      return;
      if (this.playbackInfo.startPositionUs == -9223372036854775807L)
      {
        paramPair = getPeriodPosition(0, -9223372036854775807L);
        this.playbackInfo = new PlaybackInfo(((Integer)paramPair.first).intValue(), ((Long)paramPair.second).longValue());
      }
      j = 0;
      break;
    }
    label197: int k = this.timeline.getIndexOfPeriod(paramPair.uid);
    int i;
    if (k == -1)
    {
      i = resolveSubsequentPeriod(paramPair.index, (Timeline)localObject1, this.timeline);
      if (i == -1)
      {
        notifySourceInfoRefresh(localObject2, j);
        stopInternal();
        return;
      }
      localObject1 = getPeriodPosition(this.timeline.getPeriod(i, this.period).windowIndex, -9223372036854775807L);
      k = ((Integer)((Pair)localObject1).first).intValue();
      long l = ((Long)((Pair)localObject1).second).longValue();
      this.timeline.getPeriod(k, this.period, true);
      localObject1 = this.period.uid;
      paramPair.index = -1;
      if (paramPair.next != null)
      {
        paramPair = paramPair.next;
        if (paramPair.uid.equals(localObject1));
        for (i = k; ; i = -1)
        {
          paramPair.index = i;
          break;
        }
      }
      this.playbackInfo = new PlaybackInfo(k, seekToPeriodPosition(k, l));
      notifySourceInfoRefresh(localObject2, j);
      return;
    }
    this.timeline.getPeriod(k, this.period);
    boolean bool;
    label469: label494: int m;
    if ((k == this.timeline.getPeriodCount() - 1) && (!this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic))
    {
      bool = true;
      paramPair.setIndex(k, bool);
      if (paramPair != this.readingPeriodHolder)
        break label629;
      i = 1;
      if (k != this.playbackInfo.periodIndex)
        this.playbackInfo = this.playbackInfo.copyWithPeriodIndex(k);
      if (paramPair.next == null)
        break label682;
      localObject1 = paramPair.next;
      m = k + 1;
      this.timeline.getPeriod(m, this.period, true);
      if ((m != this.timeline.getPeriodCount() - 1) || (this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic))
        break label634;
      bool = true;
      label569: if (!((MediaPeriodHolder)localObject1).uid.equals(this.period.uid))
        break label646;
      ((MediaPeriodHolder)localObject1).setIndex(m, bool);
      if (localObject1 != this.readingPeriodHolder)
        break label640;
    }
    label640: for (k = 1; ; k = 0)
    {
      i |= k;
      paramPair = (Pair<Timeline, Object>)localObject1;
      k = m;
      break label494;
      bool = false;
      break;
      label629: i = 0;
      break label469;
      label634: bool = false;
      break label569;
    }
    label646: if (i == 0)
    {
      i = this.playingPeriodHolder.index;
      this.playbackInfo = new PlaybackInfo(i, seekToPeriodPosition(i, this.playbackInfo.positionUs));
    }
    while (true)
    {
      label682: notifySourceInfoRefresh(localObject2, j);
      return;
      this.loadingPeriodHolder = paramPair;
      this.loadingPeriodHolder.next = null;
      releasePeriodHoldersFrom((MediaPeriodHolder)localObject1);
    }
  }

  private boolean haveSufficientBuffer(boolean paramBoolean)
  {
    if (this.loadingPeriodHolder == null)
      return false;
    long l1;
    if (!this.loadingPeriodHolder.prepared)
      l1 = this.loadingPeriodHolder.startPositionUs;
    long l2;
    while (true)
    {
      l2 = l1;
      if (l1 != -9223372036854775808L)
        break;
      if (this.loadingPeriodHolder.isLast)
      {
        return true;
        l1 = this.loadingPeriodHolder.mediaPeriod.getBufferedPositionUs();
        continue;
      }
      l2 = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
    }
    return this.loadControl.shouldStartPlayback(l2 - this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs), paramBoolean);
  }

  private boolean isTimelineReady(long paramLong)
  {
    return (paramLong == -9223372036854775807L) || (this.playbackInfo.positionUs < paramLong) || ((this.playingPeriodHolder.next != null) && (this.playingPeriodHolder.next.prepared));
  }

  private void maybeContinueLoading()
  {
    long l1 = this.loadingPeriodHolder.mediaPeriod.getNextLoadPositionUs();
    if (l1 == -9223372036854775808L)
    {
      setIsLoading(false);
      return;
    }
    long l2 = this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs);
    boolean bool = this.loadControl.shouldContinueLoading(l1 - l2);
    setIsLoading(bool);
    if (bool)
    {
      this.loadingPeriodHolder.needsContinueLoading = false;
      this.loadingPeriodHolder.mediaPeriod.continueLoading(l2);
      return;
    }
    this.loadingPeriodHolder.needsContinueLoading = true;
  }

  private void maybeThrowPeriodPrepareError()
  {
    Renderer[] arrayOfRenderer;
    int j;
    int i;
    if ((this.loadingPeriodHolder != null) && (!this.loadingPeriodHolder.prepared) && ((this.readingPeriodHolder == null) || (this.readingPeriodHolder.next == this.loadingPeriodHolder)))
    {
      arrayOfRenderer = this.enabledRenderers;
      j = arrayOfRenderer.length;
      i = 0;
    }
    while (i < j)
    {
      if (!arrayOfRenderer[i].hasReadStreamToEnd())
        return;
      i += 1;
    }
    this.loadingPeriodHolder.mediaPeriod.maybeThrowPrepareError();
  }

  private void notifySourceInfoRefresh(Object paramObject, int paramInt)
  {
    this.eventHandler.obtainMessage(6, new SourceInfo(this.timeline, paramObject, this.playbackInfo, paramInt)).sendToTarget();
  }

  private void prepareInternal(MediaSource paramMediaSource, boolean paramBoolean)
  {
    resetInternal();
    this.loadControl.onPrepared();
    if (paramBoolean)
      this.playbackInfo = new PlaybackInfo(0, -9223372036854775807L);
    this.mediaSource = paramMediaSource;
    paramMediaSource.prepareSource(this);
    setState(2);
    this.handler.sendEmptyMessage(2);
  }

  private void releaseInternal()
  {
    resetInternal();
    this.loadControl.onReleased();
    setState(1);
    monitorenter;
    try
    {
      this.released = true;
      notifyAll();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private void releasePeriodHoldersFrom(MediaPeriodHolder paramMediaPeriodHolder)
  {
    while (paramMediaPeriodHolder != null)
    {
      paramMediaPeriodHolder.release();
      paramMediaPeriodHolder = paramMediaPeriodHolder.next;
    }
  }

  private void reselectTracksInternal()
  {
    if (this.playingPeriodHolder == null)
      return;
    MediaPeriodHolder localMediaPeriodHolder = this.playingPeriodHolder;
    int i = 1;
    label16: boolean bool;
    label55: boolean[] arrayOfBoolean1;
    long l;
    boolean[] arrayOfBoolean2;
    int j;
    label160: Renderer localRenderer;
    label190: int k;
    if ((localMediaPeriodHolder != null) && (localMediaPeriodHolder.prepared))
      if (localMediaPeriodHolder.selectTracks())
      {
        if (i == 0)
          break label394;
        if (this.readingPeriodHolder == this.playingPeriodHolder)
          break label320;
        bool = true;
        releasePeriodHoldersFrom(this.playingPeriodHolder.next);
        this.playingPeriodHolder.next = null;
        this.loadingPeriodHolder = this.playingPeriodHolder;
        this.readingPeriodHolder = this.playingPeriodHolder;
        arrayOfBoolean1 = new boolean[this.renderers.length];
        l = this.playingPeriodHolder.updatePeriodTrackSelection(this.playbackInfo.positionUs, bool, arrayOfBoolean1);
        if (l != this.playbackInfo.positionUs)
        {
          this.playbackInfo.positionUs = l;
          resetRendererPosition(l);
        }
        arrayOfBoolean2 = new boolean[this.renderers.length];
        i = 0;
        j = 0;
        if (i >= this.renderers.length)
          break label353;
        localRenderer = this.renderers[i];
        if (localRenderer.getState() == 0)
          break label326;
        bool = true;
        arrayOfBoolean2[i] = bool;
        SampleStream localSampleStream = this.playingPeriodHolder.sampleStreams[i];
        k = j;
        if (localSampleStream != null)
          k = j + 1;
        if (arrayOfBoolean2[i] != 0)
        {
          if (localSampleStream == localRenderer.getStream())
            break label332;
          if (localRenderer == this.rendererMediaClockSource)
          {
            if (localSampleStream == null)
              this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
            this.rendererMediaClock = null;
            this.rendererMediaClockSource = null;
          }
          ensureStopped(localRenderer);
          localRenderer.disable();
        }
      }
    while (true)
    {
      i += 1;
      j = k;
      break label160;
      if (localMediaPeriodHolder == this.readingPeriodHolder)
        i = 0;
      localMediaPeriodHolder = localMediaPeriodHolder.next;
      break label16;
      break;
      label320: bool = false;
      break label55;
      label326: bool = false;
      break label190;
      label332: if (arrayOfBoolean1[i] == 0)
        continue;
      localRenderer.resetPosition(this.rendererPositionUs);
    }
    label353: this.eventHandler.obtainMessage(3, localMediaPeriodHolder.getTrackInfo()).sendToTarget();
    enableRenderers(arrayOfBoolean2, j);
    while (true)
    {
      maybeContinueLoading();
      updatePlaybackPositions();
      this.handler.sendEmptyMessage(2);
      return;
      label394: this.loadingPeriodHolder = localMediaPeriodHolder;
      for (localMediaPeriodHolder = this.loadingPeriodHolder.next; localMediaPeriodHolder != null; localMediaPeriodHolder = localMediaPeriodHolder.next)
        localMediaPeriodHolder.release();
      this.loadingPeriodHolder.next = null;
      if (!this.loadingPeriodHolder.prepared)
        continue;
      l = Math.max(this.loadingPeriodHolder.startPositionUs, this.loadingPeriodHolder.toPeriodTime(this.rendererPositionUs));
      this.loadingPeriodHolder.updatePeriodTrackSelection(l, false);
    }
  }

  private void resetInternal()
  {
    this.handler.removeMessages(2);
    this.rebuffering = false;
    this.standaloneMediaClock.stop();
    this.rendererMediaClock = null;
    this.rendererMediaClockSource = null;
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (true)
    {
      Renderer localRenderer;
      if (i < j)
        localRenderer = arrayOfRenderer[i];
      try
      {
        ensureStopped(localRenderer);
        localRenderer.disable();
        i += 1;
      }
      catch (RuntimeException localMediaPeriodHolder)
      {
        while (true)
          Log.e("ExoPlayerImplInternal", "Stop failed.", localRuntimeException);
        this.enabledRenderers = new Renderer[0];
        if (this.playingPeriodHolder != null);
        for (MediaPeriodHolder localMediaPeriodHolder = this.playingPeriodHolder; ; localMediaPeriodHolder = this.loadingPeriodHolder)
        {
          releasePeriodHoldersFrom(localMediaPeriodHolder);
          if (this.mediaSource != null)
          {
            this.mediaSource.releaseSource();
            this.mediaSource = null;
          }
          this.loadingPeriodHolder = null;
          this.readingPeriodHolder = null;
          this.playingPeriodHolder = null;
          this.timeline = null;
          setIsLoading(false);
          return;
        }
      }
      catch (ExoPlaybackException localExoPlaybackException)
      {
        label71: break label71;
      }
    }
  }

  private void resetRendererPosition(long paramLong)
  {
    if (this.playingPeriodHolder == null);
    while (true)
    {
      this.rendererPositionUs = paramLong;
      this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
      Renderer[] arrayOfRenderer = this.enabledRenderers;
      int j = arrayOfRenderer.length;
      int i = 0;
      while (i < j)
      {
        arrayOfRenderer[i].resetPosition(this.rendererPositionUs);
        i += 1;
      }
      paramLong = this.playingPeriodHolder.toRendererTime(paramLong);
    }
  }

  private Pair<Integer, Long> resolveSeekPosition(SeekPosition paramSeekPosition)
  {
    Timeline localTimeline2 = paramSeekPosition.timeline;
    Timeline localTimeline1 = localTimeline2;
    if (localTimeline2.isEmpty())
    {
      localTimeline1 = this.timeline;
      Assertions.checkIndex(paramSeekPosition.windowIndex, 0, this.timeline.getWindowCount());
    }
    paramSeekPosition = getPeriodPosition(localTimeline1, paramSeekPosition.windowIndex, paramSeekPosition.windowPositionUs);
    if (this.timeline == localTimeline1)
      return paramSeekPosition;
    int i = this.timeline.getIndexOfPeriod(localTimeline1.getPeriod(((Integer)paramSeekPosition.first).intValue(), this.period, true).uid);
    if (i != -1)
      return Pair.create(Integer.valueOf(i), paramSeekPosition.second);
    i = resolveSubsequentPeriod(((Integer)paramSeekPosition.first).intValue(), localTimeline1, this.timeline);
    if (i != -1)
      return getPeriodPosition(this.timeline.getPeriod(i, this.period).windowIndex, -9223372036854775807L);
    return null;
  }

  private int resolveSubsequentPeriod(int paramInt, Timeline paramTimeline1, Timeline paramTimeline2)
  {
    int j = -1;
    int i = paramInt;
    for (paramInt = j; (paramInt == -1) && (i < paramTimeline1.getPeriodCount() - 1); paramInt = paramTimeline2.getIndexOfPeriod(paramTimeline1.getPeriod(i, this.period, true).uid))
      i += 1;
    return paramInt;
  }

  private void scheduleNextWork(long paramLong1, long paramLong2)
  {
    this.handler.removeMessages(2);
    paramLong1 = paramLong1 + paramLong2 - SystemClock.elapsedRealtime();
    if (paramLong1 <= 0L)
    {
      this.handler.sendEmptyMessage(2);
      return;
    }
    this.handler.sendEmptyMessageDelayed(2, paramLong1);
  }

  private void seekToInternal(SeekPosition paramSeekPosition)
  {
    if (this.timeline == null)
    {
      this.pendingInitialSeekCount += 1;
      this.pendingSeekPosition = paramSeekPosition;
      return;
    }
    paramSeekPosition = resolveSeekPosition(paramSeekPosition);
    if (paramSeekPosition == null)
    {
      stopInternal();
      return;
    }
    int i = ((Integer)paramSeekPosition.first).intValue();
    long l1 = ((Long)paramSeekPosition.second).longValue();
    try
    {
      if (i == this.playbackInfo.periodIndex)
      {
        l2 = l1 / 1000L;
        long l3 = this.playbackInfo.positionUs / 1000L;
        if (l2 == l3)
          return;
      }
      long l2 = seekToPeriodPosition(i, l1);
      return;
    }
    finally
    {
      this.playbackInfo = new PlaybackInfo(i, l1);
      this.eventHandler.obtainMessage(4, this.playbackInfo).sendToTarget();
    }
    throw paramSeekPosition;
  }

  private long seekToPeriodPosition(int paramInt, long paramLong)
  {
    stopRenderers();
    this.rebuffering = false;
    setState(2);
    Object localObject2;
    if (this.playingPeriodHolder == null)
    {
      if (this.loadingPeriodHolder == null)
        break label272;
      this.loadingPeriodHolder.release();
      localObject2 = null;
    }
    while (true)
    {
      if ((this.playingPeriodHolder != localObject2) || (this.playingPeriodHolder != this.readingPeriodHolder))
      {
        Object localObject1 = this.enabledRenderers;
        int i = localObject1.length;
        paramInt = 0;
        while (true)
          if (paramInt < i)
          {
            localObject1[paramInt].disable();
            paramInt += 1;
            continue;
            localObject1 = this.playingPeriodHolder;
            Object localObject3 = null;
            localObject2 = localObject3;
            if (localObject1 == null)
              break;
            if ((((MediaPeriodHolder)localObject1).index == paramInt) && (((MediaPeriodHolder)localObject1).prepared))
              localObject3 = localObject1;
            while (true)
            {
              localObject1 = ((MediaPeriodHolder)localObject1).next;
              break;
              ((MediaPeriodHolder)localObject1).release();
            }
          }
        this.enabledRenderers = new Renderer[0];
        this.rendererMediaClock = null;
        this.rendererMediaClockSource = null;
      }
      if (localObject2 != null)
      {
        localObject2.next = null;
        this.loadingPeriodHolder = localObject2;
        this.readingPeriodHolder = localObject2;
        setPlayingPeriodHolder(localObject2);
        long l = paramLong;
        if (this.playingPeriodHolder.hasEnabledTracks)
          l = this.playingPeriodHolder.mediaPeriod.seekToUs(paramLong);
        resetRendererPosition(l);
        maybeContinueLoading();
        paramLong = l;
      }
      while (true)
      {
        this.handler.sendEmptyMessage(2);
        return paramLong;
        this.loadingPeriodHolder = null;
        this.readingPeriodHolder = null;
        this.playingPeriodHolder = null;
        resetRendererPosition(paramLong);
      }
      label272: localObject2 = null;
    }
  }

  // ERROR //
  private void sendMessagesInternal(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    // Byte code:
    //   0: aload_1
    //   1: arraylength
    //   2: istore_3
    //   3: iconst_0
    //   4: istore_2
    //   5: iload_2
    //   6: iload_3
    //   7: if_icmpge +35 -> 42
    //   10: aload_1
    //   11: iload_2
    //   12: aaload
    //   13: astore 4
    //   15: aload 4
    //   17: getfield 703	org/vidogram/messenger/exoplayer2/ExoPlayer$ExoPlayerMessage:target	Lorg/vidogram/messenger/exoplayer2/ExoPlayer$ExoPlayerComponent;
    //   20: aload 4
    //   22: getfield 706	org/vidogram/messenger/exoplayer2/ExoPlayer$ExoPlayerMessage:messageType	I
    //   25: aload 4
    //   27: getfield 709	org/vidogram/messenger/exoplayer2/ExoPlayer$ExoPlayerMessage:message	Ljava/lang/Object;
    //   30: invokeinterface 715 3 0
    //   35: iload_2
    //   36: iconst_1
    //   37: iadd
    //   38: istore_2
    //   39: goto -34 -> 5
    //   42: aload_0
    //   43: getfield 583	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:mediaSource	Lorg/vidogram/messenger/exoplayer2/source/MediaSource;
    //   46: ifnull +12 -> 58
    //   49: aload_0
    //   50: getfield 196	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:handler	Landroid/os/Handler;
    //   53: iconst_2
    //   54: invokevirtual 593	android/os/Handler:sendEmptyMessage	(I)Z
    //   57: pop
    //   58: aload_0
    //   59: monitorenter
    //   60: aload_0
    //   61: aload_0
    //   62: getfield 717	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   65: iconst_1
    //   66: iadd
    //   67: putfield 717	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   70: aload_0
    //   71: invokevirtual 602	java/lang/Object:notifyAll	()V
    //   74: aload_0
    //   75: monitorexit
    //   76: return
    //   77: astore_1
    //   78: aload_0
    //   79: monitorexit
    //   80: aload_1
    //   81: athrow
    //   82: astore_1
    //   83: aload_0
    //   84: monitorenter
    //   85: aload_0
    //   86: aload_0
    //   87: getfield 717	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   90: iconst_1
    //   91: iadd
    //   92: putfield 717	org/vidogram/messenger/exoplayer2/ExoPlayerImplInternal:customMessagesProcessed	I
    //   95: aload_0
    //   96: invokevirtual 602	java/lang/Object:notifyAll	()V
    //   99: aload_0
    //   100: monitorexit
    //   101: aload_1
    //   102: athrow
    //   103: astore_1
    //   104: aload_0
    //   105: monitorexit
    //   106: aload_1
    //   107: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   60	76	77	finally
    //   78	80	77	finally
    //   0	3	82	finally
    //   15	35	82	finally
    //   42	58	82	finally
    //   85	101	103	finally
    //   104	106	103	finally
  }

  private void setIsLoading(boolean paramBoolean)
  {
    Handler localHandler;
    if (this.isLoading != paramBoolean)
    {
      this.isLoading = paramBoolean;
      localHandler = this.eventHandler;
      if (!paramBoolean)
        break label35;
    }
    label35: for (int i = 1; ; i = 0)
    {
      localHandler.obtainMessage(2, i, 0).sendToTarget();
      return;
    }
  }

  private void setPlayWhenReadyInternal(boolean paramBoolean)
  {
    this.rebuffering = false;
    this.playWhenReady = paramBoolean;
    if (!paramBoolean)
    {
      stopRenderers();
      updatePlaybackPositions();
    }
    do
    {
      return;
      if (this.state != 3)
        continue;
      startRenderers();
      this.handler.sendEmptyMessage(2);
      return;
    }
    while (this.state != 2);
    this.handler.sendEmptyMessage(2);
  }

  private void setPlayingPeriodHolder(MediaPeriodHolder paramMediaPeriodHolder)
  {
    this.playingPeriodHolder = paramMediaPeriodHolder;
    boolean[] arrayOfBoolean = new boolean[this.renderers.length];
    int i = 0;
    int j = 0;
    if (i < this.renderers.length)
    {
      Renderer localRenderer = this.renderers[i];
      int m;
      label48: int k;
      if (localRenderer.getState() != 0)
      {
        m = 1;
        arrayOfBoolean[i] = m;
        if (paramMediaPeriodHolder.trackSelections.get(i) == null)
          break label86;
        k = j + 1;
      }
      while (true)
      {
        i += 1;
        j = k;
        break;
        m = 0;
        break label48;
        label86: k = j;
        if (arrayOfBoolean[i] == 0)
          continue;
        if (localRenderer == this.rendererMediaClockSource)
        {
          this.standaloneMediaClock.setPositionUs(this.rendererMediaClock.getPositionUs());
          this.rendererMediaClock = null;
          this.rendererMediaClockSource = null;
        }
        ensureStopped(localRenderer);
        localRenderer.disable();
        k = j;
      }
    }
    this.eventHandler.obtainMessage(3, paramMediaPeriodHolder.getTrackInfo()).sendToTarget();
    enableRenderers(arrayOfBoolean, j);
  }

  private void setState(int paramInt)
  {
    if (this.state != paramInt)
    {
      this.state = paramInt;
      this.eventHandler.obtainMessage(1, paramInt, 0).sendToTarget();
    }
  }

  private void startRenderers()
  {
    int i = 0;
    this.rebuffering = false;
    this.standaloneMediaClock.start();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    while (i < j)
    {
      arrayOfRenderer[i].start();
      i += 1;
    }
  }

  private void stopInternal()
  {
    resetInternal();
    this.loadControl.onStopped();
    setState(1);
  }

  private void stopRenderers()
  {
    this.standaloneMediaClock.stop();
    Renderer[] arrayOfRenderer = this.enabledRenderers;
    int j = arrayOfRenderer.length;
    int i = 0;
    while (i < j)
    {
      ensureStopped(arrayOfRenderer[i]);
      i += 1;
    }
  }

  private void updatePeriods()
  {
    if (this.timeline == null)
      this.mediaSource.maybeThrowSourceInfoRefreshError();
    label242: int j;
    label279: label295: label311: label456: label607: label613: label635: 
    do
    {
      while (true)
      {
        return;
        if ((this.loadingPeriodHolder == null) || ((this.loadingPeriodHolder.isFullyBuffered()) && (!this.loadingPeriodHolder.isLast) && ((this.playingPeriodHolder == null) || (this.loadingPeriodHolder.index - this.playingPeriodHolder.index < 100))))
        {
          if (this.loadingPeriodHolder == null)
          {
            i = this.playbackInfo.periodIndex;
            if (i < this.timeline.getPeriodCount())
              break label242;
            this.mediaSource.maybeThrowSourceInfoRefreshError();
          }
        }
        else
        {
          if ((this.loadingPeriodHolder != null) && (!this.loadingPeriodHolder.isFullyBuffered()))
            break label613;
          setIsLoading(false);
        }
        while (true)
        {
          if (this.playingPeriodHolder == null)
            break label635;
          while ((this.playingPeriodHolder != this.readingPeriodHolder) && (this.rendererPositionUs >= this.playingPeriodHolder.next.rendererPositionOffsetUs))
          {
            this.playingPeriodHolder.release();
            setPlayingPeriodHolder(this.playingPeriodHolder.next);
            this.playbackInfo = new PlaybackInfo(this.playingPeriodHolder.index, this.playingPeriodHolder.startPositionUs);
            updatePlaybackPositions();
            this.eventHandler.obtainMessage(5, this.playbackInfo).sendToTarget();
          }
          i = this.loadingPeriodHolder.index + 1;
          break;
          int k = this.timeline.getPeriod(i, this.period).windowIndex;
          long l1;
          long l2;
          if (i == this.timeline.getWindow(k, this.window).firstPeriodIndex)
          {
            j = 1;
            if (this.loadingPeriodHolder != null)
              break label456;
            l1 = this.playbackInfo.startPositionUs;
            if (i == -1)
              break label571;
            if (this.loadingPeriodHolder != null)
              break label573;
            l2 = l1;
            this.timeline.getPeriod(i, this.period, true);
            if ((i != this.timeline.getPeriodCount() - 1) || (this.timeline.getWindow(this.period.windowIndex, this.window).isDynamic))
              break label607;
          }
          for (boolean bool = true; ; bool = false)
          {
            localObject1 = new MediaPeriodHolder(this.renderers, this.rendererCapabilities, l2, this.trackSelector, this.loadControl, this.mediaSource, this.period.uid, i, bool, l1);
            if (this.loadingPeriodHolder != null)
              this.loadingPeriodHolder.next = ((MediaPeriodHolder)localObject1);
            this.loadingPeriodHolder = ((MediaPeriodHolder)localObject1);
            this.loadingPeriodHolder.mediaPeriod.prepare(this);
            setIsLoading(true);
            break;
            j = 0;
            break label279;
            if (j == 0)
            {
              l1 = 0L;
              break label295;
            }
            l1 = this.loadingPeriodHolder.getRendererOffset();
            l2 = this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
            long l3 = this.rendererPositionUs;
            localObject1 = getPeriodPosition(this.timeline, k, -9223372036854775807L, Math.max(0L, l1 + l2 - l3));
            if (localObject1 == null)
            {
              l1 = -9223372036854775807L;
              i = -1;
              break label295;
            }
            i = ((Integer)((Pair)localObject1).first).intValue();
            l1 = ((Long)((Pair)localObject1).second).longValue();
            break label295;
            break;
            l2 = this.loadingPeriodHolder.getRendererOffset() + this.timeline.getPeriod(this.loadingPeriodHolder.index, this.period).getDurationUs();
            break label311;
          }
          if ((this.loadingPeriodHolder == null) || (!this.loadingPeriodHolder.needsContinueLoading))
            continue;
          maybeContinueLoading();
        }
        continue;
        if (!this.readingPeriodHolder.isLast)
          break;
        localObject1 = this.enabledRenderers;
        j = localObject1.length;
        i = 0;
        while (i < j)
        {
          localObject1[i].setCurrentStreamIsFinal();
          i += 1;
        }
      }
      localObject1 = this.enabledRenderers;
      j = localObject1.length;
      i = 0;
      while (true)
      {
        if (i >= j)
          break label716;
        if (!localObject1[i].hasReadStreamToEnd())
          break;
        i += 1;
      }
    }
    while ((this.readingPeriodHolder.next == null) || (!this.readingPeriodHolder.next.prepared));
    label571: label573: label716: Object localObject1 = this.readingPeriodHolder.trackSelections;
    this.readingPeriodHolder = this.readingPeriodHolder.next;
    TrackSelectionArray localTrackSelectionArray = this.readingPeriodHolder.trackSelections;
    int i = 0;
    label770: Renderer localRenderer;
    if (i < this.renderers.length)
    {
      localRenderer = this.renderers[i];
      Object localObject2 = ((TrackSelectionArray)localObject1).get(i);
      TrackSelection localTrackSelection = localTrackSelectionArray.get(i);
      if (localObject2 != null)
      {
        if (localTrackSelection == null)
          break label885;
        localObject2 = new Format[localTrackSelection.length()];
        j = 0;
        while (j < localObject2.length)
        {
          localObject2[j] = localTrackSelection.getFormat(j);
          j += 1;
        }
        localRenderer.replaceStream(localObject2, this.readingPeriodHolder.sampleStreams[i], this.readingPeriodHolder.getRendererOffset());
      }
    }
    while (true)
    {
      i += 1;
      break label770;
      break;
      label885: localRenderer.setCurrentStreamIsFinal();
    }
  }

  private void updatePlaybackPositions()
  {
    if (this.playingPeriodHolder == null)
      return;
    long l1 = this.playingPeriodHolder.mediaPeriod.readDiscontinuity();
    if (l1 != -9223372036854775807L)
    {
      resetRendererPosition(l1);
      this.playbackInfo.positionUs = l1;
      this.elapsedRealtimeUs = (SystemClock.elapsedRealtime() * 1000L);
      if (this.enabledRenderers.length != 0)
        break label182;
      l1 = -9223372036854775808L;
    }
    while (true)
    {
      PlaybackInfo localPlaybackInfo = this.playbackInfo;
      long l2 = l1;
      if (l1 == -9223372036854775808L)
        l2 = this.timeline.getPeriod(this.playingPeriodHolder.index, this.period).getDurationUs();
      localPlaybackInfo.bufferedPositionUs = l2;
      return;
      if ((this.rendererMediaClockSource != null) && (!this.rendererMediaClockSource.isEnded()))
      {
        this.rendererPositionUs = this.rendererMediaClock.getPositionUs();
        this.standaloneMediaClock.setPositionUs(this.rendererPositionUs);
      }
      while (true)
      {
        l1 = this.playingPeriodHolder.toPeriodTime(this.rendererPositionUs);
        break;
        this.rendererPositionUs = this.standaloneMediaClock.getPositionUs();
      }
      label182: l1 = this.playingPeriodHolder.mediaPeriod.getBufferedPositionUs();
    }
  }

  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    monitorenter;
    try
    {
      if (this.released)
      {
        Log.w("ExoPlayerImplInternal", "Ignoring messages sent after release.");
        return;
      }
      int i = this.customMessagesSent;
      this.customMessagesSent = (i + 1);
      this.handler.obtainMessage(10, paramArrayOfExoPlayerMessage).sendToTarget();
      while (true)
      {
        int j = this.customMessagesProcessed;
        if (j > i)
          break;
        try
        {
          wait();
        }
        catch (InterruptedException paramArrayOfExoPlayerMessage)
        {
          Thread.currentThread().interrupt();
        }
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramArrayOfExoPlayerMessage;
  }

  public boolean handleMessage(Message paramMessage)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    try
    {
      switch (paramMessage.what)
      {
      case 0:
        MediaSource localMediaSource = (MediaSource)paramMessage.obj;
        if (paramMessage.arg1 != 0)
          bool1 = true;
        prepareInternal(localMediaSource, bool1);
        return true;
      case 1:
        bool1 = bool2;
        if (paramMessage.arg1 != 0)
          bool1 = true;
        setPlayWhenReadyInternal(bool1);
        return true;
      case 2:
        doSomeWork();
        return true;
      case 3:
        seekToInternal((SeekPosition)paramMessage.obj);
        return true;
      case 4:
        stopInternal();
        return true;
      case 5:
        releaseInternal();
        return true;
      case 7:
        handlePeriodPrepared((MediaPeriod)paramMessage.obj);
        return true;
      case 6:
        handleSourceInfoRefreshed((Pair)paramMessage.obj);
        return true;
      case 8:
        handleContinueLoadingRequested((MediaPeriod)paramMessage.obj);
        return true;
      case 9:
        reselectTracksInternal();
        return true;
      case 10:
        sendMessagesInternal((ExoPlayer.ExoPlayerMessage[])(ExoPlayer.ExoPlayerMessage[])paramMessage.obj);
        return true;
      }
    }
    catch (ExoPlaybackException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Renderer error.", paramMessage);
      this.eventHandler.obtainMessage(7, paramMessage).sendToTarget();
      stopInternal();
      return true;
    }
    catch (java.io.IOException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Source error.", paramMessage);
      this.eventHandler.obtainMessage(7, ExoPlaybackException.createForSource(paramMessage)).sendToTarget();
      stopInternal();
      return true;
    }
    catch (RuntimeException paramMessage)
    {
      Log.e("ExoPlayerImplInternal", "Internal runtime error.", paramMessage);
      this.eventHandler.obtainMessage(7, ExoPlaybackException.createForUnexpected(paramMessage)).sendToTarget();
      stopInternal();
      return true;
    }
    return false;
  }

  public void onContinueLoadingRequested(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(8, paramMediaPeriod).sendToTarget();
  }

  public void onPrepared(MediaPeriod paramMediaPeriod)
  {
    this.handler.obtainMessage(7, paramMediaPeriod).sendToTarget();
  }

  public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
  {
    this.handler.obtainMessage(6, Pair.create(paramTimeline, paramObject)).sendToTarget();
  }

  public void onTrackSelectionsInvalidated()
  {
    this.handler.sendEmptyMessage(9);
  }

  public void prepare(MediaSource paramMediaSource, boolean paramBoolean)
  {
    Handler localHandler = this.handler;
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      localHandler.obtainMessage(0, i, 0, paramMediaSource).sendToTarget();
      return;
    }
  }

  public void release()
  {
    monitorenter;
    while (true)
    {
      try
      {
        boolean bool = this.released;
        if (bool)
          return;
        this.handler.sendEmptyMessage(5);
        bool = this.released;
        if (!bool)
        {
          try
          {
            wait();
          }
          catch (InterruptedException localInterruptedException)
          {
            Thread.currentThread().interrupt();
          }
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      this.internalPlaybackThread.quit();
    }
  }

  public void seekTo(Timeline paramTimeline, int paramInt, long paramLong)
  {
    this.handler.obtainMessage(3, new SeekPosition(paramTimeline, paramInt, paramLong)).sendToTarget();
  }

  public void sendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    if (this.released)
    {
      Log.w("ExoPlayerImplInternal", "Ignoring messages sent after release.");
      return;
    }
    this.customMessagesSent += 1;
    this.handler.obtainMessage(10, paramArrayOfExoPlayerMessage).sendToTarget();
  }

  public void setPlayWhenReady(boolean paramBoolean)
  {
    Handler localHandler = this.handler;
    if (paramBoolean);
    for (int i = 1; ; i = 0)
    {
      localHandler.obtainMessage(1, i, 0).sendToTarget();
      return;
    }
  }

  public void stop()
  {
    this.handler.sendEmptyMessage(4);
  }

  private static final class MediaPeriodHolder
  {
    public boolean hasEnabledTracks;
    public int index;
    public boolean isLast;
    private final LoadControl loadControl;
    public final boolean[] mayRetainStreamFlags;
    public final MediaPeriod mediaPeriod;
    private final MediaSource mediaSource;
    public boolean needsContinueLoading;
    public MediaPeriodHolder next;
    private TrackSelectionArray periodTrackSelections;
    public boolean prepared;
    private final RendererCapabilities[] rendererCapabilities;
    public final long rendererPositionOffsetUs;
    private final Renderer[] renderers;
    public final SampleStream[] sampleStreams;
    public long startPositionUs;
    private TrackGroupArray trackGroups;
    private TrackSelectionArray trackSelections;
    private Object trackSelectionsInfo;
    private final TrackSelector trackSelector;
    public final Object uid;

    public MediaPeriodHolder(Renderer[] paramArrayOfRenderer, RendererCapabilities[] paramArrayOfRendererCapabilities, long paramLong1, TrackSelector paramTrackSelector, LoadControl paramLoadControl, MediaSource paramMediaSource, Object paramObject, int paramInt, boolean paramBoolean, long paramLong2)
    {
      this.renderers = paramArrayOfRenderer;
      this.rendererCapabilities = paramArrayOfRendererCapabilities;
      this.rendererPositionOffsetUs = paramLong1;
      this.trackSelector = paramTrackSelector;
      this.loadControl = paramLoadControl;
      this.mediaSource = paramMediaSource;
      this.uid = Assertions.checkNotNull(paramObject);
      this.index = paramInt;
      this.isLast = paramBoolean;
      this.startPositionUs = paramLong2;
      this.sampleStreams = new SampleStream[paramArrayOfRenderer.length];
      this.mayRetainStreamFlags = new boolean[paramArrayOfRenderer.length];
      this.mediaPeriod = paramMediaSource.createPeriod(paramInt, paramLoadControl.getAllocator(), paramLong2);
    }

    public long getRendererOffset()
    {
      return this.rendererPositionOffsetUs - this.startPositionUs;
    }

    public ExoPlayerImplInternal.TrackInfo getTrackInfo()
    {
      return new ExoPlayerImplInternal.TrackInfo(this.trackGroups, this.trackSelections, this.trackSelectionsInfo);
    }

    public void handlePrepared()
    {
      this.prepared = true;
      this.trackGroups = this.mediaPeriod.getTrackGroups();
      selectTracks();
      this.startPositionUs = updatePeriodTrackSelection(this.startPositionUs, false);
    }

    public boolean isFullyBuffered()
    {
      return (this.prepared) && ((!this.hasEnabledTracks) || (this.mediaPeriod.getBufferedPositionUs() == -9223372036854775808L));
    }

    public void release()
    {
      try
      {
        this.mediaSource.releasePeriod(this.mediaPeriod);
        return;
      }
      catch (RuntimeException localRuntimeException)
      {
        Log.e("ExoPlayerImplInternal", "Period release failed.", localRuntimeException);
      }
    }

    public boolean selectTracks()
    {
      Pair localPair = this.trackSelector.selectTracks(this.rendererCapabilities, this.trackGroups);
      TrackSelectionArray localTrackSelectionArray = (TrackSelectionArray)localPair.first;
      if (localTrackSelectionArray.equals(this.periodTrackSelections))
        return false;
      this.trackSelections = localTrackSelectionArray;
      this.trackSelectionsInfo = localPair.second;
      return true;
    }

    public void setIndex(int paramInt, boolean paramBoolean)
    {
      this.index = paramInt;
      this.isLast = paramBoolean;
    }

    public long toPeriodTime(long paramLong)
    {
      return paramLong - getRendererOffset();
    }

    public long toRendererTime(long paramLong)
    {
      return getRendererOffset() + paramLong;
    }

    public long updatePeriodTrackSelection(long paramLong, boolean paramBoolean)
    {
      return updatePeriodTrackSelection(paramLong, paramBoolean, new boolean[this.renderers.length]);
    }

    public long updatePeriodTrackSelection(long paramLong, boolean paramBoolean, boolean[] paramArrayOfBoolean)
    {
      int i = 0;
      if (i < this.trackSelections.length)
      {
        boolean[] arrayOfBoolean = this.mayRetainStreamFlags;
        Object localObject;
        if (!paramBoolean)
          if (this.periodTrackSelections == null)
          {
            localObject = null;
            label35: if (!Util.areEqual(localObject, this.trackSelections.get(i)))
              break label85;
          }
        label85: for (int j = 1; ; j = 0)
        {
          arrayOfBoolean[i] = j;
          i += 1;
          break;
          localObject = this.periodTrackSelections.get(i);
          break label35;
        }
      }
      paramLong = this.mediaPeriod.selectTracks(this.trackSelections.getAll(), this.mayRetainStreamFlags, this.sampleStreams, paramArrayOfBoolean, paramLong);
      this.periodTrackSelections = this.trackSelections;
      this.hasEnabledTracks = false;
      i = 0;
      if (i < this.sampleStreams.length)
      {
        if (this.sampleStreams[i] != null)
        {
          if (this.trackSelections.get(i) != null);
          for (paramBoolean = true; ; paramBoolean = false)
          {
            Assertions.checkState(paramBoolean);
            this.hasEnabledTracks = true;
            i += 1;
            break;
          }
        }
        if (this.trackSelections.get(i) == null);
        for (paramBoolean = true; ; paramBoolean = false)
        {
          Assertions.checkState(paramBoolean);
          break;
        }
      }
      this.loadControl.onTracksSelected(this.renderers, this.trackGroups, this.trackSelections);
      return paramLong;
    }
  }

  public static final class PlaybackInfo
  {
    public volatile long bufferedPositionUs;
    public final int periodIndex;
    public volatile long positionUs;
    public final long startPositionUs;

    public PlaybackInfo(int paramInt, long paramLong)
    {
      this.periodIndex = paramInt;
      this.startPositionUs = paramLong;
      this.positionUs = paramLong;
      this.bufferedPositionUs = paramLong;
    }

    public PlaybackInfo copyWithPeriodIndex(int paramInt)
    {
      PlaybackInfo localPlaybackInfo = new PlaybackInfo(paramInt, this.startPositionUs);
      localPlaybackInfo.positionUs = this.positionUs;
      localPlaybackInfo.bufferedPositionUs = this.bufferedPositionUs;
      return localPlaybackInfo;
    }
  }

  private static final class SeekPosition
  {
    public final Timeline timeline;
    public final int windowIndex;
    public final long windowPositionUs;

    public SeekPosition(Timeline paramTimeline, int paramInt, long paramLong)
    {
      this.timeline = paramTimeline;
      this.windowIndex = paramInt;
      this.windowPositionUs = paramLong;
    }
  }

  public static final class SourceInfo
  {
    public final Object manifest;
    public final ExoPlayerImplInternal.PlaybackInfo playbackInfo;
    public final int seekAcks;
    public final Timeline timeline;

    public SourceInfo(Timeline paramTimeline, Object paramObject, ExoPlayerImplInternal.PlaybackInfo paramPlaybackInfo, int paramInt)
    {
      this.timeline = paramTimeline;
      this.manifest = paramObject;
      this.playbackInfo = paramPlaybackInfo;
      this.seekAcks = paramInt;
    }
  }

  public static final class TrackInfo
  {
    public final TrackGroupArray groups;
    public final Object info;
    public final TrackSelectionArray selections;

    public TrackInfo(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray, Object paramObject)
    {
      this.groups = paramTrackGroupArray;
      this.selections = paramTrackSelectionArray;
      this.info = paramObject;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ExoPlayerImplInternal
 * JD-Core Version:    0.6.0
 */