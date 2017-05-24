package org.vidogram.messenger.support.customtabsclient.shared;

import android.content.ComponentName;
import java.lang.ref.WeakReference;
import org.vidogram.messenger.support.customtabs.CustomTabsClient;
import org.vidogram.messenger.support.customtabs.CustomTabsServiceConnection;

public class ServiceConnection extends CustomTabsServiceConnection
{
  private WeakReference<ServiceConnectionCallback> mConnectionCallback;

  public ServiceConnection(ServiceConnectionCallback paramServiceConnectionCallback)
  {
    this.mConnectionCallback = new WeakReference(paramServiceConnectionCallback);
  }

  public void onCustomTabsServiceConnected(ComponentName paramComponentName, CustomTabsClient paramCustomTabsClient)
  {
    paramComponentName = (ServiceConnectionCallback)this.mConnectionCallback.get();
    if (paramComponentName != null)
      paramComponentName.onServiceConnected(paramCustomTabsClient);
  }

  public void onServiceDisconnected(ComponentName paramComponentName)
  {
    paramComponentName = (ServiceConnectionCallback)this.mConnectionCallback.get();
    if (paramComponentName != null)
      paramComponentName.onServiceDisconnected();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabsclient.shared.ServiceConnection
 * JD-Core Version:    0.6.0
 */