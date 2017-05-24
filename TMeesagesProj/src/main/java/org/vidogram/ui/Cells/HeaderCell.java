package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class HeaderCell extends FrameLayout
{
  private TextView textView = new TextView(getContext());

  public HeaderCell(Context paramContext)
  {
    super(paramContext);
    this.textView.setTextSize(1, 15.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueHeader"));
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label118;
    }
    label118: for (int i = j; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 15.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }

  public TextView getTextView()
  {
    return this.textView;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(38.0F), 1073741824));
  }

  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.HeaderCell
 * JD-Core Version:    0.6.0
 */