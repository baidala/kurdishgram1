package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.content.Context;
import com.google.firebase.crash.FirebaseCrash;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.VidogramUi.WebRTC.a.c;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer.I420Frame;

public class e extends SurfaceViewRenderer
{
  private c a;
  private Timer b;
  private boolean c;
  private long d;

  public e(Context paramContext, c paramc)
  {
    super(paramContext);
    this.a = paramc;
  }

  private void a()
  {
    try
    {
      this.b = new Timer();
      this.b.schedule(new TimerTask()
      {
        public void run()
        {
          try
          {
            if (System.currentTimeMillis() > e.a(e.this) + 2500L)
            {
              e.a(e.this, false);
              e.b(e.this).cancel();
              e.c(e.this).a();
            }
            return;
          }
          catch (Exception localException)
          {
          }
        }
      }
      , 1000L, 2000L);
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public void renderFrame(VideoRenderer.I420Frame paramI420Frame)
  {
    super.renderFrame(paramI420Frame);
    try
    {
      this.d = System.currentTimeMillis();
      if (!this.c)
      {
        this.a.b();
        this.c = true;
        a();
      }
      return;
    }
    catch (Exception paramI420Frame)
    {
      FirebaseCrash.a(paramI420Frame);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.e
 * JD-Core Version:    0.6.0
 */