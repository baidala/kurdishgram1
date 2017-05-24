package org.vidogram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.security.SecureRandom;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.fingerprint.FingerprintManagerCompat;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.NumberPicker;
import org.vidogram.ui.Components.NumberPicker.Formatter;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class PasscodeActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private static final int password_item = 3;
  private static final int pin_item = 2;
  private int autoLockDetailRow;
  private int autoLockRow;
  private int captureDetailRow;
  private int captureRow;
  private int changePasscodeRow;
  private int currentPasswordType = 0;
  private TextView dropDown;
  private ActionBarMenuItem dropDownContainer;
  private Drawable dropDownDrawable;
  private int fingerprintRow;
  private String firstPassword;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int passcodeDetailRow;
  private int passcodeRow;
  private int passcodeSetStep = 0;
  private EditText passwordEditText;
  private int rowCount;
  private TextView titleTextView;
  private int type;

  public PasscodeActivity(int paramInt)
  {
    this.type = paramInt;
  }

  private void fixLayoutInternal()
  {
    FrameLayout.LayoutParams localLayoutParams;
    if (this.dropDownContainer != null)
      if (!AndroidUtilities.isTablet())
      {
        localLayoutParams = (FrameLayout.LayoutParams)this.dropDownContainer.getLayoutParams();
        if (Build.VERSION.SDK_INT < 21)
          break label81;
      }
    label81: for (int i = AndroidUtilities.statusBarHeight; ; i = 0)
    {
      localLayoutParams.topMargin = i;
      this.dropDownContainer.setLayoutParams(localLayoutParams);
      if ((AndroidUtilities.isTablet()) || (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation != 2))
        break;
      this.dropDown.setTextSize(18.0F);
      return;
    }
    this.dropDown.setTextSize(20.0F);
  }

  private void onPasscodeError()
  {
    if (getParentActivity() == null)
      return;
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null)
      localVibrator.vibrate(200L);
    AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
  }

  private void processDone()
  {
    if (this.passwordEditText.getText().length() == 0)
      onPasscodeError();
    do
    {
      return;
      if (this.type != 1)
        continue;
      if (!this.firstPassword.equals(this.passwordEditText.getText().toString()))
        try
        {
          Toast.makeText(getParentActivity(), LocaleController.getString("PasscodeDoNotMatch", 2131166194), 0).show();
          AndroidUtilities.shakeView(this.titleTextView, 2.0F, 0);
          this.passwordEditText.setText("");
          return;
        }
        catch (Exception localException1)
        {
          while (true)
            FileLog.e(localException1);
        }
      try
      {
        UserConfig.passcodeSalt = new byte[16];
        Utilities.random.nextBytes(UserConfig.passcodeSalt);
        byte[] arrayOfByte1 = this.firstPassword.getBytes("UTF-8");
        byte[] arrayOfByte2 = new byte[arrayOfByte1.length + 32];
        System.arraycopy(UserConfig.passcodeSalt, 0, arrayOfByte2, 0, 16);
        System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 16, arrayOfByte1.length);
        System.arraycopy(UserConfig.passcodeSalt, 0, arrayOfByte2, arrayOfByte1.length + 16, 16);
        UserConfig.passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte2, 0, arrayOfByte2.length));
        UserConfig.passcodeType = this.currentPasswordType;
        UserConfig.saveConfig(false);
        finishFragment();
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
        this.passwordEditText.clearFocus();
        AndroidUtilities.hideKeyboard(this.passwordEditText);
        return;
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
    while (this.type != 2);
    if (!UserConfig.checkPasscode(this.passwordEditText.getText().toString()))
    {
      this.passwordEditText.setText("");
      onPasscodeError();
      return;
    }
    this.passwordEditText.clearFocus();
    AndroidUtilities.hideKeyboard(this.passwordEditText);
    presentFragment(new PasscodeActivity(0), true);
  }

  private void processNext()
  {
    if ((this.passwordEditText.getText().length() == 0) || ((this.currentPasswordType == 0) && (this.passwordEditText.getText().length() != 4)))
    {
      onPasscodeError();
      return;
    }
    if (this.currentPasswordType == 0)
      this.actionBar.setTitle(LocaleController.getString("PasscodePIN", 2131166195));
    while (true)
    {
      this.dropDownContainer.setVisibility(8);
      this.titleTextView.setText(LocaleController.getString("ReEnterYourPasscode", 2131166314));
      this.firstPassword = this.passwordEditText.getText().toString();
      this.passwordEditText.setText("");
      this.passcodeSetStep = 1;
      return;
      this.actionBar.setTitle(LocaleController.getString("PasscodePassword", 2131166196));
    }
  }

  private void updateDropDownTextView()
  {
    if (this.dropDown != null)
    {
      if (this.currentPasswordType == 0)
        this.dropDown.setText(LocaleController.getString("PasscodePIN", 2131166195));
    }
    else
    {
      if (((this.type != 1) || (this.currentPasswordType != 0)) && ((this.type != 2) || (UserConfig.passcodeType != 0)))
        break label142;
      InputFilter.LengthFilter localLengthFilter = new InputFilter.LengthFilter(4);
      this.passwordEditText.setFilters(new InputFilter[] { localLengthFilter });
      this.passwordEditText.setInputType(3);
      this.passwordEditText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
    }
    while (true)
    {
      this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
      return;
      if (this.currentPasswordType != 1)
        break;
      this.dropDown.setText(LocaleController.getString("PasscodePassword", 2131166196));
      break;
      label142: if (((this.type != 1) || (this.currentPasswordType != 1)) && ((this.type != 2) || (UserConfig.passcodeType != 1)))
        continue;
      this.passwordEditText.setFilters(new InputFilter[0]);
      this.passwordEditText.setKeyListener(null);
      this.passwordEditText.setInputType(129);
    }
  }

  private void updateRows()
  {
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.changePasscodeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.passcodeDetailRow = i;
    if (UserConfig.passcodeHash.length() > 0)
      try
      {
        if ((Build.VERSION.SDK_INT >= 23) && (FingerprintManagerCompat.from(ApplicationLoader.applicationContext).isHardwareDetected()))
        {
          i = this.rowCount;
          this.rowCount = (i + 1);
          this.fingerprintRow = i;
        }
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.autoLockRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.autoLockDetailRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.captureRow = i;
        i = this.rowCount;
        this.rowCount = (i + 1);
        this.captureDetailRow = i;
        return;
      }
      catch (Throwable localThrowable)
      {
        while (true)
          FileLog.e(localThrowable);
      }
    this.captureRow = -1;
    this.captureDetailRow = -1;
    this.fingerprintRow = -1;
    this.autoLockRow = -1;
    this.autoLockDetailRow = -1;
  }

  public View createView(Context paramContext)
  {
    if (this.type != 3)
      this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          PasscodeActivity.this.finishFragment();
        do
        {
          while (true)
          {
            return;
            if (paramInt != 1)
              break;
            if (PasscodeActivity.this.passcodeSetStep == 0)
            {
              PasscodeActivity.this.processNext();
              return;
            }
            if (PasscodeActivity.this.passcodeSetStep != 1)
              continue;
            PasscodeActivity.this.processDone();
            return;
          }
          if (paramInt != 2)
            continue;
          PasscodeActivity.access$302(PasscodeActivity.this, 0);
          PasscodeActivity.this.updateDropDownTextView();
          return;
        }
        while (paramInt != 3);
        PasscodeActivity.access$302(PasscodeActivity.this, 1);
        PasscodeActivity.this.updateDropDownTextView();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    Object localObject1 = (FrameLayout)this.fragmentView;
    label292: float f;
    if (this.type != 0)
    {
      Object localObject2 = this.actionBar.createMenu();
      ((ActionBarMenu)localObject2).addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      this.titleTextView = new TextView(paramContext);
      this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText6"));
      if (this.type == 1)
        if (UserConfig.passcodeHash.length() != 0)
        {
          this.titleTextView.setText(LocaleController.getString("EnterNewPasscode", 2131165698));
          this.titleTextView.setTextSize(1, 18.0F);
          this.titleTextView.setGravity(1);
          ((FrameLayout)localObject1).addView(this.titleTextView, LayoutHelper.createFrame(-2, -2.0F, 1, 0.0F, 38.0F, 0.0F, 0.0F));
          this.passwordEditText = new EditText(paramContext);
          this.passwordEditText.setTextSize(1, 20.0F);
          this.passwordEditText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          this.passwordEditText.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
          this.passwordEditText.setMaxLines(1);
          this.passwordEditText.setLines(1);
          this.passwordEditText.setGravity(1);
          this.passwordEditText.setSingleLine(true);
          if (this.type != 1)
            break label761;
          this.passcodeSetStep = 0;
          this.passwordEditText.setImeOptions(5);
          this.passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
          this.passwordEditText.setTypeface(Typeface.DEFAULT);
          AndroidUtilities.clearCursorDrawable(this.passwordEditText);
          ((FrameLayout)localObject1).addView(this.passwordEditText, LayoutHelper.createFrame(-1, 36.0F, 51, 40.0F, 90.0F, 40.0F, 0.0F));
          this.passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
          {
            public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
            {
              if (PasscodeActivity.this.passcodeSetStep == 0)
              {
                PasscodeActivity.this.processNext();
                return true;
              }
              if (PasscodeActivity.this.passcodeSetStep == 1)
              {
                PasscodeActivity.this.processDone();
                return true;
              }
              return false;
            }
          });
          this.passwordEditText.addTextChangedListener(new TextWatcher()
          {
            public void afterTextChanged(Editable paramEditable)
            {
              if (PasscodeActivity.this.passwordEditText.length() == 4)
              {
                if ((PasscodeActivity.this.type != 2) || (UserConfig.passcodeType != 0))
                  break label39;
                PasscodeActivity.this.processDone();
              }
              label39: 
              do
              {
                do
                  return;
                while ((PasscodeActivity.this.type != 1) || (PasscodeActivity.this.currentPasswordType != 0));
                if (PasscodeActivity.this.passcodeSetStep != 0)
                  continue;
                PasscodeActivity.this.processNext();
                return;
              }
              while (PasscodeActivity.this.passcodeSetStep != 1);
              PasscodeActivity.this.processDone();
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
          if (this.type != 1)
            break label785;
          ((FrameLayout)localObject1).setTag("windowBackgroundWhite");
          this.dropDownContainer = new ActionBarMenuItem(paramContext, (ActionBarMenu)localObject2, 0, 0);
          this.dropDownContainer.setSubMenuOpenSide(1);
          this.dropDownContainer.addSubItem(2, LocaleController.getString("PasscodePIN", 2131166195));
          this.dropDownContainer.addSubItem(3, LocaleController.getString("PasscodePassword", 2131166196));
          localObject1 = this.actionBar;
          localObject2 = this.dropDownContainer;
          if (!AndroidUtilities.isTablet())
            break label778;
          f = 64.0F;
          label487: ((ActionBar)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -1.0F, 51, f, 0.0F, 40.0F, 0.0F));
          this.dropDownContainer.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              PasscodeActivity.this.dropDownContainer.toggleSubMenu();
            }
          });
          this.dropDown = new TextView(paramContext);
          this.dropDown.setGravity(3);
          this.dropDown.setSingleLine(true);
          this.dropDown.setLines(1);
          this.dropDown.setMaxLines(1);
          this.dropDown.setEllipsize(TextUtils.TruncateAt.END);
          this.dropDown.setTextColor(Theme.getColor("actionBarDefaultTitle"));
          this.dropDown.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          this.dropDownDrawable = paramContext.getResources().getDrawable(2130837748).mutate();
          this.dropDownDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("actionBarDefaultTitle"), PorterDuff.Mode.MULTIPLY));
          this.dropDown.setCompoundDrawablesWithIntrinsicBounds(null, null, this.dropDownDrawable, null);
          this.dropDown.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          this.dropDown.setPadding(0, 0, AndroidUtilities.dp(10.0F), 0);
          this.dropDownContainer.addView(this.dropDown, LayoutHelper.createFrame(-2, -2.0F, 16, 16.0F, 0.0F, 0.0F, 1.0F));
          label714: updateDropDownTextView();
        }
    }
    while (true)
    {
      return this.fragmentView;
      this.titleTextView.setText(LocaleController.getString("EnterNewFirstPasscode", 2131165697));
      break;
      this.titleTextView.setText(LocaleController.getString("EnterCurrentPasscode", 2131165694));
      break;
      label761: this.passcodeSetStep = 1;
      this.passwordEditText.setImeOptions(6);
      break label292;
      label778: f = 56.0F;
      break label487;
      label785: this.actionBar.setTitle(LocaleController.getString("Passcode", 2131166193));
      break label714;
      this.actionBar.setTitle(LocaleController.getString("Passcode", 2131166193));
      ((FrameLayout)localObject1).setTag("windowBackgroundGray");
      ((FrameLayout)localObject1).setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      this.listView = new RecyclerListView(paramContext);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      });
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      ((FrameLayout)localObject1).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
      localObject1 = this.listView;
      paramContext = new ListAdapter(paramContext);
      this.listAdapter = paramContext;
      ((RecyclerListView)localObject1).setAdapter(paramContext);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          boolean bool2 = true;
          boolean bool1 = true;
          if (!paramView.isEnabled());
          label171: 
          do
          {
            while (true)
            {
              return;
              if (paramInt == PasscodeActivity.this.changePasscodeRow)
              {
                PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                return;
              }
              if (paramInt == PasscodeActivity.this.passcodeRow)
              {
                paramView = (TextCheckCell)paramView;
                if (UserConfig.passcodeHash.length() != 0)
                {
                  UserConfig.passcodeHash = "";
                  UserConfig.appLocked = false;
                  UserConfig.saveConfig(false);
                  int i = PasscodeActivity.this.listView.getChildCount();
                  paramInt = 0;
                  if (paramInt < i)
                  {
                    localObject = PasscodeActivity.this.listView.getChildAt(paramInt);
                    if ((localObject instanceof TextSettingsCell))
                      ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
                  }
                  else
                  {
                    if (UserConfig.passcodeHash.length() == 0)
                      break label171;
                  }
                  for (bool1 = true; ; bool1 = false)
                  {
                    paramView.setChecked(bool1);
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
                    return;
                    paramInt += 1;
                    break;
                  }
                }
                PasscodeActivity.this.presentFragment(new PasscodeActivity(1));
                return;
              }
              if (paramInt != PasscodeActivity.this.autoLockRow)
                break;
              if (PasscodeActivity.this.getParentActivity() == null)
                continue;
              paramView = new AlertDialog.Builder(PasscodeActivity.this.getParentActivity());
              paramView.setTitle(LocaleController.getString("AutoLock", 2131165373));
              Object localObject = new NumberPicker(PasscodeActivity.this.getParentActivity());
              ((NumberPicker)localObject).setMinValue(0);
              ((NumberPicker)localObject).setMaxValue(4);
              if (UserConfig.autoLockIn == 0)
                ((NumberPicker)localObject).setValue(0);
              while (true)
              {
                ((NumberPicker)localObject).setFormatter(new NumberPicker.Formatter()
                {
                  public String format(int paramInt)
                  {
                    if (paramInt == 0)
                      return LocaleController.getString("AutoLockDisabled", 2131165374);
                    if (paramInt == 1)
                      return LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Minutes", 1) });
                    if (paramInt == 2)
                      return LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Minutes", 5) });
                    if (paramInt == 3)
                      return LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Hours", 1) });
                    if (paramInt == 4)
                      return LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Hours", 5) });
                    return "";
                  }
                });
                paramView.setView((View)localObject);
                paramView.setNegativeButton(LocaleController.getString("Done", 2131165661), new DialogInterface.OnClickListener((NumberPicker)localObject, paramInt)
                {
                  public void onClick(DialogInterface paramDialogInterface, int paramInt)
                  {
                    paramInt = this.val$numberPicker.getValue();
                    if (paramInt == 0)
                      UserConfig.autoLockIn = 0;
                    while (true)
                    {
                      PasscodeActivity.this.listAdapter.notifyItemChanged(this.val$position);
                      UserConfig.saveConfig(false);
                      return;
                      if (paramInt == 1)
                      {
                        UserConfig.autoLockIn = 60;
                        continue;
                      }
                      if (paramInt == 2)
                      {
                        UserConfig.autoLockIn = 300;
                        continue;
                      }
                      if (paramInt == 3)
                      {
                        UserConfig.autoLockIn = 3600;
                        continue;
                      }
                      if (paramInt != 4)
                        continue;
                      UserConfig.autoLockIn = 18000;
                    }
                  }
                });
                PasscodeActivity.this.showDialog(paramView.create());
                return;
                if (UserConfig.autoLockIn == 60)
                {
                  ((NumberPicker)localObject).setValue(1);
                  continue;
                }
                if (UserConfig.autoLockIn == 300)
                {
                  ((NumberPicker)localObject).setValue(2);
                  continue;
                }
                if (UserConfig.autoLockIn == 3600)
                {
                  ((NumberPicker)localObject).setValue(3);
                  continue;
                }
                if (UserConfig.autoLockIn != 18000)
                  continue;
                ((NumberPicker)localObject).setValue(4);
              }
            }
            if (paramInt != PasscodeActivity.this.fingerprintRow)
              continue;
            if (!UserConfig.useFingerprint);
            while (true)
            {
              UserConfig.useFingerprint = bool1;
              UserConfig.saveConfig(false);
              ((TextCheckCell)paramView).setChecked(UserConfig.useFingerprint);
              return;
              bool1 = false;
            }
          }
          while (paramInt != PasscodeActivity.this.captureRow);
          if (!UserConfig.allowScreenCapture);
          for (bool1 = bool2; ; bool1 = false)
          {
            UserConfig.allowScreenCapture = bool1;
            UserConfig.saveConfig(false);
            ((TextCheckCell)paramView).setChecked(UserConfig.allowScreenCapture);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didSetPasscode, new Object[0]);
            return;
          }
        }
      });
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.didSetPasscode) && (this.type == 0))
    {
      updateRows();
      if (this.listAdapter != null)
        this.listAdapter.notifyDataSetChanged();
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextCheckCell.class, TextSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.titleTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText6"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.passwordEditText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.dropDown, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.dropDown, 0, null, null, new Drawable[] { this.dropDownDrawable }, null, "actionBarDefaultTitle"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText7"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null)
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PasscodeActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          PasscodeActivity.this.fixLayoutInternal();
          return true;
        }
      });
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    updateRows();
    if (this.type == 0)
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetPasscode);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.type == 0)
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetPasscode);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    if (this.type != 0)
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (PasscodeActivity.this.passwordEditText != null)
          {
            PasscodeActivity.this.passwordEditText.requestFocus();
            AndroidUtilities.showKeyboard(PasscodeActivity.this.passwordEditText);
          }
        }
      }
      , 200L);
    fixLayoutInternal();
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.type != 0))
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
      return PasscodeActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == PasscodeActivity.this.passcodeRow) || (paramInt == PasscodeActivity.this.fingerprintRow) || (paramInt == PasscodeActivity.this.captureRow));
      do
      {
        return 0;
        if ((paramInt == PasscodeActivity.this.changePasscodeRow) || (paramInt == PasscodeActivity.this.autoLockRow))
          return 1;
      }
      while ((paramInt != PasscodeActivity.this.passcodeDetailRow) && (paramInt != PasscodeActivity.this.autoLockDetailRow) && (paramInt != PasscodeActivity.this.captureDetailRow));
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == PasscodeActivity.this.passcodeRow) || (i == PasscodeActivity.this.fingerprintRow) || (i == PasscodeActivity.this.autoLockRow) || (i == PasscodeActivity.this.captureRow) || ((UserConfig.passcodeHash.length() != 0) && (i == PasscodeActivity.this.changePasscodeRow));
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = false;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
      }
      do
      {
        Object localObject;
        do
        {
          do
          {
            return;
            paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
            if (paramInt == PasscodeActivity.this.passcodeRow)
            {
              localObject = LocaleController.getString("Passcode", 2131166193);
              if (UserConfig.passcodeHash.length() > 0)
                bool = true;
              paramViewHolder.setTextAndCheck((String)localObject, bool, true);
              return;
            }
            if (paramInt != PasscodeActivity.this.fingerprintRow)
              continue;
            paramViewHolder.setTextAndCheck(LocaleController.getString("UnlockFingerprint", 2131166534), UserConfig.useFingerprint, true);
            return;
          }
          while (paramInt != PasscodeActivity.this.captureRow);
          paramViewHolder.setTextAndCheck(LocaleController.getString("ScreenCapture", 2131166378), UserConfig.allowScreenCapture, false);
          return;
          localObject = (TextSettingsCell)paramViewHolder.itemView;
          if (paramInt != PasscodeActivity.this.changePasscodeRow)
            continue;
          ((TextSettingsCell)localObject).setText(LocaleController.getString("ChangePasscode", 2131165434), false);
          if (UserConfig.passcodeHash.length() == 0)
          {
            ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteGrayText7");
            ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteGrayText7"));
            return;
          }
          ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteBlackText");
          ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          return;
        }
        while (paramInt != PasscodeActivity.this.autoLockRow);
        if (UserConfig.autoLockIn == 0)
          paramViewHolder = LocaleController.formatString("AutoLockDisabled", 2131165374, new Object[0]);
        while (true)
        {
          ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("AutoLock", 2131165373), paramViewHolder, true);
          ((TextSettingsCell)localObject).setTag("windowBackgroundWhiteBlackText");
          ((TextSettingsCell)localObject).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
          return;
          if (UserConfig.autoLockIn < 3600)
          {
            paramViewHolder = LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Minutes", UserConfig.autoLockIn / 60) });
            continue;
          }
          if (UserConfig.autoLockIn < 86400)
          {
            paramViewHolder = LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Hours", (int)Math.ceil(UserConfig.autoLockIn / 60.0F / 60.0F)) });
            continue;
          }
          paramViewHolder = LocaleController.formatString("AutoLockInTime", 2131165375, new Object[] { LocaleController.formatPluralString("Days", (int)Math.ceil(UserConfig.autoLockIn / 60.0F / 60.0F / 24.0F)) });
        }
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if (paramInt == PasscodeActivity.this.passcodeDetailRow)
        {
          paramViewHolder.setText(LocaleController.getString("ChangePasscodeInfo", 2131165436));
          if (PasscodeActivity.this.autoLockDetailRow != -1)
          {
            paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
            return;
          }
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        if (paramInt != PasscodeActivity.this.autoLockDetailRow)
          continue;
        paramViewHolder.setText(LocaleController.getString("AutoLockInfo", 2131165376));
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
        return;
      }
      while (paramInt != PasscodeActivity.this.captureDetailRow);
      paramViewHolder.setText(LocaleController.getString("ScreenCaptureInfo", 2131166379));
      paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      case 0:
      case 1:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextCheckCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PasscodeActivity
 * JD-Core Version:    0.6.0
 */