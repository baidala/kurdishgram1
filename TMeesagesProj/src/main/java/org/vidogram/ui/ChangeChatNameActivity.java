package org.vidogram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.LayoutHelper;

public class ChangeChatNameActivity extends BaseFragment
{
  private static final int done_button = 1;
  private int chat_id;
  private View doneButton;
  private EditText firstNameField;
  private View headerLabelView;

  public ChangeChatNameActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void saveName()
  {
    MessagesController.getInstance().changeChatTitle(this.chat_id, this.firstNameField.getText().toString());
  }

  public View createView(Context paramContext)
  {
    int j = 3;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("EditName", 2131165667));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ChangeChatNameActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (ChangeChatNameActivity.this.firstNameField.getText().length() == 0));
        ChangeChatNameActivity.this.saveName();
        ChangeChatNameActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chat_id));
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
    this.firstNameField.setText(localChat.title);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.firstNameField.setMaxLines(3);
    this.firstNameField.setPadding(0, 0, 0, 0);
    paramContext = this.firstNameField;
    int i;
    if (LocaleController.isRTL)
    {
      i = 5;
      paramContext.setGravity(i);
      this.firstNameField.setInputType(180224);
      this.firstNameField.setImeOptions(6);
      paramContext = this.firstNameField;
      i = j;
      if (LocaleController.isRTL)
        i = 5;
      paramContext.setGravity(i);
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          if ((paramInt == 6) && (ChangeChatNameActivity.this.doneButton != null))
          {
            ChangeChatNameActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      if (this.chat_id <= 0)
        break label377;
      this.firstNameField.setHint(LocaleController.getString("GroupName", 2131165796));
    }
    while (true)
    {
      this.firstNameField.setSelection(this.firstNameField.length());
      return this.fragmentView;
      i = 3;
      break;
      label377: this.firstNameField.setHint(LocaleController.getString("EnterListName", 2131165696));
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.chat_id = getArguments().getInt("chat_id", 0);
    return true;
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
          if (ChangeChatNameActivity.this.firstNameField != null)
          {
            ChangeChatNameActivity.this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(ChangeChatNameActivity.this.firstNameField);
          }
        }
      }
      , 100L);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChangeChatNameActivity
 * JD-Core Version:    0.6.0
 */