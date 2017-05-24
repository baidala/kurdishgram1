package org.vidogram.a;

public class c
{
  public int a;
  public int b;
  public int c;
  public int d;
  public int e;
  public int f;
  public int g;
  public int h;
  public String i;
  public boolean j;
  public boolean k;

  String a(String paramString1, String paramString2, String paramString3)
  {
    StringBuilder localStringBuilder = new StringBuilder(20);
    int i5 = 0;
    int i6 = 0;
    int m = 0;
    int i4 = 0;
    int i3 = 0;
    label92: int i7;
    int n;
    int i1;
    int i2;
    if (i5 < this.i.length())
    {
      int i8 = this.i.charAt(i5);
      switch (i8)
      {
      default:
        if ((i8 == 32) && (i5 > 0))
        {
          if (this.i.charAt(i5 - 1) == 'n')
          {
            i7 = i6;
            n = m;
            i1 = i4;
            i2 = i3;
            if (paramString3 == null)
              break;
          }
          if (this.i.charAt(i5 - 1) == 'c')
          {
            i7 = i6;
            n = m;
            i1 = i4;
            i2 = i3;
            if (paramString2 == null)
              break;
          }
        }
        if (i6 >= paramString1.length())
        {
          i7 = i6;
          n = m;
          i1 = i4;
          i2 = i3;
          if (m == 0)
            break;
          i7 = i6;
          n = m;
          i1 = i4;
          i2 = i3;
          if (i8 != 41)
            break;
        }
        else
        {
          localStringBuilder.append(this.i.substring(i5, i5 + 1));
          i7 = i6;
          n = m;
          i1 = i4;
          i2 = i3;
          if (i8 != 41)
            break;
          n = 0;
          i2 = i3;
          i1 = i4;
          i7 = i6;
        }
      case 99:
      case 110:
      case 35:
      case 40:
      }
    }
    while (true)
    {
      i5 += 1;
      i6 = i7;
      m = n;
      i4 = i1;
      i3 = i2;
      break;
      if (paramString2 != null)
      {
        localStringBuilder.append(paramString2);
        i2 = 1;
        i7 = i6;
        n = m;
        i1 = i4;
        continue;
        if (paramString3 != null)
        {
          localStringBuilder.append(paramString3);
          i1 = 1;
          i7 = i6;
          n = m;
          i2 = i3;
          continue;
          if (i6 < paramString1.length())
          {
            localStringBuilder.append(paramString1.substring(i6, i6 + 1));
            i7 = i6 + 1;
            n = m;
            i1 = i4;
            i2 = i3;
            continue;
          }
          i7 = i6;
          n = m;
          i1 = i4;
          i2 = i3;
          if (m == 0)
            continue;
          localStringBuilder.append(" ");
          i7 = i6;
          n = m;
          i1 = i4;
          i2 = i3;
          continue;
          if (i6 >= paramString1.length())
            break label92;
          m = 1;
          break label92;
          if ((paramString2 != null) && (i3 == 0))
            localStringBuilder.insert(0, String.format("%s ", new Object[] { paramString2 }));
          while (true)
          {
            return localStringBuilder.toString();
            if ((paramString3 == null) || (i4 != 0))
              continue;
            localStringBuilder.insert(0, paramString3);
          }
        }
        i1 = 1;
        i7 = i6;
        n = m;
        i2 = i3;
        continue;
      }
      i2 = 1;
      i7 = i6;
      n = m;
      i1 = i4;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.a.c
 * JD-Core Version:    0.6.0
 */