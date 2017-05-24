package org.vidogram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.lang.reflect.Array;

public class StatsController
{
  private static volatile StatsController Instance;
  private static final int TYPES_COUNT = 7;
  public static final int TYPE_AUDIOS = 3;
  public static final int TYPE_CALLS = 0;
  public static final int TYPE_FILES = 5;
  public static final int TYPE_MESSAGES = 1;
  public static final int TYPE_MOBILE = 0;
  public static final int TYPE_PHOTOS = 4;
  public static final int TYPE_ROAMING = 2;
  public static final int TYPE_TOTAL = 6;
  public static final int TYPE_VIDEOS = 2;
  public static final int TYPE_WIFI = 1;
  private static final ThreadLocal<Long> lastStatsSaveTime = new ThreadLocal()
  {
    protected Long initialValue()
    {
      return Long.valueOf(System.currentTimeMillis() - 1000L);
    }
  };
  private int[] callsTotalTime = new int[3];
  private SharedPreferences.Editor editor;
  private long[][] receivedBytes = (long[][])Array.newInstance(Long.TYPE, new int[] { 3, 7 });
  private int[][] receivedItems = (int[][])Array.newInstance(Integer.TYPE, new int[] { 3, 7 });
  private long[] resetStatsDate = new long[3];
  private long[][] sentBytes = (long[][])Array.newInstance(Long.TYPE, new int[] { 3, 7 });
  private int[][] sentItems = (int[][])Array.newInstance(Integer.TYPE, new int[] { 3, 7 });
  private DispatchQueue statsSaveQueue = new DispatchQueue("statsSaveQueue");

  static
  {
    Instance = null;
  }

  private StatsController()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("stats", 0);
    this.editor = localSharedPreferences.edit();
    int i = 0;
    int j = 0;
    while (i < 3)
    {
      this.callsTotalTime[i] = localSharedPreferences.getInt("callsTotalTime" + i, 0);
      this.resetStatsDate[i] = localSharedPreferences.getLong("resetStatsDate" + i, 0L);
      int k = 0;
      while (k < 7)
      {
        this.sentBytes[i][k] = localSharedPreferences.getLong("sentBytes" + i + "_" + k, 0L);
        this.receivedBytes[i][k] = localSharedPreferences.getLong("receivedBytes" + i + "_" + k, 0L);
        this.sentItems[i][k] = localSharedPreferences.getInt("sentItems" + i + "_" + k, 0);
        this.receivedItems[i][k] = localSharedPreferences.getInt("receivedItems" + i + "_" + k, 0);
        k += 1;
      }
      if (this.resetStatsDate[i] == 0L)
      {
        j = 1;
        this.resetStatsDate[i] = System.currentTimeMillis();
      }
      i += 1;
    }
    if (j != 0)
      saveStats();
  }

  public static StatsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        StatsController localStatsController = Instance;
        localObject1 = localStatsController;
        if (localStatsController == null)
        {
          localObject1 = new StatsController();
          Instance = (StatsController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (StatsController)localObject2;
  }

  private void saveStats()
  {
    long l = System.currentTimeMillis();
    if (Math.abs(l - ((Long)lastStatsSaveTime.get()).longValue()) >= 1000L)
    {
      lastStatsSaveTime.set(Long.valueOf(l));
      this.statsSaveQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          int i = 0;
          while (i < 3)
          {
            int j = 0;
            while (j < 7)
            {
              StatsController.this.editor.putInt("receivedItems" + i + "_" + j, StatsController.this.receivedItems[i][j]);
              StatsController.this.editor.putInt("sentItems" + i + "_" + j, StatsController.this.sentItems[i][j]);
              StatsController.this.editor.putLong("receivedBytes" + i + "_" + j, StatsController.this.receivedBytes[i][j]);
              StatsController.this.editor.putLong("sentBytes" + i + "_" + j, StatsController.this.sentBytes[i][j]);
              j += 1;
            }
            StatsController.this.editor.putInt("callsTotalTime" + i, StatsController.this.callsTotalTime[i]);
            StatsController.this.editor.putLong("resetStatsDate" + i, StatsController.this.resetStatsDate[i]);
            i += 1;
          }
          try
          {
            StatsController.this.editor.commit();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }
  }

  public int getCallsTotalTime(int paramInt)
  {
    return this.callsTotalTime[paramInt];
  }

  public long getReceivedBytesCount(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1)
      return this.receivedBytes[paramInt1][6] - this.receivedBytes[paramInt1][5] - this.receivedBytes[paramInt1][3] - this.receivedBytes[paramInt1][2] - this.receivedBytes[paramInt1][4];
    return this.receivedBytes[paramInt1][paramInt2];
  }

  public int getRecivedItemsCount(int paramInt1, int paramInt2)
  {
    return this.receivedItems[paramInt1][paramInt2];
  }

  public long getResetStatsDate(int paramInt)
  {
    return this.resetStatsDate[paramInt];
  }

  public long getSentBytesCount(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1)
      return this.sentBytes[paramInt1][6] - this.sentBytes[paramInt1][5] - this.sentBytes[paramInt1][3] - this.sentBytes[paramInt1][2] - this.sentBytes[paramInt1][4];
    return this.sentBytes[paramInt1][paramInt2];
  }

  public int getSentItemsCount(int paramInt1, int paramInt2)
  {
    return this.sentItems[paramInt1][paramInt2];
  }

  public void incrementReceivedBytesCount(int paramInt1, int paramInt2, long paramLong)
  {
    long[] arrayOfLong = this.receivedBytes[paramInt1];
    arrayOfLong[paramInt2] += paramLong;
    saveStats();
  }

  public void incrementReceivedItemsCount(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = this.receivedItems[paramInt1];
    arrayOfInt[paramInt2] += paramInt3;
    saveStats();
  }

  public void incrementSentBytesCount(int paramInt1, int paramInt2, long paramLong)
  {
    long[] arrayOfLong = this.sentBytes[paramInt1];
    arrayOfLong[paramInt2] += paramLong;
    saveStats();
  }

  public void incrementSentItemsCount(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = this.sentItems[paramInt1];
    arrayOfInt[paramInt2] += paramInt3;
    saveStats();
  }

  public void incrementTotalCallsTime(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.callsTotalTime;
    arrayOfInt[paramInt1] += paramInt2;
    saveStats();
  }

  public void resetStats(int paramInt)
  {
    this.resetStatsDate[paramInt] = System.currentTimeMillis();
    int i = 0;
    while (i < 7)
    {
      this.sentBytes[paramInt][i] = 0L;
      this.receivedBytes[paramInt][i] = 0L;
      this.sentItems[paramInt][i] = 0;
      this.receivedItems[paramInt][i] = 0;
      i += 1;
    }
    this.callsTotalTime[paramInt] = 0;
    saveStats();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.StatsController
 * JD-Core Version:    0.6.0
 */