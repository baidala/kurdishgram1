package org.vidogram.messenger;

import android.app.IntentService;
import android.content.Intent;

public class NotificationRepeat extends IntentService
{
  public NotificationRepeat()
  {
    super("NotificationRepeat");
  }

  protected void onHandleIntent(Intent paramIntent)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationsController.getInstance().repeatNotificationMaybe();
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationRepeat
 * JD-Core Version:    0.6.0
 */