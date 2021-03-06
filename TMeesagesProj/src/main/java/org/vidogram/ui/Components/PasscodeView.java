package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.support.v4.e.d;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.vidogram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationCallback;
import org.vidogram.messenger.support.fingerprint.FingerprintManagerCompat.AuthenticationResult;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.Theme;

public class PasscodeView extends FrameLayout
{
  private static final int id_fingerprint_imageview = 1001;
  private static final int id_fingerprint_textview = 1000;
  private Drawable backgroundDrawable;
  private FrameLayout backgroundFrameLayout;
  private d cancellationSignal;
  private ImageView checkImage;
  private PasscodeViewDelegate delegate;
  private ImageView eraseView;
  private AlertDialog fingerprintDialog;
  private ImageView fingerprintImageView;
  private TextView fingerprintStatusTextView;
  private int keyboardHeight = 0;
  private ArrayList<TextView> lettersTextViews;
  private ArrayList<FrameLayout> numberFrameLayouts;
  private ArrayList<TextView> numberTextViews;
  private FrameLayout numbersFrameLayout;
  private TextView passcodeTextView;
  private EditText passwordEditText;
  private AnimatingTextView passwordEditText2;
  private FrameLayout passwordFrameLayout;
  private Rect rect = new Rect();
  private boolean selfCancelled;

