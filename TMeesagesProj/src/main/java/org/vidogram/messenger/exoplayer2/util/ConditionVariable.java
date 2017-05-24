package org.vidogram.messenger.exoplayer2.util;

public final class ConditionVariable
{
  private boolean isOpen;

  public void block()
  {
    monitorenter;
    try
    {
      if (!this.isOpen)
        wait();
    }
    finally
    {
      monitorexit;
    }
  }

  public boolean close()
  {
    monitorenter;
    try
    {
      boolean bool = this.isOpen;
      this.isOpen = false;
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public boolean open()
  {
    int i = 1;
    monitorenter;
    try
    {
      boolean bool = this.isOpen;
      if (bool)
        i = 0;
      while (true)
      {
        return i;
        this.isOpen = true;
        notifyAll();
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.ConditionVariable
 * JD-Core Version:    0.6.0
 */