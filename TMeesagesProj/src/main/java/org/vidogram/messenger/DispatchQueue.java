package org.vidogram.messenger;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import java.util.concurrent.CountDownLatch;

public class DispatchQueue extends Thread
{
  private volatile Handler handler = null;
  private CountDownLatch syncLatch = new CountDownLatch(1);

  public DispatchQueue(String paramString)
  {
    setName(paramString);
    start();
  }

  private void sendMessage(Message paramMessage, int paramInt)
  {
    try
    {
      this.syncLatch.await();
      if (paramInt <= 0)
      {
        this.handler.sendMessage(paramMessage);
        return;
      }
      this.handler.sendMessageDelayed(paramMessage, paramInt);
      return;
    }
    catch (Exception paramMessage)
    {
      FileLog.e(paramMessage);
    }
  }

  public void cancelRunnable(Runnable paramRunnable)
  {
    try
    {
      this.syncLatch.await();
      this.handler.removeCallbacks(paramRunnable);
      return;
    }
    catch (Exception paramRunnable)
    {
      FileLog.e(paramRunnable);
    }
  }

  public void cleanupQueue()
  {
    try
    {
      this.syncLatch.await();
      this.handler.removeCallbacksAndMessages(null);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void postRunnable(Runnable paramRunnable)
  {
    postRunnable(paramRunnable, 0L);
  }

  public void postRunnable(Runnable paramRunnable, long paramLong)
  {
    try
    {
      this.syncLatch.await();
      if (paramLong <= 0L)
      {
        this.handler.post(paramRunnable);
        return;
      }
      this.handler.postDelayed(paramRunnable, paramLong);
      return;
    }
    catch (Exception paramRunnable)
    {
      FileLog.e(paramRunnable);
    }
  }

  public void run()
  {
    Looper.prepare();
    this.handler = new Handler();
    this.syncLatch.countDown();
    Looper.loop();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.DispatchQueue
 * JD-Core Version:    0.6.0
 */