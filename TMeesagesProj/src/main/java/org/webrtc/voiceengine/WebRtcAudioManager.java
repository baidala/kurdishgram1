package org.webrtc.voiceengine;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.os.Build;
import java.util.Timer;
import java.util.TimerTask;
import org.webrtc.Logging;

public class WebRtcAudioManager
{
  private static final String[] AUDIO_MODES;
  private static final int BITS_PER_SAMPLE = 16;
  private static final int CHANNELS = 1;
  private static final boolean DEBUG = false;
  private static final int DEFAULT_FRAME_PER_BUFFER = 256;
  private static final String TAG = "WebRtcAudioManager";
  private static boolean blacklistDeviceForOpenSLESUsage = false;
  private static boolean blacklistDeviceForOpenSLESUsageIsOverridden = false;
  private final AudioManager audioManager;
  private int channels;
  private final Context context;
  private boolean hardwareAEC;
  private boolean hardwareAGC;
  private boolean hardwareNS;
  private boolean initialized = false;
  private int inputBufferSize;
  private boolean lowLatencyOutput;
  private final long nativeAudioManager;
  private int nativeChannels;
  private int nativeSampleRate;
  private int outputBufferSize;
  private boolean proAudio;
  private int sampleRate;
  private final VolumeLogger volumeLogger;

  static
  {
    AUDIO_MODES = new String[] { "MODE_NORMAL", "MODE_RINGTONE", "MODE_IN_CALL", "MODE_IN_COMMUNICATION" };
  }

  WebRtcAudioManager(Context paramContext, long paramLong)
  {
    Logging.d("WebRtcAudioManager", "ctor" + WebRtcAudioUtils.getThreadInfo());
    this.context = paramContext;
    this.nativeAudioManager = paramLong;
    this.audioManager = ((AudioManager)paramContext.getSystemService("audio"));
    this.volumeLogger = new VolumeLogger(this.audioManager);
    storeAudioParameters();
    nativeCacheAudioParameters(this.sampleRate, this.channels, this.hardwareAEC, this.hardwareAGC, this.hardwareNS, this.lowLatencyOutput, this.proAudio, this.outputBufferSize, this.inputBufferSize, paramLong);
  }

  private static void assertTrue(boolean paramBoolean)
  {
    if (!paramBoolean)
      throw new AssertionError("Expected condition to be true");
  }

  private void dispose()
  {
    Logging.d("WebRtcAudioManager", "dispose" + WebRtcAudioUtils.getThreadInfo());
    if (!this.initialized)
      return;
    this.volumeLogger.stop();
  }

  private int getLowLatencyInputFramesPerBuffer()
  {
    assertTrue(isLowLatencyInputSupported());
    return getLowLatencyOutputFramesPerBuffer();
  }

  @TargetApi(17)
  private int getLowLatencyOutputFramesPerBuffer()
  {
    assertTrue(isLowLatencyOutputSupported());
    if (!WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher());
    String str;
    do
    {
      return 256;
      str = this.audioManager.getProperty("android.media.property.OUTPUT_FRAMES_PER_BUFFER");
    }
    while (str == null);
    return Integer.parseInt(str);
  }

  private static int getMinInputFrameSize(int paramInt1, int paramInt2)
  {
    boolean bool = true;
    if (paramInt2 == 1);
    while (true)
    {
      assertTrue(bool);
      return AudioRecord.getMinBufferSize(paramInt1, 16, 2) / (paramInt2 * 2);
      bool = false;
    }
  }

