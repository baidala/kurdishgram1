package org.vidogram.messenger.exoplayer2.util;

import java.io.IOException;
import java.util.Collections;
import java.util.PriorityQueue;

public final class PriorityTaskManager
{
  private int highestPriority = -2147483648;
  private final Object lock = new Object();
  private final PriorityQueue<Integer> queue = new PriorityQueue(10, Collections.reverseOrder());

  public void add(int paramInt)
  {
    synchronized (this.lock)
    {
      this.queue.add(Integer.valueOf(paramInt));
      this.highestPriority = Math.max(this.highestPriority, paramInt);
      return;
    }
  }

  public void proceed(int paramInt)
  {
    synchronized (this.lock)
    {
      if (this.highestPriority != paramInt)
        this.lock.wait();
    }
    monitorexit;
  }

  public boolean proceedNonBlocking(int paramInt)
  {
    while (true)
    {
      synchronized (this.lock)
      {
        if (this.highestPriority == paramInt)
        {
          i = 1;
          return i;
        }
      }
      int i = 0;
    }
  }

  public void proceedOrThrow(int paramInt)
  {
    synchronized (this.lock)
    {
      if (this.highestPriority != paramInt)
        throw new PriorityTooLowException(paramInt, this.highestPriority);
    }
    monitorexit;
  }

  public void remove(int paramInt)
  {
    synchronized (this.lock)
    {
      this.queue.remove(Integer.valueOf(paramInt));
      if (this.queue.isEmpty())
      {
        paramInt = -2147483648;
        this.highestPriority = paramInt;
        this.lock.notifyAll();
        return;
      }
      paramInt = ((Integer)this.queue.peek()).intValue();
    }
  }

  public static class PriorityTooLowException extends IOException
  {
    public PriorityTooLowException(int paramInt1, int paramInt2)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.PriorityTaskManager
 * JD-Core Version:    0.6.0
 */