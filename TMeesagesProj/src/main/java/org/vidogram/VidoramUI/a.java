package org.vidogram.VidogramUi;

import android.content.Context;

public class a
{
  private final Context a;

  public a(Context paramContext)
  {
    this.a = paramContext;
  }

  private boolean a(String paramString)
  {
    return android.support.v4.content.a.b(this.a, paramString) == -1;
  }

  public boolean a(String[] paramArrayOfString)
  {
    int m = 0;
    int j = paramArrayOfString.length;
    int i = 0;
    while (true)
    {
      int k = m;
      if (i < j)
      {
        if (a(paramArrayOfString[i]))
          k = 1;
      }
      else
        return k;
      i += 1;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.a
 * JD-Core Version:    0.6.0
 */