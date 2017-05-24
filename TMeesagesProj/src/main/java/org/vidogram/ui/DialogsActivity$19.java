package org.vidogram.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import itman.Vidofilm.b;
import org.vidogram.messenger.NotificationsController;

class DialogsActivity$19 extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    int i = b.a(paramContext).o();
    if (i > 0)
    {
      NotificationsController.getInstance().setMissedCallCountBadge(i);
      DialogsActivity.access$3800(this.this$0);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.19
 * JD-Core Version:    0.6.0
 */