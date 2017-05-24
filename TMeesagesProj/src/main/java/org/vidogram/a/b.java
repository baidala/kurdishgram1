package org.vidogram.a;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.FileLog;

public class b
{
  private static volatile b j = null;
  public byte[] a;
  public ByteBuffer b;
  public String c;
  public String d;
  public HashMap<String, Integer> e;
  public HashMap<String, ArrayList<String>> f;
  public HashMap<String, a> g;
  public HashMap<String, String> h;
  private boolean i = false;

  public b()
  {
    c(null);
  }

  public static String a(String paramString)
  {
    paramString = new StringBuilder(paramString);
    int k = paramString.length() - 1;
    while (k >= 0)
    {
      if (!"0123456789+*#".contains(paramString.substring(k, k + 1)))
        paramString.deleteCharAt(k);
      k -= 1;
    }
    return paramString.toString();
  }

  public static String a(String paramString, boolean paramBoolean)
  {
    if (paramString == null)
      return null;
    StringBuilder localStringBuilder = new StringBuilder(paramString);
    paramString = "0123456789";
    if (paramBoolean)
      paramString = "0123456789" + "+";
    int k = localStringBuilder.length() - 1;
    while (k >= 0)
    {
      if (!paramString.contains(localStringBuilder.substring(k, k + 1)))
        localStringBuilder.deleteCharAt(k);
      k -= 1;
    }
    return localStringBuilder.toString();
  }

  public static b a()
  {
    Object localObject1 = j;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        b localb = j;
        localObject1 = localb;
        if (localb == null)
        {
          localObject1 = new b();
          j = (b)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (b)localObject2;
  }

  public static String b(String paramString)
  {
    return a(paramString, false);
  }

  int a(int paramInt)
  {
    if (paramInt + 4 <= this.a.length)
    {
      this.b.position(paramInt);
      return this.b.getInt();
    }
    return 0;
  }

  short b(int paramInt)
  {
    if (paramInt + 2 <= this.a.length)
    {
      this.b.position(paramInt);
      return this.b.getShort();
    }
    return 0;
  }

  public void b()
  {
    int n = a(0);
    int m = 4;
    int k = 0;
    while (k < n)
    {
      String str1 = c(m);
      m += 4;
      String str2 = c(m);
      m += 4;
      int i1 = a(m);
      if (str2.equals(this.c))
        this.d = str1;
      this.h.put(str2, str1);
      this.e.put(str1, Integer.valueOf(i1 + (n * 12 + 4)));
      ArrayList localArrayList2 = (ArrayList)this.f.get(str1);
      ArrayList localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        this.f.put(str1, localArrayList1);
      }
      localArrayList1.add(str2);
      k += 1;
      m += 4;
    }
    if (this.d != null)
      f(this.d);
  }

