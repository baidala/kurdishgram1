package org.vidogram.messenger.support.customtabs;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.support.v4.f.a;
import java.util.List;
import java.util.Map;

public abstract class CustomTabsService extends Service
{
  public static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  public static final String KEY_URL = "android.support.customtabs.otherurls.URL";
  private ICustomTabsService.Stub mBinder = new ICustomTabsService.Stub()
  {
    public Bundle extraCommand(String paramString, Bundle paramBundle)
    {
      return CustomTabsService.this.extraCommand(paramString, paramBundle);
    }

    public boolean mayLaunchUrl(ICustomTabsCallback paramICustomTabsCallback, Uri paramUri, Bundle paramBundle, List<Bundle> paramList)
    {
      return CustomTabsService.this.mayLaunchUrl(new CustomTabsSessionToken(paramICustomTabsCallback), paramUri, paramBundle, paramList);
    }

    public boolean newSession(ICustomTabsCallback paramICustomTabsCallback)
    {
      CustomTabsSessionToken localCustomTabsSessionToken = new CustomTabsSessionToken(paramICustomTabsCallback);
      try
      {
        1 local1 = new IBinder.DeathRecipient(localCustomTabsSessionToken)
        {
          public void binderDied()
          {
            CustomTabsService.this.cleanUpSession(this.val$sessionToken);
          }
        };
        synchronized (CustomTabsService.this.mDeathRecipientMap)
        {
          paramICustomTabsCallback.asBinder().linkToDeath(local1, 0);
          CustomTabsService.this.mDeathRecipientMap.put(paramICustomTabsCallback.asBinder(), local1);
          boolean bool = CustomTabsService.this.newSession(localCustomTabsSessionToken);
          return bool;
        }
      }
      catch (android.os.RemoteException paramICustomTabsCallback)
      {
      }
      return false;
    }

    public boolean updateVisuals(ICustomTabsCallback paramICustomTabsCallback, Bundle paramBundle)
    {
      return CustomTabsService.this.updateVisuals(new CustomTabsSessionToken(paramICustomTabsCallback), paramBundle);
    }

    public boolean warmup(long paramLong)
    {
      return CustomTabsService.this.warmup(paramLong);
    }
  };
  private final Map<IBinder, IBinder.DeathRecipient> mDeathRecipientMap = new a();

  protected boolean cleanUpSession(CustomTabsSessionToken paramCustomTabsSessionToken)
  {
    try
    {
      ??? = this.mDeathRecipientMap;
      synchronized (this.mDeathRecipientMap)
      {
        paramCustomTabsSessionToken = paramCustomTabsSessionToken.getCallbackBinder();
        paramCustomTabsSessionToken.unlinkToDeath((IBinder.DeathRecipient)this.mDeathRecipientMap.get(paramCustomTabsSessionToken), 0);
        this.mDeathRecipientMap.remove(paramCustomTabsSessionToken);
        return true;
      }
    }
    catch (java.util.NoSuchElementException paramCustomTabsSessionToken)
    {
    }
    return false;
  }

  protected abstract Bundle extraCommand(String paramString, Bundle paramBundle);

  protected abstract boolean mayLaunchUrl(CustomTabsSessionToken paramCustomTabsSessionToken, Uri paramUri, Bundle paramBundle, List<Bundle> paramList);

  protected abstract boolean newSession(CustomTabsSessionToken paramCustomTabsSessionToken);

  public IBinder onBind(Intent paramIntent)
  {
    return this.mBinder;
  }

  protected abstract boolean updateVisuals(CustomTabsSessionToken paramCustomTabsSessionToken, Bundle paramBundle);

  protected abstract boolean warmup(long paramLong);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsService
 * JD-Core Version:    0.6.0
 */