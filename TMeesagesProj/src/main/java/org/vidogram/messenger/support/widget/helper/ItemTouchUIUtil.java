package org.vidogram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.view.View;
import org.vidogram.messenger.support.widget.RecyclerView;

public abstract interface ItemTouchUIUtil
{
  public abstract void clearView(View paramView);

  public abstract void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean);

  public abstract void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean);

  public abstract void onSelected(View paramView);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.helper.ItemTouchUIUtil
 * JD-Core Version:    0.6.0
 */