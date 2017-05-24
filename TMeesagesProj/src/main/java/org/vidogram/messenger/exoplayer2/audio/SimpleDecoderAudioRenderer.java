package org.vidogram.messenger.exoplayer2.audio;

import android.media.PlaybackParams;
import android.os.Handler;
import android.os.Looper;
import org.vidogram.messenger.exoplayer2.BaseRenderer;
import org.vidogram.messenger.exoplayer2.ExoPlaybackException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderCounters;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.decoder.SimpleDecoder;
import org.vidogram.messenger.exoplayer2.decoder.SimpleOutputBuffer;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.drm.DrmSession;
import org.vidogram.messenger.exoplayer2.drm.DrmSessionManager;
import org.vidogram.messenger.exoplayer2.drm.ExoMediaCrypto;
import org.vidogram.messenger.exoplayer2.util.MediaClock;
import org.vidogram.messenger.exoplayer2.util.Util;

public abstract class SimpleDecoderAudioRenderer extends BaseRenderer
  implements AudioTrack.Listener, MediaClock
{
  private boolean allowPositionDiscontinuity;
  private int audioSessionId;
  private final AudioTrack audioTrack;
  private long currentPositionUs;
  private SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> decoder;
  private DecoderCounters decoderCounters;
  private DrmSession<ExoMediaCrypto> drmSession;
  private final DrmSessionManager<ExoMediaCrypto> drmSessionManager;
  private final AudioRendererEventListener.EventDispatcher eventDispatcher;
  private final FormatHolder formatHolder;
  private DecoderInputBuffer inputBuffer;
  private Format inputFormat;
  private boolean inputStreamEnded;
  private SimpleOutputBuffer outputBuffer;
  private boolean outputStreamEnded;
  private DrmSession<ExoMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean waitingForKeys;

  public SimpleDecoderAudioRenderer()
  {
    this(null, null);
  }

  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener)
  {
    this(paramHandler, paramAudioRendererEventListener, null);
  }

  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities)
  {
    this(paramHandler, paramAudioRendererEventListener, paramAudioCapabilities, null, false);
  }

  public SimpleDecoderAudioRenderer(Handler paramHandler, AudioRendererEventListener paramAudioRendererEventListener, AudioCapabilities paramAudioCapabilities, DrmSessionManager<ExoMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    super(1);
    this.eventDispatcher = new AudioRendererEventListener.EventDispatcher(paramHandler, paramAudioRendererEventListener);
    this.audioTrack = new AudioTrack(paramAudioCapabilities, this);
    this.drmSessionManager = paramDrmSessionManager;
    this.formatHolder = new FormatHolder();
    this.playClearSamplesWithoutKeys = paramBoolean;
    this.audioSessionId = 0;
  }

  private boolean drainOutputBuffer()
  {
    if (this.outputStreamEnded);
    Object localObject;
    while (true)
    {
      return false;
      if (this.outputBuffer != null)
        break;
      this.outputBuffer = ((SimpleOutputBuffer)this.decoder.dequeueOutputBuffer());
      if (this.outputBuffer == null)
        continue;
      localObject = this.decoderCounters;
      ((DecoderCounters)localObject).skippedOutputBufferCount += this.outputBuffer.skippedOutputBufferCount;
    }
    if (this.outputBuffer.isEndOfStream())
    {
      this.outputStreamEnded = true;
      this.audioTrack.handleEndOfStream();
      this.outputBuffer.release();
      this.outputBuffer = null;
      return false;
    }
    if (!this.audioTrack.isInitialized())
    {
      localObject = getOutputFormat();
      this.audioTrack.configure(((Format)localObject).sampleMimeType, ((Format)localObject).channelCount, ((Format)localObject).sampleRate, ((Format)localObject).pcmEncoding, 0);
      if (this.audioSessionId != 0)
        break label254;
      this.audioSessionId = this.audioTrack.initialize(0);
      this.eventDispatcher.audioSessionId(this.audioSessionId);
      onAudioSessionId(this.audioSessionId);
    }
    while (true)
    {
      if (getState() == 2)
        this.audioTrack.play();
      int i = this.audioTrack.handleBuffer(this.outputBuffer.data, this.outputBuffer.timeUs);
      if ((i & 0x1) != 0)
        this.allowPositionDiscontinuity = true;
      if ((i & 0x2) == 0)
        break;
      localObject = this.decoderCounters;
      ((DecoderCounters)localObject).renderedOutputBufferCount += 1;
      this.outputBuffer.release();
      this.outputBuffer = null;
      return true;
      label254: this.audioTrack.initialize(this.audioSessionId);
    }
  }

  private boolean feedInputBuffer()
  {
    if (this.inputStreamEnded);
    label85: 
    do
    {
      while (true)
      {
        return false;
        if (this.inputBuffer == null)
        {
          this.inputBuffer = this.decoder.dequeueInputBuffer();
          if (this.inputBuffer == null)
            continue;
        }
        if (this.waitingForKeys);
        for (int i = -4; i != -3; i = readSource(this.formatHolder, this.inputBuffer))
        {
          if (i != -5)
            break label85;
          onInputFormatChanged(this.formatHolder.format);
          return true;
        }
      }
      if (this.inputBuffer.isEndOfStream())
      {
        this.inputStreamEnded = true;
        this.decoder.queueInputBuffer(this.inputBuffer);
        this.inputBuffer = null;
        return false;
      }
      this.waitingForKeys = shouldWaitForKeys(this.inputBuffer.isEncrypted());
    }
    while (this.waitingForKeys);
    this.inputBuffer.flip();
    this.decoder.queueInputBuffer(this.inputBuffer);
    DecoderCounters localDecoderCounters = this.decoderCounters;
    localDecoderCounters.inputBufferCount += 1;
    this.inputBuffer = null;
    return true;
  }

  private void flushDecoder()
  {
    this.inputBuffer = null;
    this.waitingForKeys = false;
    if (this.outputBuffer != null)
    {
      this.outputBuffer.release();
      this.outputBuffer = null;
    }
    this.decoder.flush();
  }

  private void onInputFormatChanged(Format paramFormat)
  {
    Object localObject = this.inputFormat;
    this.inputFormat = paramFormat;
    DrmInitData localDrmInitData = this.inputFormat.drmInitData;
    if (localObject == null)
    {
      localObject = null;
      if (Util.areEqual(localDrmInitData, localObject))
        break label82;
    }
    label82: for (int i = 1; ; i = 0)
    {
      if (i == 0)
        break label134;
      if (this.inputFormat.drmInitData == null)
        break label143;
      if (this.drmSessionManager != null)
        break label87;
      throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
      localObject = ((Format)localObject).drmInitData;
      break;
    }
    label87: this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.inputFormat.drmInitData);
    if (this.pendingDrmSession == this.drmSession)
      this.drmSessionManager.releaseSession(this.pendingDrmSession);
    while (true)
    {
      label134: this.eventDispatcher.inputFormatChanged(paramFormat);
      return;
      label143: this.pendingDrmSession = null;
    }
  }

  private boolean readFormat()
  {
    if (readSource(this.formatHolder, null) == -5)
    {
      onInputFormatChanged(this.formatHolder.format);
      return true;
    }
    return false;
  }

  private boolean shouldWaitForKeys(boolean paramBoolean)
  {
    if (this.drmSession == null);
    int i;
    do
    {
      return false;
      i = this.drmSession.getState();
      if (i != 0)
        continue;
      throw ExoPlaybackException.createForRenderer(this.drmSession.getError(), getIndex());
    }
    while ((i == 4) || ((!paramBoolean) && (this.playClearSamplesWithoutKeys)));
    return true;
  }

  protected abstract SimpleDecoder<DecoderInputBuffer, ? extends SimpleOutputBuffer, ? extends AudioDecoderException> createDecoder(Format paramFormat, ExoMediaCrypto paramExoMediaCrypto);

  public MediaClock getMediaClock()
  {
    return this;
  }

  protected Format getOutputFormat()
  {
    return Format.createAudioSampleFormat(null, "audio/raw", null, -1, -1, this.inputFormat.channelCount, this.inputFormat.sampleRate, 2, null, null, 0, null);
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
    return (this.outputStreamEnded) && (!this.audioTrack.hasPendingData());
  }

  public boolean isReady()
  {
    return (this.audioTrack.hasPendingData()) || ((this.inputFormat != null) && (!this.waitingForKeys) && ((isSourceReady()) || (this.outputBuffer != null)));
  }

  protected void onAudioSessionId(int paramInt)
  {
  }

  protected void onDisabled()
  {
    this.inputBuffer = null;
    this.outputBuffer = null;
    this.inputFormat = null;
    this.audioSessionId = 0;
    this.waitingForKeys = false;
    try
    {
      if (this.decoder != null)
      {
        this.decoder.release();
        this.decoder = null;
        DecoderCounters localDecoderCounters = this.decoderCounters;
        localDecoderCounters.decoderReleaseCount += 1;
      }
      this.audioTrack.release();
      try
      {
        if (this.drmSession != null)
          this.drmSessionManager.releaseSession(this.drmSession);
        try
        {
          if ((this.pendingDrmSession != null) && (this.pendingDrmSession != this.drmSession))
            this.drmSessionManager.releaseSession(this.pendingDrmSession);
          return;
        }
        finally
        {
          this.drmSession = null;
          this.pendingDrmSession = null;
          this.decoderCounters.ensureUpdated();
          this.eventDispatcher.disabled(this.decoderCounters);
        }
      }
      finally
      {
      }
    }
    finally
    {
      try
      {
        if (this.drmSession != null)
          this.drmSessionManager.releaseSession(this.drmSession);
        try
        {
          if ((this.pendingDrmSession != null) && (this.pendingDrmSession != this.drmSession))
            this.drmSessionManager.releaseSession(this.pendingDrmSession);
          this.drmSession = null;
          this.pendingDrmSession = null;
          this.decoderCounters.ensureUpdated();
          this.eventDispatcher.disabled(this.decoderCounters);
          throw localObject4;
        }
        finally
        {
          this.drmSession = null;
          this.pendingDrmSession = null;
          this.decoderCounters.ensureUpdated();
          this.eventDispatcher.disabled(this.decoderCounters);
        }
      }
      finally
      {
      }
    }
    throw localObject7;
  }

  protected void onEnabled(boolean paramBoolean)
  {
    this.decoderCounters = new DecoderCounters();
    this.eventDispatcher.enabled(this.decoderCounters);
  }

  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    this.audioTrack.reset();
    this.currentPositionUs = paramLong;
    this.allowPositionDiscontinuity = true;
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.decoder != null)
      flushDecoder();
  }

  protected void onStarted()
  {
    this.audioTrack.play();
  }

  protected void onStopped()
  {
    this.audioTrack.pause();
  }

  public void onUnderrun(int paramInt, long paramLong1, long paramLong2)
  {
    this.eventDispatcher.audioTrackUnderrun(paramInt, paramLong1, paramLong2);
  }

  // ERROR //
  public void render(long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 87	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:outputStreamEnded	Z
    //   4: ifeq +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: getfield 222	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:inputFormat	Lorg/vidogram/messenger/exoplayer2/Format;
    //   12: ifnonnull +10 -> 22
    //   15: aload_0
    //   16: invokespecial 390	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:readFormat	()Z
    //   19: ifeq -12 -> 7
    //   22: aload_0
    //   23: aload_0
    //   24: getfield 262	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:pendingDrmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   27: putfield 264	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   30: aconst_null
    //   31: astore 6
    //   33: aload_0
    //   34: getfield 264	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   37: ifnull +59 -> 96
    //   40: aload_0
    //   41: getfield 264	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   44: invokeinterface 275 1 0
    //   49: istore 5
    //   51: iload 5
    //   53: ifne +20 -> 73
    //   56: aload_0
    //   57: getfield 264	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   60: invokeinterface 279 1 0
    //   65: aload_0
    //   66: invokevirtual 242	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:getIndex	()I
    //   69: invokestatic 248	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   72: athrow
    //   73: iload 5
    //   75: iconst_3
    //   76: if_icmpeq +9 -> 85
    //   79: iload 5
    //   81: iconst_4
    //   82: if_icmpne -75 -> 7
    //   85: aload_0
    //   86: getfield 264	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   89: invokeinterface 394 1 0
    //   94: astore 6
    //   96: aload_0
    //   97: getfield 91	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoder	Lorg/vidogram/messenger/exoplayer2/decoder/SimpleDecoder;
    //   100: ifnonnull +70 -> 170
    //   103: invokestatic 399	android/os/SystemClock:elapsedRealtime	()J
    //   106: lstore_1
    //   107: ldc_w 401
    //   110: invokestatic 406	org/vidogram/messenger/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
    //   113: aload_0
    //   114: aload_0
    //   115: aload_0
    //   116: getfield 222	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:inputFormat	Lorg/vidogram/messenger/exoplayer2/Format;
    //   119: aload 6
    //   121: invokevirtual 408	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:createDecoder	(Lorg/vidogram/messenger/exoplayer2/Format;Lorg/vidogram/messenger/exoplayer2/drm/ExoMediaCrypto;)Lorg/vidogram/messenger/exoplayer2/decoder/SimpleDecoder;
    //   124: putfield 91	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoder	Lorg/vidogram/messenger/exoplayer2/decoder/SimpleDecoder;
    //   127: invokestatic 411	org/vidogram/messenger/exoplayer2/util/TraceUtil:endSection	()V
    //   130: invokestatic 399	android/os/SystemClock:elapsedRealtime	()J
    //   133: lstore_3
    //   134: aload_0
    //   135: getfield 62	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:eventDispatcher	Lorg/vidogram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher;
    //   138: aload_0
    //   139: getfield 91	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoder	Lorg/vidogram/messenger/exoplayer2/decoder/SimpleDecoder;
    //   142: invokevirtual 415	org/vidogram/messenger/exoplayer2/decoder/SimpleDecoder:getName	()Ljava/lang/String;
    //   145: lload_3
    //   146: lload_3
    //   147: lload_1
    //   148: lsub
    //   149: invokevirtual 419	org/vidogram/messenger/exoplayer2/audio/AudioRendererEventListener$EventDispatcher:decoderInitialized	(Ljava/lang/String;JJ)V
    //   152: aload_0
    //   153: getfield 101	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/vidogram/messenger/exoplayer2/decoder/DecoderCounters;
    //   156: astore 6
    //   158: aload 6
    //   160: aload 6
    //   162: getfield 422	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:decoderInitCount	I
    //   165: iconst_1
    //   166: iadd
    //   167: putfield 422	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:decoderInitCount	I
    //   170: ldc_w 424
    //   173: invokestatic 406	org/vidogram/messenger/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
    //   176: aload_0
    //   177: invokespecial 426	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:drainOutputBuffer	()Z
    //   180: ifne -4 -> 176
    //   183: aload_0
    //   184: invokespecial 428	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:feedInputBuffer	()Z
    //   187: ifne -4 -> 183
    //   190: invokestatic 411	org/vidogram/messenger/exoplayer2/util/TraceUtil:endSection	()V
    //   193: aload_0
    //   194: getfield 101	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:decoderCounters	Lorg/vidogram/messenger/exoplayer2/decoder/DecoderCounters;
    //   197: invokevirtual 353	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:ensureUpdated	()V
    //   200: return
    //   201: astore 6
    //   203: aload 6
    //   205: aload_0
    //   206: invokevirtual 242	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:getIndex	()I
    //   209: invokestatic 248	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   212: athrow
    //   213: astore 6
    //   215: aload 6
    //   217: aload_0
    //   218: invokevirtual 242	org/vidogram/messenger/exoplayer2/audio/SimpleDecoderAudioRenderer:getIndex	()I
    //   221: invokestatic 248	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   224: athrow
    //   225: astore 6
    //   227: goto -12 -> 215
    //   230: astore 6
    //   232: goto -17 -> 215
    //
    // Exception table:
    //   from	to	target	type
    //   103	170	201	org/vidogram/messenger/exoplayer2/audio/AudioDecoderException
    //   170	176	213	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   176	183	213	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   183	193	213	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   170	176	225	org/vidogram/messenger/exoplayer2/audio/AudioTrack$WriteException
    //   176	183	225	org/vidogram/messenger/exoplayer2/audio/AudioTrack$WriteException
    //   183	193	225	org/vidogram/messenger/exoplayer2/audio/AudioTrack$WriteException
    //   170	176	230	org/vidogram/messenger/exoplayer2/audio/AudioDecoderException
    //   176	183	230	org/vidogram/messenger/exoplayer2/audio/AudioDecoderException
    //   183	193	230	org/vidogram/messenger/exoplayer2/audio/AudioDecoderException
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.audio.SimpleDecoderAudioRenderer
 * JD-Core Version:    0.6.0
 */