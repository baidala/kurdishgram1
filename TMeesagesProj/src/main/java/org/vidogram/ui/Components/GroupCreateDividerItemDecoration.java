package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.view.View;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.ui.ActionBar.Theme;

public class GroupCreateDividerItemDecoration extends RecyclerView.ItemDecoration
{
  private boolean searching;

  public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    super.getItemOffsets(paramRect, paramView, paramRecyclerView, paramState);
    int i = paramRecyclerView.getChildAdapterPosition(paramView);
    if ((i == 0) || ((!this.searching) && (i == 1)))
      return;
    paramRect.top = 1;
  }

  public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
  {
    int k = paramRecyclerView.getWidth();
    int m = paramRecyclerView.getChildCount();
    int i = 0;
    while (i < m - 1)
    {
      paramState = paramRecyclerView.getChildAt(i);
      if (paramRecyclerView.getChildAdapterPosition(paramState) == 0)
      {
        i += 1;
        continue;
      }
      int n = paramState.getBottom();
      float f1;
      label63: float f2;
      if (LocaleController.isRTL)
      {
        f1 = 0.0F;
        f2 = n;
        if (!LocaleController.isRTL)
          break label115;
      }
      label115: for (int j = AndroidUtilities.dp(72.0F); ; j = 0)
      {
        paramCanvas.drawLine(f1, f2, k - j, n, Theme.dividerPaint);
        break;
        f1 = AndroidUtilities.dp(72.0F);
        break label63;
      }
    }
  }

  public void setSearching(boolean paramBoolean)
  {
    this.searching = paramBoolean;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.GroupCreateDividerItemDecoration
 * JD-Core Version:    0.6.0
 */