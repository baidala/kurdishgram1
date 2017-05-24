package org.vidogram.ui.Components;

import android.text.SpannableString;
import java.lang.reflect.Field;
import org.vidogram.messenger.FileLog;

public class SpannableStringLight extends SpannableString
{
  private static boolean fieldsAvailable;
  private static Field mSpanCountField;
  private static Field mSpanDataField;
  private static Field mSpansField;
  private int mSpanCountOverride;
  private int[] mSpanDataOverride;
  private Object[] mSpansOverride;
  private int num;

  public SpannableStringLight(CharSequence paramCharSequence)
  {
    super(paramCharSequence);
    try
    {
      this.mSpansOverride = ((Object[])(Object[])mSpansField.get(this));
      this.mSpanDataOverride = ((int[])(int[])mSpanDataField.get(this));
      this.mSpanCountOverride = ((Integer)mSpanCountField.get(this)).intValue();
      return;
    }
    catch (Throwable paramCharSequence)
    {
      FileLog.e(paramCharSequence);
    }
  }

  public static boolean isFieldsAvailable()
  {
    if ((!fieldsAvailable) && (mSpansField == null));
    try
    {
      mSpansField = SpannableString.class.getSuperclass().getDeclaredField("mSpans");
      mSpansField.setAccessible(true);
      mSpanDataField = SpannableString.class.getSuperclass().getDeclaredField("mSpanData");
      mSpanDataField.setAccessible(true);
      mSpanCountField = SpannableString.class.getSuperclass().getDeclaredField("mSpanCount");
      mSpanCountField.setAccessible(true);
      fieldsAvailable = true;
      if (mSpansField != null)
        return true;
    }
    catch (Throwable localThrowable)
    {
      while (true)
        FileLog.e(localThrowable);
    }
    return false;
  }

  public void removeSpan(Object paramObject)
  {
    super.removeSpan(paramObject);
  }

  public void setSpanLight(Object paramObject, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSpansOverride[this.num] = paramObject;
    this.mSpanDataOverride[(this.num * 3)] = paramInt1;
    this.mSpanDataOverride[(this.num * 3 + 1)] = paramInt2;
    this.mSpanDataOverride[(this.num * 3 + 2)] = paramInt3;
    this.num += 1;
  }

  public void setSpansCount(int paramInt)
  {
    paramInt = this.mSpanCountOverride + paramInt;
    this.mSpansOverride = new Object[paramInt];
    this.mSpanDataOverride = new int[paramInt * 3];
    this.num = this.mSpanCountOverride;
    this.mSpanCountOverride = paramInt;
    try
    {
      mSpansField.set(this, this.mSpansOverride);
      mSpanDataField.set(this, this.mSpanDataOverride);
      mSpanCountField.set(this, Integer.valueOf(this.mSpanCountOverride));
      return;
    }
    catch (Throwable localThrowable)
    {
      FileLog.e(localThrowable);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.SpannableStringLight
 * JD-Core Version:    0.6.0
 */