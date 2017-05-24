package org.vidogram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.ContactsController.Contact;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.SecretChatHelper;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Adapters.ContactsAdapter;
import org.vidogram.ui.Adapters.SearchAdapter;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.LetterSectionCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Cells.TextCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;

public class ContactsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int add_button = 1;
  private static final int search_button = 0;
  private boolean addingToChannel;
  private boolean allowBots = true;
  private boolean allowUsernameSearch = true;
  private int chat_id;
  private boolean checkPermission = true;
  private boolean createSecretChat;
  private boolean creatingChat;
  private ContactsActivityDelegate delegate;
  private boolean destroyAfterSelect;
  private EmptyTextProgressView emptyView;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private RecyclerListView listView;
  private ContactsAdapter listViewAdapter;
  private boolean needForwardCount = true;
  private boolean needPhonebook;
  private boolean onlyUsers;
  private AlertDialog permissionDialog;
  private boolean returnAsResult;
  private SearchAdapter searchListViewAdapter;
  private boolean searchWas;
  private boolean searching;
  private String selectAlertString = null;

  public ContactsActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  @TargetApi(23)
  private void askForPermissons()
  {
    Activity localActivity = getParentActivity();
    if (localActivity == null)
      return;
    ArrayList localArrayList = new ArrayList();
    if (localActivity.checkSelfPermission("android.permission.READ_CONTACTS") != 0)
    {
      localArrayList.add("android.permission.READ_CONTACTS");
      localArrayList.add("android.permission.WRITE_CONTACTS");
      localArrayList.add("android.permission.GET_ACCOUNTS");
    }
    localActivity.requestPermissions((String[])localArrayList.toArray(new String[localArrayList.size()]), 1);
  }

  private void didSelectResult(TLRPC.User paramUser, boolean paramBoolean, String paramString)
  {
    AlertDialog.Builder localBuilder;
    EditText localEditText;
    if ((paramBoolean) && (this.selectAlertString != null))
    {
      if (getParentActivity() == null)
        return;
      if ((paramUser.bot) && (paramUser.bot_nochats) && (!this.addingToChannel))
        try
        {
          Toast.makeText(getParentActivity(), LocaleController.getString("BotCantJoinGroups", 2131165391), 0).show();
          return;
        }
        catch (java.lang.Exception paramUser)
        {
          FileLog.e(paramUser);
          return;
        }
      localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      paramString = LocaleController.formatStringSimple(this.selectAlertString, new Object[] { UserObject.getUserName(paramUser) });
      if ((paramUser.bot) || (!this.needForwardCount))
        break label394;
      paramString = String.format("%s\n\n%s", new Object[] { paramString, LocaleController.getString("AddToTheGroupForwardCount", 2131165292) });
      localEditText = new EditText(getParentActivity());
      localEditText.setTextSize(18.0F);
      localEditText.setText("50");
      localEditText.setGravity(17);
      localEditText.setInputType(2);
      localEditText.setImeOptions(6);
      localEditText.setBackgroundDrawable(Theme.createEditTextDrawable(getParentActivity(), true));
      localEditText.addTextChangedListener(new TextWatcher(localEditText)
      {
        public void afterTextChanged(Editable paramEditable)
        {
          int i;
          try
          {
            paramEditable = paramEditable.toString();
            if (paramEditable.length() == 0)
              return;
            i = Utilities.parseInt(paramEditable).intValue();
            if (i < 0)
            {
              this.val$editTextFinal.setText("0");
              this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
              return;
            }
            if (i > 300)
            {
              this.val$editTextFinal.setText("300");
              this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
              return;
            }
          }
          catch (java.lang.Exception paramEditable)
          {
            FileLog.e(paramEditable);
            return;
          }
          if (!paramEditable.equals("" + i))
          {
            this.val$editTextFinal.setText("" + i);
            this.val$editTextFinal.setSelection(this.val$editTextFinal.length());
          }
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }
      });
      localBuilder.setView(localEditText);
    }
    while (true)
    {
      localBuilder.setMessage(paramString);
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramUser, localEditText)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          ContactsActivity localContactsActivity = ContactsActivity.this;
          TLRPC.User localUser = this.val$user;
          if (this.val$finalEditText != null);
          for (paramDialogInterface = this.val$finalEditText.getText().toString(); ; paramDialogInterface = "0")
          {
            localContactsActivity.didSelectResult(localUser, false, paramDialogInterface);
            return;
          }
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      showDialog(localBuilder.create());
      if (localEditText == null)
        break;
      paramUser = (ViewGroup.MarginLayoutParams)localEditText.getLayoutParams();
      if (paramUser != null)
      {
        if ((paramUser instanceof FrameLayout.LayoutParams))
          ((FrameLayout.LayoutParams)paramUser).gravity = 1;
        int i = AndroidUtilities.dp(10.0F);
        paramUser.leftMargin = i;
        paramUser.rightMargin = i;
        localEditText.setLayoutParams(paramUser);
      }
      localEditText.setSelection(localEditText.getText().length());
      return;
      if (this.delegate != null)
      {
        this.delegate.didSelectContact(paramUser, paramString);
        this.delegate = null;
      }
      finishFragment();
      return;
      label394: localEditText = null;
    }
  }

  private void updateVisibleRows(int paramInt)
  {
    if (this.listView != null)
    {
      int j = this.listView.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.listView.getChildAt(i);
        if ((localView instanceof UserCell))
          ((UserCell)localView).update(paramInt);
        i += 1;
      }
    }
  }

  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject;
    int i;
    label180: boolean bool2;
    if (this.destroyAfterSelect)
      if (this.returnAsResult)
      {
        this.actionBar.setTitle(LocaleController.getString("SelectContact", 2131166407));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramInt)
          {
            if (paramInt == -1)
              ContactsActivity.this.finishFragment();
            do
              return;
            while (paramInt != 1);
            ContactsActivity.this.presentFragment(new NewContactActivity(null));
          }
        });
        localObject = this.actionBar.createMenu();
        ((ActionBarMenu)localObject).addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public void onSearchCollapse()
          {
            ContactsActivity.this.searchListViewAdapter.searchDialogs(null);
            ContactsActivity.access$002(ContactsActivity.this, false);
            ContactsActivity.access$202(ContactsActivity.this, false);
            ContactsActivity.this.listView.setAdapter(ContactsActivity.this.listViewAdapter);
            ContactsActivity.this.listViewAdapter.notifyDataSetChanged();
            ContactsActivity.this.listView.setFastScrollVisible(true);
            ContactsActivity.this.listView.setVerticalScrollBarEnabled(false);
            ContactsActivity.this.emptyView.setText(LocaleController.getString("NoContacts", 2131166027));
          }

          public void onSearchExpand()
          {
            ContactsActivity.access$002(ContactsActivity.this, true);
          }

          public void onTextChanged(EditText paramEditText)
          {
            if (ContactsActivity.this.searchListViewAdapter == null)
              return;
            paramEditText = paramEditText.getText().toString();
            if (paramEditText.length() != 0)
            {
              ContactsActivity.access$202(ContactsActivity.this, true);
              if (ContactsActivity.this.listView != null)
              {
                ContactsActivity.this.listView.setAdapter(ContactsActivity.this.searchListViewAdapter);
                ContactsActivity.this.searchListViewAdapter.notifyDataSetChanged();
                ContactsActivity.this.listView.setFastScrollVisible(false);
                ContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
              }
              if (ContactsActivity.this.emptyView != null)
                ContactsActivity.this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
            }
            ContactsActivity.this.searchListViewAdapter.searchDialogs(paramEditText);
          }
        }).getSearchField().setHint(LocaleController.getString("Search", 2131166381));
        if ((!this.createSecretChat) && (!this.returnAsResult))
          ((ActionBarMenu)localObject).addItem(1, 2130837588);
        this.searchListViewAdapter = new SearchAdapter(paramContext, this.ignoreUsers, this.allowUsernameSearch, false, false, this.allowBots);
        if (!this.onlyUsers)
          break label472;
        i = 1;
        bool2 = this.needPhonebook;
        localObject = this.ignoreUsers;
        if (this.chat_id == 0)
          break label477;
      }
    label472: label477: for (boolean bool1 = true; ; bool1 = false)
    {
      this.listViewAdapter = new ContactsAdapter(paramContext, i, bool2, (HashMap)localObject, bool1);
      this.fragmentView = new FrameLayout(paramContext);
      localObject = (FrameLayout)this.fragmentView;
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.setShowAtCenter(true);
      this.emptyView.showTextView();
      ((FrameLayout)localObject).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setEmptyView(this.emptyView);
      this.listView.setSectionsType(1);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setFastScrollEnabled();
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setAdapter(this.listViewAdapter);
      ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if ((ContactsActivity.this.searching) && (ContactsActivity.this.searchWas))
          {
            paramView = (TLRPC.User)ContactsActivity.this.searchListViewAdapter.getItem(paramInt);
            if (paramView != null);
          }
          while (true)
          {
            return;
            if (ContactsActivity.this.searchListViewAdapter.isGlobalSearch(paramInt))
            {
              localObject = new ArrayList();
              ((ArrayList)localObject).add(paramView);
              MessagesController.getInstance().putUsers((ArrayList)localObject, false);
              MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject, null, false, true);
            }
            if (ContactsActivity.this.returnAsResult)
            {
              if ((ContactsActivity.this.ignoreUsers != null) && (ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(paramView.id))))
                continue;
              ContactsActivity.this.didSelectResult(paramView, true, null);
              return;
            }
            if (ContactsActivity.this.createSecretChat)
            {
              if (paramView.id == UserConfig.getClientUserId())
                continue;
              ContactsActivity.access$1002(ContactsActivity.this, true);
              SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), paramView);
              return;
            }
            Object localObject = new Bundle();
            ((Bundle)localObject).putInt("user_id", paramView.id);
            if (!MessagesController.checkCanOpenChat((Bundle)localObject, ContactsActivity.this))
              continue;
            ContactsActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
            return;
            int i = ContactsActivity.this.listViewAdapter.getSectionForPosition(paramInt);
            paramInt = ContactsActivity.this.listViewAdapter.getPositionInSectionForPosition(paramInt);
            if ((paramInt < 0) || (i < 0))
              continue;
            if (((!ContactsActivity.this.onlyUsers) || (ContactsActivity.this.chat_id != 0)) && (i == 0))
            {
              if (ContactsActivity.this.needPhonebook)
              {
                if (paramInt != 0)
                  continue;
                try
                {
                  paramView = new Intent("android.intent.action.SEND");
                  paramView.setType("text/plain");
                  paramView.putExtra("android.intent.extra.TEXT", ContactsController.getInstance().getInviteText());
                  ContactsActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramView, LocaleController.getString("InviteFriends", 2131165844)), 500);
                  return;
                }
                catch (java.lang.Exception paramView)
                {
                  FileLog.e(paramView);
                  return;
                }
              }
              if (ContactsActivity.this.chat_id != 0)
              {
                if (paramInt != 0)
                  continue;
                ContactsActivity.this.presentFragment(new GroupInviteActivity(ContactsActivity.this.chat_id));
                return;
              }
              if (paramInt == 0)
              {
                if (!MessagesController.isFeatureEnabled("chat_create", ContactsActivity.this))
                  continue;
                ContactsActivity.this.presentFragment(new GroupCreateActivity(), false);
                return;
              }
              if (paramInt == 1)
              {
                paramView = new Bundle();
                paramView.putBoolean("onlyUsers", true);
                paramView.putBoolean("destroyAfterSelect", true);
                paramView.putBoolean("createSecretChat", true);
                paramView.putBoolean("allowBots", false);
                ContactsActivity.this.presentFragment(new ContactsActivity(paramView), false);
                return;
              }
              if ((paramInt != 2) || (!MessagesController.isFeatureEnabled("broadcast_create", ContactsActivity.this)))
                continue;
              paramView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              if ((!BuildVars.DEBUG_VERSION) && (paramView.getBoolean("channel_intro", false)))
              {
                paramView = new Bundle();
                paramView.putInt("step", 0);
                ContactsActivity.this.presentFragment(new ChannelCreateActivity(paramView));
                return;
              }
              ContactsActivity.this.presentFragment(new ChannelIntroActivity());
              paramView.edit().putBoolean("channel_intro", true).commit();
              return;
            }
            paramView = ContactsActivity.this.listViewAdapter.getItem(i, paramInt);
            if ((paramView instanceof TLRPC.User))
            {
              paramView = (TLRPC.User)paramView;
              if (ContactsActivity.this.returnAsResult)
              {
                if ((ContactsActivity.this.ignoreUsers != null) && (ContactsActivity.this.ignoreUsers.containsKey(Integer.valueOf(paramView.id))))
                  continue;
                ContactsActivity.this.didSelectResult(paramView, true, null);
                return;
              }
              if (ContactsActivity.this.createSecretChat)
              {
                ContactsActivity.access$1002(ContactsActivity.this, true);
                SecretChatHelper.getInstance().startSecretChat(ContactsActivity.this.getParentActivity(), paramView);
                return;
              }
              localObject = new Bundle();
              ((Bundle)localObject).putInt("user_id", paramView.id);
              if (!MessagesController.checkCanOpenChat((Bundle)localObject, ContactsActivity.this))
                continue;
              ContactsActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
              return;
            }
            if (!(paramView instanceof ContactsController.Contact))
              continue;
            paramView = (ContactsController.Contact)paramView;
            if (!paramView.phones.isEmpty());
            for (paramView = (String)paramView.phones.get(0); (paramView != null) && (ContactsActivity.this.getParentActivity() != null); paramView = null)
            {
              localObject = new AlertDialog.Builder(ContactsActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("InviteUser", 2131165850));
              ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
              ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramView)
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  try
                  {
                    paramDialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", this.val$arg1, null));
                    paramDialogInterface.putExtra("sms_body", LocaleController.getString("InviteText", 2131165846));
                    ContactsActivity.this.getParentActivity().startActivityForResult(paramDialogInterface, 500);
                    return;
                  }
                  catch (java.lang.Exception paramDialogInterface)
                  {
                    FileLog.e(paramDialogInterface);
                  }
                }
              });
              ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              ContactsActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              return;
            }
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
          if ((paramInt == 1) && (ContactsActivity.this.searching) && (ContactsActivity.this.searchWas))
            AndroidUtilities.hideKeyboard(ContactsActivity.this.getParentActivity().getCurrentFocus());
        }

        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          super.onScrolled(paramRecyclerView, paramInt1, paramInt2);
        }
      });
      return this.fragmentView;
      if (this.createSecretChat)
      {
        this.actionBar.setTitle(LocaleController.getString("NewSecretChat", 2131166017));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NewMessageTitle", 2131166010));
      break;
      this.actionBar.setTitle(LocaleController.getString("Contacts", 2131165574));
      break;
      i = 0;
      break label180;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.contactsDidLoaded)
      if (this.listViewAdapter != null)
        this.listViewAdapter.notifyDataSetChanged();
    do
      while (true)
      {
        return;
        if (paramInt == NotificationCenter.updateInterfaces)
        {
          paramInt = ((Integer)paramArrayOfObject[0]).intValue();
          if (((paramInt & 0x2) == 0) && ((paramInt & 0x1) == 0) && ((paramInt & 0x4) == 0))
            continue;
          updateVisibleRows(paramInt);
          return;
        }
        if (paramInt != NotificationCenter.encryptedChatCreated)
          break;
        if ((!this.createSecretChat) || (!this.creatingChat))
          continue;
        paramArrayOfObject = (TLRPC.EncryptedChat)paramArrayOfObject[0];
        Bundle localBundle = new Bundle();
        localBundle.putInt("enc_id", paramArrayOfObject.id);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
        presentFragment(new ChatActivity(localBundle), true);
        return;
      }
    while ((paramInt != NotificationCenter.closeChats) || (this.creatingChat));
    removeSelfFromStack();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    Object localObject6 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = ContactsActivity.this.listView.getChildCount();
        paramInt = 0;
        if (paramInt < i)
        {
          View localView = ContactsActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof UserCell))
            ((UserCell)localView).update(0);
          while (true)
          {
            paramInt += 1;
            break;
            if (!(localView instanceof ProfileSearchCell))
              continue;
            ((ProfileSearchCell)localView).update(0);
          }
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SECTIONS, new Class[] { LetterSectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "windowBackgroundWhiteBlueText");
    Object localObject3 = this.listView;
    Object localObject4 = Theme.avatar_photoDrawable;
    Object localObject5 = Theme.avatar_broadcastDrawable;
    localObject3 = new ThemeDescription((View)localObject3, 0, new Class[] { UserCell.class }, null, new Drawable[] { localObject4, localObject5 }, null, "avatar_text");
    localObject4 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundRed");
    localObject5 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundOrange");
    ThemeDescription localThemeDescription17 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundViolet");
    ThemeDescription localThemeDescription18 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundGreen");
    ThemeDescription localThemeDescription19 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundCyan");
    ThemeDescription localThemeDescription20 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundBlue");
    localObject6 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundPink");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection");
    Object localObject7 = this.listView;
    Object localObject8 = Theme.dialogs_groupDrawable;
    Object localObject9 = Theme.dialogs_broadcastDrawable;
    Object localObject10 = Theme.dialogs_botDrawable;
    localObject7 = new ThemeDescription((View)localObject7, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject8, localObject9, localObject10 }, null, "chats_nameIcon");
    localObject8 = this.listView;
    localObject9 = Theme.dialogs_verifiedCheckDrawable;
    localObject8 = new ThemeDescription((View)localObject8, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject9 }, null, "chats_verifiedCheck");
    localObject9 = this.listView;
    localObject10 = Theme.dialogs_verifiedDrawable;
    localObject9 = new ThemeDescription((View)localObject9, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localObject10 }, null, "chats_verifiedBackground");
    localObject10 = this.listView;
    Object localObject11 = Theme.dialogs_offlinePaint;
    localObject10 = new ThemeDescription((View)localObject10, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject11, null, null, "windowBackgroundWhiteGrayText3");
    localObject11 = this.listView;
    Object localObject12 = Theme.dialogs_onlinePaint;
    localObject11 = new ThemeDescription((View)localObject11, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject12, null, null, "windowBackgroundWhiteBlueText3");
    localObject12 = this.listView;
    TextPaint localTextPaint = Theme.dialogs_namePaint;
    return (ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localObject1, localObject2, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localObject3, localObject4, localObject5, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localObject6, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, localObject7, localObject8, localObject9, localObject10, localObject11, new ThemeDescription((View)localObject12, 0, new Class[] { ProfileSearchCell.class }, localTextPaint, null, null, "chats_name") };
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    super.onDialogDismiss(paramDialog);
    if ((this.permissionDialog != null) && (paramDialog == this.permissionDialog) && (getParentActivity() != null))
      askForPermissons();
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    if (this.arguments != null)
    {
      this.onlyUsers = getArguments().getBoolean("onlyUsers", false);
      this.destroyAfterSelect = this.arguments.getBoolean("destroyAfterSelect", false);
      this.returnAsResult = this.arguments.getBoolean("returnAsResult", false);
      this.createSecretChat = this.arguments.getBoolean("createSecretChat", false);
      this.selectAlertString = this.arguments.getString("selectAlertString");
      this.allowUsernameSearch = this.arguments.getBoolean("allowUsernameSearch", true);
      this.needForwardCount = this.arguments.getBoolean("needForwardCount", true);
      this.allowBots = this.arguments.getBoolean("allowBots", true);
      this.addingToChannel = this.arguments.getBoolean("addingToChannel", false);
      this.chat_id = this.arguments.getInt("chat_id", 0);
    }
    while (true)
    {
      ContactsController.getInstance().checkInviteText();
      return true;
      this.needPhonebook = true;
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    this.delegate = null;
  }

  public void onPause()
  {
    super.onPause();
    if (this.actionBar != null)
      this.actionBar.closeSearchField();
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 1)
    {
      paramInt = 0;
      while (paramInt < paramArrayOfString.length)
      {
        if ((paramArrayOfInt.length <= paramInt) || (paramArrayOfInt[paramInt] != 0))
        {
          paramInt += 1;
          continue;
        }
        String str = paramArrayOfString[paramInt];
        int i = -1;
        switch (str.hashCode())
        {
        default:
        case 1977429404:
        }
        while (true)
          switch (i)
          {
          default:
            break;
          case 0:
            ContactsController.getInstance().readContacts();
            break;
            if (!str.equals("android.permission.READ_CONTACTS"))
              continue;
            i = 0;
          }
      }
    }
  }

  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
    if ((this.checkPermission) && (Build.VERSION.SDK_INT >= 23))
    {
      Object localObject = getParentActivity();
      if (localObject != null)
      {
        this.checkPermission = false;
        if (((Activity)localObject).checkSelfPermission("android.permission.READ_CONTACTS") != 0)
        {
          if (!((Activity)localObject).shouldShowRequestPermissionRationale("android.permission.READ_CONTACTS"))
            break label132;
          localObject = new AlertDialog.Builder((Context)localObject);
          ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
          ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PermissionContacts", 2131166253));
          ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
          localObject = ((AlertDialog.Builder)localObject).create();
          this.permissionDialog = ((AlertDialog)localObject);
          showDialog((Dialog)localObject);
        }
      }
    }
    return;
    label132: askForPermissons();
  }

  public void setDelegate(ContactsActivityDelegate paramContactsActivityDelegate)
  {
    this.delegate = paramContactsActivityDelegate;
  }

  public void setIgnoreUsers(HashMap<Integer, TLRPC.User> paramHashMap)
  {
    this.ignoreUsers = paramHashMap;
  }

  public static abstract interface ContactsActivityDelegate
  {
    public abstract void didSelectContact(TLRPC.User paramUser, String paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ContactsActivity
 * JD-Core Version:    0.6.0
 */