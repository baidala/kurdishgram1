package org.vidogram.messenger.exoplayer2.upstream;

import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class DefaultAllocator
  implements Allocator
{
  private static final int AVAILABLE_EXTRA_CAPACITY = 100;
  private int allocatedCount;
  private Allocation[] availableAllocations;
  private int availableCount;
  private final int individualAllocationSize;
  private final byte[] initialAllocationBlock;
  private final Allocation[] singleAllocationReleaseHolder;
  private int targetBufferSize;
  private final boolean trimOnReset;

  public DefaultAllocator(boolean paramBoolean, int paramInt)
  {
    this(paramBoolean, paramInt, 0);
  }

  public DefaultAllocator(boolean paramBoolean, int paramInt1, int paramInt2)
  {
    if (paramInt1 > 0)
    {
      bool = true;
      Assertions.checkArgument(bool);
      if (paramInt2 < 0)
        break label113;
    }
    label113: for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.trimOnReset = paramBoolean;
      this.individualAllocationSize = paramInt1;
      this.availableCount = paramInt2;
      this.availableAllocations = new Allocation[paramInt2 + 100];
      if (paramInt2 <= 0)
        break label119;
      this.initialAllocationBlock = new byte[paramInt2 * paramInt1];
      while (i < paramInt2)
      {
        this.availableAllocations[i] = new Allocation(this.initialAllocationBlock, i * paramInt1);
        i += 1;
      }
      bool = false;
      break;
    }
    label119: this.initialAllocationBlock = null;
    this.singleAllocationReleaseHolder = new Allocation[1];
  }

  public Allocation allocate()
  {
    monitorenter;
    try
    {
      this.allocatedCount += 1;
      Object localObject1;
      if (this.availableCount > 0)
      {
        localObject1 = this.availableAllocations;
        int i = this.availableCount - 1;
        this.availableCount = i;
        localObject1 = localObject1[i];
        this.availableAllocations[this.availableCount] = null;
      }
      while (true)
      {
        return localObject1;
        localObject1 = new Allocation(new byte[this.individualAllocationSize], 0);
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject2;
  }

  public int getIndividualAllocationLength()
  {
    return this.individualAllocationSize;
  }

  public int getTotalBytesAllocated()
  {
    monitorenter;
    try
    {
      int i = this.allocatedCount;
      int j = this.individualAllocationSize;
      monitorexit;
      return i * j;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void release(Allocation paramAllocation)
  {
    monitorenter;
    try
    {
      this.singleAllocationReleaseHolder[0] = paramAllocation;
      release(this.singleAllocationReleaseHolder);
      monitorexit;
      return;
    }
    finally
    {
      paramAllocation = finally;
      monitorexit;
    }
    throw paramAllocation;
  }

  public void release(Allocation[] paramArrayOfAllocation)
  {
    monitorenter;
    while (true)
    {
      try
      {
        if (this.availableCount + paramArrayOfAllocation.length < this.availableAllocations.length)
          continue;
        this.availableAllocations = ((Allocation[])Arrays.copyOf(this.availableAllocations, Math.max(this.availableAllocations.length * 2, this.availableCount + paramArrayOfAllocation.length)));
        int j = paramArrayOfAllocation.length;
        int i = 0;
        if (i >= j)
          continue;
        Allocation localAllocation = paramArrayOfAllocation[i];
        if (localAllocation.data != this.initialAllocationBlock)
        {
          if (localAllocation.data.length != this.individualAllocationSize)
            break label159;
          break label153;
          Assertions.checkArgument(bool);
          Allocation[] arrayOfAllocation = this.availableAllocations;
          int k = this.availableCount;
          this.availableCount = (k + 1);
          arrayOfAllocation[k] = localAllocation;
          i += 1;
          continue;
          this.allocatedCount -= paramArrayOfAllocation.length;
          notifyAll();
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      label153: boolean bool = true;
      continue;
      label159: bool = false;
    }
  }

  public void reset()
  {
    monitorenter;
    try
    {
      if (this.trimOnReset)
        setTargetBufferSize(0);
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void setTargetBufferSize(int paramInt)
  {
    monitorenter;
    try
    {
      if (paramInt < this.targetBufferSize);
      for (int i = 1; ; i = 0)
      {
        this.targetBufferSize = paramInt;
        if (i != 0)
          trim();
        return;
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public void trim()
  {
    int i = 0;
    monitorenter;
    while (true)
    {
      int k;
      try
      {
        k = Math.max(0, Util.ceilDivide(this.targetBufferSize, this.individualAllocationSize) - this.allocatedCount);
        int j = this.availableCount;
        if (k >= j)
          return;
        if (this.initialAllocationBlock != null)
        {
          j = this.availableCount - 1;
          if (i > j)
            continue;
          Allocation localAllocation1 = this.availableAllocations[i];
          if (localAllocation1.data != this.initialAllocationBlock)
            continue;
          i += 1;
          continue;
          Allocation localAllocation2 = this.availableAllocations[j];
          if (localAllocation2.data == this.initialAllocationBlock)
            continue;
          j -= 1;
          continue;
          this.availableAllocations[i] = localAllocation2;
          this.availableAllocations[j] = localAllocation1;
          j -= 1;
          i += 1;
          continue;
          i = Math.max(k, i);
          if (i >= this.availableCount)
            continue;
          Arrays.fill(this.availableAllocations, i, this.availableCount, null);
          this.availableCount = i;
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      i = k;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DefaultAllocator
 * JD-Core Version:    0.6.0
 */