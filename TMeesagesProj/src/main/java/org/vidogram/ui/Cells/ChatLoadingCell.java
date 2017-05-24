package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadialProgressView;

public class ChatLoadingCell extends FrameLayout
{
  private FrameLayout frameLayout;
  private RadialProgressView progressBar;

  public ChatLoadingCell(Context paramContext)
  {
    super(paramContext);
    this.frameLayout = new FrameLayout(paramContext);
    this.frameLayout.setBackgroundResource(2130838082);
    this.frameLayout.getBackground().setColorFilter(Theme.colorFilter);
    addView(this.frameLayout, LayoutHelper.createFrame(36, 36, 17));
    this.progressBar = new RadialProgressView(paramContext);
    this.progressBar.setSize(AndroidUtilities.dp(28.0F));
    this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
    this.frameLayout.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0F), 1073741824));
  }

  public void setProgressVisible(boolean paramBoolean)
  {
    FrameLayout localFrameLayout = this.frameLayout;
    if (paramBoolean);
    for (int i = 0; ; i = 4)
    {
      localFrameLayout.setVisibility(i);
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ChatLoadingCell
 * JD-Core Version:    0.6.0
 */