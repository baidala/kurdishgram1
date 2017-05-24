package org.vidogram.ui.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.ui.Cells.DialogCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsAdapter extends RecyclerListView.SelectionAdapter
{
  private int currentCount;
  public int dialogsType;
  private Context mContext;
  private long openedDialogId;

  public DialogsAdapter(Context paramContext, int paramInt)
  {
    this.mContext = paramContext;
    this.dialogsType = paramInt;
  }

  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0)
      return MessagesController.getInstance().dialogs;
    if (this.dialogsType == 1)
      return MessagesController.getInstance().dialogsServerOnly;
    if (this.dialogsType == 2)
      return MessagesController.getInstance().dialogsGroupsOnly;
    if (this.dialogsType == 3)
      return MessagesController.getInstance().dialogsChannelOnly;
    if (this.dialogsType == 4)
      return MessagesController.getInstance().dialogsUserOnly;
    if (this.dialogsType == 5)
      return MessagesController.getInstance().dialogsBotOnly;
    if (this.dialogsType == 6)
      return MessagesController.getInstance().dialogsFavoriteOnly;
    return null;
  }

  public TLRPC.TL_dialog getItem(int paramInt)
  {
    ArrayList localArrayList = getDialogsArray();
    if ((paramInt < 0) || (paramInt >= localArrayList.size()))
      return null;
    return (TLRPC.TL_dialog)localArrayList.get(paramInt);
  }

  public int getItemCount()
  {
    int j = getDialogsArray().size();
    if ((j == 0) && (MessagesController.getInstance().loadingDialogs))
      return 0;
    int i = j;
    if (!MessagesController.getInstance().dialogsEndReached)
      i = j + 1;
    this.currentCount = i;
    return i;
  }

  public int getItemViewType(int paramInt)
  {
    if (paramInt == getDialogsArray().size())
      return 1;
    return 0;
  }

  public boolean isDataSetChanged()
  {
    int i = this.currentCount;
    return (i != getItemCount()) || (i == 1);
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return paramViewHolder.getItemViewType() != 1;
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = true;
    TLRPC.TL_dialog localTL_dialog;
    if (paramViewHolder.getItemViewType() == 0)
    {
      paramViewHolder = (DialogCell)paramViewHolder.itemView;
      if (paramInt == getItemCount() - 1)
        break label88;
      bool1 = true;
      paramViewHolder.useSeparator = bool1;
      localTL_dialog = getItem(paramInt);
      if ((this.dialogsType == 0) && (AndroidUtilities.isTablet()))
        if (localTL_dialog.id != this.openedDialogId)
          break label93;
    }
    label88: label93: for (boolean bool1 = bool2; ; bool1 = false)
    {
      paramViewHolder.setDialogSelected(bool1);
      paramViewHolder.setDialog(localTL_dialog, paramInt, this.dialogsType);
      return;
      bool1 = false;
      break;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = null;
    if (paramInt == 0)
      paramViewGroup = new DialogCell(this.mContext);
    while (true)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new RecyclerListView.Holder(paramViewGroup);
      if (paramInt != 1)
        continue;
      paramViewGroup = new LoadingCell(this.mContext);
    }
  }

  public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.itemView instanceof DialogCell))
      ((DialogCell)paramViewHolder.itemView).checkCurrentDialogIndex();
  }

  public void setOpenedDialogId(long paramLong)
  {
    this.openedDialogId = paramLong;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.DialogsAdapter
 * JD-Core Version:    0.6.0
 */