package org.vidogram.messenger.exoplayer2.source;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.Timeline.Window;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;

public final class MergingMediaSource
  implements MediaSource
{
  private static final int PERIOD_COUNT_UNSET = -1;
  private MediaSource.Listener listener;
  private final MediaSource[] mediaSources;
  private IllegalMergeException mergeError;
  private final ArrayList<MediaSource> pendingTimelineSources;
  private int periodCount;
  private Object primaryManifest;
  private Timeline primaryTimeline;
  private final Timeline.Window window;

  public MergingMediaSource(MediaSource[] paramArrayOfMediaSource)
  {
    this.mediaSources = paramArrayOfMediaSource;
    this.pendingTimelineSources = new ArrayList(Arrays.asList(paramArrayOfMediaSource));
    this.window = new Timeline.Window();
    this.periodCount = -1;
  }

  private IllegalMergeException checkTimelineMerges(Timeline paramTimeline)
  {
    int j = paramTimeline.getWindowCount();
    int i = 0;
    while (i < j)
    {
      if (paramTimeline.getWindow(i, this.window, false).isDynamic)
        return new IllegalMergeException(0);
      i += 1;
    }
    if (this.periodCount == -1)
      this.periodCount = paramTimeline.getPeriodCount();
    do
      return null;
    while (paramTimeline.getPeriodCount() == this.periodCount);
    return new IllegalMergeException(1);
  }

  private void handleSourceInfoRefreshed(int paramInt, Timeline paramTimeline, Object paramObject)
  {
    if (this.mergeError == null)
      this.mergeError = checkTimelineMerges(paramTimeline);
    if (this.mergeError != null);
    do
    {
      return;
      this.pendingTimelineSources.remove(this.mediaSources[paramInt]);
      if (paramInt != 0)
        continue;
      this.primaryTimeline = paramTimeline;
      this.primaryManifest = paramObject;
    }
    while (!this.pendingTimelineSources.isEmpty());
    this.listener.onSourceInfoRefreshed(this.primaryTimeline, this.primaryManifest);
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    MediaPeriod[] arrayOfMediaPeriod = new MediaPeriod[this.mediaSources.length];
    int i = 0;
    while (i < arrayOfMediaPeriod.length)
    {
      arrayOfMediaPeriod[i] = this.mediaSources[i].createPeriod(paramInt, paramAllocator, paramLong);
      i += 1;
    }
    return new MergingMediaPeriod(arrayOfMediaPeriod);
  }

  public void maybeThrowSourceInfoRefreshError()
  {
    if (this.mergeError != null)
      throw this.mergeError;
    MediaSource[] arrayOfMediaSource = this.mediaSources;
    int j = arrayOfMediaSource.length;
    int i = 0;
    while (i < j)
    {
      arrayOfMediaSource[i].maybeThrowSourceInfoRefreshError();
      i += 1;
    }
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    this.listener = paramListener;
    int i = 0;
    while (i < this.mediaSources.length)
    {
      this.mediaSources[i].prepareSource(new MediaSource.Listener(i)
      {
        public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
        {
          MergingMediaSource.this.handleSourceInfoRefreshed(this.val$sourceIndex, paramTimeline, paramObject);
        }
      });
      i += 1;
    }
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    paramMediaPeriod = (MergingMediaPeriod)paramMediaPeriod;
    int i = 0;
    while (i < this.mediaSources.length)
    {
      this.mediaSources[i].releasePeriod(paramMediaPeriod.periods[i]);
      i += 1;
    }
  }

  public void releaseSource()
  {
    MediaSource[] arrayOfMediaSource = this.mediaSources;
    int j = arrayOfMediaSource.length;
    int i = 0;
    while (i < j)
    {
      arrayOfMediaSource[i].releaseSource();
      i += 1;
    }
  }

  public static final class IllegalMergeException extends IOException
  {
    public static final int REASON_PERIOD_COUNT_MISMATCH = 1;
    public static final int REASON_WINDOWS_ARE_DYNAMIC = 0;
    public final int reason;

    public IllegalMergeException(int paramInt)
    {
      this.reason = paramInt;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface Reason
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.MergingMediaSource
 * JD-Core Version:    0.6.0
 */