  public PasscodeView(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    setVisibility(8);
    this.backgroundFrameLayout = new FrameLayout(paramContext);
    addView(this.backgroundFrameLayout);
    Object localObject = (FrameLayout.LayoutParams)this.backgroundFrameLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    this.backgroundFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.passwordFrameLayout = new FrameLayout(paramContext);
    addView(this.passwordFrameLayout);
    localObject = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = -1;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).gravity = 51;
    this.passwordFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
    localObject = new ImageView(paramContext);
    ((ImageView)localObject).setScaleType(ImageView.ScaleType.FIT_XY);
    ((ImageView)localObject).setImageResource(2130837995);
    this.passwordFrameLayout.addView((View)localObject);
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)((ImageView)localObject).getLayoutParams();
    if (AndroidUtilities.density < 1.0F)
    {
      localLayoutParams.width = AndroidUtilities.dp(30.0F);
      localLayoutParams.height = AndroidUtilities.dp(30.0F);
      localLayoutParams.gravity = 81;
      localLayoutParams.bottomMargin = AndroidUtilities.dp(100.0F);
      ((ImageView)localObject).setLayoutParams(localLayoutParams);
      this.passcodeTextView = new TextView(paramContext);
      this.passcodeTextView.setTextColor(-1);
      this.passcodeTextView.setTextSize(1, 14.0F);
      this.passcodeTextView.setGravity(1);
      this.passwordFrameLayout.addView(this.passcodeTextView);
      localObject = (FrameLayout.LayoutParams)this.passcodeTextView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -2;
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(62.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      this.passcodeTextView.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.passwordEditText2 = new AnimatingTextView(paramContext);
      this.passwordFrameLayout.addView(this.passwordEditText2);
      localObject = (FrameLayout.LayoutParams)this.passwordEditText2.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(6.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      this.passwordEditText2.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.passwordEditText = new EditText(paramContext);
      this.passwordEditText.setTextSize(1, 36.0F);
      this.passwordEditText.setTextColor(-1);
      this.passwordEditText.setMaxLines(1);
      this.passwordEditText.setLines(1);
      this.passwordEditText.setGravity(1);
      this.passwordEditText.setSingleLine(true);
      this.passwordEditText.setImeOptions(6);
      this.passwordEditText.setTypeface(Typeface.DEFAULT);
      this.passwordEditText.setBackgroundDrawable(null);
      AndroidUtilities.clearCursorDrawable(this.passwordEditText);
      this.passwordFrameLayout.addView(this.passwordEditText);
      localObject = (FrameLayout.LayoutParams)this.passwordEditText.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).height = -2;
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(70.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(6.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 81;
      this.passwordEditText.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          int i = 0;
          if (paramInt == 6)
          {
            PasscodeView.this.processDone(false);
            i = 1;
          }
          return i;
        }
      });
      this.passwordEditText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
          if ((PasscodeView.this.passwordEditText.length() == 4) && (UserConfig.passcodeType == 0))
            PasscodeView.this.processDone(false);
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
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
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setImageResource(2130837993);
      this.checkImage.setScaleType(ImageView.ScaleType.CENTER);
      this.checkImage.setBackgroundResource(2130837624);
      this.passwordFrameLayout.addView(this.checkImage);
      localObject = (FrameLayout.LayoutParams)this.checkImage.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(60.0F);
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(60.0F);
      ((FrameLayout.LayoutParams)localObject).bottomMargin = AndroidUtilities.dp(4.0F);
      ((FrameLayout.LayoutParams)localObject).rightMargin = AndroidUtilities.dp(10.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 85;
      this.checkImage.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.checkImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PasscodeView.this.processDone(false);
        }
      });
      localObject = new FrameLayout(paramContext);
      ((FrameLayout)localObject).setBackgroundColor(654311423);
      this.passwordFrameLayout.addView((View)localObject);
      localLayoutParams = (FrameLayout.LayoutParams)((FrameLayout)localObject).getLayoutParams();
      localLayoutParams.width = -1;
      localLayoutParams.height = AndroidUtilities.dp(1.0F);
      localLayoutParams.gravity = 83;
      localLayoutParams.leftMargin = AndroidUtilities.dp(20.0F);
      localLayoutParams.rightMargin = AndroidUtilities.dp(20.0F);
      ((FrameLayout)localObject).setLayoutParams(localLayoutParams);
      this.numbersFrameLayout = new FrameLayout(paramContext);
      addView(this.numbersFrameLayout);
      localObject = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = -1;
      ((FrameLayout.LayoutParams)localObject).height = -1;
      ((FrameLayout.LayoutParams)localObject).gravity = 51;
      this.numbersFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      this.lettersTextViews = new ArrayList(10);
      this.numberTextViews = new ArrayList(10);
      this.numberFrameLayouts = new ArrayList(10);
      i = 0;
      label935: if (i >= 10)
        break label1323;
      localObject = new TextView(paramContext);
      ((TextView)localObject).setTextColor(-1);
      ((TextView)localObject).setTextSize(1, 36.0F);
      ((TextView)localObject).setGravity(17);
      ((TextView)localObject).setText(String.format(Locale.US, "%d", new Object[] { Integer.valueOf(i) }));
      this.numbersFrameLayout.addView((View)localObject);
      localLayoutParams = (FrameLayout.LayoutParams)((TextView)localObject).getLayoutParams();
      localLayoutParams.width = AndroidUtilities.dp(50.0F);
      localLayoutParams.height = AndroidUtilities.dp(50.0F);
      localLayoutParams.gravity = 51;
      ((TextView)localObject).setLayoutParams(localLayoutParams);
      this.numberTextViews.add(localObject);
      localObject = new TextView(paramContext);
      ((TextView)localObject).setTextSize(1, 12.0F);
      ((TextView)localObject).setTextColor(2147483647);
      ((TextView)localObject).setGravity(17);
      this.numbersFrameLayout.addView((View)localObject);
      localLayoutParams = (FrameLayout.LayoutParams)((TextView)localObject).getLayoutParams();
      localLayoutParams.width = AndroidUtilities.dp(50.0F);
      localLayoutParams.height = AndroidUtilities.dp(20.0F);
      localLayoutParams.gravity = 51;
      ((TextView)localObject).setLayoutParams(localLayoutParams);
      switch (i)
      {
      case 1:
      default:
      case 0:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      }
    }
    while (true)
    {
      this.lettersTextViews.add(localObject);
      i += 1;
      break label935;
      localLayoutParams.width = AndroidUtilities.dp(40.0F);
      localLayoutParams.height = AndroidUtilities.dp(40.0F);
      break;
      ((TextView)localObject).setText("+");
      continue;
      ((TextView)localObject).setText("ABC");
      continue;
      ((TextView)localObject).setText("DEF");
      continue;
      ((TextView)localObject).setText("GHI");
      continue;
      ((TextView)localObject).setText("JKL");
      continue;
      ((TextView)localObject).setText("MNO");
      continue;
      ((TextView)localObject).setText("PQRS");
      continue;
      ((TextView)localObject).setText("TUV");
      continue;
      ((TextView)localObject).setText("WXYZ");
    }
    label1323: this.eraseView = new ImageView(paramContext);
    this.eraseView.setScaleType(ImageView.ScaleType.CENTER);
    this.eraseView.setImageResource(2130837994);
    this.numbersFrameLayout.addView(this.eraseView);
    localObject = (FrameLayout.LayoutParams)this.eraseView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(50.0F);
    ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(50.0F);
    ((FrameLayout.LayoutParams)localObject).gravity = 51;
    this.eraseView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    int i = 0;
    while (i < 11)
    {
      localObject = new FrameLayout(paramContext);
      ((FrameLayout)localObject).setBackgroundResource(2130837624);
      ((FrameLayout)localObject).setTag(Integer.valueOf(i));
      if (i == 10)
        ((FrameLayout)localObject).setOnLongClickListener(new View.OnLongClickListener()
        {
          public boolean onLongClick(View paramView)
          {
            PasscodeView.this.passwordEditText.setText("");
            PasscodeView.AnimatingTextView.access$700(PasscodeView.this.passwordEditText2, true);
            return true;
          }
        });
      ((FrameLayout)localObject).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          switch (((Integer)paramView.getTag()).intValue())
          {
          default:
          case 0:
          case 1:
          case 2:
          case 3:
          case 4:
          case 5:
          case 6:
          case 7:
          case 8:
          case 9:
          case 10:
          }
          while (true)
          {
            if (PasscodeView.this.passwordEditText2.lenght() == 4)
              PasscodeView.this.processDone(false);
            return;
            PasscodeView.this.passwordEditText2.appendCharacter("0");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("1");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("2");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("3");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("4");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("5");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("6");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("7");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("8");
            continue;
            PasscodeView.this.passwordEditText2.appendCharacter("9");
            continue;
            PasscodeView.this.passwordEditText2.eraseLastCharacter();
          }
        }
      });
      this.numberFrameLayouts.add(localObject);
      i += 1;
    }
    i = 10;
    while (i >= 0)
    {
      paramContext = (FrameLayout)this.numberFrameLayouts.get(i);
      this.numbersFrameLayout.addView(paramContext);
      localObject = (FrameLayout.LayoutParams)paramContext.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject).width = AndroidUtilities.dp(100.0F);
      ((FrameLayout.LayoutParams)localObject).height = AndroidUtilities.dp(100.0F);
      ((FrameLayout.LayoutParams)localObject).gravity = 51;
      paramContext.setLayoutParams((ViewGroup.LayoutParams)localObject);
      i -= 1;
    }
  }

  private void checkFingerprint()
  {
    Activity localActivity = (Activity)getContext();
    if ((Build.VERSION.SDK_INT >= 23) && (localActivity != null) && (UserConfig.useFingerprint) && (!ApplicationLoader.mainInterfacePaused));
    try
    {
      if (this.fingerprintDialog != null)
      {
        boolean bool = this.fingerprintDialog.isShowing();
        if (bool)
          return;
      }
    }
    catch (Exception localThrowable)
    {
      while (true)
      {
        FileLog.e(localException1);
        try
        {
          FingerprintManagerCompat localFingerprintManagerCompat = FingerprintManagerCompat.from(ApplicationLoader.applicationContext);
          if ((!localFingerprintManagerCompat.isHardwareDetected()) || (!localFingerprintManagerCompat.hasEnrolledFingerprints()))
            continue;
          Object localObject1 = new RelativeLayout(getContext());
          ((RelativeLayout)localObject1).setPadding(AndroidUtilities.dp(24.0F), 0, AndroidUtilities.dp(24.0F), 0);
          Object localObject2 = new TextView(getContext());
          ((TextView)localObject2).setTextColor(-7105645);
          ((TextView)localObject2).setId(1000);
          ((TextView)localObject2).setTextAppearance(16974344);
          ((TextView)localObject2).setText(LocaleController.getString("FingerprintInfo", 2131165712));
          ((RelativeLayout)localObject1).addView((View)localObject2);
          RelativeLayout.LayoutParams localLayoutParams = LayoutHelper.createRelative(-2, -2);
          localLayoutParams.addRule(10);
          localLayoutParams.addRule(20);
          ((TextView)localObject2).setLayoutParams(localLayoutParams);
          this.fingerprintImageView = new ImageView(getContext());
          this.fingerprintImageView.setImageResource(2130837776);
          this.fingerprintImageView.setId(1001);
          ((RelativeLayout)localObject1).addView(this.fingerprintImageView, LayoutHelper.createRelative(-2.0F, -2.0F, 0, 20, 0, 0, 20, 3, 1000));
          this.fingerprintStatusTextView = new TextView(getContext());
          this.fingerprintStatusTextView.setGravity(16);
          this.fingerprintStatusTextView.setText(LocaleController.getString("FingerprintHelp", 2131165711));
          this.fingerprintStatusTextView.setTextAppearance(16974320);
          this.fingerprintStatusTextView.setTextColor(1107296256);
          ((RelativeLayout)localObject1).addView(this.fingerprintStatusTextView);
          localObject2 = LayoutHelper.createRelative(-2, -2);
          ((RelativeLayout.LayoutParams)localObject2).setMarginStart(AndroidUtilities.dp(16.0F));
          ((RelativeLayout.LayoutParams)localObject2).addRule(8, 1001);
          ((RelativeLayout.LayoutParams)localObject2).addRule(6, 1001);
          ((RelativeLayout.LayoutParams)localObject2).addRule(17, 1001);
          this.fingerprintStatusTextView.setLayoutParams((ViewGroup.LayoutParams)localObject2);
          localObject2 = new AlertDialog.Builder(getContext());
          ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", 2131165319));
          ((AlertDialog.Builder)localObject2).setView((View)localObject1);
          ((AlertDialog.Builder)localObject2).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          ((AlertDialog.Builder)localObject2).setOnDismissListener(new DialogInterface.OnDismissListener()
          {
            public void onDismiss(DialogInterface paramDialogInterface)
            {
              if (PasscodeView.this.cancellationSignal != null)
              {
                PasscodeView.access$1002(PasscodeView.this, true);
                PasscodeView.this.cancellationSignal.a();
                PasscodeView.access$902(PasscodeView.this, null);
              }
            }
          });
          localObject1 = this.fingerprintDialog;
          if (localObject1 != null);
          try
          {
            if (this.fingerprintDialog.isShowing())
              this.fingerprintDialog.dismiss();
            this.fingerprintDialog = ((AlertDialog.Builder)localObject2).show();
            this.cancellationSignal = new d();
            this.selfCancelled = false;
            localFingerprintManagerCompat.authenticate(null, 0, this.cancellationSignal, new FingerprintManagerCompat.AuthenticationCallback()
            {
              public void onAuthenticationError(int paramInt, CharSequence paramCharSequence)
              {
                if (!PasscodeView.this.selfCancelled)
                  PasscodeView.this.showFingerprintError(paramCharSequence);
              }

              public void onAuthenticationFailed()
              {
                PasscodeView.this.showFingerprintError(LocaleController.getString("FingerprintNotRecognized", 2131165713));
              }

              public void onAuthenticationHelp(int paramInt, CharSequence paramCharSequence)
              {
                PasscodeView.this.showFingerprintError(paramCharSequence);
              }

              public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult paramAuthenticationResult)
              {
                try
                {
                  if (PasscodeView.this.fingerprintDialog.isShowing())
                    PasscodeView.this.fingerprintDialog.dismiss();
                  PasscodeView.access$1202(PasscodeView.this, null);
                  PasscodeView.this.processDone(true);
                  return;
                }
                catch (Exception paramAuthenticationResult)
                {
                  while (true)
                    FileLog.e(paramAuthenticationResult);
                }
              }
            }
            , null);
            return;
          }
          catch (Exception localException2)
          {
            while (true)
              FileLog.e(localException2);
          }
        }
        catch (Throwable localThrowable)
        {
        }
      }
    }
  }

  private void onPasscodeError()
  {
    Vibrator localVibrator = (Vibrator)getContext().getSystemService("vibrator");
    if (localVibrator != null)
      localVibrator.vibrate(200L);
    shakeTextView(2.0F, 0);
  }

  private void processDone(boolean paramBoolean)
  {
    Object localObject;
    if (!paramBoolean)
    {
      localObject = "";
      if (UserConfig.passcodeType != 0);
    }
    do
    {
      localObject = this.passwordEditText2.getString();
      while (((String)localObject).length() == 0)
      {
        onPasscodeError();
        return;
        if (UserConfig.passcodeType != 1)
          continue;
        localObject = this.passwordEditText.getText().toString();
      }
      if (!UserConfig.checkPasscode((String)localObject))
      {
        this.passwordEditText.setText("");
        this.passwordEditText2.eraseAllCharacters(true);
        onPasscodeError();
        return;
      }
      this.passwordEditText.clearFocus();
      AndroidUtilities.hideKeyboard(this.passwordEditText);
      localObject = new AnimatorSet();
      ((AnimatorSet)localObject).setDuration(200L);
      ((AnimatorSet)localObject).playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "translationY", new float[] { AndroidUtilities.dp(20.0F) }), ObjectAnimator.ofFloat(this, "alpha", new float[] { AndroidUtilities.dp(0.0F) }) });
      ((AnimatorSet)localObject).addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          PasscodeView.this.setVisibility(8);
        }
      });
      ((AnimatorSet)localObject).start();
      UserConfig.appLocked = false;
      UserConfig.saveConfig(false);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
      setOnTouchListener(null);
    }
    while (this.delegate == null);
    this.delegate.didAcceptedPassword();
  }

  private void shakeTextView(float paramFloat, int paramInt)
  {
    if (paramInt == 6)
      return;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.passcodeTextView, "translationX", new float[] { AndroidUtilities.dp(paramFloat) }) });
    localAnimatorSet.setDuration(50L);
    localAnimatorSet.addListener(new AnimatorListenerAdapter(paramInt, paramFloat)
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        paramAnimator = PasscodeView.this;
        float f;
        if (this.val$num == 5)
          f = 0.0F;
        while (true)
        {
          paramAnimator.shakeTextView(f, this.val$num + 1);
          return;
          f = -this.val$x;
        }
      }
    });
    localAnimatorSet.start();
  }

  private void showFingerprintError(CharSequence paramCharSequence)
  {
    this.fingerprintImageView.setImageResource(2130837775);
    this.fingerprintStatusTextView.setText(paramCharSequence);
    this.fingerprintStatusTextView.setTextColor(-765666);
    paramCharSequence = (Vibrator)getContext().getSystemService("vibrator");
    if (paramCharSequence != null)
      paramCharSequence.vibrate(200L);
    AndroidUtilities.shakeView(this.fingerprintStatusTextView, 2.0F, 0);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if (getVisibility() != 0)
      return;
    if (this.backgroundDrawable != null)
    {
      if ((this.backgroundDrawable instanceof ColorDrawable))
      {
        this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.backgroundDrawable.draw(paramCanvas);
        return;
      }
      float f1 = getMeasuredWidth() / this.backgroundDrawable.getIntrinsicWidth();
      float f2 = (getMeasuredHeight() + this.keyboardHeight) / this.backgroundDrawable.getIntrinsicHeight();
      if (f1 < f2)
        f1 = f2;
      while (true)
      {
        int i = (int)Math.ceil(this.backgroundDrawable.getIntrinsicWidth() * f1);
        int j = (int)Math.ceil(f1 * this.backgroundDrawable.getIntrinsicHeight());
        int k = (getMeasuredWidth() - i) / 2;
        int m = (getMeasuredHeight() - j + this.keyboardHeight) / 2;
        this.backgroundDrawable.setBounds(k, m, i + k, j + m);
        this.backgroundDrawable.draw(paramCanvas);
        return;
      }
    }
    super.onDraw(paramCanvas);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int j = 0;
    Object localObject = getRootView();
    int i = ((View)localObject).getHeight();
    int k = AndroidUtilities.statusBarHeight;
    int m = AndroidUtilities.getViewInset((View)localObject);
    getWindowVisibleDisplayFrame(this.rect);
    this.keyboardHeight = (i - k - m - (this.rect.bottom - this.rect.top));
    if ((UserConfig.passcodeType == 1) && ((AndroidUtilities.isTablet()) || (getContext().getResources().getConfiguration().orientation != 2)))
      if (this.passwordFrameLayout.getTag() == null)
        break label196;
    label196: for (i = ((Integer)this.passwordFrameLayout.getTag()).intValue(); ; i = 0)
    {
      localObject = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      k = ((FrameLayout.LayoutParams)localObject).height;
      m = this.keyboardHeight / 2;
      if (Build.VERSION.SDK_INT >= 21)
        j = AndroidUtilities.statusBarHeight;
      ((FrameLayout.LayoutParams)localObject).topMargin = (i + k - m - j);
      this.passwordFrameLayout.setLayoutParams((ViewGroup.LayoutParams)localObject);
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int j = View.MeasureSpec.getSize(paramInt1);
    int k = AndroidUtilities.displaySize.y;
    int m;
    FrameLayout.LayoutParams localLayoutParams1;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = 0;
      m = k - i;
      if ((AndroidUtilities.isTablet()) || (getContext().getResources().getConfiguration().orientation != 2))
        break label469;
      localLayoutParams1 = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      if (UserConfig.passcodeType != 0)
        break label463;
    }
    label231: int i1;
    int n;
    label386: label463: for (int i = j / 2; ; i = j)
    {
      localLayoutParams1.width = i;
      localLayoutParams1.height = AndroidUtilities.dp(140.0F);
      localLayoutParams1.topMargin = ((m - AndroidUtilities.dp(140.0F)) / 2);
      this.passwordFrameLayout.setLayoutParams(localLayoutParams1);
      localLayoutParams1 = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      localLayoutParams1.height = m;
      localLayoutParams1.leftMargin = (j / 2);
      localLayoutParams1.topMargin = (m - localLayoutParams1.height);
      localLayoutParams1.width = (j / 2);
      this.numbersFrameLayout.setLayoutParams(localLayoutParams1);
      k = (localLayoutParams1.width - AndroidUtilities.dp(50.0F) * 3) / 4;
      m = (localLayoutParams1.height - AndroidUtilities.dp(50.0F) * 4) / 5;
      j = 0;
      while (true)
      {
        if (j >= 11)
          break label779;
        if (j != 0)
          break;
        i = 10;
        i1 = i / 3;
        n = i % 3;
        if (j >= 10)
          break label693;
        Object localObject1 = (TextView)this.numberTextViews.get(j);
        Object localObject2 = (TextView)this.lettersTextViews.get(j);
        localLayoutParams1 = (FrameLayout.LayoutParams)((TextView)localObject1).getLayoutParams();
        FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)((TextView)localObject2).getLayoutParams();
        i = i1 * (AndroidUtilities.dp(50.0F) + m) + m;
        localLayoutParams1.topMargin = i;
        localLayoutParams2.topMargin = i;
        n = n * (AndroidUtilities.dp(50.0F) + k) + k;
        localLayoutParams1.leftMargin = n;
        localLayoutParams2.leftMargin = n;
        localLayoutParams2.topMargin += AndroidUtilities.dp(40.0F);
        ((TextView)localObject1).setLayoutParams(localLayoutParams1);
        ((TextView)localObject2).setLayoutParams(localLayoutParams2);
        localObject1 = (FrameLayout)this.numberFrameLayouts.get(j);
        localObject2 = (FrameLayout.LayoutParams)((FrameLayout)localObject1).getLayoutParams();
        ((FrameLayout.LayoutParams)localObject2).topMargin = (i - AndroidUtilities.dp(17.0F));
        ((FrameLayout.LayoutParams)localObject2).leftMargin = (localLayoutParams1.leftMargin - AndroidUtilities.dp(25.0F));
        ((FrameLayout)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
        j += 1;
      }
      i = AndroidUtilities.statusBarHeight;
      break;
    }
    label469: if (AndroidUtilities.isTablet())
      if (j > AndroidUtilities.dp(498.0F))
      {
        i = (j - AndroidUtilities.dp(498.0F)) / 2;
        j = AndroidUtilities.dp(498.0F);
        label506: if (m > AndroidUtilities.dp(528.0F))
        {
          n = (m - AndroidUtilities.dp(528.0F)) / 2;
          m = AndroidUtilities.dp(528.0F);
          k = j;
          j = n;
        }
      }
    while (true)
    {
      localLayoutParams1 = (FrameLayout.LayoutParams)this.passwordFrameLayout.getLayoutParams();
      localLayoutParams1.height = (m / 3);
      localLayoutParams1.width = k;
      localLayoutParams1.topMargin = j;
      localLayoutParams1.leftMargin = i;
      this.passwordFrameLayout.setTag(Integer.valueOf(j));
      this.passwordFrameLayout.setLayoutParams(localLayoutParams1);
      localLayoutParams1 = (FrameLayout.LayoutParams)this.numbersFrameLayout.getLayoutParams();
      localLayoutParams1.height = (m / 3 * 2);
      localLayoutParams1.leftMargin = i;
      localLayoutParams1.topMargin = (m - localLayoutParams1.height + j);
      localLayoutParams1.width = k;
      this.numbersFrameLayout.setLayoutParams(localLayoutParams1);
      break;
      if (j == 10)
      {
        i = 11;
        break label231;
      }
      i = j - 1;
      break label231;
      label693: localLayoutParams1 = (FrameLayout.LayoutParams)this.eraseView.getLayoutParams();
      i = (AndroidUtilities.dp(50.0F) + m) * i1 + m + AndroidUtilities.dp(8.0F);
      localLayoutParams1.topMargin = i;
      localLayoutParams1.leftMargin = ((AndroidUtilities.dp(50.0F) + k) * n + k);
      n = AndroidUtilities.dp(8.0F);
      this.eraseView.setLayoutParams(localLayoutParams1);
      i -= n;
      break label386;
      label779: super.onMeasure(paramInt1, paramInt2);
      return;
      k = j;
      j = 0;
      continue;
      i = 0;
      break label506;
      i = 0;
      k = j;
      j = 0;
    }
  }

  public void onPause()
  {
    if (this.fingerprintDialog != null);
    try
    {
      if (this.fingerprintDialog.isShowing())
        this.fingerprintDialog.dismiss();
      this.fingerprintDialog = null;
    }
    catch (Exception localException2)
    {
      try
      {
        while (true)
        {
          if ((Build.VERSION.SDK_INT >= 23) && (this.cancellationSignal != null))
          {
            this.cancellationSignal.a();
            this.cancellationSignal = null;
          }
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }

  public void onResume()
  {
    if (UserConfig.passcodeType == 1)
    {
      if (this.passwordEditText != null)
      {
        this.passwordEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.passwordEditText);
      }
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (PasscodeView.this.passwordEditText != null)
          {
            PasscodeView.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(PasscodeView.this.passwordEditText);
          }
        }
      }
      , 200L);
    }
    checkFingerprint();
  }

  public void onShow()
  {
    Object localObject = (Activity)getContext();
    if (UserConfig.passcodeType == 1)
      if (this.passwordEditText != null)
      {
        this.passwordEditText.requestFocus();
        AndroidUtilities.showKeyboard(this.passwordEditText);
      }
    while (true)
    {
      checkFingerprint();
      if (getVisibility() != 0)
        break;
      return;
      if (localObject == null)
        continue;
      localObject = ((Activity)localObject).getCurrentFocus();
      if (localObject == null)
        continue;
      ((View)localObject).clearFocus();
      AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
    }
    setAlpha(1.0F);
    setTranslationY(0.0F);
    if (ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("selectedBackground", 1000001) == 1000001)
    {
      this.backgroundFrameLayout.setBackgroundColor(-11436898);
      this.passcodeTextView.setText(LocaleController.getString("EnterYourPasscode", 2131165700));
      if (UserConfig.passcodeType != 0)
        break label271;
      this.numbersFrameLayout.setVisibility(0);
      this.passwordEditText.setVisibility(8);
      this.passwordEditText2.setVisibility(0);
      this.checkImage.setVisibility(8);
    }
    while (true)
    {
      setVisibility(0);
      this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
      this.passwordEditText.setText("");
      this.passwordEditText2.eraseAllCharacters(false);
      setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      return;
      this.backgroundDrawable = Theme.getCachedWallpaper();
      if (this.backgroundDrawable != null)
      {
        this.backgroundFrameLayout.setBackgroundColor(-1090519040);
        break;
      }
      this.backgroundFrameLayout.setBackgroundColor(-11436898);
      break;
      label271: if (UserConfig.passcodeType != 1)
        continue;
      this.passwordEditText.setFilters(new InputFilter[0]);
      this.passwordEditText.setInputType(129);
      this.numbersFrameLayout.setVisibility(8);
      this.passwordEditText.setFocusable(true);
      this.passwordEditText.setFocusableInTouchMode(true);
      this.passwordEditText.setVisibility(0);
      this.passwordEditText2.setVisibility(8);
      this.checkImage.setVisibility(0);
    }
  }

  public void setDelegate(PasscodeViewDelegate paramPasscodeViewDelegate)
  {
    this.delegate = paramPasscodeViewDelegate;
  }

  private class AnimatingTextView extends FrameLayout
  {
    private String DOT = "•";
    private ArrayList<TextView> characterTextViews = new ArrayList(4);
    private AnimatorSet currentAnimation;
    private Runnable dotRunnable;
    private ArrayList<TextView> dotTextViews = new ArrayList(4);
    private StringBuilder stringBuilder = new StringBuilder(4);

    public AnimatingTextView(Context arg2)
    {
      super();
      int i = 0;
      while (i < 4)
      {
        this$1 = new TextView(localContext);
        PasscodeView.this.setTextColor(-1);
        PasscodeView.this.setTextSize(1, 36.0F);
        PasscodeView.this.setGravity(17);
        PasscodeView.this.setAlpha(0.0F);
        PasscodeView.this.setPivotX(AndroidUtilities.dp(25.0F));
        PasscodeView.this.setPivotY(AndroidUtilities.dp(25.0F));
        addView(PasscodeView.this);
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)PasscodeView.this.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.dp(50.0F);
        localLayoutParams.height = AndroidUtilities.dp(50.0F);
        localLayoutParams.gravity = 51;
        PasscodeView.this.setLayoutParams(localLayoutParams);
        this.characterTextViews.add(PasscodeView.this);
        this$1 = new TextView(localContext);
        PasscodeView.this.setTextColor(-1);
        PasscodeView.this.setTextSize(1, 36.0F);
        PasscodeView.this.setGravity(17);
        PasscodeView.this.setAlpha(0.0F);
        PasscodeView.this.setText(this.DOT);
        PasscodeView.this.setPivotX(AndroidUtilities.dp(25.0F));
        PasscodeView.this.setPivotY(AndroidUtilities.dp(25.0F));
        addView(PasscodeView.this);
        localLayoutParams = (FrameLayout.LayoutParams)PasscodeView.this.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.dp(50.0F);
        localLayoutParams.height = AndroidUtilities.dp(50.0F);
        localLayoutParams.gravity = 51;
        PasscodeView.this.setLayoutParams(localLayoutParams);
        this.dotTextViews.add(PasscodeView.this);
        i += 1;
      }
    }

    private void eraseAllCharacters(boolean paramBoolean)
    {
      int i = 0;
      if (this.stringBuilder.length() == 0);
      while (true)
      {
        return;
        if (this.dotRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
          this.dotRunnable = null;
        }
        if (this.currentAnimation != null)
        {
          this.currentAnimation.cancel();
          this.currentAnimation = null;
        }
        this.stringBuilder.delete(0, this.stringBuilder.length());
        if (paramBoolean)
        {
          ArrayList localArrayList = new ArrayList();
          i = 0;
          while (i < 4)
          {
            TextView localTextView = (TextView)this.characterTextViews.get(i);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            }
            localTextView = (TextView)this.dotTextViews.get(i);
            if (localTextView.getAlpha() != 0.0F)
            {
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
              localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            }
            i += 1;
          }
          this.currentAnimation = new AnimatorSet();
          this.currentAnimation.setDuration(150L);
          this.currentAnimation.playTogether(localArrayList);
          this.currentAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationEnd(Animator paramAnimator)
            {
              if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnimator)))
                PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
            }
          });
          this.currentAnimation.start();
          return;
        }
        while (i < 4)
        {
          ((TextView)this.characterTextViews.get(i)).setAlpha(0.0F);
          ((TextView)this.dotTextViews.get(i)).setAlpha(0.0F);
          i += 1;
        }
      }
    }

    private int getXForTextView(int paramInt)
    {
      return (getMeasuredWidth() - this.stringBuilder.length() * AndroidUtilities.dp(30.0F)) / 2 + AndroidUtilities.dp(30.0F) * paramInt - AndroidUtilities.dp(10.0F);
    }

    public void appendCharacter(String paramString)
    {
      if (this.stringBuilder.length() == 4)
        return;
      try
      {
        performHapticFeedback(3);
        ArrayList localArrayList = new ArrayList();
        j = this.stringBuilder.length();
        this.stringBuilder.append(paramString);
        TextView localTextView = (TextView)this.characterTextViews.get(j);
        localTextView.setText(paramString);
        localTextView.setTranslationX(getXForTextView(j));
        localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F, 1.0F }));
        localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F, 1.0F }));
        localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F, 1.0F }));
        localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { AndroidUtilities.dp(20.0F), 0.0F }));
        paramString = (TextView)this.dotTextViews.get(j);
        paramString.setTranslationX(getXForTextView(j));
        paramString.setAlpha(0.0F);
        localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F, 1.0F }));
        localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F, 1.0F }));
        localArrayList.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { AndroidUtilities.dp(20.0F), 0.0F }));
        i = j + 1;
        while (i < 4)
        {
          paramString = (TextView)this.characterTextViews.get(i);
          if (paramString.getAlpha() != 0.0F)
          {
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
          }
          paramString = (TextView)this.dotTextViews.get(i);
          if (paramString.getAlpha() != 0.0F)
          {
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
          }
          i += 1;
        }
      }
      catch (Exception localException)
      {
        int j;
        while (true)
          FileLog.e(localException);
        if (this.dotRunnable != null)
          AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
        this.dotRunnable = new Runnable(j)
        {
          public void run()
          {
            if (PasscodeView.AnimatingTextView.this.dotRunnable != this)
              return;
            ArrayList localArrayList = new ArrayList();
            TextView localTextView = (TextView)PasscodeView.AnimatingTextView.this.characterTextViews.get(this.val$newPos);
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            localTextView = (TextView)PasscodeView.AnimatingTextView.this.dotTextViews.get(this.val$newPos);
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 1.0F }));
            PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, new AnimatorSet());
            PasscodeView.AnimatingTextView.this.currentAnimation.setDuration(150L);
            PasscodeView.AnimatingTextView.this.currentAnimation.playTogether(localArrayList);
            PasscodeView.AnimatingTextView.this.currentAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnimator)
              {
                if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnimator)))
                  PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
              }
            });
            PasscodeView.AnimatingTextView.this.currentAnimation.start();
          }
        };
        AndroidUtilities.runOnUIThread(this.dotRunnable, 1500L);
        int i = 0;
        while (i < j)
        {
          paramString = (TextView)this.characterTextViews.get(i);
          localException.add(ObjectAnimator.ofFloat(paramString, "translationX", new float[] { getXForTextView(i) }));
          localException.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 0.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 0.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 0.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { 0.0F }));
          paramString = (TextView)this.dotTextViews.get(i);
          localException.add(ObjectAnimator.ofFloat(paramString, "translationX", new float[] { getXForTextView(i) }));
          localException.add(ObjectAnimator.ofFloat(paramString, "scaleX", new float[] { 1.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "scaleY", new float[] { 1.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "alpha", new float[] { 1.0F }));
          localException.add(ObjectAnimator.ofFloat(paramString, "translationY", new float[] { 0.0F }));
          i += 1;
        }
        if (this.currentAnimation != null)
          this.currentAnimation.cancel();
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(150L);
        this.currentAnimation.playTogether(localException);
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnimator)))
              PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
          }
        });
        this.currentAnimation.start();
      }
    }

    public void eraseLastCharacter()
    {
      if (this.stringBuilder.length() == 0)
        return;
      try
      {
        performHapticFeedback(3);
        ArrayList localArrayList = new ArrayList();
        j = this.stringBuilder.length() - 1;
        if (j != 0)
          this.stringBuilder.deleteCharAt(j);
        i = j;
        while (i < 4)
        {
          TextView localTextView = (TextView)this.characterTextViews.get(i);
          if (localTextView.getAlpha() != 0.0F)
          {
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationX", new float[] { getXForTextView(i) }));
          }
          localTextView = (TextView)this.dotTextViews.get(i);
          if (localTextView.getAlpha() != 0.0F)
          {
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleX", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "scaleY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "alpha", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationY", new float[] { 0.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(localTextView, "translationX", new float[] { getXForTextView(i) }));
          }
          i += 1;
        }
      }
      catch (Exception localException)
      {
        int j;
        while (true)
          FileLog.e(localException);
        if (j == 0)
          this.stringBuilder.deleteCharAt(j);
        int i = 0;
        while (i < j)
        {
          localException.add(ObjectAnimator.ofFloat((TextView)this.characterTextViews.get(i), "translationX", new float[] { getXForTextView(i) }));
          localException.add(ObjectAnimator.ofFloat((TextView)this.dotTextViews.get(i), "translationX", new float[] { getXForTextView(i) }));
          i += 1;
        }
        if (this.dotRunnable != null)
        {
          AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
          this.dotRunnable = null;
        }
        if (this.currentAnimation != null)
          this.currentAnimation.cancel();
        this.currentAnimation = new AnimatorSet();
        this.currentAnimation.setDuration(150L);
        this.currentAnimation.playTogether(localException);
        this.currentAnimation.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((PasscodeView.AnimatingTextView.this.currentAnimation != null) && (PasscodeView.AnimatingTextView.this.currentAnimation.equals(paramAnimator)))
              PasscodeView.AnimatingTextView.access$302(PasscodeView.AnimatingTextView.this, null);
          }
        });
        this.currentAnimation.start();
      }
    }

    public String getString()
    {
      return this.stringBuilder.toString();
    }

    public int lenght()
    {
      return this.stringBuilder.length();
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.dotRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.dotRunnable);
        this.dotRunnable = null;
      }
      if (this.currentAnimation != null)
      {
        this.currentAnimation.cancel();
        this.currentAnimation = null;
      }
      int i = 0;
      if (i < 4)
      {
        if (i < this.stringBuilder.length())
        {
          TextView localTextView = (TextView)this.characterTextViews.get(i);
          localTextView.setAlpha(0.0F);
          localTextView.setScaleX(1.0F);
          localTextView.setScaleY(1.0F);
          localTextView.setTranslationY(0.0F);
          localTextView.setTranslationX(getXForTextView(i));
          localTextView = (TextView)this.dotTextViews.get(i);
          localTextView.setAlpha(1.0F);
          localTextView.setScaleX(1.0F);
          localTextView.setScaleY(1.0F);
          localTextView.setTranslationY(0.0F);
          localTextView.setTranslationX(getXForTextView(i));
        }
        while (true)
        {
          i += 1;
          break;
          ((TextView)this.characterTextViews.get(i)).setAlpha(0.0F);
          ((TextView)this.dotTextViews.get(i)).setAlpha(0.0F);
        }
      }
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    }
  }

  public static abstract interface PasscodeViewDelegate
  {
    public abstract void didAcceptedPassword();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PasscodeView
 * JD-Core Version:    0.6.0
 */