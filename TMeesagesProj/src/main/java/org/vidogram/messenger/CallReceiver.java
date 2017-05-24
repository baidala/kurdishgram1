package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import org.vidogram.a.b;

public class CallReceiver extends BroadcastReceiver
{
  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ((paramIntent.getAction().equals("android.intent.action.PHONE_STATE")) && (paramIntent.getStringExtra("state").equals(TelephonyManager.EXTRA_STATE_RINGING)))
    {
      paramContext = paramIntent.getStringExtra("incoming_number");
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveCall, new Object[] { b.b(paramContext) });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.CallReceiver
 * JD-Core Version:    0.6.0
 */