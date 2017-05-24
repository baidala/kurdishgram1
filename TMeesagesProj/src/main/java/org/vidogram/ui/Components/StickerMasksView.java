package org.vidogram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.StickerEmojiCell;
import org.vidogram.ui.StickerPreviewViewer;

public class StickerMasksView extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private int currentType = 1;
  private int lastNotifyWidth;
  private Listener listener;
  private ArrayList<TLRPC.Document>[] recentStickers = { new ArrayList(), new ArrayList() };
  private int recentTabBum = -2;
  private ScrollSlidingTabStrip scrollSlidingTabStrip;
  private ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = { new ArrayList(), new ArrayList() };
  private TextView stickersEmptyView;
  private StickersGridAdapter stickersGridAdapter;
  private RecyclerListView stickersGridView;
  private GridLayoutManager stickersLayoutManager;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
  private int stickersTabOffset;

  public StickerMasksView(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(-14540254);
    setClickable(true);
    StickersQuery.checkStickers(0);
    StickersQuery.checkStickers(1);
    this.stickersGridView = new RecyclerListView(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        boolean bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramMotionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), null);
        return (super.onInterceptTouchEvent(paramMotionEvent)) || (bool);
      }
    };
    RecyclerListView localRecyclerListView = this.stickersGridView;
    Object localObject = new GridLayoutManager(paramContext, 5);
    this.stickersLayoutManager = ((GridLayoutManager)localObject);
    localRecyclerListView.setLayoutManager((RecyclerView.LayoutManager)localObject);
    this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
    {
      public int getSpanSize(int paramInt)
      {
        if (paramInt == StickerMasksView.StickersGridAdapter.access$200(StickerMasksView.this.stickersGridAdapter))
          return StickerMasksView.StickersGridAdapter.access$300(StickerMasksView.this.stickersGridAdapter);
        return 1;
      }
    });
    this.stickersGridView.setPadding(0, AndroidUtilities.dp(4.0F), 0, 0);
    this.stickersGridView.setClipToPadding(false);
    localRecyclerListView = this.stickersGridView;
    localObject = new StickersGridAdapter(paramContext);
    this.stickersGridAdapter = ((StickersGridAdapter)localObject);
    localRecyclerListView.setAdapter((RecyclerView.Adapter)localObject);
    this.stickersGridView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return StickerPreviewViewer.getInstance().onTouch(paramMotionEvent, StickerMasksView.this.stickersGridView, StickerMasksView.this.getMeasuredHeight(), StickerMasksView.this.stickersOnItemClickListener, null);
      }
    });
    this.stickersOnItemClickListener = new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (!(paramView instanceof StickerEmojiCell));
        do
        {
          return;
          StickerPreviewViewer.getInstance().reset();
          paramView = (StickerEmojiCell)paramView;
        }
        while (paramView.isDisabled());
        paramView = paramView.getSticker();
        StickerMasksView.this.listener.onStickerSelected(paramView);
        StickersQuery.addRecentSticker(1, paramView, (int)(System.currentTimeMillis() / 1000L));
        MessagesController.getInstance().saveRecentSticker(paramView, true);
      }
    };
    this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
    this.stickersGridView.setGlowColor(-657673);
    addView(this.stickersGridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    this.stickersEmptyView = new TextView(paramContext);
    this.stickersEmptyView.setTextSize(1, 18.0F);
    this.stickersEmptyView.setTextColor(-7829368);
    addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 48.0F, 0.0F, 0.0F));
    this.stickersGridView.setEmptyView(this.stickersEmptyView);
    this.scrollSlidingTabStrip = new ScrollSlidingTabStrip(paramContext);
    this.scrollSlidingTabStrip.setBackgroundColor(-16777216);
    this.scrollSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0F));
    this.scrollSlidingTabStrip.setIndicatorColor(-10305560);
    this.scrollSlidingTabStrip.setUnderlineColor(-15066598);
    this.scrollSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(1.0F) + 1);
    addView(this.scrollSlidingTabStrip, LayoutHelper.createFrame(-1, 48, 51));
    updateStickerTabs();
    this.scrollSlidingTabStrip.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate()
    {
      public void onPageSelected(int paramInt)
      {
        if (paramInt == 0)
        {
          if (StickerMasksView.this.currentType == 0)
            StickerMasksView.access$602(StickerMasksView.this, 1);
          while (true)
          {
            if (StickerMasksView.this.listener != null)
              StickerMasksView.this.listener.onTypeChanged();
            StickerMasksView.this.recentStickers[StickerMasksView.this.currentType] = StickersQuery.getRecentStickers(StickerMasksView.access$600(StickerMasksView.this));
            StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
            StickerMasksView.this.updateStickerTabs();
            StickerMasksView.this.reloadStickersAdapter();
            StickerMasksView.this.checkDocuments();
            StickerMasksView.this.checkPanels();
            return;
            StickerMasksView.access$602(StickerMasksView.this, 0);
          }
        }
        if (paramInt == StickerMasksView.this.recentTabBum + 1)
        {
          StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
          return;
        }
        int i = paramInt - 1 - StickerMasksView.this.stickersTabOffset;
        paramInt = i;
        if (i >= StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size())
          paramInt = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].size() - 1;
        StickerMasksView.this.stickersLayoutManager.scrollToPositionWithOffset(StickerMasksView.this.stickersGridAdapter.getPositionForPack((TLRPC.TL_messages_stickerSet)StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].get(paramInt)), 0);
        StickerMasksView.this.checkScroll();
      }
    });
    this.stickersGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
      {
        StickerMasksView.this.checkScroll();
      }
    });
  }

  private void checkDocuments()
  {
    int i = this.recentStickers[this.currentType].size();
    this.recentStickers[this.currentType] = StickersQuery.getRecentStickers(this.currentType);
    if (this.stickersGridAdapter != null)
      this.stickersGridAdapter.notifyDataSetChanged();
    if (i != this.recentStickers[this.currentType].size())
      updateStickerTabs();
  }

  private void checkPanels()
  {
    if (this.scrollSlidingTabStrip == null);
    do
    {
      return;
      i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    }
    while (i == -1);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.scrollSlidingTabStrip;
    int j = this.stickersGridAdapter.getTabForPosition(i);
    if (this.recentTabBum > 0);
    for (int i = this.recentTabBum; ; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      return;
    }
  }

  private void checkScroll()
  {
    int i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    if (i == -1)
      return;
    checkStickersScroll(i);
  }

  private void checkStickersScroll(int paramInt)
  {
    if (this.stickersGridView == null)
      return;
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.scrollSlidingTabStrip;
    int i = this.stickersGridAdapter.getTabForPosition(paramInt);
    if (this.recentTabBum > 0);
    for (paramInt = this.recentTabBum; ; paramInt = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(i + 1, paramInt + 1);
      return;
    }
  }

  private void reloadStickersAdapter()
  {
    if (this.stickersGridAdapter != null)
      this.stickersGridAdapter.notifyDataSetChanged();
    if (StickerPreviewViewer.getInstance().isVisible())
      StickerPreviewViewer.getInstance().close();
    StickerPreviewViewer.getInstance().reset();
  }

  private void updateStickerTabs()
  {
    if (this.scrollSlidingTabStrip == null)
      return;
    this.recentTabBum = -2;
    this.stickersTabOffset = 0;
    int j = this.scrollSlidingTabStrip.getCurrentPosition();
    this.scrollSlidingTabStrip.removeTabs();
    Object localObject;
    label170: TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
    if (this.currentType == 0)
    {
      localObject = getContext().getResources().getDrawable(2130837808);
      Theme.setDrawableColorByKey((Drawable)localObject, "chat_emojiPanelIcon");
      this.scrollSlidingTabStrip.addIconTab((Drawable)localObject);
      this.stickersEmptyView.setText(LocaleController.getString("NoStickers", 2131166053));
      if (!this.recentStickers[this.currentType].isEmpty())
      {
        this.recentTabBum = this.stickersTabOffset;
        this.stickersTabOffset += 1;
        this.scrollSlidingTabStrip.addIconTab(Theme.createEmojiIconSelectorDrawable(getContext(), 2130837810, Theme.getColor("chat_emojiPanelMasksIcon"), Theme.getColor("chat_emojiPanelMasksIconSelected")));
      }
      this.stickerSets[this.currentType].clear();
      localObject = StickersQuery.getStickerSets(this.currentType);
      i = 0;
      if (i >= ((ArrayList)localObject).size())
        break label291;
      localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject).get(i);
      if ((!localTL_messages_stickerSet.set.archived) && (localTL_messages_stickerSet.documents != null) && (!localTL_messages_stickerSet.documents.isEmpty()))
        break label273;
    }
    while (true)
    {
      i += 1;
      break label170;
      localObject = getContext().getResources().getDrawable(2130837812);
      Theme.setDrawableColorByKey((Drawable)localObject, "chat_emojiPanelIcon");
      this.scrollSlidingTabStrip.addIconTab((Drawable)localObject);
      this.stickersEmptyView.setText(LocaleController.getString("NoMasks", 2131166032));
      break;
      label273: this.stickerSets[this.currentType].add(localTL_messages_stickerSet);
    }
    label291: int i = 0;
    while (i < this.stickerSets[this.currentType].size())
    {
      this.scrollSlidingTabStrip.addStickerTab((TLRPC.Document)((TLRPC.TL_messages_stickerSet)this.stickerSets[this.currentType].get(i)).documents.get(0));
      i += 1;
    }
    this.scrollSlidingTabStrip.updateTabStyles();
    if (j != 0)
      this.scrollSlidingTabStrip.onPageScrolled(j, j);
    checkPanels();
  }

  public void addRecentSticker(TLRPC.Document paramDocument)
  {
    if (paramDocument == null);
    boolean bool;
    do
    {
      return;
      StickersQuery.addRecentSticker(this.currentType, paramDocument, (int)(System.currentTimeMillis() / 1000L));
      bool = this.recentStickers[this.currentType].isEmpty();
      this.recentStickers[this.currentType] = StickersQuery.getRecentStickers(this.currentType);
      if (this.stickersGridAdapter == null)
        continue;
      this.stickersGridAdapter.notifyDataSetChanged();
    }
    while (!bool);
    updateStickerTabs();
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.stickersDidLoaded)
      if (((Integer)paramArrayOfObject[0]).intValue() == this.currentType)
      {
        updateStickerTabs();
        reloadStickersAdapter();
        checkPanels();
      }
    do
      return;
    while ((paramInt != NotificationCenter.recentDocumentsDidLoaded) || (((Boolean)paramArrayOfObject[0]).booleanValue()) || (((Integer)paramArrayOfObject[1]).intValue() != this.currentType));
    checkDocuments();
  }

  public int getCurrentType()
  {
    return this.currentType;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickerMasksView.this.updateStickerTabs();
        StickerMasksView.this.reloadStickersAdapter();
      }
    });
  }

  public void onDestroy()
  {
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.lastNotifyWidth != paramInt3 - paramInt1)
    {
      this.lastNotifyWidth = (paramInt3 - paramInt1);
      reloadStickersAdapter();
    }
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
  }

  public void setListener(Listener paramListener)
  {
    this.listener = paramListener;
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt != 8)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
      updateStickerTabs();
      reloadStickersAdapter();
      checkDocuments();
      StickersQuery.loadRecents(0, false, true);
      StickersQuery.loadRecents(1, false, true);
    }
  }

  public static abstract interface Listener
  {
    public abstract void onStickerSelected(TLRPC.Document paramDocument);

    public abstract void onTypeChanged();
  }

  private class StickersGridAdapter extends RecyclerListView.SelectionAdapter
  {
    private HashMap<Integer, TLRPC.Document> cache = new HashMap();
    private Context context;
    private HashMap<TLRPC.TL_messages_stickerSet, Integer> packStartRow = new HashMap();
    private HashMap<Integer, TLRPC.TL_messages_stickerSet> rowStartPack = new HashMap();
    private int stickersPerRow;
    private int totalItems;

    public StickersGridAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public Object getItem(int paramInt)
    {
      return this.cache.get(Integer.valueOf(paramInt));
    }

    public int getItemCount()
    {
      if (this.totalItems != 0)
        return this.totalItems + 1;
      return 0;
    }

    public int getItemViewType(int paramInt)
    {
      if (this.cache.get(Integer.valueOf(paramInt)) != null)
        return 0;
      return 1;
    }

    public int getPositionForPack(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
    {
      return ((Integer)this.packStartRow.get(paramTL_messages_stickerSet)).intValue() * this.stickersPerRow;
    }

    public int getTabForPosition(int paramInt)
    {
      if (this.stickersPerRow == 0)
      {
        int j = StickerMasksView.this.getMeasuredWidth();
        int i = j;
        if (j == 0)
          i = AndroidUtilities.displaySize.x;
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      }
      paramInt /= this.stickersPerRow;
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
      if (localTL_messages_stickerSet == null)
        return StickerMasksView.this.recentTabBum;
      return StickerMasksView.this.stickerSets[StickerMasksView.this.currentType].indexOf(localTL_messages_stickerSet) + StickerMasksView.this.stickersTabOffset;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void notifyDataSetChanged()
    {
      int j = StickerMasksView.this.getMeasuredWidth();
      int i = j;
      if (j == 0)
        i = AndroidUtilities.displaySize.x;
      this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      StickerMasksView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
      this.rowStartPack.clear();
      this.packStartRow.clear();
      this.cache.clear();
      this.totalItems = 0;
      ArrayList localArrayList2 = StickerMasksView.this.stickerSets[StickerMasksView.this.currentType];
      i = -1;
      if (i < localArrayList2.size())
      {
        TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = null;
        int k = this.totalItems / this.stickersPerRow;
        ArrayList localArrayList1;
        if (i == -1)
        {
          localArrayList1 = StickerMasksView.this.recentStickers[StickerMasksView.this.currentType];
          label135: if (!localArrayList1.isEmpty())
            break label185;
        }
        while (true)
        {
          i += 1;
          break;
          localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList2.get(i);
          localArrayList1 = localTL_messages_stickerSet.documents;
          this.packStartRow.put(localTL_messages_stickerSet, Integer.valueOf(k));
          break label135;
          label185: int m = (int)Math.ceil(localArrayList1.size() / this.stickersPerRow);
          j = 0;
          while (j < localArrayList1.size())
          {
            this.cache.put(Integer.valueOf(this.totalItems + j), localArrayList1.get(j));
            j += 1;
          }
          this.totalItems += this.stickersPerRow * m;
          j = 0;
          while (j < m)
          {
            this.rowStartPack.put(Integer.valueOf(k + j), localTL_messages_stickerSet);
            j += 1;
          }
        }
      }
      super.notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      Object localObject;
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 0:
        localObject = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
        ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)localObject, false);
        return;
      case 1:
      }
      if (paramInt == this.totalItems)
      {
        paramInt = (paramInt - 1) / this.stickersPerRow;
        localObject = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
        if (localObject == null)
        {
          ((EmptyCell)paramViewHolder.itemView).setHeight(1);
          return;
        }
        paramInt = StickerMasksView.this.stickersGridView.getMeasuredHeight() - (int)Math.ceil(((TLRPC.TL_messages_stickerSet)localObject).documents.size() / this.stickersPerRow) * AndroidUtilities.dp(82.0F);
        paramViewHolder = (EmptyCell)paramViewHolder.itemView;
        if (paramInt > 0);
        while (true)
        {
          paramViewHolder.setHeight(paramInt);
          return;
          paramInt = 1;
        }
      }
      ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(82.0F));
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
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new StickerEmojiCell(this.context)
        {
          public void onMeasure(int paramInt1, int paramInt2)
          {
            super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0F), 1073741824));
          }
        };
        continue;
        paramViewGroup = new EmptyCell(this.context);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.StickerMasksView
 * JD-Core Version:    0.6.0
 */