package org.vidogram.messenger.exoplayer2.audio;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.AudioTimestamp;
import android.media.PlaybackParams;
import android.os.ConditionVariable;
import android.os.SystemClock;
import android.util.Log;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import org.vidogram.messenger.exoplayer2.C;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class AudioTrack
{
  private static final int BUFFER_MULTIPLICATION_FACTOR = 4;
  public static final long CURRENT_POSITION_NOT_SET = -9223372036854775808L;
  private static final int ERROR_BAD_VALUE = -2;
  private static final long MAX_AUDIO_TIMESTAMP_OFFSET_US = 5000000L;
  private static final long MAX_BUFFER_DURATION_US = 750000L;
  private static final long MAX_LATENCY_US = 5000000L;
  private static final int MAX_PLAYHEAD_OFFSET_COUNT = 10;
  private static final long MIN_BUFFER_DURATION_US = 250000L;
  private static final int MIN_PLAYHEAD_OFFSET_SAMPLE_INTERVAL_US = 30000;
  private static final int MIN_TIMESTAMP_SAMPLE_INTERVAL_US = 500000;
  private static final int MODE_STATIC = 0;
  private static final int MODE_STREAM = 1;
  private static final long PASSTHROUGH_BUFFER_DURATION_US = 250000L;
  private static final int PLAYSTATE_PAUSED = 2;
  private static final int PLAYSTATE_PLAYING = 3;
  private static final int PLAYSTATE_STOPPED = 1;
  public static final int RESULT_BUFFER_CONSUMED = 2;
  public static final int RESULT_POSITION_DISCONTINUITY = 1;
  public static final int SESSION_ID_NOT_SET = 0;
  private static final int START_IN_SYNC = 1;
  private static final int START_NEED_SYNC = 2;
  private static final int START_NOT_SET = 0;
  private static final int STATE_INITIALIZED = 1;
  private static final String TAG = "AudioTrack";

  @SuppressLint({"InlinedApi"})
  private static final int WRITE_NON_BLOCKING = 1;
  public static boolean enablePreV21AudioSessionWorkaround = false;
  public static boolean failOnSpuriousAudioTimestamp = false;
  private final AudioCapabilities audioCapabilities;
  private boolean audioTimestampSet;
  private android.media.AudioTrack audioTrack;
  private final AudioTrackUtil audioTrackUtil;
  private int bufferSize;
  private long bufferSizeUs;
  private int channelConfig;
  private ByteBuffer currentSourceBuffer;
  private int framesPerEncodedSample;
  private Method getLatencyMethod;
  private boolean hasData;
  private android.media.AudioTrack keepSessionIdAudioTrack;
  private long lastFeedElapsedRealtimeMs;
  private long lastPlayheadSampleTimeUs;
  private long lastTimestampSampleTimeUs;
  private long latencyUs;
  private final Listener listener;
  private int nextPlayheadOffsetIndex;
  private boolean passthrough;
  private int pcmFrameSize;
  private int playheadOffsetCount;
  private final long[] playheadOffsets;
  private final ConditionVariable releasingConditionVariable;
  private ByteBuffer resampledBuffer;
  private long resumeSystemTimeUs;
  private int sampleRate;
  private long smoothedPlayheadOffsetUs;
  private int sourceEncoding;
  private int startMediaTimeState;
  private long startMediaTimeUs;
  private int streamType;
  private long submittedEncodedFrames;
  private long submittedPcmBytes;
  private int targetEncoding;
  private byte[] temporaryBuffer;
  private int temporaryBufferOffset;
  private boolean useResampledBuffer;
  private float volume;

  public AudioTrack(AudioCapabilities paramAudioCapabilities, Listener paramListener)
  {
    this.audioCapabilities = paramAudioCapabilities;
    this.listener = paramListener;
    this.releasingConditionVariable = new ConditionVariable(true);
    if (Util.SDK_INT >= 18);
    try
    {
      this.getLatencyMethod = android.media.AudioTrack.class.getMethod("getLatency", (Class[])null);
      label49: if (Util.SDK_INT >= 23)
        this.audioTrackUtil = new AudioTrackUtilV23();
      while (true)
      {
        this.playheadOffsets = new long[10];
        this.volume = 1.0F;
        this.startMediaTimeState = 0;
        this.streamType = 3;
        return;
        if (Util.SDK_INT >= 19)
        {
          this.audioTrackUtil = new AudioTrackUtilV19();
          continue;
        }
        this.audioTrackUtil = new AudioTrackUtil(null);
      }
    }
    catch (java.lang.NoSuchMethodException paramAudioCapabilities)
    {
      break label49;
    }
  }

  // ERROR //
  private void checkAudioTrackInitialized()
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 195	org/vidogram/messenger/exoplayer2/audio/AudioTrack:audioTrack	Landroid/media/AudioTrack;
    //   4: invokevirtual 199	android/media/AudioTrack:getState	()I
    //   7: istore_1
    //   8: iload_1
    //   9: iconst_1
    //   10: if_icmpne +4 -> 14
    //   13: return
    //   14: aload_0
    //   15: getfield 195	org/vidogram/messenger/exoplayer2/audio/AudioTrack:audioTrack	Landroid/media/AudioTrack;
    //   18: invokevirtual 202	android/media/AudioTrack:release	()V
    //   21: aload_0
    //   22: aconst_null
    //   23: putfield 195	org/vidogram/messenger/exoplayer2/audio/AudioTrack:audioTrack	Landroid/media/AudioTrack;
    //   26: new 19	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException
    //   29: dup
    //   30: iload_1
    //   31: aload_0
    //   32: getfield 204	org/vidogram/messenger/exoplayer2/audio/AudioTrack:sampleRate	I
    //   35: aload_0
    //   36: getfield 206	org/vidogram/messenger/exoplayer2/audio/AudioTrack:channelConfig	I
    //   39: aload_0
    //   40: getfield 208	org/vidogram/messenger/exoplayer2/audio/AudioTrack:bufferSize	I
    //   43: invokespecial 211	org/vidogram/messenger/exoplayer2/audio/AudioTrack$InitializationException:<init>	(IIII)V
    //   46: athrow
    //   47: astore_2
    //   48: aload_0
    //   49: aconst_null
    //   50: putfield 195	org/vidogram/messenger/exoplayer2/audio/AudioTrack:audioTrack	Landroid/media/AudioTrack;
    //   53: goto -27 -> 26
    //   56: astore_2
    //   57: aload_0
    //   58: aconst_null
    //   59: putfield 195	org/vidogram/messenger/exoplayer2/audio/AudioTrack:audioTrack	Landroid/media/AudioTrack;
    //   62: aload_2
    //   63: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   14	21	47	java/lang/Exception
    //   14	21	56	finally
  }

  private long durationUsToFrames(long paramLong)
  {
    return this.sampleRate * paramLong / 1000000L;
  }

  private long framesToDurationUs(long paramLong)
  {
    return 1000000L * paramLong / this.sampleRate;
  }

  private static int getEncodingForMimeType(String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 187078296:
    case 1504578661:
    case -1095064472:
    case 1505942594:
    }
    while (true)
      switch (i)
      {
      default:
        return 0;
        if (!paramString.equals("audio/ac3"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("audio/eac3"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("audio/vnd.dts"))
          continue;
        i = 2;
        continue;
        if (!paramString.equals("audio/vnd.dts.hd"))
          continue;
        i = 3;
      case 0:
      case 1:
      case 2:
      case 3:
      }
    return 5;
    return 6;
    return 7;
    return 8;
  }

  private static int getFramesPerEncodedSample(int paramInt, ByteBuffer paramByteBuffer)
  {
    if ((paramInt == 7) || (paramInt == 8))
      return DtsUtil.parseDtsAudioSampleCount(paramByteBuffer);
    if (paramInt == 5)
      return Ac3Util.getAc3SyncframeAudioSampleCount();
    if (paramInt == 6)
      return Ac3Util.parseEAc3SyncframeAudioSampleCount(paramByteBuffer);
    throw new IllegalStateException("Unexpected audio encoding: " + paramInt);
  }

  private long getSubmittedFrames()
  {
    if (this.passthrough)
      return this.submittedEncodedFrames;
    return pcmBytesToFrames(this.submittedPcmBytes);
  }

  private boolean hasCurrentPositionUs()
  {
    return (isInitialized()) && (this.startMediaTimeState != 0);
  }

  private void maybeSampleSyncParams()
  {
    long l1 = this.audioTrackUtil.getPlaybackHeadPositionUs();
    if (l1 == 0L);
    long l2;
    do
    {
      return;
      l2 = System.nanoTime() / 1000L;
      if (l2 - this.lastPlayheadSampleTimeUs < 30000L)
        continue;
      this.playheadOffsets[this.nextPlayheadOffsetIndex] = (l1 - l2);
      this.nextPlayheadOffsetIndex = ((this.nextPlayheadOffsetIndex + 1) % 10);
      if (this.playheadOffsetCount < 10)
        this.playheadOffsetCount += 1;
      this.lastPlayheadSampleTimeUs = l2;
      this.smoothedPlayheadOffsetUs = 0L;
      int i = 0;
      while (i < this.playheadOffsetCount)
      {
        this.smoothedPlayheadOffsetUs += this.playheadOffsets[i] / this.playheadOffsetCount;
        i += 1;
      }
    }
    while ((needsPassthroughWorkarounds()) || (l2 - this.lastTimestampSampleTimeUs < 500000L));
    this.audioTimestampSet = this.audioTrackUtil.updateTimestamp();
    long l3;
    long l4;
    if (this.audioTimestampSet)
    {
      l3 = this.audioTrackUtil.getTimestampNanoTime() / 1000L;
      l4 = this.audioTrackUtil.getTimestampFramePosition();
      if (l3 >= this.resumeSystemTimeUs)
        break label321;
      this.audioTimestampSet = false;
    }
    while (true)
    {
      if ((this.getLatencyMethod != null) && (!this.passthrough));
      try
      {
        this.latencyUs = (((Integer)this.getLatencyMethod.invoke(this.audioTrack, (Object[])null)).intValue() * 1000L - this.bufferSizeUs);
        this.latencyUs = Math.max(this.latencyUs, 0L);
        if (this.latencyUs > 5000000L)
        {
          Log.w("AudioTrack", "Ignoring impossibly large audio latency: " + this.latencyUs);
          this.latencyUs = 0L;
        }
        this.lastTimestampSampleTimeUs = l2;
        return;
        label321: if (Math.abs(l3 - l2) > 5000000L)
        {
          str = "Spurious audio timestamp (system clock mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1;
          if (failOnSpuriousAudioTimestamp)
            throw new InvalidAudioTrackTimestampException(str);
          Log.w("AudioTrack", str);
          this.audioTimestampSet = false;
          continue;
        }
        if (Math.abs(framesToDurationUs(l4) - l1) <= 5000000L)
          continue;
        String str = "Spurious audio timestamp (frame position mismatch): " + l4 + ", " + l3 + ", " + l2 + ", " + l1;
        if (failOnSpuriousAudioTimestamp)
          throw new InvalidAudioTrackTimestampException(str);
        Log.w("AudioTrack", str);
        this.audioTimestampSet = false;
      }
      catch (Exception localException)
      {
        while (true)
          this.getLatencyMethod = null;
      }
    }
  }

  private boolean needsPassthroughWorkarounds()
  {
    return (Util.SDK_INT < 23) && ((this.targetEncoding == 5) || (this.targetEncoding == 6));
  }

  private boolean overrideHasPendingData()
  {
    return (needsPassthroughWorkarounds()) && (this.audioTrack.getPlayState() == 2) && (this.audioTrack.getPlaybackHeadPosition() == 0);
  }

  private long pcmBytesToFrames(long paramLong)
  {
    return paramLong / this.pcmFrameSize;
  }

  private void releaseKeepSessionIdAudioTrack()
  {
    if (this.keepSessionIdAudioTrack == null)
      return;
    android.media.AudioTrack localAudioTrack = this.keepSessionIdAudioTrack;
    this.keepSessionIdAudioTrack = null;
    new Thread(localAudioTrack)
    {
      public void run()
      {
        this.val$toRelease.release();
      }
    }
    .start();
  }

  private static ByteBuffer resampleTo16BitPcm(ByteBuffer paramByteBuffer1, int paramInt, ByteBuffer paramByteBuffer2)
  {
    int i = paramByteBuffer1.position();
    int m = paramByteBuffer1.limit();
    int j = m - i;
    switch (paramInt)
    {
    default:
      throw new IllegalStateException();
    case 3:
      j *= 2;
    case -2147483648:
    case 1073741824:
    }
    ByteBuffer localByteBuffer;
    int k;
    while (true)
    {
      if (paramByteBuffer2 != null)
      {
        localByteBuffer = paramByteBuffer2;
        if (paramByteBuffer2.capacity() >= j);
      }
      else
      {
        localByteBuffer = ByteBuffer.allocateDirect(j);
      }
      localByteBuffer.position(0);
      localByteBuffer.limit(j);
      j = i;
      k = i;
      switch (paramInt)
      {
      default:
        throw new IllegalStateException();
        j = j / 3 * 2;
        continue;
        j /= 2;
      case 3:
      case -2147483648:
      case 1073741824:
      }
    }
    while (j < m)
    {
      localByteBuffer.put(0);
      localByteBuffer.put((byte)((paramByteBuffer1.get(j) & 0xFF) - 128));
      j += 1;
      continue;
      while (k < m)
      {
        localByteBuffer.put(paramByteBuffer1.get(k + 1));
        localByteBuffer.put(paramByteBuffer1.get(k + 2));
        k += 3;
        continue;
        while (i < m)
        {
          localByteBuffer.put(paramByteBuffer1.get(i + 2));
          localByteBuffer.put(paramByteBuffer1.get(i + 3));
          i += 4;
        }
      }
    }
    localByteBuffer.position(0);
    return localByteBuffer;
  }

  private void resetSyncParams()
  {
    this.smoothedPlayheadOffsetUs = 0L;
    this.playheadOffsetCount = 0;
    this.nextPlayheadOffsetIndex = 0;
    this.lastPlayheadSampleTimeUs = 0L;
    this.audioTimestampSet = false;
    this.lastTimestampSampleTimeUs = 0L;
  }

  private void setAudioTrackVolume()
  {
    if (!isInitialized())
      return;
    if (Util.SDK_INT >= 21)
    {
      setAudioTrackVolumeV21(this.audioTrack, this.volume);
      return;
    }
    setAudioTrackVolumeV3(this.audioTrack, this.volume);
  }

  @TargetApi(21)
  private static void setAudioTrackVolumeV21(android.media.AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setVolume(paramFloat);
  }

  private static void setAudioTrackVolumeV3(android.media.AudioTrack paramAudioTrack, float paramFloat)
  {
    paramAudioTrack.setStereoVolume(paramFloat, paramFloat);
  }

  private int writeBuffer(ByteBuffer paramByteBuffer, long paramLong)
  {
    int j = 1;
    int k = 0;
    int i;
    boolean bool;
    if (this.currentSourceBuffer == null)
    {
      i = 1;
      if ((i == 0) && (this.currentSourceBuffer != paramByteBuffer))
        break label68;
      bool = true;
      label32: Assertions.checkState(bool);
      this.currentSourceBuffer = paramByteBuffer;
      if (!needsPassthroughWorkarounds())
        break label97;
      if (this.audioTrack.getPlayState() != 2)
        break label74;
    }
    label68: label74: 
    do
    {
      return 0;
      i = 0;
      break;
      bool = false;
      break label32;
    }
    while ((this.audioTrack.getPlayState() == 1) && (this.audioTrackUtil.getPlaybackHeadPosition() != 0L));
    label97: label236: ByteBuffer localByteBuffer;
    label157: int m;
    if (i != 0)
    {
      if (!this.currentSourceBuffer.hasRemaining())
      {
        this.currentSourceBuffer = null;
        return 2;
      }
      if (this.targetEncoding != this.sourceEncoding)
      {
        bool = true;
        this.useResampledBuffer = bool;
        if (this.useResampledBuffer)
        {
          if (this.targetEncoding != 2)
            break label465;
          bool = true;
          Assertions.checkState(bool);
          this.resampledBuffer = resampleTo16BitPcm(this.currentSourceBuffer, this.sourceEncoding, this.resampledBuffer);
          paramByteBuffer = this.resampledBuffer;
        }
        if ((this.passthrough) && (this.framesPerEncodedSample == 0))
          this.framesPerEncodedSample = getFramesPerEncodedSample(this.targetEncoding, paramByteBuffer);
        if (this.startMediaTimeState != 0)
          break label471;
        this.startMediaTimeUs = Math.max(0L, paramLong);
        this.startMediaTimeState = 1;
        i = 0;
        j = i;
        localByteBuffer = paramByteBuffer;
        if (Util.SDK_INT < 21)
        {
          j = paramByteBuffer.remaining();
          if ((this.temporaryBuffer == null) || (this.temporaryBuffer.length < j))
            this.temporaryBuffer = new byte[j];
          m = paramByteBuffer.position();
          paramByteBuffer.get(this.temporaryBuffer, 0, j);
          paramByteBuffer.position(m);
          this.temporaryBufferOffset = 0;
          localByteBuffer = paramByteBuffer;
          j = i;
        }
      }
    }
    while (true)
    {
      if (this.useResampledBuffer)
        localByteBuffer = this.resampledBuffer;
      m = localByteBuffer.remaining();
      if (Util.SDK_INT < 21)
      {
        i = (int)(this.submittedPcmBytes - this.audioTrackUtil.getPlaybackHeadPosition() * this.pcmFrameSize);
        int n = this.bufferSize - i;
        i = k;
        if (n > 0)
        {
          i = Math.min(m, n);
          i = this.audioTrack.write(this.temporaryBuffer, this.temporaryBufferOffset, i);
          if (i >= 0)
            this.temporaryBufferOffset += i;
          localByteBuffer.position(localByteBuffer.position() + i);
        }
      }
      while (true)
      {
        if (i >= 0)
          break label605;
        throw new WriteException(i);
        bool = false;
        break;
        label465: bool = false;
        break label157;
        label471: long l = this.startMediaTimeUs + framesToDurationUs(getSubmittedFrames());
        if ((this.startMediaTimeState == 1) && (Math.abs(l - paramLong) > 200000L))
        {
          Log.e("AudioTrack", "Discontinuity detected [expected " + l + ", got " + paramLong + "]");
          this.startMediaTimeState = 2;
        }
        if (this.startMediaTimeState != 2)
          break label670;
        this.startMediaTimeUs = (paramLong - l + this.startMediaTimeUs);
        this.startMediaTimeState = 1;
        i = j;
        break label236;
        i = writeNonBlockingV21(this.audioTrack, localByteBuffer, m);
      }
      label605: if (!this.passthrough)
        this.submittedPcmBytes += i;
      k = j;
      if (i == m)
      {
        if (this.passthrough)
          this.submittedEncodedFrames += this.framesPerEncodedSample;
        this.currentSourceBuffer = null;
        k = j | 0x2;
      }
      return k;
      label670: i = 0;
      break label236;
      j = 0;
      localByteBuffer = paramByteBuffer;
    }
  }

  @TargetApi(21)
  private static int writeNonBlockingV21(android.media.AudioTrack paramAudioTrack, ByteBuffer paramByteBuffer, int paramInt)
  {
    return paramAudioTrack.write(paramByteBuffer, paramInt, 1);
  }

  public void configure(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i;
    boolean bool1;
    label92: int j;
    switch (paramInt1)
    {
    default:
      throw new IllegalArgumentException("Unsupported channel count: " + paramInt1);
    case 1:
      i = 4;
      if ("audio/raw".equals(paramString))
        break;
      bool1 = true;
      if (bool1)
        j = getEncodingForMimeType(paramString);
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    }
    do
    {
      do
      {
        do
        {
          do
          {
            if ((!isInitialized()) || (this.sourceEncoding != j) || (this.sampleRate != paramInt2) || (this.channelConfig != i))
              break label270;
            return;
            i = 12;
            break;
            i = 28;
            break;
            i = 204;
            break;
            i = 220;
            break;
            i = 252;
            break;
            i = 1276;
            break;
            i = C.CHANNEL_OUT_7POINT1_SURROUND;
            break;
            bool1 = false;
            break label92;
            j = paramInt3;
          }
          while (paramInt3 == 3);
          j = paramInt3;
        }
        while (paramInt3 == 2);
        j = paramInt3;
      }
      while (paramInt3 == -2147483648);
      j = paramInt3;
    }
    while (paramInt3 == 1073741824);
    throw new IllegalArgumentException("Unsupported PCM encoding: " + paramInt3);
    label270: reset();
    this.sourceEncoding = j;
    this.passthrough = bool1;
    this.sampleRate = paramInt2;
    this.channelConfig = i;
    label326: long l;
    if (bool1)
    {
      this.targetEncoding = j;
      this.pcmFrameSize = (paramInt1 * 2);
      if (paramInt4 == 0)
        break label349;
      this.bufferSize = paramInt4;
      if (!bool1)
        break label495;
      l = -9223372036854775807L;
    }
    while (true)
    {
      this.bufferSizeUs = l;
      return;
      j = 2;
      break;
      label349: if (bool1)
      {
        if ((this.targetEncoding == 5) || (this.targetEncoding == 6))
        {
          this.bufferSize = 20480;
          break label326;
        }
        this.bufferSize = 49152;
        break label326;
      }
      paramInt3 = android.media.AudioTrack.getMinBufferSize(paramInt2, i, this.targetEncoding);
      boolean bool2;
      if (paramInt3 != -2)
      {
        bool2 = true;
        label413: Assertions.checkState(bool2);
        paramInt1 = paramInt3 * 4;
        paramInt2 = (int)durationUsToFrames(250000L) * this.pcmFrameSize;
        paramInt3 = (int)Math.max(paramInt3, durationUsToFrames(750000L) * this.pcmFrameSize);
        if (paramInt1 >= paramInt2)
          break label480;
        paramInt1 = paramInt2;
      }
      while (true)
      {
        this.bufferSize = paramInt1;
        break;
        bool2 = false;
        break label413;
        label480: if (paramInt1 > paramInt3)
        {
          paramInt1 = paramInt3;
          continue;
        }
      }
      label495: l = framesToDurationUs(pcmBytesToFrames(this.bufferSize));
    }
  }

  public long getCurrentPositionUs(boolean paramBoolean)
  {
    long l2;
    if (!hasCurrentPositionUs())
    {
      l2 = -9223372036854775808L;
      return l2;
    }
    if (this.audioTrack.getPlayState() == 3)
      maybeSampleSyncParams();
    long l1 = System.nanoTime() / 1000L;
    if (this.audioTimestampSet)
      return framesToDurationUs(durationUsToFrames(()((float)(l1 - this.audioTrackUtil.getTimestampNanoTime() / 1000L) * this.audioTrackUtil.getPlaybackSpeed())) + this.audioTrackUtil.getTimestampFramePosition()) + this.startMediaTimeUs;
    if (this.playheadOffsetCount == 0)
      l1 = this.audioTrackUtil.getPlaybackHeadPositionUs() + this.startMediaTimeUs;
    while (true)
    {
      l2 = l1;
      if (paramBoolean)
        break;
      return l1 - this.latencyUs;
      l1 = l1 + this.smoothedPlayheadOffsetUs + this.startMediaTimeUs;
    }
  }

  public int handleBuffer(ByteBuffer paramByteBuffer, long paramLong)
  {
    boolean bool = this.hasData;
    this.hasData = hasPendingData();
    if ((bool) && (!this.hasData) && (this.audioTrack.getPlayState() != 1))
    {
      long l1 = SystemClock.elapsedRealtime();
      long l2 = this.lastFeedElapsedRealtimeMs;
      this.listener.onUnderrun(this.bufferSize, C.usToMs(this.bufferSizeUs), l1 - l2);
    }
    int i = writeBuffer(paramByteBuffer, paramLong);
    this.lastFeedElapsedRealtimeMs = SystemClock.elapsedRealtime();
    return i;
  }

  public void handleDiscontinuity()
  {
    if (this.startMediaTimeState == 1)
      this.startMediaTimeState = 2;
  }

  public void handleEndOfStream()
  {
    if (isInitialized())
      this.audioTrackUtil.handleEndOfStream(getSubmittedFrames());
  }

  public boolean hasPendingData()
  {
    return (isInitialized()) && ((getSubmittedFrames() > this.audioTrackUtil.getPlaybackHeadPosition()) || (overrideHasPendingData()));
  }

  public int initialize(int paramInt)
  {
    this.releasingConditionVariable.block();
    if (paramInt == 0);
    for (this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, 1); ; this.audioTrack = new android.media.AudioTrack(this.streamType, this.sampleRate, this.channelConfig, this.targetEncoding, this.bufferSize, 1, paramInt))
    {
      checkAudioTrackInitialized();
      paramInt = this.audioTrack.getAudioSessionId();
      if ((enablePreV21AudioSessionWorkaround) && (Util.SDK_INT < 21))
      {
        if ((this.keepSessionIdAudioTrack != null) && (paramInt != this.keepSessionIdAudioTrack.getAudioSessionId()))
          releaseKeepSessionIdAudioTrack();
        if (this.keepSessionIdAudioTrack == null)
          this.keepSessionIdAudioTrack = new android.media.AudioTrack(this.streamType, 4000, 4, 2, 2, 0, paramInt);
      }
      this.audioTrackUtil.reconfigure(this.audioTrack, needsPassthroughWorkarounds());
      setAudioTrackVolume();
      this.hasData = false;
      return paramInt;
    }
  }

  public boolean isInitialized()
  {
    return this.audioTrack != null;
  }

  public boolean isPassthroughSupported(String paramString)
  {
    return (this.audioCapabilities != null) && (this.audioCapabilities.supportsEncoding(getEncodingForMimeType(paramString)));
  }

  public void pause()
  {
    if (isInitialized())
    {
      resetSyncParams();
      this.audioTrackUtil.pause();
    }
  }

  public void play()
  {
    if (isInitialized())
    {
      this.resumeSystemTimeUs = (System.nanoTime() / 1000L);
      this.audioTrack.play();
    }
  }

  public void release()
  {
    reset();
    releaseKeepSessionIdAudioTrack();
  }

  public void reset()
  {
    if (isInitialized())
    {
      this.submittedPcmBytes = 0L;
      this.submittedEncodedFrames = 0L;
      this.framesPerEncodedSample = 0;
      this.currentSourceBuffer = null;
      this.startMediaTimeState = 0;
      this.latencyUs = 0L;
      resetSyncParams();
      if (this.audioTrack.getPlayState() == 3)
        this.audioTrack.pause();
      android.media.AudioTrack localAudioTrack = this.audioTrack;
      this.audioTrack = null;
      this.audioTrackUtil.reconfigure(null, false);
      this.releasingConditionVariable.close();
      new Thread(localAudioTrack)
      {
        public void run()
        {
          try
          {
            this.val$toRelease.flush();
            this.val$toRelease.release();
            return;
          }
          finally
          {
            AudioTrack.this.releasingConditionVariable.open();
          }
          throw localObject;
        }
      }
      .start();
    }
  }

  public void setPlaybackParams(PlaybackParams paramPlaybackParams)
  {
    this.audioTrackUtil.setPlaybackParams(paramPlaybackParams);
  }

  public boolean setStreamType(int paramInt)
  {
    if (this.streamType == paramInt)
      return false;
    this.streamType = paramInt;
    reset();
    return true;
  }

  public void setVolume(float paramFloat)
  {
    if (this.volume != paramFloat)
    {
      this.volume = paramFloat;
      setAudioTrackVolume();
    }
  }

  private static class AudioTrackUtil
  {
    protected android.media.AudioTrack audioTrack;
    private long endPlaybackHeadPosition;
    private long lastRawPlaybackHeadPosition;
    private boolean needsPassthroughWorkaround;
    private long passthroughWorkaroundPauseOffset;
    private long rawPlaybackHeadWrapCount;
    private int sampleRate;
    private long stopPlaybackHeadPosition;
    private long stopTimestampUs;

    public long getPlaybackHeadPosition()
    {
      if (this.stopTimestampUs != -9223372036854775807L)
      {
        l1 = (SystemClock.elapsedRealtime() * 1000L - this.stopTimestampUs) * this.sampleRate / 1000000L;
        return Math.min(this.endPlaybackHeadPosition, l1 + this.stopPlaybackHeadPosition);
      }
      int i = this.audioTrack.getPlayState();
      if (i == 1)
        return 0L;
      long l2 = 0xFFFFFFFF & this.audioTrack.getPlaybackHeadPosition();
      long l1 = l2;
      if (this.needsPassthroughWorkaround)
      {
        if ((i == 2) && (l2 == 0L))
          this.passthroughWorkaroundPauseOffset = this.lastRawPlaybackHeadPosition;
        l1 = l2 + this.passthroughWorkaroundPauseOffset;
      }
      if (this.lastRawPlaybackHeadPosition > l1)
        this.rawPlaybackHeadWrapCount += 1L;
      this.lastRawPlaybackHeadPosition = l1;
      return l1 + (this.rawPlaybackHeadWrapCount << 32);
    }

    public long getPlaybackHeadPositionUs()
    {
      return getPlaybackHeadPosition() * 1000000L / this.sampleRate;
    }

    public float getPlaybackSpeed()
    {
      return 1.0F;
    }

    public long getTimestampFramePosition()
    {
      throw new UnsupportedOperationException();
    }

    public long getTimestampNanoTime()
    {
      throw new UnsupportedOperationException();
    }

    public void handleEndOfStream(long paramLong)
    {
      this.stopPlaybackHeadPosition = getPlaybackHeadPosition();
      this.stopTimestampUs = (SystemClock.elapsedRealtime() * 1000L);
      this.endPlaybackHeadPosition = paramLong;
      this.audioTrack.stop();
    }

    public void pause()
    {
      if (this.stopTimestampUs != -9223372036854775807L)
        return;
      this.audioTrack.pause();
    }

    public void reconfigure(android.media.AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      this.audioTrack = paramAudioTrack;
      this.needsPassthroughWorkaround = paramBoolean;
      this.stopTimestampUs = -9223372036854775807L;
      this.lastRawPlaybackHeadPosition = 0L;
      this.rawPlaybackHeadWrapCount = 0L;
      this.passthroughWorkaroundPauseOffset = 0L;
      if (paramAudioTrack != null)
        this.sampleRate = paramAudioTrack.getSampleRate();
    }

    public void setPlaybackParams(PlaybackParams paramPlaybackParams)
    {
      throw new UnsupportedOperationException();
    }

    public boolean updateTimestamp()
    {
      return false;
    }
  }

  @TargetApi(19)
  private static class AudioTrackUtilV19 extends AudioTrack.AudioTrackUtil
  {
    private final AudioTimestamp audioTimestamp = new AudioTimestamp();
    private long lastRawTimestampFramePosition;
    private long lastTimestampFramePosition;
    private long rawTimestampFramePositionWrapCount;

    public AudioTrackUtilV19()
    {
      super();
    }

    public long getTimestampFramePosition()
    {
      return this.lastTimestampFramePosition;
    }

    public long getTimestampNanoTime()
    {
      return this.audioTimestamp.nanoTime;
    }

    public void reconfigure(android.media.AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      super.reconfigure(paramAudioTrack, paramBoolean);
      this.rawTimestampFramePositionWrapCount = 0L;
      this.lastRawTimestampFramePosition = 0L;
      this.lastTimestampFramePosition = 0L;
    }

    public boolean updateTimestamp()
    {
      boolean bool = this.audioTrack.getTimestamp(this.audioTimestamp);
      if (bool)
      {
        long l = this.audioTimestamp.framePosition;
        if (this.lastRawTimestampFramePosition > l)
          this.rawTimestampFramePositionWrapCount += 1L;
        this.lastRawTimestampFramePosition = l;
        this.lastTimestampFramePosition = (l + (this.rawTimestampFramePositionWrapCount << 32));
      }
      return bool;
    }
  }

  @TargetApi(23)
  private static class AudioTrackUtilV23 extends AudioTrack.AudioTrackUtilV19
  {
    private PlaybackParams playbackParams;
    private float playbackSpeed = 1.0F;

    private void maybeApplyPlaybackParams()
    {
      if ((this.audioTrack != null) && (this.playbackParams != null))
        this.audioTrack.setPlaybackParams(this.playbackParams);
    }

    public float getPlaybackSpeed()
    {
      return this.playbackSpeed;
    }

    public void reconfigure(android.media.AudioTrack paramAudioTrack, boolean paramBoolean)
    {
      super.reconfigure(paramAudioTrack, paramBoolean);
      maybeApplyPlaybackParams();
    }

    public void setPlaybackParams(PlaybackParams paramPlaybackParams)
    {
      if (paramPlaybackParams != null);
      while (true)
      {
        paramPlaybackParams = paramPlaybackParams.allowDefaults();
        this.playbackParams = paramPlaybackParams;
        this.playbackSpeed = paramPlaybackParams.getSpeed();
        maybeApplyPlaybackParams();
        return;
        paramPlaybackParams = new PlaybackParams();
      }
    }
  }

  public static final class InitializationException extends Exception
  {
    public final int audioTrackState;

    public InitializationException(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super();
      this.audioTrackState = paramInt1;
    }
  }

  public static final class InvalidAudioTrackTimestampException extends RuntimeException
  {
    public InvalidAudioTrackTimestampException(String paramString)
    {
      super();
    }
  }

  public static abstract interface Listener
  {
    public abstract void onUnderrun(int paramInt, long paramLong1, long paramLong2);
  }

  public static final class WriteException extends Exception
  {
    public final int errorCode;

    public WriteException(int paramInt)
    {
      super();
      this.errorCode = paramInt;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.audio.AudioTrack
 * JD-Core Version:    0.6.0
 */