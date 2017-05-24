package org.vidogram.messenger;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputEncryptedFile;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_fileLocation;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_photoCachedSize;
import org.vidogram.tgnet.TLRPC.TL_photoSize;
import org.vidogram.tgnet.TLRPC.TL_webDocument;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.Components.AnimatedFileDrawable;

public class ImageLoader
{
  private static volatile ImageLoader Instance;
  private static byte[] bytes;
  private static byte[] bytesThumb;
  private static byte[] header = new byte[12];
  private static byte[] headerThumb = new byte[12];
  private HashMap<String, Integer> bitmapUseCounts = new HashMap();
  private DispatchQueue cacheOutQueue = new DispatchQueue("cacheOutQueue");
  private DispatchQueue cacheThumbOutQueue = new DispatchQueue("cacheThumbOutQueue");
  private int currentHttpFileLoadTasksCount = 0;
  private int currentHttpTasksCount = 0;
  private ConcurrentHashMap<String, Float> fileProgresses = new ConcurrentHashMap();
  private LinkedList<HttpFileTask> httpFileLoadTasks = new LinkedList();
  private HashMap<String, HttpFileTask> httpFileLoadTasksByKeys = new HashMap();
  private LinkedList<HttpImageTask> httpTasks = new LinkedList();
  private String ignoreRemoval = null;
  private DispatchQueue imageLoadQueue = new DispatchQueue("imageLoadQueue");
  private HashMap<String, CacheImage> imageLoadingByKeys = new HashMap();
  private HashMap<Integer, CacheImage> imageLoadingByTag = new HashMap();
  private HashMap<String, CacheImage> imageLoadingByUrl = new HashMap();
  private volatile long lastCacheOutTime = 0L;
  private int lastImageNum = 0;
  private long lastProgressUpdateTime = 0L;
  private LruCache memCache;
  private HashMap<String, Runnable> retryHttpsTasks = new HashMap();
  private File telegramPath = null;
  private HashMap<String, ThumbGenerateTask> thumbGenerateTasks = new HashMap();
  private DispatchQueue thumbGeneratingQueue = new DispatchQueue("thumbGeneratingQueue");
  private HashMap<String, ThumbGenerateInfo> waitingForQualityThumb = new HashMap();
  private HashMap<Integer, String> waitingForQualityThumbByTag = new HashMap();

  static
  {
    Instance = null;
  }

