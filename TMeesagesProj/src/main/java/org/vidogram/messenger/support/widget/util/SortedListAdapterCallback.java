package org.vidogram.messenger.support.widget.util;

import org.vidogram.messenger.support.util.SortedList.Callback;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;

public abstract class SortedListAdapterCallback<T2> extends SortedList.Callback<T2>
{
  final RecyclerView.Adapter mAdapter;

  public SortedListAdapterCallback(RecyclerView.Adapter paramAdapter)
  {
    this.mAdapter = paramAdapter;
  }

  public void onChanged(int paramInt1, int paramInt2)
  {
    this.mAdapter.notifyItemRangeChanged(paramInt1, paramInt2);
  }

  public void onInserted(int paramInt1, int paramInt2)
  {
    this.mAdapter.notifyItemRangeInserted(paramInt1, paramInt2);
  }

  public void onMoved(int paramInt1, int paramInt2)
  {
    this.mAdapter.notifyItemMoved(paramInt1, paramInt2);
  }

  public void onRemoved(int paramInt1, int paramInt2)
  {
    this.mAdapter.notifyItemRangeRemoved(paramInt1, paramInt2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.util.SortedListAdapterCallback
 * JD-Core Version:    0.6.0
 */