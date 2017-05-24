package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import org.vidogram.messenger.AndroidUtilities;

public class VideoSeekBarView extends View
{
  private SeekBarDelegate delegate;
  private Paint paint = new Paint();
  private Paint paint2 = new Paint(1);
  private boolean pressed = false;
  private float progress = 0.0F;
  private int thumbDX = 0;
  private int thumbHeight = AndroidUtilities.dp(12.0F);
  private int thumbWidth = AndroidUtilities.dp(12.0F);

  public VideoSeekBarView(Context paramContext)
  {
    super(paramContext);
    this.paint.setColor(-10724260);
    this.paint2.setColor(-1);
  }

  public float getProgress()
  {
    return this.progress;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = (getMeasuredHeight() - this.thumbHeight) / 2;
    int j = (int)((getMeasuredWidth() - this.thumbWidth) * this.progress);
    paramCanvas.drawRect(this.thumbWidth / 2, getMeasuredHeight() / 2 - AndroidUtilities.dp(1.0F), getMeasuredWidth() - this.thumbWidth / 2, getMeasuredHeight() / 2 + AndroidUtilities.dp(1.0F), this.paint);
    paramCanvas.drawCircle(this.thumbWidth / 2 + j, this.thumbHeight / 2 + i, this.thumbWidth / 2, this.paint2);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    float f1 = 0.0F;
    if (paramMotionEvent == null)
      return false;
    float f2 = paramMotionEvent.getX();
    float f3 = paramMotionEvent.getY();
    float f4 = (int)((getMeasuredWidth() - this.thumbWidth) * this.progress);
    if (paramMotionEvent.getAction() == 0)
    {
      int i = (getMeasuredHeight() - this.thumbWidth) / 2;
      if (f4 - i <= f2)
      {
        f1 = this.thumbWidth;
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
        if ((paramMotionEvent.getAction() == 1) && (this.delegate != null))
          this.delegate.onSeekBarDrag(f4 / (getMeasuredWidth() - this.thumbWidth));
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
      this.progress = (f1 / (getMeasuredWidth() - this.thumbWidth));
      invalidate();
      return true;
      if (f2 > getMeasuredWidth() - this.thumbWidth)
      {
        f1 = getMeasuredWidth() - this.thumbWidth;
        continue;
        return false;
      }
      f1 = f2;
    }
  }

  public void setDelegate(SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }

  public void setProgress(float paramFloat)
  {
    float f;
    if (paramFloat < 0.0F)
      f = 0.0F;
    while (true)
    {
      this.progress = f;
      invalidate();
      return;
      f = paramFloat;
      if (paramFloat <= 1.0F)
        continue;
      f = 1.0F;
    }
  }

  public static abstract interface SeekBarDelegate
  {
    public abstract void onSeekBarDrag(float paramFloat);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.VideoSeekBarView
 * JD-Core Version:    0.6.0
 */