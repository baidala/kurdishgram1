package org.vidogram.messenger.support.widget;

import android.os.Bundle;
import android.support.v4.view.a.c;
import android.support.v4.view.b;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;

public class RecyclerViewAccessibilityDelegate extends b
{
  final b mItemDelegate = new b()
  {
    public void onInitializeAccessibilityNodeInfo(View paramView, c paramc)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramc);
      if ((!RecyclerViewAccessibilityDelegate.this.shouldIgnore()) && (RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null))
        RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfoForItem(paramView, paramc);
    }

    public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
    {
      if (super.performAccessibilityAction(paramView, paramInt, paramBundle))
        return true;
      if ((!RecyclerViewAccessibilityDelegate.this.shouldIgnore()) && (RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager() != null))
        return RecyclerViewAccessibilityDelegate.this.mRecyclerView.getLayoutManager().performAccessibilityActionForItem(paramView, paramInt, paramBundle);
      return false;
    }
  };
  final RecyclerView mRecyclerView;

  public RecyclerViewAccessibilityDelegate(RecyclerView paramRecyclerView)
  {
    this.mRecyclerView = paramRecyclerView;
  }

  public b getItemDelegate()
  {
    return this.mItemDelegate;
  }

  public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
    paramAccessibilityEvent.setClassName(RecyclerView.class.getName());
    if (((paramView instanceof RecyclerView)) && (!shouldIgnore()))
    {
      paramView = (RecyclerView)paramView;
      if (paramView.getLayoutManager() != null)
        paramView.getLayoutManager().onInitializeAccessibilityEvent(paramAccessibilityEvent);
    }
  }

  public void onInitializeAccessibilityNodeInfo(View paramView, c paramc)
  {
    super.onInitializeAccessibilityNodeInfo(paramView, paramc);
    paramc.a(RecyclerView.class.getName());
    if ((!shouldIgnore()) && (this.mRecyclerView.getLayoutManager() != null))
      this.mRecyclerView.getLayoutManager().onInitializeAccessibilityNodeInfo(paramc);
  }

  public boolean performAccessibilityAction(View paramView, int paramInt, Bundle paramBundle)
  {
    if (super.performAccessibilityAction(paramView, paramInt, paramBundle))
      return true;
    if ((!shouldIgnore()) && (this.mRecyclerView.getLayoutManager() != null))
      return this.mRecyclerView.getLayoutManager().performAccessibilityAction(paramInt, paramBundle);
    return false;
  }

  boolean shouldIgnore()
  {
    return this.mRecyclerView.hasPendingAdapterUpdates();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.RecyclerViewAccessibilityDelegate
 * JD-Core Version:    0.6.0
 */