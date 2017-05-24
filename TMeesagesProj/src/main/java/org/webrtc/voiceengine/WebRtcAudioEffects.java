package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.AudioEffect.Descriptor;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Build;
import java.util.List;
import java.util.UUID;
import org.webrtc.Logging;

class WebRtcAudioEffects
{
  private static final UUID AOSP_ACOUSTIC_ECHO_CANCELER = UUID.fromString("bb392ec0-8d4d-11e0-a896-0002a5d5c51b");
  private static final UUID AOSP_AUTOMATIC_GAIN_CONTROL = UUID.fromString("aa8130e0-66fc-11e0-bad0-0002a5d5c51b");
  private static final UUID AOSP_NOISE_SUPPRESSOR = UUID.fromString("c06c8400-8e06-11e0-9cb6-0002a5d5c51b");
  private static final boolean DEBUG = false;
  private static final String TAG = "WebRtcAudioEffects";
  private static AudioEffect.Descriptor[] cachedEffects = null;
  private AcousticEchoCanceler aec = null;
  private AutomaticGainControl agc = null;
  private NoiseSuppressor ns = null;
  private boolean shouldEnableAec = false;
  private boolean shouldEnableAgc = false;
  private boolean shouldEnableNs = false;

  private WebRtcAudioEffects()
  {
    Logging.d("WebRtcAudioEffects", "ctor" + WebRtcAudioUtils.getThreadInfo());
  }

  private static void assertTrue(boolean paramBoolean)
  {
    if (!paramBoolean)
      throw new AssertionError("Expected condition to be true");
  }

  public static boolean canUseAcousticEchoCanceler()
  {
    if ((isAcousticEchoCancelerSupported()) && (!WebRtcAudioUtils.useWebRtcBasedAcousticEchoCanceler()) && (!isAcousticEchoCancelerBlacklisted()) && (!isAcousticEchoCancelerExcludedByUUID()));
    for (boolean bool = true; ; bool = false)
    {
      Logging.d("WebRtcAudioEffects", "canUseAcousticEchoCanceler: " + bool);
      return bool;
    }
  }

  public static boolean canUseAutomaticGainControl()
  {
    if ((isAutomaticGainControlSupported()) && (!WebRtcAudioUtils.useWebRtcBasedAutomaticGainControl()) && (!isAutomaticGainControlBlacklisted()) && (!isAutomaticGainControlExcludedByUUID()));
    for (boolean bool = true; ; bool = false)
    {
      Logging.d("WebRtcAudioEffects", "canUseAutomaticGainControl: " + bool);
      return bool;
    }
  }

  public static boolean canUseNoiseSuppressor()
  {
    if ((isNoiseSuppressorSupported()) && (!WebRtcAudioUtils.useWebRtcBasedNoiseSuppressor()) && (!isNoiseSuppressorBlacklisted()) && (!isNoiseSuppressorExcludedByUUID()));
    for (boolean bool = true; ; bool = false)
    {
      Logging.d("WebRtcAudioEffects", "canUseNoiseSuppressor: " + bool);
      return bool;
    }
  }

  static WebRtcAudioEffects create()
  {
    if (!WebRtcAudioUtils.runningOnJellyBeanOrHigher())
    {
      Logging.w("WebRtcAudioEffects", "API level 16 or higher is required!");
      return null;
    }
    return new WebRtcAudioEffects();
  }

  @TargetApi(18)
  private boolean effectTypeIsVoIP(UUID paramUUID)
  {
    if (!WebRtcAudioUtils.runningOnJellyBeanMR2OrHigher());
    do
      return false;
    while (((!AudioEffect.EFFECT_TYPE_AEC.equals(paramUUID)) || (!isAcousticEchoCancelerSupported())) && ((!AudioEffect.EFFECT_TYPE_AGC.equals(paramUUID)) || (!isAutomaticGainControlSupported())) && ((!AudioEffect.EFFECT_TYPE_NS.equals(paramUUID)) || (!isNoiseSuppressorSupported())));
    return true;
  }

