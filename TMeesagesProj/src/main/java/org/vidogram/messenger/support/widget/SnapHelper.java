package org.vidogram.messenger.support.widget;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

public abstract class SnapHelper extends RecyclerView.OnFlingListener
{
  static final float MILLISECONDS_PER_INCH = 100.0F;
  private Scroller mGravityScroller;
  RecyclerView mRecyclerView;
  private final RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener()
  {
    boolean mScrolled = false;

    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
    {
      super.onScrollStateChanged(paramRecyclerView, paramInt);
      if ((paramInt == 0) && (this.mScrolled))
      {
        this.mScrolled = false;
        SnapHelper.this.snapToTargetExistingView();
      }
    }

    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0))
        this.mScrolled = true;
    }
  };

  private void destroyCallbacks()
  {
    this.mRecyclerView.removeOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(null);
  }

  private void setupCallbacks()
  {
    if (this.mRecyclerView.getOnFlingListener() != null)
      throw new IllegalStateException("An instance of OnFlingListener already set.");
    this.mRecyclerView.addOnScrollListener(this.mScrollListener);
    this.mRecyclerView.setOnFlingListener(this);
  }

  private boolean snapFromFling(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider));
    LinearSmoothScroller localLinearSmoothScroller;
    do
    {
      do
      {
        return false;
        localLinearSmoothScroller = createSnapScroller(paramLayoutManager);
      }
      while (localLinearSmoothScroller == null);
      paramInt1 = findTargetSnapPosition(paramLayoutManager, paramInt1, paramInt2);
    }
    while (paramInt1 == -1);
    localLinearSmoothScroller.setTargetPosition(paramInt1);
    paramLayoutManager.startSmoothScroll(localLinearSmoothScroller);
    return true;
  }

  public void attachToRecyclerView(RecyclerView paramRecyclerView)
  {
    if (this.mRecyclerView == paramRecyclerView);
    do
    {
      return;
      if (this.mRecyclerView != null)
        destroyCallbacks();
      this.mRecyclerView = paramRecyclerView;
    }
    while (this.mRecyclerView == null);
    setupCallbacks();
    this.mGravityScroller = new Scroller(this.mRecyclerView.getContext(), new DecelerateInterpolator());
    snapToTargetExistingView();
  }

  public abstract int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager paramLayoutManager, View paramView);

  public int[] calculateScrollDistance(int paramInt1, int paramInt2)
  {
    this.mGravityScroller.fling(0, 0, paramInt1, paramInt2, -2147483648, 2147483647, -2147483648, 2147483647);
    return new int[] { this.mGravityScroller.getFinalX(), this.mGravityScroller.getFinalY() };
  }

  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
      return null;
    return new LinearSmoothScroller(this.mRecyclerView.getContext())
    {
      protected float calculateSpeedPerPixel(DisplayMetrics paramDisplayMetrics)
      {
        return 100.0F / paramDisplayMetrics.densityDpi;
      }

      protected void onTargetFound(View paramView, RecyclerView.State paramState, RecyclerView.SmoothScroller.Action paramAction)
      {
        paramView = SnapHelper.this.calculateDistanceToFinalSnap(SnapHelper.this.mRecyclerView.getLayoutManager(), paramView);
        int i = paramView[0];
        int j = paramView[1];
        int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
        if (k > 0)
          paramAction.update(i, j, k, this.mDecelerateInterpolator);
      }
    };
  }

  public abstract View findSnapView(RecyclerView.LayoutManager paramLayoutManager);

  public abstract int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2);

  public boolean onFling(int paramInt1, int paramInt2)
  {
    RecyclerView.LayoutManager localLayoutManager = this.mRecyclerView.getLayoutManager();
    if (localLayoutManager == null);
    int i;
    do
    {
      do
        return false;
      while (this.mRecyclerView.getAdapter() == null);
      i = this.mRecyclerView.getMinFlingVelocity();
    }
    while (((Math.abs(paramInt2) <= i) && (Math.abs(paramInt1) <= i)) || (!snapFromFling(localLayoutManager, paramInt1, paramInt2)));
    return true;
  }

  void snapToTargetExistingView()
  {
    if (this.mRecyclerView == null);
    Object localObject;
    do
    {
      View localView;
      do
      {
        do
        {
          return;
          localObject = this.mRecyclerView.getLayoutManager();
        }
        while (localObject == null);
        localView = findSnapView((RecyclerView.LayoutManager)localObject);
      }
      while (localView == null);
      localObject = calculateDistanceToFinalSnap((RecyclerView.LayoutManager)localObject, localView);
    }
    while ((localObject[0] == 0) && (localObject[1] == 0));
    this.mRecyclerView.smoothScrollBy(localObject[0], localObject[1]);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.SnapHelper
 * JD-Core Version:    0.6.0
 */