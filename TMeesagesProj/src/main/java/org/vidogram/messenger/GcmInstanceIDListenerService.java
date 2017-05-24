package org.vidogram.messenger;

import android.content.Intent;
import com.google.android.gms.iid.b;

public class GcmInstanceIDListenerService extends b
{
  public void onTokenRefresh()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ApplicationLoader.postInitApplication();
        Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
        GcmInstanceIDListenerService.this.startService(localIntent);
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.GcmInstanceIDListenerService
 * JD-Core Version:    0.6.0
 */