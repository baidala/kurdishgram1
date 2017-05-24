package org.vidogram.messenger.exoplayer2.source.dash.manifest;

public final class UtcTimingElement
{
  public final String schemeIdUri;
  public final String value;

  public UtcTimingElement(String paramString1, String paramString2)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
  }

  public String toString()
  {
    return this.schemeIdUri + ", " + this.value;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.UtcTimingElement
 * JD-Core Version:    0.6.0
 */