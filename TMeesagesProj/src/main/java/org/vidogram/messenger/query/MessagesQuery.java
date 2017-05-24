package org.vidogram.messenger.query;

import android.text.Spannable;
import android.text.TextUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_channels_getMessages;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.vidogram.tgnet.TLRPC.TL_messageActionGameScore;
import org.vidogram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.vidogram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.vidogram.tgnet.TLRPC.TL_messageEntityBold;
import org.vidogram.tgnet.TLRPC.TL_messageEntityCode;
import org.vidogram.tgnet.TLRPC.TL_messageEntityItalic;
import org.vidogram.tgnet.TLRPC.TL_messageEntityPre;
import org.vidogram.tgnet.TLRPC.TL_messages_getMessages;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.ui.Components.TypefaceSpan;
import org.vidogram.ui.Components.URLSpanUserMention;

public class MessagesQuery
{
  private static Comparator<TLRPC.MessageEntity> entityComparator = new Comparator()
  {
    public int compare(TLRPC.MessageEntity paramMessageEntity1, TLRPC.MessageEntity paramMessageEntity2)
    {
      if (paramMessageEntity1.offset > paramMessageEntity2.offset)
        return 1;
      if (paramMessageEntity1.offset < paramMessageEntity2.offset)
        return -1;
      return 0;
    }
  };

