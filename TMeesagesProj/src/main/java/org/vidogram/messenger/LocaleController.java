package org.vidogram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateFormat;
import itman.Vidofilm.b;
import itman.Vidofilm.b.a;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.TL_userEmpty;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.vidogram.tgnet.TLRPC.TL_userStatusRecently;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserStatus;

public class LocaleController
{
  private static volatile LocaleController Instance;
  static final int QUANTITY_FEW = 8;
  static final int QUANTITY_MANY = 16;
  static final int QUANTITY_ONE = 2;
  static final int QUANTITY_OTHER = 0;
  static final int QUANTITY_TWO = 4;
  static final int QUANTITY_ZERO = 1;
  private static boolean is24HourFormat;
  public static boolean isRTL = false;
  public static int nameDisplayOrder = 1;
  private HashMap<String, PluralRules> allRules = new HashMap();
  private boolean changingConfiguration = false;
  public FastDateFormat chatDate;
  public FastDateFormat chatFullDate;
  private HashMap<String, String> currencyValues;
  private Locale currentLocale;
  private LocaleInfo currentLocaleInfo;
  private PluralRules currentPluralRules;
  private LocaleInfo defaultLocalInfo;
  public FastDateFormat formatterDay;
  public FastDateFormat formatterMonth;
  public FastDateFormat formatterMonthYear;
  public FastDateFormat formatterStats;
  public FastDateFormat formatterWeek;
  public FastDateFormat formatterYear;
  public FastDateFormat formatterYearMax;
  private String languageOverride;
  public HashMap<String, LocaleInfo> languagesDict = new HashMap();
  private HashMap<String, String> localeValues = new HashMap();
  private ArrayList<LocaleInfo> otherLanguages = new ArrayList();
  public ArrayList<LocaleInfo> sortedLanguages = new ArrayList();
  private Locale systemDefaultLocale;
  private HashMap<String, String> translitChars;

  static
  {
    is24HourFormat = false;
    Instance = null;
  }

