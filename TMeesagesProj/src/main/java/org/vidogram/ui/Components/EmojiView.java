package org.vidogram.ui.Components;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.f;
import android.support.v4.view.ab;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.TextView;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.EmojiData;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.GridLayoutManager.SpanSizeLookup;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.ContextLinkCell;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.FeaturedStickerSetInfoCell;
import org.vidogram.ui.Cells.StickerEmojiCell;
import org.vidogram.ui.StickerPreviewViewer;
import org.vidogram.ui.StickerPreviewViewer.StickerPreviewViewerDelegate;

public class EmojiView extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final ViewTreeObserver.OnScrollChangedListener NOP;
  private static HashMap<String, String> emojiColor;
  private static final Field superListenerField;
  private ArrayList<EmojiGridAdapter> adapters = new ArrayList();
  private ImageView backspaceButton;
  private boolean backspaceOnce;
  private boolean backspacePressed;
  private int currentBackgroundType = -1;
  private int currentPage;
  private Paint dotPaint;
  private ArrayList<GridView> emojiGrids = new ArrayList();
  private int emojiSize;
  private LinearLayout emojiTab;
  private HashMap<String, Integer> emojiUseHistory = new HashMap();
  private int featuredStickersHash;
  private ExtendedGridLayoutManager flowLayoutManager;
  private int gifTabNum = -2;
  private GifsAdapter gifsAdapter;
  private RecyclerListView gifsGridView;
  private Drawable[] icons;
  private HashMap<Long, TLRPC.StickerSetCovered> installingStickerSets = new HashMap();
  private boolean isLayout;
  private int lastNotifyWidth;
  private Listener listener;
  private int[] location = new int[2];
  private int minusDy;
  private int oldWidth;
  private Object outlineProvider;
  private ViewPager pager;
  private PagerSlidingTabStrip pagerSlidingTabStrip;
  private EmojiColorPickerView pickerView;
  private EmojiPopupWindow pickerViewPopup;
  private int popupHeight;
  private int popupWidth;
  private ArrayList<String> recentEmoji = new ArrayList();
  private ArrayList<TLRPC.Document> recentGifs = new ArrayList();
  private ArrayList<TLRPC.Document> recentStickers = new ArrayList();
  private int recentTabBum = -2;
  private HashMap<Long, TLRPC.StickerSetCovered> removingStickerSets = new HashMap();
  private boolean showGifs;
  private StickerPreviewViewer.StickerPreviewViewerDelegate stickerPreviewViewerDelegate = new StickerPreviewViewer.StickerPreviewViewerDelegate()
  {
    public void openSet(TLRPC.InputStickerSet paramInputStickerSet)
    {
      if (paramInputStickerSet == null)
        return;
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
      if (paramInputStickerSet.id != 0L)
      {
        localTL_messages_stickerSet = StickersQuery.getStickerSetById(Long.valueOf(paramInputStickerSet.id));
        if (localTL_messages_stickerSet == null)
          break label82;
      }
      label82: for (int i = EmojiView.this.stickersGridAdapter.getPositionForPack(localTL_messages_stickerSet); ; i = -1)
      {
        if (i == -1)
          break label87;
        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(i, 0);
        return;
        if (paramInputStickerSet.short_name != null)
        {
          localTL_messages_stickerSet = StickersQuery.getStickerSetByName(paramInputStickerSet.short_name);
          break;
        }
        localTL_messages_stickerSet = null;
        break;
      }
      label87: EmojiView.this.listener.onShowStickerSet(null, paramInputStickerSet);
    }

    public void sentSticker(TLRPC.Document paramDocument)
    {
      EmojiView.this.listener.onStickerSelected(paramDocument);
    }
  };
  private ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = new ArrayList();
  private TextView stickersEmptyView;
  private StickersGridAdapter stickersGridAdapter;
  private RecyclerListView stickersGridView;
  private GridLayoutManager stickersLayoutManager;
  private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
  private ScrollSlidingTabStrip stickersTab;
  private int stickersTabOffset;
  private FrameLayout stickersWrap;
  private boolean switchToGifTab;
  private TrendingGridAdapter trendingGridAdapter;
  private RecyclerListView trendingGridView;
  private GridLayoutManager trendingLayoutManager;
  private boolean trendingLoaded;
  private int trendingTabNum = -2;
  private ArrayList<View> views = new ArrayList();

  static
  {
    Object localObject = null;
    try
    {
      Field localField = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
      localObject = localField;
      localField.setAccessible(true);
      localObject = localField;
      label19: superListenerField = localObject;
      NOP = new ViewTreeObserver.OnScrollChangedListener()
      {
        public void onScrollChanged()
        {
        }
      };
      emojiColor = new HashMap();
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      break label19;
    }
  }

  public EmojiView(boolean paramBoolean1, boolean paramBoolean2, Context paramContext)
  {
    super(paramContext);
    Object localObject1 = paramContext.getResources().getDrawable(2130837845);
    Theme.setDrawableColorByKey((Drawable)localObject1, "chat_emojiPanelIcon");
    this.icons = new Drawable[] { Theme.createEmojiIconSelectorDrawable(paramContext, 2130837843, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(paramContext, 2130837844, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(paramContext, 2130837841, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(paramContext, 2130837840, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(paramContext, 2130837839, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), Theme.createEmojiIconSelectorDrawable(paramContext, 2130837842, Theme.getColor("chat_emojiPanelIcon"), Theme.getColor("chat_emojiPanelIconSelected")), localObject1 };
    this.showGifs = paramBoolean2;
    this.dotPaint = new Paint(1);
    this.dotPaint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
    if (Build.VERSION.SDK_INT >= 21)
      this.outlineProvider = new ViewOutlineProvider()
      {
        @TargetApi(21)
        public void getOutline(View paramView, Outline paramOutline)
        {
          paramOutline.setRoundRect(paramView.getPaddingLeft(), paramView.getPaddingTop(), paramView.getMeasuredWidth() - paramView.getPaddingRight(), paramView.getMeasuredHeight() - paramView.getPaddingBottom(), AndroidUtilities.dp(6.0F));
        }
      };
    int i = 0;
    if (i < EmojiData.dataColored.length + 1)
    {
      localObject1 = new GridView(paramContext);
      if (AndroidUtilities.isTablet())
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(60.0F));
      while (true)
      {
        ((GridView)localObject1).setNumColumns(-1);
        localObject2 = new EmojiGridAdapter(i - 1);
        ((GridView)localObject1).setAdapter((ListAdapter)localObject2);
        this.adapters.add(localObject2);
        this.emojiGrids.add(localObject1);
        localObject2 = new FrameLayout(paramContext);
        ((FrameLayout)localObject2).addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
        this.views.add(localObject2);
        i += 1;
        break;
        ((GridView)localObject1).setColumnWidth(AndroidUtilities.dp(45.0F));
      }
    }
    if (paramBoolean1)
    {
      this.stickersWrap = new FrameLayout(paramContext);
      StickersQuery.checkStickers(0);
      StickersQuery.checkFeaturedStickers();
      this.stickersGridView = new RecyclerListView(paramContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          boolean bool = StickerPreviewViewer.getInstance().onInterceptTouchEvent(paramMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickerPreviewViewerDelegate);
          return (super.onInterceptTouchEvent(paramMotionEvent)) || (bool);
        }

        public void setVisibility(int paramInt)
        {
          if (((EmojiView.this.gifsGridView != null) && (EmojiView.this.gifsGridView.getVisibility() == 0)) || ((EmojiView.this.trendingGridView != null) && (EmojiView.this.trendingGridView.getVisibility() == 0)))
          {
            super.setVisibility(8);
            return;
          }
          super.setVisibility(paramInt);
        }
      };
      localObject1 = this.stickersGridView;
      localObject2 = new GridLayoutManager(paramContext, 5);
      this.stickersLayoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
      {
        public int getSpanSize(int paramInt)
        {
          if (paramInt == EmojiView.StickersGridAdapter.access$3000(EmojiView.this.stickersGridAdapter))
            return EmojiView.StickersGridAdapter.access$3100(EmojiView.this.stickersGridAdapter);
          return 1;
        }
      });
      this.stickersGridView.setPadding(0, AndroidUtilities.dp(52.0F), 0, 0);
      this.stickersGridView.setClipToPadding(false);
      this.views.add(this.stickersWrap);
      localObject1 = this.stickersGridView;
      localObject2 = new StickersGridAdapter(paramContext);
      this.stickersGridAdapter = ((StickersGridAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.stickersGridView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return StickerPreviewViewer.getInstance().onTouch(paramMotionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.stickersOnItemClickListener, EmojiView.this.stickerPreviewViewerDelegate);
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
          paramView.disable();
          EmojiView.this.listener.onStickerSelected(paramView.getSticker());
        }
      };
      this.stickersGridView.setOnItemClickListener(this.stickersOnItemClickListener);
      this.stickersGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
      this.stickersWrap.addView(this.stickersGridView);
      this.trendingGridView = new RecyclerListView(paramContext);
      this.trendingGridView.setItemAnimator(null);
      this.trendingGridView.setLayoutAnimation(null);
      localObject1 = this.trendingGridView;
      localObject2 = new GridLayoutManager(paramContext, 5)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.trendingLayoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.trendingLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
      {
        public int getSpanSize(int paramInt)
        {
          if (((EmojiView.TrendingGridAdapter.access$3400(EmojiView.this.trendingGridAdapter).get(Integer.valueOf(paramInt)) instanceof Integer)) || (paramInt == EmojiView.TrendingGridAdapter.access$3500(EmojiView.this.trendingGridAdapter)))
            return EmojiView.TrendingGridAdapter.access$3600(EmojiView.this.trendingGridAdapter);
          return 1;
        }
      });
      this.trendingGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          EmojiView.this.checkStickersTabY(paramRecyclerView, paramInt2);
        }
      });
      this.trendingGridView.setClipToPadding(false);
      this.trendingGridView.setPadding(0, AndroidUtilities.dp(48.0F), 0, 0);
      localObject1 = this.trendingGridView;
      localObject2 = new TrendingGridAdapter(paramContext);
      this.trendingGridAdapter = ((TrendingGridAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.trendingGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          paramView = (TLRPC.StickerSetCovered)EmojiView.TrendingGridAdapter.access$3800(EmojiView.this.trendingGridAdapter).get(Integer.valueOf(paramInt));
          if (paramView != null)
            EmojiView.this.listener.onShowStickerSet(paramView.set, null);
        }
      });
      this.trendingGridAdapter.notifyDataSetChanged();
      this.trendingGridView.setGlowColor(Theme.getColor("chat_emojiPanelBackground"));
      this.trendingGridView.setVisibility(8);
      this.stickersWrap.addView(this.trendingGridView);
      if (paramBoolean2)
      {
        this.gifsGridView = new RecyclerListView(paramContext);
        this.gifsGridView.setClipToPadding(false);
        this.gifsGridView.setPadding(0, AndroidUtilities.dp(48.0F), 0, 0);
        localObject1 = this.gifsGridView;
        localObject2 = new ExtendedGridLayoutManager(paramContext, 100)
        {
          private Size size = new Size();

          protected Size getSizeForItem(int paramInt)
          {
            float f2 = 100.0F;
            TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramInt);
            Object localObject = this.size;
            float f1;
            if ((localDocument.thumb != null) && (localDocument.thumb.w != 0))
            {
              f1 = localDocument.thumb.w;
              ((Size)localObject).width = f1;
              localObject = this.size;
              f1 = f2;
              if (localDocument.thumb != null)
              {
                f1 = f2;
                if (localDocument.thumb.h != 0)
                  f1 = localDocument.thumb.h;
              }
              ((Size)localObject).height = f1;
              paramInt = 0;
            }
            while (true)
            {
              if (paramInt < localDocument.attributes.size())
              {
                localObject = (TLRPC.DocumentAttribute)localDocument.attributes.get(paramInt);
                if (((localObject instanceof TLRPC.TL_documentAttributeImageSize)) || ((localObject instanceof TLRPC.TL_documentAttributeVideo)))
                {
                  this.size.width = ((TLRPC.DocumentAttribute)localObject).w;
                  this.size.height = ((TLRPC.DocumentAttribute)localObject).h;
                }
              }
              else
              {
                return this.size;
                f1 = 100.0F;
                break;
              }
              paramInt += 1;
            }
          }
        };
        this.flowLayoutManager = ((ExtendedGridLayoutManager)localObject2);
        ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
        this.flowLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup()
        {
          public int getSpanSize(int paramInt)
          {
            return EmojiView.this.flowLayoutManager.getSpanSizeForItem(paramInt);
          }
        });
        this.gifsGridView.addItemDecoration(new RecyclerView.ItemDecoration()
        {
          public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
          {
            int i = 0;
            paramRect.left = 0;
            paramRect.top = 0;
            paramRect.bottom = 0;
            int j = paramRecyclerView.getChildAdapterPosition(paramView);
            if (!EmojiView.this.flowLayoutManager.isFirstRow(j))
              paramRect.top = AndroidUtilities.dp(2.0F);
            if (EmojiView.this.flowLayoutManager.isLastInRow(j));
            while (true)
            {
              paramRect.right = i;
              return;
              i = AndroidUtilities.dp(2.0F);
            }
          }
        });
        this.gifsGridView.setOverScrollMode(2);
        localObject1 = this.gifsGridView;
        localObject2 = new GifsAdapter(paramContext);
        this.gifsAdapter = ((GifsAdapter)localObject2);
        ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
        this.gifsGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
          {
            EmojiView.this.checkStickersTabY(paramRecyclerView, paramInt2);
          }
        });
        this.gifsGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
        {
          public void onItemClick(View paramView, int paramInt)
          {
            if ((paramInt < 0) || (paramInt >= EmojiView.this.recentGifs.size()) || (EmojiView.this.listener == null))
              return;
            EmojiView.this.listener.onGifSelected((TLRPC.Document)EmojiView.this.recentGifs.get(paramInt));
          }
        });
        this.gifsGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramView, int paramInt)
          {
            if ((paramInt < 0) || (paramInt >= EmojiView.this.recentGifs.size()))
              return false;
            TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramInt);
            paramView = new AlertDialog.Builder(paramView.getContext());
            paramView.setTitle(LocaleController.getString("AppName", 2131165319));
            paramView.setMessage(LocaleController.getString("DeleteGif", 2131165642));
            paramView.setPositiveButton(LocaleController.getString("OK", 2131166153).toUpperCase(), new DialogInterface.OnClickListener(localDocument)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                StickersQuery.removeRecentGif(this.val$searchImage);
                EmojiView.access$3902(EmojiView.this, StickersQuery.getRecentGifs());
                if (EmojiView.this.gifsAdapter != null)
                  EmojiView.this.gifsAdapter.notifyDataSetChanged();
                if (EmojiView.this.recentGifs.isEmpty())
                  EmojiView.this.updateStickerTabs();
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            paramView.show().setCanceledOnTouchOutside(true);
            return true;
          }
        });
        this.gifsGridView.setVisibility(8);
        this.stickersWrap.addView(this.gifsGridView);
      }
      this.stickersEmptyView = new TextView(paramContext);
      this.stickersEmptyView.setText(LocaleController.getString("NoStickers", 2131166053));
      this.stickersEmptyView.setTextSize(1, 18.0F);
      this.stickersEmptyView.setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
      this.stickersWrap.addView(this.stickersEmptyView, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 48.0F, 0.0F, 0.0F));
      this.stickersGridView.setEmptyView(this.stickersEmptyView);
      this.stickersTab = new ScrollSlidingTabStrip(paramContext)
      {
        boolean first = true;
        float lastTranslateX;
        float lastX;
        boolean startedScroll;

        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          if (getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(true);
          return super.onInterceptTouchEvent(paramMotionEvent);
        }

        public boolean onTouchEvent(MotionEvent paramMotionEvent)
        {
          int j = 0;
          if (this.first)
          {
            this.first = false;
            this.lastX = paramMotionEvent.getX();
          }
          float f = EmojiView.this.stickersTab.getTranslationX();
          if ((EmojiView.this.stickersTab.getScrollX() == 0) && (f == 0.0F))
          {
            if ((this.startedScroll) || (this.lastX - paramMotionEvent.getX() >= 0.0F))
              break label220;
            if (EmojiView.this.pager.beginFakeDrag())
            {
              this.startedScroll = true;
              this.lastTranslateX = EmojiView.this.stickersTab.getTranslationX();
            }
          }
          int i;
          if (this.startedScroll)
            i = (int)(paramMotionEvent.getX() - this.lastX + f - this.lastTranslateX);
          while (true)
          {
            try
            {
              EmojiView.this.pager.fakeDragBy(i);
              this.lastTranslateX = f;
              this.lastX = paramMotionEvent.getX();
              if ((paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 1))
                continue;
              this.first = true;
              if (!this.startedScroll)
                continue;
              EmojiView.this.pager.endFakeDrag();
              this.startedScroll = false;
              if ((!this.startedScroll) && (!super.onTouchEvent(paramMotionEvent)))
                continue;
              j = 1;
              return j;
              label220: if ((!this.startedScroll) || (this.lastX - paramMotionEvent.getX() <= 0.0F) || (!EmojiView.this.pager.isFakeDragging()))
                break;
              EmojiView.this.pager.endFakeDrag();
              this.startedScroll = false;
            }
            catch (Exception localException1)
            {
            }
            try
            {
              EmojiView.this.pager.endFakeDrag();
              label284: this.startedScroll = false;
              FileLog.e(localException1);
            }
            catch (Exception localException2)
            {
              break label284;
            }
          }
        }
      };
      this.stickersTab.setUnderlineHeight(AndroidUtilities.dp(1.0F));
      this.stickersTab.setIndicatorColor(Theme.getColor("chat_emojiPanelStickerPackSelector"));
      this.stickersTab.setUnderlineColor(Theme.getColor("chat_emojiPanelStickerPackSelector"));
      this.stickersTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
      this.stickersTab.setVisibility(4);
      addView(this.stickersTab, LayoutHelper.createFrame(-1, 48, 51));
      this.stickersTab.setTranslationX(AndroidUtilities.displaySize.x);
      updateStickerTabs();
      this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate()
      {
        public void onPageSelected(int paramInt)
        {
          int i = 8;
          if (EmojiView.this.gifsGridView != null)
          {
            if (paramInt != EmojiView.this.gifTabNum + 1)
              break label75;
            if (EmojiView.this.gifsGridView.getVisibility() != 0)
            {
              EmojiView.this.listener.onGifTab(true);
              EmojiView.this.showGifTab();
            }
          }
          if (paramInt == 0)
            EmojiView.this.pager.setCurrentItem(0);
          label75: 
          do
          {
            Object localObject;
            do
            {
              return;
              if (paramInt == EmojiView.this.trendingTabNum + 1)
              {
                if (EmojiView.this.trendingGridView.getVisibility() == 0)
                  break;
                EmojiView.this.showTrendingTab();
                break;
              }
              if (EmojiView.this.gifsGridView.getVisibility() == 0)
              {
                EmojiView.this.listener.onGifTab(false);
                EmojiView.this.gifsGridView.setVisibility(8);
                EmojiView.this.stickersGridView.setVisibility(0);
                localObject = EmojiView.this.stickersEmptyView;
                if (EmojiView.this.stickersGridAdapter.getItemCount() != 0);
                while (true)
                {
                  ((TextView)localObject).setVisibility(i);
                  EmojiView.this.checkScroll();
                  EmojiView.this.saveNewPage();
                  break;
                  i = 0;
                }
              }
              if (EmojiView.this.trendingGridView.getVisibility() != 0)
                break;
              EmojiView.this.trendingGridView.setVisibility(8);
              EmojiView.this.stickersGridView.setVisibility(0);
              localObject = EmojiView.this.stickersEmptyView;
              if (EmojiView.this.stickersGridAdapter.getItemCount() != 0);
              while (true)
              {
                ((TextView)localObject).setVisibility(i);
                EmojiView.this.saveNewPage();
                break;
                i = 0;
              }
            }
            while ((paramInt == EmojiView.this.gifTabNum + 1) || (paramInt == EmojiView.this.trendingTabNum + 1));
            if (paramInt == EmojiView.this.recentTabBum + 1)
            {
              EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
              EmojiView.this.checkStickersTabY(null, 0);
              localObject = EmojiView.this.stickersTab;
              i = EmojiView.this.recentTabBum;
              if (EmojiView.this.recentTabBum > 0);
              for (paramInt = EmojiView.this.recentTabBum; ; paramInt = EmojiView.this.stickersTabOffset)
              {
                ((ScrollSlidingTabStrip)localObject).onPageScrolled(i + 1, paramInt + 1);
                return;
              }
            }
            i = paramInt - 1 - EmojiView.this.stickersTabOffset;
            if (i < EmojiView.this.stickerSets.size())
              break label450;
          }
          while (EmojiView.this.listener == null);
          EmojiView.this.listener.onStickersSettingsClick();
          return;
          label450: paramInt = i;
          if (i >= EmojiView.this.stickerSets.size())
            paramInt = EmojiView.this.stickerSets.size() - 1;
          EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(EmojiView.this.stickersGridAdapter.getPositionForPack((TLRPC.TL_messages_stickerSet)EmojiView.this.stickerSets.get(paramInt)), 0);
          EmojiView.this.checkStickersTabY(null, 0);
          EmojiView.this.checkScroll();
        }
      });
      this.stickersGridView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          EmojiView.this.checkScroll();
          EmojiView.this.checkStickersTabY(paramRecyclerView, paramInt2);
        }
      });
    }
    this.pager = new ViewPager(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        if (getParent() != null)
          getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(paramMotionEvent);
      }
    };
    this.pager.setAdapter(new EmojiPagesAdapter(null));
    this.emojiTab = new LinearLayout(paramContext)
    {
      public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
      {
        if (getParent() != null)
          getParent().requestDisallowInterceptTouchEvent(true);
        return super.onInterceptTouchEvent(paramMotionEvent);
      }
    };
    this.emojiTab.setOrientation(0);
    addView(this.emojiTab, LayoutHelper.createFrame(-1, 48.0F));
    this.pagerSlidingTabStrip = new PagerSlidingTabStrip(paramContext);
    this.pagerSlidingTabStrip.setViewPager(this.pager);
    this.pagerSlidingTabStrip.setShouldExpand(true);
    this.pagerSlidingTabStrip.setIndicatorHeight(AndroidUtilities.dp(2.0F));
    this.pagerSlidingTabStrip.setUnderlineHeight(AndroidUtilities.dp(1.0F));
    this.pagerSlidingTabStrip.setIndicatorColor(Theme.getColor("chat_emojiPanelIconSelector"));
    this.pagerSlidingTabStrip.setUnderlineColor(Theme.getColor("chat_emojiPanelShadowLine"));
    this.emojiTab.addView(this.pagerSlidingTabStrip, LayoutHelper.createLinear(0, 48, 1.0F));
    this.pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.f()
    {
      public void onPageScrollStateChanged(int paramInt)
      {
      }

      public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
      {
        EmojiView.this.onPageScrolled(paramInt1, EmojiView.this.getMeasuredWidth() - EmojiView.this.getPaddingLeft() - EmojiView.this.getPaddingRight(), paramInt2);
      }

      public void onPageSelected(int paramInt)
      {
        EmojiView.this.saveNewPage();
      }
    });
    localObject1 = new FrameLayout(paramContext);
    this.emojiTab.addView((View)localObject1, LayoutHelper.createLinear(52, 48));
    this.backspaceButton = new ImageView(paramContext)
    {
      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        if (paramMotionEvent.getAction() == 0)
        {
          EmojiView.access$5602(EmojiView.this, true);
          EmojiView.access$5702(EmojiView.this, false);
          EmojiView.this.postBackspaceRunnable(350);
        }
        while (true)
        {
          super.onTouchEvent(paramMotionEvent);
          return true;
          if ((paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 1))
            continue;
          EmojiView.access$5602(EmojiView.this, false);
          if ((EmojiView.this.backspaceOnce) || (EmojiView.this.listener == null) || (!EmojiView.this.listener.onBackspace()))
            continue;
          EmojiView.this.backspaceButton.performHapticFeedback(3);
        }
      }
    };
    this.backspaceButton.setImageResource(2130837846);
    this.backspaceButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackspace"), PorterDuff.Mode.MULTIPLY));
    this.backspaceButton.setScaleType(ImageView.ScaleType.CENTER);
    ((FrameLayout)localObject1).addView(this.backspaceButton, LayoutHelper.createFrame(52, 48.0F));
    Object localObject2 = new View(paramContext);
    ((View)localObject2).setBackgroundColor(Theme.getColor("chat_emojiPanelShadowLine"));
    ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(52, 1, 83));
    localObject1 = new TextView(paramContext);
    ((TextView)localObject1).setText(LocaleController.getString("NoRecent", 2131166042));
    ((TextView)localObject1).setTextSize(1, 18.0F);
    ((TextView)localObject1).setTextColor(Theme.getColor("chat_emojiPanelEmptyText"));
    ((TextView)localObject1).setGravity(17);
    ((TextView)localObject1).setClickable(false);
    ((TextView)localObject1).setFocusable(false);
    ((FrameLayout)this.views.get(0)).addView((View)localObject1, LayoutHelper.createFrame(-2, -2.0F, 17, 0.0F, 48.0F, 0.0F, 0.0F));
    ((GridView)this.emojiGrids.get(0)).setEmptyView((View)localObject1);
    addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
    float f;
    if (AndroidUtilities.isTablet())
    {
      f = 40.0F;
      this.emojiSize = AndroidUtilities.dp(f);
      this.pickerView = new EmojiColorPickerView(paramContext);
      paramContext = this.pickerView;
      if (!AndroidUtilities.isTablet())
        break label2013;
      i = 40;
      label1851: i = AndroidUtilities.dp(i * 6 + 10 + 20);
      this.popupWidth = i;
      if (!AndroidUtilities.isTablet())
        break label2020;
      f = 64.0F;
    }
    while (true)
    {
      int j = AndroidUtilities.dp(f);
      this.popupHeight = j;
      this.pickerViewPopup = new EmojiPopupWindow(paramContext, i, j);
      this.pickerViewPopup.setOutsideTouchable(true);
      this.pickerViewPopup.setClippingEnabled(true);
      this.pickerViewPopup.setInputMethodMode(2);
      this.pickerViewPopup.setSoftInputMode(0);
      this.pickerViewPopup.getContentView().setFocusableInTouchMode(true);
      this.pickerViewPopup.getContentView().setOnKeyListener(new View.OnKeyListener()
      {
        public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
        {
          if ((paramInt == 82) && (paramKeyEvent.getRepeatCount() == 0) && (paramKeyEvent.getAction() == 1) && (EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
          {
            EmojiView.this.pickerViewPopup.dismiss();
            return true;
          }
          return false;
        }
      });
      this.currentPage = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
      loadRecents();
      return;
      f = 32.0F;
      break;
      label2013: i = 32;
      break label1851;
      label2020: f = 56.0F;
    }
  }

  private static String addColorToCode(String paramString1, String paramString2)
  {
    Object localObject2 = null;
    int i = paramString1.length();
    Object localObject1;
    String str;
    if ((i > 2) && (paramString1.charAt(paramString1.length() - 2) == '‍'))
    {
      localObject1 = paramString1.substring(paramString1.length() - 2);
      str = paramString1.substring(0, paramString1.length() - 2);
    }
    while (true)
    {
      paramString1 = str + paramString2;
      if (localObject1 == null)
        break;
      return paramString1 + (String)localObject1;
      localObject1 = localObject2;
      str = paramString1;
      if (i <= 3)
        continue;
      localObject1 = localObject2;
      str = paramString1;
      if (paramString1.charAt(paramString1.length() - 3) != '‍')
        continue;
      localObject1 = paramString1.substring(paramString1.length() - 3);
      str = paramString1.substring(0, paramString1.length() - 3);
    }
    return (String)paramString1;
  }

  private void checkDocuments(boolean paramBoolean)
  {
    int i;
    if (paramBoolean)
    {
      i = this.recentGifs.size();
      this.recentGifs = StickersQuery.getRecentGifs();
      if (this.gifsAdapter != null)
        this.gifsAdapter.notifyDataSetChanged();
      if (i != this.recentGifs.size())
        updateStickerTabs();
    }
    do
    {
      return;
      i = this.recentStickers.size();
      this.recentStickers = StickersQuery.getRecentStickers(0);
      if (this.stickersGridAdapter == null)
        continue;
      this.stickersGridAdapter.notifyDataSetChanged();
    }
    while (i == this.recentStickers.size());
    updateStickerTabs();
  }

  private void checkPanels()
  {
    int j = 8;
    if (this.stickersTab == null);
    label176: 
    do
    {
      do
      {
        return;
        if ((this.trendingTabNum == -2) && (this.trendingGridView != null) && (this.trendingGridView.getVisibility() == 0))
        {
          this.gifsGridView.setVisibility(8);
          this.trendingGridView.setVisibility(8);
          this.stickersGridView.setVisibility(0);
          localObject = this.stickersEmptyView;
          if (this.stickersGridAdapter.getItemCount() != 0)
          {
            i = 8;
            ((TextView)localObject).setVisibility(i);
          }
        }
        else
        {
          if ((this.gifTabNum != -2) || (this.gifsGridView == null) || (this.gifsGridView.getVisibility() != 0))
            continue;
          this.listener.onGifTab(false);
          this.gifsGridView.setVisibility(8);
          this.trendingGridView.setVisibility(8);
          this.stickersGridView.setVisibility(0);
          localObject = this.stickersEmptyView;
          if (this.stickersGridAdapter.getItemCount() == 0)
            break label176;
        }
        for (i = j; ; i = 0)
        {
          ((TextView)localObject).setVisibility(i);
          return;
          i = 0;
          break;
        }
      }
      while (this.gifTabNum == -2);
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0))
      {
        localObject = this.stickersTab;
        j = this.gifTabNum;
        if (this.recentTabBum > 0);
        for (i = this.recentTabBum; ; i = this.stickersTabOffset)
        {
          ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
          return;
        }
      }
      if ((this.trendingGridView != null) && (this.trendingGridView.getVisibility() == 0))
      {
        localObject = this.stickersTab;
        j = this.trendingTabNum;
        if (this.recentTabBum > 0);
        for (i = this.recentTabBum; ; i = this.stickersTabOffset)
        {
          ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
          return;
        }
      }
      i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    }
    while (i == -1);
    Object localObject = this.stickersTab;
    j = this.stickersGridAdapter.getTabForPosition(i);
    if (this.recentTabBum > 0);
    for (int i = this.recentTabBum; ; i = this.stickersTabOffset)
    {
      ((ScrollSlidingTabStrip)localObject).onPageScrolled(j + 1, i + 1);
      return;
    }
  }

  private void checkScroll()
  {
    int i = this.stickersLayoutManager.findFirstVisibleItemPosition();
    if (i == -1);
    do
      return;
    while (this.stickersGridView == null);
    if (this.stickersGridView.getVisibility() != 0)
    {
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() != 0))
        this.gifsGridView.setVisibility(0);
      if ((this.stickersEmptyView != null) && (this.stickersEmptyView.getVisibility() == 0))
        this.stickersEmptyView.setVisibility(8);
      localScrollSlidingTabStrip = this.stickersTab;
      j = this.gifTabNum;
      if (this.recentTabBum > 0);
      for (i = this.recentTabBum; ; i = this.stickersTabOffset)
      {
        localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
        return;
      }
    }
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int j = this.stickersGridAdapter.getTabForPosition(i);
    if (this.recentTabBum > 0);
    for (i = this.recentTabBum; ; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      return;
    }
  }

  private void checkStickersTabY(View paramView, int paramInt)
  {
    if (paramView == null)
    {
      paramView = this.stickersTab;
      this.minusDy = 0;
      paramView.setTranslationY(0);
    }
    do
      return;
    while (paramView.getVisibility() != 0);
    this.minusDy -= paramInt;
    if (this.minusDy > 0)
      this.minusDy = 0;
    while (true)
    {
      this.stickersTab.setTranslationY(Math.max(-AndroidUtilities.dp(47.0F), this.minusDy));
      return;
      if (this.minusDy >= -AndroidUtilities.dp(288.0F))
        continue;
      this.minusDy = (-AndroidUtilities.dp(288.0F));
    }
  }

  private void onPageScrolled(int paramInt1, int paramInt2, int paramInt3)
  {
    boolean bool = true;
    int j = 0;
    if (this.stickersTab == null);
    label188: label189: 
    while (true)
    {
      return;
      int i = paramInt2;
      if (paramInt2 == 0)
        i = AndroidUtilities.displaySize.x;
      Object localObject;
      if (paramInt1 == 5)
      {
        paramInt2 = -paramInt3;
        paramInt1 = paramInt2;
        if (this.listener == null)
          break label188;
        localObject = this.listener;
        if (paramInt3 != 0)
        {
          ((Listener)localObject).onStickersTab(bool);
          paramInt1 = paramInt2;
        }
      }
      while (true)
      {
        label67: if (this.emojiTab.getTranslationX() == paramInt1)
          break label189;
        this.emojiTab.setTranslationX(paramInt1);
        this.stickersTab.setTranslationX(i + paramInt1);
        localObject = this.stickersTab;
        if (paramInt1 < 0);
        for (paramInt1 = j; ; paramInt1 = 4)
        {
          ((ScrollSlidingTabStrip)localObject).setVisibility(paramInt1);
          return;
          bool = false;
          break;
          if (paramInt1 == 6)
          {
            paramInt2 = -i;
            paramInt1 = paramInt2;
            if (this.listener == null)
              break label188;
            this.listener.onStickersTab(true);
            paramInt1 = paramInt2;
            break label67;
          }
          if (this.listener != null)
            this.listener.onStickersTab(false);
          paramInt1 = 0;
          break label67;
        }
      }
    }
  }

  private void postBackspaceRunnable(int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInt)
    {
      public void run()
      {
        if (!EmojiView.this.backspacePressed)
          return;
        if ((EmojiView.this.listener != null) && (EmojiView.this.listener.onBackspace()))
          EmojiView.this.backspaceButton.performHapticFeedback(3);
        EmojiView.access$5702(EmojiView.this, true);
        EmojiView.this.postBackspaceRunnable(Math.max(50, this.val$time - 100));
      }
    }
    , paramInt);
  }

  private void reloadStickersAdapter()
  {
    if (this.stickersGridAdapter != null)
      this.stickersGridAdapter.notifyDataSetChanged();
    if (this.trendingGridAdapter != null)
      this.trendingGridAdapter.notifyDataSetChanged();
    if (StickerPreviewViewer.getInstance().isVisible())
      StickerPreviewViewer.getInstance().close();
    StickerPreviewViewer.getInstance().reset();
  }

  private void saveEmojiColors()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = emojiColor.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0)
        localStringBuilder.append(",");
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append((String)localEntry.getValue());
    }
    localSharedPreferences.edit().putString("color", localStringBuilder.toString()).commit();
  }

  private void saveNewPage()
  {
    int i;
    if (this.pager.getCurrentItem() == 6)
      if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0))
        i = 2;
    while (true)
    {
      if (this.currentPage != i)
      {
        this.currentPage = i;
        getContext().getSharedPreferences("emoji", 0).edit().putInt("selected_page", i).commit();
      }
      return;
      i = 1;
      continue;
      i = 0;
    }
  }

  private void saveRecentEmoji()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      if (localStringBuilder.length() != 0)
        localStringBuilder.append(",");
      localStringBuilder.append((String)localEntry.getKey());
      localStringBuilder.append("=");
      localStringBuilder.append(localEntry.getValue());
    }
    localSharedPreferences.edit().putString("emojis2", localStringBuilder.toString()).commit();
  }

  private void showGifTab()
  {
    this.gifsGridView.setVisibility(0);
    this.stickersGridView.setVisibility(8);
    this.stickersEmptyView.setVisibility(8);
    this.trendingGridView.setVisibility(8);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int j = this.gifTabNum;
    if (this.recentTabBum > 0);
    for (int i = this.recentTabBum; ; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      saveNewPage();
      return;
    }
  }

  private void showTrendingTab()
  {
    this.trendingGridView.setVisibility(0);
    this.stickersGridView.setVisibility(8);
    this.stickersEmptyView.setVisibility(8);
    this.gifsGridView.setVisibility(8);
    ScrollSlidingTabStrip localScrollSlidingTabStrip = this.stickersTab;
    int j = this.trendingTabNum;
    if (this.recentTabBum > 0);
    for (int i = this.recentTabBum; ; i = this.stickersTabOffset)
    {
      localScrollSlidingTabStrip.onPageScrolled(j + 1, i + 1);
      saveNewPage();
      return;
    }
  }

  private void sortEmoji()
  {
    this.recentEmoji.clear();
    Iterator localIterator = this.emojiUseHistory.entrySet().iterator();
    while (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      this.recentEmoji.add(localEntry.getKey());
    }
    Collections.sort(this.recentEmoji, new Comparator()
    {
      public int compare(String paramString1, String paramString2)
      {
        Integer localInteger2 = (Integer)EmojiView.this.emojiUseHistory.get(paramString1);
        Integer localInteger1 = (Integer)EmojiView.this.emojiUseHistory.get(paramString2);
        paramString1 = localInteger2;
        if (localInteger2 == null)
          paramString1 = Integer.valueOf(0);
        paramString2 = localInteger1;
        if (localInteger1 == null)
          paramString2 = Integer.valueOf(0);
        if (paramString1.intValue() > paramString2.intValue())
          return -1;
        if (paramString1.intValue() < paramString2.intValue())
          return 1;
        return 0;
      }
    });
    while (this.recentEmoji.size() > 50)
      this.recentEmoji.remove(this.recentEmoji.size() - 1);
  }

  private void updateStickerTabs()
  {
    if (this.stickersTab == null)
      return;
    this.recentTabBum = -2;
    this.gifTabNum = -2;
    this.trendingTabNum = -2;
    this.stickersTabOffset = 0;
    int j = this.stickersTab.getCurrentPosition();
    this.stickersTab.removeTabs();
    Object localObject1 = getContext().getResources().getDrawable(2130837844);
    Theme.setDrawableColorByKey((Drawable)localObject1, "chat_emojiPanelIcon");
    this.stickersTab.addIconTab((Drawable)localObject1);
    if ((this.showGifs) && (!this.recentGifs.isEmpty()))
    {
      localObject1 = getContext().getResources().getDrawable(2130837847);
      Theme.setDrawableColorByKey((Drawable)localObject1, "chat_emojiPanelIcon");
      this.stickersTab.addIconTab((Drawable)localObject1);
      this.gifTabNum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
    }
    localObject1 = StickersQuery.getUnreadStickerSets();
    if ((this.trendingGridAdapter != null) && (this.trendingGridAdapter.getItemCount() != 0) && (!((ArrayList)localObject1).isEmpty()))
    {
      localObject2 = getContext().getResources().getDrawable(2130837849);
      Theme.setDrawableColorByKey((Drawable)localObject2, "chat_emojiPanelIcon");
      localObject2 = this.stickersTab.addIconTabWithCounter((Drawable)localObject2);
      this.trendingTabNum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
      ((TextView)localObject2).setText(String.format("%d", new Object[] { Integer.valueOf(((ArrayList)localObject1).size()) }));
    }
    if (!this.recentStickers.isEmpty())
    {
      this.recentTabBum = this.stickersTabOffset;
      this.stickersTabOffset += 1;
      localObject2 = getContext().getResources().getDrawable(2130837843);
      Theme.setDrawableColorByKey((Drawable)localObject2, "chat_emojiPanelIcon");
      this.stickersTab.addIconTab((Drawable)localObject2);
    }
    this.stickerSets.clear();
    Object localObject2 = StickersQuery.getStickerSets(0);
    int i = 0;
    if (i < ((ArrayList)localObject2).size())
    {
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)((ArrayList)localObject2).get(i);
      if ((localTL_messages_stickerSet.set.archived) || (localTL_messages_stickerSet.documents == null) || (localTL_messages_stickerSet.documents.isEmpty()));
      while (true)
      {
        i += 1;
        break;
        this.stickerSets.add(localTL_messages_stickerSet);
      }
    }
    i = 0;
    while (i < this.stickerSets.size())
    {
      this.stickersTab.addStickerTab((TLRPC.Document)((TLRPC.TL_messages_stickerSet)this.stickerSets.get(i)).documents.get(0));
      i += 1;
    }
    if ((this.trendingGridAdapter != null) && (this.trendingGridAdapter.getItemCount() != 0) && (((ArrayList)localObject1).isEmpty()))
    {
      localObject1 = getContext().getResources().getDrawable(2130837849);
      Theme.setDrawableColorByKey((Drawable)localObject1, "chat_emojiPanelIcon");
      this.trendingTabNum = (this.stickersTabOffset + this.stickerSets.size());
      this.stickersTab.addIconTab((Drawable)localObject1);
    }
    localObject1 = getContext().getResources().getDrawable(2130837848);
    Theme.setDrawableColorByKey((Drawable)localObject1, "chat_emojiPanelIcon");
    this.stickersTab.addIconTab((Drawable)localObject1);
    this.stickersTab.updateTabStyles();
    if (j != 0)
      this.stickersTab.onPageScrolled(j, j);
    if ((this.switchToGifTab) && (this.gifTabNum >= 0) && (this.gifsGridView.getVisibility() != 0))
    {
      showGifTab();
      this.switchToGifTab = false;
    }
    checkPanels();
  }

  private void updateVisibleTrendingSets()
  {
    if ((this.trendingGridAdapter == null) || (this.trendingGridAdapter == null))
      return;
    while (true)
    {
      int i;
      TLRPC.StickerSetCovered localStickerSetCovered;
      boolean bool3;
      boolean bool4;
      try
      {
        int j = this.trendingGridView.getChildCount();
        i = 0;
        if (i >= j)
          break;
        Object localObject = this.trendingGridView.getChildAt(i);
        if ((!(localObject instanceof FeaturedStickerSetInfoCell)) || ((RecyclerListView.Holder)this.trendingGridView.getChildViewHolder((View)localObject) == null))
          break label314;
        localObject = (FeaturedStickerSetInfoCell)localObject;
        ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
        localStickerSetCovered = ((FeaturedStickerSetInfoCell)localObject).getStickerSet();
        if ((localArrayList != null) && (localArrayList.contains(Long.valueOf(localStickerSetCovered.set.id))))
        {
          bool1 = true;
          ((FeaturedStickerSetInfoCell)localObject).setStickerSet(localStickerSetCovered, bool1);
          if (!bool1)
            continue;
          StickersQuery.markFaturedStickersByIdAsRead(localStickerSetCovered.set.id);
          bool3 = this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id));
          bool4 = this.removingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id));
          if (bool3)
            continue;
          bool1 = bool4;
          bool2 = bool3;
          if (!bool4)
            break label321;
          if ((!bool3) || (!((FeaturedStickerSetInfoCell)localObject).isInstalled()))
            break label254;
          this.installingStickerSets.remove(Long.valueOf(localStickerSetCovered.set.id));
          bool2 = false;
          bool1 = bool4;
          break label321;
          label232: ((FeaturedStickerSetInfoCell)localObject).setDrawProgress(bool1);
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
      boolean bool1 = false;
      continue;
      label254: bool1 = bool4;
      boolean bool2 = bool3;
      if (bool4)
      {
        bool1 = bool4;
        bool2 = bool3;
        if (!localException.isInstalled())
        {
          this.removingStickerSets.remove(Long.valueOf(localStickerSetCovered.set.id));
          bool1 = false;
          bool2 = bool3;
        }
      }
      label314: label321: 
      while ((!bool2) && (!bool1))
      {
        bool1 = false;
        break label232;
        i += 1;
        break;
      }
      bool1 = true;
    }
  }

  public void addRecentGif(TLRPC.Document paramDocument)
  {
    if (paramDocument == null);
    boolean bool;
    do
    {
      return;
      bool = this.recentGifs.isEmpty();
      this.recentGifs = StickersQuery.getRecentGifs();
      if (this.gifsAdapter == null)
        continue;
      this.gifsAdapter.notifyDataSetChanged();
    }
    while (!bool);
    updateStickerTabs();
  }

  public void addRecentSticker(TLRPC.Document paramDocument)
  {
    if (paramDocument == null);
    boolean bool;
    do
    {
      return;
      StickersQuery.addRecentSticker(0, paramDocument, (int)(System.currentTimeMillis() / 1000L));
      bool = this.recentStickers.isEmpty();
      this.recentStickers = StickersQuery.getRecentStickers(0);
      if (this.stickersGridAdapter == null)
        continue;
      this.stickersGridAdapter.notifyDataSetChanged();
    }
    while (!bool);
    updateStickerTabs();
  }

  public void clearRecentEmoji()
  {
    getContext().getSharedPreferences("emoji", 0).edit().putBoolean("filled_default", true).commit();
    this.emojiUseHistory.clear();
    this.recentEmoji.clear();
    saveRecentEmoji();
    ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.stickersDidLoaded)
      if (((Integer)paramArrayOfObject[0]).intValue() == 0)
      {
        if (this.trendingGridAdapter != null)
        {
          if (!this.trendingLoaded)
            break label52;
          updateVisibleTrendingSets();
        }
        updateStickerTabs();
        reloadStickersAdapter();
        checkPanels();
      }
    label52: label104: 
    do
    {
      boolean bool;
      do
      {
        return;
        this.trendingGridAdapter.notifyDataSetChanged();
        break;
        if (paramInt != NotificationCenter.recentDocumentsDidLoaded)
          break label104;
        bool = ((Boolean)paramArrayOfObject[0]).booleanValue();
      }
      while ((!bool) && (((Integer)paramArrayOfObject[1]).intValue() != 0));
      checkDocuments(bool);
      return;
    }
    while (paramInt != NotificationCenter.featuredStickersDidLoaded);
    if (this.trendingGridAdapter != null)
    {
      if (this.featuredStickersHash != StickersQuery.getFeaturesStickersHashWithoutUnread())
        this.trendingLoaded = false;
      if (!this.trendingLoaded)
        break label186;
      updateVisibleTrendingSets();
    }
    while (this.pagerSlidingTabStrip != null)
    {
      int j = this.pagerSlidingTabStrip.getChildCount();
      paramInt = i;
      while (paramInt < j)
      {
        this.pagerSlidingTabStrip.getChildAt(paramInt).invalidate();
        paramInt += 1;
      }
      label186: this.trendingGridAdapter.notifyDataSetChanged();
    }
    updateStickerTabs();
  }

  public int getCurrentPage()
  {
    return this.currentPage;
  }

  public void invalidateViews()
  {
    int i = 0;
    while (i < this.emojiGrids.size())
    {
      ((GridView)this.emojiGrids.get(i)).invalidateViews();
      i += 1;
    }
  }

  public void loadRecents()
  {
    SharedPreferences localSharedPreferences = getContext().getSharedPreferences("emoji", 0);
    while (true)
    {
      int i;
      int j;
      try
      {
        this.emojiUseHistory.clear();
        if (!localSharedPreferences.contains("emojis"))
          continue;
        localObject1 = localSharedPreferences.getString("emojis", "");
        if ((localObject1 == null) || (((String)localObject1).length() <= 0))
          continue;
        String[] arrayOfString1 = ((String)localObject1).split(",");
        int k = arrayOfString1.length;
        i = 0;
        if (i >= k)
          continue;
        String[] arrayOfString2 = arrayOfString1[i].split("=");
        long l = Utilities.parseLong(arrayOfString2[0]).longValue();
        localObject1 = "";
        j = 0;
        localObject2 = localObject1;
        if (j >= 4)
          continue;
        char c = (char)(int)l;
        localObject1 = String.valueOf(c) + (String)localObject1;
        l >>= 16;
        if (l != 0L)
          break label794;
        localObject2 = localObject1;
        if (((String)localObject2).length() <= 0)
          break label787;
        this.emojiUseHistory.put(localObject2, Utilities.parseInt(arrayOfString2[1]));
        break label787;
        localSharedPreferences.edit().remove("emojis").commit();
        saveRecentEmoji();
        if ((!this.emojiUseHistory.isEmpty()) || (localSharedPreferences.getBoolean("filled_default", false)))
          continue;
        localObject1 = new String[34];
        localObject1[0] = "😂";
        localObject1[1] = "😘";
        localObject1[2] = "❤";
        localObject1[3] = "😍";
        localObject1[4] = "😊";
        localObject1[5] = "😁";
        localObject1[6] = "👍";
        localObject1[7] = "☺";
        localObject1[8] = "😔";
        localObject1[9] = "😄";
        localObject1[10] = "😭";
        localObject1[11] = "💋";
        localObject1[12] = "😒";
        localObject1[13] = "😳";
        localObject1[14] = "😜";
        localObject1[15] = "🙈";
        localObject1[16] = "😉";
        localObject1[17] = "😃";
        localObject1[18] = "😢";
        localObject1[19] = "😝";
        localObject1[20] = "😱";
        localObject1[21] = "😡";
        localObject1[22] = "😏";
        localObject1[23] = "😞";
        localObject1[24] = "😅";
        localObject1[25] = "😚";
        localObject1[26] = "🙊";
        localObject1[27] = "😌";
        localObject1[28] = "😀";
        localObject1[29] = "😋";
        localObject1[30] = "😆";
        localObject1[31] = "👌";
        localObject1[32] = "😐";
        localObject1[33] = "😕";
        i = 0;
        if (i >= localObject1.length)
          continue;
        this.emojiUseHistory.put(localObject1[i], Integer.valueOf(localObject1.length - i));
        i += 1;
        continue;
        localObject1 = localSharedPreferences.getString("emojis2", "");
        if ((localObject1 == null) || (((String)localObject1).length() <= 0))
          continue;
        localObject1 = ((String)localObject1).split(",");
        j = localObject1.length;
        i = 0;
        if (i >= j)
          continue;
        localObject2 = localObject1[i].split("=");
        this.emojiUseHistory.put(localObject2[0], Utilities.parseInt(localObject2[1]));
        i += 1;
        continue;
        continue;
        localSharedPreferences.edit().putBoolean("filled_default", true).commit();
        saveRecentEmoji();
        sortEmoji();
        ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
      }
      catch (Exception localException2)
      {
        try
        {
          Object localObject2;
          Object localObject1 = localSharedPreferences.getString("color", "");
          if ((localObject1 != null) && (((String)localObject1).length() > 0))
          {
            localObject1 = ((String)localObject1).split(",");
            i = 0;
            if (i < localObject1.length)
            {
              localObject2 = localObject1[i].split("=");
              emojiColor.put(localObject2[0], localObject2[1]);
              i += 1;
              continue;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
            }
          }
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
      }
      return;
      label787: i += 1;
      continue;
      label794: j += 1;
    }
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          EmojiView.this.updateStickerTabs();
          EmojiView.this.reloadStickersAdapter();
        }
      });
    }
  }

  public void onDestroy()
  {
    if (this.stickersGridAdapter != null)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.stickersDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentDocumentsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    }
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if ((this.pickerViewPopup != null) && (this.pickerViewPopup.isShowing()))
      this.pickerViewPopup.dismiss();
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

  public void onMeasure(int paramInt1, int paramInt2)
  {
    Object localObject = null;
    this.isLayout = true;
    if (AndroidUtilities.isInMultiwindow)
      if (this.currentBackgroundType != 1)
      {
        if (Build.VERSION.SDK_INT >= 21)
        {
          setOutlineProvider((ViewOutlineProvider)this.outlineProvider);
          setClipToOutline(true);
          setElevation(AndroidUtilities.dp(2.0F));
        }
        setBackgroundResource(2130838068);
        getBackground().setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_emojiPanelBackground"), PorterDuff.Mode.MULTIPLY));
        this.emojiTab.setBackgroundDrawable(null);
        this.currentBackgroundType = 1;
      }
    while (true)
    {
      FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)this.emojiTab.getLayoutParams();
      localLayoutParams2.width = View.MeasureSpec.getSize(paramInt1);
      if (this.stickersTab != null)
      {
        FrameLayout.LayoutParams localLayoutParams1 = (FrameLayout.LayoutParams)this.stickersTab.getLayoutParams();
        localObject = localLayoutParams1;
        if (localLayoutParams1 != null)
        {
          localLayoutParams1.width = localLayoutParams2.width;
          localObject = localLayoutParams1;
        }
      }
      if (localLayoutParams2.width != this.oldWidth)
      {
        if ((this.stickersTab != null) && (localObject != null))
        {
          onPageScrolled(this.pager.getCurrentItem(), localLayoutParams2.width - getPaddingLeft() - getPaddingRight(), 0);
          this.stickersTab.setLayoutParams(localObject);
        }
        this.emojiTab.setLayoutParams(localLayoutParams2);
        this.oldWidth = localLayoutParams2.width;
      }
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(localLayoutParams2.width, 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt2), 1073741824));
      this.isLayout = false;
      return;
      if (this.currentBackgroundType == 0)
        continue;
      if (Build.VERSION.SDK_INT >= 21)
      {
        setOutlineProvider(null);
        setClipToOutline(false);
        setElevation(0.0F);
      }
      setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
      this.emojiTab.setBackgroundColor(Theme.getColor("chat_emojiPanelBackground"));
      this.currentBackgroundType = 0;
    }
  }

  public void onOpen(boolean paramBoolean)
  {
    boolean bool = true;
    if (this.stickersTab != null)
    {
      if ((this.currentPage != 0) && (!paramBoolean))
        break label55;
      if (this.pager.getCurrentItem() == 6)
      {
        ViewPager localViewPager = this.pager;
        if (paramBoolean)
          break label50;
        paramBoolean = bool;
        localViewPager.setCurrentItem(0, paramBoolean);
      }
    }
    label50: label55: 
    do
    {
      do
      {
        do
        {
          return;
          paramBoolean = false;
          break;
          if (this.currentPage != 1)
            break label151;
          if (this.pager.getCurrentItem() == 6)
            continue;
          this.pager.setCurrentItem(6);
        }
        while (this.stickersTab.getCurrentPosition() != this.gifTabNum + 1);
        if (this.recentTabBum >= 0)
        {
          this.stickersTab.selectTab(this.recentTabBum + 1);
          return;
        }
        if (this.gifTabNum >= 0)
        {
          this.stickersTab.selectTab(this.gifTabNum + 2);
          return;
        }
        this.stickersTab.selectTab(1);
        return;
      }
      while (this.currentPage != 2);
      if (this.pager.getCurrentItem() == 6)
        continue;
      this.pager.setCurrentItem(6);
    }
    while (this.stickersTab.getCurrentPosition() == this.gifTabNum + 1);
    label151: if ((this.gifTabNum >= 0) && (!this.recentGifs.isEmpty()))
    {
      this.stickersTab.selectTab(this.gifTabNum + 1);
      return;
    }
    this.switchToGifTab = true;
  }

  public void requestLayout()
  {
    if (this.isLayout)
      return;
    super.requestLayout();
  }

  public void setListener(Listener paramListener)
  {
    this.listener = paramListener;
  }

  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    Listener localListener;
    if (paramInt != 8)
    {
      sortEmoji();
      ((EmojiGridAdapter)this.adapters.get(0)).notifyDataSetChanged();
      if (this.stickersGridAdapter != null)
      {
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.stickersDidLoaded);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentDocumentsDidLoaded);
        updateStickerTabs();
        reloadStickersAdapter();
        if ((this.gifsGridView != null) && (this.gifsGridView.getVisibility() == 0) && (this.listener != null))
        {
          localListener = this.listener;
          if ((this.pager == null) || (this.pager.getCurrentItem() < 6))
            break label163;
        }
      }
    }
    label163: for (boolean bool = true; ; bool = false)
    {
      localListener.onGifTab(bool);
      if (this.trendingGridAdapter != null)
      {
        this.trendingLoaded = false;
        this.trendingGridAdapter.notifyDataSetChanged();
      }
      checkDocuments(true);
      checkDocuments(false);
      StickersQuery.loadRecents(0, true, true);
      StickersQuery.loadRecents(0, false, true);
      return;
    }
  }

  public void switchToGifRecent()
  {
    if ((this.gifTabNum >= 0) && (!this.recentGifs.isEmpty()))
      this.stickersTab.selectTab(this.gifTabNum + 1);
    while (true)
    {
      this.pager.setCurrentItem(6);
      return;
      this.switchToGifTab = true;
    }
  }

  private class EmojiColorPickerView extends View
  {
    private Drawable arrowDrawable = getResources().getDrawable(2130838073);
    private int arrowX;
    private Drawable backgroundDrawable = getResources().getDrawable(2130838072);
    private String currentEmoji;
    private RectF rect = new RectF();
    private Paint rectPaint = new Paint(1);
    private int selection;

    public EmojiColorPickerView(Context arg2)
    {
      super();
    }

    public String getEmoji()
    {
      return this.currentEmoji;
    }

    public int getSelection()
    {
      return this.selection;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      float f2 = 55.5F;
      Object localObject = this.backgroundDrawable;
      int i = getMeasuredWidth();
      float f1;
      int j;
      label73: int k;
      label100: String str;
      if (AndroidUtilities.isTablet())
      {
        f1 = 60.0F;
        ((Drawable)localObject).setBounds(0, 0, i, AndroidUtilities.dp(f1));
        this.backgroundDrawable.draw(paramCanvas);
        localObject = this.arrowDrawable;
        i = this.arrowX;
        j = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet())
          break label383;
        f1 = 55.5F;
        k = AndroidUtilities.dp(f1);
        int m = this.arrowX;
        int n = AndroidUtilities.dp(9.0F);
        if (!AndroidUtilities.isTablet())
          break label389;
        f1 = f2;
        ((Drawable)localObject).setBounds(i - j, k, m + n, AndroidUtilities.dp(f1 + 8.0F));
        this.arrowDrawable.draw(paramCanvas);
        if (this.currentEmoji == null)
          break label430;
        i = 0;
        label142: if (i >= 6)
          break label430;
        j = EmojiView.this.emojiSize * i + AndroidUtilities.dp(i * 4 + 5);
        k = AndroidUtilities.dp(9.0F);
        if (this.selection == i)
        {
          this.rect.set(j, k - (int)AndroidUtilities.dpf2(3.5F), EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k + AndroidUtilities.dp(3.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), this.rectPaint);
        }
        str = this.currentEmoji;
        if (i == 0)
          break label431;
        switch (i)
        {
        default:
          localObject = "";
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        }
      }
      label312: for (localObject = EmojiView.access$1700(str, (String)localObject); ; localObject = str)
      {
        localObject = Emoji.getEmojiBigDrawable((String)localObject);
        if (localObject != null)
        {
          ((Drawable)localObject).setBounds(j, k, EmojiView.this.emojiSize + j, EmojiView.this.emojiSize + k);
          ((Drawable)localObject).draw(paramCanvas);
        }
        i += 1;
        break label142;
        f1 = 52.0F;
        break;
        label383: f1 = 47.5F;
        break label73;
        f1 = 47.5F;
        break label100;
        localObject = "🏻";
        break label312;
        localObject = "🏼";
        break label312;
        localObject = "🏽";
        break label312;
        localObject = "🏾";
        break label312;
        localObject = "🏿";
        break label312;
        return;
      }
    }

    public void setEmoji(String paramString, int paramInt)
    {
      this.currentEmoji = paramString;
      this.arrowX = paramInt;
      this.rectPaint.setColor(788529152);
      invalidate();
    }

    public void setSelection(int paramInt)
    {
      if (this.selection == paramInt)
        return;
      this.selection = paramInt;
      invalidate();
    }
  }

  private class EmojiGridAdapter extends BaseAdapter
  {
    private int emojiPage;

    public EmojiGridAdapter(int arg2)
    {
      int i;
      this.emojiPage = i;
    }

    public int getCount()
    {
      if (this.emojiPage == -1)
        return EmojiView.this.recentEmoji.size();
      return EmojiData.dataColored[this.emojiPage].length;
    }

    public Object getItem(int paramInt)
    {
      return null;
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      paramView = (EmojiView.ImageViewEmoji)paramView;
      paramViewGroup = paramView;
      if (paramView == null)
        paramViewGroup = new EmojiView.ImageViewEmoji(EmojiView.this, EmojiView.this.getContext());
      Object localObject;
      if (this.emojiPage == -1)
      {
        paramView = (String)EmojiView.this.recentEmoji.get(paramInt);
        localObject = paramView;
      }
      while (true)
      {
        paramViewGroup.setImageDrawable(Emoji.getEmojiBigDrawable(paramView));
        paramViewGroup.setTag(localObject);
        return paramViewGroup;
        localObject = EmojiData.dataColored[this.emojiPage][paramInt];
        paramView = (String)EmojiView.emojiColor.get(localObject);
        if (paramView != null)
        {
          paramView = EmojiView.access$1700((String)localObject, paramView);
          continue;
        }
        paramView = (View)localObject;
      }
    }

    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null)
        super.unregisterDataSetObserver(paramDataSetObserver);
    }
  }

  private class EmojiPagesAdapter extends ab
    implements PagerSlidingTabStrip.IconTabProvider
  {
    private EmojiPagesAdapter()
    {
    }

    public void customOnDraw(Canvas paramCanvas, int paramInt)
    {
      if ((paramInt == 6) && (!StickersQuery.getUnreadStickerSets().isEmpty()) && (EmojiView.this.dotPaint != null))
      {
        paramInt = paramCanvas.getWidth() / 2;
        int i = AndroidUtilities.dp(9.0F);
        int j = paramCanvas.getHeight() / 2;
        int k = AndroidUtilities.dp(8.0F);
        paramCanvas.drawCircle(paramInt + i, j - k, AndroidUtilities.dp(5.0F), EmojiView.this.dotPaint);
      }
    }

    public void destroyItem(ViewGroup paramViewGroup, int paramInt, Object paramObject)
    {
      if (paramInt == 6);
      for (paramObject = EmojiView.this.stickersWrap; ; paramObject = (View)EmojiView.this.views.get(paramInt))
      {
        paramViewGroup.removeView(paramObject);
        return;
      }
    }

    public int getCount()
    {
      return EmojiView.this.views.size();
    }

    public Drawable getPageIconDrawable(int paramInt)
    {
      return EmojiView.this.icons[paramInt];
    }

    public Object instantiateItem(ViewGroup paramViewGroup, int paramInt)
    {
      if (paramInt == 6);
      for (Object localObject = EmojiView.this.stickersWrap; ; localObject = (View)EmojiView.this.views.get(paramInt))
      {
        paramViewGroup.addView((View)localObject);
        return localObject;
      }
    }

    public boolean isViewFromObject(View paramView, Object paramObject)
    {
      return paramView == paramObject;
    }

    public void unregisterDataSetObserver(DataSetObserver paramDataSetObserver)
    {
      if (paramDataSetObserver != null)
        super.unregisterDataSetObserver(paramDataSetObserver);
    }
  }

  private class EmojiPopupWindow extends PopupWindow
  {
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;

    public EmojiPopupWindow()
    {
      init();
    }

    public EmojiPopupWindow(int paramInt1, int arg3)
    {
      super(i);
      init();
    }

    public EmojiPopupWindow(Context arg2)
    {
      super();
      init();
    }

    public EmojiPopupWindow(View arg2)
    {
      super();
      init();
    }

    public EmojiPopupWindow(View paramInt1, int paramInt2, int arg4)
    {
      super(paramInt2, i);
      init();
    }

    public EmojiPopupWindow(View paramInt1, int paramInt2, int paramBoolean, boolean arg5)
    {
      super(paramInt2, paramBoolean, bool);
      init();
    }

    private void init()
    {
      if (EmojiView.superListenerField != null);
      try
      {
        this.mSuperScrollListener = ((ViewTreeObserver.OnScrollChangedListener)EmojiView.superListenerField.get(this));
        EmojiView.superListenerField.set(this, EmojiView.NOP);
        return;
      }
      catch (Exception localException)
      {
        this.mSuperScrollListener = null;
      }
    }

    private void registerListener(View paramView)
    {
      if (this.mSuperScrollListener != null)
        if (paramView.getWindowToken() == null)
          break label73;
      label73: for (paramView = paramView.getViewTreeObserver(); ; paramView = null)
      {
        if (paramView != this.mViewTreeObserver)
        {
          if ((this.mViewTreeObserver != null) && (this.mViewTreeObserver.isAlive()))
            this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
          this.mViewTreeObserver = paramView;
          if (paramView != null)
            paramView.addOnScrollChangedListener(this.mSuperScrollListener);
        }
        return;
      }
    }

    private void unregisterListener()
    {
      if ((this.mSuperScrollListener != null) && (this.mViewTreeObserver != null))
      {
        if (this.mViewTreeObserver.isAlive())
          this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
        this.mViewTreeObserver = null;
      }
    }

    public void dismiss()
    {
      setFocusable(false);
      try
      {
        super.dismiss();
        label9: unregisterListener();
        return;
      }
      catch (Exception localException)
      {
        break label9;
      }
    }

    public void showAsDropDown(View paramView, int paramInt1, int paramInt2)
    {
      try
      {
        super.showAsDropDown(paramView, paramInt1, paramInt2);
        registerListener(paramView);
        return;
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }

    public void showAtLocation(View paramView, int paramInt1, int paramInt2, int paramInt3)
    {
      super.showAtLocation(paramView, paramInt1, paramInt2, paramInt3);
      unregisterListener();
    }

    public void update(View paramView, int paramInt1, int paramInt2)
    {
      super.update(paramView, paramInt1, paramInt2);
      registerListener(paramView);
    }

    public void update(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.update(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
      registerListener(paramView);
    }
  }

  private class GifsAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public GifsAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      return EmojiView.this.recentGifs.size();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.Document localDocument = (TLRPC.Document)EmojiView.this.recentGifs.get(paramInt);
      if (localDocument != null)
        ((ContextLinkCell)paramViewHolder.itemView).setGif(localDocument, false);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new ContextLinkCell(this.mContext));
    }
  }

  private class ImageViewEmoji extends ImageView
  {
    private float lastX;
    private float lastY;
    private boolean touched;
    private float touchedX;
    private float touchedY;

    public ImageViewEmoji(Context arg2)
    {
      super();
      setOnClickListener(new View.OnClickListener(EmojiView.this)
      {
        public void onClick(View paramView)
        {
          EmojiView.ImageViewEmoji.this.sendEmoji(null);
        }
      });
      setOnLongClickListener(new View.OnLongClickListener(EmojiView.this)
      {
        public boolean onLongClick(View paramView)
        {
          String str = (String)paramView.getTag();
          if (EmojiData.emojiColoredMap.containsKey(str))
          {
            EmojiView.ImageViewEmoji.access$402(EmojiView.ImageViewEmoji.this, true);
            EmojiView.ImageViewEmoji.access$502(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastX);
            EmojiView.ImageViewEmoji.access$702(EmojiView.ImageViewEmoji.this, EmojiView.ImageViewEmoji.this.lastY);
            Object localObject = (String)EmojiView.emojiColor.get(str);
            int i;
            label118: int j;
            label152: label216: float f;
            if (localObject != null)
              switch (((String)localObject).hashCode())
              {
              default:
                i = -1;
                switch (i)
                {
                default:
                  paramView.getLocationOnScreen(EmojiView.this.location);
                  j = EmojiView.this.emojiSize;
                  int k = EmojiView.this.pickerView.getSelection();
                  int m = EmojiView.this.pickerView.getSelection();
                  if (AndroidUtilities.isTablet())
                  {
                    i = 5;
                    j = AndroidUtilities.dp(m * 4 - i) + k * j;
                    if (EmojiView.this.location[0] - j >= AndroidUtilities.dp(5.0F))
                      break label600;
                    i = j + (EmojiView.this.location[0] - j - AndroidUtilities.dp(5.0F));
                    label282: j = -i;
                    if (paramView.getTop() >= 0)
                      break label690;
                    i = paramView.getTop();
                    label298: localObject = EmojiView.this.pickerView;
                    if (!AndroidUtilities.isTablet())
                      break label695;
                    f = 30.0F;
                  }
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                }
              case 1773375:
              case 1773376:
              case 1773377:
              case 1773378:
              case 1773379:
              }
            while (true)
            {
              ((EmojiView.EmojiColorPickerView)localObject).setEmoji(str, AndroidUtilities.dp(f) - j + (int)AndroidUtilities.dpf2(0.5F));
              EmojiView.this.pickerViewPopup.setFocusable(true);
              EmojiView.this.pickerViewPopup.showAsDropDown(paramView, j, -paramView.getMeasuredHeight() - EmojiView.this.popupHeight + (paramView.getMeasuredHeight() - EmojiView.this.emojiSize) / 2 - i);
              paramView.getParent().requestDisallowInterceptTouchEvent(true);
              return true;
              if (!((String)localObject).equals("🏻"))
                break;
              i = 0;
              break label118;
              if (!((String)localObject).equals("🏼"))
                break;
              i = 1;
              break label118;
              if (!((String)localObject).equals("🏽"))
                break;
              i = 2;
              break label118;
              if (!((String)localObject).equals("🏾"))
                break;
              i = 3;
              break label118;
              if (!((String)localObject).equals("🏿"))
                break;
              i = 4;
              break label118;
              EmojiView.this.pickerView.setSelection(1);
              break label152;
              EmojiView.this.pickerView.setSelection(2);
              break label152;
              EmojiView.this.pickerView.setSelection(3);
              break label152;
              EmojiView.this.pickerView.setSelection(4);
              break label152;
              EmojiView.this.pickerView.setSelection(5);
              break label152;
              EmojiView.this.pickerView.setSelection(0);
              break label152;
              i = 1;
              break label216;
              label600: i = j;
              if (EmojiView.this.location[0] - j + EmojiView.this.popupWidth <= AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F))
                break label282;
              i = j + (EmojiView.this.location[0] - j + EmojiView.this.popupWidth - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0F)));
              break label282;
              label690: i = 0;
              break label298;
              label695: f = 22.0F;
            }
          }
          if (EmojiView.this.pager.getCurrentItem() == 0)
            EmojiView.this.listener.onClearEmojiRecent();
          return false;
        }
      });
      setBackgroundDrawable(Theme.getSelectorDrawable(false));
      setScaleType(ImageView.ScaleType.CENTER);
    }

    private void sendEmoji(String paramString)
    {
      Object localObject;
      String str;
      if (paramString != null)
      {
        localObject = paramString;
        if (paramString != null)
          break label280;
        paramString = (String)localObject;
        if (EmojiView.this.pager.getCurrentItem() != 0)
        {
          str = (String)EmojiView.emojiColor.get(localObject);
          paramString = (String)localObject;
          if (str != null)
            paramString = EmojiView.access$1700((String)localObject, str);
        }
        localObject = (Integer)EmojiView.this.emojiUseHistory.get(paramString);
        if (localObject != null)
          break label307;
        localObject = Integer.valueOf(0);
      }
      label273: label280: label307: 
      while (true)
      {
        int i;
        if ((((Integer)localObject).intValue() == 0) && (EmojiView.this.emojiUseHistory.size() > 50))
        {
          i = EmojiView.this.recentEmoji.size() - 1;
          label110: if (i >= 0)
          {
            str = (String)EmojiView.this.recentEmoji.get(i);
            EmojiView.this.emojiUseHistory.remove(str);
            EmojiView.this.recentEmoji.remove(i);
            if (EmojiView.this.emojiUseHistory.size() > 50)
              break label273;
          }
        }
        EmojiView.this.emojiUseHistory.put(paramString, Integer.valueOf(((Integer)localObject).intValue() + 1));
        if (EmojiView.this.pager.getCurrentItem() != 0)
          EmojiView.this.sortEmoji();
        EmojiView.this.saveRecentEmoji();
        ((EmojiView.EmojiGridAdapter)EmojiView.this.adapters.get(0)).notifyDataSetChanged();
        if (EmojiView.this.listener != null)
          EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
        do
        {
          return;
          localObject = (String)getTag();
          break;
          i -= 1;
          break label110;
        }
        while (EmojiView.this.listener == null);
        EmojiView.this.listener.onEmojiSelected(Emoji.fixEmoji(paramString));
        return;
      }
    }

    public void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), View.MeasureSpec.getSize(paramInt1));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      int j = 5;
      int i = 1;
      Object localObject1;
      Object localObject2;
      if (this.touched)
      {
        if ((paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3))
          break label313;
        if ((EmojiView.this.pickerViewPopup != null) && (EmojiView.this.pickerViewPopup.isShowing()))
          EmojiView.this.pickerViewPopup.dismiss();
        switch (EmojiView.this.pickerView.getSelection())
        {
        default:
          localObject1 = null;
          localObject2 = (String)getTag();
          if (EmojiView.this.pager.getCurrentItem() != 0)
          {
            if (localObject1 == null)
              break;
            EmojiView.emojiColor.put(localObject2, localObject1);
            localObject1 = EmojiView.access$1700((String)localObject2, (String)localObject1);
            label158: setImageDrawable(Emoji.getEmojiBigDrawable((String)localObject1));
            sendEmoji(null);
            EmojiView.this.saveEmojiColors();
            this.touched = false;
            this.touchedX = -10000.0F;
            this.touchedY = -10000.0F;
          }
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
        }
      }
      label525: 
      while (true)
      {
        this.lastX = paramMotionEvent.getX();
        this.lastY = paramMotionEvent.getY();
        return super.onTouchEvent(paramMotionEvent);
        localObject1 = "🏻";
        break;
        localObject1 = "🏼";
        break;
        localObject1 = "🏽";
        break;
        localObject1 = "🏾";
        break;
        localObject1 = "🏿";
        break;
        EmojiView.emojiColor.remove(localObject2);
        localObject1 = localObject2;
        break label158;
        localObject2 = new StringBuilder().append((String)localObject2);
        if (localObject1 != null);
        while (true)
        {
          sendEmoji((String)localObject1);
          break;
          localObject1 = "";
        }
        label313: if (paramMotionEvent.getAction() != 2)
          continue;
        if (this.touchedX != -10000.0F)
          if ((Math.abs(this.touchedX - paramMotionEvent.getX()) > AndroidUtilities.getPixelsInCM(0.2F, true)) || (Math.abs(this.touchedY - paramMotionEvent.getY()) > AndroidUtilities.getPixelsInCM(0.2F, false)))
          {
            this.touchedX = -10000.0F;
            this.touchedY = -10000.0F;
          }
        for (i = 0; ; i = 0)
        {
          if (i != 0)
            break label525;
          getLocationOnScreen(EmojiView.this.location);
          float f1 = EmojiView.this.location[0];
          float f2 = paramMotionEvent.getX();
          EmojiView.this.pickerView.getLocationOnScreen(EmojiView.this.location);
          int k = (int)((f1 + f2 - (EmojiView.this.location[0] + AndroidUtilities.dp(3.0F))) / (EmojiView.this.emojiSize + AndroidUtilities.dp(4.0F)));
          if (k < 0)
            i = 0;
          while (true)
          {
            EmojiView.this.pickerView.setSelection(i);
            break;
            i = j;
            if (k > 5)
              continue;
            i = k;
          }
        }
      }
    }
  }

  public static abstract interface Listener
  {
    public abstract boolean onBackspace();

    public abstract void onClearEmojiRecent();

    public abstract void onEmojiSelected(String paramString);

    public abstract void onGifSelected(TLRPC.Document paramDocument);

    public abstract void onGifTab(boolean paramBoolean);

    public abstract void onShowStickerSet(TLRPC.StickerSet paramStickerSet, TLRPC.InputStickerSet paramInputStickerSet);

    public abstract void onStickerSelected(TLRPC.Document paramDocument);

    public abstract void onStickerSetAdd(TLRPC.StickerSetCovered paramStickerSetCovered);

    public abstract void onStickerSetRemove(TLRPC.StickerSetCovered paramStickerSetCovered);

    public abstract void onStickersSettingsClick();

    public abstract void onStickersTab(boolean paramBoolean);
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
      paramTL_messages_stickerSet = (Integer)this.packStartRow.get(paramTL_messages_stickerSet);
      if (paramTL_messages_stickerSet == null)
        return -1;
      return paramTL_messages_stickerSet.intValue() * this.stickersPerRow;
    }

    public int getTabForPosition(int paramInt)
    {
      if (this.stickersPerRow == 0)
      {
        int j = EmojiView.this.getMeasuredWidth();
        int i = j;
        if (j == 0)
          i = AndroidUtilities.displaySize.x;
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      }
      paramInt /= this.stickersPerRow;
      TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.rowStartPack.get(Integer.valueOf(paramInt));
      if (localTL_messages_stickerSet == null)
        return EmojiView.this.recentTabBum;
      return EmojiView.this.stickerSets.indexOf(localTL_messages_stickerSet) + EmojiView.this.stickersTabOffset;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void notifyDataSetChanged()
    {
      int j = EmojiView.this.getMeasuredWidth();
      int i = j;
      if (j == 0)
        i = AndroidUtilities.displaySize.x;
      this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
      EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
      this.rowStartPack.clear();
      this.packStartRow.clear();
      this.cache.clear();
      this.totalItems = 0;
      ArrayList localArrayList2 = EmojiView.this.stickerSets;
      i = -1;
      if (i < localArrayList2.size())
      {
        TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = null;
        int k = this.totalItems / this.stickersPerRow;
        ArrayList localArrayList1;
        if (i == -1)
        {
          localArrayList1 = EmojiView.this.recentStickers;
          label119: if (!localArrayList1.isEmpty())
            break label169;
        }
        while (true)
        {
          i += 1;
          break;
          localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)localArrayList2.get(i);
          localArrayList1 = localTL_messages_stickerSet.documents;
          this.packStartRow.put(localTL_messages_stickerSet, Integer.valueOf(k));
          break label119;
          label169: int m = (int)Math.ceil(localArrayList1.size() / this.stickersPerRow);
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
        ((StickerEmojiCell)paramViewHolder.itemView).setRecent(EmojiView.this.recentStickers.contains(localObject));
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
        paramInt = EmojiView.this.pager.getHeight() - (int)Math.ceil(((TLRPC.TL_messages_stickerSet)localObject).documents.size() / this.stickersPerRow) * AndroidUtilities.dp(82.0F);
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

  private class TrendingGridAdapter extends RecyclerListView.SelectionAdapter
  {
    private HashMap<Integer, Object> cache = new HashMap();
    private Context context;
    private HashMap<Integer, TLRPC.StickerSetCovered> positionsToSets = new HashMap();
    private ArrayList<TLRPC.StickerSetCovered> sets = new ArrayList();
    private int stickersPerRow;
    private int totalItems;

    public TrendingGridAdapter(Context arg2)
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
      return this.totalItems;
    }

    public int getItemViewType(int paramInt)
    {
      Object localObject = this.cache.get(Integer.valueOf(paramInt));
      if (localObject != null)
      {
        if ((localObject instanceof TLRPC.Document))
          return 0;
        return 2;
      }
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void notifyDataSetChanged()
    {
      int j = EmojiView.this.getMeasuredWidth();
      int i = j;
      int k;
      if (j == 0)
      {
        if (!AndroidUtilities.isTablet())
          break label92;
        k = AndroidUtilities.displaySize.x;
        j = k * 35 / 100;
        i = j;
        if (j < AndroidUtilities.dp(320.0F))
          i = AndroidUtilities.dp(320.0F);
      }
      label92: for (i = k - i; ; i = AndroidUtilities.displaySize.x)
      {
        this.stickersPerRow = (i / AndroidUtilities.dp(72.0F));
        EmojiView.this.trendingLayoutManager.setSpanCount(this.stickersPerRow);
        if (!EmojiView.this.trendingLoaded)
          break;
        return;
      }
      this.cache.clear();
      this.positionsToSets.clear();
      this.sets.clear();
      this.totalItems = 0;
      ArrayList localArrayList = StickersQuery.getFeaturedStickerSets();
      i = 0;
      j = 0;
      if (i < localArrayList.size())
      {
        TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)localArrayList.get(i);
        k = j;
        if (!StickersQuery.isStickerPackInstalled(localStickerSetCovered.set.id))
          if ((!localStickerSetCovered.covers.isEmpty()) || (localStickerSetCovered.cover != null))
            break label203;
        for (k = j; ; k = j + 1)
        {
          i += 1;
          j = k;
          break;
          label203: this.sets.add(localStickerSetCovered);
          this.positionsToSets.put(Integer.valueOf(this.totalItems), localStickerSetCovered);
          HashMap localHashMap = this.cache;
          k = this.totalItems;
          this.totalItems = (k + 1);
          localHashMap.put(Integer.valueOf(k), Integer.valueOf(j));
          k = this.totalItems / this.stickersPerRow;
          int m;
          if (!localStickerSetCovered.covers.isEmpty())
          {
            m = (int)Math.ceil(localStickerSetCovered.covers.size() / this.stickersPerRow);
            k = 0;
            while (k < localStickerSetCovered.covers.size())
            {
              this.cache.put(Integer.valueOf(this.totalItems + k), localStickerSetCovered.covers.get(k));
              k += 1;
            }
          }
          for (k = m; ; k = 1)
          {
            m = 0;
            while (m < this.stickersPerRow * k)
            {
              this.positionsToSets.put(Integer.valueOf(this.totalItems + m), localStickerSetCovered);
              m += 1;
            }
            this.cache.put(Integer.valueOf(this.totalItems), localStickerSetCovered.cover);
          }
          this.totalItems += k * this.stickersPerRow;
        }
      }
      if (this.totalItems != 0)
      {
        EmojiView.access$6402(EmojiView.this, true);
        EmojiView.access$6502(EmojiView.this, StickersQuery.getFeaturesStickersHashWithoutUnread());
      }
      super.notifyDataSetChanged();
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool4 = false;
      switch (paramViewHolder.getItemViewType())
      {
      default:
        return;
      case 0:
        localObject = (TLRPC.Document)this.cache.get(Integer.valueOf(paramInt));
        ((StickerEmojiCell)paramViewHolder.itemView).setSticker((TLRPC.Document)localObject, false);
        return;
      case 1:
        ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(82.0F));
        return;
      case 2:
      }
      ArrayList localArrayList = StickersQuery.getUnreadStickerSets();
      Object localObject = (TLRPC.StickerSetCovered)this.sets.get(((Integer)this.cache.get(Integer.valueOf(paramInt))).intValue());
      boolean bool1;
      boolean bool2;
      if ((localArrayList != null) && (localArrayList.contains(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id))))
      {
        bool1 = true;
        paramViewHolder = (FeaturedStickerSetInfoCell)paramViewHolder.itemView;
        paramViewHolder.setStickerSet((TLRPC.StickerSetCovered)localObject, bool1);
        if (bool1)
          StickersQuery.markFaturedStickersByIdAsRead(((TLRPC.StickerSetCovered)localObject).set.id);
        bool1 = EmojiView.this.installingStickerSets.containsKey(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
        bool2 = EmojiView.this.removingStickerSets.containsKey(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
        if ((!bool1) && (!bool2))
          break label336;
        if ((!bool1) || (!paramViewHolder.isInstalled()))
          break label290;
        EmojiView.this.installingStickerSets.remove(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
        bool1 = bool2;
        bool2 = false;
      }
      while (true)
      {
        if (!bool2)
        {
          bool2 = bool4;
          if (!bool1);
        }
        else
        {
          bool2 = true;
        }
        paramViewHolder.setDrawProgress(bool2);
        return;
        bool1 = false;
        break;
        label290: if ((bool2) && (!paramViewHolder.isInstalled()))
        {
          EmojiView.this.removingStickerSets.remove(Long.valueOf(((TLRPC.StickerSetCovered)localObject).set.id));
          bool3 = false;
          bool2 = bool1;
          bool1 = bool3;
          continue;
        }
        label336: boolean bool3 = bool1;
        bool1 = bool2;
        bool2 = bool3;
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
        continue;
        paramViewGroup = new FeaturedStickerSetInfoCell(this.context, 17);
        ((FeaturedStickerSetInfoCell)paramViewGroup).setAddOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            paramView = (FeaturedStickerSetInfoCell)paramView.getParent();
            TLRPC.StickerSetCovered localStickerSetCovered = paramView.getStickerSet();
            if ((EmojiView.this.installingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id))) || (EmojiView.this.removingStickerSets.containsKey(Long.valueOf(localStickerSetCovered.set.id))))
              return;
            if (paramView.isInstalled())
            {
              EmojiView.this.removingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
              EmojiView.this.listener.onStickerSetRemove(paramView.getStickerSet());
            }
            while (true)
            {
              paramView.setDrawProgress(true);
              return;
              EmojiView.this.installingStickerSets.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
              EmojiView.this.listener.onStickerSetAdd(paramView.getStickerSet());
            }
          }
        });
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.EmojiView
 * JD-Core Version:    0.6.0
 */