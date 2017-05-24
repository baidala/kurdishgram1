package org.vidogram.ui;

import android.widget.Toast;
import itman.Vidofilm.b;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.DrawerLayoutContainer;

class DialogsActivity$3 extends ActionBar.ActionBarMenuOnItemClick
{
  public void onItemClick(int paramInt)
  {
    boolean bool = true;
    if (paramInt == -1)
      if (DialogsActivity.access$700(this.this$0))
        this.this$0.finishFragment();
    do
    {
      do
        return;
      while (DialogsActivity.access$1800(this.this$0) == null);
      DialogsActivity.access$1900(this.this$0).getDrawerLayoutContainer().openDrawer(false);
      return;
      if (paramInt != 1)
        continue;
      if (!UserConfig.appLocked);
      while (true)
      {
        UserConfig.appLocked = bool;
        UserConfig.saveConfig(false);
        DialogsActivity.access$1100(this.this$0);
        return;
        bool = false;
      }
    }
    while (paramInt != 10);
    b localb = b.a(ApplicationLoader.applicationContext);
    if (localb.b())
    {
      DialogsActivity.access$2000(this.this$0).setIcon(2130837590);
      localb.a(false);
      Toast.makeText(this.this$0.getParentActivity(), LocaleController.getString("InvisibleModeDeeactive", 2131166773), 0).show();
      return;
    }
    localb.a(true);
    MessagesController.getInstance().updateTimerProcInSecretMode();
    DialogsActivity.access$2000(this.this$0).setIcon(2130837591);
    Toast.makeText(this.this$0.getParentActivity(), LocaleController.getString("InvisibleModeActive", 2131166772), 0).show();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DialogsActivity.3
 * JD-Core Version:    0.6.0
 */