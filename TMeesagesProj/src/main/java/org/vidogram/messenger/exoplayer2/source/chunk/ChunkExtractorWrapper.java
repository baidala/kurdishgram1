package org.vidogram.messenger.exoplayer2.source.chunk;

import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class ChunkExtractorWrapper
  implements ExtractorOutput, TrackOutput
{
  private final Extractor extractor;
  private boolean extractorInitialized;
  private final Format manifestFormat;
  private SingleTrackMetadataOutput metadataOutput;
  private final boolean preferManifestDrmInitData;
  private final boolean resendFormatOnInit;
  private boolean seenTrack;
  private int seenTrackId;
  private Format sentFormat;
  private TrackOutput trackOutput;

  public ChunkExtractorWrapper(Extractor paramExtractor, Format paramFormat, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.extractor = paramExtractor;
    this.manifestFormat = paramFormat;
    this.preferManifestDrmInitData = paramBoolean1;
    this.resendFormatOnInit = paramBoolean2;
  }

  public void endTracks()
  {
    Assertions.checkState(this.seenTrack);
  }

  public void format(Format paramFormat)
  {
    this.sentFormat = paramFormat.copyWithManifestFormatInfo(this.manifestFormat, this.preferManifestDrmInitData);
    this.trackOutput.format(this.sentFormat);
  }

  public void init(SingleTrackMetadataOutput paramSingleTrackMetadataOutput, TrackOutput paramTrackOutput)
  {
    this.metadataOutput = paramSingleTrackMetadataOutput;
    this.trackOutput = paramTrackOutput;
    if (!this.extractorInitialized)
    {
      this.extractor.init(this);
      this.extractorInitialized = true;
    }
    do
    {
      return;
      this.extractor.seek(0L);
    }
    while ((!this.resendFormatOnInit) || (this.sentFormat == null));
    paramTrackOutput.format(this.sentFormat);
  }

  public int read(ExtractorInput paramExtractorInput)
  {
    boolean bool = true;
    int i = this.extractor.read(paramExtractorInput, null);
    if (i != 1);
    while (true)
    {
      Assertions.checkState(bool);
      return i;
      bool = false;
    }
  }

  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
  {
    return this.trackOutput.sampleData(paramExtractorInput, paramInt, paramBoolean);
  }

  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    this.trackOutput.sampleData(paramParsableByteArray, paramInt);
  }

  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.trackOutput.sampleMetadata(paramLong, paramInt1, paramInt2, paramInt3, paramArrayOfByte);
  }

  public void seekMap(SeekMap paramSeekMap)
  {
    this.metadataOutput.seekMap(paramSeekMap);
  }

  public TrackOutput track(int paramInt)
  {
    if ((!this.seenTrack) || (this.seenTrackId == paramInt));
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.seenTrack = true;
      this.seenTrackId = paramInt;
      return this;
    }
  }

  public static abstract interface SingleTrackMetadataOutput
  {
    public abstract void seekMap(SeekMap paramSeekMap);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper
 * JD-Core Version:    0.6.0
 */