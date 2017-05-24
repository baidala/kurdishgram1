package org.vidogram.ui.Components;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;

public class PickerBottomLayout extends FrameLayout
{
  public TextView cancelButton;
  public LinearLayout doneButton;
  public TextView doneButtonBadgeTextView;
  public TextView doneButtonTextView;
  private boolean isDarkTheme;

  public PickerBottomLayout(Context paramContext)
  {
    this(paramContext, true);
  }

  public PickerBottomLayout(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.isDarkTheme = paramBoolean;
    Object localObject;
    if (this.isDarkTheme)
    {
      i = -15066598;
      setBackgroundColor(i);
      this.cancelButton = new TextView(paramContext);
      this.cancelButton.setTextSize(1, 14.0F);
      localObject = this.cancelButton;
      if (!this.isDarkTheme)
        break label523;
      i = -1;
      label65: ((TextView)localObject).setTextColor(i);
      this.cancelButton.setGravity(17);
      localObject = this.cancelButton;
      if (!this.isDarkTheme)
        break label532;
      i = -12763843;
      label96: ((TextView)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(i, 0));
      this.cancelButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      this.cancelButton.setText(LocaleController.getString("Cancel", 2131165427).toUpperCase());
      this.cancelButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      addView(this.cancelButton, LayoutHelper.createFrame(-2, -1, 51));
      this.doneButton = new LinearLayout(paramContext);
      this.doneButton.setOrientation(0);
      localObject = this.doneButton;
      if (!this.isDarkTheme)
        break label538;
      i = -12763843;
      label206: ((LinearLayout)localObject).setBackgroundDrawable(Theme.createSelectorDrawable(i, 0));
      this.doneButton.setPadding(AndroidUtilities.dp(29.0F), 0, AndroidUtilities.dp(29.0F), 0);
      addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
      this.doneButtonBadgeTextView = new TextView(paramContext);
      this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
      localObject = this.doneButtonBadgeTextView;
      if (!this.isDarkTheme)
        break label544;
      i = -1;
      label300: ((TextView)localObject).setTextColor(i);
      this.doneButtonBadgeTextView.setGravity(17);
      if (!this.isDarkTheme)
        break label553;
      localObject = getResources().getDrawable(2130838012);
      label333: this.doneButtonBadgeTextView.setBackgroundDrawable((Drawable)localObject);
      this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
      this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
      this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
      this.doneButtonTextView = new TextView(paramContext);
      this.doneButtonTextView.setTextSize(1, 14.0F);
      paramContext = this.doneButtonTextView;
      if (!this.isDarkTheme)
        break label571;
    }
    label523: label532: label538: label544: label553: label571: for (int i = j; ; i = Theme.getColor("picker_enabledButton"))
    {
      paramContext.setTextColor(i);
      this.doneButtonTextView.setGravity(17);
      this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
      this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166409).toUpperCase());
      this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
      return;
      i = Theme.getColor("windowBackgroundWhite");
      break;
      i = Theme.getColor("picker_enabledButton");
      break label65;
      i = 788529152;
      break label96;
      i = 788529152;
      break label206;
      i = Theme.getColor("picker_badgeText");
      break label300;
      localObject = Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0F), Theme.getColor("picker_badge"));
      break label333;
    }
  }

  public void updateSelectedCount(int paramInt, boolean paramBoolean)
  {
    int i = -1;
    TextView localTextView1 = null;
    TextView localTextView2 = null;
    Object localObject = null;
    if (paramInt == 0)
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if (paramBoolean)
      {
        localTextView1 = this.doneButtonTextView;
        if (this.isDarkTheme)
        {
          localTextView1.setTag(localObject);
          localObject = this.doneButtonTextView;
          if (!this.isDarkTheme)
            break label86;
        }
        label86: for (paramInt = -6710887; ; paramInt = Theme.getColor("picker_disabledButton"))
        {
          ((TextView)localObject).setTextColor(paramInt);
          this.doneButton.setEnabled(false);
          return;
          localObject = "picker_disabledButton";
          break;
        }
      }
      localTextView2 = this.doneButtonTextView;
      if (this.isDarkTheme)
      {
        localObject = localTextView1;
        localTextView2.setTag(localObject);
        localObject = this.doneButtonTextView;
        if (!this.isDarkTheme)
          break label148;
      }
      label148: for (paramInt = -1; ; paramInt = Theme.getColor("picker_enabledButton"))
      {
        ((TextView)localObject).setTextColor(paramInt);
        return;
        localObject = "picker_enabledButton";
        break;
      }
    }
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(paramInt) }));
    localTextView1 = this.doneButtonTextView;
    if (this.isDarkTheme)
    {
      localObject = localTextView2;
      label205: localTextView1.setTag(localObject);
      localObject = this.doneButtonTextView;
      if (!this.isDarkTheme)
        break label253;
    }
    label253: for (paramInt = i; ; paramInt = Theme.getColor("picker_enabledButton"))
    {
      ((TextView)localObject).setTextColor(paramInt);
      if (!paramBoolean)
        break;
      this.doneButton.setEnabled(true);
      return;
      localObject = "picker_enabledButton";
      break label205;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PickerBottomLayout
 * JD-Core Version:    0.6.0
 */