  private static int getMinOutputFrameSize(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1);
    for (int i = 4; ; i = 12)
    {
      return AudioTrack.getMinBufferSize(paramInt1, i, 2) / (paramInt2 * 2);
      if (paramInt2 != 2)
        break;
    }
    return -1;
  }

  private int getNativeOutputSampleRate()
  {
    if (WebRtcAudioUtils.runningOnEmulator())
    {
      Logging.d("WebRtcAudioManager", "Running emulator, overriding sample rate to 8 kHz.");
      return 8000;
    }
    if (WebRtcAudioUtils.isDefaultSampleRateOverridden())
    {
      Logging.d("WebRtcAudioManager", "Default sample rate is overriden to " + WebRtcAudioUtils.getDefaultSampleRateHz() + " Hz");
      return WebRtcAudioUtils.getDefaultSampleRateHz();
    }
    if (WebRtcAudioUtils.runningOnJellyBeanMR1OrHigher());
    for (int i = getSampleRateOnJellyBeanMR10OrHigher(); ; i = WebRtcAudioUtils.getDefaultSampleRateHz())
    {
      Logging.d("WebRtcAudioManager", "Sample rate is set to " + i + " Hz");
      return i;
    }
  }

  @TargetApi(17)
  private int getSampleRateOnJellyBeanMR10OrHigher()
  {
    String str = this.audioManager.getProperty("android.media.property.OUTPUT_SAMPLE_RATE");
    if (str == null)
      return WebRtcAudioUtils.getDefaultSampleRateHz();
    return Integer.parseInt(str);
  }

  private boolean hasEarpiece()
  {
    return this.context.getPackageManager().hasSystemFeature("android.hardware.telephony");
  }

  private boolean init()
  {
    Logging.d("WebRtcAudioManager", "init" + WebRtcAudioUtils.getThreadInfo());
    if (this.initialized)
      return true;
    Logging.d("WebRtcAudioManager", "audio mode is: " + AUDIO_MODES[this.audioManager.getMode()]);
    this.initialized = true;
    this.volumeLogger.start();
    return true;
  }

  private static boolean isAcousticEchoCancelerSupported()
  {
    return WebRtcAudioEffects.canUseAcousticEchoCanceler();
  }

  private static boolean isAutomaticGainControlSupported()
  {
    return WebRtcAudioEffects.canUseAutomaticGainControl();
  }

  private boolean isCommunicationModeEnabled()
  {
    return this.audioManager.getMode() == 3;
  }

  private boolean isDeviceBlacklistedForOpenSLESUsage()
  {
    if (blacklistDeviceForOpenSLESUsageIsOverridden);
    for (boolean bool = blacklistDeviceForOpenSLESUsage; ; bool = WebRtcAudioUtils.deviceIsBlacklistedForOpenSLESUsage())
    {
      if (bool)
        Logging.e("WebRtcAudioManager", Build.MODEL + " is blacklisted for OpenSL ES usage!");
      return bool;
    }
  }

  private boolean isLowLatencyOutputSupported()
  {
    return (isOpenSLESSupported()) && (this.context.getPackageManager().hasSystemFeature("android.hardware.audio.low_latency"));
  }

  private static boolean isNoiseSuppressorSupported()
  {
    return WebRtcAudioEffects.canUseNoiseSuppressor();
  }

  private static boolean isOpenSLESSupported()
  {
    return WebRtcAudioUtils.runningOnGingerBreadOrHigher();
  }

  private boolean isProAudioSupported()
  {
    return (WebRtcAudioUtils.runningOnMarshmallowOrHigher()) && (this.context.getPackageManager().hasSystemFeature("android.hardware.audio.pro"));
  }

  private native void nativeCacheAudioParameters(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, boolean paramBoolean5, int paramInt3, int paramInt4, long paramLong);

  public static void setBlacklistDeviceForOpenSLESUsage(boolean paramBoolean)
  {
    monitorenter;
    try
    {
      blacklistDeviceForOpenSLESUsageIsOverridden = true;
      blacklistDeviceForOpenSLESUsage = paramBoolean;
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

  private void storeAudioParameters()
  {
    this.channels = 1;
    this.sampleRate = getNativeOutputSampleRate();
    this.hardwareAEC = isAcousticEchoCancelerSupported();
    this.hardwareAGC = isAutomaticGainControlSupported();
    this.hardwareNS = isNoiseSuppressorSupported();
    this.lowLatencyOutput = isLowLatencyOutputSupported();
    this.proAudio = isProAudioSupported();
    if (this.lowLatencyOutput);
    for (int i = getLowLatencyOutputFramesPerBuffer(); ; i = getMinOutputFrameSize(this.sampleRate, this.channels))
    {
      this.outputBufferSize = i;
      this.inputBufferSize = getMinInputFrameSize(this.sampleRate, this.channels);
      return;
    }
  }

  public boolean isLowLatencyInputSupported()
  {
    return (WebRtcAudioUtils.runningOnLollipopOrHigher()) && (isLowLatencyOutputSupported());
  }

  private static class VolumeLogger
  {
    private static final String THREAD_NAME = "WebRtcVolumeLevelLoggerThread";
    private static final int TIMER_PERIOD_IN_SECONDS = 10;
    private final AudioManager audioManager;
    private Timer timer;

    public VolumeLogger(AudioManager paramAudioManager)
    {
      this.audioManager = paramAudioManager;
    }

    private void stop()
    {
      if (this.timer != null)
      {
        this.timer.cancel();
        this.timer = null;
      }
    }

    public void start()
    {
      this.timer = new Timer("WebRtcVolumeLevelLoggerThread");
      this.timer.schedule(new LogVolumeTask(this.audioManager.getStreamMaxVolume(2), this.audioManager.getStreamMaxVolume(0)), 0L, 10000L);
    }

    private class LogVolumeTask extends TimerTask
    {
      private final int maxRingVolume;
      private final int maxVoiceCallVolume;

      LogVolumeTask(int paramInt1, int arg3)
      {
        this.maxRingVolume = paramInt1;
        int i;
        this.maxVoiceCallVolume = i;
      }

      public void run()
      {
        int i = WebRtcAudioManager.VolumeLogger.this.audioManager.getMode();
        if (i == 1)
        {
          Logging.d("WebRtcAudioManager", "STREAM_RING stream volume: " + WebRtcAudioManager.VolumeLogger.this.audioManager.getStreamVolume(2) + " (max=" + this.maxRingVolume + ")");
          return;
        }
        if (i == 3)
        {
          Logging.d("WebRtcAudioManager", "VOICE_CALL stream volume: " + WebRtcAudioManager.VolumeLogger.this.audioManager.getStreamVolume(0) + " (max=" + this.maxVoiceCallVolume + ")");
          return;
        }
        Logging.w("WebRtcAudioManager", "Invalid audio mode: " + WebRtcAudioManager.AUDIO_MODES[i]);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.voiceengine.WebRtcAudioManager
 * JD-Core Version:    0.6.0
 */