package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import itman.Vidofilm.glowpadbackport.GlowPadView;
import itman.Vidofilm.glowpadbackport.GlowPadView.OnTriggerListener;
import org.vidogram.VidogramUi.WebRTC.c;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.LaunchActivity;

public class b extends Fragment
{
  boolean a = false;
  private FrameLayout b;
  private GlowPadView c;
  private a d;
  private c e;

  public void onAttach(Activity paramActivity)
  {
    super.onAttach(paramActivity);
    this.d = ((a)paramActivity);
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    this.b = new FrameLayout(ApplicationLoader.applicationContext);
    this.e = c.c();
    this.c = ((GlowPadView)paramLayoutInflater.inflate(2130903072, null, false));
    this.b.addView(this.c, LayoutHelper.createFrame(-1, -2, 81));
    this.c.ping();
    this.c.setOnTriggerListener(new GlowPadView.OnTriggerListener()
    {
      public void onFinishFinalAnimation()
      {
      }

      public void onGrabbed(View paramView, int paramInt)
      {
      }

      public void onGrabbedStateChange(View paramView, int paramInt)
      {
      }

      public void onReleased(View paramView, int paramInt)
      {
      }

      public void onTrigger(View paramView, int paramInt)
      {
        switch (paramInt)
        {
        default:
        case 0:
        case 1:
        case 2:
        }
        while (true)
        {
          b.c(b.this).reset(true);
          return;
          b.a(b.this).onAcceptCall();
          continue;
          paramView = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
          paramView.setAction("com.tmessages.openchat" + Math.random() + 2147483647);
          paramView.setFlags(32768);
          paramView.putExtra("userId", b.this.getArguments().getInt("itman.Vidofilm.apprtc.Callee_ID", 0));
          b.this.getActivity().startActivity(paramView);
          b.b(b.this).F();
          continue;
          b.b(b.this).F();
        }
      }
    });
    return this.b;
  }

  public void onDestroy()
  {
    super.onDestroy();
  }

  public void onStop()
  {
    super.onStop();
  }

  public static abstract interface a
  {
    public abstract void onAcceptCall();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.b
 * JD-Core Version:    0.6.0
 */