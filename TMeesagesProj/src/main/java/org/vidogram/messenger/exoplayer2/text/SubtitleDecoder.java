package org.vidogram.messenger.exoplayer2.text;

import org.vidogram.messenger.exoplayer2.decoder.Decoder;

public abstract interface SubtitleDecoder extends Decoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
{
  public abstract void setPositionUs(long paramLong);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SubtitleDecoder
 * JD-Core Version:    0.6.0
 */