package org.vidogram.messenger.exoplayer2.drm;

import java.util.UUID;

public abstract interface MediaDrmCallback
{
  public abstract byte[] executeKeyRequest(UUID paramUUID, ExoMediaDrm.KeyRequest paramKeyRequest);

  public abstract byte[] executeProvisionRequest(UUID paramUUID, ExoMediaDrm.ProvisionRequest paramProvisionRequest);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.MediaDrmCallback
 * JD-Core Version:    0.6.0
 */