package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppStartReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.startPushService();
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.AppStartReceiver
 * JD-Core Version:    0.6.0
 */