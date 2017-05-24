package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;

public abstract class BaseMediaChunk extends MediaChunk
{
  private int firstSampleIndex;
  private DefaultTrackOutput trackOutput;

  public BaseMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2)
  {
    super(paramDataSource, paramDataSpec, paramFormat, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
  }

  public final int getFirstSampleIndex()
  {
    return this.firstSampleIndex;
  }

  protected final DefaultTrackOutput getTrackOutput()
  {
    return this.trackOutput;
  }

  public void init(DefaultTrackOutput paramDefaultTrackOutput)
  {
    this.trackOutput = paramDefaultTrackOutput;
    this.firstSampleIndex = paramDefaultTrackOutput.getWriteIndex();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.BaseMediaChunk
 * JD-Core Version:    0.6.0
 */