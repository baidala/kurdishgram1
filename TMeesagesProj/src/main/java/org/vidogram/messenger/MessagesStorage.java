package org.vidogram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseIntArray;
import java.io.File;
import java.util.ArrayList;
import java.util.ArrayList<Ljava.lang.Long;>;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.a.b;
import org.vidogram.messenger.query.BotQuery;
import org.vidogram.messenger.query.SharedMediaQuery;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.ChannelParticipant;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.InputChannel;
import org.vidogram.tgnet.TLRPC.InputMedia;
import org.vidogram.tgnet.TLRPC.InputPeer;
import org.vidogram.tgnet.TLRPC.InputUser;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.PeerNotifySettings;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.ReplyMarkup;
import org.vidogram.tgnet.TLRPC.TL_channelFull;
import org.vidogram.tgnet.TLRPC.TL_channels_deleteMessages;
import org.vidogram.tgnet.TLRPC.TL_chatFull;
import org.vidogram.tgnet.TLRPC.TL_chatInviteEmpty;
import org.vidogram.tgnet.TLRPC.TL_chatParticipant;
import org.vidogram.tgnet.TLRPC.TL_chatParticipantAdmin;
import org.vidogram.tgnet.TLRPC.TL_contact;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_inputMediaGame;
import org.vidogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.vidogram.tgnet.TLRPC.TL_messageActionGameScore;
import org.vidogram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.vidogram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.vidogram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.vidogram.tgnet.TLRPC.TL_messageEntityMentionName;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.vidogram.tgnet.TLRPC.TL_messageMediaUnsupported_old;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_message_secret;
import org.vidogram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.vidogram.tgnet.TLRPC.TL_messages_botResults;
import org.vidogram.tgnet.TLRPC.TL_messages_deleteMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_dialogs;
import org.vidogram.tgnet.TLRPC.TL_peerChannel;
import org.vidogram.tgnet.TLRPC.TL_peerNotifySettingsEmpty;
import org.vidogram.tgnet.TLRPC.TL_photoEmpty;
import org.vidogram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.vidogram.tgnet.TLRPC.TL_updates_channelDifferenceTooLong;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastMonth;
import org.vidogram.tgnet.TLRPC.TL_userStatusLastWeek;
import org.vidogram.tgnet.TLRPC.TL_userStatusRecently;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.tgnet.TLRPC.WallPaper;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.tgnet.TLRPC.messages_Dialogs;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.tgnet.TLRPC.photos_Photos;

public class MessagesStorage
{
  private static volatile MessagesStorage Instance;
  public static int lastDateValue = 0;
  public static int lastPtsValue = 0;
  public static int lastQtsValue = 0;
  public static int lastSecretVersion;
  public static int lastSeqValue = 0;
  public static int secretG;
  public static byte[] secretPBytes;
  private File cacheFile;
  private SQLiteDatabase database;
  private int lastSavedDate = 0;
  private int lastSavedPts = 0;
  private int lastSavedQts = 0;
  private int lastSavedSeq = 0;
  private AtomicLong lastTaskId = new AtomicLong(System.currentTimeMillis());
  private DispatchQueue storageQueue = new DispatchQueue("storageQueue");

  static
  {
    lastSecretVersion = 0;
    secretPBytes = null;
    secretG = 0;
    Instance = null;
  }

  public MessagesStorage()
  {
    this.storageQueue.setPriority(10);
    openDatabase(true);
  }

  public static void addUsersAndChatsFromMessage(TLRPC.Message paramMessage, ArrayList<Integer> paramArrayList1, ArrayList<Integer> paramArrayList2)
  {
    int j = 0;
    if (paramMessage.from_id != 0)
    {
      if (paramMessage.from_id <= 0)
        break label277;
      if (!paramArrayList1.contains(Integer.valueOf(paramMessage.from_id)))
        paramArrayList1.add(Integer.valueOf(paramMessage.from_id));
    }
    int i;
    Object localObject;
    while (true)
    {
      if ((paramMessage.via_bot_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.via_bot_id))))
        paramArrayList1.add(Integer.valueOf(paramMessage.via_bot_id));
      if (paramMessage.action == null)
        break;
      if ((paramMessage.action.user_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.action.user_id))))
        paramArrayList1.add(Integer.valueOf(paramMessage.action.user_id));
      if ((paramMessage.action.channel_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.action.channel_id))))
        paramArrayList2.add(Integer.valueOf(paramMessage.action.channel_id));
      if ((paramMessage.action.chat_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.action.chat_id))))
        paramArrayList2.add(Integer.valueOf(paramMessage.action.chat_id));
      if (paramMessage.action.users.isEmpty())
        break;
      i = 0;
      while (i < paramMessage.action.users.size())
      {
        localObject = (Integer)paramMessage.action.users.get(i);
        if (!paramArrayList1.contains(localObject))
          paramArrayList1.add(localObject);
        i += 1;
      }
      label277: if (paramArrayList2.contains(Integer.valueOf(-paramMessage.from_id)))
        continue;
      paramArrayList2.add(Integer.valueOf(-paramMessage.from_id));
    }
    if (!paramMessage.entities.isEmpty())
    {
      i = j;
      if (i < paramMessage.entities.size())
      {
        localObject = (TLRPC.MessageEntity)paramMessage.entities.get(i);
        if ((localObject instanceof TLRPC.TL_messageEntityMentionName))
          paramArrayList1.add(Integer.valueOf(((TLRPC.TL_messageEntityMentionName)localObject).user_id));
        while (true)
        {
          i += 1;
          break;
          if (!(localObject instanceof TLRPC.TL_inputMessageEntityMentionName))
            continue;
          paramArrayList1.add(Integer.valueOf(((TLRPC.TL_inputMessageEntityMentionName)localObject).user_id.user_id));
        }
      }
    }
    if ((paramMessage.media != null) && (paramMessage.media.user_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.media.user_id))))
      paramArrayList1.add(Integer.valueOf(paramMessage.media.user_id));
    if (paramMessage.fwd_from != null)
    {
      if ((paramMessage.fwd_from.from_id != 0) && (!paramArrayList1.contains(Integer.valueOf(paramMessage.fwd_from.from_id))))
        paramArrayList1.add(Integer.valueOf(paramMessage.fwd_from.from_id));
      if ((paramMessage.fwd_from.channel_id != 0) && (!paramArrayList2.contains(Integer.valueOf(paramMessage.fwd_from.channel_id))))
        paramArrayList2.add(Integer.valueOf(paramMessage.fwd_from.channel_id));
    }
    if ((paramMessage.ttl < 0) && (!paramArrayList2.contains(Integer.valueOf(-paramMessage.ttl))))
      paramArrayList2.add(Integer.valueOf(-paramMessage.ttl));
  }

  private void cleanupInternal()
  {
    lastDateValue = 0;
    lastSeqValue = 0;
    lastPtsValue = 0;
    lastQtsValue = 0;
    lastSecretVersion = 0;
    this.lastSavedSeq = 0;
    this.lastSavedPts = 0;
    this.lastSavedDate = 0;
    this.lastSavedQts = 0;
    secretPBytes = null;
    secretG = 0;
    if (this.database != null)
    {
      this.database.close();
      this.database = null;
    }
    if (this.cacheFile != null)
    {
      this.cacheFile.delete();
      this.cacheFile = null;
    }
  }

  private void closeHolesInTable(String paramString, long paramLong, int paramInt1, int paramInt2)
  {
    while (true)
    {
      int i;
      int j;
      try
      {
        Object localObject = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM " + paramString + " WHERE uid = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
        ArrayList localArrayList = null;
        if (!((SQLiteCursor)localObject).next())
          continue;
        if (localArrayList != null)
          break label793;
        localArrayList = new ArrayList();
        i = ((SQLiteCursor)localObject).intValue(0);
        j = ((SQLiteCursor)localObject).intValue(1);
        if ((i == j) && (i == 1))
          continue;
        localArrayList.add(new Hole(i, j));
        continue;
        ((SQLiteCursor)localObject).dispose();
        if (localArrayList != null)
        {
          i = 0;
          if (i < localArrayList.size())
          {
            localObject = (Hole)localArrayList.get(i);
            if ((paramInt2 < ((Hole)localObject).end - 1) || (paramInt1 > ((Hole)localObject).start + 1))
              continue;
            this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject).start), Integer.valueOf(((Hole)localObject).end) })).stepThis().dispose();
            break label796;
            if (paramInt2 < ((Hole)localObject).end - 1)
              break label473;
            j = ((Hole)localObject).end;
            if (j == paramInt1)
              break label796;
            try
            {
              this.database.executeFast(String.format(Locale.US, "UPDATE " + paramString + " SET end = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject).start), Integer.valueOf(((Hole)localObject).end) })).stepThis().dispose();
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
            }
          }
        }
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
      }
      return;
      label473: if (paramInt1 <= localException1.start + 1)
      {
        j = localException1.start;
        if (j != paramInt2)
          try
          {
            this.database.executeFast(String.format(Locale.US, "UPDATE " + paramString + " SET start = %d WHERE uid = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException1.start), Integer.valueOf(localException1.end) })).stepThis().dispose();
          }
          catch (Exception localException2)
          {
            FileLog.e(localException2);
          }
      }
      else
      {
        this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
        SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO " + paramString + " VALUES(?, ?, ?)");
        localSQLitePreparedStatement.requery();
        localSQLitePreparedStatement.bindLong(1, paramLong);
        localSQLitePreparedStatement.bindInteger(2, localException2.start);
        localSQLitePreparedStatement.bindInteger(3, paramInt1);
        localSQLitePreparedStatement.step();
        localSQLitePreparedStatement.requery();
        localSQLitePreparedStatement.bindLong(1, paramLong);
        localSQLitePreparedStatement.bindInteger(2, paramInt2);
        localSQLitePreparedStatement.bindInteger(3, localException2.end);
        localSQLitePreparedStatement.step();
        localSQLitePreparedStatement.dispose();
        break label796;
        continue;
      }
      label793: label796: i += 1;
    }
  }

  public static void createFirstHoles(long paramLong, SQLitePreparedStatement paramSQLitePreparedStatement1, SQLitePreparedStatement paramSQLitePreparedStatement2, int paramInt)
  {
    paramSQLitePreparedStatement1.requery();
    paramSQLitePreparedStatement1.bindLong(1, paramLong);
    int i;
    if (paramInt == 1)
    {
      i = 1;
      paramSQLitePreparedStatement1.bindInteger(2, i);
      paramSQLitePreparedStatement1.bindInteger(3, paramInt);
      paramSQLitePreparedStatement1.step();
      i = 0;
      label41: if (i >= 5)
        return;
      paramSQLitePreparedStatement2.requery();
      paramSQLitePreparedStatement2.bindLong(1, paramLong);
      paramSQLitePreparedStatement2.bindInteger(2, i);
      if (paramInt != 1)
        break label107;
    }
    label107: for (int j = 1; ; j = 0)
    {
      paramSQLitePreparedStatement2.bindInteger(3, j);
      paramSQLitePreparedStatement2.bindInteger(4, paramInt);
      paramSQLitePreparedStatement2.step();
      i += 1;
      break label41;
      i = 0;
      break;
    }
  }

  private void doneHolesInTable(String paramString, long paramLong, int paramInt)
  {
    if (paramInt == 0)
      this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
    while (true)
    {
      paramString = this.database.executeFast("REPLACE INTO " + paramString + " VALUES(?, ?, ?)");
      paramString.requery();
      paramString.bindLong(1, paramLong);
      paramString.bindInteger(2, 1);
      paramString.bindInteger(3, 1);
      paramString.step();
      paramString.dispose();
      return;
      this.database.executeFast(String.format(Locale.US, "DELETE FROM " + paramString + " WHERE uid = %d AND start = 0", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
    }
  }

  private void fixNotificationSettings()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        while (true)
        {
          Object localObject1;
          Object localObject2;
          int i;
          try
          {
            HashMap localHashMap = new HashMap();
            localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getAll();
            localObject2 = ((Map)localObject1).entrySet().iterator();
            if (!((Iterator)localObject2).hasNext())
              break label242;
            localObject3 = (Map.Entry)((Iterator)localObject2).next();
            String str = (String)((Map.Entry)localObject3).getKey();
            if (!str.startsWith("notify2_"))
              continue;
            localObject3 = (Integer)((Map.Entry)localObject3).getValue();
            if ((((Integer)localObject3).intValue() != 2) && (((Integer)localObject3).intValue() != 3))
              break label228;
            str = str.replace("notify2_", "");
            i = ((Integer)localObject3).intValue();
            if (i == 2)
            {
              l = 1L;
              try
              {
                localHashMap.put(Long.valueOf(Long.parseLong(str)), Long.valueOf(l));
              }
              catch (Exception localException2)
              {
                localException2.printStackTrace();
              }
              continue;
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
            return;
          }
          Object localObject3 = (Integer)((Map)localObject1).get("notifyuntil_" + localException2);
          if (localObject3 != null)
          {
            l = ((Integer)localObject3).intValue() << 32 | 1L;
            continue;
            label228: i = ((Integer)localObject3).intValue();
            if (i != 3)
              continue;
            continue;
            try
            {
              label242: MessagesStorage.this.database.beginTransaction();
              localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
              Iterator localIterator = localThrowable.entrySet().iterator();
              while (localIterator.hasNext())
              {
                localObject2 = (Map.Entry)localIterator.next();
                ((SQLitePreparedStatement)localObject1).requery();
                ((SQLitePreparedStatement)localObject1).bindLong(1, ((Long)((Map.Entry)localObject2).getKey()).longValue());
                ((SQLitePreparedStatement)localObject1).bindLong(2, ((Long)((Map.Entry)localObject2).getValue()).longValue());
                ((SQLitePreparedStatement)localObject1).step();
              }
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
              return;
            }
            ((SQLitePreparedStatement)localObject1).dispose();
            MessagesStorage.this.database.commitTransaction();
            return;
          }
          long l = 1L;
        }
      }
    });
  }

  private void fixUnsupportedMedia(TLRPC.Message paramMessage)
  {
    if (paramMessage == null);
    do
      while (true)
      {
        return;
        if (!(paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported_old))
          break;
        if (paramMessage.media.bytes.length != 0)
          continue;
        paramMessage.media.bytes = new byte[1];
        paramMessage.media.bytes[0] = 65;
        return;
      }
    while (!(paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported));
    paramMessage.media = new TLRPC.TL_messageMediaUnsupported_old();
    paramMessage.media.bytes = new byte[1];
    paramMessage.media.bytes[0] = 65;
    paramMessage.flags |= 512;
  }

  private String formatUserSearchName(TLRPC.User paramUser)
  {
    StringBuilder localStringBuilder = new StringBuilder("");
    if ((paramUser.first_name != null) && (paramUser.first_name.length() > 0))
      localStringBuilder.append(paramUser.first_name);
    if ((paramUser.last_name != null) && (paramUser.last_name.length() > 0))
    {
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(" ");
      localStringBuilder.append(paramUser.last_name);
    }
    localStringBuilder.append(";;;");
    if ((paramUser.username != null) && (paramUser.username.length() > 0))
      localStringBuilder.append(paramUser.username);
    return localStringBuilder.toString().toLowerCase();
  }

  public static MessagesStorage getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        MessagesStorage localMessagesStorage = Instance;
        localObject1 = localMessagesStorage;
        if (localMessagesStorage == null)
        {
          localObject1 = new MessagesStorage();
          Instance = (MessagesStorage)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (MessagesStorage)localObject2;
  }

  private int getMessageMediaType(TLRPC.Message paramMessage)
  {
    if (((paramMessage instanceof TLRPC.TL_message_secret)) && ((((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.ttl > 0) && (paramMessage.ttl <= 60)) || (MessageObject.isVoiceMessage(paramMessage)) || (MessageObject.isVideoMessage(paramMessage))))
      return 1;
    if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || (MessageObject.isVideoMessage(paramMessage)))
      return 0;
    return -1;
  }

  private boolean isValidKeyboardToSave(TLRPC.Message paramMessage)
  {
    return (paramMessage.reply_markup != null) && (!(paramMessage.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && ((!paramMessage.reply_markup.selective) || (paramMessage.mentioned));
  }

  private void loadPendingTasks()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        while (true)
        {
          SQLiteCursor localSQLiteCursor;
          long l1;
          NativeByteBuffer localNativeByteBuffer;
          try
          {
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT id, data FROM pending_tasks WHERE 1", new Object[0]);
            if (!localSQLiteCursor.next())
              break label561;
            l1 = localSQLiteCursor.longValue(0);
            localNativeByteBuffer = localSQLiteCursor.byteBufferValue(1);
            if (localNativeByteBuffer == null)
              continue;
            i = localNativeByteBuffer.readInt32(false);
            switch (i)
            {
            case 0:
              localNativeByteBuffer.reuse();
              continue;
            case 1:
            case 2:
            case 5:
            case 3:
            case 4:
            case 6:
            case 7:
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          Object localObject1 = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          if (localObject1 == null)
            continue;
          Utilities.stageQueue.postRunnable(new Runnable((TLRPC.Chat)localObject1, l1)
          {
            public void run()
            {
              MessagesController.getInstance().loadUnknownChannel(this.val$chat, this.val$taskId);
            }
          });
          continue;
          int i = localNativeByteBuffer.readInt32(false);
          int j = localNativeByteBuffer.readInt32(false);
          Utilities.stageQueue.postRunnable(new Runnable(i, j, l1)
          {
            public void run()
            {
              MessagesController.getInstance().getChannelDifference(this.val$channelId, this.val$newDialogType, this.val$taskId, null);
            }
          });
          continue;
          localObject1 = new TLRPC.TL_dialog();
          ((TLRPC.TL_dialog)localObject1).id = localNativeByteBuffer.readInt64(false);
          ((TLRPC.TL_dialog)localObject1).top_message = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).read_inbox_max_id = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).read_outbox_max_id = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).unread_count = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).last_message_date = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).pts = localNativeByteBuffer.readInt32(false);
          ((TLRPC.TL_dialog)localObject1).flags = localNativeByteBuffer.readInt32(false);
          if (i == 5)
          {
            ((TLRPC.TL_dialog)localObject1).pinned = localNativeByteBuffer.readBool(false);
            ((TLRPC.TL_dialog)localObject1).pinnedNum = localNativeByteBuffer.readInt32(false);
          }
          AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_dialog)localObject1, TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false), l1)
          {
            public void run()
            {
              MessagesController.getInstance().checkLastDialogMessage(this.val$dialog, this.val$peer, this.val$taskId);
            }
          });
          continue;
          long l2 = localNativeByteBuffer.readInt64(false);
          localObject1 = TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          Object localObject2 = (TLRPC.TL_inputMediaGame)TLRPC.InputMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          SendMessagesHelper.getInstance().sendGame((TLRPC.InputPeer)localObject1, (TLRPC.TL_inputMediaGame)localObject2, l2, l1);
          continue;
          AndroidUtilities.runOnUIThread(new Runnable(localNativeByteBuffer.readInt64(false), localNativeByteBuffer.readBool(false), TLRPC.InputPeer.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false), l1)
          {
            public void run()
            {
              MessagesController.getInstance().pinDialog(this.val$did, this.val$pin, this.val$peer, this.val$taskId);
            }
          });
          continue;
          i = localNativeByteBuffer.readInt32(false);
          j = localNativeByteBuffer.readInt32(false);
          localObject1 = TLRPC.InputChannel.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
          Utilities.stageQueue.postRunnable(new Runnable(i, j, l1, (TLRPC.InputChannel)localObject1)
          {
            public void run()
            {
              MessagesController.getInstance().getChannelDifference(this.val$channelId, this.val$newDialogType, this.val$taskId, this.val$inputChannel);
            }
          });
          continue;
          i = localNativeByteBuffer.readInt32(false);
          j = localNativeByteBuffer.readInt32(false);
          localObject2 = TLRPC.TL_messages_deleteMessages.TLdeserialize(localNativeByteBuffer, j, false);
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = TLRPC.TL_channels_deleteMessages.TLdeserialize(localNativeByteBuffer, j, false);
          if (localObject1 == null)
          {
            MessagesStorage.this.removePendingTask(l1);
            continue;
          }
          AndroidUtilities.runOnUIThread(new Runnable(i, l1, (TLObject)localObject1)
          {
            public void run()
            {
              MessagesController.getInstance().deleteMessages(null, null, null, this.val$channelId, true, this.val$taskId, this.val$finalRequest);
            }
          });
          continue;
          label561: localSQLiteCursor.dispose();
          return;
        }
      }
    });
  }

  private ArrayList<Long> markMessagesAsDeletedInternal(ArrayList<Integer> paramArrayList, int paramInt)
  {
    ArrayList localArrayList1;
    Object localObject1;
    while (true)
    {
      ArrayList localArrayList2;
      try
      {
        localArrayList1 = new ArrayList();
        Object localObject5 = new HashMap();
        if (paramInt != 0)
        {
          localObject1 = new StringBuilder(paramArrayList.size());
          int i = 0;
          if (i >= paramArrayList.size())
            continue;
          long l1 = ((Integer)paramArrayList.get(i)).intValue();
          long l2 = paramInt;
          if (((StringBuilder)localObject1).length() <= 0)
            continue;
          ((StringBuilder)localObject1).append(',');
          ((StringBuilder)localObject1).append(l1 | l2 << 32);
          i += 1;
          continue;
          localObject1 = ((StringBuilder)localObject1).toString();
          Object localObject6 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, data, read_state, out FROM messages WHERE mid IN(%s)", new Object[] { localObject1 }), new Object[0]);
          localArrayList2 = new ArrayList();
          paramInt = UserConfig.getClientUserId();
          try
          {
            if (!((SQLiteCursor)localObject6).next())
              continue;
            l1 = ((SQLiteCursor)localObject6).longValue(0);
            if (l1 == paramInt)
              continue;
            i = ((SQLiteCursor)localObject6).intValue(2);
            if (((i != 0) && (i != 2)) || (((SQLiteCursor)localObject6).intValue(3) != 0))
              continue;
            localObject4 = (Integer)((HashMap)localObject5).get(Long.valueOf(l1));
            Object localObject2 = localObject4;
            if (localObject4 != null)
              continue;
            localObject2 = Integer.valueOf(0);
            ((HashMap)localObject5).put(Long.valueOf(l1), Integer.valueOf(((Integer)localObject2).intValue() + 1));
            if ((int)l1 != 0)
              continue;
            localObject4 = ((SQLiteCursor)localObject6).byteBufferValue(1);
            if (localObject4 == null)
              continue;
            localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
            ((NativeByteBuffer)localObject4).reuse();
            if (localObject2 == null)
              continue;
            if (!(((TLRPC.Message)localObject2).media instanceof TLRPC.TL_messageMediaPhoto))
              break label539;
            localObject2 = ((TLRPC.Message)localObject2).media.photo.sizes.iterator();
            if (!((Iterator)localObject2).hasNext())
              continue;
            localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject2).next());
            if ((localObject4 == null) || (((File)localObject4).toString().length() <= 0))
              continue;
            localArrayList2.add(localObject4);
            continue;
          }
          catch (Exception localObject3)
          {
            FileLog.e(localException);
            ((SQLiteCursor)localObject6).dispose();
            FileLoader.getInstance().deleteFiles(localArrayList2, 0);
            localObject3 = ((HashMap)localObject5).entrySet().iterator();
            if (!((Iterator)localObject3).hasNext())
              break;
          }
          localObject4 = (Map.Entry)((Iterator)localObject3).next();
          localObject5 = (Long)((Map.Entry)localObject4).getKey();
          localArrayList1.add(localObject5);
          localObject6 = this.database.executeFast("UPDATE dialogs SET unread_count = max(0, ((SELECT unread_count FROM dialogs WHERE did = ?) - ?)) WHERE did = ?");
          ((SQLitePreparedStatement)localObject6).requery();
          ((SQLitePreparedStatement)localObject6).bindLong(1, ((Long)localObject5).longValue());
          ((SQLitePreparedStatement)localObject6).bindInteger(2, ((Integer)((Map.Entry)localObject4).getValue()).intValue());
          ((SQLitePreparedStatement)localObject6).bindLong(3, ((Long)localObject5).longValue());
          ((SQLitePreparedStatement)localObject6).step();
          ((SQLitePreparedStatement)localObject6).dispose();
          continue;
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
        return null;
      }
      localObject1 = TextUtils.join(",", paramArrayList);
      continue;
      label539: if (!(((TLRPC.Message)localObject3).media instanceof TLRPC.TL_messageMediaDocument))
        continue;
      Object localObject4 = FileLoader.getPathToAttach(((TLRPC.Message)localObject3).media.document);
      if ((localObject4 != null) && (((File)localObject4).toString().length() > 0))
        localArrayList2.add(localObject4);
      Object localObject3 = FileLoader.getPathToAttach(((TLRPC.Message)localObject3).media.document.thumb);
      if ((localObject3 == null) || (((File)localObject3).toString().length() <= 0))
        continue;
      localArrayList2.add(localObject3);
    }
    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
    this.database.executeFast(String.format(Locale.US, "DELETE FROM bot_keyboard WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_seq WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
    this.database.executeFast(String.format(Locale.US, "DELETE FROM media_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
    this.database.executeFast("DELETE FROM media_counts_v2 WHERE 1").stepThis().dispose();
    BotQuery.clearBotKeyboard(0L, paramArrayList);
    return (ArrayList<Long>)(ArrayList<Long>)(ArrayList<Long>)(ArrayList<Long>)(ArrayList<Long>)(ArrayList<Long>)localArrayList1;
  }

  private void markMessagesAsReadInternal(SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2, HashMap<Integer, Integer> paramHashMap)
  {
    int j = 0;
    if (paramSparseArray1 != null);
    for (int i = 0; ; i = j)
      do
      {
        try
        {
          long l;
          while (i < paramSparseArray1.size())
          {
            int k = paramSparseArray1.keyAt(i);
            l = ((Long)paramSparseArray1.get(k)).longValue();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(k), Long.valueOf(l) })).stepThis().dispose();
            i += 1;
          }
          while (i < paramSparseArray2.size())
          {
            j = paramSparseArray2.keyAt(i);
            l = ((Long)paramSparseArray2.get(j)).longValue();
            this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 1 WHERE uid = %d AND mid > 0 AND mid <= %d AND read_state IN(0,2) AND out = 1", new Object[] { Integer.valueOf(j), Long.valueOf(l) })).stepThis().dispose();
            i += 1;
          }
          if ((paramHashMap != null) && (!paramHashMap.isEmpty()))
          {
            paramSparseArray1 = paramHashMap.entrySet().iterator();
            while (paramSparseArray1.hasNext())
            {
              paramSparseArray2 = (Map.Entry)paramSparseArray1.next();
              l = ((Integer)paramSparseArray2.getKey()).intValue();
              i = ((Integer)paramSparseArray2.getValue()).intValue();
              paramSparseArray2 = this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 1");
              paramSparseArray2.requery();
              paramSparseArray2.bindLong(1, l << 32);
              paramSparseArray2.bindInteger(2, i);
              paramSparseArray2.step();
              paramSparseArray2.dispose();
            }
          }
        }
        catch (Exception paramSparseArray1)
        {
          FileLog.e(paramSparseArray1);
        }
        return;
      }
      while (paramSparseArray2 == null);
  }

  private void putChatsInternal(ArrayList<TLRPC.Chat> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO chats VALUES(?, ?, ?)");
    int i = 0;
    while (true)
      if (i < paramArrayList.size())
      {
        Object localObject3 = (TLRPC.Chat)paramArrayList.get(i);
        Object localObject1 = localObject3;
        SQLiteCursor localSQLiteCursor;
        if (((TLRPC.Chat)localObject3).min)
        {
          localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.Chat)localObject3).id) }), new Object[0]);
          localObject1 = localObject3;
          if (!localSQLiteCursor.next());
        }
        try
        {
          NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject3;
          TLRPC.Chat localChat;
          if (localNativeByteBuffer != null)
          {
            localChat = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            localObject1 = localObject3;
            if (localChat != null)
            {
              localChat.title = ((TLRPC.Chat)localObject3).title;
              localChat.photo = ((TLRPC.Chat)localObject3).photo;
              localChat.broadcast = ((TLRPC.Chat)localObject3).broadcast;
              localChat.verified = ((TLRPC.Chat)localObject3).verified;
              localChat.megagroup = ((TLRPC.Chat)localObject3).megagroup;
              localChat.democracy = ((TLRPC.Chat)localObject3).democracy;
              if (((TLRPC.Chat)localObject3).username == null)
                break label325;
              localChat.username = ((TLRPC.Chat)localObject3).username;
              localChat.flags |= 64;
            }
          }
          while (true)
          {
            localObject1 = localChat;
            localSQLiteCursor.dispose();
            localSQLitePreparedStatement.requery();
            localObject3 = new NativeByteBuffer(localObject1.getObjectSize());
            localObject1.serializeToStream((AbstractSerializedData)localObject3);
            localSQLitePreparedStatement.bindInteger(1, localObject1.id);
            if (localObject1.title == null)
              break label358;
            localSQLitePreparedStatement.bindString(2, localObject1.title.toLowerCase());
            localSQLitePreparedStatement.bindByteBuffer(3, (NativeByteBuffer)localObject3);
            localSQLitePreparedStatement.step();
            ((NativeByteBuffer)localObject3).reuse();
            i += 1;
            break;
            label325: localChat.username = null;
            localChat.flags &= -65;
          }
        }
        catch (Exception localObject2)
        {
          while (true)
          {
            FileLog.e(localException);
            Object localObject2 = localObject3;
            continue;
            label358: localSQLitePreparedStatement.bindString(2, "");
          }
        }
      }
    localSQLitePreparedStatement.dispose();
  }

  private void putDialogsInternal(TLRPC.messages_Dialogs parammessages_Dialogs, boolean paramBoolean)
  {
    HashMap localHashMap;
    int i;
    Object localObject1;
    SQLitePreparedStatement localSQLitePreparedStatement1;
    SQLitePreparedStatement localSQLitePreparedStatement2;
    SQLitePreparedStatement localSQLitePreparedStatement3;
    SQLitePreparedStatement localSQLitePreparedStatement4;
    SQLitePreparedStatement localSQLitePreparedStatement5;
    TLRPC.TL_dialog localTL_dialog;
    while (true)
    {
      try
      {
        this.database.beginTransaction();
        localHashMap = new HashMap();
        i = 0;
        if (i >= parammessages_Dialogs.messages.size())
          continue;
        localObject1 = (TLRPC.Message)parammessages_Dialogs.messages.get(i);
        localHashMap.put(Long.valueOf(((TLRPC.Message)localObject1).dialog_id), localObject1);
        i += 1;
        continue;
        if (parammessages_Dialogs.dialogs.isEmpty())
          break label920;
        localObject1 = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
        localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
        localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
        localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
        localSQLitePreparedStatement5 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        i = 0;
        if (i >= parammessages_Dialogs.dialogs.size())
          break label890;
        localTL_dialog = (TLRPC.TL_dialog)parammessages_Dialogs.dialogs.get(i);
        if (localTL_dialog.id != 0L)
          continue;
        if (localTL_dialog.peer.user_id == 0)
          continue;
        localTL_dialog.id = localTL_dialog.peer.user_id;
        if (!paramBoolean)
          break;
        localObject2 = this.database.queryFinalized("SELECT did FROM dialogs WHERE did = " + localTL_dialog.id, new Object[0]);
        boolean bool = ((SQLiteCursor)localObject2).next();
        ((SQLiteCursor)localObject2).dispose();
        if (!bool)
          break;
        break label944;
        if (localTL_dialog.peer.chat_id != 0)
        {
          localTL_dialog.id = (-localTL_dialog.peer.chat_id);
          continue;
        }
      }
      catch (Exception parammessages_Dialogs)
      {
        FileLog.e(parammessages_Dialogs);
        return;
      }
      localTL_dialog.id = (-localTL_dialog.peer.channel_id);
    }
    int j = 0;
    Object localObject2 = (TLRPC.Message)localHashMap.get(Long.valueOf(localTL_dialog.id));
    long l2;
    long l1;
    if (localObject2 != null)
    {
      int k = Math.max(((TLRPC.Message)localObject2).date, 0);
      if (isValidKeyboardToSave((TLRPC.Message)localObject2))
        BotQuery.putBotKeyboard(localTL_dialog.id, (TLRPC.Message)localObject2);
      fixUnsupportedMedia((TLRPC.Message)localObject2);
      NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(((TLRPC.Message)localObject2).getObjectSize());
      ((TLRPC.Message)localObject2).serializeToStream(localNativeByteBuffer);
      l2 = ((TLRPC.Message)localObject2).id;
      l1 = l2;
      if (((TLRPC.Message)localObject2).to_id.channel_id != 0)
        l1 = l2 | ((TLRPC.Message)localObject2).to_id.channel_id << 32;
      ((SQLitePreparedStatement)localObject1).requery();
      ((SQLitePreparedStatement)localObject1).bindLong(1, l1);
      ((SQLitePreparedStatement)localObject1).bindLong(2, localTL_dialog.id);
      ((SQLitePreparedStatement)localObject1).bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject2));
      ((SQLitePreparedStatement)localObject1).bindInteger(4, ((TLRPC.Message)localObject2).send_state);
      ((SQLitePreparedStatement)localObject1).bindInteger(5, ((TLRPC.Message)localObject2).date);
      ((SQLitePreparedStatement)localObject1).bindByteBuffer(6, localNativeByteBuffer);
      if (MessageObject.isOut((TLRPC.Message)localObject2))
      {
        j = 1;
        label522: ((SQLitePreparedStatement)localObject1).bindInteger(7, j);
        ((SQLitePreparedStatement)localObject1).bindInteger(8, 0);
        if ((((TLRPC.Message)localObject2).flags & 0x400) == 0)
          break label957;
        j = ((TLRPC.Message)localObject2).views;
        label558: ((SQLitePreparedStatement)localObject1).bindInteger(9, j);
        ((SQLitePreparedStatement)localObject1).bindInteger(10, 0);
        ((SQLitePreparedStatement)localObject1).step();
        if (SharedMediaQuery.canAddMessageToMedia((TLRPC.Message)localObject2))
        {
          localSQLitePreparedStatement2.requery();
          localSQLitePreparedStatement2.bindLong(1, l1);
          localSQLitePreparedStatement2.bindLong(2, localTL_dialog.id);
          localSQLitePreparedStatement2.bindInteger(3, ((TLRPC.Message)localObject2).date);
          localSQLitePreparedStatement2.bindInteger(4, SharedMediaQuery.getMediaType((TLRPC.Message)localObject2));
          localSQLitePreparedStatement2.bindByteBuffer(5, localNativeByteBuffer);
          localSQLitePreparedStatement2.step();
        }
        localNativeByteBuffer.reuse();
        createFirstHoles(localTL_dialog.id, localSQLitePreparedStatement4, localSQLitePreparedStatement5, ((TLRPC.Message)localObject2).id);
        j = k;
      }
    }
    else
    {
      l2 = localTL_dialog.top_message;
      l1 = l2;
      if (localTL_dialog.peer.channel_id != 0)
        l1 = l2 | localTL_dialog.peer.channel_id << 32;
      localSQLitePreparedStatement1.requery();
      localSQLitePreparedStatement1.bindLong(1, localTL_dialog.id);
      localSQLitePreparedStatement1.bindInteger(2, j);
      localSQLitePreparedStatement1.bindInteger(3, localTL_dialog.unread_count);
      localSQLitePreparedStatement1.bindLong(4, l1);
      localSQLitePreparedStatement1.bindInteger(5, localTL_dialog.read_inbox_max_id);
      localSQLitePreparedStatement1.bindInteger(6, localTL_dialog.read_outbox_max_id);
      localSQLitePreparedStatement1.bindLong(7, 0L);
      localSQLitePreparedStatement1.bindInteger(8, 0);
      localSQLitePreparedStatement1.bindInteger(9, localTL_dialog.pts);
      localSQLitePreparedStatement1.bindInteger(10, 0);
      localSQLitePreparedStatement1.bindInteger(11, localTL_dialog.pinnedNum);
      localSQLitePreparedStatement1.step();
      if (localTL_dialog.notify_settings != null)
      {
        localSQLitePreparedStatement3.requery();
        localSQLitePreparedStatement3.bindLong(1, localTL_dialog.id);
        if (localTL_dialog.notify_settings.mute_until == 0)
          break label963;
      }
    }
    label920: label944: label957: label963: for (j = 1; ; j = 0)
    {
      localSQLitePreparedStatement3.bindInteger(2, j);
      localSQLitePreparedStatement3.step();
      break label944;
      label890: ((SQLitePreparedStatement)localObject1).dispose();
      localSQLitePreparedStatement1.dispose();
      localSQLitePreparedStatement2.dispose();
      localSQLitePreparedStatement3.dispose();
      localSQLitePreparedStatement4.dispose();
      localSQLitePreparedStatement5.dispose();
      putUsersInternal(parammessages_Dialogs.users);
      putChatsInternal(parammessages_Dialogs.chats);
      this.database.commitTransaction();
      return;
      i += 1;
      break;
      j = 0;
      break label522;
      j = 0;
      break label558;
    }
  }

  private void putMessagesInternal(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, int paramInt, boolean paramBoolean3)
  {
    if (paramBoolean3)
      while (true)
      {
        try
        {
          localObject1 = (TLRPC.Message)paramArrayList.get(0);
          if (((TLRPC.Message)localObject1).dialog_id != 0L)
            continue;
          if (((TLRPC.Message)localObject1).to_id.user_id == 0)
            continue;
          ((TLRPC.Message)localObject1).dialog_id = ((TLRPC.Message)localObject1).to_id.user_id;
          localObject1 = this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + ((TLRPC.Message)localObject1).dialog_id, new Object[0]);
          if (!((SQLiteCursor)localObject1).next())
            break label3218;
          i = ((SQLiteCursor)localObject1).intValue(0);
          ((SQLiteCursor)localObject1).dispose();
          if (i == 0)
            break;
          return;
          if (((TLRPC.Message)localObject1).to_id.chat_id != 0)
          {
            ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.chat_id);
            continue;
          }
        }
        catch (Exception paramArrayList)
        {
          FileLog.e(paramArrayList);
          return;
        }
        ((TLRPC.Message)localObject1).dialog_id = (-((TLRPC.Message)localObject1).to_id.channel_id);
      }
    if (paramBoolean1)
      this.database.beginTransaction();
    HashMap localHashMap2 = new HashMap();
    HashMap localHashMap1 = new HashMap();
    Object localObject7 = new HashMap();
    StringBuilder localStringBuilder = new StringBuilder();
    HashMap localHashMap4 = new HashMap();
    HashMap localHashMap3 = new HashMap();
    SQLitePreparedStatement localSQLitePreparedStatement1 = this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
    SQLitePreparedStatement localSQLitePreparedStatement2 = this.database.executeFast("REPLACE INTO randoms VALUES(?, ?)");
    SQLitePreparedStatement localSQLitePreparedStatement3 = this.database.executeFast("REPLACE INTO download_queue VALUES(?, ?, ?, ?)");
    SQLitePreparedStatement localSQLitePreparedStatement4 = this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
    int i = 0;
    Object localObject1 = null;
    Object localObject3 = null;
    Object localObject2 = null;
    label292: long l2;
    long l1;
    Object localObject5;
    Object localObject6;
    if (i < paramArrayList.size())
    {
      TLRPC.Message localMessage = (TLRPC.Message)paramArrayList.get(i);
      l2 = localMessage.id;
      if (localMessage.dialog_id == 0L)
      {
        if (localMessage.to_id.user_id != 0)
          localMessage.dialog_id = localMessage.to_id.user_id;
      }
      else
      {
        l1 = l2;
        if (localMessage.to_id.channel_id != 0)
          l1 = l2 | localMessage.to_id.channel_id << 32;
        if ((MessageObject.isUnread(localMessage)) && (!MessageObject.isOut(localMessage)))
        {
          localObject5 = (Integer)localHashMap4.get(Long.valueOf(localMessage.dialog_id));
          localObject4 = localObject5;
          if (localObject5 == null)
          {
            localObject5 = this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + localMessage.dialog_id, new Object[0]);
            if (!((SQLiteCursor)localObject5).next())
              break label810;
          }
        }
      }
      label810: for (localObject4 = Integer.valueOf(((SQLiteCursor)localObject5).intValue(0)); ; localObject4 = Integer.valueOf(0))
      {
        ((SQLiteCursor)localObject5).dispose();
        localHashMap4.put(Long.valueOf(localMessage.dialog_id), localObject4);
        if ((localMessage.id < 0) || (((Integer)localObject4).intValue() < localMessage.id))
        {
          if (localStringBuilder.length() > 0)
            localStringBuilder.append(",");
          localStringBuilder.append(l1);
          localHashMap3.put(Long.valueOf(l1), Long.valueOf(localMessage.dialog_id));
        }
        localObject5 = localObject1;
        localObject6 = localObject3;
        localObject4 = localObject2;
        if (SharedMediaQuery.canAddMessageToMedia(localMessage))
        {
          localObject6 = localObject3;
          if (localObject3 == null)
          {
            localObject6 = new StringBuilder();
            localObject2 = new HashMap();
            localObject1 = new HashMap();
          }
          if (((StringBuilder)localObject6).length() > 0)
            ((StringBuilder)localObject6).append(",");
          ((StringBuilder)localObject6).append(l1);
          ((HashMap)localObject2).put(Long.valueOf(l1), Long.valueOf(localMessage.dialog_id));
          ((HashMap)localObject1).put(Long.valueOf(l1), Integer.valueOf(SharedMediaQuery.getMediaType(localMessage)));
          localObject4 = localObject2;
          localObject5 = localObject1;
        }
        if (!isValidKeyboardToSave(localMessage))
          break label3225;
        localObject1 = (TLRPC.Message)((HashMap)localObject7).get(Long.valueOf(localMessage.dialog_id));
        if ((localObject1 != null) && (((TLRPC.Message)localObject1).id >= localMessage.id))
          break label3225;
        ((HashMap)localObject7).put(Long.valueOf(localMessage.dialog_id), localMessage);
        break label3225;
        if (localMessage.to_id.chat_id != 0)
        {
          localMessage.dialog_id = (-localMessage.to_id.chat_id);
          break;
        }
        localMessage.dialog_id = (-localMessage.to_id.channel_id);
        break;
      }
    }
    Object localObject4 = ((HashMap)localObject7).entrySet().iterator();
    while (((Iterator)localObject4).hasNext())
    {
      localObject5 = (Map.Entry)((Iterator)localObject4).next();
      BotQuery.putBotKeyboard(((Long)((Map.Entry)localObject5).getKey()).longValue(), (TLRPC.Message)((Map.Entry)localObject5).getValue());
    }
    label1128: int k;
    label1292: int m;
    label1656: label2046: int j;
    label1780: int i3;
    label1840: label2149: int i1;
    label2529: int n;
    label2570: int i2;
    if (localObject3 != null)
    {
      localObject3 = this.database.queryFinalized("SELECT mid FROM media_v2 WHERE mid IN(" + ((StringBuilder)localObject3).toString() + ")", new Object[0]);
      while (((SQLiteCursor)localObject3).next())
        ((HashMap)localObject2).remove(Long.valueOf(((SQLiteCursor)localObject3).longValue(0)));
      ((SQLiteCursor)localObject3).dispose();
      localObject5 = new HashMap();
      localObject6 = ((HashMap)localObject2).entrySet().iterator();
      if (!((Iterator)localObject6).hasNext())
        break label3246;
      localObject7 = (Map.Entry)((Iterator)localObject6).next();
      localObject2 = (Integer)((HashMap)localObject1).get(((Map.Entry)localObject7).getKey());
      localObject3 = (HashMap)((HashMap)localObject5).get(localObject2);
      if (localObject3 == null)
      {
        localObject3 = new HashMap();
        ((HashMap)localObject5).put(localObject2, localObject3);
      }
      for (localObject2 = Integer.valueOf(0); ; localObject2 = (Integer)((HashMap)localObject3).get(((Map.Entry)localObject7).getValue()))
      {
        localObject4 = localObject2;
        if (localObject2 == null)
          localObject4 = Integer.valueOf(0);
        i = ((Integer)localObject4).intValue();
        ((HashMap)localObject3).put(((Map.Entry)localObject7).getValue(), Integer.valueOf(i + 1));
        break;
      }
      if (localStringBuilder.length() <= 0)
        break label3253;
      localObject1 = this.database.queryFinalized("SELECT mid FROM messages WHERE mid IN(" + localStringBuilder.toString() + ")", new Object[0]);
      while (((SQLiteCursor)localObject1).next())
        localHashMap3.remove(Long.valueOf(((SQLiteCursor)localObject1).longValue(0)));
      ((SQLiteCursor)localObject1).dispose();
      localObject4 = localHashMap3.values().iterator();
      while (true)
        if (((Iterator)localObject4).hasNext())
        {
          localObject5 = (Long)((Iterator)localObject4).next();
          localObject2 = (Integer)localHashMap1.get(localObject5);
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = Integer.valueOf(0);
          localHashMap1.put(localObject5, Integer.valueOf(((Integer)localObject1).intValue() + 1));
          continue;
          if (k < paramArrayList.size())
          {
            localObject4 = (TLRPC.Message)paramArrayList.get(k);
            fixUnsupportedMedia((TLRPC.Message)localObject4);
            localSQLitePreparedStatement1.requery();
            l1 = ((TLRPC.Message)localObject4).id;
            if (((TLRPC.Message)localObject4).local_id != 0)
              l1 = ((TLRPC.Message)localObject4).local_id;
            l2 = l1;
            if (((TLRPC.Message)localObject4).to_id.channel_id != 0)
              l2 = l1 | ((TLRPC.Message)localObject4).to_id.channel_id << 32;
            localObject5 = new NativeByteBuffer(((TLRPC.Message)localObject4).getObjectSize());
            ((TLRPC.Message)localObject4).serializeToStream((AbstractSerializedData)localObject5);
            m = 1;
            i = m;
            if (((TLRPC.Message)localObject4).action != null)
            {
              i = m;
              if ((((TLRPC.Message)localObject4).action instanceof TLRPC.TL_messageEncryptedAction))
              {
                i = m;
                if (!(((TLRPC.Message)localObject4).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
                {
                  i = m;
                  if (!(((TLRPC.Message)localObject4).action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
                    i = 0;
                }
              }
            }
            if (i != 0)
            {
              localObject2 = (TLRPC.Message)localHashMap2.get(Long.valueOf(((TLRPC.Message)localObject4).dialog_id));
              if ((localObject2 == null) || (((TLRPC.Message)localObject4).date > ((TLRPC.Message)localObject2).date) || ((((TLRPC.Message)localObject4).id > 0) && (((TLRPC.Message)localObject2).id > 0) && (((TLRPC.Message)localObject4).id > ((TLRPC.Message)localObject2).id)) || ((((TLRPC.Message)localObject4).id < 0) && (((TLRPC.Message)localObject2).id < 0) && (((TLRPC.Message)localObject4).id < ((TLRPC.Message)localObject2).id)))
                localHashMap2.put(Long.valueOf(((TLRPC.Message)localObject4).dialog_id), localObject4);
            }
            localSQLitePreparedStatement1.bindLong(1, l2);
            localSQLitePreparedStatement1.bindLong(2, ((TLRPC.Message)localObject4).dialog_id);
            localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags((TLRPC.Message)localObject4));
            localSQLitePreparedStatement1.bindInteger(4, ((TLRPC.Message)localObject4).send_state);
            localSQLitePreparedStatement1.bindInteger(5, ((TLRPC.Message)localObject4).date);
            localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject5);
            if (!MessageObject.isOut((TLRPC.Message)localObject4))
              break label3282;
            i = 1;
            localSQLitePreparedStatement1.bindInteger(7, i);
            localSQLitePreparedStatement1.bindInteger(8, ((TLRPC.Message)localObject4).ttl);
            if ((((TLRPC.Message)localObject4).flags & 0x400) != 0)
            {
              localSQLitePreparedStatement1.bindInteger(9, ((TLRPC.Message)localObject4).views);
              localSQLitePreparedStatement1.bindInteger(10, 0);
              localSQLitePreparedStatement1.step();
              if (((TLRPC.Message)localObject4).random_id != 0L)
              {
                localSQLitePreparedStatement2.requery();
                localSQLitePreparedStatement2.bindLong(1, ((TLRPC.Message)localObject4).random_id);
                localSQLitePreparedStatement2.bindLong(2, l2);
                localSQLitePreparedStatement2.step();
              }
              if (!SharedMediaQuery.canAddMessageToMedia((TLRPC.Message)localObject4))
                break label3189;
              if (localObject1 != null)
                break;
              localObject2 = this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
              ((SQLitePreparedStatement)localObject2).requery();
              ((SQLitePreparedStatement)localObject2).bindLong(1, l2);
              ((SQLitePreparedStatement)localObject2).bindLong(2, ((TLRPC.Message)localObject4).dialog_id);
              ((SQLitePreparedStatement)localObject2).bindInteger(3, ((TLRPC.Message)localObject4).date);
              ((SQLitePreparedStatement)localObject2).bindInteger(4, SharedMediaQuery.getMediaType((TLRPC.Message)localObject4));
              ((SQLitePreparedStatement)localObject2).bindByteBuffer(5, (NativeByteBuffer)localObject5);
              ((SQLitePreparedStatement)localObject2).step();
              if ((((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaWebPage))
              {
                localSQLitePreparedStatement4.requery();
                localSQLitePreparedStatement4.bindLong(1, ((TLRPC.Message)localObject4).media.webpage.id);
                localSQLitePreparedStatement4.bindLong(2, l2);
                localSQLitePreparedStatement4.step();
              }
              ((NativeByteBuffer)localObject5).reuse();
              if (((((TLRPC.Message)localObject4).to_id.channel_id != 0) && (!((TLRPC.Message)localObject4).post)) || (((TLRPC.Message)localObject4).date < ConnectionsManager.getInstance().getCurrentTime() - 3600) || (paramInt == 0) || ((!(((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaPhoto)) && (!(((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaDocument))))
                break label3196;
              m = 0;
              i = 0;
              l1 = 0L;
              localObject1 = null;
              if (!MessageObject.isVoiceMessage((TLRPC.Message)localObject4))
                break label2149;
              if (((paramInt & 0x2) == 0) || (((TLRPC.Message)localObject4).media.document.size >= 5242880))
                break label3203;
              l1 = ((TLRPC.Message)localObject4).media.document.id;
              i = 2;
              localObject1 = new TLRPC.TL_messageMediaDocument();
              ((TLRPC.MessageMedia)localObject1).caption = "";
              ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject4).media.document;
            }
            while (true)
            {
              if (localObject1 == null)
                break label3196;
              localSQLitePreparedStatement3.requery();
              localObject5 = new NativeByteBuffer(((TLRPC.MessageMedia)localObject1).getObjectSize());
              ((TLRPC.MessageMedia)localObject1).serializeToStream((AbstractSerializedData)localObject5);
              localSQLitePreparedStatement3.bindLong(1, l1);
              localSQLitePreparedStatement3.bindInteger(2, i);
              localSQLitePreparedStatement3.bindInteger(3, ((TLRPC.Message)localObject4).date);
              localSQLitePreparedStatement3.bindByteBuffer(4, (NativeByteBuffer)localObject5);
              localSQLitePreparedStatement3.step();
              ((NativeByteBuffer)localObject5).reuse();
              i = j | i;
              break label3265;
              localSQLitePreparedStatement1.bindInteger(9, getMessageMediaType((TLRPC.Message)localObject4));
              break;
              if ((((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaPhoto))
              {
                if ((paramInt & 0x1) == 0)
                  break label3203;
                i = m;
                if (FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Message)localObject4).media.photo.sizes, AndroidUtilities.getPhotoSize()) == null)
                  break label3288;
                l1 = ((TLRPC.Message)localObject4).media.photo.id;
                i = 1;
                localObject1 = new TLRPC.TL_messageMediaPhoto();
                ((TLRPC.MessageMedia)localObject1).caption = "";
                ((TLRPC.MessageMedia)localObject1).photo = ((TLRPC.Message)localObject4).media.photo;
                break label3288;
              }
              if (MessageObject.isVideoMessage((TLRPC.Message)localObject4))
              {
                if ((paramInt & 0x4) == 0)
                  break label3203;
                l1 = ((TLRPC.Message)localObject4).media.document.id;
                i = 4;
                localObject1 = new TLRPC.TL_messageMediaDocument();
                ((TLRPC.MessageMedia)localObject1).caption = "";
                ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject4).media.document;
                continue;
              }
              if ((!(((TLRPC.Message)localObject4).media instanceof TLRPC.TL_messageMediaDocument)) || (MessageObject.isMusicMessage((TLRPC.Message)localObject4)) || (MessageObject.isGifDocument(((TLRPC.Message)localObject4).media.document)) || ((paramInt & 0x8) == 0))
                break label3203;
              l1 = ((TLRPC.Message)localObject4).media.document.id;
              i = 8;
              localObject1 = new TLRPC.TL_messageMediaDocument();
              ((TLRPC.MessageMedia)localObject1).caption = "";
              ((TLRPC.MessageMedia)localObject1).document = ((TLRPC.Message)localObject4).media.document;
            }
          }
          else
          {
            localSQLitePreparedStatement1.dispose();
            if (localObject1 != null)
              ((SQLitePreparedStatement)localObject1).dispose();
            localSQLitePreparedStatement2.dispose();
            localSQLitePreparedStatement3.dispose();
            localSQLitePreparedStatement4.dispose();
            localObject1 = this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            paramArrayList = new HashMap();
            paramArrayList.putAll(localHashMap2);
            localObject2 = paramArrayList.entrySet().iterator();
            while (true)
              if (((Iterator)localObject2).hasNext())
              {
                localObject4 = (Long)((Map.Entry)((Iterator)localObject2).next()).getKey();
                if (((Long)localObject4).longValue() == 0L)
                  continue;
                localObject5 = (TLRPC.Message)localHashMap2.get(localObject4);
                if (localObject5 == null)
                  break label3176;
                i = ((TLRPC.Message)localObject5).to_id.channel_id;
                paramArrayList = this.database.queryFinalized("SELECT date, unread_count, pts, last_mid, inbox_max, outbox_max, pinned FROM dialogs WHERE did = " + localObject4, new Object[0]);
                if (i == 0)
                  break label3314;
                paramInt = 1;
                if (!paramArrayList.next())
                  break;
                i3 = paramArrayList.intValue(0);
                paramInt = paramArrayList.intValue(1);
                i1 = paramArrayList.intValue(2);
                n = paramArrayList.intValue(3);
                int i4 = paramArrayList.intValue(4);
                k = paramArrayList.intValue(5);
                m = paramArrayList.intValue(6);
                i2 = paramInt;
                paramInt = i4;
                label2635: paramArrayList.dispose();
                paramArrayList = (Integer)localHashMap1.get(localObject4);
                if (paramArrayList == null)
                {
                  paramArrayList = Integer.valueOf(0);
                  label2659: if (localObject5 == null)
                    break label3345;
                  l2 = ((TLRPC.Message)localObject5).id;
                }
              }
          }
        }
    }
    while (true)
    {
      l1 = l2;
      if (localObject5 != null)
      {
        l1 = l2;
        if (((TLRPC.Message)localObject5).local_id != 0)
          l1 = ((TLRPC.Message)localObject5).local_id;
      }
      while (true)
      {
        ((SQLitePreparedStatement)localObject1).requery();
        ((SQLitePreparedStatement)localObject1).bindLong(1, ((Long)localObject4).longValue());
        if ((localObject5 != null) && ((!paramBoolean2) || (i3 == 0)))
          ((SQLitePreparedStatement)localObject1).bindInteger(2, ((TLRPC.Message)localObject5).date);
        while (true)
        {
          ((SQLitePreparedStatement)localObject1).bindInteger(3, paramArrayList.intValue() + i2);
          ((SQLitePreparedStatement)localObject1).bindLong(4, l2);
          ((SQLitePreparedStatement)localObject1).bindInteger(5, paramInt);
          ((SQLitePreparedStatement)localObject1).bindInteger(6, k);
          ((SQLitePreparedStatement)localObject1).bindLong(7, 0L);
          ((SQLitePreparedStatement)localObject1).bindInteger(8, 0);
          ((SQLitePreparedStatement)localObject1).bindInteger(9, i1);
          ((SQLitePreparedStatement)localObject1).bindInteger(10, 0);
          ((SQLitePreparedStatement)localObject1).bindInteger(11, m);
          ((SQLitePreparedStatement)localObject1).step();
          break;
          if (i == 0)
            break label3320;
          MessagesController.getInstance().checkChannelInviter(i);
          break label3320;
          localHashMap1.put(localObject4, Integer.valueOf(paramArrayList.intValue() + i2));
          break label2659;
          ((SQLitePreparedStatement)localObject1).bindInteger(2, i3);
        }
        ((SQLitePreparedStatement)localObject1).dispose();
        if (localObject3 != null)
        {
          paramArrayList = this.database.executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
          localObject1 = ((HashMap)localObject3).entrySet().iterator();
          while (((Iterator)localObject1).hasNext())
          {
            localObject3 = (Map.Entry)((Iterator)localObject1).next();
            localObject2 = (Integer)((Map.Entry)localObject3).getKey();
            localObject3 = ((HashMap)((Map.Entry)localObject3).getValue()).entrySet().iterator();
            while (((Iterator)localObject3).hasNext())
            {
              localObject4 = (Map.Entry)((Iterator)localObject3).next();
              l1 = ((Long)((Map.Entry)localObject4).getKey()).longValue();
              paramInt = (int)l1;
              paramInt = -1;
              localObject5 = this.database.queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(l1), localObject2 }), new Object[0]);
              if (((SQLiteCursor)localObject5).next())
                paramInt = ((SQLiteCursor)localObject5).intValue(0);
              ((SQLiteCursor)localObject5).dispose();
              if (paramInt == -1)
                continue;
              paramArrayList.requery();
              i = ((Integer)((Map.Entry)localObject4).getValue()).intValue();
              paramArrayList.bindLong(1, l1);
              paramArrayList.bindInteger(2, ((Integer)localObject2).intValue());
              paramArrayList.bindInteger(3, i + paramInt);
              paramArrayList.step();
            }
          }
          paramArrayList.dispose();
        }
        if (paramBoolean1)
          this.database.commitTransaction();
        MessagesController.getInstance().processDialogsUpdateRead(localHashMap1);
        if (j != 0)
        {
          AndroidUtilities.runOnUIThread(new Runnable(j)
          {
            public void run()
            {
              MediaController.getInstance().newDownloadObjectsAvailable(this.val$downloadMediaMaskFinal);
            }
          });
          return;
          label3176: i = 0;
          break label2529;
          localObject2 = localObject1;
          break label1780;
          label3189: localObject2 = localObject1;
          break label1840;
          label3196: i = j;
          break label3265;
          label3203: l1 = 0L;
          localObject1 = null;
          break label2046;
          localObject3 = null;
          break label1128;
          label3218: i = -1;
          break;
        }
        else
        {
          return;
          label3225: i += 1;
          localObject3 = localObject6;
          localObject2 = localObject4;
          localObject1 = localObject5;
          break label292;
          label3246: localObject3 = localObject5;
          break label1128;
          label3253: j = 0;
          k = 0;
          localObject1 = null;
          break label1292;
        }
        label3265: k += 1;
        localObject1 = localObject2;
        j = i;
        break label1292;
        label3282: i = 0;
        break label1656;
        label3288: break label2046;
        l2 = l1;
        if (i == 0)
          continue;
        l2 = l1 | i << 32;
      }
      label3314: paramInt = 0;
      break label2570;
      label3320: i2 = 0;
      i3 = 0;
      i1 = paramInt;
      k = 0;
      m = 0;
      n = 0;
      paramInt = 0;
      break label2635;
      label3345: l2 = n;
    }
  }

  private void putUsersAndChatsInternal(ArrayList<TLRPC.User> paramArrayList, ArrayList<TLRPC.Chat> paramArrayList1, boolean paramBoolean)
  {
    if (paramBoolean);
    try
    {
      this.database.beginTransaction();
      putUsersInternal(paramArrayList);
      putChatsInternal(paramArrayList1);
      if (paramBoolean)
        this.database.commitTransaction();
      return;
    }
    catch (Exception paramArrayList)
    {
      FileLog.e(paramArrayList);
    }
  }

  private void putUsersInternal(ArrayList<TLRPC.User> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    SQLitePreparedStatement localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
    int i = 0;
    while (true)
      if (i < paramArrayList.size())
      {
        Object localObject3 = (TLRPC.User)paramArrayList.get(i);
        Object localObject1 = localObject3;
        SQLiteCursor localSQLiteCursor;
        if (((TLRPC.User)localObject3).min)
        {
          localSQLiteCursor = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", new Object[] { Integer.valueOf(((TLRPC.User)localObject3).id) }), new Object[0]);
          localObject1 = localObject3;
          if (!localSQLiteCursor.next());
        }
        try
        {
          NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
          localObject1 = localObject3;
          if (localNativeByteBuffer != null)
          {
            localUser = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            localObject1 = localObject3;
            if (localUser != null)
            {
              if (((TLRPC.User)localObject3).username == null)
                break label326;
              localUser.username = ((TLRPC.User)localObject3).username;
              localUser.flags |= 8;
            }
          }
          while (true)
          {
            if (((TLRPC.User)localObject3).photo == null)
              break label359;
            localUser.photo = ((TLRPC.User)localObject3).photo;
            localUser.flags |= 32;
            localObject1 = localUser;
            localSQLiteCursor.dispose();
            localSQLitePreparedStatement.requery();
            localObject3 = new NativeByteBuffer(localObject1.getObjectSize());
            localObject1.serializeToStream((AbstractSerializedData)localObject3);
            localSQLitePreparedStatement.bindInteger(1, localObject1.id);
            localSQLitePreparedStatement.bindString(2, formatUserSearchName(localObject1));
            if (localObject1.status == null)
              break label425;
            if (!(localObject1.status instanceof TLRPC.TL_userStatusRecently))
              break label381;
            localObject1.status.expires = -100;
            localSQLitePreparedStatement.bindInteger(3, localObject1.status.expires);
            localSQLitePreparedStatement.bindByteBuffer(4, (NativeByteBuffer)localObject3);
            localSQLitePreparedStatement.step();
            ((NativeByteBuffer)localObject3).reuse();
            i += 1;
            break;
            label326: localUser.username = null;
            localUser.flags &= -9;
          }
        }
        catch (Exception localObject2)
        {
          while (true)
          {
            TLRPC.User localUser;
            FileLog.e(localException);
            Object localObject2 = localObject3;
            continue;
            label359: localUser.photo = null;
            localUser.flags &= -33;
            continue;
            label381: if ((localObject2.status instanceof TLRPC.TL_userStatusLastWeek))
            {
              localObject2.status.expires = -101;
              continue;
            }
            if (!(localObject2.status instanceof TLRPC.TL_userStatusLastMonth))
              continue;
            localObject2.status.expires = -102;
            continue;
            label425: localSQLitePreparedStatement.bindInteger(3, 0);
          }
        }
      }
    localSQLitePreparedStatement.dispose();
  }

  private void updateDbToLastVersion(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        SQLitePreparedStatement localSQLitePreparedStatement;
        int k;
        try
        {
          j = this.val$currentVersion;
          i = j;
          if (j < 4)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS read_state_out_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS ttl_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_messages;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid < 0 AND send_state = 1").stepThis().dispose();
            MessagesStorage.this.fixNotificationSettings();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 4").stepThis().dispose();
            i = 4;
          }
          j = i;
          Object localObject1;
          if (i == 4)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
            MessagesStorage.this.database.beginTransaction();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT date, data FROM enc_tasks WHERE 1", new Object[0]);
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            if (localSQLiteCursor.next())
            {
              j = localSQLiteCursor.intValue(0);
              localObject1 = localSQLiteCursor.byteBufferValue(1);
              if (localObject1 != null)
              {
                k = ((NativeByteBuffer)localObject1).limit();
                i = 0;
                while (i < k / 4)
                {
                  localSQLitePreparedStatement.requery();
                  localSQLitePreparedStatement.bindInteger(1, ((NativeByteBuffer)localObject1).readInt32(false));
                  localSQLitePreparedStatement.bindInteger(2, j);
                  localSQLitePreparedStatement.step();
                  i += 1;
                }
                ((NativeByteBuffer)localObject1).reuse();
              }
            }
            localSQLitePreparedStatement.dispose();
            localSQLiteCursor.dispose();
            MessagesStorage.this.database.commitTransaction();
            MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS date_idx_enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS enc_tasks;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN media INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 6").stepThis().dispose();
            j = 6;
          }
          k = j;
          if (j != 6)
            break label2956;
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN layer INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_in INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN seq_out INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 7").stepThis().dispose();
          k = 7;
          break label2956;
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN use_count INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN exchange_id INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN key_date INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fprint INTEGER default 0").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN fauthkey BLOB default NULL").stepThis().dispose();
          MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN khash BLOB default NULL").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 10").stepThis().dispose();
          i = 10;
          label897: j = i;
          if (i != 10)
            break label2979;
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, PRIMARY KEY (id, type));").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 11").stepThis().dispose();
          j = 11;
          break label2979;
          label947: MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_media;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS mid_idx_media;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_idx_media;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS media_counts;").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 13").stepThis().dispose();
          i = 13;
          label1130: j = i;
          if (i == 13)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN replydata BLOB default NULL").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 14").stepThis().dispose();
            j = 14;
          }
          i = j;
          if (j == 14)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 15").stepThis().dispose();
            i = 15;
          }
          j = i;
          if (i == 15)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 16").stepThis().dispose();
            j = 16;
          }
          i = j;
          if (j == 16)
          {
            MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN inbox_max INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN outbox_max INTEGER default 0").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 17").stepThis().dispose();
            i = 17;
          }
          j = i;
          if (i == 17)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 18").stepThis().dispose();
            j = 18;
          }
          i = j;
          if (j == 18)
          {
            MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS stickers;").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 19").stepThis().dispose();
            i = 19;
          }
          j = i;
          if (i == 19)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
            MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 20").stepThis().dispose();
            j = 20;
          }
          i = j;
          if (j == 20)
          {
            MessagesStorage.this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 21").stepThis().dispose();
            i = 21;
          }
          j = i;
          if (i != 21)
            break label2013;
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT uid, participants FROM chat_settings WHERE uid < 0", new Object[0]);
          localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?)");
          while (localSQLiteCursor.next())
          {
            i = localSQLiteCursor.intValue(0);
            Object localObject2 = localSQLiteCursor.byteBufferValue(1);
            if (localObject2 == null)
              continue;
            localObject1 = TLRPC.ChatParticipants.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
            ((NativeByteBuffer)localObject2).reuse();
            if (localObject1 == null)
              continue;
            localObject2 = new TLRPC.TL_chatFull();
            ((TLRPC.TL_chatFull)localObject2).id = i;
            ((TLRPC.TL_chatFull)localObject2).chat_photo = new TLRPC.TL_photoEmpty();
            ((TLRPC.TL_chatFull)localObject2).notify_settings = new TLRPC.TL_peerNotifySettingsEmpty();
            ((TLRPC.TL_chatFull)localObject2).exported_invite = new TLRPC.TL_chatInviteEmpty();
            ((TLRPC.TL_chatFull)localObject2).participants = ((TLRPC.ChatParticipants)localObject1);
            localObject1 = new NativeByteBuffer(((TLRPC.TL_chatFull)localObject2).getObjectSize());
            ((TLRPC.TL_chatFull)localObject2).serializeToStream((AbstractSerializedData)localObject1);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, i);
            localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject1);
            localSQLitePreparedStatement.step();
            ((NativeByteBuffer)localObject1).reuse();
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        label1790: return;
        localSQLitePreparedStatement.dispose();
        localException.dispose();
        MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS chat_settings;").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN last_mid_i INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN unread_count_i INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pts INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN date_i INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
        MessagesStorage.this.database.executeFast("ALTER TABLE messages ADD COLUMN imp INTEGER default 0").stepThis().dispose();
        MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
        MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
        MessagesStorage.this.database.executeFast("PRAGMA user_version = 22").stepThis().dispose();
        int j = 22;
        label2013: int i = j;
        if (j == 22)
        {
          MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
          MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 23").stepThis().dispose();
          i = 23;
          break label2996;
          label2084: MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid != 0 AND type >= 0 AND start IN (0, 1)").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 25").stepThis().dispose();
          j = 25;
          break label3013;
          label2128: MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 27").stepThis().dispose();
          i = 27;
          label2169: j = i;
          if (i != 27)
            break label3030;
          MessagesStorage.this.database.executeFast("ALTER TABLE web_recent_v3 ADD COLUMN document BLOB default NULL").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 28").stepThis().dispose();
          j = 28;
          break label3030;
        }
        while (true)
        {
          label2221: MessagesStorage.this.database.executeFast("DELETE FROM sent_files_v2 WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.executeFast("PRAGMA user_version = 30").stepThis().dispose();
          i = 30;
          label2956: label2979: label2996: label3013: label3030: 
          do
          {
            j = i;
            if (i == 30)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE chat_settings_v2 ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 31").stepThis().dispose();
              j = 31;
            }
            i = j;
            if (j == 31)
            {
              MessagesStorage.this.database.executeFast("DROP TABLE IF EXISTS bot_recent;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 32").stepThis().dispose();
              i = 32;
            }
            j = i;
            if (i == 32)
            {
              MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_mid_idx_imp_messages;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("DROP INDEX IF EXISTS uid_date_mid_imp_idx_messages;").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 33").stepThis().dispose();
              j = 33;
            }
            i = j;
            if (j == 33)
            {
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 34").stepThis().dispose();
              i = 34;
            }
            j = i;
            if (i == 34)
            {
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 35").stepThis().dispose();
              j = 35;
            }
            i = j;
            if (j == 35)
            {
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 36").stepThis().dispose();
              i = 36;
            }
            j = i;
            if (i == 36)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN in_seq_no INTEGER default 0").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 37").stepThis().dispose();
              j = 37;
            }
            i = j;
            if (j == 37)
            {
              MessagesStorage.this.database.executeFast("CREATE TABLE IF NOT EXISTS botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
              MessagesStorage.this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 38").stepThis().dispose();
              i = 38;
            }
            j = i;
            if (i == 38)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE dialogs ADD COLUMN pinned INTEGER default 0").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 39").stepThis().dispose();
              j = 39;
            }
            i = j;
            if (j == 39)
            {
              MessagesStorage.this.database.executeFast("ALTER TABLE enc_chats ADD COLUMN admin_id INTEGER default 0").stepThis().dispose();
              MessagesStorage.this.database.executeFast("PRAGMA user_version = 40").stepThis().dispose();
              i = 40;
            }
            if (i != 40)
              break label1790;
            MessagesStorage.this.fixNotificationSettings();
            MessagesStorage.this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
            return;
            if ((k == 7) || (k == 8))
              break;
            i = k;
            if (k != 9)
              break label897;
            break;
            if (j == 11)
              break label947;
            i = j;
            if (j != 12)
              break label1130;
            break label947;
            if (i == 23)
              break label2084;
            j = i;
            if (i == 24)
              break label2084;
            if (j == 25)
              break label2128;
            i = j;
            if (j != 26)
              break label2169;
            break label2128;
            if (j == 28)
              break label2221;
            i = j;
          }
          while (j != 29);
        }
      }
    });
  }

  private void updateDialogsWithDeletedMessagesInternal(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, int paramInt)
  {
    if (Thread.currentThread().getId() != this.storageQueue.getId())
      throw new RuntimeException("wrong db thread");
    int i;
    while (true)
    {
      try
      {
        localArrayList1 = new ArrayList();
        if (paramArrayList.isEmpty())
          break;
        if (paramInt == 0)
          continue;
        localArrayList1.add(Long.valueOf(-paramInt));
        paramArrayList = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ?)) WHERE did = ?");
        this.database.beginTransaction();
        i = 0;
        if (i >= localArrayList1.size())
          break label230;
        long l = ((Long)localArrayList1.get(i)).longValue();
        paramArrayList.requery();
        paramArrayList.bindLong(1, l);
        paramArrayList.bindLong(2, l);
        paramArrayList.bindLong(3, l);
        paramArrayList.step();
        i += 1;
        continue;
        paramArrayList = TextUtils.join(",", paramArrayList);
        paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE last_mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
        if (!paramArrayList.next())
          break label212;
        localArrayList1.add(Long.valueOf(paramArrayList.longValue(0)));
        continue;
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
      }
      return;
      label212: paramArrayList.dispose();
      paramArrayList = this.database.executeFast("UPDATE dialogs SET last_mid = (SELECT mid FROM messages WHERE uid = ? AND date = (SELECT MAX(date) FROM messages WHERE uid = ? AND date != 0)) WHERE did = ?");
      continue;
      label230: paramArrayList.dispose();
      this.database.commitTransaction();
      break label896;
      label244: if (i >= paramArrayList1.size())
        break label297;
      paramArrayList = (Long)paramArrayList1.get(i);
      if (localArrayList1.contains(paramArrayList))
        break label906;
      localArrayList1.add(paramArrayList);
      break label906;
    }
    localArrayList1.add(Long.valueOf(-paramInt));
    break label896;
    label297: Object localObject = TextUtils.join(",", localArrayList1);
    paramArrayList = new TLRPC.messages_Dialogs();
    paramArrayList1 = new ArrayList();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    localObject = this.database.queryFinalized(String.format(Locale.US, "SELECT d.did, d.last_mid, d.unread_count, d.date, m.data, m.read_state, m.mid, m.send_state, m.date, d.pts, d.inbox_max, d.outbox_max, d.pinned FROM dialogs as d LEFT JOIN messages as m ON d.last_mid = m.mid WHERE d.did IN(%s)", new Object[] { localObject }), new Object[0]);
    label381: TLRPC.TL_dialog localTL_dialog;
    if (((SQLiteCursor)localObject).next())
    {
      localTL_dialog = new TLRPC.TL_dialog();
      localTL_dialog.id = ((SQLiteCursor)localObject).longValue(0);
      localTL_dialog.top_message = ((SQLiteCursor)localObject).intValue(1);
      localTL_dialog.read_inbox_max_id = ((SQLiteCursor)localObject).intValue(10);
      localTL_dialog.read_outbox_max_id = ((SQLiteCursor)localObject).intValue(11);
      localTL_dialog.unread_count = ((SQLiteCursor)localObject).intValue(2);
      localTL_dialog.last_message_date = ((SQLiteCursor)localObject).intValue(3);
      localTL_dialog.pts = ((SQLiteCursor)localObject).intValue(9);
      if (paramInt != 0)
        break label915;
      i = 0;
      label485: localTL_dialog.flags = i;
      localTL_dialog.pinnedNum = ((SQLiteCursor)localObject).intValue(12);
      if (localTL_dialog.pinnedNum == 0)
        break label921;
    }
    label896: label906: label915: label921: for (boolean bool = true; ; bool = false)
    {
      localTL_dialog.pinned = bool;
      paramArrayList.dialogs.add(localTL_dialog);
      NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject).byteBufferValue(4);
      if (localNativeByteBuffer != null)
      {
        TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        MessageObject.setUnreadFlags(localMessage, ((SQLiteCursor)localObject).intValue(5));
        localMessage.id = ((SQLiteCursor)localObject).intValue(6);
        localMessage.send_state = ((SQLiteCursor)localObject).intValue(7);
        i = ((SQLiteCursor)localObject).intValue(8);
        if (i != 0)
          localTL_dialog.last_message_date = i;
        localMessage.dialog_id = localTL_dialog.id;
        paramArrayList.messages.add(localMessage);
        addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
      }
      i = (int)localTL_dialog.id;
      int j = (int)(localTL_dialog.id >> 32);
      if (i != 0)
      {
        if (j == 1)
        {
          if (localArrayList2.contains(Integer.valueOf(i)))
            break label381;
          localArrayList2.add(Integer.valueOf(i));
          break label381;
        }
        if (i > 0)
        {
          if (localArrayList1.contains(Integer.valueOf(i)))
            break label381;
          localArrayList1.add(Integer.valueOf(i));
          break label381;
        }
        if (localArrayList2.contains(Integer.valueOf(-i)))
          break label381;
        localArrayList2.add(Integer.valueOf(-i));
        break label381;
      }
      if (localArrayList3.contains(Integer.valueOf(j)))
        break label381;
      localArrayList3.add(Integer.valueOf(j));
      break label381;
      ((SQLiteCursor)localObject).dispose();
      if (!localArrayList3.isEmpty())
        getEncryptedChatsInternal(TextUtils.join(",", localArrayList3), paramArrayList1, localArrayList1);
      if (!localArrayList2.isEmpty())
        getChatsInternal(TextUtils.join(",", localArrayList2), paramArrayList.chats);
      if (!localArrayList1.isEmpty())
        getUsersInternal(TextUtils.join(",", localArrayList1), paramArrayList.users);
      if ((paramArrayList.dialogs.isEmpty()) && (paramArrayList1.isEmpty()))
        break;
      MessagesController.getInstance().processDialogsUpdate(paramArrayList, paramArrayList1);
      return;
      if (paramArrayList1 == null)
        break label297;
      i = 0;
      break label244;
      i += 1;
      break label244;
      i = 1;
      break label485;
    }
  }

  private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> paramArrayList, SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2)
  {
    int j = 0;
    HashMap localHashMap;
    label155: 
    do
    {
      long l;
      while (true)
      {
        try
        {
          localHashMap = new HashMap();
          if ((paramArrayList == null) || (paramArrayList.isEmpty()))
            break;
          paramArrayList = TextUtils.join(",", paramArrayList);
          paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages WHERE mid IN(%s)", new Object[] { paramArrayList }), new Object[0]);
          if (!paramArrayList.next())
            break label155;
          if ((paramArrayList.intValue(2) != 0) || (paramArrayList.intValue(1) != 0))
            continue;
          l = paramArrayList.longValue(0);
          paramSparseArray1 = (Integer)localHashMap.get(Long.valueOf(l));
          if (paramSparseArray1 == null)
          {
            localHashMap.put(Long.valueOf(l), Integer.valueOf(1));
            continue;
          }
        }
        catch (Exception paramArrayList)
        {
          FileLog.e(paramArrayList);
          return;
        }
        localHashMap.put(Long.valueOf(l), Integer.valueOf(paramSparseArray1.intValue() + 1));
        continue;
        paramArrayList.dispose();
      }
      while (!localHashMap.isEmpty())
      {
        this.database.beginTransaction();
        paramArrayList = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
        paramSparseArray1 = localHashMap.entrySet().iterator();
        while (true)
          if (paramSparseArray1.hasNext())
          {
            paramSparseArray2 = (Map.Entry)paramSparseArray1.next();
            paramArrayList.requery();
            paramArrayList.bindInteger(1, ((Integer)paramSparseArray2.getValue()).intValue());
            paramArrayList.bindLong(2, ((Long)paramSparseArray2.getKey()).longValue());
            paramArrayList.step();
            continue;
            if ((paramSparseArray1 != null) && (paramSparseArray1.size() != 0))
            {
              i = 0;
              while (i < paramSparseArray1.size())
              {
                int k = paramSparseArray1.keyAt(i);
                l = ((Long)paramSparseArray1.get(k)).longValue();
                paramArrayList = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", new Object[] { Integer.valueOf(k), Long.valueOf(l) }), new Object[0]);
                if (paramArrayList.next())
                {
                  int m = paramArrayList.intValue(0);
                  localHashMap.put(Long.valueOf(k), Integer.valueOf(m));
                }
                paramArrayList.dispose();
                paramArrayList = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                paramArrayList.requery();
                paramArrayList.bindLong(1, k);
                paramArrayList.bindInteger(2, (int)l);
                paramArrayList.bindLong(3, k);
                paramArrayList.step();
                paramArrayList.dispose();
                i += 1;
              }
            }
            if ((paramSparseArray2 == null) || (paramSparseArray2.size() == 0))
              break;
            int i = j;
            while (i < paramSparseArray2.size())
            {
              j = paramSparseArray2.keyAt(i);
              l = ((Long)paramSparseArray2.get(j)).longValue();
              paramArrayList = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
              paramArrayList.requery();
              paramArrayList.bindLong(1, j);
              paramArrayList.bindInteger(2, (int)l);
              paramArrayList.bindLong(3, j);
              paramArrayList.step();
              paramArrayList.dispose();
              i += 1;
            }
            break;
          }
        paramArrayList.dispose();
        this.database.commitTransaction();
      }
    }
    while (localHashMap.isEmpty());
    MessagesController.getInstance().processDialogsUpdateRead(localHashMap);
  }

  // ERROR //
  private long[] updateMessageStateAndIdInternal(long paramLong, Integer paramInteger, int paramInt1, int paramInt2, int paramInt3)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 16
    //   3: iload 4
    //   5: i2l
    //   6: lstore 12
    //   8: aload_3
    //   9: astore 18
    //   11: aload_3
    //   12: ifnonnull +150 -> 162
    //   15: aload_0
    //   16: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   19: getstatic 539	java/util/Locale:US	Ljava/util/Locale;
    //   22: ldc_w 1272
    //   25: iconst_1
    //   26: anewarray 4	java/lang/Object
    //   29: dup
    //   30: iconst_0
    //   31: lload_1
    //   32: invokestatic 559	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   35: aastore
    //   36: invokestatic 565	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   39: iconst_0
    //   40: anewarray 4	java/lang/Object
    //   43: invokevirtual 569	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
    //   46: astore 16
    //   48: aload_3
    //   49: astore 17
    //   51: aload 16
    //   53: astore 18
    //   55: aload 16
    //   57: invokevirtual 574	org/vidogram/SQLite/SQLiteCursor:next	()Z
    //   60: ifeq +22 -> 82
    //   63: aload 16
    //   65: astore 18
    //   67: aload 16
    //   69: iconst_0
    //   70: invokevirtual 579	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
    //   73: istore 7
    //   75: iload 7
    //   77: invokestatic 440	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   80: astore 17
    //   82: aload 16
    //   84: astore 19
    //   86: aload 17
    //   88: astore 18
    //   90: aload 16
    //   92: ifnull +883 -> 975
    //   95: aload 16
    //   97: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   100: aload 17
    //   102: astore_3
    //   103: aload_3
    //   104: astore 18
    //   106: aload_3
    //   107: ifnonnull +55 -> 162
    //   110: aconst_null
    //   111: areturn
    //   112: astore 17
    //   114: aconst_null
    //   115: astore 16
    //   117: aload 16
    //   119: astore 18
    //   121: aload 17
    //   123: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   126: aload 16
    //   128: astore 19
    //   130: aload_3
    //   131: astore 18
    //   133: aload 16
    //   135: ifnull +840 -> 975
    //   138: aload 16
    //   140: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   143: goto -40 -> 103
    //   146: astore_3
    //   147: aconst_null
    //   148: astore 18
    //   150: aload 18
    //   152: ifnull +8 -> 160
    //   155: aload 18
    //   157: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   160: aload_3
    //   161: athrow
    //   162: aload 18
    //   164: invokevirtual 726	java/lang/Integer:intValue	()I
    //   167: i2l
    //   168: lstore_1
    //   169: lload 12
    //   171: lstore 10
    //   173: lload_1
    //   174: lstore 8
    //   176: iload 6
    //   178: ifeq +24 -> 202
    //   181: lload_1
    //   182: iload 6
    //   184: i2l
    //   185: bipush 32
    //   187: lshl
    //   188: lor
    //   189: lstore 8
    //   191: lload 12
    //   193: iload 6
    //   195: i2l
    //   196: bipush 32
    //   198: lshl
    //   199: lor
    //   200: lstore 10
    //   202: lconst_0
    //   203: lstore 12
    //   205: aload_0
    //   206: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   209: getstatic 539	java/util/Locale:US	Ljava/util/Locale;
    //   212: ldc_w 1274
    //   215: iconst_1
    //   216: anewarray 4	java/lang/Object
    //   219: dup
    //   220: iconst_0
    //   221: lload 8
    //   223: invokestatic 559	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   226: aastore
    //   227: invokestatic 565	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   230: iconst_0
    //   231: anewarray 4	java/lang/Object
    //   234: invokevirtual 569	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
    //   237: astore_3
    //   238: aload_3
    //   239: astore 16
    //   241: lload 12
    //   243: lstore_1
    //   244: aload 16
    //   246: astore_3
    //   247: aload 16
    //   249: invokevirtual 574	org/vidogram/SQLite/SQLiteCursor:next	()Z
    //   252: ifeq +13 -> 265
    //   255: aload 16
    //   257: astore_3
    //   258: aload 16
    //   260: iconst_0
    //   261: invokevirtual 743	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
    //   264: lstore_1
    //   265: lload_1
    //   266: lstore 14
    //   268: aload 16
    //   270: ifnull +11 -> 281
    //   273: aload 16
    //   275: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   278: lload_1
    //   279: lstore 14
    //   281: lload 14
    //   283: lconst_0
    //   284: lcmp
    //   285: ifne +57 -> 342
    //   288: aconst_null
    //   289: areturn
    //   290: astore 17
    //   292: aload 16
    //   294: astore_3
    //   295: aload 17
    //   297: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   300: lload 12
    //   302: lstore 14
    //   304: aload 16
    //   306: ifnull -25 -> 281
    //   309: aload 16
    //   311: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   314: lload 12
    //   316: lstore 14
    //   318: goto -37 -> 281
    //   321: astore_3
    //   322: aload 16
    //   324: astore 17
    //   326: aload_3
    //   327: astore 16
    //   329: aload 17
    //   331: ifnull +8 -> 339
    //   334: aload 17
    //   336: invokevirtual 585	org/vidogram/SQLite/SQLiteCursor:dispose	()V
    //   339: aload 16
    //   341: athrow
    //   342: lload 8
    //   344: lload 10
    //   346: lcmp
    //   347: ifne +127 -> 474
    //   350: iload 5
    //   352: ifeq +122 -> 474
    //   355: aconst_null
    //   356: astore 16
    //   358: aconst_null
    //   359: astore_3
    //   360: aload_0
    //   361: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   364: ldc_w 1276
    //   367: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   370: astore 17
    //   372: aload 17
    //   374: astore_3
    //   375: aload 17
    //   377: astore 16
    //   379: aload 17
    //   381: iconst_1
    //   382: iload 5
    //   384: invokevirtual 633	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
    //   387: aload 17
    //   389: astore_3
    //   390: aload 17
    //   392: astore 16
    //   394: aload 17
    //   396: iconst_2
    //   397: lload 10
    //   399: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   402: aload 17
    //   404: astore_3
    //   405: aload 17
    //   407: astore 16
    //   409: aload 17
    //   411: invokevirtual 636	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
    //   414: pop
    //   415: aload 17
    //   417: ifnull +8 -> 425
    //   420: aload 17
    //   422: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   425: iconst_2
    //   426: newarray long
    //   428: dup
    //   429: iconst_0
    //   430: lload 14
    //   432: lastore
    //   433: dup
    //   434: iconst_1
    //   435: iload 4
    //   437: i2l
    //   438: lastore
    //   439: areturn
    //   440: astore 17
    //   442: aload_3
    //   443: astore 16
    //   445: aload 17
    //   447: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   450: aload_3
    //   451: ifnull -26 -> 425
    //   454: aload_3
    //   455: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   458: goto -33 -> 425
    //   461: astore_3
    //   462: aload 16
    //   464: ifnull +8 -> 472
    //   467: aload 16
    //   469: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   472: aload_3
    //   473: athrow
    //   474: aconst_null
    //   475: astore 16
    //   477: aload_0
    //   478: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   481: ldc_w 1278
    //   484: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   487: astore 17
    //   489: aload 17
    //   491: astore 16
    //   493: aload 17
    //   495: astore_3
    //   496: aload 17
    //   498: iconst_1
    //   499: lload 10
    //   501: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   504: aload 17
    //   506: astore 16
    //   508: aload 17
    //   510: astore_3
    //   511: aload 17
    //   513: iconst_2
    //   514: lload 8
    //   516: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   519: aload 17
    //   521: astore 16
    //   523: aload 17
    //   525: astore_3
    //   526: aload 17
    //   528: invokevirtual 636	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
    //   531: pop
    //   532: aload 17
    //   534: astore_3
    //   535: aload 17
    //   537: ifnull +10 -> 547
    //   540: aload 17
    //   542: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   545: aconst_null
    //   546: astore_3
    //   547: aload_3
    //   548: astore 16
    //   550: aload_0
    //   551: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   554: ldc_w 1280
    //   557: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   560: astore 17
    //   562: aload 17
    //   564: astore 16
    //   566: aload 17
    //   568: astore_3
    //   569: aload 17
    //   571: iconst_1
    //   572: lload 10
    //   574: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   577: aload 17
    //   579: astore 16
    //   581: aload 17
    //   583: astore_3
    //   584: aload 17
    //   586: iconst_2
    //   587: lload 8
    //   589: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   592: aload 17
    //   594: astore 16
    //   596: aload 17
    //   598: astore_3
    //   599: aload 17
    //   601: invokevirtual 636	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
    //   604: pop
    //   605: aload 17
    //   607: astore_3
    //   608: aload 17
    //   610: ifnull +362 -> 972
    //   613: aload 17
    //   615: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   618: aconst_null
    //   619: astore_3
    //   620: aload_3
    //   621: astore 16
    //   623: aload_0
    //   624: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   627: ldc_w 1282
    //   630: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   633: astore 17
    //   635: aload 17
    //   637: astore 16
    //   639: aload 17
    //   641: astore_3
    //   642: aload 17
    //   644: iconst_1
    //   645: lload 10
    //   647: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   650: aload 17
    //   652: astore 16
    //   654: aload 17
    //   656: astore_3
    //   657: aload 17
    //   659: iconst_2
    //   660: lload 8
    //   662: invokevirtual 629	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
    //   665: aload 17
    //   667: astore 16
    //   669: aload 17
    //   671: astore_3
    //   672: aload 17
    //   674: invokevirtual 636	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
    //   677: pop
    //   678: aload 17
    //   680: ifnull +8 -> 688
    //   683: aload 17
    //   685: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   688: iconst_2
    //   689: newarray long
    //   691: dup
    //   692: iconst_0
    //   693: lload 14
    //   695: lastore
    //   696: dup
    //   697: iconst_1
    //   698: aload 18
    //   700: invokevirtual 726	java/lang/Integer:intValue	()I
    //   703: i2l
    //   704: lastore
    //   705: areturn
    //   706: astore_3
    //   707: aload 16
    //   709: astore_3
    //   710: aload_0
    //   711: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   714: getstatic 539	java/util/Locale:US	Ljava/util/Locale;
    //   717: ldc_w 1284
    //   720: iconst_1
    //   721: anewarray 4	java/lang/Object
    //   724: dup
    //   725: iconst_0
    //   726: lload 8
    //   728: invokestatic 559	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   731: aastore
    //   732: invokestatic 565	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   735: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   738: invokevirtual 605	org/vidogram/SQLite/SQLitePreparedStatement:stepThis	()Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   741: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   744: aload 16
    //   746: astore_3
    //   747: aload_0
    //   748: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   751: getstatic 539	java/util/Locale:US	Ljava/util/Locale;
    //   754: ldc_w 1286
    //   757: iconst_1
    //   758: anewarray 4	java/lang/Object
    //   761: dup
    //   762: iconst_0
    //   763: lload 8
    //   765: invokestatic 559	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   768: aastore
    //   769: invokestatic 565	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   772: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   775: invokevirtual 605	org/vidogram/SQLite/SQLitePreparedStatement:stepThis	()Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   778: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   781: aload 16
    //   783: astore_3
    //   784: aload 16
    //   786: ifnull -239 -> 547
    //   789: aload 16
    //   791: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   794: aconst_null
    //   795: astore_3
    //   796: goto -249 -> 547
    //   799: astore 17
    //   801: aload 16
    //   803: astore_3
    //   804: aload 17
    //   806: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   809: goto -28 -> 781
    //   812: astore 16
    //   814: aload_3
    //   815: ifnull +7 -> 822
    //   818: aload_3
    //   819: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   822: aload 16
    //   824: athrow
    //   825: astore_3
    //   826: aload 16
    //   828: astore_3
    //   829: aload_0
    //   830: getfield 315	org/vidogram/messenger/MessagesStorage:database	Lorg/vidogram/SQLite/SQLiteDatabase;
    //   833: getstatic 539	java/util/Locale:US	Ljava/util/Locale;
    //   836: ldc_w 1288
    //   839: iconst_1
    //   840: anewarray 4	java/lang/Object
    //   843: dup
    //   844: iconst_0
    //   845: lload 8
    //   847: invokestatic 559	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   850: aastore
    //   851: invokestatic 565	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   854: invokevirtual 599	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   857: invokevirtual 605	org/vidogram/SQLite/SQLitePreparedStatement:stepThis	()Lorg/vidogram/SQLite/SQLitePreparedStatement;
    //   860: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   863: aload 16
    //   865: astore_3
    //   866: aload 16
    //   868: ifnull +104 -> 972
    //   871: aload 16
    //   873: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   876: aconst_null
    //   877: astore_3
    //   878: goto -258 -> 620
    //   881: astore 17
    //   883: aload 16
    //   885: astore_3
    //   886: aload 17
    //   888: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   891: goto -28 -> 863
    //   894: astore 16
    //   896: aload_3
    //   897: ifnull +7 -> 904
    //   900: aload_3
    //   901: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   904: aload 16
    //   906: athrow
    //   907: astore 17
    //   909: aload 16
    //   911: astore_3
    //   912: aload 17
    //   914: invokestatic 616	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   917: aload 16
    //   919: ifnull -231 -> 688
    //   922: aload 16
    //   924: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   927: goto -239 -> 688
    //   930: astore 16
    //   932: aload_3
    //   933: ifnull +7 -> 940
    //   936: aload_3
    //   937: invokevirtual 606	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
    //   940: aload 16
    //   942: athrow
    //   943: astore 16
    //   945: aconst_null
    //   946: astore_3
    //   947: goto -133 -> 814
    //   950: astore 16
    //   952: aload_3
    //   953: astore 17
    //   955: goto -626 -> 329
    //   958: astore 17
    //   960: goto -668 -> 292
    //   963: astore_3
    //   964: goto -814 -> 150
    //   967: astore 17
    //   969: goto -852 -> 117
    //   972: goto -352 -> 620
    //   975: aload 19
    //   977: astore 16
    //   979: aload 18
    //   981: astore_3
    //   982: goto -879 -> 103
    //
    // Exception table:
    //   from	to	target	type
    //   15	48	112	java/lang/Exception
    //   15	48	146	finally
    //   205	238	290	java/lang/Exception
    //   205	238	321	finally
    //   360	372	440	java/lang/Exception
    //   379	387	440	java/lang/Exception
    //   394	402	440	java/lang/Exception
    //   409	415	440	java/lang/Exception
    //   360	372	461	finally
    //   379	387	461	finally
    //   394	402	461	finally
    //   409	415	461	finally
    //   445	450	461	finally
    //   477	489	706	java/lang/Exception
    //   496	504	706	java/lang/Exception
    //   511	519	706	java/lang/Exception
    //   526	532	706	java/lang/Exception
    //   710	744	799	java/lang/Exception
    //   747	781	799	java/lang/Exception
    //   496	504	812	finally
    //   511	519	812	finally
    //   526	532	812	finally
    //   710	744	812	finally
    //   747	781	812	finally
    //   804	809	812	finally
    //   550	562	825	java/lang/Exception
    //   569	577	825	java/lang/Exception
    //   584	592	825	java/lang/Exception
    //   599	605	825	java/lang/Exception
    //   829	863	881	java/lang/Exception
    //   550	562	894	finally
    //   569	577	894	finally
    //   584	592	894	finally
    //   599	605	894	finally
    //   829	863	894	finally
    //   886	891	894	finally
    //   623	635	907	java/lang/Exception
    //   642	650	907	java/lang/Exception
    //   657	665	907	java/lang/Exception
    //   672	678	907	java/lang/Exception
    //   623	635	930	finally
    //   642	650	930	finally
    //   657	665	930	finally
    //   672	678	930	finally
    //   912	917	930	finally
    //   477	489	943	finally
    //   247	255	950	finally
    //   258	265	950	finally
    //   295	300	950	finally
    //   247	255	958	java/lang/Exception
    //   258	265	958	java/lang/Exception
    //   55	63	963	finally
    //   67	75	963	finally
    //   121	126	963	finally
    //   55	63	967	java/lang/Exception
    //   67	75	967	java/lang/Exception
  }

  private void updateUsersInternal(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (Thread.currentThread().getId() != this.storageQueue.getId())
      throw new RuntimeException("wrong db thread");
    Object localObject1;
    Object localObject2;
    if (paramBoolean1)
    {
      if (paramBoolean2);
      try
      {
        this.database.beginTransaction();
        localObject1 = this.database.executeFast("UPDATE users SET status = ? WHERE uid = ?");
        paramArrayList = paramArrayList.iterator();
        while (true)
        {
          if (!paramArrayList.hasNext())
            break label143;
          localObject2 = (TLRPC.User)paramArrayList.next();
          ((SQLitePreparedStatement)localObject1).requery();
          if (((TLRPC.User)localObject2).status == null)
            break;
          ((SQLitePreparedStatement)localObject1).bindInteger(1, ((TLRPC.User)localObject2).status.expires);
          ((SQLitePreparedStatement)localObject1).bindInteger(2, ((TLRPC.User)localObject2).id);
          ((SQLitePreparedStatement)localObject1).step();
        }
      }
      catch (Exception paramArrayList)
      {
        FileLog.e(paramArrayList);
      }
    }
    label143: 
    do
    {
      do
      {
        do
        {
          return;
          ((SQLitePreparedStatement)localObject1).bindInteger(1, 0);
          break;
          ((SQLitePreparedStatement)localObject1).dispose();
        }
        while (!paramBoolean2);
        this.database.commitTransaction();
        return;
        localObject2 = new StringBuilder();
        localObject1 = new HashMap();
        paramArrayList = paramArrayList.iterator();
        TLRPC.User localUser1;
        while (paramArrayList.hasNext())
        {
          localUser1 = (TLRPC.User)paramArrayList.next();
          if (((StringBuilder)localObject2).length() != 0)
            ((StringBuilder)localObject2).append(",");
          ((StringBuilder)localObject2).append(localUser1.id);
          ((HashMap)localObject1).put(Integer.valueOf(localUser1.id), localUser1);
        }
        paramArrayList = new ArrayList();
        getUsersInternal(((StringBuilder)localObject2).toString(), paramArrayList);
        localObject2 = paramArrayList.iterator();
        while (((Iterator)localObject2).hasNext())
        {
          localUser1 = (TLRPC.User)((Iterator)localObject2).next();
          TLRPC.User localUser2 = (TLRPC.User)((HashMap)localObject1).get(Integer.valueOf(localUser1.id));
          if (localUser2 == null)
            continue;
          if ((localUser2.first_name != null) && (localUser2.last_name != null))
          {
            if (!UserObject.isContact(localUser1))
            {
              localUser1.first_name = localUser2.first_name;
              localUser1.last_name = localUser2.last_name;
            }
            localUser1.username = localUser2.username;
            continue;
          }
          if (localUser2.photo != null)
          {
            localUser1.photo = localUser2.photo;
            continue;
          }
          if (localUser2.phone == null)
            continue;
          localUser1.phone = localUser2.phone;
        }
      }
      while (paramArrayList.isEmpty());
      if (paramBoolean2)
        this.database.beginTransaction();
      putUsersInternal(paramArrayList);
    }
    while (!paramBoolean2);
    this.database.commitTransaction();
  }

  public void addRecentLocalFile(String paramString1, String paramString2, TLRPC.Document paramDocument)
  {
    if ((paramString1 == null) || (paramString1.length() == 0) || (((paramString2 == null) || (paramString2.length() == 0)) && (paramDocument == null)))
      return;
    this.storageQueue.postRunnable(new Runnable(paramDocument, paramString1, paramString2)
    {
      public void run()
      {
        try
        {
          if (this.val$document != null)
          {
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$document.getObjectSize());
            this.val$document.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
            localSQLitePreparedStatement.bindString(2, this.val$imageUrl);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            localNativeByteBuffer.reuse();
            return;
          }
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindString(1, this.val$localUrl);
          localSQLitePreparedStatement.bindString(2, this.val$imageUrl);
          localSQLitePreparedStatement.step();
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

  public void applyPhoneBookUpdates(String paramString1, String paramString2)
  {
    if ((paramString1.length() == 0) && (paramString2.length() == 0))
      return;
    this.storageQueue.postRunnable(new Runnable(paramString1, paramString2)
    {
      public void run()
      {
        try
        {
          if (this.val$adds.length() != 0)
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 0 WHERE sphone IN(%s)", new Object[] { this.val$adds })).stepThis().dispose();
          if (this.val$deletes.length() != 0)
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 1 WHERE sphone IN(%s)", new Object[] { this.val$deletes })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public boolean checkMessageId(long paramLong, int paramInt)
  {
    boolean[] arrayOfBoolean = new boolean[1];
    Semaphore localSemaphore = new Semaphore(0);
    this.storageQueue.postRunnable(new Runnable(paramLong, paramInt, arrayOfBoolean, localSemaphore)
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d AND mid = %d", new Object[] { Long.valueOf(this.val$dialog_id), Integer.valueOf(this.val$mid) }), new Object[0]);
          localObject1 = localSQLiteCursor;
          localObject3 = localSQLiteCursor;
          if (localSQLiteCursor.next())
          {
            localObject1 = localSQLiteCursor;
            localObject3 = localSQLiteCursor;
            this.val$result[0] = true;
          }
          if (localSQLiteCursor != null)
            localSQLiteCursor.dispose();
          this.val$semaphore.release();
          return;
        }
        catch (Exception localException)
        {
          while (true)
          {
            localObject3 = localObject1;
            FileLog.e(localException);
            if (localObject1 == null)
              continue;
            localObject1.dispose();
          }
        }
        finally
        {
          if (localObject3 != null)
            localObject3.dispose();
        }
        throw localObject2;
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void cleanup(boolean paramBoolean)
  {
    this.storageQueue.cleanupQueue();
    this.storageQueue.postRunnable(new Runnable(paramBoolean)
    {
      public void run()
      {
        MessagesStorage.this.cleanupInternal();
        MessagesStorage.this.openDatabase(false);
        if (this.val$isLogin)
          Utilities.stageQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              MessagesController.getInstance().getDifference();
            }
          });
      }
    });
  }

  public void clearDownloadQueue(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        try
        {
          if (this.val$type == 0)
          {
            MessagesStorage.this.database.executeFast("DELETE FROM download_queue WHERE 1").stepThis().dispose();
            return;
          }
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE type = %d", new Object[] { Integer.valueOf(this.val$type) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void clearUserPhoto(int paramInt, long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt, paramLong)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + this.val$uid + " AND id = " + this.val$pid).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void clearUserPhotos(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM user_photos WHERE uid = " + this.val$uid).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void clearWebRecent(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE type = " + this.val$type).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void closeHolesInMedia(long paramLong, int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 0;
    Object localObject3;
    Object localObject4;
    if (paramInt3 < 0)
      try
      {
        localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type >= 0 AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
        break label851;
        while (((SQLiteCursor)localObject3).next())
        {
          localObject4 = localObject1;
          if (localObject1 == null)
            localObject4 = new ArrayList();
          paramInt3 = ((SQLiteCursor)localObject3).intValue(0);
          int j = ((SQLiteCursor)localObject3).intValue(1);
          int k = ((SQLiteCursor)localObject3).intValue(2);
          if (j == k)
          {
            localObject1 = localObject4;
            if (j == 1)
              continue;
          }
          ((ArrayList)localObject4).add(new Hole(paramInt3, j, k));
          Object localObject1 = localObject4;
        }
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
      }
    label851: label857: label864: 
    while (true)
    {
      return;
      localObject3 = this.database.queryFinalized(String.format(Locale.US, "SELECT type, start, end FROM media_holes_v2 WHERE uid = %d AND type = %d AND ((end >= %d AND end <= %d) OR (start >= %d AND start <= %d) OR (start >= %d AND end <= %d) OR (start <= %d AND end >= %d))", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt3), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), Integer.valueOf(paramInt1), Integer.valueOf(paramInt2) }), new Object[0]);
      break label851;
      ((SQLiteCursor)localObject3).dispose();
      if (localException1 == null)
        continue;
      paramInt3 = i;
      while (true)
      {
        if (paramInt3 >= localException1.size())
          break label864;
        localObject3 = (Hole)localException1.get(paramInt3);
        if ((paramInt2 >= ((Hole)localObject3).end - 1) && (paramInt1 <= ((Hole)localObject3).start + 1))
        {
          this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject3).type), Integer.valueOf(((Hole)localObject3).start), Integer.valueOf(((Hole)localObject3).end) })).stepThis().dispose();
        }
        else if (paramInt2 >= ((Hole)localObject3).end - 1)
        {
          i = ((Hole)localObject3).end;
          if (i != paramInt1)
            try
            {
              this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET end = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt1), Long.valueOf(paramLong), Integer.valueOf(((Hole)localObject3).type), Integer.valueOf(((Hole)localObject3).start), Integer.valueOf(((Hole)localObject3).end) })).stepThis().dispose();
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
            }
        }
        else if (paramInt1 <= localException2.start + 1)
        {
          i = localException2.start;
          if (i != paramInt2)
            try
            {
              this.database.executeFast(String.format(Locale.US, "UPDATE media_holes_v2 SET start = %d WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Integer.valueOf(paramInt2), Long.valueOf(paramLong), Integer.valueOf(localException2.type), Integer.valueOf(localException2.start), Integer.valueOf(localException2.end) })).stepThis().dispose();
            }
            catch (Exception localException3)
            {
              FileLog.e(localException3);
            }
        }
        else
        {
          this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = %d AND end = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(localException3.type), Integer.valueOf(localException3.start), Integer.valueOf(localException3.end) })).stepThis().dispose();
          localObject4 = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
          ((SQLitePreparedStatement)localObject4).requery();
          ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
          ((SQLitePreparedStatement)localObject4).bindInteger(2, localException3.type);
          ((SQLitePreparedStatement)localObject4).bindInteger(3, localException3.start);
          ((SQLitePreparedStatement)localObject4).bindInteger(4, paramInt1);
          ((SQLitePreparedStatement)localObject4).step();
          ((SQLitePreparedStatement)localObject4).requery();
          ((SQLitePreparedStatement)localObject4).bindLong(1, paramLong);
          ((SQLitePreparedStatement)localObject4).bindInteger(2, localException3.type);
          ((SQLitePreparedStatement)localObject4).bindInteger(3, paramInt2);
          ((SQLitePreparedStatement)localObject4).bindInteger(4, localException3.end);
          ((SQLitePreparedStatement)localObject4).step();
          ((SQLitePreparedStatement)localObject4).dispose();
          break label857;
          Object localObject2 = null;
          break;
        }
        paramInt3 += 1;
      }
    }
  }

  public void commitTransaction(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.this.database.commitTransaction();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      return;
    }
    try
    {
      this.database.commitTransaction();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public long createPendingTask(NativeByteBuffer paramNativeByteBuffer)
  {
    if (paramNativeByteBuffer == null)
      return 0L;
    long l = this.lastTaskId.getAndAdd(1L);
    this.storageQueue.postRunnable(new Runnable(l, paramNativeByteBuffer)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO pending_tasks VALUES(?, ?)");
          localSQLitePreparedStatement.bindLong(1, this.val$id);
          localSQLitePreparedStatement.bindByteBuffer(2, this.val$data);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          this.val$data.reuse();
        }
        throw localObject;
      }
    });
    return l;
  }

  public void createTaskForSecretChat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, ArrayList<Long> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramArrayList, paramInt1, paramInt4, paramInt2, paramInt3)
    {
      public void run()
      {
        while (true)
        {
          int j;
          try
          {
            SparseArray localSparseArray = new SparseArray();
            ArrayList localArrayList3 = new ArrayList();
            StringBuilder localStringBuilder = new StringBuilder();
            if (this.val$random_ids != null)
              continue;
            Object localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, ttl FROM messages WHERE uid = %d AND out = %d AND read_state != 0 AND ttl > 0 AND date <= %d AND send_state = 0 AND media != 1", new Object[] { Long.valueOf(this.val$chat_id << 32), Integer.valueOf(this.val$isOut), Integer.valueOf(this.val$time) }), new Object[0]);
            int i = 2147483647;
            if (!((SQLiteCursor)localObject).next())
              continue;
            int n = ((SQLiteCursor)localObject).intValue(1);
            int k = ((SQLiteCursor)localObject).intValue(0);
            if (this.val$random_ids == null)
              continue;
            localArrayList3.add(Long.valueOf(k));
            if (n <= 0)
              continue;
            if (this.val$time <= this.val$readTime)
              continue;
            j = this.val$time;
            j += n;
            i = Math.min(i, j);
            ArrayList localArrayList2 = (ArrayList)localSparseArray.get(j);
            ArrayList localArrayList1 = localArrayList2;
            if (localArrayList2 != null)
              continue;
            localArrayList1 = new ArrayList();
            localSparseArray.put(j, localArrayList1);
            if (localStringBuilder.length() == 0)
              continue;
            localStringBuilder.append(",");
            localStringBuilder.append(k);
            localArrayList1.add(Integer.valueOf(k));
            continue;
            localObject = TextUtils.join(",", this.val$random_ids);
            localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT m.mid, m.ttl FROM messages as m INNER JOIN randoms as r ON m.mid = r.mid WHERE r.random_id IN (%s)", new Object[] { localObject }), new Object[0]);
            i = 2147483647;
            continue;
            j = this.val$readTime;
            continue;
            ((SQLiteCursor)localObject).dispose();
            if (this.val$random_ids == null)
              continue;
            AndroidUtilities.runOnUIThread(new Runnable(localArrayList3)
            {
              public void run()
              {
                MessagesStorage.getInstance().markMessagesContentAsRead(this.val$midsArray);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesReadContent, new Object[] { this.val$midsArray });
              }
            });
            if (localSparseArray.size() == 0)
              continue;
            MessagesStorage.this.database.beginTransaction();
            localObject = MessagesStorage.this.database.executeFast("REPLACE INTO enc_tasks_v2 VALUES(?, ?)");
            j = 0;
            if (j >= localSparseArray.size())
              continue;
            n = localSparseArray.keyAt(j);
            localArrayList1 = (ArrayList)localSparseArray.get(n);
            int m = 0;
            if (m < localArrayList1.size())
            {
              ((SQLitePreparedStatement)localObject).requery();
              ((SQLitePreparedStatement)localObject).bindInteger(1, ((Integer)localArrayList1.get(m)).intValue());
              ((SQLitePreparedStatement)localObject).bindInteger(2, n);
              ((SQLitePreparedStatement)localObject).step();
              m += 1;
              continue;
              ((SQLitePreparedStatement)localObject).dispose();
              MessagesStorage.this.database.commitTransaction();
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET ttl = 0 WHERE mid IN(%s)", new Object[] { localStringBuilder.toString() })).stepThis().dispose();
              MessagesController.getInstance().didAddedNewTask(i, localSparseArray);
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          j += 1;
        }
      }
    });
  }

  public void deleteBlockedUser(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE uid = " + this.val$id).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void deleteCachedPhoneBook(String paramString)
  {
    this.storageQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE user_phones_v6 SET deleted = 1 WHERE sphone IN(%s)", new Object[] { this.val$phone })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
        }
      }
    });
  }

  public void deleteContacts(ArrayList<Integer> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        try
        {
          String str = TextUtils.join(",", this.val$uids);
          MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + str + ")").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void deleteDialog(long paramLong, int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt, paramLong)
    {
      public void run()
      {
        while (true)
        {
          Object localObject1;
          int j;
          try
          {
            if (this.val$messagesOnly != 3)
              continue;
            SQLiteCursor localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT last_mid FROM dialogs WHERE did = " + this.val$did, new Object[0]);
            if (!localSQLiteCursor1.next())
              break label1505;
            i = localSQLiteCursor1.intValue(0);
            localSQLiteCursor1.dispose();
            if (i != 0)
              return;
            if (((int)this.val$did != 0) && (this.val$messagesOnly != 2))
              continue;
            localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + this.val$did, new Object[0]);
            localObject1 = new ArrayList();
            try
            {
              if (!localSQLiteCursor1.next())
                continue;
              localObject4 = localSQLiteCursor1.byteBufferValue(0);
              if (localObject4 == null)
                continue;
              Object localObject2 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
              ((NativeByteBuffer)localObject4).reuse();
              if ((localObject2 == null) || (((TLRPC.Message)localObject2).media == null))
                continue;
              if (!(((TLRPC.Message)localObject2).media instanceof TLRPC.TL_messageMediaPhoto))
                break label848;
              localObject2 = ((TLRPC.Message)localObject2).media.photo.sizes.iterator();
              if (!((Iterator)localObject2).hasNext())
                continue;
              localObject4 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject2).next());
              if ((localObject4 == null) || (((File)localObject4).toString().length() <= 0))
                continue;
              ((ArrayList)localObject1).add(localObject4);
              continue;
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              localSQLiteCursor1.dispose();
              FileLoader.getInstance().deleteFiles((ArrayList)localObject1, this.val$messagesOnly);
            }
            if ((this.val$messagesOnly != 0) && (this.val$messagesOnly != 3))
              break label984;
            MessagesStorage.this.database.executeFast("DELETE FROM dialogs WHERE did = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM chat_settings_v2 WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM chat_pinned WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM search_recent WHERE did = " + this.val$did).stepThis().dispose();
            i = (int)this.val$did;
            j = (int)(this.val$did >> 32);
            if (i == 0)
              break label946;
            if (j != 1)
              break label939;
            MessagesStorage.this.database.executeFast("DELETE FROM chats WHERE uid = " + i).stepThis().dispose();
            MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + this.val$did).stepThis().dispose();
            MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + this.val$did).stepThis().dispose();
            BotQuery.clearBotKeyboard(this.val$did, null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadRecentDialogsSearch, new Object[0]);
              }
            });
            return;
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
            return;
          }
          label848: if (!(localException2.media instanceof TLRPC.TL_messageMediaDocument))
            continue;
          Object localObject4 = FileLoader.getPathToAttach(localException2.media.document);
          if ((localObject4 != null) && (((File)localObject4).toString().length() > 0))
            ((ArrayList)localObject1).add(localObject4);
          Object localObject3 = FileLoader.getPathToAttach(localException2.media.document.thumb);
          if ((localObject3 == null) || (((File)localObject3).toString().length() <= 0))
            continue;
          ((ArrayList)localObject1).add(localObject3);
          continue;
          label939: if (i >= 0)
            continue;
          continue;
          label946: MessagesStorage.this.database.executeFast("DELETE FROM enc_chats WHERE uid = " + j).stepThis().dispose();
          continue;
          label984: if (this.val$messagesOnly != 2)
            continue;
          SQLiteCursor localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + this.val$did, new Object[0]);
          if (localSQLiteCursor2.next())
          {
            long l1 = localSQLiteCursor2.longValue(0);
            long l2 = localSQLiteCursor2.longValue(1);
            localObject1 = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + this.val$did + " AND mid IN (" + l1 + "," + l2 + ")", new Object[0]);
            i = -1;
            while (true)
            {
              j = i;
              try
              {
                if (((SQLiteCursor)localObject1).next())
                {
                  localObject3 = ((SQLiteCursor)localObject1).byteBufferValue(0);
                  i = j;
                  if (localObject3 == null)
                    continue;
                  localObject4 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  i = j;
                  if (localObject4 == null)
                    continue;
                  i = ((TLRPC.Message)localObject4).id;
                }
              }
              catch (Exception localSQLitePreparedStatement)
              {
                FileLog.e(localException3);
                ((SQLiteCursor)localObject1).dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + this.val$did + " AND mid != " + l1 + " AND mid != " + l2).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + this.val$did).stepThis().dispose();
                BotQuery.clearBotKeyboard(this.val$did, null);
                localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                if (j != -1)
                  MessagesStorage.createFirstHoles(this.val$did, (SQLitePreparedStatement)localObject1, localSQLitePreparedStatement, j);
                ((SQLitePreparedStatement)localObject1).dispose();
                localSQLitePreparedStatement.dispose();
              }
            }
          }
          localSQLiteCursor2.dispose();
          return;
          label1505: int i = -1;
        }
      }
    });
  }

  public void deleteUserChannelHistory(int paramInt1, int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        try
        {
          long l = -this.val$channelId;
          ArrayList localArrayList1 = new ArrayList();
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT data FROM messages WHERE uid = " + l, new Object[0]);
          ArrayList localArrayList2 = new ArrayList();
          while (true)
          {
            try
            {
              if (!localSQLiteCursor.next())
                continue;
              localObject2 = localSQLiteCursor.byteBufferValue(0);
              if (localObject2 == null)
                continue;
              Object localObject1 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
              ((NativeByteBuffer)localObject2).reuse();
              if ((localObject1 == null) || (((TLRPC.Message)localObject1).from_id != this.val$uid) || (((TLRPC.Message)localObject1).id == 1))
                continue;
              localArrayList1.add(Integer.valueOf(((TLRPC.Message)localObject1).id));
              if ((((TLRPC.Message)localObject1).media instanceof TLRPC.TL_messageMediaPhoto))
              {
                localObject1 = ((TLRPC.Message)localObject1).media.photo.sizes.iterator();
                if (!((Iterator)localObject1).hasNext())
                  continue;
                localObject2 = FileLoader.getPathToAttach((TLRPC.PhotoSize)((Iterator)localObject1).next());
                if ((localObject2 == null) || (((File)localObject2).toString().length() <= 0))
                  continue;
                localArrayList2.add(localObject2);
                continue;
              }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
              localSQLiteCursor.dispose();
              AndroidUtilities.runOnUIThread(new Runnable(localArrayList1)
              {
                public void run()
                {
                  MessagesController.getInstance().markChannelDialogMessageAsDeleted(this.val$mids, MessagesStorage.21.this.val$channelId);
                }
              });
              MessagesStorage.this.markMessagesAsDeletedInternal(localArrayList1, this.val$channelId);
              MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(localArrayList1, null, this.val$channelId);
              FileLoader.getInstance().deleteFiles(localArrayList2, 0);
              if (localArrayList1.isEmpty())
                continue;
              AndroidUtilities.runOnUIThread(new Runnable(localArrayList1)
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { this.val$mids, Integer.valueOf(MessagesStorage.21.this.val$channelId) });
                }
              });
              return;
            }
            if (!(localException2.media instanceof TLRPC.TL_messageMediaDocument))
              continue;
            Object localObject2 = FileLoader.getPathToAttach(localException2.media.document);
            if ((localObject2 != null) && (((File)localObject2).toString().length() > 0))
              localArrayList2.add(localObject2);
            File localFile = FileLoader.getPathToAttach(localException2.media.document.thumb);
            if ((localFile == null) || (localFile.toString().length() <= 0))
              continue;
            localArrayList2.add(localFile);
          }
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
        }
      }
    });
  }

  public void doneHolesInMedia(long paramLong, int paramInt1, int paramInt2)
  {
    int i = 0;
    SQLitePreparedStatement localSQLitePreparedStatement;
    if (paramInt2 == -1)
    {
      if (paramInt1 == 0)
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
      while (true)
      {
        localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
        paramInt1 = i;
        while (paramInt1 < 5)
        {
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, paramLong);
          localSQLitePreparedStatement.bindInteger(2, paramInt1);
          localSQLitePreparedStatement.bindInteger(3, 1);
          localSQLitePreparedStatement.bindInteger(4, 1);
          localSQLitePreparedStatement.step();
          paramInt1 += 1;
        }
        this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND start = 0", new Object[] { Long.valueOf(paramLong) })).stepThis().dispose();
      }
      localSQLitePreparedStatement.dispose();
      return;
    }
    if (paramInt1 == 0)
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) })).stepThis().dispose();
    while (true)
    {
      localSQLitePreparedStatement = this.database.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
      localSQLitePreparedStatement.requery();
      localSQLitePreparedStatement.bindLong(1, paramLong);
      localSQLitePreparedStatement.bindInteger(2, paramInt2);
      localSQLitePreparedStatement.bindInteger(3, 1);
      localSQLitePreparedStatement.bindInteger(4, 1);
      localSQLitePreparedStatement.step();
      localSQLitePreparedStatement.dispose();
      return;
      this.database.executeFast(String.format(Locale.US, "DELETE FROM media_holes_v2 WHERE uid = %d AND type = %d AND start = 0", new Object[] { Long.valueOf(paramLong), Integer.valueOf(paramInt2) })).stepThis().dispose();
    }
  }

  public void getBlockedUsers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList2;
        SQLiteCursor localSQLiteCursor;
        StringBuilder localStringBuilder;
        try
        {
          ArrayList localArrayList1 = new ArrayList();
          localArrayList2 = new ArrayList();
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM blocked_users WHERE 1", new Object[0]);
          localStringBuilder = new StringBuilder();
          while (localSQLiteCursor.next())
          {
            int i = localSQLiteCursor.intValue(0);
            localArrayList1.add(Integer.valueOf(i));
            if (localStringBuilder.length() != 0)
              localStringBuilder.append(",");
            localStringBuilder.append(i);
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localSQLiteCursor.dispose();
        if (localStringBuilder.length() != 0)
          MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList2);
        MessagesController.getInstance().processLoadedBlockedUsers(localException, localArrayList2, true);
      }
    });
  }

  public void getBotCache(String paramString, RequestDelegate paramRequestDelegate)
  {
    if ((paramString == null) || (paramRequestDelegate == null))
      return;
    int i = ConnectionsManager.getInstance().getCurrentTime();
    this.storageQueue.postRunnable(new Runnable(i, paramString, paramRequestDelegate)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aload_0
        //   1: getfield 23	org/vidogram/messenger/MessagesStorage$33:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   4: invokestatic 40	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   7: new 42	java/lang/StringBuilder
        //   10: dup
        //   11: invokespecial 43	java/lang/StringBuilder:<init>	()V
        //   14: ldc 45
        //   16: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   19: aload_0
        //   20: getfield 25	org/vidogram/messenger/MessagesStorage$33:val$currentDate	I
        //   23: invokevirtual 52	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   26: invokevirtual 56	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   29: invokevirtual 62	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
        //   32: invokevirtual 68	org/vidogram/SQLite/SQLitePreparedStatement:stepThis	()Lorg/vidogram/SQLite/SQLitePreparedStatement;
        //   35: invokevirtual 71	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
        //   38: aload_0
        //   39: getfield 23	org/vidogram/messenger/MessagesStorage$33:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   42: invokestatic 40	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   45: getstatic 77	java/util/Locale:US	Ljava/util/Locale;
        //   48: ldc 79
        //   50: iconst_1
        //   51: anewarray 4	java/lang/Object
        //   54: dup
        //   55: iconst_0
        //   56: aload_0
        //   57: getfield 27	org/vidogram/messenger/MessagesStorage$33:val$key	Ljava/lang/String;
        //   60: aastore
        //   61: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   64: iconst_0
        //   65: anewarray 4	java/lang/Object
        //   68: invokevirtual 89	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   71: astore 7
        //   73: aload 7
        //   75: invokevirtual 95	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   78: istore_2
        //   79: iload_2
        //   80: ifeq +167 -> 247
        //   83: aload 7
        //   85: iconst_0
        //   86: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   89: astore 4
        //   91: aload 4
        //   93: ifnull +149 -> 242
        //   96: aload 4
        //   98: iconst_0
        //   99: invokevirtual 105	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   102: istore_1
        //   103: iload_1
        //   104: getstatic 110	org/vidogram/tgnet/TLRPC$TL_messages_botCallbackAnswer:constructor	I
        //   107: if_icmpne +39 -> 146
        //   110: aload 4
        //   112: iload_1
        //   113: iconst_0
        //   114: invokestatic 114	org/vidogram/tgnet/TLRPC$TL_messages_botCallbackAnswer:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$TL_messages_botCallbackAnswer;
        //   117: astore_3
        //   118: aload 4
        //   120: invokevirtual 117	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   123: aload_3
        //   124: astore 5
        //   126: aload_3
        //   127: astore 4
        //   129: aload 7
        //   131: invokevirtual 118	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   134: aload_0
        //   135: getfield 29	org/vidogram/messenger/MessagesStorage$33:val$requestDelegate	Lorg/vidogram/tgnet/RequestDelegate;
        //   138: aload_3
        //   139: aconst_null
        //   140: invokeinterface 123 3 0
        //   145: return
        //   146: aload 4
        //   148: iload_1
        //   149: iconst_0
        //   150: invokestatic 128	org/vidogram/tgnet/TLRPC$TL_messages_botResults:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$TL_messages_botResults;
        //   153: astore_3
        //   154: goto -36 -> 118
        //   157: astore 6
        //   159: aconst_null
        //   160: astore_3
        //   161: aload_3
        //   162: astore 5
        //   164: aload_3
        //   165: astore 4
        //   167: aload 6
        //   169: invokestatic 134	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   172: goto -49 -> 123
        //   175: astore_3
        //   176: aload 5
        //   178: astore 4
        //   180: aload_3
        //   181: invokestatic 134	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   184: aload_0
        //   185: getfield 29	org/vidogram/messenger/MessagesStorage$33:val$requestDelegate	Lorg/vidogram/tgnet/RequestDelegate;
        //   188: aload 5
        //   190: aconst_null
        //   191: invokeinterface 123 3 0
        //   196: return
        //   197: astore_3
        //   198: aconst_null
        //   199: astore 4
        //   201: aload_0
        //   202: getfield 29	org/vidogram/messenger/MessagesStorage$33:val$requestDelegate	Lorg/vidogram/tgnet/RequestDelegate;
        //   205: aload 4
        //   207: aconst_null
        //   208: invokeinterface 123 3 0
        //   213: aload_3
        //   214: athrow
        //   215: astore 5
        //   217: aload_3
        //   218: astore 4
        //   220: aload 5
        //   222: astore_3
        //   223: goto -22 -> 201
        //   226: astore_3
        //   227: goto -26 -> 201
        //   230: astore_3
        //   231: aconst_null
        //   232: astore 5
        //   234: goto -58 -> 176
        //   237: astore 6
        //   239: goto -78 -> 161
        //   242: aconst_null
        //   243: astore_3
        //   244: goto -121 -> 123
        //   247: aconst_null
        //   248: astore_3
        //   249: goto -126 -> 123
        //
        // Exception table:
        //   from	to	target	type
        //   83	91	157	java/lang/Exception
        //   96	118	157	java/lang/Exception
        //   146	154	157	java/lang/Exception
        //   129	134	175	java/lang/Exception
        //   167	172	175	java/lang/Exception
        //   0	79	197	finally
        //   83	91	197	finally
        //   96	118	197	finally
        //   146	154	197	finally
        //   118	123	215	finally
        //   129	134	226	finally
        //   167	172	226	finally
        //   180	184	226	finally
        //   0	79	230	java/lang/Exception
        //   118	123	237	java/lang/Exception
      }
    });
  }

  public void getCachedPhoneBook()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        HashMap localHashMap = new HashMap();
        label267: 
        while (true)
        {
          SQLiteCursor localSQLiteCursor;
          try
          {
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
            if (localSQLiteCursor.next())
            {
              int i = localSQLiteCursor.intValue(0);
              ContactsController.Contact localContact = (ContactsController.Contact)localHashMap.get(Integer.valueOf(i));
              if (localContact != null)
                break label267;
              localContact = new ContactsController.Contact();
              localContact.first_name = localSQLiteCursor.stringValue(1);
              localContact.last_name = localSQLiteCursor.stringValue(2);
              if (localContact.first_name != null)
                continue;
              localContact.first_name = "";
              if (localContact.last_name != null)
                continue;
              localContact.last_name = "";
              localContact.id = i;
              localHashMap.put(Integer.valueOf(i), localContact);
              String str3 = localSQLiteCursor.stringValue(3);
              if (str3 == null)
                continue;
              localContact.phones.add(str3);
              String str2 = localSQLiteCursor.stringValue(4);
              if (str2 == null)
                continue;
              String str1 = str2;
              if (str2.length() != 8)
                continue;
              str1 = str2;
              if (str3.length() == 8)
                continue;
              str1 = b.b(str3);
              localContact.shortPhones.add(str1);
              localContact.phoneDeleted.add(Integer.valueOf(localSQLiteCursor.intValue(5)));
              localContact.phoneTypes.add("");
              continue;
            }
          }
          catch (Exception localException)
          {
            localHashMap.clear();
            FileLog.e(localException);
            ContactsController.getInstance().performSyncPhoneBook(localHashMap, true, true, false, false);
            return;
          }
          localSQLiteCursor.dispose();
          continue;
        }
      }
    });
  }

  public int getChannelPtsSync(int paramInt)
  {
    Semaphore localSemaphore = new Semaphore(0);
    Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    getInstance().getStorageQueue().postRunnable(new Runnable(paramInt, arrayOfInteger, localSemaphore)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore_2
        //   2: aconst_null
        //   3: astore_1
        //   4: aload_0
        //   5: getfield 23	org/vidogram/messenger/MessagesStorage$86:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8: invokestatic 40	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11: new 42	java/lang/StringBuilder
        //   14: dup
        //   15: invokespecial 43	java/lang/StringBuilder:<init>	()V
        //   18: ldc 45
        //   20: invokevirtual 49	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   23: aload_0
        //   24: getfield 25	org/vidogram/messenger/MessagesStorage$86:val$channelId	I
        //   27: ineg
        //   28: invokevirtual 52	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   31: invokevirtual 56	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   34: iconst_0
        //   35: anewarray 4	java/lang/Object
        //   38: invokevirtual 62	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   41: astore_3
        //   42: aload_3
        //   43: astore_1
        //   44: aload_3
        //   45: astore_2
        //   46: aload_3
        //   47: invokevirtual 68	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   50: ifeq +21 -> 71
        //   53: aload_3
        //   54: astore_1
        //   55: aload_3
        //   56: astore_2
        //   57: aload_0
        //   58: getfield 27	org/vidogram/messenger/MessagesStorage$86:val$pts	[Ljava/lang/Integer;
        //   61: iconst_0
        //   62: aload_3
        //   63: iconst_0
        //   64: invokevirtual 71	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   67: invokestatic 77	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   70: aastore
        //   71: aload_3
        //   72: ifnull +7 -> 79
        //   75: aload_3
        //   76: invokevirtual 80	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   79: aload_0
        //   80: getfield 29	org/vidogram/messenger/MessagesStorage$86:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   83: ifnull +10 -> 93
        //   86: aload_0
        //   87: getfield 29	org/vidogram/messenger/MessagesStorage$86:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   90: invokevirtual 85	java/util/concurrent/Semaphore:release	()V
        //   93: return
        //   94: astore_3
        //   95: aload_1
        //   96: astore_2
        //   97: aload_3
        //   98: invokestatic 91	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   101: aload_1
        //   102: ifnull -23 -> 79
        //   105: aload_1
        //   106: invokevirtual 80	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   109: goto -30 -> 79
        //   112: astore_1
        //   113: aload_2
        //   114: ifnull +7 -> 121
        //   117: aload_2
        //   118: invokevirtual 80	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   121: aload_1
        //   122: athrow
        //   123: astore_1
        //   124: aload_1
        //   125: invokestatic 91	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   128: return
        //
        // Exception table:
        //   from	to	target	type
        //   4	42	94	java/lang/Exception
        //   46	53	94	java/lang/Exception
        //   57	71	94	java/lang/Exception
        //   4	42	112	finally
        //   46	53	112	finally
        //   57	71	112	finally
        //   97	101	112	finally
        //   79	93	123	java/lang/Exception
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public TLRPC.Chat getChat(int paramInt)
  {
    try
    {
      Object localObject = new ArrayList();
      getChatsInternal("" + paramInt, (ArrayList)localObject);
      if (!((ArrayList)localObject).isEmpty())
      {
        localObject = (TLRPC.Chat)((ArrayList)localObject).get(0);
        return localObject;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return (TLRPC.Chat)null;
  }

  public TLRPC.Chat getChatSync(int paramInt)
  {
    Semaphore localSemaphore = new Semaphore(0);
    TLRPC.Chat[] arrayOfChat = new TLRPC.Chat[1];
    getInstance().getStorageQueue().postRunnable(new Runnable(arrayOfChat, paramInt, localSemaphore)
    {
      public void run()
      {
        this.val$chat[0] = MessagesStorage.this.getChat(this.val$user_id);
        this.val$semaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfChat[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void getChatsInternal(String paramString, ArrayList<TLRPC.Chat> paramArrayList)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null))
      return;
    paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM chats WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
    while (paramString.next())
      try
      {
        NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
        if (localNativeByteBuffer == null)
          continue;
        TLRPC.Chat localChat = TLRPC.Chat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        if (localChat == null)
          continue;
        paramArrayList.add(localChat);
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    paramString.dispose();
  }

  public void getContacts()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        StringBuilder localStringBuilder;
        boolean bool;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
          localStringBuilder = new StringBuilder();
          while (true)
          {
            if (!localSQLiteCursor.next())
              break label161;
            int i = localSQLiteCursor.intValue(0);
            TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
            localTL_contact.user_id = i;
            if (localSQLiteCursor.intValue(1) != 1)
              break;
            bool = true;
            localTL_contact.mutual = bool;
            if (localStringBuilder.length() != 0)
              localStringBuilder.append(",");
            localArrayList1.add(localTL_contact);
            localStringBuilder.append(localTL_contact.user_id);
          }
        }
        catch (Exception localException)
        {
          localArrayList1.clear();
          localArrayList2.clear();
          FileLog.e(localException);
        }
        while (true)
        {
          ContactsController.getInstance().processLoadedContacts(localArrayList1, localArrayList2, 1);
          return;
          bool = false;
          break;
          label161: localException.dispose();
          if (localStringBuilder.length() == 0)
            continue;
          MessagesStorage.this.getUsersInternal(localStringBuilder.toString(), localArrayList2);
        }
      }
    });
  }

  public SQLiteDatabase getDatabase()
  {
    return this.database;
  }

  public void getDialogPhotos(int paramInt1, int paramInt2, int paramInt3, long paramLong, int paramInt4)
  {
    this.storageQueue.postRunnable(new Runnable(paramLong, paramInt1, paramInt3, paramInt2, paramInt4)
    {
      public void run()
      {
        TLRPC.photos_Photos localphotos_Photos;
        SQLiteCursor localSQLiteCursor2;
        while (true)
        {
          try
          {
            if (this.val$max_id != 0L)
            {
              SQLiteCursor localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d AND id < %d ORDER BY id DESC LIMIT %d", new Object[] { Integer.valueOf(this.val$did), Long.valueOf(this.val$max_id), Integer.valueOf(this.val$count) }), new Object[0]);
              localphotos_Photos = new TLRPC.photos_Photos();
              if (!localSQLiteCursor1.next())
                break;
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor1.byteBufferValue(0);
              if (localNativeByteBuffer == null)
                continue;
              TLRPC.Photo localPhoto = TLRPC.Photo.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              localphotos_Photos.photos.add(localPhoto);
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          localSQLiteCursor2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM user_photos WHERE uid = %d ORDER BY id DESC LIMIT %d,%d", new Object[] { Integer.valueOf(this.val$did), Integer.valueOf(this.val$offset), Integer.valueOf(this.val$count) }), new Object[0]);
        }
        localSQLiteCursor2.dispose();
        Utilities.stageQueue.postRunnable(new Runnable(localphotos_Photos)
        {
          public void run()
          {
            MessagesController.getInstance().processLoadedUserPhotos(this.val$res, MessagesStorage.23.this.val$did, MessagesStorage.23.this.val$offset, MessagesStorage.23.this.val$count, MessagesStorage.23.this.val$max_id, true, MessagesStorage.23.this.val$classGuid);
          }
        });
      }
    });
  }

  public int getDialogReadMax(boolean paramBoolean, long paramLong)
  {
    Semaphore localSemaphore = new Semaphore(0);
    Integer[] arrayOfInteger = new Integer[1];
    arrayOfInteger[0] = Integer.valueOf(0);
    getInstance().getStorageQueue().postRunnable(new Runnable(paramBoolean, paramLong, arrayOfInteger, localSemaphore)
    {
      public void run()
      {
        Object localObject3 = null;
        SQLiteCursor localSQLiteCursor1 = null;
        SQLiteCursor localSQLiteCursor2 = localSQLiteCursor1;
        Object localObject2 = localObject3;
        try
        {
          if (this.val$outbox)
          {
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject2 = localObject3;
          }
          for (localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]); ; localSQLiteCursor1 = MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]))
          {
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject2 = localSQLiteCursor1;
            if (localSQLiteCursor1.next())
            {
              localSQLiteCursor2 = localSQLiteCursor1;
              localObject2 = localSQLiteCursor1;
              this.val$max[0] = Integer.valueOf(localSQLiteCursor1.intValue(0));
            }
            if (localSQLiteCursor1 != null)
              localSQLiteCursor1.dispose();
            this.val$semaphore.release();
            return;
            localSQLiteCursor2 = localSQLiteCursor1;
            localObject2 = localObject3;
          }
        }
        catch (Exception localException)
        {
          while (true)
          {
            localObject2 = localSQLiteCursor2;
            FileLog.e(localException);
            if (localSQLiteCursor2 == null)
              continue;
            localSQLiteCursor2.dispose();
          }
        }
        finally
        {
          if (localObject2 != null)
            ((SQLiteCursor)localObject2).dispose();
        }
        throw localObject1;
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfInteger[0].intValue();
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void getDialogs(int paramInt1, int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: new 33	org/vidogram/tgnet/TLRPC$messages_Dialogs
        //   3: dup
        //   4: invokespecial 34	org/vidogram/tgnet/TLRPC$messages_Dialogs:<init>	()V
        //   7: astore 8
        //   9: new 36	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 37	java/util/ArrayList:<init>	()V
        //   16: astore 9
        //   18: new 36	java/util/ArrayList
        //   21: dup
        //   22: invokespecial 37	java/util/ArrayList:<init>	()V
        //   25: astore 10
        //   27: aload 10
        //   29: invokestatic 43	org/vidogram/messenger/UserConfig:getClientUserId	()I
        //   32: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   35: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   38: pop
        //   39: new 36	java/util/ArrayList
        //   42: dup
        //   43: invokespecial 37	java/util/ArrayList:<init>	()V
        //   46: astore 11
        //   48: new 36	java/util/ArrayList
        //   51: dup
        //   52: invokespecial 37	java/util/ArrayList:<init>	()V
        //   55: astore 12
        //   57: new 36	java/util/ArrayList
        //   60: dup
        //   61: invokespecial 37	java/util/ArrayList:<init>	()V
        //   64: astore 14
        //   66: new 55	java/util/HashMap
        //   69: dup
        //   70: invokespecial 56	java/util/HashMap:<init>	()V
        //   73: astore 13
        //   75: aload_0
        //   76: getfield 20	org/vidogram/messenger/MessagesStorage$81:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   79: invokestatic 60	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   82: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   85: ldc 68
        //   87: iconst_2
        //   88: anewarray 4	java/lang/Object
        //   91: dup
        //   92: iconst_0
        //   93: aload_0
        //   94: getfield 22	org/vidogram/messenger/MessagesStorage$81:val$offset	I
        //   97: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   100: aastore
        //   101: dup
        //   102: iconst_1
        //   103: aload_0
        //   104: getfield 24	org/vidogram/messenger/MessagesStorage$81:val$count	I
        //   107: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   110: aastore
        //   111: invokestatic 74	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   114: iconst_0
        //   115: anewarray 4	java/lang/Object
        //   118: invokevirtual 80	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   121: astore 15
        //   123: aload 15
        //   125: invokevirtual 86	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   128: ifeq +744 -> 872
        //   131: new 88	org/vidogram/tgnet/TLRPC$TL_dialog
        //   134: dup
        //   135: invokespecial 89	org/vidogram/tgnet/TLRPC$TL_dialog:<init>	()V
        //   138: astore 16
        //   140: aload 16
        //   142: aload 15
        //   144: iconst_0
        //   145: invokevirtual 93	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   148: putfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   151: aload 16
        //   153: aload 15
        //   155: iconst_1
        //   156: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   159: putfield 104	org/vidogram/tgnet/TLRPC$TL_dialog:top_message	I
        //   162: aload 16
        //   164: aload 15
        //   166: iconst_2
        //   167: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   170: putfield 107	org/vidogram/tgnet/TLRPC$TL_dialog:unread_count	I
        //   173: aload 16
        //   175: aload 15
        //   177: iconst_3
        //   178: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   181: putfield 110	org/vidogram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   184: aload 16
        //   186: aload 15
        //   188: bipush 10
        //   190: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   193: putfield 113	org/vidogram/tgnet/TLRPC$TL_dialog:pts	I
        //   196: aload 16
        //   198: getfield 113	org/vidogram/tgnet/TLRPC$TL_dialog:pts	I
        //   201: ifeq +960 -> 1161
        //   204: aload 16
        //   206: getfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   209: l2i
        //   210: ifle +561 -> 771
        //   213: goto +948 -> 1161
        //   216: aload 16
        //   218: iload_1
        //   219: putfield 116	org/vidogram/tgnet/TLRPC$TL_dialog:flags	I
        //   222: aload 16
        //   224: aload 15
        //   226: bipush 11
        //   228: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   231: putfield 119	org/vidogram/tgnet/TLRPC$TL_dialog:read_inbox_max_id	I
        //   234: aload 16
        //   236: aload 15
        //   238: bipush 12
        //   240: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   243: putfield 122	org/vidogram/tgnet/TLRPC$TL_dialog:read_outbox_max_id	I
        //   246: aload 16
        //   248: aload 15
        //   250: bipush 14
        //   252: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   255: putfield 125	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
        //   258: aload 16
        //   260: getfield 125	org/vidogram/tgnet/TLRPC$TL_dialog:pinnedNum	I
        //   263: ifeq +513 -> 776
        //   266: iconst_1
        //   267: istore_3
        //   268: aload 16
        //   270: iload_3
        //   271: putfield 129	org/vidogram/tgnet/TLRPC$TL_dialog:pinned	Z
        //   274: aload 15
        //   276: bipush 8
        //   278: invokevirtual 93	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   281: lstore 4
        //   283: lload 4
        //   285: l2i
        //   286: istore_1
        //   287: aload 16
        //   289: new 131	org/vidogram/tgnet/TLRPC$TL_peerNotifySettings
        //   292: dup
        //   293: invokespecial 132	org/vidogram/tgnet/TLRPC$TL_peerNotifySettings:<init>	()V
        //   296: putfield 136	org/vidogram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/vidogram/tgnet/TLRPC$PeerNotifySettings;
        //   299: iload_1
        //   300: iconst_1
        //   301: iand
        //   302: ifeq +38 -> 340
        //   305: aload 16
        //   307: getfield 136	org/vidogram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/vidogram/tgnet/TLRPC$PeerNotifySettings;
        //   310: lload 4
        //   312: bipush 32
        //   314: lshr
        //   315: l2i
        //   316: putfield 141	org/vidogram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   319: aload 16
        //   321: getfield 136	org/vidogram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/vidogram/tgnet/TLRPC$PeerNotifySettings;
        //   324: getfield 141	org/vidogram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   327: ifne +13 -> 340
        //   330: aload 16
        //   332: getfield 136	org/vidogram/tgnet/TLRPC$TL_dialog:notify_settings	Lorg/vidogram/tgnet/TLRPC$PeerNotifySettings;
        //   335: ldc 142
        //   337: putfield 141	org/vidogram/tgnet/TLRPC$PeerNotifySettings:mute_until	I
        //   340: aload 8
        //   342: getfield 146	org/vidogram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   345: aload 16
        //   347: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   350: pop
        //   351: aload 15
        //   353: iconst_4
        //   354: invokevirtual 150	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   357: astore 18
        //   359: aload 18
        //   361: ifnull +305 -> 666
        //   364: aload 18
        //   366: aload 18
        //   368: iconst_0
        //   369: invokevirtual 156	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   372: iconst_0
        //   373: invokestatic 162	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   376: astore 17
        //   378: aload 18
        //   380: invokevirtual 165	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   383: aload 17
        //   385: ifnull +281 -> 666
        //   388: aload 17
        //   390: aload 15
        //   392: iconst_5
        //   393: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   396: invokestatic 171	org/vidogram/messenger/MessageObject:setUnreadFlags	(Lorg/vidogram/tgnet/TLRPC$Message;I)V
        //   399: aload 17
        //   401: aload 15
        //   403: bipush 6
        //   405: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   408: putfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   411: aload 15
        //   413: bipush 9
        //   415: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   418: istore_1
        //   419: iload_1
        //   420: ifeq +9 -> 429
        //   423: aload 16
        //   425: iload_1
        //   426: putfield 110	org/vidogram/tgnet/TLRPC$TL_dialog:last_message_date	I
        //   429: aload 17
        //   431: aload 15
        //   433: bipush 7
        //   435: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   438: putfield 176	org/vidogram/tgnet/TLRPC$Message:send_state	I
        //   441: aload 17
        //   443: aload 16
        //   445: getfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   448: putfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   451: aload 8
        //   453: getfield 182	org/vidogram/tgnet/TLRPC$messages_Dialogs:messages	Ljava/util/ArrayList;
        //   456: aload 17
        //   458: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   461: pop
        //   462: aload 17
        //   464: aload 10
        //   466: aload 11
        //   468: invokestatic 186	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   471: aload 17
        //   473: getfield 189	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   476: ifeq +190 -> 666
        //   479: aload 17
        //   481: getfield 193	org/vidogram/tgnet/TLRPC$Message:action	Lorg/vidogram/tgnet/TLRPC$MessageAction;
        //   484: instanceof 195
        //   487: ifne +25 -> 512
        //   490: aload 17
        //   492: getfield 193	org/vidogram/tgnet/TLRPC$Message:action	Lorg/vidogram/tgnet/TLRPC$MessageAction;
        //   495: instanceof 197
        //   498: ifne +14 -> 512
        //   501: aload 17
        //   503: getfield 193	org/vidogram/tgnet/TLRPC$Message:action	Lorg/vidogram/tgnet/TLRPC$MessageAction;
        //   506: instanceof 199
        //   509: ifeq +157 -> 666
        //   512: aload 15
        //   514: bipush 13
        //   516: invokevirtual 203	org/vidogram/SQLite/SQLiteCursor:isNull	(I)Z
        //   519: ifne +59 -> 578
        //   522: aload 15
        //   524: bipush 13
        //   526: invokevirtual 150	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   529: astore 18
        //   531: aload 18
        //   533: ifnull +45 -> 578
        //   536: aload 17
        //   538: aload 18
        //   540: aload 18
        //   542: iconst_0
        //   543: invokevirtual 156	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   546: iconst_0
        //   547: invokestatic 162	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   550: putfield 207	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   553: aload 18
        //   555: invokevirtual 165	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   558: aload 17
        //   560: getfield 207	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   563: ifnull +15 -> 578
        //   566: aload 17
        //   568: getfield 207	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   571: aload 10
        //   573: aload 11
        //   575: invokestatic 186	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   578: aload 17
        //   580: getfield 207	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   583: ifnonnull +83 -> 666
        //   586: aload 17
        //   588: getfield 189	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   591: i2l
        //   592: lstore 6
        //   594: lload 6
        //   596: lstore 4
        //   598: aload 17
        //   600: getfield 211	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   603: getfield 216	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
        //   606: ifeq +20 -> 626
        //   609: lload 6
        //   611: aload 17
        //   613: getfield 211	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   616: getfield 216	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
        //   619: i2l
        //   620: bipush 32
        //   622: lshl
        //   623: lor
        //   624: lstore 4
        //   626: aload 14
        //   628: lload 4
        //   630: invokestatic 221	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   633: invokevirtual 224	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   636: ifne +14 -> 650
        //   639: aload 14
        //   641: lload 4
        //   643: invokestatic 221	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   646: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   649: pop
        //   650: aload 13
        //   652: aload 16
        //   654: getfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   657: invokestatic 221	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   660: aload 17
        //   662: invokevirtual 228	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   665: pop
        //   666: aload 16
        //   668: getfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   671: l2i
        //   672: istore_1
        //   673: aload 16
        //   675: getfield 97	org/vidogram/tgnet/TLRPC$TL_dialog:id	J
        //   678: bipush 32
        //   680: lshr
        //   681: l2i
        //   682: istore_2
        //   683: iload_1
        //   684: ifeq +163 -> 847
        //   687: iload_2
        //   688: iconst_1
        //   689: if_icmpne +102 -> 791
        //   692: aload 11
        //   694: iload_1
        //   695: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   698: invokevirtual 224	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   701: ifne -578 -> 123
        //   704: aload 11
        //   706: iload_1
        //   707: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   710: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   713: pop
        //   714: goto -591 -> 123
        //   717: astore 10
        //   719: aload 8
        //   721: getfield 146	org/vidogram/tgnet/TLRPC$messages_Dialogs:dialogs	Ljava/util/ArrayList;
        //   724: invokevirtual 231	java/util/ArrayList:clear	()V
        //   727: aload 8
        //   729: getfield 234	org/vidogram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   732: invokevirtual 231	java/util/ArrayList:clear	()V
        //   735: aload 8
        //   737: getfield 237	org/vidogram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   740: invokevirtual 231	java/util/ArrayList:clear	()V
        //   743: aload 9
        //   745: invokevirtual 231	java/util/ArrayList:clear	()V
        //   748: aload 10
        //   750: invokestatic 243	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   753: invokestatic 249	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   756: aload 8
        //   758: aload 9
        //   760: iconst_0
        //   761: bipush 100
        //   763: iconst_1
        //   764: iconst_1
        //   765: iconst_0
        //   766: iconst_1
        //   767: invokevirtual 253	org/vidogram/messenger/MessagesController:processLoadedDialogs	(Lorg/vidogram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZZ)V
        //   770: return
        //   771: iconst_1
        //   772: istore_1
        //   773: goto -557 -> 216
        //   776: iconst_0
        //   777: istore_3
        //   778: goto -510 -> 268
        //   781: astore 17
        //   783: aload 17
        //   785: invokestatic 243	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   788: goto -122 -> 666
        //   791: iload_1
        //   792: ifle +28 -> 820
        //   795: aload 10
        //   797: iload_1
        //   798: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   801: invokevirtual 224	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   804: ifne -681 -> 123
        //   807: aload 10
        //   809: iload_1
        //   810: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   813: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   816: pop
        //   817: goto -694 -> 123
        //   820: aload 11
        //   822: iload_1
        //   823: ineg
        //   824: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   827: invokevirtual 224	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   830: ifne -707 -> 123
        //   833: aload 11
        //   835: iload_1
        //   836: ineg
        //   837: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   840: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   843: pop
        //   844: goto -721 -> 123
        //   847: aload 12
        //   849: iload_2
        //   850: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   853: invokevirtual 224	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   856: ifne -733 -> 123
        //   859: aload 12
        //   861: iload_2
        //   862: invokestatic 49	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   865: invokevirtual 53	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   868: pop
        //   869: goto -746 -> 123
        //   872: aload 15
        //   874: invokevirtual 256	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   877: aload 14
        //   879: invokevirtual 259	java/util/ArrayList:isEmpty	()Z
        //   882: ifne +173 -> 1055
        //   885: aload_0
        //   886: getfield 20	org/vidogram/messenger/MessagesStorage$81:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   889: invokestatic 60	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   892: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   895: ldc_w 261
        //   898: iconst_1
        //   899: anewarray 4	java/lang/Object
        //   902: dup
        //   903: iconst_0
        //   904: ldc_w 263
        //   907: aload 14
        //   909: invokestatic 269	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   912: aastore
        //   913: invokestatic 74	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   916: iconst_0
        //   917: anewarray 4	java/lang/Object
        //   920: invokevirtual 80	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   923: astore 14
        //   925: aload 14
        //   927: invokevirtual 86	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   930: ifeq +120 -> 1050
        //   933: aload 14
        //   935: iconst_0
        //   936: invokevirtual 150	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   939: astore 16
        //   941: aload 16
        //   943: ifnull -18 -> 925
        //   946: aload 16
        //   948: aload 16
        //   950: iconst_0
        //   951: invokevirtual 156	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   954: iconst_0
        //   955: invokestatic 162	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   958: astore 15
        //   960: aload 16
        //   962: invokevirtual 165	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   965: aload 15
        //   967: aload 14
        //   969: iconst_1
        //   970: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   973: putfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   976: aload 15
        //   978: aload 14
        //   980: iconst_2
        //   981: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   984: putfield 272	org/vidogram/tgnet/TLRPC$Message:date	I
        //   987: aload 15
        //   989: aload 14
        //   991: iconst_3
        //   992: invokevirtual 93	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   995: putfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   998: aload 15
        //   1000: aload 10
        //   1002: aload 11
        //   1004: invokestatic 186	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   1007: aload 13
        //   1009: aload 15
        //   1011: getfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   1014: invokestatic 221	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   1017: invokevirtual 276	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   1020: checkcast 158	org/vidogram/tgnet/TLRPC$Message
        //   1023: astore 16
        //   1025: aload 16
        //   1027: ifnull -102 -> 925
        //   1030: aload 16
        //   1032: aload 15
        //   1034: putfield 207	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   1037: aload 15
        //   1039: aload 16
        //   1041: getfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   1044: putfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   1047: goto -122 -> 925
        //   1050: aload 14
        //   1052: invokevirtual 256	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   1055: aload 12
        //   1057: invokevirtual 259	java/util/ArrayList:isEmpty	()Z
        //   1060: ifne +22 -> 1082
        //   1063: aload_0
        //   1064: getfield 20	org/vidogram/messenger/MessagesStorage$81:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1067: ldc_w 263
        //   1070: aload 12
        //   1072: invokestatic 269	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1075: aload 9
        //   1077: aload 10
        //   1079: invokevirtual 280	org/vidogram/messenger/MessagesStorage:getEncryptedChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   1082: aload 11
        //   1084: invokevirtual 259	java/util/ArrayList:isEmpty	()Z
        //   1087: ifne +23 -> 1110
        //   1090: aload_0
        //   1091: getfield 20	org/vidogram/messenger/MessagesStorage$81:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1094: ldc_w 263
        //   1097: aload 11
        //   1099: invokestatic 269	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1102: aload 8
        //   1104: getfield 237	org/vidogram/tgnet/TLRPC$messages_Dialogs:chats	Ljava/util/ArrayList;
        //   1107: invokevirtual 284	org/vidogram/messenger/MessagesStorage:getChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1110: aload 10
        //   1112: invokevirtual 259	java/util/ArrayList:isEmpty	()Z
        //   1115: ifne +23 -> 1138
        //   1118: aload_0
        //   1119: getfield 20	org/vidogram/messenger/MessagesStorage$81:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1122: ldc_w 263
        //   1125: aload 10
        //   1127: invokestatic 269	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1130: aload 8
        //   1132: getfield 234	org/vidogram/tgnet/TLRPC$messages_Dialogs:users	Ljava/util/ArrayList;
        //   1135: invokevirtual 287	org/vidogram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1138: invokestatic 249	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   1141: aload 8
        //   1143: aload 9
        //   1145: aload_0
        //   1146: getfield 22	org/vidogram/messenger/MessagesStorage$81:val$offset	I
        //   1149: aload_0
        //   1150: getfield 24	org/vidogram/messenger/MessagesStorage$81:val$count	I
        //   1153: iconst_1
        //   1154: iconst_0
        //   1155: iconst_0
        //   1156: iconst_1
        //   1157: invokevirtual 253	org/vidogram/messenger/MessagesController:processLoadedDialogs	(Lorg/vidogram/tgnet/TLRPC$messages_Dialogs;Ljava/util/ArrayList;IIIZZZ)V
        //   1160: return
        //   1161: iconst_0
        //   1162: istore_1
        //   1163: goto -947 -> 216
        //
        // Exception table:
        //   from	to	target	type
        //   18	123	717	java/lang/Exception
        //   123	213	717	java/lang/Exception
        //   216	266	717	java/lang/Exception
        //   268	283	717	java/lang/Exception
        //   287	299	717	java/lang/Exception
        //   305	340	717	java/lang/Exception
        //   340	359	717	java/lang/Exception
        //   364	383	717	java/lang/Exception
        //   388	419	717	java/lang/Exception
        //   423	429	717	java/lang/Exception
        //   429	471	717	java/lang/Exception
        //   666	683	717	java/lang/Exception
        //   692	714	717	java/lang/Exception
        //   783	788	717	java/lang/Exception
        //   795	817	717	java/lang/Exception
        //   820	844	717	java/lang/Exception
        //   847	869	717	java/lang/Exception
        //   872	925	717	java/lang/Exception
        //   925	941	717	java/lang/Exception
        //   946	1025	717	java/lang/Exception
        //   1030	1047	717	java/lang/Exception
        //   1050	1055	717	java/lang/Exception
        //   1055	1082	717	java/lang/Exception
        //   1082	1110	717	java/lang/Exception
        //   1110	1138	717	java/lang/Exception
        //   1138	1160	717	java/lang/Exception
        //   471	512	781	java/lang/Exception
        //   512	531	781	java/lang/Exception
        //   536	578	781	java/lang/Exception
        //   578	594	781	java/lang/Exception
        //   598	626	781	java/lang/Exception
        //   626	650	781	java/lang/Exception
        //   650	666	781	java/lang/Exception
      }
    });
  }

  public void getDownloadQueue(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        SQLiteCursor localSQLiteCursor;
        while (true)
        {
          DownloadObject localDownloadObject;
          TLRPC.MessageMedia localMessageMedia;
          try
          {
            ArrayList localArrayList = new ArrayList();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT uid, type, data FROM download_queue WHERE type = %d ORDER BY date DESC LIMIT 3", new Object[] { Integer.valueOf(this.val$type) }), new Object[0]);
            if (!localSQLiteCursor.next())
              break;
            localDownloadObject = new DownloadObject();
            localDownloadObject.type = localSQLiteCursor.intValue(1);
            localDownloadObject.id = localSQLiteCursor.longValue(0);
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(2);
            if (localNativeByteBuffer == null)
              continue;
            localMessageMedia = TLRPC.MessageMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (localMessageMedia.document != null)
            {
              localDownloadObject.object = localMessageMedia.document;
              localArrayList.add(localDownloadObject);
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          if (localMessageMedia.photo == null)
            continue;
          localDownloadObject.object = FileLoader.getClosestPhotoSizeWithSize(localMessageMedia.photo.sizes, AndroidUtilities.getPhotoSize());
        }
        localSQLiteCursor.dispose();
        AndroidUtilities.runOnUIThread(new Runnable(localException)
        {
          public void run()
          {
            MediaController.getInstance().processDownloadObjects(MessagesStorage.65.this.val$type, this.val$objects);
          }
        });
      }
    });
  }

  public TLRPC.EncryptedChat getEncryptedChat(int paramInt)
  {
    try
    {
      Object localObject = new ArrayList();
      getEncryptedChatsInternal("" + paramInt, (ArrayList)localObject, null);
      if (!((ArrayList)localObject).isEmpty())
      {
        localObject = (TLRPC.EncryptedChat)((ArrayList)localObject).get(0);
        return localObject;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return (TLRPC.EncryptedChat)null;
  }

  public void getEncryptedChat(int paramInt, Semaphore paramSemaphore, ArrayList<TLObject> paramArrayList)
  {
    if ((paramSemaphore == null) || (paramArrayList == null))
      return;
    this.storageQueue.postRunnable(new Runnable(paramInt, paramArrayList, paramSemaphore)
    {
      public void run()
      {
        try
        {
          ArrayList localArrayList1 = new ArrayList();
          ArrayList localArrayList2 = new ArrayList();
          MessagesStorage.this.getEncryptedChatsInternal("" + this.val$chat_id, localArrayList2, localArrayList1);
          if ((!localArrayList2.isEmpty()) && (!localArrayList1.isEmpty()))
          {
            ArrayList localArrayList3 = new ArrayList();
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localArrayList3);
            if (!localArrayList3.isEmpty())
            {
              this.val$result.add(localArrayList2.get(0));
              this.val$result.add(localArrayList3.get(0));
            }
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          this.val$semaphore.release();
        }
        throw localObject;
      }
    });
  }

  public void getEncryptedChatsInternal(String paramString, ArrayList<TLRPC.EncryptedChat> paramArrayList, ArrayList<Integer> paramArrayList1)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null))
      return;
    paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data, user, g, authkey, ttl, layer, seq_in, seq_out, use_count, exchange_id, key_date, fprint, fauthkey, khash, in_seq_no, admin_id FROM enc_chats WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
    while (paramString.next())
      try
      {
        NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
        if (localNativeByteBuffer == null)
          continue;
        TLRPC.EncryptedChat localEncryptedChat = TLRPC.EncryptedChat.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        if (localEncryptedChat == null)
          continue;
        localEncryptedChat.user_id = paramString.intValue(1);
        if ((paramArrayList1 != null) && (!paramArrayList1.contains(Integer.valueOf(localEncryptedChat.user_id))))
          paramArrayList1.add(Integer.valueOf(localEncryptedChat.user_id));
        localEncryptedChat.a_or_b = paramString.byteArrayValue(2);
        localEncryptedChat.auth_key = paramString.byteArrayValue(3);
        localEncryptedChat.ttl = paramString.intValue(4);
        localEncryptedChat.layer = paramString.intValue(5);
        localEncryptedChat.seq_in = paramString.intValue(6);
        localEncryptedChat.seq_out = paramString.intValue(7);
        int i = paramString.intValue(8);
        localEncryptedChat.key_use_count_in = (short)(i >> 16);
        localEncryptedChat.key_use_count_out = (short)i;
        localEncryptedChat.exchange_id = paramString.longValue(9);
        localEncryptedChat.key_create_date = paramString.intValue(10);
        localEncryptedChat.future_key_fingerprint = paramString.longValue(11);
        localEncryptedChat.future_auth_key = paramString.byteArrayValue(12);
        localEncryptedChat.key_hash = paramString.byteArrayValue(13);
        localEncryptedChat.in_seq_no = paramString.intValue(14);
        i = paramString.intValue(15);
        if (i != 0)
          localEncryptedChat.admin_id = i;
        paramArrayList.add(localEncryptedChat);
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    paramString.dispose();
  }

  public void getMessages(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, boolean paramBoolean, int paramInt7)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2, paramBoolean, paramLong, paramInt6, paramInt4, paramInt3, paramInt5, paramInt7)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: new 58	org/vidogram/tgnet/TLRPC$TL_messages_messages
        //   3: dup
        //   4: invokespecial 59	org/vidogram/tgnet/TLRPC$TL_messages_messages:<init>	()V
        //   7: astore 82
        //   9: iconst_0
        //   10: istore 35
        //   12: iconst_0
        //   13: istore 38
        //   15: iconst_0
        //   16: istore 39
        //   18: iconst_0
        //   19: istore 29
        //   21: iconst_0
        //   22: istore 36
        //   24: iconst_0
        //   25: istore 30
        //   27: iconst_0
        //   28: istore 37
        //   30: iconst_0
        //   31: istore 11
        //   33: iconst_0
        //   34: istore 13
        //   36: iconst_0
        //   37: istore 46
        //   39: aload_0
        //   40: getfield 33	org/vidogram/messenger/MessagesStorage$49:val$count	I
        //   43: istore 4
        //   45: iconst_0
        //   46: istore 24
        //   48: iconst_0
        //   49: istore 16
        //   51: iconst_0
        //   52: istore 44
        //   54: iconst_0
        //   55: istore 31
        //   57: iconst_0
        //   58: istore 32
        //   60: iconst_0
        //   61: istore 42
        //   63: iconst_0
        //   64: istore 41
        //   66: iconst_0
        //   67: istore 47
        //   69: iconst_0
        //   70: istore 33
        //   72: iconst_0
        //   73: istore 19
        //   75: iconst_0
        //   76: istore 26
        //   78: iconst_0
        //   79: istore 20
        //   81: iconst_0
        //   82: istore 23
        //   84: iconst_0
        //   85: istore 17
        //   87: iconst_0
        //   88: istore 18
        //   90: iconst_0
        //   91: istore 25
        //   93: iconst_0
        //   94: istore 60
        //   96: iconst_0
        //   97: istore 67
        //   99: iconst_0
        //   100: istore 61
        //   102: iconst_0
        //   103: istore 58
        //   105: iconst_0
        //   106: istore 62
        //   108: iconst_0
        //   109: istore 59
        //   111: iconst_0
        //   112: istore 68
        //   114: iconst_0
        //   115: istore 49
        //   117: iconst_0
        //   118: istore 69
        //   120: iconst_0
        //   121: istore 54
        //   123: iconst_0
        //   124: istore 70
        //   126: iconst_0
        //   127: istore 34
        //   129: iconst_0
        //   130: istore 40
        //   132: iconst_0
        //   133: istore 45
        //   135: iconst_0
        //   136: istore 27
        //   138: iconst_0
        //   139: istore 28
        //   141: iconst_0
        //   142: istore 43
        //   144: iconst_0
        //   145: istore 10
        //   147: iconst_0
        //   148: istore 12
        //   150: aload_0
        //   151: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   154: i2l
        //   155: lstore 71
        //   157: aload_0
        //   158: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   161: istore 9
        //   163: aload_0
        //   164: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   167: istore_1
        //   168: aload_0
        //   169: getfield 37	org/vidogram/messenger/MessagesStorage$49:val$isChannel	Z
        //   172: ifeq +19868 -> 20040
        //   175: aload_0
        //   176: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   179: l2i
        //   180: ineg
        //   181: istore 21
        //   183: lload 71
        //   185: lstore 73
        //   187: lload 71
        //   189: lconst_0
        //   190: lcmp
        //   191: ifeq +23 -> 214
        //   194: lload 71
        //   196: lstore 73
        //   198: iload 21
        //   200: ifeq +14 -> 214
        //   203: lload 71
        //   205: iload 21
        //   207: i2l
        //   208: bipush 32
        //   210: lshl
        //   211: lor
        //   212: lstore 73
        //   214: iconst_0
        //   215: istore 63
        //   217: iconst_0
        //   218: istore 64
        //   220: iconst_0
        //   221: istore 65
        //   223: iconst_0
        //   224: istore 56
        //   226: iconst_0
        //   227: istore 66
        //   229: iconst_0
        //   230: istore 57
        //   232: iconst_0
        //   233: istore 55
        //   235: aload_0
        //   236: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   239: ldc2_w 60
        //   242: lcmp
        //   243: ifne +6828 -> 7071
        //   246: bipush 10
        //   248: istore 22
        //   250: iload 31
        //   252: istore_2
        //   253: iload 20
        //   255: istore 14
        //   257: iload 29
        //   259: istore 5
        //   261: iload 27
        //   263: istore 7
        //   265: iload 65
        //   267: istore 52
        //   269: iload 58
        //   271: istore 50
        //   273: iload 32
        //   275: istore_3
        //   276: iload 23
        //   278: istore 15
        //   280: iload 30
        //   282: istore 6
        //   284: iload 28
        //   286: istore 8
        //   288: iload 66
        //   290: istore 53
        //   292: iload 59
        //   294: istore 51
        //   296: new 63	java/util/ArrayList
        //   299: dup
        //   300: invokespecial 64	java/util/ArrayList:<init>	()V
        //   303: astore 83
        //   305: iload 31
        //   307: istore_2
        //   308: iload 20
        //   310: istore 14
        //   312: iload 29
        //   314: istore 5
        //   316: iload 27
        //   318: istore 7
        //   320: iload 65
        //   322: istore 52
        //   324: iload 58
        //   326: istore 50
        //   328: iload 32
        //   330: istore_3
        //   331: iload 23
        //   333: istore 15
        //   335: iload 30
        //   337: istore 6
        //   339: iload 28
        //   341: istore 8
        //   343: iload 66
        //   345: istore 53
        //   347: iload 59
        //   349: istore 51
        //   351: new 63	java/util/ArrayList
        //   354: dup
        //   355: invokespecial 64	java/util/ArrayList:<init>	()V
        //   358: astore 84
        //   360: iload 31
        //   362: istore_2
        //   363: iload 20
        //   365: istore 14
        //   367: iload 29
        //   369: istore 5
        //   371: iload 27
        //   373: istore 7
        //   375: iload 65
        //   377: istore 52
        //   379: iload 58
        //   381: istore 50
        //   383: iload 32
        //   385: istore_3
        //   386: iload 23
        //   388: istore 15
        //   390: iload 30
        //   392: istore 6
        //   394: iload 28
        //   396: istore 8
        //   398: iload 66
        //   400: istore 53
        //   402: iload 59
        //   404: istore 51
        //   406: new 63	java/util/ArrayList
        //   409: dup
        //   410: invokespecial 64	java/util/ArrayList:<init>	()V
        //   413: astore 87
        //   415: iload 31
        //   417: istore_2
        //   418: iload 20
        //   420: istore 14
        //   422: iload 29
        //   424: istore 5
        //   426: iload 27
        //   428: istore 7
        //   430: iload 65
        //   432: istore 52
        //   434: iload 58
        //   436: istore 50
        //   438: iload 32
        //   440: istore_3
        //   441: iload 23
        //   443: istore 15
        //   445: iload 30
        //   447: istore 6
        //   449: iload 28
        //   451: istore 8
        //   453: iload 66
        //   455: istore 53
        //   457: iload 59
        //   459: istore 51
        //   461: new 66	java/util/HashMap
        //   464: dup
        //   465: invokespecial 67	java/util/HashMap:<init>	()V
        //   468: astore 85
        //   470: iload 31
        //   472: istore_2
        //   473: iload 20
        //   475: istore 14
        //   477: iload 29
        //   479: istore 5
        //   481: iload 27
        //   483: istore 7
        //   485: iload 65
        //   487: istore 52
        //   489: iload 58
        //   491: istore 50
        //   493: iload 32
        //   495: istore_3
        //   496: iload 23
        //   498: istore 15
        //   500: iload 30
        //   502: istore 6
        //   504: iload 28
        //   506: istore 8
        //   508: iload 66
        //   510: istore 53
        //   512: iload 59
        //   514: istore 51
        //   516: new 66	java/util/HashMap
        //   519: dup
        //   520: invokespecial 67	java/util/HashMap:<init>	()V
        //   523: astore 86
        //   525: iload 31
        //   527: istore_2
        //   528: iload 20
        //   530: istore 14
        //   532: iload 29
        //   534: istore 5
        //   536: iload 27
        //   538: istore 7
        //   540: iload 65
        //   542: istore 52
        //   544: iload 58
        //   546: istore 50
        //   548: iload 32
        //   550: istore_3
        //   551: iload 23
        //   553: istore 15
        //   555: iload 30
        //   557: istore 6
        //   559: iload 28
        //   561: istore 8
        //   563: iload 66
        //   565: istore 53
        //   567: iload 59
        //   569: istore 51
        //   571: aload_0
        //   572: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   575: l2i
        //   576: istore 48
        //   578: iload 48
        //   580: ifeq +12014 -> 12594
        //   583: iload 31
        //   585: istore_2
        //   586: iload 20
        //   588: istore 14
        //   590: iload 29
        //   592: istore 5
        //   594: iload 27
        //   596: istore 7
        //   598: iload 65
        //   600: istore 52
        //   602: iload 58
        //   604: istore 50
        //   606: iload 32
        //   608: istore_3
        //   609: iload 23
        //   611: istore 15
        //   613: iload 30
        //   615: istore 6
        //   617: iload 28
        //   619: istore 8
        //   621: iload 66
        //   623: istore 53
        //   625: iload 59
        //   627: istore 51
        //   629: aload_0
        //   630: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   633: iconst_3
        //   634: if_icmpne +6443 -> 7077
        //   637: iload 31
        //   639: istore_2
        //   640: iload 20
        //   642: istore 14
        //   644: iload 29
        //   646: istore 5
        //   648: iload 27
        //   650: istore 7
        //   652: iload 65
        //   654: istore 52
        //   656: iload 58
        //   658: istore 50
        //   660: iload 32
        //   662: istore_3
        //   663: iload 23
        //   665: istore 15
        //   667: iload 30
        //   669: istore 6
        //   671: iload 28
        //   673: istore 8
        //   675: iload 66
        //   677: istore 53
        //   679: iload 59
        //   681: istore 51
        //   683: aload_0
        //   684: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   687: ifne +6390 -> 7077
        //   690: iload 31
        //   692: istore_2
        //   693: iload 20
        //   695: istore 14
        //   697: iload 29
        //   699: istore 5
        //   701: iload 27
        //   703: istore 7
        //   705: iload 65
        //   707: istore 52
        //   709: iload 58
        //   711: istore 50
        //   713: iload 32
        //   715: istore_3
        //   716: iload 23
        //   718: istore 15
        //   720: iload 30
        //   722: istore 6
        //   724: iload 28
        //   726: istore 8
        //   728: iload 66
        //   730: istore 53
        //   732: iload 59
        //   734: istore 51
        //   736: aload_0
        //   737: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   740: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   743: new 73	java/lang/StringBuilder
        //   746: dup
        //   747: invokespecial 74	java/lang/StringBuilder:<init>	()V
        //   750: ldc 76
        //   752: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   755: aload_0
        //   756: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   759: invokevirtual 83	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   762: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   765: iconst_0
        //   766: anewarray 4	java/lang/Object
        //   769: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   772: astore 79
        //   774: iload 47
        //   776: istore 10
        //   778: iload 46
        //   780: istore 11
        //   782: iload 31
        //   784: istore_2
        //   785: iload 20
        //   787: istore 14
        //   789: iload 29
        //   791: istore 5
        //   793: iload 27
        //   795: istore 7
        //   797: iload 65
        //   799: istore 52
        //   801: iload 58
        //   803: istore 50
        //   805: iload 32
        //   807: istore_3
        //   808: iload 23
        //   810: istore 15
        //   812: iload 30
        //   814: istore 6
        //   816: iload 28
        //   818: istore 8
        //   820: iload 66
        //   822: istore 53
        //   824: iload 59
        //   826: istore 51
        //   828: aload 79
        //   830: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   833: ifeq +167 -> 1000
        //   836: iload 31
        //   838: istore_2
        //   839: iload 20
        //   841: istore 14
        //   843: iload 29
        //   845: istore 5
        //   847: iload 27
        //   849: istore 7
        //   851: iload 65
        //   853: istore 52
        //   855: iload 58
        //   857: istore 50
        //   859: iload 32
        //   861: istore_3
        //   862: iload 23
        //   864: istore 15
        //   866: iload 30
        //   868: istore 6
        //   870: iload 28
        //   872: istore 8
        //   874: iload 66
        //   876: istore 53
        //   878: iload 59
        //   880: istore 51
        //   882: aload 79
        //   884: iconst_0
        //   885: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   888: iconst_1
        //   889: iadd
        //   890: istore 10
        //   892: iload 10
        //   894: istore_2
        //   895: iload 20
        //   897: istore 14
        //   899: iload 29
        //   901: istore 5
        //   903: iload 27
        //   905: istore 7
        //   907: iload 65
        //   909: istore 52
        //   911: iload 58
        //   913: istore 50
        //   915: iload 10
        //   917: istore_3
        //   918: iload 23
        //   920: istore 15
        //   922: iload 30
        //   924: istore 6
        //   926: iload 28
        //   928: istore 8
        //   930: iload 66
        //   932: istore 53
        //   934: iload 59
        //   936: istore 51
        //   938: aload 79
        //   940: iconst_1
        //   941: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   944: istore 11
        //   946: iload 10
        //   948: istore_2
        //   949: iload 20
        //   951: istore 14
        //   953: iload 11
        //   955: istore 5
        //   957: iload 27
        //   959: istore 7
        //   961: iload 65
        //   963: istore 52
        //   965: iload 58
        //   967: istore 50
        //   969: iload 10
        //   971: istore_3
        //   972: iload 23
        //   974: istore 15
        //   976: iload 11
        //   978: istore 6
        //   980: iload 28
        //   982: istore 8
        //   984: iload 66
        //   986: istore 53
        //   988: iload 59
        //   990: istore 51
        //   992: aload 79
        //   994: iconst_2
        //   995: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   998: istore 12
        //   1000: iload 10
        //   1002: istore_2
        //   1003: iload 20
        //   1005: istore 14
        //   1007: iload 11
        //   1009: istore 5
        //   1011: iload 12
        //   1013: istore 7
        //   1015: iload 65
        //   1017: istore 52
        //   1019: iload 58
        //   1021: istore 50
        //   1023: iload 10
        //   1025: istore_3
        //   1026: iload 23
        //   1028: istore 15
        //   1030: iload 11
        //   1032: istore 6
        //   1034: iload 12
        //   1036: istore 8
        //   1038: iload 66
        //   1040: istore 53
        //   1042: iload 59
        //   1044: istore 51
        //   1046: aload 79
        //   1048: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   1051: lload 73
        //   1053: lstore 71
        //   1055: iload 4
        //   1057: istore_3
        //   1058: iload 9
        //   1060: istore_2
        //   1061: iload 70
        //   1063: istore 49
        //   1065: iload 12
        //   1067: istore 6
        //   1069: iload 11
        //   1071: istore 5
        //   1073: iload 10
        //   1075: istore 4
        //   1077: iload 24
        //   1079: istore 9
        //   1081: iconst_0
        //   1082: istore 23
        //   1084: iconst_0
        //   1085: istore 13
        //   1087: iconst_0
        //   1088: istore 7
        //   1090: iconst_0
        //   1091: istore 20
        //   1093: iconst_0
        //   1094: istore 22
        //   1096: iload_3
        //   1097: istore 18
        //   1099: iload 4
        //   1101: istore 15
        //   1103: iload 13
        //   1105: istore 8
        //   1107: iload 5
        //   1109: istore 14
        //   1111: iload 6
        //   1113: istore 11
        //   1115: iload 56
        //   1117: istore 51
        //   1119: iload 49
        //   1121: istore 53
        //   1123: iload_3
        //   1124: istore 19
        //   1126: iload 4
        //   1128: istore 17
        //   1130: iload 20
        //   1132: istore 10
        //   1134: iload 5
        //   1136: istore 16
        //   1138: iload 6
        //   1140: istore 12
        //   1142: iload 57
        //   1144: istore 52
        //   1146: iload 49
        //   1148: istore 54
        //   1150: aload_0
        //   1151: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1154: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   1157: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   1160: ldc 114
        //   1162: iconst_1
        //   1163: anewarray 4	java/lang/Object
        //   1166: dup
        //   1167: iconst_0
        //   1168: aload_0
        //   1169: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   1172: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   1175: aastore
        //   1176: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   1179: iconst_0
        //   1180: anewarray 4	java/lang/Object
        //   1183: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   1186: astore 79
        //   1188: iload_3
        //   1189: istore 18
        //   1191: iload 4
        //   1193: istore 15
        //   1195: iload 13
        //   1197: istore 8
        //   1199: iload 5
        //   1201: istore 14
        //   1203: iload 6
        //   1205: istore 11
        //   1207: iload 56
        //   1209: istore 51
        //   1211: iload 49
        //   1213: istore 53
        //   1215: iload_3
        //   1216: istore 19
        //   1218: iload 4
        //   1220: istore 17
        //   1222: iload 20
        //   1224: istore 10
        //   1226: iload 5
        //   1228: istore 16
        //   1230: iload 6
        //   1232: istore 12
        //   1234: iload 57
        //   1236: istore 52
        //   1238: iload 49
        //   1240: istore 54
        //   1242: aload 79
        //   1244: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   1247: ifeq +7692 -> 8939
        //   1250: iload_3
        //   1251: istore 18
        //   1253: iload 4
        //   1255: istore 15
        //   1257: iload 13
        //   1259: istore 8
        //   1261: iload 5
        //   1263: istore 14
        //   1265: iload 6
        //   1267: istore 11
        //   1269: iload 56
        //   1271: istore 51
        //   1273: iload 49
        //   1275: istore 53
        //   1277: iload_3
        //   1278: istore 19
        //   1280: iload 4
        //   1282: istore 17
        //   1284: iload 20
        //   1286: istore 10
        //   1288: iload 5
        //   1290: istore 16
        //   1292: iload 6
        //   1294: istore 12
        //   1296: iload 57
        //   1298: istore 52
        //   1300: iload 49
        //   1302: istore 54
        //   1304: aload 79
        //   1306: iconst_0
        //   1307: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   1310: iconst_1
        //   1311: if_icmpne +7622 -> 8933
        //   1314: iconst_1
        //   1315: istore 50
        //   1317: iload_3
        //   1318: istore 18
        //   1320: iload 4
        //   1322: istore 15
        //   1324: iload 13
        //   1326: istore 8
        //   1328: iload 5
        //   1330: istore 14
        //   1332: iload 6
        //   1334: istore 11
        //   1336: iload 50
        //   1338: istore 51
        //   1340: iload 49
        //   1342: istore 53
        //   1344: iload_3
        //   1345: istore 19
        //   1347: iload 4
        //   1349: istore 17
        //   1351: iload 20
        //   1353: istore 10
        //   1355: iload 5
        //   1357: istore 16
        //   1359: iload 6
        //   1361: istore 12
        //   1363: iload 50
        //   1365: istore 52
        //   1367: iload 49
        //   1369: istore 54
        //   1371: aload 79
        //   1373: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   1376: iload_3
        //   1377: istore 18
        //   1379: iload 4
        //   1381: istore 15
        //   1383: iload 13
        //   1385: istore 8
        //   1387: iload 5
        //   1389: istore 14
        //   1391: iload 6
        //   1393: istore 11
        //   1395: iload 50
        //   1397: istore 51
        //   1399: iload 49
        //   1401: istore 53
        //   1403: iload_3
        //   1404: istore 19
        //   1406: iload 4
        //   1408: istore 17
        //   1410: iload 20
        //   1412: istore 10
        //   1414: iload 5
        //   1416: istore 16
        //   1418: iload 6
        //   1420: istore 12
        //   1422: iload 50
        //   1424: istore 52
        //   1426: iload 49
        //   1428: istore 54
        //   1430: aload_0
        //   1431: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   1434: iconst_3
        //   1435: if_icmpeq +132 -> 1567
        //   1438: iload_3
        //   1439: istore 18
        //   1441: iload 4
        //   1443: istore 15
        //   1445: iload 13
        //   1447: istore 8
        //   1449: iload 5
        //   1451: istore 14
        //   1453: iload 6
        //   1455: istore 11
        //   1457: iload 50
        //   1459: istore 51
        //   1461: iload 49
        //   1463: istore 53
        //   1465: iload_3
        //   1466: istore 19
        //   1468: iload 4
        //   1470: istore 17
        //   1472: iload 20
        //   1474: istore 10
        //   1476: iload 5
        //   1478: istore 16
        //   1480: iload 6
        //   1482: istore 12
        //   1484: iload 50
        //   1486: istore 52
        //   1488: iload 49
        //   1490: istore 54
        //   1492: aload_0
        //   1493: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   1496: iconst_4
        //   1497: if_icmpeq +70 -> 1567
        //   1500: iload 49
        //   1502: ifeq +8892 -> 10394
        //   1505: iload_3
        //   1506: istore 18
        //   1508: iload 4
        //   1510: istore 15
        //   1512: iload 13
        //   1514: istore 8
        //   1516: iload 5
        //   1518: istore 14
        //   1520: iload 6
        //   1522: istore 11
        //   1524: iload 50
        //   1526: istore 51
        //   1528: iload 49
        //   1530: istore 53
        //   1532: iload_3
        //   1533: istore 19
        //   1535: iload 4
        //   1537: istore 17
        //   1539: iload 20
        //   1541: istore 10
        //   1543: iload 5
        //   1545: istore 16
        //   1547: iload 6
        //   1549: istore 12
        //   1551: iload 50
        //   1553: istore 52
        //   1555: iload 49
        //   1557: istore 54
        //   1559: aload_0
        //   1560: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   1563: iconst_2
        //   1564: if_icmpne +8830 -> 10394
        //   1567: iload_3
        //   1568: istore 18
        //   1570: iload 4
        //   1572: istore 15
        //   1574: iload 13
        //   1576: istore 8
        //   1578: iload 5
        //   1580: istore 14
        //   1582: iload 6
        //   1584: istore 11
        //   1586: iload 50
        //   1588: istore 51
        //   1590: iload 49
        //   1592: istore 53
        //   1594: iload_3
        //   1595: istore 19
        //   1597: iload 4
        //   1599: istore 17
        //   1601: iload 20
        //   1603: istore 10
        //   1605: iload 5
        //   1607: istore 16
        //   1609: iload 6
        //   1611: istore 12
        //   1613: iload 50
        //   1615: istore 52
        //   1617: iload 49
        //   1619: istore 54
        //   1621: aload_0
        //   1622: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1625: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   1628: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   1631: ldc 128
        //   1633: iconst_1
        //   1634: anewarray 4	java/lang/Object
        //   1637: dup
        //   1638: iconst_0
        //   1639: aload_0
        //   1640: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   1643: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   1646: aastore
        //   1647: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   1650: iconst_0
        //   1651: anewarray 4	java/lang/Object
        //   1654: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   1657: astore 79
        //   1659: iload 22
        //   1661: istore 7
        //   1663: iload_3
        //   1664: istore 18
        //   1666: iload 4
        //   1668: istore 15
        //   1670: iload 13
        //   1672: istore 8
        //   1674: iload 5
        //   1676: istore 14
        //   1678: iload 6
        //   1680: istore 11
        //   1682: iload 50
        //   1684: istore 51
        //   1686: iload 49
        //   1688: istore 53
        //   1690: iload_3
        //   1691: istore 19
        //   1693: iload 4
        //   1695: istore 17
        //   1697: iload 20
        //   1699: istore 10
        //   1701: iload 5
        //   1703: istore 16
        //   1705: iload 6
        //   1707: istore 12
        //   1709: iload 50
        //   1711: istore 52
        //   1713: iload 49
        //   1715: istore 54
        //   1717: aload 79
        //   1719: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   1722: ifeq +65 -> 1787
        //   1725: iload_3
        //   1726: istore 18
        //   1728: iload 4
        //   1730: istore 15
        //   1732: iload 13
        //   1734: istore 8
        //   1736: iload 5
        //   1738: istore 14
        //   1740: iload 6
        //   1742: istore 11
        //   1744: iload 50
        //   1746: istore 51
        //   1748: iload 49
        //   1750: istore 53
        //   1752: iload_3
        //   1753: istore 19
        //   1755: iload 4
        //   1757: istore 17
        //   1759: iload 20
        //   1761: istore 10
        //   1763: iload 5
        //   1765: istore 16
        //   1767: iload 6
        //   1769: istore 12
        //   1771: iload 50
        //   1773: istore 52
        //   1775: iload 49
        //   1777: istore 54
        //   1779: aload 79
        //   1781: iconst_0
        //   1782: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   1785: istore 7
        //   1787: iload_3
        //   1788: istore 18
        //   1790: iload 4
        //   1792: istore 15
        //   1794: iload 7
        //   1796: istore 8
        //   1798: iload 5
        //   1800: istore 14
        //   1802: iload 6
        //   1804: istore 11
        //   1806: iload 50
        //   1808: istore 51
        //   1810: iload 49
        //   1812: istore 53
        //   1814: iload_3
        //   1815: istore 19
        //   1817: iload 4
        //   1819: istore 17
        //   1821: iload 7
        //   1823: istore 10
        //   1825: iload 5
        //   1827: istore 16
        //   1829: iload 6
        //   1831: istore 12
        //   1833: iload 50
        //   1835: istore 52
        //   1837: iload 49
        //   1839: istore 54
        //   1841: aload 79
        //   1843: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   1846: iload_3
        //   1847: istore 18
        //   1849: iload 4
        //   1851: istore 15
        //   1853: iload 7
        //   1855: istore 8
        //   1857: iload 5
        //   1859: istore 14
        //   1861: iload 6
        //   1863: istore 11
        //   1865: iload 50
        //   1867: istore 51
        //   1869: iload 49
        //   1871: istore 53
        //   1873: iload_3
        //   1874: istore 19
        //   1876: iload 4
        //   1878: istore 17
        //   1880: iload 7
        //   1882: istore 10
        //   1884: iload 5
        //   1886: istore 16
        //   1888: iload 6
        //   1890: istore 12
        //   1892: iload 50
        //   1894: istore 52
        //   1896: iload 49
        //   1898: istore 54
        //   1900: aload_0
        //   1901: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   1904: iconst_4
        //   1905: if_icmpne +18028 -> 19933
        //   1908: iload_3
        //   1909: istore 18
        //   1911: iload 4
        //   1913: istore 15
        //   1915: iload 7
        //   1917: istore 8
        //   1919: iload 5
        //   1921: istore 14
        //   1923: iload 6
        //   1925: istore 11
        //   1927: iload 50
        //   1929: istore 51
        //   1931: iload 49
        //   1933: istore 53
        //   1935: iload_3
        //   1936: istore 19
        //   1938: iload 4
        //   1940: istore 17
        //   1942: iload 7
        //   1944: istore 10
        //   1946: iload 5
        //   1948: istore 16
        //   1950: iload 6
        //   1952: istore 12
        //   1954: iload 50
        //   1956: istore 52
        //   1958: iload 49
        //   1960: istore 54
        //   1962: aload_0
        //   1963: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   1966: ifeq +17967 -> 19933
        //   1969: iload_3
        //   1970: istore 18
        //   1972: iload 4
        //   1974: istore 15
        //   1976: iload 7
        //   1978: istore 8
        //   1980: iload 5
        //   1982: istore 14
        //   1984: iload 6
        //   1986: istore 11
        //   1988: iload 50
        //   1990: istore 51
        //   1992: iload 49
        //   1994: istore 53
        //   1996: iload_3
        //   1997: istore 19
        //   1999: iload 4
        //   2001: istore 17
        //   2003: iload 7
        //   2005: istore 10
        //   2007: iload 5
        //   2009: istore 16
        //   2011: iload 6
        //   2013: istore 12
        //   2015: iload 50
        //   2017: istore 52
        //   2019: iload 49
        //   2021: istore 54
        //   2023: aload_0
        //   2024: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   2027: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   2030: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   2033: ldc 130
        //   2035: iconst_2
        //   2036: anewarray 4	java/lang/Object
        //   2039: dup
        //   2040: iconst_0
        //   2041: aload_0
        //   2042: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   2045: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   2048: aastore
        //   2049: dup
        //   2050: iconst_1
        //   2051: aload_0
        //   2052: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   2055: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2058: aastore
        //   2059: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   2062: iconst_0
        //   2063: anewarray 4	java/lang/Object
        //   2066: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   2069: astore 79
        //   2071: iload_3
        //   2072: istore 18
        //   2074: iload 4
        //   2076: istore 15
        //   2078: iload 7
        //   2080: istore 8
        //   2082: iload 5
        //   2084: istore 14
        //   2086: iload 6
        //   2088: istore 11
        //   2090: iload 50
        //   2092: istore 51
        //   2094: iload 49
        //   2096: istore 53
        //   2098: iload_3
        //   2099: istore 19
        //   2101: iload 4
        //   2103: istore 17
        //   2105: iload 7
        //   2107: istore 10
        //   2109: iload 5
        //   2111: istore 16
        //   2113: iload 6
        //   2115: istore 12
        //   2117: iload 50
        //   2119: istore 52
        //   2121: iload 49
        //   2123: istore 54
        //   2125: aload 79
        //   2127: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   2130: ifeq +18032 -> 20162
        //   2133: iload_3
        //   2134: istore 18
        //   2136: iload 4
        //   2138: istore 15
        //   2140: iload 7
        //   2142: istore 8
        //   2144: iload 5
        //   2146: istore 14
        //   2148: iload 6
        //   2150: istore 11
        //   2152: iload 50
        //   2154: istore 51
        //   2156: iload 49
        //   2158: istore 53
        //   2160: iload_3
        //   2161: istore 19
        //   2163: iload 4
        //   2165: istore 17
        //   2167: iload 7
        //   2169: istore 10
        //   2171: iload 5
        //   2173: istore 16
        //   2175: iload 6
        //   2177: istore 12
        //   2179: iload 50
        //   2181: istore 52
        //   2183: iload 49
        //   2185: istore 54
        //   2187: aload 79
        //   2189: iconst_0
        //   2190: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   2193: istore 9
        //   2195: iload_3
        //   2196: istore 18
        //   2198: iload 4
        //   2200: istore 15
        //   2202: iload 7
        //   2204: istore 8
        //   2206: iload 5
        //   2208: istore 14
        //   2210: iload 6
        //   2212: istore 11
        //   2214: iload 50
        //   2216: istore 51
        //   2218: iload 49
        //   2220: istore 53
        //   2222: iload_3
        //   2223: istore 19
        //   2225: iload 4
        //   2227: istore 17
        //   2229: iload 7
        //   2231: istore 10
        //   2233: iload 5
        //   2235: istore 16
        //   2237: iload 6
        //   2239: istore 12
        //   2241: iload 50
        //   2243: istore 52
        //   2245: iload 49
        //   2247: istore 54
        //   2249: aload 79
        //   2251: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   2254: iload_3
        //   2255: istore 18
        //   2257: iload 4
        //   2259: istore 15
        //   2261: iload 7
        //   2263: istore 8
        //   2265: iload 5
        //   2267: istore 14
        //   2269: iload 6
        //   2271: istore 11
        //   2273: iload 50
        //   2275: istore 51
        //   2277: iload 49
        //   2279: istore 53
        //   2281: iload_3
        //   2282: istore 19
        //   2284: iload 4
        //   2286: istore 17
        //   2288: iload 7
        //   2290: istore 10
        //   2292: iload 5
        //   2294: istore 16
        //   2296: iload 6
        //   2298: istore 12
        //   2300: iload 50
        //   2302: istore 52
        //   2304: iload 49
        //   2306: istore 54
        //   2308: aload_0
        //   2309: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   2312: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   2315: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   2318: ldc 137
        //   2320: iconst_2
        //   2321: anewarray 4	java/lang/Object
        //   2324: dup
        //   2325: iconst_0
        //   2326: aload_0
        //   2327: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   2330: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   2333: aastore
        //   2334: dup
        //   2335: iconst_1
        //   2336: aload_0
        //   2337: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   2340: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2343: aastore
        //   2344: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   2347: iconst_0
        //   2348: anewarray 4	java/lang/Object
        //   2351: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   2354: astore 79
        //   2356: iload_3
        //   2357: istore 18
        //   2359: iload 4
        //   2361: istore 15
        //   2363: iload 7
        //   2365: istore 8
        //   2367: iload 5
        //   2369: istore 14
        //   2371: iload 6
        //   2373: istore 11
        //   2375: iload 50
        //   2377: istore 51
        //   2379: iload 49
        //   2381: istore 53
        //   2383: iload_3
        //   2384: istore 19
        //   2386: iload 4
        //   2388: istore 17
        //   2390: iload 7
        //   2392: istore 10
        //   2394: iload 5
        //   2396: istore 16
        //   2398: iload 6
        //   2400: istore 12
        //   2402: iload 50
        //   2404: istore 52
        //   2406: iload 49
        //   2408: istore 54
        //   2410: aload 79
        //   2412: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   2415: ifeq +17753 -> 20168
        //   2418: iload_3
        //   2419: istore 18
        //   2421: iload 4
        //   2423: istore 15
        //   2425: iload 7
        //   2427: istore 8
        //   2429: iload 5
        //   2431: istore 14
        //   2433: iload 6
        //   2435: istore 11
        //   2437: iload 50
        //   2439: istore 51
        //   2441: iload 49
        //   2443: istore 53
        //   2445: iload_3
        //   2446: istore 19
        //   2448: iload 4
        //   2450: istore 17
        //   2452: iload 7
        //   2454: istore 10
        //   2456: iload 5
        //   2458: istore 16
        //   2460: iload 6
        //   2462: istore 12
        //   2464: iload 50
        //   2466: istore 52
        //   2468: iload 49
        //   2470: istore 54
        //   2472: aload 79
        //   2474: iconst_0
        //   2475: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   2478: istore 13
        //   2480: iload_3
        //   2481: istore 18
        //   2483: iload 4
        //   2485: istore 15
        //   2487: iload 7
        //   2489: istore 8
        //   2491: iload 5
        //   2493: istore 14
        //   2495: iload 6
        //   2497: istore 11
        //   2499: iload 50
        //   2501: istore 51
        //   2503: iload 49
        //   2505: istore 53
        //   2507: iload_3
        //   2508: istore 19
        //   2510: iload 4
        //   2512: istore 17
        //   2514: iload 7
        //   2516: istore 10
        //   2518: iload 5
        //   2520: istore 16
        //   2522: iload 6
        //   2524: istore 12
        //   2526: iload 50
        //   2528: istore 52
        //   2530: iload 49
        //   2532: istore 54
        //   2534: aload 79
        //   2536: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   2539: iload 9
        //   2541: iconst_m1
        //   2542: if_icmpeq +17391 -> 19933
        //   2545: iload 13
        //   2547: iconst_m1
        //   2548: if_icmpeq +17385 -> 19933
        //   2551: iload 9
        //   2553: iload 13
        //   2555: if_icmpne +7165 -> 9720
        //   2558: iload 9
        //   2560: istore_2
        //   2561: lload 71
        //   2563: lstore 73
        //   2565: iload_2
        //   2566: ifeq +7669 -> 10235
        //   2569: iconst_1
        //   2570: istore 8
        //   2572: iload 8
        //   2574: istore 9
        //   2576: iload 8
        //   2578: ifeq +255 -> 2833
        //   2581: iload_3
        //   2582: istore 16
        //   2584: iload_1
        //   2585: istore 17
        //   2587: iload 4
        //   2589: istore 18
        //   2591: iload 7
        //   2593: istore 15
        //   2595: iload 5
        //   2597: istore 19
        //   2599: iload 6
        //   2601: istore 20
        //   2603: iload 50
        //   2605: istore 53
        //   2607: iload 49
        //   2609: istore 54
        //   2611: iload_3
        //   2612: istore 9
        //   2614: iload_1
        //   2615: istore 10
        //   2617: iload 4
        //   2619: istore 11
        //   2621: iload 7
        //   2623: istore 12
        //   2625: iload 5
        //   2627: istore 13
        //   2629: iload 6
        //   2631: istore 14
        //   2633: iload 50
        //   2635: istore 51
        //   2637: iload 49
        //   2639: istore 52
        //   2641: aload_0
        //   2642: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   2645: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   2648: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   2651: ldc 139
        //   2653: iconst_3
        //   2654: anewarray 4	java/lang/Object
        //   2657: dup
        //   2658: iconst_0
        //   2659: aload_0
        //   2660: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   2663: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   2666: aastore
        //   2667: dup
        //   2668: iconst_1
        //   2669: iload_2
        //   2670: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2673: aastore
        //   2674: dup
        //   2675: iconst_2
        //   2676: iload_2
        //   2677: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2680: aastore
        //   2681: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   2684: iconst_0
        //   2685: anewarray 4	java/lang/Object
        //   2688: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   2691: astore 79
        //   2693: iload_3
        //   2694: istore 16
        //   2696: iload_1
        //   2697: istore 17
        //   2699: iload 4
        //   2701: istore 18
        //   2703: iload 7
        //   2705: istore 15
        //   2707: iload 5
        //   2709: istore 19
        //   2711: iload 6
        //   2713: istore 20
        //   2715: iload 50
        //   2717: istore 53
        //   2719: iload 49
        //   2721: istore 54
        //   2723: iload_3
        //   2724: istore 9
        //   2726: iload_1
        //   2727: istore 10
        //   2729: iload 4
        //   2731: istore 11
        //   2733: iload 7
        //   2735: istore 12
        //   2737: iload 5
        //   2739: istore 13
        //   2741: iload 6
        //   2743: istore 14
        //   2745: iload 50
        //   2747: istore 51
        //   2749: iload 49
        //   2751: istore 52
        //   2753: aload 79
        //   2755: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   2758: ifeq +6 -> 2764
        //   2761: iconst_0
        //   2762: istore 8
        //   2764: iload_3
        //   2765: istore 16
        //   2767: iload_1
        //   2768: istore 17
        //   2770: iload 4
        //   2772: istore 18
        //   2774: iload 7
        //   2776: istore 15
        //   2778: iload 5
        //   2780: istore 19
        //   2782: iload 6
        //   2784: istore 20
        //   2786: iload 50
        //   2788: istore 53
        //   2790: iload 49
        //   2792: istore 54
        //   2794: iload_3
        //   2795: istore 9
        //   2797: iload_1
        //   2798: istore 10
        //   2800: iload 4
        //   2802: istore 11
        //   2804: iload 7
        //   2806: istore 12
        //   2808: iload 5
        //   2810: istore 13
        //   2812: iload 6
        //   2814: istore 14
        //   2816: iload 50
        //   2818: istore 51
        //   2820: iload 49
        //   2822: istore 52
        //   2824: aload 79
        //   2826: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   2829: iload 8
        //   2831: istore 9
        //   2833: iload 9
        //   2835: ifeq +7553 -> 10388
        //   2838: lconst_0
        //   2839: lstore 71
        //   2841: lconst_1
        //   2842: lstore 75
        //   2844: iload_3
        //   2845: istore 16
        //   2847: iload_1
        //   2848: istore 17
        //   2850: iload 4
        //   2852: istore 18
        //   2854: iload 7
        //   2856: istore 15
        //   2858: iload 5
        //   2860: istore 19
        //   2862: iload 6
        //   2864: istore 20
        //   2866: iload 50
        //   2868: istore 53
        //   2870: iload 49
        //   2872: istore 54
        //   2874: iload_3
        //   2875: istore 9
        //   2877: iload_1
        //   2878: istore 10
        //   2880: iload 4
        //   2882: istore 11
        //   2884: iload 7
        //   2886: istore 12
        //   2888: iload 5
        //   2890: istore 13
        //   2892: iload 6
        //   2894: istore 14
        //   2896: iload 50
        //   2898: istore 51
        //   2900: iload 49
        //   2902: istore 52
        //   2904: aload_0
        //   2905: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   2908: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   2911: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   2914: ldc 141
        //   2916: iconst_2
        //   2917: anewarray 4	java/lang/Object
        //   2920: dup
        //   2921: iconst_0
        //   2922: aload_0
        //   2923: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   2926: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   2929: aastore
        //   2930: dup
        //   2931: iconst_1
        //   2932: iload_2
        //   2933: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   2936: aastore
        //   2937: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   2940: iconst_0
        //   2941: anewarray 4	java/lang/Object
        //   2944: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   2947: astore 79
        //   2949: iload_3
        //   2950: istore 16
        //   2952: iload_1
        //   2953: istore 17
        //   2955: iload 4
        //   2957: istore 18
        //   2959: iload 7
        //   2961: istore 15
        //   2963: iload 5
        //   2965: istore 19
        //   2967: iload 6
        //   2969: istore 20
        //   2971: iload 50
        //   2973: istore 53
        //   2975: iload 49
        //   2977: istore 54
        //   2979: iload_3
        //   2980: istore 9
        //   2982: iload_1
        //   2983: istore 10
        //   2985: iload 4
        //   2987: istore 11
        //   2989: iload 7
        //   2991: istore 12
        //   2993: iload 5
        //   2995: istore 13
        //   2997: iload 6
        //   2999: istore 14
        //   3001: iload 50
        //   3003: istore 51
        //   3005: iload 49
        //   3007: istore 52
        //   3009: aload 79
        //   3011: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   3014: ifeq +92 -> 3106
        //   3017: iload_3
        //   3018: istore 16
        //   3020: iload_1
        //   3021: istore 17
        //   3023: iload 4
        //   3025: istore 18
        //   3027: iload 7
        //   3029: istore 15
        //   3031: iload 5
        //   3033: istore 19
        //   3035: iload 6
        //   3037: istore 20
        //   3039: iload 50
        //   3041: istore 53
        //   3043: iload 49
        //   3045: istore 54
        //   3047: iload_3
        //   3048: istore 9
        //   3050: iload_1
        //   3051: istore 10
        //   3053: iload 4
        //   3055: istore 11
        //   3057: iload 7
        //   3059: istore 12
        //   3061: iload 5
        //   3063: istore 13
        //   3065: iload 6
        //   3067: istore 14
        //   3069: iload 50
        //   3071: istore 51
        //   3073: iload 49
        //   3075: istore 52
        //   3077: aload 79
        //   3079: iconst_0
        //   3080: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   3083: i2l
        //   3084: lstore 77
        //   3086: lload 77
        //   3088: lstore 71
        //   3090: iload 21
        //   3092: ifeq +14 -> 3106
        //   3095: lload 77
        //   3097: iload 21
        //   3099: i2l
        //   3100: bipush 32
        //   3102: lshl
        //   3103: lor
        //   3104: lstore 71
        //   3106: iload_3
        //   3107: istore 16
        //   3109: iload_1
        //   3110: istore 17
        //   3112: iload 4
        //   3114: istore 18
        //   3116: iload 7
        //   3118: istore 15
        //   3120: iload 5
        //   3122: istore 19
        //   3124: iload 6
        //   3126: istore 20
        //   3128: iload 50
        //   3130: istore 53
        //   3132: iload 49
        //   3134: istore 54
        //   3136: iload_3
        //   3137: istore 9
        //   3139: iload_1
        //   3140: istore 10
        //   3142: iload 4
        //   3144: istore 11
        //   3146: iload 7
        //   3148: istore 12
        //   3150: iload 5
        //   3152: istore 13
        //   3154: iload 6
        //   3156: istore 14
        //   3158: iload 50
        //   3160: istore 51
        //   3162: iload 49
        //   3164: istore 52
        //   3166: aload 79
        //   3168: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   3171: iload_3
        //   3172: istore 16
        //   3174: iload_1
        //   3175: istore 17
        //   3177: iload 4
        //   3179: istore 18
        //   3181: iload 7
        //   3183: istore 15
        //   3185: iload 5
        //   3187: istore 19
        //   3189: iload 6
        //   3191: istore 20
        //   3193: iload 50
        //   3195: istore 53
        //   3197: iload 49
        //   3199: istore 54
        //   3201: iload_3
        //   3202: istore 9
        //   3204: iload_1
        //   3205: istore 10
        //   3207: iload 4
        //   3209: istore 11
        //   3211: iload 7
        //   3213: istore 12
        //   3215: iload 5
        //   3217: istore 13
        //   3219: iload 6
        //   3221: istore 14
        //   3223: iload 50
        //   3225: istore 51
        //   3227: iload 49
        //   3229: istore 52
        //   3231: aload_0
        //   3232: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   3235: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   3238: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   3241: ldc 143
        //   3243: iconst_2
        //   3244: anewarray 4	java/lang/Object
        //   3247: dup
        //   3248: iconst_0
        //   3249: aload_0
        //   3250: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   3253: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3256: aastore
        //   3257: dup
        //   3258: iconst_1
        //   3259: iload_2
        //   3260: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   3263: aastore
        //   3264: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   3267: iconst_0
        //   3268: anewarray 4	java/lang/Object
        //   3271: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   3274: astore 79
        //   3276: iload_3
        //   3277: istore 16
        //   3279: iload_1
        //   3280: istore 17
        //   3282: iload 4
        //   3284: istore 18
        //   3286: iload 7
        //   3288: istore 15
        //   3290: iload 5
        //   3292: istore 19
        //   3294: iload 6
        //   3296: istore 20
        //   3298: iload 50
        //   3300: istore 53
        //   3302: iload 49
        //   3304: istore 54
        //   3306: iload_3
        //   3307: istore 9
        //   3309: iload_1
        //   3310: istore 10
        //   3312: iload 4
        //   3314: istore 11
        //   3316: iload 7
        //   3318: istore 12
        //   3320: iload 5
        //   3322: istore 13
        //   3324: iload 6
        //   3326: istore 14
        //   3328: iload 50
        //   3330: istore 51
        //   3332: iload 49
        //   3334: istore 52
        //   3336: aload 79
        //   3338: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   3341: ifeq +92 -> 3433
        //   3344: iload_3
        //   3345: istore 16
        //   3347: iload_1
        //   3348: istore 17
        //   3350: iload 4
        //   3352: istore 18
        //   3354: iload 7
        //   3356: istore 15
        //   3358: iload 5
        //   3360: istore 19
        //   3362: iload 6
        //   3364: istore 20
        //   3366: iload 50
        //   3368: istore 53
        //   3370: iload 49
        //   3372: istore 54
        //   3374: iload_3
        //   3375: istore 9
        //   3377: iload_1
        //   3378: istore 10
        //   3380: iload 4
        //   3382: istore 11
        //   3384: iload 7
        //   3386: istore 12
        //   3388: iload 5
        //   3390: istore 13
        //   3392: iload 6
        //   3394: istore 14
        //   3396: iload 50
        //   3398: istore 51
        //   3400: iload 49
        //   3402: istore 52
        //   3404: aload 79
        //   3406: iconst_0
        //   3407: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   3410: i2l
        //   3411: lstore 77
        //   3413: lload 77
        //   3415: lstore 75
        //   3417: iload 21
        //   3419: ifeq +14 -> 3433
        //   3422: lload 77
        //   3424: iload 21
        //   3426: i2l
        //   3427: bipush 32
        //   3429: lshl
        //   3430: lor
        //   3431: lstore 75
        //   3433: iload_3
        //   3434: istore 16
        //   3436: iload_1
        //   3437: istore 17
        //   3439: iload 4
        //   3441: istore 18
        //   3443: iload 7
        //   3445: istore 15
        //   3447: iload 5
        //   3449: istore 19
        //   3451: iload 6
        //   3453: istore 20
        //   3455: iload 50
        //   3457: istore 53
        //   3459: iload 49
        //   3461: istore 54
        //   3463: iload_3
        //   3464: istore 9
        //   3466: iload_1
        //   3467: istore 10
        //   3469: iload 4
        //   3471: istore 11
        //   3473: iload 7
        //   3475: istore 12
        //   3477: iload 5
        //   3479: istore 13
        //   3481: iload 6
        //   3483: istore 14
        //   3485: iload 50
        //   3487: istore 51
        //   3489: iload 49
        //   3491: istore 52
        //   3493: aload 79
        //   3495: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   3498: lload 71
        //   3500: lconst_0
        //   3501: lcmp
        //   3502: ifne +16544 -> 20046
        //   3505: lload 75
        //   3507: lconst_1
        //   3508: lcmp
        //   3509: ifeq +6732 -> 10241
        //   3512: goto +16534 -> 20046
        //   3515: iload_3
        //   3516: istore 16
        //   3518: iload_1
        //   3519: istore 17
        //   3521: iload 4
        //   3523: istore 18
        //   3525: iload 7
        //   3527: istore 15
        //   3529: iload 5
        //   3531: istore 19
        //   3533: iload 6
        //   3535: istore 20
        //   3537: iload 50
        //   3539: istore 53
        //   3541: iload 49
        //   3543: istore 54
        //   3545: iload_3
        //   3546: istore 9
        //   3548: iload_1
        //   3549: istore 10
        //   3551: iload 4
        //   3553: istore 11
        //   3555: iload 7
        //   3557: istore 12
        //   3559: iload 5
        //   3561: istore 13
        //   3563: iload 6
        //   3565: istore 14
        //   3567: iload 50
        //   3569: istore 51
        //   3571: iload 49
        //   3573: istore 52
        //   3575: aload_0
        //   3576: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   3579: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   3582: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   3585: ldc 145
        //   3587: bipush 8
        //   3589: anewarray 4	java/lang/Object
        //   3592: dup
        //   3593: iconst_0
        //   3594: aload_0
        //   3595: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   3598: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3601: aastore
        //   3602: dup
        //   3603: iconst_1
        //   3604: lload 73
        //   3606: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3609: aastore
        //   3610: dup
        //   3611: iconst_2
        //   3612: lload 75
        //   3614: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3617: aastore
        //   3618: dup
        //   3619: iconst_3
        //   3620: iload_3
        //   3621: iconst_2
        //   3622: idiv
        //   3623: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   3626: aastore
        //   3627: dup
        //   3628: iconst_4
        //   3629: aload_0
        //   3630: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   3633: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3636: aastore
        //   3637: dup
        //   3638: iconst_5
        //   3639: lload 73
        //   3641: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3644: aastore
        //   3645: dup
        //   3646: bipush 6
        //   3648: lload 77
        //   3650: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   3653: aastore
        //   3654: dup
        //   3655: bipush 7
        //   3657: iload_3
        //   3658: iconst_2
        //   3659: idiv
        //   3660: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   3663: aastore
        //   3664: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   3667: iconst_0
        //   3668: anewarray 4	java/lang/Object
        //   3671: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   3674: astore 79
        //   3676: goto +16406 -> 20082
        //   3679: iload_3
        //   3680: istore 16
        //   3682: iload_1
        //   3683: istore 17
        //   3685: iload 4
        //   3687: istore 18
        //   3689: iload 7
        //   3691: istore 15
        //   3693: iload 5
        //   3695: istore 19
        //   3697: iload 6
        //   3699: istore 20
        //   3701: iload 50
        //   3703: istore 53
        //   3705: iload 49
        //   3707: istore 54
        //   3709: iload_3
        //   3710: istore 9
        //   3712: iload_1
        //   3713: istore 10
        //   3715: iload 4
        //   3717: istore 11
        //   3719: iload 7
        //   3721: istore 12
        //   3723: iload 5
        //   3725: istore 13
        //   3727: iload 6
        //   3729: istore 14
        //   3731: iload 50
        //   3733: istore 51
        //   3735: iload 49
        //   3737: istore 52
        //   3739: aload 79
        //   3741: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   3744: ifeq +12420 -> 16164
        //   3747: iload_3
        //   3748: istore 16
        //   3750: iload_1
        //   3751: istore 17
        //   3753: iload 4
        //   3755: istore 18
        //   3757: iload 7
        //   3759: istore 15
        //   3761: iload 5
        //   3763: istore 19
        //   3765: iload 6
        //   3767: istore 20
        //   3769: iload 50
        //   3771: istore 53
        //   3773: iload 49
        //   3775: istore 54
        //   3777: iload_3
        //   3778: istore 9
        //   3780: iload_1
        //   3781: istore 10
        //   3783: iload 4
        //   3785: istore 11
        //   3787: iload 7
        //   3789: istore 12
        //   3791: iload 5
        //   3793: istore 13
        //   3795: iload 6
        //   3797: istore 14
        //   3799: iload 50
        //   3801: istore 51
        //   3803: iload 49
        //   3805: istore 52
        //   3807: aload 79
        //   3809: iconst_1
        //   3810: invokevirtual 149	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   3813: astore 80
        //   3815: aload 80
        //   3817: ifnull -138 -> 3679
        //   3820: iload_3
        //   3821: istore 16
        //   3823: iload_1
        //   3824: istore 17
        //   3826: iload 4
        //   3828: istore 18
        //   3830: iload 7
        //   3832: istore 15
        //   3834: iload 5
        //   3836: istore 19
        //   3838: iload 6
        //   3840: istore 20
        //   3842: iload 50
        //   3844: istore 53
        //   3846: iload 49
        //   3848: istore 54
        //   3850: iload_3
        //   3851: istore 9
        //   3853: iload_1
        //   3854: istore 10
        //   3856: iload 4
        //   3858: istore 11
        //   3860: iload 7
        //   3862: istore 12
        //   3864: iload 5
        //   3866: istore 13
        //   3868: iload 6
        //   3870: istore 14
        //   3872: iload 50
        //   3874: istore 51
        //   3876: iload 49
        //   3878: istore 52
        //   3880: aload 80
        //   3882: aload 80
        //   3884: iconst_0
        //   3885: invokevirtual 155	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   3888: iconst_0
        //   3889: invokestatic 161	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   3892: astore 88
        //   3894: iload_3
        //   3895: istore 16
        //   3897: iload_1
        //   3898: istore 17
        //   3900: iload 4
        //   3902: istore 18
        //   3904: iload 7
        //   3906: istore 15
        //   3908: iload 5
        //   3910: istore 19
        //   3912: iload 6
        //   3914: istore 20
        //   3916: iload 50
        //   3918: istore 53
        //   3920: iload 49
        //   3922: istore 54
        //   3924: iload_3
        //   3925: istore 9
        //   3927: iload_1
        //   3928: istore 10
        //   3930: iload 4
        //   3932: istore 11
        //   3934: iload 7
        //   3936: istore 12
        //   3938: iload 5
        //   3940: istore 13
        //   3942: iload 6
        //   3944: istore 14
        //   3946: iload 50
        //   3948: istore 51
        //   3950: iload 49
        //   3952: istore 52
        //   3954: aload 80
        //   3956: invokevirtual 164	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   3959: iload_3
        //   3960: istore 16
        //   3962: iload_1
        //   3963: istore 17
        //   3965: iload 4
        //   3967: istore 18
        //   3969: iload 7
        //   3971: istore 15
        //   3973: iload 5
        //   3975: istore 19
        //   3977: iload 6
        //   3979: istore 20
        //   3981: iload 50
        //   3983: istore 53
        //   3985: iload 49
        //   3987: istore 54
        //   3989: iload_3
        //   3990: istore 9
        //   3992: iload_1
        //   3993: istore 10
        //   3995: iload 4
        //   3997: istore 11
        //   3999: iload 7
        //   4001: istore 12
        //   4003: iload 5
        //   4005: istore 13
        //   4007: iload 6
        //   4009: istore 14
        //   4011: iload 50
        //   4013: istore 51
        //   4015: iload 49
        //   4017: istore 52
        //   4019: aload 88
        //   4021: aload 79
        //   4023: iconst_0
        //   4024: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   4027: invokestatic 170	org/vidogram/messenger/MessageObject:setUnreadFlags	(Lorg/vidogram/tgnet/TLRPC$Message;I)V
        //   4030: iload_3
        //   4031: istore 16
        //   4033: iload_1
        //   4034: istore 17
        //   4036: iload 4
        //   4038: istore 18
        //   4040: iload 7
        //   4042: istore 15
        //   4044: iload 5
        //   4046: istore 19
        //   4048: iload 6
        //   4050: istore 20
        //   4052: iload 50
        //   4054: istore 53
        //   4056: iload 49
        //   4058: istore 54
        //   4060: iload_3
        //   4061: istore 9
        //   4063: iload_1
        //   4064: istore 10
        //   4066: iload 4
        //   4068: istore 11
        //   4070: iload 7
        //   4072: istore 12
        //   4074: iload 5
        //   4076: istore 13
        //   4078: iload 6
        //   4080: istore 14
        //   4082: iload 50
        //   4084: istore 51
        //   4086: iload 49
        //   4088: istore 52
        //   4090: aload 88
        //   4092: aload 79
        //   4094: iconst_3
        //   4095: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   4098: putfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   4101: iload_3
        //   4102: istore 16
        //   4104: iload_1
        //   4105: istore 17
        //   4107: iload 4
        //   4109: istore 18
        //   4111: iload 7
        //   4113: istore 15
        //   4115: iload 5
        //   4117: istore 19
        //   4119: iload 6
        //   4121: istore 20
        //   4123: iload 50
        //   4125: istore 53
        //   4127: iload 49
        //   4129: istore 54
        //   4131: iload_3
        //   4132: istore 9
        //   4134: iload_1
        //   4135: istore 10
        //   4137: iload 4
        //   4139: istore 11
        //   4141: iload 7
        //   4143: istore 12
        //   4145: iload 5
        //   4147: istore 13
        //   4149: iload 6
        //   4151: istore 14
        //   4153: iload 50
        //   4155: istore 51
        //   4157: iload 49
        //   4159: istore 52
        //   4161: aload 88
        //   4163: aload 79
        //   4165: iconst_4
        //   4166: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   4169: putfield 176	org/vidogram/tgnet/TLRPC$Message:date	I
        //   4172: iload_3
        //   4173: istore 16
        //   4175: iload_1
        //   4176: istore 17
        //   4178: iload 4
        //   4180: istore 18
        //   4182: iload 7
        //   4184: istore 15
        //   4186: iload 5
        //   4188: istore 19
        //   4190: iload 6
        //   4192: istore 20
        //   4194: iload 50
        //   4196: istore 53
        //   4198: iload 49
        //   4200: istore 54
        //   4202: iload_3
        //   4203: istore 9
        //   4205: iload_1
        //   4206: istore 10
        //   4208: iload 4
        //   4210: istore 11
        //   4212: iload 7
        //   4214: istore 12
        //   4216: iload 5
        //   4218: istore 13
        //   4220: iload 6
        //   4222: istore 14
        //   4224: iload 50
        //   4226: istore 51
        //   4228: iload 49
        //   4230: istore 52
        //   4232: aload 88
        //   4234: aload_0
        //   4235: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   4238: putfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   4241: iload_3
        //   4242: istore 16
        //   4244: iload_1
        //   4245: istore 17
        //   4247: iload 4
        //   4249: istore 18
        //   4251: iload 7
        //   4253: istore 15
        //   4255: iload 5
        //   4257: istore 19
        //   4259: iload 6
        //   4261: istore 20
        //   4263: iload 50
        //   4265: istore 53
        //   4267: iload 49
        //   4269: istore 54
        //   4271: iload_3
        //   4272: istore 9
        //   4274: iload_1
        //   4275: istore 10
        //   4277: iload 4
        //   4279: istore 11
        //   4281: iload 7
        //   4283: istore 12
        //   4285: iload 5
        //   4287: istore 13
        //   4289: iload 6
        //   4291: istore 14
        //   4293: iload 50
        //   4295: istore 51
        //   4297: iload 49
        //   4299: istore 52
        //   4301: aload 88
        //   4303: getfield 182	org/vidogram/tgnet/TLRPC$Message:flags	I
        //   4306: sipush 1024
        //   4309: iand
        //   4310: ifeq +75 -> 4385
        //   4313: iload_3
        //   4314: istore 16
        //   4316: iload_1
        //   4317: istore 17
        //   4319: iload 4
        //   4321: istore 18
        //   4323: iload 7
        //   4325: istore 15
        //   4327: iload 5
        //   4329: istore 19
        //   4331: iload 6
        //   4333: istore 20
        //   4335: iload 50
        //   4337: istore 53
        //   4339: iload 49
        //   4341: istore 54
        //   4343: iload_3
        //   4344: istore 9
        //   4346: iload_1
        //   4347: istore 10
        //   4349: iload 4
        //   4351: istore 11
        //   4353: iload 7
        //   4355: istore 12
        //   4357: iload 5
        //   4359: istore 13
        //   4361: iload 6
        //   4363: istore 14
        //   4365: iload 50
        //   4367: istore 51
        //   4369: iload 49
        //   4371: istore 52
        //   4373: aload 88
        //   4375: aload 79
        //   4377: bipush 7
        //   4379: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   4382: putfield 185	org/vidogram/tgnet/TLRPC$Message:views	I
        //   4385: iload 48
        //   4387: ifeq +75 -> 4462
        //   4390: iload_3
        //   4391: istore 16
        //   4393: iload_1
        //   4394: istore 17
        //   4396: iload 4
        //   4398: istore 18
        //   4400: iload 7
        //   4402: istore 15
        //   4404: iload 5
        //   4406: istore 19
        //   4408: iload 6
        //   4410: istore 20
        //   4412: iload 50
        //   4414: istore 53
        //   4416: iload 49
        //   4418: istore 54
        //   4420: iload_3
        //   4421: istore 9
        //   4423: iload_1
        //   4424: istore 10
        //   4426: iload 4
        //   4428: istore 11
        //   4430: iload 7
        //   4432: istore 12
        //   4434: iload 5
        //   4436: istore 13
        //   4438: iload 6
        //   4440: istore 14
        //   4442: iload 50
        //   4444: istore 51
        //   4446: iload 49
        //   4448: istore 52
        //   4450: aload 88
        //   4452: aload 79
        //   4454: bipush 8
        //   4456: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   4459: putfield 188	org/vidogram/tgnet/TLRPC$Message:ttl	I
        //   4462: iload_3
        //   4463: istore 16
        //   4465: iload_1
        //   4466: istore 17
        //   4468: iload 4
        //   4470: istore 18
        //   4472: iload 7
        //   4474: istore 15
        //   4476: iload 5
        //   4478: istore 19
        //   4480: iload 6
        //   4482: istore 20
        //   4484: iload 50
        //   4486: istore 53
        //   4488: iload 49
        //   4490: istore 54
        //   4492: iload_3
        //   4493: istore 9
        //   4495: iload_1
        //   4496: istore 10
        //   4498: iload 4
        //   4500: istore 11
        //   4502: iload 7
        //   4504: istore 12
        //   4506: iload 5
        //   4508: istore 13
        //   4510: iload 6
        //   4512: istore 14
        //   4514: iload 50
        //   4516: istore 51
        //   4518: iload 49
        //   4520: istore 52
        //   4522: aload 82
        //   4524: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   4527: aload 88
        //   4529: invokevirtual 196	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   4532: pop
        //   4533: iload_3
        //   4534: istore 16
        //   4536: iload_1
        //   4537: istore 17
        //   4539: iload 4
        //   4541: istore 18
        //   4543: iload 7
        //   4545: istore 15
        //   4547: iload 5
        //   4549: istore 19
        //   4551: iload 6
        //   4553: istore 20
        //   4555: iload 50
        //   4557: istore 53
        //   4559: iload 49
        //   4561: istore 54
        //   4563: iload_3
        //   4564: istore 9
        //   4566: iload_1
        //   4567: istore 10
        //   4569: iload 4
        //   4571: istore 11
        //   4573: iload 7
        //   4575: istore 12
        //   4577: iload 5
        //   4579: istore 13
        //   4581: iload 6
        //   4583: istore 14
        //   4585: iload 50
        //   4587: istore 51
        //   4589: iload 49
        //   4591: istore 52
        //   4593: aload 88
        //   4595: aload 83
        //   4597: aload 84
        //   4599: invokestatic 200	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   4602: iload_3
        //   4603: istore 16
        //   4605: iload_1
        //   4606: istore 17
        //   4608: iload 4
        //   4610: istore 18
        //   4612: iload 7
        //   4614: istore 15
        //   4616: iload 5
        //   4618: istore 19
        //   4620: iload 6
        //   4622: istore 20
        //   4624: iload 50
        //   4626: istore 53
        //   4628: iload 49
        //   4630: istore 54
        //   4632: iload_3
        //   4633: istore 9
        //   4635: iload_1
        //   4636: istore 10
        //   4638: iload 4
        //   4640: istore 11
        //   4642: iload 7
        //   4644: istore 12
        //   4646: iload 5
        //   4648: istore 13
        //   4650: iload 6
        //   4652: istore 14
        //   4654: iload 50
        //   4656: istore 51
        //   4658: iload 49
        //   4660: istore 52
        //   4662: aload 88
        //   4664: getfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   4667: ifne +73 -> 4740
        //   4670: iload_3
        //   4671: istore 16
        //   4673: iload_1
        //   4674: istore 17
        //   4676: iload 4
        //   4678: istore 18
        //   4680: iload 7
        //   4682: istore 15
        //   4684: iload 5
        //   4686: istore 19
        //   4688: iload 6
        //   4690: istore 20
        //   4692: iload 50
        //   4694: istore 53
        //   4696: iload 49
        //   4698: istore 54
        //   4700: iload_3
        //   4701: istore 9
        //   4703: iload_1
        //   4704: istore 10
        //   4706: iload 4
        //   4708: istore 11
        //   4710: iload 7
        //   4712: istore 12
        //   4714: iload 5
        //   4716: istore 13
        //   4718: iload 6
        //   4720: istore 14
        //   4722: iload 50
        //   4724: istore 51
        //   4726: iload 49
        //   4728: istore 52
        //   4730: aload 88
        //   4732: getfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   4735: lconst_0
        //   4736: lcmp
        //   4737: ifeq +1229 -> 5966
        //   4740: iload_3
        //   4741: istore 16
        //   4743: iload_1
        //   4744: istore 17
        //   4746: iload 4
        //   4748: istore 18
        //   4750: iload 7
        //   4752: istore 15
        //   4754: iload 5
        //   4756: istore 19
        //   4758: iload 6
        //   4760: istore 20
        //   4762: iload 50
        //   4764: istore 53
        //   4766: iload 49
        //   4768: istore 54
        //   4770: iload_3
        //   4771: istore 9
        //   4773: iload_1
        //   4774: istore 10
        //   4776: iload 4
        //   4778: istore 11
        //   4780: iload 7
        //   4782: istore 12
        //   4784: iload 5
        //   4786: istore 13
        //   4788: iload 6
        //   4790: istore 14
        //   4792: iload 50
        //   4794: istore 51
        //   4796: iload 49
        //   4798: istore 52
        //   4800: aload 79
        //   4802: bipush 6
        //   4804: invokevirtual 210	org/vidogram/SQLite/SQLiteCursor:isNull	(I)Z
        //   4807: ifne +359 -> 5166
        //   4810: iload_3
        //   4811: istore 16
        //   4813: iload_1
        //   4814: istore 17
        //   4816: iload 4
        //   4818: istore 18
        //   4820: iload 7
        //   4822: istore 15
        //   4824: iload 5
        //   4826: istore 19
        //   4828: iload 6
        //   4830: istore 20
        //   4832: iload 50
        //   4834: istore 53
        //   4836: iload 49
        //   4838: istore 54
        //   4840: iload_3
        //   4841: istore 9
        //   4843: iload_1
        //   4844: istore 10
        //   4846: iload 4
        //   4848: istore 11
        //   4850: iload 7
        //   4852: istore 12
        //   4854: iload 5
        //   4856: istore 13
        //   4858: iload 6
        //   4860: istore 14
        //   4862: iload 50
        //   4864: istore 51
        //   4866: iload 49
        //   4868: istore 52
        //   4870: aload 79
        //   4872: bipush 6
        //   4874: invokevirtual 149	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   4877: astore 80
        //   4879: aload 80
        //   4881: ifnull +285 -> 5166
        //   4884: iload_3
        //   4885: istore 16
        //   4887: iload_1
        //   4888: istore 17
        //   4890: iload 4
        //   4892: istore 18
        //   4894: iload 7
        //   4896: istore 15
        //   4898: iload 5
        //   4900: istore 19
        //   4902: iload 6
        //   4904: istore 20
        //   4906: iload 50
        //   4908: istore 53
        //   4910: iload 49
        //   4912: istore 54
        //   4914: iload_3
        //   4915: istore 9
        //   4917: iload_1
        //   4918: istore 10
        //   4920: iload 4
        //   4922: istore 11
        //   4924: iload 7
        //   4926: istore 12
        //   4928: iload 5
        //   4930: istore 13
        //   4932: iload 6
        //   4934: istore 14
        //   4936: iload 50
        //   4938: istore 51
        //   4940: iload 49
        //   4942: istore 52
        //   4944: aload 88
        //   4946: aload 80
        //   4948: aload 80
        //   4950: iconst_0
        //   4951: invokevirtual 155	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   4954: iconst_0
        //   4955: invokestatic 161	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   4958: putfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   4961: iload_3
        //   4962: istore 16
        //   4964: iload_1
        //   4965: istore 17
        //   4967: iload 4
        //   4969: istore 18
        //   4971: iload 7
        //   4973: istore 15
        //   4975: iload 5
        //   4977: istore 19
        //   4979: iload 6
        //   4981: istore 20
        //   4983: iload 50
        //   4985: istore 53
        //   4987: iload 49
        //   4989: istore 54
        //   4991: iload_3
        //   4992: istore 9
        //   4994: iload_1
        //   4995: istore 10
        //   4997: iload 4
        //   4999: istore 11
        //   5001: iload 7
        //   5003: istore 12
        //   5005: iload 5
        //   5007: istore 13
        //   5009: iload 6
        //   5011: istore 14
        //   5013: iload 50
        //   5015: istore 51
        //   5017: iload 49
        //   5019: istore 52
        //   5021: aload 80
        //   5023: invokevirtual 164	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   5026: iload_3
        //   5027: istore 16
        //   5029: iload_1
        //   5030: istore 17
        //   5032: iload 4
        //   5034: istore 18
        //   5036: iload 7
        //   5038: istore 15
        //   5040: iload 5
        //   5042: istore 19
        //   5044: iload 6
        //   5046: istore 20
        //   5048: iload 50
        //   5050: istore 53
        //   5052: iload 49
        //   5054: istore 54
        //   5056: iload_3
        //   5057: istore 9
        //   5059: iload_1
        //   5060: istore 10
        //   5062: iload 4
        //   5064: istore 11
        //   5066: iload 7
        //   5068: istore 12
        //   5070: iload 5
        //   5072: istore 13
        //   5074: iload 6
        //   5076: istore 14
        //   5078: iload 50
        //   5080: istore 51
        //   5082: iload 49
        //   5084: istore 52
        //   5086: aload 88
        //   5088: getfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   5091: ifnull +75 -> 5166
        //   5094: iload_3
        //   5095: istore 16
        //   5097: iload_1
        //   5098: istore 17
        //   5100: iload 4
        //   5102: istore 18
        //   5104: iload 7
        //   5106: istore 15
        //   5108: iload 5
        //   5110: istore 19
        //   5112: iload 6
        //   5114: istore 20
        //   5116: iload 50
        //   5118: istore 53
        //   5120: iload 49
        //   5122: istore 54
        //   5124: iload_3
        //   5125: istore 9
        //   5127: iload_1
        //   5128: istore 10
        //   5130: iload 4
        //   5132: istore 11
        //   5134: iload 7
        //   5136: istore 12
        //   5138: iload 5
        //   5140: istore 13
        //   5142: iload 6
        //   5144: istore 14
        //   5146: iload 50
        //   5148: istore 51
        //   5150: iload 49
        //   5152: istore 52
        //   5154: aload 88
        //   5156: getfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   5159: aload 83
        //   5161: aload 84
        //   5163: invokestatic 200	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   5166: iload_3
        //   5167: istore 16
        //   5169: iload_1
        //   5170: istore 17
        //   5172: iload 4
        //   5174: istore 18
        //   5176: iload 7
        //   5178: istore 15
        //   5180: iload 5
        //   5182: istore 19
        //   5184: iload 6
        //   5186: istore 20
        //   5188: iload 50
        //   5190: istore 53
        //   5192: iload 49
        //   5194: istore 54
        //   5196: iload_3
        //   5197: istore 9
        //   5199: iload_1
        //   5200: istore 10
        //   5202: iload 4
        //   5204: istore 11
        //   5206: iload 7
        //   5208: istore 12
        //   5210: iload 5
        //   5212: istore 13
        //   5214: iload 6
        //   5216: istore 14
        //   5218: iload 50
        //   5220: istore 51
        //   5222: iload 49
        //   5224: istore 52
        //   5226: aload 88
        //   5228: getfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   5231: ifnonnull +735 -> 5966
        //   5234: iload_3
        //   5235: istore 16
        //   5237: iload_1
        //   5238: istore 17
        //   5240: iload 4
        //   5242: istore 18
        //   5244: iload 7
        //   5246: istore 15
        //   5248: iload 5
        //   5250: istore 19
        //   5252: iload 6
        //   5254: istore 20
        //   5256: iload 50
        //   5258: istore 53
        //   5260: iload 49
        //   5262: istore 54
        //   5264: iload_3
        //   5265: istore 9
        //   5267: iload_1
        //   5268: istore 10
        //   5270: iload 4
        //   5272: istore 11
        //   5274: iload 7
        //   5276: istore 12
        //   5278: iload 5
        //   5280: istore 13
        //   5282: iload 6
        //   5284: istore 14
        //   5286: iload 50
        //   5288: istore 51
        //   5290: iload 49
        //   5292: istore 52
        //   5294: aload 88
        //   5296: getfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   5299: ifeq +10340 -> 15639
        //   5302: iload_3
        //   5303: istore 16
        //   5305: iload_1
        //   5306: istore 17
        //   5308: iload 4
        //   5310: istore 18
        //   5312: iload 7
        //   5314: istore 15
        //   5316: iload 5
        //   5318: istore 19
        //   5320: iload 6
        //   5322: istore 20
        //   5324: iload 50
        //   5326: istore 53
        //   5328: iload 49
        //   5330: istore 54
        //   5332: iload_3
        //   5333: istore 9
        //   5335: iload_1
        //   5336: istore 10
        //   5338: iload 4
        //   5340: istore 11
        //   5342: iload 7
        //   5344: istore 12
        //   5346: iload 5
        //   5348: istore 13
        //   5350: iload 6
        //   5352: istore 14
        //   5354: iload 50
        //   5356: istore 51
        //   5358: iload 49
        //   5360: istore 52
        //   5362: aload 88
        //   5364: getfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   5367: i2l
        //   5368: lstore 73
        //   5370: lload 73
        //   5372: lstore 71
        //   5374: iload_3
        //   5375: istore 16
        //   5377: iload_1
        //   5378: istore 17
        //   5380: iload 4
        //   5382: istore 18
        //   5384: iload 7
        //   5386: istore 15
        //   5388: iload 5
        //   5390: istore 19
        //   5392: iload 6
        //   5394: istore 20
        //   5396: iload 50
        //   5398: istore 53
        //   5400: iload 49
        //   5402: istore 54
        //   5404: iload_3
        //   5405: istore 9
        //   5407: iload_1
        //   5408: istore 10
        //   5410: iload 4
        //   5412: istore 11
        //   5414: iload 7
        //   5416: istore 12
        //   5418: iload 5
        //   5420: istore 13
        //   5422: iload 6
        //   5424: istore 14
        //   5426: iload 50
        //   5428: istore 51
        //   5430: iload 49
        //   5432: istore 52
        //   5434: aload 88
        //   5436: getfield 218	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   5439: getfield 223	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
        //   5442: ifeq +80 -> 5522
        //   5445: iload_3
        //   5446: istore 16
        //   5448: iload_1
        //   5449: istore 17
        //   5451: iload 4
        //   5453: istore 18
        //   5455: iload 7
        //   5457: istore 15
        //   5459: iload 5
        //   5461: istore 19
        //   5463: iload 6
        //   5465: istore 20
        //   5467: iload 50
        //   5469: istore 53
        //   5471: iload 49
        //   5473: istore 54
        //   5475: iload_3
        //   5476: istore 9
        //   5478: iload_1
        //   5479: istore 10
        //   5481: iload 4
        //   5483: istore 11
        //   5485: iload 7
        //   5487: istore 12
        //   5489: iload 5
        //   5491: istore 13
        //   5493: iload 6
        //   5495: istore 14
        //   5497: iload 50
        //   5499: istore 51
        //   5501: iload 49
        //   5503: istore 52
        //   5505: lload 73
        //   5507: aload 88
        //   5509: getfield 218	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   5512: getfield 223	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
        //   5515: i2l
        //   5516: bipush 32
        //   5518: lshl
        //   5519: lor
        //   5520: lstore 71
        //   5522: iload_3
        //   5523: istore 16
        //   5525: iload_1
        //   5526: istore 17
        //   5528: iload 4
        //   5530: istore 18
        //   5532: iload 7
        //   5534: istore 15
        //   5536: iload 5
        //   5538: istore 19
        //   5540: iload 6
        //   5542: istore 20
        //   5544: iload 50
        //   5546: istore 53
        //   5548: iload 49
        //   5550: istore 54
        //   5552: iload_3
        //   5553: istore 9
        //   5555: iload_1
        //   5556: istore 10
        //   5558: iload 4
        //   5560: istore 11
        //   5562: iload 7
        //   5564: istore 12
        //   5566: iload 5
        //   5568: istore 13
        //   5570: iload 6
        //   5572: istore 14
        //   5574: iload 50
        //   5576: istore 51
        //   5578: iload 49
        //   5580: istore 52
        //   5582: aload 87
        //   5584: lload 71
        //   5586: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   5589: invokevirtual 226	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   5592: ifne +74 -> 5666
        //   5595: iload_3
        //   5596: istore 16
        //   5598: iload_1
        //   5599: istore 17
        //   5601: iload 4
        //   5603: istore 18
        //   5605: iload 7
        //   5607: istore 15
        //   5609: iload 5
        //   5611: istore 19
        //   5613: iload 6
        //   5615: istore 20
        //   5617: iload 50
        //   5619: istore 53
        //   5621: iload 49
        //   5623: istore 54
        //   5625: iload_3
        //   5626: istore 9
        //   5628: iload_1
        //   5629: istore 10
        //   5631: iload 4
        //   5633: istore 11
        //   5635: iload 7
        //   5637: istore 12
        //   5639: iload 5
        //   5641: istore 13
        //   5643: iload 6
        //   5645: istore 14
        //   5647: iload 50
        //   5649: istore 51
        //   5651: iload 49
        //   5653: istore 52
        //   5655: aload 87
        //   5657: lload 71
        //   5659: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   5662: invokevirtual 196	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   5665: pop
        //   5666: iload_3
        //   5667: istore 16
        //   5669: iload_1
        //   5670: istore 17
        //   5672: iload 4
        //   5674: istore 18
        //   5676: iload 7
        //   5678: istore 15
        //   5680: iload 5
        //   5682: istore 19
        //   5684: iload 6
        //   5686: istore 20
        //   5688: iload 50
        //   5690: istore 53
        //   5692: iload 49
        //   5694: istore 54
        //   5696: iload_3
        //   5697: istore 9
        //   5699: iload_1
        //   5700: istore 10
        //   5702: iload 4
        //   5704: istore 11
        //   5706: iload 7
        //   5708: istore 12
        //   5710: iload 5
        //   5712: istore 13
        //   5714: iload 6
        //   5716: istore 14
        //   5718: iload 50
        //   5720: istore 51
        //   5722: iload 49
        //   5724: istore 52
        //   5726: aload 85
        //   5728: aload 88
        //   5730: getfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   5733: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   5736: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   5739: checkcast 63	java/util/ArrayList
        //   5742: astore 81
        //   5744: aload 81
        //   5746: astore 80
        //   5748: aload 81
        //   5750: ifnonnull +148 -> 5898
        //   5753: iload_3
        //   5754: istore 16
        //   5756: iload_1
        //   5757: istore 17
        //   5759: iload 4
        //   5761: istore 18
        //   5763: iload 7
        //   5765: istore 15
        //   5767: iload 5
        //   5769: istore 19
        //   5771: iload 6
        //   5773: istore 20
        //   5775: iload 50
        //   5777: istore 53
        //   5779: iload 49
        //   5781: istore 54
        //   5783: iload_3
        //   5784: istore 9
        //   5786: iload_1
        //   5787: istore 10
        //   5789: iload 4
        //   5791: istore 11
        //   5793: iload 7
        //   5795: istore 12
        //   5797: iload 5
        //   5799: istore 13
        //   5801: iload 6
        //   5803: istore 14
        //   5805: iload 50
        //   5807: istore 51
        //   5809: iload 49
        //   5811: istore 52
        //   5813: new 63	java/util/ArrayList
        //   5816: dup
        //   5817: invokespecial 64	java/util/ArrayList:<init>	()V
        //   5820: astore 80
        //   5822: iload_3
        //   5823: istore 16
        //   5825: iload_1
        //   5826: istore 17
        //   5828: iload 4
        //   5830: istore 18
        //   5832: iload 7
        //   5834: istore 15
        //   5836: iload 5
        //   5838: istore 19
        //   5840: iload 6
        //   5842: istore 20
        //   5844: iload 50
        //   5846: istore 53
        //   5848: iload 49
        //   5850: istore 54
        //   5852: iload_3
        //   5853: istore 9
        //   5855: iload_1
        //   5856: istore 10
        //   5858: iload 4
        //   5860: istore 11
        //   5862: iload 7
        //   5864: istore 12
        //   5866: iload 5
        //   5868: istore 13
        //   5870: iload 6
        //   5872: istore 14
        //   5874: iload 50
        //   5876: istore 51
        //   5878: iload 49
        //   5880: istore 52
        //   5882: aload 85
        //   5884: aload 88
        //   5886: getfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   5889: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   5892: aload 80
        //   5894: invokevirtual 234	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   5897: pop
        //   5898: iload_3
        //   5899: istore 16
        //   5901: iload_1
        //   5902: istore 17
        //   5904: iload 4
        //   5906: istore 18
        //   5908: iload 7
        //   5910: istore 15
        //   5912: iload 5
        //   5914: istore 19
        //   5916: iload 6
        //   5918: istore 20
        //   5920: iload 50
        //   5922: istore 53
        //   5924: iload 49
        //   5926: istore 54
        //   5928: iload_3
        //   5929: istore 9
        //   5931: iload_1
        //   5932: istore 10
        //   5934: iload 4
        //   5936: istore 11
        //   5938: iload 7
        //   5940: istore 12
        //   5942: iload 5
        //   5944: istore 13
        //   5946: iload 6
        //   5948: istore 14
        //   5950: iload 50
        //   5952: istore 51
        //   5954: iload 49
        //   5956: istore 52
        //   5958: aload 80
        //   5960: aload 88
        //   5962: invokevirtual 196	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   5965: pop
        //   5966: iload_3
        //   5967: istore 16
        //   5969: iload_1
        //   5970: istore 17
        //   5972: iload 4
        //   5974: istore 18
        //   5976: iload 7
        //   5978: istore 15
        //   5980: iload 5
        //   5982: istore 19
        //   5984: iload 6
        //   5986: istore 20
        //   5988: iload 50
        //   5990: istore 53
        //   5992: iload 49
        //   5994: istore 54
        //   5996: iload_3
        //   5997: istore 9
        //   5999: iload_1
        //   6000: istore 10
        //   6002: iload 4
        //   6004: istore 11
        //   6006: iload 7
        //   6008: istore 12
        //   6010: iload 5
        //   6012: istore 13
        //   6014: iload 6
        //   6016: istore 14
        //   6018: iload 50
        //   6020: istore 51
        //   6022: iload 49
        //   6024: istore 52
        //   6026: aload 88
        //   6028: aload 79
        //   6030: iconst_2
        //   6031: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   6034: putfield 237	org/vidogram/tgnet/TLRPC$Message:send_state	I
        //   6037: iload_3
        //   6038: istore 16
        //   6040: iload_1
        //   6041: istore 17
        //   6043: iload 4
        //   6045: istore 18
        //   6047: iload 7
        //   6049: istore 15
        //   6051: iload 5
        //   6053: istore 19
        //   6055: iload 6
        //   6057: istore 20
        //   6059: iload 50
        //   6061: istore 53
        //   6063: iload 49
        //   6065: istore 54
        //   6067: iload_3
        //   6068: istore 9
        //   6070: iload_1
        //   6071: istore 10
        //   6073: iload 4
        //   6075: istore 11
        //   6077: iload 7
        //   6079: istore 12
        //   6081: iload 5
        //   6083: istore 13
        //   6085: iload 6
        //   6087: istore 14
        //   6089: iload 50
        //   6091: istore 51
        //   6093: iload 49
        //   6095: istore 52
        //   6097: aload 88
        //   6099: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   6102: ifle +137 -> 6239
        //   6105: iload_3
        //   6106: istore 16
        //   6108: iload_1
        //   6109: istore 17
        //   6111: iload 4
        //   6113: istore 18
        //   6115: iload 7
        //   6117: istore 15
        //   6119: iload 5
        //   6121: istore 19
        //   6123: iload 6
        //   6125: istore 20
        //   6127: iload 50
        //   6129: istore 53
        //   6131: iload 49
        //   6133: istore 54
        //   6135: iload_3
        //   6136: istore 9
        //   6138: iload_1
        //   6139: istore 10
        //   6141: iload 4
        //   6143: istore 11
        //   6145: iload 7
        //   6147: istore 12
        //   6149: iload 5
        //   6151: istore 13
        //   6153: iload 6
        //   6155: istore 14
        //   6157: iload 50
        //   6159: istore 51
        //   6161: iload 49
        //   6163: istore 52
        //   6165: aload 88
        //   6167: getfield 237	org/vidogram/tgnet/TLRPC$Message:send_state	I
        //   6170: ifeq +69 -> 6239
        //   6173: iload_3
        //   6174: istore 16
        //   6176: iload_1
        //   6177: istore 17
        //   6179: iload 4
        //   6181: istore 18
        //   6183: iload 7
        //   6185: istore 15
        //   6187: iload 5
        //   6189: istore 19
        //   6191: iload 6
        //   6193: istore 20
        //   6195: iload 50
        //   6197: istore 53
        //   6199: iload 49
        //   6201: istore 54
        //   6203: iload_3
        //   6204: istore 9
        //   6206: iload_1
        //   6207: istore 10
        //   6209: iload 4
        //   6211: istore 11
        //   6213: iload 7
        //   6215: istore 12
        //   6217: iload 5
        //   6219: istore 13
        //   6221: iload 6
        //   6223: istore 14
        //   6225: iload 50
        //   6227: istore 51
        //   6229: iload 49
        //   6231: istore 52
        //   6233: aload 88
        //   6235: iconst_0
        //   6236: putfield 237	org/vidogram/tgnet/TLRPC$Message:send_state	I
        //   6239: iload 48
        //   6241: ifne +143 -> 6384
        //   6244: iload_3
        //   6245: istore 16
        //   6247: iload_1
        //   6248: istore 17
        //   6250: iload 4
        //   6252: istore 18
        //   6254: iload 7
        //   6256: istore 15
        //   6258: iload 5
        //   6260: istore 19
        //   6262: iload 6
        //   6264: istore 20
        //   6266: iload 50
        //   6268: istore 53
        //   6270: iload 49
        //   6272: istore 54
        //   6274: iload_3
        //   6275: istore 9
        //   6277: iload_1
        //   6278: istore 10
        //   6280: iload 4
        //   6282: istore 11
        //   6284: iload 7
        //   6286: istore 12
        //   6288: iload 5
        //   6290: istore 13
        //   6292: iload 6
        //   6294: istore 14
        //   6296: iload 50
        //   6298: istore 51
        //   6300: iload 49
        //   6302: istore 52
        //   6304: aload 79
        //   6306: iconst_5
        //   6307: invokevirtual 210	org/vidogram/SQLite/SQLiteCursor:isNull	(I)Z
        //   6310: ifne +74 -> 6384
        //   6313: iload_3
        //   6314: istore 16
        //   6316: iload_1
        //   6317: istore 17
        //   6319: iload 4
        //   6321: istore 18
        //   6323: iload 7
        //   6325: istore 15
        //   6327: iload 5
        //   6329: istore 19
        //   6331: iload 6
        //   6333: istore 20
        //   6335: iload 50
        //   6337: istore 53
        //   6339: iload 49
        //   6341: istore 54
        //   6343: iload_3
        //   6344: istore 9
        //   6346: iload_1
        //   6347: istore 10
        //   6349: iload 4
        //   6351: istore 11
        //   6353: iload 7
        //   6355: istore 12
        //   6357: iload 5
        //   6359: istore 13
        //   6361: iload 6
        //   6363: istore 14
        //   6365: iload 50
        //   6367: istore 51
        //   6369: iload 49
        //   6371: istore 52
        //   6373: aload 88
        //   6375: aload 79
        //   6377: iconst_5
        //   6378: invokevirtual 241	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   6381: putfield 244	org/vidogram/tgnet/TLRPC$Message:random_id	J
        //   6384: iload_3
        //   6385: istore 16
        //   6387: iload_1
        //   6388: istore 17
        //   6390: iload 4
        //   6392: istore 18
        //   6394: iload 7
        //   6396: istore 15
        //   6398: iload 5
        //   6400: istore 19
        //   6402: iload 6
        //   6404: istore 20
        //   6406: iload 50
        //   6408: istore 53
        //   6410: iload 49
        //   6412: istore 54
        //   6414: iload_3
        //   6415: istore 9
        //   6417: iload_1
        //   6418: istore 10
        //   6420: iload 4
        //   6422: istore 11
        //   6424: iload 7
        //   6426: istore 12
        //   6428: iload 5
        //   6430: istore 13
        //   6432: iload 6
        //   6434: istore 14
        //   6436: iload 50
        //   6438: istore 51
        //   6440: iload 49
        //   6442: istore 52
        //   6444: aload_0
        //   6445: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   6448: l2i
        //   6449: ifne -2770 -> 3679
        //   6452: iload_3
        //   6453: istore 16
        //   6455: iload_1
        //   6456: istore 17
        //   6458: iload 4
        //   6460: istore 18
        //   6462: iload 7
        //   6464: istore 15
        //   6466: iload 5
        //   6468: istore 19
        //   6470: iload 6
        //   6472: istore 20
        //   6474: iload 50
        //   6476: istore 53
        //   6478: iload 49
        //   6480: istore 54
        //   6482: iload_3
        //   6483: istore 9
        //   6485: iload_1
        //   6486: istore 10
        //   6488: iload 4
        //   6490: istore 11
        //   6492: iload 7
        //   6494: istore 12
        //   6496: iload 5
        //   6498: istore 13
        //   6500: iload 6
        //   6502: istore 14
        //   6504: iload 50
        //   6506: istore 51
        //   6508: iload 49
        //   6510: istore 52
        //   6512: aload 88
        //   6514: getfield 248	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   6517: ifnull -2838 -> 3679
        //   6520: iload_3
        //   6521: istore 16
        //   6523: iload_1
        //   6524: istore 17
        //   6526: iload 4
        //   6528: istore 18
        //   6530: iload 7
        //   6532: istore 15
        //   6534: iload 5
        //   6536: istore 19
        //   6538: iload 6
        //   6540: istore 20
        //   6542: iload 50
        //   6544: istore 53
        //   6546: iload 49
        //   6548: istore 54
        //   6550: iload_3
        //   6551: istore 9
        //   6553: iload_1
        //   6554: istore 10
        //   6556: iload 4
        //   6558: istore 11
        //   6560: iload 7
        //   6562: istore 12
        //   6564: iload 5
        //   6566: istore 13
        //   6568: iload 6
        //   6570: istore 14
        //   6572: iload 50
        //   6574: istore 51
        //   6576: iload 49
        //   6578: istore 52
        //   6580: aload 88
        //   6582: getfield 248	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   6585: getfield 254	org/vidogram/tgnet/TLRPC$MessageMedia:photo	Lorg/vidogram/tgnet/TLRPC$Photo;
        //   6588: astore 80
        //   6590: aload 80
        //   6592: ifnull -2913 -> 3679
        //   6595: iload_3
        //   6596: istore 9
        //   6598: iload_1
        //   6599: istore 10
        //   6601: iload 4
        //   6603: istore 11
        //   6605: iload 7
        //   6607: istore 12
        //   6609: iload 5
        //   6611: istore 13
        //   6613: iload 6
        //   6615: istore 14
        //   6617: iload 50
        //   6619: istore 51
        //   6621: iload 49
        //   6623: istore 52
        //   6625: aload_0
        //   6626: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   6629: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   6632: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   6635: ldc_w 256
        //   6638: iconst_1
        //   6639: anewarray 4	java/lang/Object
        //   6642: dup
        //   6643: iconst_0
        //   6644: aload 88
        //   6646: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   6649: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   6652: aastore
        //   6653: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   6656: iconst_0
        //   6657: anewarray 4	java/lang/Object
        //   6660: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   6663: astore 80
        //   6665: iload_3
        //   6666: istore 9
        //   6668: iload_1
        //   6669: istore 10
        //   6671: iload 4
        //   6673: istore 11
        //   6675: iload 7
        //   6677: istore 12
        //   6679: iload 5
        //   6681: istore 13
        //   6683: iload 6
        //   6685: istore 14
        //   6687: iload 50
        //   6689: istore 51
        //   6691: iload 49
        //   6693: istore 52
        //   6695: aload 80
        //   6697: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   6700: ifeq +44 -> 6744
        //   6703: iload_3
        //   6704: istore 9
        //   6706: iload_1
        //   6707: istore 10
        //   6709: iload 4
        //   6711: istore 11
        //   6713: iload 7
        //   6715: istore 12
        //   6717: iload 5
        //   6719: istore 13
        //   6721: iload 6
        //   6723: istore 14
        //   6725: iload 50
        //   6727: istore 51
        //   6729: iload 49
        //   6731: istore 52
        //   6733: aload 88
        //   6735: aload 80
        //   6737: iconst_0
        //   6738: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   6741: putfield 259	org/vidogram/tgnet/TLRPC$Message:destroyTime	I
        //   6744: iload_3
        //   6745: istore 9
        //   6747: iload_1
        //   6748: istore 10
        //   6750: iload 4
        //   6752: istore 11
        //   6754: iload 7
        //   6756: istore 12
        //   6758: iload 5
        //   6760: istore 13
        //   6762: iload 6
        //   6764: istore 14
        //   6766: iload 50
        //   6768: istore 51
        //   6770: iload 49
        //   6772: istore 52
        //   6774: aload 80
        //   6776: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   6779: goto -3100 -> 3679
        //   6782: astore 80
        //   6784: iload_3
        //   6785: istore 16
        //   6787: iload_1
        //   6788: istore 17
        //   6790: iload 4
        //   6792: istore 18
        //   6794: iload 7
        //   6796: istore 15
        //   6798: iload 5
        //   6800: istore 19
        //   6802: iload 6
        //   6804: istore 20
        //   6806: iload 50
        //   6808: istore 53
        //   6810: iload 49
        //   6812: istore 54
        //   6814: iload_3
        //   6815: istore 9
        //   6817: iload_1
        //   6818: istore 10
        //   6820: iload 4
        //   6822: istore 11
        //   6824: iload 7
        //   6826: istore 12
        //   6828: iload 5
        //   6830: istore 13
        //   6832: iload 6
        //   6834: istore 14
        //   6836: iload 50
        //   6838: istore 51
        //   6840: iload 49
        //   6842: istore 52
        //   6844: aload 80
        //   6846: invokestatic 265	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   6849: goto -3170 -> 3679
        //   6852: astore 79
        //   6854: iload 54
        //   6856: istore 49
        //   6858: iload 53
        //   6860: istore 50
        //   6862: iload 20
        //   6864: istore 8
        //   6866: iload 19
        //   6868: istore 6
        //   6870: iload 18
        //   6872: istore_3
        //   6873: iload 17
        //   6875: istore_2
        //   6876: iload 16
        //   6878: istore_1
        //   6879: iload_1
        //   6880: istore 9
        //   6882: iload_2
        //   6883: istore 10
        //   6885: iload_3
        //   6886: istore 11
        //   6888: iload 15
        //   6890: istore 12
        //   6892: iload 6
        //   6894: istore 13
        //   6896: iload 8
        //   6898: istore 14
        //   6900: iload 50
        //   6902: istore 51
        //   6904: iload 49
        //   6906: istore 52
        //   6908: aload 82
        //   6910: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   6913: invokevirtual 268	java/util/ArrayList:clear	()V
        //   6916: iload_1
        //   6917: istore 9
        //   6919: iload_2
        //   6920: istore 10
        //   6922: iload_3
        //   6923: istore 11
        //   6925: iload 15
        //   6927: istore 12
        //   6929: iload 6
        //   6931: istore 13
        //   6933: iload 8
        //   6935: istore 14
        //   6937: iload 50
        //   6939: istore 51
        //   6941: iload 49
        //   6943: istore 52
        //   6945: aload 82
        //   6947: getfield 271	org/vidogram/tgnet/TLRPC$TL_messages_messages:chats	Ljava/util/ArrayList;
        //   6950: invokevirtual 268	java/util/ArrayList:clear	()V
        //   6953: iload_1
        //   6954: istore 9
        //   6956: iload_2
        //   6957: istore 10
        //   6959: iload_3
        //   6960: istore 11
        //   6962: iload 15
        //   6964: istore 12
        //   6966: iload 6
        //   6968: istore 13
        //   6970: iload 8
        //   6972: istore 14
        //   6974: iload 50
        //   6976: istore 51
        //   6978: iload 49
        //   6980: istore 52
        //   6982: aload 82
        //   6984: getfield 274	org/vidogram/tgnet/TLRPC$TL_messages_messages:users	Ljava/util/ArrayList;
        //   6987: invokevirtual 268	java/util/ArrayList:clear	()V
        //   6990: iload_1
        //   6991: istore 9
        //   6993: iload_2
        //   6994: istore 10
        //   6996: iload_3
        //   6997: istore 11
        //   6999: iload 15
        //   7001: istore 12
        //   7003: iload 6
        //   7005: istore 13
        //   7007: iload 8
        //   7009: istore 14
        //   7011: iload 50
        //   7013: istore 51
        //   7015: iload 49
        //   7017: istore 52
        //   7019: aload 79
        //   7021: invokestatic 265	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   7024: invokestatic 280	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   7027: aload 82
        //   7029: aload_0
        //   7030: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   7033: iload_1
        //   7034: iload_2
        //   7035: aload_0
        //   7036: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   7039: iconst_1
        //   7040: aload_0
        //   7041: getfield 47	org/vidogram/messenger/MessagesStorage$49:val$classGuid	I
        //   7044: iload_3
        //   7045: iload 15
        //   7047: iload 6
        //   7049: iload 8
        //   7051: aload_0
        //   7052: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   7055: aload_0
        //   7056: getfield 37	org/vidogram/messenger/MessagesStorage$49:val$isChannel	Z
        //   7059: iload 50
        //   7061: aload_0
        //   7062: getfield 49	org/vidogram/messenger/MessagesStorage$49:val$loadIndex	I
        //   7065: iload 49
        //   7067: invokevirtual 284	org/vidogram/messenger/MessagesController:processLoadedMessages	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIZIIIIIIZZIZ)V
        //   7070: return
        //   7071: iconst_1
        //   7072: istore 22
        //   7074: goto -6824 -> 250
        //   7077: iload 31
        //   7079: istore_2
        //   7080: iload 20
        //   7082: istore 14
        //   7084: iload 29
        //   7086: istore 5
        //   7088: iload 27
        //   7090: istore 7
        //   7092: iload 65
        //   7094: istore 52
        //   7096: iload 58
        //   7098: istore 50
        //   7100: iload 32
        //   7102: istore_3
        //   7103: iload 23
        //   7105: istore 15
        //   7107: iload 30
        //   7109: istore 6
        //   7111: iload 28
        //   7113: istore 8
        //   7115: iload 66
        //   7117: istore 53
        //   7119: iload 59
        //   7121: istore 51
        //   7123: lload 73
        //   7125: lstore 71
        //   7127: iload 9
        //   7129: istore 18
        //   7131: iload 4
        //   7133: istore 19
        //   7135: iload 42
        //   7137: istore 17
        //   7139: iload 11
        //   7141: istore 16
        //   7143: iload 43
        //   7145: istore 12
        //   7147: iload 68
        //   7149: istore 49
        //   7151: aload_0
        //   7152: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   7155: iconst_1
        //   7156: if_icmpeq +12791 -> 19947
        //   7159: iload 31
        //   7161: istore_2
        //   7162: iload 20
        //   7164: istore 14
        //   7166: iload 29
        //   7168: istore 5
        //   7170: iload 27
        //   7172: istore 7
        //   7174: iload 65
        //   7176: istore 52
        //   7178: iload 58
        //   7180: istore 50
        //   7182: iload 32
        //   7184: istore_3
        //   7185: iload 23
        //   7187: istore 15
        //   7189: iload 30
        //   7191: istore 6
        //   7193: iload 28
        //   7195: istore 8
        //   7197: iload 66
        //   7199: istore 53
        //   7201: iload 59
        //   7203: istore 51
        //   7205: lload 73
        //   7207: lstore 71
        //   7209: iload 9
        //   7211: istore 18
        //   7213: iload 4
        //   7215: istore 19
        //   7217: iload 42
        //   7219: istore 17
        //   7221: iload 11
        //   7223: istore 16
        //   7225: iload 43
        //   7227: istore 12
        //   7229: iload 68
        //   7231: istore 49
        //   7233: aload_0
        //   7234: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   7237: iconst_3
        //   7238: if_icmpeq +12709 -> 19947
        //   7241: iload 31
        //   7243: istore_2
        //   7244: iload 20
        //   7246: istore 14
        //   7248: iload 29
        //   7250: istore 5
        //   7252: iload 27
        //   7254: istore 7
        //   7256: iload 65
        //   7258: istore 52
        //   7260: iload 58
        //   7262: istore 50
        //   7264: iload 32
        //   7266: istore_3
        //   7267: iload 23
        //   7269: istore 15
        //   7271: iload 30
        //   7273: istore 6
        //   7275: iload 28
        //   7277: istore 8
        //   7279: iload 66
        //   7281: istore 53
        //   7283: iload 59
        //   7285: istore 51
        //   7287: lload 73
        //   7289: lstore 71
        //   7291: iload 9
        //   7293: istore 18
        //   7295: iload 4
        //   7297: istore 19
        //   7299: iload 42
        //   7301: istore 17
        //   7303: iload 11
        //   7305: istore 16
        //   7307: iload 43
        //   7309: istore 12
        //   7311: iload 68
        //   7313: istore 49
        //   7315: aload_0
        //   7316: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   7319: iconst_4
        //   7320: if_icmpeq +12627 -> 19947
        //   7323: iload 31
        //   7325: istore_2
        //   7326: iload 20
        //   7328: istore 14
        //   7330: iload 29
        //   7332: istore 5
        //   7334: iload 27
        //   7336: istore 7
        //   7338: iload 65
        //   7340: istore 52
        //   7342: iload 58
        //   7344: istore 50
        //   7346: iload 32
        //   7348: istore_3
        //   7349: iload 23
        //   7351: istore 15
        //   7353: iload 30
        //   7355: istore 6
        //   7357: iload 28
        //   7359: istore 8
        //   7361: iload 66
        //   7363: istore 53
        //   7365: iload 59
        //   7367: istore 51
        //   7369: lload 73
        //   7371: lstore 71
        //   7373: iload 9
        //   7375: istore 18
        //   7377: iload 4
        //   7379: istore 19
        //   7381: iload 42
        //   7383: istore 17
        //   7385: iload 11
        //   7387: istore 16
        //   7389: iload 43
        //   7391: istore 12
        //   7393: iload 68
        //   7395: istore 49
        //   7397: aload_0
        //   7398: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   7401: ifne +12546 -> 19947
        //   7404: iload 31
        //   7406: istore_2
        //   7407: iload 20
        //   7409: istore 14
        //   7411: iload 29
        //   7413: istore 5
        //   7415: iload 27
        //   7417: istore 7
        //   7419: iload 65
        //   7421: istore 52
        //   7423: iload 58
        //   7425: istore 50
        //   7427: iload 32
        //   7429: istore_3
        //   7430: iload 23
        //   7432: istore 15
        //   7434: iload 30
        //   7436: istore 6
        //   7438: iload 28
        //   7440: istore 8
        //   7442: iload 66
        //   7444: istore 53
        //   7446: iload 59
        //   7448: istore 51
        //   7450: lload 73
        //   7452: lstore 71
        //   7454: iload 9
        //   7456: istore 11
        //   7458: iload 41
        //   7460: istore 12
        //   7462: iload 69
        //   7464: istore 49
        //   7466: aload_0
        //   7467: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   7470: iconst_2
        //   7471: if_icmpne +12619 -> 20090
        //   7474: iload 31
        //   7476: istore_2
        //   7477: iload 20
        //   7479: istore 14
        //   7481: iload 29
        //   7483: istore 5
        //   7485: iload 27
        //   7487: istore 7
        //   7489: iload 65
        //   7491: istore 52
        //   7493: iload 58
        //   7495: istore 50
        //   7497: iload 32
        //   7499: istore_3
        //   7500: iload 23
        //   7502: istore 15
        //   7504: iload 30
        //   7506: istore 6
        //   7508: iload 28
        //   7510: istore 8
        //   7512: iload 66
        //   7514: istore 53
        //   7516: iload 59
        //   7518: istore 51
        //   7520: aload_0
        //   7521: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   7524: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   7527: new 73	java/lang/StringBuilder
        //   7530: dup
        //   7531: invokespecial 74	java/lang/StringBuilder:<init>	()V
        //   7534: ldc 76
        //   7536: invokevirtual 80	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   7539: aload_0
        //   7540: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   7543: invokevirtual 83	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
        //   7546: invokevirtual 87	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   7549: iconst_0
        //   7550: anewarray 4	java/lang/Object
        //   7553: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   7556: astore 79
        //   7558: iload 31
        //   7560: istore_2
        //   7561: iload 20
        //   7563: istore 14
        //   7565: iload 29
        //   7567: istore 5
        //   7569: iload 27
        //   7571: istore 7
        //   7573: iload 65
        //   7575: istore 52
        //   7577: iload 58
        //   7579: istore 50
        //   7581: iload 32
        //   7583: istore_3
        //   7584: iload 23
        //   7586: istore 15
        //   7588: iload 30
        //   7590: istore 6
        //   7592: iload 28
        //   7594: istore 8
        //   7596: iload 66
        //   7598: istore 53
        //   7600: iload 59
        //   7602: istore 51
        //   7604: aload 79
        //   7606: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   7609: ifeq +12410 -> 20019
        //   7612: iload 31
        //   7614: istore_2
        //   7615: iload 20
        //   7617: istore 14
        //   7619: iload 29
        //   7621: istore 5
        //   7623: iload 27
        //   7625: istore 7
        //   7627: iload 65
        //   7629: istore 52
        //   7631: iload 58
        //   7633: istore 50
        //   7635: iload 32
        //   7637: istore_3
        //   7638: iload 23
        //   7640: istore 15
        //   7642: iload 30
        //   7644: istore 6
        //   7646: iload 28
        //   7648: istore 8
        //   7650: iload 66
        //   7652: istore 53
        //   7654: iload 59
        //   7656: istore 51
        //   7658: aload 79
        //   7660: iconst_0
        //   7661: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   7664: istore 9
        //   7666: iload 9
        //   7668: i2l
        //   7669: lstore 71
        //   7671: iload 9
        //   7673: istore_2
        //   7674: iload 20
        //   7676: istore 14
        //   7678: iload 29
        //   7680: istore 5
        //   7682: iload 27
        //   7684: istore 7
        //   7686: iload 65
        //   7688: istore 52
        //   7690: iload 58
        //   7692: istore 50
        //   7694: iload 9
        //   7696: istore_3
        //   7697: iload 23
        //   7699: istore 15
        //   7701: iload 30
        //   7703: istore 6
        //   7705: iload 28
        //   7707: istore 8
        //   7709: iload 66
        //   7711: istore 53
        //   7713: iload 59
        //   7715: istore 51
        //   7717: aload 79
        //   7719: iconst_1
        //   7720: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   7723: istore 10
        //   7725: iload 9
        //   7727: istore_2
        //   7728: iload 20
        //   7730: istore 14
        //   7732: iload 10
        //   7734: istore 5
        //   7736: iload 27
        //   7738: istore 7
        //   7740: iload 65
        //   7742: istore 52
        //   7744: iload 58
        //   7746: istore 50
        //   7748: iload 9
        //   7750: istore_3
        //   7751: iload 23
        //   7753: istore 15
        //   7755: iload 10
        //   7757: istore 6
        //   7759: iload 28
        //   7761: istore 8
        //   7763: iload 66
        //   7765: istore 53
        //   7767: iload 59
        //   7769: istore 51
        //   7771: aload 79
        //   7773: iconst_2
        //   7774: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   7777: istore 11
        //   7779: iload 11
        //   7781: istore 8
        //   7783: iconst_1
        //   7784: istore 49
        //   7786: lload 71
        //   7788: lconst_0
        //   7789: lcmp
        //   7790: ifeq +12215 -> 20005
        //   7793: iload 21
        //   7795: ifeq +12210 -> 20005
        //   7798: lload 71
        //   7800: iload 21
        //   7802: i2l
        //   7803: bipush 32
        //   7805: lshl
        //   7806: lor
        //   7807: lstore 71
        //   7809: iload 9
        //   7811: istore_3
        //   7812: iload 9
        //   7814: istore_2
        //   7815: iload_3
        //   7816: istore 9
        //   7818: iload_2
        //   7819: istore 5
        //   7821: iload_2
        //   7822: istore 6
        //   7824: aload 79
        //   7826: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   7829: iload 49
        //   7831: ifne +564 -> 8395
        //   7834: iload_2
        //   7835: istore 5
        //   7837: iload_2
        //   7838: istore 6
        //   7840: aload_0
        //   7841: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   7844: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   7847: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   7850: ldc_w 286
        //   7853: iconst_1
        //   7854: anewarray 4	java/lang/Object
        //   7857: dup
        //   7858: iconst_0
        //   7859: aload_0
        //   7860: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   7863: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   7866: aastore
        //   7867: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   7870: iconst_0
        //   7871: anewarray 4	java/lang/Object
        //   7874: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   7877: astore 79
        //   7879: iload_2
        //   7880: istore 5
        //   7882: iload_2
        //   7883: istore 6
        //   7885: aload 79
        //   7887: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   7890: ifeq +12105 -> 19995
        //   7893: iload_2
        //   7894: istore 5
        //   7896: iload_2
        //   7897: istore 6
        //   7899: aload 79
        //   7901: iconst_0
        //   7902: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   7905: istore 12
        //   7907: iload 12
        //   7909: istore_2
        //   7910: iload 20
        //   7912: istore 14
        //   7914: iload 10
        //   7916: istore 5
        //   7918: iload 8
        //   7920: istore 7
        //   7922: iload 65
        //   7924: istore 52
        //   7926: iload 49
        //   7928: istore 50
        //   7930: iload 12
        //   7932: istore_3
        //   7933: iload 23
        //   7935: istore 15
        //   7937: iload 10
        //   7939: istore 6
        //   7941: iload 66
        //   7943: istore 53
        //   7945: iload 49
        //   7947: istore 51
        //   7949: aload 79
        //   7951: iconst_1
        //   7952: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   7955: istore 16
        //   7957: iload 12
        //   7959: istore_2
        //   7960: iload 20
        //   7962: istore 14
        //   7964: iload 10
        //   7966: istore 5
        //   7968: iload 16
        //   7970: istore 7
        //   7972: iload 65
        //   7974: istore 52
        //   7976: iload 49
        //   7978: istore 50
        //   7980: iload 12
        //   7982: istore_3
        //   7983: iload 23
        //   7985: istore 15
        //   7987: iload 10
        //   7989: istore 6
        //   7991: iload 16
        //   7993: istore 8
        //   7995: iload 66
        //   7997: istore 53
        //   7999: iload 49
        //   8001: istore 51
        //   8003: aload 79
        //   8005: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8008: iload 12
        //   8010: ifeq +11970 -> 19980
        //   8013: iload 12
        //   8015: istore_2
        //   8016: iload 20
        //   8018: istore 14
        //   8020: iload 10
        //   8022: istore 5
        //   8024: iload 16
        //   8026: istore 7
        //   8028: iload 65
        //   8030: istore 52
        //   8032: iload 49
        //   8034: istore 50
        //   8036: iload 12
        //   8038: istore_3
        //   8039: iload 23
        //   8041: istore 15
        //   8043: iload 10
        //   8045: istore 6
        //   8047: iload 16
        //   8049: istore 8
        //   8051: iload 66
        //   8053: istore 53
        //   8055: iload 49
        //   8057: istore 51
        //   8059: aload_0
        //   8060: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8063: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   8066: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   8069: ldc_w 288
        //   8072: iconst_2
        //   8073: anewarray 4	java/lang/Object
        //   8076: dup
        //   8077: iconst_0
        //   8078: aload_0
        //   8079: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   8082: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   8085: aastore
        //   8086: dup
        //   8087: iconst_1
        //   8088: iload 12
        //   8090: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   8093: aastore
        //   8094: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   8097: iconst_0
        //   8098: anewarray 4	java/lang/Object
        //   8101: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   8104: astore 79
        //   8106: iload 10
        //   8108: istore 13
        //   8110: iload 12
        //   8112: istore_2
        //   8113: iload 20
        //   8115: istore 14
        //   8117: iload 10
        //   8119: istore 5
        //   8121: iload 16
        //   8123: istore 7
        //   8125: iload 65
        //   8127: istore 52
        //   8129: iload 49
        //   8131: istore 50
        //   8133: iload 12
        //   8135: istore_3
        //   8136: iload 23
        //   8138: istore 15
        //   8140: iload 10
        //   8142: istore 6
        //   8144: iload 16
        //   8146: istore 8
        //   8148: iload 66
        //   8150: istore 53
        //   8152: iload 49
        //   8154: istore 51
        //   8156: aload 79
        //   8158: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   8161: ifeq +57 -> 8218
        //   8164: iload 12
        //   8166: istore_2
        //   8167: iload 20
        //   8169: istore 14
        //   8171: iload 10
        //   8173: istore 5
        //   8175: iload 16
        //   8177: istore 7
        //   8179: iload 65
        //   8181: istore 52
        //   8183: iload 49
        //   8185: istore 50
        //   8187: iload 12
        //   8189: istore_3
        //   8190: iload 23
        //   8192: istore 15
        //   8194: iload 10
        //   8196: istore 6
        //   8198: iload 16
        //   8200: istore 8
        //   8202: iload 66
        //   8204: istore 53
        //   8206: iload 49
        //   8208: istore 51
        //   8210: aload 79
        //   8212: iconst_0
        //   8213: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   8216: istore 13
        //   8218: iload 12
        //   8220: istore_2
        //   8221: iload 20
        //   8223: istore 14
        //   8225: iload 13
        //   8227: istore 5
        //   8229: iload 16
        //   8231: istore 7
        //   8233: iload 65
        //   8235: istore 52
        //   8237: iload 49
        //   8239: istore 50
        //   8241: iload 12
        //   8243: istore_3
        //   8244: iload 23
        //   8246: istore 15
        //   8248: iload 13
        //   8250: istore 6
        //   8252: iload 16
        //   8254: istore 8
        //   8256: iload 66
        //   8258: istore 53
        //   8260: iload 49
        //   8262: istore 51
        //   8264: aload 79
        //   8266: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8269: iload 9
        //   8271: istore 11
        //   8273: iload 16
        //   8275: istore 10
        //   8277: goto +11813 -> 20090
        //   8280: iload 12
        //   8282: istore_2
        //   8283: iload 20
        //   8285: istore 14
        //   8287: iload 13
        //   8289: istore 5
        //   8291: iload 10
        //   8293: istore 7
        //   8295: iload 65
        //   8297: istore 52
        //   8299: iload 49
        //   8301: istore 50
        //   8303: iload 12
        //   8305: istore_3
        //   8306: iload 23
        //   8308: istore 15
        //   8310: iload 13
        //   8312: istore 6
        //   8314: iload 10
        //   8316: istore 8
        //   8318: iload 66
        //   8320: istore 53
        //   8322: iload 49
        //   8324: istore 51
        //   8326: iload 4
        //   8328: iload 13
        //   8330: bipush 10
        //   8332: iadd
        //   8333: invokestatic 294	java/lang/Math:max	(II)I
        //   8336: istore 9
        //   8338: iload 9
        //   8340: istore_2
        //   8341: iload 11
        //   8343: istore 18
        //   8345: iload_2
        //   8346: istore 19
        //   8348: iload 12
        //   8350: istore 17
        //   8352: iload 13
        //   8354: istore 16
        //   8356: iload 10
        //   8358: istore 12
        //   8360: iload 13
        //   8362: iload 22
        //   8364: if_icmpge +11583 -> 19947
        //   8367: iconst_0
        //   8368: istore 5
        //   8370: iconst_0
        //   8371: istore 4
        //   8373: iconst_0
        //   8374: istore 49
        //   8376: lconst_0
        //   8377: lstore 71
        //   8379: iload_2
        //   8380: istore_3
        //   8381: iload 11
        //   8383: istore_2
        //   8384: iload 24
        //   8386: istore 9
        //   8388: iload 10
        //   8390: istore 6
        //   8392: goto -7311 -> 1081
        //   8395: iload 9
        //   8397: ifne +235 -> 8632
        //   8400: iconst_0
        //   8401: istore 7
        //   8403: iload_2
        //   8404: istore 5
        //   8406: iload_2
        //   8407: istore 6
        //   8409: aload_0
        //   8410: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8413: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   8416: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   8419: ldc_w 296
        //   8422: iconst_1
        //   8423: anewarray 4	java/lang/Object
        //   8426: dup
        //   8427: iconst_0
        //   8428: aload_0
        //   8429: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   8432: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   8435: aastore
        //   8436: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   8439: iconst_0
        //   8440: anewarray 4	java/lang/Object
        //   8443: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   8446: astore 79
        //   8448: iload_2
        //   8449: istore 5
        //   8451: iload_2
        //   8452: istore 6
        //   8454: aload 79
        //   8456: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   8459: ifeq +17 -> 8476
        //   8462: iload_2
        //   8463: istore 5
        //   8465: iload_2
        //   8466: istore 6
        //   8468: aload 79
        //   8470: iconst_0
        //   8471: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   8474: istore 7
        //   8476: iload_2
        //   8477: istore 5
        //   8479: iload_2
        //   8480: istore 6
        //   8482: aload 79
        //   8484: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8487: iload 9
        //   8489: istore 5
        //   8491: iload_2
        //   8492: istore_3
        //   8493: lload 71
        //   8495: lstore 73
        //   8497: iload 7
        //   8499: iload 10
        //   8501: if_icmpne +11606 -> 20107
        //   8504: iload_2
        //   8505: istore 5
        //   8507: iload_2
        //   8508: istore 6
        //   8510: aload_0
        //   8511: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8514: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   8517: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   8520: ldc_w 298
        //   8523: iconst_1
        //   8524: anewarray 4	java/lang/Object
        //   8527: dup
        //   8528: iconst_0
        //   8529: aload_0
        //   8530: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   8533: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   8536: aastore
        //   8537: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   8540: iconst_0
        //   8541: anewarray 4	java/lang/Object
        //   8544: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   8547: astore 79
        //   8549: iload_2
        //   8550: istore_3
        //   8551: iload_2
        //   8552: istore 5
        //   8554: iload_2
        //   8555: istore 6
        //   8557: aload 79
        //   8559: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   8562: ifeq +48 -> 8610
        //   8565: iload_2
        //   8566: istore 5
        //   8568: iload_2
        //   8569: istore 6
        //   8571: aload 79
        //   8573: iconst_0
        //   8574: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   8577: istore_2
        //   8578: iload_2
        //   8579: i2l
        //   8580: lstore 71
        //   8582: lload 71
        //   8584: lconst_0
        //   8585: lcmp
        //   8586: ifeq +11386 -> 19972
        //   8589: iload 21
        //   8591: ifeq +11381 -> 19972
        //   8594: lload 71
        //   8596: iload 21
        //   8598: i2l
        //   8599: bipush 32
        //   8601: lshl
        //   8602: lor
        //   8603: lstore 71
        //   8605: iload_2
        //   8606: istore_3
        //   8607: iload_2
        //   8608: istore 9
        //   8610: iload_3
        //   8611: istore 5
        //   8613: iload_3
        //   8614: istore 6
        //   8616: aload 79
        //   8618: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8621: iload 9
        //   8623: istore 5
        //   8625: lload 71
        //   8627: lstore 73
        //   8629: goto +11478 -> 20107
        //   8632: iload_2
        //   8633: istore 5
        //   8635: iload_2
        //   8636: istore 6
        //   8638: aload_0
        //   8639: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8642: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   8645: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   8648: ldc_w 300
        //   8651: iconst_3
        //   8652: anewarray 4	java/lang/Object
        //   8655: dup
        //   8656: iconst_0
        //   8657: aload_0
        //   8658: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   8661: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   8664: aastore
        //   8665: dup
        //   8666: iconst_1
        //   8667: iload 9
        //   8669: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   8672: aastore
        //   8673: dup
        //   8674: iconst_2
        //   8675: iload 9
        //   8677: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   8680: aastore
        //   8681: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   8684: iconst_0
        //   8685: anewarray 4	java/lang/Object
        //   8688: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   8691: astore 79
        //   8693: iload_2
        //   8694: istore 5
        //   8696: iload_2
        //   8697: istore 6
        //   8699: aload 79
        //   8701: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   8704: ifne +192 -> 8896
        //   8707: iconst_1
        //   8708: istore 7
        //   8710: iload_2
        //   8711: istore 5
        //   8713: iload_2
        //   8714: istore 6
        //   8716: aload 79
        //   8718: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8721: iload 9
        //   8723: istore_3
        //   8724: lload 71
        //   8726: lstore 73
        //   8728: iload 7
        //   8730: ifeq +145 -> 8875
        //   8733: iload_2
        //   8734: istore 5
        //   8736: iload_2
        //   8737: istore 6
        //   8739: aload_0
        //   8740: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   8743: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   8746: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   8749: ldc_w 302
        //   8752: iconst_2
        //   8753: anewarray 4	java/lang/Object
        //   8756: dup
        //   8757: iconst_0
        //   8758: aload_0
        //   8759: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   8762: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   8765: aastore
        //   8766: dup
        //   8767: iconst_1
        //   8768: iload 9
        //   8770: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   8773: aastore
        //   8774: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   8777: iconst_0
        //   8778: anewarray 4	java/lang/Object
        //   8781: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   8784: astore 79
        //   8786: iload_2
        //   8787: istore 5
        //   8789: iload_2
        //   8790: istore 6
        //   8792: aload 79
        //   8794: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   8797: ifeq +60 -> 8857
        //   8800: iload_2
        //   8801: istore 5
        //   8803: iload_2
        //   8804: istore 6
        //   8806: aload 79
        //   8808: iconst_0
        //   8809: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   8812: istore_3
        //   8813: iload_3
        //   8814: i2l
        //   8815: lstore 73
        //   8817: iload_3
        //   8818: istore 9
        //   8820: lload 73
        //   8822: lstore 71
        //   8824: lload 73
        //   8826: lconst_0
        //   8827: lcmp
        //   8828: ifeq +29 -> 8857
        //   8831: iload_3
        //   8832: istore 9
        //   8834: lload 73
        //   8836: lstore 71
        //   8838: iload 21
        //   8840: ifeq +17 -> 8857
        //   8843: lload 73
        //   8845: iload 21
        //   8847: i2l
        //   8848: bipush 32
        //   8850: lshl
        //   8851: lor
        //   8852: lstore 71
        //   8854: iload_3
        //   8855: istore 9
        //   8857: iload_2
        //   8858: istore 5
        //   8860: iload_2
        //   8861: istore 6
        //   8863: aload 79
        //   8865: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8868: lload 71
        //   8870: lstore 73
        //   8872: iload 9
        //   8874: istore_3
        //   8875: lload 73
        //   8877: lstore 71
        //   8879: iload_3
        //   8880: istore 11
        //   8882: iload_2
        //   8883: istore 12
        //   8885: iload 10
        //   8887: istore 13
        //   8889: iload 8
        //   8891: istore 10
        //   8893: goto +11197 -> 20090
        //   8896: iconst_0
        //   8897: istore 7
        //   8899: goto -189 -> 8710
        //   8902: iload 13
        //   8904: iload 4
        //   8906: isub
        //   8907: istore 9
        //   8909: iload 4
        //   8911: bipush 10
        //   8913: iadd
        //   8914: istore_3
        //   8915: iload 11
        //   8917: istore_2
        //   8918: iload 12
        //   8920: istore 4
        //   8922: iload 13
        //   8924: istore 5
        //   8926: iload 10
        //   8928: istore 6
        //   8930: goto -7849 -> 1081
        //   8933: iconst_0
        //   8934: istore 50
        //   8936: goto -7619 -> 1317
        //   8939: iload_3
        //   8940: istore 18
        //   8942: iload 4
        //   8944: istore 15
        //   8946: iload 13
        //   8948: istore 8
        //   8950: iload 5
        //   8952: istore 14
        //   8954: iload 6
        //   8956: istore 11
        //   8958: iload 56
        //   8960: istore 51
        //   8962: iload 49
        //   8964: istore 53
        //   8966: iload_3
        //   8967: istore 19
        //   8969: iload 4
        //   8971: istore 17
        //   8973: iload 20
        //   8975: istore 10
        //   8977: iload 5
        //   8979: istore 16
        //   8981: iload 6
        //   8983: istore 12
        //   8985: iload 57
        //   8987: istore 52
        //   8989: iload 49
        //   8991: istore 54
        //   8993: aload 79
        //   8995: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   8998: iload_3
        //   8999: istore 18
        //   9001: iload 4
        //   9003: istore 15
        //   9005: iload 13
        //   9007: istore 8
        //   9009: iload 5
        //   9011: istore 14
        //   9013: iload 6
        //   9015: istore 11
        //   9017: iload 56
        //   9019: istore 51
        //   9021: iload 49
        //   9023: istore 53
        //   9025: iload_3
        //   9026: istore 19
        //   9028: iload 4
        //   9030: istore 17
        //   9032: iload 20
        //   9034: istore 10
        //   9036: iload 5
        //   9038: istore 16
        //   9040: iload 6
        //   9042: istore 12
        //   9044: iload 57
        //   9046: istore 52
        //   9048: iload 49
        //   9050: istore 54
        //   9052: aload_0
        //   9053: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   9056: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   9059: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   9062: ldc_w 304
        //   9065: iconst_1
        //   9066: anewarray 4	java/lang/Object
        //   9069: dup
        //   9070: iconst_0
        //   9071: aload_0
        //   9072: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   9075: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   9078: aastore
        //   9079: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   9082: iconst_0
        //   9083: anewarray 4	java/lang/Object
        //   9086: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   9089: astore 79
        //   9091: iload_3
        //   9092: istore 18
        //   9094: iload 4
        //   9096: istore 15
        //   9098: iload 13
        //   9100: istore 8
        //   9102: iload 5
        //   9104: istore 14
        //   9106: iload 6
        //   9108: istore 11
        //   9110: iload 56
        //   9112: istore 51
        //   9114: iload 49
        //   9116: istore 53
        //   9118: iload_3
        //   9119: istore 19
        //   9121: iload 4
        //   9123: istore 17
        //   9125: iload 20
        //   9127: istore 10
        //   9129: iload 5
        //   9131: istore 16
        //   9133: iload 6
        //   9135: istore 12
        //   9137: iload 57
        //   9139: istore 52
        //   9141: iload 49
        //   9143: istore 54
        //   9145: aload 79
        //   9147: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   9150: ifeq +504 -> 9654
        //   9153: iload_3
        //   9154: istore 18
        //   9156: iload 4
        //   9158: istore 15
        //   9160: iload 13
        //   9162: istore 8
        //   9164: iload 5
        //   9166: istore 14
        //   9168: iload 6
        //   9170: istore 11
        //   9172: iload 56
        //   9174: istore 51
        //   9176: iload 49
        //   9178: istore 53
        //   9180: iload_3
        //   9181: istore 19
        //   9183: iload 4
        //   9185: istore 17
        //   9187: iload 20
        //   9189: istore 10
        //   9191: iload 5
        //   9193: istore 16
        //   9195: iload 6
        //   9197: istore 12
        //   9199: iload 57
        //   9201: istore 52
        //   9203: iload 49
        //   9205: istore 54
        //   9207: aload 79
        //   9209: iconst_0
        //   9210: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   9213: istore 24
        //   9215: iload 24
        //   9217: ifeq +437 -> 9654
        //   9220: iload_3
        //   9221: istore 18
        //   9223: iload 4
        //   9225: istore 15
        //   9227: iload 13
        //   9229: istore 8
        //   9231: iload 5
        //   9233: istore 14
        //   9235: iload 6
        //   9237: istore 11
        //   9239: iload 56
        //   9241: istore 51
        //   9243: iload 49
        //   9245: istore 53
        //   9247: iload_3
        //   9248: istore 19
        //   9250: iload 4
        //   9252: istore 17
        //   9254: iload 20
        //   9256: istore 10
        //   9258: iload 5
        //   9260: istore 16
        //   9262: iload 6
        //   9264: istore 12
        //   9266: iload 57
        //   9268: istore 52
        //   9270: iload 49
        //   9272: istore 54
        //   9274: aload_0
        //   9275: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   9278: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   9281: ldc_w 306
        //   9284: invokevirtual 310	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
        //   9287: astore 80
        //   9289: iload_3
        //   9290: istore 18
        //   9292: iload 4
        //   9294: istore 15
        //   9296: iload 13
        //   9298: istore 8
        //   9300: iload 5
        //   9302: istore 14
        //   9304: iload 6
        //   9306: istore 11
        //   9308: iload 56
        //   9310: istore 51
        //   9312: iload 49
        //   9314: istore 53
        //   9316: iload_3
        //   9317: istore 19
        //   9319: iload 4
        //   9321: istore 17
        //   9323: iload 20
        //   9325: istore 10
        //   9327: iload 5
        //   9329: istore 16
        //   9331: iload 6
        //   9333: istore 12
        //   9335: iload 57
        //   9337: istore 52
        //   9339: iload 49
        //   9341: istore 54
        //   9343: aload 80
        //   9345: invokevirtual 315	org/vidogram/SQLite/SQLitePreparedStatement:requery	()V
        //   9348: iload_3
        //   9349: istore 18
        //   9351: iload 4
        //   9353: istore 15
        //   9355: iload 13
        //   9357: istore 8
        //   9359: iload 5
        //   9361: istore 14
        //   9363: iload 6
        //   9365: istore 11
        //   9367: iload 56
        //   9369: istore 51
        //   9371: iload 49
        //   9373: istore 53
        //   9375: iload_3
        //   9376: istore 19
        //   9378: iload 4
        //   9380: istore 17
        //   9382: iload 20
        //   9384: istore 10
        //   9386: iload 5
        //   9388: istore 16
        //   9390: iload 6
        //   9392: istore 12
        //   9394: iload 57
        //   9396: istore 52
        //   9398: iload 49
        //   9400: istore 54
        //   9402: aload 80
        //   9404: iconst_1
        //   9405: aload_0
        //   9406: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   9409: invokevirtual 319	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
        //   9412: iload_3
        //   9413: istore 18
        //   9415: iload 4
        //   9417: istore 15
        //   9419: iload 13
        //   9421: istore 8
        //   9423: iload 5
        //   9425: istore 14
        //   9427: iload 6
        //   9429: istore 11
        //   9431: iload 56
        //   9433: istore 51
        //   9435: iload 49
        //   9437: istore 53
        //   9439: iload_3
        //   9440: istore 19
        //   9442: iload 4
        //   9444: istore 17
        //   9446: iload 20
        //   9448: istore 10
        //   9450: iload 5
        //   9452: istore 16
        //   9454: iload 6
        //   9456: istore 12
        //   9458: iload 57
        //   9460: istore 52
        //   9462: iload 49
        //   9464: istore 54
        //   9466: aload 80
        //   9468: iconst_2
        //   9469: iconst_0
        //   9470: invokevirtual 323	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
        //   9473: iload_3
        //   9474: istore 18
        //   9476: iload 4
        //   9478: istore 15
        //   9480: iload 13
        //   9482: istore 8
        //   9484: iload 5
        //   9486: istore 14
        //   9488: iload 6
        //   9490: istore 11
        //   9492: iload 56
        //   9494: istore 51
        //   9496: iload 49
        //   9498: istore 53
        //   9500: iload_3
        //   9501: istore 19
        //   9503: iload 4
        //   9505: istore 17
        //   9507: iload 20
        //   9509: istore 10
        //   9511: iload 5
        //   9513: istore 16
        //   9515: iload 6
        //   9517: istore 12
        //   9519: iload 57
        //   9521: istore 52
        //   9523: iload 49
        //   9525: istore 54
        //   9527: aload 80
        //   9529: iconst_3
        //   9530: iload 24
        //   9532: invokevirtual 323	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
        //   9535: iload_3
        //   9536: istore 18
        //   9538: iload 4
        //   9540: istore 15
        //   9542: iload 13
        //   9544: istore 8
        //   9546: iload 5
        //   9548: istore 14
        //   9550: iload 6
        //   9552: istore 11
        //   9554: iload 56
        //   9556: istore 51
        //   9558: iload 49
        //   9560: istore 53
        //   9562: iload_3
        //   9563: istore 19
        //   9565: iload 4
        //   9567: istore 17
        //   9569: iload 20
        //   9571: istore 10
        //   9573: iload 5
        //   9575: istore 16
        //   9577: iload 6
        //   9579: istore 12
        //   9581: iload 57
        //   9583: istore 52
        //   9585: iload 49
        //   9587: istore 54
        //   9589: aload 80
        //   9591: invokevirtual 327	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
        //   9594: pop
        //   9595: iload_3
        //   9596: istore 18
        //   9598: iload 4
        //   9600: istore 15
        //   9602: iload 13
        //   9604: istore 8
        //   9606: iload 5
        //   9608: istore 14
        //   9610: iload 6
        //   9612: istore 11
        //   9614: iload 56
        //   9616: istore 51
        //   9618: iload 49
        //   9620: istore 53
        //   9622: iload_3
        //   9623: istore 19
        //   9625: iload 4
        //   9627: istore 17
        //   9629: iload 20
        //   9631: istore 10
        //   9633: iload 5
        //   9635: istore 16
        //   9637: iload 6
        //   9639: istore 12
        //   9641: iload 57
        //   9643: istore 52
        //   9645: iload 49
        //   9647: istore 54
        //   9649: aload 80
        //   9651: invokevirtual 328	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
        //   9654: iload_3
        //   9655: istore 18
        //   9657: iload 4
        //   9659: istore 15
        //   9661: iload 13
        //   9663: istore 8
        //   9665: iload 5
        //   9667: istore 14
        //   9669: iload 6
        //   9671: istore 11
        //   9673: iload 56
        //   9675: istore 51
        //   9677: iload 49
        //   9679: istore 53
        //   9681: iload_3
        //   9682: istore 19
        //   9684: iload 4
        //   9686: istore 17
        //   9688: iload 20
        //   9690: istore 10
        //   9692: iload 5
        //   9694: istore 16
        //   9696: iload 6
        //   9698: istore 12
        //   9700: iload 57
        //   9702: istore 52
        //   9704: iload 49
        //   9706: istore 54
        //   9708: aload 79
        //   9710: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   9713: iload 55
        //   9715: istore 50
        //   9717: goto -8341 -> 1376
        //   9720: iload_3
        //   9721: istore 18
        //   9723: iload 4
        //   9725: istore 15
        //   9727: iload 7
        //   9729: istore 8
        //   9731: iload 5
        //   9733: istore 14
        //   9735: iload 6
        //   9737: istore 11
        //   9739: iload 50
        //   9741: istore 51
        //   9743: iload 49
        //   9745: istore 53
        //   9747: iload_3
        //   9748: istore 19
        //   9750: iload 4
        //   9752: istore 17
        //   9754: iload 7
        //   9756: istore 10
        //   9758: iload 5
        //   9760: istore 16
        //   9762: iload 6
        //   9764: istore 12
        //   9766: iload 50
        //   9768: istore 52
        //   9770: iload 49
        //   9772: istore 54
        //   9774: aload_0
        //   9775: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   9778: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   9781: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   9784: ldc_w 330
        //   9787: iconst_3
        //   9788: anewarray 4	java/lang/Object
        //   9791: dup
        //   9792: iconst_0
        //   9793: aload_0
        //   9794: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   9797: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   9800: aastore
        //   9801: dup
        //   9802: iconst_1
        //   9803: iload 9
        //   9805: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   9808: aastore
        //   9809: dup
        //   9810: iconst_2
        //   9811: iload 9
        //   9813: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   9816: aastore
        //   9817: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   9820: iconst_0
        //   9821: anewarray 4	java/lang/Object
        //   9824: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   9827: astore 79
        //   9829: iload_3
        //   9830: istore 18
        //   9832: iload 4
        //   9834: istore 15
        //   9836: iload 7
        //   9838: istore 8
        //   9840: iload 5
        //   9842: istore 14
        //   9844: iload 6
        //   9846: istore 11
        //   9848: iload 50
        //   9850: istore 51
        //   9852: iload 49
        //   9854: istore 53
        //   9856: iload_3
        //   9857: istore 19
        //   9859: iload 4
        //   9861: istore 17
        //   9863: iload 7
        //   9865: istore 10
        //   9867: iload 5
        //   9869: istore 16
        //   9871: iload 6
        //   9873: istore 12
        //   9875: iload 50
        //   9877: istore 52
        //   9879: iload 49
        //   9881: istore 54
        //   9883: aload 79
        //   9885: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   9888: ifeq +6 -> 9894
        //   9891: iconst_m1
        //   9892: istore 9
        //   9894: iload_3
        //   9895: istore 18
        //   9897: iload 4
        //   9899: istore 15
        //   9901: iload 7
        //   9903: istore 8
        //   9905: iload 5
        //   9907: istore 14
        //   9909: iload 6
        //   9911: istore 11
        //   9913: iload 50
        //   9915: istore 51
        //   9917: iload 49
        //   9919: istore 53
        //   9921: iload_3
        //   9922: istore 19
        //   9924: iload 4
        //   9926: istore 17
        //   9928: iload 7
        //   9930: istore 10
        //   9932: iload 5
        //   9934: istore 16
        //   9936: iload 6
        //   9938: istore 12
        //   9940: iload 50
        //   9942: istore 52
        //   9944: iload 49
        //   9946: istore 54
        //   9948: aload 79
        //   9950: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   9953: iload 9
        //   9955: iconst_m1
        //   9956: if_icmpeq +9977 -> 19933
        //   9959: iload_3
        //   9960: istore 18
        //   9962: iload 4
        //   9964: istore 15
        //   9966: iload 7
        //   9968: istore 8
        //   9970: iload 5
        //   9972: istore 14
        //   9974: iload 6
        //   9976: istore 11
        //   9978: iload 50
        //   9980: istore 51
        //   9982: iload 49
        //   9984: istore 53
        //   9986: iload_3
        //   9987: istore 19
        //   9989: iload 4
        //   9991: istore 17
        //   9993: iload 7
        //   9995: istore 10
        //   9997: iload 5
        //   9999: istore 16
        //   10001: iload 6
        //   10003: istore 12
        //   10005: iload 50
        //   10007: istore 52
        //   10009: iload 49
        //   10011: istore 54
        //   10013: aload_0
        //   10014: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   10017: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   10020: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   10023: ldc_w 330
        //   10026: iconst_3
        //   10027: anewarray 4	java/lang/Object
        //   10030: dup
        //   10031: iconst_0
        //   10032: aload_0
        //   10033: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10036: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10039: aastore
        //   10040: dup
        //   10041: iconst_1
        //   10042: iload 13
        //   10044: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10047: aastore
        //   10048: dup
        //   10049: iconst_2
        //   10050: iload 13
        //   10052: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10055: aastore
        //   10056: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   10059: iconst_0
        //   10060: anewarray 4	java/lang/Object
        //   10063: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   10066: astore 79
        //   10068: iload_3
        //   10069: istore 18
        //   10071: iload 4
        //   10073: istore 15
        //   10075: iload 7
        //   10077: istore 8
        //   10079: iload 5
        //   10081: istore 14
        //   10083: iload 6
        //   10085: istore 11
        //   10087: iload 50
        //   10089: istore 51
        //   10091: iload 49
        //   10093: istore 53
        //   10095: iload_3
        //   10096: istore 19
        //   10098: iload 4
        //   10100: istore 17
        //   10102: iload 7
        //   10104: istore 10
        //   10106: iload 5
        //   10108: istore 16
        //   10110: iload 6
        //   10112: istore 12
        //   10114: iload 50
        //   10116: istore 52
        //   10118: iload 49
        //   10120: istore 54
        //   10122: aload 79
        //   10124: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   10127: ifeq +9813 -> 19940
        //   10130: iconst_m1
        //   10131: istore 9
        //   10133: iload_3
        //   10134: istore 18
        //   10136: iload 4
        //   10138: istore 15
        //   10140: iload 7
        //   10142: istore 8
        //   10144: iload 5
        //   10146: istore 14
        //   10148: iload 6
        //   10150: istore 11
        //   10152: iload 50
        //   10154: istore 51
        //   10156: iload 49
        //   10158: istore 53
        //   10160: iload_3
        //   10161: istore 19
        //   10163: iload 4
        //   10165: istore 17
        //   10167: iload 7
        //   10169: istore 10
        //   10171: iload 5
        //   10173: istore 16
        //   10175: iload 6
        //   10177: istore 12
        //   10179: iload 50
        //   10181: istore 52
        //   10183: iload 49
        //   10185: istore 54
        //   10187: aload 79
        //   10189: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   10192: iload 9
        //   10194: iconst_m1
        //   10195: if_icmpeq +9738 -> 19933
        //   10198: iload 9
        //   10200: i2l
        //   10201: lstore 73
        //   10203: lload 73
        //   10205: lconst_0
        //   10206: lcmp
        //   10207: ifeq +9717 -> 19924
        //   10210: iload 21
        //   10212: ifeq +9712 -> 19924
        //   10215: lload 73
        //   10217: iload 21
        //   10219: i2l
        //   10220: bipush 32
        //   10222: lshl
        //   10223: lor
        //   10224: lstore 73
        //   10226: iload 9
        //   10228: istore_2
        //   10229: iload 9
        //   10231: istore_1
        //   10232: goto -7667 -> 2565
        //   10235: iconst_0
        //   10236: istore 8
        //   10238: goto -7666 -> 2572
        //   10241: iload_3
        //   10242: istore 16
        //   10244: iload_1
        //   10245: istore 17
        //   10247: iload 4
        //   10249: istore 18
        //   10251: iload 7
        //   10253: istore 15
        //   10255: iload 5
        //   10257: istore 19
        //   10259: iload 6
        //   10261: istore 20
        //   10263: iload 50
        //   10265: istore 53
        //   10267: iload 49
        //   10269: istore 54
        //   10271: iload_3
        //   10272: istore 9
        //   10274: iload_1
        //   10275: istore 10
        //   10277: iload 4
        //   10279: istore 11
        //   10281: iload 7
        //   10283: istore 12
        //   10285: iload 5
        //   10287: istore 13
        //   10289: iload 6
        //   10291: istore 14
        //   10293: iload 50
        //   10295: istore 51
        //   10297: iload 49
        //   10299: istore 52
        //   10301: aload_0
        //   10302: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   10305: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   10308: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   10311: ldc_w 332
        //   10314: bipush 6
        //   10316: anewarray 4	java/lang/Object
        //   10319: dup
        //   10320: iconst_0
        //   10321: aload_0
        //   10322: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10325: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10328: aastore
        //   10329: dup
        //   10330: iconst_1
        //   10331: lload 73
        //   10333: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10336: aastore
        //   10337: dup
        //   10338: iconst_2
        //   10339: iload_3
        //   10340: iconst_2
        //   10341: idiv
        //   10342: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10345: aastore
        //   10346: dup
        //   10347: iconst_3
        //   10348: aload_0
        //   10349: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10352: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10355: aastore
        //   10356: dup
        //   10357: iconst_4
        //   10358: lload 73
        //   10360: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10363: aastore
        //   10364: dup
        //   10365: iconst_5
        //   10366: iload_3
        //   10367: iconst_2
        //   10368: idiv
        //   10369: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10372: aastore
        //   10373: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   10376: iconst_0
        //   10377: anewarray 4	java/lang/Object
        //   10380: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   10383: astore 79
        //   10385: goto +9697 -> 20082
        //   10388: aconst_null
        //   10389: astore 79
        //   10391: goto +9691 -> 20082
        //   10394: iload_3
        //   10395: istore 18
        //   10397: iload 4
        //   10399: istore 15
        //   10401: iload 13
        //   10403: istore 8
        //   10405: iload 5
        //   10407: istore 14
        //   10409: iload 6
        //   10411: istore 11
        //   10413: iload 50
        //   10415: istore 51
        //   10417: iload 49
        //   10419: istore 53
        //   10421: iload_3
        //   10422: istore 19
        //   10424: iload 4
        //   10426: istore 17
        //   10428: iload 20
        //   10430: istore 10
        //   10432: iload 5
        //   10434: istore 16
        //   10436: iload 6
        //   10438: istore 12
        //   10440: iload 50
        //   10442: istore 52
        //   10444: iload 49
        //   10446: istore 54
        //   10448: aload_0
        //   10449: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   10452: iconst_1
        //   10453: if_icmpne +570 -> 11023
        //   10456: lconst_0
        //   10457: lstore 73
        //   10459: iload_3
        //   10460: istore 18
        //   10462: iload 4
        //   10464: istore 15
        //   10466: iload 13
        //   10468: istore 8
        //   10470: iload 5
        //   10472: istore 14
        //   10474: iload 6
        //   10476: istore 11
        //   10478: iload 50
        //   10480: istore 51
        //   10482: iload 49
        //   10484: istore 53
        //   10486: iload_3
        //   10487: istore 19
        //   10489: iload 4
        //   10491: istore 17
        //   10493: iload 20
        //   10495: istore 10
        //   10497: iload 5
        //   10499: istore 16
        //   10501: iload 6
        //   10503: istore 12
        //   10505: iload 50
        //   10507: istore 52
        //   10509: iload 49
        //   10511: istore 54
        //   10513: aload_0
        //   10514: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   10517: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   10520: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   10523: ldc_w 334
        //   10526: iconst_2
        //   10527: anewarray 4	java/lang/Object
        //   10530: dup
        //   10531: iconst_0
        //   10532: aload_0
        //   10533: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10536: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10539: aastore
        //   10540: dup
        //   10541: iconst_1
        //   10542: aload_0
        //   10543: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   10546: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10549: aastore
        //   10550: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   10553: iconst_0
        //   10554: anewarray 4	java/lang/Object
        //   10557: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   10560: astore 79
        //   10562: iload_3
        //   10563: istore 18
        //   10565: iload 4
        //   10567: istore 15
        //   10569: iload 13
        //   10571: istore 8
        //   10573: iload 5
        //   10575: istore 14
        //   10577: iload 6
        //   10579: istore 11
        //   10581: iload 50
        //   10583: istore 51
        //   10585: iload 49
        //   10587: istore 53
        //   10589: iload_3
        //   10590: istore 19
        //   10592: iload 4
        //   10594: istore 17
        //   10596: iload 20
        //   10598: istore 10
        //   10600: iload 5
        //   10602: istore 16
        //   10604: iload 6
        //   10606: istore 12
        //   10608: iload 50
        //   10610: istore 52
        //   10612: iload 49
        //   10614: istore 54
        //   10616: aload 79
        //   10618: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   10621: ifeq +86 -> 10707
        //   10624: iload_3
        //   10625: istore 18
        //   10627: iload 4
        //   10629: istore 15
        //   10631: iload 13
        //   10633: istore 8
        //   10635: iload 5
        //   10637: istore 14
        //   10639: iload 6
        //   10641: istore 11
        //   10643: iload 50
        //   10645: istore 51
        //   10647: iload 49
        //   10649: istore 53
        //   10651: iload_3
        //   10652: istore 19
        //   10654: iload 4
        //   10656: istore 17
        //   10658: iload 20
        //   10660: istore 10
        //   10662: iload 5
        //   10664: istore 16
        //   10666: iload 6
        //   10668: istore 12
        //   10670: iload 50
        //   10672: istore 52
        //   10674: iload 49
        //   10676: istore 54
        //   10678: aload 79
        //   10680: iconst_0
        //   10681: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   10684: i2l
        //   10685: lstore 75
        //   10687: lload 75
        //   10689: lstore 73
        //   10691: iload 21
        //   10693: ifeq +14 -> 10707
        //   10696: lload 75
        //   10698: iload 21
        //   10700: i2l
        //   10701: bipush 32
        //   10703: lshl
        //   10704: lor
        //   10705: lstore 73
        //   10707: iload_3
        //   10708: istore 18
        //   10710: iload 4
        //   10712: istore 15
        //   10714: iload 13
        //   10716: istore 8
        //   10718: iload 5
        //   10720: istore 14
        //   10722: iload 6
        //   10724: istore 11
        //   10726: iload 50
        //   10728: istore 51
        //   10730: iload 49
        //   10732: istore 53
        //   10734: iload_3
        //   10735: istore 19
        //   10737: iload 4
        //   10739: istore 17
        //   10741: iload 20
        //   10743: istore 10
        //   10745: iload 5
        //   10747: istore 16
        //   10749: iload 6
        //   10751: istore 12
        //   10753: iload 50
        //   10755: istore 52
        //   10757: iload 49
        //   10759: istore 54
        //   10761: aload 79
        //   10763: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   10766: lload 73
        //   10768: lconst_0
        //   10769: lcmp
        //   10770: ifeq +132 -> 10902
        //   10773: iload_3
        //   10774: istore 18
        //   10776: iload 4
        //   10778: istore 15
        //   10780: iload 13
        //   10782: istore 8
        //   10784: iload 5
        //   10786: istore 14
        //   10788: iload 6
        //   10790: istore 11
        //   10792: iload 50
        //   10794: istore 51
        //   10796: iload 49
        //   10798: istore 53
        //   10800: iload_3
        //   10801: istore 19
        //   10803: iload 4
        //   10805: istore 17
        //   10807: iload 20
        //   10809: istore 10
        //   10811: iload 5
        //   10813: istore 16
        //   10815: iload 6
        //   10817: istore 12
        //   10819: iload 50
        //   10821: istore 52
        //   10823: iload 49
        //   10825: istore 54
        //   10827: aload_0
        //   10828: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   10831: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   10834: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   10837: ldc_w 336
        //   10840: iconst_5
        //   10841: anewarray 4	java/lang/Object
        //   10844: dup
        //   10845: iconst_0
        //   10846: aload_0
        //   10847: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10850: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10853: aastore
        //   10854: dup
        //   10855: iconst_1
        //   10856: aload_0
        //   10857: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   10860: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10863: aastore
        //   10864: dup
        //   10865: iconst_2
        //   10866: lload 71
        //   10868: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10871: aastore
        //   10872: dup
        //   10873: iconst_3
        //   10874: lload 73
        //   10876: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10879: aastore
        //   10880: dup
        //   10881: iconst_4
        //   10882: iload_3
        //   10883: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10886: aastore
        //   10887: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   10890: iconst_0
        //   10891: anewarray 4	java/lang/Object
        //   10894: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   10897: astore 79
        //   10899: goto +9275 -> 20174
        //   10902: iload_3
        //   10903: istore 18
        //   10905: iload 4
        //   10907: istore 15
        //   10909: iload 13
        //   10911: istore 8
        //   10913: iload 5
        //   10915: istore 14
        //   10917: iload 6
        //   10919: istore 11
        //   10921: iload 50
        //   10923: istore 51
        //   10925: iload 49
        //   10927: istore 53
        //   10929: iload_3
        //   10930: istore 19
        //   10932: iload 4
        //   10934: istore 17
        //   10936: iload 20
        //   10938: istore 10
        //   10940: iload 5
        //   10942: istore 16
        //   10944: iload 6
        //   10946: istore 12
        //   10948: iload 50
        //   10950: istore 52
        //   10952: iload 49
        //   10954: istore 54
        //   10956: aload_0
        //   10957: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   10960: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   10963: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   10966: ldc_w 338
        //   10969: iconst_4
        //   10970: anewarray 4	java/lang/Object
        //   10973: dup
        //   10974: iconst_0
        //   10975: aload_0
        //   10976: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   10979: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   10982: aastore
        //   10983: dup
        //   10984: iconst_1
        //   10985: aload_0
        //   10986: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   10989: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   10992: aastore
        //   10993: dup
        //   10994: iconst_2
        //   10995: lload 71
        //   10997: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11000: aastore
        //   11001: dup
        //   11002: iconst_3
        //   11003: iload_3
        //   11004: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11007: aastore
        //   11008: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11011: iconst_0
        //   11012: anewarray 4	java/lang/Object
        //   11015: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11018: astore 79
        //   11020: goto +9154 -> 20174
        //   11023: iload_3
        //   11024: istore 18
        //   11026: iload 4
        //   11028: istore 15
        //   11030: iload 13
        //   11032: istore 8
        //   11034: iload 5
        //   11036: istore 14
        //   11038: iload 6
        //   11040: istore 11
        //   11042: iload 50
        //   11044: istore 51
        //   11046: iload 49
        //   11048: istore 53
        //   11050: iload_3
        //   11051: istore 19
        //   11053: iload 4
        //   11055: istore 17
        //   11057: iload 20
        //   11059: istore 10
        //   11061: iload 5
        //   11063: istore 16
        //   11065: iload 6
        //   11067: istore 12
        //   11069: iload 50
        //   11071: istore 52
        //   11073: iload 49
        //   11075: istore 54
        //   11077: aload_0
        //   11078: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   11081: ifeq +697 -> 11778
        //   11084: lload 71
        //   11086: lconst_0
        //   11087: lcmp
        //   11088: ifeq +569 -> 11657
        //   11091: lconst_0
        //   11092: lstore 73
        //   11094: iload_3
        //   11095: istore 18
        //   11097: iload 4
        //   11099: istore 15
        //   11101: iload 13
        //   11103: istore 8
        //   11105: iload 5
        //   11107: istore 14
        //   11109: iload 6
        //   11111: istore 11
        //   11113: iload 50
        //   11115: istore 51
        //   11117: iload 49
        //   11119: istore 53
        //   11121: iload_3
        //   11122: istore 19
        //   11124: iload 4
        //   11126: istore 17
        //   11128: iload 20
        //   11130: istore 10
        //   11132: iload 5
        //   11134: istore 16
        //   11136: iload 6
        //   11138: istore 12
        //   11140: iload 50
        //   11142: istore 52
        //   11144: iload 49
        //   11146: istore 54
        //   11148: aload_0
        //   11149: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   11152: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11155: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   11158: ldc 143
        //   11160: iconst_2
        //   11161: anewarray 4	java/lang/Object
        //   11164: dup
        //   11165: iconst_0
        //   11166: aload_0
        //   11167: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   11170: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11173: aastore
        //   11174: dup
        //   11175: iconst_1
        //   11176: aload_0
        //   11177: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   11180: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11183: aastore
        //   11184: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11187: iconst_0
        //   11188: anewarray 4	java/lang/Object
        //   11191: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11194: astore 79
        //   11196: iload_3
        //   11197: istore 18
        //   11199: iload 4
        //   11201: istore 15
        //   11203: iload 13
        //   11205: istore 8
        //   11207: iload 5
        //   11209: istore 14
        //   11211: iload 6
        //   11213: istore 11
        //   11215: iload 50
        //   11217: istore 51
        //   11219: iload 49
        //   11221: istore 53
        //   11223: iload_3
        //   11224: istore 19
        //   11226: iload 4
        //   11228: istore 17
        //   11230: iload 20
        //   11232: istore 10
        //   11234: iload 5
        //   11236: istore 16
        //   11238: iload 6
        //   11240: istore 12
        //   11242: iload 50
        //   11244: istore 52
        //   11246: iload 49
        //   11248: istore 54
        //   11250: aload 79
        //   11252: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   11255: ifeq +86 -> 11341
        //   11258: iload_3
        //   11259: istore 18
        //   11261: iload 4
        //   11263: istore 15
        //   11265: iload 13
        //   11267: istore 8
        //   11269: iload 5
        //   11271: istore 14
        //   11273: iload 6
        //   11275: istore 11
        //   11277: iload 50
        //   11279: istore 51
        //   11281: iload 49
        //   11283: istore 53
        //   11285: iload_3
        //   11286: istore 19
        //   11288: iload 4
        //   11290: istore 17
        //   11292: iload 20
        //   11294: istore 10
        //   11296: iload 5
        //   11298: istore 16
        //   11300: iload 6
        //   11302: istore 12
        //   11304: iload 50
        //   11306: istore 52
        //   11308: iload 49
        //   11310: istore 54
        //   11312: aload 79
        //   11314: iconst_0
        //   11315: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   11318: i2l
        //   11319: lstore 75
        //   11321: lload 75
        //   11323: lstore 73
        //   11325: iload 21
        //   11327: ifeq +14 -> 11341
        //   11330: lload 75
        //   11332: iload 21
        //   11334: i2l
        //   11335: bipush 32
        //   11337: lshl
        //   11338: lor
        //   11339: lstore 73
        //   11341: iload_3
        //   11342: istore 18
        //   11344: iload 4
        //   11346: istore 15
        //   11348: iload 13
        //   11350: istore 8
        //   11352: iload 5
        //   11354: istore 14
        //   11356: iload 6
        //   11358: istore 11
        //   11360: iload 50
        //   11362: istore 51
        //   11364: iload 49
        //   11366: istore 53
        //   11368: iload_3
        //   11369: istore 19
        //   11371: iload 4
        //   11373: istore 17
        //   11375: iload 20
        //   11377: istore 10
        //   11379: iload 5
        //   11381: istore 16
        //   11383: iload 6
        //   11385: istore 12
        //   11387: iload 50
        //   11389: istore 52
        //   11391: iload 49
        //   11393: istore 54
        //   11395: aload 79
        //   11397: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   11400: lload 73
        //   11402: lconst_0
        //   11403: lcmp
        //   11404: ifeq +132 -> 11536
        //   11407: iload_3
        //   11408: istore 18
        //   11410: iload 4
        //   11412: istore 15
        //   11414: iload 13
        //   11416: istore 8
        //   11418: iload 5
        //   11420: istore 14
        //   11422: iload 6
        //   11424: istore 11
        //   11426: iload 50
        //   11428: istore 51
        //   11430: iload 49
        //   11432: istore 53
        //   11434: iload_3
        //   11435: istore 19
        //   11437: iload 4
        //   11439: istore 17
        //   11441: iload 20
        //   11443: istore 10
        //   11445: iload 5
        //   11447: istore 16
        //   11449: iload 6
        //   11451: istore 12
        //   11453: iload 50
        //   11455: istore 52
        //   11457: iload 49
        //   11459: istore 54
        //   11461: aload_0
        //   11462: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   11465: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11468: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   11471: ldc_w 340
        //   11474: iconst_5
        //   11475: anewarray 4	java/lang/Object
        //   11478: dup
        //   11479: iconst_0
        //   11480: aload_0
        //   11481: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   11484: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11487: aastore
        //   11488: dup
        //   11489: iconst_1
        //   11490: aload_0
        //   11491: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   11494: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11497: aastore
        //   11498: dup
        //   11499: iconst_2
        //   11500: lload 71
        //   11502: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11505: aastore
        //   11506: dup
        //   11507: iconst_3
        //   11508: lload 73
        //   11510: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11513: aastore
        //   11514: dup
        //   11515: iconst_4
        //   11516: iload_3
        //   11517: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11520: aastore
        //   11521: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11524: iconst_0
        //   11525: anewarray 4	java/lang/Object
        //   11528: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11531: astore 79
        //   11533: goto +8644 -> 20177
        //   11536: iload_3
        //   11537: istore 18
        //   11539: iload 4
        //   11541: istore 15
        //   11543: iload 13
        //   11545: istore 8
        //   11547: iload 5
        //   11549: istore 14
        //   11551: iload 6
        //   11553: istore 11
        //   11555: iload 50
        //   11557: istore 51
        //   11559: iload 49
        //   11561: istore 53
        //   11563: iload_3
        //   11564: istore 19
        //   11566: iload 4
        //   11568: istore 17
        //   11570: iload 20
        //   11572: istore 10
        //   11574: iload 5
        //   11576: istore 16
        //   11578: iload 6
        //   11580: istore 12
        //   11582: iload 50
        //   11584: istore 52
        //   11586: iload 49
        //   11588: istore 54
        //   11590: aload_0
        //   11591: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   11594: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11597: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   11600: ldc_w 342
        //   11603: iconst_4
        //   11604: anewarray 4	java/lang/Object
        //   11607: dup
        //   11608: iconst_0
        //   11609: aload_0
        //   11610: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   11613: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11616: aastore
        //   11617: dup
        //   11618: iconst_1
        //   11619: aload_0
        //   11620: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   11623: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11626: aastore
        //   11627: dup
        //   11628: iconst_2
        //   11629: lload 71
        //   11631: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11634: aastore
        //   11635: dup
        //   11636: iconst_3
        //   11637: iload_3
        //   11638: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11641: aastore
        //   11642: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11645: iconst_0
        //   11646: anewarray 4	java/lang/Object
        //   11649: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11652: astore 79
        //   11654: goto +8523 -> 20177
        //   11657: iload_3
        //   11658: istore 18
        //   11660: iload 4
        //   11662: istore 15
        //   11664: iload 13
        //   11666: istore 8
        //   11668: iload 5
        //   11670: istore 14
        //   11672: iload 6
        //   11674: istore 11
        //   11676: iload 50
        //   11678: istore 51
        //   11680: iload 49
        //   11682: istore 53
        //   11684: iload_3
        //   11685: istore 19
        //   11687: iload 4
        //   11689: istore 17
        //   11691: iload 20
        //   11693: istore 10
        //   11695: iload 5
        //   11697: istore 16
        //   11699: iload 6
        //   11701: istore 12
        //   11703: iload 50
        //   11705: istore 52
        //   11707: iload 49
        //   11709: istore 54
        //   11711: aload_0
        //   11712: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   11715: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11718: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   11721: ldc_w 344
        //   11724: iconst_4
        //   11725: anewarray 4	java/lang/Object
        //   11728: dup
        //   11729: iconst_0
        //   11730: aload_0
        //   11731: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   11734: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11737: aastore
        //   11738: dup
        //   11739: iconst_1
        //   11740: aload_0
        //   11741: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   11744: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11747: aastore
        //   11748: dup
        //   11749: iconst_2
        //   11750: iload 9
        //   11752: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11755: aastore
        //   11756: dup
        //   11757: iconst_3
        //   11758: iload_3
        //   11759: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   11762: aastore
        //   11763: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11766: iconst_0
        //   11767: anewarray 4	java/lang/Object
        //   11770: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11773: astore 79
        //   11775: goto +8307 -> 20082
        //   11778: iload_3
        //   11779: istore 18
        //   11781: iload 4
        //   11783: istore 15
        //   11785: iload 13
        //   11787: istore 8
        //   11789: iload 5
        //   11791: istore 14
        //   11793: iload 6
        //   11795: istore 11
        //   11797: iload 50
        //   11799: istore 51
        //   11801: iload 49
        //   11803: istore 53
        //   11805: iload_3
        //   11806: istore 19
        //   11808: iload 4
        //   11810: istore 17
        //   11812: iload 20
        //   11814: istore 10
        //   11816: iload 5
        //   11818: istore 16
        //   11820: iload 6
        //   11822: istore 12
        //   11824: iload 50
        //   11826: istore 52
        //   11828: iload 49
        //   11830: istore 54
        //   11832: aload_0
        //   11833: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   11836: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   11839: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   11842: ldc 128
        //   11844: iconst_1
        //   11845: anewarray 4	java/lang/Object
        //   11848: dup
        //   11849: iconst_0
        //   11850: aload_0
        //   11851: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   11854: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   11857: aastore
        //   11858: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   11861: iconst_0
        //   11862: anewarray 4	java/lang/Object
        //   11865: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   11868: astore 79
        //   11870: iload 23
        //   11872: istore 7
        //   11874: iload_3
        //   11875: istore 18
        //   11877: iload 4
        //   11879: istore 15
        //   11881: iload 13
        //   11883: istore 8
        //   11885: iload 5
        //   11887: istore 14
        //   11889: iload 6
        //   11891: istore 11
        //   11893: iload 50
        //   11895: istore 51
        //   11897: iload 49
        //   11899: istore 53
        //   11901: iload_3
        //   11902: istore 19
        //   11904: iload 4
        //   11906: istore 17
        //   11908: iload 20
        //   11910: istore 10
        //   11912: iload 5
        //   11914: istore 16
        //   11916: iload 6
        //   11918: istore 12
        //   11920: iload 50
        //   11922: istore 52
        //   11924: iload 49
        //   11926: istore 54
        //   11928: aload 79
        //   11930: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   11933: ifeq +65 -> 11998
        //   11936: iload_3
        //   11937: istore 18
        //   11939: iload 4
        //   11941: istore 15
        //   11943: iload 13
        //   11945: istore 8
        //   11947: iload 5
        //   11949: istore 14
        //   11951: iload 6
        //   11953: istore 11
        //   11955: iload 50
        //   11957: istore 51
        //   11959: iload 49
        //   11961: istore 53
        //   11963: iload_3
        //   11964: istore 19
        //   11966: iload 4
        //   11968: istore 17
        //   11970: iload 20
        //   11972: istore 10
        //   11974: iload 5
        //   11976: istore 16
        //   11978: iload 6
        //   11980: istore 12
        //   11982: iload 50
        //   11984: istore 52
        //   11986: iload 49
        //   11988: istore 54
        //   11990: aload 79
        //   11992: iconst_0
        //   11993: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   11996: istore 7
        //   11998: iload_3
        //   11999: istore 18
        //   12001: iload 4
        //   12003: istore 15
        //   12005: iload 7
        //   12007: istore 8
        //   12009: iload 5
        //   12011: istore 14
        //   12013: iload 6
        //   12015: istore 11
        //   12017: iload 50
        //   12019: istore 51
        //   12021: iload 49
        //   12023: istore 53
        //   12025: iload_3
        //   12026: istore 19
        //   12028: iload 4
        //   12030: istore 17
        //   12032: iload 7
        //   12034: istore 10
        //   12036: iload 5
        //   12038: istore 16
        //   12040: iload 6
        //   12042: istore 12
        //   12044: iload 50
        //   12046: istore 52
        //   12048: iload 49
        //   12050: istore 54
        //   12052: aload 79
        //   12054: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   12057: lconst_0
        //   12058: lstore 71
        //   12060: iload_3
        //   12061: istore 18
        //   12063: iload 4
        //   12065: istore 15
        //   12067: iload 7
        //   12069: istore 8
        //   12071: iload 5
        //   12073: istore 14
        //   12075: iload 6
        //   12077: istore 11
        //   12079: iload 50
        //   12081: istore 51
        //   12083: iload 49
        //   12085: istore 53
        //   12087: iload_3
        //   12088: istore 19
        //   12090: iload 4
        //   12092: istore 17
        //   12094: iload 7
        //   12096: istore 10
        //   12098: iload 5
        //   12100: istore 16
        //   12102: iload 6
        //   12104: istore 12
        //   12106: iload 50
        //   12108: istore 52
        //   12110: iload 49
        //   12112: istore 54
        //   12114: aload_0
        //   12115: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   12118: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   12121: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   12124: ldc_w 346
        //   12127: iconst_1
        //   12128: anewarray 4	java/lang/Object
        //   12131: dup
        //   12132: iconst_0
        //   12133: aload_0
        //   12134: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   12137: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   12140: aastore
        //   12141: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   12144: iconst_0
        //   12145: anewarray 4	java/lang/Object
        //   12148: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   12151: astore 79
        //   12153: iload_3
        //   12154: istore 18
        //   12156: iload 4
        //   12158: istore 15
        //   12160: iload 7
        //   12162: istore 8
        //   12164: iload 5
        //   12166: istore 14
        //   12168: iload 6
        //   12170: istore 11
        //   12172: iload 50
        //   12174: istore 51
        //   12176: iload 49
        //   12178: istore 53
        //   12180: iload_3
        //   12181: istore 19
        //   12183: iload 4
        //   12185: istore 17
        //   12187: iload 7
        //   12189: istore 10
        //   12191: iload 5
        //   12193: istore 16
        //   12195: iload 6
        //   12197: istore 12
        //   12199: iload 50
        //   12201: istore 52
        //   12203: iload 49
        //   12205: istore 54
        //   12207: aload 79
        //   12209: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   12212: ifeq +86 -> 12298
        //   12215: iload_3
        //   12216: istore 18
        //   12218: iload 4
        //   12220: istore 15
        //   12222: iload 7
        //   12224: istore 8
        //   12226: iload 5
        //   12228: istore 14
        //   12230: iload 6
        //   12232: istore 11
        //   12234: iload 50
        //   12236: istore 51
        //   12238: iload 49
        //   12240: istore 53
        //   12242: iload_3
        //   12243: istore 19
        //   12245: iload 4
        //   12247: istore 17
        //   12249: iload 7
        //   12251: istore 10
        //   12253: iload 5
        //   12255: istore 16
        //   12257: iload 6
        //   12259: istore 12
        //   12261: iload 50
        //   12263: istore 52
        //   12265: iload 49
        //   12267: istore 54
        //   12269: aload 79
        //   12271: iconst_0
        //   12272: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   12275: i2l
        //   12276: lstore 73
        //   12278: lload 73
        //   12280: lstore 71
        //   12282: iload 21
        //   12284: ifeq +14 -> 12298
        //   12287: lload 73
        //   12289: iload 21
        //   12291: i2l
        //   12292: bipush 32
        //   12294: lshl
        //   12295: lor
        //   12296: lstore 71
        //   12298: iload_3
        //   12299: istore 18
        //   12301: iload 4
        //   12303: istore 15
        //   12305: iload 7
        //   12307: istore 8
        //   12309: iload 5
        //   12311: istore 14
        //   12313: iload 6
        //   12315: istore 11
        //   12317: iload 50
        //   12319: istore 51
        //   12321: iload 49
        //   12323: istore 53
        //   12325: iload_3
        //   12326: istore 19
        //   12328: iload 4
        //   12330: istore 17
        //   12332: iload 7
        //   12334: istore 10
        //   12336: iload 5
        //   12338: istore 16
        //   12340: iload 6
        //   12342: istore 12
        //   12344: iload 50
        //   12346: istore 52
        //   12348: iload 49
        //   12350: istore 54
        //   12352: aload 79
        //   12354: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   12357: lload 71
        //   12359: lconst_0
        //   12360: lcmp
        //   12361: ifeq +122 -> 12483
        //   12364: iload_3
        //   12365: istore 18
        //   12367: iload 4
        //   12369: istore 15
        //   12371: iload 7
        //   12373: istore 8
        //   12375: iload 5
        //   12377: istore 14
        //   12379: iload 6
        //   12381: istore 11
        //   12383: iload 50
        //   12385: istore 51
        //   12387: iload 49
        //   12389: istore 53
        //   12391: iload_3
        //   12392: istore 19
        //   12394: iload 4
        //   12396: istore 17
        //   12398: iload 7
        //   12400: istore 10
        //   12402: iload 5
        //   12404: istore 16
        //   12406: iload 6
        //   12408: istore 12
        //   12410: iload 50
        //   12412: istore 52
        //   12414: iload 49
        //   12416: istore 54
        //   12418: aload_0
        //   12419: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   12422: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   12425: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   12428: ldc_w 348
        //   12431: iconst_4
        //   12432: anewarray 4	java/lang/Object
        //   12435: dup
        //   12436: iconst_0
        //   12437: aload_0
        //   12438: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   12441: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   12444: aastore
        //   12445: dup
        //   12446: iconst_1
        //   12447: lload 71
        //   12449: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   12452: aastore
        //   12453: dup
        //   12454: iconst_2
        //   12455: iload 9
        //   12457: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   12460: aastore
        //   12461: dup
        //   12462: iconst_3
        //   12463: iload_3
        //   12464: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   12467: aastore
        //   12468: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   12471: iconst_0
        //   12472: anewarray 4	java/lang/Object
        //   12475: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   12478: astore 79
        //   12480: goto +7700 -> 20180
        //   12483: iload_3
        //   12484: istore 18
        //   12486: iload 4
        //   12488: istore 15
        //   12490: iload 7
        //   12492: istore 8
        //   12494: iload 5
        //   12496: istore 14
        //   12498: iload 6
        //   12500: istore 11
        //   12502: iload 50
        //   12504: istore 51
        //   12506: iload 49
        //   12508: istore 53
        //   12510: iload_3
        //   12511: istore 19
        //   12513: iload 4
        //   12515: istore 17
        //   12517: iload 7
        //   12519: istore 10
        //   12521: iload 5
        //   12523: istore 16
        //   12525: iload 6
        //   12527: istore 12
        //   12529: iload 50
        //   12531: istore 52
        //   12533: iload 49
        //   12535: istore 54
        //   12537: aload_0
        //   12538: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   12541: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   12544: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   12547: ldc_w 350
        //   12550: iconst_3
        //   12551: anewarray 4	java/lang/Object
        //   12554: dup
        //   12555: iconst_0
        //   12556: aload_0
        //   12557: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   12560: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   12563: aastore
        //   12564: dup
        //   12565: iconst_1
        //   12566: iload 9
        //   12568: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   12571: aastore
        //   12572: dup
        //   12573: iconst_2
        //   12574: iload_3
        //   12575: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   12578: aastore
        //   12579: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   12582: iconst_0
        //   12583: anewarray 4	java/lang/Object
        //   12586: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   12589: astore 79
        //   12591: goto +7589 -> 20180
        //   12594: iconst_1
        //   12595: istore 63
        //   12597: iconst_1
        //   12598: istore 64
        //   12600: iconst_1
        //   12601: istore 65
        //   12603: iconst_1
        //   12604: istore 66
        //   12606: iconst_1
        //   12607: istore 55
        //   12609: iconst_1
        //   12610: istore 56
        //   12612: iconst_1
        //   12613: istore 57
        //   12615: iload 44
        //   12617: istore 10
        //   12619: iload 39
        //   12621: istore 11
        //   12623: iload 45
        //   12625: istore 12
        //   12627: iload 31
        //   12629: istore_2
        //   12630: iload 20
        //   12632: istore 14
        //   12634: iload 29
        //   12636: istore 5
        //   12638: iload 27
        //   12640: istore 7
        //   12642: iload 64
        //   12644: istore 52
        //   12646: iload 58
        //   12648: istore 50
        //   12650: iload 32
        //   12652: istore_3
        //   12653: iload 23
        //   12655: istore 15
        //   12657: iload 30
        //   12659: istore 6
        //   12661: iload 28
        //   12663: istore 8
        //   12665: iload 66
        //   12667: istore 53
        //   12669: iload 59
        //   12671: istore 51
        //   12673: aload_0
        //   12674: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   12677: iconst_3
        //   12678: if_icmpne +746 -> 13424
        //   12681: iload 44
        //   12683: istore 10
        //   12685: iload 39
        //   12687: istore 11
        //   12689: iload 45
        //   12691: istore 12
        //   12693: iload 31
        //   12695: istore_2
        //   12696: iload 20
        //   12698: istore 14
        //   12700: iload 29
        //   12702: istore 5
        //   12704: iload 27
        //   12706: istore 7
        //   12708: iload 64
        //   12710: istore 52
        //   12712: iload 58
        //   12714: istore 50
        //   12716: iload 32
        //   12718: istore_3
        //   12719: iload 23
        //   12721: istore 15
        //   12723: iload 30
        //   12725: istore 6
        //   12727: iload 28
        //   12729: istore 8
        //   12731: iload 66
        //   12733: istore 53
        //   12735: iload 59
        //   12737: istore 51
        //   12739: aload_0
        //   12740: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   12743: ifne +681 -> 13424
        //   12746: iload 31
        //   12748: istore_2
        //   12749: iload 20
        //   12751: istore 14
        //   12753: iload 29
        //   12755: istore 5
        //   12757: iload 27
        //   12759: istore 7
        //   12761: iload 64
        //   12763: istore 52
        //   12765: iload 58
        //   12767: istore 50
        //   12769: iload 32
        //   12771: istore_3
        //   12772: iload 23
        //   12774: istore 15
        //   12776: iload 30
        //   12778: istore 6
        //   12780: iload 28
        //   12782: istore 8
        //   12784: iload 66
        //   12786: istore 53
        //   12788: iload 59
        //   12790: istore 51
        //   12792: aload_0
        //   12793: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   12796: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   12799: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   12802: ldc_w 352
        //   12805: iconst_1
        //   12806: anewarray 4	java/lang/Object
        //   12809: dup
        //   12810: iconst_0
        //   12811: aload_0
        //   12812: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   12815: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   12818: aastore
        //   12819: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   12822: iconst_0
        //   12823: anewarray 4	java/lang/Object
        //   12826: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   12829: astore 79
        //   12831: iload 16
        //   12833: istore 10
        //   12835: iload 31
        //   12837: istore_2
        //   12838: iload 20
        //   12840: istore 14
        //   12842: iload 29
        //   12844: istore 5
        //   12846: iload 27
        //   12848: istore 7
        //   12850: iload 64
        //   12852: istore 52
        //   12854: iload 58
        //   12856: istore 50
        //   12858: iload 32
        //   12860: istore_3
        //   12861: iload 23
        //   12863: istore 15
        //   12865: iload 30
        //   12867: istore 6
        //   12869: iload 28
        //   12871: istore 8
        //   12873: iload 66
        //   12875: istore 53
        //   12877: iload 59
        //   12879: istore 51
        //   12881: aload 79
        //   12883: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   12886: ifeq +57 -> 12943
        //   12889: iload 31
        //   12891: istore_2
        //   12892: iload 20
        //   12894: istore 14
        //   12896: iload 29
        //   12898: istore 5
        //   12900: iload 27
        //   12902: istore 7
        //   12904: iload 64
        //   12906: istore 52
        //   12908: iload 58
        //   12910: istore 50
        //   12912: iload 32
        //   12914: istore_3
        //   12915: iload 23
        //   12917: istore 15
        //   12919: iload 30
        //   12921: istore 6
        //   12923: iload 28
        //   12925: istore 8
        //   12927: iload 66
        //   12929: istore 53
        //   12931: iload 59
        //   12933: istore 51
        //   12935: aload 79
        //   12937: iconst_0
        //   12938: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   12941: istore 10
        //   12943: iload 10
        //   12945: istore_2
        //   12946: iload 20
        //   12948: istore 14
        //   12950: iload 29
        //   12952: istore 5
        //   12954: iload 27
        //   12956: istore 7
        //   12958: iload 64
        //   12960: istore 52
        //   12962: iload 58
        //   12964: istore 50
        //   12966: iload 10
        //   12968: istore_3
        //   12969: iload 23
        //   12971: istore 15
        //   12973: iload 30
        //   12975: istore 6
        //   12977: iload 28
        //   12979: istore 8
        //   12981: iload 66
        //   12983: istore 53
        //   12985: iload 59
        //   12987: istore 51
        //   12989: aload 79
        //   12991: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   12994: iconst_0
        //   12995: istore 16
        //   12997: iload 10
        //   12999: istore_2
        //   13000: iload 20
        //   13002: istore 14
        //   13004: iload 29
        //   13006: istore 5
        //   13008: iload 27
        //   13010: istore 7
        //   13012: iload 64
        //   13014: istore 52
        //   13016: iload 58
        //   13018: istore 50
        //   13020: iload 10
        //   13022: istore_3
        //   13023: iload 23
        //   13025: istore 15
        //   13027: iload 30
        //   13029: istore 6
        //   13031: iload 28
        //   13033: istore 8
        //   13035: iload 66
        //   13037: istore 53
        //   13039: iload 59
        //   13041: istore 51
        //   13043: aload_0
        //   13044: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   13047: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   13050: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   13053: ldc_w 354
        //   13056: iconst_1
        //   13057: anewarray 4	java/lang/Object
        //   13060: dup
        //   13061: iconst_0
        //   13062: aload_0
        //   13063: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   13066: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13069: aastore
        //   13070: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   13073: iconst_0
        //   13074: anewarray 4	java/lang/Object
        //   13077: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   13080: astore 79
        //   13082: iload 40
        //   13084: istore 13
        //   13086: iload 10
        //   13088: istore_2
        //   13089: iload 20
        //   13091: istore 14
        //   13093: iload 29
        //   13095: istore 5
        //   13097: iload 27
        //   13099: istore 7
        //   13101: iload 64
        //   13103: istore 52
        //   13105: iload 58
        //   13107: istore 50
        //   13109: iload 10
        //   13111: istore_3
        //   13112: iload 23
        //   13114: istore 15
        //   13116: iload 30
        //   13118: istore 6
        //   13120: iload 28
        //   13122: istore 8
        //   13124: iload 66
        //   13126: istore 53
        //   13128: iload 59
        //   13130: istore 51
        //   13132: aload 79
        //   13134: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   13137: ifeq +111 -> 13248
        //   13140: iload 10
        //   13142: istore_2
        //   13143: iload 20
        //   13145: istore 14
        //   13147: iload 29
        //   13149: istore 5
        //   13151: iload 27
        //   13153: istore 7
        //   13155: iload 64
        //   13157: istore 52
        //   13159: iload 58
        //   13161: istore 50
        //   13163: iload 10
        //   13165: istore_3
        //   13166: iload 23
        //   13168: istore 15
        //   13170: iload 30
        //   13172: istore 6
        //   13174: iload 28
        //   13176: istore 8
        //   13178: iload 66
        //   13180: istore 53
        //   13182: iload 59
        //   13184: istore 51
        //   13186: aload 79
        //   13188: iconst_0
        //   13189: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   13192: istore 16
        //   13194: iload 10
        //   13196: istore_2
        //   13197: iload 20
        //   13199: istore 14
        //   13201: iload 29
        //   13203: istore 5
        //   13205: iload 27
        //   13207: istore 7
        //   13209: iload 64
        //   13211: istore 52
        //   13213: iload 58
        //   13215: istore 50
        //   13217: iload 10
        //   13219: istore_3
        //   13220: iload 23
        //   13222: istore 15
        //   13224: iload 30
        //   13226: istore 6
        //   13228: iload 28
        //   13230: istore 8
        //   13232: iload 66
        //   13234: istore 53
        //   13236: iload 59
        //   13238: istore 51
        //   13240: aload 79
        //   13242: iconst_1
        //   13243: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   13246: istore 13
        //   13248: iload 10
        //   13250: istore_2
        //   13251: iload 20
        //   13253: istore 14
        //   13255: iload 29
        //   13257: istore 5
        //   13259: iload 13
        //   13261: istore 7
        //   13263: iload 64
        //   13265: istore 52
        //   13267: iload 58
        //   13269: istore 50
        //   13271: iload 10
        //   13273: istore_3
        //   13274: iload 23
        //   13276: istore 15
        //   13278: iload 30
        //   13280: istore 6
        //   13282: iload 13
        //   13284: istore 8
        //   13286: iload 66
        //   13288: istore 53
        //   13290: iload 59
        //   13292: istore 51
        //   13294: aload 79
        //   13296: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   13299: iload 39
        //   13301: istore 11
        //   13303: iload 13
        //   13305: istore 12
        //   13307: iload 16
        //   13309: ifeq +115 -> 13424
        //   13312: iload 36
        //   13314: istore 5
        //   13316: iload 37
        //   13318: istore 6
        //   13320: aload_0
        //   13321: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   13324: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   13327: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   13330: ldc_w 356
        //   13333: iconst_2
        //   13334: anewarray 4	java/lang/Object
        //   13337: dup
        //   13338: iconst_0
        //   13339: aload_0
        //   13340: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   13343: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13346: aastore
        //   13347: dup
        //   13348: iconst_1
        //   13349: iload 16
        //   13351: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   13354: aastore
        //   13355: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   13358: iconst_0
        //   13359: anewarray 4	java/lang/Object
        //   13362: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   13365: astore 79
        //   13367: iload 38
        //   13369: istore 11
        //   13371: iload 36
        //   13373: istore 5
        //   13375: iload 37
        //   13377: istore 6
        //   13379: aload 79
        //   13381: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   13384: ifeq +19 -> 13403
        //   13387: iload 36
        //   13389: istore 5
        //   13391: iload 37
        //   13393: istore 6
        //   13395: aload 79
        //   13397: iconst_0
        //   13398: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   13401: istore 11
        //   13403: iload 11
        //   13405: istore 5
        //   13407: iload 11
        //   13409: istore 6
        //   13411: aload 79
        //   13413: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   13416: iload 16
        //   13418: istore 10
        //   13420: iload 13
        //   13422: istore 12
        //   13424: iload 10
        //   13426: istore_2
        //   13427: iload 20
        //   13429: istore 14
        //   13431: iload 11
        //   13433: istore 5
        //   13435: iload 12
        //   13437: istore 7
        //   13439: iload 64
        //   13441: istore 52
        //   13443: iload 58
        //   13445: istore 50
        //   13447: iload 10
        //   13449: istore_3
        //   13450: iload 23
        //   13452: istore 15
        //   13454: iload 11
        //   13456: istore 6
        //   13458: iload 12
        //   13460: istore 8
        //   13462: iload 66
        //   13464: istore 53
        //   13466: iload 59
        //   13468: istore 51
        //   13470: aload_0
        //   13471: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   13474: iconst_3
        //   13475: if_icmpeq +57 -> 13532
        //   13478: iload 10
        //   13480: istore_2
        //   13481: iload 20
        //   13483: istore 14
        //   13485: iload 11
        //   13487: istore 5
        //   13489: iload 12
        //   13491: istore 7
        //   13493: iload 64
        //   13495: istore 52
        //   13497: iload 58
        //   13499: istore 50
        //   13501: iload 10
        //   13503: istore_3
        //   13504: iload 23
        //   13506: istore 15
        //   13508: iload 11
        //   13510: istore 6
        //   13512: iload 12
        //   13514: istore 8
        //   13516: iload 66
        //   13518: istore 53
        //   13520: iload 59
        //   13522: istore 51
        //   13524: aload_0
        //   13525: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   13528: iconst_4
        //   13529: if_icmpne +412 -> 13941
        //   13532: iload 10
        //   13534: istore_2
        //   13535: iload 20
        //   13537: istore 14
        //   13539: iload 11
        //   13541: istore 5
        //   13543: iload 12
        //   13545: istore 7
        //   13547: iload 64
        //   13549: istore 52
        //   13551: iload 58
        //   13553: istore 50
        //   13555: iload 10
        //   13557: istore_3
        //   13558: iload 23
        //   13560: istore 15
        //   13562: iload 11
        //   13564: istore 6
        //   13566: iload 12
        //   13568: istore 8
        //   13570: iload 66
        //   13572: istore 53
        //   13574: iload 59
        //   13576: istore 51
        //   13578: aload_0
        //   13579: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   13582: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   13585: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   13588: ldc_w 352
        //   13591: iconst_1
        //   13592: anewarray 4	java/lang/Object
        //   13595: dup
        //   13596: iconst_0
        //   13597: aload_0
        //   13598: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   13601: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13604: aastore
        //   13605: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   13608: iconst_0
        //   13609: anewarray 4	java/lang/Object
        //   13612: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   13615: astore 79
        //   13617: iload 33
        //   13619: istore 13
        //   13621: iload 10
        //   13623: istore_2
        //   13624: iload 20
        //   13626: istore 14
        //   13628: iload 11
        //   13630: istore 5
        //   13632: iload 12
        //   13634: istore 7
        //   13636: iload 64
        //   13638: istore 52
        //   13640: iload 58
        //   13642: istore 50
        //   13644: iload 10
        //   13646: istore_3
        //   13647: iload 23
        //   13649: istore 15
        //   13651: iload 11
        //   13653: istore 6
        //   13655: iload 12
        //   13657: istore 8
        //   13659: iload 66
        //   13661: istore 53
        //   13663: iload 59
        //   13665: istore 51
        //   13667: aload 79
        //   13669: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   13672: ifeq +57 -> 13729
        //   13675: iload 10
        //   13677: istore_2
        //   13678: iload 20
        //   13680: istore 14
        //   13682: iload 11
        //   13684: istore 5
        //   13686: iload 12
        //   13688: istore 7
        //   13690: iload 64
        //   13692: istore 52
        //   13694: iload 58
        //   13696: istore 50
        //   13698: iload 10
        //   13700: istore_3
        //   13701: iload 23
        //   13703: istore 15
        //   13705: iload 11
        //   13707: istore 6
        //   13709: iload 12
        //   13711: istore 8
        //   13713: iload 66
        //   13715: istore 53
        //   13717: iload 59
        //   13719: istore 51
        //   13721: aload 79
        //   13723: iconst_0
        //   13724: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   13727: istore 13
        //   13729: iload 10
        //   13731: istore_2
        //   13732: iload 13
        //   13734: istore 14
        //   13736: iload 11
        //   13738: istore 5
        //   13740: iload 12
        //   13742: istore 7
        //   13744: iload 64
        //   13746: istore 52
        //   13748: iload 58
        //   13750: istore 50
        //   13752: iload 10
        //   13754: istore_3
        //   13755: iload 13
        //   13757: istore 15
        //   13759: iload 11
        //   13761: istore 6
        //   13763: iload 12
        //   13765: istore 8
        //   13767: iload 66
        //   13769: istore 53
        //   13771: iload 59
        //   13773: istore 51
        //   13775: aload 79
        //   13777: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   13780: iload 10
        //   13782: istore_2
        //   13783: iload 13
        //   13785: istore 14
        //   13787: iload 11
        //   13789: istore 5
        //   13791: iload 12
        //   13793: istore 7
        //   13795: iload 64
        //   13797: istore 52
        //   13799: iload 58
        //   13801: istore 50
        //   13803: iload 10
        //   13805: istore_3
        //   13806: iload 13
        //   13808: istore 15
        //   13810: iload 11
        //   13812: istore 6
        //   13814: iload 12
        //   13816: istore 8
        //   13818: iload 66
        //   13820: istore 53
        //   13822: iload 59
        //   13824: istore 51
        //   13826: aload_0
        //   13827: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   13830: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   13833: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   13836: ldc_w 358
        //   13839: bipush 6
        //   13841: anewarray 4	java/lang/Object
        //   13844: dup
        //   13845: iconst_0
        //   13846: aload_0
        //   13847: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   13850: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13853: aastore
        //   13854: dup
        //   13855: iconst_1
        //   13856: lload 73
        //   13858: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13861: aastore
        //   13862: dup
        //   13863: iconst_2
        //   13864: iload 4
        //   13866: iconst_2
        //   13867: idiv
        //   13868: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   13871: aastore
        //   13872: dup
        //   13873: iconst_3
        //   13874: aload_0
        //   13875: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   13878: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13881: aastore
        //   13882: dup
        //   13883: iconst_4
        //   13884: lload 73
        //   13886: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   13889: aastore
        //   13890: dup
        //   13891: iconst_5
        //   13892: iload 4
        //   13894: iconst_2
        //   13895: idiv
        //   13896: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   13899: aastore
        //   13900: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   13903: iconst_0
        //   13904: anewarray 4	java/lang/Object
        //   13907: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   13910: astore 79
        //   13912: iload 4
        //   13914: istore_3
        //   13915: iload 9
        //   13917: istore_2
        //   13918: iload 10
        //   13920: istore 4
        //   13922: iload 13
        //   13924: istore 7
        //   13926: iload 11
        //   13928: istore 5
        //   13930: iload 12
        //   13932: istore 6
        //   13934: iload 55
        //   13936: istore 50
        //   13938: goto +6144 -> 20082
        //   13941: iload 10
        //   13943: istore_2
        //   13944: iload 20
        //   13946: istore 14
        //   13948: iload 11
        //   13950: istore 5
        //   13952: iload 12
        //   13954: istore 7
        //   13956: iload 64
        //   13958: istore 52
        //   13960: iload 58
        //   13962: istore 50
        //   13964: iload 10
        //   13966: istore_3
        //   13967: iload 23
        //   13969: istore 15
        //   13971: iload 11
        //   13973: istore 6
        //   13975: iload 12
        //   13977: istore 8
        //   13979: iload 66
        //   13981: istore 53
        //   13983: iload 59
        //   13985: istore 51
        //   13987: aload_0
        //   13988: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   13991: iconst_1
        //   13992: if_icmpne +135 -> 14127
        //   13995: iload 10
        //   13997: istore_2
        //   13998: iload 20
        //   14000: istore 14
        //   14002: iload 11
        //   14004: istore 5
        //   14006: iload 12
        //   14008: istore 7
        //   14010: iload 64
        //   14012: istore 52
        //   14014: iload 58
        //   14016: istore 50
        //   14018: iload 10
        //   14020: istore_3
        //   14021: iload 23
        //   14023: istore 15
        //   14025: iload 11
        //   14027: istore 6
        //   14029: iload 12
        //   14031: istore 8
        //   14033: iload 66
        //   14035: istore 53
        //   14037: iload 59
        //   14039: istore 51
        //   14041: aload_0
        //   14042: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   14045: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   14048: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   14051: ldc_w 360
        //   14054: iconst_3
        //   14055: anewarray 4	java/lang/Object
        //   14058: dup
        //   14059: iconst_0
        //   14060: aload_0
        //   14061: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   14064: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   14067: aastore
        //   14068: dup
        //   14069: iconst_1
        //   14070: aload_0
        //   14071: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   14074: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14077: aastore
        //   14078: dup
        //   14079: iconst_2
        //   14080: iload 4
        //   14082: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14085: aastore
        //   14086: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   14089: iconst_0
        //   14090: anewarray 4	java/lang/Object
        //   14093: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   14096: astore 79
        //   14098: iload 4
        //   14100: istore_3
        //   14101: iload 9
        //   14103: istore_2
        //   14104: iload 10
        //   14106: istore 4
        //   14108: iload 17
        //   14110: istore 7
        //   14112: iload 11
        //   14114: istore 5
        //   14116: iload 12
        //   14118: istore 6
        //   14120: iload 55
        //   14122: istore 50
        //   14124: goto +5958 -> 20082
        //   14127: iload 10
        //   14129: istore_2
        //   14130: iload 20
        //   14132: istore 14
        //   14134: iload 11
        //   14136: istore 5
        //   14138: iload 12
        //   14140: istore 7
        //   14142: iload 64
        //   14144: istore 52
        //   14146: iload 58
        //   14148: istore 50
        //   14150: iload 10
        //   14152: istore_3
        //   14153: iload 23
        //   14155: istore 15
        //   14157: iload 11
        //   14159: istore 6
        //   14161: iload 12
        //   14163: istore 8
        //   14165: iload 66
        //   14167: istore 53
        //   14169: iload 59
        //   14171: istore 51
        //   14173: aload_0
        //   14174: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   14177: ifeq +327 -> 14504
        //   14180: iload 10
        //   14182: istore_2
        //   14183: iload 20
        //   14185: istore 14
        //   14187: iload 11
        //   14189: istore 5
        //   14191: iload 12
        //   14193: istore 7
        //   14195: iload 64
        //   14197: istore 52
        //   14199: iload 58
        //   14201: istore 50
        //   14203: iload 10
        //   14205: istore_3
        //   14206: iload 23
        //   14208: istore 15
        //   14210: iload 11
        //   14212: istore 6
        //   14214: iload 12
        //   14216: istore 8
        //   14218: iload 66
        //   14220: istore 53
        //   14222: iload 59
        //   14224: istore 51
        //   14226: aload_0
        //   14227: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   14230: ifeq +135 -> 14365
        //   14233: iload 10
        //   14235: istore_2
        //   14236: iload 20
        //   14238: istore 14
        //   14240: iload 11
        //   14242: istore 5
        //   14244: iload 12
        //   14246: istore 7
        //   14248: iload 64
        //   14250: istore 52
        //   14252: iload 58
        //   14254: istore 50
        //   14256: iload 10
        //   14258: istore_3
        //   14259: iload 23
        //   14261: istore 15
        //   14263: iload 11
        //   14265: istore 6
        //   14267: iload 12
        //   14269: istore 8
        //   14271: iload 66
        //   14273: istore 53
        //   14275: iload 59
        //   14277: istore 51
        //   14279: aload_0
        //   14280: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   14283: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   14286: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   14289: ldc_w 362
        //   14292: iconst_3
        //   14293: anewarray 4	java/lang/Object
        //   14296: dup
        //   14297: iconst_0
        //   14298: aload_0
        //   14299: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   14302: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   14305: aastore
        //   14306: dup
        //   14307: iconst_1
        //   14308: aload_0
        //   14309: getfield 35	org/vidogram/messenger/MessagesStorage$49:val$max_id	I
        //   14312: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14315: aastore
        //   14316: dup
        //   14317: iconst_2
        //   14318: iload 4
        //   14320: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14323: aastore
        //   14324: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   14327: iconst_0
        //   14328: anewarray 4	java/lang/Object
        //   14331: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   14334: astore 79
        //   14336: iload 4
        //   14338: istore_3
        //   14339: iload 9
        //   14341: istore_2
        //   14342: iload 10
        //   14344: istore 4
        //   14346: iload 17
        //   14348: istore 7
        //   14350: iload 11
        //   14352: istore 5
        //   14354: iload 12
        //   14356: istore 6
        //   14358: iload 55
        //   14360: istore 50
        //   14362: goto +5720 -> 20082
        //   14365: iload 10
        //   14367: istore_2
        //   14368: iload 20
        //   14370: istore 14
        //   14372: iload 11
        //   14374: istore 5
        //   14376: iload 12
        //   14378: istore 7
        //   14380: iload 64
        //   14382: istore 52
        //   14384: iload 58
        //   14386: istore 50
        //   14388: iload 10
        //   14390: istore_3
        //   14391: iload 23
        //   14393: istore 15
        //   14395: iload 11
        //   14397: istore 6
        //   14399: iload 12
        //   14401: istore 8
        //   14403: iload 66
        //   14405: istore 53
        //   14407: iload 59
        //   14409: istore 51
        //   14411: aload_0
        //   14412: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   14415: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   14418: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   14421: ldc_w 364
        //   14424: iconst_4
        //   14425: anewarray 4	java/lang/Object
        //   14428: dup
        //   14429: iconst_0
        //   14430: aload_0
        //   14431: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   14434: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   14437: aastore
        //   14438: dup
        //   14439: iconst_1
        //   14440: aload_0
        //   14441: getfield 43	org/vidogram/messenger/MessagesStorage$49:val$minDate	I
        //   14444: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14447: aastore
        //   14448: dup
        //   14449: iconst_2
        //   14450: iconst_0
        //   14451: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14454: aastore
        //   14455: dup
        //   14456: iconst_3
        //   14457: iload 4
        //   14459: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   14462: aastore
        //   14463: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   14466: iconst_0
        //   14467: anewarray 4	java/lang/Object
        //   14470: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   14473: astore 79
        //   14475: iload 4
        //   14477: istore_3
        //   14478: iload 9
        //   14480: istore_2
        //   14481: iload 10
        //   14483: istore 4
        //   14485: iload 17
        //   14487: istore 7
        //   14489: iload 11
        //   14491: istore 5
        //   14493: iload 12
        //   14495: istore 6
        //   14497: iload 55
        //   14499: istore 50
        //   14501: goto +5581 -> 20082
        //   14504: iload 10
        //   14506: istore_2
        //   14507: iload 20
        //   14509: istore 14
        //   14511: iload 11
        //   14513: istore 5
        //   14515: iload 12
        //   14517: istore 7
        //   14519: iload 64
        //   14521: istore 52
        //   14523: iload 58
        //   14525: istore 50
        //   14527: iload 10
        //   14529: istore_3
        //   14530: iload 23
        //   14532: istore 15
        //   14534: iload 11
        //   14536: istore 6
        //   14538: iload 12
        //   14540: istore 8
        //   14542: iload 66
        //   14544: istore 53
        //   14546: iload 59
        //   14548: istore 51
        //   14550: iload 10
        //   14552: istore 16
        //   14554: iload 18
        //   14556: istore 17
        //   14558: iload 11
        //   14560: istore 18
        //   14562: iload 12
        //   14564: istore 13
        //   14566: aload_0
        //   14567: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   14570: iconst_2
        //   14571: if_icmpne +5612 -> 20183
        //   14574: iload 10
        //   14576: istore_2
        //   14577: iload 20
        //   14579: istore 14
        //   14581: iload 11
        //   14583: istore 5
        //   14585: iload 12
        //   14587: istore 7
        //   14589: iload 64
        //   14591: istore 52
        //   14593: iload 58
        //   14595: istore 50
        //   14597: iload 10
        //   14599: istore_3
        //   14600: iload 23
        //   14602: istore 15
        //   14604: iload 11
        //   14606: istore 6
        //   14608: iload 12
        //   14610: istore 8
        //   14612: iload 66
        //   14614: istore 53
        //   14616: iload 59
        //   14618: istore 51
        //   14620: aload_0
        //   14621: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   14624: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   14627: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   14630: ldc_w 352
        //   14633: iconst_1
        //   14634: anewarray 4	java/lang/Object
        //   14637: dup
        //   14638: iconst_0
        //   14639: aload_0
        //   14640: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   14643: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   14646: aastore
        //   14647: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   14650: iconst_0
        //   14651: anewarray 4	java/lang/Object
        //   14654: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   14657: astore 79
        //   14659: iload 10
        //   14661: istore_2
        //   14662: iload 20
        //   14664: istore 14
        //   14666: iload 11
        //   14668: istore 5
        //   14670: iload 12
        //   14672: istore 7
        //   14674: iload 64
        //   14676: istore 52
        //   14678: iload 58
        //   14680: istore 50
        //   14682: iload 10
        //   14684: istore_3
        //   14685: iload 23
        //   14687: istore 15
        //   14689: iload 11
        //   14691: istore 6
        //   14693: iload 12
        //   14695: istore 8
        //   14697: iload 66
        //   14699: istore 53
        //   14701: iload 59
        //   14703: istore 51
        //   14705: aload 79
        //   14707: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   14710: ifeq +57 -> 14767
        //   14713: iload 10
        //   14715: istore_2
        //   14716: iload 20
        //   14718: istore 14
        //   14720: iload 11
        //   14722: istore 5
        //   14724: iload 12
        //   14726: istore 7
        //   14728: iload 64
        //   14730: istore 52
        //   14732: iload 58
        //   14734: istore 50
        //   14736: iload 10
        //   14738: istore_3
        //   14739: iload 23
        //   14741: istore 15
        //   14743: iload 11
        //   14745: istore 6
        //   14747: iload 12
        //   14749: istore 8
        //   14751: iload 66
        //   14753: istore 53
        //   14755: iload 59
        //   14757: istore 51
        //   14759: aload 79
        //   14761: iconst_0
        //   14762: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   14765: istore 19
        //   14767: iload 10
        //   14769: istore_2
        //   14770: iload 19
        //   14772: istore 14
        //   14774: iload 11
        //   14776: istore 5
        //   14778: iload 12
        //   14780: istore 7
        //   14782: iload 64
        //   14784: istore 52
        //   14786: iload 58
        //   14788: istore 50
        //   14790: iload 10
        //   14792: istore_3
        //   14793: iload 19
        //   14795: istore 15
        //   14797: iload 11
        //   14799: istore 6
        //   14801: iload 12
        //   14803: istore 8
        //   14805: iload 66
        //   14807: istore 53
        //   14809: iload 59
        //   14811: istore 51
        //   14813: aload 79
        //   14815: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   14818: iload 10
        //   14820: istore_2
        //   14821: iload 19
        //   14823: istore 14
        //   14825: iload 11
        //   14827: istore 5
        //   14829: iload 12
        //   14831: istore 7
        //   14833: iload 64
        //   14835: istore 52
        //   14837: iload 58
        //   14839: istore 50
        //   14841: iload 10
        //   14843: istore_3
        //   14844: iload 19
        //   14846: istore 15
        //   14848: iload 11
        //   14850: istore 6
        //   14852: iload 12
        //   14854: istore 8
        //   14856: iload 66
        //   14858: istore 53
        //   14860: iload 59
        //   14862: istore 51
        //   14864: aload_0
        //   14865: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   14868: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   14871: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   14874: ldc_w 354
        //   14877: iconst_1
        //   14878: anewarray 4	java/lang/Object
        //   14881: dup
        //   14882: iconst_0
        //   14883: aload_0
        //   14884: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   14887: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   14890: aastore
        //   14891: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   14894: iconst_0
        //   14895: anewarray 4	java/lang/Object
        //   14898: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   14901: astore 79
        //   14903: iload 10
        //   14905: istore 21
        //   14907: iload 12
        //   14909: istore 20
        //   14911: iload 10
        //   14913: istore_2
        //   14914: iload 19
        //   14916: istore 14
        //   14918: iload 11
        //   14920: istore 5
        //   14922: iload 12
        //   14924: istore 7
        //   14926: iload 64
        //   14928: istore 52
        //   14930: iload 58
        //   14932: istore 50
        //   14934: iload 10
        //   14936: istore_3
        //   14937: iload 19
        //   14939: istore 15
        //   14941: iload 11
        //   14943: istore 6
        //   14945: iload 12
        //   14947: istore 8
        //   14949: iload 66
        //   14951: istore 53
        //   14953: iload 59
        //   14955: istore 51
        //   14957: aload 79
        //   14959: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   14962: ifeq +111 -> 15073
        //   14965: iload 10
        //   14967: istore_2
        //   14968: iload 19
        //   14970: istore 14
        //   14972: iload 11
        //   14974: istore 5
        //   14976: iload 12
        //   14978: istore 7
        //   14980: iload 64
        //   14982: istore 52
        //   14984: iload 58
        //   14986: istore 50
        //   14988: iload 10
        //   14990: istore_3
        //   14991: iload 19
        //   14993: istore 15
        //   14995: iload 11
        //   14997: istore 6
        //   14999: iload 12
        //   15001: istore 8
        //   15003: iload 66
        //   15005: istore 53
        //   15007: iload 59
        //   15009: istore 51
        //   15011: aload 79
        //   15013: iconst_0
        //   15014: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   15017: istore 21
        //   15019: iload 21
        //   15021: istore_2
        //   15022: iload 19
        //   15024: istore 14
        //   15026: iload 11
        //   15028: istore 5
        //   15030: iload 12
        //   15032: istore 7
        //   15034: iload 64
        //   15036: istore 52
        //   15038: iload 58
        //   15040: istore 50
        //   15042: iload 21
        //   15044: istore_3
        //   15045: iload 19
        //   15047: istore 15
        //   15049: iload 11
        //   15051: istore 6
        //   15053: iload 12
        //   15055: istore 8
        //   15057: iload 66
        //   15059: istore 53
        //   15061: iload 59
        //   15063: istore 51
        //   15065: aload 79
        //   15067: iconst_1
        //   15068: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   15071: istore 20
        //   15073: iload 21
        //   15075: istore_2
        //   15076: iload 19
        //   15078: istore 14
        //   15080: iload 11
        //   15082: istore 5
        //   15084: iload 20
        //   15086: istore 7
        //   15088: iload 64
        //   15090: istore 52
        //   15092: iload 58
        //   15094: istore 50
        //   15096: iload 21
        //   15098: istore_3
        //   15099: iload 19
        //   15101: istore 15
        //   15103: iload 11
        //   15105: istore 6
        //   15107: iload 20
        //   15109: istore 8
        //   15111: iload 66
        //   15113: istore 53
        //   15115: iload 59
        //   15117: istore 51
        //   15119: aload 79
        //   15121: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   15124: iload 21
        //   15126: istore 16
        //   15128: iload 19
        //   15130: istore 17
        //   15132: iload 11
        //   15134: istore 18
        //   15136: iload 20
        //   15138: istore 13
        //   15140: iload 21
        //   15142: ifeq +5041 -> 20183
        //   15145: iload 21
        //   15147: istore_2
        //   15148: iload 19
        //   15150: istore 14
        //   15152: iload 11
        //   15154: istore 5
        //   15156: iload 20
        //   15158: istore 7
        //   15160: iload 64
        //   15162: istore 52
        //   15164: iload 58
        //   15166: istore 50
        //   15168: iload 21
        //   15170: istore_3
        //   15171: iload 19
        //   15173: istore 15
        //   15175: iload 11
        //   15177: istore 6
        //   15179: iload 20
        //   15181: istore 8
        //   15183: iload 66
        //   15185: istore 53
        //   15187: iload 59
        //   15189: istore 51
        //   15191: aload_0
        //   15192: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   15195: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   15198: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   15201: ldc_w 356
        //   15204: iconst_2
        //   15205: anewarray 4	java/lang/Object
        //   15208: dup
        //   15209: iconst_0
        //   15210: aload_0
        //   15211: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   15214: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   15217: aastore
        //   15218: dup
        //   15219: iconst_1
        //   15220: iload 21
        //   15222: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   15225: aastore
        //   15226: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   15229: iconst_0
        //   15230: anewarray 4	java/lang/Object
        //   15233: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   15236: astore 79
        //   15238: iload 11
        //   15240: istore 18
        //   15242: iload 21
        //   15244: istore_2
        //   15245: iload 19
        //   15247: istore 14
        //   15249: iload 11
        //   15251: istore 5
        //   15253: iload 20
        //   15255: istore 7
        //   15257: iload 64
        //   15259: istore 52
        //   15261: iload 58
        //   15263: istore 50
        //   15265: iload 21
        //   15267: istore_3
        //   15268: iload 19
        //   15270: istore 15
        //   15272: iload 11
        //   15274: istore 6
        //   15276: iload 20
        //   15278: istore 8
        //   15280: iload 66
        //   15282: istore 53
        //   15284: iload 59
        //   15286: istore 51
        //   15288: aload 79
        //   15290: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   15293: ifeq +57 -> 15350
        //   15296: iload 21
        //   15298: istore_2
        //   15299: iload 19
        //   15301: istore 14
        //   15303: iload 11
        //   15305: istore 5
        //   15307: iload 20
        //   15309: istore 7
        //   15311: iload 64
        //   15313: istore 52
        //   15315: iload 58
        //   15317: istore 50
        //   15319: iload 21
        //   15321: istore_3
        //   15322: iload 19
        //   15324: istore 15
        //   15326: iload 11
        //   15328: istore 6
        //   15330: iload 20
        //   15332: istore 8
        //   15334: iload 66
        //   15336: istore 53
        //   15338: iload 59
        //   15340: istore 51
        //   15342: aload 79
        //   15344: iconst_0
        //   15345: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   15348: istore 18
        //   15350: iload 21
        //   15352: istore_2
        //   15353: iload 19
        //   15355: istore 14
        //   15357: iload 18
        //   15359: istore 5
        //   15361: iload 20
        //   15363: istore 7
        //   15365: iload 64
        //   15367: istore 52
        //   15369: iload 58
        //   15371: istore 50
        //   15373: iload 21
        //   15375: istore_3
        //   15376: iload 19
        //   15378: istore 15
        //   15380: iload 18
        //   15382: istore 6
        //   15384: iload 20
        //   15386: istore 8
        //   15388: iload 66
        //   15390: istore 53
        //   15392: iload 59
        //   15394: istore 51
        //   15396: aload 79
        //   15398: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   15401: iload 21
        //   15403: istore 16
        //   15405: iload 19
        //   15407: istore 17
        //   15409: iload 20
        //   15411: istore 13
        //   15413: goto +4770 -> 20183
        //   15416: iload 16
        //   15418: istore_2
        //   15419: iload 17
        //   15421: istore 14
        //   15423: iload 18
        //   15425: istore 5
        //   15427: iload 13
        //   15429: istore 7
        //   15431: iload 64
        //   15433: istore 52
        //   15435: iload 58
        //   15437: istore 50
        //   15439: iload 16
        //   15441: istore_3
        //   15442: iload 17
        //   15444: istore 15
        //   15446: iload 18
        //   15448: istore 6
        //   15450: iload 13
        //   15452: istore 8
        //   15454: iload 66
        //   15456: istore 53
        //   15458: iload 59
        //   15460: istore 51
        //   15462: iload 4
        //   15464: iload 18
        //   15466: bipush 10
        //   15468: iadd
        //   15469: invokestatic 294	java/lang/Math:max	(II)I
        //   15472: istore 10
        //   15474: iload 10
        //   15476: istore_3
        //   15477: iload 18
        //   15479: iload 22
        //   15481: if_icmpge +4426 -> 19907
        //   15484: iconst_0
        //   15485: istore 5
        //   15487: iconst_0
        //   15488: istore 4
        //   15490: iconst_0
        //   15491: istore 7
        //   15493: iconst_0
        //   15494: istore_2
        //   15495: iload_3
        //   15496: istore 18
        //   15498: iload 4
        //   15500: istore 15
        //   15502: iload 7
        //   15504: istore 8
        //   15506: iload 5
        //   15508: istore 14
        //   15510: iload 13
        //   15512: istore 11
        //   15514: iload 65
        //   15516: istore 51
        //   15518: iload 62
        //   15520: istore 53
        //   15522: iload_3
        //   15523: istore 19
        //   15525: iload 4
        //   15527: istore 17
        //   15529: iload 7
        //   15531: istore 10
        //   15533: iload 5
        //   15535: istore 16
        //   15537: iload 13
        //   15539: istore 12
        //   15541: iload 56
        //   15543: istore 52
        //   15545: aload_0
        //   15546: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   15549: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   15552: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   15555: ldc_w 366
        //   15558: iconst_3
        //   15559: anewarray 4	java/lang/Object
        //   15562: dup
        //   15563: iconst_0
        //   15564: aload_0
        //   15565: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   15568: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   15571: aastore
        //   15572: dup
        //   15573: iconst_1
        //   15574: iload_2
        //   15575: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   15578: aastore
        //   15579: dup
        //   15580: iconst_2
        //   15581: iload_3
        //   15582: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   15585: aastore
        //   15586: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   15589: iconst_0
        //   15590: anewarray 4	java/lang/Object
        //   15593: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   15596: astore 79
        //   15598: iload 9
        //   15600: istore_2
        //   15601: iload 13
        //   15603: istore 6
        //   15605: iload 55
        //   15607: istore 50
        //   15609: goto +4473 -> 20082
        //   15612: iload 18
        //   15614: iload 4
        //   15616: isub
        //   15617: istore_2
        //   15618: iload 4
        //   15620: bipush 10
        //   15622: iadd
        //   15623: istore_3
        //   15624: iload 16
        //   15626: istore 4
        //   15628: iload 17
        //   15630: istore 7
        //   15632: iload 18
        //   15634: istore 5
        //   15636: goto -141 -> 15495
        //   15639: iload_3
        //   15640: istore 16
        //   15642: iload_1
        //   15643: istore 17
        //   15645: iload 4
        //   15647: istore 18
        //   15649: iload 7
        //   15651: istore 15
        //   15653: iload 5
        //   15655: istore 19
        //   15657: iload 6
        //   15659: istore 20
        //   15661: iload 50
        //   15663: istore 53
        //   15665: iload 49
        //   15667: istore 54
        //   15669: iload_3
        //   15670: istore 9
        //   15672: iload_1
        //   15673: istore 10
        //   15675: iload 4
        //   15677: istore 11
        //   15679: iload 7
        //   15681: istore 12
        //   15683: iload 5
        //   15685: istore 13
        //   15687: iload 6
        //   15689: istore 14
        //   15691: iload 50
        //   15693: istore 51
        //   15695: iload 49
        //   15697: istore 52
        //   15699: aload 87
        //   15701: aload 88
        //   15703: getfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   15706: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   15709: invokevirtual 226	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   15712: ifne +77 -> 15789
        //   15715: iload_3
        //   15716: istore 16
        //   15718: iload_1
        //   15719: istore 17
        //   15721: iload 4
        //   15723: istore 18
        //   15725: iload 7
        //   15727: istore 15
        //   15729: iload 5
        //   15731: istore 19
        //   15733: iload 6
        //   15735: istore 20
        //   15737: iload 50
        //   15739: istore 53
        //   15741: iload 49
        //   15743: istore 54
        //   15745: iload_3
        //   15746: istore 9
        //   15748: iload_1
        //   15749: istore 10
        //   15751: iload 4
        //   15753: istore 11
        //   15755: iload 7
        //   15757: istore 12
        //   15759: iload 5
        //   15761: istore 13
        //   15763: iload 6
        //   15765: istore 14
        //   15767: iload 50
        //   15769: istore 51
        //   15771: iload 49
        //   15773: istore 52
        //   15775: aload 87
        //   15777: aload 88
        //   15779: getfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   15782: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   15785: invokevirtual 196	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   15788: pop
        //   15789: iload_3
        //   15790: istore 16
        //   15792: iload_1
        //   15793: istore 17
        //   15795: iload 4
        //   15797: istore 18
        //   15799: iload 7
        //   15801: istore 15
        //   15803: iload 5
        //   15805: istore 19
        //   15807: iload 6
        //   15809: istore 20
        //   15811: iload 50
        //   15813: istore 53
        //   15815: iload 49
        //   15817: istore 54
        //   15819: iload_3
        //   15820: istore 9
        //   15822: iload_1
        //   15823: istore 10
        //   15825: iload 4
        //   15827: istore 11
        //   15829: iload 7
        //   15831: istore 12
        //   15833: iload 5
        //   15835: istore 13
        //   15837: iload 6
        //   15839: istore 14
        //   15841: iload 50
        //   15843: istore 51
        //   15845: iload 49
        //   15847: istore 52
        //   15849: aload 86
        //   15851: aload 88
        //   15853: getfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   15856: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   15859: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   15862: checkcast 63	java/util/ArrayList
        //   15865: astore 81
        //   15867: aload 81
        //   15869: astore 80
        //   15871: aload 81
        //   15873: ifnonnull +148 -> 16021
        //   15876: iload_3
        //   15877: istore 16
        //   15879: iload_1
        //   15880: istore 17
        //   15882: iload 4
        //   15884: istore 18
        //   15886: iload 7
        //   15888: istore 15
        //   15890: iload 5
        //   15892: istore 19
        //   15894: iload 6
        //   15896: istore 20
        //   15898: iload 50
        //   15900: istore 53
        //   15902: iload 49
        //   15904: istore 54
        //   15906: iload_3
        //   15907: istore 9
        //   15909: iload_1
        //   15910: istore 10
        //   15912: iload 4
        //   15914: istore 11
        //   15916: iload 7
        //   15918: istore 12
        //   15920: iload 5
        //   15922: istore 13
        //   15924: iload 6
        //   15926: istore 14
        //   15928: iload 50
        //   15930: istore 51
        //   15932: iload 49
        //   15934: istore 52
        //   15936: new 63	java/util/ArrayList
        //   15939: dup
        //   15940: invokespecial 64	java/util/ArrayList:<init>	()V
        //   15943: astore 80
        //   15945: iload_3
        //   15946: istore 16
        //   15948: iload_1
        //   15949: istore 17
        //   15951: iload 4
        //   15953: istore 18
        //   15955: iload 7
        //   15957: istore 15
        //   15959: iload 5
        //   15961: istore 19
        //   15963: iload 6
        //   15965: istore 20
        //   15967: iload 50
        //   15969: istore 53
        //   15971: iload 49
        //   15973: istore 54
        //   15975: iload_3
        //   15976: istore 9
        //   15978: iload_1
        //   15979: istore 10
        //   15981: iload 4
        //   15983: istore 11
        //   15985: iload 7
        //   15987: istore 12
        //   15989: iload 5
        //   15991: istore 13
        //   15993: iload 6
        //   15995: istore 14
        //   15997: iload 50
        //   15999: istore 51
        //   16001: iload 49
        //   16003: istore 52
        //   16005: aload 86
        //   16007: aload 88
        //   16009: getfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   16012: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   16015: aload 80
        //   16017: invokevirtual 234	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
        //   16020: pop
        //   16021: iload_3
        //   16022: istore 16
        //   16024: iload_1
        //   16025: istore 17
        //   16027: iload 4
        //   16029: istore 18
        //   16031: iload 7
        //   16033: istore 15
        //   16035: iload 5
        //   16037: istore 19
        //   16039: iload 6
        //   16041: istore 20
        //   16043: iload 50
        //   16045: istore 53
        //   16047: iload 49
        //   16049: istore 54
        //   16051: iload_3
        //   16052: istore 9
        //   16054: iload_1
        //   16055: istore 10
        //   16057: iload 4
        //   16059: istore 11
        //   16061: iload 7
        //   16063: istore 12
        //   16065: iload 5
        //   16067: istore 13
        //   16069: iload 6
        //   16071: istore 14
        //   16073: iload 50
        //   16075: istore 51
        //   16077: iload 49
        //   16079: istore 52
        //   16081: aload 80
        //   16083: aload 88
        //   16085: invokevirtual 196	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   16088: pop
        //   16089: goto -10123 -> 5966
        //   16092: astore 79
        //   16094: iload 52
        //   16096: istore 49
        //   16098: iload 14
        //   16100: istore 7
        //   16102: iload 13
        //   16104: istore 5
        //   16106: iload 11
        //   16108: istore_2
        //   16109: iload 10
        //   16111: istore_3
        //   16112: iload 9
        //   16114: istore_1
        //   16115: invokestatic 280	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   16118: aload 82
        //   16120: aload_0
        //   16121: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   16124: iload_1
        //   16125: iload_3
        //   16126: aload_0
        //   16127: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   16130: iconst_1
        //   16131: aload_0
        //   16132: getfield 47	org/vidogram/messenger/MessagesStorage$49:val$classGuid	I
        //   16135: iload_2
        //   16136: iload 12
        //   16138: iload 5
        //   16140: iload 7
        //   16142: aload_0
        //   16143: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   16146: aload_0
        //   16147: getfield 37	org/vidogram/messenger/MessagesStorage$49:val$isChannel	Z
        //   16150: iload 51
        //   16152: aload_0
        //   16153: getfield 49	org/vidogram/messenger/MessagesStorage$49:val$loadIndex	I
        //   16156: iload 49
        //   16158: invokevirtual 284	org/vidogram/messenger/MessagesController:processLoadedMessages	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIZIIIIIIZZIZ)V
        //   16161: aload 79
        //   16163: athrow
        //   16164: iload_3
        //   16165: istore 16
        //   16167: iload_1
        //   16168: istore 17
        //   16170: iload 4
        //   16172: istore 18
        //   16174: iload 7
        //   16176: istore 15
        //   16178: iload 5
        //   16180: istore 19
        //   16182: iload 6
        //   16184: istore 20
        //   16186: iload 50
        //   16188: istore 53
        //   16190: iload 49
        //   16192: istore 54
        //   16194: iload_3
        //   16195: istore 9
        //   16197: iload_1
        //   16198: istore 10
        //   16200: iload 4
        //   16202: istore 11
        //   16204: iload 7
        //   16206: istore 12
        //   16208: iload 5
        //   16210: istore 13
        //   16212: iload 6
        //   16214: istore 14
        //   16216: iload 50
        //   16218: istore 51
        //   16220: iload 49
        //   16222: istore 52
        //   16224: aload 79
        //   16226: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   16229: iload_3
        //   16230: istore 16
        //   16232: iload_1
        //   16233: istore 17
        //   16235: iload 4
        //   16237: istore 18
        //   16239: iload 7
        //   16241: istore 15
        //   16243: iload 5
        //   16245: istore 19
        //   16247: iload 6
        //   16249: istore 20
        //   16251: iload 50
        //   16253: istore 53
        //   16255: iload 49
        //   16257: istore 54
        //   16259: iload_3
        //   16260: istore 9
        //   16262: iload_1
        //   16263: istore 10
        //   16265: iload 4
        //   16267: istore 11
        //   16269: iload 7
        //   16271: istore 12
        //   16273: iload 5
        //   16275: istore 13
        //   16277: iload 6
        //   16279: istore 14
        //   16281: iload 50
        //   16283: istore 51
        //   16285: iload 49
        //   16287: istore 52
        //   16289: aload 82
        //   16291: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   16294: new 13	org/vidogram/messenger/MessagesStorage$49$1
        //   16297: dup
        //   16298: aload_0
        //   16299: invokespecial 369	org/vidogram/messenger/MessagesStorage$49$1:<init>	(Lorg/vidogram/messenger/MessagesStorage$49;)V
        //   16302: invokestatic 375	java/util/Collections:sort	(Ljava/util/List;Ljava/util/Comparator;)V
        //   16305: iload 48
        //   16307: ifeq +997 -> 17304
        //   16310: iload_3
        //   16311: istore 16
        //   16313: iload_1
        //   16314: istore 17
        //   16316: iload 4
        //   16318: istore 18
        //   16320: iload 7
        //   16322: istore 15
        //   16324: iload 5
        //   16326: istore 19
        //   16328: iload 6
        //   16330: istore 20
        //   16332: iload 50
        //   16334: istore 53
        //   16336: iload 49
        //   16338: istore 54
        //   16340: iload_3
        //   16341: istore 9
        //   16343: iload_1
        //   16344: istore 10
        //   16346: iload 4
        //   16348: istore 11
        //   16350: iload 7
        //   16352: istore 12
        //   16354: iload 5
        //   16356: istore 13
        //   16358: iload 6
        //   16360: istore 14
        //   16362: iload 50
        //   16364: istore 51
        //   16366: iload 49
        //   16368: istore 52
        //   16370: aload_0
        //   16371: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   16374: iconst_3
        //   16375: if_icmpeq +144 -> 16519
        //   16378: iload_3
        //   16379: istore 16
        //   16381: iload_1
        //   16382: istore 17
        //   16384: iload 4
        //   16386: istore 18
        //   16388: iload 7
        //   16390: istore 15
        //   16392: iload 5
        //   16394: istore 19
        //   16396: iload 6
        //   16398: istore 20
        //   16400: iload 50
        //   16402: istore 53
        //   16404: iload 49
        //   16406: istore 54
        //   16408: iload_3
        //   16409: istore 9
        //   16411: iload_1
        //   16412: istore 10
        //   16414: iload 4
        //   16416: istore 11
        //   16418: iload 7
        //   16420: istore 12
        //   16422: iload 5
        //   16424: istore 13
        //   16426: iload 6
        //   16428: istore 14
        //   16430: iload 50
        //   16432: istore 51
        //   16434: iload 49
        //   16436: istore 52
        //   16438: aload_0
        //   16439: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   16442: iconst_4
        //   16443: if_icmpeq +76 -> 16519
        //   16446: iload_3
        //   16447: istore 16
        //   16449: iload_1
        //   16450: istore 17
        //   16452: iload 4
        //   16454: istore 18
        //   16456: iload 7
        //   16458: istore 15
        //   16460: iload 5
        //   16462: istore 19
        //   16464: iload 6
        //   16466: istore 20
        //   16468: iload 50
        //   16470: istore 53
        //   16472: iload 49
        //   16474: istore 54
        //   16476: iload_3
        //   16477: istore 9
        //   16479: iload_1
        //   16480: istore 10
        //   16482: iload 4
        //   16484: istore 11
        //   16486: iload 7
        //   16488: istore 12
        //   16490: iload 5
        //   16492: istore 13
        //   16494: iload 6
        //   16496: istore 14
        //   16498: iload 50
        //   16500: istore 51
        //   16502: iload 49
        //   16504: istore 52
        //   16506: aload_0
        //   16507: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   16510: iconst_2
        //   16511: if_icmpne +517 -> 17028
        //   16514: iload 49
        //   16516: ifeq +512 -> 17028
        //   16519: iload_3
        //   16520: istore 16
        //   16522: iload_1
        //   16523: istore 17
        //   16525: iload 4
        //   16527: istore 18
        //   16529: iload 7
        //   16531: istore 15
        //   16533: iload 5
        //   16535: istore 19
        //   16537: iload 6
        //   16539: istore 20
        //   16541: iload 50
        //   16543: istore 53
        //   16545: iload 49
        //   16547: istore 54
        //   16549: iload_3
        //   16550: istore 9
        //   16552: iload_1
        //   16553: istore 10
        //   16555: iload 4
        //   16557: istore 11
        //   16559: iload 7
        //   16561: istore 12
        //   16563: iload 5
        //   16565: istore 13
        //   16567: iload 6
        //   16569: istore 14
        //   16571: iload 50
        //   16573: istore 51
        //   16575: iload 49
        //   16577: istore 52
        //   16579: aload 82
        //   16581: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   16584: invokevirtual 378	java/util/ArrayList:isEmpty	()Z
        //   16587: ifne +441 -> 17028
        //   16590: iload_3
        //   16591: istore 16
        //   16593: iload_1
        //   16594: istore 17
        //   16596: iload 4
        //   16598: istore 18
        //   16600: iload 7
        //   16602: istore 15
        //   16604: iload 5
        //   16606: istore 19
        //   16608: iload 6
        //   16610: istore 20
        //   16612: iload 50
        //   16614: istore 53
        //   16616: iload 49
        //   16618: istore 54
        //   16620: iload_3
        //   16621: istore 9
        //   16623: iload_1
        //   16624: istore 10
        //   16626: iload 4
        //   16628: istore 11
        //   16630: iload 7
        //   16632: istore 12
        //   16634: iload 5
        //   16636: istore 13
        //   16638: iload 6
        //   16640: istore 14
        //   16642: iload 50
        //   16644: istore 51
        //   16646: iload 49
        //   16648: istore 52
        //   16650: aload 82
        //   16652: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   16655: aload 82
        //   16657: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   16660: invokevirtual 381	java/util/ArrayList:size	()I
        //   16663: iconst_1
        //   16664: isub
        //   16665: invokevirtual 384	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   16668: checkcast 157	org/vidogram/tgnet/TLRPC$Message
        //   16671: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   16674: istore 8
        //   16676: iload_3
        //   16677: istore 16
        //   16679: iload_1
        //   16680: istore 17
        //   16682: iload 4
        //   16684: istore 18
        //   16686: iload 7
        //   16688: istore 15
        //   16690: iload 5
        //   16692: istore 19
        //   16694: iload 6
        //   16696: istore 20
        //   16698: iload 50
        //   16700: istore 53
        //   16702: iload 49
        //   16704: istore 54
        //   16706: iload_3
        //   16707: istore 9
        //   16709: iload_1
        //   16710: istore 10
        //   16712: iload 4
        //   16714: istore 11
        //   16716: iload 7
        //   16718: istore 12
        //   16720: iload 5
        //   16722: istore 13
        //   16724: iload 6
        //   16726: istore 14
        //   16728: iload 50
        //   16730: istore 51
        //   16732: iload 49
        //   16734: istore 52
        //   16736: aload 82
        //   16738: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   16741: iconst_0
        //   16742: invokevirtual 384	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   16745: checkcast 157	org/vidogram/tgnet/TLRPC$Message
        //   16748: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   16751: istore 21
        //   16753: iload 8
        //   16755: iload_2
        //   16756: if_icmpgt +9 -> 16765
        //   16759: iload 21
        //   16761: iload_2
        //   16762: if_icmpge +266 -> 17028
        //   16765: iload_3
        //   16766: istore 16
        //   16768: iload_1
        //   16769: istore 17
        //   16771: iload 4
        //   16773: istore 18
        //   16775: iload 7
        //   16777: istore 15
        //   16779: iload 5
        //   16781: istore 19
        //   16783: iload 6
        //   16785: istore 20
        //   16787: iload 50
        //   16789: istore 53
        //   16791: iload 49
        //   16793: istore 54
        //   16795: iload_3
        //   16796: istore 9
        //   16798: iload_1
        //   16799: istore 10
        //   16801: iload 4
        //   16803: istore 11
        //   16805: iload 7
        //   16807: istore 12
        //   16809: iload 5
        //   16811: istore 13
        //   16813: iload 6
        //   16815: istore 14
        //   16817: iload 50
        //   16819: istore 51
        //   16821: iload 49
        //   16823: istore 52
        //   16825: aload 87
        //   16827: invokevirtual 268	java/util/ArrayList:clear	()V
        //   16830: iload_3
        //   16831: istore 16
        //   16833: iload_1
        //   16834: istore 17
        //   16836: iload 4
        //   16838: istore 18
        //   16840: iload 7
        //   16842: istore 15
        //   16844: iload 5
        //   16846: istore 19
        //   16848: iload 6
        //   16850: istore 20
        //   16852: iload 50
        //   16854: istore 53
        //   16856: iload 49
        //   16858: istore 54
        //   16860: iload_3
        //   16861: istore 9
        //   16863: iload_1
        //   16864: istore 10
        //   16866: iload 4
        //   16868: istore 11
        //   16870: iload 7
        //   16872: istore 12
        //   16874: iload 5
        //   16876: istore 13
        //   16878: iload 6
        //   16880: istore 14
        //   16882: iload 50
        //   16884: istore 51
        //   16886: iload 49
        //   16888: istore 52
        //   16890: aload 83
        //   16892: invokevirtual 268	java/util/ArrayList:clear	()V
        //   16895: iload_3
        //   16896: istore 16
        //   16898: iload_1
        //   16899: istore 17
        //   16901: iload 4
        //   16903: istore 18
        //   16905: iload 7
        //   16907: istore 15
        //   16909: iload 5
        //   16911: istore 19
        //   16913: iload 6
        //   16915: istore 20
        //   16917: iload 50
        //   16919: istore 53
        //   16921: iload 49
        //   16923: istore 54
        //   16925: iload_3
        //   16926: istore 9
        //   16928: iload_1
        //   16929: istore 10
        //   16931: iload 4
        //   16933: istore 11
        //   16935: iload 7
        //   16937: istore 12
        //   16939: iload 5
        //   16941: istore 13
        //   16943: iload 6
        //   16945: istore 14
        //   16947: iload 50
        //   16949: istore 51
        //   16951: iload 49
        //   16953: istore 52
        //   16955: aload 84
        //   16957: invokevirtual 268	java/util/ArrayList:clear	()V
        //   16960: iload_3
        //   16961: istore 16
        //   16963: iload_1
        //   16964: istore 17
        //   16966: iload 4
        //   16968: istore 18
        //   16970: iload 7
        //   16972: istore 15
        //   16974: iload 5
        //   16976: istore 19
        //   16978: iload 6
        //   16980: istore 20
        //   16982: iload 50
        //   16984: istore 53
        //   16986: iload 49
        //   16988: istore 54
        //   16990: iload_3
        //   16991: istore 9
        //   16993: iload_1
        //   16994: istore 10
        //   16996: iload 4
        //   16998: istore 11
        //   17000: iload 7
        //   17002: istore 12
        //   17004: iload 5
        //   17006: istore 13
        //   17008: iload 6
        //   17010: istore 14
        //   17012: iload 50
        //   17014: istore 51
        //   17016: iload 49
        //   17018: istore 52
        //   17020: aload 82
        //   17022: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   17025: invokevirtual 268	java/util/ArrayList:clear	()V
        //   17028: iload_3
        //   17029: istore 16
        //   17031: iload_1
        //   17032: istore 17
        //   17034: iload 4
        //   17036: istore 18
        //   17038: iload 7
        //   17040: istore 15
        //   17042: iload 5
        //   17044: istore 19
        //   17046: iload 6
        //   17048: istore 20
        //   17050: iload 50
        //   17052: istore 53
        //   17054: iload 49
        //   17056: istore 54
        //   17058: iload_3
        //   17059: istore 9
        //   17061: iload_1
        //   17062: istore 10
        //   17064: iload 4
        //   17066: istore 11
        //   17068: iload 7
        //   17070: istore 12
        //   17072: iload 5
        //   17074: istore 13
        //   17076: iload 6
        //   17078: istore 14
        //   17080: iload 50
        //   17082: istore 51
        //   17084: iload 49
        //   17086: istore 52
        //   17088: aload_0
        //   17089: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   17092: iconst_4
        //   17093: if_icmpeq +71 -> 17164
        //   17096: iload_3
        //   17097: istore 16
        //   17099: iload_1
        //   17100: istore 17
        //   17102: iload 4
        //   17104: istore 18
        //   17106: iload 7
        //   17108: istore 15
        //   17110: iload 5
        //   17112: istore 19
        //   17114: iload 6
        //   17116: istore 20
        //   17118: iload 50
        //   17120: istore 53
        //   17122: iload 49
        //   17124: istore 54
        //   17126: iload_3
        //   17127: istore 9
        //   17129: iload_1
        //   17130: istore 10
        //   17132: iload 4
        //   17134: istore 11
        //   17136: iload 7
        //   17138: istore 12
        //   17140: iload 5
        //   17142: istore 13
        //   17144: iload 6
        //   17146: istore 14
        //   17148: iload 50
        //   17150: istore 51
        //   17152: iload 49
        //   17154: istore 52
        //   17156: aload_0
        //   17157: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   17160: iconst_3
        //   17161: if_icmpne +143 -> 17304
        //   17164: iload_3
        //   17165: istore 16
        //   17167: iload_1
        //   17168: istore 17
        //   17170: iload 4
        //   17172: istore 18
        //   17174: iload 7
        //   17176: istore 15
        //   17178: iload 5
        //   17180: istore 19
        //   17182: iload 6
        //   17184: istore 20
        //   17186: iload 50
        //   17188: istore 53
        //   17190: iload 49
        //   17192: istore 54
        //   17194: iload_3
        //   17195: istore 9
        //   17197: iload_1
        //   17198: istore 10
        //   17200: iload 4
        //   17202: istore 11
        //   17204: iload 7
        //   17206: istore 12
        //   17208: iload 5
        //   17210: istore 13
        //   17212: iload 6
        //   17214: istore 14
        //   17216: iload 50
        //   17218: istore 51
        //   17220: iload 49
        //   17222: istore 52
        //   17224: aload 82
        //   17226: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   17229: invokevirtual 381	java/util/ArrayList:size	()I
        //   17232: iconst_1
        //   17233: if_icmpne +71 -> 17304
        //   17236: iload_3
        //   17237: istore 16
        //   17239: iload_1
        //   17240: istore 17
        //   17242: iload 4
        //   17244: istore 18
        //   17246: iload 7
        //   17248: istore 15
        //   17250: iload 5
        //   17252: istore 19
        //   17254: iload 6
        //   17256: istore 20
        //   17258: iload 50
        //   17260: istore 53
        //   17262: iload 49
        //   17264: istore 54
        //   17266: iload_3
        //   17267: istore 9
        //   17269: iload_1
        //   17270: istore 10
        //   17272: iload 4
        //   17274: istore 11
        //   17276: iload 7
        //   17278: istore 12
        //   17280: iload 5
        //   17282: istore 13
        //   17284: iload 6
        //   17286: istore 14
        //   17288: iload 50
        //   17290: istore 51
        //   17292: iload 49
        //   17294: istore 52
        //   17296: aload 82
        //   17298: getfield 192	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   17301: invokevirtual 268	java/util/ArrayList:clear	()V
        //   17304: iload_3
        //   17305: istore 16
        //   17307: iload_1
        //   17308: istore 17
        //   17310: iload 4
        //   17312: istore 18
        //   17314: iload 7
        //   17316: istore 15
        //   17318: iload 5
        //   17320: istore 19
        //   17322: iload 6
        //   17324: istore 20
        //   17326: iload 50
        //   17328: istore 53
        //   17330: iload 49
        //   17332: istore 54
        //   17334: iload_3
        //   17335: istore 9
        //   17337: iload_1
        //   17338: istore 10
        //   17340: iload 4
        //   17342: istore 11
        //   17344: iload 7
        //   17346: istore 12
        //   17348: iload 5
        //   17350: istore 13
        //   17352: iload 6
        //   17354: istore 14
        //   17356: iload 50
        //   17358: istore 51
        //   17360: iload 49
        //   17362: istore 52
        //   17364: aload 87
        //   17366: invokevirtual 378	java/util/ArrayList:isEmpty	()Z
        //   17369: ifne +2013 -> 19382
        //   17372: iload_3
        //   17373: istore 16
        //   17375: iload_1
        //   17376: istore 17
        //   17378: iload 4
        //   17380: istore 18
        //   17382: iload 7
        //   17384: istore 15
        //   17386: iload 5
        //   17388: istore 19
        //   17390: iload 6
        //   17392: istore 20
        //   17394: iload 50
        //   17396: istore 53
        //   17398: iload 49
        //   17400: istore 54
        //   17402: iload_3
        //   17403: istore 9
        //   17405: iload_1
        //   17406: istore 10
        //   17408: iload 4
        //   17410: istore 11
        //   17412: iload 7
        //   17414: istore 12
        //   17416: iload 5
        //   17418: istore 13
        //   17420: iload 6
        //   17422: istore 14
        //   17424: iload 50
        //   17426: istore 51
        //   17428: iload 49
        //   17430: istore 52
        //   17432: aload 85
        //   17434: invokevirtual 385	java/util/HashMap:isEmpty	()Z
        //   17437: ifne +966 -> 18403
        //   17440: iload_3
        //   17441: istore 16
        //   17443: iload_1
        //   17444: istore 17
        //   17446: iload 4
        //   17448: istore 18
        //   17450: iload 7
        //   17452: istore 15
        //   17454: iload 5
        //   17456: istore 19
        //   17458: iload 6
        //   17460: istore 20
        //   17462: iload 50
        //   17464: istore 53
        //   17466: iload 49
        //   17468: istore 54
        //   17470: iload_3
        //   17471: istore 9
        //   17473: iload_1
        //   17474: istore 10
        //   17476: iload 4
        //   17478: istore 11
        //   17480: iload 7
        //   17482: istore 12
        //   17484: iload 5
        //   17486: istore 13
        //   17488: iload 6
        //   17490: istore 14
        //   17492: iload 50
        //   17494: istore 51
        //   17496: iload 49
        //   17498: istore 52
        //   17500: aload_0
        //   17501: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   17504: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   17507: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   17510: ldc_w 387
        //   17513: iconst_1
        //   17514: anewarray 4	java/lang/Object
        //   17517: dup
        //   17518: iconst_0
        //   17519: ldc_w 389
        //   17522: aload 87
        //   17524: invokestatic 395	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   17527: aastore
        //   17528: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   17531: iconst_0
        //   17532: anewarray 4	java/lang/Object
        //   17535: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   17538: astore 79
        //   17540: iload_3
        //   17541: istore 16
        //   17543: iload_1
        //   17544: istore 17
        //   17546: iload 4
        //   17548: istore 18
        //   17550: iload 7
        //   17552: istore 15
        //   17554: iload 5
        //   17556: istore 19
        //   17558: iload 6
        //   17560: istore 20
        //   17562: iload 50
        //   17564: istore 53
        //   17566: iload 49
        //   17568: istore 54
        //   17570: iload_3
        //   17571: istore 9
        //   17573: iload_1
        //   17574: istore 10
        //   17576: iload 4
        //   17578: istore 11
        //   17580: iload 7
        //   17582: istore 12
        //   17584: iload 5
        //   17586: istore 13
        //   17588: iload 6
        //   17590: istore 14
        //   17592: iload 50
        //   17594: istore 51
        //   17596: iload 49
        //   17598: istore 52
        //   17600: aload 79
        //   17602: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   17605: ifeq +1271 -> 18876
        //   17608: iload_3
        //   17609: istore 16
        //   17611: iload_1
        //   17612: istore 17
        //   17614: iload 4
        //   17616: istore 18
        //   17618: iload 7
        //   17620: istore 15
        //   17622: iload 5
        //   17624: istore 19
        //   17626: iload 6
        //   17628: istore 20
        //   17630: iload 50
        //   17632: istore 53
        //   17634: iload 49
        //   17636: istore 54
        //   17638: iload_3
        //   17639: istore 9
        //   17641: iload_1
        //   17642: istore 10
        //   17644: iload 4
        //   17646: istore 11
        //   17648: iload 7
        //   17650: istore 12
        //   17652: iload 5
        //   17654: istore 13
        //   17656: iload 6
        //   17658: istore 14
        //   17660: iload 50
        //   17662: istore 51
        //   17664: iload 49
        //   17666: istore 52
        //   17668: aload 79
        //   17670: iconst_0
        //   17671: invokevirtual 149	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   17674: astore 81
        //   17676: aload 81
        //   17678: ifnull -138 -> 17540
        //   17681: iload_3
        //   17682: istore 16
        //   17684: iload_1
        //   17685: istore 17
        //   17687: iload 4
        //   17689: istore 18
        //   17691: iload 7
        //   17693: istore 15
        //   17695: iload 5
        //   17697: istore 19
        //   17699: iload 6
        //   17701: istore 20
        //   17703: iload 50
        //   17705: istore 53
        //   17707: iload 49
        //   17709: istore 54
        //   17711: iload_3
        //   17712: istore 9
        //   17714: iload_1
        //   17715: istore 10
        //   17717: iload 4
        //   17719: istore 11
        //   17721: iload 7
        //   17723: istore 12
        //   17725: iload 5
        //   17727: istore 13
        //   17729: iload 6
        //   17731: istore 14
        //   17733: iload 50
        //   17735: istore 51
        //   17737: iload 49
        //   17739: istore 52
        //   17741: aload 81
        //   17743: aload 81
        //   17745: iconst_0
        //   17746: invokevirtual 155	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   17749: iconst_0
        //   17750: invokestatic 161	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   17753: astore 80
        //   17755: iload_3
        //   17756: istore 16
        //   17758: iload_1
        //   17759: istore 17
        //   17761: iload 4
        //   17763: istore 18
        //   17765: iload 7
        //   17767: istore 15
        //   17769: iload 5
        //   17771: istore 19
        //   17773: iload 6
        //   17775: istore 20
        //   17777: iload 50
        //   17779: istore 53
        //   17781: iload 49
        //   17783: istore 54
        //   17785: iload_3
        //   17786: istore 9
        //   17788: iload_1
        //   17789: istore 10
        //   17791: iload 4
        //   17793: istore 11
        //   17795: iload 7
        //   17797: istore 12
        //   17799: iload 5
        //   17801: istore 13
        //   17803: iload 6
        //   17805: istore 14
        //   17807: iload 50
        //   17809: istore 51
        //   17811: iload 49
        //   17813: istore 52
        //   17815: aload 81
        //   17817: invokevirtual 164	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   17820: iload_3
        //   17821: istore 16
        //   17823: iload_1
        //   17824: istore 17
        //   17826: iload 4
        //   17828: istore 18
        //   17830: iload 7
        //   17832: istore 15
        //   17834: iload 5
        //   17836: istore 19
        //   17838: iload 6
        //   17840: istore 20
        //   17842: iload 50
        //   17844: istore 53
        //   17846: iload 49
        //   17848: istore 54
        //   17850: iload_3
        //   17851: istore 9
        //   17853: iload_1
        //   17854: istore 10
        //   17856: iload 4
        //   17858: istore 11
        //   17860: iload 7
        //   17862: istore 12
        //   17864: iload 5
        //   17866: istore 13
        //   17868: iload 6
        //   17870: istore 14
        //   17872: iload 50
        //   17874: istore 51
        //   17876: iload 49
        //   17878: istore 52
        //   17880: aload 80
        //   17882: aload 79
        //   17884: iconst_1
        //   17885: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   17888: putfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   17891: iload_3
        //   17892: istore 16
        //   17894: iload_1
        //   17895: istore 17
        //   17897: iload 4
        //   17899: istore 18
        //   17901: iload 7
        //   17903: istore 15
        //   17905: iload 5
        //   17907: istore 19
        //   17909: iload 6
        //   17911: istore 20
        //   17913: iload 50
        //   17915: istore 53
        //   17917: iload 49
        //   17919: istore 54
        //   17921: iload_3
        //   17922: istore 9
        //   17924: iload_1
        //   17925: istore 10
        //   17927: iload 4
        //   17929: istore 11
        //   17931: iload 7
        //   17933: istore 12
        //   17935: iload 5
        //   17937: istore 13
        //   17939: iload 6
        //   17941: istore 14
        //   17943: iload 50
        //   17945: istore 51
        //   17947: iload 49
        //   17949: istore 52
        //   17951: aload 80
        //   17953: aload 79
        //   17955: iconst_2
        //   17956: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   17959: putfield 176	org/vidogram/tgnet/TLRPC$Message:date	I
        //   17962: iload_3
        //   17963: istore 16
        //   17965: iload_1
        //   17966: istore 17
        //   17968: iload 4
        //   17970: istore 18
        //   17972: iload 7
        //   17974: istore 15
        //   17976: iload 5
        //   17978: istore 19
        //   17980: iload 6
        //   17982: istore 20
        //   17984: iload 50
        //   17986: istore 53
        //   17988: iload 49
        //   17990: istore 54
        //   17992: iload_3
        //   17993: istore 9
        //   17995: iload_1
        //   17996: istore 10
        //   17998: iload 4
        //   18000: istore 11
        //   18002: iload 7
        //   18004: istore 12
        //   18006: iload 5
        //   18008: istore 13
        //   18010: iload 6
        //   18012: istore 14
        //   18014: iload 50
        //   18016: istore 51
        //   18018: iload 49
        //   18020: istore 52
        //   18022: aload 80
        //   18024: aload_0
        //   18025: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   18028: putfield 179	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   18031: iload_3
        //   18032: istore 16
        //   18034: iload_1
        //   18035: istore 17
        //   18037: iload 4
        //   18039: istore 18
        //   18041: iload 7
        //   18043: istore 15
        //   18045: iload 5
        //   18047: istore 19
        //   18049: iload 6
        //   18051: istore 20
        //   18053: iload 50
        //   18055: istore 53
        //   18057: iload 49
        //   18059: istore 54
        //   18061: iload_3
        //   18062: istore 9
        //   18064: iload_1
        //   18065: istore 10
        //   18067: iload 4
        //   18069: istore 11
        //   18071: iload 7
        //   18073: istore 12
        //   18075: iload 5
        //   18077: istore 13
        //   18079: iload 6
        //   18081: istore 14
        //   18083: iload 50
        //   18085: istore 51
        //   18087: iload 49
        //   18089: istore 52
        //   18091: aload 80
        //   18093: aload 83
        //   18095: aload 84
        //   18097: invokestatic 200	org/vidogram/messenger/MessagesStorage:addUsersAndChatsFromMessage	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/ArrayList;Ljava/util/ArrayList;)V
        //   18100: iload_3
        //   18101: istore 16
        //   18103: iload_1
        //   18104: istore 17
        //   18106: iload 4
        //   18108: istore 18
        //   18110: iload 7
        //   18112: istore 15
        //   18114: iload 5
        //   18116: istore 19
        //   18118: iload 6
        //   18120: istore 20
        //   18122: iload 50
        //   18124: istore 53
        //   18126: iload 49
        //   18128: istore 54
        //   18130: iload_3
        //   18131: istore 9
        //   18133: iload_1
        //   18134: istore 10
        //   18136: iload 4
        //   18138: istore 11
        //   18140: iload 7
        //   18142: istore 12
        //   18144: iload 5
        //   18146: istore 13
        //   18148: iload 6
        //   18150: istore 14
        //   18152: iload 50
        //   18154: istore 51
        //   18156: iload 49
        //   18158: istore 52
        //   18160: aload 85
        //   18162: invokevirtual 385	java/util/HashMap:isEmpty	()Z
        //   18165: ifne +341 -> 18506
        //   18168: iload_3
        //   18169: istore 16
        //   18171: iload_1
        //   18172: istore 17
        //   18174: iload 4
        //   18176: istore 18
        //   18178: iload 7
        //   18180: istore 15
        //   18182: iload 5
        //   18184: istore 19
        //   18186: iload 6
        //   18188: istore 20
        //   18190: iload 50
        //   18192: istore 53
        //   18194: iload 49
        //   18196: istore 54
        //   18198: iload_3
        //   18199: istore 9
        //   18201: iload_1
        //   18202: istore 10
        //   18204: iload 4
        //   18206: istore 11
        //   18208: iload 7
        //   18210: istore 12
        //   18212: iload 5
        //   18214: istore 13
        //   18216: iload 6
        //   18218: istore 14
        //   18220: iload 50
        //   18222: istore 51
        //   18224: iload 49
        //   18226: istore 52
        //   18228: aload 85
        //   18230: aload 80
        //   18232: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   18235: invokestatic 135	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   18238: invokevirtual 230	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
        //   18241: checkcast 63	java/util/ArrayList
        //   18244: astore 81
        //   18246: aload 81
        //   18248: ifnull -708 -> 17540
        //   18251: iconst_0
        //   18252: istore_2
        //   18253: iload_3
        //   18254: istore 16
        //   18256: iload_1
        //   18257: istore 17
        //   18259: iload 4
        //   18261: istore 18
        //   18263: iload 7
        //   18265: istore 15
        //   18267: iload 5
        //   18269: istore 19
        //   18271: iload 6
        //   18273: istore 20
        //   18275: iload 50
        //   18277: istore 53
        //   18279: iload 49
        //   18281: istore 54
        //   18283: iload_3
        //   18284: istore 9
        //   18286: iload_1
        //   18287: istore 10
        //   18289: iload 4
        //   18291: istore 11
        //   18293: iload 7
        //   18295: istore 12
        //   18297: iload 5
        //   18299: istore 13
        //   18301: iload 6
        //   18303: istore 14
        //   18305: iload 50
        //   18307: istore 51
        //   18309: iload 49
        //   18311: istore 52
        //   18313: iload_2
        //   18314: aload 81
        //   18316: invokevirtual 381	java/util/ArrayList:size	()I
        //   18319: if_icmpge -779 -> 17540
        //   18322: iload_3
        //   18323: istore 16
        //   18325: iload_1
        //   18326: istore 17
        //   18328: iload 4
        //   18330: istore 18
        //   18332: iload 7
        //   18334: istore 15
        //   18336: iload 5
        //   18338: istore 19
        //   18340: iload 6
        //   18342: istore 20
        //   18344: iload 50
        //   18346: istore 53
        //   18348: iload 49
        //   18350: istore 54
        //   18352: iload_3
        //   18353: istore 9
        //   18355: iload_1
        //   18356: istore 10
        //   18358: iload 4
        //   18360: istore 11
        //   18362: iload 7
        //   18364: istore 12
        //   18366: iload 5
        //   18368: istore 13
        //   18370: iload 6
        //   18372: istore 14
        //   18374: iload 50
        //   18376: istore 51
        //   18378: iload 49
        //   18380: istore 52
        //   18382: aload 81
        //   18384: iload_2
        //   18385: invokevirtual 384	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   18388: checkcast 157	org/vidogram/tgnet/TLRPC$Message
        //   18391: aload 80
        //   18393: putfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   18396: iload_2
        //   18397: iconst_1
        //   18398: iadd
        //   18399: istore_2
        //   18400: goto -147 -> 18253
        //   18403: iload_3
        //   18404: istore 16
        //   18406: iload_1
        //   18407: istore 17
        //   18409: iload 4
        //   18411: istore 18
        //   18413: iload 7
        //   18415: istore 15
        //   18417: iload 5
        //   18419: istore 19
        //   18421: iload 6
        //   18423: istore 20
        //   18425: iload 50
        //   18427: istore 53
        //   18429: iload 49
        //   18431: istore 54
        //   18433: iload_3
        //   18434: istore 9
        //   18436: iload_1
        //   18437: istore 10
        //   18439: iload 4
        //   18441: istore 11
        //   18443: iload 7
        //   18445: istore 12
        //   18447: iload 5
        //   18449: istore 13
        //   18451: iload 6
        //   18453: istore 14
        //   18455: iload 50
        //   18457: istore 51
        //   18459: iload 49
        //   18461: istore 52
        //   18463: aload_0
        //   18464: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   18467: invokestatic 71	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   18470: getstatic 112	java/util/Locale:US	Ljava/util/Locale;
        //   18473: ldc_w 397
        //   18476: iconst_1
        //   18477: anewarray 4	java/lang/Object
        //   18480: dup
        //   18481: iconst_0
        //   18482: ldc_w 389
        //   18485: aload 87
        //   18487: invokestatic 395	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   18490: aastore
        //   18491: invokestatic 126	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   18494: iconst_0
        //   18495: anewarray 4	java/lang/Object
        //   18498: invokevirtual 93	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   18501: astore 79
        //   18503: goto -963 -> 17540
        //   18506: iload_3
        //   18507: istore 16
        //   18509: iload_1
        //   18510: istore 17
        //   18512: iload 4
        //   18514: istore 18
        //   18516: iload 7
        //   18518: istore 15
        //   18520: iload 5
        //   18522: istore 19
        //   18524: iload 6
        //   18526: istore 20
        //   18528: iload 50
        //   18530: istore 53
        //   18532: iload 49
        //   18534: istore 54
        //   18536: iload_3
        //   18537: istore 9
        //   18539: iload_1
        //   18540: istore 10
        //   18542: iload 4
        //   18544: istore 11
        //   18546: iload 7
        //   18548: istore 12
        //   18550: iload 5
        //   18552: istore 13
        //   18554: iload 6
        //   18556: istore 14
        //   18558: iload 50
        //   18560: istore 51
        //   18562: iload 49
        //   18564: istore 52
        //   18566: aload 86
        //   18568: aload 79
        //   18570: iconst_3
        //   18571: invokevirtual 241	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   18574: invokestatic 120	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   18577: invokevirtual 400	java/util/HashMap:remove	(Ljava/lang/Object;)Ljava/lang/Object;
        //   18580: checkcast 63	java/util/ArrayList
        //   18583: astore 81
        //   18585: aload 81
        //   18587: ifnull -1047 -> 17540
        //   18590: iconst_0
        //   18591: istore_2
        //   18592: iload_3
        //   18593: istore 16
        //   18595: iload_1
        //   18596: istore 17
        //   18598: iload 4
        //   18600: istore 18
        //   18602: iload 7
        //   18604: istore 15
        //   18606: iload 5
        //   18608: istore 19
        //   18610: iload 6
        //   18612: istore 20
        //   18614: iload 50
        //   18616: istore 53
        //   18618: iload 49
        //   18620: istore 54
        //   18622: iload_3
        //   18623: istore 9
        //   18625: iload_1
        //   18626: istore 10
        //   18628: iload 4
        //   18630: istore 11
        //   18632: iload 7
        //   18634: istore 12
        //   18636: iload 5
        //   18638: istore 13
        //   18640: iload 6
        //   18642: istore 14
        //   18644: iload 50
        //   18646: istore 51
        //   18648: iload 49
        //   18650: istore 52
        //   18652: iload_2
        //   18653: aload 81
        //   18655: invokevirtual 381	java/util/ArrayList:size	()I
        //   18658: if_icmpge -1118 -> 17540
        //   18661: iload_3
        //   18662: istore 16
        //   18664: iload_1
        //   18665: istore 17
        //   18667: iload 4
        //   18669: istore 18
        //   18671: iload 7
        //   18673: istore 15
        //   18675: iload 5
        //   18677: istore 19
        //   18679: iload 6
        //   18681: istore 20
        //   18683: iload 50
        //   18685: istore 53
        //   18687: iload 49
        //   18689: istore 54
        //   18691: iload_3
        //   18692: istore 9
        //   18694: iload_1
        //   18695: istore 10
        //   18697: iload 4
        //   18699: istore 11
        //   18701: iload 7
        //   18703: istore 12
        //   18705: iload 5
        //   18707: istore 13
        //   18709: iload 6
        //   18711: istore 14
        //   18713: iload 50
        //   18715: istore 51
        //   18717: iload 49
        //   18719: istore 52
        //   18721: aload 81
        //   18723: iload_2
        //   18724: invokevirtual 384	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   18727: checkcast 157	org/vidogram/tgnet/TLRPC$Message
        //   18730: astore 87
        //   18732: iload_3
        //   18733: istore 16
        //   18735: iload_1
        //   18736: istore 17
        //   18738: iload 4
        //   18740: istore 18
        //   18742: iload 7
        //   18744: istore 15
        //   18746: iload 5
        //   18748: istore 19
        //   18750: iload 6
        //   18752: istore 20
        //   18754: iload 50
        //   18756: istore 53
        //   18758: iload 49
        //   18760: istore 54
        //   18762: iload_3
        //   18763: istore 9
        //   18765: iload_1
        //   18766: istore 10
        //   18768: iload 4
        //   18770: istore 11
        //   18772: iload 7
        //   18774: istore 12
        //   18776: iload 5
        //   18778: istore 13
        //   18780: iload 6
        //   18782: istore 14
        //   18784: iload 50
        //   18786: istore 51
        //   18788: iload 49
        //   18790: istore 52
        //   18792: aload 87
        //   18794: aload 80
        //   18796: putfield 214	org/vidogram/tgnet/TLRPC$Message:replyMessage	Lorg/vidogram/tgnet/TLRPC$Message;
        //   18799: iload_3
        //   18800: istore 16
        //   18802: iload_1
        //   18803: istore 17
        //   18805: iload 4
        //   18807: istore 18
        //   18809: iload 7
        //   18811: istore 15
        //   18813: iload 5
        //   18815: istore 19
        //   18817: iload 6
        //   18819: istore 20
        //   18821: iload 50
        //   18823: istore 53
        //   18825: iload 49
        //   18827: istore 54
        //   18829: iload_3
        //   18830: istore 9
        //   18832: iload_1
        //   18833: istore 10
        //   18835: iload 4
        //   18837: istore 11
        //   18839: iload 7
        //   18841: istore 12
        //   18843: iload 5
        //   18845: istore 13
        //   18847: iload 6
        //   18849: istore 14
        //   18851: iload 50
        //   18853: istore 51
        //   18855: iload 49
        //   18857: istore 52
        //   18859: aload 87
        //   18861: aload 80
        //   18863: getfield 173	org/vidogram/tgnet/TLRPC$Message:id	I
        //   18866: putfield 203	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
        //   18869: iload_2
        //   18870: iconst_1
        //   18871: iadd
        //   18872: istore_2
        //   18873: goto -281 -> 18592
        //   18876: iload_3
        //   18877: istore 16
        //   18879: iload_1
        //   18880: istore 17
        //   18882: iload 4
        //   18884: istore 18
        //   18886: iload 7
        //   18888: istore 15
        //   18890: iload 5
        //   18892: istore 19
        //   18894: iload 6
        //   18896: istore 20
        //   18898: iload 50
        //   18900: istore 53
        //   18902: iload 49
        //   18904: istore 54
        //   18906: iload_3
        //   18907: istore 9
        //   18909: iload_1
        //   18910: istore 10
        //   18912: iload 4
        //   18914: istore 11
        //   18916: iload 7
        //   18918: istore 12
        //   18920: iload 5
        //   18922: istore 13
        //   18924: iload 6
        //   18926: istore 14
        //   18928: iload 50
        //   18930: istore 51
        //   18932: iload 49
        //   18934: istore 52
        //   18936: aload 79
        //   18938: invokevirtual 106	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   18941: iload_3
        //   18942: istore 16
        //   18944: iload_1
        //   18945: istore 17
        //   18947: iload 4
        //   18949: istore 18
        //   18951: iload 7
        //   18953: istore 15
        //   18955: iload 5
        //   18957: istore 19
        //   18959: iload 6
        //   18961: istore 20
        //   18963: iload 50
        //   18965: istore 53
        //   18967: iload 49
        //   18969: istore 54
        //   18971: iload_3
        //   18972: istore 9
        //   18974: iload_1
        //   18975: istore 10
        //   18977: iload 4
        //   18979: istore 11
        //   18981: iload 7
        //   18983: istore 12
        //   18985: iload 5
        //   18987: istore 13
        //   18989: iload 6
        //   18991: istore 14
        //   18993: iload 50
        //   18995: istore 51
        //   18997: iload 49
        //   18999: istore 52
        //   19001: aload 86
        //   19003: invokevirtual 385	java/util/HashMap:isEmpty	()Z
        //   19006: ifne +376 -> 19382
        //   19009: iload_3
        //   19010: istore 16
        //   19012: iload_1
        //   19013: istore 17
        //   19015: iload 4
        //   19017: istore 18
        //   19019: iload 7
        //   19021: istore 15
        //   19023: iload 5
        //   19025: istore 19
        //   19027: iload 6
        //   19029: istore 20
        //   19031: iload 50
        //   19033: istore 53
        //   19035: iload 49
        //   19037: istore 54
        //   19039: iload_3
        //   19040: istore 9
        //   19042: iload_1
        //   19043: istore 10
        //   19045: iload 4
        //   19047: istore 11
        //   19049: iload 7
        //   19051: istore 12
        //   19053: iload 5
        //   19055: istore 13
        //   19057: iload 6
        //   19059: istore 14
        //   19061: iload 50
        //   19063: istore 51
        //   19065: iload 49
        //   19067: istore 52
        //   19069: aload 86
        //   19071: invokevirtual 404	java/util/HashMap:entrySet	()Ljava/util/Set;
        //   19074: invokeinterface 410 1 0
        //   19079: astore 79
        //   19081: iload_3
        //   19082: istore 16
        //   19084: iload_1
        //   19085: istore 17
        //   19087: iload 4
        //   19089: istore 18
        //   19091: iload 7
        //   19093: istore 15
        //   19095: iload 5
        //   19097: istore 19
        //   19099: iload 6
        //   19101: istore 20
        //   19103: iload 50
        //   19105: istore 53
        //   19107: iload 49
        //   19109: istore 54
        //   19111: iload_3
        //   19112: istore 9
        //   19114: iload_1
        //   19115: istore 10
        //   19117: iload 4
        //   19119: istore 11
        //   19121: iload 7
        //   19123: istore 12
        //   19125: iload 5
        //   19127: istore 13
        //   19129: iload 6
        //   19131: istore 14
        //   19133: iload 50
        //   19135: istore 51
        //   19137: iload 49
        //   19139: istore 52
        //   19141: aload 79
        //   19143: invokeinterface 415 1 0
        //   19148: ifeq +234 -> 19382
        //   19151: iload_3
        //   19152: istore 16
        //   19154: iload_1
        //   19155: istore 17
        //   19157: iload 4
        //   19159: istore 18
        //   19161: iload 7
        //   19163: istore 15
        //   19165: iload 5
        //   19167: istore 19
        //   19169: iload 6
        //   19171: istore 20
        //   19173: iload 50
        //   19175: istore 53
        //   19177: iload 49
        //   19179: istore 54
        //   19181: iload_3
        //   19182: istore 9
        //   19184: iload_1
        //   19185: istore 10
        //   19187: iload 4
        //   19189: istore 11
        //   19191: iload 7
        //   19193: istore 12
        //   19195: iload 5
        //   19197: istore 13
        //   19199: iload 6
        //   19201: istore 14
        //   19203: iload 50
        //   19205: istore 51
        //   19207: iload 49
        //   19209: istore 52
        //   19211: aload 79
        //   19213: invokeinterface 418 1 0
        //   19218: checkcast 420	java/util/Map$Entry
        //   19221: invokeinterface 423 1 0
        //   19226: checkcast 63	java/util/ArrayList
        //   19229: astore 80
        //   19231: iconst_0
        //   19232: istore_2
        //   19233: iload_3
        //   19234: istore 16
        //   19236: iload_1
        //   19237: istore 17
        //   19239: iload 4
        //   19241: istore 18
        //   19243: iload 7
        //   19245: istore 15
        //   19247: iload 5
        //   19249: istore 19
        //   19251: iload 6
        //   19253: istore 20
        //   19255: iload 50
        //   19257: istore 53
        //   19259: iload 49
        //   19261: istore 54
        //   19263: iload_3
        //   19264: istore 9
        //   19266: iload_1
        //   19267: istore 10
        //   19269: iload 4
        //   19271: istore 11
        //   19273: iload 7
        //   19275: istore 12
        //   19277: iload 5
        //   19279: istore 13
        //   19281: iload 6
        //   19283: istore 14
        //   19285: iload 50
        //   19287: istore 51
        //   19289: iload 49
        //   19291: istore 52
        //   19293: iload_2
        //   19294: aload 80
        //   19296: invokevirtual 381	java/util/ArrayList:size	()I
        //   19299: if_icmpge -218 -> 19081
        //   19302: iload_3
        //   19303: istore 16
        //   19305: iload_1
        //   19306: istore 17
        //   19308: iload 4
        //   19310: istore 18
        //   19312: iload 7
        //   19314: istore 15
        //   19316: iload 5
        //   19318: istore 19
        //   19320: iload 6
        //   19322: istore 20
        //   19324: iload 50
        //   19326: istore 53
        //   19328: iload 49
        //   19330: istore 54
        //   19332: iload_3
        //   19333: istore 9
        //   19335: iload_1
        //   19336: istore 10
        //   19338: iload 4
        //   19340: istore 11
        //   19342: iload 7
        //   19344: istore 12
        //   19346: iload 5
        //   19348: istore 13
        //   19350: iload 6
        //   19352: istore 14
        //   19354: iload 50
        //   19356: istore 51
        //   19358: iload 49
        //   19360: istore 52
        //   19362: aload 80
        //   19364: iload_2
        //   19365: invokevirtual 384	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   19368: checkcast 157	org/vidogram/tgnet/TLRPC$Message
        //   19371: lconst_0
        //   19372: putfield 206	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
        //   19375: iload_2
        //   19376: iconst_1
        //   19377: iadd
        //   19378: istore_2
        //   19379: goto -146 -> 19233
        //   19382: iload_3
        //   19383: istore 16
        //   19385: iload_1
        //   19386: istore 17
        //   19388: iload 4
        //   19390: istore 18
        //   19392: iload 7
        //   19394: istore 15
        //   19396: iload 5
        //   19398: istore 19
        //   19400: iload 6
        //   19402: istore 20
        //   19404: iload 50
        //   19406: istore 53
        //   19408: iload 49
        //   19410: istore 54
        //   19412: iload_3
        //   19413: istore 9
        //   19415: iload_1
        //   19416: istore 10
        //   19418: iload 4
        //   19420: istore 11
        //   19422: iload 7
        //   19424: istore 12
        //   19426: iload 5
        //   19428: istore 13
        //   19430: iload 6
        //   19432: istore 14
        //   19434: iload 50
        //   19436: istore 51
        //   19438: iload 49
        //   19440: istore 52
        //   19442: aload 83
        //   19444: invokevirtual 378	java/util/ArrayList:isEmpty	()Z
        //   19447: ifne +83 -> 19530
        //   19450: iload_3
        //   19451: istore 16
        //   19453: iload_1
        //   19454: istore 17
        //   19456: iload 4
        //   19458: istore 18
        //   19460: iload 7
        //   19462: istore 15
        //   19464: iload 5
        //   19466: istore 19
        //   19468: iload 6
        //   19470: istore 20
        //   19472: iload 50
        //   19474: istore 53
        //   19476: iload 49
        //   19478: istore 54
        //   19480: iload_3
        //   19481: istore 9
        //   19483: iload_1
        //   19484: istore 10
        //   19486: iload 4
        //   19488: istore 11
        //   19490: iload 7
        //   19492: istore 12
        //   19494: iload 5
        //   19496: istore 13
        //   19498: iload 6
        //   19500: istore 14
        //   19502: iload 50
        //   19504: istore 51
        //   19506: iload 49
        //   19508: istore 52
        //   19510: aload_0
        //   19511: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   19514: ldc_w 389
        //   19517: aload 83
        //   19519: invokestatic 395	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   19522: aload 82
        //   19524: getfield 274	org/vidogram/tgnet/TLRPC$TL_messages_messages:users	Ljava/util/ArrayList;
        //   19527: invokevirtual 427	org/vidogram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   19530: iload_3
        //   19531: istore 16
        //   19533: iload_1
        //   19534: istore 17
        //   19536: iload 4
        //   19538: istore 18
        //   19540: iload 7
        //   19542: istore 15
        //   19544: iload 5
        //   19546: istore 19
        //   19548: iload 6
        //   19550: istore 20
        //   19552: iload 50
        //   19554: istore 53
        //   19556: iload 49
        //   19558: istore 54
        //   19560: iload_3
        //   19561: istore 9
        //   19563: iload_1
        //   19564: istore 10
        //   19566: iload 4
        //   19568: istore 11
        //   19570: iload 7
        //   19572: istore 12
        //   19574: iload 5
        //   19576: istore 13
        //   19578: iload 6
        //   19580: istore 14
        //   19582: iload 50
        //   19584: istore 51
        //   19586: iload 49
        //   19588: istore 52
        //   19590: aload 84
        //   19592: invokevirtual 378	java/util/ArrayList:isEmpty	()Z
        //   19595: ifne +83 -> 19678
        //   19598: iload_3
        //   19599: istore 16
        //   19601: iload_1
        //   19602: istore 17
        //   19604: iload 4
        //   19606: istore 18
        //   19608: iload 7
        //   19610: istore 15
        //   19612: iload 5
        //   19614: istore 19
        //   19616: iload 6
        //   19618: istore 20
        //   19620: iload 50
        //   19622: istore 53
        //   19624: iload 49
        //   19626: istore 54
        //   19628: iload_3
        //   19629: istore 9
        //   19631: iload_1
        //   19632: istore 10
        //   19634: iload 4
        //   19636: istore 11
        //   19638: iload 7
        //   19640: istore 12
        //   19642: iload 5
        //   19644: istore 13
        //   19646: iload 6
        //   19648: istore 14
        //   19650: iload 50
        //   19652: istore 51
        //   19654: iload 49
        //   19656: istore 52
        //   19658: aload_0
        //   19659: getfield 31	org/vidogram/messenger/MessagesStorage$49:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   19662: ldc_w 389
        //   19665: aload 84
        //   19667: invokestatic 395	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   19670: aload 82
        //   19672: getfield 271	org/vidogram/tgnet/TLRPC$TL_messages_messages:chats	Ljava/util/ArrayList;
        //   19675: invokevirtual 430	org/vidogram/messenger/MessagesStorage:getChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   19678: invokestatic 280	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   19681: aload 82
        //   19683: aload_0
        //   19684: getfield 39	org/vidogram/messenger/MessagesStorage$49:val$dialog_id	J
        //   19687: iload_3
        //   19688: iload_1
        //   19689: aload_0
        //   19690: getfield 45	org/vidogram/messenger/MessagesStorage$49:val$offset_date	I
        //   19693: iconst_1
        //   19694: aload_0
        //   19695: getfield 47	org/vidogram/messenger/MessagesStorage$49:val$classGuid	I
        //   19698: iload 4
        //   19700: iload 7
        //   19702: iload 5
        //   19704: iload 6
        //   19706: aload_0
        //   19707: getfield 41	org/vidogram/messenger/MessagesStorage$49:val$load_type	I
        //   19710: aload_0
        //   19711: getfield 37	org/vidogram/messenger/MessagesStorage$49:val$isChannel	Z
        //   19714: iload 50
        //   19716: aload_0
        //   19717: getfield 49	org/vidogram/messenger/MessagesStorage$49:val$loadIndex	I
        //   19720: iload 49
        //   19722: invokevirtual 284	org/vidogram/messenger/MessagesController:processLoadedMessages	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIZIIIIIIZZIZ)V
        //   19725: return
        //   19726: astore 79
        //   19728: iload_1
        //   19729: istore_3
        //   19730: iload 4
        //   19732: istore_1
        //   19733: iload 14
        //   19735: istore 12
        //   19737: iload 52
        //   19739: istore 51
        //   19741: iload 50
        //   19743: istore 49
        //   19745: goto -3630 -> 16115
        //   19748: astore 79
        //   19750: iload_1
        //   19751: istore_3
        //   19752: iload 5
        //   19754: istore_2
        //   19755: iload 4
        //   19757: istore_1
        //   19758: iload 26
        //   19760: istore 12
        //   19762: iload 10
        //   19764: istore 5
        //   19766: iload 8
        //   19768: istore 7
        //   19770: iload 64
        //   19772: istore 51
        //   19774: goto -3659 -> 16115
        //   19777: astore 79
        //   19779: iload_1
        //   19780: istore_3
        //   19781: iload 18
        //   19783: istore_1
        //   19784: iload 15
        //   19786: istore_2
        //   19787: iload 8
        //   19789: istore 12
        //   19791: iload 14
        //   19793: istore 5
        //   19795: iload 11
        //   19797: istore 7
        //   19799: iload 53
        //   19801: istore 49
        //   19803: goto -3688 -> 16115
        //   19806: astore 79
        //   19808: iload_1
        //   19809: istore_3
        //   19810: iload 16
        //   19812: istore_2
        //   19813: iload 4
        //   19815: istore_1
        //   19816: iload 26
        //   19818: istore 12
        //   19820: iload 13
        //   19822: istore 7
        //   19824: iload 63
        //   19826: istore 51
        //   19828: iload 61
        //   19830: istore 49
        //   19832: goto -3717 -> 16115
        //   19835: astore 79
        //   19837: iload_1
        //   19838: istore_2
        //   19839: iload 4
        //   19841: istore_1
        //   19842: iload 53
        //   19844: istore 50
        //   19846: iload 51
        //   19848: istore 49
        //   19850: goto -12971 -> 6879
        //   19853: astore 79
        //   19855: iload_1
        //   19856: istore_2
        //   19857: iload 6
        //   19859: istore_3
        //   19860: iload 4
        //   19862: istore_1
        //   19863: iload 25
        //   19865: istore 15
        //   19867: iload 10
        //   19869: istore 6
        //   19871: iload 63
        //   19873: istore 50
        //   19875: goto -12996 -> 6879
        //   19878: astore 79
        //   19880: iload_1
        //   19881: istore_2
        //   19882: iload 16
        //   19884: istore_3
        //   19885: iload 4
        //   19887: istore_1
        //   19888: iload 25
        //   19890: istore 15
        //   19892: iload 13
        //   19894: istore 8
        //   19896: iload 57
        //   19898: istore 50
        //   19900: iload 60
        //   19902: istore 49
        //   19904: goto -13025 -> 6879
        //   19907: iconst_0
        //   19908: istore_2
        //   19909: iload 16
        //   19911: istore 4
        //   19913: iload 17
        //   19915: istore 7
        //   19917: iload 18
        //   19919: istore 5
        //   19921: goto -4426 -> 15495
        //   19924: iload 9
        //   19926: istore_2
        //   19927: iload 9
        //   19929: istore_1
        //   19930: goto -17365 -> 2565
        //   19933: lload 71
        //   19935: lstore 73
        //   19937: goto -17372 -> 2565
        //   19940: iload 13
        //   19942: istore 9
        //   19944: goto -9811 -> 10133
        //   19947: iload 19
        //   19949: istore_3
        //   19950: iload 18
        //   19952: istore_2
        //   19953: iload 24
        //   19955: istore 9
        //   19957: iload 17
        //   19959: istore 4
        //   19961: iload 16
        //   19963: istore 5
        //   19965: iload 12
        //   19967: istore 6
        //   19969: goto -18888 -> 1081
        //   19972: iload_2
        //   19973: istore_3
        //   19974: iload_2
        //   19975: istore 9
        //   19977: goto -11367 -> 8610
        //   19980: iload 9
        //   19982: istore 11
        //   19984: iload 10
        //   19986: istore 13
        //   19988: iload 16
        //   19990: istore 10
        //   19992: goto +98 -> 20090
        //   19995: iload_2
        //   19996: istore 12
        //   19998: iload 8
        //   20000: istore 16
        //   20002: goto -12045 -> 7957
        //   20005: iload 9
        //   20007: istore_2
        //   20008: iload 9
        //   20010: istore_3
        //   20011: iload_2
        //   20012: istore 9
        //   20014: iload_3
        //   20015: istore_2
        //   20016: goto -12198 -> 7818
        //   20019: lload 73
        //   20021: lstore 71
        //   20023: iconst_0
        //   20024: istore_2
        //   20025: iload 35
        //   20027: istore 10
        //   20029: iload 34
        //   20031: istore 8
        //   20033: iload 67
        //   20035: istore 49
        //   20037: goto -12219 -> 7818
        //   20040: iconst_0
        //   20041: istore 21
        //   20043: goto -19860 -> 183
        //   20046: lload 71
        //   20048: lstore 77
        //   20050: lload 71
        //   20052: lconst_0
        //   20053: lcmp
        //   20054: ifne -16539 -> 3515
        //   20057: ldc2_w 431
        //   20060: lstore 77
        //   20062: iload 21
        //   20064: ifeq -16549 -> 3515
        //   20067: ldc2_w 431
        //   20070: iload 21
        //   20072: i2l
        //   20073: bipush 32
        //   20075: lshl
        //   20076: lor
        //   20077: lstore 77
        //   20079: goto -16564 -> 3515
        //   20082: aload 79
        //   20084: ifnull -3855 -> 16229
        //   20087: goto -16408 -> 3679
        //   20090: iload 4
        //   20092: iload 13
        //   20094: if_icmpgt -11814 -> 8280
        //   20097: iload 13
        //   20099: iload 22
        //   20101: if_icmpge -11199 -> 8902
        //   20104: goto -11824 -> 8280
        //   20107: lload 73
        //   20109: lstore 71
        //   20111: iload 5
        //   20113: istore 11
        //   20115: iload_3
        //   20116: istore 12
        //   20118: iload 10
        //   20120: istore 13
        //   20122: iload 8
        //   20124: istore 10
        //   20126: goto -36 -> 20090
        //   20129: astore 79
        //   20131: iload_1
        //   20132: istore_2
        //   20133: iload 19
        //   20135: istore_1
        //   20136: iload 17
        //   20138: istore_3
        //   20139: iload 10
        //   20141: istore 15
        //   20143: iload 16
        //   20145: istore 6
        //   20147: iload 12
        //   20149: istore 8
        //   20151: iload 52
        //   20153: istore 50
        //   20155: iload 54
        //   20157: istore 49
        //   20159: goto -13280 -> 6879
        //   20162: iconst_m1
        //   20163: istore 9
        //   20165: goto -17970 -> 2195
        //   20168: iconst_m1
        //   20169: istore 13
        //   20171: goto -17691 -> 2480
        //   20174: goto -92 -> 20082
        //   20177: goto -95 -> 20082
        //   20180: goto -98 -> 20082
        //   20183: iload 4
        //   20185: iload 18
        //   20187: if_icmpgt -4771 -> 15416
        //   20190: iload 18
        //   20192: iload 22
        //   20194: if_icmpge -4582 -> 15612
        //   20197: goto -4781 -> 15416
        //
        // Exception table:
        //   from	to	target	type
        //   6625	6665	6782	java/lang/Exception
        //   6695	6703	6782	java/lang/Exception
        //   6733	6744	6782	java/lang/Exception
        //   6774	6779	6782	java/lang/Exception
        //   2641	2693	6852	java/lang/Exception
        //   2753	2761	6852	java/lang/Exception
        //   2824	2829	6852	java/lang/Exception
        //   2904	2949	6852	java/lang/Exception
        //   3009	3017	6852	java/lang/Exception
        //   3077	3086	6852	java/lang/Exception
        //   3166	3171	6852	java/lang/Exception
        //   3231	3276	6852	java/lang/Exception
        //   3336	3344	6852	java/lang/Exception
        //   3404	3413	6852	java/lang/Exception
        //   3493	3498	6852	java/lang/Exception
        //   3575	3676	6852	java/lang/Exception
        //   3739	3747	6852	java/lang/Exception
        //   3807	3815	6852	java/lang/Exception
        //   3880	3894	6852	java/lang/Exception
        //   3954	3959	6852	java/lang/Exception
        //   4019	4030	6852	java/lang/Exception
        //   4090	4101	6852	java/lang/Exception
        //   4161	4172	6852	java/lang/Exception
        //   4232	4241	6852	java/lang/Exception
        //   4301	4313	6852	java/lang/Exception
        //   4373	4385	6852	java/lang/Exception
        //   4450	4462	6852	java/lang/Exception
        //   4522	4533	6852	java/lang/Exception
        //   4593	4602	6852	java/lang/Exception
        //   4662	4670	6852	java/lang/Exception
        //   4730	4740	6852	java/lang/Exception
        //   4800	4810	6852	java/lang/Exception
        //   4870	4879	6852	java/lang/Exception
        //   4944	4961	6852	java/lang/Exception
        //   5021	5026	6852	java/lang/Exception
        //   5086	5094	6852	java/lang/Exception
        //   5154	5166	6852	java/lang/Exception
        //   5226	5234	6852	java/lang/Exception
        //   5294	5302	6852	java/lang/Exception
        //   5362	5370	6852	java/lang/Exception
        //   5434	5445	6852	java/lang/Exception
        //   5505	5522	6852	java/lang/Exception
        //   5582	5595	6852	java/lang/Exception
        //   5655	5666	6852	java/lang/Exception
        //   5726	5744	6852	java/lang/Exception
        //   5813	5822	6852	java/lang/Exception
        //   5882	5898	6852	java/lang/Exception
        //   5958	5966	6852	java/lang/Exception
        //   6026	6037	6852	java/lang/Exception
        //   6097	6105	6852	java/lang/Exception
        //   6165	6173	6852	java/lang/Exception
        //   6233	6239	6852	java/lang/Exception
        //   6304	6313	6852	java/lang/Exception
        //   6373	6384	6852	java/lang/Exception
        //   6444	6452	6852	java/lang/Exception
        //   6512	6520	6852	java/lang/Exception
        //   6580	6590	6852	java/lang/Exception
        //   6844	6849	6852	java/lang/Exception
        //   10301	10385	6852	java/lang/Exception
        //   15699	15715	6852	java/lang/Exception
        //   15775	15789	6852	java/lang/Exception
        //   15849	15867	6852	java/lang/Exception
        //   15936	15945	6852	java/lang/Exception
        //   16005	16021	6852	java/lang/Exception
        //   16081	16089	6852	java/lang/Exception
        //   16224	16229	6852	java/lang/Exception
        //   16289	16305	6852	java/lang/Exception
        //   16370	16378	6852	java/lang/Exception
        //   16438	16446	6852	java/lang/Exception
        //   16506	16514	6852	java/lang/Exception
        //   16579	16590	6852	java/lang/Exception
        //   16650	16676	6852	java/lang/Exception
        //   16736	16753	6852	java/lang/Exception
        //   16825	16830	6852	java/lang/Exception
        //   16890	16895	6852	java/lang/Exception
        //   16955	16960	6852	java/lang/Exception
        //   17020	17028	6852	java/lang/Exception
        //   17088	17096	6852	java/lang/Exception
        //   17156	17164	6852	java/lang/Exception
        //   17224	17236	6852	java/lang/Exception
        //   17296	17304	6852	java/lang/Exception
        //   17364	17372	6852	java/lang/Exception
        //   17432	17440	6852	java/lang/Exception
        //   17500	17540	6852	java/lang/Exception
        //   17600	17608	6852	java/lang/Exception
        //   17668	17676	6852	java/lang/Exception
        //   17741	17755	6852	java/lang/Exception
        //   17815	17820	6852	java/lang/Exception
        //   17880	17891	6852	java/lang/Exception
        //   17951	17962	6852	java/lang/Exception
        //   18022	18031	6852	java/lang/Exception
        //   18091	18100	6852	java/lang/Exception
        //   18160	18168	6852	java/lang/Exception
        //   18228	18246	6852	java/lang/Exception
        //   18313	18322	6852	java/lang/Exception
        //   18382	18396	6852	java/lang/Exception
        //   18463	18503	6852	java/lang/Exception
        //   18566	18585	6852	java/lang/Exception
        //   18652	18661	6852	java/lang/Exception
        //   18721	18732	6852	java/lang/Exception
        //   18792	18799	6852	java/lang/Exception
        //   18859	18869	6852	java/lang/Exception
        //   18936	18941	6852	java/lang/Exception
        //   19001	19009	6852	java/lang/Exception
        //   19069	19081	6852	java/lang/Exception
        //   19141	19151	6852	java/lang/Exception
        //   19211	19231	6852	java/lang/Exception
        //   19293	19302	6852	java/lang/Exception
        //   19362	19375	6852	java/lang/Exception
        //   19442	19450	6852	java/lang/Exception
        //   19510	19530	6852	java/lang/Exception
        //   19590	19598	6852	java/lang/Exception
        //   19658	19678	6852	java/lang/Exception
        //   2641	2693	16092	finally
        //   2753	2761	16092	finally
        //   2824	2829	16092	finally
        //   2904	2949	16092	finally
        //   3009	3017	16092	finally
        //   3077	3086	16092	finally
        //   3166	3171	16092	finally
        //   3231	3276	16092	finally
        //   3336	3344	16092	finally
        //   3404	3413	16092	finally
        //   3493	3498	16092	finally
        //   3575	3676	16092	finally
        //   3739	3747	16092	finally
        //   3807	3815	16092	finally
        //   3880	3894	16092	finally
        //   3954	3959	16092	finally
        //   4019	4030	16092	finally
        //   4090	4101	16092	finally
        //   4161	4172	16092	finally
        //   4232	4241	16092	finally
        //   4301	4313	16092	finally
        //   4373	4385	16092	finally
        //   4450	4462	16092	finally
        //   4522	4533	16092	finally
        //   4593	4602	16092	finally
        //   4662	4670	16092	finally
        //   4730	4740	16092	finally
        //   4800	4810	16092	finally
        //   4870	4879	16092	finally
        //   4944	4961	16092	finally
        //   5021	5026	16092	finally
        //   5086	5094	16092	finally
        //   5154	5166	16092	finally
        //   5226	5234	16092	finally
        //   5294	5302	16092	finally
        //   5362	5370	16092	finally
        //   5434	5445	16092	finally
        //   5505	5522	16092	finally
        //   5582	5595	16092	finally
        //   5655	5666	16092	finally
        //   5726	5744	16092	finally
        //   5813	5822	16092	finally
        //   5882	5898	16092	finally
        //   5958	5966	16092	finally
        //   6026	6037	16092	finally
        //   6097	6105	16092	finally
        //   6165	6173	16092	finally
        //   6233	6239	16092	finally
        //   6304	6313	16092	finally
        //   6373	6384	16092	finally
        //   6444	6452	16092	finally
        //   6512	6520	16092	finally
        //   6580	6590	16092	finally
        //   6625	6665	16092	finally
        //   6695	6703	16092	finally
        //   6733	6744	16092	finally
        //   6774	6779	16092	finally
        //   6844	6849	16092	finally
        //   6908	6916	16092	finally
        //   6945	6953	16092	finally
        //   6982	6990	16092	finally
        //   7019	7024	16092	finally
        //   10301	10385	16092	finally
        //   15699	15715	16092	finally
        //   15775	15789	16092	finally
        //   15849	15867	16092	finally
        //   15936	15945	16092	finally
        //   16005	16021	16092	finally
        //   16081	16089	16092	finally
        //   16224	16229	16092	finally
        //   16289	16305	16092	finally
        //   16370	16378	16092	finally
        //   16438	16446	16092	finally
        //   16506	16514	16092	finally
        //   16579	16590	16092	finally
        //   16650	16676	16092	finally
        //   16736	16753	16092	finally
        //   16825	16830	16092	finally
        //   16890	16895	16092	finally
        //   16955	16960	16092	finally
        //   17020	17028	16092	finally
        //   17088	17096	16092	finally
        //   17156	17164	16092	finally
        //   17224	17236	16092	finally
        //   17296	17304	16092	finally
        //   17364	17372	16092	finally
        //   17432	17440	16092	finally
        //   17500	17540	16092	finally
        //   17600	17608	16092	finally
        //   17668	17676	16092	finally
        //   17741	17755	16092	finally
        //   17815	17820	16092	finally
        //   17880	17891	16092	finally
        //   17951	17962	16092	finally
        //   18022	18031	16092	finally
        //   18091	18100	16092	finally
        //   18160	18168	16092	finally
        //   18228	18246	16092	finally
        //   18313	18322	16092	finally
        //   18382	18396	16092	finally
        //   18463	18503	16092	finally
        //   18566	18585	16092	finally
        //   18652	18661	16092	finally
        //   18721	18732	16092	finally
        //   18792	18799	16092	finally
        //   18859	18869	16092	finally
        //   18936	18941	16092	finally
        //   19001	19009	16092	finally
        //   19069	19081	16092	finally
        //   19141	19151	16092	finally
        //   19211	19231	16092	finally
        //   19293	19302	16092	finally
        //   19362	19375	16092	finally
        //   19442	19450	16092	finally
        //   19510	19530	16092	finally
        //   19590	19598	16092	finally
        //   19658	19678	16092	finally
        //   296	305	19726	finally
        //   351	360	19726	finally
        //   406	415	19726	finally
        //   461	470	19726	finally
        //   516	525	19726	finally
        //   571	578	19726	finally
        //   629	637	19726	finally
        //   683	690	19726	finally
        //   736	774	19726	finally
        //   828	836	19726	finally
        //   882	892	19726	finally
        //   938	946	19726	finally
        //   992	1000	19726	finally
        //   1046	1051	19726	finally
        //   7151	7159	19726	finally
        //   7233	7241	19726	finally
        //   7315	7323	19726	finally
        //   7397	7404	19726	finally
        //   7466	7474	19726	finally
        //   7520	7558	19726	finally
        //   7604	7612	19726	finally
        //   7658	7666	19726	finally
        //   7717	7725	19726	finally
        //   7771	7779	19726	finally
        //   7949	7957	19726	finally
        //   8003	8008	19726	finally
        //   8059	8106	19726	finally
        //   8156	8164	19726	finally
        //   8210	8218	19726	finally
        //   8264	8269	19726	finally
        //   8326	8338	19726	finally
        //   12673	12681	19726	finally
        //   12739	12746	19726	finally
        //   12792	12831	19726	finally
        //   12881	12889	19726	finally
        //   12935	12943	19726	finally
        //   12989	12994	19726	finally
        //   13043	13082	19726	finally
        //   13132	13140	19726	finally
        //   13186	13194	19726	finally
        //   13240	13248	19726	finally
        //   13294	13299	19726	finally
        //   13470	13478	19726	finally
        //   13524	13532	19726	finally
        //   13578	13617	19726	finally
        //   13667	13675	19726	finally
        //   13721	13729	19726	finally
        //   13775	13780	19726	finally
        //   13826	13912	19726	finally
        //   13987	13995	19726	finally
        //   14041	14098	19726	finally
        //   14173	14180	19726	finally
        //   14226	14233	19726	finally
        //   14279	14336	19726	finally
        //   14411	14475	19726	finally
        //   14566	14574	19726	finally
        //   14620	14659	19726	finally
        //   14705	14713	19726	finally
        //   14759	14767	19726	finally
        //   14813	14818	19726	finally
        //   14864	14903	19726	finally
        //   14957	14965	19726	finally
        //   15011	15019	19726	finally
        //   15065	15073	19726	finally
        //   15119	15124	19726	finally
        //   15191	15238	19726	finally
        //   15288	15296	19726	finally
        //   15342	15350	19726	finally
        //   15396	15401	19726	finally
        //   15462	15474	19726	finally
        //   7824	7829	19748	finally
        //   7840	7879	19748	finally
        //   7885	7893	19748	finally
        //   7899	7907	19748	finally
        //   8409	8448	19748	finally
        //   8454	8462	19748	finally
        //   8468	8476	19748	finally
        //   8482	8487	19748	finally
        //   8510	8549	19748	finally
        //   8557	8565	19748	finally
        //   8571	8578	19748	finally
        //   8616	8621	19748	finally
        //   8638	8693	19748	finally
        //   8699	8707	19748	finally
        //   8716	8721	19748	finally
        //   8739	8786	19748	finally
        //   8792	8800	19748	finally
        //   8806	8813	19748	finally
        //   8863	8868	19748	finally
        //   1150	1188	19777	finally
        //   1242	1250	19777	finally
        //   1304	1314	19777	finally
        //   1371	1376	19777	finally
        //   1430	1438	19777	finally
        //   1492	1500	19777	finally
        //   1559	1567	19777	finally
        //   1621	1659	19777	finally
        //   1717	1725	19777	finally
        //   1779	1787	19777	finally
        //   1841	1846	19777	finally
        //   1900	1908	19777	finally
        //   1962	1969	19777	finally
        //   2023	2071	19777	finally
        //   2125	2133	19777	finally
        //   2187	2195	19777	finally
        //   2249	2254	19777	finally
        //   2308	2356	19777	finally
        //   2410	2418	19777	finally
        //   2472	2480	19777	finally
        //   2534	2539	19777	finally
        //   8993	8998	19777	finally
        //   9052	9091	19777	finally
        //   9145	9153	19777	finally
        //   9207	9215	19777	finally
        //   9274	9289	19777	finally
        //   9343	9348	19777	finally
        //   9402	9412	19777	finally
        //   9466	9473	19777	finally
        //   9527	9535	19777	finally
        //   9589	9595	19777	finally
        //   9649	9654	19777	finally
        //   9708	9713	19777	finally
        //   9774	9829	19777	finally
        //   9883	9891	19777	finally
        //   9948	9953	19777	finally
        //   10013	10068	19777	finally
        //   10122	10130	19777	finally
        //   10187	10192	19777	finally
        //   10448	10456	19777	finally
        //   10513	10562	19777	finally
        //   10616	10624	19777	finally
        //   10678	10687	19777	finally
        //   10761	10766	19777	finally
        //   10827	10899	19777	finally
        //   10956	11020	19777	finally
        //   11077	11084	19777	finally
        //   11148	11196	19777	finally
        //   11250	11258	19777	finally
        //   11312	11321	19777	finally
        //   11395	11400	19777	finally
        //   11461	11533	19777	finally
        //   11590	11654	19777	finally
        //   11711	11775	19777	finally
        //   11832	11870	19777	finally
        //   11928	11936	19777	finally
        //   11990	11998	19777	finally
        //   12052	12057	19777	finally
        //   12114	12153	19777	finally
        //   12207	12215	19777	finally
        //   12269	12278	19777	finally
        //   12352	12357	19777	finally
        //   12418	12480	19777	finally
        //   12537	12591	19777	finally
        //   15545	15598	19777	finally
        //   13320	13367	19806	finally
        //   13379	13387	19806	finally
        //   13395	13403	19806	finally
        //   13411	13416	19806	finally
        //   296	305	19835	java/lang/Exception
        //   351	360	19835	java/lang/Exception
        //   406	415	19835	java/lang/Exception
        //   461	470	19835	java/lang/Exception
        //   516	525	19835	java/lang/Exception
        //   571	578	19835	java/lang/Exception
        //   629	637	19835	java/lang/Exception
        //   683	690	19835	java/lang/Exception
        //   736	774	19835	java/lang/Exception
        //   828	836	19835	java/lang/Exception
        //   882	892	19835	java/lang/Exception
        //   938	946	19835	java/lang/Exception
        //   992	1000	19835	java/lang/Exception
        //   1046	1051	19835	java/lang/Exception
        //   7151	7159	19835	java/lang/Exception
        //   7233	7241	19835	java/lang/Exception
        //   7315	7323	19835	java/lang/Exception
        //   7397	7404	19835	java/lang/Exception
        //   7466	7474	19835	java/lang/Exception
        //   7520	7558	19835	java/lang/Exception
        //   7604	7612	19835	java/lang/Exception
        //   7658	7666	19835	java/lang/Exception
        //   7717	7725	19835	java/lang/Exception
        //   7771	7779	19835	java/lang/Exception
        //   7949	7957	19835	java/lang/Exception
        //   8003	8008	19835	java/lang/Exception
        //   8059	8106	19835	java/lang/Exception
        //   8156	8164	19835	java/lang/Exception
        //   8210	8218	19835	java/lang/Exception
        //   8264	8269	19835	java/lang/Exception
        //   8326	8338	19835	java/lang/Exception
        //   12673	12681	19835	java/lang/Exception
        //   12739	12746	19835	java/lang/Exception
        //   12792	12831	19835	java/lang/Exception
        //   12881	12889	19835	java/lang/Exception
        //   12935	12943	19835	java/lang/Exception
        //   12989	12994	19835	java/lang/Exception
        //   13043	13082	19835	java/lang/Exception
        //   13132	13140	19835	java/lang/Exception
        //   13186	13194	19835	java/lang/Exception
        //   13240	13248	19835	java/lang/Exception
        //   13294	13299	19835	java/lang/Exception
        //   13470	13478	19835	java/lang/Exception
        //   13524	13532	19835	java/lang/Exception
        //   13578	13617	19835	java/lang/Exception
        //   13667	13675	19835	java/lang/Exception
        //   13721	13729	19835	java/lang/Exception
        //   13775	13780	19835	java/lang/Exception
        //   13826	13912	19835	java/lang/Exception
        //   13987	13995	19835	java/lang/Exception
        //   14041	14098	19835	java/lang/Exception
        //   14173	14180	19835	java/lang/Exception
        //   14226	14233	19835	java/lang/Exception
        //   14279	14336	19835	java/lang/Exception
        //   14411	14475	19835	java/lang/Exception
        //   14566	14574	19835	java/lang/Exception
        //   14620	14659	19835	java/lang/Exception
        //   14705	14713	19835	java/lang/Exception
        //   14759	14767	19835	java/lang/Exception
        //   14813	14818	19835	java/lang/Exception
        //   14864	14903	19835	java/lang/Exception
        //   14957	14965	19835	java/lang/Exception
        //   15011	15019	19835	java/lang/Exception
        //   15065	15073	19835	java/lang/Exception
        //   15119	15124	19835	java/lang/Exception
        //   15191	15238	19835	java/lang/Exception
        //   15288	15296	19835	java/lang/Exception
        //   15342	15350	19835	java/lang/Exception
        //   15396	15401	19835	java/lang/Exception
        //   15462	15474	19835	java/lang/Exception
        //   7824	7829	19853	java/lang/Exception
        //   7840	7879	19853	java/lang/Exception
        //   7885	7893	19853	java/lang/Exception
        //   7899	7907	19853	java/lang/Exception
        //   8409	8448	19853	java/lang/Exception
        //   8454	8462	19853	java/lang/Exception
        //   8468	8476	19853	java/lang/Exception
        //   8482	8487	19853	java/lang/Exception
        //   8510	8549	19853	java/lang/Exception
        //   8557	8565	19853	java/lang/Exception
        //   8571	8578	19853	java/lang/Exception
        //   8616	8621	19853	java/lang/Exception
        //   8638	8693	19853	java/lang/Exception
        //   8699	8707	19853	java/lang/Exception
        //   8716	8721	19853	java/lang/Exception
        //   8739	8786	19853	java/lang/Exception
        //   8792	8800	19853	java/lang/Exception
        //   8806	8813	19853	java/lang/Exception
        //   8863	8868	19853	java/lang/Exception
        //   13320	13367	19878	java/lang/Exception
        //   13379	13387	19878	java/lang/Exception
        //   13395	13403	19878	java/lang/Exception
        //   13411	13416	19878	java/lang/Exception
        //   1150	1188	20129	java/lang/Exception
        //   1242	1250	20129	java/lang/Exception
        //   1304	1314	20129	java/lang/Exception
        //   1371	1376	20129	java/lang/Exception
        //   1430	1438	20129	java/lang/Exception
        //   1492	1500	20129	java/lang/Exception
        //   1559	1567	20129	java/lang/Exception
        //   1621	1659	20129	java/lang/Exception
        //   1717	1725	20129	java/lang/Exception
        //   1779	1787	20129	java/lang/Exception
        //   1841	1846	20129	java/lang/Exception
        //   1900	1908	20129	java/lang/Exception
        //   1962	1969	20129	java/lang/Exception
        //   2023	2071	20129	java/lang/Exception
        //   2125	2133	20129	java/lang/Exception
        //   2187	2195	20129	java/lang/Exception
        //   2249	2254	20129	java/lang/Exception
        //   2308	2356	20129	java/lang/Exception
        //   2410	2418	20129	java/lang/Exception
        //   2472	2480	20129	java/lang/Exception
        //   2534	2539	20129	java/lang/Exception
        //   8993	8998	20129	java/lang/Exception
        //   9052	9091	20129	java/lang/Exception
        //   9145	9153	20129	java/lang/Exception
        //   9207	9215	20129	java/lang/Exception
        //   9274	9289	20129	java/lang/Exception
        //   9343	9348	20129	java/lang/Exception
        //   9402	9412	20129	java/lang/Exception
        //   9466	9473	20129	java/lang/Exception
        //   9527	9535	20129	java/lang/Exception
        //   9589	9595	20129	java/lang/Exception
        //   9649	9654	20129	java/lang/Exception
        //   9708	9713	20129	java/lang/Exception
        //   9774	9829	20129	java/lang/Exception
        //   9883	9891	20129	java/lang/Exception
        //   9948	9953	20129	java/lang/Exception
        //   10013	10068	20129	java/lang/Exception
        //   10122	10130	20129	java/lang/Exception
        //   10187	10192	20129	java/lang/Exception
        //   10448	10456	20129	java/lang/Exception
        //   10513	10562	20129	java/lang/Exception
        //   10616	10624	20129	java/lang/Exception
        //   10678	10687	20129	java/lang/Exception
        //   10761	10766	20129	java/lang/Exception
        //   10827	10899	20129	java/lang/Exception
        //   10956	11020	20129	java/lang/Exception
        //   11077	11084	20129	java/lang/Exception
        //   11148	11196	20129	java/lang/Exception
        //   11250	11258	20129	java/lang/Exception
        //   11312	11321	20129	java/lang/Exception
        //   11395	11400	20129	java/lang/Exception
        //   11461	11533	20129	java/lang/Exception
        //   11590	11654	20129	java/lang/Exception
        //   11711	11775	20129	java/lang/Exception
        //   11832	11870	20129	java/lang/Exception
        //   11928	11936	20129	java/lang/Exception
        //   11990	11998	20129	java/lang/Exception
        //   12052	12057	20129	java/lang/Exception
        //   12114	12153	20129	java/lang/Exception
        //   12207	12215	20129	java/lang/Exception
        //   12269	12278	20129	java/lang/Exception
        //   12352	12357	20129	java/lang/Exception
        //   12418	12480	20129	java/lang/Exception
        //   12537	12591	20129	java/lang/Exception
        //   15545	15598	20129	java/lang/Exception
      }
    });
  }

  public void getNewTask(ArrayList<Integer> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        int i = 0;
        SQLiteCursor localSQLiteCursor;
        try
        {
          if (this.val$oldTask != null)
          {
            localObject1 = TextUtils.join(",", this.val$oldTask);
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM enc_tasks_v2 WHERE mid IN(%s)", new Object[] { localObject1 })).stepThis().dispose();
          }
          Object localObject1 = null;
          localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT mid, date FROM enc_tasks_v2 WHERE date = (SELECT min(date) FROM enc_tasks_v2)", new Object[0]);
          while (localSQLiteCursor.next())
          {
            int j = localSQLiteCursor.intValue(0);
            i = localSQLiteCursor.intValue(1);
            Object localObject2 = localObject1;
            if (localObject1 == null)
              localObject2 = new ArrayList();
            ((ArrayList)localObject2).add(Integer.valueOf(j));
            localObject1 = localObject2;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localSQLiteCursor.dispose();
        MessagesController.getInstance().processLoadedDeleteTask(i, localException);
      }
    });
  }

  public HashMap<Integer, ContactsController.Contact> getPhoneBookContact()
  {
    HashMap localHashMap = new HashMap();
    label233: 
    while (true)
    {
      SQLiteCursor localSQLiteCursor;
      try
      {
        localSQLiteCursor = this.database.queryFinalized("SELECT us.uid, us.fname, us.sname, up.phone, up.sphone, up.deleted FROM user_contacts_v6 as us LEFT JOIN user_phones_v6 as up ON us.uid = up.uid WHERE 1", new Object[0]);
        if (localSQLiteCursor.next())
        {
          int i = localSQLiteCursor.intValue(0);
          ContactsController.Contact localContact = (ContactsController.Contact)localHashMap.get(Integer.valueOf(i));
          if (localContact != null)
            break label233;
          localContact = new ContactsController.Contact();
          localContact.first_name = localSQLiteCursor.stringValue(1);
          localContact.last_name = localSQLiteCursor.stringValue(2);
          localContact.id = i;
          localHashMap.put(Integer.valueOf(i), localContact);
          String str3 = localSQLiteCursor.stringValue(3);
          if (str3 == null)
            continue;
          localContact.phones.add(str3);
          String str2 = localSQLiteCursor.stringValue(4);
          if (str2 == null)
            continue;
          String str1 = str2;
          if (str2.length() != 8)
            continue;
          str1 = str2;
          if (str3.length() == 8)
            continue;
          str1 = b.b(str3);
          localContact.shortPhones.add(str1);
          localContact.phoneDeleted.add(Integer.valueOf(localSQLiteCursor.intValue(5)));
          localContact.phoneTypes.add("");
          continue;
        }
      }
      catch (Exception localException)
      {
        localHashMap.clear();
        FileLog.e("tmessages", localException);
        return localHashMap;
      }
      localSQLiteCursor.dispose();
      return localHashMap;
    }
  }

  public TLObject getSentFile(String paramString, int paramInt)
  {
    if ((paramString == null) || (paramString.endsWith("attheme")))
      return null;
    Semaphore localSemaphore = new Semaphore(0);
    ArrayList localArrayList = new ArrayList();
    this.storageQueue.postRunnable(new Runnable(paramString, paramInt, localArrayList, localSemaphore)
    {
      public void run()
      {
        try
        {
          Object localObject1 = Utilities.MD5(this.val$path);
          TLRPC.MessageMedia localMessageMedia;
          if (localObject1 != null)
          {
            localObject1 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM sent_files_v2 WHERE uid = '%s' AND type = %d", new Object[] { localObject1, Integer.valueOf(this.val$type) }), new Object[0]);
            if (((SQLiteCursor)localObject1).next())
            {
              NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localNativeByteBuffer != null)
              {
                localMessageMedia = TLRPC.MessageMedia.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                if (!(localMessageMedia instanceof TLRPC.TL_messageMediaDocument))
                  break label119;
                this.val$result.add(((TLRPC.TL_messageMediaDocument)localMessageMedia).document);
              }
            }
          }
          while (true)
          {
            ((SQLiteCursor)localObject1).dispose();
            return;
            label119: if (!(localMessageMedia instanceof TLRPC.TL_messageMediaPhoto))
              continue;
            this.val$result.add(((TLRPC.TL_messageMediaPhoto)localMessageMedia).photo);
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          this.val$semaphore.release();
        }
        throw localObject2;
      }
    });
    try
    {
      localSemaphore.acquire();
      if (!localArrayList.isEmpty())
        return (TLObject)localArrayList.get(0);
    }
    catch (Exception paramString)
    {
      while (true)
        FileLog.e(paramString);
    }
    return null;
  }

  public DispatchQueue getStorageQueue()
  {
    return this.storageQueue;
  }

  public ArrayList<TLRPC.User> getTelegramContacts()
  {
    ArrayList localArrayList = new ArrayList();
    StringBuilder localStringBuilder;
    do
    {
      while (true)
      {
        try
        {
          SQLiteCursor localSQLiteCursor = this.database.queryFinalized("SELECT * FROM contacts WHERE 1", new Object[0]);
          localStringBuilder = new StringBuilder();
          if (!localSQLiteCursor.next())
            break;
          int i = localSQLiteCursor.intValue(0);
          TLRPC.TL_contact localTL_contact = new TLRPC.TL_contact();
          localTL_contact.user_id = i;
          if (localSQLiteCursor.intValue(1) == 1)
          {
            bool = true;
            localTL_contact.mutual = bool;
            if (localStringBuilder.length() == 0)
              continue;
            localStringBuilder.append(",");
            localStringBuilder.append(localTL_contact.user_id);
            continue;
          }
        }
        catch (Exception localException)
        {
          localArrayList.clear();
          FileLog.e("tmessages", localException);
          return localArrayList;
        }
        boolean bool = false;
      }
      localException.dispose();
    }
    while (localStringBuilder.length() == 0);
    getUsersInternal(localStringBuilder.toString(), localArrayList);
    return localArrayList;
  }

  public void getUnsentMessages(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        Object localObject2;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        ArrayList localArrayList4;
        Object localObject3;
        Object localObject1;
        ArrayList localArrayList5;
        ArrayList localArrayList6;
        SQLiteCursor localSQLiteCursor;
        int i;
        while (true)
        {
          int j;
          try
          {
            localObject2 = new HashMap();
            ArrayList localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localArrayList4 = new ArrayList();
            localObject3 = new ArrayList();
            localObject1 = new ArrayList();
            localArrayList5 = new ArrayList();
            localArrayList6 = new ArrayList();
            localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT m.read_state, m.data, m.send_state, m.mid, m.date, r.random_id, m.uid, s.seq_in, s.seq_out, m.ttl FROM messages as m LEFT JOIN randoms as r ON r.mid = m.mid LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.mid < 0 AND m.send_state = 1 ORDER BY m.mid DESC LIMIT " + this.val$count, new Object[0]);
            if (!localSQLiteCursor.next())
              break;
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(1);
            if (localNativeByteBuffer == null)
              continue;
            TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (((HashMap)localObject2).containsKey(Integer.valueOf(localMessage.id)))
              continue;
            MessageObject.setUnreadFlags(localMessage, localSQLiteCursor.intValue(0));
            localMessage.id = localSQLiteCursor.intValue(3);
            localMessage.date = localSQLiteCursor.intValue(4);
            if (localSQLiteCursor.isNull(5))
              continue;
            localMessage.random_id = localSQLiteCursor.longValue(5);
            localMessage.dialog_id = localSQLiteCursor.longValue(6);
            localMessage.seq_in = localSQLiteCursor.intValue(7);
            localMessage.seq_out = localSQLiteCursor.intValue(8);
            localMessage.ttl = localSQLiteCursor.intValue(9);
            localArrayList1.add(localMessage);
            ((HashMap)localObject2).put(Integer.valueOf(localMessage.id), localMessage);
            i = (int)localMessage.dialog_id;
            j = (int)(localMessage.dialog_id >> 32);
            if (i == 0)
              break label494;
            if (j == 1)
            {
              if (localArrayList5.contains(Integer.valueOf(i)))
                continue;
              localArrayList5.add(Integer.valueOf(i));
              MessagesStorage.addUsersAndChatsFromMessage(localMessage, (ArrayList)localObject3, (ArrayList)localObject1);
              localMessage.send_state = localSQLiteCursor.intValue(2);
              if (((localMessage.to_id.channel_id != 0) || (MessageObject.isUnread(localMessage)) || (i == 0)) && (localMessage.id <= 0))
                continue;
              localMessage.send_state = 0;
              if ((i != 0) || (localSQLiteCursor.isNull(5)))
                continue;
              localMessage.random_id = localSQLiteCursor.longValue(5);
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          if (i < 0)
          {
            j = -i;
            if (((ArrayList)localObject1).contains(Integer.valueOf(j)))
              continue;
            ((ArrayList)localObject1).add(Integer.valueOf(-i));
            continue;
          }
          if (((ArrayList)localObject3).contains(Integer.valueOf(i)))
            continue;
          ((ArrayList)localObject3).add(Integer.valueOf(i));
          continue;
          label494: if (localArrayList6.contains(Integer.valueOf(j)))
            continue;
          localArrayList6.add(Integer.valueOf(j));
        }
        localSQLiteCursor.dispose();
        if (!localArrayList6.isEmpty())
          MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", localArrayList6), localArrayList4, (ArrayList)localObject3);
        if (!((ArrayList)localObject3).isEmpty())
          MessagesStorage.this.getUsersInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList2);
        if ((!((ArrayList)localObject1).isEmpty()) || (!localArrayList5.isEmpty()))
        {
          localObject2 = new StringBuilder();
          i = 0;
          while (i < ((ArrayList)localObject1).size())
          {
            localObject3 = (Integer)((ArrayList)localObject1).get(i);
            if (((StringBuilder)localObject2).length() != 0)
              ((StringBuilder)localObject2).append(",");
            ((StringBuilder)localObject2).append(localObject3);
            i += 1;
          }
        }
        while (true)
        {
          if (i < localArrayList5.size())
          {
            localObject1 = (Integer)localArrayList5.get(i);
            if (((StringBuilder)localObject2).length() != 0)
              ((StringBuilder)localObject2).append(",");
            ((StringBuilder)localObject2).append(-((Integer)localObject1).intValue());
            i += 1;
            continue;
          }
          MessagesStorage.this.getChatsInternal(((StringBuilder)localObject2).toString(), localArrayList3);
          SendMessagesHelper.getInstance().processUnsentMessages(localException, localArrayList2, localArrayList3, localArrayList4);
          return;
          i = 0;
        }
      }
    });
  }

  public TLRPC.User getUser(int paramInt)
  {
    try
    {
      Object localObject = new ArrayList();
      getUsersInternal("" + paramInt, (ArrayList)localObject);
      if (!((ArrayList)localObject).isEmpty())
      {
        localObject = (TLRPC.User)((ArrayList)localObject).get(0);
        return localObject;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return (TLRPC.User)null;
  }

  public TLRPC.User getUserSync(int paramInt)
  {
    Semaphore localSemaphore = new Semaphore(0);
    TLRPC.User[] arrayOfUser = new TLRPC.User[1];
    getInstance().getStorageQueue().postRunnable(new Runnable(arrayOfUser, paramInt, localSemaphore)
    {
      public void run()
      {
        this.val$user[0] = MessagesStorage.this.getUser(this.val$user_id);
        this.val$semaphore.release();
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfUser[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public ArrayList<TLRPC.User> getUsers(ArrayList<Integer> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    try
    {
      getUsersInternal(TextUtils.join(",", paramArrayList), localArrayList);
      return localArrayList;
    }
    catch (Exception paramArrayList)
    {
      localArrayList.clear();
      FileLog.e(paramArrayList);
    }
    return localArrayList;
  }

  public void getUsersInternal(String paramString, ArrayList<TLRPC.User> paramArrayList)
  {
    if ((paramString == null) || (paramString.length() == 0) || (paramArrayList == null))
      return;
    paramString = this.database.queryFinalized(String.format(Locale.US, "SELECT data, status FROM users WHERE uid IN(%s)", new Object[] { paramString }), new Object[0]);
    while (paramString.next())
      try
      {
        NativeByteBuffer localNativeByteBuffer = paramString.byteBufferValue(0);
        if (localNativeByteBuffer == null)
          continue;
        TLRPC.User localUser = TLRPC.User.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        if (localUser == null)
          continue;
        if (localUser.status != null)
          localUser.status.expires = paramString.intValue(1);
        paramArrayList.add(localUser);
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
    paramString.dispose();
  }

  public void getWallpapers()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT data FROM wallpapers WHERE 1", new Object[0]);
          localArrayList = new ArrayList();
          while (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            if (localNativeByteBuffer == null)
              continue;
            TLRPC.WallPaper localWallPaper = TLRPC.WallPaper.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            localArrayList.add(localWallPaper);
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localException.dispose();
        AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.wallpapersDidLoaded, new Object[] { this.val$wallPapers });
          }
        });
      }
    });
  }

  public boolean hasAuthMessage(int paramInt)
  {
    Semaphore localSemaphore = new Semaphore(0);
    boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable(paramInt, arrayOfBoolean, localSemaphore)
    {
      public void run()
      {
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", new Object[] { Integer.valueOf(this.val$date) }), new Object[0]);
          this.val$result[0] = localSQLiteCursor.next();
          localSQLiteCursor.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          this.val$semaphore.release();
        }
        throw localObject;
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public boolean isDialogHasMessages(long paramLong)
  {
    Semaphore localSemaphore = new Semaphore(0);
    boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable(paramLong, arrayOfBoolean, localSemaphore)
    {
      public void run()
      {
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages WHERE uid = %d LIMIT 1", new Object[] { Long.valueOf(this.val$did) }), new Object[0]);
          this.val$result[0] = localSQLiteCursor.next();
          localSQLiteCursor.dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          this.val$semaphore.release();
        }
        throw localObject;
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public boolean isMigratedChat(int paramInt)
  {
    Semaphore localSemaphore = new Semaphore(0);
    boolean[] arrayOfBoolean = new boolean[1];
    this.storageQueue.postRunnable(new Runnable(paramInt, arrayOfBoolean, localSemaphore)
    {
      public void run()
      {
        int j = 0;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT info FROM chat_settings_v2 WHERE uid = " + this.val$chat_id, new Object[0]);
          boolean[] arrayOfBoolean = null;
          new ArrayList();
          Object localObject1 = arrayOfBoolean;
          if (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            localObject1 = arrayOfBoolean;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
            }
          }
          localSQLiteCursor.dispose();
          arrayOfBoolean = this.val$result;
          int i = j;
          if ((localObject1 instanceof TLRPC.TL_channelFull))
          {
            i = j;
            if (((TLRPC.ChatFull)localObject1).migrated_from_chat_id != 0)
              i = 1;
          }
          arrayOfBoolean[0] = i;
          if (this.val$semaphore != null)
            this.val$semaphore.release();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        finally
        {
          if (this.val$semaphore != null)
            this.val$semaphore.release();
        }
        throw localObject2;
      }
    });
    try
    {
      localSemaphore.acquire();
      return arrayOfBoolean[0];
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void loadChatInfo(int paramInt, Semaphore paramSemaphore, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt, paramSemaphore, paramBoolean1, paramBoolean2)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: aconst_null
        //   1: astore 8
        //   3: iconst_0
        //   4: istore_2
        //   5: iconst_0
        //   6: istore_1
        //   7: new 41	java/util/ArrayList
        //   10: dup
        //   11: invokespecial 42	java/util/ArrayList:<init>	()V
        //   14: astore 9
        //   16: aload_0
        //   17: getfield 24	org/vidogram/messenger/MessagesStorage$38:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   20: invokestatic 46	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   23: new 48	java/lang/StringBuilder
        //   26: dup
        //   27: invokespecial 49	java/lang/StringBuilder:<init>	()V
        //   30: ldc 51
        //   32: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   35: aload_0
        //   36: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   39: invokevirtual 58	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   42: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   45: iconst_0
        //   46: anewarray 4	java/lang/Object
        //   49: invokevirtual 68	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   52: astore 7
        //   54: aload 7
        //   56: invokevirtual 74	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   59: ifeq +1045 -> 1104
        //   62: aload 7
        //   64: iconst_0
        //   65: invokevirtual 78	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   68: astore 10
        //   70: aload 10
        //   72: ifnull +1032 -> 1104
        //   75: aload 10
        //   77: aload 10
        //   79: iconst_0
        //   80: invokevirtual 84	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   83: iconst_0
        //   84: invokestatic 90	org/vidogram/tgnet/TLRPC$ChatFull:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$ChatFull;
        //   87: astore 5
        //   89: aload 5
        //   91: astore 6
        //   93: aload 5
        //   95: astore 4
        //   97: aload 10
        //   99: invokevirtual 93	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   102: aload 5
        //   104: astore 6
        //   106: aload 5
        //   108: astore 4
        //   110: aload 5
        //   112: aload 7
        //   114: iconst_1
        //   115: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   118: putfield 100	org/vidogram/tgnet/TLRPC$ChatFull:pinned_msg_id	I
        //   121: aload 5
        //   123: astore 6
        //   125: aload 5
        //   127: astore 4
        //   129: aload 7
        //   131: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   134: aload 5
        //   136: astore 6
        //   138: aload 5
        //   140: astore 4
        //   142: aload 5
        //   144: instanceof 105
        //   147: ifeq +297 -> 444
        //   150: aload 5
        //   152: astore 6
        //   154: aload 5
        //   156: astore 4
        //   158: new 48	java/lang/StringBuilder
        //   161: dup
        //   162: invokespecial 49	java/lang/StringBuilder:<init>	()V
        //   165: astore 7
        //   167: aload 5
        //   169: astore 6
        //   171: aload 5
        //   173: astore 4
        //   175: iload_1
        //   176: aload 5
        //   178: getfield 109	org/vidogram/tgnet/TLRPC$ChatFull:participants	Lorg/vidogram/tgnet/TLRPC$ChatParticipants;
        //   181: getfield 114	org/vidogram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
        //   184: invokevirtual 118	java/util/ArrayList:size	()I
        //   187: if_icmpge +86 -> 273
        //   190: aload 5
        //   192: astore 6
        //   194: aload 5
        //   196: astore 4
        //   198: aload 5
        //   200: getfield 109	org/vidogram/tgnet/TLRPC$ChatFull:participants	Lorg/vidogram/tgnet/TLRPC$ChatParticipants;
        //   203: getfield 114	org/vidogram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
        //   206: iload_1
        //   207: invokevirtual 122	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   210: checkcast 124	org/vidogram/tgnet/TLRPC$ChatParticipant
        //   213: astore 10
        //   215: aload 5
        //   217: astore 6
        //   219: aload 5
        //   221: astore 4
        //   223: aload 7
        //   225: invokevirtual 127	java/lang/StringBuilder:length	()I
        //   228: ifeq +19 -> 247
        //   231: aload 5
        //   233: astore 6
        //   235: aload 5
        //   237: astore 4
        //   239: aload 7
        //   241: ldc 129
        //   243: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   246: pop
        //   247: aload 5
        //   249: astore 6
        //   251: aload 5
        //   253: astore 4
        //   255: aload 7
        //   257: aload 10
        //   259: getfield 132	org/vidogram/tgnet/TLRPC$ChatParticipant:user_id	I
        //   262: invokevirtual 58	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   265: pop
        //   266: iload_1
        //   267: iconst_1
        //   268: iadd
        //   269: istore_1
        //   270: goto -103 -> 167
        //   273: aload 5
        //   275: astore 6
        //   277: aload 5
        //   279: astore 4
        //   281: aload 7
        //   283: invokevirtual 127	java/lang/StringBuilder:length	()I
        //   286: ifeq +25 -> 311
        //   289: aload 5
        //   291: astore 6
        //   293: aload 5
        //   295: astore 4
        //   297: aload_0
        //   298: getfield 24	org/vidogram/messenger/MessagesStorage$38:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   301: aload 7
        //   303: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   306: aload 9
        //   308: invokevirtual 136	org/vidogram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   311: aload 5
        //   313: astore 6
        //   315: aload 5
        //   317: astore 4
        //   319: aload_0
        //   320: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   323: ifnull +18 -> 341
        //   326: aload 5
        //   328: astore 6
        //   330: aload 5
        //   332: astore 4
        //   334: aload_0
        //   335: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   338: invokevirtual 141	java/util/concurrent/Semaphore:release	()V
        //   341: aload 8
        //   343: astore 7
        //   345: aload 5
        //   347: astore 6
        //   349: aload 5
        //   351: astore 4
        //   353: aload 5
        //   355: instanceof 143
        //   358: ifeq +46 -> 404
        //   361: aload 8
        //   363: astore 7
        //   365: aload 5
        //   367: astore 6
        //   369: aload 5
        //   371: astore 4
        //   373: aload 5
        //   375: getfield 100	org/vidogram/tgnet/TLRPC$ChatFull:pinned_msg_id	I
        //   378: ifeq +26 -> 404
        //   381: aload 5
        //   383: astore 6
        //   385: aload 5
        //   387: astore 4
        //   389: aload_0
        //   390: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   393: aload 5
        //   395: getfield 100	org/vidogram/tgnet/TLRPC$ChatFull:pinned_msg_id	I
        //   398: iconst_0
        //   399: invokestatic 149	org/vidogram/messenger/query/MessagesQuery:loadPinnedMessage	(IIZ)Lorg/vidogram/messenger/MessageObject;
        //   402: astore 7
        //   404: invokestatic 155	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   407: aload_0
        //   408: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   411: aload 5
        //   413: aload 9
        //   415: iconst_1
        //   416: aload_0
        //   417: getfield 30	org/vidogram/messenger/MessagesStorage$38:val$force	Z
        //   420: aload_0
        //   421: getfield 32	org/vidogram/messenger/MessagesStorage$38:val$byChannelUsers	Z
        //   424: aload 7
        //   426: invokevirtual 159	org/vidogram/messenger/MessagesController:processChatInfo	(ILorg/vidogram/tgnet/TLRPC$ChatFull;Ljava/util/ArrayList;ZZZLorg/vidogram/messenger/MessageObject;)V
        //   429: aload_0
        //   430: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   433: ifnull +10 -> 443
        //   436: aload_0
        //   437: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   440: invokevirtual 141	java/util/concurrent/Semaphore:release	()V
        //   443: return
        //   444: aload 5
        //   446: astore 6
        //   448: aload 5
        //   450: astore 4
        //   452: aload 5
        //   454: instanceof 143
        //   457: ifeq -146 -> 311
        //   460: aload 5
        //   462: astore 6
        //   464: aload 5
        //   466: astore 4
        //   468: aload_0
        //   469: getfield 24	org/vidogram/messenger/MessagesStorage$38:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   472: invokestatic 46	org/vidogram/messenger/MessagesStorage:access$000	(Lorg/vidogram/messenger/MessagesStorage;)Lorg/vidogram/SQLite/SQLiteDatabase;
        //   475: new 48	java/lang/StringBuilder
        //   478: dup
        //   479: invokespecial 49	java/lang/StringBuilder:<init>	()V
        //   482: ldc 161
        //   484: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   487: aload_0
        //   488: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   491: ineg
        //   492: invokevirtual 58	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   495: ldc 163
        //   497: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   500: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   503: iconst_0
        //   504: anewarray 4	java/lang/Object
        //   507: invokevirtual 68	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   510: astore 10
        //   512: aload 5
        //   514: astore 6
        //   516: aload 5
        //   518: astore 4
        //   520: aload 5
        //   522: new 165	org/vidogram/tgnet/TLRPC$TL_chatParticipants
        //   525: dup
        //   526: invokespecial 166	org/vidogram/tgnet/TLRPC$TL_chatParticipants:<init>	()V
        //   529: putfield 109	org/vidogram/tgnet/TLRPC$ChatFull:participants	Lorg/vidogram/tgnet/TLRPC$ChatParticipants;
        //   532: aload 5
        //   534: astore 6
        //   536: aload 5
        //   538: astore 4
        //   540: aload 10
        //   542: invokevirtual 74	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   545: istore_3
        //   546: iload_3
        //   547: ifeq +313 -> 860
        //   550: aload 5
        //   552: astore 4
        //   554: aload 10
        //   556: iconst_0
        //   557: invokevirtual 78	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   560: astore 7
        //   562: aload 7
        //   564: ifnull +534 -> 1098
        //   567: aload 5
        //   569: astore 4
        //   571: aload 7
        //   573: aload 7
        //   575: iconst_0
        //   576: invokevirtual 84	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   579: iconst_0
        //   580: invokestatic 171	org/vidogram/tgnet/TLRPC$User:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$User;
        //   583: astore 6
        //   585: aload 5
        //   587: astore 4
        //   589: aload 7
        //   591: invokevirtual 93	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   594: aload 5
        //   596: astore 4
        //   598: aload 10
        //   600: iconst_2
        //   601: invokevirtual 78	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   604: astore 11
        //   606: aload 11
        //   608: ifnull +484 -> 1092
        //   611: aload 5
        //   613: astore 4
        //   615: aload 11
        //   617: aload 11
        //   619: iconst_0
        //   620: invokevirtual 84	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   623: iconst_0
        //   624: invokestatic 176	org/vidogram/tgnet/TLRPC$ChannelParticipant:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$ChannelParticipant;
        //   627: astore 7
        //   629: aload 5
        //   631: astore 4
        //   633: aload 11
        //   635: invokevirtual 93	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   638: aload 6
        //   640: ifnull -108 -> 532
        //   643: aload 7
        //   645: ifnull -113 -> 532
        //   648: aload 5
        //   650: astore 4
        //   652: aload 6
        //   654: getfield 180	org/vidogram/tgnet/TLRPC$User:status	Lorg/vidogram/tgnet/TLRPC$UserStatus;
        //   657: ifnull +21 -> 678
        //   660: aload 5
        //   662: astore 4
        //   664: aload 6
        //   666: getfield 180	org/vidogram/tgnet/TLRPC$User:status	Lorg/vidogram/tgnet/TLRPC$UserStatus;
        //   669: aload 10
        //   671: iconst_1
        //   672: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   675: putfield 185	org/vidogram/tgnet/TLRPC$UserStatus:expires	I
        //   678: aload 5
        //   680: astore 4
        //   682: aload 9
        //   684: aload 6
        //   686: invokevirtual 189	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   689: pop
        //   690: aload 5
        //   692: astore 4
        //   694: aload 7
        //   696: aload 10
        //   698: iconst_3
        //   699: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   702: putfield 192	org/vidogram/tgnet/TLRPC$ChannelParticipant:date	I
        //   705: aload 5
        //   707: astore 4
        //   709: new 194	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant
        //   712: dup
        //   713: invokespecial 195	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant:<init>	()V
        //   716: astore 6
        //   718: aload 5
        //   720: astore 4
        //   722: aload 6
        //   724: aload 7
        //   726: getfield 196	org/vidogram/tgnet/TLRPC$ChannelParticipant:user_id	I
        //   729: putfield 197	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant:user_id	I
        //   732: aload 5
        //   734: astore 4
        //   736: aload 6
        //   738: aload 7
        //   740: getfield 192	org/vidogram/tgnet/TLRPC$ChannelParticipant:date	I
        //   743: putfield 198	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant:date	I
        //   746: aload 5
        //   748: astore 4
        //   750: aload 6
        //   752: aload 7
        //   754: getfield 201	org/vidogram/tgnet/TLRPC$ChannelParticipant:inviter_id	I
        //   757: putfield 202	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant:inviter_id	I
        //   760: aload 5
        //   762: astore 4
        //   764: aload 6
        //   766: aload 7
        //   768: putfield 206	org/vidogram/tgnet/TLRPC$TL_chatChannelParticipant:channelParticipant	Lorg/vidogram/tgnet/TLRPC$ChannelParticipant;
        //   771: aload 5
        //   773: astore 4
        //   775: aload 5
        //   777: getfield 109	org/vidogram/tgnet/TLRPC$ChatFull:participants	Lorg/vidogram/tgnet/TLRPC$ChatParticipants;
        //   780: getfield 114	org/vidogram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
        //   783: aload 6
        //   785: invokevirtual 189	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   788: pop
        //   789: goto -257 -> 532
        //   792: astore 7
        //   794: aload 5
        //   796: astore 6
        //   798: aload 5
        //   800: astore 4
        //   802: aload 7
        //   804: invokestatic 212	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   807: goto -275 -> 532
        //   810: astore 5
        //   812: aload 6
        //   814: astore 4
        //   816: aload 5
        //   818: invokestatic 212	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   821: invokestatic 155	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   824: aload_0
        //   825: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   828: aload 6
        //   830: aload 9
        //   832: iconst_1
        //   833: aload_0
        //   834: getfield 30	org/vidogram/messenger/MessagesStorage$38:val$force	Z
        //   837: aload_0
        //   838: getfield 32	org/vidogram/messenger/MessagesStorage$38:val$byChannelUsers	Z
        //   841: aconst_null
        //   842: invokevirtual 159	org/vidogram/messenger/MessagesController:processChatInfo	(ILorg/vidogram/tgnet/TLRPC$ChatFull;Ljava/util/ArrayList;ZZZLorg/vidogram/messenger/MessageObject;)V
        //   845: aload_0
        //   846: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   849: ifnull -406 -> 443
        //   852: aload_0
        //   853: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   856: invokevirtual 141	java/util/concurrent/Semaphore:release	()V
        //   859: return
        //   860: aload 5
        //   862: astore 6
        //   864: aload 5
        //   866: astore 4
        //   868: aload 10
        //   870: invokevirtual 103	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   873: aload 5
        //   875: astore 6
        //   877: aload 5
        //   879: astore 4
        //   881: new 48	java/lang/StringBuilder
        //   884: dup
        //   885: invokespecial 49	java/lang/StringBuilder:<init>	()V
        //   888: astore 7
        //   890: iload_2
        //   891: istore_1
        //   892: aload 5
        //   894: astore 6
        //   896: aload 5
        //   898: astore 4
        //   900: iload_1
        //   901: aload 5
        //   903: getfield 215	org/vidogram/tgnet/TLRPC$ChatFull:bot_info	Ljava/util/ArrayList;
        //   906: invokevirtual 118	java/util/ArrayList:size	()I
        //   909: if_icmpge +83 -> 992
        //   912: aload 5
        //   914: astore 6
        //   916: aload 5
        //   918: astore 4
        //   920: aload 5
        //   922: getfield 215	org/vidogram/tgnet/TLRPC$ChatFull:bot_info	Ljava/util/ArrayList;
        //   925: iload_1
        //   926: invokevirtual 122	java/util/ArrayList:get	(I)Ljava/lang/Object;
        //   929: checkcast 217	org/vidogram/tgnet/TLRPC$BotInfo
        //   932: astore 10
        //   934: aload 5
        //   936: astore 6
        //   938: aload 5
        //   940: astore 4
        //   942: aload 7
        //   944: invokevirtual 127	java/lang/StringBuilder:length	()I
        //   947: ifeq +19 -> 966
        //   950: aload 5
        //   952: astore 6
        //   954: aload 5
        //   956: astore 4
        //   958: aload 7
        //   960: ldc 129
        //   962: invokevirtual 55	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   965: pop
        //   966: aload 5
        //   968: astore 6
        //   970: aload 5
        //   972: astore 4
        //   974: aload 7
        //   976: aload 10
        //   978: getfield 218	org/vidogram/tgnet/TLRPC$BotInfo:user_id	I
        //   981: invokevirtual 58	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   984: pop
        //   985: iload_1
        //   986: iconst_1
        //   987: iadd
        //   988: istore_1
        //   989: goto -97 -> 892
        //   992: aload 5
        //   994: astore 6
        //   996: aload 5
        //   998: astore 4
        //   1000: aload 7
        //   1002: invokevirtual 127	java/lang/StringBuilder:length	()I
        //   1005: ifeq -694 -> 311
        //   1008: aload 5
        //   1010: astore 6
        //   1012: aload 5
        //   1014: astore 4
        //   1016: aload_0
        //   1017: getfield 24	org/vidogram/messenger/MessagesStorage$38:this$0	Lorg/vidogram/messenger/MessagesStorage;
        //   1020: aload 7
        //   1022: invokevirtual 62	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   1025: aload 9
        //   1027: invokevirtual 136	org/vidogram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1030: goto -719 -> 311
        //   1033: astore 5
        //   1035: invokestatic 155	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
        //   1038: aload_0
        //   1039: getfield 26	org/vidogram/messenger/MessagesStorage$38:val$chat_id	I
        //   1042: aload 4
        //   1044: aload 9
        //   1046: iconst_1
        //   1047: aload_0
        //   1048: getfield 30	org/vidogram/messenger/MessagesStorage$38:val$force	Z
        //   1051: aload_0
        //   1052: getfield 32	org/vidogram/messenger/MessagesStorage$38:val$byChannelUsers	Z
        //   1055: aconst_null
        //   1056: invokevirtual 159	org/vidogram/messenger/MessagesController:processChatInfo	(ILorg/vidogram/tgnet/TLRPC$ChatFull;Ljava/util/ArrayList;ZZZLorg/vidogram/messenger/MessageObject;)V
        //   1059: aload_0
        //   1060: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   1063: ifnull +10 -> 1073
        //   1066: aload_0
        //   1067: getfield 28	org/vidogram/messenger/MessagesStorage$38:val$semaphore	Ljava/util/concurrent/Semaphore;
        //   1070: invokevirtual 141	java/util/concurrent/Semaphore:release	()V
        //   1073: aload 5
        //   1075: athrow
        //   1076: astore 5
        //   1078: aconst_null
        //   1079: astore 4
        //   1081: goto -46 -> 1035
        //   1084: astore 5
        //   1086: aconst_null
        //   1087: astore 6
        //   1089: goto -277 -> 812
        //   1092: aconst_null
        //   1093: astore 7
        //   1095: goto -457 -> 638
        //   1098: aconst_null
        //   1099: astore 6
        //   1101: goto -507 -> 594
        //   1104: aconst_null
        //   1105: astore 5
        //   1107: goto -986 -> 121
        //
        // Exception table:
        //   from	to	target	type
        //   554	562	792	java/lang/Exception
        //   571	585	792	java/lang/Exception
        //   589	594	792	java/lang/Exception
        //   598	606	792	java/lang/Exception
        //   615	629	792	java/lang/Exception
        //   633	638	792	java/lang/Exception
        //   652	660	792	java/lang/Exception
        //   664	678	792	java/lang/Exception
        //   682	690	792	java/lang/Exception
        //   694	705	792	java/lang/Exception
        //   709	718	792	java/lang/Exception
        //   722	732	792	java/lang/Exception
        //   736	746	792	java/lang/Exception
        //   750	760	792	java/lang/Exception
        //   764	771	792	java/lang/Exception
        //   775	789	792	java/lang/Exception
        //   97	102	810	java/lang/Exception
        //   110	121	810	java/lang/Exception
        //   129	134	810	java/lang/Exception
        //   142	150	810	java/lang/Exception
        //   158	167	810	java/lang/Exception
        //   175	190	810	java/lang/Exception
        //   198	215	810	java/lang/Exception
        //   223	231	810	java/lang/Exception
        //   239	247	810	java/lang/Exception
        //   255	266	810	java/lang/Exception
        //   281	289	810	java/lang/Exception
        //   297	311	810	java/lang/Exception
        //   319	326	810	java/lang/Exception
        //   334	341	810	java/lang/Exception
        //   353	361	810	java/lang/Exception
        //   373	381	810	java/lang/Exception
        //   389	404	810	java/lang/Exception
        //   452	460	810	java/lang/Exception
        //   468	512	810	java/lang/Exception
        //   520	532	810	java/lang/Exception
        //   540	546	810	java/lang/Exception
        //   802	807	810	java/lang/Exception
        //   868	873	810	java/lang/Exception
        //   881	890	810	java/lang/Exception
        //   900	912	810	java/lang/Exception
        //   920	934	810	java/lang/Exception
        //   942	950	810	java/lang/Exception
        //   958	966	810	java/lang/Exception
        //   974	985	810	java/lang/Exception
        //   1000	1008	810	java/lang/Exception
        //   1016	1030	810	java/lang/Exception
        //   97	102	1033	finally
        //   110	121	1033	finally
        //   129	134	1033	finally
        //   142	150	1033	finally
        //   158	167	1033	finally
        //   175	190	1033	finally
        //   198	215	1033	finally
        //   223	231	1033	finally
        //   239	247	1033	finally
        //   255	266	1033	finally
        //   281	289	1033	finally
        //   297	311	1033	finally
        //   319	326	1033	finally
        //   334	341	1033	finally
        //   353	361	1033	finally
        //   373	381	1033	finally
        //   389	404	1033	finally
        //   452	460	1033	finally
        //   468	512	1033	finally
        //   520	532	1033	finally
        //   540	546	1033	finally
        //   554	562	1033	finally
        //   571	585	1033	finally
        //   589	594	1033	finally
        //   598	606	1033	finally
        //   615	629	1033	finally
        //   633	638	1033	finally
        //   652	660	1033	finally
        //   664	678	1033	finally
        //   682	690	1033	finally
        //   694	705	1033	finally
        //   709	718	1033	finally
        //   722	732	1033	finally
        //   736	746	1033	finally
        //   750	760	1033	finally
        //   764	771	1033	finally
        //   775	789	1033	finally
        //   802	807	1033	finally
        //   816	821	1033	finally
        //   868	873	1033	finally
        //   881	890	1033	finally
        //   900	912	1033	finally
        //   920	934	1033	finally
        //   942	950	1033	finally
        //   958	966	1033	finally
        //   974	985	1033	finally
        //   1000	1008	1033	finally
        //   1016	1030	1033	finally
        //   16	70	1076	finally
        //   75	89	1076	finally
        //   16	70	1084	java/lang/Exception
        //   75	89	1084	java/lang/Exception
      }
    });
  }

  public void loadUnreadMessages()
  {
    this.storageQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList1;
        ArrayList localArrayList2;
        ArrayList localArrayList3;
        HashMap localHashMap1;
        Object localObject3;
        int j;
        long l1;
        int i;
        while (true)
        {
          int k;
          try
          {
            localArrayList1 = new ArrayList();
            localArrayList2 = new ArrayList();
            localArrayList3 = new ArrayList();
            localHashMap1 = new HashMap();
            localObject3 = MessagesStorage.this.database.queryFinalized("SELECT d.did, d.unread_count, s.flags FROM dialogs as d LEFT JOIN dialog_settings as s ON d.did = s.did WHERE d.unread_count != 0", new Object[0]);
            StringBuilder localStringBuilder = new StringBuilder();
            j = ConnectionsManager.getInstance().getCurrentTime();
            if (!((SQLiteCursor)localObject3).next())
              break;
            l1 = ((SQLiteCursor)localObject3).longValue(2);
            if ((1L & l1) != 0L)
            {
              i = 1;
              k = (int)(l1 >> 32);
              if ((!((SQLiteCursor)localObject3).isNull(2)) && (i != 0) && ((k == 0) || (k >= j)))
                continue;
              l1 = ((SQLiteCursor)localObject3).longValue(0);
              localHashMap1.put(Long.valueOf(l1), Integer.valueOf(((SQLiteCursor)localObject3).intValue(1)));
              if (localStringBuilder.length() == 0)
                continue;
              localStringBuilder.append(",");
              localStringBuilder.append(l1);
              i = (int)l1;
              k = (int)(l1 >> 32);
              if (i == 0)
                break label262;
              if (i >= 0)
                break label237;
              if (localArrayList2.contains(Integer.valueOf(-i)))
                continue;
              localArrayList2.add(Integer.valueOf(-i));
              continue;
            }
          }
          catch (Exception localException1)
          {
            FileLog.e(localException1);
            return;
          }
          i = 0;
          continue;
          label237: if (localArrayList1.contains(Integer.valueOf(i)))
            continue;
          localArrayList1.add(Integer.valueOf(i));
          continue;
          label262: if (localArrayList3.contains(Integer.valueOf(k)))
            continue;
          localArrayList3.add(Integer.valueOf(k));
        }
        ((SQLiteCursor)localObject3).dispose();
        Object localObject4 = new ArrayList();
        HashMap localHashMap2 = new HashMap();
        ArrayList localArrayList4 = new ArrayList();
        ArrayList localArrayList5 = new ArrayList();
        ArrayList localArrayList6 = new ArrayList();
        ArrayList localArrayList7 = new ArrayList();
        Object localObject2;
        if (localException1.length() > 0)
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT read_state, data, send_state, mid, date, uid, replydata FROM messages WHERE uid IN (" + localException1.toString() + ") AND out = 0 AND read_state IN(0,2) ORDER BY date DESC LIMIT 50", new Object[0]);
          while (localSQLiteCursor.next())
          {
            Object localObject1 = localSQLiteCursor.byteBufferValue(1);
            if (localObject1 == null)
              continue;
            TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
            ((NativeByteBuffer)localObject1).reuse();
            MessageObject.setUnreadFlags(localMessage, localSQLiteCursor.intValue(0));
            localMessage.id = localSQLiteCursor.intValue(3);
            localMessage.date = localSQLiteCursor.intValue(4);
            localMessage.dialog_id = localSQLiteCursor.longValue(5);
            localArrayList4.add(localMessage);
            i = (int)localMessage.dialog_id;
            MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList1, localArrayList2);
            localMessage.send_state = localSQLiteCursor.intValue(2);
            if (((localMessage.to_id.channel_id == 0) && (!MessageObject.isUnread(localMessage)) && (i != 0)) || (localMessage.id > 0))
              localMessage.send_state = 0;
            if ((i == 0) && (!localSQLiteCursor.isNull(5)))
              localMessage.random_id = localSQLiteCursor.longValue(5);
            try
            {
              if ((localMessage.reply_to_msg_id == 0) || ((!(localMessage.action instanceof TLRPC.TL_messageActionPinMessage)) && (!(localMessage.action instanceof TLRPC.TL_messageActionPaymentSent)) && (!(localMessage.action instanceof TLRPC.TL_messageActionGameScore))))
                continue;
              if (!localSQLiteCursor.isNull(6))
              {
                localObject1 = localSQLiteCursor.byteBufferValue(6);
                if (localObject1 != null)
                {
                  localMessage.replyMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject1, ((NativeByteBuffer)localObject1).readInt32(false), false);
                  ((NativeByteBuffer)localObject1).reuse();
                  if (localMessage.replyMessage != null)
                    MessagesStorage.addUsersAndChatsFromMessage(localMessage.replyMessage, localArrayList1, localArrayList2);
                }
              }
              if (localMessage.replyMessage != null)
                continue;
              long l2 = localMessage.reply_to_msg_id;
              l1 = l2;
              if (localMessage.to_id.channel_id != 0)
                l1 = l2 | localMessage.to_id.channel_id << 32;
              if (!((ArrayList)localObject4).contains(Long.valueOf(l1)))
                ((ArrayList)localObject4).add(Long.valueOf(l1));
              localObject3 = (ArrayList)localHashMap2.get(Integer.valueOf(localMessage.reply_to_msg_id));
              localObject1 = localObject3;
              if (localObject3 == null)
              {
                localObject1 = new ArrayList();
                localHashMap2.put(Integer.valueOf(localMessage.reply_to_msg_id), localObject1);
              }
              ((ArrayList)localObject1).add(localMessage);
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
            }
          }
          localSQLiteCursor.dispose();
          if (!((ArrayList)localObject4).isEmpty())
          {
            localObject2 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT data, mid, date, uid FROM messages WHERE mid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject4) }), new Object[0]);
            while (((SQLiteCursor)localObject2).next())
            {
              localObject4 = ((SQLiteCursor)localObject2).byteBufferValue(0);
              if (localObject4 == null)
                continue;
              localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
              ((NativeByteBuffer)localObject4).reuse();
              ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject2).intValue(1);
              ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject2).intValue(2);
              ((TLRPC.Message)localObject3).dialog_id = ((SQLiteCursor)localObject2).longValue(3);
              MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList1, localArrayList2);
              localObject4 = (ArrayList)localHashMap2.get(Integer.valueOf(((TLRPC.Message)localObject3).id));
              if (localObject4 == null)
                continue;
              i = 0;
              while (i < ((ArrayList)localObject4).size())
              {
                ((TLRPC.Message)((ArrayList)localObject4).get(i)).replyMessage = ((TLRPC.Message)localObject3);
                i += 1;
              }
            }
            ((SQLiteCursor)localObject2).dispose();
          }
          if (!localArrayList3.isEmpty())
            MessagesStorage.this.getEncryptedChatsInternal(TextUtils.join(",", localArrayList3), localArrayList7, localArrayList1);
          if (!localArrayList1.isEmpty())
            MessagesStorage.this.getUsersInternal(TextUtils.join(",", localArrayList1), localArrayList5);
          if (!localArrayList2.isEmpty())
          {
            MessagesStorage.this.getChatsInternal(TextUtils.join(",", localArrayList2), localArrayList6);
            i = 0;
          }
        }
        while (true)
        {
          if (i < localArrayList6.size())
          {
            localObject2 = (TLRPC.Chat)localArrayList6.get(i);
            if ((localObject2 == null) || ((!((TLRPC.Chat)localObject2).left) && (((TLRPC.Chat)localObject2).migrated_to == null)))
              break label1324;
            l1 = -((TLRPC.Chat)localObject2).id;
            MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = " + l1).stepThis().dispose();
            MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = 3 WHERE uid = %d AND mid > 0 AND read_state IN(0,2) AND out = 0", new Object[] { Long.valueOf(l1) })).stepThis().dispose();
            localArrayList6.remove(i);
            localHashMap1.remove(Long.valueOf(-((TLRPC.Chat)localObject2).id));
            j = 0;
          }
          while (true)
            if (j < localArrayList4.size())
            {
              if (((TLRPC.Message)localArrayList4.get(j)).dialog_id == -((TLRPC.Chat)localObject2).id)
              {
                localArrayList4.remove(j);
                j -= 1;
                break label1327;
                Collections.reverse(localArrayList4);
                AndroidUtilities.runOnUIThread(new Runnable(localHashMap1, localArrayList4, localArrayList5, localArrayList6, localArrayList7)
                {
                  public void run()
                  {
                    NotificationsController.getInstance().processLoadedUnreadMessages(this.val$pushDialogs, this.val$messages, this.val$users, this.val$chats, this.val$encryptedChats);
                  }
                });
                return;
              }
              else
              {
                break label1327;
                label1324: break;
              }
              label1327: j += 1;
              continue;
            }
            else
            {
              i -= 1;
            }
          i += 1;
        }
      }
    });
  }

  public void loadWebRecent(int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        ArrayList localArrayList;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT id, image_url, thumb_url, local_url, width, height, size, date, document FROM web_recent_v3 WHERE type = " + this.val$type + " ORDER BY date DESC", new Object[0]);
          localArrayList = new ArrayList();
          while (localSQLiteCursor.next())
          {
            MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
            localSearchImage.id = localSQLiteCursor.stringValue(0);
            localSearchImage.imageUrl = localSQLiteCursor.stringValue(1);
            localSearchImage.thumbUrl = localSQLiteCursor.stringValue(2);
            localSearchImage.localUrl = localSQLiteCursor.stringValue(3);
            localSearchImage.width = localSQLiteCursor.intValue(4);
            localSearchImage.height = localSQLiteCursor.intValue(5);
            localSearchImage.size = localSQLiteCursor.intValue(6);
            localSearchImage.date = localSQLiteCursor.intValue(7);
            if (!localSQLiteCursor.isNull(8))
            {
              NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(8);
              if (localNativeByteBuffer != null)
              {
                localSearchImage.document = TLRPC.Document.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
              }
            }
            localSearchImage.type = this.val$type;
            localArrayList.add(localSearchImage);
          }
        }
        catch (Throwable localThrowable)
        {
          FileLog.e(localThrowable);
          return;
        }
        localThrowable.dispose();
        AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentImagesDidLoaded, new Object[] { Integer.valueOf(MessagesStorage.13.this.val$type), this.val$arrayList });
          }
        });
      }
    });
  }

  public void markMessageAsSendError(TLRPC.Message paramMessage)
  {
    this.storageQueue.postRunnable(new Runnable(paramMessage)
    {
      public void run()
      {
        try
        {
          long l2 = this.val$message.id;
          long l1 = l2;
          if (this.val$message.to_id.channel_id != 0)
            l1 = l2 | this.val$message.to_id.channel_id << 32;
          MessagesStorage.this.database.executeFast("UPDATE messages SET send_state = 2 WHERE mid = " + l1).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public ArrayList<Long> markMessagesAsDeleted(ArrayList<Integer> paramArrayList, boolean paramBoolean, int paramInt)
  {
    if (paramArrayList.isEmpty())
      return null;
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable(paramArrayList, paramInt)
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsDeletedInternal(this.val$messages, this.val$channelId);
        }
      });
      return null;
    }
    return markMessagesAsDeletedInternal(paramArrayList, paramInt);
  }

  public void markMessagesAsDeletedByRandoms(ArrayList<Long> paramArrayList)
  {
    if (paramArrayList.isEmpty())
      return;
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        ArrayList localArrayList;
        do
        {
          try
          {
            Object localObject = TextUtils.join(",", this.val$messages);
            localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM randoms WHERE random_id IN(%s)", new Object[] { localObject }), new Object[0]);
            localArrayList = new ArrayList();
            while (((SQLiteCursor)localObject).next())
              localArrayList.add(Integer.valueOf(((SQLiteCursor)localObject).intValue(0)));
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          localException.dispose();
        }
        while (localArrayList.isEmpty());
        AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messagesDeleted, new Object[] { this.val$mids, Integer.valueOf(0) });
          }
        });
        MessagesStorage.getInstance().updateDialogsWithReadMessagesInternal(localArrayList, null, null);
        MessagesStorage.getInstance().markMessagesAsDeletedInternal(localArrayList, 0);
        MessagesStorage.getInstance().updateDialogsWithDeletedMessagesInternal(localArrayList, null, 0);
      }
    });
  }

  public void markMessagesAsRead(SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2, HashMap<Integer, Integer> paramHashMap, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable(paramSparseArray1, paramSparseArray2, paramHashMap)
      {
        public void run()
        {
          MessagesStorage.this.markMessagesAsReadInternal(this.val$inbox, this.val$outbox, this.val$encryptedMessages);
        }
      });
      return;
    }
    markMessagesAsReadInternal(paramSparseArray1, paramSparseArray2, paramHashMap);
  }

  public void markMessagesContentAsRead(ArrayList<Long> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE messages SET read_state = read_state | 2 WHERE mid IN (%s)", new Object[] { TextUtils.join(",", this.val$mids) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void openDatabase(boolean paramBoolean)
  {
    this.cacheFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
    if (!this.cacheFile.exists());
    for (int i = 1; ; i = 0)
      while (true)
      {
        try
        {
          this.database = new SQLiteDatabase(this.cacheFile.getPath());
          this.database.executeFast("PRAGMA secure_delete = ON").stepThis().dispose();
          this.database.executeFast("PRAGMA temp_store = 1").stepThis().dispose();
          if (i == 0)
            continue;
          FileLog.e("create new database");
          this.database.executeFast("CREATE TABLE messages_holes(uid INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, start));").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_messages_holes ON messages_holes(uid, end);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE media_holes_v2(uid INTEGER, type INTEGER, start INTEGER, end INTEGER, PRIMARY KEY(uid, type, start));").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_end_media_holes_v2 ON media_holes_v2(uid, type, end);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE messages(mid INTEGER PRIMARY KEY, uid INTEGER, read_state INTEGER, send_state INTEGER, date INTEGER, data BLOB, out INTEGER, ttl INTEGER, media INTEGER, replydata BLOB, imp INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_idx_messages ON messages(uid, mid);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_date_mid_idx_messages ON messages(uid, date, mid);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_out_idx_messages ON messages(mid, out);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS task_idx_messages ON messages(uid, out, read_state, ttl, date, send_state);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS send_state_idx_messages ON messages(mid, send_state, date) WHERE mid < 0 AND send_state = 1;").stepThis().dispose();
          this.database.executeFast("CREATE TABLE download_queue(uid INTEGER, type INTEGER, date INTEGER, data BLOB, PRIMARY KEY (uid, type));").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS type_date_idx_download_queue ON download_queue(type, date);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE user_phones_v6(uid INTEGER, phone TEXT, sphone TEXT, deleted INTEGER, PRIMARY KEY (uid, phone))").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS sphone_deleted_idx_user_phones ON user_phones_v6(sphone, deleted);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE dialogs(did INTEGER PRIMARY KEY, date INTEGER, unread_count INTEGER, last_mid INTEGER, inbox_max INTEGER, outbox_max INTEGER, last_mid_i INTEGER, unread_count_i INTEGER, pts INTEGER, date_i INTEGER, pinned INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_dialogs ON dialogs(date);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_idx_dialogs ON dialogs(last_mid);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_idx_dialogs ON dialogs(unread_count);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS last_mid_i_idx_dialogs ON dialogs(last_mid_i);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS unread_count_i_idx_dialogs ON dialogs(unread_count_i);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE randoms(random_id INTEGER, mid INTEGER, PRIMARY KEY (random_id, mid))").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS mid_idx_randoms ON randoms(mid);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE enc_tasks_v2(mid INTEGER PRIMARY KEY, date INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS date_idx_enc_tasks_v2 ON enc_tasks_v2(date);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE messages_seq(mid INTEGER PRIMARY KEY, seq_in INTEGER, seq_out INTEGER);").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS seq_idx_messages_seq ON messages_seq(seq_in, seq_out);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
          this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE media_v2(mid INTEGER PRIMARY KEY, uid INTEGER, date INTEGER, type INTEGER, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS uid_mid_type_date_idx_media ON media_v2(uid, mid, type, date);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE bot_keyboard(uid INTEGER PRIMARY KEY, mid INTEGER, info BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS bot_keyboard_idx_mid ON bot_keyboard(mid);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE chat_settings_v2(uid INTEGER PRIMARY KEY, info BLOB, pinned INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_settings_pinned_idx ON chat_settings_v2(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
          this.database.executeFast("CREATE TABLE chat_pinned(uid INTEGER PRIMARY KEY, pinned INTEGER, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_pinned_mid_idx ON chat_pinned(uid, pinned) WHERE pinned != 0;").stepThis().dispose();
          this.database.executeFast("CREATE TABLE chat_hints(did INTEGER, type INTEGER, rating REAL, date INTEGER, PRIMARY KEY(did, type))").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS chat_hints_rating_idx ON chat_hints(rating);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE botcache(id TEXT PRIMARY KEY, date INTEGER, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE INDEX IF NOT EXISTS botcache_date_idx ON botcache(date);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE users_data(uid INTEGER PRIMARY KEY, about TEXT)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE users(uid INTEGER PRIMARY KEY, name TEXT, status INTEGER, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE chats(uid INTEGER PRIMARY KEY, name TEXT, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE enc_chats(uid INTEGER PRIMARY KEY, user INTEGER, name TEXT, data BLOB, g BLOB, authkey BLOB, ttl INTEGER, layer INTEGER, seq_in INTEGER, seq_out INTEGER, use_count INTEGER, exchange_id INTEGER, key_date INTEGER, fprint INTEGER, fauthkey BLOB, khash BLOB, in_seq_no INTEGER, admin_id INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE channel_users_v2(did INTEGER, uid INTEGER, date INTEGER, data BLOB, PRIMARY KEY(did, uid))").stepThis().dispose();
          this.database.executeFast("CREATE TABLE contacts(uid INTEGER PRIMARY KEY, mutual INTEGER)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE wallpapers(uid INTEGER PRIMARY KEY, data BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE user_photos(uid INTEGER, id INTEGER, data BLOB, PRIMARY KEY (uid, id))").stepThis().dispose();
          this.database.executeFast("CREATE TABLE blocked_users(uid INTEGER PRIMARY KEY)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE dialog_settings(did INTEGER PRIMARY KEY, flags INTEGER);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE web_recent_v3(id TEXT, type INTEGER, image_url TEXT, thumb_url TEXT, local_url TEXT, width INTEGER, height INTEGER, size INTEGER, date INTEGER, document BLOB, PRIMARY KEY (id, type));").stepThis().dispose();
          this.database.executeFast("CREATE TABLE stickers_v2(id INTEGER PRIMARY KEY, data BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE stickers_featured(id INTEGER PRIMARY KEY, data BLOB, unread BLOB, date INTEGER, hash TEXT);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE hashtag_recent_v2(id TEXT PRIMARY KEY, date INTEGER);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE webpage_pending(id INTEGER, mid INTEGER, PRIMARY KEY (id, mid));").stepThis().dispose();
          this.database.executeFast("CREATE TABLE user_contacts_v6(uid INTEGER PRIMARY KEY, fname TEXT, sname TEXT)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE sent_files_v2(uid TEXT, type INTEGER, data BLOB, PRIMARY KEY (uid, type))").stepThis().dispose();
          this.database.executeFast("CREATE TABLE search_recent(did INTEGER PRIMARY KEY, date INTEGER);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE media_counts_v2(uid INTEGER, type INTEGER, count INTEGER, PRIMARY KEY(uid, type))").stepThis().dispose();
          this.database.executeFast("CREATE TABLE keyvalue(id TEXT PRIMARY KEY, value TEXT)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE bot_info(uid INTEGER PRIMARY KEY, info BLOB)").stepThis().dispose();
          this.database.executeFast("CREATE TABLE pending_tasks(id INTEGER PRIMARY KEY, data BLOB);").stepThis().dispose();
          this.database.executeFast("CREATE TABLE requested_holes(uid INTEGER, seq_out_start INTEGER, seq_out_end INTEGER, PRIMARY KEY (uid, seq_out_start, seq_out_end));").stepThis().dispose();
          this.database.executeFast("PRAGMA user_version = 41").stepThis().dispose();
          loadUnreadMessages();
          loadPendingTasks();
          return;
          i = this.database.executeInt("PRAGMA user_version", new Object[0]).intValue();
          FileLog.e("current db version = " + i);
          if (i == 0)
            throw new Exception("malformed");
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          if ((!paramBoolean) || (!localException1.getMessage().contains("malformed")))
            continue;
          cleanupInternal();
          openDatabase(false);
          continue;
        }
        try
        {
          SQLiteCursor localSQLiteCursor = this.database.queryFinalized("SELECT seq, pts, date, qts, lsv, sg, pbytes FROM params WHERE id = 1", new Object[0]);
          if (localSQLiteCursor.next())
          {
            lastSeqValue = localSQLiteCursor.intValue(0);
            lastPtsValue = localSQLiteCursor.intValue(1);
            lastDateValue = localSQLiteCursor.intValue(2);
            lastQtsValue = localSQLiteCursor.intValue(3);
            lastSecretVersion = localSQLiteCursor.intValue(4);
            secretG = localSQLiteCursor.intValue(5);
            if (!localSQLiteCursor.isNull(6))
              break label1313;
            secretPBytes = null;
          }
          while (true)
          {
            localSQLiteCursor.dispose();
            if (i >= 41)
              break;
            updateDbToLastVersion(i);
            break;
            label1313: secretPBytes = localSQLiteCursor.byteArrayValue(6);
            if ((secretPBytes == null) || (secretPBytes.length != 1))
              continue;
            secretPBytes = null;
          }
        }
        catch (Exception localException3)
        {
          while (true)
          {
            FileLog.e(localException2);
            try
            {
              this.database.executeFast("CREATE TABLE IF NOT EXISTS params(id INTEGER PRIMARY KEY, seq INTEGER, pts INTEGER, date INTEGER, qts INTEGER, lsv INTEGER, sg INTEGER, pbytes BLOB)").stepThis().dispose();
              this.database.executeFast("INSERT INTO params VALUES(1, 0, 0, 0, 0, 0, 0, NULL)").stepThis().dispose();
            }
            catch (Exception localException3)
            {
              FileLog.e(localException3);
            }
          }
        }
      }
  }

  public void overwriteChannel(int paramInt1, TLRPC.TL_updates_channelDifferenceTooLong paramTL_updates_channelDifferenceTooLong, int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2, paramTL_updates_channelDifferenceTooLong)
    {
      public void run()
      {
        boolean bool = false;
        while (true)
        {
          try
          {
            long l = -this.val$channel_id;
            Object localObject = MessagesStorage.this.database.queryFinalized("SELECT pts, pinned FROM dialogs WHERE did = " + l, new Object[0]);
            if (((SQLiteCursor)localObject).next())
              continue;
            if (this.val$newDialogType != 0)
            {
              i = 0;
              j = 1;
              ((SQLiteCursor)localObject).dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages WHERE uid = " + l).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM bot_keyboard WHERE uid = " + l).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + l).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_v2 WHERE uid = " + l).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM messages_holes WHERE uid = " + l).stepThis().dispose();
              MessagesStorage.this.database.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + l).stepThis().dispose();
              BotQuery.clearBotKeyboard(l, null);
              localObject = new TLRPC.TL_messages_dialogs();
              ((TLRPC.TL_messages_dialogs)localObject).chats.addAll(this.val$difference.chats);
              ((TLRPC.TL_messages_dialogs)localObject).users.addAll(this.val$difference.users);
              ((TLRPC.TL_messages_dialogs)localObject).messages.addAll(this.val$difference.messages);
              TLRPC.TL_dialog localTL_dialog = new TLRPC.TL_dialog();
              localTL_dialog.id = l;
              localTL_dialog.flags = 1;
              localTL_dialog.peer = new TLRPC.TL_peerChannel();
              localTL_dialog.peer.channel_id = this.val$channel_id;
              localTL_dialog.top_message = this.val$difference.top_message;
              localTL_dialog.read_inbox_max_id = this.val$difference.read_inbox_max_id;
              localTL_dialog.read_outbox_max_id = this.val$difference.read_outbox_max_id;
              localTL_dialog.unread_count = this.val$difference.unread_count;
              localTL_dialog.notify_settings = null;
              if (i == 0)
                continue;
              bool = true;
              localTL_dialog.pinned = bool;
              localTL_dialog.pinnedNum = i;
              localTL_dialog.pts = this.val$difference.pts;
              ((TLRPC.TL_messages_dialogs)localObject).dialogs.add(localTL_dialog);
              MessagesStorage.this.putDialogsInternal((TLRPC.messages_Dialogs)localObject, false);
              MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), null, false, this.val$channel_id);
              AndroidUtilities.runOnUIThread(new Runnable(l)
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.removeAllMessagesFromDialog, new Object[] { Long.valueOf(this.val$did), Boolean.valueOf(true) });
                }
              });
              if (j == 0)
                break;
              if (this.val$newDialogType != 1)
                continue;
              MessagesController.getInstance().checkChannelInviter(this.val$channel_id);
              return;
              i = ((SQLiteCursor)localObject).intValue(1);
              j = 0;
              continue;
              MessagesController.getInstance().generateJoinMessage(this.val$channel_id, false);
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          int i = 0;
          int j = 0;
        }
      }
    });
  }

  public void processPendingRead(long paramLong1, long paramLong2, int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramLong1, paramLong2, paramInt)
    {
      public void run()
      {
        int i = 0;
        try
        {
          MessagesStorage.this.database.beginTransaction();
          Object localObject;
          if ((int)this.val$dialog_id != 0)
          {
            localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND mid <= ? AND read_state IN(0,2) AND out = 0");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, this.val$dialog_id);
            ((SQLitePreparedStatement)localObject).bindLong(2, this.val$max_id);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
          }
          while (true)
          {
            localObject = MessagesStorage.this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]);
            if (((SQLiteCursor)localObject).next())
              i = ((SQLiteCursor)localObject).intValue(0);
            ((SQLiteCursor)localObject).dispose();
            i = Math.max(i, (int)this.val$max_id);
            localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET unread_count = 0, unread_count_i = 0, inbox_max = ? WHERE did = ?");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindInteger(1, i);
            ((SQLitePreparedStatement)localObject).bindLong(2, this.val$dialog_id);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
            MessagesStorage.this.database.commitTransaction();
            return;
            localObject = MessagesStorage.this.database.executeFast("UPDATE messages SET read_state = read_state | 1 WHERE uid = ? AND date <= ? AND read_state IN(0,2) AND out = 0");
            ((SQLitePreparedStatement)localObject).requery();
            ((SQLitePreparedStatement)localObject).bindLong(1, this.val$dialog_id);
            ((SQLitePreparedStatement)localObject).bindInteger(2, this.val$max_date);
            ((SQLitePreparedStatement)localObject).step();
            ((SQLitePreparedStatement)localObject).dispose();
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void putBlockedUsers(ArrayList<Integer> paramArrayList, boolean paramBoolean)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    this.storageQueue.postRunnable(new Runnable(paramBoolean, paramArrayList)
    {
      public void run()
      {
        try
        {
          if (this.val$replace)
            MessagesStorage.this.database.executeFast("DELETE FROM blocked_users WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO blocked_users VALUES(?)");
          Iterator localIterator = this.val$ids.iterator();
          while (localIterator.hasNext())
          {
            Integer localInteger = (Integer)localIterator.next();
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, localInteger.intValue());
            localSQLitePreparedStatement.step();
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localException.dispose();
        MessagesStorage.this.database.commitTransaction();
      }
    });
  }

  public void putCachedPhoneBook(HashMap<Integer, ContactsController.Contact> paramHashMap)
  {
    this.storageQueue.postRunnable(new Runnable(paramHashMap)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO user_contacts_v6 VALUES(?, ?, ?)");
          SQLitePreparedStatement localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO user_phones_v6 VALUES(?, ?, ?, ?)");
          Iterator localIterator = this.val$contactHashMap.entrySet().iterator();
          while (localIterator.hasNext())
          {
            ContactsController.Contact localContact = (ContactsController.Contact)((Map.Entry)localIterator.next()).getValue();
            if ((localContact.phones.isEmpty()) || (localContact.shortPhones.isEmpty()))
              continue;
            localSQLitePreparedStatement1.requery();
            localSQLitePreparedStatement1.bindInteger(1, localContact.id);
            localSQLitePreparedStatement1.bindString(2, localContact.first_name);
            localSQLitePreparedStatement1.bindString(3, localContact.last_name);
            localSQLitePreparedStatement1.step();
            int i = 0;
            while (i < localContact.phones.size())
            {
              localSQLitePreparedStatement2.requery();
              localSQLitePreparedStatement2.bindInteger(1, localContact.id);
              localSQLitePreparedStatement2.bindString(2, (String)localContact.phones.get(i));
              localSQLitePreparedStatement2.bindString(3, (String)localContact.shortPhones.get(i));
              localSQLitePreparedStatement2.bindInteger(4, ((Integer)localContact.phoneDeleted.get(i)).intValue());
              localSQLitePreparedStatement2.step();
              i += 1;
            }
          }
          localSQLitePreparedStatement1.dispose();
          localSQLitePreparedStatement2.dispose();
          MessagesStorage.this.database.commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void putChannelViews(SparseArray<SparseIntArray> paramSparseArray, boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable(paramSparseArray, paramBoolean)
    {
      public void run()
      {
        while (true)
        {
          int i;
          try
          {
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE messages SET media = max((SELECT media FROM messages WHERE mid = ?), ?) WHERE mid = ?");
            i = 0;
            if (i >= this.val$channelViews.size())
              continue;
            int k = this.val$channelViews.keyAt(i);
            SparseIntArray localSparseIntArray = (SparseIntArray)this.val$channelViews.get(k);
            int j = 0;
            if (j < localSparseIntArray.size())
            {
              int m = localSparseIntArray.get(localSparseIntArray.keyAt(j));
              long l2 = localSparseIntArray.keyAt(j);
              long l1 = l2;
              if (!this.val$isChannel)
                continue;
              l1 = l2 | -k << 32;
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindLong(1, l1);
              localSQLitePreparedStatement.bindInteger(2, m);
              localSQLitePreparedStatement.bindLong(3, l1);
              localSQLitePreparedStatement.step();
              j += 1;
              continue;
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          i += 1;
        }
      }
    });
  }

  public void putContacts(ArrayList<TLRPC.TL_contact> paramArrayList, boolean paramBoolean)
  {
    if (paramArrayList.isEmpty())
      return;
    paramArrayList = new ArrayList(paramArrayList);
    this.storageQueue.postRunnable(new Runnable(paramBoolean, paramArrayList)
    {
      public void run()
      {
        while (true)
        {
          try
          {
            if (!this.val$deleteAll)
              continue;
            MessagesStorage.this.database.executeFast("DELETE FROM contacts WHERE 1").stepThis().dispose();
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO contacts VALUES(?, ?)");
            int i = 0;
            if (i >= this.val$contactsCopy.size())
              continue;
            TLRPC.TL_contact localTL_contact = (TLRPC.TL_contact)this.val$contactsCopy.get(i);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, localTL_contact.user_id);
            if (localTL_contact.mutual)
            {
              j = 1;
              localSQLitePreparedStatement.bindInteger(2, j);
              localSQLitePreparedStatement.step();
              i += 1;
              continue;
              localSQLitePreparedStatement.dispose();
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          int j = 0;
        }
      }
    });
  }

  public void putDialogPhotos(int paramInt, TLRPC.photos_Photos paramphotos_Photos)
  {
    if ((paramphotos_Photos == null) || (paramphotos_Photos.photos.isEmpty()))
      return;
    this.storageQueue.postRunnable(new Runnable(paramphotos_Photos, paramInt)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO user_photos VALUES(?, ?, ?)");
          Iterator localIterator = this.val$photos.photos.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.Photo localPhoto = (TLRPC.Photo)localIterator.next();
            if ((localPhoto instanceof TLRPC.TL_photoEmpty))
              continue;
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localPhoto.getObjectSize());
            localPhoto.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindInteger(1, this.val$did);
            localSQLitePreparedStatement.bindLong(2, localPhoto.id);
            localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            localNativeByteBuffer.reuse();
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localException.dispose();
      }
    });
  }

  public void putDialogs(TLRPC.messages_Dialogs parammessages_Dialogs, boolean paramBoolean)
  {
    if (parammessages_Dialogs.dialogs.isEmpty())
      return;
    this.storageQueue.postRunnable(new Runnable(parammessages_Dialogs, paramBoolean)
    {
      public void run()
      {
        MessagesStorage.this.putDialogsInternal(this.val$dialogs, this.val$check);
        MessagesStorage.this.loadUnreadMessages();
      }
    });
  }

  public void putEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat, TLRPC.User paramUser, TLRPC.TL_dialog paramTL_dialog)
  {
    if (paramEncryptedChat == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramEncryptedChat, paramUser, paramTL_dialog)
    {
      public void run()
      {
        int j = 1;
        try
        {
          if (((this.val$chat.key_hash == null) || (this.val$chat.key_hash.length < 16)) && (this.val$chat.auth_key != null))
            this.val$chat.key_hash = AndroidUtilities.calcAuthKeyHash(this.val$chat.auth_key);
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO enc_chats VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer1 = new NativeByteBuffer(this.val$chat.getObjectSize());
          NativeByteBuffer localNativeByteBuffer2;
          label129: NativeByteBuffer localNativeByteBuffer3;
          if (this.val$chat.a_or_b != null)
          {
            i = this.val$chat.a_or_b.length;
            localNativeByteBuffer2 = new NativeByteBuffer(i);
            if (this.val$chat.auth_key == null)
              break label704;
            i = this.val$chat.auth_key.length;
            localNativeByteBuffer3 = new NativeByteBuffer(i);
            if (this.val$chat.future_auth_key == null)
              break label709;
          }
          label704: label709: for (int i = this.val$chat.future_auth_key.length; ; i = 1)
          {
            NativeByteBuffer localNativeByteBuffer4 = new NativeByteBuffer(i);
            i = j;
            if (this.val$chat.key_hash != null)
              i = this.val$chat.key_hash.length;
            NativeByteBuffer localNativeByteBuffer5 = new NativeByteBuffer(i);
            this.val$chat.serializeToStream(localNativeByteBuffer1);
            localSQLitePreparedStatement.bindInteger(1, this.val$chat.id);
            localSQLitePreparedStatement.bindInteger(2, this.val$user.id);
            localSQLitePreparedStatement.bindString(3, MessagesStorage.this.formatUserSearchName(this.val$user));
            localSQLitePreparedStatement.bindByteBuffer(4, localNativeByteBuffer1);
            if (this.val$chat.a_or_b != null)
              localNativeByteBuffer2.writeBytes(this.val$chat.a_or_b);
            if (this.val$chat.auth_key != null)
              localNativeByteBuffer3.writeBytes(this.val$chat.auth_key);
            if (this.val$chat.future_auth_key != null)
              localNativeByteBuffer4.writeBytes(this.val$chat.future_auth_key);
            if (this.val$chat.key_hash != null)
              localNativeByteBuffer5.writeBytes(this.val$chat.key_hash);
            localSQLitePreparedStatement.bindByteBuffer(5, localNativeByteBuffer2);
            localSQLitePreparedStatement.bindByteBuffer(6, localNativeByteBuffer3);
            localSQLitePreparedStatement.bindInteger(7, this.val$chat.ttl);
            localSQLitePreparedStatement.bindInteger(8, this.val$chat.layer);
            localSQLitePreparedStatement.bindInteger(9, this.val$chat.seq_in);
            localSQLitePreparedStatement.bindInteger(10, this.val$chat.seq_out);
            localSQLitePreparedStatement.bindInteger(11, this.val$chat.key_use_count_in << 16 | this.val$chat.key_use_count_out);
            localSQLitePreparedStatement.bindLong(12, this.val$chat.exchange_id);
            localSQLitePreparedStatement.bindInteger(13, this.val$chat.key_create_date);
            localSQLitePreparedStatement.bindLong(14, this.val$chat.future_key_fingerprint);
            localSQLitePreparedStatement.bindByteBuffer(15, localNativeByteBuffer4);
            localSQLitePreparedStatement.bindByteBuffer(16, localNativeByteBuffer5);
            localSQLitePreparedStatement.bindInteger(17, this.val$chat.in_seq_no);
            localSQLitePreparedStatement.bindInteger(18, this.val$chat.admin_id);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            localNativeByteBuffer1.reuse();
            localNativeByteBuffer2.reuse();
            localNativeByteBuffer3.reuse();
            localNativeByteBuffer4.reuse();
            localNativeByteBuffer5.reuse();
            if (this.val$dialog != null)
            {
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              localSQLitePreparedStatement.bindLong(1, this.val$dialog.id);
              localSQLitePreparedStatement.bindInteger(2, this.val$dialog.last_message_date);
              localSQLitePreparedStatement.bindInteger(3, this.val$dialog.unread_count);
              localSQLitePreparedStatement.bindInteger(4, this.val$dialog.top_message);
              localSQLitePreparedStatement.bindInteger(5, this.val$dialog.read_inbox_max_id);
              localSQLitePreparedStatement.bindInteger(6, this.val$dialog.read_outbox_max_id);
              localSQLitePreparedStatement.bindInteger(7, 0);
              localSQLitePreparedStatement.bindInteger(8, 0);
              localSQLitePreparedStatement.bindInteger(9, this.val$dialog.pts);
              localSQLitePreparedStatement.bindInteger(10, 0);
              localSQLitePreparedStatement.bindInteger(11, this.val$dialog.pinnedNum);
              localSQLitePreparedStatement.step();
              localSQLitePreparedStatement.dispose();
            }
            return;
            i = 1;
            break;
            i = 1;
            break label129;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void putMessages(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt)
  {
    putMessages(paramArrayList, paramBoolean1, paramBoolean2, paramBoolean3, paramInt, false);
  }

  public void putMessages(ArrayList<TLRPC.Message> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, int paramInt, boolean paramBoolean4)
  {
    if (paramArrayList.size() == 0)
      return;
    if (paramBoolean2)
    {
      this.storageQueue.postRunnable(new Runnable(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4)
      {
        public void run()
        {
          MessagesStorage.this.putMessagesInternal(this.val$messages, this.val$withTransaction, this.val$doNotUpdateDialogDate, this.val$downloadMask, this.val$ifNoLastMessage);
        }
      });
      return;
    }
    putMessagesInternal(paramArrayList, paramBoolean1, paramBoolean3, paramInt, paramBoolean4);
  }

  public void putMessages(TLRPC.messages_Messages parammessages_Messages, long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable(parammessages_Messages, paramInt1, paramLong, paramInt2, paramBoolean)
    {
      public void run()
      {
        SQLitePreparedStatement localSQLitePreparedStatement1;
        SQLitePreparedStatement localSQLitePreparedStatement2;
        Object localObject3;
        int j;
        TLRPC.Message localMessage1;
        label228: long l1;
        do
          try
          {
            if (this.val$messages.messages.isEmpty())
            {
              if (this.val$load_type != 0)
                break label1262;
              MessagesStorage.this.doneHolesInTable("messages_holes", this.val$dialog_id, this.val$max_id);
              MessagesStorage.this.doneHolesInMedia(this.val$dialog_id, this.val$max_id, -1);
              return;
            }
            MessagesStorage.this.database.beginTransaction();
            if (this.val$load_type == 0)
            {
              i = ((TLRPC.Message)this.val$messages.messages.get(this.val$messages.messages.size() - 1)).id;
              MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, i, this.val$max_id);
              MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, i, this.val$max_id, -1);
            }
            while (true)
            {
              int m = this.val$messages.messages.size();
              localSQLitePreparedStatement1 = MessagesStorage.this.database.executeFast("REPLACE INTO messages VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?)");
              localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
              Object localObject1 = null;
              localObject3 = null;
              i = 0;
              j = 0;
              if (j >= m)
                break label1152;
              localMessage1 = (TLRPC.Message)this.val$messages.messages.get(j);
              long l2 = localMessage1.id;
              if (i != 0)
                break label1259;
              i = localMessage1.to_id.channel_id;
              l1 = l2;
              if (localMessage1.to_id.channel_id != 0)
                l1 = l2 | i << 32;
              if (this.val$load_type != -2)
                break label589;
              localObject4 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data, ttl FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1) }), new Object[0]);
              boolean bool = ((SQLiteCursor)localObject4).next();
              if (bool)
              {
                NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject4).byteBufferValue(1);
                if (localNativeByteBuffer != null)
                {
                  TLRPC.Message localMessage2 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  localNativeByteBuffer.reuse();
                  if (localMessage2 != null)
                  {
                    localMessage1.attachPath = localMessage2.attachPath;
                    localMessage1.ttl = ((SQLiteCursor)localObject4).intValue(2);
                  }
                }
              }
              ((SQLiteCursor)localObject4).dispose();
              if (bool)
                break label589;
              localObject4 = localObject1;
              localObject1 = localObject3;
              localObject3 = localObject4;
              break label1263;
              if (this.val$load_type != 1)
                break;
              i = ((TLRPC.Message)this.val$messages.messages.get(0)).id;
              MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, this.val$max_id, i);
              MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, this.val$max_id, i, -1);
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
        while ((this.val$load_type != 3) && (this.val$load_type != 2) && (this.val$load_type != 4));
        if ((this.val$max_id == 0) && (this.val$load_type != 4));
        for (int i = 2147483647; ; i = ((TLRPC.Message)this.val$messages.messages.get(0)).id)
        {
          j = ((TLRPC.Message)this.val$messages.messages.get(this.val$messages.messages.size() - 1)).id;
          MessagesStorage.this.closeHolesInTable("messages_holes", this.val$dialog_id, j, i);
          MessagesStorage.this.closeHolesInMedia(this.val$dialog_id, j, i, -1);
          break;
        }
        label589: if ((j == 0) && (this.val$createDialog))
        {
          k = 0;
          localObject4 = MessagesStorage.this.database.queryFinalized("SELECT pinned FROM dialogs WHERE did = " + this.val$dialog_id, new Object[0]);
          if (((SQLiteCursor)localObject4).next())
            k = ((SQLiteCursor)localObject4).intValue(0);
          ((SQLiteCursor)localObject4).dispose();
          localObject4 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
          ((SQLitePreparedStatement)localObject4).bindLong(1, this.val$dialog_id);
          ((SQLitePreparedStatement)localObject4).bindInteger(2, localMessage1.date);
          ((SQLitePreparedStatement)localObject4).bindInteger(3, 0);
          ((SQLitePreparedStatement)localObject4).bindLong(4, l1);
          ((SQLitePreparedStatement)localObject4).bindInteger(5, localMessage1.id);
          ((SQLitePreparedStatement)localObject4).bindInteger(6, 0);
          ((SQLitePreparedStatement)localObject4).bindLong(7, l1);
          ((SQLitePreparedStatement)localObject4).bindInteger(8, localMessage1.ttl);
          ((SQLitePreparedStatement)localObject4).bindInteger(9, this.val$messages.pts);
          ((SQLitePreparedStatement)localObject4).bindInteger(10, localMessage1.date);
          ((SQLitePreparedStatement)localObject4).bindInteger(11, k);
          ((SQLitePreparedStatement)localObject4).step();
          ((SQLitePreparedStatement)localObject4).dispose();
        }
        MessagesStorage.this.fixUnsupportedMedia(localMessage1);
        localSQLitePreparedStatement1.requery();
        Object localObject4 = new NativeByteBuffer(localMessage1.getObjectSize());
        localMessage1.serializeToStream((AbstractSerializedData)localObject4);
        localSQLitePreparedStatement1.bindLong(1, l1);
        localSQLitePreparedStatement1.bindLong(2, this.val$dialog_id);
        localSQLitePreparedStatement1.bindInteger(3, MessageObject.getUnreadFlags(localMessage1));
        localSQLitePreparedStatement1.bindInteger(4, localMessage1.send_state);
        localSQLitePreparedStatement1.bindInteger(5, localMessage1.date);
        localSQLitePreparedStatement1.bindByteBuffer(6, (NativeByteBuffer)localObject4);
        if (MessageObject.isOut(localMessage1));
        for (int k = 1; ; k = 0)
        {
          localSQLitePreparedStatement1.bindInteger(7, k);
          localSQLitePreparedStatement1.bindInteger(8, 0);
          if ((localMessage1.flags & 0x400) != 0)
            localSQLitePreparedStatement1.bindInteger(9, localMessage1.views);
          while (true)
          {
            localSQLitePreparedStatement1.bindInteger(10, 0);
            localSQLitePreparedStatement1.step();
            if (SharedMediaQuery.canAddMessageToMedia(localMessage1))
            {
              localSQLitePreparedStatement2.requery();
              localSQLitePreparedStatement2.bindLong(1, l1);
              localSQLitePreparedStatement2.bindLong(2, this.val$dialog_id);
              localSQLitePreparedStatement2.bindInteger(3, localMessage1.date);
              localSQLitePreparedStatement2.bindInteger(4, SharedMediaQuery.getMediaType(localMessage1));
              localSQLitePreparedStatement2.bindByteBuffer(5, (NativeByteBuffer)localObject4);
              localSQLitePreparedStatement2.step();
            }
            ((NativeByteBuffer)localObject4).reuse();
            localObject4 = localException;
            if ((localMessage1.media instanceof TLRPC.TL_messageMediaWebPage))
            {
              localObject4 = localException;
              if (localException == null)
                localObject4 = MessagesStorage.this.database.executeFast("REPLACE INTO webpage_pending VALUES(?, ?)");
              ((SQLitePreparedStatement)localObject4).requery();
              ((SQLitePreparedStatement)localObject4).bindLong(1, localMessage1.media.webpage.id);
              ((SQLitePreparedStatement)localObject4).bindLong(2, l1);
              ((SQLitePreparedStatement)localObject4).step();
            }
            if ((this.val$load_type != 0) || (!MessagesStorage.this.isValidKeyboardToSave(localMessage1)))
              break;
            if (localObject3 == null)
              break label1282;
            if (localObject3.id >= localMessage1.id)
              break;
            break label1282;
            localSQLitePreparedStatement1.bindInteger(9, 0);
            continue;
            label1152: localSQLitePreparedStatement1.dispose();
            localSQLitePreparedStatement2.dispose();
            if (localException != null)
              localException.dispose();
            if (localObject3 != null)
              BotQuery.putBotKeyboard(this.val$dialog_id, localObject3);
            MessagesStorage.this.putUsersInternal(this.val$messages.users);
            MessagesStorage.this.putChatsInternal(this.val$messages.chats);
            MessagesStorage.this.database.commitTransaction();
            if (!this.val$createDialog)
              break label1262;
            MessagesStorage.getInstance().updateDialogsWithDeletedMessages(new ArrayList(), null, false, i);
            return;
          }
          Object localObject2 = localObject3;
          localObject3 = localObject4;
          break label1263;
          label1259: break label228;
          label1262: return;
          while (true)
          {
            label1263: j += 1;
            localObject4 = localObject3;
            localObject3 = localObject2;
            localObject2 = localObject4;
            break;
            label1282: localObject3 = localObject4;
            localObject2 = localMessage1;
          }
        }
      }
    });
  }

  public void putSentFile(String paramString, TLObject paramTLObject, int paramInt)
  {
    if ((paramString == null) || (paramTLObject == null))
      return;
    this.storageQueue.postRunnable(new Runnable(paramString, paramTLObject, paramInt)
    {
      public void run()
      {
        Object localObject6 = null;
        NativeByteBuffer localNativeByteBuffer = null;
        SQLitePreparedStatement localSQLitePreparedStatement = null;
        Object localObject2 = localObject6;
        Object localObject1 = localNativeByteBuffer;
        while (true)
        {
          try
          {
            String str = Utilities.MD5(this.val$path);
            if (str == null)
              continue;
            localObject2 = localObject6;
            localObject1 = localNativeByteBuffer;
            if (!(this.val$file instanceof TLRPC.Photo))
              continue;
            localObject2 = localObject6;
            localObject1 = localNativeByteBuffer;
            Object localObject4 = new TLRPC.TL_messageMediaPhoto();
            localObject2 = localObject6;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).caption = "";
            localObject2 = localObject6;
            localObject1 = localNativeByteBuffer;
            ((TLRPC.MessageMedia)localObject4).photo = ((TLRPC.Photo)this.val$file);
            if (localObject4 != null)
              continue;
            if (0 == 0)
              continue;
            throw new NullPointerException();
            return;
            localObject2 = localObject6;
            localObject1 = localNativeByteBuffer;
            if ((this.val$file instanceof TLRPC.Document))
            {
              localObject2 = localObject6;
              localObject1 = localNativeByteBuffer;
              localObject4 = new TLRPC.TL_messageMediaDocument();
              localObject2 = localObject6;
              localObject1 = localNativeByteBuffer;
              ((TLRPC.MessageMedia)localObject4).caption = "";
              localObject2 = localObject6;
              localObject1 = localNativeByteBuffer;
              ((TLRPC.MessageMedia)localObject4).document = ((TLRPC.Document)this.val$file);
              continue;
              localObject2 = localObject6;
              localObject1 = localNativeByteBuffer;
              localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO sent_files_v2 VALUES(?, ?, ?)");
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.requery();
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localNativeByteBuffer = new NativeByteBuffer(((TLRPC.MessageMedia)localObject4).getObjectSize());
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              ((TLRPC.MessageMedia)localObject4).serializeToStream(localNativeByteBuffer);
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindString(1, str);
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindInteger(2, this.val$type);
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localSQLitePreparedStatement.step();
              localObject2 = localSQLitePreparedStatement;
              localObject1 = localSQLitePreparedStatement;
              localNativeByteBuffer.reuse();
              if (localSQLitePreparedStatement == null)
                continue;
              localSQLitePreparedStatement.dispose();
              return;
            }
          }
          catch (Exception localException)
          {
            localObject1 = localObject2;
            FileLog.e(localException);
            return;
          }
          finally
          {
            if (localObject1 == null)
              continue;
            ((SQLitePreparedStatement)localObject1).dispose();
          }
          Object localObject5 = null;
        }
      }
    });
  }

  public void putUsersAndChats(ArrayList<TLRPC.User> paramArrayList, ArrayList<TLRPC.Chat> paramArrayList1, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramArrayList != null) && (paramArrayList.isEmpty()) && (paramArrayList1 != null) && (paramArrayList1.isEmpty()))
      return;
    if (paramBoolean2)
    {
      this.storageQueue.postRunnable(new Runnable(paramArrayList, paramArrayList1, paramBoolean1)
      {
        public void run()
        {
          MessagesStorage.this.putUsersAndChatsInternal(this.val$users, this.val$chats, this.val$withTransaction);
        }
      });
      return;
    }
    putUsersAndChatsInternal(paramArrayList, paramArrayList1, paramBoolean1);
  }

  public void putWallpapers(ArrayList<TLRPC.WallPaper> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM wallpapers WHERE 1").stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO wallpapers VALUES(?, ?)");
          Iterator localIterator = this.val$wallPapers.iterator();
          int i = 0;
          while (localIterator.hasNext())
          {
            TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)localIterator.next();
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localWallPaper.getObjectSize());
            localWallPaper.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindInteger(1, i);
            localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            localNativeByteBuffer.reuse();
            i += 1;
          }
          localSQLitePreparedStatement.dispose();
          MessagesStorage.this.database.commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void putWebPages(HashMap<Long, TLRPC.WebPage> paramHashMap)
  {
    if ((paramHashMap == null) || (paramHashMap.isEmpty()))
      return;
    this.storageQueue.postRunnable(new Runnable(paramHashMap)
    {
      public void run()
      {
        Object localObject3;
        Object localObject4;
        do
          while (true)
          {
            try
            {
              ArrayList localArrayList = new ArrayList();
              localObject1 = this.val$webPages.entrySet().iterator();
              if (!((Iterator)localObject1).hasNext())
                break;
              localObject2 = (Map.Entry)((Iterator)localObject1).next();
              localObject3 = MessagesStorage.this.database.queryFinalized("SELECT mid FROM webpage_pending WHERE id = " + ((Map.Entry)localObject2).getKey(), new Object[0]);
              localObject4 = new ArrayList();
              if (((SQLiteCursor)localObject3).next())
              {
                ((ArrayList)localObject4).add(Long.valueOf(((SQLiteCursor)localObject3).longValue(0)));
                continue;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            ((SQLiteCursor)localObject3).dispose();
            if (((ArrayList)localObject4).isEmpty())
              continue;
            localObject3 = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT mid, data FROM messages WHERE mid IN (%s)", new Object[] { TextUtils.join(",", (Iterable)localObject4) }), new Object[0]);
            while (((SQLiteCursor)localObject3).next())
            {
              i = ((SQLiteCursor)localObject3).intValue(0);
              localObject4 = ((SQLiteCursor)localObject3).byteBufferValue(1);
              if (localObject4 == null)
                continue;
              TLRPC.Message localMessage = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
              ((NativeByteBuffer)localObject4).reuse();
              if (!(localMessage.media instanceof TLRPC.TL_messageMediaWebPage))
                continue;
              localMessage.id = i;
              localMessage.media.webpage = ((TLRPC.WebPage)((Map.Entry)localObject2).getValue());
              localException.add(localMessage);
            }
            ((SQLiteCursor)localObject3).dispose();
          }
        while (localException.isEmpty());
        MessagesStorage.this.database.beginTransaction();
        Object localObject1 = MessagesStorage.this.database.executeFast("UPDATE messages SET data = ? WHERE mid = ?");
        Object localObject2 = MessagesStorage.this.database.executeFast("UPDATE media_v2 SET data = ? WHERE mid = ?");
        int i = 0;
        long l;
        if (i < localException.size())
        {
          localObject3 = (TLRPC.Message)localException.get(i);
          localObject4 = new NativeByteBuffer(((TLRPC.Message)localObject3).getObjectSize());
          ((TLRPC.Message)localObject3).serializeToStream((AbstractSerializedData)localObject4);
          l = ((TLRPC.Message)localObject3).id;
          if (((TLRPC.Message)localObject3).to_id.channel_id == 0)
            break label501;
          l = ((TLRPC.Message)localObject3).to_id.channel_id << 32 | l;
        }
        label501: 
        while (true)
        {
          ((SQLitePreparedStatement)localObject1).requery();
          ((SQLitePreparedStatement)localObject1).bindByteBuffer(1, (NativeByteBuffer)localObject4);
          ((SQLitePreparedStatement)localObject1).bindLong(2, l);
          ((SQLitePreparedStatement)localObject1).step();
          ((SQLitePreparedStatement)localObject2).requery();
          ((SQLitePreparedStatement)localObject2).bindByteBuffer(1, (NativeByteBuffer)localObject4);
          ((SQLitePreparedStatement)localObject2).bindLong(2, l);
          ((SQLitePreparedStatement)localObject2).step();
          ((NativeByteBuffer)localObject4).reuse();
          i += 1;
          break;
          ((SQLitePreparedStatement)localObject1).dispose();
          ((SQLitePreparedStatement)localObject2).dispose();
          MessagesStorage.this.database.commitTransaction();
          AndroidUtilities.runOnUIThread(new Runnable(localException)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.didReceivedWebpages, new Object[] { this.val$messages });
            }
          });
          return;
        }
      }
    });
  }

  public void putWebRecent(ArrayList<MediaController.SearchImage> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        int j = 200;
        while (true)
        {
          int i;
          try
          {
            MessagesStorage.this.database.beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            i = 0;
            if ((i < this.val$arrayList.size()) && (i != 200))
              continue;
            localSQLitePreparedStatement.dispose();
            MessagesStorage.this.database.commitTransaction();
            if (this.val$arrayList.size() < 200)
              continue;
            MessagesStorage.this.database.beginTransaction();
            i = j;
            if (i >= this.val$arrayList.size())
              continue;
            MessagesStorage.this.database.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((MediaController.SearchImage)this.val$arrayList.get(i)).id + "'").stepThis().dispose();
            i += 1;
            continue;
            MediaController.SearchImage localSearchImage = (MediaController.SearchImage)this.val$arrayList.get(i);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindString(1, localSearchImage.id);
            localSQLitePreparedStatement.bindInteger(2, localSearchImage.type);
            if (localSearchImage.imageUrl == null)
              break label404;
            Object localObject = localSearchImage.imageUrl;
            localSQLitePreparedStatement.bindString(3, (String)localObject);
            if (localSearchImage.thumbUrl == null)
              break label410;
            localObject = localSearchImage.thumbUrl;
            localSQLitePreparedStatement.bindString(4, (String)localObject);
            if (localSearchImage.localUrl == null)
              break label416;
            localObject = localSearchImage.localUrl;
            localSQLitePreparedStatement.bindString(5, (String)localObject);
            localSQLitePreparedStatement.bindInteger(6, localSearchImage.width);
            localSQLitePreparedStatement.bindInteger(7, localSearchImage.height);
            localSQLitePreparedStatement.bindInteger(8, localSearchImage.size);
            localSQLitePreparedStatement.bindInteger(9, localSearchImage.date);
            if (localSearchImage.document == null)
              continue;
            localObject = new NativeByteBuffer(localSearchImage.document.getObjectSize());
            localSearchImage.document.serializeToStream((AbstractSerializedData)localObject);
            localSQLitePreparedStatement.bindByteBuffer(10, (NativeByteBuffer)localObject);
            localSQLitePreparedStatement.step();
            if (localObject != null)
            {
              ((NativeByteBuffer)localObject).reuse();
              break label397;
              localSQLitePreparedStatement.bindNull(10);
              localObject = null;
              continue;
              MessagesStorage.this.database.commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          label397: i += 1;
          continue;
          label404: String str = "";
          continue;
          label410: str = "";
          continue;
          label416: str = "";
        }
      }
    });
  }

  public void removeFromDownloadQueue(long paramLong, int paramInt, boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable(paramBoolean, paramInt, paramLong)
    {
      public void run()
      {
        while (true)
        {
          try
          {
            if (!this.val$move)
              continue;
            SQLiteCursor localSQLiteCursor = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT min(date) FROM download_queue WHERE type = %d", new Object[] { Integer.valueOf(this.val$type) }), new Object[0]);
            if (localSQLiteCursor.next())
            {
              i = localSQLiteCursor.intValue(0);
              localSQLiteCursor.dispose();
              if (i == -1)
                break;
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", new Object[] { Integer.valueOf(i - 1), Long.valueOf(this.val$id), Integer.valueOf(this.val$type) })).stepThis().dispose();
              return;
              MessagesStorage.this.database.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", new Object[] { Long.valueOf(this.val$id), Integer.valueOf(this.val$type) })).stepThis().dispose();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          int i = -1;
        }
      }
    });
  }

  public void removePendingTask(long paramLong)
  {
    this.storageQueue.postRunnable(new Runnable(paramLong)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + this.val$id).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void saveBotCache(String paramString, TLObject paramTLObject)
  {
    if ((paramTLObject == null) || (TextUtils.isEmpty(paramString)))
      return;
    this.storageQueue.postRunnable(new Runnable(paramTLObject, paramString)
    {
      public void run()
      {
        while (true)
          try
          {
            int i = ConnectionsManager.getInstance().getCurrentTime();
            if (!(this.val$result instanceof TLRPC.TL_messages_botCallbackAnswer))
              continue;
            i = ((TLRPC.TL_messages_botCallbackAnswer)this.val$result).cache_time + i;
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$result.getObjectSize());
            this.val$result.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindString(1, this.val$key);
            localSQLitePreparedStatement.bindInteger(2, i);
            localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            localNativeByteBuffer.reuse();
            return;
            if ((this.val$result instanceof TLRPC.TL_messages_botResults))
            {
              int j = ((TLRPC.TL_messages_botResults)this.val$result).cache_time;
              i = j + i;
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
      }
    });
  }

  public void saveChannelPts(int paramInt1, int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt2, paramInt1)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
          localSQLitePreparedStatement.bindInteger(1, this.val$pts);
          localSQLitePreparedStatement.bindInteger(2, -this.val$channelId);
          localSQLitePreparedStatement.step();
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

  public void saveDiffParams(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2, paramInt3, paramInt4)
    {
      public void run()
      {
        try
        {
          if ((MessagesStorage.this.lastSavedSeq == this.val$seq) && (MessagesStorage.this.lastSavedPts == this.val$pts) && (MessagesStorage.this.lastSavedDate == this.val$date) && (MessagesStorage.lastQtsValue == this.val$qts))
            return;
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
          localSQLitePreparedStatement.bindInteger(1, this.val$seq);
          localSQLitePreparedStatement.bindInteger(2, this.val$pts);
          localSQLitePreparedStatement.bindInteger(3, this.val$date);
          localSQLitePreparedStatement.bindInteger(4, this.val$qts);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.access$302(MessagesStorage.this, this.val$seq);
          MessagesStorage.access$402(MessagesStorage.this, this.val$pts);
          MessagesStorage.access$502(MessagesStorage.this, this.val$date);
          MessagesStorage.access$602(MessagesStorage.this, this.val$qts);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void saveSecretParams(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2, paramArrayOfByte)
    {
      public void run()
      {
        int i = 1;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
          localSQLitePreparedStatement.bindInteger(1, this.val$lsv);
          localSQLitePreparedStatement.bindInteger(2, this.val$sg);
          if (this.val$pbytes != null)
            i = this.val$pbytes.length;
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(i);
          if (this.val$pbytes != null)
            localNativeByteBuffer.writeBytes(this.val$pbytes);
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localSQLitePreparedStatement.dispose();
          localNativeByteBuffer.reuse();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void setDialogFlags(long paramLong1, long paramLong2)
  {
    this.storageQueue.postRunnable(new Runnable(paramLong1, paramLong2)
    {
      public void run()
      {
        try
        {
          MessagesStorage.this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", new Object[] { Long.valueOf(this.val$did), Long.valueOf(this.val$flags) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void setDialogPinned(long paramLong, int paramInt)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt, paramLong)
    {
      public void run()
      {
        int j = 0;
        while (true)
        {
          try
          {
            if ((this.val$pinned != 0) || ((int)this.val$did == 0))
              continue;
            Object localObject = MessagesStorage.this.database.queryFinalized("SELECT date FROM dialogs WHERE did = " + this.val$did, new Object[0]);
            if (((SQLiteCursor)localObject).next())
            {
              i = ((SQLiteCursor)localObject).intValue(0);
              ((SQLiteCursor)localObject).dispose();
              localObject = MessagesStorage.this.database.queryFinalized("SELECT min(date) FROM dialogs WHERE date != 0 AND pinned = 0", new Object[0]);
              if (!((SQLiteCursor)localObject).next())
                continue;
              j = ((SQLiteCursor)localObject).intValue(0);
              ((SQLiteCursor)localObject).dispose();
              if (i > j)
                continue;
              MessagesStorage.this.database.executeFast("DELETE FROM dialogs WHERE did = " + this.val$did).stepThis().dispose();
              return;
              localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
              ((SQLitePreparedStatement)localObject).bindInteger(1, this.val$pinned);
              ((SQLitePreparedStatement)localObject).bindLong(2, this.val$did);
              ((SQLitePreparedStatement)localObject).step();
              ((SQLitePreparedStatement)localObject).dispose();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          int i = 0;
        }
      }
    });
  }

  public void setMessageSeq(int paramInt1, int paramInt2, int paramInt3)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$mid);
          localSQLitePreparedStatement.bindInteger(2, this.val$seq_in);
          localSQLitePreparedStatement.bindInteger(3, this.val$seq_out);
          localSQLitePreparedStatement.step();
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

  public void startTransaction(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable()
      {
        public void run()
        {
          try
          {
            MessagesStorage.this.database.beginTransaction();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
      return;
    }
    try
    {
      this.database.beginTransaction();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void unpinAllDialogsExceptNew(ArrayList<Long> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        do
        {
          try
          {
            ArrayList localArrayList = new ArrayList();
            localObject = MessagesStorage.this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs WHERE pinned != 0 AND did NOT IN (%s)", new Object[] { TextUtils.join(",", this.val$dids) }), new Object[0]);
            while (((SQLiteCursor)localObject).next())
            {
              if ((int)((SQLiteCursor)localObject).longValue(0) == 0)
                continue;
              localArrayList.add(Long.valueOf(((SQLiteCursor)localObject).longValue(0)));
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          ((SQLiteCursor)localObject).dispose();
        }
        while (localException.isEmpty());
        Object localObject = MessagesStorage.this.database.queryFinalized("SELECT min(date), min(date_i) FROM dialogs WHERE (date != 0 OR date_i != 0) AND pinned = 0", new Object[0]);
        int i;
        int j;
        if (((SQLiteCursor)localObject).next())
        {
          i = ((SQLiteCursor)localObject).intValue(0);
          j = ((SQLiteCursor)localObject).intValue(1);
          if ((i == 0) || (j == 0))
            break label356;
          i = Math.min(i, j);
        }
        while (true)
        {
          ((SQLiteCursor)localObject).dispose();
          localObject = MessagesStorage.this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
          j = 0;
          while (true)
          {
            long l;
            SQLiteCursor localSQLiteCursor;
            int k;
            if (j < localException.size())
            {
              l = ((Long)localException.get(j)).longValue();
              localSQLiteCursor = MessagesStorage.this.database.queryFinalized("SELECT date FROM dialogs WHERE did = " + l, new Object[0]);
              if (!localSQLiteCursor.next())
                break label339;
              k = localSQLiteCursor.intValue(0);
            }
            while (true)
            {
              localSQLiteCursor.dispose();
              if (k <= i)
              {
                MessagesStorage.this.database.executeFast("DELETE FROM dialogs WHERE did = " + l).stepThis().dispose();
              }
              else
              {
                ((SQLitePreparedStatement)localObject).requery();
                ((SQLitePreparedStatement)localObject).bindInteger(1, 0);
                ((SQLitePreparedStatement)localObject).bindLong(2, l);
                ((SQLitePreparedStatement)localObject).step();
                break label349;
                ((SQLitePreparedStatement)localObject).dispose();
                return;
                label339: k = 0;
                continue;
                i = 0;
                break;
              }
            }
            label349: j += 1;
          }
          label356: if (i == 0)
          {
            i = j;
            continue;
          }
        }
      }
    });
  }

  public void updateChannelPinnedMessage(int paramInt1, int paramInt2)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        try
        {
          Object localObject2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$channelId, new Object[0]);
          SQLitePreparedStatement localSQLitePreparedStatement = null;
          new ArrayList();
          Object localObject1 = localSQLitePreparedStatement;
          if (((SQLiteCursor)localObject2).next())
          {
            NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
            localObject1 = localSQLitePreparedStatement;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject2).intValue(1);
            }
          }
          ((SQLiteCursor)localObject2).dispose();
          if ((localObject1 instanceof TLRPC.TL_channelFull))
          {
            ((TLRPC.ChatFull)localObject1).pinned_msg_id = this.val$messageId;
            ((TLRPC.ChatFull)localObject1).flags |= 32;
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.ChatFull)localObject1)
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject2 = new NativeByteBuffer(((TLRPC.ChatFull)localObject1).getObjectSize());
            ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement.bindInteger(1, this.val$channelId);
            localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject2);
            localSQLitePreparedStatement.bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            ((NativeByteBuffer)localObject2).reuse();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void updateChannelUsers(int paramInt, ArrayList<TLRPC.ChannelParticipant> paramArrayList)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt, paramArrayList)
    {
      public void run()
      {
        try
        {
          long l = -this.val$channel_id;
          MessagesStorage.this.database.executeFast("DELETE FROM channel_users_v2 WHERE did = " + l).stepThis().dispose();
          MessagesStorage.this.database.beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO channel_users_v2 VALUES(?, ?, ?, ?)");
          int j = (int)(System.currentTimeMillis() / 1000L);
          int i = 0;
          while (i < this.val$participants.size())
          {
            TLRPC.ChannelParticipant localChannelParticipant = (TLRPC.ChannelParticipant)this.val$participants.get(i);
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, l);
            localSQLitePreparedStatement.bindInteger(2, localChannelParticipant.user_id);
            localSQLitePreparedStatement.bindInteger(3, j);
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localChannelParticipant.getObjectSize());
            localChannelParticipant.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindByteBuffer(4, localNativeByteBuffer);
            localNativeByteBuffer.reuse();
            localSQLitePreparedStatement.step();
            j -= 1;
            i += 1;
          }
          localSQLitePreparedStatement.dispose();
          MessagesStorage.this.database.commitTransaction();
          MessagesStorage.this.loadChatInfo(this.val$channel_id, null, false, true);
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void updateChatInfo(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    this.storageQueue.postRunnable(new Runnable(paramInt1, paramInt3, paramInt2, paramInt4, paramInt5)
    {
      public void run()
      {
        while (true)
        {
          Object localObject2;
          Object localObject3;
          try
          {
            localObject2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$chat_id, new Object[0]);
            new ArrayList();
            if (!((SQLiteCursor)localObject2).next())
              break label515;
            localObject3 = ((SQLiteCursor)localObject2).byteBufferValue(0);
            if (localObject3 == null)
              break label515;
            TLRPC.ChatFull localChatFull = TLRPC.ChatFull.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
            ((NativeByteBuffer)localObject3).reuse();
            localChatFull.pinned_msg_id = ((SQLiteCursor)localObject2).intValue(1);
            ((SQLiteCursor)localObject2).dispose();
            if (!(localChatFull instanceof TLRPC.TL_chatFull))
              break label520;
            if (this.val$what != 1)
              continue;
            i = 0;
            if (i >= localChatFull.participants.participants.size())
              continue;
            if (((TLRPC.ChatParticipant)localChatFull.participants.participants.get(i)).user_id != this.val$user_id)
              break label521;
            localChatFull.participants.participants.remove(i);
            localChatFull.participants.version = this.val$version;
            AndroidUtilities.runOnUIThread(new Runnable(localChatFull)
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
            localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject3 = new NativeByteBuffer(localChatFull.getObjectSize());
            localChatFull.serializeToStream((AbstractSerializedData)localObject3);
            ((SQLitePreparedStatement)localObject2).bindInteger(1, this.val$chat_id);
            ((SQLitePreparedStatement)localObject2).bindByteBuffer(2, (NativeByteBuffer)localObject3);
            ((SQLitePreparedStatement)localObject2).bindInteger(3, localChatFull.pinned_msg_id);
            ((SQLitePreparedStatement)localObject2).step();
            ((SQLitePreparedStatement)localObject2).dispose();
            ((NativeByteBuffer)localObject3).reuse();
            return;
            if (this.val$what == 0)
            {
              localObject2 = localChatFull.participants.participants.iterator();
              if (!((Iterator)localObject2).hasNext())
                continue;
              if (((TLRPC.ChatParticipant)((Iterator)localObject2).next()).user_id == this.val$user_id)
              {
                return;
                localObject2 = new TLRPC.TL_chatParticipant();
                ((TLRPC.TL_chatParticipant)localObject2).user_id = this.val$user_id;
                ((TLRPC.TL_chatParticipant)localObject2).inviter_id = this.val$invited_id;
                ((TLRPC.TL_chatParticipant)localObject2).date = ConnectionsManager.getInstance().getCurrentTime();
                localChatFull.participants.participants.add(localObject2);
                continue;
              }
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          if (this.val$what != 2)
            continue;
          int i = 0;
          while (i < localException.participants.participants.size())
          {
            localObject3 = (TLRPC.ChatParticipant)localException.participants.participants.get(i);
            if (((TLRPC.ChatParticipant)localObject3).user_id == this.val$user_id)
            {
              if (this.val$invited_id == 1)
              {
                localObject2 = new TLRPC.TL_chatParticipantAdmin();
                ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
                ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
              }
              for (((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id; ; ((TLRPC.ChatParticipant)localObject2).inviter_id = ((TLRPC.ChatParticipant)localObject3).inviter_id)
              {
                localException.participants.participants.set(i, localObject2);
                break;
                localObject2 = new TLRPC.TL_chatParticipant();
                ((TLRPC.ChatParticipant)localObject2).user_id = ((TLRPC.ChatParticipant)localObject3).user_id;
                ((TLRPC.ChatParticipant)localObject2).date = ((TLRPC.ChatParticipant)localObject3).date;
              }
            }
            i += 1;
          }
          label515: Object localObject1 = null;
          continue;
          label520: return;
          label521: i += 1;
        }
      }
    });
  }

  public void updateChatInfo(TLRPC.ChatFull paramChatFull, boolean paramBoolean)
  {
    this.storageQueue.postRunnable(new Runnable(paramBoolean, paramChatFull)
    {
      public void run()
      {
        try
        {
          if (this.val$ifExist)
          {
            localObject1 = MessagesStorage.this.database.queryFinalized("SELECT uid FROM chat_settings_v2 WHERE uid = " + this.val$info.id, new Object[0]);
            boolean bool = ((SQLiteCursor)localObject1).next();
            ((SQLiteCursor)localObject1).dispose();
            if (!bool)
              return;
          }
          Object localObject1 = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
          Object localObject2 = new NativeByteBuffer(this.val$info.getObjectSize());
          this.val$info.serializeToStream((AbstractSerializedData)localObject2);
          ((SQLitePreparedStatement)localObject1).bindInteger(1, this.val$info.id);
          ((SQLitePreparedStatement)localObject1).bindByteBuffer(2, (NativeByteBuffer)localObject2);
          ((SQLitePreparedStatement)localObject1).bindInteger(3, this.val$info.pinned_msg_id);
          ((SQLitePreparedStatement)localObject1).step();
          ((SQLitePreparedStatement)localObject1).dispose();
          ((NativeByteBuffer)localObject2).reuse();
          if ((this.val$info instanceof TLRPC.TL_channelFull))
          {
            localObject1 = MessagesStorage.this.database.queryFinalized("SELECT date, pts, last_mid, inbox_max, outbox_max, pinned FROM dialogs WHERE did = " + -this.val$info.id, new Object[0]);
            if (((SQLiteCursor)localObject1).next())
            {
              int i = ((SQLiteCursor)localObject1).intValue(3);
              if (i <= this.val$info.read_inbox_max_id)
              {
                i = this.val$info.read_inbox_max_id - i;
                if (i < this.val$info.unread_count)
                  this.val$info.unread_count = i;
                i = ((SQLiteCursor)localObject1).intValue(0);
                int j = ((SQLiteCursor)localObject1).intValue(1);
                long l = ((SQLiteCursor)localObject1).longValue(2);
                int k = ((SQLiteCursor)localObject1).intValue(4);
                int m = ((SQLiteCursor)localObject1).intValue(5);
                localObject2 = MessagesStorage.this.database.executeFast("REPLACE INTO dialogs VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                ((SQLitePreparedStatement)localObject2).bindLong(1, -this.val$info.id);
                ((SQLitePreparedStatement)localObject2).bindInteger(2, i);
                ((SQLitePreparedStatement)localObject2).bindInteger(3, this.val$info.unread_count);
                ((SQLitePreparedStatement)localObject2).bindLong(4, l);
                ((SQLitePreparedStatement)localObject2).bindInteger(5, this.val$info.read_inbox_max_id);
                ((SQLitePreparedStatement)localObject2).bindInteger(6, Math.max(k, this.val$info.read_outbox_max_id));
                ((SQLitePreparedStatement)localObject2).bindLong(7, 0L);
                ((SQLitePreparedStatement)localObject2).bindInteger(8, 0);
                ((SQLitePreparedStatement)localObject2).bindInteger(9, j);
                ((SQLitePreparedStatement)localObject2).bindInteger(10, 0);
                ((SQLitePreparedStatement)localObject2).bindInteger(11, m);
                ((SQLitePreparedStatement)localObject2).step();
                ((SQLitePreparedStatement)localObject2).dispose();
              }
            }
            ((SQLiteCursor)localObject1).dispose();
            return;
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void updateChatParticipants(TLRPC.ChatParticipants paramChatParticipants)
  {
    if (paramChatParticipants == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramChatParticipants)
    {
      public void run()
      {
        try
        {
          Object localObject2 = MessagesStorage.this.database.queryFinalized("SELECT info, pinned FROM chat_settings_v2 WHERE uid = " + this.val$participants.chat_id, new Object[0]);
          SQLitePreparedStatement localSQLitePreparedStatement = null;
          new ArrayList();
          Object localObject1 = localSQLitePreparedStatement;
          if (((SQLiteCursor)localObject2).next())
          {
            NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject2).byteBufferValue(0);
            localObject1 = localSQLitePreparedStatement;
            if (localNativeByteBuffer != null)
            {
              localObject1 = TLRPC.ChatFull.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
              localNativeByteBuffer.reuse();
              ((TLRPC.ChatFull)localObject1).pinned_msg_id = ((SQLiteCursor)localObject2).intValue(1);
            }
          }
          ((SQLiteCursor)localObject2).dispose();
          if ((localObject1 instanceof TLRPC.TL_chatFull))
          {
            ((TLRPC.ChatFull)localObject1).participants = this.val$participants;
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.ChatFull)localObject1)
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatInfoDidLoaded, new Object[] { this.val$finalInfo, Integer.valueOf(0), Boolean.valueOf(false), null });
              }
            });
            localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?)");
            localObject2 = new NativeByteBuffer(((TLRPC.ChatFull)localObject1).getObjectSize());
            ((TLRPC.ChatFull)localObject1).serializeToStream((AbstractSerializedData)localObject2);
            localSQLitePreparedStatement.bindInteger(1, ((TLRPC.ChatFull)localObject1).id);
            localSQLitePreparedStatement.bindByteBuffer(2, (NativeByteBuffer)localObject2);
            localSQLitePreparedStatement.bindInteger(3, ((TLRPC.ChatFull)localObject1).pinned_msg_id);
            localSQLitePreparedStatement.step();
            localSQLitePreparedStatement.dispose();
            ((NativeByteBuffer)localObject2).reuse();
          }
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public void updateDialogsWithDeletedMessages(ArrayList<Integer> paramArrayList, ArrayList<Long> paramArrayList1, boolean paramBoolean, int paramInt)
  {
    if ((paramArrayList.isEmpty()) && (paramInt == 0))
      return;
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable(paramArrayList, paramArrayList1, paramInt)
      {
        public void run()
        {
          MessagesStorage.this.updateDialogsWithDeletedMessagesInternal(this.val$messages, this.val$additionalDialogsToUpdate, this.val$channelId);
        }
      });
      return;
    }
    updateDialogsWithDeletedMessagesInternal(paramArrayList, paramArrayList1, paramInt);
  }

  public void updateDialogsWithReadMessages(SparseArray<Long> paramSparseArray1, SparseArray<Long> paramSparseArray2, boolean paramBoolean)
  {
    if (paramSparseArray1.size() == 0)
      return;
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable(paramSparseArray1, paramSparseArray2)
      {
        public void run()
        {
          MessagesStorage.this.updateDialogsWithReadMessagesInternal(null, this.val$inbox, this.val$outbox);
        }
      });
      return;
    }
    updateDialogsWithReadMessagesInternal(null, paramSparseArray1, paramSparseArray2);
  }

  public void updateEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramEncryptedChat)
    {
      public void run()
      {
        int j = 1;
        NativeByteBuffer localNativeByteBuffer1 = null;
        SQLitePreparedStatement localSQLitePreparedStatement2 = null;
        SQLitePreparedStatement localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
        Object localObject1 = localNativeByteBuffer1;
        try
        {
          if (this.val$chat.key_hash != null)
          {
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localNativeByteBuffer1;
            if (this.val$chat.key_hash.length >= 16);
          }
          else
          {
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localNativeByteBuffer1;
            if (this.val$chat.auth_key != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localNativeByteBuffer1;
              this.val$chat.key_hash = AndroidUtilities.calcAuthKeyHash(this.val$chat.auth_key);
            }
          }
          localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
          localObject1 = localNativeByteBuffer1;
          localSQLitePreparedStatement2 = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET data = ?, g = ?, authkey = ?, ttl = ?, layer = ?, seq_in = ?, seq_out = ?, use_count = ?, exchange_id = ?, key_date = ?, fprint = ?, fauthkey = ?, khash = ?, in_seq_no = ?, admin_id = ? WHERE uid = ?");
          localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
          localObject1 = localSQLitePreparedStatement2;
          localNativeByteBuffer1 = new NativeByteBuffer(this.val$chat.getObjectSize());
          localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
          localObject1 = localSQLitePreparedStatement2;
          NativeByteBuffer localNativeByteBuffer2;
          label213: NativeByteBuffer localNativeByteBuffer3;
          if (this.val$chat.a_or_b != null)
          {
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            i = this.val$chat.a_or_b.length;
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer2 = new NativeByteBuffer(i);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.auth_key == null)
              break label898;
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            i = this.val$chat.auth_key.length;
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer3 = new NativeByteBuffer(i);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.future_auth_key == null)
              break label903;
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
          }
          label898: label903: for (int i = this.val$chat.future_auth_key.length; ; i = 1)
          {
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            NativeByteBuffer localNativeByteBuffer4 = new NativeByteBuffer(i);
            i = j;
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.key_hash != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              i = this.val$chat.key_hash.length;
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            NativeByteBuffer localNativeByteBuffer5 = new NativeByteBuffer(i);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            this.val$chat.serializeToStream(localNativeByteBuffer1);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindByteBuffer(1, localNativeByteBuffer1);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.a_or_b != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer2.writeBytes(this.val$chat.a_or_b);
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.auth_key != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer3.writeBytes(this.val$chat.auth_key);
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.future_auth_key != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer4.writeBytes(this.val$chat.future_auth_key);
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            if (this.val$chat.key_hash != null)
            {
              localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
              localObject1 = localSQLitePreparedStatement2;
              localNativeByteBuffer5.writeBytes(this.val$chat.key_hash);
            }
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindByteBuffer(2, localNativeByteBuffer2);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindByteBuffer(3, localNativeByteBuffer3);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(4, this.val$chat.ttl);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(5, this.val$chat.layer);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(6, this.val$chat.seq_in);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(7, this.val$chat.seq_out);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(8, this.val$chat.key_use_count_in << 16 | this.val$chat.key_use_count_out);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindLong(9, this.val$chat.exchange_id);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(10, this.val$chat.key_create_date);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindLong(11, this.val$chat.future_key_fingerprint);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindByteBuffer(12, localNativeByteBuffer4);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindByteBuffer(13, localNativeByteBuffer5);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(14, this.val$chat.in_seq_no);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(15, this.val$chat.admin_id);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.bindInteger(16, this.val$chat.id);
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localSQLitePreparedStatement2.step();
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer1.reuse();
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer2.reuse();
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer3.reuse();
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer4.reuse();
            localSQLitePreparedStatement1 = localSQLitePreparedStatement2;
            localObject1 = localSQLitePreparedStatement2;
            localNativeByteBuffer5.reuse();
            if (localSQLitePreparedStatement2 != null)
              localSQLitePreparedStatement2.dispose();
            return;
            i = 1;
            break;
            i = 1;
            break label213;
          }
        }
        catch (Exception localException)
        {
          do
          {
            localObject1 = localSQLitePreparedStatement1;
            FileLog.e(localException);
          }
          while (localSQLitePreparedStatement1 == null);
          localSQLitePreparedStatement1.dispose();
          return;
        }
        finally
        {
          if (localObject1 != null)
            ((SQLitePreparedStatement)localObject1).dispose();
        }
        throw localObject2;
      }
    });
  }

  public void updateEncryptedChatLayer(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramEncryptedChat)
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, this.val$chat.layer);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, this.val$chat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e(localException);
          return;
        }
        finally
        {
          if (localObject3 != null)
            localObject3.dispose();
        }
        throw localObject2;
      }
    });
  }

  public void updateEncryptedChatSeq(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramEncryptedChat)
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, this.val$chat.seq_in);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, this.val$chat.seq_out);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(3, this.val$chat.key_use_count_in << 16 | this.val$chat.key_use_count_out);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(4, this.val$chat.in_seq_no);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(5, this.val$chat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e(localException);
          return;
        }
        finally
        {
          if (localObject3 != null)
            localObject3.dispose();
        }
        throw localObject2;
      }
    });
  }

  public void updateEncryptedChatTTL(TLRPC.EncryptedChat paramEncryptedChat)
  {
    if (paramEncryptedChat == null)
      return;
    this.storageQueue.postRunnable(new Runnable(paramEncryptedChat)
    {
      public void run()
      {
        Object localObject3 = null;
        Object localObject1 = null;
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.this.database.executeFast("UPDATE enc_chats SET ttl = ? WHERE uid = ?");
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(1, this.val$chat.ttl);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.bindInteger(2, this.val$chat.id);
          localObject1 = localSQLitePreparedStatement;
          localObject3 = localSQLitePreparedStatement;
          localSQLitePreparedStatement.step();
          return;
        }
        catch (Exception localException)
        {
          localObject3 = localObject1;
          FileLog.e(localException);
          return;
        }
        finally
        {
          if (localObject3 != null)
            localObject3.dispose();
        }
        throw localObject2;
      }
    });
  }

  public long[] updateMessageStateAndId(long paramLong, Integer paramInteger, int paramInt1, int paramInt2, boolean paramBoolean, int paramInt3)
  {
    if (paramBoolean)
    {
      this.storageQueue.postRunnable(new Runnable(paramLong, paramInteger, paramInt1, paramInt2, paramInt3)
      {
        public void run()
        {
          MessagesStorage.this.updateMessageStateAndIdInternal(this.val$random_id, this.val$_oldId, this.val$newId, this.val$date, this.val$channelId);
        }
      });
      return null;
    }
    return updateMessageStateAndIdInternal(paramLong, paramInteger, paramInt1, paramInt2, paramInt3);
  }

  public void updateUsers(ArrayList<TLRPC.User> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (paramArrayList.isEmpty())
      return;
    if (paramBoolean3)
    {
      this.storageQueue.postRunnable(new Runnable(paramArrayList, paramBoolean1, paramBoolean2)
      {
        public void run()
        {
          MessagesStorage.this.updateUsersInternal(this.val$users, this.val$onlyStatus, this.val$withTransaction);
        }
      });
      return;
    }
    updateUsersInternal(paramArrayList, paramBoolean1, paramBoolean2);
  }

  private class Hole
  {
    public int end;
    public int start;
    public int type;

    public Hole(int paramInt1, int arg3)
    {
      this.start = paramInt1;
      int i;
      this.end = i;
    }

    public Hole(int paramInt1, int paramInt2, int arg4)
    {
      this.type = paramInt1;
      this.start = paramInt2;
      int i;
      this.end = i;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MessagesStorage
 * JD-Core Version:    0.6.0
 */