package org.vidogram.messenger.exoplayer2.extractor.mkv;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;

abstract interface EbmlReader
{
  public static final int TYPE_BINARY = 4;
  public static final int TYPE_FLOAT = 5;
  public static final int TYPE_MASTER = 1;
  public static final int TYPE_STRING = 3;
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_UNSIGNED_INT = 2;

  public abstract void init(EbmlReaderOutput paramEbmlReaderOutput);

  public abstract boolean read(ExtractorInput paramExtractorInput);

  public abstract void reset();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mkv.EbmlReader
 * JD-Core Version:    0.6.0
 */