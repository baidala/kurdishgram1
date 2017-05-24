package org.vidogram.messenger.support.customtabsclient.shared;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CustomTabsHelper
{
  private static final String ACTION_CUSTOM_TABS_CONNECTION = "android.support.customtabs.action.CustomTabsService";
  static final String BETA_PACKAGE = "com.chrome.beta";
  static final String DEV_PACKAGE = "com.chrome.dev";
  private static final String EXTRA_CUSTOM_TABS_KEEP_ALIVE = "android.support.customtabs.extra.KEEP_ALIVE";
  static final String LOCAL_PACKAGE = "com.google.android.apps.chrome";
  static final String STABLE_PACKAGE = "com.android.chrome";
  private static final String TAG = "CustomTabsHelper";
  private static String sPackageNameToUse;

  public static void addKeepAliveExtra(Context paramContext, Intent paramIntent)
  {
    paramIntent.putExtra("android.support.customtabs.extra.KEEP_ALIVE", new Intent().setClassName(paramContext.getPackageName(), KeepAliveService.class.getCanonicalName()));
  }

  public static String getPackageNameToUse(Context paramContext)
  {
    if (sPackageNameToUse != null)
      return sPackageNameToUse;
    PackageManager localPackageManager = paramContext.getPackageManager();
    Intent localIntent1 = new Intent("android.intent.action.VIEW", Uri.parse("http://www.example.com"));
    Object localObject1 = localPackageManager.resolveActivity(localIntent1, 0);
    if (localObject1 != null);
    for (localObject1 = ((ResolveInfo)localObject1).activityInfo.packageName; ; localObject1 = null)
    {
      Object localObject2 = localPackageManager.queryIntentActivities(localIntent1, 0);
      ArrayList localArrayList = new ArrayList();
      localObject2 = ((List)localObject2).iterator();
      while (((Iterator)localObject2).hasNext())
      {
        ResolveInfo localResolveInfo = (ResolveInfo)((Iterator)localObject2).next();
        Intent localIntent2 = new Intent();
        localIntent2.setAction("android.support.customtabs.action.CustomTabsService");
        localIntent2.setPackage(localResolveInfo.activityInfo.packageName);
        if (localPackageManager.resolveService(localIntent2, 0) == null)
          continue;
        localArrayList.add(localResolveInfo.activityInfo.packageName);
      }
      if (localArrayList.isEmpty())
        sPackageNameToUse = null;
      while (true)
      {
        return sPackageNameToUse;
        if (localArrayList.size() == 1)
        {
          sPackageNameToUse = (String)localArrayList.get(0);
          continue;
        }
        if ((!TextUtils.isEmpty((CharSequence)localObject1)) && (!hasSpecializedHandlerIntents(paramContext, localIntent1)) && (localArrayList.contains(localObject1)))
        {
          sPackageNameToUse = (String)localObject1;
          continue;
        }
        if (localArrayList.contains("com.android.chrome"))
        {
          sPackageNameToUse = "com.android.chrome";
          continue;
        }
        if (localArrayList.contains("com.chrome.beta"))
        {
          sPackageNameToUse = "com.chrome.beta";
          continue;
        }
        if (localArrayList.contains("com.chrome.dev"))
        {
          sPackageNameToUse = "com.chrome.dev";
          continue;
        }
        if (!localArrayList.contains("com.google.android.apps.chrome"))
          continue;
        sPackageNameToUse = "com.google.android.apps.chrome";
      }
    }
  }

  public static String[] getPackages()
  {
    return new String[] { "", "com.android.chrome", "com.chrome.beta", "com.chrome.dev", "com.google.android.apps.chrome" };
  }

  private static boolean hasSpecializedHandlerIntents(Context paramContext, Intent paramIntent)
  {
    try
    {
      paramContext = paramContext.getPackageManager().queryIntentActivities(paramIntent, 64);
      if ((paramContext == null) || (paramContext.size() == 0))
        break label98;
      paramContext = paramContext.iterator();
      while (paramContext.hasNext())
      {
        paramIntent = (ResolveInfo)paramContext.next();
        IntentFilter localIntentFilter = paramIntent.filter;
        if ((localIntentFilter == null) || (localIntentFilter.countDataAuthorities() == 0) || (localIntentFilter.countDataPaths() == 0))
          continue;
        paramIntent = paramIntent.activityInfo;
        if (paramIntent != null)
          return true;
      }
    }
    catch (java.lang.RuntimeException paramContext)
    {
      Log.e("CustomTabsHelper", "Runtime exception while getting specialized handlers");
    }
    return false;
    label98: return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabsclient.shared.CustomTabsHelper
 * JD-Core Version:    0.6.0
 */