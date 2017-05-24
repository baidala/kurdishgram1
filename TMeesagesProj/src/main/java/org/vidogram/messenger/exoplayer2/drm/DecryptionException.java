package org.vidogram.messenger.exoplayer2.drm;

public class DecryptionException extends Exception
{
  private final int errorCode;

  public DecryptionException(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }

  public int getErrorCode()
  {
    return this.errorCode;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.DecryptionException
 * JD-Core Version:    0.6.0
 */