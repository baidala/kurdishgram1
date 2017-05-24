package org.vidogram.ui.Components;

import android.annotation.SuppressLint;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Handler;
import android.view.TextureView;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.exoplayer2.DefaultLoadControl;
import org.vidogram.messenger.exoplayer2.ExoPlaybackException;
import org.vidogram.messenger.exoplayer2.ExoPlayer.EventListener;
import org.vidogram.messenger.exoplayer2.ExoPlayerFactory;
import org.vidogram.messenger.exoplayer2.SimpleExoPlayer;
import org.vidogram.messenger.exoplayer2.SimpleExoPlayer.VideoListener;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorsFactory;
import org.vidogram.messenger.exoplayer2.source.ExtractorMediaSource;
import org.vidogram.messenger.exoplayer2.source.LoopingMediaSource;
import org.vidogram.messenger.exoplayer2.source.MediaSource;
import org.vidogram.messenger.exoplayer2.source.MergingMediaSource;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.source.dash.DashMediaSource;
import org.vidogram.messenger.exoplayer2.source.dash.DefaultDashChunkSource.Factory;
import org.vidogram.messenger.exoplayer2.source.hls.HlsMediaSource;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.DefaultSsChunkSource.Factory;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.SsMediaSource;
import org.vidogram.messenger.exoplayer2.trackselection.AdaptiveVideoTrackSelection.Factory;
import org.vidogram.messenger.exoplayer2.trackselection.DefaultTrackSelector;
import org.vidogram.messenger.exoplayer2.trackselection.MappingTrackSelector;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.upstream.DefaultBandwidthMeter;
import org.vidogram.messenger.exoplayer2.upstream.DefaultDataSourceFactory;
import org.vidogram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory;

