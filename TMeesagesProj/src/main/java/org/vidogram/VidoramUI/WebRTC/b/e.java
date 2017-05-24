package org.vidogram.VidogramUi.WebRTC.b;

import java.lang.reflect.Field;

public class e
{
  public static void a(Field paramField, Object paramObject1, Object paramObject2)
  {
    if (paramField != null);
    try
    {
      paramField.setAccessible(true);
      paramField.set(paramObject1, paramObject2);
      return;
    }
    catch (IllegalAccessException paramField)
    {
      paramField.printStackTrace();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.b.e
 * JD-Core Version:    0.6.0
 */