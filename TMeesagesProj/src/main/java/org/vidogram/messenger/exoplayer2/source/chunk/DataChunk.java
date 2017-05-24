package org.vidogram.messenger.exoplayer2.source.chunk;

import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;

public abstract class DataChunk extends Chunk
{
  private static final int READ_GRANULARITY = 16384;
  private byte[] data;
  private int limit;
  private volatile boolean loadCanceled;

  public DataChunk(DataSource paramDataSource, DataSpec paramDataSpec, int paramInt1, Format paramFormat, int paramInt2, Object paramObject, byte[] paramArrayOfByte)
  {
    super(paramDataSource, paramDataSpec, paramInt1, paramFormat, paramInt2, paramObject, -9223372036854775807L, -9223372036854775807L);
    this.data = paramArrayOfByte;
  }

  private void maybeExpandData()
  {
    if (this.data == null)
      this.data = new byte[16384];
    do
      return;
    while (this.data.length >= this.limit + 16384);
    this.data = Arrays.copyOf(this.data, this.data.length + 16384);
  }

  public long bytesLoaded()
  {
    return this.limit;
  }

  public final void cancelLoad()
  {
    this.loadCanceled = true;
  }

  protected abstract void consume(byte[] paramArrayOfByte, int paramInt);

  public byte[] getDataHolder()
  {
    return this.data;
  }

  public final boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }

  public final void load()
  {
    int i = 0;
    try
    {
      this.dataSource.open(this.dataSpec);
      this.limit = 0;
      while ((i != -1) && (!this.loadCanceled))
      {
        maybeExpandData();
        int j = this.dataSource.read(this.data, this.limit, 16384);
        i = j;
        if (j == -1)
          continue;
        this.limit += j;
        i = j;
      }
    }
    finally
    {
      this.dataSource.close();
    }
    if (!this.loadCanceled)
      consume(this.data, this.limit);
    this.dataSource.close();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.DataChunk
 * JD-Core Version:    0.6.0
 */