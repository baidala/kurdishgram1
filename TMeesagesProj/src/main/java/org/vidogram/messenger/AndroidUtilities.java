package org.vidogram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore.Audio.Media;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Video.Media;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.h;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.a.a.a.a;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Locale;
import java.util.regex.Pattern;
import net.hockeyapp.android.c;
import net.hockeyapp.android.m;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.ForegroundDetector;
import org.vidogram.ui.Components.TypefaceSpan;

public class AndroidUtilities
{
  public static final int FLAG_TAG_ALL = 3;
  public static final int FLAG_TAG_BOLD = 2;
  public static final int FLAG_TAG_BR = 1;
  public static final int FLAG_TAG_COLOR = 4;
  public static Pattern WEB_URL;
  private static int adjustOwnerClassGuid;
  private static RectF bitmapRect;
  private static final Object callLock;
  private static ContentObserver callLogContentObserver;
  public static float density;
  public static DisplayMetrics displayMetrics;
  public static Point displaySize;
  private static boolean hasCallPermissions;
  public static boolean incorrectDisplaySizeFix;
  public static boolean isInMultiwindow;
  private static Boolean isTablet;
  public static int leftBaseline;
  private static Field mAttachInfoField;
  private static Field mStableInsetsField;
  public static Integer photoSize;
  private static int prevOrientation;
  private static Paint roundPaint;
  private static final Object smsLock;
  public static int statusBarHeight;
  private static final Hashtable<String, Typeface> typefaceCache = new Hashtable();
  private static Runnable unregisterRunnable;
  public static boolean usingHardwareInput;
  private static boolean waitingForCall;
  private static boolean waitingForSms;

  static
  {
    prevOrientation = -10;
    waitingForSms = false;
    waitingForCall = false;
    smsLock = new Object();
    callLock = new Object();
    statusBarHeight = 0;
    density = 1.0F;
    displaySize = new Point();
    photoSize = null;
    displayMetrics = new DisplayMetrics();
    isTablet = null;
    adjustOwnerClassGuid = 0;
    WEB_URL = null;
    try
    {
      Pattern localPattern = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))");
      localPattern = Pattern.compile("(([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]([a-zA-Z0-9 -퟿豈-﷏ﷰ-￯\\-]{0,61}[a-zA-Z0-9 -퟿豈-﷏ﷰ-￯]){0,1}\\.)+[a-zA-Z -퟿豈-﷏ﷰ-￯]{2,63}|" + localPattern + ")");
      WEB_URL = Pattern.compile("((?:(http|https|Http|Https):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + localPattern + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[" + "a-zA-Z0-9 -퟿豈-﷏ﷰ-￯" + "\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
      if (isTablet())
      {
        i = 80;
        leftBaseline = i;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        if (Build.VERSION.SDK_INT < 23)
          break label210;
        bool = true;
        hasCallPermissions = bool;
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
      {
        FileLog.e(localException);
        continue;
        int i = 72;
        continue;
        label210: boolean bool = false;
      }
    }
  }

  public static void addMediaToGallery(Uri paramUri)
  {
    if (paramUri == null)
      return;
    try
    {
      Intent localIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
      localIntent.setData(paramUri);
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception paramUri)
    {
      FileLog.e(paramUri);
    }
  }

  public static void addMediaToGallery(String paramString)
  {
    if (paramString == null)
      return;
    addMediaToGallery(Uri.fromFile(new File(paramString)));
  }

  public static void addToClipboard(CharSequence paramCharSequence)
  {
    try
    {
      ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", paramCharSequence));
      return;
    }
    catch (Exception paramCharSequence)
    {
      FileLog.e(paramCharSequence);
    }
  }

