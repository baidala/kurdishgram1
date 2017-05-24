package org.vidogram.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

class DialogsActivity$9
  implements View.OnClickListener
{
  public void onClick(View paramView)
  {
    paramView = new Bundle();
    paramView.putBoolean("destroyAfterSelect", true);
    this.this$0.presentFragment(new ContactsActivity(paramView));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.9
 * JD-Core Version:    0.6.0
 */