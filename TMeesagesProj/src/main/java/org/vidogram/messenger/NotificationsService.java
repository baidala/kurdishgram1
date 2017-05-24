package org.vidogram.messenger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationsService extends Service
{
  static final int UPDATE_INTERVAL = 300000;
  private Timer timer = new Timer();

  private void doSomeThingRepeatedly()
  {
    this.timer.scheduleAtFixedRate(new TimerTask()
    {
      public void run()
      {
        NotificationsService.this.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        NotificationsService.this.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
      }
    }
    , 0L, 300000L);
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCreate()
  {
    FileLog.e("service started");
    ApplicationLoader.postInitApplication();
  }

  public void onDestroy()
  {
    FileLog.e("service destroyed");
    if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true))
      sendBroadcast(new Intent("org.telegram.start"));
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    doSomeThingRepeatedly();
    return 1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationsService
 * JD-Core Version:    0.6.0
 */