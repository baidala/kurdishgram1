package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class InitializationChunk extends Chunk
  implements TrackOutput, ChunkExtractorWrapper.SingleTrackMetadataOutput
{
  private volatile int bytesLoaded;
  private final ChunkExtractorWrapper extractorWrapper;
  private volatile boolean loadCanceled;
  private Format sampleFormat;
  private SeekMap seekMap;

  public InitializationChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, ChunkExtractorWrapper paramChunkExtractorWrapper)
  {
    super(paramDataSource, paramDataSpec, 2, paramFormat, paramInt, paramObject, -9223372036854775807L, -9223372036854775807L);
    this.extractorWrapper = paramChunkExtractorWrapper;
  }

  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }

  public void cancelLoad()
  {
    this.loadCanceled = true;
  }

  public void format(Format paramFormat)
  {
    this.sampleFormat = paramFormat;
  }

  public Format getSampleFormat()
  {
    return this.sampleFormat;
  }

  public SeekMap getSeekMap()
  {
    return this.seekMap;
  }

  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }

  public void load()
  {
    Object localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
    try
    {
      localObject1 = new DefaultExtractorInput(this.dataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.dataSource.open((DataSpec)localObject1));
      if (this.bytesLoaded == 0)
        this.extractorWrapper.init(this, this);
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

  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
  {
    throw new IllegalStateException("Unexpected sample data in initialization chunk");
  }

  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    throw new IllegalStateException("Unexpected sample data in initialization chunk");
  }

  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    throw new IllegalStateException("Unexpected sample data in initialization chunk");
  }

  public void seekMap(SeekMap paramSeekMap)
  {
    this.seekMap = paramSeekMap;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.InitializationChunk
 * JD-Core Version:    0.6.0
 */