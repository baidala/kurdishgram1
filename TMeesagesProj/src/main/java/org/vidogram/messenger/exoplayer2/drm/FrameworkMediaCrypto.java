package org.vidogram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import org.vidogram.messenger.exoplayer2.util.Assertions;

@TargetApi(16)
public final class FrameworkMediaCrypto
  implements ExoMediaCrypto
{
  private final MediaCrypto mediaCrypto;

  FrameworkMediaCrypto(MediaCrypto paramMediaCrypto)
  {
    this.mediaCrypto = ((MediaCrypto)Assertions.checkNotNull(paramMediaCrypto));
  }

  public MediaCrypto getWrappedMediaCrypto()
  {
    return this.mediaCrypto;
  }

  public boolean requiresSecureDecoderComponent(String paramString)
  {
    return this.mediaCrypto.requiresSecureDecoderComponent(paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.FrameworkMediaCrypto
 * JD-Core Version:    0.6.0
 */