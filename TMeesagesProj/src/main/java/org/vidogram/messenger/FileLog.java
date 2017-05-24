package org.vidogram.messenger;

import android.content.Context;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;
import org.vidogram.messenger.time.FastDateFormat;

public class FileLog
{
  private static volatile FileLog Instance = null;
  private File currentFile = null;
  private FastDateFormat dateFormat = null;
  private DispatchQueue logQueue = null;
  private File networkFile = null;
  private OutputStreamWriter streamWriter = null;

  public FileLog()
  {
    if (!BuildVars.DEBUG_VERSION);
    while (true)
    {
      return;
      this.dateFormat = FastDateFormat.getInstance("dd_MM_yyyy_HH_mm_ss", Locale.US);
      try
      {
        File localFile = ApplicationLoader.applicationContext.getExternalFilesDir(null);
        if (localFile == null)
          continue;
        localFile = new File(localFile.getAbsolutePath() + "/logs");
        localFile.mkdirs();
        this.currentFile = new File(localFile, this.dateFormat.format(System.currentTimeMillis()) + ".txt");
        try
        {
          this.logQueue = new DispatchQueue("logQueue");
          this.currentFile.createNewFile();
          this.streamWriter = new OutputStreamWriter(new FileOutputStream(this.currentFile));
          this.streamWriter.write("-----start log " + this.dateFormat.format(System.currentTimeMillis()) + "-----\n");
          this.streamWriter.flush();
          return;
        }
        catch (Exception localException1)
        {
          localException1.printStackTrace();
          return;
        }
      }
      catch (Exception localException2)
      {
        while (true)
          localException2.printStackTrace();
      }
    }
  }

  public static void cleanupLogs()
  {
    Object localObject1 = ApplicationLoader.applicationContext.getExternalFilesDir(null);
    if (localObject1 == null);
    do
    {
      return;
      localObject1 = new File(((File)localObject1).getAbsolutePath() + "/logs").listFiles();
    }
    while (localObject1 == null);
    int i = 0;
    label52: Object localObject2;
    if (i < localObject1.length)
    {
      localObject2 = localObject1[i];
      if ((getInstance().currentFile == null) || (!localObject2.getAbsolutePath().equals(getInstance().currentFile.getAbsolutePath())))
        break label97;
    }
    while (true)
    {
      i += 1;
      break label52;
      break;
      label97: if ((getInstance().networkFile != null) && (localObject2.getAbsolutePath().equals(getInstance().networkFile.getAbsolutePath())))
        continue;
      localObject2.delete();
    }
  }

  public static void d(String paramString)
  {
    if (!BuildVars.DEBUG_VERSION);
    do
    {
      return;
      Log.d("tmessages", paramString);
    }
    while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " D/tmessages: " + this.val$message + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }

  public static void e(String paramString)
  {
    if (!BuildVars.DEBUG_VERSION);
    do
    {
      return;
      Log.e("tmessages", paramString);
    }
    while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$message + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }

  public static void e(String paramString, Throwable paramThrowable)
  {
    if (!BuildVars.DEBUG_VERSION);
    do
    {
      return;
      Log.e("tmessages", paramString, paramThrowable);
    }
    while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable(paramString, paramThrowable)
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$message + "\n");
          FileLog.getInstance().streamWriter.write(this.val$exception.toString());
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }

  public static void e(Throwable paramThrowable)
  {
    if (!BuildVars.DEBUG_VERSION)
      return;
    paramThrowable.printStackTrace();
    if (getInstance().streamWriter != null)
    {
      getInstance().logQueue.postRunnable(new Runnable(paramThrowable)
      {
        public void run()
        {
          try
          {
            FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + this.val$e + "\n");
            StackTraceElement[] arrayOfStackTraceElement = this.val$e.getStackTrace();
            int i = 0;
            while (i < arrayOfStackTraceElement.length)
            {
              FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " E/tmessages: " + arrayOfStackTraceElement[i] + "\n");
              i += 1;
            }
            FileLog.getInstance().streamWriter.flush();
            return;
          }
          catch (Exception localException)
          {
            localException.printStackTrace();
          }
        }
      });
      return;
    }
    paramThrowable.printStackTrace();
  }

  public static FileLog getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        FileLog localFileLog = Instance;
        localObject1 = localFileLog;
        if (localFileLog == null)
        {
          localObject1 = new FileLog();
          Instance = (FileLog)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (FileLog)localObject2;
  }

  public static String getNetworkLogPath()
  {
    if (!BuildVars.DEBUG_VERSION)
      return "";
    try
    {
      Object localObject = ApplicationLoader.applicationContext.getExternalFilesDir(null);
      if (localObject == null)
        return "";
      localObject = new File(((File)localObject).getAbsolutePath() + "/logs");
      ((File)localObject).mkdirs();
      getInstance().networkFile = new File((File)localObject, getInstance().dateFormat.format(System.currentTimeMillis()) + "_net.txt");
      localObject = getInstance().networkFile.getAbsolutePath();
      return localObject;
    }
    catch (Throwable localThrowable)
    {
      localThrowable.printStackTrace();
    }
    return (String)"";
  }

  public static void w(String paramString)
  {
    if (!BuildVars.DEBUG_VERSION);
    do
    {
      return;
      Log.w("tmessages", paramString);
    }
    while (getInstance().streamWriter == null);
    getInstance().logQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        try
        {
          FileLog.getInstance().streamWriter.write(FileLog.getInstance().dateFormat.format(System.currentTimeMillis()) + " W/tmessages: " + this.val$message + "\n");
          FileLog.getInstance().streamWriter.flush();
          return;
        }
        catch (Exception localException)
        {
          localException.printStackTrace();
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.FileLog
 * JD-Core Version:    0.6.0
 */