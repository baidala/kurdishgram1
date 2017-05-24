package org.vidogram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface TsPayloadReader
{
  public abstract void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean);

  public abstract void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TrackIdGenerator paramTrackIdGenerator);

  public abstract void seek();

  public static final class EsInfo
  {
    public final byte[] descriptorBytes;
    public final String language;
    public final int streamType;

    public EsInfo(int paramInt, String paramString, byte[] paramArrayOfByte)
    {
      this.streamType = paramInt;
      this.language = paramString;
      this.descriptorBytes = paramArrayOfByte;
    }
  }

  public static abstract interface Factory
  {
    public abstract SparseArray<TsPayloadReader> createInitialPayloadReaders();

    public abstract TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo);
  }

  public static final class TrackIdGenerator
  {
    private final int firstId;
    private int generatedIdCount;
    private final int idIncrement;

    public TrackIdGenerator(int paramInt1, int paramInt2)
    {
      this.firstId = paramInt1;
      this.idIncrement = paramInt2;
    }

    public int getNextId()
    {
      int i = this.firstId;
      int j = this.idIncrement;
      int k = this.generatedIdCount;
      this.generatedIdCount = (k + 1);
      return i + j * k;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.TsPayloadReader
 * JD-Core Version:    0.6.0
 */