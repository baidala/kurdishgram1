package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NotificationDismissReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("dismissDate", paramIntent.getIntExtra("messageDate", 0)).commit();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationDismissReceiver
 * JD-Core Version:    0.6.0
 */