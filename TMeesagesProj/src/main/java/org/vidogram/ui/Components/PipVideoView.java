package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collection;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.vidogram.ui.ActionBar.ActionBar;

public class PipVideoView
{
  private View controlsView;
  private DecelerateInterpolator decelerateInterpolator;
  private Activity parentActivity;
  private EmbedBottomSheet parentSheet;
  private SharedPreferences preferences;
  private int videoHeight;
  private int videoWidth;
  private WindowManager.LayoutParams windowLayoutParams;
  private WindowManager windowManager;
  private FrameLayout windowView;

  private void animateToBoundsMaybe()
  {
    int i = getSideCoord(true, 0, 0.0F, this.videoWidth);
    int n = getSideCoord(true, 1, 0.0F, this.videoWidth);
    int j = getSideCoord(false, 0, 0.0F, this.videoHeight);
    int k = getSideCoord(false, 1, 0.0F, this.videoHeight);
    Object localObject2 = null;
    Object localObject3 = null;
    Object localObject4 = null;
    Object localObject1 = null;
    SharedPreferences.Editor localEditor = this.preferences.edit();
    int m = AndroidUtilities.dp(20.0F);
    if ((Math.abs(i - this.windowLayoutParams.x) <= m) || ((this.windowLayoutParams.x < 0) && (this.windowLayoutParams.x > -this.videoWidth / 4)))
    {
      if (0 == 0)
        localObject1 = new ArrayList();
      localEditor.putInt("sidex", 0);
      if (this.windowView.getAlpha() != 1.0F)
        ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
      ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { i }));
      i = 0;
      localObject2 = localObject1;
      if (i == 0)
      {
        if ((Math.abs(j - this.windowLayoutParams.y) > m) && (this.windowLayoutParams.y > ActionBar.getCurrentActionBarHeight()))
          break label693;
        localObject2 = localObject1;
        if (localObject1 == null)
          localObject2 = new ArrayList();
        localEditor.putInt("sidey", 0);
        ((ArrayList)localObject2).add(ObjectAnimator.ofInt(this, "y", new int[] { j }));
        localObject1 = localObject2;
      }
    }
    while (true)
    {
      localEditor.commit();
      localObject2 = localObject1;
      if (localObject2 != null)
      {
        if (this.decelerateInterpolator == null)
          this.decelerateInterpolator = new DecelerateInterpolator();
        localObject1 = new AnimatorSet();
        ((AnimatorSet)localObject1).setInterpolator(this.decelerateInterpolator);
        ((AnimatorSet)localObject1).setDuration(150L);
        if (i != 0)
        {
          ((ArrayList)localObject2).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 0.0F }));
          ((AnimatorSet)localObject1).addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              PipVideoView.this.parentSheet.destroy();
            }
          });
        }
        ((AnimatorSet)localObject1).playTogether((Collection)localObject2);
        ((AnimatorSet)localObject1).start();
      }
      return;
      if ((Math.abs(n - this.windowLayoutParams.x) <= m) || ((this.windowLayoutParams.x > AndroidUtilities.displaySize.x - this.videoWidth) && (this.windowLayoutParams.x < AndroidUtilities.displaySize.x - this.videoWidth / 4 * 3)))
      {
        localObject1 = localObject3;
        if (0 == 0)
          localObject1 = new ArrayList();
        localEditor.putInt("sidex", 1);
        if (this.windowView.getAlpha() != 1.0F)
          ((ArrayList)localObject1).add(ObjectAnimator.ofFloat(this.windowView, "alpha", new float[] { 1.0F }));
        ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { n }));
        i = 0;
        break;
      }
      if (this.windowView.getAlpha() != 1.0F)
      {
        localObject1 = localObject4;
        if (0 == 0)
          localObject1 = new ArrayList();
        if (this.windowLayoutParams.x < 0)
          ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { -this.videoWidth }));
        while (true)
        {
          i = 1;
          break;
          ((ArrayList)localObject1).add(ObjectAnimator.ofInt(this, "x", new int[] { AndroidUtilities.displaySize.x }));
        }
      }
      localEditor.putFloat("px", (this.windowLayoutParams.x - i) / (n - i));
      localEditor.putInt("sidex", 2);
      i = 0;
      localObject1 = localObject2;
      break;
      label693: if (Math.abs(k - this.windowLayoutParams.y) <= m)
      {
        localObject2 = localObject1;
        if (localObject1 == null)
          localObject2 = new ArrayList();
        localEditor.putInt("sidey", 1);
        ((ArrayList)localObject2).add(ObjectAnimator.ofInt(this, "y", new int[] { k }));
        localObject1 = localObject2;
        continue;
      }
      localEditor.putFloat("py", (this.windowLayoutParams.y - j) / (k - j));
      localEditor.putInt("sidey", 2);
    }
  }

  public static Rect getPipRect(float paramFloat)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
    int n = localSharedPreferences.getInt("sidex", 1);
    int i1 = localSharedPreferences.getInt("sidey", 0);
    float f1 = localSharedPreferences.getFloat("px", 0.0F);
    float f2 = localSharedPreferences.getFloat("py", 0.0F);
    int k;
    int i;
    if (paramFloat > 1.0F)
    {
      k = AndroidUtilities.dp(192.0F);
      i = (int)(k / paramFloat);
    }
    while (true)
    {
      return new Rect(getSideCoord(true, n, f1, k), getSideCoord(false, i1, f2, i), k, i);
      int j = AndroidUtilities.dp(192.0F);
      int m = (int)(j * paramFloat);
    }
  }

  private static int getSideCoord(boolean paramBoolean, int paramInt1, float paramFloat, int paramInt2)
  {
    if (paramBoolean)
    {
      paramInt2 = AndroidUtilities.displaySize.x - paramInt2;
      if (paramInt1 != 0)
        break label53;
      paramInt1 = AndroidUtilities.dp(10.0F);
    }
    while (true)
    {
      paramInt2 = paramInt1;
      if (!paramBoolean)
        paramInt2 = paramInt1 + ActionBar.getCurrentActionBarHeight();
      return paramInt2;
      paramInt2 = AndroidUtilities.displaySize.y - paramInt2 - ActionBar.getCurrentActionBarHeight();
      break;
      label53: if (paramInt1 == 1)
      {
        paramInt1 = paramInt2 - AndroidUtilities.dp(10.0F);
        continue;
      }
      paramInt1 = Math.round((paramInt2 - AndroidUtilities.dp(20.0F)) * paramFloat) + AndroidUtilities.dp(10.0F);
    }
  }

  public void close()
  {
    try
    {
      this.windowManager.removeView(this.windowView);
      label13: this.parentSheet = null;
      this.parentActivity = null;
      return;
    }
    catch (Exception localException)
    {
      break label13;
    }
  }

  public int getX()
  {
    return this.windowLayoutParams.x;
  }

  public int getY()
  {
    return this.windowLayoutParams.y;
  }

  public void onConfigurationChanged()
  {
    int i = this.preferences.getInt("sidex", 1);
    int j = this.preferences.getInt("sidey", 0);
    float f1 = this.preferences.getFloat("px", 0.0F);
    float f2 = this.preferences.getFloat("py", 0.0F);
    this.windowLayoutParams.x = getSideCoord(true, i, f1, this.videoWidth);
    this.windowLayoutParams.y = getSideCoord(false, j, f2, this.videoHeight);
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }

  public void setX(int paramInt)
  {
    this.windowLayoutParams.x = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }

  public void setY(int paramInt)
  {
    this.windowLayoutParams.y = paramInt;
    this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
  }

  public TextureView show(Activity paramActivity, EmbedBottomSheet paramEmbedBottomSheet, View paramView, float paramFloat, int paramInt)
  {
    this.windowView = new FrameLayout(paramActivity)
    {
      private boolean dragging;
      private float startX;
      private float startY;

      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        float f1 = paramMotionEvent.getRawX();
        float f2 = paramMotionEvent.getRawY();
        if (paramMotionEvent.getAction() == 0)
        {
          this.startX = f1;
          this.startY = f2;
        }
        do
          return super.onInterceptTouchEvent(paramMotionEvent);
        while ((paramMotionEvent.getAction() != 2) || (this.dragging) || ((Math.abs(this.startX - f1) < AndroidUtilities.getPixelsInCM(0.3F, true)) && (Math.abs(this.startY - f2) < AndroidUtilities.getPixelsInCM(0.3F, false))));
        this.dragging = true;
        this.startX = f1;
        this.startY = f2;
        ((ViewParent)PipVideoView.this.controlsView).requestDisallowInterceptTouchEvent(true);
        return true;
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        float f1 = 1.0F;
        if (!this.dragging)
          return false;
        float f2 = paramMotionEvent.getRawX();
        float f3 = paramMotionEvent.getRawY();
        int i;
        if (paramMotionEvent.getAction() == 2)
        {
          float f4 = this.startX;
          float f5 = this.startY;
          paramMotionEvent = PipVideoView.this.windowLayoutParams;
          paramMotionEvent.x = (int)(f2 - f4 + paramMotionEvent.x);
          paramMotionEvent = PipVideoView.this.windowLayoutParams;
          paramMotionEvent.y = (int)(f3 - f5 + paramMotionEvent.y);
          i = PipVideoView.this.videoWidth / 2;
          if (PipVideoView.this.windowLayoutParams.x < -i)
          {
            PipVideoView.this.windowLayoutParams.x = (-i);
            if (PipVideoView.this.windowLayoutParams.x >= 0)
              break label321;
            f1 = 1.0F + PipVideoView.this.windowLayoutParams.x / i * 0.5F;
            label163: if (PipVideoView.this.windowView.getAlpha() != f1)
              PipVideoView.this.windowView.setAlpha(f1);
            if (PipVideoView.this.windowLayoutParams.y >= -0)
              break label393;
            PipVideoView.this.windowLayoutParams.y = (-0);
            label216: PipVideoView.this.windowManager.updateViewLayout(PipVideoView.this.windowView, PipVideoView.this.windowLayoutParams);
            this.startX = f2;
            this.startY = f3;
          }
        }
        while (true)
        {
          return true;
          if (PipVideoView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width + i)
            break;
          PipVideoView.this.windowLayoutParams.x = (AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width + i);
          break;
          label321: if (PipVideoView.this.windowLayoutParams.x <= AndroidUtilities.displaySize.x - PipVideoView.this.windowLayoutParams.width)
            break label163;
          f1 = 1.0F - (PipVideoView.this.windowLayoutParams.x - AndroidUtilities.displaySize.x + PipVideoView.this.windowLayoutParams.width) / i * 0.5F;
          break label163;
          label393: if (PipVideoView.this.windowLayoutParams.y <= AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height + 0)
            break label216;
          PipVideoView.this.windowLayoutParams.y = (0 + (AndroidUtilities.displaySize.y - PipVideoView.this.windowLayoutParams.height));
          break label216;
          if (paramMotionEvent.getAction() != 1)
            continue;
          this.dragging = false;
          PipVideoView.this.animateToBoundsMaybe();
        }
      }

      public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
      {
        super.requestDisallowInterceptTouchEvent(paramBoolean);
      }
    };
    if (paramFloat > 1.0F)
    {
      this.videoWidth = AndroidUtilities.dp(192.0F);
      this.videoHeight = (int)(this.videoWidth / paramFloat);
    }
    while (true)
    {
      AspectRatioFrameLayout localAspectRatioFrameLayout = new AspectRatioFrameLayout(paramActivity);
      localAspectRatioFrameLayout.setAspectRatio(paramFloat, paramInt);
      this.windowView.addView(localAspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
      TextureView localTextureView = new TextureView(paramActivity);
      localAspectRatioFrameLayout.addView(localTextureView, LayoutHelper.createFrame(-1, -1.0F));
      this.controlsView = paramView;
      this.windowView.addView(this.controlsView, LayoutHelper.createFrame(-1, -1.0F));
      this.windowManager = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window"));
      this.preferences = ApplicationLoader.applicationContext.getSharedPreferences("pipconfig", 0);
      paramInt = this.preferences.getInt("sidex", 1);
      int i = this.preferences.getInt("sidey", 0);
      paramFloat = this.preferences.getFloat("px", 0.0F);
      float f = this.preferences.getFloat("py", 0.0F);
      try
      {
        this.windowLayoutParams = new WindowManager.LayoutParams();
        this.windowLayoutParams.width = this.videoWidth;
        this.windowLayoutParams.height = this.videoHeight;
        this.windowLayoutParams.x = getSideCoord(true, paramInt, paramFloat, this.videoWidth);
        this.windowLayoutParams.y = getSideCoord(false, i, f, this.videoHeight);
        this.windowLayoutParams.format = -3;
        this.windowLayoutParams.gravity = 51;
        this.windowLayoutParams.type = 2003;
        this.windowLayoutParams.flags = 16777736;
        this.windowManager.addView(this.windowView, this.windowLayoutParams);
        this.parentSheet = paramEmbedBottomSheet;
        this.parentActivity = paramActivity;
        return localTextureView;
        this.videoHeight = AndroidUtilities.dp(192.0F);
        this.videoWidth = (int)(this.videoHeight * paramFloat);
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
      }
    }
    return null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PipVideoView
 * JD-Core Version:    0.6.0
 */