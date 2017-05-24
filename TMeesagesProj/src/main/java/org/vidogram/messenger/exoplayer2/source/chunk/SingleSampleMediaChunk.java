package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class SingleSampleMediaChunk extends BaseMediaChunk
{
  private volatile int bytesLoaded;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final Format sampleFormat;

  public SingleSampleMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.sampleFormat = paramFormat2;
  }

  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }

  public void cancelLoad()
  {
    this.loadCanceled = true;
  }

  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }

  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }

  public void load()
  {
    Object localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
    try
    {
      long l2 = this.dataSource.open((DataSpec)localObject1);
      long l1 = l2;
      if (l2 != -1L)
        l1 = l2 + this.bytesLoaded;
      localObject1 = new DefaultExtractorInput(this.dataSource, this.bytesLoaded, l1);
      DefaultTrackOutput localDefaultTrackOutput = getTrackOutput();
      localDefaultTrackOutput.formatWithOffset(this.sampleFormat, 0L);
      for (int i = 0; i != -1; i = localDefaultTrackOutput.sampleData((ExtractorInput)localObject1, 2147483647, true))
        this.bytesLoaded = (i + this.bytesLoaded);
      i = this.bytesLoaded;
      localDefaultTrackOutput.sampleMetadata(this.startTimeUs, 1, i, 0, null);
      this.dataSource.close();
      this.loadCompleted = true;
      return;
    }
    finally
    {
      this.dataSource.close();
    }
    throw localObject2;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.SingleSampleMediaChunk
 * JD-Core Version:    0.6.0
 */