package org.vidogram.messenger.exoplayer2.upstream;

public final class Allocation
{
  public final byte[] data;
  private final int offset;

  public Allocation(byte[] paramArrayOfByte, int paramInt)
  {
    this.data = paramArrayOfByte;
    this.offset = paramInt;
  }

  public int translateOffset(int paramInt)
  {
    return this.offset + paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.Allocation
 * JD-Core Version:    0.6.0
 */