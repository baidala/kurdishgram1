package org.vidogram.messenger.support.widget;

import android.support.v4.a.a;
import android.support.v4.view.ag;
import android.support.v4.view.au;
import android.support.v4.view.ay;
import android.view.View;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DefaultItemAnimator extends SimpleItemAnimator
{
  private static final boolean DEBUG = false;
  ArrayList<RecyclerView.ViewHolder> mAddAnimations = new ArrayList();
  ArrayList<ArrayList<RecyclerView.ViewHolder>> mAdditionsList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mChangeAnimations = new ArrayList();
  ArrayList<ArrayList<ChangeInfo>> mChangesList = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mMoveAnimations = new ArrayList();
  ArrayList<ArrayList<MoveInfo>> mMovesList = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingAdditions = new ArrayList();
  private ArrayList<ChangeInfo> mPendingChanges = new ArrayList();
  private ArrayList<MoveInfo> mPendingMoves = new ArrayList();
  private ArrayList<RecyclerView.ViewHolder> mPendingRemovals = new ArrayList();
  ArrayList<RecyclerView.ViewHolder> mRemoveAnimations = new ArrayList();

  private void animateRemoveImpl(RecyclerView.ViewHolder paramViewHolder)
  {
    au localau = ag.p(paramViewHolder.itemView);
    this.mRemoveAnimations.add(paramViewHolder);
    localau.a(getRemoveDuration()).a(0.0F).a(new VpaListenerAdapter(paramViewHolder, localau)
    {
      public void onAnimationEnd(View paramView)
      {
        this.val$animation.a(null);
        ag.c(paramView, 1.0F);
        DefaultItemAnimator.this.dispatchRemoveFinished(this.val$holder);
        DefaultItemAnimator.this.mRemoveAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(View paramView)
      {
        DefaultItemAnimator.this.dispatchRemoveStarting(this.val$holder);
      }
    }).c();
  }

  private void endChangeAnimation(List<ChangeInfo> paramList, RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ChangeInfo localChangeInfo = (ChangeInfo)paramList.get(i);
      if ((endChangeAnimationIfNecessary(localChangeInfo, paramViewHolder)) && (localChangeInfo.oldHolder == null) && (localChangeInfo.newHolder == null))
        paramList.remove(localChangeInfo);
      i -= 1;
    }
  }

  private void endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo)
  {
    if (paramChangeInfo.oldHolder != null)
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.oldHolder);
    if (paramChangeInfo.newHolder != null)
      endChangeAnimationIfNecessary(paramChangeInfo, paramChangeInfo.newHolder);
  }

  private boolean endChangeAnimationIfNecessary(ChangeInfo paramChangeInfo, RecyclerView.ViewHolder paramViewHolder)
  {
    boolean bool2 = false;
    boolean bool1 = false;
    if (paramChangeInfo.newHolder == paramViewHolder)
      paramChangeInfo.newHolder = null;
    while (true)
    {
      ag.c(paramViewHolder.itemView, 1.0F);
      ag.a(paramViewHolder.itemView, 0.0F);
      ag.b(paramViewHolder.itemView, 0.0F);
      dispatchChangeFinished(paramViewHolder, bool1);
      bool1 = true;
      do
      {
        return bool1;
        bool1 = bool2;
      }
      while (paramChangeInfo.oldHolder != paramViewHolder);
      paramChangeInfo.oldHolder = null;
      bool1 = true;
    }
  }

  private void resetAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    a.a(paramViewHolder.itemView);
    endAnimation(paramViewHolder);
  }

  public boolean animateAdd(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    ag.c(paramViewHolder.itemView, 0.0F);
    this.mPendingAdditions.add(paramViewHolder);
    return true;
  }

  void animateAddImpl(RecyclerView.ViewHolder paramViewHolder)
  {
    au localau = ag.p(paramViewHolder.itemView);
    this.mAddAnimations.add(paramViewHolder);
    localau.a(1.0F).a(getAddDuration()).a(new VpaListenerAdapter(paramViewHolder, localau)
    {
      public void onAnimationCancel(View paramView)
      {
        ag.c(paramView, 1.0F);
      }

      public void onAnimationEnd(View paramView)
      {
        this.val$animation.a(null);
        DefaultItemAnimator.this.dispatchAddFinished(this.val$holder);
        DefaultItemAnimator.this.mAddAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(View paramView)
      {
        DefaultItemAnimator.this.dispatchAddStarting(this.val$holder);
      }
    }).c();
  }

  public boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (paramViewHolder1 == paramViewHolder2)
      return animateMove(paramViewHolder1, paramInt1, paramInt2, paramInt3, paramInt4);
    float f1 = ag.k(paramViewHolder1.itemView);
    float f2 = ag.l(paramViewHolder1.itemView);
    float f3 = ag.e(paramViewHolder1.itemView);
    resetAnimation(paramViewHolder1);
    int i = (int)(paramInt3 - paramInt1 - f1);
    int j = (int)(paramInt4 - paramInt2 - f2);
    ag.a(paramViewHolder1.itemView, f1);
    ag.b(paramViewHolder1.itemView, f2);
    ag.c(paramViewHolder1.itemView, f3);
    if (paramViewHolder2 != null)
    {
      resetAnimation(paramViewHolder2);
      ag.a(paramViewHolder2.itemView, -i);
      ag.b(paramViewHolder2.itemView, -j);
      ag.c(paramViewHolder2.itemView, 0.0F);
    }
    this.mPendingChanges.add(new ChangeInfo(paramViewHolder1, paramViewHolder2, paramInt1, paramInt2, paramInt3, paramInt4));
    return true;
  }

  void animateChangeImpl(ChangeInfo paramChangeInfo)
  {
    View localView = null;
    Object localObject = paramChangeInfo.oldHolder;
    if (localObject == null);
    for (localObject = null; ; localObject = ((RecyclerView.ViewHolder)localObject).itemView)
    {
      RecyclerView.ViewHolder localViewHolder = paramChangeInfo.newHolder;
      if (localViewHolder != null)
        localView = localViewHolder.itemView;
      if (localObject != null)
      {
        localObject = ag.p((View)localObject).a(getChangeDuration());
        this.mChangeAnimations.add(paramChangeInfo.oldHolder);
        ((au)localObject).b(paramChangeInfo.toX - paramChangeInfo.fromX);
        ((au)localObject).c(paramChangeInfo.toY - paramChangeInfo.fromY);
        ((au)localObject).a(0.0F).a(new VpaListenerAdapter(paramChangeInfo, (au)localObject)
        {
          public void onAnimationEnd(View paramView)
          {
            this.val$oldViewAnim.a(null);
            ag.c(paramView, 1.0F);
            ag.a(paramView, 0.0F);
            ag.b(paramView, 0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.oldHolder, true);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.oldHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }

          public void onAnimationStart(View paramView)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.oldHolder, true);
          }
        }).c();
      }
      if (localView != null)
      {
        localObject = ag.p(localView);
        this.mChangeAnimations.add(paramChangeInfo.newHolder);
        ((au)localObject).b(0.0F).c(0.0F).a(getChangeDuration()).a(1.0F).a(new VpaListenerAdapter(paramChangeInfo, (au)localObject, localView)
        {
          public void onAnimationEnd(View paramView)
          {
            this.val$newViewAnimation.a(null);
            ag.c(this.val$newView, 1.0F);
            ag.a(this.val$newView, 0.0F);
            ag.b(this.val$newView, 0.0F);
            DefaultItemAnimator.this.dispatchChangeFinished(this.val$changeInfo.newHolder, false);
            DefaultItemAnimator.this.mChangeAnimations.remove(this.val$changeInfo.newHolder);
            DefaultItemAnimator.this.dispatchFinishedWhenDone();
          }

          public void onAnimationStart(View paramView)
          {
            DefaultItemAnimator.this.dispatchChangeStarting(this.val$changeInfo.newHolder, false);
          }
        }).c();
      }
      return;
    }
  }

  public boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    View localView = paramViewHolder.itemView;
    paramInt1 = (int)(paramInt1 + ag.k(paramViewHolder.itemView));
    paramInt2 = (int)(paramInt2 + ag.l(paramViewHolder.itemView));
    resetAnimation(paramViewHolder);
    int i = paramInt3 - paramInt1;
    int j = paramInt4 - paramInt2;
    if ((i == 0) && (j == 0))
    {
      dispatchMoveFinished(paramViewHolder);
      return false;
    }
    if (i != 0)
      ag.a(localView, -i);
    if (j != 0)
      ag.b(localView, -j);
    this.mPendingMoves.add(new MoveInfo(paramViewHolder, paramInt1, paramInt2, paramInt3, paramInt4));
    return true;
  }

  void animateMoveImpl(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Object localObject = paramViewHolder.itemView;
    paramInt1 = paramInt3 - paramInt1;
    paramInt2 = paramInt4 - paramInt2;
    if (paramInt1 != 0)
      ag.p((View)localObject).b(0.0F);
    if (paramInt2 != 0)
      ag.p((View)localObject).c(0.0F);
    localObject = ag.p((View)localObject);
    this.mMoveAnimations.add(paramViewHolder);
    ((au)localObject).a(getMoveDuration()).a(new VpaListenerAdapter(paramViewHolder, paramInt1, paramInt2, (au)localObject)
    {
      public void onAnimationCancel(View paramView)
      {
        if (this.val$deltaX != 0)
          ag.a(paramView, 0.0F);
        if (this.val$deltaY != 0)
          ag.b(paramView, 0.0F);
      }

      public void onAnimationEnd(View paramView)
      {
        this.val$animation.a(null);
        DefaultItemAnimator.this.dispatchMoveFinished(this.val$holder);
        DefaultItemAnimator.this.mMoveAnimations.remove(this.val$holder);
        DefaultItemAnimator.this.dispatchFinishedWhenDone();
      }

      public void onAnimationStart(View paramView)
      {
        DefaultItemAnimator.this.dispatchMoveStarting(this.val$holder);
      }
    }).c();
  }

  public boolean animateRemove(RecyclerView.ViewHolder paramViewHolder)
  {
    resetAnimation(paramViewHolder);
    this.mPendingRemovals.add(paramViewHolder);
    return true;
  }

  public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder paramViewHolder, List<Object> paramList)
  {
    return (!paramList.isEmpty()) || (super.canReuseUpdatedViewHolder(paramViewHolder, paramList));
  }

  void cancelAll(List<RecyclerView.ViewHolder> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      ag.p(((RecyclerView.ViewHolder)paramList.get(i)).itemView).b();
      i -= 1;
    }
  }

  void dispatchFinishedWhenDone()
  {
    if (!isRunning())
      dispatchAnimationsFinished();
  }

  public void endAnimation(RecyclerView.ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    ag.p(localView).b();
    int i = this.mPendingMoves.size() - 1;
    while (i >= 0)
    {
      if (((MoveInfo)this.mPendingMoves.get(i)).holder == paramViewHolder)
      {
        ag.b(localView, 0.0F);
        ag.a(localView, 0.0F);
        dispatchMoveFinished(paramViewHolder);
        this.mPendingMoves.remove(i);
      }
      i -= 1;
    }
    endChangeAnimation(this.mPendingChanges, paramViewHolder);
    if (this.mPendingRemovals.remove(paramViewHolder))
    {
      ag.c(localView, 1.0F);
      dispatchRemoveFinished(paramViewHolder);
    }
    if (this.mPendingAdditions.remove(paramViewHolder))
    {
      ag.c(localView, 1.0F);
      dispatchAddFinished(paramViewHolder);
    }
    i = this.mChangesList.size() - 1;
    ArrayList localArrayList;
    while (i >= 0)
    {
      localArrayList = (ArrayList)this.mChangesList.get(i);
      endChangeAnimation(localArrayList, paramViewHolder);
      if (localArrayList.isEmpty())
        this.mChangesList.remove(i);
      i -= 1;
    }
    i = this.mMovesList.size() - 1;
    if (i >= 0)
    {
      localArrayList = (ArrayList)this.mMovesList.get(i);
      int j = localArrayList.size() - 1;
      while (true)
      {
        if (j >= 0)
        {
          if (((MoveInfo)localArrayList.get(j)).holder != paramViewHolder)
            break label293;
          ag.b(localView, 0.0F);
          ag.a(localView, 0.0F);
          dispatchMoveFinished(paramViewHolder);
          localArrayList.remove(j);
          if (localArrayList.isEmpty())
            this.mMovesList.remove(i);
        }
        i -= 1;
        break;
        label293: j -= 1;
      }
    }
    i = this.mAdditionsList.size() - 1;
    while (i >= 0)
    {
      localArrayList = (ArrayList)this.mAdditionsList.get(i);
      if (localArrayList.remove(paramViewHolder))
      {
        ag.c(localView, 1.0F);
        dispatchAddFinished(paramViewHolder);
        if (localArrayList.isEmpty())
          this.mAdditionsList.remove(i);
      }
      i -= 1;
    }
    if ((!this.mRemoveAnimations.remove(paramViewHolder)) || ((!this.mAddAnimations.remove(paramViewHolder)) || ((!this.mChangeAnimations.remove(paramViewHolder)) || (this.mMoveAnimations.remove(paramViewHolder)))));
    dispatchFinishedWhenDone();
  }

  public void endAnimations()
  {
    int i = this.mPendingMoves.size() - 1;
    Object localObject1;
    Object localObject2;
    while (i >= 0)
    {
      localObject1 = (MoveInfo)this.mPendingMoves.get(i);
      localObject2 = ((MoveInfo)localObject1).holder.itemView;
      ag.b((View)localObject2, 0.0F);
      ag.a((View)localObject2, 0.0F);
      dispatchMoveFinished(((MoveInfo)localObject1).holder);
      this.mPendingMoves.remove(i);
      i -= 1;
    }
    i = this.mPendingRemovals.size() - 1;
    while (i >= 0)
    {
      dispatchRemoveFinished((RecyclerView.ViewHolder)this.mPendingRemovals.get(i));
      this.mPendingRemovals.remove(i);
      i -= 1;
    }
    i = this.mPendingAdditions.size() - 1;
    while (i >= 0)
    {
      localObject1 = (RecyclerView.ViewHolder)this.mPendingAdditions.get(i);
      ag.c(((RecyclerView.ViewHolder)localObject1).itemView, 1.0F);
      dispatchAddFinished((RecyclerView.ViewHolder)localObject1);
      this.mPendingAdditions.remove(i);
      i -= 1;
    }
    i = this.mPendingChanges.size() - 1;
    while (i >= 0)
    {
      endChangeAnimationIfNecessary((ChangeInfo)this.mPendingChanges.get(i));
      i -= 1;
    }
    this.mPendingChanges.clear();
    if (!isRunning())
      return;
    i = this.mMovesList.size() - 1;
    int j;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mMovesList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        localObject2 = (MoveInfo)((ArrayList)localObject1).get(j);
        View localView = ((MoveInfo)localObject2).holder.itemView;
        ag.b(localView, 0.0F);
        ag.a(localView, 0.0F);
        dispatchMoveFinished(((MoveInfo)localObject2).holder);
        ((ArrayList)localObject1).remove(j);
        if (((ArrayList)localObject1).isEmpty())
          this.mMovesList.remove(localObject1);
        j -= 1;
      }
      i -= 1;
    }
    i = this.mAdditionsList.size() - 1;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mAdditionsList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        localObject2 = (RecyclerView.ViewHolder)((ArrayList)localObject1).get(j);
        ag.c(((RecyclerView.ViewHolder)localObject2).itemView, 1.0F);
        dispatchAddFinished((RecyclerView.ViewHolder)localObject2);
        ((ArrayList)localObject1).remove(j);
        if (((ArrayList)localObject1).isEmpty())
          this.mAdditionsList.remove(localObject1);
        j -= 1;
      }
      i -= 1;
    }
    i = this.mChangesList.size() - 1;
    while (i >= 0)
    {
      localObject1 = (ArrayList)this.mChangesList.get(i);
      j = ((ArrayList)localObject1).size() - 1;
      while (j >= 0)
      {
        endChangeAnimationIfNecessary((ChangeInfo)((ArrayList)localObject1).get(j));
        if (((ArrayList)localObject1).isEmpty())
          this.mChangesList.remove(localObject1);
        j -= 1;
      }
      i -= 1;
    }
    cancelAll(this.mRemoveAnimations);
    cancelAll(this.mMoveAnimations);
    cancelAll(this.mAddAnimations);
    cancelAll(this.mChangeAnimations);
    dispatchAnimationsFinished();
  }

  public boolean isRunning()
  {
    return (!this.mPendingAdditions.isEmpty()) || (!this.mPendingChanges.isEmpty()) || (!this.mPendingMoves.isEmpty()) || (!this.mPendingRemovals.isEmpty()) || (!this.mMoveAnimations.isEmpty()) || (!this.mRemoveAnimations.isEmpty()) || (!this.mAddAnimations.isEmpty()) || (!this.mChangeAnimations.isEmpty()) || (!this.mMovesList.isEmpty()) || (!this.mAdditionsList.isEmpty()) || (!this.mChangesList.isEmpty());
  }

  public void runPendingAnimations()
  {
    int i;
    int j;
    label24: int k;
    if (!this.mPendingRemovals.isEmpty())
    {
      i = 1;
      if (this.mPendingMoves.isEmpty())
        break label72;
      j = 1;
      if (this.mPendingChanges.isEmpty())
        break label77;
      k = 1;
      label36: if (this.mPendingAdditions.isEmpty())
        break label82;
    }
    label72: label77: label82: for (int m = 1; ; m = 0)
    {
      if ((i != 0) || (j != 0) || (m != 0) || (k != 0))
        break label88;
      return;
      i = 0;
      break;
      j = 0;
      break label24;
      k = 0;
      break label36;
    }
    label88: Object localObject1 = this.mPendingRemovals.iterator();
    while (((Iterator)localObject1).hasNext())
      animateRemoveImpl((RecyclerView.ViewHolder)((Iterator)localObject1).next());
    this.mPendingRemovals.clear();
    Object localObject2;
    label211: long l1;
    label291: label366: long l2;
    label376: long l3;
    if (j != 0)
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).addAll(this.mPendingMoves);
      this.mMovesList.add(localObject1);
      this.mPendingMoves.clear();
      localObject2 = new Runnable((ArrayList)localObject1)
      {
        public void run()
        {
          Iterator localIterator = this.val$moves.iterator();
          while (localIterator.hasNext())
          {
            DefaultItemAnimator.MoveInfo localMoveInfo = (DefaultItemAnimator.MoveInfo)localIterator.next();
            DefaultItemAnimator.this.animateMoveImpl(localMoveInfo.holder, localMoveInfo.fromX, localMoveInfo.fromY, localMoveInfo.toX, localMoveInfo.toY);
          }
          this.val$moves.clear();
          DefaultItemAnimator.this.mMovesList.remove(this.val$moves);
        }
      };
      if (i != 0)
        ag.a(((MoveInfo)((ArrayList)localObject1).get(0)).holder.itemView, (Runnable)localObject2, getRemoveDuration());
    }
    else
    {
      if (k != 0)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).addAll(this.mPendingChanges);
        this.mChangesList.add(localObject1);
        this.mPendingChanges.clear();
        localObject2 = new Runnable((ArrayList)localObject1)
        {
          public void run()
          {
            Iterator localIterator = this.val$changes.iterator();
            while (localIterator.hasNext())
            {
              DefaultItemAnimator.ChangeInfo localChangeInfo = (DefaultItemAnimator.ChangeInfo)localIterator.next();
              DefaultItemAnimator.this.animateChangeImpl(localChangeInfo);
            }
            this.val$changes.clear();
            DefaultItemAnimator.this.mChangesList.remove(this.val$changes);
          }
        };
        if (i == 0)
          break label428;
        ag.a(((ChangeInfo)((ArrayList)localObject1).get(0)).oldHolder.itemView, (Runnable)localObject2, getRemoveDuration());
      }
      if (m == 0)
        break label436;
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).addAll(this.mPendingAdditions);
      this.mAdditionsList.add(localObject1);
      this.mPendingAdditions.clear();
      localObject2 = new Runnable((ArrayList)localObject1)
      {
        public void run()
        {
          Iterator localIterator = this.val$additions.iterator();
          while (localIterator.hasNext())
          {
            RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localIterator.next();
            DefaultItemAnimator.this.animateAddImpl(localViewHolder);
          }
          this.val$additions.clear();
          DefaultItemAnimator.this.mAdditionsList.remove(this.val$additions);
        }
      };
      if ((i == 0) && (j == 0) && (k == 0))
        break label456;
      if (i == 0)
        break label438;
      l1 = getRemoveDuration();
      if (j == 0)
        break label444;
      l2 = getMoveDuration();
      if (k == 0)
        break label450;
      l3 = getChangeDuration();
    }
    while (true)
    {
      l2 = Math.max(l2, l3);
      ag.a(((RecyclerView.ViewHolder)((ArrayList)localObject1).get(0)).itemView, (Runnable)localObject2, l1 + l2);
      return;
      ((Runnable)localObject2).run();
      break label211;
      label428: ((Runnable)localObject2).run();
      break label291;
      label436: break;
      label438: l1 = 0L;
      break label366;
      label444: l2 = 0L;
      break label376;
      label450: l3 = 0L;
    }
    label456: ((Runnable)localObject2).run();
  }

  private static class ChangeInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder newHolder;
    public RecyclerView.ViewHolder oldHolder;
    public int toX;
    public int toY;

    private ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2)
    {
      this.oldHolder = paramViewHolder1;
      this.newHolder = paramViewHolder2;
    }

    ChangeInfo(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this(paramViewHolder1, paramViewHolder2);
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }

    public String toString()
    {
      return "ChangeInfo{oldHolder=" + this.oldHolder + ", newHolder=" + this.newHolder + ", fromX=" + this.fromX + ", fromY=" + this.fromY + ", toX=" + this.toX + ", toY=" + this.toY + '}';
    }
  }

  private static class MoveInfo
  {
    public int fromX;
    public int fromY;
    public RecyclerView.ViewHolder holder;
    public int toX;
    public int toY;

    MoveInfo(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.holder = paramViewHolder;
      this.fromX = paramInt1;
      this.fromY = paramInt2;
      this.toX = paramInt3;
      this.toY = paramInt4;
    }
  }

  private static class VpaListenerAdapter
    implements ay
  {
    public void onAnimationCancel(View paramView)
    {
    }

    public void onAnimationEnd(View paramView)
    {
    }

    public void onAnimationStart(View paramView)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.DefaultItemAnimator
 * JD-Core Version:    0.6.0
 */