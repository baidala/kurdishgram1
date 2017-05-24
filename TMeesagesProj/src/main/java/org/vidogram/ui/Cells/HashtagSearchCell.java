package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View.MeasureSpec;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class HashtagSearchCell extends TextView
{
  private boolean needDivider;

  public HashtagSearchCell(Context paramContext)
  {
    super(paramContext);
    setGravity(16);
    setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
    setTextSize(1, 17.0F);
    setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if (this.needDivider)
      paramCanvas.drawLine(0.0F, getHeight() - 1, getWidth(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(48.0F) + 1);
  }

  public void setNeedDivider(boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.HashtagSearchCell
 * JD-Core Version:    0.6.0
 */