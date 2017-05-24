package org.vidogram.messenger.support.customtabs;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

public class CustomTabsClient
{
  private final ICustomTabsService mService;
  private final ComponentName mServiceComponentName;

  CustomTabsClient(ICustomTabsService paramICustomTabsService, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mServiceComponentName = paramComponentName;
  }

  public static boolean bindCustomTabsService(Context paramContext, String paramString, CustomTabsServiceConnection paramCustomTabsServiceConnection)
  {
    Intent localIntent = new Intent("android.support.customtabs.action.CustomTabsService");
    if (!TextUtils.isEmpty(paramString))
      localIntent.setPackage(paramString);
    return paramContext.bindService(localIntent, paramCustomTabsServiceConnection, 33);
  }

  public Bundle extraCommand(String paramString, Bundle paramBundle)
  {
    try
    {
      paramString = this.mService.extraCommand(paramString, paramBundle);
      return paramString;
    }
    catch (RemoteException paramString)
    {
    }
    return null;
  }

  public CustomTabsSession newSession(CustomTabsCallback paramCustomTabsCallback)
  {
    paramCustomTabsCallback = new ICustomTabsCallback.Stub(paramCustomTabsCallback)
    {
      public void extraCallback(String paramString, Bundle paramBundle)
      {
        if (this.val$callback != null)
          this.val$callback.extraCallback(paramString, paramBundle);
      }

      public void onNavigationEvent(int paramInt, Bundle paramBundle)
      {
        if (this.val$callback != null)
          this.val$callback.onNavigationEvent(paramInt, paramBundle);
      }
    };
    try
    {
      boolean bool = this.mService.newSession(paramCustomTabsCallback);
      if (!bool)
        return null;
      return new CustomTabsSession(this.mService, paramCustomTabsCallback, this.mServiceComponentName);
    }
    catch (RemoteException paramCustomTabsCallback)
    {
    }
    return null;
  }

  public boolean warmup(long paramLong)
  {
    try
    {
      boolean bool = this.mService.warmup(paramLong);
      return bool;
    }
    catch (RemoteException localRemoteException)
    {
    }
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsClient
 * JD-Core Version:    0.6.0
 */