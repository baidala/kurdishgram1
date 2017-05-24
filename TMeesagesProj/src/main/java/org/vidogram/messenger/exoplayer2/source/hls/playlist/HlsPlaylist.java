package org.vidogram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public abstract class HlsPlaylist
{
  public static final int TYPE_MASTER = 0;
  public static final int TYPE_MEDIA = 1;
  public final String baseUri;
  public final int type;

  protected HlsPlaylist(String paramString, int paramInt)
  {
    this.baseUri = paramString;
    this.type = paramInt;
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Type
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist
 * JD-Core Version:    0.6.0
 */