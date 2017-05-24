package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.Util;

public class ContainerMediaChunk extends BaseMediaChunk
  implements ChunkExtractorWrapper.SingleTrackMetadataOutput
{
  private volatile int bytesLoaded;
  private final int chunkCount;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final Format sampleFormat;
  private final long sampleOffsetUs;

  public ContainerMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat1, int paramInt1, Object paramObject, long paramLong1, long paramLong2, int paramInt2, int paramInt3, long paramLong3, ChunkExtractorWrapper paramChunkExtractorWrapper, Format paramFormat2)
  {
    super(paramDataSource, paramDataSpec, paramFormat1, paramInt1, paramObject, paramLong1, paramLong2, paramInt2);
    this.chunkCount = paramInt3;
    this.sampleOffsetUs = paramLong3;
    this.extractorWrapper = paramChunkExtractorWrapper;
    this.sampleFormat = paramFormat2;
  }

  public final long bytesLoaded()
  {
    return this.bytesLoaded;
  }

  public final void cancelLoad()
  {
    this.loadCanceled = true;
  }

  public int getNextChunkIndex()
  {
    return this.chunkIndex + this.chunkCount;
  }

  public final boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }

  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }

  public final void load()
  {
    Object localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
    try
    {
      localObject1 = new DefaultExtractorInput(this.dataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.dataSource.open((DataSpec)localObject1));
      if (this.bytesLoaded == 0)
      {
        DefaultTrackOutput localDefaultTrackOutput = getTrackOutput();
        localDefaultTrackOutput.formatWithOffset(this.sampleFormat, this.sampleOffsetUs);
        this.extractorWrapper.init(this, localDefaultTrackOutput);
      }
      int i = 0;
      while (true)
      {
        if (i == 0);
        try
        {
          if (!this.loadCanceled)
          {
            i = this.extractorWrapper.read((ExtractorInput)localObject1);
            continue;
          }
          this.bytesLoaded = (int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition);
          this.dataSource.close();
          this.loadCompleted = true;
          return;
        }
        finally
        {
          this.bytesLoaded = (int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition);
        }
      }
    }
    finally
    {
      this.dataSource.close();
    }
    throw localObject2;
  }

  public final void seekMap(SeekMap paramSeekMap)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ContainerMediaChunk
 * JD-Core Version:    0.6.0
 */