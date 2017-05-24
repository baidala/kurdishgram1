package org.vidogram.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_checkUsername;
import org.vidogram.tgnet.TLRPC.TL_account_updateUsername;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.LayoutHelper;

public class ChangeUsernameActivity extends BaseFragment
{
  private static final int done_button = 1;
  private int checkReqId;
  private Runnable checkRunnable;
  private TextView checkTextView;
  private View doneButton;
  boolean findId;
  private EditText firstNameField;
  private TextView helpTextView;
  private boolean ignoreCheck;
  private CharSequence infoText;
  private String lastCheckName;
  private boolean lastNameAvailable;

  public ChangeUsernameActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private boolean checkUserName(String paramString, boolean paramBoolean)
  {
    if ((paramString != null) && (paramString.length() > 0))
      this.checkTextView.setVisibility(0);
    while ((paramBoolean) && (paramString.length() == 0))
    {
      return true;
      this.checkTextView.setVisibility(8);
    }
    if (this.checkRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
      this.checkRunnable = null;
      this.lastCheckName = null;
      if (this.checkReqId != 0)
        ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
    }
    this.lastNameAvailable = false;
    if (paramString != null)
    {
      if ((paramString.startsWith("_")) || (paramString.endsWith("_")))
      {
        this.checkTextView.setText(LocaleController.getString("UsernameInvalid", 2131166557));
        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        return false;
      }
      int i = 0;
      while (i < paramString.length())
      {
        int j = paramString.charAt(i);
        if ((i == 0) && (j >= 48) && (j <= 57))
        {
          if (paramBoolean)
          {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidStartNumber", 2131166560));
            return false;
          }
          this.checkTextView.setText(LocaleController.getString("UsernameInvalidStartNumber", 2131166560));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          return false;
        }
        if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
        {
          if (paramBoolean)
          {
            AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalid", 2131166557));
            return false;
          }
          this.checkTextView.setText(LocaleController.getString("UsernameInvalid", 2131166557));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          return false;
        }
        i += 1;
      }
    }
    if ((paramString == null) || (paramString.length() < 5))
    {
      if (paramBoolean)
      {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidShort", 2131166559));
        return false;
      }
      this.checkTextView.setText(LocaleController.getString("UsernameInvalidShort", 2131166559));
      this.checkTextView.setTag("windowBackgroundWhiteRedText4");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
    if (paramString.length() > 32)
    {
      if (paramBoolean)
      {
        AlertsCreator.showSimpleAlert(this, LocaleController.getString("UsernameInvalidLong", 2131166558));
        return false;
      }
      this.checkTextView.setText(LocaleController.getString("UsernameInvalidLong", 2131166558));
      this.checkTextView.setTag("windowBackgroundWhiteRedText4");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
    if (!paramBoolean)
    {
      String str2 = UserConfig.getCurrentUser().username;
      String str1 = str2;
      if (str2 == null)
        str1 = "";
      if (paramString.equals(str1))
      {
        this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", 2131166551, new Object[] { paramString }));
        this.checkTextView.setTag("windowBackgroundWhiteGreenText");
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
        return true;
      }
      this.checkTextView.setText(LocaleController.getString("UsernameChecking", 2131166552));
      this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
      this.lastCheckName = paramString;
      this.checkRunnable = new Runnable(paramString)
      {
        public void run()
        {
          TLRPC.TL_account_checkUsername localTL_account_checkUsername = new TLRPC.TL_account_checkUsername();
          localTL_account_checkUsername.username = this.val$name;
          ChangeUsernameActivity.access$802(ChangeUsernameActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_account_checkUsername, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  ChangeUsernameActivity.access$802(ChangeUsernameActivity.this, 0);
                  if ((ChangeUsernameActivity.this.lastCheckName != null) && (ChangeUsernameActivity.this.lastCheckName.equals(ChangeUsernameActivity.5.this.val$name)))
                  {
                    if ((this.val$error != null) || (!(this.val$response instanceof TLRPC.TL_boolTrue)))
                      break label226;
                    if (ChangeUsernameActivity.this.findId)
                    {
                      ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameUnavailable", 2131166793, new Object[] { ChangeUsernameActivity.5.this.val$name }));
                      ChangeUsernameActivity.this.checkTextView.setTextColor(-3198928);
                    }
                  }
                  else
                  {
                    return;
                  }
                  ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameAvailable", 2131166551, new Object[] { ChangeUsernameActivity.5.this.val$name }));
                  ChangeUsernameActivity.this.checkTextView.setTextColor(-14248148);
                  ChangeUsernameActivity.access$1102(ChangeUsernameActivity.this, true);
                  return;
                  label226: if (ChangeUsernameActivity.this.findId)
                  {
                    ChangeUsernameActivity.this.checkTextView.setText(LocaleController.formatString("UsernameIsAvailable", 2131166792, new Object[] { ChangeUsernameActivity.5.this.val$name }));
                    ChangeUsernameActivity.this.checkTextView.setTextColor(-14248148);
                    return;
                  }
                  ChangeUsernameActivity.this.checkTextView.setText(LocaleController.getString("UsernameInUse", 2131166556));
                  ChangeUsernameActivity.this.checkTextView.setTextColor(-3198928);
                  ChangeUsernameActivity.access$1102(ChangeUsernameActivity.this, false);
                }
              });
            }
          }
          , 2));
        }
      };
      AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    }
    return true;
  }

  private void saveName()
  {
    if (!checkUserName(this.firstNameField.getText().toString(), true));
    do
    {
      return;
      localObject = UserConfig.getCurrentUser();
    }
    while ((getParentActivity() == null) || (localObject == null));
    String str = ((TLRPC.User)localObject).username;
    Object localObject = str;
    if (str == null)
      localObject = "";
    str = this.firstNameField.getText().toString();
    if (((String)localObject).equals(str))
    {
      finishFragment();
      return;
    }
    localObject = new AlertDialog(getParentActivity(), 1);
    ((AlertDialog)localObject).setMessage(LocaleController.getString("Loading", 2131165920));
    ((AlertDialog)localObject).setCanceledOnTouchOutside(false);
    ((AlertDialog)localObject).setCancelable(false);
    TLRPC.TL_account_updateUsername localTL_account_updateUsername = new TLRPC.TL_account_updateUsername();
    localTL_account_updateUsername.username = str;
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
    int i = ConnectionsManager.getInstance().sendRequest(localTL_account_updateUsername, new RequestDelegate((AlertDialog)localObject, localTL_account_updateUsername)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error == null)
        {
          AndroidUtilities.runOnUIThread(new Runnable((TLRPC.User)paramTLObject)
          {
            public void run()
            {
              try
              {
                ChangeUsernameActivity.6.this.val$progressDialog.dismiss();
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(this.val$user);
                MessagesController.getInstance().putUsers(localArrayList, false);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, false, true);
                UserConfig.saveConfig(true);
                ChangeUsernameActivity.this.finishFragment();
                return;
              }
              catch (Exception localException)
              {
                while (true)
                  FileLog.e(localException);
              }
            }
          });
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
        {
          public void run()
          {
            try
            {
              ChangeUsernameActivity.6.this.val$progressDialog.dismiss();
              AlertsCreator.processError(this.val$error, ChangeUsernameActivity.this, ChangeUsernameActivity.6.this.val$req, new Object[0]);
              return;
            }
            catch (Exception localException)
            {
              while (true)
                FileLog.e(localException);
            }
          }
        });
      }
    }
    , 2);
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
    ((AlertDialog)localObject).setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(i)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
        try
        {
          paramDialogInterface.dismiss();
          return;
        }
        catch (Exception paramDialogInterface)
        {
          FileLog.e(paramDialogInterface);
        }
      }
    });
    ((AlertDialog)localObject).show();
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    if (this.arguments != null)
      this.findId = this.arguments.getBoolean("findId", false);
    TLRPC.User localUser;
    if (this.findId)
    {
      this.actionBar.setTitle(LocaleController.getString("UsernameFinder", 2131166789));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            ChangeUsernameActivity.this.finishFragment();
          do
            return;
          while (paramInt != 1);
          if (ChangeUsernameActivity.this.findId)
          {
            MessagesController.openByUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), ChangeUsernameActivity.this, 0);
            return;
          }
          ChangeUsernameActivity.this.saveName();
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
      if (localUser != null)
        break label797;
      localUser = UserConfig.getCurrentUser();
    }
    label275: label792: label797: 
    while (true)
    {
      this.fragmentView = new LinearLayout(paramContext);
      LinearLayout localLinearLayout = (LinearLayout)this.fragmentView;
      localLinearLayout.setOrientation(1);
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
      this.firstNameField.setPadding(0, 0, 0, 0);
      this.firstNameField.setSingleLine(true);
      Object localObject = this.firstNameField;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((EditText)localObject).setGravity(i);
        this.firstNameField.setInputType(180224);
        this.firstNameField.setImeOptions(6);
        if (!this.findId)
          break label724;
        this.firstNameField.setHint(LocaleController.getString("UsernameHint", 2131166791));
        label323: AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if ((paramInt == 6) && (ChangeUsernameActivity.this.doneButton != null))
            {
              ChangeUsernameActivity.this.doneButton.performClick();
              return true;
            }
            return false;
          }
        });
        this.firstNameField.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramEditable)
          {
            if (!ChangeUsernameActivity.this.findId)
            {
              if (ChangeUsernameActivity.this.firstNameField.length() > 0)
              {
                paramEditable = "https://" + MessagesController.getInstance().linkPrefix + "/" + ChangeUsernameActivity.this.firstNameField.getText();
                Object localObject = LocaleController.formatString("UsernameHelpLink", 2131166555, new Object[] { paramEditable });
                int i = ((String)localObject).indexOf(paramEditable);
                localObject = new SpannableStringBuilder((CharSequence)localObject);
                ((SpannableStringBuilder)localObject).setSpan(new ChangeUsernameActivity.LinkSpan(ChangeUsernameActivity.this, paramEditable), i, paramEditable.length() + i, 33);
                ChangeUsernameActivity.this.helpTextView.setText(TextUtils.concat(new CharSequence[] { ChangeUsernameActivity.access$500(ChangeUsernameActivity.this), "\n\n", localObject }));
              }
            }
            else
              return;
            ChangeUsernameActivity.this.helpTextView.setText(ChangeUsernameActivity.this.infoText);
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
            if (ChangeUsernameActivity.this.ignoreCheck)
              return;
            ChangeUsernameActivity.this.checkUserName(ChangeUsernameActivity.this.firstNameField.getText().toString(), false);
          }
        });
        localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
        this.checkTextView = new TextView(paramContext);
        this.checkTextView.setTextSize(1, 15.0F);
        localObject = this.checkTextView;
        if (!LocaleController.isRTL)
          break label743;
        i = 5;
        label422: ((TextView)localObject).setGravity(i);
        localObject = this.checkTextView;
        if (!LocaleController.isRTL)
          break label748;
        i = 5;
        label442: localLinearLayout.addView((View)localObject, LayoutHelper.createLinear(-2, -2, i, 24, 12, 24, 0));
        this.helpTextView = new TextView(paramContext);
        this.helpTextView.setTextSize(1, 15.0F);
        this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        paramContext = this.helpTextView;
        if (!LocaleController.isRTL)
          break label753;
        i = 5;
        paramContext.setGravity(i);
        if (!this.findId)
          break label758;
        paramContext = this.helpTextView;
        localObject = AndroidUtilities.replaceTags(LocaleController.getString("UsernameFinderHelp", 2131166790));
        this.infoText = ((CharSequence)localObject);
        paramContext.setText((CharSequence)localObject);
        label555: this.helpTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.helpTextView.setHighlightColor(Theme.getColor("windowBackgroundWhiteLinkSelection"));
        this.helpTextView.setMovementMethod(new LinkMovementMethodMy(null));
        paramContext = this.helpTextView;
        if (!LocaleController.isRTL)
          break label792;
      }
      for (int i = 5; ; i = 3)
      {
        localLinearLayout.addView(paramContext, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 0));
        this.checkTextView.setVisibility(8);
        if ((localUser != null) && (localUser.username != null) && (localUser.username.length() > 0))
        {
          this.ignoreCheck = true;
          this.firstNameField.setText(localUser.username);
          this.firstNameField.setSelection(this.firstNameField.length());
          this.ignoreCheck = false;
        }
        return this.fragmentView;
        this.actionBar.setTitle(LocaleController.getString("Username", 2131166550));
        break;
        i = 3;
        break label275;
        label724: this.firstNameField.setHint(LocaleController.getString("UsernamePlaceholder", 2131166561));
        break label323;
        label743: i = 3;
        break label422;
        label748: i = 3;
        break label442;
        label753: i = 3;
        break label512;
        label758: paramContext = this.helpTextView;
        localObject = AndroidUtilities.replaceTags(LocaleController.getString("UsernameHelp", 2131166554));
        this.infoText = ((CharSequence)localObject);
        paramContext.setText((CharSequence)localObject);
        break label555;
      }
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8") };
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
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }

  private static class LinkMovementMethodMy extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
          Selection.removeSelection(paramSpannable);
        return bool;
      }
      catch (Exception paramTextView)
      {
        FileLog.e(paramTextView);
      }
      return false;
    }
  }

  public class LinkSpan extends ClickableSpan
  {
    private String url;

    public LinkSpan(String arg2)
    {
      Object localObject;
      this.url = localObject;
    }

    public void onClick(View paramView)
    {
      try
      {
        ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.url));
        Toast.makeText(ChangeUsernameActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165909), 0).show();
        return;
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }

    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChangeUsernameActivity
 * JD-Core Version:    0.6.0
 */