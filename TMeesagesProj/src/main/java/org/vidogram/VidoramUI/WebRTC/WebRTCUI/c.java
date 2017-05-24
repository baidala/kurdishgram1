package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build.VERSION;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import b.a.a.a;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.webrtc.SurfaceViewRenderer;

public class c
{
  float a;
  float b;
  float c;
  float d;
  boolean e;
  boolean f;
  private SurfaceViewRenderer g;
  private a h;
  private boolean i;

  private void a(boolean paramBoolean)
  {
    Object localObject = ApplicationLoader.applicationContext;
    Context localContext = ApplicationLoader.applicationContext;
    localObject = (WindowManager)((Context)localObject).getSystemService("window");
    if ((paramBoolean) && (!this.i))
    {
      this.i = true;
      this.h.setBackgroundColor(Color.parseColor("#E61E44"));
    }
    do
      return;
    while ((paramBoolean) || (!this.i));
    this.i = false;
    this.h.setBackgroundColor(Color.parseColor("#B4E61E44"));
  }

  private void b()
  {
    Object localObject1 = ApplicationLoader.applicationContext;
    Object localObject2 = ApplicationLoader.applicationContext;
    localObject1 = (WindowManager)((Context)localObject1).getSystemService("window");
    localObject2 = new WindowManager.LayoutParams();
    DisplayMetrics localDisplayMetrics = new DisplayMetrics();
    ((WindowManager)localObject1).getDefaultDisplay().getMetrics(localDisplayMetrics);
    int j = localDisplayMetrics.widthPixels;
    int k = localDisplayMetrics.heightPixels;
    ((WindowManager.LayoutParams)localObject2).height = AndroidUtilities.dp(78.0F);
    ((WindowManager.LayoutParams)localObject2).width = AndroidUtilities.dp(78.0F);
    ((WindowManager.LayoutParams)localObject2).x = (j / 2 - ((WindowManager.LayoutParams)localObject2).width / 2);
    ((WindowManager.LayoutParams)localObject2).y = (k - ((WindowManager.LayoutParams)localObject2).height - AndroidUtilities.dp(50.0F));
    ((WindowManager.LayoutParams)localObject2).token = new Binder();
    ((WindowManager.LayoutParams)localObject2).type = 2007;
    ((WindowManager.LayoutParams)localObject2).flags = 552;
    ((WindowManager.LayoutParams)localObject2).format = -3;
    ((WindowManager.LayoutParams)localObject2).gravity = 51;
    this.h = new a(ApplicationLoader.applicationContext);
    this.h.setBackgroundColor(Color.parseColor("#B4E61E44"));
    this.h.setFocusBackgroundColor(Color.parseColor("#B4E61E44"));
    this.h.setRadius(178);
    this.h.setKeepScreenOn(true);
    this.h.setVisibility(8);
    this.h.setIconResource(2130837751);
    if (this.h.getParent() == null)
      ((WindowManager)localObject1).addView(this.h, (ViewGroup.LayoutParams)localObject2);
  }

