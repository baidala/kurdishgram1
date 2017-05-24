package org.vidogram.messenger.support.customtabsclient.shared;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class KeepAliveService extends Service
{
  private static final Binder sBinder = new Binder();

  public IBinder onBind(Intent paramIntent)
  {
    return sBinder;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabsclient.shared.KeepAliveService
 * JD-Core Version:    0.6.0
 */