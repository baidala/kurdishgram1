package org.vidogram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.ui.Components.LayoutHelper;

public class PhotoEditToolCell extends FrameLayout
{
  private ImageView iconImage;
  private TextView nameTextView;
  private TextView valueTextView;
  private int width;

  public PhotoEditToolCell(Context paramContext, int paramInt)
  {
    super(paramContext);
    this.width = paramInt;
    this.iconImage = new ImageView(paramContext);
    this.iconImage.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.iconImage, LayoutHelper.createFrame(-1, -1.0F, 49, 0.0F, 0.0F, 7.0F, 12.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setGravity(17);
    this.nameTextView.setTextColor(-1);
    this.nameTextView.setTextSize(1, 10.0F);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0F, 81, 0.0F, 0.0F, 7.0F, 0.0F));
    this.valueTextView = new TextView(paramContext);
    this.valueTextView.setTextColor(-9649153);
    this.valueTextView.setTextSize(1, 11.0F);
    this.valueTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.valueTextView.setSingleLine(true);
    addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 50.0F, 3.0F, 0.0F, 0.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.width, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0F), 1073741824));
  }

  public void setIconAndTextAndValue(int paramInt, String paramString, float paramFloat)
  {
    this.iconImage.setImageResource(paramInt);
    this.nameTextView.setText(paramString.toUpperCase());
    if (paramFloat == 0.0F)
    {
      this.valueTextView.setText("");
      return;
    }
    if (paramFloat > 0.0F)
    {
      this.valueTextView.setText("+" + (int)paramFloat);
      return;
    }
    this.valueTextView.setText("" + (int)paramFloat);
  }

  public void setIconAndTextAndValue(int paramInt, String paramString1, String paramString2)
  {
    this.iconImage.setImageResource(paramInt);
    this.nameTextView.setText(paramString1.toUpperCase());
    this.valueTextView.setText(paramString2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.PhotoEditToolCell
 * JD-Core Version:    0.6.0
 */