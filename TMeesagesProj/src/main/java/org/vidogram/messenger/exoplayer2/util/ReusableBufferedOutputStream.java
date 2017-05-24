package org.vidogram.messenger.exoplayer2.util;

import java.io.BufferedOutputStream;
import java.io.OutputStream;

public final class ReusableBufferedOutputStream extends BufferedOutputStream
{
  private boolean closed;

  public ReusableBufferedOutputStream(OutputStream paramOutputStream)
  {
    super(paramOutputStream);
  }

  public ReusableBufferedOutputStream(OutputStream paramOutputStream, int paramInt)
  {
    super(paramOutputStream, paramInt);
  }

  public void close()
  {
    this.closed = true;
    Object localObject1 = null;
    try
    {
      flush();
      try
      {
        label11: this.out.close();
        Object localObject2 = localObject1;
        if (localObject2 != null)
          Util.sneakyThrow(localObject2);
        return;
      }
      catch (Throwable localObject3)
      {
        while (true)
        {
          if (localObject1 == null)
            continue;
          Object localObject3 = localObject1;
        }
      }
    }
    catch (Throwable localThrowable1)
    {
      break label11;
    }
  }

  public void reset(OutputStream paramOutputStream)
  {
    Assertions.checkState(this.closed);
    this.out = paramOutputStream;
    this.closed = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.ReusableBufferedOutputStream
 * JD-Core Version:    0.6.0
 */