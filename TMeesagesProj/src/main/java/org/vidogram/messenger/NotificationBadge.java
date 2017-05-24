package org.vidogram.messenger;

import android.annotation.TargetApi;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class NotificationBadge
{
  private static final List<Class<? extends Badger>> BADGERS = new LinkedList();
  private static Badger badger;
  private static ComponentName componentName;
  private static boolean initied;

  static
  {
    BADGERS.add(AdwHomeBadger.class);
    BADGERS.add(ApexHomeBadger.class);
    BADGERS.add(NewHtcHomeBadger.class);
    BADGERS.add(NovaHomeBadger.class);
    BADGERS.add(SonyHomeBadger.class);
    BADGERS.add(XiaomiHomeBadger.class);
    BADGERS.add(AsusHomeBadger.class);
    BADGERS.add(HuaweiHomeBadger.class);
    BADGERS.add(OPPOHomeBader.class);
    BADGERS.add(SamsungHomeBadger.class);
    BADGERS.add(ZukHomeBadger.class);
    BADGERS.add(VivoHomeBadger.class);
  }

  public static boolean applyCount(int paramInt)
  {
    try
    {
      if ((badger == null) && (!initied))
      {
        initBadger();
        initied = true;
      }
      if (badger == null)
        return false;
      badger.executeBadge(paramInt);
      return true;
    }
    catch (Throwable localThrowable)
    {
    }
    return false;
  }

  private static boolean canResolveBroadcast(Intent paramIntent)
  {
    int j = 0;
    paramIntent = ApplicationLoader.applicationContext.getPackageManager().queryBroadcastReceivers(paramIntent, 0);
    int i = j;
    if (paramIntent != null)
    {
      i = j;
      if (paramIntent.size() > 0)
        i = 1;
    }
    return i;
  }

  public static void close(Cursor paramCursor)
  {
    if ((paramCursor != null) && (!paramCursor.isClosed()))
      paramCursor.close();
  }

  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable != null);
    try
    {
      paramCloseable.close();
      return;
    }
    catch (Throwable paramCloseable)
    {
    }
  }

  private static boolean initBadger()
  {
    Object localObject1 = ApplicationLoader.applicationContext;
    Object localObject3 = ((Context)localObject1).getPackageManager().getLaunchIntentForPackage(((Context)localObject1).getPackageName());
    if (localObject3 == null);
    do
    {
      return false;
      componentName = ((Intent)localObject3).getComponent();
      localObject3 = new Intent("android.intent.action.MAIN");
      ((Intent)localObject3).addCategory("android.intent.category.HOME");
      localObject1 = ((Context)localObject1).getPackageManager().resolveActivity((Intent)localObject3, 65536);
    }
    while ((localObject1 == null) || (((ResolveInfo)localObject1).activityInfo.name.toLowerCase().contains("resolver")));
    localObject3 = ((ResolveInfo)localObject1).activityInfo.packageName;
    Iterator localIterator = BADGERS.iterator();
    while (true)
    {
      if (localIterator.hasNext())
        localObject1 = (Class)localIterator.next();
      try
      {
        localObject1 = (Badger)((Class)localObject1).newInstance();
        if ((localObject1 == null) || (!((Badger)localObject1).getSupportLaunchers().contains(localObject3)))
          continue;
        badger = (Badger)localObject1;
        if (badger == null)
        {
          if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi"))
            badger = new XiaomiHomeBadger();
        }
        else
          return true;
      }
      catch (Exception localObject2)
      {
        while (true)
        {
          Object localObject2 = null;
          continue;
          if (Build.MANUFACTURER.equalsIgnoreCase("ZUK"))
          {
            badger = new ZukHomeBadger();
            continue;
          }
          if (Build.MANUFACTURER.equalsIgnoreCase("OPPO"))
          {
            badger = new OPPOHomeBader();
            continue;
          }
          if (Build.MANUFACTURER.equalsIgnoreCase("VIVO"))
          {
            badger = new VivoHomeBadger();
            continue;
          }
          badger = new DefaultBadger();
        }
      }
    }
  }

  public static class AdwHomeBadger
    implements NotificationBadge.Badger
  {
    public static final String CLASSNAME = "CNAME";
    public static final String COUNT = "COUNT";
    public static final String INTENT_UPDATE_COUNTER = "org.adw.launcher.counter.SEND";
    public static final String PACKAGENAME = "PNAME";

    public void executeBadge(int paramInt)
    {
      Intent localIntent = new Intent("org.adw.launcher.counter.SEND");
      localIntent.putExtra("PNAME", NotificationBadge.componentName.getPackageName());
      localIntent.putExtra("CNAME", NotificationBadge.componentName.getClassName());
      localIntent.putExtra("COUNT", paramInt);
      if (NotificationBadge.access$100(localIntent))
        AndroidUtilities.runOnUIThread(new Runnable(localIntent)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "org.adw.launcher", "org.adwfreak.launcher" });
    }
  }

  public static class ApexHomeBadger
    implements NotificationBadge.Badger
  {
    private static final String CLASS = "class";
    private static final String COUNT = "count";
    private static final String INTENT_UPDATE_COUNTER = "com.anddoes.launcher.COUNTER_CHANGED";
    private static final String PACKAGENAME = "package";

    public void executeBadge(int paramInt)
    {
      Intent localIntent = new Intent("com.anddoes.launcher.COUNTER_CHANGED");
      localIntent.putExtra("package", NotificationBadge.componentName.getPackageName());
      localIntent.putExtra("count", paramInt);
      localIntent.putExtra("class", NotificationBadge.componentName.getClassName());
      if (NotificationBadge.access$100(localIntent))
        AndroidUtilities.runOnUIThread(new Runnable(localIntent)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.anddoes.launcher" });
    }
  }

  public static class AsusHomeBadger
    implements NotificationBadge.Badger
  {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

    public void executeBadge(int paramInt)
    {
      Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
      localIntent.putExtra("badge_count", paramInt);
      localIntent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
      localIntent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
      localIntent.putExtra("badge_vip_count", 0);
      if (NotificationBadge.access$100(localIntent))
        AndroidUtilities.runOnUIThread(new Runnable(localIntent)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.asus.launcher" });
    }
  }

  public static abstract interface Badger
  {
    public abstract void executeBadge(int paramInt);

    public abstract List<String> getSupportLaunchers();
  }

  public static class DefaultBadger
    implements NotificationBadge.Badger
  {
    private static final String INTENT_ACTION = "android.intent.action.BADGE_COUNT_UPDATE";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "badge_count_class_name";
    private static final String INTENT_EXTRA_BADGE_COUNT = "badge_count";
    private static final String INTENT_EXTRA_PACKAGENAME = "badge_count_package_name";

    public void executeBadge(int paramInt)
    {
      Intent localIntent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
      localIntent.putExtra("badge_count", paramInt);
      localIntent.putExtra("badge_count_package_name", NotificationBadge.componentName.getPackageName());
      localIntent.putExtra("badge_count_class_name", NotificationBadge.componentName.getClassName());
      AndroidUtilities.runOnUIThread(new Runnable(localIntent)
      {
        public void run()
        {
          try
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
            return;
          }
          catch (Exception localException)
          {
          }
        }
      });
    }

    public List<String> getSupportLaunchers()
    {
      return new ArrayList(0);
    }
  }

  public static class HuaweiHomeBadger
    implements NotificationBadge.Badger
  {
    public void executeBadge(int paramInt)
    {
      Bundle localBundle = new Bundle();
      localBundle.putString("package", ApplicationLoader.applicationContext.getPackageName());
      localBundle.putString("class", NotificationBadge.componentName.getClassName());
      localBundle.putInt("badgenumber", paramInt);
      AndroidUtilities.runOnUIThread(new Runnable(localBundle)
      {
        public void run()
        {
          try
          {
            ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, this.val$localBundle);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.huawei.android.launcher" });
    }
  }

  public static class NewHtcHomeBadger
    implements NotificationBadge.Badger
  {
    public static final String COUNT = "count";
    public static final String EXTRA_COMPONENT = "com.htc.launcher.extra.COMPONENT";
    public static final String EXTRA_COUNT = "com.htc.launcher.extra.COUNT";
    public static final String INTENT_SET_NOTIFICATION = "com.htc.launcher.action.SET_NOTIFICATION";
    public static final String INTENT_UPDATE_SHORTCUT = "com.htc.launcher.action.UPDATE_SHORTCUT";
    public static final String PACKAGENAME = "packagename";

    public void executeBadge(int paramInt)
    {
      Intent localIntent1 = new Intent("com.htc.launcher.action.SET_NOTIFICATION");
      localIntent1.putExtra("com.htc.launcher.extra.COMPONENT", NotificationBadge.componentName.flattenToShortString());
      localIntent1.putExtra("com.htc.launcher.extra.COUNT", paramInt);
      Intent localIntent2 = new Intent("com.htc.launcher.action.UPDATE_SHORTCUT");
      localIntent2.putExtra("packagename", NotificationBadge.componentName.getPackageName());
      localIntent2.putExtra("count", paramInt);
      if ((NotificationBadge.access$100(localIntent1)) || (NotificationBadge.access$100(localIntent2)))
        AndroidUtilities.runOnUIThread(new Runnable(localIntent1, localIntent2)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent1);
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.htc.launcher" });
    }
  }

  public static class NovaHomeBadger
    implements NotificationBadge.Badger
  {
    private static final String CONTENT_URI = "content://com.teslacoilsw.notifier/unread_count";
    private static final String COUNT = "count";
    private static final String TAG = "tag";

    public void executeBadge(int paramInt)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("tag", NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
      localContentValues.put("count", Integer.valueOf(paramInt));
      ApplicationLoader.applicationContext.getContentResolver().insert(Uri.parse("content://com.teslacoilsw.notifier/unread_count"), localContentValues);
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.teslacoilsw.launcher" });
    }
  }

  public static class OPPOHomeBader
    implements NotificationBadge.Badger
  {
    private static final String INTENT_ACTION = "com.oppo.unsettledevent";
    private static final String INTENT_EXTRA_BADGEUPGRADE_COUNT = "app_badge_count";
    private static final String INTENT_EXTRA_BADGE_COUNT = "number";
    private static final String INTENT_EXTRA_BADGE_UPGRADENUMBER = "upgradeNumber";
    private static final String INTENT_EXTRA_PACKAGENAME = "pakeageName";
    private static final String PROVIDER_CONTENT_URI = "content://com.android.badge/badge";
    private static int ROMVERSION = -1;

    private boolean checkObjExists(Object paramObject)
    {
      return (paramObject == null) || (paramObject.toString().equals("")) || (paramObject.toString().trim().equals("null"));
    }

    private Object executeClassLoad(Class paramClass, String paramString, Class[] paramArrayOfClass, Object[] paramArrayOfObject)
    {
      Object localObject2 = null;
      Object localObject1 = localObject2;
      if (paramClass != null)
      {
        localObject1 = localObject2;
        if (!checkObjExists(paramString))
        {
          paramClass = getMethod(paramClass, paramString, paramArrayOfClass);
          localObject1 = localObject2;
          if (paramClass != null)
            paramClass.setAccessible(true);
        }
      }
      try
      {
        localObject1 = paramClass.invoke(null, paramArrayOfObject);
        return localObject1;
      }
      catch (Throwable paramClass)
      {
      }
      return null;
    }

    private Class getClass(String paramString)
    {
      try
      {
        paramString = Class.forName(paramString);
        return paramString;
      }
      catch (java.lang.ClassNotFoundException paramString)
      {
      }
      return null;
    }

    private Method getMethod(Class paramClass, String paramString, Class[] paramArrayOfClass)
    {
      if ((paramClass == null) || (checkObjExists(paramString)));
      do
      {
        return null;
        try
        {
          paramClass.getMethods();
          paramClass.getDeclaredMethods();
          Method localMethod1 = paramClass.getDeclaredMethod(paramString, paramArrayOfClass);
          return localMethod1;
        }
        catch (Exception localException2)
        {
          try
          {
            Method localMethod2 = paramClass.getMethod(paramString, paramArrayOfClass);
            return localMethod2;
          }
          catch (Exception localException2)
          {
          }
        }
      }
      while (paramClass.getSuperclass() == null);
      return getMethod(paramClass.getSuperclass(), paramString, paramArrayOfClass);
    }

    private int getSupportVersion()
    {
      if (ROMVERSION >= 0)
        return ROMVERSION;
      try
      {
        i = ((Integer)executeClassLoad(getClass("com.color.os.ColorBuild"), "getColorOSVERSION", null, null)).intValue();
        if (i == 0)
          try
          {
            String str = getSystemProperty("ro.build.version.opporom");
            if (str.startsWith("V1.4"))
              return 3;
            if (str.startsWith("V2.0"))
              return 4;
            boolean bool = str.startsWith("V2.1");
            if (bool)
              return 5;
          }
          catch (Exception localException1)
          {
          }
        ROMVERSION = i;
        return ROMVERSION;
      }
      catch (Exception localException2)
      {
        while (true)
          int i = 0;
      }
    }

    // ERROR //
    private String getSystemProperty(String paramString)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_2
      //   2: new 141	java/io/BufferedReader
      //   5: dup
      //   6: new 143	java/io/InputStreamReader
      //   9: dup
      //   10: invokestatic 149	java/lang/Runtime:getRuntime	()Ljava/lang/Runtime;
      //   13: new 151	java/lang/StringBuilder
      //   16: dup
      //   17: invokespecial 152	java/lang/StringBuilder:<init>	()V
      //   20: ldc 154
      //   22: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   25: aload_1
      //   26: invokevirtual 158	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
      //   29: invokevirtual 159	java/lang/StringBuilder:toString	()Ljava/lang/String;
      //   32: invokevirtual 163	java/lang/Runtime:exec	(Ljava/lang/String;)Ljava/lang/Process;
      //   35: invokevirtual 169	java/lang/Process:getInputStream	()Ljava/io/InputStream;
      //   38: invokespecial 172	java/io/InputStreamReader:<init>	(Ljava/io/InputStream;)V
      //   41: sipush 1024
      //   44: invokespecial 175	java/io/BufferedReader:<init>	(Ljava/io/Reader;I)V
      //   47: astore_1
      //   48: aload_1
      //   49: invokevirtual 178	java/io/BufferedReader:readLine	()Ljava/lang/String;
      //   52: astore_2
      //   53: aload_1
      //   54: invokevirtual 181	java/io/BufferedReader:close	()V
      //   57: aload_1
      //   58: invokestatic 185	org/vidogram/messenger/NotificationBadge:closeQuietly	(Ljava/io/Closeable;)V
      //   61: aload_2
      //   62: areturn
      //   63: astore_1
      //   64: aconst_null
      //   65: astore_1
      //   66: aload_1
      //   67: invokestatic 185	org/vidogram/messenger/NotificationBadge:closeQuietly	(Ljava/io/Closeable;)V
      //   70: aconst_null
      //   71: areturn
      //   72: astore_1
      //   73: aload_2
      //   74: invokestatic 185	org/vidogram/messenger/NotificationBadge:closeQuietly	(Ljava/io/Closeable;)V
      //   77: aload_1
      //   78: athrow
      //   79: astore_3
      //   80: aload_1
      //   81: astore_2
      //   82: aload_3
      //   83: astore_1
      //   84: goto -11 -> 73
      //   87: astore_2
      //   88: goto -22 -> 66
      //
      // Exception table:
      //   from	to	target	type
      //   2	48	63	java/lang/Throwable
      //   2	48	72	finally
      //   48	57	79	finally
      //   48	57	87	java/lang/Throwable
    }

    @TargetApi(11)
    public void executeBadge(int paramInt)
    {
      int i = paramInt;
      if (paramInt == 0)
        i = -1;
      Object localObject = new Intent("com.oppo.unsettledevent");
      ((Intent)localObject).putExtra("pakeageName", NotificationBadge.componentName.getPackageName());
      ((Intent)localObject).putExtra("number", i);
      ((Intent)localObject).putExtra("upgradeNumber", i);
      if (NotificationBadge.access$100((Intent)localObject))
        AndroidUtilities.runOnUIThread(new Runnable((Intent)localObject)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
      do
        return;
      while (getSupportVersion() != 6);
      try
      {
        localObject = new Bundle();
        ((Bundle)localObject).putInt("app_badge_count", i);
        AndroidUtilities.runOnUIThread(new Runnable((Bundle)localObject)
        {
          public void run()
          {
            try
            {
              ApplicationLoader.applicationContext.getContentResolver().call(Uri.parse("content://com.android.badge/badge"), "setAppBadgeCount", null, this.val$extras);
              return;
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
          }
        });
        return;
      }
      catch (Throwable localThrowable)
      {
      }
    }

    public List<String> getSupportLaunchers()
    {
      return Collections.singletonList("com.oppo.launcher");
    }
  }

  public static class SamsungHomeBadger
    implements NotificationBadge.Badger
  {
    private static final String[] CONTENT_PROJECTION = { "_id", "class" };
    private static final String CONTENT_URI = "content://com.sec.badge/apps?notify=true";
    private static NotificationBadge.DefaultBadger defaultBadger;

    private ContentValues getContentValues(ComponentName paramComponentName, int paramInt, boolean paramBoolean)
    {
      ContentValues localContentValues = new ContentValues();
      if (paramBoolean)
      {
        localContentValues.put("package", paramComponentName.getPackageName());
        localContentValues.put("class", paramComponentName.getClassName());
      }
      localContentValues.put("badgecount", Integer.valueOf(paramInt));
      return localContentValues;
    }

    // ERROR //
    public void executeBadge(int paramInt)
    {
      // Byte code:
      //   0: getstatic 68	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:defaultBadger	Lorg/vidogram/messenger/NotificationBadge$DefaultBadger;
      //   3: ifnonnull +13 -> 16
      //   6: new 70	org/vidogram/messenger/NotificationBadge$DefaultBadger
      //   9: dup
      //   10: invokespecial 71	org/vidogram/messenger/NotificationBadge$DefaultBadger:<init>	()V
      //   13: putstatic 68	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:defaultBadger	Lorg/vidogram/messenger/NotificationBadge$DefaultBadger;
      //   16: getstatic 68	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:defaultBadger	Lorg/vidogram/messenger/NotificationBadge$DefaultBadger;
      //   19: iload_1
      //   20: invokevirtual 73	org/vidogram/messenger/NotificationBadge$DefaultBadger:executeBadge	(I)V
      //   23: ldc 15
      //   25: invokestatic 79	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
      //   28: astore 4
      //   30: getstatic 85	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   33: invokevirtual 91	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   36: astore 6
      //   38: aload 6
      //   40: aload 4
      //   42: getstatic 27	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:CONTENT_PROJECTION	[Ljava/lang/String;
      //   45: ldc 93
      //   47: iconst_1
      //   48: anewarray 21	java/lang/String
      //   51: dup
      //   52: iconst_0
      //   53: invokestatic 97	org/vidogram/messenger/NotificationBadge:access$000	()Landroid/content/ComponentName;
      //   56: invokevirtual 44	android/content/ComponentName:getPackageName	()Ljava/lang/String;
      //   59: aastore
      //   60: aconst_null
      //   61: invokevirtual 103	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
      //   64: astore 5
      //   66: aload 5
      //   68: ifnull +112 -> 180
      //   71: invokestatic 97	org/vidogram/messenger/NotificationBadge:access$000	()Landroid/content/ComponentName;
      //   74: invokevirtual 51	android/content/ComponentName:getClassName	()Ljava/lang/String;
      //   77: astore 7
      //   79: iconst_0
      //   80: istore_2
      //   81: aload 5
      //   83: invokeinterface 109 1 0
      //   88: ifeq +71 -> 159
      //   91: aload 5
      //   93: iconst_0
      //   94: invokeinterface 113 2 0
      //   99: istore_3
      //   100: aload 6
      //   102: aload 4
      //   104: aload_0
      //   105: invokestatic 97	org/vidogram/messenger/NotificationBadge:access$000	()Landroid/content/ComponentName;
      //   108: iload_1
      //   109: iconst_0
      //   110: invokespecial 115	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:getContentValues	(Landroid/content/ComponentName;IZ)Landroid/content/ContentValues;
      //   113: ldc 117
      //   115: iconst_1
      //   116: anewarray 21	java/lang/String
      //   119: dup
      //   120: iconst_0
      //   121: iload_3
      //   122: invokestatic 120	java/lang/String:valueOf	(I)Ljava/lang/String;
      //   125: aastore
      //   126: invokevirtual 124	android/content/ContentResolver:update	(Landroid/net/Uri;Landroid/content/ContentValues;Ljava/lang/String;[Ljava/lang/String;)I
      //   129: pop
      //   130: aload 7
      //   132: aload 5
      //   134: aload 5
      //   136: ldc 25
      //   138: invokeinterface 128 2 0
      //   143: invokeinterface 131 2 0
      //   148: invokevirtual 135	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   151: ifeq -70 -> 81
      //   154: iconst_1
      //   155: istore_2
      //   156: goto -75 -> 81
      //   159: iload_2
      //   160: ifne +20 -> 180
      //   163: aload 6
      //   165: aload 4
      //   167: aload_0
      //   168: invokestatic 97	org/vidogram/messenger/NotificationBadge:access$000	()Landroid/content/ComponentName;
      //   171: iload_1
      //   172: iconst_1
      //   173: invokespecial 115	org/vidogram/messenger/NotificationBadge$SamsungHomeBadger:getContentValues	(Landroid/content/ComponentName;IZ)Landroid/content/ContentValues;
      //   176: invokevirtual 139	android/content/ContentResolver:insert	(Landroid/net/Uri;Landroid/content/ContentValues;)Landroid/net/Uri;
      //   179: pop
      //   180: aload 5
      //   182: invokestatic 143	org/vidogram/messenger/NotificationBadge:close	(Landroid/database/Cursor;)V
      //   185: return
      //   186: astore 4
      //   188: aconst_null
      //   189: astore 5
      //   191: aload 5
      //   193: invokestatic 143	org/vidogram/messenger/NotificationBadge:close	(Landroid/database/Cursor;)V
      //   196: aload 4
      //   198: athrow
      //   199: astore 4
      //   201: goto -10 -> 191
      //   204: astore 4
      //   206: goto -183 -> 23
      //
      // Exception table:
      //   from	to	target	type
      //   38	66	186	finally
      //   71	79	199	finally
      //   81	154	199	finally
      //   163	180	199	finally
      //   0	16	204	java/lang/Exception
      //   16	23	204	java/lang/Exception
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.sec.android.app.launcher", "com.sec.android.app.twlauncher" });
    }
  }

  public static class SonyHomeBadger
    implements NotificationBadge.Badger
  {
    private static final String INTENT_ACTION = "com.sonyericsson.home.action.UPDATE_BADGE";
    private static final String INTENT_EXTRA_ACTIVITY_NAME = "com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME";
    private static final String INTENT_EXTRA_MESSAGE = "com.sonyericsson.home.intent.extra.badge.MESSAGE";
    private static final String INTENT_EXTRA_PACKAGE_NAME = "com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME";
    private static final String INTENT_EXTRA_SHOW_MESSAGE = "com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE";
    private static final String PROVIDER_COLUMNS_ACTIVITY_NAME = "activity_name";
    private static final String PROVIDER_COLUMNS_BADGE_COUNT = "badge_count";
    private static final String PROVIDER_COLUMNS_PACKAGE_NAME = "package_name";
    private static final String PROVIDER_CONTENT_URI = "content://com.sonymobile.home.resourceprovider/badge";
    private static final String SONY_HOME_PROVIDER_NAME = "com.sonymobile.home.resourceprovider";
    private static AsyncQueryHandler mQueryHandler;
    private final Uri BADGE_CONTENT_URI = Uri.parse("content://com.sonymobile.home.resourceprovider/badge");

    private static void executeBadgeByBroadcast(int paramInt)
    {
      Intent localIntent = new Intent("com.sonyericsson.home.action.UPDATE_BADGE");
      localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.PACKAGE_NAME", NotificationBadge.componentName.getPackageName());
      localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.ACTIVITY_NAME", NotificationBadge.componentName.getClassName());
      localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.MESSAGE", String.valueOf(paramInt));
      if (paramInt > 0);
      for (boolean bool = true; ; bool = false)
      {
        localIntent.putExtra("com.sonyericsson.home.intent.extra.badge.SHOW_MESSAGE", bool);
        AndroidUtilities.runOnUIThread(new Runnable(localIntent)
        {
          public void run()
          {
            ApplicationLoader.applicationContext.sendBroadcast(this.val$intent);
          }
        });
        return;
      }
    }

    private void executeBadgeByContentProvider(int paramInt)
    {
      if (paramInt < 0)
        return;
      if (mQueryHandler == null)
        mQueryHandler = new AsyncQueryHandler(ApplicationLoader.applicationContext.getApplicationContext().getContentResolver())
        {
          public void handleMessage(Message paramMessage)
          {
            try
            {
              super.handleMessage(paramMessage);
              return;
            }
            catch (Throwable paramMessage)
            {
            }
          }
        };
      insertBadgeAsync(paramInt, NotificationBadge.componentName.getPackageName(), NotificationBadge.componentName.getClassName());
    }

    private void insertBadgeAsync(int paramInt, String paramString1, String paramString2)
    {
      ContentValues localContentValues = new ContentValues();
      localContentValues.put("badge_count", Integer.valueOf(paramInt));
      localContentValues.put("package_name", paramString1);
      localContentValues.put("activity_name", paramString2);
      mQueryHandler.startInsert(0, null, this.BADGE_CONTENT_URI, localContentValues);
    }

    private static boolean sonyBadgeContentProviderExists()
    {
      int i = 0;
      if (ApplicationLoader.applicationContext.getPackageManager().resolveContentProvider("com.sonymobile.home.resourceprovider", 0) != null)
        i = 1;
      return i;
    }

    public void executeBadge(int paramInt)
    {
      if (sonyBadgeContentProviderExists())
      {
        executeBadgeByContentProvider(paramInt);
        return;
      }
      executeBadgeByBroadcast(paramInt);
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.sonyericsson.home", "com.sonymobile.home" });
    }
  }

  public static class VivoHomeBadger
    implements NotificationBadge.Badger
  {
    public void executeBadge(int paramInt)
    {
      Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
      localIntent.putExtra("packageName", ApplicationLoader.applicationContext.getPackageName());
      localIntent.putExtra("className", NotificationBadge.componentName.getClassName());
      localIntent.putExtra("notificationNum", paramInt);
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.vivo.launcher" });
    }
  }

  public static class XiaomiHomeBadger
    implements NotificationBadge.Badger
  {
    public static final String EXTRA_UPDATE_APP_COMPONENT_NAME = "android.intent.extra.update_application_component_name";
    public static final String EXTRA_UPDATE_APP_MSG_TEXT = "android.intent.extra.update_application_message_text";
    public static final String INTENT_ACTION = "android.intent.action.APPLICATION_MESSAGE_UPDATE";

    public void executeBadge(int paramInt)
    {
      try
      {
        localObject3 = Class.forName("android.app.MiuiNotification").newInstance();
        Field localField = localObject3.getClass().getDeclaredField("messageCount");
        localField.setAccessible(true);
        if (paramInt == 0);
        for (Object localObject1 = ""; ; localObject1 = Integer.valueOf(paramInt))
        {
          localField.set(localObject3, String.valueOf(localObject1));
          return;
        }
      }
      catch (Throwable localObject2)
      {
        Object localObject3 = new Intent("android.intent.action.APPLICATION_MESSAGE_UPDATE");
        ((Intent)localObject3).putExtra("android.intent.extra.update_application_component_name", NotificationBadge.componentName.getPackageName() + "/" + NotificationBadge.componentName.getClassName());
        if (paramInt == 0);
        for (Object localObject2 = ""; ; localObject2 = Integer.valueOf(paramInt))
        {
          ((Intent)localObject3).putExtra("android.intent.extra.update_application_message_text", String.valueOf(localObject2));
          if (!NotificationBadge.access$100((Intent)localObject3))
            break;
          AndroidUtilities.runOnUIThread(new Runnable((Intent)localObject3)
          {
            public void run()
            {
              ApplicationLoader.applicationContext.sendBroadcast(this.val$localIntent);
            }
          });
          return;
        }
      }
    }

    public List<String> getSupportLaunchers()
    {
      return Arrays.asList(new String[] { "com.miui.miuilite", "com.miui.home", "com.miui.miuihome", "com.miui.miuihome2", "com.miui.mihome", "com.miui.mihome2" });
    }
  }

  public static class ZukHomeBadger
    implements NotificationBadge.Badger
  {
    private final Uri CONTENT_URI = Uri.parse("content://com.android.badge/badge");

    @TargetApi(11)
    public void executeBadge(int paramInt)
    {
      Bundle localBundle = new Bundle();
      localBundle.putInt("app_badge_count", paramInt);
      AndroidUtilities.runOnUIThread(new Runnable(localBundle)
      {
        public void run()
        {
          try
          {
            ApplicationLoader.applicationContext.getContentResolver().call(NotificationBadge.ZukHomeBadger.this.CONTENT_URI, "setAppBadgeCount", null, this.val$extra);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }

    public List<String> getSupportLaunchers()
    {
      return Collections.singletonList("com.zui.launcher");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationBadge
 * JD-Core Version:    0.6.0
 */