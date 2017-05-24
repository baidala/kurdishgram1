package org.vidogram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Parcelable;
import android.provider.Settings.System;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.NotificationsController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_resetNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextColorCell;
import org.vidogram.ui.Cells.TextDetailSettingsCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class NotificationsSettingsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListAdapter adapter;
  private int androidAutoAlertRow;
  private int badgeNumberRow;
  private int callsRingtoneRow;
  private int callsSectionRow;
  private int callsSectionRow2;
  private int callsVibrateRow;
  private int contactJoinedRow;
  private int eventsSectionRow;
  private int eventsSectionRow2;
  private int groupAlertRow;
  private int groupLedRow;
  private int groupPopupNotificationRow;
  private int groupPreviewRow;
  private int groupPriorityRow;
  private int groupSectionRow;
  private int groupSectionRow2;
  private int groupSoundRow;
  private int groupVibrateRow;
  private int inappPreviewRow;
  private int inappPriorityRow;
  private int inappSectionRow;
  private int inappSectionRow2;
  private int inappSoundRow;
  private int inappVibrateRow;
  private int inchatSoundRow;
  private RecyclerListView listView;
  private int messageAlertRow;
  private int messageLedRow;
  private int messagePopupNotificationRow;
  private int messagePreviewRow;
  private int messagePriorityRow;
  private int messageSectionRow;
  private int messageSoundRow;
  private int messageVibrateRow;
  private int notificationsServiceConnectionRow;
  private int notificationsServiceRow;
  private int otherSectionRow;
  private int otherSectionRow2;
  private int pinnedMessageRow;
  private int repeatRow;
  private int resetNotificationsRow;
  private int resetSectionRow;
  private int resetSectionRow2;
  private boolean reseting = false;
  private int rowCount = 0;

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NotificationsAndSounds", 2131166129));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          NotificationsSettingsActivity.this.finishFragment();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    ((FrameLayout)localObject).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    });
    this.listView.setVerticalScrollBarEnabled(false);
    ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    localObject = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.adapter = paramContext;
    ((RecyclerListView)localObject).setAdapter(paramContext);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        String str1 = null;
        int j = 2;
        boolean bool3 = true;
        Object localObject1;
        Object localObject3;
        boolean bool2;
        boolean bool1;
        if ((paramInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramInt == NotificationsSettingsActivity.this.groupAlertRow))
        {
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          localObject3 = ((SharedPreferences)localObject1).edit();
          if (paramInt == NotificationsSettingsActivity.this.messageAlertRow)
          {
            bool2 = ((SharedPreferences)localObject1).getBoolean("EnableAll", true);
            if (!bool2)
            {
              bool1 = true;
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableAll", bool1);
              bool1 = bool2;
              ((SharedPreferences.Editor)localObject3).commit();
              localObject1 = NotificationsSettingsActivity.this;
              if (paramInt != NotificationsSettingsActivity.this.groupAlertRow)
                break label223;
            }
          }
          label133: label154: label160: label223: for (bool2 = true; ; bool2 = false)
          {
            ((NotificationsSettingsActivity)localObject1).updateServerNotificationsSettings(bool2);
            if ((paramView instanceof TextCheckCell))
            {
              paramView = (TextCheckCell)paramView;
              if (bool1)
                break label2637;
              bool1 = bool3;
              paramView.setChecked(bool1);
            }
            return;
            bool1 = false;
            break;
            if (paramInt != NotificationsSettingsActivity.this.groupAlertRow)
              break label2667;
            bool2 = ((SharedPreferences)localObject1).getBoolean("EnableGroup", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableGroup", bool1);
              bool1 = bool2;
              break;
            }
          }
        }
        if ((paramInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramInt == NotificationsSettingsActivity.this.groupPreviewRow))
        {
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          localObject3 = ((SharedPreferences)localObject1).edit();
          if (paramInt == NotificationsSettingsActivity.this.messagePreviewRow)
          {
            bool2 = ((SharedPreferences)localObject1).getBoolean("EnablePreviewAll", true);
            if (!bool2)
            {
              bool1 = true;
              label302: ((SharedPreferences.Editor)localObject3).putBoolean("EnablePreviewAll", bool1);
              bool1 = bool2;
              label318: ((SharedPreferences.Editor)localObject3).commit();
              localObject1 = NotificationsSettingsActivity.this;
              if (paramInt != NotificationsSettingsActivity.this.groupPreviewRow)
                break label418;
            }
          }
          label418: for (bool2 = true; ; bool2 = false)
          {
            ((NotificationsSettingsActivity)localObject1).updateServerNotificationsSettings(bool2);
            break;
            bool1 = false;
            break label302;
            if (paramInt != NotificationsSettingsActivity.this.groupPreviewRow)
              break label2661;
            bool2 = ((SharedPreferences)localObject1).getBoolean("EnablePreviewGroup", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnablePreviewGroup", bool1);
              bool1 = bool2;
              break;
            }
          }
        }
        if ((paramInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramInt == NotificationsSettingsActivity.this.groupSoundRow) || (paramInt == NotificationsSettingsActivity.this.callsRingtoneRow));
        while (true)
        {
          Object localObject6;
          Object localObject5;
          try
          {
            localObject6 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject5 = new Intent("android.intent.action.RINGTONE_PICKER");
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              break label2680;
            i = 1;
            ((Intent)localObject5).putExtra("android.intent.extra.ringtone.TYPE", i);
            ((Intent)localObject5).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
            i = j;
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              continue;
            i = 1;
            ((Intent)localObject5).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(i));
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              continue;
            localObject3 = Settings.System.DEFAULT_RINGTONE_URI;
            if (localObject3 == null)
              break label2655;
            localObject4 = ((Uri)localObject3).getPath();
            if (paramInt != NotificationsSettingsActivity.this.messageSoundRow)
              continue;
            localObject1 = ((SharedPreferences)localObject6).getString("GlobalSoundPath", (String)localObject4);
            if ((localObject1 == null) || (((String)localObject1).equals("NoSound")))
              break label2649;
            if (!((String)localObject1).equals(localObject4))
              continue;
            break label2673;
            ((Intent)localObject5).putExtra("android.intent.extra.ringtone.EXISTING_URI", (Parcelable)localObject1);
            NotificationsSettingsActivity.this.startActivityForResult((Intent)localObject5, paramInt);
            bool1 = false;
            break label133;
            localObject3 = Settings.System.DEFAULT_NOTIFICATION_URI;
            continue;
            localObject3 = Uri.parse((String)localObject1);
            break label2673;
            if (paramInt != NotificationsSettingsActivity.this.groupSoundRow)
              continue;
            localObject6 = ((SharedPreferences)localObject6).getString("GroupSoundPath", (String)localObject4);
            localObject1 = str1;
            if (localObject6 == null)
              continue;
            localObject1 = str1;
            if (((String)localObject6).equals("NoSound"))
              continue;
            if (!((String)localObject6).equals(localObject4))
              continue;
            localObject1 = localObject3;
            continue;
            localObject1 = Uri.parse((String)localObject6);
            continue;
            localObject1 = str1;
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              continue;
            localObject6 = ((SharedPreferences)localObject6).getString("CallsRingtonfePath", (String)localObject4);
            localObject1 = str1;
            if (localObject6 == null)
              continue;
            localObject1 = str1;
            if (((String)localObject6).equals("NoSound"))
              continue;
            if (!((String)localObject6).equals(localObject4))
              continue;
            localObject1 = localObject3;
            continue;
            localObject1 = Uri.parse((String)localObject6);
            continue;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            bool1 = false;
          }
          break label133;
          if (paramInt == NotificationsSettingsActivity.this.resetNotificationsRow)
          {
            if (NotificationsSettingsActivity.this.reseting)
              break label160;
            NotificationsSettingsActivity.access$802(NotificationsSettingsActivity.this, true);
            localObject2 = new TLRPC.TL_account_resetNotifySettings();
            ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    MessagesController.getInstance().enableJoined = true;
                    NotificationsSettingsActivity.access$802(NotificationsSettingsActivity.this, false);
                    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                    localEditor.clear();
                    localEditor.commit();
                    NotificationsSettingsActivity.this.adapter.notifyDataSetChanged();
                    if (NotificationsSettingsActivity.this.getParentActivity() != null)
                      Toast.makeText(NotificationsSettingsActivity.this.getParentActivity(), LocaleController.getString("ResetNotificationsText", 2131166350), 0).show();
                  }
                });
              }
            });
            bool1 = false;
            break label133;
          }
          if (paramInt == NotificationsSettingsActivity.this.inappSoundRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableInAppSounds", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppSounds", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.inappVibrateRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableInAppVibrate", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppVibrate", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.inappPreviewRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableInAppPreview", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppPreview", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.inchatSoundRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableInChatSound", true);
            if (!bool2)
            {
              bool1 = true;
              label1192: ((SharedPreferences.Editor)localObject3).putBoolean("EnableInChatSound", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              localObject2 = NotificationsController.getInstance();
              if (bool2)
                break label1245;
            }
            label1245: for (bool1 = true; ; bool1 = false)
            {
              ((NotificationsController)localObject2).setInChatSoundEnabled(bool1);
              bool1 = bool2;
              break;
              bool1 = false;
              break label1192;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.inappPriorityRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableInAppPriority", false);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableInAppPriority", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.contactJoinedRow)
          {
            localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject2 = ((SharedPreferences)localObject3).edit();
            bool2 = ((SharedPreferences)localObject3).getBoolean("EnableContactJoined", true);
            localObject3 = MessagesController.getInstance();
            if (!bool2)
            {
              bool1 = true;
              label1391: ((MessagesController)localObject3).enableJoined = bool1;
              if (bool2)
                break label1439;
            }
            label1439: for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject2).putBoolean("EnableContactJoined", bool1);
              ((SharedPreferences.Editor)localObject2).commit();
              bool1 = bool2;
              break;
              bool1 = false;
              break label1391;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("PinnedMessages", true);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("PinnedMessages", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("EnableAutoNotifications", false);
            if (!bool2);
            for (bool1 = true; ; bool1 = false)
            {
              ((SharedPreferences.Editor)localObject3).putBoolean("EnableAutoNotifications", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              bool1 = bool2;
              break;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.badgeNumberRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = ((SharedPreferences)localObject2).edit();
            bool2 = ((SharedPreferences)localObject2).getBoolean("badgeNumber", true);
            if (!bool2)
            {
              bool1 = true;
              label1669: ((SharedPreferences.Editor)localObject3).putBoolean("badgeNumber", bool1);
              ((SharedPreferences.Editor)localObject3).commit();
              localObject2 = NotificationsController.getInstance();
              if (bool2)
                break label1723;
            }
            label1723: for (bool1 = true; ; bool1 = false)
            {
              ((NotificationsController)localObject2).setBadgeEnabled(bool1);
              bool1 = bool2;
              break;
              bool1 = false;
              break label1669;
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            bool2 = ((SharedPreferences)localObject2).getBoolean("pushConnection", true);
            localObject2 = ((SharedPreferences)localObject2).edit();
            if (!bool2)
            {
              bool1 = true;
              label1781: ((SharedPreferences.Editor)localObject2).putBoolean("pushConnection", bool1);
              ((SharedPreferences.Editor)localObject2).commit();
              if (bool2)
                break label1827;
              ConnectionsManager.getInstance().setPushConnectionEnabled(true);
            }
            while (true)
            {
              bool1 = bool2;
              break;
              bool1 = false;
              break label1781;
              label1827: ConnectionsManager.getInstance().setPushConnectionEnabled(false);
            }
          }
          if (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow)
          {
            localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            bool2 = ((SharedPreferences)localObject2).getBoolean("pushService", true);
            localObject2 = ((SharedPreferences)localObject2).edit();
            if (!bool2)
            {
              bool1 = true;
              label1889: ((SharedPreferences.Editor)localObject2).putBoolean("pushService", bool1);
              ((SharedPreferences.Editor)localObject2).commit();
              if (bool2)
                break label1931;
              ApplicationLoader.startPushService();
            }
            while (true)
            {
              bool1 = bool2;
              break;
              bool1 = false;
              break label1889;
              label1931: ApplicationLoader.stopPushService();
            }
          }
          if ((paramInt == NotificationsSettingsActivity.this.messageLedRow) || (paramInt == NotificationsSettingsActivity.this.groupLedRow))
          {
            if (NotificationsSettingsActivity.this.getParentActivity() == null)
              break label160;
            localObject2 = NotificationsSettingsActivity.this;
            localObject3 = NotificationsSettingsActivity.this.getParentActivity();
            if (paramInt == NotificationsSettingsActivity.this.groupLedRow)
            {
              bool1 = true;
              label1998: if (paramInt != NotificationsSettingsActivity.this.messageLedRow)
                break label2049;
            }
            label2049: for (bool2 = true; ; bool2 = false)
            {
              ((NotificationsSettingsActivity)localObject2).showDialog(AlertsCreator.createColorSelectDialog((Activity)localObject3, 0L, bool1, bool2, new Runnable(paramInt)
              {
                public void run()
                {
                  NotificationsSettingsActivity.this.adapter.notifyItemChanged(this.val$position);
                }
              }));
              bool1 = false;
              break;
              bool1 = false;
              break label1998;
            }
          }
          if ((paramInt == NotificationsSettingsActivity.this.messagePopupNotificationRow) || (paramInt == NotificationsSettingsActivity.this.groupPopupNotificationRow))
          {
            if (NotificationsSettingsActivity.this.getParentActivity() == null)
              break label160;
            localObject2 = NotificationsSettingsActivity.this;
            localObject3 = NotificationsSettingsActivity.this.getParentActivity();
            localObject4 = NotificationsSettingsActivity.this;
            if (paramInt == NotificationsSettingsActivity.this.groupPopupNotificationRow)
            {
              bool1 = true;
              label2122: if (paramInt != NotificationsSettingsActivity.this.messagePopupNotificationRow)
                break label2174;
            }
            label2174: for (bool2 = true; ; bool2 = false)
            {
              ((NotificationsSettingsActivity)localObject2).showDialog(AlertsCreator.createPopupSelectDialog((Activity)localObject3, (BaseFragment)localObject4, bool1, bool2, new Runnable(paramInt)
              {
                public void run()
                {
                  NotificationsSettingsActivity.this.adapter.notifyItemChanged(this.val$position);
                }
              }));
              bool1 = false;
              break;
              bool1 = false;
              break label2122;
            }
          }
          if ((paramInt == NotificationsSettingsActivity.this.messageVibrateRow) || (paramInt == NotificationsSettingsActivity.this.groupVibrateRow) || (paramInt == NotificationsSettingsActivity.this.callsVibrateRow))
          {
            if (NotificationsSettingsActivity.this.getParentActivity() == null)
              break label160;
            if (paramInt == NotificationsSettingsActivity.this.messageVibrateRow)
              localObject2 = "vibrate_messages";
          }
          while (true)
          {
            NotificationsSettingsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(NotificationsSettingsActivity.this.getParentActivity(), NotificationsSettingsActivity.this, 0L, (String)localObject2, new Runnable(paramInt)
            {
              public void run()
              {
                NotificationsSettingsActivity.this.adapter.notifyItemChanged(this.val$position);
              }
            }));
            bool1 = false;
            break;
            if (paramInt == NotificationsSettingsActivity.this.groupVibrateRow)
            {
              localObject2 = "vibrate_group";
              continue;
            }
            if (paramInt == NotificationsSettingsActivity.this.callsVibrateRow)
            {
              localObject2 = "vibrate_calls";
              continue;
              if ((paramInt == NotificationsSettingsActivity.this.messagePriorityRow) || (paramInt == NotificationsSettingsActivity.this.groupPriorityRow))
              {
                localObject2 = NotificationsSettingsActivity.this;
                localObject3 = NotificationsSettingsActivity.this.getParentActivity();
                localObject4 = NotificationsSettingsActivity.this;
                if (paramInt == NotificationsSettingsActivity.this.groupPriorityRow)
                {
                  bool1 = true;
                  label2374: if (paramInt != NotificationsSettingsActivity.this.messagePriorityRow)
                    break label2427;
                }
                label2427: for (bool2 = true; ; bool2 = false)
                {
                  ((NotificationsSettingsActivity)localObject2).showDialog(AlertsCreator.createPrioritySelectDialog((Activity)localObject3, (BaseFragment)localObject4, 0L, bool1, bool2, new Runnable(paramInt)
                  {
                    public void run()
                    {
                      NotificationsSettingsActivity.this.adapter.notifyItemChanged(this.val$position);
                    }
                  }));
                  bool1 = false;
                  break;
                  bool1 = false;
                  break label2374;
                }
              }
              if (paramInt == NotificationsSettingsActivity.this.repeatRow)
              {
                localObject2 = new AlertDialog.Builder(NotificationsSettingsActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("RepeatNotifications", 2131166322));
                localObject3 = LocaleController.getString("RepeatDisabled", 2131166321);
                localObject4 = LocaleController.formatPluralString("Minutes", 5);
                str1 = LocaleController.formatPluralString("Minutes", 10);
                localObject5 = LocaleController.formatPluralString("Minutes", 30);
                localObject6 = LocaleController.formatPluralString("Hours", 1);
                String str2 = LocaleController.formatPluralString("Hours", 2);
                String str3 = LocaleController.formatPluralString("Hours", 4);
                6 local6 = new DialogInterface.OnClickListener(paramInt)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    int i = 5;
                    if (paramInt == 1)
                      paramInt = i;
                    while (true)
                    {
                      ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("repeat_messages", paramInt).commit();
                      NotificationsSettingsActivity.this.adapter.notifyItemChanged(this.val$position);
                      return;
                      if (paramInt == 2)
                      {
                        paramInt = 10;
                        continue;
                      }
                      if (paramInt == 3)
                      {
                        paramInt = 30;
                        continue;
                      }
                      if (paramInt == 4)
                      {
                        paramInt = 60;
                        continue;
                      }
                      if (paramInt == 5)
                      {
                        paramInt = 120;
                        continue;
                      }
                      if (paramInt == 6)
                      {
                        paramInt = 240;
                        continue;
                      }
                      paramInt = 0;
                    }
                  }
                };
                ((AlertDialog.Builder)localObject2).setItems(new CharSequence[] { localObject3, localObject4, str1, localObject5, localObject6, str2, str3 }, local6);
                ((AlertDialog.Builder)localObject2).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                NotificationsSettingsActivity.this.showDialog(((AlertDialog.Builder)localObject2).create());
              }
              bool1 = false;
              break;
              label2637: bool1 = false;
              break label154;
            }
            localObject2 = null;
          }
          label2649: localObject3 = null;
          break label2673;
          label2655: Object localObject4 = null;
          continue;
          label2661: bool1 = false;
          break label318;
          label2667: bool1 = false;
          break;
          label2673: Object localObject2 = localObject3;
          continue;
          label2680: int i = 2;
        }
      }
    });
    return (View)this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.notificationsSettingsUpdated)
      this.adapter.notifyDataSetChanged();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { HeaderCell.class, TextCheckCell.class, TextDetailSettingsCell.class, TextColorCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextColorCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    Uri localUri;
    SharedPreferences.Editor localEditor;
    Ringtone localRingtone;
    if (paramInt2 == -1)
    {
      localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
      localEditor = null;
      paramIntent = localEditor;
      if (localUri != null)
      {
        localRingtone = RingtoneManager.getRingtone(getParentActivity(), localUri);
        paramIntent = localEditor;
        if (localRingtone != null)
        {
          if (paramInt1 != this.callsRingtoneRow)
            break label173;
          if (!localUri.equals(Settings.System.DEFAULT_RINGTONE_URI))
            break label160;
          paramIntent = LocaleController.getString("DefaultRingtone", 2131165627);
          localRingtone.stop();
        }
      }
      localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
      if (paramInt1 != this.messageSoundRow)
        break label241;
      if ((paramIntent == null) || (localUri == null))
        break label210;
      localEditor.putString("GlobalSound", paramIntent);
      localEditor.putString("GlobalSoundPath", localUri.toString());
    }
    while (true)
    {
      localEditor.commit();
      this.adapter.notifyItemChanged(paramInt1);
      return;
      label160: paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label173: if (localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI))
      {
        paramIntent = LocaleController.getString("SoundDefault", 2131166481);
        break;
      }
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      label210: localEditor.putString("GlobalSound", "NoSound");
      localEditor.putString("GlobalSoundPath", "NoSound");
      continue;
      label241: if (paramInt1 == this.groupSoundRow)
      {
        if ((paramIntent != null) && (localUri != null))
        {
          localEditor.putString("GroupSound", paramIntent);
          localEditor.putString("GroupSoundPath", localUri.toString());
          continue;
        }
        localEditor.putString("GroupSound", "NoSound");
        localEditor.putString("GroupSoundPath", "NoSound");
        continue;
      }
      if (paramInt1 != this.callsRingtoneRow)
        continue;
      if ((paramIntent != null) && (localUri != null))
      {
        localEditor.putString("CallsRingtone", paramIntent);
        localEditor.putString("CallsRingtonePath", localUri.toString());
        continue;
      }
      localEditor.putString("CallsRingtone", "NoSound");
      localEditor.putString("CallsRingtonePath", "NoSound");
    }
  }

  public boolean onFragmentCreate()
  {
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageAlertRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagePreviewRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageLedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageVibrateRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagePopupNotificationRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messageSoundRow = i;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagePriorityRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupAlertRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPreviewRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupLedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPopupNotificationRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupSoundRow = i;
      if (Build.VERSION.SDK_INT < 21)
        break label749;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupPriorityRow = i;
      label305: i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappSoundRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inappPreviewRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.inchatSoundRow = i;
      if (Build.VERSION.SDK_INT < 21)
        break label757;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label749: label757: for (this.inappPriorityRow = i; ; this.inappPriorityRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsRingtoneRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.eventsSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.eventsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.contactJoinedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.pinnedMessageRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.otherSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.otherSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.notificationsServiceRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.notificationsServiceConnectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.badgeNumberRow = i;
      this.androidAutoAlertRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.repeatRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetSectionRow2 = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetNotificationsRow = i;
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      return super.onFragmentCreate();
      this.messagePriorityRow = -1;
      break;
      this.groupPriorityRow = -1;
      break label305;
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
  }

  public void updateServerNotificationsSettings(boolean paramBoolean)
  {
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      return NotificationsSettingsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == NotificationsSettingsActivity.this.messageSectionRow) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow) || (paramInt == NotificationsSettingsActivity.this.eventsSectionRow) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow) || (paramInt == NotificationsSettingsActivity.this.callsSectionRow))
        return 0;
      if ((paramInt == NotificationsSettingsActivity.this.messageAlertRow) || (paramInt == NotificationsSettingsActivity.this.messagePreviewRow) || (paramInt == NotificationsSettingsActivity.this.groupAlertRow) || (paramInt == NotificationsSettingsActivity.this.groupPreviewRow) || (paramInt == NotificationsSettingsActivity.this.inappSoundRow) || (paramInt == NotificationsSettingsActivity.this.inappVibrateRow) || (paramInt == NotificationsSettingsActivity.this.inappPreviewRow) || (paramInt == NotificationsSettingsActivity.this.contactJoinedRow) || (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow) || (paramInt == NotificationsSettingsActivity.this.badgeNumberRow) || (paramInt == NotificationsSettingsActivity.this.inappPriorityRow) || (paramInt == NotificationsSettingsActivity.this.inchatSoundRow) || (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow) || (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow))
        return 1;
      if ((paramInt == NotificationsSettingsActivity.this.messageLedRow) || (paramInt == NotificationsSettingsActivity.this.groupLedRow))
        return 3;
      if ((paramInt == NotificationsSettingsActivity.this.eventsSectionRow2) || (paramInt == NotificationsSettingsActivity.this.groupSectionRow2) || (paramInt == NotificationsSettingsActivity.this.inappSectionRow2) || (paramInt == NotificationsSettingsActivity.this.otherSectionRow2) || (paramInt == NotificationsSettingsActivity.this.resetSectionRow2) || (paramInt == NotificationsSettingsActivity.this.callsSectionRow2))
        return 4;
      if (paramInt == NotificationsSettingsActivity.this.resetNotificationsRow)
        return 2;
      return 5;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i != NotificationsSettingsActivity.this.messageSectionRow) && (i != NotificationsSettingsActivity.this.groupSectionRow) && (i != NotificationsSettingsActivity.this.inappSectionRow) && (i != NotificationsSettingsActivity.this.eventsSectionRow) && (i != NotificationsSettingsActivity.this.otherSectionRow) && (i != NotificationsSettingsActivity.this.resetSectionRow) && (i != NotificationsSettingsActivity.this.eventsSectionRow2) && (i != NotificationsSettingsActivity.this.groupSectionRow2) && (i != NotificationsSettingsActivity.this.inappSectionRow2) && (i != NotificationsSettingsActivity.this.otherSectionRow2) && (i != NotificationsSettingsActivity.this.resetSectionRow2) && (i != NotificationsSettingsActivity.this.callsSectionRow2) && (i != NotificationsSettingsActivity.this.callsSectionRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int j = 0;
      int i = 0;
      switch (paramViewHolder.getItemViewType())
      {
      case 4:
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 5:
      }
      Object localObject;
      TextSettingsCell localTextSettingsCell;
      while (true)
      {
        return;
        paramViewHolder = (HeaderCell)paramViewHolder.itemView;
        if (paramInt == NotificationsSettingsActivity.this.messageSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("MessageNotifications", 2131165965));
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.groupSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("GroupNotifications", 2131165797));
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inappSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("InAppNotifications", 2131165826));
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.eventsSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("Events", 2131165702));
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.otherSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("NotificationsOther", 2131166139));
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.resetSectionRow)
        {
          paramViewHolder.setText(LocaleController.getString("Reset", 2131166338));
          return;
        }
        if (paramInt != NotificationsSettingsActivity.this.callsSectionRow)
          continue;
        paramViewHolder.setText(LocaleController.getString("VoipNotificationSettings", 2131166591));
        return;
        paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        if (paramInt == NotificationsSettingsActivity.this.messageAlertRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("Alert", 2131165294), ((SharedPreferences)localObject).getBoolean("EnableAll", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.groupAlertRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("Alert", 2131165294), ((SharedPreferences)localObject).getBoolean("EnableGroup", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.messagePreviewRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("MessagePreview", 2131165966), ((SharedPreferences)localObject).getBoolean("EnablePreviewAll", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.groupPreviewRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("MessagePreview", 2131165966), ((SharedPreferences)localObject).getBoolean("EnablePreviewGroup", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inappSoundRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("InAppSounds", 2131165828), ((SharedPreferences)localObject).getBoolean("EnableInAppSounds", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inappVibrateRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("InAppVibrate", 2131165829), ((SharedPreferences)localObject).getBoolean("EnableInAppVibrate", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inappPreviewRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("InAppPreview", 2131165827), ((SharedPreferences)localObject).getBoolean("EnableInAppPreview", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inappPriorityRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("NotificationsPriority", 2131166140), ((SharedPreferences)localObject).getBoolean("EnableInAppPriority", false), false);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.contactJoinedRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("ContactJoined", 2131165572), ((SharedPreferences)localObject).getBoolean("EnableContactJoined", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.pinnedMessageRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("PinnedMessages", 2131166287), ((SharedPreferences)localObject).getBoolean("PinnedMessages", true), false);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.androidAutoAlertRow)
        {
          paramViewHolder.setTextAndCheck("Android Auto", ((SharedPreferences)localObject).getBoolean("EnableAutoNotifications", false), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.notificationsServiceRow)
        {
          paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("NotificationsService", 2131166146), LocaleController.getString("NotificationsServiceInfo", 2131166149), ((SharedPreferences)localObject).getBoolean("pushService", true), true, true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.notificationsServiceConnectionRow)
        {
          paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("NotificationsServiceConnection", 2131166147), LocaleController.getString("NotificationsServiceConnectionInfo", 2131166148), ((SharedPreferences)localObject).getBoolean("pushConnection", true), true, true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.badgeNumberRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("BadgeNumber", 2131165381), ((SharedPreferences)localObject).getBoolean("badgeNumber", true), true);
          return;
        }
        if (paramInt == NotificationsSettingsActivity.this.inchatSoundRow)
        {
          paramViewHolder.setTextAndCheck(LocaleController.getString("InChatSound", 2131165830), ((SharedPreferences)localObject).getBoolean("EnableInChatSound", true), true);
          return;
        }
        if (paramInt != NotificationsSettingsActivity.this.callsVibrateRow)
          continue;
        paramViewHolder.setTextAndCheck(LocaleController.getString("Vibrate", 2131166568), ((SharedPreferences)localObject).getBoolean("EnableCallVibrate", true), true);
        return;
        paramViewHolder = (TextDetailSettingsCell)paramViewHolder.itemView;
        paramViewHolder.setMultilineDetail(true);
        paramViewHolder.setTextAndValue(LocaleController.getString("ResetAllNotifications", 2131166344), LocaleController.getString("UndoAllCustom", 2131166532), false);
        return;
        paramViewHolder = (TextColorCell)paramViewHolder.itemView;
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        if (paramInt == NotificationsSettingsActivity.this.messageLedRow)
          paramInt = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
        while (true)
        {
          j = paramInt;
          if (i < 9)
          {
            if (TextColorCell.colorsToSave[i] == paramInt)
              j = TextColorCell.colors[i];
          }
          else
          {
            paramViewHolder.setTextAndColor(LocaleController.getString("LedColor", 2131165905), j, true);
            return;
            paramInt = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
            continue;
          }
          i += 1;
        }
        localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        if ((paramInt == NotificationsSettingsActivity.this.messageSoundRow) || (paramInt == NotificationsSettingsActivity.this.groupSoundRow) || (paramInt == NotificationsSettingsActivity.this.callsRingtoneRow))
        {
          paramViewHolder = null;
          if (paramInt == NotificationsSettingsActivity.this.messageSoundRow)
            paramViewHolder = ((SharedPreferences)localObject).getString("GlobalSound", LocaleController.getString("SoundDefault", 2131166481));
          while (true)
          {
            localObject = paramViewHolder;
            if (paramViewHolder.equals("NoSound"))
              localObject = LocaleController.getString("NoSound", 2131166052);
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              break;
            localTextSettingsCell.setTextAndValue(LocaleController.getString("VoipSettingsRingtone", 2131166606), (String)localObject, true);
            return;
            if (paramInt == NotificationsSettingsActivity.this.groupSoundRow)
            {
              paramViewHolder = ((SharedPreferences)localObject).getString("GroupSound", LocaleController.getString("SoundDefault", 2131166481));
              continue;
            }
            if (paramInt != NotificationsSettingsActivity.this.callsRingtoneRow)
              continue;
            paramViewHolder = ((SharedPreferences)localObject).getString("CallsRingtone", LocaleController.getString("DefaultRingtone", 2131165627));
          }
          localTextSettingsCell.setTextAndValue(LocaleController.getString("Sound", 2131166480), (String)localObject, true);
          return;
        }
        if ((paramInt != NotificationsSettingsActivity.this.messageVibrateRow) && (paramInt != NotificationsSettingsActivity.this.groupVibrateRow) && (paramInt != NotificationsSettingsActivity.this.callsVibrateRow))
          break;
        if (paramInt == NotificationsSettingsActivity.this.messageVibrateRow)
          i = ((SharedPreferences)localObject).getInt("vibrate_messages", 0);
        while (i == 0)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("VibrationDefault", 2131166569), true);
          return;
          if (paramInt == NotificationsSettingsActivity.this.groupVibrateRow)
          {
            i = ((SharedPreferences)localObject).getInt("vibrate_group", 0);
            continue;
          }
          i = j;
          if (paramInt != NotificationsSettingsActivity.this.callsVibrateRow)
            continue;
          i = ((SharedPreferences)localObject).getInt("vibrate_calls", 0);
        }
        if (i == 1)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("Short", 2131166465), true);
          return;
        }
        if (i == 2)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("VibrationDisabled", 2131166570), true);
          return;
        }
        if (i == 3)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("Long", 2131165934), true);
          return;
        }
        if (i != 4)
          continue;
        localTextSettingsCell.setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("OnlyIfSilent", 2131166162), true);
        return;
      }
      if (paramInt == NotificationsSettingsActivity.this.repeatRow)
      {
        paramInt = ((SharedPreferences)localObject).getInt("repeat_messages", 60);
        if (paramInt == 0)
          paramViewHolder = LocaleController.getString("RepeatNotificationsNever", 2131166323);
        while (true)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("RepeatNotifications", 2131166322), paramViewHolder, false);
          return;
          if (paramInt < 60)
          {
            paramViewHolder = LocaleController.formatPluralString("Minutes", paramInt);
            continue;
          }
          paramViewHolder = LocaleController.formatPluralString("Hours", paramInt / 60);
        }
      }
      if ((paramInt == NotificationsSettingsActivity.this.messagePriorityRow) || (paramInt == NotificationsSettingsActivity.this.groupPriorityRow))
        if (paramInt == NotificationsSettingsActivity.this.messagePriorityRow)
          paramInt = ((SharedPreferences)localObject).getInt("priority_messages", 1);
      while (true)
      {
        if (paramInt == 0)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityDefault", 2131166141), false);
          return;
          if (paramInt == NotificationsSettingsActivity.this.groupPriorityRow)
          {
            paramInt = ((SharedPreferences)localObject).getInt("priority_group", 1);
            continue;
          }
        }
        else
        {
          if (paramInt == 1)
          {
            localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityHigh", 2131166142), false);
            return;
          }
          if (paramInt != 2)
            break;
          localTextSettingsCell.setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityMax", 2131166144), false);
          return;
          if ((paramInt != NotificationsSettingsActivity.this.messagePopupNotificationRow) && (paramInt != NotificationsSettingsActivity.this.groupPopupNotificationRow))
            break;
          if (paramInt == NotificationsSettingsActivity.this.messagePopupNotificationRow)
            paramInt = ((SharedPreferences)localObject).getInt("popupAll", 0);
          while (true)
          {
            if (paramInt == 0)
              paramViewHolder = LocaleController.getString("NoPopup", 2131166041);
            while (true)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("PopupNotification", 2131166301), paramViewHolder, true);
              return;
              if (paramInt != NotificationsSettingsActivity.this.groupPopupNotificationRow)
                break label1828;
              paramInt = ((SharedPreferences)localObject).getInt("popupGroup", 0);
              break;
              if (paramInt == 1)
              {
                paramViewHolder = LocaleController.getString("OnlyWhenScreenOn", 2131166164);
                continue;
              }
              if (paramInt == 2)
              {
                paramViewHolder = LocaleController.getString("OnlyWhenScreenOff", 2131166163);
                continue;
              }
              paramViewHolder = LocaleController.getString("AlwaysShowPopup", 2131165306);
            }
            label1828: paramInt = 0;
          }
        }
        paramInt = 0;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextDetailSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextColorCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.NotificationsSettingsActivity
 * JD-Core Version:    0.6.0
 */