  private static MessageObject broadcastPinnedMessage(TLRPC.Message paramMessage, ArrayList<TLRPC.User> paramArrayList, ArrayList<TLRPC.Chat> paramArrayList1, boolean paramBoolean1, boolean paramBoolean2)
  {
    HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList.size())
    {
      localObject = (TLRPC.User)paramArrayList.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList1.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList1.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    if (paramBoolean2)
      return new MessageObject(paramMessage, localHashMap, (AbstractMap)localObject, false);
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramBoolean1, paramArrayList1, paramMessage, localHashMap, (HashMap)localObject)
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
        MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedPinnedMessage, new Object[] { new MessageObject(this.val$result, this.val$usersDict, this.val$chatsDict, false) });
      }
    });
    return (MessageObject)null;
  }

  private static void broadcastReplyMessages(ArrayList<TLRPC.Message> paramArrayList, HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, long paramLong, boolean paramBoolean)
  {
    HashMap localHashMap = new HashMap();
    int i = 0;
    while (i < paramArrayList1.size())
    {
      localObject = (TLRPC.User)paramArrayList1.get(i);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      i += 1;
    }
    Object localObject = new HashMap();
    i = 0;
    while (i < paramArrayList2.size())
    {
      TLRPC.Chat localChat = (TLRPC.Chat)paramArrayList2.get(i);
      ((HashMap)localObject).put(Integer.valueOf(localChat.id), localChat);
      i += 1;
    }
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramBoolean, paramArrayList2, paramArrayList, paramHashMap, localHashMap, (HashMap)localObject, paramLong)
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, this.val$isCache);
        MessagesController.getInstance().putChats(this.val$chats, this.val$isCache);
        int j = 0;
        int i = 0;
        if (j < this.val$result.size())
        {
          Object localObject = (TLRPC.Message)this.val$result.get(j);
          ArrayList localArrayList = (ArrayList)this.val$replyMessageOwners.get(Integer.valueOf(((TLRPC.Message)localObject).id));
          if (localArrayList == null)
            break label237;
          localObject = new MessageObject((TLRPC.Message)localObject, this.val$usersDict, this.val$chatsDict, false);
          i = 0;
          if (i < localArrayList.size())
          {
            MessageObject localMessageObject = (MessageObject)localArrayList.get(i);
            localMessageObject.replyMessageObject = ((MessageObject)localObject);
            if ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))
              localMessageObject.generatePinMessageText(null, null);
            while (true)
            {
              i += 1;
              break;
              if ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore))
              {
                localMessageObject.generateGameMessageText(null);
                continue;
              }
              if (!(localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent))
                continue;
              localMessageObject.generatePaymentSentMessageText(null);
            }
          }
          i = 1;
        }
        label237: 
        while (true)
        {
          j += 1;
          break;
          if (i != 0)
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(this.val$dialog_id) });
          return;
        }
      }
    });
  }

  private static boolean checkInclusion(int paramInt, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return false;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
      if (localMessageEntity.offset <= paramInt)
      {
        int k = localMessageEntity.offset;
        if (localMessageEntity.length + k > paramInt)
          return true;
      }
      i += 1;
    }
    return false;
  }

  private static boolean checkIntersection(int paramInt1, int paramInt2, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return false;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
      if (localMessageEntity.offset > paramInt1)
      {
        int k = localMessageEntity.offset;
        if (localMessageEntity.length + k <= paramInt2)
          return true;
      }
      i += 1;
    }
    return false;
  }

  public static ArrayList<TLRPC.MessageEntity> getEntities(CharSequence[] paramArrayOfCharSequence)
  {
    if ((paramArrayOfCharSequence == null) || (paramArrayOfCharSequence[0] == null))
    {
      localObject1 = null;
      return localObject1;
    }
    Object localObject1 = null;
    int i = -1;
    int j = 0;
    int k = 0;
    label25: Object localObject3 = paramArrayOfCharSequence[0];
    Object localObject2;
    if (j == 0)
    {
      localObject2 = "`";
      label38: k = TextUtils.indexOf((CharSequence)localObject3, (CharSequence)localObject2, k);
      if (k == -1)
        break label690;
      if (i != -1)
        break label143;
      if ((paramArrayOfCharSequence[0].length() - k <= 2) || (paramArrayOfCharSequence[0].charAt(k + 1) != '`') || (paramArrayOfCharSequence[0].charAt(k + 2) != '`'))
        break label133;
      i = 1;
      label105: if (i == 0)
        break label138;
    }
    int m;
    label133: label138: for (j = 3; ; j = 1)
    {
      m = k;
      k = j + k;
      j = i;
      i = m;
      break;
      localObject2 = "```";
      break label38;
      i = 0;
      break label105;
    }
    label143: label157: label224: int n;
    label250: Object localObject4;
    if (localObject1 == null)
    {
      localObject1 = new ArrayList();
      if (j != 0);
      for (m = 3; ; m = 1)
      {
        m += k;
        while ((m < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(m) == '`'))
        {
          k += 1;
          m += 1;
        }
      }
      if (j != 0)
      {
        m = 3;
        n = k + m;
        if (j == 0)
          break label588;
        if (i <= 0)
          break label544;
        j = paramArrayOfCharSequence[0].charAt(i - 1);
        if ((j != 32) && (j != 10))
          break label549;
        j = 1;
        label264: localObject2 = paramArrayOfCharSequence[0];
        if (j == 0)
          break label554;
        m = 1;
        label276: localObject2 = TextUtils.substring((CharSequence)localObject2, 0, i - m);
        localObject4 = TextUtils.substring(paramArrayOfCharSequence[0], i + 3, k);
        if (k + 3 >= paramArrayOfCharSequence[0].length())
          break label560;
        m = paramArrayOfCharSequence[0].charAt(k + 3);
        label327: localObject3 = paramArrayOfCharSequence[0];
        if ((m != 32) && (m != 10))
          break label566;
        m = 1;
        label349: localObject3 = TextUtils.substring((CharSequence)localObject3, m + (k + 3), paramArrayOfCharSequence[0].length());
        if (((CharSequence)localObject2).length() == 0)
          break label572;
        localObject2 = TextUtils.concat(new CharSequence[] { localObject2, "\n" });
        label399: if (((CharSequence)localObject3).length() == 0)
          break label1637;
        localObject3 = TextUtils.concat(new CharSequence[] { "\n", localObject3 });
      }
    }
    label477: label1637: 
    while (true)
    {
      if (!TextUtils.isEmpty((CharSequence)localObject4))
      {
        paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { localObject2, localObject4, localObject3 });
        localObject2 = new TLRPC.TL_messageEntityPre();
        if (j != 0)
        {
          m = 0;
          ((TLRPC.TL_messageEntityPre)localObject2).offset = (m + i);
          if (j == 0)
            break label583;
          j = 0;
          label492: ((TLRPC.TL_messageEntityPre)localObject2).length = (j + (k - i - 3));
          ((TLRPC.TL_messageEntityPre)localObject2).language = "";
          ((ArrayList)localObject1).add(localObject2);
          i = n - 6;
        }
      }
      while (true)
      {
        m = -1;
        j = 0;
        k = i;
        i = m;
        break label25;
        m = 1;
        break label224;
        j = 0;
        break label250;
        j = 0;
        break label264;
        m = 0;
        break label276;
        m = 0;
        break label327;
        m = 0;
        break label349;
        j = 1;
        break label399;
        m = 1;
        break label477;
        j = 1;
        break label492;
        if (i + 1 != k)
        {
          paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 1, k), TextUtils.substring(paramArrayOfCharSequence[0], k + 1, paramArrayOfCharSequence[0].length()) });
          localObject2 = new TLRPC.TL_messageEntityCode();
          ((TLRPC.TL_messageEntityCode)localObject2).offset = i;
          ((TLRPC.TL_messageEntityCode)localObject2).length = (k - i - 1);
          ((ArrayList)localObject1).add(localObject2);
          i = n - 2;
          continue;
          localObject2 = localObject1;
          if (i != -1)
          {
            localObject2 = localObject1;
            if (j != 0)
            {
              paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 2, paramArrayOfCharSequence[0].length()) });
              localObject2 = localObject1;
              if (localObject1 == null)
                localObject2 = new ArrayList();
              localObject1 = new TLRPC.TL_messageEntityCode();
              ((TLRPC.TL_messageEntityCode)localObject1).offset = i;
              ((TLRPC.TL_messageEntityCode)localObject1).length = 1;
              ((ArrayList)localObject2).add(localObject1);
            }
          }
          localObject3 = localObject2;
          if ((paramArrayOfCharSequence[0] instanceof Spannable))
          {
            localObject4 = (Spannable)paramArrayOfCharSequence[0];
            localObject3 = (TypefaceSpan[])((Spannable)localObject4).getSpans(0, paramArrayOfCharSequence[0].length(), TypefaceSpan.class);
            localObject1 = localObject2;
            if (localObject3 != null)
            {
              localObject1 = localObject2;
              if (localObject3.length > 0)
              {
                i = 0;
                while (true)
                {
                  localObject1 = localObject2;
                  if (i >= localObject3.length)
                    break;
                  localObject5 = localObject3[i];
                  j = ((Spannable)localObject4).getSpanStart(localObject5);
                  k = ((Spannable)localObject4).getSpanEnd(localObject5);
                  localObject1 = localObject2;
                  if (!checkInclusion(j, (ArrayList)localObject2))
                  {
                    localObject1 = localObject2;
                    if (!checkInclusion(k, (ArrayList)localObject2))
                    {
                      if (!checkIntersection(j, k, (ArrayList)localObject2))
                        break label948;
                      localObject1 = localObject2;
                    }
                  }
                  i += 1;
                  localObject2 = localObject1;
                  continue;
                  localObject1 = localObject2;
                  if (localObject2 == null)
                    localObject1 = new ArrayList();
                  if (((TypefaceSpan)localObject5).isBold());
                  for (localObject2 = new TLRPC.TL_messageEntityBold(); ; localObject2 = new TLRPC.TL_messageEntityItalic())
                  {
                    ((TLRPC.MessageEntity)localObject2).offset = j;
                    ((TLRPC.MessageEntity)localObject2).length = (k - j);
                    ((ArrayList)localObject1).add(localObject2);
                    break;
                  }
                }
              }
            }
            Object localObject5 = (URLSpanUserMention[])((Spannable)localObject4).getSpans(0, paramArrayOfCharSequence[0].length(), URLSpanUserMention.class);
            localObject3 = localObject1;
            if (localObject5 != null)
            {
              localObject3 = localObject1;
              if (localObject5.length > 0)
              {
                localObject2 = localObject1;
                if (localObject1 == null)
                  localObject2 = new ArrayList();
                i = 0;
                while (true)
                {
                  localObject3 = localObject2;
                  if (i >= localObject5.length)
                    break;
                  localObject1 = new TLRPC.TL_inputMessageEntityMentionName();
                  ((TLRPC.TL_inputMessageEntityMentionName)localObject1).user_id = MessagesController.getInputUser(Utilities.parseInt(localObject5[i].getURL()).intValue());
                  if (((TLRPC.TL_inputMessageEntityMentionName)localObject1).user_id != null)
                  {
                    ((TLRPC.TL_inputMessageEntityMentionName)localObject1).offset = ((Spannable)localObject4).getSpanStart(localObject5[i]);
                    ((TLRPC.TL_inputMessageEntityMentionName)localObject1).length = (Math.min(((Spannable)localObject4).getSpanEnd(localObject5[i]), paramArrayOfCharSequence[0].length()) - ((TLRPC.TL_inputMessageEntityMentionName)localObject1).offset);
                    if (paramArrayOfCharSequence[0].charAt(((TLRPC.TL_inputMessageEntityMentionName)localObject1).offset + ((TLRPC.TL_inputMessageEntityMentionName)localObject1).length - 1) == ' ')
                      ((TLRPC.TL_inputMessageEntityMentionName)localObject1).length -= 1;
                    ((ArrayList)localObject2).add(localObject1);
                  }
                  i += 1;
                }
              }
            }
          }
          k = 0;
          while (true)
          {
            localObject1 = localObject3;
            if (k >= 2)
              break;
            j = 0;
            i = -1;
            if (k == 0)
            {
              localObject2 = "**";
              if (k != 0)
                break label1343;
              m = 42;
            }
            label1265: int i1;
            while (true)
            {
              j = TextUtils.indexOf(paramArrayOfCharSequence[0], (CharSequence)localObject2, j);
              if (j == -1)
                break label1621;
              if (i == -1)
              {
                if (j == 0);
                for (i1 = 32; ; i1 = paramArrayOfCharSequence[0].charAt(j - 1))
                {
                  n = i;
                  if (!checkInclusion(j, (ArrayList)localObject3))
                    if (i1 != 32)
                    {
                      n = i;
                      if (i1 != 10);
                    }
                    else
                    {
                      n = j;
                    }
                  j += 2;
                  i = n;
                  break label1265;
                  localObject2 = "__";
                  break;
                  m = 95;
                  break label1265;
                }
              }
              i1 = j + 2;
              n = j;
              j = i1;
              while ((j < paramArrayOfCharSequence[0].length()) && (paramArrayOfCharSequence[0].charAt(j) == m))
              {
                n += 1;
                j += 1;
              }
              i1 = n + 2;
              if ((!checkInclusion(n, (ArrayList)localObject3)) && (!checkIntersection(i, n, (ArrayList)localObject3)))
                break label1451;
              i = -1;
              j = i1;
            }
            j = i1;
            localObject1 = localObject3;
            if (i + 2 != n)
            {
              localObject1 = localObject3;
              if (localObject3 == null)
                localObject1 = new ArrayList();
              paramArrayOfCharSequence[0] = TextUtils.concat(new CharSequence[] { TextUtils.substring(paramArrayOfCharSequence[0], 0, i), TextUtils.substring(paramArrayOfCharSequence[0], i + 2, n), TextUtils.substring(paramArrayOfCharSequence[0], n + 2, paramArrayOfCharSequence[0].length()) });
              if (k != 0)
                break label1609;
            }
            for (localObject3 = new TLRPC.TL_messageEntityBold(); ; localObject3 = new TLRPC.TL_messageEntityItalic())
            {
              ((TLRPC.MessageEntity)localObject3).offset = i;
              ((TLRPC.MessageEntity)localObject3).length = (n - i - 2);
              removeOffsetAfter(((TLRPC.MessageEntity)localObject3).offset + ((TLRPC.MessageEntity)localObject3).length, 4, (ArrayList)localObject1);
              ((ArrayList)localObject1).add(localObject3);
              j = i1 - 4;
              i = -1;
              localObject3 = localObject1;
              break;
            }
            k += 1;
          }
          break label157;
        }
        i = n;
      }
    }
  }

  public static MessageObject loadPinnedMessage(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt1, paramInt2)
      {
        public void run()
        {
          MessagesQuery.access$000(this.val$channelId, this.val$mid, false);
        }
      });
      return null;
    }
    return loadPinnedMessageInternal(paramInt1, paramInt2, true);
  }

  private static MessageObject loadPinnedMessageInternal(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    long l1 = paramInt2;
    long l2 = paramInt1;
    while (true)
    {
      ArrayList localArrayList1;
      ArrayList localArrayList2;
      ArrayList localArrayList3;
      ArrayList localArrayList4;
      Object localObject3;
      try
      {
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        localArrayList3 = new ArrayList();
        localArrayList4 = new ArrayList();
        localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid = %d", new Object[] { Long.valueOf(l1 | l2 << 32) }), new Object[0]);
        if (!((SQLiteCursor)localObject3).next())
          break label439;
        Object localObject4 = ((SQLiteCursor)localObject3).byteBufferValue(0);
        if (localObject4 == null)
          break label439;
        Object localObject1 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject4, ((NativeByteBuffer)localObject4).readInt32(false), false);
        ((NativeByteBuffer)localObject4).reuse();
        ((TLRPC.Message)localObject1).id = ((SQLiteCursor)localObject3).intValue(1);
        ((TLRPC.Message)localObject1).date = ((SQLiteCursor)localObject3).intValue(2);
        ((TLRPC.Message)localObject1).dialog_id = (-paramInt1);
        MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject1, localArrayList3, localArrayList4);
        ((SQLiteCursor)localObject3).dispose();
        localObject3 = localObject1;
        if (localObject1 != null)
          continue;
        localObject4 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data FROM chat_pinned WHERE uid = %d", new Object[] { Integer.valueOf(paramInt1) }), new Object[0]);
        localObject3 = localObject1;
        if (!((SQLiteCursor)localObject4).next())
          continue;
        NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject4).byteBufferValue(0);
        localObject3 = localObject1;
        if (localNativeByteBuffer == null)
          continue;
        localObject3 = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
        localNativeByteBuffer.reuse();
        if (((TLRPC.Message)localObject3).id == paramInt2)
          continue;
        localObject3 = null;
        ((SQLiteCursor)localObject4).dispose();
        if (localObject3 == null)
        {
          localObject1 = new TLRPC.TL_channels_getMessages();
          ((TLRPC.TL_channels_getMessages)localObject1).channel = MessagesController.getInputChannel(paramInt1);
          ((TLRPC.TL_channels_getMessages)localObject1).id.add(Integer.valueOf(paramInt2));
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate(paramInt1)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if (paramTL_error == null)
              {
                paramTLObject = (TLRPC.messages_Messages)paramTLObject;
                if (!paramTLObject.messages.isEmpty())
                {
                  ImageLoader.saveMessagesThumbs(paramTLObject.messages);
                  MessagesQuery.access$100((TLRPC.Message)paramTLObject.messages.get(0), paramTLObject.users, paramTLObject.chats, false, false);
                  MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, paramTLObject.chats, true, true);
                  MessagesQuery.access$200((TLRPC.Message)paramTLObject.messages.get(0));
                }
              }
              for (int i = 1; ; i = 0)
              {
                if (i == 0)
                  MessagesStorage.getInstance().updateChannelPinnedMessage(this.val$channelId, 0);
                return;
              }
            }
          });
          return null;
          ((TLRPC.Message)localObject3).dialog_id = (-paramInt1);
          MessagesStorage.addUsersAndChatsFromMessage((TLRPC.Message)localObject3, localArrayList3, localArrayList4);
          continue;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return null;
      }
      if (paramBoolean)
        return broadcastPinnedMessage((TLRPC.Message)localObject3, localArrayList1, localArrayList2, true, paramBoolean);
      if (!localArrayList3.isEmpty())
        MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList3), localArrayList1);
      if (!localArrayList4.isEmpty())
        MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList4), localArrayList2);
      broadcastPinnedMessage((TLRPC.Message)localObject3, localArrayList1, localArrayList2, true, false);
      return null;
      label439: Object localObject2 = null;
    }
  }

  public static void loadReplyMessagesForMessages(ArrayList<MessageObject> paramArrayList, long paramLong)
  {
    MessageObject localMessageObject;
    Object localObject;
    ArrayList localArrayList2;
    ArrayList localArrayList1;
    if ((int)paramLong == 0)
    {
      localArrayList3 = new ArrayList();
      localHashMap = new HashMap();
      localStringBuilder = new StringBuilder();
      i = 0;
      while (i < paramArrayList.size())
      {
        localMessageObject = (MessageObject)paramArrayList.get(i);
        if ((localMessageObject.isReply()) && (localMessageObject.replyMessageObject == null))
        {
          localObject = Long.valueOf(localMessageObject.messageOwner.reply_to_random_id);
          if (localStringBuilder.length() > 0)
            localStringBuilder.append(',');
          localStringBuilder.append(localObject);
          localArrayList2 = (ArrayList)localHashMap.get(localObject);
          localArrayList1 = localArrayList2;
          if (localArrayList2 == null)
          {
            localArrayList1 = new ArrayList();
            localHashMap.put(localObject, localArrayList1);
          }
          localArrayList1.add(localMessageObject);
          if (!localArrayList3.contains(localObject))
            localArrayList3.add(localObject);
        }
        i += 1;
      }
      if (localArrayList3.isEmpty())
        return;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localArrayList3, paramLong, localHashMap)
      {
        public void run()
        {
          try
          {
            Object localObject1 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT m.data, m.mid, m.date, r.random_id FROM randoms as r INNER JOIN messages as m ON r.mid = m.mid WHERE r.random_id IN(%s)", new Object[] { TextUtils.join(",", this.val$replyMessages) }), new Object[0]);
            Object localObject2;
            int i;
            while (((SQLiteCursor)localObject1).next())
            {
              localObject2 = ((SQLiteCursor)localObject1).byteBufferValue(0);
              if (localObject2 == null)
                continue;
              Object localObject3 = TLRPC.Message.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
              ((NativeByteBuffer)localObject2).reuse();
              ((TLRPC.Message)localObject3).id = ((SQLiteCursor)localObject1).intValue(1);
              ((TLRPC.Message)localObject3).date = ((SQLiteCursor)localObject1).intValue(2);
              ((TLRPC.Message)localObject3).dialog_id = this.val$dialogId;
              localObject2 = (ArrayList)this.val$replyMessageRandomOwners.remove(Long.valueOf(((SQLiteCursor)localObject1).longValue(3)));
              if (localObject2 == null)
                continue;
              localObject3 = new MessageObject((TLRPC.Message)localObject3, null, null, false);
              i = 0;
              while (i < ((ArrayList)localObject2).size())
              {
                MessageObject localMessageObject = (MessageObject)((ArrayList)localObject2).get(i);
                localMessageObject.replyMessageObject = ((MessageObject)localObject3);
                localMessageObject.messageOwner.reply_to_msg_id = ((MessageObject)localObject3).getId();
                i += 1;
              }
            }
            ((SQLiteCursor)localObject1).dispose();
            if (!this.val$replyMessageRandomOwners.isEmpty())
            {
              localObject1 = this.val$replyMessageRandomOwners.entrySet().iterator();
              while (((Iterator)localObject1).hasNext())
              {
                localObject2 = (ArrayList)((Map.Entry)((Iterator)localObject1).next()).getValue();
                i = 0;
                while (i < ((ArrayList)localObject2).size())
                {
                  ((MessageObject)((ArrayList)localObject2).get(i)).messageOwner.reply_to_random_id = 0L;
                  i += 1;
                }
              }
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.didLoadedReplyMessages, new Object[] { Long.valueOf(MessagesQuery.6.this.val$dialogId) });
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
      return;
    }
    ArrayList localArrayList3 = new ArrayList();
    HashMap localHashMap = new HashMap();
    StringBuilder localStringBuilder = new StringBuilder();
    int i = 0;
    int j = 0;
    label241: int k;
    long l;
    if (j < paramArrayList.size())
    {
      localMessageObject = (MessageObject)paramArrayList.get(j);
      k = i;
      if (localMessageObject.getId() > 0)
      {
        k = i;
        if (localMessageObject.isReply())
        {
          k = i;
          if (localMessageObject.replyMessageObject == null)
          {
            localObject = Integer.valueOf(localMessageObject.messageOwner.reply_to_msg_id);
            l = ((Integer)localObject).intValue();
            if (localMessageObject.messageOwner.to_id.channel_id == 0)
              break label499;
            l = localMessageObject.messageOwner.to_id.channel_id << 32 | l;
            i = localMessageObject.messageOwner.to_id.channel_id;
          }
        }
      }
    }
    label499: 
    while (true)
    {
      if (localStringBuilder.length() > 0)
        localStringBuilder.append(',');
      localStringBuilder.append(l);
      localArrayList2 = (ArrayList)localHashMap.get(localObject);
      localArrayList1 = localArrayList2;
      if (localArrayList2 == null)
      {
        localArrayList1 = new ArrayList();
        localHashMap.put(localObject, localArrayList1);
      }
      localArrayList1.add(localMessageObject);
      if (!localArrayList3.contains(localObject))
        localArrayList3.add(localObject);
      k = i;
      j += 1;
      i = k;
      break label241;
      if (localArrayList3.isEmpty())
        break;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localStringBuilder, paramLong, localArrayList3, localHashMap, i)
      {
        public void run()
        {
          do
          {
            ArrayList localArrayList2;
            ArrayList localArrayList3;
            ArrayList localArrayList4;
            ArrayList localArrayList5;
            SQLiteCursor localSQLiteCursor;
            try
            {
              ArrayList localArrayList1 = new ArrayList();
              localArrayList2 = new ArrayList();
              localArrayList3 = new ArrayList();
              localArrayList4 = new ArrayList();
              localArrayList5 = new ArrayList();
              localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid, date FROM messages WHERE mid IN(%s)", new Object[] { this.val$stringBuilder.toString() }), new Object[0]);
              while (localSQLiteCursor.next())
              {
                NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
                if (localNativeByteBuffer == null)
                  continue;
                TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                localNativeByteBuffer.reuse();
                localMessage.id = localSQLiteCursor.intValue(1);
                localMessage.date = localSQLiteCursor.intValue(2);
                localMessage.dialog_id = this.val$dialogId;
                MessagesStorage.addUsersAndChatsFromMessage(localMessage, localArrayList4, localArrayList5);
                localArrayList1.add(localMessage);
                this.val$replyMessages.remove(Integer.valueOf(localMessage.id));
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            localSQLiteCursor.dispose();
            if (!localArrayList4.isEmpty())
              MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList4), localArrayList2);
            if (!localArrayList5.isEmpty())
              MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList5), localArrayList3);
            MessagesQuery.access$300(localException, this.val$replyMessageOwners, localArrayList2, localArrayList3, this.val$dialogId, true);
          }
          while (this.val$replyMessages.isEmpty());
          if (this.val$channelIdFinal != 0)
          {
            localObject = new TLRPC.TL_channels_getMessages();
            ((TLRPC.TL_channels_getMessages)localObject).channel = MessagesController.getInputChannel(this.val$channelIdFinal);
            ((TLRPC.TL_channels_getMessages)localObject).id = this.val$replyMessages;
            ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
            {
              public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
              {
                if (paramTL_error == null)
                {
                  paramTLObject = (TLRPC.messages_Messages)paramTLObject;
                  ImageLoader.saveMessagesThumbs(paramTLObject.messages);
                  MessagesQuery.access$300(paramTLObject.messages, MessagesQuery.7.this.val$replyMessageOwners, paramTLObject.users, paramTLObject.chats, MessagesQuery.7.this.val$dialogId, false);
                  MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, paramTLObject.chats, true, true);
                  MessagesQuery.access$400(MessagesQuery.7.this.val$replyMessageOwners, paramTLObject.messages);
                }
              }
            });
            return;
          }
          Object localObject = new TLRPC.TL_messages_getMessages();
          ((TLRPC.TL_messages_getMessages)localObject).id = this.val$replyMessages;
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              if (paramTL_error == null)
              {
                paramTLObject = (TLRPC.messages_Messages)paramTLObject;
                ImageLoader.saveMessagesThumbs(paramTLObject.messages);
                MessagesQuery.access$300(paramTLObject.messages, MessagesQuery.7.this.val$replyMessageOwners, paramTLObject.users, paramTLObject.chats, MessagesQuery.7.this.val$dialogId, false);
                MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, paramTLObject.chats, true, true);
                MessagesQuery.access$400(MessagesQuery.7.this.val$replyMessageOwners, paramTLObject.messages);
              }
            }
          });
        }
      });
      return;
    }
  }

  private static void removeOffsetAfter(int paramInt1, int paramInt2, ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramArrayList.get(i);
      if (localMessageEntity.offset > paramInt1)
        localMessageEntity.offset -= paramInt2;
      i += 1;
    }
  }

  private static void savePinnedMessage(TLRPC.Message paramMessage)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramMessage)
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_pinned VALUES(?, ?, ?)");
          NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(this.val$result.getObjectSize());
          this.val$result.serializeToStream(localNativeByteBuffer);
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$result.to_id.channel_id);
          localSQLitePreparedStatement.bindInteger(2, this.val$result.id);
          localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer);
          localSQLitePreparedStatement.step();
          localNativeByteBuffer.reuse();
          localSQLitePreparedStatement.dispose();
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  private static void saveReplyMessages(HashMap<Integer, ArrayList<MessageObject>> paramHashMap, ArrayList<TLRPC.Message> paramArrayList)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList, paramHashMap)
    {
      public void run()
      {
        while (true)
        {
          int i;
          try
          {
            MessagesStorage.getInstance().getDatabase().beginTransaction();
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE messages SET replydata = ? WHERE mid = ?");
            i = 0;
            if (i >= this.val$result.size())
              continue;
            Object localObject = (TLRPC.Message)this.val$result.get(i);
            ArrayList localArrayList = (ArrayList)this.val$replyMessageOwners.get(Integer.valueOf(((TLRPC.Message)localObject).id));
            if (localArrayList != null)
            {
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(((TLRPC.Message)localObject).getObjectSize());
              ((TLRPC.Message)localObject).serializeToStream(localNativeByteBuffer);
              int j = 0;
              if (j >= localArrayList.size())
                continue;
              localObject = (MessageObject)localArrayList.get(j);
              localSQLitePreparedStatement.requery();
              long l2 = ((MessageObject)localObject).getId();
              long l1 = l2;
              if (((MessageObject)localObject).messageOwner.to_id.channel_id == 0)
                continue;
              l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
              localSQLitePreparedStatement.bindByteBuffer(1, localNativeByteBuffer);
              localSQLitePreparedStatement.bindLong(2, l1);
              localSQLitePreparedStatement.step();
              j += 1;
              continue;
              localNativeByteBuffer.reuse();
              break label224;
              localSQLitePreparedStatement.dispose();
              MessagesStorage.getInstance().getDatabase().commitTransaction();
              return;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          label224: i += 1;
        }
      }
    });
  }

  public static void sortEntities(ArrayList<TLRPC.MessageEntity> paramArrayList)
  {
    Collections.sort(paramArrayList, entityComparator);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.MessagesQuery
 * JD-Core Version:    0.6.0
 */