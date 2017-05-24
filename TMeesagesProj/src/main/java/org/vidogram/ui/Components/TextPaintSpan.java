package org.vidogram.ui.Components;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class TextPaintSpan extends MetricAffectingSpan
{
  private int color;
  private TextPaint textPaint;
  private int textSize;

  public TextPaintSpan(TextPaint paramTextPaint)
  {
    this.textPaint = paramTextPaint;
  }

  public void updateDrawState(TextPaint paramTextPaint)
  {
    paramTextPaint.setColor(this.textPaint.getColor());
    paramTextPaint.setTypeface(this.textPaint.getTypeface());
    paramTextPaint.setFlags(this.textPaint.getFlags());
  }

  public void updateMeasureState(TextPaint paramTextPaint)
  {
    paramTextPaint.setColor(this.textPaint.getColor());
    paramTextPaint.setTypeface(this.textPaint.getTypeface());
    paramTextPaint.setFlags(this.textPaint.getFlags());
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.TextPaintSpan
 * JD-Core Version:    0.6.0
 */