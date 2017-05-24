package org.vidogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.DividerCell;
import org.vidogram.ui.Cells.DrawerActionCell;
import org.vidogram.ui.Cells.DrawerProfileCell;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DrawerLayoutAdapter extends RecyclerListView.SelectionAdapter
{
  private ArrayList<Item> items = new ArrayList(12);
  private Context mContext;

  public DrawerLayoutAdapter(Context paramContext)
  {
    this.mContext = paramContext;
    Theme.createDialogsResources(paramContext);
    resetItems();
  }

  private void resetItems()
  {
    this.items.clear();
    if (!UserConfig.isClientActivated())
      return;
    this.items.add(null);
    this.items.add(null);
    this.items.add(new Item(2, LocaleController.getString("NewGroup", 2131166009), 2130837917));
    this.items.add(new Item(3, LocaleController.getString("NewSecretChat", 2131166017), 2130837918));
    this.items.add(new Item(4, LocaleController.getString("NewChannel", 2131166007), 2130837912));
    this.items.add(null);
    this.items.add(new Item(6, LocaleController.getString("Contacts", 2131165574), 2130837914));
    if (MessagesController.getInstance().callsEnabled)
      this.items.add(new Item(10, LocaleController.getString("Calls", 2131165424), 2130837913));
    this.items.add(new Item(7, LocaleController.getString("InviteFriends", 2131165844), 2130837916));
    this.items.add(new Item(8, LocaleController.getString("Settings", 2131166448), 2130837919));
    this.items.add(new Item(11, LocaleController.getString("UsernameFinder", 2131166789), 2130837774));
    this.items.add(new Item(9, LocaleController.getString("TelegramFaq", 2131166502), 2130837915));
  }

  public int getId(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.items.size()))
      return -1;
    Item localItem = (Item)this.items.get(paramInt);
    if (localItem != null)
      return localItem.id;
    return -1;
  }

  public int getItemCount()
  {
    return this.items.size();
  }

  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == 0)
      i = 0;
    do
      return i;
    while (paramInt == 1);
    if (paramInt == 5)
      return 2;
    return 3;
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return paramViewHolder.getItemViewType() == 3;
  }

  public void notifyDataSetChanged()
  {
    resetItems();
    super.notifyDataSetChanged();
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    switch (paramViewHolder.getItemViewType())
    {
    case 1:
    case 2:
    default:
      return;
    case 0:
      ((DrawerProfileCell)paramViewHolder.itemView).setUser(MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())));
      paramViewHolder.itemView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
      return;
    case 3:
    }
    ((Item)this.items.get(paramInt)).bind((DrawerActionCell)paramViewHolder.itemView);
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    case 1:
    default:
      paramViewGroup = new EmptyCell(this.mContext, AndroidUtilities.dp(8.0F));
    case 0:
    case 2:
    case 3:
    }
    while (true)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new DrawerProfileCell(this.mContext);
      continue;
      paramViewGroup = new DividerCell(this.mContext);
      continue;
      paramViewGroup = new DrawerActionCell(this.mContext);
    }
  }

  private class Item
  {
    public int icon;
    public int id;
    public String text;

    public Item(int paramString, String paramInt1, int arg4)
    {
      int i;
      this.icon = i;
      this.id = paramString;
      this.text = paramInt1;
    }

    public void bind(DrawerActionCell paramDrawerActionCell)
    {
      paramDrawerActionCell.setTextAndIcon(this.text, this.icon);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.DrawerLayoutAdapter
 * JD-Core Version:    0.6.0
 */