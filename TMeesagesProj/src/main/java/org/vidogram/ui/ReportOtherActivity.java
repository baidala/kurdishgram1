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
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_reportPeer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputReportReasonOther;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.LayoutHelper;

public class ReportOtherActivity extends BaseFragment
{
  private static final int done_button = 1;
  private long dialog_id = getArguments().getLong("dialog_id", 0L);
  private View doneButton;
  private EditText firstNameField;
  private View headerLabelView;

  public ReportOtherActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  public View createView(Context paramContext)
  {
    int j = 3;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ReportChat", 2131166327));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ReportOtherActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (ReportOtherActivity.this.firstNameField.getText().length() == 0));
        TLRPC.TL_account_reportPeer localTL_account_reportPeer = new TLRPC.TL_account_reportPeer();
        localTL_account_reportPeer.peer = MessagesController.getInputPeer((int)ReportOtherActivity.this.dialog_id);
        localTL_account_reportPeer.reason = new TLRPC.TL_inputReportReasonOther();
        localTL_account_reportPeer.reason.text = ReportOtherActivity.this.firstNameField.getText().toString();
        ConnectionsManager.getInstance().sendRequest(localTL_account_reportPeer, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
          }
        });
        ReportOtherActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
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
    this.firstNameField.setMaxLines(3);
    this.firstNameField.setPadding(0, 0, 0, 0);
    paramContext = this.firstNameField;
    if (LocaleController.isRTL);
    for (int i = 5; ; i = 3)
    {
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
          if ((paramInt == 6) && (ReportOtherActivity.this.doneButton != null))
          {
            ReportOtherActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setHint(LocaleController.getString("ReportChatDescription", 2131166328));
      this.firstNameField.setSelection(this.firstNameField.length());
      return this.fragmentView;
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated") };
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
          if (ReportOtherActivity.this.firstNameField != null)
          {
            ReportOtherActivity.this.firstNameField.requestFocus();
            AndroidUtilities.showKeyboard(ReportOtherActivity.this.firstNameField);
          }
        }
      }
      , 100L);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ReportOtherActivity
 * JD-Core Version:    0.6.0
 */