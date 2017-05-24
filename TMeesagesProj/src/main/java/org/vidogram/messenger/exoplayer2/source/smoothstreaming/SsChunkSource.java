package org.vidogram.messenger.exoplayer2.source.smoothstreaming;

import org.vidogram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;
import org.vidogram.messenger.exoplayer2.source.chunk.ChunkSource;
import org.vidogram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower;

public abstract interface SsChunkSource extends ChunkSource
{
  public abstract void updateManifest(SsManifest paramSsManifest);

  public static abstract interface Factory
  {
    public abstract SsChunkSource createChunkSource(LoaderErrorThrower paramLoaderErrorThrower, SsManifest paramSsManifest, int paramInt, TrackSelection paramTrackSelection, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.smoothstreaming.SsChunkSource
 * JD-Core Version:    0.6.0
 */