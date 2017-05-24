package org.vidogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.Components.LayoutHelper;

@SuppressLint({"NewApi"})
public class PhotoAttachCameraCell extends FrameLayout
{
  public PhotoAttachCameraCell(Context paramContext)
  {
    super(paramContext);
    paramContext = new ImageView(paramContext);
    paramContext.setScaleType(ImageView.ScaleType.CENTER);
    paramContext.setImageResource(2130837873);
    paramContext.setBackgroundColor(-16777216);
    addView(paramContext, LayoutHelper.createFrame(80, 80.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0F), 1073741824));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.PhotoAttachCameraCell
 * JD-Core Version:    0.6.0
 */