package org.vidogram.messenger.browser;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import java.lang.ref.WeakReference;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.ShareBroadcastReceiver;
import org.vidogram.messenger.support.customtabs.CustomTabsCallback;
import org.vidogram.messenger.support.customtabs.CustomTabsClient;
import org.vidogram.messenger.support.customtabs.CustomTabsIntent;
import org.vidogram.messenger.support.customtabs.CustomTabsIntent.Builder;
import org.vidogram.messenger.support.customtabs.CustomTabsServiceConnection;
import org.vidogram.messenger.support.customtabs.CustomTabsSession;
import org.vidogram.messenger.support.customtabsclient.shared.CustomTabsHelper;
import org.vidogram.messenger.support.customtabsclient.shared.ServiceConnection;
import org.vidogram.messenger.support.customtabsclient.shared.ServiceConnectionCallback;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.LaunchActivity;

public class Browser
{
  private static WeakReference<Activity> currentCustomTabsActivity;
  private static CustomTabsClient customTabsClient;
  private static WeakReference<CustomTabsSession> customTabsCurrentSession;
  private static String customTabsPackageToBind;
  private static CustomTabsServiceConnection customTabsServiceConnection;
  private static CustomTabsSession customTabsSession;

  public static void bindCustomTabsService(Activity paramActivity)
  {
    Activity localActivity = null;
    if (Build.VERSION.SDK_INT < 15)
      return;
    if (currentCustomTabsActivity == null);
    while (true)
    {
      while (true)
      {
        if ((localActivity != null) && (localActivity != paramActivity))
          unbindCustomTabsService(localActivity);
        if (customTabsClient != null)
          break;
        currentCustomTabsActivity = new WeakReference(paramActivity);
        try
        {
          if (TextUtils.isEmpty(customTabsPackageToBind))
          {
            customTabsPackageToBind = CustomTabsHelper.getPackageNameToUse(paramActivity);
            if (customTabsPackageToBind == null)
              break;
          }
          customTabsServiceConnection = new ServiceConnection(new ServiceConnectionCallback()
          {
            public void onServiceConnected(CustomTabsClient paramCustomTabsClient)
            {
              Browser.access$102(paramCustomTabsClient);
              if ((MediaController.getInstance().canCustomTabs()) && (Browser.customTabsClient != null));
              try
              {
                Browser.customTabsClient.warmup(0L);
                return;
              }
              catch (Exception paramCustomTabsClient)
              {
                FileLog.e(paramCustomTabsClient);
              }
            }

            public void onServiceDisconnected()
            {
              Browser.access$102(null);
            }
          });
          if (CustomTabsClient.bindCustomTabsService(paramActivity, customTabsPackageToBind, customTabsServiceConnection))
            break;
          customTabsServiceConnection = null;
          return;
        }
        catch (Exception paramActivity)
        {
          FileLog.e(paramActivity);
          return;
        }
      }
      localActivity = (Activity)currentCustomTabsActivity.get();
    }
  }

  private static CustomTabsSession getCurrentSession()
  {
    if (customTabsCurrentSession == null)
      return null;
    return (CustomTabsSession)customTabsCurrentSession.get();
  }

  private static CustomTabsSession getSession()
  {
    if (customTabsClient == null)
      customTabsSession = null;
    while (true)
    {
      return customTabsSession;
      if (customTabsSession != null)
        continue;
      customTabsSession = customTabsClient.newSession(new NavigationCallback(null));
      setCurrentSession(customTabsSession);
    }
  }

  public static boolean isInternalUri(Uri paramUri)
  {
    String str = paramUri.getHost();
    if (str != null);
    for (str = str.toLowerCase(); ("tg".equals(paramUri.getScheme())) || ("telegram.me".equals(str)) || ("t.me".equals(str)) || ("telegram.dog".equals(str)); str = "")
      return true;
    return false;
  }

  public static boolean isInternalUrl(String paramString)
  {
    return isInternalUri(Uri.parse(paramString));
  }

  public static void openUrl(Context paramContext, Uri paramUri)
  {
    openUrl(paramContext, paramUri, true);
  }

  public static void openUrl(Context paramContext, Uri paramUri, boolean paramBoolean)
  {
    if ((paramContext == null) || (paramUri == null))
      return;
    boolean bool = isInternalUri(paramUri);
    try
    {
      if (paramUri.getScheme() != null)
      {
        Object localObject = paramUri.getScheme().toLowerCase();
        if ((Build.VERSION.SDK_INT >= 15) && (paramBoolean) && (MediaController.getInstance().canCustomTabs()) && (!bool) && (!((String)localObject).equals("tel")))
        {
          localObject = new Intent(ApplicationLoader.applicationContext, ShareBroadcastReceiver.class);
          ((Intent)localObject).setAction("android.intent.action.SEND");
          CustomTabsIntent.Builder localBuilder = new CustomTabsIntent.Builder(getSession());
          localBuilder.setToolbarColor(Theme.getColor("actionBarDefault"));
          localBuilder.setShowTitle(true);
          localBuilder.setActionButton(BitmapFactory.decodeResource(paramContext.getResources(), 2130837534), LocaleController.getString("ShareFile", 2131166451), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 0, (Intent)localObject, 0), false);
          localBuilder.build().launchUrl((Activity)paramContext, paramUri);
          return;
        }
      }
    }
    catch (Exception str)
    {
      while (true)
      {
        FileLog.e(localException);
        try
        {
          paramUri = new Intent("android.intent.action.VIEW", paramUri);
          if (bool)
            paramUri.setComponent(new ComponentName(paramContext.getPackageName(), LaunchActivity.class.getName()));
          paramUri.putExtra("com.android.browser.application_id", paramContext.getPackageName());
          paramContext.startActivity(paramUri);
          return;
        }
        catch (Exception paramContext)
        {
          FileLog.e(paramContext);
          return;
        }
        String str = "";
      }
    }
  }

  public static void openUrl(Context paramContext, String paramString)
  {
    if (paramString == null)
      return;
    openUrl(paramContext, Uri.parse(paramString), true);
  }

  public static void openUrl(Context paramContext, String paramString, boolean paramBoolean)
  {
    if ((paramContext == null) || (paramString == null))
      return;
    openUrl(paramContext, Uri.parse(paramString), paramBoolean);
  }

  private static void setCurrentSession(CustomTabsSession paramCustomTabsSession)
  {
    customTabsCurrentSession = new WeakReference(paramCustomTabsSession);
  }

  public static void unbindCustomTabsService(Activity paramActivity)
  {
    if ((Build.VERSION.SDK_INT < 15) || (customTabsServiceConnection == null))
      return;
    Activity localActivity;
    if (currentCustomTabsActivity == null)
      localActivity = null;
    while (true)
    {
      if (localActivity == paramActivity)
        currentCustomTabsActivity.clear();
      try
      {
        paramActivity.unbindService(customTabsServiceConnection);
        customTabsClient = null;
        customTabsSession = null;
        return;
        localActivity = (Activity)currentCustomTabsActivity.get();
      }
      catch (Exception paramActivity)
      {
        while (true)
          FileLog.e(paramActivity);
      }
    }
  }

  private static class NavigationCallback extends CustomTabsCallback
  {
    public void onNavigationEvent(int paramInt, Bundle paramBundle)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.browser.Browser
 * JD-Core Version:    0.6.0
 */