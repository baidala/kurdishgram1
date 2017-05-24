package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.text.cea.Cea608Decoder;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class SeiReader
{
  private final TrackOutput output;

  public SeiReader(TrackOutput paramTrackOutput)
  {
    this.output = paramTrackOutput;
    paramTrackOutput.format(Format.createTextSampleFormat(null, "application/cea-608", null, -1, 0, null, null));
  }

  public void consume(long paramLong, ParsableByteArray paramParsableByteArray)
  {
    int k;
    int j;
    if (paramParsableByteArray.bytesLeft() > 1)
    {
      i = 0;
      do
      {
        k = paramParsableByteArray.readUnsignedByte();
        j = i + k;
        i = j;
      }
      while (k == 255);
    }
    for (int i = 0; ; i = k)
    {
      int m = paramParsableByteArray.readUnsignedByte();
      k = i + m;
      if (m == 255)
        continue;
      if (Cea608Decoder.isSeiMessageCea608(j, k, paramParsableByteArray))
      {
        paramParsableByteArray.skipBytes(8);
        m = paramParsableByteArray.readUnsignedByte() & 0x1F;
        paramParsableByteArray.skipBytes(1);
        i = 0;
        j = 0;
        if (i < m)
        {
          if ((paramParsableByteArray.peekUnsignedByte() & 0x7) != 4)
            paramParsableByteArray.skipBytes(3);
          while (true)
          {
            i += 1;
            break;
            j += 3;
            this.output.sampleData(paramParsableByteArray, 3);
          }
        }
        this.output.sampleMetadata(paramLong, 1, j, 0, null);
        paramParsableByteArray.skipBytes(k - (m * 3 + 10));
        break;
      }
      paramParsableByteArray.skipBytes(k);
      break;
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.SeiReader
 * JD-Core Version:    0.6.0
 */