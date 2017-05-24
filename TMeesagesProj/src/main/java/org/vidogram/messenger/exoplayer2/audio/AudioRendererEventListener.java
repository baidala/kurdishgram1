package org.vidogram.messenger.exoplayer2.audio;

import android.os.Handler;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.decoder.DecoderCounters;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public abstract interface AudioRendererEventListener
{
  public abstract void onAudioDecoderInitialized(String paramString, long paramLong1, long paramLong2);

  public abstract void onAudioDisabled(DecoderCounters paramDecoderCounters);

  public abstract void onAudioEnabled(DecoderCounters paramDecoderCounters);

  public abstract void onAudioInputFormatChanged(Format paramFormat);

  public abstract void onAudioSessionId(int paramInt);

  public abstract void onAudioTrackUnderrun(int paramInt, long paramLong1, long paramLong2);

  public static final class EventDispatcher
  {
    private final Handler handler;
    private final AudioRendererEventListener listener;

    public EventDispatcher(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
    {
      if (paramAudioRendererEventListener != null);
      for (paramHandler = (Handler)Assertions.checkNotNull(paramHandler); ; paramHandler = null)
      {
        this.handler = paramHandler;
        this.listener = paramAudioRendererEventListener;
        return;
      }
    }

    public void audioSessionId(int paramInt)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramInt)
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioSessionId(this.val$audioSessionId);
          }
        });
    }

    public void audioTrackUnderrun(int paramInt, long paramLong1, long paramLong2)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramInt, paramLong1, paramLong2)
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioTrackUnderrun(this.val$bufferSize, this.val$bufferSizeMs, this.val$elapsedSinceLastFeedMs);
          }
        });
    }

    public void decoderInitialized(String paramString, long paramLong1, long paramLong2)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramString, paramLong1, paramLong2)
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioDecoderInitialized(this.val$decoderName, this.val$initializedTimestampMs, this.val$initializationDurationMs);
          }
        });
    }

    public void disabled(DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDecoderCounters)
        {
          public void run()
          {
            this.val$counters.ensureUpdated();
            AudioRendererEventListener.this.onAudioDisabled(this.val$counters);
          }
        });
    }

    public void enabled(DecoderCounters paramDecoderCounters)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramDecoderCounters)
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioEnabled(this.val$decoderCounters);
          }
        });
    }

    public void inputFormatChanged(Format paramFormat)
    {
      if (this.listener != null)
        this.handler.post(new Runnable(paramFormat)
        {
          public void run()
          {
            AudioRendererEventListener.this.onAudioInputFormatChanged(this.val$format);
          }
        });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.audio.AudioRendererEventListener
 * JD-Core Version:    0.6.0
 */