@SuppressLint({"NewApi"})
public class VideoPlayer
  implements ExoPlayer.EventListener, SimpleExoPlayer.VideoListener
{
  private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
  private static final int RENDERER_BUILDING_STATE_BUILDING = 2;
  private static final int RENDERER_BUILDING_STATE_BUILT = 3;
  private static final int RENDERER_BUILDING_STATE_IDLE = 1;
  private boolean autoplay;
  private VideoPlayerDelegate delegate;
  private boolean lastReportedPlayWhenReady;
  private int lastReportedPlaybackState = 1;
  private Handler mainHandler = new Handler();
  private DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(ApplicationLoader.applicationContext, BANDWIDTH_METER, new DefaultHttpDataSourceFactory("Mozilla/5.0 (X11; Linux x86_64; rv:10.0) Gecko/20150101 Firefox/47.0 (Chrome)", BANDWIDTH_METER));
  private SimpleExoPlayer player;
  private TextureView textureView;
  private MappingTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveVideoTrackSelection.Factory(BANDWIDTH_METER));

  private void ensurePleyaerCreated()
  {
    if (this.player == null)
    {
      this.player = ExoPlayerFactory.newSimpleInstance(ApplicationLoader.applicationContext, this.trackSelector, new DefaultLoadControl(), null, 0);
      this.player.addListener(this);
      this.player.setVideoListener(this);
      this.player.setVideoTextureView(this.textureView);
      this.player.setPlayWhenReady(this.autoplay);
    }
  }

  private void maybeReportPlayerState()
  {
    boolean bool = this.player.getPlayWhenReady();
    int i = this.player.getPlaybackState();
    if ((this.lastReportedPlayWhenReady != bool) || (this.lastReportedPlaybackState != i))
    {
      this.delegate.onStateChanged(bool, i);
      this.lastReportedPlayWhenReady = bool;
      this.lastReportedPlaybackState = i;
    }
  }

  public int getBufferedPercentage()
  {
    if (this.player != null)
      return this.player.getBufferedPercentage();
    return 0;
  }

  public long getBufferedPosition()
  {
    if (this.player != null)
      return this.player.getBufferedPosition();
    return 0L;
  }

  public long getCurrentPosition()
  {
    if (this.player != null)
      return this.player.getCurrentPosition();
    return 0L;
  }

  public long getDuration()
  {
    if (this.player != null)
      return this.player.getDuration();
    return 0L;
  }

  public boolean isBuffering()
  {
    return (this.player != null) && (this.lastReportedPlaybackState == 2);
  }

  public boolean isPlayerPrepared()
  {
    return this.player != null;
  }

  public boolean isPlaying()
  {
    return (this.player != null) && (this.player.getPlayWhenReady());
  }

  public void onLoadingChanged(boolean paramBoolean)
  {
  }

  public void onPlayerError(ExoPlaybackException paramExoPlaybackException)
  {
    this.delegate.onError(paramExoPlaybackException);
  }

  public void onPlayerStateChanged(boolean paramBoolean, int paramInt)
  {
    maybeReportPlayerState();
  }

  public void onPositionDiscontinuity()
  {
  }

  public void onRenderedFirstFrame()
  {
    this.delegate.onRenderedFirstFrame();
  }

  public boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture)
  {
    return this.delegate.onSurfaceDestroyed(paramSurfaceTexture);
  }

  public void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture)
  {
    this.delegate.onSurfaceTextureUpdated(paramSurfaceTexture);
  }

  public void onTimelineChanged(Timeline paramTimeline, Object paramObject)
  {
  }

  public void onTracksChanged(TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
  }

  public void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat)
  {
    this.delegate.onVideoSizeChanged(paramInt1, paramInt2, paramInt3, paramFloat);
  }

  public void pause()
  {
    if (this.player == null)
      return;
    this.player.setPlayWhenReady(false);
  }

  public void play()
  {
    if (this.player == null)
      return;
    this.player.setPlayWhenReady(true);
  }

  public void preparePlayer(Uri paramUri, String paramString)
  {
    ensurePleyaerCreated();
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
      switch (i)
      {
      default:
        paramUri = new ExtractorMediaSource(paramUri, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
      case 0:
      case 1:
      case 2:
      }
    case 3075986:
    case 103407:
    case 3680:
    }
    while (true)
    {
      this.player.prepare(paramUri, true, true);
      return;
      if (!paramString.equals("dash"))
        break;
      i = 0;
      break;
      if (!paramString.equals("hls"))
        break;
      i = 1;
      break;
      if (!paramString.equals("ss"))
        break;
      i = 2;
      break;
      paramUri = new DashMediaSource(paramUri, this.mediaDataSourceFactory, new DefaultDashChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
      continue;
      paramUri = new HlsMediaSource(paramUri, this.mediaDataSourceFactory, this.mainHandler, null);
      continue;
      paramUri = new SsMediaSource(paramUri, this.mediaDataSourceFactory, new DefaultSsChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
    }
  }

  public void preparePlayerLoop(Uri paramUri1, String paramString1, Uri paramUri2, String paramString2)
  {
    ensurePleyaerCreated();
    Object localObject3 = null;
    Object localObject2 = null;
    int j = 0;
    if (j < 2)
    {
      Uri localUri;
      Object localObject1;
      label30: label68: int i;
      if (j == 0)
      {
        localUri = paramUri1;
        localObject1 = paramString1;
        switch (((String)localObject1).hashCode())
        {
        default:
          i = -1;
          switch (i)
          {
          default:
            label71: localObject1 = new ExtractorMediaSource(localUri, this.mediaDataSourceFactory, new DefaultExtractorsFactory(), this.mainHandler, null);
            label127: localObject1 = new LoopingMediaSource((MediaSource)localObject1);
            if (j != 0);
          case 0:
          case 1:
          case 2:
          }
        case 3075986:
        case 103407:
        case 3680:
        }
      }
      while (true)
      {
        j += 1;
        localObject3 = localObject1;
        break;
        localUri = paramUri2;
        localObject1 = paramString2;
        break label30;
        if (!((String)localObject1).equals("dash"))
          break label68;
        i = 0;
        break label71;
        if (!((String)localObject1).equals("hls"))
          break label68;
        i = 1;
        break label71;
        if (!((String)localObject1).equals("ss"))
          break label68;
        i = 2;
        break label71;
        localObject1 = new DashMediaSource(localUri, this.mediaDataSourceFactory, new DefaultDashChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
        break label127;
        localObject1 = new HlsMediaSource(localUri, this.mediaDataSourceFactory, this.mainHandler, null);
        break label127;
        localObject1 = new SsMediaSource(localUri, this.mediaDataSourceFactory, new DefaultSsChunkSource.Factory(this.mediaDataSourceFactory), this.mainHandler, null);
        break label127;
        localObject2 = localObject1;
        localObject1 = localObject3;
      }
    }
    new MergingMediaSource(new MediaSource[] { localObject3, localObject2 });
    this.player.prepare(localObject3, true, true);
  }

  public void releasePlayer()
  {
    if (this.player != null)
    {
      this.player.release();
      this.player = null;
    }
  }

  public void seekTo(long paramLong)
  {
    if (this.player == null)
      return;
    this.player.seekTo(paramLong);
  }

  public void setDelegate(VideoPlayerDelegate paramVideoPlayerDelegate)
  {
    this.delegate = paramVideoPlayerDelegate;
  }

  public void setMute(boolean paramBoolean)
  {
    if (this.player == null)
      return;
    if (paramBoolean)
    {
      this.player.setVolume(0.0F);
      return;
    }
    this.player.setVolume(1.0F);
  }

  public void setPlayWhenReady(boolean paramBoolean)
  {
    this.autoplay = paramBoolean;
    if (this.player == null)
      return;
    this.player.setPlayWhenReady(paramBoolean);
  }

  public void setTextureView(TextureView paramTextureView)
  {
    this.textureView = paramTextureView;
    if (this.player == null)
      return;
    this.player.setVideoTextureView(this.textureView);
  }

  public static abstract interface RendererBuilder
  {
    public abstract void buildRenderers(VideoPlayer paramVideoPlayer);

    public abstract void cancel();
  }

  public static abstract interface VideoPlayerDelegate
  {
    public abstract void onError(Exception paramException);

    public abstract void onRenderedFirstFrame();

    public abstract void onStateChanged(boolean paramBoolean, int paramInt);

    public abstract boolean onSurfaceDestroyed(SurfaceTexture paramSurfaceTexture);

    public abstract void onSurfaceTextureUpdated(SurfaceTexture paramSurfaceTexture);

    public abstract void onVideoSizeChanged(int paramInt1, int paramInt2, int paramInt3, float paramFloat);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.VideoPlayer
 * JD-Core Version:    0.6.0
 */