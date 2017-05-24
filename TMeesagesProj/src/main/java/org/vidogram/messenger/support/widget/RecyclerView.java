package org.vidogram.messenger.support.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.v4.e.f;
import android.support.v4.e.g;
import android.support.v4.e.j;
import android.support.v4.view.a.c;
import android.support.v4.view.a.c.l;
import android.support.v4.view.a.c.m;
import android.support.v4.view.a.k;
import android.support.v4.view.ac;
import android.support.v4.view.ae;
import android.support.v4.view.ag;
import android.support.v4.view.u;
import android.support.v4.view.w;
import android.support.v4.view.x;
import android.support.v4.widget.h;
import android.support.v4.widget.t;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.EdgeEffect;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.FileLog;

public class RecyclerView extends ViewGroup
  implements ac, w
{
  static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC;
  private static final boolean ALLOW_THREAD_GAP_WORK;
  private static final int[] CLIP_TO_PADDING_ATTR;
  static final boolean DEBUG = false;
  static final boolean DISPATCH_TEMP_DETACH = false;
  private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION;
  static final boolean FORCE_INVALIDATE_DISPLAY_LIST;
  static final long FOREVER_NS = 9223372036854775807L;
  public static final int HORIZONTAL = 0;
  private static final boolean IGNORE_DETACHED_FOCUSED_CHILD;
  private static final int INVALID_POINTER = -1;
  public static final int INVALID_TYPE = -1;
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  static final int MAX_SCROLL_DURATION = 2000;
  private static final int[] NESTED_SCROLLING_ATTRS = { 16843830 };
  public static final long NO_ID = -1L;
  public static final int NO_POSITION = -1;
  static final boolean POST_UPDATES_ON_ANIMATION;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  static final String TAG = "RecyclerView";
  public static final int TOUCH_SLOP_DEFAULT = 0;
  public static final int TOUCH_SLOP_PAGING = 1;
  static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
  static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
  private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
  static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
  private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
  private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
  static final String TRACE_PREFETCH_TAG = "RV Prefetch";
  static final String TRACE_SCROLL_TAG = "RV Scroll";
  public static final int VERTICAL = 1;
  static final Interpolator sQuinticInterpolator;
  private int glowColor;
  RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
  private final AccessibilityManager mAccessibilityManager;
  private OnItemTouchListener mActiveOnItemTouchListener;
  Adapter mAdapter;
  AdapterHelper mAdapterHelper;
  boolean mAdapterUpdateDuringMeasure;
  private h mBottomGlow;
  private ChildDrawingOrderCallback mChildDrawingOrderCallback;
  ChildHelper mChildHelper;
  boolean mClipToPadding;
  boolean mDataSetHasChangedAfterLayout = false;
  private int mDispatchScrollCounter = 0;
  private int mEatRequestLayout = 0;
  private int mEatenAccessibilityChangeFlags;
  boolean mFirstLayoutComplete;
  GapWorker mGapWorker;
  boolean mHasFixedSize;
  private boolean mIgnoreMotionEventTillDown;
  private int mInitialTouchX;
  private int mInitialTouchY;
  boolean mIsAttached;
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  private RecyclerView.ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
  private Runnable mItemAnimatorRunner;
  final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  boolean mItemsAddedOrRemoved;
  boolean mItemsChanged;
  private int mLastTouchX;
  private int mLastTouchY;
  LayoutManager mLayout;
  boolean mLayoutFrozen;
  private int mLayoutOrScrollCounter = 0;
  boolean mLayoutRequestEaten;
  private h mLeftGlow;
  private final int mMaxFlingVelocity;
  private final int mMinFlingVelocity;
  private final int[] mMinMaxLayoutPositions;
  private final int[] mNestedOffsets;
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  private OnFlingListener mOnFlingListener;
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  final List<ViewHolder> mPendingAccessibilityImportanceChange;
  private SavedState mPendingSavedState;
  boolean mPostedAnimatorRunner;
  GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
  private boolean mPreserveFocusAfterLayout = true;
  final Recycler mRecycler = new Recycler();
  RecyclerListener mRecyclerListener;
  private h mRightGlow;
  private final int[] mScrollConsumed;
  private float mScrollFactor = 1.4E-45F;
  private OnScrollListener mScrollListener;
  private List<OnScrollListener> mScrollListeners;
  private final int[] mScrollOffset;
  private int mScrollPointerId = -1;
  private int mScrollState = 0;
  private x mScrollingChildHelper;
  final State mState;
  final Rect mTempRect = new Rect();
  private final Rect mTempRect2 = new Rect();
  final RectF mTempRectF = new RectF();
  private h mTopGlow;
  private int mTouchSlop;
  final Runnable mUpdateChildViewsRunnable = new Runnable()
  {
    public void run()
    {
      if ((!RecyclerView.this.mFirstLayoutComplete) || (RecyclerView.this.isLayoutRequested()))
        return;
      if (!RecyclerView.this.mIsAttached)
      {
        RecyclerView.this.requestLayout();
        return;
      }
      if (RecyclerView.this.mLayoutFrozen)
      {
        RecyclerView.this.mLayoutRequestEaten = true;
        return;
      }
      RecyclerView.this.consumePendingUpdateOperations();
    }
  };
  private VelocityTracker mVelocityTracker;
  final ViewFlinger mViewFlinger = new ViewFlinger();
  private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
  final ViewInfoStore mViewInfoStore = new ViewInfoStore();
  private int topGlowOffset;

  static
  {
    CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
    if ((Build.VERSION.SDK_INT == 18) || (Build.VERSION.SDK_INT == 19) || (Build.VERSION.SDK_INT == 20))
    {
      bool = true;
      FORCE_INVALIDATE_DISPLAY_LIST = bool;
      if (Build.VERSION.SDK_INT < 23)
        break label171;
      bool = true;
      label64: ALLOW_SIZE_IN_UNSPECIFIED_SPEC = bool;
      if (Build.VERSION.SDK_INT < 16)
        break label176;
      bool = true;
      label78: POST_UPDATES_ON_ANIMATION = bool;
      if (Build.VERSION.SDK_INT < 21)
        break label181;
      bool = true;
      label92: ALLOW_THREAD_GAP_WORK = bool;
      if (Build.VERSION.SDK_INT > 15)
        break label186;
      bool = true;
      label106: FORCE_ABS_FOCUS_SEARCH_DIRECTION = bool;
      if (Build.VERSION.SDK_INT > 15)
        break label191;
    }
    label171: label176: label181: label186: label191: for (boolean bool = true; ; bool = false)
    {
      IGNORE_DETACHED_FOCUSED_CHILD = bool;
      LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = new Class[] { Context.class, AttributeSet.class, Integer.TYPE, Integer.TYPE };
      sQuinticInterpolator = new Interpolator()
      {
        public float getInterpolation(float paramFloat)
        {
          paramFloat -= 1.0F;
          return paramFloat * (paramFloat * paramFloat * paramFloat * paramFloat) + 1.0F;
        }
      };
      return;
      bool = false;
      break;
      bool = false;
      break label64;
      bool = false;
      break label78;
      bool = false;
      break label92;
      bool = false;
      break label106;
    }
  }

  public RecyclerView(Context paramContext)
  {
    this(paramContext, null);
  }

  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }

  public RecyclerView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    GapWorker.LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    if (ALLOW_THREAD_GAP_WORK)
    {
      localLayoutPrefetchRegistryImpl = new GapWorker.LayoutPrefetchRegistryImpl();
      this.mPrefetchRegistry = localLayoutPrefetchRegistryImpl;
      this.mState = new State();
      this.mItemsAddedOrRemoved = false;
      this.mItemsChanged = false;
      this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
      this.mPostedAnimatorRunner = false;
      this.mMinMaxLayoutPositions = new int[2];
      this.mScrollOffset = new int[2];
      this.mScrollConsumed = new int[2];
      this.mNestedOffsets = new int[2];
      this.topGlowOffset = 0;
      this.glowColor = 0;
      this.mPendingAccessibilityImportanceChange = new ArrayList();
      this.mItemAnimatorRunner = new Runnable()
      {
        public void run()
        {
          if (RecyclerView.this.mItemAnimator != null)
            RecyclerView.this.mItemAnimator.runPendingAnimations();
          RecyclerView.this.mPostedAnimatorRunner = false;
        }
      };
      this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback()
      {
        public void processAppeared(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
        {
          RecyclerView.this.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2);
        }

        public void processDisappeared(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
        {
          RecyclerView.this.mRecycler.unscrapView(paramViewHolder);
          RecyclerView.this.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2);
        }

        public void processPersistent(RecyclerView.ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
        {
          paramViewHolder.setIsRecyclable(false);
          if (RecyclerView.this.mDataSetHasChangedAfterLayout)
            if (RecyclerView.this.mItemAnimator.animateChange(paramViewHolder, paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2))
              RecyclerView.this.postAnimationRunner();
          do
            return;
          while (!RecyclerView.this.mItemAnimator.animatePersistence(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2));
          RecyclerView.this.postAnimationRunner();
        }

        public void unused(RecyclerView.ViewHolder paramViewHolder)
        {
          RecyclerView.this.mLayout.removeAndRecycleView(paramViewHolder.itemView, RecyclerView.this.mRecycler);
        }
      };
      if (paramAttributeSet == null)
        break label470;
      paramAttributeSet = paramContext.obtainStyledAttributes(paramAttributeSet, CLIP_TO_PADDING_ATTR, paramInt, 0);
      this.mClipToPadding = paramAttributeSet.getBoolean(0, true);
      paramAttributeSet.recycle();
      label335: setScrollContainer(true);
      setFocusableInTouchMode(true);
      paramContext = ViewConfiguration.get(paramContext);
      this.mTouchSlop = paramContext.getScaledTouchSlop();
      this.mMinFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
      this.mMaxFlingVelocity = paramContext.getScaledMaximumFlingVelocity();
      if (getOverScrollMode() != 2)
        break label478;
    }
    label470: label478: for (boolean bool = true; ; bool = false)
    {
      setWillNotDraw(bool);
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
      initAdapterManager();
      initChildrenHelper();
      if (ag.d(this) == 0)
        ag.c(this, 1);
      this.mAccessibilityManager = ((AccessibilityManager)getContext().getSystemService("accessibility"));
      setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
      setDescendantFocusability(262144);
      setNestedScrollingEnabled(true);
      return;
      localLayoutPrefetchRegistryImpl = null;
      break;
      this.mClipToPadding = true;
      break label335;
    }
  }

  private void addAnimatingView(ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    if (localView.getParent() == this);
    for (int i = 1; ; i = 0)
    {
      this.mRecycler.unscrapView(getChildViewHolder(localView));
      if (!paramViewHolder.isTmpDetached())
        break;
      this.mChildHelper.attachViewToParent(localView, -1, localView.getLayoutParams(), true);
      return;
    }
    if (i == 0)
    {
      this.mChildHelper.addView(localView, true);
      return;
    }
    this.mChildHelper.hide(localView);
  }

  private void animateChange(ViewHolder paramViewHolder1, ViewHolder paramViewHolder2, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramViewHolder1.setIsRecyclable(false);
    if (paramBoolean1)
      addAnimatingView(paramViewHolder1);
    if (paramViewHolder1 != paramViewHolder2)
    {
      if (paramBoolean2)
        addAnimatingView(paramViewHolder2);
      paramViewHolder1.mShadowedHolder = paramViewHolder2;
      addAnimatingView(paramViewHolder1);
      this.mRecycler.unscrapView(paramViewHolder1);
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    }
    if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner();
  }

  private void cancelTouch()
  {
    resetTouch();
    setScrollState(0);
  }

  static void clearNestedRecyclerViewIfNotNested(ViewHolder paramViewHolder)
  {
    Object localObject;
    if (paramViewHolder.mNestedRecyclerView != null)
      localObject = (View)paramViewHolder.mNestedRecyclerView.get();
    while (localObject != null)
    {
      if (localObject == paramViewHolder.itemView)
        return;
      localObject = ((View)localObject).getParent();
      if ((localObject instanceof View))
      {
        localObject = (View)localObject;
        continue;
      }
      localObject = null;
    }
    paramViewHolder.mNestedRecyclerView = null;
  }

  private void createLayoutManager(Context paramContext, String paramString, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    if (paramString != null)
    {
      paramString = paramString.trim();
      if (paramString.length() != 0)
      {
        String str = getFullClassName(paramContext, paramString);
        try
        {
          if (isInEditMode())
            paramString = getClass().getClassLoader();
          while (true)
          {
            Class localClass = paramString.loadClass(str).asSubclass(LayoutManager.class);
            try
            {
              paramString = localClass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
              Object[] arrayOfObject = new Object[4];
              arrayOfObject[0] = paramContext;
              arrayOfObject[1] = paramAttributeSet;
              arrayOfObject[2] = Integer.valueOf(paramInt1);
              arrayOfObject[3] = Integer.valueOf(paramInt2);
              paramContext = arrayOfObject;
              paramString.setAccessible(true);
              setLayoutManager((LayoutManager)paramString.newInstance(paramContext));
              return;
              paramString = paramContext.getClassLoader();
            }
            catch (NoSuchMethodException paramContext)
            {
              try
              {
                paramString = localClass.getConstructor(new Class[0]);
                paramContext = null;
              }
              catch (NoSuchMethodException paramString)
              {
                paramString.initCause(paramContext);
                throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Error creating LayoutManager " + str, paramString);
              }
            }
          }
        }
        catch (java.lang.ClassNotFoundException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Unable to find LayoutManager " + str, paramContext);
        }
        catch (java.lang.reflect.InvocationTargetException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str, paramContext);
        }
        catch (java.lang.InstantiationException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str, paramContext);
        }
        catch (java.lang.IllegalAccessException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Cannot access non-public constructor " + str, paramContext);
        }
        catch (java.lang.ClassCastException paramContext)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Class is not a LayoutManager " + str, paramContext);
        }
      }
    }
  }

  private boolean didChildRangeChange(int paramInt1, int paramInt2)
  {
    int i = 0;
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    if ((this.mMinMaxLayoutPositions[0] != paramInt1) || (this.mMinMaxLayoutPositions[1] != paramInt2))
      i = 1;
    return i;
  }

  private void dispatchContentChangedIfNecessary()
  {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if ((i != 0) && (isAccessibilityEnabled()))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.setEventType(2048);
      android.support.v4.view.a.a.a(localAccessibilityEvent, i);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }

  private void dispatchLayoutStep1()
  {
    boolean bool = true;
    this.mState.assertLayoutStep(1);
    this.mState.mIsMeasuring = false;
    eatRequestLayout();
    this.mViewInfoStore.clear();
    onEnterLayoutOrScroll();
    processAdapterUpdatesAndSetAnimationFlags();
    saveFocusInfo();
    Object localObject = this.mState;
    int j;
    int i;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemsChanged))
    {
      ((State)localObject).mTrackOldChangeHolders = bool;
      this.mItemsChanged = false;
      this.mItemsAddedOrRemoved = false;
      this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
      this.mState.mItemCount = this.mAdapter.getItemCount();
      findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
      if (!this.mState.mRunSimpleAnimations)
        break label295;
      j = this.mChildHelper.getChildCount();
      i = 0;
      label138: if (i >= j)
        break label295;
      localObject = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((!((ViewHolder)localObject).shouldIgnore()) && ((!((ViewHolder)localObject).isInvalid()) || (this.mAdapter.hasStableIds())))
        break label195;
    }
    label195: RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo;
    while (true)
    {
      i += 1;
      break label138;
      bool = false;
      break;
      localItemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, (ViewHolder)localObject, ItemAnimator.buildAdapterChangeFlagsForAnimations((ViewHolder)localObject), ((ViewHolder)localObject).getUnmodifiedPayloads());
      this.mViewInfoStore.addToPreLayout((ViewHolder)localObject, localItemHolderInfo);
      if ((!this.mState.mTrackOldChangeHolders) || (!((ViewHolder)localObject).isUpdated()) || (((ViewHolder)localObject).isRemoved()) || (((ViewHolder)localObject).shouldIgnore()) || (((ViewHolder)localObject).isInvalid()))
        continue;
      long l = getChangedHolderKey((ViewHolder)localObject);
      this.mViewInfoStore.addToOldChangeHolders(l, (ViewHolder)localObject);
    }
    label295: if (this.mState.mRunPredictiveAnimations)
    {
      saveOldPositions();
      bool = this.mState.mStructureChanged;
      this.mState.mStructureChanged = false;
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      this.mState.mStructureChanged = bool;
      i = 0;
      if (i < this.mChildHelper.getChildCount())
      {
        localObject = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (((ViewHolder)localObject).shouldIgnore());
        while (true)
        {
          i += 1;
          break;
          if (this.mViewInfoStore.isInPreLayout((ViewHolder)localObject))
            continue;
          int k = ItemAnimator.buildAdapterChangeFlagsForAnimations((ViewHolder)localObject);
          bool = ((ViewHolder)localObject).hasAnyOfTheFlags(8192);
          j = k;
          if (!bool)
            j = k | 0x1000;
          localItemHolderInfo = this.mItemAnimator.recordPreLayoutInformation(this.mState, (ViewHolder)localObject, j, ((ViewHolder)localObject).getUnmodifiedPayloads());
          if (bool)
          {
            recordAnimationInfoIfBouncedHiddenView((ViewHolder)localObject, localItemHolderInfo);
            continue;
          }
          this.mViewInfoStore.addToAppearedInPreLayoutHolders((ViewHolder)localObject, localItemHolderInfo);
        }
      }
      clearOldPositions();
    }
    while (true)
    {
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      this.mState.mLayoutStep = 2;
      return;
      clearOldPositions();
    }
  }

  private void dispatchLayoutStep2()
  {
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.assertLayoutStep(6);
    this.mAdapterHelper.consumeUpdatesInOnePass();
    this.mState.mItemCount = this.mAdapter.getItemCount();
    this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
    this.mState.mInPreLayout = false;
    this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
    this.mState.mStructureChanged = false;
    this.mPendingSavedState = null;
    State localState = this.mState;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemAnimator != null));
    for (boolean bool = true; ; bool = false)
    {
      localState.mRunSimpleAnimations = bool;
      this.mState.mLayoutStep = 4;
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      return;
    }
  }

  private void dispatchLayoutStep3()
  {
    this.mState.assertLayoutStep(4);
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.mLayoutStep = 1;
    if (this.mState.mRunSimpleAnimations)
    {
      int i = this.mChildHelper.getChildCount() - 1;
      if (i >= 0)
      {
        ViewHolder localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (localViewHolder1.shouldIgnore());
        while (true)
        {
          i -= 1;
          break;
          long l = getChangedHolderKey(localViewHolder1);
          RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo2 = this.mItemAnimator.recordPostLayoutInformation(this.mState, localViewHolder1);
          ViewHolder localViewHolder2 = this.mViewInfoStore.getFromOldChangeHolders(l);
          if ((localViewHolder2 != null) && (!localViewHolder2.shouldIgnore()))
          {
            boolean bool1 = this.mViewInfoStore.isDisappearing(localViewHolder2);
            boolean bool2 = this.mViewInfoStore.isDisappearing(localViewHolder1);
            if ((bool1) && (localViewHolder2 == localViewHolder1))
            {
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
              continue;
            }
            RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo1 = this.mViewInfoStore.popFromPreLayout(localViewHolder2);
            this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
            localItemHolderInfo2 = this.mViewInfoStore.popFromPostLayout(localViewHolder1);
            if (localItemHolderInfo1 == null)
            {
              handleMissingPreInfoForChangeError(l, localViewHolder1, localViewHolder2);
              continue;
            }
            animateChange(localViewHolder2, localViewHolder1, localItemHolderInfo1, localItemHolderInfo2, bool1, bool2);
            continue;
          }
          this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo2);
        }
      }
      this.mViewInfoStore.process(this.mViewInfoProcessCallback);
    }
    this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
    this.mDataSetHasChangedAfterLayout = false;
    this.mState.mRunSimpleAnimations = false;
    this.mState.mRunPredictiveAnimations = false;
    this.mLayout.mRequestedSimpleAnimations = false;
    if (this.mRecycler.mChangedScrap != null)
      this.mRecycler.mChangedScrap.clear();
    if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch)
    {
      this.mLayout.mPrefetchMaxCountObserved = 0;
      this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
      this.mRecycler.updateViewCacheSize();
    }
    this.mLayout.onLayoutCompleted(this.mState);
    onExitLayoutOrScroll();
    resumeRequestLayout(false);
    this.mViewInfoStore.clear();
    if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1]))
      dispatchOnScrolled(0, 0);
    recoverFocusFromState();
    resetFocusInfo();
  }

  private boolean dispatchOnItemTouch(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    int j;
    if (this.mActiveOnItemTouchListener != null)
    {
      if (i == 0)
        this.mActiveOnItemTouchListener = null;
    }
    else
    {
      if (i == 0)
        break label108;
      j = this.mOnItemTouchListeners.size();
      i = 0;
    }
    while (i < j)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
      if (localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        return true;
        this.mActiveOnItemTouchListener.onTouchEvent(this, paramMotionEvent);
        if ((i == 3) || (i == 1))
          this.mActiveOnItemTouchListener = null;
        return true;
      }
      i += 1;
    }
    label108: return false;
  }

  private boolean dispatchOnItemTouchIntercept(MotionEvent paramMotionEvent)
  {
    int j = paramMotionEvent.getAction();
    if ((j == 3) || (j == 0))
      this.mActiveOnItemTouchListener = null;
    int k = this.mOnItemTouchListeners.size();
    int i = 0;
    while (i < k)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(i);
      if ((localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) && (j != 3))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        return true;
      }
      i += 1;
    }
    return false;
  }

  private void findMinMaxChildLayoutPositions(int[] paramArrayOfInt)
  {
    int i1 = this.mChildHelper.getChildCount();
    if (i1 == 0)
    {
      paramArrayOfInt[0] = -1;
      paramArrayOfInt[1] = -1;
      return;
    }
    int i = 2147483647;
    int m = -2147483648;
    int k = 0;
    ViewHolder localViewHolder;
    if (k < i1)
    {
      localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(k));
      if (!localViewHolder.shouldIgnore());
    }
    while (true)
    {
      k += 1;
      break;
      int n = localViewHolder.getLayoutPosition();
      int j = i;
      if (n < i)
        j = n;
      if (n > m)
      {
        m = n;
        i = j;
        continue;
        paramArrayOfInt[0] = i;
        paramArrayOfInt[1] = m;
        return;
      }
      i = j;
    }
  }

  static RecyclerView findNestedRecyclerView(View paramView)
  {
    if (!(paramView instanceof ViewGroup))
      return null;
    if ((paramView instanceof RecyclerView))
      return (RecyclerView)paramView;
    paramView = (ViewGroup)paramView;
    int j = paramView.getChildCount();
    int i = 0;
    while (i < j)
    {
      RecyclerView localRecyclerView = findNestedRecyclerView(paramView.getChildAt(i));
      if (localRecyclerView != null)
        return localRecyclerView;
      i += 1;
    }
    return null;
  }

  private View findNextViewToFocus()
  {
    int i;
    int j;
    label29: ViewHolder localViewHolder;
    if (this.mState.mFocusedItemPosition != -1)
    {
      i = this.mState.mFocusedItemPosition;
      int k = this.mState.getItemCount();
      j = i;
      if (j < k)
      {
        localViewHolder = findViewHolderForAdapterPosition(j);
        if (localViewHolder != null)
          break label77;
      }
      i = Math.min(k, i) - 1;
    }
    while (true)
    {
      if (i < 0)
        break label125;
      localViewHolder = findViewHolderForAdapterPosition(i);
      if (localViewHolder == null)
      {
        return null;
        i = 0;
        break;
        label77: if (localViewHolder.itemView.hasFocusable())
          return localViewHolder.itemView;
        j += 1;
        break label29;
      }
      if (localViewHolder.itemView.hasFocusable())
        return localViewHolder.itemView;
      i -= 1;
    }
    label125: return null;
  }

  static ViewHolder getChildViewHolderInt(View paramView)
  {
    if (paramView == null)
      return null;
    return ((LayoutParams)paramView.getLayoutParams()).mViewHolder;
  }

  static void getDecoratedBoundsWithMarginsInt(View paramView, Rect paramRect)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    int i = paramView.getLeft();
    int j = localRect.left;
    int k = localLayoutParams.leftMargin;
    int m = paramView.getTop();
    int n = localRect.top;
    int i1 = localLayoutParams.topMargin;
    int i2 = paramView.getRight();
    int i3 = localRect.right;
    int i4 = localLayoutParams.rightMargin;
    int i5 = paramView.getBottom();
    int i6 = localRect.bottom;
    paramRect.set(i - j - k, m - n - i1, i2 + i3 + i4, localLayoutParams.bottomMargin + (i6 + i5));
  }

  private int getDeepestFocusedViewWithId(View paramView)
  {
    int i = paramView.getId();
    if ((!paramView.isFocused()) && ((paramView instanceof ViewGroup)) && (paramView.hasFocus()))
    {
      paramView = ((ViewGroup)paramView).getFocusedChild();
      if (paramView.getId() == -1)
        break label52;
      i = paramView.getId();
    }
    label52: 
    while (true)
    {
      break;
      return i;
    }
  }

  private String getFullClassName(Context paramContext, String paramString)
  {
    if (paramString.charAt(0) == '.')
      paramContext = paramContext.getPackageName() + paramString;
    do
    {
      return paramContext;
      paramContext = paramString;
    }
    while (paramString.contains("."));
    return RecyclerView.class.getPackage().getName() + '.' + paramString;
  }

  private float getScrollFactor()
  {
    if (this.mScrollFactor == 1.4E-45F)
    {
      TypedValue localTypedValue = new TypedValue();
      if (getContext().getTheme().resolveAttribute(16842829, localTypedValue, true))
        this.mScrollFactor = localTypedValue.getDimension(getContext().getResources().getDisplayMetrics());
    }
    else
    {
      return this.mScrollFactor;
    }
    return 0.0F;
  }

  private x getScrollingChildHelper()
  {
    if (this.mScrollingChildHelper == null)
      this.mScrollingChildHelper = new x(this);
    return this.mScrollingChildHelper;
  }

  private void handleMissingPreInfoForChangeError(long paramLong, ViewHolder paramViewHolder1, ViewHolder paramViewHolder2)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if (localViewHolder == paramViewHolder1);
      do
      {
        i += 1;
        break;
      }
      while (getChangedHolderKey(localViewHolder) != paramLong);
      if ((this.mAdapter != null) && (this.mAdapter.hasStableIds()))
        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
      throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
    }
    Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + paramViewHolder2 + " cannot be found but it is necessary for " + paramViewHolder1);
  }

  private boolean hasUpdatedView()
  {
    int m = 0;
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    int k = m;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore()));
      do
      {
        i += 1;
        break;
      }
      while (!localViewHolder.isUpdated());
      k = 1;
    }
    return k;
  }

  private void initChildrenHelper()
  {
    this.mChildHelper = new ChildHelper(new ChildHelper.Callback()
    {
      public void addView(View paramView, int paramInt)
      {
        RecyclerView.this.addView(paramView, paramInt);
        RecyclerView.this.dispatchChildAttached(paramView);
      }

      public void attachViewToParent(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
        if (localViewHolder != null)
        {
          if ((!localViewHolder.isTmpDetached()) && (!localViewHolder.shouldIgnore()))
            throw new IllegalArgumentException("Called attach on a child which is not detached: " + localViewHolder);
          localViewHolder.clearTmpDetachFlag();
        }
        RecyclerView.this.attachViewToParent(paramView, paramInt, paramLayoutParams);
      }

      public void detachViewFromParent(int paramInt)
      {
        Object localObject = getChildAt(paramInt);
        if (localObject != null)
        {
          localObject = RecyclerView.getChildViewHolderInt((View)localObject);
          if (localObject != null)
          {
            if ((((RecyclerView.ViewHolder)localObject).isTmpDetached()) && (!((RecyclerView.ViewHolder)localObject).shouldIgnore()))
              throw new IllegalArgumentException("called detach on an already detached child " + localObject);
            ((RecyclerView.ViewHolder)localObject).addFlags(256);
          }
        }
        RecyclerView.this.detachViewFromParent(paramInt);
      }

      public View getChildAt(int paramInt)
      {
        return RecyclerView.this.getChildAt(paramInt);
      }

      public int getChildCount()
      {
        return RecyclerView.this.getChildCount();
      }

      public RecyclerView.ViewHolder getChildViewHolder(View paramView)
      {
        return RecyclerView.getChildViewHolderInt(paramView);
      }

      public int indexOfChild(View paramView)
      {
        return RecyclerView.this.indexOfChild(paramView);
      }

      public void onEnteredHiddenState(View paramView)
      {
        paramView = RecyclerView.getChildViewHolderInt(paramView);
        if (paramView != null)
          RecyclerView.ViewHolder.access$200(paramView, RecyclerView.this);
      }

      public void onLeftHiddenState(View paramView)
      {
        paramView = RecyclerView.getChildViewHolderInt(paramView);
        if (paramView != null)
          RecyclerView.ViewHolder.access$300(paramView, RecyclerView.this);
      }

      public void removeAllViews()
      {
        int j = getChildCount();
        int i = 0;
        while (i < j)
        {
          RecyclerView.this.dispatchChildDetached(getChildAt(i));
          i += 1;
        }
        RecyclerView.this.removeAllViews();
      }

      public void removeViewAt(int paramInt)
      {
        View localView = RecyclerView.this.getChildAt(paramInt);
        if (localView != null)
          RecyclerView.this.dispatchChildDetached(localView);
        RecyclerView.this.removeViewAt(paramInt);
      }
    });
  }

  private boolean isPreferredNextFocus(View paramView1, View paramView2, int paramInt)
  {
    int j = 0;
    if ((paramView2 == null) || (paramView2 == this))
      return false;
    if (paramView1 == null)
      return true;
    if ((paramInt == 2) || (paramInt == 1))
    {
      if (this.mLayout.getLayoutDirection() == 1)
      {
        i = 1;
        if (paramInt == 2)
          j = 1;
        if ((j ^ i) == 0)
          break label83;
      }
      label83: for (int i = 66; ; i = 17)
      {
        if (!isPreferredNextFocusAbsolute(paramView1, paramView2, i))
          break label90;
        return true;
        i = 0;
        break;
      }
      label90: if (paramInt == 2)
        return isPreferredNextFocusAbsolute(paramView1, paramView2, 130);
      return isPreferredNextFocusAbsolute(paramView1, paramView2, 33);
    }
    return isPreferredNextFocusAbsolute(paramView1, paramView2, paramInt);
  }

  private boolean isPreferredNextFocusAbsolute(View paramView1, View paramView2, int paramInt)
  {
    this.mTempRect.set(0, 0, paramView1.getWidth(), paramView1.getHeight());
    this.mTempRect2.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
    offsetDescendantRectToMyCoords(paramView1, this.mTempRect);
    offsetDescendantRectToMyCoords(paramView2, this.mTempRect2);
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException("direction must be absolute. received:" + paramInt);
    case 17:
      if (((this.mTempRect.right <= this.mTempRect2.right) && (this.mTempRect.left < this.mTempRect2.right)) || (this.mTempRect.left <= this.mTempRect2.left))
        break;
    case 66:
    case 33:
    case 130:
    }
    do
    {
      do
      {
        do
        {
          return true;
          return false;
        }
        while (((this.mTempRect.left < this.mTempRect2.left) || (this.mTempRect.right <= this.mTempRect2.left)) && (this.mTempRect.right < this.mTempRect2.right));
        return false;
      }
      while (((this.mTempRect.bottom > this.mTempRect2.bottom) || (this.mTempRect.top >= this.mTempRect2.bottom)) && (this.mTempRect.top > this.mTempRect2.top));
      return false;
    }
    while (((this.mTempRect.top < this.mTempRect2.top) || (this.mTempRect.bottom <= this.mTempRect2.top)) && (this.mTempRect.bottom < this.mTempRect2.bottom));
    return false;
  }

  private void onPointerUp(MotionEvent paramMotionEvent)
  {
    int i = u.b(paramMotionEvent);
    if (paramMotionEvent.getPointerId(i) == this.mScrollPointerId)
      if (i != 0)
        break label75;
    label75: for (i = 1; ; i = 0)
    {
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      int j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      return;
    }
  }

  private boolean predictiveItemAnimationsEnabled()
  {
    return (this.mItemAnimator != null) && (this.mLayout.supportsPredictiveItemAnimations());
  }

  private void processAdapterUpdatesAndSetAnimationFlags()
  {
    boolean bool2 = true;
    if (this.mDataSetHasChangedAfterLayout)
    {
      this.mAdapterHelper.reset();
      this.mLayout.onItemsChanged(this);
    }
    int i;
    label54: State localState;
    if (predictiveItemAnimationsEnabled())
    {
      this.mAdapterHelper.preProcess();
      if ((!this.mItemsAddedOrRemoved) && (!this.mItemsChanged))
        break label173;
      i = 1;
      localState = this.mState;
      if ((!this.mFirstLayoutComplete) || (this.mItemAnimator == null) || ((!this.mDataSetHasChangedAfterLayout) && (i == 0) && (!this.mLayout.mRequestedSimpleAnimations)) || ((this.mDataSetHasChangedAfterLayout) && (!this.mAdapter.hasStableIds())))
        break label178;
      bool1 = true;
      label114: localState.mRunSimpleAnimations = bool1;
      localState = this.mState;
      if ((!this.mState.mRunSimpleAnimations) || (i == 0) || (this.mDataSetHasChangedAfterLayout) || (!predictiveItemAnimationsEnabled()))
        break label183;
    }
    label173: label178: label183: for (boolean bool1 = bool2; ; bool1 = false)
    {
      localState.mRunPredictiveAnimations = bool1;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      break;
      i = 0;
      break label54;
      bool1 = false;
      break label114;
    }
  }

  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int j = 1;
    int k = 0;
    int i;
    if (paramFloat2 < 0.0F)
    {
      ensureLeftGlow();
      i = k;
      if (this.mLeftGlow.a(-paramFloat2 / getWidth(), 1.0F - paramFloat3 / getHeight()))
        i = 1;
      if (paramFloat4 >= 0.0F)
        break label158;
      ensureTopGlow();
      if (!this.mTopGlow.a(-paramFloat4 / getHeight(), paramFloat1 / getWidth()))
        break label196;
    }
    while (true)
    {
      if ((j != 0) || (paramFloat2 != 0.0F) || (paramFloat4 != 0.0F))
        ag.c(this);
      return;
      i = k;
      if (paramFloat2 <= 0.0F)
        break;
      ensureRightGlow();
      i = k;
      if (!this.mRightGlow.a(paramFloat2 / getWidth(), paramFloat3 / getHeight()))
        break;
      i = 1;
      break;
      label158: if (paramFloat4 > 0.0F)
      {
        ensureBottomGlow();
        if (this.mBottomGlow.a(paramFloat4 / getHeight(), 1.0F - paramFloat1 / getWidth()))
          continue;
      }
      label196: j = i;
    }
  }

  private void recoverFocusFromState()
  {
    View localView = null;
    if ((!this.mPreserveFocusAfterLayout) || (this.mAdapter == null) || (!hasFocus()) || (getDescendantFocusability() == 393216) || ((getDescendantFocusability() == 131072) && (isFocused())));
    do
    {
      return;
      if (isFocused())
        break;
      localObject = getFocusedChild();
      if ((!IGNORE_DETACHED_FOCUSED_CHILD) || ((((View)localObject).getParent() != null) && (((View)localObject).hasFocus())))
        continue;
      if (this.mChildHelper.getChildCount() != 0)
        break;
      requestFocus();
      return;
    }
    while (!this.mChildHelper.isHidden((View)localObject));
    if ((this.mState.mFocusedItemId != -1L) && (this.mAdapter.hasStableIds()));
    for (Object localObject = findViewHolderForItemId(this.mState.mFocusedItemId); ; localObject = null)
    {
      if ((localObject == null) || (this.mChildHelper.isHidden(((ViewHolder)localObject).itemView)) || (!((ViewHolder)localObject).itemView.hasFocusable()))
      {
        localObject = localView;
        if (this.mChildHelper.getChildCount() > 0)
          localObject = findNextViewToFocus();
        label191: if (localObject == null)
          break label247;
        if (this.mState.mFocusedSubChildId == -1L)
          break label249;
        localView = ((View)localObject).findViewById(this.mState.mFocusedSubChildId);
        if ((localView == null) || (!localView.isFocusable()))
          break label249;
        localObject = localView;
      }
      label247: label249: 
      while (true)
      {
        ((View)localObject).requestFocus();
        return;
        localObject = ((ViewHolder)localObject).itemView;
        break label191;
        break;
      }
    }
  }

  private void releaseGlows()
  {
    boolean bool2 = false;
    if (this.mLeftGlow != null)
      bool2 = this.mLeftGlow.c();
    boolean bool1 = bool2;
    if (this.mTopGlow != null)
      bool1 = bool2 | this.mTopGlow.c();
    bool2 = bool1;
    if (this.mRightGlow != null)
      bool2 = bool1 | this.mRightGlow.c();
    bool1 = bool2;
    if (this.mBottomGlow != null)
      bool1 = bool2 | this.mBottomGlow.c();
    if (bool1)
      ag.c(this);
  }

  private void resetFocusInfo()
  {
    this.mState.mFocusedItemId = -1L;
    this.mState.mFocusedItemPosition = -1;
    this.mState.mFocusedSubChildId = -1;
  }

  private void resetTouch()
  {
    if (this.mVelocityTracker != null)
      this.mVelocityTracker.clear();
    stopNestedScroll();
    releaseGlows();
  }

  private void saveFocusInfo()
  {
    if ((this.mPreserveFocusAfterLayout) && (hasFocus()) && (this.mAdapter != null));
    for (Object localObject = getFocusedChild(); ; localObject = null)
    {
      if (localObject == null);
      for (localObject = null; localObject == null; localObject = findContainingViewHolder((View)localObject))
      {
        resetFocusInfo();
        return;
      }
      State localState = this.mState;
      long l;
      int i;
      if (this.mAdapter.hasStableIds())
      {
        l = ((ViewHolder)localObject).getItemId();
        localState.mFocusedItemId = l;
        localState = this.mState;
        if (!this.mDataSetHasChangedAfterLayout)
          break label129;
        i = -1;
      }
      while (true)
      {
        localState.mFocusedItemPosition = i;
        this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(((ViewHolder)localObject).itemView);
        return;
        l = -1L;
        break;
        label129: if (((ViewHolder)localObject).isRemoved())
        {
          i = ((ViewHolder)localObject).mOldPosition;
          continue;
        }
        i = ((ViewHolder)localObject).getAdapterPosition();
      }
    }
  }

  private void setAdapterInternal(Adapter paramAdapter, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
      this.mAdapter.onDetachedFromRecyclerView(this);
    }
    if ((!paramBoolean1) || (paramBoolean2))
      removeAndRecycleViews();
    this.mAdapterHelper.reset();
    Adapter localAdapter = this.mAdapter;
    this.mAdapter = paramAdapter;
    if (paramAdapter != null)
    {
      paramAdapter.registerAdapterDataObserver(this.mObserver);
      paramAdapter.onAttachedToRecyclerView(this);
    }
    if (this.mLayout != null)
      this.mLayout.onAdapterChanged(localAdapter, this.mAdapter);
    this.mRecycler.onAdapterChanged(localAdapter, this.mAdapter, paramBoolean1);
    this.mState.mStructureChanged = true;
    markKnownViewsInvalid();
  }

  private void stopScrollersInternal()
  {
    this.mViewFlinger.stop();
    if (this.mLayout != null)
      this.mLayout.stopSmoothScroller();
  }

  void absorbGlows(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
    {
      ensureLeftGlow();
      this.mLeftGlow.a(-paramInt1);
      if (paramInt2 >= 0)
        break label69;
      ensureTopGlow();
      this.mTopGlow.a(-paramInt2);
    }
    while (true)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0))
        ag.c(this);
      return;
      if (paramInt1 <= 0)
        break;
      ensureRightGlow();
      this.mRightGlow.a(paramInt1);
      break;
      label69: if (paramInt2 <= 0)
        continue;
      ensureBottomGlow();
      this.mBottomGlow.a(paramInt2);
    }
  }

  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((this.mLayout == null) || (!this.mLayout.onAddFocusables(this, paramArrayList, paramInt1, paramInt2)))
      super.addFocusables(paramArrayList, paramInt1, paramInt2);
  }

  public void addItemDecoration(ItemDecoration paramItemDecoration)
  {
    addItemDecoration(paramItemDecoration, -1);
  }

  public void addItemDecoration(ItemDecoration paramItemDecoration, int paramInt)
  {
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
    if (this.mItemDecorations.isEmpty())
      setWillNotDraw(false);
    if (paramInt < 0)
      this.mItemDecorations.add(paramItemDecoration);
    while (true)
    {
      markItemDecorInsetsDirty();
      requestLayout();
      return;
      this.mItemDecorations.add(paramInt, paramItemDecoration);
    }
  }

  public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null)
      this.mOnChildAttachStateListeners = new ArrayList();
    this.mOnChildAttachStateListeners.add(paramOnChildAttachStateChangeListener);
  }

  public void addOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.add(paramOnItemTouchListener);
  }

  public void addOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners == null)
      this.mScrollListeners = new ArrayList();
    this.mScrollListeners.add(paramOnScrollListener);
  }

  void animateAppearance(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner();
  }

  void animateDisappearance(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    addAnimatingView(paramViewHolder);
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2))
      postAnimationRunner();
  }

  void applyEdgeEffectColor(h paramh)
  {
    if ((Build.VERSION.SDK_INT >= 21) && (this.glowColor != 0));
    try
    {
      Field localField = h.class.getDeclaredField("a");
      localField.setAccessible(true);
      paramh = (EdgeEffect)localField.get(paramh);
      if (paramh != null)
        paramh.setColor(this.glowColor);
      return;
    }
    catch (java.lang.Exception paramh)
    {
      FileLog.e(paramh);
    }
  }

  void assertInLayoutOrScroll(String paramString)
  {
    if (!isComputingLayout())
    {
      if (paramString == null)
        throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
      throw new IllegalStateException(paramString);
    }
  }

  void assertNotInLayoutOrScroll(String paramString)
  {
    if (isComputingLayout())
    {
      if (paramString == null)
        throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
      throw new IllegalStateException(paramString);
    }
    if (this.mDispatchScrollCounter > 0)
      Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.", new IllegalStateException(""));
  }

  boolean canReuseUpdatedViewHolder(ViewHolder paramViewHolder)
  {
    return (this.mItemAnimator == null) || (this.mItemAnimator.canReuseUpdatedViewHolder(paramViewHolder, paramViewHolder.getUnmodifiedPayloads()));
  }

  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (this.mLayout.checkLayoutParams((LayoutParams)paramLayoutParams));
  }

  void clearOldPositions()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if (!localViewHolder.shouldIgnore())
        localViewHolder.clearOldPosition();
      i += 1;
    }
    this.mRecycler.clearOldPositions();
  }

  public void clearOnChildAttachStateChangeListeners()
  {
    if (this.mOnChildAttachStateListeners != null)
      this.mOnChildAttachStateListeners.clear();
  }

  public void clearOnScrollListeners()
  {
    if (this.mScrollListeners != null)
      this.mScrollListeners.clear();
  }

  public int computeHorizontalScrollExtent()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollHorizontally());
    return this.mLayout.computeHorizontalScrollExtent(this.mState);
  }

  public int computeHorizontalScrollOffset()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollHorizontally());
    return this.mLayout.computeHorizontalScrollOffset(this.mState);
  }

  public int computeHorizontalScrollRange()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollHorizontally());
    return this.mLayout.computeHorizontalScrollRange(this.mState);
  }

  public int computeVerticalScrollExtent()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollVertically());
    return this.mLayout.computeVerticalScrollExtent(this.mState);
  }

  public int computeVerticalScrollOffset()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollVertically());
    return this.mLayout.computeVerticalScrollOffset(this.mState);
  }

  public int computeVerticalScrollRange()
  {
    if (this.mLayout == null);
    do
      return 0;
    while (!this.mLayout.canScrollVertically());
    return this.mLayout.computeVerticalScrollRange(this.mState);
  }

  void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (this.mLeftGlow != null)
    {
      bool1 = bool2;
      if (!this.mLeftGlow.a())
      {
        bool1 = bool2;
        if (paramInt1 > 0)
          bool1 = this.mLeftGlow.c();
      }
    }
    bool2 = bool1;
    if (this.mRightGlow != null)
    {
      bool2 = bool1;
      if (!this.mRightGlow.a())
      {
        bool2 = bool1;
        if (paramInt1 < 0)
          bool2 = bool1 | this.mRightGlow.c();
      }
    }
    bool1 = bool2;
    if (this.mTopGlow != null)
    {
      bool1 = bool2;
      if (!this.mTopGlow.a())
      {
        bool1 = bool2;
        if (paramInt2 > 0)
          bool1 = bool2 | this.mTopGlow.c();
      }
    }
    bool2 = bool1;
    if (this.mBottomGlow != null)
    {
      bool2 = bool1;
      if (!this.mBottomGlow.a())
      {
        bool2 = bool1;
        if (paramInt2 < 0)
          bool2 = bool1 | this.mBottomGlow.c();
      }
    }
    if (bool2)
      ag.c(this);
  }

  void consumePendingUpdateOperations()
  {
    if ((!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout))
    {
      j.a("RV FullInvalidate");
      dispatchLayout();
      j.a();
    }
    label111: 
    do
    {
      do
        return;
      while (!this.mAdapterHelper.hasPendingUpdates());
      if ((!this.mAdapterHelper.hasAnyUpdateTypes(4)) || (this.mAdapterHelper.hasAnyUpdateTypes(11)))
        continue;
      j.a("RV PartialInvalidate");
      eatRequestLayout();
      onEnterLayoutOrScroll();
      this.mAdapterHelper.preProcess();
      if (!this.mLayoutRequestEaten)
      {
        if (!hasUpdatedView())
          break label111;
        dispatchLayout();
      }
      while (true)
      {
        resumeRequestLayout(true);
        onExitLayoutOrScroll();
        j.a();
        return;
        this.mAdapterHelper.consumePostponedUpdates();
      }
    }
    while (!this.mAdapterHelper.hasPendingUpdates());
    j.a("RV FullInvalidate");
    dispatchLayout();
    j.a();
  }

  void defaultOnMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(LayoutManager.chooseSize(paramInt1, getPaddingLeft() + getPaddingRight(), ag.n(this)), LayoutManager.chooseSize(paramInt2, getPaddingTop() + getPaddingBottom(), ag.o(this)));
  }

  void dispatchChildAttached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildAttachedToWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null))
      this.mAdapter.onViewAttachedToWindow(localViewHolder);
    if (this.mOnChildAttachStateListeners != null)
    {
      int i = this.mOnChildAttachStateListeners.size() - 1;
      while (i >= 0)
      {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewAttachedToWindow(paramView);
        i -= 1;
      }
    }
  }

  void dispatchChildDetached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildDetachedFromWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null))
      this.mAdapter.onViewDetachedFromWindow(localViewHolder);
    if (this.mOnChildAttachStateListeners != null)
    {
      int i = this.mOnChildAttachStateListeners.size() - 1;
      while (i >= 0)
      {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewDetachedFromWindow(paramView);
        i -= 1;
      }
    }
  }

  void dispatchLayout()
  {
    if (this.mAdapter == null)
    {
      Log.e("RecyclerView", "No adapter attached; skipping layout");
      return;
    }
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "No layout manager attached; skipping layout");
      return;
    }
    this.mState.mIsMeasuring = false;
    if (this.mState.mLayoutStep == 1)
    {
      dispatchLayoutStep1();
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    }
    while (true)
    {
      dispatchLayoutStep3();
      return;
      if ((this.mAdapterHelper.hasUpdates()) || (this.mLayout.getWidth() != getWidth()) || (this.mLayout.getHeight() != getHeight()))
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
        continue;
      }
      this.mLayout.setExactMeasureSpecsFrom(this);
    }
  }

  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return getScrollingChildHelper().a(paramFloat1, paramFloat2, paramBoolean);
  }

  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return getScrollingChildHelper().a(paramFloat1, paramFloat2);
  }

  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return getScrollingChildHelper().a(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }

  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return getScrollingChildHelper().a(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }

  void dispatchOnScrollStateChanged(int paramInt)
  {
    if (this.mLayout != null)
      this.mLayout.onScrollStateChanged(paramInt);
    onScrollStateChanged(paramInt);
    if (this.mScrollListener != null)
      this.mScrollListener.onScrollStateChanged(this, paramInt);
    if (this.mScrollListeners != null)
    {
      int i = this.mScrollListeners.size() - 1;
      while (i >= 0)
      {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrollStateChanged(this, paramInt);
        i -= 1;
      }
    }
  }

  void dispatchOnScrolled(int paramInt1, int paramInt2)
  {
    this.mDispatchScrollCounter += 1;
    int i = getScrollX();
    int j = getScrollY();
    onScrollChanged(i, j, i, j);
    onScrolled(paramInt1, paramInt2);
    if (this.mScrollListener != null)
      this.mScrollListener.onScrolled(this, paramInt1, paramInt2);
    if (this.mScrollListeners != null)
    {
      i = this.mScrollListeners.size() - 1;
      while (i >= 0)
      {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrolled(this, paramInt1, paramInt2);
        i -= 1;
      }
    }
    this.mDispatchScrollCounter -= 1;
  }

  void dispatchPendingImportantForAccessibilityChanges()
  {
    int i = this.mPendingAccessibilityImportanceChange.size() - 1;
    if (i >= 0)
    {
      ViewHolder localViewHolder = (ViewHolder)this.mPendingAccessibilityImportanceChange.get(i);
      if ((localViewHolder.itemView.getParent() != this) || (localViewHolder.shouldIgnore()));
      while (true)
      {
        i -= 1;
        break;
        int j = localViewHolder.mPendingAccessibilityState;
        if (j == -1)
          continue;
        ag.c(localViewHolder.itemView, j);
        localViewHolder.mPendingAccessibilityState = -1;
      }
    }
    this.mPendingAccessibilityImportanceChange.clear();
  }

  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }

  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }

  public void draw(Canvas paramCanvas)
  {
    int k = 1;
    int m = 0;
    super.draw(paramCanvas);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      ((ItemDecoration)this.mItemDecorations.get(i)).onDrawOver(paramCanvas, this, this.mState);
      i += 1;
    }
    int n;
    if ((this.mLeftGlow != null) && (!this.mLeftGlow.a()))
    {
      n = paramCanvas.save();
      if (this.mClipToPadding)
      {
        i = getPaddingBottom();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(i + -getHeight(), 0.0F);
        if ((this.mLeftGlow == null) || (!this.mLeftGlow.a(paramCanvas)))
          break label466;
        j = 1;
        label128: paramCanvas.restoreToCount(n);
      }
    }
    while (true)
    {
      i = j;
      if (this.mTopGlow != null)
      {
        i = j;
        if (!this.mTopGlow.a())
        {
          n = paramCanvas.save();
          if (this.mClipToPadding)
            paramCanvas.translate(getPaddingLeft(), getPaddingTop());
          paramCanvas.translate(0.0F, this.topGlowOffset);
          if ((this.mTopGlow == null) || (!this.mTopGlow.a(paramCanvas)))
            break label471;
          i = 1;
          label212: i = j | i;
          paramCanvas.restoreToCount(n);
        }
      }
      j = i;
      if (this.mRightGlow != null)
      {
        j = i;
        if (!this.mRightGlow.a())
        {
          n = paramCanvas.save();
          int i1 = getWidth();
          if (!this.mClipToPadding)
            break label476;
          j = getPaddingTop();
          label267: paramCanvas.rotate(90.0F);
          paramCanvas.translate(-j, -i1);
          if ((this.mRightGlow == null) || (!this.mRightGlow.a(paramCanvas)))
            break label481;
          j = 1;
          label305: j = i | j;
          paramCanvas.restoreToCount(n);
        }
      }
      i = j;
      if (this.mBottomGlow != null)
      {
        i = j;
        if (!this.mBottomGlow.a())
        {
          n = paramCanvas.save();
          paramCanvas.rotate(180.0F);
          if (!this.mClipToPadding)
            break label486;
          paramCanvas.translate(-getWidth() + getPaddingRight(), -getHeight() + getPaddingBottom());
          label382: i = m;
          if (this.mBottomGlow != null)
          {
            i = m;
            if (this.mBottomGlow.a(paramCanvas))
              i = 1;
          }
          i = j | i;
          paramCanvas.restoreToCount(n);
        }
      }
      if ((i == 0) && (this.mItemAnimator != null) && (this.mItemDecorations.size() > 0) && (this.mItemAnimator.isRunning()))
        i = k;
      while (true)
      {
        if (i != 0)
          ag.c(this);
        return;
        i = 0;
        break;
        label466: j = 0;
        break label128;
        label471: i = 0;
        break label212;
        label476: j = 0;
        break label267;
        label481: j = 0;
        break label305;
        label486: paramCanvas.translate(-getWidth(), -getHeight());
        break label382;
      }
      j = 0;
    }
  }

  public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return super.drawChild(paramCanvas, paramView, paramLong);
  }

  void eatRequestLayout()
  {
    this.mEatRequestLayout += 1;
    if ((this.mEatRequestLayout == 1) && (!this.mLayoutFrozen))
      this.mLayoutRequestEaten = false;
  }

  void ensureBottomGlow()
  {
    if (this.mBottomGlow != null)
      return;
    this.mBottomGlow = new h(getContext());
    if (this.mClipToPadding)
      this.mBottomGlow.a(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
    while (true)
    {
      applyEdgeEffectColor(this.mBottomGlow);
      return;
      this.mBottomGlow.a(getMeasuredWidth(), getMeasuredHeight());
    }
  }

  void ensureLeftGlow()
  {
    if (this.mLeftGlow != null)
      return;
    this.mLeftGlow = new h(getContext());
    if (this.mClipToPadding)
      this.mLeftGlow.a(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
    while (true)
    {
      applyEdgeEffectColor(this.mLeftGlow);
      return;
      this.mLeftGlow.a(getMeasuredHeight(), getMeasuredWidth());
    }
  }

  void ensureRightGlow()
  {
    if (this.mRightGlow != null)
      return;
    this.mRightGlow = new h(getContext());
    if (this.mClipToPadding)
      this.mRightGlow.a(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
    while (true)
    {
      applyEdgeEffectColor(this.mRightGlow);
      return;
      this.mRightGlow.a(getMeasuredHeight(), getMeasuredWidth());
    }
  }

  void ensureTopGlow()
  {
    if (this.mTopGlow != null)
      return;
    this.mTopGlow = new h(getContext());
    if (this.mClipToPadding)
      this.mTopGlow.a(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
    while (true)
    {
      applyEdgeEffectColor(this.mTopGlow);
      return;
      this.mTopGlow.a(getMeasuredWidth(), getMeasuredHeight());
    }
  }

  public View findChildViewUnder(float paramFloat1, float paramFloat2)
  {
    int i = this.mChildHelper.getChildCount() - 1;
    while (i >= 0)
    {
      View localView = this.mChildHelper.getChildAt(i);
      float f1 = ag.k(localView);
      float f2 = ag.l(localView);
      if ((paramFloat1 >= localView.getLeft() + f1) && (paramFloat1 <= f1 + localView.getRight()) && (paramFloat2 >= localView.getTop() + f2) && (paramFloat2 <= localView.getBottom() + f2))
        return localView;
      i -= 1;
    }
    return null;
  }

  public View findContainingItemView(View paramView)
  {
    for (ViewParent localViewParent = paramView.getParent(); (localViewParent != null) && (localViewParent != this) && ((localViewParent instanceof View)); localViewParent = paramView.getParent())
      paramView = (View)localViewParent;
    if (localViewParent == this)
      return paramView;
    return null;
  }

  public ViewHolder findContainingViewHolder(View paramView)
  {
    paramView = findContainingItemView(paramView);
    if (paramView == null)
      return null;
    return getChildViewHolder(paramView);
  }

  public ViewHolder findViewHolderForAdapterPosition(int paramInt)
  {
    Object localObject = null;
    if (this.mDataSetHasChangedAfterLayout);
    int i;
    while (true)
    {
      return localObject;
      int j = this.mChildHelper.getUnfilteredChildCount();
      i = 0;
      localObject = null;
      if (i >= j)
        break;
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder == null) || (localViewHolder.isRemoved()) || (getAdapterPositionFor(localViewHolder) != paramInt))
        break label100;
      localObject = localViewHolder;
      if (!this.mChildHelper.isHidden(localViewHolder.itemView))
        continue;
      localObject = localViewHolder;
    }
    label100: 
    while (true)
    {
      i += 1;
      break;
      return localObject;
    }
  }

  public ViewHolder findViewHolderForItemId(long paramLong)
  {
    ViewHolder localViewHolder2 = null;
    ViewHolder localViewHolder1 = localViewHolder2;
    if (this.mAdapter != null)
    {
      if (this.mAdapter.hasStableIds())
        break label31;
      localViewHolder1 = localViewHolder2;
    }
    label31: int i;
    while (true)
    {
      return localViewHolder1;
      int j = this.mChildHelper.getUnfilteredChildCount();
      i = 0;
      localViewHolder1 = null;
      if (i >= j)
        break;
      localViewHolder2 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder2 == null) || (localViewHolder2.isRemoved()) || (localViewHolder2.getItemId() != paramLong))
        break label120;
      localViewHolder1 = localViewHolder2;
      if (!this.mChildHelper.isHidden(localViewHolder2.itemView))
        continue;
      localViewHolder1 = localViewHolder2;
    }
    label120: 
    while (true)
    {
      i += 1;
      break;
      return localViewHolder1;
    }
  }

  public ViewHolder findViewHolderForLayoutPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }

  @Deprecated
  public ViewHolder findViewHolderForPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }

  ViewHolder findViewHolderForPosition(int paramInt, boolean paramBoolean)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    Object localObject1 = null;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      Object localObject2 = localObject1;
      if (localViewHolder != null)
      {
        localObject2 = localObject1;
        if (!localViewHolder.isRemoved())
        {
          if (!paramBoolean)
            break label82;
          if (localViewHolder.mPosition == paramInt)
            break label95;
          localObject2 = localObject1;
        }
      }
      while (true)
      {
        i += 1;
        localObject1 = localObject2;
        break;
        label82: localObject2 = localObject1;
        if (localViewHolder.getLayoutPosition() != paramInt)
          continue;
        label95: localObject1 = localViewHolder;
        if (!this.mChildHelper.isHidden(localViewHolder.itemView))
          break label121;
        localObject2 = localViewHolder;
      }
    }
    label121: return (ViewHolder)localObject1;
  }

  public boolean fling(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null)
      Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    boolean bool1;
    int i;
    do
    {
      boolean bool2;
      do
      {
        do
          return false;
        while (this.mLayoutFrozen);
        bool1 = this.mLayout.canScrollHorizontally();
        bool2 = this.mLayout.canScrollVertically();
        if (bool1)
        {
          i = paramInt1;
          if (Math.abs(paramInt1) >= this.mMinFlingVelocity);
        }
        else
        {
          i = 0;
        }
        if (bool2)
        {
          paramInt1 = paramInt2;
          if (Math.abs(paramInt2) >= this.mMinFlingVelocity)
            continue;
        }
        paramInt1 = 0;
      }
      while (((i == 0) && (paramInt1 == 0)) || (dispatchNestedPreFling(i, paramInt1)));
      if ((bool1) || (bool2));
      for (bool1 = true; ; bool1 = false)
      {
        dispatchNestedFling(i, paramInt1, bool1);
        if ((this.mOnFlingListener == null) || (!this.mOnFlingListener.onFling(i, paramInt1)))
          break;
        return true;
      }
    }
    while (!bool1);
    paramInt2 = Math.max(-this.mMaxFlingVelocity, Math.min(i, this.mMaxFlingVelocity));
    paramInt1 = Math.max(-this.mMaxFlingVelocity, Math.min(paramInt1, this.mMaxFlingVelocity));
    this.mViewFlinger.fling(paramInt2, paramInt1);
    return true;
  }

  public View focusSearch(View paramView, int paramInt)
  {
    int k = 1;
    Object localObject2 = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (localObject2 != null)
      return localObject2;
    int i;
    label52: Object localObject1;
    int j;
    if ((this.mAdapter != null) && (this.mLayout != null) && (!isComputingLayout()) && (!this.mLayoutFrozen))
    {
      i = 1;
      localObject1 = FocusFinder.getInstance();
      if ((i == 0) || ((paramInt != 2) && (paramInt != 1)))
        break label308;
      if (!this.mLayout.canScrollVertically())
        break label384;
      if (paramInt != 2)
        break label214;
      j = 130;
      label91: if (((FocusFinder)localObject1).findNextFocus(this, paramView, j) != null)
        break label221;
      i = 1;
      label105: if (!FORCE_ABS_FOCUS_SEARCH_DIRECTION)
        break label381;
      paramInt = j;
    }
    while (true)
    {
      if ((i == 0) && (this.mLayout.canScrollHorizontally()))
        if (this.mLayout.getLayoutDirection() == 1)
        {
          i = 1;
          label141: if (paramInt != 2)
            break label231;
          j = 1;
          label149: if ((j ^ i) == 0)
            break label237;
          i = 66;
          label159: if (((FocusFinder)localObject1).findNextFocus(this, paramView, i) != null)
            break label243;
          j = k;
          label174: k = j;
          if (FORCE_ABS_FOCUS_SEARCH_DIRECTION)
            paramInt = i;
        }
      for (k = j; ; k = i)
      {
        if (k != 0)
        {
          consumePendingUpdateOperations();
          if (findContainingItemView(paramView) == null)
          {
            return null;
            i = 0;
            break label52;
            label214: j = 33;
            break label91;
            label221: i = 0;
            break label105;
            i = 0;
            break label141;
            label231: j = 0;
            break label149;
            label237: i = 17;
            break label159;
            label243: j = 0;
            break label174;
          }
          eatRequestLayout();
          this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
          resumeRequestLayout(false);
        }
        localObject1 = ((FocusFinder)localObject1).findNextFocus(this, paramView, paramInt);
        while (true)
        {
          localObject2 = localObject1;
          if (isPreferredNextFocus(paramView, (View)localObject1, paramInt))
            break;
          return super.focusSearch(paramView, paramInt);
          label308: localObject1 = ((FocusFinder)localObject1).findNextFocus(this, paramView, paramInt);
          if ((localObject1 == null) && (i != 0))
          {
            consumePendingUpdateOperations();
            if (findContainingItemView(paramView) == null)
              return null;
            eatRequestLayout();
            localObject1 = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
            resumeRequestLayout(false);
            continue;
          }
        }
      }
      label381: continue;
      label384: i = 0;
    }
  }

  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mLayout == null)
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    return this.mLayout.generateDefaultLayoutParams();
  }

  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    if (this.mLayout == null)
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    return this.mLayout.generateLayoutParams(getContext(), paramAttributeSet);
  }

  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (this.mLayout == null)
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    return this.mLayout.generateLayoutParams(paramLayoutParams);
  }

  public Adapter getAdapter()
  {
    return this.mAdapter;
  }

  int getAdapterPositionFor(ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.hasAnyOfTheFlags(524)) || (!paramViewHolder.isBound()))
      return -1;
    return this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition);
  }

  public int getBaseline()
  {
    if (this.mLayout != null)
      return this.mLayout.getBaseline();
    return super.getBaseline();
  }

  long getChangedHolderKey(ViewHolder paramViewHolder)
  {
    if (this.mAdapter.hasStableIds())
      return paramViewHolder.getItemId();
    return paramViewHolder.mPosition;
  }

  public int getChildAdapterPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null)
      return paramView.getAdapterPosition();
    return -1;
  }

  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mChildDrawingOrderCallback == null)
      return super.getChildDrawingOrder(paramInt1, paramInt2);
    return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(paramInt1, paramInt2);
  }

  public long getChildItemId(View paramView)
  {
    if ((this.mAdapter == null) || (!this.mAdapter.hasStableIds()));
    do
    {
      return -1L;
      paramView = getChildViewHolderInt(paramView);
    }
    while (paramView == null);
    return paramView.getItemId();
  }

  public int getChildLayoutPosition(View paramView)
  {
    paramView = getChildViewHolderInt(paramView);
    if (paramView != null)
      return paramView.getLayoutPosition();
    return -1;
  }

  @Deprecated
  public int getChildPosition(View paramView)
  {
    return getChildAdapterPosition(paramView);
  }

  public ViewHolder getChildViewHolder(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent != null) && (localViewParent != this))
      throw new IllegalArgumentException("View " + paramView + " is not a direct child of " + this);
    return getChildViewHolderInt(paramView);
  }

  public boolean getClipToPadding()
  {
    return this.mClipToPadding;
  }

  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }

  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
  {
    getDecoratedBoundsWithMarginsInt(paramView, paramRect);
  }

  public ItemAnimator getItemAnimator()
  {
    return this.mItemAnimator;
  }

  Rect getItemDecorInsetsForChild(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.mInsetsDirty)
      return localLayoutParams.mDecorInsets;
    if ((this.mState.isPreLayout()) && ((localLayoutParams.isItemChanged()) || (localLayoutParams.isViewInvalid())))
      return localLayoutParams.mDecorInsets;
    Rect localRect = localLayoutParams.mDecorInsets;
    localRect.set(0, 0, 0, 0);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      this.mTempRect.set(0, 0, 0, 0);
      ((ItemDecoration)this.mItemDecorations.get(i)).getItemOffsets(this.mTempRect, paramView, this, this.mState);
      localRect.left += this.mTempRect.left;
      localRect.top += this.mTempRect.top;
      localRect.right += this.mTempRect.right;
      localRect.bottom += this.mTempRect.bottom;
      i += 1;
    }
    localLayoutParams.mInsetsDirty = false;
    return localRect;
  }

  public LayoutManager getLayoutManager()
  {
    return this.mLayout;
  }

  public int getMaxFlingVelocity()
  {
    return this.mMaxFlingVelocity;
  }

  public int getMinFlingVelocity()
  {
    return this.mMinFlingVelocity;
  }

  long getNanoTime()
  {
    if (ALLOW_THREAD_GAP_WORK)
      return System.nanoTime();
    return 0L;
  }

  public OnFlingListener getOnFlingListener()
  {
    return this.mOnFlingListener;
  }

  public boolean getPreserveFocusAfterLayout()
  {
    return this.mPreserveFocusAfterLayout;
  }

  public RecycledViewPool getRecycledViewPool()
  {
    return this.mRecycler.getRecycledViewPool();
  }

  public int getScrollState()
  {
    return this.mScrollState;
  }

  public boolean hasFixedSize()
  {
    return this.mHasFixedSize;
  }

  public boolean hasNestedScrollingParent()
  {
    return getScrollingChildHelper().b();
  }

  public boolean hasPendingAdapterUpdates()
  {
    return (!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout) || (this.mAdapterHelper.hasPendingUpdates());
  }

  void initAdapterManager()
  {
    this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback()
    {
      void dispatchUpdate(AdapterHelper.UpdateOp paramUpdateOp)
      {
        switch (paramUpdateOp.cmd)
        {
        case 3:
        case 5:
        case 6:
        case 7:
        default:
          return;
        case 1:
          RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, paramUpdateOp.positionStart, paramUpdateOp.itemCount);
          return;
        case 2:
          RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, paramUpdateOp.positionStart, paramUpdateOp.itemCount);
          return;
        case 4:
          RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, paramUpdateOp.positionStart, paramUpdateOp.itemCount, paramUpdateOp.payload);
          return;
        case 8:
        }
        RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, paramUpdateOp.positionStart, paramUpdateOp.itemCount, 1);
      }

      public RecyclerView.ViewHolder findViewHolder(int paramInt)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.this.findViewHolderForPosition(paramInt, true);
        if (localViewHolder == null);
        do
          return null;
        while (RecyclerView.this.mChildHelper.isHidden(localViewHolder.itemView));
        return localViewHolder;
      }

      public void markViewHoldersUpdated(int paramInt1, int paramInt2, Object paramObject)
      {
        RecyclerView.this.viewRangeUpdate(paramInt1, paramInt2, paramObject);
        RecyclerView.this.mItemsChanged = true;
      }

      public void offsetPositionsForAdd(int paramInt1, int paramInt2)
      {
        RecyclerView.this.offsetPositionRecordsForInsert(paramInt1, paramInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }

      public void offsetPositionsForMove(int paramInt1, int paramInt2)
      {
        RecyclerView.this.offsetPositionRecordsForMove(paramInt1, paramInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }

      public void offsetPositionsForRemovingInvisible(int paramInt1, int paramInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramInt1, paramInt2, true);
        RecyclerView.this.mItemsAddedOrRemoved = true;
        RecyclerView.State localState = RecyclerView.this.mState;
        localState.mDeletedInvisibleItemCountSincePreviousLayout += paramInt2;
      }

      public void offsetPositionsForRemovingLaidOutOrNewView(int paramInt1, int paramInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramInt1, paramInt2, false);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }

      public void onDispatchFirstPass(AdapterHelper.UpdateOp paramUpdateOp)
      {
        dispatchUpdate(paramUpdateOp);
      }

      public void onDispatchSecondPass(AdapterHelper.UpdateOp paramUpdateOp)
      {
        dispatchUpdate(paramUpdateOp);
      }
    });
  }

  void invalidateGlows()
  {
    this.mBottomGlow = null;
    this.mTopGlow = null;
    this.mRightGlow = null;
    this.mLeftGlow = null;
  }

  public void invalidateItemDecorations()
  {
    if (this.mItemDecorations.size() == 0)
      return;
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
    markItemDecorInsetsDirty();
    requestLayout();
  }

  boolean isAccessibilityEnabled()
  {
    return (this.mAccessibilityManager != null) && (this.mAccessibilityManager.isEnabled());
  }

  public boolean isAnimating()
  {
    return (this.mItemAnimator != null) && (this.mItemAnimator.isRunning());
  }

  public boolean isAttachedToWindow()
  {
    return this.mIsAttached;
  }

  public boolean isComputingLayout()
  {
    return this.mLayoutOrScrollCounter > 0;
  }

  public boolean isLayoutFrozen()
  {
    return this.mLayoutFrozen;
  }

  public boolean isNestedScrollingEnabled()
  {
    return getScrollingChildHelper().a();
  }

  void jumpToPositionForSmoothScroller(int paramInt)
  {
    if (this.mLayout == null)
      return;
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }

  void markItemDecorInsetsDirty()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(i).getLayoutParams()).mInsetsDirty = true;
      i += 1;
    }
    this.mRecycler.markItemDecorInsetsDirty();
  }

  void markKnownViewsInvalid()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()))
        localViewHolder.addFlags(6);
      i += 1;
    }
    markItemDecorInsetsDirty();
    this.mRecycler.markKnownViewsInvalid();
  }

  public void offsetChildrenHorizontal(int paramInt)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mChildHelper.getChildAt(i).offsetLeftAndRight(paramInt);
      i += 1;
    }
  }

  public void offsetChildrenVertical(int paramInt)
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      this.mChildHelper.getChildAt(i).offsetTopAndBottom(paramInt);
      i += 1;
    }
  }

  void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()) && (localViewHolder.mPosition >= paramInt1))
      {
        localViewHolder.offsetPosition(paramInt2, false);
        this.mState.mStructureChanged = true;
      }
      i += 1;
    }
    this.mRecycler.offsetPositionRecordsForInsert(paramInt1, paramInt2);
    requestLayout();
  }

  void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
  {
    int n = this.mChildHelper.getUnfilteredChildCount();
    int i;
    int j;
    int k;
    if (paramInt1 < paramInt2)
    {
      i = -1;
      j = paramInt2;
      k = paramInt1;
    }
    while (true)
    {
      int m = 0;
      ViewHolder localViewHolder;
      while (true)
      {
        if (m >= n)
          break label130;
        localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(m));
        if ((localViewHolder == null) || (localViewHolder.mPosition < k) || (localViewHolder.mPosition > j))
        {
          m += 1;
          continue;
          i = 1;
          j = paramInt1;
          k = paramInt2;
          break;
        }
      }
      if (localViewHolder.mPosition == paramInt1)
        localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
      while (true)
      {
        this.mState.mStructureChanged = true;
        break;
        localViewHolder.offsetPosition(i, false);
      }
    }
    label130: this.mRecycler.offsetPositionRecordsForMove(paramInt1, paramInt2);
    requestLayout();
  }

  void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()))
      {
        if (localViewHolder.mPosition < paramInt1 + paramInt2)
          break label82;
        localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        this.mState.mStructureChanged = true;
      }
      while (true)
      {
        i += 1;
        break;
        label82: if (localViewHolder.mPosition < paramInt1)
          continue;
        localViewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
        this.mState.mStructureChanged = true;
      }
    }
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }

  protected void onAttachedToWindow()
  {
    boolean bool = true;
    super.onAttachedToWindow();
    this.mLayoutOrScrollCounter = 0;
    this.mIsAttached = true;
    float f;
    if ((this.mFirstLayoutComplete) && (!isLayoutRequested()))
    {
      this.mFirstLayoutComplete = bool;
      if (this.mLayout != null)
        this.mLayout.dispatchAttachedToWindow(this);
      this.mPostedAnimatorRunner = false;
      if (ALLOW_THREAD_GAP_WORK)
      {
        this.mGapWorker = ((GapWorker)GapWorker.sGapWorker.get());
        if (this.mGapWorker == null)
        {
          this.mGapWorker = new GapWorker();
          Display localDisplay = ag.A(this);
          if ((isInEditMode()) || (localDisplay == null))
            break label158;
          f = localDisplay.getRefreshRate();
          if (f < 30.0F)
            break label158;
        }
      }
    }
    while (true)
    {
      this.mGapWorker.mFrameIntervalNs = ()(1.0E+009F / f);
      GapWorker.sGapWorker.set(this.mGapWorker);
      this.mGapWorker.add(this);
      return;
      bool = false;
      break;
      label158: f = 60.0F;
    }
  }

  public void onChildAttachedToWindow(View paramView)
  {
  }

  public void onChildDetachedFromWindow(View paramView)
  {
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mItemAnimator != null)
      this.mItemAnimator.endAnimations();
    stopScroll();
    this.mIsAttached = false;
    if (this.mLayout != null)
      this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
    this.mPendingAccessibilityImportanceChange.clear();
    removeCallbacks(this.mItemAnimatorRunner);
    this.mViewInfoStore.onDetach();
    if (ALLOW_THREAD_GAP_WORK)
    {
      this.mGapWorker.remove(this);
      this.mGapWorker = null;
    }
  }

  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int j = this.mItemDecorations.size();
    int i = 0;
    while (i < j)
    {
      ((ItemDecoration)this.mItemDecorations.get(i)).onDraw(paramCanvas, this, this.mState);
      i += 1;
    }
  }

  void onEnterLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter += 1;
  }

  void onExitLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter -= 1;
    if (this.mLayoutOrScrollCounter < 1)
    {
      this.mLayoutOrScrollCounter = 0;
      dispatchContentChangedIfNecessary();
      dispatchPendingImportantForAccessibilityChanges();
    }
  }

  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayout == null);
    label110: label113: 
    while (true)
    {
      return false;
      if ((this.mLayoutFrozen) || ((paramMotionEvent.getSource() & 0x2) == 0) || (paramMotionEvent.getAction() != 8))
        continue;
      float f1;
      float f2;
      if (this.mLayout.canScrollVertically())
      {
        f1 = -u.a(paramMotionEvent, 9);
        if (!this.mLayout.canScrollHorizontally())
          break label110;
        f2 = u.a(paramMotionEvent, 10);
      }
      while (true)
      {
        if ((f1 == 0.0F) && (f2 == 0.0F))
          break label113;
        float f3 = getScrollFactor();
        scrollByInternal((int)(f2 * f3), (int)(f1 * f3), paramMotionEvent);
        return false;
        f1 = 0.0F;
        break;
        f2 = 0.0F;
      }
    }
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int k = -1;
    int i1 = 1;
    if (this.mLayoutFrozen);
    do
    {
      return false;
      if (!dispatchOnItemTouchIntercept(paramMotionEvent))
        continue;
      cancelTouch();
      return true;
    }
    while (this.mLayout == null);
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain();
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int j = u.a(paramMotionEvent);
    int i = u.b(paramMotionEvent);
    switch (j)
    {
    case 4:
    default:
      if (this.mScrollState != 1)
        break;
      return i1;
    case 0:
      label136: if (this.mIgnoreMotionEventTillDown)
        this.mIgnoreMotionEventTillDown = false;
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      i = (int)(paramMotionEvent.getX() + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY() + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      if (this.mScrollState == 2)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        setScrollState(1);
      }
      paramMotionEvent = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      paramMotionEvent[0] = 0;
      if (!bool1);
    case 5:
    case 2:
    case 6:
    case 1:
    case 3:
    }
    for (i = 1; ; i = 0)
    {
      j = i;
      if (bool2)
        j = i | 0x2;
      startNestedScroll(j);
      break;
      this.mScrollPointerId = paramMotionEvent.getPointerId(i);
      j = (int)(paramMotionEvent.getX(i) + 0.5F);
      this.mLastTouchX = j;
      this.mInitialTouchX = j;
      i = (int)(paramMotionEvent.getY(i) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      break;
      j = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (j < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      i = (int)(paramMotionEvent.getX(j) + 0.5F);
      j = (int)(paramMotionEvent.getY(j) + 0.5F);
      if (this.mScrollState == 1)
        break;
      i -= this.mInitialTouchX;
      int m = j - this.mInitialTouchY;
      int n;
      if ((bool1) && (Math.abs(i) > this.mTouchSlop))
      {
        j = this.mInitialTouchX;
        n = this.mTouchSlop;
        if (i < 0)
        {
          i = -1;
          label448: this.mLastTouchX = (i * n + j);
        }
      }
      for (i = 1; ; i = 0)
      {
        j = i;
        if (bool2)
        {
          j = i;
          if (Math.abs(m) > this.mTouchSlop)
          {
            j = this.mInitialTouchY;
            n = this.mTouchSlop;
            if (m >= 0)
              break label529;
          }
        }
        label529: for (i = k; ; i = 1)
        {
          this.mLastTouchY = (j + i * n);
          j = 1;
          if (j == 0)
            break;
          setScrollState(1);
          break;
          i = 1;
          break label448;
        }
        onPointerUp(paramMotionEvent);
        break;
        this.mVelocityTracker.clear();
        stopNestedScroll();
        break;
        cancelTouch();
        break;
        i1 = 0;
        break label136;
      }
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    j.a("RV OnLayout");
    dispatchLayout();
    j.a();
    this.mFirstLayoutComplete = true;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = 0;
    if (this.mLayout == null)
      defaultOnMeasure(paramInt1, paramInt2);
    while (true)
    {
      return;
      if (!this.mLayout.mAutoMeasure)
        break;
      int k = View.MeasureSpec.getMode(paramInt1);
      int m = View.MeasureSpec.getMode(paramInt2);
      int i = j;
      if (k == 1073741824)
      {
        i = j;
        if (m == 1073741824)
          i = 1;
      }
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      if ((i != 0) || (this.mAdapter == null))
        continue;
      if (this.mState.mLayoutStep == 1)
        dispatchLayoutStep1();
      this.mLayout.setMeasureSpecs(paramInt1, paramInt2);
      this.mState.mIsMeasuring = true;
      dispatchLayoutStep2();
      this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
      if (!this.mLayout.shouldMeasureTwice())
        continue;
      this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      this.mState.mIsMeasuring = true;
      dispatchLayoutStep2();
      this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
      return;
    }
    if (this.mHasFixedSize)
    {
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      return;
    }
    if (this.mAdapterUpdateDuringMeasure)
    {
      eatRequestLayout();
      processAdapterUpdatesAndSetAnimationFlags();
      if (this.mState.mRunPredictiveAnimations)
      {
        this.mState.mInPreLayout = true;
        this.mAdapterUpdateDuringMeasure = false;
        resumeRequestLayout(false);
      }
    }
    else
    {
      if (this.mAdapter == null)
        break label337;
    }
    label337: for (this.mState.mItemCount = this.mAdapter.getItemCount(); ; this.mState.mItemCount = 0)
    {
      eatRequestLayout();
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      resumeRequestLayout(false);
      this.mState.mInPreLayout = false;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      this.mState.mInPreLayout = false;
      break;
    }
  }

  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (isComputingLayout())
      return false;
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }

  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
      super.onRestoreInstanceState(paramParcelable);
    do
    {
      return;
      this.mPendingSavedState = ((SavedState)paramParcelable);
      super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
    }
    while ((this.mLayout == null) || (this.mPendingSavedState.mLayoutState == null));
    this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
  }

  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (this.mPendingSavedState != null)
    {
      localSavedState.copyFrom(this.mPendingSavedState);
      return localSavedState;
    }
    if (this.mLayout != null)
    {
      localSavedState.mLayoutState = this.mLayout.onSaveInstanceState();
      return localSavedState;
    }
    localSavedState.mLayoutState = null;
    return localSavedState;
  }

  public void onScrollStateChanged(int paramInt)
  {
  }

  public void onScrolled(int paramInt1, int paramInt2)
  {
  }

  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 != paramInt3) || (paramInt2 != paramInt4))
      invalidateGlows();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i2 = 0;
    if ((this.mLayoutFrozen) || (this.mIgnoreMotionEventTillDown));
    do
    {
      return false;
      if (!dispatchOnItemTouch(paramMotionEvent))
        continue;
      cancelTouch();
      return true;
    }
    while (this.mLayout == null);
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain();
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int k = u.a(paramMotionEvent);
    int j = u.b(paramMotionEvent);
    if (k == 0)
    {
      int[] arrayOfInt = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt[0] = 0;
    }
    localMotionEvent.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
    int i = i2;
    switch (k)
    {
    default:
      i = i2;
    case 4:
      if (i == 0)
        this.mVelocityTracker.addMovement(localMotionEvent);
      localMotionEvent.recycle();
      return true;
    case 0:
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      i = (int)(paramMotionEvent.getX() + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY() + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      if (!bool1)
        break;
    case 5:
    case 2:
    case 6:
    case 1:
    case 3:
    }
    label661: label737: for (i = 1; ; i = 0)
    {
      j = i;
      if (bool2)
        j = i | 0x2;
      startNestedScroll(j);
      i = i2;
      break;
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      i = (int)(paramMotionEvent.getX(j) + 0.5F);
      this.mLastTouchX = i;
      this.mInitialTouchX = i;
      i = (int)(paramMotionEvent.getY(j) + 0.5F);
      this.mLastTouchY = i;
      this.mInitialTouchY = i;
      i = i2;
      break;
      i = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (i < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      int i3 = (int)(paramMotionEvent.getX(i) + 0.5F);
      int i4 = (int)(paramMotionEvent.getY(i) + 0.5F);
      int m = this.mLastTouchX - i3;
      k = this.mLastTouchY - i4;
      i = k;
      j = m;
      if (dispatchNestedPreScroll(m, k, this.mScrollConsumed, this.mScrollOffset))
      {
        j = m - this.mScrollConsumed[0];
        i = k - this.mScrollConsumed[1];
        localMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[0] += this.mScrollOffset[0];
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[1] += this.mScrollOffset[1];
      }
      k = i;
      m = j;
      if (this.mScrollState != 1)
      {
        if ((!bool1) || (Math.abs(j) <= this.mTouchSlop))
          break label980;
        if (j <= 0)
          break label813;
        j -= this.mTouchSlop;
      }
      label611: for (k = 1; ; k = 0)
      {
        int n = i;
        int i1 = k;
        if (bool2)
        {
          n = i;
          i1 = k;
          if (Math.abs(i) > this.mTouchSlop)
          {
            if (i <= 0)
              break label825;
            n = i - this.mTouchSlop;
            i1 = 1;
          }
        }
        k = n;
        m = j;
        if (i1 != 0)
        {
          setScrollState(1);
          m = j;
          k = n;
        }
        i = i2;
        if (this.mScrollState != 1)
          break;
        this.mLastTouchX = (i3 - this.mScrollOffset[0]);
        this.mLastTouchY = (i4 - this.mScrollOffset[1]);
        if (bool1)
        {
          i = m;
          if (!bool2)
            break label843;
        }
        for (j = k; ; j = 0)
        {
          if (scrollByInternal(i, j, localMotionEvent))
            getParent().requestDisallowInterceptTouchEvent(true);
          i = i2;
          if (this.mGapWorker == null)
            break;
          if (m == 0)
          {
            i = i2;
            if (k == 0)
              break;
          }
          this.mGapWorker.postFromTraversal(this, m, k);
          i = i2;
          break;
          j += this.mTouchSlop;
          break label611;
          n = i + this.mTouchSlop;
          break label661;
          i = 0;
          break label737;
        }
        onPointerUp(paramMotionEvent);
        i = i2;
        break;
        this.mVelocityTracker.addMovement(localMotionEvent);
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
        float f1;
        float f2;
        if (bool1)
        {
          f1 = -ae.a(this.mVelocityTracker, this.mScrollPointerId);
          if (!bool2)
            break label964;
          f2 = -ae.b(this.mVelocityTracker, this.mScrollPointerId);
        }
        while (true)
        {
          if (((f1 == 0.0F) && (f2 == 0.0F)) || (!fling((int)f1, (int)f2)))
            setScrollState(0);
          resetTouch();
          i = 1;
          break;
          f1 = 0.0F;
          break label903;
          f2 = 0.0F;
        }
        cancelTouch();
        i = i2;
        break;
      }
    }
  }

  void postAnimationRunner()
  {
    if ((!this.mPostedAnimatorRunner) && (this.mIsAttached))
    {
      ag.a(this, this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    }
  }

  void recordAnimationInfoIfBouncedHiddenView(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo)
  {
    paramViewHolder.setFlags(0, 8192);
    if ((this.mState.mTrackOldChangeHolders) && (paramViewHolder.isUpdated()) && (!paramViewHolder.isRemoved()) && (!paramViewHolder.shouldIgnore()))
    {
      long l = getChangedHolderKey(paramViewHolder);
      this.mViewInfoStore.addToOldChangeHolders(l, paramViewHolder);
    }
    this.mViewInfoStore.addToPreLayout(paramViewHolder, paramItemHolderInfo);
  }

  void removeAndRecycleViews()
  {
    if (this.mItemAnimator != null)
      this.mItemAnimator.endAnimations();
    if (this.mLayout != null)
    {
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    }
    this.mRecycler.clear();
  }

  boolean removeAnimatingView(View paramView)
  {
    eatRequestLayout();
    boolean bool2 = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool2)
    {
      paramView = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(paramView);
      this.mRecycler.recycleViewHolderInternal(paramView);
    }
    if (!bool2);
    for (boolean bool1 = true; ; bool1 = false)
    {
      resumeRequestLayout(bool1);
      return bool2;
    }
  }

  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null)
    {
      if (!localViewHolder.isTmpDetached())
        break label32;
      localViewHolder.clearTmpDetachFlag();
    }
    label32: 
    do
    {
      dispatchChildDetached(paramView);
      super.removeDetachedView(paramView, paramBoolean);
      return;
    }
    while (localViewHolder.shouldIgnore());
    throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + localViewHolder);
  }

  public void removeItemDecoration(ItemDecoration paramItemDecoration)
  {
    if (this.mLayout != null)
      this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
    this.mItemDecorations.remove(paramItemDecoration);
    if (this.mItemDecorations.isEmpty())
      if (getOverScrollMode() != 2)
        break label60;
    label60: for (boolean bool = true; ; bool = false)
    {
      setWillNotDraw(bool);
      markItemDecorInsetsDirty();
      requestLayout();
      return;
    }
  }

  public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null)
      return;
    this.mOnChildAttachStateListeners.remove(paramOnChildAttachStateChangeListener);
  }

  public void removeOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.remove(paramOnItemTouchListener);
    if (this.mActiveOnItemTouchListener == paramOnItemTouchListener)
      this.mActiveOnItemTouchListener = null;
  }

  public void removeOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners != null)
      this.mScrollListeners.remove(paramOnScrollListener);
  }

  void repositionShadowingViews()
  {
    int j = this.mChildHelper.getChildCount();
    int i = 0;
    while (i < j)
    {
      View localView = this.mChildHelper.getChildAt(i);
      Object localObject = getChildViewHolder(localView);
      if ((localObject != null) && (((ViewHolder)localObject).mShadowingHolder != null))
      {
        localObject = ((ViewHolder)localObject).mShadowingHolder.itemView;
        int k = localView.getLeft();
        int m = localView.getTop();
        if ((k != ((View)localObject).getLeft()) || (m != ((View)localObject).getTop()))
          ((View)localObject).layout(k, m, ((View)localObject).getWidth() + k, ((View)localObject).getHeight() + m);
      }
      i += 1;
    }
  }

  public void requestChildFocus(View paramView1, View paramView2)
  {
    Object localObject;
    if ((!this.mLayout.onRequestChildFocus(this, this.mState, paramView1, paramView2)) && (paramView2 != null))
    {
      this.mTempRect.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
      localObject = paramView2.getLayoutParams();
      if ((localObject instanceof LayoutParams))
      {
        localObject = (LayoutParams)localObject;
        if (!((LayoutParams)localObject).mInsetsDirty)
        {
          localObject = ((LayoutParams)localObject).mDecorInsets;
          Rect localRect = this.mTempRect;
          localRect.left -= ((Rect)localObject).left;
          localRect = this.mTempRect;
          localRect.right += ((Rect)localObject).right;
          localRect = this.mTempRect;
          localRect.top -= ((Rect)localObject).top;
          localRect = this.mTempRect;
          int i = localRect.bottom;
          localRect.bottom = (((Rect)localObject).bottom + i);
        }
      }
      offsetDescendantRectToMyCoords(paramView2, this.mTempRect);
      offsetRectIntoDescendantCoords(paramView1, this.mTempRect);
      localObject = this.mTempRect;
      if (this.mFirstLayoutComplete)
        break label215;
    }
    label215: for (boolean bool = true; ; bool = false)
    {
      requestChildRectangleOnScreen(paramView1, (Rect)localObject, bool);
      super.requestChildFocus(paramView1, paramView2);
      return;
    }
  }

  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    return this.mLayout.requestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean);
  }

  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    int j = this.mOnItemTouchListeners.size();
    int i = 0;
    while (i < j)
    {
      ((OnItemTouchListener)this.mOnItemTouchListeners.get(i)).onRequestDisallowInterceptTouchEvent(paramBoolean);
      i += 1;
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }

  public void requestLayout()
  {
    if ((this.mEatRequestLayout == 0) && (!this.mLayoutFrozen))
    {
      super.requestLayout();
      return;
    }
    this.mLayoutRequestEaten = true;
  }

  void resumeRequestLayout(boolean paramBoolean)
  {
    if (this.mEatRequestLayout < 1)
      this.mEatRequestLayout = 1;
    if (!paramBoolean)
      this.mLayoutRequestEaten = false;
    if (this.mEatRequestLayout == 1)
    {
      if ((paramBoolean) && (this.mLayoutRequestEaten) && (!this.mLayoutFrozen) && (this.mLayout != null) && (this.mAdapter != null))
        dispatchLayout();
      if (!this.mLayoutFrozen)
        this.mLayoutRequestEaten = false;
    }
    this.mEatRequestLayout -= 1;
  }

  void saveOldPositions()
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if (!localViewHolder.shouldIgnore())
        localViewHolder.saveOldPosition();
      i += 1;
    }
  }

  public void scrollBy(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null);
    boolean bool1;
    boolean bool2;
    do
    {
      Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      do
        return;
      while (this.mLayoutFrozen);
      bool1 = this.mLayout.canScrollHorizontally();
      bool2 = this.mLayout.canScrollVertically();
    }
    while ((!bool1) && (!bool2));
    if (bool1)
      if (!bool2)
        break label73;
    while (true)
    {
      scrollByInternal(paramInt1, paramInt2, null);
      return;
      paramInt1 = 0;
      break;
      label73: paramInt2 = 0;
    }
  }

  boolean scrollByInternal(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    int i2 = 0;
    consumePendingUpdateOperations();
    int j;
    int i;
    int k;
    int m;
    if (this.mAdapter != null)
    {
      eatRequestLayout();
      onEnterLayoutOrScroll();
      j.a("RV Scroll");
      if (paramInt1 != 0)
      {
        j = this.mLayout.scrollHorizontallyBy(paramInt1, this.mRecycler, this.mState);
        i = paramInt1 - j;
        if (paramInt2 != 0)
        {
          k = this.mLayout.scrollVerticallyBy(paramInt2, this.mRecycler, this.mState);
          m = paramInt2 - k;
          label83: j.a();
          repositionShadowingViews();
          onExitLayoutOrScroll();
          resumeRequestLayout(false);
          int i1 = k;
          k = i;
          i = i1;
        }
      }
    }
    while (true)
    {
      if (!this.mItemDecorations.isEmpty())
        invalidate();
      if (dispatchNestedScroll(j, i, k, m, this.mScrollOffset))
      {
        this.mLastTouchX -= this.mScrollOffset[0];
        this.mLastTouchY -= this.mScrollOffset[1];
        if (paramMotionEvent != null)
          paramMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[0] += this.mScrollOffset[0];
        paramMotionEvent = this.mNestedOffsets;
        paramMotionEvent[1] += this.mScrollOffset[1];
      }
      while (true)
      {
        if ((j != 0) || (i != 0))
          dispatchOnScrolled(j, i);
        if (!awakenScrollBars())
          invalidate();
        if ((j != 0) || (i != 0))
          i2 = 1;
        return i2;
        if (getOverScrollMode() == 2)
          continue;
        if (paramMotionEvent != null)
          pullGlows(paramMotionEvent.getX(), k, paramMotionEvent.getY(), m);
        considerReleasingGlowsOnScroll(paramInt1, paramInt2);
      }
      k = 0;
      int n = 0;
      break label83;
      j = 0;
      i = 0;
      break;
      i = 0;
      j = 0;
      n = 0;
      k = 0;
    }
  }

  public void scrollTo(int paramInt1, int paramInt2)
  {
    Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
  }

  public void scrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen)
      return;
    stopScroll();
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }

  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    if (shouldDeferAccessibilityEvent(paramAccessibilityEvent))
      return;
    super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
  }

  public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate)
  {
    this.mAccessibilityDelegate = paramRecyclerViewAccessibilityDelegate;
    ag.a(this, this.mAccessibilityDelegate);
  }

  public void setAdapter(Adapter paramAdapter)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, false, true);
    requestLayout();
  }

  public void setChildDrawingOrderCallback(ChildDrawingOrderCallback paramChildDrawingOrderCallback)
  {
    if (paramChildDrawingOrderCallback == this.mChildDrawingOrderCallback)
      return;
    this.mChildDrawingOrderCallback = paramChildDrawingOrderCallback;
    if (this.mChildDrawingOrderCallback != null);
    for (boolean bool = true; ; bool = false)
    {
      setChildrenDrawingOrderEnabled(bool);
      return;
    }
  }

  boolean setChildImportantForAccessibilityInternal(ViewHolder paramViewHolder, int paramInt)
  {
    if (isComputingLayout())
    {
      paramViewHolder.mPendingAccessibilityState = paramInt;
      this.mPendingAccessibilityImportanceChange.add(paramViewHolder);
      return false;
    }
    ag.c(paramViewHolder.itemView, paramInt);
    return true;
  }

  public void setClipToPadding(boolean paramBoolean)
  {
    if (paramBoolean != this.mClipToPadding)
      invalidateGlows();
    this.mClipToPadding = paramBoolean;
    super.setClipToPadding(paramBoolean);
    if (this.mFirstLayoutComplete)
      requestLayout();
  }

  void setDataSetChangedAfterLayout()
  {
    if (this.mDataSetHasChangedAfterLayout)
      return;
    this.mDataSetHasChangedAfterLayout = true;
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (i < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(i));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()))
        localViewHolder.addFlags(512);
      i += 1;
    }
    this.mRecycler.setAdapterPositionsAsUnknown();
    markKnownViewsInvalid();
  }

  public void setGlowColor(int paramInt)
  {
    this.glowColor = paramInt;
  }

  public void setHasFixedSize(boolean paramBoolean)
  {
    this.mHasFixedSize = paramBoolean;
  }

  public void setItemAnimator(ItemAnimator paramItemAnimator)
  {
    if (this.mItemAnimator != null)
    {
      this.mItemAnimator.endAnimations();
      this.mItemAnimator.setListener(null);
    }
    this.mItemAnimator = paramItemAnimator;
    if (this.mItemAnimator != null)
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
  }

  public void setItemViewCacheSize(int paramInt)
  {
    this.mRecycler.setViewCacheSize(paramInt);
  }

  public void setLayoutFrozen(boolean paramBoolean)
  {
    if (paramBoolean != this.mLayoutFrozen)
    {
      assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
      if (!paramBoolean)
      {
        this.mLayoutFrozen = false;
        if ((this.mLayoutRequestEaten) && (this.mLayout != null) && (this.mAdapter != null))
          requestLayout();
        this.mLayoutRequestEaten = false;
      }
    }
    else
    {
      return;
    }
    long l = SystemClock.uptimeMillis();
    onTouchEvent(MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0));
    this.mLayoutFrozen = true;
    this.mIgnoreMotionEventTillDown = true;
    stopScroll();
  }

  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager == this.mLayout)
      return;
    stopScroll();
    if (this.mLayout != null)
    {
      if (this.mItemAnimator != null)
        this.mItemAnimator.endAnimations();
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
      this.mRecycler.clear();
      if (this.mIsAttached)
        this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
      this.mLayout.setRecyclerView(null);
      this.mLayout = null;
    }
    while (true)
    {
      this.mChildHelper.removeAllViewsUnfiltered();
      this.mLayout = paramLayoutManager;
      if (paramLayoutManager == null)
        break;
      if (paramLayoutManager.mRecyclerView != null)
      {
        throw new IllegalArgumentException("LayoutManager " + paramLayoutManager + " is already attached to a RecyclerView: " + paramLayoutManager.mRecyclerView);
        this.mRecycler.clear();
        continue;
      }
      this.mLayout.setRecyclerView(this);
      if (!this.mIsAttached)
        break;
      this.mLayout.dispatchAttachedToWindow(this);
    }
    this.mRecycler.updateViewCacheSize();
    requestLayout();
  }

  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    getScrollingChildHelper().a(paramBoolean);
  }

  public void setOnFlingListener(OnFlingListener paramOnFlingListener)
  {
    this.mOnFlingListener = paramOnFlingListener;
  }

  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mScrollListener = paramOnScrollListener;
  }

  public void setPreserveFocusAfterLayout(boolean paramBoolean)
  {
    this.mPreserveFocusAfterLayout = paramBoolean;
  }

  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool)
  {
    this.mRecycler.setRecycledViewPool(paramRecycledViewPool);
  }

  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    this.mRecyclerListener = paramRecyclerListener;
  }

  void setScrollState(int paramInt)
  {
    if (paramInt == this.mScrollState)
      return;
    this.mScrollState = paramInt;
    if (paramInt != 2)
      stopScrollersInternal();
    dispatchOnScrollStateChanged(paramInt);
  }

  public void setScrollingTouchSlop(int paramInt)
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt)
    {
    default:
      Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + paramInt + "; using default value");
    case 0:
      this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
      return;
    case 1:
    }
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
  }

  public void setTopGlowOffset(int paramInt)
  {
    this.topGlowOffset = paramInt;
  }

  public void setViewCacheExtension(ViewCacheExtension paramViewCacheExtension)
  {
    this.mRecycler.setViewCacheExtension(paramViewCacheExtension);
  }

  boolean shouldDeferAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    int k = 0;
    int j = 0;
    if (isComputingLayout())
      if (paramAccessibilityEvent == null)
        break label46;
    label46: for (int i = android.support.v4.view.a.a.b(paramAccessibilityEvent); ; i = 0)
    {
      if (i == 0)
        i = j;
      while (true)
      {
        this.mEatenAccessibilityChangeFlags = (i | this.mEatenAccessibilityChangeFlags);
        k = 1;
        return k;
      }
    }
  }

  public void smoothScrollBy(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1, paramInt2, null);
  }

  public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
  {
    int i = 0;
    if (this.mLayout == null)
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    while (true)
    {
      return;
      if (this.mLayoutFrozen)
        continue;
      if (!this.mLayout.canScrollHorizontally())
        paramInt1 = 0;
      if (!this.mLayout.canScrollVertically())
        paramInt2 = i;
      while ((paramInt1 != 0) || (paramInt2 != 0))
      {
        this.mViewFlinger.smoothScrollBy(paramInt1, paramInt2, paramInterpolator);
        return;
      }
    }
  }

  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen)
      return;
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.smoothScrollToPosition(this, this.mState, paramInt);
  }

  public boolean startNestedScroll(int paramInt)
  {
    return getScrollingChildHelper().a(paramInt);
  }

  public void stopNestedScroll()
  {
    getScrollingChildHelper().c();
  }

  public void stopScroll()
  {
    setScrollState(0);
    stopScrollersInternal();
  }

  public void swapAdapter(Adapter paramAdapter, boolean paramBoolean)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, true, paramBoolean);
    setDataSetChangedAfterLayout();
    requestLayout();
  }

  void viewRangeUpdate(int paramInt1, int paramInt2, Object paramObject)
  {
    int j = this.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    if (i < j)
    {
      View localView = this.mChildHelper.getUnfilteredChildAt(i);
      ViewHolder localViewHolder = getChildViewHolderInt(localView);
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore()));
      while (true)
      {
        i += 1;
        break;
        if ((localViewHolder.mPosition < paramInt1) || (localViewHolder.mPosition >= paramInt1 + paramInt2))
          continue;
        localViewHolder.addFlags(2);
        localViewHolder.addChangePayload(paramObject);
        ((LayoutParams)localView.getLayoutParams()).mInsetsDirty = true;
      }
    }
    this.mRecycler.viewRangeUpdate(paramInt1, paramInt2);
  }

  public static abstract class Adapter<VH extends RecyclerView.ViewHolder>
  {
    private boolean mHasStableIds = false;
    private final RecyclerView.AdapterDataObservable mObservable = new RecyclerView.AdapterDataObservable();

    public final void bindViewHolder(VH paramVH, int paramInt)
    {
      paramVH.mPosition = paramInt;
      if (hasStableIds())
        paramVH.mItemId = getItemId(paramInt);
      paramVH.setFlags(1, 519);
      j.a("RV OnBindView");
      onBindViewHolder(paramVH, paramInt, paramVH.getUnmodifiedPayloads());
      paramVH.clearPayload();
      paramVH = paramVH.itemView.getLayoutParams();
      if ((paramVH instanceof RecyclerView.LayoutParams))
        ((RecyclerView.LayoutParams)paramVH).mInsetsDirty = true;
      j.a();
    }

    public final VH createViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      j.a("RV CreateView");
      paramViewGroup = onCreateViewHolder(paramViewGroup, paramInt);
      paramViewGroup.mItemViewType = paramInt;
      j.a();
      return paramViewGroup;
    }

    public abstract int getItemCount();

    public long getItemId(int paramInt)
    {
      return -1L;
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public final boolean hasObservers()
    {
      return this.mObservable.hasObservers();
    }

    public final boolean hasStableIds()
    {
      return this.mHasStableIds;
    }

    public void notifyDataSetChanged()
    {
      this.mObservable.notifyChanged();
    }

    public void notifyItemChanged(int paramInt)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1);
    }

    public void notifyItemChanged(int paramInt, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1, paramObject);
    }

    public void notifyItemInserted(int paramInt)
    {
      this.mObservable.notifyItemRangeInserted(paramInt, 1);
    }

    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemMoved(paramInt1, paramInt2);
    }

    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2);
    }

    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2, paramObject);
    }

    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeInserted(paramInt1, paramInt2);
    }

    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt1, paramInt2);
    }

    public void notifyItemRemoved(int paramInt)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt, 1);
    }

    public void onAttachedToRecyclerView(RecyclerView paramRecyclerView)
    {
    }

    public abstract void onBindViewHolder(VH paramVH, int paramInt);

    public void onBindViewHolder(VH paramVH, int paramInt, List<Object> paramList)
    {
      onBindViewHolder(paramVH, paramInt);
    }

    public abstract VH onCreateViewHolder(ViewGroup paramViewGroup, int paramInt);

    public void onDetachedFromRecyclerView(RecyclerView paramRecyclerView)
    {
    }

    public boolean onFailedToRecycleView(VH paramVH)
    {
      return false;
    }

    public void onViewAttachedToWindow(VH paramVH)
    {
    }

    public void onViewDetachedFromWindow(VH paramVH)
    {
    }

    public void onViewRecycled(VH paramVH)
    {
    }

    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.registerObserver(paramAdapterDataObserver);
    }

    public void setHasStableIds(boolean paramBoolean)
    {
      if (hasObservers())
        throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
      this.mHasStableIds = paramBoolean;
    }

    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.unregisterObserver(paramAdapterDataObserver);
    }
  }

  static class AdapterDataObservable extends Observable<RecyclerView.AdapterDataObserver>
  {
    public boolean hasObservers()
    {
      return !this.mObservers.isEmpty();
    }

    public void notifyChanged()
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged();
        i -= 1;
      }
    }

    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(paramInt1, paramInt2, 1);
        i -= 1;
      }
    }

    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      notifyItemRangeChanged(paramInt1, paramInt2, null);
    }

    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(paramInt1, paramInt2, paramObject);
        i -= 1;
      }
    }

    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(paramInt1, paramInt2);
        i -= 1;
      }
    }

    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      int i = this.mObservers.size() - 1;
      while (i >= 0)
      {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(paramInt1, paramInt2);
        i -= 1;
      }
    }
  }

  public static abstract class AdapterDataObserver
  {
    public void onChanged()
    {
    }

    public void onItemRangeChanged(int paramInt1, int paramInt2)
    {
    }

    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      onItemRangeChanged(paramInt1, paramInt2);
    }

    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
    }

    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
    }
  }

  public static abstract interface ChildDrawingOrderCallback
  {
    public abstract int onGetChildDrawingOrder(int paramInt1, int paramInt2);
  }

  public static abstract class ItemAnimator
  {
    public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    public static final int FLAG_CHANGED = 2;
    public static final int FLAG_INVALIDATED = 4;
    public static final int FLAG_MOVED = 2048;
    public static final int FLAG_REMOVED = 8;
    private long mAddDuration = 120L;
    private long mChangeDuration = 250L;
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    private ItemAnimatorListener mListener = null;
    private long mMoveDuration = 250L;
    private long mRemoveDuration = 120L;

    static int buildAdapterChangeFlagsForAnimations(RecyclerView.ViewHolder paramViewHolder)
    {
      int j = RecyclerView.ViewHolder.access$1400(paramViewHolder) & 0xE;
      int i;
      if (paramViewHolder.isInvalid())
        i = 4;
      int k;
      int m;
      do
      {
        do
        {
          do
          {
            do
            {
              return i;
              i = j;
            }
            while ((j & 0x4) != 0);
            k = paramViewHolder.getOldPosition();
            m = paramViewHolder.getAdapterPosition();
            i = j;
          }
          while (k == -1);
          i = j;
        }
        while (m == -1);
        i = j;
      }
      while (k == m);
      return j | 0x800;
    }

    public abstract boolean animateAppearance(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);

    public abstract boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);

    public abstract boolean animateDisappearance(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);

    public abstract boolean animatePersistence(RecyclerView.ViewHolder paramViewHolder, ItemHolderInfo paramItemHolderInfo1, ItemHolderInfo paramItemHolderInfo2);

    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder, List<Object> paramList)
    {
      return canReuseUpdatedViewHolder(paramViewHolder);
    }

    public final void dispatchAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationFinished(paramViewHolder);
      if (this.mListener != null)
        this.mListener.onAnimationFinished(paramViewHolder);
    }

    public final void dispatchAnimationStarted(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationStarted(paramViewHolder);
    }

    public final void dispatchAnimationsFinished()
    {
      int j = this.mFinishedListeners.size();
      int i = 0;
      while (i < j)
      {
        ((ItemAnimatorFinishedListener)this.mFinishedListeners.get(i)).onAnimationsFinished();
        i += 1;
      }
      this.mFinishedListeners.clear();
    }

    public abstract void endAnimation(RecyclerView.ViewHolder paramViewHolder);

    public abstract void endAnimations();

    public long getAddDuration()
    {
      return this.mAddDuration;
    }

    public long getChangeDuration()
    {
      return this.mChangeDuration;
    }

    public long getMoveDuration()
    {
      return this.mMoveDuration;
    }

    public long getRemoveDuration()
    {
      return this.mRemoveDuration;
    }

    public abstract boolean isRunning();

    public final boolean isRunning(ItemAnimatorFinishedListener paramItemAnimatorFinishedListener)
    {
      boolean bool = isRunning();
      if (paramItemAnimatorFinishedListener != null)
      {
        if (!bool)
          paramItemAnimatorFinishedListener.onAnimationsFinished();
      }
      else
        return bool;
      this.mFinishedListeners.add(paramItemAnimatorFinishedListener);
      return bool;
    }

    public ItemHolderInfo obtainHolderInfo()
    {
      return new ItemHolderInfo();
    }

    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
    }

    public void onAnimationStarted(RecyclerView.ViewHolder paramViewHolder)
    {
    }

    public ItemHolderInfo recordPostLayoutInformation(RecyclerView.State paramState, RecyclerView.ViewHolder paramViewHolder)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }

    public ItemHolderInfo recordPreLayoutInformation(RecyclerView.State paramState, RecyclerView.ViewHolder paramViewHolder, int paramInt, List<Object> paramList)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }

    public abstract void runPendingAnimations();

    public void setAddDuration(long paramLong)
    {
      this.mAddDuration = paramLong;
    }

    public void setChangeDuration(long paramLong)
    {
      this.mChangeDuration = paramLong;
    }

    void setListener(ItemAnimatorListener paramItemAnimatorListener)
    {
      this.mListener = paramItemAnimatorListener;
    }

    public void setMoveDuration(long paramLong)
    {
      this.mMoveDuration = paramLong;
    }

    public void setRemoveDuration(long paramLong)
    {
      this.mRemoveDuration = paramLong;
    }

    @Retention(RetentionPolicy.SOURCE)
    public static @interface AdapterChanges
    {
    }

    public static abstract interface ItemAnimatorFinishedListener
    {
      public abstract void onAnimationsFinished();
    }

    static abstract interface ItemAnimatorListener
    {
      public abstract void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder);
    }

    public static class ItemHolderInfo
    {
      public int bottom;
      public int changeFlags;
      public int left;
      public int right;
      public int top;

      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder)
      {
        return setFrom(paramViewHolder, 0);
      }

      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        paramViewHolder = paramViewHolder.itemView;
        this.left = paramViewHolder.getLeft();
        this.top = paramViewHolder.getTop();
        this.right = paramViewHolder.getRight();
        this.bottom = paramViewHolder.getBottom();
        return this;
      }
    }
  }

  private class ItemAnimatorRestoreListener
    implements RecyclerView.ItemAnimator.ItemAnimatorListener
  {
    ItemAnimatorRestoreListener()
    {
    }

    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((paramViewHolder.mShadowedHolder != null) && (paramViewHolder.mShadowingHolder == null))
        paramViewHolder.mShadowedHolder = null;
      paramViewHolder.mShadowingHolder = null;
      if ((!RecyclerView.ViewHolder.access$1300(paramViewHolder)) && (!RecyclerView.this.removeAnimatingView(paramViewHolder.itemView)) && (paramViewHolder.isTmpDetached()))
        RecyclerView.this.removeDetachedView(paramViewHolder.itemView, false);
    }
  }

  public static abstract class ItemDecoration
  {
    @Deprecated
    public void getItemOffsets(Rect paramRect, int paramInt, RecyclerView paramRecyclerView)
    {
      paramRect.set(0, 0, 0, 0);
    }

    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      getItemOffsets(paramRect, ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition(), paramRecyclerView);
    }

    @Deprecated
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView)
    {
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDraw(paramCanvas, paramRecyclerView);
    }

    @Deprecated
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView)
    {
    }

    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDrawOver(paramCanvas, paramRecyclerView);
    }
  }

  public static abstract class LayoutManager
  {
    boolean mAutoMeasure = false;
    ChildHelper mChildHelper;
    private int mHeight;
    private int mHeightMode;
    boolean mIsAttachedToWindow = false;
    private boolean mItemPrefetchEnabled = true;
    private boolean mMeasurementCacheEnabled = true;
    int mPrefetchMaxCountObserved;
    boolean mPrefetchMaxObservedInInitialPrefetch;
    RecyclerView mRecyclerView;
    boolean mRequestedSimpleAnimations = false;
    RecyclerView.SmoothScroller mSmoothScroller;
    private int mWidth;
    private int mWidthMode;

    private void addViewInt(View paramView, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.LayoutParams localLayoutParams;
      if ((paramBoolean) || (localViewHolder.isRemoved()))
      {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
        localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
        if ((!localViewHolder.wasReturnedFromScrap()) && (!localViewHolder.isScrap()))
          break label128;
        if (!localViewHolder.isScrap())
          break label120;
        localViewHolder.unScrap();
        label68: this.mChildHelper.attachViewToParent(paramView, paramInt, paramView.getLayoutParams(), false);
      }
      while (true)
      {
        if (localLayoutParams.mPendingInvalidate)
        {
          localViewHolder.itemView.invalidate();
          localLayoutParams.mPendingInvalidate = false;
        }
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
        break;
        label120: localViewHolder.clearReturnedFromScrapFlag();
        break label68;
        label128: if (paramView.getParent() == this.mRecyclerView)
        {
          int j = this.mChildHelper.indexOfChild(paramView);
          int i = paramInt;
          if (paramInt == -1)
            i = this.mChildHelper.getChildCount();
          if (j == -1)
            throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(paramView));
          if (j == i)
            continue;
          this.mRecyclerView.mLayout.moveView(j, i);
          continue;
        }
        this.mChildHelper.addView(paramView, paramInt, false);
        localLayoutParams.mInsetsDirty = true;
        if ((this.mSmoothScroller == null) || (!this.mSmoothScroller.isRunning()))
          continue;
        this.mSmoothScroller.onChildAttachedToWindow(paramView);
      }
    }

    public static int chooseSize(int paramInt1, int paramInt2, int paramInt3)
    {
      int j = View.MeasureSpec.getMode(paramInt1);
      int i = View.MeasureSpec.getSize(paramInt1);
      paramInt1 = i;
      switch (j)
      {
      default:
        paramInt1 = Math.max(paramInt2, paramInt3);
      case 1073741824:
        return paramInt1;
      case -2147483648:
      }
      return Math.min(i, Math.max(paramInt2, paramInt3));
    }

    private void detachViewInternal(int paramInt, View paramView)
    {
      this.mChildHelper.detachViewFromParent(paramInt);
    }

    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int j = 0;
      int k = 0;
      int i = Math.max(0, paramInt1 - paramInt3);
      if (paramBoolean)
        if (paramInt4 >= 0)
        {
          paramInt1 = 1073741824;
          paramInt3 = paramInt4;
        }
      while (true)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt3, paramInt1);
        if (paramInt4 == -1)
        {
          switch (paramInt2)
          {
          default:
            paramInt2 = 0;
            paramInt1 = j;
          case 1073741824:
          case -2147483648:
          case 0:
          }
          while (true)
          {
            paramInt3 = paramInt1;
            paramInt1 = paramInt2;
            break;
            paramInt1 = i;
            continue;
            paramInt2 = 0;
            paramInt1 = j;
          }
        }
        if (paramInt4 == -2)
        {
          paramInt3 = 0;
          paramInt1 = k;
          continue;
          if (paramInt4 >= 0)
          {
            paramInt1 = 1073741824;
            paramInt3 = paramInt4;
            continue;
          }
          if (paramInt4 == -1)
          {
            paramInt1 = paramInt2;
            paramInt3 = i;
            continue;
          }
          if (paramInt4 == -2)
          {
            if (paramInt2 != -2147483648)
            {
              paramInt1 = k;
              paramInt3 = i;
              if (paramInt2 != 1073741824)
                continue;
            }
            paramInt1 = -2147483648;
            paramInt3 = i;
            continue;
          }
        }
        paramInt3 = 0;
        paramInt1 = k;
      }
    }

    @Deprecated
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int j = 1073741824;
      int i = Math.max(0, paramInt1 - paramInt2);
      if (paramBoolean)
        if (paramInt3 >= 0)
        {
          paramInt1 = paramInt3;
          paramInt2 = j;
        }
      while (true)
      {
        return View.MeasureSpec.makeMeasureSpec(paramInt1, paramInt2);
        paramInt2 = 0;
        paramInt1 = 0;
        continue;
        paramInt2 = j;
        paramInt1 = paramInt3;
        if (paramInt3 >= 0)
          continue;
        if (paramInt3 == -1)
        {
          paramInt1 = i;
          paramInt2 = j;
          continue;
        }
        if (paramInt3 == -2)
        {
          paramInt2 = -2147483648;
          paramInt1 = i;
          continue;
        }
        paramInt2 = 0;
        paramInt1 = 0;
      }
    }

    private static boolean isMeasurementUpToDate(int paramInt1, int paramInt2, int paramInt3)
    {
      int k = 1;
      int i = View.MeasureSpec.getMode(paramInt2);
      paramInt2 = View.MeasureSpec.getSize(paramInt2);
      int j;
      if ((paramInt3 > 0) && (paramInt1 != paramInt3))
        j = 0;
      do
      {
        do
        {
          return j;
          j = k;
          switch (i)
          {
          case 0:
          default:
            return false;
          case -2147483648:
            j = k;
          case 1073741824:
          }
        }
        while (paramInt2 >= paramInt1);
        return false;
        j = k;
      }
      while (paramInt2 == paramInt1);
      return false;
    }

    private void onSmoothScrollerStopped(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if (this.mSmoothScroller == paramSmoothScroller)
        this.mSmoothScroller = null;
    }

    private void scrapOrRecycleView(RecyclerView.Recycler paramRecycler, int paramInt, View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.shouldIgnore())
        return;
      if ((localViewHolder.isInvalid()) && (!localViewHolder.isRemoved()) && (!this.mRecyclerView.mAdapter.hasStableIds()))
      {
        removeViewAt(paramInt);
        paramRecycler.recycleViewHolderInternal(localViewHolder);
        return;
      }
      detachViewAt(paramInt);
      paramRecycler.scrapView(paramView);
      this.mRecyclerView.mViewInfoStore.onViewDetached(localViewHolder);
    }

    public void addDisappearingView(View paramView)
    {
      addDisappearingView(paramView, -1);
    }

    public void addDisappearingView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, true);
    }

    public void addView(View paramView)
    {
      addView(paramView, -1);
    }

    public void addView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, false);
    }

    public void assertInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null)
        this.mRecyclerView.assertInLayoutOrScroll(paramString);
    }

    public void assertNotInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null)
        this.mRecyclerView.assertNotInLayoutOrScroll(paramString);
    }

    public void attachView(View paramView)
    {
      attachView(paramView, -1);
    }

    public void attachView(View paramView, int paramInt)
    {
      attachView(paramView, paramInt, (RecyclerView.LayoutParams)paramView.getLayoutParams());
    }

    public void attachView(View paramView, int paramInt, RecyclerView.LayoutParams paramLayoutParams)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isRemoved())
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
      while (true)
      {
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramLayoutParams, localViewHolder.isRemoved());
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
      }
    }

    public void calculateItemDecorationsForChild(View paramView, Rect paramRect)
    {
      if (this.mRecyclerView == null)
      {
        paramRect.set(0, 0, 0, 0);
        return;
      }
      paramRect.set(this.mRecyclerView.getItemDecorInsetsForChild(paramView));
    }

    public boolean canScrollHorizontally()
    {
      return false;
    }

    public boolean canScrollVertically()
    {
      return false;
    }

    public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      return paramLayoutParams != null;
    }

    public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
    {
    }

    public void collectInitialPrefetchPositions(int paramInt, LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
    {
    }

    public int computeHorizontalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }

    public int computeHorizontalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }

    public int computeHorizontalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }

    public int computeVerticalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }

    public int computeVerticalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }

    public int computeVerticalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }

    public void detachAndScrapAttachedViews(RecyclerView.Recycler paramRecycler)
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        scrapOrRecycleView(paramRecycler, i, getChildAt(i));
        i -= 1;
      }
    }

    public void detachAndScrapView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, this.mChildHelper.indexOfChild(paramView), paramView);
    }

    public void detachAndScrapViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, paramInt, getChildAt(paramInt));
    }

    public void detachView(View paramView)
    {
      int i = this.mChildHelper.indexOfChild(paramView);
      if (i >= 0)
        detachViewInternal(i, paramView);
    }

    public void detachViewAt(int paramInt)
    {
      detachViewInternal(paramInt, getChildAt(paramInt));
    }

    void dispatchAttachedToWindow(RecyclerView paramRecyclerView)
    {
      this.mIsAttachedToWindow = true;
      onAttachedToWindow(paramRecyclerView);
    }

    void dispatchDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      this.mIsAttachedToWindow = false;
      onDetachedFromWindow(paramRecyclerView, paramRecycler);
    }

    public void endAnimation(View paramView)
    {
      if (this.mRecyclerView.mItemAnimator != null)
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(paramView));
    }

    public View findContainingItemView(View paramView)
    {
      if (this.mRecyclerView == null);
      do
      {
        return null;
        paramView = this.mRecyclerView.findContainingItemView(paramView);
      }
      while ((paramView == null) || (this.mChildHelper.isHidden(paramView)));
      return paramView;
    }

    public View findViewByPosition(int paramInt)
    {
      int j = getChildCount();
      int i = 0;
      if (i < j)
      {
        View localView = getChildAt(i);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder == null);
        do
        {
          i += 1;
          break;
        }
        while ((localViewHolder.getLayoutPosition() != paramInt) || (localViewHolder.shouldIgnore()) || ((!this.mRecyclerView.mState.isPreLayout()) && (localViewHolder.isRemoved())));
        return localView;
      }
      return null;
    }

    public abstract RecyclerView.LayoutParams generateDefaultLayoutParams();

    public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      return new RecyclerView.LayoutParams(paramContext, paramAttributeSet);
    }

    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      if ((paramLayoutParams instanceof RecyclerView.LayoutParams))
        return new RecyclerView.LayoutParams((RecyclerView.LayoutParams)paramLayoutParams);
      if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams))
        return new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
      return new RecyclerView.LayoutParams(paramLayoutParams);
    }

    public int getBaseline()
    {
      return -1;
    }

    public int getBottomDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.bottom;
    }

    public View getChildAt(int paramInt)
    {
      if (this.mChildHelper != null)
        return this.mChildHelper.getChildAt(paramInt);
      return null;
    }

    public int getChildCount()
    {
      if (this.mChildHelper != null)
        return this.mChildHelper.getChildCount();
      return 0;
    }

    public boolean getClipToPadding()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.mClipToPadding);
    }

    public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null));
      do
        return 1;
      while (!canScrollHorizontally());
      return this.mRecyclerView.mAdapter.getItemCount();
    }

    public int getDecoratedBottom(View paramView)
    {
      return paramView.getBottom() + getBottomDecorationHeight(paramView);
    }

    public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
    {
      RecyclerView.getDecoratedBoundsWithMarginsInt(paramView, paramRect);
    }

    public int getDecoratedLeft(View paramView)
    {
      return paramView.getLeft() - getLeftDecorationWidth(paramView);
    }

    public int getDecoratedMeasuredHeight(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      int i = paramView.getMeasuredHeight();
      int j = localRect.top;
      return localRect.bottom + (i + j);
    }

    public int getDecoratedMeasuredWidth(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      int i = paramView.getMeasuredWidth();
      int j = localRect.left;
      return localRect.right + (i + j);
    }

    public int getDecoratedRight(View paramView)
    {
      return paramView.getRight() + getRightDecorationWidth(paramView);
    }

    public int getDecoratedTop(View paramView)
    {
      return paramView.getTop() - getTopDecorationHeight(paramView);
    }

    public View getFocusedChild()
    {
      if (this.mRecyclerView == null);
      View localView;
      do
      {
        return null;
        localView = this.mRecyclerView.getFocusedChild();
      }
      while ((localView == null) || (this.mChildHelper.isHidden(localView)));
      return localView;
    }

    public int getHeight()
    {
      return this.mHeight;
    }

    public int getHeightMode()
    {
      return this.mHeightMode;
    }

    public int getItemCount()
    {
      if (this.mRecyclerView != null);
      for (RecyclerView.Adapter localAdapter = this.mRecyclerView.getAdapter(); localAdapter != null; localAdapter = null)
        return localAdapter.getItemCount();
      return 0;
    }

    public int getItemViewType(View paramView)
    {
      return RecyclerView.getChildViewHolderInt(paramView).getItemViewType();
    }

    public int getLayoutDirection()
    {
      return ag.f(this.mRecyclerView);
    }

    public int getLeftDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.left;
    }

    public int getMinimumHeight()
    {
      return ag.o(this.mRecyclerView);
    }

    public int getMinimumWidth()
    {
      return ag.n(this.mRecyclerView);
    }

    public int getPaddingBottom()
    {
      if (this.mRecyclerView != null)
        return this.mRecyclerView.getPaddingBottom();
      return 0;
    }

    public int getPaddingEnd()
    {
      if (this.mRecyclerView != null)
        return ag.j(this.mRecyclerView);
      return 0;
    }

    public int getPaddingLeft()
    {
      if (this.mRecyclerView != null)
        return this.mRecyclerView.getPaddingLeft();
      return 0;
    }

    public int getPaddingRight()
    {
      if (this.mRecyclerView != null)
        return this.mRecyclerView.getPaddingRight();
      return 0;
    }

    public int getPaddingStart()
    {
      if (this.mRecyclerView != null)
        return ag.i(this.mRecyclerView);
      return 0;
    }

    public int getPaddingTop()
    {
      if (this.mRecyclerView != null)
        return this.mRecyclerView.getPaddingTop();
      return 0;
    }

    public int getPosition(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition();
    }

    public int getRightDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.right;
    }

    public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null));
      do
        return 1;
      while (!canScrollVertically());
      return this.mRecyclerView.mAdapter.getItemCount();
    }

    public int getSelectionModeForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }

    public int getTopDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.top;
    }

    public void getTransformedBoundingBox(View paramView, boolean paramBoolean, Rect paramRect)
    {
      Object localObject;
      if (paramBoolean)
      {
        localObject = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
        int i = -((Rect)localObject).left;
        int j = -((Rect)localObject).top;
        int k = paramView.getWidth();
        int m = ((Rect)localObject).right;
        int n = paramView.getHeight();
        paramRect.set(i, j, k + m, ((Rect)localObject).bottom + n);
      }
      while (true)
      {
        if (this.mRecyclerView != null)
        {
          localObject = ag.m(paramView);
          if ((localObject != null) && (!((Matrix)localObject).isIdentity()))
          {
            RectF localRectF = this.mRecyclerView.mTempRectF;
            localRectF.set(paramRect);
            ((Matrix)localObject).mapRect(localRectF);
            paramRect.set((int)Math.floor(localRectF.left), (int)Math.floor(localRectF.top), (int)Math.ceil(localRectF.right), (int)Math.ceil(localRectF.bottom));
          }
        }
        paramRect.offset(paramView.getLeft(), paramView.getTop());
        return;
        paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
      }
    }

    public int getWidth()
    {
      return this.mWidth;
    }

    public int getWidthMode()
    {
      return this.mWidthMode;
    }

    boolean hasFlexibleChildInBothOrientations()
    {
      int m = 0;
      int j = getChildCount();
      int i = 0;
      while (true)
      {
        int k = m;
        if (i < j)
        {
          ViewGroup.LayoutParams localLayoutParams = getChildAt(i).getLayoutParams();
          if ((localLayoutParams.width < 0) && (localLayoutParams.height < 0))
            k = 1;
        }
        else
        {
          return k;
        }
        i += 1;
      }
    }

    public boolean hasFocus()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.hasFocus());
    }

    public void ignoreView(View paramView)
    {
      if ((paramView.getParent() != this.mRecyclerView) || (this.mRecyclerView.indexOfChild(paramView) == -1))
        throw new IllegalArgumentException("View should be fully attached to be ignored");
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.addFlags(128);
      this.mRecyclerView.mViewInfoStore.removeViewHolder(paramView);
    }

    public boolean isAttachedToWindow()
    {
      return this.mIsAttachedToWindow;
    }

    public boolean isAutoMeasureEnabled()
    {
      return this.mAutoMeasure;
    }

    public boolean isFocused()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.isFocused());
    }

    public final boolean isItemPrefetchEnabled()
    {
      return this.mItemPrefetchEnabled;
    }

    public boolean isLayoutHierarchical(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return false;
    }

    public boolean isMeasurementCacheEnabled()
    {
      return this.mMeasurementCacheEnabled;
    }

    public boolean isSmoothScrolling()
    {
      return (this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning());
    }

    public void layoutDecorated(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      paramView.layout(localRect.left + paramInt1, localRect.top + paramInt2, paramInt3 - localRect.right, paramInt4 - localRect.bottom);
    }

    public void layoutDecoratedWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = localLayoutParams.mDecorInsets;
      paramView.layout(localRect.left + paramInt1 + localLayoutParams.leftMargin, localRect.top + paramInt2 + localLayoutParams.topMargin, paramInt3 - localRect.right - localLayoutParams.rightMargin, paramInt4 - localRect.bottom - localLayoutParams.bottomMargin);
    }

    public void measureChild(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int k = localRect.left;
      int m = localRect.right;
      int i = localRect.top;
      int j = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), k + m + paramInt1 + (getPaddingLeft() + getPaddingRight()), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), j + i + paramInt2 + (getPaddingTop() + getPaddingBottom()), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
        paramView.measure(paramInt1, paramInt2);
    }

    public void measureChildWithMargins(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int k = localRect.left;
      int m = localRect.right;
      int i = localRect.top;
      int j = localRect.bottom;
      paramInt1 = getChildMeasureSpec(getWidth(), getWidthMode(), k + m + paramInt1 + (getPaddingLeft() + getPaddingRight() + localLayoutParams.leftMargin + localLayoutParams.rightMargin), localLayoutParams.width, canScrollHorizontally());
      paramInt2 = getChildMeasureSpec(getHeight(), getHeightMode(), j + i + paramInt2 + (getPaddingTop() + getPaddingBottom() + localLayoutParams.topMargin + localLayoutParams.bottomMargin), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
        paramView.measure(paramInt1, paramInt2);
    }

    public void moveView(int paramInt1, int paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView == null)
        throw new IllegalArgumentException("Cannot move a child from non-existing index:" + paramInt1);
      detachViewAt(paramInt1);
      attachView(localView, paramInt2);
    }

    public void offsetChildrenHorizontal(int paramInt)
    {
      if (this.mRecyclerView != null)
        this.mRecyclerView.offsetChildrenHorizontal(paramInt);
    }

    public void offsetChildrenVertical(int paramInt)
    {
      if (this.mRecyclerView != null)
        this.mRecyclerView.offsetChildrenVertical(paramInt);
    }

    public void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2)
    {
    }

    public boolean onAddFocusables(RecyclerView paramRecyclerView, ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
    {
      return false;
    }

    public void onAttachedToWindow(RecyclerView paramRecyclerView)
    {
    }

    @Deprecated
    public void onDetachedFromWindow(RecyclerView paramRecyclerView)
    {
    }

    public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      onDetachedFromWindow(paramRecyclerView);
    }

    public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return null;
    }

    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityEvent);
    }

    public void onInitializeAccessibilityEvent(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityEvent paramAccessibilityEvent)
    {
      boolean bool2 = true;
      paramRecycler = android.support.v4.view.a.a.a(paramAccessibilityEvent);
      if ((this.mRecyclerView == null) || (paramRecycler == null))
        return;
      boolean bool1 = bool2;
      if (!ag.b(this.mRecyclerView, 1))
      {
        bool1 = bool2;
        if (!ag.b(this.mRecyclerView, -1))
        {
          bool1 = bool2;
          if (!ag.a(this.mRecyclerView, -1))
            if (!ag.a(this.mRecyclerView, 1))
              break label111;
        }
      }
      label111: for (bool1 = bool2; ; bool1 = false)
      {
        paramRecycler.a(bool1);
        if (this.mRecyclerView.mAdapter == null)
          break;
        paramRecycler.a(this.mRecyclerView.mAdapter.getItemCount());
        return;
      }
    }

    void onInitializeAccessibilityNodeInfo(c paramc)
    {
      onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramc);
    }

    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, c paramc)
    {
      if ((ag.b(this.mRecyclerView, -1)) || (ag.a(this.mRecyclerView, -1)))
      {
        paramc.a(8192);
        paramc.a(true);
      }
      if ((ag.b(this.mRecyclerView, 1)) || (ag.a(this.mRecyclerView, 1)))
      {
        paramc.a(4096);
        paramc.a(true);
      }
      paramc.a(c.l.a(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
    }

    void onInitializeAccessibilityNodeInfoForItem(View paramView, c paramc)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder != null) && (!localViewHolder.isRemoved()) && (!this.mChildHelper.isHidden(localViewHolder.itemView)))
        onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramc);
    }

    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, c paramc)
    {
      int i;
      if (canScrollVertically())
      {
        i = getPosition(paramView);
        if (!canScrollHorizontally())
          break label51;
      }
      label51: for (int j = getPosition(paramView); ; j = 0)
      {
        paramc.b(c.m.a(i, 1, j, 1, false, false));
        return;
        i = 0;
        break;
      }
    }

    public View onInterceptFocusSearch(View paramView, int paramInt)
    {
      return null;
    }

    public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
    }

    public void onItemsChanged(RecyclerView paramRecyclerView)
    {
    }

    public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
    {
    }

    public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
    }

    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
    }

    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
    {
      onItemsUpdated(paramRecyclerView, paramInt1, paramInt2);
    }

    public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
    }

    public void onLayoutCompleted(RecyclerView.State paramState)
    {
    }

    public void onMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
    {
      this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
    }

    @Deprecated
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
    {
      return (isSmoothScrolling()) || (paramRecyclerView.isComputingLayout());
    }

    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, RecyclerView.State paramState, View paramView1, View paramView2)
    {
      return onRequestChildFocus(paramRecyclerView, paramView1, paramView2);
    }

    public void onRestoreInstanceState(Parcelable paramParcelable)
    {
    }

    public Parcelable onSaveInstanceState()
    {
      return null;
    }

    public void onScrollStateChanged(int paramInt)
    {
    }

    boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
    {
      return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramInt, paramBundle);
    }

    public boolean performAccessibilityAction(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, Bundle paramBundle)
    {
      if (this.mRecyclerView == null);
      int i;
      do
      {
        return false;
        switch (paramInt)
        {
        default:
          paramInt = 0;
          i = 0;
        case 8192:
        case 4096:
        }
      }
      while ((i == 0) && (paramInt == 0));
      this.mRecyclerView.scrollBy(paramInt, i);
      return true;
      if (ag.b(this.mRecyclerView, -1));
      for (paramInt = -(getHeight() - getPaddingTop() - getPaddingBottom()); ; paramInt = 0)
      {
        i = paramInt;
        int j;
        if (ag.a(this.mRecyclerView, -1))
        {
          j = -(getWidth() - getPaddingLeft() - getPaddingRight());
          i = paramInt;
          paramInt = j;
          break;
          if (!ag.b(this.mRecyclerView, 1))
            break label207;
        }
        label207: for (paramInt = getHeight() - getPaddingTop() - getPaddingBottom(); ; paramInt = 0)
        {
          i = paramInt;
          if (ag.a(this.mRecyclerView, 1))
          {
            j = getWidth();
            int k = getPaddingLeft();
            int m = getPaddingRight();
            i = paramInt;
            paramInt = j - k - m;
            break;
          }
          paramInt = 0;
          break;
        }
      }
    }

    boolean performAccessibilityActionForItem(View paramView, int paramInt, Bundle paramBundle)
    {
      return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramInt, paramBundle);
    }

    public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }

    public void postOnAnimation(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null)
        ag.a(this.mRecyclerView, paramRunnable);
    }

    public void removeAllViews()
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        this.mChildHelper.removeViewAt(i);
        i -= 1;
      }
    }

    public void removeAndRecycleAllViews(RecyclerView.Recycler paramRecycler)
    {
      int i = getChildCount() - 1;
      while (i >= 0)
      {
        if (!RecyclerView.getChildViewHolderInt(getChildAt(i)).shouldIgnore())
          removeAndRecycleViewAt(i, paramRecycler);
        i -= 1;
      }
    }

    void removeAndRecycleScrapInt(RecyclerView.Recycler paramRecycler)
    {
      int j = paramRecycler.getScrapCount();
      int i = j - 1;
      if (i >= 0)
      {
        View localView = paramRecycler.getScrapViewAt(i);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder.shouldIgnore());
        while (true)
        {
          i -= 1;
          break;
          localViewHolder.setIsRecyclable(false);
          if (localViewHolder.isTmpDetached())
            this.mRecyclerView.removeDetachedView(localView, false);
          if (this.mRecyclerView.mItemAnimator != null)
            this.mRecyclerView.mItemAnimator.endAnimation(localViewHolder);
          localViewHolder.setIsRecyclable(true);
          paramRecycler.quickRecycleScrapView(localView);
        }
      }
      paramRecycler.clearScrap();
      if (j > 0)
        this.mRecyclerView.invalidate();
    }

    public void removeAndRecycleView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      removeView(paramView);
      paramRecycler.recycleView(paramView);
    }

    public void removeAndRecycleViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      View localView = getChildAt(paramInt);
      removeViewAt(paramInt);
      paramRecycler.recycleView(localView);
    }

    public boolean removeCallbacks(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null)
        return this.mRecyclerView.removeCallbacks(paramRunnable);
      return false;
    }

    public void removeDetachedView(View paramView)
    {
      this.mRecyclerView.removeDetachedView(paramView, false);
    }

    public void removeView(View paramView)
    {
      this.mChildHelper.removeView(paramView);
    }

    public void removeViewAt(int paramInt)
    {
      if (getChildAt(paramInt) != null)
        this.mChildHelper.removeViewAt(paramInt);
    }

    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      int i2 = getPaddingLeft();
      int m = getPaddingTop();
      int i3 = getWidth() - getPaddingRight();
      int i1 = getHeight();
      int i6 = getPaddingBottom();
      int i4 = paramView.getLeft() + paramRect.left - paramView.getScrollX();
      int n = paramView.getTop() + paramRect.top - paramView.getScrollY();
      int i5 = i4 + paramRect.width();
      int i7 = paramRect.height();
      int i = Math.min(0, i4 - i2);
      int j = Math.min(0, n - m);
      int k = Math.max(0, i5 - i3);
      i1 = Math.max(0, n + i7 - (i1 - i6));
      if (getLayoutDirection() == 1)
        if (k != 0)
        {
          i = k;
          if (j == 0)
            break label217;
          label154: if ((i == 0) && (j == 0))
            break label243;
          if (!paramBoolean)
            break label232;
          paramRecyclerView.scrollBy(i, j);
        }
      while (true)
      {
        return true;
        i = Math.max(i, i5 - i3);
        break;
        if (i != 0)
          break;
        while (true)
          i = Math.min(i4 - i2, k);
        label217: j = Math.min(n - m, i1);
        break label154;
        label232: paramRecyclerView.smoothScrollBy(i, j);
      }
      label243: return false;
    }

    public void requestLayout()
    {
      if (this.mRecyclerView != null)
        this.mRecyclerView.requestLayout();
    }

    public void requestSimpleAnimationsInNextLayout()
    {
      this.mRequestedSimpleAnimations = true;
    }

    public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }

    public void scrollToPosition(int paramInt)
    {
    }

    public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }

    public void setAutoMeasureEnabled(boolean paramBoolean)
    {
      this.mAutoMeasure = paramBoolean;
    }

    void setExactMeasureSpecsFrom(RecyclerView paramRecyclerView)
    {
      setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getHeight(), 1073741824));
    }

    public final void setItemPrefetchEnabled(boolean paramBoolean)
    {
      if (paramBoolean != this.mItemPrefetchEnabled)
      {
        this.mItemPrefetchEnabled = paramBoolean;
        this.mPrefetchMaxCountObserved = 0;
        if (this.mRecyclerView != null)
          this.mRecyclerView.mRecycler.updateViewCacheSize();
      }
    }

    void setMeasureSpecs(int paramInt1, int paramInt2)
    {
      this.mWidth = View.MeasureSpec.getSize(paramInt1);
      this.mWidthMode = View.MeasureSpec.getMode(paramInt1);
      if ((this.mWidthMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC))
        this.mWidth = 0;
      this.mHeight = View.MeasureSpec.getSize(paramInt2);
      this.mHeightMode = View.MeasureSpec.getMode(paramInt2);
      if ((this.mHeightMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC))
        this.mHeight = 0;
    }

    public void setMeasuredDimension(int paramInt1, int paramInt2)
    {
      this.mRecyclerView.setMeasuredDimension(paramInt1, paramInt2);
    }

    public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
    {
      int i = paramRect.width();
      int j = getPaddingLeft();
      int k = getPaddingRight();
      int m = paramRect.height();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      setMeasuredDimension(chooseSize(paramInt1, i + j + k, getMinimumWidth()), chooseSize(paramInt2, m + n + i1, getMinimumHeight()));
    }

    void setMeasuredDimensionFromChildren(int paramInt1, int paramInt2)
    {
      int k = 2147483647;
      int j = -2147483648;
      int i5 = getChildCount();
      if (i5 == 0)
      {
        this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
        return;
      }
      int i = 0;
      int n = -2147483648;
      int i3 = 2147483647;
      while (i < i5)
      {
        View localView = getChildAt(i);
        Rect localRect = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(localView, localRect);
        int m = i3;
        if (localRect.left < i3)
          m = localRect.left;
        int i1 = n;
        if (localRect.right > n)
          i1 = localRect.right;
        int i2 = k;
        if (localRect.top < k)
          i2 = localRect.top;
        int i4 = j;
        if (localRect.bottom > j)
          i4 = localRect.bottom;
        i += 1;
        i3 = m;
        n = i1;
        k = i2;
        j = i4;
      }
      this.mRecyclerView.mTempRect.set(i3, k, n, j);
      setMeasuredDimension(this.mRecyclerView.mTempRect, paramInt1, paramInt2);
    }

    public void setMeasurementCacheEnabled(boolean paramBoolean)
    {
      this.mMeasurementCacheEnabled = paramBoolean;
    }

    void setRecyclerView(RecyclerView paramRecyclerView)
    {
      if (paramRecyclerView == null)
      {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        this.mWidth = 0;
      }
      for (this.mHeight = 0; ; this.mHeight = paramRecyclerView.getHeight())
      {
        this.mWidthMode = 1073741824;
        this.mHeightMode = 1073741824;
        return;
        this.mRecyclerView = paramRecyclerView;
        this.mChildHelper = paramRecyclerView.mChildHelper;
        this.mWidth = paramRecyclerView.getWidth();
      }
    }

    boolean shouldMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      return (paramView.isLayoutRequested()) || (!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getHeight(), paramInt2, paramLayoutParams.height));
    }

    boolean shouldMeasureTwice()
    {
      return false;
    }

    boolean shouldReMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      return (!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getMeasuredWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getMeasuredHeight(), paramInt2, paramLayoutParams.height));
    }

    public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
    {
      Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
    }

    public void startSmoothScroll(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if ((this.mSmoothScroller != null) && (paramSmoothScroller != this.mSmoothScroller) && (this.mSmoothScroller.isRunning()))
        this.mSmoothScroller.stop();
      this.mSmoothScroller = paramSmoothScroller;
      this.mSmoothScroller.start(this.mRecyclerView, this);
    }

    public void stopIgnoringView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      paramView.stopIgnoring();
      paramView.resetInternal();
      paramView.addFlags(4);
    }

    void stopSmoothScroller()
    {
      if (this.mSmoothScroller != null)
        this.mSmoothScroller.stop();
    }

    public boolean supportsPredictiveItemAnimations()
    {
      return false;
    }

    public static abstract interface LayoutPrefetchRegistry
    {
      public abstract void addPosition(int paramInt1, int paramInt2);
    }

    public static class Properties
    {
      public int orientation;
      public boolean reverseLayout;
      public int spanCount;
      public boolean stackFromEnd;
    }
  }

  public static class LayoutParams extends ViewGroup.MarginLayoutParams
  {
    final Rect mDecorInsets = new Rect();
    boolean mInsetsDirty = true;
    boolean mPendingInvalidate = false;
    RecyclerView.ViewHolder mViewHolder;

    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }

    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }

    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }

    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }

    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }

    public int getViewAdapterPosition()
    {
      return this.mViewHolder.getAdapterPosition();
    }

    public int getViewLayoutPosition()
    {
      return this.mViewHolder.getLayoutPosition();
    }

    @Deprecated
    public int getViewPosition()
    {
      return this.mViewHolder.getPosition();
    }

    public boolean isItemChanged()
    {
      return this.mViewHolder.isUpdated();
    }

    public boolean isItemRemoved()
    {
      return this.mViewHolder.isRemoved();
    }

    public boolean isViewInvalid()
    {
      return this.mViewHolder.isInvalid();
    }

    public boolean viewNeedsUpdate()
    {
      return this.mViewHolder.needsUpdate();
    }
  }

  public static abstract interface OnChildAttachStateChangeListener
  {
    public abstract void onChildViewAttachedToWindow(View paramView);

    public abstract void onChildViewDetachedFromWindow(View paramView);
  }

  public static abstract class OnFlingListener
  {
    public abstract boolean onFling(int paramInt1, int paramInt2);
  }

  public static abstract interface OnItemTouchListener
  {
    public abstract boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);

    public abstract void onRequestDisallowInterceptTouchEvent(boolean paramBoolean);

    public abstract void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
  }

  public static abstract class OnScrollListener
  {
    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
    {
    }

    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
    }
  }

  public static class RecycledViewPool
  {
    private static final int DEFAULT_MAX_SCRAP = 20;
    private int mAttachCount = 0;
    SparseArray<ScrapData> mScrap = new SparseArray();

    private ScrapData getScrapDataForType(int paramInt)
    {
      ScrapData localScrapData2 = (ScrapData)this.mScrap.get(paramInt);
      ScrapData localScrapData1 = localScrapData2;
      if (localScrapData2 == null)
      {
        localScrapData1 = new ScrapData();
        this.mScrap.put(paramInt, localScrapData1);
      }
      return localScrapData1;
    }

    void attach(RecyclerView.Adapter paramAdapter)
    {
      this.mAttachCount += 1;
    }

    public void clear()
    {
      int i = 0;
      while (i < this.mScrap.size())
      {
        ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap.clear();
        i += 1;
      }
    }

    void detach()
    {
      this.mAttachCount -= 1;
    }

    void factorInBindTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mBindRunningAverageNs = runningAverage(localScrapData.mBindRunningAverageNs, paramLong);
    }

    void factorInCreateTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mCreateRunningAverageNs = runningAverage(localScrapData.mCreateRunningAverageNs, paramLong);
    }

    public RecyclerView.ViewHolder getRecycledView(int paramInt)
    {
      Object localObject = (ScrapData)this.mScrap.get(paramInt);
      if ((localObject != null) && (!((ScrapData)localObject).mScrapHeap.isEmpty()))
      {
        localObject = ((ScrapData)localObject).mScrapHeap;
        return (RecyclerView.ViewHolder)((ArrayList)localObject).remove(((ArrayList)localObject).size() - 1);
      }
      return (RecyclerView.ViewHolder)null;
    }

    public int getRecycledViewCount(int paramInt)
    {
      return getScrapDataForType(paramInt).mScrapHeap.size();
    }

    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      if (paramAdapter1 != null)
        detach();
      if ((!paramBoolean) && (this.mAttachCount == 0))
        clear();
      if (paramAdapter2 != null)
        attach(paramAdapter2);
    }

    public void putRecycledView(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      ArrayList localArrayList = getScrapDataForType(i).mScrapHeap;
      if (((ScrapData)this.mScrap.get(i)).mMaxScrap <= localArrayList.size())
        return;
      paramViewHolder.resetInternal();
      localArrayList.add(paramViewHolder);
    }

    long runningAverage(long paramLong1, long paramLong2)
    {
      if (paramLong1 == 0L)
        return paramLong2;
      return paramLong1 / 4L * 3L + paramLong2 / 4L;
    }

    public void setMaxRecycledViews(int paramInt1, int paramInt2)
    {
      Object localObject = getScrapDataForType(paramInt1);
      ((ScrapData)localObject).mMaxScrap = paramInt2;
      localObject = ((ScrapData)localObject).mScrapHeap;
      if (localObject != null)
        while (((ArrayList)localObject).size() > paramInt2)
          ((ArrayList)localObject).remove(((ArrayList)localObject).size() - 1);
    }

    int size()
    {
      int i = 0;
      int k;
      for (int j = 0; i < this.mScrap.size(); j = k)
      {
        ArrayList localArrayList = ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap;
        k = j;
        if (localArrayList != null)
          k = j + localArrayList.size();
        i += 1;
      }
      return j;
    }

    boolean willBindInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mBindRunningAverageNs;
      return (l == 0L) || (l + paramLong1 < paramLong2);
    }

    boolean willCreateInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mCreateRunningAverageNs;
      return (l == 0L) || (l + paramLong1 < paramLong2);
    }

    static class ScrapData
    {
      long mBindRunningAverageNs = 0L;
      long mCreateRunningAverageNs = 0L;
      int mMaxScrap = 20;
      ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList();
    }
  }

  public final class Recycler
  {
    static final int DEFAULT_CACHE_SIZE = 2;
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    RecyclerView.RecycledViewPool mRecyclerPool;
    private int mRequestedCacheMax = 2;
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    int mViewCacheMax = 2;

    public Recycler()
    {
    }

    private void attachAccessibilityDelegate(View paramView)
    {
      if (RecyclerView.this.isAccessibilityEnabled())
      {
        if (ag.d(paramView) == 0)
          ag.c(paramView, 1);
        if (!ag.a(paramView))
          ag.a(paramView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
      }
    }

    private void invalidateDisplayListInt(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      int i = paramViewGroup.getChildCount() - 1;
      while (i >= 0)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView instanceof ViewGroup))
          invalidateDisplayListInt((ViewGroup)localView, true);
        i -= 1;
      }
      if (!paramBoolean)
        return;
      if (paramViewGroup.getVisibility() == 4)
      {
        paramViewGroup.setVisibility(0);
        paramViewGroup.setVisibility(4);
        return;
      }
      i = paramViewGroup.getVisibility();
      paramViewGroup.setVisibility(4);
      paramViewGroup.setVisibility(i);
    }

    private void invalidateDisplayListInt(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup))
        invalidateDisplayListInt((ViewGroup)paramViewHolder.itemView, false);
    }

    private boolean tryBindViewHolderByDeadline(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, long paramLong)
    {
      paramViewHolder.mOwnerRecyclerView = RecyclerView.this;
      int i = paramViewHolder.getItemViewType();
      long l = RecyclerView.this.getNanoTime();
      if ((paramLong != 9223372036854775807L) && (!this.mRecyclerPool.willBindInTime(i, l, paramLong)))
        return false;
      RecyclerView.this.mAdapter.bindViewHolder(paramViewHolder, paramInt1);
      paramLong = RecyclerView.this.getNanoTime();
      this.mRecyclerPool.factorInBindTime(paramViewHolder.getItemViewType(), paramLong - l);
      attachAccessibilityDelegate(paramViewHolder.itemView);
      if (RecyclerView.this.mState.isPreLayout())
        paramViewHolder.mPreLayoutPosition = paramInt2;
      return true;
    }

    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
    {
      RecyclerView.clearNestedRecyclerViewIfNotNested(paramViewHolder);
      ag.a(paramViewHolder.itemView, null);
      if (paramBoolean)
        dispatchViewRecycled(paramViewHolder);
      paramViewHolder.mOwnerRecyclerView = null;
      getRecycledViewPool().putRecycledView(paramViewHolder);
    }

    public void bindViewToPosition(View paramView, int paramInt)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder == null)
        throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
      int i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
      if ((i < 0) || (i >= RecyclerView.this.mAdapter.getItemCount()))
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + i + ").state:" + RecyclerView.this.mState.getItemCount());
      tryBindViewHolderByDeadline(localViewHolder, i, paramInt, 9223372036854775807L);
      paramView = localViewHolder.itemView.getLayoutParams();
      if (paramView == null)
      {
        paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        localViewHolder.itemView.setLayoutParams(paramView);
        paramView.mInsetsDirty = true;
        paramView.mViewHolder = localViewHolder;
        if (localViewHolder.itemView.getParent() != null)
          break label225;
      }
      label225: for (boolean bool = true; ; bool = false)
      {
        paramView.mPendingInvalidate = bool;
        return;
        if (!RecyclerView.this.checkLayoutParams(paramView))
        {
          paramView = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(paramView);
          localViewHolder.itemView.setLayoutParams(paramView);
          break;
        }
        paramView = (RecyclerView.LayoutParams)paramView;
        break;
      }
    }

    public void clear()
    {
      this.mAttachedScrap.clear();
      recycleAndClearCachedViews();
    }

    void clearOldPositions()
    {
      int j = 0;
      int k = this.mCachedViews.size();
      int i = 0;
      while (i < k)
      {
        ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).clearOldPosition();
        i += 1;
      }
      k = this.mAttachedScrap.size();
      i = 0;
      while (i < k)
      {
        ((RecyclerView.ViewHolder)this.mAttachedScrap.get(i)).clearOldPosition();
        i += 1;
      }
      if (this.mChangedScrap != null)
      {
        k = this.mChangedScrap.size();
        i = j;
        while (i < k)
        {
          ((RecyclerView.ViewHolder)this.mChangedScrap.get(i)).clearOldPosition();
          i += 1;
        }
      }
    }

    void clearScrap()
    {
      this.mAttachedScrap.clear();
      if (this.mChangedScrap != null)
        this.mChangedScrap.clear();
    }

    public int convertPreLayoutPositionToPostLayout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount()))
        throw new IndexOutOfBoundsException("invalid position " + paramInt + ". State item count is " + RecyclerView.this.mState.getItemCount());
      if (!RecyclerView.this.mState.isPreLayout())
        return paramInt;
      return RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
    }

    void dispatchViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.this.mRecyclerListener != null)
        RecyclerView.this.mRecyclerListener.onViewRecycled(paramViewHolder);
      if (RecyclerView.this.mAdapter != null)
        RecyclerView.this.mAdapter.onViewRecycled(paramViewHolder);
      if (RecyclerView.this.mState != null)
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
    }

    RecyclerView.ViewHolder getChangedScrapViewForPosition(int paramInt)
    {
      int j = 0;
      int k;
      if (this.mChangedScrap != null)
      {
        k = this.mChangedScrap.size();
        if (k != 0);
      }
      else
      {
        return null;
      }
      int i = 0;
      RecyclerView.ViewHolder localViewHolder;
      while (i < k)
      {
        localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(i);
        if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getLayoutPosition() == paramInt))
        {
          localViewHolder.addFlags(32);
          return localViewHolder;
        }
        i += 1;
      }
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        paramInt = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((paramInt > 0) && (paramInt < RecyclerView.this.mAdapter.getItemCount()))
        {
          long l = RecyclerView.this.mAdapter.getItemId(paramInt);
          paramInt = j;
          while (paramInt < k)
          {
            localViewHolder = (RecyclerView.ViewHolder)this.mChangedScrap.get(paramInt);
            if ((!localViewHolder.wasReturnedFromScrap()) && (localViewHolder.getItemId() == l))
            {
              localViewHolder.addFlags(32);
              return localViewHolder;
            }
            paramInt += 1;
          }
        }
      }
      return null;
    }

    RecyclerView.RecycledViewPool getRecycledViewPool()
    {
      if (this.mRecyclerPool == null)
        this.mRecyclerPool = new RecyclerView.RecycledViewPool();
      return this.mRecyclerPool;
    }

    int getScrapCount()
    {
      return this.mAttachedScrap.size();
    }

    public List<RecyclerView.ViewHolder> getScrapList()
    {
      return this.mUnmodifiableAttachedScrap;
    }

    RecyclerView.ViewHolder getScrapOrCachedViewForId(long paramLong, int paramInt, boolean paramBoolean)
    {
      int i = this.mAttachedScrap.size() - 1;
      RecyclerView.ViewHolder localViewHolder2;
      RecyclerView.ViewHolder localViewHolder1;
      while (i >= 0)
      {
        localViewHolder2 = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((localViewHolder2.getItemId() == paramLong) && (!localViewHolder2.wasReturnedFromScrap()))
        {
          if (paramInt == localViewHolder2.getItemViewType())
          {
            localViewHolder2.addFlags(32);
            localViewHolder1 = localViewHolder2;
            if (localViewHolder2.isRemoved())
            {
              localViewHolder1 = localViewHolder2;
              if (!RecyclerView.this.mState.isPreLayout())
              {
                localViewHolder2.setFlags(2, 14);
                localViewHolder1 = localViewHolder2;
              }
            }
            return localViewHolder1;
          }
          if (!paramBoolean)
          {
            this.mAttachedScrap.remove(i);
            RecyclerView.this.removeDetachedView(localViewHolder2.itemView, false);
            quickRecycleScrapView(localViewHolder2.itemView);
          }
        }
        i -= 1;
      }
      i = this.mCachedViews.size() - 1;
      while (true)
      {
        if (i < 0)
          break label247;
        localViewHolder2 = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder2.getItemId() == paramLong)
        {
          if (paramInt == localViewHolder2.getItemViewType())
          {
            localViewHolder1 = localViewHolder2;
            if (paramBoolean)
              break;
            this.mCachedViews.remove(i);
            return localViewHolder2;
          }
          if (!paramBoolean)
          {
            recycleCachedViewAt(i);
            return null;
          }
        }
        i -= 1;
      }
      label247: return null;
    }

    RecyclerView.ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int paramInt, boolean paramBoolean)
    {
      int j = 0;
      int k = this.mAttachedScrap.size();
      int i = 0;
      Object localObject;
      while (i < k)
      {
        localObject = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((!((RecyclerView.ViewHolder)localObject).wasReturnedFromScrap()) && (((RecyclerView.ViewHolder)localObject).getLayoutPosition() == paramInt) && (!((RecyclerView.ViewHolder)localObject).isInvalid()) && ((RecyclerView.this.mState.mInPreLayout) || (!((RecyclerView.ViewHolder)localObject).isRemoved())))
        {
          ((RecyclerView.ViewHolder)localObject).addFlags(32);
          return localObject;
        }
        i += 1;
      }
      RecyclerView.ViewHolder localViewHolder;
      if (!paramBoolean)
      {
        localObject = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(paramInt);
        if (localObject != null)
        {
          localViewHolder = RecyclerView.getChildViewHolderInt((View)localObject);
          RecyclerView.this.mChildHelper.unhide((View)localObject);
          paramInt = RecyclerView.this.mChildHelper.indexOfChild((View)localObject);
          if (paramInt == -1)
            throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + localViewHolder);
          RecyclerView.this.mChildHelper.detachViewFromParent(paramInt);
          scrapView((View)localObject);
          localViewHolder.addFlags(8224);
          return localViewHolder;
        }
      }
      k = this.mCachedViews.size();
      i = j;
      while (true)
      {
        if (i >= k)
          break label287;
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if ((!localViewHolder.isInvalid()) && (localViewHolder.getLayoutPosition() == paramInt))
        {
          localObject = localViewHolder;
          if (paramBoolean)
            break;
          this.mCachedViews.remove(i);
          return localViewHolder;
        }
        i += 1;
      }
      label287: return (RecyclerView.ViewHolder)null;
    }

    View getScrapViewAt(int paramInt)
    {
      return ((RecyclerView.ViewHolder)this.mAttachedScrap.get(paramInt)).itemView;
    }

    public View getViewForPosition(int paramInt)
    {
      return getViewForPosition(paramInt, false);
    }

    View getViewForPosition(int paramInt, boolean paramBoolean)
    {
      return tryGetViewHolderForPositionByDeadline(paramInt, paramBoolean, 9223372036854775807L).itemView;
    }

    void markItemDecorInsetsDirty()
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)((RecyclerView.ViewHolder)this.mCachedViews.get(i)).itemView.getLayoutParams();
        if (localLayoutParams != null)
          localLayoutParams.mInsetsDirty = true;
        i += 1;
      }
    }

    void markKnownViewsInvalid()
    {
      int j;
      int i;
      if ((RecyclerView.this.mAdapter != null) && (RecyclerView.this.mAdapter.hasStableIds()))
      {
        j = this.mCachedViews.size();
        i = 0;
      }
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
        {
          localViewHolder.addFlags(6);
          localViewHolder.addChangePayload(null);
        }
        i += 1;
        continue;
        recycleAndClearCachedViews();
      }
    }

    void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= paramInt1))
          localViewHolder.offsetPosition(paramInt2, true);
        i += 1;
      }
    }

    void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
    {
      int i;
      int j;
      int k;
      int m;
      label25: RecyclerView.ViewHolder localViewHolder;
      if (paramInt1 < paramInt2)
      {
        i = -1;
        j = paramInt2;
        k = paramInt1;
        int n = this.mCachedViews.size();
        m = 0;
        if (m >= n)
          return;
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(m);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= k) && (localViewHolder.mPosition <= j))
          break label91;
      }
      while (true)
      {
        m += 1;
        break label25;
        i = 1;
        j = paramInt1;
        k = paramInt2;
        break;
        label91: if (localViewHolder.mPosition == paramInt1)
        {
          localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
          continue;
        }
        localViewHolder.offsetPosition(i, false);
      }
    }

    void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
        {
          if (localViewHolder.mPosition < paramInt1 + paramInt2)
            break label63;
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        }
        while (true)
        {
          i -= 1;
          break;
          label63: if (localViewHolder.mPosition < paramInt1)
            continue;
          localViewHolder.addFlags(8);
          recycleCachedViewAt(i);
        }
      }
    }

    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      clear();
      getRecycledViewPool().onAdapterChanged(paramAdapter1, paramAdapter2, paramBoolean);
    }

    void quickRecycleScrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.ViewHolder.access$802(paramView, null);
      RecyclerView.ViewHolder.access$902(paramView, false);
      paramView.clearReturnedFromScrapFlag();
      recycleViewHolderInternal(paramView);
    }

    void recycleAndClearCachedViews()
    {
      int i = this.mCachedViews.size() - 1;
      while (i >= 0)
      {
        recycleCachedViewAt(i);
        i -= 1;
      }
      this.mCachedViews.clear();
      if (RecyclerView.ALLOW_THREAD_GAP_WORK)
        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
    }

    void recycleCachedViewAt(int paramInt)
    {
      addViewHolderToRecycledViewPool((RecyclerView.ViewHolder)this.mCachedViews.get(paramInt), true);
      this.mCachedViews.remove(paramInt);
    }

    public void recycleView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isTmpDetached())
        RecyclerView.this.removeDetachedView(paramView, false);
      if (localViewHolder.isScrap())
        localViewHolder.unScrap();
      while (true)
      {
        recycleViewHolderInternal(localViewHolder);
        return;
        if (!localViewHolder.wasReturnedFromScrap())
          continue;
        localViewHolder.clearReturnedFromScrapFlag();
      }
    }

    void recycleViewHolderInternal(RecyclerView.ViewHolder paramViewHolder)
    {
      int k = 0;
      if ((paramViewHolder.isScrap()) || (paramViewHolder.itemView.getParent() != null))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Scrapped or attached views may not be recycled. isScrap:").append(paramViewHolder.isScrap()).append(" isAttached:");
        if (paramViewHolder.itemView.getParent() != null);
        for (bool = true; ; bool = false)
          throw new IllegalArgumentException(bool);
      }
      if (paramViewHolder.isTmpDetached())
        throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + paramViewHolder);
      if (paramViewHolder.shouldIgnore())
        throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
      boolean bool = RecyclerView.ViewHolder.access$700(paramViewHolder);
      int i;
      if ((RecyclerView.this.mAdapter != null) && (bool) && (RecyclerView.this.mAdapter.onFailedToRecycleView(paramViewHolder)))
      {
        i = 1;
        if ((i == 0) && (!paramViewHolder.isRecyclable()))
          break label386;
        if ((this.mViewCacheMax <= 0) || (paramViewHolder.hasAnyOfTheFlags(526)))
          break label381;
        j = this.mCachedViews.size();
        i = j;
        if (j >= this.mViewCacheMax)
        {
          i = j;
          if (j > 0)
          {
            recycleCachedViewAt(0);
            i = j - 1;
          }
        }
        j = i;
        if (RecyclerView.ALLOW_THREAD_GAP_WORK)
        {
          j = i;
          if (i > 0)
          {
            j = i;
            if (!RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(paramViewHolder.mPosition))
            {
              i -= 1;
              label273: if (i >= 0)
              {
                j = ((RecyclerView.ViewHolder)this.mCachedViews.get(i)).mPosition;
                if (RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(j))
                  break label374;
              }
              j = i + 1;
            }
          }
        }
        this.mCachedViews.add(j, paramViewHolder);
        i = 1;
        label321: j = i;
        if (i == 0)
        {
          addViewHolderToRecycledViewPool(paramViewHolder, true);
          k = 1;
        }
      }
      label386: for (int j = i; ; j = 0)
      {
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
        if ((j == 0) && (k == 0) && (bool))
          paramViewHolder.mOwnerRecyclerView = null;
        return;
        i = 0;
        break;
        label374: i -= 1;
        break label273;
        label381: i = 0;
        break label321;
      }
    }

    void recycleViewInternal(View paramView)
    {
      recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(paramView));
    }

    void scrapView(View paramView)
    {
      paramView = RecyclerView.getChildViewHolderInt(paramView);
      if ((paramView.hasAnyOfTheFlags(12)) || (!paramView.isUpdated()) || (RecyclerView.this.canReuseUpdatedViewHolder(paramView)))
      {
        if ((paramView.isInvalid()) && (!paramView.isRemoved()) && (!RecyclerView.this.mAdapter.hasStableIds()))
          throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
        paramView.setScrapContainer(this, false);
        this.mAttachedScrap.add(paramView);
        return;
      }
      if (this.mChangedScrap == null)
        this.mChangedScrap = new ArrayList();
      paramView.setScrapContainer(this, true);
      this.mChangedScrap.add(paramView);
    }

    void setAdapterPositionsAsUnknown()
    {
      int j = this.mCachedViews.size();
      int i = 0;
      while (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder != null)
          localViewHolder.addFlags(512);
        i += 1;
      }
    }

    void setRecycledViewPool(RecyclerView.RecycledViewPool paramRecycledViewPool)
    {
      if (this.mRecyclerPool != null)
        this.mRecyclerPool.detach();
      this.mRecyclerPool = paramRecycledViewPool;
      if (paramRecycledViewPool != null)
        this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
    }

    void setViewCacheExtension(RecyclerView.ViewCacheExtension paramViewCacheExtension)
    {
      this.mViewCacheExtension = paramViewCacheExtension;
    }

    public void setViewCacheSize(int paramInt)
    {
      this.mRequestedCacheMax = paramInt;
      updateViewCacheSize();
    }

    RecyclerView.ViewHolder tryGetViewHolderForPositionByDeadline(int paramInt, boolean paramBoolean, long paramLong)
    {
      boolean bool = true;
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount()))
        throw new IndexOutOfBoundsException("Invalid item position " + paramInt + "(" + paramInt + "). Item count:" + RecyclerView.this.mState.getItemCount());
      Object localObject2;
      int i;
      if (RecyclerView.this.mState.isPreLayout())
      {
        localObject2 = getChangedScrapViewForPosition(paramInt);
        if (localObject2 != null)
          i = 1;
      }
      for (int j = i; ; j = 0)
      {
        Object localObject1 = localObject2;
        i = j;
        if (localObject2 == null)
        {
          localObject2 = getScrapOrHiddenOrCachedHolderForPosition(paramInt, paramBoolean);
          localObject1 = localObject2;
          i = j;
          if (localObject2 != null)
          {
            if (validateViewHolderForOffsetPosition((RecyclerView.ViewHolder)localObject2))
              break label317;
            if (!paramBoolean)
            {
              ((RecyclerView.ViewHolder)localObject2).addFlags(4);
              if (!((RecyclerView.ViewHolder)localObject2).isScrap())
                break label301;
              RecyclerView.this.removeDetachedView(((RecyclerView.ViewHolder)localObject2).itemView, false);
              ((RecyclerView.ViewHolder)localObject2).unScrap();
              label189: recycleViewHolderInternal((RecyclerView.ViewHolder)localObject2);
            }
            localObject1 = null;
            i = j;
          }
        }
        int k;
        while (true)
        {
          if (localObject1 != null)
            break label971;
          k = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
          if ((k >= 0) && (k < RecyclerView.this.mAdapter.getItemCount()))
            break label327;
          throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + k + ").state:" + RecyclerView.this.mState.getItemCount());
          i = 0;
          break;
          label301: if (!((RecyclerView.ViewHolder)localObject2).wasReturnedFromScrap())
            break label189;
          ((RecyclerView.ViewHolder)localObject2).clearReturnedFromScrapFlag();
          break label189;
          label317: i = 1;
          localObject1 = localObject2;
        }
        label327: j = RecyclerView.this.mAdapter.getItemViewType(k);
        if (RecyclerView.this.mAdapter.hasStableIds())
        {
          localObject2 = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(k), j, paramBoolean);
          localObject1 = localObject2;
          if (localObject2 != null)
          {
            ((RecyclerView.ViewHolder)localObject2).mPosition = k;
            i = 1;
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = localObject2;
              if (this.mViewCacheExtension != null)
              {
                View localView = this.mViewCacheExtension.getViewForPositionAndType(this, paramInt, j);
                localObject1 = localObject2;
                if (localView != null)
                {
                  localObject2 = RecyclerView.this.getChildViewHolder(localView);
                  if (localObject2 == null)
                    throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder");
                  localObject1 = localObject2;
                  if (((RecyclerView.ViewHolder)localObject2).shouldIgnore())
                    throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
                }
              }
            }
            localObject2 = localObject1;
            if (localObject1 == null)
            {
              localObject1 = getRecycledViewPool().getRecycledView(j);
              localObject2 = localObject1;
              if (localObject1 != null)
              {
                ((RecyclerView.ViewHolder)localObject1).resetInternal();
                localObject2 = localObject1;
                if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST)
                {
                  invalidateDisplayListInt((RecyclerView.ViewHolder)localObject1);
                  localObject2 = localObject1;
                }
              }
            }
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              long l1 = RecyclerView.this.getNanoTime();
              if ((paramLong != 9223372036854775807L) && (!this.mRecyclerPool.willCreateInTime(j, l1, paramLong)))
                return null;
              localObject1 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, j);
              if (RecyclerView.ALLOW_THREAD_GAP_WORK)
              {
                localObject2 = RecyclerView.findNestedRecyclerView(((RecyclerView.ViewHolder)localObject1).itemView);
                if (localObject2 != null)
                  ((RecyclerView.ViewHolder)localObject1).mNestedRecyclerView = new WeakReference(localObject2);
              }
              long l2 = RecyclerView.this.getNanoTime();
              this.mRecyclerPool.factorInCreateTime(j, l2 - l1);
            }
          }
        }
        label830: label959: label971: for (localObject2 = localObject1; ; localObject2 = localObject1)
        {
          if ((i != 0) && (!RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject2).hasAnyOfTheFlags(8192)))
          {
            ((RecyclerView.ViewHolder)localObject2).setFlags(0, 8192);
            if (RecyclerView.this.mState.mRunSimpleAnimations)
            {
              j = RecyclerView.ItemAnimator.buildAdapterChangeFlagsForAnimations((RecyclerView.ViewHolder)localObject2);
              localObject1 = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, (RecyclerView.ViewHolder)localObject2, j | 0x1000, ((RecyclerView.ViewHolder)localObject2).getUnmodifiedPayloads());
              RecyclerView.this.recordAnimationInfoIfBouncedHiddenView((RecyclerView.ViewHolder)localObject2, (RecyclerView.ItemAnimator.ItemHolderInfo)localObject1);
            }
          }
          if ((RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject2).isBound()))
          {
            ((RecyclerView.ViewHolder)localObject2).mPreLayoutPosition = paramInt;
            paramBoolean = false;
          }
          while (true)
          {
            localObject1 = ((RecyclerView.ViewHolder)localObject2).itemView.getLayoutParams();
            if (localObject1 == null)
            {
              localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
              ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
              ((RecyclerView.LayoutParams)localObject1).mViewHolder = ((RecyclerView.ViewHolder)localObject2);
              if ((i == 0) || (!paramBoolean))
                break label954;
            }
            for (paramBoolean = bool; ; paramBoolean = false)
            {
              ((RecyclerView.LayoutParams)localObject1).mPendingInvalidate = paramBoolean;
              return localObject2;
              if ((((RecyclerView.ViewHolder)localObject2).isBound()) && (!((RecyclerView.ViewHolder)localObject2).needsUpdate()) && (!((RecyclerView.ViewHolder)localObject2).isInvalid()))
                break label959;
              paramBoolean = tryBindViewHolderByDeadline((RecyclerView.ViewHolder)localObject2, RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt), paramInt, paramLong);
              break;
              if (!RecyclerView.this.checkLayoutParams((ViewGroup.LayoutParams)localObject1))
              {
                localObject1 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams((ViewGroup.LayoutParams)localObject1);
                ((RecyclerView.ViewHolder)localObject2).itemView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
                break label830;
              }
              localObject1 = (RecyclerView.LayoutParams)localObject1;
              break label830;
            }
            paramBoolean = false;
          }
          localObject2 = localObject1;
          break;
        }
        label954: localObject2 = null;
      }
    }

    void unscrapView(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.ViewHolder.access$900(paramViewHolder))
        this.mChangedScrap.remove(paramViewHolder);
      while (true)
      {
        RecyclerView.ViewHolder.access$802(paramViewHolder, null);
        RecyclerView.ViewHolder.access$902(paramViewHolder, false);
        paramViewHolder.clearReturnedFromScrapFlag();
        return;
        this.mAttachedScrap.remove(paramViewHolder);
      }
    }

    void updateViewCacheSize()
    {
      if (RecyclerView.this.mLayout != null);
      for (int i = RecyclerView.this.mLayout.mPrefetchMaxCountObserved; ; i = 0)
      {
        this.mViewCacheMax = (i + this.mRequestedCacheMax);
        i = this.mCachedViews.size() - 1;
        while ((i >= 0) && (this.mCachedViews.size() > this.mViewCacheMax))
        {
          recycleCachedViewAt(i);
          i -= 1;
        }
      }
    }

    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool2 = true;
      boolean bool1;
      if (paramViewHolder.isRemoved())
        bool1 = RecyclerView.this.mState.isPreLayout();
      do
      {
        do
        {
          return bool1;
          if ((paramViewHolder.mPosition < 0) || (paramViewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount()))
            throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + paramViewHolder);
          if ((!RecyclerView.this.mState.isPreLayout()) && (RecyclerView.this.mAdapter.getItemViewType(paramViewHolder.mPosition) != paramViewHolder.getItemViewType()))
            return false;
          bool1 = bool2;
        }
        while (!RecyclerView.this.mAdapter.hasStableIds());
        bool1 = bool2;
      }
      while (paramViewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(paramViewHolder.mPosition));
      return false;
    }

    void viewRangeUpdate(int paramInt1, int paramInt2)
    {
      int i = this.mCachedViews.size() - 1;
      if (i >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(i);
        if (localViewHolder == null);
        while (true)
        {
          i -= 1;
          break;
          int j = localViewHolder.getLayoutPosition();
          if ((j < paramInt1) || (j >= paramInt1 + paramInt2))
            continue;
          localViewHolder.addFlags(2);
          recycleCachedViewAt(i);
        }
      }
    }
  }

  public static abstract interface RecyclerListener
  {
    public abstract void onViewRecycled(RecyclerView.ViewHolder paramViewHolder);
  }

  private class RecyclerViewDataObserver extends RecyclerView.AdapterDataObserver
  {
    RecyclerViewDataObserver()
    {
    }

    public void onChanged()
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      RecyclerView.this.mState.mStructureChanged = true;
      RecyclerView.this.setDataSetChangedAfterLayout();
      if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates())
        RecyclerView.this.requestLayout();
    }

    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(paramInt1, paramInt2, paramObject))
        triggerUpdateProcessor();
    }

    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(paramInt1, paramInt2))
        triggerUpdateProcessor();
    }

    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(paramInt1, paramInt2, paramInt3))
        triggerUpdateProcessor();
    }

    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(paramInt1, paramInt2))
        triggerUpdateProcessor();
    }

    void triggerUpdateProcessor()
    {
      if ((RecyclerView.POST_UPDATES_ON_ANIMATION) && (RecyclerView.this.mHasFixedSize) && (RecyclerView.this.mIsAttached))
      {
        ag.a(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
        return;
      }
      RecyclerView.this.mAdapterUpdateDuringMeasure = true;
      RecyclerView.this.requestLayout();
    }
  }

  public static class SavedState extends android.support.v4.view.a
  {
    public static final Parcelable.Creator<SavedState> CREATOR = f.a(new g()
    {
      public RecyclerView.SavedState createFromParcel(Parcel paramParcel, ClassLoader paramClassLoader)
      {
        return new RecyclerView.SavedState(paramParcel, paramClassLoader);
      }

      public RecyclerView.SavedState[] newArray(int paramInt)
      {
        return new RecyclerView.SavedState[paramInt];
      }
    });
    Parcelable mLayoutState;

    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramClassLoader != null);
      while (true)
      {
        this.mLayoutState = paramParcel.readParcelable(paramClassLoader);
        return;
        paramClassLoader = RecyclerView.LayoutManager.class.getClassLoader();
      }
    }

    SavedState(Parcelable paramParcelable)
    {
      super();
    }

    void copyFrom(SavedState paramSavedState)
    {
      this.mLayoutState = paramSavedState.mLayoutState;
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeParcelable(this.mLayoutState, 0);
    }
  }

  public static class SimpleOnItemTouchListener
    implements RecyclerView.OnItemTouchListener
  {
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      return false;
    }

    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
    }

    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
    }
  }

  public static abstract class SmoothScroller
  {
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mPendingInitialRun;
    private RecyclerView mRecyclerView;
    private final Action mRecyclingAction = new Action(0, 0);
    private boolean mRunning;
    private int mTargetPosition = -1;
    private View mTargetView;

    private void onAnimation(int paramInt1, int paramInt2)
    {
      RecyclerView localRecyclerView = this.mRecyclerView;
      if ((!this.mRunning) || (this.mTargetPosition == -1) || (localRecyclerView == null))
        stop();
      this.mPendingInitialRun = false;
      if (this.mTargetView != null)
      {
        if (getChildPosition(this.mTargetView) != this.mTargetPosition)
          break label151;
        onTargetFound(this.mTargetView, localRecyclerView.mState, this.mRecyclingAction);
        this.mRecyclingAction.runIfNecessary(localRecyclerView);
        stop();
      }
      while (true)
      {
        if (this.mRunning)
        {
          onSeekTargetStep(paramInt1, paramInt2, localRecyclerView.mState, this.mRecyclingAction);
          boolean bool = this.mRecyclingAction.hasJumpTarget();
          this.mRecyclingAction.runIfNecessary(localRecyclerView);
          if (bool)
          {
            if (!this.mRunning)
              break;
            this.mPendingInitialRun = true;
            localRecyclerView.mViewFlinger.postOnAnimation();
          }
        }
        return;
        label151: Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
        this.mTargetView = null;
      }
      stop();
    }

    public View findViewByPosition(int paramInt)
    {
      return this.mRecyclerView.mLayout.findViewByPosition(paramInt);
    }

    public int getChildCount()
    {
      return this.mRecyclerView.mLayout.getChildCount();
    }

    public int getChildPosition(View paramView)
    {
      return this.mRecyclerView.getChildLayoutPosition(paramView);
    }

    public RecyclerView.LayoutManager getLayoutManager()
    {
      return this.mLayoutManager;
    }

    public int getTargetPosition()
    {
      return this.mTargetPosition;
    }

    @Deprecated
    public void instantScrollToPosition(int paramInt)
    {
      this.mRecyclerView.scrollToPosition(paramInt);
    }

    public boolean isPendingInitialRun()
    {
      return this.mPendingInitialRun;
    }

    public boolean isRunning()
    {
      return this.mRunning;
    }

    protected void normalize(PointF paramPointF)
    {
      double d = Math.sqrt(paramPointF.x * paramPointF.x + paramPointF.y * paramPointF.y);
      paramPointF.x = (float)(paramPointF.x / d);
      paramPointF.y = (float)(paramPointF.y / d);
    }

    protected void onChildAttachedToWindow(View paramView)
    {
      if (getChildPosition(paramView) == getTargetPosition())
        this.mTargetView = paramView;
    }

    protected abstract void onSeekTargetStep(int paramInt1, int paramInt2, RecyclerView.State paramState, Action paramAction);

    protected abstract void onStart();

    protected abstract void onStop();

    protected abstract void onTargetFound(View paramView, RecyclerView.State paramState, Action paramAction);

    public void setTargetPosition(int paramInt)
    {
      this.mTargetPosition = paramInt;
    }

    void start(RecyclerView paramRecyclerView, RecyclerView.LayoutManager paramLayoutManager)
    {
      this.mRecyclerView = paramRecyclerView;
      this.mLayoutManager = paramLayoutManager;
      if (this.mTargetPosition == -1)
        throw new IllegalArgumentException("Invalid target position");
      RecyclerView.State.access$1102(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      this.mRecyclerView.mViewFlinger.postOnAnimation();
    }

    protected final void stop()
    {
      if (!this.mRunning)
        return;
      onStop();
      RecyclerView.State.access$1102(this.mRecyclerView.mState, -1);
      this.mTargetView = null;
      this.mTargetPosition = -1;
      this.mPendingInitialRun = false;
      this.mRunning = false;
      this.mLayoutManager.onSmoothScrollerStopped(this);
      this.mLayoutManager = null;
      this.mRecyclerView = null;
    }

    public static class Action
    {
      public static final int UNDEFINED_DURATION = -2147483648;
      private boolean changed = false;
      private int consecutiveUpdates = 0;
      private int mDuration;
      private int mDx;
      private int mDy;
      private Interpolator mInterpolator;
      private int mJumpToPosition = -1;

      public Action(int paramInt1, int paramInt2)
      {
        this(paramInt1, paramInt2, -2147483648, null);
      }

      public Action(int paramInt1, int paramInt2, int paramInt3)
      {
        this(paramInt1, paramInt2, paramInt3, null);
      }

      public Action(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
      }

      private void validate()
      {
        if ((this.mInterpolator != null) && (this.mDuration < 1))
          throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
        if (this.mDuration < 1)
          throw new IllegalStateException("Scroll duration must be a positive number");
      }

      public int getDuration()
      {
        return this.mDuration;
      }

      public int getDx()
      {
        return this.mDx;
      }

      public int getDy()
      {
        return this.mDy;
      }

      public Interpolator getInterpolator()
      {
        return this.mInterpolator;
      }

      boolean hasJumpTarget()
      {
        return this.mJumpToPosition >= 0;
      }

      public void jumpTo(int paramInt)
      {
        this.mJumpToPosition = paramInt;
      }

      void runIfNecessary(RecyclerView paramRecyclerView)
      {
        if (this.mJumpToPosition >= 0)
        {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          paramRecyclerView.jumpToPositionForSmoothScroller(i);
          this.changed = false;
          return;
        }
        if (this.changed)
        {
          validate();
          if (this.mInterpolator == null)
            if (this.mDuration == -2147483648)
              paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
          while (true)
          {
            this.consecutiveUpdates += 1;
            if (this.consecutiveUpdates > 10)
              Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
            this.changed = false;
            return;
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
            continue;
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
          }
        }
        this.consecutiveUpdates = 0;
      }

      public void setDuration(int paramInt)
      {
        this.changed = true;
        this.mDuration = paramInt;
      }

      public void setDx(int paramInt)
      {
        this.changed = true;
        this.mDx = paramInt;
      }

      public void setDy(int paramInt)
      {
        this.changed = true;
        this.mDy = paramInt;
      }

      public void setInterpolator(Interpolator paramInterpolator)
      {
        this.changed = true;
        this.mInterpolator = paramInterpolator;
      }

      public void update(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
        this.changed = true;
      }
    }

    public static abstract interface ScrollVectorProvider
    {
      public abstract PointF computeScrollVectorForPosition(int paramInt);
    }
  }

  public static class State
  {
    static final int STEP_ANIMATIONS = 4;
    static final int STEP_LAYOUT = 2;
    static final int STEP_START = 1;
    private SparseArray<Object> mData;
    int mDeletedInvisibleItemCountSincePreviousLayout = 0;
    long mFocusedItemId;
    int mFocusedItemPosition;
    int mFocusedSubChildId;
    boolean mInPreLayout = false;
    boolean mIsMeasuring = false;
    int mItemCount = 0;
    int mLayoutStep = 1;
    int mPreviousLayoutItemCount = 0;
    boolean mRunPredictiveAnimations = false;
    boolean mRunSimpleAnimations = false;
    boolean mStructureChanged = false;
    private int mTargetPosition = -1;
    boolean mTrackOldChangeHolders = false;

    void assertLayoutStep(int paramInt)
    {
      if ((this.mLayoutStep & paramInt) == 0)
        throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(paramInt) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
    }

    public boolean didStructureChange()
    {
      return this.mStructureChanged;
    }

    public <T> T get(int paramInt)
    {
      if (this.mData == null)
        return null;
      return this.mData.get(paramInt);
    }

    public int getItemCount()
    {
      if (this.mInPreLayout)
        return this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
      return this.mItemCount;
    }

    public int getTargetScrollPosition()
    {
      return this.mTargetPosition;
    }

    public boolean hasTargetScrollPosition()
    {
      return this.mTargetPosition != -1;
    }

    public boolean isMeasuring()
    {
      return this.mIsMeasuring;
    }

    public boolean isPreLayout()
    {
      return this.mInPreLayout;
    }

    void prepareForNestedPrefetch(RecyclerView.Adapter paramAdapter)
    {
      this.mLayoutStep = 1;
      this.mItemCount = paramAdapter.getItemCount();
      this.mStructureChanged = false;
      this.mInPreLayout = false;
      this.mTrackOldChangeHolders = false;
      this.mIsMeasuring = false;
    }

    public void put(int paramInt, Object paramObject)
    {
      if (this.mData == null)
        this.mData = new SparseArray();
      this.mData.put(paramInt, paramObject);
    }

    public void remove(int paramInt)
    {
      if (this.mData == null)
        return;
      this.mData.remove(paramInt);
    }

    State reset()
    {
      this.mTargetPosition = -1;
      if (this.mData != null)
        this.mData.clear();
      this.mItemCount = 0;
      this.mStructureChanged = false;
      this.mIsMeasuring = false;
      return this;
    }

    public String toString()
    {
      return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
    }

    public boolean willRunPredictiveAnimations()
    {
      return this.mRunPredictiveAnimations;
    }

    public boolean willRunSimpleAnimations()
    {
      return this.mRunSimpleAnimations;
    }

    @Retention(RetentionPolicy.SOURCE)
    static @interface LayoutState
    {
    }
  }

  public static abstract class ViewCacheExtension
  {
    public abstract View getViewForPositionAndType(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2);
  }

  class ViewFlinger
    implements Runnable
  {
    private boolean mEatRunOnAnimationRequest = false;
    Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
    private int mLastFlingX;
    private int mLastFlingY;
    private boolean mReSchedulePostAnimationCallback = false;
    private t mScroller = t.a(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);

    public ViewFlinger()
    {
    }

    private int computeScrollDuration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int j = Math.abs(paramInt1);
      int k = Math.abs(paramInt2);
      int i;
      if (j > k)
      {
        i = 1;
        paramInt3 = (int)Math.sqrt(paramInt3 * paramInt3 + paramInt4 * paramInt4);
        paramInt2 = (int)Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
        if (i == 0)
          break label140;
      }
      label140: for (paramInt1 = RecyclerView.this.getWidth(); ; paramInt1 = RecyclerView.this.getHeight())
      {
        paramInt4 = paramInt1 / 2;
        float f3 = Math.min(1.0F, paramInt2 * 1.0F / paramInt1);
        float f1 = paramInt4;
        float f2 = paramInt4;
        f3 = distanceInfluenceForSnapDuration(f3);
        if (paramInt3 <= 0)
          break label151;
        paramInt1 = Math.round(1000.0F * Math.abs((f3 * f2 + f1) / paramInt3)) * 4;
        return Math.min(paramInt1, 2000);
        i = 0;
        break;
      }
      label151: if (i != 0);
      for (paramInt2 = j; ; paramInt2 = k)
      {
        paramInt1 = (int)((paramInt2 / paramInt1 + 1.0F) * 300.0F);
        break;
      }
    }

    private void disableRunOnAnimationRequests()
    {
      this.mReSchedulePostAnimationCallback = false;
      this.mEatRunOnAnimationRequest = true;
    }

    private float distanceInfluenceForSnapDuration(float paramFloat)
    {
      return (float)Math.sin((float)((paramFloat - 0.5F) * 0.47123891676382D));
    }

    private void enableRunOnAnimationRequests()
    {
      this.mEatRunOnAnimationRequest = false;
      if (this.mReSchedulePostAnimationCallback)
        postOnAnimation();
    }

    public void fling(int paramInt1, int paramInt2)
    {
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.a(0, 0, paramInt1, paramInt2, -2147483648, 2147483647, -2147483648, 2147483647);
      postOnAnimation();
    }

    void postOnAnimation()
    {
      if (this.mEatRunOnAnimationRequest)
      {
        this.mReSchedulePostAnimationCallback = true;
        return;
      }
      RecyclerView.this.removeCallbacks(this);
      ag.a(RecyclerView.this, this);
    }

    public void run()
    {
      if (RecyclerView.this.mLayout == null)
      {
        stop();
        return;
      }
      disableRunOnAnimationRequests();
      RecyclerView.this.consumePendingUpdateOperations();
      t localt = this.mScroller;
      RecyclerView.SmoothScroller localSmoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
      int i6;
      int i7;
      int i4;
      int i5;
      int i1;
      int k;
      int j;
      int i3;
      int i;
      int n;
      int m;
      if (localt.g())
      {
        i6 = localt.b();
        i7 = localt.c();
        i4 = i6 - this.mLastFlingX;
        i5 = i7 - this.mLastFlingY;
        i1 = 0;
        k = 0;
        i2 = 0;
        j = 0;
        this.mLastFlingX = i6;
        this.mLastFlingY = i7;
        i3 = 0;
        i = 0;
        n = 0;
        m = 0;
        if (RecyclerView.this.mAdapter == null)
          break label739;
        RecyclerView.this.eatRequestLayout();
        RecyclerView.this.onEnterLayoutOrScroll();
        j.a("RV Scroll");
        if (i4 != 0)
        {
          k = RecyclerView.this.mLayout.scrollHorizontallyBy(i4, RecyclerView.this.mRecycler, RecyclerView.this.mState);
          i = i4 - k;
        }
        if (i5 != 0)
        {
          j = RecyclerView.this.mLayout.scrollVerticallyBy(i5, RecyclerView.this.mRecycler, RecyclerView.this.mState);
          m = i5 - j;
        }
        j.a();
        RecyclerView.this.repositionShadowingViews();
        RecyclerView.this.onExitLayoutOrScroll();
        RecyclerView.this.resumeRequestLayout(false);
        n = m;
        i2 = j;
        i3 = i;
        i1 = k;
        if (localSmoothScroller == null)
          break label739;
        n = m;
        i2 = j;
        i3 = i;
        i1 = k;
        if (localSmoothScroller.isPendingInitialRun())
          break label739;
        n = m;
        i2 = j;
        i3 = i;
        i1 = k;
        if (!localSmoothScroller.isRunning())
          break label739;
        n = RecyclerView.this.mState.getItemCount();
        if (n != 0)
          break label671;
        localSmoothScroller.stop();
        n = j;
        j = i;
        if (!RecyclerView.this.mItemDecorations.isEmpty())
          RecyclerView.this.invalidate();
        if (RecyclerView.this.getOverScrollMode() != 2)
          RecyclerView.this.considerReleasingGlowsOnScroll(i4, i5);
        if ((j != 0) || (m != 0))
        {
          i1 = (int)localt.f();
          if (j == i6)
            break label839;
          if (j >= 0)
            break label756;
          i = -i1;
        }
      }
      label406: label425: label578: label839: for (int i2 = i; ; i2 = 0)
      {
        if (m != i7)
          if (m < 0)
            i = -i1;
        while (true)
        {
          if (RecyclerView.this.getOverScrollMode() != 2)
            RecyclerView.this.absorbGlows(i2, i);
          if (((i2 != 0) || (j == i6) || (localt.d() == 0)) && ((i != 0) || (m == i7) || (localt.e() == 0)))
            localt.h();
          if ((k != 0) || (n != 0))
            RecyclerView.this.dispatchOnScrolled(k, n);
          if (!RecyclerView.this.awakenScrollBars())
            RecyclerView.this.invalidate();
          if ((i5 != 0) && (RecyclerView.this.mLayout.canScrollVertically()) && (n == i5))
          {
            i = 1;
            if ((i4 == 0) || (!RecyclerView.this.mLayout.canScrollHorizontally()) || (k != i4))
              break label789;
            j = 1;
            if (((i4 != 0) || (i5 != 0)) && (j == 0) && (i == 0))
              break label794;
            i = 1;
            label598: if ((!localt.a()) && (i != 0))
              break label799;
            RecyclerView.this.setScrollState(0);
            if (RecyclerView.ALLOW_THREAD_GAP_WORK)
              RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
          }
          while (true)
          {
            if (localSmoothScroller != null)
            {
              if (localSmoothScroller.isPendingInitialRun())
                localSmoothScroller.onAnimation(0, 0);
              if (!this.mReSchedulePostAnimationCallback)
                localSmoothScroller.stop();
            }
            enableRunOnAnimationRequests();
            return;
            if (localSmoothScroller.getTargetPosition() >= n)
            {
              localSmoothScroller.setTargetPosition(n - 1);
              localSmoothScroller.onAnimation(i4 - i, i5 - m);
              n = j;
              j = i;
              break;
            }
            localSmoothScroller.onAnimation(i4 - i, i5 - m);
            i1 = k;
            i3 = i;
            i2 = j;
            n = m;
            label739: j = i3;
            m = n;
            n = i2;
            k = i1;
            break;
            label756: if (j > 0)
            {
              i = i1;
              break label406;
            }
            i = 0;
            break label406;
            i = i1;
            if (m > 0)
              break label425;
            i = 0;
            break label425;
            i = 0;
            break label552;
            j = 0;
            break label578;
            i = 0;
            break label598;
            postOnAnimation();
            if (RecyclerView.this.mGapWorker == null)
              continue;
            RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, i4, i5);
          }
          i = 0;
        }
      }
    }

    public void smoothScrollBy(int paramInt1, int paramInt2)
    {
      smoothScrollBy(paramInt1, paramInt2, 0, 0);
    }

    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3)
    {
      smoothScrollBy(paramInt1, paramInt2, paramInt3, RecyclerView.sQuinticInterpolator);
    }

    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      smoothScrollBy(paramInt1, paramInt2, computeScrollDuration(paramInt1, paramInt2, paramInt3, paramInt4));
    }

    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
    {
      if (this.mInterpolator != paramInterpolator)
      {
        this.mInterpolator = paramInterpolator;
        this.mScroller = t.a(RecyclerView.this.getContext(), paramInterpolator);
      }
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.a(0, 0, paramInt1, paramInt2, paramInt3);
      postOnAnimation();
    }

    public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
    {
      int i = computeScrollDuration(paramInt1, paramInt2, 0, 0);
      Interpolator localInterpolator = paramInterpolator;
      if (paramInterpolator == null)
        localInterpolator = RecyclerView.sQuinticInterpolator;
      smoothScrollBy(paramInt1, paramInt2, i, localInterpolator);
    }

    public void stop()
    {
      RecyclerView.this.removeCallbacks(this);
      this.mScroller.h();
    }
  }

  public static abstract class ViewHolder
  {
    static final int FLAG_ADAPTER_FULLUPDATE = 1024;
    static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
    static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
    static final int FLAG_BOUND = 1;
    static final int FLAG_IGNORE = 128;
    static final int FLAG_INVALID = 4;
    static final int FLAG_MOVED = 2048;
    static final int FLAG_NOT_RECYCLABLE = 16;
    static final int FLAG_REMOVED = 8;
    static final int FLAG_RETURNED_FROM_SCRAP = 32;
    static final int FLAG_TMP_DETACHED = 256;
    static final int FLAG_UPDATE = 2;
    private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
    static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
    public final View itemView;
    private int mFlags;
    private boolean mInChangeScrap = false;
    private int mIsRecyclableCount = 0;
    long mItemId = -1L;
    int mItemViewType = -1;
    WeakReference<RecyclerView> mNestedRecyclerView;
    int mOldPosition = -1;
    RecyclerView mOwnerRecyclerView;
    List<Object> mPayloads = null;
    int mPendingAccessibilityState = -1;
    int mPosition = -1;
    int mPreLayoutPosition = -1;
    private RecyclerView.Recycler mScrapContainer = null;
    ViewHolder mShadowedHolder = null;
    ViewHolder mShadowingHolder = null;
    List<Object> mUnmodifiedPayloads = null;
    private int mWasImportantForAccessibilityBeforeHidden = 0;

    public ViewHolder(View paramView)
    {
      if (paramView == null)
        throw new IllegalArgumentException("itemView may not be null");
      this.itemView = paramView;
    }

    private void createPayloadsIfNeeded()
    {
      if (this.mPayloads == null)
      {
        this.mPayloads = new ArrayList();
        this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
      }
    }

    private boolean doesTransientStatePreventRecycling()
    {
      return ((this.mFlags & 0x10) == 0) && (ag.b(this.itemView));
    }

    private void onEnteredHiddenState(RecyclerView paramRecyclerView)
    {
      this.mWasImportantForAccessibilityBeforeHidden = ag.d(this.itemView);
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, 4);
    }

    private void onLeftHiddenState(RecyclerView paramRecyclerView)
    {
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }

    private boolean shouldBeKeptAsChild()
    {
      return (this.mFlags & 0x10) != 0;
    }

    void addChangePayload(Object paramObject)
    {
      if (paramObject == null)
        addFlags(1024);
      do
        return;
      while ((this.mFlags & 0x400) != 0);
      createPayloadsIfNeeded();
      this.mPayloads.add(paramObject);
    }

    void addFlags(int paramInt)
    {
      this.mFlags |= paramInt;
    }

    void clearOldPosition()
    {
      this.mOldPosition = -1;
      this.mPreLayoutPosition = -1;
    }

    void clearPayload()
    {
      if (this.mPayloads != null)
        this.mPayloads.clear();
      this.mFlags &= -1025;
    }

    void clearReturnedFromScrapFlag()
    {
      this.mFlags &= -33;
    }

    void clearTmpDetachFlag()
    {
      this.mFlags &= -257;
    }

    void flagRemovedAndOffsetPosition(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      addFlags(8);
      offsetPosition(paramInt2, paramBoolean);
      this.mPosition = paramInt1;
    }

    public final int getAdapterPosition()
    {
      if (this.mOwnerRecyclerView == null)
        return -1;
      return this.mOwnerRecyclerView.getAdapterPositionFor(this);
    }

    public final long getItemId()
    {
      return this.mItemId;
    }

    public final int getItemViewType()
    {
      return this.mItemViewType;
    }

    public final int getLayoutPosition()
    {
      if (this.mPreLayoutPosition == -1)
        return this.mPosition;
      return this.mPreLayoutPosition;
    }

    public final int getOldPosition()
    {
      return this.mOldPosition;
    }

    @Deprecated
    public final int getPosition()
    {
      if (this.mPreLayoutPosition == -1)
        return this.mPosition;
      return this.mPreLayoutPosition;
    }

    List<Object> getUnmodifiedPayloads()
    {
      if ((this.mFlags & 0x400) == 0)
      {
        if ((this.mPayloads == null) || (this.mPayloads.size() == 0))
          return FULLUPDATE_PAYLOADS;
        return this.mUnmodifiedPayloads;
      }
      return FULLUPDATE_PAYLOADS;
    }

    boolean hasAnyOfTheFlags(int paramInt)
    {
      return (this.mFlags & paramInt) != 0;
    }

    boolean isAdapterPositionUnknown()
    {
      return ((this.mFlags & 0x200) != 0) || (isInvalid());
    }

    boolean isBound()
    {
      return (this.mFlags & 0x1) != 0;
    }

    boolean isInvalid()
    {
      return (this.mFlags & 0x4) != 0;
    }

    public final boolean isRecyclable()
    {
      return ((this.mFlags & 0x10) == 0) && (!ag.b(this.itemView));
    }

    boolean isRemoved()
    {
      return (this.mFlags & 0x8) != 0;
    }

    boolean isScrap()
    {
      return this.mScrapContainer != null;
    }

    boolean isTmpDetached()
    {
      return (this.mFlags & 0x100) != 0;
    }

    boolean isUpdated()
    {
      return (this.mFlags & 0x2) != 0;
    }

    boolean needsUpdate()
    {
      return (this.mFlags & 0x2) != 0;
    }

    void offsetPosition(int paramInt, boolean paramBoolean)
    {
      if (this.mOldPosition == -1)
        this.mOldPosition = this.mPosition;
      if (this.mPreLayoutPosition == -1)
        this.mPreLayoutPosition = this.mPosition;
      if (paramBoolean)
        this.mPreLayoutPosition += paramInt;
      this.mPosition += paramInt;
      if (this.itemView.getLayoutParams() != null)
        ((RecyclerView.LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
    }

    void resetInternal()
    {
      this.mFlags = 0;
      this.mPosition = -1;
      this.mOldPosition = -1;
      this.mItemId = -1L;
      this.mPreLayoutPosition = -1;
      this.mIsRecyclableCount = 0;
      this.mShadowedHolder = null;
      this.mShadowingHolder = null;
      clearPayload();
      this.mWasImportantForAccessibilityBeforeHidden = 0;
      this.mPendingAccessibilityState = -1;
      RecyclerView.clearNestedRecyclerViewIfNotNested(this);
    }

    void saveOldPosition()
    {
      if (this.mOldPosition == -1)
        this.mOldPosition = this.mPosition;
    }

    void setFlags(int paramInt1, int paramInt2)
    {
      this.mFlags = (this.mFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
    }

    public final void setIsRecyclable(boolean paramBoolean)
    {
      int i;
      if (paramBoolean)
      {
        i = this.mIsRecyclableCount - 1;
        this.mIsRecyclableCount = i;
        if (this.mIsRecyclableCount >= 0)
          break label66;
        this.mIsRecyclableCount = 0;
        Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
      }
      label66: 
      do
      {
        return;
        i = this.mIsRecyclableCount + 1;
        break;
        if ((paramBoolean) || (this.mIsRecyclableCount != 1))
          continue;
        this.mFlags |= 16;
        return;
      }
      while ((!paramBoolean) || (this.mIsRecyclableCount != 0));
      this.mFlags &= -17;
    }

    void setScrapContainer(RecyclerView.Recycler paramRecycler, boolean paramBoolean)
    {
      this.mScrapContainer = paramRecycler;
      this.mInChangeScrap = paramBoolean;
    }

    boolean shouldIgnore()
    {
      return (this.mFlags & 0x80) != 0;
    }

    void stopIgnoring()
    {
      this.mFlags &= -129;
    }

    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
      StringBuilder localStringBuilder2;
      if (isScrap())
      {
        localStringBuilder2 = localStringBuilder1.append(" scrap ");
        if (!this.mInChangeScrap)
          break label295;
      }
      label295: for (String str = "[changeScrap]"; ; str = "[attachedScrap]")
      {
        localStringBuilder2.append(str);
        if (isInvalid())
          localStringBuilder1.append(" invalid");
        if (!isBound())
          localStringBuilder1.append(" unbound");
        if (needsUpdate())
          localStringBuilder1.append(" update");
        if (isRemoved())
          localStringBuilder1.append(" removed");
        if (shouldIgnore())
          localStringBuilder1.append(" ignored");
        if (isTmpDetached())
          localStringBuilder1.append(" tmpDetached");
        if (!isRecyclable())
          localStringBuilder1.append(" not recyclable(" + this.mIsRecyclableCount + ")");
        if (isAdapterPositionUnknown())
          localStringBuilder1.append(" undefined adapter position");
        if (this.itemView.getParent() == null)
          localStringBuilder1.append(" no parent");
        localStringBuilder1.append("}");
        return localStringBuilder1.toString();
      }
    }

    void unScrap()
    {
      this.mScrapContainer.unscrapView(this);
    }

    boolean wasReturnedFromScrap()
    {
      return (this.mFlags & 0x20) != 0;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.RecyclerView
 * JD-Core Version:    0.6.0
 */