  public String c(int paramInt)
  {
    int k = paramInt;
    while (true)
    {
      try
      {
        if (k >= this.a.length)
          break;
        if (this.a[k] == 0)
        {
          if (paramInt == k - paramInt)
            return "";
          String str = new String(this.a, paramInt, k - paramInt);
          return str;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        return "";
      }
      k += 1;
    }
    return "";
  }

  // ERROR //
  public void c(String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: getstatic 159	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   6: invokevirtual 165	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   9: ldc 167
    //   11: invokevirtual 173	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   14: astore_3
    //   15: new 175	java/io/ByteArrayOutputStream
    //   18: dup
    //   19: invokespecial 176	java/io/ByteArrayOutputStream:<init>	()V
    //   22: astore 4
    //   24: sipush 1024
    //   27: newarray byte
    //   29: astore 5
    //   31: aload_3
    //   32: aload 5
    //   34: iconst_0
    //   35: sipush 1024
    //   38: invokevirtual 182	java/io/InputStream:read	([BII)I
    //   41: istore_2
    //   42: iload_2
    //   43: iconst_m1
    //   44: if_icmpeq +48 -> 92
    //   47: aload 4
    //   49: aload 5
    //   51: iconst_0
    //   52: iload_2
    //   53: invokevirtual 185	java/io/ByteArrayOutputStream:write	([BII)V
    //   56: goto -25 -> 31
    //   59: astore 5
    //   61: aload_3
    //   62: astore_1
    //   63: aload 4
    //   65: astore_3
    //   66: aload 5
    //   68: astore 4
    //   70: aload 4
    //   72: invokevirtual 153	java/lang/Exception:printStackTrace	()V
    //   75: aload_3
    //   76: ifnull +7 -> 83
    //   79: aload_3
    //   80: invokevirtual 188	java/io/ByteArrayOutputStream:close	()V
    //   83: aload_1
    //   84: ifnull +7 -> 91
    //   87: aload_1
    //   88: invokevirtual 189	java/io/InputStream:close	()V
    //   91: return
    //   92: aload_0
    //   93: aload 4
    //   95: invokevirtual 193	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   98: putfield 83	org/vidogram/a/b:a	[B
    //   101: aload_0
    //   102: aload_0
    //   103: getfield 83	org/vidogram/a/b:a	[B
    //   106: invokestatic 197	java/nio/ByteBuffer:wrap	([B)Ljava/nio/ByteBuffer;
    //   109: putfield 85	org/vidogram/a/b:b	Ljava/nio/ByteBuffer;
    //   112: aload_0
    //   113: getfield 85	org/vidogram/a/b:b	Ljava/nio/ByteBuffer;
    //   116: getstatic 203	java/nio/ByteOrder:LITTLE_ENDIAN	Ljava/nio/ByteOrder;
    //   119: invokevirtual 207	java/nio/ByteBuffer:order	(Ljava/nio/ByteOrder;)Ljava/nio/ByteBuffer;
    //   122: pop
    //   123: aload 4
    //   125: ifnull +8 -> 133
    //   128: aload 4
    //   130: invokevirtual 188	java/io/ByteArrayOutputStream:close	()V
    //   133: aload_3
    //   134: ifnull +7 -> 141
    //   137: aload_3
    //   138: invokevirtual 189	java/io/InputStream:close	()V
    //   141: aload_1
    //   142: ifnull +156 -> 298
    //   145: aload_1
    //   146: invokevirtual 208	java/lang/String:length	()I
    //   149: ifeq +149 -> 298
    //   152: aload_0
    //   153: aload_1
    //   154: putfield 106	org/vidogram/a/b:c	Ljava/lang/String;
    //   157: aload_0
    //   158: new 116	java/util/HashMap
    //   161: dup
    //   162: sipush 255
    //   165: invokespecial 211	java/util/HashMap:<init>	(I)V
    //   168: putfield 122	org/vidogram/a/b:e	Ljava/util/HashMap;
    //   171: aload_0
    //   172: new 116	java/util/HashMap
    //   175: dup
    //   176: sipush 255
    //   179: invokespecial 211	java/util/HashMap:<init>	(I)V
    //   182: putfield 130	org/vidogram/a/b:f	Ljava/util/HashMap;
    //   185: aload_0
    //   186: new 116	java/util/HashMap
    //   189: dup
    //   190: bipush 10
    //   192: invokespecial 211	java/util/HashMap:<init>	(I)V
    //   195: putfield 213	org/vidogram/a/b:g	Ljava/util/HashMap;
    //   198: aload_0
    //   199: new 116	java/util/HashMap
    //   202: dup
    //   203: sipush 255
    //   206: invokespecial 211	java/util/HashMap:<init>	(I)V
    //   209: putfield 114	org/vidogram/a/b:h	Ljava/util/HashMap;
    //   212: aload_0
    //   213: invokevirtual 215	org/vidogram/a/b:b	()V
    //   216: aload_0
    //   217: iconst_1
    //   218: putfield 34	org/vidogram/a/b:i	Z
    //   221: return
    //   222: astore 4
    //   224: aload 4
    //   226: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   229: goto -96 -> 133
    //   232: astore_3
    //   233: aload_3
    //   234: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   237: goto -96 -> 141
    //   240: astore_3
    //   241: aload_3
    //   242: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   245: goto -162 -> 83
    //   248: astore_1
    //   249: aload_1
    //   250: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   253: return
    //   254: astore_1
    //   255: aconst_null
    //   256: astore 4
    //   258: aconst_null
    //   259: astore_3
    //   260: aload 4
    //   262: ifnull +8 -> 270
    //   265: aload 4
    //   267: invokevirtual 188	java/io/ByteArrayOutputStream:close	()V
    //   270: aload_3
    //   271: ifnull +7 -> 278
    //   274: aload_3
    //   275: invokevirtual 189	java/io/InputStream:close	()V
    //   278: aload_1
    //   279: athrow
    //   280: astore 4
    //   282: aload 4
    //   284: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   287: goto -17 -> 270
    //   290: astore_3
    //   291: aload_3
    //   292: invokestatic 220	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   295: goto -17 -> 278
    //   298: aload_0
    //   299: invokestatic 226	java/util/Locale:getDefault	()Ljava/util/Locale;
    //   302: invokevirtual 229	java/util/Locale:getCountry	()Ljava/lang/String;
    //   305: invokevirtual 232	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   308: putfield 106	org/vidogram/a/b:c	Ljava/lang/String;
    //   311: goto -154 -> 157
    //   314: astore_1
    //   315: aconst_null
    //   316: astore 4
    //   318: goto -58 -> 260
    //   321: astore_1
    //   322: goto -62 -> 260
    //   325: astore 4
    //   327: aload_1
    //   328: astore 5
    //   330: aload 4
    //   332: astore_1
    //   333: aload_3
    //   334: astore 4
    //   336: aload 5
    //   338: astore_3
    //   339: goto -79 -> 260
    //   342: astore 4
    //   344: aconst_null
    //   345: astore_3
    //   346: aload 5
    //   348: astore_1
    //   349: goto -279 -> 70
    //   352: astore 4
    //   354: aconst_null
    //   355: astore 5
    //   357: aload_3
    //   358: astore_1
    //   359: aload 5
    //   361: astore_3
    //   362: goto -292 -> 70
    //
    // Exception table:
    //   from	to	target	type
    //   24	31	59	java/lang/Exception
    //   31	42	59	java/lang/Exception
    //   47	56	59	java/lang/Exception
    //   92	123	59	java/lang/Exception
    //   128	133	222	java/lang/Exception
    //   137	141	232	java/lang/Exception
    //   79	83	240	java/lang/Exception
    //   87	91	248	java/lang/Exception
    //   3	15	254	finally
    //   265	270	280	java/lang/Exception
    //   274	278	290	java/lang/Exception
    //   15	24	314	finally
    //   24	31	321	finally
    //   31	42	321	finally
    //   47	56	321	finally
    //   92	123	321	finally
    //   70	75	325	finally
    //   3	15	342	java/lang/Exception
    //   15	24	352	java/lang/Exception
  }

  public a d(String paramString)
  {
    a locala1 = null;
    int k = 0;
    while (true)
    {
      a locala2 = locala1;
      if (k < 3)
      {
        locala2 = locala1;
        if (k < paramString.length())
        {
          locala1 = f(paramString.substring(0, k + 1));
          if (locala1 == null)
            break label46;
          locala2 = locala1;
        }
      }
      return locala2;
      label46: k += 1;
    }
  }

  public String e(String paramString)
  {
    if (!this.i);
    while (true)
    {
      return paramString;
      try
      {
        Object localObject1 = a(paramString);
        if (((String)localObject1).startsWith("+"))
        {
          localObject1 = ((String)localObject1).substring(1);
          localObject2 = d((String)localObject1);
          if (localObject2 == null)
            continue;
          localObject1 = ((a)localObject2).c((String)localObject1);
          return "+" + (String)localObject1;
        }
        Object localObject2 = f(this.d);
        if (localObject2 == null)
          continue;
        String str = ((a)localObject2).a((String)localObject1);
        if (str != null)
        {
          localObject2 = ((String)localObject1).substring(str.length());
          a locala = d((String)localObject2);
          localObject1 = localObject2;
          if (locala != null)
            localObject1 = locala.c((String)localObject2);
          if (((String)localObject1).length() == 0)
            return str;
          return String.format("%s %s", new Object[] { str, localObject1 });
        }
        localObject1 = ((a)localObject2).c((String)localObject1);
        return localObject1;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    }
    return (String)(String)paramString;
  }

  public a f(String paramString)
  {
    Object localObject2 = (a)this.g.get(paramString);
    Object localObject1 = localObject2;
    if (localObject2 == null)
    {
      Object localObject3 = (Integer)this.e.get(paramString);
      localObject1 = localObject2;
      if (localObject3 != null)
      {
        localObject2 = this.a;
        int i1 = ((Integer)localObject3).intValue();
        localObject1 = new a();
        ((a)localObject1).b = paramString;
        ((a)localObject1).a = ((ArrayList)this.f.get(paramString));
        this.g.put(paramString, localObject1);
        int i2 = b(i1);
        int k = i1 + 2 + 2;
        int i3 = b(k);
        k = k + 2 + 2;
        int i4 = b(k);
        k = k + 2 + 2;
        paramString = new ArrayList(5);
        while (true)
        {
          localObject3 = c(k);
          if (((String)localObject3).length() == 0)
            break;
          paramString.add(localObject3);
          k += ((String)localObject3).length() + 1;
        }
        ((a)localObject1).c = paramString;
        k += 1;
        paramString = new ArrayList(5);
        while (true)
        {
          localObject3 = c(k);
          if (((String)localObject3).length() == 0)
            break;
          paramString.add(localObject3);
          k += ((String)localObject3).length() + 1;
        }
        ((a)localObject1).d = paramString;
        paramString = new ArrayList(i4);
        k = i1 + i2;
        int m = 0;
        while (m < i4)
        {
          localObject3 = new d();
          ((d)localObject3).a = b(k);
          k += 2;
          int i5 = b(k);
          k += 2;
          ArrayList localArrayList = new ArrayList(i5);
          int n = 0;
          while (n < i5)
          {
            c localc = new c();
            localc.a = a(k);
            k += 4;
            localc.b = a(k);
            int i6 = k + 4;
            k = i6 + 1;
            localc.c = localObject2[i6];
            i6 = k + 1;
            localc.d = localObject2[k];
            k = i6 + 1;
            localc.e = localObject2[i6];
            i6 = k + 1;
            localc.f = localObject2[k];
            k = i6 + 1;
            localc.g = localObject2[i6];
            i6 = k + 1;
            localc.h = localObject2[k];
            int i7 = b(i6);
            k = i6 + 2;
            localc.i = c(i7 + (i1 + i2 + i3));
            i6 = localc.i.indexOf("[[");
            if (i6 != -1)
            {
              i7 = localc.i.indexOf("]]");
              localc.i = String.format("%s%s", new Object[] { localc.i.substring(0, i6), localc.i.substring(i7 + 2) });
            }
            localArrayList.add(localc);
            if (localc.j)
              ((d)localObject3).c = true;
            if (localc.k)
              ((d)localObject3).d = true;
            n += 1;
          }
          ((d)localObject3).b = localArrayList;
          paramString.add(localObject3);
          m += 1;
        }
        ((a)localObject1).e = paramString;
      }
    }
    return (a)(a)(a)localObject1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.a.b
 * JD-Core Version:    0.6.0
 */