package org.vidogram.messenger.exoplayer2.extractor.mp4;

public final class TrackEncryptionBox
{
  public final int initializationVectorSize;
  public final boolean isEncrypted;
  public final byte[] keyId;

  public TrackEncryptionBox(boolean paramBoolean, int paramInt, byte[] paramArrayOfByte)
  {
    this.isEncrypted = paramBoolean;
    this.initializationVectorSize = paramInt;
    this.keyId = paramArrayOfByte;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox
 * JD-Core Version:    0.6.0
 */