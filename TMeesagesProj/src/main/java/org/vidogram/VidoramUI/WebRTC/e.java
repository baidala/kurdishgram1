package org.vidogram.VidogramUi.WebRTC;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.h;
import com.google.a.i;
import e.m;
import itman.Vidofilm.a.f;
import itman.Vidofilm.a.p;
import itman.Vidofilm.a.v;
import java.util.ArrayList;
import java.util.ArrayList<Lorg.webrtc.PeerConnection.IceServer;>;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vidogram.VidogramUi.WebRTC.WebRTCUI.CallActivity;
import org.vidogram.VidogramUi.WebRTC.a.a.b;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.TLRPC.User;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

public class e
{
  Context a;
  private int b;
  private boolean c;

  // ERROR //
  public e(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 31	java/lang/Object:<init>	()V
    //   4: aload_0
    //   5: iconst_1
    //   6: putfield 33	org/vidogram/VidogramUi/WebRTC/e:c	Z
    //   9: aload_0
    //   10: aload_1
    //   11: putfield 35	org/vidogram/VidogramUi/WebRTC/e:a	Landroid/content/Context;
    //   14: aload_1
    //   15: invokestatic 39	com/google/android/gms/c/a:a	(Landroid/content/Context;)V
    //   18: ldc 41
    //   20: invokestatic 47	javax/net/ssl/SSLContext:getInstance	(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
    //   23: astore_1
    //   24: aload_1
    //   25: aconst_null
    //   26: aconst_null
    //   27: aconst_null
    //   28: invokevirtual 51	javax/net/ssl/SSLContext:init	([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V
    //   31: aload_1
    //   32: invokevirtual 55	javax/net/ssl/SSLContext:createSSLEngine	()Ljavax/net/ssl/SSLEngine;
    //   35: pop
    //   36: return
    //   37: astore_1
    //   38: aload_1
    //   39: invokevirtual 58	com/google/android/gms/common/e:printStackTrace	()V
    //   42: goto -24 -> 18
    //   45: astore_1
    //   46: aload_1
    //   47: invokevirtual 59	com/google/android/gms/common/d:printStackTrace	()V
    //   50: goto -32 -> 18
    //   53: astore_1
    //   54: aload_1
    //   55: invokevirtual 60	java/security/NoSuchAlgorithmException:printStackTrace	()V
    //   58: aconst_null
    //   59: astore_1
    //   60: goto -36 -> 24
    //   63: astore_2
    //   64: aload_2
    //   65: invokevirtual 61	java/security/KeyManagementException:printStackTrace	()V
    //   68: goto -37 -> 31
    //   71: astore_1
    //   72: return
    //
    // Exception table:
    //   from	to	target	type
    //   14	18	37	com/google/android/gms/common/e
    //   14	18	45	com/google/android/gms/common/d
    //   18	24	53	java/security/NoSuchAlgorithmException
    //   24	31	63	java/security/KeyManagementException
    //   14	18	71	java/lang/Exception
    //   18	24	71	java/lang/Exception
    //   24	31	71	java/lang/Exception
    //   31	36	71	java/lang/Exception
    //   38	42	71	java/lang/Exception
    //   46	50	71	java/lang/Exception
    //   54	58	71	java/lang/Exception
    //   64	68	71	java/lang/Exception
  }

