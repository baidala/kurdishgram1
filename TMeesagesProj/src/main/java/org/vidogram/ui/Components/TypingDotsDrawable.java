package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.ui.ActionBar.Theme;

public class TypingDotsDrawable extends Drawable
{
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private float[] elapsedTimes = { 0.0F, 0.0F, 0.0F };
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float[] scales = new float[3];
  private float[] startTimes = { 0.0F, 150.0F, 300.0F };
  private boolean started = false;

  private void checkUpdate()
  {
    if (this.started)
    {
      if (!NotificationCenter.getInstance().isAnimationInProgress())
        update();
    }
    else
      return;
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        TypingDotsDrawable.this.checkUpdate();
      }
    }
    , 100L);
  }

  private void update()
  {
    long l1 = 50L;
    long l3 = System.currentTimeMillis();
    long l2 = l3 - this.lastUpdateTime;
    this.lastUpdateTime = l3;
    if (l2 > 50L);
    while (true)
    {
      int i = 0;
      if (i < 3)
      {
        float[] arrayOfFloat = this.elapsedTimes;
        arrayOfFloat[i] += (float)l1;
        float f = this.elapsedTimes[i] - this.startTimes[i];
        if (f > 0.0F)
          if (f <= 320.0F)
          {
            f = this.decelerateInterpolator.getInterpolation(f / 320.0F);
            this.scales[i] = (f + 1.33F);
          }
        while (true)
        {
          i += 1;
          break;
          if (f <= 640.0F)
          {
            f = this.decelerateInterpolator.getInterpolation((f - 320.0F) / 320.0F);
            this.scales[i] = (1.0F - f + 1.33F);
            continue;
          }
          if (f >= 800.0F)
          {
            this.elapsedTimes[i] = 0.0F;
            this.startTimes[i] = 0.0F;
            this.scales[i] = 1.33F;
            continue;
          }
          this.scales[i] = 1.33F;
          continue;
          this.scales[i] = 1.33F;
        }
      }
      invalidateSelf();
      return;
      l1 = l2;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    if (this.isChat);
    int j;
    for (int i = AndroidUtilities.dp(8.5F) + getBounds().top; ; j = AndroidUtilities.dp(9.3F) + getBounds().top)
    {
      paramCanvas.drawCircle(AndroidUtilities.dp(3.0F), i, this.scales[0] * AndroidUtilities.density, Theme.chat_statusPaint);
      paramCanvas.drawCircle(AndroidUtilities.dp(9.0F), i, this.scales[1] * AndroidUtilities.density, Theme.chat_statusPaint);
      paramCanvas.drawCircle(AndroidUtilities.dp(15.0F), i, this.scales[2] * AndroidUtilities.density, Theme.chat_statusPaint);
      checkUpdate();
      return;
    }
  }

  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(18.0F);
  }

  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(18.0F);
  }

  public int getOpacity()
  {
    return 0;
  }

  public void setAlpha(int paramInt)
  {
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
  }

  public void setIsChat(boolean paramBoolean)
  {
    this.isChat = paramBoolean;
  }

  public void start()
  {
    this.lastUpdateTime = System.currentTimeMillis();
    this.started = true;
    invalidateSelf();
  }

  public void stop()
  {
    int i = 0;
    while (i < 3)
    {
      this.elapsedTimes[i] = 0.0F;
      this.scales[i] = 1.33F;
      i += 1;
    }
    this.startTimes[0] = 0.0F;
    this.startTimes[1] = 150.0F;
    this.startTimes[2] = 300.0F;
    this.started = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.TypingDotsDrawable
 * JD-Core Version:    0.6.0
 */