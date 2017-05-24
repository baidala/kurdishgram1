package org.vidogram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.vidogram.messenger.voip.VoIPService;
import org.vidogram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity
{
  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 101);
  }

  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if (paramInt == 101)
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
      {
        if (VoIPService.getSharedInstance() != null)
          VoIPService.getSharedInstance().acceptIncomingCall();
        finish();
        startActivity(new Intent(this, VoIPActivity.class));
      }
    }
    else
      return;
    if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO"))
    {
      if (VoIPService.getSharedInstance() != null)
        VoIPService.getSharedInstance().declineIncomingCall();
      VoIPHelper.permissionDenied(this, new Runnable()
      {
        public void run()
        {
          VoIPPermissionActivity.this.finish();
        }
      });
      return;
    }
    finish();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.VoIPPermissionActivity
 * JD-Core Version:    0.6.0
 */