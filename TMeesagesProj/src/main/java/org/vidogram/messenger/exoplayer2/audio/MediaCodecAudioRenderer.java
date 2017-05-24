package org.vidogram.messenger.exoplayer2.audio;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.media.PlaybackParams;
import android.os.Handler;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.decoder.DecoderCounters;
import org.vidogram.messenger.exoplayer2.drm.DrmSessionManager;
import org.vidogram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecInfo;
import org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecRenderer;
import org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.vidogram.messenger.exoplayer2.util.MediaClock;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;
import org.vidogram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public class MediaCodecAudioRenderer extends MediaCodecRenderer
  implements AudioTrack.Listener, MediaClock
{
  private boolean allowPositionDiscontinuity;
  private int audioSessionId = 0;
  private final AudioTrack audioTrack = new AudioTrack(paramAudioCapabilities, this);
  private long currentPositionUs;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private boolean passthroughEnabled;
  private MediaFormat passthroughMediaFormat;
  private int pcmEncoding;

  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector)
  {
    this(paramMediaCodecSelector, null, true);
  }

  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramMediaCodecSelector, null, true, paramHandler, paramAudioRendererEventListener);
  }

  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    this(paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, null, null);
  }

  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramMediaCodecSelector, paramDrmSessionManager, paramBoolean, paramHandler, paramAudioRendererEventListener, null);
  }

  public MediaCodecAudioRenderer(MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean, Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    super(1, paramMediaCodecSelector, paramDrmSessionManager, paramBoolean);
    this.eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
  }

  protected boolean allowPassthrough(String paramString)
  {
    return this.audioTrack.isPassthroughSupported(paramString);
  }

  protected void configureCodec(MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto)
  {
    if (this.passthroughEnabled)
    {
      this.passthroughMediaFormat = paramFormat.getFrameworkMediaFormatV16();
      this.passthroughMediaFormat.setString("mime", "audio/raw");
      paramMediaCodec.configure(this.passthroughMediaFormat, null, paramMediaCrypto, 0);
      this.passthroughMediaFormat.setString("mime", paramFormat.sampleMimeType);
      return;
    }
    paramMediaCodec.configure(paramFormat.getFrameworkMediaFormatV16(), null, paramMediaCrypto, 0);
    this.passthroughMediaFormat = null;
  }

  protected MediaCodecInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
  {
    if (allowPassthrough(paramFormat.sampleMimeType))
    {
      MediaCodecInfo localMediaCodecInfo = paramMediaCodecSelector.getPassthroughDecoderInfo();
      if (localMediaCodecInfo != null)
      {
        this.passthroughEnabled = true;
        return localMediaCodecInfo;
      }
    }
    this.passthroughEnabled = false;
    return super.getDecoderInfo(paramMediaCodecSelector, paramFormat, paramBoolean);
  }

  public MediaClock getMediaClock()
  {
    return this;
  }

  public long getPositionUs()
  {
    long l = this.audioTrack.getCurrentPositionUs(isEnded());
    if (l != -9223372036854775808L)
      if (!this.allowPositionDiscontinuity)
        break label42;
    while (true)
    {
      this.currentPositionUs = l;
      this.allowPositionDiscontinuity = false;
      return this.currentPositionUs;
      label42: l = Math.max(this.currentPositionUs, l);
    }
  }

  protected void handleAudioTrackDiscontinuity()
  {
  }

  public void handleMessage(int paramInt, Object paramObject)
  {
    switch (paramInt)
    {
    default:
      super.handleMessage(paramInt, paramObject);
    case 2:
    case 3:
    case 4:
    }
    do
    {
      return;
      this.audioTrack.setVolume(((Float)paramObject).floatValue());
      return;
      this.audioTrack.setPlaybackParams((PlaybackParams)paramObject);
      return;
      paramInt = ((Integer)paramObject).intValue();
    }
    while (!this.audioTrack.setStreamType(paramInt));
    this.audioSessionId = 0;
  }

  public boolean isEnded()
  {
    return (super.isEnded()) && (!this.audioTrack.hasPendingData());
  }

  public boolean isReady()
  {
    return (this.audioTrack.hasPendingData()) || (super.isReady());
  }

  protected void onAudioSessionId(int paramInt)
  {
  }

  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.decoderInitialized(paramString, paramLong1, paramLong2);
  }

  protected void onDisabled()
  {
    this.audioSessionId = 0;
    try
    {
      this.audioTrack.release();
      try
      {
        super.onDisabled();
        return;
      }
      finally
      {
        this.decoderCounters.ensureUpdated();
        this.eventDispatcher.disabled(this.decoderCounters);
      }
    }
    finally
    {
    }
    throw localObject3;
  }

  protected void onEnabled(boolean paramBoolean)
  {
    super.onEnabled(paramBoolean);
    this.eventDispatcher.enabled(this.decoderCounters);
  }

  protected void onInputFormatChanged(Format paramFormat)
  {
    super.onInputFormatChanged(paramFormat);
    this.eventDispatcher.inputFormatChanged(paramFormat);
    if ("audio/raw".equals(paramFormat.sampleMimeType));
    for (int i = paramFormat.pcmEncoding; ; i = 2)
    {
      this.pcmEncoding = i;
      return;
    }
  }

  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
  {
    int i;
    if (this.passthroughMediaFormat != null)
    {
      i = 1;
      if (i == 0)
        break label69;
    }
    label69: for (paramMediaCodec = this.passthroughMediaFormat.getString("mime"); ; paramMediaCodec = "audio/raw")
    {
      if (i != 0)
        paramMediaFormat = this.passthroughMediaFormat;
      i = paramMediaFormat.getInteger("channel-count");
      int j = paramMediaFormat.getInteger("sample-rate");
      this.audioTrack.configure(paramMediaCodec, i, j, this.pcmEncoding, 0);
      return;
      i = 0;
      break;
    }
  }

  protected void onOutputStreamEnded()
  {
    this.audioTrack.handleEndOfStream();
  }

  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    super.onPositionReset(paramLong, paramBoolean);
    this.audioTrack.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
  }

  protected void onStarted()
  {
    super.onStarted();
    this.audioTrack.play();
  }

  protected void onStopped()
  {
    this.audioTrack.pause();
    super.onStopped();
  }

  public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
  }

  // ERROR //
  protected boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, java.nio.ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 70	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:passthroughEnabled	Z
    //   4: ifeq +20 -> 24
    //   7: iload 8
    //   9: iconst_2
    //   10: iand
    //   11: ifeq +13 -> 24
    //   14: aload 5
    //   16: iload 7
    //   18: iconst_0
    //   19: invokevirtual 277	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   22: iconst_1
    //   23: ireturn
    //   24: iload 11
    //   26: ifeq +38 -> 64
    //   29: aload 5
    //   31: iload 7
    //   33: iconst_0
    //   34: invokevirtual 277	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   37: aload_0
    //   38: getfield 190	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lorg/vidogram/messenger/exoplayer2/decoder/DecoderCounters;
    //   41: astore 5
    //   43: aload 5
    //   45: aload 5
    //   47: getfield 280	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:skippedOutputBufferCount	I
    //   50: iconst_1
    //   51: iadd
    //   52: putfield 280	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:skippedOutputBufferCount	I
    //   55: aload_0
    //   56: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   59: invokevirtual 283	org/vidogram/messenger/exoplayer2/audio/AudioTrack:handleDiscontinuity	()V
    //   62: iconst_1
    //   63: ireturn
    //   64: aload_0
    //   65: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   68: invokevirtual 286	org/vidogram/messenger/exoplayer2/audio/AudioTrack:isInitialized	()Z
    //   71: ifne +56 -> 127
    //   74: aload_0
    //   75: getfield 46	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioSessionId	I
    //   78: ifne +113 -> 191
    //   81: aload_0
    //   82: aload_0
    //   83: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   86: iconst_0
    //   87: invokevirtual 290	org/vidogram/messenger/exoplayer2/audio/AudioTrack:initialize	(I)I
    //   90: putfield 46	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioSessionId	I
    //   93: aload_0
    //   94: getfield 60	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:eventDispatcher	Lorg/vidogram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   97: aload_0
    //   98: getfield 46	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioSessionId	I
    //   101: invokevirtual 292	org/vidogram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:audioSessionId	(I)V
    //   104: aload_0
    //   105: aload_0
    //   106: getfield 46	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioSessionId	I
    //   109: invokevirtual 294	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:onAudioSessionId	(I)V
    //   112: aload_0
    //   113: invokevirtual 297	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:getState	()I
    //   116: iconst_2
    //   117: if_icmpne +10 -> 127
    //   120: aload_0
    //   121: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   124: invokevirtual 256	org/vidogram/messenger/exoplayer2/audio/AudioTrack:play	()V
    //   127: aload_0
    //   128: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   131: aload 6
    //   133: lload 9
    //   135: invokevirtual 301	org/vidogram/messenger/exoplayer2/audio/AudioTrack:handleBuffer	(Ljava/nio/ByteBuffer;J)I
    //   138: istore 8
    //   140: iload 8
    //   142: iconst_1
    //   143: iand
    //   144: ifeq +12 -> 156
    //   147: aload_0
    //   148: invokevirtual 303	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:handleAudioTrackDiscontinuity	()V
    //   151: aload_0
    //   152: iconst_1
    //   153: putfield 126	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:allowPositionDiscontinuity	Z
    //   156: iload 8
    //   158: iconst_2
    //   159: iand
    //   160: ifeq +70 -> 230
    //   163: aload 5
    //   165: iload 7
    //   167: iconst_0
    //   168: invokevirtual 277	android/media/MediaCodec:releaseOutputBuffer	(IZ)V
    //   171: aload_0
    //   172: getfield 190	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:decoderCounters	Lorg/vidogram/messenger/exoplayer2/decoder/DecoderCounters;
    //   175: astore 5
    //   177: aload 5
    //   179: aload 5
    //   181: getfield 306	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:renderedOutputBufferCount	I
    //   184: iconst_1
    //   185: iadd
    //   186: putfield 306	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:renderedOutputBufferCount	I
    //   189: iconst_1
    //   190: ireturn
    //   191: aload_0
    //   192: getfield 53	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioTrack	Lorg/vidogram/messenger/exoplayer2/audio/AudioTrack;
    //   195: aload_0
    //   196: getfield 46	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:audioSessionId	I
    //   199: invokevirtual 290	org/vidogram/messenger/exoplayer2/audio/AudioTrack:initialize	(I)I
    //   202: pop
    //   203: goto -91 -> 112
    //   206: astore 5
    //   208: aload 5
    //   210: aload_0
    //   211: invokevirtual 309	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:getIndex	()I
    //   214: invokestatic 315	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   217: athrow
    //   218: astore 5
    //   220: aload 5
    //   222: aload_0
    //   223: invokevirtual 309	org/vidogram/messenger/exoplayer2/audio/MediaCodecAudioRenderer:getIndex	()I
    //   226: invokestatic 315	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   229: athrow
    //   230: iconst_0
    //   231: ireturn
    //
    // Exception table:
    //   from	to	target	type
    //   74	112	206	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   191	203	206	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   127	140	218	org/vidogram/messenger/exoplayer2/audio/AudioTrack$WriteException
  }

  protected int supportsFormat(MediaCodecSelector paramMediaCodecSelector, Format paramFormat)
  {
    int j = 0;
    String str = paramFormat.sampleMimeType;
    if (!MimeTypes.isAudio(str))
      return 0;
    if ((allowPassthrough(str)) && (paramMediaCodecSelector.getPassthroughDecoderInfo() != null))
      return 7;
    paramMediaCodecSelector = paramMediaCodecSelector.getDecoderInfo(str, false);
    if (paramMediaCodecSelector == null)
      return 1;
    if (Util.SDK_INT >= 21)
    {
      if (paramFormat.sampleRate != -1)
      {
        i = j;
        if (!paramMediaCodecSelector.isAudioSampleRateSupportedV21(paramFormat.sampleRate))
          break label110;
      }
      if (paramFormat.channelCount != -1)
      {
        i = j;
        if (!paramMediaCodecSelector.isAudioChannelCountSupportedV21(paramFormat.channelCount))
          break label110;
      }
    }
    int i = 1;
    label110: if (i != 0);
    for (i = 3; ; i = 2)
      return i | 0x4;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.audio.MediaCodecAudioRenderer
 * JD-Core Version:    0.6.0
 */