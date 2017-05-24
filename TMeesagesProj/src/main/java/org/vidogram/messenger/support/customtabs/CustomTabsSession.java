package org.vidogram.messenger.support.customtabs;

import android.content.ComponentName;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import java.util.List;

public final class CustomTabsSession
{
  private static final String TAG = "CustomTabsSession";
  private final ICustomTabsCallback mCallback;
  private final ComponentName mComponentName;
  private final ICustomTabsService mService;

  CustomTabsSession(ICustomTabsService paramICustomTabsService, ICustomTabsCallback paramICustomTabsCallback, ComponentName paramComponentName)
  {
    this.mService = paramICustomTabsService;
    this.mCallback = paramICustomTabsCallback;
    this.mComponentName = paramComponentName;
  }

  IBinder getBinder()
  {
    return this.mCallback.asBinder();
  }

  ComponentName getComponentName()
  {
    return this.mComponentName;
  }

  public boolean mayLaunchUrl(Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
  {
    try
    {
      boolean bool = this.mService.mayLaunchUrl(this.mCallback, paramUri, paramBundle, paramList);
      return bool;
    }
    catch (android.os.RemoteException paramUri)
    {
    }
    return false;
  }

  public boolean setActionButton(Bitmap paramBitmap, String paramString)
  {
    return setToolbarItem(0, paramBitmap, paramString);
  }

  public boolean setToolbarItem(int paramInt, Bitmap paramBitmap, String paramString)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("android.support.customtabs.customaction.ID", paramInt);
    localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
    localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
    paramBitmap = new Bundle();
    paramBitmap.putBundle("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
    try
    {
      boolean bool = this.mService.updateVisuals(this.mCallback, paramBitmap);
      return bool;
    }
    catch (android.os.RemoteException paramBitmap)
    {
    }
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsSession
 * JD-Core Version:    0.6.0
 */