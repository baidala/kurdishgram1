package org.vidogram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class DummyTrackOutput
  implements TrackOutput
{
  public void format(Format paramFormat)
  {
  }

  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
  {
    paramInt = paramExtractorInput.skip(paramInt);
    if (paramInt == -1)
    {
      if (paramBoolean)
        return -1;
      throw new EOFException();
    }
    return paramInt;
  }

  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(paramInt);
  }

  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.DummyTrackOutput
 * JD-Core Version:    0.6.0
 */