package org.vidogram.messenger.exoplayer2.text;

final class SimpleSubtitleOutputBuffer extends SubtitleOutputBuffer
{
  private final SimpleSubtitleDecoder owner;

  public SimpleSubtitleOutputBuffer(SimpleSubtitleDecoder paramSimpleSubtitleDecoder)
  {
    this.owner = paramSimpleSubtitleDecoder;
  }

  public final void release()
  {
    this.owner.releaseOutputBuffer(this);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SimpleSubtitleOutputBuffer
 * JD-Core Version:    0.6.0
 */