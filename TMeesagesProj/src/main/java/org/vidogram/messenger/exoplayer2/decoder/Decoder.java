package org.vidogram.messenger.exoplayer2.decoder;

public abstract interface Decoder<I, O, E extends Exception>
{
  public abstract I dequeueInputBuffer();

  public abstract O dequeueOutputBuffer();

  public abstract void flush();

  public abstract String getName();

  public abstract void queueInputBuffer(I paramI);

  public abstract void release();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.decoder.Decoder
 * JD-Core Version:    0.6.0
 */