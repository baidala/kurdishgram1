package org.vidogram.messenger.exoplayer2.source;

import android.util.Pair;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import org.vidogram.messenger.exoplayer2.Timeline;
import org.vidogram.messenger.exoplayer2.Timeline.Period;
import org.vidogram.messenger.exoplayer2.Timeline.Window;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class ConcatenatingMediaSource
  implements MediaSource
{
  private final boolean[] duplicateFlags;
  private MediaSource.Listener listener;
  private final Object[] manifests;
  private final MediaSource[] mediaSources;
  private final Map<MediaPeriod, Integer> sourceIndexByMediaPeriod;
  private ConcatenatedTimeline timeline;
  private final Timeline[] timelines;

  public ConcatenatingMediaSource(MediaSource[] paramArrayOfMediaSource)
  {
    this.mediaSources = paramArrayOfMediaSource;
    this.timelines = new Timeline[paramArrayOfMediaSource.length];
    this.manifests = new Object[paramArrayOfMediaSource.length];
    this.sourceIndexByMediaPeriod = new HashMap();
    this.duplicateFlags = buildDuplicateFlags(paramArrayOfMediaSource);
  }

  private static boolean[] buildDuplicateFlags(MediaSource[] paramArrayOfMediaSource)
  {
    boolean[] arrayOfBoolean = new boolean[paramArrayOfMediaSource.length];
    IdentityHashMap localIdentityHashMap = new IdentityHashMap(paramArrayOfMediaSource.length);
    int i = 0;
    if (i < paramArrayOfMediaSource.length)
    {
      MediaSource localMediaSource = paramArrayOfMediaSource[i];
      if (!localIdentityHashMap.containsKey(localMediaSource))
        localIdentityHashMap.put(localMediaSource, null);
      while (true)
      {
        i += 1;
        break;
        arrayOfBoolean[i] = true;
      }
    }
    return arrayOfBoolean;
  }

  private void handleSourceInfoRefreshed(int paramInt, Timeline paramTimeline, Object paramObject)
  {
    this.timelines[paramInt] = paramTimeline;
    this.manifests[paramInt] = paramObject;
    int i = paramInt + 1;
    while (i < this.mediaSources.length)
    {
      if (this.mediaSources[i] == this.mediaSources[paramInt])
      {
        this.timelines[i] = paramTimeline;
        this.manifests[i] = paramObject;
      }
      i += 1;
    }
    paramTimeline = this.timelines;
    i = paramTimeline.length;
    paramInt = 0;
    while (paramInt < i)
    {
      if (paramTimeline[paramInt] == null)
        return;
      paramInt += 1;
    }
    this.timeline = new ConcatenatedTimeline((Timeline[])this.timelines.clone());
    this.listener.onSourceInfoRefreshed(this.timeline, this.manifests.clone());
  }

  public MediaPeriod createPeriod(int paramInt, Allocator paramAllocator, long paramLong)
  {
    int i = this.timeline.getSourceIndexForPeriod(paramInt);
    int j = this.timeline.getFirstPeriodIndexInSource(i);
    paramAllocator = this.mediaSources[i].createPeriod(paramInt - j, paramAllocator, paramLong);
    this.sourceIndexByMediaPeriod.put(paramAllocator, Integer.valueOf(i));
    return paramAllocator;
  }

  public void maybeThrowSourceInfoRefreshError()
  {
    int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0)
        this.mediaSources[i].maybeThrowSourceInfoRefreshError();
      i += 1;
    }
  }

  public void prepareSource(MediaSource.Listener paramListener)
  {
    this.listener = paramListener;
    int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0)
        this.mediaSources[i].prepareSource(new MediaSource.Listener(i)
        {
          public void onSourceInfoRefreshed(Timeline paramTimeline, Object paramObject)
          {
            ConcatenatingMediaSource.this.handleSourceInfoRefreshed(this.val$index, paramTimeline, paramObject);
          }
        });
      i += 1;
    }
  }

  public void releasePeriod(MediaPeriod paramMediaPeriod)
  {
    int i = ((Integer)this.sourceIndexByMediaPeriod.get(paramMediaPeriod)).intValue();
    this.sourceIndexByMediaPeriod.remove(paramMediaPeriod);
    this.mediaSources[i].releasePeriod(paramMediaPeriod);
  }

  public void releaseSource()
  {
    int i = 0;
    while (i < this.mediaSources.length)
    {
      if (this.duplicateFlags[i] == 0)
        this.mediaSources[i].releaseSource();
      i += 1;
    }
  }

  private static final class ConcatenatedTimeline extends Timeline
  {
    private final int[] sourcePeriodOffsets;
    private final int[] sourceWindowOffsets;
    private final Timeline[] timelines;

    public ConcatenatedTimeline(Timeline[] paramArrayOfTimeline)
    {
      int[] arrayOfInt1 = new int[paramArrayOfTimeline.length];
      int[] arrayOfInt2 = new int[paramArrayOfTimeline.length];
      int j = 0;
      int k = 0;
      while (i < paramArrayOfTimeline.length)
      {
        Timeline localTimeline = paramArrayOfTimeline[i];
        k += localTimeline.getPeriodCount();
        arrayOfInt1[i] = k;
        j += localTimeline.getWindowCount();
        arrayOfInt2[i] = j;
        i += 1;
      }
      this.timelines = paramArrayOfTimeline;
      this.sourcePeriodOffsets = arrayOfInt1;
      this.sourceWindowOffsets = arrayOfInt2;
    }

    private int getFirstPeriodIndexInSource(int paramInt)
    {
      if (paramInt == 0)
        return 0;
      return this.sourcePeriodOffsets[(paramInt - 1)];
    }

    private int getFirstWindowIndexInSource(int paramInt)
    {
      if (paramInt == 0)
        return 0;
      return this.sourceWindowOffsets[(paramInt - 1)];
    }

    private int getSourceIndexForPeriod(int paramInt)
    {
      return Util.binarySearchFloor(this.sourcePeriodOffsets, paramInt, true, false) + 1;
    }

    private int getSourceIndexForWindow(int paramInt)
    {
      return Util.binarySearchFloor(this.sourceWindowOffsets, paramInt, true, false) + 1;
    }

    public int getIndexOfPeriod(Object paramObject)
    {
      if (!(paramObject instanceof Pair));
      do
      {
        do
        {
          return -1;
          paramObject = (Pair)paramObject;
        }
        while (!(paramObject.first instanceof Integer));
        i = ((Integer)paramObject.first).intValue();
        paramObject = paramObject.second;
      }
      while ((i < 0) || (i >= this.timelines.length));
      int j = this.timelines[i].getIndexOfPeriod(paramObject);
      if (j == -1);
      for (int i = -1; ; i = getFirstPeriodIndexInSource(i) + j)
        return i;
    }

    public Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
    {
      int i = getSourceIndexForPeriod(paramInt);
      int j = getFirstWindowIndexInSource(i);
      int k = getFirstPeriodIndexInSource(i);
      this.timelines[i].getPeriod(paramInt - k, paramPeriod, paramBoolean);
      paramPeriod.windowIndex = (j + paramPeriod.windowIndex);
      if (paramBoolean)
        paramPeriod.uid = Pair.create(Integer.valueOf(i), paramPeriod.uid);
      return paramPeriod;
    }

    public int getPeriodCount()
    {
      return this.sourcePeriodOffsets[(this.sourcePeriodOffsets.length - 1)];
    }

    public Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
    {
      int i = getSourceIndexForWindow(paramInt);
      int j = getFirstWindowIndexInSource(i);
      int k = getFirstPeriodIndexInSource(i);
      this.timelines[i].getWindow(paramInt - j, paramWindow, paramBoolean, paramLong);
      paramWindow.firstPeriodIndex += k;
      paramWindow.lastPeriodIndex += k;
      return paramWindow;
    }

    public int getWindowCount()
    {
      return this.sourceWindowOffsets[(this.sourceWindowOffsets.length - 1)];
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.ConcatenatingMediaSource
 * JD-Core Version:    0.6.0
 */