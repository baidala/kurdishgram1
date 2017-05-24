package org.vidogram.messenger.exoplayer2.source.dash;

import org.vidogram.messenger.exoplayer2.source.chunk.ChunkSource;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower;

public abstract interface DashChunkSource extends ChunkSource
{
  public abstract void updateManifest(DashManifest paramDashManifest, int paramInt);

  public static abstract interface Factory
  {
    public abstract DashChunkSource createDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int paramInt2, TrackSelection paramTrackSelection, long paramLong);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.DashChunkSource
 * JD-Core Version:    0.6.0
 */