package org.vidogram.messenger.exoplayer2.extractor.wav;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;

public final class WavExtractor
  implements Extractor, SeekMap
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new WavExtractor() };
    }
  };
  private static final int MAX_INPUT_SIZE = 32768;
  private int bytesPerFrame;
  private ExtractorOutput extractorOutput;
  private int pendingBytes;
  private TrackOutput trackOutput;
  private WavHeader wavHeader;

  public long getDurationUs()
  {
    return this.wavHeader.getDurationUs();
  }

  public long getPosition(long paramLong)
  {
    return this.wavHeader.getPosition(paramLong);
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    this.trackOutput = paramExtractorOutput.track(0);
    this.wavHeader = null;
    paramExtractorOutput.endTracks();
  }

  public boolean isSeekable()
  {
    return true;
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    if (this.wavHeader == null)
    {
      this.wavHeader = WavHeaderReader.peek(paramExtractorInput);
      if (this.wavHeader == null)
        throw new ParserException("Unsupported or unrecognized wav header.");
      paramPositionHolder = Format.createAudioSampleFormat(null, "audio/raw", null, this.wavHeader.getBitrate(), 32768, this.wavHeader.getNumChannels(), this.wavHeader.getSampleRateHz(), this.wavHeader.getEncoding(), null, null, 0, null);
      this.trackOutput.format(paramPositionHolder);
      this.bytesPerFrame = this.wavHeader.getBytesPerFrame();
    }
    if (!this.wavHeader.hasDataBounds())
    {
      WavHeaderReader.skipToData(paramExtractorInput, this.wavHeader);
      this.extractorOutput.seekMap(this);
    }
    int i = this.trackOutput.sampleData(paramExtractorInput, 32768 - this.pendingBytes, true);
    if (i != -1)
      this.pendingBytes += i;
    int j = this.pendingBytes / this.bytesPerFrame;
    if (j > 0)
    {
      long l = this.wavHeader.getTimeUs(paramExtractorInput.getPosition() - this.pendingBytes);
      j *= this.bytesPerFrame;
      this.pendingBytes -= j;
      this.trackOutput.sampleMetadata(l, 1, j, this.pendingBytes, null);
    }
    if (i == -1)
      return -1;
    return 0;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.pendingBytes = 0;
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    return WavHeaderReader.peek(paramExtractorInput) != null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.wav.WavExtractor
 * JD-Core Version:    0.6.0
 */