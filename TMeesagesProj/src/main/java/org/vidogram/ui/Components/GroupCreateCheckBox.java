package org.vidogram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class GroupCreateCheckBox extends View
{
  private static Paint backgroundInnerPaint;
  private static Paint backgroundPaint;
  private static Paint checkPaint;
  private static Paint eraser;
  private static Paint eraser2;
  private static final float progressBounceDiff = 0.2F;
  private boolean attachedToWindow;
  private Canvas bitmapCanvas;
  private ObjectAnimator checkAnimator;
  private Bitmap drawBitmap;
  private boolean isCheckAnimation = true;
  private boolean isChecked;
  private float progress;

  public GroupCreateCheckBox(Context paramContext)
  {
    super(paramContext);
    if (backgroundPaint == null)
    {
      backgroundPaint = new Paint(1);
      backgroundInnerPaint = new Paint(1);
      checkPaint = new Paint(1);
      checkPaint.setStyle(Paint.Style.STROKE);
      eraser = new Paint(1);
      eraser.setColor(0);
      eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
      eraser2 = new Paint(1);
      eraser2.setColor(0);
      eraser2.setStyle(Paint.Style.STROKE);
      eraser2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    checkPaint.setStrokeWidth(AndroidUtilities.dp(1.5F));
    eraser2.setStrokeWidth(AndroidUtilities.dp(28.0F));
    this.drawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(24.0F), Bitmap.Config.ARGB_4444);
    this.bitmapCanvas = new Canvas(this.drawBitmap);
    updateColors();
  }

  private void animateToCheckedState(boolean paramBoolean)
  {
    this.isCheckAnimation = paramBoolean;
    float f;
    if (paramBoolean)
      f = 1.0F;
    while (true)
    {
      this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", new float[] { f });
      this.checkAnimator.setDuration(300L);
      this.checkAnimator.start();
      return;
      f = 0.0F;
    }
  }

  private void cancelCheckAnimator()
  {
    if (this.checkAnimator != null)
      this.checkAnimator.cancel();
  }

  public float getProgress()
  {
    return this.progress;
  }

  public boolean isChecked()
  {
    return this.isChecked;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.attachedToWindow = true;
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.attachedToWindow = false;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0);
    do
      return;
    while (this.progress == 0.0F);
    int k = getMeasuredWidth() / 2;
    int i = getMeasuredHeight() / 2;
    eraser2.setStrokeWidth(AndroidUtilities.dp(30.0F));
    this.drawBitmap.eraseColor(0);
    float f2;
    float f3;
    label78: float f1;
    if (this.progress >= 0.5F)
    {
      f2 = 1.0F;
      if (this.progress >= 0.5F)
        break label336;
      f3 = 0.0F;
      if (!this.isCheckAnimation)
        break label351;
      f1 = this.progress;
      label90: if (f1 >= 0.2F)
        break label361;
      f1 = f1 * AndroidUtilities.dp(2.0F) / 0.2F;
    }
    while (true)
    {
      if (f3 != 0.0F)
        paramCanvas.drawCircle(k, i, k - AndroidUtilities.dp(2.0F) + AndroidUtilities.dp(2.0F) * f3 - f1, backgroundPaint);
      f1 = k - AndroidUtilities.dp(2.0F) - f1;
      this.bitmapCanvas.drawCircle(k, i, f1, backgroundInnerPaint);
      this.bitmapCanvas.drawCircle(k, i, (1.0F - f2) * f1, eraser);
      paramCanvas.drawBitmap(this.drawBitmap, 0.0F, 0.0F, null);
      f1 = AndroidUtilities.dp(10.0F) * f3;
      f2 = AndroidUtilities.dp(5.0F) * f3;
      int m;
      k -= AndroidUtilities.dp(1.0F);
      int j;
      i += AndroidUtilities.dp(4.0F);
      f2 = (float)Math.sqrt(f2 * f2 / 2.0F);
      paramCanvas.drawLine(m, j, m - f2, j - f2, checkPaint);
      f1 = (float)Math.sqrt(f1 * f1 / 2.0F);
      int n;
      m -= AndroidUtilities.dp(1.2F);
      paramCanvas.drawLine(n, j, n + f1, j - f1, checkPaint);
      return;
      f2 = this.progress / 0.5F;
      break;
      label336: f3 = (this.progress - 0.5F) / 0.5F;
      break label78;
      label351: f1 = 1.0F - this.progress;
      break label90;
      label361: if (f1 < 0.4F)
      {
        f1 = AndroidUtilities.dp(2.0F) - (f1 - 0.2F) * AndroidUtilities.dp(2.0F) / 0.2F;
        continue;
      }
      f1 = 0.0F;
    }
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1 == this.isChecked)
      return;
    this.isChecked = paramBoolean1;
    if ((this.attachedToWindow) && (paramBoolean2))
    {
      animateToCheckedState(paramBoolean1);
      return;
    }
    cancelCheckAnimator();
    float f;
    if (paramBoolean1)
      f = 1.0F;
    while (true)
    {
      setProgress(f);
      return;
      f = 0.0F;
    }
  }

  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat)
      return;
    this.progress = paramFloat;
    invalidate();
  }

  public void updateColors()
  {
    backgroundInnerPaint.setColor(Theme.getColor("groupcreate_checkbox"));
    backgroundPaint.setColor(Theme.getColor("groupcreate_checkboxCheck"));
    checkPaint.setColor(Theme.getColor("groupcreate_checkboxCheck"));
    invalidate();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.GroupCreateCheckBox
 * JD-Core Version:    0.6.0
 */