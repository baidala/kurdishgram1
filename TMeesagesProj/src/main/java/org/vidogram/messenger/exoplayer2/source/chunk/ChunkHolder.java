package org.vidogram.messenger.exoplayer2.source.chunk;

public final class ChunkHolder
{
  public Chunk chunk;
  public boolean endOfStream;

  public void clear()
  {
    this.chunk = null;
    this.endOfStream = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ChunkHolder
 * JD-Core Version:    0.6.0
 */