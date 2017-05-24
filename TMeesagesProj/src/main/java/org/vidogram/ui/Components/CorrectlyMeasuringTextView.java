package org.vidogram.ui.Components;

import android.content.Context;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

public class CorrectlyMeasuringTextView extends TextView
{
  public CorrectlyMeasuringTextView(Context paramContext)
  {
    super(paramContext);
  }

  public CorrectlyMeasuringTextView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public CorrectlyMeasuringTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  public void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    try
    {
      Layout localLayout = getLayout();
      if (localLayout.getLineCount() <= 1)
        return;
      paramInt2 = 0;
      paramInt1 = localLayout.getLineCount() - 1;
      while (paramInt1 >= 0)
      {
        paramInt2 = Math.max(paramInt2, Math.round(localLayout.getPaint().measureText(getText(), localLayout.getLineStart(paramInt1), localLayout.getLineEnd(paramInt1))));
        paramInt1 -= 1;
      }
      super.onMeasure(Math.min(getPaddingLeft() + paramInt2 + getPaddingRight(), getMeasuredWidth()) | 0x40000000, getMeasuredHeight() | 0x40000000);
      return;
    }
    catch (Exception localException)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.CorrectlyMeasuringTextView
 * JD-Core Version:    0.6.0
 */