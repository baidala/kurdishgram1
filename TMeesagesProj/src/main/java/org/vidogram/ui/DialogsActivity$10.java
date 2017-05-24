package org.vidogram.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import itman.Vidofilm.tabLayout.CommonTabLayout;
import org.vidogram.ui.Components.RecyclerListView;

class DialogsActivity$10
  implements View.OnTouchListener
{
  public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
  {
    if (((paramMotionEvent.getAction() == 0) && ((int)paramMotionEvent.getX() < DialogsActivity.access$200(this.this$0).getMeasuredWidth() / 5)) || (DialogsActivity.access$000(this.this$0)));
    float f;
    do
    {
      do
      {
        return false;
        switch (paramMotionEvent.getAction())
        {
        default:
          return false;
        case 0:
          this.this$0.downX = paramMotionEvent.getX();
          this.this$0.downY = paramMotionEvent.getY();
          return false;
        case 1:
        }
        this.this$0.upX = paramMotionEvent.getX();
      }
      while (Math.abs(this.this$0.downY - paramMotionEvent.getY()) > 150.0F);
      f = this.this$0.upX - this.this$0.downX;
      if ((f >= -200.0F) || (DialogsActivity.access$1700(this.this$0) >= DialogsActivity.access$2900(this.this$0).length - 1))
        continue;
      DialogsActivity.access$1708(this.this$0);
      DialogsActivity.access$1000(this.this$0).setCurrentTab(DialogsActivity.access$1700(this.this$0));
      DialogsActivity.access$3000(this.this$0, DialogsActivity.access$1700(this.this$0));
      this.this$0.downX = paramMotionEvent.getX();
      return false;
    }
    while ((f <= 200.0F) || (DialogsActivity.access$1700(this.this$0) <= 0));
    DialogsActivity.access$1710(this.this$0);
    DialogsActivity.access$1000(this.this$0).setCurrentTab(DialogsActivity.access$1700(this.this$0));
    DialogsActivity.access$3000(this.this$0, DialogsActivity.access$1700(this.this$0));
    this.this$0.downX = paramMotionEvent.getX();
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.10
 * JD-Core Version:    0.6.0
 */