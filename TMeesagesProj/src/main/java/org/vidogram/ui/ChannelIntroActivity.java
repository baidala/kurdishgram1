package org.vidogram.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;

public class ChannelIntroActivity extends BaseFragment
{
  private TextView createChannelText;
  private TextView descriptionText;
  private ImageView imageView;
  private TextView whatIsChannelText;

  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setItemsColor(Theme.getColor("windowBackgroundWhiteGrayText2"), false);
    this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarWhiteSelector"), false);
    this.actionBar.setCastShadows(false);
    if (!AndroidUtilities.isTablet())
      this.actionBar.showActionModeTop();
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ChannelIntroActivity.this.finishFragment();
      }
    });
    this.fragmentView = new ViewGroup(paramContext)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        int i = paramInt3 - paramInt1;
        paramInt1 = paramInt4 - paramInt2;
        if (paramInt3 > paramInt4)
        {
          paramInt2 = (int)(paramInt1 * 0.05F);
          ChannelIntroActivity.this.imageView.layout(0, paramInt2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + paramInt2);
          paramInt2 = (int)(i * 0.4F);
          paramInt3 = (int)(paramInt1 * 0.14F);
          ChannelIntroActivity.this.whatIsChannelText.layout(paramInt2, paramInt3, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth() + paramInt2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + paramInt3);
          paramInt3 = (int)(paramInt1 * 0.61F);
          ChannelIntroActivity.this.createChannelText.layout(paramInt2, paramInt3, ChannelIntroActivity.this.createChannelText.getMeasuredWidth() + paramInt2, ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + paramInt3);
          paramInt2 = (int)(i * 0.45F);
          paramInt1 = (int)(paramInt1 * 0.31F);
          ChannelIntroActivity.this.descriptionText.layout(paramInt2, paramInt1, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + paramInt2, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + paramInt1);
          return;
        }
        paramInt2 = (int)(paramInt1 * 0.05F);
        ChannelIntroActivity.this.imageView.layout(0, paramInt2, ChannelIntroActivity.this.imageView.getMeasuredWidth(), ChannelIntroActivity.this.imageView.getMeasuredHeight() + paramInt2);
        paramInt2 = (int)(paramInt1 * 0.59F);
        ChannelIntroActivity.this.whatIsChannelText.layout(0, paramInt2, ChannelIntroActivity.this.whatIsChannelText.getMeasuredWidth(), ChannelIntroActivity.this.whatIsChannelText.getMeasuredHeight() + paramInt2);
        paramInt2 = (int)(paramInt1 * 0.68F);
        paramInt3 = (int)(i * 0.05F);
        ChannelIntroActivity.this.descriptionText.layout(paramInt3, paramInt2, ChannelIntroActivity.this.descriptionText.getMeasuredWidth() + paramInt3, ChannelIntroActivity.this.descriptionText.getMeasuredHeight() + paramInt2);
        paramInt1 = (int)(paramInt1 * 0.86F);
        ChannelIntroActivity.this.createChannelText.layout(0, paramInt1, ChannelIntroActivity.this.createChannelText.getMeasuredWidth(), ChannelIntroActivity.this.createChannelText.getMeasuredHeight() + paramInt1);
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        paramInt1 = View.MeasureSpec.getSize(paramInt1);
        paramInt2 = View.MeasureSpec.getSize(paramInt2);
        if (paramInt1 > paramInt2)
        {
          ChannelIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.45F), 1073741824), View.MeasureSpec.makeMeasureSpec((int)(paramInt2 * 0.78F), 1073741824));
          ChannelIntroActivity.this.whatIsChannelText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.6F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 0));
          ChannelIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.5F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 0));
          ChannelIntroActivity.this.createChannelText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.6F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), 1073741824));
        }
        while (true)
        {
          setMeasuredDimension(paramInt1, paramInt2);
          return;
          ChannelIntroActivity.this.imageView.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec((int)(paramInt2 * 0.44F), 1073741824));
          ChannelIntroActivity.this.whatIsChannelText.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 0));
          ChannelIntroActivity.this.descriptionText.measure(View.MeasureSpec.makeMeasureSpec((int)(paramInt1 * 0.9F), 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt2, 0));
          ChannelIntroActivity.this.createChannelText.measure(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), 1073741824));
        }
      }
    };
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    ViewGroup localViewGroup = (ViewGroup)this.fragmentView;
    localViewGroup.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.imageView = new ImageView(paramContext);
    this.imageView.setImageResource(2130837663);
    this.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    localViewGroup.addView(this.imageView);
    this.whatIsChannelText = new TextView(paramContext);
    this.whatIsChannelText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.whatIsChannelText.setGravity(1);
    this.whatIsChannelText.setTextSize(1, 24.0F);
    this.whatIsChannelText.setText(LocaleController.getString("ChannelAlertTitle", 2131165454));
    localViewGroup.addView(this.whatIsChannelText);
    this.descriptionText = new TextView(paramContext);
    this.descriptionText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
    this.descriptionText.setGravity(1);
    this.descriptionText.setTextSize(1, 16.0F);
    this.descriptionText.setText(LocaleController.getString("ChannelAlertText", 2131165453));
    localViewGroup.addView(this.descriptionText);
    this.createChannelText = new TextView(paramContext);
    this.createChannelText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText5"));
    this.createChannelText.setGravity(17);
    this.createChannelText.setTextSize(1, 16.0F);
    this.createChannelText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.createChannelText.setText(LocaleController.getString("ChannelAlertCreate", 2131165452));
    localViewGroup.addView(this.createChannelText);
    this.createChannelText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        paramView = new Bundle();
        paramView.putInt("step", 0);
        ChannelIntroActivity.this.presentFragment(new ChannelCreateActivity(paramView), true);
      }
    });
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarWhiteSelector"), new ThemeDescription(this.whatIsChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.createChannelText, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText5") };
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChannelIntroActivity
 * JD-Core Version:    0.6.0
 */