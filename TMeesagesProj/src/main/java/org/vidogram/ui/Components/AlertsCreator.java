package org.vidogram.ui.Components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationsController;
import org.vidogram.messenger.SecretChatHelper;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.TL_account_changePhone;
import org.vidogram.tgnet.TLRPC.TL_account_confirmPhone;
import org.vidogram.tgnet.TLRPC.TL_account_getPassword;
import org.vidogram.tgnet.TLRPC.TL_account_getTmpPassword;
import org.vidogram.tgnet.TLRPC.TL_account_reportPeer;
import org.vidogram.tgnet.TLRPC.TL_account_sendChangePhoneCode;
import org.vidogram.tgnet.TLRPC.TL_account_sendConfirmPhoneCode;
import org.vidogram.tgnet.TLRPC.TL_auth_resendCode;
import org.vidogram.tgnet.TLRPC.TL_channels_createChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_editAdmin;
import org.vidogram.tgnet.TLRPC.TL_channels_inviteToChannel;
import org.vidogram.tgnet.TLRPC.TL_channels_joinChannel;
import org.vidogram.tgnet.TLRPC.TL_contacts_importContacts;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_geochats_sendMedia;
import org.vidogram.tgnet.TLRPC.TL_geochats_sendMessage;
import org.vidogram.tgnet.TLRPC.TL_inputReportReasonPornography;
import org.vidogram.tgnet.TLRPC.TL_inputReportReasonSpam;
import org.vidogram.tgnet.TLRPC.TL_inputReportReasonViolence;
import org.vidogram.tgnet.TLRPC.TL_messages_addChatUser;
import org.vidogram.tgnet.TLRPC.TL_messages_createChat;
import org.vidogram.tgnet.TLRPC.TL_messages_editMessage;
import org.vidogram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_getAttachedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_importChatInvite;
import org.vidogram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.vidogram.tgnet.TLRPC.TL_messages_sendInlineBotResult;
import org.vidogram.tgnet.TLRPC.TL_messages_sendMedia;
import org.vidogram.tgnet.TLRPC.TL_messages_sendMessage;
import org.vidogram.tgnet.TLRPC.TL_messages_startBot;
import org.vidogram.tgnet.TLRPC.TL_payments_sendPaymentForm;
import org.vidogram.tgnet.TLRPC.TL_payments_validateRequestedInfo;
import org.vidogram.tgnet.TLRPC.TL_peerNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_updateUserName;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.Cells.RadioColorCell;
import org.vidogram.ui.ReportOtherActivity;

