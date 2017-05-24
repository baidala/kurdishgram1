package org.vidogram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLRPC.TL_account_tmpPassword;
import org.vidogram.tgnet.TLRPC.User;

public class UserConfig
{
  public static boolean allowScreenCapture;
  public static boolean appLocked;
  public static int autoLockIn;
  public static boolean blockedUsersLoaded;
  public static String contactsHash;
  private static TLRPC.User currentUser;
  public static boolean draftsLoaded;
  public static boolean isWaitingForPasscodeEnter;
  public static int lastBroadcastId;
  public static int lastContactsSyncTime;
  public static int lastHintsSyncTime;
  public static int lastLocalId;
  public static int lastPauseTime;
  public static int lastSendMessageId;
  public static String lastUpdateVersion;
  public static long migrateOffsetAccess;
  public static int migrateOffsetChannelId;
  public static int migrateOffsetChatId;
  public static int migrateOffsetDate;
  public static int migrateOffsetId;
  public static int migrateOffsetUserId;
  public static boolean notificationsConverted;
  public static String passcodeHash;
  public static byte[] passcodeSalt;
  public static int passcodeType;
  public static boolean pinnedDialogsLoaded;
  public static String pushString = "";
  public static boolean registeredForPush;
  public static boolean saveIncomingPhotos;
  private static final Object sync;
  public static TLRPC.TL_account_tmpPassword tmpPassword;
  public static boolean useFingerprint;

  static
  {
    lastSendMessageId = -210000;
    lastLocalId = -210000;
    lastBroadcastId = -1;
    contactsHash = "";
    sync = new Object();
    passcodeHash = "";
    passcodeSalt = new byte[0];
    autoLockIn = 3600;
    useFingerprint = true;
    notificationsConverted = true;
    pinnedDialogsLoaded = true;
    migrateOffsetId = -1;
    migrateOffsetDate = -1;
    migrateOffsetUserId = -1;
    migrateOffsetChatId = -1;
    migrateOffsetChannelId = -1;
    migrateOffsetAccess = -1L;
  }

