package org.vidogram.messenger.exoplayer2;

import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DefaultAllocator;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class DefaultLoadControl
  implements LoadControl
{
  private static final int ABOVE_HIGH_WATERMARK = 0;
  private static final int BELOW_LOW_WATERMARK = 2;
  private static final int BETWEEN_WATERMARKS = 1;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS = 5000;
  public static final int DEFAULT_BUFFER_FOR_PLAYBACK_MS = 2500;
  public static final int DEFAULT_MAX_BUFFER_MS = 30000;
  public static final int DEFAULT_MIN_BUFFER_MS = 15000;
  private final DefaultAllocator allocator;
  private final long bufferForPlaybackAfterRebufferUs;
  private final long bufferForPlaybackUs;
  private boolean isBuffering;
  private final long maxBufferUs;
  private final long minBufferUs;
  private int targetBufferSize;

  public DefaultLoadControl()
  {
    this(new DefaultAllocator(true, 65536));
  }

  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator)
  {
    this(paramDefaultAllocator, 15000, 30000, 2500L, 5000L);
  }

  public DefaultLoadControl(DefaultAllocator paramDefaultAllocator, int paramInt1, int paramInt2, long paramLong1, long paramLong2)
  {
    this.allocator = paramDefaultAllocator;
    this.minBufferUs = (paramInt1 * 1000L);
    this.maxBufferUs = (paramInt2 * 1000L);
    this.bufferForPlaybackUs = (paramLong1 * 1000L);
    this.bufferForPlaybackAfterRebufferUs = (paramLong2 * 1000L);
  }

  private int getBufferTimeState(long paramLong)
  {
    if (paramLong > this.maxBufferUs)
      return 0;
    if (paramLong < this.minBufferUs)
      return 2;
    return 1;
  }

  private void reset(boolean paramBoolean)
  {
    this.targetBufferSize = 0;
    this.isBuffering = false;
    if (paramBoolean)
      this.allocator.reset();
  }

  public Allocator getAllocator()
  {
    return this.allocator;
  }

  public void onPrepared()
  {
    reset(false);
  }

  public void onReleased()
  {
    reset(true);
  }

  public void onStopped()
  {
    reset(true);
  }

  public void onTracksSelected(Renderer[] paramArrayOfRenderer, TrackGroupArray paramTrackGroupArray, TrackSelectionArray paramTrackSelectionArray)
  {
    int i = 0;
    this.targetBufferSize = 0;
    while (i < paramArrayOfRenderer.length)
    {
      if (paramTrackSelectionArray.get(i) != null)
        this.targetBufferSize += Util.getDefaultBufferSize(paramArrayOfRenderer[i].getTrackType());
      i += 1;
    }
    this.allocator.setTargetBufferSize(this.targetBufferSize);
  }

  public boolean shouldContinueLoading(long paramLong)
  {
    boolean bool2 = false;
    int j = getBufferTimeState(paramLong);
    if (this.allocator.getTotalBytesAllocated() >= this.targetBufferSize);
    for (int i = 1; ; i = 0)
    {
      boolean bool1;
      if (j != 2)
      {
        bool1 = bool2;
        if (j == 1)
        {
          bool1 = bool2;
          if (this.isBuffering)
          {
            bool1 = bool2;
            if (i != 0);
          }
        }
      }
      else
      {
        bool1 = true;
      }
      this.isBuffering = bool1;
      return this.isBuffering;
    }
  }

  public boolean shouldStartPlayback(long paramLong, boolean paramBoolean)
  {
    long l;
    if (paramBoolean)
      l = this.bufferForPlaybackAfterRebufferUs;
    while ((l <= 0L) || (paramLong >= l))
    {
      return true;
      l = this.bufferForPlaybackUs;
    }
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.DefaultLoadControl
 * JD-Core Version:    0.6.0
 */