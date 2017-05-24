package org.vidogram.tgnet;

public abstract interface FileLoadOperationDelegate
{
  public abstract void onFailed(int paramInt);

  public abstract void onFinished(String paramString);

  public abstract void onProgressChanged(float paramFloat);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.tgnet.FileLoadOperationDelegate
 * JD-Core Version:    0.6.0
 */