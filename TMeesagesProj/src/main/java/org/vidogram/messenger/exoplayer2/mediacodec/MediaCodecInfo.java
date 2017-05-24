package org.vidogram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Log;
import android.util.Pair;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;
import org.vidogram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo
{
  public static final String TAG = "MediaCodecInfo";
  public final boolean adaptive;
  private final MediaCodecInfo.CodecCapabilities capabilities;
  private final String mimeType;
  public final String name;

  private MediaCodecInfo(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    this.name = ((String)Assertions.checkNotNull(paramString1));
    this.mimeType = paramString2;
    this.capabilities = paramCodecCapabilities;
    if ((paramCodecCapabilities != null) && (isAdaptive(paramCodecCapabilities)));
    for (boolean bool = true; ; bool = false)
    {
      this.adaptive = bool;
      return;
    }
  }

  private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (Util.SDK_INT >= 19) && (isAdaptiveV19(paramCodecCapabilities));
  }

  @TargetApi(19)
  private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("adaptive-playback");
  }

  private void logAssumedSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "AssumedSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }

  private void logNoSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "NoSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }

  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities);
  }

  public static MediaCodecInfo newPassthroughInstance(String paramString)
  {
    return new MediaCodecInfo(paramString, null, null);
  }

  public MediaCodecInfo.CodecProfileLevel[] getProfileLevels()
  {
    if ((this.capabilities == null) || (this.capabilities.profileLevels == null))
      return new MediaCodecInfo.CodecProfileLevel[0];
    return this.capabilities.profileLevels;
  }

  @TargetApi(21)
  public boolean isAudioChannelCountSupportedV21(int paramInt)
  {
    if (this.capabilities == null)
    {
      logNoSupport("channelCount.caps");
      return false;
    }
    MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
    if (localAudioCapabilities == null)
    {
      logNoSupport("channelCount.aCaps");
      return false;
    }
    if (localAudioCapabilities.getMaxInputChannelCount() < paramInt)
    {
      logNoSupport("channelCount.support, " + paramInt);
      return false;
    }
    return true;
  }

  @TargetApi(21)
  public boolean isAudioSampleRateSupportedV21(int paramInt)
  {
    if (this.capabilities == null)
    {
      logNoSupport("sampleRate.caps");
      return false;
    }
    MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
    if (localAudioCapabilities == null)
    {
      logNoSupport("sampleRate.aCaps");
      return false;
    }
    if (!localAudioCapabilities.isSampleRateSupported(paramInt))
    {
      logNoSupport("sampleRate.support, " + paramInt);
      return false;
    }
    return true;
  }

  public boolean isCodecSupported(String paramString)
  {
    if ((paramString == null) || (this.mimeType == null))
      return true;
    String str = MimeTypes.getMediaMimeType(paramString);
    if (str == null)
      return true;
    if (!this.mimeType.equals(str))
    {
      logNoSupport("codec.mime " + paramString + ", " + str);
      return false;
    }
    Pair localPair = MediaCodecUtil.getCodecProfileAndLevel(paramString);
    if (localPair == null)
      return true;
    MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = getProfileLevels();
    int j = arrayOfCodecProfileLevel.length;
    int i = 0;
    while (i < j)
    {
      MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = arrayOfCodecProfileLevel[i];
      if ((localCodecProfileLevel.profile == ((Integer)localPair.first).intValue()) && (localCodecProfileLevel.level >= ((Integer)localPair.second).intValue()))
        return true;
      i += 1;
    }
    logNoSupport("codec.profileLevel, " + paramString + ", " + str);
    return false;
  }

  @TargetApi(21)
  public boolean isVideoSizeAndRateSupportedV21(int paramInt1, int paramInt2, double paramDouble)
  {
    if (this.capabilities == null)
    {
      logNoSupport("sizeAndRate.caps");
      return false;
    }
    MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
    if (localVideoCapabilities == null)
    {
      logNoSupport("sizeAndRate.vCaps");
      return false;
    }
    if (!localVideoCapabilities.areSizeAndRateSupported(paramInt1, paramInt2, paramDouble))
    {
      if ((paramInt1 >= paramInt2) || (!localVideoCapabilities.areSizeAndRateSupported(paramInt2, paramInt1, paramDouble)))
      {
        logNoSupport("sizeAndRate.support, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
        return false;
      }
      logAssumedSupport("sizeAndRate.rotated, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
    }
    return true;
  }

  @TargetApi(21)
  public boolean isVideoSizeSupportedV21(int paramInt1, int paramInt2)
  {
    if (this.capabilities == null)
    {
      logNoSupport("size.caps");
      return false;
    }
    MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
    if (localVideoCapabilities == null)
    {
      logNoSupport("size.vCaps");
      return false;
    }
    if (!localVideoCapabilities.isSizeSupported(paramInt1, paramInt2))
    {
      if ((paramInt1 >= paramInt2) || (!localVideoCapabilities.isSizeSupported(paramInt2, paramInt1)))
      {
        logNoSupport("size.support, " + paramInt1 + "x" + paramInt2);
        return false;
      }
      logAssumedSupport("size.rotated, " + paramInt1 + "x" + paramInt2);
    }
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.mediacodec.MediaCodecInfo
 * JD-Core Version:    0.6.0
 */