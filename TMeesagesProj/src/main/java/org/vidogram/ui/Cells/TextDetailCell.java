package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextDetailCell extends FrameLayout
{
  private ImageView imageView;
  private TextView textView;
  private TextView valueTextView;

  public TextDetailCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    int i;
    label103: float f1;
    label112: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i);
      localTextView = this.textView;
      if (!LocaleController.isRTL)
        break label378;
      i = 5;
      if (!LocaleController.isRTL)
        break label384;
      f1 = 16.0F;
      if (!LocaleController.isRTL)
        break label390;
      f2 = 71.0F;
      label121: addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i, f1, 10.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label396;
      i = 5;
      label214: localTextView.setGravity(i);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label402;
      i = 5;
      label236: if (!LocaleController.isRTL)
        break label408;
      f1 = 16.0F;
      label245: if (!LocaleController.isRTL)
        break label414;
      f2 = 71.0F;
      label254: addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i, f1, 35.0F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.imageView;
      if (!LocaleController.isRTL)
        break label420;
      i = j;
      label333: if (!LocaleController.isRTL)
        break label426;
      f1 = 0.0F;
      label341: if (!LocaleController.isRTL)
        break label432;
      f2 = 16.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label378: i = 3;
      break label103;
      label384: f1 = 71.0F;
      break label112;
      label390: f2 = 16.0F;
      break label121;
      label396: i = 3;
      break label214;
      label402: i = 3;
      break label236;
      label408: f1 = 71.0F;
      break label245;
      label414: f2 = 16.0F;
      break label254;
      label420: i = 3;
      break label333;
      label426: f1 = 16.0F;
      break label341;
      label432: f2 = 0.0F;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
  }

  public void setTextAndValue(String paramString1, String paramString2)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(4);
  }

  public void setTextAndValueAndIcon(String paramString1, String paramString2, int paramInt)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.imageView.setVisibility(0);
    this.imageView.setImageResource(paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextDetailCell
 * JD-Core Version:    0.6.0
 */