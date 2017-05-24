package org.a.b.b;

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.a.a.a;
import org.a.a.a.a;

public final class b
{
  static Hashtable e = new Hashtable();
  static Class f;
  private static Object[] g;
  Class a;
  ClassLoader b;
  String c;
  int d;

  static
  {
    e.put("void", Void.TYPE);
    e.put("boolean", Boolean.TYPE);
    e.put("byte", Byte.TYPE);
    e.put("char", Character.TYPE);
    e.put("short", Short.TYPE);
    e.put("int", Integer.TYPE);
    e.put("long", Long.TYPE);
    e.put("float", Float.TYPE);
    e.put("double", Double.TYPE);
    g = new Object[0];
  }

  public b(String paramString, Class paramClass)
  {
    this.c = paramString;
    this.a = paramClass;
    this.d = 0;
    this.b = paramClass.getClassLoader();
  }

  static Class a(String paramString)
  {
    try
    {
      paramString = Class.forName(paramString);
      return paramString;
    }
    catch (java.lang.ClassNotFoundException paramString)
    {
    }
    throw new NoClassDefFoundError(paramString.getMessage());
  }

  static Class a(String paramString, ClassLoader paramClassLoader)
  {
    Object localObject;
    if (paramString.equals("*"))
      localObject = null;
    Class localClass;
    do
    {
      return localObject;
      localClass = (Class)e.get(paramString);
      localObject = localClass;
    }
    while (localClass != null);
    if (paramClassLoader == null);
    try
    {
      return Class.forName(paramString);
      paramString = Class.forName(paramString, false, paramClassLoader);
      return paramString;
    }
    catch (java.lang.ClassNotFoundException paramString)
    {
      if (f == null)
      {
        paramString = a("java.lang.ClassNotFoundException");
        f = paramString;
        return paramString;
      }
    }
    return f;
  }

  public static a a(a.a parama, Object paramObject1, Object paramObject2)
  {
    return new c(parama, paramObject1, paramObject2, g);
  }

  public static a a(a.a parama, Object paramObject1, Object paramObject2, Object paramObject3)
  {
    return new c(parama, paramObject1, paramObject2, new Object[] { paramObject3 });
  }

  public static a a(a.a parama, Object paramObject1, Object paramObject2, Object paramObject3, Object paramObject4)
  {
    return new c(parama, paramObject1, paramObject2, new Object[] { paramObject3, paramObject4 });
  }

  public static a a(a.a parama, Object paramObject1, Object paramObject2, Object[] paramArrayOfObject)
  {
    return new c(parama, paramObject1, paramObject2, paramArrayOfObject);
  }

  public a.a a(String paramString, org.a.a.d paramd, int paramInt)
  {
    int i = this.d;
    this.d = (i + 1);
    return new c.a(i, paramString, paramd, a(paramInt, -1));
  }

  public org.a.a.a.c a(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6, String paramString7)
  {
    int j = Integer.parseInt(paramString1, 16);
    paramString1 = a(paramString3, this.b);
    paramString4 = new StringTokenizer(paramString4, ":");
    int k = paramString4.countTokens();
    paramString3 = new Class[k];
    int i = 0;
    while (i < k)
    {
      paramString3[i] = a(paramString4.nextToken(), this.b);
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString5, ":");
    k = paramString5.countTokens();
    paramString4 = new String[k];
    i = 0;
    while (i < k)
    {
      paramString4[i] = paramString5.nextToken();
      i += 1;
    }
    paramString5 = new StringTokenizer(paramString6, ":");
    k = paramString5.countTokens();
    paramString6 = new Class[k];
    i = 0;
    while (i < k)
    {
      paramString6[i] = a(paramString5.nextToken(), this.b);
      i += 1;
    }
    return new e(j, paramString2, paramString1, paramString3, paramString4, paramString6, a(paramString7, this.b));
  }

  public org.a.a.a.d a(int paramInt1, int paramInt2)
  {
    return new g(this.a, this.c, paramInt1);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.a.b.b.b
 * JD-Core Version:    0.6.0
 */