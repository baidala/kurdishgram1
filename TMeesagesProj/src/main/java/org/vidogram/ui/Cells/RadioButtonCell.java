package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadioButton;

public class RadioButtonCell extends FrameLayout
{
  private RadioButton radioButton;
  private TextView textView;
  private TextView valueTextView;

  public RadioButtonCell(Context paramContext)
  {
    super(paramContext);
    this.radioButton = new RadioButton(paramContext);
    this.radioButton.setSize(AndroidUtilities.dp(20.0F));
    this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
    Object localObject = this.radioButton;
    int i;
    label76: float f;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label419;
      int j = 0;
      f = j;
      if (!LocaleController.isRTL)
        break label426;
      j = 18;
      label90: addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f, 10.0F, j, 0.0F));
      this.textView = new TextView(paramContext);
      this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label432;
      i = 5;
      label186: ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label437;
      i = 5;
      label209: if (!LocaleController.isRTL)
        break label442;
      int k = 17;
      label219: f = k;
      if (!LocaleController.isRTL)
        break label449;
      k = 51;
      label233: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f, 10.0F, k, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL)
        break label456;
      i = 5;
      label304: paramContext.setGravity(i);
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL)
        break label461;
      i = i2;
      label362: if (!LocaleController.isRTL)
        break label466;
    }
    label419: int n;
    label426: label432: label437: label442: label449: label456: label461: label466: for (int m = 17; ; n = 51)
    {
      f = m;
      m = i1;
      if (LocaleController.isRTL)
        m = 51;
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f, 35.0F, m, 0.0F));
      return;
      i = 3;
      break;
      n = 18;
      break label76;
      n = 0;
      break label90;
      i = 3;
      break label186;
      i = 3;
      break label209;
      n = 51;
      break label219;
      n = 17;
      break label233;
      i = 3;
      break label304;
      i = 3;
      break label362;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.radioButton.setChecked(paramBoolean, false);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.RadioButtonCell
 * JD-Core Version:    0.6.0
 */