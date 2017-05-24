package org.vidogram.messenger.exoplayer2.drm;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract interface ExoMediaDrm<T extends ExoMediaCrypto>
{
  public abstract void closeSession(byte[] paramArrayOfByte);

  public abstract T createMediaCrypto(UUID paramUUID, byte[] paramArrayOfByte);

  public abstract KeyRequest getKeyRequest(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, String paramString, int paramInt, HashMap<String, String> paramHashMap);

  public abstract byte[] getPropertyByteArray(String paramString);

  public abstract String getPropertyString(String paramString);

  public abstract ProvisionRequest getProvisionRequest();

  public abstract byte[] openSession();

  public abstract byte[] provideKeyResponse(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public abstract void provideProvisionResponse(byte[] paramArrayOfByte);

  public abstract Map<String, String> queryKeyStatus(byte[] paramArrayOfByte);

  public abstract void release();

  public abstract void restoreKeys(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2);

  public abstract void setOnEventListener(OnEventListener<? super T> paramOnEventListener);

  public abstract void setPropertyByteArray(String paramString, byte[] paramArrayOfByte);

  public abstract void setPropertyString(String paramString1, String paramString2);

  public static abstract interface KeyRequest
  {
    public abstract byte[] getData();

    public abstract String getDefaultUrl();
  }

  public static abstract interface OnEventListener<T extends ExoMediaCrypto>
  {
    public abstract void onEvent(ExoMediaDrm<? extends T> paramExoMediaDrm, byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2);
  }

  public static abstract interface ProvisionRequest
  {
    public abstract byte[] getData();

    public abstract String getDefaultUrl();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.ExoMediaDrm
 * JD-Core Version:    0.6.0
 */