package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import org.vidogram.messenger.AndroidUtilities;

public class PhotoEditorSeekBar extends View
{
  private PhotoEditorSeekBarDelegate delegate;
  private Paint innerPaint = new Paint();
  private int maxValue;
  private int minValue;
  private Paint outerPaint = new Paint(1);
  private boolean pressed = false;
  private float progress = 0.0F;
  private int thumbDX = 0;
  private int thumbSize = AndroidUtilities.dp(16.0F);

  public PhotoEditorSeekBar(Context paramContext)
  {
    super(paramContext);
    this.innerPaint.setColor(-1724368840);
    this.outerPaint.setColor(-11292945);
  }

  public int getProgress()
  {
    return (int)(this.minValue + this.progress * (this.maxValue - this.minValue));
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - this.thumbSize) / 2;
    int j = (int)((getMeasuredWidth() - this.thumbSize) * this.progress);
    paramCanvas.drawRect(this.thumbSize / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbSize / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.innerPaint);
    if (this.minValue == 0)
      paramCanvas.drawRect(this.thumbSize / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), j, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
    while (true)
    {
      paramCanvas.drawCircle(this.thumbSize / 2 + j, this.thumbSize / 2 + i, this.thumbSize / 2, this.outerPaint);
      return;
      if (this.progress > 0.5F)
      {
        paramCanvas.drawRect(getMeasuredWidth() / 2 - AndroidUtilities.dp(1.0F), (getMeasuredHeight() - this.thumbSize) / 2, getMeasuredWidth() / 2, (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
        paramCanvas.drawRect(getMeasuredWidth() / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), j, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
        continue;
      }
      paramCanvas.drawRect(getMeasuredWidth() / 2, (getMeasuredHeight() - this.thumbSize) / 2, getMeasuredWidth() / 2 + AndroidUtilities.dp(1.0F), (getMeasuredHeight() + this.thumbSize) / 2, this.outerPaint);
      paramCanvas.drawRect(j, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.outerPaint);
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = 0.0F;
    if (paramMotionEvent == null)
      return false;
    float f2 = paramMotionEvent.getX();
    float f3 = paramMotionEvent.getY();
    float f4 = (int)((getMeasuredWidth() - this.thumbSize) * this.progress);
    if (paramMotionEvent.getAction() == 0)
    {
      int i = (getMeasuredHeight() - this.thumbSize) / 2;
      if (f4 - i <= f2)
      {
        f1 = this.thumbSize;
        if ((f2 <= i + (f1 + f4)) && (f3 >= 0.0F) && (f3 <= getMeasuredHeight()))
        {
          this.pressed = true;
          this.thumbDX = (int)(f2 - f4);
          getParent().requestDisallowInterceptTouchEvent(true);
          invalidate();
          return true;
        }
      }
    }
    else if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
    {
      if (this.pressed)
      {
        this.pressed = false;
        invalidate();
        return true;
      }
    }
    else if ((paramMotionEvent.getAction() == 2) && (this.pressed))
    {
      f2 = (int)(f2 - this.thumbDX);
      if (f2 >= 0.0F);
    }
    while (true)
    {
      this.progress = (f1 / (getMeasuredWidth() - this.thumbSize));
      if (this.delegate != null)
        this.delegate.onProgressChanged();
      invalidate();
      return true;
      if (f2 > getMeasuredWidth() - this.thumbSize)
      {
        f1 = getMeasuredWidth() - this.thumbSize;
        continue;
        return false;
      }
      f1 = f2;
    }
  }

  public void setDelegate(PhotoEditorSeekBarDelegate paramPhotoEditorSeekBarDelegate)
  {
    this.delegate = paramPhotoEditorSeekBarDelegate;
  }

  public void setMinMax(int paramInt1, int paramInt2)
  {
    this.minValue = paramInt1;
    this.maxValue = paramInt2;
  }

  public void setProgress(int paramInt)
  {
    setProgress(paramInt, true);
  }

  public void setProgress(int paramInt, boolean paramBoolean)
  {
    int i;
    if (paramInt < this.minValue)
      i = this.minValue;
    while (true)
    {
      this.progress = ((i - this.minValue) / (this.maxValue - this.minValue));
      invalidate();
      if ((paramBoolean) && (this.delegate != null))
        this.delegate.onProgressChanged();
      return;
      i = paramInt;
      if (paramInt <= this.maxValue)
        continue;
      i = this.maxValue;
    }
  }

  public static abstract interface PhotoEditorSeekBarDelegate
  {
    public abstract void onProgressChanged();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PhotoEditorSeekBar
 * JD-Core Version:    0.6.0
 */