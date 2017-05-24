package org.vidogram.ui.Components;

import android.text.TextPaint;
import org.vidogram.messenger.AndroidUtilities;

public class URLSpanNoUnderlineBold extends URLSpanNoUnderline
{
  public URLSpanNoUnderlineBold(String paramString)
  {
    super(paramString);
  }

  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    paramTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    paramTextPaint.setUnderlineText(false);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.URLSpanNoUnderlineBold
 * JD-Core Version:    0.6.0
 */