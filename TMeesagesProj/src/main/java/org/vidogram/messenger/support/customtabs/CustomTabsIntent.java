package org.vidogram.messenger.support.customtabs;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.b.a;
import android.support.v4.b.c;
import android.support.v4.b.j;
import java.util.ArrayList;

public final class CustomTabsIntent
{
  public static final String EXTRA_ACTION_BUTTON_BUNDLE = "android.support.customtabs.extra.ACTION_BUTTON_BUNDLE";
  public static final String EXTRA_CLOSE_BUTTON_ICON = "android.support.customtabs.extra.CLOSE_BUTTON_ICON";
  public static final String EXTRA_DEFAULT_SHARE_MENU_ITEM = "android.support.customtabs.extra.SHARE_MENU_ITEM";
  public static final String EXTRA_ENABLE_URLBAR_HIDING = "android.support.customtabs.extra.ENABLE_URLBAR_HIDING";
  public static final String EXTRA_EXIT_ANIMATION_BUNDLE = "android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE";
  public static final String EXTRA_MENU_ITEMS = "android.support.customtabs.extra.MENU_ITEMS";
  public static final String EXTRA_SECONDARY_TOOLBAR_COLOR = "android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR";
  public static final String EXTRA_SESSION = "android.support.customtabs.extra.SESSION";
  public static final String EXTRA_TINT_ACTION_BUTTON = "android.support.customtabs.extra.TINT_ACTION_BUTTON";
  public static final String EXTRA_TITLE_VISIBILITY_STATE = "android.support.customtabs.extra.TITLE_VISIBILITY";
  public static final String EXTRA_TOOLBAR_COLOR = "android.support.customtabs.extra.TOOLBAR_COLOR";
  public static final String EXTRA_TOOLBAR_ITEMS = "android.support.customtabs.extra.TOOLBAR_ITEMS";
  public static final String KEY_DESCRIPTION = "android.support.customtabs.customaction.DESCRIPTION";
  public static final String KEY_ICON = "android.support.customtabs.customaction.ICON";
  public static final String KEY_ID = "android.support.customtabs.customaction.ID";
  public static final String KEY_MENU_ITEM_TITLE = "android.support.customtabs.customaction.MENU_ITEM_TITLE";
  public static final String KEY_PENDING_INTENT = "android.support.customtabs.customaction.PENDING_INTENT";
  private static final int MAX_TOOLBAR_ITEMS = 5;
  public static final int NO_TITLE = 0;
  public static final int SHOW_PAGE_TITLE = 1;
  public static final int TOOLBAR_ACTION_BUTTON_ID = 0;
  public final Intent intent;
  public final Bundle startAnimationBundle;

  private CustomTabsIntent(Intent paramIntent, Bundle paramBundle)
  {
    this.intent = paramIntent;
    this.startAnimationBundle = paramBundle;
  }

  public static int getMaxToolbarItems()
  {
    return 5;
  }

  public void launchUrl(Activity paramActivity, Uri paramUri)
  {
    this.intent.setData(paramUri);
    a.a(paramActivity, this.intent, this.startAnimationBundle);
  }

  public static final class Builder
  {
    private ArrayList<Bundle> mActionButtons = null;
    private final Intent mIntent = new Intent("android.intent.action.VIEW");
    private ArrayList<Bundle> mMenuItems = null;
    private Bundle mStartAnimationBundle = null;

    public Builder()
    {
      this(null);
    }

    public Builder(CustomTabsSession paramCustomTabsSession)
    {
      if (paramCustomTabsSession != null)
        this.mIntent.setPackage(paramCustomTabsSession.getComponentName().getPackageName());
      Bundle localBundle = new Bundle();
      if (paramCustomTabsSession == null);
      for (paramCustomTabsSession = localObject; ; paramCustomTabsSession = paramCustomTabsSession.getBinder())
      {
        j.a(localBundle, "android.support.customtabs.extra.SESSION", paramCustomTabsSession);
        this.mIntent.putExtras(localBundle);
        return;
      }
    }

