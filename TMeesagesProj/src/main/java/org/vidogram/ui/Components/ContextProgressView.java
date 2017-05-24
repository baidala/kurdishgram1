package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.view.View;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class ContextProgressView extends View
{
  private RectF cicleRect = new RectF();
  private int currentColorType;
  private Paint innerPaint = new Paint(1);
  private long lastUpdateTime;
  private Paint outerPaint = new Paint(1);
  private int radOffset = 0;

  public ContextProgressView(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.innerPaint.setStyle(Paint.Style.STROKE);
    this.innerPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.outerPaint.setStyle(Paint.Style.STROKE);
    this.outerPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.outerPaint.setStrokeCap(Paint.Cap.ROUND);
    this.currentColorType = paramInt;
    updateColors();
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0)
      return;
    long l1 = System.currentTimeMillis();
    long l2 = this.lastUpdateTime;
    this.lastUpdateTime = l1;
    this.radOffset = (int)(this.radOffset + (float)((l1 - l2) * 360L) / 1000.0F);
    int i = getMeasuredWidth() / 2 - AndroidUtilities.dp(9.0F);
    int j = getMeasuredHeight() / 2 - AndroidUtilities.dp(9.0F);
    this.cicleRect.set(i, j, i + AndroidUtilities.dp(18.0F), j + AndroidUtilities.dp(18.0F));
    paramCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, AndroidUtilities.dp(9.0F), this.innerPaint);
    paramCanvas.drawArc(this.cicleRect, this.radOffset - 90, 90.0F, false, this.outerPaint);
    invalidate();
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  public void updateColors()
  {
    if (this.currentColorType == 0)
    {
      this.innerPaint.setColor(Theme.getColor("contextProgressInner1"));
      this.outerPaint.setColor(Theme.getColor("contextProgressOuter1"));
    }
    while (true)
    {
      invalidate();
      return;
      if (this.currentColorType == 1)
      {
        this.innerPaint.setColor(Theme.getColor("contextProgressInner2"));
        this.outerPaint.setColor(Theme.getColor("contextProgressOuter2"));
        continue;
      }
      if (this.currentColorType != 2)
        continue;
      this.innerPaint.setColor(Theme.getColor("contextProgressInner3"));
      this.outerPaint.setColor(Theme.getColor("contextProgressOuter3"));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ContextProgressView
 * JD-Core Version:    0.6.0
 */