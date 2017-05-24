package org.vidogram.messenger.exoplayer2;

public abstract interface RendererCapabilities
{
  public static final int ADAPTIVE_NOT_SEAMLESS = 4;
  public static final int ADAPTIVE_NOT_SUPPORTED = 0;
  public static final int ADAPTIVE_SEAMLESS = 8;
  public static final int ADAPTIVE_SUPPORT_MASK = 12;
  public static final int FORMAT_EXCEEDS_CAPABILITIES = 2;
  public static final int FORMAT_HANDLED = 3;
  public static final int FORMAT_SUPPORT_MASK = 3;
  public static final int FORMAT_UNSUPPORTED_SUBTYPE = 1;
  public static final int FORMAT_UNSUPPORTED_TYPE = 0;

  public abstract int getTrackType();

  public abstract int supportsFormat(Format paramFormat);

  public abstract int supportsMixedMimeTypeAdaptation();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.RendererCapabilities
 * JD-Core Version:    0.6.0
 */