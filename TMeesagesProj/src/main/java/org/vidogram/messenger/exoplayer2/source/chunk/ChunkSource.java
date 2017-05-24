package org.vidogram.messenger.exoplayer2.source.chunk;

import java.util.List;

public abstract interface ChunkSource
{
  public abstract void getNextChunk(MediaChunk paramMediaChunk, long paramLong, ChunkHolder paramChunkHolder);

  public abstract int getPreferredQueueSize(long paramLong, List<? extends MediaChunk> paramList);

  public abstract void maybeThrowError();

  public abstract void onChunkLoadCompleted(Chunk paramChunk);

  public abstract boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ChunkSource
 * JD-Core Version:    0.6.0
 */