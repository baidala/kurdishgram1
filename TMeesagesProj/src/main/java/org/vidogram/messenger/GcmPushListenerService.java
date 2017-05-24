package org.vidogram.messenger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import com.google.android.gms.gcm.a;
import org.json.JSONObject;
import org.vidogram.tgnet.ConnectionsManager;

public class GcmPushListenerService extends a
{
  public static final int NOTIFICATION_ID = 1;

  public void onMessageReceived(String paramString, Bundle paramBundle)
  {
    FileLog.d("GCM received bundle: " + paramBundle + " from: " + paramString);
    AndroidUtilities.runOnUIThread(new Runnable(paramBundle)
    {
      public void run()
      {
        ApplicationLoader.postInitApplication();
        try
        {
          Object localObject;
          if ("DC_UPDATE".equals(this.val$bundle.getString("loc_key")))
          {
            localObject = new JSONObject(this.val$bundle.getString("custom"));
            int i = ((JSONObject)localObject).getInt("dc");
            localObject = ((JSONObject)localObject).getString("addr").split(":");
            if (localObject.length != 2)
              return;
            String str = localObject[0];
            int j = Integer.parseInt(localObject[1]);
            ConnectionsManager.getInstance().applyDatacenterAddress(i, str, j);
          }
          while (true)
          {
            ConnectionsManager.onInternalPushReceived();
            ConnectionsManager.getInstance().resumeNetworkMaybe();
            return;
            if ((!ApplicationLoader.mainInterfacePaused) || (this.val$bundle.getInt("badge", -1) != -1))
              continue;
            localObject = ((ConnectivityManager)ApplicationLoader.applicationContext.getSystemService("connectivity")).getActiveNetworkInfo();
            if ((localObject != null) && (((NetworkInfo)localObject).isConnected()))
              continue;
            NotificationsController.getInstance().showSingleBackgroundNotification();
          }
        }
        catch (Exception localException)
        {
          while (true)
            FileLog.e(localException);
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.GcmPushListenerService
 * JD-Core Version:    0.6.0
 */