package org.vidogram.messenger;

import android.app.IntentService;
import android.content.Intent;
import org.vidogram.ui.LaunchActivity;

public class BringAppForegroundService extends IntentService
{
  public BringAppForegroundService()
  {
    super("BringAppForegroundService");
  }

  protected void onHandleIntent(Intent paramIntent)
  {
    paramIntent = new Intent(this, LaunchActivity.class);
    paramIntent.setFlags(268435456);
    startActivity(paramIntent);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.BringAppForegroundService
 * JD-Core Version:    0.6.0
 */