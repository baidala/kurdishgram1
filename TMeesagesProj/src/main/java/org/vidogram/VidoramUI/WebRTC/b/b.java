package org.vidogram.VidogramUi.WebRTC.b;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

public class b extends Thread
  implements Executor
{
  private final Object a = new Object();
  private final List<Runnable> b = new LinkedList();
  private Handler c = null;
  private boolean d = false;
  private long e;

  public void a()
  {
    monitorenter;
    while (true)
    {
      try
      {
        boolean bool = this.d;
        if (bool)
          return;
        this.d = true;
        this.c = null;
        start();
        synchronized (this.a)
        {
          Handler localHandler = this.c;
          if (localHandler == null)
            try
            {
              this.a.wait();
            }
            catch (InterruptedException localInterruptedException)
            {
              Log.e("LooperExecutor", "Can not start looper thread");
              this.d = false;
            }
        }
      }
      finally
      {
        monitorexit;
      }
      monitorexit;
    }
  }

  public void b()
  {
    monitorenter;
    try
    {
      boolean bool = this.d;
      if (!bool);
      while (true)
      {
        return;
        this.d = false;
        this.c.post(new Runnable()
        {
          public void run()
          {
            b.a(b.this).getLooper().quit();
            Log.d("LooperExecutor", "Looper thread finished.");
          }
        });
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public boolean c()
  {
    return Thread.currentThread().getId() == this.e;
  }

  public void execute(Runnable paramRunnable)
  {
    monitorenter;
    while (true)
    {
      try
      {
        if (this.d)
          continue;
        Log.w("LooperExecutor", "Running looper executor without calling requestStart()");
        return;
        if (Thread.currentThread().getId() == this.e)
        {
          paramRunnable.run();
          continue;
        }
      }
      finally
      {
        monitorexit;
      }
      this.c.post(paramRunnable);
    }
  }

  public void run()
  {
    Looper.prepare();
    synchronized (this.a)
    {
      Log.d("LooperExecutor", "Looper thread started.");
      this.c = new Handler();
      this.e = Thread.currentThread().getId();
      this.a.notify();
      Looper.loop();
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.b
 * JD-Core Version:    0.6.0
 */