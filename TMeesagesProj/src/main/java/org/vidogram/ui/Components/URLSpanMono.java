package org.vidogram.ui.Components;

import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessagesController;
import org.vidogram.ui.ActionBar.Theme;

public class URLSpanMono extends MetricAffectingSpan
{
  private int currentEnd;
  private CharSequence currentMessage;
  private int currentStart;
  private boolean isOut;

  public URLSpanMono(CharSequence paramCharSequence, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.currentMessage = paramCharSequence;
    this.currentStart = paramInt1;
    this.currentEnd = paramInt2;
  }

  public void copyToClipboard()
  {
    AndroidUtilities.addToClipboard(this.currentMessage.subSequence(this.currentStart, this.currentEnd).toString());
  }

  public void updateDrawState(TextPaint paramTextPaint)
  {
    paramTextPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize - 1));
    paramTextPaint.setTypeface(Typeface.MONOSPACE);
    paramTextPaint.setUnderlineText(false);
    if (this.isOut)
    {
      paramTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      return;
    }
    paramTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
  }

  public void updateMeasureState(TextPaint paramTextPaint)
  {
    paramTextPaint.setTypeface(Typeface.MONOSPACE);
    paramTextPaint.setTextSize(AndroidUtilities.dp(MessagesController.getInstance().fontSize - 1));
    paramTextPaint.setFlags(paramTextPaint.getFlags() | 0x80);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.URLSpanMono
 * JD-Core Version:    0.6.0
 */