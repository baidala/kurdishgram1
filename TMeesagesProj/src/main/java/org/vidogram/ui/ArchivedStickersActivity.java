package org.vidogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetID;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.vidogram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.ArchivedStickerSetCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.StickersAlert;
import org.vidogram.ui.Components.StickersAlert.StickersAlertInstallDelegate;

public class ArchivedStickersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private int currentType;
  private EmptyTextProgressView emptyView;
  private boolean endReached;
  private boolean firstLoaded;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loadingStickers;
  private int rowCount;
  private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList();
  private int stickersEndRow;
  private int stickersLoadingRow;
  private int stickersShadowRow;
  private int stickersStartRow;

  public ArchivedStickersActivity(int paramInt)
  {
    this.currentType = paramInt;
  }

  private void getStickers()
  {
    if ((this.loadingStickers) || (this.endReached))
      return;
    this.loadingStickers = true;
    if ((this.emptyView != null) && (!this.firstLoaded))
      this.emptyView.showProgress();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    TLRPC.TL_messages_getArchivedStickers localTL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
    long l;
    if (this.sets.isEmpty())
    {
      l = 0L;
      localTL_messages_getArchivedStickers.offset_id = l;
      localTL_messages_getArchivedStickers.limit = 15;
      if (this.currentType != 1)
        break label165;
    }
    label165: for (boolean bool = true; ; bool = false)
    {
      localTL_messages_getArchivedStickers.masks = bool;
      int i = ConnectionsManager.getInstance().sendRequest(localTL_messages_getArchivedStickers, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              ArchivedStickersActivity localArchivedStickersActivity;
              if (this.val$error == null)
              {
                TLRPC.TL_messages_archivedStickers localTL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers)this.val$response;
                ArchivedStickersActivity.this.sets.addAll(localTL_messages_archivedStickers.sets);
                localArchivedStickersActivity = ArchivedStickersActivity.this;
                if (localTL_messages_archivedStickers.sets.size() == 15)
                  break label122;
              }
              label122: for (boolean bool = true; ; bool = false)
              {
                ArchivedStickersActivity.access$402(localArchivedStickersActivity, bool);
                ArchivedStickersActivity.access$302(ArchivedStickersActivity.this, false);
                ArchivedStickersActivity.access$802(ArchivedStickersActivity.this, true);
                if (ArchivedStickersActivity.this.emptyView != null)
                  ArchivedStickersActivity.this.emptyView.showTextView();
                ArchivedStickersActivity.this.updateRows();
                return;
              }
            }
          });
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
      return;
      l = ((TLRPC.StickerSetCovered)this.sets.get(this.sets.size() - 1)).set.id;
      break;
    }
  }

  private void updateRows()
  {
    this.rowCount = 0;
    int i;
    if (!this.sets.isEmpty())
    {
      this.stickersStartRow = this.rowCount;
      this.stickersEndRow = (this.rowCount + this.sets.size());
      this.rowCount += this.sets.size();
      if (!this.endReached)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.stickersLoadingRow = i;
        this.stickersShadowRow = -1;
      }
    }
    while (true)
    {
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.stickersShadowRow = i;
      this.stickersLoadingRow = -1;
      continue;
      this.stickersStartRow = -1;
      this.stickersEndRow = -1;
      this.stickersLoadingRow = -1;
      this.stickersShadowRow = -1;
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    FrameLayout localFrameLayout;
    if (this.currentType == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("ArchivedStickers", 2131165329));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            ArchivedStickersActivity.this.finishFragment();
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.emptyView = new EmptyTextProgressView(paramContext);
      if (this.currentType != 0)
        break label292;
      this.emptyView.setText(LocaleController.getString("ArchivedStickersEmpty", 2131165332));
      label128: localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
      if (!this.loadingStickers)
        break label311;
      this.emptyView.showProgress();
    }
    while (true)
    {
      this.listView = new RecyclerListView(paramContext);
      this.listView.setFocusable(true);
      this.listView.setEmptyView(this.emptyView);
      RecyclerListView localRecyclerListView = this.listView;
      paramContext = new LinearLayoutManager(paramContext, 1, false);
      this.layoutManager = paramContext;
      localRecyclerListView.setLayoutManager(paramContext);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          TLRPC.StickerSetCovered localStickerSetCovered;
          Object localObject;
          if ((paramInt >= ArchivedStickersActivity.this.stickersStartRow) && (paramInt < ArchivedStickersActivity.this.stickersEndRow) && (ArchivedStickersActivity.this.getParentActivity() != null))
          {
            localStickerSetCovered = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(paramInt);
            if (localStickerSetCovered.set.id == 0L)
              break label138;
            localObject = new TLRPC.TL_inputStickerSetID();
            ((TLRPC.InputStickerSet)localObject).id = localStickerSetCovered.set.id;
          }
          while (true)
          {
            ((TLRPC.InputStickerSet)localObject).access_hash = localStickerSetCovered.set.access_hash;
            localObject = new StickersAlert(ArchivedStickersActivity.this.getParentActivity(), ArchivedStickersActivity.this, (TLRPC.InputStickerSet)localObject, null, null);
            ((StickersAlert)localObject).setInstallDelegate(new StickersAlert.StickersAlertInstallDelegate(paramView)
            {
              public void onStickerSetInstalled()
              {
                ((ArchivedStickerSetCell)this.val$view).setChecked(true);
              }

              public void onStickerSetUninstalled()
              {
                ((ArchivedStickerSetCell)this.val$view).setChecked(false);
              }
            });
            ArchivedStickersActivity.this.showDialog((Dialog)localObject);
            return;
            label138: localObject = new TLRPC.TL_inputStickerSetShortName();
            ((TLRPC.InputStickerSet)localObject).short_name = localStickerSetCovered.set.short_name;
          }
        }
      });
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          if ((!ArchivedStickersActivity.this.loadingStickers) && (!ArchivedStickersActivity.this.endReached) && (ArchivedStickersActivity.this.layoutManager.findLastVisibleItemPosition() > ArchivedStickersActivity.this.stickersLoadingRow - 2))
            ArchivedStickersActivity.this.getStickers();
        }
      });
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("ArchivedMasks", 2131165324));
      break;
      label292: this.emptyView.setText(LocaleController.getString("ArchivedMasksEmpty", 2131165327));
      break label128;
      label311: this.emptyView.showTextView();
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.needReloadArchivedStickers)
    {
      this.firstLoaded = false;
      this.endReached = false;
      this.sets.clear();
      updateRows();
      if (this.emptyView != null)
        this.emptyView.showProgress();
      getStickers();
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { ArchivedStickerSetCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { LoadingCell.class, TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { ArchivedStickerSetCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    getStickers();
    updateRows();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.needReloadArchivedStickers);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.needReloadArchivedStickers);
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
      return ArchivedStickersActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= ArchivedStickersActivity.this.stickersStartRow) && (paramInt < ArchivedStickersActivity.this.stickersEndRow));
      do
      {
        return 0;
        if (paramInt == ArchivedStickersActivity.this.stickersLoadingRow)
          return 1;
      }
      while (paramInt != ArchivedStickersActivity.this.stickersShadowRow);
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() == 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.StickerSetCovered localStickerSetCovered;
      if (getItemViewType(paramInt) == 0)
      {
        paramViewHolder = (ArchivedStickerSetCell)paramViewHolder.itemView;
        paramViewHolder.setTag(Integer.valueOf(paramInt));
        localStickerSetCovered = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(paramInt);
        if (paramInt == ArchivedStickersActivity.this.sets.size() - 1)
          break label81;
      }
      label81: for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.setStickersSet(localStickerSetCovered, bool);
        paramViewHolder.setChecked(StickersQuery.isStickerPackInstalled(localStickerSetCovered.set.id));
        return;
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
      case 2:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ArchivedStickerSetCell(this.mContext, true);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        ((ArchivedStickerSetCell)paramViewGroup).setOnCheckClick(new CompoundButton.OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
          {
            int i = ((Integer)((ArchivedStickerSetCell)paramCompoundButton.getParent()).getTag()).intValue();
            if (i >= ArchivedStickersActivity.this.sets.size())
              return;
            Object localObject = (TLRPC.StickerSetCovered)ArchivedStickersActivity.this.sets.get(i);
            paramCompoundButton = ArchivedStickersActivity.this.getParentActivity();
            localObject = ((TLRPC.StickerSetCovered)localObject).set;
            if (!paramBoolean);
            for (i = 1; ; i = 2)
            {
              StickersQuery.removeStickersSet(paramCompoundButton, (TLRPC.StickerSet)localObject, i, ArchivedStickersActivity.this, false);
              return;
            }
          }
        });
        continue;
        paramViewGroup = new LoadingCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
        paramViewGroup.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ArchivedStickersActivity
 * JD-Core Version:    0.6.0
 */