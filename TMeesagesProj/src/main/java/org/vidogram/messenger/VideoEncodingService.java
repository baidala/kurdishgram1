package org.vidogram.messenger;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.b.aa;
import android.support.v4.b.r.d;

public class VideoEncodingService extends Service
  implements NotificationCenter.NotificationCenterDelegate
{
  private r.d builder;
  private int currentProgress;
  private String path;

  public VideoEncodingService()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileUploadProgressChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stopEncodingService);
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    boolean bool;
    if (paramInt == NotificationCenter.FileUploadProgressChanged)
    {
      Object localObject = (String)paramArrayOfObject[0];
      if ((this.path != null) && (this.path.equals(localObject)))
      {
        localObject = (Float)paramArrayOfObject[1];
        paramArrayOfObject = (Boolean)paramArrayOfObject[2];
        this.currentProgress = (int)(((Float)localObject).floatValue() * 100.0F);
        paramArrayOfObject = this.builder;
        paramInt = this.currentProgress;
        if (this.currentProgress != 0)
          break label108;
        bool = true;
        paramArrayOfObject.a(100, paramInt, bool);
      }
    }
    label108: 
    do
    {
      do
        try
        {
          aa.a(ApplicationLoader.applicationContext).a(4, this.builder.b());
          return;
          bool = false;
        }
        catch (java.lang.Throwable paramArrayOfObject)
        {
          FileLog.e(paramArrayOfObject);
          return;
        }
      while (paramInt != NotificationCenter.stopEncodingService);
      paramArrayOfObject = (String)paramArrayOfObject[0];
    }
    while ((paramArrayOfObject != null) && (!paramArrayOfObject.equals(this.path)));
    stopSelf();
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onDestroy()
  {
    stopForeground(true);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileUploadProgressChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stopEncodingService);
    FileLog.e("destroy video service");
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    boolean bool1 = false;
    this.path = paramIntent.getStringExtra("path");
    boolean bool2 = paramIntent.getBooleanExtra("gif", false);
    if (this.path == null)
    {
      stopSelf();
      return 2;
    }
    FileLog.e("start video service");
    if (this.builder == null)
    {
      this.builder = new r.d(ApplicationLoader.applicationContext);
      this.builder.a(17301640);
      this.builder.a(System.currentTimeMillis());
      this.builder.a(LocaleController.getString("AppName", 2131165319));
      if (!bool2)
        break label198;
      this.builder.c(LocaleController.getString("SendingGif", 2131166426));
      this.builder.b(LocaleController.getString("SendingGif", 2131166426));
    }
    while (true)
    {
      this.currentProgress = 0;
      paramIntent = this.builder;
      paramInt1 = this.currentProgress;
      if (this.currentProgress == 0)
        bool1 = true;
      paramIntent.a(100, paramInt1, bool1);
      startForeground(4, this.builder.b());
      aa.a(ApplicationLoader.applicationContext).a(4, this.builder.b());
      return 2;
      label198: this.builder.c(LocaleController.getString("SendingVideo", 2131166428));
      this.builder.b(LocaleController.getString("SendingVideo", 2131166428));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.VideoEncodingService
 * JD-Core Version:    0.6.0
 */