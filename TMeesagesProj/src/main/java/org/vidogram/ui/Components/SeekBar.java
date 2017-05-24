package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import org.vidogram.messenger.AndroidUtilities;

public class SeekBar
{
  private static Paint innerPaint;
  private static Paint outerPaint;
  private static int thumbWidth;
  private SeekBarDelegate delegate;
  private int height;
  private int innerColor;
  private int outerColor;
  private boolean pressed = false;
  private boolean selected;
  private int selectedColor;
  private int thumbDX = 0;
  private int thumbX = 0;
  private int width;

  public SeekBar(Context paramContext)
  {
    if (innerPaint == null)
    {
      innerPaint = new Paint(1);
      outerPaint = new Paint(1);
      thumbWidth = AndroidUtilities.dp(24.0F);
    }
  }

  public void draw(Canvas paramCanvas)
  {
    Paint localPaint = innerPaint;
    int i;
    float f2;
    float f3;
    float f1;
    if (this.selected)
    {
      i = this.selectedColor;
      localPaint.setColor(i);
      outerPaint.setColor(this.outerColor);
      paramCanvas.drawRect(thumbWidth / 2, this.height / 2 - AndroidUtilities.dp(1.0F), this.width - thumbWidth / 2, this.height / 2 + AndroidUtilities.dp(1.0F), innerPaint);
      paramCanvas.drawRect(thumbWidth / 2, this.height / 2 - AndroidUtilities.dp(1.0F), thumbWidth / 2 + this.thumbX, this.height / 2 + AndroidUtilities.dp(1.0F), outerPaint);
      f2 = this.thumbX + thumbWidth / 2;
      f3 = this.height / 2;
      if (!this.pressed)
        break label187;
      f1 = 8.0F;
    }
    while (true)
    {
      paramCanvas.drawCircle(f2, f3, AndroidUtilities.dp(f1), outerPaint);
      return;
      i = this.innerColor;
      break;
      label187: f1 = 6.0F;
    }
  }

  public float getProgress()
  {
    return this.thumbX / (this.width - thumbWidth);
  }

  public boolean isDragging()
  {
    return this.pressed;
  }

  public boolean onTouch(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramInt == 0)
    {
      paramInt = (this.height - thumbWidth) / 2;
      if ((this.thumbX - paramInt <= paramFloat1) && (paramFloat1 <= paramInt + (this.thumbX + thumbWidth)) && (paramFloat2 >= 0.0F) && (paramFloat2 <= this.height))
      {
        this.pressed = true;
        this.thumbDX = (int)(paramFloat1 - this.thumbX);
      }
    }
    else
    {
      while (true)
      {
        return true;
        if ((paramInt == 1) || (paramInt == 3))
        {
          if (!this.pressed)
            break;
          if ((paramInt == 1) && (this.delegate != null))
            this.delegate.onSeekBarDrag(this.thumbX / (this.width - thumbWidth));
          this.pressed = false;
          return true;
        }
        else
        {
          if ((paramInt != 2) || (!this.pressed))
            break;
          this.thumbX = (int)(paramFloat1 - this.thumbDX);
          if (this.thumbX < 0)
          {
            this.thumbX = 0;
            return true;
          }
          if (this.thumbX <= this.width - thumbWidth)
            continue;
          this.thumbX = (this.width - thumbWidth);
          return true;
        }
      }
    }
    return false;
  }

  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.innerColor = paramInt1;
    this.outerColor = paramInt2;
    this.selectedColor = paramInt3;
  }

  public void setDelegate(SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }

  public void setProgress(float paramFloat)
  {
    this.thumbX = (int)Math.ceil((this.width - thumbWidth) * paramFloat);
    if (this.thumbX < 0)
      this.thumbX = 0;
    do
      return;
    while (this.thumbX <= this.width - thumbWidth);
    this.thumbX = (this.width - thumbWidth);
  }

  public void setSelected(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }

  public static abstract interface SeekBarDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.SeekBar
 * JD-Core Version:    0.6.0
 */