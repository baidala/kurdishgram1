package org.vidogram.ui;

import android.annotation.SuppressLint;
import android.graphics.Outline;
import android.view.View;
import android.view.ViewOutlineProvider;
import org.vidogram.messenger.AndroidUtilities;

class DialogsActivity$8 extends ViewOutlineProvider
{
  @SuppressLint({"NewApi"})
  public void getOutline(View paramView, Outline paramOutline)
  {
    paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.8
 * JD-Core Version:    0.6.0
 */