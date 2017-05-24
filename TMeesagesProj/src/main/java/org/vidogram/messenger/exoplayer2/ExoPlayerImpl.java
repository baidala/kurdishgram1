package org.vidogram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelector;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

final class ExoPlayerImpl
  implements ExoPlayer
{
  private static final String TAG = "ExoPlayerImpl";
  private final TrackSelectionArray emptyTrackSelections;
  private final Handler eventHandler;
  private final ExoPlayerImplInternal internalPlayer;
  private boolean isLoading;
  private final CopyOnWriteArraySet<ExoPlayer.EventListener> listeners;
  private Object manifest;
  private int maskingWindowIndex;
  private long maskingWindowPositionMs;
  private int pendingSeekAcks;
  private final Timeline.Period period;
  private boolean playWhenReady;
  private ExoPlayerImplInternal.PlaybackInfo playbackInfo;
  private int playbackState;
  private final Renderer[] renderers;
  private Timeline timeline;
  private TrackGroupArray trackGroups;
  private TrackSelectionArray trackSelections;
  private final TrackSelector trackSelector;
  private boolean tracksSelected;
  private final Timeline.Window window;

  @SuppressLint({"HandlerLeak"})
  public ExoPlayerImpl(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    Log.i("ExoPlayerImpl", "Init 2.0.4 [" + Util.DEVICE_DEBUG_INFO + "]");
    if (paramArrayOfRenderer.length > 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.renderers = ((Renderer[])Assertions.checkNotNull(paramArrayOfRenderer));
      this.trackSelector = ((TrackSelector)Assertions.checkNotNull(paramTrackSelector));
      this.playWhenReady = false;
      this.playbackState = 1;
      this.listeners = new CopyOnWriteArraySet();
      this.emptyTrackSelections = new TrackSelectionArray(new TrackSelection[paramArrayOfRenderer.length]);
      this.timeline = Timeline.EMPTY;
      this.window = new Timeline.Window();
      this.period = new Timeline.Period();
      this.trackGroups = TrackGroupArray.EMPTY;
      this.trackSelections = this.emptyTrackSelections;
      this.eventHandler = new Handler()
      {
        public void handleMessage(Message paramMessage)
        {
          ExoPlayerImpl.this.handleEvent(paramMessage);
        }
      };
      this.playbackInfo = new ExoPlayerImplInternal.PlaybackInfo(0, 0L);
      this.internalPlayer = new ExoPlayerImplInternal(paramArrayOfRenderer, paramTrackSelector, paramLoadControl, this.playWhenReady, this.eventHandler, this.playbackInfo);
      return;
    }
  }

  public void addListener(ExoPlayer.EventListener paramEventListener)
  {
    this.listeners.add(paramEventListener);
  }

  public void blockingSendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    this.internalPlayer.blockingSendMessages(paramArrayOfExoPlayerMessage);
  }

  public int getBufferedPercentage()
  {
    long l1 = 100L;
    if (this.timeline.isEmpty())
      return 0;
    long l2 = getBufferedPosition();
    long l3 = getDuration();
    int i;
    if ((l2 == -9223372036854775807L) || (l3 == -9223372036854775807L))
    {
      i = 0;
      return i;
    }
    if (l3 == 0L);
    while (true)
    {
      i = (int)l1;
      break;
      l1 = 100L * l2 / l3;
    }
  }

  public long getBufferedPosition()
  {
    if ((this.timeline.isEmpty()) || (this.pendingSeekAcks > 0))
      return this.maskingWindowPositionMs;
    this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
    return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.bufferedPositionUs);
  }

  public Object getCurrentManifest()
  {
    return this.manifest;
  }

  public int getCurrentPeriodIndex()
  {
    return this.playbackInfo.periodIndex;
  }

  public long getCurrentPosition()
  {
    if ((this.timeline.isEmpty()) || (this.pendingSeekAcks > 0))
      return this.maskingWindowPositionMs;
    this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period);
    return this.period.getPositionInWindowMs() + C.usToMs(this.playbackInfo.positionUs);
  }

  public Timeline getCurrentTimeline()
  {
    return this.timeline;
  }

  public TrackGroupArray getCurrentTrackGroups()
  {
    return this.trackGroups;
  }

  public TrackSelectionArray getCurrentTrackSelections()
  {
    return this.trackSelections;
  }

  public int getCurrentWindowIndex()
  {
    if ((this.timeline.isEmpty()) || (this.pendingSeekAcks > 0))
      return this.maskingWindowIndex;
    return this.timeline.getPeriod(this.playbackInfo.periodIndex, this.period).windowIndex;
  }

  public long getDuration()
  {
    if (this.timeline.isEmpty())
      return -9223372036854775807L;
    return this.timeline.getWindow(getCurrentWindowIndex(), this.window).getDurationMs();
  }

  public boolean getPlayWhenReady()
  {
    return this.playWhenReady;
  }

  public int getPlaybackState()
  {
    return this.playbackState;
  }

  public int getRendererCount()
  {
    return this.renderers.length;
  }

  public int getRendererType(int paramInt)
  {
    return this.renderers[paramInt].getTrackType();
  }

  void handleEvent(Message paramMessage)
  {
    switch (paramMessage.what)
    {
    default:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    }
    while (true)
    {
      return;
      this.playbackState = paramMessage.arg1;
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext())
        ((ExoPlayer.EventListener)paramMessage.next()).onPlayerStateChanged(this.playWhenReady, this.playbackState);
      continue;
      if (paramMessage.arg1 != 0);
      for (boolean bool = true; ; bool = false)
      {
        this.isLoading = bool;
        paramMessage = this.listeners.iterator();
        while (paramMessage.hasNext())
          ((ExoPlayer.EventListener)paramMessage.next()).onLoadingChanged(this.isLoading);
        break;
      }
      paramMessage = (ExoPlayerImplInternal.TrackInfo)paramMessage.obj;
      this.tracksSelected = true;
      this.trackGroups = paramMessage.groups;
      this.trackSelections = paramMessage.selections;
      this.trackSelector.onSelectionActivated(paramMessage.info);
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext())
        ((ExoPlayer.EventListener)paramMessage.next()).onTracksChanged(this.trackGroups, this.trackSelections);
      continue;
      int i = this.pendingSeekAcks - 1;
      this.pendingSeekAcks = i;
      if (i != 0)
        continue;
      this.playbackInfo = ((ExoPlayerImplInternal.PlaybackInfo)paramMessage.obj);
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext())
        ((ExoPlayer.EventListener)paramMessage.next()).onPositionDiscontinuity();
      continue;
      if (this.pendingSeekAcks != 0)
        continue;
      this.playbackInfo = ((ExoPlayerImplInternal.PlaybackInfo)paramMessage.obj);
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext())
        ((ExoPlayer.EventListener)paramMessage.next()).onPositionDiscontinuity();
      continue;
      paramMessage = (ExoPlayerImplInternal.SourceInfo)paramMessage.obj;
      this.timeline = paramMessage.timeline;
      this.manifest = paramMessage.manifest;
      this.playbackInfo = paramMessage.playbackInfo;
      this.pendingSeekAcks -= paramMessage.seekAcks;
      paramMessage = this.listeners.iterator();
      while (paramMessage.hasNext())
        ((ExoPlayer.EventListener)paramMessage.next()).onTimelineChanged(this.timeline, this.manifest);
      continue;
      paramMessage = (ExoPlaybackException)paramMessage.obj;
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext())
        ((ExoPlayer.EventListener)localIterator.next()).onPlayerError(paramMessage);
    }
  }

  public boolean isLoading()
  {
    return this.isLoading;
  }

  public void prepare(MediaSource paramMediaSource)
  {
    prepare(paramMediaSource, true, true);
  }

  public void prepare(MediaSource paramMediaSource, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean2)
    {
      Iterator localIterator;
      if ((!this.timeline.isEmpty()) || (this.manifest != null))
      {
        this.timeline = Timeline.EMPTY;
        this.manifest = null;
        localIterator = this.listeners.iterator();
        while (localIterator.hasNext())
          ((ExoPlayer.EventListener)localIterator.next()).onTimelineChanged(null, null);
      }
      if (this.tracksSelected)
      {
        this.tracksSelected = false;
        this.trackGroups = TrackGroupArray.EMPTY;
        this.trackSelections = this.emptyTrackSelections;
        this.trackSelector.onSelectionActivated(null);
        localIterator = this.listeners.iterator();
        while (localIterator.hasNext())
          ((ExoPlayer.EventListener)localIterator.next()).onTracksChanged(this.trackGroups, this.trackSelections);
      }
    }
    this.internalPlayer.prepare(paramMediaSource, paramBoolean1);
  }

  public void release()
  {
    this.internalPlayer.release();
    this.eventHandler.removeCallbacksAndMessages(null);
  }

  public void removeListener(ExoPlayer.EventListener paramEventListener)
  {
    this.listeners.remove(paramEventListener);
  }

  public void seekTo(int paramInt, long paramLong)
  {
    if ((paramInt < 0) || ((!this.timeline.isEmpty()) && (paramInt >= this.timeline.getWindowCount())))
      throw new IndexOutOfBoundsException();
    this.pendingSeekAcks += 1;
    this.maskingWindowIndex = paramInt;
    if (paramLong == -9223372036854775807L)
    {
      this.maskingWindowPositionMs = 0L;
      this.internalPlayer.seekTo(this.timeline, paramInt, -9223372036854775807L);
    }
    while (true)
    {
      return;
      this.maskingWindowPositionMs = paramLong;
      this.internalPlayer.seekTo(this.timeline, paramInt, C.msToUs(paramLong));
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext())
        ((ExoPlayer.EventListener)localIterator.next()).onPositionDiscontinuity();
    }
  }

  public void seekTo(long paramLong)
  {
    seekTo(getCurrentWindowIndex(), paramLong);
  }

  public void seekToDefaultPosition()
  {
    seekToDefaultPosition(getCurrentWindowIndex());
  }

  public void seekToDefaultPosition(int paramInt)
  {
    seekTo(paramInt, -9223372036854775807L);
  }

  public void sendMessages(ExoPlayer.ExoPlayerMessage[] paramArrayOfExoPlayerMessage)
  {
    this.internalPlayer.sendMessages(paramArrayOfExoPlayerMessage);
  }

  public void setPlayWhenReady(boolean paramBoolean)
  {
    if (this.playWhenReady != paramBoolean)
    {
      this.playWhenReady = paramBoolean;
      this.internalPlayer.setPlayWhenReady(paramBoolean);
      Iterator localIterator = this.listeners.iterator();
      while (localIterator.hasNext())
        ((ExoPlayer.EventListener)localIterator.next()).onPlayerStateChanged(paramBoolean, this.playbackState);
    }
  }

  public void stop()
  {
    this.internalPlayer.stop();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ExoPlayerImpl
 * JD-Core Version:    0.6.0
 */