package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ShareBroadcastReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    paramIntent = paramIntent.getDataString();
    if (paramIntent != null)
    {
      Intent localIntent = new Intent("android.intent.action.SEND");
      localIntent.setType("text/plain");
      localIntent.putExtra("android.intent.extra.TEXT", paramIntent);
      paramIntent = Intent.createChooser(localIntent, LocaleController.getString("ShareLink", 2131166452));
      paramIntent.setFlags(268435456);
      paramContext.startActivity(paramIntent);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ShareBroadcastReceiver
 * JD-Core Version:    0.6.0
 */