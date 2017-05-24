package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextUtils.TruncateAt;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
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
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_contacts_importContacts;
import org.vidogram.tgnet.TLRPC.TL_contacts_importedContacts;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPhoneContact;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.HintEditText;
import org.vidogram.ui.Components.LayoutHelper;

public class NewContactActivity extends BaseFragment
  implements AdapterView.OnItemSelectedListener
{
  private static final int done_button = 1;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private EditText codeField;
  private HashMap<String, String> codesMap = new HashMap();
  private ArrayList<String> countriesArray = new ArrayList();
  private HashMap<String, String> countriesMap = new HashMap();
  private TextView countryButton;
  private int countryState;
  private boolean donePressed;
  private ActionBarMenuItem editDoneItem;
  private AnimatorSet editDoneItemAnimation;
  private ContextProgressView editDoneItemProgress;
  private EditText firstNameField;
  private boolean ignoreOnPhoneChange;
  private boolean ignoreOnTextChange;
  private boolean ignoreSelection;
  private EditText lastNameField;
  private View lineView;
  private HintEditText phoneField;
  private HashMap<String, String> phoneFormatMap = new HashMap();
  private TextView textView;

  public NewContactActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void showEditDoneProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.editDoneItemAnimation != null)
      this.editDoneItemAnimation.cancel();
    if (!paramBoolean2)
    {
      if (paramBoolean1)
      {
        this.editDoneItem.getImageView().setScaleX(0.1F);
        this.editDoneItem.getImageView().setScaleY(0.1F);
        this.editDoneItem.getImageView().setAlpha(0.0F);
        this.editDoneItemProgress.setScaleX(1.0F);
        this.editDoneItemProgress.setScaleY(1.0F);
        this.editDoneItemProgress.setAlpha(1.0F);
        this.editDoneItem.getImageView().setVisibility(4);
        this.editDoneItemProgress.setVisibility(0);
        this.editDoneItem.setEnabled(false);
        return;
      }
      this.editDoneItemProgress.setScaleX(0.1F);
      this.editDoneItemProgress.setScaleY(0.1F);
      this.editDoneItemProgress.setAlpha(0.0F);
      this.editDoneItem.getImageView().setScaleX(1.0F);
      this.editDoneItem.getImageView().setScaleY(1.0F);
      this.editDoneItem.getImageView().setAlpha(1.0F);
      this.editDoneItem.getImageView().setVisibility(0);
      this.editDoneItemProgress.setVisibility(4);
      this.editDoneItem.setEnabled(true);
      return;
    }
    this.editDoneItemAnimation = new AnimatorSet();
    if (paramBoolean1)
    {
      this.editDoneItemProgress.setVisibility(0);
      this.editDoneItem.setEnabled(false);
      this.editDoneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[] { 1.0F }) });
    }
    while (true)
    {
      this.editDoneItemAnimation.addListener(new AnimatorListenerAdapter(paramBoolean1)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((NewContactActivity.this.editDoneItemAnimation != null) && (NewContactActivity.this.editDoneItemAnimation.equals(paramAnimator)))
            NewContactActivity.access$1802(NewContactActivity.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((NewContactActivity.this.editDoneItemAnimation != null) && (NewContactActivity.this.editDoneItemAnimation.equals(paramAnimator)))
          {
            if (!this.val$show)
              NewContactActivity.this.editDoneItemProgress.setVisibility(4);
          }
          else
            return;
          NewContactActivity.this.editDoneItem.getImageView().setVisibility(4);
        }
      });
      this.editDoneItemAnimation.setDuration(150L);
      this.editDoneItemAnimation.start();
      return;
      this.editDoneItem.getImageView().setVisibility(0);
      this.editDoneItem.setEnabled(true);
      this.editDoneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.editDoneItemProgress, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.editDoneItem.getImageView(), "alpha", new float[] { 1.0F }) });
    }
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("AddContactTitle", 2131165277));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          NewContactActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (NewContactActivity.this.donePressed));
        if (NewContactActivity.this.firstNameField.length() == 0)
        {
          localObject = (Vibrator)NewContactActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject != null)
            ((Vibrator)localObject).vibrate(200L);
          AndroidUtilities.shakeView(NewContactActivity.this.firstNameField, 2.0F, 0);
          return;
        }
        if (NewContactActivity.this.codeField.length() == 0)
        {
          localObject = (Vibrator)NewContactActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject != null)
            ((Vibrator)localObject).vibrate(200L);
          AndroidUtilities.shakeView(NewContactActivity.this.codeField, 2.0F, 0);
          return;
        }
        if (NewContactActivity.this.phoneField.length() == 0)
        {
          localObject = (Vibrator)NewContactActivity.this.getParentActivity().getSystemService("vibrator");
          if (localObject != null)
            ((Vibrator)localObject).vibrate(200L);
          AndroidUtilities.shakeView(NewContactActivity.this.phoneField, 2.0F, 0);
          return;
        }
        NewContactActivity.access$002(NewContactActivity.this, true);
        NewContactActivity.this.showEditDoneProgress(true, true);
        Object localObject = new TLRPC.TL_contacts_importContacts();
        TLRPC.TL_inputPhoneContact localTL_inputPhoneContact = new TLRPC.TL_inputPhoneContact();
        localTL_inputPhoneContact.first_name = NewContactActivity.this.firstNameField.getText().toString();
        localTL_inputPhoneContact.last_name = NewContactActivity.this.lastNameField.getText().toString();
        localTL_inputPhoneContact.phone = ("+" + NewContactActivity.this.codeField.getText().toString() + NewContactActivity.this.phoneField.getText().toString());
        ((TLRPC.TL_contacts_importContacts)localObject).contacts.add(localTL_inputPhoneContact);
        paramInt = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(localTL_inputPhoneContact, (TLRPC.TL_contacts_importContacts)localObject)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_contacts_importedContacts)paramTLObject, paramTL_error)
            {
              public void run()
              {
                NewContactActivity.access$002(NewContactActivity.this, false);
                if (this.val$res != null)
                {
                  if (!this.val$res.users.isEmpty())
                  {
                    MessagesController.getInstance().putUsers(this.val$res.users, false);
                    MessagesController.openChatOrProfileWith((TLRPC.User)this.val$res.users.get(0), null, NewContactActivity.this, 1, true);
                  }
                  do
                    return;
                  while (NewContactActivity.this.getParentActivity() == null);
                  NewContactActivity.this.showEditDoneProgress(false, true);
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(NewContactActivity.this.getParentActivity());
                  localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                  localBuilder.setMessage(LocaleController.formatString("ContactNotRegistered", 2131165573, new Object[] { ContactsController.formatName(NewContactActivity.1.1.this.val$inputPhoneContact.first_name, NewContactActivity.1.1.this.val$inputPhoneContact.last_name) }));
                  localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                  localBuilder.setPositiveButton(LocaleController.getString("Invite", 2131165843), new DialogInterface.OnClickListener()
                  {
                    public void onClick(DialogInterface paramDialogInterface, int paramInt)
                    {
                      try
                      {
                        paramDialogInterface = new Intent("android.intent.action.VIEW", Uri.fromParts("sms", NewContactActivity.1.1.this.val$inputPhoneContact.phone, null));
                        paramDialogInterface.putExtra("sms_body", LocaleController.getString("InviteText", 2131165846));
                        NewContactActivity.this.getParentActivity().startActivityForResult(paramDialogInterface, 500);
                        return;
                      }
                      catch (java.lang.Exception paramDialogInterface)
                      {
                        FileLog.e(paramDialogInterface);
                      }
                    }
                  });
                  NewContactActivity.this.showDialog(localBuilder.create());
                  return;
                }
                NewContactActivity.this.showEditDoneProgress(false, true);
                AlertsCreator.processError(this.val$error, NewContactActivity.this, NewContactActivity.1.1.this.val$req, new Object[0]);
              }
            });
          }
        }
        , 2);
        ConnectionsManager.getInstance().bindRequestToGuid(paramInt, NewContactActivity.this.classGuid);
      }
    });
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setInfo(5, "", "", false);
    this.editDoneItem = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.editDoneItemProgress = new ContextProgressView(paramContext, 1);
    this.editDoneItem.addView(this.editDoneItemProgress, LayoutHelper.createFrame(-1, -1.0F));
    this.editDoneItemProgress.setVisibility(4);
    this.fragmentView = new ScrollView(paramContext);
    Object localObject1 = new LinearLayout(paramContext);
    ((LinearLayout)localObject1).setPadding(AndroidUtilities.dp(24.0F), 0, AndroidUtilities.dp(24.0F), 0);
    ((LinearLayout)localObject1).setOrientation(1);
    ((ScrollView)this.fragmentView).addView((View)localObject1, LayoutHelper.createScroll(-1, -2, 51));
    ((LinearLayout)localObject1).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    Object localObject2 = new FrameLayout(paramContext);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2, 0.0F, 24.0F, 0.0F, 0.0F));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setImageDrawable(this.avatarDrawable);
    ((FrameLayout)localObject2).addView(this.avatarImage, LayoutHelper.createFrame(60, 60.0F, 51, 0.0F, 9.0F, 0.0F, 0.0F));
    this.firstNameField = new EditText(paramContext);
    this.firstNameField.setTextSize(1, 18.0F);
    this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.firstNameField.setMaxLines(1);
    this.firstNameField.setLines(1);
    this.firstNameField.setSingleLine(true);
    this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.firstNameField.setGravity(3);
    this.firstNameField.setInputType(49152);
    this.firstNameField.setImeOptions(5);
    this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165714));
    AndroidUtilities.clearCursorDrawable(this.firstNameField);
    ((FrameLayout)localObject2).addView(this.firstNameField, LayoutHelper.createFrame(-1, 34.0F, 51, 84.0F, 0.0F, 0.0F, 0.0F));
    this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
      {
        if (paramInt == 5)
        {
          NewContactActivity.this.lastNameField.requestFocus();
          NewContactActivity.this.lastNameField.setSelection(NewContactActivity.this.lastNameField.length());
          return true;
        }
        return false;
      }
    });
    this.firstNameField.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramEditable)
      {
        NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
        NewContactActivity.this.avatarImage.invalidate();
      }

      public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
      }

      public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
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
    this.lastNameField.setGravity(3);
    this.lastNameField.setInputType(49152);
    this.lastNameField.setImeOptions(5);
    this.lastNameField.setHint(LocaleController.getString("LastName", 2131165874));
    AndroidUtilities.clearCursorDrawable(this.lastNameField);
    ((FrameLayout)localObject2).addView(this.lastNameField, LayoutHelper.createFrame(-1, 34.0F, 51, 84.0F, 44.0F, 0.0F, 0.0F));
    this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
      {
        if (paramInt == 5)
        {
          NewContactActivity.this.phoneField.requestFocus();
          NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
          return true;
        }
        return false;
      }
    });
    this.lastNameField.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramEditable)
      {
        NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
        NewContactActivity.this.avatarImage.invalidate();
      }

      public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
      }

      public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
      }
    });
    this.countryButton = new TextView(paramContext);
    this.countryButton.setTextSize(1, 18.0F);
    this.countryButton.setPadding(AndroidUtilities.dp(6.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(6.0F), 0);
    this.countryButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.countryButton.setMaxLines(1);
    this.countryButton.setSingleLine(true);
    this.countryButton.setEllipsize(TextUtils.TruncateAt.END);
    this.countryButton.setGravity(3);
    this.countryButton.setBackgroundResource(2130838069);
    ((LinearLayout)localObject1).addView(this.countryButton, LayoutHelper.createLinear(-1, 36, 0.0F, 24.0F, 0.0F, 14.0F));
    this.countryButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        paramView = new CountrySelectActivity(true);
        paramView.setCountrySelectActivityDelegate(new CountrySelectActivity.CountrySelectActivityDelegate()
        {
          public void didSelectCountry(String paramString1, String paramString2)
          {
            NewContactActivity.this.selectCountry(paramString1);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                AndroidUtilities.showKeyboard(NewContactActivity.this.phoneField);
              }
            }
            , 300L);
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
          }
        });
        NewContactActivity.this.presentFragment(paramView);
      }
    });
    this.lineView = new View(paramContext);
    this.lineView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), 0);
    this.lineView.setBackgroundColor(Theme.getColor("windowBackgroundWhiteGrayLine"));
    ((LinearLayout)localObject1).addView(this.lineView, LayoutHelper.createLinear(-1, 1, 0.0F, -17.5F, 0.0F, 0.0F));
    localObject2 = new LinearLayout(paramContext);
    ((LinearLayout)localObject2).setOrientation(0);
    ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-1, -2, 0.0F, 20.0F, 0.0F, 0.0F));
    this.textView = new TextView(paramContext);
    this.textView.setText("+");
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.textView.setTextSize(1, 18.0F);
    ((LinearLayout)localObject2).addView(this.textView, LayoutHelper.createLinear(-2, -2));
    this.codeField = new EditText(paramContext);
    this.codeField.setInputType(3);
    this.codeField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.codeField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    AndroidUtilities.clearCursorDrawable(this.codeField);
    this.codeField.setPadding(AndroidUtilities.dp(10.0F), 0, 0, 0);
    this.codeField.setTextSize(1, 18.0F);
    this.codeField.setMaxLines(1);
    this.codeField.setGravity(19);
    this.codeField.setImeOptions(268435461);
    localObject1 = new InputFilter.LengthFilter(5);
    this.codeField.setFilters(new InputFilter[] { localObject1 });
    ((LinearLayout)localObject2).addView(this.codeField, LayoutHelper.createLinear(55, 36, -9.0F, 0.0F, 16.0F, 0.0F));
    this.codeField.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramEditable)
      {
        Object localObject3 = null;
        if (NewContactActivity.this.ignoreOnTextChange)
          return;
        NewContactActivity.access$902(NewContactActivity.this, true);
        paramEditable = b.b(NewContactActivity.this.codeField.getText().toString());
        NewContactActivity.this.codeField.setText(paramEditable);
        if (paramEditable.length() == 0)
        {
          NewContactActivity.this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165546));
          NewContactActivity.this.phoneField.setHintText(null);
          NewContactActivity.access$1102(NewContactActivity.this, 1);
          NewContactActivity.access$902(NewContactActivity.this, false);
          return;
        }
        int i;
        label124: Object localObject1;
        Object localObject2;
        label216: Object localObject4;
        if (paramEditable.length() > 4)
        {
          NewContactActivity.access$902(NewContactActivity.this, true);
          i = 4;
          if (i >= 1)
          {
            localObject1 = paramEditable.substring(0, i);
            if ((String)NewContactActivity.this.codesMap.get(localObject1) != null)
            {
              localObject2 = paramEditable.substring(i, paramEditable.length()) + NewContactActivity.this.phoneField.getText().toString();
              NewContactActivity.this.codeField.setText((CharSequence)localObject1);
              i = 1;
              paramEditable = (Editable)localObject1;
              localObject1 = localObject2;
              localObject2 = paramEditable;
              if (i == 0)
              {
                NewContactActivity.access$902(NewContactActivity.this, true);
                localObject1 = paramEditable.substring(1, paramEditable.length()) + NewContactActivity.this.phoneField.getText().toString();
                localObject4 = NewContactActivity.this.codeField;
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
          localObject2 = (String)NewContactActivity.this.codesMap.get(localObject1);
          if (localObject2 != null)
          {
            int j = NewContactActivity.this.countriesArray.indexOf(localObject2);
            if (j != -1)
            {
              NewContactActivity.access$1402(NewContactActivity.this, true);
              NewContactActivity.this.countryButton.setText((CharSequence)NewContactActivity.this.countriesArray.get(j));
              localObject4 = (String)NewContactActivity.this.phoneFormatMap.get(localObject1);
              localObject2 = NewContactActivity.this.phoneField;
              localObject1 = localObject3;
              if (localObject4 != null)
                localObject1 = ((String)localObject4).replace('X', '–');
              ((HintEditText)localObject2).setHintText((String)localObject1);
              NewContactActivity.access$1102(NewContactActivity.this, 0);
            }
          }
          while (true)
          {
            if (i == 0)
              NewContactActivity.this.codeField.setSelection(NewContactActivity.this.codeField.getText().length());
            if (paramEditable == null)
              break;
            NewContactActivity.this.phoneField.requestFocus();
            NewContactActivity.this.phoneField.setText(paramEditable);
            NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
            break;
            i -= 1;
            break label124;
            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
            NewContactActivity.this.phoneField.setHintText(null);
            NewContactActivity.access$1102(NewContactActivity.this, 2);
            continue;
            NewContactActivity.this.countryButton.setText(LocaleController.getString("WrongCountry", 2131166631));
            NewContactActivity.this.phoneField.setHintText(null);
            NewContactActivity.access$1102(NewContactActivity.this, 2);
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
    this.codeField.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
      {
        if (paramInt == 5)
        {
          NewContactActivity.this.phoneField.requestFocus();
          NewContactActivity.this.phoneField.setSelection(NewContactActivity.this.phoneField.length());
          return true;
        }
        return false;
      }
    });
    this.phoneField = new HintEditText(paramContext);
    this.phoneField.setInputType(3);
    this.phoneField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.phoneField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
    this.phoneField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
    this.phoneField.setPadding(0, 0, 0, 0);
    AndroidUtilities.clearCursorDrawable(this.phoneField);
    this.phoneField.setTextSize(1, 18.0F);
    this.phoneField.setMaxLines(1);
    this.phoneField.setGravity(19);
    this.phoneField.setImeOptions(268435462);
    ((LinearLayout)localObject2).addView(this.phoneField, LayoutHelper.createFrame(-1, 36.0F));
    this.phoneField.addTextChangedListener(new TextWatcher()
    {
      private int actionPosition;
      private int characterAction = -1;

      public void afterTextChanged(Editable paramEditable)
      {
        if (NewContactActivity.this.ignoreOnPhoneChange)
          return;
        int j = NewContactActivity.this.phoneField.getSelectionStart();
        Object localObject = NewContactActivity.this.phoneField.getText().toString();
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
        NewContactActivity.access$1602(NewContactActivity.this, true);
        paramEditable = NewContactActivity.this.phoneField.getHintText();
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
        NewContactActivity.this.phoneField.setText((CharSequence)localObject);
        if (j >= 0)
        {
          paramEditable = NewContactActivity.this.phoneField;
          if (j > NewContactActivity.this.phoneField.length())
            break label404;
        }
        while (true)
        {
          paramEditable.setSelection(j);
          NewContactActivity.this.phoneField.onTextChange();
          NewContactActivity.access$1602(NewContactActivity.this, false);
          return;
          label404: j = NewContactActivity.this.phoneField.length();
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
    this.phoneField.setOnEditorActionListener(new TextView.OnEditorActionListener()
    {
      public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
      {
        if (paramInt == 6)
        {
          NewContactActivity.this.editDoneItem.performClick();
          return true;
        }
        return false;
      }
    });
    localObject1 = new HashMap();
    try
    {
      paramContext = new BufferedReader(new InputStreamReader(paramContext.getResources().getAssets().open("countries.txt")));
      while (true)
      {
        localObject2 = paramContext.readLine();
        if (localObject2 == null)
          break;
        localObject2 = ((String)localObject2).split(";");
        this.countriesArray.add(0, localObject2[2]);
        this.countriesMap.put(localObject2[2], localObject2[0]);
        this.codesMap.put(localObject2[0], localObject2[2]);
        if (localObject2.length > 3)
          this.phoneFormatMap.put(localObject2[0], localObject2[3]);
        ((HashMap)localObject1).put(localObject2[1], localObject2[2]);
      }
    }
    catch (java.lang.Exception paramContext)
    {
      FileLog.e(paramContext);
    }
    while (true)
    {
      Collections.sort(this.countriesArray, new Comparator()
      {
        public int compare(String paramString1, String paramString2)
        {
          return paramString1.compareTo(paramString2);
        }
      });
      try
      {
        paramContext = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        if (paramContext != null)
        {
          paramContext = paramContext.getSimCountryIso().toUpperCase();
          if (paramContext != null)
          {
            paramContext = (String)((HashMap)localObject1).get(paramContext);
            if ((paramContext != null) && (this.countriesArray.indexOf(paramContext) != -1))
            {
              this.codeField.setText((CharSequence)this.countriesMap.get(paramContext));
              this.countryState = 0;
            }
          }
          if (this.codeField.length() == 0)
          {
            this.countryButton.setText(LocaleController.getString("ChooseCountry", 2131165546));
            this.phoneField.setHintText(null);
            this.countryState = 1;
          }
          if ((getArguments() != null) && (getArguments().getString("phone") != null))
            this.phoneField.setText(getArguments().getString("phone"));
          return this.fragmentView;
          paramContext.close();
        }
      }
      catch (java.lang.Exception paramContext)
      {
        while (true)
        {
          FileLog.e(paramContext);
          paramContext = null;
        }
      }
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    14 local14 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        if (NewContactActivity.this.avatarImage != null)
        {
          NewContactActivity.this.avatarDrawable.setInfo(5, NewContactActivity.this.firstNameField.getText().toString(), NewContactActivity.this.lastNameField.getText().toString(), false);
          NewContactActivity.this.avatarImage.invalidate();
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.codeField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.codeField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.phoneField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhiteGrayLine"), new ThemeDescription(this.countryButton, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, "contextProgressInner2"), new ThemeDescription(this.editDoneItemProgress, 0, null, null, null, null, "contextProgressOuter2"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local14, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local14, "avatar_backgroundPink") };
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

  public void onNothingSelected(AdapterView<?> paramAdapterView)
  {
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

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.NewContactActivity
 * JD-Core Version:    0.6.0
 */