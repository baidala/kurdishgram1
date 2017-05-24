package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
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
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONObject;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.InputPaymentCredentials;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.TL_account_getPassword;
import org.vidogram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.vidogram.tgnet.TLRPC.TL_account_noPassword;
import org.vidogram.tgnet.TLRPC.TL_account_password;
import org.vidogram.tgnet.TLRPC.TL_account_tmpPassword;
import org.vidogram.tgnet.TLRPC.TL_dataJSON;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPaymentCredentials;
import org.vidogram.tgnet.TLRPC.TL_inputPaymentCredentialsSaved;
import org.vidogram.tgnet.TLRPC.TL_invoice;
import org.vidogram.tgnet.TLRPC.TL_labeledPrice;
import org.vidogram.tgnet.TLRPC.TL_paymentRequestedInfo;
import org.vidogram.tgnet.TLRPC.TL_paymentSavedCredentialsCard;
import org.vidogram.tgnet.TLRPC.TL_payments_clearSavedInfo;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentForm;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentResult;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentVerficationNeeded;
import org.vidogram.tgnet.TLRPC.TL_payments_sendPaymentForm;
import org.vidogram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
import org.vidogram.tgnet.TLRPC.TL_payments_validatedRequestedInfo;
import org.vidogram.tgnet.TLRPC.TL_postAddress;
import org.vidogram.tgnet.TLRPC.TL_shippingOption;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.PaymentInfoCell;
import org.vidogram.ui.Cells.RadioCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextDetailSettingsCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextPriceCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.HintEditText;

