package org.vidogram.messenger.exoplayer2.source;

import android.os.Handler;
import java.io.IOException;
import org.vidogram.messenger.exoplayer2.C;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public abstract interface AdaptiveMediaSourceEventListener
{
  public abstract void onDownstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong);

  public abstract void onLoadCanceled(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);

  public abstract void onLoadCompleted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5);

  public abstract void onLoadError(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, IOException paramIOException, boolean paramBoolean);

  public abstract void onLoadStarted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3);

  public abstract void onUpstreamDiscarded(int paramInt, long paramLong1, long paramLong2);

  public static final class EventDispatcher
  {
    private final Handler handler;
    private final AdaptiveMediaSourceEventListener listener;
    private final long mediaTimeOffsetMs;

    public EventDispatcher(Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener)
    {
      this(paramHandler, paramAdaptiveMediaSourceEventListener, 0L);
    }

    public EventDispatcher(Handler paramHandler, AdaptiveMediaSourceEventListener paramAdaptiveMediaSourceEventListener, long paramLong)
    {
      if (paramAdaptiveMediaSourceEventListener != null);
      for (paramHandler = (Handler)Assertions.checkNotNull(paramHandler); ; paramHandler = null)
      {
        this.handler = paramHandler;
        this.listener = paramAdaptiveMediaSourceEventListener;
        this.mediaTimeOffsetMs = paramLong;
        return;
      }
    }

    private long adjustMediaTime(long paramLong)
    {
      paramLong = C.usToMs(paramLong);
      if (paramLong == -9223372036854775807L)
        return -9223372036854775807L;
      return this.mediaTimeOffsetMs + paramLong;
    }

    public EventDispatcher copyWithMediaTimeOffsetMs(long paramLong)
    {
      return new EventDispatcher(this.handler, this.listener, paramLong);
    }

    public void downstreamFormatChanged(int paramInt1, Format paramFormat, int paramInt2, Object paramObject, long paramLong)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramInt1, paramFormat, paramInt2, paramObject, paramLong)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onDownstreamFormatChanged(this.val$trackType, this.val$trackFormat, this.val$trackSelectionReason, this.val$trackSelectionData, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaTimeUs));
          }
        });
    }

    public void loadCanceled(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadCanceled(this.val$dataSpec, this.val$dataType, this.val$trackType, this.val$trackFormat, this.val$trackSelectionReason, this.val$trackSelectionData, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaStartTimeUs), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs, this.val$loadDurationMs, this.val$bytesLoaded);
          }
        });
    }

    public void loadCanceled(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3)
    {
      loadCanceled(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3);
    }

    public void loadCompleted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadCompleted(this.val$dataSpec, this.val$dataType, this.val$trackType, this.val$trackFormat, this.val$trackSelectionReason, this.val$trackSelectionData, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaStartTimeUs), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs, this.val$loadDurationMs, this.val$bytesLoaded);
          }
        });
    }

    public void loadCompleted(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3)
    {
      loadCompleted(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3);
    }

    public void loadError(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5, IOException paramIOException, boolean paramBoolean)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, paramLong1, paramLong2, paramLong3, paramLong4, paramLong5, paramIOException, paramBoolean)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadError(this.val$dataSpec, this.val$dataType, this.val$trackType, this.val$trackFormat, this.val$trackSelectionReason, this.val$trackSelectionData, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaStartTimeUs), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs, this.val$loadDurationMs, this.val$bytesLoaded, this.val$error, this.val$wasCanceled);
          }
        });
    }

    public void loadError(DataSpec paramDataSpec, int paramInt, long paramLong1, long paramLong2, long paramLong3, IOException paramIOException, boolean paramBoolean)
    {
      loadError(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong1, paramLong2, paramLong3, paramIOException, paramBoolean);
    }

    public void loadStarted(DataSpec paramDataSpec, int paramInt1, int paramInt2, Format paramFormat, int paramInt3, Object paramObject, long paramLong1, long paramLong2, long paramLong3)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDataSpec, paramInt1, paramInt2, paramFormat, paramInt3, paramObject, paramLong1, paramLong2, paramLong3)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onLoadStarted(this.val$dataSpec, this.val$dataType, this.val$trackType, this.val$trackFormat, this.val$trackSelectionReason, this.val$trackSelectionData, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaStartTimeUs), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs), this.val$elapsedRealtimeMs);
          }
        });
    }

    public void loadStarted(DataSpec paramDataSpec, int paramInt, long paramLong)
    {
      loadStarted(paramDataSpec, paramInt, -1, null, 0, null, -9223372036854775807L, -9223372036854775807L, paramLong);
    }

    public void upstreamDiscarded(int paramInt, long paramLong1, long paramLong2)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramInt, paramLong1, paramLong2)
        {
          public void run()
          {
            AdaptiveMediaSourceEventListener.this.onUpstreamDiscarded(this.val$trackType, AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaStartTimeUs), AdaptiveMediaSourceEventListener.EventDispatcher.this.adjustMediaTime(this.val$mediaEndTimeUs));
          }
        });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener
 * JD-Core Version:    0.6.0
 */