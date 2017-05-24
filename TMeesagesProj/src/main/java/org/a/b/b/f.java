package org.a.b.b;

import java.lang.ref.SoftReference;
import java.util.StringTokenizer;
import org.a.a.d;

abstract class f
  implements d
{
  private static boolean a = true;
  static String[] k = new String[0];
  static Class[] l = new Class[0];
  private String b;
  int e = -1;
  String f;
  String g;
  Class h;
  a i;
  ClassLoader j = null;

  f(int paramInt, String paramString, Class paramClass)
  {
    this.e = paramInt;
    this.f = paramString;
    this.h = paramClass;
  }

  private ClassLoader a()
  {
    if (this.j == null)
      this.j = getClass().getClassLoader();
    return this.j;
  }

  String a(int paramInt)
  {
    int m = 0;
    int i1 = this.b.indexOf('-');
    int n = paramInt;
    paramInt = i1;
    while (n > 0)
    {
      m = paramInt + 1;
      paramInt = this.b.indexOf('-', m);
      n -= 1;
    }
    n = paramInt;
    if (paramInt == -1)
      n = this.b.length();
    return this.b.substring(m, n);
  }

  protected abstract String a(h paramh);

  int b(int paramInt)
  {
    return Integer.parseInt(a(paramInt), 16);
  }

  String b(h paramh)
  {
    Object localObject3 = null;
    Object localObject1 = localObject3;
    if ((!a) || (this.i == null));
    while (true)
    {
      try
      {
        this.i = new b();
        localObject1 = localObject3;
        localObject3 = localObject1;
        if (localObject1 != null)
          continue;
        localObject3 = a(paramh);
        if (!a)
          continue;
        this.i.a(paramh.i, (String)localObject3);
        return localObject3;
      }
      catch (Throwable localObject2)
      {
        a = false;
        localObject2 = localObject3;
        continue;
      }
      Object localObject2 = this.i.a(paramh.i);
    }
  }

  Class c(int paramInt)
  {
    return b.a(a(paramInt), a());
  }

  public int d()
  {
    if (this.e == -1)
      this.e = b(0);
    return this.e;
  }

  Class[] d(int paramInt)
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(a(paramInt), ":");
    int m = localStringTokenizer.countTokens();
    Class[] arrayOfClass = new Class[m];
    paramInt = 0;
    while (paramInt < m)
    {
      arrayOfClass[paramInt] = b.a(localStringTokenizer.nextToken(), a());
      paramInt += 1;
    }
    return arrayOfClass;
  }

  public String e()
  {
    if (this.f == null)
      this.f = a(1);
    return this.f;
  }

  public Class f()
  {
    if (this.h == null)
      this.h = c(2);
    return this.h;
  }

  public String g()
  {
    if (this.g == null)
      this.g = f().getName();
    return this.g;
  }

  public final String toString()
  {
    return b(h.k);
  }

  private static abstract interface a
  {
    public abstract String a(int paramInt);

    public abstract void a(int paramInt, String paramString);
  }

  private static final class b
    implements f.a
  {
    private SoftReference a;

    public b()
    {
      b();
    }

    private String[] a()
    {
      return (String[])(String[])this.a.get();
    }

    private String[] b()
    {
      String[] arrayOfString = new String[3];
      this.a = new SoftReference(arrayOfString);
      return arrayOfString;
    }

    public String a(int paramInt)
    {
      String[] arrayOfString = a();
      if (arrayOfString == null)
        return null;
      return arrayOfString[paramInt];
    }

    public void a(int paramInt, String paramString)
    {
      String[] arrayOfString2 = a();
      String[] arrayOfString1 = arrayOfString2;
      if (arrayOfString2 == null)
        arrayOfString1 = b();
      arrayOfString1[paramInt] = paramString;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.a.b.b.f
 * JD-Core Version:    0.6.0
 */