  public LocaleController()
  {
    Object localObject1 = new PluralRules_One();
    addRules(new String[] { "bem", "brx", "da", "de", "el", "en", "eo", "es", "et", "fi", "fo", "gl", "he", "iw", "it", "nb", "nl", "nn", "no", "sv", "af", "bg", "bn", "ca", "eu", "fur", "fy", "gu", "ha", "is", "ku", "lb", "ml", "mr", "nah", "ne", "om", "or", "pa", "pap", "ps", "so", "sq", "sw", "ta", "te", "tk", "ur", "zu", "mn", "gsw", "chr", "rm", "pt", "an", "ast" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Czech();
    addRules(new String[] { "cs", "sk" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_French();
    addRules(new String[] { "ff", "fr", "kab" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Balkan();
    addRules(new String[] { "hr", "ru", "sr", "uk", "be", "bs", "sh" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Latvian();
    addRules(new String[] { "lv" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Lithuanian();
    addRules(new String[] { "lt" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Polish();
    addRules(new String[] { "pl" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Romanian();
    addRules(new String[] { "ro", "mo" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Slovenian();
    addRules(new String[] { "sl" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Arabic();
    addRules(new String[] { "ar" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Macedonian();
    addRules(new String[] { "mk" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Welsh();
    addRules(new String[] { "cy" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Breton();
    addRules(new String[] { "br" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Langi();
    addRules(new String[] { "lag" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Tachelhit();
    addRules(new String[] { "shi" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Maltese();
    addRules(new String[] { "mt" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Two();
    addRules(new String[] { "ga", "se", "sma", "smi", "smj", "smn", "sms" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_Zero();
    addRules(new String[] { "ak", "am", "bh", "fil", "tl", "guw", "hi", "ln", "mg", "nso", "ti", "wa" }, (PluralRules)localObject1);
    localObject1 = new PluralRules_None();
    addRules(new String[] { "az", "bm", "fa", "ig", "hu", "ja", "kde", "kea", "ko", "my", "ses", "sg", "to", "tr", "vi", "wo", "yo", "zh", "bo", "dz", "id", "jv", "ka", "km", "kn", "ms", "th" }, (PluralRules)localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "English";
    ((LocaleInfo)localObject1).nameEnglish = "English";
    ((LocaleInfo)localObject1).shortName = "en";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Italiano";
    ((LocaleInfo)localObject1).nameEnglish = "Italian";
    ((LocaleInfo)localObject1).shortName = "it";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Español";
    ((LocaleInfo)localObject1).nameEnglish = "Spanish";
    ((LocaleInfo)localObject1).shortName = "es";
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Deutsch";
    ((LocaleInfo)localObject1).nameEnglish = "German";
    ((LocaleInfo)localObject1).shortName = "de";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Nederlands";
    ((LocaleInfo)localObject1).nameEnglish = "Dutch";
    ((LocaleInfo)localObject1).shortName = "nl";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "کوردی(ناوەندی)";
    ((LocaleInfo)localObject1).nameEnglish = "persian";
    ((LocaleInfo)localObject1).shortName = "fa";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "العربية";
    ((LocaleInfo)localObject1).nameEnglish = "Arabic";
    ((LocaleInfo)localObject1).shortName = "ar";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "Português (Brasil)";
    ((LocaleInfo)localObject1).nameEnglish = "Portuguese (Brazil)";
    ((LocaleInfo)localObject1).shortName = "pt_BR";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    localObject1 = new LocaleInfo();
    ((LocaleInfo)localObject1).name = "한국어";
    ((LocaleInfo)localObject1).nameEnglish = "Korean";
    ((LocaleInfo)localObject1).shortName = "ko";
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(localObject1);
    this.languagesDict.put(((LocaleInfo)localObject1).shortName, localObject1);
    loadOtherLanguages();
    localObject1 = this.otherLanguages.iterator();
    Object localObject2;
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (LocaleInfo)((Iterator)localObject1).next();
      this.sortedLanguages.add(localObject2);
      this.languagesDict.put(((LocaleInfo)localObject2).shortName, localObject2);
    }
    Collections.sort(this.sortedLanguages, new Comparator()
    {
      public int compare(LocaleController.LocaleInfo paramLocaleInfo1, LocaleController.LocaleInfo paramLocaleInfo2)
      {
        return paramLocaleInfo1.name.compareTo(paramLocaleInfo2.name);
      }
    });
    localObject1 = new LocaleInfo();
    this.defaultLocalInfo = ((LocaleInfo)localObject1);
    ((LocaleInfo)localObject1).name = "System default";
    ((LocaleInfo)localObject1).nameEnglish = "System default";
    ((LocaleInfo)localObject1).shortName = null;
    ((LocaleInfo)localObject1).pathToFile = null;
    this.sortedLanguages.add(0, localObject1);
    this.systemDefaultLocale = Locale.getDefault();
    is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
    while (true)
    {
      try
      {
        localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getString("language", null);
        if (localObject1 == null)
          break label1992;
        localObject2 = (LocaleInfo)this.languagesDict.get(localObject1);
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (localObject2 != null)
            continue;
          localObject1 = localObject2;
          if (this.systemDefaultLocale.getLanguage() == null)
            continue;
          localObject1 = (LocaleInfo)this.languagesDict.get(this.systemDefaultLocale.getLanguage());
          localObject2 = localObject1;
          if (localObject1 != null)
            continue;
          localObject2 = (LocaleInfo)this.languagesDict.get(getLocaleString(this.systemDefaultLocale));
          localObject1 = localObject2;
          if (localObject2 != null)
            continue;
          localObject1 = (LocaleInfo)this.languagesDict.get("en");
          applyLanguage((LocaleInfo)localObject1, bool);
        }
      }
      catch (Exception localException2)
      {
        try
        {
          localObject1 = new IntentFilter("android.intent.action.TIMEZONE_CHANGED");
          ApplicationLoader.applicationContext.registerReceiver(new TimeZoneChangedReceiver(null), (IntentFilter)localObject1);
          return;
          localException1 = localException1;
          FileLog.e(localException1);
          continue;
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
          return;
        }
      }
      bool = false;
      continue;
      label1992: bool = false;
      localObject2 = null;
    }
  }

  private void addRules(String[] paramArrayOfString, PluralRules paramPluralRules)
  {
    int j = paramArrayOfString.length;
    int i = 0;
    while (i < j)
    {
      String str = paramArrayOfString[i];
      this.allRules.put(str, paramPluralRules);
      i += 1;
    }
  }

  private FastDateFormat createFormatter(Locale paramLocale, String paramString1, String paramString2)
  {
    String str;
    if (paramString1 != null)
    {
      str = paramString1;
      if (paramString1.length() != 0);
    }
    else
    {
      str = paramString2;
    }
    try
    {
      paramString1 = FastDateFormat.getInstance(str, paramLocale);
      return paramString1;
    }
    catch (Exception paramString1)
    {
    }
    return FastDateFormat.getInstance(paramString2, paramLocale);
  }

  public static String formatCallDuration(int paramInt)
  {
    if (paramInt > 3600)
    {
      String str2 = formatPluralString("Hours", paramInt / 3600);
      paramInt = paramInt % 3600 / 60;
      String str1 = str2;
      if (paramInt > 0)
        str1 = str2 + ", " + formatPluralString("Minutes", paramInt);
      return str1;
    }
    if (paramInt > 60)
      return formatPluralString("Minutes", paramInt / 60);
    return formatPluralString("Seconds", paramInt);
  }

  public static String formatDate(long paramLong)
  {
    paramLong = 1000L * paramLong;
    try
    {
      Object localObject = Calendar.getInstance();
      int i = ((Calendar)localObject).get(6);
      int j = ((Calendar)localObject).get(1);
      ((Calendar)localObject).setTimeInMillis(paramLong);
      int k = ((Calendar)localObject).get(6);
      int m = ((Calendar)localObject).get(1);
      if ((k == i) && (j == m))
        return getInstance().formatterDay.format(new Date(paramLong));
      if ((k + 1 == i) && (j == m))
        return getString("Yesterday", 2131166640);
      if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        return getInstance().formatterMonth.format(new Date(paramLong));
      localObject = getInstance().formatterYear.format(new Date(paramLong));
      return localObject;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return (String)"LOC_ERR: formatDate";
  }

  public static String formatDateAudio(long paramLong)
  {
    int i;
    int j;
    int k;
    int m;
    if ((getInstance().currentLocale.getLanguage().toLowerCase().equals("fa")) || (b.a(ApplicationLoader.applicationContext).e()))
    {
      paramLong = 1000L * paramLong;
      try
      {
        Object localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        a locala = new a();
        locala.a((Calendar)localObject1);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k == i) && (j == m))
          return String.format("%s %s", new Object[] { getString("TodayAt", 2131166523), getInstance().formatterDay.format(new Date(paramLong)) });
        if ((k + 1 == i) && (j == m))
          return String.format("%s %s", new Object[] { getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
        if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
          return formatString("formatDateAtTime", 2131166662, new Object[] { locala.a(ApplicationLoader.applicationContext), getInstance().formatterDay.format(new Date(paramLong)) });
        localObject1 = formatString("formatDateAtTime", 2131166662, new Object[] { locala.c(ApplicationLoader.applicationContext), getInstance().formatterDay.format(new Date(paramLong)) });
        return localObject1;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        return "LOC_ERR";
      }
    }
    paramLong = 1000L * paramLong;
    try
    {
      Object localObject2 = Calendar.getInstance();
      i = ((Calendar)localObject2).get(6);
      j = ((Calendar)localObject2).get(1);
      ((Calendar)localObject2).setTimeInMillis(paramLong);
      k = ((Calendar)localObject2).get(6);
      m = ((Calendar)localObject2).get(1);
      if ((k == i) && (j == m))
        return String.format("%s %s", new Object[] { getString("TodayAt", 2131166523), getInstance().formatterDay.format(new Date(paramLong)) });
      if ((k + 1 == i) && (j == m))
        return String.format("%s %s", new Object[] { getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
      if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        return formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().formatterMonth.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
      localObject2 = formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().formatterYear.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
      return localObject2;
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
    }
    return (String)(String)"LOC_ERR";
  }

  public static String formatDateCallLog(long paramLong)
  {
    int i;
    int j;
    int k;
    int m;
    if ((getInstance().currentLocale.getLanguage().toLowerCase().equals("fa")) || (b.a(ApplicationLoader.applicationContext).e()))
    {
      paramLong = 1000L * paramLong;
      try
      {
        Object localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        a locala = new a();
        locala.a((Calendar)localObject1);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k == i) && (j == m))
          return getInstance().formatterDay.format(new Date(paramLong));
        if ((k + 1 == i) && (j == m))
          return String.format("%s %s", new Object[] { getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
        if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
          return formatString("formatDateAtTime", 2131166662, new Object[] { locala.a(ApplicationLoader.applicationContext), getInstance().formatterDay.format(new Date(paramLong)) });
        localObject1 = formatString("formatDateAtTime", 2131166662, new Object[] { locala.b(ApplicationLoader.applicationContext.getApplicationContext()), getInstance().formatterDay.format(new Date(paramLong)) });
        return localObject1;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        return "LOC_ERR";
      }
    }
    paramLong = 1000L * paramLong;
    try
    {
      Object localObject2 = Calendar.getInstance();
      i = ((Calendar)localObject2).get(6);
      j = ((Calendar)localObject2).get(1);
      ((Calendar)localObject2).setTimeInMillis(paramLong);
      k = ((Calendar)localObject2).get(6);
      m = ((Calendar)localObject2).get(1);
      if ((k == i) && (j == m))
        return getInstance().formatterDay.format(new Date(paramLong));
      if ((k + 1 == i) && (j == m))
        return String.format("%s %s", new Object[] { getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
      if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        return formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().chatDate.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
      localObject2 = formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().chatFullDate.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
      return localObject2;
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
    }
    return (String)(String)"LOC_ERR";
  }

  public static String formatDateChat(long paramLong)
  {
    if ((getInstance().currentLocale.getLanguage().toLowerCase().equals("fa")) || (b.a(ApplicationLoader.applicationContext).e()))
    {
      paramLong *= 1000L;
      try
      {
        Object localObject = Calendar.getInstance();
        ((Calendar)localObject).setTimeInMillis(paramLong);
        a locala = new a();
        locala.a((Calendar)localObject);
        if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
          return locala.a(ApplicationLoader.applicationContext.getApplicationContext());
        localObject = locala.b(ApplicationLoader.applicationContext.getApplicationContext());
        return localObject;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        return "LOC_ERR: formatDateChat";
      }
    }
    paramLong *= 1000L;
    try
    {
      Calendar.getInstance().setTimeInMillis(paramLong);
      if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        return getInstance().chatDate.format(paramLong);
      String str = getInstance().chatFullDate.format(paramLong);
      return str;
    }
    catch (Exception localException2)
    {
      FileLog.e("tmessages", localException2);
    }
    return (String)"LOC_ERR: formatDateChat";
  }

  public static String formatDateOnline(long paramLong)
  {
    int i;
    int j;
    int k;
    int m;
    if ((getInstance().currentLocale.getLanguage().toLowerCase().equals("fa")) || (b.a(ApplicationLoader.applicationContext).e()))
    {
      paramLong = 1000L * paramLong;
      try
      {
        Object localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        j = ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        a locala = new a();
        locala.a((Calendar)localObject1);
        k = ((Calendar)localObject1).get(6);
        m = ((Calendar)localObject1).get(1);
        if ((k == i) && (j == m))
          return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165875), getString("TodayAt", 2131166523), getInstance().formatterDay.format(new Date(paramLong)) });
        if ((k + 1 == i) && (j == m))
          return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165875), getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
        if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
        {
          localObject1 = formatString("formatDateAtTime", 2131166662, new Object[] { locala.a(ApplicationLoader.applicationContext), getInstance().formatterDay.format(new Date(paramLong)) });
          return String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165880), localObject1 });
        }
        localObject1 = formatString("formatDateAtTime", 2131166662, new Object[] { locala.c(ApplicationLoader.applicationContext), getInstance().formatterDay.format(new Date(paramLong)) });
        localObject1 = String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165880), localObject1 });
        return localObject1;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        return "LOC_ERR";
      }
    }
    paramLong = 1000L * paramLong;
    try
    {
      Object localObject2 = Calendar.getInstance();
      i = ((Calendar)localObject2).get(6);
      j = ((Calendar)localObject2).get(1);
      ((Calendar)localObject2).setTimeInMillis(paramLong);
      k = ((Calendar)localObject2).get(6);
      m = ((Calendar)localObject2).get(1);
      if ((k == i) && (j == m))
        return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165875), getString("TodayAt", 2131166523), getInstance().formatterDay.format(new Date(paramLong)) });
      if ((k + 1 == i) && (j == m))
        return String.format("%s %s %s", new Object[] { getString("LastSeen", 2131165875), getString("YesterdayAt", 2131166641), getInstance().formatterDay.format(new Date(paramLong)) });
      if (Math.abs(System.currentTimeMillis() - paramLong) < 31536000000L)
      {
        localObject2 = formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().formatterMonth.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
        return String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165880), localObject2 });
      }
      localObject2 = formatString("formatDateAtTime", 2131166662, new Object[] { getInstance().formatterYear.format(new Date(paramLong)), getInstance().formatterDay.format(new Date(paramLong)) });
      localObject2 = String.format("%s %s", new Object[] { getString("LastSeenDate", 2131165880), localObject2 });
      return localObject2;
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
    }
    return (String)(String)"LOC_ERR";
  }

  public static String formatPluralString(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.length() == 0) || (getInstance().currentPluralRules == null))
      return "LOC_ERR:" + paramString;
    String str = getInstance().stringForQuantity(getInstance().currentPluralRules.quantityForNumber(paramInt));
    paramString = paramString + "_" + str;
    return formatString(paramString, ApplicationLoader.applicationContext.getResources().getIdentifier(paramString, "string", ApplicationLoader.applicationContext.getPackageName()), new Object[] { Integer.valueOf(paramInt) });
  }

  public static String formatShortNumber(int paramInt, int[] paramArrayOfInt)
  {
    String str = "";
    int i = 0;
    while (paramInt / 1000 > 0)
    {
      str = str + "K";
      i = paramInt % 1000 / 100;
      paramInt /= 1000;
    }
    if (paramArrayOfInt != null)
    {
      double d = paramInt + i / 10.0D;
      int j = 0;
      while (j < str.length())
      {
        d *= 1000.0D;
        j += 1;
      }
      paramArrayOfInt[0] = (int)d;
    }
    if ((i != 0) && (str.length() > 0))
    {
      if (str.length() == 2)
        return String.format(Locale.US, "%d.%dM", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i) });
      return String.format(Locale.US, "%d.%d%s", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), str });
    }
    if (str.length() == 2)
      return String.format(Locale.US, "%dM", new Object[] { Integer.valueOf(paramInt) });
    return String.format(Locale.US, "%d%s", new Object[] { Integer.valueOf(paramInt), str });
  }

  public static String formatString(String paramString, int paramInt, Object[] paramArrayOfObject)
  {
    try
    {
      String str2 = (String)getInstance().localeValues.get(paramString);
      String str1 = str2;
      if (str2 == null)
        str1 = ApplicationLoader.applicationContext.getString(paramInt);
      if (getInstance().currentLocale != null)
        return String.format(getInstance().currentLocale, str1, paramArrayOfObject);
      paramArrayOfObject = String.format(str1, paramArrayOfObject);
      return paramArrayOfObject;
    }
    catch (Exception paramArrayOfObject)
    {
      FileLog.e(paramArrayOfObject);
    }
    return "LOC_ERR: " + paramString;
  }

  public static String formatStringSimple(String paramString, Object[] paramArrayOfObject)
  {
    try
    {
      if (getInstance().currentLocale != null)
        return String.format(getInstance().currentLocale, paramString, paramArrayOfObject);
      paramArrayOfObject = String.format(paramString, paramArrayOfObject);
      return paramArrayOfObject;
    }
    catch (Exception paramArrayOfObject)
    {
      FileLog.e(paramArrayOfObject);
    }
    return "LOC_ERR: " + paramString;
  }

  public static String formatTTLString(int paramInt)
  {
    if (paramInt < 60)
      return formatPluralString("Seconds", paramInt);
    if (paramInt < 3600)
      return formatPluralString("Minutes", paramInt / 60);
    if (paramInt < 86400)
      return formatPluralString("Hours", paramInt / 60 / 60);
    if (paramInt < 604800)
      return formatPluralString("Days", paramInt / 60 / 60 / 24);
    int i = paramInt / 60 / 60 / 24;
    if (paramInt % 7 == 0)
      return formatPluralString("Weeks", i / 7);
    return String.format("%s %s", new Object[] { formatPluralString("Weeks", i / 7), formatPluralString("Days", i % 7) });
  }

  public static String formatUserStatus(TLRPC.User paramUser)
  {
    if ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires == 0))
    {
      if (!(paramUser.status instanceof TLRPC.TL_userStatusRecently))
        break label90;
      paramUser.status.expires = -100;
    }
    while ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires <= 0) && (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(paramUser.id))))
    {
      return getString("Online", 2131166155);
      label90: if ((paramUser.status instanceof TLRPC.TL_userStatusLastWeek))
      {
        paramUser.status.expires = -101;
        continue;
      }
      if (!(paramUser.status instanceof TLRPC.TL_userStatusLastMonth))
        continue;
      paramUser.status.expires = -102;
    }
    if ((paramUser == null) || (paramUser.status == null) || (paramUser.status.expires == 0) || (UserObject.isDeleted(paramUser)) || ((paramUser instanceof TLRPC.TL_userEmpty)))
      return getString("ALongTimeAgo", 2131165222);
    int i = ConnectionsManager.getInstance().getCurrentTime();
    if (paramUser.status.expires > i)
      return getString("Online", 2131166155);
    if (paramUser.status.expires == -1)
      return getString("Invisible", 2131165842);
    if (paramUser.status.expires == -100)
      return getString("Lately", 2131165900);
    if (paramUser.status.expires == -101)
      return getString("WithinAWeek", 2131166630);
    if (paramUser.status.expires == -102)
      return getString("WithinAMonth", 2131166629);
    return formatDateOnline(paramUser.status.expires);
  }

  public static String getCurrentLanguageName()
  {
    return getString("LanguageName", 2131165872);
  }

  public static LocaleController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        LocaleController localLocaleController = Instance;
        localObject1 = localLocaleController;
        if (localLocaleController == null)
        {
          localObject1 = new LocaleController();
          Instance = (LocaleController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (LocaleController)localObject2;
  }

  // ERROR //
  private HashMap<String, String> getLocaleFileStrings(File paramFile)
  {
    // Byte code:
    //   0: new 147	java/util/HashMap
    //   3: dup
    //   4: invokespecial 148	java/util/HashMap:<init>	()V
    //   7: astore 9
    //   9: invokestatic 926	android/util/Xml:newPullParser	()Lorg/xmlpull/v1/XmlPullParser;
    //   12: astore 10
    //   14: new 928	java/io/FileInputStream
    //   17: dup
    //   18: aload_1
    //   19: invokespecial 931	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   22: astore 8
    //   24: aload 10
    //   26: aload 8
    //   28: ldc_w 933
    //   31: invokeinterface 939 3 0
    //   36: aload 10
    //   38: invokeinterface 942 1 0
    //   43: istore_2
    //   44: aconst_null
    //   45: astore 7
    //   47: aconst_null
    //   48: astore 6
    //   50: aconst_null
    //   51: astore 5
    //   53: iload_2
    //   54: iconst_1
    //   55: if_icmpeq +282 -> 337
    //   58: iload_2
    //   59: iconst_2
    //   60: if_icmpne +174 -> 234
    //   63: aload 10
    //   65: invokeinterface 945 1 0
    //   70: astore 6
    //   72: aload 5
    //   74: astore_3
    //   75: aload 7
    //   77: astore_1
    //   78: aload 6
    //   80: astore 4
    //   82: aload 10
    //   84: invokeinterface 948 1 0
    //   89: ifle +19 -> 108
    //   92: aload 10
    //   94: iconst_0
    //   95: invokeinterface 951 2 0
    //   100: astore_3
    //   101: aload 6
    //   103: astore 4
    //   105: aload 7
    //   107: astore_1
    //   108: aload_3
    //   109: astore 5
    //   111: aload_1
    //   112: astore 7
    //   114: aload 4
    //   116: astore 6
    //   118: aload 4
    //   120: ifnull +103 -> 223
    //   123: aload_3
    //   124: astore 5
    //   126: aload_1
    //   127: astore 7
    //   129: aload 4
    //   131: astore 6
    //   133: aload 4
    //   135: ldc_w 791
    //   138: invokevirtual 702	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   141: ifeq +82 -> 223
    //   144: aload_3
    //   145: astore 5
    //   147: aload_1
    //   148: astore 7
    //   150: aload 4
    //   152: astore 6
    //   154: aload_1
    //   155: ifnull +68 -> 223
    //   158: aload_3
    //   159: astore 5
    //   161: aload_1
    //   162: astore 7
    //   164: aload 4
    //   166: astore 6
    //   168: aload_3
    //   169: ifnull +54 -> 223
    //   172: aload_3
    //   173: astore 5
    //   175: aload_1
    //   176: astore 7
    //   178: aload 4
    //   180: astore 6
    //   182: aload_1
    //   183: invokevirtual 609	java/lang/String:length	()I
    //   186: ifeq +37 -> 223
    //   189: aload_3
    //   190: astore 5
    //   192: aload_1
    //   193: astore 7
    //   195: aload 4
    //   197: astore 6
    //   199: aload_3
    //   200: invokevirtual 609	java/lang/String:length	()I
    //   203: ifeq +20 -> 223
    //   206: aload 9
    //   208: aload_3
    //   209: aload_1
    //   210: invokevirtual 465	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   213: pop
    //   214: aconst_null
    //   215: astore 5
    //   217: aconst_null
    //   218: astore 7
    //   220: aconst_null
    //   221: astore 6
    //   223: aload 10
    //   225: invokeinterface 953 1 0
    //   230: istore_2
    //   231: goto -178 -> 53
    //   234: iload_2
    //   235: iconst_4
    //   236: if_icmpne +76 -> 312
    //   239: aload 5
    //   241: astore_3
    //   242: aload 7
    //   244: astore_1
    //   245: aload 6
    //   247: astore 4
    //   249: aload 5
    //   251: ifnull -143 -> 108
    //   254: aload 10
    //   256: invokeinterface 956 1 0
    //   261: astore 7
    //   263: aload 5
    //   265: astore_3
    //   266: aload 7
    //   268: astore_1
    //   269: aload 6
    //   271: astore 4
    //   273: aload 7
    //   275: ifnull -167 -> 108
    //   278: aload 7
    //   280: invokevirtual 959	java/lang/String:trim	()Ljava/lang/String;
    //   283: ldc_w 961
    //   286: ldc_w 963
    //   289: invokevirtual 967	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   292: ldc_w 969
    //   295: ldc_w 810
    //   298: invokevirtual 967	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   301: astore_1
    //   302: aload 5
    //   304: astore_3
    //   305: aload 6
    //   307: astore 4
    //   309: goto -201 -> 108
    //   312: aload 5
    //   314: astore_3
    //   315: aload 7
    //   317: astore_1
    //   318: aload 6
    //   320: astore 4
    //   322: iload_2
    //   323: iconst_3
    //   324: if_icmpne -216 -> 108
    //   327: aconst_null
    //   328: astore_3
    //   329: aconst_null
    //   330: astore_1
    //   331: aconst_null
    //   332: astore 4
    //   334: goto -226 -> 108
    //   337: aload 8
    //   339: ifnull +8 -> 347
    //   342: aload 8
    //   344: invokevirtual 972	java/io/FileInputStream:close	()V
    //   347: aload 9
    //   349: areturn
    //   350: astore_1
    //   351: aload_1
    //   352: invokestatic 603	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   355: goto -8 -> 347
    //   358: astore_3
    //   359: aconst_null
    //   360: astore_1
    //   361: aload_3
    //   362: invokestatic 603	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   365: aload_1
    //   366: ifnull +7 -> 373
    //   369: aload_1
    //   370: invokevirtual 972	java/io/FileInputStream:close	()V
    //   373: new 147	java/util/HashMap
    //   376: dup
    //   377: invokespecial 148	java/util/HashMap:<init>	()V
    //   380: areturn
    //   381: astore_1
    //   382: aload_1
    //   383: invokestatic 603	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   386: goto -13 -> 373
    //   389: astore_1
    //   390: aconst_null
    //   391: astore_3
    //   392: aload_3
    //   393: ifnull +7 -> 400
    //   396: aload_3
    //   397: invokevirtual 972	java/io/FileInputStream:close	()V
    //   400: aload_1
    //   401: athrow
    //   402: astore_3
    //   403: aload_3
    //   404: invokestatic 603	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   407: goto -7 -> 400
    //   410: astore_1
    //   411: aload 8
    //   413: astore_3
    //   414: goto -22 -> 392
    //   417: astore 4
    //   419: aload_1
    //   420: astore_3
    //   421: aload 4
    //   423: astore_1
    //   424: goto -32 -> 392
    //   427: astore_3
    //   428: aload 8
    //   430: astore_1
    //   431: goto -70 -> 361
    //
    // Exception table:
    //   from	to	target	type
    //   342	347	350	java/lang/Exception
    //   0	24	358	java/lang/Exception
    //   369	373	381	java/lang/Exception
    //   0	24	389	finally
    //   396	400	402	java/lang/Exception
    //   24	44	410	finally
    //   63	72	410	finally
    //   82	101	410	finally
    //   133	144	410	finally
    //   182	189	410	finally
    //   199	214	410	finally
    //   223	231	410	finally
    //   254	263	410	finally
    //   278	302	410	finally
    //   361	365	417	finally
    //   24	44	427	java/lang/Exception
    //   63	72	427	java/lang/Exception
    //   82	101	427	java/lang/Exception
    //   133	144	427	java/lang/Exception
    //   182	189	427	java/lang/Exception
    //   199	214	427	java/lang/Exception
    //   223	231	427	java/lang/Exception
    //   254	263	427	java/lang/Exception
    //   278	302	427	java/lang/Exception
  }

  private String getLocaleString(Locale paramLocale)
  {
    if (paramLocale == null)
      return "en";
    String str1 = paramLocale.getLanguage();
    String str2 = paramLocale.getCountry();
    paramLocale = paramLocale.getVariant();
    if ((str1.length() == 0) && (str2.length() == 0))
      return "en";
    StringBuilder localStringBuilder = new StringBuilder(11);
    localStringBuilder.append(str1);
    if ((str2.length() > 0) || (paramLocale.length() > 0))
      localStringBuilder.append('_');
    localStringBuilder.append(str2);
    if (paramLocale.length() > 0)
      localStringBuilder.append('_');
    localStringBuilder.append(paramLocale);
    return localStringBuilder.toString();
  }

  public static String getLocaleStringIso639()
  {
    Object localObject = getInstance().getSystemDefaultLocale();
    if (localObject == null)
      return "en";
    String str1 = ((Locale)localObject).getLanguage();
    String str2 = ((Locale)localObject).getCountry();
    localObject = ((Locale)localObject).getVariant();
    if ((str1.length() == 0) && (str2.length() == 0))
      return "en";
    StringBuilder localStringBuilder = new StringBuilder(11);
    localStringBuilder.append(str1);
    if ((str2.length() > 0) || (((String)localObject).length() > 0))
      localStringBuilder.append('-');
    localStringBuilder.append(str2);
    if (((String)localObject).length() > 0)
      localStringBuilder.append('_');
    localStringBuilder.append((String)localObject);
    return (String)localStringBuilder.toString();
  }

  public static String getString(String paramString, int paramInt)
  {
    return getInstance().getStringInternal(paramString, paramInt);
  }

  private String getStringInternal(String paramString, int paramInt)
  {
    Object localObject3 = (String)this.localeValues.get(paramString);
    Object localObject1 = localObject3;
    if (localObject3 == null);
    try
    {
      localObject1 = ApplicationLoader.applicationContext.getString(paramInt);
      localObject3 = localObject1;
      if (localObject1 == null)
        localObject3 = "LOC_ERR:" + paramString;
      return localObject3;
    }
    catch (Exception localObject2)
    {
      while (true)
      {
        FileLog.e(localException);
        Object localObject2 = localObject3;
      }
    }
  }

  public static boolean isRTLCharacter(char paramChar)
  {
    return (Character.getDirectionality(paramChar) == 1) || (Character.getDirectionality(paramChar) == 2) || (Character.getDirectionality(paramChar) == 16) || (Character.getDirectionality(paramChar) == 17);
  }

  private void loadOtherLanguages()
  {
    int i = 0;
    Object localObject = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).getString("locales", null);
    if ((localObject == null) || (((String)localObject).length() == 0));
    while (true)
    {
      return;
      localObject = ((String)localObject).split("&");
      int j = localObject.length;
      while (i < j)
      {
        LocaleInfo localLocaleInfo = LocaleInfo.createWithString(localObject[i]);
        if (localLocaleInfo != null)
          this.otherLanguages.add(localLocaleInfo);
        i += 1;
      }
    }
  }

  private void saveOtherLanguages()
  {
    SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
    String str1 = "";
    Iterator localIterator = this.otherLanguages.iterator();
    String str2;
    while (localIterator.hasNext())
    {
      str2 = ((LocaleInfo)localIterator.next()).getSaveString();
      if (str2 == null)
        continue;
      if (str1.length() == 0)
        break label126;
      str1 = str1 + "&";
    }
    label126: 
    while (true)
    {
      str1 = str1 + str2;
      break;
      localEditor.putString("locales", str1);
      localEditor.commit();
      return;
    }
  }

  public static String stringForMessageListDate(long paramLong)
  {
    int i;
    int j;
    if ((getInstance().currentLocale.getLanguage().toLowerCase().equals("fa")) || (b.a(ApplicationLoader.applicationContext).e()))
    {
      paramLong *= 1000L;
      try
      {
        Object localObject1 = Calendar.getInstance();
        i = ((Calendar)localObject1).get(6);
        ((Calendar)localObject1).get(1);
        ((Calendar)localObject1).setTimeInMillis(paramLong);
        a locala = new a();
        locala.a((Calendar)localObject1);
        j = ((Calendar)localObject1).get(6);
        ((Calendar)localObject1).get(1);
        if (Math.abs(System.currentTimeMillis() - paramLong) >= 31536000000L)
          return locala.c(ApplicationLoader.applicationContext);
        i = j - i;
        if ((i == 0) || ((i == -1) && ((int)System.currentTimeMillis() - paramLong < 28800L)))
          return getInstance().formatterDay.format(new Date(paramLong));
        if ((i > -7) && (i <= -1))
          return locala.d(ApplicationLoader.applicationContext);
        localObject1 = locala.a(ApplicationLoader.applicationContext);
        return localObject1;
      }
      catch (Exception localException1)
      {
        FileLog.e("tmessages", localException1);
        return "LOC_ERR";
      }
    }
    paramLong *= 1000L;
    try
    {
      Object localObject2 = Calendar.getInstance();
      i = ((Calendar)localObject2).get(6);
      ((Calendar)localObject2).get(1);
      ((Calendar)localObject2).setTimeInMillis(paramLong);
      j = ((Calendar)localObject2).get(6);
      ((Calendar)localObject2).get(1);
      if (Math.abs(System.currentTimeMillis() - paramLong) >= 31536000000L)
        return getInstance().formatterYear.format(new Date(paramLong));
      i = j - i;
      if ((i == 0) || ((i == -1) && (System.currentTimeMillis() - paramLong < 28800000L)))
        return getInstance().formatterDay.format(new Date(paramLong));
      if ((i > -7) && (i <= -1))
        return getInstance().formatterWeek.format(new Date(paramLong));
      localObject2 = getInstance().formatterMonth.format(new Date(paramLong));
      return localObject2;
    }
    catch (Exception localException2)
    {
      FileLog.e(localException2);
    }
    return (String)(String)"LOC_ERR";
  }

  private String stringForQuantity(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "other";
    case 1:
      return "zero";
    case 2:
      return "one";
    case 4:
      return "two";
    case 8:
      return "few";
    case 16:
    }
    return "many";
  }

  public static Drawable userStatusDrawable(TLRPC.User paramUser, Context paramContext)
  {
    if ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires == 0))
    {
      if (!(paramUser.status instanceof TLRPC.TL_userStatusRecently))
        break label91;
      paramUser.status.expires = -100;
    }
    while ((paramUser != null) && (paramUser.status != null) && (paramUser.status.expires <= 0) && (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(paramUser.id))))
    {
      return paramContext.getResources().getDrawable(2130837984);
      label91: if ((paramUser.status instanceof TLRPC.TL_userStatusLastWeek))
      {
        paramUser.status.expires = -101;
        continue;
      }
      if (!(paramUser.status instanceof TLRPC.TL_userStatusLastMonth))
        continue;
      paramUser.status.expires = -102;
    }
    if ((paramUser == null) || (paramUser.status == null) || (paramUser.status.expires == 0) || (UserObject.isDeleted(paramUser)) || ((paramUser instanceof TLRPC.TL_userEmpty)))
      return paramContext.getResources().getDrawable(2130837628);
    int i = ConnectionsManager.getInstance().getCurrentTime();
    if (paramUser.status.expires > i)
      return paramContext.getResources().getDrawable(2130837984);
    if (paramUser.status.expires == -1)
      return paramContext.getResources().getDrawable(2130837881);
    if (paramUser.status.expires == -100)
      return paramContext.getResources().getDrawable(2130837883);
    if (paramUser.status.expires == -101)
      return paramContext.getResources().getDrawable(2130838126);
    if (paramUser.status.expires == -102)
      return paramContext.getResources().getDrawable(2130838125);
    return paramContext.getResources().getDrawable(2130837983);
  }

  public void applyLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean)
  {
    applyLanguage(paramLocaleInfo, paramBoolean, false);
  }

  public void applyLanguage(LocaleInfo paramLocaleInfo, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject1 = null;
    if (paramLocaleInfo == null)
      return;
    try
    {
      if (paramLocaleInfo.shortName != null)
      {
        localObject1 = paramLocaleInfo.shortName.split("_");
        if (localObject1.length == 1);
        for (localObject1 = new Locale(paramLocaleInfo.shortName); ; localObject1 = new Locale(localObject1[0], localObject1[1]))
        {
          localObject2 = localObject1;
          if (localObject1 != null)
          {
            localObject2 = localObject1;
            if (paramBoolean1)
            {
              this.languageOverride = paramLocaleInfo.shortName;
              localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
              ((SharedPreferences.Editor)localObject2).putString("language", paramLocaleInfo.shortName);
              ((SharedPreferences.Editor)localObject2).commit();
              localObject2 = localObject1;
            }
          }
          if (localObject2 != null)
          {
            if (paramLocaleInfo.pathToFile != null)
              break;
            this.localeValues.clear();
            this.currentLocale = ((Locale)localObject2);
            this.currentLocaleInfo = paramLocaleInfo;
            this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
            if (this.currentPluralRules == null)
              this.currentPluralRules = ((PluralRules)this.allRules.get("en"));
            this.changingConfiguration = true;
            Locale.setDefault(this.currentLocale);
            paramLocaleInfo = new Configuration();
            paramLocaleInfo.locale = this.currentLocale;
            ApplicationLoader.applicationContext.getResources().updateConfiguration(paramLocaleInfo, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
            this.changingConfiguration = false;
          }
          recreateFormatters();
          return;
        }
      }
    }
    catch (Exception paramLocaleInfo)
    {
      while (true)
      {
        FileLog.e(paramLocaleInfo);
        this.changingConfiguration = false;
        continue;
        Locale localLocale = this.systemDefaultLocale;
        this.languageOverride = null;
        Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
        ((SharedPreferences.Editor)localObject2).remove("language");
        ((SharedPreferences.Editor)localObject2).commit();
        if (localLocale != null)
        {
          if (localLocale.getLanguage() != null)
            localObject1 = (LocaleInfo)this.languagesDict.get(localLocale.getLanguage());
          localObject2 = localObject1;
          if (localObject1 == null)
            localObject2 = (LocaleInfo)this.languagesDict.get(getLocaleString(localLocale));
          if (localObject2 == null)
          {
            localObject2 = Locale.US;
            continue;
            if (paramBoolean2)
              continue;
            this.localeValues = getLocaleFileStrings(new File(paramLocaleInfo.pathToFile));
            continue;
          }
        }
        localObject2 = localLocale;
      }
    }
  }

  public boolean applyLanguageFile(File paramFile)
  {
    try
    {
      HashMap localHashMap = getLocaleFileStrings(paramFile);
      String str1 = (String)localHashMap.get("LanguageName");
      String str2 = (String)localHashMap.get("LanguageNameInEnglish");
      String str3 = (String)localHashMap.get("LanguageCode");
      if ((str1 != null) && (str1.length() > 0) && (str2 != null) && (str2.length() > 0) && (str3 != null) && (str3.length() > 0))
      {
        if ((str1.contains("&")) || (str1.contains("|")))
          break label347;
        if ((str2.contains("&")) || (str2.contains("|")))
          break label349;
        if ((str3.contains("&")) || (str3.contains("|")) || (str3.contains("/")) || (str3.contains("\\")))
          break label351;
        File localFile = new File(ApplicationLoader.getFilesDirFixed(), str3 + ".xml");
        if (!AndroidUtilities.copyFile(paramFile, localFile))
          return false;
        LocaleInfo localLocaleInfo = (LocaleInfo)this.languagesDict.get(str3);
        paramFile = localLocaleInfo;
        if (localLocaleInfo == null)
        {
          paramFile = new LocaleInfo();
          paramFile.name = str1;
          paramFile.nameEnglish = str2;
          paramFile.shortName = str3;
          paramFile.pathToFile = localFile.getAbsolutePath();
          this.sortedLanguages.add(paramFile);
          this.languagesDict.put(paramFile.shortName, paramFile);
          this.otherLanguages.add(paramFile);
          Collections.sort(this.sortedLanguages, new Comparator()
          {
            public int compare(LocaleController.LocaleInfo paramLocaleInfo1, LocaleController.LocaleInfo paramLocaleInfo2)
            {
              if (paramLocaleInfo1.shortName == null)
                return -1;
              if (paramLocaleInfo2.shortName == null)
                return 1;
              return paramLocaleInfo1.name.compareTo(paramLocaleInfo2.name);
            }
          });
          saveOtherLanguages();
        }
        this.localeValues = localHashMap;
        applyLanguage(paramFile, true, true);
        return true;
      }
    }
    catch (Exception paramFile)
    {
      FileLog.e(paramFile);
    }
    return false;
    label347: return false;
    label349: return false;
    label351: return false;
  }

  public boolean deleteLanguage(LocaleInfo paramLocaleInfo)
  {
    if (paramLocaleInfo.pathToFile == null)
      return false;
    if (this.currentLocaleInfo == paramLocaleInfo)
      applyLanguage(this.defaultLocalInfo, true);
    this.otherLanguages.remove(paramLocaleInfo);
    this.sortedLanguages.remove(paramLocaleInfo);
    this.languagesDict.remove(paramLocaleInfo.shortName);
    new File(paramLocaleInfo.pathToFile).delete();
    saveOtherLanguages();
    return true;
  }

  public String formatCurrencyDecimalString(long paramLong, String paramString)
  {
    String str = paramString.toUpperCase();
    paramLong = Math.abs(paramLong);
    int i = -1;
    double d;
    switch (str.hashCode())
    {
    default:
      switch (i)
      {
      default:
        paramString = " %.2f";
        d = paramLong / 100.0D;
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
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      }
    case 66813:
    case 65726:
    case 72732:
    case 73631:
    case 74840:
    case 75863:
    case 78388:
    case 83210:
    case 65759:
    case 66267:
    case 66823:
    case 67122:
    case 67712:
    case 70719:
    case 72801:
    case 73683:
    case 74532:
    case 74704:
    case 76263:
    case 79710:
    case 81569:
    case 83974:
    case 84517:
    case 85132:
    case 85367:
    case 86653:
    case 87087:
    case 87118:
    case 76618:
    }
    while (true)
    {
      return String.format(str + paramString, new Object[] { Double.valueOf(d) });
      if (!str.equals("CLF"))
        break;
      i = 0;
      break;
      if (!str.equals("BHD"))
        break;
      i = 1;
      break;
      if (!str.equals("IQD"))
        break;
      i = 2;
      break;
      if (!str.equals("JOD"))
        break;
      i = 3;
      break;
      if (!str.equals("KWD"))
        break;
      i = 4;
      break;
      if (!str.equals("LYD"))
        break;
      i = 5;
      break;
      if (!str.equals("OMR"))
        break;
      i = 6;
      break;
      if (!str.equals("TND"))
        break;
      i = 7;
      break;
      if (!str.equals("BIF"))
        break;
      i = 8;
      break;
      if (!str.equals("BYR"))
        break;
      i = 9;
      break;
      if (!str.equals("CLP"))
        break;
      i = 10;
      break;
      if (!str.equals("CVE"))
        break;
      i = 11;
      break;
      if (!str.equals("DJF"))
        break;
      i = 12;
      break;
      if (!str.equals("GNF"))
        break;
      i = 13;
      break;
      if (!str.equals("ISK"))
        break;
      i = 14;
      break;
      if (!str.equals("JPY"))
        break;
      i = 15;
      break;
      if (!str.equals("KMF"))
        break;
      i = 16;
      break;
      if (!str.equals("KRW"))
        break;
      i = 17;
      break;
      if (!str.equals("MGA"))
        break;
      i = 18;
      break;
      if (!str.equals("PYG"))
        break;
      i = 19;
      break;
      if (!str.equals("RWF"))
        break;
      i = 20;
      break;
      if (!str.equals("UGX"))
        break;
      i = 21;
      break;
      if (!str.equals("UYI"))
        break;
      i = 22;
      break;
      if (!str.equals("VND"))
        break;
      i = 23;
      break;
      if (!str.equals("VUV"))
        break;
      i = 24;
      break;
      if (!str.equals("XAF"))
        break;
      i = 25;
      break;
      if (!str.equals("XOF"))
        break;
      i = 26;
      break;
      if (!str.equals("XPF"))
        break;
      i = 27;
      break;
      if (!str.equals("MRO"))
        break;
      i = 28;
      break;
      paramString = " %.4f";
      d = paramLong / 10000.0D;
      continue;
      paramString = " %.3f";
      d = paramLong / 1000.0D;
      continue;
      paramString = " %.0f";
      d = paramLong;
      continue;
      paramString = " %.1f";
      d = paramLong / 10.0D;
    }
  }

  public String formatCurrencyString(long paramLong, String paramString)
  {
    Object localObject2 = paramString.toUpperCase();
    int j;
    int i;
    label272: double d;
    if (paramLong < 0L)
    {
      j = 1;
      paramLong = Math.abs(paramLong);
      i = -1;
      switch (((String)localObject2).hashCode())
      {
      default:
        switch (i)
        {
        default:
          paramString = " %.2f";
          d = paramLong / 100.0D;
          label416: localObject1 = Currency.getInstance((String)localObject2);
          if (localObject1 == null)
            break label1084;
          if (this.currentLocale != null)
          {
            paramString = this.currentLocale;
            label440: localObject2 = NumberFormat.getCurrencyInstance(paramString);
            ((NumberFormat)localObject2).setCurrency((Currency)localObject1);
            localObject1 = new StringBuilder();
            if (j == 0)
              break label1077;
          }
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
        case 12:
        case 13:
        case 14:
        case 15:
        case 16:
        case 17:
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        }
      case 66813:
      case 65726:
      case 72732:
      case 73631:
      case 74840:
      case 75863:
      case 78388:
      case 83210:
      case 65759:
      case 66267:
      case 66823:
      case 67122:
      case 67712:
      case 70719:
      case 72801:
      case 73683:
      case 74532:
      case 74704:
      case 76263:
      case 79710:
      case 81569:
      case 83974:
      case 84517:
      case 85132:
      case 85367:
      case 86653:
      case 87087:
      case 87118:
      case 76618:
      }
    }
    label1077: for (paramString = "-"; ; paramString = "")
    {
      return paramString + ((NumberFormat)localObject2).format(d);
      j = 0;
      break;
      if (!((String)localObject2).equals("CLF"))
        break label272;
      i = 0;
      break label272;
      if (!((String)localObject2).equals("BHD"))
        break label272;
      i = 1;
      break label272;
      if (!((String)localObject2).equals("IQD"))
        break label272;
      i = 2;
      break label272;
      if (!((String)localObject2).equals("JOD"))
        break label272;
      i = 3;
      break label272;
      if (!((String)localObject2).equals("KWD"))
        break label272;
      i = 4;
      break label272;
      if (!((String)localObject2).equals("LYD"))
        break label272;
      i = 5;
      break label272;
      if (!((String)localObject2).equals("OMR"))
        break label272;
      i = 6;
      break label272;
      if (!((String)localObject2).equals("TND"))
        break label272;
      i = 7;
      break label272;
      if (!((String)localObject2).equals("BIF"))
        break label272;
      i = 8;
      break label272;
      if (!((String)localObject2).equals("BYR"))
        break label272;
      i = 9;
      break label272;
      if (!((String)localObject2).equals("CLP"))
        break label272;
      i = 10;
      break label272;
      if (!((String)localObject2).equals("CVE"))
        break label272;
      i = 11;
      break label272;
      if (!((String)localObject2).equals("DJF"))
        break label272;
      i = 12;
      break label272;
      if (!((String)localObject2).equals("GNF"))
        break label272;
      i = 13;
      break label272;
      if (!((String)localObject2).equals("ISK"))
        break label272;
      i = 14;
      break label272;
      if (!((String)localObject2).equals("JPY"))
        break label272;
      i = 15;
      break label272;
      if (!((String)localObject2).equals("KMF"))
        break label272;
      i = 16;
      break label272;
      if (!((String)localObject2).equals("KRW"))
        break label272;
      i = 17;
      break label272;
      if (!((String)localObject2).equals("MGA"))
        break label272;
      i = 18;
      break label272;
      if (!((String)localObject2).equals("PYG"))
        break label272;
      i = 19;
      break label272;
      if (!((String)localObject2).equals("RWF"))
        break label272;
      i = 20;
      break label272;
      if (!((String)localObject2).equals("UGX"))
        break label272;
      i = 21;
      break label272;
      if (!((String)localObject2).equals("UYI"))
        break label272;
      i = 22;
      break label272;
      if (!((String)localObject2).equals("VND"))
        break label272;
      i = 23;
      break label272;
      if (!((String)localObject2).equals("VUV"))
        break label272;
      i = 24;
      break label272;
      if (!((String)localObject2).equals("XAF"))
        break label272;
      i = 25;
      break label272;
      if (!((String)localObject2).equals("XOF"))
        break label272;
      i = 26;
      break label272;
      if (!((String)localObject2).equals("XPF"))
        break label272;
      i = 27;
      break label272;
      if (!((String)localObject2).equals("MRO"))
        break label272;
      i = 28;
      break label272;
      paramString = " %.4f";
      d = paramLong / 10000.0D;
      break label416;
      paramString = " %.3f";
      d = paramLong / 1000.0D;
      break label416;
      paramString = " %.0f";
      d = paramLong;
      break label416;
      paramString = " %.1f";
      d = paramLong / 10.0D;
      break label416;
      paramString = this.systemDefaultLocale;
      break label440;
    }
    label1084: StringBuilder localStringBuilder = new StringBuilder();
    if (j != 0);
    for (Object localObject1 = "-"; ; localObject1 = "")
      return (String)localObject1 + String.format(new StringBuilder().append((String)localObject2).append(paramString).toString(), new Object[] { Double.valueOf(d) });
  }

  public Locale getCurrentLocale()
  {
    return getInstance().currentLocale;
  }

  public Locale getSystemDefaultLocale()
  {
    return this.systemDefaultLocale;
  }

  public String getTranslitString(String paramString)
  {
    if (this.translitChars == null)
    {
      this.translitChars = new HashMap(520);
      this.translitChars.put("ȼ", "c");
      this.translitChars.put("ᶇ", "n");
      this.translitChars.put("ɖ", "d");
      this.translitChars.put("ỿ", "y");
      this.translitChars.put("ᴓ", "o");
      this.translitChars.put("ø", "o");
      this.translitChars.put("ḁ", "a");
      this.translitChars.put("ʯ", "h");
      this.translitChars.put("ŷ", "y");
      this.translitChars.put("ʞ", "k");
      this.translitChars.put("ừ", "u");
      this.translitChars.put("ꜳ", "aa");
      this.translitChars.put("ĳ", "ij");
      this.translitChars.put("ḽ", "l");
      this.translitChars.put("ɪ", "i");
      this.translitChars.put("ḇ", "b");
      this.translitChars.put("ʀ", "r");
      this.translitChars.put("ě", "e");
      this.translitChars.put("ﬃ", "ffi");
      this.translitChars.put("ơ", "o");
      this.translitChars.put("ⱹ", "r");
      this.translitChars.put("ồ", "o");
      this.translitChars.put("ǐ", "i");
      this.translitChars.put("ꝕ", "p");
      this.translitChars.put("ý", "y");
      this.translitChars.put("ḝ", "e");
      this.translitChars.put("ₒ", "o");
      this.translitChars.put("ⱥ", "a");
      this.translitChars.put("ʙ", "b");
      this.translitChars.put("ḛ", "e");
      this.translitChars.put("ƈ", "c");
      this.translitChars.put("ɦ", "h");
      this.translitChars.put("ᵬ", "b");
      this.translitChars.put("ṣ", "s");
      this.translitChars.put("đ", "d");
      this.translitChars.put("ỗ", "o");
      this.translitChars.put("ɟ", "j");
      this.translitChars.put("ẚ", "a");
      this.translitChars.put("ɏ", "y");
      this.translitChars.put("л", "l");
      this.translitChars.put("ʌ", "v");
      this.translitChars.put("ꝓ", "p");
      this.translitChars.put("ﬁ", "fi");
      this.translitChars.put("ᶄ", "k");
      this.translitChars.put("ḏ", "d");
      this.translitChars.put("ᴌ", "l");
      this.translitChars.put("ė", "e");
      this.translitChars.put("ё", "yo");
      this.translitChars.put("ᴋ", "k");
      this.translitChars.put("ċ", "c");
      this.translitChars.put("ʁ", "r");
      this.translitChars.put("ƕ", "hv");
      this.translitChars.put("ƀ", "b");
      this.translitChars.put("ṍ", "o");
      this.translitChars.put("ȣ", "ou");
      this.translitChars.put("ǰ", "j");
      this.translitChars.put("ᶃ", "g");
      this.translitChars.put("ṋ", "n");
      this.translitChars.put("ɉ", "j");
      this.translitChars.put("ǧ", "g");
      this.translitChars.put("ǳ", "dz");
      this.translitChars.put("ź", "z");
      this.translitChars.put("ꜷ", "au");
      this.translitChars.put("ǖ", "u");
      this.translitChars.put("ᵹ", "g");
      this.translitChars.put("ȯ", "o");
      this.translitChars.put("ɐ", "a");
      this.translitChars.put("ą", "a");
      this.translitChars.put("õ", "o");
      this.translitChars.put("ɻ", "r");
      this.translitChars.put("ꝍ", "o");
      this.translitChars.put("ǟ", "a");
      this.translitChars.put("ȴ", "l");
      this.translitChars.put("ʂ", "s");
      this.translitChars.put("ﬂ", "fl");
      this.translitChars.put("ȉ", "i");
      this.translitChars.put("ⱻ", "e");
      this.translitChars.put("ṉ", "n");
      this.translitChars.put("ï", "i");
      this.translitChars.put("ñ", "n");
      this.translitChars.put("ᴉ", "i");
      this.translitChars.put("ʇ", "t");
      this.translitChars.put("ẓ", "z");
      this.translitChars.put("ỷ", "y");
      this.translitChars.put("ȳ", "y");
      this.translitChars.put("ṩ", "s");
      this.translitChars.put("ɽ", "r");
      this.translitChars.put("ĝ", "g");
      this.translitChars.put("в", "v");
      this.translitChars.put("ᴝ", "u");
      this.translitChars.put("ḳ", "k");
      this.translitChars.put("ꝫ", "et");
      this.translitChars.put("ī", "i");
      this.translitChars.put("ť", "t");
      this.translitChars.put("ꜿ", "c");
      this.translitChars.put("ʟ", "l");
      this.translitChars.put("ꜹ", "av");
      this.translitChars.put("û", "u");
      this.translitChars.put("æ", "ae");
      this.translitChars.put("и", "i");
      this.translitChars.put("ă", "a");
      this.translitChars.put("ǘ", "u");
      this.translitChars.put("ꞅ", "s");
      this.translitChars.put("ᵣ", "r");
      this.translitChars.put("ᴀ", "a");
      this.translitChars.put("ƃ", "b");
      this.translitChars.put("ḩ", "h");
      this.translitChars.put("ṧ", "s");
      this.translitChars.put("ₑ", "e");
      this.translitChars.put("ʜ", "h");
      this.translitChars.put("ẋ", "x");
      this.translitChars.put("ꝅ", "k");
      this.translitChars.put("ḋ", "d");
      this.translitChars.put("ƣ", "oi");
      this.translitChars.put("ꝑ", "p");
      this.translitChars.put("ħ", "h");
      this.translitChars.put("ⱴ", "v");
      this.translitChars.put("ẇ", "w");
      this.translitChars.put("ǹ", "n");
      this.translitChars.put("ɯ", "m");
      this.translitChars.put("ɡ", "g");
      this.translitChars.put("ɴ", "n");
      this.translitChars.put("ᴘ", "p");
      this.translitChars.put("ᵥ", "v");
      this.translitChars.put("ū", "u");
      this.translitChars.put("ḃ", "b");
      this.translitChars.put("ṗ", "p");
      this.translitChars.put("ь", "");
      this.translitChars.put("å", "a");
      this.translitChars.put("ɕ", "c");
      this.translitChars.put("ọ", "o");
      this.translitChars.put("ắ", "a");
      this.translitChars.put("ƒ", "f");
      this.translitChars.put("ǣ", "ae");
      this.translitChars.put("ꝡ", "vy");
      this.translitChars.put("ﬀ", "ff");
      this.translitChars.put("ᶉ", "r");
      this.translitChars.put("ô", "o");
      this.translitChars.put("ǿ", "o");
      this.translitChars.put("ṳ", "u");
      this.translitChars.put("ȥ", "z");
      this.translitChars.put("ḟ", "f");
      this.translitChars.put("ḓ", "d");
      this.translitChars.put("ȇ", "e");
      this.translitChars.put("ȕ", "u");
      this.translitChars.put("п", "p");
      this.translitChars.put("ȵ", "n");
      this.translitChars.put("ʠ", "q");
      this.translitChars.put("ấ", "a");
      this.translitChars.put("ǩ", "k");
      this.translitChars.put("ĩ", "i");
      this.translitChars.put("ṵ", "u");
      this.translitChars.put("ŧ", "t");
      this.translitChars.put("ɾ", "r");
      this.translitChars.put("ƙ", "k");
      this.translitChars.put("ṫ", "t");
      this.translitChars.put("ꝗ", "q");
      this.translitChars.put("ậ", "a");
      this.translitChars.put("н", "n");
      this.translitChars.put("ʄ", "j");
      this.translitChars.put("ƚ", "l");
      this.translitChars.put("ᶂ", "f");
      this.translitChars.put("д", "d");
      this.translitChars.put("ᵴ", "s");
      this.translitChars.put("ꞃ", "r");
      this.translitChars.put("ᶌ", "v");
      this.translitChars.put("ɵ", "o");
      this.translitChars.put("ḉ", "c");
      this.translitChars.put("ᵤ", "u");
      this.translitChars.put("ẑ", "z");
      this.translitChars.put("ṹ", "u");
      this.translitChars.put("ň", "n");
      this.translitChars.put("ʍ", "w");
      this.translitChars.put("ầ", "a");
      this.translitChars.put("ǉ", "lj");
      this.translitChars.put("ɓ", "b");
      this.translitChars.put("ɼ", "r");
      this.translitChars.put("ò", "o");
      this.translitChars.put("ẘ", "w");
      this.translitChars.put("ɗ", "d");
      this.translitChars.put("ꜽ", "ay");
      this.translitChars.put("ư", "u");
      this.translitChars.put("ᶀ", "b");
      this.translitChars.put("ǜ", "u");
      this.translitChars.put("ẹ", "e");
      this.translitChars.put("ǡ", "a");
      this.translitChars.put("ɥ", "h");
      this.translitChars.put("ṏ", "o");
      this.translitChars.put("ǔ", "u");
      this.translitChars.put("ʎ", "y");
      this.translitChars.put("ȱ", "o");
      this.translitChars.put("ệ", "e");
      this.translitChars.put("ế", "e");
      this.translitChars.put("ĭ", "i");
      this.translitChars.put("ⱸ", "e");
      this.translitChars.put("ṯ", "t");
      this.translitChars.put("ᶑ", "d");
      this.translitChars.put("ḧ", "h");
      this.translitChars.put("ṥ", "s");
      this.translitChars.put("ë", "e");
      this.translitChars.put("ᴍ", "m");
      this.translitChars.put("ö", "o");
      this.translitChars.put("é", "e");
      this.translitChars.put("ı", "i");
      this.translitChars.put("ď", "d");
      this.translitChars.put("ᵯ", "m");
      this.translitChars.put("ỵ", "y");
      this.translitChars.put("я", "ya");
      this.translitChars.put("ŵ", "w");
      this.translitChars.put("ề", "e");
      this.translitChars.put("ứ", "u");
      this.translitChars.put("ƶ", "z");
      this.translitChars.put("ĵ", "j");
      this.translitChars.put("ḍ", "d");
      this.translitChars.put("ŭ", "u");
      this.translitChars.put("ʝ", "j");
      this.translitChars.put("ж", "zh");
      this.translitChars.put("ê", "e");
      this.translitChars.put("ǚ", "u");
      this.translitChars.put("ġ", "g");
      this.translitChars.put("ṙ", "r");
      this.translitChars.put("ƞ", "n");
      this.translitChars.put("ъ", "");
      this.translitChars.put("ḗ", "e");
      this.translitChars.put("ẝ", "s");
      this.translitChars.put("ᶁ", "d");
      this.translitChars.put("ķ", "k");
      this.translitChars.put("ᴂ", "ae");
      this.translitChars.put("ɘ", "e");
      this.translitChars.put("ợ", "o");
      this.translitChars.put("ḿ", "m");
      this.translitChars.put("ꜰ", "f");
      this.translitChars.put("а", "a");
      this.translitChars.put("ẵ", "a");
      this.translitChars.put("ꝏ", "oo");
      this.translitChars.put("ᶆ", "m");
      this.translitChars.put("ᵽ", "p");
      this.translitChars.put("ц", "ts");
      this.translitChars.put("ữ", "u");
      this.translitChars.put("ⱪ", "k");
      this.translitChars.put("ḥ", "h");
      this.translitChars.put("ţ", "t");
      this.translitChars.put("ᵱ", "p");
      this.translitChars.put("ṁ", "m");
      this.translitChars.put("á", "a");
      this.translitChars.put("ᴎ", "n");
      this.translitChars.put("ꝟ", "v");
      this.translitChars.put("è", "e");
      this.translitChars.put("ᶎ", "z");
      this.translitChars.put("ꝺ", "d");
      this.translitChars.put("ᶈ", "p");
      this.translitChars.put("м", "m");
      this.translitChars.put("ɫ", "l");
      this.translitChars.put("ᴢ", "z");
      this.translitChars.put("ɱ", "m");
      this.translitChars.put("ṝ", "r");
      this.translitChars.put("ṽ", "v");
      this.translitChars.put("ũ", "u");
      this.translitChars.put("ß", "ss");
      this.translitChars.put("т", "t");
      this.translitChars.put("ĥ", "h");
      this.translitChars.put("ᵵ", "t");
      this.translitChars.put("ʐ", "z");
      this.translitChars.put("ṟ", "r");
      this.translitChars.put("ɲ", "n");
      this.translitChars.put("à", "a");
      this.translitChars.put("ẙ", "y");
      this.translitChars.put("ỳ", "y");
      this.translitChars.put("ᴔ", "oe");
      this.translitChars.put("ы", "i");
      this.translitChars.put("ₓ", "x");
      this.translitChars.put("ȗ", "u");
      this.translitChars.put("ⱼ", "j");
      this.translitChars.put("ẫ", "a");
      this.translitChars.put("ʑ", "z");
      this.translitChars.put("ẛ", "s");
      this.translitChars.put("ḭ", "i");
      this.translitChars.put("ꜵ", "ao");
      this.translitChars.put("ɀ", "z");
      this.translitChars.put("ÿ", "y");
      this.translitChars.put("ǝ", "e");
      this.translitChars.put("ǭ", "o");
      this.translitChars.put("ᴅ", "d");
      this.translitChars.put("ᶅ", "l");
      this.translitChars.put("ù", "u");
      this.translitChars.put("ạ", "a");
      this.translitChars.put("ḅ", "b");
      this.translitChars.put("ụ", "u");
      this.translitChars.put("к", "k");
      this.translitChars.put("ằ", "a");
      this.translitChars.put("ᴛ", "t");
      this.translitChars.put("ƴ", "y");
      this.translitChars.put("ⱦ", "t");
      this.translitChars.put("з", "z");
      this.translitChars.put("ⱡ", "l");
      this.translitChars.put("ȷ", "j");
      this.translitChars.put("ᵶ", "z");
      this.translitChars.put("ḫ", "h");
      this.translitChars.put("ⱳ", "w");
      this.translitChars.put("ḵ", "k");
      this.translitChars.put("ờ", "o");
      this.translitChars.put("î", "i");
      this.translitChars.put("ģ", "g");
      this.translitChars.put("ȅ", "e");
      this.translitChars.put("ȧ", "a");
      this.translitChars.put("ẳ", "a");
      this.translitChars.put("щ", "sch");
      this.translitChars.put("ɋ", "q");
      this.translitChars.put("ṭ", "t");
      this.translitChars.put("ꝸ", "um");
      this.translitChars.put("ᴄ", "c");
      this.translitChars.put("ẍ", "x");
      this.translitChars.put("ủ", "u");
      this.translitChars.put("ỉ", "i");
      this.translitChars.put("ᴚ", "r");
      this.translitChars.put("ś", "s");
      this.translitChars.put("ꝋ", "o");
      this.translitChars.put("ỹ", "y");
      this.translitChars.put("ṡ", "s");
      this.translitChars.put("ǌ", "nj");
      this.translitChars.put("ȁ", "a");
      this.translitChars.put("ẗ", "t");
      this.translitChars.put("ĺ", "l");
      this.translitChars.put("ž", "z");
      this.translitChars.put("ᵺ", "th");
      this.translitChars.put("ƌ", "d");
      this.translitChars.put("ș", "s");
      this.translitChars.put("š", "s");
      this.translitChars.put("ᶙ", "u");
      this.translitChars.put("ẽ", "e");
      this.translitChars.put("ẜ", "s");
      this.translitChars.put("ɇ", "e");
      this.translitChars.put("ṷ", "u");
      this.translitChars.put("ố", "o");
      this.translitChars.put("ȿ", "s");
      this.translitChars.put("ᴠ", "v");
      this.translitChars.put("ꝭ", "is");
      this.translitChars.put("ᴏ", "o");
      this.translitChars.put("ɛ", "e");
      this.translitChars.put("ǻ", "a");
      this.translitChars.put("ﬄ", "ffl");
      this.translitChars.put("ⱺ", "o");
      this.translitChars.put("ȋ", "i");
      this.translitChars.put("ᵫ", "ue");
      this.translitChars.put("ȡ", "d");
      this.translitChars.put("ⱬ", "z");
      this.translitChars.put("ẁ", "w");
      this.translitChars.put("ᶏ", "a");
      this.translitChars.put("ꞇ", "t");
      this.translitChars.put("ğ", "g");
      this.translitChars.put("ɳ", "n");
      this.translitChars.put("ʛ", "g");
      this.translitChars.put("ᴜ", "u");
      this.translitChars.put("ф", "f");
      this.translitChars.put("ẩ", "a");
      this.translitChars.put("ṅ", "n");
      this.translitChars.put("ɨ", "i");
      this.translitChars.put("ᴙ", "r");
      this.translitChars.put("ǎ", "a");
      this.translitChars.put("ſ", "s");
      this.translitChars.put("у", "u");
      this.translitChars.put("ȫ", "o");
      this.translitChars.put("ɿ", "r");
      this.translitChars.put("ƭ", "t");
      this.translitChars.put("ḯ", "i");
      this.translitChars.put("ǽ", "ae");
      this.translitChars.put("ⱱ", "v");
      this.translitChars.put("ɶ", "oe");
      this.translitChars.put("ṃ", "m");
      this.translitChars.put("ż", "z");
      this.translitChars.put("ĕ", "e");
      this.translitChars.put("ꜻ", "av");
      this.translitChars.put("ở", "o");
      this.translitChars.put("ễ", "e");
      this.translitChars.put("ɬ", "l");
      this.translitChars.put("ị", "i");
      this.translitChars.put("ᵭ", "d");
      this.translitChars.put("ﬆ", "st");
      this.translitChars.put("ḷ", "l");
      this.translitChars.put("ŕ", "r");
      this.translitChars.put("ᴕ", "ou");
      this.translitChars.put("ʈ", "t");
      this.translitChars.put("ā", "a");
      this.translitChars.put("э", "e");
      this.translitChars.put("ḙ", "e");
      this.translitChars.put("ᴑ", "o");
      this.translitChars.put("ç", "c");
      this.translitChars.put("ᶊ", "s");
      this.translitChars.put("ặ", "a");
      this.translitChars.put("ų", "u");
      this.translitChars.put("ả", "a");
      this.translitChars.put("ǥ", "g");
      this.translitChars.put("р", "r");
      this.translitChars.put("ꝁ", "k");
      this.translitChars.put("ẕ", "z");
      this.translitChars.put("ŝ", "s");
      this.translitChars.put("ḕ", "e");
      this.translitChars.put("ɠ", "g");
      this.translitChars.put("ꝉ", "l");
      this.translitChars.put("ꝼ", "f");
      this.translitChars.put("ᶍ", "x");
      this.translitChars.put("х", "h");
      this.translitChars.put("ǒ", "o");
      this.translitChars.put("ę", "e");
      this.translitChars.put("ổ", "o");
      this.translitChars.put("ƫ", "t");
      this.translitChars.put("ǫ", "o");
      this.translitChars.put("i̇", "i");
      this.translitChars.put("ṇ", "n");
      this.translitChars.put("ć", "c");
      this.translitChars.put("ᵷ", "g");
      this.translitChars.put("ẅ", "w");
      this.translitChars.put("ḑ", "d");
      this.translitChars.put("ḹ", "l");
      this.translitChars.put("ч", "ch");
      this.translitChars.put("œ", "oe");
      this.translitChars.put("ᵳ", "r");
      this.translitChars.put("ļ", "l");
      this.translitChars.put("ȑ", "r");
      this.translitChars.put("ȭ", "o");
      this.translitChars.put("ᵰ", "n");
      this.translitChars.put("ᴁ", "ae");
      this.translitChars.put("ŀ", "l");
      this.translitChars.put("ä", "a");
      this.translitChars.put("ƥ", "p");
      this.translitChars.put("ỏ", "o");
      this.translitChars.put("į", "i");
      this.translitChars.put("ȓ", "r");
      this.translitChars.put("ǆ", "dz");
      this.translitChars.put("ḡ", "g");
      this.translitChars.put("ṻ", "u");
      this.translitChars.put("ō", "o");
      this.translitChars.put("ľ", "l");
      this.translitChars.put("ẃ", "w");
      this.translitChars.put("ț", "t");
      this.translitChars.put("ń", "n");
      this.translitChars.put("ɍ", "r");
      this.translitChars.put("ȃ", "a");
      this.translitChars.put("ü", "u");
      this.translitChars.put("ꞁ", "l");
      this.translitChars.put("ᴐ", "o");
      this.translitChars.put("ớ", "o");
      this.translitChars.put("ᴃ", "b");
      this.translitChars.put("ɹ", "r");
      this.translitChars.put("ᵲ", "r");
      this.translitChars.put("ʏ", "y");
      this.translitChars.put("ᵮ", "f");
      this.translitChars.put("ⱨ", "h");
      this.translitChars.put("ŏ", "o");
      this.translitChars.put("ú", "u");
      this.translitChars.put("ṛ", "r");
      this.translitChars.put("ʮ", "h");
      this.translitChars.put("ó", "o");
      this.translitChars.put("ů", "u");
      this.translitChars.put("ỡ", "o");
      this.translitChars.put("ṕ", "p");
      this.translitChars.put("ᶖ", "i");
      this.translitChars.put("ự", "u");
      this.translitChars.put("ã", "a");
      this.translitChars.put("ᵢ", "i");
      this.translitChars.put("ṱ", "t");
      this.translitChars.put("ể", "e");
      this.translitChars.put("ử", "u");
      this.translitChars.put("í", "i");
      this.translitChars.put("ɔ", "o");
      this.translitChars.put("с", "s");
      this.translitChars.put("й", "i");
      this.translitChars.put("ɺ", "r");
      this.translitChars.put("ɢ", "g");
      this.translitChars.put("ř", "r");
      this.translitChars.put("ẖ", "h");
      this.translitChars.put("ű", "u");
      this.translitChars.put("ȍ", "o");
      this.translitChars.put("ш", "sh");
      this.translitChars.put("ḻ", "l");
      this.translitChars.put("ḣ", "h");
      this.translitChars.put("ȶ", "t");
      this.translitChars.put("ņ", "n");
      this.translitChars.put("ᶒ", "e");
      this.translitChars.put("ì", "i");
      this.translitChars.put("ẉ", "w");
      this.translitChars.put("б", "b");
      this.translitChars.put("ē", "e");
      this.translitChars.put("ᴇ", "e");
      this.translitChars.put("ł", "l");
      this.translitChars.put("ộ", "o");
      this.translitChars.put("ɭ", "l");
      this.translitChars.put("ẏ", "y");
      this.translitChars.put("ᴊ", "j");
      this.translitChars.put("ḱ", "k");
      this.translitChars.put("ṿ", "v");
      this.translitChars.put("ȩ", "e");
      this.translitChars.put("â", "a");
      this.translitChars.put("ş", "s");
      this.translitChars.put("ŗ", "r");
      this.translitChars.put("ʋ", "v");
      this.translitChars.put("ₐ", "a");
      this.translitChars.put("ↄ", "c");
      this.translitChars.put("ᶓ", "e");
      this.translitChars.put("ɰ", "m");
      this.translitChars.put("е", "e");
      this.translitChars.put("ᴡ", "w");
      this.translitChars.put("ȏ", "o");
      this.translitChars.put("č", "c");
      this.translitChars.put("ǵ", "g");
      this.translitChars.put("ĉ", "c");
      this.translitChars.put("ю", "yu");
      this.translitChars.put("ᶗ", "o");
      this.translitChars.put("ꝃ", "k");
      this.translitChars.put("ꝙ", "q");
      this.translitChars.put("г", "g");
      this.translitChars.put("ṑ", "o");
      this.translitChars.put("ꜱ", "s");
      this.translitChars.put("ṓ", "o");
      this.translitChars.put("ȟ", "h");
      this.translitChars.put("ő", "o");
      this.translitChars.put("ꜩ", "tz");
      this.translitChars.put("ẻ", "e");
      this.translitChars.put("о", "o");
    }
    StringBuilder localStringBuilder = new StringBuilder(paramString.length());
    int j = paramString.length();
    int i = 0;
    if (i < j)
    {
      String str1 = paramString.substring(i, i + 1);
      String str2 = (String)this.translitChars.get(str1);
      if (str2 != null)
        localStringBuilder.append(str2);
      while (true)
      {
        i += 1;
        break;
        localStringBuilder.append(str1);
      }
    }
    return localStringBuilder.toString();
  }

  public void onDeviceConfigurationChange(Configuration paramConfiguration)
  {
    if (this.changingConfiguration);
    do
    {
      do
      {
        return;
        is24HourFormat = DateFormat.is24HourFormat(ApplicationLoader.applicationContext);
        this.systemDefaultLocale = paramConfiguration.locale;
        if (this.languageOverride != null)
        {
          paramConfiguration = this.currentLocaleInfo;
          this.currentLocaleInfo = null;
          applyLanguage(paramConfiguration, false);
          return;
        }
        paramConfiguration = paramConfiguration.locale;
      }
      while (paramConfiguration == null);
      String str1 = paramConfiguration.getDisplayName();
      String str2 = this.currentLocale.getDisplayName();
      if ((str1 != null) && (str2 != null) && (!str1.equals(str2)))
        recreateFormatters();
      this.currentLocale = paramConfiguration;
      this.currentPluralRules = ((PluralRules)this.allRules.get(this.currentLocale.getLanguage()));
    }
    while (this.currentPluralRules != null);
    this.currentPluralRules = ((PluralRules)this.allRules.get("en"));
  }

  public void recreateFormatters()
  {
    int i = 1;
    Object localObject2 = this.currentLocale;
    Object localObject1 = localObject2;
    if (localObject2 == null)
      localObject1 = Locale.getDefault();
    String str1 = ((Locale)localObject1).getLanguage();
    localObject2 = str1;
    if (str1 == null)
      localObject2 = "en";
    if ((((String)localObject2).toLowerCase().equals("ar")) || (((String)localObject2).toLowerCase().equals("fa")))
      isRTL = true;
    if (((String)localObject2).toLowerCase().equals("ko"))
      i = 2;
    nameDisplayOrder = i;
    this.formatterMonth = createFormatter((Locale)localObject1, getStringInternal("formatterMonth", 2131166665), "dd MMM");
    this.formatterYear = createFormatter((Locale)localObject1, getStringInternal("formatterYear", 2131166670), "dd.MM.yy");
    this.formatterYearMax = createFormatter((Locale)localObject1, getStringInternal("formatterYearMax", 2131166671), "dd.MM.yyyy");
    this.chatDate = createFormatter((Locale)localObject1, getStringInternal("chatDate", 2131166660), "d MMMM");
    this.chatFullDate = createFormatter((Locale)localObject1, getStringInternal("chatFullDate", 2131166661), "d MMMM yyyy");
    this.formatterWeek = createFormatter((Locale)localObject1, getStringInternal("formatterWeek", 2131166669), "EEE");
    this.formatterMonthYear = createFormatter((Locale)localObject1, getStringInternal("formatterMonthYear", 2131166666), "MMMM yyyy");
    label296: String str2;
    if ((((String)localObject2).toLowerCase().equals("fa")) || (((String)localObject2).toLowerCase().equals("ar")) || (((String)localObject2).toLowerCase().equals("ko")))
    {
      localObject2 = localObject1;
      if (!is24HourFormat)
        break label368;
      str1 = getStringInternal("formatterDay24H", 2131166664);
      if (!is24HourFormat)
        break label383;
      str2 = "HH:mm";
      label307: this.formatterDay = createFormatter((Locale)localObject2, str1, str2);
      if (!is24HourFormat)
        break label391;
      localObject2 = getStringInternal("formatterStats24H", 2131166668);
      label337: if (!is24HourFormat)
        break label405;
    }
    label391: label405: for (str1 = "MMM dd yyyy, HH:mm"; ; str1 = "MMM dd yyyy, h:mm a")
    {
      this.formatterStats = createFormatter((Locale)localObject1, (String)localObject2, str1);
      return;
      localObject2 = Locale.US;
      break;
      label368: str1 = getStringInternal("formatterDay12H", 2131166663);
      break label296;
      label383: str2 = "h:mm a";
      break label307;
      localObject2 = getStringInternal("formatterStats12H", 2131166667);
      break label337;
    }
  }

  public static class LocaleInfo
  {
    public String name;
    public String nameEnglish;
    public String pathToFile;
    public String shortName;

    public static LocaleInfo createWithString(String paramString)
    {
      if ((paramString == null) || (paramString.length() == 0));
      do
      {
        return null;
        paramString = paramString.split("\\|");
      }
      while (paramString.length != 4);
      LocaleInfo localLocaleInfo = new LocaleInfo();
      localLocaleInfo.name = paramString[0];
      localLocaleInfo.nameEnglish = paramString[1];
      localLocaleInfo.shortName = paramString[2];
      localLocaleInfo.pathToFile = paramString[3];
      return localLocaleInfo;
    }

    public String getSaveString()
    {
      return this.name + "|" + this.nameEnglish + "|" + this.shortName + "|" + this.pathToFile;
    }
  }

  public static abstract class PluralRules
  {
    abstract int quantityForNumber(int paramInt);
  }

  public static class PluralRules_Arabic extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 0)
        return 1;
      if (paramInt == 1)
        return 2;
      if (paramInt == 2)
        return 4;
      if ((i >= 3) && (i <= 10))
        return 8;
      if ((i >= 11) && (i <= 99))
        return 16;
      return 0;
    }
  }

  public static class PluralRules_Balkan extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      paramInt %= 10;
      if ((paramInt == 1) && (i != 11))
        return 2;
      if ((paramInt >= 2) && (paramInt <= 4) && ((i < 12) || (i > 14)))
        return 8;
      if ((paramInt == 0) || ((paramInt >= 5) && (paramInt <= 9)) || ((i >= 11) && (i <= 14)))
        return 16;
      return 0;
    }
  }

  public static class PluralRules_Breton extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0)
        return 1;
      if (paramInt == 1)
        return 2;
      if (paramInt == 2)
        return 4;
      if (paramInt == 3)
        return 8;
      if (paramInt == 6)
        return 16;
      return 0;
    }
  }

  public static class PluralRules_Czech extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1)
        return 2;
      if ((paramInt >= 2) && (paramInt <= 4))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_French extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt < 2))
        return 2;
      return 0;
    }
  }

  public static class PluralRules_Langi extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = 2;
      if (paramInt == 0)
        i = 1;
      do
        return i;
      while ((paramInt > 0) && (paramInt < 2));
      return 0;
    }
  }

  public static class PluralRules_Latvian extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0)
        return 1;
      if ((paramInt % 10 == 1) && (paramInt % 100 != 11))
        return 2;
      return 0;
    }
  }

  public static class PluralRules_Lithuanian extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      paramInt %= 10;
      if ((paramInt == 1) && ((i < 11) || (i > 19)))
        return 2;
      if ((paramInt >= 2) && (paramInt <= 9) && ((i < 11) || (i > 19)))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_Macedonian extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt % 10 == 1) && (paramInt != 11))
        return 2;
      return 0;
    }
  }

  public static class PluralRules_Maltese extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 1)
        return 2;
      if ((paramInt == 0) || ((i >= 2) && (i <= 10)))
        return 8;
      if ((i >= 11) && (i <= 19))
        return 16;
      return 0;
    }
  }

  public static class PluralRules_None extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      return 0;
    }
  }

  public static class PluralRules_One extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1)
        return 2;
      return 0;
    }
  }

  public static class PluralRules_Polish extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      int j = paramInt % 10;
      if (paramInt == 1)
        return 2;
      if ((j >= 2) && (j <= 4) && ((i < 12) || (i > 14)) && ((i < 22) || (i > 24)))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_Romanian extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      int i = paramInt % 100;
      if (paramInt == 1)
        return 2;
      if ((paramInt == 0) || ((i >= 1) && (i <= 19)))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_Slovenian extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      paramInt %= 100;
      if (paramInt == 1)
        return 2;
      if (paramInt == 2)
        return 4;
      if ((paramInt >= 3) && (paramInt <= 4))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_Tachelhit extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt >= 0) && (paramInt <= 1))
        return 2;
      if ((paramInt >= 2) && (paramInt <= 10))
        return 8;
      return 0;
    }
  }

  public static class PluralRules_Two extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 1)
        return 2;
      if (paramInt == 2)
        return 4;
      return 0;
    }
  }

  public static class PluralRules_Welsh extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if (paramInt == 0)
        return 1;
      if (paramInt == 1)
        return 2;
      if (paramInt == 2)
        return 4;
      if (paramInt == 3)
        return 8;
      if (paramInt == 6)
        return 16;
      return 0;
    }
  }

  public static class PluralRules_Zero extends LocaleController.PluralRules
  {
    public int quantityForNumber(int paramInt)
    {
      if ((paramInt == 0) || (paramInt == 1))
        return 2;
      return 0;
    }
  }

  private class TimeZoneChangedReceiver extends BroadcastReceiver
  {
    private TimeZoneChangedReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      ApplicationLoader.applicationHandler.post(new Runnable()
      {
        public void run()
        {
          if (!LocaleController.this.formatterMonth.getTimeZone().equals(TimeZone.getDefault()))
            LocaleController.getInstance().recreateFormatters();
        }
      });
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.LocaleController
 * JD-Core Version:    0.6.0
 */