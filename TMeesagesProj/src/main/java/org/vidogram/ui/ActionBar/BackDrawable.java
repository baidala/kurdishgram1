package org.vidogram.ui.ActionBar;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import org.vidogram.messenger.AndroidUtilities;

public class BackDrawable extends Drawable
{
  private boolean alwaysClose;
  private boolean animationInProgress;
  private float animationTime = 300.0F;
  private int color = -1;
  private int currentAnimationTime;
  private float currentRotation;
  private float finalRotation;
  private DecelerateInterpolator interpolator = new DecelerateInterpolator();
  private long lastFrameTime;
  private Paint paint = new Paint(1);
  private boolean reverseAngle = false;
  private boolean rotated = true;
  private int rotatedColor = -9079435;

  public BackDrawable(boolean paramBoolean)
  {
    this.paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.alwaysClose = paramBoolean;
  }

  public void draw(Canvas paramCanvas)
  {
    int k = 0;
    label106: int j;
    label137: float f2;
    if (this.currentRotation != this.finalRotation)
    {
      if (this.lastFrameTime != 0L)
      {
        this.currentAnimationTime = (int)(System.currentTimeMillis() - this.lastFrameTime + this.currentAnimationTime);
        if (this.currentAnimationTime >= this.animationTime)
          this.currentRotation = this.finalRotation;
      }
      else
      {
        this.lastFrameTime = System.currentTimeMillis();
        invalidateSelf();
      }
    }
    else
    {
      if (!this.rotated)
        break label468;
      i = (int)((Color.red(this.rotatedColor) - Color.red(this.color)) * this.currentRotation);
      if (!this.rotated)
        break label474;
      j = (int)((Color.green(this.rotatedColor) - Color.green(this.color)) * this.currentRotation);
      if (this.rotated)
        k = (int)((Color.blue(this.rotatedColor) - Color.blue(this.color)) * this.currentRotation);
      i = Color.rgb(i + Color.red(this.color), j + Color.green(this.color), k + Color.blue(this.color));
      this.paint.setColor(i);
      paramCanvas.save();
      paramCanvas.translate(getIntrinsicWidth() / 2, getIntrinsicHeight() / 2);
      f1 = this.currentRotation;
      if (this.alwaysClose)
        break label488;
      f2 = this.currentRotation;
      if (!this.reverseAngle)
        break label480;
    }
    label468: label474: label480: for (int i = -225; ; i = 135)
    {
      paramCanvas.rotate(i * f2);
      paramCanvas.drawLine(-AndroidUtilities.dp(7.0F) - AndroidUtilities.dp(1.0F) * f1, 0.0F, AndroidUtilities.dp(8.0F), 0.0F, this.paint);
      f2 = -AndroidUtilities.dp(0.5F);
      float f3 = AndroidUtilities.dp(7.0F) + AndroidUtilities.dp(1.0F) * f1;
      float f4 = -AndroidUtilities.dp(7.0F);
      f4 = AndroidUtilities.dp(7.0F) * f1 + f4;
      f1 = AndroidUtilities.dp(0.5F) - AndroidUtilities.dp(0.5F) * f1;
      paramCanvas.drawLine(f4, -f2, f1, -f3, this.paint);
      paramCanvas.drawLine(f4, f2, f1, f3, this.paint);
      paramCanvas.restore();
      return;
      if (this.currentRotation < this.finalRotation)
      {
        this.currentRotation = (this.interpolator.getInterpolation(this.currentAnimationTime / this.animationTime) * this.finalRotation);
        break;
      }
      this.currentRotation = (1.0F - this.interpolator.getInterpolation(this.currentAnimationTime / this.animationTime));
      break;
      i = 0;
      break label106;
      j = 0;
      break label137;
    }
    label488: float f1 = this.currentRotation;
    if (this.reverseAngle);
    for (i = -180; ; i = 180)
    {
      paramCanvas.rotate(i * f1 + 135.0F);
      f1 = 1.0F;
      break;
    }
  }

  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(24.0F);
  }

  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(24.0F);
  }

  public int getOpacity()
  {
    return -2;
  }

  public void setAlpha(int paramInt)
  {
  }

  public void setAnimationTime(float paramFloat)
  {
    this.animationTime = paramFloat;
  }

  public void setColor(int paramInt)
  {
    this.color = paramInt;
    invalidateSelf();
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
  }

  public void setRotated(boolean paramBoolean)
  {
    this.rotated = paramBoolean;
  }

  public void setRotatedColor(int paramInt)
  {
    this.rotatedColor = paramInt;
    invalidateSelf();
  }

  public void setRotation(float paramFloat, boolean paramBoolean)
  {
    this.lastFrameTime = 0L;
    if (this.currentRotation == 1.0F)
    {
      this.reverseAngle = true;
      this.lastFrameTime = 0L;
      if (!paramBoolean)
        break label104;
      if (this.currentRotation >= paramFloat)
        break label85;
      this.currentAnimationTime = (int)(this.currentRotation * this.animationTime);
      label51: this.lastFrameTime = System.currentTimeMillis();
    }
    for (this.finalRotation = paramFloat; ; this.finalRotation = paramFloat)
    {
      invalidateSelf();
      return;
      if (this.currentRotation != 0.0F)
        break;
      this.reverseAngle = false;
      break;
      label85: this.currentAnimationTime = (int)((1.0F - this.currentRotation) * this.animationTime);
      break label51;
      label104: this.currentRotation = paramFloat;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.BackDrawable
 * JD-Core Version:    0.6.0
 */