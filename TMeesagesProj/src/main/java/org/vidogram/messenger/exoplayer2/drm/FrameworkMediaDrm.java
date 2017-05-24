package org.vidogram.messenger.exoplayer2.drm;

import android.annotation.TargetApi;
import android.media.MediaCrypto;
import android.media.MediaDrm;
import android.media.MediaDrm.KeyRequest;
import android.media.MediaDrm.OnEventListener;
import android.media.MediaDrm.ProvisionRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.vidogram.messenger.exoplayer2.util.Assertions;

@TargetApi(18)
public final class FrameworkMediaDrm
  implements ExoMediaDrm<FrameworkMediaCrypto>
{
  private final MediaDrm mediaDrm;

  private FrameworkMediaDrm(UUID paramUUID)
  {
    this.mediaDrm = new MediaDrm((UUID)Assertions.checkNotNull(paramUUID));
  }

  public static FrameworkMediaDrm newInstance(UUID paramUUID)
  {
    try
    {
      paramUUID = new FrameworkMediaDrm(paramUUID);
      return paramUUID;
    }
    catch (android.media.UnsupportedSchemeException paramUUID)
    {
      throw new UnsupportedDrmException(1, paramUUID);
    }
    catch (java.lang.Exception paramUUID)
    {
    }
    throw new UnsupportedDrmException(2, paramUUID);
  }

  public void closeSession(byte[] paramArrayOfByte)
  {
    this.mediaDrm.closeSession(paramArrayOfByte);
  }

  public FrameworkMediaCrypto createMediaCrypto(UUID paramUUID, byte[] paramArrayOfByte)
  {
    return new FrameworkMediaCrypto(new MediaCrypto(paramUUID, paramArrayOfByte));
  }

  public ExoMediaDrm.KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap)
  {
    return new ExoMediaDrm.KeyRequest(this.mediaDrm.getKeyRequest(paramArrayOfByte1, paramArrayOfByte2, paramString, paramInt, paramHashMap))
    {
      public byte[] getData()
      {
        return this.val$request.getData();
      }

      public String getDefaultUrl()
      {
        return this.val$request.getDefaultUrl();
      }
    };
  }

  public byte[] getPropertyByteArray(String paramString)
  {
    return this.mediaDrm.getPropertyByteArray(paramString);
  }

  public String getPropertyString(String paramString)
  {
    return this.mediaDrm.getPropertyString(paramString);
  }

  public ExoMediaDrm.ProvisionRequest getProvisionRequest()
  {
    return new ExoMediaDrm.ProvisionRequest(this.mediaDrm.getProvisionRequest())
    {
      public byte[] getData()
      {
        return this.val$provisionRequest.getData();
      }

      public String getDefaultUrl()
      {
        return this.val$provisionRequest.getDefaultUrl();
      }
    };
  }

  public byte[] openSession()
  {
    return this.mediaDrm.openSession();
  }

  public byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    return this.mediaDrm.provideKeyResponse(paramArrayOfByte1, paramArrayOfByte2);
  }

  public void provideProvisionResponse(byte[] paramArrayOfByte)
  {
    this.mediaDrm.provideProvisionResponse(paramArrayOfByte);
  }

  public Map<String, String> queryKeyStatus(byte[] paramArrayOfByte)
  {
    return this.mediaDrm.queryKeyStatus(paramArrayOfByte);
  }

  public void release()
  {
    this.mediaDrm.release();
  }

  public void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    this.mediaDrm.restoreKeys(paramArrayOfByte1, paramArrayOfByte2);
  }

  public void setOnEventListener(ExoMediaDrm.OnEventListener<? super FrameworkMediaCrypto> paramOnEventListener)
  {
    MediaDrm localMediaDrm = this.mediaDrm;
    if (paramOnEventListener == null);
    for (paramOnEventListener = null; ; paramOnEventListener = new MediaDrm.OnEventListener(paramOnEventListener)
    {
      public void onEvent(MediaDrm paramMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
      {
        this.val$listener.onEvent(FrameworkMediaDrm.this, paramArrayOfByte1, paramInt1, paramInt2, paramArrayOfByte2);
      }
    })
    {
      localMediaDrm.setOnEventListener(paramOnEventListener);
      return;
    }
  }

  public void setPropertyByteArray(String paramString, byte[] paramArrayOfByte)
  {
    this.mediaDrm.setPropertyByteArray(paramString, paramArrayOfByte);
  }

  public void setPropertyString(String paramString1, String paramString2)
  {
    this.mediaDrm.setPropertyString(paramString1, paramString2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.FrameworkMediaDrm
 * JD-Core Version:    0.6.0
 */