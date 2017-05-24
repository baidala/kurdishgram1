package org.webrtc;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThreadUtils
{
  public static void awaitUninterruptibly(CountDownLatch paramCountDownLatch)
  {
    executeUninterruptibly(new BlockingOperation(paramCountDownLatch)
    {
      public void run()
      {
        this.val$latch.await();
      }
    });
  }

  public static boolean awaitUninterruptibly(CountDownLatch paramCountDownLatch, long paramLong)
  {
    int k = 0;
    long l3 = SystemClock.elapsedRealtime();
    int i = 0;
    long l1 = paramLong;
    while (true)
    {
      int j;
      long l2;
      try
      {
        boolean bool = paramCountDownLatch.await(l1, TimeUnit.MILLISECONDS);
        k = bool;
        if (i == 0)
          continue;
        Thread.currentThread().interrupt();
        return k;
      }
      catch (InterruptedException localInterruptedException)
      {
        j = 1;
        i = 1;
        l2 = paramLong - (SystemClock.elapsedRealtime() - l3);
        l1 = l2;
      }
      if (l2 > 0L)
        continue;
      i = j;
    }
  }

  public static void executeUninterruptibly(BlockingOperation paramBlockingOperation)
  {
    int i = 0;
    while (true)
      try
      {
        paramBlockingOperation.run();
        if (i == 0)
          continue;
        Thread.currentThread().interrupt();
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        i = 1;
      }
  }

  public static <V> V invokeAtFrontUninterruptibly(Handler paramHandler, Callable<V> paramCallable)
  {
    if (paramHandler.getLooper().getThread() == Thread.currentThread())
      try
      {
        paramHandler = paramCallable.call();
        return paramHandler;
      }
      catch (Exception paramHandler)
      {
        paramCallable = new RuntimeException("Callable threw exception: " + paramHandler);
        paramCallable.setStackTrace(paramHandler.getStackTrace());
        throw paramCallable;
      }
    1Result local1Result = new Object()
    {
      public V value;
    };
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    paramHandler.post(new Runnable(local1Result, paramCallable, localCountDownLatch)
    {
      public void run()
      {
        RuntimeException localRuntimeException;
        try
        {
          this.val$result.value = this.val$callable.call();
          this.val$barrier.countDown();
          return;
        }
        catch (Exception localException)
        {
          localRuntimeException = new RuntimeException("Callable threw exception: " + localException);
          localRuntimeException.setStackTrace(localException.getStackTrace());
        }
        throw localRuntimeException;
      }
    });
    awaitUninterruptibly(localCountDownLatch);
    return local1Result.value;
  }

  public static void invokeAtFrontUninterruptibly(Handler paramHandler, Runnable paramRunnable)
  {
    if (paramHandler.getLooper().getThread() == Thread.currentThread())
    {
      paramRunnable.run();
      return;
    }
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    paramHandler.postAtFrontOfQueue(new Runnable(paramRunnable, localCountDownLatch)
    {
      public void run()
      {
        this.val$runner.run();
        this.val$barrier.countDown();
      }
    });
    awaitUninterruptibly(localCountDownLatch);
  }

  public static void joinUninterruptibly(Thread paramThread)
  {
    executeUninterruptibly(new BlockingOperation(paramThread)
    {
      public void run()
      {
        this.val$thread.join();
      }
    });
  }

  public static boolean joinUninterruptibly(Thread paramThread, long paramLong)
  {
    long l2 = SystemClock.elapsedRealtime();
    int i = 0;
    long l1 = paramLong;
    while (true)
    {
      if (l1 > 0L);
      try
      {
        paramThread.join(l1);
        if (i != 0)
          Thread.currentThread().interrupt();
        if (paramThread.isAlive())
          break;
        return true;
      }
      catch (InterruptedException localInterruptedException)
      {
        l1 = paramLong - (SystemClock.elapsedRealtime() - l2);
        i = 1;
      }
    }
    return false;
  }

  public static abstract interface BlockingOperation
  {
    public abstract void run();
  }

  public static class ThreadChecker
  {
    private Thread thread = Thread.currentThread();

    public void checkIsOnValidThread()
    {
      if (this.thread == null)
        this.thread = Thread.currentThread();
      if (Thread.currentThread() != this.thread)
        throw new IllegalStateException("Wrong thread");
    }

    public void detachThread()
    {
      this.thread = null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.ThreadUtils
 * JD-Core Version:    0.6.0
 */