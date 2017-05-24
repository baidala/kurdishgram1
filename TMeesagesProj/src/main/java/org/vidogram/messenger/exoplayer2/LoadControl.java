package org.vidogram.messenger.exoplayer2;

import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;

public abstract interface LoadControl
{
  public abstract Allocator getAllocator();

  public abstract void onPrepared();

  public abstract void onReleased();

  public abstract void onStopped();

  public abstract void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray);

  public abstract boolean shouldContinueLoading(long paramLong);

  public abstract boolean shouldStartPlayback(long paramLong, boolean paramBoolean);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.LoadControl
 * JD-Core Version:    0.6.0
 */