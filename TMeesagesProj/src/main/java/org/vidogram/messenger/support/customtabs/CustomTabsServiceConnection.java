package org.vidogram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

public abstract class CustomTabsServiceConnection
  implements ServiceConnection
{
  public abstract void onCustomTabsServiceConnected(ComponentName paramComponentName, CustomTabsClient paramCustomTabsClient);

  public final void onServiceConnected(ComponentName paramComponentName, IBinder paramIBinder)
  {
    onCustomTabsServiceConnected(paramComponentName, new CustomTabsClient(ICustomTabsService.Stub.asInterface(paramIBinder), paramComponentName)
    {
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsServiceConnection
 * JD-Core Version:    0.6.0
 */