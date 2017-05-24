package org.vidogram.messenger.exoplayer2.text.cea;

import org.vidogram.messenger.exoplayer2.text.SubtitleOutputBuffer;

public final class CeaOutputBuffer extends SubtitleOutputBuffer
{
  private final CeaDecoder owner;

  public CeaOutputBuffer(CeaDecoder paramCeaDecoder)
  {
    this.owner = paramCeaDecoder;
  }

  public final void release()
  {
    this.owner.releaseOutputBuffer(this);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.cea.CeaOutputBuffer
 * JD-Core Version:    0.6.0
 */