  private void c()
  {
    Object localObject1 = ApplicationLoader.applicationContext;
    Object localObject2 = ApplicationLoader.applicationContext;
    localObject1 = (WindowManager)((Context)localObject1).getSystemService("window");
    localObject2 = new WindowManager.LayoutParams();
    ((WindowManager.LayoutParams)localObject2).height = AndroidUtilities.dp(150.0F);
    ((WindowManager.LayoutParams)localObject2).width = AndroidUtilities.dp(90.0F);
    ((WindowManager.LayoutParams)localObject2).token = new Binder();
    ((WindowManager.LayoutParams)localObject2).type = 2007;
    ((WindowManager.LayoutParams)localObject2).flags = 552;
    ((WindowManager.LayoutParams)localObject2).format = -3;
    ((WindowManager.LayoutParams)localObject2).gravity = 51;
    this.g.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (!c.this.e)
        {
          paramView = new Intent(ApplicationLoader.applicationContext, CallActivity.class);
          paramView.addFlags(268468224);
          ApplicationLoader.applicationContext.startActivity(paramView);
        }
      }
    });
    this.g.setKeepScreenOn(true);
    this.g.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        if (paramMotionEvent.getAction() == 0)
        {
          c.this.a = paramMotionEvent.getRawX();
          c.this.b = paramMotionEvent.getRawY();
          c.this.c = c.this.a;
          c.this.d = c.this.b;
          c.a(c.this).setVisibility(0);
          c.this.e = false;
        }
        label122: 
        do
        {
          return false;
          if (paramMotionEvent.getAction() != 1)
            continue;
          if (c.b(c.this))
          {
            if (!org.vidogram.VidogramUi.WebRTC.c.e())
              break label122;
            org.vidogram.VidogramUi.WebRTC.c.c().h();
          }
          while (true)
          {
            c.a(c.this).setVisibility(8);
            return false;
            c.this.a();
          }
        }
        while (paramMotionEvent.getAction() != 2);
        Object localObject1 = ApplicationLoader.applicationContext;
        Object localObject2 = ApplicationLoader.applicationContext;
        localObject1 = (WindowManager)((Context)localObject1).getSystemService("window");
        localObject2 = new DisplayMetrics();
        ((WindowManager)localObject1).getDefaultDisplay().getMetrics((DisplayMetrics)localObject2);
        int i = ((DisplayMetrics)localObject2).widthPixels;
        int j = ((DisplayMetrics)localObject2).heightPixels;
        paramView = (WindowManager.LayoutParams)paramView.getLayoutParams();
        paramView.x = (int)(paramView.x + (paramMotionEvent.getRawX() - c.this.a));
        paramView.y = (int)(paramView.y + (paramMotionEvent.getRawY() - c.this.b));
        if (paramView.x < 0)
          paramView.x = 0;
        if (paramView.y < 0)
          paramView.y = 0;
        if (paramView.x > i - paramView.width)
          paramView.x = (i - paramView.width);
        if (paramView.y > j - paramView.height)
          paramView.y = (j - paramView.height);
        if ((Math.abs(paramMotionEvent.getRawX() - c.this.c) > 50.0F) || (Math.abs(paramMotionEvent.getRawY() - c.this.d) > 50.0F))
          c.this.e = true;
        c.this.a = paramMotionEvent.getRawX();
        c.this.b = paramMotionEvent.getRawY();
        paramMotionEvent = new Rect(paramView.x, paramView.y, paramView.x + paramView.width, paramView.y + paramView.height);
        localObject2 = (WindowManager.LayoutParams)c.a(c.this).getLayoutParams();
        i = ((WindowManager.LayoutParams)localObject2).x;
        j = ((WindowManager.LayoutParams)localObject2).y;
        int k = ((WindowManager.LayoutParams)localObject2).x;
        int m = ((WindowManager.LayoutParams)localObject2).width;
        int n = ((WindowManager.LayoutParams)localObject2).y;
        if (paramMotionEvent.intersect(new Rect(i, j, k + m, ((WindowManager.LayoutParams)localObject2).height + n)))
          c.a(c.this, true);
        while (true)
        {
          ((WindowManager)localObject1).updateViewLayout(c.c(c.this), paramView);
          return false;
          c.a(c.this, false);
        }
      }
    });
    if (this.g.getParent() == null)
      ((WindowManager)localObject1).addView(this.g, (ViewGroup.LayoutParams)localObject2);
  }

  public void a()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (!Settings.canDrawOverlays(ApplicationLoader.applicationContext)));
    do
      return;
    while ((this.g == null) || (!this.g.isShown()) || (!this.f));
    try
    {
      ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).removeView(this.g);
      ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).removeView(this.h);
      this.f = false;
      return;
    }
    catch (Exception localException)
    {
    }
  }

  public void a(SurfaceViewRenderer paramSurfaceViewRenderer)
  {
    try
    {
      if ((Build.VERSION.SDK_INT >= 23) && (!Settings.canDrawOverlays(ApplicationLoader.applicationContext)))
        return;
      if (!this.f)
      {
        this.i = false;
        this.g = paramSurfaceViewRenderer;
        c();
        this.f = true;
        b();
        return;
      }
    }
    catch (Exception paramSurfaceViewRenderer)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.c
 * JD-Core Version:    0.6.0
 */