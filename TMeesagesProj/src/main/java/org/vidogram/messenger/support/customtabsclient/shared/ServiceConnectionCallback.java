package org.vidogram.messenger.support.customtabsclient.shared;

import org.vidogram.messenger.support.customtabs.CustomTabsClient;

public abstract interface ServiceConnectionCallback
{
  public abstract void onServiceConnected(CustomTabsClient paramCustomTabsClient);

  public abstract void onServiceDisconnected();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabsclient.shared.ServiceConnectionCallback
 * JD-Core Version:    0.6.0
 */