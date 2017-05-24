package org.vidogram.messenger.exoplayer2.extractor.ogg;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class OggPacket
{
  private int currentSegmentIndex = -1;
  private final ParsableByteArray packetArray = new ParsableByteArray(new byte[65025], 0);
  private final OggPageHeader pageHeader = new OggPageHeader();
  private boolean populated;
  private int segmentCount;

  private int calculatePacketSize(int paramInt)
  {
    int i = 0;
    this.segmentCount = 0;
    int j;
    int k;
    do
    {
      j = i;
      if (this.segmentCount + paramInt >= this.pageHeader.pageSegmentCount)
        break;
      int[] arrayOfInt = this.pageHeader.laces;
      j = this.segmentCount;
      this.segmentCount = (j + 1);
      k = arrayOfInt[(j + paramInt)];
      j = i + k;
      i = j;
    }
    while (k == 255);
    return j;
  }

  public OggPageHeader getPageHeader()
  {
    return this.pageHeader;
  }

  public ParsableByteArray getPayload()
  {
    return this.packetArray;
  }

  public boolean populate(ExtractorInput paramExtractorInput)
  {
    boolean bool;
    if (paramExtractorInput != null)
      bool = true;
    int i;
    while (true)
    {
      Assertions.checkState(bool);
      if (this.populated)
      {
        this.populated = false;
        this.packetArray.reset();
      }
      if (this.populated)
        break label239;
      if (this.currentSegmentIndex >= 0)
        break;
      if (!this.pageHeader.populate(paramExtractorInput, true))
      {
        return false;
        bool = false;
        continue;
      }
      i = this.pageHeader.headerSize;
      if (((this.pageHeader.type & 0x1) != 1) || (this.packetArray.limit() != 0))
        break label241;
      i += calculatePacketSize(0);
    }
    label203: label236: label239: label241: for (int j = this.segmentCount + 0; ; j = 0)
    {
      paramExtractorInput.skipFully(i);
      this.currentSegmentIndex = j;
      j = calculatePacketSize(this.currentSegmentIndex);
      i = this.currentSegmentIndex + this.segmentCount;
      if (j > 0)
      {
        paramExtractorInput.readFully(this.packetArray.data, this.packetArray.limit(), j);
        this.packetArray.setLimit(j + this.packetArray.limit());
        if (this.pageHeader.laces[(i - 1)] != 255)
        {
          bool = true;
          this.populated = bool;
        }
      }
      else
      {
        if (i != this.pageHeader.pageSegmentCount)
          break label236;
        i = -1;
      }
      while (true)
      {
        this.currentSegmentIndex = i;
        break;
        bool = false;
        break label203;
      }
      return true;
    }
  }

  public void reset()
  {
    this.pageHeader.reset();
    this.packetArray.reset();
    this.currentSegmentIndex = -1;
    this.populated = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.OggPacket
 * JD-Core Version:    0.6.0
 */