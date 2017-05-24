package org.vidogram.messenger.exoplayer2.metadata;

public abstract interface MetadataDecoder
{
  public abstract boolean canDecode(String paramString);

  public abstract Metadata decode(byte[] paramArrayOfByte, int paramInt);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.MetadataDecoder
 * JD-Core Version:    0.6.0
 */