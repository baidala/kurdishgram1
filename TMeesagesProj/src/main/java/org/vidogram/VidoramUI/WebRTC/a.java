package org.vidogram.VidogramUi.WebRTC;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import org.vidogram.messenger.ApplicationLoader;

public class a
{
  private final Context a;
  private final Runnable b;
  private boolean c = false;
  private AudioManager d;
  private int e = -2;
  private boolean f = false;
  private boolean g = false;
  private PowerManager h;
  private PowerManager.WakeLock i;
  private int j = 32;
  private a k;
  private String l;
  private b m = null;
  private a n;
  private final Set<a> o = new HashSet();
  private BroadcastReceiver p;

  private a(Context paramContext, Runnable paramRunnable, String paramString)
  {
    this.a = paramContext;
    this.b = paramRunnable;
    this.d = ((AudioManager)paramContext.getSystemService("audio"));
    PreferenceManager.getDefaultSharedPreferences(paramContext);
    this.l = paramString;
    if (this.l.equals("false"));
    for (this.k = a.c; ; this.k = a.a)
    {
      this.m = b.a(paramContext, new Runnable()
      {
        public void run()
        {
          a.a(a.this);
        }
      });
      return;
    }
  }

  static a a(Context paramContext, Runnable paramRunnable, String paramString)
  {
    return new a(paramContext, paramRunnable, paramString);
  }

  private void a(boolean paramBoolean)
  {
    if (this.d.isSpeakerphoneOn() == paramBoolean)
      return;
    this.d.setSpeakerphoneOn(paramBoolean);
  }

  private void b(boolean paramBoolean)
  {
    if (this.d.isMicrophoneMute() == paramBoolean)
      return;
    this.d.setMicrophoneMute(paramBoolean);
  }

  private void c()
  {
    try
    {
      if ((this.m.c()) && (!c.c().J()))
      {
        if (!this.i.isHeld())
        {
          this.i.acquire();
          return;
        }
      }
      else if (this.i.isHeld())
      {
        this.i.release();
        return;
      }
    }
    catch (Exception localException)
    {
    }
  }

  private void c(boolean paramBoolean)
  {
    this.o.clear();
    if (paramBoolean)
      this.o.add(a.b);
    while (true)
    {
      Log.d("AppRTCAudioManager", "audioDevices: " + this.o);
      if (!paramBoolean)
        break;
      a(a.b);
      return;
      this.o.add(a.a);
      if (!f())
        continue;
      this.o.add(a.c);
    }
    a(this.k);
  }

  private void d()
  {
    IntentFilter localIntentFilter = new IntentFilter("android.intent.action.HEADSET_PLUG");
    this.p = new BroadcastReceiver()
    {
      public void onReceive(Context paramContext, Intent paramIntent)
      {
        boolean bool = true;
        int i = paramIntent.getIntExtra("state", 0);
        if (i == 1)
          switch (i)
          {
          default:
            Log.e("AppRTCAudioManager", "Invalid state");
          case 0:
          case 1:
          }
        do
        {
          return;
          bool = false;
          break;
          a.a(a.this, bool);
          return;
        }
        while (a.b(a.this) == a.a.b);
        a.a(a.this, bool);
      }
    };
    try
    {
      this.a.registerReceiver(this.p, localIntentFilter);
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void e()
  {
    this.a.unregisterReceiver(this.p);
    this.p = null;
  }

  private boolean f()
  {
    return this.a.getPackageManager().hasSystemFeature("android.hardware.telephony");
  }

  @Deprecated
  private boolean g()
  {
    return this.d.isWiredHeadsetOn();
  }

  private void h()
  {
    Log.d("AppRTCAudioManager", "onAudioManagerChangedState: devices=" + this.o + ", selected=" + this.n);
    if (this.o.size() == 2)
      this.m.a();
    while (true)
    {
      if (this.b != null)
        this.b.run();
      return;
      if (this.o.size() == 1)
      {
        this.m.b();
        continue;
      }
      Log.e("AppRTCAudioManager", "Invalid device list");
    }
  }

  public void a()
  {
    Log.d("AppRTCAudioManager", "init");
    if (this.c)
      return;
    this.e = this.d.getMode();
    this.f = this.d.isSpeakerphoneOn();
    this.g = this.d.isMicrophoneMute();
    this.h = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power"));
    this.i = this.h.newWakeLock(32, "calling");
    this.d.requestAudioFocus(null, 0, 2);
    this.d.setMode(3);
    b(false);
    c(g());
    try
    {
      d();
      label118: this.c = true;
      return;
    }
    catch (Exception localException)
    {
      break label118;
    }
  }

  public void a(String paramString, a parama)
  {
    this.k = parama;
    this.l = paramString;
    c(g());
  }

  public void a(a parama)
  {
    Log.d("AppRTCAudioManager", "setAudioDevice(device=" + parama + ")");
    if (parama == this.n)
      return;
    switch (3.a[parama.ordinal()])
    {
    default:
      Log.e("AppRTCAudioManager", "Invalid audio device selection");
    case 1:
    case 2:
    case 3:
    }
    while (true)
    {
      h();
      return;
      a(true);
      this.n = a.a;
      continue;
      a(false);
      this.n = a.c;
      continue;
      a(false);
      this.n = a.b;
    }
  }

  public void b()
  {
    Log.d("AppRTCAudioManager", "close");
    if (!this.c)
      return;
    e();
    a(this.f);
    b(this.g);
    this.d.setMode(this.e);
    this.d.abandonAudioFocus(null);
    if (this.m != null)
    {
      this.m.b();
      this.m = null;
    }
    if (c.e())
      c.c().U();
    this.c = false;
  }

  public static enum a
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.a
 * JD-Core Version:    0.6.0
 */