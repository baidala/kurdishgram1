package org.vidogram.messenger.support.customtabs;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.b.j;
import android.util.Log;

public class CustomTabsSessionToken
{
  private static final String TAG = "CustomTabsSessionToken";
  private final CustomTabsCallback mCallback;
  private final ICustomTabsCallback mCallbackBinder;

  CustomTabsSessionToken(ICustomTabsCallback paramICustomTabsCallback)
  {
    this.mCallbackBinder = paramICustomTabsCallback;
    this.mCallback = new CustomTabsCallback()
    {
      public void onNavigationEvent(int paramInt, Bundle paramBundle)
      {
        try
        {
          CustomTabsSessionToken.this.mCallbackBinder.onNavigationEvent(paramInt, paramBundle);
          return;
        }
        catch (android.os.RemoteException paramBundle)
        {
          Log.e("CustomTabsSessionToken", "RemoteException during ICustomTabsCallback transaction");
        }
      }
    };
  }

  public static CustomTabsSessionToken getSessionTokenFromIntent(Intent paramIntent)
  {
    paramIntent = j.a(paramIntent.getExtras(), "android.support.customtabs.extra.SESSION");
    if (paramIntent == null)
      return null;
    return new CustomTabsSessionToken(ICustomTabsCallback.Stub.asInterface(paramIntent));
  }

  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof CustomTabsSessionToken))
      return false;
    return ((CustomTabsSessionToken)paramObject).getCallbackBinder().equals(this.mCallbackBinder.asBinder());
  }

  public CustomTabsCallback getCallback()
  {
    return this.mCallback;
  }

  IBinder getCallbackBinder()
  {
    return this.mCallbackBinder.asBinder();
  }

  public int hashCode()
  {
    return getCallbackBinder().hashCode();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsSessionToken
 * JD-Core Version:    0.6.0
 */