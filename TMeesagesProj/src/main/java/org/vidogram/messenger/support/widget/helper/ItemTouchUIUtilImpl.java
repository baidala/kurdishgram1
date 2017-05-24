package org.vidogram.messenger.support.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ag;
import android.view.View;
import org.vidogram.messenger.support.widget.RecyclerView;

class ItemTouchUIUtilImpl
{
  static class Gingerbread
    implements ItemTouchUIUtil
  {
    private void draw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2)
    {
      paramCanvas.save();
      paramCanvas.translate(paramFloat1, paramFloat2);
      paramRecyclerView.drawChild(paramCanvas, paramView, 0L);
      paramCanvas.restore();
    }

    public void clearView(View paramView)
    {
      paramView.setVisibility(0);
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt != 2)
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
    }

    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt == 2)
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
    }

    public void onSelected(View paramView)
    {
      paramView.setVisibility(4);
    }
  }

  static class Honeycomb
    implements ItemTouchUIUtil
  {
    public void clearView(View paramView)
    {
      ag.a(paramView, 0.0F);
      ag.b(paramView, 0.0F);
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      ag.a(paramView, paramFloat1);
      ag.b(paramView, paramFloat2);
    }

    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
    }

    public void onSelected(View paramView)
    {
    }
  }

  static class Lollipop extends ItemTouchUIUtilImpl.Honeycomb
  {
    private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
    {
      int j = paramRecyclerView.getChildCount();
      int i = 0;
      float f1 = 0.0F;
      if (i < j)
      {
        View localView = paramRecyclerView.getChildAt(i);
        float f2;
        if (localView == paramView)
          f2 = f1;
        while (true)
        {
          i += 1;
          f1 = f2;
          break;
          float f3 = ag.q(localView);
          f2 = f1;
          if (f3 <= f1)
            continue;
          f2 = f3;
        }
      }
      return f1;
    }

    public void clearView(View paramView)
    {
      Object localObject = paramView.getTag();
      if ((localObject != null) && ((localObject instanceof Float)))
        ag.d(paramView, ((Float)localObject).floatValue());
      paramView.setTag(null);
      super.clearView(paramView);
    }

    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if ((paramBoolean) && (paramView.getTag() == null))
      {
        float f = ag.q(paramView);
        ag.d(paramView, 1.0F + findMaxElevation(paramRecyclerView, paramView));
        paramView.setTag(Float.valueOf(f));
      }
      super.onDraw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.helper.ItemTouchUIUtilImpl
 * JD-Core Version:    0.6.0
 */