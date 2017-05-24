package org.vidogram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextColorCell extends FrameLayout
{
  private static Paint colorPaint;
  public static final int[] colors = { -1031100, -29183, -12769, -8792480, -12521994, -12140801, -2984711, -45162, -4473925 };
  public static final int[] colorsToSave = { -65536, -29183, -12769, -16711936, -12521994, -16776961, -2984711, -45162, -1 };
  private float alpha = 1.0F;
  private int currentColor;
  private boolean needDivider;
  private TextView textView;

  public TextColorCell(Context paramContext)
  {
    super(paramContext);
    if (colorPaint == null)
      colorPaint = new Paint(1);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label148;
    }
    label148: for (int i = j; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }

  public float getAlpha()
  {
    return this.alpha;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    float f;
    if (this.currentColor != 0)
    {
      colorPaint.setColor(this.currentColor);
      colorPaint.setAlpha((int)(255.0F * this.alpha));
      if (!LocaleController.isRTL)
        break label109;
      f = AndroidUtilities.dp(29.0F);
    }
    while (true)
    {
      paramCanvas.drawCircle(f, getMeasuredHeight() / 2, AndroidUtilities.dp(10.0F), colorPaint);
      return;
      label109: f = getMeasuredWidth() - AndroidUtilities.dp(29.0F);
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
    }
  }

  public void setAlpha(float paramFloat)
  {
    this.alpha = paramFloat;
    invalidate();
  }

  public void setEnabled(boolean paramBoolean, ArrayList<Animator> paramArrayList)
  {
    float f1 = 1.0F;
    float f2;
    if (paramArrayList != null)
    {
      TextView localTextView = this.textView;
      if (paramBoolean)
      {
        f2 = 1.0F;
        paramArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { f2 }));
        if (!paramBoolean)
          break label69;
      }
      while (true)
      {
        paramArrayList.add(ObjectAnimator.ofFloat(this, "alpha", new float[] { f1 }));
        return;
        f2 = 0.5F;
        break;
        label69: f1 = 0.5F;
      }
    }
    paramArrayList = this.textView;
    if (paramBoolean)
    {
      f2 = 1.0F;
      paramArrayList.setAlpha(f2);
      if (!paramBoolean)
        break label110;
    }
    while (true)
    {
      setAlpha(f1);
      return;
      f2 = 0.5F;
      break;
      label110: f1 = 0.5F;
    }
  }

  public void setTextAndColor(String paramString, int paramInt, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.needDivider = paramBoolean;
    this.currentColor = paramInt;
    if ((!this.needDivider) && (this.currentColor == 0));
    for (paramBoolean = true; ; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      invalidate();
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextColorCell
 * JD-Core Version:    0.6.0
 */