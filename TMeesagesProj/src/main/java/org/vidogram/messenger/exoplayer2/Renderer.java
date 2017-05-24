package org.vidogram.messenger.exoplayer2;

import org.vidogram.messenger.exoplayer2.source.SampleStream;
import org.vidogram.messenger.exoplayer2.util.MediaClock;

public abstract interface Renderer extends ExoPlayer.ExoPlayerComponent
{
  public static final int STATE_DISABLED = 0;
  public static final int STATE_ENABLED = 1;
  public static final int STATE_STARTED = 2;

  public abstract void disable();

  public abstract void enable(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2);

  public abstract RendererCapabilities getCapabilities();

  public abstract MediaClock getMediaClock();

  public abstract int getState();

  public abstract SampleStream getStream();

  public abstract int getTrackType();

  public abstract boolean hasReadStreamToEnd();

  public abstract boolean isEnded();

  public abstract boolean isReady();

  public abstract void maybeThrowStreamError();

  public abstract void render(long paramLong1, long paramLong2);

  public abstract void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong);

  public abstract void resetPosition(long paramLong);

  public abstract void setCurrentStreamIsFinal();

  public abstract void setIndex(int paramInt);

  public abstract void start();

  public abstract void stop();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.Renderer
 * JD-Core Version:    0.6.0
 */