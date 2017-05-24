package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.vidogram.tgnet.ConnectionsManager;

public class ScreenReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent.getAction().equals("android.intent.action.SCREEN_OFF"))
    {
      FileLog.e("screen off");
      ConnectionsManager.getInstance().setAppPaused(true, true);
      ApplicationLoader.isScreenOn = false;
    }
    while (true)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.screenStateChanged, new Object[0]);
      return;
      if (!paramIntent.getAction().equals("android.intent.action.SCREEN_ON"))
        continue;
      FileLog.e("screen on");
      ConnectionsManager.getInstance().setAppPaused(false, true);
      ApplicationLoader.isScreenOn = true;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ScreenReceiver
 * JD-Core Version:    0.6.0
 */