package org.vidogram.messenger.exoplayer2.upstream;

import android.os.Handler;
import android.os.SystemClock;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.SlidingPercentile;

public final class DefaultBandwidthMeter
  implements BandwidthMeter, TransferListener<Object>
{
  private static final int BYTES_TRANSFERRED_FOR_ESTIMATE = 524288;
  public static final int DEFAULT_MAX_WEIGHT = 2000;
  private static final int ELAPSED_MILLIS_FOR_ESTIMATE = 2000;
  private long bitrateEstimate;
  private final Handler eventHandler;
  private final BandwidthMeter.EventListener eventListener;
  private long sampleBytesTransferred;
  private long sampleStartTimeMs;
  private final SlidingPercentile slidingPercentile;
  private int streamCount;
  private long totalBytesTransferred;
  private long totalElapsedTimeMs;

  public DefaultBandwidthMeter()
  {
    this(null, null);
  }

  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener)
  {
    this(paramHandler, paramEventListener, 2000);
  }

  public DefaultBandwidthMeter(Handler paramHandler, BandwidthMeter.EventListener paramEventListener, int paramInt)
  {
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.slidingPercentile = new SlidingPercentile(paramInt);
    this.bitrateEstimate = -1L;
  }

  private void notifyBandwidthSample(int paramInt, long paramLong1, long paramLong2)
  {
    if ((this.eventHandler != null) && (this.eventListener != null))
      this.eventHandler.post(new Runnable(paramInt, paramLong1, paramLong2)
      {
        public void run()
        {
          DefaultBandwidthMeter.this.eventListener.onBandwidthSample(this.val$elapsedMs, this.val$bytes, this.val$bitrate);
        }
      });
  }

  public long getBitrateEstimate()
  {
    monitorenter;
    try
    {
      long l = this.bitrateEstimate;
      monitorexit;
      return l;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public void onBytesTransferred(Object paramObject, int paramInt)
  {
    monitorenter;
    try
    {
      this.sampleBytesTransferred += paramInt;
      monitorexit;
      return;
    }
    finally
    {
      paramObject = finally;
      monitorexit;
    }
    throw paramObject;
  }

  public void onTransferEnd(Object paramObject)
  {
    monitorenter;
    try
    {
      boolean bool;
      long l2;
      int i;
      float f;
      long l1;
      if (this.streamCount > 0)
      {
        bool = true;
        Assertions.checkState(bool);
        l2 = SystemClock.elapsedRealtime();
        i = (int)(l2 - this.sampleStartTimeMs);
        this.totalElapsedTimeMs += i;
        this.totalBytesTransferred += this.sampleBytesTransferred;
        if (i > 0)
        {
          f = (float)(this.sampleBytesTransferred * 8000L / i);
          this.slidingPercentile.addSample((int)Math.sqrt(this.sampleBytesTransferred), f);
          if ((this.totalElapsedTimeMs >= 2000L) || (this.totalBytesTransferred >= 524288L))
          {
            f = this.slidingPercentile.getPercentile(0.5F);
            if (!Float.isNaN(f))
              break label188;
            l1 = -1L;
          }
        }
      }
      while (true)
      {
        this.bitrateEstimate = l1;
        notifyBandwidthSample(i, this.sampleBytesTransferred, this.bitrateEstimate);
        i = this.streamCount - 1;
        this.streamCount = i;
        if (i > 0)
          this.sampleStartTimeMs = l2;
        this.sampleBytesTransferred = 0L;
        return;
        bool = false;
        break;
        label188: l1 = ()f;
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramObject;
  }

  public void onTransferStart(Object paramObject, DataSpec paramDataSpec)
  {
    monitorenter;
    try
    {
      if (this.streamCount == 0)
        this.sampleStartTimeMs = SystemClock.elapsedRealtime();
      this.streamCount += 1;
      return;
    }
    finally
    {
      monitorexit;
    }
    throw paramObject;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DefaultBandwidthMeter
 * JD-Core Version:    0.6.0
 */