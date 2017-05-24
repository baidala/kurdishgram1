package org.vidogram.messenger.exoplayer2.extractor.ogg;

import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public class OggExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new OggExtractor() };
    }
  };
  private static final int MAX_VERIFICATION_BYTES = 8;
  private StreamReader streamReader;

  private static ParsableByteArray resetPosition(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(0);
    return paramParsableByteArray;
  }

  StreamReader getStreamReader()
  {
    return this.streamReader;
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    TrackOutput localTrackOutput = paramExtractorOutput.track(0);
    paramExtractorOutput.endTracks();
    this.streamReader.init(paramExtractorOutput, localTrackOutput);
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    return this.streamReader.read(paramExtractorInput, paramPositionHolder);
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.streamReader.seek(paramLong);
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    try
    {
      Object localObject = new OggPageHeader();
      if (((OggPageHeader)localObject).populate(paramExtractorInput, true))
      {
        if ((((OggPageHeader)localObject).type & 0x2) != 2)
          return false;
        int i = Math.min(((OggPageHeader)localObject).bodySize, 8);
        localObject = new ParsableByteArray(i);
        paramExtractorInput.peekFully(((ParsableByteArray)localObject).data, 0, i);
        if (FlacReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new FlacReader();
          break label134;
        }
        if (VorbisReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new VorbisReader();
          break label134;
        }
        if (OpusReader.verifyBitstreamType(resetPosition((ParsableByteArray)localObject)))
        {
          this.streamReader = new OpusReader();
          break label134;
        }
      }
      return false;
      label134: return true;
    }
    catch (org.vidogram.messenger.exoplayer2.ParserException paramExtractorInput)
    {
    }
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.OggExtractor
 * JD-Core Version:    0.6.0
 */