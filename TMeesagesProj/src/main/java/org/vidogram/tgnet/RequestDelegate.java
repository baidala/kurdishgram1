package org.vidogram.tgnet;

public abstract interface RequestDelegate
{
  public abstract void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.tgnet.RequestDelegate
 * JD-Core Version:    0.6.0
 */