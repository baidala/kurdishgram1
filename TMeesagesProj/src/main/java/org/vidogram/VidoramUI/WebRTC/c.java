package org.vidogram.VidogramUi.WebRTC;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build.VERSION;
import android.os.Handler;
import android.support.v4.b.r.d;
import android.util.Log;
import com.google.firebase.crash.FirebaseCrash;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.vidogram.VidogramUi.WebRTC.WebRTCUI.CallActivity;
import org.vidogram.VidogramUi.WebRTC.WebRTCUI.e;
import org.vidogram.VidogramUi.WebRTC.a.a.b;
import org.vidogram.VidogramUi.WebRTC.b.f;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.CameraVideoCapturer;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer.Callbacks;

public class c
  implements org.vidogram.VidogramUi.WebRTC.a.a.a, org.vidogram.VidogramUi.WebRTC.a.c, d.c
{
  private static final String[] R;
  public static volatile c a = null;
  public static String d = "seen";
  private f A;
  private int B;
  private String C = d;
  private Runnable D;
  private Runnable E;
  private Runnable F;
  private Handler G;
  private Handler H;
  private boolean I;
  private boolean J;
  private boolean K;
  private String L;
  private a M;
  private boolean N;
  private boolean O;
  private int P = 911112;
  private org.vidogram.VidogramUi.WebRTC.WebRTCUI.c Q;
  private BroadcastReceiver S = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      if (((ApplicationLoader.applicationContext.getPackageName() + ".END_VIDEO_CALL").equals(paramIntent.getAction())) && (paramIntent.getIntExtra("end_hash", 0) == c.a(c.this)))
      {
        if (c.e())
          c.this.h();
      }
      else
        return;
      c.this.U();
    }
  };
  public SurfaceViewRenderer b;
  public e c;
  org.vidogram.VidogramUi.WebRTC.b.g e;
  private Intent f;
  private org.vidogram.VidogramUi.WebRTC.a.b g;
  private org.vidogram.VidogramUi.WebRTC.a.d h;
  private SurfaceViewRenderer i;
  private d j = null;
  private org.vidogram.VidogramUi.WebRTC.a.a k;
  private a.b l;
  private a m = null;
  private EglBase n;
  private RendererCommon.ScalingType o = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
  private final List<VideoRenderer.Callbacks> p = new ArrayList();
  private d.d q;
  private long r;
  private long s = 0L;
  private boolean t = true;
  private boolean u = false;
  private boolean v = true;
  private boolean w = true;
  private boolean x = false;
  private boolean y = false;
  private boolean z = false;

  static
  {
    R = new String[] { "android.permission.MODIFY_AUDIO_SETTINGS", "android.permission.RECORD_AUDIO", "android.permission.CAMERA" };
  }

  public c(Intent paramIntent)
  {
    a(a.a);
    this.f = paramIntent;
  }

  private boolean V()
  {
    return (Camera2Enumerator.isSupported()) && (this.f.getBooleanExtra("org.appspot.apprtc.CAMERA2", true));
  }

  private boolean W()
  {
    return this.f.getBooleanExtra("org.appspot.apprtc.CAPTURETOTEXTURE", false);
  }

  private void X()
  {
    this.A.f();
    this.A.h();
    if (this.j == null)
      return;
    this.j.a(true, 1500);
  }

  private VideoCapturer Y()
  {
    VideoCapturer localVideoCapturer;
    if (this.f.getStringExtra("org.appspot.apprtc.VIDEO_FILE_AS_CAMERA") != null)
      localVideoCapturer = null;
    while (localVideoCapturer == null)
    {
      d("Failed to open camera");
      return null;
      if (this.x)
      {
        localVideoCapturer = null;
        continue;
      }
      if (V())
      {
        if (!W())
        {
          d(ApplicationLoader.applicationContext.getString(2131166837));
          return null;
        }
        Logging.d("CallConnectionClient", "Creating capturer using camera2 API.");
        localVideoCapturer = a(new Camera2Enumerator(ApplicationLoader.applicationContext));
        continue;
      }
      Logging.d("CallConnectionClient", "Creating capturer using camera1 API.");
      localVideoCapturer = a(new Camera1Enumerator(W()));
    }
    return localVideoCapturer;
  }

  private String Z()
  {
    if (this.s > 0L)
      if (!this.O)
        break label52;
    label52: for (this.C = (-1L * this.s + ""); ; this.C = (this.s + ""))
      return this.C;
  }

  private String a(double paramDouble)
  {
    if (S() == a.b)
      return LocaleController.getString("NetworkQualityPOOR", 2131166777);
    if (this.u)
    {
      if (paramDouble >= 2000.0D)
        return LocaleController.getString("NetworkQualityEXCELLENT", 2131166774);
      if (paramDouble > 1000.0D)
        return LocaleController.getString("NetworkQualityGOOD", 2131166775);
      if (paramDouble > 200.0D)
        return LocaleController.getString("NetworkQualityMODERATE", 2131166776);
      return LocaleController.getString("NetworkQualityPOOR", 2131166777);
    }
    if (paramDouble >= 45.0D)
      return LocaleController.getString("NetworkQualityEXCELLENT", 2131166774);
    if (paramDouble > 30.0D)
      return LocaleController.getString("NetworkQualityGOOD", 2131166775);
    if (paramDouble > 15.0D)
      return LocaleController.getString("NetworkQualityMODERATE", 2131166776);
    return LocaleController.getString("NetworkQualityPOOR", 2131166777);
  }

  public static c a(Intent paramIntent)
  {
    Object localObject = a;
    if (!e())
    {
      monitorenter;
      try
      {
        c localc = a;
        localObject = localc;
        if (localc == null)
        {
          localObject = new c(paramIntent);
          a = (c)localObject;
        }
        return localObject;
      }
      finally
      {
        monitorexit;
      }
    }
    return (c)localObject;
  }

  private VideoCapturer a(CameraEnumerator paramCameraEnumerator)
  {
    int i2 = 0;
    String[] arrayOfString = paramCameraEnumerator.getDeviceNames();
    Logging.d("CallConnectionClient", "Looking for front facing cameras.");
    int i3 = arrayOfString.length;
    int i1 = 0;
    Object localObject;
    while (i1 < i3)
    {
      localObject = arrayOfString[i1];
      if (paramCameraEnumerator.isFrontFacing((String)localObject))
      {
        Logging.d("CallConnectionClient", "Creating front facing camera capturer.");
        localObject = paramCameraEnumerator.createCapturer((String)localObject, null);
        if (localObject != null)
          return localObject;
      }
      i1 += 1;
    }
    Logging.d("CallConnectionClient", "Looking for other cameras.");
    i3 = arrayOfString.length;
    i1 = i2;
    while (true)
    {
      if (i1 >= i3)
        break label155;
      localObject = arrayOfString[i1];
      if (!paramCameraEnumerator.isFrontFacing((String)localObject))
      {
        Logging.d("CallConnectionClient", "Creating other camera capturer.");
        CameraVideoCapturer localCameraVideoCapturer = paramCameraEnumerator.createCapturer((String)localObject, null);
        localObject = localCameraVideoCapturer;
        if (localCameraVideoCapturer != null)
          break;
      }
      i1 += 1;
    }
    label155: return (VideoCapturer)null;
  }

  private void aa()
  {
    if (this.y)
      return;
    a(a.j);
    this.A.a();
    this.K = true;
  }

  private void ab()
  {
    U();
    Object localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.f.getIntExtra("itman.Vidofilm.apprtc.Callee_ID", 0)));
    Object localObject1 = "Unknown";
    if (localObject2 != null)
      localObject1 = ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name);
    Object localObject3;
    int i1;
    while (true)
    {
      localObject3 = new Intent(ApplicationLoader.applicationContext, CallActivity.class);
      ((Intent)localObject3).addFlags(805306368);
      localObject1 = new r.d(ApplicationLoader.applicationContext).a(LocaleController.getString("VideoOutgoingCall", 2131166808)).b((CharSequence)localObject1).a(2130837867).c(false).b(true).a(PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject3, 0));
      if (Build.VERSION.SDK_INT >= 16)
      {
        localObject3 = new Intent();
        ((Intent)localObject3).setAction(ApplicationLoader.applicationContext.getPackageName() + ".END_VIDEO_CALL");
        i1 = Utilities.random.nextInt();
        this.B = i1;
        ((Intent)localObject3).putExtra("end_hash", i1);
        ((r.d)localObject1).a(2130837752, LocaleController.getString("VoipEndCall", 2131166582), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, (Intent)localObject3, 134217728));
        ((r.d)localObject1).d(2);
      }
      if (Build.VERSION.SDK_INT >= 17)
        ((r.d)localObject1).a(false);
      if (Build.VERSION.SDK_INT >= 21)
        ((r.d)localObject1).e(-13851168);
      if ((localObject2 != null) && (((TLRPC.User)localObject2).photo != null))
      {
        localObject2 = ((TLRPC.User)localObject2).photo.photo_small;
        if (localObject2 != null)
        {
          localObject3 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject2, null, "50_50");
          if (localObject3 == null)
            break;
          ((r.d)localObject1).a(((BitmapDrawable)localObject3).getBitmap());
        }
      }
      ((NotificationManager)ApplicationLoader.applicationContext.getSystemService("notification")).notify(this.P, ((r.d)localObject1).b());
      return;
      if (this.f.getStringExtra("itman.Vidofilm.apprtc.PHONE_NUMBER") == null)
        continue;
      localObject1 = this.f.getStringExtra("itman.Vidofilm.apprtc.PHONE_NUMBER");
    }
    while (true)
    {
      float f1;
      try
      {
        f1 = 160.0F / AndroidUtilities.dp(50.0F);
        localObject3 = new BitmapFactory.Options();
        if (f1 >= 1.0F)
          break label446;
        i1 = 1;
        ((BitmapFactory.Options)localObject3).inSampleSize = i1;
        localObject2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString(), (BitmapFactory.Options)localObject3);
        if (localObject2 == null)
          break;
        ((r.d)localObject1).a((Bitmap)localObject2);
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
      }
      break;
      label446: i1 = (int)f1;
    }
  }

  public static c c()
  {
    return a;
  }

  private void c(String paramString)
  {
    FirebaseCrash.a(new Exception(paramString));
    this.A.e();
  }

  private void d(String paramString)
  {
    c(paramString);
  }

  public static boolean e()
  {
    return a != null;
  }

  public void A()
  {
    if (!this.K)
      this.y = true;
    a(a.l);
    this.C = "cancel";
    if (this.r != 0L)
      this.s = (System.currentTimeMillis() - this.r);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        c.this.h();
      }
    }
    , 1000L);
  }

  public void B()
  {
    if ((this.u) && (this.j != null) && (this.k != null))
    {
      this.j.i();
      if (this.w)
        break label65;
    }
    label65: for (boolean bool = true; ; bool = false)
    {
      this.w = bool;
      if (this.b != null)
      {
        if (!this.w)
          break;
        this.b.setMirror(true);
      }
      return;
    }
    this.b.setMirror(false);
  }

  public boolean C()
  {
    try
    {
      if (this.j != null)
        if (this.t)
          break label37;
      label32: label37: for (boolean bool = true; ; bool = false)
      {
        this.t = bool;
        this.j.a(this.t);
        return this.t;
      }
    }
    catch (Exception localException)
    {
      break label32;
    }
  }

  public boolean D()
  {
    if (this.k == null)
      return this.u;
    boolean bool;
    if (!this.u)
    {
      bool = true;
      this.u = bool;
      if (this.j != null)
      {
        if (!this.u)
          break label83;
        if (this.k != null)
          this.k.b();
        this.j.a("start_remote");
        this.j.c();
      }
    }
    while (true)
    {
      return this.u;
      bool = false;
      break;
      label83: if (this.k != null)
        this.k.a();
      this.j.a("stop_remote");
      this.j.d();
    }
  }

  public boolean E()
  {
    if (!this.v);
    for (boolean bool = true; ; bool = false)
    {
      this.v = bool;
      if ((!this.y) && (this.m != null))
        break;
      return this.v;
    }
    if (this.v)
      this.m.a("true", a.a.a);
    while (true)
    {
      return this.v;
      this.m.a("false", a.a.c);
    }
  }

  public void F()
  {
    this.C = "reject";
    if (!this.J)
      this.y = true;
    h();
  }

  public void G()
  {
    if (this.j == null)
      return;
    this.G.postDelayed(this.F, 4000L);
    this.I = true;
    a(a.b);
    if (this.J)
      this.j.f();
    if (this.m != null);
    try
    {
      this.m.a();
    }
    catch (Exception localException2)
    {
      try
      {
        while (true)
        {
          this.A.h();
          label70: this.K = true;
          return;
          localException1 = localException1;
          this.m = a.a(ApplicationLoader.applicationContext, new Runnable()
          {
            public void run()
            {
            }
          }
          , this.L);
        }
      }
      catch (Exception localException2)
      {
        break label70;
      }
    }
  }

  public void H()
  {
    this.A.h();
  }

  public long I()
  {
    return this.r;
  }

  public boolean J()
  {
    return (this.u) || (this.z);
  }

  public boolean K()
  {
    return this.z;
  }

  public boolean L()
  {
    return this.u;
  }

  public boolean M()
  {
    return this.v;
  }

  public boolean N()
  {
    return this.t;
  }

  public boolean O()
  {
    return this.w;
  }

  public RendererCommon.ScalingType P()
  {
    return this.o;
  }

  public boolean Q()
  {
    return this.I;
  }

  public a.b R()
  {
    return this.l;
  }

  public a S()
  {
    return this.M;
  }

  public void T()
  {
    if (this.y)
      return;
    a(a.k);
    this.A.a();
    this.K = true;
  }

  public void U()
  {
    try
    {
      Context localContext1 = ApplicationLoader.applicationContext;
      Context localContext2 = ApplicationLoader.applicationContext;
      ((NotificationManager)localContext1.getSystemService("notification")).cancel(this.P);
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public void a()
  {
    l();
  }

  public void a(long paramLong)
  {
    this.r = paramLong;
  }

  public void a(String paramString)
  {
    d(paramString);
  }

  public void a(a.b paramb)
  {
    if (paramb == null)
    {
      FileLog.d("VidogramwebRTC :SignalingParameters null");
      h();
    }
    FileLog.d("CallConnectionClient" + paramb + "");
    FileLog.d("VidogramwebRTC :SignalingParameters  set");
    this.l = paramb;
    if (S() == a.c)
    {
      FileLog.d("VidogramwebRTC :SignalingParameters  READY TO CONNECT");
      a(a.e);
      g();
      return;
    }
    FileLog.d("VidogramwebRTC :SignalingParameters  not READY TO CONNECT");
    a(a.e);
  }

  public void a(org.vidogram.VidogramUi.WebRTC.a.b paramb)
  {
    this.g = paramb;
  }

  public void a(org.vidogram.VidogramUi.WebRTC.a.d paramd)
  {
    this.h = paramd;
  }

  public void a(a parama)
  {
    this.M = parama;
    if (this.g != null)
      this.g.onStateChanged(this.M);
  }

  public void a(IceCandidate paramIceCandidate)
  {
    if (this.j == null)
    {
      Log.e("CallConnectionClient", "Received ICE candidate for a non-initialized peer connection.");
      return;
    }
    this.j.a(paramIceCandidate);
  }

  public void a(SessionDescription paramSessionDescription)
  {
    b(paramSessionDescription);
  }

  public void a(boolean paramBoolean)
  {
    if (this.r != 0L)
      this.s = (System.currentTimeMillis() - this.r);
    if ((paramBoolean) && (R() != null) && (R().b) && (this.r == 0L))
    {
      aa();
      return;
    }
    if (this.A != null)
    {
      if (paramBoolean)
      {
        this.A.b();
        return;
      }
      this.A.e();
      return;
    }
    h();
  }

  public void a(IceCandidate[] paramArrayOfIceCandidate)
  {
    if (this.j == null)
    {
      Log.e("CallConnectionClient", "Received ICE candidate removals for a non-initialized peer connection.");
      return;
    }
    this.j.a(paramArrayOfIceCandidate);
  }

  public void a(StatsReport[] paramArrayOfStatsReport)
  {
    int i2 = paramArrayOfStatsReport.length;
    int i1 = 0;
    while (i1 < i2)
    {
      StatsReport localStatsReport = paramArrayOfStatsReport[i1];
      this.e.a(localStatsReport);
      i1 += 1;
    }
    if (this.h != null)
      this.h.a(a(this.e.a()));
  }

  public void b()
  {
    m();
  }

  public void b(String paramString)
  {
    d(paramString);
  }

  public void b(IceCandidate paramIceCandidate)
  {
    if (this.k != null)
      this.k.a(paramIceCandidate);
  }

  public void b(SessionDescription paramSessionDescription)
  {
    if (this.j == null)
      Log.e("CallConnectionClient", "Received remote SDP for non-initilized peer connection.");
    do
    {
      do
      {
        return;
        this.j.a(paramSessionDescription);
      }
      while (this.l.b);
      this.J = true;
    }
    while (!this.I);
    this.j.f();
  }

  public void b(IceCandidate[] paramArrayOfIceCandidate)
  {
    if (this.k != null)
      this.k.a(paramArrayOfIceCandidate);
  }

  public void c(SessionDescription paramSessionDescription)
  {
    if (this.k != null)
    {
      if (!this.l.b)
        break label92;
      this.k.a(paramSessionDescription);
      this.K = true;
    }
    while (true)
    {
      if (this.q.f > 0)
      {
        Log.d("CallConnectionClient", "Set video maximum bitrate: " + this.q.f);
        this.j.a(Integer.valueOf(this.q.f));
      }
      return;
      label92: this.k.b(paramSessionDescription);
    }
  }

  public Intent d()
  {
    return this.f;
  }

  // ERROR //
  public void f()
  {
    // Byte code:
    //   0: ldc_w 795
    //   3: invokestatic 693	org/vidogram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   6: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   9: invokestatic 799	com/google/android/gms/c/a:a	(Landroid/content/Context;)V
    //   12: ldc_w 801
    //   15: invokestatic 806	javax/net/ssl/SSLContext:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
    //   18: astore 4
    //   20: aload 4
    //   22: aconst_null
    //   23: aconst_null
    //   24: aconst_null
    //   25: invokevirtual 810	javax/net/ssl/SSLContext:init	([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V
    //   28: aload 4
    //   30: invokevirtual 814	javax/net/ssl/SSLContext:createSSLEngine	()Ljavax/net/ssl/SSLEngine;
    //   33: pop
    //   34: aload_0
    //   35: getfield 816	org/vidogram/VidogramUi/WebRTC/c:N	Z
    //   38: ifeq +47 -> 85
    //   41: return
    //   42: astore 4
    //   44: aload 4
    //   46: invokevirtual 819	com/google/android/gms/common/e:printStackTrace	()V
    //   49: goto -37 -> 12
    //   52: astore 4
    //   54: aload 4
    //   56: invokevirtual 820	com/google/android/gms/common/d:printStackTrace	()V
    //   59: goto -47 -> 12
    //   62: astore 4
    //   64: aload 4
    //   66: invokevirtual 821	java/security/NoSuchAlgorithmException:printStackTrace	()V
    //   69: aconst_null
    //   70: astore 4
    //   72: goto -52 -> 20
    //   75: astore 5
    //   77: aload 5
    //   79: invokevirtual 822	java/security/KeyManagementException:printStackTrace	()V
    //   82: goto -54 -> 28
    //   85: getstatic 440	android/os/Build$VERSION:SDK_INT	I
    //   88: bipush 23
    //   90: if_icmplt +31 -> 121
    //   93: new 824	org/vidogram/VidogramUi/a
    //   96: dup
    //   97: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: invokespecial 825	org/vidogram/VidogramUi/a:<init>	(Landroid/content/Context;)V
    //   103: getstatic 118	org/vidogram/VidogramUi/WebRTC/c:R	[Ljava/lang/String;
    //   106: invokevirtual 828	org/vidogram/VidogramUi/a:a	([Ljava/lang/String;)Z
    //   109: ifeq +12 -> 121
    //   112: aload_0
    //   113: invokevirtual 652	org/vidogram/VidogramUi/WebRTC/c:h	()V
    //   116: aconst_null
    //   117: putstatic 104	org/vidogram/VidogramUi/WebRTC/c:a	Lorg/vidogram/VidogramUi/WebRTC/c;
    //   120: return
    //   121: aload_0
    //   122: new 830	org/vidogram/VidogramUi/WebRTC/WebRTCUI/c
    //   125: dup
    //   126: invokespecial 831	org/vidogram/VidogramUi/WebRTC/WebRTCUI/c:<init>	()V
    //   129: putfield 833	org/vidogram/VidogramUi/WebRTC/c:Q	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/c;
    //   132: aload_0
    //   133: new 749	org/vidogram/VidogramUi/WebRTC/b/g
    //   136: dup
    //   137: invokespecial 834	org/vidogram/VidogramUi/WebRTC/b/g:<init>	()V
    //   140: putfield 747	org/vidogram/VidogramUi/WebRTC/c:e	Lorg/vidogram/VidogramUi/WebRTC/b/g;
    //   143: ldc_w 836
    //   146: invokestatic 693	org/vidogram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   149: aload_0
    //   150: iconst_1
    //   151: putfield 816	org/vidogram/VidogramUi/WebRTC/c:N	Z
    //   154: aload_0
    //   155: invokespecial 838	org/vidogram/VidogramUi/WebRTC/c:ab	()V
    //   158: aload_0
    //   159: new 611	org/webrtc/SurfaceViewRenderer
    //   162: dup
    //   163: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   166: invokespecial 839	org/webrtc/SurfaceViewRenderer:<init>	(Landroid/content/Context;)V
    //   169: putfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   172: aload_0
    //   173: new 841	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e
    //   176: dup
    //   177: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   180: aload_0
    //   181: invokespecial 844	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e:<init>	(Landroid/content/Context;Lorg/vidogram/VidogramUi/WebRTC/a/c;)V
    //   184: putfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   187: aload_0
    //   188: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   191: bipush 8
    //   193: invokevirtual 849	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e:setVisibility	(I)V
    //   196: aload_0
    //   197: getfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   200: bipush 8
    //   202: invokevirtual 850	org/webrtc/SurfaceViewRenderer:setVisibility	(I)V
    //   205: aload_0
    //   206: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   209: invokevirtual 854	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e:getHolder	()Landroid/view/SurfaceHolder;
    //   212: bipush 253
    //   214: invokeinterface 859 2 0
    //   219: aload_0
    //   220: new 611	org/webrtc/SurfaceViewRenderer
    //   223: dup
    //   224: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   227: invokespecial 839	org/webrtc/SurfaceViewRenderer:<init>	(Landroid/content/Context;)V
    //   230: putfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   233: aload_0
    //   234: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   237: iconst_1
    //   238: invokevirtual 864	org/webrtc/SurfaceViewRenderer:setZOrderOnTop	(Z)V
    //   241: aload_0
    //   242: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   245: invokevirtual 865	org/webrtc/SurfaceViewRenderer:getHolder	()Landroid/view/SurfaceHolder;
    //   248: bipush 253
    //   250: invokeinterface 859 2 0
    //   255: aload_0
    //   256: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   259: ldc_w 866
    //   262: invokevirtual 869	org/webrtc/SurfaceViewRenderer:setBackgroundColor	(I)V
    //   265: aload_0
    //   266: getfield 139	org/vidogram/VidogramUi/WebRTC/c:p	Ljava/util/List;
    //   269: aload_0
    //   270: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   273: invokeinterface 875 2 0
    //   278: pop
    //   279: aload_0
    //   280: getfield 139	org/vidogram/VidogramUi/WebRTC/c:p	Ljava/util/List;
    //   283: aload_0
    //   284: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   287: invokeinterface 875 2 0
    //   292: pop
    //   293: aload_0
    //   294: invokestatic 881	org/webrtc/EglBase:create	()Lorg/webrtc/EglBase;
    //   297: putfield 883	org/vidogram/VidogramUi/WebRTC/c:n	Lorg/webrtc/EglBase;
    //   300: aload_0
    //   301: getfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   304: aload_0
    //   305: getfield 883	org/vidogram/VidogramUi/WebRTC/c:n	Lorg/webrtc/EglBase;
    //   308: invokevirtual 887	org/webrtc/EglBase:getEglBaseContext	()Lorg/webrtc/EglBase$Context;
    //   311: aconst_null
    //   312: invokevirtual 890	org/webrtc/SurfaceViewRenderer:init	(Lorg/webrtc/EglBase$Context;Lorg/webrtc/RendererCommon$RendererEvents;)V
    //   315: aload_0
    //   316: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   319: ldc_w 892
    //   322: invokevirtual 212	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   325: pop
    //   326: aload_0
    //   327: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   330: aload_0
    //   331: getfield 883	org/vidogram/VidogramUi/WebRTC/c:n	Lorg/webrtc/EglBase;
    //   334: invokevirtual 887	org/webrtc/EglBase:getEglBaseContext	()Lorg/webrtc/EglBase$Context;
    //   337: aconst_null
    //   338: invokevirtual 890	org/webrtc/SurfaceViewRenderer:init	(Lorg/webrtc/EglBase$Context;Lorg/webrtc/RendererCommon$RendererEvents;)V
    //   341: aload_0
    //   342: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   345: aload_0
    //   346: getfield 883	org/vidogram/VidogramUi/WebRTC/c:n	Lorg/webrtc/EglBase;
    //   349: invokevirtual 887	org/webrtc/EglBase:getEglBaseContext	()Lorg/webrtc/EglBase$Context;
    //   352: aconst_null
    //   353: invokevirtual 893	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e:init	(Lorg/webrtc/EglBase$Context;Lorg/webrtc/RendererCommon$RendererEvents;)V
    //   356: aload_0
    //   357: getfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   360: iconst_1
    //   361: invokevirtual 896	org/webrtc/SurfaceViewRenderer:setZOrderMediaOverlay	(Z)V
    //   364: aload_0
    //   365: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   368: ldc_w 366
    //   371: iconst_0
    //   372: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   375: istore_1
    //   376: aload_0
    //   377: new 195	org/vidogram/VidogramUi/WebRTC/b/f
    //   380: dup
    //   381: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   384: iload_1
    //   385: invokespecial 899	org/vidogram/VidogramUi/WebRTC/b/f:<init>	(Landroid/content/Context;I)V
    //   388: putfield 193	org/vidogram/VidogramUi/WebRTC/c:A	Lorg/vidogram/VidogramUi/WebRTC/b/f;
    //   391: aload_0
    //   392: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   395: ldc_w 901
    //   398: iconst_0
    //   399: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   402: istore_3
    //   403: aload_0
    //   404: iconst_0
    //   405: putfield 147	org/vidogram/VidogramUi/WebRTC/c:v	Z
    //   408: aload_0
    //   409: ldc_w 645
    //   412: putfield 671	org/vidogram/VidogramUi/WebRTC/c:L	Ljava/lang/String;
    //   415: aload_0
    //   416: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   419: new 14	org/vidogram/VidogramUi/WebRTC/c$2
    //   422: dup
    //   423: aload_0
    //   424: invokespecial 902	org/vidogram/VidogramUi/WebRTC/c$2:<init>	(Lorg/vidogram/VidogramUi/WebRTC/c;)V
    //   427: aload_0
    //   428: getfield 671	org/vidogram/VidogramUi/WebRTC/c:L	Ljava/lang/String;
    //   431: invokestatic 674	org/vidogram/VidogramUi/WebRTC/a:a	(Landroid/content/Context;Ljava/lang/Runnable;Ljava/lang/String;)Lorg/vidogram/VidogramUi/WebRTC/a;
    //   434: putfield 127	org/vidogram/VidogramUi/WebRTC/c:m	Lorg/vidogram/VidogramUi/WebRTC/a;
    //   437: aload_0
    //   438: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   441: ldc_w 904
    //   444: iconst_0
    //   445: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   448: istore_1
    //   449: aload_0
    //   450: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   453: ldc_w 906
    //   456: iconst_0
    //   457: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   460: istore_2
    //   461: aload_0
    //   462: aload_0
    //   463: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   466: ldc_w 908
    //   469: iconst_0
    //   470: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   473: putfield 151	org/vidogram/VidogramUi/WebRTC/c:x	Z
    //   476: aconst_null
    //   477: astore 4
    //   479: aload_0
    //   480: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   483: ldc_w 910
    //   486: iconst_1
    //   487: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   490: ifeq +77 -> 567
    //   493: new 912	org/vidogram/VidogramUi/WebRTC/d$a
    //   496: dup
    //   497: aload_0
    //   498: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   501: ldc_w 914
    //   504: iconst_1
    //   505: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   508: aload_0
    //   509: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   512: ldc_w 916
    //   515: iconst_m1
    //   516: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   519: aload_0
    //   520: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   523: ldc_w 918
    //   526: iconst_m1
    //   527: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   530: aload_0
    //   531: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   534: ldc_w 920
    //   537: invokevirtual 212	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   540: aload_0
    //   541: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   544: ldc_w 922
    //   547: iconst_0
    //   548: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   551: aload_0
    //   552: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   555: ldc_w 924
    //   558: iconst_m1
    //   559: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   562: invokespecial 927	org/vidogram/VidogramUi/WebRTC/d$a:<init>	(ZIILjava/lang/String;ZI)V
    //   565: astore 4
    //   567: aload_0
    //   568: new 771	org/vidogram/VidogramUi/WebRTC/d$d
    //   571: dup
    //   572: aload_0
    //   573: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   576: ldc_w 929
    //   579: iconst_1
    //   580: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   583: iload_3
    //   584: iload_1
    //   585: iload_2
    //   586: aload_0
    //   587: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   590: ldc_w 931
    //   593: iconst_0
    //   594: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   597: aload_0
    //   598: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   601: ldc_w 933
    //   604: iconst_0
    //   605: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   608: aload_0
    //   609: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   612: ldc_w 935
    //   615: invokevirtual 212	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   618: aload_0
    //   619: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   622: ldc_w 937
    //   625: iconst_1
    //   626: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   629: aload_0
    //   630: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   633: ldc_w 939
    //   636: iconst_0
    //   637: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   640: aload_0
    //   641: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   644: ldc_w 941
    //   647: iconst_0
    //   648: invokevirtual 370	android/content/Intent:getIntExtra	(Ljava/lang/String;I)I
    //   651: aload_0
    //   652: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   655: ldc_w 943
    //   658: invokevirtual 212	android/content/Intent:getStringExtra	(Ljava/lang/String;)Ljava/lang/String;
    //   661: aload_0
    //   662: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   665: ldc_w 945
    //   668: iconst_0
    //   669: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   672: aload_0
    //   673: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   676: ldc_w 947
    //   679: iconst_0
    //   680: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   683: aload_0
    //   684: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   687: ldc_w 949
    //   690: iconst_0
    //   691: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   694: aload_0
    //   695: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   698: ldc_w 951
    //   701: iconst_0
    //   702: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   705: aload_0
    //   706: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   709: ldc_w 953
    //   712: iconst_0
    //   713: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   716: aload_0
    //   717: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   720: ldc_w 955
    //   723: iconst_0
    //   724: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   727: aload_0
    //   728: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   731: ldc_w 957
    //   734: iconst_0
    //   735: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   738: aload 4
    //   740: invokespecial 960	org/vidogram/VidogramUi/WebRTC/d$d:<init>	(ZZIIIILjava/lang/String;ZZILjava/lang/String;ZZZZZZZLorg/vidogram/VidogramUi/WebRTC/d$a;)V
    //   743: putfield 769	org/vidogram/VidogramUi/WebRTC/c:q	Lorg/vidogram/VidogramUi/WebRTC/d$d;
    //   746: aload_0
    //   747: new 660	android/os/Handler
    //   750: dup
    //   751: invokespecial 961	android/os/Handler:<init>	()V
    //   754: putfield 963	org/vidogram/VidogramUi/WebRTC/c:H	Landroid/os/Handler;
    //   757: aload_0
    //   758: new 660	android/os/Handler
    //   761: dup
    //   762: invokespecial 961	android/os/Handler:<init>	()V
    //   765: putfield 654	org/vidogram/VidogramUi/WebRTC/c:G	Landroid/os/Handler;
    //   768: aload_0
    //   769: new 16	org/vidogram/VidogramUi/WebRTC/c$3
    //   772: dup
    //   773: aload_0
    //   774: invokespecial 964	org/vidogram/VidogramUi/WebRTC/c$3:<init>	(Lorg/vidogram/VidogramUi/WebRTC/c;)V
    //   777: putfield 966	org/vidogram/VidogramUi/WebRTC/c:E	Ljava/lang/Runnable;
    //   780: aload_0
    //   781: new 18	org/vidogram/VidogramUi/WebRTC/c$4
    //   784: dup
    //   785: aload_0
    //   786: invokespecial 967	org/vidogram/VidogramUi/WebRTC/c$4:<init>	(Lorg/vidogram/VidogramUi/WebRTC/c;)V
    //   789: putfield 969	org/vidogram/VidogramUi/WebRTC/c:D	Ljava/lang/Runnable;
    //   792: aload_0
    //   793: new 20	org/vidogram/VidogramUi/WebRTC/c$5
    //   796: dup
    //   797: aload_0
    //   798: invokespecial 970	org/vidogram/VidogramUi/WebRTC/c$5:<init>	(Lorg/vidogram/VidogramUi/WebRTC/c;)V
    //   801: putfield 656	org/vidogram/VidogramUi/WebRTC/c:F	Ljava/lang/Runnable;
    //   804: aload_0
    //   805: getfield 963	org/vidogram/VidogramUi/WebRTC/c:H	Landroid/os/Handler;
    //   808: aload_0
    //   809: getfield 969	org/vidogram/VidogramUi/WebRTC/c:D	Ljava/lang/Runnable;
    //   812: ldc2_w 971
    //   815: invokevirtual 664	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   818: pop
    //   819: aload_0
    //   820: getfield 172	org/vidogram/VidogramUi/WebRTC/c:f	Landroid/content/Intent;
    //   823: ldc_w 974
    //   826: iconst_1
    //   827: invokevirtual 187	android/content/Intent:getBooleanExtra	(Ljava/lang/String;Z)Z
    //   830: ifne +111 -> 941
    //   833: aload_0
    //   834: getfield 127	org/vidogram/VidogramUi/WebRTC/c:m	Lorg/vidogram/VidogramUi/WebRTC/a;
    //   837: invokevirtual 668	org/vidogram/VidogramUi/WebRTC/a:a	()V
    //   840: aload_0
    //   841: getfield 193	org/vidogram/VidogramUi/WebRTC/c:A	Lorg/vidogram/VidogramUi/WebRTC/b/f;
    //   844: invokevirtual 975	org/vidogram/VidogramUi/WebRTC/b/f:c	()V
    //   847: aload_0
    //   848: getfield 654	org/vidogram/VidogramUi/WebRTC/c:G	Landroid/os/Handler;
    //   851: aload_0
    //   852: getfield 656	org/vidogram/VidogramUi/WebRTC/c:F	Ljava/lang/Runnable;
    //   855: ldc2_w 976
    //   858: invokevirtual 664	android/os/Handler:postDelayed	(Ljava/lang/Runnable;J)Z
    //   861: pop
    //   862: aload_0
    //   863: invokevirtual 706	org/vidogram/VidogramUi/WebRTC/c:g	()V
    //   866: aload_0
    //   867: invokestatic 980	org/vidogram/VidogramUi/WebRTC/d:a	()Lorg/vidogram/VidogramUi/WebRTC/d;
    //   870: putfield 125	org/vidogram/VidogramUi/WebRTC/c:j	Lorg/vidogram/VidogramUi/WebRTC/d;
    //   873: aload_0
    //   874: getfield 125	org/vidogram/VidogramUi/WebRTC/c:j	Lorg/vidogram/VidogramUi/WebRTC/d;
    //   877: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   880: aload_0
    //   881: getfield 769	org/vidogram/VidogramUi/WebRTC/c:q	Lorg/vidogram/VidogramUi/WebRTC/d$d;
    //   884: aload_0
    //   885: invokevirtual 983	org/vidogram/VidogramUi/WebRTC/d:a	(Landroid/content/Context;Lorg/vidogram/VidogramUi/WebRTC/d$d;Lorg/vidogram/VidogramUi/WebRTC/d$c;)V
    //   888: new 985	android/content/IntentFilter
    //   891: dup
    //   892: invokespecial 986	android/content/IntentFilter:<init>	()V
    //   895: astore 4
    //   897: aload 4
    //   899: new 261	java/lang/StringBuilder
    //   902: dup
    //   903: invokespecial 262	java/lang/StringBuilder:<init>	()V
    //   906: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   909: invokevirtual 444	android/content/Context:getPackageName	()Ljava/lang/String;
    //   912: invokevirtual 273	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   915: ldc_w 446
    //   918: invokevirtual 273	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   921: invokevirtual 276	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   924: invokevirtual 989	android/content/IntentFilter:addAction	(Ljava/lang/String;)V
    //   927: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   930: aload_0
    //   931: getfield 165	org/vidogram/VidogramUi/WebRTC/c:S	Landroid/content/BroadcastReceiver;
    //   934: aload 4
    //   936: invokevirtual 993	android/content/Context:registerReceiver	(Landroid/content/BroadcastReceiver;Landroid/content/IntentFilter;)Landroid/content/Intent;
    //   939: pop
    //   940: return
    //   941: aload_0
    //   942: getfield 193	org/vidogram/VidogramUi/WebRTC/c:A	Lorg/vidogram/VidogramUi/WebRTC/b/f;
    //   945: invokevirtual 994	org/vidogram/VidogramUi/WebRTC/b/f:i	()V
    //   948: goto -86 -> 862
    //   951: astore 4
    //   953: goto -106 -> 847
    //   956: astore 4
    //   958: goto -924 -> 34
    //
    // Exception table:
    //   from	to	target	type
    //   6	12	42	com/google/android/gms/common/e
    //   6	12	52	com/google/android/gms/common/d
    //   12	20	62	java/security/NoSuchAlgorithmException
    //   20	28	75	java/security/KeyManagementException
    //   833	847	951	java/lang/Exception
    //   0	6	956	java/lang/Exception
    //   6	12	956	java/lang/Exception
    //   12	20	956	java/lang/Exception
    //   20	28	956	java/lang/Exception
    //   28	34	956	java/lang/Exception
    //   44	49	956	java/lang/Exception
    //   54	59	956	java/lang/Exception
    //   64	69	956	java/lang/Exception
    //   77	82	956	java/lang/Exception
  }

  public void g()
  {
    if (S() == a.e)
    {
      a(a.b);
      this.r = 0L;
      this.s = 0L;
      if (this.y)
        return;
      this.k = new g(ApplicationLoader.applicationContext, this, new org.vidogram.VidogramUi.WebRTC.b.b());
      FileLog.d("VidogramwebRTC :startCall");
      this.k.a(R());
      return;
    }
    FileLog.d("VidogramwebRTC :parametr not ready");
    a(a.c);
  }

  // ERROR //
  public void h()
  {
    // Byte code:
    //   0: aconst_null
    //   1: putstatic 104	org/vidogram/VidogramUi/WebRTC/c:a	Lorg/vidogram/VidogramUi/WebRTC/c;
    //   4: aload_0
    //   5: getfield 833	org/vidogram/VidogramUi/WebRTC/c:Q	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/c;
    //   8: ifnull +10 -> 18
    //   11: aload_0
    //   12: getfield 833	org/vidogram/VidogramUi/WebRTC/c:Q	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/c;
    //   15: invokevirtual 1009	org/vidogram/VidogramUi/WebRTC/WebRTCUI/c:a	()V
    //   18: aload_0
    //   19: invokevirtual 358	org/vidogram/VidogramUi/WebRTC/c:U	()V
    //   22: aload_0
    //   23: invokevirtual 283	org/vidogram/VidogramUi/WebRTC/c:S	()Lorg/vidogram/VidogramUi/WebRTC/c$a;
    //   26: getstatic 586	org/vidogram/VidogramUi/WebRTC/c$a:l	Lorg/vidogram/VidogramUi/WebRTC/c$a;
    //   29: if_acmpeq +10 -> 39
    //   32: aload_0
    //   33: getstatic 1011	org/vidogram/VidogramUi/WebRTC/c$a:d	Lorg/vidogram/VidogramUi/WebRTC/c$a;
    //   36: invokevirtual 170	org/vidogram/VidogramUi/WebRTC/c:a	(Lorg/vidogram/VidogramUi/WebRTC/c$a;)V
    //   39: aload_0
    //   40: lconst_0
    //   41: invokevirtual 1013	org/vidogram/VidogramUi/WebRTC/c:a	(J)V
    //   44: getstatic 227	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   47: aload_0
    //   48: getfield 165	org/vidogram/VidogramUi/WebRTC/c:S	Landroid/content/BroadcastReceiver;
    //   51: invokevirtual 1017	android/content/Context:unregisterReceiver	(Landroid/content/BroadcastReceiver;)V
    //   54: aload_0
    //   55: getfield 963	org/vidogram/VidogramUi/WebRTC/c:H	Landroid/os/Handler;
    //   58: aload_0
    //   59: getfield 969	org/vidogram/VidogramUi/WebRTC/c:D	Ljava/lang/Runnable;
    //   62: invokevirtual 1021	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   65: aload_0
    //   66: getfield 963	org/vidogram/VidogramUi/WebRTC/c:H	Landroid/os/Handler;
    //   69: aload_0
    //   70: getfield 966	org/vidogram/VidogramUi/WebRTC/c:E	Ljava/lang/Runnable;
    //   73: invokevirtual 1021	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   76: aload_0
    //   77: getfield 654	org/vidogram/VidogramUi/WebRTC/c:G	Landroid/os/Handler;
    //   80: aload_0
    //   81: getfield 656	org/vidogram/VidogramUi/WebRTC/c:F	Ljava/lang/Runnable;
    //   84: invokevirtual 1021	android/os/Handler:removeCallbacks	(Ljava/lang/Runnable;)V
    //   87: aload_0
    //   88: getfield 193	org/vidogram/VidogramUi/WebRTC/c:A	Lorg/vidogram/VidogramUi/WebRTC/b/f;
    //   91: ifnull +10 -> 101
    //   94: aload_0
    //   95: getfield 193	org/vidogram/VidogramUi/WebRTC/c:A	Lorg/vidogram/VidogramUi/WebRTC/b/f;
    //   98: invokevirtual 1022	org/vidogram/VidogramUi/WebRTC/b/f:g	()V
    //   101: aload_0
    //   102: getfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   105: ifnull +15 -> 120
    //   108: aload_0
    //   109: getfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   112: invokevirtual 1025	org/webrtc/SurfaceViewRenderer:release	()V
    //   115: aload_0
    //   116: aconst_null
    //   117: putfield 609	org/vidogram/VidogramUi/WebRTC/c:b	Lorg/webrtc/SurfaceViewRenderer;
    //   120: aload_0
    //   121: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   124: ifnull +15 -> 139
    //   127: aload_0
    //   128: getfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   131: invokevirtual 1025	org/webrtc/SurfaceViewRenderer:release	()V
    //   134: aload_0
    //   135: aconst_null
    //   136: putfield 861	org/vidogram/VidogramUi/WebRTC/c:i	Lorg/webrtc/SurfaceViewRenderer;
    //   139: aload_0
    //   140: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   143: ifnull +22 -> 165
    //   146: aload_0
    //   147: getfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   150: invokevirtual 1026	org/vidogram/VidogramUi/WebRTC/WebRTCUI/e:release	()V
    //   153: aload_0
    //   154: aconst_null
    //   155: putfield 846	org/vidogram/VidogramUi/WebRTC/c:c	Lorg/vidogram/VidogramUi/WebRTC/WebRTCUI/e;
    //   158: aload_0
    //   159: getfield 883	org/vidogram/VidogramUi/WebRTC/c:n	Lorg/webrtc/EglBase;
    //   162: invokevirtual 1027	org/webrtc/EglBase:release	()V
    //   165: aload_0
    //   166: getfield 127	org/vidogram/VidogramUi/WebRTC/c:m	Lorg/vidogram/VidogramUi/WebRTC/a;
    //   169: ifnull +15 -> 184
    //   172: aload_0
    //   173: getfield 127	org/vidogram/VidogramUi/WebRTC/c:m	Lorg/vidogram/VidogramUi/WebRTC/a;
    //   176: invokevirtual 1028	org/vidogram/VidogramUi/WebRTC/a:b	()V
    //   179: aload_0
    //   180: aconst_null
    //   181: putfield 127	org/vidogram/VidogramUi/WebRTC/c:m	Lorg/vidogram/VidogramUi/WebRTC/a;
    //   184: aload_0
    //   185: getfield 605	org/vidogram/VidogramUi/WebRTC/c:k	Lorg/vidogram/VidogramUi/WebRTC/a/a;
    //   188: ifnull +21 -> 209
    //   191: aload_0
    //   192: getfield 605	org/vidogram/VidogramUi/WebRTC/c:k	Lorg/vidogram/VidogramUi/WebRTC/a/a;
    //   195: aload_0
    //   196: invokespecial 1030	org/vidogram/VidogramUi/WebRTC/c:Z	()Ljava/lang/String;
    //   199: invokeinterface 1031 2 0
    //   204: aload_0
    //   205: aconst_null
    //   206: putfield 605	org/vidogram/VidogramUi/WebRTC/c:k	Lorg/vidogram/VidogramUi/WebRTC/a/a;
    //   209: aload_0
    //   210: getfield 125	org/vidogram/VidogramUi/WebRTC/c:j	Lorg/vidogram/VidogramUi/WebRTC/d;
    //   213: ifnull +15 -> 228
    //   216: aload_0
    //   217: getfield 125	org/vidogram/VidogramUi/WebRTC/c:j	Lorg/vidogram/VidogramUi/WebRTC/d;
    //   220: invokevirtual 1032	org/vidogram/VidogramUi/WebRTC/d:b	()V
    //   223: aload_0
    //   224: aconst_null
    //   225: putfield 125	org/vidogram/VidogramUi/WebRTC/c:j	Lorg/vidogram/VidogramUi/WebRTC/d;
    //   228: aload_0
    //   229: getfield 711	org/vidogram/VidogramUi/WebRTC/c:g	Lorg/vidogram/VidogramUi/WebRTC/a/b;
    //   232: ifnull +12 -> 244
    //   235: aload_0
    //   236: getfield 711	org/vidogram/VidogramUi/WebRTC/c:g	Lorg/vidogram/VidogramUi/WebRTC/a/b;
    //   239: invokeinterface 1035 1 0
    //   244: return
    //   245: astore_1
    //   246: return
    //   247: astore_1
    //   248: goto -161 -> 87
    //   251: astore_1
    //   252: goto -230 -> 22
    //
    // Exception table:
    //   from	to	target	type
    //   0	4	245	java/lang/Exception
    //   22	39	245	java/lang/Exception
    //   39	54	245	java/lang/Exception
    //   87	101	245	java/lang/Exception
    //   101	120	245	java/lang/Exception
    //   120	139	245	java/lang/Exception
    //   139	165	245	java/lang/Exception
    //   165	184	245	java/lang/Exception
    //   184	209	245	java/lang/Exception
    //   209	228	245	java/lang/Exception
    //   228	244	245	java/lang/Exception
    //   54	87	247	java/lang/Exception
    //   4	18	251	java/lang/Exception
    //   18	22	251	java/lang/Exception
  }

  public void i()
  {
    FileLog.d("CallConnectionClientonConnectedToRoomInternal");
    Object localObject = null;
    try
    {
      if (this.q.a)
        localObject = Y();
      this.j.a(this.n.getEglBaseContext(), this.b, this.p, (VideoCapturer)localObject, this.l);
      if (this.l.b)
      {
        this.j.e();
        this.A.d();
      }
      while (true)
      {
        FileLog.d("CallConnectionClientend onConnectedToRoomInternal");
        return;
        if (this.l.f != null)
        {
          this.j.a(this.l.f);
          this.J = true;
          if (this.I)
            this.j.f();
        }
        if (this.l.g == null)
          continue;
        localObject = this.l.g.iterator();
        while (((Iterator)localObject).hasNext())
        {
          IceCandidate localIceCandidate = (IceCandidate)((Iterator)localObject).next();
          this.j.a(localIceCandidate);
        }
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        FileLog.d("CallConnectionClient" + localException.getMessage() + " 8");
        h();
      }
    }
  }

  public void j()
  {
    i();
  }

  public void k()
  {
    a(a.g);
  }

  public void l()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (c.this.c != null)
          c.this.c.setVisibility(8);
      }
    }
    , 0L);
    if (this.h != null)
      this.h.b();
    this.z = false;
  }

  public void m()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (c.this.c != null)
          c.this.c.setVisibility(0);
      }
    }
    , 0L);
    if (this.h != null)
      this.h.a();
    this.z = true;
  }

  public void n()
  {
    this.j.d();
  }

  public void o()
  {
    this.j.c();
  }

  public void p()
  {
    a(a.i);
    if (this.t)
      C();
    if (this.u)
      D();
    if (this.h != null)
      this.h.c();
  }

  public void q()
  {
    a(a.f);
    C();
    if (this.h != null)
      this.h.d();
  }

  public void r()
  {
    this.G.removeCallbacks(this.F);
    if (S() != a.f)
      a(a.h);
  }

  public void s()
  {
    this.O = false;
    if (this.r == 0L)
      a(System.currentTimeMillis());
    a(a.f);
    if (this.H != null)
    {
      this.H.removeCallbacks(this.D);
      this.H.removeCallbacks(this.E);
      this.G.removeCallbacks(this.F);
    }
    X();
  }

  public void t()
  {
    this.O = true;
    if (this.r != 0L)
      this.s = (System.currentTimeMillis() - this.r);
    if (this.h != null)
      this.h.a("");
    a(a.b);
    this.H.postDelayed(this.E, 25000L);
  }

  public void u()
  {
  }

  public void v()
  {
  }

  public void w()
  {
  }

  public void x()
  {
    this.g = null;
    this.h = null;
  }

  public void y()
  {
    if ((this.Q != null) && (this.i != null) && (e()))
      this.Q.a(this.i);
  }

  public void z()
  {
    if (this.Q != null)
      this.Q.a();
  }

  public static enum a
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.c
 * JD-Core Version:    0.6.0
 */