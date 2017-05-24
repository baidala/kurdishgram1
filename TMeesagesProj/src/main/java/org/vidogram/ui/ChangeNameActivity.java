package org.vidogram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_updateProfile;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.LayoutHelper;

public class ChangeNameActivity extends BaseFragment
{
  private static final int done_button = 1;
  private View doneButton;
  private EditText firstNameField;
  private View headerLabelView;
  private EditText lastNameField;

  private void saveName()
  {
    TLRPC.User localUser = UserConfig.getCurrentUser();
    if ((localUser == null) || (this.lastNameField.getText() == null) || (this.firstNameField.getText() == null));
    String str1;
    String str2;
    do
    {
      return;
      str1 = this.firstNameField.getText().toString();
      str2 = this.lastNameField.getText().toString();
    }
    while ((localUser.first_name != null) && (localUser.first_name.equals(str1)) && (localUser.last_name != null) && (localUser.last_name.equals(str2)));
    TLRPC.TL_account_updateProfile localTL_account_updateProfile = new TLRPC.TL_account_updateProfile();
    localTL_account_updateProfile.flags = 3;
    localTL_account_updateProfile.first_name = str1;
    localUser.first_name = str1;
    localTL_account_updateProfile.last_name = str2;
    localUser.last_name = str2;
    localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    if (localUser != null)
    {
      localUser.first_name = localTL_account_updateProfile.first_name;
      localUser.last_name = localTL_account_updateProfile.last_name;
    }
    UserConfig.saveConfig(true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
    ConnectionsManager.getInstance().sendRequest(localTL_account_updateProfile, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public View createView(Context paramContext)
  {
    int j = 5;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("EditName", 2131165667));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ChangeNameActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (ChangeNameActivity.this.firstNameField.getText().length() == 0));
        ChangeNameActivity.this.saveName();
        ChangeNameActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    if (localUser == null)
      localUser = UserConfig.getCurrentUser();
    while (true)
    {
      LinearLayout localLinearLayout = new LinearLayout(paramContext);
      this.fragmentView = localLinearLayout;
      this.fragmentView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
      ((LinearLayout)this.fragmentView).setOrientation(1);
      this.fragmentView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      this.firstNameField = new EditText(paramContext);
      this.firstNameField.setTextSize(1, 18.0F);
      this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.firstNameField.setMaxLines(1);
      this.firstNameField.setLines(1);
      this.firstNameField.setSingleLine(true);
      EditText localEditText = this.firstNameField;
      if (LocaleController.isRTL)
      {
        i = 5;
        localEditText.setGravity(i);
        this.firstNameField.setInputType(49152);
        this.firstNameField.setImeOptions(5);
        this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165714));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              ChangeNameActivity.this.lastNameField.requestFocus();
              ChangeNameActivity.this.lastNameField.setSelection(ChangeNameActivity.this.lastNameField.length());
              return true;
            }
            return false;
          }
        });
        this.lastNameField = new EditText(paramContext);
        this.lastNameField.setTextSize(1, 18.0F);
        this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setLines(1);
        this.lastNameField.setSingleLine(true);
        paramContext = this.lastNameField;
        if (!LocaleController.isRTL)
          break label570;
      }
      label570: for (int i = j; ; i = 3)
      {
        paramContext.setGravity(i);
        this.lastNameField.setInputType(49152);
        this.lastNameField.setImeOptions(6);
        this.lastNameField.setHint(LocaleController.getString("LastName", 2131165874));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        localLinearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
        this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 6)
            {
              ChangeNameActivity.this.doneButton.performClick();
              return true;
            }
            return false;
          }
        });
        if (localUser != null)
        {
          this.firstNameField.setText(localUser.first_name);
          this.firstNameField.setSelection(this.firstNameField.length());
          this.lastNameField.setText(localUser.last_name);
        }
        return this.fragmentView;
        i = 3;
        break;
      }
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated") };
  }

  public void onResume()
  {
    super.onResume();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (ChangeNameActivity.this.firstNameField != null)
          {
            ChangeNameActivity.this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(ChangeNameActivity.this.firstNameField);
          }
        }
      }
      , 100L);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChangeNameActivity
 * JD-Core Version:    0.6.0
 */