  private static AudioEffect.Descriptor[] getAvailableEffects()
  {
    if (cachedEffects != null)
      return cachedEffects;
    cachedEffects = AudioEffect.queryEffects();
    return cachedEffects;
  }

  public static boolean isAcousticEchoCancelerBlacklisted()
  {
    boolean bool = WebRtcAudioUtils.getBlackListedModelsForAecUsage().contains(Build.MODEL);
    if (bool)
      Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW AEC usage!");
    return bool;
  }

  @TargetApi(18)
  private static boolean isAcousticEchoCancelerEffectAvailable()
  {
    return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AEC);
  }

  @TargetApi(18)
  private static boolean isAcousticEchoCancelerExcludedByUUID()
  {
    int m = 0;
    AudioEffect.Descriptor[] arrayOfDescriptor = getAvailableEffects();
    int j = arrayOfDescriptor.length;
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        AudioEffect.Descriptor localDescriptor = arrayOfDescriptor[i];
        if ((localDescriptor.type.equals(AudioEffect.EFFECT_TYPE_AEC)) && (localDescriptor.uuid.equals(AOSP_ACOUSTIC_ECHO_CANCELER)))
          k = 1;
      }
      else
      {
        return k;
      }
      i += 1;
    }
  }

  public static boolean isAcousticEchoCancelerSupported()
  {
    return (WebRtcAudioUtils.runningOnJellyBeanOrHigher()) && (isAcousticEchoCancelerEffectAvailable());
  }

  public static boolean isAutomaticGainControlBlacklisted()
  {
    boolean bool = WebRtcAudioUtils.getBlackListedModelsForAgcUsage().contains(Build.MODEL);
    if (bool)
      Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW AGC usage!");
    return bool;
  }

  @TargetApi(18)
  private static boolean isAutomaticGainControlEffectAvailable()
  {
    return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_AGC);
  }

  @TargetApi(18)
  private static boolean isAutomaticGainControlExcludedByUUID()
  {
    int m = 0;
    AudioEffect.Descriptor[] arrayOfDescriptor = getAvailableEffects();
    int j = arrayOfDescriptor.length;
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        AudioEffect.Descriptor localDescriptor = arrayOfDescriptor[i];
        if ((localDescriptor.type.equals(AudioEffect.EFFECT_TYPE_AGC)) && (localDescriptor.uuid.equals(AOSP_AUTOMATIC_GAIN_CONTROL)))
          k = 1;
      }
      else
      {
        return k;
      }
      i += 1;
    }
  }

  public static boolean isAutomaticGainControlSupported()
  {
    return (WebRtcAudioUtils.runningOnJellyBeanOrHigher()) && (isAutomaticGainControlEffectAvailable());
  }

  private static boolean isEffectTypeAvailable(UUID paramUUID)
  {
    AudioEffect.Descriptor[] arrayOfDescriptor = getAvailableEffects();
    if (arrayOfDescriptor == null);
    while (true)
    {
      return false;
      int j = arrayOfDescriptor.length;
      int i = 0;
      while (i < j)
      {
        if (arrayOfDescriptor[i].type.equals(paramUUID))
          return true;
        i += 1;
      }
    }
  }

  public static boolean isNoiseSuppressorBlacklisted()
  {
    boolean bool = WebRtcAudioUtils.getBlackListedModelsForNsUsage().contains(Build.MODEL);
    if (bool)
      Logging.w("WebRtcAudioEffects", Build.MODEL + " is blacklisted for HW NS usage!");
    return bool;
  }

  @TargetApi(18)
  private static boolean isNoiseSuppressorEffectAvailable()
  {
    return isEffectTypeAvailable(AudioEffect.EFFECT_TYPE_NS);
  }

  @TargetApi(18)
  private static boolean isNoiseSuppressorExcludedByUUID()
  {
    int m = 0;
    AudioEffect.Descriptor[] arrayOfDescriptor = getAvailableEffects();
    int j = arrayOfDescriptor.length;
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        AudioEffect.Descriptor localDescriptor = arrayOfDescriptor[i];
        if ((localDescriptor.type.equals(AudioEffect.EFFECT_TYPE_NS)) && (localDescriptor.uuid.equals(AOSP_NOISE_SUPPRESSOR)))
          k = 1;
      }
      else
      {
        return k;
      }
      i += 1;
    }
  }

  public static boolean isNoiseSuppressorSupported()
  {
    return (WebRtcAudioUtils.runningOnJellyBeanOrHigher()) && (isNoiseSuppressorEffectAvailable());
  }

  public void enable(int paramInt)
  {
    boolean bool2 = true;
    Logging.d("WebRtcAudioEffects", "enable(audioSession=" + paramInt + ")");
    boolean bool1;
    label57: label72: int i;
    label88: StringBuilder localStringBuilder;
    if (this.aec == null)
    {
      bool1 = true;
      assertTrue(bool1);
      if (this.agc != null)
        break label124;
      bool1 = true;
      assertTrue(bool1);
      if (this.ns != null)
        break label130;
      bool1 = true;
      assertTrue(bool1);
      localObject = AudioEffect.queryEffects();
      int j = localObject.length;
      i = 0;
      if (i >= j)
        break label224;
      localStringBuilder = localObject[i];
      if (effectTypeIsVoIP(localStringBuilder.type))
        break label136;
    }
    while (true)
    {
      i += 1;
      break label88;
      bool1 = false;
      break;
      label124: bool1 = false;
      break label57;
      label130: bool1 = false;
      break label72;
      label136: Logging.d("WebRtcAudioEffects", "name: " + localStringBuilder.name + ", " + "mode: " + localStringBuilder.connectMode + ", " + "implementor: " + localStringBuilder.implementor + ", " + "UUID: " + localStringBuilder.uuid);
    }
    label224: boolean bool3;
    label417: label565: if (isAcousticEchoCancelerSupported())
    {
      this.aec = AcousticEchoCanceler.create(paramInt);
      if (this.aec == null)
        break label689;
      bool3 = this.aec.getEnabled();
      if ((this.shouldEnableAec) && (canUseAcousticEchoCanceler()))
      {
        bool1 = true;
        if (this.aec.setEnabled(bool1) != 0)
          Logging.e("WebRtcAudioEffects", "Failed to set the AcousticEchoCanceler state");
        localStringBuilder = new StringBuilder().append("AcousticEchoCanceler: was ");
        if (!bool3)
          break label673;
        localObject = "enabled";
        label315: localStringBuilder = localStringBuilder.append((String)localObject).append(", enable: ").append(bool1).append(", is now: ");
        if (!this.aec.getEnabled())
          break label681;
        localObject = "enabled";
        label356: Logging.d("WebRtcAudioEffects", (String)localObject);
      }
    }
    else
    {
      label371: if (isAutomaticGainControlSupported())
      {
        this.agc = AutomaticGainControl.create(paramInt);
        if (this.agc == null)
          break label722;
        bool3 = this.agc.getEnabled();
        if ((!this.shouldEnableAgc) || (!canUseAutomaticGainControl()))
          break label700;
        bool1 = true;
        if (this.agc.setEnabled(bool1) != 0)
          Logging.e("WebRtcAudioEffects", "Failed to set the AutomaticGainControl state");
        localStringBuilder = new StringBuilder().append("AutomaticGainControl: was ");
        if (!bool3)
          break label706;
        localObject = "enabled";
        localStringBuilder = localStringBuilder.append((String)localObject).append(", enable: ").append(bool1).append(", is now: ");
        if (!this.agc.getEnabled())
          break label714;
        localObject = "enabled";
        label503: Logging.d("WebRtcAudioEffects", (String)localObject);
      }
      label462: if (isNoiseSuppressorSupported())
      {
        this.ns = NoiseSuppressor.create(paramInt);
        if (this.ns == null)
          break label755;
        bool3 = this.ns.getEnabled();
        if ((!this.shouldEnableNs) || (!canUseNoiseSuppressor()))
          break label733;
        bool1 = bool2;
        if (this.ns.setEnabled(bool1) != 0)
          Logging.e("WebRtcAudioEffects", "Failed to set the NoiseSuppressor state");
        localStringBuilder = new StringBuilder().append("NoiseSuppressor: was ");
        if (!bool3)
          break label739;
        localObject = "enabled";
        label610: localStringBuilder = localStringBuilder.append((String)localObject).append(", enable: ").append(bool1).append(", is now: ");
        if (!this.ns.getEnabled())
          break label747;
      }
    }
    label518: label673: label681: label689: label700: label706: label714: label722: label733: label739: label747: for (Object localObject = "enabled"; ; localObject = "disabled")
    {
      Logging.d("WebRtcAudioEffects", (String)localObject);
      return;
      bool1 = false;
      break;
      localObject = "disabled";
      break label315;
      localObject = "disabled";
      break label356;
      Logging.e("WebRtcAudioEffects", "Failed to create the AcousticEchoCanceler instance");
      break label371;
      bool1 = false;
      break label417;
      localObject = "disabled";
      break label462;
      localObject = "disabled";
      break label503;
      Logging.e("WebRtcAudioEffects", "Failed to create the AutomaticGainControl instance");
      break label518;
      bool1 = false;
      break label565;
      localObject = "disabled";
      break label610;
    }
    label755: Logging.e("WebRtcAudioEffects", "Failed to create the NoiseSuppressor instance");
  }

  public void release()
  {
    Logging.d("WebRtcAudioEffects", "release");
    if (this.aec != null)
    {
      this.aec.release();
      this.aec = null;
    }
    if (this.agc != null)
    {
      this.agc.release();
      this.agc = null;
    }
    if (this.ns != null)
    {
      this.ns.release();
      this.ns = null;
    }
  }

  public boolean setAEC(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioEffects", "setAEC(" + paramBoolean + ")");
    if (!canUseAcousticEchoCanceler())
    {
      Logging.w("WebRtcAudioEffects", "Platform AEC is not supported");
      this.shouldEnableAec = false;
      return false;
    }
    if ((this.aec != null) && (paramBoolean != this.shouldEnableAec))
    {
      Logging.e("WebRtcAudioEffects", "Platform AEC state can't be modified while recording");
      return false;
    }
    this.shouldEnableAec = paramBoolean;
    return true;
  }

  public boolean setAGC(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioEffects", "setAGC(" + paramBoolean + ")");
    if (!canUseAutomaticGainControl())
    {
      Logging.w("WebRtcAudioEffects", "Platform AGC is not supported");
      this.shouldEnableAgc = false;
      return false;
    }
    if ((this.agc != null) && (paramBoolean != this.shouldEnableAgc))
    {
      Logging.e("WebRtcAudioEffects", "Platform AGC state can't be modified while recording");
      return false;
    }
    this.shouldEnableAgc = paramBoolean;
    return true;
  }

  public boolean setNS(boolean paramBoolean)
  {
    Logging.d("WebRtcAudioEffects", "setNS(" + paramBoolean + ")");
    if (!canUseNoiseSuppressor())
    {
      Logging.w("WebRtcAudioEffects", "Platform NS is not supported");
      this.shouldEnableNs = false;
      return false;
    }
    if ((this.ns != null) && (paramBoolean != this.shouldEnableNs))
    {
      Logging.e("WebRtcAudioEffects", "Platform NS state can't be modified while recording");
      return false;
    }
    this.shouldEnableNs = paramBoolean;
    return true;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.voiceengine.WebRtcAudioEffects
 * JD-Core Version:    0.6.0
 */