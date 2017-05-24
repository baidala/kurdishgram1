package org.vidogram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Settings.System;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.TL_userFull;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.VoIPActivity;

public class VoIPHelper
{
  private static long lastCallRequestTime = 0L;

  private static void doInitiateCall(TLRPC.User paramUser, Activity paramActivity)
  {
    if ((paramActivity == null) || (paramUser == null));
    do
      return;
    while (System.currentTimeMillis() - lastCallRequestTime < 1000L);
    lastCallRequestTime = System.currentTimeMillis();
    Intent localIntent = new Intent(paramActivity, VoIPService.class);
    localIntent.putExtra("user_id", paramUser.id);
    localIntent.putExtra("is_outgoing", true);
    localIntent.putExtra("start_incall_activity", true);
    paramActivity.startService(localIntent);
  }

  private static void initiateCall(TLRPC.User paramUser, Activity paramActivity)
  {
    if ((paramActivity == null) || (paramUser == null));
    do
    {
      return;
      if (VoIPService.getSharedInstance() == null)
        continue;
      TLRPC.User localUser = VoIPService.getSharedInstance().getUser();
      if (localUser.id != paramUser.id)
      {
        new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("VoipOngoingAlertTitle", 2131166598)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("VoipOngoingAlert", 2131166597, new Object[] { ContactsController.formatName(localUser.first_name, localUser.last_name), ContactsController.formatName(paramUser.first_name, paramUser.last_name) }))).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramUser, paramActivity)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            if (VoIPService.getSharedInstance() != null)
            {
              VoIPService.getSharedInstance().hangUp(new Runnable()
              {
                public void run()
                {
                  VoIPHelper.access$000(VoIPHelper.2.this.val$user, VoIPHelper.2.this.val$activity);
                }
              });
              return;
            }
            VoIPHelper.access$000(this.val$user, this.val$activity);
          }
        }).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null).show();
        return;
      }
      paramActivity.startActivity(new Intent(paramActivity, VoIPActivity.class).addFlags(268435456));
      return;
    }
    while (VoIPService.callIShouldHavePutIntoIntent != null);
    doInitiateCall(paramUser, paramActivity);
  }

  @TargetApi(23)
  public static void permissionDenied(Activity paramActivity, Runnable paramRunnable)
  {
    if (!paramActivity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO"))
      new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("AppName", 2131165319)).setMessage(LocaleController.getString("VoipNeedMicPermission", 2131166590)).setPositiveButton(LocaleController.getString("OK", 2131166153), null).setNegativeButton(LocaleController.getString("Settings", 2131166448), new DialogInterface.OnClickListener(paramActivity)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          paramDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
          paramDialogInterface.setData(Uri.fromParts("package", this.val$activity.getPackageName(), null));
          this.val$activity.startActivity(paramDialogInterface);
        }
      }).show().setOnDismissListener(new DialogInterface.OnDismissListener(paramRunnable)
      {
        public void onDismiss(DialogInterface paramDialogInterface)
        {
          if (this.val$onFinish != null)
            this.val$onFinish.run();
        }
      });
  }

  public static void startCall(TLRPC.User paramUser, Activity paramActivity, TLRPC.TL_userFull paramTL_userFull)
  {
    int i = 1;
    if ((paramTL_userFull != null) && (paramTL_userFull.phone_calls_private))
    {
      new AlertDialog.Builder(paramActivity).setTitle(LocaleController.getString("VoipFailed", 2131166584)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", 2131165420, new Object[] { ContactsController.formatName(paramUser.first_name, paramUser.last_name) }))).setPositiveButton(LocaleController.getString("OK", 2131166153), null).show();
      return;
    }
    if (ConnectionsManager.getInstance().getConnectionState() != 3)
    {
      if (Settings.System.getInt(paramActivity.getContentResolver(), "airplane_mode_on", 0) != 0)
      {
        paramTL_userFull = new AlertDialog.Builder(paramActivity);
        if (i == 0)
          break label214;
        paramUser = LocaleController.getString("VoipOfflineAirplaneTitle", 2131166594);
        label122: paramTL_userFull = paramTL_userFull.setTitle(paramUser);
        if (i == 0)
          break label227;
      }
      label214: label227: for (paramUser = LocaleController.getString("VoipOfflineAirplane", 2131166593); ; paramUser = LocaleController.getString("VoipOffline", 2131166592))
      {
        paramUser = paramTL_userFull.setMessage(paramUser).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
        if (i != 0)
        {
          paramTL_userFull = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
          if (paramTL_userFull.resolveActivity(paramActivity.getPackageManager()) != null)
            paramUser.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", 2131166595), new DialogInterface.OnClickListener(paramActivity, paramTL_userFull)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                this.val$activity.startActivity(this.val$settingsIntent);
              }
            });
        }
        paramUser.show();
        return;
        i = 0;
        break;
        paramUser = LocaleController.getString("VoipOfflineTitle", 2131166596);
        break label122;
      }
    }
    if ((Build.VERSION.SDK_INT >= 23) && (paramActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
    {
      paramActivity.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
      return;
    }
    initiateCall(paramUser, paramActivity);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.voip.VoIPHelper
 * JD-Core Version:    0.6.0
 */