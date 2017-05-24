package org.vidogram.messenger.exoplayer2.extractor;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public abstract interface TrackOutput
{
  public abstract void format(Format paramFormat);

  public abstract int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean);

  public abstract void sampleData(ParsableByteArray paramParsableByteArray, int paramInt);

  public abstract void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.TrackOutput
 * JD-Core Version:    0.6.0
 */