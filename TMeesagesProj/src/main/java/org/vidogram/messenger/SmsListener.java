package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsListener extends BroadcastReceiver
{
  private SharedPreferences preferences;

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if ((!paramIntent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) || (!AndroidUtilities.isWaitingForSms()));
    while (true)
    {
      return;
      paramContext = paramIntent.getExtras();
      if (paramContext == null)
        continue;
      try
      {
        paramIntent = (Object[])(Object[])paramContext.get("pdus");
        SmsMessage[] arrayOfSmsMessage = new SmsMessage[paramIntent.length];
        paramContext = "";
        int i = 0;
        while (i < arrayOfSmsMessage.length)
        {
          arrayOfSmsMessage[i] = SmsMessage.createFromPdu((byte[])(byte[])paramIntent[i]);
          paramContext = paramContext + arrayOfSmsMessage[i].getMessageBody();
          i += 1;
        }
        try
        {
          paramContext = Pattern.compile("[0-9]+").matcher(paramContext);
          if ((!paramContext.find()) || (paramContext.group(0).length() < 3))
            continue;
          AndroidUtilities.runOnUIThread(new Runnable(paramContext)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceiveSmsCode, new Object[] { this.val$matcher.group(0) });
            }
          });
          return;
        }
        catch (java.lang.Throwable paramContext)
        {
          FileLog.e(paramContext);
          return;
        }
      }
      catch (java.lang.Throwable paramContext)
      {
        FileLog.e(paramContext);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.SmsListener
 * JD-Core Version:    0.6.0
 */