package org.vidogram.ui.Cells;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LayoutHelper;

public class TextInfoPrivacyCell extends FrameLayout
{
  private TextView textView;

  public TextInfoPrivacyCell(Context paramContext)
  {
    super(paramContext);
    this.textView = new TextView(paramContext);
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.textView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
    this.textView.setTextSize(1, 14.0F);
    paramContext = this.textView;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i);
      this.textView.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(17.0F));
      this.textView.setMovementMethod(LinkMovementMethod.getInstance());
      paramContext = this.textView;
      if (!LocaleController.isRTL)
        break label141;
    }
    label141: for (int i = j; ; i = 3)
    {
      addView(paramContext, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, 17.0F, 0.0F, 17.0F, 0.0F));
      return;
      i = 3;
      break;
    }
  }

  public TextView getTextView()
  {
    return this.textView;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(0, 0));
  }

  public void setEnabled(boolean paramBoolean, ArrayList<Animator> paramArrayList)
  {
    float f = 1.0F;
    if (paramArrayList != null)
    {
      TextView localTextView = this.textView;
      if (paramBoolean);
      while (true)
      {
        paramArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { f }));
        return;
        f = 0.5F;
      }
    }
    paramArrayList = this.textView;
    if (paramBoolean);
    while (true)
    {
      paramArrayList.setAlpha(f);
      return;
      f = 0.5F;
    }
  }

  public void setText(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null)
      this.textView.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    while (true)
    {
      this.textView.setText(paramCharSequence);
      return;
      this.textView.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(17.0F));
    }
  }

  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.TextInfoPrivacyCell
 * JD-Core Version:    0.6.0
 */