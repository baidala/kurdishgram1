package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.ui.ActionBar.Theme;

public class PlayingGameDrawable extends Drawable
{
  private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private Paint paint = new Paint(1);
  private float progress;
  private RectF rect = new RectF();
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
        PlayingGameDrawable.this.checkUpdate();
      }
    }
    , 100L);
  }

  private void update()
  {
    long l1 = 16L;
    long l3 = System.currentTimeMillis();
    long l2 = l3 - this.lastUpdateTime;
    this.lastUpdateTime = l3;
    if (l2 > 16L);
    while (true)
    {
      if (this.progress >= 1.0F)
        this.progress = 0.0F;
      float f = this.progress;
      this.progress = ((float)l1 / 300.0F + f);
      if (this.progress > 1.0F)
        this.progress = 1.0F;
      invalidateSelf();
      return;
      l1 = l2;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    int n = AndroidUtilities.dp(10.0F);
    int i = getBounds().top + (getIntrinsicHeight() - n) / 2;
    int k;
    label90: int m;
    label93: float f1;
    float f2;
    float f3;
    if (this.isChat)
    {
      this.paint.setColor(Theme.getColor("actionBarDefaultSubtitle"));
      this.rect.set(0.0F, i, n, i + n);
      if (this.progress >= 0.5F)
        break label207;
      k = (int)((1.0F - this.progress / 0.5F) * 35.0F);
      m = 0;
      if (m >= 3)
        break label293;
      f1 = AndroidUtilities.dp(5.0F) * m + AndroidUtilities.dp(9.2F);
      f2 = AndroidUtilities.dp(5.0F);
      f3 = this.progress;
      if (m != 2)
        break label226;
      this.paint.setAlpha(Math.min(255, (int)(255.0F * this.progress / 0.5F)));
    }
    int j;
    while (true)
    {
      paramCanvas.drawCircle(f1 - f2 * f3, n / 2 + i, AndroidUtilities.dp(1.2F), this.paint);
      m += 1;
      break label93;
      i += AndroidUtilities.dp(1.0F);
      break;
      label207: k = (int)((this.progress - 0.5F) * 35.0F / 0.5F);
      break label90;
      label226: if (m == 0)
      {
        if (this.progress > 0.5F)
        {
          this.paint.setAlpha((int)(255.0F * (1.0F - (this.progress - 0.5F) / 0.5F)));
          continue;
        }
        this.paint.setAlpha(255);
        continue;
      }
      this.paint.setAlpha(255);
    }
    label293: this.paint.setAlpha(255);
    paramCanvas.drawArc(this.rect, k, 360 - k * 2, true, this.paint);
    this.paint.setColor(Theme.getColor("actionBarDefault"));
    paramCanvas.drawCircle(AndroidUtilities.dp(4.0F), n / 2 + j - AndroidUtilities.dp(2.0F), AndroidUtilities.dp(1.0F), this.paint);
    checkUpdate();
  }

  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(18.0F);
  }

  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(20.0F);
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
    this.progress = 0.0F;
    this.started = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PlayingGameDrawable
 * JD-Core Version:    0.6.0
 */