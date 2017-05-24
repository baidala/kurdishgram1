package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextDetailSettingsCell extends FrameLayout
{
  private boolean multiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;

  public TextDetailSettingsCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    TextView localTextView = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      localTextView = this.textView;
      if (!LocaleController.isRTL)
        break label265;
      i = 5;
      label112: addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 10.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL)
        break label270;
      i = 5;
      label183: paramContext.setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      paramContext = this.valueTextView;
      if (!LocaleController.isRTL)
        break label275;
    }
    label265: label270: label275: for (int i = j; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 35.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label112;
      i = 3;
      break label183;
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = 0;
    if (!this.multiline)
    {
      int i = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
      int j = AndroidUtilities.dp(64.0F);
      paramInt1 = paramInt2;
      if (this.needDivider)
        paramInt1 = 1;
      super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(paramInt1 + j, 1073741824));
      return;
    }
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }

  public void setMultilineDetail(boolean paramBoolean)
  {
    this.multiline = paramBoolean;
    if (paramBoolean)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(12.0F));
      return;
    }
    this.valueTextView.setLines(1);
    this.valueTextView.setMaxLines(1);
    this.valueTextView.setSingleLine(true);
    this.valueTextView.setPadding(0, 0, 0, 0);
  }

  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean;
    if (!paramBoolean);
    for (paramBoolean = true; ; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextDetailSettingsCell
 * JD-Core Version:    0.6.0
 */