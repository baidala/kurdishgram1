package org.vidogram.messenger.exoplayer2.upstream;

public abstract interface Allocator
{
  public abstract Allocation allocate();

  public abstract int getIndividualAllocationLength();

  public abstract int getTotalBytesAllocated();

  public abstract void release(Allocation paramAllocation);

  public abstract void release(Allocation[] paramArrayOfAllocation);

  public abstract void trim();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.Allocator
 * JD-Core Version:    0.6.0
 */