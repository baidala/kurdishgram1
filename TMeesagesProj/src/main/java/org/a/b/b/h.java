package org.a.b.b;

import java.lang.reflect.Modifier;

class h
{
  static h j = new h();
  static h k;
  static h l;
  boolean a = true;
  boolean b = true;
  boolean c = false;
  boolean d = false;
  boolean e = false;
  boolean f = true;
  boolean g = true;
  boolean h = true;
  int i;

  static
  {
    j.a = true;
    j.b = false;
    j.c = false;
    j.d = false;
    j.e = true;
    j.f = false;
    j.g = false;
    j.i = 0;
    k = new h();
    k.a = true;
    k.b = true;
    k.c = false;
    k.d = false;
    k.e = false;
    j.i = 1;
    l = new h();
    l.a = false;
    l.b = true;
    l.c = false;
    l.d = true;
    l.e = false;
    l.h = false;
    l.i = 2;
  }

  String a(int paramInt)
  {
    if (!this.d)
      return "";
    String str = Modifier.toString(paramInt);
    if (str.length() == 0)
      return "";
    return str + " ";
  }

  public String a(Class paramClass)
  {
    return a(paramClass, paramClass.getName(), this.a);
  }

  public String a(Class paramClass, String paramString)
  {
    return a(paramClass, paramString, this.e);
  }

  String a(Class paramClass, String paramString, boolean paramBoolean)
  {
    if (paramClass == null)
      return "ANONYMOUS";
    if (paramClass.isArray())
    {
      paramClass = paramClass.getComponentType();
      return a(paramClass, paramClass.getName(), paramBoolean) + "[]";
    }
    if (paramBoolean)
      return b(paramString).replace('$', '.');
    return paramString.replace('$', '.');
  }

  String a(String paramString)
  {
    int m = paramString.lastIndexOf('-');
    if (m == -1)
      return paramString;
    return paramString.substring(m + 1);
  }

  public void a(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    int m = 0;
    while (m < paramArrayOfClass.length)
    {
      if (m > 0)
        paramStringBuffer.append(", ");
      paramStringBuffer.append(a(paramArrayOfClass[m]));
      m += 1;
    }
  }

  String b(String paramString)
  {
    int m = paramString.lastIndexOf('.');
    if (m == -1)
      return paramString;
    return paramString.substring(m + 1);
  }

  public void b(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if (paramArrayOfClass == null)
      return;
    if (!this.b)
    {
      if (paramArrayOfClass.length == 0)
      {
        paramStringBuffer.append("()");
        return;
      }
      paramStringBuffer.append("(..)");
      return;
    }
    paramStringBuffer.append("(");
    a(paramStringBuffer, paramArrayOfClass);
    paramStringBuffer.append(")");
  }

  public void c(StringBuffer paramStringBuffer, Class[] paramArrayOfClass)
  {
    if ((!this.c) || (paramArrayOfClass == null) || (paramArrayOfClass.length == 0))
      return;
    paramStringBuffer.append(" throws ");
    a(paramStringBuffer, paramArrayOfClass);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.a.b.b.h
 * JD-Core Version:    0.6.0
 */