package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_account_confirmPhone;
import org.vidogram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
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
import org.vidogram.tgnet.TLRPC.auth_SentCodeType;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.SlideView;

public class CancelAccountDeletionActivity extends BaseFragment
{
  private static final int done_button = 1;
  private boolean checkPermissions = false;
  private int currentViewNum = 0;
  private View doneButton;
  private Dialog errorDialog;
  private String hash;
  private Dialog permissionsDialog;
  private ArrayList<String> permissionsItems = new ArrayList();
  private String phone;
  private AlertDialog progressDialog;
  private SlideView[] views = new SlideView[5];

  public CancelAccountDeletionActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.hash = paramBundle.getString("hash");
    this.phone = paramBundle.getString("phone");
  }

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
          CancelAccountDeletionActivity.this.views[CancelAccountDeletionActivity.this.currentViewNum].onNextPressed();
        do
          return;
        while (paramInt != -1);
        CancelAccountDeletionActivity.this.finishFragment();
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.doneButton.setVisibility(8);
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
      label229: float f1;
      label252: float f2;
      label262: float f3;
      if (i == 0)
      {
        j = 0;
        paramContext.setVisibility(j);
        paramContext = this.views[i];
        if (i != 0)
          break label309;
        f1 = -2.0F;
        if (!AndroidUtilities.isTablet())
          break label316;
        f2 = 26.0F;
        if (!AndroidUtilities.isTablet())
          break label323;
        f3 = 26.0F;
      }
      while (true)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, f1, 51, f2, 30.0F, f3, 0.0F));
        i += 1;
        break;
        j = 8;
        break label229;
        label309: f1 = -1.0F;
        break label252;
        label316: f2 = 18.0F;
        break label262;
        label323: f3 = 18.0F;
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
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(localPhoneView.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(localLoginActivitySmsView1.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView1.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView1.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView1.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView1.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView2.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView2.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView2.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView2.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView2.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView3.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView3.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView3.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView3.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView3.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter"), new ThemeDescription(localLoginActivitySmsView4.confirmTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(localLoginActivitySmsView4.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(localLoginActivitySmsView4.timeText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(localLoginActivitySmsView4.problemText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText4"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressInner"), new ThemeDescription(localLoginActivitySmsView4.progressView, 0, new Class[] { ProgressView.class }, new String[] { "paint" }, null, null, null, "login_progressOuter") };
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
    while (i < this.views.length)
    {
      if (this.views[i] != null)
        this.views[i].onDestroyActivity();
      i += 1;
    }
    return true;
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    if ((Build.VERSION.SDK_INT >= 23) && (paramDialog == this.permissionsDialog) && (!this.permissionsItems.isEmpty()))
      getParentActivity().requestPermissions((String[])this.permissionsItems.toArray(new String[this.permissionsItems.size()]), 6);
    if (paramDialog == this.errorDialog)
      finishFragment();
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
    if ((paramInt == 3) || (paramInt == 0))
    {
      if (paramInt == 0);
      this.doneButton.setVisibility(8);
      localSlideView1 = this.views[this.currentViewNum];
      localSlideView2 = this.views[paramInt];
      this.currentViewNum = paramInt;
      localSlideView2.setParams(paramBundle, false);
      this.actionBar.setTitle(localSlideView2.getHeaderName());
      localSlideView2.onShow();
      if (!paramBoolean2)
        break label210;
      f = -AndroidUtilities.displaySize.x;
      label85: localSlideView2.setX(f);
      paramBundle = new AnimatorSet();
      paramBundle.setInterpolator(new AccelerateDecelerateInterpolator());
      paramBundle.setDuration(300L);
      if (!paramBoolean2)
        break label222;
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
      this.doneButton.setVisibility(0);
      break;
      label210: f = AndroidUtilities.displaySize.x;
      break label85;
      label222: f = -AndroidUtilities.displaySize.x;
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
    private CancelAccountDeletionActivity.ProgressView progressView;
    private volatile int time = 60000;
    private TextView timeText;
    private Timer timeTimer;
    private int timeout;
    private final Object timerSync = new Object();
    private boolean waitingForEvent;

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
          break label804;
        localObject1 = new FrameLayout(paramInt);
        localObject2 = new ImageView(paramInt);
        ((ImageView)localObject2).setImageResource(2130837996);
        if (!LocaleController.isRTL)
          break label738;
        ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 19, 2.0F, 2.0F, 0.0F, 0.0F));
        localObject2 = this.confirmTextView;
        if (!LocaleController.isRTL)
          break label733;
        i = 5;
        label198: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, i, 82.0F, 0.0F, 0.0F, 0.0F));
        if (!LocaleController.isRTL)
          break label799;
        i = 5;
        label225: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i));
        this.codeField = new EditText(paramInt);
        this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.codeField.setHint(LocaleController.getString("Code", 2131165558));
        AndroidUtilities.clearCursorDrawable(this.codeField);
        this.codeField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.codeField.setImeOptions(268435461);
        this.codeField.setTextSize(1, 18.0F);
        this.codeField.setInputType(3);
        this.codeField.setMaxLines(1);
        this.codeField.setPadding(0, 0, 0, 0);
        this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(paramInt, false));
        addView(this.codeField, LayoutHelper.createLinear(-1, 36, 1, 0, 20, 0, 0));
        this.codeField.addTextChangedListener(new TextWatcher(CancelAccountDeletionActivity.this)
        {
          public void afterTextChanged(Editable paramEditable)
          {
            if (CancelAccountDeletionActivity.LoginActivitySmsView.this.ignoreOnTextChange);
            do
              return;
            while ((CancelAccountDeletionActivity.LoginActivitySmsView.this.length == 0) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.codeField.length() != CancelAccountDeletionActivity.LoginActivitySmsView.this.length));
            CancelAccountDeletionActivity.LoginActivitySmsView.this.onNextPressed();
          }

          public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }

          public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
          {
          }
        });
        this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener(CancelAccountDeletionActivity.this)
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if (paramInt == 5)
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.this.onNextPressed();
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
          break label840;
        i = 5;
        label500: ((TextView)localObject1).setGravity(i);
        localObject1 = this.timeText;
        if (!LocaleController.isRTL)
          break label845;
        i = 5;
        label520: addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 0, 30, 0, 0));
        if (this.currentType == 3)
        {
          this.progressView = new CancelAccountDeletionActivity.ProgressView(CancelAccountDeletionActivity.this, paramInt);
          addView(this.progressView, LayoutHelper.createLinear(-1, 3, 0.0F, 12.0F, 0.0F, 0.0F));
        }
        this.problemText = new TextView(paramInt);
        this.problemText.setText(LocaleController.getString("DidNotGetTheCode", 2131165655));
        paramInt = this.problemText;
        if (!LocaleController.isRTL)
          break label850;
        i = 5;
        label620: paramInt.setGravity(i);
        this.problemText.setTextSize(1, 14.0F);
        this.problemText.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText4"));
        this.problemText.setLineSpacing(AndroidUtilities.dp(2.0F), 1.0F);
        this.problemText.setPadding(0, AndroidUtilities.dp(2.0F), 0, AndroidUtilities.dp(12.0F));
        paramInt = this.problemText;
        if (!LocaleController.isRTL)
          break label855;
      }
      label799: label804: label840: label845: label850: label855: for (int i = 5; ; i = 3)
      {
        addView(paramInt, LayoutHelper.createLinear(-2, -2, i, 0, 20, 0, 0));
        this.problemText.setOnClickListener(new View.OnClickListener(CancelAccountDeletionActivity.this)
        {
          public void onClick(View paramView)
          {
            if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextPressed)
              return;
            if ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 0) && (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 4))
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
              return;
            }
            try
            {
              paramView = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
              paramView = String.format(Locale.US, "%s (%d)", new Object[] { paramView.versionName, Integer.valueOf(paramView.versionCode) });
              Intent localIntent = new Intent("android.intent.action.SEND");
              localIntent.setType("message/rfc822");
              localIntent.putExtra("android.intent.extra.EMAIL", new String[] { "sms@stel.com" });
              localIntent.putExtra("android.intent.extra.SUBJECT", "Android cancel account deletion issue " + paramView + " " + CancelAccountDeletionActivity.LoginActivitySmsView.this.phone);
              localIntent.putExtra("android.intent.extra.TEXT", "Phone: " + CancelAccountDeletionActivity.LoginActivitySmsView.this.phone + "\nApp version: " + paramView + "\nOS version: SDK " + Build.VERSION.SDK_INT + "\nDevice Name: " + Build.MANUFACTURER + Build.MODEL + "\nLocale: " + Locale.getDefault() + "\nError: " + CancelAccountDeletionActivity.LoginActivitySmsView.this.lastError);
              CancelAccountDeletionActivity.LoginActivitySmsView.this.getContext().startActivity(Intent.createChooser(localIntent, "Send email..."));
              return;
            }
            catch (Exception paramView)
            {
              AlertsCreator.showSimpleAlert(CancelAccountDeletionActivity.this, LocaleController.getString("NoMailInstalled", 2131166031));
            }
          }
        });
        return;
        i = 3;
        break;
        label733: i = 3;
        break label198;
        label738: TextView localTextView = this.confirmTextView;
        if (LocaleController.isRTL);
        for (i = 5; ; i = 3)
        {
          ((FrameLayout)localObject1).addView(localTextView, LayoutHelper.createFrame(-1, -2.0F, i, 0.0F, 0.0F, 82.0F, 0.0F));
          ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 76.0F, 21, 0.0F, 2.0F, 0.0F, 2.0F));
          break;
        }
        i = 3;
        break label225;
        localObject1 = this.confirmTextView;
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
        break label620;
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
          double d2 = CancelAccountDeletionActivity.LoginActivitySmsView.this.lastCodeTime;
          CancelAccountDeletionActivity.LoginActivitySmsView.access$1602(CancelAccountDeletionActivity.LoginActivitySmsView.this, (int)(CancelAccountDeletionActivity.LoginActivitySmsView.this.codeTime - (d1 - d2)));
          CancelAccountDeletionActivity.LoginActivitySmsView.access$1502(CancelAccountDeletionActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (CancelAccountDeletionActivity.LoginActivitySmsView.this.codeTime <= 1000)
              {
                CancelAccountDeletionActivity.LoginActivitySmsView.this.problemText.setVisibility(0);
                CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
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
          if (CancelAccountDeletionActivity.LoginActivitySmsView.this.timeTimer == null)
            return;
          double d1 = System.currentTimeMillis();
          double d2 = CancelAccountDeletionActivity.LoginActivitySmsView.this.lastCurrentTime;
          CancelAccountDeletionActivity.LoginActivitySmsView.access$2102(CancelAccountDeletionActivity.LoginActivitySmsView.this, (int)(CancelAccountDeletionActivity.LoginActivitySmsView.this.time - (d1 - d2)));
          CancelAccountDeletionActivity.LoginActivitySmsView.access$2002(CancelAccountDeletionActivity.LoginActivitySmsView.this, d1);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              int i;
              int j;
              if (CancelAccountDeletionActivity.LoginActivitySmsView.this.time >= 1000)
              {
                i = CancelAccountDeletionActivity.LoginActivitySmsView.this.time / 1000 / 60;
                j = CancelAccountDeletionActivity.LoginActivitySmsView.this.time / 1000 - i * 60;
                if ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 3))
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("CallText", 2131165421, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
              }
              do
              {
                do
                {
                  while (true)
                  {
                    if (CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView != null)
                      CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F - CancelAccountDeletionActivity.LoginActivitySmsView.this.time / CancelAccountDeletionActivity.LoginActivitySmsView.this.timeout);
                    return;
                    if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 2)
                      continue;
                    CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.formatString("SmsText", 2131166475, new Object[] { Integer.valueOf(i), Integer.valueOf(j) }));
                  }
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView != null)
                    CancelAccountDeletionActivity.LoginActivitySmsView.this.progressView.setProgress(1.0F);
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyTimer();
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType != 3)
                    continue;
                  AndroidUtilities.setWaitingForCall(false);
                  NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveCall);
                  CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
                  return;
                }
                while (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType != 2);
                if (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 4)
                  continue;
                CancelAccountDeletionActivity.LoginActivitySmsView.this.timeText.setText(LocaleController.getString("Calling", 2131165423));
                CancelAccountDeletionActivity.LoginActivitySmsView.this.createCodeTimer();
                TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
                localTL_auth_resendCode.phone_number = CancelAccountDeletionActivity.LoginActivitySmsView.this.phone;
                localTL_auth_resendCode.phone_code_hash = CancelAccountDeletionActivity.LoginActivitySmsView.this.phoneHash;
                ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    if ((paramTL_error != null) && (paramTL_error.text != null))
                      AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                      {
                        public void run()
                        {
                          CancelAccountDeletionActivity.LoginActivitySmsView.access$1402(CancelAccountDeletionActivity.LoginActivitySmsView.this, this.val$error.text);
                        }
                      });
                  }
                }
                , 2);
                return;
              }
              while (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType != 3);
              AndroidUtilities.setWaitingForSms(false);
              NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReceiveSmsCode);
              CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
              CancelAccountDeletionActivity.LoginActivitySmsView.this.destroyCodeTimer();
              CancelAccountDeletionActivity.LoginActivitySmsView.this.resendCode();
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
      this.nextPressed = true;
      CancelAccountDeletionActivity.this.needShowProgress();
      TLRPC.TL_auth_resendCode localTL_auth_resendCode = new TLRPC.TL_auth_resendCode();
      localTL_auth_resendCode.phone_number = this.phone;
      localTL_auth_resendCode.phone_code_hash = this.phoneHash;
      ConnectionsManager.getInstance().sendRequest(localTL_auth_resendCode, new RequestDelegate(localBundle, localTL_auth_resendCode)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              CancelAccountDeletionActivity.LoginActivitySmsView.access$1002(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
              if (this.val$error == null)
                CancelAccountDeletionActivity.this.fillNextCodeParams(CancelAccountDeletionActivity.LoginActivitySmsView.4.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
              while (true)
              {
                CancelAccountDeletionActivity.this.needHideProgress();
                return;
                AlertsCreator.processError(this.val$error, CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.LoginActivitySmsView.4.this.val$req, new Object[0]);
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
      return LocaleController.getString("CancelAccountReset", 2131165428);
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
        TLRPC.TL_account_confirmPhone localTL_account_confirmPhone = new TLRPC.TL_account_confirmPhone();
        localTL_account_confirmPhone.phone_code = this.codeField.getText().toString();
        localTL_account_confirmPhone.phone_code_hash = this.phoneHash;
        destroyTimer();
        CancelAccountDeletionActivity.this.needShowProgress();
        ConnectionsManager.getInstance().sendRequest(localTL_account_confirmPhone, new RequestDelegate(localTL_account_confirmPhone)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
            {
              public void run()
              {
                CancelAccountDeletionActivity.this.needHideProgress();
                CancelAccountDeletionActivity.LoginActivitySmsView.access$1002(CancelAccountDeletionActivity.LoginActivitySmsView.this, false);
                if (this.val$error == null)
                {
                  CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, AlertsCreator.showSimpleAlert(CancelAccountDeletionActivity.this, LocaleController.formatString("CancelLinkSuccess", 2131165431, new Object[] { b.a().e("+" + CancelAccountDeletionActivity.LoginActivitySmsView.access$1300(CancelAccountDeletionActivity.LoginActivitySmsView.this)) })));
                  return;
                }
                CancelAccountDeletionActivity.LoginActivitySmsView.access$1402(CancelAccountDeletionActivity.LoginActivitySmsView.this, this.val$error.text);
                if (((CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3) && ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 2))) || ((CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 2) && ((CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 4) || (CancelAccountDeletionActivity.LoginActivitySmsView.this.nextType == 3))))
                  CancelAccountDeletionActivity.LoginActivitySmsView.this.createTimer();
                if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 2)
                {
                  AndroidUtilities.setWaitingForSms(true);
                  NotificationCenter.getInstance().addObserver(CancelAccountDeletionActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveSmsCode);
                }
                while (true)
                {
                  CancelAccountDeletionActivity.LoginActivitySmsView.access$2702(CancelAccountDeletionActivity.LoginActivitySmsView.this, true);
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType == 3)
                    break;
                  AlertsCreator.processError(this.val$error, CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.LoginActivitySmsView.7.this.val$req, new Object[0]);
                  return;
                  if (CancelAccountDeletionActivity.LoginActivitySmsView.this.currentType != 3)
                    continue;
                  AndroidUtilities.setWaitingForCall(true);
                  NotificationCenter.getInstance().addObserver(CancelAccountDeletionActivity.LoginActivitySmsView.this, NotificationCenter.didReceiveCall);
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
        this.phoneHash = paramBundle.getString("phoneHash");
        i = paramBundle.getInt("timeout");
        this.time = i;
        this.timeout = i;
        this.openTime = (int)(System.currentTimeMillis() / 1000L);
        this.nextType = paramBundle.getInt("nextType");
        this.pattern = paramBundle.getString("pattern");
        this.length = paramBundle.getInt("length");
        if (this.length == 0)
          break label356;
        paramBundle = new InputFilter.LengthFilter(this.length);
        this.codeField.setFilters(new InputFilter[] { paramBundle });
        label168: if (this.progressView != null)
        {
          paramBundle = this.progressView;
          if (this.nextType == 0)
            break label370;
          i = 0;
          label189: paramBundle.setVisibility(i);
        }
        if (this.phone == null)
          continue;
        paramBundle = b.a().e(this.phone);
        paramBundle = AndroidUtilities.replaceTags(LocaleController.formatString("CancelAccountResetInfo", 2131165429, new Object[] { b.a().e("+" + paramBundle) }));
        this.confirmTextView.setText(paramBundle);
        if (this.currentType == 3)
          break label376;
        AndroidUtilities.showKeyboard(this.codeField);
        this.codeField.requestFocus();
      }
      while (true)
      {
        destroyTimer();
        destroyCodeTimer();
        this.lastCurrentTime = System.currentTimeMillis();
        if (this.currentType != 1)
          break label386;
        this.problemText.setVisibility(0);
        this.timeText.setVisibility(8);
        return;
        if (this.currentType != 3)
          break;
        AndroidUtilities.setWaitingForCall(true);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReceiveCall);
        break;
        label356: this.codeField.setFilters(new InputFilter[0]);
        break label168;
        label370: i = 8;
        break label189;
        label376: AndroidUtilities.hideKeyboard(this.codeField);
      }
      label386: if ((this.currentType == 3) && ((this.nextType == 4) || (this.nextType == 2)))
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
  {
    private boolean nextPressed = false;
    private RadialProgressView progressBar;

    public PhoneView(Context arg2)
    {
      super();
      setOrientation(1);
      this$1 = new FrameLayout(localContext);
      addView(CancelAccountDeletionActivity.this, LayoutHelper.createLinear(-1, 200));
      this.progressBar = new RadialProgressView(localContext);
      CancelAccountDeletionActivity.this.addView(this.progressBar, LayoutHelper.createFrame(-2, -2, 17));
    }

    public String getHeaderName()
    {
      return LocaleController.getString("CancelAccountReset", 2131165428);
    }

    public void onNextPressed()
    {
      if ((CancelAccountDeletionActivity.this.getParentActivity() == null) || (this.nextPressed))
        return;
      Object localObject = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
      int i;
      if ((((TelephonyManager)localObject).getSimState() != 1) && (((TelephonyManager)localObject).getPhoneType() != 0))
        i = 1;
      while (true)
      {
        if ((Build.VERSION.SDK_INT >= 23) && (i != 0));
        TLRPC.TL_account_sendConfirmPhoneCode localTL_account_sendConfirmPhoneCode = new TLRPC.TL_account_sendConfirmPhoneCode();
        localTL_account_sendConfirmPhoneCode.allow_flashcall = false;
        localTL_account_sendConfirmPhoneCode.hash = CancelAccountDeletionActivity.this.hash;
        if (localTL_account_sendConfirmPhoneCode.allow_flashcall);
        try
        {
          localObject = ((TelephonyManager)localObject).getLine1Number();
          if (!TextUtils.isEmpty((CharSequence)localObject))
          {
            if (CancelAccountDeletionActivity.this.phone.contains((CharSequence)localObject))
              break label240;
            if (((String)localObject).contains(CancelAccountDeletionActivity.this.phone))
            {
              break label240;
              label141: localTL_account_sendConfirmPhoneCode.current_number = bool;
              if (!localTL_account_sendConfirmPhoneCode.current_number)
                localTL_account_sendConfirmPhoneCode.allow_flashcall = false;
            }
          }
          while (true)
          {
            localObject = new Bundle();
            ((Bundle)localObject).putString("phone", CancelAccountDeletionActivity.this.phone);
            this.nextPressed = true;
            ConnectionsManager.getInstance().sendRequest(localTL_account_sendConfirmPhoneCode, new RequestDelegate((Bundle)localObject, localTL_account_sendConfirmPhoneCode)
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
                {
                  public void run()
                  {
                    CancelAccountDeletionActivity.PhoneView.access$402(CancelAccountDeletionActivity.PhoneView.this, false);
                    if (this.val$error == null)
                    {
                      CancelAccountDeletionActivity.this.fillNextCodeParams(CancelAccountDeletionActivity.PhoneView.1.this.val$params, (TLRPC.TL_auth_sentCode)this.val$response);
                      return;
                    }
                    CancelAccountDeletionActivity.access$602(CancelAccountDeletionActivity.this, AlertsCreator.processError(this.val$error, CancelAccountDeletionActivity.this, CancelAccountDeletionActivity.PhoneView.1.this.val$req, new Object[0]));
                  }
                });
              }
            }
            , 2);
            return;
            i = 0;
            break;
            bool = false;
            break label141;
            localTL_account_sendConfirmPhoneCode.current_number = false;
          }
        }
        catch (Exception localException)
        {
          while (true)
          {
            localTL_account_sendConfirmPhoneCode.allow_flashcall = false;
            FileLog.e(localException);
            continue;
            label240: boolean bool = true;
          }
        }
      }
    }

    public void onShow()
    {
      super.onShow();
      onNextPressed();
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
 * Qualified Name:     org.vidogram.ui.CancelAccountDeletionActivity
 * JD-Core Version:    0.6.0
 */