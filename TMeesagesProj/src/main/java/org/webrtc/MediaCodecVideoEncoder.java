package org.webrtc;

import android.annotation.TargetApi;
import android.media.MediaCodec;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.Surface;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@TargetApi(19)
public class MediaCodecVideoEncoder
{
  private static final int BITRATE_ADJUSTMENT_FPS = 30;
  private static final int COLOR_QCOM_FORMATYUV420PackedSemiPlanar32m = 2141391876;
  private static final int DEQUEUE_TIMEOUT = 0;
  private static final String[] H264_HW_EXCEPTION_MODELS;
  private static final String H264_MIME_TYPE = "video/avc";
  private static final int MEDIA_CODEC_RELEASE_TIMEOUT_MS = 5000;
  private static final String TAG = "MediaCodecVideoEncoder";
  private static final int VIDEO_ControlRateConstant = 2;
  private static final String VP8_MIME_TYPE = "video/x-vnd.on2.vp8";
  private static final String VP9_MIME_TYPE = "video/x-vnd.on2.vp9";
  private static int codecErrors;
  private static MediaCodecVideoEncoderErrorCallback errorCallback;
  private static final MediaCodecProperties exynosH264HwProperties;
  private static final MediaCodecProperties exynosVp8HwProperties;
  private static final MediaCodecProperties exynosVp9HwProperties;
  private static final MediaCodecProperties[] h264HwList;
  private static Set<String> hwEncoderDisabledTypes;
  private static final MediaCodecProperties intelVp8HwProperties;
  private static final MediaCodecProperties qcomH264HwProperties;
  private static final MediaCodecProperties qcomVp8HwProperties;
  private static final MediaCodecProperties qcomVp9HwProperties;
  private static MediaCodecVideoEncoder runningInstance = null;
  private static final int[] supportedColorList;
  private static final int[] supportedSurfaceColorList;
  private static final MediaCodecProperties[] vp8HwList;
  private static final MediaCodecProperties[] vp9HwList;
  private boolean bitrateAdjustmentRequired;
  private int colorFormat;
  private ByteBuffer configData = null;
  private GlRectDrawer drawer;
  private EglBase14 eglBase;
  private int height;
  private Surface inputSurface;
  private MediaCodec mediaCodec;
  private Thread mediaCodecThread;
  private ByteBuffer[] outputBuffers;
  private VideoCodecType type;
  private int width;

  static
  {
    errorCallback = null;
    codecErrors = 0;
    hwEncoderDisabledTypes = new HashSet();
    qcomVp8HwProperties = new MediaCodecProperties("OMX.qcom.", 19, false);
    exynosVp8HwProperties = new MediaCodecProperties("OMX.Exynos.", 23, false);
    intelVp8HwProperties = new MediaCodecProperties("OMX.Intel.", 21, false);
    vp8HwList = new MediaCodecProperties[] { qcomVp8HwProperties, exynosVp8HwProperties, intelVp8HwProperties };
    qcomVp9HwProperties = new MediaCodecProperties("OMX.qcom.", 23, false);
    exynosVp9HwProperties = new MediaCodecProperties("OMX.Exynos.", 23, false);
    vp9HwList = new MediaCodecProperties[] { qcomVp9HwProperties, exynosVp9HwProperties };
    qcomH264HwProperties = new MediaCodecProperties("OMX.qcom.", 19, false);
    exynosH264HwProperties = new MediaCodecProperties("OMX.Exynos.", 21, true);
    h264HwList = new MediaCodecProperties[] { qcomH264HwProperties, exynosH264HwProperties };
    H264_HW_EXCEPTION_MODELS = new String[] { "SAMSUNG-SGH-I337", "Nexus 7", "Nexus 4" };
    supportedColorList = new int[] { 19, 21, 2141391872, 2141391876 };
    supportedSurfaceColorList = new int[] { 2130708361 };
  }

  private void checkOnMediaCodecThread()
  {
    if (this.mediaCodecThread.getId() != Thread.currentThread().getId())
      throw new RuntimeException("MediaCodecVideoEncoder previously operated on " + this.mediaCodecThread + " but is now called on " + Thread.currentThread());
  }

  static MediaCodec createByCodecName(String paramString)
  {
    try
    {
      paramString = MediaCodec.createByCodecName(paramString);
      return paramString;
    }
    catch (Exception paramString)
    {
    }
    return null;
  }

