package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class LetterSectionCell extends FrameLayout
{
  private TextView textView;

  public LetterSectionCell(Context paramContext)
  {
    super(paramContext);
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), AndroidUtilities.dp(64.0F)));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 22.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.textView.setGravity(17);
    addView(this.textView, LayoutHelper.createFrame(-1, -1.0F));
  }

  public void setCellHeight(int paramInt)
  {
    setLayoutParams(new ViewGroup.LayoutParams(AndroidUtilities.dp(54.0F), paramInt));
  }

  public void setLetter(String paramString)
  {
    this.textView.setText(paramString.toUpperCase());
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.LetterSectionCell
 * JD-Core Version:    0.6.0
 */