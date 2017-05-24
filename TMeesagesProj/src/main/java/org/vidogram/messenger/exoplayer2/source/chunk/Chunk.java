package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public abstract class Chunk
  implements Loader.Loadable
{
  protected final DataSource dataSource;
  public final DataSpec dataSpec;
  public final long endTimeUs;
  public final long startTimeUs;
  public final Format trackFormat;
  public final Object trackSelectionData;
  public final int trackSelectionReason;
  public final int type;

  public Chunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong1, long paramLong2)
  {
    this.dataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.dataSpec = ((DataSpec)Assertions.checkNotNull(paramDataSpec));
    this.type = paramInt1;
    this.trackFormat = paramFormat;
    this.trackSelectionReason = paramInt2;
    this.trackSelectionData = paramObject;
    this.startTimeUs = paramLong1;
    this.endTimeUs = paramLong2;
  }

  public abstract long bytesLoaded();

  public final long getDurationUs()
  {
    return this.endTimeUs - this.startTimeUs;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.Chunk
 * JD-Core Version:    0.6.0
 */