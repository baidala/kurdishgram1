package org.vidogram.VidogramUi.WebRTC;

import android.util.Log;
import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver;
import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import org.json.JSONException;
import org.json.JSONObject;
import org.vidogram.VidogramUi.WebRTC.b.a;
import org.vidogram.VidogramUi.WebRTC.b.a.a;
import org.vidogram.VidogramUi.WebRTC.b.b;
import org.vidogram.messenger.FileLog;

public class f
{
  private final a a;
  private final b b;
  private WebSocketConnection c;
  private c d;
  private String e;
  private String f;
  private String g;
  private b h;
  private final Object i = new Object();
  private boolean j;
  private final LinkedList<String> k;

  public f(b paramb, a parama)
  {
    this.b = paramb;
    this.a = parama;
    this.f = null;
    this.g = null;
    this.k = new LinkedList();
    this.h = b.b;
  }

  private void a(String paramString1, String paramString2, String paramString3)
  {
    paramString1 = paramString1 + "/" + this.f + "/" + this.g;
    Log.d("WSChannelRTCClient", "WS " + paramString2 + " : " + paramString1 + " : " + paramString3);
    new a(paramString2, paramString1, paramString3, new a.a()
    {
      public void a(String paramString)
      {
      }

      public void b(String paramString)
      {
      }
    }).a();
  }

  private void b()
  {
    if (!this.b.c())
      throw new IllegalStateException("WebSocket method is not called on valid thread");
  }

  private void d(String paramString)
  {
    Log.e("WSChannelRTCClient", paramString);
    this.b.execute(new Runnable(paramString)
    {
      public void run()
      {
        if (f.a(f.this) != f.b.f)
        {
          f.a(f.this, f.b.f);
          f.b(f.this).c(this.a);
        }
      }
    });
  }

  public b a()
  {
    return this.h;
  }

  public void a(String paramString1, String paramString2)
  {
    b();
    this.f = paramString1;
    this.g = paramString2;
    if (this.h != b.c)
    {
      Log.w("WSChannelRTCClient", "WebSocket register() in state " + this.h);
      return;
    }
    Log.d("WSChannelRTCClient", "Registering WebSocket for room " + paramString1 + ". ClientID: " + paramString2);
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("cmd", "register");
      localJSONObject.put("roomid", paramString1);
      localJSONObject.put("clientid", paramString2);
      Log.d("WSChannelRTCClient", "C->WSS: " + localJSONObject.toString());
      this.c.sendTextMessage(localJSONObject.toString());
      this.h = b.d;
      paramString1 = this.k.iterator();
      while (paramString1.hasNext())
        c((String)paramString1.next());
    }
    catch (JSONException paramString1)
    {
      d("WebSocket register JSON error: " + paramString1.getMessage());
      return;
    }
    this.k.clear();
  }

  public void a(b paramb)
  {
    this.h = paramb;
  }

  public void a(boolean paramBoolean)
  {
    b();
    Log.d("WSChannelRTCClient", "Disconnect WebSocket. State: " + this.h);
    if (this.h == b.d)
    {
      c("{\"type\": \"bye\"}");
      this.h = b.c;
    }
    if ((this.h == b.c) || (this.h == b.f))
    {
      this.c.disconnect();
      this.h = b.e;
      if (!paramBoolean);
    }
    synchronized (this.i)
    {
      paramBoolean = this.j;
      if (!paramBoolean);
      try
      {
        c("{\"type\": \"bye\"}");
        this.i.wait(1000L);
        Log.d("WSChannelRTCClient", "Disconnecting WebSocket done.");
        return;
      }
      catch (InterruptedException localInterruptedException)
      {
        Log.e("WSChannelRTCClient", "Wait error: " + localInterruptedException.toString());
      }
    }
  }

  public boolean a(String paramString)
  {
    if ((this.c != null) && (this.c.isConnected()))
      return false;
    a(paramString, "DELETE", "");
    this.c.reconnect();
    this.h = b.a;
    return true;
  }

  public void b(String paramString)
  {
    b();
    if (this.h != b.b)
    {
      FileLog.e("WSChannelRTCClientWebSocket is already connected.");
      return;
    }
    this.e = paramString;
    this.j = false;
    this.c = new WebSocketConnection();
    this.d = new c(null);
    try
    {
      this.c.connect(new URI(this.e), this.d);
      return;
    }
    catch (URISyntaxException paramString)
    {
      d("URI error: " + paramString.getMessage());
      return;
    }
    catch (WebSocketException paramString)
    {
      d("WebSocket connection error: " + paramString.getMessage());
    }
  }

  public void c(String paramString)
  {
    b();
    switch (3.a[this.h.ordinal()])
    {
    default:
      return;
    case 1:
      this.k.add(paramString);
    case 2:
      Log.d("WSChannelRTCClient", "WS ACC: " + paramString);
      this.k.add(paramString);
      return;
    case 3:
    case 4:
      this.k.add(paramString);
      Log.e("WSChannelRTCClient", "WebSocket send() in error or closed state : " + paramString);
      return;
    case 5:
    }
    JSONObject localJSONObject = new JSONObject();
    try
    {
      localJSONObject.put("cmd", "send");
      localJSONObject.put("msg", paramString);
      paramString = localJSONObject.toString();
      Log.d("WSChannelRTCClient", "C->WSS: " + paramString);
      this.c.sendTextMessage(paramString);
      return;
    }
    catch (JSONException paramString)
    {
      d("WebSocket send JSON error: " + paramString.getMessage());
    }
  }

  public static abstract interface a
  {
    public abstract void b(String paramString);

    public abstract void c();

    public abstract void c(String paramString);

    public abstract void d();
  }

  public static enum b
  {
  }

  private class c
    implements WebSocket.WebSocketConnectionObserver
  {
    private c()
    {
    }

    public void onBinaryMessage(byte[] paramArrayOfByte)
    {
    }

    public void onClose(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification arg1, String paramString)
    {
      Log.d("WSChannelRTCClient", "WebSocket connection closed. Code: " + ??? + ". Reason: " + paramString + ". State: " + f.a(f.this));
      synchronized (f.g(f.this))
      {
        f.a(f.this, true);
        f.g(f.this).notify();
        f.f(f.this).execute(new Runnable()
        {
          public void run()
          {
            if (f.a(f.this) != f.b.e)
            {
              f.a(f.this, f.b.e);
              f.b(f.this).c();
            }
          }
        });
        return;
      }
    }

    public void onOpen()
    {
      Log.d("WSChannelRTCClient", "WebSocket connection opened to: " + f.c(f.this));
      f.f(f.this).execute(new Runnable()
      {
        public void run()
        {
          f.a(f.this, f.b.c);
          if ((f.d(f.this) != null) && (f.e(f.this) != null))
            f.this.a(f.d(f.this), f.e(f.this));
          f.b(f.this).d();
        }
      });
    }

    public void onRawTextMessage(byte[] paramArrayOfByte)
    {
    }

    public void onTextMessage(String paramString)
    {
      Log.d("WSChannelRTCClient", "WSS->C: " + paramString);
      f.f(f.this).execute(new Runnable(paramString)
      {
        public void run()
        {
          if ((f.a(f.this) == f.b.c) || (f.a(f.this) == f.b.d))
            f.b(f.this).b(this.a);
        }
      });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.f
 * JD-Core Version:    0.6.0
 */