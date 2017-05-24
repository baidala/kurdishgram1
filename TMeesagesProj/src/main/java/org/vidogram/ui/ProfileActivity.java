package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.provider.Settings.System;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.b.a.a.o;
import com.google.firebase.crash.FirebaseCrash;
import itman.Vidofilm.a.u;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.vidogram.VidogramUi.WebRTC.e;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.NotificationsController;
import org.vidogram.messenger.SecretChatHelper;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.query.BotQuery;
import org.vidogram.messenger.query.MessagesQuery;
import org.vidogram.messenger.query.SharedMediaQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInfo;
import org.vidogram.tgnet.TLRPC.ChannelParticipant;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_channelFull;
import org.vidogram.tgnet.TLRPC.TL_channelParticipant;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.vidogram.tgnet.TLRPC.TL_channelRoleEditor;
import org.vidogram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.vidogram.tgnet.TLRPC.TL_channels_editAdmin;
import org.vidogram.tgnet.TLRPC.TL_channels_getParticipants;
import org.vidogram.tgnet.TLRPC.TL_chatChannelParticipant;
import org.vidogram.tgnet.TLRPC.TL_chatFull;
import org.vidogram.tgnet.TLRPC.TL_chatParticipant;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.vidogram.tgnet.TLRPC.TL_chatParticipants;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantsForbidden;
import org.vidogram.tgnet.TLRPC.TL_chatPhotoEmpty;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_encryptedChat;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.vidogram.tgnet.TLRPC.TL_peerNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_userEmpty;
import org.vidogram.tgnet.TLRPC.TL_userFull;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BackDrawable;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.AboutLinkCell;
import org.vidogram.ui.Cells.AboutLinkCell.AboutLinkCellDelegate;
import org.vidogram.ui.Cells.DividerCell;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCell;
import org.vidogram.ui.Cells.TextDetailCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.AvatarUpdater;
import org.vidogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.ChatActivityEnterView;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.IdenticonDrawable;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.voip.VoIPHelper;

