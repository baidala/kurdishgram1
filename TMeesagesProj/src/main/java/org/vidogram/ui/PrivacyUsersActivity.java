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
import java.util.Iterator;
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

public class PrivacyUsersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int block_user = 1;
  private PrivacyActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private boolean isAlwaysShare;
  private boolean isGroup;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private int selectedUserId;
  private ArrayList<Integer> uidArray;

  public PrivacyUsersActivity(ArrayList<Integer> paramArrayList, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.uidArray = paramArrayList;
    this.isAlwaysShare = paramBoolean2;
    this.isGroup = paramBoolean1;
  }

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
    FrameLayout localFrameLayout;
    if (this.isGroup)
      if (this.isAlwaysShare)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysAllow", 2131165301));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
        {
          public void onItemClick(int paramInt)
          {
            if (paramInt == -1)
              PrivacyUsersActivity.this.finishFragment();
            do
              return;
            while (paramInt != 1);
            Bundle localBundle = new Bundle();
            if (PrivacyUsersActivity.this.isAlwaysShare);
            for (Object localObject = "isAlwaysShare"; ; localObject = "isNeverShare")
            {
              localBundle.putBoolean((String)localObject, true);
              localBundle.putBoolean("isGroup", PrivacyUsersActivity.this.isGroup);
              localObject = new GroupCreateActivity(localBundle);
              ((GroupCreateActivity)localObject).setDelegate(new GroupCreateActivity.GroupCreateActivityDelegate()
              {
                public void didSelectUsers(ArrayList<Integer> paramArrayList)
                {
                  paramArrayList = paramArrayList.iterator();
                  while (paramArrayList.hasNext())
                  {
                    Integer localInteger = (Integer)paramArrayList.next();
                    if (PrivacyUsersActivity.this.uidArray.contains(localInteger))
                      continue;
                    PrivacyUsersActivity.this.uidArray.add(localInteger);
                  }
                  PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                  if (PrivacyUsersActivity.this.delegate != null)
                    PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, true);
                }
              });
              PrivacyUsersActivity.this.presentFragment((BaseFragment)localObject);
              return;
            }
          }
        });
        this.actionBar.createMenu().addItem(1, 2130838031);
        this.fragmentView = new FrameLayout(paramContext);
        localFrameLayout = (FrameLayout)this.fragmentView;
        this.emptyView = new EmptyTextProgressView(paramContext);
        this.emptyView.showTextView();
        this.emptyView.setText(LocaleController.getString("NoContacts", 2131166027));
        localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
        this.listView = new RecyclerListView(paramContext);
        this.listView.setEmptyView(this.emptyView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
        RecyclerListView localRecyclerListView = this.listView;
        paramContext = new ListAdapter(paramContext);
        this.listViewAdapter = paramContext;
        localRecyclerListView.setAdapter(paramContext);
        paramContext = this.listView;
        if (!LocaleController.isRTL)
          break label341;
      }
    while (true)
    {
      paramContext.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (paramInt < PrivacyUsersActivity.this.uidArray.size())
          {
            paramView = new Bundle();
            paramView.putInt("user_id", ((Integer)PrivacyUsersActivity.this.uidArray.get(paramInt)).intValue());
            PrivacyUsersActivity.this.presentFragment(new ProfileActivity(paramView));
          }
        }
      });
      this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          if ((paramInt < 0) || (paramInt >= PrivacyUsersActivity.this.uidArray.size()) || (PrivacyUsersActivity.this.getParentActivity() == null))
            return false;
          PrivacyUsersActivity.access$502(PrivacyUsersActivity.this, ((Integer)PrivacyUsersActivity.this.uidArray.get(paramInt)).intValue());
          paramView = new AlertDialog.Builder(PrivacyUsersActivity.this.getParentActivity());
          String str = LocaleController.getString("Delete", 2131165628);
          1 local1 = new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              if (paramInt == 0)
              {
                PrivacyUsersActivity.this.uidArray.remove(Integer.valueOf(PrivacyUsersActivity.this.selectedUserId));
                PrivacyUsersActivity.this.listViewAdapter.notifyDataSetChanged();
                if (PrivacyUsersActivity.this.delegate != null)
                  PrivacyUsersActivity.this.delegate.didUpdatedUserList(PrivacyUsersActivity.this.uidArray, false);
              }
            }
          };
          paramView.setItems(new CharSequence[] { str }, local1);
          PrivacyUsersActivity.this.showDialog(paramView.create());
          return true;
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("NeverAllow", 2131166001));
      break;
      if (this.isAlwaysShare)
      {
        this.actionBar.setTitle(LocaleController.getString("AlwaysShareWithTitle", 2131165305));
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("NeverShareWithTitle", 2131166005));
      break;
      label341: i = 2;
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
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    4 local4 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = PrivacyUsersActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = PrivacyUsersActivity.this.listView.getChildAt(paramInt);
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
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { UserCell.class }, new String[] { "statusColor" }, null, null, local4, "windowBackgroundWhiteGrayText");
    RecyclerListView localRecyclerListView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { UserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local4, "avatar_backgroundPink") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listViewAdapter != null)
      this.listViewAdapter.notifyDataSetChanged();
  }

  public void setDelegate(PrivacyActivityDelegate paramPrivacyActivityDelegate)
  {
    this.delegate = paramPrivacyActivityDelegate;
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
      if (PrivacyUsersActivity.this.uidArray.isEmpty())
        return 0;
      return PrivacyUsersActivity.this.uidArray.size() + 1;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == PrivacyUsersActivity.this.uidArray.size())
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getAdapterPosition() != PrivacyUsersActivity.this.uidArray.size();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.User localUser;
      UserCell localUserCell;
      if (paramViewHolder.getItemViewType() == 0)
      {
        localUser = MessagesController.getInstance().getUser((Integer)PrivacyUsersActivity.this.uidArray.get(paramInt));
        localUserCell = (UserCell)paramViewHolder.itemView;
        if ((localUser.phone == null) || (localUser.phone.length() == 0))
          break label93;
      }
      label93: for (paramViewHolder = b.a().e("+" + localUser.phone); ; paramViewHolder = LocaleController.getString("NumberUnknown", 2131166152))
      {
        localUserCell.setData(localUser, null, paramViewHolder, 0);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoCell(this.mContext);
        ((TextInfoCell)paramViewGroup).setText(LocaleController.getString("RemoveFromListText", 2131166320));
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new UserCell(this.mContext, 1, 0, false);
      }
    }
  }

  public static abstract interface PrivacyActivityDelegate
  {
    public abstract void didUpdatedUserList(ArrayList<Integer> paramArrayList, boolean paramBoolean);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PrivacyUsersActivity
 * JD-Core Version:    0.6.0
 */