    public Builder addDefaultShareMenuItem()
    {
      this.mIntent.putExtra("android.support.customtabs.extra.SHARE_MENU_ITEM", true);
      return this;
    }

    public Builder addMenuItem(String paramString, PendingIntent paramPendingIntent)
    {
      if (this.mMenuItems == null)
        this.mMenuItems = new ArrayList();
      Bundle localBundle = new Bundle();
      localBundle.putString("android.support.customtabs.customaction.MENU_ITEM_TITLE", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mMenuItems.add(localBundle);
      return this;
    }

    public Builder addToolbarItem(int paramInt, Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent)
    {
      if (this.mActionButtons == null)
        this.mActionButtons = new ArrayList();
      if (this.mActionButtons.size() >= 5)
        throw new IllegalStateException("Exceeded maximum toolbar item count of 5");
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.support.customtabs.customaction.ID", paramInt);
      localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
      localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mActionButtons.add(localBundle);
      return this;
    }

    public CustomTabsIntent build()
    {
      if (this.mMenuItems != null)
        this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.MENU_ITEMS", this.mMenuItems);
      if (this.mActionButtons != null)
        this.mIntent.putParcelableArrayListExtra("android.support.customtabs.extra.TOOLBAR_ITEMS", this.mActionButtons);
      return new CustomTabsIntent(this.mIntent, this.mStartAnimationBundle, null);
    }

    public Builder enableUrlBarHiding()
    {
      this.mIntent.putExtra("android.support.customtabs.extra.ENABLE_URLBAR_HIDING", true);
      return this;
    }

    public Builder setActionButton(Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent)
    {
      return setActionButton(paramBitmap, paramString, paramPendingIntent, false);
    }

    public Builder setActionButton(Bitmap paramBitmap, String paramString, PendingIntent paramPendingIntent, boolean paramBoolean)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("android.support.customtabs.customaction.ID", 0);
      localBundle.putParcelable("android.support.customtabs.customaction.ICON", paramBitmap);
      localBundle.putString("android.support.customtabs.customaction.DESCRIPTION", paramString);
      localBundle.putParcelable("android.support.customtabs.customaction.PENDING_INTENT", paramPendingIntent);
      this.mIntent.putExtra("android.support.customtabs.extra.ACTION_BUTTON_BUNDLE", localBundle);
      this.mIntent.putExtra("android.support.customtabs.extra.TINT_ACTION_BUTTON", paramBoolean);
      return this;
    }

    public Builder setCloseButtonIcon(Bitmap paramBitmap)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.CLOSE_BUTTON_ICON", paramBitmap);
      return this;
    }

    public Builder setExitAnimations(Context paramContext, int paramInt1, int paramInt2)
    {
      paramContext = c.a(paramContext, paramInt1, paramInt2).a();
      this.mIntent.putExtra("android.support.customtabs.extra.EXIT_ANIMATION_BUNDLE", paramContext);
      return this;
    }

    public Builder setSecondaryToolbarColor(int paramInt)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.SECONDARY_TOOLBAR_COLOR", paramInt);
      return this;
    }

    public Builder setShowTitle(boolean paramBoolean)
    {
      Intent localIntent = this.mIntent;
      if (paramBoolean);
      for (int i = 1; ; i = 0)
      {
        localIntent.putExtra("android.support.customtabs.extra.TITLE_VISIBILITY", i);
        return this;
      }
    }

    public Builder setStartAnimations(Context paramContext, int paramInt1, int paramInt2)
    {
      this.mStartAnimationBundle = c.a(paramContext, paramInt1, paramInt2).a();
      return this;
    }

    public Builder setToolbarColor(int paramInt)
    {
      this.mIntent.putExtra("android.support.customtabs.extra.TOOLBAR_COLOR", paramInt);
      return this;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsIntent
 * JD-Core Version:    0.6.0
 */