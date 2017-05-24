package org.vidogram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class GroupCreateSectionCell extends FrameLayout
{
  private Drawable drawable;
  private TextView textView;

  public GroupCreateSectionCell(Context paramContext)
  {
    super(paramContext);
    setBackgroundColor(Theme.getColor("graySection"));
    this.drawable = getResources().getDrawable(2130838057);
    this.drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("groupcreate_sectionShadow"), PorterDuff.Mode.MULTIPLY));
    this.textView = new TextView(getContext());
    this.textView.setTextSize(1, 14.0F);
    this.textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.textView.setTextColor(Theme.getColor("groupcreate_sectionText"));
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label161;
    }
    label161: for (int i = j; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 16.0F, 0.0F, 16.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    this.drawable.setBounds(0, getMeasuredHeight() - AndroidUtilities.dp(3.0F), getMeasuredWidth(), getMeasuredHeight());
    this.drawable.draw(paramCanvas);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0F), 1073741824));
  }

  public void setText(String paramString)
  {
    this.textView.setText(paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.GroupCreateSectionCell
 * JD-Core Version:    0.6.0
 */