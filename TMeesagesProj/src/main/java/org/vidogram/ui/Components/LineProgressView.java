package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import org.vidogram.messenger.AndroidUtilities;

public class LineProgressView extends View
{
  private static DecelerateInterpolator decelerateInterpolator = null;
  private static Paint progressPaint = null;
  private float animatedAlphaValue = 1.0F;
  private float animatedProgressValue = 0.0F;
  private float animationProgressStart = 0.0F;
  private int backColor;
  private float currentProgress = 0.0F;
  private long currentProgressTime = 0L;
  private long lastUpdateTime = 0L;
  private int progressColor;

  public LineProgressView(Context paramContext)
  {
    super(paramContext);
    if (decelerateInterpolator == null)
    {
      decelerateInterpolator = new DecelerateInterpolator();
      progressPaint = new Paint(1);
      progressPaint.setStrokeCap(Paint.Cap.ROUND);
      progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    }
  }

  private void updateAnimation()
  {
    long l1 = System.currentTimeMillis();
    long l2 = l1 - this.lastUpdateTime;
    this.lastUpdateTime = l1;
    float f1;
    if ((this.animatedProgressValue != 1.0F) && (this.animatedProgressValue != this.currentProgress))
    {
      f1 = this.currentProgress - this.animationProgressStart;
      if (f1 > 0.0F)
      {
        this.currentProgressTime += l2;
        if (this.currentProgressTime < 300L)
          break label162;
        this.animatedProgressValue = this.currentProgress;
        this.animationProgressStart = this.currentProgress;
        this.currentProgressTime = 0L;
      }
    }
    while (true)
    {
      invalidate();
      if ((this.animatedProgressValue >= 1.0F) && (this.animatedProgressValue == 1.0F) && (this.animatedAlphaValue != 0.0F))
      {
        this.animatedAlphaValue -= (float)l2 / 200.0F;
        if (this.animatedAlphaValue <= 0.0F)
          this.animatedAlphaValue = 0.0F;
        invalidate();
      }
      return;
      label162: float f2 = this.animationProgressStart;
      this.animatedProgressValue = (f1 * decelerateInterpolator.getInterpolation((float)this.currentProgressTime / 300.0F) + f2);
    }
  }

  public void onDraw(Canvas paramCanvas)
  {
    if ((this.backColor != 0) && (this.animatedProgressValue != 1.0F))
    {
      progressPaint.setColor(this.backColor);
      progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F));
      paramCanvas.drawRect((int)(getWidth() * this.animatedProgressValue), 0.0F, getWidth(), getHeight(), progressPaint);
    }
    progressPaint.setColor(this.progressColor);
    progressPaint.setAlpha((int)(this.animatedAlphaValue * 255.0F));
    paramCanvas.drawRect(0.0F, 0.0F, getWidth() * this.animatedProgressValue, getHeight(), progressPaint);
    updateAnimation();
  }

  public void setBackColor(int paramInt)
  {
    this.backColor = paramInt;
  }

  public void setProgress(float paramFloat, boolean paramBoolean)
  {
    if (!paramBoolean)
      this.animatedProgressValue = paramFloat;
    for (this.animationProgressStart = paramFloat; ; this.animationProgressStart = this.animatedProgressValue)
    {
      if (paramFloat != 1.0F)
        this.animatedAlphaValue = 1.0F;
      this.currentProgress = paramFloat;
      this.currentProgressTime = 0L;
      this.lastUpdateTime = System.currentTimeMillis();
      invalidate();
      return;
    }
  }

  public void setProgressColor(int paramInt)
  {
    this.progressColor = paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.LineProgressView
 * JD-Core Version:    0.6.0
 */