  public static byte[] calcAuthKeyHash(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = Utilities.computeSHA1(paramArrayOfByte);
    byte[] arrayOfByte = new byte[16];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, 16);
    return arrayOfByte;
  }

  public static int[] calcDrawableColor(Drawable paramDrawable)
  {
    int k = -16777216;
    int j = k;
    try
    {
      if ((paramDrawable instanceof BitmapDrawable))
      {
        j = k;
        paramDrawable = ((BitmapDrawable)paramDrawable).getBitmap();
        i = k;
        if (paramDrawable != null)
        {
          j = k;
          Bitmap localBitmap = Bitmaps.createScaledBitmap(paramDrawable, 1, 1, true);
          i = k;
          if (localBitmap != null)
          {
            j = k;
            k = localBitmap.getPixel(0, 0);
            i = k;
            if (paramDrawable != localBitmap)
            {
              j = k;
              localBitmap.recycle();
              i = k;
            }
          }
        }
      }
      while (true)
      {
        paramDrawable = rgbToHsv(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
        paramDrawable[1] = Math.min(1.0D, paramDrawable[1] + 0.05D + 0.1D * (1.0D - paramDrawable[1]));
        paramDrawable[2] = Math.max(0.0D, paramDrawable[2] * 0.65D);
        paramDrawable = hsvToRgb(paramDrawable[0], paramDrawable[1], paramDrawable[2]);
        return new int[] { Color.argb(102, paramDrawable[0], paramDrawable[1], paramDrawable[2]), Color.argb(136, paramDrawable[0], paramDrawable[1], paramDrawable[2]) };
        i = k;
        j = k;
        if (!(paramDrawable instanceof ColorDrawable))
          continue;
        j = k;
        i = ((ColorDrawable)paramDrawable).getColor();
      }
    }
    catch (Exception paramDrawable)
    {
      while (true)
      {
        FileLog.e(paramDrawable);
        int i = j;
      }
    }
  }

  public static void cancelRunOnUIThread(Runnable paramRunnable)
  {
    ApplicationLoader.applicationHandler.removeCallbacks(paramRunnable);
  }

  public static void checkDisplaySize(Context paramContext, Configuration paramConfiguration)
  {
    boolean bool = true;
    try
    {
      density = paramContext.getResources().getDisplayMetrics().density;
      Configuration localConfiguration = paramConfiguration;
      if (paramConfiguration == null)
        localConfiguration = paramContext.getResources().getConfiguration();
      if ((localConfiguration.keyboard != 1) && (localConfiguration.hardKeyboardHidden == 1));
      while (true)
      {
        usingHardwareInput = bool;
        paramContext = (WindowManager)paramContext.getSystemService("window");
        if (paramContext != null)
        {
          paramContext = paramContext.getDefaultDisplay();
          if (paramContext != null)
          {
            paramContext.getMetrics(displayMetrics);
            paramContext.getSize(displaySize);
          }
        }
        int i;
        if (localConfiguration.screenWidthDp != 0)
        {
          i = (int)Math.ceil(localConfiguration.screenWidthDp * density);
          if (Math.abs(displaySize.x - i) > 3)
            displaySize.x = i;
        }
        if (localConfiguration.screenHeightDp != 0)
        {
          i = (int)Math.ceil(localConfiguration.screenHeightDp * density);
          if (Math.abs(displaySize.y - i) > 3)
            displaySize.y = i;
        }
        FileLog.e("display size = " + displaySize.x + " " + displaySize.y + " " + displayMetrics.xdpi + "x" + displayMetrics.ydpi);
        return;
        bool = false;
      }
    }
    catch (Exception paramContext)
    {
      FileLog.e(paramContext);
    }
  }

  public static void checkForCrashes(Activity paramActivity)
  {
    if (BuildVars.DEBUG_VERSION);
    for (String str = BuildVars.HOCKEY_APP_HASH_DEBUG; ; str = BuildVars.HOCKEY_APP_HASH)
    {
      net.hockeyapp.android.b.a(paramActivity, str, new c()
      {
        public boolean includeDeviceData()
        {
          return true;
        }
      });
      return;
    }
  }

  public static void checkForUpdates(Activity paramActivity)
  {
    if (BuildVars.DEBUG_VERSION)
      if (!BuildVars.DEBUG_VERSION)
        break label22;
    label22: for (String str = BuildVars.HOCKEY_APP_HASH_DEBUG; ; str = BuildVars.HOCKEY_APP_HASH)
    {
      m.a(paramActivity, str);
      return;
    }
  }

  public static boolean checkPhonePattern(String paramString1, String paramString2)
  {
    int n = 0;
    int m;
    if ((TextUtils.isEmpty(paramString1)) || (paramString1.equals("*")))
    {
      m = 1;
      return m;
    }
    paramString1 = paramString1.split("\\*");
    paramString2 = org.vidogram.a.b.b(paramString2);
    int i = 0;
    int k;
    for (int j = 0; ; j = k)
    {
      if (i >= paramString1.length)
        break label101;
      CharSequence localCharSequence = paramString1[i];
      k = j;
      if (!TextUtils.isEmpty(localCharSequence))
      {
        j = paramString2.indexOf(localCharSequence, j);
        m = n;
        if (j == -1)
          break;
        k = j + localCharSequence.length();
      }
      i += 1;
    }
    label101: return true;
  }

  public static void clearCursorDrawable(EditText paramEditText)
  {
    if (paramEditText == null)
      return;
    try
    {
      Field localField = TextView.class.getDeclaredField("mCursorDrawableRes");
      localField.setAccessible(true);
      localField.setInt(paramEditText, 0);
      return;
    }
    catch (Exception paramEditText)
    {
      FileLog.e(paramEditText);
    }
  }

  @SuppressLint({"NewApi"})
  public static void clearDrawableAnimation(View paramView)
  {
    if ((Build.VERSION.SDK_INT < 21) || (paramView == null));
    do
    {
      while (true)
      {
        return;
        if (!(paramView instanceof ListView))
          break;
        paramView = ((ListView)paramView).getSelector();
        if (paramView == null)
          continue;
        paramView.setState(StateSet.NOTHING);
        return;
      }
      paramView = paramView.getBackground();
    }
    while (paramView == null);
    paramView.setState(StateSet.NOTHING);
    paramView.jumpToCurrentState();
  }

  public static int compare(int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2)
      return 0;
    if (paramInt1 > paramInt2)
      return 1;
    return -1;
  }

  // ERROR //
  public static boolean copyFile(File paramFile1, File paramFile2)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 4
    //   3: aload_1
    //   4: invokevirtual 518	java/io/File:exists	()Z
    //   7: ifne +8 -> 15
    //   10: aload_1
    //   11: invokevirtual 521	java/io/File:createNewFile	()Z
    //   14: pop
    //   15: new 523	java/io/FileInputStream
    //   18: dup
    //   19: aload_0
    //   20: invokespecial 526	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   23: astore_3
    //   24: new 528	java/io/FileOutputStream
    //   27: dup
    //   28: aload_1
    //   29: invokespecial 529	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   32: astore_1
    //   33: aload_1
    //   34: invokevirtual 533	java/io/FileOutputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   37: aload_3
    //   38: invokevirtual 534	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   41: lconst_0
    //   42: aload_3
    //   43: invokevirtual 534	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   46: invokevirtual 540	java/nio/channels/FileChannel:size	()J
    //   49: invokevirtual 544	java/nio/channels/FileChannel:transferFrom	(Ljava/nio/channels/ReadableByteChannel;JJ)J
    //   52: pop2
    //   53: aload_3
    //   54: ifnull +7 -> 61
    //   57: aload_3
    //   58: invokevirtual 547	java/io/FileInputStream:close	()V
    //   61: aload_1
    //   62: ifnull +7 -> 69
    //   65: aload_1
    //   66: invokevirtual 548	java/io/FileOutputStream:close	()V
    //   69: iconst_1
    //   70: istore_2
    //   71: iload_2
    //   72: ireturn
    //   73: astore_1
    //   74: aconst_null
    //   75: astore_0
    //   76: aload 4
    //   78: astore_3
    //   79: aload_1
    //   80: invokestatic 169	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   83: iconst_0
    //   84: istore_2
    //   85: aload_0
    //   86: ifnull +7 -> 93
    //   89: aload_0
    //   90: invokevirtual 547	java/io/FileInputStream:close	()V
    //   93: aload_3
    //   94: ifnull -23 -> 71
    //   97: aload_3
    //   98: invokevirtual 548	java/io/FileOutputStream:close	()V
    //   101: iconst_0
    //   102: ireturn
    //   103: astore_0
    //   104: aconst_null
    //   105: astore_1
    //   106: aconst_null
    //   107: astore_3
    //   108: aload_3
    //   109: ifnull +7 -> 116
    //   112: aload_3
    //   113: invokevirtual 547	java/io/FileInputStream:close	()V
    //   116: aload_1
    //   117: ifnull +7 -> 124
    //   120: aload_1
    //   121: invokevirtual 548	java/io/FileOutputStream:close	()V
    //   124: aload_0
    //   125: athrow
    //   126: astore_0
    //   127: aconst_null
    //   128: astore_1
    //   129: goto -21 -> 108
    //   132: astore_0
    //   133: goto -25 -> 108
    //   136: astore 4
    //   138: aload_3
    //   139: astore_1
    //   140: aload_0
    //   141: astore_3
    //   142: aload 4
    //   144: astore_0
    //   145: goto -37 -> 108
    //   148: astore_1
    //   149: aload_3
    //   150: astore_0
    //   151: aload 4
    //   153: astore_3
    //   154: goto -75 -> 79
    //   157: astore 5
    //   159: aload_1
    //   160: astore_0
    //   161: aload_3
    //   162: astore 4
    //   164: aload 5
    //   166: astore_1
    //   167: aload_0
    //   168: astore_3
    //   169: aload 4
    //   171: astore_0
    //   172: goto -93 -> 79
    //
    // Exception table:
    //   from	to	target	type
    //   15	24	73	java/lang/Exception
    //   15	24	103	finally
    //   24	33	126	finally
    //   33	53	132	finally
    //   79	83	136	finally
    //   24	33	148	java/lang/Exception
    //   33	53	157	java/lang/Exception
  }

  public static boolean copyFile(InputStream paramInputStream, File paramFile)
  {
    paramFile = new FileOutputStream(paramFile);
    byte[] arrayOfByte = new byte[4096];
    while (true)
    {
      int i = paramInputStream.read(arrayOfByte);
      if (i <= 0)
        break;
      Thread.yield();
      paramFile.write(arrayOfByte, 0, i);
    }
    paramFile.close();
    return true;
  }

  private static Intent createShortcutIntent(long paramLong, boolean paramBoolean)
  {
    Canvas localCanvas = null;
    Object localObject6 = null;
    Intent localIntent1 = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
    int i = (int)paramLong;
    int j = (int)(paramLong >> 32);
    Object localObject1;
    TLRPC.User localUser;
    label87: Object localObject4;
    Object localObject5;
    if (i == 0)
    {
      localIntent1.putExtra("encId", j);
      localObject1 = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j));
      if (localObject1 == null);
      do
      {
        return null;
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.EncryptedChat)localObject1).user_id));
        localObject1 = null;
      }
      while ((localUser == null) && (localObject1 == null));
      if (localUser == null)
        break label686;
      localObject2 = ContactsController.formatName(localUser.first_name, localUser.last_name);
      localObject4 = localObject2;
      if (localUser.photo == null)
        break label862;
      localObject5 = localUser.photo.photo_small;
      localObject4 = localObject2;
    }
    label147: label686: Object localObject3;
    label862: for (Object localObject2 = localObject5; ; localObject3 = null)
      while (true)
      {
        localIntent1.setAction("com.tmessages.openchat" + paramLong);
        localIntent1.addFlags(67108864);
        Intent localIntent2 = new Intent();
        localIntent2.putExtra("android.intent.extra.shortcut.INTENT", localIntent1);
        localIntent2.putExtra("android.intent.extra.shortcut.NAME", (String)localObject4);
        localIntent2.putExtra("duplicate", false);
        if (!paramBoolean)
        {
          localObject4 = localObject6;
          if (localObject2 != null)
            localObject4 = localCanvas;
        }
        try
        {
          localObject2 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject2, true).toString());
          if (localObject2 != null)
          {
            localObject4 = localObject2;
            int k = dp(58.0F);
            localObject4 = localObject2;
            localObject5 = Bitmap.createBitmap(k, k, Bitmap.Config.ARGB_8888);
            localObject4 = localObject2;
            ((Bitmap)localObject5).eraseColor(0);
            localObject4 = localObject2;
            localCanvas = new Canvas((Bitmap)localObject5);
            localObject4 = localObject2;
            localObject6 = new BitmapShader((Bitmap)localObject2, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            localObject4 = localObject2;
            if (roundPaint == null)
            {
              localObject4 = localObject2;
              roundPaint = new Paint(1);
              localObject4 = localObject2;
              bitmapRect = new RectF();
            }
            localObject4 = localObject2;
            float f = k / ((Bitmap)localObject2).getWidth();
            localObject4 = localObject2;
            localCanvas.save();
            localObject4 = localObject2;
            localCanvas.scale(f, f);
            localObject4 = localObject2;
            roundPaint.setShader((Shader)localObject6);
            localObject4 = localObject2;
            bitmapRect.set(0.0F, 0.0F, ((Bitmap)localObject2).getWidth(), ((Bitmap)localObject2).getHeight());
            localObject4 = localObject2;
            localCanvas.drawRoundRect(bitmapRect, ((Bitmap)localObject2).getWidth(), ((Bitmap)localObject2).getHeight(), roundPaint);
            localObject4 = localObject2;
            localCanvas.restore();
            localObject4 = localObject2;
            localObject6 = ApplicationLoader.applicationContext.getResources().getDrawable(2130837641);
            localObject4 = localObject2;
            i = dp(15.0F);
            localObject4 = localObject2;
            j = k - i - dp(2.0F);
            localObject4 = localObject2;
            k = k - i - dp(2.0F);
            localObject4 = localObject2;
            ((Drawable)localObject6).setBounds(j, k, j + i, i + k);
            localObject4 = localObject2;
            ((Drawable)localObject6).draw(localCanvas);
            localObject4 = localObject2;
          }
        }
        catch (Throwable localObject3)
        {
          while (true)
            try
            {
              localCanvas.setBitmap(null);
              localObject2 = localObject5;
              localObject4 = localObject2;
              if (localObject4 == null)
                continue;
              localIntent2.putExtra("android.intent.extra.shortcut.ICON", (Parcelable)localObject4);
              return localIntent2;
              if (i <= 0)
                continue;
              localIntent1.putExtra("userId", i);
              localUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
              localObject1 = null;
              break label87;
              if (i >= 0)
                break;
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
              localIntent1.putExtra("chatId", -i);
              localUser = null;
              break label87;
              localObject5 = ((TLRPC.Chat)localObject1).title;
              localObject4 = localObject5;
              if (((TLRPC.Chat)localObject1).photo == null)
                break label862;
              localObject2 = ((TLRPC.Chat)localObject1).photo.photo_small;
              localObject4 = localObject5;
              break label147;
              localThrowable = localThrowable;
              FileLog.e(localThrowable);
              continue;
              if (localUser == null)
                continue;
              if (!localUser.bot)
                continue;
              localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, 2130837638));
              continue;
              localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, 2130837642));
              continue;
              if (localObject1 == null)
                continue;
              if ((!ChatObject.isChannel((TLRPC.Chat)localObject1)) || (((TLRPC.Chat)localObject1).megagroup))
                continue;
              localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, 2130837639));
              continue;
              localIntent2.putExtra("android.intent.extra.shortcut.ICON_RESOURCE", Intent.ShortcutIconResource.fromContext(ApplicationLoader.applicationContext, 2130837640));
              continue;
            }
            catch (Exception localObject3)
            {
              localObject3 = localObject5;
              continue;
            }
        }
      }
  }

  public static byte[] decodeQuotedPrintable(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      return null;
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    int i = 0;
    if (i < paramArrayOfByte.length)
    {
      int j = paramArrayOfByte[i];
      if (j == 61)
        i += 1;
      while (true)
      {
        try
        {
          j = Character.digit((char)paramArrayOfByte[i], 16);
          i += 1;
          localByteArrayOutputStream.write((char)((j << 4) + Character.digit((char)paramArrayOfByte[i], 16)));
          i += 1;
        }
        catch (Exception paramArrayOfByte)
        {
          FileLog.e(paramArrayOfByte);
          return null;
        }
        localByteArrayOutputStream.write(j);
      }
    }
    paramArrayOfByte = localByteArrayOutputStream.toByteArray();
    try
    {
      localByteArrayOutputStream.close();
      return paramArrayOfByte;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return paramArrayOfByte;
  }

  public static int dp(float paramFloat)
  {
    if (paramFloat == 0.0F)
      return 0;
    return (int)Math.ceil(density * paramFloat);
  }

  public static int dp2(float paramFloat)
  {
    if (paramFloat == 0.0F)
      return 0;
    return (int)Math.floor(density * paramFloat);
  }

  public static float dpf2(float paramFloat)
  {
    if (paramFloat == 0.0F)
      return 0.0F;
    return density * paramFloat;
  }

  public static void endIncomingCall()
  {
    if (!hasCallPermissions)
      return;
    try
    {
      Object localObject = (TelephonyManager)ApplicationLoader.applicationContext.getSystemService("phone");
      Method localMethod = Class.forName(localObject.getClass().getName()).getDeclaredMethod("getITelephony", new Class[0]);
      localMethod.setAccessible(true);
      a locala = (a)localMethod.invoke(localObject, new Object[0]);
      localObject = (a)localMethod.invoke(localObject, new Object[0]);
      ((a)localObject).b();
      ((a)localObject).a();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("tmessages", localException);
    }
  }

  public static String formatFileSize(long paramLong)
  {
    if (paramLong < 1024L)
      return String.format("%d B", new Object[] { Long.valueOf(paramLong) });
    if (paramLong < 1048576L)
      return String.format("%.1f KB", new Object[] { Float.valueOf((float)paramLong / 1024.0F) });
    if (paramLong < 1073741824L)
      return String.format("%.1f MB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F) });
    return String.format("%.1f GB", new Object[] { Float.valueOf((float)paramLong / 1024.0F / 1024.0F / 1024.0F) });
  }

  public static File generatePicturePath()
  {
    try
    {
      File localFile = getAlbumDir();
      String str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
      localFile = new File(localFile, "IMG_" + str + ".jpg");
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return null;
  }

  public static CharSequence generateSearchName(String paramString1, String paramString2, String paramString3)
  {
    if ((paramString1 == null) && (paramString2 == null))
      return "";
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    String str;
    int i;
    label67: int j;
    label108: int k;
    if ((paramString1 == null) || (paramString1.length() == 0))
    {
      str = paramString2;
      paramString1 = str.trim();
      paramString2 = " " + paramString1.toLowerCase();
      i = 0;
      int m = paramString2.indexOf(" " + paramString3, i);
      if (m == -1)
        break label339;
      if (m != 0)
        break label302;
      j = 0;
      k = m - j;
      int n = paramString3.length();
      if (m != 0)
        break label308;
      j = 0;
      label129: j = j + n + k;
      if ((i == 0) || (i == k + 1))
        break label314;
      localSpannableStringBuilder.append(paramString1.substring(i, k));
    }
    while (true)
    {
      str = paramString1.substring(k, Math.min(paramString1.length(), j));
      if (str.startsWith(" "))
        localSpannableStringBuilder.append(" ");
      str = str.trim();
      i = localSpannableStringBuilder.length();
      localSpannableStringBuilder.append(str);
      localSpannableStringBuilder.setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), i, str.length() + i, 33);
      i = j;
      break label67;
      str = paramString1;
      if (paramString2 == null)
        break;
      str = paramString1;
      if (paramString2.length() == 0)
        break;
      str = paramString1 + " " + paramString2;
      break;
      label302: j = 1;
      break label108;
      label308: j = 1;
      break label129;
      label314: if ((i != 0) || (k == 0))
        continue;
      localSpannableStringBuilder.append(paramString1.substring(0, k));
    }
    label339: if ((i != -1) && (i != paramString1.length()))
      localSpannableStringBuilder.append(paramString1.substring(i, paramString1.length()));
    return localSpannableStringBuilder;
  }

  public static File generateVideoPath()
  {
    try
    {
      File localFile = getAlbumDir();
      String str = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
      localFile = new File(localFile, "VID_" + str + ".mp4");
      return localFile;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return null;
  }

  private static File getAlbumDir()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (ApplicationLoader.applicationContext.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0))
      return FileLoader.getInstance().getDirectory(4);
    File localFile1;
    if ("mounted".equals(Environment.getExternalStorageState()))
    {
      File localFile2 = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Telegram");
      localFile1 = localFile2;
      if (!localFile2.mkdirs())
      {
        localFile1 = localFile2;
        if (!localFile2.exists())
        {
          FileLog.d("failed to create directory");
          return null;
        }
      }
    }
    else
    {
      FileLog.d("External storage is not mounted READ/WRITE.");
      localFile1 = null;
    }
    return localFile1;
  }

  public static File getCacheDir()
  {
    Object localObject1 = null;
    while (true)
    {
      try
      {
        String str = Environment.getExternalStorageState();
        localObject1 = str;
        if (localObject1 == null)
          continue;
        if (!((String)localObject1).startsWith("mounted"));
      }
      catch (Exception localException3)
      {
        try
        {
          localObject1 = ApplicationLoader.applicationContext.getExternalCacheDir();
          if (localObject1 != null)
          {
            return localObject1;
            localException3 = localException3;
            FileLog.e(localException3);
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
      }
      try
      {
        File localFile = ApplicationLoader.applicationContext.getCacheDir();
        Object localObject2 = localFile;
        if (localFile != null)
          continue;
        return new File("");
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  // ERROR //
  public static String getDataColumn(Context paramContext, Uri paramUri, String paramString, String[] paramArrayOfString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   4: aload_1
    //   5: iconst_1
    //   6: anewarray 435	java/lang/String
    //   9: dup
    //   10: iconst_0
    //   11: ldc_w 1033
    //   14: aastore
    //   15: aload_2
    //   16: aload_3
    //   17: aconst_null
    //   18: invokevirtual 1039	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   21: astore_1
    //   22: aload_1
    //   23: ifnull +100 -> 123
    //   26: aload_1
    //   27: astore_0
    //   28: aload_1
    //   29: invokeinterface 1044 1 0
    //   34: ifeq +89 -> 123
    //   37: aload_1
    //   38: astore_0
    //   39: aload_1
    //   40: aload_1
    //   41: ldc_w 1033
    //   44: invokeinterface 1047 2 0
    //   49: invokeinterface 1051 2 0
    //   54: astore_2
    //   55: aload_1
    //   56: astore_0
    //   57: aload_2
    //   58: ldc_w 1053
    //   61: invokevirtual 961	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   64: ifne +31 -> 95
    //   67: aload_1
    //   68: astore_0
    //   69: aload_2
    //   70: ldc_w 1055
    //   73: invokevirtual 961	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   76: ifne +33 -> 109
    //   79: aload_1
    //   80: astore_0
    //   81: aload_2
    //   82: ldc_w 1057
    //   85: invokevirtual 961	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   88: istore 4
    //   90: iload 4
    //   92: ifne +17 -> 109
    //   95: aload_1
    //   96: ifnull +9 -> 105
    //   99: aload_1
    //   100: invokeinterface 1058 1 0
    //   105: aconst_null
    //   106: astore_0
    //   107: aload_0
    //   108: areturn
    //   109: aload_2
    //   110: astore_0
    //   111: aload_1
    //   112: ifnull -5 -> 107
    //   115: aload_1
    //   116: invokeinterface 1058 1 0
    //   121: aload_2
    //   122: areturn
    //   123: aload_1
    //   124: ifnull +9 -> 133
    //   127: aload_1
    //   128: invokeinterface 1058 1 0
    //   133: aconst_null
    //   134: areturn
    //   135: astore_2
    //   136: aconst_null
    //   137: astore_1
    //   138: aload_1
    //   139: astore_0
    //   140: aload_2
    //   141: invokestatic 169	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   144: aload_1
    //   145: ifnull -12 -> 133
    //   148: aload_1
    //   149: invokeinterface 1058 1 0
    //   154: goto -21 -> 133
    //   157: astore_1
    //   158: aconst_null
    //   159: astore_0
    //   160: aload_0
    //   161: ifnull +9 -> 170
    //   164: aload_0
    //   165: invokeinterface 1058 1 0
    //   170: aload_1
    //   171: athrow
    //   172: astore_1
    //   173: goto -13 -> 160
    //   176: astore_2
    //   177: goto -39 -> 138
    //
    // Exception table:
    //   from	to	target	type
    //   0	22	135	java/lang/Exception
    //   0	22	157	finally
    //   28	37	172	finally
    //   39	55	172	finally
    //   57	67	172	finally
    //   69	79	172	finally
    //   81	90	172	finally
    //   140	144	172	finally
    //   28	37	176	java/lang/Exception
    //   39	55	176	java/lang/Exception
    //   57	67	176	java/lang/Exception
    //   69	79	176	java/lang/Exception
    //   81	90	176	java/lang/Exception
  }

  public static int getMinTabletSide()
  {
    if (!isSmallTablet())
    {
      k = Math.min(displaySize.x, displaySize.y);
      j = k * 35 / 100;
      i = j;
      if (j < dp(320.0F))
        i = dp(320.0F);
      return k - i;
    }
    int k = Math.min(displaySize.x, displaySize.y);
    int m = Math.max(displaySize.x, displaySize.y);
    int j = m * 35 / 100;
    int i = j;
    if (j < dp(320.0F))
      i = dp(320.0F);
    return Math.min(k, m - i);
  }

  public static int getMyLayerVersion(int paramInt)
  {
    return 0xFFFF & paramInt;
  }

  @SuppressLint({"NewApi"})
  public static String getPath(Uri paramUri)
  {
    int j = 0;
    while (true)
    {
      try
      {
        if (Build.VERSION.SDK_INT < 19)
          break label323;
        i = 1;
        if ((i == 0) || (!DocumentsContract.isDocumentUri(ApplicationLoader.applicationContext, paramUri)))
          continue;
        if (!isExternalStorageDocument(paramUri))
          continue;
        paramUri = DocumentsContract.getDocumentId(paramUri).split(":");
        if ("primary".equalsIgnoreCase(paramUri[0]))
        {
          return Environment.getExternalStorageDirectory() + "/" + paramUri[1];
          if (!isDownloadsDocument(paramUri))
            continue;
          paramUri = DocumentsContract.getDocumentId(paramUri);
          paramUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(paramUri).longValue());
          return getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
          if (isMediaDocument(paramUri))
          {
            Object localObject = DocumentsContract.getDocumentId(paramUri).split(":");
            paramUri = localObject[0];
            switch (paramUri.hashCode())
            {
            case 100313435:
              localObject = localObject[1];
              return getDataColumn(ApplicationLoader.applicationContext, paramUri, "_id=?", new String[] { localObject });
              if (!paramUri.equals("image"))
                break;
              i = j;
              break;
            case 112202875:
              if (!paramUri.equals("video"))
                break;
              i = 1;
              break;
            case 93166550:
              if (!paramUri.equals("audio"))
                break;
              i = 2;
              break label330;
              paramUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
              continue;
              paramUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
              continue;
              paramUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
              continue;
              if ("content".equalsIgnoreCase(paramUri.getScheme()))
                return getDataColumn(ApplicationLoader.applicationContext, paramUri, null, null);
              if ("file".equalsIgnoreCase(paramUri.getScheme()))
              {
                paramUri = paramUri.getPath();
                return paramUri;
              }
            }
          }
        }
      }
      catch (Exception paramUri)
      {
        FileLog.e(paramUri);
      }
      return null;
      label323: int i = 0;
      continue;
      i = -1;
      label330: switch (i)
      {
      case 0:
      case 1:
      case 2:
      }
      paramUri = null;
    }
  }

  public static int getPeerLayerVersion(int paramInt)
  {
    return paramInt >> 16 & 0xFFFF;
  }

  public static int getPhotoSize()
  {
    if (photoSize == null)
    {
      if (Build.VERSION.SDK_INT < 16)
        break label30;
      photoSize = Integer.valueOf(1280);
    }
    while (true)
    {
      return photoSize.intValue();
      label30: photoSize = Integer.valueOf(800);
    }
  }

  public static float getPixelsInCM(float paramFloat, boolean paramBoolean)
  {
    float f = paramFloat / 2.54F;
    if (paramBoolean)
      paramFloat = displayMetrics.xdpi;
    while (true)
    {
      return paramFloat * f;
      paramFloat = displayMetrics.ydpi;
    }
  }

  public static Point getRealScreenSize()
  {
    Point localPoint = new Point();
    try
    {
      WindowManager localWindowManager = (WindowManager)ApplicationLoader.applicationContext.getSystemService("window");
      if (Build.VERSION.SDK_INT >= 17)
      {
        localWindowManager.getDefaultDisplay().getRealSize(localPoint);
        return localPoint;
      }
      try
      {
        Method localMethod1 = Display.class.getMethod("getRawWidth", new Class[0]);
        Method localMethod2 = Display.class.getMethod("getRawHeight", new Class[0]);
        localPoint.set(((Integer)localMethod1.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue(), ((Integer)localMethod2.invoke(localWindowManager.getDefaultDisplay(), new Object[0])).intValue());
        return localPoint;
      }
      catch (Exception localException2)
      {
        localPoint.set(localWindowManager.getDefaultDisplay().getWidth(), localWindowManager.getDefaultDisplay().getHeight());
        FileLog.e(localException2);
        return localPoint;
      }
    }
    catch (Exception localException1)
    {
      FileLog.e(localException1);
    }
    return localPoint;
  }

  public static CharSequence getTrimmedString(CharSequence paramCharSequence)
  {
    CharSequence localCharSequence = paramCharSequence;
    if (paramCharSequence != null)
    {
      localCharSequence = paramCharSequence;
      if (paramCharSequence.length() == 0)
        localCharSequence = paramCharSequence;
    }
    else
    {
      return localCharSequence;
    }
    while (true)
    {
      paramCharSequence = localCharSequence;
      if (localCharSequence.length() <= 0)
        break;
      if (localCharSequence.charAt(0) != '\n')
      {
        paramCharSequence = localCharSequence;
        if (localCharSequence.charAt(0) != ' ')
          break;
      }
      localCharSequence = localCharSequence.subSequence(1, localCharSequence.length());
    }
    while (true)
    {
      localCharSequence = paramCharSequence;
      if (paramCharSequence.length() <= 0)
        break;
      if (paramCharSequence.charAt(paramCharSequence.length() - 1) != '\n')
      {
        localCharSequence = paramCharSequence;
        if (paramCharSequence.charAt(paramCharSequence.length() - 1) != ' ')
          break;
      }
      paramCharSequence = paramCharSequence.subSequence(0, paramCharSequence.length() - 1);
    }
  }

  public static Typeface getTypeface(String paramString)
  {
    synchronized (typefaceCache)
    {
      boolean bool = typefaceCache.containsKey(paramString);
      if (!bool);
      try
      {
        Typeface localTypeface = Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), paramString);
        typefaceCache.put(paramString, localTypeface);
        paramString = (Typeface)typefaceCache.get(paramString);
        return paramString;
      }
      catch (Exception localException)
      {
        FileLog.e("Could not get typeface '" + paramString + "' because " + localException.getMessage());
        return null;
      }
    }
  }

  public static int getViewInset(View paramView)
  {
    if ((paramView == null) || (Build.VERSION.SDK_INT < 21) || (paramView.getHeight() == displaySize.y) || (paramView.getHeight() == displaySize.y - statusBarHeight))
      return 0;
    try
    {
      if (mAttachInfoField == null)
      {
        mAttachInfoField = View.class.getDeclaredField("mAttachInfo");
        mAttachInfoField.setAccessible(true);
      }
      paramView = mAttachInfoField.get(paramView);
      if (paramView != null)
      {
        if (mStableInsetsField == null)
        {
          mStableInsetsField = paramView.getClass().getDeclaredField("mStableInsets");
          mStableInsetsField.setAccessible(true);
        }
        int i = ((Rect)mStableInsetsField.get(paramView)).bottom;
        return i;
      }
    }
    catch (Exception paramView)
    {
      FileLog.e(paramView);
    }
    return 0;
  }

  public static void hideKeyboard(View paramView)
  {
    if (paramView == null);
    while (true)
    {
      return;
      try
      {
        InputMethodManager localInputMethodManager = (InputMethodManager)paramView.getContext().getSystemService("input_method");
        if (!localInputMethodManager.isActive())
          continue;
        localInputMethodManager.hideSoftInputFromWindow(paramView.getWindowToken(), 0);
        return;
      }
      catch (Exception paramView)
      {
        FileLog.e(paramView);
      }
    }
  }

  private static int[] hsvToRgb(double paramDouble1, double paramDouble2, double paramDouble3)
  {
    double d2 = (int)Math.floor(6.0D * paramDouble1);
    double d3 = 6.0D * paramDouble1 - d2;
    paramDouble1 = (1.0D - paramDouble2) * paramDouble3;
    double d1 = (1.0D - d3 * paramDouble2) * paramDouble3;
    paramDouble2 = (1.0D - (1.0D - d3) * paramDouble2) * paramDouble3;
    switch ((int)d2 % 6)
    {
    default:
      paramDouble3 = 0.0D;
      paramDouble2 = 0.0D;
      paramDouble1 = 0.0D;
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    }
    while (true)
    {
      return new int[] { (int)(paramDouble1 * 255.0D), (int)(paramDouble2 * 255.0D), (int)(255.0D * paramDouble3) };
      d1 = paramDouble1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d1;
      continue;
      paramDouble2 = paramDouble3;
      paramDouble3 = paramDouble1;
      paramDouble1 = d1;
      continue;
      d1 = paramDouble3;
      paramDouble3 = paramDouble2;
      paramDouble2 = d1;
      continue;
      paramDouble2 = d1;
      continue;
      d1 = paramDouble2;
      paramDouble2 = paramDouble1;
      paramDouble1 = d1;
      continue;
      paramDouble2 = paramDouble1;
      paramDouble1 = paramDouble3;
      paramDouble3 = d1;
    }
  }

  public static void installShortcut(long paramLong)
  {
    try
    {
      Intent localIntent = createShortcutIntent(paramLong, false);
      localIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static boolean isDownloadsDocument(Uri paramUri)
  {
    return "com.android.providers.downloads.documents".equals(paramUri.getAuthority());
  }

  public static boolean isExternalStorageDocument(Uri paramUri)
  {
    return "com.android.externalstorage.documents".equals(paramUri.getAuthority());
  }

  public static boolean isGoogleMapsInstalled(BaseFragment paramBaseFragment)
  {
    int i = 0;
    try
    {
      ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
      i = 1;
      return i;
    }
    catch (PackageManager.NameNotFoundException localBuilder)
    {
      while (paramBaseFragment.getParentActivity() == null);
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(paramBaseFragment.getParentActivity());
      localBuilder.setMessage("Install Google Maps?");
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramBaseFragment)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          try
          {
            paramDialogInterface = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.google.android.apps.maps"));
            this.val$fragment.getParentActivity().startActivityForResult(paramDialogInterface, 500);
            return;
          }
          catch (Exception paramDialogInterface)
          {
            FileLog.e(paramDialogInterface);
          }
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
      paramBaseFragment.showDialog(localBuilder.create());
    }
    return false;
  }

  public static boolean isInternalUri(Uri paramUri)
  {
    Object localObject = paramUri.getPath();
    paramUri = (Uri)localObject;
    if (localObject == null)
      return false;
    do
    {
      paramUri = (Uri)localObject;
      localObject = Utilities.readlink(paramUri);
    }
    while ((localObject != null) && (!((String)localObject).equals(paramUri)));
    localObject = paramUri;
    if (paramUri != null);
    try
    {
      String str = new File(paramUri).getCanonicalPath();
      localObject = paramUri;
      if (str != null)
        localObject = str;
      if ((localObject != null) && (((String)localObject).toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName() + "/files")))
      {
        i = 1;
        return i;
      }
    }
    catch (Exception localUri)
    {
      while (true)
      {
        paramUri.replace("/./", "/");
        Uri localUri = paramUri;
        continue;
        int i = 0;
      }
    }
  }

  public static boolean isKeyboardShowed(View paramView)
  {
    if (paramView == null)
      return false;
    try
    {
      boolean bool = ((InputMethodManager)paramView.getContext().getSystemService("input_method")).isActive(paramView);
      return bool;
    }
    catch (Exception paramView)
    {
      FileLog.e(paramView);
    }
    return false;
  }

  public static boolean isMediaDocument(Uri paramUri)
  {
    return "com.android.providers.media.documents".equals(paramUri.getAuthority());
  }

  public static boolean isSmallTablet()
  {
    return Math.min(displaySize.x, displaySize.y) / density <= 700.0F;
  }

  public static boolean isTablet()
  {
    if (isTablet == null)
      isTablet = Boolean.valueOf(ApplicationLoader.applicationContext.getResources().getBoolean(2131427329));
    return isTablet.booleanValue();
  }

  public static boolean isWaitingForCall()
  {
    synchronized (callLock)
    {
      boolean bool = waitingForCall;
      return bool;
    }
  }

  public static boolean isWaitingForSms()
  {
    synchronized (smsLock)
    {
      boolean bool = waitingForSms;
      return bool;
    }
  }

  public static void lockOrientation(Activity paramActivity)
  {
    if ((paramActivity == null) || (prevOrientation != -10));
    int i;
    int j;
    while (true)
    {
      return;
      try
      {
        prevOrientation = paramActivity.getRequestedOrientation();
        WindowManager localWindowManager = (WindowManager)paramActivity.getSystemService("window");
        if ((localWindowManager == null) || (localWindowManager.getDefaultDisplay() == null))
          continue;
        i = localWindowManager.getDefaultDisplay().getRotation();
        j = paramActivity.getResources().getConfiguration().orientation;
        if (i != 3)
          break label94;
        if (j != 1)
          break;
        paramActivity.setRequestedOrientation(1);
        return;
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
        return;
      }
    }
    paramActivity.setRequestedOrientation(8);
    return;
    label94: if (i == 1)
    {
      if (j == 1)
      {
        paramActivity.setRequestedOrientation(9);
        return;
      }
      paramActivity.setRequestedOrientation(0);
      return;
    }
    if (i == 0)
    {
      if (j == 2)
      {
        paramActivity.setRequestedOrientation(0);
        return;
      }
      paramActivity.setRequestedOrientation(1);
      return;
    }
    if (j == 2)
    {
      paramActivity.setRequestedOrientation(8);
      return;
    }
    paramActivity.setRequestedOrientation(9);
  }

  public static long makeBroadcastId(int paramInt)
  {
    return 0x0 | paramInt & 0xFFFFFFFF;
  }

  public static boolean needShowPasscode(boolean paramBoolean)
  {
    boolean bool = ForegroundDetector.getInstance().isWasInBackground(paramBoolean);
    if (paramBoolean)
      ForegroundDetector.getInstance().resetBackgroundVar();
    return (UserConfig.passcodeHash.length() > 0) && (bool) && ((UserConfig.appLocked) || ((UserConfig.autoLockIn != 0) && (UserConfig.lastPauseTime != 0) && (!UserConfig.appLocked) && (UserConfig.lastPauseTime + UserConfig.autoLockIn <= ConnectionsManager.getInstance().getCurrentTime())));
  }

  // ERROR //
  public static String obtainLoginPhoneCall(String paramString)
  {
    // Byte code:
    //   0: getstatic 163	org/vidogram/messenger/AndroidUtilities:hasCallPermissions	Z
    //   3: ifne +7 -> 10
    //   6: aconst_null
    //   7: astore_0
    //   8: aload_0
    //   9: areturn
    //   10: getstatic 152	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   13: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   16: getstatic 1443	android/provider/CallLog$Calls:CONTENT_URI	Landroid/net/Uri;
    //   19: iconst_2
    //   20: anewarray 435	java/lang/String
    //   23: dup
    //   24: iconst_0
    //   25: ldc_w 1445
    //   28: aastore
    //   29: dup
    //   30: iconst_1
    //   31: ldc_w 1447
    //   34: aastore
    //   35: ldc_w 1449
    //   38: aconst_null
    //   39: ldc_w 1451
    //   42: invokevirtual 1039	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   45: astore 5
    //   47: aload 5
    //   49: astore 4
    //   51: aload 5
    //   53: invokeinterface 1454 1 0
    //   58: ifeq +110 -> 168
    //   61: aload 5
    //   63: astore 4
    //   65: aload 5
    //   67: iconst_0
    //   68: invokeinterface 1051 2 0
    //   73: astore 6
    //   75: aload 5
    //   77: astore 4
    //   79: aload 5
    //   81: iconst_1
    //   82: invokeinterface 1457 2 0
    //   87: lstore_1
    //   88: aload 5
    //   90: astore 4
    //   92: new 117	java/lang/StringBuilder
    //   95: dup
    //   96: invokespecial 118	java/lang/StringBuilder:<init>	()V
    //   99: ldc_w 1459
    //   102: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   105: aload 6
    //   107: invokevirtual 124	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   110: invokevirtual 133	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   113: invokestatic 396	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   116: aload 5
    //   118: astore 4
    //   120: invokestatic 1462	java/lang/System:currentTimeMillis	()J
    //   123: lload_1
    //   124: lsub
    //   125: invokestatic 1465	java/lang/Math:abs	(J)J
    //   128: ldc2_w 1466
    //   131: lcmp
    //   132: ifge -85 -> 47
    //   135: aload 5
    //   137: astore 4
    //   139: aload_0
    //   140: aload 6
    //   142: invokestatic 1469	org/vidogram/messenger/AndroidUtilities:checkPhonePattern	(Ljava/lang/String;Ljava/lang/String;)Z
    //   145: istore_3
    //   146: iload_3
    //   147: ifeq -100 -> 47
    //   150: aload 6
    //   152: astore_0
    //   153: aload 5
    //   155: ifnull -147 -> 8
    //   158: aload 5
    //   160: invokeinterface 1058 1 0
    //   165: aload 6
    //   167: areturn
    //   168: aload 5
    //   170: ifnull +10 -> 180
    //   173: aload 5
    //   175: invokeinterface 1058 1 0
    //   180: aconst_null
    //   181: areturn
    //   182: astore_0
    //   183: aconst_null
    //   184: astore 5
    //   186: aload 5
    //   188: astore 4
    //   190: aload_0
    //   191: invokestatic 169	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   194: aload 5
    //   196: ifnull -16 -> 180
    //   199: aload 5
    //   201: invokeinterface 1058 1 0
    //   206: goto -26 -> 180
    //   209: astore_0
    //   210: aconst_null
    //   211: astore 4
    //   213: aload 4
    //   215: ifnull +10 -> 225
    //   218: aload 4
    //   220: invokeinterface 1058 1 0
    //   225: aload_0
    //   226: athrow
    //   227: astore_0
    //   228: goto -15 -> 213
    //   231: astore_0
    //   232: goto -46 -> 186
    //
    // Exception table:
    //   from	to	target	type
    //   10	47	182	java/lang/Exception
    //   10	47	209	finally
    //   51	61	227	finally
    //   65	75	227	finally
    //   79	88	227	finally
    //   92	116	227	finally
    //   120	135	227	finally
    //   139	146	227	finally
    //   190	194	227	finally
    //   51	61	231	java/lang/Exception
    //   65	75	231	java/lang/Exception
    //   79	88	231	java/lang/Exception
    //   92	116	231	java/lang/Exception
    //   120	135	231	java/lang/Exception
    //   139	146	231	java/lang/Exception
  }

  public static void openForView(MessageObject paramMessageObject, Activity paramActivity)
  {
    Object localObject2 = paramMessageObject.getFileName();
    if ((paramMessageObject.messageOwner.attachPath != null) && (paramMessageObject.messageOwner.attachPath.length() != 0));
    for (Object localObject1 = new File(paramMessageObject.messageOwner.attachPath); ; localObject1 = null)
    {
      if ((localObject1 == null) || (!((File)localObject1).exists()));
      label305: for (Object localObject3 = FileLoader.getPathToMessage(paramMessageObject.messageOwner); ; localObject3 = localObject1)
      {
        Intent localIntent;
        if ((localObject3 != null) && (((File)localObject3).exists()))
        {
          localIntent = new Intent("android.intent.action.VIEW");
          localIntent.setFlags(1);
          localObject1 = MimeTypeMap.getSingleton();
          int i = ((String)localObject2).lastIndexOf('.');
          if (i == -1)
            break label332;
          localObject2 = ((MimeTypeMap)localObject1).getMimeTypeFromExtension(((String)localObject2).substring(i + 1).toLowerCase());
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            if ((paramMessageObject.type == 9) || (paramMessageObject.type == 0))
              localObject2 = paramMessageObject.getDocument().mime_type;
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0)
                break label181;
            }
          }
        }
        label181: label322: label332: for (localObject1 = null; ; localObject1 = null)
        {
          if (Build.VERSION.SDK_INT >= 24)
          {
            localObject2 = FileProvider.a(paramActivity, "org.vidogram.messenger.provider", (File)localObject3);
            if (localObject1 != null)
              paramMessageObject = (MessageObject)localObject1;
          }
          while (true)
          {
            localIntent.setDataAndType((Uri)localObject2, paramMessageObject);
            if (localObject1 == null)
              break label322;
            try
            {
              paramActivity.startActivityForResult(localIntent, 500);
              return;
              paramMessageObject = "text/plain";
              continue;
              localObject2 = Uri.fromFile((File)localObject3);
              if (localObject1 != null);
              for (paramMessageObject = (MessageObject)localObject1; ; paramMessageObject = "text/plain")
              {
                localIntent.setDataAndType((Uri)localObject2, paramMessageObject);
                break;
              }
            }
            catch (Exception paramMessageObject)
            {
              if (Build.VERSION.SDK_INT < 24)
                break label305;
            }
          }
          localIntent.setDataAndType(FileProvider.a(paramActivity, "org.vidogram.messenger.provider", (File)localObject3), "text/plain");
          while (true)
          {
            paramActivity.startActivityForResult(localIntent, 500);
            return;
            localIntent.setDataAndType(Uri.fromFile((File)localObject3), "text/plain");
          }
          paramActivity.startActivityForResult(localIntent, 500);
          return;
        }
      }
    }
  }

  public static void openForView(TLObject paramTLObject, Activity paramActivity)
  {
    if ((paramTLObject == null) || (paramActivity == null));
    File localFile;
    do
    {
      return;
      localObject1 = FileLoader.getAttachFileName(paramTLObject);
      localFile = FileLoader.getPathToAttach(paramTLObject, true);
    }
    while ((localFile == null) || (!localFile.exists()));
    Intent localIntent = new Intent("android.intent.action.VIEW");
    localIntent.setFlags(1);
    Object localObject2 = MimeTypeMap.getSingleton();
    int i = ((String)localObject1).lastIndexOf('.');
    if (i != -1)
    {
      localObject2 = ((MimeTypeMap)localObject2).getMimeTypeFromExtension(((String)localObject1).substring(i + 1).toLowerCase());
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        if ((paramTLObject instanceof TLRPC.TL_document))
          localObject2 = ((TLRPC.TL_document)paramTLObject).mime_type;
        if (localObject2 != null)
        {
          localObject1 = localObject2;
          if (((String)localObject2).length() != 0)
            break label129;
        }
      }
    }
    for (Object localObject1 = null; ; localObject1 = null)
    {
      label129: if (Build.VERSION.SDK_INT >= 24)
      {
        localObject2 = FileProvider.a(paramActivity, "org.vidogram.messenger.provider", localFile);
        if (localObject1 != null)
        {
          paramTLObject = (TLObject)localObject1;
          localIntent.setDataAndType((Uri)localObject2, paramTLObject);
          if (localObject1 == null)
            break label270;
          try
          {
            paramActivity.startActivityForResult(localIntent, 500);
            return;
          }
          catch (Exception paramTLObject)
          {
            if (Build.VERSION.SDK_INT < 24)
              break label253;
          }
          localIntent.setDataAndType(FileProvider.a(paramActivity, "org.vidogram.messenger.provider", localFile), "text/plain");
        }
      }
      while (true)
      {
        paramActivity.startActivityForResult(localIntent, 500);
        return;
        paramTLObject = "text/plain";
        break;
        localObject2 = Uri.fromFile(localFile);
        if (localObject1 != null);
        for (paramTLObject = (TLObject)localObject1; ; paramTLObject = "text/plain")
        {
          localIntent.setDataAndType((Uri)localObject2, paramTLObject);
          break;
        }
        label253: localIntent.setDataAndType(Uri.fromFile(localFile), "text/plain");
      }
      label270: paramActivity.startActivityForResult(localIntent, 500);
      return;
    }
  }

  // ERROR //
  private static void registerLoginContentObserver(boolean paramBoolean, String paramString)
  {
    // Byte code:
    //   0: iload_0
    //   1: ifeq +72 -> 73
    //   4: getstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   7: ifnull +4 -> 11
    //   10: return
    //   11: getstatic 152	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   14: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   17: astore_2
    //   18: getstatic 1443	android/provider/CallLog$Calls:CONTENT_URI	Landroid/net/Uri;
    //   21: astore_3
    //   22: new 8	org/vidogram/messenger/AndroidUtilities$2
    //   25: dup
    //   26: new 311	android/os/Handler
    //   29: dup
    //   30: invokespecial 1548	android/os/Handler:<init>	()V
    //   33: aload_1
    //   34: invokespecial 1551	org/vidogram/messenger/AndroidUtilities$2:<init>	(Landroid/os/Handler;Ljava/lang/String;)V
    //   37: astore 4
    //   39: aload 4
    //   41: putstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   44: aload_2
    //   45: aload_3
    //   46: iconst_1
    //   47: aload 4
    //   49: invokevirtual 1555	android/content/ContentResolver:registerContentObserver	(Landroid/net/Uri;ZLandroid/database/ContentObserver;)V
    //   52: new 10	org/vidogram/messenger/AndroidUtilities$3
    //   55: dup
    //   56: aload_1
    //   57: invokespecial 1556	org/vidogram/messenger/AndroidUtilities$3:<init>	(Ljava/lang/String;)V
    //   60: astore_1
    //   61: aload_1
    //   62: putstatic 179	org/vidogram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   65: aload_1
    //   66: ldc2_w 1557
    //   69: invokestatic 1562	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;J)V
    //   72: return
    //   73: getstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   76: ifnull -66 -> 10
    //   79: getstatic 179	org/vidogram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   82: ifnull +13 -> 95
    //   85: getstatic 179	org/vidogram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   88: invokestatic 1564	org/vidogram/messenger/AndroidUtilities:cancelRunOnUIThread	(Ljava/lang/Runnable;)V
    //   91: aconst_null
    //   92: putstatic 179	org/vidogram/messenger/AndroidUtilities:unregisterRunnable	Ljava/lang/Runnable;
    //   95: getstatic 152	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   98: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   101: getstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   104: invokevirtual 1568	android/content/ContentResolver:unregisterContentObserver	(Landroid/database/ContentObserver;)V
    //   107: aconst_null
    //   108: putstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   111: return
    //   112: astore_1
    //   113: aconst_null
    //   114: putstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   117: return
    //   118: astore_1
    //   119: aconst_null
    //   120: putstatic 1547	org/vidogram/messenger/AndroidUtilities:callLogContentObserver	Landroid/database/ContentObserver;
    //   123: aload_1
    //   124: athrow
    //
    // Exception table:
    //   from	to	target	type
    //   95	107	112	java/lang/Exception
    //   95	107	118	finally
  }

  public static void removeAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet()));
    do
      return;
    while (adjustOwnerClassGuid != paramInt);
    paramActivity.getWindow().setSoftInputMode(32);
  }

  // ERROR //
  public static void removeLoginPhoneCall(String paramString, boolean paramBoolean)
  {
    // Byte code:
    //   0: getstatic 163	org/vidogram/messenger/AndroidUtilities:hasCallPermissions	Z
    //   3: ifne +4 -> 7
    //   6: return
    //   7: getstatic 152	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   10: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   13: getstatic 1443	android/provider/CallLog$Calls:CONTENT_URI	Landroid/net/Uri;
    //   16: iconst_2
    //   17: anewarray 435	java/lang/String
    //   20: dup
    //   21: iconst_0
    //   22: ldc_w 1583
    //   25: aastore
    //   26: dup
    //   27: iconst_1
    //   28: ldc_w 1445
    //   31: aastore
    //   32: ldc_w 1449
    //   35: aconst_null
    //   36: ldc_w 1451
    //   39: invokevirtual 1039	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
    //   42: astore 4
    //   44: aload 4
    //   46: astore_3
    //   47: aload 4
    //   49: invokeinterface 1454 1 0
    //   54: ifeq +155 -> 209
    //   57: aload 4
    //   59: astore_3
    //   60: aload 4
    //   62: iconst_1
    //   63: invokeinterface 1051 2 0
    //   68: astore 5
    //   70: aload 4
    //   72: astore_3
    //   73: aload 5
    //   75: aload_0
    //   76: invokevirtual 1351	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   79: ifne +15 -> 94
    //   82: aload 4
    //   84: astore_3
    //   85: aload_0
    //   86: aload 5
    //   88: invokevirtual 1351	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   91: ifeq -47 -> 44
    //   94: aload 4
    //   96: astore_3
    //   97: getstatic 152	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   100: invokevirtual 1031	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   103: getstatic 1443	android/provider/CallLog$Calls:CONTENT_URI	Landroid/net/Uri;
    //   106: ldc_w 1585
    //   109: iconst_1
    //   110: anewarray 435	java/lang/String
    //   113: dup
    //   114: iconst_0
    //   115: aload 4
    //   117: iconst_0
    //   118: invokeinterface 1588 2 0
    //   123: invokestatic 1590	java/lang/String:valueOf	(I)Ljava/lang/String;
    //   126: aastore
    //   127: invokevirtual 1594	android/content/ContentResolver:delete	(Landroid/net/Uri;Ljava/lang/String;[Ljava/lang/String;)I
    //   130: pop
    //   131: iconst_1
    //   132: istore_2
    //   133: iload_2
    //   134: ifne +15 -> 149
    //   137: iload_1
    //   138: ifeq +11 -> 149
    //   141: aload 4
    //   143: astore_3
    //   144: iconst_1
    //   145: aload_0
    //   146: invokestatic 175	org/vidogram/messenger/AndroidUtilities:registerLoginContentObserver	(ZLjava/lang/String;)V
    //   149: aload 4
    //   151: ifnull -145 -> 6
    //   154: aload 4
    //   156: invokeinterface 1058 1 0
    //   161: return
    //   162: astore_0
    //   163: aconst_null
    //   164: astore 4
    //   166: aload 4
    //   168: astore_3
    //   169: aload_0
    //   170: invokestatic 169	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   173: aload 4
    //   175: ifnull -169 -> 6
    //   178: aload 4
    //   180: invokeinterface 1058 1 0
    //   185: return
    //   186: astore_0
    //   187: aconst_null
    //   188: astore_3
    //   189: aload_3
    //   190: ifnull +9 -> 199
    //   193: aload_3
    //   194: invokeinterface 1058 1 0
    //   199: aload_0
    //   200: athrow
    //   201: astore_0
    //   202: goto -13 -> 189
    //   205: astore_0
    //   206: goto -40 -> 166
    //   209: iconst_0
    //   210: istore_2
    //   211: goto -78 -> 133
    //
    // Exception table:
    //   from	to	target	type
    //   7	44	162	java/lang/Exception
    //   7	44	186	finally
    //   47	57	201	finally
    //   60	70	201	finally
    //   73	82	201	finally
    //   85	94	201	finally
    //   97	131	201	finally
    //   144	149	201	finally
    //   169	173	201	finally
    //   47	57	205	java/lang/Exception
    //   60	70	205	java/lang/Exception
    //   73	82	205	java/lang/Exception
    //   85	94	205	java/lang/Exception
    //   97	131	205	java/lang/Exception
    //   144	149	205	java/lang/Exception
  }

  public static SpannableStringBuilder replaceTags(String paramString)
  {
    return replaceTags(paramString, 3);
  }

  public static SpannableStringBuilder replaceTags(String paramString, int paramInt)
  {
    int i;
    try
    {
      localObject = new StringBuilder(paramString);
      if ((paramInt & 0x1) == 0)
        break label92;
      while (true)
      {
        i = ((StringBuilder)localObject).indexOf("<br>");
        if (i == -1)
          break;
        ((StringBuilder)localObject).replace(i, i + 4, "\n");
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      return new SpannableStringBuilder(paramString);
    }
    while (true)
    {
      i = ((StringBuilder)localObject).indexOf("<br/>");
      if (i == -1)
        break;
      ((StringBuilder)localObject).replace(i, i + 5, "\n");
    }
    label92: ArrayList localArrayList = new ArrayList();
    if ((paramInt & 0x2) != 0)
      while (true)
      {
        int j = ((StringBuilder)localObject).indexOf("<b>");
        if (j == -1)
          break;
        ((StringBuilder)localObject).replace(j, j + 3, "");
        i = ((StringBuilder)localObject).indexOf("</b>");
        paramInt = i;
        if (i == -1)
          paramInt = ((StringBuilder)localObject).indexOf("<b>");
        ((StringBuilder)localObject).replace(paramInt, paramInt + 4, "");
        localArrayList.add(Integer.valueOf(j));
        localArrayList.add(Integer.valueOf(paramInt));
      }
    Object localObject = new SpannableStringBuilder((CharSequence)localObject);
    paramInt = 0;
    while (paramInt < localArrayList.size() / 2)
    {
      ((SpannableStringBuilder)localObject).setSpan(new TypefaceSpan(getTypeface("fonts/rmedium.ttf")), ((Integer)localArrayList.get(paramInt * 2)).intValue(), ((Integer)localArrayList.get(paramInt * 2 + 1)).intValue(), 33);
      paramInt += 1;
    }
    return (SpannableStringBuilder)localObject;
  }

  public static void requestAdjustResize(Activity paramActivity, int paramInt)
  {
    if ((paramActivity == null) || (isTablet()))
      return;
    paramActivity.getWindow().setSoftInputMode(16);
    adjustOwnerClassGuid = paramInt;
  }

  private static double[] rgbToHsv(int paramInt1, int paramInt2, int paramInt3)
  {
    double d5 = paramInt1 / 255.0D;
    double d3 = paramInt2 / 255.0D;
    double d4 = paramInt3 / 255.0D;
    double d1;
    double d2;
    label63: double d7;
    double d6;
    if ((d5 > d3) && (d5 > d4))
    {
      d1 = d5;
      if ((d5 >= d3) || (d5 >= d4))
        break label126;
      d2 = d5;
      d7 = d1 - d2;
      if (d1 != 0.0D)
        break label148;
      d6 = 0.0D;
    }
    while (true)
    {
      if (d1 != d2)
        break label157;
      d2 = 0.0D;
      return new double[] { d2, d6, d1 };
      if (d3 > d4)
      {
        d1 = d3;
        break;
      }
      d1 = d4;
      break;
      label126: if (d3 < d4)
      {
        d2 = d3;
        break label63;
      }
      d2 = d4;
      break label63;
      label148: d6 = d7 / d1;
    }
    label157: if ((d5 > d3) && (d5 > d4))
    {
      d2 = (d3 - d4) / d7;
      if (d3 < d4)
      {
        paramInt1 = 6;
        label194: d2 = paramInt1 + d2;
      }
    }
    while (true)
    {
      d2 /= 6.0D;
      break;
      paramInt1 = 0;
      break label194;
      if (d3 > d4)
      {
        d2 = (d4 - d5) / d7 + 2.0D;
        continue;
      }
      d2 = (d5 - d3) / d7 + 4.0D;
    }
  }

  public static void runOnUIThread(Runnable paramRunnable)
  {
    runOnUIThread(paramRunnable, 0L);
  }

  public static void runOnUIThread(Runnable paramRunnable, long paramLong)
  {
    if (paramLong == 0L)
    {
      ApplicationLoader.applicationHandler.post(paramRunnable);
      return;
    }
    ApplicationLoader.applicationHandler.postDelayed(paramRunnable, paramLong);
  }

  public static int setMyLayerVersion(int paramInt1, int paramInt2)
  {
    return 0xFFFF0000 & paramInt1 | paramInt2;
  }

  public static int setPeerLayerVersion(int paramInt1, int paramInt2)
  {
    return 0xFFFF & paramInt1 | paramInt2 << 16;
  }

  public static void setRectToRect(Matrix paramMatrix, RectF paramRectF1, RectF paramRectF2, int paramInt, Matrix.ScaleToFit paramScaleToFit)
  {
    float f2;
    float f1;
    float f3;
    float f4;
    if ((paramInt == 90) || (paramInt == 270))
    {
      f2 = paramRectF2.height() / paramRectF1.width();
      f1 = paramRectF2.width() / paramRectF1.height();
      f3 = f1;
      f4 = f2;
      if (paramScaleToFit != Matrix.ScaleToFit.FILL)
      {
        if (f2 <= f1)
          break label168;
        f4 = f1;
        f3 = f1;
      }
      label67: f1 = -paramRectF1.left;
      f2 = -paramRectF1.top;
      paramMatrix.setTranslate(paramRectF2.left, paramRectF2.top);
      if (paramInt != 90)
        break label179;
      paramMatrix.preRotate(90.0F);
      paramMatrix.preTranslate(0.0F, -paramRectF2.width());
    }
    while (true)
    {
      paramMatrix.preScale(f4, f3);
      paramMatrix.preTranslate(f1 * f4, f2 * f3);
      return;
      f2 = paramRectF2.width() / paramRectF1.width();
      f1 = paramRectF2.height() / paramRectF1.height();
      break;
      label168: f3 = f2;
      f4 = f2;
      break label67;
      label179: if (paramInt == 180)
      {
        paramMatrix.preRotate(180.0F);
        paramMatrix.preTranslate(-paramRectF2.width(), -paramRectF2.height());
        continue;
      }
      if (paramInt != 270)
        continue;
      paramMatrix.preRotate(270.0F);
      paramMatrix.preTranslate(-paramRectF2.height(), 0.0F);
    }
  }

  public static void setScrollViewEdgeEffectColor(ScrollView paramScrollView, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21);
    try
    {
      Object localObject = ScrollView.class.getDeclaredField("mEdgeGlowTop");
      ((Field)localObject).setAccessible(true);
      localObject = (EdgeEffect)((Field)localObject).get(paramScrollView);
      if (localObject != null)
        ((EdgeEffect)localObject).setColor(paramInt);
      localObject = ScrollView.class.getDeclaredField("mEdgeGlowBottom");
      ((Field)localObject).setAccessible(true);
      paramScrollView = (EdgeEffect)((Field)localObject).get(paramScrollView);
      if (paramScrollView != null)
        paramScrollView.setColor(paramInt);
      return;
    }
    catch (Exception paramScrollView)
    {
      FileLog.e(paramScrollView);
    }
  }

  public static void setViewPagerEdgeEffectColor(ViewPager paramViewPager, int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 21);
    try
    {
      Object localObject = ViewPager.class.getDeclaredField("mLeftEdge");
      ((Field)localObject).setAccessible(true);
      localObject = (h)((Field)localObject).get(paramViewPager);
      if (localObject != null)
      {
        Field localField = h.class.getDeclaredField("a");
        localField.setAccessible(true);
        localObject = (EdgeEffect)localField.get(localObject);
        if (localObject != null)
          ((EdgeEffect)localObject).setColor(paramInt);
      }
      localObject = ViewPager.class.getDeclaredField("mRightEdge");
      ((Field)localObject).setAccessible(true);
      paramViewPager = (h)((Field)localObject).get(paramViewPager);
      if (paramViewPager != null)
      {
        localObject = h.class.getDeclaredField("a");
        ((Field)localObject).setAccessible(true);
        paramViewPager = (EdgeEffect)((Field)localObject).get(paramViewPager);
        if (paramViewPager != null)
          paramViewPager.setColor(paramInt);
      }
      return;
    }
    catch (Exception paramViewPager)
    {
      FileLog.e(paramViewPager);
    }
  }

  public static void setWaitingForCall(boolean paramBoolean)
  {
    synchronized (callLock)
    {
      waitingForCall = paramBoolean;
      return;
    }
  }

  public static void setWaitingForSms(boolean paramBoolean)
  {
    synchronized (smsLock)
    {
      waitingForSms = paramBoolean;
      return;
    }
  }

  public static void shakeView(View paramView, float paramFloat, int paramInt)
  {
    if (paramInt == 6)
    {
      paramView.setTranslationX(0.0F);
      return;
    }
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(paramView, "translationX", new float[] { dp(paramFloat) }) });
    localAnimatorSet.setDuration(50L);
    localAnimatorSet.addListener(new AnimatorListenerAdapter(paramView, paramInt, paramFloat)
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        paramAnimator = this.val$view;
        float f;
        if (this.val$num == 5)
          f = 0.0F;
        while (true)
        {
          AndroidUtilities.shakeView(paramAnimator, f, this.val$num + 1);
          return;
          f = -this.val$x;
        }
      }
    });
    localAnimatorSet.start();
  }

  public static void showKeyboard(View paramView)
  {
    if (paramView == null)
      return;
    try
    {
      ((InputMethodManager)paramView.getContext().getSystemService("input_method")).showSoftInput(paramView, 1);
      return;
    }
    catch (Exception paramView)
    {
      FileLog.e(paramView);
    }
  }

  public static void uninstallShortcut(long paramLong)
  {
    try
    {
      Intent localIntent = createShortcutIntent(paramLong, true);
      localIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
      ApplicationLoader.applicationContext.sendBroadcast(localIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public static void unlockOrientation(Activity paramActivity)
  {
    if (paramActivity == null);
    while (true)
    {
      return;
      try
      {
        if (prevOrientation == -10)
          continue;
        paramActivity.setRequestedOrientation(prevOrientation);
        prevOrientation = -10;
        return;
      }
      catch (Exception paramActivity)
      {
        FileLog.e(paramActivity);
      }
    }
  }

  public static void unregisterUpdates()
  {
    if (BuildVars.DEBUG_VERSION)
      m.a();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.AndroidUtilities
 * JD-Core Version:    0.6.0
 */