  private Intent a(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2, String paramString4)
  {
    FileLog.e("VidogramWebRTCconnectToRoom");
    boolean bool1 = Boolean.valueOf(this.a.getString(2131166874)).booleanValue();
    String str1 = this.a.getString(2131166962);
    String str2 = this.a.getString(2131166865);
    boolean bool2 = Boolean.valueOf(this.a.getString(2131166909)).booleanValue();
    boolean bool3 = Boolean.valueOf(this.a.getString(2131166882)).booleanValue();
    boolean bool4 = Boolean.valueOf(this.a.getString(2131166915)).booleanValue();
    boolean bool5 = Boolean.valueOf(this.a.getString(2131166861)).booleanValue();
    boolean bool6 = Boolean.valueOf(this.a.getString(2131166919)).booleanValue();
    boolean bool7 = Boolean.valueOf(this.a.getString(2131166886)).booleanValue();
    boolean bool8 = Boolean.valueOf(this.a.getString(2131166890)).booleanValue();
    boolean bool9 = Boolean.valueOf(this.a.getString(2131166894)).booleanValue();
    boolean bool10 = Boolean.valueOf(this.a.getString(2131166903)).booleanValue();
    int j = 0;
    int i = 0;
    Object localObject = this.a.getString(2131166923);
    boolean bool11 = itman.Vidofilm.b.a(this.a).r();
    String[] arrayOfString = ((String)localObject).split("[ x]+");
    if (arrayOfString.length == 2);
    try
    {
      j = Integer.parseInt(arrayOfString[0]);
      i = Integer.parseInt(arrayOfString[1]);
      m = 0;
      localObject = this.a.getString(2131166905);
      arrayOfString = ((String)localObject).split("[ x]+");
      k = m;
      if (arrayOfString.length != 2);
    }
    catch (NumberFormatException localNumberFormatException2)
    {
      try
      {
        k = Integer.parseInt(arrayOfString[0]);
        boolean bool12 = Boolean.valueOf(this.a.getString(2131166878)).booleanValue();
        m = 1700;
        localObject = this.a.getString(2131166944);
        if (!((String)localObject).equals(localObject))
          m = Integer.parseInt(this.a.getString(2131166948));
        int n = 0;
        localObject = this.a.getString(2131166936);
        if (!((String)localObject).equals(localObject))
          n = Integer.parseInt(this.a.getString(2131166940));
        boolean bool13 = Boolean.valueOf(this.a.getString(2131166898)).booleanValue();
        boolean bool14 = Boolean.valueOf(this.a.getString(2131166952)).booleanValue();
        FileLog.d("VidogramWebRTCConnecting to room " + paramString1);
        localObject = new Intent(this.a, CallActivity.class);
        ((Intent)localObject).putExtra("itman.Vidofilm.apprtc.Incoming_Call", paramBoolean1);
        ((Intent)localObject).putExtra("itman.Vidofilm.apprtc.PHONE_NUMBER", paramString4);
        ((Intent)localObject).putExtra("itman.Vidofilm.apprtc.Callee_ID", Integer.valueOf(paramString3));
        ((Intent)localObject).putExtra("itman.Vidofilm.apprtc.Room_Info", paramString2);
        ((Intent)localObject).putExtra("itman.Vidofilm.apprtc.ROOMID", paramString1);
        ((Intent)localObject).putExtra("org.appspot.apprtc.LOOPBACK", false);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEO_CALL", paramBoolean2);
        ((Intent)localObject).putExtra("org.appspot.apprtc.CAMERA2", bool1);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEO_WIDTH", j);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEO_HEIGHT", i);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEO_FPS", k);
        ((Intent)localObject).putExtra("org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER", bool12);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEO_BITRATE", m);
        ((Intent)localObject).putExtra("org.appspot.apprtc.VIDEOCODEC", str1);
        ((Intent)localObject).putExtra("org.appspot.apprtc.HWCODEC", bool2);
        ((Intent)localObject).putExtra("org.appspot.apprtc.CAPTURETOTEXTURE", bool3);
        ((Intent)localObject).putExtra("org.appspot.apprtc.NOAUDIOPROCESSING", bool4);
        ((Intent)localObject).putExtra("org.appspot.apprtc.AECDUMP", bool5);
        ((Intent)localObject).putExtra("org.appspot.apprtc.OPENSLES", bool6);
        ((Intent)localObject).putExtra("org.appspot.apprtc.DISABLE_BUILT_IN_AEC", bool7);
        ((Intent)localObject).putExtra("org.appspot.apprtc.DISABLE_BUILT_IN_AGC", bool8);
        ((Intent)localObject).putExtra("org.appspot.apprtc.DISABLE_BUILT_IN_NS", bool9);
        ((Intent)localObject).putExtra("org.appspot.apprtc.ENABLE_LEVEL_CONTROL", bool10);
        ((Intent)localObject).putExtra("org.appspot.apprtc.AUDIO_BITRATE", n);
        ((Intent)localObject).putExtra("org.appspot.apprtc.AUDIOCODEC", str2);
        ((Intent)localObject).putExtra("org.appspot.apprtc.DISPLAY_HUD", bool13);
        ((Intent)localObject).putExtra("org.appspot.apprtc.TRACING", bool14);
        ((Intent)localObject).putExtra("org.appspot.apprtc.FLEXFEC", false);
        ((Intent)localObject).putExtra("org.appspot.apprtc.DATA_CHANNEL_ENABLED", bool11);
        if (bool11)
        {
          ((Intent)localObject).putExtra("org.appspot.apprtc.ORDERED", true);
          ((Intent)localObject).putExtra("org.appspot.apprtc.MAX_RETRANSMITS_MS", -1);
          ((Intent)localObject).putExtra("org.appspot.apprtc.MAX_RETRANSMITS", -1);
          ((Intent)localObject).putExtra("org.appspot.apprtc.PROTOCOL", "");
          ((Intent)localObject).putExtra("org.appspot.apprtc.NEGOTIATED", false);
          ((Intent)localObject).putExtra("org.appspot.apprtc.ID", -1);
        }
        ((Intent)localObject).setFlags(268435456);
        return localObject;
        localNumberFormatException1 = localNumberFormatException1;
        j = 0;
        i = 0;
        FileLog.e("VidogramWebRTCWrong video resolution setting: " + (String)localObject);
      }
      catch (NumberFormatException localNumberFormatException2)
      {
        while (true)
        {
          int m;
          FileLog.e("VidogramWebRTCWrong camera fps setting: " + (String)localObject);
          int k = m;
        }
      }
    }
  }

  private a.b a(boolean paramBoolean1, String paramString1, boolean paramBoolean2, String paramString2, String paramString3, String paramString4)
  {
    try
    {
      paramString1 = new a.b(paramBoolean1, paramString1, c(paramString2), paramBoolean2, UserConfig.getClientUserId() + "", paramString3, paramString4, null, null);
      return paramString1;
    }
    catch (Exception paramString1)
    {
      FileLog.e("VidogramwebRTC : " + paramString1.getMessage() + "code :4");
    }
    return null;
  }

  private void a(int paramInt, Intent paramIntent)
  {
    FileLog.e("VidogramwebRTC : caller ID :" + paramInt);
    p localp = new p();
    localp.a(itman.Vidofilm.b.a(this.a).k());
    localp.a(paramInt);
    if (localp.a() == null)
    {
      itman.Vidofilm.d.d.a(this.a).a(true);
      return;
    }
    try
    {
      Timer localTimer = new Timer();
      localTimer.schedule(new TimerTask(localTimer, localp, paramInt, paramIntent)
      {
        public void run()
        {
          if (!c.e())
            this.a.cancel();
          do
            return;
          while (!e.a(e.this));
          e.b(e.this);
          this.b.b(e.a(e.this, this.c + ""));
          e.a(e.this, false);
          ((itman.Vidofilm.c.b)itman.Vidofilm.c.a.a().a(itman.Vidofilm.c.b.class)).a(this.b).a(new e.d()
          {
            public void onFailure(e.b<com.google.a.l> paramb, Throwable paramThrowable)
            {
              if (e.c(e.this) > 10)
              {
                e.2.this.a.cancel();
                c.a(e.2.this.d).a(false);
              }
              e.a(e.this, true);
              FileLog.e("VidogramwebRTC : " + paramThrowable.getMessage() + " code :3");
            }

            public void onResponse(e.b<com.google.a.l> paramb, e.l<com.google.a.l> paraml)
            {
              FileLog.e("VidogramwebRTC : response code: " + paraml.a());
              if (paraml.b())
                if (!c.e())
                  e.2.this.a.cancel();
              while (true)
              {
                return;
                paramb = ((com.google.a.l)paraml.c()).a("turn_server").b();
                String str = ((com.google.a.l)paraml.c()).a("wss_url").b();
                paraml = ((com.google.a.l)paraml.c()).a("wss_post_url").b();
                e.2.this.a.cancel();
                c.a(e.2.this.d).a(e.a(e.this, true, e.2.this.b.b(), true, paramb, str, paraml));
                return;
                if (paraml.a() == 404)
                {
                  if (!c.e())
                  {
                    e.2.this.a.cancel();
                    return;
                  }
                  e.2.this.a.cancel();
                  paramb = new Intent("callResult");
                  h.a(ApplicationLoader.applicationContext).a(paramb);
                  if (!c.e())
                    continue;
                  c.c().h();
                  return;
                }
                if (paraml.a() != 409)
                  break;
                if (!c.e())
                {
                  e.2.this.a.cancel();
                  return;
                }
                e.2.this.a.cancel();
                itman.Vidofilm.b.a(e.this.a).f(null);
                if (!c.e())
                  continue;
                c.c().T();
                return;
              }
              if (paraml.a() == 401)
              {
                itman.Vidofilm.d.d.a(e.this.a).a(true);
                c.a(e.2.this.d).a(false);
                e.2.this.a.cancel();
                return;
              }
              e.a(e.this, true);
            }
          });
        }
      }
      , 0L, 500L);
      return;
    }
    catch (Exception paramIntent)
    {
      FileLog.e("VidogramwebRTC : " + paramIntent.getMessage() + "code :2");
    }
  }

  private String b(String paramString)
  {
    paramString = UserConfig.getClientUserId() + "-" + paramString + "-" + System.currentTimeMillis() / 1000L;
    itman.Vidofilm.b.a(this.a).g(paramString);
    itman.Vidofilm.b.a(this.a).f(c.d);
    FileLog.e("VidogramwebRTC : room id " + paramString);
    return paramString;
  }

  private ArrayList<PeerConnection.IceServer> c(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    JSONArray localJSONArray1 = new JSONArray(paramString);
    int i = 0;
    while (i < localJSONArray1.length())
    {
      Object localObject = localJSONArray1.getJSONObject(i);
      JSONArray localJSONArray2 = ((JSONObject)localObject).getJSONArray("urls");
      if (((JSONObject)localObject).has("username"))
      {
        paramString = ((JSONObject)localObject).getString("username");
        if (!((JSONObject)localObject).has("credential"))
          break label135;
      }
      label135: for (localObject = ((JSONObject)localObject).getString("credential"); ; localObject = "")
      {
        int j = 0;
        while (j < localJSONArray2.length())
        {
          localArrayList.add(new PeerConnection.IceServer(localJSONArray2.getString(j), paramString, (String)localObject));
          j += 1;
        }
        paramString = "";
        break;
      }
      i += 1;
    }
    return (ArrayList<PeerConnection.IceServer>)localArrayList;
  }

  public a.b a(String paramString)
  {
    LinkedList localLinkedList = null;
    JSONArray localJSONArray = null;
    try
    {
      paramString = new JSONObject(paramString);
      if (!paramString.getString("result").equals("SUCCESS"))
        return null;
      JSONObject localJSONObject1 = new JSONObject(paramString.getString("params"));
      String str1 = localJSONObject1.getString("room_id");
      String str2 = localJSONObject1.getString("client_id");
      String str3 = localJSONObject1.getString("wss_url");
      String str4 = localJSONObject1.getString("wss_post_url");
      boolean bool = localJSONObject1.getBoolean("is_initiator");
      paramString = localJSONArray;
      int i;
      if (!bool)
      {
        localLinkedList = new LinkedList();
        localJSONArray = new JSONArray(localJSONObject1.getString("messages"));
        i = 0;
        paramString = null;
      }
      while (true)
      {
        if (i >= localJSONArray.length())
          break label340;
        String str5 = localJSONArray.getString(i);
        JSONObject localJSONObject2 = new JSONObject(str5);
        String str6 = localJSONObject2.getString("type");
        if (str6.equals("offer"))
        {
          paramString = new SessionDescription(SessionDescription.Type.fromCanonicalForm(str6), localJSONObject2.getString("sdp"));
        }
        else if (str6.equals("candidate"))
        {
          localLinkedList.add(new IceCandidate(localJSONObject2.getString("id"), localJSONObject2.getInt("label"), localJSONObject2.getString("candidate")));
        }
        else
        {
          FileLog.e("VidogramWebRTCUnknown message: " + str5);
          break label322;
          paramString = new a.b(true, str1, c(localJSONObject1.getString("turn_server_override")), bool, str2, str3, str4, paramString, localLinkedList);
          break;
        }
        label322: i += 1;
      }
    }
    catch (org.json.JSONException paramString)
    {
      paramString = null;
      return paramString;
    }
    catch (java.io.IOException paramString)
    {
      label340: 
      while (true)
      {
        paramString = null;
        continue;
      }
    }
  }

  public void a()
  {
    if (c.e());
    itman.Vidofilm.b localb;
    String str;
    do
    {
      return;
      localb = itman.Vidofilm.b.a(this.a);
      str = localb.p();
    }
    while (str == null);
    itman.Vidofilm.c.b localb1 = (itman.Vidofilm.c.b)itman.Vidofilm.c.a.a().a(itman.Vidofilm.c.b.class);
    f localf = new f();
    localf.c(str);
    localf.b(localb.q());
    localf.a(localb.k());
    localb1.a(localf).a(new e.d(localb)
    {
      public void onFailure(e.b<com.google.a.l> paramb, Throwable paramThrowable)
      {
      }

      public void onResponse(e.b<com.google.a.l> paramb, e.l<com.google.a.l> paraml)
      {
        try
        {
          if (paraml.b())
          {
            this.a.f(null);
            itman.Vidofilm.d.a.a(e.this.a).a(1, null);
            return;
          }
          if (paraml.a() == 401)
          {
            itman.Vidofilm.d.d.a(e.this.a).a(true);
            return;
          }
        }
        catch (Exception paramb)
        {
        }
      }
    });
  }

  public void a(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    v localv = new v();
    localv.b(paramString1);
    localv.c(paramString2);
    localv.a(paramString3);
    localv.d(paramString4);
    itman.Vidofilm.b.a(this.a).a(localv);
    itman.Vidofilm.d.d.a(this.a).a(true);
  }

  public void a(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6)
  {
    if (c.e())
      return;
    FileLog.e("VidogramwebRTC : answerNewCall");
    new e(this.a).a();
    itman.Vidofilm.b.a(this.a).g(paramString1);
    itman.Vidofilm.b.a(this.a).f(c.d);
    paramString2 = a(paramString1, null, paramString2, true, true, paramString6);
    c.a(paramString2).a(a(false, paramString1, false, paramString3, paramString4, paramString5));
    this.a.startActivity(paramString2);
  }

  public void a(String paramString1, String paramString2, String paramString3, boolean paramBoolean, String paramString4)
  {
    if (c.e())
      return;
    new e(this.a).a();
    itman.Vidofilm.b.a(this.a).g(paramString1);
    itman.Vidofilm.b.a(this.a).f(c.d);
    paramString1 = a(paramString1, paramString2, paramString3, true, paramBoolean, paramString4);
    c.a(paramString1).a(a(paramString2));
    this.a.startActivity(paramString1);
  }

  public void a(TLRPC.User paramUser)
  {
    try
    {
      new e(this.a).a();
      if (c.e())
      {
        paramUser = new Intent(this.a, CallActivity.class);
        paramUser.setFlags(268435456);
        this.a.startActivity(paramUser);
        return;
      }
      this.b = 0;
      Intent localIntent = a("", "", paramUser.id + "", false, true, paramUser.phone);
      c.a(localIntent);
      a(paramUser.id, localIntent);
      this.a.startActivity(localIntent);
      return;
    }
    catch (Exception paramUser)
    {
      FileLog.e("VidogramwebRTC : " + paramUser.getMessage() + "//1");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.e
 * JD-Core Version:    0.6.0
 */