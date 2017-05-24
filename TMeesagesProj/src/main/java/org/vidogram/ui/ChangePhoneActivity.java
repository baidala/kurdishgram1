package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
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
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_changePhone;
import org.vidogram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.vidogram.tgnet.TLRPC.TL_auth_cancelCode;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeCall;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeFlashCall;
import org.vidogram.tgnet.TLRPC.TL_auth_codeTypeSms;
import org.vidogram.tgnet.TLRPC.TL_auth_resendCode;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCode;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeApp;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeCall;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeFlashCall;
import org.vidogram.tgnet.TLRPC.TL_auth_sentCodeTypeSms;
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
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.HintEditText;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.SlideView;

public class ChangePhoneActivity extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = true;
  private int currentViewNum = 0;
  private View doneButton;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private AlertDialog progressDialog;
  private SlideView[] views = new SlideView[5];

  private void fillNextCodeParams(Bundle paramBundle, TLRPC.TL_auth_sentCode paramTL_auth_sentCode)
  {
    paramBundle.putString("phoneHash", paramTL_auth_sentCode.phone_code_hash);
    if ((paramTL_auth_sentCode.next_type instanceof TLRPC.TL_auth_codeTypeCall))
    {
      paramBundle.putInt("nextType", 4);
      if (!(paramTL_auth_sentCode.type instanceof TLRPC.TL_auth_sentCodeTypeApp))
        break label106;
      paramBundle.putInt("type", 1);
      paramBundle.putInt("length", paramTL_auth_sentCode.type.length);
      setPage(1, true, paramBundle, false);
    }
    label106: 
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

  public View createView(Context paramContext)
  {
    this.actionBar.setTitle(LocaleController.getString("AppName", 2131165319));
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == 1)
          ChangePhoneActivity.this.views[ChangePhoneActivity.this.currentViewNum].onNextPressed();
        do
          return;
        while (paramInt != -1);
        ChangePhoneActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    ScrollView localScrollView = (ScrollView)this.fragmentView;
    localScrollView.setFillViewport(true);
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    localScrollView.addView(localFrameLayout, LayoutHelper.createScroll(-1, -2, 51));
    this.views[0] = new PhoneView(paramContext);
    this.views[1] = new LoginActivitySmsView(paramContext, 1);
    this.views[2] = new LoginActivitySmsView(paramContext, 2);
    this.views[3] = new LoginActivitySmsView(paramContext, 3);
    this.views[4] = new LoginActivitySmsView(paramContext, 4);
    int i = 0;
    if (i < this.views.length)
    {
      paramContext = this.views[i];
      int j;
      label220: float f1;
      label243: float f2;
      label253: float f3;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0)
          break label300;
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet())
          break label307;
        f2 = 26.0F;
        if (!AndroidUtilities.isTablet())
          break label314;
        f3 = 26.0F;
      }
      while (true)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label220;
        label300: f1 = -1.0F;
        break label243;
        label307: f2 = 18.0F;
        break label253;
        label314: f3 = 18.0F;
      }
    }
    this.actionBar.setTitle(this.views[0].getHeaderName());
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    PhoneView localPhoneView = (PhoneView)this.views[0];
    LoginActivitySmsView localLoginActivitySmsView1 = (LoginActivitySmsView)this.views[1];
    LoginActivitySmsView localLoginActivitySmsView2 = (LoginActivitySmsView)this.views[2];
    LoginActivitySmsView localLoginActivitySmsView3 = (LoginActivitySmsView)this.views[3];
    LoginActivitySmsView localLoginActivitySmsView4 = (LoginActivitySmsView)this.views[4];
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(localPhoneView.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.view, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"), new ThemeDescription(localPhoneView.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localPhoneView.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localPhoneView.textView2, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.wrongNumber, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter") };
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

  public void needShowProgress()
  {
    if ((getParentActivity() == null) || (getParentActivity().isFinishing()) || (this.progressDialog != null))
      return;
    this.progressDialog = new AlertDialog(getParentActivity(), 1);
    this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165920));
    this.progressDialog.setCanceledOnTouchOutside(false);
    this.progressDialog.setCancelable(false);
    this.progressDialog.show();
  }

  public boolean onBackPressed()
  {
    int i = 0;
    if (this.currentViewNum == 0)
    {
      while (i < this.views.length)
      {
        if (this.views[i] != null)
          this.views[i].onDestroyActivity();
        i += 1;
      }
      return true;
    }
    this.views[this.currentViewNum].onBackPressed();
    setPage(0, true, null, true);
    return false;
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty()))
      getParentActivity().requestPermissions((String[])this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
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
      AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 6)
    {
      this.checkPermissions = false;
      if (this.currentViewNum == 0)
        this.views[this.currentViewNum].onNextPressed();
    }
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
      this.views[this.currentViewNum].onShow();
  }

  public void setPage(int paramInt, boolean paramBoolean1, Bundle paramBundle, boolean paramBoolean2)
  {
    SlideView localSlideView1;
    SlideView localSlideView2;
    float f;
    if (paramInt == 3)
    {
      this.doneButton.setVisibility(8);
      localSlideView1 = this.views[this.currentViewNum];
      localSlideView2 = this.views[paramInt];
      this.currentViewNum = paramInt;
      localSlideView2.setParams(paramBundle, false);
      this.actionBar.setTitle(localSlideView2.getHeaderName());
      localSlideView2.onShow();
      if (!paramBoolean2)
        break label211;
      f = -AndroidUtilities.displaySize.x;
      label77: localSlideView2.setX(f);
      paramBundle = new AnimatorSet();
      paramBundle.setInterpolator(new AccelerateDecelerateInterpolator());
      paramBundle.setDuration(300L);
      if (!paramBoolean2)
        break label223;
      f = AndroidUtilities.displaySize.x;
    }
    while (true)
    {
      paramBundle.playTogether(new Animator[] { ObjectAnimator.ofFloat(localSlideView1, "translationX", new float[] { f }), ObjectAnimator.ofFloat(localSlideView2, "translationX", new float[] { 0.0F }) });
      paramBundle.addListener(new AnimatorListenerAdapter(localSlideView2, localSlideView1)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          this.val$outView.setVisibility(8);
          this.val$outView.setX(0.0F);
        }

        public void onAnimationStart(Animator paramAnimator)
        {
          this.val$newView.setVisibility(0);
        }
      });
      paramBundle.start();
      return;
      if (paramInt == 0)
        this.checkPermissions = true;
      this.doneButton.setVisibility(0);
      break;
      label211: f = AndroidUtilities.displaySize.x;
      break label77;
      label223: f = -AndroidUtilities.displaySize.x;
    }
  }

  public class LoginActivitySmsView extends SlideView
    implements NotificationCenter.NotificationCenterDelegate
  {
    private EditText codeField;
    private volatile int codeTime = 15000;
    private Timer codeTimer;
    private TextView confirmTextView;
    private Bundle currentParams;
    private int currentType;
    private String emailPhone;
    private boolean ignoreOnTextChange;
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
    private ChangePhoneActivity.ProgressView progressView;
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
          break label1006;
        localObject1 = new FrameLayout(paramInt);
        localObject2 = new ImageView(paramInt);
        ((ImageView)localObject2).setImageResource(2130837996);
        if (!LocaleController.isRTL)
          break label940;
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label935;
        i = 5;
        label198: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, i, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL)
          break label1001;
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
        this.codeField.addTextChangedListener(new TextWatcher(ChangePhoneActivity.this)
        {
          public void afterTextChanged(Editable paramEditable)
          {
            if (ChangePhoneActivity.LoginActivitySmsView.this.ignoreOnTextChange);
            do
              return;
            while ((ChangePhoneActivity.LoginActivitySmsView.this.length == 0) || (ChangePhoneActivity.LoginActivitySmsView.this.codeField.length() != ChangePhoneActivity.LoginActivitySmsView.this.length));
            ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(ChangePhoneActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              ChangePhoneActivity.LoginActivitySmsView.this.onNextPressed();
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
          break label1042;
        i = 5;
        label500: ((TextView)localObject1).setGravity(i);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL)
          break label1047;
        i = 5;
        label520: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 30, 0, 0));
        if (this.currentType == 3)
        {
          this.progressView = new ChangePhoneActivity.ProgressView(ChangePhoneActivity.this, paramInt);
          addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0F, 12.0F, 0.0F, 0.0F));
        }
        this.problemText = new TextView(paramInt);
        this.problemText.setText(LocaleController.getString("DidNotGetTheCode", 2131165655));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL)
          break label1052;
        i = 5;
        label621: ((TextView)localObject1).setGravity(i);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        localObject1 = this.problemText;
        if (!LocaleController.isRTL)
          break label1057;
        i = 5;
        label696: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener(ChangePhoneActivity.this)
        {
          public void onClick(View paramView)
          {
            if (ChangePhoneActivity.LoginActivitySmsView.this.nextPressed)
              return;
            if ((ChangePhoneActivity.LoginActivitySmsView.this.nextType != 0) && (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 4))
            {
              ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramView = String.format(Locale.US, "%s (%d)", new Object[] { paramView.versionName, Integer.valueOf(paramView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android registration/login issue " + paramView + " " + ChangePhoneActivity.LoginActivitySmsView.this.emailPhone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + ChangePhoneActivity.LoginActivitySmsView.this.requestPhone + "\nApp version: " + paramView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + ChangePhoneActivity.LoginActivitySmsView.this.lastError);
              ChangePhoneActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramView)
            {
              AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("NoMailInstalled", 2131166031));
            }
          }
        });
        localObject1 = new LinearLayout(paramInt);
        if (!LocaleController.isRTL)
          break label1062;
        i = 5;
        label749: ((LinearLayout)localObject1).setGravity(i | 0x10);
        if (!LocaleController.isRTL)
          break label1067;
        i = 5;
        label766: addView((View)localObject1, LayoutHelper.createLinear(-1, -1, i));
        this.wrongNumber = new TextView(paramInt);
        paramInt = this.wrongNumber;
        if (!LocaleController.isRTL)
          break label1072;
        i = 5;
        label803: paramInt.setGravity(i | 0x1);
        this.wrongNumber.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.wrongNumber.setTextSize(1, 14.0F);
        this.wrongNumber.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.wrongNumber.setPadding(0, AndroidUtilities.dp(24.0F), 0, 0);
        paramInt = this.wrongNumber;
        if (!LocaleController.isRTL)
          break label1077;
      }
      label935: label940: label1072: label1077: for (int i = 5; ; i = 3)
      {
        ((LinearLayout)localObject1).addView(paramInt, LayoutHelper.createLinear(-2, -2, i | 0x50, 0, 0, 0, 10));
        this.wrongNumber.setText(LocaleController.getString("WrongNumber", 2131166632));
        this.wrongNumber.setOnClickListener(new View.OnClickListener(ChangePhoneActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new TLRPC.TL_auth_cancelCode();
            paramView.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
            paramView.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
            ConnectionsManager.getInstance().sendRequest(paramView, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
              }
            }
            , 2);
            ChangePhoneActivity.LoginActivitySmsView.this.onBackPressed();
            ChangePhoneActivity.this.setPage(0, true, null, true);
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
        label1001: i = 3;
        break label225;
        label1006: localObject1 = this.confirmTextView;
        if (LocaleController.isRTL);
        for (i = 5; ; i = 3)
        {
          addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i));
          break;
        }
        i = 3;
        break label500;
        i = 3;
        break label520;
        i = 3;
        break label621;
        i = 3;
        break label696;
        i = 3;
        break label749;
        i = 3;
        break label766;
        i = 3;
        break label803;
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
          double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCodeTime;
          ChangePhoneActivity.LoginActivitySmsView.access$2802(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          ChangePhoneActivity.LoginActivitySmsView.access$2702(ChangePhoneActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (ChangePhoneActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                ChangePhoneActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
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
          if (ChangePhoneActivity.LoginActivitySmsView.this.timeTimer == null)
            return;
          double d1 = System.currentTimeMillis();
          double d2 = ChangePhoneActivity.LoginActivitySmsView.this.lastCurrentTime;
          ChangePhoneActivity.LoginActivitySmsView.access$3302(ChangePhoneActivity.LoginActivitySmsView.this, (int)(ChangePhoneActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          ChangePhoneActivity.LoginActivitySmsView.access$3202(ChangePhoneActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (ChangePhoneActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = ChangePhoneActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = ChangePhoneActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3))
                  ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
              }
              do
              {
                do
                {
                  while (true)
                  {
                    if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null)
                      ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - ChangePhoneActivity.LoginActivitySmsView.this.time / ChangePhoneActivity.LoginActivitySmsView.this.timeout);
                    return;
                    if (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 2)
                      continue;
                    ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166475, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                  }
                  if (ChangePhoneActivity.LoginActivitySmsView.this.progressView != null)
                    ChangePhoneActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType != 3)
                    continue;
                  AndroidUtilities.setWaitingForCall(false);
                  NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                  ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, false);
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
                  return;
                }
                while (ChangePhoneActivity.LoginActivitySmsView.this.currentType != 2);
                if (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 4)
                  continue;
                ChangePhoneActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165423));
                ChangePhoneActivity.LoginActivitySmsView.this.createCodeTimer();
                TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                localTL_auth_resendCode.phone_number = ChangePhoneActivity.LoginActivitySmsView.this.requestPhone;
                localTL_auth_resendCode.phone_code_hash = ChangePhoneActivity.LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    if ((paramTL_error != null) && (paramTL_error.text != null))
                      AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                      {
                        public void run()
                        {
                          ChangePhoneActivity.LoginActivitySmsView.access$2502(ChangePhoneActivity.LoginActivitySmsView.this, this.val$error.text);
                        }
                      });
                  }
                }
                , 2);
                return;
              }
              while (ChangePhoneActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, false);
              ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
              ChangePhoneActivity.LoginActivitySmsView.this.resendCode();
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
      ChangePhoneActivity.this.needShowProgress();
      TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.requestPhone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate(localBundle, localTL_auth_resendCode)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              ChangePhoneActivity.LoginActivitySmsView.access$2002(ChangePhoneActivity.LoginActivitySmsView.this, false);
              if (this.val$error == null)
                ChangePhoneActivity.this.fillNextCodeParams(ChangePhoneActivity.LoginActivitySmsView.5.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
              while (true)
              {
                ChangePhoneActivity.this.needHideProgress();
                return;
                AlertsCreator.processError(this.val$error, ChangePhoneActivity.this, ChangePhoneActivity.LoginActivitySmsView.5.this.val$req, new Object[0]);
                if (!this.val$error.text.contains("PHONE_CODE_EXPIRED"))
                  continue;
                ChangePhoneActivity.LoginActivitySmsView.this.onBackPressed();
                ChangePhoneActivity.this.setPage(0, true, null, true);
              }
            }
          });
        }
      }
      , 2);
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
        TLRPC.TL_account_changePhone localTL_account_changePhone = new TLRPC.TL_account_changePhone();
        localTL_account_changePhone.phone_number = this.requestPhone;
        localTL_account_changePhone.phone_code = this.codeField.getText().toString();
        localTL_account_changePhone.phone_code_hash = this.phoneHash;
        destroyTimer();
        ChangePhoneActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_account_changePhone, new RequestDelegate(localTL_account_changePhone)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
            {
              public void run()
              {
                ChangePhoneActivity.this.needHideProgress();
                ChangePhoneActivity.LoginActivitySmsView.access$2002(ChangePhoneActivity.LoginActivitySmsView.this, false);
                if (this.val$error == null)
                {
                  TLRPC.User localUser = (TLRPC.User)this.val$response;
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyTimer();
                  ChangePhoneActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  UserConfig.setCurrentUser(localUser);
                  UserConfig.saveConfig(true);
                  ArrayList localArrayList = new ArrayList();
                  localArrayList.add(localUser);
                  MessagesStorage.getInstance().putUsersAndChats(localArrayList, null, true, true);
                  MessagesController.getInstance().putUser(localUser, false);
                  ChangePhoneActivity.this.finishFragment();
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  return;
                }
                ChangePhoneActivity.LoginActivitySmsView.access$2502(ChangePhoneActivity.LoginActivitySmsView.this, this.val$error.text);
                if (((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 2))) || ((ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2) && ((ChangePhoneActivity.LoginActivitySmsView.this.nextType == 4) || (ChangePhoneActivity.LoginActivitySmsView.this.nextType == 3))))
                  ChangePhoneActivity.LoginActivitySmsView.this.createTimer();
                if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                while (true)
                {
                  ChangePhoneActivity.LoginActivitySmsView.access$3902(ChangePhoneActivity.LoginActivitySmsView.this, true);
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType == 3)
                    break;
                  AlertsCreator.processError(this.val$error, ChangePhoneActivity.this, ChangePhoneActivity.LoginActivitySmsView.8.this.val$req, new Object[0]);
                  return;
                  if (ChangePhoneActivity.LoginActivitySmsView.this.currentType != 3)
                    continue;
                  AndroidUtilities.setWaitingForCall(true);
                  NotificationCenter.getInstance().addObserver(ChangePhoneActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
                }
              }
            });
          }
        }
        , 2);
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
      if (this.codeField != null)
      {
        this.codeField.requestFocus();
        this.codeField.setSelection(this.codeField.length());
      }
    }

    public void setParams(Bundle paramBundle, boolean paramBoolean)
    {
      int j = 0;
      if (paramBundle == null);
      int i;
      label190: label211: String str;
      while (true)
      {
        return;
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
          break label357;
        paramBundle = new InputFilter.LengthFilter(this.length);
        this.codeField.setFilters(new InputFilter[] { paramBundle });
        if (this.progressView != null)
        {
          paramBundle = this.progressView;
          if (this.nextType == 0)
            break label371;
          i = 0;
          paramBundle.setVisibility(i);
        }
        if (this.phone == null)
          continue;
        str = b.a().e(this.phone);
        paramBundle = "";
        if (this.currentType != 1)
          break label377;
        paramBundle = AndroidUtilities.replaceTags(LocaleController.getString("SentAppCode", 2131166430));
        label259: this.confirmTextView.setText(paramBundle);
        if (this.currentType == 3)
          break label476;
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
      while (true)
      {
        destroyTimer();
        destroyCodeTimer();
        this.lastCurrentTime = System.currentTimeMillis();
        if (this.currentType != 1)
          break label486;
        this.problemText.setVisibility(0);
        this.timeText.setVisibility(8);
        return;
        if (this.currentType != 3)
          break;
        AndroidUtilities.setWaitingForCall(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
        break;
        label357: this.codeField.setFilters(new InputFilter[0]);
        break label190;
        label371: i = 8;
        break label211;
        label377: if (this.currentType == 2)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentSmsCode", 2131166433, new Object[] { str }));
          break label259;
        }
        if (this.currentType == 3)
        {
          paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallCode", 2131166431, new Object[] { str }));
          break label259;
        }
        if (this.currentType != 4)
          break label259;
        paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("SentCallOnly", 2131166432, new Object[] { str }));
        break label259;
        label476: AndroidUtilities.hideKeyboard(this.codeField);
      }
      label486: if ((this.currentType == 3) && ((this.nextType == 4) || (this.nextType == 2)))
      {
        this.problemText.setVisibility(8);
        this.timeText.setVisibility(0);
        if (this.nextType == 4)
          this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
        while (true)
        {
          createTimer();
          return;
          if (this.nextType != 2)
            continue;
          this.timeText.setText(LocaleController.formatString("SmsText", 2131166475, new Object[] { Integer.valueOf(1), Integer.valueOf(0) }));
        }
      }
      if ((this.currentType == 2) && ((this.nextType == 4) || (this.nextType == 3)))
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
        this.countryButton.setOnClickListener(new View.OnClickListener(ChangePhoneActivity.this)
        {
          public void onClick(View paramView)
          {
            paramView = new CountrySelectActivity(true);
            paramView.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate()
            {
              public void didSelectCountry(String paramString1, String paramString2)
              {
                ChangePhoneActivity.PhoneView.this.selectCountry(paramString1);
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    AndroidUtilities.showKeyboard(ChangePhoneActivity.PhoneView.this.phoneField);
                  }
                }
                , 300L);
                ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
                ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
              }
            });
            ChangePhoneActivity.this.presentFragment(paramView);
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
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable((Context)localObject1, false));
        this.codeField.setPadding(AndroidUtilities.dp(10.0F), 0, 0, 0);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setMaxLines(1);
        this.codeField.setGravity(19);
        this.codeField.setImeOptions(268435461);
        Object localObject3 = new InputFilter.LengthFilter(5);
        this.codeField.setFilters(new InputFilter[] { localObject3 });
        ((LinearLayout)localObject2).addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0F, 0.0F, 16.0F, 0.0F));
        this.codeField.addTextChangedListener(new TextWatcher(ChangePhoneActivity.this)
        {
          public void afterTextChanged(Editable paramEditable)
          {
            Object localObject3 = null;
            if (ChangePhoneActivity.PhoneView.this.ignoreOnTextChange)
              return;
            ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
            paramEditable = b.b(ChangePhoneActivity.PhoneView.this.codeField.getText().toString());
            ChangePhoneActivity.PhoneView.this.codeField.setText(paramEditable);
            if (paramEditable.length() == 0)
            {
              ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165546));
              ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
              ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 1);
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, false);
              return;
            }
            int i;
            label124: Object localObject1;
            Object localObject2;
            label216: Object localObject4;
            if (paramEditable.length() > 4)
            {
              ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
              i = 4;
              if (i >= 1)
              {
                localObject1 = paramEditable.substring(0, i);
                if ((String)ChangePhoneActivity.PhoneView.this.codesMap.get(localObject1) != null)
                {
                  localObject2 = paramEditable.substring(i, paramEditable.length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                  ChangePhoneActivity.PhoneView.this.codeField.setText((CharSequence)localObject1);
                  i = 1;
                  paramEditable = (Editable)localObject1;
                  localObject1 = localObject2;
                  localObject2 = paramEditable;
                  if (i == 0)
                  {
                    ChangePhoneActivity.PhoneView.access$302(ChangePhoneActivity.PhoneView.this, true);
                    localObject1 = paramEditable.substring(1, paramEditable.length()) + ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
                    localObject4 = ChangePhoneActivity.PhoneView.this.codeField;
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
              localObject2 = (String)ChangePhoneActivity.PhoneView.this.codesMap.get(localObject1);
              if (localObject2 != null)
              {
                int j = ChangePhoneActivity.PhoneView.this.countriesArray.indexOf(localObject2);
                if (j != -1)
                {
                  ChangePhoneActivity.PhoneView.access$902(ChangePhoneActivity.PhoneView.this, true);
                  ChangePhoneActivity.PhoneView.this.countryButton.setText((CharSequence)ChangePhoneActivity.PhoneView.this.countriesArray.get(j));
                  localObject4 = (String)ChangePhoneActivity.PhoneView.this.phoneFormatMap.get(localObject1);
                  localObject2 = ChangePhoneActivity.PhoneView.this.phoneField;
                  localObject1 = localObject3;
                  if (localObject4 != null)
                    localObject1 = ((String)localObject4).replace('X', '–');
                  ((HintEditText)localObject2).setHintText((String)localObject1);
                  ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 0);
                }
              }
              while (true)
              {
                if (i == 0)
                  ChangePhoneActivity.PhoneView.this.codeField.setSelection(ChangePhoneActivity.PhoneView.this.codeField.getText().length());
                if (paramEditable == null)
                  break;
                ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
                ChangePhoneActivity.PhoneView.this.phoneField.setText(paramEditable);
                ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
                break;
                i -= 1;
                break label124;
                ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
                ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
                ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 2);
                continue;
                ChangePhoneActivity.PhoneView.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
                ChangePhoneActivity.PhoneView.this.phoneField.setHintText(null);
                ChangePhoneActivity.PhoneView.access$602(ChangePhoneActivity.PhoneView.this, 2);
              }
              localObject1 = null;
              i = 0;
              break label216;
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
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(ChangePhoneActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              ChangePhoneActivity.PhoneView.this.phoneField.requestFocus();
              ChangePhoneActivity.PhoneView.this.phoneField.setSelection(ChangePhoneActivity.PhoneView.this.phoneField.length());
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
        this.phoneField.addTextChangedListener(new TextWatcher(ChangePhoneActivity.this)
        {
          private int actionPosition;
          private int characterAction = -1;

          public void afterTextChanged(Editable paramEditable)
          {
            if (ChangePhoneActivity.PhoneView.this.ignoreOnPhoneChange)
              return;
            int j = ChangePhoneActivity.PhoneView.this.phoneField.getSelectionStart();
            Object localObject = ChangePhoneActivity.PhoneView.this.phoneField.getText().toString();
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
            ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, true);
            paramEditable = ChangePhoneActivity.PhoneView.this.phoneField.getHintText();
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
            ChangePhoneActivity.PhoneView.this.phoneField.setText((CharSequence)localObject);
            if (j >= 0)
            {
              paramEditable = ChangePhoneActivity.PhoneView.this.phoneField;
              if (j > ChangePhoneActivity.PhoneView.this.phoneField.length())
                break label404;
            }
            while (true)
            {
              paramEditable.setSelection(j);
              ChangePhoneActivity.PhoneView.this.phoneField.onTextChange();
              ChangePhoneActivity.PhoneView.access$1102(ChangePhoneActivity.PhoneView.this, false);
              return;
              label404: j = ChangePhoneActivity.PhoneView.this.phoneField.length();
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
        this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener(ChangePhoneActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              ChangePhoneActivity.PhoneView.this.onNextPressed();
              return true;
            }
            return false;
          }
        });
        this.textView2 = new TextView((Context)localObject1);
        this.textView2.setText(LocaleController.getString("ChangePhoneHelp", 2131165438));
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
            break label1174;
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
          Collections.sort(this.countriesArray, new Comparator(ChangePhoneActivity.this)
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
          if (ChangePhoneActivity.this != null)
          {
            this$1 = ChangePhoneActivity.this.getSimCountryIso().toUpperCase();
            if (ChangePhoneActivity.this != null)
            {
              this$1 = (String)((HashMap)localObject1).get(ChangePhoneActivity.this);
              if ((ChangePhoneActivity.this != null) && (this.countriesArray.indexOf(ChangePhoneActivity.this) != -1))
              {
                this.codeField.setText((CharSequence)this.countriesMap.get(ChangePhoneActivity.this));
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
              break label1197;
            AndroidUtilities.showKeyboard(this.phoneField);
            this.phoneField.requestFocus();
            this.phoneField.setSelection(this.phoneField.length());
            return;
            i = 3;
            continue;
            i = 3;
            break label793;
            label1174: i = 3;
            break label824;
            localException.close();
          }
        }
        catch (Exception this$1)
        {
          while (true)
          {
            FileLog.e(ChangePhoneActivity.this);
            this$1 = null;
          }
          label1197: AndroidUtilities.showKeyboard(this.codeField);
          this.codeField.requestFocus();
        }
      }
    }

    public String getHeaderName()
    {
      return LocaleController.getString("ChangePhoneNewNumber", 2131165439);
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
      if ((ChangePhoneActivity.this.getParentActivity() == null) || (this.nextPressed))
        return;
      Object localObject2 = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
      int j;
      int i;
      label81: int k;
      if ((((TelephonyManager)localObject2).getSimState() != 1) && (((TelephonyManager)localObject2).getPhoneType() != 0))
      {
        j = 1;
        if ((Build.VERSION.SDK_INT < 23) || (j == 0))
          break label445;
        if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0)
          break label356;
        i = 1;
        if (ChangePhoneActivity.this.getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0)
          break label361;
        k = 1;
        label99: m = i;
        if (!ChangePhoneActivity.this.checkPermissions)
          break label448;
        ChangePhoneActivity.this.permissionsItems.clear();
        if (i == 0)
          ChangePhoneActivity.this.permissionsItems.add("android.permission.READ_PHONE_STATE");
        if (k == 0)
          ChangePhoneActivity.this.permissionsItems.add("android.permission.RECEIVE_SMS");
        m = i;
        if (ChangePhoneActivity.this.permissionsItems.isEmpty())
          break label448;
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        if ((!((SharedPreferences)localObject1).getBoolean("firstlogin", true)) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.READ_PHONE_STATE")) && (!ChangePhoneActivity.this.getParentActivity().shouldShowRequestPermissionRationale("android.permission.RECEIVE_SMS")))
          break label406;
        ((SharedPreferences)localObject1).edit().putBoolean("firstlogin", false).commit();
        localObject1 = new AlertDialog.Builder(ChangePhoneActivity.this.getParentActivity());
        ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165319));
        ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
        if (ChangePhoneActivity.this.permissionsItems.size() != 2)
          break label366;
        ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCallAndSms", 2131165299));
      }
      while (true)
      {
        ChangePhoneActivity.access$1402(ChangePhoneActivity.this, ChangePhoneActivity.this.showDialog(((AlertDialog.Builder)localObject1).create()));
        return;
        j = 0;
        break;
        label356: i = 0;
        break label81;
        label361: k = 0;
        break label99;
        label366: if (k == 0)
        {
          ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadSms", 2131165300));
          continue;
        }
        ((AlertDialog.Builder)localObject1).setMessage(LocaleController.getString("AllowReadCall", 2131165298));
      }
      label406: ChangePhoneActivity.this.getParentActivity().requestPermissions((String[])ChangePhoneActivity.this.permissionsItems.toArray(new String[ChangePhoneActivity.this.permissionsItems.size()]), 6);
      return;
      label445: int m = 1;
      label448: if (this.countryState == 1)
      {
        AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("ChooseCountry", 2131165546));
        return;
      }
      if ((this.countryState == 2) && (!BuildVars.DEBUG_VERSION))
      {
        AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("WrongCountry", 2131166631));
        return;
      }
      if (this.codeField.length() == 0)
      {
        AlertsCreator.showSimpleAlert(ChangePhoneActivity.this, LocaleController.getString("InvalidPhoneNumber", 2131165841));
        return;
      }
      Object localObject1 = new TLRPC.TL_account_sendChangePhoneCode();
      String str = b.b("" + this.codeField.getText() + this.phoneField.getText());
      ((TLRPC.TL_account_sendChangePhoneCode)localObject1).phone_number = str;
      boolean bool;
      if ((j != 0) && (m != 0))
        bool = true;
      while (true)
      {
        ((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall = bool;
        if (((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall);
        try
        {
          localObject2 = ((TelephonyManager)localObject2).getLine1Number();
          if (!TextUtils.isEmpty((CharSequence)localObject2))
          {
            if (str.contains((CharSequence)localObject2))
              break label910;
            if (((String)localObject2).contains(str))
            {
              break label910;
              ((TLRPC.TL_account_sendChangePhoneCode)localObject1).current_number = bool;
              if (!((TLRPC.TL_account_sendChangePhoneCode)localObject1).current_number)
                ((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall = false;
              localObject2 = new Bundle();
              ((Bundle)localObject2).putString("phone", "+" + this.codeField.getText() + this.phoneField.getText());
            }
          }
        }
        catch (Exception localException1)
        {
          while (true)
          {
            try
            {
              ((Bundle)localObject2).putString("ephone", "+" + b.b(this.codeField.getText().toString()) + " " + b.b(this.phoneField.getText().toString()));
              ((Bundle)localObject2).putString("phoneFormated", str);
              this.nextPressed = true;
              ChangePhoneActivity.this.needShowProgress();
              ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate((Bundle)localObject2, (TLRPC.TL_account_sendChangePhoneCode)localObject1)
              {
                public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                {
                  AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                  {
                    public void run()
                    {
                      ChangePhoneActivity.PhoneView.access$1502(ChangePhoneActivity.PhoneView.this, false);
                      if (this.val$error == null)
                        ChangePhoneActivity.this.fillNextCodeParams(ChangePhoneActivity.PhoneView.7.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
                      while (true)
                      {
                        ChangePhoneActivity.this.needHideProgress();
                        return;
                        AlertsCreator.processError(this.val$error, ChangePhoneActivity.this, ChangePhoneActivity.PhoneView.7.this.val$req, new Object[] { ChangePhoneActivity.PhoneView.7.this.val$params.getString("phone") });
                      }
                    }
                  });
                }
              }
              , 2);
              return;
              bool = false;
              break;
              bool = false;
              continue;
              ((TLRPC.TL_account_sendChangePhoneCode)localObject1).current_number = false;
              continue;
              localException1 = localException1;
              ((TLRPC.TL_account_sendChangePhoneCode)localObject1).allow_flashcall = false;
              FileLog.e(localException1);
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              localException1.putString("ephone", "+" + str);
              continue;
            }
            label910: bool = true;
          }
        }
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
        if (this.codeField.length() != 0)
        {
          AndroidUtilities.showKeyboard(this.phoneField);
          this.phoneField.requestFocus();
          this.phoneField.setSelection(this.phoneField.length());
        }
      }
      else
        return;
      AndroidUtilities.showKeyboard(this.codeField);
      this.codeField.requestFocus();
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
 * Qualified Name:     org.vidogram.ui.ChangePhoneActivity
 * JD-Core Version:    0.6.0
 */