  public static boolean checkPasscode(String paramString)
  {
    boolean bool;
    byte[] arrayOfByte;
    if (passcodeSalt.length == 0)
    {
      bool = Utilities.MD5(paramString).equals(passcodeHash);
      if (bool);
      try
      {
        passcodeSalt = new byte[16];
        Utilities.random.nextBytes(passcodeSalt);
        paramString = paramString.getBytes("UTF-8");
        arrayOfByte = new byte[paramString.length + 32];
        System.arraycopy(passcodeSalt, 0, arrayOfByte, 0, 16);
        System.arraycopy(paramString, 0, arrayOfByte, 16, paramString.length);
        System.arraycopy(passcodeSalt, 0, arrayOfByte, paramString.length + 16, 16);
        passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length));
        saveConfig(false);
        return bool;
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        return bool;
      }
    }
    try
    {
      paramString = paramString.getBytes("UTF-8");
      arrayOfByte = new byte[paramString.length + 32];
      System.arraycopy(passcodeSalt, 0, arrayOfByte, 0, 16);
      System.arraycopy(paramString, 0, arrayOfByte, 16, paramString.length);
      System.arraycopy(passcodeSalt, 0, arrayOfByte, paramString.length + 16, 16);
      paramString = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length));
      bool = passcodeHash.equals(paramString);
      return bool;
    }
    catch (Exception paramString)
    {
      FileLog.e(paramString);
    }
    return false;
  }

  public static void clearConfig()
  {
    currentUser = null;
    registeredForPush = false;
    contactsHash = "";
    lastSendMessageId = -210000;
    lastBroadcastId = -1;
    saveIncomingPhotos = false;
    blockedUsersLoaded = false;
    migrateOffsetId = -1;
    migrateOffsetDate = -1;
    migrateOffsetUserId = -1;
    migrateOffsetChatId = -1;
    migrateOffsetChannelId = -1;
    migrateOffsetAccess = -1L;
    appLocked = false;
    passcodeType = 0;
    passcodeHash = "";
    passcodeSalt = new byte[0];
    autoLockIn = 3600;
    lastPauseTime = 0;
    useFingerprint = true;
    draftsLoaded = true;
    notificationsConverted = true;
    isWaitingForPasscodeEnter = false;
    allowScreenCapture = false;
    pinnedDialogsLoaded = false;
    lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
    lastContactsSyncTime = (int)(System.currentTimeMillis() / 1000L) - 82800;
    lastHintsSyncTime = (int)(System.currentTimeMillis() / 1000L) - 90000;
    saveConfig(true);
  }

  public static int getClientUserId()
  {
    while (true)
    {
      synchronized (sync)
      {
        if (currentUser != null)
        {
          i = currentUser.id;
          return i;
        }
      }
      int i = 0;
    }
  }

  public static TLRPC.User getCurrentUser()
  {
    synchronized (sync)
    {
      TLRPC.User localUser = currentUser;
      return localUser;
    }
  }

  public static int getNewMessageId()
  {
    synchronized (sync)
    {
      int i = lastSendMessageId;
      lastSendMessageId -= 1;
      return i;
    }
  }

  public static boolean isClientActivated()
  {
    while (true)
    {
      synchronized (sync)
      {
        if (currentUser != null)
        {
          i = 1;
          return i;
        }
      }
      int i = 0;
    }
  }

  public static void loadConfig()
  {
    boolean bool;
    Object localObject4;
    int i;
    Object localObject5;
    Object localObject3;
    label1021: 
    do
    {
      synchronized (sync)
      {
        File localFile = new File(ApplicationLoader.getFilesDirFixed(), "user.dat");
        bool = localFile.exists();
        if (bool)
          try
          {
            localObject4 = new SerializedData(localFile);
            i = ((SerializedData)localObject4).readInt32(false);
            if (i == 1)
            {
              currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
              MessagesStorage.lastDateValue = ((SerializedData)localObject4).readInt32(false);
              MessagesStorage.lastPtsValue = ((SerializedData)localObject4).readInt32(false);
              MessagesStorage.lastSeqValue = ((SerializedData)localObject4).readInt32(false);
              registeredForPush = ((SerializedData)localObject4).readBool(false);
              pushString = ((SerializedData)localObject4).readString(false);
              lastSendMessageId = ((SerializedData)localObject4).readInt32(false);
              lastLocalId = ((SerializedData)localObject4).readInt32(false);
              contactsHash = ((SerializedData)localObject4).readString(false);
              ((SerializedData)localObject4).readString(false);
              saveIncomingPhotos = ((SerializedData)localObject4).readBool(false);
              MessagesStorage.lastQtsValue = ((SerializedData)localObject4).readInt32(false);
              MessagesStorage.lastSecretVersion = ((SerializedData)localObject4).readInt32(false);
              if (((SerializedData)localObject4).readInt32(false) == 1)
                MessagesStorage.secretPBytes = ((SerializedData)localObject4).readByteArray(false);
              MessagesStorage.secretG = ((SerializedData)localObject4).readInt32(false);
              Utilities.stageQueue.postRunnable(new Runnable(localFile)
              {
                public void run()
                {
                  UserConfig.saveConfig(true, this.val$configFile);
                }
              });
            }
            while (true)
            {
              if (lastLocalId > -210000)
                lastLocalId = -210000;
              if (lastSendMessageId > -210000)
                lastSendMessageId = -210000;
              ((SerializedData)localObject4).cleanup();
              Utilities.stageQueue.postRunnable(new Runnable(localFile)
              {
                public void run()
                {
                  UserConfig.saveConfig(true, this.val$configFile);
                }
              });
              return;
              if (i != 2)
                continue;
              currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
              localObject5 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
              registeredForPush = ((SharedPreferences)localObject5).getBoolean("registeredForPush", false);
              pushString = ((SharedPreferences)localObject5).getString("pushString2", "");
              lastSendMessageId = ((SharedPreferences)localObject5).getInt("lastSendMessageId", -210000);
              lastLocalId = ((SharedPreferences)localObject5).getInt("lastLocalId", -210000);
              contactsHash = ((SharedPreferences)localObject5).getString("contactsHash", "");
              saveIncomingPhotos = ((SharedPreferences)localObject5).getBoolean("saveIncomingPhotos", false);
            }
          }
          catch (Exception localException1)
          {
            while (true)
              FileLog.e(localException1);
          }
      }
      localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
      registeredForPush = ((SharedPreferences)localObject3).getBoolean("registeredForPush", false);
      pushString = ((SharedPreferences)localObject3).getString("pushString2", "");
      lastSendMessageId = ((SharedPreferences)localObject3).getInt("lastSendMessageId", -210000);
      lastLocalId = ((SharedPreferences)localObject3).getInt("lastLocalId", -210000);
      contactsHash = ((SharedPreferences)localObject3).getString("contactsHash", "");
      saveIncomingPhotos = ((SharedPreferences)localObject3).getBoolean("saveIncomingPhotos", false);
      lastBroadcastId = ((SharedPreferences)localObject3).getInt("lastBroadcastId", -1);
      blockedUsersLoaded = ((SharedPreferences)localObject3).getBoolean("blockedUsersLoaded", false);
      passcodeHash = ((SharedPreferences)localObject3).getString("passcodeHash1", "");
      appLocked = ((SharedPreferences)localObject3).getBoolean("appLocked", false);
      passcodeType = ((SharedPreferences)localObject3).getInt("passcodeType", 0);
      autoLockIn = ((SharedPreferences)localObject3).getInt("autoLockIn", 3600);
      lastPauseTime = ((SharedPreferences)localObject3).getInt("lastPauseTime", 0);
      useFingerprint = ((SharedPreferences)localObject3).getBoolean("useFingerprint", true);
      lastUpdateVersion = ((SharedPreferences)localObject3).getString("lastUpdateVersion2", "3.5");
      lastContactsSyncTime = ((SharedPreferences)localObject3).getInt("lastContactsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 82800);
      lastHintsSyncTime = ((SharedPreferences)localObject3).getInt("lastHintsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 90000);
      draftsLoaded = ((SharedPreferences)localObject3).getBoolean("draftsLoaded", false);
      notificationsConverted = ((SharedPreferences)localObject3).getBoolean("notificationsConverted", false);
      allowScreenCapture = ((SharedPreferences)localObject3).getBoolean("allowScreenCapture", false);
      pinnedDialogsLoaded = ((SharedPreferences)localObject3).getBoolean("pinnedDialogsLoaded", false);
      if ((passcodeHash.length() > 0) && (lastPauseTime == 0))
        lastPauseTime = (int)(System.currentTimeMillis() / 1000L - 600L);
      migrateOffsetId = ((SharedPreferences)localObject3).getInt("migrateOffsetId", 0);
      if (migrateOffsetId != -1)
      {
        migrateOffsetDate = ((SharedPreferences)localObject3).getInt("migrateOffsetDate", 0);
        migrateOffsetUserId = ((SharedPreferences)localObject3).getInt("migrateOffsetUserId", 0);
        migrateOffsetChatId = ((SharedPreferences)localObject3).getInt("migrateOffsetChatId", 0);
        migrateOffsetChannelId = ((SharedPreferences)localObject3).getInt("migrateOffsetChannelId", 0);
        migrateOffsetAccess = ((SharedPreferences)localObject3).getLong("migrateOffsetAccess", 0L);
      }
      localObject4 = ((SharedPreferences)localObject3).getString("tmpPassword", null);
      if (localObject4 != null)
      {
        localObject4 = Base64.decode((String)localObject4, 0);
        if (localObject4 != null)
        {
          localObject4 = new SerializedData(localObject4);
          tmpPassword = TLRPC.TL_account_tmpPassword.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
          ((SerializedData)localObject4).cleanup();
        }
      }
      localObject4 = ((SharedPreferences)localObject3).getString("user", null);
      if (localObject4 != null)
      {
        localObject4 = Base64.decode((String)localObject4, 0);
        if (localObject4 != null)
        {
          localObject4 = new SerializedData(localObject4);
          currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
          ((SerializedData)localObject4).cleanup();
        }
      }
      localObject3 = ((SharedPreferences)localObject3).getString("passcodeSalt", "");
      if (((String)localObject3).length() <= 0)
        break;
      passcodeSalt = Base64.decode((String)localObject3, 0);
      bool = notificationsConverted;
    }
    while (bool);
    label1084: Map.Entry localEntry;
    String str;
    long l;
    try
    {
      localObject3 = new ArrayList();
      localObject4 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      Object localObject6 = ((SharedPreferences)localObject4).getAll();
      localObject5 = LocaleController.getString("SoundDefault", 2131166481);
      localObject6 = ((Map)localObject6).entrySet().iterator();
      while (true)
      {
        if (!((Iterator)localObject6).hasNext())
          break label1517;
        localEntry = (Map.Entry)((Iterator)localObject6).next();
        str = (String)localEntry.getKey();
        if (!str.startsWith("sound_"))
          break;
        if (((String)localEntry.getValue()).equals(localObject5))
          continue;
        l = Utilities.parseLong(str).longValue();
        if (((ArrayList)localObject3).contains(Long.valueOf(l)))
          continue;
        ((ArrayList)localObject3).add(Long.valueOf(l));
      }
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
    }
    while (true)
    {
      notificationsConverted = true;
      saveConfig(false);
      break;
      passcodeSalt = new byte[0];
      break label1021;
      if (str.startsWith("vibrate_"))
      {
        if (((Integer)localEntry.getValue()).intValue() == 0)
          break label1084;
        l = Utilities.parseLong(str).longValue();
        if (localException2.contains(Long.valueOf(l)))
          break label1084;
        localException2.add(Long.valueOf(l));
        break label1084;
      }
      if (str.startsWith("priority_"))
      {
        if (((Integer)localEntry.getValue()).intValue() == 0)
          break label1084;
        l = Utilities.parseLong(str).longValue();
        if (localException2.contains(Long.valueOf(l)))
          break label1084;
        localException2.add(Long.valueOf(l));
        break label1084;
      }
      if (str.startsWith("color_"))
      {
        if (((Integer)localEntry.getValue()).intValue() == 0)
          break label1084;
        l = Utilities.parseLong(str).longValue();
        if (localException2.contains(Long.valueOf(l)))
          break label1084;
        localException2.add(Long.valueOf(l));
        break label1084;
      }
      if (str.startsWith("smart_max_count_"))
      {
        if (((Integer)localEntry.getValue()).intValue() == 2)
          break label1084;
        l = Utilities.parseLong(str).longValue();
        if (localException2.contains(Long.valueOf(l)))
          break label1084;
        localException2.add(Long.valueOf(l));
        break label1084;
      }
      if ((!str.startsWith("smart_delay_")) || (((Integer)localEntry.getValue()).intValue() == 180))
        break label1084;
      l = Utilities.parseLong(str).longValue();
      if (localException2.contains(Long.valueOf(l)))
        break label1084;
      localException2.add(Long.valueOf(l));
      break label1084;
      label1517: if (localException2.isEmpty())
        continue;
      localObject4 = ((SharedPreferences)localObject4).edit();
      i = 0;
      while (i < localException2.size())
      {
        ((SharedPreferences.Editor)localObject4).putBoolean("custom_" + localException2.get(i), true);
        i += 1;
      }
      ((SharedPreferences.Editor)localObject4).commit();
    }
  }

  public static void saveConfig(boolean paramBoolean)
  {
    saveConfig(paramBoolean, null);
  }

  public static void saveConfig(boolean paramBoolean, File paramFile)
  {
    while (true)
    {
      SharedPreferences.Editor localEditor;
      synchronized (sync)
      {
        try
        {
          localEditor = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
          localEditor.putBoolean("registeredForPush", registeredForPush);
          localEditor.putString("pushString2", pushString);
          localEditor.putInt("lastSendMessageId", lastSendMessageId);
          localEditor.putInt("lastLocalId", lastLocalId);
          localEditor.putString("contactsHash", contactsHash);
          localEditor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
          localEditor.putInt("lastBroadcastId", lastBroadcastId);
          localEditor.putBoolean("blockedUsersLoaded", blockedUsersLoaded);
          localEditor.putString("passcodeHash1", passcodeHash);
          if (passcodeSalt.length <= 0)
            continue;
          Object localObject1 = Base64.encodeToString(passcodeSalt, 0);
          localEditor.putString("passcodeSalt", (String)localObject1);
          localEditor.putBoolean("appLocked", appLocked);
          localEditor.putInt("passcodeType", passcodeType);
          localEditor.putInt("autoLockIn", autoLockIn);
          localEditor.putInt("lastPauseTime", lastPauseTime);
          localEditor.putString("lastUpdateVersion2", lastUpdateVersion);
          localEditor.putInt("lastContactsSyncTime", lastContactsSyncTime);
          localEditor.putBoolean("useFingerprint", useFingerprint);
          localEditor.putInt("lastHintsSyncTime", lastHintsSyncTime);
          localEditor.putBoolean("draftsLoaded", draftsLoaded);
          localEditor.putBoolean("notificationsConverted", notificationsConverted);
          localEditor.putBoolean("allowScreenCapture", allowScreenCapture);
          localEditor.putBoolean("pinnedDialogsLoaded", pinnedDialogsLoaded);
          localEditor.putInt("migrateOffsetId", migrateOffsetId);
          if (migrateOffsetId == -1)
            continue;
          localEditor.putInt("migrateOffsetDate", migrateOffsetDate);
          localEditor.putInt("migrateOffsetUserId", migrateOffsetUserId);
          localEditor.putInt("migrateOffsetChatId", migrateOffsetChatId);
          localEditor.putInt("migrateOffsetChannelId", migrateOffsetChannelId);
          localEditor.putLong("migrateOffsetAccess", migrateOffsetAccess);
          if (tmpPassword == null)
            continue;
          localObject1 = new SerializedData();
          tmpPassword.serializeToStream((AbstractSerializedData)localObject1);
          localEditor.putString("tmpPassword", Base64.encodeToString(((SerializedData)localObject1).toByteArray(), 0));
          ((SerializedData)localObject1).cleanup();
          if (currentUser != null)
          {
            if (!paramBoolean)
              continue;
            localObject1 = new SerializedData();
            currentUser.serializeToStream((AbstractSerializedData)localObject1);
            localEditor.putString("user", Base64.encodeToString(((SerializedData)localObject1).toByteArray(), 0));
            ((SerializedData)localObject1).cleanup();
            localEditor.commit();
            if (paramFile == null)
              continue;
            paramFile.delete();
            return;
            localObject1 = "";
            continue;
            localEditor.remove("tmpPassword");
            continue;
          }
        }
        catch (Exception paramFile)
        {
          FileLog.e(paramFile);
          continue;
        }
      }
      localEditor.remove("user");
    }
  }

  public static void setCurrentUser(TLRPC.User paramUser)
  {
    synchronized (sync)
    {
      currentUser = paramUser;
      return;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.UserConfig
 * JD-Core Version:    0.6.0
 */