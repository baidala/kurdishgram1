package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class RecordStatusDrawable extends Drawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float progress;
  private RectF rect = new RectF();
  private boolean started = false;

  private void update()
  {
    long l1 = 50L;
    long l3 = System.currentTimeMillis();
    long l2 = l3 - this.lastUpdateTime;
    this.lastUpdateTime = l3;
    if (l2 > 50L);
    while (true)
    {
      float f = this.progress;
      this.progress = ((float)l1 / 300.0F + f);
      while (this.progress > 1.0F)
        this.progress -= 1.0F;
      invalidateSelf();
      return;
      l1 = l2;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    paramCanvas.save();
    int i = getIntrinsicHeight() / 2;
    float f;
    if (this.isChat)
    {
      f = 1.0F;
      paramCanvas.translate(0.0F, AndroidUtilities.dp(f) + i);
      i = 0;
      label35: if (i >= 4)
        break label156;
      if (i != 0)
        break label120;
      Theme.chat_statusRecordPaint.setAlpha((int)(this.progress * 255.0F));
    }
    while (true)
    {
      f = AndroidUtilities.dp(4.0F) * i + AndroidUtilities.dp(4.0F) * this.progress;
      this.rect.set(-f, -f, f, f);
      paramCanvas.drawArc(this.rect, -15.0F, 30.0F, false, Theme.chat_statusRecordPaint);
      i += 1;
      break label35;
      f = 2.0F;
      break;
      label120: if (i == 3)
      {
        Theme.chat_statusRecordPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
        continue;
      }
      Theme.chat_statusRecordPaint.setAlpha(255);
    }
    label156: paramCanvas.restore();
    if (this.started)
      update();
  }

  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(14.0F);
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
    this.started = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.RecordStatusDrawable
 * JD-Core Version:    0.6.0
 */