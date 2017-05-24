package org.vidogram.messenger.exoplayer2.extractor;

public abstract interface ExtractorInput
{
  public abstract void advancePeekPosition(int paramInt);

  public abstract boolean advancePeekPosition(int paramInt, boolean paramBoolean);

  public abstract long getLength();

  public abstract long getPeekPosition();

  public abstract long getPosition();

  public abstract void peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract boolean peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public abstract boolean readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean);

  public abstract void resetPeekPosition();

  public abstract <E extends Throwable> void setRetryPosition(long paramLong, E paramE);

  public abstract int skip(int paramInt);

  public abstract void skipFully(int paramInt);

  public abstract boolean skipFully(int paramInt, boolean paramBoolean);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ExtractorInput
 * JD-Core Version:    0.6.0
 */