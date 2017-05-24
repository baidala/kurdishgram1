package org.vidogram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextSettingsCell extends FrameLayout
{
  private boolean needDivider;
  private TextView textView;
  private ImageView valueImageView;
  private TextView valueTextView;

  public TextSettingsCell(Context paramContext)
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
        break label355;
      i = 5;
      label112: addView(localTextView, LayoutHelper.createFrame(-1, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueTextView = new TextView(paramContext);
      this.valueTextView.setTextColor(Theme.getColor("windowBackgroundWhiteValueText"));
      this.valueTextView.setTextSize(1, 16.0F);
      this.valueTextView.setLines(1);
      this.valueTextView.setMaxLines(1);
      this.valueTextView.setSingleLine(true);
      this.valueTextView.setEllipsize(TextUtils.TruncateAt.END);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label360;
      i = 3;
      label216: localTextView.setGravity(i | 0x10);
      localTextView = this.valueTextView;
      if (!LocaleController.isRTL)
        break label365;
      i = 3;
      label239: addView(localTextView, LayoutHelper.createFrame(-2, -1.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      this.valueImageView = new ImageView(paramContext);
      this.valueImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.valueImageView.setVisibility(4);
      this.valueImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayIcon"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.valueImageView;
      if (!LocaleController.isRTL)
        break label370;
    }
    label355: label360: label365: label370: for (int i = j; ; i = 5)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
      i = 3;
      break label112;
      i = 5;
      break label216;
      i = 5;
      break label239;
    }
  }

  public TextView getTextView()
  {
    return this.textView;
  }

  public TextView getValueTextView()
  {
    return this.valueTextView;
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
      paramInt2 = getMeasuredWidth() - getPaddingLeft() - getPaddingRight() - AndroidUtilities.dp(34.0F);
      i = paramInt2 / 2;
      if (this.valueImageView.getVisibility() == 0)
        this.valueImageView.measure(View.MeasureSpec.makeMeasureSpec(i, -2147483648), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      paramInt1 = paramInt2;
      if (this.valueTextView.getVisibility() == 0)
      {
        this.valueTextView.measure(View.MeasureSpec.makeMeasureSpec(i, -2147483648), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
        paramInt1 = paramInt2 - this.valueTextView.getMeasuredWidth() - AndroidUtilities.dp(8.0F);
      }
      this.textView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
      return;
    }
  }

  public void setEnabled(boolean paramBoolean, ArrayList<Animator> paramArrayList)
  {
    float f1 = 1.0F;
    setEnabled(paramBoolean);
    float f2;
    if (paramArrayList != null)
    {
      Object localObject = this.textView;
      if (paramBoolean)
      {
        f2 = 1.0F;
        paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
        if (this.valueTextView.getVisibility() == 0)
        {
          localObject = this.valueTextView;
          if (!paramBoolean)
            break label134;
          f2 = 1.0F;
          label67: paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f2 }));
        }
        if (this.valueImageView.getVisibility() == 0)
        {
          localObject = this.valueImageView;
          if (!paramBoolean)
            break label141;
          label107: paramArrayList.add(ObjectAnimator.ofFloat(localObject, "alpha", new float[] { f1 }));
        }
      }
    }
    label134: label141: label187: 
    do
    {
      return;
      f2 = 0.5F;
      break;
      f2 = 0.5F;
      break label67;
      f1 = 0.5F;
      break label107;
      paramArrayList = this.textView;
      if (!paramBoolean)
        break label218;
      f2 = 1.0F;
      paramArrayList.setAlpha(f2);
      if (this.valueTextView.getVisibility() != 0)
        continue;
      paramArrayList = this.valueTextView;
      if (!paramBoolean)
        break label225;
      f2 = 1.0F;
      paramArrayList.setAlpha(f2);
    }
    while (this.valueImageView.getVisibility() != 0);
    paramArrayList = this.valueImageView;
    if (paramBoolean);
    while (true)
    {
      paramArrayList.setAlpha(f1);
      return;
      label218: f2 = 0.5F;
      break;
      label225: f2 = 0.5F;
      break label187;
      f1 = 0.5F;
    }
  }

  public void setText(String paramString, boolean paramBoolean)
  {
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    this.valueImageView.setVisibility(4);
    this.needDivider = paramBoolean;
    if (!paramBoolean);
    for (paramBoolean = true; ; paramBoolean = false)
    {
      setWillNotDraw(paramBoolean);
      return;
    }
  }

  public void setTextAndIcon(String paramString, int paramInt, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString);
    this.valueTextView.setVisibility(4);
    if (paramInt != 0)
    {
      this.valueImageView.setVisibility(0);
      this.valueImageView.setImageResource(paramInt);
    }
    while (true)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean)
        bool = true;
      setWillNotDraw(bool);
      return;
      this.valueImageView.setVisibility(4);
    }
  }

  public void setTextAndValue(String paramString1, String paramString2, boolean paramBoolean)
  {
    boolean bool = false;
    this.textView.setText(paramString1);
    this.valueImageView.setVisibility(4);
    if (paramString2 != null)
    {
      this.valueTextView.setText(paramString2);
      this.valueTextView.setVisibility(0);
    }
    while (true)
    {
      this.needDivider = paramBoolean;
      if (!paramBoolean)
        bool = true;
      setWillNotDraw(bool);
      requestLayout();
      return;
      this.valueTextView.setVisibility(4);
    }
  }

  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }

  public void setTextValueColor(int paramInt)
  {
    this.valueTextView.setTextColor(paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextSettingsCell
 * JD-Core Version:    0.6.0
 */