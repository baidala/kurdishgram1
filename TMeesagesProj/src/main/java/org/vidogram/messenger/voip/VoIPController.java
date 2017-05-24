package org.vidogram.messenger.voip;

import android.content.Context;
import android.media.audiofx.AcousticEchoCanceler;
import android.os.Build.VERSION;
import android.os.SystemClock;
import java.io.File;
import java.util.Calendar;
import java.util.Locale;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.tgnet.TLRPC.TL_phoneConnection;

public class VoIPController
{
  public static final int DATA_SAVING_ALWAYS = 2;
  public static final int DATA_SAVING_MOBILE = 1;
  public static final int DATA_SAVING_NEVER = 0;
  public static final int ERROR_AUDIO_IO = 3;
  public static final int ERROR_INCOMPATIBLE = 1;
  public static final int ERROR_LOCALIZED = -3;
  public static final int ERROR_PEER_OUTDATED = -1;
  public static final int ERROR_PRIVACY = -2;
  public static final int ERROR_TIMEOUT = 2;
  public static final int ERROR_UNKNOWN = 0;
  public static final int NET_TYPE_3G = 3;
  public static final int NET_TYPE_DIALUP = 10;
  public static final int NET_TYPE_EDGE = 2;
  public static final int NET_TYPE_ETHERNET = 7;
  public static final int NET_TYPE_GPRS = 1;
  public static final int NET_TYPE_HSPA = 4;
  public static final int NET_TYPE_LTE = 5;
  public static final int NET_TYPE_OTHER_HIGH_SPEED = 8;
  public static final int NET_TYPE_OTHER_LOW_SPEED = 9;
  public static final int NET_TYPE_OTHER_MOBILE = 11;
  public static final int NET_TYPE_UNKNOWN = 0;
  public static final int NET_TYPE_WIFI = 6;
  public static final int STATE_ESTABLISHED = 3;
  public static final int STATE_FAILED = 4;
  public static final int STATE_WAIT_INIT = 1;
  public static final int STATE_WAIT_INIT_ACK = 2;
  private long callStartTime;
  private ConnectionStateListener listener;
  private long nativeInst = 0L;

  private void ensureNativeInstance()
  {
    if (this.nativeInst == 0L)
      throw new IllegalStateException("Native instance is not valid");
  }

