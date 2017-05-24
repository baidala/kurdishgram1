package org.vidogram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.PowerManager;
import android.support.b.b;
import android.util.Base64;
import com.google.android.gms.common.GooglePlayServicesUtil;
import java.io.File;
import java.io.RandomAccessFile;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.Components.ForegroundDetector;

public class ApplicationLoader extends b
{

  @SuppressLint({"StaticFieldLeak"})
  public static volatile Context applicationContext;
  public static volatile Handler applicationHandler;
  private static volatile boolean applicationInited = false;
  public static volatile boolean isScreenOn = false;
  public static volatile boolean mainInterfacePaused = true;
  public static volatile boolean mainInterfacePausedStageQueue = true;
  public static volatile long mainInterfacePausedStageQueueTime;

  private boolean checkPlayServices()
  {
    try
    {
      int i = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
      return i == 0;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return true;
  }

  private static void convertConfig()
  {
    SharedPreferences localSharedPreferences = applicationContext.getSharedPreferences("dataconfig", 0);
    SerializedData localSerializedData;
    boolean bool;
    if (localSharedPreferences.contains("currentDatacenterId"))
    {
      localSerializedData = new SerializedData(32768);
      localSerializedData.writeInt32(2);
      if (localSharedPreferences.getInt("datacenterSetId", 0) == 0)
        break label251;
      bool = true;
    }
    while (true)
    {
      localSerializedData.writeBool(bool);
      localSerializedData.writeBool(true);
      localSerializedData.writeInt32(localSharedPreferences.getInt("currentDatacenterId", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("timeDifference", 0));
      localSerializedData.writeInt32(localSharedPreferences.getInt("lastDcUpdateTime", 0));
      localSerializedData.writeInt64(localSharedPreferences.getLong("pushSessionId", 0L));
      localSerializedData.writeBool(false);
      localSerializedData.writeInt32(0);
      try
      {
        localObject1 = localSharedPreferences.getString("datacenters", null);
        if (localObject1 != null)
        {
          localObject1 = Base64.decode((String)localObject1, 0);
          if (localObject1 != null)
          {
            localObject2 = new SerializedData(localObject1);
            localSerializedData.writeInt32(((SerializedData)localObject2).readInt32(false));
            localSerializedData.writeBytes(localObject1, 4, localObject1.length - 4);
            ((SerializedData)localObject2).cleanup();
          }
        }
      }
      catch (Exception localException2)
      {
        try
        {
          while (true)
          {
            Object localObject1 = new RandomAccessFile(new File(getFilesDirFixed(), "tgnet.dat"), "rws");
            Object localObject2 = localSerializedData.toByteArray();
            ((RandomAccessFile)localObject1).writeInt(Integer.reverseBytes(localObject2.length));
            ((RandomAccessFile)localObject1).write(localObject2);
            ((RandomAccessFile)localObject1).close();
            localSerializedData.cleanup();
            localSharedPreferences.edit().clear().commit();
            return;
            label251: bool = false;
            break;
            localException1 = localException1;
            FileLog.e(localException1);
          }
        }
        catch (Exception localException2)
        {
          while (true)
            FileLog.e(localException2);
        }
      }
    }
  }

  public static File getFilesDirFixed()
  {
    int i = 0;
    File localFile;
    while (i < 10)
    {
      localFile = applicationContext.getFilesDir();
      if (localFile != null)
        return localFile;
      i += 1;
    }
    try
    {
      localFile = new File(applicationContext.getApplicationInfo().dataDir, "files");
      localFile.mkdirs();
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return new File("/data/data/org.telegram.messenger/files");
  }

  private void initPlayServices()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (ApplicationLoader.this.checkPlayServices())
        {
          if ((UserConfig.pushString != null) && (UserConfig.pushString.length() != 0))
            FileLog.d("GCM regId = " + UserConfig.pushString);
          while (true)
          {
            Intent localIntent = new Intent(ApplicationLoader.applicationContext, GcmRegistrationIntentService.class);
            ApplicationLoader.this.startService(localIntent);
            return;
            FileLog.d("GCM Registration not found.");
          }
        }
        FileLog.d("No valid Google Play Services APK found.");
      }
    }
    , 1000L);
  }

  public static void postInitApplication()
  {
    if (applicationInited)
      return;
    applicationInited = true;
    convertConfig();
    try
    {
      LocaleController.getInstance();
    }
    catch (Exception str1)
    {
      try
      {
        localObject1 = new IntentFilter("android.intent.action.SCREEN_ON");
        ((IntentFilter)localObject1).addAction("android.intent.action.SCREEN_OFF");
        localObject2 = new ScreenReceiver();
        applicationContext.registerReceiver((BroadcastReceiver)localObject2, (IntentFilter)localObject1);
      }
      catch (Exception str1)
      {
        try
        {
          isScreenOn = ((PowerManager)applicationContext.getSystemService("power")).isScreenOn();
          FileLog.e("screen state = " + isScreenOn);
          UserConfig.loadConfig();
          str4 = getFilesDirFixed().toString();
        }
        catch (Exception str1)
        {
          try
          {
            while (true)
            {
              String str4;
              str3 = LocaleController.getLocaleStringIso639();
              str2 = Build.MANUFACTURER + Build.MODEL;
              Object localObject1 = applicationContext.getPackageManager().getPackageInfo(applicationContext.getPackageName(), 0);
              localObject2 = ((PackageInfo)localObject1).versionName + " (" + ((PackageInfo)localObject1).versionCode + ")";
              localObject1 = "SDK " + Build.VERSION.SDK_INT;
              if (str3.trim().length() != 0)
                break label460;
              str3 = "en";
              if (str2.trim().length() != 0)
                break label457;
              str2 = "Android unknown";
              if (((String)localObject2).trim().length() != 0)
                break label454;
              localObject2 = "App version unknown";
              if (((String)localObject1).trim().length() != 0)
                break;
              localObject1 = "SDK Unknown";
              boolean bool = applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushConnection", true);
              MessagesController.getInstance();
              ConnectionsManager.getInstance().init(BuildVars.BUILD_VERSION, 65, BuildVars.APP_ID, str2, (String)localObject1, (String)localObject2, str3, str4, FileLog.getNetworkLogPath(), UserConfig.getClientUserId(), bool);
              if (UserConfig.getCurrentUser() != null)
              {
                MessagesController.getInstance().putUser(UserConfig.getCurrentUser(), true);
                ConnectionsManager.getInstance().applyCountryPortNumber(UserConfig.getCurrentUser().phone);
                MessagesController.getInstance().getBlockedUsers(true);
                SendMessagesHelper.getInstance().checkUnsentMessages();
              }
              ((ApplicationLoader)applicationContext).initPlayServices();
              FileLog.e("app initied");
              ContactsController.getInstance().checkAppAccount();
              MediaController.getInstance();
              return;
              localException1 = localException1;
              localException1.printStackTrace();
              continue;
              localException2 = localException2;
              localException2.printStackTrace();
            }
            localException3 = localException3;
            FileLog.e(localException3);
          }
          catch (Exception str1)
          {
            label454: label457: label460: 
            while (true)
            {
              String str3 = "en";
              String str2 = "Android unknown";
              Object localObject2 = "App version unknown";
              String str1 = "SDK " + Build.VERSION.SDK_INT;
              continue;
              continue;
              continue;
              continue;
            }
          }
        }
      }
    }
  }

  public static void startPushService()
  {
    if (applicationContext.getSharedPreferences("Notifications", 0).getBoolean("pushService", true))
    {
      applicationContext.startService(new Intent(applicationContext, NotificationsService.class));
      return;
    }
    stopPushService();
  }

  public static void stopPushService()
  {
    applicationContext.stopService(new Intent(applicationContext, NotificationsService.class));
    PendingIntent localPendingIntent = PendingIntent.getService(applicationContext, 0, new Intent(applicationContext, NotificationsService.class), 0);
    ((AlarmManager)applicationContext.getSystemService("alarm")).cancel(localPendingIntent);
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    try
    {
      LocaleController.getInstance().onDeviceConfigurationChange(paramConfiguration);
      AndroidUtilities.checkDisplaySize(applicationContext, paramConfiguration);
      return;
    }
    catch (Exception paramConfiguration)
    {
      paramConfiguration.printStackTrace();
    }
  }

  public void onCreate()
  {
    super.onCreate();
    applicationContext = getApplicationContext();
    NativeLoader.initNativeLibs(applicationContext);
    if ((Build.VERSION.SDK_INT == 14) || (Build.VERSION.SDK_INT == 15));
    for (boolean bool = true; ; bool = false)
    {
      ConnectionsManager.native_setJava(bool);
      new ForegroundDetector(this);
      applicationHandler = new Handler(applicationContext.getMainLooper());
      startPushService();
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ApplicationLoader
 * JD-Core Version:    0.6.0
 */