package org.vidogram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputUserEmpty;
import org.vidogram.tgnet.TLRPC.TL_messages_getCommonChats;
import org.vidogram.tgnet.TLRPC.messages_Chats;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class CommonGroupsActivity extends BaseFragment
{
  private ArrayList<TLRPC.Chat> chats = new ArrayList();
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private LinearLayoutManager layoutManager;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loading;
  private int userId;

  public CommonGroupsActivity(int paramInt)
  {
    this.userId = paramInt;
  }

  private void getChats(int paramInt1, int paramInt2)
  {
    if (this.loading);
    TLRPC.TL_messages_getCommonChats localTL_messages_getCommonChats;
    do
    {
      return;
      this.loading = true;
      if ((this.emptyView != null) && (!this.firstLoaded))
        this.emptyView.showProgress();
      if (this.listViewAdapter != null)
        this.listViewAdapter.notifyDataSetChanged();
      localTL_messages_getCommonChats = new TLRPC.TL_messages_getCommonChats();
      localTL_messages_getCommonChats.user_id = MessagesController.getInputUser(this.userId);
    }
    while ((localTL_messages_getCommonChats.user_id instanceof TLRPC.TL_inputUserEmpty));
    localTL_messages_getCommonChats.limit = paramInt2;
    localTL_messages_getCommonChats.max_id = paramInt1;
    paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_getCommonChats, new RequestDelegate(paramInt2)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            boolean bool;
            if (this.val$error == null)
            {
              TLRPC.messages_Chats localmessages_Chats = (TLRPC.messages_Chats)this.val$response;
              MessagesController.getInstance().putChats(localmessages_Chats.chats, false);
              CommonGroupsActivity localCommonGroupsActivity = CommonGroupsActivity.this;
              if ((localmessages_Chats.chats.isEmpty()) || (localmessages_Chats.chats.size() != CommonGroupsActivity.4.this.val$count))
              {
                bool = true;
                CommonGroupsActivity.access$302(localCommonGroupsActivity, bool);
                CommonGroupsActivity.this.chats.addAll(localmessages_Chats.chats);
              }
            }
            while (true)
            {
              CommonGroupsActivity.access$402(CommonGroupsActivity.this, false);
              CommonGroupsActivity.access$602(CommonGroupsActivity.this, true);
              if (CommonGroupsActivity.this.emptyView != null)
                CommonGroupsActivity.this.emptyView.showTextView();
              if (CommonGroupsActivity.this.listViewAdapter != null)
                CommonGroupsActivity.this.listViewAdapter.notifyDataSetChanged();
              return;
              bool = false;
              break;
              CommonGroupsActivity.access$302(CommonGroupsActivity.this, true);
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, this.classGuid);
  }

  public View createView(Context paramContext)
  {
    int i = 1;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("GroupsInCommonTitle", 2131165807));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          CommonGroupsActivity.this.finishFragment();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoGroupsInCommon", 2131166029));
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    RecyclerListView localRecyclerListView = this.listView;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = localLinearLayoutManager;
    localRecyclerListView.setLayoutManager(localLinearLayoutManager);
    localRecyclerListView = this.listView;
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
          if ((paramInt < 0) || (paramInt >= CommonGroupsActivity.this.chats.size()));
          Bundle localBundle;
          do
          {
            return;
            paramView = (TLRPC.Chat)CommonGroupsActivity.this.chats.get(paramInt);
            localBundle = new Bundle();
            localBundle.putInt("chat_id", paramView.id);
          }
          while (!MessagesController.checkCanOpenChat(localBundle, CommonGroupsActivity.this));
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
          CommonGroupsActivity.this.presentFragment(new ChatActivity(localBundle), true);
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          paramInt2 = CommonGroupsActivity.this.layoutManager.findFirstVisibleItemPosition();
          if (paramInt2 == -1);
          for (paramInt1 = 0; ; paramInt1 = Math.abs(CommonGroupsActivity.this.layoutManager.findLastVisibleItemPosition() - paramInt2) + 1)
          {
            if (paramInt1 > 0)
            {
              int i = CommonGroupsActivity.this.listViewAdapter.getItemCount();
              if ((!CommonGroupsActivity.this.endReached) && (!CommonGroupsActivity.this.loading) && (!CommonGroupsActivity.this.chats.isEmpty()) && (paramInt1 + paramInt2 >= i - 5))
                CommonGroupsActivity.this.getChats(((TLRPC.Chat)CommonGroupsActivity.this.chats.get(CommonGroupsActivity.this.chats.size() - 1)).id, 100);
            }
            return;
          }
        }
      });
      if (!this.loading)
        break label285;
      this.emptyView.showProgress();
    }
    while (true)
    {
      return this.fragmentView;
      i = 2;
      break;
      label285: this.emptyView.showTextView();
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    5 local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = CommonGroupsActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          View localView = CommonGroupsActivity.this.listView.getChildAt(paramInt);
          if ((localView instanceof ProfileSearchCell))
            ((ProfileSearchCell)localView).update(0);
          paramInt += 1;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { LoadingCell.class, ProfileSearchCell.class }, null, null, null, "windowBackgroundWhite");
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
    localObject2 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle");
    Object localObject3 = this.listView;
    Object localObject4 = Theme.dialogs_namePaint;
    localObject3 = new ThemeDescription((View)localObject3, 0, new Class[] { ProfileSearchCell.class }, (Paint)localObject4, null, null, "chats_name");
    localObject4 = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return (ThemeDescription)(ThemeDescription)(ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localObject1, localObject2, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localObject3, new ThemeDescription((View)localObject4, 0, new Class[] { ProfileSearchCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, null, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getChats(0, 50);
    return true;
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
      int j = CommonGroupsActivity.this.chats.size();
      int i = j;
      if (!CommonGroupsActivity.this.chats.isEmpty())
      {
        j += 1;
        i = j;
        if (!CommonGroupsActivity.this.endReached)
          i = j + 1;
      }
      return i;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt < CommonGroupsActivity.this.chats.size())
        return 0;
      if ((!CommonGroupsActivity.this.endReached) && (paramInt == CommonGroupsActivity.this.chats.size()))
        return 1;
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getAdapterPosition() != CommonGroupsActivity.this.chats.size();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = false;
      if (paramViewHolder.getItemViewType() == 0)
      {
        paramViewHolder = (ProfileSearchCell)paramViewHolder.itemView;
        paramViewHolder.setData((TLRPC.Chat)CommonGroupsActivity.this.chats.get(paramInt), null, null, null, false);
        if ((paramInt != CommonGroupsActivity.this.chats.size() - 1) || (!CommonGroupsActivity.this.endReached))
          bool = true;
        paramViewHolder.useSeparator = bool;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ProfileSearchCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.CommonGroupsActivity
 * JD-Core Version:    0.6.0
 */