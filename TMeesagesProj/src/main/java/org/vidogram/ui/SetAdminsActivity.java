package org.vidogram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.TL_chatParticipant;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantCreator;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class SetAdminsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int allAdminsInfoRow;
  private int allAdminsRow;
  private TLRPC.Chat chat;
  private int chat_id;
  private EmptyTextProgressView emptyView;
  private TLRPC.ChatFull info;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private ArrayList<TLRPC.ChatParticipant> participants = new ArrayList();
  private int rowCount;
  private SearchAdapter searchAdapter;
  private ActionBarMenuItem searchItem;
  private boolean searchWas;
  private boolean searching;
  private int usersEndRow;
  private int usersStartRow;

  public SetAdminsActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chat_id = paramBundle.getInt("chat_id");
  }

  private int getChatAdminParticipantType(TLRPC.ChatParticipant paramChatParticipant)
  {
    if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantCreator))
      return 0;
    if ((paramChatParticipant instanceof TLRPC.TL_chatParticipantAdmin))
      return 1;
    return 2;
  }

  private void updateChatParticipants()
  {
    if (this.info == null);
    do
      return;
    while (this.participants.size() == this.info.participants.participants.size());
    this.participants.clear();
    this.participants.addAll(this.info.participants.participants);
    try
    {
      Collections.sort(this.participants, new Comparator()
      {
        public int compare(TLRPC.ChatParticipant paramChatParticipant1, TLRPC.ChatParticipant paramChatParticipant2)
        {
          int i = SetAdminsActivity.this.getChatAdminParticipantType(paramChatParticipant1);
          int j = SetAdminsActivity.this.getChatAdminParticipantType(paramChatParticipant2);
          if (i > j)
            return 1;
          if (i < j)
            return -1;
          if (i == j)
          {
            paramChatParticipant2 = MessagesController.getInstance().getUser(Integer.valueOf(paramChatParticipant2.user_id));
            paramChatParticipant1 = MessagesController.getInstance().getUser(Integer.valueOf(paramChatParticipant1.user_id));
            if ((paramChatParticipant2 == null) || (paramChatParticipant2.status == null))
              break label204;
          }
          label204: for (i = paramChatParticipant2.status.expires; ; i = 0)
          {
            if ((paramChatParticipant1 != null) && (paramChatParticipant1.status != null));
            for (j = paramChatParticipant1.status.expires; ; j = 0)
            {
              if ((i > 0) && (j > 0))
              {
                if (i > j)
                  break;
                if (i < j)
                  return -1;
                return 0;
              }
              if ((i < 0) && (j < 0))
              {
                if (i > j)
                  break;
                if (i < j)
                  return -1;
                return 0;
              }
              if (((i < 0) && (j > 0)) || ((i == 0) && (j != 0)))
                return -1;
              if (((j < 0) && (i > 0)) || ((j == 0) && (i != 0)))
                break;
              return 0;
            }
          }
        }
      });
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void updateRowsIds()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.allAdminsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.allAdminsInfoRow = i;
    if (this.info != null)
    {
      this.usersStartRow = this.rowCount;
      this.rowCount += this.participants.size();
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.usersEndRow = i;
      if ((this.searchItem != null) && (!this.searchWas))
        this.searchItem.setVisibility(0);
    }
    while (true)
    {
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      return;
      this.usersStartRow = -1;
      this.usersEndRow = -1;
      if (this.searchItem == null)
        continue;
      this.searchItem.setVisibility(8);
    }
  }

  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SetAdminsTitle", 2131166444));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          SetAdminsActivity.this.finishFragment();
      }
    });
    this.searchItem = this.actionBar.createMenu().addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, false);
        SetAdminsActivity.access$302(SetAdminsActivity.this, false);
        if (SetAdminsActivity.this.listView != null)
        {
          SetAdminsActivity.this.listView.setEmptyView(null);
          SetAdminsActivity.this.emptyView.setVisibility(8);
          if (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.listAdapter)
            SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.listAdapter);
        }
        if (SetAdminsActivity.this.searchAdapter != null)
          SetAdminsActivity.this.searchAdapter.search(null);
      }

      public void onSearchExpand()
      {
        SetAdminsActivity.access$002(SetAdminsActivity.this, true);
        SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
      }

      public void onTextChanged(EditText paramEditText)
      {
        paramEditText = paramEditText.getText().toString();
        if (paramEditText.length() != 0)
        {
          SetAdminsActivity.access$302(SetAdminsActivity.this, true);
          if ((SetAdminsActivity.this.searchAdapter != null) && (SetAdminsActivity.this.listView.getAdapter() != SetAdminsActivity.this.searchAdapter))
          {
            SetAdminsActivity.this.listView.setAdapter(SetAdminsActivity.this.searchAdapter);
            SetAdminsActivity.this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          }
          if ((SetAdminsActivity.this.emptyView != null) && (SetAdminsActivity.this.listView.getEmptyView() != SetAdminsActivity.this.emptyView))
          {
            SetAdminsActivity.this.emptyView.showTextView();
            SetAdminsActivity.this.listView.setEmptyView(SetAdminsActivity.this.emptyView);
          }
        }
        if (SetAdminsActivity.this.searchAdapter != null)
          SetAdminsActivity.this.searchAdapter.search(paramEditText);
      }
    });
    this.searchItem.getSearchField().setHint(LocaleController.getString("Search", 2131166381));
    this.listAdapter = new ListAdapter(paramContext);
    this.searchAdapter = new SearchAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        boolean bool3 = true;
        boolean bool2 = true;
        UserCell localUserCell;
        int i;
        if ((SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter) || ((paramInt >= SetAdminsActivity.this.usersStartRow) && (paramInt < SetAdminsActivity.this.usersEndRow)))
        {
          localUserCell = (UserCell)paramView;
          SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)
          {
            paramView = SetAdminsActivity.this.searchAdapter.getItem(paramInt);
            i = 0;
            if (i >= SetAdminsActivity.this.participants.size())
              break label632;
            if (((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(i)).user_id != paramView.user_id);
          }
        }
        while (true)
        {
          label150: if ((i != -1) && (!(paramView instanceof TLRPC.TL_chatParticipantCreator)))
          {
            if (!(paramView instanceof TLRPC.TL_chatParticipant))
              break label440;
            localObject = new TLRPC.TL_chatParticipantAdmin();
            ((TLRPC.ChatParticipant)localObject).user_id = paramView.user_id;
            ((TLRPC.ChatParticipant)localObject).date = paramView.date;
            ((TLRPC.ChatParticipant)localObject).inviter_id = paramView.inviter_id;
            label205: SetAdminsActivity.this.participants.set(i, localObject);
            i = SetAdminsActivity.this.info.participants.participants.indexOf(paramView);
            if (i != -1)
              SetAdminsActivity.this.info.participants.participants.set(i, localObject);
            if (SetAdminsActivity.this.listView.getAdapter() == SetAdminsActivity.this.searchAdapter)
              SetAdminsActivity.SearchAdapter.access$1300(SetAdminsActivity.this.searchAdapter).set(paramInt, localObject);
            if (((localObject instanceof TLRPC.TL_chatParticipant)) && ((SetAdminsActivity.this.chat == null) || (SetAdminsActivity.this.chat.admins_enabled)))
              break label479;
            bool1 = true;
            label333: localUserCell.setChecked(bool1, true);
            if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
            {
              paramView = MessagesController.getInstance();
              paramInt = SetAdminsActivity.this.chat_id;
              i = ((TLRPC.ChatParticipant)localObject).user_id;
              if ((localObject instanceof TLRPC.TL_chatParticipant))
                break label485;
              bool1 = bool2;
              paramView.toggleUserAdmin(paramInt, i, bool1);
            }
          }
          label394: label440: label479: label485: 
          do
          {
            do
            {
              return;
              i += 1;
              break;
              paramView = SetAdminsActivity.this.participants;
              i = paramInt - SetAdminsActivity.this.usersStartRow;
              paramView = (TLRPC.ChatParticipant)paramView.get(i);
              break label150;
              localObject = new TLRPC.TL_chatParticipant();
              ((TLRPC.ChatParticipant)localObject).user_id = paramView.user_id;
              ((TLRPC.ChatParticipant)localObject).date = paramView.date;
              ((TLRPC.ChatParticipant)localObject).inviter_id = paramView.inviter_id;
              break label205;
              bool1 = false;
              break label333;
              bool1 = false;
              break label394;
            }
            while (paramInt != SetAdminsActivity.this.allAdminsRow);
            SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          }
          while (SetAdminsActivity.this.chat == null);
          Object localObject = SetAdminsActivity.this.chat;
          if (!SetAdminsActivity.this.chat.admins_enabled)
          {
            bool1 = true;
            ((TLRPC.Chat)localObject).admins_enabled = bool1;
            paramView = (TextCheckCell)paramView;
            if (SetAdminsActivity.this.chat.admins_enabled)
              break label626;
          }
          label626: for (boolean bool1 = bool3; ; bool1 = false)
          {
            paramView.setChecked(bool1);
            MessagesController.getInstance().toggleAdminMode(SetAdminsActivity.this.chat_id, SetAdminsActivity.this.chat.admins_enabled);
            return;
            bool1 = false;
            break;
          }
          label632: i = -1;
        }
      }
    });
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setVisibility(8);
    this.emptyView.setShowAtCenter(true);
    this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.emptyView.showTextView();
    updateRowsIds();
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      paramArrayOfObject = (TLRPC.ChatFull)paramArrayOfObject[0];
      if (paramArrayOfObject.id == this.chat_id)
      {
        this.info = paramArrayOfObject;
        updateChatParticipants();
        updateRowsIds();
      }
    }
    while (true)
    {
      return;
      if (paramInt != NotificationCenter.updateInterfaces)
        continue;
      int j = ((Integer)paramArrayOfObject[0]).intValue();
      if ((((j & 0x2) == 0) && ((j & 0x1) == 0) && ((j & 0x4) == 0)) || (this.listView == null))
        break;
      int k = this.listView.getChildCount();
      paramInt = i;
      while (paramInt < k)
      {
        paramArrayOfObject = this.listView.getChildAt(paramInt);
        if ((paramArrayOfObject instanceof UserCell))
          ((UserCell)paramArrayOfObject).update(j);
        paramInt += 1;
      }
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    5 local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = SetAdminsActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = SetAdminsActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof UserCell))
            ((UserCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextCheckCell.class, UserCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareUnchecked");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareDisabled");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareBackground");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, null, null, null, "checkboxSquareCheck");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local5, "windowBackgroundWhiteGrayText");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusOnlineColor" }, null, null, local5, "windowBackgroundWhiteBlueText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return (ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localObject1, localObject2, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localThemeDescription24, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
  }

  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    updateChatParticipants();
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
      return SetAdminsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == SetAdminsActivity.this.allAdminsRow)
        return 0;
      if ((paramInt == SetAdminsActivity.this.allAdminsInfoRow) || (paramInt == SetAdminsActivity.this.usersEndRow))
        return 1;
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      if (i == SetAdminsActivity.this.allAdminsRow)
        return true;
      return (i >= SetAdminsActivity.this.usersStartRow) && (i < SetAdminsActivity.this.usersEndRow) && (!((TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(i - SetAdminsActivity.this.usersStartRow) instanceof TLRPC.TL_chatParticipantCreator));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool2 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
        do
        {
          return;
          paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
          SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
          localObject = LocaleController.getString("SetAdminsAll", 2131166441);
          if ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled));
          for (bool1 = true; ; bool1 = false)
          {
            paramViewHolder.setTextAndCheck((String)localObject, bool1, false);
            return;
          }
          paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
          if (paramInt != SetAdminsActivity.this.allAdminsInfoRow)
            continue;
          if (SetAdminsActivity.this.chat.admins_enabled)
            paramViewHolder.setText(LocaleController.getString("SetAdminsNotAllInfo", 2131166443));
          while (SetAdminsActivity.this.usersStartRow != -1)
          {
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
            return;
            paramViewHolder.setText(LocaleController.getString("SetAdminsAllInfo", 2131166442));
          }
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        while (paramInt != SetAdminsActivity.this.usersEndRow);
        paramViewHolder.setText("");
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
        return;
      case 2:
      }
      paramViewHolder = (UserCell)paramViewHolder.itemView;
      Object localObject = (TLRPC.ChatParticipant)SetAdminsActivity.this.participants.get(paramInt - SetAdminsActivity.this.usersStartRow);
      paramViewHolder.setData(MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id)), null, null, 0);
      SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
      if ((!(localObject instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled)));
      for (boolean bool1 = true; ; bool1 = false)
      {
        paramViewHolder.setChecked(bool1, false);
        if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
        {
          bool1 = bool2;
          if (((TLRPC.ChatParticipant)localObject).user_id != UserConfig.getClientUserId());
        }
        else
        {
          bool1 = true;
        }
        paramViewHolder.setCheckDisabled(bool1);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new UserCell(this.mContext, 1, 2, false);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }

  public class SearchAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private ArrayList<TLRPC.ChatParticipant> searchResult = new ArrayList();
    private ArrayList<CharSequence> searchResultNames = new ArrayList();
    private Timer searchTimer;

    public SearchAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    private void processSearch(String paramString)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramString)
      {
        public void run()
        {
          ArrayList localArrayList = new ArrayList();
          localArrayList.addAll(SetAdminsActivity.this.participants);
          Utilities.searchQueue.postRunnable(new Runnable(localArrayList)
          {
            public void run()
            {
              String str2 = SetAdminsActivity.SearchAdapter.2.this.val$query.trim().toLowerCase();
              if (str2.length() == 0)
              {
                SetAdminsActivity.SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
                return;
              }
              String str1 = LocaleController.getInstance().getTranslitString(str2);
              if ((str2.equals(str1)) || (str1.length() == 0))
                str1 = null;
              while (true)
              {
                int i;
                String[] arrayOfString;
                ArrayList localArrayList1;
                ArrayList localArrayList2;
                int j;
                label131: TLRPC.ChatParticipant localChatParticipant;
                TLRPC.User localUser;
                if (str1 != null)
                {
                  i = 1;
                  arrayOfString = new String[i + 1];
                  arrayOfString[0] = str2;
                  if (str1 != null)
                    arrayOfString[1] = str1;
                  localArrayList1 = new ArrayList();
                  localArrayList2 = new ArrayList();
                  j = 0;
                  if (j >= this.val$contactsCopy.size())
                    break label483;
                  localChatParticipant = (TLRPC.ChatParticipant)this.val$contactsCopy.get(j);
                  localUser = MessagesController.getInstance().getUser(Integer.valueOf(localChatParticipant.user_id));
                  if (localUser.id != UserConfig.getClientUserId())
                    break label194;
                }
                label194: label344: label473: label481: 
                while (true)
                {
                  j += 1;
                  break label131;
                  i = 0;
                  break;
                  String str3 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                  str2 = LocaleController.getInstance().getTranslitString(str3);
                  str1 = str2;
                  if (str3.equals(str2))
                    str1 = null;
                  int n = arrayOfString.length;
                  int m = 0;
                  int k = 0;
                  while (true)
                  {
                    if (k >= n)
                      break label481;
                    str2 = arrayOfString[k];
                    if ((str3.startsWith(str2)) || (str3.contains(" " + str2)) || ((str1 != null) && ((str1.startsWith(str2)) || (str1.contains(" " + str2)))))
                    {
                      i = 1;
                      if (i == 0)
                        break label473;
                      if (i != 1)
                        break label417;
                      localArrayList2.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str2));
                    }
                    while (true)
                    {
                      localArrayList1.add(localChatParticipant);
                      break;
                      i = m;
                      if (localUser.username == null)
                        break label344;
                      i = m;
                      if (!localUser.username.startsWith(str2))
                        break label344;
                      i = 2;
                      break label344;
                      localArrayList2.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str2));
                    }
                    k += 1;
                    m = i;
                  }
                }
                label417: label483: SetAdminsActivity.SearchAdapter.this.updateSearchResults(localArrayList1, localArrayList2);
                return;
              }
            }
          });
        }
      });
    }

    private void updateSearchResults(ArrayList<TLRPC.ChatParticipant> paramArrayList, ArrayList<CharSequence> paramArrayList1)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramArrayList1)
      {
        public void run()
        {
          SetAdminsActivity.SearchAdapter.access$1302(SetAdminsActivity.SearchAdapter.this, this.val$users);
          SetAdminsActivity.SearchAdapter.access$2102(SetAdminsActivity.SearchAdapter.this, this.val$names);
          SetAdminsActivity.SearchAdapter.this.notifyDataSetChanged();
        }
      });
    }

    public TLRPC.ChatParticipant getItem(int paramInt)
    {
      return (TLRPC.ChatParticipant)this.searchResult.get(paramInt);
    }

    public int getItemCount()
    {
      return this.searchResult.size();
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject2 = null;
      boolean bool2 = false;
      TLRPC.ChatParticipant localChatParticipant = getItem(paramInt);
      TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(localChatParticipant.user_id));
      String str = localUser.username;
      Object localObject1;
      if (paramInt < this.searchResult.size())
      {
        localObject1 = (CharSequence)this.searchResultNames.get(paramInt);
        if ((localObject1 == null) || (str == null) || (str.length() <= 0) || (!((CharSequence)localObject1).toString().startsWith("@" + str)));
      }
      while (true)
      {
        paramViewHolder = (UserCell)paramViewHolder.itemView;
        paramViewHolder.setData(localUser, (CharSequence)localObject2, (CharSequence)localObject1, 0);
        SetAdminsActivity.access$902(SetAdminsActivity.this, MessagesController.getInstance().getChat(Integer.valueOf(SetAdminsActivity.this.chat_id)));
        if ((!(localChatParticipant instanceof TLRPC.TL_chatParticipant)) || ((SetAdminsActivity.this.chat != null) && (!SetAdminsActivity.this.chat.admins_enabled)));
        for (boolean bool1 = true; ; bool1 = false)
        {
          paramViewHolder.setChecked(bool1, false);
          if ((SetAdminsActivity.this.chat != null) && (SetAdminsActivity.this.chat.admins_enabled))
          {
            bool1 = bool2;
            if (localChatParticipant.user_id != UserConfig.getClientUserId());
          }
          else
          {
            bool1 = true;
          }
          paramViewHolder.setCheckDisabled(bool1);
          return;
        }
        str = null;
        localObject2 = localObject1;
        localObject1 = str;
        continue;
        localObject1 = null;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new UserCell(this.mContext, 1, 2, false));
    }

    public void search(String paramString)
    {
      try
      {
        if (this.searchTimer != null)
          this.searchTimer.cancel();
        if (paramString == null)
        {
          this.searchResult.clear();
          this.searchResultNames.clear();
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask(paramString)
        {
          public void run()
          {
            try
            {
              SetAdminsActivity.SearchAdapter.this.searchTimer.cancel();
              SetAdminsActivity.SearchAdapter.access$1802(SetAdminsActivity.SearchAdapter.this, null);
              SetAdminsActivity.SearchAdapter.this.processSearch(this.val$query);
              return;
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
            }
          }
        }
        , 200L, 300L);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.SetAdminsActivity
 * JD-Core Version:    0.6.0
 */