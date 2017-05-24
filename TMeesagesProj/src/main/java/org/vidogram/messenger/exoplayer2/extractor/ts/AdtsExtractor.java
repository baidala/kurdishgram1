package org.vidogram.messenger.exoplayer2.extractor.ts;

import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.vidogram.messenger.exoplayer2.util.ParsableBitArray;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class AdtsExtractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new AdtsExtractor() };
    }
  };
  private static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int MAX_PACKET_SIZE = 200;
  private static final int MAX_SNIFF_BYTES = 8192;
  private final long firstSampleTimestampUs;
  private final ParsableByteArray packetBuffer;
  private AdtsReader reader;
  private boolean startedPacket;

  public AdtsExtractor()
  {
    this(0L);
  }

  public AdtsExtractor(long paramLong)
  {
    this.firstSampleTimestampUs = paramLong;
    this.packetBuffer = new ParsableByteArray(200);
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.reader = new AdtsReader(true);
    this.reader.createTracks(paramExtractorOutput, new TsPayloadReader.TrackIdGenerator(0, 1));
    paramExtractorOutput.endTracks();
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    int i = paramExtractorInput.read(this.packetBuffer.data, 0, 200);
    if (i == -1)
      return -1;
    this.packetBuffer.setPosition(0);
    this.packetBuffer.setLimit(i);
    if (!this.startedPacket)
    {
      this.reader.packetStarted(this.firstSampleTimestampUs, true);
      this.startedPacket = true;
    }
    this.reader.consume(this.packetBuffer);
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
    ParsableBitArray localParsableBitArray = new ParsableBitArray(localParsableByteArray.data);
    int i = 0;
    paramExtractorInput.peekFully(localParsableByteArray.data, 0, 10);
    localParsableByteArray.setPosition(0);
    int k;
    int j;
    int m;
    if (localParsableByteArray.readUnsignedInt24() != ID3_TAG)
    {
      paramExtractorInput.resetPeekPosition();
      paramExtractorInput.advancePeekPosition(i);
      k = 0;
      j = 0;
      m = i;
    }
    while (true)
    {
      label79: paramExtractorInput.peekFully(localParsableByteArray.data, 0, 2);
      localParsableByteArray.setPosition(0);
      if ((localParsableByteArray.readUnsignedShort() & 0xFFF6) != 65520)
      {
        paramExtractorInput.resetPeekPosition();
        m += 1;
        if (m - i < 8192);
      }
      int n;
      do
      {
        return false;
        localParsableByteArray.skipBytes(3);
        j = localParsableByteArray.readSynchSafeInt();
        i += j + 10;
        paramExtractorInput.advancePeekPosition(j);
        break;
        paramExtractorInput.advancePeekPosition(m);
        k = 0;
        j = 0;
        break label79;
        k += 1;
        if ((k >= 4) && (j > 188))
          return true;
        paramExtractorInput.peekFully(localParsableByteArray.data, 0, 4);
        localParsableBitArray.setPosition(14);
        n = localParsableBitArray.readBits(13);
      }
      while (n <= 6);
      paramExtractorInput.advancePeekPosition(n - 6);
      j += n;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.AdtsExtractor
 * JD-Core Version:    0.6.0
 */