package org.a.b.b;

import org.a.a.a.c;

class e extends a
  implements c
{
  Class d;

  e(int paramInt, String paramString, Class paramClass1, Class[] paramArrayOfClass1, String[] paramArrayOfString, Class[] paramArrayOfClass2, Class paramClass2)
  {
    super(paramInt, paramString, paramClass1, paramArrayOfClass1, paramArrayOfString, paramArrayOfClass2);
    this.d = paramClass2;
  }

  protected String a(h paramh)
  {
    StringBuffer localStringBuffer = new StringBuffer();
    localStringBuffer.append(paramh.a(d()));
    if (paramh.b)
      localStringBuffer.append(paramh.a(c()));
    if (paramh.b)
      localStringBuffer.append(" ");
    localStringBuffer.append(paramh.a(f(), g()));
    localStringBuffer.append(".");
    localStringBuffer.append(e());
    paramh.b(localStringBuffer, a());
    paramh.c(localStringBuffer, b());
    return localStringBuffer.toString();
  }

  public Class c()
  {
    if (this.d == null)
      this.d = c(6);
    return this.d;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.a.b.b.e
 * JD-Core Version:    0.6.0
 */