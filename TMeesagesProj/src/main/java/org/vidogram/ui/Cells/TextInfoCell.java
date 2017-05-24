package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextInfoCell extends FrameLayout
{
  private TextView textView;

  public TextInfoCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText5"));
    this.textView.setTextSize(1, 13.0F);
    this.textView.setGravity(17);
    this.textView.setPadding(0, AndroidUtilities.dp(19.0F), 0, AndroidUtilities.dp(19.0F));
    addView(this.textView, LayoutHelper.createFrame(-2, -2.0F, 17, 17.0F, 0.0F, 17.0F, 0.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }

  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextInfoCell
 * JD-Core Version:    0.6.0
 */