package org.vidogram.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.VidogramUi.WebRTC.e;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_deleteAccount;
import org.vidogram.tgnet.TLRPC.TL_account_getPassword;
import org.vidogram.tgnet.TLRPC.TL_account_password;
import org.vidogram.tgnet.TLRPC.TL_auth_authorization;
import org.vidogram.tgnet.TLRPC.TL_auth_cancelCode;
import org.vidogram.tgnet.TLRPC.TL_auth_checkPassword;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.vidogram.tgnet.TLRPC.TL_auth_passwordRecovery;
import org.vidogram.tgnet.TLRPC.TL_auth_recoverPassword;
import org.vidogram.tgnet.TLRPC.TL_auth_requestPasswordRecovery;
import org.vidogram.tgnet.TLRPC.TL_auth_resendCode;
import org.vidogram.tgnet.TLRPC.TL_auth_sendCode;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCode;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
import org.vidogram.tgnet.TLRPC.TL_auth_signIn;
import org.vidogram.tgnet.TLRPC.TL_auth_signUp;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.auth_SentCodeType;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.HintEditText;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.SlideView;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = true;
  private boolean checkShowPermissions = true;
  private int currentViewNum;
  private View doneButton;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private Dialog permissionsShowDialog;
  private ArrayList<String> permissionsShowItems = new ArrayList();
  private AlertDialog progressDialog;
  private SlideView[] views = new SlideView[9];

  private void clearCurrentState()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
    localEditor.clear();
    localEditor.commit();
  }

  private void fillNextCodeParams(Bundle paramBundle, TLRPC.TL_auth_sentCode paramTL_auth_sentCode)
  {
    paramBundle.putString("phoneHash", paramTL_auth_sentCode.phone_code_hash);
    if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeCall))
    {
      paramBundle.putInt("nextType", 4);
      if (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeApp))
        break label112;
      paramBundle.putInt("type", 1);
      paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
      setPage(1, true, paramBundle, false);
    }
    label112: 
    do
    {
      return;
      if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeFlashCall))
      {
        paramBundle.putInt("nextType", 3);
        break;
      }
      if (!(paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeSms))
        break;
      paramBundle.putInt("nextType", 2);
      break;
      if (paramTL_auth_sentCode.timeout == 0)
        paramTL_auth_sentCode.timeout = 60;
      paramBundle.putInt("timeout", paramTL_auth_sentCode.timeout * 1000);
      if ((paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeCall))
      {
        paramBundle.putInt("type", 4);
        paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
        setPage(4, true, paramBundle, false);
        return;
      }
      if (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeFlashCall))
        continue;
      paramBundle.putInt("type", 3);
      paramBundle.putString("pattern", paramTL_auth_sentCode.type.pattern);
      setPage(3, true, paramBundle, false);
      return;
    }
    while (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeSms));
    paramBundle.putInt("type", 2);
    paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
    setPage(2, true, paramBundle, false);
  }

  private Bundle loadCurrentState()
  {
    Bundle localBundle;
    while (true)
    {
      Object localObject3;
      String[] arrayOfString;
      try
      {
        localBundle = new Bundle();
        Iterator localIterator = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).getAll().entrySet().iterator();
        if (!localIterator.hasNext())
          break;
        localObject2 = (Map.Entry)localIterator.next();
        String str = (String)((Map.Entry)localObject2).getKey();
        localObject3 = ((Map.Entry)localObject2).getValue();
        arrayOfString = str.split("_\\|_");
        if (arrayOfString.length != 1)
          break label141;
        if ((localObject3 instanceof String))
        {
          localBundle.putString(str, (String)localObject3);
          continue;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return null;
      }
      if (!(localObject3 instanceof Integer))
        continue;
      localBundle.putInt(localException, ((Integer)localObject3).intValue());
      continue;
      label141: if (arrayOfString.length != 2)
        continue;
      Object localObject2 = localBundle.getBundle(arrayOfString[0]);
      Object localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject1 = new Bundle();
        localBundle.putBundle(arrayOfString[0], (Bundle)localObject1);
      }
      if ((localObject3 instanceof String))
      {
        ((Bundle)localObject1).putString(arrayOfString[1], (String)localObject3);
        continue;
      }
      if (!(localObject3 instanceof Integer))
        continue;
      ((Bundle)localObject1).putInt(arrayOfString[1], ((Integer)localObject3).intValue());
    }
    return (Bundle)(Bundle)localBundle;
  }

  private void needFinishActivity()
  {
    setVideoChatConfig(MessagesController.getInstance().getFullName(UserConfig.getCurrentUser()), UserConfig.getClientUserId() + "");
  }

  private void needShowAlert(String paramString1, String paramString2)
  {
    if ((paramString2 == null) || (getParentActivity() == null))
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(paramString1);
    localBuilder.setMessage(paramString2);
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    showDialog(localBuilder.create());
  }

  private void needShowInvalidAlert(String paramString, boolean paramBoolean)
  {
    if (getParentActivity() == null)
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    if (paramBoolean)
      localBuilder.setMessage(LocaleController.getString("BannedPhoneNumber", 2131165382));
    while (true)
    {
      localBuilder.setNeutralButton(LocaleController.getString("BotHelp", 2131165392), new DialogInterface.OnClickListener(paramBoolean, paramString)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          try
          {
            paramDialogInterface = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            paramDialogInterface = String.format(Locale.US, "%s (%d)", new Object[] { paramDialogInterface.versionName, Integer.valueOf(paramDialogInterface.versionCode) });
            Intent localIntent = new Intent("android.intent.action.SEND");
            localIntent.setType("message/rfc822");
            localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "login@stel.com" });
            if (this.val$banned)
            {
              localIntent.putExtra("android.intent.extra.SUBJECT", "Banned phone number: " + this.val$phoneNumber);
              localIntent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + this.val$phoneNumber + "\nBut Telegram says it's banned. Please help.\n\nApp version: " + paramDialogInterface + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
            while (true)
            {
              LoginActivity.this.getParentActivity().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
              localIntent.putExtra("android.intent.extra.SUBJECT", "Invalid phone number: " + this.val$phoneNumber);
              localIntent.putExtra("android.intent.extra.TEXT", "I'm trying to use my mobile phone number: " + this.val$phoneNumber + "\nBut Telegram says it's invalid. Please help.\n\nApp version: " + paramDialogInterface + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault());
            }
          }
          catch (Exception paramDialogInterface)
          {
            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("NoMailInstalled", 2131166031));
          }
        }
      });
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      showDialog(localBuilder.create());
      return;
      localBuilder.setMessage(LocaleController.getString("InvalidPhoneNumber", 2131165841));
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

  private void putBundleToEditor(Bundle paramBundle, SharedPreferences.Editor paramEditor, String paramString)
  {
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = paramBundle.get(str);
      if ((localObject instanceof String))
      {
        if (paramString != null)
        {
          paramEditor.putString(paramString + "_|_" + str, (String)localObject);
          continue;
        }
        paramEditor.putString(str, (String)localObject);
        continue;
      }
      if ((localObject instanceof Integer))
      {
        if (paramString != null)
        {
          paramEditor.putInt(paramString + "_|_" + str, ((Integer)localObject).intValue());
          continue;
        }
        paramEditor.putInt(str, ((Integer)localObject).intValue());
        continue;
      }
      if (!(localObject instanceof Bundle))
        continue;
      putBundleToEditor((Bundle)localObject, paramEditor, str);
    }
  }

  private void setVideoChatConfig(String paramString1, String paramString2)
  {
    new e(getParentActivity()).a(UserConfig.getCurrentUser().first_name, UserConfig.getCurrentUser().last_name, UserConfig.getCurrentUser().id + "", UserConfig.getCurrentUser().phone);
    needHideProgress();
    clearCurrentState();
    presentFragment(new DialogsActivity(null), true);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setTitle(LocaleController.getString("AppName", 2131165319));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == 1)
          LoginActivity.this.views[LoginActivity.this.currentViewNum].onNextPressed();
        do
          return;
        while (paramInt != -1);
        LoginActivity.this.onBackPressed();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    ScrollView localScrollView = (ScrollView)this.fragmentView;
    localScrollView.setFillViewport(true);
    Object localObject = new FrameLayout(paramContext);
    localScrollView.addView((View)localObject, LayoutHelper.createScroll(-1, -2, 51));
    this.views[0] = new PhoneView(paramContext);
    this.views[1] = new LoginActivitySmsView(paramContext, 1);
    this.views[2] = new LoginActivitySmsView(paramContext, 2);
    this.views[3] = new LoginActivitySmsView(paramContext, 3);
    this.views[4] = new LoginActivitySmsView(paramContext, 4);
    this.views[5] = new LoginActivityRegisterView(paramContext);
    this.views[6] = new LoginActivityPasswordView(paramContext);
    this.views[7] = new LoginActivityRecoverView(paramContext);
    this.views[8] = new LoginActivityResetWaitView(paramContext);
    int i = 0;
    int j;
    if (i < this.views.length)
    {
      paramContext = this.views[i];
      label278: float f1;
      label301: float f2;
      label311: float f3;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0)
          break label358;
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet())
          break label365;
        f2 = 26.0F;
        if (!AndroidUtilities.isTablet())
          break label372;
        f3 = 26.0F;
      }
      while (true)
      {
        ((FrameLayout)localObject).addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label278;
        label358: f1 = -1.0F;
        break label301;
        label365: f2 = 18.0F;
        break label311;
        label372: f3 = 18.0F;
      }
    }
    localObject = loadCurrentState();
    paramContext = (Context)localObject;
    if (localObject != null)
    {
      this.currentViewNum = ((Bundle)localObject).getInt("currentViewNum", 0);
      paramContext = (Context)localObject;
      if (this.currentViewNum >= 1)
      {
        paramContext = (Context)localObject;
        if (this.currentViewNum <= 4)
        {
          i = ((Bundle)localObject).getInt("open");
          paramContext = (Context)localObject;
          if (i != 0)
          {
            paramContext = (Context)localObject;
            if (Math.abs(System.currentTimeMillis() / 1000L - i) >= 86400L)
            {
              this.currentViewNum = 0;
              paramContext = null;
              clearCurrentState();
            }
          }
        }
      }
    }
    this.actionBar.setTitle(this.views[this.currentViewNum].getHeaderName());
    i = 0;
    if (i < this.views.length)
    {
      if (paramContext != null)
      {
        if ((i < 1) || (i > 4))
          break label642;
        if (i == this.currentViewNum)
          this.views[i].restoreStateParams(paramContext);
      }
      label550: if (this.currentViewNum == i)
      {
        localObject = this.actionBar;
        if (this.views[i].needBackButton())
        {
          j = 2130837732;
          label583: ((ActionBar)localObject).setBackButtonImage(j);
          this.views[i].setVisibility(0);
          this.views[i].onShow();
          if ((i == 3) || (i == 8))
            this.doneButton.setVisibility(8);
        }
      }
      while (true)
      {
        i += 1;
        break;
        label642: this.views[i].restoreStateParams(paramContext);
        break label550;
        j = 0;
        break label583;
        this.views[i].setVisibility(8);
      }
    }
    return (View)this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    PhoneView localPhoneView = (PhoneView)this.views[0];
    LoginActivitySmsView localLoginActivitySmsView1 = (LoginActivitySmsView)this.views[1];
    LoginActivitySmsView localLoginActivitySmsView2 = (LoginActivitySmsView)this.views[2];
    LoginActivitySmsView localLoginActivitySmsView3 = (LoginActivitySmsView)this.views[3];
    LoginActivitySmsView localLoginActivitySmsView4 = (LoginActivitySmsView)this.views[4];
    LoginActivityRegisterView localLoginActivityRegisterView = (LoginActivityRegisterView)this.views[5];
    LoginActivityPasswordView localLoginActivityPasswordView = (LoginActivityPasswordView)this.views[6];
    LoginActivityRecoverView localLoginActivityRecoverView = (LoginActivityRecoverView)this.views[7];
    LoginActivityResetWaitView localLoginActivityResetWaitView = (LoginActivityResetWaitView)this.views[8];
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(localPhoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"), new ThemeDescription(localPhoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityPasswordView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityPasswordView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivityPasswordView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivityPasswordView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivityPasswordView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivityPasswordView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteRedText6"), new ThemeDescription(localLoginActivityPasswordView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityRegisterView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityRegisterView.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivityRegisterView.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivityRegisterView.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivityRegisterView.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivityRegisterView.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivityRegisterView.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivityRegisterView.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivityRecoverView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityRecoverView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivityRecoverView.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivityRecoverView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivityRecoverView.cancelButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivityResetWaitView.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityResetWaitView.resetAccountText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityResetWaitView.resetAccountTime, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivityResetWaitView.resetAccountButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText6"), new ThemeDescription(localLoginActivitySmsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter") };
  }

  public void needHideProgress()
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

  public boolean onBackPressed()
  {
    int j = 0;
    int i = 0;
    if (this.currentViewNum == 0)
    {
      while (i < this.views.length)
      {
        if (this.views[i] != null)
          this.views[i].onDestroyActivity();
        i += 1;
      }
      clearCurrentState();
      j = 1;
    }
    do
    {
      return j;
      if (this.currentViewNum != 6)
        continue;
      this.views[this.currentViewNum].onBackPressed();
      setPage(0, true, null, true);
      return false;
    }
    while ((this.currentViewNum != 7) && (this.currentViewNum != 8));
    this.views[this.currentViewNum].onBackPressed();
    setPage(6, true, null, true);
    return false;
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT < 23) || ((paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty()) && (getParentActivity() != null)));
    try
    {
      getParentActivity().requestPermissions((String[])this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
      do
        return;
      while ((paramDialog != this.permissionsShowDialog) || (this.permissionsShowItems.isEmpty()) || (getParentActivity() == null));
      try
      {
        getParentActivity().requestPermissions((String[])this.permissionsShowItems.toArray(new String[this.permissionsShowItems.size()]), 7);
        return;
      }
      catch (Exception paramDialog)
      {
        return;
      }
    }
    catch (Exception paramDialog)
    {
    }
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    int i = 0;
    while (i < this.views.length)
    {
      if (this.views[i] != null)
        this.views[i].onDestroyActivity();
      i += 1;
    }
    if (this.progressDialog != null);
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

  public void onPause()
  {
    super.onPause();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 6)
    {
      this.checkPermissions = false;
      if (this.currentViewNum == 0)
        this.views[this.currentViewNum].onNextPressed();
    }
    do
    {
      do
        return;
      while (paramInt != 7);
      this.checkShowPermissions = false;
    }
    while (this.currentViewNum != 0);
    ((PhoneView)this.views[this.currentViewNum]).fillNumber();
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    try
    {
      if ((this.currentViewNum >= 1) && (this.currentViewNum <= 4) && ((this.views[this.currentViewNum] instanceof LoginActivitySmsView)))
      {
        int i = ((LoginActivitySmsView)this.views[this.currentViewNum]).openTime;
        if ((i != 0) && (Math.abs(System.currentTimeMillis() / 1000L - i) >= 86400L))
        {
          this.views[this.currentViewNum].onBackPressed();
          setPage(0, false, null, true);
        }
      }
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    int i = 0;
    while (true)
    {
      try
      {
        paramBundle = new Bundle();
        paramBundle.putInt("currentViewNum", this.currentViewNum);
        if (i > this.currentViewNum)
          continue;
        Object localObject = this.views[i];
        if (localObject != null)
        {
          ((SlideView)localObject).saveStateParams(paramBundle);
          break label91;
          localObject = ApplicationLoader.applicationContext.getSharedPreferences("logininfo2", 0).edit();
          ((SharedPreferences.Editor)localObject).clear();
          putBundleToEditor(paramBundle, (SharedPreferences.Editor)localObject, null);
          ((SharedPreferences.Editor)localObject).commit();
          return;
        }
      }
      catch (Exception paramBundle)
      {
        FileLog.e(paramBundle);
        return;
      }
      label91: i += 1;
    }
  }

  public void setPage(int paramInt, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    int i = 2130837732;
    label67: float f;
    if ((paramInt == 3) || (paramInt == 8))
    {
      this.doneButton.setVisibility(8);
      if (!paramBoolean1)
        break label276;
      SlideView localSlideView = this.views[this.currentViewNum];
      localObject = this.views[paramInt];
      this.currentViewNum = paramInt;
      ActionBar localActionBar = this.actionBar;
      if (!((SlideView)localObject).needBackButton())
        break label245;
      localActionBar.setBackButtonImage(i);
      ((SlideView)localObject).setParams(paramBundle, false);
      this.actionBar.setTitle(((SlideView)localObject).getHeaderName());
      ((SlideView)localObject).onShow();
      if (!paramBoolean2)
        break label251;
      f = -AndroidUtilities.displaySize.x;
      label113: ((SlideView)localObject).setX(f);
      paramBundle = localSlideView.animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener(localSlideView)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
        }

        @SuppressLint({"NewApi"})
        public void onAnimationEnd(Animator paramAnimator)
        {
          this.val$outView.setVisibility(8);
          this.val$outView.setX(0.0F);
        }

        public void onAnimationRepeat(Animator paramAnimator)
        {
        }

        public void onAnimationStart(Animator paramAnimator)
        {
        }
      }).setDuration(300L);
      if (!paramBoolean2)
        break label263;
      f = AndroidUtilities.displaySize.x;
    }
    while (true)
    {
      paramBundle.translationX(f).start();
      ((SlideView)localObject).animate().setInterpolator(new AccelerateDecelerateInterpolator()).setListener(new Animator.AnimatorListener((SlideView)localObject)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
        }

        public void onAnimationRepeat(Animator paramAnimator)
        {
        }

        public void onAnimationStart(Animator paramAnimator)
        {
          this.val$newView.setVisibility(0);
        }
      }).setDuration(300L).translationX(0.0F).start();
      return;
      if (paramInt == 0)
      {
        this.checkPermissions = true;
        this.checkShowPermissions = true;
      }
      this.doneButton.setVisibility(0);
      break;
      label245: i = 0;
      break label67;
      label251: f = AndroidUtilities.displaySize.x;
      break label113;
      label263: f = -AndroidUtilities.displaySize.x;
    }
    label276: Object localObject = this.actionBar;
    if (this.views[paramInt].needBackButton());
    while (true)
    {
      ((ActionBar)localObject).setBackButtonImage(i);
      this.views[this.currentViewNum].setVisibility(8);
      this.currentViewNum = paramInt;
      this.views[paramInt].setParams(paramBundle, false);
      this.views[paramInt].setVisibility(0);
      this.actionBar.setTitle(this.views[paramInt].getHeaderName());
      this.views[paramInt].onShow();
      return;
      i = 0;
    }
  }

  public class LoginActivityPasswordView extends SlideView
  {
    private TextView cancelButton;
    private EditText codeField;
    private TextView confirmTextView;
    private Bundle currentParams;
    private byte[] current_salt;
    private String email_unconfirmed_pattern;
    private boolean has_recovery;
    private String hint;
    private boolean nextPressed;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    private TextView resetAccountButton;
    private TextView resetAccountText;

    public LoginActivityPasswordView(Context arg2)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView(localContext);
      this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject).setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.confirmTextView.setText(LocaleController.getString("LoginPasswordText", 2131165933));
        localObject = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label767;
        i = 5;
        label110: addView((View)localObject, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText(localContext);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(localContext, false));
        this.codeField.setHint(LocaleController.getString("LoginPassword", 2131165932));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setInputType(129);
        this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.codeField.setTypeface(Typeface.DEFAULT);
        localObject = this.codeField;
        if (!LocaleController.isRTL)
          break label772;
        i = 5;
        label275: ((EditText)localObject).setGravity(i);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.LoginActivityPasswordView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        this.cancelButton = new TextView(localContext);
        localObject = this.cancelButton;
        if (!LocaleController.isRTL)
          break label777;
        i = 5;
        label343: ((TextView)localObject).setGravity(i | 0x30);
        this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.cancelButton.setText(LocaleController.getString("ForgotPassword", 2131165718));
        this.cancelButton.setTextSize(1, 14.0F);
        this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        localObject = this.cancelButton;
        if (!LocaleController.isRTL)
          break label782;
        i = 5;
        label430: addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30));
        this.cancelButton.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            if (LoginActivity.LoginActivityPasswordView.this.has_recovery)
            {
              LoginActivity.this.needShowProgress();
              paramView = new TLRPC.TL_auth_requestPasswordRecovery();
              ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate()
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                  {
                    public void run()
                    {
                      LoginActivity.this.needHideProgress();
                      Object localObject;
                      if (this.val$error == null)
                      {
                        localObject = (TLRPC.TL_auth_passwordRecovery)this.val$response;
                        AlertDialog.Builder localBuilder = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
                        localBuilder.setMessage(LocaleController.formatString("RestoreEmailSent", 2131166353, new Object[] { ((TLRPC.TL_auth_passwordRecovery)localObject).email_pattern }));
                        localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener((TLRPC.TL_auth_passwordRecovery)localObject)
                        {
                          public void onClick(DialogInterface paramDialogInterface, int paramInt)
                          {
                            paramDialogInterface = new Bundle();
                            paramDialogInterface.putString("email_unconfirmed_pattern", this.val$res.email_pattern);
                            LoginActivity.this.setPage(7, true, paramDialogInterface, false);
                          }
                        });
                        localObject = LoginActivity.this.showDialog(localBuilder.create());
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
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                          return;
                        }
                      }
                      LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                    }
                  });
                }
              }
              , 10);
              return;
            }
            LoginActivity.LoginActivityPasswordView.this.resetAccountText.setVisibility(0);
            LoginActivity.LoginActivityPasswordView.this.resetAccountButton.setVisibility(0);
            AndroidUtilities.hideKeyboard(LoginActivity.LoginActivityPasswordView.this.codeField);
            LoginActivity.this.needShowAlert(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166358), LocaleController.getString("RestorePasswordNoEmailText", 2131166357));
          }
        });
        this.resetAccountButton = new TextView(localContext);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL)
          break label787;
        i = 5;
        label489: ((TextView)localObject).setGravity(i | 0x30);
        this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
        this.resetAccountButton.setVisibility(8);
        this.resetAccountButton.setText(LocaleController.getString("ResetMyAccount", 2131166345));
        this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.resetAccountButton.setTextSize(1, 14.0F);
        this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL)
          break label792;
        i = 5;
        label597: addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 34, 0, 0));
        this.resetAccountButton.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramView.setMessage(LocaleController.getString("ResetMyAccountWarningText", 2131166349));
            paramView.setTitle(LocaleController.getString("ResetMyAccountWarning", 2131166347));
            paramView.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", 2131166348), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                LoginActivity.this.needShowProgress();
                paramDialogInterface = new TLRPC.TL_account_deleteAccount();
                paramDialogInterface.reason = "Forgot password";
                ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                    {
                      public void run()
                      {
                        LoginActivity.this.needHideProgress();
                        Bundle localBundle;
                        if (this.val$error == null)
                        {
                          localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityPasswordView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityPasswordView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityPasswordView.this.phoneCode);
                          LoginActivity.this.setPage(5, true, localBundle, false);
                          return;
                        }
                        if (this.val$error.text.equals("2FA_RECENT_CONFIRM"))
                        {
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("ResetAccountCancelledAlert", 2131166341));
                          return;
                        }
                        if (this.val$error.text.startsWith("2FA_CONFIRM_WAIT_"))
                        {
                          localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityPasswordView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityPasswordView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityPasswordView.this.phoneCode);
                          localBundle.putInt("startTime", ConnectionsManager.getInstance().getCurrentTime());
                          localBundle.putInt("waitTime", Utilities.parseInt(this.val$error.text.replace("2FA_CONFIRM_WAIT_", "")).intValue());
                          LoginActivity.this.setPage(8, true, localBundle, false);
                          return;
                        }
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                      }
                    });
                  }
                }
                , 10);
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            LoginActivity.this.showDialog(paramView.create());
          }
        });
        this.resetAccountText = new TextView(localContext);
        this$1 = this.resetAccountText;
        if (!LocaleController.isRTL)
          break label797;
        i = 5;
        label660: LoginActivity.this.setGravity(i | 0x30);
        this.resetAccountText.setVisibility(8);
        this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.resetAccountText.setText(LocaleController.getString("ResetMyAccountText", 2131166346));
        this.resetAccountText.setTextSize(1, 14.0F);
        this.resetAccountText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this$1 = this.resetAccountText;
        if (!LocaleController.isRTL)
          break label802;
      }
      label772: label777: label782: label787: label792: label797: label802: for (int i = 5; ; i = 3)
      {
        addView(LoginActivity.this, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 7, 0, 14));
        return;
        i = 3;
        break;
        label767: i = 3;
        break label110;
        i = 3;
        break label275;
        i = 3;
        break label343;
        i = 3;
        break label430;
        i = 3;
        break label489;
        i = 3;
        break label597;
        i = 3;
        break label660;
      }
    }

    private void onPasscodeError(boolean paramBoolean)
    {
      if (LoginActivity.this.getParentActivity() == null)
        return;
      Vibrator localVibrator = (Vibrator)LoginActivity.this.getParentActivity().getSystemService("vibrator");
      if (localVibrator != null)
        localVibrator.vibrate(200L);
      if (paramBoolean)
        this.codeField.setText("");
      AndroidUtilities.shakeView(this.confirmTextView, 2.0F, 0);
    }

    public String getHeaderName()
    {
      return LocaleController.getString("LoginPassword", 2131165932);
    }

    public boolean needBackButton()
    {
      return true;
    }

    public void onBackPressed()
    {
      this.currentParams = null;
    }

    public void onNextPressed()
    {
      if (this.nextPressed)
        return;
      Object localObject2 = this.codeField.getText().toString();
      if (((String)localObject2).length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      this.nextPressed = true;
      Object localObject1 = null;
      try
      {
        localObject2 = ((String)localObject2).getBytes("UTF-8");
        localObject1 = localObject2;
        LoginActivity.this.needShowProgress();
        localObject2 = new byte[this.current_salt.length * 2 + localObject1.length];
        System.arraycopy(this.current_salt, 0, localObject2, 0, this.current_salt.length);
        System.arraycopy(localObject1, 0, localObject2, this.current_salt.length, localObject1.length);
        System.arraycopy(this.current_salt, 0, localObject2, localObject2.length - this.current_salt.length, this.current_salt.length);
        localObject1 = new TLRPC.TL_auth_checkPassword();
        ((TLRPC.TL_auth_checkPassword)localObject1).password_hash = Utilities.computeSHA256(localObject2, 0, localObject2.length);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
            {
              public void run()
              {
                LoginActivity.LoginActivityPasswordView.access$5702(LoginActivity.LoginActivityPasswordView.this, false);
                Object localObject;
                if (this.val$error == null)
                {
                  localObject = (TLRPC.TL_auth_authorization)this.val$response;
                  ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                  UserConfig.clearConfig();
                  MessagesController.getInstance().cleanup();
                  UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                  UserConfig.saveConfig(true);
                  MessagesStorage.getInstance().cleanup(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                  MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                  ContactsController.getInstance().checkAppAccount();
                  MessagesController.getInstance().getBlockedUsers(true);
                  ConnectionsManager.getInstance().updateDcSettings();
                  LoginActivity.this.needFinishActivity();
                  return;
                }
                if (this.val$error.text.equals("PASSWORD_HASH_INVALID"))
                {
                  LoginActivity.LoginActivityPasswordView.this.onPasscodeError(true);
                  return;
                }
                if (this.val$error.text.startsWith("FLOOD_WAIT"))
                {
                  int i = Utilities.parseInt(this.val$error.text).intValue();
                  if (i < 60);
                  for (localObject = LocaleController.formatPluralString("Seconds", i); ; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                    return;
                  }
                }
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
              }
            });
          }
        }
        , 10);
        return;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }

    public void onShow()
    {
      super.onShow();
      if (this.codeField != null)
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
        AndroidUtilities.showKeyboard(this.codeField);
      }
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("passview_params");
      if (this.currentParams != null)
        setParams(this.currentParams, true);
      paramBundle = paramBundle.getString("passview_code");
      if (paramBundle != null)
        this.codeField.setText(paramBundle);
    }

    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("passview_code", str);
      if (this.currentParams != null)
        paramBundle.putBundle("passview_params", this.currentParams);
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      paramBoolean = true;
      if (paramBundle == null)
        return;
      if (paramBundle.isEmpty())
      {
        this.resetAccountButton.setVisibility(0);
        this.resetAccountText.setVisibility(0);
        AndroidUtilities.hideKeyboard(this.codeField);
        return;
      }
      this.resetAccountButton.setVisibility(8);
      this.resetAccountText.setVisibility(8);
      this.codeField.setText("");
      this.currentParams = paramBundle;
      this.current_salt = Utilities.hexToBytes(this.currentParams.getString("current_salt"));
      this.hint = this.currentParams.getString("hint");
      if (this.currentParams.getInt("has_recovery") == 1);
      while (true)
      {
        this.has_recovery = paramBoolean;
        this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
        this.requestPhone = paramBundle.getString("phoneFormated");
        this.phoneHash = paramBundle.getString("phoneHash");
        this.phoneCode = paramBundle.getString("code");
        if ((this.hint == null) || (this.hint.length() <= 0))
          break;
        this.codeField.setHint(this.hint);
        return;
        paramBoolean = false;
      }
      this.codeField.setHint(LocaleController.getString("LoginPassword", 2131165932));
    }
  }

  public class LoginActivityRecoverView extends SlideView
  {
    private TextView cancelButton;
    private EditText codeField;
    private TextView confirmTextView;
    private Bundle currentParams;
    private String email_unconfirmed_pattern;
    private boolean nextPressed;

    public LoginActivityRecoverView(Context arg2)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView((Context)localObject1);
      this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject2 = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject2).setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.confirmTextView.setText(LocaleController.getString("RestoreEmailSentInfo", 2131166354));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label458;
        i = 5;
        label113: addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText((Context)localObject1);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        this.codeField.setHint(LocaleController.getString("PasswordCode", 2131166198));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setInputType(3);
        this.codeField.setTransformationMethod(PasswordTransformationMethod.getInstance());
        this.codeField.setTypeface(Typeface.DEFAULT);
        localObject2 = this.codeField;
        if (!LocaleController.isRTL)
          break label463;
        i = 5;
        label276: ((EditText)localObject2).setGravity(i);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.LoginActivityRecoverView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        this.cancelButton = new TextView((Context)localObject1);
        localObject1 = this.cancelButton;
        if (!LocaleController.isRTL)
          break label468;
        i = 5;
        label343: ((TextView)localObject1).setGravity(i | 0x50);
        this.cancelButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.cancelButton.setTextSize(1, 14.0F);
        this.cancelButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.cancelButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        localObject1 = this.cancelButton;
        if (!LocaleController.isRTL)
          break label473;
      }
      label458: label463: label468: label473: for (int i = j; ; i = 3)
      {
        addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 14));
        this.cancelButton.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramView.setMessage(LocaleController.getString("RestoreEmailTroubleText", 2131166356));
            paramView.setTitle(LocaleController.getString("RestorePasswordNoEmailTitle", 2131166358));
            paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                LoginActivity.this.setPage(6, true, new Bundle(), true);
              }
            });
            paramView = LoginActivity.this.showDialog(paramView.create());
            if (paramView != null)
            {
              paramView.setCanceledOnTouchOutside(false);
              paramView.setCancelable(false);
            }
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label113;
        i = 3;
        break label276;
        i = 3;
        break label343;
      }
    }

    private void onPasscodeError(boolean paramBoolean)
    {
      if (LoginActivity.this.getParentActivity() == null)
        return;
      Vibrator localVibrator = (Vibrator)LoginActivity.this.getParentActivity().getSystemService("vibrator");
      if (localVibrator != null)
        localVibrator.vibrate(200L);
      if (paramBoolean)
        this.codeField.setText("");
      AndroidUtilities.shakeView(this.confirmTextView, 2.0F, 0);
    }

    public String getHeaderName()
    {
      return LocaleController.getString("LoginPassword", 2131165932);
    }

    public boolean needBackButton()
    {
      return true;
    }

    public void onBackPressed()
    {
      this.currentParams = null;
    }

    public void onNextPressed()
    {
      if (this.nextPressed)
        return;
      if (this.codeField.getText().toString().length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      this.nextPressed = true;
      String str = this.codeField.getText().toString();
      if (str.length() == 0)
      {
        onPasscodeError(false);
        return;
      }
      LoginActivity.this.needShowProgress();
      TLRPC.TL_auth_recoverPassword localTL_auth_recoverPassword = new TLRPC.TL_auth_recoverPassword();
      localTL_auth_recoverPassword.code = str;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_recoverPassword, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              LoginActivity.LoginActivityRecoverView.access$6602(LoginActivity.LoginActivityRecoverView.this, false);
              Object localObject;
              if (this.val$error == null)
              {
                localObject = (TLRPC.TL_auth_authorization)this.val$response;
                ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                UserConfig.clearConfig();
                MessagesController.getInstance().cleanup();
                UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                UserConfig.saveConfig(true);
                MessagesStorage.getInstance().cleanup(true);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                ContactsController.getInstance().checkAppAccount();
                MessagesController.getInstance().getBlockedUsers(true);
                ConnectionsManager.getInstance().updateDcSettings();
                LoginActivity.this.needFinishActivity();
                return;
              }
              if (this.val$error.text.startsWith("CODE_INVALID"))
              {
                LoginActivity.LoginActivityRecoverView.this.onPasscodeError(true);
                return;
              }
              if (this.val$error.text.startsWith("FLOOD_WAIT"))
              {
                int i = Utilities.parseInt(this.val$error.text).intValue();
                if (i < 60);
                for (localObject = LocaleController.formatPluralString("Seconds", i); ; localObject = LocaleController.formatPluralString("Minutes", i / 60))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { localObject }));
                  return;
                }
              }
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
            }
          });
        }
      }
      , 10);
    }

    public void onShow()
    {
      super.onShow();
      if (this.codeField != null)
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
      }
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("recoveryview_params");
      if (this.currentParams != null)
        setParams(this.currentParams, true);
      paramBundle = paramBundle.getString("recoveryview_code");
      if (paramBundle != null)
        this.codeField.setText(paramBundle);
    }

    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if ((str != null) && (str.length() != 0))
        paramBundle.putString("recoveryview_code", str);
      if (this.currentParams != null)
        paramBundle.putBundle("recoveryview_params", this.currentParams);
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      if (paramBundle == null)
        return;
      this.codeField.setText("");
      this.currentParams = paramBundle;
      this.email_unconfirmed_pattern = this.currentParams.getString("email_unconfirmed_pattern");
      this.cancelButton.setText(LocaleController.formatString("RestoreEmailTrouble", 2131166355, new Object[] { this.email_unconfirmed_pattern }));
      AndroidUtilities.showKeyboard(this.codeField);
      this.codeField.requestFocus();
    }
  }

  public class LoginActivityRegisterView extends SlideView
  {
    private Bundle currentParams;
    private EditText firstNameField;
    private EditText lastNameField;
    private boolean nextPressed = false;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    private TextView textView;
    private TextView wrongNumber;

    public LoginActivityRegisterView(Context arg2)
    {
      super();
      setOrientation(1);
      this.textView = new TextView((Context)localObject1);
      this.textView.setText(LocaleController.getString("RegisterText", 2131166318));
      this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      Object localObject2 = this.textView;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject2).setGravity(i);
        this.textView.setTextSize(1, 14.0F);
        localObject2 = this.textView;
        if (!LocaleController.isRTL)
          break label568;
        i = 5;
        label102: addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 0, 8, 0, 0));
        this.firstNameField = new EditText((Context)localObject1);
        this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        AndroidUtilities.clearCursorDrawable(this.firstNameField);
        this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165714));
        this.firstNameField.setImeOptions(268435461);
        this.firstNameField.setTextSize(1, 18.0F);
        this.firstNameField.setMaxLines(1);
        this.firstNameField.setInputType(8192);
        addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 0.0F, 26.0F, 0.0F, 0.0F));
        this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.LoginActivityRegisterView.this.lastNameField.requestFocus();
              return true;
            }
            return false;
          }
        });
        this.lastNameField = new EditText((Context)localObject1);
        this.lastNameField.setHint(LocaleController.getString("LastName", 2131165874));
        this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        AndroidUtilities.clearCursorDrawable(this.lastNameField);
        this.lastNameField.setImeOptions(268435461);
        this.lastNameField.setTextSize(1, 18.0F);
        this.lastNameField.setMaxLines(1);
        this.lastNameField.setInputType(8192);
        addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 0.0F, 10.0F, 0.0F, 0.0F));
        localObject2 = new LinearLayout((Context)localObject1);
        ((LinearLayout)localObject2).setGravity(80);
        addView((View)localObject2, LayoutHelper.createLinear(-1, -1));
        this.wrongNumber = new TextView((Context)localObject1);
        this.wrongNumber.setText(LocaleController.getString("CancelRegistration", 2131165432));
        localObject1 = this.wrongNumber;
        if (!LocaleController.isRTL)
          break label573;
        i = 5;
        label454: ((TextView)localObject1).setGravity(i | 0x1);
        this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.wrongNumber.setTextSize(1, 14.0F);
        this.wrongNumber.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        localObject1 = this.wrongNumber;
        if (!LocaleController.isRTL)
          break label578;
      }
      label568: label573: label578: for (int i = 5; ; i = 3)
      {
        ((LinearLayout)localObject2).addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 10));
        this.wrongNumber.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("AppName", 2131165319));
            paramView.setMessage(LocaleController.getString("AreYouSureRegistration", 2131165347));
            paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                LoginActivity.LoginActivityRegisterView.this.onBackPressed();
                LoginActivity.this.setPage(0, true, null, true);
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            LoginActivity.this.showDialog(paramView.create());
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label102;
        i = 3;
        break label454;
      }
    }

    public String getHeaderName()
    {
      return LocaleController.getString("YourName", 2131166655);
    }

    public void onBackPressed()
    {
      this.currentParams = null;
    }

    public void onNextPressed()
    {
      if (this.nextPressed)
        return;
      this.nextPressed = true;
      TLRPC.TL_auth_signUp localTL_auth_signUp = new TLRPC.TL_auth_signUp();
      localTL_auth_signUp.phone_code = this.phoneCode;
      localTL_auth_signUp.phone_code_hash = this.phoneHash;
      localTL_auth_signUp.phone_number = this.requestPhone;
      localTL_auth_signUp.first_name = this.firstNameField.getText().toString();
      localTL_auth_signUp.last_name = this.lastNameField.getText().toString();
      LoginActivity.this.needShowProgress();
      ConnectionsManager.getInstance().sendRequest(localTL_auth_signUp, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              LoginActivity.LoginActivityRegisterView.access$6902(LoginActivity.LoginActivityRegisterView.this, false);
              if (this.val$error == null)
              {
                TLRPC.TL_auth_authorization localTL_auth_authorization = (TLRPC.TL_auth_authorization)this.val$response;
                ConnectionsManager.getInstance().setUserId(localTL_auth_authorization.user.id);
                UserConfig.clearConfig();
                MessagesController.getInstance().cleanup();
                UserConfig.setCurrentUser(localTL_auth_authorization.user);
                UserConfig.saveConfig(true);
                MessagesStorage.getInstance().cleanup(true);
                ArrayList localArrayList = new ArrayList();
                localArrayList.add(localTL_auth_authorization.user);
                MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                MessagesController.getInstance().putUser(localTL_auth_authorization.user, false);
                ContactsController.getInstance().checkAppAccount();
                MessagesController.getInstance().getBlockedUsers(true);
                ConnectionsManager.getInstance().updateDcSettings();
                LoginActivity.this.needFinishActivity();
                return;
              }
              if (this.val$error.text.contains("PHONE_NUMBER_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidPhoneNumber", 2131165841));
                return;
              }
              if ((this.val$error.text.contains("PHONE_CODE_EMPTY")) || (this.val$error.text.contains("PHONE_CODE_INVALID")))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidCode", 2131165837));
                return;
              }
              if (this.val$error.text.contains("PHONE_CODE_EXPIRED"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("CodeExpired", 2131165559));
                return;
              }
              if (this.val$error.text.contains("FIRSTNAME_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidFirstName", 2131165838));
                return;
              }
              if (this.val$error.text.contains("LASTNAME_INVALID"))
              {
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidLastName", 2131165839));
                return;
              }
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
            }
          });
        }
      }
      , 10);
    }

    public void onShow()
    {
      super.onShow();
      if (this.firstNameField != null)
      {
        this.firstNameField.requestFocus();
        this.firstNameField.setSelection(this.firstNameField.length());
      }
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("registerview_params");
      if (this.currentParams != null)
        setParams(this.currentParams, true);
      String str = paramBundle.getString("registerview_first");
      if (str != null)
        this.firstNameField.setText(str);
      paramBundle = paramBundle.getString("registerview_last");
      if (paramBundle != null)
        this.lastNameField.setText(paramBundle);
    }

    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.firstNameField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("registerview_first", str);
      str = this.lastNameField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("registerview_last", str);
      if (this.currentParams != null)
        paramBundle.putBundle("registerview_params", this.currentParams);
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      if (paramBundle == null)
        return;
      this.firstNameField.setText("");
      this.lastNameField.setText("");
      this.requestPhone = paramBundle.getString("phoneFormated");
      this.phoneHash = paramBundle.getString("phoneHash");
      this.phoneCode = paramBundle.getString("code");
      this.currentParams = paramBundle;
    }
  }

  public class LoginActivityResetWaitView extends SlideView
  {
    private TextView confirmTextView;
    private Bundle currentParams;
    private String phoneCode;
    private String phoneHash;
    private String requestPhone;
    private TextView resetAccountButton;
    private TextView resetAccountText;
    private TextView resetAccountTime;
    private int startTime;
    private Runnable timeRunnable;
    private int waitTime;

    public LoginActivityResetWaitView(Context arg2)
    {
      super();
      setOrientation(1);
      this.confirmTextView = new TextView((Context)localObject);
      this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      this.confirmTextView.setTextSize(1, 14.0F);
      TextView localTextView = this.confirmTextView;
      if (LocaleController.isRTL)
      {
        i = 5;
        localTextView.setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localTextView = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label492;
        i = 5;
        label99: addView(localTextView, LayoutHelper.createLinear(-2, -2, i));
        this.resetAccountText = new TextView((Context)localObject);
        localTextView = this.resetAccountText;
        if (!LocaleController.isRTL)
          break label497;
        i = 5;
        label139: localTextView.setGravity(i | 0x30);
        this.resetAccountText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.resetAccountText.setText(LocaleController.getString("ResetAccountStatus", 2131166343));
        this.resetAccountText.setTextSize(1, 14.0F);
        this.resetAccountText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localTextView = this.resetAccountText;
        if (!LocaleController.isRTL)
          break label502;
        i = 5;
        label211: addView(localTextView, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 24, 0, 0));
        this.resetAccountTime = new TextView((Context)localObject);
        localTextView = this.resetAccountTime;
        if (!LocaleController.isRTL)
          break label507;
        i = 5;
        label259: localTextView.setGravity(i | 0x30);
        this.resetAccountTime.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.resetAccountTime.setTextSize(1, 14.0F);
        this.resetAccountTime.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localTextView = this.resetAccountTime;
        if (!LocaleController.isRTL)
          break label512;
        i = 5;
        label317: addView(localTextView, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 2, 0, 0));
        this.resetAccountButton = new TextView((Context)localObject);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL)
          break label517;
        i = 5;
        label363: ((TextView)localObject).setGravity(i | 0x30);
        this.resetAccountButton.setText(LocaleController.getString("ResetAccountButton", 2131166340));
        this.resetAccountButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.resetAccountButton.setTextSize(1, 14.0F);
        this.resetAccountButton.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.resetAccountButton.setPadding(0, AndroidUtilities.dp(14.0F), 0, 0);
        localObject = this.resetAccountButton;
        if (!LocaleController.isRTL)
          break label522;
      }
      label512: label517: label522: for (int i = j; ; i = 3)
      {
        addView((View)localObject, LayoutHelper.createLinear(-2, -2, i | 0x30, 0, 7, 0, 0));
        this.resetAccountButton.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            if (Math.abs(ConnectionsManager.getInstance().getCurrentTime() - LoginActivity.LoginActivityResetWaitView.this.startTime) < LoginActivity.LoginActivityResetWaitView.this.waitTime)
              return;
            paramView = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            paramView.setMessage(LocaleController.getString("ResetMyAccountWarningText", 2131166349));
            paramView.setTitle(LocaleController.getString("ResetMyAccountWarning", 2131166347));
            paramView.setPositiveButton(LocaleController.getString("ResetMyAccountWarningReset", 2131166348), new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                LoginActivity.this.needShowProgress();
                paramDialogInterface = new TLRPC.TL_account_deleteAccount();
                paramDialogInterface.reason = "Forgot password";
                ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                    {
                      public void run()
                      {
                        LoginActivity.this.needHideProgress();
                        if (this.val$error == null)
                        {
                          Bundle localBundle = new Bundle();
                          localBundle.putString("phoneFormated", LoginActivity.LoginActivityResetWaitView.this.requestPhone);
                          localBundle.putString("phoneHash", LoginActivity.LoginActivityResetWaitView.this.phoneHash);
                          localBundle.putString("code", LoginActivity.LoginActivityResetWaitView.this.phoneCode);
                          LoginActivity.this.setPage(5, true, localBundle, false);
                          return;
                        }
                        if (this.val$error.text.equals("2FA_RECENT_CONFIRM"))
                        {
                          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("ResetAccountCancelledAlert", 2131166341));
                          return;
                        }
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                      }
                    });
                  }
                }
                , 10);
              }
            });
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            LoginActivity.this.showDialog(paramView.create());
          }
        });
        return;
        i = 3;
        break;
        label492: i = 3;
        break label99;
        label497: i = 3;
        break label139;
        label502: i = 3;
        break label211;
        label507: i = 3;
        break label259;
        i = 3;
        break label317;
        i = 3;
        break label363;
      }
    }

    private void updateTimeText()
    {
      int i = Math.max(0, this.waitTime - (ConnectionsManager.getInstance().getCurrentTime() - this.startTime));
      int j = i / 86400;
      int k = (i - j * 86400) / 3600;
      int m = (i - j * 86400 - k * 3600) / 60;
      if (j != 0)
        this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DaysBold", j) + " " + LocaleController.formatPluralString("HoursBold", k) + " " + LocaleController.formatPluralString("MinutesBold", m)));
      while (i > 0)
      {
        this.resetAccountButton.setTag("windowBackgroundWhiteGrayText6");
        this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        return;
        this.resetAccountTime.setText(AndroidUtilities.replaceTags(LocaleController.formatPluralString("HoursBold", k) + " " + LocaleController.formatPluralString("MinutesBold", m) + " " + LocaleController.formatPluralString("SecondsBold", i % 60)));
      }
      this.resetAccountButton.setTag("windowBackgroundWhiteRedText6");
      this.resetAccountButton.setTextColor(Theme.getColor("windowBackgroundWhiteRedText6"));
    }

    public String getHeaderName()
    {
      return LocaleController.getString("ResetAccount", 2131166339);
    }

    public boolean needBackButton()
    {
      return true;
    }

    public void onBackPressed()
    {
      AndroidUtilities.cancelRunOnUIThread(this.timeRunnable);
      this.timeRunnable = null;
      this.currentParams = null;
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("resetview_params");
      if (this.currentParams != null)
        setParams(this.currentParams, true);
    }

    public void saveStateParams(Bundle paramBundle)
    {
      if (this.currentParams != null)
        paramBundle.putBundle("resetview_params", this.currentParams);
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      if (paramBundle == null)
        return;
      this.currentParams = paramBundle;
      this.requestPhone = paramBundle.getString("phoneFormated");
      this.phoneHash = paramBundle.getString("phoneHash");
      this.phoneCode = paramBundle.getString("code");
      this.startTime = paramBundle.getInt("startTime");
      this.waitTime = paramBundle.getInt("waitTime");
      this.confirmTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ResetAccountInfo", 2131166342, new Object[] { b.a().e("+" + this.requestPhone) })));
      updateTimeText();
      this.timeRunnable = new Runnable()
      {
        public void run()
        {
          if (LoginActivity.LoginActivityResetWaitView.this.timeRunnable != this)
            return;
          LoginActivity.LoginActivityResetWaitView.this.updateTimeText();
          AndroidUtilities.runOnUIThread(LoginActivity.LoginActivityResetWaitView.this.timeRunnable, 1000L);
        }
      };
      AndroidUtilities.runOnUIThread(this.timeRunnable, 1000L);
    }
  }

  public class LoginActivitySmsView extends SlideView
    implements NotificationCenter.NotificationCenterDelegate
  {
    private String catchedPhone;
    private EditText codeField;
    private volatile int codeTime = 15000;
    private Timer codeTimer;
    private TextView confirmTextView;
    private Bundle currentParams;
    private int currentType;
    private String emailPhone;
    private boolean ignoreOnTextChange;
    private boolean isRestored;
    private double lastCodeTime;
    private double lastCurrentTime;
    private String lastError = "";
    private int length;
    private boolean nextPressed;
    private int nextType;
    private int openTime;
    private String pattern = "*";
    private String phone;
    private String phoneHash;
    private TextView problemText;
    private LoginActivity.ProgressView progressView;
    private String requestPhone;
    private volatile int time = 60000;
    private TextView timeText;
    private Timer timeTimer;
    private int timeout;
    private final Object timerSync = new Object();
    private boolean waitingForEvent;
    private TextView wrongNumber;

    public LoginActivitySmsView(Context paramInt, int arg3)
    {
      super();
      this.currentType = i;
      setOrientation(1);
      this.confirmTextView = new TextView(paramInt);
      this.confirmTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      this.confirmTextView.setTextSize(1, 14.0F);
      Object localObject1 = this.confirmTextView;
      Object localObject2;
      if (LocaleController.isRTL)
      {
        i = 5;
        ((TextView)localObject1).setGravity(i);
        this.confirmTextView.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        if (this.currentType != 3)
          break label975;
        localObject1 = new FrameLayout(paramInt);
        localObject2 = new ImageView(paramInt);
        ((ImageView)localObject2).setImageResource(2130837996);
        if (!LocaleController.isRTL)
          break label909;
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label904;
        i = 5;
        label198: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, i, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL)
          break label970;
        i = 5;
        label225: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText(paramInt);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setHint(LocaleController.getString("Code", 2131165558));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(paramInt, false));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setInputType(3);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.addTextChangedListener(new TextWatcher(LoginActivity.this)
        {
          public void afterTextChanged(Editable paramEditable)
          {
            if (LoginActivity.LoginActivitySmsView.this.ignoreOnTextChange);
            do
              return;
            while ((LoginActivity.LoginActivitySmsView.this.length == 0) || (LoginActivity.LoginActivitySmsView.this.codeField.length() != LoginActivity.LoginActivitySmsView.this.length));
            LoginActivity.LoginActivitySmsView.this.onNextPressed();
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.LoginActivitySmsView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        if (this.currentType == 3)
        {
          this.codeField.setEnabled(false);
          this.codeField.setInputType(0);
          this.codeField.setVisibility(8);
        }
        this.timeText = new TextView(paramInt);
        this.timeText.setTextSize(1, 14.0F);
        this.timeText.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.timeText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL)
          break label1011;
        i = 5;
        label500: ((TextView)localObject1).setGravity(i);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL)
          break label1016;
        i = 5;
        label520: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 30, 0, 0));
        if (this.currentType == 3)
        {
          this.progressView = new LoginActivity.ProgressView(LoginActivity.this, paramInt);
          addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0F, 12.0F, 0.0F, 0.0F));
        }
        this.problemText = new TextView(paramInt);
        this.problemText.setText(LocaleController.getString("DidNotGetTheCode", 2131165655));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL)
          break label1021;
        i = 5;
        label621: ((TextView)localObject1).setGravity(i);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL)
          break label1026;
        i = 5;
        label696: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            if (LoginActivity.LoginActivitySmsView.this.nextPressed)
              return;
            if ((LoginActivity.LoginActivitySmsView.this.nextType != 0) && (LoginActivity.LoginActivitySmsView.this.nextType != 4))
            {
              LoginActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramView = String.format(Locale.US, "%s (%d)", new Object[] { paramView.versionName, Integer.valueOf(paramView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + paramView + " " + LoginActivity.LoginActivitySmsView.this.emailPhone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + LoginActivity.LoginActivitySmsView.this.requestPhone + "\nApp version: " + paramView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + LoginActivity.LoginActivitySmsView.this.lastError);
              LoginActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramView)
            {
              LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("NoMailInstalled", 2131166031));
            }
          }
        });
        localObject1 = new LinearLayout(paramInt);
        if (!LocaleController.isRTL)
          break label1031;
        i = 5;
        label749: ((LinearLayout)localObject1).setGravity(i | 0x10);
        if (!LocaleController.isRTL)
          break label1036;
        i = 5;
        label766: addView((View)localObject1, LayoutHelper.createLinear(-1, -1, i));
        paramInt = new TextView(paramInt);
        if (!LocaleController.isRTL)
          break label1041;
        i = 5;
        label795: paramInt.setGravity(i | 0x1);
        paramInt.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        paramInt.setTextSize(1, 14.0F);
        paramInt.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        paramInt.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        if (!LocaleController.isRTL)
          break label1046;
      }
      label904: label909: label1041: label1046: for (int i = 5; ; i = 3)
      {
        ((LinearLayout)localObject1).addView(paramInt, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 10));
        paramInt.setText(LocaleController.getString("WrongNumber", 2131166632));
        paramInt.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new TLRPC.TL_auth_cancelCode();
            paramView.phone_number = LoginActivity.LoginActivitySmsView.this.requestPhone;
            paramView.phone_code_hash = LoginActivity.LoginActivitySmsView.this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
              }
            }
            , 10);
            LoginActivity.LoginActivitySmsView.this.onBackPressed();
            LoginActivity.this.setPage(0, true, null, true);
          }
        });
        return;
        i = 3;
        break;
        i = 3;
        break label198;
        TextView localTextView = this.confirmTextView;
        if (LocaleController.isRTL);
        for (i = 5; ; i = 3)
        {
          ((FrameLayout)localObject1).addView(localTextView, LayoutHelper.createFrame(-1, -2.0F, i, 0.0F, 0.0F, 82.0F, 0.0F));
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 21, 0.0F, 2.0F, 0.0F, 2.0F));
          break;
        }
        label970: i = 3;
        break label225;
        label975: localObject1 = this.confirmTextView;
        if (LocaleController.isRTL);
        for (i = 5; ; i = 3)
        {
          addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i));
          break;
        }
        label1011: i = 3;
        break label500;
        label1016: i = 3;
        break label520;
        label1021: i = 3;
        break label621;
        i = 3;
        break label696;
        i = 3;
        break label749;
        i = 3;
        break label766;
        i = 3;
        break label795;
      }
    }

    private void createCodeTimer()
    {
      if (this.codeTimer != null)
        return;
      this.codeTime = 15000;
      this.codeTimer = new Timer();
      this.lastCodeTime = System.currentTimeMillis();
      this.codeTimer.schedule(new TimerTask()
      {
        public void run()
        {
          double d1 = System.currentTimeMillis();
          double d2 = LoginActivity.LoginActivitySmsView.this.lastCodeTime;
          LoginActivity.LoginActivitySmsView.access$3502(LoginActivity.LoginActivitySmsView.this, (int)(LoginActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          LoginActivity.LoginActivitySmsView.access$3402(LoginActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (LoginActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                LoginActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
              }
            }
          });
        }
      }
      , 0L, 1000L);
    }

    private void createTimer()
    {
      if (this.timeTimer != null)
        return;
      this.timeTimer = new Timer();
      this.timeTimer.schedule(new TimerTask()
      {
        public void run()
        {
          if (LoginActivity.LoginActivitySmsView.this.timeTimer == null)
            return;
          double d1 = System.currentTimeMillis();
          double d2 = LoginActivity.LoginActivitySmsView.this.lastCurrentTime;
          LoginActivity.LoginActivitySmsView.access$4002(LoginActivity.LoginActivitySmsView.this, (int)(LoginActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          LoginActivity.LoginActivitySmsView.access$3902(LoginActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (LoginActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = LoginActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = LoginActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 3))
                  LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
              }
              do
              {
                do
                {
                  while (true)
                  {
                    if (LoginActivity.LoginActivitySmsView.this.progressView != null)
                      LoginActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - LoginActivity.LoginActivitySmsView.this.time / LoginActivity.LoginActivitySmsView.this.timeout);
                    return;
                    if (LoginActivity.LoginActivitySmsView.this.nextType != 2)
                      continue;
                    LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166475, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                  }
                  if (LoginActivity.LoginActivitySmsView.this.progressView != null)
                    LoginActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  if (LoginActivity.LoginActivitySmsView.this.currentType != 3)
                    continue;
                  AndroidUtilities.setWaitingForCall(false);
                  NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                  LoginActivity.LoginActivitySmsView.access$4602(LoginActivity.LoginActivitySmsView.this, false);
                  LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  LoginActivity.LoginActivitySmsView.this.resendCode();
                  return;
                }
                while (LoginActivity.LoginActivitySmsView.this.currentType != 2);
                if (LoginActivity.LoginActivitySmsView.this.nextType != 4)
                  continue;
                LoginActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165423));
                LoginActivity.LoginActivitySmsView.this.createCodeTimer();
                TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                localTL_auth_resendCode.phone_number = LoginActivity.LoginActivitySmsView.this.requestPhone;
                localTL_auth_resendCode.phone_code_hash = LoginActivity.LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    if ((paramTL_error != null) && (paramTL_error.text != null))
                      AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                      {
                        public void run()
                        {
                          LoginActivity.LoginActivitySmsView.access$3202(LoginActivity.LoginActivitySmsView.this, this.val$error.text);
                        }
                      });
                  }
                }
                , 10);
                return;
              }
              while (LoginActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              LoginActivity.LoginActivitySmsView.access$4602(LoginActivity.LoginActivitySmsView.this, false);
              LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
              LoginActivity.LoginActivitySmsView.this.resendCode();
            }
          });
        }
      }
      , 0L, 1000L);
    }

    private void destroyCodeTimer()
    {
      try
      {
        synchronized (this.timerSync)
        {
          if (this.codeTimer != null)
          {
            this.codeTimer.cancel();
            this.codeTimer = null;
          }
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }

    private void destroyTimer()
    {
      try
      {
        synchronized (this.timerSync)
        {
          if (this.timeTimer != null)
          {
            this.timeTimer.cancel();
            this.timeTimer = null;
          }
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }

    private void resendCode()
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("phone", this.phone);
      localBundle.putString("ephone", this.emailPhone);
      localBundle.putString("phoneFormated", this.requestPhone);
      this.nextPressed = true;
      LoginActivity.this.needShowProgress();
      TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.requestPhone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate(localBundle)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              LoginActivity.LoginActivitySmsView.access$2702(LoginActivity.LoginActivitySmsView.this, false);
              if (this.val$error == null)
                LoginActivity.this.fillNextCodeParams(LoginActivity.LoginActivitySmsView.5.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
              while (true)
              {
                LoginActivity.this.needHideProgress();
                return;
                if (this.val$error.text == null)
                  continue;
                if (this.val$error.text.contains("PHONE_NUMBER_INVALID"))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidPhoneNumber", 2131165841));
                  continue;
                }
                if ((this.val$error.text.contains("PHONE_CODE_EMPTY")) || (this.val$error.text.contains("PHONE_CODE_INVALID")))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidCode", 2131165837));
                  continue;
                }
                if (this.val$error.text.contains("PHONE_CODE_EXPIRED"))
                {
                  LoginActivity.LoginActivitySmsView.this.onBackPressed();
                  LoginActivity.this.setPage(0, true, null, true);
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("CodeExpired", 2131165559));
                  continue;
                }
                if (this.val$error.text.startsWith("FLOOD_WAIT"))
                {
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("FloodWait", 2131165715));
                  continue;
                }
                if (this.val$error.code == -1000)
                  continue;
                LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("ErrorOccurred", 2131165701) + "\n" + this.val$error.text);
              }
            }
          });
        }
      }
      , 10);
    }

    public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
    {
      if ((!this.waitingForEvent) || (this.codeField == null));
      do
      {
        do
        {
          return;
          if (paramInt != NotificationCenter.didReceiveSmsCode)
            continue;
          this.ignoreOnTextChange = true;
          this.codeField.setText("" + paramArrayOfObject[0]);
          this.ignoreOnTextChange = false;
          onNextPressed();
          return;
        }
        while (paramInt != NotificationCenter.didReceiveCall);
        paramArrayOfObject = "" + paramArrayOfObject[0];
      }
      while (!AndroidUtilities.checkPhonePattern(this.pattern, paramArrayOfObject));
      if (!this.pattern.equals("*"))
      {
        this.catchedPhone = paramArrayOfObject;
        AndroidUtilities.endIncomingCall();
        AndroidUtilities.removeLoginPhoneCall(paramArrayOfObject, true);
      }
      this.ignoreOnTextChange = true;
      this.codeField.setText(paramArrayOfObject);
      this.ignoreOnTextChange = false;
      onNextPressed();
    }

    public String getHeaderName()
    {
      return LocaleController.getString("YourCode", 2131166647);
    }

    public void onBackPressed()
    {
      destroyTimer();
      destroyCodeTimer();
      this.currentParams = null;
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      while (true)
      {
        this.waitingForEvent = false;
        return;
        if (this.currentType != 3)
          continue;
        AndroidUtilities.setWaitingForCall(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
      }
    }

    public void onDestroyActivity()
    {
      super.onDestroyActivity();
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      while (true)
      {
        this.waitingForEvent = false;
        destroyTimer();
        destroyCodeTimer();
        return;
        if (this.currentType != 3)
          continue;
        AndroidUtilities.setWaitingForCall(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
      }
    }

    public void onNextPressed()
    {
      if (this.nextPressed)
        return;
      this.nextPressed = true;
      if (this.currentType == 2)
      {
        AndroidUtilities.setWaitingForSms(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
      }
      while (true)
      {
        this.waitingForEvent = false;
        String str = this.codeField.getText().toString();
        TLRPC.TL_auth_signIn localTL_auth_signIn = new TLRPC.TL_auth_signIn();
        localTL_auth_signIn.phone_number = this.requestPhone;
        localTL_auth_signIn.phone_code = str;
        localTL_auth_signIn.phone_code_hash = this.phoneHash;
        destroyTimer();
        LoginActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_auth_signIn, new RequestDelegate(localTL_auth_signIn, str)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
            {
              public void run()
              {
                LoginActivity.LoginActivitySmsView.access$2702(LoginActivity.LoginActivitySmsView.this, false);
                Object localObject;
                int i;
                if (this.val$error == null)
                {
                  localObject = (TLRPC.TL_auth_authorization)this.val$response;
                  ConnectionsManager.getInstance().setUserId(((TLRPC.TL_auth_authorization)localObject).user.id);
                  LoginActivity.LoginActivitySmsView.this.destroyTimer();
                  LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  UserConfig.clearConfig();
                  MessagesController.getInstance().cleanup();
                  UserConfig.setCurrentUser(((TLRPC.TL_auth_authorization)localObject).user);
                  UserConfig.saveConfig(true);
                  MessagesStorage.getInstance().cleanup(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(((TLRPC.TL_auth_authorization)localObject).user);
                  MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance().putUser(((TLRPC.TL_auth_authorization)localObject).user, false);
                  ContactsController.getInstance().checkAppAccount();
                  MessagesController.getInstance().getBlockedUsers(true);
                  ConnectionsManager.getInstance().updateDcSettings();
                  LoginActivity.this.needFinishActivity();
                  i = 1;
                }
                while (true)
                {
                  if ((i != 0) && (LoginActivity.LoginActivitySmsView.this.currentType == 3))
                  {
                    AndroidUtilities.endIncomingCall();
                    AndroidUtilities.removeLoginPhoneCall(LoginActivity.LoginActivitySmsView.8.this.val$code, true);
                  }
                  return;
                  LoginActivity.LoginActivitySmsView.access$3202(LoginActivity.LoginActivitySmsView.this, this.val$error.text);
                  if (this.val$error.text.contains("PHONE_NUMBER_UNOCCUPIED"))
                  {
                    LoginActivity.this.needHideProgress();
                    localObject = new Bundle();
                    ((Bundle)localObject).putString("phoneFormated", LoginActivity.LoginActivitySmsView.this.requestPhone);
                    ((Bundle)localObject).putString("phoneHash", LoginActivity.LoginActivitySmsView.this.phoneHash);
                    ((Bundle)localObject).putString("code", LoginActivity.LoginActivitySmsView.8.this.val$req.phone_code);
                    LoginActivity.this.setPage(5, true, (Bundle)localObject, false);
                    LoginActivity.LoginActivitySmsView.this.destroyTimer();
                    LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                    i = 1;
                    continue;
                  }
                  if (this.val$error.text.contains("SESSION_PASSWORD_NEEDED"))
                  {
                    localObject = new TLRPC.TL_account_getPassword();
                    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
                    {
                      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                      {
                        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                        {
                          public void run()
                          {
                            LoginActivity.this.needHideProgress();
                            if (this.val$error == null)
                            {
                              TLRPC.TL_account_password localTL_account_password = (TLRPC.TL_account_password)this.val$response;
                              Bundle localBundle = new Bundle();
                              localBundle.putString("current_salt", Utilities.bytesToHex(localTL_account_password.current_salt));
                              localBundle.putString("hint", localTL_account_password.hint);
                              localBundle.putString("email_unconfirmed_pattern", localTL_account_password.email_unconfirmed_pattern);
                              localBundle.putString("phoneFormated", LoginActivity.LoginActivitySmsView.this.requestPhone);
                              localBundle.putString("phoneHash", LoginActivity.LoginActivitySmsView.this.phoneHash);
                              localBundle.putString("code", LoginActivity.LoginActivitySmsView.8.this.val$req.phone_code);
                              if (localTL_account_password.has_recovery);
                              for (int i = 1; ; i = 0)
                              {
                                localBundle.putInt("has_recovery", i);
                                LoginActivity.this.setPage(6, true, localBundle, false);
                                return;
                              }
                            }
                            LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                          }
                        });
                      }
                    }
                    , 10);
                    LoginActivity.LoginActivitySmsView.this.destroyTimer();
                    LoginActivity.LoginActivitySmsView.this.destroyCodeTimer();
                    i = 1;
                    continue;
                  }
                  LoginActivity.this.needHideProgress();
                  if (((LoginActivity.LoginActivitySmsView.this.currentType == 3) && ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 2))) || ((LoginActivity.LoginActivitySmsView.this.currentType == 2) && ((LoginActivity.LoginActivitySmsView.this.nextType == 4) || (LoginActivity.LoginActivitySmsView.this.nextType == 3))))
                    LoginActivity.LoginActivitySmsView.this.createTimer();
                  if (LoginActivity.LoginActivitySmsView.this.currentType == 2)
                  {
                    AndroidUtilities.setWaitingForSms(true);
                    NotificationCenter.getInstance().addObserver(LoginActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                  }
                  while (true)
                  {
                    LoginActivity.LoginActivitySmsView.access$4602(LoginActivity.LoginActivitySmsView.this, true);
                    if (LoginActivity.LoginActivitySmsView.this.currentType == 3)
                      break label907;
                    if (!this.val$error.text.contains("PHONE_NUMBER_INVALID"))
                      break label656;
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidPhoneNumber", 2131165841));
                    i = 0;
                    break;
                    if (LoginActivity.LoginActivitySmsView.this.currentType != 3)
                      continue;
                    AndroidUtilities.setWaitingForCall(true);
                    NotificationCenter.getInstance().addObserver(LoginActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                  }
                  label656: if ((this.val$error.text.contains("PHONE_CODE_EMPTY")) || (this.val$error.text.contains("PHONE_CODE_INVALID")))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidCode", 2131165837));
                    i = 0;
                    continue;
                  }
                  if (this.val$error.text.contains("PHONE_CODE_EXPIRED"))
                  {
                    LoginActivity.LoginActivitySmsView.this.onBackPressed();
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("CodeExpired", 2131165559));
                    i = 0;
                    continue;
                  }
                  if (this.val$error.text.startsWith("FLOOD_WAIT"))
                  {
                    LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("FloodWait", 2131165715));
                    i = 0;
                    continue;
                  }
                  LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("ErrorOccurred", 2131165701) + "\n" + this.val$error.text);
                  label907: i = 0;
                }
              }
            });
          }
        }
        , 10);
        return;
        if (this.currentType != 3)
          continue;
        AndroidUtilities.setWaitingForCall(false);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
      }
    }

    public void onShow()
    {
      super.onShow();
      if ((this.codeField != null) && (this.currentType != 3))
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
      }
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      this.currentParams = paramBundle.getBundle("smsview_params_" + this.currentType);
      if (this.currentParams != null)
        setParams(this.currentParams, true);
      String str = paramBundle.getString("catchedPhone");
      if (str != null)
        this.catchedPhone = str;
      str = paramBundle.getString("smsview_code_" + this.currentType);
      if (str != null)
        this.codeField.setText(str);
      int i = paramBundle.getInt("time");
      if (i != 0)
        this.time = i;
      i = paramBundle.getInt("open");
      if (i != 0)
        this.openTime = i;
    }

    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("smsview_code_" + this.currentType, str);
      if (this.catchedPhone != null)
        paramBundle.putString("catchedPhone", this.catchedPhone);
      if (this.currentParams != null)
        paramBundle.putBundle("smsview_params_" + this.currentType, this.currentParams);
      if (this.time != 0)
        paramBundle.putInt("time", this.time);
      if (this.openTime != 0)
        paramBundle.putInt("open", this.openTime);
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      int j = 0;
      if (paramBundle == null);
      int i;
      label195: label216: String str;
      while (true)
      {
        return;
        this.isRestored = paramBoolean;
        this.codeField.setText("");
        this.waitingForEvent = true;
        if (this.currentType != 2)
          break;
        AndroidUtilities.setWaitingForSms(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveSmsCode);
        this.currentParams = paramBundle;
        this.phone = paramBundle.getString("phone");
        this.emailPhone = paramBundle.getString("ephone");
        this.requestPhone = paramBundle.getString("phoneFormated");
        this.phoneHash = paramBundle.getString("phoneHash");
        i = paramBundle.getInt("timeout");
        this.time = i;
        this.timeout = i;
        this.openTime = (int)(System.currentTimeMillis() / 1000L);
        this.nextType = paramBundle.getInt("nextType");
        this.pattern = paramBundle.getString("pattern");
        this.length = paramBundle.getInt("length");
        if (this.length == 0)
          break label362;
        paramBundle = new InputFilter.LengthFilter(this.length);
        this.codeField.setFilters(new InputFilter[] { paramBundle });
        if (this.progressView != null)
        {
          paramBundle = this.progressView;
          if (this.nextType == 0)
            break label376;
          i = 0;
          paramBundle.setVisibility(i);
        }
        if (this.phone == null)
          continue;
        str = b.a().e(this.phone);
        paramBundle = "";
        if (this.currentType != 1)
          break label382;
        paramBundle = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", 2131166430));
        label264: this.confirmTextView.setText(paramBundle);
        if (this.currentType == 3)
          break label481;
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
      while (true)
      {
        destroyTimer();
        destroyCodeTimer();
        this.lastCurrentTime = System.currentTimeMillis();
        if (this.currentType != 1)
          break label491;
        this.problemText.setVisibility(0);
        this.timeText.setVisibility(8);
        return;
        if (this.currentType != 3)
          break;
        AndroidUtilities.setWaitingForCall(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
        break;
        label362: this.codeField.setFilters(new InputFilter[0]);
        break label195;
        label376: i = 8;
        break label216;
        label382: if (this.currentType == 2)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", 2131166433, new Object[] { str }));
          break label264;
        }
        if (this.currentType == 3)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", 2131166431, new Object[] { str }));
          break label264;
        }
        if (this.currentType != 4)
          break label264;
        paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", 2131166432, new Object[] { str }));
        break label264;
        label481: AndroidUtilities.hideKeyboard(this.codeField);
      }
      label491: if ((this.currentType == 3) && ((this.nextType == 4) || (this.nextType == 2)))
      {
        this.problemText.setVisibility(8);
        this.timeText.setVisibility(0);
        if (this.nextType == 4)
        {
          this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
          if (!this.isRestored)
            break label661;
        }
        for (paramBundle = AndroidUtilities.obtainLoginPhoneCall(this.pattern); ; paramBundle = null)
        {
          if (paramBundle == null)
            break label666;
          this.ignoreOnTextChange = true;
          this.codeField.setText(paramBundle);
          this.ignoreOnTextChange = false;
          onNextPressed();
          return;
          if (this.nextType != 2)
            break;
          this.timeText.setText(LocaleController.formatString("SmsText", 2131166475, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
          break;
        }
        if (this.catchedPhone != null)
        {
          this.ignoreOnTextChange = true;
          this.codeField.setText(this.catchedPhone);
          this.ignoreOnTextChange = false;
          onNextPressed();
          return;
        }
        createTimer();
        return;
      }
      label661: label666: if ((this.currentType == 2) && ((this.nextType == 4) || (this.nextType == 3)))
      {
        this.timeText.setVisibility(0);
        this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(2), Integer.valueOf(0) }));
        paramBundle = this.problemText;
        if (this.time < 1000);
        for (i = j; ; i = 8)
        {
          paramBundle.setVisibility(i);
          createTimer();
          return;
        }
      }
      this.timeText.setVisibility(8);
      this.problemText.setVisibility(8);
      createCodeTimer();
    }
  }

  public class PhoneView extends SlideView
    implements AdapterView.OnItemSelectedListener
  {
    private EditText codeField;
    private HashMap<String, String> codesMap = new HashMap();
    private ArrayList<String> countriesArray = new ArrayList();
    private HashMap<String, String> countriesMap = new HashMap();
    private TextView countryButton;
    private int countryState = 0;
    private boolean ignoreOnPhoneChange = false;
    private boolean ignoreOnTextChange = false;
    private boolean ignoreSelection = false;
    private boolean nextPressed = false;
    private HintEditText phoneField;
    private HashMap<String, String> phoneFormatMap = new HashMap();
    private TextView textView;
    private TextView textView2;
    private View view;

    public PhoneView(Context arg2)
    {
      super();
      setOrientation(1);
      this.countryButton = new TextView((Context)localObject1);
      this.countryButton.setTextSize(1, 18.0F);
      this.countryButton.setPadding(AndroidUtilities.dp(12.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(12.0F), 0);
      this.countryButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.countryButton.setMaxLines(1);
      this.countryButton.setSingleLine(true);
      this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
      Object localObject2 = this.countryButton;
      int i;
      if (LocaleController.isRTL)
        i = 5;
      while (true)
      {
        ((TextView)localObject2).setGravity(i | 0x1);
        this.countryButton.setBackgroundResource(2130838069);
        addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0F, 0.0F, 0.0F, 14.0F));
        this.countryButton.setOnClickListener(new View.OnClickListener(LoginActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new CountrySelectActivity(true);
            paramView.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate()
            {
              public void didSelectCountry(String paramString1, String paramString2)
              {
                LoginActivity.PhoneView.this.selectCountry(paramString1);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.showKeyboard(LoginActivity.PhoneView.this.phoneField);
                  }
                }
                , 300L);
                LoginActivity.PhoneView.this.phoneField.requestFocus();
                LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
              }
            });
            LoginActivity.this.presentFragment(paramView);
          }
        });
        this.view = new View((Context)localObject1);
        this.view.setPadding(AndroidUtilities.dp(12.0F), 0, AndroidUtilities.dp(12.0F), 0);
        this.view.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
        addView(this.view, LayoutHelper.createLinear(-1, 1, 4.0F, -17.5F, 4.0F, 0.0F));
        localObject2 = new LinearLayout((Context)localObject1);
        ((LinearLayout)localObject2).setOrientation(0);
        addView((View)localObject2, LayoutHelper.createLinear(-1, -2, 0.0F, 20.0F, 0.0F, 0.0F));
        this.textView = new TextView((Context)localObject1);
        this.textView.setText("+");
        this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.textView.setTextSize(1, 18.0F);
        ((LinearLayout)localObject2).addView(this.textView, LayoutHelper.createLinear(-2, -2));
        this.codeField = new EditText((Context)localObject1);
        this.codeField.setInputType(3);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setPadding(AndroidUtilities.dp(10.0F), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        Object localObject3 = new InputFilter.LengthFilter(5);
        this.codeField.setFilters(new InputFilter[] { localObject3 });
        ((LinearLayout)localObject2).addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0F, 0.0F, 16.0F, 0.0F));
        this.codeField.addTextChangedListener(new TextWatcher(LoginActivity.this)
        {
          public void afterTextChanged(Editable paramEditable)
          {
            Object localObject3 = null;
            if (LoginActivity.PhoneView.this.ignoreOnTextChange)
              return;
            LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, true);
            paramEditable = b.b(LoginActivity.PhoneView.this.codeField.getText().toString());
            LoginActivity.PhoneView.this.codeField.setText(paramEditable);
            if (paramEditable.length() == 0)
            {
              LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165546));
              LoginActivity.PhoneView.this.phoneField.setHintText(null);
              LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 1);
              LoginActivity.PhoneView.access$502(LoginActivity.PhoneView.this, false);
              return;
            }
            int i;
            label115: Object localObject1;
            Object localObject2;
            label207: Object localObject4;
            if (paramEditable.length() > 4)
            {
              i = 4;
              if (i >= 1)
              {
                localObject1 = paramEditable.substring(0, i);
                if ((String)LoginActivity.PhoneView.this.codesMap.get(localObject1) != null)
                {
                  localObject2 = paramEditable.substring(i, paramEditable.length()) + LoginActivity.PhoneView.this.phoneField.getText().toString();
                  LoginActivity.PhoneView.this.codeField.setText((CharSequence)localObject1);
                  i = 1;
                  paramEditable = (Editable)localObject1;
                  localObject1 = localObject2;
                  localObject2 = paramEditable;
                  if (i == 0)
                  {
                    localObject1 = paramEditable.substring(1, paramEditable.length()) + LoginActivity.PhoneView.this.phoneField.getText().toString();
                    localObject4 = LoginActivity.PhoneView.this.codeField;
                    localObject2 = paramEditable.substring(0, 1);
                    ((EditText)localObject4).setText((CharSequence)localObject2);
                  }
                  paramEditable = (Editable)localObject1;
                  localObject1 = localObject2;
                }
              }
            }
            while (true)
            {
              localObject2 = (String)LoginActivity.PhoneView.this.codesMap.get(localObject1);
              if (localObject2 != null)
              {
                int j = LoginActivity.PhoneView.this.countriesArray.indexOf(localObject2);
                if (j != -1)
                {
                  LoginActivity.PhoneView.access$1102(LoginActivity.PhoneView.this, true);
                  LoginActivity.PhoneView.this.countryButton.setText((CharSequence)LoginActivity.PhoneView.this.countriesArray.get(j));
                  localObject4 = (String)LoginActivity.PhoneView.this.phoneFormatMap.get(localObject1);
                  localObject2 = LoginActivity.PhoneView.this.phoneField;
                  localObject1 = localObject3;
                  if (localObject4 != null)
                    localObject1 = ((String)localObject4).replace('X', '–');
                  ((HintEditText)localObject2).setHintText((String)localObject1);
                  LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 0);
                }
              }
              while (true)
              {
                if (i == 0)
                  LoginActivity.PhoneView.this.codeField.setSelection(LoginActivity.PhoneView.this.codeField.getText().length());
                if (paramEditable == null)
                  break;
                LoginActivity.PhoneView.this.phoneField.requestFocus();
                LoginActivity.PhoneView.this.phoneField.setText(paramEditable);
                LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
                break;
                i -= 1;
                break label115;
                LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
                LoginActivity.PhoneView.this.phoneField.setHintText(null);
                LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 2);
                continue;
                LoginActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
                LoginActivity.PhoneView.this.phoneField.setHintText(null);
                LoginActivity.PhoneView.access$802(LoginActivity.PhoneView.this, 2);
              }
              localObject1 = null;
              i = 0;
              break label207;
              i = 0;
              localObject1 = paramEditable;
              paramEditable = null;
            }
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.PhoneView.this.phoneField.requestFocus();
              LoginActivity.PhoneView.this.phoneField.setSelection(LoginActivity.PhoneView.this.phoneField.length());
              return true;
            }
            return false;
          }
        });
        this.phoneField = new HintEditText((Context)localObject1);
        this.phoneField.setInputType(3);
        this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        this.phoneField.setPadding(0, 0, 0, 0);
        AndroidUtilities.clearCursorDrawable(this.phoneField);
        this.phoneField.setTextSize(1, 18.0F);
        this.phoneField.setMaxLines(1);
        this.phoneField.setGravity(19);
        this.phoneField.setImeOptions(268435461);
        ((LinearLayout)localObject2).addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0F));
        this.phoneField.addTextChangedListener(new TextWatcher(LoginActivity.this)
        {
          private int actionPosition;
          private int characterAction = -1;

          public void afterTextChanged(Editable paramEditable)
          {
            if (LoginActivity.PhoneView.this.ignoreOnPhoneChange)
              return;
            int j = LoginActivity.PhoneView.this.phoneField.getSelectionStart();
            Object localObject = LoginActivity.PhoneView.this.phoneField.getText().toString();
            paramEditable = (Editable)localObject;
            int i = j;
            if (this.characterAction == 3)
            {
              paramEditable = ((String)localObject).substring(0, this.actionPosition) + ((String)localObject).substring(this.actionPosition + 1, ((String)localObject).length());
              i = j - 1;
            }
            localObject = new StringBuilder(paramEditable.length());
            j = 0;
            while (j < paramEditable.length())
            {
              String str = paramEditable.substring(j, j + 1);
              if ("0123456789".contains(str))
                ((StringBuilder)localObject).append(str);
              j += 1;
            }
            LoginActivity.PhoneView.access$1302(LoginActivity.PhoneView.this, true);
            paramEditable = LoginActivity.PhoneView.this.phoneField.getHintText();
            j = i;
            if (paramEditable != null)
            {
              int k = 0;
              while (true)
              {
                j = i;
                if (k >= ((StringBuilder)localObject).length())
                  break;
                if (k < paramEditable.length())
                {
                  int m = k;
                  j = i;
                  if (paramEditable.charAt(k) == ' ')
                  {
                    ((StringBuilder)localObject).insert(k, ' ');
                    k += 1;
                    m = k;
                    j = i;
                    if (i == k)
                    {
                      m = k;
                      j = i;
                      if (this.characterAction != 2)
                      {
                        m = k;
                        j = i;
                        if (this.characterAction != 3)
                        {
                          j = i + 1;
                          m = k;
                        }
                      }
                    }
                  }
                  k = m + 1;
                  i = j;
                  continue;
                }
                ((StringBuilder)localObject).insert(k, ' ');
                j = i;
                if (i != k + 1)
                  break;
                j = i;
                if (this.characterAction == 2)
                  break;
                j = i;
                if (this.characterAction == 3)
                  break;
                j = i + 1;
              }
            }
            LoginActivity.PhoneView.this.phoneField.setText((CharSequence)localObject);
            if (j >= 0)
            {
              paramEditable = LoginActivity.PhoneView.this.phoneField;
              if (j > LoginActivity.PhoneView.this.phoneField.length())
                break label404;
            }
            while (true)
            {
              paramEditable.setSelection(j);
              LoginActivity.PhoneView.this.phoneField.onTextChange();
              LoginActivity.PhoneView.access$1302(LoginActivity.PhoneView.this, false);
              return;
              label404: j = LoginActivity.PhoneView.this.phoneField.length();
            }
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
            if ((paramInt2 == 0) && (paramInt3 == 1))
            {
              this.characterAction = 1;
              return;
            }
            if ((paramInt2 == 1) && (paramInt3 == 0))
            {
              if ((paramCharSequence.charAt(paramInt1) == ' ') && (paramInt1 > 0))
              {
                this.characterAction = 3;
                this.actionPosition = (paramInt1 - 1);
                return;
              }
              this.characterAction = 2;
              return;
            }
            this.characterAction = -1;
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }
        });
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener(LoginActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              LoginActivity.PhoneView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        this.textView2 = new TextView((Context)localObject1);
        this.textView2.setText(LocaleController.getString("StartText", 2131166484));
        this.textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
        this.textView2.setTextSize(1, 14.0F);
        localObject1 = this.textView2;
        if (LocaleController.isRTL)
        {
          i = 5;
          label793: ((TextView)localObject1).setGravity(i);
          this.textView2.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
          localObject1 = this.textView2;
          if (!LocaleController.isRTL)
            break label1167;
          i = 5;
          label824: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 28, 0, 10));
          localObject1 = new HashMap();
          try
          {
            localObject2 = new BufferedReader(new InputStreamReader(getResources().getAssets().open("countries.txt")));
            while (true)
            {
              localObject3 = ((BufferedReader)localObject2).readLine();
              if (localObject3 == null)
                break;
              localObject3 = ((String)localObject3).split(";");
              this.countriesArray.add(0, localObject3[2]);
              this.countriesMap.put(localObject3[2], localObject3[0]);
              this.codesMap.put(localObject3[0], localObject3[2]);
              if (localObject3.length > 3)
                this.phoneFormatMap.put(localObject3[0], localObject3[3]);
              ((HashMap)localObject1).put(localObject3[1], localObject3[2]);
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
          Collections.sort(this.countriesArray, new Comparator(LoginActivity.this)
          {
            public int compare(String paramString1, String paramString2)
            {
              return paramString1.compareTo(paramString2);
            }
          });
        }
        try
        {
          this$1 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
          if (LoginActivity.this != null)
          {
            this$1 = LoginActivity.this.getSimCountryIso().toUpperCase();
            if (LoginActivity.this != null)
            {
              this$1 = (String)((HashMap)localObject1).get(LoginActivity.this);
              if ((LoginActivity.this != null) && (this.countriesArray.indexOf(LoginActivity.this) != -1))
              {
                this.codeField.setText((CharSequence)this.countriesMap.get(LoginActivity.this));
                this.countryState = 0;
              }
            }
            if (this.codeField.length() == 0)
            {
              this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165546));
              this.phoneField.setHintText(null);
              this.countryState = 1;
            }
            if (this.codeField.length() == 0)
              break label1190;
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
            return;
            i = 3;
            continue;
            i = 3;
            break label793;
            label1167: i = 3;
            break label824;
            localException.close();
          }
        }
        catch (Exception this$1)
        {
          while (true)
          {
            FileLog.e(LoginActivity.this);
            this$1 = null;
          }
          label1190: this.codeField.requestFocus();
        }
      }
    }

    public void fillNumber()
    {
      int n = 4;
      while (true)
      {
        try
        {
          Object localObject = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
          if ((((TelephonyManager)localObject).getSimState() == 1) || (((TelephonyManager)localObject).getPhoneType() == 0))
            break label566;
          if (Build.VERSION.SDK_INT >= 23)
          {
            if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0)
              break label567;
            i = 1;
            if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0)
              break label572;
            j = 1;
            m = j;
            k = i;
            if (!LoginActivity.this.checkShowPermissions)
              break label385;
            m = j;
            k = i;
            if (i != 0)
              break label385;
            m = j;
            k = i;
            if (j != 0)
              break label385;
            LoginActivity.this.permissionsShowItems.clear();
            if (i != 0)
              continue;
            LoginActivity.this.permissionsShowItems.add("android.permission.READ_PHONE_STATE");
            if (j != 0)
              continue;
            LoginActivity.this.permissionsShowItems.add("android.permission.RECEIVE_SMS");
            if (LoginActivity.this.permissionsShowItems.isEmpty())
              break label566;
            localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if ((!((SharedPreferences)localObject).getBoolean("firstloginshow", true)) && (!LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) && (!LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")))
              continue;
            ((SharedPreferences)localObject).edit().putBoolean("firstloginshow", false).commit();
            localObject = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
            ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
            ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("AllowFillNumber", 2131165297));
            LoginActivity.access$2302(LoginActivity.this, LoginActivity.this.showDialog(((AlertDialog.Builder)localObject).create()));
            return;
            LoginActivity.this.getParentActivity().requestPermissions((String[])LoginActivity.this.permissionsShowItems.toArray(new String[LoginActivity.this.permissionsShowItems.size()]), 7);
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        int m = 1;
        int k = 1;
        label385: if ((k != 0) || (m != 0))
        {
          String str2 = b.b(localException.getLine1Number());
          if (!TextUtils.isEmpty(str2))
          {
            String str1;
            if (str2.length() > 4)
            {
              i = n;
              if (i >= 1)
              {
                String str3 = str2.substring(0, i);
                if ((String)this.codesMap.get(str3) != null)
                {
                  str1 = str2.substring(i, str2.length());
                  this.codeField.setText(str3);
                  i = 1;
                  label477: if (i == 0)
                  {
                    str1 = str2.substring(1, str2.length());
                    this.codeField.setText(str2.substring(0, 1));
                  }
                }
              }
            }
            while (true)
            {
              if (str1 == null)
                break label566;
              this.phoneField.requestFocus();
              this.phoneField.setText(str1);
              this.phoneField.setSelection(this.phoneField.length());
              return;
              i -= 1;
              break;
              str1 = null;
              i = 0;
              break label477;
              str1 = null;
            }
          }
        }
        label566: return;
        label567: int i = 0;
        continue;
        label572: int j = 0;
      }
    }

    public String getHeaderName()
    {
      return LocaleController.getString("YourPhone", 2131166659);
    }

    public void onItemSelected(AdapterView<?> paramAdapterView, View paramView, int paramInt, long paramLong)
    {
      if (this.ignoreSelection)
      {
        this.ignoreSelection = false;
        return;
      }
      this.ignoreOnTextChange = true;
      paramAdapterView = (String)this.countriesArray.get(paramInt);
      this.codeField.setText((CharSequence)this.countriesMap.get(paramAdapterView));
      this.ignoreOnTextChange = false;
    }

    public void onNextPressed()
    {
      if ((LoginActivity.this.getParentActivity() == null) || (this.nextPressed));
      Object localObject2;
      label83: label102: label121: int k;
      label139: label163: label423: label428: label434: label440: label445: label450: label708: label710: TLRPC.TL_auth_sendCode localTL_auth_sendCode;
      String str;
      boolean bool;
      while (true)
      {
        return;
        localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        int m;
        int j;
        int n;
        int i1;
        int i;
        Object localObject1;
        if ((((TelephonyManager)localObject2).getSimState() != 1) && (((TelephonyManager)localObject2).getPhoneType() != 0))
        {
          m = 1;
          if ((Build.VERSION.SDK_INT < 23) || (m == 0))
            break label1212;
          if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0)
            break label423;
          j = 1;
          if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0)
            break label428;
          n = 1;
          if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.CALL_PHONE") != 0)
            break label434;
          i1 = 1;
          if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0)
            break label440;
          k = 1;
          i = k;
          if (k != 0)
          {
            if (LoginActivity.this.getParentActivity().checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0)
              break label445;
            i = 1;
          }
          k = j;
          if (LoginActivity.this.checkPermissions)
          {
            LoginActivity.this.permissionsItems.clear();
            if (i == 0)
            {
              LoginActivity.this.permissionsItems.add("android.permission.ACCESS_COARSE_LOCATION");
              LoginActivity.this.permissionsItems.add("android.permission.ACCESS_FINE_LOCATION");
            }
            if (j == 0)
              LoginActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
            if (n == 0)
              LoginActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
            if (i1 == 0)
            {
              LoginActivity.this.permissionsItems.add("android.permission.CALL_PHONE");
              LoginActivity.this.permissionsItems.add("android.permission.WRITE_CALL_LOG");
              LoginActivity.this.permissionsItems.add("android.permission.READ_CALL_LOG");
            }
            k = j;
            if (!LoginActivity.this.permissionsItems.isEmpty())
            {
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              if ((i1 != 0) || (j == 0))
                break label450;
              LoginActivity.this.getParentActivity().requestPermissions((String[])LoginActivity.this.permissionsItems.toArray(new String[LoginActivity.this.permissionsItems.size()]), 6);
              i = 1;
            }
          }
        }
        while (true)
        {
          if (i != 0)
            break label708;
          k = j;
          if (this.countryState != 1)
            break label710;
          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("ChooseCountry", 2131165546));
          return;
          m = 0;
          break;
          j = 0;
          break label83;
          n = 0;
          break label102;
          i1 = 0;
          break label121;
          k = 0;
          break label139;
          i = 0;
          break label163;
          if ((((SharedPreferences)localObject1).getBoolean("firstlogin", true)) || (LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) || (LoginActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")))
          {
            ((SharedPreferences)localObject1).edit().putBoolean("firstlogin", false).commit();
            localObject1 = new AlertDialog.Builder(LoginActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165319));
            ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
            if (LoginActivity.this.permissionsItems.size() >= 2)
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCallAndSms", 2131165299));
            while (true)
            {
              LoginActivity.access$1602(LoginActivity.this, LoginActivity.this.showDialog(((AlertDialog.Builder)localObject1).create()));
              i = 1;
              break;
              if (n == 0)
              {
                ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadSms", 2131165300));
                continue;
              }
              ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCall", 2131165298));
            }
          }
          try
          {
            LoginActivity.this.getParentActivity().requestPermissions((String[])LoginActivity.this.permissionsItems.toArray(new String[LoginActivity.this.permissionsItems.size()]), 6);
            i = 1;
          }
          catch (Exception localException1)
          {
            i = 0;
          }
        }
        continue;
        if ((this.countryState == 2) && (!BuildVars.DEBUG_VERSION) && (!this.codeField.getText().toString().equals("999")))
        {
          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("WrongCountry", 2131166631));
          return;
        }
        if (this.codeField.length() == 0)
        {
          LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidPhoneNumber", 2131165841));
          return;
        }
        ConnectionsManager.getInstance().cleanup();
        localTL_auth_sendCode = new TLRPC.TL_auth_sendCode();
        str = b.b("" + this.codeField.getText() + this.phoneField.getText());
        ConnectionsManager.getInstance().applyCountryPortNumber(str);
        localTL_auth_sendCode.api_hash = BuildVars.APP_HASH;
        localTL_auth_sendCode.api_id = BuildVars.APP_ID;
        localTL_auth_sendCode.phone_number = str;
        if ((m != 0) && (k != 0))
        {
          bool = true;
          label904: localTL_auth_sendCode.allow_flashcall = bool;
          if (!localTL_auth_sendCode.allow_flashcall)
            break;
        }
      }
      while (true)
      {
        try
        {
          localObject2 = ((TelephonyManager)localObject2).getLine1Number();
          if (TextUtils.isEmpty((CharSequence)localObject2))
            continue;
          if (str.contains((CharSequence)localObject2))
            break label1217;
          if (!((String)localObject2).contains(str))
            continue;
          break label1217;
          localTL_auth_sendCode.current_number = bool;
          if (localTL_auth_sendCode.current_number)
            continue;
          localTL_auth_sendCode.allow_flashcall = false;
          localObject2 = new Bundle();
          ((Bundle)localObject2).putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
        }
        catch (Exception localException2)
        {
          try
          {
            ((Bundle)localObject2).putString("ephone", "+" + b.b(this.codeField.getText().toString()) + " " + b.b(this.phoneField.getText().toString()));
            ((Bundle)localObject2).putString("phoneFormated", str);
            this.nextPressed = true;
            LoginActivity.this.needShowProgress();
            ConnectionsManager.getInstance().sendRequest(localTL_auth_sendCode, new RequestDelegate((Bundle)localObject2, localTL_auth_sendCode)
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                {
                  public void run()
                  {
                    LoginActivity.PhoneView.access$1802(LoginActivity.PhoneView.this, false);
                    if (this.val$error == null)
                      LoginActivity.this.fillNextCodeParams(LoginActivity.PhoneView.7.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
                    while (true)
                    {
                      LoginActivity.this.needHideProgress();
                      return;
                      if (this.val$error.text == null)
                        continue;
                      if (this.val$error.text.contains("PHONE_NUMBER_INVALID"))
                      {
                        LoginActivity.this.needShowInvalidAlert(LoginActivity.PhoneView.7.this.val$req.phone_number, false);
                        continue;
                      }
                      if (this.val$error.text.contains("PHONE_NUMBER_FLOOD"))
                      {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("PhoneNumberFlood", 2131166268));
                        continue;
                      }
                      if (this.val$error.text.contains("PHONE_NUMBER_BANNED"))
                      {
                        LoginActivity.this.needShowInvalidAlert(LoginActivity.PhoneView.7.this.val$req.phone_number, true);
                        continue;
                      }
                      if ((this.val$error.text.contains("PHONE_CODE_EMPTY")) || (this.val$error.text.contains("PHONE_CODE_INVALID")))
                      {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("InvalidCode", 2131165837));
                        continue;
                      }
                      if (this.val$error.text.contains("PHONE_CODE_EXPIRED"))
                      {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("CodeExpired", 2131165559));
                        continue;
                      }
                      if (this.val$error.text.startsWith("FLOOD_WAIT"))
                      {
                        LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), LocaleController.getString("FloodWait", 2131165715));
                        continue;
                      }
                      if (this.val$error.code == -1000)
                        continue;
                      LoginActivity.this.needShowAlert(LocaleController.getString("AppName", 2131165319), this.val$error.text);
                    }
                  }
                });
              }
            }
            , 27);
            return;
            bool = false;
            break label904;
            bool = false;
            continue;
            localTL_auth_sendCode.current_number = false;
            continue;
            localException2 = localException2;
            localTL_auth_sendCode.allow_flashcall = false;
            FileLog.e(localException2);
          }
          catch (Exception localException3)
          {
            FileLog.e(localException3);
            localException2.putString("ephone", "+" + str);
            continue;
          }
        }
        label1212: k = 1;
        break;
        label1217: bool = true;
      }
    }

    public void onNothingSelected(AdapterView<?> paramAdapterView)
    {
    }

    public void onShow()
    {
      super.onShow();
      if (this.phoneField != null)
      {
        if (this.codeField.length() == 0)
          break label55;
        AndroidUtilities.showKeyboard(this.phoneField);
        this.phoneField.requestFocus();
        this.phoneField.setSelection(this.phoneField.length());
      }
      while (true)
      {
        fillNumber();
        return;
        label55: AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
    }

    public void restoreStateParams(Bundle paramBundle)
    {
      String str = paramBundle.getString("phoneview_code");
      if (str != null)
        this.codeField.setText(str);
      paramBundle = paramBundle.getString("phoneview_phone");
      if (paramBundle != null)
        this.phoneField.setText(paramBundle);
    }

    public void saveStateParams(Bundle paramBundle)
    {
      String str = this.codeField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("phoneview_code", str);
      str = this.phoneField.getText().toString();
      if (str.length() != 0)
        paramBundle.putString("phoneview_phone", str);
    }

    public void selectCountry(String paramString)
    {
      Object localObject;
      if (this.countriesArray.indexOf(paramString) != -1)
      {
        this.ignoreOnTextChange = true;
        localObject = (String)this.countriesMap.get(paramString);
        this.codeField.setText((CharSequence)localObject);
        this.countryButton.setText(paramString);
        paramString = (String)this.phoneFormatMap.get(localObject);
        localObject = this.phoneField;
        if (paramString == null)
          break label92;
      }
      label92: for (paramString = paramString.replace('X', '–'); ; paramString = null)
      {
        ((HintEditText)localObject).setHintText(paramString);
        this.countryState = 0;
        this.ignoreOnTextChange = false;
        return;
      }
    }
  }

  private class ProgressView extends View
  {
    private Paint paint = new Paint();
    private Paint paint2 = new Paint();
    private float progress;

    public ProgressView(Context arg2)
    {
      super();
      this.paint.setColor(Theme.getColor("login_progressInner"));
      this.paint2.setColor(Theme.getColor("login_progressOuter"));
    }

    protected void onDraw(Canvas paramCanvas)
    {
      int i = (int)(getMeasuredWidth() * this.progress);
      paramCanvas.drawRect(0.0F, 0.0F, i, getMeasuredHeight(), this.paint2);
      paramCanvas.drawRect(i, 0.0F, getMeasuredWidth(), getMeasuredHeight(), this.paint);
    }

    public void setProgress(float paramFloat)
    {
      this.progress = paramFloat;
      invalidate();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.LoginActivity
 * JD-Core Version:    0.6.0
 */