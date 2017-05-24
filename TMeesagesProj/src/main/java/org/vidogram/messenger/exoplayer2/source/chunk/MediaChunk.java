package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public abstract class MediaChunk extends Chunk
{
  public final int chunkIndex;

  public MediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2)
  {
    super(paramDataSource, paramDataSpec, 1, paramFormat, paramInt1, paramObject, paramLong1, paramLong2);
    Assertions.checkNotNull(paramFormat);
    this.chunkIndex = paramInt2;
  }

  public int getNextChunkIndex()
  {
    return this.chunkIndex + 1;
  }

  public abstract boolean isLoadCompleted();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.MediaChunk
 * JD-Core Version:    0.6.0
 */