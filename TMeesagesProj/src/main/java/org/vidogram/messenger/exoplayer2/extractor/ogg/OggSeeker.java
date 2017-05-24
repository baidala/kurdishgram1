package org.vidogram.messenger.exoplayer2.extractor.ogg;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;

abstract interface OggSeeker
{
  public abstract SeekMap createSeekMap();

  public abstract long read(ExtractorInput paramExtractorInput);

  public abstract long startSeek();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ogg.OggSeeker
 * JD-Core Version:    0.6.0
 */