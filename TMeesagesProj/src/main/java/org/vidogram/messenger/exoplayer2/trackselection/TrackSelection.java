package org.vidogram.messenger.exoplayer2.trackselection;

import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.source.chunk.MediaChunk;

public abstract interface TrackSelection
{
  public abstract boolean blacklist(int paramInt, long paramLong);

  public abstract int evaluateQueueSize(long paramLong, List<? extends MediaChunk> paramList);

  public abstract Format getFormat(int paramInt);

  public abstract int getIndexInTrackGroup(int paramInt);

  public abstract Format getSelectedFormat();

  public abstract int getSelectedIndex();

  public abstract int getSelectedIndexInTrackGroup();

  public abstract Object getSelectionData();

  public abstract int getSelectionReason();

  public abstract TrackGroup getTrackGroup();

  public abstract int indexOf(int paramInt);

  public abstract int indexOf(Format paramFormat);

  public abstract int length();

  public abstract void updateSelectedTrack(long paramLong);

  public static abstract interface Factory
  {
    public abstract TrackSelection createTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.TrackSelection
 * JD-Core Version:    0.6.0
 */