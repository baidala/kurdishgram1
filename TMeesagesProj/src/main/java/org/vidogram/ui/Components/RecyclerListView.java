package org.vidogram.ui.Components;

import android.content.Context;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.AdapterDataObserver;
import org.vidogram.messenger.support.widget.RecyclerView.OnItemTouchListener;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.Theme;

public class RecyclerListView extends RecyclerView
{
  private static int[] attributes;
  private static boolean gotAttributes;
  private Runnable clickRunnable;
  private int currentChildPosition;
  private View currentChildView;
  private int currentFirst = -1;
  private int currentVisible = -1;
  private boolean disallowInterceptTouchEvents;
  private View emptyView;
  private FastScroll fastScroll;
  private GestureDetector gestureDetector;
  private ArrayList<View> headers;
  private ArrayList<View> headersCache;
  private boolean ignoreOnScroll;
  private boolean instantClick;
  private boolean interceptedByChild;
  private boolean isChildViewEnabled;
  private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver()
  {
    public void onChanged()
    {
      RecyclerListView.this.checkIfEmpty();
      RecyclerListView.this.selectorRect.setEmpty();
      RecyclerListView.this.invalidate();
    }

    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      RecyclerListView.this.checkIfEmpty();
    }

    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      RecyclerListView.this.checkIfEmpty();
    }
  };
  private OnInterceptTouchListener onInterceptTouchListener;
  private OnItemClickListener onItemClickListener;
  private OnItemLongClickListener onItemLongClickListener;
  private RecyclerView.OnScrollListener onScrollListener;
  private View pinnedHeader;
  private SectionsAdapter sectionsAdapter;
  private int sectionsCount;
  private int sectionsType;
  private Runnable selectChildRunnable;
  private Drawable selectorDrawable;
  private int selectorPosition;
  private Rect selectorRect = new Rect();
  private boolean selfOnLayout;
  private int startSection;
  private boolean wasPressed;

  public RecyclerListView(Context paramContext)
  {
    super(paramContext);
    setGlowColor(Theme.getColor("actionBarDefault"));
    this.selectorDrawable = Theme.getSelectorDrawable(false);
    this.selectorDrawable.setCallback(this);
    try
    {
      if (!gotAttributes)
      {
        attributes = getResourceDeclareStyleableIntArray("com.android.internal", "View");
        gotAttributes = true;
      }
      TypedArray localTypedArray = paramContext.getTheme().obtainStyledAttributes(attributes);
      View.class.getDeclaredMethod("initializeScrollbars", new Class[] { TypedArray.class }).invoke(this, new Object[] { localTypedArray });
      localTypedArray.recycle();
      super.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        boolean scrollingByUser;

        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
          boolean bool = false;
          Object localObject;
          if ((paramInt != 0) && (RecyclerListView.this.currentChildView != null))
          {
            if (RecyclerListView.this.selectChildRunnable != null)
            {
              AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
              RecyclerListView.access$702(RecyclerListView.this, null);
            }
            localObject = MotionEvent.obtain(0L, 0L, 3, 0.0F, 0.0F, 0);
          }
          try
          {
            RecyclerListView.this.gestureDetector.onTouchEvent((MotionEvent)localObject);
            RecyclerListView.this.currentChildView.onTouchEvent((MotionEvent)localObject);
            ((MotionEvent)localObject).recycle();
            localObject = RecyclerListView.this.currentChildView;
            RecyclerListView.this.currentChildView.setPressed(false);
            RecyclerListView.access$202(RecyclerListView.this, null);
            RecyclerListView.this.removeSelection((View)localObject, null);
            RecyclerListView.access$802(RecyclerListView.this, false);
            if (RecyclerListView.this.onScrollListener != null)
              RecyclerListView.this.onScrollListener.onScrollStateChanged(paramRecyclerView, paramInt);
            if ((paramInt == 1) || (paramInt == 2))
              bool = true;
            this.scrollingByUser = bool;
            return;
          }
          catch (Exception localException)
          {
            while (true)
              FileLog.e(localException);
          }
        }

        public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
        {
          if (RecyclerListView.this.onScrollListener != null)
            RecyclerListView.this.onScrollListener.onScrolled(paramRecyclerView, paramInt1, paramInt2);
          int j;
          if (RecyclerListView.this.selectorPosition != -1)
          {
            RecyclerListView.this.selectorRect.offset(0, -paramInt2);
            RecyclerListView.this.selectorDrawable.setBounds(RecyclerListView.this.selectorRect);
            RecyclerListView.this.invalidate();
            if (((this.scrollingByUser) && (RecyclerListView.this.fastScroll != null)) || ((RecyclerListView.this.sectionsType != 0) && (RecyclerListView.this.sectionsAdapter != null)))
            {
              paramRecyclerView = RecyclerListView.this.getLayoutManager();
              if ((paramRecyclerView instanceof LinearLayoutManager))
              {
                paramRecyclerView = (LinearLayoutManager)paramRecyclerView;
                if (paramRecyclerView.getOrientation() == 1)
                {
                  j = paramRecyclerView.findFirstVisibleItemPosition();
                  if (j != -1)
                    break label162;
                }
              }
            }
          }
          label162: label620: label760: 
          do
          {
            do
            {
              do
              {
                return;
                RecyclerListView.this.selectorRect.setEmpty();
                break;
                if ((!this.scrollingByUser) || (RecyclerListView.this.fastScroll == null))
                  continue;
                localObject = RecyclerListView.this.getAdapter();
                if (!(localObject instanceof RecyclerListView.FastScrollAdapter))
                  continue;
                RecyclerListView.FastScroll.access$2100(RecyclerListView.this.fastScroll, j / ((RecyclerView.Adapter)localObject).getItemCount());
              }
              while (RecyclerListView.this.sectionsAdapter == null);
              if (RecyclerListView.this.sectionsType != 1)
                break label760;
              paramInt2 = Math.abs(paramRecyclerView.findLastVisibleItemPosition() - j) + 1;
              RecyclerListView.this.headersCache.addAll(RecyclerListView.this.headers);
              RecyclerListView.this.headers.clear();
            }
            while (RecyclerListView.this.sectionsAdapter.getItemCount() == 0);
            int k;
            int i;
            if ((RecyclerListView.this.currentFirst != j) || (RecyclerListView.this.currentVisible != paramInt2))
            {
              RecyclerListView.access$2402(RecyclerListView.this, j);
              RecyclerListView.access$2502(RecyclerListView.this, paramInt2);
              RecyclerListView.access$2602(RecyclerListView.this, 1);
              RecyclerListView.access$2702(RecyclerListView.this, RecyclerListView.this.sectionsAdapter.getSectionForPosition(j));
              paramInt1 = RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection) + j - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(j);
              if (paramInt1 < j + paramInt2);
            }
            else
            {
              paramInt2 = RecyclerListView.this.startSection;
              paramInt1 = j;
              if (paramInt2 < RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount)
              {
                paramRecyclerView = null;
                if (!RecyclerListView.this.headersCache.isEmpty())
                {
                  paramRecyclerView = (View)RecyclerListView.this.headersCache.get(0);
                  RecyclerListView.this.headersCache.remove(0);
                }
                paramRecyclerView = RecyclerListView.this.getSectionHeaderView(paramInt2, paramRecyclerView);
                RecyclerListView.this.headers.add(paramRecyclerView);
                k = RecyclerListView.this.sectionsAdapter.getCountForSection(paramInt2);
                if (paramInt2 != RecyclerListView.this.startSection)
                  break label705;
                i = RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(paramInt1);
                if (i != k - 1)
                  break label620;
                paramRecyclerView.setTag(Integer.valueOf(-paramRecyclerView.getHeight()));
              }
            }
            while (true)
            {
              paramInt1 += k - RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(j);
              paramInt2 += 1;
              break label415;
              break;
              paramInt1 += RecyclerListView.this.sectionsAdapter.getCountForSection(RecyclerListView.this.startSection + RecyclerListView.this.sectionsCount);
              RecyclerListView.access$2608(RecyclerListView.this);
              break label396;
              if (i == k - 2)
              {
                localObject = RecyclerListView.this.getChildAt(paramInt1 - j);
                if (localObject != null);
                for (i = ((View)localObject).getTop(); ; i = -AndroidUtilities.dp(100.0F))
                {
                  if (i >= 0)
                    break label683;
                  paramRecyclerView.setTag(Integer.valueOf(i));
                  break;
                }
                paramRecyclerView.setTag(Integer.valueOf(0));
                continue;
              }
              paramRecyclerView.setTag(Integer.valueOf(0));
            }
            Object localObject = RecyclerListView.this.getChildAt(paramInt1 - j);
            if (localObject != null)
              paramRecyclerView.setTag(Integer.valueOf(((View)localObject).getTop()));
            while (true)
            {
              paramInt1 += k;
              break;
              paramRecyclerView.setTag(Integer.valueOf(-AndroidUtilities.dp(100.0F)));
            }
          }
          while ((RecyclerListView.this.sectionsType != 2) || (RecyclerListView.this.sectionsAdapter.getItemCount() == 0));
          label396: label415: label683: label705: paramInt1 = RecyclerListView.this.sectionsAdapter.getSectionForPosition(j);
          if ((RecyclerListView.this.currentFirst != paramInt1) || (RecyclerListView.this.pinnedHeader == null))
          {
            RecyclerListView.access$2902(RecyclerListView.this, RecyclerListView.this.getSectionHeaderView(paramInt1, RecyclerListView.this.pinnedHeader));
            RecyclerListView.access$2402(RecyclerListView.this, paramInt1);
          }
          paramInt1 = RecyclerListView.this.sectionsAdapter.getCountForSection(paramInt1);
          if (RecyclerListView.this.sectionsAdapter.getPositionInSectionForPosition(j) == paramInt1 - 1)
          {
            paramRecyclerView = RecyclerListView.this.getChildAt(0);
            paramInt1 = RecyclerListView.this.pinnedHeader.getHeight();
            if (paramRecyclerView != null)
            {
              paramInt2 = paramRecyclerView.getTop();
              paramInt2 = paramRecyclerView.getHeight() + paramInt2;
              if (paramInt2 >= paramInt1)
                break label996;
              paramInt1 = paramInt2 - paramInt1;
            }
          }
          while (true)
          {
            if (paramInt1 < 0)
              RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(paramInt1));
            while (true)
            {
              RecyclerListView.this.invalidate();
              return;
              paramInt1 = -AndroidUtilities.dp(100.0F);
              break;
              RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
              continue;
              RecyclerListView.this.pinnedHeader.setTag(Integer.valueOf(0));
            }
            label996: paramInt1 = 0;
          }
        }
      });
      addOnItemTouchListener(new RecyclerListViewItemClickListener(paramContext));
      return;
    }
    catch (Throwable localThrowable)
    {
      while (true)
        FileLog.e(localThrowable);
    }
  }

  private void checkIfEmpty()
  {
    int k = 0;
    if ((this.emptyView == null) || (getAdapter() == null))
      return;
    int i;
    View localView;
    if (getAdapter().getItemCount() == 0)
    {
      i = 1;
      localView = this.emptyView;
      if (i == 0)
        break label66;
    }
    label66: for (int j = 0; ; j = 8)
    {
      localView.setVisibility(j);
      j = k;
      if (i != 0)
        j = 4;
      setVisibility(j);
      return;
      i = 0;
      break;
    }
  }

  private void ensurePinnedHeaderLayout(View paramView, boolean paramBoolean)
  {
    int i;
    int j;
    if ((paramView.isLayoutRequested()) || (paramBoolean))
    {
      if (this.sectionsType != 1)
        break label82;
      ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
      i = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);
      j = View.MeasureSpec.makeMeasureSpec(localLayoutParams.width, 1073741824);
    }
    while (true)
    {
      try
      {
        paramView.measure(j, i);
        paramView.layout(0, 0, paramView.getMeasuredWidth(), paramView.getMeasuredHeight());
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      label82: if (this.sectionsType != 2)
        continue;
      i = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824);
      j = View.MeasureSpec.makeMeasureSpec(0, 0);
      try
      {
        paramView.measure(i, j);
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }

  private int[] getDrawableStateForSelector()
  {
    int[] arrayOfInt = onCreateDrawableState(1);
    arrayOfInt[(arrayOfInt.length - 1)] = 16842919;
    return arrayOfInt;
  }

  private View getSectionHeaderView(int paramInt, View paramView)
  {
    if (paramView == null);
    for (int i = 1; ; i = 0)
    {
      paramView = this.sectionsAdapter.getSectionHeaderView(paramInt, paramView);
      if (i != 0)
        ensurePinnedHeaderLayout(paramView, false);
      return paramView;
    }
  }

  private void positionSelector(int paramInt, View paramView)
  {
    positionSelector(paramInt, paramView, false, -1.0F, -1.0F);
  }

  private void positionSelector(int paramInt, View paramView, boolean paramBoolean, float paramFloat1, float paramFloat2)
  {
    if (this.selectorDrawable == null)
      return;
    if (paramInt != this.selectorPosition);
    for (int i = 1; ; i = 0)
    {
      if (paramInt != -1)
        this.selectorPosition = paramInt;
      this.selectorRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
      boolean bool = paramView.isEnabled();
      if (this.isChildViewEnabled != bool)
        this.isChildViewEnabled = bool;
      if (i != 0)
      {
        this.selectorDrawable.setVisible(false, false);
        this.selectorDrawable.setState(StateSet.NOTHING);
      }
      this.selectorDrawable.setBounds(this.selectorRect);
      if ((i != 0) && (getVisibility() == 0))
        this.selectorDrawable.setVisible(true, false);
      if ((Build.VERSION.SDK_INT < 21) || (!paramBoolean))
        break;
      this.selectorDrawable.setHotspot(paramFloat1, paramFloat2);
      return;
    }
  }

  private void removeSelection(View paramView, MotionEvent paramMotionEvent)
  {
    if (paramView == null)
      return;
    if ((paramView != null) && (paramView.isEnabled()))
    {
      positionSelector(this.currentChildPosition, paramView);
      if (this.selectorDrawable != null)
      {
        paramView = this.selectorDrawable.getCurrent();
        if ((paramView != null) && ((paramView instanceof TransitionDrawable)))
          ((TransitionDrawable)paramView).resetTransition();
        if ((paramMotionEvent != null) && (Build.VERSION.SDK_INT >= 21))
          this.selectorDrawable.setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
      }
    }
    while (true)
    {
      updateSelectorState();
      return;
      this.selectorRect.setEmpty();
    }
  }

  private void updateSelectorState()
  {
    if ((this.selectorDrawable != null) && (this.selectorDrawable.isStateful()))
    {
      if (this.currentChildView == null)
        break label47;
      if (this.selectorDrawable.setState(getDrawableStateForSelector()))
        invalidateDrawable(this.selectorDrawable);
    }
    return;
    label47: this.selectorDrawable.setState(StateSet.NOTHING);
  }

  public void cancelClickRunnables(boolean paramBoolean)
  {
    if (this.selectChildRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.selectChildRunnable);
      this.selectChildRunnable = null;
    }
    if (this.currentChildView != null)
    {
      View localView = this.currentChildView;
      if (paramBoolean)
        this.currentChildView.setPressed(false);
      this.currentChildView = null;
      removeSelection(localView, null);
    }
    if (this.clickRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.clickRunnable);
      this.clickRunnable = null;
    }
    this.interceptedByChild = false;
  }

  protected void dispatchDraw(Canvas paramCanvas)
  {
    float f = 0.0F;
    super.dispatchDraw(paramCanvas);
    if (this.sectionsType == 1)
      if ((this.sectionsAdapter != null) && (!this.headers.isEmpty()));
    do
      while (true)
      {
        return;
        int i = 0;
        if (i >= this.headers.size())
          break;
        View localView = (View)this.headers.get(i);
        int j = paramCanvas.save();
        int k = ((Integer)localView.getTag()).intValue();
        if (LocaleController.isRTL)
          f = getWidth() - localView.getWidth();
        while (true)
        {
          paramCanvas.translate(f, k);
          paramCanvas.clipRect(0, 0, getWidth(), localView.getMeasuredHeight());
          localView.draw(paramCanvas);
          paramCanvas.restoreToCount(j);
          i += 1;
          break;
          f = 0.0F;
        }
        if (this.sectionsType != 2)
          break;
        if ((this.sectionsAdapter == null) || (this.pinnedHeader == null))
          continue;
        i = paramCanvas.save();
        j = ((Integer)this.pinnedHeader.getTag()).intValue();
        if (LocaleController.isRTL)
          f = getWidth() - this.pinnedHeader.getWidth();
        paramCanvas.translate(f, j);
        paramCanvas.clipRect(0, 0, getWidth(), this.pinnedHeader.getMeasuredHeight());
        this.pinnedHeader.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
      }
    while (this.selectorRect.isEmpty());
    this.selectorDrawable.setBounds(this.selectorRect);
    this.selectorDrawable.draw(paramCanvas);
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    updateSelectorState();
  }

  public View getEmptyView()
  {
    return this.emptyView;
  }

  public ArrayList<View> getHeaders()
  {
    return this.headers;
  }

  public ArrayList<View> getHeadersCache()
  {
    return this.headersCache;
  }

  public View getPinnedHeader()
  {
    return this.pinnedHeader;
  }

  public int[] getResourceDeclareStyleableIntArray(String paramString1, String paramString2)
  {
    try
    {
      paramString1 = Class.forName(paramString1 + ".R$styleable").getField(paramString2);
      if (paramString1 != null)
      {
        paramString1 = (int[])(int[])paramString1.get(null);
        return paramString1;
      }
    }
    catch (Throwable paramString1)
    {
    }
    return null;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  public void invalidateViews()
  {
    int j = getChildCount();
    int i = 0;
    while (i < j)
    {
      getChildAt(i).invalidate();
      i += 1;
    }
  }

  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.selectorDrawable != null)
      this.selectorDrawable.jumpToCurrentState();
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.fastScroll != null) && (this.fastScroll.getParent() != getParent()))
    {
      ViewGroup localViewGroup = (ViewGroup)this.fastScroll.getParent();
      if (localViewGroup != null)
        localViewGroup.removeView(this.fastScroll);
      ((ViewGroup)getParent()).addView(this.fastScroll);
    }
  }

  public void onChildAttachedToWindow(View paramView)
  {
    if ((getAdapter() instanceof SelectionAdapter))
    {
      RecyclerView.ViewHolder localViewHolder = findContainingViewHolder(paramView);
      if (localViewHolder != null)
        paramView.setEnabled(((SelectionAdapter)getAdapter()).isEnabled(localViewHolder));
    }
    while (true)
    {
      super.onChildAttachedToWindow(paramView);
      return;
      paramView.setEnabled(false);
    }
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.selectorPosition = -1;
    this.selectorRect.setEmpty();
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.disallowInterceptTouchEvents)
      requestDisallowInterceptTouchEvent(true);
    return ((this.onInterceptTouchListener != null) && (this.onInterceptTouchListener.onInterceptTouchEvent(paramMotionEvent))) || (super.onInterceptTouchEvent(paramMotionEvent));
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.fastScroll != null)
    {
      this.selfOnLayout = true;
      if (!LocaleController.isRTL)
        break label60;
      this.fastScroll.layout(0, paramInt2, this.fastScroll.getMeasuredWidth(), this.fastScroll.getMeasuredHeight() + paramInt2);
    }
    while (true)
    {
      this.selfOnLayout = false;
      return;
      label60: paramInt1 = getMeasuredWidth() - this.fastScroll.getMeasuredWidth();
      this.fastScroll.layout(paramInt1, paramInt2, this.fastScroll.getMeasuredWidth() + paramInt1, this.fastScroll.getMeasuredHeight() + paramInt2);
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if (this.fastScroll != null)
      this.fastScroll.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(132.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.sectionsType == 1)
      if ((this.sectionsAdapter != null) && (!this.headers.isEmpty()));
    do
      while (true)
      {
        return;
        paramInt1 = 0;
        while (paramInt1 < this.headers.size())
        {
          ensurePinnedHeaderLayout((View)this.headers.get(paramInt1), true);
          paramInt1 += 1;
        }
      }
    while ((this.sectionsType != 2) || (this.sectionsAdapter == null) || (this.pinnedHeader == null));
    ensurePinnedHeaderLayout(this.pinnedHeader, true);
  }

  public void setAdapter(RecyclerView.Adapter paramAdapter)
  {
    RecyclerView.Adapter localAdapter = getAdapter();
    if (localAdapter != null)
      localAdapter.unregisterAdapterDataObserver(this.observer);
    if (this.headers != null)
    {
      this.headers.clear();
      this.headersCache.clear();
    }
    this.selectorPosition = -1;
    this.selectorRect.setEmpty();
    this.pinnedHeader = null;
    if ((paramAdapter instanceof SectionsAdapter));
    for (this.sectionsAdapter = ((SectionsAdapter)paramAdapter); ; this.sectionsAdapter = null)
    {
      super.setAdapter(paramAdapter);
      if (paramAdapter != null)
        paramAdapter.registerAdapterDataObserver(this.observer);
      checkIfEmpty();
      return;
    }
  }

  public void setDisallowInterceptTouchEvents(boolean paramBoolean)
  {
    this.disallowInterceptTouchEvents = paramBoolean;
  }

  public void setEmptyView(View paramView)
  {
    if (this.emptyView == paramView)
      return;
    this.emptyView = paramView;
    checkIfEmpty();
  }

  public void setFastScrollEnabled()
  {
    this.fastScroll = new FastScroll(getContext());
    if (getParent() != null)
      ((ViewGroup)getParent()).addView(this.fastScroll);
  }

  public void setFastScrollVisible(boolean paramBoolean)
  {
    if (this.fastScroll == null)
      return;
    FastScroll localFastScroll = this.fastScroll;
    if (paramBoolean);
    for (int i = 0; ; i = 8)
    {
      localFastScroll.setVisibility(i);
      return;
    }
  }

  public void setInstantClick(boolean paramBoolean)
  {
    this.instantClick = paramBoolean;
  }

  public void setListSelectorColor(int paramInt)
  {
    Theme.setSelectorDrawableColor(this.selectorDrawable, paramInt, true);
  }

  public void setOnInterceptTouchListener(OnInterceptTouchListener paramOnInterceptTouchListener)
  {
    this.onInterceptTouchListener = paramOnInterceptTouchListener;
  }

  public void setOnItemClickListener(OnItemClickListener paramOnItemClickListener)
  {
    this.onItemClickListener = paramOnItemClickListener;
  }

  public void setOnItemLongClickListener(OnItemLongClickListener paramOnItemLongClickListener)
  {
    this.onItemLongClickListener = paramOnItemLongClickListener;
  }

  public void setOnScrollListener(RecyclerView.OnScrollListener paramOnScrollListener)
  {
    this.onScrollListener = paramOnScrollListener;
  }

  public void setSectionsType(int paramInt)
  {
    this.sectionsType = paramInt;
    if (this.sectionsType == 1)
    {
      this.headers = new ArrayList();
      this.headersCache = new ArrayList();
    }
  }

  public void setVerticalScrollBarEnabled(boolean paramBoolean)
  {
    if (attributes != null)
      super.setVerticalScrollBarEnabled(paramBoolean);
  }

  public void stopScroll()
  {
    try
    {
      super.stopScroll();
      return;
    }
    catch (NullPointerException localNullPointerException)
    {
    }
  }

  public void updateFastScrollColors()
  {
    if (this.fastScroll != null)
      this.fastScroll.updateColors();
  }

  public boolean verifyDrawable(Drawable paramDrawable)
  {
    return (this.selectorDrawable == paramDrawable) || (super.verifyDrawable(paramDrawable));
  }

  private class FastScroll extends View
  {
    private float bubbleProgress;
    private int[] colors = new int[6];
    private String currentLetter;
    private long lastUpdateTime;
    private float lastY;
    private StaticLayout letterLayout;
    private TextPaint letterPaint = new TextPaint(1);
    private StaticLayout oldLetterLayout;
    private Paint paint = new Paint(1);
    private Path path = new Path();
    private boolean pressed;
    private float progress;
    private float[] radii = new float[8];
    private RectF rect = new RectF();
    private int scrollX;
    private float startDy;
    private float textX;
    private float textY;

    public FastScroll(Context arg2)
    {
      super();
      this.letterPaint.setTextSize(AndroidUtilities.dp(45.0F));
      int i = 0;
      while (i < 8)
      {
        this.radii[i] = AndroidUtilities.dp(44.0F);
        i += 1;
      }
      if (LocaleController.isRTL);
      for (i = AndroidUtilities.dp(10.0F); ; i = AndroidUtilities.dp(117.0F))
      {
        this.scrollX = i;
        updateColors();
        return;
      }
    }

    private void getCurrentLetter()
    {
      Object localObject1 = RecyclerListView.this.getLayoutManager();
      if ((localObject1 instanceof LinearLayoutManager))
      {
        localObject1 = (LinearLayoutManager)localObject1;
        if (((LinearLayoutManager)localObject1).getOrientation() == 1)
        {
          Object localObject2 = RecyclerListView.this.getAdapter();
          if ((localObject2 instanceof RecyclerListView.FastScrollAdapter))
          {
            localObject2 = (RecyclerListView.FastScrollAdapter)localObject2;
            int i = ((RecyclerListView.FastScrollAdapter)localObject2).getPositionForScrollProgress(this.progress);
            ((LinearLayoutManager)localObject1).scrollToPositionWithOffset(i, 0);
            localObject1 = ((RecyclerListView.FastScrollAdapter)localObject2).getLetter(i);
            if (localObject1 != null)
              break label94;
            if (this.letterLayout != null)
              this.oldLetterLayout = this.letterLayout;
            this.letterLayout = null;
          }
        }
      }
      label94: 
      do
      {
        do
          return;
        while (((String)localObject1).equals(this.currentLetter));
        this.letterLayout = new StaticLayout((CharSequence)localObject1, this.letterPaint, 1000, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.oldLetterLayout = null;
      }
      while (this.letterLayout.getLineCount() <= 0);
      if (LocaleController.isRTL)
      {
        this.letterLayout.getLineWidth(0);
        this.letterLayout.getLineLeft(0);
      }
      for (this.textX = (AndroidUtilities.dp(10.0F) + (AndroidUtilities.dp(88.0F) - (this.letterLayout.getLineWidth(0) - this.letterLayout.getLineLeft(0))) / 2.0F); ; this.textX = ((AndroidUtilities.dp(88.0F) - (this.letterLayout.getLineWidth(0) - this.letterLayout.getLineLeft(0))) / 2.0F))
      {
        this.textY = ((AndroidUtilities.dp(88.0F) - this.letterLayout.getHeight()) / 2);
        return;
      }
    }

    private void setProgress(float paramFloat)
    {
      this.progress = paramFloat;
      invalidate();
    }

    private void updateColors()
    {
      int i = Theme.getColor("fastScrollInactive");
      int j = Theme.getColor("fastScrollActive");
      this.paint.setColor(i);
      this.letterPaint.setColor(Theme.getColor("fastScrollText"));
      this.colors[0] = Color.red(i);
      this.colors[1] = Color.red(j);
      this.colors[2] = Color.green(i);
      this.colors[3] = Color.green(j);
      this.colors[4] = Color.blue(i);
      this.colors[5] = Color.blue(j);
      invalidate();
    }

    public void layout(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (!RecyclerListView.this.selfOnLayout)
        return;
      super.layout(paramInt1, paramInt2, paramInt3, paramInt4);
    }

    protected void onDraw(Canvas paramCanvas)
    {
      this.paint.setColor(Color.argb(255, this.colors[0] + (int)((this.colors[1] - this.colors[0]) * this.bubbleProgress), this.colors[2] + (int)((this.colors[3] - this.colors[2]) * this.bubbleProgress), this.colors[4] + (int)((this.colors[5] - this.colors[4]) * this.bubbleProgress)));
      int j = (int)Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0F)) * this.progress);
      this.rect.set(this.scrollX, AndroidUtilities.dp(12.0F) + j, this.scrollX + AndroidUtilities.dp(5.0F), AndroidUtilities.dp(42.0F) + j);
      paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), this.paint);
      int k;
      int i;
      float f1;
      if ((this.pressed) || (this.bubbleProgress != 0.0F))
      {
        this.paint.setAlpha((int)(255.0F * this.bubbleProgress));
        k = AndroidUtilities.dp(30.0F);
        i = j - AndroidUtilities.dp(46.0F);
        if (i > AndroidUtilities.dp(12.0F))
          break label870;
        f1 = AndroidUtilities.dp(12.0F) - i;
        i = AndroidUtilities.dp(12.0F);
      }
      while (true)
      {
        paramCanvas.translate(AndroidUtilities.dp(10.0F), i);
        float f2;
        float f3;
        Object localObject;
        label418: label444: label454: label510: long l1;
        if (f1 <= AndroidUtilities.dp(29.0F))
        {
          f2 = AndroidUtilities.dp(44.0F);
          f3 = AndroidUtilities.dp(4.0F);
          f1 = f1 / AndroidUtilities.dp(29.0F) * AndroidUtilities.dp(40.0F) + f3;
          if (((LocaleController.isRTL) && ((this.radii[0] != f2) || (this.radii[6] != f1))) || ((!LocaleController.isRTL) && ((this.radii[2] != f2) || (this.radii[4] != f1))))
          {
            if (!LocaleController.isRTL)
              break label780;
            localObject = this.radii;
            this.radii[1] = f2;
            localObject[0] = f2;
            localObject = this.radii;
            this.radii[7] = f1;
            localObject[6] = f1;
            this.path.reset();
            localObject = this.rect;
            if (!LocaleController.isRTL)
              break label819;
            f1 = AndroidUtilities.dp(10.0F);
            if (!LocaleController.isRTL)
              break label824;
            f2 = 98.0F;
            ((RectF)localObject).set(f1, 0.0F, AndroidUtilities.dp(f2), AndroidUtilities.dp(88.0F));
            this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
            this.path.close();
          }
          if (this.letterLayout == null)
            break label830;
          localObject = this.letterLayout;
          if (localObject != null)
          {
            paramCanvas.save();
            paramCanvas.scale(this.bubbleProgress, this.bubbleProgress, this.scrollX, j + k - i);
            paramCanvas.drawPath(this.path, this.paint);
            paramCanvas.translate(this.textX, this.textY);
            ((StaticLayout)localObject).draw(paramCanvas);
            paramCanvas.restore();
          }
          if (((this.pressed) && (this.letterLayout != null) && (this.bubbleProgress < 1.0F)) || (((!this.pressed) || (this.letterLayout == null)) && (this.bubbleProgress > 0.0F)))
          {
            long l3 = System.currentTimeMillis();
            long l2 = l3 - this.lastUpdateTime;
            if (l2 >= 0L)
            {
              l1 = l2;
              if (l2 <= 17L);
            }
            else
            {
              l1 = 17L;
            }
            this.lastUpdateTime = l3;
            invalidate();
            if ((!this.pressed) || (this.letterLayout == null))
              break label839;
            this.bubbleProgress += (float)l1 / 120.0F;
            if (this.bubbleProgress > 1.0F)
              this.bubbleProgress = 1.0F;
          }
        }
        label780: label819: label824: label830: label839: 
        do
        {
          return;
          f3 = AndroidUtilities.dp(29.0F);
          f2 = AndroidUtilities.dp(44.0F);
          float f4 = AndroidUtilities.dp(4.0F);
          f3 = (1.0F - (f1 - f3) / AndroidUtilities.dp(29.0F)) * AndroidUtilities.dp(40.0F) + f4;
          f1 = f2;
          f2 = f3;
          break;
          localObject = this.radii;
          this.radii[3] = f2;
          localObject[2] = f2;
          localObject = this.radii;
          this.radii[5] = f1;
          localObject[4] = f1;
          break label418;
          f1 = 0.0F;
          break label444;
          f2 = 88.0F;
          break label454;
          localObject = this.oldLetterLayout;
          break label510;
          this.bubbleProgress -= (float)l1 / 120.0F;
        }
        while (this.bubbleProgress >= 0.0F);
        this.bubbleProgress = 0.0F;
        return;
        label870: f1 = 0.0F;
      }
    }

    protected void onMeasure(int paramInt1, int paramInt2)
    {
      setMeasuredDimension(AndroidUtilities.dp(132.0F), View.MeasureSpec.getSize(paramInt2));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      float f1;
      float f2;
      float f3;
      switch (paramMotionEvent.getAction())
      {
      default:
        return super.onTouchEvent(paramMotionEvent);
      case 0:
        f1 = paramMotionEvent.getX();
        this.lastY = paramMotionEvent.getY();
        f2 = (float)Math.ceil((getMeasuredHeight() - AndroidUtilities.dp(54.0F)) * this.progress) + AndroidUtilities.dp(12.0F);
        if (((LocaleController.isRTL) && (f1 > AndroidUtilities.dp(25.0F))) || ((!LocaleController.isRTL) && (f1 < AndroidUtilities.dp(107.0F))) || (this.lastY < f2) || (this.lastY > AndroidUtilities.dp(30.0F) + f2))
          return false;
        this.startDy = (this.lastY - f2);
        this.pressed = true;
        this.lastUpdateTime = System.currentTimeMillis();
        getCurrentLetter();
        invalidate();
        return true;
      case 2:
        if (!this.pressed)
          return true;
        f1 = paramMotionEvent.getY();
        f2 = AndroidUtilities.dp(12.0F) + this.startDy;
        f3 = getMeasuredHeight() - AndroidUtilities.dp(42.0F) + this.startDy;
        if (f1 >= f2)
          break;
        f1 = f2;
      case 1:
      case 3:
      }
      label337: 
      while (true)
      {
        f2 = this.lastY;
        this.lastY = f1;
        this.progress += (f1 - f2) / (getMeasuredHeight() - AndroidUtilities.dp(54.0F));
        if (this.progress < 0.0F)
          this.progress = 0.0F;
        while (true)
        {
          getCurrentLetter();
          invalidate();
          return true;
          if (f1 <= f3)
            break label337;
          f1 = f3;
          break;
          if (this.progress <= 1.0F)
            continue;
          this.progress = 1.0F;
        }
        this.pressed = false;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
        return true;
      }
    }
  }

  public static abstract class FastScrollAdapter extends RecyclerListView.SelectionAdapter
  {
    public abstract String getLetter(int paramInt);

    public abstract int getPositionForScrollProgress(float paramFloat);
  }

  public static class Holder extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }

  public static abstract interface OnInterceptTouchListener
  {
    public abstract boolean onInterceptTouchEvent(MotionEvent paramMotionEvent);
  }

  public static abstract interface OnItemClickListener
  {
    public abstract void onItemClick(View paramView, int paramInt);
  }

  public static abstract interface OnItemLongClickListener
  {
    public abstract boolean onItemClick(View paramView, int paramInt);
  }

  private class RecyclerListViewItemClickListener
    implements RecyclerView.OnItemTouchListener
  {
    public RecyclerListViewItemClickListener(Context arg2)
    {
      Context localContext;
      RecyclerListView.access$102(RecyclerListView.this, new GestureDetector(localContext, new GestureDetector.SimpleOnGestureListener(RecyclerListView.this)
      {
        public void onLongPress(MotionEvent paramMotionEvent)
        {
          if (RecyclerListView.this.currentChildView != null)
          {
            paramMotionEvent = RecyclerListView.this.currentChildView;
            if ((RecyclerListView.this.onItemLongClickListener != null) && (RecyclerListView.this.currentChildPosition != -1) && (RecyclerListView.this.onItemLongClickListener.onItemClick(RecyclerListView.this.currentChildView, RecyclerListView.this.currentChildPosition)))
              paramMotionEvent.performHapticFeedback(0);
          }
        }

        public boolean onSingleTapUp(MotionEvent paramMotionEvent)
        {
          if ((RecyclerListView.this.currentChildView != null) && (RecyclerListView.this.onItemClickListener != null))
          {
            RecyclerListView.this.currentChildView.setPressed(true);
            View localView = RecyclerListView.this.currentChildView;
            int i = RecyclerListView.this.currentChildPosition;
            if ((RecyclerListView.this.instantClick) && (i != -1))
            {
              localView.playSoundEffect(0);
              RecyclerListView.this.onItemClickListener.onItemClick(localView, i);
            }
            AndroidUtilities.runOnUIThread(RecyclerListView.access$602(RecyclerListView.this, new Runnable(localView, i)
            {
              public void run()
              {
                if (this == RecyclerListView.this.clickRunnable)
                  RecyclerListView.access$602(RecyclerListView.this, null);
                if (this.val$view != null)
                {
                  this.val$view.setPressed(false);
                  if (!RecyclerListView.this.instantClick)
                  {
                    this.val$view.playSoundEffect(0);
                    if ((RecyclerListView.this.onItemClickListener != null) && (this.val$position != -1))
                      RecyclerListView.this.onItemClickListener.onItemClick(this.val$view, this.val$position);
                  }
                }
              }
            }), ViewConfiguration.getPressedStateDuration());
            if (RecyclerListView.this.selectChildRunnable != null)
            {
              localView = RecyclerListView.this.currentChildView;
              AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
              RecyclerListView.access$702(RecyclerListView.this, null);
              RecyclerListView.access$202(RecyclerListView.this, null);
              RecyclerListView.access$802(RecyclerListView.this, false);
              RecyclerListView.this.removeSelection(localView, paramMotionEvent);
            }
          }
          return true;
        }
      }));
    }

    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      int k = paramMotionEvent.getActionMasked();
      int i;
      if (RecyclerListView.this.getScrollState() == 0)
        i = 1;
      while (true)
      {
        int j;
        if (((k == 0) || (k == 5)) && (RecyclerListView.this.currentChildView == null) && (i != 0))
        {
          RecyclerListView.access$202(RecyclerListView.this, paramRecyclerView.findChildViewUnder(paramMotionEvent.getX(), paramMotionEvent.getY()));
          if ((RecyclerListView.this.currentChildView instanceof ViewGroup))
          {
            float f1 = paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft();
            float f2 = paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop();
            ViewGroup localViewGroup = (ViewGroup)RecyclerListView.this.currentChildView;
            j = localViewGroup.getChildCount() - 1;
            if (j >= 0)
            {
              View localView = localViewGroup.getChildAt(j);
              if ((f1 < localView.getLeft()) || (f1 > localView.getRight()) || (f2 < localView.getTop()) || (f2 > localView.getBottom()) || (!localView.isClickable()))
                break label543;
              RecyclerListView.access$202(RecyclerListView.this, null);
            }
          }
          RecyclerListView.access$402(RecyclerListView.this, -1);
          if (RecyclerListView.this.currentChildView != null)
          {
            RecyclerListView.access$402(RecyclerListView.this, paramRecyclerView.getChildPosition(RecyclerListView.this.currentChildView));
            paramRecyclerView = MotionEvent.obtain(0L, 0L, paramMotionEvent.getActionMasked(), paramMotionEvent.getX() - RecyclerListView.this.currentChildView.getLeft(), paramMotionEvent.getY() - RecyclerListView.this.currentChildView.getTop(), 0);
            if (RecyclerListView.this.currentChildView.onTouchEvent(paramRecyclerView))
              RecyclerListView.access$802(RecyclerListView.this, true);
            paramRecyclerView.recycle();
          }
        }
        if ((RecyclerListView.this.currentChildView != null) && (!RecyclerListView.this.interceptedByChild) && (paramMotionEvent != null));
        try
        {
          RecyclerListView.this.gestureDetector.onTouchEvent(paramMotionEvent);
          if ((k == 0) || (k == 5))
            if ((!RecyclerListView.this.interceptedByChild) && (RecyclerListView.this.currentChildView != null))
            {
              RecyclerListView.access$702(RecyclerListView.this, new Runnable()
              {
                public void run()
                {
                  if ((RecyclerListView.this.selectChildRunnable != null) && (RecyclerListView.this.currentChildView != null))
                  {
                    RecyclerListView.this.currentChildView.setPressed(true);
                    RecyclerListView.access$702(RecyclerListView.this, null);
                  }
                }
              });
              AndroidUtilities.runOnUIThread(RecyclerListView.this.selectChildRunnable, ViewConfiguration.getTapTimeout());
              if (!RecyclerListView.this.currentChildView.isEnabled())
                break label570;
              RecyclerListView.this.positionSelector(RecyclerListView.this.currentChildPosition, RecyclerListView.this.currentChildView);
              if (RecyclerListView.this.selectorDrawable != null)
              {
                paramRecyclerView = RecyclerListView.this.selectorDrawable.getCurrent();
                if ((paramRecyclerView != null) && ((paramRecyclerView instanceof TransitionDrawable)))
                {
                  if (RecyclerListView.this.onItemLongClickListener == null)
                    break label560;
                  ((TransitionDrawable)paramRecyclerView).startTransition(ViewConfiguration.getLongPressTimeout());
                }
                if (Build.VERSION.SDK_INT >= 21)
                  RecyclerListView.this.selectorDrawable.setHotspot(paramMotionEvent.getX(), paramMotionEvent.getY());
              }
              RecyclerListView.this.updateSelectorState();
            }
            else
            {
              return false;
              i = 0;
              continue;
              label543: j -= 1;
            }
        }
        catch (Exception paramRecyclerView)
        {
          while (true)
          {
            FileLog.e(paramRecyclerView);
            continue;
            label560: ((TransitionDrawable)paramRecyclerView).resetTransition();
            continue;
            label570: RecyclerListView.this.selectorRect.setEmpty();
            continue;
            if (((k != 1) && (k != 6) && (k != 3) && (i != 0)) || (RecyclerListView.this.currentChildView == null))
              continue;
            if (RecyclerListView.this.selectChildRunnable != null)
            {
              AndroidUtilities.cancelRunOnUIThread(RecyclerListView.this.selectChildRunnable);
              RecyclerListView.access$702(RecyclerListView.this, null);
            }
            paramRecyclerView = RecyclerListView.this.currentChildView;
            RecyclerListView.this.currentChildView.setPressed(false);
            RecyclerListView.access$202(RecyclerListView.this, null);
            RecyclerListView.access$802(RecyclerListView.this, false);
            RecyclerListView.this.removeSelection(paramRecyclerView, paramMotionEvent);
          }
        }
      }
    }

    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      RecyclerListView.this.cancelClickRunnables(true);
    }

    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
    }
  }

  public static abstract class SectionsAdapter extends RecyclerListView.FastScrollAdapter
  {
    private int count;
    private SparseArray<Integer> sectionCache;
    private int sectionCount;
    private SparseArray<Integer> sectionCountCache;
    private SparseArray<Integer> sectionPositionCache;

    public SectionsAdapter()
    {
      cleanupCache();
    }

    private void cleanupCache()
    {
      this.sectionCache = new SparseArray();
      this.sectionPositionCache = new SparseArray();
      this.sectionCountCache = new SparseArray();
      this.count = -1;
      this.sectionCount = -1;
    }

    private int internalGetCountForSection(int paramInt)
    {
      Integer localInteger = (Integer)this.sectionCountCache.get(paramInt);
      if (localInteger != null)
        return localInteger.intValue();
      int i = getCountForSection(paramInt);
      this.sectionCountCache.put(paramInt, Integer.valueOf(i));
      return i;
    }

    private int internalGetSectionCount()
    {
      if (this.sectionCount >= 0)
        return this.sectionCount;
      this.sectionCount = getSectionCount();
      return this.sectionCount;
    }

    public abstract int getCountForSection(int paramInt);

    public final Object getItem(int paramInt)
    {
      return getItem(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
    }

    public abstract Object getItem(int paramInt1, int paramInt2);

    public final int getItemCount()
    {
      int i = 0;
      if (this.count >= 0)
        return this.count;
      this.count = 0;
      while (i < internalGetSectionCount())
      {
        this.count += internalGetCountForSection(i);
        i += 1;
      }
      return this.count;
    }

    public final int getItemViewType(int paramInt)
    {
      return getItemViewType(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt));
    }

    public abstract int getItemViewType(int paramInt1, int paramInt2);

    public int getPositionInSectionForPosition(int paramInt)
    {
      int j = 0;
      Integer localInteger = (Integer)this.sectionPositionCache.get(paramInt);
      if (localInteger != null)
        return localInteger.intValue();
      int i = 0;
      while (i < internalGetSectionCount())
      {
        int k = internalGetCountForSection(i) + j;
        if ((paramInt >= j) && (paramInt < k))
        {
          i = paramInt - j;
          this.sectionPositionCache.put(paramInt, Integer.valueOf(i));
          return i;
        }
        i += 1;
        j = k;
      }
      return -1;
    }

    public abstract int getSectionCount();

    public final int getSectionForPosition(int paramInt)
    {
      int j = 0;
      Integer localInteger = (Integer)this.sectionCache.get(paramInt);
      if (localInteger != null)
        return localInteger.intValue();
      int i = 0;
      while (i < internalGetSectionCount())
      {
        int k = internalGetCountForSection(i) + j;
        if ((paramInt >= j) && (paramInt < k))
        {
          this.sectionCache.put(paramInt, Integer.valueOf(i));
          return i;
        }
        i += 1;
        j = k;
      }
      return -1;
    }

    public abstract View getSectionHeaderView(int paramInt, View paramView);

    public abstract boolean isEnabled(int paramInt1, int paramInt2);

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return isEnabled(getSectionForPosition(i), getPositionInSectionForPosition(i));
    }

    public void notifyDataSetChanged()
    {
      cleanupCache();
      super.notifyDataSetChanged();
    }

    public abstract void onBindViewHolder(int paramInt1, int paramInt2, RecyclerView.ViewHolder paramViewHolder);

    public final void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      onBindViewHolder(getSectionForPosition(paramInt), getPositionInSectionForPosition(paramInt), paramViewHolder);
    }
  }

  public static abstract class SelectionAdapter extends RecyclerView.Adapter
  {
    public abstract boolean isEnabled(RecyclerView.ViewHolder paramViewHolder);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.RecyclerListView
 * JD-Core Version:    0.6.0
 */