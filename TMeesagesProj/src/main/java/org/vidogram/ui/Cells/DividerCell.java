package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.View.MeasureSpec;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class DividerCell extends View
{
  public DividerCell(Context paramContext)
  {
    super(paramContext);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    paramCanvas.drawLine(getPaddingLeft(), AndroidUtilities.dp(8.0F), getWidth() - getPaddingRight(), AndroidUtilities.dp(8.0F), Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(16.0F) + 1);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.DividerCell
 * JD-Core Version:    0.6.0
 */