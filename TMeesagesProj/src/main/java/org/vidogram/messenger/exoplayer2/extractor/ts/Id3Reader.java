package org.vidogram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class Id3Reader
  implements ElementaryStreamReader
{
  private static final int ID3_HEADER_SIZE = 10;
  private static final String TAG = "Id3Reader";
  private final ParsableByteArray id3Header = new ParsableByteArray(10);
  private TrackOutput output;
  private int sampleBytesRead;
  private int sampleSize;
  private long sampleTimeUs;
  private boolean writingSample;

  public void consume(ParsableByteArray paramParsableByteArray)
  {
    if (!this.writingSample)
      return;
    int i = paramParsableByteArray.bytesLeft();
    if (this.sampleBytesRead < 10)
    {
      int j = Math.min(i, 10 - this.sampleBytesRead);
      System.arraycopy(paramParsableByteArray.data, paramParsableByteArray.getPosition(), this.id3Header.data, this.sampleBytesRead, j);
      if (j + this.sampleBytesRead == 10)
      {
        this.id3Header.setPosition(0);
        if ((73 != this.id3Header.readUnsignedByte()) || (68 != this.id3Header.readUnsignedByte()) || (51 != this.id3Header.readUnsignedByte()))
        {
          Log.w("Id3Reader", "Discarding invalid ID3 tag");
          this.writingSample = false;
          return;
        }
        this.id3Header.skipBytes(3);
        this.sampleSize = (this.id3Header.readSynchSafeInt() + 10);
      }
    }
    i = Math.min(i, this.sampleSize - this.sampleBytesRead);
    this.output.sampleData(paramParsableByteArray, i);
    this.sampleBytesRead = (i + this.sampleBytesRead);
  }

  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getNextId());
    this.output.format(Format.createSampleFormat(null, "application/id3", null, -1, null));
  }

  public void packetFinished()
  {
    if ((!this.writingSample) || (this.sampleSize == 0) || (this.sampleBytesRead != this.sampleSize))
      return;
    this.output.sampleMetadata(this.sampleTimeUs, 1, this.sampleSize, 0, null);
    this.writingSample = false;
  }

  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    if (!paramBoolean)
      return;
    this.writingSample = true;
    this.sampleTimeUs = paramLong;
    this.sampleSize = 0;
    this.sampleBytesRead = 0;
  }

  public void seek()
  {
    this.writingSample = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.Id3Reader
 * JD-Core Version:    0.6.0
 */