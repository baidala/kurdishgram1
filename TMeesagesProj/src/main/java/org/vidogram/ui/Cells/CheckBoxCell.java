package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.CheckBoxSquare;
import org.vidogram.ui.Components.LayoutHelper;

public class CheckBoxCell extends FrameLayout
{
  private CheckBoxSquare checkBox;
  private boolean needDivider;
  private TextView textView;
  private TextView valueTextView;

  public CheckBoxCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    TextView localTextView = this.textView;
    Object localObject;
    int i;
    label107: float f;
    if (paramBoolean)
    {
      localObject = "dialogTextBlack";
      localTextView.setTextColor(Theme.getColor((String)localObject));
      this.textView.setTextSize(1, 16.0F);
      this.textView.setLines(1);
      this.textView.setMaxLines(1);
      this.textView.setSingleLine(true);
      this.textView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label406;
      i = 5;
      ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL)
        break label412;
      i = 5;
      label132: if (!LocaleController.isRTL)
        break label418;
      int j = 17;
      label142: f = j;
      if (!LocaleController.isRTL)
        break label425;
      j = 46;
      label156: addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, f, 0.0F, j, 0.0F));
      this.valueTextView = new TextView(paramContext);
      localTextView = this.valueTextView;
      if (!paramBoolean)
        break label432;
      localObject = "dialogTextBlue";
      label205: localTextView.setTextColor(Theme.getColor((String)localObject));
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label439;
      i = 3;
      label274: ((TextView)localObject).setGravity(i | 0x10);
      localObject = this.valueTextView;
      if (!LocaleController.isRTL)
        break label445;
      i = 3;
      label299: addView((View)localObject, LayoutHelper.createFrame(-2, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.checkBox = new CheckBoxSquare(paramContext, paramBoolean);
      paramContext = this.checkBox;
      if (!LocaleController.isRTL)
        break label451;
      i = i1;
      label351: if (!LocaleController.isRTL)
        break label457;
      k = 0;
      label360: f = k;
      if (!LocaleController.isRTL)
        break label464;
    }
    label406: label412: label418: int m;
    label425: label432: label439: label445: label451: label457: label464: for (int k = n; ; m = 0)
    {
      addView(paramContext, LayoutHelper.createFrame(18, 18.0F, i | 0x30, f, 15.0F, k, 0.0F));
      return;
      localObject = "windowBackgroundWhiteBlackText";
      break;
      i = 3;
      break label107;
      i = 3;
      break label132;
      m = 46;
      break label142;
      m = 17;
      break label156;
      localObject = "windowBackgroundWhiteValueText";
      break label205;
      i = 5;
      break label274;
      i = 5;
      break label299;
      i = 3;
      break label351;
      m = 17;
      break label360;
    }
  }

  public CheckBoxSquare getCheckBox()
  {
    return this.checkBox;
  }

  public TextView getTextView()
  {
    return this.textView;
  }

  public TextView getValueTextView()
  {
    return this.valueTextView;
  }

  public boolean isChecked()
  {
    return this.checkBox.isChecked();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider)
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      paramInt1 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 / 2, -2147483648), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(18.0F), 1073741824));
      return;
    }
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setText(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.checkBox.setChecked(paramBoolean1, false);
    this.valueTextView.setText(paramString2);
    this.needDivider = paramBoolean2;
    paramBoolean1 = bool;
    if (!paramBoolean2)
      paramBoolean1 = true;
    setWillNotDraw(paramBoolean1);
  }

  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.CheckBoxCell
 * JD-Core Version:    0.6.0
 */