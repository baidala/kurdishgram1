package org.vidogram.ui.Components;

import android.text.TextPaint;
import org.vidogram.ui.ActionBar.Theme;

public class URLSpanBotCommand extends URLSpanNoUnderline
{
  public static boolean enabled = true;
  public boolean isOut;

  public URLSpanBotCommand(String paramString, boolean paramBoolean)
  {
    super(paramString);
    this.isOut = paramBoolean;
  }

  public void updateDrawState(TextPaint paramTextPaint)
  {
    super.updateDrawState(paramTextPaint);
    if (this.isOut)
    {
      if (enabled);
      for (str = "chat_messageLinkOut"; ; str = "chat_messageTextOut")
      {
        paramTextPaint.setColor(Theme.getColor(str));
        paramTextPaint.setUnderlineText(false);
        return;
      }
    }
    if (enabled);
    for (String str = "chat_messageLinkIn"; ; str = "chat_messageTextIn")
    {
      paramTextPaint.setColor(Theme.getColor(str));
      break;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.URLSpanBotCommand
 * JD-Core Version:    0.6.0
 */