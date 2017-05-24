package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class ConvertGroupActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int chat_id;
  private int convertDetailRow;
  private int convertInfoRow;
  private int convertRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int rowCount;

  public ConvertGroupActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chat_id = paramBundle.getInt("chat_id");
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ConvertGroup", 2131165576));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ConvertGroupActivity.this.finishFragment();
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
        if (paramInt == ConvertGroupActivity.this.convertRow)
        {
          paramView = new AlertDialog.Builder(ConvertGroupActivity.this.getParentActivity());
          paramView.setMessage(LocaleController.getString("ConvertGroupAlert", 2131165577));
          paramView.setTitle(LocaleController.getString("ConvertGroupAlertWarning", 2131165578));
          paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              MessagesController.getInstance().convertToMegaGroup(ConvertGroupActivity.this.getParentActivity(), ConvertGroupActivity.this.chat_id);
            }
          });
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          ConvertGroupActivity.this.showDialog(paramView.create());
        }
      }
    });
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.closeChats)
      removeSelfFromStack();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class }, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.convertDetailRow = i;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
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
      return ConvertGroupActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == ConvertGroupActivity.this.convertRow);
      do
        return 0;
      while ((paramInt != ConvertGroupActivity.this.convertInfoRow) && (paramInt != ConvertGroupActivity.this.convertDetailRow));
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getAdapterPosition() == ConvertGroupActivity.this.convertRow;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      }
      do
      {
        do
        {
          return;
          paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
        }
        while (paramInt != ConvertGroupActivity.this.convertRow);
        paramViewHolder.setText(LocaleController.getString("ConvertGroup", 2131165576), false);
        return;
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if (paramInt != ConvertGroupActivity.this.convertInfoRow)
          continue;
        paramViewHolder.setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo2", 2131165580)));
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
        return;
      }
      while (paramInt != ConvertGroupActivity.this.convertDetailRow);
      paramViewHolder.setText(AndroidUtilities.replaceTags(LocaleController.getString("ConvertGroupInfo3", 2131165581)));
      paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ConvertGroupActivity
 * JD-Core Version:    0.6.0
 */