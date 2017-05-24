package org.vidogram.messenger.support.widget;

import android.support.v4.e.j;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

final class GapWorker
  implements Runnable
{
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  static Comparator<Task> sTaskComparator = new Comparator()
  {
    public int compare(GapWorker.Task paramTask1, GapWorker.Task paramTask2)
    {
      int k = -1;
      int m = 1;
      int i;
      int j;
      if (paramTask1.view == null)
      {
        i = 1;
        if (paramTask2.view != null)
          break label48;
        j = 1;
        label25: if (i == j)
          break label56;
        if (paramTask1.view != null)
          break label54;
        i = m;
      }
      label48: label54: label56: 
      do
      {
        do
        {
          return i;
          i = 0;
          break;
          j = 0;
          break label25;
          return -1;
          if (paramTask1.immediate != paramTask2.immediate)
          {
            if (paramTask1.immediate);
            for (i = k; ; i = 1)
              return i;
          }
          j = paramTask2.viewVelocity - paramTask1.viewVelocity;
          i = j;
        }
        while (j != 0);
        j = paramTask1.distanceToItem - paramTask2.distanceToItem;
        i = j;
      }
      while (j != 0);
      return 0;
    }
  };
  long mFrameIntervalNs;
  long mPostTimeNs;
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  private ArrayList<Task> mTasks = new ArrayList();

  private void buildTaskList()
  {
    int m = this.mRecyclerViews.size();
    int i = 0;
    int j = 0;
    Object localObject;
    while (i < m)
    {
      localObject = (RecyclerView)this.mRecyclerViews.get(i);
      ((RecyclerView)localObject).mPrefetchRegistry.collectPrefetchPositionsFromView((RecyclerView)localObject, false);
      j += ((RecyclerView)localObject).mPrefetchRegistry.mCount;
      i += 1;
    }
    this.mTasks.ensureCapacity(j);
    j = 0;
    i = 0;
    while (j < m)
    {
      RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(j);
      LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl = localRecyclerView.mPrefetchRegistry;
      int n = Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDy);
      int k = 0;
      if (k < localLayoutPrefetchRegistryImpl.mCount * 2)
      {
        label161: int i1;
        if (i >= this.mTasks.size())
        {
          localObject = new Task();
          this.mTasks.add(localObject);
          i1 = localLayoutPrefetchRegistryImpl.mPrefetchArray[(k + 1)];
          if (i1 > n)
            break label249;
        }
        label249: for (boolean bool = true; ; bool = false)
        {
          ((Task)localObject).immediate = bool;
          ((Task)localObject).viewVelocity = n;
          ((Task)localObject).distanceToItem = i1;
          ((Task)localObject).view = localRecyclerView;
          ((Task)localObject).position = localLayoutPrefetchRegistryImpl.mPrefetchArray[k];
          i += 1;
          k += 2;
          break;
          localObject = (Task)this.mTasks.get(i);
          break label161;
        }
      }
      j += 1;
    }
    Collections.sort(this.mTasks, sTaskComparator);
  }

  private void flushTaskWithDeadline(Task paramTask, long paramLong)
  {
    long l;
    if (paramTask.immediate)
      l = 9223372036854775807L;
    while (true)
    {
      paramTask = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
      if ((paramTask != null) && (paramTask.mNestedRecyclerView != null))
        prefetchInnerRecyclerViewWithDeadline((RecyclerView)paramTask.mNestedRecyclerView.get(), paramLong);
      return;
      l = paramLong;
    }
  }

  private void flushTasksWithDeadline(long paramLong)
  {
    int i = 0;
    while (true)
    {
      Task localTask;
      if (i < this.mTasks.size())
      {
        localTask = (Task)this.mTasks.get(i);
        if (localTask.view != null);
      }
      else
      {
        return;
      }
      flushTaskWithDeadline(localTask, paramLong);
      localTask.clear();
      i += 1;
    }
  }

  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt)
  {
    int m = 0;
    int j = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(i));
        if ((localViewHolder.mPosition == paramInt) && (!localViewHolder.isInvalid()))
          k = 1;
      }
      else
      {
        return k;
      }
      i += 1;
    }
  }

  private void prefetchInnerRecyclerViewWithDeadline(RecyclerView paramRecyclerView, long paramLong)
  {
    if (paramRecyclerView == null);
    LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    do
    {
      return;
      if ((paramRecyclerView.mDataSetHasChangedAfterLayout) && (paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0))
        paramRecyclerView.removeAndRecycleViews();
      localLayoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
      localLayoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
    }
    while (localLayoutPrefetchRegistryImpl.mCount == 0);
    try
    {
      j.a("RV Nested Prefetch");
      paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
      int i = 0;
      while (i < localLayoutPrefetchRegistryImpl.mCount * 2)
      {
        prefetchPositionWithDeadline(paramRecyclerView, localLayoutPrefetchRegistryImpl.mPrefetchArray[i], paramLong);
        i += 2;
      }
      return;
    }
    finally
    {
      j.a();
    }
    throw paramRecyclerView;
  }

  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong)
  {
    if (isPrefetchPositionAttached(paramRecyclerView, paramInt))
      paramRecyclerView = null;
    RecyclerView.Recycler localRecycler;
    RecyclerView.ViewHolder localViewHolder;
    do
    {
      return paramRecyclerView;
      localRecycler = paramRecyclerView.mRecycler;
      localViewHolder = localRecycler.tryGetViewHolderForPositionByDeadline(paramInt, false, paramLong);
      paramRecyclerView = localViewHolder;
    }
    while (localViewHolder == null);
    if (localViewHolder.isBound())
    {
      localRecycler.recycleView(localViewHolder.itemView);
      return localViewHolder;
    }
    localRecycler.addViewHolderToRecycledViewPool(localViewHolder, false);
    return localViewHolder;
  }

  public void add(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.add(paramRecyclerView);
  }

  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((paramRecyclerView.isAttachedToWindow()) && (this.mPostTimeNs == 0L))
    {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    }
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }

  void prefetch(long paramLong)
  {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }

  public void remove(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.remove(paramRecyclerView);
  }

  public void run()
  {
    try
    {
      j.a("RV Prefetch");
      boolean bool = this.mRecyclerViews.isEmpty();
      if (bool)
        return;
      long l = TimeUnit.MILLISECONDS.toNanos(((RecyclerView)this.mRecyclerViews.get(0)).getDrawingTime());
      if (l == 0L)
        return;
      prefetch(l + this.mFrameIntervalNs);
      return;
    }
    finally
    {
      this.mPostTimeNs = 0L;
      j.a();
    }
    throw localObject;
  }

  static class LayoutPrefetchRegistryImpl
    implements RecyclerView.LayoutManager.LayoutPrefetchRegistry
  {
    int mCount;
    int[] mPrefetchArray;
    int mPrefetchDx;
    int mPrefetchDy;

    public void addPosition(int paramInt1, int paramInt2)
    {
      if (paramInt2 < 0)
        throw new IllegalArgumentException("Pixel distance must be non-negative");
      int i = this.mCount * 2;
      if (this.mPrefetchArray == null)
      {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      }
      while (true)
      {
        this.mPrefetchArray[i] = paramInt1;
        this.mPrefetchArray[(i + 1)] = paramInt2;
        this.mCount += 1;
        return;
        if (i < this.mPrefetchArray.length)
          continue;
        int[] arrayOfInt = this.mPrefetchArray;
        this.mPrefetchArray = new int[i * 2];
        System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
      }
    }

    void clearPrefetchPositions()
    {
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1);
    }

    void collectPrefetchPositionsFromView(RecyclerView paramRecyclerView, boolean paramBoolean)
    {
      this.mCount = 0;
      if (this.mPrefetchArray != null)
        Arrays.fill(this.mPrefetchArray, -1);
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.mLayout;
      if ((paramRecyclerView.mAdapter != null) && (localLayoutManager != null) && (localLayoutManager.isItemPrefetchEnabled()))
      {
        if (!paramBoolean)
          break label101;
        if (!paramRecyclerView.mAdapterHelper.hasPendingUpdates())
          localLayoutManager.collectInitialPrefetchPositions(paramRecyclerView.mAdapter.getItemCount(), this);
      }
      while (true)
      {
        if (this.mCount > localLayoutManager.mPrefetchMaxCountObserved)
        {
          localLayoutManager.mPrefetchMaxCountObserved = this.mCount;
          localLayoutManager.mPrefetchMaxObservedInInitialPrefetch = paramBoolean;
          paramRecyclerView.mRecycler.updateViewCacheSize();
        }
        return;
        label101: if (paramRecyclerView.hasPendingAdapterUpdates())
          continue;
        localLayoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, paramRecyclerView.mState, this);
      }
    }

    boolean lastPrefetchIncludedPosition(int paramInt)
    {
      int m = 0;
      int k = m;
      int j;
      int i;
      if (this.mPrefetchArray != null)
      {
        j = this.mCount;
        i = 0;
      }
      while (true)
      {
        k = m;
        if (i < j * 2)
        {
          if (this.mPrefetchArray[i] == paramInt)
            k = 1;
        }
        else
          return k;
        i += 2;
      }
    }

    void setPrefetchVector(int paramInt1, int paramInt2)
    {
      this.mPrefetchDx = paramInt1;
      this.mPrefetchDy = paramInt2;
    }
  }

  static class Task
  {
    public int distanceToItem;
    public boolean immediate;
    public int position;
    public RecyclerView view;
    public int viewVelocity;

    public void clear()
    {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.GapWorker
 * JD-Core Version:    0.6.0
 */