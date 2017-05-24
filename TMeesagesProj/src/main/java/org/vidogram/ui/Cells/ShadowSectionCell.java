package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;

public class ShadowSectionCell extends View
{
  private int size = 12;

  public ShadowSectionCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837725, "windowBackgroundGrayShadow"));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.size), 1073741824));
  }

  public void setSize(int paramInt)
  {
    this.size = paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ShadowSectionCell
 * JD-Core Version:    0.6.0
 */