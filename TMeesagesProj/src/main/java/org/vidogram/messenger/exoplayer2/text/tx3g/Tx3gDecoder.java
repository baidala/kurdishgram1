package org.vidogram.messenger.exoplayer2.text.tx3g;

import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.vidogram.messenger.exoplayer2.text.Subtitle;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class Tx3gDecoder extends SimpleSubtitleDecoder
{
  private final ParsableByteArray parsableByteArray = new ParsableByteArray();

  public Tx3gDecoder()
  {
    super("Tx3gDecoder");
  }

  protected Subtitle decode(byte[] paramArrayOfByte, int paramInt)
  {
    this.parsableByteArray.reset(paramArrayOfByte, paramInt);
    paramInt = this.parsableByteArray.readUnsignedShort();
    if (paramInt == 0)
      return Tx3gSubtitle.EMPTY;
    return new Tx3gSubtitle(new Cue(this.parsableByteArray.readString(paramInt)));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.tx3g.Tx3gDecoder
 * JD-Core Version:    0.6.0
 */