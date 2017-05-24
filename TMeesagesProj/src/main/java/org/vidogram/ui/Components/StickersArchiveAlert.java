package org.vidogram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.ArchivedStickerSetCell;
import org.vidogram.ui.StickersActivity;

public class StickersArchiveAlert extends AlertDialog.Builder
{
  private int currentType;
  private boolean ignoreLayout;
  private BaseFragment parentFragment;
  private int reqId;
  private int scrollOffsetY;
  private ArrayList<TLRPC.StickerSetCovered> stickerSets;

  public StickersArchiveAlert(Context paramContext, BaseFragment paramBaseFragment, ArrayList<TLRPC.StickerSetCovered> paramArrayList)
  {
    super(paramContext);
    TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)paramArrayList.get(0);
    if (localStickerSetCovered.set.masks)
    {
      this.currentType = 1;
      setTitle(LocaleController.getString("ArchivedMasksAlertTitle", 2131165326));
      this.stickerSets = new ArrayList(paramArrayList);
      this.parentFragment = paramBaseFragment;
      paramBaseFragment = new LinearLayout(paramContext);
      paramBaseFragment.setOrientation(1);
      setView(paramBaseFragment);
      paramArrayList = new TextView(paramContext);
      paramArrayList.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      paramArrayList.setTextSize(1, 16.0F);
      paramArrayList.setPadding(AndroidUtilities.dp(23.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(23.0F), 0);
      if (!localStickerSetCovered.set.masks)
        break label309;
      paramArrayList.setText(LocaleController.getString("ArchivedMasksAlertInfo", 2131165325));
    }
    while (true)
    {
      paramBaseFragment.addView(paramArrayList, LayoutHelper.createLinear(-2, -2));
      paramArrayList = new RecyclerListView(paramContext);
      paramArrayList.setLayoutManager(new LinearLayoutManager(getContext(), 1, false));
      paramArrayList.setAdapter(new ListAdapter(paramContext));
      paramArrayList.setVerticalScrollBarEnabled(false);
      paramArrayList.setPadding(AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F), 0);
      paramArrayList.setGlowColor(-657673);
      paramBaseFragment.addView(paramArrayList, LayoutHelper.createLinear(-1, -2, 0.0F, 10.0F, 0.0F, 0.0F));
      setNegativeButton(LocaleController.getString("Close", 2131165556), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramDialogInterface.dismiss();
        }
      });
      if (this.parentFragment != null)
        setPositiveButton(LocaleController.getString("Settings", 2131166448), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            StickersArchiveAlert.this.parentFragment.presentFragment(new StickersActivity(StickersArchiveAlert.this.currentType));
            paramDialogInterface.dismiss();
          }
        });
      return;
      this.currentType = 0;
      setTitle(LocaleController.getString("ArchivedStickersAlertTitle", 2131165331));
      break;
      label309: paramArrayList.setText(LocaleController.getString("ArchivedStickersAlertInfo", 2131165330));
    }
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    Context context;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public int getItemCount()
    {
      return StickersArchiveAlert.this.stickerSets.size();
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ArchivedStickerSetCell)paramViewHolder.itemView;
      TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)StickersArchiveAlert.this.stickerSets.get(paramInt);
      if (paramInt != StickersArchiveAlert.this.stickerSets.size() - 1);
      for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.setStickersSet(localStickerSetCovered, bool);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ArchivedStickerSetCell(this.context, false);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(82.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.StickersArchiveAlert
 * JD-Core Version:    0.6.0
 */