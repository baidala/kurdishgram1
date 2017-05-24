package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.vidogram.a.b;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.TextInfoCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class BlockedUsersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, ContactsActivity.ContactsActivityDelegate
{
  private static final int block_user = 1;
  private EmptyTextProgressView emptyView;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private int selectedUserId;

  private void updateVisibleRows(int paramInt)
  {
    if (this.listView == null);
    while (true)
    {
      return;
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
    int i = 1;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("BlockedUsers", 2131165384));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          BlockedUsersActivity.this.finishFragment();
        do
          return;
        while (paramInt != 1);
        Object localObject = new Bundle();
        ((Bundle)localObject).putBoolean("onlyUsers", true);
        ((Bundle)localObject).putBoolean("destroyAfterSelect", true);
        ((Bundle)localObject).putBoolean("returnAsResult", true);
        localObject = new ContactsActivity((Bundle)localObject);
        ((ContactsActivity)localObject).setDelegate(BlockedUsersActivity.this);
        BlockedUsersActivity.this.presentFragment((BaseFragment)localObject);
      }
    });
    this.actionBar.createMenu().addItem(1, 2130838031);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoBlocked", 2131166022));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    RecyclerListView localRecyclerListView = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listViewAdapter = paramContext;
    localRecyclerListView.setAdapter(paramContext);
    paramContext = this.listView;
    if (LocaleController.isRTL)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (paramInt >= MessagesController.getInstance().blockedUsers.size())
            return;
          paramView = new Bundle();
          paramView.putInt("user_id", ((Integer)MessagesController.getInstance().blockedUsers.get(paramInt)).intValue());
          BlockedUsersActivity.this.presentFragment(new ProfileActivity(paramView));
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          if ((paramInt >= MessagesController.getInstance().blockedUsers.size()) || (BlockedUsersActivity.this.getParentActivity() == null))
            return true;
          BlockedUsersActivity.access$002(BlockedUsersActivity.this, ((Integer)MessagesController.getInstance().blockedUsers.get(paramInt)).intValue());
          paramView = new AlertDialog.Builder(BlockedUsersActivity.this.getParentActivity());
          String str = LocaleController.getString("Unblock", 2131166530);
          1 local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              if (paramInt == 0)
                MessagesController.getInstance().unblockUser(BlockedUsersActivity.this.selectedUserId);
            }
          };
          paramView.setItems(new CharSequence[] { str }, local1);
          BlockedUsersActivity.this.showDialog(paramView.create());
          return true;
        }
      });
      if (!MessagesController.getInstance().loadingBlockedUsers)
        break label283;
      this.emptyView.showProgress();
    }
    while (true)
    {
      return this.fragmentView;
      i = 2;
      break;
      label283: this.emptyView.showTextView();
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0))
        updateVisibleRows(paramInt);
    }
    do
    {
      do
        return;
      while (paramInt != NotificationCenter.blockedUsersDidLoaded);
      this.emptyView.showTextView();
    }
    while (this.listViewAdapter == null);
    this.listViewAdapter.notifyDataSetChanged();
  }

  public void didSelectContact(TLRPC.User paramUser, String paramString)
  {
    if (paramUser == null)
      return;
    MessagesController.getInstance().blockUser(paramUser.id);
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    4 local4 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = BlockedUsersActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = BlockedUsersActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof UserCell))
            ((UserCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local4, "windowBackgroundWhiteGrayText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundPink") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.blockedUsersDidLoaded);
    MessagesController.getInstance().getBlockedUsers(false);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.blockedUsersDidLoaded);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
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
      if (MessagesController.getInstance().blockedUsers.isEmpty())
        return 0;
      return MessagesController.getInstance().blockedUsers.size() + 1;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == MessagesController.getInstance().blockedUsers.size())
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() == 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.User localUser;
      String str;
      if (paramViewHolder.getItemViewType() == 0)
      {
        localUser = MessagesController.getInstance().getUser((Integer)MessagesController.getInstance().blockedUsers.get(paramInt));
        if (localUser != null)
        {
          if (!localUser.bot)
            break label100;
          str = LocaleController.getString("Bot", 2131165390).substring(0, 1).toUpperCase() + LocaleController.getString("Bot", 2131165390).substring(1);
        }
      }
      while (true)
      {
        ((UserCell)paramViewHolder.itemView).setData(localUser, null, str, 0);
        return;
        label100: if ((localUser.phone != null) && (localUser.phone.length() != 0))
        {
          str = b.a().e("+" + localUser.phone);
          continue;
        }
        str = LocaleController.getString("NumberUnknown", 2131166152);
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoCell(this.mContext);
        ((TextInfoCell)paramViewGroup).setText(LocaleController.getString("UnblockText", 2131166531));
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.BlockedUsersActivity
 * JD-Core Version:    0.6.0
 */