public class PaymentFormActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int FIELDS_COUNT_ADDRESS = 10;
  private static final int FIELDS_COUNT_CARD = 6;
  private static final int FIELDS_COUNT_SAVEDCARD = 2;
  private static final int FIELD_CARD = 0;
  private static final int FIELD_CARDNAME = 2;
  private static final int FIELD_CARD_COUNTRY = 4;
  private static final int FIELD_CARD_POSTCODE = 5;
  private static final int FIELD_CITY = 2;
  private static final int FIELD_COUNTRY = 4;
  private static final int FIELD_CVV = 3;
  private static final int FIELD_EMAIL = 7;
  private static final int FIELD_EXPIRE_DATE = 1;
  private static final int FIELD_NAME = 6;
  private static final int FIELD_PHONE = 9;
  private static final int FIELD_PHONECODE = 8;
  private static final int FIELD_POSTCODE = 5;
  private static final int FIELD_SAVEDCARD = 0;
  private static final int FIELD_SAVEDPASSWORD = 1;
  private static final int FIELD_STATE = 3;
  private static final int FIELD_STREET1 = 0;
  private static final int FIELD_STREET2 = 1;
  private static final int done_button = 1;
  private TextInfoPrivacyCell[] bottomCell = new TextInfoPrivacyCell[2];
  private FrameLayout bottomLayout;
  private boolean canceled;
  private String cardName;
  private TextCheckCell checkCell1;
  private HashMap<String, String> codesMap = new HashMap();
  private ArrayList<String> countriesArray = new ArrayList();
  private HashMap<String, String> countriesMap = new HashMap();
  private String countryName;
  private String currentBotName;
  private String currentItemName;
  private int currentStep;
  private PaymentFormActivityDelegate delegate;
  private TextDetailSettingsCell[] detailSettingsCell = new TextDetailSettingsCell[6];
  private ArrayList<View> dividers = new ArrayList();
  private ActionBarMenuItem doneItem;
  private AnimatorSet doneItemAnimation;
  private boolean donePressed;
  private HeaderCell[] headerCell = new HeaderCell[3];
  private boolean ignoreOnCardChange;
  private boolean ignoreOnPhoneChange;
  private boolean ignoreOnTextChange;
  private EditText[] inputFields;
  private LinearLayout linearLayout2;
  private MessageObject messageObject;
  private boolean need_card_country;
  private boolean need_card_name;
  private boolean need_card_postcode;
  private boolean passwordOk;
  private TextView payTextView;
  private TLRPC.TL_payments_paymentForm paymentForm;
  private PaymentInfoCell paymentInfoCell;
  private String paymentJson;
  private HashMap<String, String> phoneFormatMap = new HashMap();
  private ContextProgressView progressView;
  private RadioCell[] radioCells;
  private TLRPC.TL_payments_validatedRequestedInfo requestedInfo;
  private boolean saveCardInfo;
  private boolean saveShippingInfo;
  private ScrollView scrollView;
  private ShadowSectionCell[] sectionCell = new ShadowSectionCell[3];
  private TextSettingsCell settingsCell1;
  private TLRPC.TL_shippingOption shippingOption;
  private String stripeApiKey;
  private TextView textView;
  private TLRPC.TL_payments_validateRequestedInfo validateRequest;
  private WebView webView;
  private boolean webviewLoading;

  public PaymentFormActivity(MessageObject paramMessageObject, TLRPC.TL_payments_paymentReceipt paramTL_payments_paymentReceipt)
  {
    this.currentStep = 5;
    this.paymentForm = new TLRPC.TL_payments_paymentForm();
    this.paymentForm.bot_id = paramTL_payments_paymentReceipt.bot_id;
    this.paymentForm.invoice = paramTL_payments_paymentReceipt.invoice;
    this.paymentForm.provider_id = paramTL_payments_paymentReceipt.provider_id;
    this.paymentForm.users = paramTL_payments_paymentReceipt.users;
    this.shippingOption = paramTL_payments_paymentReceipt.shipping;
    this.messageObject = paramMessageObject;
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramTL_payments_paymentReceipt.bot_id));
    if (localUser != null);
    for (this.currentBotName = localUser.first_name; ; this.currentBotName = "")
    {
      this.currentItemName = paramMessageObject.messageOwner.media.title;
      if (paramTL_payments_paymentReceipt.info != null)
      {
        this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
        this.validateRequest.info = paramTL_payments_paymentReceipt.info;
      }
      this.cardName = paramTL_payments_paymentReceipt.credentials_title;
      return;
    }
  }

  public PaymentFormActivity(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject)
  {
    if ((paramTL_payments_paymentForm.invoice.shipping_address_requested) || (paramTL_payments_paymentForm.invoice.email_requested) || (paramTL_payments_paymentForm.invoice.name_requested) || (paramTL_payments_paymentForm.invoice.phone_requested))
      i = 0;
    while (true)
    {
      init(paramTL_payments_paymentForm, paramMessageObject, i, null, null, null, null, null, false);
      return;
      if (paramTL_payments_paymentForm.saved_credentials != null)
      {
        if ((UserConfig.tmpPassword != null) && (UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60))
        {
          UserConfig.tmpPassword = null;
          UserConfig.saveConfig(false);
        }
        if (UserConfig.tmpPassword == null)
          continue;
        i = 4;
        continue;
      }
      i = 2;
    }
  }

  private PaymentFormActivity(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject, int paramInt, TLRPC.TL_payments_validatedRequestedInfo paramTL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption paramTL_shippingOption, String paramString1, String paramString2, TLRPC.TL_payments_validateRequestedInfo paramTL_payments_validateRequestedInfo, boolean paramBoolean)
  {
    init(paramTL_payments_paymentForm, paramMessageObject, paramInt, paramTL_payments_validatedRequestedInfo, paramTL_shippingOption, paramString1, paramString2, paramTL_payments_validateRequestedInfo, paramBoolean);
  }

  private void checkPassword()
  {
    if ((UserConfig.tmpPassword != null) && (UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60))
    {
      UserConfig.tmpPassword = null;
      UserConfig.saveConfig(false);
    }
    if (UserConfig.tmpPassword != null)
    {
      sendData();
      return;
    }
    if (this.inputFields[1].length() == 0)
    {
      localObject = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
      if (localObject != null)
        ((Vibrator)localObject).vibrate(200L);
      AndroidUtilities.shakeView(this.inputFields[1], 2.0F, 0);
      return;
    }
    Object localObject = this.inputFields[1].getText().toString();
    showEditDoneProgress(true);
    setDonePressed(true);
    TLRPC.TL_account_getPassword localTL_account_getPassword = new TLRPC.TL_account_getPassword();
    ConnectionsManager.getInstance().sendRequest(localTL_account_getPassword, new RequestDelegate((String)localObject, localTL_account_getPassword)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if (this.val$error == null)
            {
              if ((this.val$response instanceof TLRPC.TL_account_noPassword))
              {
                PaymentFormActivity.access$2902(PaymentFormActivity.this, false);
                PaymentFormActivity.this.goToNextStep();
                return;
              }
              TLRPC.TL_account_password localTL_account_password = (TLRPC.TL_account_password)this.val$response;
              Object localObject = null;
              try
              {
                byte[] arrayOfByte = PaymentFormActivity.25.this.val$password.getBytes("UTF-8");
                localObject = arrayOfByte;
                arrayOfByte = new byte[localTL_account_password.current_salt.length * 2 + localObject.length];
                System.arraycopy(localTL_account_password.current_salt, 0, arrayOfByte, 0, localTL_account_password.current_salt.length);
                System.arraycopy(localObject, 0, arrayOfByte, localTL_account_password.current_salt.length, localObject.length);
                System.arraycopy(localTL_account_password.current_salt, 0, arrayOfByte, arrayOfByte.length - localTL_account_password.current_salt.length, localTL_account_password.current_salt.length);
                localObject = new TLRPC.TL_account_getTmpPassword();
                ((TLRPC.TL_account_getTmpPassword)localObject).password_hash = Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length);
                ((TLRPC.TL_account_getTmpPassword)localObject).period = 1800;
                ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate((TLRPC.TL_account_getTmpPassword)localObject)
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    AndroidUtilities.runOnUIThread(new Runnable(paramTLObject, paramTL_error)
                    {
                      public void run()
                      {
                        PaymentFormActivity.this.showEditDoneProgress(false);
                        PaymentFormActivity.this.setDonePressed(false);
                        if (this.val$response != null)
                        {
                          PaymentFormActivity.access$2902(PaymentFormActivity.this, true);
                          UserConfig.tmpPassword = (TLRPC.TL_account_tmpPassword)this.val$response;
                          UserConfig.saveConfig(false);
                          PaymentFormActivity.this.goToNextStep();
                          return;
                        }
                        if (this.val$error.text.equals("PASSWORD_HASH_INVALID"))
                        {
                          Vibrator localVibrator = (Vibrator)ApplicationLoader.applicationContext.getSystemService("vibrator");
                          if (localVibrator != null)
                            localVibrator.vibrate(200L);
                          AndroidUtilities.shakeView(PaymentFormActivity.this.inputFields[1], 2.0F, 0);
                          PaymentFormActivity.this.inputFields[1].setText("");
                          return;
                        }
                        AlertsCreator.processError(this.val$error, PaymentFormActivity.this, PaymentFormActivity.25.1.1.this.val$req, new Object[0]);
                      }
                    });
                  }
                }
                , 2);
                return;
              }
              catch (Exception localException)
              {
                while (true)
                  FileLog.e(localException);
              }
            }
            AlertsCreator.processError(this.val$error, PaymentFormActivity.this, PaymentFormActivity.25.this.val$req, new Object[0]);
            PaymentFormActivity.this.showEditDoneProgress(false);
            PaymentFormActivity.this.setDonePressed(false);
          }
        });
      }
    }
    , 2);
  }

  private TLRPC.TL_paymentRequestedInfo getRequestInfo()
  {
    TLRPC.TL_paymentRequestedInfo localTL_paymentRequestedInfo = new TLRPC.TL_paymentRequestedInfo();
    if (this.paymentForm.invoice.name_requested)
    {
      localTL_paymentRequestedInfo.name = this.inputFields[6].getText().toString();
      localTL_paymentRequestedInfo.flags |= 1;
    }
    if (this.paymentForm.invoice.phone_requested)
    {
      localTL_paymentRequestedInfo.phone = ("+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString());
      localTL_paymentRequestedInfo.flags |= 2;
    }
    if (this.paymentForm.invoice.email_requested)
    {
      localTL_paymentRequestedInfo.email = this.inputFields[7].getText().toString();
      localTL_paymentRequestedInfo.flags |= 4;
    }
    TLRPC.TL_postAddress localTL_postAddress;
    if (this.paymentForm.invoice.shipping_address_requested)
    {
      localTL_paymentRequestedInfo.shipping_address = new TLRPC.TL_postAddress();
      localTL_paymentRequestedInfo.shipping_address.street_line1 = this.inputFields[0].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.street_line2 = this.inputFields[1].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.city = this.inputFields[2].getText().toString();
      localTL_paymentRequestedInfo.shipping_address.state = this.inputFields[3].getText().toString();
      localTL_postAddress = localTL_paymentRequestedInfo.shipping_address;
      if (this.countryName == null)
        break label317;
    }
    label317: for (String str = this.countryName; ; str = "")
    {
      localTL_postAddress.country_iso2 = str;
      localTL_paymentRequestedInfo.shipping_address.post_code = this.inputFields[5].getText().toString();
      localTL_paymentRequestedInfo.flags |= 8;
      return localTL_paymentRequestedInfo;
    }
  }

  private String getTotalPriceDecimalString(ArrayList<TLRPC.TL_labeledPrice> paramArrayList)
  {
    int j = 0;
    int i = 0;
    while (i < paramArrayList.size())
    {
      j = (int)(j + ((TLRPC.TL_labeledPrice)paramArrayList.get(i)).amount);
      i += 1;
    }
    return LocaleController.getInstance().formatCurrencyDecimalString(j, this.paymentForm.invoice.currency);
  }

  private String getTotalPriceString(ArrayList<TLRPC.TL_labeledPrice> paramArrayList)
  {
    int j = 0;
    int i = 0;
    while (i < paramArrayList.size())
    {
      j = (int)(j + ((TLRPC.TL_labeledPrice)paramArrayList.get(i)).amount);
      i += 1;
    }
    return LocaleController.getInstance().formatCurrencyString(j, this.paymentForm.invoice.currency);
  }

  private void goToNextStep()
  {
    int i;
    if (this.currentStep == 0)
      if (this.paymentForm.invoice.flexible)
      {
        i = 1;
        presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, null, null, this.cardName, this.validateRequest, this.saveCardInfo));
      }
    label403: 
    do
    {
      return;
      if (this.paymentForm.saved_credentials != null)
      {
        if ((UserConfig.tmpPassword != null) && (UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60))
        {
          UserConfig.tmpPassword = null;
          UserConfig.saveConfig(false);
        }
        if (UserConfig.tmpPassword != null)
        {
          i = 4;
          break;
        }
        i = 3;
        break;
      }
      i = 2;
      break;
      if (this.currentStep == 1)
      {
        if (this.paymentForm.saved_credentials != null)
        {
          if ((UserConfig.tmpPassword != null) && (UserConfig.tmpPassword.valid_until < ConnectionsManager.getInstance().getCurrentTime() + 60))
          {
            UserConfig.tmpPassword = null;
            UserConfig.saveConfig(false);
          }
          if (UserConfig.tmpPassword != null)
            i = 4;
        }
        while (true)
        {
          presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, null, this.cardName, this.validateRequest, this.saveCardInfo));
          return;
          i = 3;
          continue;
          i = 2;
        }
      }
      if (this.currentStep == 2)
      {
        if (this.delegate != null)
        {
          this.delegate.didSelectNewCard(this.paymentJson, this.cardName, this.saveCardInfo);
          finishFragment();
          return;
        }
        presentFragment(new PaymentFormActivity(this.paymentForm, this.messageObject, 4, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo));
        return;
      }
      if (this.currentStep != 3)
        continue;
      PaymentFormActivity localPaymentFormActivity;
      if (this.passwordOk)
      {
        i = 4;
        localPaymentFormActivity = new PaymentFormActivity(this.paymentForm, this.messageObject, i, this.requestedInfo, this.shippingOption, this.paymentJson, this.cardName, this.validateRequest, this.saveCardInfo);
        if (this.passwordOk)
          break label403;
      }
      for (boolean bool = true; ; bool = false)
      {
        presentFragment(localPaymentFormActivity, bool);
        return;
        i = 2;
        break;
      }
    }
    while (this.currentStep != 4);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.paymentFinished, new Object[0]);
    finishFragment();
  }

  private void init(TLRPC.TL_payments_paymentForm paramTL_payments_paymentForm, MessageObject paramMessageObject, int paramInt, TLRPC.TL_payments_validatedRequestedInfo paramTL_payments_validatedRequestedInfo, TLRPC.TL_shippingOption paramTL_shippingOption, String paramString1, String paramString2, TLRPC.TL_payments_validateRequestedInfo paramTL_payments_validateRequestedInfo, boolean paramBoolean)
  {
    boolean bool = true;
    this.currentStep = paramInt;
    this.paymentJson = paramString1;
    this.requestedInfo = paramTL_payments_validatedRequestedInfo;
    this.paymentForm = paramTL_payments_paymentForm;
    this.shippingOption = paramTL_shippingOption;
    this.messageObject = paramMessageObject;
    this.saveCardInfo = paramBoolean;
    paramTL_payments_validatedRequestedInfo = MessagesController.getInstance().getUser(Integer.valueOf(paramTL_payments_paymentForm.bot_id));
    if (paramTL_payments_validatedRequestedInfo != null)
      this.currentBotName = paramTL_payments_validatedRequestedInfo.first_name;
    while (true)
    {
      this.currentItemName = paramMessageObject.messageOwner.media.title;
      this.validateRequest = paramTL_payments_validateRequestedInfo;
      this.saveShippingInfo = true;
      if (paramBoolean)
      {
        this.saveCardInfo = paramBoolean;
        if (paramString2 != null)
          break;
        if (paramTL_payments_paymentForm.saved_credentials != null)
          this.cardName = paramTL_payments_paymentForm.saved_credentials.title;
        return;
        this.currentBotName = "";
        continue;
      }
      else
      {
        if (this.paymentForm.saved_credentials != null);
        for (paramBoolean = bool; ; paramBoolean = false)
        {
          this.saveCardInfo = paramBoolean;
          break;
        }
      }
    }
    this.cardName = paramString2;
  }

  private boolean sendCardData()
  {
    Object localObject1;
    if ((this.paymentForm.saved_credentials != null) && (!this.saveCardInfo) && (this.paymentForm.can_save_credentials))
    {
      localObject1 = new TLRPC.TL_payments_clearSavedInfo();
      ((TLRPC.TL_payments_clearSavedInfo)localObject1).credentials = true;
      this.paymentForm.saved_credentials = null;
      UserConfig.tmpPassword = null;
      UserConfig.saveConfig(false);
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      });
    }
    Object localObject2 = this.inputFields[1].getText().toString().split("/");
    if (localObject2.length == 2)
      localObject1 = Utilities.parseInt(localObject2[0]);
    for (localObject2 = Utilities.parseInt(localObject2[1]); ; localObject2 = null)
    {
      localObject1 = new com.c.a.b.a(this.inputFields[0].getText().toString(), (Integer)localObject1, (Integer)localObject2, this.inputFields[3].getText().toString(), this.inputFields[2].getText().toString(), null, null, null, null, this.inputFields[5].getText().toString(), this.inputFields[4].getText().toString(), null);
      this.cardName = (((com.c.a.b.a)localObject1).s() + " *" + ((com.c.a.b.a)localObject1).r());
      if (((com.c.a.b.a)localObject1).a())
        break;
      shakeField(0);
      return false;
      localObject1 = null;
    }
    if ((!((com.c.a.b.a)localObject1).d()) || (!((com.c.a.b.a)localObject1).e()) || (!((com.c.a.b.a)localObject1).b()))
    {
      shakeField(1);
      return false;
    }
    if ((this.need_card_name) && (this.inputFields[2].length() == 0))
    {
      shakeField(2);
      return false;
    }
    if (!((com.c.a.b.a)localObject1).c())
    {
      shakeField(3);
      return false;
    }
    if ((this.need_card_country) && (this.inputFields[4].length() == 0))
    {
      shakeField(4);
      return false;
    }
    if ((this.need_card_postcode) && (this.inputFields[5].length() == 0))
    {
      shakeField(5);
      return false;
    }
    showEditDoneProgress(true);
    try
    {
      new com.c.a.a(this.stripeApiKey).a((com.c.a.b.a)localObject1, new com.c.a.b()
      {
        public void onError(Exception paramException)
        {
          if (PaymentFormActivity.this.canceled)
            return;
          PaymentFormActivity.this.showEditDoneProgress(false);
          PaymentFormActivity.this.setDonePressed(false);
          if (((paramException instanceof com.c.a.a.a)) || ((paramException instanceof com.c.a.a.b)))
          {
            AlertsCreator.showSimpleToast(PaymentFormActivity.this, LocaleController.getString("PaymentConnectionFailed", 2131166225));
            return;
          }
          AlertsCreator.showSimpleToast(PaymentFormActivity.this, paramException.getMessage());
        }

        public void onSuccess(com.c.a.b.b paramb)
        {
          if (PaymentFormActivity.this.canceled)
            return;
          PaymentFormActivity.access$002(PaymentFormActivity.this, String.format(Locale.US, "{\"type\":\"%1$s\", \"id\":\"%2$s\"}", new Object[] { paramb.b(), paramb.a() }));
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              PaymentFormActivity.this.goToNextStep();
              PaymentFormActivity.this.showEditDoneProgress(false);
              PaymentFormActivity.this.setDonePressed(false);
            }
          });
        }
      });
      return true;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private void sendData()
  {
    if (this.canceled)
      return;
    showEditDoneProgress(true);
    TLRPC.TL_payments_sendPaymentForm localTL_payments_sendPaymentForm = new TLRPC.TL_payments_sendPaymentForm();
    localTL_payments_sendPaymentForm.msg_id = this.messageObject.getId();
    if ((UserConfig.tmpPassword != null) && (this.paymentForm.saved_credentials != null))
    {
      localTL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentialsSaved();
      localTL_payments_sendPaymentForm.credentials.id = this.paymentForm.saved_credentials.id;
      localTL_payments_sendPaymentForm.credentials.tmp_password = UserConfig.tmpPassword.tmp_password;
    }
    while (true)
    {
      if ((this.requestedInfo != null) && (this.requestedInfo.id != null))
      {
        localTL_payments_sendPaymentForm.requested_info_id = this.requestedInfo.id;
        localTL_payments_sendPaymentForm.flags |= 1;
      }
      if (this.shippingOption != null)
      {
        localTL_payments_sendPaymentForm.shipping_option_id = this.shippingOption.id;
        localTL_payments_sendPaymentForm.flags |= 2;
      }
      ConnectionsManager.getInstance().sendRequest(localTL_payments_sendPaymentForm, new RequestDelegate(localTL_payments_sendPaymentForm)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTLObject != null)
          {
            if ((paramTLObject instanceof TLRPC.TL_payments_paymentResult))
            {
              MessagesController.getInstance().processUpdates(((TLRPC.TL_payments_paymentResult)paramTLObject).updates, false);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  PaymentFormActivity.this.goToNextStep();
                }
              });
            }
            do
              return;
            while (!(paramTLObject instanceof TLRPC.TL_payments_paymentVerficationNeeded));
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                Browser.openUrl(PaymentFormActivity.this.getParentActivity(), ((TLRPC.TL_payments_paymentVerficationNeeded)this.val$response).url, false);
                PaymentFormActivity.this.goToNextStep();
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
          {
            public void run()
            {
              AlertsCreator.processError(this.val$error, PaymentFormActivity.this, PaymentFormActivity.24.this.val$req, new Object[0]);
              PaymentFormActivity.this.setDonePressed(false);
              PaymentFormActivity.this.showEditDoneProgress(false);
            }
          });
        }
      }
      , 2);
      return;
      localTL_payments_sendPaymentForm.credentials = new TLRPC.TL_inputPaymentCredentials();
      localTL_payments_sendPaymentForm.credentials.save = this.saveCardInfo;
      localTL_payments_sendPaymentForm.credentials.data = new TLRPC.TL_dataJSON();
      localTL_payments_sendPaymentForm.credentials.data.data = this.paymentJson;
    }
  }

  private void sendForm()
  {
    if (this.canceled)
      return;
    showEditDoneProgress(true);
    this.validateRequest = new TLRPC.TL_payments_validateRequestedInfo();
    this.validateRequest.save = this.saveShippingInfo;
    this.validateRequest.msg_id = this.messageObject.getId();
    this.validateRequest.info = new TLRPC.TL_paymentRequestedInfo();
    if (this.paymentForm.invoice.name_requested)
    {
      this.validateRequest.info.name = this.inputFields[6].getText().toString();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 1;
    }
    if (this.paymentForm.invoice.phone_requested)
    {
      this.validateRequest.info.phone = ("+" + this.inputFields[8].getText().toString() + this.inputFields[9].getText().toString());
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 2;
    }
    if (this.paymentForm.invoice.email_requested)
    {
      this.validateRequest.info.email = this.inputFields[7].getText().toString();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 4;
    }
    TLRPC.TL_postAddress localTL_postAddress;
    if (this.paymentForm.invoice.shipping_address_requested)
    {
      this.validateRequest.info.shipping_address = new TLRPC.TL_postAddress();
      this.validateRequest.info.shipping_address.street_line1 = this.inputFields[0].getText().toString();
      this.validateRequest.info.shipping_address.street_line2 = this.inputFields[1].getText().toString();
      this.validateRequest.info.shipping_address.city = this.inputFields[2].getText().toString();
      this.validateRequest.info.shipping_address.state = this.inputFields[3].getText().toString();
      localTL_postAddress = this.validateRequest.info.shipping_address;
      if (this.countryName == null)
        break label489;
    }
    label489: for (Object localObject = this.countryName; ; localObject = "")
    {
      localTL_postAddress.country_iso2 = ((String)localObject);
      this.validateRequest.info.shipping_address.post_code = this.inputFields[5].getText().toString();
      localObject = this.validateRequest.info;
      ((TLRPC.TL_paymentRequestedInfo)localObject).flags |= 8;
      localObject = this.validateRequest;
      ConnectionsManager.getInstance().sendRequest(this.validateRequest, new RequestDelegate((TLObject)localObject)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if ((paramTLObject instanceof TLRPC.TL_payments_validatedRequestedInfo))
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                PaymentFormActivity.access$902(PaymentFormActivity.this, (TLRPC.TL_payments_validatedRequestedInfo)this.val$response);
                if ((PaymentFormActivity.this.paymentForm.saved_info != null) && (!PaymentFormActivity.this.saveShippingInfo))
                {
                  TLRPC.TL_payments_clearSavedInfo localTL_payments_clearSavedInfo = new TLRPC.TL_payments_clearSavedInfo();
                  localTL_payments_clearSavedInfo.info = true;
                  ConnectionsManager.getInstance().sendRequest(localTL_payments_clearSavedInfo, new RequestDelegate()
                  {
                    public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                    {
                    }
                  });
                }
                PaymentFormActivity.this.goToNextStep();
                PaymentFormActivity.this.setDonePressed(false);
                PaymentFormActivity.this.showEditDoneProgress(false);
              }
            });
            return;
          }
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
          {
            public void run()
            {
              PaymentFormActivity.this.setDonePressed(false);
              PaymentFormActivity.this.showEditDoneProgress(false);
              String str;
              int i;
              if (this.val$error != null)
              {
                str = this.val$error.text;
                i = -1;
                switch (str.hashCode())
                {
                default:
                case -1031752045:
                case 708423542:
                case 889106340:
                case -1224177757:
                case -2092780146:
                case -274035920:
                case 417441502:
                case -1623547228:
                case 863965605:
                }
              }
              while (true)
                switch (i)
                {
                default:
                  AlertsCreator.processError(this.val$error, PaymentFormActivity.this, PaymentFormActivity.23.this.val$req, new Object[0]);
                  return;
                  if (!str.equals("REQ_INFO_NAME_INVALID"))
                    continue;
                  i = 0;
                  continue;
                  if (!str.equals("REQ_INFO_PHONE_INVALID"))
                    continue;
                  i = 1;
                  continue;
                  if (!str.equals("REQ_INFO_EMAIL_INVALID"))
                    continue;
                  i = 2;
                  continue;
                  if (!str.equals("ADDRESS_COUNTRY_INVALID"))
                    continue;
                  i = 3;
                  continue;
                  if (!str.equals("ADDRESS_CITY_INVALID"))
                    continue;
                  i = 4;
                  continue;
                  if (!str.equals("ADDRESS_POSTCODE_INVALID"))
                    continue;
                  i = 5;
                  continue;
                  if (!str.equals("ADDRESS_STATE_INVALID"))
                    continue;
                  i = 6;
                  continue;
                  if (!str.equals("ADDRESS_STREET_LINE1_INVALID"))
                    continue;
                  i = 7;
                  continue;
                  if (!str.equals("ADDRESS_STREET_LINE2_INVALID"))
                    continue;
                  i = 8;
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                }
              PaymentFormActivity.this.shakeField(6);
              return;
              PaymentFormActivity.this.shakeField(9);
              return;
              PaymentFormActivity.this.shakeField(7);
              return;
              PaymentFormActivity.this.shakeField(4);
              return;
              PaymentFormActivity.this.shakeField(2);
              return;
              PaymentFormActivity.this.shakeField(5);
              return;
              PaymentFormActivity.this.shakeField(3);
              return;
              PaymentFormActivity.this.shakeField(0);
              return;
              PaymentFormActivity.this.shakeField(1);
            }
          });
        }
      }
      , 2);
      return;
    }
  }

  private void setDelegate(PaymentFormActivityDelegate paramPaymentFormActivityDelegate)
  {
    this.delegate = paramPaymentFormActivityDelegate;
  }

  private void setDonePressed(boolean paramBoolean)
  {
    boolean bool = true;
    this.donePressed = paramBoolean;
    Object localObject;
    if (!paramBoolean)
    {
      paramBoolean = true;
      this.swipeBackEnabled = paramBoolean;
      localObject = this.actionBar.getBackButton();
      if (this.donePressed)
        break label76;
      paramBoolean = true;
      label35: ((View)localObject).setEnabled(paramBoolean);
      if (this.detailSettingsCell[0] != null)
      {
        localObject = this.detailSettingsCell[0];
        if (this.donePressed)
          break label81;
      }
    }
    label76: label81: for (paramBoolean = bool; ; paramBoolean = false)
    {
      ((TextDetailSettingsCell)localObject).setEnabled(paramBoolean);
      return;
      paramBoolean = false;
      break;
      paramBoolean = false;
      break label35;
    }
  }

  private void shakeField(int paramInt)
  {
    Vibrator localVibrator = (Vibrator)getParentActivity().getSystemService("vibrator");
    if (localVibrator != null)
      localVibrator.vibrate(200L);
    AndroidUtilities.shakeView(this.inputFields[paramInt], 2.0F, 0);
  }

  private void showEditDoneProgress(boolean paramBoolean)
  {
    if (this.doneItemAnimation != null)
      this.doneItemAnimation.cancel();
    if (this.doneItem != null)
    {
      this.doneItemAnimation = new AnimatorSet();
      if (paramBoolean)
      {
        this.progressView.setVisibility(0);
        this.doneItem.setEnabled(false);
        this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
        this.doneItemAnimation.addListener(new AnimatorListenerAdapter(paramBoolean)
        {
          public void onAnimationCancel(Animator paramAnimator)
          {
            if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnimator)))
              PaymentFormActivity.access$4202(PaymentFormActivity.this, null);
          }

          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnimator)))
            {
              if (!this.val$show)
                PaymentFormActivity.this.progressView.setVisibility(4);
            }
            else
              return;
            PaymentFormActivity.this.doneItem.getImageView().setVisibility(4);
          }
        });
        this.doneItemAnimation.setDuration(150L);
        this.doneItemAnimation.start();
      }
    }
    do
    {
      return;
      if (this.webView != null)
      {
        this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }) });
        break;
      }
      this.doneItem.getImageView().setVisibility(0);
      this.doneItem.setEnabled(true);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 1.0F }) });
      break;
    }
    while (this.payTextView == null);
    this.doneItemAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.progressView.setVisibility(0);
      this.bottomLayout.setEnabled(false);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
    }
    while (true)
    {
      this.doneItemAnimation.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnimator)))
            PaymentFormActivity.access$4202(PaymentFormActivity.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((PaymentFormActivity.this.doneItemAnimation != null) && (PaymentFormActivity.this.doneItemAnimation.equals(paramAnimator)))
          {
            if (!this.val$show)
              PaymentFormActivity.this.progressView.setVisibility(4);
          }
          else
            return;
          PaymentFormActivity.this.payTextView.setVisibility(4);
        }
      });
      this.doneItemAnimation.setDuration(150L);
      this.doneItemAnimation.start();
      return;
      this.payTextView.setVisibility(0);
      this.bottomLayout.setEnabled(true);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.payTextView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.payTextView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.payTextView, "alpha", new float[] { 1.0F }) });
    }
  }

  private void updateSavePaymentField()
  {
    if (this.bottomCell[0] == null)
      return;
    if ((this.paymentForm.can_save_credentials) && ((this.webView == null) || ((this.webView != null) && (!this.webviewLoading))))
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(LocaleController.getString("PaymentCardSavePaymentInformationInfoLine1", 2131166213));
      if (this.paymentForm.password_missing)
      {
        localSpannableStringBuilder.append("\n");
        int i = localSpannableStringBuilder.length();
        String str = LocaleController.getString("PaymentCardSavePaymentInformationInfoLine2", 2131166214);
        int k = str.indexOf('*');
        int j = str.lastIndexOf('*');
        localSpannableStringBuilder.append(str);
        if ((k != -1) && (j != -1))
        {
          k += i;
          i += j;
          this.bottomCell[0].getTextView().setMovementMethod(new LinkMovementMethodMy(null));
          localSpannableStringBuilder.replace(i, i + 1, "");
          localSpannableStringBuilder.replace(k, k + 1, "");
          localSpannableStringBuilder.setSpan(new LinkSpan(), k, i - 1, 33);
        }
        this.checkCell1.setEnabled(false);
      }
      while (true)
      {
        this.bottomCell[0].setText(localSpannableStringBuilder);
        this.checkCell1.setVisibility(0);
        this.bottomCell[0].setVisibility(0);
        this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), 2130837725, "windowBackgroundGrayShadow"));
        return;
        this.checkCell1.setEnabled(true);
      }
    }
    this.checkCell1.setVisibility(8);
    this.bottomCell[0].setVisibility(8);
    this.sectionCell[2].setBackgroundDrawable(Theme.getThemedDrawable(this.sectionCell[2].getContext(), 2130837726, "windowBackgroundGrayShadow"));
  }

  // ERROR //
  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public View createView(Context paramContext)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   4: ifne +1279 -> 1283
    //   7: aload_0
    //   8: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   11: ldc_w 1034
    //   14: ldc_w 1035
    //   17: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   20: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   23: aload_0
    //   24: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   27: ldc_w 1039
    //   30: invokevirtual 1042	org/vidogram/ui/ActionBar/ActionBar:setBackButtonImage	(I)V
    //   33: aload_0
    //   34: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   37: iconst_1
    //   38: invokevirtual 1045	org/vidogram/ui/ActionBar/ActionBar:setAllowOverlayTitle	(Z)V
    //   41: aload_0
    //   42: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   45: new 8	org/vidogram/ui/PaymentFormActivity$1
    //   48: dup
    //   49: aload_0
    //   50: invokespecial 1046	org/vidogram/ui/PaymentFormActivity$1:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   53: invokevirtual 1050	org/vidogram/ui/ActionBar/ActionBar:setActionBarMenuOnItemClick	(Lorg/vidogram/ui/ActionBar/ActionBar$ActionBarMenuOnItemClick;)V
    //   56: aload_0
    //   57: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   60: invokevirtual 1054	org/vidogram/ui/ActionBar/ActionBar:createMenu	()Lorg/vidogram/ui/ActionBar/ActionBarMenu;
    //   63: astore 8
    //   65: aload_0
    //   66: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   69: ifeq +27 -> 96
    //   72: aload_0
    //   73: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   76: iconst_1
    //   77: if_icmpeq +19 -> 96
    //   80: aload_0
    //   81: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   84: iconst_2
    //   85: if_icmpeq +11 -> 96
    //   88: aload_0
    //   89: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   92: iconst_3
    //   93: if_icmpne +61 -> 154
    //   96: aload_0
    //   97: aload 8
    //   99: iconst_1
    //   100: ldc_w 1055
    //   103: ldc_w 1056
    //   106: invokestatic 1060	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   109: invokevirtual 1066	org/vidogram/ui/ActionBar/ActionBarMenu:addItemWithWidth	(III)Lorg/vidogram/ui/ActionBar/ActionBarMenuItem;
    //   112: putfield 437	org/vidogram/ui/PaymentFormActivity:doneItem	Lorg/vidogram/ui/ActionBar/ActionBarMenuItem;
    //   115: aload_0
    //   116: new 891	org/vidogram/ui/Components/ContextProgressView
    //   119: dup
    //   120: aload_1
    //   121: iconst_1
    //   122: invokespecial 1069	org/vidogram/ui/Components/ContextProgressView:<init>	(Landroid/content/Context;I)V
    //   125: putfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   128: aload_0
    //   129: getfield 437	org/vidogram/ui/PaymentFormActivity:doneItem	Lorg/vidogram/ui/ActionBar/ActionBarMenuItem;
    //   132: aload_0
    //   133: getfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   136: iconst_m1
    //   137: ldc_w 1070
    //   140: invokestatic 1076	org/vidogram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   143: invokevirtual 1080	org/vidogram/ui/ActionBar/ActionBarMenuItem:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   146: aload_0
    //   147: getfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   150: iconst_4
    //   151: invokevirtual 894	org/vidogram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   154: aload_0
    //   155: new 944	android/widget/FrameLayout
    //   158: dup
    //   159: aload_1
    //   160: invokespecial 1083	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   163: putfield 1087	org/vidogram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   166: aload_0
    //   167: getfield 1087	org/vidogram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   170: checkcast 944	android/widget/FrameLayout
    //   173: astore 8
    //   175: aload_0
    //   176: getfield 1087	org/vidogram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   179: ldc_w 1089
    //   182: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   185: invokevirtual 1096	android/view/View:setBackgroundColor	(I)V
    //   188: aload_0
    //   189: new 1098	android/widget/ScrollView
    //   192: dup
    //   193: aload_1
    //   194: invokespecial 1099	android/widget/ScrollView:<init>	(Landroid/content/Context;)V
    //   197: putfield 1101	org/vidogram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   200: aload_0
    //   201: getfield 1101	org/vidogram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   204: iconst_1
    //   205: invokevirtual 1104	android/widget/ScrollView:setFillViewport	(Z)V
    //   208: aload_0
    //   209: getfield 1101	org/vidogram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   212: ldc_w 1106
    //   215: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   218: invokestatic 1110	org/vidogram/messenger/AndroidUtilities:setScrollViewEdgeEffectColor	(Landroid/widget/ScrollView;I)V
    //   221: aload_0
    //   222: getfield 1101	org/vidogram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   225: astore 9
    //   227: aload_0
    //   228: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   231: iconst_4
    //   232: if_icmpne +1288 -> 1520
    //   235: ldc_w 1111
    //   238: fstore_2
    //   239: aload 8
    //   241: aload 9
    //   243: iconst_m1
    //   244: ldc_w 1070
    //   247: bipush 51
    //   249: fconst_0
    //   250: fconst_0
    //   251: fconst_0
    //   252: fload_2
    //   253: invokestatic 1114	org/vidogram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   256: invokevirtual 1115	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   259: aload_0
    //   260: new 1117	android/widget/LinearLayout
    //   263: dup
    //   264: aload_1
    //   265: invokespecial 1118	android/widget/LinearLayout:<init>	(Landroid/content/Context;)V
    //   268: putfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   271: aload_0
    //   272: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   275: iconst_1
    //   276: invokevirtual 1123	android/widget/LinearLayout:setOrientation	(I)V
    //   279: aload_0
    //   280: getfield 1101	org/vidogram/ui/PaymentFormActivity:scrollView	Landroid/widget/ScrollView;
    //   283: aload_0
    //   284: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   287: new 1125	android/widget/FrameLayout$LayoutParams
    //   290: dup
    //   291: iconst_m1
    //   292: bipush 254
    //   294: invokespecial 1128	android/widget/FrameLayout$LayoutParams:<init>	(II)V
    //   297: invokevirtual 1129	android/widget/ScrollView:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   300: aload_0
    //   301: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   304: ifne +2997 -> 3301
    //   307: new 226	java/util/HashMap
    //   310: dup
    //   311: invokespecial 227	java/util/HashMap:<init>	()V
    //   314: astore 10
    //   316: new 226	java/util/HashMap
    //   319: dup
    //   320: invokespecial 227	java/util/HashMap:<init>	()V
    //   323: astore 11
    //   325: new 1131	java/io/BufferedReader
    //   328: dup
    //   329: new 1133	java/io/InputStreamReader
    //   332: dup
    //   333: aload_1
    //   334: invokevirtual 1137	android/content/Context:getResources	()Landroid/content/res/Resources;
    //   337: invokevirtual 1143	android/content/res/Resources:getAssets	()Landroid/content/res/AssetManager;
    //   340: ldc_w 1145
    //   343: invokevirtual 1151	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   346: invokespecial 1154	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
    //   349: invokespecial 1157	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   352: astore 8
    //   354: aload 8
    //   356: invokevirtual 1160	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   359: astore 9
    //   361: aload 9
    //   363: ifnull +1162 -> 1525
    //   366: aload 9
    //   368: ldc_w 1162
    //   371: invokevirtual 741	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   374: astore 9
    //   376: aload_0
    //   377: getfield 224	org/vidogram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   380: iconst_0
    //   381: aload 9
    //   383: iconst_2
    //   384: aaload
    //   385: invokevirtual 1166	java/util/ArrayList:add	(ILjava/lang/Object;)V
    //   388: aload_0
    //   389: getfield 229	org/vidogram/ui/PaymentFormActivity:countriesMap	Ljava/util/HashMap;
    //   392: aload 9
    //   394: iconst_2
    //   395: aaload
    //   396: aload 9
    //   398: iconst_0
    //   399: aaload
    //   400: invokevirtual 1170	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   403: pop
    //   404: aload_0
    //   405: getfield 231	org/vidogram/ui/PaymentFormActivity:codesMap	Ljava/util/HashMap;
    //   408: aload 9
    //   410: iconst_0
    //   411: aaload
    //   412: aload 9
    //   414: iconst_2
    //   415: aaload
    //   416: invokevirtual 1170	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   419: pop
    //   420: aload 11
    //   422: aload 9
    //   424: iconst_1
    //   425: aaload
    //   426: aload 9
    //   428: iconst_2
    //   429: aaload
    //   430: invokevirtual 1170	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   433: pop
    //   434: aload 9
    //   436: arraylength
    //   437: iconst_3
    //   438: if_icmple +19 -> 457
    //   441: aload_0
    //   442: getfield 233	org/vidogram/ui/PaymentFormActivity:phoneFormatMap	Ljava/util/HashMap;
    //   445: aload 9
    //   447: iconst_0
    //   448: aaload
    //   449: aload 9
    //   451: iconst_3
    //   452: aaload
    //   453: invokevirtual 1170	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   456: pop
    //   457: aload 10
    //   459: aload 9
    //   461: iconst_1
    //   462: aaload
    //   463: aload 9
    //   465: iconst_2
    //   466: aaload
    //   467: invokevirtual 1170	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   470: pop
    //   471: goto -117 -> 354
    //   474: astore 8
    //   476: aload 8
    //   478: invokestatic 795	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   481: aload_0
    //   482: getfield 224	org/vidogram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   485: new 34	org/vidogram/ui/PaymentFormActivity$2
    //   488: dup
    //   489: aload_0
    //   490: invokespecial 1171	org/vidogram/ui/PaymentFormActivity$2:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   493: invokestatic 1177	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
    //   496: aload_0
    //   497: bipush 10
    //   499: anewarray 547	android/widget/EditText
    //   502: putfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   505: iconst_0
    //   506: istore 5
    //   508: iload 5
    //   510: bipush 10
    //   512: if_icmpge +2151 -> 2663
    //   515: iload 5
    //   517: ifne +1016 -> 1533
    //   520: aload_0
    //   521: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   524: iconst_0
    //   525: new 235	org/vidogram/ui/Cells/HeaderCell
    //   528: dup
    //   529: aload_1
    //   530: invokespecial 1178	org/vidogram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   533: aastore
    //   534: aload_0
    //   535: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   538: iconst_0
    //   539: aaload
    //   540: ldc_w 1180
    //   543: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   546: invokevirtual 1181	org/vidogram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   549: aload_0
    //   550: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   553: iconst_0
    //   554: aaload
    //   555: ldc_w 1183
    //   558: ldc_w 1184
    //   561: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   564: invokevirtual 1186	org/vidogram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   567: aload_0
    //   568: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   571: aload_0
    //   572: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   575: iconst_0
    //   576: aaload
    //   577: iconst_m1
    //   578: bipush 254
    //   580: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   583: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   586: iload 5
    //   588: bipush 8
    //   590: if_icmpne +1052 -> 1642
    //   593: new 1117	android/widget/LinearLayout
    //   596: dup
    //   597: aload_1
    //   598: invokespecial 1118	android/widget/LinearLayout:<init>	(Landroid/content/Context;)V
    //   601: astore 8
    //   603: aload 8
    //   605: checkcast 1117	android/widget/LinearLayout
    //   608: iconst_0
    //   609: invokevirtual 1123	android/widget/LinearLayout:setOrientation	(I)V
    //   612: aload_0
    //   613: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   616: aload 8
    //   618: iconst_m1
    //   619: bipush 48
    //   621: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   624: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   627: aload 8
    //   629: ldc_w 1180
    //   632: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   635: invokevirtual 1194	android/view/ViewGroup:setBackgroundColor	(I)V
    //   638: iload 5
    //   640: bipush 9
    //   642: if_icmpne +1216 -> 1858
    //   645: aload_0
    //   646: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   649: iload 5
    //   651: new 1196	org/vidogram/ui/Components/HintEditText
    //   654: dup
    //   655: aload_1
    //   656: invokespecial 1197	org/vidogram/ui/Components/HintEditText:<init>	(Landroid/content/Context;)V
    //   659: aastore
    //   660: aload_0
    //   661: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   664: iload 5
    //   666: aaload
    //   667: iload 5
    //   669: invokestatic 296	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   672: invokevirtual 1201	android/widget/EditText:setTag	(Ljava/lang/Object;)V
    //   675: aload_0
    //   676: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   679: iload 5
    //   681: aaload
    //   682: iconst_1
    //   683: ldc_w 1202
    //   686: invokevirtual 1206	android/widget/EditText:setTextSize	(IF)V
    //   689: aload_0
    //   690: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   693: iload 5
    //   695: aaload
    //   696: ldc_w 1208
    //   699: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   702: invokevirtual 1211	android/widget/EditText:setHintTextColor	(I)V
    //   705: aload_0
    //   706: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   709: iload 5
    //   711: aaload
    //   712: ldc_w 1213
    //   715: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   718: invokevirtual 1216	android/widget/EditText:setTextColor	(I)V
    //   721: aload_0
    //   722: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   725: iload 5
    //   727: aaload
    //   728: aconst_null
    //   729: invokevirtual 1217	android/widget/EditText:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   732: aload_0
    //   733: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   736: iload 5
    //   738: aaload
    //   739: invokestatic 1221	org/vidogram/messenger/AndroidUtilities:clearCursorDrawable	(Landroid/widget/EditText;)V
    //   742: iload 5
    //   744: iconst_4
    //   745: if_icmpne +32 -> 777
    //   748: aload_0
    //   749: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   752: iload 5
    //   754: aaload
    //   755: new 74	org/vidogram/ui/PaymentFormActivity$3
    //   758: dup
    //   759: aload_0
    //   760: invokespecial 1222	org/vidogram/ui/PaymentFormActivity$3:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   763: invokevirtual 1226	android/widget/EditText:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   766: aload_0
    //   767: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   770: iload 5
    //   772: aaload
    //   773: iconst_0
    //   774: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   777: iload 5
    //   779: bipush 9
    //   781: if_icmpeq +10 -> 791
    //   784: iload 5
    //   786: bipush 8
    //   788: if_icmpne +1088 -> 1876
    //   791: aload_0
    //   792: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   795: iload 5
    //   797: aaload
    //   798: iconst_3
    //   799: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   802: aload_0
    //   803: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   806: iload 5
    //   808: aaload
    //   809: ldc_w 1230
    //   812: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   815: iload 5
    //   817: tableswitch	default:+47 -> 864, 0:+1226->2043, 1:+1294->2111, 2:+1362->2179, 3:+1430->2247, 4:+1498->2315, 5:+1613->2430, 6:+1096->1913, 7:+1161->1978
    //   865: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   868: iload 5
    //   870: aaload
    //   871: aload_0
    //   872: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   875: iload 5
    //   877: aaload
    //   878: invokevirtual 550	android/widget/EditText:length	()I
    //   881: invokevirtual 1236	android/widget/EditText:setSelection	(I)V
    //   884: iload 5
    //   886: bipush 8
    //   888: if_icmpne +1610 -> 2498
    //   891: aload_0
    //   892: new 948	android/widget/TextView
    //   895: dup
    //   896: aload_1
    //   897: invokespecial 1237	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   900: putfield 1239	org/vidogram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   903: aload_0
    //   904: getfield 1239	org/vidogram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   907: ldc_w 614
    //   910: invokevirtual 1240	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   913: aload_0
    //   914: getfield 1239	org/vidogram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   917: ldc_w 1213
    //   920: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   923: invokevirtual 1241	android/widget/TextView:setTextColor	(I)V
    //   926: aload_0
    //   927: getfield 1239	org/vidogram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   930: iconst_1
    //   931: ldc_w 1202
    //   934: invokevirtual 1242	android/widget/TextView:setTextSize	(IF)V
    //   937: aload 8
    //   939: aload_0
    //   940: getfield 1239	org/vidogram/ui/PaymentFormActivity:textView	Landroid/widget/TextView;
    //   943: bipush 254
    //   945: bipush 254
    //   947: ldc_w 1243
    //   950: ldc_w 1244
    //   953: fconst_0
    //   954: ldc_w 1245
    //   957: invokestatic 1248	org/vidogram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   960: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   963: aload_0
    //   964: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   967: iload 5
    //   969: aaload
    //   970: ldc_w 1250
    //   973: invokestatic 1060	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   976: iconst_0
    //   977: iconst_0
    //   978: iconst_0
    //   979: invokevirtual 1254	android/widget/EditText:setPadding	(IIII)V
    //   982: aload_0
    //   983: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   986: iload 5
    //   988: aaload
    //   989: bipush 19
    //   991: invokevirtual 1257	android/widget/EditText:setGravity	(I)V
    //   994: new 1259	android/text/InputFilter$LengthFilter
    //   997: dup
    //   998: iconst_5
    //   999: invokespecial 1261	android/text/InputFilter$LengthFilter:<init>	(I)V
    //   1002: astore 9
    //   1004: aload_0
    //   1005: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1008: iload 5
    //   1010: aaload
    //   1011: iconst_1
    //   1012: anewarray 1263	android/text/InputFilter
    //   1015: dup
    //   1016: iconst_0
    //   1017: aload 9
    //   1019: aastore
    //   1020: invokevirtual 1267	android/widget/EditText:setFilters	([Landroid/text/InputFilter;)V
    //   1023: aload 8
    //   1025: aload_0
    //   1026: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1029: iload 5
    //   1031: aaload
    //   1032: bipush 55
    //   1034: bipush 254
    //   1036: fconst_0
    //   1037: ldc_w 1244
    //   1040: ldc_w 1202
    //   1043: ldc_w 1245
    //   1046: invokestatic 1248	org/vidogram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   1049: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1052: aload_0
    //   1053: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1056: iload 5
    //   1058: aaload
    //   1059: new 78	org/vidogram/ui/PaymentFormActivity$4
    //   1062: dup
    //   1063: aload_0
    //   1064: invokespecial 1268	org/vidogram/ui/PaymentFormActivity$4:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   1067: invokevirtual 1272	android/widget/EditText:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   1070: aload_0
    //   1071: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1074: iload 5
    //   1076: aaload
    //   1077: new 82	org/vidogram/ui/PaymentFormActivity$6
    //   1080: dup
    //   1081: aload_0
    //   1082: invokespecial 1273	org/vidogram/ui/PaymentFormActivity$6:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   1085: invokevirtual 1277	android/widget/EditText:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   1088: iload 5
    //   1090: bipush 9
    //   1092: if_icmpne +182 -> 1274
    //   1095: aload_0
    //   1096: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   1099: iconst_1
    //   1100: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   1103: dup
    //   1104: aload_1
    //   1105: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   1108: aastore
    //   1109: aload_0
    //   1110: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1113: aload_0
    //   1114: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   1117: iconst_1
    //   1118: aaload
    //   1119: iconst_m1
    //   1120: bipush 254
    //   1122: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1125: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1128: aload_0
    //   1129: new 1002	org/vidogram/ui/Cells/TextCheckCell
    //   1132: dup
    //   1133: aload_1
    //   1134: invokespecial 1279	org/vidogram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   1137: putfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   1140: aload_0
    //   1141: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   1144: iconst_1
    //   1145: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   1148: invokevirtual 1284	org/vidogram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   1151: aload_0
    //   1152: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   1155: ldc_w 1286
    //   1158: ldc_w 1287
    //   1161: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1164: aload_0
    //   1165: getfield 444	org/vidogram/ui/PaymentFormActivity:saveShippingInfo	Z
    //   1168: iconst_0
    //   1169: invokevirtual 1291	org/vidogram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   1172: aload_0
    //   1173: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1176: aload_0
    //   1177: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   1180: iconst_m1
    //   1181: bipush 254
    //   1183: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1186: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1189: aload_0
    //   1190: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   1193: new 84	org/vidogram/ui/PaymentFormActivity$7
    //   1196: dup
    //   1197: aload_0
    //   1198: invokespecial 1292	org/vidogram/ui/PaymentFormActivity$7:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   1201: invokevirtual 1296	org/vidogram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   1204: aload_0
    //   1205: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   1208: iconst_0
    //   1209: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   1212: dup
    //   1213: aload_1
    //   1214: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   1217: aastore
    //   1218: aload_0
    //   1219: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   1222: iconst_0
    //   1223: aaload
    //   1224: aload_1
    //   1225: ldc_w 1026
    //   1228: ldc_w 1015
    //   1231: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   1234: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   1237: aload_0
    //   1238: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   1241: iconst_0
    //   1242: aaload
    //   1243: ldc_w 1300
    //   1246: ldc_w 1301
    //   1249: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1252: invokevirtual 1006	org/vidogram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   1255: aload_0
    //   1256: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1259: aload_0
    //   1260: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   1263: iconst_0
    //   1264: aaload
    //   1265: iconst_m1
    //   1266: bipush 254
    //   1268: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1271: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1274: iload 5
    //   1276: iconst_1
    //   1277: iadd
    //   1278: istore 5
    //   1280: goto -772 -> 508
    //   1283: aload_0
    //   1284: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   1287: iconst_1
    //   1288: if_icmpne +22 -> 1310
    //   1291: aload_0
    //   1292: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1295: ldc_w 1303
    //   1298: ldc_w 1304
    //   1301: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1304: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1307: goto -1284 -> 23
    //   1310: aload_0
    //   1311: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   1314: iconst_2
    //   1315: if_icmpne +22 -> 1337
    //   1318: aload_0
    //   1319: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1322: ldc_w 1306
    //   1325: ldc_w 1307
    //   1328: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1331: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1334: goto -1311 -> 23
    //   1337: aload_0
    //   1338: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   1341: iconst_3
    //   1342: if_icmpne +22 -> 1364
    //   1345: aload_0
    //   1346: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1349: ldc_w 1306
    //   1352: ldc_w 1307
    //   1355: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1358: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1361: goto -1338 -> 23
    //   1364: aload_0
    //   1365: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   1368: iconst_4
    //   1369: if_icmpne +73 -> 1442
    //   1372: aload_0
    //   1373: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1376: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   1379: getfield 1310	org/vidogram/tgnet/TLRPC$TL_invoice:test	Z
    //   1382: ifeq +41 -> 1423
    //   1385: aload_0
    //   1386: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1389: new 611	java/lang/StringBuilder
    //   1392: dup
    //   1393: invokespecial 612	java/lang/StringBuilder:<init>	()V
    //   1396: ldc_w 1312
    //   1399: invokevirtual 618	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1402: ldc_w 1314
    //   1405: ldc_w 1315
    //   1408: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1411: invokevirtual 618	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1414: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1417: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1420: goto -1397 -> 23
    //   1423: aload_0
    //   1424: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1427: ldc_w 1314
    //   1430: ldc_w 1315
    //   1433: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1436: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1439: goto -1416 -> 23
    //   1442: aload_0
    //   1443: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   1446: iconst_5
    //   1447: if_icmpne -1424 -> 23
    //   1450: aload_0
    //   1451: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1454: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   1457: getfield 1310	org/vidogram/tgnet/TLRPC$TL_invoice:test	Z
    //   1460: ifeq +41 -> 1501
    //   1463: aload_0
    //   1464: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1467: new 611	java/lang/StringBuilder
    //   1470: dup
    //   1471: invokespecial 612	java/lang/StringBuilder:<init>	()V
    //   1474: ldc_w 1312
    //   1477: invokevirtual 618	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1480: ldc_w 1317
    //   1483: ldc_w 1318
    //   1486: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1489: invokevirtual 618	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1492: invokevirtual 619	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1495: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1498: goto -1475 -> 23
    //   1501: aload_0
    //   1502: getfield 864	org/vidogram/ui/PaymentFormActivity:actionBar	Lorg/vidogram/ui/ActionBar/ActionBar;
    //   1505: ldc_w 1317
    //   1508: ldc_w 1318
    //   1511: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1514: invokevirtual 1038	org/vidogram/ui/ActionBar/ActionBar:setTitle	(Ljava/lang/CharSequence;)V
    //   1517: goto -1494 -> 23
    //   1520: fconst_0
    //   1521: fstore_2
    //   1522: goto -1283 -> 239
    //   1525: aload 8
    //   1527: invokevirtual 1321	java/io/BufferedReader:close	()V
    //   1530: goto -1049 -> 481
    //   1533: iload 5
    //   1535: bipush 6
    //   1537: if_icmpne -951 -> 586
    //   1540: aload_0
    //   1541: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   1544: iconst_0
    //   1545: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   1548: dup
    //   1549: aload_1
    //   1550: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   1553: aastore
    //   1554: aload_0
    //   1555: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1558: aload_0
    //   1559: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   1562: iconst_0
    //   1563: aaload
    //   1564: iconst_m1
    //   1565: bipush 254
    //   1567: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1570: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1573: aload_0
    //   1574: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   1577: iconst_1
    //   1578: new 235	org/vidogram/ui/Cells/HeaderCell
    //   1581: dup
    //   1582: aload_1
    //   1583: invokespecial 1178	org/vidogram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   1586: aastore
    //   1587: aload_0
    //   1588: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   1591: iconst_1
    //   1592: aaload
    //   1593: ldc_w 1180
    //   1596: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1599: invokevirtual 1181	org/vidogram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   1602: aload_0
    //   1603: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   1606: iconst_1
    //   1607: aaload
    //   1608: ldc_w 1323
    //   1611: ldc_w 1324
    //   1614: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1617: invokevirtual 1186	org/vidogram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   1620: aload_0
    //   1621: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1624: aload_0
    //   1625: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   1628: iconst_1
    //   1629: aaload
    //   1630: iconst_m1
    //   1631: bipush 254
    //   1633: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1636: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1639: goto -1053 -> 586
    //   1642: iload 5
    //   1644: bipush 9
    //   1646: if_icmpne +21 -> 1667
    //   1649: aload_0
    //   1650: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1653: bipush 8
    //   1655: aaload
    //   1656: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   1659: checkcast 1193	android/view/ViewGroup
    //   1662: astore 8
    //   1664: goto -1026 -> 638
    //   1667: new 944	android/widget/FrameLayout
    //   1670: dup
    //   1671: aload_1
    //   1672: invokespecial 1083	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   1675: astore 8
    //   1677: aload_0
    //   1678: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   1681: aload 8
    //   1683: iconst_m1
    //   1684: bipush 48
    //   1686: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   1689: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1692: aload 8
    //   1694: ldc_w 1180
    //   1697: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1700: invokevirtual 1194	android/view/ViewGroup:setBackgroundColor	(I)V
    //   1703: iload 5
    //   1705: iconst_5
    //   1706: if_icmpeq +99 -> 1805
    //   1709: iload 5
    //   1711: bipush 9
    //   1713: if_icmpeq +92 -> 1805
    //   1716: iconst_1
    //   1717: istore_3
    //   1718: iload_3
    //   1719: istore 4
    //   1721: iload_3
    //   1722: ifeq +26 -> 1748
    //   1725: iload 5
    //   1727: bipush 7
    //   1729: if_icmpne +81 -> 1810
    //   1732: aload_0
    //   1733: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1736: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   1739: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   1742: ifne +68 -> 1810
    //   1745: iconst_0
    //   1746: istore 4
    //   1748: iload 4
    //   1750: ifeq +52 -> 1802
    //   1753: new 872	android/view/View
    //   1756: dup
    //   1757: aload_1
    //   1758: invokespecial 1329	android/view/View:<init>	(Landroid/content/Context;)V
    //   1761: astore 9
    //   1763: aload_0
    //   1764: getfield 239	org/vidogram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   1767: aload 9
    //   1769: invokevirtual 1332	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1772: pop
    //   1773: aload 9
    //   1775: ldc_w 1334
    //   1778: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   1781: invokevirtual 1096	android/view/View:setBackgroundColor	(I)V
    //   1784: aload 8
    //   1786: aload 9
    //   1788: new 1125	android/widget/FrameLayout$LayoutParams
    //   1791: dup
    //   1792: iconst_m1
    //   1793: iconst_1
    //   1794: bipush 83
    //   1796: invokespecial 1337	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   1799: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   1802: goto -1164 -> 638
    //   1805: iconst_0
    //   1806: istore_3
    //   1807: goto -89 -> 1718
    //   1810: iload_3
    //   1811: istore 4
    //   1813: iload 5
    //   1815: bipush 6
    //   1817: if_icmpne -69 -> 1748
    //   1820: iload_3
    //   1821: istore 4
    //   1823: aload_0
    //   1824: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1827: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   1830: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   1833: ifne -85 -> 1748
    //   1836: iload_3
    //   1837: istore 4
    //   1839: aload_0
    //   1840: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1843: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   1846: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   1849: ifne -101 -> 1748
    //   1852: iconst_0
    //   1853: istore 4
    //   1855: goto -107 -> 1748
    //   1858: aload_0
    //   1859: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1862: iload 5
    //   1864: new 547	android/widget/EditText
    //   1867: dup
    //   1868: aload_1
    //   1869: invokespecial 1338	android/widget/EditText:<init>	(Landroid/content/Context;)V
    //   1872: aastore
    //   1873: goto -1213 -> 660
    //   1876: iload 5
    //   1878: bipush 7
    //   1880: if_icmpne +17 -> 1897
    //   1883: aload_0
    //   1884: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1887: iload 5
    //   1889: aaload
    //   1890: iconst_1
    //   1891: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   1894: goto -1092 -> 802
    //   1897: aload_0
    //   1898: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1901: iload 5
    //   1903: aaload
    //   1904: sipush 16385
    //   1907: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   1910: goto -1108 -> 802
    //   1913: aload_0
    //   1914: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1917: iload 5
    //   1919: aaload
    //   1920: ldc_w 1340
    //   1923: ldc_w 1341
    //   1926: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1929: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   1932: aload_0
    //   1933: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1936: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1939: ifnull -1075 -> 864
    //   1942: aload_0
    //   1943: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1946: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1949: getfield 606	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   1952: ifnull -1088 -> 864
    //   1955: aload_0
    //   1956: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1959: iload 5
    //   1961: aaload
    //   1962: aload_0
    //   1963: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   1966: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   1969: getfield 606	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   1972: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   1975: goto -1111 -> 864
    //   1978: aload_0
    //   1979: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   1982: iload 5
    //   1984: aaload
    //   1985: ldc_w 1350
    //   1988: ldc_w 1351
    //   1991: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1994: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   1997: aload_0
    //   1998: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2001: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2004: ifnull -1140 -> 864
    //   2007: aload_0
    //   2008: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2011: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2014: getfield 625	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   2017: ifnull -1153 -> 864
    //   2020: aload_0
    //   2021: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2024: iload 5
    //   2026: aaload
    //   2027: aload_0
    //   2028: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2031: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2034: getfield 625	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   2037: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2040: goto -1176 -> 864
    //   2043: aload_0
    //   2044: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2047: iload 5
    //   2049: aaload
    //   2050: ldc_w 1353
    //   2053: ldc_w 1354
    //   2056: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2059: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2062: aload_0
    //   2063: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2066: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2069: ifnull -1205 -> 864
    //   2072: aload_0
    //   2073: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2076: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2079: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2082: ifnull -1218 -> 864
    //   2085: aload_0
    //   2086: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2089: iload 5
    //   2091: aaload
    //   2092: aload_0
    //   2093: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2096: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2099: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2102: getfield 635	org/vidogram/tgnet/TLRPC$TL_postAddress:street_line1	Ljava/lang/String;
    //   2105: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2108: goto -1244 -> 864
    //   2111: aload_0
    //   2112: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2115: iload 5
    //   2117: aaload
    //   2118: ldc_w 1356
    //   2121: ldc_w 1357
    //   2124: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2127: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2130: aload_0
    //   2131: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2134: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2137: ifnull -1273 -> 864
    //   2140: aload_0
    //   2141: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2144: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2147: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2150: ifnull -1286 -> 864
    //   2153: aload_0
    //   2154: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2157: iload 5
    //   2159: aaload
    //   2160: aload_0
    //   2161: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2164: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2167: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2170: getfield 638	org/vidogram/tgnet/TLRPC$TL_postAddress:street_line2	Ljava/lang/String;
    //   2173: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2176: goto -1312 -> 864
    //   2179: aload_0
    //   2180: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2183: iload 5
    //   2185: aaload
    //   2186: ldc_w 1359
    //   2189: ldc_w 1360
    //   2192: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2195: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2198: aload_0
    //   2199: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2202: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2205: ifnull -1341 -> 864
    //   2208: aload_0
    //   2209: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2212: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2215: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2218: ifnull -1354 -> 864
    //   2221: aload_0
    //   2222: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2225: iload 5
    //   2227: aaload
    //   2228: aload_0
    //   2229: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2232: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2235: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2238: getfield 641	org/vidogram/tgnet/TLRPC$TL_postAddress:city	Ljava/lang/String;
    //   2241: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2244: goto -1380 -> 864
    //   2247: aload_0
    //   2248: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2251: iload 5
    //   2253: aaload
    //   2254: ldc_w 1362
    //   2257: ldc_w 1363
    //   2260: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2263: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2266: aload_0
    //   2267: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2270: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2273: ifnull -1409 -> 864
    //   2276: aload_0
    //   2277: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2280: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2283: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2286: ifnull -1422 -> 864
    //   2289: aload_0
    //   2290: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2293: iload 5
    //   2295: aaload
    //   2296: aload_0
    //   2297: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2300: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2303: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2306: getfield 644	org/vidogram/tgnet/TLRPC$TL_postAddress:state	Ljava/lang/String;
    //   2309: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2312: goto -1448 -> 864
    //   2315: aload_0
    //   2316: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2319: iload 5
    //   2321: aaload
    //   2322: ldc_w 1365
    //   2325: ldc_w 1366
    //   2328: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2331: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2334: aload_0
    //   2335: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2338: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2341: ifnull -1477 -> 864
    //   2344: aload_0
    //   2345: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2348: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2351: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2354: ifnull -1490 -> 864
    //   2357: aload 11
    //   2359: aload_0
    //   2360: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2363: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2366: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2369: getfield 647	org/vidogram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   2372: invokevirtual 1369	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2375: checkcast 737	java/lang/String
    //   2378: astore 9
    //   2380: aload_0
    //   2381: aload_0
    //   2382: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2385: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2388: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2391: getfield 647	org/vidogram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   2394: putfield 419	org/vidogram/ui/PaymentFormActivity:countryName	Ljava/lang/String;
    //   2397: aload_0
    //   2398: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2401: iload 5
    //   2403: aaload
    //   2404: astore 12
    //   2406: aload 9
    //   2408: ifnull +13 -> 2421
    //   2411: aload 12
    //   2413: aload 9
    //   2415: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2418: goto -1554 -> 864
    //   2421: aload_0
    //   2422: getfield 419	org/vidogram/ui/PaymentFormActivity:countryName	Ljava/lang/String;
    //   2425: astore 9
    //   2427: goto -16 -> 2411
    //   2430: aload_0
    //   2431: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2434: iload 5
    //   2436: aaload
    //   2437: ldc_w 1371
    //   2440: ldc_w 1372
    //   2443: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   2446: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   2449: aload_0
    //   2450: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2453: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2456: ifnull -1592 -> 864
    //   2459: aload_0
    //   2460: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2463: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2466: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2469: ifnull -1605 -> 864
    //   2472: aload_0
    //   2473: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2476: iload 5
    //   2478: aaload
    //   2479: aload_0
    //   2480: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2483: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   2486: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   2489: getfield 650	org/vidogram/tgnet/TLRPC$TL_postAddress:post_code	Ljava/lang/String;
    //   2492: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   2495: goto -1631 -> 864
    //   2498: iload 5
    //   2500: bipush 9
    //   2502: if_icmpne +78 -> 2580
    //   2505: aload_0
    //   2506: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2509: iload 5
    //   2511: aaload
    //   2512: iconst_0
    //   2513: iconst_0
    //   2514: iconst_0
    //   2515: iconst_0
    //   2516: invokevirtual 1254	android/widget/EditText:setPadding	(IIII)V
    //   2519: aload_0
    //   2520: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2523: iload 5
    //   2525: aaload
    //   2526: bipush 19
    //   2528: invokevirtual 1257	android/widget/EditText:setGravity	(I)V
    //   2531: aload 8
    //   2533: aload_0
    //   2534: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2537: iload 5
    //   2539: aaload
    //   2540: iconst_m1
    //   2541: bipush 254
    //   2543: fconst_0
    //   2544: ldc_w 1244
    //   2547: ldc_w 1243
    //   2550: ldc_w 1245
    //   2553: invokestatic 1248	org/vidogram/ui/Components/LayoutHelper:createLinear	(IIFFFF)Landroid/widget/LinearLayout$LayoutParams;
    //   2556: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2559: aload_0
    //   2560: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2563: iload 5
    //   2565: aaload
    //   2566: new 80	org/vidogram/ui/PaymentFormActivity$5
    //   2569: dup
    //   2570: aload_0
    //   2571: invokespecial 1373	org/vidogram/ui/PaymentFormActivity$5:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   2574: invokevirtual 1272	android/widget/EditText:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   2577: goto -1507 -> 1070
    //   2580: aload_0
    //   2581: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2584: iload 5
    //   2586: aaload
    //   2587: iconst_0
    //   2588: iconst_0
    //   2589: iconst_0
    //   2590: ldc_w 1245
    //   2593: invokestatic 1060	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2596: invokevirtual 1254	android/widget/EditText:setPadding	(IIII)V
    //   2599: aload_0
    //   2600: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2603: iload 5
    //   2605: aaload
    //   2606: astore 9
    //   2608: getstatic 1376	org/vidogram/messenger/LocaleController:isRTL	Z
    //   2611: ifeq +47 -> 2658
    //   2614: iconst_5
    //   2615: istore_3
    //   2616: aload 9
    //   2618: iload_3
    //   2619: invokevirtual 1257	android/widget/EditText:setGravity	(I)V
    //   2622: aload 8
    //   2624: aload_0
    //   2625: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2628: iload 5
    //   2630: aaload
    //   2631: iconst_m1
    //   2632: ldc_w 1377
    //   2635: bipush 51
    //   2637: ldc_w 1243
    //   2640: ldc_w 1244
    //   2643: ldc_w 1243
    //   2646: ldc_w 1245
    //   2649: invokestatic 1114	org/vidogram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   2652: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   2655: goto -1585 -> 1070
    //   2658: iconst_3
    //   2659: istore_3
    //   2660: goto -44 -> 2616
    //   2663: aload_0
    //   2664: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2667: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2670: getfield 356	org/vidogram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   2673: ifne +21 -> 2694
    //   2676: aload_0
    //   2677: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2680: bipush 6
    //   2682: aaload
    //   2683: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2686: checkcast 1193	android/view/ViewGroup
    //   2689: bipush 8
    //   2691: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2694: aload_0
    //   2695: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2698: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2701: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   2704: ifne +21 -> 2725
    //   2707: aload_0
    //   2708: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2711: bipush 8
    //   2713: aaload
    //   2714: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2717: checkcast 1193	android/view/ViewGroup
    //   2720: bipush 8
    //   2722: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2725: aload_0
    //   2726: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2729: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2732: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   2735: ifne +21 -> 2756
    //   2738: aload_0
    //   2739: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2742: bipush 7
    //   2744: aaload
    //   2745: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2748: checkcast 1193	android/view/ViewGroup
    //   2751: bipush 8
    //   2753: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2756: aload_0
    //   2757: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2760: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2763: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   2766: ifeq +432 -> 3198
    //   2769: aload_0
    //   2770: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2773: bipush 9
    //   2775: aaload
    //   2776: ldc_w 1379
    //   2779: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   2782: aload_0
    //   2783: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   2786: iconst_1
    //   2787: aaload
    //   2788: astore_1
    //   2789: aload_0
    //   2790: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2793: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2796: getfield 356	org/vidogram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   2799: ifne +29 -> 2828
    //   2802: aload_0
    //   2803: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2806: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2809: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   2812: ifne +16 -> 2828
    //   2815: aload_0
    //   2816: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2819: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2822: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   2825: ifeq +446 -> 3271
    //   2828: iconst_0
    //   2829: istore_3
    //   2830: aload_1
    //   2831: iload_3
    //   2832: invokevirtual 1380	org/vidogram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   2835: aload_0
    //   2836: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   2839: iconst_1
    //   2840: aaload
    //   2841: astore_1
    //   2842: aload_0
    //   2843: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2846: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2849: getfield 356	org/vidogram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   2852: ifne +29 -> 2881
    //   2855: aload_0
    //   2856: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2859: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2862: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   2865: ifne +16 -> 2881
    //   2868: aload_0
    //   2869: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2872: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2875: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   2878: ifeq +399 -> 3277
    //   2881: iconst_0
    //   2882: istore_3
    //   2883: aload_1
    //   2884: iload_3
    //   2885: invokevirtual 1381	org/vidogram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   2888: aload_0
    //   2889: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   2892: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   2895: getfield 350	org/vidogram/tgnet/TLRPC$TL_invoice:shipping_address_requested	Z
    //   2898: ifne +127 -> 3025
    //   2901: aload_0
    //   2902: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   2905: iconst_0
    //   2906: aaload
    //   2907: bipush 8
    //   2909: invokevirtual 1381	org/vidogram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   2912: aload_0
    //   2913: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   2916: iconst_0
    //   2917: aaload
    //   2918: bipush 8
    //   2920: invokevirtual 1380	org/vidogram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   2923: aload_0
    //   2924: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2927: iconst_0
    //   2928: aaload
    //   2929: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2932: checkcast 1193	android/view/ViewGroup
    //   2935: bipush 8
    //   2937: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2940: aload_0
    //   2941: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2944: iconst_1
    //   2945: aaload
    //   2946: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2949: checkcast 1193	android/view/ViewGroup
    //   2952: bipush 8
    //   2954: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2957: aload_0
    //   2958: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2961: iconst_2
    //   2962: aaload
    //   2963: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2966: checkcast 1193	android/view/ViewGroup
    //   2969: bipush 8
    //   2971: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2974: aload_0
    //   2975: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2978: iconst_3
    //   2979: aaload
    //   2980: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   2983: checkcast 1193	android/view/ViewGroup
    //   2986: bipush 8
    //   2988: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   2991: aload_0
    //   2992: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   2995: iconst_4
    //   2996: aaload
    //   2997: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   3000: checkcast 1193	android/view/ViewGroup
    //   3003: bipush 8
    //   3005: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   3008: aload_0
    //   3009: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3012: iconst_5
    //   3013: aaload
    //   3014: invokevirtual 1328	android/widget/EditText:getParent	()Landroid/view/ViewParent;
    //   3017: checkcast 1193	android/view/ViewGroup
    //   3020: bipush 8
    //   3022: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   3025: aload_0
    //   3026: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3029: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3032: ifnull +251 -> 3283
    //   3035: aload_0
    //   3036: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3039: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3042: getfield 622	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3045: invokestatic 1387	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3048: ifne +235 -> 3283
    //   3051: aload_0
    //   3052: aload_0
    //   3053: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3056: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3059: getfield 622	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3062: invokevirtual 1390	org/vidogram/ui/PaymentFormActivity:fillNumber	(Ljava/lang/String;)V
    //   3065: aload_0
    //   3066: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3069: bipush 8
    //   3071: aaload
    //   3072: invokevirtual 550	android/widget/EditText:length	()I
    //   3075: ifne +118 -> 3193
    //   3078: aload_0
    //   3079: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3082: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   3085: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   3088: ifeq +105 -> 3193
    //   3091: aload_0
    //   3092: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3095: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3098: ifnull +19 -> 3117
    //   3101: aload_0
    //   3102: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3105: getfield 1347	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   3108: getfield 622	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   3111: invokestatic 1387	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   3114: ifeq +79 -> 3193
    //   3117: getstatic 556	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   3120: ldc_w 1391
    //   3123: invokevirtual 564	android/content/Context:getSystemService	(Ljava/lang/String;)Ljava/lang/Object;
    //   3126: checkcast 1393	android/telephony/TelephonyManager
    //   3129: astore_1
    //   3130: aload_1
    //   3131: ifnull +165 -> 3296
    //   3134: aload_1
    //   3135: invokevirtual 1396	android/telephony/TelephonyManager:getSimCountryIso	()Ljava/lang/String;
    //   3138: invokevirtual 1399	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   3141: astore_1
    //   3142: aload_1
    //   3143: ifnull +50 -> 3193
    //   3146: aload 10
    //   3148: aload_1
    //   3149: invokevirtual 1369	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3152: checkcast 737	java/lang/String
    //   3155: astore_1
    //   3156: aload_1
    //   3157: ifnull +36 -> 3193
    //   3160: aload_0
    //   3161: getfield 224	org/vidogram/ui/PaymentFormActivity:countriesArray	Ljava/util/ArrayList;
    //   3164: aload_1
    //   3165: invokevirtual 1402	java/util/ArrayList:indexOf	(Ljava/lang/Object;)I
    //   3168: iconst_m1
    //   3169: if_icmpeq +24 -> 3193
    //   3172: aload_0
    //   3173: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3176: bipush 8
    //   3178: aaload
    //   3179: aload_0
    //   3180: getfield 229	org/vidogram/ui/PaymentFormActivity:countriesMap	Ljava/util/HashMap;
    //   3183: aload_1
    //   3184: invokevirtual 1369	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3187: checkcast 1404	java/lang/CharSequence
    //   3190: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   3193: aload_0
    //   3194: getfield 1087	org/vidogram/ui/PaymentFormActivity:fragmentView	Landroid/view/View;
    //   3197: areturn
    //   3198: aload_0
    //   3199: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3202: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   3205: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   3208: ifeq +19 -> 3227
    //   3211: aload_0
    //   3212: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3215: bipush 7
    //   3217: aaload
    //   3218: ldc_w 1379
    //   3221: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   3224: goto -442 -> 2782
    //   3227: aload_0
    //   3228: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3231: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   3234: getfield 356	org/vidogram/tgnet/TLRPC$TL_invoice:name_requested	Z
    //   3237: ifeq +19 -> 3256
    //   3240: aload_0
    //   3241: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3244: bipush 6
    //   3246: aaload
    //   3247: ldc_w 1379
    //   3250: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   3253: goto -471 -> 2782
    //   3256: aload_0
    //   3257: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3260: iconst_5
    //   3261: aaload
    //   3262: ldc_w 1379
    //   3265: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   3268: goto -486 -> 2782
    //   3271: bipush 8
    //   3273: istore_3
    //   3274: goto -444 -> 2830
    //   3277: bipush 8
    //   3279: istore_3
    //   3280: goto -397 -> 2883
    //   3283: aload_0
    //   3284: aconst_null
    //   3285: invokevirtual 1390	org/vidogram/ui/PaymentFormActivity:fillNumber	(Ljava/lang/String;)V
    //   3288: goto -223 -> 3065
    //   3291: astore_1
    //   3292: aload_1
    //   3293: invokestatic 795	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3296: aconst_null
    //   3297: astore_1
    //   3298: goto -156 -> 3142
    //   3301: aload_0
    //   3302: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   3305: iconst_2
    //   3306: if_icmpne +1660 -> 4966
    //   3309: ldc_w 1406
    //   3312: aload_0
    //   3313: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3316: getfield 1409	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:native_provider	Ljava/lang/String;
    //   3319: invokevirtual 1412	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3322: ifne +324 -> 3646
    //   3325: aload_0
    //   3326: iconst_1
    //   3327: putfield 452	org/vidogram/ui/PaymentFormActivity:webviewLoading	Z
    //   3330: aload_0
    //   3331: iconst_1
    //   3332: invokespecial 457	org/vidogram/ui/PaymentFormActivity:showEditDoneProgress	(Z)V
    //   3335: aload_0
    //   3336: getfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   3339: iconst_0
    //   3340: invokevirtual 894	org/vidogram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   3343: aload_0
    //   3344: getfield 437	org/vidogram/ui/PaymentFormActivity:doneItem	Lorg/vidogram/ui/ActionBar/ActionBarMenuItem;
    //   3347: iconst_0
    //   3348: invokevirtual 897	org/vidogram/ui/ActionBar/ActionBarMenuItem:setEnabled	(Z)V
    //   3351: aload_0
    //   3352: getfield 437	org/vidogram/ui/PaymentFormActivity:doneItem	Lorg/vidogram/ui/ActionBar/ActionBarMenuItem;
    //   3355: invokevirtual 903	org/vidogram/ui/ActionBar/ActionBarMenuItem:getImageView	()Landroid/widget/ImageView;
    //   3358: iconst_4
    //   3359: invokevirtual 940	android/widget/ImageView:setVisibility	(I)V
    //   3362: aload_0
    //   3363: new 1414	android/webkit/WebView
    //   3366: dup
    //   3367: aload_1
    //   3368: invokespecial 1415	android/webkit/WebView:<init>	(Landroid/content/Context;)V
    //   3371: putfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3374: aload_0
    //   3375: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3378: invokevirtual 1419	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   3381: iconst_1
    //   3382: invokevirtual 1424	android/webkit/WebSettings:setJavaScriptEnabled	(Z)V
    //   3385: aload_0
    //   3386: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3389: invokevirtual 1419	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   3392: iconst_1
    //   3393: invokevirtual 1427	android/webkit/WebSettings:setDomStorageEnabled	(Z)V
    //   3396: getstatic 1432	android/os/Build$VERSION:SDK_INT	I
    //   3399: bipush 21
    //   3401: if_icmplt +44 -> 3445
    //   3404: aload_0
    //   3405: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3408: invokevirtual 1419	android/webkit/WebView:getSettings	()Landroid/webkit/WebSettings;
    //   3411: iconst_0
    //   3412: invokevirtual 1435	android/webkit/WebSettings:setMixedContentMode	(I)V
    //   3415: invokestatic 1440	android/webkit/CookieManager:getInstance	()Landroid/webkit/CookieManager;
    //   3418: aload_0
    //   3419: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3422: iconst_1
    //   3423: invokevirtual 1444	android/webkit/CookieManager:setAcceptThirdPartyCookies	(Landroid/webkit/WebView;Z)V
    //   3426: aload_0
    //   3427: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3430: new 99	org/vidogram/ui/PaymentFormActivity$TelegramWebviewProxy
    //   3433: dup
    //   3434: aload_0
    //   3435: aconst_null
    //   3436: invokespecial 1447	org/vidogram/ui/PaymentFormActivity$TelegramWebviewProxy:<init>	(Lorg/vidogram/ui/PaymentFormActivity;Lorg/vidogram/ui/PaymentFormActivity$1;)V
    //   3439: ldc_w 1448
    //   3442: invokevirtual 1452	android/webkit/WebView:addJavascriptInterface	(Ljava/lang/Object;Ljava/lang/String;)V
    //   3445: aload_0
    //   3446: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3449: new 86	org/vidogram/ui/PaymentFormActivity$8
    //   3452: dup
    //   3453: aload_0
    //   3454: invokespecial 1453	org/vidogram/ui/PaymentFormActivity$8:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   3457: invokevirtual 1457	android/webkit/WebView:setWebViewClient	(Landroid/webkit/WebViewClient;)V
    //   3460: aload_0
    //   3461: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3464: aload_0
    //   3465: getfield 937	org/vidogram/ui/PaymentFormActivity:webView	Landroid/webkit/WebView;
    //   3468: iconst_m1
    //   3469: ldc_w 1377
    //   3472: invokestatic 1076	org/vidogram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   3475: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3478: aload_0
    //   3479: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   3482: iconst_2
    //   3483: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   3486: dup
    //   3487: aload_1
    //   3488: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   3491: aastore
    //   3492: aload_0
    //   3493: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3496: aload_0
    //   3497: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   3500: iconst_2
    //   3501: aaload
    //   3502: iconst_m1
    //   3503: bipush 254
    //   3505: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3508: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3511: aload_0
    //   3512: new 1002	org/vidogram/ui/Cells/TextCheckCell
    //   3515: dup
    //   3516: aload_1
    //   3517: invokespecial 1279	org/vidogram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   3520: putfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   3523: aload_0
    //   3524: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   3527: iconst_1
    //   3528: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   3531: invokevirtual 1284	org/vidogram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   3534: aload_0
    //   3535: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   3538: ldc_w 1459
    //   3541: ldc_w 1460
    //   3544: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3547: aload_0
    //   3548: getfield 464	org/vidogram/ui/PaymentFormActivity:saveCardInfo	Z
    //   3551: iconst_0
    //   3552: invokevirtual 1291	org/vidogram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   3555: aload_0
    //   3556: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3559: aload_0
    //   3560: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   3563: iconst_m1
    //   3564: bipush 254
    //   3566: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3569: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3572: aload_0
    //   3573: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   3576: new 88	org/vidogram/ui/PaymentFormActivity$9
    //   3579: dup
    //   3580: aload_0
    //   3581: invokespecial 1461	org/vidogram/ui/PaymentFormActivity$9:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   3584: invokevirtual 1296	org/vidogram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   3587: aload_0
    //   3588: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   3591: iconst_0
    //   3592: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   3595: dup
    //   3596: aload_1
    //   3597: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   3600: aastore
    //   3601: aload_0
    //   3602: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   3605: iconst_0
    //   3606: aaload
    //   3607: aload_1
    //   3608: ldc_w 1026
    //   3611: ldc_w 1015
    //   3614: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   3617: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   3620: aload_0
    //   3621: invokespecial 461	org/vidogram/ui/PaymentFormActivity:updateSavePaymentField	()V
    //   3624: aload_0
    //   3625: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3628: aload_0
    //   3629: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   3632: iconst_0
    //   3633: aaload
    //   3634: iconst_m1
    //   3635: bipush 254
    //   3637: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3640: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3643: goto -450 -> 3193
    //   3646: new 1463	org/json/JSONObject
    //   3649: dup
    //   3650: aload_0
    //   3651: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   3654: getfield 1466	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:native_params	Lorg/vidogram/tgnet/TLRPC$TL_dataJSON;
    //   3657: getfield 852	org/vidogram/tgnet/TLRPC$TL_dataJSON:data	Ljava/lang/String;
    //   3660: invokespecial 1467	org/json/JSONObject:<init>	(Ljava/lang/String;)V
    //   3663: astore 8
    //   3665: aload_0
    //   3666: aload 8
    //   3668: ldc_w 1469
    //   3671: invokevirtual 1473	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   3674: putfield 777	org/vidogram/ui/PaymentFormActivity:need_card_country	Z
    //   3677: aload_0
    //   3678: aload 8
    //   3680: ldc_w 1475
    //   3683: invokevirtual 1473	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   3686: putfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   3689: aload_0
    //   3690: aload 8
    //   3692: ldc_w 1477
    //   3695: invokevirtual 1473	org/json/JSONObject:getBoolean	(Ljava/lang/String;)Z
    //   3698: putfield 472	org/vidogram/ui/PaymentFormActivity:need_card_name	Z
    //   3701: aload_0
    //   3702: aload 8
    //   3704: ldc_w 1479
    //   3707: invokevirtual 1482	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
    //   3710: putfield 783	org/vidogram/ui/PaymentFormActivity:stripeApiKey	Ljava/lang/String;
    //   3713: aload_0
    //   3714: bipush 6
    //   3716: anewarray 547	android/widget/EditText
    //   3719: putfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3722: iconst_0
    //   3723: istore_3
    //   3724: iload_3
    //   3725: bipush 6
    //   3727: if_icmpge +1166 -> 4893
    //   3730: iload_3
    //   3731: ifne +645 -> 4376
    //   3734: aload_0
    //   3735: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   3738: iconst_0
    //   3739: new 235	org/vidogram/ui/Cells/HeaderCell
    //   3742: dup
    //   3743: aload_1
    //   3744: invokespecial 1178	org/vidogram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   3747: aastore
    //   3748: aload_0
    //   3749: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   3752: iconst_0
    //   3753: aaload
    //   3754: ldc_w 1180
    //   3757: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3760: invokevirtual 1181	org/vidogram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   3763: aload_0
    //   3764: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   3767: iconst_0
    //   3768: aaload
    //   3769: ldc_w 1484
    //   3772: ldc_w 1485
    //   3775: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   3778: invokevirtual 1186	org/vidogram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   3781: aload_0
    //   3782: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3785: aload_0
    //   3786: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   3789: iconst_0
    //   3790: aaload
    //   3791: iconst_m1
    //   3792: bipush 254
    //   3794: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3797: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3800: iload_3
    //   3801: iconst_3
    //   3802: if_icmpeq +648 -> 4450
    //   3805: iload_3
    //   3806: iconst_5
    //   3807: if_icmpeq +643 -> 4450
    //   3810: iload_3
    //   3811: iconst_4
    //   3812: if_icmpne +10 -> 3822
    //   3815: aload_0
    //   3816: getfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   3819: ifeq +631 -> 4450
    //   3822: iconst_1
    //   3823: istore 4
    //   3825: new 944	android/widget/FrameLayout
    //   3828: dup
    //   3829: aload_1
    //   3830: invokespecial 1083	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   3833: astore 8
    //   3835: aload_0
    //   3836: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   3839: aload 8
    //   3841: iconst_m1
    //   3842: bipush 48
    //   3844: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   3847: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3850: aload 8
    //   3852: ldc_w 1180
    //   3855: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3858: invokevirtual 1194	android/view/ViewGroup:setBackgroundColor	(I)V
    //   3861: iload 4
    //   3863: ifeq +52 -> 3915
    //   3866: new 872	android/view/View
    //   3869: dup
    //   3870: aload_1
    //   3871: invokespecial 1329	android/view/View:<init>	(Landroid/content/Context;)V
    //   3874: astore 9
    //   3876: aload_0
    //   3877: getfield 239	org/vidogram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   3880: aload 9
    //   3882: invokevirtual 1332	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3885: pop
    //   3886: aload 9
    //   3888: ldc_w 1334
    //   3891: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3894: invokevirtual 1096	android/view/View:setBackgroundColor	(I)V
    //   3897: aload 8
    //   3899: aload 9
    //   3901: new 1125	android/widget/FrameLayout$LayoutParams
    //   3904: dup
    //   3905: iconst_m1
    //   3906: iconst_1
    //   3907: bipush 83
    //   3909: invokespecial 1337	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   3912: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   3915: aload_0
    //   3916: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3919: iload_3
    //   3920: new 547	android/widget/EditText
    //   3923: dup
    //   3924: aload_1
    //   3925: invokespecial 1338	android/widget/EditText:<init>	(Landroid/content/Context;)V
    //   3928: aastore
    //   3929: aload_0
    //   3930: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3933: iload_3
    //   3934: aaload
    //   3935: iload_3
    //   3936: invokestatic 296	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3939: invokevirtual 1201	android/widget/EditText:setTag	(Ljava/lang/Object;)V
    //   3942: aload_0
    //   3943: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3946: iload_3
    //   3947: aaload
    //   3948: iconst_1
    //   3949: ldc_w 1202
    //   3952: invokevirtual 1206	android/widget/EditText:setTextSize	(IF)V
    //   3955: aload_0
    //   3956: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3959: iload_3
    //   3960: aaload
    //   3961: ldc_w 1208
    //   3964: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3967: invokevirtual 1211	android/widget/EditText:setHintTextColor	(I)V
    //   3970: aload_0
    //   3971: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3974: iload_3
    //   3975: aaload
    //   3976: ldc_w 1213
    //   3979: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   3982: invokevirtual 1216	android/widget/EditText:setTextColor	(I)V
    //   3985: aload_0
    //   3986: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3989: iload_3
    //   3990: aaload
    //   3991: aconst_null
    //   3992: invokevirtual 1217	android/widget/EditText:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   3995: aload_0
    //   3996: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   3999: iload_3
    //   4000: aaload
    //   4001: invokestatic 1221	org/vidogram/messenger/AndroidUtilities:clearCursorDrawable	(Landroid/widget/EditText;)V
    //   4004: iload_3
    //   4005: iconst_3
    //   4006: if_icmpne +450 -> 4456
    //   4009: new 1259	android/text/InputFilter$LengthFilter
    //   4012: dup
    //   4013: iconst_3
    //   4014: invokespecial 1261	android/text/InputFilter$LengthFilter:<init>	(I)V
    //   4017: astore 9
    //   4019: aload_0
    //   4020: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4023: iload_3
    //   4024: aaload
    //   4025: iconst_1
    //   4026: anewarray 1263	android/text/InputFilter
    //   4029: dup
    //   4030: iconst_0
    //   4031: aload 9
    //   4033: aastore
    //   4034: invokevirtual 1267	android/widget/EditText:setFilters	([Landroid/text/InputFilter;)V
    //   4037: aload_0
    //   4038: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4041: iload_3
    //   4042: aaload
    //   4043: sipush 130
    //   4046: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4049: aload_0
    //   4050: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4053: iload_3
    //   4054: aaload
    //   4055: getstatic 1491	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   4058: invokevirtual 1495	android/widget/EditText:setTypeface	(Landroid/graphics/Typeface;)V
    //   4061: aload_0
    //   4062: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4065: iload_3
    //   4066: aaload
    //   4067: invokestatic 1500	android/text/method/PasswordTransformationMethod:getInstance	()Landroid/text/method/PasswordTransformationMethod;
    //   4070: invokevirtual 1504	android/widget/EditText:setTransformationMethod	(Landroid/text/method/TransformationMethod;)V
    //   4073: aload_0
    //   4074: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4077: iload_3
    //   4078: aaload
    //   4079: ldc_w 1230
    //   4082: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   4085: iload_3
    //   4086: tableswitch	default:+38 -> 4124, 0:+477->4563, 1:+519->4605, 2:+540->4626, 3:+498->4584, 4:+582->4668, 5:+561->4647
    //   4125: ifne +564 -> 4689
    //   4128: aload_0
    //   4129: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4132: iload_3
    //   4133: aaload
    //   4134: new 14	org/vidogram/ui/PaymentFormActivity$11
    //   4137: dup
    //   4138: aload_0
    //   4139: invokespecial 1505	org/vidogram/ui/PaymentFormActivity$11:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   4142: invokevirtual 1272	android/widget/EditText:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   4145: aload_0
    //   4146: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4149: iload_3
    //   4150: aaload
    //   4151: iconst_0
    //   4152: iconst_0
    //   4153: iconst_0
    //   4154: ldc_w 1245
    //   4157: invokestatic 1060	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4160: invokevirtual 1254	android/widget/EditText:setPadding	(IIII)V
    //   4163: aload_0
    //   4164: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4167: iload_3
    //   4168: aaload
    //   4169: astore 9
    //   4171: getstatic 1376	org/vidogram/messenger/LocaleController:isRTL	Z
    //   4174: ifeq +540 -> 4714
    //   4177: iconst_5
    //   4178: istore 4
    //   4180: aload 9
    //   4182: iload 4
    //   4184: invokevirtual 1257	android/widget/EditText:setGravity	(I)V
    //   4187: aload 8
    //   4189: aload_0
    //   4190: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4193: iload_3
    //   4194: aaload
    //   4195: iconst_m1
    //   4196: ldc_w 1377
    //   4199: bipush 51
    //   4201: ldc_w 1243
    //   4204: ldc_w 1244
    //   4207: ldc_w 1243
    //   4210: ldc_w 1245
    //   4213: invokestatic 1114	org/vidogram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   4216: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4219: aload_0
    //   4220: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4223: iload_3
    //   4224: aaload
    //   4225: new 18	org/vidogram/ui/PaymentFormActivity$13
    //   4228: dup
    //   4229: aload_0
    //   4230: invokespecial 1506	org/vidogram/ui/PaymentFormActivity$13:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   4233: invokevirtual 1277	android/widget/EditText:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   4236: iload_3
    //   4237: iconst_3
    //   4238: if_icmpne +482 -> 4720
    //   4241: aload_0
    //   4242: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   4245: iconst_0
    //   4246: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   4249: dup
    //   4250: aload_1
    //   4251: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   4254: aastore
    //   4255: aload_0
    //   4256: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4259: aload_0
    //   4260: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   4263: iconst_0
    //   4264: aaload
    //   4265: iconst_m1
    //   4266: bipush 254
    //   4268: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4271: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4274: iload_3
    //   4275: iconst_4
    //   4276: if_icmpne +10 -> 4286
    //   4279: aload_0
    //   4280: getfield 777	org/vidogram/ui/PaymentFormActivity:need_card_country	Z
    //   4283: ifeq +27 -> 4310
    //   4286: iload_3
    //   4287: iconst_5
    //   4288: if_icmpne +10 -> 4298
    //   4291: aload_0
    //   4292: getfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4295: ifeq +15 -> 4310
    //   4298: iload_3
    //   4299: iconst_2
    //   4300: if_icmpne +17 -> 4317
    //   4303: aload_0
    //   4304: getfield 472	org/vidogram/ui/PaymentFormActivity:need_card_name	Z
    //   4307: ifne +10 -> 4317
    //   4310: aload 8
    //   4312: bipush 8
    //   4314: invokevirtual 1378	android/view/ViewGroup:setVisibility	(I)V
    //   4317: iload_3
    //   4318: iconst_1
    //   4319: iadd
    //   4320: istore_3
    //   4321: goto -597 -> 3724
    //   4324: astore 9
    //   4326: aload_0
    //   4327: iconst_0
    //   4328: putfield 777	org/vidogram/ui/PaymentFormActivity:need_card_country	Z
    //   4331: goto -654 -> 3677
    //   4334: astore 8
    //   4336: aload 8
    //   4338: invokestatic 795	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   4341: goto -628 -> 3713
    //   4344: astore 9
    //   4346: aload_0
    //   4347: iconst_0
    //   4348: putfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4351: goto -662 -> 3689
    //   4354: astore 9
    //   4356: aload_0
    //   4357: iconst_0
    //   4358: putfield 472	org/vidogram/ui/PaymentFormActivity:need_card_name	Z
    //   4361: goto -660 -> 3701
    //   4364: astore 8
    //   4366: aload_0
    //   4367: ldc_w 343
    //   4370: putfield 783	org/vidogram/ui/PaymentFormActivity:stripeApiKey	Ljava/lang/String;
    //   4373: goto -660 -> 3713
    //   4376: iload_3
    //   4377: iconst_4
    //   4378: if_icmpne -578 -> 3800
    //   4381: aload_0
    //   4382: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   4385: iconst_1
    //   4386: new 235	org/vidogram/ui/Cells/HeaderCell
    //   4389: dup
    //   4390: aload_1
    //   4391: invokespecial 1178	org/vidogram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   4394: aastore
    //   4395: aload_0
    //   4396: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   4399: iconst_1
    //   4400: aaload
    //   4401: ldc_w 1180
    //   4404: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   4407: invokevirtual 1181	org/vidogram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   4410: aload_0
    //   4411: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   4414: iconst_1
    //   4415: aaload
    //   4416: ldc_w 1508
    //   4419: ldc_w 1509
    //   4422: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4425: invokevirtual 1186	org/vidogram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   4428: aload_0
    //   4429: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4432: aload_0
    //   4433: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   4436: iconst_1
    //   4437: aaload
    //   4438: iconst_m1
    //   4439: bipush 254
    //   4441: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4444: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4447: goto -647 -> 3800
    //   4450: iconst_0
    //   4451: istore 4
    //   4453: goto -628 -> 3825
    //   4456: iload_3
    //   4457: ifne +16 -> 4473
    //   4460: aload_0
    //   4461: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4464: iload_3
    //   4465: aaload
    //   4466: iconst_2
    //   4467: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4470: goto -397 -> 4073
    //   4473: iload_3
    //   4474: iconst_4
    //   4475: if_icmpne +33 -> 4508
    //   4478: aload_0
    //   4479: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4482: iload_3
    //   4483: aaload
    //   4484: new 10	org/vidogram/ui/PaymentFormActivity$10
    //   4487: dup
    //   4488: aload_0
    //   4489: invokespecial 1510	org/vidogram/ui/PaymentFormActivity$10:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   4492: invokevirtual 1226	android/widget/EditText:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   4495: aload_0
    //   4496: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4499: iload_3
    //   4500: aaload
    //   4501: iconst_0
    //   4502: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4505: goto -432 -> 4073
    //   4508: iload_3
    //   4509: iconst_1
    //   4510: if_icmpne +18 -> 4528
    //   4513: aload_0
    //   4514: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4517: iload_3
    //   4518: aaload
    //   4519: sipush 16386
    //   4522: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4525: goto -452 -> 4073
    //   4528: iload_3
    //   4529: iconst_2
    //   4530: if_icmpne +18 -> 4548
    //   4533: aload_0
    //   4534: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4537: iload_3
    //   4538: aaload
    //   4539: sipush 4097
    //   4542: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4545: goto -472 -> 4073
    //   4548: aload_0
    //   4549: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4552: iload_3
    //   4553: aaload
    //   4554: sipush 16385
    //   4557: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   4560: goto -487 -> 4073
    //   4563: aload_0
    //   4564: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4567: iload_3
    //   4568: aaload
    //   4569: ldc_w 1512
    //   4572: ldc_w 1513
    //   4575: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4578: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4581: goto -457 -> 4124
    //   4584: aload_0
    //   4585: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4588: iload_3
    //   4589: aaload
    //   4590: ldc_w 1515
    //   4593: ldc_w 1516
    //   4596: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4599: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4602: goto -478 -> 4124
    //   4605: aload_0
    //   4606: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4609: iload_3
    //   4610: aaload
    //   4611: ldc_w 1518
    //   4614: ldc_w 1519
    //   4617: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4620: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4623: goto -499 -> 4124
    //   4626: aload_0
    //   4627: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4630: iload_3
    //   4631: aaload
    //   4632: ldc_w 1521
    //   4635: ldc_w 1522
    //   4638: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4641: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4644: goto -520 -> 4124
    //   4647: aload_0
    //   4648: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4651: iload_3
    //   4652: aaload
    //   4653: ldc_w 1371
    //   4656: ldc_w 1372
    //   4659: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4662: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4665: goto -541 -> 4124
    //   4668: aload_0
    //   4669: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4672: iload_3
    //   4673: aaload
    //   4674: ldc_w 1365
    //   4677: ldc_w 1366
    //   4680: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4683: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   4686: goto -562 -> 4124
    //   4689: iload_3
    //   4690: iconst_1
    //   4691: if_icmpne -546 -> 4145
    //   4694: aload_0
    //   4695: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4698: iload_3
    //   4699: aaload
    //   4700: new 16	org/vidogram/ui/PaymentFormActivity$12
    //   4703: dup
    //   4704: aload_0
    //   4705: invokespecial 1523	org/vidogram/ui/PaymentFormActivity$12:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   4708: invokevirtual 1272	android/widget/EditText:addTextChangedListener	(Landroid/text/TextWatcher;)V
    //   4711: goto -566 -> 4145
    //   4714: iconst_3
    //   4715: istore 4
    //   4717: goto -537 -> 4180
    //   4720: iload_3
    //   4721: iconst_5
    //   4722: if_icmpne -448 -> 4274
    //   4725: aload_0
    //   4726: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   4729: iconst_2
    //   4730: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   4733: dup
    //   4734: aload_1
    //   4735: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   4738: aastore
    //   4739: aload_0
    //   4740: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4743: aload_0
    //   4744: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   4747: iconst_2
    //   4748: aaload
    //   4749: iconst_m1
    //   4750: bipush 254
    //   4752: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4755: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4758: aload_0
    //   4759: new 1002	org/vidogram/ui/Cells/TextCheckCell
    //   4762: dup
    //   4763: aload_1
    //   4764: invokespecial 1279	org/vidogram/ui/Cells/TextCheckCell:<init>	(Landroid/content/Context;)V
    //   4767: putfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   4770: aload_0
    //   4771: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   4774: iconst_1
    //   4775: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   4778: invokevirtual 1284	org/vidogram/ui/Cells/TextCheckCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   4781: aload_0
    //   4782: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   4785: ldc_w 1459
    //   4788: ldc_w 1460
    //   4791: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4794: aload_0
    //   4795: getfield 464	org/vidogram/ui/PaymentFormActivity:saveCardInfo	Z
    //   4798: iconst_0
    //   4799: invokevirtual 1291	org/vidogram/ui/Cells/TextCheckCell:setTextAndCheck	(Ljava/lang/String;ZZ)V
    //   4802: aload_0
    //   4803: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4806: aload_0
    //   4807: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   4810: iconst_m1
    //   4811: bipush 254
    //   4813: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4816: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4819: aload_0
    //   4820: getfield 449	org/vidogram/ui/PaymentFormActivity:checkCell1	Lorg/vidogram/ui/Cells/TextCheckCell;
    //   4823: new 20	org/vidogram/ui/PaymentFormActivity$14
    //   4826: dup
    //   4827: aload_0
    //   4828: invokespecial 1524	org/vidogram/ui/PaymentFormActivity$14:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   4831: invokevirtual 1296	org/vidogram/ui/Cells/TextCheckCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   4834: aload_0
    //   4835: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   4838: iconst_0
    //   4839: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   4842: dup
    //   4843: aload_1
    //   4844: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   4847: aastore
    //   4848: aload_0
    //   4849: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   4852: iconst_0
    //   4853: aaload
    //   4854: aload_1
    //   4855: ldc_w 1026
    //   4858: ldc_w 1015
    //   4861: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   4864: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   4867: aload_0
    //   4868: invokespecial 461	org/vidogram/ui/PaymentFormActivity:updateSavePaymentField	()V
    //   4871: aload_0
    //   4872: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   4875: aload_0
    //   4876: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   4879: iconst_0
    //   4880: aaload
    //   4881: iconst_m1
    //   4882: bipush 254
    //   4884: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   4887: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   4890: goto -616 -> 4274
    //   4893: aload_0
    //   4894: getfield 777	org/vidogram/ui/PaymentFormActivity:need_card_country	Z
    //   4897: ifne +32 -> 4929
    //   4900: aload_0
    //   4901: getfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4904: ifne +25 -> 4929
    //   4907: aload_0
    //   4908: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   4911: iconst_1
    //   4912: aaload
    //   4913: bipush 8
    //   4915: invokevirtual 1381	org/vidogram/ui/Cells/HeaderCell:setVisibility	(I)V
    //   4918: aload_0
    //   4919: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   4922: iconst_0
    //   4923: aaload
    //   4924: bipush 8
    //   4926: invokevirtual 1380	org/vidogram/ui/Cells/ShadowSectionCell:setVisibility	(I)V
    //   4929: aload_0
    //   4930: getfield 779	org/vidogram/ui/PaymentFormActivity:need_card_postcode	Z
    //   4933: ifeq +18 -> 4951
    //   4936: aload_0
    //   4937: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4940: iconst_5
    //   4941: aaload
    //   4942: ldc_w 1379
    //   4945: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   4948: goto -1755 -> 3193
    //   4951: aload_0
    //   4952: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   4955: iconst_3
    //   4956: aaload
    //   4957: ldc_w 1379
    //   4960: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   4963: goto -1770 -> 3193
    //   4966: aload_0
    //   4967: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   4970: iconst_1
    //   4971: if_icmpne +261 -> 5232
    //   4974: aload_0
    //   4975: getfield 543	org/vidogram/ui/PaymentFormActivity:requestedInfo	Lorg/vidogram/tgnet/TLRPC$TL_payments_validatedRequestedInfo;
    //   4978: getfield 1527	org/vidogram/tgnet/TLRPC$TL_payments_validatedRequestedInfo:shipping_options	Ljava/util/ArrayList;
    //   4981: invokevirtual 655	java/util/ArrayList:size	()I
    //   4984: istore 4
    //   4986: aload_0
    //   4987: iload 4
    //   4989: anewarray 1529	org/vidogram/ui/Cells/RadioCell
    //   4992: putfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   4995: iconst_0
    //   4996: istore_3
    //   4997: iload_3
    //   4998: iload 4
    //   5000: if_icmpge +177 -> 5177
    //   5003: aload_0
    //   5004: getfield 543	org/vidogram/ui/PaymentFormActivity:requestedInfo	Lorg/vidogram/tgnet/TLRPC$TL_payments_validatedRequestedInfo;
    //   5007: getfield 1527	org/vidogram/tgnet/TLRPC$TL_payments_validatedRequestedInfo:shipping_options	Ljava/util/ArrayList;
    //   5010: iload_3
    //   5011: invokevirtual 659	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5014: checkcast 830	org/vidogram/tgnet/TLRPC$TL_shippingOption
    //   5017: astore 9
    //   5019: aload_0
    //   5020: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5023: iload_3
    //   5024: new 1529	org/vidogram/ui/Cells/RadioCell
    //   5027: dup
    //   5028: aload_1
    //   5029: invokespecial 1530	org/vidogram/ui/Cells/RadioCell:<init>	(Landroid/content/Context;)V
    //   5032: aastore
    //   5033: aload_0
    //   5034: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5037: iload_3
    //   5038: aaload
    //   5039: iload_3
    //   5040: invokestatic 296	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5043: invokevirtual 1531	org/vidogram/ui/Cells/RadioCell:setTag	(Ljava/lang/Object;)V
    //   5046: aload_0
    //   5047: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5050: iload_3
    //   5051: aaload
    //   5052: iconst_1
    //   5053: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   5056: invokevirtual 1532	org/vidogram/ui/Cells/RadioCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5059: aload_0
    //   5060: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5063: iload_3
    //   5064: aaload
    //   5065: astore 8
    //   5067: ldc_w 1534
    //   5070: iconst_2
    //   5071: anewarray 584	java/lang/Object
    //   5074: dup
    //   5075: iconst_0
    //   5076: aload_0
    //   5077: aload 9
    //   5079: getfield 1537	org/vidogram/tgnet/TLRPC$TL_shippingOption:prices	Ljava/util/ArrayList;
    //   5082: invokespecial 1539	org/vidogram/ui/PaymentFormActivity:getTotalPriceString	(Ljava/util/ArrayList;)Ljava/lang/String;
    //   5085: aastore
    //   5086: dup
    //   5087: iconst_1
    //   5088: aload 9
    //   5090: getfield 1540	org/vidogram/tgnet/TLRPC$TL_shippingOption:title	Ljava/lang/String;
    //   5093: aastore
    //   5094: invokestatic 1544	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   5097: astore 9
    //   5099: iload_3
    //   5100: ifne +65 -> 5165
    //   5103: iconst_1
    //   5104: istore 6
    //   5106: iload_3
    //   5107: iload 4
    //   5109: iconst_1
    //   5110: isub
    //   5111: if_icmpeq +60 -> 5171
    //   5114: iconst_1
    //   5115: istore 7
    //   5117: aload 8
    //   5119: aload 9
    //   5121: iload 6
    //   5123: iload 7
    //   5125: invokevirtual 1546	org/vidogram/ui/Cells/RadioCell:setText	(Ljava/lang/String;ZZ)V
    //   5128: aload_0
    //   5129: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5132: iload_3
    //   5133: aaload
    //   5134: new 22	org/vidogram/ui/PaymentFormActivity$15
    //   5137: dup
    //   5138: aload_0
    //   5139: invokespecial 1547	org/vidogram/ui/PaymentFormActivity$15:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   5142: invokevirtual 1548	org/vidogram/ui/Cells/RadioCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   5145: aload_0
    //   5146: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5149: aload_0
    //   5150: getfield 535	org/vidogram/ui/PaymentFormActivity:radioCells	[Lorg/vidogram/ui/Cells/RadioCell;
    //   5153: iload_3
    //   5154: aaload
    //   5155: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   5158: iload_3
    //   5159: iconst_1
    //   5160: iadd
    //   5161: istore_3
    //   5162: goto -165 -> 4997
    //   5165: iconst_0
    //   5166: istore 6
    //   5168: goto -62 -> 5106
    //   5171: iconst_0
    //   5172: istore 7
    //   5174: goto -57 -> 5117
    //   5177: aload_0
    //   5178: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5181: iconst_0
    //   5182: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   5185: dup
    //   5186: aload_1
    //   5187: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   5190: aastore
    //   5191: aload_0
    //   5192: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5195: iconst_0
    //   5196: aaload
    //   5197: aload_1
    //   5198: ldc_w 1026
    //   5201: ldc_w 1015
    //   5204: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   5207: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5210: aload_0
    //   5211: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5214: aload_0
    //   5215: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5218: iconst_0
    //   5219: aaload
    //   5220: iconst_m1
    //   5221: bipush 254
    //   5223: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5226: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5229: goto -2036 -> 3193
    //   5232: aload_0
    //   5233: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   5236: iconst_3
    //   5237: if_icmpne +849 -> 6086
    //   5240: aload_0
    //   5241: iconst_2
    //   5242: anewarray 547	android/widget/EditText
    //   5245: putfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5248: iconst_0
    //   5249: istore 5
    //   5251: iload 5
    //   5253: iconst_2
    //   5254: if_icmpge -2061 -> 3193
    //   5257: iload 5
    //   5259: ifne +69 -> 5328
    //   5262: aload_0
    //   5263: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   5266: iconst_0
    //   5267: new 235	org/vidogram/ui/Cells/HeaderCell
    //   5270: dup
    //   5271: aload_1
    //   5272: invokespecial 1178	org/vidogram/ui/Cells/HeaderCell:<init>	(Landroid/content/Context;)V
    //   5275: aastore
    //   5276: aload_0
    //   5277: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   5280: iconst_0
    //   5281: aaload
    //   5282: ldc_w 1180
    //   5285: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5288: invokevirtual 1181	org/vidogram/ui/Cells/HeaderCell:setBackgroundColor	(I)V
    //   5291: aload_0
    //   5292: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   5295: iconst_0
    //   5296: aaload
    //   5297: ldc_w 1484
    //   5300: ldc_w 1485
    //   5303: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5306: invokevirtual 1186	org/vidogram/ui/Cells/HeaderCell:setText	(Ljava/lang/String;)V
    //   5309: aload_0
    //   5310: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5313: aload_0
    //   5314: getfield 237	org/vidogram/ui/PaymentFormActivity:headerCell	[Lorg/vidogram/ui/Cells/HeaderCell;
    //   5317: iconst_0
    //   5318: aaload
    //   5319: iconst_m1
    //   5320: bipush 254
    //   5322: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5325: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5328: new 944	android/widget/FrameLayout
    //   5331: dup
    //   5332: aload_1
    //   5333: invokespecial 1083	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   5336: astore 8
    //   5338: aload_0
    //   5339: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5342: aload 8
    //   5344: iconst_m1
    //   5345: bipush 48
    //   5347: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5350: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5353: aload 8
    //   5355: ldc_w 1180
    //   5358: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5361: invokevirtual 1194	android/view/ViewGroup:setBackgroundColor	(I)V
    //   5364: iload 5
    //   5366: iconst_1
    //   5367: if_icmpeq +576 -> 5943
    //   5370: iconst_1
    //   5371: istore_3
    //   5372: iload_3
    //   5373: istore 4
    //   5375: iload_3
    //   5376: ifeq +26 -> 5402
    //   5379: iload 5
    //   5381: bipush 7
    //   5383: if_icmpne +565 -> 5948
    //   5386: aload_0
    //   5387: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   5390: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   5393: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   5396: ifne +552 -> 5948
    //   5399: iconst_0
    //   5400: istore 4
    //   5402: iload 4
    //   5404: ifeq +52 -> 5456
    //   5407: new 872	android/view/View
    //   5410: dup
    //   5411: aload_1
    //   5412: invokespecial 1329	android/view/View:<init>	(Landroid/content/Context;)V
    //   5415: astore 9
    //   5417: aload_0
    //   5418: getfield 239	org/vidogram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   5421: aload 9
    //   5423: invokevirtual 1332	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   5426: pop
    //   5427: aload 9
    //   5429: ldc_w 1334
    //   5432: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5435: invokevirtual 1096	android/view/View:setBackgroundColor	(I)V
    //   5438: aload 8
    //   5440: aload 9
    //   5442: new 1125	android/widget/FrameLayout$LayoutParams
    //   5445: dup
    //   5446: iconst_m1
    //   5447: iconst_1
    //   5448: bipush 83
    //   5450: invokespecial 1337	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   5453: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5456: aload_0
    //   5457: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5460: iload 5
    //   5462: new 547	android/widget/EditText
    //   5465: dup
    //   5466: aload_1
    //   5467: invokespecial 1338	android/widget/EditText:<init>	(Landroid/content/Context;)V
    //   5470: aastore
    //   5471: aload_0
    //   5472: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5475: iload 5
    //   5477: aaload
    //   5478: iload 5
    //   5480: invokestatic 296	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   5483: invokevirtual 1201	android/widget/EditText:setTag	(Ljava/lang/Object;)V
    //   5486: aload_0
    //   5487: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5490: iload 5
    //   5492: aaload
    //   5493: iconst_1
    //   5494: ldc_w 1202
    //   5497: invokevirtual 1206	android/widget/EditText:setTextSize	(IF)V
    //   5500: aload_0
    //   5501: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5504: iload 5
    //   5506: aaload
    //   5507: ldc_w 1208
    //   5510: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5513: invokevirtual 1211	android/widget/EditText:setHintTextColor	(I)V
    //   5516: aload_0
    //   5517: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5520: iload 5
    //   5522: aaload
    //   5523: ldc_w 1213
    //   5526: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   5529: invokevirtual 1216	android/widget/EditText:setTextColor	(I)V
    //   5532: aload_0
    //   5533: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5536: iload 5
    //   5538: aaload
    //   5539: aconst_null
    //   5540: invokevirtual 1217	android/widget/EditText:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5543: aload_0
    //   5544: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5547: iload 5
    //   5549: aaload
    //   5550: invokestatic 1221	org/vidogram/messenger/AndroidUtilities:clearCursorDrawable	(Landroid/widget/EditText;)V
    //   5553: iload 5
    //   5555: ifne +441 -> 5996
    //   5558: aload_0
    //   5559: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5562: iload 5
    //   5564: aaload
    //   5565: new 24	org/vidogram/ui/PaymentFormActivity$16
    //   5568: dup
    //   5569: aload_0
    //   5570: invokespecial 1552	org/vidogram/ui/PaymentFormActivity$16:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   5573: invokevirtual 1226	android/widget/EditText:setOnTouchListener	(Landroid/view/View$OnTouchListener;)V
    //   5576: aload_0
    //   5577: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5580: iload 5
    //   5582: aaload
    //   5583: iconst_0
    //   5584: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   5587: aload_0
    //   5588: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5591: iload 5
    //   5593: aaload
    //   5594: ldc_w 1379
    //   5597: invokevirtual 1233	android/widget/EditText:setImeOptions	(I)V
    //   5600: iload 5
    //   5602: tableswitch	default:+22 -> 5624, 0:+423->6025, 1:+446->6048
    //   5625: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5628: iload 5
    //   5630: aaload
    //   5631: iconst_0
    //   5632: iconst_0
    //   5633: iconst_0
    //   5634: ldc_w 1245
    //   5637: invokestatic 1060	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5640: invokevirtual 1254	android/widget/EditText:setPadding	(IIII)V
    //   5643: aload_0
    //   5644: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5647: iload 5
    //   5649: aaload
    //   5650: astore 9
    //   5652: getstatic 1376	org/vidogram/messenger/LocaleController:isRTL	Z
    //   5655: ifeq +426 -> 6081
    //   5658: iconst_5
    //   5659: istore_3
    //   5660: aload 9
    //   5662: iload_3
    //   5663: invokevirtual 1257	android/widget/EditText:setGravity	(I)V
    //   5666: aload 8
    //   5668: aload_0
    //   5669: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5672: iload 5
    //   5674: aaload
    //   5675: iconst_m1
    //   5676: ldc_w 1377
    //   5679: bipush 51
    //   5681: ldc_w 1243
    //   5684: ldc_w 1244
    //   5687: ldc_w 1243
    //   5690: ldc_w 1245
    //   5693: invokestatic 1114	org/vidogram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   5696: invokevirtual 1249	android/view/ViewGroup:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5699: aload_0
    //   5700: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   5703: iload 5
    //   5705: aaload
    //   5706: new 26	org/vidogram/ui/PaymentFormActivity$17
    //   5709: dup
    //   5710: aload_0
    //   5711: invokespecial 1553	org/vidogram/ui/PaymentFormActivity$17:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   5714: invokevirtual 1277	android/widget/EditText:setOnEditorActionListener	(Landroid/widget/TextView$OnEditorActionListener;)V
    //   5717: iload 5
    //   5719: iconst_1
    //   5720: if_icmpne +214 -> 5934
    //   5723: aload_0
    //   5724: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5727: iconst_0
    //   5728: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   5731: dup
    //   5732: aload_1
    //   5733: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   5736: aastore
    //   5737: aload_0
    //   5738: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5741: iconst_0
    //   5742: aaload
    //   5743: ldc_w 1555
    //   5746: ldc_w 1556
    //   5749: iconst_1
    //   5750: anewarray 584	java/lang/Object
    //   5753: dup
    //   5754: iconst_0
    //   5755: aload_0
    //   5756: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   5759: getfield 367	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_credentials	Lorg/vidogram/tgnet/TLRPC$TL_paymentSavedCredentialsCard;
    //   5762: getfield 717	org/vidogram/tgnet/TLRPC$TL_paymentSavedCredentialsCard:title	Ljava/lang/String;
    //   5765: aastore
    //   5766: invokestatic 1560	org/vidogram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   5769: invokevirtual 1006	org/vidogram/ui/Cells/TextInfoPrivacyCell:setText	(Ljava/lang/CharSequence;)V
    //   5772: aload_0
    //   5773: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5776: iconst_0
    //   5777: aaload
    //   5778: aload_1
    //   5779: ldc_w 1013
    //   5782: ldc_w 1015
    //   5785: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   5788: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5791: aload_0
    //   5792: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5795: aload_0
    //   5796: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5799: iconst_0
    //   5800: aaload
    //   5801: iconst_m1
    //   5802: bipush 254
    //   5804: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5807: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5810: aload_0
    //   5811: new 1562	org/vidogram/ui/Cells/TextSettingsCell
    //   5814: dup
    //   5815: aload_1
    //   5816: invokespecial 1563	org/vidogram/ui/Cells/TextSettingsCell:<init>	(Landroid/content/Context;)V
    //   5819: putfield 1565	org/vidogram/ui/PaymentFormActivity:settingsCell1	Lorg/vidogram/ui/Cells/TextSettingsCell;
    //   5822: aload_0
    //   5823: getfield 1565	org/vidogram/ui/PaymentFormActivity:settingsCell1	Lorg/vidogram/ui/Cells/TextSettingsCell;
    //   5826: iconst_1
    //   5827: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   5830: invokevirtual 1566	org/vidogram/ui/Cells/TextSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5833: aload_0
    //   5834: getfield 1565	org/vidogram/ui/PaymentFormActivity:settingsCell1	Lorg/vidogram/ui/Cells/TextSettingsCell;
    //   5837: ldc_w 1568
    //   5840: ldc_w 1569
    //   5843: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   5846: iconst_0
    //   5847: invokevirtual 1572	org/vidogram/ui/Cells/TextSettingsCell:setText	(Ljava/lang/String;Z)V
    //   5850: aload_0
    //   5851: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5854: aload_0
    //   5855: getfield 1565	org/vidogram/ui/PaymentFormActivity:settingsCell1	Lorg/vidogram/ui/Cells/TextSettingsCell;
    //   5858: iconst_m1
    //   5859: bipush 254
    //   5861: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5864: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5867: aload_0
    //   5868: getfield 1565	org/vidogram/ui/PaymentFormActivity:settingsCell1	Lorg/vidogram/ui/Cells/TextSettingsCell;
    //   5871: new 28	org/vidogram/ui/PaymentFormActivity$18
    //   5874: dup
    //   5875: aload_0
    //   5876: invokespecial 1573	org/vidogram/ui/PaymentFormActivity$18:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   5879: invokevirtual 1574	org/vidogram/ui/Cells/TextSettingsCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   5882: aload_0
    //   5883: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5886: iconst_1
    //   5887: new 245	org/vidogram/ui/Cells/TextInfoPrivacyCell
    //   5890: dup
    //   5891: aload_1
    //   5892: invokespecial 1297	org/vidogram/ui/Cells/TextInfoPrivacyCell:<init>	(Landroid/content/Context;)V
    //   5895: aastore
    //   5896: aload_0
    //   5897: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5900: iconst_1
    //   5901: aaload
    //   5902: aload_1
    //   5903: ldc_w 1026
    //   5906: ldc_w 1015
    //   5909: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   5912: invokevirtual 1298	org/vidogram/ui/Cells/TextInfoPrivacyCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   5915: aload_0
    //   5916: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   5919: aload_0
    //   5920: getfield 247	org/vidogram/ui/PaymentFormActivity:bottomCell	[Lorg/vidogram/ui/Cells/TextInfoPrivacyCell;
    //   5923: iconst_1
    //   5924: aaload
    //   5925: iconst_m1
    //   5926: bipush 254
    //   5928: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   5931: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   5934: iload 5
    //   5936: iconst_1
    //   5937: iadd
    //   5938: istore 5
    //   5940: goto -689 -> 5251
    //   5943: iconst_0
    //   5944: istore_3
    //   5945: goto -573 -> 5372
    //   5948: iload_3
    //   5949: istore 4
    //   5951: iload 5
    //   5953: bipush 6
    //   5955: if_icmpne -553 -> 5402
    //   5958: iload_3
    //   5959: istore 4
    //   5961: aload_0
    //   5962: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   5965: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   5968: getfield 359	org/vidogram/tgnet/TLRPC$TL_invoice:phone_requested	Z
    //   5971: ifne -569 -> 5402
    //   5974: iload_3
    //   5975: istore 4
    //   5977: aload_0
    //   5978: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   5981: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   5984: getfield 353	org/vidogram/tgnet/TLRPC$TL_invoice:email_requested	Z
    //   5987: ifne -585 -> 5402
    //   5990: iconst_0
    //   5991: istore 4
    //   5993: goto -591 -> 5402
    //   5996: aload_0
    //   5997: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   6000: iload 5
    //   6002: aaload
    //   6003: sipush 129
    //   6006: invokevirtual 1229	android/widget/EditText:setInputType	(I)V
    //   6009: aload_0
    //   6010: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   6013: iload 5
    //   6015: aaload
    //   6016: getstatic 1491	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   6019: invokevirtual 1495	android/widget/EditText:setTypeface	(Landroid/graphics/Typeface;)V
    //   6022: goto -435 -> 5587
    //   6025: aload_0
    //   6026: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   6029: iload 5
    //   6031: aaload
    //   6032: aload_0
    //   6033: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6036: getfield 367	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:saved_credentials	Lorg/vidogram/tgnet/TLRPC$TL_paymentSavedCredentialsCard;
    //   6039: getfield 717	org/vidogram/tgnet/TLRPC$TL_paymentSavedCredentialsCard:title	Ljava/lang/String;
    //   6042: invokevirtual 1348	android/widget/EditText:setText	(Ljava/lang/CharSequence;)V
    //   6045: goto -421 -> 5624
    //   6048: aload_0
    //   6049: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   6052: iload 5
    //   6054: aaload
    //   6055: ldc_w 1576
    //   6058: ldc_w 1577
    //   6061: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6064: invokevirtual 1344	android/widget/EditText:setHint	(Ljava/lang/CharSequence;)V
    //   6067: aload_0
    //   6068: getfield 416	org/vidogram/ui/PaymentFormActivity:inputFields	[Landroid/widget/EditText;
    //   6071: iload 5
    //   6073: aaload
    //   6074: invokevirtual 1580	android/widget/EditText:requestFocus	()Z
    //   6077: pop
    //   6078: goto -454 -> 5624
    //   6081: iconst_3
    //   6082: istore_3
    //   6083: goto -423 -> 5660
    //   6086: aload_0
    //   6087: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   6090: iconst_4
    //   6091: if_icmpeq +11 -> 6102
    //   6094: aload_0
    //   6095: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   6098: iconst_5
    //   6099: if_icmpne -2906 -> 3193
    //   6102: aload_0
    //   6103: new 1582	org/vidogram/ui/Cells/PaymentInfoCell
    //   6106: dup
    //   6107: aload_1
    //   6108: invokespecial 1583	org/vidogram/ui/Cells/PaymentInfoCell:<init>	(Landroid/content/Context;)V
    //   6111: putfield 1585	org/vidogram/ui/PaymentFormActivity:paymentInfoCell	Lorg/vidogram/ui/Cells/PaymentInfoCell;
    //   6114: aload_0
    //   6115: getfield 1585	org/vidogram/ui/PaymentFormActivity:paymentInfoCell	Lorg/vidogram/ui/Cells/PaymentInfoCell;
    //   6118: ldc_w 1180
    //   6121: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6124: invokevirtual 1586	org/vidogram/ui/Cells/PaymentInfoCell:setBackgroundColor	(I)V
    //   6127: aload_0
    //   6128: getfield 1585	org/vidogram/ui/PaymentFormActivity:paymentInfoCell	Lorg/vidogram/ui/Cells/PaymentInfoCell;
    //   6131: aload_0
    //   6132: getfield 284	org/vidogram/ui/PaymentFormActivity:messageObject	Lorg/vidogram/messenger/MessageObject;
    //   6135: getfield 313	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   6138: getfield 319	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   6141: checkcast 1588	org/vidogram/tgnet/TLRPC$TL_messageMediaInvoice
    //   6144: aload_0
    //   6145: getfield 307	org/vidogram/ui/PaymentFormActivity:currentBotName	Ljava/lang/String;
    //   6148: invokevirtual 1592	org/vidogram/ui/Cells/PaymentInfoCell:setInvoice	(Lorg/vidogram/tgnet/TLRPC$TL_messageMediaInvoice;Ljava/lang/String;)V
    //   6151: aload_0
    //   6152: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6155: aload_0
    //   6156: getfield 1585	org/vidogram/ui/PaymentFormActivity:paymentInfoCell	Lorg/vidogram/ui/Cells/PaymentInfoCell;
    //   6159: iconst_m1
    //   6160: bipush 254
    //   6162: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6165: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6168: aload_0
    //   6169: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   6172: iconst_0
    //   6173: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   6176: dup
    //   6177: aload_1
    //   6178: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   6181: aastore
    //   6182: aload_0
    //   6183: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6186: aload_0
    //   6187: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   6190: iconst_0
    //   6191: aaload
    //   6192: iconst_m1
    //   6193: bipush 254
    //   6195: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   6198: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6201: new 221	java/util/ArrayList
    //   6204: dup
    //   6205: invokespecial 222	java/util/ArrayList:<init>	()V
    //   6208: astore 10
    //   6210: aload 10
    //   6212: aload_0
    //   6213: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6216: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   6219: getfield 1593	org/vidogram/tgnet/TLRPC$TL_invoice:prices	Ljava/util/ArrayList;
    //   6222: invokevirtual 1597	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   6225: pop
    //   6226: aload_0
    //   6227: getfield 282	org/vidogram/ui/PaymentFormActivity:shippingOption	Lorg/vidogram/tgnet/TLRPC$TL_shippingOption;
    //   6230: ifnull +16 -> 6246
    //   6233: aload 10
    //   6235: aload_0
    //   6236: getfield 282	org/vidogram/ui/PaymentFormActivity:shippingOption	Lorg/vidogram/tgnet/TLRPC$TL_shippingOption;
    //   6239: getfield 1537	org/vidogram/tgnet/TLRPC$TL_shippingOption:prices	Ljava/util/ArrayList;
    //   6242: invokevirtual 1597	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   6245: pop
    //   6246: aload_0
    //   6247: aload 10
    //   6249: invokespecial 1539	org/vidogram/ui/PaymentFormActivity:getTotalPriceString	(Ljava/util/ArrayList;)Ljava/lang/String;
    //   6252: astore 9
    //   6254: iconst_0
    //   6255: istore_3
    //   6256: iload_3
    //   6257: aload 10
    //   6259: invokevirtual 655	java/util/ArrayList:size	()I
    //   6262: if_icmpge +83 -> 6345
    //   6265: aload 10
    //   6267: iload_3
    //   6268: invokevirtual 659	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6271: checkcast 661	org/vidogram/tgnet/TLRPC$TL_labeledPrice
    //   6274: astore 11
    //   6276: new 1599	org/vidogram/ui/Cells/TextPriceCell
    //   6279: dup
    //   6280: aload_1
    //   6281: invokespecial 1600	org/vidogram/ui/Cells/TextPriceCell:<init>	(Landroid/content/Context;)V
    //   6284: astore 12
    //   6286: aload 12
    //   6288: ldc_w 1180
    //   6291: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6294: invokevirtual 1601	org/vidogram/ui/Cells/TextPriceCell:setBackgroundColor	(I)V
    //   6297: aload 12
    //   6299: aload 11
    //   6301: getfield 1604	org/vidogram/tgnet/TLRPC$TL_labeledPrice:label	Ljava/lang/String;
    //   6304: invokestatic 670	org/vidogram/messenger/LocaleController:getInstance	()Lorg/vidogram/messenger/LocaleController;
    //   6307: aload 11
    //   6309: getfield 665	org/vidogram/tgnet/TLRPC$TL_labeledPrice:amount	J
    //   6312: aload_0
    //   6313: getfield 258	org/vidogram/ui/PaymentFormActivity:paymentForm	Lorg/vidogram/tgnet/TLRPC$TL_payments_paymentForm;
    //   6316: getfield 269	org/vidogram/tgnet/TLRPC$TL_payments_paymentForm:invoice	Lorg/vidogram/tgnet/TLRPC$TL_invoice;
    //   6319: getfield 673	org/vidogram/tgnet/TLRPC$TL_invoice:currency	Ljava/lang/String;
    //   6322: invokevirtual 683	org/vidogram/messenger/LocaleController:formatCurrencyString	(JLjava/lang/String;)Ljava/lang/String;
    //   6325: iconst_0
    //   6326: invokevirtual 1607	org/vidogram/ui/Cells/TextPriceCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6329: aload_0
    //   6330: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6333: aload 12
    //   6335: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6338: iload_3
    //   6339: iconst_1
    //   6340: iadd
    //   6341: istore_3
    //   6342: goto -86 -> 6256
    //   6345: new 1599	org/vidogram/ui/Cells/TextPriceCell
    //   6348: dup
    //   6349: aload_1
    //   6350: invokespecial 1600	org/vidogram/ui/Cells/TextPriceCell:<init>	(Landroid/content/Context;)V
    //   6353: astore 10
    //   6355: aload 10
    //   6357: ldc_w 1180
    //   6360: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6363: invokevirtual 1601	org/vidogram/ui/Cells/TextPriceCell:setBackgroundColor	(I)V
    //   6366: aload 10
    //   6368: ldc_w 1609
    //   6371: ldc_w 1610
    //   6374: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6377: aload 9
    //   6379: iconst_1
    //   6380: invokevirtual 1607	org/vidogram/ui/Cells/TextPriceCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6383: aload_0
    //   6384: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6387: aload 10
    //   6389: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6392: new 872	android/view/View
    //   6395: dup
    //   6396: aload_1
    //   6397: invokespecial 1329	android/view/View:<init>	(Landroid/content/Context;)V
    //   6400: astore 10
    //   6402: aload_0
    //   6403: getfield 239	org/vidogram/ui/PaymentFormActivity:dividers	Ljava/util/ArrayList;
    //   6406: aload 10
    //   6408: invokevirtual 1332	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   6411: pop
    //   6412: aload 10
    //   6414: ldc_w 1334
    //   6417: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6420: invokevirtual 1096	android/view/View:setBackgroundColor	(I)V
    //   6423: aload_0
    //   6424: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6427: aload 10
    //   6429: new 1125	android/widget/FrameLayout$LayoutParams
    //   6432: dup
    //   6433: iconst_m1
    //   6434: iconst_1
    //   6435: bipush 83
    //   6437: invokespecial 1337	android/widget/FrameLayout$LayoutParams:<init>	(III)V
    //   6440: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   6443: aload_0
    //   6444: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6447: iconst_0
    //   6448: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6451: dup
    //   6452: aload_1
    //   6453: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   6456: aastore
    //   6457: aload_0
    //   6458: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6461: iconst_0
    //   6462: aaload
    //   6463: iconst_1
    //   6464: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   6467: invokevirtual 1612	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   6470: aload_0
    //   6471: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6474: iconst_0
    //   6475: aaload
    //   6476: aload_0
    //   6477: getfield 341	org/vidogram/ui/PaymentFormActivity:cardName	Ljava/lang/String;
    //   6480: ldc_w 1614
    //   6483: ldc_w 1615
    //   6486: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6489: iconst_1
    //   6490: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6493: aload_0
    //   6494: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6497: aload_0
    //   6498: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6501: iconst_0
    //   6502: aaload
    //   6503: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6506: aload_0
    //   6507: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   6510: iconst_4
    //   6511: if_icmpne +20 -> 6531
    //   6514: aload_0
    //   6515: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6518: iconst_0
    //   6519: aaload
    //   6520: new 30	org/vidogram/ui/PaymentFormActivity$19
    //   6523: dup
    //   6524: aload_0
    //   6525: invokespecial 1617	org/vidogram/ui/PaymentFormActivity$19:<init>	(Lorg/vidogram/ui/PaymentFormActivity;)V
    //   6528: invokevirtual 1618	org/vidogram/ui/Cells/TextDetailSettingsCell:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   6531: aload_0
    //   6532: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6535: ifnull +521 -> 7056
    //   6538: aload_0
    //   6539: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6542: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6545: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6548: ifnull +175 -> 6723
    //   6551: ldc_w 1620
    //   6554: bipush 6
    //   6556: anewarray 584	java/lang/Object
    //   6559: dup
    //   6560: iconst_0
    //   6561: aload_0
    //   6562: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6565: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6568: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6571: getfield 635	org/vidogram/tgnet/TLRPC$TL_postAddress:street_line1	Ljava/lang/String;
    //   6574: aastore
    //   6575: dup
    //   6576: iconst_1
    //   6577: aload_0
    //   6578: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6581: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6584: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6587: getfield 638	org/vidogram/tgnet/TLRPC$TL_postAddress:street_line2	Ljava/lang/String;
    //   6590: aastore
    //   6591: dup
    //   6592: iconst_2
    //   6593: aload_0
    //   6594: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6597: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6600: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6603: getfield 641	org/vidogram/tgnet/TLRPC$TL_postAddress:city	Ljava/lang/String;
    //   6606: aastore
    //   6607: dup
    //   6608: iconst_3
    //   6609: aload_0
    //   6610: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6613: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6616: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6619: getfield 644	org/vidogram/tgnet/TLRPC$TL_postAddress:state	Ljava/lang/String;
    //   6622: aastore
    //   6623: dup
    //   6624: iconst_4
    //   6625: aload_0
    //   6626: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6629: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6632: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6635: getfield 647	org/vidogram/tgnet/TLRPC$TL_postAddress:country_iso2	Ljava/lang/String;
    //   6638: aastore
    //   6639: dup
    //   6640: iconst_5
    //   6641: aload_0
    //   6642: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6645: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6648: getfield 632	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:shipping_address	Lorg/vidogram/tgnet/TLRPC$TL_postAddress;
    //   6651: getfield 650	org/vidogram/tgnet/TLRPC$TL_postAddress:post_code	Ljava/lang/String;
    //   6654: aastore
    //   6655: invokestatic 1544	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   6658: astore 10
    //   6660: aload_0
    //   6661: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6664: iconst_1
    //   6665: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6668: dup
    //   6669: aload_1
    //   6670: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   6673: aastore
    //   6674: aload_0
    //   6675: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6678: iconst_1
    //   6679: aaload
    //   6680: ldc_w 1180
    //   6683: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6686: invokevirtual 1621	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   6689: aload_0
    //   6690: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6693: iconst_1
    //   6694: aaload
    //   6695: aload 10
    //   6697: ldc_w 1183
    //   6700: ldc_w 1184
    //   6703: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6706: iconst_1
    //   6707: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6710: aload_0
    //   6711: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6714: aload_0
    //   6715: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6718: iconst_1
    //   6719: aaload
    //   6720: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6723: aload_0
    //   6724: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6727: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6730: getfield 606	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   6733: ifnull +74 -> 6807
    //   6736: aload_0
    //   6737: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6740: iconst_2
    //   6741: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6744: dup
    //   6745: aload_1
    //   6746: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   6749: aastore
    //   6750: aload_0
    //   6751: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6754: iconst_2
    //   6755: aaload
    //   6756: ldc_w 1180
    //   6759: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6762: invokevirtual 1621	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   6765: aload_0
    //   6766: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6769: iconst_2
    //   6770: aaload
    //   6771: aload_0
    //   6772: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6775: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6778: getfield 606	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:name	Ljava/lang/String;
    //   6781: ldc_w 1623
    //   6784: ldc_w 1624
    //   6787: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6790: iconst_1
    //   6791: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6794: aload_0
    //   6795: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6798: aload_0
    //   6799: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6802: iconst_2
    //   6803: aaload
    //   6804: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6807: aload_0
    //   6808: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6811: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6814: getfield 622	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   6817: ifnull +80 -> 6897
    //   6820: aload_0
    //   6821: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6824: iconst_3
    //   6825: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6828: dup
    //   6829: aload_1
    //   6830: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   6833: aastore
    //   6834: aload_0
    //   6835: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6838: iconst_3
    //   6839: aaload
    //   6840: ldc_w 1180
    //   6843: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6846: invokevirtual 1621	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   6849: aload_0
    //   6850: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6853: iconst_3
    //   6854: aaload
    //   6855: invokestatic 1629	org/vidogram/a/b:a	()Lorg/vidogram/a/b;
    //   6858: aload_0
    //   6859: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6862: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6865: getfield 622	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:phone	Ljava/lang/String;
    //   6868: invokevirtual 1631	org/vidogram/a/b:e	(Ljava/lang/String;)Ljava/lang/String;
    //   6871: ldc_w 1633
    //   6874: ldc_w 1634
    //   6877: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6880: iconst_1
    //   6881: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6884: aload_0
    //   6885: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6888: aload_0
    //   6889: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6892: iconst_3
    //   6893: aaload
    //   6894: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6897: aload_0
    //   6898: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6901: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6904: getfield 625	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   6907: ifnull +74 -> 6981
    //   6910: aload_0
    //   6911: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6914: iconst_4
    //   6915: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6918: dup
    //   6919: aload_1
    //   6920: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   6923: aastore
    //   6924: aload_0
    //   6925: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6928: iconst_4
    //   6929: aaload
    //   6930: ldc_w 1180
    //   6933: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   6936: invokevirtual 1621	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   6939: aload_0
    //   6940: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6943: iconst_4
    //   6944: aaload
    //   6945: aload_0
    //   6946: getfield 335	org/vidogram/ui/PaymentFormActivity:validateRequest	Lorg/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo;
    //   6949: getfield 336	org/vidogram/tgnet/TLRPC$TL_payments_validateRequestedInfo:info	Lorg/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo;
    //   6952: getfield 625	org/vidogram/tgnet/TLRPC$TL_paymentRequestedInfo:email	Ljava/lang/String;
    //   6955: ldc_w 1636
    //   6958: ldc_w 1637
    //   6961: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   6964: iconst_1
    //   6965: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   6968: aload_0
    //   6969: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   6972: aload_0
    //   6973: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6976: iconst_4
    //   6977: aaload
    //   6978: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   6981: aload_0
    //   6982: getfield 282	org/vidogram/ui/PaymentFormActivity:shippingOption	Lorg/vidogram/tgnet/TLRPC$TL_shippingOption;
    //   6985: ifnull +71 -> 7056
    //   6988: aload_0
    //   6989: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   6992: iconst_5
    //   6993: new 249	org/vidogram/ui/Cells/TextDetailSettingsCell
    //   6996: dup
    //   6997: aload_1
    //   6998: invokespecial 1611	org/vidogram/ui/Cells/TextDetailSettingsCell:<init>	(Landroid/content/Context;)V
    //   7001: aastore
    //   7002: aload_0
    //   7003: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   7006: iconst_5
    //   7007: aaload
    //   7008: ldc_w 1180
    //   7011: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7014: invokevirtual 1621	org/vidogram/ui/Cells/TextDetailSettingsCell:setBackgroundColor	(I)V
    //   7017: aload_0
    //   7018: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   7021: iconst_5
    //   7022: aaload
    //   7023: aload_0
    //   7024: getfield 282	org/vidogram/ui/PaymentFormActivity:shippingOption	Lorg/vidogram/tgnet/TLRPC$TL_shippingOption;
    //   7027: getfield 1540	org/vidogram/tgnet/TLRPC$TL_shippingOption:title	Ljava/lang/String;
    //   7030: ldc_w 1639
    //   7033: ldc_w 1640
    //   7036: invokestatic 958	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7039: iconst_0
    //   7040: invokevirtual 1616	org/vidogram/ui/Cells/TextDetailSettingsCell:setTextAndValue	(Ljava/lang/String;Ljava/lang/String;Z)V
    //   7043: aload_0
    //   7044: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7047: aload_0
    //   7048: getfield 251	org/vidogram/ui/PaymentFormActivity:detailSettingsCell	[Lorg/vidogram/ui/Cells/TextDetailSettingsCell;
    //   7051: iconst_5
    //   7052: aaload
    //   7053: invokevirtual 1551	android/widget/LinearLayout:addView	(Landroid/view/View;)V
    //   7056: aload_0
    //   7057: getfield 253	org/vidogram/ui/PaymentFormActivity:currentStep	I
    //   7060: iconst_4
    //   7061: if_icmpne +240 -> 7301
    //   7064: aload_0
    //   7065: new 944	android/widget/FrameLayout
    //   7068: dup
    //   7069: aload_1
    //   7070: invokespecial 1083	android/widget/FrameLayout:<init>	(Landroid/content/Context;)V
    //   7073: putfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7076: aload_0
    //   7077: getfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7080: iconst_1
    //   7081: invokestatic 1283	org/vidogram/ui/ActionBar/Theme:getSelectorDrawable	(Z)Landroid/graphics/drawable/Drawable;
    //   7084: invokevirtual 1641	android/widget/FrameLayout:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   7087: aload 8
    //   7089: aload_0
    //   7090: getfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7093: iconst_m1
    //   7094: bipush 48
    //   7096: bipush 80
    //   7098: invokestatic 1644	org/vidogram/ui/Components/LayoutHelper:createFrame	(III)Landroid/widget/FrameLayout$LayoutParams;
    //   7101: invokevirtual 1115	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7104: aload_0
    //   7105: getfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7108: new 36	org/vidogram/ui/PaymentFormActivity$20
    //   7111: dup
    //   7112: aload_0
    //   7113: aload 9
    //   7115: invokespecial 1647	org/vidogram/ui/PaymentFormActivity$20:<init>	(Lorg/vidogram/ui/PaymentFormActivity;Ljava/lang/String;)V
    //   7118: invokevirtual 1648	android/widget/FrameLayout:setOnClickListener	(Landroid/view/View$OnClickListener;)V
    //   7121: aload_0
    //   7122: new 948	android/widget/TextView
    //   7125: dup
    //   7126: aload_1
    //   7127: invokespecial 1237	android/widget/TextView:<init>	(Landroid/content/Context;)V
    //   7130: putfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7133: aload_0
    //   7134: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7137: ldc_w 1650
    //   7140: invokestatic 1093	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   7143: invokevirtual 1241	android/widget/TextView:setTextColor	(I)V
    //   7146: aload_0
    //   7147: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7150: ldc_w 1652
    //   7153: ldc_w 1653
    //   7156: iconst_1
    //   7157: anewarray 584	java/lang/Object
    //   7160: dup
    //   7161: iconst_0
    //   7162: aload 9
    //   7164: aastore
    //   7165: invokestatic 1560	org/vidogram/messenger/LocaleController:formatString	(Ljava/lang/String;I[Ljava/lang/Object;)Ljava/lang/String;
    //   7168: invokevirtual 1240	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   7171: aload_0
    //   7172: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7175: iconst_1
    //   7176: ldc_w 1654
    //   7179: invokevirtual 1242	android/widget/TextView:setTextSize	(IF)V
    //   7182: aload_0
    //   7183: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7186: bipush 17
    //   7188: invokevirtual 1655	android/widget/TextView:setGravity	(I)V
    //   7191: aload_0
    //   7192: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7195: ldc_w 1657
    //   7198: invokestatic 1661	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   7201: invokevirtual 1662	android/widget/TextView:setTypeface	(Landroid/graphics/Typeface;)V
    //   7204: aload_0
    //   7205: getfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7208: aload_0
    //   7209: getfield 523	org/vidogram/ui/PaymentFormActivity:payTextView	Landroid/widget/TextView;
    //   7212: iconst_m1
    //   7213: ldc_w 1070
    //   7216: invokestatic 1076	org/vidogram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   7219: invokevirtual 1115	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7222: aload_0
    //   7223: new 891	org/vidogram/ui/Components/ContextProgressView
    //   7226: dup
    //   7227: aload_1
    //   7228: iconst_0
    //   7229: invokespecial 1069	org/vidogram/ui/Components/ContextProgressView:<init>	(Landroid/content/Context;I)V
    //   7232: putfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   7235: aload_0
    //   7236: getfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   7239: iconst_4
    //   7240: invokevirtual 894	org/vidogram/ui/Components/ContextProgressView:setVisibility	(I)V
    //   7243: aload_0
    //   7244: getfield 942	org/vidogram/ui/PaymentFormActivity:bottomLayout	Landroid/widget/FrameLayout;
    //   7247: aload_0
    //   7248: getfield 519	org/vidogram/ui/PaymentFormActivity:progressView	Lorg/vidogram/ui/Components/ContextProgressView;
    //   7251: iconst_m1
    //   7252: ldc_w 1070
    //   7255: invokestatic 1076	org/vidogram/ui/Components/LayoutHelper:createFrame	(IF)Landroid/widget/FrameLayout$LayoutParams;
    //   7258: invokevirtual 1115	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7261: new 872	android/view/View
    //   7264: dup
    //   7265: aload_1
    //   7266: invokespecial 1329	android/view/View:<init>	(Landroid/content/Context;)V
    //   7269: astore 9
    //   7271: aload 9
    //   7273: ldc_w 1663
    //   7276: invokevirtual 1666	android/view/View:setBackgroundResource	(I)V
    //   7279: aload 8
    //   7281: aload 9
    //   7283: iconst_m1
    //   7284: ldc_w 1667
    //   7287: bipush 83
    //   7289: fconst_0
    //   7290: fconst_0
    //   7291: fconst_0
    //   7292: ldc_w 1111
    //   7295: invokestatic 1114	org/vidogram/ui/Components/LayoutHelper:createFrame	(IFIFFFF)Landroid/widget/FrameLayout$LayoutParams;
    //   7298: invokevirtual 1115	android/widget/FrameLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7301: aload_0
    //   7302: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   7305: iconst_1
    //   7306: new 241	org/vidogram/ui/Cells/ShadowSectionCell
    //   7309: dup
    //   7310: aload_1
    //   7311: invokespecial 1278	org/vidogram/ui/Cells/ShadowSectionCell:<init>	(Landroid/content/Context;)V
    //   7314: aastore
    //   7315: aload_0
    //   7316: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   7319: iconst_1
    //   7320: aaload
    //   7321: aload_1
    //   7322: ldc_w 1026
    //   7325: ldc_w 1015
    //   7328: invokestatic 1021	org/vidogram/ui/ActionBar/Theme:getThemedDrawable	(Landroid/content/Context;ILjava/lang/String;)Landroid/graphics/drawable/Drawable;
    //   7331: invokevirtual 1025	org/vidogram/ui/Cells/ShadowSectionCell:setBackgroundDrawable	(Landroid/graphics/drawable/Drawable;)V
    //   7334: aload_0
    //   7335: getfield 1120	org/vidogram/ui/PaymentFormActivity:linearLayout2	Landroid/widget/LinearLayout;
    //   7338: aload_0
    //   7339: getfield 243	org/vidogram/ui/PaymentFormActivity:sectionCell	[Lorg/vidogram/ui/Cells/ShadowSectionCell;
    //   7342: iconst_1
    //   7343: aaload
    //   7344: iconst_m1
    //   7345: bipush 254
    //   7347: invokestatic 1190	org/vidogram/ui/Components/LayoutHelper:createLinear	(II)Landroid/widget/LinearLayout$LayoutParams;
    //   7350: invokevirtual 1191	android/widget/LinearLayout:addView	(Landroid/view/View;Landroid/view/ViewGroup$LayoutParams;)V
    //   7353: goto -4160 -> 3193
    //
    // Exception table:
    //   from	to	target	type
    //   325	354	474	java/lang/Exception
    //   354	361	474	java/lang/Exception
    //   366	457	474	java/lang/Exception
    //   457	471	474	java/lang/Exception
    //   1525	1530	474	java/lang/Exception
    //   3117	3130	3291	java/lang/Exception
    //   3134	3142	3291	java/lang/Exception
    //   3665	3677	4324	java/lang/Exception
    //   3646	3665	4334	java/lang/Exception
    //   4326	4331	4334	java/lang/Exception
    //   4346	4351	4334	java/lang/Exception
    //   4356	4361	4334	java/lang/Exception
    //   4366	4373	4334	java/lang/Exception
    //   3677	3689	4344	java/lang/Exception
    //   3689	3701	4354	java/lang/Exception
    //   3701	3713	4364	java/lang/Exception
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.didSetTwoStepPassword)
    {
      this.paymentForm.password_missing = false;
      updateSavePaymentField();
    }
    do
    {
      return;
      if (paramInt != NotificationCenter.didRemovedTwoStepPassword)
        continue;
      this.paymentForm.password_missing = true;
      updateSavePaymentField();
      return;
    }
    while (paramInt != NotificationCenter.paymentFinished);
    removeSelfFromStack();
  }

  @SuppressLint({"HardwareIds"})
  public void fillNumber(String paramString)
  {
    int m = 4;
    int k = 1;
    while (true)
    {
      try
      {
        Object localObject = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
        if ((paramString == null) && ((((TelephonyManager)localObject).getSimState() == 1) || (((TelephonyManager)localObject).getPhoneType() == 0)))
          continue;
        if (Build.VERSION.SDK_INT < 23)
          break label279;
        if (getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") != 0)
          continue;
        i = 1;
        if (getParentActivity().checkSelfPermission("android.permission.RECEIVE_SMS") != 0)
          continue;
        j = 1;
        break label283;
        String str = paramString;
        if (paramString != null)
          continue;
        str = org.vidogram.a.b.b(((TelephonyManager)localObject).getLine1Number());
        if (TextUtils.isEmpty(str))
          continue;
        if (str.length() <= 4)
          break label274;
        i = m;
        if (i >= 1)
        {
          localObject = str.substring(0, i);
          if ((String)this.codesMap.get(localObject) == null)
            continue;
          paramString = str.substring(i, str.length());
          this.inputFields[8].setText((CharSequence)localObject);
          i = k;
          if (i != 0)
            continue;
          paramString = str.substring(1, str.length());
          this.inputFields[8].setText(str.substring(0, 1));
          if (paramString == null)
            continue;
          this.inputFields[9].setText(paramString);
          this.inputFields[9].setSelection(this.inputFields[9].length());
          return;
          i = 0;
          continue;
          j = 0;
          break label283;
          i -= 1;
          continue;
        }
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        return;
      }
      int i = 0;
      paramString = null;
      continue;
      label274: paramString = null;
      continue;
      label279: int j = 1;
      i = 1;
      label283: if ((paramString != null) || (i != 0))
        continue;
      if (j == 0)
        continue;
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ArrayList localArrayList = new ArrayList();
    localArrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch"));
    localArrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder"));
    LinearLayout localLinearLayout = this.linearLayout2;
    Paint localPaint = Theme.dividerPaint;
    localArrayList.add(new ThemeDescription(localLinearLayout, 0, new Class[] { View.class }, localPaint, null, null, "divider"));
    localArrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"));
    localArrayList.add(new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"));
    if (this.inputFields != null)
    {
      i = 0;
      while (i < this.inputFields.length)
      {
        localArrayList.add(new ThemeDescription((View)this.inputFields[i].getParent(), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
        localArrayList.add(new ThemeDescription(this.inputFields[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
        i += 1;
      }
    }
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"));
    if (this.radioCells != null)
    {
      i = 0;
      while (i < this.radioCells.length)
      {
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"));
        localArrayList.add(new ThemeDescription(this.radioCells[i], ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"));
        i += 1;
      }
    }
    localArrayList.add(new ThemeDescription(null, 0, new Class[] { RadioCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"));
    localArrayList.add(new ThemeDescription(null, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"));
    int i = 0;
    while (i < this.headerCell.length)
    {
      localArrayList.add(new ThemeDescription(this.headerCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
      localArrayList.add(new ThemeDescription(this.headerCell[i], 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"));
      i += 1;
    }
    i = 0;
    while (i < this.sectionCell.length)
    {
      localArrayList.add(new ThemeDescription(this.sectionCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"));
      i += 1;
    }
    i = 0;
    while (i < this.bottomCell.length)
    {
      localArrayList.add(new ThemeDescription(this.bottomCell[i], ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"));
      localArrayList.add(new ThemeDescription(this.bottomCell[i], 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"));
      localArrayList.add(new ThemeDescription(this.bottomCell[i], ThemeDescription.FLAG_LINKCOLOR, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteLinkText"));
      i += 1;
    }
    i = 0;
    while (i < this.dividers.size())
    {
      localArrayList.add(new ThemeDescription((View)this.dividers.get(i), ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"));
      i += 1;
    }
    localArrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.checkCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    localArrayList.add(new ThemeDescription(this.settingsCell1, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.payTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlueText6"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextPriceCell.class }, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextPriceCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.detailSettingsCell[0], ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    i = 1;
    while (i < this.detailSettingsCell.length)
    {
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"));
      localArrayList.add(new ThemeDescription(this.detailSettingsCell[i], 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
      i += 1;
    }
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "detailTextView" }, null, null, null, "windowBackgroundWhiteBlackText"));
    localArrayList.add(new ThemeDescription(this.paymentInfoCell, 0, new Class[] { PaymentInfoCell.class }, new String[] { "detailExTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"));
    localArrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "windowBackgroundWhite"));
    localArrayList.add(new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_SELECTORWHITE, null, null, null, null, "listSelectorSDK21"));
    return (ThemeDescription[])localArrayList.toArray(new ThemeDescription[localArrayList.size()]);
  }

  public boolean onBackPressed()
  {
    return !this.donePressed;
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didSetTwoStepPassword);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didRemovedTwoStepPassword);
    if (this.currentStep != 4)
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.paymentFinished);
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didSetTwoStepPassword);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didRemovedTwoStepPassword);
    if (this.currentStep != 4)
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.paymentFinished);
    if (this.webView != null);
    try
    {
      ViewParent localViewParent = this.webView.getParent();
      if (localViewParent != null)
        ((FrameLayout)localViewParent).removeView(this.webView);
      this.webView.stopLoading();
      this.webView.loadUrl("about:blank");
      this.webView.destroy();
      this.webView = null;
    }
    catch (Exception localThrowable)
    {
      try
      {
        while (true)
        {
          if ((this.currentStep == 2) && (Build.VERSION.SDK_INT >= 23) && ((UserConfig.passcodeHash.length() == 0) || (UserConfig.allowScreenCapture)))
            getParentActivity().getWindow().clearFlags(8192);
          super.onFragmentDestroy();
          this.canceled = true;
          return;
          localException = localException;
          FileLog.e(localException);
        }
      }
      catch (Throwable localThrowable)
      {
        while (true)
          FileLog.e(localThrowable);
      }
    }
  }

  public void onPause()
  {
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
    if (Build.VERSION.SDK_INT >= 23)
      try
      {
        if (this.currentStep == 2)
        {
          getParentActivity().getWindow().setFlags(8192, 8192);
          return;
        }
        if ((UserConfig.passcodeHash.length() == 0) || (UserConfig.allowScreenCapture))
        {
          getParentActivity().getWindow().clearFlags(8192);
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
      }
  }

  protected void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (!paramBoolean2))
    {
      if (this.webView == null)
        break label30;
      this.webView.loadUrl(this.paymentForm.url);
    }
    label30: 
    do
    {
      return;
      if (this.currentStep != 2)
        continue;
      this.inputFields[0].requestFocus();
      AndroidUtilities.showKeyboard(this.inputFields[0]);
      return;
    }
    while (this.currentStep != 3);
    this.inputFields[1].requestFocus();
    AndroidUtilities.showKeyboard(this.inputFields[1]);
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
    public LinkSpan()
    {
    }

    public void onClick(View paramView)
    {
      PaymentFormActivity.this.presentFragment(new TwoStepVerificationActivity(0));
    }

    public void updateDrawState(TextPaint paramTextPaint)
    {
      super.updateDrawState(paramTextPaint);
      paramTextPaint.setUnderlineText(false);
    }
  }

  private static abstract interface PaymentFormActivityDelegate
  {
    public abstract void didSelectNewCard(String paramString1, String paramString2, boolean paramBoolean);
  }

  private class TelegramWebviewProxy
  {
    private TelegramWebviewProxy()
    {
    }

    @JavascriptInterface
    public void postEvent(String paramString1, String paramString2)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramString1, paramString2)
      {
        public void run()
        {
          if (PaymentFormActivity.this.getParentActivity() == null);
          do
            return;
          while (!this.val$eventName.equals("payment_form_submit"));
          try
          {
            JSONObject localJSONObject1 = new JSONObject(this.val$eventData);
            JSONObject localJSONObject2 = localJSONObject1.getJSONObject("credentials");
            PaymentFormActivity.access$002(PaymentFormActivity.this, localJSONObject2.toString());
            PaymentFormActivity.access$102(PaymentFormActivity.this, localJSONObject1.getString("title"));
            PaymentFormActivity.this.goToNextStep();
            return;
          }
          catch (Throwable localThrowable)
          {
            while (true)
            {
              PaymentFormActivity.access$002(PaymentFormActivity.this, this.val$eventData);
              FileLog.e(localThrowable);
            }
          }
        }
      });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PaymentFormActivity
 * JD-Core Version:    0.6.0
 */