package org.webrtc.voiceengine;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Process;
import java.util.Arrays;
import java.util.List;
import org.webrtc.Logging;

public final class WebRtcAudioUtils
{
  private static final String[] BLACKLISTED_AEC_MODELS;
  private static final String[] BLACKLISTED_AGC_MODELS;
  private static final String[] BLACKLISTED_NS_MODELS;
  private static final String[] BLACKLISTED_OPEN_SL_ES_MODELS = new String[0];
  private static final int DEFAULT_SAMPLE_RATE_HZ = 16000;
  private static final String TAG = "WebRtcAudioUtils";
  private static int defaultSampleRateHz;
  private static boolean isDefaultSampleRateOverridden;
  private static boolean useWebRtcBasedAcousticEchoCanceler;
  private static boolean useWebRtcBasedAutomaticGainControl;
  private static boolean useWebRtcBasedNoiseSuppressor;

  static
  {
    BLACKLISTED_AEC_MODELS = new String[] { "D6503", "ONE A2005", "MotoG3" };
    BLACKLISTED_AGC_MODELS = new String[] { "Nexus 10", "Nexus 9" };
    BLACKLISTED_NS_MODELS = new String[] { "Nexus 10", "Nexus 9", "ONE A2005" };
    defaultSampleRateHz = 16000;
    isDefaultSampleRateOverridden = false;
    useWebRtcBasedAcousticEchoCanceler = false;
    useWebRtcBasedAutomaticGainControl = false;
    useWebRtcBasedNoiseSuppressor = false;
  }

  public static boolean deviceIsBlacklistedForOpenSLESUsage()
  {
    return Arrays.asList(BLACKLISTED_OPEN_SL_ES_MODELS).contains(Build.MODEL);
  }

  public static List<String> getBlackListedModelsForAecUsage()
  {
    return Arrays.asList(BLACKLISTED_AEC_MODELS);
  }

  public static List<String> getBlackListedModelsForAgcUsage()
  {
    return Arrays.asList(BLACKLISTED_AGC_MODELS);
  }

  public static List<String> getBlackListedModelsForNsUsage()
  {
    return Arrays.asList(BLACKLISTED_NS_MODELS);
  }

  public static int getDefaultSampleRateHz()
  {
    monitorenter;
    try
    {
      int i = defaultSampleRateHz;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static String getThreadInfo()
  {
    return "@[name=" + Thread.currentThread().getName() + ", id=" + Thread.currentThread().getId() + "]";
  }

  public static boolean hasPermission(Context paramContext, String paramString)
  {
    return paramContext.checkPermission(paramString, Process.myPid(), Process.myUid()) == 0;
  }

  public static boolean isAcousticEchoCancelerSupported()
  {
    return WebRtcAudioEffects.canUseAcousticEchoCanceler();
  }

  public static boolean isAutomaticGainControlSupported()
  {
    return WebRtcAudioEffects.canUseAutomaticGainControl();
  }

  public static boolean isDefaultSampleRateOverridden()
  {
    monitorenter;
    try
    {
      boolean bool = isDefaultSampleRateOverridden;
      monitorexit;
      return bool;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static boolean isNoiseSuppressorSupported()
  {
    return WebRtcAudioEffects.canUseNoiseSuppressor();
  }

  public static void logDeviceInfo(String paramString)
  {
    Logging.d(paramString, "Android SDK: " + Build.VERSION.SDK_INT + ", " + "Release: " + Build.VERSION.RELEASE + ", " + "Brand: " + Build.BRAND + ", " + "Device: " + Build.DEVICE + ", " + "Id: " + Build.ID + ", " + "Hardware: " + Build.HARDWARE + ", " + "Manufacturer: " + Build.MANUFACTURER + ", " + "Model: " + Build.MODEL + ", " + "Product: " + Build.PRODUCT);
  }

  public static boolean runningOnEmulator()
  {
    return (Build.HARDWARE.equals("goldfish")) && (Build.BRAND.startsWith("generic_"));
  }

  public static boolean runningOnGingerBreadOrHigher()
  {
    return Build.VERSION.SDK_INT >= 9;
  }

  public static boolean runningOnJellyBeanMR1OrHigher()
  {
    return Build.VERSION.SDK_INT >= 17;
  }

  public static boolean runningOnJellyBeanMR2OrHigher()
  {
    return Build.VERSION.SDK_INT >= 18;
  }

  public static boolean runningOnJellyBeanOrHigher()
  {
    return Build.VERSION.SDK_INT >= 16;
  }

  public static boolean runningOnLollipopOrHigher()
  {
    return Build.VERSION.SDK_INT >= 21;
  }

  public static boolean runningOnMarshmallowOrHigher()
  {
    return Build.VERSION.SDK_INT >= 23;
  }

  public static void setDefaultSampleRateHz(int paramInt)
  {
    monitorenter;
    try
    {
      isDefaultSampleRateOverridden = true;
      defaultSampleRateHz = paramInt;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static void setWebRtcBasedAcousticEchoCanceler(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      useWebRtcBasedAcousticEchoCanceler = paramBoolean;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static void setWebRtcBasedAutomaticGainControl(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      useWebRtcBasedAutomaticGainControl = paramBoolean;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static void setWebRtcBasedNoiseSuppressor(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      useWebRtcBasedNoiseSuppressor = paramBoolean;
      monitorexit;
      return;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public static boolean useWebRtcBasedAcousticEchoCanceler()
  {
    monitorenter;
    try
    {
      if (useWebRtcBasedAcousticEchoCanceler)
        Logging.w("WebRtcAudioUtils", "Overriding default behavior; now using WebRTC AEC!");
      boolean bool = useWebRtcBasedAcousticEchoCanceler;
      return bool;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static boolean useWebRtcBasedAutomaticGainControl()
  {
    monitorenter;
    try
    {
      if (useWebRtcBasedAutomaticGainControl)
        Logging.w("WebRtcAudioUtils", "Overriding default behavior; now using WebRTC AGC!");
      boolean bool = useWebRtcBasedAutomaticGainControl;
      return bool;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static boolean useWebRtcBasedNoiseSuppressor()
  {
    monitorenter;
    try
    {
      if (useWebRtcBasedNoiseSuppressor)
        Logging.w("WebRtcAudioUtils", "Overriding default behavior; now using WebRTC NS!");
      boolean bool = useWebRtcBasedNoiseSuppressor;
      return bool;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.voiceengine.WebRtcAudioUtils
 * JD-Core Version:    0.6.0
 */