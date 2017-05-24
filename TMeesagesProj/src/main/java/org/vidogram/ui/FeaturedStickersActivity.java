package org.vidogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetID;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.FeaturedStickerSetCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.StickersAlert;
import org.vidogram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class FeaturedStickersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private HashMap<Long, TLRPC.StickerSetCovered> installingStickerSets = new HashMap();
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int rowCount;
  private int stickersEndRow;
  private int stickersShadowRow;
  private int stickersStartRow;
  private ArrayList<Long> unreadStickers = null;

  private void updateRows()
  {
    this.rowCount = 0;
    ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
    int i;
    if (!localArrayList.isEmpty())
    {
      this.stickersStartRow = this.rowCount;
      this.stickersEndRow = (this.rowCount + localArrayList.size());
      i = this.rowCount;
      this.rowCount = (localArrayList.size() + i);
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.stickersShadowRow = i; ; this.stickersShadowRow = -1)
    {
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      StickersQuery.markFaturedStickersAsRead(true);
      return;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
    }
  }

  private void updateVisibleTrendingSets()
  {
    if (this.layoutManager == null);
    int i;
    int j;
    do
    {
      do
      {
        return;
        i = this.layoutManager.findFirstVisibleItemPosition();
      }
      while (i == -1);
      j = this.layoutManager.findLastVisibleItemPosition();
    }
    while (j == -1);
    this.listAdapter.notifyItemRangeChanged(i, j - i + 1);
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("FeaturedStickers", 2131165707));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          FeaturedStickersActivity.this.finishFragment();
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setItemAnimator(null);
    this.listView.setLayoutAnimation(null);
    this.listView.setFocusable(true);
    this.listView.setTag(Integer.valueOf(14));
    this.layoutManager = new LinearLayoutManager(paramContext)
    {
      public boolean supportsPredictiveItemAnimations()
      {
        return false;
      }
    };
    this.layoutManager.setOrientation(1);
    this.listView.setLayoutManager(this.layoutManager);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        TLRPC.StickerSetCovered localStickerSetCovered;
        Object localObject;
        if ((paramInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramInt < FeaturedStickersActivity.this.stickersEndRow) && (FeaturedStickersActivity.this.getParentActivity() != null))
        {
          localStickerSetCovered = (TLRPC.StickerSetCovered)StickersQuery.getFeaturedStickerSets().get(paramInt);
          if (localStickerSetCovered.set.id == 0L)
            break label136;
          localObject = new TLRPC.TL_inputStickerSetID();
          ((TLRPC.InputStickerSet)localObject).id = localStickerSetCovered.set.id;
        }
        while (true)
        {
          ((TLRPC.InputStickerSet)localObject).access_hash = localStickerSetCovered.set.access_hash;
          localObject = new StickersAlert(FeaturedStickersActivity.this.getParentActivity(), FeaturedStickersActivity.this, (TLRPC.InputStickerSet)localObject, null, null);
          ((StickersAlert)localObject).setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate(paramView, localStickerSetCovered)
          {
            public void onStickerSetInstalled()
            {
              ((FeaturedStickerSetCell)this.val$view).setDrawProgress(true);
              FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(this.val$stickerSet.set.id), this.val$stickerSet);
            }

            public void onStickerSetUninstalled()
            {
            }
          });
          FeaturedStickersActivity.this.showDialog((Dialog)localObject);
          return;
          label136: localObject = new TLRPC.TL_inputStickerSetShortName();
          ((TLRPC.InputStickerSet)localObject).short_name = localStickerSetCovered.set.short_name;
        }
      }
    });
    return this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.featuredStickersDidLoaded)
    {
      if (this.unreadStickers == null)
        this.unreadStickers = StickersQuery.getUnreadStickerSets();
      updateRows();
    }
    do
      return;
    while (paramInt != NotificationCenter.stickersDidLoaded);
    updateVisibleTrendingSets();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { FeaturedStickerSetCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "progressPaint" }, null, null, null, "featuredStickers_buttonProgress"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_buttonText"), new ThemeDescription(this.listView, 0, new Class[] { FeaturedStickerSetCell.class }, new String[] { "checkImage" }, null, null, null, "featuredStickers_addedIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_addButton"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[] { FeaturedStickerSetCell.class }, new String[] { "addButton" }, null, null, null, "featuredStickers_addButtonPressed") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    StickersQuery.checkFeaturedStickers();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
    if (localArrayList != null)
      this.unreadStickers = new ArrayList(localArrayList);
    updateRows();
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
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
      return FeaturedStickersActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= FeaturedStickersActivity.this.stickersStartRow) && (paramInt < FeaturedStickersActivity.this.stickersEndRow));
      do
        return 0;
      while (paramInt != FeaturedStickersActivity.this.stickersShadowRow);
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() == 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool2 = true;
      boolean bool3 = false;
      boolean bool1;
      if (getItemViewType(paramInt) == 0)
      {
        ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
        paramViewHolder = (FeaturedStickerSetCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)localArrayList.get(paramInt);
        if (paramInt == localArrayList.size() - 1)
          break label171;
        bool1 = true;
        if ((FeaturedStickersActivity.this.unreadStickers == null) || (!FeaturedStickersActivity.this.unreadStickers.contains(Long.valueOf(localStickerSetCovered.set.id))))
          break label176;
        label93: paramViewHolder.setStickersSet(localStickerSetCovered, bool1, bool2);
        bool1 = FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id));
        if ((!bool1) || (!paramViewHolder.isInstalled()))
          break label182;
        FeaturedStickersActivity.this.installingStickerSets.remove(Long.valueOf(localStickerSetCovered.set.id));
        paramViewHolder.setDrawProgress(false);
        bool1 = bool3;
      }
      label171: label176: label182: 
      while (true)
      {
        paramViewHolder.setDrawProgress(bool1);
        return;
        bool1 = false;
        break;
        bool2 = false;
        break label93;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new FeaturedStickerSetCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((FeaturedStickerSetCell)paramViewGroup).setAddOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            paramView = (FeaturedStickerSetCell)paramView.getParent();
            TLRPC.StickerSetCovered localStickerSetCovered = paramView.getStickerSet();
            if (FeaturedStickersActivity.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id)))
              return;
            FeaturedStickersActivity.this.installingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
            StickersQuery.removeStickersSet(FeaturedStickersActivity.this.getParentActivity(), localStickerSetCovered.set, 2, FeaturedStickersActivity.this, false);
            paramView.setDrawProgress(true);
          }
        });
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.FeaturedStickersActivity
 * JD-Core Version:    0.6.0
 */