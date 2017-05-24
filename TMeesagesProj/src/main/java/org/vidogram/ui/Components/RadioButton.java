package org.vidogram.ui.Components;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Keep;
import android.view.View;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class RadioButton extends View
{
  private static Paint checkedPaint;
  private static Paint eraser;
  private static Paint paint;
  private boolean attachedToWindow;
  private Bitmap bitmap;
  private Canvas bitmapCanvas;
  private ObjectAnimator checkAnimator;
  private int checkedColor;
  private int color;
  private boolean isChecked;
  private float progress;
  private int size = AndroidUtilities.dp(16.0F);

  public RadioButton(Context paramContext)
  {
    super(paramContext);
    if (paint == null)
    {
      paint = new Paint(1);
      paint.setStrokeWidth(AndroidUtilities.dp(2.0F));
      paint.setStyle(Paint.Style.STROKE);
      checkedPaint = new Paint(1);
      eraser = new Paint(1);
      eraser.setColor(0);
      eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
    try
    {
      this.bitmap = Bitmap.createBitmap(AndroidUtilities.dp(this.size), AndroidUtilities.dp(this.size), Bitmap.Config.ARGB_4444);
      this.bitmapCanvas = new Canvas(this.bitmap);
      return;
    }
    catch (Throwable paramContext)
    {
      FileLog.e(paramContext);
    }
  }

  private void animateToCheckedState(boolean paramBoolean)
  {
    float f;
    if (paramBoolean)
      f = 1.0F;
    while (true)
    {
      this.checkAnimator = ObjectAnimator.ofFloat(this, "progress", new float[] { f });
      this.checkAnimator.setDuration(200L);
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
    if ((this.bitmap == null) || (this.bitmap.getWidth() != getMeasuredWidth()))
      if (this.bitmap != null)
        this.bitmap.recycle();
    try
    {
      this.bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
      this.bitmapCanvas = new Canvas(this.bitmap);
      if (this.progress <= 0.5F)
      {
        paint.setColor(this.color);
        checkedPaint.setColor(this.color);
        f1 = this.progress / 0.5F;
        if (this.bitmap != null)
        {
          this.bitmap.eraseColor(0);
          f2 = this.size / 2 - (1.0F + f1) * AndroidUtilities.density;
          this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f2, paint);
          if (this.progress > 0.5F)
            break label386;
          this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, f2 - AndroidUtilities.dp(1.0F), checkedPaint);
          this.bitmapCanvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, (1.0F - f1) * (f2 - AndroidUtilities.dp(1.0F)), eraser);
          paramCanvas.drawBitmap(this.bitmap, 0.0F, 0.0F, null);
        }
        return;
      }
    }
    catch (Throwable localCanvas)
    {
      while (true)
      {
        float f2;
        FileLog.e(localThrowable);
        continue;
        float f1 = 2.0F - this.progress / 0.5F;
        int i = Color.red(this.color);
        int j = (int)((Color.red(this.checkedColor) - i) * (1.0F - f1));
        int k = Color.green(this.color);
        int m = (int)((Color.green(this.checkedColor) - k) * (1.0F - f1));
        int n = Color.blue(this.color);
        i = Color.rgb(i + j, k + m, n + (int)((Color.blue(this.checkedColor) - n) * (1.0F - f1)));
        paint.setColor(i);
        checkedPaint.setColor(i);
        continue;
        label386: Canvas localCanvas = this.bitmapCanvas;
        float f3 = getMeasuredWidth() / 2;
        float f4 = getMeasuredHeight() / 2;
        float f5 = this.size / 4;
        localCanvas.drawCircle(f3, f4, f1 * (f2 - AndroidUtilities.dp(1.0F) - this.size / 4) + f5, checkedPaint);
      }
    }
  }

  public void setBackgroundColor(int paramInt)
  {
    this.color = paramInt;
    invalidate();
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

  public void setCheckedColor(int paramInt)
  {
    this.checkedColor = paramInt;
    invalidate();
  }

  public void setColor(int paramInt1, int paramInt2)
  {
    this.color = paramInt1;
    this.checkedColor = paramInt2;
    invalidate();
  }

  @Keep
  public void setProgress(float paramFloat)
  {
    if (this.progress == paramFloat)
      return;
    this.progress = paramFloat;
    invalidate();
  }

  public void setSize(int paramInt)
  {
    if (this.size == paramInt)
      return;
    this.size = paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.RadioButton
 * JD-Core Version:    0.6.0
 */