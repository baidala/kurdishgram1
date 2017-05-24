package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
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
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.RadioCell;
import org.vidogram.ui.Cells.TextCheckBoxCell;
import org.vidogram.ui.Cells.TextColorCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class ProfileNotificationsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ListAdapter adapter;
  private AnimatorSet animatorSet;
  private int callsRow;
  private int callsVibrateRow;
  private int colorRow;
  private boolean customEnabled;
  private int customInfoRow;
  private int customRow;
  private long dialog_id;
  private int generalRow;
  private int ledInfoRow;
  private int ledRow;
  private RecyclerListView listView;
  private boolean notificationsEnabled;
  private int popupDisabledRow;
  private int popupEnabledRow;
  private int popupInfoRow;
  private int popupRow;
  private int priorityInfoRow;
  private int priorityRow;
  private int ringtoneInfoRow;
  private int ringtoneRow;
  private int rowCount;
  private int smartRow;
  private int soundRow;
  private int vibrateRow;

  public ProfileNotificationsActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.dialog_id = paramBundle.getLong("dialog_id");
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("CustomNotifications", 2131165606));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
        {
          if ((ProfileNotificationsActivity.this.notificationsEnabled) && (ProfileNotificationsActivity.this.customEnabled))
            ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("notify2_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
          ProfileNotificationsActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject = (FrameLayout)this.fragmentView;
    ((FrameLayout)localObject).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    localObject = this.listView;
    ListAdapter localListAdapter = new ListAdapter(paramContext);
    this.adapter = localListAdapter;
    ((RecyclerListView)localObject).setAdapter(localListAdapter);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    });
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener(paramContext)
    {
      public void onItemClick(View paramView, int paramInt)
      {
        Object localObject2 = null;
        int i = 2;
        int j = 0;
        Object localObject1;
        label165: label456: if ((paramInt == ProfileNotificationsActivity.this.customRow) && ((paramView instanceof TextCheckBoxCell)))
        {
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          localObject2 = ProfileNotificationsActivity.this;
          boolean bool;
          if (!ProfileNotificationsActivity.this.customEnabled)
          {
            bool = true;
            ProfileNotificationsActivity.access$102((ProfileNotificationsActivity)localObject2, bool);
            ProfileNotificationsActivity.access$002(ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.customEnabled);
            ((SharedPreferences)localObject1).edit().putBoolean("custom_" + ProfileNotificationsActivity.this.dialog_id, ProfileNotificationsActivity.this.customEnabled).commit();
            ((TextCheckBoxCell)paramView).setChecked(ProfileNotificationsActivity.this.customEnabled);
            i = ProfileNotificationsActivity.this.listView.getChildCount();
            paramView = new ArrayList();
            paramInt = j;
            if (paramInt >= i)
              break label361;
            localObject1 = ProfileNotificationsActivity.this.listView.getChildAt(paramInt);
            localObject1 = (RecyclerListView.Holder)ProfileNotificationsActivity.this.listView.getChildViewHolder((View)localObject1);
            j = ((RecyclerListView.Holder)localObject1).getItemViewType();
            if ((((RecyclerListView.Holder)localObject1).getAdapterPosition() != ProfileNotificationsActivity.this.customRow) && (j != 0))
              switch (j)
              {
              default:
              case 1:
              case 2:
              case 3:
              case 4:
              }
          }
          while (true)
          {
            paramInt += 1;
            break label165;
            bool = false;
            break;
            ((TextSettingsCell)((RecyclerListView.Holder)localObject1).itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, paramView);
            continue;
            ((TextInfoPrivacyCell)((RecyclerListView.Holder)localObject1).itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, paramView);
            continue;
            ((TextColorCell)((RecyclerListView.Holder)localObject1).itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, paramView);
            continue;
            ((RadioCell)((RecyclerListView.Holder)localObject1).itemView).setEnabled(ProfileNotificationsActivity.this.customEnabled, paramView);
          }
          label361: if (!paramView.isEmpty())
          {
            if (ProfileNotificationsActivity.this.animatorSet != null)
              ProfileNotificationsActivity.this.animatorSet.cancel();
            ProfileNotificationsActivity.access$502(ProfileNotificationsActivity.this, new AnimatorSet());
            ProfileNotificationsActivity.this.animatorSet.playTogether(paramView);
            ProfileNotificationsActivity.this.animatorSet.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if (paramAnimator.equals(ProfileNotificationsActivity.this.animatorSet))
                  ProfileNotificationsActivity.access$502(ProfileNotificationsActivity.this, null);
              }
            });
            ProfileNotificationsActivity.this.animatorSet.setDuration(150L);
            ProfileNotificationsActivity.this.animatorSet.start();
            break label456;
            break label456;
            break label456;
            break label456;
          }
        }
        do
          return;
        while (!ProfileNotificationsActivity.this.customEnabled);
        if (paramInt == ProfileNotificationsActivity.this.soundRow);
        while (true)
        {
          Object localObject3;
          String str;
          try
          {
            Intent localIntent = new Intent("android.intent.action.RINGTONE_PICKER");
            localIntent.putExtra("android.intent.extra.ringtone.TYPE", 2);
            localIntent.putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
            localIntent.putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(2));
            paramView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
            localObject3 = Settings.System.DEFAULT_NOTIFICATION_URI;
            if (localObject3 == null)
              break label1564;
            localObject1 = ((Uri)localObject3).getPath();
            str = paramView.getString("sound_path_" + ProfileNotificationsActivity.this.dialog_id, (String)localObject1);
            paramView = (View)localObject2;
            if (str == null)
              continue;
            paramView = (View)localObject2;
            if (str.equals("NoSound"))
              continue;
            if (str.equals(localObject1))
            {
              paramView = (View)localObject3;
              localIntent.putExtra("android.intent.extra.ringtone.EXISTING_URI", paramView);
              ProfileNotificationsActivity.this.startActivityForResult(localIntent, 12);
              return;
            }
          }
          catch (java.lang.Exception paramView)
          {
            FileLog.e(paramView);
            return;
          }
          paramView = Uri.parse(str);
          continue;
          if (paramInt == ProfileNotificationsActivity.this.ringtoneRow);
          while (true)
          {
            try
            {
              localObject2 = new Intent("android.intent.action.RINGTONE_PICKER");
              ((Intent)localObject2).putExtra("android.intent.extra.ringtone.TYPE", 1);
              ((Intent)localObject2).putExtra("android.intent.extra.ringtone.SHOW_DEFAULT", true);
              ((Intent)localObject2).putExtra("android.intent.extra.ringtone.DEFAULT_URI", RingtoneManager.getDefaultUri(1));
              localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              localObject1 = Settings.System.DEFAULT_NOTIFICATION_URI;
              if (localObject1 == null)
                break label1559;
              paramView = ((Uri)localObject1).getPath();
              localObject3 = ((SharedPreferences)localObject3).getString("ringtone_path_" + ProfileNotificationsActivity.this.dialog_id, paramView);
              if ((localObject3 == null) || (((String)localObject3).equals("NoSound")))
                break label1554;
              if (((String)localObject3).equals(paramView))
              {
                paramView = (View)localObject1;
                ((Intent)localObject2).putExtra("android.intent.extra.ringtone.EXISTING_URI", paramView);
                ProfileNotificationsActivity.this.startActivityForResult((Intent)localObject2, 13);
                return;
              }
            }
            catch (java.lang.Exception paramView)
            {
              FileLog.e(paramView);
              return;
            }
            paramView = Uri.parse((String)localObject3);
            continue;
            if (paramInt == ProfileNotificationsActivity.this.vibrateRow)
            {
              ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable()
              {
                public void run()
                {
                  if (ProfileNotificationsActivity.this.adapter != null)
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.vibrateRow);
                }
              }));
              return;
            }
            if (paramInt == ProfileNotificationsActivity.this.callsVibrateRow)
            {
              ProfileNotificationsActivity.this.showDialog(AlertsCreator.createVibrationSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, "calls_vibrate_", new Runnable()
              {
                public void run()
                {
                  if (ProfileNotificationsActivity.this.adapter != null)
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.callsVibrateRow);
                }
              }));
              return;
            }
            if (paramInt == ProfileNotificationsActivity.this.priorityRow)
            {
              ProfileNotificationsActivity.this.showDialog(AlertsCreator.createPrioritySelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this, ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable()
              {
                public void run()
                {
                  if (ProfileNotificationsActivity.this.adapter != null)
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.priorityRow);
                }
              }));
              return;
            }
            if (paramInt == ProfileNotificationsActivity.this.smartRow)
            {
              if (ProfileNotificationsActivity.this.getParentActivity() == null)
                break;
              paramView = ProfileNotificationsActivity.this.getParentActivity();
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              paramInt = ((SharedPreferences)localObject1).getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
              j = ((SharedPreferences)localObject1).getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
              if (paramInt != 0)
                break label1551;
              paramInt = i;
            }
            label1551: 
            while (true)
            {
              i = j / 60;
              localObject1 = new RecyclerListView(ProfileNotificationsActivity.this.getParentActivity());
              ((RecyclerListView)localObject1).setLayoutManager(new LinearLayoutManager(this.val$context, 1, false));
              ((RecyclerListView)localObject1).setClipToPadding(true);
              ((RecyclerListView)localObject1).setAdapter(new RecyclerListView.SelectionAdapter(paramView, paramInt + (i - 1) * 10 - 1)
              {
                public int getItemCount()
                {
                  return 100;
                }

                public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
                {
                  return true;
                }

                public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
                {
                  TextView localTextView = (TextView)paramViewHolder.itemView;
                  if (paramInt == this.val$selected);
                  for (paramViewHolder = "dialogTextGray"; ; paramViewHolder = "dialogTextBlack")
                  {
                    localTextView.setTextColor(Theme.getColor(paramViewHolder));
                    int i = paramInt / 10;
                    localTextView.setText(LocaleController.formatString("SmartNotificationsDetail", 2131166472, new Object[] { LocaleController.formatPluralString("Times", paramInt % 10 + 1), LocaleController.formatPluralString("Minutes", i + 1) }));
                    return;
                  }
                }

                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
                {
                  paramViewGroup = new TextView(this.val$context1)
                  {
                    protected void onMeasure(int paramInt1, int paramInt2)
                    {
                      super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
                    }
                  };
                  TextView localTextView = (TextView)paramViewGroup;
                  localTextView.setGravity(17);
                  localTextView.setTextSize(1, 18.0F);
                  localTextView.setSingleLine(true);
                  localTextView.setEllipsize(TextUtils.TruncateAt.END);
                  localTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                  return new RecyclerListView.Holder(paramViewGroup);
                }
              });
              ((RecyclerListView)localObject1).setPadding(0, AndroidUtilities.dp(12.0F), 0, AndroidUtilities.dp(8.0F));
              ((RecyclerListView)localObject1).setOnItemClickListener(new RecyclerListView.OnItemClickListener()
              {
                public void onItemClick(View paramView, int paramInt)
                {
                  if ((paramInt < 0) || (paramInt >= 100))
                    return;
                  int i = paramInt / 10;
                  paramView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                  paramView.edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, paramInt % 10 + 1).commit();
                  paramView.edit().putInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, (i + 1) * 60).commit();
                  if (ProfileNotificationsActivity.this.adapter != null)
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                  ProfileNotificationsActivity.this.dismissCurrentDialig();
                }
              });
              paramView = new AlertDialog.Builder(ProfileNotificationsActivity.this.getParentActivity());
              paramView.setTitle(LocaleController.getString("SmartNotificationsAlert", 2131166471));
              paramView.setView((View)localObject1);
              paramView.setPositiveButton(LocaleController.getString("Cancel", 2131165427), null);
              paramView.setNegativeButton(LocaleController.getString("SmartNotificationsDisabled", 2131166473), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 0).commit();
                  if (ProfileNotificationsActivity.this.adapter != null)
                    ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.smartRow);
                  ProfileNotificationsActivity.this.dismissCurrentDialig();
                }
              });
              ProfileNotificationsActivity.this.showDialog(paramView.create());
              return;
              if (paramInt == ProfileNotificationsActivity.this.colorRow)
              {
                if (ProfileNotificationsActivity.this.getParentActivity() == null)
                  break;
                ProfileNotificationsActivity.this.showDialog(AlertsCreator.createColorSelectDialog(ProfileNotificationsActivity.this.getParentActivity(), ProfileNotificationsActivity.this.dialog_id, false, false, new Runnable()
                {
                  public void run()
                  {
                    if (ProfileNotificationsActivity.this.adapter != null)
                      ProfileNotificationsActivity.this.adapter.notifyItemChanged(ProfileNotificationsActivity.this.colorRow);
                  }
                }));
                return;
              }
              if (paramInt == ProfileNotificationsActivity.this.popupEnabledRow)
              {
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 1).commit();
                ((RadioCell)paramView).setChecked(true, true);
                paramView = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(2));
                if (paramView == null)
                  break;
                ((RadioCell)paramView).setChecked(false, true);
                return;
              }
              if (paramInt != ProfileNotificationsActivity.this.popupDisabledRow)
                break;
              ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 2).commit();
              ((RadioCell)paramView).setChecked(true, true);
              paramView = ProfileNotificationsActivity.this.listView.findViewWithTag(Integer.valueOf(1));
              if (paramView == null)
                break;
              ((RadioCell)paramView).setChecked(false, true);
              return;
            }
            label1554: paramView = null;
            continue;
            label1559: paramView = null;
          }
          label1564: localObject1 = null;
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
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { HeaderCell.class, TextSettingsCell.class, TextColorCell.class, RadioCell.class, TextCheckBoxCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextColorCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareUnchecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareDisabled"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareBackground"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckBoxCell.class }, null, null, null, "checkboxSquareCheck") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if ((paramInt2 != -1) || (paramIntent == null));
    Uri localUri;
    SharedPreferences.Editor localEditor;
    Ringtone localRingtone;
    while (true)
    {
      return;
      localUri = (Uri)paramIntent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
      localEditor = null;
      paramIntent = localEditor;
      if (localUri != null)
      {
        localRingtone = RingtoneManager.getRingtone(ApplicationLoader.applicationContext, localUri);
        paramIntent = localEditor;
        if (localRingtone != null)
        {
          if (paramInt1 != 13)
            break label228;
          if (!localUri.equals(Settings.System.DEFAULT_RINGTONE_URI))
            break;
          paramIntent = LocaleController.getString("DefaultRingtone", 2131165627);
          localRingtone.stop();
        }
      }
      localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
      if (paramInt1 != 12)
        break label336;
      if (paramIntent == null)
        break label265;
      localEditor.putString("sound_" + this.dialog_id, paramIntent);
      localEditor.putString("sound_path_" + this.dialog_id, localUri.toString());
      label178: localEditor.commit();
      if (this.adapter == null)
        continue;
      paramIntent = this.adapter;
      if (paramInt1 != 13)
        break label488;
    }
    label228: label488: for (paramInt1 = this.ringtoneRow; ; paramInt1 = this.soundRow)
    {
      paramIntent.notifyItemChanged(paramInt1);
      return;
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      if (localUri.equals(Settings.System.DEFAULT_NOTIFICATION_URI))
      {
        paramIntent = LocaleController.getString("SoundDefault", 2131166481);
        break;
      }
      paramIntent = localRingtone.getTitle(getParentActivity());
      break;
      localEditor.putString("sound_" + this.dialog_id, "NoSound");
      localEditor.putString("sound_path_" + this.dialog_id, "NoSound");
      break label178;
      if (paramInt1 != 13)
        break label178;
      if (paramIntent != null)
      {
        localEditor.putString("ringtone_" + this.dialog_id, paramIntent);
        localEditor.putString("ringtone_path_" + this.dialog_id, localUri.toString());
        break label178;
      }
      localEditor.putString("ringtone_" + this.dialog_id, "NoSound");
      localEditor.putString("ringtone_path_" + this.dialog_id, "NoSound");
      break label178;
    }
  }

  public boolean onFragmentCreate()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.customRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.customInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.generalRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.soundRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.vibrateRow = i;
    label140: Object localObject;
    if ((int)this.dialog_id < 0)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.smartRow = i;
      if (Build.VERSION.SDK_INT < 21)
        break label548;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.priorityRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.priorityInfoRow = i;
      int j = (int)this.dialog_id;
      if (j >= 0)
        break label561;
      localObject = MessagesController.getInstance().getChat(Integer.valueOf(-j));
      if ((localObject == null) || (!ChatObject.isChannel((TLRPC.Chat)localObject)) || (((TLRPC.Chat)localObject).megagroup))
        break label556;
      i = 1;
      label203: if ((j == 0) || (i != 0))
        break label566;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.popupRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.popupEnabledRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.popupDisabledRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.popupInfoRow = i;
      label279: if (j <= 0)
        break label589;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsVibrateRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.ringtoneRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.ringtoneInfoRow = i;
      label351: i = this.rowCount;
      this.rowCount = (i + 1);
      this.ledRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.colorRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.ledInfoRow = i;
      localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      this.customEnabled = ((SharedPreferences)localObject).getBoolean("custom_" + this.dialog_id, false);
      boolean bool = ((SharedPreferences)localObject).contains("notify2_" + this.dialog_id);
      i = ((SharedPreferences)localObject).getInt("notify2_" + this.dialog_id, 0);
      if (i != 0)
        break label656;
      if (!bool)
        break label612;
      this.notificationsEnabled = true;
    }
    while (true)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.notificationsSettingsUpdated);
      return super.onFragmentCreate();
      this.smartRow = -1;
      break;
      label548: this.priorityRow = -1;
      break label140;
      label556: i = 0;
      break label203;
      label561: i = 0;
      break label203;
      label566: this.popupRow = -1;
      this.popupEnabledRow = -1;
      this.popupDisabledRow = -1;
      this.popupInfoRow = -1;
      break label279;
      label589: this.callsRow = -1;
      this.callsVibrateRow = -1;
      this.ringtoneRow = -1;
      this.ringtoneInfoRow = -1;
      break label351;
      label612: if ((int)this.dialog_id < 0)
      {
        this.notificationsEnabled = ((SharedPreferences)localObject).getBoolean("EnableGroup", true);
        continue;
      }
      this.notificationsEnabled = ((SharedPreferences)localObject).getBoolean("EnableAll", true);
      continue;
      label656: if (i == 1)
      {
        this.notificationsEnabled = true;
        continue;
      }
      if (i == 2)
      {
        this.notificationsEnabled = false;
        continue;
      }
      this.notificationsEnabled = false;
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.notificationsSettingsUpdated);
  }

  private class ListAdapter extends RecyclerView.Adapter
  {
    private Context context;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public int getItemCount()
    {
      return ProfileNotificationsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == ProfileNotificationsActivity.this.generalRow) || (paramInt == ProfileNotificationsActivity.this.popupRow) || (paramInt == ProfileNotificationsActivity.this.ledRow) || (paramInt == ProfileNotificationsActivity.this.callsRow));
      do
      {
        return 0;
        if ((paramInt == ProfileNotificationsActivity.this.soundRow) || (paramInt == ProfileNotificationsActivity.this.vibrateRow) || (paramInt == ProfileNotificationsActivity.this.priorityRow) || (paramInt == ProfileNotificationsActivity.this.smartRow) || (paramInt == ProfileNotificationsActivity.this.ringtoneRow) || (paramInt == ProfileNotificationsActivity.this.callsVibrateRow))
          return 1;
        if ((paramInt == ProfileNotificationsActivity.this.popupInfoRow) || (paramInt == ProfileNotificationsActivity.this.ledInfoRow) || (paramInt == ProfileNotificationsActivity.this.priorityInfoRow) || (paramInt == ProfileNotificationsActivity.this.customInfoRow) || (paramInt == ProfileNotificationsActivity.this.ringtoneInfoRow))
          return 2;
        if (paramInt == ProfileNotificationsActivity.this.colorRow)
          return 3;
        if ((paramInt == ProfileNotificationsActivity.this.popupEnabledRow) || (paramInt == ProfileNotificationsActivity.this.popupDisabledRow))
          return 4;
      }
      while (paramInt != ProfileNotificationsActivity.this.customRow);
      return 5;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool3 = true;
      boolean bool4 = true;
      boolean bool5 = true;
      boolean bool1 = true;
      boolean bool6 = false;
      boolean bool7 = false;
      boolean bool8 = false;
      boolean bool9 = false;
      boolean bool2 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
        int i;
        label1726: label1733: label1738: 
        do
        {
          do
          {
            do
            {
              do
              {
                while (true)
                {
                  return;
                  paramViewHolder = (HeaderCell)paramViewHolder.itemView;
                  if (paramInt == ProfileNotificationsActivity.this.generalRow)
                  {
                    paramViewHolder.setText(LocaleController.getString("General", 2131165790));
                    return;
                  }
                  if (paramInt == ProfileNotificationsActivity.this.popupRow)
                  {
                    paramViewHolder.setText(LocaleController.getString("ProfilePopupNotification", 2131166310));
                    return;
                  }
                  if (paramInt == ProfileNotificationsActivity.this.ledRow)
                  {
                    paramViewHolder.setText(LocaleController.getString("NotificationsLed", 2131166134));
                    return;
                  }
                  if (paramInt != ProfileNotificationsActivity.this.callsRow)
                    continue;
                  paramViewHolder.setText(LocaleController.getString("VoipNotificationSettings", 2131166591));
                  return;
                  localObject2 = (TextSettingsCell)paramViewHolder.itemView;
                  paramViewHolder = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
                  if (paramInt == ProfileNotificationsActivity.this.soundRow)
                  {
                    localObject1 = paramViewHolder.getString("sound_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("SoundDefault", 2131166481));
                    paramViewHolder = (RecyclerView.ViewHolder)localObject1;
                    if (((String)localObject1).equals("NoSound"))
                      paramViewHolder = LocaleController.getString("NoSound", 2131166052);
                    ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Sound", 2131166480), paramViewHolder, true);
                    return;
                  }
                  if (paramInt == ProfileNotificationsActivity.this.ringtoneRow)
                  {
                    localObject1 = paramViewHolder.getString("ringtone_" + ProfileNotificationsActivity.this.dialog_id, LocaleController.getString("DefaultRingtone", 2131165627));
                    paramViewHolder = (RecyclerView.ViewHolder)localObject1;
                    if (((String)localObject1).equals("NoSound"))
                      paramViewHolder = LocaleController.getString("NoSound", 2131166052);
                    ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("VoipSettingsRingtone", 2131166606), paramViewHolder, true);
                    return;
                  }
                  if (paramInt == ProfileNotificationsActivity.this.vibrateRow)
                  {
                    paramInt = paramViewHolder.getInt("vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
                    if ((paramInt == 0) || (paramInt == 4))
                    {
                      paramViewHolder = LocaleController.getString("Vibrate", 2131166568);
                      localObject1 = LocaleController.getString("VibrationDefault", 2131166569);
                      if (ProfileNotificationsActivity.this.smartRow == -1)
                      {
                        bool1 = bool2;
                        if (ProfileNotificationsActivity.this.priorityRow == -1);
                      }
                      else
                      {
                        bool1 = true;
                      }
                      ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                      return;
                    }
                    if (paramInt == 1)
                    {
                      paramViewHolder = LocaleController.getString("Vibrate", 2131166568);
                      localObject1 = LocaleController.getString("Short", 2131166465);
                      if (ProfileNotificationsActivity.this.smartRow == -1)
                      {
                        bool1 = bool6;
                        if (ProfileNotificationsActivity.this.priorityRow == -1);
                      }
                      else
                      {
                        bool1 = true;
                      }
                      ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                      return;
                    }
                    if (paramInt == 2)
                    {
                      paramViewHolder = LocaleController.getString("Vibrate", 2131166568);
                      localObject1 = LocaleController.getString("VibrationDisabled", 2131166570);
                      if (ProfileNotificationsActivity.this.smartRow == -1)
                      {
                        bool1 = bool7;
                        if (ProfileNotificationsActivity.this.priorityRow == -1);
                      }
                      else
                      {
                        bool1 = true;
                      }
                      ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                      return;
                    }
                    if (paramInt != 3)
                      continue;
                    paramViewHolder = LocaleController.getString("Vibrate", 2131166568);
                    localObject1 = LocaleController.getString("Long", 2131165934);
                    if (ProfileNotificationsActivity.this.smartRow == -1)
                    {
                      bool1 = bool8;
                      if (ProfileNotificationsActivity.this.priorityRow == -1);
                    }
                    else
                    {
                      bool1 = true;
                    }
                    ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                    return;
                  }
                  if (paramInt != ProfileNotificationsActivity.this.priorityRow)
                    break;
                  paramInt = paramViewHolder.getInt("priority_" + ProfileNotificationsActivity.this.dialog_id, 3);
                  if (paramInt == 0)
                  {
                    ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityDefault", 2131166141), false);
                    return;
                  }
                  if (paramInt == 1)
                  {
                    ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityHigh", 2131166142), false);
                    return;
                  }
                  if (paramInt == 2)
                  {
                    ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPriorityMax", 2131166144), false);
                    return;
                  }
                  if (paramInt != 3)
                    continue;
                  ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("NotificationsPriority", 2131166140), LocaleController.getString("NotificationsPrioritySettings", 2131166145), false);
                  return;
                }
                if (paramInt != ProfileNotificationsActivity.this.smartRow)
                  continue;
                paramInt = paramViewHolder.getInt("smart_max_count_" + ProfileNotificationsActivity.this.dialog_id, 2);
                i = paramViewHolder.getInt("smart_delay_" + ProfileNotificationsActivity.this.dialog_id, 180);
                if (paramInt == 0)
                {
                  paramViewHolder = LocaleController.getString("SmartNotifications", 2131166470);
                  localObject1 = LocaleController.getString("SmartNotificationsDisabled", 2131166473);
                  if (ProfileNotificationsActivity.this.priorityRow != -1);
                  while (true)
                  {
                    ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                    return;
                    bool1 = false;
                  }
                }
                localObject1 = LocaleController.formatPluralString("Minutes", i / 60);
                paramViewHolder = LocaleController.getString("SmartNotifications", 2131166470);
                localObject1 = LocaleController.formatString("SmartNotificationsInfo", 2131166474, new Object[] { Integer.valueOf(paramInt), localObject1 });
                if (ProfileNotificationsActivity.this.priorityRow != -1);
                for (bool1 = bool3; ; bool1 = false)
                {
                  ((TextSettingsCell)localObject2).setTextAndValue(paramViewHolder, (String)localObject1, bool1);
                  return;
                }
              }
              while (paramInt != ProfileNotificationsActivity.this.callsVibrateRow);
              paramInt = paramViewHolder.getInt("calls_vibrate_" + ProfileNotificationsActivity.this.dialog_id, 0);
              if ((paramInt == 0) || (paramInt == 4))
              {
                ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("VibrationDefault", 2131166569), true);
                return;
              }
              if (paramInt == 1)
              {
                ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("Short", 2131166465), true);
                return;
              }
              if (paramInt != 2)
                continue;
              ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("VibrationDisabled", 2131166570), true);
              return;
            }
            while (paramInt != 3);
            ((TextSettingsCell)localObject2).setTextAndValue(LocaleController.getString("Vibrate", 2131166568), LocaleController.getString("Long", 2131165934), true);
            return;
            paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt == ProfileNotificationsActivity.this.popupInfoRow)
            {
              paramViewHolder.setText(LocaleController.getString("ProfilePopupNotificationInfo", 2131166311));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.context, 2130837725, "windowBackgroundGrayShadow"));
              return;
            }
            if (paramInt == ProfileNotificationsActivity.this.ledInfoRow)
            {
              paramViewHolder.setText(LocaleController.getString("NotificationsLedInfo", 2131166136));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.context, 2130837726, "windowBackgroundGrayShadow"));
              return;
            }
            if (paramInt == ProfileNotificationsActivity.this.priorityInfoRow)
            {
              if (ProfileNotificationsActivity.this.priorityRow == -1)
                paramViewHolder.setText("");
              while (true)
              {
                paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.context, 2130837725, "windowBackgroundGrayShadow"));
                return;
                paramViewHolder.setText(LocaleController.getString("PriorityInfo", 2131166303));
              }
            }
            if (paramInt != ProfileNotificationsActivity.this.customInfoRow)
              continue;
            paramViewHolder.setText(null);
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.context, 2130837725, "windowBackgroundGrayShadow"));
            return;
          }
          while (paramInt != ProfileNotificationsActivity.this.ringtoneInfoRow);
          paramViewHolder.setText(LocaleController.getString("VoipRingtoneInfo", 2131166605));
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.context, 2130837725, "windowBackgroundGrayShadow"));
          return;
          paramViewHolder = (TextColorCell)paramViewHolder.itemView;
          localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          if (((SharedPreferences)localObject1).contains("color_" + ProfileNotificationsActivity.this.dialog_id))
          {
            paramInt = ((SharedPreferences)localObject1).getInt("color_" + ProfileNotificationsActivity.this.dialog_id, -16776961);
            i = 0;
          }
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
              paramViewHolder.setTextAndColor(LocaleController.getString("NotificationsLedColor", 2131166135), j, false);
              return;
              if ((int)ProfileNotificationsActivity.this.dialog_id < 0)
              {
                paramInt = ((SharedPreferences)localObject1).getInt("GroupLed", -16776961);
                break;
              }
              paramInt = ((SharedPreferences)localObject1).getInt("MessagesLed", -16776961);
              break;
            }
            i += 1;
          }
          localObject1 = (RadioCell)paramViewHolder.itemView;
          Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          int j = ((SharedPreferences)localObject2).getInt("popup_" + ProfileNotificationsActivity.this.dialog_id, 0);
          i = j;
          if (j == 0)
          {
            if ((int)ProfileNotificationsActivity.this.dialog_id >= 0)
              break label1726;
            paramViewHolder = "popupGroup";
            if (((SharedPreferences)localObject2).getInt(paramViewHolder, 0) == 0)
              break label1733;
          }
          for (i = 1; ; i = 2)
          {
            if (paramInt != ProfileNotificationsActivity.this.popupEnabledRow)
              break label1738;
            paramViewHolder = LocaleController.getString("PopupEnabled", 2131166300);
            bool1 = bool9;
            if (i == 1)
              bool1 = true;
            ((RadioCell)localObject1).setText(paramViewHolder, bool1, true);
            ((RadioCell)localObject1).setTag(Integer.valueOf(1));
            return;
            paramViewHolder = "popupAll";
            break;
          }
        }
        while (paramInt != ProfileNotificationsActivity.this.popupDisabledRow);
        paramViewHolder = LocaleController.getString("PopupDisabled", 2131166299);
        if (i == 2);
        for (bool1 = bool4; ; bool1 = false)
        {
          ((RadioCell)localObject1).setText(paramViewHolder, bool1, false);
          ((RadioCell)localObject1).setTag(Integer.valueOf(2));
          return;
        }
      case 5:
      }
      paramViewHolder = (TextCheckBoxCell)paramViewHolder.itemView;
      ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      Object localObject1 = LocaleController.getString("NotificationsEnableCustom", 2131166133);
      if ((ProfileNotificationsActivity.this.customEnabled) && (ProfileNotificationsActivity.this.notificationsEnabled));
      for (bool1 = bool5; ; bool1 = false)
      {
        paramViewHolder.setTextAndCheck((String)localObject1, bool1, false);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextCheckBoxCell(this.context);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new HeaderCell(this.context);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextSettingsCell(this.context);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.context);
        continue;
        paramViewGroup = new TextColorCell(this.context);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new RadioCell(this.context);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }

    public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool2 = true;
      boolean bool3 = true;
      boolean bool4 = true;
      boolean bool1 = true;
      if (paramViewHolder.getItemViewType() != 0);
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 1:
        paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
        if ((ProfileNotificationsActivity.this.customEnabled) && (ProfileNotificationsActivity.this.notificationsEnabled));
        while (true)
        {
          paramViewHolder.setEnabled(bool1, null);
          return;
          bool1 = false;
        }
      case 2:
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if ((ProfileNotificationsActivity.this.customEnabled) && (ProfileNotificationsActivity.this.notificationsEnabled));
        for (bool1 = bool2; ; bool1 = false)
        {
          paramViewHolder.setEnabled(bool1, null);
          return;
        }
      case 3:
        paramViewHolder = (TextColorCell)paramViewHolder.itemView;
        if ((ProfileNotificationsActivity.this.customEnabled) && (ProfileNotificationsActivity.this.notificationsEnabled));
        for (bool1 = bool3; ; bool1 = false)
        {
          paramViewHolder.setEnabled(bool1, null);
          return;
        }
      case 4:
      }
      paramViewHolder = (RadioCell)paramViewHolder.itemView;
      if ((ProfileNotificationsActivity.this.customEnabled) && (ProfileNotificationsActivity.this.notificationsEnabled));
      for (bool1 = bool4; ; bool1 = false)
      {
        paramViewHolder.setEnabled(bool1, null);
        return;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ProfileNotificationsActivity
 * JD-Core Version:    0.6.0
 */