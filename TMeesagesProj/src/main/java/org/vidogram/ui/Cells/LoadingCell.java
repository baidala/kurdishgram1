package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadialProgressView;

public class LoadingCell extends FrameLayout
{
  private RadialProgressView progressBar;

  public LoadingCell(Context paramContext)
  {
    super(paramContext);
    this.progressBar = new RadialProgressView(paramContext);
    addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0F), 1073741824));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.LoadingCell
 * JD-Core Version:    0.6.0
 */