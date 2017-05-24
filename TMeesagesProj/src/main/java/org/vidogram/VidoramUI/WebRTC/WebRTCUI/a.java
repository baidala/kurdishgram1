package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.VidogramUi.WebRTC.c;
import org.vidogram.VidogramUi.WebRTC.c.a;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.voip.CheckableImageView;
import org.vidogram.ui.LaunchActivity;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SurfaceViewRenderer;

public class a extends Fragment
  implements org.vidogram.VidogramUi.WebRTC.a.d
{
  private FrameLayout a;
  private b.a.a.a b;
  private CheckableImageView c;
  private ImageView d;
  private CheckableImageView e;
  private CheckableImageView f;
  private CheckableImageView g;
  private SurfaceViewRenderer h;
  private SurfaceViewRenderer i;
  private d j;
  private d k;
  private boolean l;
  private TextView m;
  private TextView n;
  private Runnable o;
  private c p;

  private void a(boolean paramBoolean)
  {
    if (this.h == null)
      return;
    if (paramBoolean)
    {
      this.h.setVisibility(0);
      e();
      return;
    }
    this.h.setVisibility(8);
  }

  private void f()
  {
    if (this.l)
    {
      this.l = false;
      this.g.setVisibility(0);
      this.f.setVisibility(0);
      this.e.setVisibility(0);
      this.b.setVisibility(0);
      this.d.setVisibility(0);
      this.c.setVisibility(0);
      this.n.setVisibility(0);
      return;
    }
    this.g.setVisibility(8);
    this.f.setVisibility(8);
    this.e.setVisibility(8);
    this.b.setVisibility(8);
    this.d.setVisibility(8);
    this.c.setVisibility(8);
    this.n.setVisibility(8);
    this.l = true;
  }

  private void g()
  {
    this.o = new Runnable()
    {
      public void run()
      {
        long l = (System.currentTimeMillis() - a.a(a.this).I()) / 1000L;
        if (l > 3600L);
        for (String str = String.format("%d:%02d:%02d", new Object[] { Long.valueOf(l / 3600L), Long.valueOf(l % 3600L / 60L), Long.valueOf(l % 60L) }); ; str = String.format("%d:%02d", new Object[] { Long.valueOf(l / 60L), Long.valueOf(l % 60L) }))
        {
          if (a.a(a.this).I() != 0L)
            a.g(a.this).setText(str);
          a.g(a.this).postDelayed(this, 500L);
          return;
        }
      }
    };
    this.o.run();
  }

  public void a()
  {
    if (getActivity() == null)
      return;
    getActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        a.g(a.this).setVisibility(0);
        a.this.e();
      }
    });
  }

  public void a(String paramString)
  {
    getActivity().runOnUiThread(new Runnable(paramString)
    {
      public void run()
      {
        a.i(a.this).setText(this.a);
      }
    });
  }

  public void b()
  {
    if (getActivity() == null)
      return;
    getActivity().runOnUiThread(new Runnable()
    {
      public void run()
      {
        if (a.h(a.this))
          a.f(a.this);
        a.g(a.this).setVisibility(8);
        a.this.e();
      }
    });
  }

  public void c()
  {
    this.e.setChecked(true);
    this.f.setChecked(false);
    Drawable localDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("chats_actionBackground"), Theme.getColor("chats_actionPressedBackground"));
    this.f.setBackgroundDrawable(localDrawable);
    this.f.setBackgroundDrawable(localDrawable);
    this.e.setEnabled(false);
    this.f.setEnabled(false);
    a(false);
  }

  public void d()
  {
    this.e.setChecked(false);
    this.f.setBackgroundResource(2130837627);
    this.e.setBackgroundResource(2130837627);
    this.e.setEnabled(true);
    this.f.setEnabled(true);
  }

  public void e()
  {
    if ((this.i == null) || (this.h == null))
      return;
    this.k.a(0, 0, 100, 100);
    this.i.setScalingType(this.p.P());
    this.i.setMirror(false);
    if (this.p.K())
      if (LocaleController.isRTL)
      {
        this.j.a(0, 0, 25, 25);
        this.h.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        if (!this.p.O())
          break label222;
        this.h.setMirror(true);
      }
    while (true)
    {
      this.h.requestLayout();
      this.i.requestLayout();
      return;
      if (LocaleController.isRTL)
        break;
      this.j.a(72, 0, 25, 25);
      this.h.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
      break;
      if (LocaleController.isRTL)
      {
        this.j.a(0, 15, 25, 25);
        this.h.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        break;
      }
      if (LocaleController.isRTL)
        break;
      this.j.a(72, 15, 25, 25);
      this.h.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
      break;
      label222: this.h.setMirror(false);
    }
  }

  public void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    this.a = new FrameLayout(ApplicationLoader.applicationContext);
    try
    {
      if (!c.e())
        getActivity().finish();
      this.p = c.c();
      if (this.p != null)
        this.p.a(this);
      this.j = new d(ApplicationLoader.applicationContext);
      this.k = new d(ApplicationLoader.applicationContext);
      this.a.addView(this.k);
      this.a.addView(this.j);
      this.h = this.p.b;
      this.i = this.p.c;
    }
    catch (java.lang.Exception paramBundle)
    {
      try
      {
        if ((this.h != null) && (this.h.getParent() != null))
          this.j.removeView(this.h);
        if ((this.i != null) && (this.i.getParent() != null))
          this.k.removeView(this.i);
        this.k.addView(this.i);
        this.j.addView(this.h);
        label204: e();
        this.m = new TextView(ApplicationLoader.applicationContext);
        this.a.addView(this.m, LayoutHelper.createFrame(-2, -2.0F, 49, 0.0F, 10.0F, 0.0F, 0.0F));
        this.m.setTextColor(-855638017);
        this.m.setSingleLine();
        this.m.setEllipsize(TextUtils.TruncateAt.END);
        this.m.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.m.setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
        this.m.setMovementMethod(new ScrollingMovementMethod());
        this.m.setTextSize(1, 12.0F);
        this.m.setAllCaps(true);
        if (this.i != null)
          this.m.setVisibility(this.i.getVisibility());
        this.n = new TextView(ApplicationLoader.applicationContext);
        this.a.addView(this.n, LayoutHelper.createFrame(-2, -2.0F, 81, 0.0F, 10.0F, 0.0F, 150.0F));
        this.n.setTextColor(-855638017);
        this.n.setSingleLine();
        this.n.setEllipsize(TextUtils.TruncateAt.END);
        this.n.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.n.setShadowLayer(AndroidUtilities.dp(3.0F), 0.0F, AndroidUtilities.dp(0.6666667F), 1275068416);
        this.n.setTextSize(1, 12.0F);
        this.b = new b.a.a.a(ApplicationLoader.applicationContext);
        this.b.setBackgroundColor(Color.parseColor("#E61E44"));
        this.b.setFocusBackgroundColor(Color.parseColor("#e6788d"));
        this.b.setRadius(178);
        this.b.setIconResource(2130837751);
        this.a.addView(this.b, LayoutHelper.createFrame(78, 78.0F, 81, 0.0F, 0.0F, 0.0F, 68.0F));
        this.c = new CheckableImageView(ApplicationLoader.applicationContext);
        this.c.setBackgroundResource(2130837627);
        paramBundle = getResources().getDrawable(2130837759).mutate();
        paramBundle.setAlpha(204);
        this.c.setImageDrawable(paramBundle);
        this.c.setScaleType(ImageView.ScaleType.CENTER);
        paramBundle = this.c;
        if (!this.p.O())
        {
          bool = true;
          label653: paramBundle.setChecked(bool);
          this.a.addView(this.c, LayoutHelper.createFrame(38, 38.0F, 85, 0.0F, 0.0F, 16.0F, 68.0F));
          this.f = new CheckableImageView(ApplicationLoader.applicationContext);
          this.f.setBackgroundResource(2130837627);
          paramBundle = getResources().getDrawable(2130837868).mutate();
          paramBundle.setAlpha(204);
          this.f.setImageDrawable(paramBundle);
          this.f.setScaleType(ImageView.ScaleType.CENTER);
          this.f.setChecked(this.p.L());
          this.a.addView(this.f, LayoutHelper.createFrame(38, 38.0F, 83, 16.0F, 0.0F, 0.0F, 68.0F));
          this.e = new CheckableImageView(ApplicationLoader.applicationContext);
          this.e.setBackgroundResource(2130837627);
          paramBundle = getResources().getDrawable(2130837814).mutate();
          paramBundle.setAlpha(204);
          this.e.setImageDrawable(paramBundle);
          this.e.setScaleType(ImageView.ScaleType.CENTER);
          paramBundle = this.e;
          if (this.p.N())
            break label1139;
        }
        label1139: for (boolean bool = true; ; bool = false)
        {
          paramBundle.setChecked(bool);
          this.a.addView(this.e, LayoutHelper.createFrame(38, 38.0F, 83, 16.0F, 0.0F, 0.0F, 10.0F));
          if (this.p.S() == c.a.i)
            c();
          this.d = new ImageView(ApplicationLoader.applicationContext);
          paramBundle = getResources().getDrawable(2130837765).mutate();
          paramBundle.setAlpha(204);
          this.d.setImageDrawable(paramBundle);
          this.d.setScaleType(ImageView.ScaleType.CENTER);
          this.a.addView(this.d, LayoutHelper.createFrame(38, 38.0F, 81, 0.0F, 0.0F, 0.0F, 10.0F));
          this.g = new CheckableImageView(ApplicationLoader.applicationContext);
          this.g.setBackgroundResource(2130837627);
          paramBundle = getResources().getDrawable(2130837869).mutate();
          paramBundle.setAlpha(204);
          this.g.setImageDrawable(paramBundle);
          this.g.setScaleType(ImageView.ScaleType.CENTER);
          this.g.setChecked(this.p.M());
          this.a.addView(this.g, LayoutHelper.createFrame(38, 38.0F, 85, 0.0F, 0.0F, 16.0F, 10.0F));
          a(this.p.L());
          g();
          return;
          paramBundle = paramBundle;
          getActivity().finish();
          break;
          bool = false;
          break label653;
        }
      }
      catch (java.lang.Exception paramBundle)
      {
        break label204;
      }
    }
  }

  public View onCreateView(LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Bundle paramBundle)
  {
    if (!c.e())
      return this.a;
    this.b.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.a(a.this).A();
      }
    });
    this.c.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.a(a.this).B();
        paramView = a.b(a.this);
        if (!a.a(a.this).O());
        for (boolean bool = true; ; bool = false)
        {
          paramView.setChecked(bool);
          return;
        }
      }
    });
    this.d.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        paramView = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        paramView.setAction("com.tmessages.openchat" + Math.random() + 2147483647);
        paramView.setFlags(32768);
        paramView.putExtra("userId", a.this.getArguments().getInt("itman.Vidofilm.apprtc.Callee_ID", 0));
        a.this.startActivity(paramView);
        a.this.getActivity().finish();
      }
    });
    this.e.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.a(a.this).C();
        paramView = a.c(a.this);
        if (!a.a(a.this).N());
        for (boolean bool = true; ; bool = false)
        {
          paramView.setChecked(bool);
          return;
        }
      }
    });
    this.f.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.a(a.this).D();
        a.a(a.this, a.a(a.this).L());
        a.d(a.this).setChecked(a.a(a.this).L());
      }
    });
    this.g.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.a(a.this).E();
        a.e(a.this).setChecked(a.a(a.this).M());
      }
    });
    paramLayoutInflater = new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        a.f(a.this);
      }
    };
    this.i.setOnClickListener(paramLayoutInflater);
    this.h.setOnClickListener(paramLayoutInflater);
    this.a.setOnClickListener(paramLayoutInflater);
    return this.a;
  }

  public void onDestroy()
  {
    if (this.o != null)
      this.m.removeCallbacks(this.o);
    this.j.removeAllViews();
    this.k.removeAllViews();
    super.onDestroy();
  }

  public void onDestroyView()
  {
    super.onDestroyView();
  }

  public void onStart()
  {
    super.onStart();
  }

  public void onStop()
  {
    super.onStop();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.a
 * JD-Core Version:    0.6.0
 */