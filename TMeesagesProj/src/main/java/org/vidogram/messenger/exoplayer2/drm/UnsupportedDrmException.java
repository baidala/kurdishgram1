package org.vidogram.messenger.exoplayer2.drm;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class UnsupportedDrmException extends Exception
{
  public static final int REASON_INSTANTIATION_ERROR = 2;
  public static final int REASON_UNSUPPORTED_SCHEME = 1;
  public final int reason;

  public UnsupportedDrmException(int paramInt)
  {
    this.reason = paramInt;
  }

  public UnsupportedDrmException(int paramInt, Exception paramException)
  {
    super(paramException);
    this.reason = paramInt;
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Reason
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.UnsupportedDrmException
 * JD-Core Version:    0.6.0
 */