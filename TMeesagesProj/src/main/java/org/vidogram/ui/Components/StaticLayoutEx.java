package org.vidogram.ui.Components;

import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.StaticLayout.Builder;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import org.vidogram.messenger.FileLog;

public class StaticLayoutEx
{
  private static final String TEXT_DIRS_CLASS = "android.text.TextDirectionHeuristics";
  private static final String TEXT_DIR_CLASS = "android.text.TextDirectionHeuristic";
  private static final String TEXT_DIR_FIRSTSTRONG_LTR = "FIRSTSTRONG_LTR";
  private static boolean initialized;
  private static Constructor<StaticLayout> sConstructor;
  private static Object[] sConstructorArgs;
  private static Object sTextDirection;

  public static StaticLayout createStaticLayout(CharSequence paramCharSequence, int paramInt1, int paramInt2, TextPaint paramTextPaint, int paramInt3, Layout.Alignment paramAlignment, float paramFloat1, float paramFloat2, boolean paramBoolean, TextUtils.TruncateAt paramTruncateAt, int paramInt4, int paramInt5)
  {
    float f;
    if (paramInt5 == 1)
      f = paramInt4;
    while (true)
    {
      try
      {
        paramCharSequence = TextUtils.ellipsize(paramCharSequence, paramTextPaint, f, TextUtils.TruncateAt.END);
        return new StaticLayout(paramCharSequence, 0, paramCharSequence.length(), paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
        if (Build.VERSION.SDK_INT >= 23)
        {
          paramTruncateAt = StaticLayout.Builder.obtain(paramCharSequence, 0, paramCharSequence.length(), paramTextPaint, paramInt3).setAlignment(paramAlignment).setLineSpacing(paramFloat2, paramFloat1).setIncludePad(paramBoolean).setEllipsize(null).setEllipsizedWidth(paramInt4).setBreakStrategy(1).setHyphenationFrequency(1).build();
          if (paramTruncateAt.getLineCount() <= paramInt5)
            break;
          f = paramTruncateAt.getLineLeft(paramInt5 - 1);
          if (f == 0.0F)
            break label232;
          paramInt1 = paramTruncateAt.getOffsetForHorizontal(paramInt5 - 1, f);
          paramCharSequence = new SpannableStringBuilder(paramCharSequence.subSequence(0, Math.max(0, paramInt1 - 1)));
          paramCharSequence.append("…");
          paramCharSequence = new StaticLayout(paramCharSequence, paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
          return paramCharSequence;
        }
      }
      catch (java.lang.Exception paramCharSequence)
      {
        FileLog.e(paramCharSequence);
        return null;
      }
      paramTruncateAt = new StaticLayout(paramCharSequence, paramTextPaint, paramInt3, paramAlignment, paramFloat1, paramFloat2, paramBoolean);
      continue;
      label232: paramInt1 = paramTruncateAt.getOffsetForHorizontal(paramInt5 - 1, paramTruncateAt.getLineWidth(paramInt5 - 1));
    }
    return paramTruncateAt;
  }

  public static StaticLayout createStaticLayout(CharSequence paramCharSequence, TextPaint paramTextPaint, int paramInt1, Layout.Alignment paramAlignment, float paramFloat1, float paramFloat2, boolean paramBoolean, TextUtils.TruncateAt paramTruncateAt, int paramInt2, int paramInt3)
  {
    return createStaticLayout(paramCharSequence, 0, paramCharSequence.length(), paramTextPaint, paramInt1, paramAlignment, paramFloat1, paramFloat2, paramBoolean, paramTruncateAt, paramInt2, paramInt3);
  }

  public static void init()
  {
    if (initialized)
      return;
    while (true)
    {
      try
      {
        if (Build.VERSION.SDK_INT >= 18)
        {
          TextDirectionHeuristic localTextDirectionHeuristic = TextDirectionHeuristic.class;
          sTextDirection = TextDirectionHeuristics.FIRSTSTRONG_LTR;
          localObject = new Class[13];
          localObject[0] = CharSequence.class;
          localObject[1] = Integer.TYPE;
          localObject[2] = Integer.TYPE;
          localObject[3] = TextPaint.class;
          localObject[4] = Integer.TYPE;
          localObject[5] = Layout.Alignment.class;
          localObject[6] = localTextDirectionHeuristic;
          localObject[7] = Float.TYPE;
          localObject[8] = Float.TYPE;
          localObject[9] = Boolean.TYPE;
          localObject[10] = TextUtils.TruncateAt.class;
          localObject[11] = Integer.TYPE;
          localObject[12] = Integer.TYPE;
          sConstructor = StaticLayout.class.getDeclaredConstructor(localObject);
          sConstructor.setAccessible(true);
          sConstructorArgs = new Object[localObject.length];
          initialized = true;
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        return;
      }
      Object localObject = StaticLayoutEx.class.getClassLoader();
      Class localClass = ((ClassLoader)localObject).loadClass("android.text.TextDirectionHeuristic");
      localObject = ((ClassLoader)localObject).loadClass("android.text.TextDirectionHeuristics");
      sTextDirection = ((Class)localObject).getField("FIRSTSTRONG_LTR").get(localObject);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.StaticLayoutEx
 * JD-Core Version:    0.6.0
 */