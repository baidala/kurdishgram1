package org.vidogram.messenger.exoplayer2.source;

import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;

public abstract interface MediaPeriod extends SequenceableLoader
{
  public abstract long getBufferedPositionUs();

  public abstract TrackGroupArray getTrackGroups();

  public abstract void maybeThrowPrepareError();

  public abstract void prepare(Callback paramCallback);

  public abstract long readDiscontinuity();

  public abstract long seekToUs(long paramLong);

  public abstract long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong);

  public static abstract interface Callback extends SequenceableLoader.Callback<MediaPeriod>
  {
    public abstract void onPrepared(MediaPeriod paramMediaPeriod);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.MediaPeriod
 * JD-Core Version:    0.6.0
 */