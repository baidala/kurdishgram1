package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.Switch;

public class TextCheckCell extends FrameLayout
{
  private Switch checkBox;
  private boolean isMultiline;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;

  public TextCheckCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    TextView localTextView = this.textView;
    label113: float f1;
    label122: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      localTextView.setGravity(i | 0x10);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.textView;
      if (!LocaleController.isRTL)
        break label397;
      i = 5;
      if (!LocaleController.isRTL)
        break label403;
      f1 = 64.0F;
      if (!LocaleController.isRTL)
        break label409;
      f2 = 17.0F;
      label131: addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f1, 0.0F, f2, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
      this.valueTextView.setTextSize(1, 13.0F);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label415;
      i = 5;
      label201: localTextView.setGravity(i);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setPadding(0, 0, 0, 0);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label421;
      i = 5;
      label268: if (!LocaleController.isRTL)
        break label427;
      f1 = 64.0F;
      label277: if (!LocaleController.isRTL)
        break label433;
      f2 = 17.0F;
      label286: addView(localTextView, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 35.0F, f2, 0.0F));
      this.checkBox = new Switch(paramContext);
      this.checkBox.setDuplicateParentStateEnabled(false);
      this.checkBox.setFocusable(false);
      this.checkBox.setFocusableInTouchMode(false);
      this.checkBox.setClickable(false);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL)
        break label439;
    }
    label397: label403: label409: label415: label421: label427: label433: label439: for (int i = 3; ; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 14.0F, 0.0F, 14.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label113;
      f1 = 17.0F;
      break label122;
      f2 = 64.0F;
      break label131;
      i = 3;
      break label201;
      i = 3;
      break label268;
      f1 = 17.0F;
      break label277;
      f2 = 64.0F;
      break label286;
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.isMultiline)
    {
      super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
      return;
    }
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    float f;
    int i;
    if (this.valueTextView.getVisibility() == 0)
    {
      f = 64.0F;
      i = AndroidUtilities.dp(f);
      if (!this.needDivider)
        break label85;
    }
    label85: for (paramInt1 = 1; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, 1073741824));
      return;
      f = 48.0F;
      break;
    }
  }

  public void setChecked(boolean paramBoolean)
  {
    this.checkBox.setChecked(paramBoolean);
  }

  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    if (paramBoolean)
    {
      this.textView.setAlpha(1.0F);
      this.valueTextView.setAlpha(1.0F);
      return;
    }
    this.textView.setAlpha(0.5F);
    this.valueTextView.setAlpha(0.5F);
  }

  public void setTextAndCheck(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.textView.setText(paramString);
    this.isMultiline = false;
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean2;
    this.valueTextView.setVisibility(8);
    paramString = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
    paramString.height = -1;
    paramString.topMargin = 0;
    this.textView.setLayoutParams(paramString);
    if (!paramBoolean2);
    for (paramBoolean1 = true; ; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      return;
    }
  }

  public void setTextAndValueAndCheck(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.textView.setText(paramString1);
    this.valueTextView.setText(paramString2);
    this.checkBox.setChecked(paramBoolean1);
    this.needDivider = paramBoolean3;
    this.valueTextView.setVisibility(0);
    this.isMultiline = paramBoolean2;
    if (paramBoolean2)
    {
      this.valueTextView.setLines(0);
      this.valueTextView.setMaxLines(0);
      this.valueTextView.setSingleLine(false);
      this.valueTextView.setEllipsize(null);
      this.valueTextView.setPadding(0, 0, 0, AndroidUtilities.dp(11.0F));
      paramString1 = (FrameLayout.LayoutParams)this.textView.getLayoutParams();
      paramString1.height = -2;
      paramString1.topMargin = AndroidUtilities.dp(10.0F);
      this.textView.setLayoutParams(paramString1);
      if (paramBoolean3)
        break label191;
    }
    label191: for (paramBoolean1 = true; ; paramBoolean1 = false)
    {
      setWillNotDraw(paramBoolean1);
      return;
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      this.valueTextView.setPadding(0, 0, 0, 0);
      break;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextCheckCell
 * JD-Core Version:    0.6.0
 */