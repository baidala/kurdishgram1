package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.LayoutHelper;

public class ChangePhoneHelpActivity extends BaseFragment
{
  private ImageView imageView;
  private TextView textView1;
  private TextView textView2;

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    Object localObject1 = UserConfig.getCurrentUser();
    if ((localObject1 != null) && (((TLRPC.User)localObject1).phone != null) && (((TLRPC.User)localObject1).phone.length() != 0))
      localObject1 = b.a().e("+" + ((TLRPC.User)localObject1).phone);
    while (true)
    {
      this.actionBar.setTitle((CharSequence)localObject1);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            ChangePhoneHelpActivity.this.finishFragment();
        }
      });
      this.fragmentView = new RelativeLayout(paramContext);
      this.fragmentView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      Object localObject2 = (RelativeLayout)this.fragmentView;
      localObject1 = new ScrollView(paramContext);
      ((RelativeLayout)localObject2).addView((View)localObject1);
      localObject2 = (RelativeLayout.LayoutParams)((ScrollView)localObject1).getLayoutParams();
      ((RelativeLayout.LayoutParams)localObject2).width = -1;
      ((RelativeLayout.LayoutParams)localObject2).height = -2;
      ((RelativeLayout.LayoutParams)localObject2).addRule(15, -1);
      ((ScrollView)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      localObject2 = new LinearLayout(paramContext);
      ((LinearLayout)localObject2).setOrientation(1);
      ((LinearLayout)localObject2).setPadding(0, AndroidUtilities.dp(20.0F), 0, AndroidUtilities.dp(20.0F));
      ((ScrollView)localObject1).addView((View)localObject2);
      localObject1 = (FrameLayout.LayoutParams)((LinearLayout)localObject2).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -2;
      ((LinearLayout)localObject2).setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.imageView = new ImageView(paramContext);
      this.imageView.setImageResource(2130837997);
      this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("changephoneinfo_image"), PorterDuff.Mode.MULTIPLY));
      ((LinearLayout)localObject2).addView(this.imageView, LayoutHelper.createLinear(-2, -2, 1));
      this.textView1 = new TextView(paramContext);
      this.textView1.setTextSize(1, 16.0F);
      this.textView1.setGravity(1);
      this.textView1.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      try
      {
        this.textView1.setText(AndroidUtilities.replaceTags(LocaleController.getString("PhoneNumberHelp", 2131166269)));
        ((LinearLayout)localObject2).addView(this.textView1, LayoutHelper.createLinear(-2, -2, 1, 20, 56, 20, 0));
        this.textView2 = new TextView(paramContext);
        this.textView2.setTextSize(1, 18.0F);
        this.textView2.setGravity(1);
        this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.textView2.setText(LocaleController.getString("PhoneNumberChange", 2131166267));
        this.textView2.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.textView2.setPadding(0, AndroidUtilities.dp(10.0F), 0, AndroidUtilities.dp(10.0F));
        ((LinearLayout)localObject2).addView(this.textView2, LayoutHelper.createLinear(-2, -2, 1, 20, 46, 20, 0));
        this.textView2.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (ChangePhoneHelpActivity.this.getParentActivity() == null)
              return;
            paramView = new AlertDialog.Builder(ChangePhoneHelpActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("AppName", 2131165319));
            paramView.setMessage(LocaleController.getString("PhoneNumberAlert", 2131166266));
            paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                ChangePhoneHelpActivity.this.presentFragment(new ChangePhoneActivity(), true);
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            ChangePhoneHelpActivity.this.showDialog(paramView.create());
          }
        });
        return this.fragmentView;
        localObject1 = LocaleController.getString("NumberUnknown", 2131166152);
      }
      catch (Exception localException)
      {
        while (true)
        {
          FileLog.e(localException);
          this.textView1.setText(LocaleController.getString("PhoneNumberHelp", 2131166269));
        }
      }
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.textView1, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(this.imageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "changephoneinfo_image") };
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChangePhoneHelpActivity
 * JD-Core Version:    0.6.0
 */