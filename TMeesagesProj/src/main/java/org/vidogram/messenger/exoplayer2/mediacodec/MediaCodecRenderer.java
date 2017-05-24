package org.vidogram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodec.CodecException;
import android.media.MediaCodec.CryptoException;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.os.Looper;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.vidogram.messenger.exoplayer2.BaseRenderer;
import org.vidogram.messenger.exoplayer2.ExoPlaybackException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.CryptoInfo;
import org.vidogram.messenger.exoplayer2.decoder.DecoderCounters;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.drm.DrmSession;
import org.vidogram.messenger.exoplayer2.drm.DrmSessionManager;
import org.vidogram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.NalUnitUtil;
import org.vidogram.messenger.exoplayer2.util.TraceUtil;
import org.vidogram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public abstract class MediaCodecRenderer extends BaseRenderer
{
  private static final byte[] ADAPTATION_WORKAROUND_BUFFER = Util.getBytesFromHexString("0000016742C00BDA259000000168CE0F13200000016588840DCE7118A0002FBF1C31C3275D78");
  private static final int ADAPTATION_WORKAROUND_SLICE_WIDTH_HEIGHT = 32;
  private static final long MAX_CODEC_HOTSWAP_TIME_MS = 1000L;
  private static final int RECONFIGURATION_STATE_NONE = 0;
  private static final int RECONFIGURATION_STATE_QUEUE_PENDING = 2;
  private static final int RECONFIGURATION_STATE_WRITE_PENDING = 1;
  private static final int REINITIALIZATION_STATE_NONE = 0;
  private static final int REINITIALIZATION_STATE_SIGNAL_END_OF_STREAM = 1;
  private static final int REINITIALIZATION_STATE_WAIT_END_OF_STREAM = 2;
  private static final String TAG = "MediaCodecRenderer";
  private final DecoderInputBuffer buffer;
  private MediaCodec codec;
  private long codecHotswapDeadlineMs;
  private boolean codecIsAdaptive;
  private boolean codecNeedsAdaptationWorkaround;
  private boolean codecNeedsAdaptationWorkaroundBuffer;
  private boolean codecNeedsDiscardToSpsWorkaround;
  private boolean codecNeedsEosFlushWorkaround;
  private boolean codecNeedsEosPropagationWorkaround;
  private boolean codecNeedsFlushWorkaround;
  private boolean codecNeedsMonoChannelCountWorkaround;
  private boolean codecReceivedBuffers;
  private boolean codecReceivedEos;
  private int codecReconfigurationState;
  private boolean codecReconfigured;
  private int codecReinitializationState;
  private final List<Long> decodeOnlyPresentationTimestamps;
  protected DecoderCounters decoderCounters;
  private DrmSession<FrameworkMediaCrypto> drmSession;
  private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
  private Format format;
  private final FormatHolder formatHolder;
  private ByteBuffer[] inputBuffers;
  private int inputIndex;
  private boolean inputStreamEnded;
  private final MediaCodecSelector mediaCodecSelector;
  private final MediaCodec.BufferInfo outputBufferInfo;
  private ByteBuffer[] outputBuffers;
  private int outputIndex;
  private boolean outputStreamEnded;
  private DrmSession<FrameworkMediaCrypto> pendingDrmSession;
  private final boolean playClearSamplesWithoutKeys;
  private boolean shouldSkipAdaptationWorkaroundOutputBuffer;
  private boolean shouldSkipOutputBuffer;
  private boolean waitingForKeys;

  public MediaCodecRenderer(int paramInt, MediaCodecSelector paramMediaCodecSelector, DrmSessionManager<FrameworkMediaCrypto> paramDrmSessionManager, boolean paramBoolean)
  {
    super(paramInt);
    if (Util.SDK_INT >= 16);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.mediaCodecSelector = ((MediaCodecSelector)Assertions.checkNotNull(paramMediaCodecSelector));
      this.drmSessionManager = paramDrmSessionManager;
      this.playClearSamplesWithoutKeys = paramBoolean;
      this.buffer = new DecoderInputBuffer(0);
      this.formatHolder = new FormatHolder();
      this.decodeOnlyPresentationTimestamps = new ArrayList();
      this.outputBufferInfo = new MediaCodec.BufferInfo();
      this.codecReconfigurationState = 0;
      this.codecReinitializationState = 0;
      return;
    }
  }

  private static boolean codecNeedsAdaptationWorkaround(String paramString)
  {
    return (Util.SDK_INT < 24) && (("OMX.Nvidia.h264.decode".equals(paramString)) || ("OMX.Nvidia.h264.decode.secure".equals(paramString))) && (("flounder".equals(Util.DEVICE)) || ("flounder_lte".equals(Util.DEVICE)) || ("grouper".equals(Util.DEVICE)) || ("tilapia".equals(Util.DEVICE)));
  }

  private static boolean codecNeedsDiscardToSpsWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT < 21) && (paramFormat.initializationData.isEmpty()) && ("OMX.MTK.VIDEO.DECODER.AVC".equals(paramString));
  }

  private static boolean codecNeedsEosFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 23) && ("OMX.google.vorbis.decoder".equals(paramString));
  }

  private static boolean codecNeedsEosPropagationWorkaround(String paramString)
  {
    return (Util.SDK_INT <= 17) && (("OMX.rk.video_decoder.avc".equals(paramString)) || ("OMX.allwinner.video.decoder.avc".equals(paramString)));
  }

  private static boolean codecNeedsFlushWorkaround(String paramString)
  {
    return (Util.SDK_INT < 18) || ((Util.SDK_INT == 18) && (("OMX.SEC.avc.dec".equals(paramString)) || ("OMX.SEC.avc.dec.secure".equals(paramString)))) || ((Util.SDK_INT == 19) && (Util.MODEL.startsWith("SM-G800")) && (("OMX.Exynos.avc.dec".equals(paramString)) || ("OMX.Exynos.avc.dec.secure".equals(paramString))));
  }

  private static boolean codecNeedsMonoChannelCountWorkaround(String paramString, Format paramFormat)
  {
    return (Util.SDK_INT <= 18) && (paramFormat.channelCount == 1) && ("OMX.MTK.AUDIO.DECODER.MP3".equals(paramString));
  }

  private boolean drainOutputBuffer(long paramLong1, long paramLong2)
  {
    if (this.outputStreamEnded)
      return false;
    if (this.outputIndex < 0)
    {
      this.outputIndex = this.codec.dequeueOutputBuffer(this.outputBufferInfo, getDequeueOutputBufferTimeoutUs());
      if (this.outputIndex >= 0)
      {
        if (this.shouldSkipAdaptationWorkaroundOutputBuffer)
        {
          this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
          this.codec.releaseOutputBuffer(this.outputIndex, false);
          this.outputIndex = -1;
          return true;
        }
        if ((this.outputBufferInfo.flags & 0x4) != 0)
        {
          processEndOfStream();
          this.outputIndex = -1;
          return true;
        }
        ByteBuffer localByteBuffer = this.outputBuffers[this.outputIndex];
        if (localByteBuffer != null)
        {
          localByteBuffer.position(this.outputBufferInfo.offset);
          localByteBuffer.limit(this.outputBufferInfo.offset + this.outputBufferInfo.size);
        }
        this.shouldSkipOutputBuffer = shouldSkipOutputBuffer(this.outputBufferInfo.presentationTimeUs);
      }
    }
    else
    {
      if (!processOutputBuffer(paramLong1, paramLong2, this.codec, this.outputBuffers[this.outputIndex], this.outputIndex, this.outputBufferInfo.flags, this.outputBufferInfo.presentationTimeUs, this.shouldSkipOutputBuffer))
        break label283;
      onProcessedOutputBuffer(this.outputBufferInfo.presentationTimeUs);
      this.outputIndex = -1;
      return true;
    }
    if (this.outputIndex == -2)
    {
      processOutputFormat();
      return true;
    }
    if (this.outputIndex == -3)
    {
      processOutputBuffersChanged();
      return true;
    }
    if ((this.codecNeedsEosPropagationWorkaround) && ((this.inputStreamEnded) || (this.codecReinitializationState == 2)))
    {
      processEndOfStream();
      return true;
    }
    return false;
    label283: return false;
  }

  private boolean feedInputBuffer()
  {
    if ((this.inputStreamEnded) || (this.codecReinitializationState == 2));
    int i;
    boolean bool;
    do
    {
      while (true)
      {
        return false;
        if (this.inputIndex < 0)
        {
          this.inputIndex = this.codec.dequeueInputBuffer(0L);
          if (this.inputIndex < 0)
            continue;
          this.buffer.data = this.inputBuffers[this.inputIndex];
          this.buffer.clear();
        }
        if (this.codecReinitializationState == 1)
        {
          if (this.codecNeedsEosPropagationWorkaround);
          while (true)
          {
            this.codecReinitializationState = 2;
            return false;
            this.codecReceivedEos = true;
            this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
            this.inputIndex = -1;
          }
        }
        if (this.codecNeedsAdaptationWorkaroundBuffer)
        {
          this.codecNeedsAdaptationWorkaroundBuffer = false;
          this.buffer.data.put(ADAPTATION_WORKAROUND_BUFFER);
          this.codec.queueInputBuffer(this.inputIndex, 0, ADAPTATION_WORKAROUND_BUFFER.length, 0L, 0);
          this.inputIndex = -1;
          this.codecReceivedBuffers = true;
          return true;
        }
        int j;
        if (this.waitingForKeys)
        {
          j = -4;
          i = 0;
        }
        while (true)
          if (j != -3)
          {
            if (j == -5)
            {
              if (this.codecReconfigurationState == 2)
              {
                this.buffer.clear();
                this.codecReconfigurationState = 1;
              }
              onInputFormatChanged(this.formatHolder.format);
              return true;
              if (this.codecReconfigurationState == 1)
              {
                i = 0;
                while (i < this.format.initializationData.size())
                {
                  byte[] arrayOfByte = (byte[])this.format.initializationData.get(i);
                  this.buffer.data.put(arrayOfByte);
                  i += 1;
                }
                this.codecReconfigurationState = 2;
              }
              i = this.buffer.data.position();
              j = readSource(this.formatHolder, this.buffer);
              continue;
            }
          }
          else
            break;
        if (!this.buffer.isEndOfStream())
          break;
        if (this.codecReconfigurationState == 2)
        {
          this.buffer.clear();
          this.codecReconfigurationState = 1;
        }
        this.inputStreamEnded = true;
        if (!this.codecReceivedBuffers)
        {
          processEndOfStream();
          return false;
        }
        try
        {
          if (this.codecNeedsEosPropagationWorkaround)
            continue;
          this.codecReceivedEos = true;
          this.codec.queueInputBuffer(this.inputIndex, 0, 0, 0L, 4);
          this.inputIndex = -1;
          return false;
        }
        catch (MediaCodec.CryptoException localCryptoException1)
        {
          throw ExoPlaybackException.createForRenderer(localCryptoException1, getIndex());
        }
      }
      bool = this.buffer.isEncrypted();
      this.waitingForKeys = shouldWaitForKeys(bool);
    }
    while (this.waitingForKeys);
    if ((this.codecNeedsDiscardToSpsWorkaround) && (!bool))
    {
      NalUnitUtil.discardToSps(this.buffer.data);
      if (this.buffer.data.position() == 0)
        return true;
      this.codecNeedsDiscardToSpsWorkaround = false;
    }
    try
    {
      long l = this.buffer.timeUs;
      if (this.buffer.isDecodeOnly())
        this.decodeOnlyPresentationTimestamps.add(Long.valueOf(l));
      this.buffer.flip();
      onQueueInputBuffer(this.buffer);
      Object localObject;
      if (bool)
      {
        localObject = getFrameworkCryptoInfo(this.buffer, i);
        this.codec.queueSecureInputBuffer(this.inputIndex, 0, (MediaCodec.CryptoInfo)localObject, l, 0);
      }
      while (true)
      {
        this.inputIndex = -1;
        this.codecReceivedBuffers = true;
        this.codecReconfigurationState = 0;
        localObject = this.decoderCounters;
        ((DecoderCounters)localObject).inputBufferCount += 1;
        return true;
        this.codec.queueInputBuffer(this.inputIndex, 0, this.buffer.data.limit(), l, 0);
      }
    }
    catch (MediaCodec.CryptoException localCryptoException2)
    {
    }
    throw ExoPlaybackException.createForRenderer(localCryptoException2, getIndex());
  }

  private static MediaCodec.CryptoInfo getFrameworkCryptoInfo(DecoderInputBuffer paramDecoderInputBuffer, int paramInt)
  {
    paramDecoderInputBuffer = paramDecoderInputBuffer.cryptoInfo.getFrameworkCryptoInfoV16();
    if (paramInt == 0)
      return paramDecoderInputBuffer;
    if (paramDecoderInputBuffer.numBytesOfClearData == null)
      paramDecoderInputBuffer.numBytesOfClearData = new int[1];
    int[] arrayOfInt = paramDecoderInputBuffer.numBytesOfClearData;
    arrayOfInt[0] += paramInt;
    return paramDecoderInputBuffer;
  }

  private void processEndOfStream()
  {
    if (this.codecReinitializationState == 2)
    {
      releaseCodec();
      maybeInitCodec();
      return;
    }
    this.outputStreamEnded = true;
    onOutputStreamEnded();
  }

  private void processOutputBuffersChanged()
  {
    this.outputBuffers = this.codec.getOutputBuffers();
  }

  private void processOutputFormat()
  {
    MediaFormat localMediaFormat = this.codec.getOutputFormat();
    if ((this.codecNeedsAdaptationWorkaround) && (localMediaFormat.getInteger("width") == 32) && (localMediaFormat.getInteger("height") == 32))
    {
      this.shouldSkipAdaptationWorkaroundOutputBuffer = true;
      return;
    }
    if (this.codecNeedsMonoChannelCountWorkaround)
      localMediaFormat.setInteger("channel-count", 1);
    onOutputFormatChanged(this.codec, localMediaFormat);
  }

  private void readFormat()
  {
    if (readSource(this.formatHolder, null) == -5)
      onInputFormatChanged(this.formatHolder.format);
  }

  private boolean shouldSkipOutputBuffer(long paramLong)
  {
    int j = this.decodeOnlyPresentationTimestamps.size();
    int i = 0;
    while (i < j)
    {
      if (((Long)this.decodeOnlyPresentationTimestamps.get(i)).longValue() == paramLong)
      {
        this.decodeOnlyPresentationTimestamps.remove(i);
        return true;
      }
      i += 1;
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

  private void throwDecoderInitError(DecoderInitializationException paramDecoderInitializationException)
  {
    throw ExoPlaybackException.createForRenderer(paramDecoderInitializationException, getIndex());
  }

  protected boolean canReconfigureCodec(MediaCodec paramMediaCodec, boolean paramBoolean, Format paramFormat1, Format paramFormat2)
  {
    return false;
  }

  protected abstract void configureCodec(MediaCodec paramMediaCodec, Format paramFormat, MediaCrypto paramMediaCrypto);

  protected void flushCodec()
  {
    this.codecHotswapDeadlineMs = -9223372036854775807L;
    this.inputIndex = -1;
    this.outputIndex = -1;
    this.waitingForKeys = false;
    this.shouldSkipOutputBuffer = false;
    this.decodeOnlyPresentationTimestamps.clear();
    this.codecNeedsAdaptationWorkaroundBuffer = false;
    this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
    if ((this.codecNeedsFlushWorkaround) || ((this.codecNeedsEosFlushWorkaround) && (this.codecReceivedEos)))
    {
      releaseCodec();
      maybeInitCodec();
    }
    while (true)
    {
      if ((this.codecReconfigured) && (this.format != null))
        this.codecReconfigurationState = 1;
      return;
      if (this.codecReinitializationState != 0)
      {
        releaseCodec();
        maybeInitCodec();
        continue;
      }
      this.codec.flush();
      this.codecReceivedBuffers = false;
    }
  }

  protected final MediaCodec getCodec()
  {
    return this.codec;
  }

  protected MediaCodecInfo getDecoderInfo(MediaCodecSelector paramMediaCodecSelector, Format paramFormat, boolean paramBoolean)
  {
    return paramMediaCodecSelector.getDecoderInfo(paramFormat.sampleMimeType, paramBoolean);
  }

  protected long getDequeueOutputBufferTimeoutUs()
  {
    return 0L;
  }

  public boolean isEnded()
  {
    return this.outputStreamEnded;
  }

  public boolean isReady()
  {
    return (this.format != null) && (!this.waitingForKeys) && ((isSourceReady()) || (this.outputIndex >= 0) || ((this.codecHotswapDeadlineMs != -9223372036854775807L) && (SystemClock.elapsedRealtime() < this.codecHotswapDeadlineMs)));
  }

  // ERROR //
  protected final void maybeInitCodec()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 528	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:shouldInitCodec	()Z
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload_0
    //   9: aload_0
    //   10: getfield 530	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:pendingDrmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   13: putfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   16: aload_0
    //   17: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   20: getfield 508	org/vidogram/messenger/exoplayer2/Format:sampleMimeType	Ljava/lang/String;
    //   23: astore 10
    //   25: aload_0
    //   26: getfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   29: ifnull +525 -> 554
    //   32: aload_0
    //   33: getfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   36: invokeinterface 476 1 0
    //   41: istore_1
    //   42: iload_1
    //   43: ifne +20 -> 63
    //   46: aload_0
    //   47: getfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   50: invokeinterface 480 1 0
    //   55: aload_0
    //   56: invokevirtual 345	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getIndex	()I
    //   59: invokestatic 351	org/vidogram/messenger/exoplayer2/ExoPlaybackException:createForRenderer	(Ljava/lang/Exception;I)Lorg/vidogram/messenger/exoplayer2/ExoPlaybackException;
    //   62: athrow
    //   63: iload_1
    //   64: iconst_3
    //   65: if_icmpeq +8 -> 73
    //   68: iload_1
    //   69: iconst_4
    //   70: if_icmpne -63 -> 7
    //   73: aload_0
    //   74: getfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   77: invokeinterface 534 1 0
    //   82: checkcast 536	org/vidogram/messenger/exoplayer2/drm/FrameworkMediaCrypto
    //   85: invokevirtual 540	org/vidogram/messenger/exoplayer2/drm/FrameworkMediaCrypto:getWrappedMediaCrypto	()Landroid/media/MediaCrypto;
    //   88: astore 9
    //   90: aload_0
    //   91: getfield 471	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:drmSession	Lorg/vidogram/messenger/exoplayer2/drm/DrmSession;
    //   94: aload 10
    //   96: invokeinterface 543 2 0
    //   101: istore_2
    //   102: aload_0
    //   103: aload_0
    //   104: getfield 117	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:mediaCodecSelector	Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;
    //   107: aload_0
    //   108: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   111: iload_2
    //   112: invokevirtual 545	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getDecoderInfo	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;Lorg/vidogram/messenger/exoplayer2/Format;Z)Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecInfo;
    //   115: astore 8
    //   117: aload 8
    //   119: astore 7
    //   121: aload 8
    //   123: ifnonnull +94 -> 217
    //   126: aload 8
    //   128: astore 7
    //   130: iload_2
    //   131: ifeq +86 -> 217
    //   134: aload 8
    //   136: astore 7
    //   138: aload_0
    //   139: aload_0
    //   140: getfield 117	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:mediaCodecSelector	Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;
    //   143: aload_0
    //   144: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   147: iconst_0
    //   148: invokevirtual 545	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getDecoderInfo	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecSelector;Lorg/vidogram/messenger/exoplayer2/Format;Z)Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecInfo;
    //   151: astore 8
    //   153: aload 8
    //   155: astore 7
    //   157: aload 8
    //   159: ifnull +58 -> 217
    //   162: aload 8
    //   164: astore 7
    //   166: ldc 32
    //   168: new 547	java/lang/StringBuilder
    //   171: dup
    //   172: invokespecial 548	java/lang/StringBuilder:<init>	()V
    //   175: ldc_w 550
    //   178: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   181: aload 10
    //   183: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: ldc_w 556
    //   189: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   192: aload 8
    //   194: getfield 561	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecInfo:name	Ljava/lang/String;
    //   197: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   200: ldc_w 563
    //   203: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: invokevirtual 567	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   209: invokestatic 573	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   212: pop
    //   213: aload 8
    //   215: astore 7
    //   217: aload 7
    //   219: ifnonnull +23 -> 242
    //   222: aload_0
    //   223: new 6	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException
    //   226: dup
    //   227: aload_0
    //   228: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   231: aconst_null
    //   232: iload_2
    //   233: ldc_w 574
    //   236: invokespecial 577	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException:<init>	(Lorg/vidogram/messenger/exoplayer2/Format;Ljava/lang/Throwable;ZI)V
    //   239: invokespecial 579	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:throwDecoderInitError	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException;)V
    //   242: aload 7
    //   244: getfield 561	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecInfo:name	Ljava/lang/String;
    //   247: astore 8
    //   249: aload_0
    //   250: aload 7
    //   252: getfield 582	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecInfo:adaptive	Z
    //   255: putfield 584	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecIsAdaptive	Z
    //   258: aload_0
    //   259: aload 8
    //   261: aload_0
    //   262: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   265: invokestatic 586	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	(Ljava/lang/String;Lorg/vidogram/messenger/exoplayer2/Format;)Z
    //   268: putfield 360	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsDiscardToSpsWorkaround	Z
    //   271: aload_0
    //   272: aload 8
    //   274: invokestatic 588	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsFlushWorkaround	(Ljava/lang/String;)Z
    //   277: putfield 494	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsFlushWorkaround	Z
    //   280: aload_0
    //   281: aload 8
    //   283: invokestatic 590	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaround	(Ljava/lang/String;)Z
    //   286: putfield 440	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsAdaptationWorkaround	Z
    //   289: aload_0
    //   290: aload 8
    //   292: invokestatic 592	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	(Ljava/lang/String;)Z
    //   295: putfield 282	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosPropagationWorkaround	Z
    //   298: aload_0
    //   299: aload 8
    //   301: invokestatic 594	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosFlushWorkaround	(Ljava/lang/String;)Z
    //   304: putfield 496	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsEosFlushWorkaround	Z
    //   307: aload_0
    //   308: aload 8
    //   310: aload_0
    //   311: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   314: invokestatic 596	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsMonoChannelCountWorkaround	(Ljava/lang/String;Lorg/vidogram/messenger/exoplayer2/Format;)Z
    //   317: putfield 452	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecNeedsMonoChannelCountWorkaround	Z
    //   320: invokestatic 521	android/os/SystemClock:elapsedRealtime	()J
    //   323: lstore_3
    //   324: new 547	java/lang/StringBuilder
    //   327: dup
    //   328: invokespecial 548	java/lang/StringBuilder:<init>	()V
    //   331: ldc_w 598
    //   334: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   337: aload 8
    //   339: invokevirtual 554	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: invokevirtual 567	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   345: invokestatic 604	org/vidogram/messenger/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
    //   348: aload_0
    //   349: aload 8
    //   351: invokestatic 608	android/media/MediaCodec:createByCodecName	(Ljava/lang/String;)Landroid/media/MediaCodec;
    //   354: putfield 219	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   357: invokestatic 611	org/vidogram/messenger/exoplayer2/util/TraceUtil:endSection	()V
    //   360: ldc_w 612
    //   363: invokestatic 604	org/vidogram/messenger/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
    //   366: aload_0
    //   367: aload_0
    //   368: getfield 219	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   371: aload_0
    //   372: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   375: aload 9
    //   377: invokevirtual 614	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:configureCodec	(Landroid/media/MediaCodec;Lorg/vidogram/messenger/exoplayer2/Format;Landroid/media/MediaCrypto;)V
    //   380: invokestatic 611	org/vidogram/messenger/exoplayer2/util/TraceUtil:endSection	()V
    //   383: ldc_w 616
    //   386: invokestatic 604	org/vidogram/messenger/exoplayer2/util/TraceUtil:beginSection	(Ljava/lang/String;)V
    //   389: aload_0
    //   390: getfield 219	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   393: invokevirtual 619	android/media/MediaCodec:start	()V
    //   396: invokestatic 611	org/vidogram/messenger/exoplayer2/util/TraceUtil:endSection	()V
    //   399: invokestatic 521	android/os/SystemClock:elapsedRealtime	()J
    //   402: lstore 5
    //   404: aload_0
    //   405: aload 8
    //   407: lload 5
    //   409: lload 5
    //   411: lload_3
    //   412: lsub
    //   413: invokevirtual 623	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:onCodecInitialized	(Ljava/lang/String;JJ)V
    //   416: aload_0
    //   417: aload_0
    //   418: getfield 219	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   421: invokevirtual 626	android/media/MediaCodec:getInputBuffers	()[Ljava/nio/ByteBuffer;
    //   424: putfield 295	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputBuffers	[Ljava/nio/ByteBuffer;
    //   427: aload_0
    //   428: aload_0
    //   429: getfield 219	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codec	Landroid/media/MediaCodec;
    //   432: invokevirtual 434	android/media/MediaCodec:getOutputBuffers	()[Ljava/nio/ByteBuffer;
    //   435: putfield 243	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:outputBuffers	[Ljava/nio/ByteBuffer;
    //   438: aload_0
    //   439: invokevirtual 627	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:getState	()I
    //   442: iconst_2
    //   443: if_icmpne +99 -> 542
    //   446: invokestatic 521	android/os/SystemClock:elapsedRealtime	()J
    //   449: ldc2_w 18
    //   452: ladd
    //   453: lstore_3
    //   454: aload_0
    //   455: lload_3
    //   456: putfield 491	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:codecHotswapDeadlineMs	J
    //   459: aload_0
    //   460: iconst_m1
    //   461: putfield 289	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:inputIndex	I
    //   464: aload_0
    //   465: iconst_m1
    //   466: putfield 217	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:outputIndex	I
    //   469: aload_0
    //   470: getfield 398	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:decoderCounters	Lorg/vidogram/messenger/exoplayer2/decoder/DecoderCounters;
    //   473: astore 7
    //   475: aload 7
    //   477: aload 7
    //   479: getfield 630	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:decoderInitCount	I
    //   482: iconst_1
    //   483: iadd
    //   484: putfield 630	org/vidogram/messenger/exoplayer2/decoder/DecoderCounters:decoderInitCount	I
    //   487: return
    //   488: astore 8
    //   490: aconst_null
    //   491: astore 7
    //   493: aload_0
    //   494: new 6	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException
    //   497: dup
    //   498: aload_0
    //   499: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   502: aload 8
    //   504: iload_2
    //   505: ldc_w 631
    //   508: invokespecial 577	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException:<init>	(Lorg/vidogram/messenger/exoplayer2/Format;Ljava/lang/Throwable;ZI)V
    //   511: invokespecial 579	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:throwDecoderInitError	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException;)V
    //   514: goto -297 -> 217
    //   517: astore 7
    //   519: aload_0
    //   520: new 6	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException
    //   523: dup
    //   524: aload_0
    //   525: getfield 325	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:format	Lorg/vidogram/messenger/exoplayer2/Format;
    //   528: aload 7
    //   530: iload_2
    //   531: aload 8
    //   533: invokespecial 634	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException:<init>	(Lorg/vidogram/messenger/exoplayer2/Format;Ljava/lang/Throwable;ZLjava/lang/String;)V
    //   536: invokespecial 579	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer:throwDecoderInitError	(Lorg/vidogram/messenger/exoplayer2/mediacodec/MediaCodecRenderer$DecoderInitializationException;)V
    //   539: goto -101 -> 438
    //   542: ldc2_w 488
    //   545: lstore_3
    //   546: goto -92 -> 454
    //   549: astore 8
    //   551: goto -58 -> 493
    //   554: iconst_0
    //   555: istore_2
    //   556: aconst_null
    //   557: astore 9
    //   559: goto -457 -> 102
    //
    // Exception table:
    //   from	to	target	type
    //   102	117	488	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecUtil$DecoderQueryException
    //   320	438	517	java/lang/Exception
    //   138	153	549	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecUtil$DecoderQueryException
    //   166	213	549	org/vidogram/messenger/exoplayer2/mediacodec/MediaCodecUtil$DecoderQueryException
  }

  protected void onCodecInitialized(String paramString, long paramLong1, long paramLong2)
  {
  }

  protected void onDisabled()
  {
    this.format = null;
    try
    {
      releaseCodec();
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
        }
      }
      finally
      {
      }
    }
    finally
    {
    }
    throw localObject7;
  }

  protected void onEnabled(boolean paramBoolean)
  {
    this.decoderCounters = new DecoderCounters();
  }

  protected void onInputFormatChanged(Format paramFormat)
  {
    Format localFormat = this.format;
    this.format = paramFormat;
    DrmInitData localDrmInitData = this.format.drmInitData;
    if (localFormat == null)
    {
      paramFormat = null;
      if (Util.areEqual(localDrmInitData, paramFormat))
        break label86;
    }
    label86: for (int i = 1; ; i = 0)
    {
      if (i == 0)
        break label138;
      if (this.format.drmInitData == null)
        break label232;
      if (this.drmSessionManager != null)
        break label91;
      throw ExoPlaybackException.createForRenderer(new IllegalStateException("Media requires a DrmSessionManager"), getIndex());
      paramFormat = localFormat.drmInitData;
      break;
    }
    label91: this.pendingDrmSession = this.drmSessionManager.acquireSession(Looper.myLooper(), this.format.drmInitData);
    if (this.pendingDrmSession == this.drmSession)
      this.drmSessionManager.releaseSession(this.pendingDrmSession);
    label138: if ((this.pendingDrmSession == this.drmSession) && (this.codec != null) && (canReconfigureCodec(this.codec, this.codecIsAdaptive, localFormat, this.format)))
    {
      this.codecReconfigured = true;
      this.codecReconfigurationState = 1;
      if ((this.codecNeedsAdaptationWorkaround) && (this.format.width == localFormat.width) && (this.format.height == localFormat.height));
      for (boolean bool = true; ; bool = false)
      {
        this.codecNeedsAdaptationWorkaroundBuffer = bool;
        return;
        label232: this.pendingDrmSession = null;
        break;
      }
    }
    if (this.codecReceivedBuffers)
    {
      this.codecReinitializationState = 1;
      return;
    }
    releaseCodec();
    maybeInitCodec();
  }

  protected void onOutputFormatChanged(MediaCodec paramMediaCodec, MediaFormat paramMediaFormat)
  {
  }

  protected void onOutputStreamEnded()
  {
  }

  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
    this.inputStreamEnded = false;
    this.outputStreamEnded = false;
    if (this.codec != null)
      flushCodec();
  }

  protected void onProcessedOutputBuffer(long paramLong)
  {
  }

  protected void onQueueInputBuffer(DecoderInputBuffer paramDecoderInputBuffer)
  {
  }

  protected void onStarted()
  {
  }

  protected void onStopped()
  {
  }

  protected abstract boolean processOutputBuffer(long paramLong1, long paramLong2, MediaCodec paramMediaCodec, ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, long paramLong3, boolean paramBoolean);

  protected void releaseCodec()
  {
    if (this.codec != null)
    {
      this.codecHotswapDeadlineMs = -9223372036854775807L;
      this.inputIndex = -1;
      this.outputIndex = -1;
      this.waitingForKeys = false;
      this.shouldSkipOutputBuffer = false;
      this.decodeOnlyPresentationTimestamps.clear();
      this.inputBuffers = null;
      this.outputBuffers = null;
      this.codecReconfigured = false;
      this.codecReceivedBuffers = false;
      this.codecIsAdaptive = false;
      this.codecNeedsDiscardToSpsWorkaround = false;
      this.codecNeedsFlushWorkaround = false;
      this.codecNeedsAdaptationWorkaround = false;
      this.codecNeedsEosPropagationWorkaround = false;
      this.codecNeedsEosFlushWorkaround = false;
      this.codecNeedsMonoChannelCountWorkaround = false;
      this.codecNeedsAdaptationWorkaroundBuffer = false;
      this.shouldSkipAdaptationWorkaroundOutputBuffer = false;
      this.codecReceivedEos = false;
      this.codecReconfigurationState = 0;
      this.codecReinitializationState = 0;
      DecoderCounters localDecoderCounters = this.decoderCounters;
      localDecoderCounters.decoderReleaseCount += 1;
    }
    try
    {
      this.codec.stop();
      try
      {
        this.codec.release();
        this.codec = null;
        if ((this.drmSession != null) && (this.pendingDrmSession != this.drmSession));
        try
        {
          this.drmSessionManager.releaseSession(this.drmSession);
          return;
        }
        finally
        {
          this.drmSession = null;
        }
      }
      finally
      {
        this.codec = null;
        if ((this.drmSession == null) || (this.pendingDrmSession == this.drmSession));
      }
    }
    finally
    {
    }
    throw localObject7;
  }

  public void render(long paramLong1, long paramLong2)
  {
    if (this.format == null)
      readFormat();
    maybeInitCodec();
    if (this.codec != null)
    {
      TraceUtil.beginSection("drainAndFeed");
      while (drainOutputBuffer(paramLong1, paramLong2));
      while (feedInputBuffer());
      TraceUtil.endSection();
    }
    while (true)
    {
      this.decoderCounters.ensureUpdated();
      return;
      if (this.format == null)
        continue;
      skipToKeyframeBefore(paramLong1);
    }
  }

  protected boolean shouldInitCodec()
  {
    return (this.codec == null) && (this.format != null);
  }

  public final int supportsFormat(Format paramFormat)
  {
    try
    {
      int i = supportsFormat(this.mediaCodecSelector, paramFormat);
      return i;
    }
    catch (MediaCodecUtil.DecoderQueryException paramFormat)
    {
    }
    throw ExoPlaybackException.createForRenderer(paramFormat, getIndex());
  }

  protected abstract int supportsFormat(MediaCodecSelector paramMediaCodecSelector, Format paramFormat);

  public final int supportsMixedMimeTypeAdaptation()
  {
    return 4;
  }

  public static class DecoderInitializationException extends Exception
  {
    private static final int CUSTOM_ERROR_CODE_BASE = -50000;
    private static final int DECODER_QUERY_ERROR = -49998;
    private static final int NO_SUITABLE_DECODER_ERROR = -49999;
    public final String decoderName;
    public final String diagnosticInfo;
    public final String mimeType;
    public final boolean secureDecoderRequired;

    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, int paramInt)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = null;
      this.diagnosticInfo = buildCustomDiagnosticInfo(paramInt);
    }

    public DecoderInitializationException(Format paramFormat, Throwable paramThrowable, boolean paramBoolean, String paramString)
    {
      super(paramThrowable);
      this.mimeType = paramFormat.sampleMimeType;
      this.secureDecoderRequired = paramBoolean;
      this.decoderName = paramString;
      if (Util.SDK_INT >= 21);
      for (paramFormat = getDiagnosticInfoV21(paramThrowable); ; paramFormat = null)
      {
        this.diagnosticInfo = paramFormat;
        return;
      }
    }

    private static String buildCustomDiagnosticInfo(int paramInt)
    {
      if (paramInt < 0);
      for (String str = "neg_"; ; str = "")
        return "com.google.android.exoplayer.MediaCodecTrackRenderer_" + str + Math.abs(paramInt);
    }

    @TargetApi(21)
    private static String getDiagnosticInfoV21(Throwable paramThrowable)
    {
      if ((paramThrowable instanceof MediaCodec.CodecException))
        return ((MediaCodec.CodecException)paramThrowable).getDiagnosticInfo();
      return null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecRenderer
 * JD-Core Version:    0.6.0
 */