package org.vidogram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.lang.reflect.Array;
import java.util.HashMap;

public class Emoji
{
  private static int bigImgSize = 0;
  private static final int[][] cols;
  private static int drawImgSize = 0;
  private static Bitmap[][] emojiBmp;
  private static boolean inited = false;
  private static boolean[][] loadingEmoji;
  private static Paint placeholderPaint;
  private static HashMap<CharSequence, DrawableInfo> rects = new HashMap();
  private static final int splitCount = 4;

  static
  {
    inited = false;
    emojiBmp = (Bitmap[][])Array.newInstance(Bitmap.class, new int[] { 5, 4 });
    loadingEmoji = (boolean[][])Array.newInstance(Boolean.TYPE, new int[] { 5, 4 });
    Object localObject = { 8, 8, 8, 8 };
    cols = new int[][] { { 15, 15, 15, 15 }, { 6, 6, 6, 6 }, localObject, { 9, 9, 9, 9 }, { 10, 10, 10, 10 } };
    int j = 2;
    int i;
    float f;
    label232: int k;
    if (AndroidUtilities.density <= 1.0F)
    {
      i = 32;
      j = 1;
      drawImgSize = AndroidUtilities.dp(20.0F);
      if (!AndroidUtilities.isTablet())
        break label447;
      f = 40.0F;
      bigImgSize = AndroidUtilities.dp(f);
      k = 0;
    }
    while (true)
    {
      if (k >= EmojiData.data.length)
        break label460;
      int n = (int)Math.ceil(EmojiData.data[k].length / 4.0F);
      int m = 0;
      while (true)
        if (m < EmojiData.data[k].length)
        {
          int i1 = m / n;
          int i3 = m - i1 * n;
          int i2 = i3 % cols[k][i1];
          i3 /= cols[k][i1];
          localObject = new Rect(i2 * i + i2 * j, i3 * i + i3 * j, i2 * j + (i2 + 1) * i, i3 * j + (i3 + 1) * i);
          rects.put(EmojiData.data[k][m], new DrawableInfo((Rect)localObject, (byte)k, (byte)i1, m));
          m += 1;
          continue;
          if (AndroidUtilities.density <= 1.5F)
          {
            i = 64;
            break;
          }
          if (AndroidUtilities.density <= 2.0F)
          {
            i = 64;
            break;
          }
          i = 64;
          break;
          label447: f = 32.0F;
          break label232;
        }
      k += 1;
    }
    label460: placeholderPaint = new Paint();
    placeholderPaint.setColor(0);
  }

  public static String fixEmoji(String paramString)
  {
    int n = paramString.length();
    int k = 0;
    String str = paramString;
    int i;
    int j;
    int m;
    if (k < n)
    {
      i = str.charAt(k);
      if ((i >= 55356) && (i <= 55358))
        if ((i == 55356) && (k < n - 1))
        {
          j = str.charAt(k + 1);
          if ((j == 56879) || (j == 56324) || (j == 56858) || (j == 56703))
          {
            paramString = str.substring(0, k + 2) + "️" + str.substring(k + 2);
            m = n + 1;
            j = k + 2;
          }
        }
    }
    while (true)
    {
      k = j + 1;
      n = m;
      str = paramString;
      break;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      j = k + 1;
      m = n;
      paramString = str;
      continue;
      if (i == 8419)
        return str;
      j = k;
      m = n;
      paramString = str;
      if (i < 8252)
        continue;
      j = k;
      m = n;
      paramString = str;
      if (i > 12953)
        continue;
      j = k;
      m = n;
      paramString = str;
      if (!EmojiData.emojiToFE0FMap.containsKey(Character.valueOf(i)))
        continue;
      paramString = str.substring(0, k + 1) + "️" + str.substring(k + 1);
      m = n + 1;
      j = k + 1;
    }
  }

  public static Drawable getEmojiBigDrawable(String paramString)
  {
    paramString = getEmojiDrawable(paramString);
    if (paramString == null)
      return null;
    paramString.setBounds(0, 0, bigImgSize, bigImgSize);
    EmojiDrawable.access$102(paramString, true);
    return paramString;
  }

