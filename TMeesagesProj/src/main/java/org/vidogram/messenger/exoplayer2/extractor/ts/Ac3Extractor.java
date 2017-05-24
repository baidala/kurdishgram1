package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.audio.Ac3Util;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class Ac3Extractor
  implements Extractor
{
  private static final int AC3_SYNC_WORD = 2935;
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Ac3Extractor() };
    }
  };
  private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int MAX_SNIFF_BYTES = 8192;
  private static final int MAX_SYNC_FRAME_SIZE = 2786;
  private final long firstSampleTimestampUs;
  private Ac3Reader reader;
  private final ParsableByteArray sampleData;
  private boolean startedPacket;

  public Ac3Extractor()
  {
    this(0L);
  }

  public Ac3Extractor(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.sampleData = new ParsableByteArray(2786);
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.reader = new Ac3Reader();
    this.reader.createTracks(paramExtractorOutput, new TsPayloadReader.TrackIdGenerator(0, 1));
    paramExtractorOutput.endTracks();
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    int i = paramExtractorInput.read(this.sampleData.data, 0, 2786);
    if (i == -1)
      return -1;
    this.sampleData.setPosition(0);
    this.sampleData.setLimit(i);
    if (!this.startedPacket)
    {
      this.reader.packetStarted(this.firstSampleTimestampUs, true);
      this.startedPacket = true;
    }
    this.reader.consume(this.sampleData);
    return 0;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.startedPacket = false;
    this.reader.seek();
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    ParsableByteArray localParsableByteArray = new ParsableByteArray(10);
    int i = 0;
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, 10);
    localParsableByteArray.setPosition(0);
    int j;
    int k;
    if (localParsableByteArray.readUnsignedInt24() != ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      j = 0;
      k = i;
    }
    while (true)
    {
      label62: paramExtractorInput.peekFully(localParsableByteArray.data, 0, 5);
      localParsableByteArray.setPosition(0);
      if (localParsableByteArray.readUnsignedShort() != 2935)
      {
        paramExtractorInput.resetPeekPosition();
        k += 1;
        if (k - i < 8192);
      }
      int m;
      do
      {
        return false;
        localParsableByteArray.skipBytes(3);
        j = localParsableByteArray.readSynchSafeInt();
        i += j + 10;
        paramExtractorInput.advancePeekPosition(j);
        break;
        paramExtractorInput.advancePeekPosition(k);
        j = 0;
        break label62;
        j += 1;
        if (j >= 4)
          return true;
        m = Ac3Util.parseAc3SyncframeSize(localParsableByteArray.data);
      }
      while (m == -1);
      paramExtractorInput.advancePeekPosition(m - 5);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.Ac3Extractor
 * JD-Core Version:    0.6.0
 */