public class ProfileActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int add_contact = 1;
  private static final int add_shortcut = 14;
  private static final int block_contact = 2;
  private static final int call_item = 15;
  private static final int convert_to_supergroup = 13;
  private static final int delete_contact = 5;
  private static final int edit_channel = 12;
  private static final int edit_contact = 4;
  private static final int edit_name = 8;
  private static final int invite_to_group = 9;
  private static final int leave_group = 7;
  private static final int set_admins = 11;
  private static final int share = 10;
  private static final int share_contact = 3;
  private int addMemberRow;
  private boolean allowProfileAnimation = true;
  private ActionBarMenuItem animatingItem;
  private float animationProgress;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater;
  private int blockedUsersRow;
  private TLRPC.BotInfo botInfo;
  private ImageView callButton;
  private AnimatorSet callButtonAnimation;
  private int callHistoryEmptyRow;
  private int callHistoryEndRow;
  private int callHistorySeeMoreEmptyRow;
  private int callHistorySeeMoreRow;
  private int callHistoryShadowRow;
  private ActionBarMenuItem callItem;
  private int channelInfoRow;
  private int channelNameRow;
  private int chat_id;
  private int convertHelpRow;
  private int convertRow;
  private boolean creatingChat;
  private TLRPC.Chat currentChat;
  private TLRPC.EncryptedChat currentEncryptedChat;
  private long dialog_id;
  private int emptyRow;
  private int emptyRowChat;
  private int emptyRowChat2;
  private int extraHeight;
  private int groupsInCommonRow;
  ArrayList<itman.Vidofilm.a.b> history;
  private TLRPC.ChatFull info;
  private int initialAnimationExtraHeight;
  private LinearLayoutManager layoutManager;
  private int leaveChannelRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int loadMoreMembersRow;
  private boolean loadingUsers;
  private int managementRow;
  private int membersEndRow;
  private int membersRow;
  private int membersSectionRow;
  private long mergeDialogId;
  private SimpleTextView[] nameTextView = new SimpleTextView[2];
  private int onlineCount = -1;
  private SimpleTextView[] onlineTextView = new SimpleTextView[2];
  private boolean openAnimationInProgress;
  private HashMap<Integer, TLRPC.ChatParticipant> participantsMap = new HashMap();
  private int phoneRow;
  private boolean playProfileAnimation;
  private boolean recreateMenuAfterAnimation;
  private int rowCount = 0;
  private int sectionRow;
  private int selectedUser;
  private int settingsKeyRow;
  private int settingsNotificationsRow;
  private int settingsTimerRow;
  private int sharedMediaRow;
  private ArrayList<Integer> sortedUsers;
  private int startSecretChatRow;
  private TopView topView;
  private int totalMediaCount = -1;
  private int totalMediaCountMerge = -1;
  private TLRPC.User user;
  private boolean userBlocked;
  private int userInfoRow;
  private int userSectionRow;
  private int user_id;
  private int usernameRow;
  private boolean usersEndReached;
  private ImageView writeButton;
  private AnimatorSet writeButtonAnimation;

  public ProfileActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void CallFailure()
  {
    try
    {
      org.vidogram.VidogramUi.b.a(ApplicationLoader.applicationContext).a();
      if (getParentActivity() == null)
        return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      localBuilder.setMessage(LocaleController.formatString("CallFailure", 2131166759, new Object[0]));
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      localBuilder.setPositiveButton(LocaleController.getString("SendInvitation", 2131166785), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          ProfileActivity.this.processSendingText(ContactsController.getInstance().getInviteText());
          com.b.a.a.a.a().a(new o().a("Vidogram"));
        }
      });
      showDialog(localBuilder.create());
      return;
    }
    catch (Exception localException)
    {
    }
  }

  private void ConnectionFailde()
  {
    int i = 0;
    if (getParentActivity() == null)
      return;
    if (Settings.System.getInt(getParentActivity().getContentResolver(), "airplane_mode_on", 0) != 0)
      i = 1;
    Object localObject2 = new AlertDialog.Builder(getParentActivity());
    if (i != 0)
    {
      localObject1 = LocaleController.getString("VoipOfflineAirplaneTitle", 2131166594);
      localObject2 = ((AlertDialog.Builder)localObject2).setTitle((CharSequence)localObject1);
      if (i == 0)
        break label165;
    }
    label165: for (Object localObject1 = LocaleController.getString("VoipOfflineAirplane", 2131166593); ; localObject1 = LocaleController.getString("VoipOffline", 2131166592))
    {
      localObject1 = ((AlertDialog.Builder)localObject2).setMessage((CharSequence)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      if (i != 0)
      {
        localObject2 = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
        if (((Intent)localObject2).resolveActivity(getParentActivity().getPackageManager()) != null)
          ((AlertDialog.Builder)localObject1).setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", 2131166595), new DialogInterface.OnClickListener((Intent)localObject2)
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              ProfileActivity.this.getParentActivity().startActivity(this.val$settingsIntent);
            }
          });
      }
      ((AlertDialog.Builder)localObject1).show();
      return;
      localObject1 = LocaleController.getString("VoipOfflineTitle", 2131166596);
      break;
    }
  }

  private void VidogramCall()
  {
    try
    {
      u localu = org.vidogram.VidogramUi.b.a(getParentActivity()).a(this.user_id + "");
      if (ConnectionsManager.getInstance().getConnectionState() != 3)
      {
        ConnectionFailde();
        return;
      }
      if (itman.Vidofilm.b.a(getParentActivity()).l() == null)
      {
        new e(getParentActivity()).a(this.user);
        return;
      }
    }
    catch (Exception localException)
    {
      FirebaseCrash.a(localException);
      return;
    }
    if ((localException == null) && (MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)).contact))
    {
      CallFailure();
      return;
    }
    new e(getParentActivity()).a(this.user);
  }

  private void checkListViewScroll()
  {
    boolean bool = false;
    if ((this.listView.getChildCount() <= 0) || (this.openAnimationInProgress));
    while (true)
    {
      return;
      View localView = this.listView.getChildAt(0);
      RecyclerListView.Holder localHolder = (RecyclerListView.Holder)this.listView.findContainingViewHolder(localView);
      int i = localView.getTop();
      if ((i >= 0) && (localHolder != null) && (localHolder.getAdapterPosition() == 0));
      while (this.extraHeight != i)
      {
        this.extraHeight = i;
        this.topView.invalidate();
        if (this.playProfileAnimation)
        {
          if (this.extraHeight != 0)
            bool = true;
          this.allowProfileAnimation = bool;
        }
        needLayout();
        return;
        i = 0;
      }
    }
  }

  private void createActionBarMenu()
  {
    ActionBarMenu localActionBarMenu = this.actionBar.createMenu();
    localActionBarMenu.clearItems();
    this.animatingItem = null;
    Object localObject3 = null;
    Object localObject1 = null;
    Object localObject2;
    if (this.user_id != 0)
      if (UserConfig.getClientUserId() != this.user_id)
      {
        localObject1 = MessagesController.getInstance().getUserFull(this.user_id);
        if ((MessagesController.getInstance().callsEnabled) && (localObject1 != null) && (((TLRPC.TL_userFull)localObject1).phone_calls_available))
          this.callItem = localActionBarMenu.addItem(15, 2130837758);
        if (ContactsController.getInstance().contactsDict.get(this.user_id) == null)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
          if (localObject1 == null)
            return;
          localObject2 = localActionBarMenu.addItem(10, 2130837738);
          if (((TLRPC.User)localObject1).bot)
          {
            if (!((TLRPC.User)localObject1).bot_nochats)
              ((ActionBarMenuItem)localObject2).addSubItem(9, LocaleController.getString("BotInvite", 2131165394));
            ((ActionBarMenuItem)localObject2).addSubItem(10, LocaleController.getString("BotShare", 2131165398));
          }
          if ((((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0))
          {
            ((ActionBarMenuItem)localObject2).addSubItem(1, LocaleController.getString("AddContact", 2131165275));
            ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", 2131166450));
            if (!this.userBlocked)
            {
              localObject1 = LocaleController.getString("BlockContact", 2131165383);
              ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1);
              localObject1 = localObject2;
            }
          }
        }
      }
    while (true)
    {
      localObject2 = localObject1;
      if (localObject1 == null)
        localObject2 = localActionBarMenu.addItem(10, 2130837738);
      ((ActionBarMenuItem)localObject2).addSubItem(14, LocaleController.getString("AddShortcut", 2131165284));
      return;
      localObject1 = LocaleController.getString("Unblock", 2131166530);
      break;
      if (((TLRPC.User)localObject1).bot)
      {
        if (!this.userBlocked);
        for (localObject1 = LocaleController.getString("BotStop", 2131165402); ; localObject1 = LocaleController.getString("BotRestart", 2131165396))
        {
          ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1);
          break;
        }
      }
      if (!this.userBlocked);
      for (localObject1 = LocaleController.getString("BlockContact", 2131165383); ; localObject1 = LocaleController.getString("Unblock", 2131166530))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1);
        break;
      }
      localObject2 = localActionBarMenu.addItem(10, 2130837738);
      ((ActionBarMenuItem)localObject2).addSubItem(3, LocaleController.getString("ShareContact", 2131166450));
      if (!this.userBlocked);
      for (localObject1 = LocaleController.getString("BlockContact", 2131165383); ; localObject1 = LocaleController.getString("Unblock", 2131166530))
      {
        ((ActionBarMenuItem)localObject2).addSubItem(2, (String)localObject1);
        ((ActionBarMenuItem)localObject2).addSubItem(4, LocaleController.getString("EditContact", 2131165664));
        ((ActionBarMenuItem)localObject2).addSubItem(5, LocaleController.getString("DeleteContact", 2131165639));
        localObject1 = localObject2;
        break;
      }
      localObject1 = localActionBarMenu.addItem(10, 2130837738);
      ((ActionBarMenuItem)localObject1).addSubItem(3, LocaleController.getString("ShareContact", 2131166450));
      continue;
      if (this.chat_id == 0)
        continue;
      if (this.chat_id > 0)
      {
        TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
        if (this.writeButton != null)
        {
          boolean bool = ChatObject.isChannel(this.currentChat);
          if (((!bool) || (this.currentChat.creator) || ((this.currentChat.megagroup) && (this.currentChat.editor))) && ((bool) || (this.currentChat.admin) || (this.currentChat.creator) || (!this.currentChat.admins_enabled)))
            break label791;
          this.writeButton.setImageResource(2130837715);
          this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
        }
        while (true)
        {
          if (!ChatObject.isChannel(localChat))
            break label815;
          if (!localChat.creator)
          {
            localObject2 = localObject3;
            if (localChat.megagroup)
            {
              localObject2 = localObject3;
              if (!localChat.editor);
            }
          }
          else
          {
            localObject2 = localActionBarMenu.addItem(10, 2130837738);
            ((ActionBarMenuItem)localObject2).addSubItem(12, LocaleController.getString("ChannelEdit", 2131165466));
          }
          localObject1 = localObject2;
          if (localChat.creator)
            break;
          localObject1 = localObject2;
          if (localChat.left)
            break;
          localObject1 = localObject2;
          if (localChat.kicked)
            break;
          localObject1 = localObject2;
          if (!localChat.megagroup)
            break;
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = localActionBarMenu.addItem(10, 2130837738);
          ((ActionBarMenuItem)localObject1).addSubItem(7, LocaleController.getString("LeaveMegaMenu", 2131165904));
          break;
          label791: this.writeButton.setImageResource(2130837714);
          this.writeButton.setPadding(0, 0, 0, 0);
        }
        label815: localObject1 = localActionBarMenu.addItem(10, 2130837738);
        if ((localChat.creator) && (this.chat_id > 0))
          ((ActionBarMenuItem)localObject1).addSubItem(11, LocaleController.getString("SetAdmins", 2131166440));
        if ((!localChat.admins_enabled) || (localChat.creator) || (localChat.admin))
          ((ActionBarMenuItem)localObject1).addSubItem(8, LocaleController.getString("EditName", 2131165667));
        if ((localChat.creator) && ((this.info == null) || (this.info.participants.participants.size() > 0)))
          ((ActionBarMenuItem)localObject1).addSubItem(13, LocaleController.getString("ConvertGroupMenu", 2131165582));
        ((ActionBarMenuItem)localObject1).addSubItem(7, LocaleController.getString("DeleteAndExit", 2131165634));
        continue;
      }
      localObject1 = localActionBarMenu.addItem(10, 2130837738);
      ((ActionBarMenuItem)localObject1).addSubItem(8, LocaleController.getString("EditName", 2131165667));
    }
  }

  private void fetchUsersFromChannelInfo()
  {
    if (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants != null))
    {
      int i = 0;
      while (i < this.info.participants.participants.size())
      {
        TLRPC.ChatParticipant localChatParticipant = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
        this.participantsMap.put(Integer.valueOf(localChatParticipant.user_id), localChatParticipant);
        i += 1;
      }
    }
  }

  private void fixLayout()
  {
    if (this.fragmentView == null)
      return;
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (ProfileActivity.this.fragmentView != null)
        {
          ProfileActivity.this.checkListViewScroll();
          ProfileActivity.this.needLayout();
          ProfileActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        return true;
      }
    });
  }

  private void getChannelParticipants(boolean paramBoolean)
  {
    int j = 0;
    if ((this.loadingUsers) || (this.participantsMap == null) || (this.info == null))
      return;
    this.loadingUsers = true;
    int i;
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants;
    if ((!this.participantsMap.isEmpty()) && (paramBoolean))
    {
      i = 300;
      localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
      localTL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chat_id);
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
      if (!paramBoolean)
        break label135;
    }
    while (true)
    {
      localTL_channels_getParticipants.offset = j;
      localTL_channels_getParticipants.limit = 200;
      i = ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate(localTL_channels_getParticipants, i)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)this.val$response;
                MessagesController.getInstance().putUsers(localTL_channels_channelParticipants.users, false);
                if (localTL_channels_channelParticipants.users.size() != 200)
                  ProfileActivity.access$4902(ProfileActivity.this, true);
                if (ProfileActivity.20.this.val$req.offset == 0)
                {
                  ProfileActivity.this.participantsMap.clear();
                  ProfileActivity.this.info.participants = new TLRPC.TL_chatParticipants();
                  MessagesStorage.getInstance().putUsersAndChats(localTL_channels_channelParticipants.users, null, true, true);
                  MessagesStorage.getInstance().updateChannelUsers(ProfileActivity.this.chat_id, localTL_channels_channelParticipants.participants);
                }
                int i = 0;
                while (i < localTL_channels_channelParticipants.participants.size())
                {
                  TLRPC.TL_chatChannelParticipant localTL_chatChannelParticipant = new TLRPC.TL_chatChannelParticipant();
                  localTL_chatChannelParticipant.channelParticipant = ((TLRPC.ChannelParticipant)localTL_channels_channelParticipants.participants.get(i));
                  localTL_chatChannelParticipant.inviter_id = localTL_chatChannelParticipant.channelParticipant.inviter_id;
                  localTL_chatChannelParticipant.user_id = localTL_chatChannelParticipant.channelParticipant.user_id;
                  localTL_chatChannelParticipant.date = localTL_chatChannelParticipant.channelParticipant.date;
                  if (!ProfileActivity.this.participantsMap.containsKey(Integer.valueOf(localTL_chatChannelParticipant.user_id)))
                  {
                    ProfileActivity.this.info.participants.participants.add(localTL_chatChannelParticipant);
                    ProfileActivity.this.participantsMap.put(Integer.valueOf(localTL_chatChannelParticipant.user_id), localTL_chatChannelParticipant);
                  }
                  i += 1;
                }
              }
              ProfileActivity.this.updateOnlineCount();
              ProfileActivity.access$5102(ProfileActivity.this, false);
              ProfileActivity.this.updateRowsIds();
              if (ProfileActivity.this.listAdapter != null)
                ProfileActivity.this.listAdapter.notifyDataSetChanged();
            }
          }
          , this.val$delay);
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
      return;
      i = 0;
      break;
      label135: j = this.participantsMap.size();
    }
  }

  private void kickUser(int paramInt)
  {
    int k = 0;
    if (paramInt != 0)
    {
      MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(paramInt)), this.info);
      if ((this.currentChat.megagroup) && (this.info != null) && (this.info.participants != null))
      {
        i = 0;
        if (i >= this.info.participants.participants.size())
          break label359;
        if (((TLRPC.TL_chatChannelParticipant)this.info.participants.participants.get(i)).channelParticipant.user_id != paramInt)
          break label250;
        if (this.info != null)
        {
          TLRPC.ChatFull localChatFull = this.info;
          localChatFull.participants_count -= 1;
        }
        this.info.participants.participants.remove(i);
      }
    }
    label359: for (int i = 1; ; i = 0)
    {
      int j = i;
      if (this.info != null)
      {
        j = i;
        if (this.info.participants == null);
      }
      while (true)
      {
        j = i;
        if (k < this.info.participants.participants.size())
        {
          if (((TLRPC.ChatParticipant)this.info.participants.participants.get(k)).user_id == paramInt)
          {
            this.info.participants.participants.remove(k);
            j = 1;
          }
        }
        else
        {
          if (j != 0)
          {
            updateOnlineCount();
            updateRowsIds();
            this.listAdapter.notifyDataSetChanged();
          }
          return;
          label250: i += 1;
          break;
        }
        k += 1;
      }
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
      if (AndroidUtilities.isTablet())
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-this.chat_id) });
      while (true)
      {
        MessagesController.getInstance().deleteUserFromChat(this.chat_id, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), this.info);
        this.playProfileAnimation = false;
        finishFragment();
        return;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      }
    }
  }

  private void leaveChatPressed()
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    String str;
    if ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup))
      if (ChatObject.isChannel(this.chat_id))
      {
        str = LocaleController.getString("ChannelLeaveAlert", 2131165473);
        localBuilder.setMessage(str);
      }
    while (true)
    {
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          ProfileActivity.this.kickUser(0);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      showDialog(localBuilder.create());
      return;
      str = LocaleController.getString("AreYouSureDeleteAndExit", 2131165340);
      break;
      localBuilder.setMessage(LocaleController.getString("AreYouSureDeleteAndExit", 2131165340));
    }
  }

  private void needLayout()
  {
    int i;
    Object localObject;
    float f1;
    label135: label177: int j;
    label190: label210: label470: float f2;
    if (this.actionBar.getOccupyStatusBar())
    {
      i = AndroidUtilities.statusBarHeight;
      i = ActionBar.getCurrentActionBarHeight() + i;
      if ((this.listView != null) && (!this.openAnimationInProgress))
      {
        localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        if (((FrameLayout.LayoutParams)localObject).topMargin != i)
        {
          ((FrameLayout.LayoutParams)localObject).topMargin = i;
          this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
        }
      }
      if (this.avatarImage == null)
        return;
      f1 = this.extraHeight / AndroidUtilities.dp(88.0F);
      this.listView.setTopGlowOffset(this.extraHeight);
      if (this.writeButton != null)
      {
        localObject = this.writeButton;
        if (!this.actionBar.getOccupyStatusBar())
          break label790;
        i = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (!this.openAnimationInProgress)
        {
          if (f1 <= 0.2F)
            break label796;
          i = 1;
          if (this.writeButton.getTag() != null)
            break label802;
          j = 1;
          if (i != j)
          {
            if (i == 0)
              break label808;
            this.writeButton.setTag(null);
            if (this.writeButtonAnimation != null)
            {
              localObject = this.writeButtonAnimation;
              this.writeButtonAnimation = null;
              ((AnimatorSet)localObject).cancel();
            }
            this.writeButtonAnimation = new AnimatorSet();
            if (i == 0)
              break label822;
            this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
            this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
            label334: this.writeButtonAnimation.setDuration(150L);
            this.writeButtonAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if ((ProfileActivity.this.writeButtonAnimation != null) && (ProfileActivity.this.writeButtonAnimation.equals(paramAnimator)))
                  ProfileActivity.access$5302(ProfileActivity.this, null);
              }
            });
            this.writeButtonAnimation.start();
          }
        }
      }
      if (this.callButton != null)
      {
        localObject = this.callButton;
        if (!this.actionBar.getOccupyStatusBar())
          break label914;
        i = AndroidUtilities.statusBarHeight;
        label395: ((ImageView)localObject).setTranslationY(i + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (!this.openAnimationInProgress)
        {
          if (f1 <= 0.2F)
            break label920;
          i = 1;
          label437: if (this.callButton.getTag() != null)
            break label926;
          j = 1;
          label450: if (i != j)
          {
            if (i == 0)
              break label932;
            this.callButton.setTag(null);
            if (this.callButtonAnimation != null)
            {
              localObject = this.callButtonAnimation;
              this.callButtonAnimation = null;
              ((AnimatorSet)localObject).cancel();
            }
            this.callButtonAnimation = new AnimatorSet();
            if (i == 0)
              break label946;
            this.callButtonAnimation.setInterpolator(new DecelerateInterpolator());
            this.callButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.callButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.callButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.callButton, "alpha", new float[] { 1.0F }) });
            label594: this.callButtonAnimation.setDuration(150L);
            this.callButtonAnimation.setDuration(150L);
            this.callButtonAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if ((ProfileActivity.this.callButtonAnimation != null) && (ProfileActivity.this.callButtonAnimation.equals(paramAnimator)))
                  ProfileActivity.access$5402(ProfileActivity.this, null);
              }
            });
            this.callButtonAnimation.start();
          }
        }
      }
      if (!this.actionBar.getOccupyStatusBar())
        break label1038;
      i = AndroidUtilities.statusBarHeight;
      label653: f2 = i + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setScaleX((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setScaleY((42.0F + 18.0F * f1) / 42.0F);
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      i = 0;
      label759: if (i >= 2)
        return;
      if (this.nameTextView[i] != null)
        break label1044;
    }
    label790: label796: label802: label808: label822: 
    do
    {
      i += 1;
      break label759;
      i = 0;
      break;
      i = 0;
      break label135;
      i = 0;
      break label177;
      j = 0;
      break label190;
      this.writeButton.setTag(Integer.valueOf(0));
      break label210;
      this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
      break label334;
      i = 0;
      break label395;
      i = 0;
      break label437;
      j = 0;
      break label450;
      this.callButton.setTag(Integer.valueOf(0));
      break label470;
      this.callButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.callButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.callButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.callButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.callButton, "alpha", new float[] { 0.0F }) });
      break label594;
      i = 0;
      break label653;
      this.nameTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.nameTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(1.3F) + AndroidUtilities.dp(7.0F) * f1);
      this.onlineTextView[i].setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.onlineTextView[i].setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(24.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView[i].setScaleX(0.12F * f1 + 1.0F);
      this.nameTextView[i].setScaleY(0.12F * f1 + 1.0F);
    }
    while ((i != 1) || (this.openAnimationInProgress));
    label914: label920: label926: label932: label946: label1216: int k;
    label1038: label1044: if (AndroidUtilities.isTablet())
    {
      j = AndroidUtilities.dp(490.0F);
      if (this.callItem == null)
        break label1462;
      k = 48;
      label1227: j = (int)(j - AndroidUtilities.dp((k + 40) * (1.0F - f1) + 126.0F) - this.nameTextView[i].getTranslationX());
      float f3 = this.nameTextView[i].getPaint().measureText(this.nameTextView[i].getText().toString());
      float f4 = this.nameTextView[i].getScaleX();
      float f5 = this.nameTextView[i].getSideDrawablesSize();
      localObject = (FrameLayout.LayoutParams)this.nameTextView[i].getLayoutParams();
      if (j >= f5 + f3 * f4)
        break label1468;
    }
    label1462: label1468: for (((FrameLayout.LayoutParams)localObject).width = (int)Math.ceil(j / this.nameTextView[i].getScaleX()); ; ((FrameLayout.LayoutParams)localObject).width = -2)
    {
      this.nameTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      localObject = (FrameLayout.LayoutParams)this.onlineTextView[i].getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).rightMargin = (int)Math.ceil(this.onlineTextView[i].getTranslationX() + AndroidUtilities.dp(8.0F) + AndroidUtilities.dp(40.0F) * (1.0F - f1));
      this.onlineTextView[i].setLayoutParams((ViewGroup.LayoutParams)localObject);
      break;
      j = AndroidUtilities.displaySize.x;
      break label1216;
      k = 0;
      break label1227;
    }
  }

  private void openAddMember()
  {
    int i = 0;
    boolean bool = true;
    Object localObject = new Bundle();
    ((Bundle)localObject).putBoolean("onlyUsers", true);
    ((Bundle)localObject).putBoolean("destroyAfterSelect", true);
    ((Bundle)localObject).putBoolean("returnAsResult", true);
    if (!ChatObject.isChannel(this.currentChat));
    while (true)
    {
      ((Bundle)localObject).putBoolean("needForwardCount", bool);
      if (this.chat_id > 0)
      {
        if (this.currentChat.creator)
          ((Bundle)localObject).putInt("chat_id", this.currentChat.id);
        ((Bundle)localObject).putString("selectAlertString", LocaleController.getString("AddToTheGroup", 2131165291));
      }
      localObject = new ContactsActivity((Bundle)localObject);
      ((ContactsActivity)localObject).setDelegate(new ContactsActivity.ContactsActivityDelegate()
      {
        public void didSelectContact(TLRPC.User paramUser, String paramString)
        {
          MessagesController localMessagesController = MessagesController.getInstance();
          int j = ProfileActivity.this.chat_id;
          TLRPC.ChatFull localChatFull = ProfileActivity.this.info;
          if (paramString != null);
          for (int i = Utilities.parseInt(paramString).intValue(); ; i = 0)
          {
            localMessagesController.addUserToChat(j, paramUser, localChatFull, i, null, ProfileActivity.this);
            return;
          }
        }
      });
      if ((this.info == null) || (this.info.participants == null))
        break;
      HashMap localHashMap = new HashMap();
      while (true)
        if (i < this.info.participants.participants.size())
        {
          localHashMap.put(Integer.valueOf(((TLRPC.ChatParticipant)this.info.participants.participants.get(i)).user_id), null);
          i += 1;
          continue;
          bool = false;
          break;
        }
      ((ContactsActivity)localObject).setIgnoreUsers(localHashMap);
    }
    presentFragment((BaseFragment)localObject);
  }

  private boolean processOnClickOrPress(int paramInt)
  {
    Object localObject1;
    Object localObject2;
    Object localObject3;
    if ((paramInt == this.usernameRow) || (paramInt == this.channelNameRow))
    {
      if (paramInt == this.usernameRow)
      {
        localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
        if ((localObject1 == null) || (((TLRPC.User)localObject1).username == null))
          return false;
      }
      for (localObject1 = ((TLRPC.User)localObject1).username; ; localObject1 = ((TLRPC.Chat)localObject1).username)
      {
        localObject2 = new AlertDialog.Builder(getParentActivity());
        localObject3 = LocaleController.getString("Copy", 2131165583);
        localObject1 = new DialogInterface.OnClickListener((String)localObject1)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            if (paramInt == 0);
            try
            {
              ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "@" + this.val$username));
              return;
            }
            catch (Exception paramDialogInterface)
            {
              FileLog.e(paramDialogInterface);
            }
          }
        };
        ((AlertDialog.Builder)localObject2).setItems(new CharSequence[] { localObject3 }, (DialogInterface.OnClickListener)localObject1);
        showDialog(((AlertDialog.Builder)localObject2).create());
        return true;
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
        if ((localObject1 == null) || (((TLRPC.Chat)localObject1).username == null))
          return false;
      }
    }
    if (paramInt == this.phoneRow)
    {
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if ((localObject1 == null) || (((TLRPC.User)localObject1).phone == null) || (((TLRPC.User)localObject1).phone.length() == 0) || (getParentActivity() == null))
        return false;
      localObject2 = new AlertDialog.Builder(getParentActivity());
      localObject3 = new ArrayList();
      ArrayList localArrayList = new ArrayList();
      TLRPC.TL_userFull localTL_userFull = MessagesController.getInstance().getUserFull(((TLRPC.User)localObject1).id);
      if ((MessagesController.getInstance().callsEnabled) && (localTL_userFull != null) && (localTL_userFull.phone_calls_available))
      {
        ((ArrayList)localObject3).add(LocaleController.getString("CallViaTelegram", 2131165422));
        localArrayList.add(Integer.valueOf(2));
      }
      if (this.user_id != UserConfig.getClientUserId())
      {
        ((ArrayList)localObject3).add(LocaleController.getString("VidogramCall", 2131166809));
        localArrayList.add(Integer.valueOf(3));
      }
      ((ArrayList)localObject3).add(LocaleController.getString("Call", 2131165409));
      localArrayList.add(Integer.valueOf(0));
      ((ArrayList)localObject3).add(LocaleController.getString("Copy", 2131165583));
      localArrayList.add(Integer.valueOf(1));
      ((AlertDialog.Builder)localObject2).setItems((CharSequence[])((ArrayList)localObject3).toArray(new CharSequence[((ArrayList)localObject3).size()]), new DialogInterface.OnClickListener(localArrayList, (TLRPC.User)localObject1)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramInt = ((Integer)this.val$actions.get(paramInt)).intValue();
          if (paramInt == 0);
          do
          {
            try
            {
              paramDialogInterface = new Intent("android.intent.action.DIAL", Uri.parse("tel:+" + this.val$user.phone));
              paramDialogInterface.addFlags(268435456);
              ProfileActivity.this.getParentActivity().startActivityForResult(paramDialogInterface, 500);
              return;
            }
            catch (Exception paramDialogInterface)
            {
              FileLog.e(paramDialogInterface);
              return;
            }
            if (paramInt == 1)
              try
              {
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", "+" + this.val$user.phone));
                return;
              }
              catch (Exception paramDialogInterface)
              {
                FileLog.e(paramDialogInterface);
                return;
              }
            if (paramInt != 2)
              continue;
            VoIPHelper.startCall(this.val$user, ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUserFull(this.val$user.id));
            return;
          }
          while (paramInt != 3);
          ProfileActivity.this.VidogramCall();
        }
      });
      showDialog(((AlertDialog.Builder)localObject2).create());
      return true;
    }
    if ((paramInt == this.channelInfoRow) || (paramInt == this.userInfoRow))
    {
      localObject1 = new AlertDialog.Builder(getParentActivity());
      localObject2 = LocaleController.getString("Copy", 2131165583);
      localObject3 = new DialogInterface.OnClickListener(paramInt)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          do
            try
            {
              if (this.val$position == ProfileActivity.this.channelInfoRow)
              {
                paramDialogInterface = ProfileActivity.this.info.about;
              }
              else
              {
                paramDialogInterface = MessagesController.getInstance().getUserFull(ProfileActivity.this.botInfo.user_id);
                if (paramDialogInterface == null)
                  break label92;
                paramDialogInterface = paramDialogInterface.about;
                break;
                ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", paramDialogInterface));
                return;
              }
            }
            catch (Exception paramDialogInterface)
            {
              FileLog.e(paramDialogInterface);
              return;
            }
          while (paramDialogInterface != null);
          return;
          while (true)
          {
            break;
            label92: paramDialogInterface = null;
          }
        }
      };
      ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { localObject2 }, (DialogInterface.OnClickListener)localObject3);
      showDialog(((AlertDialog.Builder)localObject1).create());
      return true;
    }
    return false;
  }

  private void updateOnlineCount()
  {
    this.onlineCount = 0;
    int j = ConnectionsManager.getInstance().getCurrentTime();
    this.sortedUsers.clear();
    if (((this.info instanceof TLRPC.TL_chatFull)) || (((this.info instanceof TLRPC.TL_channelFull)) && (this.info.participants_count <= 200) && (this.info.participants != null)))
    {
      int i = 0;
      while (i < this.info.participants.participants.size())
      {
        Object localObject = (TLRPC.ChatParticipant)this.info.participants.participants.get(i);
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
        if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > j) || (((TLRPC.User)localObject).id == UserConfig.getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000))
          this.onlineCount += 1;
        this.sortedUsers.add(Integer.valueOf(i));
        i += 1;
      }
    }
    try
    {
      Collections.sort(this.sortedUsers, new Comparator()
      {
        public int compare(Integer paramInteger1, Integer paramInteger2)
        {
          paramInteger2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInteger2.intValue())).user_id));
          paramInteger1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInteger1.intValue())).user_id));
          int i;
          if ((paramInteger2 != null) && (paramInteger2.status != null))
            if (paramInteger2.id == UserConfig.getClientUserId())
              i = ConnectionsManager.getInstance().getCurrentTime() + 50000;
          while (true)
          {
            int j;
            if ((paramInteger1 != null) && (paramInteger1.status != null))
              if (paramInteger1.id == UserConfig.getClientUserId())
                j = ConnectionsManager.getInstance().getCurrentTime() + 50000;
            while (true)
            {
              if ((i > 0) && (j > 0))
              {
                if (i > j)
                {
                  return 1;
                  i = paramInteger2.status.expires;
                  break;
                  j = paramInteger1.status.expires;
                  continue;
                }
                if (i < j)
                  return -1;
                return 0;
              }
              if ((i < 0) && (j < 0))
              {
                if (i > j)
                  return 1;
                if (i < j)
                  return -1;
                return 0;
              }
              if (((i < 0) && (j > 0)) || ((i == 0) && (j != 0)))
                return -1;
              if (((j < 0) && (i > 0)) || ((j == 0) && (i != 0)))
                return 1;
              return 0;
              j = 0;
            }
            i = 0;
          }
        }
      });
      if (this.listAdapter != null)
        this.listAdapter.notifyItemRangeChanged(this.emptyRowChat2 + 1, this.sortedUsers.size());
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private void updateProfileData()
  {
    String str = null;
    boolean bool = true;
    if ((this.avatarImage == null) || (this.nameTextView == null))
      return;
    TLRPC.User localUser;
    Object localObject1;
    Object localObject3;
    if (this.user_id != 0)
    {
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (localUser.photo == null)
        break label1504;
      localObject1 = localUser.photo.photo_small;
      localObject3 = localUser.photo.photo_big;
    }
    while (true)
    {
      this.avatarDrawable.setInfo(localUser);
      this.avatarImage.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      str = UserObject.getUserName(localUser);
      if (localUser.id == UserConfig.getClientUserId())
      {
        localObject1 = LocaleController.getString("ChatYourSelf", 2131165539);
        str = LocaleController.getString("ChatYourSelfName", 2131165544);
      }
      int i;
      label151: Object localObject2;
      label388: Object localObject4;
      label428: long l;
      while (true)
      {
        i = 0;
        while (true)
        {
          if (i >= 2)
            break label568;
          if (this.nameTextView[i] == null)
          {
            i += 1;
            continue;
            if ((localUser.id == 333000) || (localUser.id == 777000))
            {
              localObject1 = LocaleController.getString("ServiceNotifications", 2131166434);
              break;
            }
            if (localUser.bot)
            {
              localObject1 = LocaleController.getString("Bot", 2131165390);
              break;
            }
            localObject1 = LocaleController.formatUserStatus(localUser);
            break;
          }
        }
        if ((i != 0) || (localUser.id == UserConfig.getClientUserId()) || (localUser.id / 1000 == 777) || (localUser.id / 1000 == 333) || (localUser.phone == null) || (localUser.phone.length() == 0) || (ContactsController.getInstance().contactsDict.get(localUser.id) != null) || ((ContactsController.getInstance().contactsDict.size() == 0) && (ContactsController.getInstance().isLoadingContacts())))
          break;
        localObject2 = org.vidogram.a.b.a().e("+" + localUser.phone);
        if (!this.nameTextView[i].getText().equals(localObject2))
          this.nameTextView[i].setText((CharSequence)localObject2);
        if (!this.onlineTextView[i].getText().equals(localObject1))
          this.onlineTextView[i].setText((CharSequence)localObject1);
        if (this.currentEncryptedChat != null)
        {
          localObject4 = Theme.chat_lockIconDrawable;
          if (i != 0)
            break label542;
          localObject2 = MessagesController.getInstance();
          if (this.dialog_id == 0L)
            break label527;
          l = this.dialog_id;
          label451: if (!((MessagesController)localObject2).isDialogMuted(l))
            break label536;
          localObject2 = Theme.chat_muteIconDrawable;
        }
      }
      label527: label1043: 
      while (true)
      {
        this.nameTextView[i].setLeftDrawable((Drawable)localObject4);
        this.nameTextView[i].setRightDrawable((Drawable)localObject2);
        break label151;
        if (this.nameTextView[i].getText().equals(str))
          break label388;
        this.nameTextView[i].setText(str);
        break label388;
        localObject4 = null;
        break label428;
        l = this.user_id;
        break label451;
        label536: localObject2 = null;
        continue;
        label542: if (localUser.verified)
        {
          localObject2 = new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable);
          continue;
          label568: localObject1 = this.avatarImage.getImageReceiver();
          if (!PhotoViewer.getInstance().isShowingImage((TLRPC.FileLocation)localObject3));
          for (bool = true; ; bool = false)
          {
            ((ImageReceiver)localObject1).setVisible(bool, false);
            return;
          }
          if (this.chat_id == 0)
            break;
          localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
          if (localObject2 != null)
          {
            this.currentChat = ((TLRPC.Chat)localObject2);
            if (!ChatObject.isChannel((TLRPC.Chat)localObject2))
              break label954;
            if ((this.info != null) && ((this.currentChat.megagroup) || ((this.info.participants_count != 0) && (!this.currentChat.admin) && (!this.info.can_view_participants))))
              break label792;
            if (!this.currentChat.megagroup)
              break label747;
            localObject1 = LocaleController.getString("Loading", 2131165920).toLowerCase();
            label715: i = 0;
            label717: if (i >= 2)
              break label1402;
            if (this.nameTextView[i] != null)
              break label1043;
          }
          while (true)
          {
            i += 1;
            break label717;
            localObject2 = this.currentChat;
            break;
            label747: if ((((TLRPC.Chat)localObject2).flags & 0x40) != 0)
            {
              localObject1 = LocaleController.getString("ChannelPublic", 2131165509).toLowerCase();
              break label715;
            }
            localObject1 = LocaleController.getString("ChannelPrivate", 2131165506).toLowerCase();
            break label715;
            if ((this.currentChat.megagroup) && (this.info.participants_count <= 200))
            {
              if ((this.onlineCount > 1) && (this.info.participants_count != 0))
              {
                localObject1 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", this.info.participants_count), LocaleController.formatPluralString("Online", this.onlineCount) });
                break label715;
              }
              localObject1 = LocaleController.formatPluralString("Members", this.info.participants_count);
              break label715;
            }
            localObject1 = new int[1];
            localObject3 = LocaleController.formatShortNumber(this.info.participants_count, localObject1);
            localObject1 = LocaleController.formatPluralString("Members", localObject1[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject1[0]) }), (CharSequence)localObject3);
            break label715;
            label954: i = ((TLRPC.Chat)localObject2).participants_count;
            if (this.info != null)
              i = this.info.participants.participants.size();
            if ((i != 0) && (this.onlineCount > 1))
            {
              localObject1 = String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("Online", this.onlineCount) });
              break label715;
            }
            localObject1 = LocaleController.formatPluralString("Members", i);
            break label715;
            if ((((TLRPC.Chat)localObject2).title != null) && (!this.nameTextView[i].getText().equals(((TLRPC.Chat)localObject2).title)))
              this.nameTextView[i].setText(((TLRPC.Chat)localObject2).title);
            this.nameTextView[i].setLeftDrawable(null);
            if (i != 0)
            {
              if (((TLRPC.Chat)localObject2).verified)
                this.nameTextView[i].setRightDrawable(new CombinedDrawable(Theme.profile_verifiedDrawable, Theme.profile_verifiedCheckDrawable));
              while (true)
              {
                if ((!this.currentChat.megagroup) || (this.info == null) || (this.info.participants_count > 200) || (this.onlineCount <= 0))
                  break label1254;
                if (this.onlineTextView[i].getText().equals(localObject1))
                  break;
                this.onlineTextView[i].setText((CharSequence)localObject1);
                break;
                this.nameTextView[i].setRightDrawable(null);
              }
            }
            localObject4 = this.nameTextView[i];
            if (MessagesController.getInstance().isDialogMuted(-this.chat_id));
            for (localObject3 = Theme.chat_muteIconDrawable; ; localObject3 = null)
            {
              ((SimpleTextView)localObject4).setRightDrawable((Drawable)localObject3);
              break;
            }
            label1254: if ((i == 0) && (ChatObject.isChannel(this.currentChat)) && (this.info != null) && (this.info.participants_count != 0) && ((this.currentChat.megagroup) || (this.currentChat.broadcast)))
            {
              localObject3 = new int[1];
              localObject4 = LocaleController.formatShortNumber(this.info.participants_count, localObject3);
              this.onlineTextView[i].setText(LocaleController.formatPluralString("Members", localObject3[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject3[0]) }), (CharSequence)localObject4));
              continue;
            }
            if (this.onlineTextView[i].getText().equals(localObject1))
              continue;
            this.onlineTextView[i].setText((CharSequence)localObject1);
          }
          label1402: if (((TLRPC.Chat)localObject2).photo != null)
            localObject3 = ((TLRPC.Chat)localObject2).photo.photo_small;
          for (localObject1 = ((TLRPC.Chat)localObject2).photo.photo_big; ; localObject1 = str)
          {
            this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
            this.avatarImage.setImage((TLObject)localObject3, "50_50", this.avatarDrawable);
            localObject2 = this.avatarImage.getImageReceiver();
            if (!PhotoViewer.getInstance().isShowingImage((TLRPC.FileLocation)localObject1));
            while (true)
            {
              ((ImageReceiver)localObject2).setVisible(bool, false);
              return;
              bool = false;
            }
            localObject3 = null;
          }
        }
        label792: localObject2 = null;
      }
      label1504: localObject3 = null;
      localObject1 = null;
    }
  }

  private void updateRowsIds()
  {
    this.emptyRow = -1;
    this.phoneRow = -1;
    this.userInfoRow = -1;
    this.userSectionRow = -1;
    this.sectionRow = -1;
    this.sharedMediaRow = -1;
    this.callHistoryEmptyRow = -1;
    this.callHistoryShadowRow = -1;
    this.callHistoryEndRow = -1;
    this.callHistorySeeMoreEmptyRow = -1;
    this.callHistorySeeMoreRow = -1;
    this.settingsNotificationsRow = -1;
    this.usernameRow = -1;
    this.settingsTimerRow = -1;
    this.settingsKeyRow = -1;
    this.startSecretChatRow = -1;
    this.membersEndRow = -1;
    this.emptyRowChat2 = -1;
    this.addMemberRow = -1;
    this.channelInfoRow = -1;
    this.channelNameRow = -1;
    this.convertRow = -1;
    this.convertHelpRow = -1;
    this.emptyRowChat = -1;
    this.membersSectionRow = -1;
    this.membersRow = -1;
    this.managementRow = -1;
    this.leaveChannelRow = -1;
    this.loadMoreMembersRow = -1;
    this.groupsInCommonRow = -1;
    this.blockedUsersRow = -1;
    this.rowCount = 0;
    String str;
    if (this.user_id != 0)
    {
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.emptyRow = i;
      if ((localUser == null) || (!localUser.bot))
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.phoneRow = i;
      }
      if ((localUser != null) && (localUser.username != null) && (localUser.username.length() > 0))
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.usernameRow = i;
      }
      TLRPC.TL_userFull localTL_userFull = MessagesController.getInstance().getUserFull(localUser.id);
      if (localTL_userFull != null)
      {
        str = localTL_userFull.about;
        if (str == null)
          break label620;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.userSectionRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.userInfoRow = i;
        label325: i = this.rowCount;
        this.rowCount = (i + 1);
        this.sectionRow = i;
        if (this.user_id != UserConfig.getClientUserId())
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsNotificationsRow = i;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.sharedMediaRow = i;
        if ((this.currentEncryptedChat instanceof TLRPC.TL_encryptedChat))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsTimerRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.settingsKeyRow = i;
        }
        if ((localTL_userFull != null) && (localTL_userFull.common_chats_count != 0))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.groupsInCommonRow = i;
        }
        if ((localUser != null) && (!localUser.bot) && (this.currentEncryptedChat == null) && (localUser.id != UserConfig.getClientUserId()))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.startSecretChatRow = i;
        }
        if ((this.history != null) && (this.history.size() > 0))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.callHistoryShadowRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.callHistoryEmptyRow = i;
          this.rowCount += this.history.size();
          this.callHistoryEndRow = this.rowCount;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.callHistorySeeMoreEmptyRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.callHistorySeeMoreRow = i;
        }
      }
    }
    label620: 
    do
      while (true)
      {
        return;
        str = null;
        break;
        this.userSectionRow = -1;
        this.userInfoRow = -1;
        break label325;
        if (this.chat_id == 0)
          continue;
        if (this.chat_id <= 0)
          break label1597;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.emptyRow = i;
        if ((ChatObject.isChannel(this.currentChat)) && (((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0)) || ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))))
        {
          if ((this.info != null) && (this.info.about != null) && (this.info.about.length() > 0))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.channelInfoRow = i;
          }
          if ((this.currentChat.username != null) && (this.currentChat.username.length() > 0))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.channelNameRow = i;
          }
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.sectionRow = i;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.settingsNotificationsRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.sharedMediaRow = i;
        if (ChatObject.isChannel(this.currentChat))
        {
          if ((!this.currentChat.megagroup) && (this.info != null) && ((this.currentChat.creator) || (this.info.can_view_participants)))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.membersRow = i;
          }
          if ((!ChatObject.isNotInChat(this.currentChat)) && (!this.currentChat.megagroup) && ((this.currentChat.creator) || (this.currentChat.editor) || (this.currentChat.moderator)))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.managementRow = i;
          }
          if ((!ChatObject.isNotInChat(this.currentChat)) && (this.currentChat.megagroup) && ((this.currentChat.editor) || (this.currentChat.creator)))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.blockedUsersRow = i;
          }
          if ((!this.currentChat.creator) && (!this.currentChat.left) && (!this.currentChat.kicked) && (!this.currentChat.megagroup))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.leaveChannelRow = i;
          }
          if ((this.currentChat.megagroup) && ((this.currentChat.editor) || (this.currentChat.creator) || (this.currentChat.democracy)) && ((this.info == null) || (this.info.participants_count < MessagesController.getInstance().maxMegagroupCount)))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.addMemberRow = i;
          }
          if ((this.info == null) || (this.info.participants == null) || (this.info.participants.participants.isEmpty()))
            continue;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.emptyRowChat = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.membersSectionRow = i;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.emptyRowChat2 = i;
          this.rowCount += this.info.participants.participants.size();
          this.membersEndRow = this.rowCount;
          if (this.usersEndReached)
            continue;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.loadMoreMembersRow = i;
          return;
        }
        if (this.info != null)
        {
          if ((!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)) && (this.info.participants.participants.size() < MessagesController.getInstance().maxGroupCount) && ((this.currentChat.admin) || (this.currentChat.creator) || (!this.currentChat.admins_enabled)))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.addMemberRow = i;
          }
          if ((this.currentChat.creator) && (this.info.participants.participants.size() >= MessagesController.getInstance().minGroupConvertSize))
          {
            i = this.rowCount;
            this.rowCount = (i + 1);
            this.convertRow = i;
          }
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.emptyRowChat = i;
        if (this.convertRow != -1)
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.convertHelpRow = i;
        }
        while ((this.info != null) && (!(this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.emptyRowChat2 = i;
          this.rowCount += this.info.participants.participants.size();
          this.membersEndRow = this.rowCount;
          return;
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.membersSectionRow = i;
        }
      }
    while ((ChatObject.isChannel(this.currentChat)) || (this.info == null) || ((this.info.participants instanceof TLRPC.TL_chatParticipantsForbidden)));
    label1597: int i = this.rowCount;
    this.rowCount = (i + 1);
    this.addMemberRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emptyRowChat2 = i;
    this.rowCount += this.info.participants.participants.size();
    this.membersEndRow = this.rowCount;
  }

  public boolean allowCaption()
  {
    return true;
  }

  public boolean cancelButtonPressed()
  {
    return true;
  }

  protected ActionBar createActionBar(Context paramContext)
  {
    paramContext = new ActionBar(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        return super.onTouchEvent(paramMotionEvent);
      }
    };
    int i;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      paramContext.setItemsBackgroundColor(AvatarDrawable.getButtonColorForId(i), false);
      paramContext.setItemsColor(Theme.getColor("actionBarDefaultIcon"), false);
      paramContext.setItemsColor(Theme.getColor("actionBarActionModeDefaultIcon"), true);
      paramContext.setBackButtonDrawable(new BackDrawable(false));
      paramContext.setCastShadows(false);
      paramContext.setAddToContainer(false);
      if ((Build.VERSION.SDK_INT < 21) || (AndroidUtilities.isTablet()))
        break label123;
    }
    label123: for (boolean bool = true; ; bool = false)
    {
      paramContext.setOccupyStatusBar(bool);
      return paramContext;
      i = this.chat_id;
      break;
    }
  }

  public View createView(Context paramContext)
  {
    Theme.createProfileResources(paramContext);
    this.hasOwnBackground = true;
    this.extraHeight = AndroidUtilities.dp(88.0F);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (ProfileActivity.this.getParentActivity() == null);
        TLRPC.User localUser;
        do
        {
          do
          {
            Object localObject1;
            Object localObject3;
            while (true)
            {
              return;
              if (paramInt == -1)
              {
                ProfileActivity.this.finishFragment();
                return;
              }
              if (paramInt == 2)
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if (localObject1 == null)
                  continue;
                if (!((TLRPC.User)localObject1).bot)
                {
                  localObject1 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                  if (!ProfileActivity.this.userBlocked)
                    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureBlockContact", 2131165336));
                  while (true)
                  {
                    ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165319));
                    ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramDialogInterface, int paramInt)
                      {
                        if (!ProfileActivity.this.userBlocked)
                        {
                          MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                          return;
                        }
                        MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                      }
                    });
                    ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                    ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                    return;
                    ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AreYouSureUnblockContact", 2131165353));
                  }
                }
                if (!ProfileActivity.this.userBlocked)
                {
                  MessagesController.getInstance().blockUser(ProfileActivity.this.user_id);
                  return;
                }
                MessagesController.getInstance().unblockUser(ProfileActivity.this.user_id);
                SendMessagesHelper.getInstance().sendMessage("/start", ProfileActivity.this.user_id, null, null, false, null, null, null);
                ProfileActivity.this.finishFragment();
                return;
              }
              if (paramInt == 1)
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                localObject3 = new Bundle();
                ((Bundle)localObject3).putInt("user_id", ((TLRPC.User)localObject1).id);
                ((Bundle)localObject3).putBoolean("addContact", true);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject3));
                return;
              }
              if (paramInt == 3)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putBoolean("onlySelect", true);
                ((Bundle)localObject1).putInt("dialogsType", 1);
                ((Bundle)localObject1).putString("selectAlertString", LocaleController.getString("SendContactTo", 2131166411));
                ((Bundle)localObject1).putString("selectAlertStringGroup", LocaleController.getString("SendContactToGroup", 2131166412));
                localObject1 = new DialogsActivity((Bundle)localObject1);
                ((DialogsActivity)localObject1).setDelegate(ProfileActivity.this);
                ProfileActivity.this.presentFragment((BaseFragment)localObject1);
                return;
              }
              if (paramInt == 4)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("user_id", ProfileActivity.this.user_id);
                ProfileActivity.this.presentFragment(new ContactAddActivity((Bundle)localObject1));
                return;
              }
              if (paramInt == 5)
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if ((localObject1 == null) || (ProfileActivity.this.getParentActivity() == null))
                  continue;
                localObject3 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject3).setMessage(LocaleController.getString("AreYouSureDeleteContact", 2131165341));
                ((AlertDialog.Builder)localObject3).setTitle(LocaleController.getString("AppName", 2131165319));
                ((AlertDialog.Builder)localObject3).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener((TLRPC.User)localObject1)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    paramDialogInterface = new ArrayList();
                    paramDialogInterface.add(this.val$user);
                    ContactsController.getInstance().deleteContact(paramDialogInterface);
                  }
                });
                ((AlertDialog.Builder)localObject3).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject3).create());
                return;
              }
              if (paramInt == 7)
              {
                ProfileActivity.this.leaveChatPressed();
                return;
              }
              if (paramInt == 8)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
                ProfileActivity.this.presentFragment(new ChangeChatNameActivity((Bundle)localObject1));
                return;
              }
              if (paramInt == 12)
              {
                localObject1 = new Bundle();
                ((Bundle)localObject1).putInt("chat_id", ProfileActivity.this.chat_id);
                localObject1 = new ChannelEditActivity((Bundle)localObject1);
                ((ChannelEditActivity)localObject1).setInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment((BaseFragment)localObject1);
                return;
              }
              if (paramInt != 9)
                break;
              localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
              if (localObject1 == null)
                continue;
              localObject3 = new Bundle();
              ((Bundle)localObject3).putBoolean("onlySelect", true);
              ((Bundle)localObject3).putInt("dialogsType", 2);
              ((Bundle)localObject3).putString("addToGroupAlertString", LocaleController.formatString("AddToTheGroupTitle", 2131165293, new Object[] { UserObject.getUserName((TLRPC.User)localObject1), "%1$s" }));
              localObject3 = new DialogsActivity((Bundle)localObject3);
              ((DialogsActivity)localObject3).setDelegate(new DialogsActivity.DialogsActivityDelegate((TLRPC.User)localObject1)
              {
                public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
                {
                  Bundle localBundle = new Bundle();
                  localBundle.putBoolean("scrollToTopOnResume", true);
                  localBundle.putInt("chat_id", -(int)paramLong);
                  if (!MessagesController.checkCanOpenChat(localBundle, paramDialogsActivity))
                    return;
                  NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  MessagesController.getInstance().addUserToChat(-(int)paramLong, this.val$user, null, 0, null, ProfileActivity.this);
                  ProfileActivity.this.presentFragment(new ChatActivity(localBundle), true);
                  ProfileActivity.this.removeSelfFromStack();
                }
              });
              ProfileActivity.this.presentFragment((BaseFragment)localObject3);
              return;
            }
            if (paramInt == 10)
              while (true)
              {
                try
                {
                  localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                  if (localObject1 == null)
                    break;
                  localObject3 = new Intent("android.intent.action.SEND");
                  ((Intent)localObject3).setType("text/plain");
                  TLRPC.TL_userFull localTL_userFull = MessagesController.getInstance().getUserFull(ProfileActivity.this.botInfo.user_id);
                  if ((ProfileActivity.this.botInfo != null) && (localTL_userFull != null) && (!TextUtils.isEmpty(localTL_userFull.about)))
                  {
                    ((Intent)localObject3).putExtra("android.intent.extra.TEXT", String.format("%s https://" + MessagesController.getInstance().linkPrefix + "/%s", new Object[] { localTL_userFull.about, ((TLRPC.User)localObject1).username }));
                    ProfileActivity.this.startActivityForResult(Intent.createChooser((Intent)localObject3, LocaleController.getString("BotShare", 2131165398)), 500);
                    return;
                  }
                }
                catch (Exception localException1)
                {
                  FileLog.e(localException1);
                  return;
                }
                ((Intent)localObject3).putExtra("android.intent.extra.TEXT", String.format("https://" + MessagesController.getInstance().linkPrefix + "/%s", new Object[] { localException1.username }));
              }
            Object localObject2;
            if (paramInt == 11)
            {
              localObject2 = new Bundle();
              ((Bundle)localObject2).putInt("chat_id", ProfileActivity.this.chat_id);
              localObject2 = new SetAdminsActivity((Bundle)localObject2);
              ((SetAdminsActivity)localObject2).setChatInfo(ProfileActivity.this.info);
              ProfileActivity.this.presentFragment((BaseFragment)localObject2);
              return;
            }
            if (paramInt == 13)
            {
              localObject2 = new Bundle();
              ((Bundle)localObject2).putInt("chat_id", ProfileActivity.this.chat_id);
              ProfileActivity.this.presentFragment(new ConvertGroupActivity((Bundle)localObject2));
              return;
            }
            if (paramInt != 14)
              continue;
            while (true)
            {
              try
              {
                if (ProfileActivity.this.currentEncryptedChat != null)
                {
                  l = ProfileActivity.this.currentEncryptedChat.id << 32;
                  AndroidUtilities.installShortcut(l);
                  return;
                }
              }
              catch (Exception localException2)
              {
                FileLog.e(localException2);
                return;
              }
              if (ProfileActivity.this.user_id != 0)
              {
                l = ProfileActivity.this.user_id;
                continue;
              }
              if (ProfileActivity.this.chat_id == 0)
                break;
              paramInt = ProfileActivity.this.chat_id;
              long l = -paramInt;
            }
          }
          while (paramInt != 15);
          localUser = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
        }
        while (localUser == null);
        VoIPHelper.startCall(localUser, ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUserFull(localUser.id));
      }
    });
    createActionBarMenu();
    this.listAdapter = new ListAdapter(paramContext);
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setProfile(true);
    this.fragmentView = new FrameLayout(paramContext)
    {
      public boolean hasOverlappingRendering()
      {
        return false;
      }

      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        ProfileActivity.this.checkListViewScroll();
      }
    };
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext)
    {
      public boolean hasOverlappingRendering()
      {
        return false;
      }
    };
    this.listView.setTag(Integer.valueOf(6));
    this.listView.setPadding(0, AndroidUtilities.dp(88.0F), 0, 0);
    this.listView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setClipToPadding(false);
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    Object localObject1 = this.listView;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      ((RecyclerListView)localObject1).setGlowColor(AvatarDrawable.getProfileBackColorForId(i));
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (ProfileActivity.this.getParentActivity() == null);
          while (true)
          {
            return;
            long l;
            if (paramInt == ProfileActivity.this.callHistorySeeMoreRow)
            {
              paramView = new Bundle();
              paramView.putBoolean("CallHistory", true);
              if (ProfileActivity.this.user_id != 0)
                if (ProfileActivity.this.dialog_id != 0L)
                {
                  l = ProfileActivity.this.dialog_id;
                  paramView.putLong("dialog_id", l);
                }
              while (true)
              {
                paramView = new MediaActivity(paramView);
                paramView.setChatInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment(paramView);
                return;
                l = ProfileActivity.this.user_id;
                break;
                paramView.putLong("dialog_id", -ProfileActivity.this.chat_id);
              }
            }
            if (paramInt == ProfileActivity.this.sharedMediaRow)
            {
              paramView = new Bundle();
              paramView.putBoolean("CallHistory", false);
              if (ProfileActivity.this.user_id != 0)
                if (ProfileActivity.this.dialog_id != 0L)
                {
                  l = ProfileActivity.this.dialog_id;
                  paramView.putLong("dialog_id", l);
                }
              while (true)
              {
                paramView = new MediaActivity(paramView);
                paramView.setChatInfo(ProfileActivity.this.info);
                ProfileActivity.this.presentFragment(paramView);
                return;
                l = ProfileActivity.this.user_id;
                break;
                paramView.putLong("dialog_id", -ProfileActivity.this.chat_id);
              }
            }
            if (paramInt == ProfileActivity.this.groupsInCommonRow)
            {
              ProfileActivity.this.presentFragment(new CommonGroupsActivity(ProfileActivity.this.user_id));
              return;
            }
            if (paramInt == ProfileActivity.this.settingsKeyRow)
            {
              paramView = new Bundle();
              paramView.putInt("chat_id", (int)(ProfileActivity.this.dialog_id >> 32));
              ProfileActivity.this.presentFragment(new IdenticonActivity(paramView));
              return;
            }
            if (paramInt == ProfileActivity.this.settingsTimerRow)
            {
              ProfileActivity.this.showDialog(AlertsCreator.createTTLAlert(ProfileActivity.this.getParentActivity(), ProfileActivity.this.currentEncryptedChat).create());
              return;
            }
            if (paramInt == ProfileActivity.this.settingsNotificationsRow)
            {
              if (ProfileActivity.this.dialog_id != 0L)
                l = ProfileActivity.this.dialog_id;
              while (true)
              {
                localObject = new String[5];
                localObject[0] = LocaleController.getString("NotificationsTurnOn", 2131166151);
                localObject[1] = LocaleController.formatString("MuteFor", 2131165997, new Object[] { LocaleController.formatPluralString("Hours", 1) });
                localObject[2] = LocaleController.formatString("MuteFor", 2131165997, new Object[] { LocaleController.formatPluralString("Days", 2) });
                localObject[3] = LocaleController.getString("NotificationsCustomize", 2131166131);
                localObject[4] = LocaleController.getString("NotificationsTurnOff", 2131166150);
                paramView = new LinearLayout(ProfileActivity.this.getParentActivity());
                paramView.setOrientation(1);
                paramInt = 0;
                while (paramInt < localObject.length)
                {
                  TextView localTextView = new TextView(ProfileActivity.this.getParentActivity());
                  localTextView.setTextColor(Theme.getColor("dialogTextBlack"));
                  localTextView.setTextSize(1, 16.0F);
                  localTextView.setLines(1);
                  localTextView.setMaxLines(1);
                  Drawable localDrawable = ProfileActivity.this.getParentActivity().getResources().getDrawable(new int[] { 2130837979, 2130837975, 2130837976, 2130837977, 2130837978 }[paramInt]);
                  localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("dialogIcon"), PorterDuff.Mode.MULTIPLY));
                  localTextView.setCompoundDrawablesWithIntrinsicBounds(localDrawable, null, null, null);
                  localTextView.setTag(Integer.valueOf(paramInt));
                  localTextView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                  localTextView.setPadding(AndroidUtilities.dp(24.0F), 0, AndroidUtilities.dp(24.0F), 0);
                  localTextView.setSingleLine(true);
                  localTextView.setGravity(19);
                  localTextView.setCompoundDrawablePadding(AndroidUtilities.dp(26.0F));
                  localTextView.setText(localObject[paramInt]);
                  paramView.addView(localTextView, LayoutHelper.createLinear(-1, 48, 51));
                  localTextView.setOnClickListener(new View.OnClickListener(l)
                  {
                    public void onClick(View paramView)
                    {
                      long l = 1L;
                      int j = ((Integer)paramView.getTag()).intValue();
                      if (j == 0)
                      {
                        paramView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                        paramView.putInt("notify2_" + this.val$did, 0);
                        MessagesStorage.getInstance().setDialogFlags(this.val$did, 0L);
                        paramView.commit();
                        paramView = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$did));
                        if (paramView != null)
                          paramView.notify_settings = new TLRPC.TL_peerNotifySettings();
                        NotificationsController.updateServerNotificationsSettings(this.val$did);
                      }
                      while (true)
                      {
                        ProfileActivity.this.listAdapter.notifyItemChanged(ProfileActivity.this.settingsNotificationsRow);
                        ProfileActivity.this.dismissCurrentDialig();
                        return;
                        if (j != 3)
                          break;
                        paramView = new Bundle();
                        paramView.putLong("dialog_id", this.val$did);
                        ProfileActivity.this.presentFragment(new ProfileNotificationsActivity(paramView));
                      }
                      int i = ConnectionsManager.getInstance().getCurrentTime();
                      if (j == 1)
                        i += 3600;
                      label448: 
                      while (true)
                      {
                        label220: paramView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
                        if (j == 4)
                          paramView.putInt("notify2_" + this.val$did, 2);
                        while (true)
                        {
                          NotificationsController.getInstance().removeNotificationsForDialog(this.val$did);
                          MessagesStorage.getInstance().setDialogFlags(this.val$did, l);
                          paramView.commit();
                          paramView = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$did));
                          if (paramView != null)
                          {
                            paramView.notify_settings = new TLRPC.TL_peerNotifySettings();
                            paramView.notify_settings.mute_until = i;
                          }
                          NotificationsController.updateServerNotificationsSettings(this.val$did);
                          break;
                          if (j == 2)
                          {
                            i += 172800;
                            break label220;
                          }
                          if (j != 4)
                            break label448;
                          i = 2147483647;
                          break label220;
                          paramView.putInt("notify2_" + this.val$did, 3);
                          paramView.putInt("notifyuntil_" + this.val$did, i);
                          l = 1L | i << 32;
                        }
                      }
                    }
                  });
                  paramInt += 1;
                }
                if (ProfileActivity.this.user_id != 0)
                {
                  l = ProfileActivity.this.user_id;
                  continue;
                }
                l = -ProfileActivity.this.chat_id;
              }
              Object localObject = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("Notifications", 2131166128));
              ((AlertDialog.Builder)localObject).setView(paramView);
              ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              return;
            }
            if (paramInt == ProfileActivity.this.startSecretChatRow)
            {
              paramView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              paramView.setMessage(LocaleController.getString("AreYouSureSecretChat", 2131165348));
              paramView.setTitle(LocaleController.getString("AppName", 2131165319));
              paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  ProfileActivity.access$2202(ProfileActivity.this, true);
                  SecretChatHelper.getInstance().startSecretChat(ProfileActivity.this.getParentActivity(), MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id)));
                }
              });
              paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              ProfileActivity.this.showDialog(paramView.create());
              return;
            }
            if ((paramInt <= ProfileActivity.this.emptyRowChat2) || (paramInt >= ProfileActivity.this.membersEndRow))
              break;
            if (!ProfileActivity.this.sortedUsers.isEmpty());
            for (paramInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue())).user_id; paramInt != UserConfig.getClientUserId(); paramInt = ((TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).user_id)
            {
              paramView = new Bundle();
              paramView.putInt("user_id", paramInt);
              ProfileActivity.this.presentFragment(new ProfileActivity(paramView));
              return;
            }
          }
          if (paramInt == ProfileActivity.this.addMemberRow)
          {
            ProfileActivity.this.openAddMember();
            return;
          }
          if (paramInt == ProfileActivity.this.channelNameRow)
            while (true)
            {
              try
              {
                paramView = new Intent("android.intent.action.SEND");
                paramView.setType("text/plain");
                if ((ProfileActivity.this.info.about != null) && (ProfileActivity.this.info.about.length() > 0))
                {
                  paramView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\n" + ProfileActivity.this.info.about + "\nhttps://" + MessagesController.getInstance().linkPrefix + "/" + ProfileActivity.this.currentChat.username);
                  ProfileActivity.this.getParentActivity().startActivityForResult(Intent.createChooser(paramView, LocaleController.getString("BotShare", 2131165398)), 500);
                  return;
                }
              }
              catch (Exception paramView)
              {
                FileLog.e(paramView);
                return;
              }
              paramView.putExtra("android.intent.extra.TEXT", ProfileActivity.this.currentChat.title + "\nhttps://" + MessagesController.getInstance().linkPrefix + "/" + ProfileActivity.this.currentChat.username);
            }
          if (paramInt == ProfileActivity.this.leaveChannelRow)
          {
            ProfileActivity.this.leaveChatPressed();
            return;
          }
          if ((paramInt == ProfileActivity.this.membersRow) || (paramInt == ProfileActivity.this.blockedUsersRow) || (paramInt == ProfileActivity.this.managementRow))
          {
            paramView = new Bundle();
            paramView.putInt("chat_id", ProfileActivity.this.chat_id);
            if (paramInt == ProfileActivity.this.blockedUsersRow)
              paramView.putInt("type", 0);
            while (true)
            {
              ProfileActivity.this.presentFragment(new ChannelUsersActivity(paramView));
              return;
              if (paramInt == ProfileActivity.this.managementRow)
              {
                paramView.putInt("type", 1);
                continue;
              }
              if (paramInt != ProfileActivity.this.membersRow)
                continue;
              paramView.putInt("type", 2);
            }
          }
          if (paramInt == ProfileActivity.this.convertRow)
          {
            paramView = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
            paramView.setMessage(LocaleController.getString("ConvertGroupAlert", 2131165577));
            paramView.setTitle(LocaleController.getString("ConvertGroupAlertWarning", 2131165578));
            paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                MessagesController.getInstance().convertToMegaGroup(ProfileActivity.this.getParentActivity(), ProfileActivity.this.chat_id);
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            ProfileActivity.this.showDialog(paramView.create());
            return;
          }
          ProfileActivity.this.processOnClickOrPress(paramInt);
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          Object localObject1;
          int i;
          if ((paramInt > ProfileActivity.this.emptyRowChat2) && (paramInt < ProfileActivity.this.membersEndRow))
          {
            if (ProfileActivity.this.getParentActivity() == null)
              return false;
            if (!ProfileActivity.this.sortedUsers.isEmpty())
            {
              paramView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());
              ProfileActivity.access$3502(ProfileActivity.this, paramView.user_id);
              if (!ChatObject.isChannel(ProfileActivity.this.currentChat))
                break label376;
              localObject1 = ((TLRPC.TL_chatChannelParticipant)paramView).channelParticipant;
              if (paramView.user_id == UserConfig.getClientUserId())
                break label530;
              if (!ProfileActivity.this.currentChat.creator)
                break label334;
              i = 1;
            }
          }
          while (true)
          {
            label153: Object localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(paramView.user_id));
            if (((localObject1 instanceof TLRPC.TL_channelParticipant)) && (!((TLRPC.User)localObject2).bot))
              paramInt = 1;
            while (true)
            {
              if (i == 0)
                break label528;
              localObject1 = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              if ((ProfileActivity.this.currentChat.megagroup) && (ProfileActivity.this.currentChat.creator) && (paramInt != 0))
              {
                localObject2 = LocaleController.getString("SetAsAdmin", 2131166445);
                String str = LocaleController.getString("KickFromGroup", 2131165869);
                paramView = new DialogInterface.OnClickListener(paramView)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    if (paramInt == 0)
                    {
                      paramDialogInterface = (TLRPC.TL_chatChannelParticipant)this.val$user;
                      paramDialogInterface.channelParticipant = new TLRPC.TL_channelParticipantEditor();
                      paramDialogInterface.channelParticipant.inviter_id = UserConfig.getClientUserId();
                      paramDialogInterface.channelParticipant.user_id = this.val$user.user_id;
                      paramDialogInterface.channelParticipant.date = this.val$user.date;
                      paramDialogInterface = new TLRPC.TL_channels_editAdmin();
                      paramDialogInterface.channel = MessagesController.getInputChannel(ProfileActivity.this.chat_id);
                      paramDialogInterface.user_id = MessagesController.getInputUser(ProfileActivity.this.selectedUser);
                      paramDialogInterface.role = new TLRPC.TL_channelRoleEditor();
                      ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate(paramDialogInterface)
                      {
                        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                        {
                          if (paramTL_error == null)
                          {
                            MessagesController.getInstance().processUpdates((TLRPC.Updates)paramTLObject, false);
                            AndroidUtilities.runOnUIThread(new Runnable()
                            {
                              public void run()
                              {
                                MessagesController.getInstance().loadFullChat(ProfileActivity.this.chat_id, 0, true);
                              }
                            }
                            , 1000L);
                            return;
                          }
                          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                          {
                            public void run()
                            {
                              AlertsCreator.processError(this.val$error, ProfileActivity.this, ProfileActivity.9.1.1.this.val$req, new Object[] { Boolean.valueOf(false) });
                            }
                          });
                        }
                      });
                    }
                    do
                      return;
                    while (paramInt != 1);
                    ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                  }
                };
                ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { localObject2, str }, paramView);
                ProfileActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
                return true;
                paramView = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1);
                break;
                label334: if ((!(localObject1 instanceof TLRPC.TL_channelParticipant)) || ((!ProfileActivity.this.currentChat.editor) && (((TLRPC.ChannelParticipant)localObject1).inviter_id != UserConfig.getClientUserId())))
                  break label530;
                i = 1;
                break label153;
                paramInt = 0;
                continue;
                label376: if (paramView.user_id != UserConfig.getClientUserId())
                {
                  if (ProfileActivity.this.currentChat.creator)
                  {
                    paramInt = 0;
                    i = 1;
                    continue;
                  }
                  if (((paramView instanceof TLRPC.TL_chatParticipant)) && (((ProfileActivity.this.currentChat.admin) && (ProfileActivity.this.currentChat.admins_enabled)) || (paramView.inviter_id == UserConfig.getClientUserId())))
                  {
                    paramInt = 0;
                    i = 1;
                    continue;
                  }
                }
              }
              else
              {
                if (ProfileActivity.this.chat_id > 0);
                for (paramView = LocaleController.getString("KickFromGroup", 2131165869); ; paramView = LocaleController.getString("KickFromBroadcast", 2131165868))
                {
                  localObject2 = new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                      if (paramInt == 0)
                        ProfileActivity.this.kickUser(ProfileActivity.this.selectedUser);
                    }
                  };
                  ((AlertDialog.Builder)localObject1).setItems(new CharSequence[] { paramView }, (DialogInterface.OnClickListener)localObject2);
                  break;
                }
                return ProfileActivity.this.processOnClickOrPress(paramInt);
              }
              paramInt = 0;
              i = 0;
            }
            label528: break;
            label530: i = 0;
          }
        }
      });
      this.topView = new TopView(paramContext);
      localObject1 = this.topView;
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup)))
        break label500;
    }
    label500: for (int i = 5; ; i = this.chat_id)
    {
      ((TopView)localObject1).setBackgroundColor(AvatarDrawable.getProfileBackColorForId(i));
      localFrameLayout.addView(this.topView);
      localFrameLayout.addView(this.actionBar);
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
      this.avatarImage.setPivotX(0.0F);
      this.avatarImage.setPivotY(0.0F);
      localFrameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ProfileActivity.this.user_id != 0)
          {
            paramView = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
            if ((paramView.photo != null) && (paramView.photo.photo_big != null))
              PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
          }
          do
          {
            PhotoViewer.getInstance().openPhoto(paramView.photo.photo_big, ProfileActivity.this);
            do
              return;
            while (ProfileActivity.this.chat_id == 0);
            paramView = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
          }
          while ((paramView.photo == null) || (paramView.photo.photo_big == null));
          PhotoViewer.getInstance().setParentActivity(ProfileActivity.this.getParentActivity());
          PhotoViewer.getInstance().openPhoto(paramView.photo.photo_big, ProfileActivity.this);
        }
      });
      i = 0;
      while (true)
      {
        if (i >= 2)
          break label867;
        if ((this.playProfileAnimation) || (i != 0))
          break;
        i += 1;
      }
      i = this.chat_id;
      break;
    }
    this.nameTextView[i] = new SimpleTextView(paramContext);
    label542: float f;
    label628: int j;
    if (i == 1)
    {
      this.nameTextView[i].setTextColor(Theme.getColor("profile_title"));
      this.nameTextView[i].setTextSize(18);
      this.nameTextView[i].setGravity(3);
      this.nameTextView[i].setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView[i].setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
      this.nameTextView[i].setPivotX(0.0F);
      this.nameTextView[i].setPivotY(0.0F);
      localObject1 = this.nameTextView[i];
      if (i != 0)
        break label836;
      f = 0.0F;
      ((SimpleTextView)localObject1).setAlpha(f);
      localObject1 = this.nameTextView[i];
      if (i != 0)
        break label841;
      f = 48.0F;
      label650: localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      this.onlineTextView[i] = new SimpleTextView(paramContext);
      localObject1 = this.onlineTextView[i];
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup)))
        break label846;
      j = 5;
      label725: ((SimpleTextView)localObject1).setTextColor(AvatarDrawable.getProfileTextColorForId(j));
      this.onlineTextView[i].setTextSize(14);
      this.onlineTextView[i].setGravity(3);
      localObject1 = this.onlineTextView[i];
      if (i != 0)
        break label855;
      f = 0.0F;
      label770: ((SimpleTextView)localObject1).setAlpha(f);
      localObject1 = this.onlineTextView[i];
      if (i != 0)
        break label860;
      f = 48.0F;
    }
    while (true)
    {
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, f, 0.0F));
      break;
      this.nameTextView[i].setTextColor(Theme.getColor("actionBarDefaultTitle"));
      break label542;
      label836: f = 1.0F;
      break label628;
      label841: f = 0.0F;
      break label650;
      label846: j = this.chat_id;
      break label725;
      label855: f = 1.0F;
      break label770;
      label860: f = 8.0F;
    }
    label867: Object localObject2;
    if ((this.user_id != 0) && (this.user_id != UserConfig.getClientUserId()))
    {
      this.callButton = new ImageView(paramContext);
      localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
      if (Build.VERSION.SDK_INT >= 21)
        break label1867;
      localObject2 = paramContext.getResources().getDrawable(2130837718).mutate();
      ((Drawable)localObject2).setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      localObject1 = new CombinedDrawable((Drawable)localObject2, (Drawable)localObject1, 0, 0);
      ((CombinedDrawable)localObject1).setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    label1089: label1480: label1867: 
    while (true)
    {
      this.callButton.setBackgroundDrawable((Drawable)localObject1);
      this.callButton.setScaleType(ImageView.ScaleType.CENTER);
      this.callButton.setImageResource(2130837867);
      this.callButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
      this.callButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
      localObject1 = this.callButton;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 56;
        if (Build.VERSION.SDK_INT < 21)
          break label1713;
        f = 56.0F;
        localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(i, f, 53, 0.0F, 0.0F, 70.0F, 0.0F));
        if (Build.VERSION.SDK_INT >= 21)
        {
          localObject1 = new StateListAnimator();
          localObject2 = ObjectAnimator.ofFloat(this.callButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[] { 16842919 }, (Animator)localObject2);
          localObject2 = ObjectAnimator.ofFloat(this.callButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
          ((StateListAnimator)localObject1).addState(new int[0], (Animator)localObject2);
          this.callButton.setStateListAnimator((StateListAnimator)localObject1);
          this.callButton.setOutlineProvider(new ViewOutlineProvider()
          {
            @SuppressLint({"NewApi"})
            public void getOutline(View paramView, Outline paramOutline)
            {
              paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
            }
          });
        }
        this.callButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (ProfileActivity.this.getParentActivity() == null)
              return;
            ProfileActivity.this.VidogramCall();
          }
        });
        if ((this.user_id != 0) || ((this.chat_id >= 0) && ((!ChatObject.isLeftFromChat(this.currentChat)) || (ChatObject.isChannel(this.currentChat)))))
        {
          this.writeButton = new ImageView(paramContext);
          localObject1 = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
          if (Build.VERSION.SDK_INT >= 21)
            break label1861;
          paramContext = paramContext.getResources().getDrawable(2130837718).mutate();
          paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
          paramContext = new CombinedDrawable(paramContext, (Drawable)localObject1, 0, 0);
          paramContext.setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
        }
      }
      while (true)
      {
        this.writeButton.setBackgroundDrawable(paramContext);
        this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
        this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
        if (this.user_id != 0)
        {
          this.writeButton.setImageResource(2130837715);
          this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
          paramContext = this.writeButton;
          if (Build.VERSION.SDK_INT < 21)
            break label1848;
          i = 56;
          label1496: if (Build.VERSION.SDK_INT < 21)
            break label1854;
          f = 56.0F;
        }
        while (true)
        {
          localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f, 53, 0.0F, 0.0F, 5.0F, 0.0F));
          if (Build.VERSION.SDK_INT >= 21)
          {
            paramContext = new StateListAnimator();
            localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
            paramContext.addState(new int[] { 16842919 }, (Animator)localObject1);
            localObject1 = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
            paramContext.addState(new int[0], (Animator)localObject1);
            this.writeButton.setStateListAnimator(paramContext);
            this.writeButton.setOutlineProvider(new ViewOutlineProvider()
            {
              @SuppressLint({"NewApi"})
              public void getOutline(View paramView, Outline paramOutline)
              {
                paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
              }
            });
          }
          this.writeButton.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              if (ProfileActivity.this.getParentActivity() == null);
              while (true)
              {
                return;
                if (ProfileActivity.this.user_id != 0)
                {
                  if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
                  {
                    ProfileActivity.this.finishFragment();
                    return;
                  }
                  paramView = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                  if ((paramView == null) || ((paramView instanceof TLRPC.TL_userEmpty)))
                    continue;
                  paramView = new Bundle();
                  paramView.putInt("user_id", ProfileActivity.this.user_id);
                  if (!MessagesController.checkCanOpenChat(paramView, ProfileActivity.this))
                    continue;
                  NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  ProfileActivity.this.presentFragment(new ChatActivity(paramView), true);
                  return;
                }
                if (ProfileActivity.this.chat_id == 0)
                  continue;
                boolean bool = ChatObject.isChannel(ProfileActivity.this.currentChat);
                if (((!bool) || (ProfileActivity.this.currentChat.creator) || ((ProfileActivity.this.currentChat.megagroup) && (ProfileActivity.this.currentChat.editor))) && ((bool) || (ProfileActivity.this.currentChat.admin) || (ProfileActivity.this.currentChat.creator) || (!ProfileActivity.this.currentChat.admins_enabled)))
                  break;
                if ((ProfileActivity.this.playProfileAnimation) && ((ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2) instanceof ChatActivity)))
                {
                  ProfileActivity.this.finishFragment();
                  return;
                }
                paramView = new Bundle();
                paramView.putInt("chat_id", ProfileActivity.this.currentChat.id);
                if (!MessagesController.checkCanOpenChat(paramView, ProfileActivity.this))
                  continue;
                NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                ProfileActivity.this.presentFragment(new ChatActivity(paramView), true);
                return;
              }
              AlertDialog.Builder localBuilder = new AlertDialog.Builder(ProfileActivity.this.getParentActivity());
              paramView = MessagesController.getInstance().getChat(Integer.valueOf(ProfileActivity.this.chat_id));
              if ((paramView.photo == null) || (paramView.photo.photo_big == null) || ((paramView.photo instanceof TLRPC.TL_chatPhotoEmpty)))
              {
                paramView = new CharSequence[2];
                paramView[0] = LocaleController.getString("FromCamera", 2131165779);
                paramView[1] = LocaleController.getString("FromGalley", 2131165786);
              }
              while (true)
              {
                localBuilder.setItems(paramView, new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    if (paramInt == 0)
                      ProfileActivity.this.avatarUpdater.openCamera();
                    do
                    {
                      return;
                      if (paramInt != 1)
                        continue;
                      ProfileActivity.this.avatarUpdater.openGallery();
                      return;
                    }
                    while (paramInt != 2);
                    MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, null);
                  }
                });
                ProfileActivity.this.showDialog(localBuilder.create());
                return;
                paramView = new CharSequence[3];
                paramView[0] = LocaleController.getString("FromCamera", 2131165779);
                paramView[1] = LocaleController.getString("FromGalley", 2131165786);
                paramView[2] = LocaleController.getString("DeletePhoto", 2131165646);
              }
            }
          });
          needLayout();
          this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
          {
            public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
            {
              ProfileActivity.this.checkListViewScroll();
              if ((ProfileActivity.this.participantsMap != null) && (ProfileActivity.this.loadMoreMembersRow != -1) && (ProfileActivity.this.layoutManager.findLastVisibleItemPosition() > ProfileActivity.this.loadMoreMembersRow - 8))
                ProfileActivity.this.getChannelParticipants(false);
            }
          });
          return this.fragmentView;
          i = 60;
          break;
          f = 60.0F;
          break label1089;
          if (this.chat_id == 0)
            break label1480;
          boolean bool = ChatObject.isChannel(this.currentChat);
          if (((bool) && (!this.currentChat.creator) && ((!this.currentChat.megagroup) || (!this.currentChat.editor))) || ((!bool) && (!this.currentChat.admin) && (!this.currentChat.creator) && (this.currentChat.admins_enabled)))
          {
            this.writeButton.setImageResource(2130837715);
            this.writeButton.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
            break label1480;
          }
          this.writeButton.setImageResource(2130837714);
          break label1480;
          i = 60;
          break label1496;
          f = 60.0F;
        }
        paramContext = (Context)localObject1;
      }
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int j = 0;
    int k = 0;
    int i = 0;
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      j = ((Integer)paramArrayOfObject[0]).intValue();
      if (this.user_id != 0)
      {
        if (((j & 0x2) != 0) || ((j & 0x1) != 0) || ((j & 0x4) != 0))
          updateProfileData();
        if (((j & 0x400) != 0) && (this.listView != null))
        {
          paramArrayOfObject = (RecyclerListView.Holder)this.listView.findViewHolderForPosition(this.phoneRow);
          label105: if (paramArrayOfObject != null)
          {
            this.listAdapter.onBindViewHolder(paramArrayOfObject, this.phoneRow);
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
            break label105;
          }
        }
      }
    }
    while (true)
    {
      return;
      if (this.chat_id == 0)
        continue;
      if ((j & 0x4000) != 0)
      {
        paramArrayOfObject = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
        if (paramArrayOfObject != null)
        {
          this.currentChat = paramArrayOfObject;
          createActionBarMenu();
          updateRowsIds();
          if (this.listAdapter != null)
            this.listAdapter.notifyDataSetChanged();
        }
      }
      if (((j & 0x2000) != 0) || ((j & 0x8) != 0) || ((j & 0x10) != 0) || ((j & 0x20) != 0) || ((j & 0x4) != 0))
      {
        updateOnlineCount();
        updateProfileData();
      }
      if ((j & 0x2000) != 0)
      {
        updateRowsIds();
        if (this.listAdapter != null)
          this.listAdapter.notifyDataSetChanged();
      }
      if ((((j & 0x2) == 0) && ((j & 0x1) == 0) && ((j & 0x4) == 0)) || (this.listView == null))
        break;
      k = this.listView.getChildCount();
      paramInt = i;
      while (paramInt < k)
      {
        paramArrayOfObject = this.listView.getChildAt(paramInt);
        if ((paramArrayOfObject instanceof UserCell))
          ((UserCell)paramArrayOfObject).update(j);
        paramInt += 1;
      }
      continue;
      if (paramInt == NotificationCenter.contactsDidLoaded)
      {
        createActionBarMenu();
        return;
      }
      if (paramInt == NotificationCenter.mediaCountDidLoaded)
      {
        long l3 = ((Long)paramArrayOfObject[0]).longValue();
        long l2 = this.dialog_id;
        long l1 = l2;
        if (l2 == 0L)
        {
          if (this.user_id != 0)
            l1 = this.user_id;
        }
        else
        {
          label380: if ((l3 != l1) && (l3 != this.mergeDialogId))
            break label507;
          if (l3 != l1)
            break label509;
          this.totalMediaCount = ((Integer)paramArrayOfObject[1]).intValue();
          label419: if (this.listView == null)
            break label523;
          i = this.listView.getChildCount();
          paramInt = j;
        }
        while (true)
        {
          if (paramInt >= i)
            break label530;
          paramArrayOfObject = this.listView.getChildAt(paramInt);
          paramArrayOfObject = (RecyclerListView.Holder)this.listView.getChildViewHolder(paramArrayOfObject);
          if (paramArrayOfObject.getAdapterPosition() == this.sharedMediaRow)
          {
            this.listAdapter.onBindViewHolder(paramArrayOfObject, this.sharedMediaRow);
            return;
            l1 = l2;
            if (this.chat_id == 0)
              break label380;
            l1 = -this.chat_id;
            break label380;
            label507: break;
            label509: this.totalMediaCountMerge = ((Integer)paramArrayOfObject[1]).intValue();
            break label419;
            label523: break;
          }
          paramInt += 1;
        }
        label530: continue;
      }
      if (paramInt == NotificationCenter.encryptedChatCreated)
      {
        if (!this.creatingChat)
          break;
        AndroidUtilities.runOnUIThread(new Runnable(paramArrayOfObject)
        {
          public void run()
          {
            NotificationCenter.getInstance().removeObserver(ProfileActivity.this, NotificationCenter.closeChats);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
            TLRPC.EncryptedChat localEncryptedChat = (TLRPC.EncryptedChat)this.val$args[0];
            Bundle localBundle = new Bundle();
            localBundle.putInt("enc_id", localEncryptedChat.id);
            ProfileActivity.this.presentFragment(new ChatActivity(localBundle), true);
          }
        });
        return;
      }
      if (paramInt == NotificationCenter.encryptedChatUpdated)
      {
        paramArrayOfObject = (TLRPC.EncryptedChat)paramArrayOfObject[0];
        if ((this.currentEncryptedChat == null) || (paramArrayOfObject.id != this.currentEncryptedChat.id))
          break;
        this.currentEncryptedChat = paramArrayOfObject;
        updateRowsIds();
        if (this.listAdapter == null)
          break;
        this.listAdapter.notifyDataSetChanged();
        return;
      }
      boolean bool;
      if (paramInt == NotificationCenter.blockedUsersDidLoaded)
      {
        bool = this.userBlocked;
        this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
        if (bool == this.userBlocked)
          break;
        createActionBarMenu();
        return;
      }
      Object localObject;
      if (paramInt == NotificationCenter.chatInfoDidLoaded)
      {
        localObject = (TLRPC.ChatFull)paramArrayOfObject[0];
        if (((TLRPC.ChatFull)localObject).id != this.chat_id)
          break;
        bool = ((Boolean)paramArrayOfObject[2]).booleanValue();
        if (((this.info instanceof TLRPC.TL_channelFull)) && (((TLRPC.ChatFull)localObject).participants == null) && (this.info != null))
          ((TLRPC.ChatFull)localObject).participants = this.info.participants;
        if ((this.info == null) && ((localObject instanceof TLRPC.TL_channelFull)));
        for (paramInt = 1; ; paramInt = 0)
        {
          this.info = ((TLRPC.ChatFull)localObject);
          if ((this.mergeDialogId == 0L) && (this.info.migrated_from_chat_id != 0))
          {
            this.mergeDialogId = (-this.info.migrated_from_chat_id);
            SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
          }
          fetchUsersFromChannelInfo();
          updateOnlineCount();
          updateRowsIds();
          if (this.listAdapter != null)
            this.listAdapter.notifyDataSetChanged();
          paramArrayOfObject = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
          if (paramArrayOfObject != null)
          {
            this.currentChat = paramArrayOfObject;
            createActionBarMenu();
          }
          if ((!this.currentChat.megagroup) || ((paramInt == 0) && (bool)))
            break;
          getChannelParticipants(true);
          return;
        }
      }
      if (paramInt == NotificationCenter.closeChats)
      {
        removeSelfFromStack();
        return;
      }
      if (paramInt == NotificationCenter.botInfoDidLoaded)
      {
        paramArrayOfObject = (TLRPC.BotInfo)paramArrayOfObject[0];
        if (paramArrayOfObject.user_id != this.user_id)
          break;
        this.botInfo = paramArrayOfObject;
        updateRowsIds();
        if (this.listAdapter == null)
          break;
        this.listAdapter.notifyDataSetChanged();
        return;
      }
      if (paramInt == NotificationCenter.userInfoDidLoaded)
      {
        if (((Integer)paramArrayOfObject[0]).intValue() != this.user_id)
          break;
        if ((!this.openAnimationInProgress) && (this.callItem == null))
          createActionBarMenu();
        while (true)
        {
          updateRowsIds();
          if (this.listAdapter == null)
            break;
          this.listAdapter.notifyDataSetChanged();
          return;
          this.recreateMenuAfterAnimation = true;
        }
      }
      if ((paramInt != NotificationCenter.didReceivedNewMessages) || (((Long)paramArrayOfObject[0]).longValue() != this.dialog_id))
        break;
      paramArrayOfObject = (ArrayList)paramArrayOfObject[1];
      paramInt = k;
      while (paramInt < paramArrayOfObject.size())
      {
        localObject = (MessageObject)paramArrayOfObject.get(paramInt);
        if ((this.currentEncryptedChat != null) && (((MessageObject)localObject).messageOwner.action != null) && ((((MessageObject)localObject).messageOwner.action instanceof TLRPC.TL_messageEncryptedAction)) && ((((MessageObject)localObject).messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
        {
          localObject = (TLRPC.TL_decryptedMessageActionSetMessageTTL)((MessageObject)localObject).messageOwner.action.encryptedAction;
          if (this.listAdapter != null)
            this.listAdapter.notifyDataSetChanged();
        }
        paramInt += 1;
      }
    }
  }

  public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
  {
    Bundle localBundle;
    int i;
    if (paramLong != 0L)
    {
      localBundle = new Bundle();
      localBundle.putBoolean("scrollToTopOnResume", true);
      i = (int)paramLong;
      if (i == 0)
        break label77;
      if (i <= 0)
        break label58;
      localBundle.putInt("user_id", i);
    }
    while (!MessagesController.checkCanOpenChat(localBundle, paramDialogsActivity))
    {
      return;
      label58: if (i >= 0)
        continue;
      localBundle.putInt("chat_id", -i);
      continue;
      label77: localBundle.putInt("enc_id", (int)(paramLong >> 32));
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
    presentFragment(new ChatActivity(localBundle), true);
    removeSelfFromStack();
    paramDialogsActivity = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
    SendMessagesHelper.getInstance().sendMessage(paramDialogsActivity, paramLong, null, null, null);
  }

  public float getAnimationProgress()
  {
    return this.animationProgress;
  }

  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    if (paramFileLocation == null);
    while (true)
    {
      return null;
      if (this.user_id != 0)
      {
        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
        if ((paramMessageObject == null) || (paramMessageObject.photo == null) || (paramMessageObject.photo.photo_big == null))
          break label300;
        paramMessageObject = paramMessageObject.photo.photo_big;
      }
      while ((paramMessageObject != null) && (paramMessageObject.local_id == paramFileLocation.local_id) && (paramMessageObject.volume_id == paramFileLocation.volume_id) && (paramMessageObject.dc_id == paramFileLocation.dc_id))
      {
        paramMessageObject = new int[2];
        this.avatarImage.getLocationInWindow(paramMessageObject);
        paramFileLocation = new PhotoViewer.PlaceProviderObject();
        paramFileLocation.viewX = paramMessageObject[0];
        int i = paramMessageObject[1];
        if (Build.VERSION.SDK_INT >= 21)
        {
          paramInt = 0;
          label136: paramFileLocation.viewY = (i - paramInt);
          paramFileLocation.parentView = this.avatarImage;
          paramFileLocation.imageReceiver = this.avatarImage.getImageReceiver();
          if (this.user_id == 0)
            break label281;
          paramFileLocation.dialogId = this.user_id;
        }
        while (true)
        {
          paramFileLocation.thumb = paramFileLocation.imageReceiver.getBitmap();
          paramFileLocation.size = -1;
          paramFileLocation.radius = this.avatarImage.getImageReceiver().getRoundRadius();
          paramFileLocation.scale = this.avatarImage.getScaleX();
          return paramFileLocation;
          if (this.chat_id == 0)
            break label300;
          paramMessageObject = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
          if ((paramMessageObject == null) || (paramMessageObject.photo == null) || (paramMessageObject.photo.photo_big == null))
            break label300;
          paramMessageObject = paramMessageObject.photo.photo_big;
          break;
          paramInt = AndroidUtilities.statusBarHeight;
          break label136;
          label281: if (this.chat_id == 0)
            continue;
          paramFileLocation.dialogId = (-this.chat_id);
        }
        label300: paramMessageObject = null;
      }
    }
  }

  public int getSelectedCount()
  {
    return 0;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    Object localObject6 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = ProfileActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = ProfileActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof UserCell))
            ((UserCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.nameTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarRed");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorRed");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileRed");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconRed");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarOrange");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorOrange");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileOrange");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconOrange");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarViolet");
    ThemeDescription localThemeDescription25 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorViolet");
    ThemeDescription localThemeDescription26 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileViolet");
    ThemeDescription localThemeDescription27 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconViolet");
    ThemeDescription localThemeDescription28 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription29 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription30 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarGreen");
    ThemeDescription localThemeDescription31 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorGreen");
    ThemeDescription localThemeDescription32 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileGreen");
    ThemeDescription localThemeDescription33 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconGreen");
    ThemeDescription localThemeDescription34 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription35 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription36 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarCyan");
    ThemeDescription localThemeDescription37 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorCyan");
    ThemeDescription localThemeDescription38 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileCyan");
    ThemeDescription localThemeDescription39 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconCyan");
    ThemeDescription localThemeDescription40 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription41 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription42 = new ThemeDescription(this.topView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarPink");
    ThemeDescription localThemeDescription43 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorPink");
    ThemeDescription localThemeDescription44 = new ThemeDescription(this.onlineTextView[1], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfilePink");
    ThemeDescription localThemeDescription45 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconPink");
    ThemeDescription localThemeDescription46 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription47 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, null, "avatar_text");
    ThemeDescription localThemeDescription48 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileRed");
    ThemeDescription localThemeDescription49 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileOrange");
    ThemeDescription localThemeDescription50 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileViolet");
    ThemeDescription localThemeDescription51 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileGreen");
    ThemeDescription localThemeDescription52 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileCyan");
    ThemeDescription localThemeDescription53 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileBlue");
    ThemeDescription localThemeDescription54 = new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfilePink");
    ThemeDescription localThemeDescription55 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon");
    ThemeDescription localThemeDescription56 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground");
    ThemeDescription localThemeDescription57 = new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground");
    ThemeDescription localThemeDescription58 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription59 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGreenText2");
    ThemeDescription localThemeDescription60 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText5");
    ThemeDescription localThemeDescription61 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText");
    ThemeDescription localThemeDescription62 = new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription63 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription64 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "valueImageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription65 = new ThemeDescription(this.listView, 0, new Class[] { TextDetailCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription66 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { UserCell.class }, new String[] { "adminImage" }, null, null, null, "profile_creatorIcon");
    ThemeDescription localThemeDescription67 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { UserCell.class }, new String[] { "adminImage" }, null, null, null, "profile_adminIcon");
    ThemeDescription localThemeDescription68 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription69 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription70 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "windowBackgroundWhiteBlueText");
    Object localObject3 = this.listView;
    Object localObject4 = Theme.avatar_photoDrawable;
    Object localObject5 = Theme.avatar_broadcastDrawable;
    localObject3 = new ThemeDescription((View)localObject3, 0, new Class[] { UserCell.class }, null, new Drawable[] { localObject4, localObject5 }, null, "avatar_text");
    localObject4 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundRed");
    localObject5 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundOrange");
    ThemeDescription localThemeDescription71 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundViolet");
    ThemeDescription localThemeDescription72 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundGreen");
    ThemeDescription localThemeDescription73 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundCyan");
    ThemeDescription localThemeDescription74 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundBlue");
    localObject6 = new ThemeDescription(null, 0, null, null, null, (ThemeDescription.ThemeDescriptionDelegate)localObject6, "avatar_backgroundPink");
    ThemeDescription localThemeDescription75 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription76 = new ThemeDescription(this.listView, 0, new Class[] { AboutLinkCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    Object localObject7 = this.listView;
    int i = ThemeDescription.FLAG_TEXTCOLOR;
    Object localObject8 = Theme.profile_aboutTextPaint;
    localObject7 = new ThemeDescription((View)localObject7, i, new Class[] { AboutLinkCell.class }, (Paint)localObject8, null, null, "windowBackgroundWhiteBlackText");
    localObject8 = this.listView;
    i = ThemeDescription.FLAG_LINKCOLOR;
    Object localObject9 = Theme.profile_aboutTextPaint;
    localObject8 = new ThemeDescription((View)localObject8, i, new Class[] { AboutLinkCell.class }, (Paint)localObject9, null, null, "windowBackgroundWhiteLinkText");
    localObject9 = this.listView;
    Paint localPaint = Theme.linkSelectionPaint;
    return (ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, localThemeDescription25, localThemeDescription26, localThemeDescription27, localThemeDescription28, localThemeDescription29, localThemeDescription30, localThemeDescription31, localThemeDescription32, localThemeDescription33, localThemeDescription34, localThemeDescription35, localThemeDescription36, localThemeDescription37, localThemeDescription38, localThemeDescription39, localThemeDescription40, localThemeDescription41, localThemeDescription42, localThemeDescription43, localThemeDescription44, localThemeDescription45, localThemeDescription46, localObject1, localObject2, localThemeDescription47, localThemeDescription48, localThemeDescription49, localThemeDescription50, localThemeDescription51, localThemeDescription52, localThemeDescription53, localThemeDescription54, localThemeDescription55, localThemeDescription56, localThemeDescription57, localThemeDescription58, localThemeDescription59, localThemeDescription60, localThemeDescription61, localThemeDescription62, localThemeDescription63, localThemeDescription64, localThemeDescription65, localThemeDescription66, localThemeDescription67, localThemeDescription68, localThemeDescription69, localThemeDescription70, localObject3, localObject4, localObject5, localThemeDescription71, localThemeDescription72, localThemeDescription73, localThemeDescription74, localObject6, localThemeDescription75, localThemeDescription76, localObject7, localObject8, new ThemeDescription((View)localObject9, 0, new Class[] { AboutLinkCell.class }, localPaint, null, null, "windowBackgroundWhiteLinkSelection"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[] { Theme.profile_verifiedCheckDrawable }, null, "profile_verifiedCheck"), new ThemeDescription(this.nameTextView[1], 0, null, null, new Drawable[] { Theme.profile_verifiedDrawable }, null, "profile_verifiedBackground") };
  }

  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }

  public boolean isChat()
  {
    return this.chat_id != 0;
  }

  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    if (this.chat_id != 0)
      this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }

  protected AnimatorSet onCustomTransitionAnimation(boolean paramBoolean, Runnable paramRunnable)
  {
    if ((this.playProfileAnimation) && (this.allowProfileAnimation))
    {
      AnimatorSet localAnimatorSet = new AnimatorSet();
      localAnimatorSet.setDuration(180L);
      if (Build.VERSION.SDK_INT > 15)
        this.listView.setLayerType(2, null);
      Object localObject = this.actionBar.createMenu();
      if ((((ActionBarMenu)localObject).getItem(10) == null) && (this.animatingItem == null))
        this.animatingItem = ((ActionBarMenu)localObject).addItem(10, 2130837738);
      int i;
      float f1;
      label531: SimpleTextView localSimpleTextView;
      if (paramBoolean)
      {
        localObject = (FrameLayout.LayoutParams)this.onlineTextView[1].getLayoutParams();
        ((FrameLayout.LayoutParams)localObject).rightMargin = (int)(-21.0F * AndroidUtilities.density + AndroidUtilities.dp(8.0F));
        this.onlineTextView[1].setLayoutParams((ViewGroup.LayoutParams)localObject);
        i = (int)Math.ceil(AndroidUtilities.displaySize.x - AndroidUtilities.dp(126.0F) + 21.0F * AndroidUtilities.density);
        f1 = this.nameTextView[1].getPaint().measureText(this.nameTextView[1].getText().toString());
        float f2 = this.nameTextView[1].getSideDrawablesSize();
        localObject = (FrameLayout.LayoutParams)this.nameTextView[1].getLayoutParams();
        if (i < f2 + f1 * 1.12F)
        {
          ((FrameLayout.LayoutParams)localObject).width = (int)Math.ceil(i / 1.12F);
          this.nameTextView[1].setLayoutParams((ViewGroup.LayoutParams)localObject);
          this.initialAnimationExtraHeight = AndroidUtilities.dp(88.0F);
          this.fragmentView.setBackgroundColor(0);
          setAnimationProgress(0.0F);
          localObject = new ArrayList();
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this, "animationProgress", new float[] { 0.0F, 1.0F }));
          if (this.writeButton != null)
          {
            this.writeButton.setScaleX(0.2F);
            this.writeButton.setScaleY(0.2F);
            this.writeButton.setAlpha(0.0F);
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }));
          }
          if (this.callButton != null)
          {
            this.callButton.setScaleX(0.2F);
            this.callButton.setScaleY(0.2F);
            this.callButton.setAlpha(0.0F);
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "scaleX", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "scaleY", new float[] { 1.0F }));
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "alpha", new float[] { 1.0F }));
          }
          i = 0;
          if (i >= 2)
            break label694;
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0)
            break label674;
          f1 = 1.0F;
          label553: localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.nameTextView[i];
          if (i != 0)
            break label679;
          f1 = 1.0F;
          label575: localSimpleTextView.setAlpha(f1);
          localSimpleTextView = this.onlineTextView[i];
          if (i != 0)
            break label684;
          f1 = 0.0F;
          label597: ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          localSimpleTextView = this.nameTextView[i];
          if (i != 0)
            break label689;
          f1 = 0.0F;
        }
        while (true)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
          i += 1;
          break label531;
          ((FrameLayout.LayoutParams)localObject).width = -2;
          break;
          label674: f1 = 0.0F;
          break label553;
          label679: f1 = 0.0F;
          break label575;
          label684: f1 = 1.0F;
          break label597;
          label689: f1 = 1.0F;
        }
        label694: if (this.animatingItem != null)
        {
          this.animatingItem.setAlpha(1.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 0.0F }));
        }
        if (this.callItem != null)
        {
          this.callItem.setAlpha(0.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[] { 1.0F }));
        }
        localAnimatorSet.playTogether((Collection)localObject);
      }
      while (true)
      {
        localAnimatorSet.addListener(new AnimatorListenerAdapter(paramRunnable)
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if (Build.VERSION.SDK_INT > 15)
              ProfileActivity.this.listView.setLayerType(0, null);
            if (ProfileActivity.this.animatingItem != null)
            {
              ProfileActivity.this.actionBar.createMenu().clearItems();
              ProfileActivity.access$5902(ProfileActivity.this, null);
            }
            this.val$callback.run();
          }
        });
        localAnimatorSet.setInterpolator(new DecelerateInterpolator());
        AndroidUtilities.runOnUIThread(new Runnable(localAnimatorSet)
        {
          public void run()
          {
            this.val$animatorSet.start();
          }
        }
        , 50L);
        return localAnimatorSet;
        this.initialAnimationExtraHeight = this.extraHeight;
        localObject = new ArrayList();
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this, "animationProgress", new float[] { 1.0F, 0.0F }));
        if (this.writeButton != null)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }));
        }
        if (this.callButton != null)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "scaleX", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "scaleY", new float[] { 0.2F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callButton, "alpha", new float[] { 0.0F }));
        }
        i = 0;
        if (i < 2)
        {
          localSimpleTextView = this.onlineTextView[i];
          if (i == 0)
          {
            f1 = 1.0F;
            label1048: ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
            localSimpleTextView = this.nameTextView[i];
            if (i != 0)
              break label1120;
            f1 = 1.0F;
          }
          while (true)
          {
            ((ArrayList)localObject).add(ObjectAnimator.ofFloat(localSimpleTextView, "alpha", new float[] { f1 }));
            i += 1;
            break;
            f1 = 0.0F;
            break label1048;
            label1120: f1 = 0.0F;
          }
        }
        if (this.animatingItem != null)
        {
          this.animatingItem.setAlpha(0.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.animatingItem, "alpha", new float[] { 1.0F }));
        }
        if (this.callItem != null)
        {
          this.callItem.setAlpha(1.0F);
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.callItem, "alpha", new float[] { 0.0F }));
        }
        localAnimatorSet.playTogether((Collection)localObject);
      }
    }
    return (AnimatorSet)null;
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    if (this.listView != null)
      this.listView.invalidateViews();
  }

  public boolean onFragmentCreate()
  {
    this.user_id = this.arguments.getInt("user_id", 0);
    this.chat_id = getArguments().getInt("chat_id", 0);
    if (this.user_id != 0)
    {
      this.dialog_id = this.arguments.getLong("dialog_id", 0L);
      if (this.dialog_id != 0L)
        this.currentEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(this.dialog_id >> 32)));
      this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (this.user == null)
        return false;
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.userInfoDidLoaded);
      if (this.currentEncryptedChat != null)
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceivedNewMessages);
      this.userBlocked = MessagesController.getInstance().blockedUsers.contains(Integer.valueOf(this.user_id));
      if (this.user.bot)
        BotQuery.loadBotInfo(this.user.id, true, this.classGuid);
      MessagesController.getInstance().loadFullUser(MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)), this.classGuid, true);
      this.participantsMap = null;
      this.history = itman.Vidofilm.d.a.a(ApplicationLoader.applicationContext).a(this.user_id + "", 3);
      label306: if (this.dialog_id == 0L)
        break label562;
      SharedMediaQuery.getMediaCount(this.dialog_id, 0, this.classGuid, true);
    }
    while (true)
    {
      while (true)
      {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.mediaCountDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        updateRowsIds();
        return true;
        if (this.chat_id == 0)
          break;
        this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
        Semaphore localSemaphore;
        if (this.currentChat == null)
        {
          localSemaphore = new Semaphore(0);
          MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localSemaphore)
          {
            public void run()
            {
              ProfileActivity.access$402(ProfileActivity.this, MessagesStorage.getInstance().getChat(ProfileActivity.this.chat_id));
              this.val$semaphore.release();
            }
          });
        }
        try
        {
          localSemaphore.acquire();
          if (this.currentChat == null)
            break;
          MessagesController.getInstance().putChat(this.currentChat, true);
          if (this.currentChat.megagroup)
          {
            getChannelParticipants(true);
            NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
            this.sortedUsers = new ArrayList();
            updateOnlineCount();
            this.avatarUpdater = new AvatarUpdater();
            this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
            {
              public void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
              {
                if (ProfileActivity.this.chat_id != 0)
                  MessagesController.getInstance().changeChatAvatar(ProfileActivity.this.chat_id, paramInputFile);
              }
            };
            this.avatarUpdater.parentFragment = this;
            if (!ChatObject.isChannel(this.currentChat))
              break label306;
            MessagesController.getInstance().loadFullChat(this.chat_id, this.classGuid, true);
          }
        }
        catch (Exception localException)
        {
          while (true)
          {
            FileLog.e(localException);
            continue;
            this.participantsMap = null;
          }
        }
      }
      label562: if (this.user_id != 0)
      {
        SharedMediaQuery.getMediaCount(this.user_id, 0, this.classGuid, true);
        continue;
      }
      if (this.chat_id <= 0)
        continue;
      SharedMediaQuery.getMediaCount(-this.chat_id, 0, this.classGuid, true);
      if (this.mergeDialogId == 0L)
        continue;
      SharedMediaQuery.getMediaCount(this.mergeDialogId, 0, this.classGuid, true);
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.mediaCountDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    if (this.user_id != 0)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatCreated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.encryptedChatUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.botInfoDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.userInfoDidLoaded);
      MessagesController.getInstance().cancelLoadFullUser(this.user_id);
      if (this.currentEncryptedChat != null)
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceivedNewMessages);
    }
    do
      return;
    while (this.chat_id == 0);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    this.avatarUpdater.clear();
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      paramArrayOfString = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (paramArrayOfString != null);
    }
    else
    {
      return;
    }
    if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
    {
      VoIPHelper.startCall(paramArrayOfString, getParentActivity(), MessagesController.getInstance().getUserFull(paramArrayOfString.id));
      return;
    }
    VoIPHelper.permissionDenied(getParentActivity(), null);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    updateProfileData();
    fixLayout();
  }

  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation))
    {
      this.openAnimationInProgress = false;
      if (this.recreateMenuAfterAnimation)
        createActionBarMenu();
    }
    NotificationCenter.getInstance().setAnimationInProgress(false);
  }

  protected void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((!paramBoolean2) && (this.playProfileAnimation) && (this.allowProfileAnimation))
      this.openAnimationInProgress = true;
    NotificationCenter.getInstance().setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.mediaCountDidLoaded });
    NotificationCenter.getInstance().setAnimationInProgress(true);
  }

  public boolean processSendingText(CharSequence paramCharSequence)
  {
    int k = 0;
    paramCharSequence = AndroidUtilities.getTrimmedString(paramCharSequence);
    if (paramCharSequence.length() != 0)
    {
      int j = (int)Math.ceil(paramCharSequence.length() / 4096.0F);
      int i = 0;
      while (i < j)
      {
        CharSequence[] arrayOfCharSequence = new CharSequence[1];
        arrayOfCharSequence[0] = paramCharSequence.subSequence(i * 4096, Math.min((i + 1) * 4096, paramCharSequence.length()));
        ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
        SendMessagesHelper.getInstance().sendMessage(arrayOfCharSequence[0].toString(), this.user_id, null, null, false, localArrayList, null, null);
        i += 1;
      }
      k = 1;
    }
    return k;
  }

  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.chat_id != 0)
    {
      MessagesController.getInstance().loadChatInfo(this.chat_id, null, false);
      if (this.avatarUpdater != null)
        this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    }
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.chat_id != 0) && (this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null))
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
  }

  public boolean scaleToFill()
  {
    return false;
  }

  public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
  {
  }

  @Keep
  public void setAnimationProgress(float paramFloat)
  {
    this.animationProgress = paramFloat;
    this.listView.setAlpha(paramFloat);
    this.listView.setTranslationX(AndroidUtilities.dp(48.0F) - AndroidUtilities.dp(48.0F) * paramFloat);
    int k;
    int m;
    int j;
    int n;
    int i1;
    label191: int i2;
    int i3;
    int i4;
    if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      k = AvatarDrawable.getProfileBackColorForId(i);
      m = Theme.getColor("actionBarDefault");
      i = Color.red(m);
      j = Color.green(m);
      m = Color.blue(m);
      n = (int)((Color.red(k) - i) * paramFloat);
      i1 = (int)((Color.green(k) - j) * paramFloat);
      k = (int)((Color.blue(k) - m) * paramFloat);
      this.topView.setBackgroundColor(Color.rgb(i + n, j + i1, k + m));
      if ((this.user_id == 0) && ((!ChatObject.isChannel(this.chat_id)) || (this.currentChat.megagroup)))
        break label412;
      i = 5;
      k = AvatarDrawable.getIconColorForId(i);
      m = Theme.getColor("actionBarDefaultIcon");
      i = Color.red(m);
      j = Color.green(m);
      m = Color.blue(m);
      n = (int)((Color.red(k) - i) * paramFloat);
      i1 = (int)((Color.green(k) - j) * paramFloat);
      k = (int)((Color.blue(k) - m) * paramFloat);
      this.actionBar.setItemsColor(Color.rgb(i + n, j + i1, k + m), false);
      i = Theme.getColor("profile_title");
      n = Theme.getColor("actionBarDefaultTitle");
      j = Color.red(n);
      k = Color.green(n);
      m = Color.blue(n);
      n = Color.alpha(n);
      i1 = (int)((Color.red(i) - j) * paramFloat);
      i2 = (int)((Color.green(i) - k) * paramFloat);
      i3 = (int)((Color.blue(i) - m) * paramFloat);
      i4 = (int)((Color.alpha(i) - n) * paramFloat);
      i = 0;
      label383: if (i >= 2)
        break label454;
      if (this.nameTextView[i] != null)
        break label420;
    }
    while (true)
    {
      i += 1;
      break label383;
      i = this.chat_id;
      break;
      label412: i = this.chat_id;
      break label191;
      label420: this.nameTextView[i].setTextColor(Color.argb(n + i4, j + i1, k + i2, m + i3));
    }
    label454: if ((this.user_id != 0) || ((ChatObject.isChannel(this.chat_id)) && (!this.currentChat.megagroup)))
    {
      i = 5;
      i = AvatarDrawable.getProfileTextColorForId(i);
      n = Theme.getColor("actionBarDefaultSubtitle");
      j = Color.red(n);
      k = Color.green(n);
      m = Color.blue(n);
      n = Color.alpha(n);
      i1 = (int)((Color.red(i) - j) * paramFloat);
      i2 = (int)((Color.green(i) - k) * paramFloat);
      i3 = (int)((Color.blue(i) - m) * paramFloat);
      i4 = (int)((Color.alpha(i) - n) * paramFloat);
      i = 0;
      if (i >= 2)
        break label639;
      if (this.onlineTextView[i] != null)
        break label605;
    }
    while (true)
    {
      label576: i += 1;
      break label576;
      i = this.chat_id;
      break;
      label605: this.onlineTextView[i].setTextColor(Color.argb(n + i4, j + i1, k + i2, m + i3));
    }
    label639: this.extraHeight = (int)(this.initialAnimationExtraHeight * paramFloat);
    if (this.user_id != 0)
    {
      i = this.user_id;
      j = AvatarDrawable.getProfileColorForId(i);
      if (this.user_id == 0)
        break label784;
    }
    label784: for (int i = this.user_id; ; i = this.chat_id)
    {
      i = AvatarDrawable.getColorForId(i);
      if (j != i)
      {
        k = (int)((Color.red(j) - Color.red(i)) * paramFloat);
        m = (int)((Color.green(j) - Color.green(i)) * paramFloat);
        j = (int)((Color.blue(j) - Color.blue(i)) * paramFloat);
        this.avatarDrawable.setColor(Color.rgb(k + Color.red(i), m + Color.green(i), Color.blue(i) + j));
        this.avatarImage.invalidate();
      }
      needLayout();
      return;
      i = this.chat_id;
      break;
    }
  }

  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if ((this.info != null) && (this.info.migrated_from_chat_id != 0))
      this.mergeDialogId = (-this.info.migrated_from_chat_id);
    fetchUsersFromChannelInfo();
  }

  public void setPhotoChecked(int paramInt)
  {
  }

  public void setPlayProfileAnimation(boolean paramBoolean)
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    if ((!AndroidUtilities.isTablet()) && (localSharedPreferences.getBoolean("view_animations", true)))
      this.playProfileAnimation = paramBoolean;
  }

  public void updatePhotoAtIndex(int paramInt)
  {
  }

  public void willHidePhotoViewer()
  {
    this.avatarImage.getImageReceiver().setVisible(true, true);
  }

  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
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
      return ProfileActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      int j = 5;
      int i;
      if ((paramInt == ProfileActivity.this.emptyRow) || (paramInt == ProfileActivity.this.emptyRowChat) || (paramInt == ProfileActivity.this.emptyRowChat2))
        i = 0;
      do
      {
        do
        {
          return i;
          if ((paramInt == ProfileActivity.this.sectionRow) || (paramInt == ProfileActivity.this.userSectionRow))
            return 1;
          if ((paramInt == ProfileActivity.this.phoneRow) || (paramInt == ProfileActivity.this.usernameRow) || (paramInt == ProfileActivity.this.channelNameRow))
            return 2;
          if ((paramInt == ProfileActivity.this.leaveChannelRow) || (paramInt == ProfileActivity.this.sharedMediaRow) || (paramInt == ProfileActivity.this.settingsTimerRow) || (paramInt == ProfileActivity.this.settingsNotificationsRow) || (paramInt == ProfileActivity.this.startSecretChatRow) || (paramInt == ProfileActivity.this.settingsKeyRow) || (paramInt == ProfileActivity.this.membersRow) || (paramInt == ProfileActivity.this.managementRow) || (paramInt == ProfileActivity.this.blockedUsersRow) || (paramInt == ProfileActivity.this.convertRow) || (paramInt == ProfileActivity.this.addMemberRow) || (paramInt == ProfileActivity.this.groupsInCommonRow))
            return 3;
          if ((paramInt > ProfileActivity.this.emptyRowChat2) && (paramInt < ProfileActivity.this.membersEndRow))
            return 4;
          i = j;
        }
        while (paramInt == ProfileActivity.this.membersSectionRow);
        if (paramInt == ProfileActivity.this.convertHelpRow)
          return 6;
        if (paramInt == ProfileActivity.this.loadMoreMembersRow)
          return 7;
        if ((paramInt == ProfileActivity.this.userInfoRow) || (paramInt == ProfileActivity.this.channelInfoRow))
          return 8;
        i = j;
      }
      while (paramInt == ProfileActivity.this.callHistoryShadowRow);
      if (paramInt == ProfileActivity.this.callHistoryEmptyRow)
        return 0;
      if ((paramInt > ProfileActivity.this.callHistoryEmptyRow) && (paramInt < ProfileActivity.this.callHistoryEndRow))
        return 9;
      if (paramInt == ProfileActivity.this.callHistorySeeMoreRow)
        return 3;
      if (paramInt == ProfileActivity.this.callHistorySeeMoreEmptyRow)
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int k = 0;
      int i = paramViewHolder.getAdapterPosition();
      int j;
      if (ProfileActivity.this.user_id != 0)
        if ((i != ProfileActivity.this.phoneRow) && (i != ProfileActivity.this.settingsTimerRow) && (i != ProfileActivity.this.settingsKeyRow) && (i != ProfileActivity.this.settingsNotificationsRow) && (i != ProfileActivity.this.sharedMediaRow) && (i != ProfileActivity.this.startSecretChatRow) && (i != ProfileActivity.this.usernameRow) && (i != ProfileActivity.this.userInfoRow) && (i != ProfileActivity.this.groupsInCommonRow) && (i != ProfileActivity.this.callHistorySeeMoreRow))
        {
          j = k;
          if (i > ProfileActivity.this.callHistoryEmptyRow)
          {
            j = k;
            if (i >= ProfileActivity.this.callHistoryEndRow);
          }
        }
        else
        {
          j = 1;
        }
      do
      {
        do
        {
          return j;
          j = k;
        }
        while (ProfileActivity.this.chat_id == 0);
        if ((i == ProfileActivity.this.convertRow) || (i == ProfileActivity.this.settingsNotificationsRow) || (i == ProfileActivity.this.sharedMediaRow) || ((i > ProfileActivity.this.emptyRowChat2) && (i < ProfileActivity.this.membersEndRow)) || (i == ProfileActivity.this.addMemberRow) || (i == ProfileActivity.this.channelNameRow) || (i == ProfileActivity.this.leaveChannelRow) || (i == ProfileActivity.this.membersRow) || (i == ProfileActivity.this.managementRow) || (i == ProfileActivity.this.blockedUsersRow))
          break;
        j = k;
      }
      while (i != ProfileActivity.this.channelInfoRow);
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      case 1:
      case 5:
      case 6:
      case 7:
      default:
      case 0:
      case 2:
      case 3:
      case 4:
      case 8:
        label1071: label1084: label1226: label1356: label2035: label2037: 
        do
        {
          do
          {
            do
            {
              return;
              if ((paramInt == ProfileActivity.this.emptyRowChat) || (paramInt == ProfileActivity.this.emptyRowChat2) || (paramInt == ProfileActivity.this.callHistoryEmptyRow))
              {
                ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(8.0F));
                return;
              }
              ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(36.0F));
              return;
              localObject1 = (TextDetailCell)paramViewHolder.itemView;
              if (paramInt == ProfileActivity.this.phoneRow)
              {
                paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
                if ((paramViewHolder.phone != null) && (paramViewHolder.phone.length() != 0));
                for (paramViewHolder = org.vidogram.a.b.a().e("+" + paramViewHolder.phone); ; paramViewHolder = LocaleController.getString("NumberUnknown", 2131166152))
                {
                  ((TextDetailCell)localObject1).setTextAndValueAndIcon(paramViewHolder, LocaleController.getString("PhoneMobile", 2131166265), 2130838035);
                  return;
                }
              }
              if (paramInt != ProfileActivity.this.usernameRow)
                continue;
              paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(ProfileActivity.this.user_id));
              if ((paramViewHolder != null) && (paramViewHolder.username != null) && (paramViewHolder.username.length() != 0));
              for (paramViewHolder = "@" + paramViewHolder.username; ; paramViewHolder = "-")
              {
                ((TextDetailCell)localObject1).setTextAndValue(paramViewHolder, LocaleController.getString("Username", 2131166550));
                return;
              }
            }
            while (paramInt != ProfileActivity.this.channelNameRow);
            if ((ProfileActivity.this.currentChat != null) && (ProfileActivity.this.currentChat.username != null) && (ProfileActivity.this.currentChat.username.length() != 0));
            for (paramViewHolder = "@" + ProfileActivity.this.currentChat.username; ; paramViewHolder = "-")
            {
              ((TextDetailCell)localObject1).setTextAndValue(paramViewHolder, MessagesController.getInstance().linkPrefix + "/" + ProfileActivity.this.currentChat.username);
              return;
            }
            localObject1 = (TextCell)paramViewHolder.itemView;
            ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            ((TextCell)localObject1).setTag("windowBackgroundWhiteBlackText");
            if (paramInt == ProfileActivity.this.callHistorySeeMoreRow)
            {
              ((TextCell)localObject1).setTextAndIcon(LocaleController.getString("CallHistory", 2131166754), 2130838034);
              return;
            }
            int i;
            if (paramInt == ProfileActivity.this.sharedMediaRow)
            {
              if (ProfileActivity.this.totalMediaCount == -1)
              {
                paramViewHolder = LocaleController.getString("Loading", 2131165920);
                if ((ProfileActivity.this.user_id != 0) && (UserConfig.getClientUserId() == ProfileActivity.this.user_id))
                {
                  ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("SharedMedia", 2131166462), paramViewHolder, 2130838034);
                  return;
                }
              }
              else
              {
                i = ProfileActivity.this.totalMediaCount;
                if (ProfileActivity.this.totalMediaCountMerge != -1);
                for (paramInt = ProfileActivity.this.totalMediaCountMerge; ; paramInt = 0)
                {
                  paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt + i) });
                  break;
                }
              }
              ((TextCell)localObject1).setTextAndValue(LocaleController.getString("SharedMedia", 2131166462), paramViewHolder);
              return;
            }
            if (paramInt == ProfileActivity.this.groupsInCommonRow)
            {
              paramViewHolder = MessagesController.getInstance().getUserFull(ProfileActivity.this.user_id);
              localObject2 = LocaleController.getString("GroupsInCommon", 2131165806);
              if (paramViewHolder != null);
              for (paramInt = paramViewHolder.common_chats_count; ; paramInt = 0)
              {
                ((TextCell)localObject1).setTextAndValue((String)localObject2, String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
                return;
              }
            }
            if (paramInt == ProfileActivity.this.settingsTimerRow)
            {
              paramViewHolder = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32)));
              if (paramViewHolder.ttl == 0);
              for (paramViewHolder = LocaleController.getString("ShortMessageLifetimeForever", 2131166466); ; paramViewHolder = LocaleController.formatTTLString(paramViewHolder.ttl))
              {
                ((TextCell)localObject1).setTextAndValue(LocaleController.getString("MessageLifetime", 2131165960), paramViewHolder);
                return;
              }
            }
            if (paramInt == ProfileActivity.this.settingsNotificationsRow)
            {
              paramViewHolder = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
              long l;
              boolean bool2;
              boolean bool1;
              if (ProfileActivity.this.dialog_id != 0L)
              {
                l = ProfileActivity.this.dialog_id;
                bool2 = paramViewHolder.getBoolean("custom_" + l, false);
                bool1 = paramViewHolder.contains("notify2_" + l);
                paramInt = paramViewHolder.getInt("notify2_" + l, 0);
                i = paramViewHolder.getInt("notifyuntil_" + l, 0);
                if ((paramInt != 3) || (i == 2147483647))
                  break label1226;
                paramInt = i - ConnectionsManager.getInstance().getCurrentTime();
                if (paramInt > 0)
                  break label1084;
                if (!bool2)
                  break label1071;
                paramViewHolder = LocaleController.getString("NotificationsCustom", 2131166130);
              }
              while (true)
              {
                if (paramViewHolder == null)
                  break label1356;
                ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("Notifications", 2131166128), paramViewHolder, 2130838034);
                return;
                if (ProfileActivity.this.user_id != 0)
                {
                  l = ProfileActivity.this.user_id;
                  break;
                }
                l = -ProfileActivity.this.chat_id;
                break;
                paramViewHolder = LocaleController.getString("NotificationsOn", 2131166138);
                continue;
                if (paramInt < 3600)
                {
                  paramViewHolder = LocaleController.formatString("WillUnmuteIn", 2131166628, new Object[] { LocaleController.formatPluralString("Minutes", paramInt / 60) });
                  continue;
                }
                if (paramInt < 86400)
                {
                  paramViewHolder = LocaleController.formatString("WillUnmuteIn", 2131166628, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(paramInt / 60.0F / 60.0F)) });
                  continue;
                }
                if (paramInt < 31536000)
                {
                  paramViewHolder = LocaleController.formatString("WillUnmuteIn", 2131166628, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(paramInt / 60.0F / 60.0F / 24.0F)) });
                  continue;
                }
                paramViewHolder = null;
                continue;
                if (paramInt == 0)
                  if (bool1)
                    bool1 = true;
                while (true)
                {
                  if ((!bool1) || (!bool2))
                    break label1325;
                  paramViewHolder = LocaleController.getString("NotificationsCustom", 2131166130);
                  break;
                  if ((int)l < 0)
                  {
                    bool1 = paramViewHolder.getBoolean("EnableGroup", true);
                    continue;
                  }
                  bool1 = paramViewHolder.getBoolean("EnableAll", true);
                  continue;
                  if (paramInt == 1)
                  {
                    bool1 = true;
                    continue;
                  }
                  if (paramInt == 2)
                  {
                    bool1 = false;
                    continue;
                  }
                  bool1 = false;
                }
                if (bool1)
                {
                  paramViewHolder = LocaleController.getString("NotificationsOn", 2131166138);
                  continue;
                }
                paramViewHolder = LocaleController.getString("NotificationsOff", 2131166137);
              }
              ((TextCell)localObject1).setTextAndValueAndIcon(LocaleController.getString("Notifications", 2131166128), LocaleController.getString("NotificationsOff", 2131166137), 2130838034);
              return;
            }
            if (paramInt == ProfileActivity.this.startSecretChatRow)
            {
              ((TextCell)localObject1).setText(LocaleController.getString("StartEncryptedChat", 2131166482));
              ((TextCell)localObject1).setTag("windowBackgroundWhiteGreenText2");
              ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteGreenText2"));
              return;
            }
            if (paramInt == ProfileActivity.this.settingsKeyRow)
            {
              paramViewHolder = new IdenticonDrawable();
              paramViewHolder.setEncryptedChat(MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(ProfileActivity.this.dialog_id >> 32))));
              ((TextCell)localObject1).setTextAndValueDrawable(LocaleController.getString("EncryptionKey", 2131165687), paramViewHolder);
              return;
            }
            if (paramInt == ProfileActivity.this.leaveChannelRow)
            {
              ((TextCell)localObject1).setTag("windowBackgroundWhiteRedText5");
              ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
              ((TextCell)localObject1).setText(LocaleController.getString("LeaveChannel", 2131165901));
              return;
            }
            if (paramInt == ProfileActivity.this.convertRow)
            {
              ((TextCell)localObject1).setText(LocaleController.getString("UpgradeGroup", 2131166543));
              ((TextCell)localObject1).setTag("windowBackgroundWhiteGreenText2");
              ((TextCell)localObject1).setTextColor(Theme.getColor("windowBackgroundWhiteGreenText2"));
              return;
            }
            if (paramInt == ProfileActivity.this.membersRow)
            {
              if (ProfileActivity.this.info != null)
              {
                ((TextCell)localObject1).setTextAndValue(LocaleController.getString("ChannelMembers", 2131165477), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.access$900(ProfileActivity.this).participants_count) }));
                return;
              }
              ((TextCell)localObject1).setText(LocaleController.getString("ChannelMembers", 2131165477));
              return;
            }
            if (paramInt == ProfileActivity.this.managementRow)
            {
              if (ProfileActivity.this.info != null)
              {
                ((TextCell)localObject1).setTextAndValue(LocaleController.getString("ChannelAdministrators", 2131165450), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.access$900(ProfileActivity.this).admins_count) }));
                return;
              }
              ((TextCell)localObject1).setText(LocaleController.getString("ChannelAdministrators", 2131165450));
              return;
            }
            if (paramInt != ProfileActivity.this.blockedUsersRow)
              continue;
            if (ProfileActivity.this.info != null)
            {
              ((TextCell)localObject1).setTextAndValue(LocaleController.getString("ChannelBlockedUsers", 2131165455), String.format("%d", new Object[] { Integer.valueOf(ProfileActivity.access$900(ProfileActivity.this).kicked_count) }));
              return;
            }
            ((TextCell)localObject1).setText(LocaleController.getString("ChannelBlockedUsers", 2131165455));
            return;
          }
          while (paramInt != ProfileActivity.this.addMemberRow);
          if (ProfileActivity.this.chat_id > 0)
          {
            ((TextCell)localObject1).setText(LocaleController.getString("AddMember", 2131165281));
            return;
          }
          ((TextCell)localObject1).setText(LocaleController.getString("AddRecipient", 2131165283));
          return;
          localObject1 = (UserCell)paramViewHolder.itemView;
          if (!ProfileActivity.this.sortedUsers.isEmpty())
          {
            paramViewHolder = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(((Integer)ProfileActivity.this.sortedUsers.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1)).intValue());
            if (paramViewHolder == null)
              break label2035;
            if (!(paramViewHolder instanceof TLRPC.TL_chatChannelParticipant))
              break label2071;
            localObject2 = ((TLRPC.TL_chatChannelParticipant)paramViewHolder).channelParticipant;
            if (!(localObject2 instanceof TLRPC.TL_channelParticipantCreator))
              break label2037;
            ((UserCell)localObject1).setIsAdmin(1);
            paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(paramViewHolder.user_id));
            if (paramInt != ProfileActivity.this.emptyRowChat2 + 1)
              break label2125;
          }
          for (paramInt = 2130837917; ; paramInt = 0)
          {
            ((UserCell)localObject1).setData(paramViewHolder, null, null, paramInt);
            return;
            paramViewHolder = (TLRPC.ChatParticipant)ProfileActivity.this.info.participants.participants.get(paramInt - ProfileActivity.this.emptyRowChat2 - 1);
            break label1928;
            break;
            if (((localObject2 instanceof TLRPC.TL_channelParticipantEditor)) || ((localObject2 instanceof TLRPC.TL_channelParticipantModerator)))
            {
              ((UserCell)localObject1).setIsAdmin(2);
              break label1962;
            }
            ((UserCell)localObject1).setIsAdmin(0);
            break label1962;
            if ((paramViewHolder instanceof TLRPC.TL_chatParticipantCreator))
            {
              ((UserCell)localObject1).setIsAdmin(1);
              break label1962;
            }
            if ((ProfileActivity.this.currentChat.admins_enabled) && ((paramViewHolder instanceof TLRPC.TL_chatParticipantAdmin)))
            {
              ((UserCell)localObject1).setIsAdmin(2);
              break label1962;
            }
            ((UserCell)localObject1).setIsAdmin(0);
            break label1962;
          }
          localObject1 = (AboutLinkCell)paramViewHolder.itemView;
          if (paramInt != ProfileActivity.this.userInfoRow)
            continue;
          paramViewHolder = MessagesController.getInstance().getUserFull(ProfileActivity.this.user_id);
          if (paramViewHolder != null);
          for (paramViewHolder = paramViewHolder.about; ; paramViewHolder = null)
          {
            ((AboutLinkCell)localObject1).setTextAndIcon(paramViewHolder, 2130838033);
            return;
          }
        }
        while (paramInt != ProfileActivity.this.channelInfoRow);
        label1325: label2125: for (paramViewHolder = ProfileActivity.this.info.about; paramViewHolder.contains("\n\n\n"); paramViewHolder = paramViewHolder.replace("\n\n\n", "\n\n"));
        label1928: label1962: ((AboutLinkCell)localObject1).setTextAndIcon(paramViewHolder, 2130838033);
        label2071: return;
      case 9:
      }
      paramViewHolder = (TextCell)paramViewHolder.itemView;
      Object localObject1 = (itman.Vidofilm.a.b)ProfileActivity.this.history.get(paramInt - ProfileActivity.this.callHistoryEmptyRow - 1);
      Object localObject2 = String.format("%02d:%02d", new Object[] { Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(((itman.Vidofilm.a.b)localObject1).i())), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(((itman.Vidofilm.a.b)localObject1).i()) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(((itman.Vidofilm.a.b)localObject1).i()))) });
      LocaleController.getInstance();
      paramViewHolder.setTextAndValueAndIconNofilter((String)localObject2, LocaleController.formatDateCallLog(((itman.Vidofilm.a.b)localObject1).h()), itman.Vidofilm.d.a.a(ApplicationLoader.applicationContext).a(((itman.Vidofilm.a.b)localObject1).g(), ((itman.Vidofilm.a.b)localObject1).f(), UserConfig.getClientUserId()));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new EmptyCell(this.mContext);
        continue;
        paramViewGroup = new DividerCell(this.mContext);
        paramViewGroup.setPadding(AndroidUtilities.dp(72.0F), 0, 0, 0);
        continue;
        paramViewGroup = new TextDetailCell(this.mContext);
        continue;
        paramViewGroup = new TextCell(this.mContext);
        continue;
        paramViewGroup = new UserCell(this.mContext, 61, 0, true);
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        Object localObject1 = Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow");
        localObject1 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), (Drawable)localObject1);
        ((CombinedDrawable)localObject1).setFullsize(true);
        paramViewGroup.setBackgroundDrawable((Drawable)localObject1);
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        localObject1 = (TextInfoPrivacyCell)paramViewGroup;
        Object localObject2 = Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow");
        localObject2 = new CombinedDrawable(new ColorDrawable(Theme.getColor("windowBackgroundGray")), (Drawable)localObject2);
        ((CombinedDrawable)localObject2).setFullsize(true);
        ((TextInfoPrivacyCell)localObject1).setBackgroundDrawable((Drawable)localObject2);
        ((TextInfoPrivacyCell)localObject1).setText(AndroidUtilities.replaceTags(LocaleController.formatString("ConvertGroupInfo", 2131165579, new Object[] { LocaleController.formatPluralString("Members", MessagesController.getInstance().maxMegagroupCount) })));
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        continue;
        paramViewGroup = new AboutLinkCell(this.mContext);
        ((AboutLinkCell)paramViewGroup).setDelegate(new AboutLinkCell.AboutLinkCellDelegate()
        {
          public void didPressUrl(String paramString)
          {
            if (paramString.startsWith("@"))
              MessagesController.openByUserName(paramString.substring(1), ProfileActivity.this, 0);
            Object localObject;
            do
            {
              do
              {
                return;
                if (!paramString.startsWith("#"))
                  continue;
                localObject = new DialogsActivity(null);
                ((DialogsActivity)localObject).setSearchString(paramString);
                ProfileActivity.this.presentFragment((BaseFragment)localObject);
                return;
              }
              while ((!paramString.startsWith("/")) || (ProfileActivity.this.parentLayout.fragmentsStack.size() <= 1));
              localObject = (BaseFragment)ProfileActivity.this.parentLayout.fragmentsStack.get(ProfileActivity.this.parentLayout.fragmentsStack.size() - 2);
            }
            while (!(localObject instanceof ChatActivity));
            ProfileActivity.this.finishFragment();
            ((ChatActivity)localObject).chatActivityEnterView.setCommand(null, paramString, false, false);
          }
        });
        continue;
        paramViewGroup = new TextCell(this.mContext)
        {
          public boolean onTouchEvent(MotionEvent paramMotionEvent)
          {
            if ((Build.VERSION.SDK_INT >= 21) && (getBackground() != null) && ((paramMotionEvent.getAction() == 0) || (paramMotionEvent.getAction() == 2)))
              getBackground().setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
            return super.onTouchEvent(paramMotionEvent);
          }
        };
        ((TextCell)paramViewGroup).changeValueTextColor();
      }
    }
  }

  private class TopView extends View
  {
    private int currentColor;
    private Paint paint = new Paint();

    public TopView(Context arg2)
    {
      super();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int i = getMeasuredHeight() - AndroidUtilities.dp(91.0F);
      paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), ProfileActivity.this.extraHeight + i, this.paint);
      if (ProfileActivity.this.parentLayout != null)
        ProfileActivity.this.parentLayout.drawHeaderShadow(paramCanvas, ProfileActivity.this.extraHeight + i);
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      paramInt2 = View.MeasureSpec.getSize(paramInt1);
      int i = ActionBar.getCurrentActionBarHeight();
      if (ProfileActivity.this.actionBar.getOccupyStatusBar());
      for (paramInt1 = AndroidUtilities.statusBarHeight; ; paramInt1 = 0)
      {
        setMeasuredDimension(paramInt2, paramInt1 + i + AndroidUtilities.dp(91.0F));
        return;
      }
    }

    public void setBackgroundColor(int paramInt)
    {
      if (paramInt != this.currentColor)
      {
        this.paint.setColor(paramInt);
        invalidate();
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ProfileActivity
 * JD-Core Version:    0.6.0
 */