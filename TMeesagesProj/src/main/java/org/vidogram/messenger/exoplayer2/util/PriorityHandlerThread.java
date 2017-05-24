package org.vidogram.messenger.exoplayer2.util;

import android.os.HandlerThread;
import android.os.Process;

public final class PriorityHandlerThread extends HandlerThread
{
  private final int priority;

  public PriorityHandlerThread(String paramString, int paramInt)
  {
    super(paramString);
    this.priority = paramInt;
  }

  public void run()
  {
    Process.setThreadPriority(this.priority);
    super.run();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.PriorityHandlerThread
 * JD-Core Version:    0.6.0
 */