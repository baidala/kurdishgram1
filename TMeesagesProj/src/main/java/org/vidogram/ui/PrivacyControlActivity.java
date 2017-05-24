package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.PrivacyRule;
import org.vidogram.tgnet.TLRPC.TL_account_privacyRules;
import org.vidogram.tgnet.TLRPC.TL_account_setPrivacy;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyChatInvite;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyKeyStatusTimestamp;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyValueAllowAll;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyValueAllowContacts;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyValueAllowUsers;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyValueDisallowAll;
import org.vidogram.tgnet.TLRPC.TL_inputPrivacyValueDisallowUsers;
import org.vidogram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.vidogram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.vidogram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.vidogram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.RadioCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacyControlActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private int alwaysShareRow;
  private ArrayList<Integer> currentMinus;
  private ArrayList<Integer> currentPlus;
  private int currentType;
  private int detailRow;
  private View doneButton;
  private boolean enableAnimation;
  private int everybodyRow;
  private int lastCheckedType = -1;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int myContactsRow;
  private int neverShareRow;
  private int nobodyRow;
  private int rowCount;
  private int rulesType;
  private int sectionRow;
  private int shareDetailRow;
  private int shareSectionRow;

  public PrivacyControlActivity(int paramInt)
  {
    this.rulesType = paramInt;
  }

  private void applyCurrentPrivacySettings()
  {
    TLRPC.TL_account_setPrivacy localTL_account_setPrivacy = new TLRPC.TL_account_setPrivacy();
    if (this.rulesType == 2)
      localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyPhoneCall();
    Object localObject1;
    int i;
    Object localObject2;
    while ((this.currentType != 0) && (this.currentPlus.size() > 0))
    {
      localObject1 = new TLRPC.TL_inputPrivacyValueAllowUsers();
      i = 0;
      while (true)
        if (i < this.currentPlus.size())
        {
          localObject2 = MessagesController.getInstance().getUser((Integer)this.currentPlus.get(i));
          if (localObject2 != null)
          {
            localObject2 = MessagesController.getInputUser((TLRPC.User)localObject2);
            if (localObject2 != null)
              ((TLRPC.TL_inputPrivacyValueAllowUsers)localObject1).users.add(localObject2);
          }
          i += 1;
          continue;
          if (this.rulesType == 1)
          {
            localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyChatInvite();
            break;
          }
          localTL_account_setPrivacy.key = new TLRPC.TL_inputPrivacyKeyStatusTimestamp();
          break;
        }
      localTL_account_setPrivacy.rules.add(localObject1);
    }
    if ((this.currentType != 1) && (this.currentMinus.size() > 0))
    {
      localObject1 = new TLRPC.TL_inputPrivacyValueDisallowUsers();
      i = 0;
      while (i < this.currentMinus.size())
      {
        localObject2 = MessagesController.getInstance().getUser((Integer)this.currentMinus.get(i));
        if (localObject2 != null)
        {
          localObject2 = MessagesController.getInputUser((TLRPC.User)localObject2);
          if (localObject2 != null)
            ((TLRPC.TL_inputPrivacyValueDisallowUsers)localObject1).users.add(localObject2);
        }
        i += 1;
      }
      localTL_account_setPrivacy.rules.add(localObject1);
    }
    if (this.currentType == 0)
      localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowAll());
    while (true)
    {
      localObject1 = null;
      if (getParentActivity() != null)
      {
        localObject1 = new AlertDialog(getParentActivity(), 1);
        ((AlertDialog)localObject1).setMessage(LocaleController.getString("Loading", 2131165920));
        ((AlertDialog)localObject1).setCanceledOnTouchOutside(false);
        ((AlertDialog)localObject1).setCancelable(false);
        ((AlertDialog)localObject1).show();
      }
      ConnectionsManager.getInstance().sendRequest(localTL_account_setPrivacy, new RequestDelegate((AlertDialog)localObject1)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              try
              {
                if (PrivacyControlActivity.3.this.val$progressDialogFinal != null)
                  PrivacyControlActivity.3.this.val$progressDialogFinal.dismiss();
                if (this.val$error == null)
                {
                  PrivacyControlActivity.this.finishFragment();
                  TLRPC.TL_account_privacyRules localTL_account_privacyRules = (TLRPC.TL_account_privacyRules)this.val$response;
                  MessagesController.getInstance().putUsers(localTL_account_privacyRules.users, false);
                  ContactsController.getInstance().setPrivacyRules(localTL_account_privacyRules.rules, PrivacyControlActivity.this.rulesType);
                  return;
                }
              }
              catch (Exception localException)
              {
                while (true)
                  FileLog.e(localException);
                PrivacyControlActivity.this.showErrorAlert();
              }
            }
          });
        }
      }
      , 2);
      return;
      if (this.currentType == 1)
      {
        localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueDisallowAll());
        continue;
      }
      if (this.currentType != 2)
        continue;
      localTL_account_setPrivacy.rules.add(new TLRPC.TL_inputPrivacyValueAllowContacts());
    }
  }

  private void checkPrivacy()
  {
    this.currentPlus = new ArrayList();
    this.currentMinus = new ArrayList();
    ArrayList localArrayList = ContactsController.getInstance().getPrivacyRules(this.rulesType);
    if ((localArrayList == null) || (localArrayList.size() == 0))
    {
      this.currentType = 1;
      return;
    }
    int j = 0;
    int i = -1;
    if (j < localArrayList.size())
    {
      TLRPC.PrivacyRule localPrivacyRule = (TLRPC.PrivacyRule)localArrayList.get(j);
      if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowUsers))
        this.currentPlus.addAll(localPrivacyRule.users);
      while (true)
      {
        j += 1;
        break;
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowUsers))
        {
          this.currentMinus.addAll(localPrivacyRule.users);
          continue;
        }
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowAll))
        {
          i = 0;
          continue;
        }
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowAll))
        {
          i = 1;
          continue;
        }
        i = 2;
      }
    }
    if ((i == 0) || ((i == -1) && (this.currentMinus.size() > 0)))
      this.currentType = 0;
    while (true)
    {
      if (this.doneButton != null)
        this.doneButton.setVisibility(8);
      updateRows();
      return;
      if ((i == 2) || ((i == -1) && (this.currentMinus.size() > 0) && (this.currentPlus.size() > 0)))
      {
        this.currentType = 2;
        continue;
      }
      if ((i != 1) && ((i != -1) || (this.currentPlus.size() <= 0)))
        continue;
      this.currentType = 1;
    }
  }

  private void showErrorAlert()
  {
    if (getParentActivity() == null)
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    localBuilder.setMessage(LocaleController.getString("PrivacyFloodControlError", 2131166304));
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    showDialog(localBuilder.create());
  }

  private void updateRows()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.sectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.everybodyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.myContactsRow = i;
    if ((this.rulesType != 0) && (this.rulesType != 2))
    {
      this.nobodyRow = -1;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.detailRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.shareSectionRow = i;
      if ((this.currentType != 1) && (this.currentType != 2))
        break label227;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.alwaysShareRow = i;
      label143: if ((this.currentType != 0) && (this.currentType != 2))
        break label235;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    label227: label235: for (this.neverShareRow = i; ; this.neverShareRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.shareDetailRow = i;
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.nobodyRow = i;
      break;
      this.alwaysShareRow = -1;
      break label143;
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.rulesType == 2)
      this.actionBar.setTitle(LocaleController.getString("Calls", 2131165424));
    while (true)
    {
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            PrivacyControlActivity.this.finishFragment();
          do
            return;
          while ((paramInt != 1) || (PrivacyControlActivity.this.getParentActivity() == null));
          if ((PrivacyControlActivity.this.currentType != 0) && (PrivacyControlActivity.this.rulesType == 0))
          {
            SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if (!localSharedPreferences.getBoolean("privacyAlertShowed", false))
            {
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(PrivacyControlActivity.this.getParentActivity());
              if (PrivacyControlActivity.this.rulesType == 1)
                localBuilder.setMessage(LocaleController.getString("WhoCanAddMeInfo", 2131166621));
              while (true)
              {
                localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(localSharedPreferences)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    PrivacyControlActivity.this.applyCurrentPrivacySettings();
                    this.val$preferences.edit().putBoolean("privacyAlertShowed", true).commit();
                  }
                });
                localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                PrivacyControlActivity.this.showDialog(localBuilder.create());
                return;
                localBuilder.setMessage(LocaleController.getString("CustomHelp", 2131165605));
              }
            }
          }
          PrivacyControlActivity.this.applyCurrentPrivacySettings();
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      this.doneButton.setVisibility(8);
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setVerticalScrollBarEnabled(false);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          boolean bool2 = true;
          boolean bool1 = true;
          int i;
          if ((paramInt == PrivacyControlActivity.this.nobodyRow) || (paramInt == PrivacyControlActivity.this.everybodyRow) || (paramInt == PrivacyControlActivity.this.myContactsRow))
          {
            i = PrivacyControlActivity.this.currentType;
            if (paramInt == PrivacyControlActivity.this.nobodyRow)
            {
              i = 1;
              if (i != PrivacyControlActivity.this.currentType)
                break label104;
            }
          }
          label104: 
          do
          {
            return;
            if (paramInt == PrivacyControlActivity.this.everybodyRow)
            {
              i = 0;
              break;
            }
            if (paramInt != PrivacyControlActivity.this.myContactsRow)
              break;
            i = 2;
            break;
            PrivacyControlActivity.access$602(PrivacyControlActivity.this, true);
            PrivacyControlActivity.this.doneButton.setVisibility(0);
            PrivacyControlActivity.access$802(PrivacyControlActivity.this, PrivacyControlActivity.this.currentType);
            PrivacyControlActivity.access$002(PrivacyControlActivity.this, i);
            PrivacyControlActivity.this.updateRows();
            return;
          }
          while ((paramInt != PrivacyControlActivity.this.neverShareRow) && (paramInt != PrivacyControlActivity.this.alwaysShareRow));
          Bundle localBundle;
          if (paramInt == PrivacyControlActivity.this.neverShareRow)
          {
            paramView = PrivacyControlActivity.this.currentMinus;
            if (!paramView.isEmpty())
              break label309;
            localBundle = new Bundle();
            if (paramInt != PrivacyControlActivity.this.neverShareRow)
              break label297;
            paramView = "isNeverShare";
            label227: localBundle.putBoolean(paramView, true);
            if (PrivacyControlActivity.this.rulesType == 0)
              break label303;
          }
          while (true)
          {
            localBundle.putBoolean("isGroup", bool1);
            paramView = new GroupCreateActivity(localBundle);
            paramView.setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate(paramInt)
            {
              public void didSelectUsers(ArrayList<Integer> paramArrayList)
              {
                if (this.val$position == PrivacyControlActivity.this.neverShareRow)
                {
                  PrivacyControlActivity.access$1202(PrivacyControlActivity.this, paramArrayList);
                  i = 0;
                  while (i < PrivacyControlActivity.this.currentMinus.size())
                  {
                    PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                    i += 1;
                  }
                }
                PrivacyControlActivity.access$1302(PrivacyControlActivity.this, paramArrayList);
                int i = 0;
                while (i < PrivacyControlActivity.this.currentPlus.size())
                {
                  PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                  i += 1;
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.access$802(PrivacyControlActivity.this, -1);
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
              }
            });
            PrivacyControlActivity.this.presentFragment(paramView);
            return;
            paramView = PrivacyControlActivity.this.currentPlus;
            break;
            label297: paramView = "isAlwaysShare";
            break label227;
            label303: bool1 = false;
          }
          label309: if (PrivacyControlActivity.this.rulesType != 0)
          {
            bool1 = true;
            if (paramInt != PrivacyControlActivity.this.alwaysShareRow)
              break label375;
          }
          while (true)
          {
            paramView = new PrivacyUsersActivity(paramView, bool1, bool2);
            paramView.setDelegate(new PrivacyUsersActivity.PrivacyActivityDelegate(paramInt)
            {
              public void didUpdatedUserList(ArrayList<Integer> paramArrayList, boolean paramBoolean)
              {
                int i;
                if (this.val$position == PrivacyControlActivity.this.neverShareRow)
                {
                  PrivacyControlActivity.access$1202(PrivacyControlActivity.this, paramArrayList);
                  if (paramBoolean)
                  {
                    i = 0;
                    while (i < PrivacyControlActivity.this.currentMinus.size())
                    {
                      PrivacyControlActivity.this.currentPlus.remove(PrivacyControlActivity.this.currentMinus.get(i));
                      i += 1;
                    }
                  }
                }
                else
                {
                  PrivacyControlActivity.access$1302(PrivacyControlActivity.this, paramArrayList);
                  if (paramBoolean)
                  {
                    i = 0;
                    while (i < PrivacyControlActivity.this.currentPlus.size())
                    {
                      PrivacyControlActivity.this.currentMinus.remove(PrivacyControlActivity.this.currentPlus.get(i));
                      i += 1;
                    }
                  }
                }
                PrivacyControlActivity.this.doneButton.setVisibility(0);
                PrivacyControlActivity.this.listAdapter.notifyDataSetChanged();
              }
            });
            PrivacyControlActivity.this.presentFragment(paramView);
            return;
            bool1 = false;
            break;
            label375: bool2 = false;
          }
        }
      });
      return this.fragmentView;
      if (this.rulesType == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("GroupsAndChannels", 2131165804));
        continue;
      }
      this.actionBar.setTitle(LocaleController.getString("PrivacyLastSeen", 2131166305));
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.privacyRulesUpdated)
      checkPrivacy();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, RadioCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    checkPrivacy();
    updateRows();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.privacyRulesUpdated);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.privacyRulesUpdated);
  }

  public void onResume()
  {
    super.onResume();
    this.lastCheckedType = -1;
    this.enableAnimation = false;
  }

  private static class LinkMovementMethodMy extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        FileLog.e(paramTextView);
      }
      return false;
    }
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
      return PrivacyControlActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == PrivacyControlActivity.this.alwaysShareRow) || (paramInt == PrivacyControlActivity.this.neverShareRow));
      do
      {
        return 0;
        if ((paramInt == PrivacyControlActivity.this.shareDetailRow) || (paramInt == PrivacyControlActivity.this.detailRow))
          return 1;
        if ((paramInt == PrivacyControlActivity.this.sectionRow) || (paramInt == PrivacyControlActivity.this.shareSectionRow))
          return 2;
      }
      while ((paramInt != PrivacyControlActivity.this.everybodyRow) && (paramInt != PrivacyControlActivity.this.myContactsRow) && (paramInt != PrivacyControlActivity.this.nobodyRow));
      return 3;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == PrivacyControlActivity.this.nobodyRow) || (i == PrivacyControlActivity.this.everybodyRow) || (i == PrivacyControlActivity.this.myContactsRow) || (i == PrivacyControlActivity.this.neverShareRow) || (i == PrivacyControlActivity.this.alwaysShareRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      Object localObject;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
        label141: label146: 
        do
        {
          do
          {
            do
            {
              return;
              localObject = (TextSettingsCell)paramViewHolder.itemView;
              if (paramInt != PrivacyControlActivity.this.alwaysShareRow)
                continue;
              if (PrivacyControlActivity.this.currentPlus.size() != 0)
              {
                paramViewHolder = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentPlus.size());
                if (PrivacyControlActivity.this.rulesType == 0)
                  break label146;
                str = LocaleController.getString("AlwaysAllow", 2131165301);
                if (PrivacyControlActivity.this.neverShareRow == -1)
                  break label141;
              }
              while (true)
              {
                ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, bool1);
                return;
                paramViewHolder = LocaleController.getString("EmpryUsersPlaceholder", 2131165674);
                break;
                bool1 = false;
              }
              String str = LocaleController.getString("AlwaysShareWith", 2131165303);
              if (PrivacyControlActivity.this.neverShareRow != -1);
              for (bool1 = bool2; ; bool1 = false)
              {
                ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, bool1);
                return;
              }
            }
            while (paramInt != PrivacyControlActivity.this.neverShareRow);
            if (PrivacyControlActivity.this.currentMinus.size() != 0);
            for (paramViewHolder = LocaleController.formatPluralString("Users", PrivacyControlActivity.this.currentMinus.size()); PrivacyControlActivity.this.rulesType != 0; paramViewHolder = LocaleController.getString("EmpryUsersPlaceholder", 2131165674))
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("NeverAllow", 2131166001), paramViewHolder, false);
              return;
            }
            ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("NeverShareWith", 2131166003), paramViewHolder, false);
            return;
            paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt != PrivacyControlActivity.this.detailRow)
              continue;
            if (PrivacyControlActivity.this.rulesType == 2)
              paramViewHolder.setText(LocaleController.getString("WhoCanCallMeInfo", 2131166626));
            while (true)
            {
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
              return;
              if (PrivacyControlActivity.this.rulesType == 1)
              {
                paramViewHolder.setText(LocaleController.getString("WhoCanAddMeInfo", 2131166621));
                continue;
              }
              paramViewHolder.setText(LocaleController.getString("CustomHelp", 2131165605));
            }
          }
          while (paramInt != PrivacyControlActivity.this.shareDetailRow);
          if (PrivacyControlActivity.this.rulesType == 2)
            paramViewHolder.setText(LocaleController.getString("CustomCallInfo", 2131165604));
          while (true)
          {
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
            return;
            if (PrivacyControlActivity.this.rulesType == 1)
            {
              paramViewHolder.setText(LocaleController.getString("CustomShareInfo", 2131165607));
              continue;
            }
            paramViewHolder.setText(LocaleController.getString("CustomShareSettingsHelp", 2131165608));
          }
          paramViewHolder = (HeaderCell)paramViewHolder.itemView;
          if (paramInt != PrivacyControlActivity.this.sectionRow)
            continue;
          if (PrivacyControlActivity.this.rulesType == 2)
          {
            paramViewHolder.setText(LocaleController.getString("WhoCanCallMe", 2131166625));
            return;
          }
          if (PrivacyControlActivity.this.rulesType == 1)
          {
            paramViewHolder.setText(LocaleController.getString("WhoCanAddMe", 2131166620));
            return;
          }
          paramViewHolder.setText(LocaleController.getString("LastSeenTitle", 2131165899));
          return;
        }
        while (paramInt != PrivacyControlActivity.this.shareSectionRow);
        paramViewHolder.setText(LocaleController.getString("AddExceptions", 2131165278));
        return;
      case 3:
      }
      paramViewHolder = (RadioCell)paramViewHolder.itemView;
      if (paramInt == PrivacyControlActivity.this.everybodyRow)
      {
        localObject = LocaleController.getString("LastSeenEverybody", 2131165881);
        if (PrivacyControlActivity.this.lastCheckedType == 0)
        {
          bool1 = true;
          label599: paramViewHolder.setText((String)localObject, bool1, true);
          paramInt = 0;
        }
      }
      while (true)
      {
        if (PrivacyControlActivity.this.lastCheckedType == paramInt)
        {
          paramViewHolder.setChecked(false, PrivacyControlActivity.this.enableAnimation);
          return;
          bool1 = false;
          break label599;
          if (paramInt == PrivacyControlActivity.this.myContactsRow)
          {
            localObject = LocaleController.getString("LastSeenContacts", 2131165876);
            if (PrivacyControlActivity.this.lastCheckedType == 2)
            {
              bool1 = true;
              label671: if (PrivacyControlActivity.this.nobodyRow == -1)
                break label704;
            }
            label704: for (bool2 = true; ; bool2 = false)
            {
              paramViewHolder.setText((String)localObject, bool1, bool2);
              paramInt = 2;
              break;
              bool1 = false;
              break label671;
            }
          }
          if (paramInt == PrivacyControlActivity.this.nobodyRow)
          {
            localObject = LocaleController.getString("LastSeenNobody", 2131165896);
            if (PrivacyControlActivity.this.lastCheckedType == 1);
            for (bool1 = true; ; bool1 = false)
            {
              paramViewHolder.setText((String)localObject, bool1, false);
              paramInt = 1;
              break;
            }
          }
        }
        else
        {
          if (PrivacyControlActivity.this.currentType != paramInt)
            break;
          paramViewHolder.setChecked(true, PrivacyControlActivity.this.enableAnimation);
          return;
        }
        paramInt = 0;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new RadioCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      case 2:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PrivacyControlActivity
 * JD-Core Version:    0.6.0
 */