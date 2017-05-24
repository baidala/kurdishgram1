package org.vidogram.messenger.exoplayer2;

import android.content.Context;
import org.vidogram.messenger.exoplayer2.drm.DrmSessionManager;
import org.vidogram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelector;

public final class ExoPlayerFactory
{
  public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000L;

  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector)
  {
    return newInstance(paramArrayOfRenderer, paramTrackSelector, new DefaultLoadControl());
  }

  public static ExoPlayer newInstance(Renderer[] paramArrayOfRenderer, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return new ExoPlayerImpl(paramArrayOfRenderer, paramTrackSelector, paramLoadControl);
  }

  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, null);
  }

  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, 0);
  }

  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt)
  {
    return newSimpleInstance(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramInt, 5000L);
  }

  public static SimpleExoPlayer newSimpleInstance(Context paramContext, TrackSelector paramTrackSelector, LoadControl paramLoadControl, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, int paramInt, long paramLong)
  {
    return new SimpleExoPlayer(paramContext, paramTrackSelector, paramLoadControl, paramDrmSessionManager, paramInt, paramLong);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ExoPlayerFactory
 * JD-Core Version:    0.6.0
 */