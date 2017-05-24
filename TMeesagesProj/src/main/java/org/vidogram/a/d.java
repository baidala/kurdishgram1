package org.vidogram.a;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class d
{
  public static Pattern e = Pattern.compile("[0-9]+");
  public int a;
  public ArrayList<c> b = new ArrayList();
  public boolean c;
  public boolean d;

  String a(String paramString1, String paramString2, String paramString3, boolean paramBoolean)
  {
    Object localObject;
    if (paramString1.length() >= this.a)
    {
      localObject = paramString1.substring(0, this.a);
      localObject = e.matcher((CharSequence)localObject);
      if (!((Matcher)localObject).find())
        break label408;
    }
    label408: for (int i = Integer.parseInt(((Matcher)localObject).group(0)); ; i = 0)
    {
      localObject = this.b.iterator();
      c localc;
      while (((Iterator)localObject).hasNext())
      {
        localc = (c)((Iterator)localObject).next();
        if ((i < localc.a) || (i > localc.b) || (paramString1.length() > localc.d))
          continue;
        if (paramBoolean)
          if ((((localc.g & 0x3) == 0) && (paramString3 == null) && (paramString2 == null)) || ((paramString3 != null) && ((localc.g & 0x1) != 0)) || ((paramString2 != null) && ((localc.g & 0x2) != 0)))
            return localc.a(paramString1, paramString2, paramString3);
        if (((paramString3 == null) && (paramString2 == null)) || ((paramString3 != null) && ((localc.g & 0x1) != 0)) || ((paramString2 != null) && ((localc.g & 0x2) != 0)))
          return localc.a(paramString1, paramString2, paramString3);
      }
      if (!paramBoolean)
      {
        if (paramString2 != null)
        {
          localObject = this.b.iterator();
          do
          {
            if (!((Iterator)localObject).hasNext())
              break;
            localc = (c)((Iterator)localObject).next();
          }
          while ((i < localc.a) || (i > localc.b) || (paramString1.length() > localc.d) || ((paramString3 != null) && ((localc.g & 0x1) == 0)));
          return localc.a(paramString1, paramString2, paramString3);
        }
        if (paramString3 != null)
        {
          localObject = this.b.iterator();
          while (((Iterator)localObject).hasNext())
          {
            localc = (c)((Iterator)localObject).next();
            if ((i >= localc.a) && (i <= localc.b) && (paramString1.length() <= localc.d) && ((paramString2 == null) || ((localc.g & 0x2) != 0)))
              return localc.a(paramString1, paramString2, paramString3);
          }
        }
      }
      return null;
      return null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.a.d
 * JD-Core Version:    0.6.0
 */