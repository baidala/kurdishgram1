package org.vidogram.ui.Components;

import android.text.TextPaint;
import org.vidogram.ui.ActionBar.Theme;

public class URLSpanUserMention extends URLSpanNoUnderline
{
  private boolean isOut;

  public URLSpanUserMention(String paramString, boolean paramBoolean)
  {
    super(paramString);
    this.isOut = paramBoolean;
  }

  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    if (this.isOut)
      paramTextPaint.setColor(Theme.getColor("chat_messageLinkOut"));
    while (true)
    {
      paramTextPaint.setUnderlineText(false);
      return;
      paramTextPaint.setColor(Theme.getColor("chat_messageLinkIn"));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.URLSpanUserMention
 * JD-Core Version:    0.6.0
 */