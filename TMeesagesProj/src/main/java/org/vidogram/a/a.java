package org.vidogram.a;

import java.util.ArrayList;
import java.util.Iterator;

public class a
{
  public ArrayList<String> a = new ArrayList();
  public String b = "";
  public ArrayList<String> c = new ArrayList();
  public ArrayList<String> d = new ArrayList();
  public ArrayList<d> e = new ArrayList();

  String a(String paramString)
  {
    Iterator localIterator = this.d.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.startsWith(str))
        return str;
    }
    return null;
  }

  String b(String paramString)
  {
    Iterator localIterator = this.c.iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (paramString.startsWith(str))
        return str;
    }
    return null;
  }

  String c(String paramString)
  {
    String str2 = null;
    String str1;
    Object localObject1;
    if (paramString.startsWith(this.b))
    {
      str2 = this.b;
      str1 = paramString.substring(str2.length());
      localObject1 = null;
    }
    while (true)
    {
      Object localObject3 = this.e.iterator();
      Object localObject2;
      while (((Iterator)localObject3).hasNext())
      {
        localObject2 = ((d)((Iterator)localObject3).next()).a(str1, str2, (String)localObject1, true);
        if (localObject2 == null)
          continue;
        localObject1 = localObject2;
      }
      do
      {
        do
        {
          return localObject1;
          localObject1 = b(paramString);
          if (localObject1 == null)
            break label185;
          str1 = paramString.substring(((String)localObject1).length());
          break;
          localObject2 = this.e.iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = ((d)((Iterator)localObject2).next()).a(str1, str2, (String)localObject1, false);
            if (localObject3 != null)
              return localObject3;
          }
          localObject1 = paramString;
        }
        while (str2 == null);
        localObject1 = paramString;
      }
      while (str1.length() == 0);
      return String.format("%s %s", new Object[] { str2, str1 });
      label185: localObject1 = null;
      str1 = paramString;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.a.a
 * JD-Core Version:    0.6.0
 */