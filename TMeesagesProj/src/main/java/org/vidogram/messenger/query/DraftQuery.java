package org.vidogram.messenger.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.DraftMessage;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.TL_channels_getMessages;
import org.vidogram.tgnet.TLRPC.TL_draftMessage;
import org.vidogram.tgnet.TLRPC.TL_draftMessageEmpty;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messages_getAllDrafts;
import org.vidogram.tgnet.TLRPC.TL_messages_getMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_saveDraft;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.messages_Messages;

public class DraftQuery
{
  private static HashMap<Long, TLRPC.Message> draftMessages;
  private static HashMap<Long, TLRPC.DraftMessage> drafts = new HashMap();
  private static boolean inTransaction;
  private static boolean loadingDrafts;
  private static SharedPreferences preferences;

  static
  {
    draftMessages = new HashMap();
    preferences = ApplicationLoader.applicationContext.getSharedPreferences("drafts", 0);
    Iterator localIterator = preferences.getAll().entrySet().iterator();
    while (true)
    {
      Object localObject2;
      if (localIterator.hasNext())
        localObject2 = (Map.Entry)localIterator.next();
      try
      {
        Object localObject1 = (String)((Map.Entry)localObject2).getKey();
        long l = Utilities.parseLong((String)localObject1).longValue();
        localObject2 = new SerializedData(Utilities.hexToBytes((String)((Map.Entry)localObject2).getValue()));
        if (((String)localObject1).startsWith("r_"))
        {
          localObject1 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((SerializedData)localObject2).readInt32(true), true);
          if (localObject1 == null)
            continue;
          draftMessages.put(Long.valueOf(l), localObject1);
          continue;
        }
        localObject1 = TLRPC.DraftMessage.TLdeserialize((AbstractSerializedData)localObject2, ((SerializedData)localObject2).readInt32(true), true);
        if (localObject1 == null)
          continue;
        drafts.put(Long.valueOf(l), localObject1);
        continue;
        return;
      }
      catch (Exception localException)
      {
      }
    }
  }

  public static void beginTransaction()
  {
    inTransaction = true;
  }

  public static void cleanDraft(long paramLong, boolean paramBoolean)
  {
    TLRPC.DraftMessage localDraftMessage = (TLRPC.DraftMessage)drafts.get(Long.valueOf(paramLong));
    if (localDraftMessage == null);
    do
    {
      return;
      if (paramBoolean)
        continue;
      drafts.remove(Long.valueOf(paramLong));
      draftMessages.remove(Long.valueOf(paramLong));
      preferences.edit().remove("" + paramLong).remove("r_" + paramLong).commit();
      MessagesController.getInstance().sortDialogs(null);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
      return;
    }
    while (localDraftMessage.reply_to_msg_id == 0);
    localDraftMessage.reply_to_msg_id = 0;
    localDraftMessage.flags &= -2;
    saveDraft(paramLong, localDraftMessage.message, localDraftMessage.entities, null, localDraftMessage.no_webpage, true);
  }

  public static void cleanup()
  {
    drafts.clear();
    draftMessages.clear();
    preferences.edit().clear().commit();
  }

  public static void endTransaction()
  {
    inTransaction = false;
  }

  public static TLRPC.DraftMessage getDraft(long paramLong)
  {
    return (TLRPC.DraftMessage)drafts.get(Long.valueOf(paramLong));
  }

  public static TLRPC.Message getDraftMessage(long paramLong)
  {
    return (TLRPC.Message)draftMessages.get(Long.valueOf(paramLong));
  }

  public static void loadDrafts()
  {
    if ((UserConfig.draftsLoaded) || (loadingDrafts))
      return;
    loadingDrafts = true;
    TLRPC.TL_messages_getAllDrafts localTL_messages_getAllDrafts = new TLRPC.TL_messages_getAllDrafts();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_getAllDrafts, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
          return;
        MessagesController.getInstance().processUpdates((TLRPC.Updates)paramTLObject, false);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            UserConfig.draftsLoaded = true;
            DraftQuery.access$002(false);
            UserConfig.saveConfig(false);
          }
        });
      }
    });
  }

  public static void saveDraft(long paramLong, CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.Message paramMessage, boolean paramBoolean)
  {
    saveDraft(paramLong, paramCharSequence, paramArrayList, paramMessage, paramBoolean, false);
  }

  public static void saveDraft(long paramLong, CharSequence paramCharSequence, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.Message paramMessage, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject;
    if ((!TextUtils.isEmpty(paramCharSequence)) || (paramMessage != null))
    {
      localObject = new TLRPC.TL_draftMessage();
      ((TLRPC.DraftMessage)localObject).date = (int)(System.currentTimeMillis() / 1000L);
      if (paramCharSequence != null)
        break label209;
      paramCharSequence = "";
      label41: ((TLRPC.DraftMessage)localObject).message = paramCharSequence;
      ((TLRPC.DraftMessage)localObject).no_webpage = paramBoolean1;
      if (paramMessage != null)
      {
        ((TLRPC.DraftMessage)localObject).reply_to_msg_id = paramMessage.id;
        ((TLRPC.DraftMessage)localObject).flags |= 1;
      }
      if ((paramArrayList != null) && (!paramArrayList.isEmpty()))
      {
        ((TLRPC.DraftMessage)localObject).entities = paramArrayList;
        ((TLRPC.DraftMessage)localObject).flags |= 8;
      }
      paramCharSequence = (TLRPC.DraftMessage)drafts.get(Long.valueOf(paramLong));
      if ((paramBoolean2) || (((paramCharSequence == null) || (!paramCharSequence.message.equals(((TLRPC.DraftMessage)localObject).message)) || (paramCharSequence.reply_to_msg_id != ((TLRPC.DraftMessage)localObject).reply_to_msg_id) || (paramCharSequence.no_webpage != ((TLRPC.DraftMessage)localObject).no_webpage)) && ((paramCharSequence != null) || (!TextUtils.isEmpty(((TLRPC.DraftMessage)localObject).message)) || (((TLRPC.DraftMessage)localObject).reply_to_msg_id != 0))))
        break label219;
    }
    label209: label219: 
    do
    {
      return;
      localObject = new TLRPC.TL_draftMessageEmpty();
      break;
      paramCharSequence = paramCharSequence.toString();
      break label41;
      saveDraft(paramLong, (TLRPC.DraftMessage)localObject, paramMessage, false);
      int i = (int)paramLong;
      if (i == 0)
        break label321;
      paramCharSequence = new TLRPC.TL_messages_saveDraft();
      paramCharSequence.peer = MessagesController.getInputPeer(i);
    }
    while (paramCharSequence.peer == null);
    paramCharSequence.message = ((TLRPC.DraftMessage)localObject).message;
    paramCharSequence.no_webpage = ((TLRPC.DraftMessage)localObject).no_webpage;
    paramCharSequence.reply_to_msg_id = ((TLRPC.DraftMessage)localObject).reply_to_msg_id;
    paramCharSequence.entities = ((TLRPC.DraftMessage)localObject).entities;
    paramCharSequence.flags = ((TLRPC.DraftMessage)localObject).flags;
    ConnectionsManager.getInstance().sendRequest(paramCharSequence, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
    label321: MessagesController.getInstance().sortDialogs(null);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
  }

  public static void saveDraft(long paramLong, TLRPC.DraftMessage paramDraftMessage, TLRPC.Message paramMessage, boolean paramBoolean)
  {
    TLRPC.User localUser = null;
    SharedPreferences.Editor localEditor = preferences.edit();
    label150: label198: long l;
    if ((paramDraftMessage == null) || ((paramDraftMessage instanceof TLRPC.TL_draftMessageEmpty)))
    {
      drafts.remove(Long.valueOf(paramLong));
      draftMessages.remove(Long.valueOf(paramLong));
      preferences.edit().remove("" + paramLong).remove("r_" + paramLong).commit();
      if (paramMessage != null)
        break label361;
      draftMessages.remove(Long.valueOf(paramLong));
      localEditor.remove("r_" + paramLong);
      localEditor.commit();
      if (paramBoolean)
        if ((paramDraftMessage.reply_to_msg_id != 0) && (paramMessage == null))
        {
          i = (int)paramLong;
          if (i <= 0)
            break label430;
          localUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
          paramMessage = null;
          if ((localUser != null) || (paramMessage != null))
          {
            l = paramDraftMessage.reply_to_msg_id;
            if (!ChatObject.isChannel(paramMessage))
              break label446;
            l |= paramMessage.id << 32;
          }
        }
    }
    label430: label446: for (int i = paramMessage.id; ; i = 0)
    {
      while (true)
      {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(l, i, paramLong)
        {
          public void run()
          {
            Object localObject2 = null;
            try
            {
              SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM messages WHERE mid = %d", new Object[] { Long.valueOf(this.val$messageIdFinal) }), new Object[0]);
              Object localObject1 = localObject2;
              if (localSQLiteCursor.next())
              {
                NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                localObject1 = localObject2;
                if (localNativeByteBuffer != null)
                {
                  localObject1 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  localNativeByteBuffer.reuse();
                }
              }
              localSQLiteCursor.dispose();
              if (localObject1 == null)
              {
                if (this.val$channelIdFinal != 0)
                {
                  localObject1 = new TLRPC.TL_channels_getMessages();
                  ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInputChannel(this.val$channelIdFinal);
                  ((TLRPC.TL_channels_getMessages)localObject1).id.add(Integer.valueOf((int)this.val$messageIdFinal));
                  ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
                  {
                    public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                    {
                      if (paramTL_error == null)
                      {
                        paramTLObject = (TLRPC.messages_Messages)paramTLObject;
                        if (!paramTLObject.messages.isEmpty())
                          DraftQuery.access$100(DraftQuery.3.this.val$did, (TLRPC.Message)paramTLObject.messages.get(0));
                      }
                    }
                  });
                  return;
                }
                localObject1 = new TLRPC.TL_messages_getMessages();
                ((TLRPC.TL_messages_getMessages)localObject1).id.add(Integer.valueOf((int)this.val$messageIdFinal));
                ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate()
                {
                  public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                  {
                    if (paramTL_error == null)
                    {
                      paramTLObject = (TLRPC.messages_Messages)paramTLObject;
                      if (!paramTLObject.messages.isEmpty())
                        DraftQuery.access$100(DraftQuery.3.this.val$did, (TLRPC.Message)paramTLObject.messages.get(0));
                    }
                  }
                });
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            DraftQuery.access$100(this.val$did, localException);
          }
        });
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, new Object[] { Long.valueOf(paramLong) });
        return;
        drafts.put(Long.valueOf(paramLong), paramDraftMessage);
        try
        {
          SerializedData localSerializedData1 = new SerializedData(paramDraftMessage.getObjectSize());
          paramDraftMessage.serializeToStream(localSerializedData1);
          localEditor.putString("" + paramLong, Utilities.bytesToHex(localSerializedData1.toByteArray()));
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
      break;
      label361: draftMessages.put(Long.valueOf(paramLong), paramMessage);
      SerializedData localSerializedData2 = new SerializedData(paramMessage.getObjectSize());
      paramMessage.serializeToStream(localSerializedData2);
      localEditor.putString("r_" + paramLong, Utilities.bytesToHex(localSerializedData2.toByteArray()));
      break label150;
      paramMessage = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      break label198;
    }
  }

  private static void saveDraftReplyMessage(long paramLong, TLRPC.Message paramMessage)
  {
    if (paramMessage == null)
      return;
    AndroidUtilities.runOnUIThread(new Runnable(paramLong, paramMessage)
    {
      public void run()
      {
        Object localObject = (TLRPC.DraftMessage)DraftQuery.drafts.get(Long.valueOf(this.val$did));
        if ((localObject != null) && (((TLRPC.DraftMessage)localObject).reply_to_msg_id == this.val$message.id))
        {
          DraftQuery.draftMessages.put(Long.valueOf(this.val$did), this.val$message);
          localObject = new SerializedData(this.val$message.getObjectSize());
          this.val$message.serializeToStream((AbstractSerializedData)localObject);
          DraftQuery.preferences.edit().putString("r_" + this.val$did, Utilities.bytesToHex(((SerializedData)localObject).toByteArray())).commit();
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.newDraftReceived, new Object[] { Long.valueOf(this.val$did) });
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.DraftQuery
 * JD-Core Version:    0.6.0
 */