  public static EmojiDrawable getEmojiDrawable(CharSequence paramCharSequence)
  {
    DrawableInfo localDrawableInfo = (DrawableInfo)rects.get(paramCharSequence);
    if (localDrawableInfo == null)
    {
      FileLog.e("No drawable for emoji " + paramCharSequence);
      return null;
    }
    paramCharSequence = new EmojiDrawable(localDrawableInfo);
    paramCharSequence.setBounds(0, 0, drawImgSize, drawImgSize);
    return paramCharSequence;
  }

  private static boolean inArray(char paramChar, char[] paramArrayOfChar)
  {
    int m = 0;
    int j = paramArrayOfChar.length;
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        if (paramArrayOfChar[i] == paramChar)
          k = 1;
      }
      else
        return k;
      i += 1;
    }
  }

  public static void invalidateAll(View paramView)
  {
    if ((paramView instanceof ViewGroup))
    {
      paramView = (ViewGroup)paramView;
      int i = 0;
      while (i < paramView.getChildCount())
      {
        invalidateAll(paramView.getChildAt(i));
        i += 1;
      }
    }
    if ((paramView instanceof TextView))
      paramView.invalidate();
  }

  // ERROR //
  private static void loadEmoji(int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: iconst_2
    //   1: istore_3
    //   2: getstatic 77	org/vidogram/messenger/AndroidUtilities:density	F
    //   5: fstore_2
    //   6: fload_2
    //   7: fconst_1
    //   8: fcmpg
    //   9: ifgt +144 -> 153
    //   12: iconst_4
    //   13: istore 4
    //   15: iload 4
    //   17: bipush 7
    //   19: if_icmpge +168 -> 187
    //   22: getstatic 256	java/util/Locale:US	Ljava/util/Locale;
    //   25: ldc_w 258
    //   28: iconst_3
    //   29: anewarray 4	java/lang/Object
    //   32: dup
    //   33: iconst_0
    //   34: iload 4
    //   36: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   39: aastore
    //   40: dup
    //   41: iconst_1
    //   42: fconst_2
    //   43: invokestatic 268	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   46: aastore
    //   47: dup
    //   48: iconst_2
    //   49: iload_0
    //   50: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   53: aastore
    //   54: invokestatic 272	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   57: astore 5
    //   59: getstatic 278	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   62: aload 5
    //   64: invokevirtual 284	android/content/Context:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
    //   67: astore 5
    //   69: aload 5
    //   71: invokevirtual 289	java/io/File:exists	()Z
    //   74: ifeq +9 -> 83
    //   77: aload 5
    //   79: invokevirtual 292	java/io/File:delete	()Z
    //   82: pop
    //   83: getstatic 256	java/util/Locale:US	Ljava/util/Locale;
    //   86: ldc_w 294
    //   89: iconst_3
    //   90: anewarray 4	java/lang/Object
    //   93: dup
    //   94: iconst_0
    //   95: iload 4
    //   97: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   100: aastore
    //   101: dup
    //   102: iconst_1
    //   103: fconst_2
    //   104: invokestatic 268	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   107: aastore
    //   108: dup
    //   109: iconst_2
    //   110: iload_0
    //   111: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   114: aastore
    //   115: invokestatic 272	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   118: astore 5
    //   120: getstatic 278	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   123: aload 5
    //   125: invokevirtual 284	android/content/Context:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
    //   128: astore 5
    //   130: aload 5
    //   132: invokevirtual 289	java/io/File:exists	()Z
    //   135: ifeq +9 -> 144
    //   138: aload 5
    //   140: invokevirtual 292	java/io/File:delete	()Z
    //   143: pop
    //   144: iload 4
    //   146: iconst_1
    //   147: iadd
    //   148: istore 4
    //   150: goto -135 -> 15
    //   153: getstatic 77	org/vidogram/messenger/AndroidUtilities:density	F
    //   156: ldc 117
    //   158: fcmpg
    //   159: ifgt +8 -> 167
    //   162: iconst_1
    //   163: istore_3
    //   164: goto -152 -> 12
    //   167: getstatic 77	org/vidogram/messenger/AndroidUtilities:density	F
    //   170: fstore_2
    //   171: fload_2
    //   172: fconst_2
    //   173: fcmpg
    //   174: ifgt +8 -> 182
    //   177: iconst_1
    //   178: istore_3
    //   179: goto -167 -> 12
    //   182: iconst_1
    //   183: istore_3
    //   184: goto -172 -> 12
    //   187: bipush 8
    //   189: istore 4
    //   191: iload 4
    //   193: bipush 11
    //   195: if_icmpge +80 -> 275
    //   198: getstatic 256	java/util/Locale:US	Ljava/util/Locale;
    //   201: ldc_w 296
    //   204: iconst_3
    //   205: anewarray 4	java/lang/Object
    //   208: dup
    //   209: iconst_0
    //   210: iload 4
    //   212: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: aastore
    //   216: dup
    //   217: iconst_1
    //   218: fconst_2
    //   219: invokestatic 268	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   222: aastore
    //   223: dup
    //   224: iconst_2
    //   225: iload_0
    //   226: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   229: aastore
    //   230: invokestatic 272	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   233: astore 5
    //   235: getstatic 278	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   238: aload 5
    //   240: invokevirtual 284	android/content/Context:getFileStreamPath	(Ljava/lang/String;)Ljava/io/File;
    //   243: astore 5
    //   245: aload 5
    //   247: invokevirtual 289	java/io/File:exists	()Z
    //   250: ifeq +9 -> 259
    //   253: aload 5
    //   255: invokevirtual 292	java/io/File:delete	()Z
    //   258: pop
    //   259: iload 4
    //   261: iconst_1
    //   262: iadd
    //   263: istore 4
    //   265: goto -74 -> 191
    //   268: astore 5
    //   270: aload 5
    //   272: invokestatic 299	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   275: getstatic 278	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   278: invokevirtual 303	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   281: new 162	java/lang/StringBuilder
    //   284: dup
    //   285: invokespecial 163	java/lang/StringBuilder:<init>	()V
    //   288: ldc_w 305
    //   291: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   294: getstatic 256	java/util/Locale:US	Ljava/util/Locale;
    //   297: ldc_w 307
    //   300: iconst_3
    //   301: anewarray 4	java/lang/Object
    //   304: dup
    //   305: iconst_0
    //   306: fconst_2
    //   307: invokestatic 268	java/lang/Float:valueOf	(F)Ljava/lang/Float;
    //   310: aastore
    //   311: dup
    //   312: iconst_1
    //   313: iload_0
    //   314: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   317: aastore
    //   318: dup
    //   319: iconst_2
    //   320: iload_1
    //   321: invokestatic 263	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   324: aastore
    //   325: invokestatic 272	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   328: invokevirtual 171	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   331: invokevirtual 180	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   334: invokevirtual 313	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   337: astore 6
    //   339: new 315	android/graphics/BitmapFactory$Options
    //   342: dup
    //   343: invokespecial 316	android/graphics/BitmapFactory$Options:<init>	()V
    //   346: astore 5
    //   348: aload 5
    //   350: iconst_0
    //   351: putfield 319	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   354: aload 5
    //   356: iload_3
    //   357: putfield 322	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   360: aload 6
    //   362: aconst_null
    //   363: aload 5
    //   365: invokestatic 328	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   368: astore 5
    //   370: aload 6
    //   372: invokevirtual 333	java/io/InputStream:close	()V
    //   375: new 6	org/vidogram/messenger/Emoji$1
    //   378: dup
    //   379: iload_0
    //   380: iload_1
    //   381: aload 5
    //   383: invokespecial 336	org/vidogram/messenger/Emoji$1:<init>	(IILandroid/graphics/Bitmap;)V
    //   386: invokestatic 340	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   389: return
    //   390: aload 6
    //   392: invokestatic 299	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   395: goto -20 -> 375
    //   398: astore 5
    //   400: ldc_w 342
    //   403: aload 5
    //   405: invokestatic 345	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
    //   408: return
    //   409: astore 6
    //   411: goto -21 -> 390
    //   414: astore 6
    //   416: aconst_null
    //   417: astore 5
    //   419: goto -29 -> 390
    //
    // Exception table:
    //   from	to	target	type
    //   22	83	268	java/lang/Exception
    //   83	144	268	java/lang/Exception
    //   198	259	268	java/lang/Exception
    //   2	6	398	java/lang/Throwable
    //   22	83	398	java/lang/Throwable
    //   83	144	398	java/lang/Throwable
    //   153	162	398	java/lang/Throwable
    //   167	171	398	java/lang/Throwable
    //   198	259	398	java/lang/Throwable
    //   270	275	398	java/lang/Throwable
    //   375	389	398	java/lang/Throwable
    //   390	395	398	java/lang/Throwable
    //   370	375	409	java/lang/Throwable
    //   275	370	414	java/lang/Throwable
  }

  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean)
  {
    return replaceEmoji(paramCharSequence, paramFontMetricsInt, paramInt, paramBoolean, null);
  }

  public static CharSequence replaceEmoji(CharSequence paramCharSequence, Paint.FontMetricsInt paramFontMetricsInt, int paramInt, boolean paramBoolean, int[] paramArrayOfInt)
  {
    if ((MessagesController.getInstance().useSystemEmoji) || (paramCharSequence == null) || (paramCharSequence.length() == 0))
      return paramCharSequence;
    Spannable localSpannable;
    long l;
    int i2;
    int k;
    int n;
    int i3;
    StringBuilder localStringBuilder;
    int i8;
    int m;
    int i1;
    if ((!paramBoolean) && ((paramCharSequence instanceof Spannable)))
    {
      localSpannable = (Spannable)paramCharSequence;
      l = 0L;
      i2 = 0;
      k = -1;
      n = 0;
      i3 = 0;
      localStringBuilder = new StringBuilder(16);
      new StringBuilder(2);
      i8 = paramCharSequence.length();
      m = 0;
      i1 = 0;
      if (i1 >= i8)
        break label820;
    }
    while (true)
    {
      label153: label249: int i6;
      int i5;
      int i7;
      int i4;
      int j;
      label357: char c;
      try
      {
        int i = paramCharSequence.charAt(i1);
        if (i < 55356)
          break label885;
        if (i <= 55358)
          break label932;
        break label885;
        localStringBuilder.append(i);
        l = l << 16 | i;
        k = i3;
        n += 1;
        if ((m == 0) || (i1 + 2 >= i8) || (paramCharSequence.charAt(i1 + 1) != 55356))
          break label860;
        i3 = paramCharSequence.charAt(i1 + 2);
        if ((i3 < 57339) || (i3 > 57343))
          break label860;
        localStringBuilder.append(paramCharSequence.subSequence(i1 + 1, i1 + 3));
        i3 = n + 2;
        n = i1 + 2;
        i1 = i3;
        break label949;
        if (i6 >= 3)
          continue;
        i5 = i3;
        i7 = m;
        i4 = i1;
        if (i3 + 1 >= i8)
          continue;
        j = paramCharSequence.charAt(i3 + 1);
        if (i6 != 1)
          break label1042;
        i5 = i3;
        i7 = m;
        i4 = i1;
        if (j != 8205)
          continue;
        i5 = i3;
        i7 = m;
        i4 = i1;
        if (localStringBuilder.length() <= 0)
          continue;
        localStringBuilder.append(j);
        i5 = i3 + 1;
        i4 = i1 + 1;
        i7 = 0;
        i6 += 1;
        i3 = i5;
        m = i7;
        i1 = i4;
        continue;
        localSpannable = Spannable.Factory.getInstance().newSpannable(paramCharSequence.toString());
        break;
        label395: if ((localStringBuilder.length() <= 0) || ((j != 9792) && (j != 9794) && (j != 9877)))
          continue;
        localStringBuilder.append(j);
        l = 0L;
        m = 1;
        n += 1;
        continue;
        if ((l <= 0L) || ((0xF000 & j) != 53248))
          continue;
        localStringBuilder.append(j);
        l = 0L;
        m = 1;
        n += 1;
        continue;
        if (j != 8419)
          break label983;
        if (i1 <= 0)
          break label875;
        c = paramCharSequence.charAt(i3);
        if ((c < '0') || (c > '9'))
          break label959;
        label532: n = i1 - i3 + 1;
        localStringBuilder.append(c);
        localStringBuilder.append(j);
        m = 1;
        break label976;
        label563: if (!EmojiData.dataCharsMap.containsKey(Character.valueOf(j)))
          continue;
        m = k;
        if (k != -1)
          continue;
        m = i1;
        localStringBuilder.append(j);
        k = m;
        m = 1;
        n += 1;
        continue;
        label615: if (k == -1)
          break label1018;
        localStringBuilder.setLength(0);
        k = -1;
        m = 0;
        n = 0;
        continue;
        if ((m == 0) || (i3 + 2 >= i8) || (paramCharSequence.charAt(i3 + 1) != 55356))
          break label857;
        i4 = paramCharSequence.charAt(i3 + 2);
        if ((i4 < 57339) || (i4 > 57343))
          break label857;
        localStringBuilder.append(paramCharSequence.subSequence(i3 + 1, i3 + 3));
        i3 += 2;
        i1 += 2;
        break label1101;
        EmojiDrawable localEmojiDrawable = getEmojiDrawable(localStringBuilder.subSequence(0, localStringBuilder.length()));
        if (localEmojiDrawable != null)
        {
          localSpannable.setSpan(new EmojiSpan(localEmojiDrawable, 0, paramInt, paramFontMetricsInt), k, i1 + k, 33);
          i2 += 1;
          i1 = 0;
          k = -1;
          localStringBuilder.setLength(0);
          m = 0;
          label801: i4 = Build.VERSION.SDK_INT;
          if ((i4 < 23) && (i2 >= 50))
            label820: return localSpannable;
          i4 = i3 + 1;
          i3 = n;
          n = i1;
          i1 = i4;
        }
      }
      catch (java.lang.Exception paramFontMetricsInt)
      {
        FileLog.e(paramFontMetricsInt);
        return paramCharSequence;
      }
      continue;
      label857: label860: label875: label885: label1018: 
      do
      {
        break label801;
        continue;
        i3 = n;
        n = i1;
        i1 = i3;
        do
        {
          break label153;
          do
          {
            i3 = k;
            break label976;
            if ((l == 0L) || ((0x0 & l) != 0L) || ((0xFFFF & l) != 55356L) || (j < 56806) || (j > 56831))
              break label395;
            i3 = k;
            if (k != -1)
              break;
            i3 = i1;
            break;
            i6 = 0;
            i3 = n;
            break label249;
            if (c == '#')
              break label532;
          }
          while (c != '*');
          break label532;
          k = i3;
          break label153;
          if ((j == 169) || (j == 174))
            break label563;
          if ((j < 8252) || (j > 12953))
            break label615;
          break label563;
        }
        while ((j == 65039) || (paramArrayOfInt == null));
        paramArrayOfInt[0] = 0;
        paramArrayOfInt = null;
        break label153;
        i5 = i3;
        i7 = m;
        i4 = i1;
        if (j < 65024)
          break label357;
        i5 = i3;
        i7 = m;
        i4 = i1;
        if (j > 65039)
          break label357;
        i5 = i3 + 1;
        i4 = i1 + 1;
        i7 = m;
        break label357;
      }
      while (m == 0);
      label932: label949: label959: label976: label983: if (paramArrayOfInt == null)
        continue;
      label1042: label1101: paramArrayOfInt[0] += 1;
    }
  }

  private static class DrawableInfo
  {
    public int emojiIndex;
    public byte page;
    public byte page2;
    public Rect rect;

    public DrawableInfo(Rect paramRect, byte paramByte1, byte paramByte2, int paramInt)
    {
      this.rect = paramRect;
      this.page = paramByte1;
      this.page2 = paramByte2;
      this.emojiIndex = paramInt;
    }
  }

  public static class EmojiDrawable extends Drawable
  {
    private static Paint paint = new Paint(2);
    private static Rect rect = new Rect();
    private static TextPaint textPaint = new TextPaint(1);
    private boolean fullSize = false;
    private Emoji.DrawableInfo info;

    public EmojiDrawable(Emoji.DrawableInfo paramDrawableInfo)
    {
      this.info = paramDrawableInfo;
    }

    public void draw(Canvas paramCanvas)
    {
      if (Emoji.emojiBmp[this.info.page][this.info.page2] == null)
      {
        if (Emoji.loadingEmoji[this.info.page][this.info.page2] != 0)
          return;
        Emoji.loadingEmoji[this.info.page][this.info.page2] = 1;
        Utilities.globalQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            Emoji.access$600(Emoji.EmojiDrawable.this.info.page, Emoji.EmojiDrawable.this.info.page2);
            Emoji.loadingEmoji[Emoji.EmojiDrawable.this.info.page][Emoji.EmojiDrawable.this.info.page2] = 0;
          }
        });
        paramCanvas.drawRect(getBounds(), Emoji.placeholderPaint);
        return;
      }
      if (this.fullSize);
      for (Rect localRect = getDrawRect(); ; localRect = getBounds())
      {
        paramCanvas.drawBitmap(Emoji.emojiBmp[this.info.page][this.info.page2], this.info.rect, localRect, paint);
        return;
      }
    }

    public Rect getDrawRect()
    {
      Rect localRect = getBounds();
      int k = localRect.centerX();
      int j = localRect.centerY();
      localRect = rect;
      if (this.fullSize)
      {
        i = Emoji.bigImgSize;
        localRect.left = (k - i / 2);
        localRect = rect;
        if (!this.fullSize)
          break label133;
        i = Emoji.bigImgSize;
        label60: localRect.right = (i / 2 + k);
        localRect = rect;
        if (!this.fullSize)
          break label140;
        i = Emoji.bigImgSize;
        label86: localRect.top = (j - i / 2);
        localRect = rect;
        if (!this.fullSize)
          break label147;
      }
      label133: label140: label147: for (int i = Emoji.bigImgSize; ; i = Emoji.drawImgSize)
      {
        localRect.bottom = (i / 2 + j);
        return rect;
        i = Emoji.drawImgSize;
        break;
        i = Emoji.drawImgSize;
        break label60;
        i = Emoji.drawImgSize;
        break label86;
      }
    }

    public Emoji.DrawableInfo getDrawableInfo()
    {
      return this.info;
    }

    public int getOpacity()
    {
      return -2;
    }

    public void setAlpha(int paramInt)
    {
    }

    public void setColorFilter(ColorFilter paramColorFilter)
    {
    }
  }

  public static class EmojiSpan extends ImageSpan
  {
    private Paint.FontMetricsInt fontMetrics = null;
    private int size = AndroidUtilities.dp(20.0F);

    public EmojiSpan(Emoji.EmojiDrawable paramEmojiDrawable, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      super(paramInt1);
      this.fontMetrics = paramFontMetricsInt;
      if (paramFontMetricsInt != null)
      {
        this.size = (Math.abs(this.fontMetrics.descent) + Math.abs(this.fontMetrics.ascent));
        if (this.size == 0)
          this.size = AndroidUtilities.dp(20.0F);
      }
    }

    public int getSize(Paint paramPaint, CharSequence paramCharSequence, int paramInt1, int paramInt2, Paint.FontMetricsInt paramFontMetricsInt)
    {
      if (paramFontMetricsInt == null)
        paramFontMetricsInt = new Paint.FontMetricsInt();
      while (true)
      {
        if (this.fontMetrics == null)
        {
          paramInt1 = super.getSize(paramPaint, paramCharSequence, paramInt1, paramInt2, paramFontMetricsInt);
          paramInt2 = AndroidUtilities.dp(8.0F);
          int i = AndroidUtilities.dp(10.0F);
          paramFontMetricsInt.top = (-i - paramInt2);
          paramFontMetricsInt.bottom = (i - paramInt2);
          paramFontMetricsInt.ascent = (-i - paramInt2);
          paramFontMetricsInt.leading = 0;
          paramFontMetricsInt.descent = (i - paramInt2);
          return paramInt1;
        }
        if (paramFontMetricsInt != null)
        {
          paramFontMetricsInt.ascent = this.fontMetrics.ascent;
          paramFontMetricsInt.descent = this.fontMetrics.descent;
          paramFontMetricsInt.top = this.fontMetrics.top;
          paramFontMetricsInt.bottom = this.fontMetrics.bottom;
        }
        if (getDrawable() != null)
          getDrawable().setBounds(0, 0, this.size, this.size);
        return this.size;
      }
    }

    public void replaceFontMetrics(Paint.FontMetricsInt paramFontMetricsInt, int paramInt)
    {
      this.fontMetrics = paramFontMetricsInt;
      this.size = paramInt;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.Emoji
 * JD-Core Version:    0.6.0
 */