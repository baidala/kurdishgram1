package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class PopupReplyReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    ApplicationLoader.postInitApplication();
    NotificationsController.getInstance().forceShowPopupForReply();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.PopupReplyReceiver
 * JD-Core Version:    0.6.0
 */