  private String getLogFilePath()
  {
    Calendar localCalendar = Calendar.getInstance();
    return new File(ApplicationLoader.applicationContext.getExternalFilesDir(null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_voip.txt", new Object[] { Integer.valueOf(localCalendar.get(5)), Integer.valueOf(localCalendar.get(2) + 1), Integer.valueOf(localCalendar.get(1)), Integer.valueOf(localCalendar.get(11)), Integer.valueOf(localCalendar.get(12)), Integer.valueOf(localCalendar.get(13)) })).getAbsolutePath();
  }

  public static String getVersion()
  {
    return nativeGetVersion();
  }

  private void handleStateChange(int paramInt)
  {
    this.callStartTime = SystemClock.elapsedRealtime();
    if (this.listener != null)
      this.listener.onConnectionStateChanged(paramInt);
  }

  private native void nativeConnect(long paramLong);

  private native void nativeDebugCtl(long paramLong, int paramInt1, int paramInt2);

  private native String nativeGetDebugLog(long paramLong);

  private native String nativeGetDebugString(long paramLong);

  private native int nativeGetLastError(long paramLong);

  private native long nativeGetPreferredRelayID(long paramLong);

  private native void nativeGetStats(long paramLong, Stats paramStats);

  private static native String nativeGetVersion();

  private native long nativeInit(int paramInt);

  private native void nativeRelease(long paramLong);

  private native void nativeSetConfig(long paramLong, double paramDouble1, double paramDouble2, int paramInt, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, String paramString);

  private native void nativeSetEncryptionKey(long paramLong, byte[] paramArrayOfByte, boolean paramBoolean);

  private native void nativeSetMicMute(long paramLong, boolean paramBoolean);

  private static native void nativeSetNativeBufferSize(int paramInt);

  private native void nativeSetNetworkType(long paramLong, int paramInt);

  private native void nativeSetRemoteEndpoints(long paramLong, TLRPC.TL_phoneConnection[] paramArrayOfTL_phoneConnection, boolean paramBoolean);

  private native void nativeStart(long paramLong);

  public static void setNativeBufferSize(int paramInt)
  {
    nativeSetNativeBufferSize(paramInt);
  }

  public void connect()
  {
    ensureNativeInstance();
    nativeConnect(this.nativeInst);
  }

  public void debugCtl(int paramInt1, int paramInt2)
  {
    ensureNativeInstance();
    nativeDebugCtl(this.nativeInst, paramInt1, paramInt2);
  }

  public long getCallDuration()
  {
    return SystemClock.elapsedRealtime() - this.callStartTime;
  }

  public String getDebugLog()
  {
    ensureNativeInstance();
    return nativeGetDebugLog(this.nativeInst);
  }

  public String getDebugString()
  {
    ensureNativeInstance();
    return nativeGetDebugString(this.nativeInst);
  }

  public int getLastError()
  {
    ensureNativeInstance();
    return nativeGetLastError(this.nativeInst);
  }

  public long getPreferredRelayID()
  {
    ensureNativeInstance();
    return nativeGetPreferredRelayID(this.nativeInst);
  }

  public void getStats(Stats paramStats)
  {
    ensureNativeInstance();
    if (paramStats == null)
      throw new NullPointerException("You're not supposed to pass null here");
    nativeGetStats(this.nativeInst, paramStats);
  }

  public void release()
  {
    ensureNativeInstance();
    nativeRelease(this.nativeInst);
    this.nativeInst = 0L;
  }

  public void setConfig(double paramDouble1, double paramDouble2, int paramInt)
  {
    ensureNativeInstance();
    boolean bool4 = false;
    boolean bool1 = false;
    boolean bool3 = false;
    boolean bool2 = bool3;
    if (Build.VERSION.SDK_INT >= 16)
      bool1 = bool4;
    try
    {
      bool2 = AcousticEchoCanceler.isAvailable();
      bool1 = bool2;
      bool4 = AcousticEchoCanceler.isAvailable();
      bool3 = bool4;
      bool1 = bool2;
      bool2 = bool3;
      long l = this.nativeInst;
      if ((Build.VERSION.SDK_INT < 16) || (!bool1) || (!VoIPServerConfig.getBoolean("use_system_aec", true)))
      {
        bool1 = true;
        if ((Build.VERSION.SDK_INT >= 16) && (bool2) && (VoIPServerConfig.getBoolean("use_system_ns", true)))
          break label134;
      }
      label134: for (bool2 = true; ; bool2 = false)
      {
        nativeSetConfig(l, paramDouble1, paramDouble2, paramInt, bool1, bool2, true, null);
        return;
        bool1 = false;
        break;
      }
    }
    catch (Throwable localThrowable)
    {
      while (true)
        bool2 = bool3;
    }
  }

  public void setConnectionStateListener(ConnectionStateListener paramConnectionStateListener)
  {
    this.listener = paramConnectionStateListener;
  }

  public void setEncryptionKey(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    if (paramArrayOfByte.length != 256)
      throw new IllegalArgumentException("key length must be exactly 256 bytes but is " + paramArrayOfByte.length);
    ensureNativeInstance();
    nativeSetEncryptionKey(this.nativeInst, paramArrayOfByte, paramBoolean);
  }

  public void setMicMute(boolean paramBoolean)
  {
    ensureNativeInstance();
    nativeSetMicMute(this.nativeInst, paramBoolean);
  }

  public void setNetworkType(int paramInt)
  {
    ensureNativeInstance();
    nativeSetNetworkType(this.nativeInst, paramInt);
  }

  public void setRemoteEndpoints(TLRPC.TL_phoneConnection[] paramArrayOfTL_phoneConnection, boolean paramBoolean)
  {
    if (paramArrayOfTL_phoneConnection.length == 0)
      throw new IllegalArgumentException("endpoints size is 0");
    int i = 0;
    while (i < paramArrayOfTL_phoneConnection.length)
    {
      TLRPC.TL_phoneConnection localTL_phoneConnection = paramArrayOfTL_phoneConnection[i];
      if ((localTL_phoneConnection.ip == null) || (localTL_phoneConnection.ip.length() == 0))
        throw new IllegalArgumentException("endpoint " + localTL_phoneConnection + " has empty/null ipv4");
      if ((localTL_phoneConnection.peer_tag != null) && (localTL_phoneConnection.peer_tag.length != 16))
        throw new IllegalArgumentException("endpoint " + localTL_phoneConnection + " has peer_tag of wrong length");
      i += 1;
    }
    ensureNativeInstance();
    nativeSetRemoteEndpoints(this.nativeInst, paramArrayOfTL_phoneConnection, paramBoolean);
  }

  public void start()
  {
    ensureNativeInstance();
    nativeStart(this.nativeInst);
  }

  public static abstract interface ConnectionStateListener
  {
    public abstract void onConnectionStateChanged(int paramInt);
  }

  public static class Stats
  {
    public long bytesRecvdMobile;
    public long bytesRecvdWifi;
    public long bytesSentMobile;
    public long bytesSentWifi;

    public String toString()
    {
      return "Stats{bytesRecvdMobile=" + this.bytesRecvdMobile + ", bytesSentWifi=" + this.bytesSentWifi + ", bytesRecvdWifi=" + this.bytesRecvdWifi + ", bytesSentMobile=" + this.bytesSentMobile + '}';
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.voip.VoIPController
 * JD-Core Version:    0.6.0
 */