public class AlertsCreator
{
  public static Dialog createColorSelectDialog(Activity paramActivity, long paramLong, boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int i;
    int[] arrayOfInt;
    int j;
    label139: RadioColorCell localRadioColorCell;
    String str10;
    if (paramBoolean1)
    {
      i = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
      localObject = new LinearLayout(paramActivity);
      ((LinearLayout)localObject).setOrientation(1);
      String str1 = LocaleController.getString("ColorRed", 2131165565);
      String str2 = LocaleController.getString("ColorOrange", 2131165563);
      String str3 = LocaleController.getString("ColorYellow", 2131165568);
      String str4 = LocaleController.getString("ColorGreen", 2131165562);
      String str5 = LocaleController.getString("ColorCyan", 2131165561);
      String str6 = LocaleController.getString("ColorBlue", 2131165560);
      String str7 = LocaleController.getString("ColorViolet", 2131165566);
      String str8 = LocaleController.getString("ColorPink", 2131165564);
      String str9 = LocaleController.getString("ColorWhite", 2131165567);
      arrayOfInt = new int[1];
      arrayOfInt[0] = i;
      j = 0;
      if (j >= 9)
        break label439;
      localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setTag(Integer.valueOf(j));
      localRadioColorCell.setCheckColor(org.vidogram.ui.Cells.TextColorCell.colors[j], org.vidogram.ui.Cells.TextColorCell.colors[j]);
      str10 = new String[] { str1, str2, str3, str4, str5, str6, str7, str8, str9 }[j];
      if (i != org.vidogram.ui.Cells.TextColorCell.colorsToSave[j])
        break label433;
    }
    label433: for (boolean bool = true; ; bool = false)
    {
      localRadioColorCell.setTextAndValue(str10, bool);
      ((LinearLayout)localObject).addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener((LinearLayout)localObject, arrayOfInt)
      {
        public void onClick(View paramView)
        {
          int j = this.val$linearLayout.getChildCount();
          int i = 0;
          if (i < j)
          {
            RadioColorCell localRadioColorCell = (RadioColorCell)this.val$linearLayout.getChildAt(i);
            if (localRadioColorCell == paramView);
            for (boolean bool = true; ; bool = false)
            {
              localRadioColorCell.setChecked(bool, true);
              i += 1;
              break;
            }
          }
          this.val$selectedColor[0] = org.vidogram.ui.Cells.TextColorCell.colorsToSave[((Integer)paramView.getTag()).intValue()];
        }
      });
      j += 1;
      break label139;
      if (paramBoolean2)
      {
        i = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
        break;
      }
      if (((SharedPreferences)localObject).contains("color_" + paramLong))
      {
        i = ((SharedPreferences)localObject).getInt("color_" + paramLong, -16776961);
        break;
      }
      if ((int)paramLong < 0)
      {
        i = ((SharedPreferences)localObject).getInt("GroupLed", -16776961);
        break;
      }
      i = ((SharedPreferences)localObject).getInt("MessagesLed", -16776961);
      break;
    }
    label439: paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("LedColor", 2131165905));
    paramActivity.setView((View)localObject);
    paramActivity.setPositiveButton(LocaleController.getString("Set", 2131166437), new DialogInterface.OnClickListener(paramBoolean2, arrayOfInt, paramBoolean1, paramLong, paramRunnable)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
        if (this.val$globalAll)
          paramDialogInterface.putInt("MessagesLed", this.val$selectedColor[0]);
        while (true)
        {
          paramDialogInterface.commit();
          if (this.val$onSelect != null)
            this.val$onSelect.run();
          return;
          if (this.val$globalGroup)
          {
            paramDialogInterface.putInt("GroupLed", this.val$selectedColor[0]);
            continue;
          }
          paramDialogInterface.putInt("color_" + this.val$dialog_id, this.val$selectedColor[0]);
        }
      }
    });
    paramActivity.setNeutralButton(LocaleController.getString("LedDisabled", 2131165906), new DialogInterface.OnClickListener(paramBoolean2, paramBoolean1, paramLong, paramRunnable)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
        if (this.val$globalAll)
          paramDialogInterface.putInt("MessagesLed", 0);
        while (true)
        {
          paramDialogInterface.commit();
          if (this.val$onSelect != null)
            this.val$onSelect.run();
          return;
          if (this.val$globalGroup)
          {
            paramDialogInterface.putInt("GroupLed", 0);
            continue;
          }
          paramDialogInterface.putInt("color_" + this.val$dialog_id, 0);
        }
      }
    });
    if ((!paramBoolean2) && (!paramBoolean1))
      paramActivity.setNegativeButton(LocaleController.getString("Default", 2131165626), new DialogInterface.OnClickListener(paramLong, paramRunnable)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          paramDialogInterface.remove("color_" + this.val$dialog_id);
          paramDialogInterface.commit();
          if (this.val$onSelect != null)
            this.val$onSelect.run();
        }
      });
    return (Dialog)paramActivity.create();
  }

  public static Dialog createMuteAlert(Context paramContext, long paramLong)
  {
    if (paramContext == null)
      return null;
    paramContext = new BottomSheet.Builder(paramContext);
    paramContext.setTitle(LocaleController.getString("Notifications", 2131166128));
    String str1 = LocaleController.formatString("MuteFor", 2131165997, new Object[] { LocaleController.formatPluralString("Hours", 1) });
    String str2 = LocaleController.formatString("MuteFor", 2131165997, new Object[] { LocaleController.formatPluralString("Hours", 8) });
    String str3 = LocaleController.formatString("MuteFor", 2131165997, new Object[] { LocaleController.formatPluralString("Days", 2) });
    String str4 = LocaleController.getString("MuteDisable", 2131165996);
    1 local1 = new DialogInterface.OnClickListener(paramLong)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        long l = 1L;
        int i = ConnectionsManager.getInstance().getCurrentTime();
        if (paramInt == 0)
          i += 3600;
        label260: 
        while (true)
        {
          paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          if (paramInt == 3)
            paramDialogInterface.putInt("notify2_" + this.val$dialog_id, 2);
          while (true)
          {
            NotificationsController.getInstance().removeNotificationsForDialog(this.val$dialog_id);
            MessagesStorage.getInstance().setDialogFlags(this.val$dialog_id, l);
            paramDialogInterface.commit();
            paramDialogInterface = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.val$dialog_id));
            if (paramDialogInterface != null)
            {
              paramDialogInterface.notify_settings = new TLRPC.TL_peerNotifySettings();
              paramDialogInterface.notify_settings.mute_until = i;
            }
            NotificationsController.updateServerNotificationsSettings(this.val$dialog_id);
            return;
            if (paramInt == 1)
            {
              i += 28800;
              break;
            }
            if (paramInt == 2)
            {
              i += 172800;
              break;
            }
            if (paramInt != 3)
              break label260;
            i = 2147483647;
            break;
            paramDialogInterface.putInt("notify2_" + this.val$dialog_id, 3);
            paramDialogInterface.putInt("notifyuntil_" + this.val$dialog_id, i);
            l = 1L | i << 32;
          }
        }
      }
    };
    paramContext.setItems(new CharSequence[] { str1, str2, str3, str4 }, local1);
    return paramContext.create();
  }

  public static Dialog createPopupSelectDialog(Activity paramActivity, BaseFragment paramBaseFragment, boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int[] arrayOfInt = new int[1];
    int i;
    label112: RadioColorCell localRadioColorCell;
    String str;
    if (paramBoolean2)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("popupAll", 0);
      String[] arrayOfString = new String[4];
      arrayOfString[0] = LocaleController.getString("NoPopup", 2131166041);
      arrayOfString[1] = LocaleController.getString("OnlyWhenScreenOn", 2131166164);
      arrayOfString[2] = LocaleController.getString("OnlyWhenScreenOff", 2131166163);
      arrayOfString[3] = LocaleController.getString("AlwaysShowPopup", 2131165306);
      localObject = new LinearLayout(paramActivity);
      ((LinearLayout)localObject).setOrientation(1);
      i = 0;
      if (i >= arrayOfString.length)
        break label255;
      localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setTag(Integer.valueOf(i));
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setCheckColor(-5000269, -13129232);
      str = arrayOfString[i];
      if (arrayOfInt[0] != i)
        break label250;
    }
    label250: for (paramBoolean2 = true; ; paramBoolean2 = false)
    {
      localRadioColorCell.setTextAndValue(str, paramBoolean2);
      ((LinearLayout)localObject).addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener(arrayOfInt, paramBoolean1, paramBaseFragment, paramRunnable)
      {
        public void onClick(View paramView)
        {
          this.val$selected[0] = ((Integer)paramView.getTag()).intValue();
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          if (this.val$globalGroup);
          for (paramView = "popupGroup"; ; paramView = "popupAll")
          {
            localEditor.putInt(paramView, this.val$selected[0]);
            localEditor.commit();
            if (this.val$parentFragment != null)
              this.val$parentFragment.dismissCurrentDialig();
            if (this.val$onSelect != null)
              this.val$onSelect.run();
            return;
          }
        }
      });
      i += 1;
      break label112;
      if (!paramBoolean1)
        break;
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("popupGroup", 0);
      break;
    }
    label255: paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("PopupNotification", 2131166301));
    paramActivity.setView((View)localObject);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", 2131165427), null);
    return (Dialog)paramActivity.create();
  }

  public static Dialog createPrioritySelectDialog(Activity paramActivity, BaseFragment paramBaseFragment, long paramLong, boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int[] arrayOfInt = new int[1];
    LinearLayout localLinearLayout;
    int i;
    label140: RadioColorCell localRadioColorCell;
    String str;
    if (paramLong != 0L)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_" + paramLong, 3);
      if (arrayOfInt[0] == 3)
      {
        arrayOfInt[0] = 0;
        localObject = new String[] { LocaleController.getString("NotificationsPrioritySettings", 2131166145), LocaleController.getString("NotificationsPriorityDefault", 2131166141), LocaleController.getString("NotificationsPriorityHigh", 2131166142), LocaleController.getString("NotificationsPriorityMax", 2131166144) };
        localLinearLayout = new LinearLayout(paramActivity);
        localLinearLayout.setOrientation(1);
        i = 0;
        if (i >= localObject.length)
          break label367;
        localRadioColorCell = new RadioColorCell(paramActivity);
        localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
        localRadioColorCell.setTag(Integer.valueOf(i));
        localRadioColorCell.setCheckColor(-5000269, -13129232);
        str = localObject[i];
        if (arrayOfInt[0] != i)
          break label361;
      }
    }
    label361: for (paramBoolean2 = true; ; paramBoolean2 = false)
    {
      localRadioColorCell.setTextAndValue(str, paramBoolean2);
      localLinearLayout.addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener(arrayOfInt, paramLong, paramBoolean1, paramBaseFragment, paramRunnable)
      {
        public void onClick(View paramView)
        {
          this.val$selected[0] = ((Integer)paramView.getTag()).intValue();
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          if (this.val$dialog_id != 0L)
          {
            if (this.val$selected[0] == 0)
              this.val$selected[0] = 3;
            while (true)
            {
              localEditor.putInt("priority_" + this.val$dialog_id, this.val$selected[0]);
              localEditor.commit();
              if (this.val$parentFragment != null)
                this.val$parentFragment.dismissCurrentDialig();
              if (this.val$onSelect != null)
                this.val$onSelect.run();
              return;
              paramView = this.val$selected;
              paramView[0] -= 1;
            }
          }
          if (this.val$globalGroup);
          for (paramView = "priority_group"; ; paramView = "priority_messages")
          {
            localEditor.putInt(paramView, this.val$selected[0]);
            break;
          }
        }
      });
      i += 1;
      break label140;
      arrayOfInt[0] += 1;
      break;
      if (paramBoolean2)
        arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_messages", 1);
      while (true)
      {
        localObject = new String[] { LocaleController.getString("NotificationsPriorityDefault", 2131166141), LocaleController.getString("NotificationsPriorityHigh", 2131166142), LocaleController.getString("NotificationsPriorityMax", 2131166144) };
        break;
        if (!paramBoolean1)
          continue;
        arrayOfInt[0] = ((SharedPreferences)localObject).getInt("priority_group", 1);
      }
    }
    label367: paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("NotificationsPriority", 2131166140));
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", 2131165427), null);
    return (Dialog)paramActivity.create();
  }

  public static Dialog createReportAlert(Context paramContext, long paramLong, BaseFragment paramBaseFragment)
  {
    if ((paramContext == null) || (paramBaseFragment == null))
      return null;
    paramContext = new BottomSheet.Builder(paramContext);
    paramContext.setTitle(LocaleController.getString("ReportChat", 2131166327));
    String str1 = LocaleController.getString("ReportChatSpam", 2131166331);
    String str2 = LocaleController.getString("ReportChatViolence", 2131166332);
    String str3 = LocaleController.getString("ReportChatPornography", 2131166330);
    String str4 = LocaleController.getString("ReportChatOther", 2131166329);
    paramBaseFragment = new DialogInterface.OnClickListener(paramLong, paramBaseFragment)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        if (paramInt == 3)
        {
          paramDialogInterface = new Bundle();
          paramDialogInterface.putLong("dialog_id", this.val$dialog_id);
          this.val$parentFragment.presentFragment(new ReportOtherActivity(paramDialogInterface));
          return;
        }
        paramDialogInterface = new TLRPC.TL_account_reportPeer();
        paramDialogInterface.peer = MessagesController.getInputPeer((int)this.val$dialog_id);
        if (paramInt == 0)
          paramDialogInterface.reason = new TLRPC.TL_inputReportReasonSpam();
        while (true)
        {
          ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
            }
          });
          return;
          if (paramInt == 1)
          {
            paramDialogInterface.reason = new TLRPC.TL_inputReportReasonViolence();
            continue;
          }
          if (paramInt != 2)
            continue;
          paramDialogInterface.reason = new TLRPC.TL_inputReportReasonPornography();
        }
      }
    };
    paramContext.setItems(new CharSequence[] { str1, str2, str3, str4 }, paramBaseFragment);
    return paramContext.create();
  }

  public static Dialog createSingleChoiceDialog(Activity paramActivity, BaseFragment paramBaseFragment, String[] paramArrayOfString, String paramString, int paramInt, DialogInterface.OnClickListener paramOnClickListener)
  {
    LinearLayout localLinearLayout = new LinearLayout(paramActivity);
    localLinearLayout.setOrientation(1);
    int i = 0;
    if (i < paramArrayOfString.length)
    {
      RadioColorCell localRadioColorCell = new RadioColorCell(paramActivity);
      localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
      localRadioColorCell.setTag(Integer.valueOf(i));
      localRadioColorCell.setCheckColor(-5000269, -13129232);
      String str = paramArrayOfString[i];
      if (paramInt == i);
      for (boolean bool = true; ; bool = false)
      {
        localRadioColorCell.setTextAndValue(str, bool);
        localLinearLayout.addView(localRadioColorCell);
        localRadioColorCell.setOnClickListener(new View.OnClickListener(paramBaseFragment, paramOnClickListener)
        {
          public void onClick(View paramView)
          {
            int i = ((Integer)paramView.getTag()).intValue();
            if (this.val$parentFragment != null)
              this.val$parentFragment.dismissCurrentDialig();
            this.val$listener.onClick(null, i);
          }
        });
        i += 1;
        break;
      }
    }
    paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(paramString);
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", 2131165427), null);
    return paramActivity.create();
  }

  public static AlertDialog.Builder createTTLAlert(Context paramContext, TLRPC.EncryptedChat paramEncryptedChat)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramContext);
    localBuilder.setTitle(LocaleController.getString("MessageLifetime", 2131165960));
    paramContext = new NumberPicker(paramContext);
    paramContext.setMinValue(0);
    paramContext.setMaxValue(20);
    if ((paramEncryptedChat.ttl > 0) && (paramEncryptedChat.ttl < 16))
      paramContext.setValue(paramEncryptedChat.ttl);
    while (true)
    {
      paramContext.setFormatter(new NumberPicker.Formatter()
      {
        public String format(int paramInt)
        {
          if (paramInt == 0)
            return LocaleController.getString("ShortMessageLifetimeForever", 2131166466);
          if ((paramInt >= 1) && (paramInt < 16))
            return LocaleController.formatTTLString(paramInt);
          if (paramInt == 16)
            return LocaleController.formatTTLString(30);
          if (paramInt == 17)
            return LocaleController.formatTTLString(60);
          if (paramInt == 18)
            return LocaleController.formatTTLString(3600);
          if (paramInt == 19)
            return LocaleController.formatTTLString(86400);
          if (paramInt == 20)
            return LocaleController.formatTTLString(604800);
          return "";
        }
      });
      localBuilder.setView(paramContext);
      localBuilder.setNegativeButton(LocaleController.getString("Done", 2131165661), new DialogInterface.OnClickListener(paramEncryptedChat, paramContext)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramInt = this.val$encryptedChat.ttl;
          int i = this.val$numberPicker.getValue();
          if ((i >= 0) && (i < 16))
            this.val$encryptedChat.ttl = i;
          while (true)
          {
            if (paramInt != this.val$encryptedChat.ttl)
            {
              SecretChatHelper.getInstance().sendTTLMessage(this.val$encryptedChat, null);
              MessagesStorage.getInstance().updateEncryptedChatTTL(this.val$encryptedChat);
            }
            return;
            if (i == 16)
            {
              this.val$encryptedChat.ttl = 30;
              continue;
            }
            if (i == 17)
            {
              this.val$encryptedChat.ttl = 60;
              continue;
            }
            if (i == 18)
            {
              this.val$encryptedChat.ttl = 3600;
              continue;
            }
            if (i == 19)
            {
              this.val$encryptedChat.ttl = 86400;
              continue;
            }
            if (i != 20)
              continue;
            this.val$encryptedChat.ttl = 604800;
          }
        }
      });
      return localBuilder;
      if (paramEncryptedChat.ttl == 30)
      {
        paramContext.setValue(16);
        continue;
      }
      if (paramEncryptedChat.ttl == 60)
      {
        paramContext.setValue(17);
        continue;
      }
      if (paramEncryptedChat.ttl == 3600)
      {
        paramContext.setValue(18);
        continue;
      }
      if (paramEncryptedChat.ttl == 86400)
      {
        paramContext.setValue(19);
        continue;
      }
      if (paramEncryptedChat.ttl == 604800)
      {
        paramContext.setValue(20);
        continue;
      }
      if (paramEncryptedChat.ttl != 0)
        continue;
      paramContext.setValue(0);
    }
  }

  public static Dialog createVibrationSelectDialog(Activity paramActivity, BaseFragment paramBaseFragment, long paramLong, String paramString, Runnable paramRunnable)
  {
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    int[] arrayOfInt = new int[1];
    LinearLayout localLinearLayout;
    int i;
    label139: RadioColorCell localRadioColorCell;
    String str;
    if (paramLong != 0L)
    {
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt(paramString + paramLong, 0);
      if (arrayOfInt[0] == 3)
      {
        arrayOfInt[0] = 2;
        localObject = new String[] { LocaleController.getString("VibrationDefault", 2131166569), LocaleController.getString("Short", 2131166465), LocaleController.getString("Long", 2131165934), LocaleController.getString("VibrationDisabled", 2131166570) };
        localLinearLayout = new LinearLayout(paramActivity);
        localLinearLayout.setOrientation(1);
        i = 0;
        if (i >= localObject.length)
          break label408;
        localRadioColorCell = new RadioColorCell(paramActivity);
        localRadioColorCell.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
        localRadioColorCell.setTag(Integer.valueOf(i));
        localRadioColorCell.setCheckColor(-5000269, -13129232);
        str = localObject[i];
        if (arrayOfInt[0] != i)
          break label402;
      }
    }
    label402: for (boolean bool = true; ; bool = false)
    {
      localRadioColorCell.setTextAndValue(str, bool);
      localLinearLayout.addView(localRadioColorCell);
      localRadioColorCell.setOnClickListener(new View.OnClickListener(arrayOfInt, paramLong, paramString, paramBaseFragment, paramRunnable)
      {
        public void onClick(View paramView)
        {
          this.val$selected[0] = ((Integer)paramView.getTag()).intValue();
          paramView = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          if (this.val$dialog_id != 0L)
            if (this.val$selected[0] == 0)
              paramView.putInt(this.val$prefKeyPrefix + this.val$dialog_id, 0);
          while (true)
          {
            paramView.commit();
            if (this.val$parentFragment != null)
              this.val$parentFragment.dismissCurrentDialig();
            if (this.val$onSelect != null)
              this.val$onSelect.run();
            return;
            if (this.val$selected[0] == 1)
            {
              paramView.putInt(this.val$prefKeyPrefix + this.val$dialog_id, 1);
              continue;
            }
            if (this.val$selected[0] == 2)
            {
              paramView.putInt(this.val$prefKeyPrefix + this.val$dialog_id, 3);
              continue;
            }
            if (this.val$selected[0] != 3)
              continue;
            paramView.putInt(this.val$prefKeyPrefix + this.val$dialog_id, 2);
            continue;
            if (this.val$selected[0] == 0)
            {
              paramView.putInt(this.val$prefKeyPrefix, 2);
              continue;
            }
            if (this.val$selected[0] == 1)
            {
              paramView.putInt(this.val$prefKeyPrefix, 0);
              continue;
            }
            if (this.val$selected[0] == 2)
            {
              paramView.putInt(this.val$prefKeyPrefix, 1);
              continue;
            }
            if (this.val$selected[0] == 3)
            {
              paramView.putInt(this.val$prefKeyPrefix, 3);
              continue;
            }
            if (this.val$selected[0] != 4)
              continue;
            paramView.putInt(this.val$prefKeyPrefix, 4);
          }
        }
      });
      i += 1;
      break label139;
      if (arrayOfInt[0] != 2)
        break;
      arrayOfInt[0] = 3;
      break;
      arrayOfInt[0] = ((SharedPreferences)localObject).getInt(paramString, 0);
      if (arrayOfInt[0] == 0)
        arrayOfInt[0] = 1;
      while (true)
      {
        localObject = new String[] { LocaleController.getString("VibrationDisabled", 2131166570), LocaleController.getString("VibrationDefault", 2131166569), LocaleController.getString("Short", 2131166465), LocaleController.getString("Long", 2131165934), LocaleController.getString("OnlyIfSilent", 2131166162) };
        break;
        if (arrayOfInt[0] == 1)
        {
          arrayOfInt[0] = 2;
          continue;
        }
        if (arrayOfInt[0] != 2)
          continue;
        arrayOfInt[0] = 0;
      }
    }
    label408: paramActivity = new AlertDialog.Builder(paramActivity);
    paramActivity.setTitle(LocaleController.getString("Vibrate", 2131166568));
    paramActivity.setView(localLinearLayout);
    paramActivity.setPositiveButton(LocaleController.getString("Cancel", 2131165427), null);
    return (Dialog)paramActivity.create();
  }

  public static Dialog createVibrationSelectDialog(Activity paramActivity, BaseFragment paramBaseFragment, long paramLong, boolean paramBoolean1, boolean paramBoolean2, Runnable paramRunnable)
  {
    if (paramLong != 0L)
    {
      str = "vibrate_";
      return createVibrationSelectDialog(paramActivity, paramBaseFragment, paramLong, str, paramRunnable);
    }
    if (paramBoolean1);
    for (String str = "vibrate_group"; ; str = "vibrate_messages")
      break;
  }

  private static String getFloodWaitString(String paramString)
  {
    int i = Utilities.parseInt(paramString).intValue();
    if (i < 60);
    for (paramString = LocaleController.formatPluralString("Seconds", i); ; paramString = LocaleController.formatPluralString("Minutes", i / 60))
      return LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { paramString });
  }

  public static Dialog processError(TLRPC.TL_error paramTL_error, BaseFragment paramBaseFragment, TLObject paramTLObject, Object[] paramArrayOfObject)
  {
    int j = 0;
    int i = 0;
    if ((paramTL_error.code == 406) || (paramTL_error.text == null))
      return null;
    if (((paramTLObject instanceof TLRPC.TL_channels_joinChannel)) || ((paramTLObject instanceof TLRPC.TL_channels_editAdmin)) || ((paramTLObject instanceof TLRPC.TL_channels_inviteToChannel)) || ((paramTLObject instanceof TLRPC.TL_messages_addChatUser)) || ((paramTLObject instanceof TLRPC.TL_messages_startBot)))
      if (paramBaseFragment != null)
        showAddUserAlert(paramTL_error.text, paramBaseFragment, ((Boolean)paramArrayOfObject[0]).booleanValue());
    while (true)
    {
      label81: return null;
      if (!paramTL_error.text.equals("PEER_FLOOD"))
        continue;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(1) });
      continue;
      if ((paramTLObject instanceof TLRPC.TL_messages_createChat))
      {
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showFloodWaitAlert(paramTL_error.text, paramBaseFragment);
          continue;
        }
        showAddUserAlert(paramTL_error.text, paramBaseFragment, false);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_channels_createChannel))
      {
        if (!paramTL_error.text.startsWith("FLOOD_WAIT"))
          continue;
        showFloodWaitAlert(paramTL_error.text, paramBaseFragment);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_messages_editMessage))
      {
        if (paramTL_error.text.equals("MESSAGE_NOT_MODIFIED"))
          continue;
        showSimpleAlert(paramBaseFragment, LocaleController.getString("EditMessageError", 2131165666));
        continue;
      }
      if (((paramTLObject instanceof TLRPC.TL_messages_sendMessage)) || ((paramTLObject instanceof TLRPC.TL_messages_sendMedia)) || ((paramTLObject instanceof TLRPC.TL_geochats_sendMessage)) || ((paramTLObject instanceof TLRPC.TL_messages_sendBroadcast)) || ((paramTLObject instanceof TLRPC.TL_messages_sendInlineBotResult)) || ((paramTLObject instanceof TLRPC.TL_geochats_sendMedia)) || ((paramTLObject instanceof TLRPC.TL_messages_forwardMessages)))
      {
        if (!paramTL_error.text.equals("PEER_FLOOD"))
          continue;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.needShowAlert, new Object[] { Integer.valueOf(0) });
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_messages_importChatInvite))
      {
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        if (paramTL_error.text.equals("USERS_TOO_MUCH"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("JoinToGroupErrorFull", 2131165861));
          continue;
        }
        showSimpleAlert(paramBaseFragment, LocaleController.getString("JoinToGroupErrorNotExist", 2131165862));
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_messages_getAttachedStickers))
      {
        if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
          continue;
        Toast.makeText(paramBaseFragment.getParentActivity(), LocaleController.getString("ErrorOccurred", 2131165701) + "\n" + paramTL_error.text, 0).show();
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_account_confirmPhone))
      {
        if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID")))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", 2131165837));
          continue;
        }
        if (paramTL_error.text.contains("PHONE_CODE_EXPIRED"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", 2131165559));
          continue;
        }
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        showSimpleAlert(paramBaseFragment, paramTL_error.text);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_auth_resendCode))
      {
        if (paramTL_error.text.contains("PHONE_NUMBER_INVALID"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", 2131165841));
          continue;
        }
        if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID")))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", 2131165837));
          continue;
        }
        if (paramTL_error.text.contains("PHONE_CODE_EXPIRED"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", 2131165559));
          continue;
        }
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        if (paramTL_error.code == -1000)
          continue;
        showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", 2131165701) + "\n" + paramTL_error.text);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_account_sendConfirmPhoneCode))
      {
        if (paramTL_error.code == 400)
          return showSimpleAlert(paramBaseFragment, LocaleController.getString("CancelLinkExpired", 2131165430));
        if (paramTL_error.text == null)
          continue;
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
          return showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
        return showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", 2131165701));
      }
      if ((paramTLObject instanceof TLRPC.TL_account_changePhone))
      {
        if (paramTL_error.text.contains("PHONE_NUMBER_INVALID"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", 2131165841));
          continue;
        }
        if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID")))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", 2131165837));
          continue;
        }
        if (paramTL_error.text.contains("PHONE_CODE_EXPIRED"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", 2131165559));
          continue;
        }
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        showSimpleAlert(paramBaseFragment, paramTL_error.text);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_account_sendChangePhoneCode))
      {
        if (paramTL_error.text.contains("PHONE_NUMBER_INVALID"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidPhoneNumber", 2131165841));
          continue;
        }
        if ((paramTL_error.text.contains("PHONE_CODE_EMPTY")) || (paramTL_error.text.contains("PHONE_CODE_INVALID")))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("InvalidCode", 2131165837));
          continue;
        }
        if (paramTL_error.text.contains("PHONE_CODE_EXPIRED"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("CodeExpired", 2131165559));
          continue;
        }
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        if (paramTL_error.text.startsWith("PHONE_NUMBER_OCCUPIED"))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.formatString("ChangePhoneNumberOccupied", 2131165440, new Object[] { (String)paramArrayOfObject[0] }));
          continue;
        }
        showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", 2131165701));
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_updateUserName))
      {
        paramTL_error = paramTL_error.text;
        switch (paramTL_error.hashCode())
        {
        default:
          label1264: i = -1;
        case 288843630:
        case 533175271:
        case -141887186:
        }
        while (true)
          switch (i)
          {
          default:
            showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", 2131165701));
            break label81;
            if (!paramTL_error.equals("USERNAME_INVALID"))
              break label1264;
            continue;
            if (!paramTL_error.equals("USERNAME_OCCUPIED"))
              break label1264;
            i = 1;
            continue;
            if (!paramTL_error.equals("USERNAMES_UNAVAILABLE"))
              break label1264;
            i = 2;
          case 0:
          case 1:
          case 2:
          }
        showSimpleAlert(paramBaseFragment, LocaleController.getString("UsernameInvalid", 2131166557));
        continue;
        showSimpleAlert(paramBaseFragment, LocaleController.getString("UsernameInUse", 2131166556));
        continue;
        showSimpleAlert(paramBaseFragment, LocaleController.getString("FeatureUnavailable", 2131165706));
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_contacts_importContacts))
      {
        if ((paramTL_error == null) || (paramTL_error.text.startsWith("FLOOD_WAIT")))
        {
          showSimpleAlert(paramBaseFragment, LocaleController.getString("FloodWait", 2131165715));
          continue;
        }
        showSimpleAlert(paramBaseFragment, LocaleController.getString("ErrorOccurred", 2131165701) + "\n" + paramTL_error.text);
        continue;
      }
      if (((paramTLObject instanceof TLRPC.TL_account_getPassword)) || ((paramTLObject instanceof TLRPC.TL_account_getTmpPassword)))
      {
        if (paramTL_error.text.startsWith("FLOOD_WAIT"))
        {
          showSimpleToast(paramBaseFragment, getFloodWaitString(paramTL_error.text));
          continue;
        }
        showSimpleToast(paramBaseFragment, paramTL_error.text);
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_payments_sendPaymentForm))
      {
        paramTLObject = paramTL_error.text;
        switch (paramTLObject.hashCode())
        {
        default:
          label1588: i = -1;
        case -1144062453:
        case -784238410:
        }
        while (true)
          switch (i)
          {
          default:
            showSimpleToast(paramBaseFragment, paramTL_error.text);
            break label81;
            if (!paramTLObject.equals("BOT_PRECHECKOUT_FAILED"))
              break label1588;
            i = j;
            continue;
            if (!paramTLObject.equals("PAYMENT_FAILED"))
              break label1588;
            i = 1;
          case 0:
          case 1:
          }
        showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentPrecheckoutFailed", 2131166229));
        continue;
        showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentFailed", 2131166226));
        continue;
      }
      if (!(paramTLObject instanceof TLRPC.TL_payments_validateRequestedInfo))
        continue;
      paramTLObject = paramTL_error.text;
      i = -1;
      switch (paramTLObject.hashCode())
      {
      default:
      case 1758025548:
      }
      while (true)
        switch (i)
        {
        default:
          showSimpleToast(paramBaseFragment, paramTL_error.text);
          break label81;
          if (!paramTLObject.equals("SHIPPING_NOT_AVAILABLE"))
            continue;
          i = 0;
        case 0:
        }
      showSimpleToast(paramBaseFragment, LocaleController.getString("PaymentNoShippingMethod", 2131166228));
    }
  }

  public static void showAddUserAlert(String paramString, BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    if ((paramString == null) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
      switch (i)
      {
      default:
        localBuilder.setMessage(paramString);
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
      case 11:
      }
    case -454039871:
    case -538116776:
    case 517420851:
    case 1227003815:
    case 1167301807:
    case 1623167701:
    case 1253103379:
    case -420079733:
    case 1916725894:
    case -1763467626:
    case -512775857:
    case 845559454:
    }
    while (true)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      paramBaseFragment.showDialog(localBuilder.create(), true, null);
      return;
      if (!paramString.equals("PEER_FLOOD"))
        break;
      i = 0;
      break;
      if (!paramString.equals("USER_BLOCKED"))
        break;
      i = 1;
      break;
      if (!paramString.equals("USER_BOT"))
        break;
      i = 2;
      break;
      if (!paramString.equals("USER_ID_INVALID"))
        break;
      i = 3;
      break;
      if (!paramString.equals("USERS_TOO_MUCH"))
        break;
      i = 4;
      break;
      if (!paramString.equals("USER_NOT_MUTUAL_CONTACT"))
        break;
      i = 5;
      break;
      if (!paramString.equals("ADMINS_TOO_MUCH"))
        break;
      i = 6;
      break;
      if (!paramString.equals("BOTS_TOO_MUCH"))
        break;
      i = 7;
      break;
      if (!paramString.equals("USER_PRIVACY_RESTRICTED"))
        break;
      i = 8;
      break;
      if (!paramString.equals("USERS_TOO_FEW"))
        break;
      i = 9;
      break;
      if (!paramString.equals("USER_RESTRICTED"))
        break;
      i = 10;
      break;
      if (!paramString.equals("YOU_BLOCKED_USER"))
        break;
      i = 11;
      break;
      localBuilder.setMessage(LocaleController.getString("NobodyLikesSpam2", 2131166057));
      localBuilder.setNegativeButton(LocaleController.getString("MoreInfo", 2131165995), new DialogInterface.OnClickListener(paramBaseFragment)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          MessagesController.openByUserName("spambot", this.val$fragment, 1);
        }
      });
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserCantAdd", 2131165523));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("GroupUserCantAdd", 2131165800));
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserAddLimit", 2131165522));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("GroupUserAddLimit", 2131165799));
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserLeftError", 2131165526));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("GroupUserLeftError", 2131165803));
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserCantAdmin", 2131165524));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("GroupUserCantAdmin", 2131165801));
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("ChannelUserCantBot", 2131165525));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("GroupUserCantBot", 2131165802));
      continue;
      if (paramBoolean)
      {
        localBuilder.setMessage(LocaleController.getString("InviteToChannelError", 2131165847));
        continue;
      }
      localBuilder.setMessage(LocaleController.getString("InviteToGroupError", 2131165849));
      continue;
      localBuilder.setMessage(LocaleController.getString("CreateGroupError", 2131165589));
      continue;
      localBuilder.setMessage(LocaleController.getString("UserRestricted", 2131166549));
      continue;
      localBuilder.setMessage(LocaleController.getString("YouBlockedUser", 2131166642));
    }
  }

  public static void showFloodWaitAlert(String paramString, BaseFragment paramBaseFragment)
  {
    if ((paramString == null) || (!paramString.startsWith("FLOOD_WAIT")) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
      return;
    int i = Utilities.parseInt(paramString).intValue();
    if (i < 60);
    for (paramString = LocaleController.formatPluralString("Seconds", i); ; paramString = LocaleController.formatPluralString("Minutes", i / 60))
    {
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
      localBuilder.setMessage(LocaleController.formatString("FloodWaitTime", 2131165716, new Object[] { paramString }));
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      paramBaseFragment.showDialog(localBuilder.create(), true, null);
      return;
    }
  }

  public static Dialog showSimpleAlert(BaseFragment paramBaseFragment, String paramString)
  {
    if ((paramString == null) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
      return null;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    localBuilder.setMessage(paramString);
    localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    paramString = localBuilder.create();
    paramBaseFragment.showDialog(paramString);
    return paramString;
  }

  public static Toast showSimpleToast(BaseFragment paramBaseFragment, String paramString)
  {
    if ((paramString == null) || (paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null))
      return null;
    paramBaseFragment = Toast.makeText(paramBaseFragment.getParentActivity(), paramString, 1);
    paramBaseFragment.show();
    return paramBaseFragment;
  }

  public static abstract interface PaymentAlertDelegate
  {
    public abstract void didPressedNewCard();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.AlertsCreator
 * JD-Core Version:    0.6.0
 */