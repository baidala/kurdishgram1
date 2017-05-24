package org.vidogram.messenger.support.customtabs;

import android.os.Bundle;

public class CustomTabsCallback
{
  public static final int NAVIGATION_ABORTED = 4;
  public static final int NAVIGATION_FAILED = 3;
  public static final int NAVIGATION_FINISHED = 2;
  public static final int NAVIGATION_STARTED = 1;
  public static final int TAB_HIDDEN = 6;
  public static final int TAB_SHOWN = 5;

  public void extraCallback(String paramString, Bundle paramBundle)
  {
  }

  public void onNavigationEvent(int paramInt, Bundle paramBundle)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.customtabs.CustomTabsCallback
 * JD-Core Version:    0.6.0
 */