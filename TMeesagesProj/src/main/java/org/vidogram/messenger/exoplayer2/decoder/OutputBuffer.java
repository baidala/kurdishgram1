package org.vidogram.messenger.exoplayer2.decoder;

public abstract class OutputBuffer extends Buffer
{
  public int skippedOutputBufferCount;
  public long timeUs;

  public abstract void release();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.decoder.OutputBuffer
 * JD-Core Version:    0.6.0
 */