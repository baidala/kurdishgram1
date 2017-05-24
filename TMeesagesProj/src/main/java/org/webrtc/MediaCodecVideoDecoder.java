package org.webrtc;

import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build.VERSION;
import android.os.SystemClock;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MediaCodecVideoDecoder
{
  private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
  private static final int DEQUEUE_INPUT_TIMEOUT = 500000;
  private static final String H264_MIME_TYPE = "video/avc";
  private static final long MAX_DECODE_TIME_MS = 200L;
  private static final int MAX_QUEUED_OUTPUTBUFFERS = 3;
  private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
  private static final String TAG = "MediaCodecVideoDecoder";
  private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
  private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
  private static int codecErrors;
  private static MediaCodecVideoDecoderErrorCallback errorCallback;
  private static Set<String> hwDecoderDisabledTypes;
  private static MediaCodecVideoDecoder runningInstance = null;
  private static final List<Integer> supportedColorList;
  private static final String[] supportedH264HwCodecPrefixes;
  private static final String[] supportedVp8HwCodecPrefixes;
  private static final String[] supportedVp9HwCodecPrefixes;
  private int colorFormat;
  private final Queue<TimeStamps> decodeStartTimeMs = new LinkedList();
  private final Queue<DecodedOutputBuffer> dequeuedSurfaceOutputBuffers = new LinkedList();
  private int droppedFrames;
  private boolean hasDecodedFirstFrame;
  private int height;
  private ByteBuffer[] inputBuffers;
  private MediaCodec mediaCodec;
  private Thread mediaCodecThread;
  private ByteBuffer[] outputBuffers;
  private int sliceHeight;
  private int stride;
  private Surface surface = null;
  private TextureListener textureListener;
  private boolean useSurface;
  private int width;

  static
  {
    errorCallback = null;
    codecErrors = 0;
    hwDecoderDisabledTypes = new HashSet();
    supportedVp8HwCodecPrefixes = new String[] { "OMX.qcom.", "OMX.Nvidia.", "OMX.Exynos.", "OMX.Intel." };
    supportedVp9HwCodecPrefixes = new String[] { "OMX.qcom.", "OMX.Exynos." };
    supportedH264HwCodecPrefixes = new String[] { "OMX.qcom.", "OMX.Intel.", "OMX.Exynos." };
    supportedColorList = Arrays.asList(new Integer[] { Integer.valueOf(19), Integer.valueOf(21), Integer.valueOf(2141391872), Integer.valueOf(2141391876) });
  }

  private void MaybeRenderDecodedTextureBuffer()
  {
    if ((this.dequeuedSurfaceOutputBuffers.isEmpty()) || (this.textureListener.isWaitingForTexture()))
      return;
    DecodedOutputBuffer localDecodedOutputBuffer = (DecodedOutputBuffer)this.dequeuedSurfaceOutputBuffers.remove();
    this.textureListener.addBufferToRender(localDecodedOutputBuffer);
    this.mediaCodec.releaseOutputBuffer(localDecodedOutputBuffer.index, true);
  }

  private void checkOnMediaCodecThread()
  {
    if (this.mediaCodecThread.getId() != Thread.currentThread().getId())
      throw new IllegalStateException("MediaCodecVideoDecoder previously operated on " + this.mediaCodecThread + " but is now called on " + Thread.currentThread());
  }

  private int dequeueInputBuffer()
  {
    checkOnMediaCodecThread();
    try
    {
      int i = this.mediaCodec.dequeueInputBuffer(500000L);
      return i;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoDecoder", "dequeueIntputBuffer failed", localIllegalStateException);
    }
    return -2;
  }

  private DecodedOutputBuffer dequeueOutputBuffer(int paramInt)
  {
    long l1 = 200L;
    checkOnMediaCodecThread();
    if (this.decodeStartTimeMs.isEmpty())
      return null;
    MediaCodec.BufferInfo localBufferInfo = new MediaCodec.BufferInfo();
    int i = this.mediaCodec.dequeueOutputBuffer(localBufferInfo, TimeUnit.MILLISECONDS.toMicros(paramInt));
    Object localObject;
    long l2;
    switch (i)
    {
    case -1:
    default:
      this.hasDecodedFirstFrame = true;
      localObject = (TimeStamps)this.decodeStartTimeMs.remove();
      l2 = SystemClock.elapsedRealtime() - ((TimeStamps)localObject).decodeStartTimeMs;
      if (l2 <= 200L)
        break;
      Logging.e("MediaCodecVideoDecoder", "Very high decode time: " + l2 + "ms" + ". Q size: " + this.decodeStartTimeMs.size() + ". Might be caused by resuming H264 decoding after a pause.");
    case -3:
    case -2:
    }
    while (true)
    {
      return new DecodedOutputBuffer(i, localBufferInfo.offset, localBufferInfo.size, TimeUnit.MICROSECONDS.toMillis(localBufferInfo.presentationTimeUs), ((TimeStamps)localObject).timeStampMs, ((TimeStamps)localObject).ntpTimeStampMs, l1, SystemClock.elapsedRealtime());
      this.outputBuffers = this.mediaCodec.getOutputBuffers();
      Logging.d("MediaCodecVideoDecoder", "Decoder output buffers changed: " + this.outputBuffers.length);
      if (!this.hasDecodedFirstFrame)
        break;
      throw new RuntimeException("Unexpected output buffer change event.");
      localObject = this.mediaCodec.getOutputFormat();
      Logging.d("MediaCodecVideoDecoder", "Decoder format changed: " + ((MediaFormat)localObject).toString());
      i = ((MediaFormat)localObject).getInteger("width");
      int j = ((MediaFormat)localObject).getInteger("height");
      if ((this.hasDecodedFirstFrame) && ((i != this.width) || (j != this.height)))
        throw new RuntimeException("Unexpected size change. Configured " + this.width + "*" + this.height + ". New " + i + "*" + j);
      this.width = ((MediaFormat)localObject).getInteger("width");
      this.height = ((MediaFormat)localObject).getInteger("height");
      if ((!this.useSurface) && (((MediaFormat)localObject).containsKey("color-format")))
      {
        this.colorFormat = ((MediaFormat)localObject).getInteger("color-format");
        Logging.d("MediaCodecVideoDecoder", "Color: 0x" + Integer.toHexString(this.colorFormat));
        if (!supportedColorList.contains(Integer.valueOf(this.colorFormat)))
          throw new IllegalStateException("Non supported color format: " + this.colorFormat);
      }
      if (((MediaFormat)localObject).containsKey("stride"))
        this.stride = ((MediaFormat)localObject).getInteger("stride");
      if (((MediaFormat)localObject).containsKey("slice-height"))
        this.sliceHeight = ((MediaFormat)localObject).getInteger("slice-height");
      Logging.d("MediaCodecVideoDecoder", "Frame stride and slice height: " + this.stride + " x " + this.sliceHeight);
      this.stride = Math.max(this.width, this.stride);
      this.sliceHeight = Math.max(this.height, this.sliceHeight);
      break;
      l1 = l2;
    }
  }

  private DecodedTextureBuffer dequeueTextureBuffer(int paramInt)
  {
    checkOnMediaCodecThread();
    if (!this.useSurface)
      throw new IllegalStateException("dequeueTexture() called for byte buffer decoding.");
    Object localObject = dequeueOutputBuffer(paramInt);
    if (localObject != null)
      this.dequeuedSurfaceOutputBuffers.add(localObject);
    MaybeRenderDecodedTextureBuffer();
    localObject = this.textureListener.dequeueTextureBuffer(paramInt);
    if (localObject != null)
    {
      MaybeRenderDecodedTextureBuffer();
      return localObject;
    }
    if ((this.dequeuedSurfaceOutputBuffers.size() >= Math.min(3, this.outputBuffers.length)) || ((paramInt > 0) && (!this.dequeuedSurfaceOutputBuffers.isEmpty())))
    {
      this.droppedFrames += 1;
      localObject = (DecodedOutputBuffer)this.dequeuedSurfaceOutputBuffers.remove();
      if (paramInt > 0)
        Logging.w("MediaCodecVideoDecoder", "Draining decoder. Dropping frame with TS: " + ((DecodedOutputBuffer)localObject).presentationTimeStampMs + ". Total number of dropped frames: " + this.droppedFrames);
      while (true)
      {
        this.mediaCodec.releaseOutputBuffer(((DecodedOutputBuffer)localObject).index, false);
        return new DecodedTextureBuffer(0, null, ((DecodedOutputBuffer)localObject).presentationTimeStampMs, ((DecodedOutputBuffer)localObject).timeStampMs, ((DecodedOutputBuffer)localObject).ntpTimeStampMs, ((DecodedOutputBuffer)localObject).decodeTimeMs, SystemClock.elapsedRealtime() - ((DecodedOutputBuffer)localObject).endDecodeTimeMs);
        Logging.w("MediaCodecVideoDecoder", "Too many output buffers " + this.dequeuedSurfaceOutputBuffers.size() + ". Dropping frame with TS: " + ((DecodedOutputBuffer)localObject).presentationTimeStampMs + ". Total number of dropped frames: " + this.droppedFrames);
      }
    }
    return (DecodedTextureBuffer)null;
  }

  public static void disableH264HwCodec()
  {
    Logging.w("MediaCodecVideoDecoder", "H.264 decoding is disabled by application.");
    hwDecoderDisabledTypes.add("video/avc");
  }

  public static void disableVp8HwCodec()
  {
    Logging.w("MediaCodecVideoDecoder", "VP8 decoding is disabled by application.");
    hwDecoderDisabledTypes.add("video/x-vnd.on2.vp8");
  }

  public static void disableVp9HwCodec()
  {
    Logging.w("MediaCodecVideoDecoder", "VP9 decoding is disabled by application.");
    hwDecoderDisabledTypes.add("video/x-vnd.on2.vp9");
  }

  private static DecoderProperties findDecoder(String paramString, String[] paramArrayOfString)
  {
    if (Build.VERSION.SDK_INT < 19)
      return null;
    Logging.d("MediaCodecVideoDecoder", "Trying to find HW decoder for mime " + paramString);
    int i = 0;
    Object localObject2;
    if (i < MediaCodecList.getCodecCount())
    {
      localObject2 = MediaCodecList.getCodecInfoAt(i);
      if (!((MediaCodecInfo)localObject2).isEncoder());
    }
    label140: label404: label410: 
    while (true)
    {
      i += 1;
      break;
      Object localObject1 = ((MediaCodecInfo)localObject2).getSupportedTypes();
      int k = localObject1.length;
      int j = 0;
      if (j < k)
        if (!localObject1[j].equals(paramString));
      for (localObject1 = ((MediaCodecInfo)localObject2).getName(); ; localObject1 = null)
      {
        if (localObject1 == null)
          break label410;
        Logging.d("MediaCodecVideoDecoder", "Found candidate decoder " + (String)localObject1);
        k = paramArrayOfString.length;
        j = 0;
        if (j < k)
          if (!((String)localObject1).startsWith(paramArrayOfString[j]));
        for (j = 1; ; j = 0)
        {
          if (j == 0)
            break label404;
          localObject2 = ((MediaCodecInfo)localObject2).getCapabilitiesForType(paramString);
          Object localObject3 = ((MediaCodecInfo.CodecCapabilities)localObject2).colorFormats;
          k = localObject3.length;
          j = 0;
          int m;
          while (true)
            if (j < k)
            {
              m = localObject3[j];
              Logging.v("MediaCodecVideoDecoder", "   Color: 0x" + Integer.toHexString(m));
              j += 1;
              continue;
              j += 1;
              break;
              j += 1;
              break label140;
            }
          localObject3 = supportedColorList.iterator();
          while (((Iterator)localObject3).hasNext())
          {
            k = ((Integer)((Iterator)localObject3).next()).intValue();
            int[] arrayOfInt = ((MediaCodecInfo.CodecCapabilities)localObject2).colorFormats;
            m = arrayOfInt.length;
            j = 0;
            while (j < m)
            {
              int n = arrayOfInt[j];
              if (n == k)
              {
                Logging.d("MediaCodecVideoDecoder", "Found target decoder " + (String)localObject1 + ". Color: 0x" + Integer.toHexString(n));
                return new DecoderProperties((String)localObject1, n);
              }
              j += 1;
            }
          }
          Logging.d("MediaCodecVideoDecoder", "No HW decoder found for mime " + paramString);
          return null;
        }
        break;
      }
    }
  }

  private boolean initDecode(VideoCodecType paramVideoCodecType, int paramInt1, int paramInt2, SurfaceTextureHelper paramSurfaceTextureHelper)
  {
    if (this.mediaCodecThread != null)
      throw new RuntimeException("initDecode: Forgot to release()?");
    boolean bool;
    String str;
    Object localObject;
    if (paramSurfaceTextureHelper != null)
    {
      bool = true;
      this.useSurface = bool;
      if (paramVideoCodecType != VideoCodecType.VIDEO_CODEC_VP8)
        break label96;
      str = "video/x-vnd.on2.vp8";
      localObject = supportedVp8HwCodecPrefixes;
    }
    while (true)
    {
      localObject = findDecoder(str, localObject);
      if (localObject != null)
        break label162;
      throw new RuntimeException("Cannot find HW decoder for " + paramVideoCodecType);
      bool = false;
      break;
      label96: if (paramVideoCodecType == VideoCodecType.VIDEO_CODEC_VP9)
      {
        str = "video/x-vnd.on2.vp9";
        localObject = supportedVp9HwCodecPrefixes;
        continue;
      }
      if (paramVideoCodecType != VideoCodecType.VIDEO_CODEC_H264)
        break label134;
      str = "video/avc";
      localObject = supportedH264HwCodecPrefixes;
    }
    label134: throw new RuntimeException("initDecode: Non-supported codec " + paramVideoCodecType);
    label162: Logging.d("MediaCodecVideoDecoder", "Java initDecode: " + paramVideoCodecType + " : " + paramInt1 + " x " + paramInt2 + ". Color: 0x" + Integer.toHexString(((DecoderProperties)localObject).colorFormat) + ". Use Surface: " + this.useSurface);
    runningInstance = this;
    this.mediaCodecThread = Thread.currentThread();
    try
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.stride = paramInt1;
      this.sliceHeight = paramInt2;
      if (this.useSurface)
      {
        this.textureListener = new TextureListener(paramSurfaceTextureHelper);
        this.surface = new Surface(paramSurfaceTextureHelper.getSurfaceTexture());
      }
      paramVideoCodecType = MediaFormat.createVideoFormat(str, paramInt1, paramInt2);
      if (!this.useSurface)
        paramVideoCodecType.setInteger("color-format", ((DecoderProperties)localObject).colorFormat);
      Logging.d("MediaCodecVideoDecoder", "  Format: " + paramVideoCodecType);
      this.mediaCodec = MediaCodecVideoEncoder.createByCodecName(((DecoderProperties)localObject).codecName);
      if (this.mediaCodec == null)
      {
        Logging.e("MediaCodecVideoDecoder", "Can not create media decoder");
        return false;
      }
      this.mediaCodec.configure(paramVideoCodecType, this.surface, null, 0);
      this.mediaCodec.start();
      this.colorFormat = ((DecoderProperties)localObject).colorFormat;
      this.outputBuffers = this.mediaCodec.getOutputBuffers();
      this.inputBuffers = this.mediaCodec.getInputBuffers();
      this.decodeStartTimeMs.clear();
      this.hasDecodedFirstFrame = false;
      this.dequeuedSurfaceOutputBuffers.clear();
      this.droppedFrames = 0;
      Logging.d("MediaCodecVideoDecoder", "Input buffers: " + this.inputBuffers.length + ". Output buffers: " + this.outputBuffers.length);
      return true;
    }
    catch (IllegalStateException paramVideoCodecType)
    {
      Logging.e("MediaCodecVideoDecoder", "initDecode failed", paramVideoCodecType);
    }
    return false;
  }

  public static boolean isH264HwSupported()
  {
    return (!hwDecoderDisabledTypes.contains("video/avc")) && (findDecoder("video/avc", supportedH264HwCodecPrefixes) != null);
  }

  public static boolean isVp8HwSupported()
  {
    return (!hwDecoderDisabledTypes.contains("video/x-vnd.on2.vp8")) && (findDecoder("video/x-vnd.on2.vp8", supportedVp8HwCodecPrefixes) != null);
  }

  public static boolean isVp9HwSupported()
  {
    return (!hwDecoderDisabledTypes.contains("video/x-vnd.on2.vp9")) && (findDecoder("video/x-vnd.on2.vp9", supportedVp9HwCodecPrefixes) != null);
  }

  public static void printStackTrace()
  {
    if ((runningInstance != null) && (runningInstance.mediaCodecThread != null))
    {
      StackTraceElement[] arrayOfStackTraceElement = runningInstance.mediaCodecThread.getStackTrace();
      if (arrayOfStackTraceElement.length > 0)
      {
        Logging.d("MediaCodecVideoDecoder", "MediaCodecVideoDecoder stacks trace:");
        int j = arrayOfStackTraceElement.length;
        int i = 0;
        while (i < j)
        {
          Logging.d("MediaCodecVideoDecoder", arrayOfStackTraceElement[i].toString());
          i += 1;
        }
      }
    }
  }

  private boolean queueInputBuffer(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3)
  {
    checkOnMediaCodecThread();
    try
    {
      this.inputBuffers[paramInt1].position(0);
      this.inputBuffers[paramInt1].limit(paramInt2);
      this.decodeStartTimeMs.add(new TimeStamps(SystemClock.elapsedRealtime(), paramLong2, paramLong3));
      this.mediaCodec.queueInputBuffer(paramInt1, 0, paramInt2, paramLong1, 0);
      return true;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoDecoder", "decode failed", localIllegalStateException);
    }
    return false;
  }

  private void release()
  {
    Logging.d("MediaCodecVideoDecoder", "Java releaseDecoder. Total number of dropped frames: " + this.droppedFrames);
    checkOnMediaCodecThread();
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    new Thread(new Runnable(localCountDownLatch)
    {
      public void run()
      {
        try
        {
          Logging.d("MediaCodecVideoDecoder", "Java releaseDecoder on release thread");
          MediaCodecVideoDecoder.this.mediaCodec.stop();
          MediaCodecVideoDecoder.this.mediaCodec.release();
          Logging.d("MediaCodecVideoDecoder", "Java releaseDecoder on release thread done");
          this.val$releaseDone.countDown();
          return;
        }
        catch (Exception localException)
        {
          while (true)
            Logging.e("MediaCodecVideoDecoder", "Media decoder release failed", localException);
        }
      }
    }).start();
    if (!ThreadUtils.awaitUninterruptibly(localCountDownLatch, 5000L))
    {
      Logging.e("MediaCodecVideoDecoder", "Media decoder release timeout");
      codecErrors += 1;
      if (errorCallback != null)
      {
        Logging.e("MediaCodecVideoDecoder", "Invoke codec error callback. Errors: " + codecErrors);
        errorCallback.onMediaCodecVideoDecoderCriticalError(codecErrors);
      }
    }
    this.mediaCodec = null;
    this.mediaCodecThread = null;
    runningInstance = null;
    if (this.useSurface)
    {
      this.surface.release();
      this.surface = null;
      this.textureListener.release();
    }
    Logging.d("MediaCodecVideoDecoder", "Java releaseDecoder done");
  }

  private void reset(int paramInt1, int paramInt2)
  {
    if ((this.mediaCodecThread == null) || (this.mediaCodec == null))
      throw new RuntimeException("Incorrect reset call for non-initialized decoder.");
    Logging.d("MediaCodecVideoDecoder", "Java reset: " + paramInt1 + " x " + paramInt2);
    this.mediaCodec.flush();
    this.width = paramInt1;
    this.height = paramInt2;
    this.decodeStartTimeMs.clear();
    this.dequeuedSurfaceOutputBuffers.clear();
    this.hasDecodedFirstFrame = false;
    this.droppedFrames = 0;
  }

  private void returnDecodedOutputBuffer(int paramInt)
  {
    checkOnMediaCodecThread();
    if (this.useSurface)
      throw new IllegalStateException("returnDecodedOutputBuffer() called for surface decoding.");
    this.mediaCodec.releaseOutputBuffer(paramInt, false);
  }

  public static void setErrorCallback(MediaCodecVideoDecoderErrorCallback paramMediaCodecVideoDecoderErrorCallback)
  {
    Logging.d("MediaCodecVideoDecoder", "Set error callback");
    errorCallback = paramMediaCodecVideoDecoderErrorCallback;
  }

  private static class DecodedOutputBuffer
  {
    private final long decodeTimeMs;
    private final long endDecodeTimeMs;
    private final int index;
    private final long ntpTimeStampMs;
    private final int offset;
    private final long presentationTimeStampMs;
    private final int size;
    private final long timeStampMs;

    public DecodedOutputBuffer(int paramInt1, int paramInt2, int paramInt3, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this.index = paramInt1;
      this.offset = paramInt2;
      this.size = paramInt3;
      this.presentationTimeStampMs = paramLong1;
      this.timeStampMs = paramLong2;
      this.ntpTimeStampMs = paramLong3;
      this.decodeTimeMs = paramLong4;
      this.endDecodeTimeMs = paramLong5;
    }
  }

  private static class DecodedTextureBuffer
  {
    private final long decodeTimeMs;
    private final long frameDelayMs;
    private final long ntpTimeStampMs;
    private final long presentationTimeStampMs;
    private final int textureID;
    private final long timeStampMs;
    private final float[] transformMatrix;

    public DecodedTextureBuffer(int paramInt, float[] paramArrayOfFloat, long paramLong1, long paramLong2, long paramLong3, long paramLong4, long paramLong5)
    {
      this.textureID = paramInt;
      this.transformMatrix = paramArrayOfFloat;
      this.presentationTimeStampMs = paramLong1;
      this.timeStampMs = paramLong2;
      this.ntpTimeStampMs = paramLong3;
      this.decodeTimeMs = paramLong4;
      this.frameDelayMs = paramLong5;
    }
  }

  private static class DecoderProperties
  {
    public final String codecName;
    public final int colorFormat;

    public DecoderProperties(String paramString, int paramInt)
    {
      this.codecName = paramString;
      this.colorFormat = paramInt;
    }
  }

  public static abstract interface MediaCodecVideoDecoderErrorCallback
  {
    public abstract void onMediaCodecVideoDecoderCriticalError(int paramInt);
  }

  private static class TextureListener
    implements SurfaceTextureHelper.OnTextureFrameAvailableListener
  {
    private MediaCodecVideoDecoder.DecodedOutputBuffer bufferToRender;
    private final Object newFrameLock = new Object();
    private MediaCodecVideoDecoder.DecodedTextureBuffer renderedBuffer;
    private final SurfaceTextureHelper surfaceTextureHelper;

    public TextureListener(SurfaceTextureHelper paramSurfaceTextureHelper)
    {
      this.surfaceTextureHelper = paramSurfaceTextureHelper;
      paramSurfaceTextureHelper.startListening(this);
    }

    public void addBufferToRender(MediaCodecVideoDecoder.DecodedOutputBuffer paramDecodedOutputBuffer)
    {
      if (this.bufferToRender != null)
      {
        Logging.e("MediaCodecVideoDecoder", "Unexpected addBufferToRender() called while waiting for a texture.");
        throw new IllegalStateException("Waiting for a texture.");
      }
      this.bufferToRender = paramDecodedOutputBuffer;
    }

    public MediaCodecVideoDecoder.DecodedTextureBuffer dequeueTextureBuffer(int paramInt)
    {
      synchronized (this.newFrameLock)
      {
        if ((this.renderedBuffer == null) && (paramInt > 0))
        {
          boolean bool = isWaitingForTexture();
          if (!bool);
        }
        try
        {
          this.newFrameLock.wait(paramInt);
          MediaCodecVideoDecoder.DecodedTextureBuffer localDecodedTextureBuffer = this.renderedBuffer;
          this.renderedBuffer = null;
          return localDecodedTextureBuffer;
        }
        catch (InterruptedException localInterruptedException)
        {
          while (true)
            Thread.currentThread().interrupt();
        }
      }
    }

    public boolean isWaitingForTexture()
    {
      while (true)
      {
        synchronized (this.newFrameLock)
        {
          if (this.bufferToRender != null)
          {
            i = 1;
            return i;
          }
        }
        int i = 0;
      }
    }

    public void onTextureFrameAvailable(int paramInt, float[] paramArrayOfFloat, long paramLong)
    {
      synchronized (this.newFrameLock)
      {
        if (this.renderedBuffer != null)
        {
          Logging.e("MediaCodecVideoDecoder", "Unexpected onTextureFrameAvailable() called while already holding a texture.");
          throw new IllegalStateException("Already holding a texture.");
        }
      }
      this.renderedBuffer = new MediaCodecVideoDecoder.DecodedTextureBuffer(paramInt, paramArrayOfFloat, this.bufferToRender.presentationTimeStampMs, this.bufferToRender.timeStampMs, this.bufferToRender.ntpTimeStampMs, this.bufferToRender.decodeTimeMs, SystemClock.elapsedRealtime() - this.bufferToRender.endDecodeTimeMs);
      this.bufferToRender = null;
      this.newFrameLock.notifyAll();
      monitorexit;
    }

    public void release()
    {
      this.surfaceTextureHelper.stopListening();
      synchronized (this.newFrameLock)
      {
        if (this.renderedBuffer != null)
        {
          this.surfaceTextureHelper.returnTextureFrame();
          this.renderedBuffer = null;
        }
        return;
      }
    }
  }

  private static class TimeStamps
  {
    private final long decodeStartTimeMs;
    private final long ntpTimeStampMs;
    private final long timeStampMs;

    public TimeStamps(long paramLong1, long paramLong2, long paramLong3)
    {
      this.decodeStartTimeMs = paramLong1;
      this.timeStampMs = paramLong2;
      this.ntpTimeStampMs = paramLong3;
    }
  }

  public static enum VideoCodecType
  {
    static
    {
      VIDEO_CODEC_H264 = new VideoCodecType("VIDEO_CODEC_H264", 2);
      $VALUES = new VideoCodecType[] { VIDEO_CODEC_VP8, VIDEO_CODEC_VP9, VIDEO_CODEC_H264 };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.MediaCodecVideoDecoder
 * JD-Core Version:    0.6.0
 */