package org.vidogram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.gms.iid.a;

public class GcmRegistrationIntentService extends IntentService
{
  public GcmRegistrationIntentService()
  {
    super("GcmRegistrationIntentService");
  }

  private void sendRegistrationToServer(String paramString)
  {
    Utilities.stageQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        UserConfig.pushString = this.val$token;
        UserConfig.registeredForPush = false;
        UserConfig.saveConfig(false);
        if (UserConfig.getClientUserId() != 0)
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance().registerForPush(GcmRegistrationIntentService.3.this.val$token);
            }
          });
      }
    });
  }

  protected void onHandleIntent(Intent paramIntent)
  {
    try
    {
      String str = a.c(this).a(getString(2131166849), "GCM", null);
      FileLog.d("GCM Registration Token: " + str);
      AndroidUtilities.runOnUIThread(new Runnable(str)
      {
        public void run()
        {
          ApplicationLoader.postInitApplication();
          GcmRegistrationIntentService.this.sendRegistrationToServer(this.val$token);
        }
      });
      return;
    }
    catch (Exception localException)
    {
      int i;
      do
      {
        do
          FileLog.e(localException);
        while (paramIntent == null);
        i = paramIntent.getIntExtra("failCount", 0);
      }
      while (i >= 60);
      paramIntent = new Runnable(i)
      {
        public void run()
        {
          try
          {
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
            localIntent.putExtra("failCount", this.val$failCount + 1);
            GcmRegistrationIntentService.this.startService(localIntent);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      };
      if (i >= 20)
        break label106;
    }
    long l = 10000L;
    while (true)
    {
      AndroidUtilities.runOnUIThread(paramIntent, l);
      return;
      label106: l = 1800000L;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.GcmRegistrationIntentService
 * JD-Core Version:    0.6.0
 */