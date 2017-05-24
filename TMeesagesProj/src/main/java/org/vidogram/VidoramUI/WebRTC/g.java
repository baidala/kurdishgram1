package org.vidogram.VidogramUi.WebRTC;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vidogram.VidogramUi.WebRTC.a.a.b;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.SessionDescription.Type;

public class g
  implements org.vidogram.VidogramUi.WebRTC.a.a, f.a
{
  public boolean a;
  private final org.vidogram.VidogramUi.WebRTC.b.b b;
  private boolean c;
  private Context d;
  private org.vidogram.VidogramUi.WebRTC.a.a.a e;
  private f f;
  private a g;
  private String h;
  private ArrayList<String> i = new ArrayList();
  private Runnable j;
  private Handler k;
  private Runnable l;
  private Handler m;
  private a.b n;

  public g(Context paramContext, org.vidogram.VidogramUi.WebRTC.a.a.a parama, org.vidogram.VidogramUi.WebRTC.b.b paramb)
  {
    this.e = parama;
    this.d = paramContext;
    this.b = paramb;
    this.g = a.a;
    paramb.a();
  }

  private void a(b paramb, String paramString1, String paramString2)
  {
    if (paramString2 != null)
      new StringBuilder().append(paramString1).append(". Message: ").append(paramString2).toString();
    new org.vidogram.VidogramUi.WebRTC.b.a("POST", paramString1, paramString2, new org.vidogram.VidogramUi.WebRTC.b.a.a(paramb)
    {
      public void a(String paramString)
      {
        g.b(g.this, "GAE POST error: " + paramString);
      }

      public void b(String paramString)
      {
        if (this.a == g.b.a);
        try
        {
          paramString = new JSONObject(paramString).getString("result");
          if (!paramString.equals("SUCCESS"))
            g.b(g.this, "GAE POST error: " + paramString);
          return;
        }
        catch (JSONException paramString)
        {
          g.b(g.this, "GAE POST JSON error: " + paramString.toString());
        }
      }
    }).a();
  }

  private JSONObject b(IceCandidate paramIceCandidate)
  {
    JSONObject localJSONObject = new JSONObject();
    b(localJSONObject, "label", Integer.valueOf(paramIceCandidate.sdpMLineIndex));
    b(localJSONObject, "id", paramIceCandidate.sdpMid);
    b(localJSONObject, "candidate", paramIceCandidate.sdp);
    return localJSONObject;
  }

  private static void b(JSONObject paramJSONObject, String paramString, Object paramObject)
  {
    try
    {
      paramJSONObject.put(paramString, paramObject);
      return;
    }
    catch (JSONException paramJSONObject)
    {
    }
    throw new RuntimeException(paramJSONObject);
  }

  private void b(a.b paramb)
  {
    this.g = a.a;
    this.f = new f(this.b, this);
    FileLog.d("WSRTCClientRoom connection start.");
    this.n = paramb;
    this.c = paramb.b;
    this.h = c(paramb);
    this.g = a.b;
    this.f.b(paramb.d);
    this.f.a(paramb.h, paramb.c);
    FileLog.d("WSRTCClientsocket connection start.");
    this.e.j();
  }

  private String c(a.b paramb)
  {
    return Uri.parse("https://vidogram.me:8443") + "/" + "message" + "/" + paramb.h + "/" + paramb.c;
  }

  private void d(String paramString)
  {
    FileLog.d("WSRTCClientDisconnect. Room state: " + this.g);
    if (this.g == a.b)
    {
      itman.Vidofilm.b.a(this.d).f(paramString);
      new e(ApplicationLoader.applicationContext).a();
    }
    this.g = a.c;
    if (this.f != null)
      this.f.a(true);
  }

  private void e()
  {
    Object localObject = new ArrayList();
    ((ArrayList)localObject).addAll(this.i);
    this.i.clear();
    localObject = ((ArrayList)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      String str = (String)((Iterator)localObject).next();
      this.f.c(str);
    }
  }

  private void e(String paramString)
  {
    FileLog.e("WSRTCClient" + paramString);
    this.b.execute(new Runnable(paramString)
    {
      public void run()
      {
        if (g.b(g.this) != g.a.d)
        {
          g.a(g.this, g.a.d);
          g.a(g.this).a(this.a);
        }
      }
    });
  }

  private void f()
  {
    this.j = new Runnable()
    {
      public void run()
      {
        g.a(g.this).a(false);
      }
    };
    this.k = new Handler();
    this.k.postDelayed(this.j, 8000L);
  }

  private void g()
  {
    JSONObject localJSONObject = new JSONObject();
    b(localJSONObject, "type", "ready");
    this.f.c(localJSONObject.toString());
    f();
  }

  IceCandidate a(JSONObject paramJSONObject)
  {
    return new IceCandidate(paramJSONObject.getString("id"), paramJSONObject.getInt("label"), paramJSONObject.getString("candidate"));
  }

  public void a()
  {
    if (this.n.i)
      return;
    this.b.execute(new Runnable()
    {
      public void run()
      {
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "type", "stop_remote");
        g.f(g.this).c(localJSONObject.toString());
      }
    });
  }

  public void a(String paramString)
  {
    this.b.execute(new Runnable(paramString)
    {
      public void run()
      {
        g.a(g.this, this.a);
      }
    });
    this.b.b();
  }

  public void a(a.b paramb)
  {
    this.b.execute(new Runnable(paramb)
    {
      public void run()
      {
        g.a(g.this, this.a);
      }
    });
  }

  public void a(IceCandidate paramIceCandidate)
  {
    this.b.execute(new Runnable(paramIceCandidate)
    {
      public void run()
      {
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "type", "candidate");
        g.a(localJSONObject, "label", Integer.valueOf(this.a.sdpMLineIndex));
        g.a(localJSONObject, "id", this.a.sdpMid);
        g.a(localJSONObject, "candidate", this.a.sdp);
        if (g.g(g.this))
        {
          if (g.b(g.this) != g.a.b)
          {
            g.b(g.this, "Sending ICE candidate in non connected state.");
            return;
          }
          g.c(g.this).add(localJSONObject.toString());
          if (g.this.a)
          {
            g.d(g.this);
            return;
          }
          g.a(g.this, g.b.a, g.e(g.this), localJSONObject.toString());
          return;
        }
        g.f(g.this).c(localJSONObject.toString());
      }
    });
  }

  public void a(SessionDescription paramSessionDescription)
  {
    this.b.execute(new Runnable(paramSessionDescription)
    {
      public void run()
      {
        if (g.b(g.this) != g.a.b)
        {
          g.b(g.this, "Sending offer SDP in non connected state.");
          return;
        }
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "sdp", this.a.description);
        g.a(localJSONObject, "type", "offer");
        FileLog.e("WSRTCClientoffer sdp2");
        g.c(g.this).add(localJSONObject.toString());
        if (g.this.a)
        {
          g.d(g.this);
          return;
        }
        g.a(g.this, g.b.a, g.e(g.this), localJSONObject.toString());
      }
    });
  }

  public void a(IceCandidate[] paramArrayOfIceCandidate)
  {
    this.b.execute(new Runnable(paramArrayOfIceCandidate)
    {
      public void run()
      {
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "type", "remove-candidates");
        JSONArray localJSONArray = new JSONArray();
        IceCandidate[] arrayOfIceCandidate = this.a;
        int j = arrayOfIceCandidate.length;
        int i = 0;
        while (i < j)
        {
          IceCandidate localIceCandidate = arrayOfIceCandidate[i];
          localJSONArray.put(g.a(g.this, localIceCandidate));
          i += 1;
        }
        g.a(localJSONObject, "candidates", localJSONArray);
        if (g.g(g.this))
        {
          if (g.b(g.this) != g.a.b)
          {
            g.b(g.this, "Sending ICE candidate removals in non connected state.");
            return;
          }
          g.c(g.this).add(localJSONObject.toString());
          if (g.this.a)
          {
            g.d(g.this);
            return;
          }
          g.a(g.this, g.b.a, g.e(g.this), localJSONObject.toString());
          return;
        }
        g.f(g.this).c(localJSONObject.toString());
      }
    });
  }

  public void b()
  {
    if (this.n.i)
      return;
    this.b.execute(new Runnable()
    {
      public void run()
      {
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "type", "start_remote");
        g.f(g.this).c(localJSONObject.toString());
      }
    });
  }

  public void b(String paramString)
  {
    int i1 = 0;
    if (this.f.a() != f.b.d)
      FileLog.e("WSRTCClientGot WebSocket message in non registered state.");
    Object localObject2;
    do
      while (true)
      {
        return;
        Object localObject1;
        try
        {
          localObject2 = new JSONObject(paramString);
          localObject1 = ((JSONObject)localObject2).getString("msg");
          if (localObject1 != null)
            FileLog.e("WSRTCClient" + (String)localObject1);
          localObject2 = ((JSONObject)localObject2).optString("error");
          if (((String)localObject1).length() <= 0)
            break;
          this.a = true;
          if ((!this.c) && (this.k != null) && (this.j != null))
            this.k.removeCallbacks(this.j);
          localObject1 = new JSONObject((String)localObject1);
          localObject2 = ((JSONObject)localObject1).optString("type");
          if (((String)localObject2).equals("ready"))
          {
            this.e.k();
            e();
            this.n.i = false;
            return;
          }
        }
        catch (JSONException paramString)
        {
          e("WebSocket message JSON parsing error: " + paramString.toString());
          return;
        }
        if (((String)localObject2).equals("candidate"))
        {
          this.e.a(a((JSONObject)localObject1));
          return;
        }
        if (((String)localObject2).equals("remove-candidates"))
        {
          paramString = ((JSONObject)localObject1).getJSONArray("candidates");
          localObject1 = new IceCandidate[paramString.length()];
          while (i1 < paramString.length())
          {
            localObject1[i1] = a(paramString.getJSONObject(i1));
            i1 += 1;
          }
          this.e.a(localObject1);
          return;
        }
        if (((String)localObject2).equals("answer"))
        {
          if (this.c)
          {
            paramString = new SessionDescription(SessionDescription.Type.fromCanonicalForm((String)localObject2), ((JSONObject)localObject1).getString("sdp"));
            this.e.a(paramString);
            return;
          }
          e("Received answer for call initiator: " + paramString);
          return;
        }
        if (((String)localObject2).equals("offer"))
        {
          if (!this.c)
          {
            FileLog.e("WSRTCClientoffer sdp1");
            paramString = new SessionDescription(SessionDescription.Type.fromCanonicalForm((String)localObject2), ((JSONObject)localObject1).getString("sdp"));
            this.e.a(paramString);
            return;
          }
          e("Received offer for call receiver: " + paramString);
          return;
        }
        if (((String)localObject2).equals("bye"))
        {
          this.e.a(true);
          return;
        }
        if (((String)localObject2).equals("stop_remote"))
        {
          this.e.l();
          return;
        }
        if (((String)localObject2).equals("start_remote"))
        {
          this.e.m();
          return;
        }
        if (((String)localObject2).equals("stop_local"))
        {
          this.e.n();
          return;
        }
        if (((String)localObject2).equals("start_local"))
        {
          this.e.o();
          return;
        }
        if (((String)localObject2).equals("hold_start"))
        {
          this.e.p();
          return;
        }
        if (!((String)localObject2).equals("hold_stop"))
          continue;
        this.e.q();
        return;
      }
    while ((localObject2 == null) || (((String)localObject2).length() <= 0) || (((String)localObject2).equals("Duplicated registration")));
    if (((String)localObject2).equals("Client not registered"))
      this.f.a(f.b.c);
    this.f.a(this.n.h, this.n.c);
  }

  public void b(SessionDescription paramSessionDescription)
  {
    this.b.execute(new Runnable(paramSessionDescription)
    {
      public void run()
      {
        JSONObject localJSONObject = new JSONObject();
        g.a(localJSONObject, "sdp", this.a.description);
        g.a(localJSONObject, "type", "answer");
        g.f(g.this).c(localJSONObject.toString());
      }
    });
  }

  public void c()
  {
    if ((this.n != null) && (c.e()))
    {
      if (this.m != null)
        this.m.removeCallbacks(this.l);
      this.l = new Runnable()
      {
        public void run()
        {
          if (g.f(g.this).a(g.h(g.this).e))
            g.this.c();
        }
      };
      this.m = new Handler();
      this.m.postDelayed(this.l, 2000L);
    }
  }

  public void c(String paramString)
  {
    e("WebSocket error: " + paramString);
  }

  public void d()
  {
    if ((!this.c) && (!this.n.i))
      g();
    this.e.r();
  }

  private static enum a
  {
  }

  private static enum b
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.g
 * JD-Core Version:    0.6.0
 */