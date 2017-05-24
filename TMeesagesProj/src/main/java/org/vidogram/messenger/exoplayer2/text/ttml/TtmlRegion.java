package org.vidogram.messenger.exoplayer2.text.ttml;

final class TtmlRegion
{
  public final float line;
  public final int lineType;
  public final float position;
  public final float width;

  public TtmlRegion()
  {
    this(1.4E-45F, 1.4E-45F, -2147483648, 1.4E-45F);
  }

  public TtmlRegion(float paramFloat1, float paramFloat2, int paramInt, float paramFloat3)
  {
    this.position = paramFloat1;
    this.line = paramFloat2;
    this.lineType = paramInt;
    this.width = paramFloat3;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.ttml.TtmlRegion
 * JD-Core Version:    0.6.0
 */