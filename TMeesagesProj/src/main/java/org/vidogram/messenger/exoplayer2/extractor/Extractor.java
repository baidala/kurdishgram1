package org.vidogram.messenger.exoplayer2.extractor;

public abstract interface Extractor
{
  public static final int RESULT_CONTINUE = 0;
  public static final int RESULT_END_OF_INPUT = -1;
  public static final int RESULT_SEEK = 1;

  public abstract void init(ExtractorOutput paramExtractorOutput);

  public abstract int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder);

  public abstract void release();

  public abstract void seek(long paramLong);

  public abstract boolean sniff(ExtractorInput paramExtractorInput);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.Extractor
 * JD-Core Version:    0.6.0
 */