package org.vidogram.ui.Cells;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.LayoutHelper;

public class SendLocationCell extends FrameLayout
{
  private SimpleTextView accurateTextView;
  private ImageView imageView;
  private SimpleTextView titleTextView;

  public SendLocationCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(2130838019);
    Object localObject = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(40.0F), Theme.getColor("location_sendLocationBackground"), Theme.getColor("location_sendLocationBackground"));
    Drawable localDrawable = getResources().getDrawable(2130838019);
    localDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("location_sendLocationIcon"), PorterDuff.Mode.MULTIPLY));
    localObject = new CombinedDrawable((Drawable)localObject, localDrawable);
    ((CombinedDrawable)localObject).setCustomSize(AndroidUtilities.dp(40.0F), AndroidUtilities.dp(40.0F));
    this.imageView.setBackgroundDrawable((Drawable)localObject);
    localObject = this.imageView;
    int i;
    float f1;
    label140: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label408;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label414;
      f2 = 17.0F;
      label149: addView((View)localObject, LayoutHelper.createFrame(40, 40.0F, i | 0x30, f1, 13.0F, f2, 0.0F));
      this.titleTextView = new SimpleTextView(paramContext);
      this.titleTextView.setTextSize(16);
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText7"));
      localObject = this.titleTextView;
      if (!LocaleController.isRTL)
        break label419;
      i = 5;
      label220: ((SimpleTextView)localObject).setGravity(i);
      this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject = this.titleTextView;
      if (!LocaleController.isRTL)
        break label425;
      i = 5;
      label254: if (!LocaleController.isRTL)
        break label431;
      f1 = 16.0F;
      label263: if (!LocaleController.isRTL)
        break label437;
      f2 = 73.0F;
      label272: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.accurateTextView = new SimpleTextView(paramContext);
      this.accurateTextView.setTextSize(14);
      this.accurateTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL)
        break label443;
      i = 5;
      label341: paramContext.setGravity(i);
      paramContext = this.accurateTextView;
      if (!LocaleController.isRTL)
        break label449;
      i = j;
      label362: if (!LocaleController.isRTL)
        break label455;
      f1 = 16.0F;
      label371: if (!LocaleController.isRTL)
        break label461;
      f2 = 73.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 37.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label408: f1 = 17.0F;
      break label140;
      label414: f2 = 0.0F;
      break label149;
      label419: i = 3;
      break label220;
      label425: i = 3;
      break label254;
      label431: f1 = 73.0F;
      break label263;
      label437: f2 = 16.0F;
      break label272;
      label443: i = 3;
      break label341;
      label449: i = 3;
      break label362;
      label455: f1 = 73.0F;
      break label371;
      label461: f2 = 16.0F;
    }
  }

  private ImageView getImageView()
  {
    return this.imageView;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(66.0F), 1073741824));
  }

  public void setText(String paramString1, String paramString2)
  {
    this.titleTextView.setText(paramString1);
    this.accurateTextView.setText(paramString2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.SendLocationCell
 * JD-Core Version:    0.6.0
 */