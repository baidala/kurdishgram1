package org.vidogram.messenger.audioinfo.mp3;

import java.nio.charset.Charset;

public enum ID3v2Encoding
{
  private final Charset charset;
  private final int zeroBytes;

  static
  {
    $VALUES = new ID3v2Encoding[] { ISO_8859_1, UTF_16, UTF_16BE, UTF_8 };
  }

  private ID3v2Encoding(Charset paramCharset, int paramInt)
  {
    this.charset = paramCharset;
    this.zeroBytes = paramInt;
  }

  public Charset getCharset()
  {
    return this.charset;
  }

  public int getZeroBytes()
  {
    return this.zeroBytes;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v2Encoding
 * JD-Core Version:    0.6.0
 */