package org.vidogram.messenger.exoplayer2.trackselection;

import android.util.Pair;
import org.vidogram.messenger.exoplayer2.RendererCapabilities;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;

public abstract class TrackSelector
{
  private InvalidationListener listener;

  public final void init(InvalidationListener paramInvalidationListener)
  {
    this.listener = paramInvalidationListener;
  }

  protected final void invalidate()
  {
    if (this.listener != null)
      this.listener.onTrackSelectionsInvalidated();
  }

  public abstract void onSelectionActivated(Object paramObject);

  public abstract Pair<TrackSelectionArray, Object> selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray);

  public static abstract interface InvalidationListener
  {
    public abstract void onTrackSelectionsInvalidated();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.TrackSelector
 * JD-Core Version:    0.6.0
 */