package org.vidogram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.os.Looper;

@TargetApi(16)
public abstract interface DrmSessionManager<T extends ExoMediaCrypto>
{
  public abstract DrmSession<T> acquireSession(Looper paramLooper, DrmInitData paramDrmInitData);

  public abstract void releaseSession(DrmSession<T> paramDrmSession);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.DrmSessionManager
 * JD-Core Version:    0.6.0
 */