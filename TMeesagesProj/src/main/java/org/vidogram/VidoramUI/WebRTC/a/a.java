package org.vidogram.VidogramUi.WebRTC.a;

import java.util.List;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection.IceServer;
import org.webrtc.SessionDescription;

public abstract interface a
{
  public abstract void a();

  public abstract void a(String paramString);

  public abstract void a(b paramb);

  public abstract void a(IceCandidate paramIceCandidate);

  public abstract void a(SessionDescription paramSessionDescription);

  public abstract void a(IceCandidate[] paramArrayOfIceCandidate);

  public abstract void b();

  public abstract void b(SessionDescription paramSessionDescription);

  public static abstract interface a
  {
    public abstract void a(String paramString);

    public abstract void a(IceCandidate paramIceCandidate);

    public abstract void a(SessionDescription paramSessionDescription);

    public abstract void a(boolean paramBoolean);

    public abstract void a(IceCandidate[] paramArrayOfIceCandidate);

    public abstract void j();

    public abstract void k();

    public abstract void l();

    public abstract void m();

    public abstract void n();

    public abstract void o();

    public abstract void p();

    public abstract void q();

    public abstract void r();
  }

  public static class b
  {
    public final List<PeerConnection.IceServer> a;
    public final boolean b;
    public final String c;
    public final String d;
    public final String e;
    public final SessionDescription f;
    public final List<IceCandidate> g;
    public final String h;
    public boolean i;

    public b(boolean paramBoolean1, String paramString1, List<PeerConnection.IceServer> paramList, boolean paramBoolean2, String paramString2, String paramString3, String paramString4, SessionDescription paramSessionDescription, List<IceCandidate> paramList1)
    {
      this.a = paramList;
      this.b = paramBoolean2;
      this.e = paramString4;
      this.c = paramString2;
      this.d = paramString3;
      this.f = paramSessionDescription;
      this.g = paramList1;
      this.h = paramString1;
      this.i = paramBoolean1;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.a.a
 * JD-Core Version:    0.6.0
 */