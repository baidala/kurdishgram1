package org.vidogram.messenger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build.VERSION;
import android.system.Os;
import android.system.StructStat;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public class ClearCacheService extends IntentService
{
  public ClearCacheService()
  {
    super("ClearCacheService");
  }

  protected void onHandleIntent(Intent paramIntent)
  {
    ApplicationLoader.postInitApplication();
    int i = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
    if (i == 2)
      return;
    Utilities.globalQueue.postRunnable(new Runnable(i)
    {
      public void run()
      {
        long l1 = System.currentTimeMillis();
        if (this.val$keepMedia == 0);
        for (int i = 7; ; i = 30)
        {
          long l2 = i * 86400000;
          Iterator localIterator = ImageLoader.getInstance().createMediaPaths().entrySet().iterator();
          Object localObject1;
          do
          {
            if (!localIterator.hasNext())
              break;
            localObject1 = (Map.Entry)localIterator.next();
          }
          while (((Integer)((Map.Entry)localObject1).getKey()).intValue() == 4);
          while (true)
          {
            StructStat localStructStat;
            try
            {
              localObject1 = ((File)((Map.Entry)localObject1).getValue()).listFiles();
              if (localObject1 == null)
                break;
              i = 0;
              if (i >= localObject1.length)
                break;
              Object localObject2 = localObject1[i];
              if ((!localObject2.isFile()) || (localObject2.getName().equals(".nomedia")))
                break label252;
              int j = Build.VERSION.SDK_INT;
              if (j < 21)
                break label229;
              try
              {
                localStructStat = Os.stat(localObject2.getPath());
                if (localStructStat.st_atime == 0L)
                  break label207;
                if (localStructStat.st_atime + l2 >= l1)
                  break label252;
                localObject2.delete();
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
              }
            }
            catch (Throwable localThrowable)
            {
              FileLog.e(localThrowable);
            }
            break;
            label207: if (localStructStat.st_mtime + l2 < l1)
            {
              localException.delete();
              break label252;
              label229: if (localException.lastModified() + l2 < l1)
              {
                localException.delete();
                break label252;
                return;
              }
            }
            label252: i += 1;
          }
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ClearCacheService
 * JD-Core Version:    0.6.0
 */