package org.vidogram.ui;

import android.os.Build.VERSION;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import org.vidogram.messenger.AndroidUtilities;

class DialogsActivity$14
  implements ViewTreeObserver.OnGlobalLayoutListener
{
  public void onGlobalLayout()
  {
    ImageView localImageView = DialogsActivity.access$800(this.this$0);
    float f;
    if (DialogsActivity.access$1300(this.this$0))
    {
      f = AndroidUtilities.dp(100.0F);
      localImageView.setTranslationY(f);
      localImageView = DialogsActivity.access$800(this.this$0);
      if (DialogsActivity.access$1300(this.this$0))
        break label93;
    }
    label93: for (boolean bool = true; ; bool = false)
    {
      localImageView.setClickable(bool);
      if (DialogsActivity.access$800(this.this$0) != null)
      {
        if (Build.VERSION.SDK_INT >= 16)
          break label98;
        DialogsActivity.access$800(this.this$0).getViewTreeObserver().removeGlobalOnLayoutListener(this);
      }
      return;
      f = 0.0F;
      break;
    }
    label98: DialogsActivity.access$800(this.this$0).getViewTreeObserver().removeOnGlobalLayoutListener(this);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.14
 * JD-Core Version:    0.6.0
 */