  public ImageLoader()
  {
    this.cacheOutQueue.setPriority(1);
    this.cacheThumbOutQueue.setPriority(1);
    this.thumbGeneratingQueue.setPriority(1);
    this.imageLoadQueue.setPriority(1);
    this.memCache = new LruCache(Math.min(15, ((ActivityManager)ApplicationLoader.applicationContext.getSystemService("activity")).getMemoryClass() / 7) * 1024 * 1024)
    {
      protected void entryRemoved(boolean paramBoolean, String paramString, BitmapDrawable paramBitmapDrawable1, BitmapDrawable paramBitmapDrawable2)
      {
        if ((ImageLoader.this.ignoreRemoval != null) && (paramString != null) && (ImageLoader.this.ignoreRemoval.equals(paramString)));
        do
        {
          do
          {
            return;
            paramString = (Integer)ImageLoader.this.bitmapUseCounts.get(paramString);
          }
          while ((paramString != null) && (paramString.intValue() != 0));
          paramString = paramBitmapDrawable1.getBitmap();
        }
        while (paramString.isRecycled());
        paramString.recycle();
      }

      protected int sizeOf(String paramString, BitmapDrawable paramBitmapDrawable)
      {
        return paramBitmapDrawable.getBitmap().getByteCount();
      }
    };
    FileLoader.getInstance().setDelegate(new FileLoader.FileLoaderDelegate()
    {
      public void fileDidFailedLoad(String paramString, int paramInt)
      {
        ImageLoader.this.fileProgresses.remove(paramString);
        AndroidUtilities.runOnUIThread(new Runnable(paramString, paramInt)
        {
          public void run()
          {
            ImageLoader.this.fileDidFailedLoad(this.val$location, this.val$canceled);
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { this.val$location, Integer.valueOf(this.val$canceled) });
          }
        });
      }

      public void fileDidFailedUpload(String paramString, boolean paramBoolean)
      {
        Utilities.stageQueue.postRunnable(new Runnable(paramString, paramBoolean)
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailUpload, new Object[] { ImageLoader.2.3.this.val$location, Boolean.valueOf(ImageLoader.2.3.this.val$isEncrypted) });
              }
            });
            ImageLoader.this.fileProgresses.remove(this.val$location);
          }
        });
      }

      public void fileDidLoaded(String paramString, File paramFile, int paramInt)
      {
        ImageLoader.this.fileProgresses.remove(paramString);
        AndroidUtilities.runOnUIThread(new Runnable(paramFile, paramString, paramInt)
        {
          public void run()
          {
            if ((MediaController.getInstance().canSaveToGallery()) && (ImageLoader.this.telegramPath != null) && (this.val$finalFile != null) && ((this.val$location.endsWith(".mp4")) || (this.val$location.endsWith(".jpg"))) && (this.val$finalFile.toString().startsWith(ImageLoader.this.telegramPath.toString())))
              AndroidUtilities.addMediaToGallery(this.val$finalFile.toString());
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { this.val$location });
            ImageLoader.this.fileDidLoaded(this.val$location, this.val$finalFile, this.val$type);
          }
        });
      }

      public void fileDidUploaded(String paramString, TLRPC.InputFile paramInputFile, TLRPC.InputEncryptedFile paramInputEncryptedFile, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, long paramLong)
      {
        Utilities.stageQueue.postRunnable(new Runnable(paramString, paramInputFile, paramInputEncryptedFile, paramArrayOfByte1, paramArrayOfByte2, paramLong)
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidUpload, new Object[] { ImageLoader.2.2.this.val$location, ImageLoader.2.2.this.val$inputFile, ImageLoader.2.2.this.val$inputEncryptedFile, ImageLoader.2.2.this.val$key, ImageLoader.2.2.this.val$iv, Long.valueOf(ImageLoader.2.2.this.val$totalFileSize) });
              }
            });
            ImageLoader.this.fileProgresses.remove(this.val$location);
          }
        });
      }

      public void fileLoadProgressChanged(String paramString, float paramFloat)
      {
        ImageLoader.this.fileProgresses.put(paramString, Float.valueOf(paramFloat));
        long l = System.currentTimeMillis();
        if ((ImageLoader.this.lastProgressUpdateTime == 0L) || (ImageLoader.this.lastProgressUpdateTime < l - 500L))
        {
          ImageLoader.access$2602(ImageLoader.this, l);
          AndroidUtilities.runOnUIThread(new Runnable(paramString, paramFloat)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { this.val$location, Float.valueOf(this.val$progress) });
            }
          });
        }
      }

      public void fileUploadProgressChanged(String paramString, float paramFloat, boolean paramBoolean)
      {
        ImageLoader.this.fileProgresses.put(paramString, Float.valueOf(paramFloat));
        long l = System.currentTimeMillis();
        if ((ImageLoader.this.lastProgressUpdateTime == 0L) || (ImageLoader.this.lastProgressUpdateTime < l - 500L))
        {
          ImageLoader.access$2602(ImageLoader.this, l);
          AndroidUtilities.runOnUIThread(new Runnable(paramString, paramFloat, paramBoolean)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileUploadProgressChanged, new Object[] { this.val$location, Float.valueOf(this.val$progress), Boolean.valueOf(this.val$isEncrypted) });
            }
          });
        }
      }
    });
    Object localObject1 = new BroadcastReceiver()
    {
      public void onReceive(Context paramContext, Intent paramIntent)
      {
        FileLog.e("file system changed");
        paramContext = new Runnable()
        {
          public void run()
          {
            ImageLoader.this.checkMediaPaths();
          }
        };
        if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramIntent.getAction()))
        {
          AndroidUtilities.runOnUIThread(paramContext, 1000L);
          return;
        }
        paramContext.run();
      }
    };
    Object localObject2 = new IntentFilter();
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_CHECKING");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_EJECT");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_MOUNTED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_NOFS");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_REMOVED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_SHARED");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
    ((IntentFilter)localObject2).addAction("android.intent.action.MEDIA_UNMOUNTED");
    ((IntentFilter)localObject2).addDataScheme("file");
    ApplicationLoader.applicationContext.registerReceiver((BroadcastReceiver)localObject1, (IntentFilter)localObject2);
    localObject1 = new HashMap();
    localObject2 = AndroidUtilities.getCacheDir();
    if (!((File)localObject2).isDirectory());
    try
    {
      ((File)localObject2).mkdirs();
    }
    catch (Exception localException2)
    {
      try
      {
        while (true)
        {
          new File((File)localObject2, ".nomedia").createNewFile();
          ((HashMap)localObject1).put(Integer.valueOf(4), localObject2);
          FileLoader.getInstance().setMediaDirs((HashMap)localObject1);
          checkMediaPaths();
          return;
          localException1 = localException1;
          FileLog.e(localException1);
        }
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  // ERROR //
  private boolean canMoveFiles(File paramFile1, File paramFile2, int paramInt)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 7
    //   6: iload_3
    //   7: ifne +138 -> 145
    //   10: aload 8
    //   12: astore 5
    //   14: new 315	java/io/File
    //   17: dup
    //   18: aload_1
    //   19: ldc_w 451
    //   22: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   25: astore_1
    //   26: aload 8
    //   28: astore 5
    //   30: new 315	java/io/File
    //   33: dup
    //   34: aload_2
    //   35: ldc_w 453
    //   38: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   41: astore 6
    //   43: aload_1
    //   44: astore_2
    //   45: aload 6
    //   47: astore_1
    //   48: aload 8
    //   50: astore 5
    //   52: sipush 1024
    //   55: newarray byte
    //   57: astore 9
    //   59: aload 8
    //   61: astore 5
    //   63: aload_2
    //   64: invokevirtual 330	java/io/File:createNewFile	()Z
    //   67: pop
    //   68: aload 8
    //   70: astore 5
    //   72: new 455	java/io/RandomAccessFile
    //   75: dup
    //   76: aload_2
    //   77: ldc_w 457
    //   80: invokespecial 458	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   83: astore 6
    //   85: aload 6
    //   87: aload 9
    //   89: invokevirtual 462	java/io/RandomAccessFile:write	([B)V
    //   92: aload 6
    //   94: invokevirtual 465	java/io/RandomAccessFile:close	()V
    //   97: aload 8
    //   99: astore 5
    //   101: aload_2
    //   102: aload_1
    //   103: invokevirtual 469	java/io/File:renameTo	(Ljava/io/File;)Z
    //   106: istore 4
    //   108: aload 8
    //   110: astore 5
    //   112: aload_2
    //   113: invokevirtual 472	java/io/File:delete	()Z
    //   116: pop
    //   117: aload 8
    //   119: astore 5
    //   121: aload_1
    //   122: invokevirtual 472	java/io/File:delete	()Z
    //   125: pop
    //   126: iload 4
    //   128: ifeq +160 -> 288
    //   131: iconst_0
    //   132: ifeq +11 -> 143
    //   135: new 474	java/lang/NullPointerException
    //   138: dup
    //   139: invokespecial 475	java/lang/NullPointerException:<init>	()V
    //   142: athrow
    //   143: iconst_1
    //   144: ireturn
    //   145: iload_3
    //   146: iconst_3
    //   147: if_icmpne +42 -> 189
    //   150: aload 8
    //   152: astore 5
    //   154: new 315	java/io/File
    //   157: dup
    //   158: aload_1
    //   159: ldc_w 477
    //   162: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   165: astore 6
    //   167: aload 8
    //   169: astore 5
    //   171: new 315	java/io/File
    //   174: dup
    //   175: aload_2
    //   176: ldc_w 479
    //   179: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   182: astore_1
    //   183: aload 6
    //   185: astore_2
    //   186: goto -138 -> 48
    //   189: iload_3
    //   190: iconst_1
    //   191: if_icmpne +44 -> 235
    //   194: aload 8
    //   196: astore 5
    //   198: new 315	java/io/File
    //   201: dup
    //   202: aload_1
    //   203: ldc_w 481
    //   206: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   209: astore_1
    //   210: aload 8
    //   212: astore 5
    //   214: new 315	java/io/File
    //   217: dup
    //   218: aload_2
    //   219: ldc_w 483
    //   222: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   225: astore 6
    //   227: aload_1
    //   228: astore_2
    //   229: aload 6
    //   231: astore_1
    //   232: goto -184 -> 48
    //   235: iload_3
    //   236: iconst_2
    //   237: if_icmpne +139 -> 376
    //   240: aload 8
    //   242: astore 5
    //   244: new 315	java/io/File
    //   247: dup
    //   248: aload_1
    //   249: ldc_w 485
    //   252: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   255: astore_1
    //   256: aload 8
    //   258: astore 5
    //   260: new 315	java/io/File
    //   263: dup
    //   264: aload_2
    //   265: ldc_w 487
    //   268: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   271: astore 6
    //   273: aload_1
    //   274: astore_2
    //   275: aload 6
    //   277: astore_1
    //   278: goto -230 -> 48
    //   281: astore_1
    //   282: aload_1
    //   283: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   286: iconst_1
    //   287: ireturn
    //   288: iconst_0
    //   289: ifeq +11 -> 300
    //   292: new 474	java/lang/NullPointerException
    //   295: dup
    //   296: invokespecial 475	java/lang/NullPointerException:<init>	()V
    //   299: athrow
    //   300: iconst_0
    //   301: ireturn
    //   302: astore_1
    //   303: aload_1
    //   304: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   307: goto -7 -> 300
    //   310: astore_2
    //   311: aload 7
    //   313: astore_1
    //   314: aload_1
    //   315: astore 5
    //   317: aload_2
    //   318: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   321: aload_1
    //   322: ifnull -22 -> 300
    //   325: aload_1
    //   326: invokevirtual 465	java/io/RandomAccessFile:close	()V
    //   329: goto -29 -> 300
    //   332: astore_1
    //   333: aload_1
    //   334: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   337: goto -37 -> 300
    //   340: astore_1
    //   341: aload 5
    //   343: ifnull +8 -> 351
    //   346: aload 5
    //   348: invokevirtual 465	java/io/RandomAccessFile:close	()V
    //   351: aload_1
    //   352: athrow
    //   353: astore_2
    //   354: aload_2
    //   355: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   358: goto -7 -> 351
    //   361: astore_1
    //   362: aload 6
    //   364: astore 5
    //   366: goto -25 -> 341
    //   369: astore_2
    //   370: aload 6
    //   372: astore_1
    //   373: goto -59 -> 314
    //   376: aconst_null
    //   377: astore_1
    //   378: aconst_null
    //   379: astore_2
    //   380: goto -332 -> 48
    //
    // Exception table:
    //   from	to	target	type
    //   135	143	281	java/lang/Exception
    //   292	300	302	java/lang/Exception
    //   14	26	310	java/lang/Exception
    //   30	43	310	java/lang/Exception
    //   52	59	310	java/lang/Exception
    //   63	68	310	java/lang/Exception
    //   72	85	310	java/lang/Exception
    //   101	108	310	java/lang/Exception
    //   112	117	310	java/lang/Exception
    //   121	126	310	java/lang/Exception
    //   154	167	310	java/lang/Exception
    //   171	183	310	java/lang/Exception
    //   198	210	310	java/lang/Exception
    //   214	227	310	java/lang/Exception
    //   244	256	310	java/lang/Exception
    //   260	273	310	java/lang/Exception
    //   325	329	332	java/lang/Exception
    //   14	26	340	finally
    //   30	43	340	finally
    //   52	59	340	finally
    //   63	68	340	finally
    //   72	85	340	finally
    //   101	108	340	finally
    //   112	117	340	finally
    //   121	126	340	finally
    //   154	167	340	finally
    //   171	183	340	finally
    //   198	210	340	finally
    //   214	227	340	finally
    //   244	256	340	finally
    //   260	273	340	finally
    //   317	321	340	finally
    //   346	351	353	java/lang/Exception
    //   85	97	361	finally
    //   85	97	369	java/lang/Exception
  }

  private void createLoadOperationForImageReceiver(ImageReceiver paramImageReceiver, String paramString1, String paramString2, String paramString3, TLObject paramTLObject, String paramString4, String paramString5, int paramInt1, boolean paramBoolean, int paramInt2)
  {
    if ((paramImageReceiver == null) || (paramString2 == null) || (paramString1 == null))
      return;
    Object localObject2;
    Object localObject1;
    if (paramInt2 != 0)
    {
      bool1 = true;
      localObject2 = paramImageReceiver.getTag(bool1);
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject2 = Integer.valueOf(this.lastImageNum);
        if (paramInt2 == 0)
          break label161;
      }
    }
    label161: for (boolean bool1 = true; ; bool1 = false)
    {
      paramImageReceiver.setTag((Integer)localObject2, bool1);
      this.lastImageNum += 1;
      localObject1 = localObject2;
      if (this.lastImageNum == 2147483647)
      {
        this.lastImageNum = 0;
        localObject1 = localObject2;
      }
      bool1 = paramImageReceiver.isNeedsQualityThumb();
      localObject2 = paramImageReceiver.getParentMessageObject();
      boolean bool2 = paramImageReceiver.isShouldGenerateQualityThumb();
      this.imageLoadQueue.postRunnable(new Runnable(paramInt2, paramString2, paramString1, localObject1, paramImageReceiver, paramString5, paramString4, bool1, (MessageObject)localObject2, paramTLObject, bool2, paramBoolean, paramInt1, paramString3)
      {
        public void run()
        {
          boolean bool2 = false;
          Object localObject1;
          Object localObject2;
          Object localObject3;
          int i;
          if (this.val$thumb != 2)
          {
            localObject1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(this.val$url);
            localObject2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(this.val$key);
            localObject3 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(this.val$finalTag);
            if (localObject3 != null)
              if ((localObject3 == localObject1) || (localObject3 == localObject2))
              {
                i = 1;
                if ((i != 0) || (localObject2 == null))
                  break label1419;
                ((ImageLoader.CacheImage)localObject2).addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                i = 1;
                label116: if ((i != 0) || (localObject1 == null))
                  break label1416;
                ((ImageLoader.CacheImage)localObject1).addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                i = 1;
              }
          }
          while (true)
          {
            if (i == 0)
            {
              if (this.val$httpLocation == null)
                break label637;
              if (this.val$httpLocation.startsWith("http"))
                break label1396;
              if (!this.val$httpLocation.startsWith("thumb://"))
                break label563;
              i = this.val$httpLocation.indexOf(":", 8);
              if (i < 0)
                break label1410;
            }
            label1410: for (localObject1 = new File(this.val$httpLocation.substring(i + 1)); ; localObject1 = null)
            {
              i = 1;
              localObject2 = localObject1;
              label404: boolean bool1;
              if (this.val$thumb != 2)
              {
                localObject3 = new ImageLoader.CacheImage(ImageLoader.this, null);
                if (((this.val$httpLocation != null) && (!this.val$httpLocation.startsWith("vthumb")) && (!this.val$httpLocation.startsWith("thumb")) && ((this.val$httpLocation.endsWith("mp4")) || (this.val$httpLocation.endsWith("gif")))) || (((this.val$imageLocation instanceof TLRPC.TL_webDocument)) && (((TLRPC.TL_webDocument)this.val$imageLocation).mime_type.equals("image/gif"))) || (((this.val$imageLocation instanceof TLRPC.Document)) && (MessageObject.isGifDocument((TLRPC.Document)this.val$imageLocation))))
                  ((ImageLoader.CacheImage)localObject3).animatedFile = true;
                localObject1 = localObject2;
                if (localObject2 == null)
                {
                  if ((this.val$cacheOnly) || (this.val$size == 0) || (this.val$httpLocation != null))
                    localObject1 = new File(FileLoader.getInstance().getDirectory(4), this.val$url);
                }
                else
                {
                  if (this.val$thumb == 0)
                    break label1081;
                  bool1 = true;
                  label413: ((ImageLoader.CacheImage)localObject3).thumb = bool1;
                  ((ImageLoader.CacheImage)localObject3).key = this.val$key;
                  ((ImageLoader.CacheImage)localObject3).filter = this.val$filter;
                  ((ImageLoader.CacheImage)localObject3).httpUrl = this.val$httpLocation;
                  ((ImageLoader.CacheImage)localObject3).ext = this.val$ext;
                  ((ImageLoader.CacheImage)localObject3).addImageReceiver(this.val$imageReceiver, this.val$key, this.val$filter);
                  if ((i == 0) && (!((File)localObject1).exists()))
                    break label1102;
                  ((ImageLoader.CacheImage)localObject3).finalFilePath = ((File)localObject1);
                  ((ImageLoader.CacheImage)localObject3).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject3);
                  ImageLoader.this.imageLoadingByKeys.put(this.val$key, localObject3);
                  if (this.val$thumb == 0)
                    break label1086;
                  ImageLoader.this.cacheThumbOutQueue.postRunnable(((ImageLoader.CacheImage)localObject3).cacheTask);
                }
              }
              else
              {
                label548: return;
                ((ImageLoader.CacheImage)localObject3).removeImageReceiver(this.val$imageReceiver);
                i = 0;
                break;
                label563: if (this.val$httpLocation.startsWith("vthumb://"))
                {
                  i = this.val$httpLocation.indexOf(":", 9);
                  if (i < 0)
                    break label1404;
                }
              }
              label1081: label1086: label1102: label1381: label1396: label1404: for (localObject1 = new File(this.val$httpLocation.substring(i + 1)); ; localObject1 = null)
              {
                i = 1;
                localObject2 = localObject1;
                break;
                localObject2 = new File(this.val$httpLocation);
                i = 1;
                break;
                label637: if (this.val$thumb != 0)
                {
                  if (this.val$finalIsNeedsQualityThumb)
                  {
                    localObject2 = new File(FileLoader.getInstance().getDirectory(4), "q_" + this.val$url);
                    if (!((File)localObject2).exists())
                      localObject2 = null;
                  }
                  while (true)
                  {
                    if (this.val$parentMessageObject != null)
                    {
                      if ((this.val$parentMessageObject.messageOwner.attachPath == null) || (this.val$parentMessageObject.messageOwner.attachPath.length() <= 0))
                        break label1381;
                      localObject3 = new File(this.val$parentMessageObject.messageOwner.attachPath);
                      localObject1 = localObject3;
                      if (((File)localObject3).exists());
                    }
                    for (localObject1 = null; ; localObject1 = null)
                    {
                      if (localObject1 == null)
                        localObject1 = FileLoader.getPathToMessage(this.val$parentMessageObject.messageOwner);
                      while (true)
                      {
                        if ((this.val$finalIsNeedsQualityThumb) && (localObject2 == null))
                        {
                          String str = this.val$parentMessageObject.getFileName();
                          ImageLoader.ThumbGenerateInfo localThumbGenerateInfo = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(str);
                          localObject3 = localThumbGenerateInfo;
                          if (localThumbGenerateInfo == null)
                          {
                            localObject3 = new ImageLoader.ThumbGenerateInfo(ImageLoader.this, null);
                            ImageLoader.ThumbGenerateInfo.access$3402((ImageLoader.ThumbGenerateInfo)localObject3, (TLRPC.TL_fileLocation)this.val$imageLocation);
                            ImageLoader.ThumbGenerateInfo.access$3502((ImageLoader.ThumbGenerateInfo)localObject3, this.val$filter);
                            ImageLoader.this.waitingForQualityThumb.put(str, localObject3);
                          }
                          ImageLoader.ThumbGenerateInfo.access$2908((ImageLoader.ThumbGenerateInfo)localObject3);
                          ImageLoader.this.waitingForQualityThumbByTag.put(this.val$finalTag, str);
                        }
                        if ((((File)localObject1).exists()) && (this.val$shouldGenerateQualityThumb))
                          ImageLoader.this.generateThumb(this.val$parentMessageObject.getFileType(), (File)localObject1, (TLRPC.TL_fileLocation)this.val$imageLocation, this.val$filter);
                        i = 0;
                        break;
                        if ((this.val$imageLocation instanceof TLRPC.Document))
                        {
                          if (MessageObject.isVideoDocument((TLRPC.Document)this.val$imageLocation))
                          {
                            localObject1 = new File(FileLoader.getInstance().getDirectory(2), this.val$url);
                            break label404;
                          }
                          localObject1 = new File(FileLoader.getInstance().getDirectory(3), this.val$url);
                          break label404;
                        }
                        if ((this.val$imageLocation instanceof TLRPC.TL_webDocument))
                        {
                          localObject1 = new File(FileLoader.getInstance().getDirectory(3), this.val$url);
                          break label404;
                        }
                        localObject1 = new File(FileLoader.getInstance().getDirectory(0), this.val$url);
                        break label404;
                        bool1 = false;
                        break label413;
                        ImageLoader.this.cacheOutQueue.postRunnable(((ImageLoader.CacheImage)localObject3).cacheTask);
                        return;
                        ((ImageLoader.CacheImage)localObject3).url = this.val$url;
                        ((ImageLoader.CacheImage)localObject3).location = this.val$imageLocation;
                        ImageLoader.this.imageLoadingByUrl.put(this.val$url, localObject3);
                        if (this.val$httpLocation == null)
                        {
                          if ((this.val$imageLocation instanceof TLRPC.FileLocation))
                          {
                            localObject1 = (TLRPC.FileLocation)this.val$imageLocation;
                            localObject2 = FileLoader.getInstance();
                            localObject3 = this.val$ext;
                            i = this.val$size;
                            if ((this.val$size != 0) && (((TLRPC.FileLocation)localObject1).key == null))
                            {
                              bool1 = bool2;
                              if (!this.val$cacheOnly);
                            }
                            else
                            {
                              bool1 = true;
                            }
                            ((FileLoader)localObject2).loadFile((TLRPC.FileLocation)localObject1, (String)localObject3, i, bool1);
                            return;
                          }
                          if ((this.val$imageLocation instanceof TLRPC.Document))
                          {
                            FileLoader.getInstance().loadFile((TLRPC.Document)this.val$imageLocation, true, this.val$cacheOnly);
                            return;
                          }
                          if (!(this.val$imageLocation instanceof TLRPC.TL_webDocument))
                            break label548;
                          FileLoader.getInstance().loadFile((TLRPC.TL_webDocument)this.val$imageLocation, true, this.val$cacheOnly);
                          return;
                        }
                        localObject2 = Utilities.MD5(this.val$httpLocation);
                        ((ImageLoader.CacheImage)localObject3).tempFilePath = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2 + "_temp.jpg");
                        ((ImageLoader.CacheImage)localObject3).finalFilePath = ((File)localObject1);
                        ((ImageLoader.CacheImage)localObject3).httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, (ImageLoader.CacheImage)localObject3, this.val$size);
                        ImageLoader.this.httpTasks.add(((ImageLoader.CacheImage)localObject3).httpTask);
                        ImageLoader.this.runHttpTasks(false);
                        return;
                      }
                    }
                    continue;
                    localObject2 = null;
                  }
                }
                localObject2 = null;
                i = 0;
                break;
              }
            }
            label1416: continue;
            label1419: break label116;
            i = 0;
          }
        }
      });
      return;
      bool1 = false;
      break;
    }
  }

  private void fileDidFailedLoad(String paramString, int paramInt)
  {
    if (paramInt == 1)
      return;
    this.imageLoadQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        ImageLoader.CacheImage localCacheImage = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(this.val$location);
        if (localCacheImage != null)
          localCacheImage.setImageAndClear(null);
      }
    });
  }

  private void fileDidLoaded(String paramString, File paramFile, int paramInt)
  {
    this.imageLoadQueue.postRunnable(new Runnable(paramString, paramInt, paramFile)
    {
      public void run()
      {
        int k = 0;
        Object localObject = (ImageLoader.ThumbGenerateInfo)ImageLoader.this.waitingForQualityThumb.get(this.val$location);
        if (localObject != null)
        {
          ImageLoader.this.generateThumb(this.val$type, this.val$finalFile, ImageLoader.ThumbGenerateInfo.access$3400((ImageLoader.ThumbGenerateInfo)localObject), ImageLoader.ThumbGenerateInfo.access$3500((ImageLoader.ThumbGenerateInfo)localObject));
          ImageLoader.this.waitingForQualityThumb.remove(this.val$location);
        }
        ImageLoader.CacheImage localCacheImage2 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(this.val$location);
        if (localCacheImage2 == null)
          return;
        ImageLoader.this.imageLoadingByUrl.remove(this.val$location);
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        int j;
        while (true)
        {
          j = k;
          if (i >= localCacheImage2.imageReceiverArray.size())
            break;
          String str1 = (String)localCacheImage2.keys.get(i);
          String str2 = (String)localCacheImage2.filters.get(i);
          ImageReceiver localImageReceiver = (ImageReceiver)localCacheImage2.imageReceiverArray.get(i);
          ImageLoader.CacheImage localCacheImage1 = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByKeys.get(str1);
          localObject = localCacheImage1;
          if (localCacheImage1 == null)
          {
            localObject = new ImageLoader.CacheImage(ImageLoader.this, null);
            ((ImageLoader.CacheImage)localObject).finalFilePath = this.val$finalFile;
            ((ImageLoader.CacheImage)localObject).key = str1;
            ((ImageLoader.CacheImage)localObject).httpUrl = localCacheImage2.httpUrl;
            ((ImageLoader.CacheImage)localObject).thumb = localCacheImage2.thumb;
            ((ImageLoader.CacheImage)localObject).ext = localCacheImage2.ext;
            ((ImageLoader.CacheImage)localObject).cacheTask = new ImageLoader.CacheOutTask(ImageLoader.this, (ImageLoader.CacheImage)localObject);
            ((ImageLoader.CacheImage)localObject).filter = str2;
            ((ImageLoader.CacheImage)localObject).animatedFile = localCacheImage2.animatedFile;
            ImageLoader.this.imageLoadingByKeys.put(str1, localObject);
            localArrayList.add(((ImageLoader.CacheImage)localObject).cacheTask);
          }
          ((ImageLoader.CacheImage)localObject).addImageReceiver(localImageReceiver, str1, str2);
          i += 1;
        }
        label338: if (j < localArrayList.size())
        {
          if (!localCacheImage2.thumb)
            break label381;
          ImageLoader.this.cacheThumbOutQueue.postRunnable((Runnable)localArrayList.get(j));
        }
        while (true)
        {
          j += 1;
          break label338;
          break;
          label381: ImageLoader.this.cacheOutQueue.postRunnable((Runnable)localArrayList.get(j));
        }
      }
    });
  }

  public static void fillPhotoSizeWithBytes(TLRPC.PhotoSize paramPhotoSize)
  {
    if ((paramPhotoSize == null) || (paramPhotoSize.bytes != null));
    while (true)
    {
      return;
      Object localObject = FileLoader.getPathToAttach(paramPhotoSize, true);
      try
      {
        localObject = new RandomAccessFile((File)localObject, "r");
        if ((int)((RandomAccessFile)localObject).length() >= 20000)
          continue;
        paramPhotoSize.bytes = new byte[(int)((RandomAccessFile)localObject).length()];
        ((RandomAccessFile)localObject).readFully(paramPhotoSize.bytes, 0, paramPhotoSize.bytes.length);
        return;
      }
      catch (Throwable paramPhotoSize)
      {
        FileLog.e(paramPhotoSize);
      }
    }
  }

  private void generateThumb(int paramInt, File paramFile, TLRPC.FileLocation paramFileLocation, String paramString)
  {
    if (((paramInt != 0) && (paramInt != 2) && (paramInt != 3)) || (paramFile == null) || (paramFileLocation == null));
    String str;
    do
    {
      return;
      str = FileLoader.getAttachFileName(paramFileLocation);
    }
    while ((ThumbGenerateTask)this.thumbGenerateTasks.get(str) != null);
    paramFile = new ThumbGenerateTask(paramInt, paramFile, paramFileLocation, paramString);
    this.thumbGeneratingQueue.postRunnable(paramFile);
  }

  public static String getHttpUrlExtension(String paramString1, String paramString2)
  {
    String str = null;
    int i = paramString1.lastIndexOf('.');
    if (i != -1)
      str = paramString1.substring(i + 1);
    if ((str == null) || (str.length() == 0) || (str.length() > 4))
      return paramString2;
    return str;
  }

  public static ImageLoader getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        ImageLoader localImageLoader = Instance;
        localObject1 = localImageLoader;
        if (localImageLoader == null)
        {
          localObject1 = new ImageLoader();
          Instance = (ImageLoader)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (ImageLoader)localObject2;
  }

  private void httpFileLoadError(String paramString)
  {
    this.imageLoadQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        ImageLoader.CacheImage localCacheImage = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByUrl.get(this.val$location);
        if (localCacheImage == null)
          return;
        ImageLoader.HttpImageTask localHttpImageTask = localCacheImage.httpTask;
        localCacheImage.httpTask = new ImageLoader.HttpImageTask(ImageLoader.this, ImageLoader.HttpImageTask.access$300(localHttpImageTask), ImageLoader.HttpImageTask.access$3900(localHttpImageTask));
        ImageLoader.this.httpTasks.add(localCacheImage.httpTask);
        ImageLoader.this.runHttpTasks(false);
      }
    });
  }

  // ERROR //
  public static Bitmap loadBitmap(String paramString, android.net.Uri paramUri, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 11
    //   3: aconst_null
    //   4: astore 12
    //   6: new 574	android/graphics/BitmapFactory$Options
    //   9: dup
    //   10: invokespecial 575	android/graphics/BitmapFactory$Options:<init>	()V
    //   13: astore 13
    //   15: aload 13
    //   17: iconst_1
    //   18: putfield 579	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   21: aload_0
    //   22: ifnonnull +291 -> 313
    //   25: aload_1
    //   26: ifnull +287 -> 313
    //   29: aload_1
    //   30: invokevirtual 585	android/net/Uri:getScheme	()Ljava/lang/String;
    //   33: ifnull +280 -> 313
    //   36: aload_1
    //   37: invokevirtual 585	android/net/Uri:getScheme	()Ljava/lang/String;
    //   40: ldc_w 300
    //   43: invokevirtual 589	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   46: ifeq +248 -> 294
    //   49: aload_1
    //   50: invokevirtual 592	android/net/Uri:getPath	()Ljava/lang/String;
    //   53: astore_0
    //   54: aload_0
    //   55: ifnull +261 -> 316
    //   58: aload_0
    //   59: aload 13
    //   61: invokestatic 598	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   64: pop
    //   65: aconst_null
    //   66: astore 9
    //   68: aload 13
    //   70: getfield 601	android/graphics/BitmapFactory$Options:outWidth	I
    //   73: i2f
    //   74: fstore 5
    //   76: aload 13
    //   78: getfield 604	android/graphics/BitmapFactory$Options:outHeight	I
    //   81: i2f
    //   82: fstore 6
    //   84: iload 4
    //   86: ifeq +282 -> 368
    //   89: fload 5
    //   91: fload_2
    //   92: fdiv
    //   93: fload 6
    //   95: fload_3
    //   96: fdiv
    //   97: invokestatic 608	java/lang/Math:max	(FF)F
    //   100: fstore_2
    //   101: fload_2
    //   102: fstore_3
    //   103: fload_2
    //   104: fconst_1
    //   105: fcmpg
    //   106: ifge +5 -> 111
    //   109: fconst_1
    //   110: fstore_3
    //   111: aload 13
    //   113: iconst_0
    //   114: putfield 579	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
    //   117: aload 13
    //   119: fload_3
    //   120: f2i
    //   121: putfield 611	android/graphics/BitmapFactory$Options:inSampleSize	I
    //   124: getstatic 616	android/os/Build$VERSION:SDK_INT	I
    //   127: bipush 21
    //   129: if_icmpge +254 -> 383
    //   132: iconst_1
    //   133: istore 4
    //   135: aload 13
    //   137: iload 4
    //   139: putfield 619	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   142: aload_0
    //   143: ifnull +246 -> 389
    //   146: aload_0
    //   147: astore 8
    //   149: aload 8
    //   151: ifnull +561 -> 712
    //   154: new 621	android/media/ExifInterface
    //   157: dup
    //   158: aload 8
    //   160: invokespecial 622	android/media/ExifInterface:<init>	(Ljava/lang/String;)V
    //   163: ldc_w 624
    //   166: iconst_1
    //   167: invokevirtual 628	android/media/ExifInterface:getAttributeInt	(Ljava/lang/String;I)I
    //   170: istore 7
    //   172: new 630	android/graphics/Matrix
    //   175: dup
    //   176: invokespecial 631	android/graphics/Matrix:<init>	()V
    //   179: astore 8
    //   181: iload 7
    //   183: tableswitch	default:+37 -> 220, 3:+245->428, 4:+37->220, 5:+37->220, 6:+219->402, 7:+37->220, 8:+257->440
    //   221: iconst_5
    //   222: astore 10
    //   224: aload_0
    //   225: ifnull +339 -> 564
    //   228: aload_0
    //   229: aload 13
    //   231: invokestatic 598	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   234: astore_1
    //   235: aload_1
    //   236: astore 8
    //   238: aload_1
    //   239: ifnull +52 -> 291
    //   242: aload 13
    //   244: getfield 619	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   247: ifeq +8 -> 255
    //   250: aload_1
    //   251: invokestatic 637	org/vidogram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   254: pop
    //   255: aload_1
    //   256: iconst_0
    //   257: iconst_0
    //   258: aload_1
    //   259: invokevirtual 642	android/graphics/Bitmap:getWidth	()I
    //   262: aload_1
    //   263: invokevirtual 645	android/graphics/Bitmap:getHeight	()I
    //   266: aload 10
    //   268: iconst_1
    //   269: invokestatic 651	org/vidogram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   272: astore 9
    //   274: aload_1
    //   275: astore 8
    //   277: aload 9
    //   279: aload_1
    //   280: if_acmpeq +11 -> 291
    //   283: aload_1
    //   284: invokevirtual 654	android/graphics/Bitmap:recycle	()V
    //   287: aload 9
    //   289: astore 8
    //   291: aload 8
    //   293: areturn
    //   294: aload_1
    //   295: invokestatic 657	org/vidogram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   298: astore 8
    //   300: aload 8
    //   302: astore_0
    //   303: goto -249 -> 54
    //   306: astore 8
    //   308: aload 8
    //   310: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   313: goto -259 -> 54
    //   316: aload_1
    //   317: ifnull +407 -> 724
    //   320: getstatic 235	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   323: invokevirtual 661	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   326: aload_1
    //   327: invokevirtual 667	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   330: astore 8
    //   332: aload 8
    //   334: aconst_null
    //   335: aload 13
    //   337: invokestatic 671	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   340: pop
    //   341: aload 8
    //   343: invokevirtual 674	java/io/InputStream:close	()V
    //   346: getstatic 235	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   349: invokevirtual 661	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
    //   352: aload_1
    //   353: invokevirtual 667	android/content/ContentResolver:openInputStream	(Landroid/net/Uri;)Ljava/io/InputStream;
    //   356: astore 9
    //   358: goto -290 -> 68
    //   361: astore_0
    //   362: aload_0
    //   363: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   366: aconst_null
    //   367: areturn
    //   368: fload 5
    //   370: fload_2
    //   371: fdiv
    //   372: fload 6
    //   374: fload_3
    //   375: fdiv
    //   376: invokestatic 676	java/lang/Math:min	(FF)F
    //   379: fstore_2
    //   380: goto -279 -> 101
    //   383: iconst_0
    //   384: istore 4
    //   386: goto -251 -> 135
    //   389: aload_1
    //   390: ifnull +328 -> 718
    //   393: aload_1
    //   394: invokestatic 657	org/vidogram/messenger/AndroidUtilities:getPath	(Landroid/net/Uri;)Ljava/lang/String;
    //   397: astore 8
    //   399: goto -250 -> 149
    //   402: aload 8
    //   404: ldc_w 677
    //   407: invokevirtual 681	android/graphics/Matrix:postRotate	(F)Z
    //   410: pop
    //   411: goto -191 -> 220
    //   414: astore 10
    //   416: aload 10
    //   418: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   421: aload 8
    //   423: astore 10
    //   425: goto -201 -> 224
    //   428: aload 8
    //   430: ldc_w 682
    //   433: invokevirtual 681	android/graphics/Matrix:postRotate	(F)Z
    //   436: pop
    //   437: goto -217 -> 220
    //   440: aload 8
    //   442: ldc_w 683
    //   445: invokevirtual 681	android/graphics/Matrix:postRotate	(F)Z
    //   448: pop
    //   449: goto -229 -> 220
    //   452: astore 8
    //   454: aconst_null
    //   455: astore_1
    //   456: aload 8
    //   458: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   461: invokestatic 685	org/vidogram/messenger/ImageLoader:getInstance	()Lorg/vidogram/messenger/ImageLoader;
    //   464: invokevirtual 688	org/vidogram/messenger/ImageLoader:clearMemory	()V
    //   467: aload_1
    //   468: astore 8
    //   470: aload_1
    //   471: ifnonnull +40 -> 511
    //   474: aload_0
    //   475: aload 13
    //   477: invokestatic 598	android/graphics/BitmapFactory:decodeFile	(Ljava/lang/String;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   480: astore_0
    //   481: aload_0
    //   482: astore 8
    //   484: aload_0
    //   485: ifnull +26 -> 511
    //   488: aload_0
    //   489: astore 8
    //   491: aload_0
    //   492: astore_1
    //   493: aload 13
    //   495: getfield 619	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   498: ifeq +13 -> 511
    //   501: aload_0
    //   502: astore_1
    //   503: aload_0
    //   504: invokestatic 637	org/vidogram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   507: pop
    //   508: aload_0
    //   509: astore 8
    //   511: aload 8
    //   513: astore_0
    //   514: aload_0
    //   515: astore 8
    //   517: aload_0
    //   518: ifnull -227 -> 291
    //   521: aload_0
    //   522: iconst_0
    //   523: iconst_0
    //   524: aload_0
    //   525: invokevirtual 642	android/graphics/Bitmap:getWidth	()I
    //   528: aload_0
    //   529: invokevirtual 645	android/graphics/Bitmap:getHeight	()I
    //   532: aload 10
    //   534: iconst_1
    //   535: invokestatic 651	org/vidogram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   538: astore_1
    //   539: aload_0
    //   540: astore 8
    //   542: aload_1
    //   543: aload_0
    //   544: if_acmpeq -253 -> 291
    //   547: aload_0
    //   548: invokevirtual 654	android/graphics/Bitmap:recycle	()V
    //   551: aload_1
    //   552: areturn
    //   553: astore 8
    //   555: aload_1
    //   556: astore_0
    //   557: aload 8
    //   559: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   562: aload_0
    //   563: areturn
    //   564: aload 12
    //   566: astore 8
    //   568: aload_1
    //   569: ifnull -278 -> 291
    //   572: aload 11
    //   574: astore_0
    //   575: aload 9
    //   577: aconst_null
    //   578: aload 13
    //   580: invokestatic 671	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   583: astore_1
    //   584: aload_1
    //   585: astore_0
    //   586: aload_1
    //   587: ifnull +58 -> 645
    //   590: aload_1
    //   591: astore_0
    //   592: aload 13
    //   594: getfield 619	android/graphics/BitmapFactory$Options:inPurgeable	Z
    //   597: ifeq +10 -> 607
    //   600: aload_1
    //   601: astore_0
    //   602: aload_1
    //   603: invokestatic 637	org/vidogram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
    //   606: pop
    //   607: aload_1
    //   608: astore_0
    //   609: aload_1
    //   610: iconst_0
    //   611: iconst_0
    //   612: aload_1
    //   613: invokevirtual 642	android/graphics/Bitmap:getWidth	()I
    //   616: aload_1
    //   617: invokevirtual 645	android/graphics/Bitmap:getHeight	()I
    //   620: aload 10
    //   622: iconst_1
    //   623: invokestatic 651	org/vidogram/messenger/Bitmaps:createBitmap	(Landroid/graphics/Bitmap;IIIILandroid/graphics/Matrix;Z)Landroid/graphics/Bitmap;
    //   626: astore 8
    //   628: aload_1
    //   629: astore_0
    //   630: aload 8
    //   632: aload_1
    //   633: if_acmpeq +12 -> 645
    //   636: aload_1
    //   637: astore_0
    //   638: aload_1
    //   639: invokevirtual 654	android/graphics/Bitmap:recycle	()V
    //   642: aload 8
    //   644: astore_0
    //   645: aload 9
    //   647: invokevirtual 674	java/io/InputStream:close	()V
    //   650: aload_0
    //   651: areturn
    //   652: astore_1
    //   653: aload_1
    //   654: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   657: aload_0
    //   658: areturn
    //   659: astore_1
    //   660: aload_1
    //   661: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   664: aload 9
    //   666: invokevirtual 674	java/io/InputStream:close	()V
    //   669: aload_0
    //   670: areturn
    //   671: astore_1
    //   672: aload_1
    //   673: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   676: aload_0
    //   677: areturn
    //   678: astore_0
    //   679: aload 9
    //   681: invokevirtual 674	java/io/InputStream:close	()V
    //   684: aload_0
    //   685: athrow
    //   686: astore_1
    //   687: aload_1
    //   688: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   691: goto -7 -> 684
    //   694: astore 8
    //   696: goto -139 -> 557
    //   699: astore 8
    //   701: goto -245 -> 456
    //   704: astore 10
    //   706: aconst_null
    //   707: astore 8
    //   709: goto -293 -> 416
    //   712: aconst_null
    //   713: astore 10
    //   715: goto -491 -> 224
    //   718: aconst_null
    //   719: astore 8
    //   721: goto -572 -> 149
    //   724: aconst_null
    //   725: astore 9
    //   727: goto -659 -> 68
    //
    // Exception table:
    //   from	to	target	type
    //   294	300	306	java/lang/Throwable
    //   320	358	361	java/lang/Throwable
    //   402	411	414	java/lang/Throwable
    //   428	437	414	java/lang/Throwable
    //   440	449	414	java/lang/Throwable
    //   228	235	452	java/lang/Throwable
    //   474	481	553	java/lang/Throwable
    //   493	501	553	java/lang/Throwable
    //   503	508	553	java/lang/Throwable
    //   645	650	652	java/lang/Throwable
    //   575	584	659	java/lang/Throwable
    //   592	600	659	java/lang/Throwable
    //   602	607	659	java/lang/Throwable
    //   609	628	659	java/lang/Throwable
    //   638	642	659	java/lang/Throwable
    //   664	669	671	java/lang/Throwable
    //   575	584	678	finally
    //   592	600	678	finally
    //   602	607	678	finally
    //   609	628	678	finally
    //   638	642	678	finally
    //   660	664	678	finally
    //   679	684	686	java/lang/Throwable
    //   521	539	694	java/lang/Throwable
    //   547	551	694	java/lang/Throwable
    //   242	255	699	java/lang/Throwable
    //   255	274	699	java/lang/Throwable
    //   283	287	699	java/lang/Throwable
    //   154	181	704	java/lang/Throwable
  }

  private void performReplace(String paramString1, String paramString2)
  {
    Object localObject1 = this.memCache.get(paramString1);
    if (localObject1 != null)
    {
      Object localObject2 = this.memCache.get(paramString2);
      int j = 0;
      int i = j;
      if (localObject2 != null)
      {
        i = j;
        if (((BitmapDrawable)localObject2).getBitmap() != null)
        {
          i = j;
          if (((BitmapDrawable)localObject1).getBitmap() != null)
          {
            localObject2 = ((BitmapDrawable)localObject2).getBitmap();
            Bitmap localBitmap = ((BitmapDrawable)localObject1).getBitmap();
            if (((Bitmap)localObject2).getWidth() <= localBitmap.getWidth())
            {
              i = j;
              if (((Bitmap)localObject2).getHeight() <= localBitmap.getHeight());
            }
            else
            {
              i = 1;
            }
          }
        }
      }
      if (i != 0)
        break label176;
      this.ignoreRemoval = paramString1;
      this.memCache.remove(paramString1);
      this.memCache.put(paramString2, (BitmapDrawable)localObject1);
      this.ignoreRemoval = null;
    }
    while (true)
    {
      localObject1 = (Integer)this.bitmapUseCounts.get(paramString1);
      if (localObject1 != null)
      {
        this.bitmapUseCounts.put(paramString2, localObject1);
        this.bitmapUseCounts.remove(paramString1);
      }
      return;
      label176: this.memCache.remove(paramString1);
    }
  }

  private void removeFromWaitingForThumb(Integer paramInteger)
  {
    String str = (String)this.waitingForQualityThumbByTag.get(paramInteger);
    if (str != null)
    {
      ThumbGenerateInfo localThumbGenerateInfo = (ThumbGenerateInfo)this.waitingForQualityThumb.get(str);
      if (localThumbGenerateInfo != null)
      {
        ThumbGenerateInfo.access$2910(localThumbGenerateInfo);
        if (localThumbGenerateInfo.count == 0)
          this.waitingForQualityThumb.remove(str);
      }
      this.waitingForQualityThumbByTag.remove(paramInteger);
    }
  }

  private void replaceImageInCacheInternal(String paramString1, String paramString2, TLRPC.FileLocation paramFileLocation)
  {
    ArrayList localArrayList = this.memCache.getFilterKeys(paramString1);
    if (localArrayList != null)
    {
      int i = 0;
      while (i < localArrayList.size())
      {
        String str2 = (String)localArrayList.get(i);
        String str1 = paramString1 + "@" + str2;
        str2 = paramString2 + "@" + str2;
        performReplace(str1, str2);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { str1, str2, paramFileLocation });
        i += 1;
      }
    }
    performReplace(paramString1, paramString2);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReplacedPhotoInMemCache, new Object[] { paramString1, paramString2, paramFileLocation });
  }

  private void runHttpFileLoadTasks(HttpFileTask paramHttpFileTask, int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramHttpFileTask, paramInt)
    {
      public void run()
      {
        if (this.val$oldTask != null)
          ImageLoader.access$4010(ImageLoader.this);
        Object localObject;
        if (this.val$oldTask != null)
        {
          if (this.val$reason != 1)
            break label229;
          if (!ImageLoader.HttpFileTask.access$4100(this.val$oldTask))
            break label178;
          localObject = new Runnable(new ImageLoader.HttpFileTask(ImageLoader.this, ImageLoader.HttpFileTask.access$000(this.val$oldTask), ImageLoader.HttpFileTask.access$4200(this.val$oldTask), ImageLoader.HttpFileTask.access$4300(this.val$oldTask)))
          {
            public void run()
            {
              ImageLoader.this.httpFileLoadTasks.add(this.val$newTask);
              ImageLoader.this.runHttpFileLoadTasks(null, 0);
            }
          };
          ImageLoader.this.retryHttpsTasks.put(ImageLoader.HttpFileTask.access$000(this.val$oldTask), localObject);
          AndroidUtilities.runOnUIThread((Runnable)localObject, 1000L);
        }
        while ((ImageLoader.this.currentHttpFileLoadTasksCount < 2) && (!ImageLoader.this.httpFileLoadTasks.isEmpty()))
        {
          ((ImageLoader.HttpFileTask)ImageLoader.this.httpFileLoadTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          ImageLoader.access$4008(ImageLoader.this);
          continue;
          label178: ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(this.val$oldTask));
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidFailedLoad, new Object[] { ImageLoader.HttpFileTask.access$000(this.val$oldTask), Integer.valueOf(0) });
          continue;
          label229: if (this.val$reason != 2)
            continue;
          ImageLoader.this.httpFileLoadTasksByKeys.remove(ImageLoader.HttpFileTask.access$000(this.val$oldTask));
          localObject = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(ImageLoader.HttpFileTask.access$000(this.val$oldTask)) + "." + ImageLoader.HttpFileTask.access$4300(this.val$oldTask));
          if (ImageLoader.HttpFileTask.access$4200(this.val$oldTask).renameTo((File)localObject));
          for (localObject = ((File)localObject).toString(); ; localObject = ImageLoader.HttpFileTask.access$4200(this.val$oldTask).toString())
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.httpFileDidLoaded, new Object[] { ImageLoader.HttpFileTask.access$000(this.val$oldTask), localObject });
            break;
          }
        }
      }
    });
  }

  private void runHttpTasks(boolean paramBoolean)
  {
    if (paramBoolean)
      this.currentHttpTasksCount -= 1;
    while ((this.currentHttpTasksCount < 4) && (!this.httpTasks.isEmpty()))
    {
      ((HttpImageTask)this.httpTasks.poll()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
      this.currentHttpTasksCount += 1;
    }
  }

  public static void saveMessageThumbs(TLRPC.Message paramMessage)
  {
    int i = 0;
    Object localObject2 = null;
    Object localObject1;
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      localObject2 = paramMessage.media.photo.sizes.iterator();
      do
      {
        if (!((Iterator)localObject2).hasNext())
          break;
        localObject1 = (TLRPC.PhotoSize)((Iterator)localObject2).next();
      }
      while (!(localObject1 instanceof TLRPC.TL_photoCachedSize));
    }
    while (true)
    {
      if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).bytes != null) && (((TLRPC.PhotoSize)localObject1).bytes.length != 0))
      {
        if ((((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable))
        {
          ((TLRPC.PhotoSize)localObject1).location = new TLRPC.TL_fileLocation();
          ((TLRPC.PhotoSize)localObject1).location.volume_id = -2147483648L;
          ((TLRPC.PhotoSize)localObject1).location.dc_id = -2147483648;
          ((TLRPC.PhotoSize)localObject1).location.local_id = UserConfig.lastLocalId;
          UserConfig.lastLocalId -= 1;
        }
        localObject2 = FileLoader.getPathToAttach((TLObject)localObject1, true);
        if (((File)localObject2).exists());
      }
      while (true)
      {
        try
        {
          localObject2 = new RandomAccessFile((File)localObject2, "rws");
          ((RandomAccessFile)localObject2).write(((TLRPC.PhotoSize)localObject1).bytes);
          ((RandomAccessFile)localObject2).close();
          localObject2 = new TLRPC.TL_photoSize();
          ((TLRPC.TL_photoSize)localObject2).w = ((TLRPC.PhotoSize)localObject1).w;
          ((TLRPC.TL_photoSize)localObject2).h = ((TLRPC.PhotoSize)localObject1).h;
          ((TLRPC.TL_photoSize)localObject2).location = ((TLRPC.PhotoSize)localObject1).location;
          ((TLRPC.TL_photoSize)localObject2).size = ((TLRPC.PhotoSize)localObject1).size;
          ((TLRPC.TL_photoSize)localObject2).type = ((TLRPC.PhotoSize)localObject1).type;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
            continue;
          i = 0;
          if (i >= paramMessage.media.photo.sizes.size())
            continue;
          if (!(paramMessage.media.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize))
            continue;
          paramMessage.media.photo.sizes.set(i, localObject2);
          return;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
            continue;
          localObject1 = localObject2;
          if (!(paramMessage.media.document.thumb instanceof TLRPC.TL_photoCachedSize))
            break;
          localObject1 = paramMessage.media.document.thumb;
          break;
          localObject1 = localObject2;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
            break;
          localObject1 = localObject2;
          if (paramMessage.media.webpage.photo == null)
            break;
          Iterator localIterator = paramMessage.media.webpage.photo.sizes.iterator();
          localObject1 = localObject2;
          if (!localIterator.hasNext())
            break;
          localObject1 = (TLRPC.PhotoSize)localIterator.next();
          if (!(localObject1 instanceof TLRPC.TL_photoCachedSize))
            continue;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          continue;
          i += 1;
          continue;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
            continue;
          paramMessage.media.document.thumb = localException;
          return;
          if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
            continue;
        }
        while (i < paramMessage.media.webpage.photo.sizes.size())
        {
          if ((paramMessage.media.webpage.photo.sizes.get(i) instanceof TLRPC.TL_photoCachedSize))
          {
            paramMessage.media.webpage.photo.sizes.set(i, localException);
            return;
          }
          i += 1;
        }
      }
      localObject1 = null;
    }
  }

  public static void saveMessagesThumbs(ArrayList<TLRPC.Message> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()));
    while (true)
    {
      return;
      int i = 0;
      while (i < paramArrayList.size())
      {
        saveMessageThumbs((TLRPC.Message)paramArrayList.get(i));
        i += 1;
      }
    }
  }

  public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap paramBitmap, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
  {
    return scaleAndSaveImage(paramBitmap, paramFloat1, paramFloat2, paramInt, paramBoolean, 0, 0);
  }

  public static TLRPC.PhotoSize scaleAndSaveImage(Bitmap paramBitmap, float paramFloat1, float paramFloat2, int paramInt1, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    if (paramBitmap == null)
      return null;
    float f1 = paramBitmap.getWidth();
    float f2 = paramBitmap.getHeight();
    if ((f1 == 0.0F) || (f2 == 0.0F))
      return null;
    boolean bool2 = false;
    paramFloat2 = Math.max(f1 / paramFloat1, f2 / paramFloat2);
    paramFloat1 = paramFloat2;
    boolean bool1 = bool2;
    if (paramInt2 != 0)
    {
      paramFloat1 = paramFloat2;
      bool1 = bool2;
      if (paramInt3 != 0)
        if (f1 >= paramInt2)
        {
          paramFloat1 = paramFloat2;
          bool1 = bool2;
          if (f2 >= paramInt3);
        }
        else
        {
          if ((f1 >= paramInt2) || (f2 <= paramInt3))
            break label151;
          paramFloat1 = f1 / paramInt2;
        }
    }
    while (true)
    {
      bool1 = true;
      paramInt2 = (int)(f1 / paramFloat1);
      paramInt3 = (int)(f2 / paramFloat1);
      if ((paramInt3 != 0) && (paramInt2 != 0))
        break;
      return null;
      label151: if ((f1 > paramInt2) && (f2 < paramInt3))
      {
        paramFloat1 = f2 / paramInt3;
        continue;
      }
      paramFloat1 = Math.max(f1 / paramInt2, f2 / paramInt3);
    }
    try
    {
      TLRPC.PhotoSize localPhotoSize = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool1);
      return localPhotoSize;
    }
    catch (Throwable localThrowable)
    {
      FileLog.e(localThrowable);
      getInstance().clearMemory();
      System.gc();
      try
      {
        paramBitmap = scaleAndSaveImageInternal(paramBitmap, paramInt2, paramInt3, f1, f2, paramFloat1, paramInt1, paramBoolean, bool1);
        return paramBitmap;
      }
      catch (Throwable paramBitmap)
      {
        FileLog.e(paramBitmap);
      }
    }
    return null;
  }

  private static TLRPC.PhotoSize scaleAndSaveImageInternal(Bitmap paramBitmap, int paramInt1, int paramInt2, float paramFloat1, float paramFloat2, float paramFloat3, int paramInt3, boolean paramBoolean1, boolean paramBoolean2)
  {
    Bitmap localBitmap;
    Object localObject;
    TLRPC.TL_photoSize localTL_photoSize;
    if ((paramFloat3 > 1.0F) || (paramBoolean2))
    {
      localBitmap = Bitmaps.createScaledBitmap(paramBitmap, paramInt1, paramInt2, true);
      localObject = new TLRPC.TL_fileLocation();
      ((TLRPC.TL_fileLocation)localObject).volume_id = -2147483648L;
      ((TLRPC.TL_fileLocation)localObject).dc_id = -2147483648;
      ((TLRPC.TL_fileLocation)localObject).local_id = UserConfig.lastLocalId;
      UserConfig.lastLocalId -= 1;
      localTL_photoSize = new TLRPC.TL_photoSize();
      localTL_photoSize.location = ((TLRPC.FileLocation)localObject);
      localTL_photoSize.w = localBitmap.getWidth();
      localTL_photoSize.h = localBitmap.getHeight();
      if ((localTL_photoSize.w > 100) || (localTL_photoSize.h > 100))
        break label282;
      localTL_photoSize.type = "s";
      label126: localObject = ((TLRPC.TL_fileLocation)localObject).volume_id + "_" + ((TLRPC.TL_fileLocation)localObject).local_id + ".jpg";
      localObject = new FileOutputStream(new File(FileLoader.getInstance().getDirectory(4), (String)localObject));
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, (OutputStream)localObject);
      if (!paramBoolean1)
        break label392;
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localBitmap.compress(Bitmap.CompressFormat.JPEG, paramInt3, localByteArrayOutputStream);
      localTL_photoSize.bytes = localByteArrayOutputStream.toByteArray();
      localTL_photoSize.size = localTL_photoSize.bytes.length;
      localByteArrayOutputStream.close();
    }
    while (true)
    {
      ((FileOutputStream)localObject).close();
      if (localBitmap != paramBitmap)
        localBitmap.recycle();
      return localTL_photoSize;
      localBitmap = paramBitmap;
      break;
      label282: if ((localTL_photoSize.w <= 320) && (localTL_photoSize.h <= 320))
      {
        localTL_photoSize.type = "m";
        break label126;
      }
      if ((localTL_photoSize.w <= 800) && (localTL_photoSize.h <= 800))
      {
        localTL_photoSize.type = "x";
        break label126;
      }
      if ((localTL_photoSize.w <= 1280) && (localTL_photoSize.h <= 1280))
      {
        localTL_photoSize.type = "y";
        break label126;
      }
      localTL_photoSize.type = "w";
      break label126;
      label392: localTL_photoSize.size = (int)((FileOutputStream)localObject).getChannel().size();
    }
  }

  public void cancelLoadHttpFile(String paramString)
  {
    HttpFileTask localHttpFileTask = (HttpFileTask)this.httpFileLoadTasksByKeys.get(paramString);
    if (localHttpFileTask != null)
    {
      localHttpFileTask.cancel(true);
      this.httpFileLoadTasksByKeys.remove(paramString);
      this.httpFileLoadTasks.remove(localHttpFileTask);
    }
    paramString = (Runnable)this.retryHttpsTasks.get(paramString);
    if (paramString != null)
      AndroidUtilities.cancelRunOnUIThread(paramString);
    runHttpFileLoadTasks(null, 0);
  }

  public void cancelLoadingForImageReceiver(ImageReceiver paramImageReceiver, int paramInt)
  {
    if (paramImageReceiver == null)
      return;
    this.imageLoadQueue.postRunnable(new Runnable(paramInt, paramImageReceiver)
    {
      public void run()
      {
        int j;
        int i;
        if (this.val$type == 1)
        {
          j = 1;
          i = 0;
        }
        while (true)
        {
          if (i < j)
          {
            Object localObject = this.val$imageReceiver;
            if (i == 0);
            for (boolean bool = true; ; bool = false)
            {
              localObject = ((ImageReceiver)localObject).getTag(bool);
              if (i == 0)
                ImageLoader.this.removeFromWaitingForThumb((Integer)localObject);
              if (localObject != null)
              {
                localObject = (ImageLoader.CacheImage)ImageLoader.this.imageLoadingByTag.get(localObject);
                if (localObject != null)
                  ((ImageLoader.CacheImage)localObject).removeImageReceiver(this.val$imageReceiver);
              }
              i += 1;
              break;
              if (this.val$type != 2)
                break label114;
              j = 2;
              i = 1;
              break;
            }
          }
          return;
          label114: j = 2;
          i = 0;
        }
      }
    });
  }

  public void checkMediaPaths()
  {
    this.cacheOutQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable(ImageLoader.this.createMediaPaths())
        {
          public void run()
          {
            FileLoader.getInstance().setMediaDirs(this.val$paths);
          }
        });
      }
    });
  }

  public void clearMemory()
  {
    this.memCache.evictAll();
  }

  // ERROR //
  public HashMap<Integer, File> createMediaPaths()
  {
    // Byte code:
    //   0: new 163	java/util/HashMap
    //   3: dup
    //   4: invokespecial 164	java/util/HashMap:<init>	()V
    //   7: astore_2
    //   8: invokestatic 313	org/vidogram/messenger/AndroidUtilities:getCacheDir	()Ljava/io/File;
    //   11: astore_3
    //   12: aload_3
    //   13: invokevirtual 319	java/io/File:isDirectory	()Z
    //   16: ifne +8 -> 24
    //   19: aload_3
    //   20: invokevirtual 322	java/io/File:mkdirs	()Z
    //   23: pop
    //   24: new 315	java/io/File
    //   27: dup
    //   28: aload_3
    //   29: ldc_w 324
    //   32: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   35: invokevirtual 330	java/io/File:createNewFile	()Z
    //   38: pop
    //   39: aload_2
    //   40: iconst_4
    //   41: invokestatic 336	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   44: aload_3
    //   45: invokevirtual 340	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   48: pop
    //   49: new 730	java/lang/StringBuilder
    //   52: dup
    //   53: invokespecial 731	java/lang/StringBuilder:<init>	()V
    //   56: ldc_w 997
    //   59: invokevirtual 735	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   62: aload_3
    //   63: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   66: invokevirtual 740	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   72: ldc_w 1004
    //   75: invokestatic 1009	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   78: invokevirtual 1012	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   81: ifeq +449 -> 530
    //   84: aload_0
    //   85: new 315	java/io/File
    //   88: dup
    //   89: invokestatic 1015	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   92: ldc_w 1017
    //   95: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   98: putfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   101: aload_0
    //   102: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   105: invokevirtual 322	java/io/File:mkdirs	()Z
    //   108: pop
    //   109: aload_0
    //   110: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   113: invokevirtual 319	java/io/File:isDirectory	()Z
    //   116: istore_1
    //   117: iload_1
    //   118: ifeq +339 -> 457
    //   121: new 315	java/io/File
    //   124: dup
    //   125: aload_0
    //   126: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   129: ldc_w 1019
    //   132: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   135: astore 4
    //   137: aload 4
    //   139: invokevirtual 1022	java/io/File:mkdir	()Z
    //   142: pop
    //   143: aload 4
    //   145: invokevirtual 319	java/io/File:isDirectory	()Z
    //   148: ifeq +49 -> 197
    //   151: aload_0
    //   152: aload_3
    //   153: aload 4
    //   155: iconst_0
    //   156: invokespecial 1024	org/vidogram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   159: ifeq +38 -> 197
    //   162: aload_2
    //   163: iconst_0
    //   164: invokestatic 336	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   167: aload 4
    //   169: invokevirtual 340	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   172: pop
    //   173: new 730	java/lang/StringBuilder
    //   176: dup
    //   177: invokespecial 731	java/lang/StringBuilder:<init>	()V
    //   180: ldc_w 1026
    //   183: invokevirtual 735	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   186: aload 4
    //   188: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   191: invokevirtual 740	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   194: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   197: new 315	java/io/File
    //   200: dup
    //   201: aload_0
    //   202: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   205: ldc_w 1028
    //   208: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   211: astore 4
    //   213: aload 4
    //   215: invokevirtual 1022	java/io/File:mkdir	()Z
    //   218: pop
    //   219: aload 4
    //   221: invokevirtual 319	java/io/File:isDirectory	()Z
    //   224: ifeq +49 -> 273
    //   227: aload_0
    //   228: aload_3
    //   229: aload 4
    //   231: iconst_2
    //   232: invokespecial 1024	org/vidogram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   235: ifeq +38 -> 273
    //   238: aload_2
    //   239: iconst_2
    //   240: invokestatic 336	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   243: aload 4
    //   245: invokevirtual 340	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   248: pop
    //   249: new 730	java/lang/StringBuilder
    //   252: dup
    //   253: invokespecial 731	java/lang/StringBuilder:<init>	()V
    //   256: ldc_w 1030
    //   259: invokevirtual 735	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   262: aload 4
    //   264: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   267: invokevirtual 740	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   270: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   273: new 315	java/io/File
    //   276: dup
    //   277: aload_0
    //   278: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   281: ldc_w 1032
    //   284: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   287: astore 4
    //   289: aload 4
    //   291: invokevirtual 1022	java/io/File:mkdir	()Z
    //   294: pop
    //   295: aload 4
    //   297: invokevirtual 319	java/io/File:isDirectory	()Z
    //   300: ifeq +65 -> 365
    //   303: aload_0
    //   304: aload_3
    //   305: aload 4
    //   307: iconst_1
    //   308: invokespecial 1024	org/vidogram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   311: ifeq +54 -> 365
    //   314: new 315	java/io/File
    //   317: dup
    //   318: aload 4
    //   320: ldc_w 324
    //   323: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   326: invokevirtual 330	java/io/File:createNewFile	()Z
    //   329: pop
    //   330: aload_2
    //   331: iconst_1
    //   332: invokestatic 336	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   335: aload 4
    //   337: invokevirtual 340	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   340: pop
    //   341: new 730	java/lang/StringBuilder
    //   344: dup
    //   345: invokespecial 731	java/lang/StringBuilder:<init>	()V
    //   348: ldc_w 1034
    //   351: invokevirtual 735	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   354: aload 4
    //   356: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   359: invokevirtual 740	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   362: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   365: new 315	java/io/File
    //   368: dup
    //   369: aload_0
    //   370: getfield 225	org/vidogram/messenger/ImageLoader:telegramPath	Ljava/io/File;
    //   373: ldc_w 1036
    //   376: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   379: astore 4
    //   381: aload 4
    //   383: invokevirtual 1022	java/io/File:mkdir	()Z
    //   386: pop
    //   387: aload 4
    //   389: invokevirtual 319	java/io/File:isDirectory	()Z
    //   392: ifeq +65 -> 457
    //   395: aload_0
    //   396: aload_3
    //   397: aload 4
    //   399: iconst_3
    //   400: invokespecial 1024	org/vidogram/messenger/ImageLoader:canMoveFiles	(Ljava/io/File;Ljava/io/File;I)Z
    //   403: ifeq +54 -> 457
    //   406: new 315	java/io/File
    //   409: dup
    //   410: aload 4
    //   412: ldc_w 324
    //   415: invokespecial 327	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   418: invokevirtual 330	java/io/File:createNewFile	()Z
    //   421: pop
    //   422: aload_2
    //   423: iconst_3
    //   424: invokestatic 336	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   427: aload 4
    //   429: invokevirtual 340	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   432: pop
    //   433: new 730	java/lang/StringBuilder
    //   436: dup
    //   437: invokespecial 731	java/lang/StringBuilder:<init>	()V
    //   440: ldc_w 1038
    //   443: invokevirtual 735	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   446: aload 4
    //   448: invokevirtual 1000	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   451: invokevirtual 740	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   454: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   457: invokestatic 1043	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   460: invokevirtual 1046	org/vidogram/messenger/MediaController:checkSaveToGalleryFiles	()V
    //   463: aload_2
    //   464: areturn
    //   465: astore 4
    //   467: aload 4
    //   469: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   472: goto -448 -> 24
    //   475: astore 4
    //   477: aload 4
    //   479: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   482: goto -443 -> 39
    //   485: astore 4
    //   487: aload 4
    //   489: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   492: goto -295 -> 197
    //   495: astore_3
    //   496: aload_3
    //   497: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   500: aload_2
    //   501: areturn
    //   502: astore 4
    //   504: aload 4
    //   506: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   509: goto -236 -> 273
    //   512: astore 4
    //   514: aload 4
    //   516: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   519: goto -154 -> 365
    //   522: astore_3
    //   523: aload_3
    //   524: invokestatic 353	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   527: goto -70 -> 457
    //   530: ldc_w 1048
    //   533: invokestatic 1002	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   536: goto -79 -> 457
    //
    // Exception table:
    //   from	to	target	type
    //   19	24	465	java/lang/Exception
    //   24	39	475	java/lang/Exception
    //   121	197	485	java/lang/Exception
    //   72	117	495	java/lang/Exception
    //   457	463	495	java/lang/Exception
    //   487	492	495	java/lang/Exception
    //   504	509	495	java/lang/Exception
    //   514	519	495	java/lang/Exception
    //   523	527	495	java/lang/Exception
    //   530	536	495	java/lang/Exception
    //   197	273	502	java/lang/Exception
    //   273	365	512	java/lang/Exception
    //   365	457	522	java/lang/Exception
  }

  public boolean decrementUseCount(String paramString)
  {
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null)
      return true;
    if (localInteger.intValue() == 1)
    {
      this.bitmapUseCounts.remove(paramString);
      return true;
    }
    this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() - 1));
    return false;
  }

  public Float getFileProgress(String paramString)
  {
    if (paramString == null)
      return null;
    return (Float)this.fileProgresses.get(paramString);
  }

  public BitmapDrawable getImageFromMemory(String paramString)
  {
    return this.memCache.get(paramString);
  }

  public BitmapDrawable getImageFromMemory(TLObject paramTLObject, String paramString1, String paramString2)
  {
    Object localObject = null;
    if ((paramTLObject == null) && (paramString1 == null))
      return null;
    if (paramString1 != null)
      paramString1 = Utilities.MD5(paramString1);
    while (true)
    {
      paramTLObject = paramString1;
      if (paramString2 != null)
        paramTLObject = paramString1 + "@" + paramString2;
      return this.memCache.get(paramTLObject);
      if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        paramTLObject = (TLRPC.FileLocation)paramTLObject;
        paramString1 = paramTLObject.volume_id + "_" + paramTLObject.local_id;
        continue;
      }
      if ((paramTLObject instanceof TLRPC.Document))
      {
        paramTLObject = (TLRPC.Document)paramTLObject;
        if (paramTLObject.version == 0)
        {
          paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id;
          continue;
        }
        paramString1 = paramTLObject.dc_id + "_" + paramTLObject.id + "_" + paramTLObject.version;
        continue;
      }
      paramString1 = localObject;
      if (!(paramTLObject instanceof TLRPC.TL_webDocument))
        continue;
      paramString1 = Utilities.MD5(((TLRPC.TL_webDocument)paramTLObject).url);
    }
  }

  public void incrementUseCount(String paramString)
  {
    Integer localInteger = (Integer)this.bitmapUseCounts.get(paramString);
    if (localInteger == null)
    {
      this.bitmapUseCounts.put(paramString, Integer.valueOf(1));
      return;
    }
    this.bitmapUseCounts.put(paramString, Integer.valueOf(localInteger.intValue() + 1));
  }

  public boolean isInCache(String paramString)
  {
    return this.memCache.get(paramString) != null;
  }

  public boolean isLoadingHttpFile(String paramString)
  {
    return this.httpFileLoadTasksByKeys.containsKey(paramString);
  }

  public void loadHttpFile(String paramString1, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (this.httpFileLoadTasksByKeys.containsKey(paramString1)))
      return;
    paramString2 = getHttpUrlExtension(paramString1, paramString2);
    File localFile = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(paramString1) + "_temp." + paramString2);
    localFile.delete();
    paramString2 = new HttpFileTask(paramString1, localFile, paramString2);
    this.httpFileLoadTasks.add(paramString2);
    this.httpFileLoadTasksByKeys.put(paramString1, paramString2);
    runHttpFileLoadTasks(null, 0);
  }

  public void loadImageForImageReceiver(ImageReceiver paramImageReceiver)
  {
    if (paramImageReceiver == null)
      return;
    Object localObject1 = paramImageReceiver.getKey();
    Object localObject2;
    if (localObject1 != null)
    {
      localObject2 = this.memCache.get((String)localObject1);
      if (localObject2 != null)
      {
        cancelLoadingForImageReceiver(paramImageReceiver, 0);
        if (!paramImageReceiver.isForcePreview())
        {
          paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject2, (String)localObject1, false, true);
          return;
        }
      }
    }
    localObject1 = paramImageReceiver.getThumbKey();
    if (localObject1 != null)
    {
      localObject2 = this.memCache.get((String)localObject1);
      if (localObject2 != null)
      {
        paramImageReceiver.setImageBitmapByKey((BitmapDrawable)localObject2, (String)localObject1, true, true);
        cancelLoadingForImageReceiver(paramImageReceiver, 1);
      }
    }
    for (int j = 1; ; j = 0)
    {
      TLRPC.FileLocation localFileLocation = paramImageReceiver.getThumbLocation();
      TLObject localTLObject = paramImageReceiver.getImageLocation();
      String str1 = paramImageReceiver.getHttpImageLocation();
      int i = 0;
      localObject1 = null;
      Object localObject3 = null;
      localObject2 = null;
      Object localObject4 = paramImageReceiver.getExt();
      Object localObject5 = localObject4;
      if (localObject4 == null)
        localObject5 = "jpg";
      if (str1 != null)
      {
        localObject1 = Utilities.MD5(str1);
        localObject3 = (String)localObject1 + "." + getHttpUrlExtension(str1, "jpg");
        i = 0;
        localObject2 = null;
      }
      while (true)
      {
        label203: Object localObject6;
        label270: String str2;
        if (localFileLocation != null)
        {
          localObject4 = localFileLocation.volume_id + "_" + localFileLocation.local_id;
          localObject2 = (String)localObject4 + "." + (String)localObject5;
          localObject6 = paramImageReceiver.getFilter();
          str2 = paramImageReceiver.getThumbFilter();
          if ((localObject1 != null) && (localObject6 != null))
          {
            localObject1 = (String)localObject1 + "@" + (String)localObject6;
            label320: if ((localObject4 != null) && (str2 != null))
            {
              localObject4 = (String)localObject4 + "@" + str2;
              label358: if (str1 != null)
                if (j != 0)
                {
                  i = 2;
                  label369: createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject4, (String)localObject2, (String)localObject5, localFileLocation, null, str2, 0, true, i);
                  createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject1, (String)localObject3, (String)localObject5, null, str1, (String)localObject6, 0, true, 0);
                  return;
                  if (localTLObject == null)
                    break label1049;
                  if ((localTLObject instanceof TLRPC.FileLocation))
                  {
                    localObject4 = (TLRPC.FileLocation)localTLObject;
                    localObject2 = ((TLRPC.FileLocation)localObject4).volume_id + "_" + ((TLRPC.FileLocation)localObject4).local_id;
                    localObject1 = (String)localObject2 + "." + (String)localObject5;
                    if ((paramImageReceiver.getExt() == null) && (((TLRPC.FileLocation)localObject4).key == null) && ((((TLRPC.FileLocation)localObject4).volume_id != -2147483648L) || (((TLRPC.FileLocation)localObject4).local_id >= 0)))
                      break label1044;
                    i = 1;
                  }
                }
            }
          }
        }
        while (true)
        {
          if (localTLObject == localFileLocation)
          {
            localObject4 = null;
            localTLObject = null;
            localObject1 = null;
            localObject2 = localObject3;
            localObject3 = localObject4;
            break label203;
            if ((localTLObject instanceof TLRPC.TL_webDocument))
            {
              localObject1 = (TLRPC.TL_webDocument)localTLObject;
              localObject4 = FileLoader.getExtensionByMime(((TLRPC.TL_webDocument)localObject1).mime_type);
              localObject2 = Utilities.MD5(((TLRPC.TL_webDocument)localObject1).url);
              localObject1 = (String)localObject2 + "." + getHttpUrlExtension(((TLRPC.TL_webDocument)localObject1).url, (String)localObject4);
              continue;
            }
            if (!(localTLObject instanceof TLRPC.Document))
              continue;
            localObject6 = (TLRPC.Document)localTLObject;
            if ((((TLRPC.Document)localObject6).id == 0L) || (((TLRPC.Document)localObject6).dc_id == 0))
              break;
            if (((TLRPC.Document)localObject6).version == 0)
            {
              localObject1 = ((TLRPC.Document)localObject6).dc_id + "_" + ((TLRPC.Document)localObject6).id;
              label703: localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject6);
              if (localObject2 != null)
              {
                i = ((String)localObject2).lastIndexOf('.');
                if (i != -1)
                  break label902;
              }
              localObject2 = "";
              label733: localObject3 = localObject2;
              if (((String)localObject2).length() <= 1)
              {
                if ((((TLRPC.Document)localObject6).mime_type == null) || (!((TLRPC.Document)localObject6).mime_type.equals("video/mp4")))
                  break label913;
                localObject3 = ".mp4";
              }
              label773: localObject4 = (String)localObject1 + (String)localObject3;
              if (0 == 0)
                break label1038;
            }
          }
          label902: label1038: for (localObject2 = null + "." + (String)localObject5; ; localObject2 = null)
          {
            if (!MessageObject.isGifDocument((TLRPC.Document)localObject6));
            for (i = 1; ; i = 0)
            {
              localObject3 = localObject2;
              localObject2 = localObject1;
              localObject1 = localObject4;
              break;
              localObject1 = ((TLRPC.Document)localObject6).dc_id + "_" + ((TLRPC.Document)localObject6).id + "_" + ((TLRPC.Document)localObject6).version;
              break label703;
              localObject2 = ((String)localObject2).substring(i);
              break label733;
              label913: localObject3 = "";
              break label773;
            }
            i = 1;
            break label369;
            if (j != 0)
            {
              j = 2;
              createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject4, (String)localObject2, (String)localObject5, localFileLocation, null, str2, 0, true, j);
              j = paramImageReceiver.getSize();
              if ((i == 0) && (!paramImageReceiver.getCacheOnly()))
                break label1001;
            }
            label1001: for (boolean bool = true; ; bool = false)
            {
              createLoadOperationForImageReceiver(paramImageReceiver, (String)localObject1, (String)localObject3, (String)localObject5, localTLObject, null, (String)localObject6, j, bool, 0);
              return;
              j = 1;
              break;
            }
            break label358;
            break label320;
            localObject4 = null;
            break label270;
            localObject4 = localObject1;
            localObject1 = localObject2;
            localObject2 = localObject3;
            localObject3 = localObject4;
            break;
          }
          label1044: i = 0;
        }
        label1049: localObject3 = null;
        i = 0;
        localObject1 = null;
        localObject2 = null;
      }
    }
  }

  public void putImageToCache(BitmapDrawable paramBitmapDrawable, String paramString)
  {
    this.memCache.put(paramString, paramBitmapDrawable);
  }

  public void removeImage(String paramString)
  {
    this.bitmapUseCounts.remove(paramString);
    this.memCache.remove(paramString);
  }

  public void replaceImageInCache(String paramString1, String paramString2, TLRPC.FileLocation paramFileLocation, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramString1, paramString2, paramFileLocation)
      {
        public void run()
        {
          ImageLoader.this.replaceImageInCacheInternal(this.val$oldKey, this.val$newKey, this.val$newLocation);
        }
      });
      return;
    }
    replaceImageInCacheInternal(paramString1, paramString2, paramFileLocation);
  }

  private class CacheImage
  {
    protected boolean animatedFile;
    protected ImageLoader.CacheOutTask cacheTask;
    protected String ext;
    protected String filter;
    protected ArrayList<String> filters = new ArrayList();
    protected File finalFilePath;
    protected ImageLoader.HttpImageTask httpTask;
    protected String httpUrl;
    protected ArrayList<ImageReceiver> imageReceiverArray = new ArrayList();
    protected String key;
    protected ArrayList<String> keys = new ArrayList();
    protected TLObject location;
    protected File tempFilePath;
    protected boolean thumb;
    protected String url;

    private CacheImage()
    {
    }

    public void addImageReceiver(ImageReceiver paramImageReceiver, String paramString1, String paramString2)
    {
      if (this.imageReceiverArray.contains(paramImageReceiver))
        return;
      this.imageReceiverArray.add(paramImageReceiver);
      this.keys.add(paramString1);
      this.filters.add(paramString2);
      ImageLoader.this.imageLoadingByTag.put(paramImageReceiver.getTag(this.thumb), this);
    }

    public void removeImageReceiver(ImageReceiver paramImageReceiver)
    {
      int k = 0;
      int j;
      for (int i = 0; i < this.imageReceiverArray.size(); i = j + 1)
      {
        ImageReceiver localImageReceiver = (ImageReceiver)this.imageReceiverArray.get(i);
        if (localImageReceiver != null)
        {
          j = i;
          if (localImageReceiver != paramImageReceiver)
            continue;
        }
        this.imageReceiverArray.remove(i);
        this.keys.remove(i);
        this.filters.remove(i);
        if (localImageReceiver != null)
          ImageLoader.this.imageLoadingByTag.remove(localImageReceiver.getTag(this.thumb));
        j = i - 1;
      }
      if (this.imageReceiverArray.size() == 0)
      {
        i = k;
        while (i < this.imageReceiverArray.size())
        {
          ImageLoader.this.imageLoadingByTag.remove(((ImageReceiver)this.imageReceiverArray.get(i)).getTag(this.thumb));
          i += 1;
        }
        this.imageReceiverArray.clear();
        if (this.location != null)
        {
          if (!(this.location instanceof TLRPC.FileLocation))
            break label327;
          FileLoader.getInstance().cancelLoadFile((TLRPC.FileLocation)this.location, this.ext);
        }
        if (this.cacheTask != null)
        {
          if (!this.thumb)
            break label379;
          ImageLoader.this.cacheThumbOutQueue.cancelRunnable(this.cacheTask);
        }
      }
      while (true)
      {
        this.cacheTask.cancel();
        this.cacheTask = null;
        if (this.httpTask != null)
        {
          ImageLoader.this.httpTasks.remove(this.httpTask);
          this.httpTask.cancel(true);
          this.httpTask = null;
        }
        if (this.url != null)
          ImageLoader.this.imageLoadingByUrl.remove(this.url);
        if (this.key != null)
          ImageLoader.this.imageLoadingByKeys.remove(this.key);
        return;
        label327: if ((this.location instanceof TLRPC.Document))
        {
          FileLoader.getInstance().cancelLoadFile((TLRPC.Document)this.location);
          break;
        }
        if (!(this.location instanceof TLRPC.TL_webDocument))
          break;
        FileLoader.getInstance().cancelLoadFile((TLRPC.TL_webDocument)this.location);
        break;
        label379: ImageLoader.this.cacheOutQueue.cancelRunnable(this.cacheTask);
      }
    }

    public void setImageAndClear(BitmapDrawable paramBitmapDrawable)
    {
      if (paramBitmapDrawable != null)
        AndroidUtilities.runOnUIThread(new Runnable(paramBitmapDrawable, new ArrayList(this.imageReceiverArray))
        {
          public void run()
          {
            int i;
            if ((this.val$image instanceof AnimatedFileDrawable))
            {
              AnimatedFileDrawable localAnimatedFileDrawable2 = (AnimatedFileDrawable)this.val$image;
              i = 0;
              int j = 0;
              if (i < this.val$finalImageReceiverArray.size())
              {
                ImageReceiver localImageReceiver = (ImageReceiver)this.val$finalImageReceiverArray.get(i);
                if (i == 0);
                for (AnimatedFileDrawable localAnimatedFileDrawable1 = localAnimatedFileDrawable2; ; localAnimatedFileDrawable1 = localAnimatedFileDrawable2.makeCopy())
                {
                  if (localImageReceiver.setImageBitmapByKey(localAnimatedFileDrawable1, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.thumb, false))
                    j = 1;
                  i += 1;
                  break;
                }
              }
              if (j == 0)
                ((AnimatedFileDrawable)this.val$image).recycle();
            }
            while (true)
            {
              return;
              i = 0;
              while (i < this.val$finalImageReceiverArray.size())
              {
                ((ImageReceiver)this.val$finalImageReceiverArray.get(i)).setImageBitmapByKey(this.val$image, ImageLoader.CacheImage.this.key, ImageLoader.CacheImage.this.thumb, false);
                i += 1;
              }
            }
          }
        });
      int i = 0;
      while (i < this.imageReceiverArray.size())
      {
        paramBitmapDrawable = (ImageReceiver)this.imageReceiverArray.get(i);
        ImageLoader.this.imageLoadingByTag.remove(paramBitmapDrawable.getTag(this.thumb));
        i += 1;
      }
      this.imageReceiverArray.clear();
      if (this.url != null)
        ImageLoader.this.imageLoadingByUrl.remove(this.url);
      if (this.key != null)
        ImageLoader.this.imageLoadingByKeys.remove(this.key);
    }
  }

  private class CacheOutTask
    implements Runnable
  {
    private ImageLoader.CacheImage cacheImage;
    private boolean isCancelled;
    private Thread runningThread;
    private final Object sync = new Object();

    public CacheOutTask(ImageLoader.CacheImage arg2)
    {
      Object localObject;
      this.cacheImage = localObject;
    }

    private void onPostExecute(BitmapDrawable paramBitmapDrawable)
    {
      AndroidUtilities.runOnUIThread(new Runnable(paramBitmapDrawable)
      {
        public void run()
        {
          BitmapDrawable localBitmapDrawable = null;
          if ((this.val$bitmapDrawable instanceof AnimatedFileDrawable))
            localBitmapDrawable = this.val$bitmapDrawable;
          while (true)
          {
            ImageLoader.this.imageLoadQueue.postRunnable(new Runnable(localBitmapDrawable)
            {
              public void run()
              {
                ImageLoader.CacheOutTask.this.cacheImage.setImageAndClear(this.val$toSetFinal);
              }
            });
            return;
            if (this.val$bitmapDrawable == null)
              continue;
            localBitmapDrawable = ImageLoader.this.memCache.get(ImageLoader.CacheOutTask.this.cacheImage.key);
            if (localBitmapDrawable == null)
            {
              ImageLoader.this.memCache.put(ImageLoader.CacheOutTask.this.cacheImage.key, this.val$bitmapDrawable);
              localBitmapDrawable = this.val$bitmapDrawable;
              continue;
            }
            this.val$bitmapDrawable.getBitmap().recycle();
          }
        }
      });
    }

    public void cancel()
    {
      try
      {
        synchronized (this.sync)
        {
          this.isCancelled = true;
          if (this.runningThread != null)
            this.runningThread.interrupt();
          label26: return;
        }
      }
      catch (Exception localException)
      {
        break label26;
      }
    }

    // ERROR //
    public void run()
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 32	org/vidogram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   4: astore 12
      //   6: aload 12
      //   8: monitorenter
      //   9: aload_0
      //   10: invokestatic 67	java/lang/Thread:currentThread	()Ljava/lang/Thread;
      //   13: putfield 55	org/vidogram/messenger/ImageLoader$CacheOutTask:runningThread	Ljava/lang/Thread;
      //   16: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   19: pop
      //   20: aload_0
      //   21: getfield 53	org/vidogram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   24: ifeq +7 -> 31
      //   27: aload 12
      //   29: monitorexit
      //   30: return
      //   31: aload 12
      //   33: monitorexit
      //   34: aload_0
      //   35: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   38: getfield 76	org/vidogram/messenger/ImageLoader$CacheImage:animatedFile	Z
      //   41: ifeq +109 -> 150
      //   44: aload_0
      //   45: getfield 32	org/vidogram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   48: astore 12
      //   50: aload 12
      //   52: monitorenter
      //   53: aload_0
      //   54: getfield 53	org/vidogram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   57: ifeq +23 -> 80
      //   60: aload 12
      //   62: monitorexit
      //   63: return
      //   64: astore 13
      //   66: aload 12
      //   68: monitorexit
      //   69: aload 13
      //   71: athrow
      //   72: astore 13
      //   74: aload 12
      //   76: monitorexit
      //   77: aload 13
      //   79: athrow
      //   80: aload 12
      //   82: monitorexit
      //   83: aload_0
      //   84: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   87: getfield 80	org/vidogram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   90: astore 12
      //   92: aload_0
      //   93: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   96: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   99: ifnull +45 -> 144
      //   102: aload_0
      //   103: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   106: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   109: ldc 86
      //   111: invokevirtual 92	java/lang/String:equals	(Ljava/lang/Object;)Z
      //   114: ifeq +30 -> 144
      //   117: iconst_1
      //   118: istore 11
      //   120: new 94	org/vidogram/ui/Components/AnimatedFileDrawable
      //   123: dup
      //   124: aload 12
      //   126: iload 11
      //   128: invokespecial 97	org/vidogram/ui/Components/AnimatedFileDrawable:<init>	(Ljava/io/File;Z)V
      //   131: astore 12
      //   133: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   136: pop
      //   137: aload_0
      //   138: aload 12
      //   140: invokespecial 99	org/vidogram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   143: return
      //   144: iconst_0
      //   145: istore 11
      //   147: goto -27 -> 120
      //   150: aconst_null
      //   151: astore 17
      //   153: aconst_null
      //   154: astore 15
      //   156: iconst_0
      //   157: istore 8
      //   159: aconst_null
      //   160: astore 16
      //   162: aload_0
      //   163: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   166: getfield 80	org/vidogram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   169: astore 18
      //   171: iconst_0
      //   172: istore 7
      //   174: iconst_0
      //   175: istore 6
      //   177: iconst_0
      //   178: istore 10
      //   180: iconst_0
      //   181: istore 9
      //   183: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   186: bipush 19
      //   188: if_icmpge +169 -> 357
      //   191: new 107	java/io/RandomAccessFile
      //   194: dup
      //   195: aload 18
      //   197: ldc 109
      //   199: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   202: astore 13
      //   204: aload 13
      //   206: astore 12
      //   208: iload 10
      //   210: istore 6
      //   212: aload_0
      //   213: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   216: getfield 115	org/vidogram/messenger/ImageLoader$CacheImage:thumb	Z
      //   219: ifeq +252 -> 471
      //   222: aload 13
      //   224: astore 12
      //   226: iload 10
      //   228: istore 6
      //   230: invokestatic 119	org/vidogram/messenger/ImageLoader:access$1200	()[B
      //   233: astore 14
      //   235: aload 13
      //   237: astore 12
      //   239: iload 10
      //   241: istore 6
      //   243: aload 13
      //   245: aload 14
      //   247: iconst_0
      //   248: aload 14
      //   250: arraylength
      //   251: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   254: aload 13
      //   256: astore 12
      //   258: iload 10
      //   260: istore 6
      //   262: new 88	java/lang/String
      //   265: dup
      //   266: aload 14
      //   268: invokespecial 126	java/lang/String:<init>	([B)V
      //   271: invokevirtual 130	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   274: invokevirtual 130	java/lang/String:toLowerCase	()Ljava/lang/String;
      //   277: astore 14
      //   279: iload 9
      //   281: istore 5
      //   283: aload 13
      //   285: astore 12
      //   287: iload 10
      //   289: istore 6
      //   291: aload 14
      //   293: ldc 132
      //   295: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   298: ifeq +28 -> 326
      //   301: iload 9
      //   303: istore 5
      //   305: aload 13
      //   307: astore 12
      //   309: iload 10
      //   311: istore 6
      //   313: aload 14
      //   315: ldc 138
      //   317: invokevirtual 141	java/lang/String:endsWith	(Ljava/lang/String;)Z
      //   320: ifeq +6 -> 326
      //   323: iconst_1
      //   324: istore 5
      //   326: aload 13
      //   328: astore 12
      //   330: iload 5
      //   332: istore 6
      //   334: aload 13
      //   336: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   339: iload 5
      //   341: istore 7
      //   343: aload 13
      //   345: ifnull +12 -> 357
      //   348: aload 13
      //   350: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   353: iload 5
      //   355: istore 7
      //   357: aload_0
      //   358: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   361: getfield 115	org/vidogram/messenger/ImageLoader$CacheImage:thumb	Z
      //   364: ifeq +905 -> 1269
      //   367: aload_0
      //   368: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   371: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   374: ifnull +2595 -> 2969
      //   377: aload_0
      //   378: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   381: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   384: ldc 146
      //   386: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   389: ifeq +189 -> 578
      //   392: iconst_3
      //   393: istore 5
      //   395: aload_0
      //   396: getfield 27	org/vidogram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/vidogram/messenger/ImageLoader;
      //   399: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   402: invokestatic 160	org/vidogram/messenger/ImageLoader:access$1402	(Lorg/vidogram/messenger/ImageLoader;J)J
      //   405: pop2
      //   406: aload_0
      //   407: getfield 32	org/vidogram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   410: astore 12
      //   412: aload 12
      //   414: monitorenter
      //   415: aload_0
      //   416: getfield 53	org/vidogram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   419: ifeq +201 -> 620
      //   422: aload 12
      //   424: monitorexit
      //   425: return
      //   426: astore 13
      //   428: aload 12
      //   430: monitorexit
      //   431: aload 13
      //   433: athrow
      //   434: astore 13
      //   436: aconst_null
      //   437: astore 12
      //   439: aload 13
      //   441: invokestatic 166	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   444: invokestatic 71	java/lang/Thread:interrupted	()Z
      //   447: pop
      //   448: aload 12
      //   450: ifnull +2459 -> 2909
      //   453: new 168	android/graphics/drawable/BitmapDrawable
      //   456: dup
      //   457: aload 12
      //   459: invokespecial 171	android/graphics/drawable/BitmapDrawable:<init>	(Landroid/graphics/Bitmap;)V
      //   462: astore 12
      //   464: aload_0
      //   465: aload 12
      //   467: invokespecial 99	org/vidogram/messenger/ImageLoader$CacheOutTask:onPostExecute	(Landroid/graphics/drawable/BitmapDrawable;)V
      //   470: return
      //   471: aload 13
      //   473: astore 12
      //   475: iload 10
      //   477: istore 6
      //   479: invokestatic 174	org/vidogram/messenger/ImageLoader:access$1300	()[B
      //   482: astore 14
      //   484: goto -249 -> 235
      //   487: astore 12
      //   489: aload 12
      //   491: invokestatic 166	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   494: iload 5
      //   496: istore 7
      //   498: goto -141 -> 357
      //   501: astore 14
      //   503: aconst_null
      //   504: astore 13
      //   506: aload 13
      //   508: astore 12
      //   510: aload 14
      //   512: invokestatic 166	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   515: iload 6
      //   517: istore 7
      //   519: aload 13
      //   521: ifnull -164 -> 357
      //   524: aload 13
      //   526: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   529: iload 6
      //   531: istore 7
      //   533: goto -176 -> 357
      //   536: astore 12
      //   538: aload 12
      //   540: invokestatic 166	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   543: iload 6
      //   545: istore 7
      //   547: goto -190 -> 357
      //   550: astore 13
      //   552: aconst_null
      //   553: astore 12
      //   555: aload 12
      //   557: ifnull +8 -> 565
      //   560: aload 12
      //   562: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   565: aload 13
      //   567: athrow
      //   568: astore 12
      //   570: aload 12
      //   572: invokestatic 166	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   575: goto -10 -> 565
      //   578: aload_0
      //   579: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   582: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   585: ldc 176
      //   587: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   590: ifeq +9 -> 599
      //   593: iconst_2
      //   594: istore 5
      //   596: goto -201 -> 395
      //   599: aload_0
      //   600: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   603: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   606: ldc 178
      //   608: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   611: ifeq +2358 -> 2969
      //   614: iconst_1
      //   615: istore 5
      //   617: goto -222 -> 395
      //   620: aload 12
      //   622: monitorexit
      //   623: new 180	android/graphics/BitmapFactory$Options
      //   626: dup
      //   627: invokespecial 181	android/graphics/BitmapFactory$Options:<init>	()V
      //   630: astore 14
      //   632: aload 14
      //   634: iconst_1
      //   635: putfield 184	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   638: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   641: bipush 21
      //   643: if_icmpge +9 -> 652
      //   646: aload 14
      //   648: iconst_1
      //   649: putfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   652: iload 7
      //   654: ifeq +196 -> 850
      //   657: new 107	java/io/RandomAccessFile
      //   660: dup
      //   661: aload 18
      //   663: ldc 109
      //   665: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   668: astore 15
      //   670: aload 15
      //   672: invokevirtual 191	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   675: getstatic 197	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   678: lconst_0
      //   679: aload 18
      //   681: invokevirtual 202	java/io/File:length	()J
      //   684: invokevirtual 208	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   687: astore 16
      //   689: new 180	android/graphics/BitmapFactory$Options
      //   692: dup
      //   693: invokespecial 181	android/graphics/BitmapFactory$Options:<init>	()V
      //   696: astore 12
      //   698: aload 12
      //   700: iconst_1
      //   701: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   704: aconst_null
      //   705: aload 16
      //   707: aload 16
      //   709: invokevirtual 217	java/nio/ByteBuffer:limit	()I
      //   712: aload 12
      //   714: iconst_1
      //   715: invokestatic 223	org/vidogram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   718: pop
      //   719: aload 12
      //   721: getfield 226	android/graphics/BitmapFactory$Options:outWidth	I
      //   724: aload 12
      //   726: getfield 229	android/graphics/BitmapFactory$Options:outHeight	I
      //   729: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   732: invokestatic 241	org/vidogram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   735: astore 13
      //   737: aload 13
      //   739: astore 12
      //   741: aload 16
      //   743: invokevirtual 217	java/nio/ByteBuffer:limit	()I
      //   746: istore 6
      //   748: aload 13
      //   750: astore 12
      //   752: aload 14
      //   754: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   757: ifne +87 -> 844
      //   760: iconst_1
      //   761: istore 11
      //   763: aload 13
      //   765: astore 12
      //   767: aload 13
      //   769: aload 16
      //   771: iload 6
      //   773: aconst_null
      //   774: iload 11
      //   776: invokestatic 223	org/vidogram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   779: pop
      //   780: aload 13
      //   782: astore 12
      //   784: aload 15
      //   786: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   789: aload 13
      //   791: ifnonnull +187 -> 978
      //   794: aload 18
      //   796: invokevirtual 202	java/io/File:length	()J
      //   799: lconst_0
      //   800: lcmp
      //   801: ifeq +17 -> 818
      //   804: aload 13
      //   806: astore 12
      //   808: aload_0
      //   809: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   812: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   815: ifnonnull -371 -> 444
      //   818: aload 18
      //   820: invokevirtual 244	java/io/File:delete	()Z
      //   823: pop
      //   824: aload 13
      //   826: astore 12
      //   828: goto -384 -> 444
      //   831: astore 14
      //   833: aload 13
      //   835: astore 12
      //   837: aload 14
      //   839: astore 13
      //   841: goto -402 -> 439
      //   844: iconst_0
      //   845: istore 11
      //   847: goto -84 -> 763
      //   850: aload 14
      //   852: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   855: ifeq +90 -> 945
      //   858: new 107	java/io/RandomAccessFile
      //   861: dup
      //   862: aload 18
      //   864: ldc 109
      //   866: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   869: astore 15
      //   871: aload 15
      //   873: invokevirtual 245	java/io/RandomAccessFile:length	()J
      //   876: l2i
      //   877: istore 6
      //   879: invokestatic 248	org/vidogram/messenger/ImageLoader:access$1500	()[B
      //   882: ifnull +2093 -> 2975
      //   885: invokestatic 248	org/vidogram/messenger/ImageLoader:access$1500	()[B
      //   888: arraylength
      //   889: iload 6
      //   891: if_icmplt +2084 -> 2975
      //   894: invokestatic 248	org/vidogram/messenger/ImageLoader:access$1500	()[B
      //   897: astore 12
      //   899: aload 12
      //   901: astore 13
      //   903: aload 12
      //   905: ifnonnull +15 -> 920
      //   908: iload 6
      //   910: newarray byte
      //   912: astore 13
      //   914: aload 13
      //   916: invokestatic 252	org/vidogram/messenger/ImageLoader:access$1502	([B)[B
      //   919: pop
      //   920: aload 15
      //   922: aload 13
      //   924: iconst_0
      //   925: iload 6
      //   927: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   930: aload 13
      //   932: iconst_0
      //   933: iload 6
      //   935: aload 14
      //   937: invokestatic 258	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   940: astore 13
      //   942: goto -153 -> 789
      //   945: new 260	java/io/FileInputStream
      //   948: dup
      //   949: aload 18
      //   951: invokespecial 263	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   954: astore 15
      //   956: aload 15
      //   958: aconst_null
      //   959: aload 14
      //   961: invokestatic 267	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   964: astore 13
      //   966: aload 13
      //   968: astore 12
      //   970: aload 15
      //   972: invokevirtual 268	java/io/FileInputStream:close	()V
      //   975: goto -186 -> 789
      //   978: iload 5
      //   980: iconst_1
      //   981: if_icmpne +59 -> 1040
      //   984: aload 13
      //   986: astore 12
      //   988: aload 13
      //   990: invokevirtual 274	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   993: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   996: if_acmpne -552 -> 444
      //   999: aload 14
      //   1001: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1004: ifeq +1977 -> 2981
      //   1007: iconst_0
      //   1008: istore 5
      //   1010: aload 13
      //   1012: iconst_3
      //   1013: iload 5
      //   1015: aload 13
      //   1017: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   1020: aload 13
      //   1022: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   1025: aload 13
      //   1027: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   1030: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1033: aload 13
      //   1035: astore 12
      //   1037: goto -593 -> 444
      //   1040: iload 5
      //   1042: iconst_2
      //   1043: if_icmpne +59 -> 1102
      //   1046: aload 13
      //   1048: astore 12
      //   1050: aload 13
      //   1052: invokevirtual 274	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1055: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1058: if_acmpne -614 -> 444
      //   1061: aload 14
      //   1063: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1066: ifeq +1921 -> 2987
      //   1069: iconst_0
      //   1070: istore 5
      //   1072: aload 13
      //   1074: iconst_1
      //   1075: iload 5
      //   1077: aload 13
      //   1079: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   1082: aload 13
      //   1084: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   1087: aload 13
      //   1089: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   1092: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1095: aload 13
      //   1097: astore 12
      //   1099: goto -655 -> 444
      //   1102: iload 5
      //   1104: iconst_3
      //   1105: if_icmpne +130 -> 1235
      //   1108: aload 13
      //   1110: astore 12
      //   1112: aload 13
      //   1114: invokevirtual 274	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   1117: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   1120: if_acmpne -676 -> 444
      //   1123: aload 14
      //   1125: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1128: ifeq +1865 -> 2993
      //   1131: iconst_0
      //   1132: istore 5
      //   1134: aload 13
      //   1136: bipush 7
      //   1138: iload 5
      //   1140: aload 13
      //   1142: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   1145: aload 13
      //   1147: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   1150: aload 13
      //   1152: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   1155: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1158: aload 14
      //   1160: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1163: ifeq +1836 -> 2999
      //   1166: iconst_0
      //   1167: istore 5
      //   1169: aload 13
      //   1171: bipush 7
      //   1173: iload 5
      //   1175: aload 13
      //   1177: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   1180: aload 13
      //   1182: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   1185: aload 13
      //   1187: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   1190: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1193: aload 14
      //   1195: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1198: ifeq +1807 -> 3005
      //   1201: iconst_0
      //   1202: istore 5
      //   1204: aload 13
      //   1206: bipush 7
      //   1208: iload 5
      //   1210: aload 13
      //   1212: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   1215: aload 13
      //   1217: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   1220: aload 13
      //   1222: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   1225: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   1228: aload 13
      //   1230: astore 12
      //   1232: goto -788 -> 444
      //   1235: aload 13
      //   1237: astore 12
      //   1239: iload 5
      //   1241: ifne -797 -> 444
      //   1244: aload 13
      //   1246: astore 12
      //   1248: aload 14
      //   1250: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   1253: ifeq -809 -> 444
      //   1256: aload 13
      //   1258: invokestatic 291	org/vidogram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   1261: pop
      //   1262: aload 13
      //   1264: astore 12
      //   1266: goto -822 -> 444
      //   1269: aconst_null
      //   1270: astore 12
      //   1272: aload_0
      //   1273: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1276: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1279: ifnull +1675 -> 2954
      //   1282: aload_0
      //   1283: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1286: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1289: ldc_w 296
      //   1292: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1295: ifeq +158 -> 1453
      //   1298: aload_0
      //   1299: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1302: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1305: ldc_w 298
      //   1308: bipush 8
      //   1310: invokevirtual 302	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1313: istore 5
      //   1315: aload 15
      //   1317: astore 13
      //   1319: iload 5
      //   1321: iflt +1690 -> 3011
      //   1324: aload_0
      //   1325: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1328: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1331: bipush 8
      //   1333: iload 5
      //   1335: invokevirtual 306	java/lang/String:substring	(II)Ljava/lang/String;
      //   1338: invokestatic 312	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1341: invokestatic 316	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1344: astore 13
      //   1346: aload_0
      //   1347: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1350: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1353: iload 5
      //   1355: iconst_1
      //   1356: iadd
      //   1357: invokevirtual 319	java/lang/String:substring	(I)Ljava/lang/String;
      //   1360: astore 12
      //   1362: goto +1649 -> 3011
      //   1365: iload 8
      //   1367: ifeq +47 -> 1414
      //   1370: aload_0
      //   1371: getfield 27	org/vidogram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/vidogram/messenger/ImageLoader;
      //   1374: invokestatic 323	org/vidogram/messenger/ImageLoader:access$1400	(Lorg/vidogram/messenger/ImageLoader;)J
      //   1377: lconst_0
      //   1378: lcmp
      //   1379: ifeq +35 -> 1414
      //   1382: aload_0
      //   1383: getfield 27	org/vidogram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/vidogram/messenger/ImageLoader;
      //   1386: invokestatic 323	org/vidogram/messenger/ImageLoader:access$1400	(Lorg/vidogram/messenger/ImageLoader;)J
      //   1389: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   1392: iload 8
      //   1394: i2l
      //   1395: lsub
      //   1396: lcmp
      //   1397: ifle +17 -> 1414
      //   1400: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   1403: bipush 21
      //   1405: if_icmpge +9 -> 1414
      //   1408: iload 8
      //   1410: i2l
      //   1411: invokestatic 327	java/lang/Thread:sleep	(J)V
      //   1414: aload_0
      //   1415: getfield 27	org/vidogram/messenger/ImageLoader$CacheOutTask:this$0	Lorg/vidogram/messenger/ImageLoader;
      //   1418: invokestatic 156	java/lang/System:currentTimeMillis	()J
      //   1421: invokestatic 160	org/vidogram/messenger/ImageLoader:access$1402	(Lorg/vidogram/messenger/ImageLoader;J)J
      //   1424: pop2
      //   1425: aload_0
      //   1426: getfield 32	org/vidogram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1429: astore 12
      //   1431: aload 12
      //   1433: monitorenter
      //   1434: aload_0
      //   1435: getfield 53	org/vidogram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1438: ifeq +124 -> 1562
      //   1441: aload 12
      //   1443: monitorexit
      //   1444: return
      //   1445: astore 13
      //   1447: aload 12
      //   1449: monitorexit
      //   1450: aload 13
      //   1452: athrow
      //   1453: aload_0
      //   1454: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1457: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1460: ldc_w 329
      //   1463: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1466: ifeq +61 -> 1527
      //   1469: aload_0
      //   1470: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1473: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1476: ldc_w 298
      //   1479: bipush 9
      //   1481: invokevirtual 302	java/lang/String:indexOf	(Ljava/lang/String;I)I
      //   1484: istore 6
      //   1486: iload 8
      //   1488: istore 5
      //   1490: aload 17
      //   1492: astore 12
      //   1494: iload 6
      //   1496: iflt +1552 -> 3048
      //   1499: aload_0
      //   1500: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1503: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1506: bipush 9
      //   1508: iload 6
      //   1510: invokevirtual 306	java/lang/String:substring	(II)Ljava/lang/String;
      //   1513: invokestatic 312	java/lang/Long:parseLong	(Ljava/lang/String;)J
      //   1516: invokestatic 316	java/lang/Long:valueOf	(J)Ljava/lang/Long;
      //   1519: astore 12
      //   1521: iconst_1
      //   1522: istore 5
      //   1524: goto +1524 -> 3048
      //   1527: aload_0
      //   1528: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1531: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   1534: ldc_w 331
      //   1537: invokevirtual 136	java/lang/String:startsWith	(Ljava/lang/String;)Z
      //   1540: istore 11
      //   1542: iload 11
      //   1544: ifne +1410 -> 2954
      //   1547: aconst_null
      //   1548: astore 14
      //   1550: iconst_0
      //   1551: istore 5
      //   1553: iconst_0
      //   1554: istore 6
      //   1556: aconst_null
      //   1557: astore 15
      //   1559: goto +1466 -> 3025
      //   1562: aload 12
      //   1564: monitorexit
      //   1565: new 180	android/graphics/BitmapFactory$Options
      //   1568: dup
      //   1569: invokespecial 181	android/graphics/BitmapFactory$Options:<init>	()V
      //   1572: astore 17
      //   1574: aload 17
      //   1576: iconst_1
      //   1577: putfield 184	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1580: aload_0
      //   1581: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1584: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1587: ifnull +282 -> 1869
      //   1590: aload_0
      //   1591: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1594: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1597: ldc_w 333
      //   1600: invokevirtual 337	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
      //   1603: astore 12
      //   1605: aload 12
      //   1607: arraylength
      //   1608: iconst_2
      //   1609: if_icmplt +1338 -> 2947
      //   1612: aload 12
      //   1614: iconst_0
      //   1615: aaload
      //   1616: invokestatic 343	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1619: fstore_1
      //   1620: getstatic 347	org/vidogram/messenger/AndroidUtilities:density	F
      //   1623: fload_1
      //   1624: fmul
      //   1625: fstore_1
      //   1626: aload 12
      //   1628: iconst_1
      //   1629: aaload
      //   1630: invokestatic 343	java/lang/Float:parseFloat	(Ljava/lang/String;)F
      //   1633: getstatic 347	org/vidogram/messenger/AndroidUtilities:density	F
      //   1636: fmul
      //   1637: fstore_2
      //   1638: aload_0
      //   1639: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   1642: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   1645: ldc 178
      //   1647: invokevirtual 150	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   1650: ifeq +1291 -> 2941
      //   1653: iconst_1
      //   1654: istore 8
      //   1656: fload_1
      //   1657: fconst_0
      //   1658: fcmpl
      //   1659: ifeq +1276 -> 2935
      //   1662: fload_2
      //   1663: fconst_0
      //   1664: fcmpl
      //   1665: ifeq +1270 -> 2935
      //   1668: aload 17
      //   1670: iconst_1
      //   1671: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1674: aload 15
      //   1676: ifnull +156 -> 1832
      //   1679: aload 14
      //   1681: ifnonnull +151 -> 1832
      //   1684: iload 6
      //   1686: ifeq +122 -> 1808
      //   1689: getstatic 353	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1692: invokevirtual 359	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1695: aload 15
      //   1697: invokevirtual 362	java/lang/Long:longValue	()J
      //   1700: iconst_1
      //   1701: aload 17
      //   1703: invokestatic 368	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1706: pop
      //   1707: aconst_null
      //   1708: astore 12
      //   1710: aload 17
      //   1712: getfield 226	android/graphics/BitmapFactory$Options:outWidth	I
      //   1715: i2f
      //   1716: fstore_3
      //   1717: aload 17
      //   1719: getfield 229	android/graphics/BitmapFactory$Options:outHeight	I
      //   1722: i2f
      //   1723: fstore 4
      //   1725: fload_3
      //   1726: fload_1
      //   1727: fdiv
      //   1728: fload 4
      //   1730: fload_2
      //   1731: fdiv
      //   1732: invokestatic 374	java/lang/Math:max	(FF)F
      //   1735: fstore_3
      //   1736: fload_3
      //   1737: fstore_2
      //   1738: fload_3
      //   1739: fconst_1
      //   1740: fcmpg
      //   1741: ifge +5 -> 1746
      //   1744: fconst_1
      //   1745: fstore_2
      //   1746: aload 17
      //   1748: iconst_0
      //   1749: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1752: aload 17
      //   1754: fload_2
      //   1755: f2i
      //   1756: putfield 184	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1759: aload 12
      //   1761: astore 16
      //   1763: aload 16
      //   1765: astore 12
      //   1767: aload_0
      //   1768: getfield 32	org/vidogram/messenger/ImageLoader$CacheOutTask:sync	Ljava/lang/Object;
      //   1771: astore 13
      //   1773: aload 16
      //   1775: astore 12
      //   1777: aload 13
      //   1779: monitorenter
      //   1780: aload_0
      //   1781: getfield 53	org/vidogram/messenger/ImageLoader$CacheOutTask:isCancelled	Z
      //   1784: ifeq +213 -> 1997
      //   1787: aload 13
      //   1789: monitorexit
      //   1790: return
      //   1791: astore 14
      //   1793: aload 13
      //   1795: monitorexit
      //   1796: aload 16
      //   1798: astore 12
      //   1800: aload 14
      //   1802: athrow
      //   1803: astore 13
      //   1805: goto -1361 -> 444
      //   1808: getstatic 353	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   1811: invokevirtual 359	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   1814: aload 15
      //   1816: invokevirtual 362	java/lang/Long:longValue	()J
      //   1819: iconst_1
      //   1820: aload 17
      //   1822: invokestatic 377	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1825: pop
      //   1826: aconst_null
      //   1827: astore 12
      //   1829: goto -119 -> 1710
      //   1832: new 260	java/io/FileInputStream
      //   1835: dup
      //   1836: aload 18
      //   1838: invokespecial 263	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   1841: astore 16
      //   1843: aload 16
      //   1845: aconst_null
      //   1846: aload 17
      //   1848: invokestatic 267	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1851: astore 13
      //   1853: aload 13
      //   1855: astore 12
      //   1857: aload 16
      //   1859: invokevirtual 268	java/io/FileInputStream:close	()V
      //   1862: aload 13
      //   1864: astore 12
      //   1866: goto -156 -> 1710
      //   1869: aload 16
      //   1871: astore 13
      //   1873: aload 14
      //   1875: ifnull +110 -> 1985
      //   1878: aload 17
      //   1880: iconst_1
      //   1881: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1884: new 260	java/io/FileInputStream
      //   1887: dup
      //   1888: aload 18
      //   1890: invokespecial 263	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   1893: astore 16
      //   1895: aload 16
      //   1897: aconst_null
      //   1898: aload 17
      //   1900: invokestatic 267	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   1903: astore 13
      //   1905: aload 13
      //   1907: astore 12
      //   1909: aload 16
      //   1911: invokevirtual 268	java/io/FileInputStream:close	()V
      //   1914: aload 13
      //   1916: astore 12
      //   1918: aload 17
      //   1920: getfield 226	android/graphics/BitmapFactory$Options:outWidth	I
      //   1923: i2f
      //   1924: fstore_1
      //   1925: aload 13
      //   1927: astore 12
      //   1929: aload 17
      //   1931: getfield 229	android/graphics/BitmapFactory$Options:outHeight	I
      //   1934: i2f
      //   1935: fstore_2
      //   1936: aload 13
      //   1938: astore 12
      //   1940: fload_1
      //   1941: ldc_w 378
      //   1944: fdiv
      //   1945: fload_2
      //   1946: ldc_w 379
      //   1949: fdiv
      //   1950: invokestatic 374	java/lang/Math:max	(FF)F
      //   1953: fstore_2
      //   1954: fload_2
      //   1955: fstore_1
      //   1956: fload_2
      //   1957: fconst_1
      //   1958: fcmpg
      //   1959: ifge +5 -> 1964
      //   1962: fconst_1
      //   1963: fstore_1
      //   1964: aload 13
      //   1966: astore 12
      //   1968: aload 17
      //   1970: iconst_0
      //   1971: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   1974: aload 13
      //   1976: astore 12
      //   1978: aload 17
      //   1980: fload_1
      //   1981: f2i
      //   1982: putfield 184	android/graphics/BitmapFactory$Options:inSampleSize	I
      //   1985: iconst_0
      //   1986: istore 8
      //   1988: fconst_0
      //   1989: fstore_1
      //   1990: aload 13
      //   1992: astore 16
      //   1994: goto -231 -> 1763
      //   1997: aload 13
      //   1999: monitorexit
      //   2000: aload 16
      //   2002: astore 12
      //   2004: aload_0
      //   2005: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   2008: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2011: ifnull +22 -> 2033
      //   2014: iload 8
      //   2016: ifne +17 -> 2033
      //   2019: aload 16
      //   2021: astore 12
      //   2023: aload_0
      //   2024: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   2027: getfield 294	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   2030: ifnull +335 -> 2365
      //   2033: aload 16
      //   2035: astore 12
      //   2037: aload 17
      //   2039: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2042: putfield 382	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2045: aload 16
      //   2047: astore 12
      //   2049: getstatic 105	android/os/Build$VERSION:SDK_INT	I
      //   2052: bipush 21
      //   2054: if_icmpge +13 -> 2067
      //   2057: aload 16
      //   2059: astore 12
      //   2061: aload 17
      //   2063: iconst_1
      //   2064: putfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2067: aload 16
      //   2069: astore 12
      //   2071: aload 17
      //   2073: iconst_0
      //   2074: putfield 385	android/graphics/BitmapFactory$Options:inDither	Z
      //   2077: aload 16
      //   2079: astore 13
      //   2081: aload 15
      //   2083: ifnull +40 -> 2123
      //   2086: aload 16
      //   2088: astore 13
      //   2090: aload 14
      //   2092: ifnonnull +31 -> 2123
      //   2095: iload 6
      //   2097: ifeq +283 -> 2380
      //   2100: aload 16
      //   2102: astore 12
      //   2104: getstatic 353	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2107: invokevirtual 359	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2110: aload 15
      //   2112: invokevirtual 362	java/lang/Long:longValue	()J
      //   2115: iconst_1
      //   2116: aload 17
      //   2118: invokestatic 368	android/provider/MediaStore$Video$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2121: astore 13
      //   2123: aload 13
      //   2125: astore 12
      //   2127: aload 13
      //   2129: ifnonnull +458 -> 2587
      //   2132: iload 7
      //   2134: ifeq +272 -> 2406
      //   2137: aload 13
      //   2139: astore 12
      //   2141: new 107	java/io/RandomAccessFile
      //   2144: dup
      //   2145: aload 18
      //   2147: ldc 109
      //   2149: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2152: astore 14
      //   2154: aload 13
      //   2156: astore 12
      //   2158: aload 14
      //   2160: invokevirtual 191	java/io/RandomAccessFile:getChannel	()Ljava/nio/channels/FileChannel;
      //   2163: getstatic 197	java/nio/channels/FileChannel$MapMode:READ_ONLY	Ljava/nio/channels/FileChannel$MapMode;
      //   2166: lconst_0
      //   2167: aload 18
      //   2169: invokevirtual 202	java/io/File:length	()J
      //   2172: invokevirtual 208	java/nio/channels/FileChannel:map	(Ljava/nio/channels/FileChannel$MapMode;JJ)Ljava/nio/MappedByteBuffer;
      //   2175: astore 15
      //   2177: aload 13
      //   2179: astore 12
      //   2181: new 180	android/graphics/BitmapFactory$Options
      //   2184: dup
      //   2185: invokespecial 181	android/graphics/BitmapFactory$Options:<init>	()V
      //   2188: astore 16
      //   2190: aload 13
      //   2192: astore 12
      //   2194: aload 16
      //   2196: iconst_1
      //   2197: putfield 211	android/graphics/BitmapFactory$Options:inJustDecodeBounds	Z
      //   2200: aload 13
      //   2202: astore 12
      //   2204: aconst_null
      //   2205: aload 15
      //   2207: aload 15
      //   2209: invokevirtual 217	java/nio/ByteBuffer:limit	()I
      //   2212: aload 16
      //   2214: iconst_1
      //   2215: invokestatic 223	org/vidogram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2218: pop
      //   2219: aload 13
      //   2221: astore 12
      //   2223: aload 16
      //   2225: getfield 226	android/graphics/BitmapFactory$Options:outWidth	I
      //   2228: aload 16
      //   2230: getfield 229	android/graphics/BitmapFactory$Options:outHeight	I
      //   2233: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2236: invokestatic 241	org/vidogram/messenger/Bitmaps:createBitmap	(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;
      //   2239: astore 13
      //   2241: aload 13
      //   2243: astore 12
      //   2245: aload 15
      //   2247: invokevirtual 217	java/nio/ByteBuffer:limit	()I
      //   2250: istore 6
      //   2252: aload 13
      //   2254: astore 12
      //   2256: aload 17
      //   2258: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2261: ifne +808 -> 3069
      //   2264: iconst_1
      //   2265: istore 11
      //   2267: aload 13
      //   2269: astore 12
      //   2271: aload 13
      //   2273: aload 15
      //   2275: iload 6
      //   2277: aconst_null
      //   2278: iload 11
      //   2280: invokestatic 223	org/vidogram/messenger/Utilities:loadWebpImage	(Landroid/graphics/Bitmap;Ljava/nio/ByteBuffer;ILandroid/graphics/BitmapFactory$Options;Z)Z
      //   2283: pop
      //   2284: aload 13
      //   2286: astore 12
      //   2288: aload 14
      //   2290: invokevirtual 144	java/io/RandomAccessFile:close	()V
      //   2293: aload 13
      //   2295: ifnonnull +299 -> 2594
      //   2298: aload 13
      //   2300: astore 12
      //   2302: iload 5
      //   2304: ifeq -1860 -> 444
      //   2307: aload 13
      //   2309: astore 14
      //   2311: aload 18
      //   2313: invokevirtual 202	java/io/File:length	()J
      //   2316: lconst_0
      //   2317: lcmp
      //   2318: ifeq +21 -> 2339
      //   2321: aload 13
      //   2323: astore 12
      //   2325: aload 13
      //   2327: astore 14
      //   2329: aload_0
      //   2330: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   2333: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2336: ifnonnull -1892 -> 444
      //   2339: aload 13
      //   2341: astore 14
      //   2343: aload 18
      //   2345: invokevirtual 244	java/io/File:delete	()Z
      //   2348: pop
      //   2349: aload 13
      //   2351: astore 12
      //   2353: goto -1909 -> 444
      //   2356: astore 12
      //   2358: aload 14
      //   2360: astore 12
      //   2362: goto -1918 -> 444
      //   2365: aload 16
      //   2367: astore 12
      //   2369: aload 17
      //   2371: getstatic 388	android/graphics/Bitmap$Config:RGB_565	Landroid/graphics/Bitmap$Config;
      //   2374: putfield 382	android/graphics/BitmapFactory$Options:inPreferredConfig	Landroid/graphics/Bitmap$Config;
      //   2377: goto -332 -> 2045
      //   2380: aload 16
      //   2382: astore 12
      //   2384: getstatic 353	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
      //   2387: invokevirtual 359	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
      //   2390: aload 15
      //   2392: invokevirtual 362	java/lang/Long:longValue	()J
      //   2395: iconst_1
      //   2396: aload 17
      //   2398: invokestatic 377	android/provider/MediaStore$Images$Thumbnails:getThumbnail	(Landroid/content/ContentResolver;JILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2401: astore 13
      //   2403: goto -280 -> 2123
      //   2406: aload 13
      //   2408: astore 12
      //   2410: aload 17
      //   2412: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2415: ifeq +130 -> 2545
      //   2418: aload 13
      //   2420: astore 12
      //   2422: new 107	java/io/RandomAccessFile
      //   2425: dup
      //   2426: aload 18
      //   2428: ldc 109
      //   2430: invokespecial 112	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   2433: astore 15
      //   2435: aload 13
      //   2437: astore 12
      //   2439: aload 15
      //   2441: invokevirtual 245	java/io/RandomAccessFile:length	()J
      //   2444: l2i
      //   2445: istore 6
      //   2447: aload 13
      //   2449: astore 12
      //   2451: invokestatic 391	org/vidogram/messenger/ImageLoader:access$1600	()[B
      //   2454: ifnull +621 -> 3075
      //   2457: aload 13
      //   2459: astore 12
      //   2461: invokestatic 391	org/vidogram/messenger/ImageLoader:access$1600	()[B
      //   2464: arraylength
      //   2465: iload 6
      //   2467: if_icmplt +608 -> 3075
      //   2470: aload 13
      //   2472: astore 12
      //   2474: invokestatic 391	org/vidogram/messenger/ImageLoader:access$1600	()[B
      //   2477: astore 14
      //   2479: aload 14
      //   2481: astore 12
      //   2483: aload 12
      //   2485: astore 14
      //   2487: aload 12
      //   2489: ifnonnull +23 -> 2512
      //   2492: aload 13
      //   2494: astore 12
      //   2496: iload 6
      //   2498: newarray byte
      //   2500: astore 14
      //   2502: aload 13
      //   2504: astore 12
      //   2506: aload 14
      //   2508: invokestatic 394	org/vidogram/messenger/ImageLoader:access$1602	([B)[B
      //   2511: pop
      //   2512: aload 13
      //   2514: astore 12
      //   2516: aload 15
      //   2518: aload 14
      //   2520: iconst_0
      //   2521: iload 6
      //   2523: invokevirtual 123	java/io/RandomAccessFile:readFully	([BII)V
      //   2526: aload 13
      //   2528: astore 12
      //   2530: aload 14
      //   2532: iconst_0
      //   2533: iload 6
      //   2535: aload 17
      //   2537: invokestatic 258	android/graphics/BitmapFactory:decodeByteArray	([BIILandroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2540: astore 13
      //   2542: goto -249 -> 2293
      //   2545: aload 13
      //   2547: astore 12
      //   2549: new 260	java/io/FileInputStream
      //   2552: dup
      //   2553: aload 18
      //   2555: invokespecial 263	java/io/FileInputStream:<init>	(Ljava/io/File;)V
      //   2558: astore 14
      //   2560: aload 13
      //   2562: astore 12
      //   2564: aload 14
      //   2566: aconst_null
      //   2567: aload 17
      //   2569: invokestatic 267	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
      //   2572: astore 13
      //   2574: aload 13
      //   2576: astore 12
      //   2578: aload 14
      //   2580: invokevirtual 268	java/io/FileInputStream:close	()V
      //   2583: aload 13
      //   2585: astore 12
      //   2587: aload 12
      //   2589: astore 13
      //   2591: goto -298 -> 2293
      //   2594: iconst_0
      //   2595: istore 6
      //   2597: aload 13
      //   2599: astore 14
      //   2601: aload 13
      //   2603: astore 15
      //   2605: iload 6
      //   2607: istore 5
      //   2609: aload_0
      //   2610: getfield 34	org/vidogram/messenger/ImageLoader$CacheOutTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   2613: getfield 84	org/vidogram/messenger/ImageLoader$CacheImage:filter	Ljava/lang/String;
      //   2616: ifnull +245 -> 2861
      //   2619: aload 13
      //   2621: astore 14
      //   2623: aload 13
      //   2625: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   2628: i2f
      //   2629: fstore_2
      //   2630: aload 13
      //   2632: astore 14
      //   2634: aload 13
      //   2636: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   2639: i2f
      //   2640: fstore_3
      //   2641: aload 13
      //   2643: astore 14
      //   2645: aload 13
      //   2647: astore 12
      //   2649: aload 17
      //   2651: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2654: ifne +89 -> 2743
      //   2657: aload 13
      //   2659: astore 12
      //   2661: fload_1
      //   2662: fconst_0
      //   2663: fcmpl
      //   2664: ifeq +79 -> 2743
      //   2667: aload 13
      //   2669: astore 12
      //   2671: fload_2
      //   2672: fload_1
      //   2673: fcmpl
      //   2674: ifeq +69 -> 2743
      //   2677: aload 13
      //   2679: astore 12
      //   2681: fload_2
      //   2682: ldc_w 395
      //   2685: fload_1
      //   2686: fadd
      //   2687: fcmpl
      //   2688: ifle +55 -> 2743
      //   2691: aload 13
      //   2693: astore 14
      //   2695: fload_2
      //   2696: fload_1
      //   2697: fdiv
      //   2698: fstore 4
      //   2700: aload 13
      //   2702: astore 14
      //   2704: aload 13
      //   2706: fload_1
      //   2707: f2i
      //   2708: fload_3
      //   2709: fload 4
      //   2711: fdiv
      //   2712: f2i
      //   2713: iconst_1
      //   2714: invokestatic 399	org/vidogram/messenger/Bitmaps:createScaledBitmap	(Landroid/graphics/Bitmap;IIZ)Landroid/graphics/Bitmap;
      //   2717: astore 15
      //   2719: aload 13
      //   2721: astore 12
      //   2723: aload 13
      //   2725: aload 15
      //   2727: if_acmpeq +16 -> 2743
      //   2730: aload 13
      //   2732: astore 14
      //   2734: aload 13
      //   2736: invokevirtual 402	android/graphics/Bitmap:recycle	()V
      //   2739: aload 15
      //   2741: astore 12
      //   2743: aload 12
      //   2745: astore 15
      //   2747: iload 6
      //   2749: istore 5
      //   2751: aload 12
      //   2753: ifnull +108 -> 2861
      //   2756: aload 12
      //   2758: astore 15
      //   2760: iload 6
      //   2762: istore 5
      //   2764: iload 8
      //   2766: ifeq +95 -> 2861
      //   2769: aload 12
      //   2771: astore 15
      //   2773: iload 6
      //   2775: istore 5
      //   2777: fload_3
      //   2778: ldc_w 403
      //   2781: fcmpg
      //   2782: ifge +79 -> 2861
      //   2785: aload 12
      //   2787: astore 15
      //   2789: iload 6
      //   2791: istore 5
      //   2793: fload_2
      //   2794: ldc_w 403
      //   2797: fcmpg
      //   2798: ifge +63 -> 2861
      //   2801: aload 12
      //   2803: astore 14
      //   2805: aload 12
      //   2807: invokevirtual 274	android/graphics/Bitmap:getConfig	()Landroid/graphics/Bitmap$Config;
      //   2810: getstatic 235	android/graphics/Bitmap$Config:ARGB_8888	Landroid/graphics/Bitmap$Config;
      //   2813: if_acmpne +268 -> 3081
      //   2816: aload 12
      //   2818: astore 14
      //   2820: aload 17
      //   2822: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2825: ifeq +78 -> 2903
      //   2828: iconst_0
      //   2829: istore 5
      //   2831: aload 12
      //   2833: astore 14
      //   2835: aload 12
      //   2837: iconst_3
      //   2838: iload 5
      //   2840: aload 12
      //   2842: invokevirtual 277	android/graphics/Bitmap:getWidth	()I
      //   2845: aload 12
      //   2847: invokevirtual 280	android/graphics/Bitmap:getHeight	()I
      //   2850: aload 12
      //   2852: invokevirtual 283	android/graphics/Bitmap:getRowBytes	()I
      //   2855: invokestatic 287	org/vidogram/messenger/Utilities:blurBitmap	(Ljava/lang/Object;IIIII)V
      //   2858: goto +223 -> 3081
      //   2861: aload 15
      //   2863: astore 12
      //   2865: iload 5
      //   2867: ifne -2423 -> 444
      //   2870: aload 15
      //   2872: astore 12
      //   2874: aload 15
      //   2876: astore 14
      //   2878: aload 17
      //   2880: getfield 187	android/graphics/BitmapFactory$Options:inPurgeable	Z
      //   2883: ifeq -2439 -> 444
      //   2886: aload 15
      //   2888: astore 14
      //   2890: aload 15
      //   2892: invokestatic 291	org/vidogram/messenger/Utilities:pinBitmap	(Landroid/graphics/Bitmap;)I
      //   2895: pop
      //   2896: aload 15
      //   2898: astore 12
      //   2900: goto -2456 -> 444
      //   2903: iconst_1
      //   2904: istore 5
      //   2906: goto -75 -> 2831
      //   2909: aconst_null
      //   2910: astore 12
      //   2912: goto -2448 -> 464
      //   2915: astore 13
      //   2917: goto -2473 -> 444
      //   2920: astore 13
      //   2922: goto -2483 -> 439
      //   2925: astore 13
      //   2927: goto -2372 -> 555
      //   2930: astore 14
      //   2932: goto -2426 -> 506
      //   2935: aconst_null
      //   2936: astore 12
      //   2938: goto -1179 -> 1759
      //   2941: iconst_0
      //   2942: istore 8
      //   2944: goto -1288 -> 1656
      //   2947: fconst_0
      //   2948: fstore_2
      //   2949: fconst_0
      //   2950: fstore_1
      //   2951: goto -1313 -> 1638
      //   2954: aconst_null
      //   2955: astore 14
      //   2957: iconst_1
      //   2958: istore 5
      //   2960: iconst_0
      //   2961: istore 6
      //   2963: aconst_null
      //   2964: astore 15
      //   2966: goto +59 -> 3025
      //   2969: iconst_0
      //   2970: istore 5
      //   2972: goto -2577 -> 395
      //   2975: aconst_null
      //   2976: astore 12
      //   2978: goto -2079 -> 899
      //   2981: iconst_1
      //   2982: istore 5
      //   2984: goto -1974 -> 1010
      //   2987: iconst_1
      //   2988: istore 5
      //   2990: goto -1918 -> 1072
      //   2993: iconst_1
      //   2994: istore 5
      //   2996: goto -1862 -> 1134
      //   2999: iconst_1
      //   3000: istore 5
      //   3002: goto -1833 -> 1169
      //   3005: iconst_1
      //   3006: istore 5
      //   3008: goto -1804 -> 1204
      //   3011: iconst_0
      //   3012: istore 5
      //   3014: iconst_0
      //   3015: istore 6
      //   3017: aload 13
      //   3019: astore 15
      //   3021: aload 12
      //   3023: astore 14
      //   3025: bipush 20
      //   3027: istore 8
      //   3029: aload 15
      //   3031: ifnull -1666 -> 1365
      //   3034: iconst_0
      //   3035: istore 8
      //   3037: goto -1672 -> 1365
      //   3040: astore 12
      //   3042: aconst_null
      //   3043: astore 12
      //   3045: goto -2601 -> 444
      //   3048: aconst_null
      //   3049: astore 14
      //   3051: iconst_0
      //   3052: istore 8
      //   3054: iload 5
      //   3056: istore 6
      //   3058: iload 8
      //   3060: istore 5
      //   3062: aload 12
      //   3064: astore 15
      //   3066: goto -41 -> 3025
      //   3069: iconst_0
      //   3070: istore 11
      //   3072: goto -805 -> 2267
      //   3075: aconst_null
      //   3076: astore 12
      //   3078: goto -595 -> 2483
      //   3081: iconst_1
      //   3082: istore 5
      //   3084: aload 12
      //   3086: astore 15
      //   3088: goto -227 -> 2861
      //
      // Exception table:
      //   from	to	target	type
      //   53	63	64	finally
      //   66	69	64	finally
      //   80	83	64	finally
      //   9	30	72	finally
      //   31	34	72	finally
      //   74	77	72	finally
      //   415	425	426	finally
      //   428	431	426	finally
      //   620	623	426	finally
      //   395	415	434	java/lang/Throwable
      //   431	434	434	java/lang/Throwable
      //   623	652	434	java/lang/Throwable
      //   657	737	434	java/lang/Throwable
      //   850	899	434	java/lang/Throwable
      //   908	920	434	java/lang/Throwable
      //   920	942	434	java/lang/Throwable
      //   945	966	434	java/lang/Throwable
      //   348	353	487	java/lang/Exception
      //   191	204	501	java/lang/Exception
      //   524	529	536	java/lang/Exception
      //   191	204	550	finally
      //   560	565	568	java/lang/Exception
      //   794	804	831	java/lang/Throwable
      //   808	818	831	java/lang/Throwable
      //   818	824	831	java/lang/Throwable
      //   988	1007	831	java/lang/Throwable
      //   1010	1033	831	java/lang/Throwable
      //   1050	1069	831	java/lang/Throwable
      //   1072	1095	831	java/lang/Throwable
      //   1112	1131	831	java/lang/Throwable
      //   1134	1166	831	java/lang/Throwable
      //   1169	1201	831	java/lang/Throwable
      //   1204	1228	831	java/lang/Throwable
      //   1248	1262	831	java/lang/Throwable
      //   1434	1444	1445	finally
      //   1447	1450	1445	finally
      //   1562	1565	1445	finally
      //   1780	1790	1791	finally
      //   1793	1796	1791	finally
      //   1997	2000	1791	finally
      //   1767	1773	1803	java/lang/Throwable
      //   1777	1780	1803	java/lang/Throwable
      //   1800	1803	1803	java/lang/Throwable
      //   1857	1862	1803	java/lang/Throwable
      //   1909	1914	1803	java/lang/Throwable
      //   1918	1925	1803	java/lang/Throwable
      //   1929	1936	1803	java/lang/Throwable
      //   1940	1954	1803	java/lang/Throwable
      //   1968	1974	1803	java/lang/Throwable
      //   1978	1985	1803	java/lang/Throwable
      //   2004	2014	1803	java/lang/Throwable
      //   2023	2033	1803	java/lang/Throwable
      //   2037	2045	1803	java/lang/Throwable
      //   2049	2057	1803	java/lang/Throwable
      //   2061	2067	1803	java/lang/Throwable
      //   2071	2077	1803	java/lang/Throwable
      //   2104	2123	1803	java/lang/Throwable
      //   2141	2154	1803	java/lang/Throwable
      //   2158	2177	1803	java/lang/Throwable
      //   2181	2190	1803	java/lang/Throwable
      //   2194	2200	1803	java/lang/Throwable
      //   2204	2219	1803	java/lang/Throwable
      //   2223	2241	1803	java/lang/Throwable
      //   2245	2252	1803	java/lang/Throwable
      //   2256	2264	1803	java/lang/Throwable
      //   2271	2284	1803	java/lang/Throwable
      //   2288	2293	1803	java/lang/Throwable
      //   2369	2377	1803	java/lang/Throwable
      //   2384	2403	1803	java/lang/Throwable
      //   2410	2418	1803	java/lang/Throwable
      //   2422	2435	1803	java/lang/Throwable
      //   2439	2447	1803	java/lang/Throwable
      //   2451	2457	1803	java/lang/Throwable
      //   2461	2470	1803	java/lang/Throwable
      //   2474	2479	1803	java/lang/Throwable
      //   2496	2502	1803	java/lang/Throwable
      //   2506	2512	1803	java/lang/Throwable
      //   2516	2526	1803	java/lang/Throwable
      //   2530	2542	1803	java/lang/Throwable
      //   2549	2560	1803	java/lang/Throwable
      //   2564	2574	1803	java/lang/Throwable
      //   2578	2583	1803	java/lang/Throwable
      //   2311	2321	2356	java/lang/Throwable
      //   2329	2339	2356	java/lang/Throwable
      //   2343	2349	2356	java/lang/Throwable
      //   2609	2619	2356	java/lang/Throwable
      //   2623	2630	2356	java/lang/Throwable
      //   2634	2641	2356	java/lang/Throwable
      //   2649	2657	2356	java/lang/Throwable
      //   2695	2700	2356	java/lang/Throwable
      //   2704	2719	2356	java/lang/Throwable
      //   2734	2739	2356	java/lang/Throwable
      //   2805	2816	2356	java/lang/Throwable
      //   2820	2828	2356	java/lang/Throwable
      //   2835	2858	2356	java/lang/Throwable
      //   2878	2886	2356	java/lang/Throwable
      //   2890	2896	2356	java/lang/Throwable
      //   1710	1736	2915	java/lang/Throwable
      //   1746	1759	2915	java/lang/Throwable
      //   741	748	2920	java/lang/Throwable
      //   752	760	2920	java/lang/Throwable
      //   767	780	2920	java/lang/Throwable
      //   784	789	2920	java/lang/Throwable
      //   970	975	2920	java/lang/Throwable
      //   212	222	2925	finally
      //   230	235	2925	finally
      //   243	254	2925	finally
      //   262	279	2925	finally
      //   291	301	2925	finally
      //   313	323	2925	finally
      //   334	339	2925	finally
      //   479	484	2925	finally
      //   510	515	2925	finally
      //   212	222	2930	java/lang/Exception
      //   230	235	2930	java/lang/Exception
      //   243	254	2930	java/lang/Exception
      //   262	279	2930	java/lang/Exception
      //   291	301	2930	java/lang/Exception
      //   313	323	2930	java/lang/Exception
      //   334	339	2930	java/lang/Exception
      //   479	484	2930	java/lang/Exception
      //   1272	1315	3040	java/lang/Throwable
      //   1324	1362	3040	java/lang/Throwable
      //   1370	1414	3040	java/lang/Throwable
      //   1414	1434	3040	java/lang/Throwable
      //   1450	1453	3040	java/lang/Throwable
      //   1453	1486	3040	java/lang/Throwable
      //   1499	1521	3040	java/lang/Throwable
      //   1527	1542	3040	java/lang/Throwable
      //   1565	1638	3040	java/lang/Throwable
      //   1638	1653	3040	java/lang/Throwable
      //   1668	1674	3040	java/lang/Throwable
      //   1689	1707	3040	java/lang/Throwable
      //   1808	1826	3040	java/lang/Throwable
      //   1832	1853	3040	java/lang/Throwable
      //   1878	1905	3040	java/lang/Throwable
    }
  }

  private class HttpFileTask extends AsyncTask<Void, Void, Boolean>
  {
    private boolean canRetry = true;
    private String ext;
    private RandomAccessFile fileOutputStream = null;
    private int fileSize;
    private long lastProgressTime;
    private File tempFile;
    private String url;

    public HttpFileTask(String paramFile, File paramString1, String arg4)
    {
      this.url = paramFile;
      this.tempFile = paramString1;
      Object localObject;
      this.ext = localObject;
    }

    private void reportProgress(float paramFloat)
    {
      long l = System.currentTimeMillis();
      if ((paramFloat == 1.0F) || (this.lastProgressTime == 0L) || (this.lastProgressTime < l - 500L))
      {
        this.lastProgressTime = l;
        Utilities.stageQueue.postRunnable(new Runnable(paramFloat)
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.put(ImageLoader.HttpFileTask.this.url, Float.valueOf(this.val$progress));
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpFileTask.access$000(ImageLoader.HttpFileTask.this), Float.valueOf(ImageLoader.HttpFileTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }

    // ERROR //
    protected Boolean doInBackground(Void[] paramArrayOfVoid)
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore 5
      //   3: iconst_0
      //   4: istore 6
      //   6: iconst_0
      //   7: istore 4
      //   9: new 87	java/net/URL
      //   12: dup
      //   13: aload_0
      //   14: getfield 40	org/vidogram/messenger/ImageLoader$HttpFileTask:url	Ljava/lang/String;
      //   17: invokespecial 90	java/net/URL:<init>	(Ljava/lang/String;)V
      //   20: invokevirtual 94	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   23: astore 8
      //   25: aload 8
      //   27: astore 7
      //   29: aload 8
      //   31: ldc 96
      //   33: ldc 98
      //   35: invokevirtual 104	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   38: aload 8
      //   40: astore 7
      //   42: aload 8
      //   44: ldc 106
      //   46: ldc 108
      //   48: invokevirtual 104	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   51: aload 8
      //   53: astore 7
      //   55: aload 8
      //   57: sipush 5000
      //   60: invokevirtual 112	java/net/URLConnection:setConnectTimeout	(I)V
      //   63: aload 8
      //   65: astore 7
      //   67: aload 8
      //   69: sipush 5000
      //   72: invokevirtual 115	java/net/URLConnection:setReadTimeout	(I)V
      //   75: aload 8
      //   77: astore_1
      //   78: aload 8
      //   80: astore 7
      //   82: aload 8
      //   84: instanceof 117
      //   87: ifeq +132 -> 219
      //   90: aload 8
      //   92: astore 7
      //   94: aload 8
      //   96: checkcast 117	java/net/HttpURLConnection
      //   99: astore 9
      //   101: aload 8
      //   103: astore 7
      //   105: aload 9
      //   107: iconst_1
      //   108: invokevirtual 121	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
      //   111: aload 8
      //   113: astore 7
      //   115: aload 9
      //   117: invokevirtual 125	java/net/HttpURLConnection:getResponseCode	()I
      //   120: istore_2
      //   121: iload_2
      //   122: sipush 302
      //   125: if_icmpeq +20 -> 145
      //   128: iload_2
      //   129: sipush 301
      //   132: if_icmpeq +13 -> 145
      //   135: aload 8
      //   137: astore_1
      //   138: iload_2
      //   139: sipush 303
      //   142: if_icmpne +77 -> 219
      //   145: aload 8
      //   147: astore 7
      //   149: aload 9
      //   151: ldc 127
      //   153: invokevirtual 131	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
      //   156: astore_1
      //   157: aload 8
      //   159: astore 7
      //   161: aload 9
      //   163: ldc 133
      //   165: invokevirtual 131	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
      //   168: astore 9
      //   170: aload 8
      //   172: astore 7
      //   174: new 87	java/net/URL
      //   177: dup
      //   178: aload_1
      //   179: invokespecial 90	java/net/URL:<init>	(Ljava/lang/String;)V
      //   182: invokevirtual 94	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   185: astore_1
      //   186: aload_1
      //   187: astore 7
      //   189: aload_1
      //   190: ldc 135
      //   192: aload 9
      //   194: invokevirtual 138	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   197: aload_1
      //   198: astore 7
      //   200: aload_1
      //   201: ldc 96
      //   203: ldc 98
      //   205: invokevirtual 104	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   208: aload_1
      //   209: astore 7
      //   211: aload_1
      //   212: ldc 106
      //   214: ldc 108
      //   216: invokevirtual 104	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   219: aload_1
      //   220: astore 7
      //   222: aload_1
      //   223: invokevirtual 141	java/net/URLConnection:connect	()V
      //   226: aload_1
      //   227: astore 7
      //   229: aload_1
      //   230: invokevirtual 145	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
      //   233: astore 8
      //   235: aload_0
      //   236: new 147	java/io/RandomAccessFile
      //   239: dup
      //   240: aload_0
      //   241: getfield 42	org/vidogram/messenger/ImageLoader$HttpFileTask:tempFile	Ljava/io/File;
      //   244: ldc 149
      //   246: invokespecial 152	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   249: putfield 36	org/vidogram/messenger/ImageLoader$HttpFileTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   252: aload 8
      //   254: astore 7
      //   256: aload_0
      //   257: getfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   260: ifeq +175 -> 435
      //   263: aload_1
      //   264: ifnull +44 -> 308
      //   267: aload_1
      //   268: instanceof 117
      //   271: ifeq +37 -> 308
      //   274: aload_1
      //   275: checkcast 117	java/net/HttpURLConnection
      //   278: invokevirtual 125	java/net/HttpURLConnection:getResponseCode	()I
      //   281: istore_2
      //   282: iload_2
      //   283: sipush 200
      //   286: if_icmpeq +22 -> 308
      //   289: iload_2
      //   290: sipush 202
      //   293: if_icmpeq +15 -> 308
      //   296: iload_2
      //   297: sipush 304
      //   300: if_icmpeq +8 -> 308
      //   303: aload_0
      //   304: iconst_0
      //   305: putfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   308: aload_1
      //   309: ifnull +63 -> 372
      //   312: aload_1
      //   313: invokevirtual 156	java/net/URLConnection:getHeaderFields	()Ljava/util/Map;
      //   316: astore_1
      //   317: aload_1
      //   318: ifnull +54 -> 372
      //   321: aload_1
      //   322: ldc 158
      //   324: invokeinterface 164 2 0
      //   329: checkcast 166	java/util/List
      //   332: astore_1
      //   333: aload_1
      //   334: ifnull +38 -> 372
      //   337: aload_1
      //   338: invokeinterface 170 1 0
      //   343: ifne +29 -> 372
      //   346: aload_1
      //   347: iconst_0
      //   348: invokeinterface 173 2 0
      //   353: checkcast 175	java/lang/String
      //   356: astore_1
      //   357: aload_1
      //   358: ifnull +14 -> 372
      //   361: aload_0
      //   362: aload_1
      //   363: invokestatic 179	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
      //   366: invokevirtual 184	java/lang/Integer:intValue	()I
      //   369: putfield 186	org/vidogram/messenger/ImageLoader$HttpFileTask:fileSize	I
      //   372: aload 7
      //   374: ifnull +24 -> 398
      //   377: ldc 187
      //   379: newarray byte
      //   381: astore_1
      //   382: iconst_0
      //   383: istore_2
      //   384: aload_0
      //   385: invokevirtual 190	org/vidogram/messenger/ImageLoader$HttpFileTask:isCancelled	()Z
      //   388: istore 4
      //   390: iload 4
      //   392: ifeq +174 -> 566
      //   395: iconst_0
      //   396: istore 4
      //   398: aload_0
      //   399: getfield 36	org/vidogram/messenger/ImageLoader$HttpFileTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   402: ifnull +15 -> 417
      //   405: aload_0
      //   406: getfield 36	org/vidogram/messenger/ImageLoader$HttpFileTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   409: invokevirtual 193	java/io/RandomAccessFile:close	()V
      //   412: aload_0
      //   413: aconst_null
      //   414: putfield 36	org/vidogram/messenger/ImageLoader$HttpFileTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   417: iload 4
      //   419: istore 5
      //   421: aload 7
      //   423: ifnull +12 -> 435
      //   426: aload 7
      //   428: invokevirtual 196	java/io/InputStream:close	()V
      //   431: iload 4
      //   433: istore 5
      //   435: iload 5
      //   437: invokestatic 202	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   440: areturn
      //   441: astore 7
      //   443: aconst_null
      //   444: astore_1
      //   445: aconst_null
      //   446: astore 8
      //   448: aload 7
      //   450: instanceof 204
      //   453: ifeq +26 -> 479
      //   456: invokestatic 209	org/vidogram/tgnet/ConnectionsManager:isNetworkOnline	()Z
      //   459: ifeq +8 -> 467
      //   462: aload_0
      //   463: iconst_0
      //   464: putfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   467: aload 7
      //   469: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   472: aload 8
      //   474: astore 7
      //   476: goto -220 -> 256
      //   479: aload 7
      //   481: instanceof 217
      //   484: ifeq +11 -> 495
      //   487: aload_0
      //   488: iconst_0
      //   489: putfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   492: goto -25 -> 467
      //   495: aload 7
      //   497: instanceof 219
      //   500: ifeq +32 -> 532
      //   503: aload 7
      //   505: invokevirtual 223	java/lang/Throwable:getMessage	()Ljava/lang/String;
      //   508: ifnull -41 -> 467
      //   511: aload 7
      //   513: invokevirtual 223	java/lang/Throwable:getMessage	()Ljava/lang/String;
      //   516: ldc 225
      //   518: invokevirtual 229	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   521: ifeq -54 -> 467
      //   524: aload_0
      //   525: iconst_0
      //   526: putfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   529: goto -62 -> 467
      //   532: aload 7
      //   534: instanceof 231
      //   537: ifeq -70 -> 467
      //   540: aload_0
      //   541: iconst_0
      //   542: putfield 38	org/vidogram/messenger/ImageLoader$HttpFileTask:canRetry	Z
      //   545: goto -78 -> 467
      //   548: astore 8
      //   550: aload 8
      //   552: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   555: goto -247 -> 308
      //   558: astore_1
      //   559: aload_1
      //   560: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   563: goto -191 -> 372
      //   566: aload 7
      //   568: aload_1
      //   569: invokevirtual 235	java/io/InputStream:read	([B)I
      //   572: istore_3
      //   573: iload_3
      //   574: ifle +62 -> 636
      //   577: aload_0
      //   578: getfield 36	org/vidogram/messenger/ImageLoader$HttpFileTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   581: aload_1
      //   582: iconst_0
      //   583: iload_3
      //   584: invokevirtual 239	java/io/RandomAccessFile:write	([BII)V
      //   587: iload_2
      //   588: iload_3
      //   589: iadd
      //   590: istore_3
      //   591: iload_3
      //   592: istore_2
      //   593: aload_0
      //   594: getfield 186	org/vidogram/messenger/ImageLoader$HttpFileTask:fileSize	I
      //   597: ifle -213 -> 384
      //   600: aload_0
      //   601: iload_3
      //   602: i2f
      //   603: aload_0
      //   604: getfield 186	org/vidogram/messenger/ImageLoader$HttpFileTask:fileSize	I
      //   607: i2f
      //   608: fdiv
      //   609: invokespecial 241	org/vidogram/messenger/ImageLoader$HttpFileTask:reportProgress	(F)V
      //   612: iload_3
      //   613: istore_2
      //   614: goto -230 -> 384
      //   617: astore_1
      //   618: iconst_0
      //   619: istore 4
      //   621: aload_1
      //   622: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   625: goto -227 -> 398
      //   628: astore_1
      //   629: aload_1
      //   630: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   633: goto -235 -> 398
      //   636: iload_3
      //   637: iconst_m1
      //   638: if_icmpne +21 -> 659
      //   641: aload_0
      //   642: getfield 186	org/vidogram/messenger/ImageLoader$HttpFileTask:fileSize	I
      //   645: ifeq +8 -> 653
      //   648: aload_0
      //   649: fconst_1
      //   650: invokespecial 241	org/vidogram/messenger/ImageLoader$HttpFileTask:reportProgress	(F)V
      //   653: iconst_1
      //   654: istore 4
      //   656: goto -258 -> 398
      //   659: iconst_0
      //   660: istore 4
      //   662: goto -264 -> 398
      //   665: astore_1
      //   666: aload_1
      //   667: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   670: goto -253 -> 417
      //   673: astore_1
      //   674: aload_1
      //   675: invokestatic 215	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   678: iload 4
      //   680: istore 5
      //   682: goto -247 -> 435
      //   685: astore_1
      //   686: iload 6
      //   688: istore 4
      //   690: goto -61 -> 629
      //   693: astore_1
      //   694: iconst_1
      //   695: istore 4
      //   697: goto -68 -> 629
      //   700: astore_1
      //   701: iconst_1
      //   702: istore 4
      //   704: goto -83 -> 621
      //   707: astore 9
      //   709: aconst_null
      //   710: astore 8
      //   712: aload 7
      //   714: astore_1
      //   715: aload 9
      //   717: astore 7
      //   719: goto -271 -> 448
      //   722: astore 7
      //   724: goto -276 -> 448
      //
      // Exception table:
      //   from	to	target	type
      //   9	25	441	java/lang/Throwable
      //   267	282	548	java/lang/Exception
      //   303	308	548	java/lang/Exception
      //   312	317	558	java/lang/Exception
      //   321	333	558	java/lang/Exception
      //   337	357	558	java/lang/Exception
      //   361	372	558	java/lang/Exception
      //   566	573	617	java/lang/Exception
      //   577	587	617	java/lang/Exception
      //   593	612	617	java/lang/Exception
      //   621	625	628	java/lang/Throwable
      //   398	417	665	java/lang/Throwable
      //   426	431	673	java/lang/Throwable
      //   377	382	685	java/lang/Throwable
      //   384	390	685	java/lang/Throwable
      //   566	573	685	java/lang/Throwable
      //   577	587	685	java/lang/Throwable
      //   593	612	685	java/lang/Throwable
      //   641	653	693	java/lang/Throwable
      //   641	653	700	java/lang/Exception
      //   29	38	707	java/lang/Throwable
      //   42	51	707	java/lang/Throwable
      //   55	63	707	java/lang/Throwable
      //   67	75	707	java/lang/Throwable
      //   82	90	707	java/lang/Throwable
      //   94	101	707	java/lang/Throwable
      //   105	111	707	java/lang/Throwable
      //   115	121	707	java/lang/Throwable
      //   149	157	707	java/lang/Throwable
      //   161	170	707	java/lang/Throwable
      //   174	186	707	java/lang/Throwable
      //   189	197	707	java/lang/Throwable
      //   200	208	707	java/lang/Throwable
      //   211	219	707	java/lang/Throwable
      //   222	226	707	java/lang/Throwable
      //   229	235	707	java/lang/Throwable
      //   235	252	722	java/lang/Throwable
    }

    protected void onCancelled()
    {
      ImageLoader.this.runHttpFileLoadTasks(this, 2);
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      ImageLoader localImageLoader = ImageLoader.this;
      if (paramBoolean.booleanValue());
      for (int i = 2; ; i = 1)
      {
        localImageLoader.runHttpFileLoadTasks(this, i);
        return;
      }
    }
  }

  private class HttpImageTask extends AsyncTask<Void, Void, Boolean>
  {
    private ImageLoader.CacheImage cacheImage = null;
    private boolean canRetry = true;
    private RandomAccessFile fileOutputStream = null;
    private URLConnection httpConnection = null;
    private int imageSize;
    private long lastProgressTime;

    public HttpImageTask(ImageLoader.CacheImage paramInt, int arg3)
    {
      this.cacheImage = paramInt;
      int i;
      this.imageSize = i;
    }

    private void reportProgress(float paramFloat)
    {
      long l = System.currentTimeMillis();
      if ((paramFloat == 1.0F) || (this.lastProgressTime == 0L) || (this.lastProgressTime < l - 500L))
      {
        this.lastProgressTime = l;
        Utilities.stageQueue.postRunnable(new Runnable(paramFloat)
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.put(ImageLoader.HttpImageTask.this.cacheImage.url, Float.valueOf(this.val$progress));
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileLoadProgressChanged, new Object[] { ImageLoader.HttpImageTask.access$300(ImageLoader.HttpImageTask.this).url, Float.valueOf(ImageLoader.HttpImageTask.1.this.val$progress) });
              }
            });
          }
        });
      }
    }

    // ERROR //
    protected Boolean doInBackground(Void[] paramArrayOfVoid)
    {
      // Byte code:
      //   0: aconst_null
      //   1: astore_1
      //   2: iconst_0
      //   3: istore 6
      //   5: iconst_0
      //   6: istore 7
      //   8: aload_0
      //   9: invokevirtual 97	org/vidogram/messenger/ImageLoader$HttpImageTask:isCancelled	()Z
      //   12: ifne +129 -> 141
      //   15: aload_0
      //   16: new 99	java/net/URL
      //   19: dup
      //   20: aload_0
      //   21: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   24: getfield 105	org/vidogram/messenger/ImageLoader$CacheImage:httpUrl	Ljava/lang/String;
      //   27: invokespecial 108	java/net/URL:<init>	(Ljava/lang/String;)V
      //   30: invokevirtual 112	java/net/URL:openConnection	()Ljava/net/URLConnection;
      //   33: putfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   36: aload_0
      //   37: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   40: ldc 114
      //   42: ldc 116
      //   44: invokevirtual 122	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   47: aload_0
      //   48: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   51: ldc 124
      //   53: ldc 126
      //   55: invokevirtual 122	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
      //   58: aload_0
      //   59: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   62: sipush 5000
      //   65: invokevirtual 130	java/net/URLConnection:setConnectTimeout	(I)V
      //   68: aload_0
      //   69: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   72: sipush 5000
      //   75: invokevirtual 133	java/net/URLConnection:setReadTimeout	(I)V
      //   78: aload_0
      //   79: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   82: instanceof 135
      //   85: ifeq +14 -> 99
      //   88: aload_0
      //   89: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   92: checkcast 135	java/net/HttpURLConnection
      //   95: iconst_1
      //   96: invokevirtual 139	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
      //   99: aload_0
      //   100: invokevirtual 97	org/vidogram/messenger/ImageLoader$HttpImageTask:isCancelled	()Z
      //   103: ifne +580 -> 683
      //   106: aload_0
      //   107: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   110: invokevirtual 142	java/net/URLConnection:connect	()V
      //   113: aload_0
      //   114: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   117: invokevirtual 146	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
      //   120: astore_1
      //   121: aload_0
      //   122: new 148	java/io/RandomAccessFile
      //   125: dup
      //   126: aload_0
      //   127: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   130: getfield 152	org/vidogram/messenger/ImageLoader$CacheImage:tempFilePath	Ljava/io/File;
      //   133: ldc 154
      //   135: invokespecial 157	java/io/RandomAccessFile:<init>	(Ljava/io/File;Ljava/lang/String;)V
      //   138: putfield 49	org/vidogram/messenger/ImageLoader$HttpImageTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   141: iload 7
      //   143: istore 5
      //   145: aload_0
      //   146: invokevirtual 97	org/vidogram/messenger/ImageLoader$HttpImageTask:isCancelled	()Z
      //   149: ifne +175 -> 324
      //   152: aload_0
      //   153: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   156: ifnull +50 -> 206
      //   159: aload_0
      //   160: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   163: instanceof 135
      //   166: ifeq +40 -> 206
      //   169: aload_0
      //   170: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   173: checkcast 135	java/net/HttpURLConnection
      //   176: invokevirtual 161	java/net/HttpURLConnection:getResponseCode	()I
      //   179: istore_2
      //   180: iload_2
      //   181: sipush 200
      //   184: if_icmpeq +22 -> 206
      //   187: iload_2
      //   188: sipush 202
      //   191: if_icmpeq +15 -> 206
      //   194: iload_2
      //   195: sipush 304
      //   198: if_icmpeq +8 -> 206
      //   201: aload_0
      //   202: iconst_0
      //   203: putfield 51	org/vidogram/messenger/ImageLoader$HttpImageTask:canRetry	Z
      //   206: aload_0
      //   207: getfield 55	org/vidogram/messenger/ImageLoader$HttpImageTask:imageSize	I
      //   210: ifne +83 -> 293
      //   213: aload_0
      //   214: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   217: ifnull +76 -> 293
      //   220: aload_0
      //   221: getfield 53	org/vidogram/messenger/ImageLoader$HttpImageTask:httpConnection	Ljava/net/URLConnection;
      //   224: invokevirtual 165	java/net/URLConnection:getHeaderFields	()Ljava/util/Map;
      //   227: astore 8
      //   229: aload 8
      //   231: ifnull +62 -> 293
      //   234: aload 8
      //   236: ldc 167
      //   238: invokeinterface 173 2 0
      //   243: checkcast 175	java/util/List
      //   246: astore 8
      //   248: aload 8
      //   250: ifnull +43 -> 293
      //   253: aload 8
      //   255: invokeinterface 178 1 0
      //   260: ifne +33 -> 293
      //   263: aload 8
      //   265: iconst_0
      //   266: invokeinterface 181 2 0
      //   271: checkcast 183	java/lang/String
      //   274: astore 8
      //   276: aload 8
      //   278: ifnull +15 -> 293
      //   281: aload_0
      //   282: aload 8
      //   284: invokestatic 187	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
      //   287: invokevirtual 192	java/lang/Integer:intValue	()I
      //   290: putfield 55	org/vidogram/messenger/ImageLoader$HttpImageTask:imageSize	I
      //   293: iload 7
      //   295: istore 5
      //   297: aload_1
      //   298: ifnull +26 -> 324
      //   301: sipush 8192
      //   304: newarray byte
      //   306: astore 8
      //   308: iconst_0
      //   309: istore_2
      //   310: aload_0
      //   311: invokevirtual 97	org/vidogram/messenger/ImageLoader$HttpImageTask:isCancelled	()Z
      //   314: istore 5
      //   316: iload 5
      //   318: ifeq +208 -> 526
      //   321: iconst_0
      //   322: istore 5
      //   324: aload_0
      //   325: getfield 49	org/vidogram/messenger/ImageLoader$HttpImageTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   328: ifnull +15 -> 343
      //   331: aload_0
      //   332: getfield 49	org/vidogram/messenger/ImageLoader$HttpImageTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   335: invokevirtual 195	java/io/RandomAccessFile:close	()V
      //   338: aload_0
      //   339: aconst_null
      //   340: putfield 49	org/vidogram/messenger/ImageLoader$HttpImageTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   343: aload_1
      //   344: ifnull +7 -> 351
      //   347: aload_1
      //   348: invokevirtual 198	java/io/InputStream:close	()V
      //   351: iload 5
      //   353: ifeq +47 -> 400
      //   356: aload_0
      //   357: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   360: getfield 152	org/vidogram/messenger/ImageLoader$CacheImage:tempFilePath	Ljava/io/File;
      //   363: ifnull +37 -> 400
      //   366: aload_0
      //   367: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   370: getfield 152	org/vidogram/messenger/ImageLoader$CacheImage:tempFilePath	Ljava/io/File;
      //   373: aload_0
      //   374: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   377: getfield 201	org/vidogram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   380: invokevirtual 207	java/io/File:renameTo	(Ljava/io/File;)Z
      //   383: ifne +17 -> 400
      //   386: aload_0
      //   387: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   390: aload_0
      //   391: getfield 47	org/vidogram/messenger/ImageLoader$HttpImageTask:cacheImage	Lorg/vidogram/messenger/ImageLoader$CacheImage;
      //   394: getfield 152	org/vidogram/messenger/ImageLoader$CacheImage:tempFilePath	Ljava/io/File;
      //   397: putfield 201	org/vidogram/messenger/ImageLoader$CacheImage:finalFilePath	Ljava/io/File;
      //   400: iload 5
      //   402: invokestatic 213	java/lang/Boolean:valueOf	(Z)Ljava/lang/Boolean;
      //   405: areturn
      //   406: astore 8
      //   408: aconst_null
      //   409: astore_1
      //   410: aload 8
      //   412: instanceof 215
      //   415: ifeq +22 -> 437
      //   418: invokestatic 220	org/vidogram/tgnet/ConnectionsManager:isNetworkOnline	()Z
      //   421: ifeq +8 -> 429
      //   424: aload_0
      //   425: iconst_0
      //   426: putfield 51	org/vidogram/messenger/ImageLoader$HttpImageTask:canRetry	Z
      //   429: aload 8
      //   431: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   434: goto -293 -> 141
      //   437: aload 8
      //   439: instanceof 228
      //   442: ifeq +11 -> 453
      //   445: aload_0
      //   446: iconst_0
      //   447: putfield 51	org/vidogram/messenger/ImageLoader$HttpImageTask:canRetry	Z
      //   450: goto -21 -> 429
      //   453: aload 8
      //   455: instanceof 230
      //   458: ifeq +32 -> 490
      //   461: aload 8
      //   463: invokevirtual 234	java/lang/Throwable:getMessage	()Ljava/lang/String;
      //   466: ifnull -37 -> 429
      //   469: aload 8
      //   471: invokevirtual 234	java/lang/Throwable:getMessage	()Ljava/lang/String;
      //   474: ldc 236
      //   476: invokevirtual 240	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
      //   479: ifeq -50 -> 429
      //   482: aload_0
      //   483: iconst_0
      //   484: putfield 51	org/vidogram/messenger/ImageLoader$HttpImageTask:canRetry	Z
      //   487: goto -58 -> 429
      //   490: aload 8
      //   492: instanceof 242
      //   495: ifeq -66 -> 429
      //   498: aload_0
      //   499: iconst_0
      //   500: putfield 51	org/vidogram/messenger/ImageLoader$HttpImageTask:canRetry	Z
      //   503: goto -74 -> 429
      //   506: astore 8
      //   508: aload 8
      //   510: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   513: goto -307 -> 206
      //   516: astore 8
      //   518: aload 8
      //   520: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   523: goto -230 -> 293
      //   526: aload_1
      //   527: aload 8
      //   529: invokevirtual 246	java/io/InputStream:read	([B)I
      //   532: istore 4
      //   534: iload 4
      //   536: ifle +69 -> 605
      //   539: iload_2
      //   540: iload 4
      //   542: iadd
      //   543: istore_3
      //   544: aload_0
      //   545: getfield 49	org/vidogram/messenger/ImageLoader$HttpImageTask:fileOutputStream	Ljava/io/RandomAccessFile;
      //   548: aload 8
      //   550: iconst_0
      //   551: iload 4
      //   553: invokevirtual 250	java/io/RandomAccessFile:write	([BII)V
      //   556: iload_3
      //   557: istore_2
      //   558: aload_0
      //   559: getfield 55	org/vidogram/messenger/ImageLoader$HttpImageTask:imageSize	I
      //   562: ifeq -252 -> 310
      //   565: aload_0
      //   566: iload_3
      //   567: i2f
      //   568: aload_0
      //   569: getfield 55	org/vidogram/messenger/ImageLoader$HttpImageTask:imageSize	I
      //   572: i2f
      //   573: fdiv
      //   574: invokespecial 252	org/vidogram/messenger/ImageLoader$HttpImageTask:reportProgress	(F)V
      //   577: iload_3
      //   578: istore_2
      //   579: goto -269 -> 310
      //   582: astore 8
      //   584: iconst_0
      //   585: istore 5
      //   587: aload 8
      //   589: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   592: goto -268 -> 324
      //   595: astore 8
      //   597: aload 8
      //   599: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   602: goto -278 -> 324
      //   605: iload 4
      //   607: iconst_m1
      //   608: if_icmpne +21 -> 629
      //   611: aload_0
      //   612: getfield 55	org/vidogram/messenger/ImageLoader$HttpImageTask:imageSize	I
      //   615: ifeq +8 -> 623
      //   618: aload_0
      //   619: fconst_1
      //   620: invokespecial 252	org/vidogram/messenger/ImageLoader$HttpImageTask:reportProgress	(F)V
      //   623: iconst_1
      //   624: istore 5
      //   626: goto -302 -> 324
      //   629: iconst_0
      //   630: istore 5
      //   632: goto -308 -> 324
      //   635: astore 8
      //   637: aload 8
      //   639: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   642: goto -299 -> 343
      //   645: astore_1
      //   646: aload_1
      //   647: invokestatic 226	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
      //   650: goto -299 -> 351
      //   653: astore 8
      //   655: iload 6
      //   657: istore 5
      //   659: goto -62 -> 597
      //   662: astore 8
      //   664: iconst_1
      //   665: istore 5
      //   667: goto -70 -> 597
      //   670: astore 8
      //   672: iconst_1
      //   673: istore 5
      //   675: goto -88 -> 587
      //   678: astore 8
      //   680: goto -270 -> 410
      //   683: aconst_null
      //   684: astore_1
      //   685: goto -544 -> 141
      //
      // Exception table:
      //   from	to	target	type
      //   15	99	406	java/lang/Throwable
      //   99	121	406	java/lang/Throwable
      //   152	180	506	java/lang/Exception
      //   201	206	506	java/lang/Exception
      //   220	229	516	java/lang/Exception
      //   234	248	516	java/lang/Exception
      //   253	276	516	java/lang/Exception
      //   281	293	516	java/lang/Exception
      //   526	534	582	java/lang/Exception
      //   544	556	582	java/lang/Exception
      //   558	577	582	java/lang/Exception
      //   587	592	595	java/lang/Throwable
      //   324	343	635	java/lang/Throwable
      //   347	351	645	java/lang/Throwable
      //   301	308	653	java/lang/Throwable
      //   310	316	653	java/lang/Throwable
      //   526	534	653	java/lang/Throwable
      //   544	556	653	java/lang/Throwable
      //   558	577	653	java/lang/Throwable
      //   611	623	662	java/lang/Throwable
      //   611	623	670	java/lang/Exception
      //   121	141	678	java/lang/Throwable
    }

    protected void onCancelled()
    {
      ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.runHttpTasks(true);
        }
      });
      Utilities.stageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          ImageLoader.this.fileProgresses.remove(ImageLoader.HttpImageTask.this.cacheImage.url);
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.access$300(ImageLoader.HttpImageTask.this).url, Integer.valueOf(1) });
            }
          });
        }
      });
    }

    protected void onPostExecute(Boolean paramBoolean)
    {
      if ((paramBoolean.booleanValue()) || (!this.canRetry))
        ImageLoader.this.fileDidLoaded(this.cacheImage.url, this.cacheImage.finalFilePath, 0);
      while (true)
      {
        Utilities.stageQueue.postRunnable(new Runnable(paramBoolean)
        {
          public void run()
          {
            ImageLoader.this.fileProgresses.remove(ImageLoader.HttpImageTask.this.cacheImage.url);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (ImageLoader.HttpImageTask.2.this.val$result.booleanValue())
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidLoaded, new Object[] { ImageLoader.HttpImageTask.access$300(ImageLoader.HttpImageTask.this).url });
                  return;
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.FileDidFailedLoad, new Object[] { ImageLoader.HttpImageTask.access$300(ImageLoader.HttpImageTask.this).url, Integer.valueOf(2) });
              }
            });
          }
        });
        ImageLoader.this.imageLoadQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            ImageLoader.this.runHttpTasks(true);
          }
        });
        return;
        ImageLoader.this.httpFileLoadError(this.cacheImage.url);
      }
    }
  }

  private class ThumbGenerateInfo
  {
    private int count;
    private TLRPC.FileLocation fileLocation;
    private String filter;

    private ThumbGenerateInfo()
    {
    }
  }

  private class ThumbGenerateTask
    implements Runnable
  {
    private String filter;
    private int mediaType;
    private File originalPath;
    private TLRPC.FileLocation thumbLocation;

    public ThumbGenerateTask(int paramFile, File paramFileLocation, TLRPC.FileLocation paramString, String arg5)
    {
      this.mediaType = paramFile;
      this.originalPath = paramFileLocation;
      this.thumbLocation = paramString;
      Object localObject;
      this.filter = localObject;
    }

    private void removeTask()
    {
      if (this.thumbLocation == null)
        return;
      String str = FileLoader.getAttachFileName(this.thumbLocation);
      ImageLoader.this.imageLoadQueue.postRunnable(new Runnable(str)
      {
        public void run()
        {
          ImageLoader.this.thumbGenerateTasks.remove(this.val$name);
        }
      });
    }

    public void run()
    {
      Object localObject1 = null;
      String str;
      File localFile;
      try
      {
        if (this.thumbLocation == null)
        {
          removeTask();
          return;
        }
        str = this.thumbLocation.volume_id + "_" + this.thumbLocation.local_id;
        localFile = new File(FileLoader.getInstance().getDirectory(4), "q_" + str + ".jpg");
        if ((localFile.exists()) || (!this.originalPath.exists()))
        {
          removeTask();
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        FileLog.e(localThrowable);
        removeTask();
        return;
      }
      int i = Math.min(180, Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) / 4);
      if (this.mediaType == 0)
        localObject2 = ImageLoader.loadBitmap(this.originalPath.toString(), null, i, i, false);
      while (localObject2 == null)
      {
        removeTask();
        return;
        if (this.mediaType == 2)
        {
          localObject2 = ThumbnailUtils.createVideoThumbnail(this.originalPath.toString(), 1);
          continue;
        }
        if (this.mediaType != 3)
          continue;
        localObject2 = this.originalPath.toString().toLowerCase();
        if ((!((String)localObject2).endsWith(".jpg")) && (!((String)localObject2).endsWith(".jpeg")) && (!((String)localObject2).endsWith(".png")) && (!((String)localObject2).endsWith(".gif")))
        {
          removeTask();
          return;
        }
        localObject2 = ImageLoader.loadBitmap((String)localObject2, null, i, i, false);
      }
      int j = ((Bitmap)localObject2).getWidth();
      int k = ((Bitmap)localObject2).getHeight();
      if ((j == 0) || (k == 0))
      {
        removeTask();
        return;
      }
      float f = Math.min(j / i, k / i);
      Bitmap localBitmap = Bitmaps.createScaledBitmap((Bitmap)localObject2, (int)(j / f), (int)(k / f), true);
      if (localBitmap != localObject2)
        ((Bitmap)localObject2).recycle();
      Object localObject2 = new FileOutputStream(localFile);
      localBitmap.compress(Bitmap.CompressFormat.JPEG, 60, (OutputStream)localObject2);
      try
      {
        ((FileOutputStream)localObject2).close();
        AndroidUtilities.runOnUIThread(new Runnable(str, new BitmapDrawable(localBitmap))
        {
          public void run()
          {
            ImageLoader.ThumbGenerateTask.this.removeTask();
            String str2 = this.val$key;
            String str1 = str2;
            if (ImageLoader.ThumbGenerateTask.this.filter != null)
              str1 = str2 + "@" + ImageLoader.ThumbGenerateTask.this.filter;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageThumbGenerated, new Object[] { this.val$bitmapDrawable, str1 });
            ImageLoader.this.memCache.put(str1, this.val$bitmapDrawable);
          }
        });
        return;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ImageLoader
 * JD-Core Version:    0.6.0
 */