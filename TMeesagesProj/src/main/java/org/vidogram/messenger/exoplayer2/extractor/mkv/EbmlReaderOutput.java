package org.vidogram.messenger.exoplayer2.extractor.mkv;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;

abstract interface EbmlReaderOutput
{
  public abstract void binaryElement(int paramInt1, int paramInt2, ExtractorInput paramExtractorInput);

  public abstract void endMasterElement(int paramInt);

  public abstract void floatElement(int paramInt, double paramDouble);

  public abstract int getElementType(int paramInt);

  public abstract void integerElement(int paramInt, long paramLong);

  public abstract boolean isLevel1Element(int paramInt);

  public abstract void startMasterElement(int paramInt, long paramLong1, long paramLong2);

  public abstract void stringElement(int paramInt, String paramString);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mkv.EbmlReaderOutput
 * JD-Core Version:    0.6.0
 */