  public static void disableH264HwCodec()
  {
    Logging.w("MediaCodecVideoEncoder", "H.264 encoding is disabled by application.");
    hwEncoderDisabledTypes.add("video/avc");
  }

  public static void disableVp8HwCodec()
  {
    Logging.w("MediaCodecVideoEncoder", "VP8 encoding is disabled by application.");
    hwEncoderDisabledTypes.add("video/x-vnd.on2.vp8");
  }

  public static void disableVp9HwCodec()
  {
    Logging.w("MediaCodecVideoEncoder", "VP9 encoding is disabled by application.");
    hwEncoderDisabledTypes.add("video/x-vnd.on2.vp9");
  }

  private static EncoderProperties findHwEncoder(String paramString, MediaCodecProperties[] paramArrayOfMediaCodecProperties, int[] paramArrayOfInt)
  {
    if (Build.VERSION.SDK_INT < 19)
      return null;
    if ((paramString.equals("video/avc")) && (Arrays.asList(H264_HW_EXCEPTION_MODELS).contains(Build.MODEL)))
    {
      Logging.w("MediaCodecVideoEncoder", "Model: " + Build.MODEL + " has black listed H.264 encoder.");
      return null;
    }
    int i = 0;
    Object localObject2;
    if (i < MediaCodecList.getCodecCount())
    {
      localObject2 = MediaCodecList.getCodecInfoAt(i);
      if (((MediaCodecInfo)localObject2).isEncoder());
    }
    label541: label548: label554: 
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
          break label554;
        Logging.v("MediaCodecVideoEncoder", "Found candidate encoder " + (String)localObject1);
        boolean bool = false;
        k = paramArrayOfMediaCodecProperties.length;
        j = 0;
        Object localObject3;
        while (true)
        {
          if (j >= k)
            break label541;
          localObject3 = paramArrayOfMediaCodecProperties[j];
          if (((String)localObject1).startsWith(((MediaCodecProperties)localObject3).codecPrefix))
          {
            if (Build.VERSION.SDK_INT < ((MediaCodecProperties)localObject3).minSdk)
              Logging.w("MediaCodecVideoEncoder", "Codec " + (String)localObject1 + " is disabled due to SDK version " + Build.VERSION.SDK_INT);
          }
          else
          {
            j += 1;
            continue;
            j += 1;
            break;
          }
        }
        if (((MediaCodecProperties)localObject3).bitrateAdjustmentRequired)
        {
          Logging.w("MediaCodecVideoEncoder", "Codec " + (String)localObject1 + " does not use frame timestamps.");
          bool = true;
        }
        for (j = 1; ; j = 0)
        {
          if (j == 0)
            break label548;
          localObject2 = ((MediaCodecInfo)localObject2).getCapabilitiesForType(paramString);
          localObject3 = ((MediaCodecInfo.CodecCapabilities)localObject2).colorFormats;
          k = localObject3.length;
          j = 0;
          while (j < k)
          {
            m = localObject3[j];
            Logging.v("MediaCodecVideoEncoder", "   Color: 0x" + Integer.toHexString(m));
            j += 1;
          }
          int m = paramArrayOfInt.length;
          j = 0;
          while (j < m)
          {
            int n = paramArrayOfInt[j];
            localObject3 = ((MediaCodecInfo.CodecCapabilities)localObject2).colorFormats;
            int i1 = localObject3.length;
            k = 0;
            while (k < i1)
            {
              int i2 = localObject3[k];
              if (i2 == n)
              {
                Logging.d("MediaCodecVideoEncoder", "Found target encoder for mime " + paramString + " : " + (String)localObject1 + ". Color: 0x" + Integer.toHexString(i2));
                return new EncoderProperties((String)localObject1, i2, bool);
              }
              k += 1;
            }
            j += 1;
          }
          break;
          return null;
          bool = false;
        }
        break;
      }
    }
  }

  public static boolean isH264HwSupported()
  {
    return (!hwEncoderDisabledTypes.contains("video/avc")) && (findHwEncoder("video/avc", h264HwList, supportedColorList) != null);
  }

  public static boolean isH264HwSupportedUsingTextures()
  {
    return (!hwEncoderDisabledTypes.contains("video/avc")) && (findHwEncoder("video/avc", h264HwList, supportedSurfaceColorList) != null);
  }

  public static boolean isVp8HwSupported()
  {
    return (!hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp8")) && (findHwEncoder("video/x-vnd.on2.vp8", vp8HwList, supportedColorList) != null);
  }

  public static boolean isVp8HwSupportedUsingTextures()
  {
    return (!hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp8")) && (findHwEncoder("video/x-vnd.on2.vp8", vp8HwList, supportedSurfaceColorList) != null);
  }

  public static boolean isVp9HwSupported()
  {
    return (!hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp9")) && (findHwEncoder("video/x-vnd.on2.vp9", vp9HwList, supportedColorList) != null);
  }

  public static boolean isVp9HwSupportedUsingTextures()
  {
    return (!hwEncoderDisabledTypes.contains("video/x-vnd.on2.vp9")) && (findHwEncoder("video/x-vnd.on2.vp9", vp9HwList, supportedSurfaceColorList) != null);
  }

  public static void printStackTrace()
  {
    if ((runningInstance != null) && (runningInstance.mediaCodecThread != null))
    {
      StackTraceElement[] arrayOfStackTraceElement = runningInstance.mediaCodecThread.getStackTrace();
      if (arrayOfStackTraceElement.length > 0)
      {
        Logging.d("MediaCodecVideoEncoder", "MediaCodecVideoEncoder stacks trace:");
        int j = arrayOfStackTraceElement.length;
        int i = 0;
        while (i < j)
        {
          Logging.d("MediaCodecVideoEncoder", arrayOfStackTraceElement[i].toString());
          i += 1;
        }
      }
    }
  }

  public static void setErrorCallback(MediaCodecVideoEncoderErrorCallback paramMediaCodecVideoEncoderErrorCallback)
  {
    Logging.d("MediaCodecVideoEncoder", "Set error callback");
    errorCallback = paramMediaCodecVideoEncoderErrorCallback;
  }

  private boolean setRates(int paramInt1, int paramInt2)
  {
    checkOnMediaCodecThread();
    int i = paramInt1 * 1000;
    if ((this.bitrateAdjustmentRequired) && (paramInt2 > 0))
    {
      i = i * 30 / paramInt2;
      Logging.v("MediaCodecVideoEncoder", "setRates: " + paramInt1 + " -> " + i / 1000 + " kbps. Fps: " + paramInt2);
      paramInt1 = i;
    }
    try
    {
      while (true)
      {
        Bundle localBundle = new Bundle();
        localBundle.putInt("video-bitrate", paramInt1);
        this.mediaCodec.setParameters(localBundle);
        return true;
        Logging.v("MediaCodecVideoEncoder", "setRates: " + paramInt1);
        paramInt1 = i;
      }
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoEncoder", "setRates failed", localIllegalStateException);
    }
    return false;
  }

  int dequeueInputBuffer()
  {
    checkOnMediaCodecThread();
    try
    {
      int i = this.mediaCodec.dequeueInputBuffer(0L);
      return i;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoEncoder", "dequeueIntputBuffer failed", localIllegalStateException);
    }
    return -2;
  }

  OutputBufferInfo dequeueOutputBuffer()
  {
    boolean bool = true;
    checkOnMediaCodecThread();
    int j;
    int i;
    try
    {
      Object localObject = new MediaCodec.BufferInfo();
      int k = this.mediaCodec.dequeueOutputBuffer((MediaCodec.BufferInfo)localObject, 0L);
      j = k;
      if (k >= 0)
      {
        if ((((MediaCodec.BufferInfo)localObject).flags & 0x2) == 0)
          break label497;
        i = 1;
        j = k;
        if (i != 0)
        {
          Logging.d("MediaCodecVideoEncoder", "Config frame generated. Offset: " + ((MediaCodec.BufferInfo)localObject).offset + ". Size: " + ((MediaCodec.BufferInfo)localObject).size);
          this.configData = ByteBuffer.allocateDirect(((MediaCodec.BufferInfo)localObject).size);
          this.outputBuffers[k].position(((MediaCodec.BufferInfo)localObject).offset);
          this.outputBuffers[k].limit(((MediaCodec.BufferInfo)localObject).offset + ((MediaCodec.BufferInfo)localObject).size);
          this.configData.put(this.outputBuffers[k]);
          this.mediaCodec.releaseOutputBuffer(k, false);
          j = this.mediaCodec.dequeueOutputBuffer((MediaCodec.BufferInfo)localObject, 0L);
        }
      }
      if (j >= 0)
      {
        ByteBuffer localByteBuffer1 = this.outputBuffers[j].duplicate();
        localByteBuffer1.position(((MediaCodec.BufferInfo)localObject).offset);
        localByteBuffer1.limit(((MediaCodec.BufferInfo)localObject).offset + ((MediaCodec.BufferInfo)localObject).size);
        if ((((MediaCodec.BufferInfo)localObject).flags & 0x1) == 0)
          break label502;
        label229: if (bool)
          Logging.d("MediaCodecVideoEncoder", "Sync frame generated");
        if ((bool) && (this.type == VideoCodecType.VIDEO_CODEC_H264))
        {
          Logging.d("MediaCodecVideoEncoder", "Appending config frame of size " + this.configData.capacity() + " to output buffer with offset " + ((MediaCodec.BufferInfo)localObject).offset + ", size " + ((MediaCodec.BufferInfo)localObject).size);
          ByteBuffer localByteBuffer2 = ByteBuffer.allocateDirect(this.configData.capacity() + ((MediaCodec.BufferInfo)localObject).size);
          this.configData.rewind();
          localByteBuffer2.put(this.configData);
          localByteBuffer2.put(localByteBuffer1);
          localByteBuffer2.position(0);
          return new OutputBufferInfo(j, localByteBuffer2, bool, ((MediaCodec.BufferInfo)localObject).presentationTimeUs);
        }
        localObject = new OutputBufferInfo(j, localByteBuffer1.slice(), bool, ((MediaCodec.BufferInfo)localObject).presentationTimeUs);
        return localObject;
      }
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoEncoder", "dequeueOutputBuffer failed", localIllegalStateException);
      return new OutputBufferInfo(-1, null, false, -1L);
    }
    if (j == -3)
    {
      this.outputBuffers = this.mediaCodec.getOutputBuffers();
      return dequeueOutputBuffer();
    }
    if (j == -2)
      return dequeueOutputBuffer();
    label497: label502: 
    do
    {
      throw new RuntimeException("dequeueOutputBuffer: " + j);
      i = 0;
      break;
      bool = false;
      break label229;
    }
    while (j != -1);
    return (OutputBufferInfo)null;
  }

  boolean encodeBuffer(boolean paramBoolean, int paramInt1, int paramInt2, long paramLong)
  {
    checkOnMediaCodecThread();
    if (paramBoolean);
    try
    {
      Logging.d("MediaCodecVideoEncoder", "Sync frame request");
      Bundle localBundle = new Bundle();
      localBundle.putInt("request-sync", 0);
      this.mediaCodec.setParameters(localBundle);
      this.mediaCodec.queueInputBuffer(paramInt1, 0, paramInt2, paramLong, 0);
      return true;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoEncoder", "encodeBuffer failed", localIllegalStateException);
    }
    return false;
  }

  boolean encodeTexture(boolean paramBoolean, int paramInt, float[] paramArrayOfFloat, long paramLong)
  {
    checkOnMediaCodecThread();
    if (paramBoolean);
    try
    {
      Logging.d("MediaCodecVideoEncoder", "Sync frame request");
      Bundle localBundle = new Bundle();
      localBundle.putInt("request-sync", 0);
      this.mediaCodec.setParameters(localBundle);
      this.eglBase.makeCurrent();
      GLES20.glClear(16384);
      this.drawer.drawOes(paramInt, paramArrayOfFloat, this.width, this.height, 0, 0, this.width, this.height);
      this.eglBase.swapBuffers(TimeUnit.MICROSECONDS.toNanos(paramLong));
      return true;
    }
    catch (RuntimeException paramArrayOfFloat)
    {
      Logging.e("MediaCodecVideoEncoder", "encodeTexture failed", paramArrayOfFloat);
    }
    return false;
  }

  ByteBuffer[] getInputBuffers()
  {
    ByteBuffer[] arrayOfByteBuffer = this.mediaCodec.getInputBuffers();
    Logging.d("MediaCodecVideoEncoder", "Input buffers: " + arrayOfByteBuffer.length);
    return arrayOfByteBuffer;
  }

  boolean initEncode(VideoCodecType paramVideoCodecType, int paramInt1, int paramInt2, int paramInt3, int paramInt4, EglBase14.Context paramContext)
  {
    if (paramContext != null);
    for (boolean bool = true; ; bool = false)
    {
      Logging.d("MediaCodecVideoEncoder", "Java initEncode: " + paramVideoCodecType + " : " + paramInt1 + " x " + paramInt2 + ". @ " + paramInt3 + " kbps. Fps: " + paramInt4 + ". Encode from texture : " + bool);
      this.width = paramInt1;
      this.height = paramInt2;
      if (this.mediaCodecThread == null)
        break;
      throw new RuntimeException("Forgot to release()?");
    }
    Object localObject2 = null;
    Object localObject1 = null;
    int i = 0;
    if (paramVideoCodecType == VideoCodecType.VIDEO_CODEC_VP8)
    {
      localObject3 = "video/x-vnd.on2.vp8";
      localObject2 = vp8HwList;
      if (bool)
      {
        localObject1 = supportedSurfaceColorList;
        localObject2 = findHwEncoder("video/x-vnd.on2.vp8", localObject2, localObject1);
        i = 100;
        localObject1 = localObject3;
      }
    }
    do
    {
      if (localObject2 != null)
        break label335;
      throw new RuntimeException("Can not find HW encoder for " + paramVideoCodecType);
      localObject1 = supportedColorList;
      break;
      if (paramVideoCodecType != VideoCodecType.VIDEO_CODEC_VP9)
        continue;
      localObject2 = "video/x-vnd.on2.vp9";
      localObject3 = vp9HwList;
      if (bool);
      for (localObject1 = supportedSurfaceColorList; ; localObject1 = supportedColorList)
      {
        localObject3 = findHwEncoder("video/x-vnd.on2.vp9", localObject3, localObject1);
        i = 100;
        localObject1 = localObject2;
        localObject2 = localObject3;
        break;
      }
    }
    while (paramVideoCodecType != VideoCodecType.VIDEO_CODEC_H264);
    localObject2 = "video/avc";
    Object localObject3 = h264HwList;
    if (bool);
    for (localObject1 = supportedSurfaceColorList; ; localObject1 = supportedColorList)
    {
      localObject3 = findHwEncoder("video/avc", localObject3, localObject1);
      i = 20;
      localObject1 = localObject2;
      localObject2 = localObject3;
      break;
    }
    label335: runningInstance = this;
    this.colorFormat = ((EncoderProperties)localObject2).colorFormat;
    this.bitrateAdjustmentRequired = ((EncoderProperties)localObject2).bitrateAdjustment;
    if (this.bitrateAdjustmentRequired)
      paramInt4 = 30;
    Logging.d("MediaCodecVideoEncoder", "Color format: " + this.colorFormat + ". Bitrate adjustment: " + this.bitrateAdjustmentRequired);
    this.mediaCodecThread = Thread.currentThread();
    try
    {
      localObject1 = MediaFormat.createVideoFormat((String)localObject1, paramInt1, paramInt2);
      ((MediaFormat)localObject1).setInteger("bitrate", paramInt3 * 1000);
      ((MediaFormat)localObject1).setInteger("bitrate-mode", 2);
      ((MediaFormat)localObject1).setInteger("color-format", ((EncoderProperties)localObject2).colorFormat);
      ((MediaFormat)localObject1).setInteger("frame-rate", paramInt4);
      ((MediaFormat)localObject1).setInteger("i-frame-interval", i);
      Logging.d("MediaCodecVideoEncoder", "  Format: " + localObject1);
      this.mediaCodec = createByCodecName(((EncoderProperties)localObject2).codecName);
      this.type = paramVideoCodecType;
      if (this.mediaCodec == null)
      {
        Logging.e("MediaCodecVideoEncoder", "Can not create media encoder");
        return false;
      }
      this.mediaCodec.configure((MediaFormat)localObject1, null, null, 1);
      if (bool)
      {
        this.eglBase = new EglBase14(paramContext, EglBase.CONFIG_RECORDABLE);
        this.inputSurface = this.mediaCodec.createInputSurface();
        this.eglBase.createSurface(this.inputSurface);
        this.drawer = new GlRectDrawer();
      }
      this.mediaCodec.start();
      this.outputBuffers = this.mediaCodec.getOutputBuffers();
      Logging.d("MediaCodecVideoEncoder", "Output buffers: " + this.outputBuffers.length);
      return true;
    }
    catch (IllegalStateException paramVideoCodecType)
    {
      Logging.e("MediaCodecVideoEncoder", "initEncode failed", paramVideoCodecType);
    }
    return false;
  }

  void release()
  {
    Logging.d("MediaCodecVideoEncoder", "Java releaseEncoder");
    checkOnMediaCodecThread();
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    new Thread(new Runnable(localCountDownLatch)
    {
      public void run()
      {
        try
        {
          Logging.d("MediaCodecVideoEncoder", "Java releaseEncoder on release thread");
          MediaCodecVideoEncoder.this.mediaCodec.stop();
          MediaCodecVideoEncoder.this.mediaCodec.release();
          Logging.d("MediaCodecVideoEncoder", "Java releaseEncoder on release thread done");
          this.val$releaseDone.countDown();
          return;
        }
        catch (Exception localException)
        {
          while (true)
            Logging.e("MediaCodecVideoEncoder", "Media encoder release failed", localException);
        }
      }
    }).start();
    if (!ThreadUtils.awaitUninterruptibly(localCountDownLatch, 5000L))
    {
      Logging.e("MediaCodecVideoEncoder", "Media encoder release timeout");
      codecErrors += 1;
      if (errorCallback != null)
      {
        Logging.e("MediaCodecVideoEncoder", "Invoke codec error callback. Errors: " + codecErrors);
        errorCallback.onMediaCodecVideoEncoderCriticalError(codecErrors);
      }
    }
    this.mediaCodec = null;
    this.mediaCodecThread = null;
    if (this.drawer != null)
    {
      this.drawer.release();
      this.drawer = null;
    }
    if (this.eglBase != null)
    {
      this.eglBase.release();
      this.eglBase = null;
    }
    if (this.inputSurface != null)
    {
      this.inputSurface.release();
      this.inputSurface = null;
    }
    runningInstance = null;
    Logging.d("MediaCodecVideoEncoder", "Java releaseEncoder done");
  }

  boolean releaseOutputBuffer(int paramInt)
  {
    checkOnMediaCodecThread();
    try
    {
      this.mediaCodec.releaseOutputBuffer(paramInt, false);
      return true;
    }
    catch (IllegalStateException localIllegalStateException)
    {
      Logging.e("MediaCodecVideoEncoder", "releaseOutputBuffer failed", localIllegalStateException);
    }
    return false;
  }

  private static class EncoderProperties
  {
    public final boolean bitrateAdjustment;
    public final String codecName;
    public final int colorFormat;

    public EncoderProperties(String paramString, int paramInt, boolean paramBoolean)
    {
      this.codecName = paramString;
      this.colorFormat = paramInt;
      this.bitrateAdjustment = paramBoolean;
    }
  }

  private static class MediaCodecProperties
  {
    public final boolean bitrateAdjustmentRequired;
    public final String codecPrefix;
    public final int minSdk;

    MediaCodecProperties(String paramString, int paramInt, boolean paramBoolean)
    {
      this.codecPrefix = paramString;
      this.minSdk = paramInt;
      this.bitrateAdjustmentRequired = paramBoolean;
    }
  }

  public static abstract interface MediaCodecVideoEncoderErrorCallback
  {
    public abstract void onMediaCodecVideoEncoderCriticalError(int paramInt);
  }

  static class OutputBufferInfo
  {
    public final ByteBuffer buffer;
    public final int index;
    public final boolean isKeyFrame;
    public final long presentationTimestampUs;

    public OutputBufferInfo(int paramInt, ByteBuffer paramByteBuffer, boolean paramBoolean, long paramLong)
    {
      this.index = paramInt;
      this.buffer = paramByteBuffer;
      this.isKeyFrame = paramBoolean;
      this.presentationTimestampUs = paramLong;
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
 * Qualified Name:     org.webrtc.MediaCodecVideoEncoder
 * JD-Core Version:    0.6.0
 */