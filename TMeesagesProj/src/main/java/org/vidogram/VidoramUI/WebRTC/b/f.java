package org.vidogram.VidogramUi.WebRTC.b;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Vibrator;
import com.google.firebase.crash.FirebaseCrash;
import org.vidogram.VidogramUi.WebRTC.c;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class f
{
  public static MediaPlayer a;
  private static final String b = f.class.getSimpleName();
  private Context c;
  private int d;
  private Vibrator e;
  private SoundPool f;
  private int g;
  private int h;
  private int i;
  private int j;
  private int k;
  private int l;
  private int m;
  private SoundPool n;

  public f(Context paramContext, int paramInt)
  {
    this.c = paramContext;
    this.d = paramInt;
    this.f = new SoundPool(1, 0, 0);
    this.n = new SoundPool(1, 0, 0);
    this.k = this.f.load(paramContext, 2131099651, 1);
    this.g = this.n.load(paramContext, 2131099654, 1);
    this.h = this.f.load(paramContext, 2131099653, 1);
    this.i = this.f.load(paramContext, 2131099652, 1);
    this.j = this.f.load(paramContext, 2131099650, 1);
  }

  public void a()
  {
    if (this.l != 0)
      this.f.stop(this.l);
    this.l = this.f.play(this.j, 1.0F, 1.0F, 0, -1, 1.0F);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        f.a(f.this, 0);
        f.a(f.this).release();
        if (c.e())
          c.c().h();
      }
    }
    , 4000L);
  }

  public void b()
  {
    if (this.l != 0)
      this.f.stop(this.l);
    this.l = this.f.play(this.i, 1.0F, 1.0F, 0, 0, 1.0F);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        f.a(f.this).release();
        f.a(f.this, 0);
        if (c.e())
          c.c().h();
      }
    }
    , 1000L);
  }

  public void c()
  {
    if (this.l != 0)
      this.f.stop(this.l);
    this.l = this.f.play(this.k, 1.0F, 1.0F, 0, -1, 1.0F);
    if (this.l == 0)
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (f.b(f.this) == 0)
            f.a(f.this, f.a(f.this).play(f.c(f.this), 1.0F, 1.0F, 0, -1, 1.0F));
          if (f.b(f.this) == 0)
            AndroidUtilities.runOnUIThread(this, 100L);
        }
      }
      , 100L);
  }

  public void d()
  {
    if (this.l != 0)
      this.f.stop(this.l);
    if (this.n == null)
      return;
    if (this.m != 0)
      this.n.stop(this.m);
    this.m = this.n.play(this.g, 1.0F, 1.0F, 0, -1, 1.0F);
  }

  public void e()
  {
    if (this.l != 0)
      this.f.stop(this.l);
    this.l = this.f.play(this.h, 1.0F, 1.0F, 0, 0, 1.0F);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        f.a(f.this, 0);
        f.a(f.this).release();
        if (c.e())
          c.c().h();
      }
    }
    , 1000L);
  }

  public void f()
  {
    try
    {
      this.n.stop(this.m);
      this.n.release();
      return;
    }
    catch (Exception localException)
    {
      FileLog.d(b + localException.getMessage() + " 16");
      FirebaseCrash.a(localException);
    }
  }

  public void g()
  {
    try
    {
      if ((this.f != null) && (this.l != 0))
      {
        this.f.stop(this.l);
        this.l = 0;
      }
      if (this.f != null)
        this.f.release();
      j();
      if (this.n != null)
      {
        this.n.stop(this.m);
        this.n.release();
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.d(b + localException.getMessage() + " 16");
      FirebaseCrash.a(localException);
    }
  }

  public void h()
  {
    FileLog.e(b + "stop() sound");
    try
    {
      if ((this.f != null) && (this.l != 0))
      {
        this.f.stop(this.l);
        this.l = 0;
      }
      j();
      if (this.n != null)
        this.n.stop(this.m);
      return;
    }
    catch (Exception localException)
    {
      FileLog.d(b + localException.getMessage() + " 15");
      FirebaseCrash.a(localException);
    }
  }

  public void i()
  {
    SharedPreferences localSharedPreferences = this.c.getSharedPreferences("Notifications", 0);
    a = new MediaPlayer();
    a.setOnPreparedListener(new MediaPlayer.OnPreparedListener()
    {
      public void onPrepared(MediaPlayer paramMediaPlayer)
      {
        f.a.start();
      }
    });
    a.setLooping(true);
    a.setAudioStreamType(2);
    try
    {
      if (localSharedPreferences.getBoolean("custom_" + this.d, false));
      for (Object localObject = localSharedPreferences.getString("ringtone_path_" + this.d, RingtoneManager.getDefaultUri(1).toString()); ; localObject = localSharedPreferences.getString("CallsRingtonePath", RingtoneManager.getDefaultUri(1).toString()))
      {
        a.setDataSource(this.c, Uri.parse((String)localObject));
        a.prepareAsync();
        localObject = (AudioManager)this.c.getSystemService("audio");
        if (!localSharedPreferences.getBoolean("custom_" + this.d, false))
          break;
        i1 = localSharedPreferences.getInt("calls_vibrate_" + this.d, 0);
        if (((i1 != 2) && (i1 != 4) && ((((AudioManager)localObject).getRingerMode() == 1) || (((AudioManager)localObject).getRingerMode() == 2))) || ((i1 == 4) && (((AudioManager)localObject).getRingerMode() == 1)))
        {
          this.e = ((Vibrator)this.c.getSystemService("vibrator"));
          l1 = 1000L;
          if (i1 != 1)
            break label382;
          l1 = 1000L / 2L;
          this.e.vibrate(new long[] { 0L, l1, 1000L }, 0);
        }
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        FileLog.e(localException);
        if (a == null)
          continue;
        a.release();
        a = null;
        continue;
        int i1 = localSharedPreferences.getInt("vibrate_calls", 0);
        continue;
        label382: if (i1 != 3)
          continue;
        long l1 = 1000L * 2L;
      }
    }
  }

  public void j()
  {
    if (a != null)
    {
      a.stop();
      a.release();
      a = null;
    }
    if (this.e != null)
    {
      this.e.cancel();
      this.e = null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.f
 * JD-Core Version:    0.6.0
 */