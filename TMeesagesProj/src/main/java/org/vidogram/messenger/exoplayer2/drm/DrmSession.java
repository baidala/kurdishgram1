package org.vidogram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@TargetApi(16)
public abstract interface DrmSession<T extends ExoMediaCrypto>
{
  public static final int STATE_CLOSED = 1;
  public static final int STATE_ERROR = 0;
  public static final int STATE_OPENED = 3;
  public static final int STATE_OPENED_WITH_KEYS = 4;
  public static final int STATE_OPENING = 2;

  public abstract Exception getError();

  public abstract T getMediaCrypto();

  public abstract int getState();

  public abstract boolean requiresSecureDecoderComponent(String paramString);

  @Retention(RetentionPolicy.SOURCE)
  public static @interface State
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.DrmSession
 * JD-Core Version:    0.6.0
 */