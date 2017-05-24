package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.PrivacyRule;
import org.vidogram.tgnet.TLRPC.TL_accountDaysTTL;
import org.vidogram.tgnet.TLRPC.TL_account_setAccountTTL;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_privacyValueAllowAll;
import org.vidogram.tgnet.TLRPC.TL_privacyValueAllowUsers;
import org.vidogram.tgnet.TLRPC.TL_privacyValueDisallowAll;
import org.vidogram.tgnet.TLRPC.TL_privacyValueDisallowUsers;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class PrivacySettingsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int blockedRow;
  private int callsRow;
  private int deleteAccountDetailRow;
  private int deleteAccountRow;
  private int deleteAccountSectionRow;
  private int groupsDetailRow;
  private int groupsRow;
  private int lastSeenRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int passcodeRow;
  private int passwordRow;
  private int privacySectionRow;
  private int rowCount;
  private int secretDetailRow;
  private int secretSectionRow;
  private int secretWebpageRow;
  private int securitySectionRow;
  private int sessionsDetailRow;
  private int sessionsRow;

  private String formatRulesString(int paramInt)
  {
    ArrayList localArrayList = ContactsController.getInstance().getPrivacyRules(paramInt);
    if (localArrayList.size() == 0)
      return LocaleController.getString("LastSeenNobody", 2131165896);
    int i = 0;
    int k = 0;
    int j = 0;
    paramInt = -1;
    if (i < localArrayList.size())
    {
      TLRPC.PrivacyRule localPrivacyRule = (TLRPC.PrivacyRule)localArrayList.get(i);
      if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowUsers))
        j += localPrivacyRule.users.size();
      while (true)
      {
        i += 1;
        break;
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowUsers))
        {
          k += localPrivacyRule.users.size();
          continue;
        }
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueAllowAll))
        {
          paramInt = 0;
          continue;
        }
        if ((localPrivacyRule instanceof TLRPC.TL_privacyValueDisallowAll))
        {
          paramInt = 1;
          continue;
        }
        paramInt = 2;
      }
    }
    if ((paramInt == 0) || ((paramInt == -1) && (k > 0)))
    {
      if (k == 0)
        return LocaleController.getString("LastSeenEverybody", 2131165881);
      return LocaleController.formatString("LastSeenEverybodyMinus", 2131165882, new Object[] { Integer.valueOf(k) });
    }
    if ((paramInt == 2) || ((paramInt == -1) && (k > 0) && (j > 0)))
    {
      if ((j == 0) && (k == 0))
        return LocaleController.getString("LastSeenContacts", 2131165876);
      if ((j != 0) && (k != 0))
        return LocaleController.formatString("LastSeenContactsMinusPlus", 2131165878, new Object[] { Integer.valueOf(k), Integer.valueOf(j) });
      if (k != 0)
        return LocaleController.formatString("LastSeenContactsMinus", 2131165877, new Object[] { Integer.valueOf(k) });
      return LocaleController.formatString("LastSeenContactsPlus", 2131165879, new Object[] { Integer.valueOf(j) });
    }
    if ((paramInt == 1) || (j > 0))
    {
      if (j == 0)
        return LocaleController.getString("LastSeenNobody", 2131165896);
      return LocaleController.formatString("LastSeenNobodyPlus", 2131165897, new Object[] { Integer.valueOf(j) });
    }
    return "unknown";
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("PrivacySettings", 2131166308));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          PrivacySettingsActivity.this.finishFragment();
      }
    });
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
        boolean bool = true;
        if (!paramView.isEnabled());
        while (true)
        {
          return;
          if (paramInt == PrivacySettingsActivity.this.blockedRow)
          {
            PrivacySettingsActivity.this.presentFragment(new BlockedUsersActivity());
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.sessionsRow)
          {
            PrivacySettingsActivity.this.presentFragment(new SessionsActivity());
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.deleteAccountRow)
          {
            if (PrivacySettingsActivity.this.getParentActivity() == null)
              continue;
            paramView = new AlertDialog.Builder(PrivacySettingsActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("DeleteAccountTitle", 2131165632));
            String str1 = LocaleController.formatPluralString("Months", 1);
            String str2 = LocaleController.formatPluralString("Months", 3);
            String str3 = LocaleController.formatPluralString("Months", 6);
            String str4 = LocaleController.formatPluralString("Years", 1);
            1 local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (paramInt == 0)
                  paramInt = 30;
                while (true)
                {
                  paramDialogInterface = new AlertDialog(PrivacySettingsActivity.this.getParentActivity(), 1);
                  paramDialogInterface.setMessage(LocaleController.getString("Loading", 2131165920));
                  paramDialogInterface.setCanceledOnTouchOutside(false);
                  paramDialogInterface.setCancelable(false);
                  paramDialogInterface.show();
                  TLRPC.TL_account_setAccountTTL localTL_account_setAccountTTL = new TLRPC.TL_account_setAccountTTL();
                  localTL_account_setAccountTTL.ttl = new TLRPC.TL_accountDaysTTL();
                  localTL_account_setAccountTTL.ttl.days = paramInt;
                  ConnectionsManager.getInstance().sendRequest(localTL_account_setAccountTTL, new RequestDelegate(paramDialogInterface, localTL_account_setAccountTTL)
                  {
                    public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                    {
                      AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
                      {
                        public void run()
                        {
                          try
                          {
                            PrivacySettingsActivity.2.1.1.this.val$progressDialog.dismiss();
                            if ((this.val$response instanceof TLRPC.TL_boolTrue))
                            {
                              ContactsController.getInstance().setDeleteAccountTTL(PrivacySettingsActivity.2.1.1.this.val$req.ttl.days);
                              PrivacySettingsActivity.this.listAdapter.notifyDataSetChanged();
                            }
                            return;
                          }
                          catch (Exception localException)
                          {
                            while (true)
                              FileLog.e(localException);
                          }
                        }
                      });
                    }
                  });
                  return;
                  if (paramInt == 1)
                  {
                    paramInt = 90;
                    continue;
                  }
                  if (paramInt == 2)
                  {
                    paramInt = 182;
                    continue;
                  }
                  if (paramInt == 3)
                  {
                    paramInt = 365;
                    continue;
                  }
                  paramInt = 0;
                }
              }
            };
            paramView.setItems(new CharSequence[] { str1, str2, str3, str4 }, local1);
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            PrivacySettingsActivity.this.showDialog(paramView.create());
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.lastSeenRow)
          {
            PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(0));
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.callsRow)
          {
            PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(2));
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.groupsRow)
          {
            PrivacySettingsActivity.this.presentFragment(new PrivacyControlActivity(1));
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.passwordRow)
          {
            PrivacySettingsActivity.this.presentFragment(new TwoStepVerificationActivity(0));
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.passcodeRow)
          {
            if (UserConfig.passcodeHash.length() > 0)
            {
              PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(2));
              return;
            }
            PrivacySettingsActivity.this.presentFragment(new PasscodeActivity(0));
            return;
          }
          if (paramInt != PrivacySettingsActivity.this.secretWebpageRow)
            continue;
          if (MessagesController.getInstance().secretWebpagePreview != 1)
            break;
          MessagesController.getInstance().secretWebpagePreview = 0;
          ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("secretWebpage2", MessagesController.getInstance().secretWebpagePreview).commit();
          if (!(paramView instanceof TextCheckCell))
            continue;
          paramView = (TextCheckCell)paramView;
          if (MessagesController.getInstance().secretWebpagePreview != 1)
            break label477;
        }
        while (true)
        {
          paramView.setChecked(bool);
          return;
          MessagesController.getInstance().secretWebpagePreview = 1;
          break;
          label477: bool = false;
        }
      }
    });
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.privacyRulesUpdated) && (this.listAdapter != null))
      this.listAdapter.notifyDataSetChanged();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class, TextCheckCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    ContactsController.getInstance().loadPrivacySettings();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacySectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.blockedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.lastSeenRow = i;
    if (MessagesController.getInstance().callsEnabled)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.groupsDetailRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.securitySectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.passcodeRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.passwordRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.sessionsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.sessionsDetailRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.deleteAccountSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.deleteAccountRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.deleteAccountDetailRow = i;
      if (MessagesController.getInstance().secretWebpagePreview == 1)
        break label344;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.secretSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.secretWebpageRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.secretDetailRow = i; ; this.secretDetailRow = -1)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.privacyRulesUpdated);
      return true;
      this.callsRow = -1;
      break;
      label344: this.secretSectionRow = -1;
      this.secretWebpageRow = -1;
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.privacyRulesUpdated);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
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
      return PrivacySettingsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == PrivacySettingsActivity.this.lastSeenRow) || (paramInt == PrivacySettingsActivity.this.blockedRow) || (paramInt == PrivacySettingsActivity.this.deleteAccountRow) || (paramInt == PrivacySettingsActivity.this.sessionsRow) || (paramInt == PrivacySettingsActivity.this.passwordRow) || (paramInt == PrivacySettingsActivity.this.passcodeRow) || (paramInt == PrivacySettingsActivity.this.groupsRow));
      do
      {
        return 0;
        if ((paramInt == PrivacySettingsActivity.this.deleteAccountDetailRow) || (paramInt == PrivacySettingsActivity.this.groupsDetailRow) || (paramInt == PrivacySettingsActivity.this.sessionsDetailRow) || (paramInt == PrivacySettingsActivity.this.secretDetailRow))
          return 1;
        if ((paramInt == PrivacySettingsActivity.this.securitySectionRow) || (paramInt == PrivacySettingsActivity.this.deleteAccountSectionRow) || (paramInt == PrivacySettingsActivity.this.privacySectionRow) || (paramInt == PrivacySettingsActivity.this.secretSectionRow))
          return 2;
      }
      while (paramInt != PrivacySettingsActivity.this.secretWebpageRow);
      return 3;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == PrivacySettingsActivity.this.passcodeRow) || (i == PrivacySettingsActivity.this.passwordRow) || (i == PrivacySettingsActivity.this.blockedRow) || (i == PrivacySettingsActivity.this.sessionsRow) || (i == PrivacySettingsActivity.this.secretWebpageRow) || ((i == PrivacySettingsActivity.this.groupsRow) && (!ContactsController.getInstance().getLoadingGroupInfo())) || ((i == PrivacySettingsActivity.this.lastSeenRow) && (!ContactsController.getInstance().getLoadingLastSeenInfo())) || ((i == PrivacySettingsActivity.this.callsRow) && (!ContactsController.getInstance().getLoadingCallsInfo())) || ((i == PrivacySettingsActivity.this.deleteAccountRow) && (!ContactsController.getInstance().getLoadingDeleteInfo()));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int i = 2130837726;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      }
      do
      {
        do
        {
          do
          {
            do
            {
              return;
              localObject = (TextSettingsCell)paramViewHolder.itemView;
              if (paramInt == PrivacySettingsActivity.this.blockedRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("BlockedUsers", 2131165384), true);
                return;
              }
              if (paramInt == PrivacySettingsActivity.this.sessionsRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("SessionsTitle", 2131166436), false);
                return;
              }
              if (paramInt == PrivacySettingsActivity.this.passwordRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("TwoStepVerification", 2131166527), true);
                return;
              }
              if (paramInt == PrivacySettingsActivity.this.passcodeRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("Passcode", 2131166193), true);
                return;
              }
              if (paramInt == PrivacySettingsActivity.this.lastSeenRow)
              {
                if (ContactsController.getInstance().getLoadingLastSeenInfo());
                for (paramViewHolder = LocaleController.getString("Loading", 2131165920); ; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(0))
                {
                  ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("PrivacyLastSeen", 2131166305), paramViewHolder, true);
                  return;
                }
              }
              if (paramInt == PrivacySettingsActivity.this.callsRow)
              {
                if (ContactsController.getInstance().getLoadingCallsInfo());
                for (paramViewHolder = LocaleController.getString("Loading", 2131165920); ; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(2))
                {
                  ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Calls", 2131165424), paramViewHolder, true);
                  return;
                }
              }
              if (paramInt != PrivacySettingsActivity.this.groupsRow)
                continue;
              if (ContactsController.getInstance().getLoadingGroupInfo());
              for (paramViewHolder = LocaleController.getString("Loading", 2131165920); ; paramViewHolder = PrivacySettingsActivity.this.formatRulesString(1))
              {
                ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("GroupsAndChannels", 2131165804), paramViewHolder, false);
                return;
              }
            }
            while (paramInt != PrivacySettingsActivity.this.deleteAccountRow);
            if (ContactsController.getInstance().getLoadingDeleteInfo())
              paramViewHolder = LocaleController.getString("Loading", 2131165920);
            while (true)
            {
              ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("DeleteAccountIfAwayFor", 2131165630), paramViewHolder, false);
              return;
              paramInt = ContactsController.getInstance().getDeleteAccountTTL();
              if (paramInt <= 182)
              {
                paramViewHolder = LocaleController.formatPluralString("Months", paramInt / 30);
                continue;
              }
              if (paramInt == 365)
              {
                paramViewHolder = LocaleController.formatPluralString("Years", paramInt / 365);
                continue;
              }
              paramViewHolder = LocaleController.formatPluralString("Days", paramInt);
            }
            paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
            if (paramInt == PrivacySettingsActivity.this.deleteAccountDetailRow)
            {
              paramViewHolder.setText(LocaleController.getString("DeleteAccountHelp", 2131165629));
              localObject = this.mContext;
              if (PrivacySettingsActivity.this.secretSectionRow == -1);
              for (paramInt = i; ; paramInt = 2130837725)
              {
                paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable((Context)localObject, paramInt, "windowBackgroundGrayShadow"));
                return;
              }
            }
            if (paramInt == PrivacySettingsActivity.this.groupsDetailRow)
            {
              paramViewHolder.setText(LocaleController.getString("GroupsAndChannelsHelp", 2131165805));
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
              return;
            }
            if (paramInt != PrivacySettingsActivity.this.sessionsDetailRow)
              continue;
            paramViewHolder.setText(LocaleController.getString("SessionsInfo", 2131166435));
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
            return;
          }
          while (paramInt != PrivacySettingsActivity.this.secretDetailRow);
          paramViewHolder.setText("");
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
          paramViewHolder = (HeaderCell)paramViewHolder.itemView;
          if (paramInt == PrivacySettingsActivity.this.privacySectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("PrivacyTitle", 2131166309));
            return;
          }
          if (paramInt == PrivacySettingsActivity.this.securitySectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("SecurityTitle", 2131166404));
            return;
          }
          if (paramInt != PrivacySettingsActivity.this.deleteAccountSectionRow)
            continue;
          paramViewHolder.setText(LocaleController.getString("DeleteAccountTitle", 2131165632));
          return;
        }
        while (paramInt != PrivacySettingsActivity.this.secretSectionRow);
        paramViewHolder.setText(LocaleController.getString("SecretChat", 2131166400));
        return;
        paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
      }
      while (paramInt != PrivacySettingsActivity.this.secretWebpageRow);
      Object localObject = LocaleController.getString("SecretWebPage", 2131166403);
      if (MessagesController.getInstance().secretWebpagePreview == 1);
      for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.setTextAndCheck((String)localObject, bool, true);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextCheckCell(this.mContext);
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
 * Qualified Name:     org.vidogram.ui.PrivacySettingsActivity
 * JD-Core Version:    0.6.0
 */