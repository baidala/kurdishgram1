package org.vidogram.messenger;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import java.io.File;
import java.lang.reflect.Field;

public class NativeLoader
{
  private static final String LIB_NAME = "tmessages.26";
  private static final String LIB_SO_NAME = "libtmessages.26.so";
  private static final int LIB_VERSION = 26;
  private static final String LOCALE_LIB_SO_NAME = "libtmessages.26loc.so";
  private static volatile boolean nativeLoaded = false;
  private String crashPath = "";

  private static File getNativeLibraryDir(Context paramContext)
  {
    if (paramContext != null);
    while (true)
    {
      try
      {
        File localFile1 = new File((String)ApplicationInfo.class.getField("nativeLibraryDir").get(paramContext.getApplicationInfo()));
        File localFile2 = localFile1;
        if (localFile1 != null)
          continue;
        localFile2 = new File(paramContext.getApplicationInfo().dataDir, "lib");
        if (!localFile2.isDirectory())
          break;
        return localFile2;
      }
      catch (Throwable localThrowable)
      {
        localThrowable.printStackTrace();
      }
      Object localObject = null;
    }
    return null;
  }

  private static native void init(String paramString, boolean paramBoolean);

  // ERROR //
  public static void initNativeLibs(Context paramContext)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: getstatic 24	org/vidogram/messenger/NativeLoader:nativeLoaded	Z
    //   6: istore_1
    //   7: iload_1
    //   8: ifeq +7 -> 15
    //   11: ldc 2
    //   13: monitorexit
    //   14: return
    //   15: aload_0
    //   16: invokestatic 93	net/hockeyapp/android/a:a	(Landroid/content/Context;)V
    //   19: getstatic 98	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   22: ldc 100
    //   24: invokevirtual 104	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   27: ifeq +237 -> 264
    //   30: ldc 100
    //   32: astore_2
    //   33: ldc 106
    //   35: invokestatic 112	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   38: astore_3
    //   39: aload_3
    //   40: ifnull +324 -> 364
    //   43: aload_3
    //   44: ldc 114
    //   46: invokevirtual 118	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   49: ifeq +315 -> 364
    //   52: ldc 120
    //   54: astore_2
    //   55: aload_0
    //   56: invokestatic 122	org/vidogram/messenger/NativeLoader:getNativeLibraryDir	(Landroid/content/Context;)Ljava/io/File;
    //   59: astore_3
    //   60: aload_3
    //   61: ifnull +50 -> 111
    //   64: new 38	java/io/File
    //   67: dup
    //   68: aload_3
    //   69: ldc 11
    //   71: invokespecial 125	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   74: invokevirtual 128	java/io/File:exists	()Z
    //   77: ifeq +34 -> 111
    //   80: ldc 130
    //   82: invokestatic 135	org/vidogram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   85: ldc 8
    //   87: invokestatic 138	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   90: getstatic 140	net/hockeyapp/android/a:a	Ljava/lang/String;
    //   93: getstatic 145	org/vidogram/messenger/BuildVars:DEBUG_VERSION	Z
    //   96: invokestatic 147	org/vidogram/messenger/NativeLoader:init	(Ljava/lang/String;Z)V
    //   99: iconst_1
    //   100: putstatic 24	org/vidogram/messenger/NativeLoader:nativeLoaded	Z
    //   103: goto -92 -> 11
    //   106: astore_3
    //   107: aload_3
    //   108: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   111: new 38	java/io/File
    //   114: dup
    //   115: aload_0
    //   116: invokevirtual 155	android/content/Context:getFilesDir	()Ljava/io/File;
    //   119: ldc 70
    //   121: invokespecial 125	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   124: astore_3
    //   125: aload_3
    //   126: invokevirtual 158	java/io/File:mkdirs	()Z
    //   129: pop
    //   130: new 38	java/io/File
    //   133: dup
    //   134: aload_3
    //   135: ldc 17
    //   137: invokespecial 125	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   140: astore 4
    //   142: aload 4
    //   144: invokevirtual 128	java/io/File:exists	()Z
    //   147: istore_1
    //   148: iload_1
    //   149: ifeq +45 -> 194
    //   152: ldc 160
    //   154: invokestatic 135	org/vidogram/messenger/FileLog:d	(Ljava/lang/String;)V
    //   157: aload 4
    //   159: invokevirtual 164	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   162: invokestatic 167	java/lang/System:load	(Ljava/lang/String;)V
    //   165: getstatic 140	net/hockeyapp/android/a:a	Ljava/lang/String;
    //   168: getstatic 145	org/vidogram/messenger/BuildVars:DEBUG_VERSION	Z
    //   171: invokestatic 147	org/vidogram/messenger/NativeLoader:init	(Ljava/lang/String;Z)V
    //   174: iconst_1
    //   175: putstatic 24	org/vidogram/messenger/NativeLoader:nativeLoaded	Z
    //   178: goto -167 -> 11
    //   181: astore 5
    //   183: aload 5
    //   185: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   188: aload 4
    //   190: invokevirtual 170	java/io/File:delete	()Z
    //   193: pop
    //   194: new 172	java/lang/StringBuilder
    //   197: dup
    //   198: invokespecial 173	java/lang/StringBuilder:<init>	()V
    //   201: ldc 175
    //   203: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   206: aload_2
    //   207: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   210: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   213: invokestatic 184	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   216: aload_0
    //   217: aload_3
    //   218: aload 4
    //   220: aload_2
    //   221: invokestatic 188	org/vidogram/messenger/NativeLoader:loadFromZip	(Landroid/content/Context;Ljava/io/File;Ljava/io/File;Ljava/lang/String;)Z
    //   224: istore_1
    //   225: iload_1
    //   226: ifne -215 -> 11
    //   229: ldc 8
    //   231: invokestatic 138	java/lang/System:loadLibrary	(Ljava/lang/String;)V
    //   234: getstatic 140	net/hockeyapp/android/a:a	Ljava/lang/String;
    //   237: getstatic 145	org/vidogram/messenger/BuildVars:DEBUG_VERSION	Z
    //   240: invokestatic 147	org/vidogram/messenger/NativeLoader:init	(Ljava/lang/String;Z)V
    //   243: iconst_1
    //   244: putstatic 24	org/vidogram/messenger/NativeLoader:nativeLoaded	Z
    //   247: goto -236 -> 11
    //   250: astore_0
    //   251: aload_0
    //   252: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   255: goto -244 -> 11
    //   258: astore_0
    //   259: ldc 2
    //   261: monitorexit
    //   262: aload_0
    //   263: athrow
    //   264: getstatic 98	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   267: ldc 190
    //   269: invokevirtual 104	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   272: ifeq +9 -> 281
    //   275: ldc 190
    //   277: astore_2
    //   278: goto -245 -> 33
    //   281: getstatic 98	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   284: ldc 120
    //   286: invokevirtual 104	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   289: ifeq +9 -> 298
    //   292: ldc 120
    //   294: astore_2
    //   295: goto -262 -> 33
    //   298: getstatic 98	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   301: ldc 192
    //   303: invokevirtual 104	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   306: ifeq +9 -> 315
    //   309: ldc 192
    //   311: astore_2
    //   312: goto -279 -> 33
    //   315: ldc 190
    //   317: astore_2
    //   318: new 172	java/lang/StringBuilder
    //   321: dup
    //   322: invokespecial 173	java/lang/StringBuilder:<init>	()V
    //   325: ldc 194
    //   327: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   330: getstatic 98	android/os/Build:CPU_ABI	Ljava/lang/String;
    //   333: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   336: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   339: invokestatic 184	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   342: goto -309 -> 33
    //   345: astore_2
    //   346: aload_2
    //   347: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   350: ldc 190
    //   352: astore_2
    //   353: goto -320 -> 33
    //   356: astore_0
    //   357: aload_0
    //   358: invokevirtual 80	java/lang/Throwable:printStackTrace	()V
    //   361: goto -132 -> 229
    //   364: goto -309 -> 55
    //
    // Exception table:
    //   from	to	target	type
    //   85	103	106	java/lang/Error
    //   152	178	181	java/lang/Error
    //   229	247	250	java/lang/Error
    //   3	7	258	finally
    //   15	19	258	finally
    //   19	30	258	finally
    //   33	39	258	finally
    //   43	52	258	finally
    //   55	60	258	finally
    //   64	85	258	finally
    //   85	103	258	finally
    //   107	111	258	finally
    //   111	148	258	finally
    //   152	178	258	finally
    //   183	194	258	finally
    //   194	225	258	finally
    //   229	247	258	finally
    //   251	255	258	finally
    //   264	275	258	finally
    //   281	292	258	finally
    //   298	309	258	finally
    //   318	342	258	finally
    //   346	350	258	finally
    //   357	361	258	finally
    //   19	30	345	java/lang/Exception
    //   264	275	345	java/lang/Exception
    //   281	292	345	java/lang/Exception
    //   298	309	345	java/lang/Exception
    //   318	342	345	java/lang/Exception
    //   19	30	356	java/lang/Throwable
    //   33	39	356	java/lang/Throwable
    //   43	52	356	java/lang/Throwable
    //   55	60	356	java/lang/Throwable
    //   64	85	356	java/lang/Throwable
    //   85	103	356	java/lang/Throwable
    //   107	111	356	java/lang/Throwable
    //   111	148	356	java/lang/Throwable
    //   152	178	356	java/lang/Throwable
    //   183	194	356	java/lang/Throwable
    //   194	225	356	java/lang/Throwable
    //   264	275	356	java/lang/Throwable
    //   281	292	356	java/lang/Throwable
    //   298	309	356	java/lang/Throwable
    //   318	342	356	java/lang/Throwable
    //   346	350	356	java/lang/Throwable
  }

  // ERROR //
  private static boolean loadFromZip(Context paramContext, File paramFile1, File paramFile2, String paramString)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 9
    //   3: aconst_null
    //   4: astore 8
    //   6: aconst_null
    //   7: astore 7
    //   9: iconst_1
    //   10: istore 6
    //   12: aload_1
    //   13: invokevirtual 198	java/io/File:listFiles	()[Ljava/io/File;
    //   16: astore_1
    //   17: aload_1
    //   18: arraylength
    //   19: istore 5
    //   21: iconst_0
    //   22: istore 4
    //   24: iload 4
    //   26: iload 5
    //   28: if_icmpge +25 -> 53
    //   31: aload_1
    //   32: iload 4
    //   34: aaload
    //   35: invokevirtual 170	java/io/File:delete	()Z
    //   38: pop
    //   39: iload 4
    //   41: iconst_1
    //   42: iadd
    //   43: istore 4
    //   45: goto -21 -> 24
    //   48: astore_1
    //   49: aload_1
    //   50: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   53: new 200	java/util/zip/ZipFile
    //   56: dup
    //   57: aload_0
    //   58: invokevirtual 54	android/content/Context:getApplicationInfo	()Landroid/content/pm/ApplicationInfo;
    //   61: getfield 203	android/content/pm/ApplicationInfo:sourceDir	Ljava/lang/String;
    //   64: invokespecial 204	java/util/zip/ZipFile:<init>	(Ljava/lang/String;)V
    //   67: astore_0
    //   68: aload 9
    //   70: astore_1
    //   71: aload_0
    //   72: new 172	java/lang/StringBuilder
    //   75: dup
    //   76: invokespecial 173	java/lang/StringBuilder:<init>	()V
    //   79: ldc 206
    //   81: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   84: aload_3
    //   85: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   88: ldc 208
    //   90: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   93: ldc 11
    //   95: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   98: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   101: invokevirtual 212	java/util/zip/ZipFile:getEntry	(Ljava/lang/String;)Ljava/util/zip/ZipEntry;
    //   104: astore 7
    //   106: aload 7
    //   108: ifnonnull +76 -> 184
    //   111: aload 9
    //   113: astore_1
    //   114: new 86	java/lang/Exception
    //   117: dup
    //   118: new 172	java/lang/StringBuilder
    //   121: dup
    //   122: invokespecial 173	java/lang/StringBuilder:<init>	()V
    //   125: ldc 214
    //   127: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: aload_3
    //   131: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   134: ldc 208
    //   136: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: ldc 8
    //   141: invokevirtual 179	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   144: invokevirtual 182	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   147: invokespecial 215	java/lang/Exception:<init>	(Ljava/lang/String;)V
    //   150: athrow
    //   151: astore_2
    //   152: aconst_null
    //   153: astore_3
    //   154: aload_0
    //   155: astore_1
    //   156: aload_3
    //   157: astore_0
    //   158: aload_2
    //   159: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   162: aload_0
    //   163: ifnull +7 -> 170
    //   166: aload_0
    //   167: invokevirtual 220	java/io/InputStream:close	()V
    //   170: aload_1
    //   171: ifnull +7 -> 178
    //   174: aload_1
    //   175: invokevirtual 221	java/util/zip/ZipFile:close	()V
    //   178: iconst_0
    //   179: istore 6
    //   181: iload 6
    //   183: ireturn
    //   184: aload 9
    //   186: astore_1
    //   187: aload_0
    //   188: aload 7
    //   190: invokevirtual 225	java/util/zip/ZipFile:getInputStream	(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;
    //   193: astore_3
    //   194: aload_3
    //   195: astore_1
    //   196: new 227	java/io/FileOutputStream
    //   199: dup
    //   200: aload_2
    //   201: invokespecial 230	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   204: astore 7
    //   206: aload_3
    //   207: astore_1
    //   208: sipush 4096
    //   211: newarray byte
    //   213: astore 8
    //   215: aload_3
    //   216: astore_1
    //   217: aload_3
    //   218: aload 8
    //   220: invokevirtual 234	java/io/InputStream:read	([B)I
    //   223: istore 4
    //   225: iload 4
    //   227: ifle +23 -> 250
    //   230: aload_3
    //   231: astore_1
    //   232: invokestatic 239	java/lang/Thread:yield	()V
    //   235: aload_3
    //   236: astore_1
    //   237: aload 7
    //   239: aload 8
    //   241: iconst_0
    //   242: iload 4
    //   244: invokevirtual 245	java/io/OutputStream:write	([BII)V
    //   247: goto -32 -> 215
    //   250: aload_3
    //   251: astore_1
    //   252: aload 7
    //   254: invokevirtual 246	java/io/OutputStream:close	()V
    //   257: aload_3
    //   258: astore_1
    //   259: aload_2
    //   260: iconst_1
    //   261: iconst_0
    //   262: invokevirtual 250	java/io/File:setReadable	(ZZ)Z
    //   265: pop
    //   266: aload_3
    //   267: astore_1
    //   268: aload_2
    //   269: iconst_1
    //   270: iconst_0
    //   271: invokevirtual 253	java/io/File:setExecutable	(ZZ)Z
    //   274: pop
    //   275: aload_3
    //   276: astore_1
    //   277: aload_2
    //   278: iconst_1
    //   279: invokevirtual 257	java/io/File:setWritable	(Z)Z
    //   282: pop
    //   283: aload_3
    //   284: astore_1
    //   285: aload_2
    //   286: invokevirtual 164	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   289: invokestatic 167	java/lang/System:load	(Ljava/lang/String;)V
    //   292: aload_3
    //   293: astore_1
    //   294: getstatic 140	net/hockeyapp/android/a:a	Ljava/lang/String;
    //   297: getstatic 145	org/vidogram/messenger/BuildVars:DEBUG_VERSION	Z
    //   300: invokestatic 147	org/vidogram/messenger/NativeLoader:init	(Ljava/lang/String;Z)V
    //   303: aload_3
    //   304: astore_1
    //   305: iconst_1
    //   306: putstatic 24	org/vidogram/messenger/NativeLoader:nativeLoaded	Z
    //   309: aload_3
    //   310: ifnull +7 -> 317
    //   313: aload_3
    //   314: invokevirtual 220	java/io/InputStream:close	()V
    //   317: aload_0
    //   318: ifnull -137 -> 181
    //   321: aload_0
    //   322: invokevirtual 221	java/util/zip/ZipFile:close	()V
    //   325: iconst_1
    //   326: ireturn
    //   327: astore_0
    //   328: aload_0
    //   329: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   332: iconst_1
    //   333: ireturn
    //   334: astore_2
    //   335: aload_3
    //   336: astore_1
    //   337: aload_2
    //   338: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   341: goto -32 -> 309
    //   344: astore_3
    //   345: aload_0
    //   346: astore_2
    //   347: aload_3
    //   348: astore_0
    //   349: aload_1
    //   350: ifnull +7 -> 357
    //   353: aload_1
    //   354: invokevirtual 220	java/io/InputStream:close	()V
    //   357: aload_2
    //   358: ifnull +7 -> 365
    //   361: aload_2
    //   362: invokevirtual 221	java/util/zip/ZipFile:close	()V
    //   365: aload_0
    //   366: athrow
    //   367: astore_1
    //   368: aload_1
    //   369: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   372: goto -55 -> 317
    //   375: astore_0
    //   376: aload_0
    //   377: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   380: goto -210 -> 170
    //   383: astore_0
    //   384: aload_0
    //   385: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   388: goto -210 -> 178
    //   391: astore_1
    //   392: aload_1
    //   393: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   396: goto -39 -> 357
    //   399: astore_1
    //   400: aload_1
    //   401: invokestatic 151	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   404: goto -39 -> 365
    //   407: astore_0
    //   408: aconst_null
    //   409: astore_2
    //   410: aload 8
    //   412: astore_1
    //   413: goto -64 -> 349
    //   416: astore_3
    //   417: aload_1
    //   418: astore_2
    //   419: aload_0
    //   420: astore_1
    //   421: aload_3
    //   422: astore_0
    //   423: goto -74 -> 349
    //   426: astore_2
    //   427: aconst_null
    //   428: astore_0
    //   429: aload 7
    //   431: astore_1
    //   432: goto -274 -> 158
    //   435: astore_2
    //   436: aload_0
    //   437: astore_1
    //   438: aload_3
    //   439: astore_0
    //   440: goto -282 -> 158
    //
    // Exception table:
    //   from	to	target	type
    //   12	21	48	java/lang/Exception
    //   31	39	48	java/lang/Exception
    //   71	106	151	java/lang/Exception
    //   114	151	151	java/lang/Exception
    //   187	194	151	java/lang/Exception
    //   321	325	327	java/lang/Exception
    //   285	292	334	java/lang/Error
    //   294	303	334	java/lang/Error
    //   305	309	334	java/lang/Error
    //   71	106	344	finally
    //   114	151	344	finally
    //   187	194	344	finally
    //   196	206	344	finally
    //   208	215	344	finally
    //   217	225	344	finally
    //   232	235	344	finally
    //   237	247	344	finally
    //   252	257	344	finally
    //   259	266	344	finally
    //   268	275	344	finally
    //   277	283	344	finally
    //   285	292	344	finally
    //   294	303	344	finally
    //   305	309	344	finally
    //   337	341	344	finally
    //   313	317	367	java/lang/Exception
    //   166	170	375	java/lang/Exception
    //   174	178	383	java/lang/Exception
    //   353	357	391	java/lang/Exception
    //   361	365	399	java/lang/Exception
    //   53	68	407	finally
    //   158	162	416	finally
    //   53	68	426	java/lang/Exception
    //   196	206	435	java/lang/Exception
    //   208	215	435	java/lang/Exception
    //   217	225	435	java/lang/Exception
    //   232	235	435	java/lang/Exception
    //   237	247	435	java/lang/Exception
    //   252	257	435	java/lang/Exception
    //   259	266	435	java/lang/Exception
    //   268	275	435	java/lang/Exception
    //   277	283	435	java/lang/Exception
    //   285	292	435	java/lang/Exception
    //   294	303	435	java/lang/Exception
    //   305	309	435	java/lang/Exception
    //   337	341	435	java/lang/Exception
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NativeLoader
 * JD-Core Version:    0.6.0
 */