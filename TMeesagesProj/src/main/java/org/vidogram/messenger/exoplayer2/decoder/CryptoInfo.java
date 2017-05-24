package org.vidogram.messenger.exoplayer2.decoder;

import android.annotation.TargetApi;
import android.media.MediaCodec.CryptoInfo;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class CryptoInfo
{
  private final MediaCodec.CryptoInfo frameworkCryptoInfo;
  public byte[] iv;
  public byte[] key;
  public int mode;
  public int[] numBytesOfClearData;
  public int[] numBytesOfEncryptedData;
  public int numSubSamples;

  public CryptoInfo()
  {
    if (Util.SDK_INT >= 16);
    for (MediaCodec.CryptoInfo localCryptoInfo = newFrameworkCryptoInfoV16(); ; localCryptoInfo = null)
    {
      this.frameworkCryptoInfo = localCryptoInfo;
      return;
    }
  }

  @TargetApi(16)
  private MediaCodec.CryptoInfo newFrameworkCryptoInfoV16()
  {
    return new MediaCodec.CryptoInfo();
  }

  @TargetApi(16)
  private void updateFrameworkCryptoInfoV16()
  {
    this.frameworkCryptoInfo.set(this.numSubSamples, this.numBytesOfClearData, this.numBytesOfEncryptedData, this.key, this.iv, this.mode);
  }

  @TargetApi(16)
  public MediaCodec.CryptoInfo getFrameworkCryptoInfoV16()
  {
    return this.frameworkCryptoInfo;
  }

  public void set(int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2)
  {
    this.numSubSamples = paramInt1;
    this.numBytesOfClearData = paramArrayOfInt1;
    this.numBytesOfEncryptedData = paramArrayOfInt2;
    this.key = paramArrayOfByte1;
    this.iv = paramArrayOfByte2;
    this.mode = paramInt2;
    if (Util.SDK_INT >= 16)
      updateFrameworkCryptoInfoV16();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.decoder.CryptoInfo
 * JD-Core Version:    0.6.0
 */