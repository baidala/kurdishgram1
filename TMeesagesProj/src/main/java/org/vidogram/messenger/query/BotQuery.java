package org.vidogram.messenger.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.TLRPC.BotInfo;
import org.vidogram.tgnet.TLRPC.Message;

public class BotQuery
{
  private static HashMap<Integer, TLRPC.BotInfo> botInfos = new HashMap();
  private static HashMap<Long, TLRPC.Message> botKeyboards = new HashMap();
  private static HashMap<Integer, Long> botKeyboardsByMids = new HashMap();

  public static void cleanup()
  {
    botInfos.clear();
    botKeyboards.clear();
    botKeyboardsByMids.clear();
  }

  public static void clearBotKeyboard(long paramLong, ArrayList<Integer> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramLong)
    {
      public void run()
      {
        if (this.val$messages != null)
        {
          int i = 0;
          while (i < this.val$messages.size())
          {
            Long localLong = (Long)BotQuery.botKeyboardsByMids.get(this.val$messages.get(i));
            if (localLong != null)
            {
              BotQuery.botKeyboards.remove(localLong);
              BotQuery.botKeyboardsByMids.remove(this.val$messages.get(i));
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { null, localLong });
            }
            i += 1;
          }
        }
        BotQuery.botKeyboards.remove(Long.valueOf(this.val$did));
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { null, Long.valueOf(this.val$did) });
      }
    });
  }

  public static void loadBotInfo(int paramInt1, boolean paramBoolean, int paramInt2)
  {
    if (paramBoolean)
    {
      TLRPC.BotInfo localBotInfo = (TLRPC.BotInfo)botInfos.get(Integer.valueOf(paramInt1));
      if (localBotInfo != null)
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { localBotInfo, Integer.valueOf(paramInt2) });
        return;
      }
    }
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        Object localObject2 = null;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_info WHERE uid = %d", new Object[] { Integer.valueOf(this.val$uid) }), new Object[0]);
          Object localObject1 = localObject2;
          if (localSQLiteCursor.next())
          {
            localObject1 = localObject2;
            if (!localSQLiteCursor.isNull(0))
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              localObject1 = localObject2;
              if (localNativeByteBuffer != null)
              {
                localObject1 = TLRPC.BotInfo.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
              }
            }
          }
          localSQLiteCursor.dispose();
          if (localObject1 != null)
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.BotInfo)localObject1)
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botInfoDidLoaded, new Object[] { this.val$botInfoFinal, Integer.valueOf(BotQuery.3.this.val$classGuid) });
              }
            });
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void loadBotKeyboard(long paramLong)
  {
    TLRPC.Message localMessage = (TLRPC.Message)botKeyboards.get(Long.valueOf(paramLong));
    if (localMessage != null)
    {
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { localMessage, Long.valueOf(paramLong) });
      return;
    }
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong)
    {
      public void run()
      {
        Object localObject2 = null;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT info FROM bot_keyboard WHERE uid = %d", new Object[] { Long.valueOf(this.val$did) }), new Object[0]);
          Object localObject1 = localObject2;
          if (localSQLiteCursor.next())
          {
            localObject1 = localObject2;
            if (!localSQLiteCursor.isNull(0))
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
              localObject1 = localObject2;
              if (localNativeByteBuffer != null)
              {
                localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
              }
            }
          }
          localSQLiteCursor.dispose();
          if (localObject1 != null)
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.Message)localObject1)
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { this.val$botKeyboardFinal, Long.valueOf(BotQuery.2.this.val$did) });
              }
            });
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void putBotInfo(TLRPC.BotInfo paramBotInfo)
  {
    if (paramBotInfo == null)
      return;
    botInfos.put(Integer.valueOf(paramBotInfo.user_id), paramBotInfo);
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramBotInfo)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_info(uid, info) VALUES(?, ?)");
          localSQLitePreparedStatement.requery();
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$botInfo.getObjectSize());
          this.val$botInfo.serializeToStream(localNativeByteBuffer);
          localSQLitePreparedStatement.bindInteger(1, this.val$botInfo.user_id);
          localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localNativeByteBuffer.reuse();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void putBotKeyboard(long paramLong, TLRPC.Message paramMessage)
  {
    int i = 0;
    if (paramMessage == null);
    while (true)
    {
      return;
      try
      {
        Object localObject = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT mid FROM bot_keyboard WHERE uid = %d", new Object[] { Long.valueOf(paramLong) }), new Object[0]);
        if (((SQLiteCursor)localObject).next())
          i = ((SQLiteCursor)localObject).intValue(0);
        ((SQLiteCursor)localObject).dispose();
        if (i >= paramMessage.id)
          continue;
        localObject = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO bot_keyboard VALUES(?, ?, ?)");
        ((SQLitePreparedStatement)localObject).requery();
        NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(paramMessage.getObjectSize());
        paramMessage.serializeToStream(localNativeByteBuffer);
        ((SQLitePreparedStatement)localObject).bindLong(1, paramLong);
        ((SQLitePreparedStatement)localObject).bindInteger(2, paramMessage.id);
        ((SQLitePreparedStatement)localObject).bindByteBuffer(3, localNativeByteBuffer);
        ((SQLitePreparedStatement)localObject).step();
        localNativeByteBuffer.reuse();
        ((SQLitePreparedStatement)localObject).dispose();
        AndroidUtilities.runOnUIThread(new Runnable(paramLong, paramMessage)
        {
          public void run()
          {
            TLRPC.Message localMessage = (TLRPC.Message)BotQuery.botKeyboards.put(Long.valueOf(this.val$did), this.val$message);
            if (localMessage != null)
              BotQuery.botKeyboardsByMids.remove(Integer.valueOf(localMessage.id));
            BotQuery.botKeyboardsByMids.put(Integer.valueOf(this.val$message.id), Long.valueOf(this.val$did));
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.botKeyboardDidLoaded, new Object[] { this.val$message, Long.valueOf(this.val$did) });
          }
        });
        return;
      }
      catch (Exception paramMessage)
      {
        FileLog.e(paramMessage);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.BotQuery
 * JD-Core Version:    0.6.0
 */