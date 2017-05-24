package org.vidogram.ui;

import B;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.text.Editable;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.security.SecureRandom;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_getPassword;
import org.vidogram.tgnet.TLRPC.TL_account_getPasswordSettings;
import org.vidogram.tgnet.TLRPC.TL_account_noPassword;
import org.vidogram.tgnet.TLRPC.TL_account_password;
import org.vidogram.tgnet.TLRPC.TL_account_passwordInputSettings;
import org.vidogram.tgnet.TLRPC.TL_account_updatePasswordSettings;
import org.vidogram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.vidogram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.vidogram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.account_Password;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class TwoStepVerificationActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private int abortPasswordRow;
  private TextView bottomButton;
  private TextView bottomTextView;
  private int changePasswordRow;
  private int changeRecoveryEmailRow;
  private TLRPC.account_Password currentPassword;
  private byte[] currentPasswordHash = new byte[0];
  private boolean destroyed;
  private ActionBarMenuItem doneItem;
  private String email;
  private boolean emailOnly;
  private EmptyTextProgressView emptyView;
  private String firstPassword;
  private String hint;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loading;
  private EditText passwordEditText;
  private int passwordEmailVerifyDetailRow;
  private int passwordEnabledDetailRow;
  private boolean passwordEntered = true;
  private int passwordSetState;
  private int passwordSetupDetailRow;
  private AlertDialog progressDialog;
  private int rowCount;
  private ScrollView scrollView;
  private int setPasswordDetailRow;
  private int setPasswordRow;
  private int setRecoveryEmailRow;
  private int shadowRow;
  private Runnable shortPollRunnable;
  private TextView titleTextView;
  private int turnPasswordOffRow;
  private int type;
  private boolean waitingForEmail;

  public TwoStepVerificationActivity(int paramInt)
  {
    this.type = paramInt;
    if (paramInt == 0)
      loadPasswordInfo(false);
  }

  private boolean isValidEmail(String paramString)
  {
    if ((paramString == null) || (paramString.length() < 3));
    int i;
    int j;
    do
    {
      return false;
      i = paramString.lastIndexOf('.');
      j = paramString.lastIndexOf('@');
    }
    while ((i < 0) || (j < 0) || (i < j));
    return true;
  }

  private void loadPasswordInfo(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.loading = true;
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
    }
    TLRPC.TL_account_getPassword localTL_account_getPassword = new TLRPC.TL_account_getPassword();
    ConnectionsManager.getInstance().sendRequest(localTL_account_getPassword, new RequestDelegate(paramBoolean)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            boolean bool2 = true;
            TwoStepVerificationActivity.access$1802(TwoStepVerificationActivity.this, false);
            Object localObject;
            if (this.val$error == null)
            {
              if (!TwoStepVerificationActivity.7.this.val$silent)
              {
                localObject = TwoStepVerificationActivity.this;
                if ((TwoStepVerificationActivity.this.currentPassword == null) && (!(this.val$response instanceof TLRPC.TL_account_noPassword)))
                  break label282;
                bool1 = true;
                TwoStepVerificationActivity.access$1902((TwoStepVerificationActivity)localObject, bool1);
              }
              TwoStepVerificationActivity.access$202(TwoStepVerificationActivity.this, (TLRPC.account_Password)this.val$response);
              localObject = TwoStepVerificationActivity.this;
              if (TwoStepVerificationActivity.this.currentPassword.email_unconfirmed_pattern.length() <= 0)
                break label287;
            }
            label282: label287: for (boolean bool1 = bool2; ; bool1 = false)
            {
              TwoStepVerificationActivity.access$2002((TwoStepVerificationActivity)localObject, bool1);
              localObject = new byte[TwoStepVerificationActivity.this.currentPassword.new_salt.length + 8];
              Utilities.random.nextBytes(localObject);
              System.arraycopy(TwoStepVerificationActivity.this.currentPassword.new_salt, 0, localObject, 0, TwoStepVerificationActivity.this.currentPassword.new_salt.length);
              TwoStepVerificationActivity.this.currentPassword.new_salt = ((B)localObject);
              if ((TwoStepVerificationActivity.this.type == 0) && (!TwoStepVerificationActivity.this.destroyed) && (TwoStepVerificationActivity.this.shortPollRunnable == null))
              {
                TwoStepVerificationActivity.access$2202(TwoStepVerificationActivity.this, new Runnable()
                {
                  public void run()
                  {
                    if (TwoStepVerificationActivity.this.shortPollRunnable == null)
                      return;
                    TwoStepVerificationActivity.this.loadPasswordInfo(true);
                    TwoStepVerificationActivity.access$2202(TwoStepVerificationActivity.this, null);
                  }
                });
                AndroidUtilities.runOnUIThread(TwoStepVerificationActivity.this.shortPollRunnable, 5000L);
              }
              TwoStepVerificationActivity.this.updateRows();
              return;
              bool1 = false;
              break;
            }
          }
        });
      }
    }
    , 10);
  }

  private void needHideProgress()
  {
    if (this.progressDialog == null)
      return;
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private void needShowProgress()
  {
    if ((getParentActivity() == null) || (getParentActivity().isFinishing()) || (this.progressDialog != null))
      return;
    this.progressDialog = new AlertDialog(getParentActivity(), 1);
    this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165920));
    this.progressDialog.setCanceledOnTouchOutside(false);
    this.progressDialog.setCancelable(false);
    this.progressDialog.show();
  }

  private void onPasscodeError(boolean paramBoolean)
  {
    if (getParentActivity() == null)
      return;
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null)
      localVibrator.vibrate(200L);
    if (paramBoolean)
      this.passwordEditText.setText("");
    AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
  }

  private void processDone()
  {
    Object localObject2;
    if (this.type == 0)
      if (!this.passwordEntered)
      {
        localObject2 = this.passwordEditText.getText().toString();
        if (((String)localObject2).length() != 0)
          break label38;
        onPasscodeError(false);
      }
    label38: 
    do
    {
      do
      {
        return;
        Object localObject1 = null;
        try
        {
          localObject2 = ((String)localObject2).getBytes("UTF-8");
          localObject1 = localObject2;
          needShowProgress();
          localObject2 = new byte[this.currentPassword.current_salt.length * 2 + localObject1.length];
          System.arraycopy(this.currentPassword.current_salt, 0, localObject2, 0, this.currentPassword.current_salt.length);
          System.arraycopy(localObject1, 0, localObject2, this.currentPassword.current_salt.length, localObject1.length);
          System.arraycopy(this.currentPassword.current_salt, 0, localObject2, localObject2.length - this.currentPassword.current_salt.length, this.currentPassword.current_salt.length);
          localObject1 = new TLRPC.TL_account_getPasswordSettings();
          ((TLRPC.TL_account_getPasswordSettings)localObject1).current_password_hash = Utilities.computeSHA256(localObject2, 0, localObject2.length);
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate((TLRPC.TL_account_getPasswordSettings)localObject1)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
              {
                public void run()
                {
                  TwoStepVerificationActivity.this.needHideProgress();
                  if (this.val$error == null)
                  {
                    TwoStepVerificationActivity.access$1102(TwoStepVerificationActivity.this, TwoStepVerificationActivity.10.this.val$req.current_password_hash);
                    TwoStepVerificationActivity.access$1902(TwoStepVerificationActivity.this, true);
                    AndroidUtilities.hideKeyboard(TwoStepVerificationActivity.this.passwordEditText);
                    TwoStepVerificationActivity.this.updateRows();
                    return;
                  }
                  if (this.val$error.text.equals("PASSWORD_HASH_INVALID"))
                  {
                    TwoStepVerificationActivity.this.onPasscodeError(true);
                    return;
                  }
                  if (this.val$error.text.startsWith("FLOOD_WAIT"))
                  {
                    int i = Utilities.parseInt(this.val$error.text).intValue();
                    if (i < 60);
                    for (String str = LocaleController.formatPluralString("Seconds", i); ; str = LocaleController.formatPluralString("Minutes", i / 60))
                    {
                      TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { str }));
                      return;
                    }
                  }
                  TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                }
              });
            }
          }
          , 10);
          return;
        }
        catch (Exception localException3)
        {
          while (true)
            FileLog.e(localException3);
        }
      }
      while (this.type != 1);
      if (this.passwordSetState == 0)
      {
        if (this.passwordEditText.getText().length() == 0)
        {
          onPasscodeError(false);
          return;
        }
        this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", 2131166314));
        this.firstPassword = this.passwordEditText.getText().toString();
        setPasswordSetState(1);
        return;
      }
      if (this.passwordSetState == 1)
      {
        if (!this.firstPassword.equals(this.passwordEditText.getText().toString()))
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordDoNotMatch", 2131166199), 0).show();
            onPasscodeError(true);
            return;
          }
          catch (Exception localException1)
          {
            while (true)
              FileLog.e(localException1);
          }
        setPasswordSetState(2);
        return;
      }
      if (this.passwordSetState == 2)
      {
        this.hint = this.passwordEditText.getText().toString();
        if (this.hint.toLowerCase().equals(this.firstPassword.toLowerCase()))
          try
          {
            Toast.makeText(getParentActivity(), LocaleController.getString("PasswordAsHintError", 2131166197), 0).show();
            onPasscodeError(false);
            return;
          }
          catch (Exception localException2)
          {
            while (true)
              FileLog.e(localException2);
          }
        if (!this.currentPassword.has_recovery)
        {
          setPasswordSetState(3);
          return;
        }
        this.email = "";
        setNewPassword(false);
        return;
      }
      if (this.passwordSetState != 3)
        continue;
      this.email = this.passwordEditText.getText().toString();
      if (!isValidEmail(this.email))
      {
        onPasscodeError(false);
        return;
      }
      setNewPassword(false);
      return;
    }
    while (this.passwordSetState != 4);
    String str = this.passwordEditText.getText().toString();
    if (str.length() == 0)
    {
      onPasscodeError(false);
      return;
    }
    TLRPC.TL_auth_recoverPassword localTL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
    localTL_auth_recoverPassword.code = str;
    ConnectionsManager.getInstance().sendRequest(localTL_auth_recoverPassword, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
        {
          public void run()
          {
            Object localObject;
            if (this.val$error == null)
            {
              localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[0]);
                  TwoStepVerificationActivity.this.finishFragment();
                }
              });
              ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("PasswordReset", 2131166204));
              ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
              localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
              if (localObject != null)
              {
                ((Dialog)localObject).setCanceledOnTouchOutside(false);
                ((Dialog)localObject).setCancelable(false);
              }
              return;
            }
            if (this.val$error.text.startsWith("CODE_INVALID"))
            {
              TwoStepVerificationActivity.this.onPasscodeError(true);
              return;
            }
            if (this.val$error.text.startsWith("FLOOD_WAIT"))
            {
              int i = Utilities.parseInt(this.val$error.text).intValue();
              if (i < 60);
              for (localObject = LocaleController.formatPluralString("Seconds", i); ; localObject = LocaleController.formatPluralString("Minutes", i / 60))
              {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                return;
              }
            }
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), this.val$error.text);
          }
        });
      }
    }
    , 10);
  }

  private void setNewPassword(boolean paramBoolean)
  {
    TLRPC.TL_account_updatePasswordSettings localTL_account_updatePasswordSettings = new TLRPC.TL_account_updatePasswordSettings();
    localTL_account_updatePasswordSettings.current_password_hash = this.currentPasswordHash;
    localTL_account_updatePasswordSettings.new_settings = new TLRPC.TL_account_passwordInputSettings();
    if (paramBoolean)
      if ((this.waitingForEmail) && ((this.currentPassword instanceof TLRPC.TL_account_noPassword)))
      {
        localTL_account_updatePasswordSettings.new_settings.flags = 2;
        localTL_account_updatePasswordSettings.new_settings.email = "";
        localTL_account_updatePasswordSettings.current_password_hash = new byte[0];
      }
    while (true)
    {
      needShowProgress();
      ConnectionsManager.getInstance().sendRequest(localTL_account_updatePasswordSettings, new RequestDelegate(paramBoolean, localTL_account_updatePasswordSettings)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              TwoStepVerificationActivity.this.needHideProgress();
              if ((this.val$error == null) && ((this.val$response instanceof TLRPC.TL_boolTrue)))
                if (TwoStepVerificationActivity.9.this.val$clear)
                {
                  TwoStepVerificationActivity.access$202(TwoStepVerificationActivity.this, null);
                  TwoStepVerificationActivity.access$1102(TwoStepVerificationActivity.this, new byte[0]);
                  TwoStepVerificationActivity.this.loadPasswordInfo(false);
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.didRemovedTwoStepPassword, new Object[0]);
                  TwoStepVerificationActivity.this.updateRows();
                }
              label97: Object localObject;
              while (true)
              {
                break label97;
                do
                  return;
                while (TwoStepVerificationActivity.this.getParentActivity() == null);
                localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[] { TwoStepVerificationActivity.9.this.val$req.new_settings.new_password_hash });
                    TwoStepVerificationActivity.this.finishFragment();
                  }
                });
                ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("YourPasswordSuccessText", 2131166658));
                ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("YourPasswordSuccess", 2131166657));
                localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
                if (localObject == null)
                  continue;
                ((Dialog)localObject).setCanceledOnTouchOutside(false);
                ((Dialog)localObject).setCancelable(false);
                return;
                if (this.val$error == null)
                  continue;
                if (!this.val$error.text.equals("EMAIL_UNCONFIRMED"))
                  break;
                localObject = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetTwoStepPassword, new Object[] { TwoStepVerificationActivity.9.this.val$req.new_settings.new_password_hash });
                    TwoStepVerificationActivity.this.finishFragment();
                  }
                });
                ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("YourEmailAlmostThereText", 2131166650));
                ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("YourEmailAlmostThere", 2131166649));
                localObject = TwoStepVerificationActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
                if (localObject == null)
                  continue;
                ((Dialog)localObject).setCanceledOnTouchOutside(false);
                ((Dialog)localObject).setCancelable(false);
                return;
              }
              if (this.val$error.text.equals("EMAIL_INVALID"))
              {
                TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), LocaleController.getString("PasswordEmailInvalid", 2131166200));
                return;
              }
              if (this.val$error.text.startsWith("FLOOD_WAIT"))
              {
                int i = Utilities.parseInt(this.val$error.text).intValue();
                if (i < 60);
                for (localObject = LocaleController.formatPluralString("Seconds", i); ; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                {
                  TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                  return;
                }
              }
              TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), this.val$error.text);
            }
          });
        }
      }
      , 10);
      return;
      localTL_account_updatePasswordSettings.new_settings.flags = 3;
      localTL_account_updatePasswordSettings.new_settings.hint = "";
      localTL_account_updatePasswordSettings.new_settings.new_password_hash = new byte[0];
      localTL_account_updatePasswordSettings.new_settings.new_salt = new byte[0];
      localTL_account_updatePasswordSettings.new_settings.email = "";
      continue;
      Object localObject;
      if ((this.firstPassword != null) && (this.firstPassword.length() > 0))
        localObject = null;
      try
      {
        byte[] arrayOfByte1 = this.firstPassword.getBytes("UTF-8");
        localObject = arrayOfByte1;
        arrayOfByte1 = this.currentPassword.new_salt;
        byte[] arrayOfByte2 = new byte[arrayOfByte1.length * 2 + localObject.length];
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, arrayOfByte1.length);
        System.arraycopy(localObject, 0, arrayOfByte2, arrayOfByte1.length, localObject.length);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, arrayOfByte2.length - arrayOfByte1.length, arrayOfByte1.length);
        localObject = localTL_account_updatePasswordSettings.new_settings;
        ((TLRPC.TL_account_passwordInputSettings)localObject).flags |= 1;
        localTL_account_updatePasswordSettings.new_settings.hint = this.hint;
        localTL_account_updatePasswordSettings.new_settings.new_password_hash = Utilities.computeSHA256(arrayOfByte2, 0, arrayOfByte2.length);
        localTL_account_updatePasswordSettings.new_settings.new_salt = arrayOfByte1;
        if (this.email.length() <= 0)
          continue;
        localObject = localTL_account_updatePasswordSettings.new_settings;
        ((TLRPC.TL_account_passwordInputSettings)localObject).flags |= 2;
        localTL_account_updatePasswordSettings.new_settings.email = this.email;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  private void setPasswordSetState(int paramInt)
  {
    int i = 4;
    if (this.passwordEditText == null)
      return;
    this.passwordSetState = paramInt;
    if (this.passwordSetState == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("YourPassword", 2131166656));
      if ((this.currentPassword instanceof TLRPC.TL_account_noPassword))
      {
        this.titleTextView.setText(LocaleController.getString("PleaseEnterFirstPassword", 2131166290));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
    }
    while (true)
    {
      this.passwordEditText.setText("");
      return;
      this.titleTextView.setText(LocaleController.getString("PleaseEnterPassword", 2131166291));
      break;
      if (this.passwordSetState == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("YourPassword", 2131166656));
        this.titleTextView.setText(LocaleController.getString("PleaseReEnterPassword", 2131166292));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
        continue;
      }
      if (this.passwordSetState == 2)
      {
        this.actionBar.setTitle(LocaleController.getString("PasswordHint", 2131166201));
        this.titleTextView.setText(LocaleController.getString("PasswordHintText", 2131166202));
        this.passwordEditText.setImeOptions(5);
        this.passwordEditText.setTransformationMethod(null);
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
        continue;
      }
      if (this.passwordSetState == 3)
      {
        this.actionBar.setTitle(LocaleController.getString("RecoveryEmail", 2131166317));
        this.titleTextView.setText(LocaleController.getString("YourEmail", 2131166648));
        this.passwordEditText.setImeOptions(6);
        this.passwordEditText.setTransformationMethod(null);
        this.passwordEditText.setInputType(33);
        this.bottomTextView.setVisibility(0);
        TextView localTextView = this.bottomButton;
        if (this.emailOnly);
        for (paramInt = i; ; paramInt = 0)
        {
          localTextView.setVisibility(paramInt);
          break;
        }
      }
      if (this.passwordSetState != 4)
        continue;
      this.actionBar.setTitle(LocaleController.getString("PasswordRecovery", 2131166203));
      this.titleTextView.setText(LocaleController.getString("PasswordCode", 2131166198));
      this.bottomTextView.setText(LocaleController.getString("RestoreEmailSentInfo", 2131166354));
      this.bottomButton.setText(LocaleController.formatString("RestoreEmailTrouble", 2131166355, new Object[] { this.currentPassword.email_unconfirmed_pattern }));
      this.passwordEditText.setImeOptions(6);
      this.passwordEditText.setTransformationMethod(null);
      this.passwordEditText.setInputType(3);
      this.bottomTextView.setVisibility(0);
      this.bottomButton.setVisibility(0);
    }
  }

  private void showAlertWithText(String paramString1, String paramString2)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    showDialog(localBuilder.create());
  }

  private void updateRows()
  {
    this.rowCount = 0;
    this.setPasswordRow = -1;
    this.setPasswordDetailRow = -1;
    this.changePasswordRow = -1;
    this.turnPasswordOffRow = -1;
    this.setRecoveryEmailRow = -1;
    this.changeRecoveryEmailRow = -1;
    this.abortPasswordRow = -1;
    this.passwordSetupDetailRow = -1;
    this.passwordEnabledDetailRow = -1;
    this.passwordEmailVerifyDetailRow = -1;
    this.shadowRow = -1;
    int i;
    if ((!this.loading) && (this.currentPassword != null))
    {
      if (!(this.currentPassword instanceof TLRPC.TL_account_noPassword))
        break label291;
      if (this.waitingForEmail)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.passwordSetupDetailRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.abortPasswordRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.shadowRow = i;
      }
    }
    else
    {
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
      if (!this.passwordEntered)
        break label429;
      if (this.listView != null)
      {
        this.listView.setVisibility(0);
        this.scrollView.setVisibility(4);
        this.emptyView.setVisibility(0);
        this.listView.setEmptyView(this.emptyView);
      }
      if (this.passwordEditText != null)
      {
        this.doneItem.setVisibility(8);
        this.passwordEditText.setVisibility(4);
        this.titleTextView.setVisibility(4);
        this.bottomTextView.setVisibility(4);
        this.bottomButton.setVisibility(4);
      }
    }
    label291: label429: 
    do
    {
      return;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.setPasswordRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.setPasswordDetailRow = i;
      break;
      if (!(this.currentPassword instanceof TLRPC.TL_account_password))
        break;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.changePasswordRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.turnPasswordOffRow = i;
      if (this.currentPassword.has_recovery)
      {
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.changeRecoveryEmailRow = i;
      }
      while (true)
      {
        if (!this.waitingForEmail)
          break label409;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.passwordEmailVerifyDetailRow = i;
        break;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.setRecoveryEmailRow = i;
      }
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.passwordEnabledDetailRow = i;
      break;
      if (this.listView == null)
        continue;
      this.listView.setEmptyView(null);
      this.listView.setVisibility(4);
      this.scrollView.setVisibility(0);
      this.emptyView.setVisibility(4);
    }
    while (this.passwordEditText == null);
    label409: this.doneItem.setVisibility(0);
    this.passwordEditText.setVisibility(0);
    this.titleTextView.setVisibility(0);
    this.bottomButton.setVisibility(0);
    this.bottomTextView.setVisibility(4);
    this.bottomButton.setText(LocaleController.getString("ForgotPassword", 2131165718));
    if ((this.currentPassword.hint != null) && (this.currentPassword.hint.length() > 0))
      this.passwordEditText.setHint(this.currentPassword.hint);
    while (true)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (TwoStepVerificationActivity.this.passwordEditText != null)
          {
            TwoStepVerificationActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
          }
        }
      }
      , 200L);
      return;
      this.passwordEditText.setHint("");
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          TwoStepVerificationActivity.this.finishFragment();
        do
          return;
        while (paramInt != 1);
        TwoStepVerificationActivity.this.processDone();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    ((FrameLayout)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.scrollView = new ScrollView(paramContext);
    this.scrollView.setFillViewport(true);
    ((FrameLayout)localObject1).addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0F));
    Object localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setOrientation(1);
    this.scrollView.addView((View)localObject2, LayoutHelper.createScroll(-1, -2, 51));
    this.titleTextView = new TextView(paramContext);
    this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
    this.titleTextView.setTextSize(1, 18.0F);
    this.titleTextView.setGravity(1);
    ((LinearLayout)localObject2).addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 38, 0, 0));
    this.passwordEditText = new EditText(paramContext);
    this.passwordEditText.setTextSize(1, 20.0F);
    this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.passwordEditText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.passwordEditText.setMaxLines(1);
    this.passwordEditText.setLines(1);
    this.passwordEditText.setGravity(1);
    this.passwordEditText.setSingleLine(true);
    this.passwordEditText.setInputType(129);
    this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    this.passwordEditText.setTypeface(Typeface.DEFAULT);
    AndroidUtilities.clearCursorDrawable(this.passwordEditText);
    ((LinearLayout)localObject2).addView(this.passwordEditText, LayoutHelper.createLinear(-1, 36, 51, 40, 32, 40, 0));
    this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
      {
        if ((paramInt == 5) || (paramInt == 6))
        {
          TwoStepVerificationActivity.this.processDone();
          return true;
        }
        return false;
      }
    });
    this.passwordEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
    {
      public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
      {
        return false;
      }

      public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
      {
        return false;
      }

      public void onDestroyActionMode(ActionMode paramActionMode)
      {
      }

      public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
      {
        return false;
      }
    });
    this.bottomTextView = new TextView(paramContext);
    this.bottomTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
    this.bottomTextView.setTextSize(1, 14.0F);
    Object localObject3 = this.bottomTextView;
    int i;
    if (LocaleController.isRTL)
    {
      i = 5;
      ((TextView)localObject3).setGravity(i | 0x30);
      this.bottomTextView.setText(LocaleController.getString("YourEmailInfo", 2131166651));
      localObject3 = this.bottomTextView;
      if (!LocaleController.isRTL)
        break label868;
      i = 5;
      label494: ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-2, -2, i | 0x30, 40, 30, 40, 0));
      localObject3 = new LinearLayout(paramContext);
      ((LinearLayout)localObject3).setGravity(80);
      ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, -1));
      this.bottomButton = new TextView(paramContext);
      this.bottomButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
      this.bottomButton.setTextSize(1, 14.0F);
      localObject2 = this.bottomButton;
      if (!LocaleController.isRTL)
        break label873;
      i = 5;
      label598: ((TextView)localObject2).setGravity(i | 0x50);
      this.bottomButton.setText(LocaleController.getString("YourEmailSkip", 2131166652));
      this.bottomButton.setPadding(0, AndroidUtilities.dp(10.0F), 0, 0);
      localObject2 = this.bottomButton;
      if (!LocaleController.isRTL)
        break label878;
      i = 5;
      label653: ((LinearLayout)localObject3).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i | 0x50, 40, 0, 40, 14));
      this.bottomButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (TwoStepVerificationActivity.this.type == 0)
          {
            if (TwoStepVerificationActivity.this.currentPassword.has_recovery)
            {
              TwoStepVerificationActivity.this.needShowProgress();
              paramView = new TLRPC.TL_auth_requestPasswordRecovery();
              ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate()
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                  {
                    public void run()
                    {
                      TwoStepVerificationActivity.this.needHideProgress();
                      Object localObject;
                      if (this.val$error == null)
                      {
                        localObject = (TLRPC.TL_auth_passwordRecovery)this.val$response;
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
                        localBuilder.setMessage(LocaleController.formatString("RestoreEmailSent", 2131166353, new Object[] { ((TLRPC.TL_auth_passwordRecovery)localObject).email_pattern }));
                        localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener((TLRPC.TL_auth_passwordRecovery)localObject)
                        {
                          public void onClick(DialogInterface paramDialogInterface, int paramInt)
                          {
                            paramDialogInterface = new TwoStepVerificationActivity(1);
                            TwoStepVerificationActivity.access$202(paramDialogInterface, TwoStepVerificationActivity.this.currentPassword);
                            paramDialogInterface.currentPassword.email_unconfirmed_pattern = this.val$res.email_pattern;
                            TwoStepVerificationActivity.access$502(paramDialogInterface, 4);
                            TwoStepVerificationActivity.this.presentFragment(paramDialogInterface);
                          }
                        });
                        localObject = TwoStepVerificationActivity.this.showDialog(localBuilder.create());
                        if (localObject != null)
                        {
                          ((Dialog)localObject).setCanceledOnTouchOutside(false);
                          ((Dialog)localObject).setCancelable(false);
                        }
                        return;
                      }
                      if (this.val$error.text.startsWith("FLOOD_WAIT"))
                      {
                        int i = Utilities.parseInt(this.val$error.text).intValue();
                        if (i < 60);
                        for (localObject = LocaleController.formatPluralString("Seconds", i); ; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                        {
                          TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                          return;
                        }
                      }
                      TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                    }
                  });
                }
              }
              , 10);
              return;
            }
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166358), LocaleController.getString("RestorePasswordNoEmailText", 2131166357));
            return;
          }
          if (TwoStepVerificationActivity.this.passwordSetState == 4)
          {
            TwoStepVerificationActivity.this.showAlertWithText(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166358), LocaleController.getString("RestoreEmailTroubleText", 2131166356));
            return;
          }
          paramView = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
          paramView.setMessage(LocaleController.getString("YourEmailSkipWarningText", 2131166654));
          paramView.setTitle(LocaleController.getString("YourEmailSkipWarning", 2131166653));
          paramView.setPositiveButton(LocaleController.getString("YourEmailSkip", 2131166652), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              TwoStepVerificationActivity.access$702(TwoStepVerificationActivity.this, "");
              TwoStepVerificationActivity.this.setNewPassword(false);
            }
          });
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          TwoStepVerificationActivity.this.showDialog(paramView.create());
        }
      });
      if (this.type != 0)
        break label883;
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.showProgress();
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      this.listView.setEmptyView(this.emptyView);
      this.listView.setVerticalScrollBarEnabled(false);
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      localObject1 = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((RecyclerListView)localObject1).setAdapter(paramContext);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if ((paramInt == TwoStepVerificationActivity.this.setPasswordRow) || (paramInt == TwoStepVerificationActivity.this.changePasswordRow))
          {
            paramView = new TwoStepVerificationActivity(1);
            TwoStepVerificationActivity.access$1102(paramView, TwoStepVerificationActivity.this.currentPasswordHash);
            TwoStepVerificationActivity.access$202(paramView, TwoStepVerificationActivity.this.currentPassword);
            TwoStepVerificationActivity.this.presentFragment(paramView);
          }
          do
          {
            return;
            if ((paramInt != TwoStepVerificationActivity.this.setRecoveryEmailRow) && (paramInt != TwoStepVerificationActivity.this.changeRecoveryEmailRow))
              continue;
            paramView = new TwoStepVerificationActivity(1);
            TwoStepVerificationActivity.access$1102(paramView, TwoStepVerificationActivity.this.currentPasswordHash);
            TwoStepVerificationActivity.access$202(paramView, TwoStepVerificationActivity.this.currentPassword);
            TwoStepVerificationActivity.access$1402(paramView, true);
            TwoStepVerificationActivity.access$502(paramView, 3);
            TwoStepVerificationActivity.this.presentFragment(paramView);
            return;
          }
          while ((paramInt != TwoStepVerificationActivity.this.turnPasswordOffRow) && (paramInt != TwoStepVerificationActivity.this.abortPasswordRow));
          paramView = new AlertDialog.Builder(TwoStepVerificationActivity.this.getParentActivity());
          paramView.setMessage(LocaleController.getString("TurnPasswordOffQuestion", 2131166526));
          paramView.setTitle(LocaleController.getString("AppName", 2131165319));
          paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              TwoStepVerificationActivity.this.setNewPassword(true);
            }
          });
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          TwoStepVerificationActivity.this.showDialog(paramView.create());
        }
      });
      updateRows();
      this.actionBar.setTitle(LocaleController.getString("TwoStepVerification", 2131166527));
      this.titleTextView.setText(LocaleController.getString("PleaseEnterCurrentPassword", 2131166289));
    }
    while (true)
    {
      return this.fragmentView;
      i = 3;
      break;
      label868: i = 3;
      break label494;
      label873: i = 3;
      break label598;
      label878: i = 3;
      break label653;
      label883: if (this.type != 1)
        continue;
      setPasswordSetState(this.passwordSetState);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.didSetTwoStepPassword)
    {
      if ((paramArrayOfObject != null) && (paramArrayOfObject.length > 0) && (paramArrayOfObject[0] != null))
        this.currentPasswordHash = ((byte[])(byte[])paramArrayOfObject[0]);
      loadPasswordInfo(false);
      updateRows();
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.bottomTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.bottomButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    if (this.type == 0)
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.type == 0)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
      if (this.shortPollRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.shortPollRunnable);
        this.shortPollRunnable = null;
      }
      this.destroyed = true;
    }
    if (this.progressDialog != null);
    try
    {
      this.progressDialog.dismiss();
      this.progressDialog = null;
      AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void onResume()
  {
    super.onResume();
    if (this.type == 1)
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (TwoStepVerificationActivity.this.passwordEditText != null)
          {
            TwoStepVerificationActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(TwoStepVerificationActivity.this.passwordEditText);
          }
        }
      }
      , 200L);
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.type == 1))
      AndroidUtilities.showKeyboard(this.passwordEditText);
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      if ((TwoStepVerificationActivity.this.loading) || (TwoStepVerificationActivity.this.currentPassword == null))
        return 0;
      return TwoStepVerificationActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == TwoStepVerificationActivity.this.setPasswordDetailRow) || (paramInt == TwoStepVerificationActivity.this.shadowRow) || (paramInt == TwoStepVerificationActivity.this.passwordSetupDetailRow) || (paramInt == TwoStepVerificationActivity.this.passwordEnabledDetailRow) || (paramInt == TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow))
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i != TwoStepVerificationActivity.this.setPasswordDetailRow) && (i != TwoStepVerificationActivity.this.shadowRow) && (i != TwoStepVerificationActivity.this.passwordSetupDetailRow) && (i != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow) && (i != TwoStepVerificationActivity.this.passwordEnabledDetailRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      }
      do
      {
        do
        {
          return;
          paramViewHolder = (TextSettingsCell)paramViewHolder.itemView;
          paramViewHolder.setTag("windowBackgroundWhiteBlackText");
          paramViewHolder.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          if (paramInt == TwoStepVerificationActivity.this.changePasswordRow)
          {
            paramViewHolder.setText(LocaleController.getString("ChangePassword", 2131165437), true);
            return;
          }
          if (paramInt == TwoStepVerificationActivity.this.setPasswordRow)
          {
            paramViewHolder.setText(LocaleController.getString("SetAdditionalPassword", 2131166438), true);
            return;
          }
          if (paramInt == TwoStepVerificationActivity.this.turnPasswordOffRow)
          {
            paramViewHolder.setText(LocaleController.getString("TurnPasswordOff", 2131166525), true);
            return;
          }
          if (paramInt == TwoStepVerificationActivity.this.changeRecoveryEmailRow)
          {
            String str = LocaleController.getString("ChangeRecoveryEmail", 2131165442);
            if (TwoStepVerificationActivity.this.abortPasswordRow != -1);
            while (true)
            {
              paramViewHolder.setText(str, bool);
              return;
              bool = false;
            }
          }
          if (paramInt != TwoStepVerificationActivity.this.setRecoveryEmailRow)
            continue;
          paramViewHolder.setText(LocaleController.getString("SetRecoveryEmail", 2131166446), false);
          return;
        }
        while (paramInt != TwoStepVerificationActivity.this.abortPasswordRow);
        paramViewHolder.setTag("windowBackgroundWhiteRedText3");
        paramViewHolder.setTextColor(Theme.getColor("windowBackgroundWhiteRedText3"));
        paramViewHolder.setText(LocaleController.getString("AbortPassword", 2131165223), false);
        return;
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if (paramInt == TwoStepVerificationActivity.this.setPasswordDetailRow)
        {
          paramViewHolder.setText(LocaleController.getString("SetAdditionalPasswordInfo", 2131166439));
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        if (paramInt == TwoStepVerificationActivity.this.shadowRow)
        {
          paramViewHolder.setText("");
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        if (paramInt == TwoStepVerificationActivity.this.passwordSetupDetailRow)
        {
          paramViewHolder.setText(LocaleController.formatString("EmailPasswordConfirmText", 2131165670, new Object[] { TwoStepVerificationActivity.access$200(TwoStepVerificationActivity.this).email_unconfirmed_pattern }));
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837727, "windowBackgroundGrayShadow"));
          return;
        }
        if (paramInt != TwoStepVerificationActivity.this.passwordEnabledDetailRow)
          continue;
        paramViewHolder.setText(LocaleController.getString("EnabledPasswordText", 2131165677));
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
        return;
      }
      while (paramInt != TwoStepVerificationActivity.this.passwordEmailVerifyDetailRow);
      paramViewHolder.setText(LocaleController.formatString("PendingEmailText", 2131166252, new Object[] { TwoStepVerificationActivity.access$200(TwoStepVerificationActivity.this).email_unconfirmed_pattern }));
      paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.TwoStepVerificationActivity
 * JD-Core Version:    0.6.0
 */