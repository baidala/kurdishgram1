package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class SendingFileDrawable extends Drawable
{
  private boolean isChat = false;
  private long lastUpdateTime = 0L;
  private float progress;
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
      this.progress = ((float)l1 / 500.0F + f);
      while (this.progress > 1.0F)
        this.progress -= 1.0F;
      invalidateSelf();
      return;
      l1 = l2;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    int i = 0;
    if (i < 3)
    {
      label28: float f1;
      float f2;
      label62: float f3;
      float f4;
      if (i == 0)
      {
        Theme.chat_statusRecordPaint.setAlpha((int)(this.progress * 255.0F));
        f1 = AndroidUtilities.dp(5.0F) * i;
        f2 = AndroidUtilities.dp(5.0F) * this.progress + f1;
        if (!this.isChat)
          break label206;
        f1 = 3.0F;
        f3 = AndroidUtilities.dp(f1);
        f4 = AndroidUtilities.dp(4.0F);
        if (!this.isChat)
          break label212;
        f1 = 7.0F;
        label87: paramCanvas.drawLine(f2, f3, f2 + f4, AndroidUtilities.dp(f1), Theme.chat_statusRecordPaint);
        if (!this.isChat)
          break label218;
        f1 = 11.0F;
        label116: f3 = AndroidUtilities.dp(f1);
        f4 = AndroidUtilities.dp(4.0F);
        if (!this.isChat)
          break label224;
        f1 = 7.0F;
      }
      while (true)
      {
        paramCanvas.drawLine(f2, f3, f2 + f4, AndroidUtilities.dp(f1), Theme.chat_statusRecordPaint);
        i += 1;
        break;
        if (i == 2)
        {
          Theme.chat_statusRecordPaint.setAlpha((int)((1.0F - this.progress) * 255.0F));
          break label28;
        }
        Theme.chat_statusRecordPaint.setAlpha(255);
        break label28;
        label206: f1 = 4.0F;
        break label62;
        label212: f1 = 8.0F;
        break label87;
        label218: f1 = 12.0F;
        break label116;
        label224: f1 = 8.0F;
      }
    }
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
 * Qualified Name:     org.vidogram.ui.Components.SendingFileDrawable
 * JD-Core Version:    0.6.0
 */