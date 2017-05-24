package org.vidogram.messenger.support.widget;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.view.View;

public class PagerSnapHelper extends SnapHelper
{
  private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
  private OrientationHelper mHorizontalHelper;
  private OrientationHelper mVerticalHelper;

  private int distanceToCenter(RecyclerView.LayoutManager paramLayoutManager, View paramView, OrientationHelper paramOrientationHelper)
  {
    int j = paramOrientationHelper.getDecoratedStart(paramView);
    int k = paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding());
    for (int i = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2; ; i = paramOrientationHelper.getEnd() / 2)
      return k + j - i;
  }

  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int n = paramLayoutManager.getChildCount();
    if (n == 0);
    int j;
    int k;
    while (true)
    {
      return localObject2;
      if (!paramLayoutManager.getClipToPadding())
        break;
      j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;
      int i = 2147483647;
      k = 0;
      label46: localObject2 = localObject1;
      if (k >= n)
        continue;
      localObject2 = paramLayoutManager.getChildAt(k);
      int m = Math.abs(paramOrientationHelper.getDecoratedStart((View)localObject2) + paramOrientationHelper.getDecoratedMeasurement((View)localObject2) / 2 - j);
      if (m >= i)
        break label121;
      localObject1 = localObject2;
      i = m;
    }
    label121: 
    while (true)
    {
      k += 1;
      break label46;
      j = paramOrientationHelper.getEnd() / 2;
      break;
    }
  }

  private View findStartView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    int m = paramLayoutManager.getChildCount();
    if (m == 0);
    int i;
    int j;
    do
    {
      return localObject2;
      i = 2147483647;
      j = 0;
      localObject2 = localObject1;
    }
    while (j >= m);
    localObject2 = paramLayoutManager.getChildAt(j);
    int k = paramOrientationHelper.getDecoratedStart((View)localObject2);
    if (k < i)
    {
      localObject1 = localObject2;
      i = k;
    }
    while (true)
    {
      j += 1;
      break;
    }
  }

  private OrientationHelper getHorizontalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mHorizontalHelper == null) || (this.mHorizontalHelper.mLayoutManager != paramLayoutManager))
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    return this.mHorizontalHelper;
  }

  private OrientationHelper getVerticalHelper(RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mVerticalHelper == null) || (this.mVerticalHelper.mLayoutManager != paramLayoutManager))
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    return this.mVerticalHelper;
  }

  public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager paramLayoutManager, View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally())
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    while (paramLayoutManager.canScrollVertically())
    {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
      arrayOfInt[0] = 0;
    }
    arrayOfInt[1] = 0;
    return arrayOfInt;
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

      protected int calculateTimeForScrolling(int paramInt)
      {
        return Math.min(100, super.calculateTimeForScrolling(paramInt));
      }

      protected void onTargetFound(View paramView, RecyclerView.State paramState, RecyclerView.SmoothScroller.Action paramAction)
      {
        paramView = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), paramView);
        int i = paramView[0];
        int j = paramView[1];
        int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
        if (k > 0)
          paramAction.update(i, j, k, this.mDecelerateInterpolator);
      }
    };
  }

  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically())
      return findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    if (paramLayoutManager.canScrollHorizontally())
      return findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
    return null;
  }

  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    int i = 0;
    int k = paramLayoutManager.getItemCount();
    if (k == 0);
    View localView;
    label38: int j;
    while (true)
    {
      return -1;
      localView = null;
      if (!paramLayoutManager.canScrollVertically())
        break;
      localView = findStartView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
      if (localView == null)
        break label157;
      j = paramLayoutManager.getPosition(localView);
      if (j == -1)
        continue;
      if (!paramLayoutManager.canScrollHorizontally())
        break label164;
      if (paramInt1 <= 0)
        break label159;
      paramInt1 = 1;
    }
    while (true)
    {
      paramInt2 = i;
      if ((paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider))
      {
        paramLayoutManager = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(k - 1);
        paramInt2 = i;
        if (paramLayoutManager != null)
          if (paramLayoutManager.x >= 0.0F)
          {
            paramInt2 = i;
            if (paramLayoutManager.y >= 0.0F);
          }
          else
          {
            paramInt2 = 1;
          }
      }
      if (paramInt2 == 0)
        break label181;
      if (paramInt1 == 0)
        break label178;
      return j - 1;
      if (!paramLayoutManager.canScrollHorizontally())
        break label38;
      localView = findStartView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
      break label38;
      label157: break;
      label159: paramInt1 = 0;
      continue;
      label164: if (paramInt2 > 0)
      {
        paramInt1 = 1;
        continue;
      }
      paramInt1 = 0;
    }
    label178: return j;
    label181: if (paramInt1 != 0)
      return j + 1;
    return j;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.PagerSnapHelper
 * JD-Core Version:    0.6.0
 */