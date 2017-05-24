package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadioButton;

public class RadioColorCell extends FrameLayout
{
  private RadioButton radioButton;
  private TextView textView;

  public RadioColorCell(Context paramContext)
  {
    super(paramContext);
    this.radioButton = new RadioButton(paramContext);
    this.radioButton.setSize(AndroidUtilities.dp(20.0F));
    this.radioButton.setColor(Theme.getColor("dialogRadioBackground"), Theme.getColor("dialogRadioBackgroundChecked"));
    RadioButton localRadioButton = this.radioButton;
    int i;
    label75: float f;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label263;
      int j = 0;
      f = j;
      j = i1;
      if (LocaleController.isRTL)
        j = 18;
      addView(localRadioButton, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f, 13.0F, j, 0.0F));
      this.textView = new TextView(paramContext);
      this.textView.setTextColor(Theme.getColor("dialogTextBlack"));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label270;
      i = 5;
      label188: paramContext.setGravity(i | 0x10);
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label275;
      i = n;
      label210: if (!LocaleController.isRTL)
        break label280;
      k = 17;
      label220: f = k;
      if (!LocaleController.isRTL)
        break label287;
    }
    label263: int m;
    label270: label275: label280: label287: for (int k = 51; ; m = 17)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f, 12.0F, k, 0.0F));
      return;
      i = 3;
      break;
      m = 18;
      break label75;
      i = 3;
      break label188;
      i = 3;
      break label210;
      m = 51;
      break label220;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0F), 1073741824));
  }

  public void setCheckColor(int paramInt1, int paramInt2)
  {
    this.radioButton.setColor(paramInt1, paramInt2);
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.radioButton.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setTextAndValue(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.radioButton.setChecked(paramBoolean, false);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.RadioColorCell
 * JD-Core Version:    0.6.0
 */