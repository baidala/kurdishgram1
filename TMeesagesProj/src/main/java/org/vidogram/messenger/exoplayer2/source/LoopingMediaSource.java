package org.vidogram.messenger.exoplayer2.source;

import android.util.Log;
import android.util.Pair;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.Timeline.Period;
import org.vidogram.messenger.exoplayer2.Timeline.Window;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class LoopingMediaSource
  implements MediaSource
{
  private static final String TAG = "LoopingMediaSource";
  private int childPeriodCount;
  private final MediaSource childSource;
  private final int loopCount;

  public LoopingMediaSource(MediaSource paramMediaSource)
  {
    this(paramMediaSource, 2147483647);
  }

  public LoopingMediaSource(MediaSource paramMediaSource, int paramInt)
  {
    if (paramInt > 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.childSource = paramMediaSource;
      this.loopCount = paramInt;
      return;
    }
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    return this.childSource.createPeriod(paramInt % this.childPeriodCount, paramAllocator, paramLong);
  }

  public void maybeThrowSourceInfoRefreshError()
  {
    this.childSource.maybeThrowSourceInfoRefreshError();
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    this.childSource.prepareSource(new MediaSource.Listener(paramListener)
    {
      public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
      {
        LoopingMediaSource.access$002(LoopingMediaSource.this, paramTimeline.getPeriodCount());
        this.val$listener.onSourceInfoRefreshed(new LoopingMediaSource.LoopingTimeline(paramTimeline, LoopingMediaSource.this.loopCount), paramObject);
      }
    });
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    this.childSource.releasePeriod(paramMediaPeriod);
  }

  public void releaseSource()
  {
    this.childSource.releaseSource();
  }

  private static final class LoopingTimeline extends Timeline
  {
    private final int childPeriodCount;
    private final Timeline childTimeline;
    private final int childWindowCount;
    private final int loopCount;

    public LoopingTimeline(Timeline paramTimeline, int paramInt)
    {
      this.childTimeline = paramTimeline;
      this.childPeriodCount = paramTimeline.getPeriodCount();
      this.childWindowCount = paramTimeline.getWindowCount();
      int i = 2147483647 / this.childPeriodCount;
      if (paramInt > i)
      {
        if (paramInt != 2147483647)
          Log.w("LoopingMediaSource", "Capped loops to avoid overflow: " + paramInt + " -> " + i);
        this.loopCount = i;
        return;
      }
      this.loopCount = paramInt;
    }

    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Pair));
      do
      {
        return -1;
        paramObject = (Pair)paramObject;
      }
      while (!(paramObject.first instanceof Integer));
      return ((Integer)paramObject.first).intValue() * this.childPeriodCount + this.childTimeline.getIndexOfPeriod(paramObject.second);
    }

    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      this.childTimeline.getPeriod(paramInt % this.childPeriodCount, paramPeriod, paramBoolean);
      paramInt /= this.childPeriodCount;
      paramPeriod.windowIndex += this.childWindowCount * paramInt;
      if (paramBoolean)
        paramPeriod.uid = Pair.create(Integer.valueOf(paramInt), paramPeriod.uid);
      return paramPeriod;
    }

    public int getPeriodCount()
    {
      return this.childPeriodCount * this.loopCount;
    }

    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      this.childTimeline.getWindow(paramInt % this.childWindowCount, paramWindow, paramBoolean, paramLong);
      paramInt = paramInt / this.childWindowCount * this.childPeriodCount;
      paramWindow.firstPeriodIndex += paramInt;
      paramWindow.lastPeriodIndex = (paramInt + paramWindow.lastPeriodIndex);
      return paramWindow;
    }

    public int getWindowCount()
    {
      return this.childWindowCount * this.loopCount;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.LoopingMediaSource
 * JD-Core Version:    0.6.0
 */