package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.ChannelParticipant;
import org.vidogram.tgnet.TLRPC.ChannelParticipantRole;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantCreator;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantEditor;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantModerator;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantSelf;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantsKicked;
import org.vidogram.tgnet.TLRPC.TL_channelParticipantsRecent;
import org.vidogram.tgnet.TLRPC.TL_channelRoleEditor;
import org.vidogram.tgnet.TLRPC.TL_channelRoleEmpty;
import org.vidogram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.vidogram.tgnet.TLRPC.TL_channels_editAdmin;
import org.vidogram.tgnet.TLRPC.TL_channels_getParticipants;
import org.vidogram.tgnet.TLRPC.TL_channels_kickFromChannel;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.RadioCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class ChannelUsersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chatId = this.arguments.getInt("chat_id");
  private EmptyTextProgressView emptyView;
  private boolean firstLoaded;
  private boolean isAdmin;
  private boolean isMegagroup;
  private boolean isModerator;
  private boolean isPublic;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loadingUsers;
  private ArrayList<TLRPC.ChannelParticipant> participants = new ArrayList();
  private int participantsStartRow;
  private int type = this.arguments.getInt("type");

  public ChannelUsersActivity(Bundle paramBundle)
  {
    super(paramBundle);
    paramBundle = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    boolean bool;
    if (paramBundle != null)
    {
      if (!paramBundle.creator)
        break label122;
      this.isAdmin = true;
      if ((paramBundle.flags & 0x40) != 0)
      {
        bool = true;
        this.isPublic = bool;
        label95: this.isMegagroup = paramBundle.megagroup;
      }
    }
    else
    {
      if (this.type != 0)
        break label137;
      this.participantsStartRow = 0;
    }
    label122: 
    do
    {
      return;
      bool = false;
      break;
      if (!paramBundle.editor)
        break label95;
      this.isModerator = true;
      break label95;
      if (this.type != 1)
        continue;
      i = j;
      if (this.isAdmin)
      {
        i = j;
        if (this.isMegagroup)
          i = 4;
      }
      this.participantsStartRow = i;
      return;
    }
    while (this.type != 2);
    label137: if (this.isAdmin)
      if (!this.isPublic)
        break label201;
    label201: for (i = 2; ; i = 3)
    {
      this.participantsStartRow = i;
      return;
    }
  }

  private int getChannelAdminParticipantType(TLRPC.ChannelParticipant paramChannelParticipant)
  {
    if (((paramChannelParticipant instanceof TLRPC.TL_channelParticipantCreator)) || ((paramChannelParticipant instanceof TLRPC.TL_channelParticipantSelf)))
      return 0;
    if ((paramChannelParticipant instanceof TLRPC.TL_channelParticipantEditor))
      return 1;
    return 2;
  }

  private void getChannelParticipants(int paramInt1, int paramInt2)
  {
    if (this.loadingUsers)
      return;
    this.loadingUsers = true;
    if ((this.emptyView != null) && (!this.firstLoaded))
      this.emptyView.showProgress();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
    localTL_channels_getParticipants.channel = MessagesController.getInputChannel(this.chatId);
    if (this.type == 0)
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsKicked();
    while (true)
    {
      localTL_channels_getParticipants.offset = paramInt1;
      localTL_channels_getParticipants.limit = paramInt2;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_channels_getParticipants, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants;
              if (this.val$error == null)
              {
                localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)this.val$response;
                MessagesController.getInstance().putUsers(localTL_channels_channelParticipants.users, false);
                ChannelUsersActivity.access$702(ChannelUsersActivity.this, localTL_channels_channelParticipants.participants);
              }
              try
              {
                if ((ChannelUsersActivity.this.type == 0) || (ChannelUsersActivity.this.type == 2))
                  Collections.sort(ChannelUsersActivity.this.participants, new Comparator()
                  {
                    public int compare(TLRPC.ChannelParticipant paramChannelParticipant1, TLRPC.ChannelParticipant paramChannelParticipant2)
                    {
                      paramChannelParticipant2 = MessagesController.getInstance().getUser(Integer.valueOf(paramChannelParticipant2.user_id));
                      paramChannelParticipant1 = MessagesController.getInstance().getUser(Integer.valueOf(paramChannelParticipant1.user_id));
                      int i;
                      if ((paramChannelParticipant2 != null) && (paramChannelParticipant2.status != null))
                        if (paramChannelParticipant2.id == UserConfig.getClientUserId())
                          i = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                      while (true)
                      {
                        int j;
                        if ((paramChannelParticipant1 != null) && (paramChannelParticipant1.status != null))
                          if (paramChannelParticipant1.id == UserConfig.getClientUserId())
                            j = ConnectionsManager.getInstance().getCurrentTime() + 50000;
                        while (true)
                        {
                          label91: if ((i > 0) && (j > 0))
                            if (i <= j);
                          label166: 
                          do
                          {
                            do
                            {
                              return 1;
                              i = paramChannelParticipant2.status.expires;
                              break;
                              j = paramChannelParticipant1.status.expires;
                              break label91;
                              if (i < j)
                                return -1;
                              return 0;
                              if ((i >= 0) || (j >= 0))
                                break label166;
                            }
                            while (i > j);
                            if (i < j)
                              return -1;
                            return 0;
                            if (((i < 0) && (j > 0)) || ((i == 0) && (j != 0)))
                              return -1;
                          }
                          while (((j < 0) && (i > 0)) || ((j == 0) && (i != 0)));
                          return 0;
                          j = 0;
                        }
                        i = 0;
                      }
                    }
                  });
                while (true)
                {
                  ChannelUsersActivity.access$1102(ChannelUsersActivity.this, false);
                  ChannelUsersActivity.access$1202(ChannelUsersActivity.this, true);
                  if (ChannelUsersActivity.this.emptyView != null)
                    ChannelUsersActivity.this.emptyView.showTextView();
                  if (ChannelUsersActivity.this.listViewAdapter != null)
                    ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                  return;
                  if (ChannelUsersActivity.this.type != 1)
                    continue;
                  Collections.sort(localTL_channels_channelParticipants.participants, new Comparator()
                  {
                    public int compare(TLRPC.ChannelParticipant paramChannelParticipant1, TLRPC.ChannelParticipant paramChannelParticipant2)
                    {
                      int i = ChannelUsersActivity.this.getChannelAdminParticipantType(paramChannelParticipant1);
                      int j = ChannelUsersActivity.this.getChannelAdminParticipantType(paramChannelParticipant2);
                      if (i > j)
                        return 1;
                      if (i < j)
                        return -1;
                      return 0;
                    }
                  });
                }
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
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, this.classGuid);
      return;
      if (this.type == 1)
      {
        localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
        continue;
      }
      if (this.type != 2)
        continue;
      localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
    }
  }

  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.type == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("ChannelBlockedUsers", 2131165455));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            ChannelUsersActivity.this.finishFragment();
        }
      });
      this.fragmentView = new FrameLayout(paramContext);
      this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (this.type == 0)
      {
        if (!this.isMegagroup)
          break label384;
        this.emptyView.setText(LocaleController.getString("NoBlockedGroup", 2131166023));
      }
      label132: localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setEmptyView(this.emptyView);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      RecyclerListView localRecyclerListView = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listViewAdapter = paramContext;
      localRecyclerListView.setAdapter(paramContext);
      paramContext = this.listView;
      if (!LocaleController.isRTL)
        break label403;
      label225: paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          boolean bool = true;
          label107: Object localObject;
          if (ChannelUsersActivity.this.type == 2)
            if (ChannelUsersActivity.this.isAdmin)
            {
              if (paramInt != 0)
                break label214;
              paramView = new Bundle();
              paramView.putBoolean("onlyUsers", true);
              paramView.putBoolean("destroyAfterSelect", true);
              paramView.putBoolean("returnAsResult", true);
              paramView.putBoolean("needForwardCount", false);
              paramView.putString("selectAlertString", LocaleController.getString("ChannelAddTo", 2131165445));
              paramView = new ContactsActivity(paramView);
              paramView.setDelegate(new ContactsActivity.ContactsActivityDelegate()
              {
                public void didSelectContact(TLRPC.User paramUser, String paramString)
                {
                  MessagesController localMessagesController = MessagesController.getInstance();
                  int j = ChannelUsersActivity.this.chatId;
                  if (paramString != null);
                  for (int i = Utilities.parseInt(paramString).intValue(); ; i = 0)
                  {
                    localMessagesController.addUserToChat(j, paramUser, null, i, null, ChannelUsersActivity.this);
                    return;
                  }
                }
              });
              ChannelUsersActivity.this.presentFragment(paramView);
            }
            else
            {
              localObject = null;
              paramView = (View)localObject;
              if (paramInt >= ChannelUsersActivity.this.participantsStartRow)
              {
                paramView = (View)localObject;
                if (paramInt < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow)
                  paramView = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramInt - ChannelUsersActivity.this.participantsStartRow);
              }
              if (paramView != null)
              {
                localObject = new Bundle();
                ((Bundle)localObject).putInt("user_id", paramView.user_id);
                ChannelUsersActivity.this.presentFragment(new ProfileActivity((Bundle)localObject));
              }
            }
          label214: label480: label612: label615: 
          while (true)
          {
            return;
            if ((ChannelUsersActivity.this.isPublic) || (paramInt != 1))
              break label107;
            ChannelUsersActivity.this.presentFragment(new GroupInviteActivity(ChannelUsersActivity.this.chatId));
            break label107;
            if ((ChannelUsersActivity.this.type != 1) || (!ChannelUsersActivity.this.isAdmin))
              break label107;
            if ((ChannelUsersActivity.this.isMegagroup) && ((paramInt == 1) || (paramInt == 2)))
            {
              paramView = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
              if (paramView == null)
                break;
              if ((paramInt == 1) && (!paramView.democracy))
              {
                paramView.democracy = true;
                paramInt = 1;
              }
            }
            while (true)
            {
              if (paramInt == 0)
                break label615;
              MessagesController.getInstance().toogleChannelInvites(ChannelUsersActivity.this.chatId, paramView.democracy);
              int i = ChannelUsersActivity.this.listView.getChildCount();
              paramInt = 0;
              label369: if (paramInt < i)
              {
                localObject = ChannelUsersActivity.this.listView.getChildAt(paramInt);
                if ((localObject instanceof RadioCell))
                {
                  int j = ((Integer)((View)localObject).getTag()).intValue();
                  localObject = (RadioCell)localObject;
                  if (((j != 0) || (!paramView.democracy)) && ((j != 1) || (paramView.democracy)))
                    break label480;
                }
              }
              for (bool = true; ; bool = false)
              {
                ((RadioCell)localObject).setChecked(bool, true);
                paramInt += 1;
                break label369;
                break;
                if ((paramInt != 2) || (!paramView.democracy))
                  break label612;
                paramView.democracy = false;
                paramInt = 1;
                break label335;
              }
              if (paramInt != ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size())
                break;
              paramView = new Bundle();
              paramView.putBoolean("onlyUsers", true);
              paramView.putBoolean("destroyAfterSelect", true);
              paramView.putBoolean("returnAsResult", true);
              paramView.putBoolean("needForwardCount", false);
              if (!ChannelUsersActivity.this.isMegagroup);
              while (true)
              {
                paramView.putBoolean("addingToChannel", bool);
                paramView.putString("selectAlertString", LocaleController.getString("ChannelAddUserAdminAlert", 2131165446));
                paramView = new ContactsActivity(paramView);
                paramView.setDelegate(new ContactsActivity.ContactsActivityDelegate()
                {
                  public void didSelectContact(TLRPC.User paramUser, String paramString)
                  {
                    ChannelUsersActivity.this.setUserChannelRole(paramUser, new TLRPC.TL_channelRoleEditor());
                  }
                });
                ChannelUsersActivity.this.presentFragment(paramView);
                return;
                bool = false;
              }
              paramInt = 0;
            }
          }
        }
      });
      if ((this.isAdmin) || ((this.isModerator) && (this.type == 2)) || ((this.isMegagroup) && (this.type == 0)))
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramView, int paramInt)
          {
            if (ChannelUsersActivity.this.getParentActivity() == null)
              return false;
            if ((paramInt >= ChannelUsersActivity.this.participantsStartRow) && (paramInt < ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow));
            for (TLRPC.ChannelParticipant localChannelParticipant = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramInt - ChannelUsersActivity.this.participantsStartRow); ; localChannelParticipant = null)
            {
              AlertDialog.Builder localBuilder;
              if (localChannelParticipant != null)
              {
                if (localChannelParticipant.user_id == UserConfig.getClientUserId())
                  return false;
                localBuilder = new AlertDialog.Builder(ChannelUsersActivity.this.getParentActivity());
                if (ChannelUsersActivity.this.type == 0)
                {
                  paramView = new CharSequence[1];
                  paramView[0] = LocaleController.getString("Unblock", 2131166530);
                }
              }
              while (true)
              {
                localBuilder.setItems(paramView, new DialogInterface.OnClickListener(localChannelParticipant)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    if (paramInt == 0)
                    {
                      if (ChannelUsersActivity.this.type != 0)
                        break label109;
                      ChannelUsersActivity.this.participants.remove(this.val$finalParticipant);
                      ChannelUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                      paramDialogInterface = new TLRPC.TL_channels_kickFromChannel();
                      paramDialogInterface.kicked = false;
                      paramDialogInterface.user_id = MessagesController.getInputUser(this.val$finalParticipant.user_id);
                      paramDialogInterface.channel = MessagesController.getInputChannel(ChannelUsersActivity.this.chatId);
                      ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
                      {
                        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                        {
                          if (paramTLObject != null)
                          {
                            paramTLObject = (TLRPC.Updates)paramTLObject;
                            MessagesController.getInstance().processUpdates(paramTLObject, false);
                            if (!paramTLObject.chats.isEmpty())
                              AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
                              {
                                public void run()
                                {
                                  TLRPC.Chat localChat = (TLRPC.Chat)this.val$updates.chats.get(0);
                                  MessagesController.getInstance().loadFullChat(localChat.id, 0, true);
                                }
                              }
                              , 1000L);
                          }
                        }
                      });
                    }
                    label109: 
                    do
                    {
                      return;
                      if (ChannelUsersActivity.this.type != 1)
                        continue;
                      ChannelUsersActivity.this.setUserChannelRole(MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), new TLRPC.TL_channelRoleEmpty());
                      return;
                    }
                    while (ChannelUsersActivity.this.type != 2);
                    MessagesController.getInstance().deleteUserFromChat(ChannelUsersActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(this.val$finalParticipant.user_id)), null);
                  }
                });
                ChannelUsersActivity.this.showDialog(localBuilder.create());
                return true;
                if (ChannelUsersActivity.this.type == 1)
                {
                  paramView = new CharSequence[1];
                  paramView[0] = LocaleController.getString("ChannelRemoveUserAdmin", 2131165513);
                  continue;
                }
                if (ChannelUsersActivity.this.type == 2)
                {
                  paramView = new CharSequence[1];
                  paramView[0] = LocaleController.getString("ChannelRemoveUser", 2131165512);
                  continue;
                  return false;
                }
                paramView = null;
              }
            }
          }
        });
      if (!this.loadingUsers)
        break label408;
      this.emptyView.showProgress();
    }
    while (true)
    {
      return this.fragmentView;
      if (this.type == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("ChannelAdministrators", 2131165450));
        break;
      }
      if (this.type != 2)
        break;
      this.actionBar.setTitle(LocaleController.getString("ChannelMembers", 2131165477));
      break;
      label384: this.emptyView.setText(LocaleController.getString("NoBlocked", 2131166022));
      break label132;
      label403: i = 2;
      break label225;
      label408: this.emptyView.showTextView();
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.chatInfoDidLoaded) && (((TLRPC.ChatFull)paramArrayOfObject[0]).id == this.chatId))
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          ChannelUsersActivity.this.getChannelParticipants(0, 200);
        }
      });
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    7 local7 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = ChannelUsersActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = ChannelUsersActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof UserCell))
            ((UserCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { UserCell.class, TextSettingsCell.class, TextCell.class, RadioCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueImageView" }, null, null, null, "windowBackgroundWhiteGrayIcon");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local7, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, local7, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return (ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localObject1, localObject2, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local7, "avatar_backgroundPink"), new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCell.class }, new String[] { "imageView" }, null, null, null, "windowBackgroundWhiteGrayIcon") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    getChannelParticipants(0, 200);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
  }

  public void setUserChannelRole(TLRPC.User paramUser, TLRPC.ChannelParticipantRole paramChannelParticipantRole)
  {
    if ((paramUser == null) || (paramChannelParticipantRole == null))
      return;
    TLRPC.TL_channels_editAdmin localTL_channels_editAdmin = new TLRPC.TL_channels_editAdmin();
    localTL_channels_editAdmin.channel = MessagesController.getInputChannel(this.chatId);
    localTL_channels_editAdmin.user_id = MessagesController.getInputUser(paramUser);
    localTL_channels_editAdmin.role = paramChannelParticipantRole;
    ConnectionsManager.getInstance().sendRequest(localTL_channels_editAdmin, new RequestDelegate(localTL_channels_editAdmin)
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
              MessagesController.getInstance().loadFullChat(ChannelUsersActivity.this.chatId, 0, true);
            }
          }
          , 1000L);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
        {
          public void run()
          {
            boolean bool = true;
            TLRPC.TL_error localTL_error = this.val$error;
            ChannelUsersActivity localChannelUsersActivity = ChannelUsersActivity.this;
            TLRPC.TL_channels_editAdmin localTL_channels_editAdmin = ChannelUsersActivity.4.this.val$req;
            if (!ChannelUsersActivity.this.isMegagroup);
            while (true)
            {
              AlertsCreator.processError(localTL_error, localChannelUsersActivity, localTL_channels_editAdmin, new Object[] { Boolean.valueOf(bool) });
              return;
              bool = false;
            }
          }
        });
      }
    });
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
      int i = 1;
      if (((ChannelUsersActivity.this.participants.isEmpty()) && (ChannelUsersActivity.this.type == 0)) || ((ChannelUsersActivity.this.loadingUsers) && (!ChannelUsersActivity.this.firstLoaded)))
        return 0;
      if (ChannelUsersActivity.this.type == 1)
      {
        int k = ChannelUsersActivity.this.participants.size();
        if (ChannelUsersActivity.this.isAdmin)
          i = 2;
        if ((ChannelUsersActivity.this.isAdmin) && (ChannelUsersActivity.this.isMegagroup));
        for (int j = 4; ; j = 0)
          return k + i + j;
      }
      return ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow + 1;
    }

    public int getItemViewType(int paramInt)
    {
      int i = 3;
      if (ChannelUsersActivity.this.type == 1)
      {
        if (ChannelUsersActivity.this.isAdmin)
        {
          if (ChannelUsersActivity.this.isMegagroup)
          {
            if (paramInt == 0)
              i = 5;
            do
            {
              return i;
              if ((paramInt == 1) || (paramInt == 2))
                return 6;
            }
            while (paramInt == 3);
          }
          if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size())
            return 4;
          if (paramInt == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size() + 1)
            return 1;
        }
      }
      else if ((ChannelUsersActivity.this.type == 2) && (ChannelUsersActivity.this.isAdmin))
        if (!ChannelUsersActivity.this.isPublic)
        {
          if ((paramInt == 0) || (paramInt == 1))
            return 2;
          if (paramInt == 2)
            return 1;
        }
        else
        {
          if (paramInt == 0)
            return 2;
          if (paramInt == 1)
            return 1;
        }
      if (paramInt == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow)
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int j = 0;
      int k = 1;
      int i = paramViewHolder.getAdapterPosition();
      if (ChannelUsersActivity.this.type == 2)
        if (ChannelUsersActivity.this.isAdmin)
        {
          if (!ChannelUsersActivity.this.isPublic)
          {
            if ((i == 0) || (i == 1))
              j = 1;
            do
              return j;
            while (i == 2);
          }
        }
        else
          label59: if ((i == ChannelUsersActivity.this.participants.size() + ChannelUsersActivity.this.participantsStartRow) || (((TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(i - ChannelUsersActivity.this.participantsStartRow)).user_id == UserConfig.getClientUserId()))
            break label239;
      label239: for (j = k; ; j = 0)
      {
        return j;
        if (i == 0)
          return true;
        if (i != 1)
          break label59;
        return false;
        if (ChannelUsersActivity.this.type != 1)
          break label59;
        if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size())
          return ChannelUsersActivity.this.isAdmin;
        if (i == ChannelUsersActivity.this.participantsStartRow + ChannelUsersActivity.this.participants.size() + 1)
          break;
        if ((!ChannelUsersActivity.this.isMegagroup) || (!ChannelUsersActivity.this.isAdmin) || (i >= 4))
          break label59;
        if ((i == 1) || (i == 2));
        for (j = 1; ; j = 0)
          return j;
      }
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      Object localObject1;
      Object localObject2;
      switch (paramViewHolder.getItemViewType())
      {
      case 3:
      default:
      case 0:
        do
        {
          return;
          localObject1 = (UserCell)paramViewHolder.itemView;
          paramViewHolder = (TLRPC.ChannelParticipant)ChannelUsersActivity.this.participants.get(paramInt - ChannelUsersActivity.this.participantsStartRow);
          localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(paramViewHolder.user_id));
        }
        while (localObject2 == null);
        if (ChannelUsersActivity.this.type == 0)
        {
          if ((((TLRPC.User)localObject2).phone != null) && (((TLRPC.User)localObject2).phone.length() != 0));
          for (paramViewHolder = b.a().e("+" + ((TLRPC.User)localObject2).phone); ; paramViewHolder = LocaleController.getString("NumberUnknown", 2131166152))
          {
            ((UserCell)localObject1).setData((TLObject)localObject2, null, paramViewHolder, 0);
            return;
          }
        }
        if (ChannelUsersActivity.this.type == 1)
        {
          if ((!(paramViewHolder instanceof TLRPC.TL_channelParticipantCreator)) && (!(paramViewHolder instanceof TLRPC.TL_channelParticipantSelf)))
            break;
          paramViewHolder = LocaleController.getString("ChannelCreator", 2131165461);
        }
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      }
      while (true)
      {
        ((UserCell)localObject1).setData((TLObject)localObject2, null, paramViewHolder, 0);
        return;
        if ((paramViewHolder instanceof TLRPC.TL_channelParticipantModerator))
        {
          paramViewHolder = LocaleController.getString("ChannelModerator", 2131165501);
          continue;
        }
        if ((paramViewHolder instanceof TLRPC.TL_channelParticipantEditor))
        {
          paramViewHolder = LocaleController.getString("ChannelEditor", 2131165467);
          continue;
          if (ChannelUsersActivity.this.type != 2)
            break;
          ((UserCell)localObject1).setData((TLObject)localObject2, null, null, 0);
          return;
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (ChannelUsersActivity.this.type == 0)
          {
            paramViewHolder.setText(String.format("%1$s\n\n%2$s", new Object[] { LocaleController.getString("NoBlockedGroup", 2131166023), LocaleController.getString("UnblockText", 2131166531) }));
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
            return;
          }
          if (ChannelUsersActivity.this.type == 1)
          {
            if (ChannelUsersActivity.this.isAdmin)
            {
              if (ChannelUsersActivity.this.isMegagroup)
                paramViewHolder.setText(LocaleController.getString("MegaAdminsInfo", 2131165940));
              while (true)
              {
                paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
                return;
                paramViewHolder.setText(LocaleController.getString("ChannelAdminsInfo", 2131165451));
              }
            }
            paramViewHolder.setText("");
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
            return;
          }
          if (ChannelUsersActivity.this.type != 2)
            break;
          if (((!ChannelUsersActivity.this.isPublic) && (paramInt == 2)) || ((paramInt == 1) && (ChannelUsersActivity.this.isAdmin)))
          {
            if (ChannelUsersActivity.this.isMegagroup)
              paramViewHolder.setText("");
            while (true)
            {
              paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
              return;
              paramViewHolder.setText(LocaleController.getString("ChannelMembersInfo", 2131165478));
            }
          }
          paramViewHolder.setText("");
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
          paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
          if (ChannelUsersActivity.this.type == 2)
          {
            if (paramInt == 0)
            {
              paramViewHolder.setText(LocaleController.getString("AddMember", 2131165281), true);
              return;
            }
            if (paramInt != 1)
              break;
            paramViewHolder.setText(LocaleController.getString("ChannelInviteViaLink", 2131165469), false);
            return;
          }
          if (ChannelUsersActivity.this.type != 1)
            break;
          paramViewHolder.setTextAndIcon(LocaleController.getString("ChannelAddAdmin", 2131165443), 2130837904, false);
          return;
          ((TextCell)paramViewHolder.itemView).setTextAndIcon(LocaleController.getString("ChannelAddAdmin", 2131165443), 2130837904);
          return;
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("WhoCanAddMembers", 2131166622));
          return;
          paramViewHolder = (RadioCell)paramViewHolder.itemView;
          localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(ChannelUsersActivity.this.chatId));
          if (paramInt == 1)
          {
            paramViewHolder.setTag(Integer.valueOf(0));
            localObject2 = LocaleController.getString("WhoCanAddMembersAllMembers", 2131166624);
            if ((localObject1 != null) && (((TLRPC.Chat)localObject1).democracy));
            for (bool = true; ; bool = false)
            {
              paramViewHolder.setText((String)localObject2, bool, true);
              return;
            }
          }
          if (paramInt != 2)
            break;
          paramViewHolder.setTag(Integer.valueOf(1));
          localObject2 = LocaleController.getString("WhoCanAddMembersAdmins", 2131166623);
          if ((localObject1 != null) && (!((TLRPC.Chat)localObject1).democracy));
          while (true)
          {
            paramViewHolder.setText((String)localObject2, bool, false);
            return;
            bool = false;
          }
        }
        paramViewHolder = null;
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
      case 3:
      case 4:
      case 5:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new TextCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChannelUsersActivity
 * JD-Core Version:    0.6.0
 */