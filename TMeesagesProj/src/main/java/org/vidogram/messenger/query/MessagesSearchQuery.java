package org.vidogram.messenger.query;

import android.text.TextUtils;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.InputPeer;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterEmpty;
import org.vidogram.tgnet.TLRPC.TL_messages_messagesSlice;
import org.vidogram.tgnet.TLRPC.TL_messages_search;
import org.vidogram.tgnet.TLRPC.messages_Messages;

public class MessagesSearchQuery
{
  private static long lastMergeDialogId;
  private static int lastReqId;
  private static int lastReturnedNum;
  private static String lastSearchQuery;
  private static int mergeReqId;
  private static int[] messagesSearchCount = { 0, 0 };
  private static boolean[] messagesSearchEndReached = { 0, 0 };
  private static int reqId;
  private static ArrayList<MessageObject> searchResultMessages = new ArrayList();

  public static String getLastSearchQuery()
  {
    return lastSearchQuery;
  }

  private static int getMask()
  {
    int i = 0;
    if ((lastReturnedNum < searchResultMessages.size() - 1) || (messagesSearchEndReached[0] == 0) || (messagesSearchEndReached[1] == 0))
      i = 1;
    int j = i;
    if (lastReturnedNum > 0)
      j = i | 0x2;
    return j;
  }

  public static void searchMessagesInChat(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2)
  {
    searchMessagesInChat(paramString, paramLong1, paramLong2, paramInt1, paramInt2, false);
  }

  private static void searchMessagesInChat(String paramString, long paramLong1, long paramLong2, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 0;
    if (!paramBoolean)
    {
      i = 1;
      if (reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(reqId, true);
        reqId = 0;
      }
      if (mergeReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(mergeReqId, true);
        mergeReqId = 0;
      }
      if (!TextUtils.isEmpty(paramString))
        break label579;
      if (!searchResultMessages.isEmpty())
        break label74;
    }
    label74: Object localObject;
    long l;
    label273: label284: label309: 
    do
    {
      return;
      i = 0;
      break;
      if (paramInt2 != 1)
        break label440;
      lastReturnedNum += 1;
      if (lastReturnedNum < searchResultMessages.size())
      {
        paramString = (MessageObject)searchResultMessages.get(lastReturnedNum);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]) });
        return;
      }
      if ((messagesSearchEndReached[0] != 0) && (paramLong2 == 0L) && (messagesSearchEndReached[1] != 0))
      {
        lastReturnedNum -= 1;
        return;
      }
      paramString = lastSearchQuery;
      localObject = (MessageObject)searchResultMessages.get(searchResultMessages.size() - 1);
      if ((((MessageObject)localObject).getDialogId() != paramLong1) || (messagesSearchEndReached[0] != 0))
        break label411;
      i = ((MessageObject)localObject).getId();
      l = paramLong1;
      int k = 0;
      j = i;
      i = k;
      if ((messagesSearchEndReached[0] == 0) || (messagesSearchEndReached[1] != 0) || (paramLong2 == 0L))
        break label750;
      l = paramLong2;
      if ((l != paramLong1) || (i == 0))
        break label644;
      if (paramLong2 == 0L)
        break label628;
      localObject = MessagesController.getInputPeer((int)paramLong2);
    }
    while (localObject == null);
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.peer = ((TLRPC.InputPeer)localObject);
    lastMergeDialogId = paramLong2;
    localTL_messages_search.limit = 1;
    localTL_messages_search.q = paramString;
    localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
    mergeReqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate(paramLong2, localTL_messages_search, paramLong1, paramInt1, paramInt2)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
        {
          public void run()
          {
            TLRPC.messages_Messages localmessages_Messages;
            int[] arrayOfInt;
            if (MessagesSearchQuery.lastMergeDialogId == MessagesSearchQuery.1.this.val$mergeDialogId)
            {
              MessagesSearchQuery.access$102(0);
              if (this.val$response != null)
              {
                localmessages_Messages = (TLRPC.messages_Messages)this.val$response;
                MessagesSearchQuery.messagesSearchEndReached[1] = localmessages_Messages.messages.isEmpty();
                arrayOfInt = MessagesSearchQuery.messagesSearchCount;
                if (!(localmessages_Messages instanceof TLRPC.TL_messages_messagesSlice))
                  break label109;
              }
            }
            label109: for (int i = localmessages_Messages.count; ; i = localmessages_Messages.messages.size())
            {
              arrayOfInt[1] = i;
              MessagesSearchQuery.access$400(MessagesSearchQuery.1.this.val$req.q, MessagesSearchQuery.1.this.val$dialog_id, MessagesSearchQuery.1.this.val$mergeDialogId, MessagesSearchQuery.1.this.val$guid, MessagesSearchQuery.1.this.val$direction, true);
              return;
            }
          }
        });
      }
    }
    , 2);
    return;
    label411: if (((MessageObject)localObject).getDialogId() == paramLong2);
    for (int i = ((MessageObject)localObject).getId(); ; i = 0)
    {
      messagesSearchEndReached[1] = false;
      l = paramLong2;
      break label273;
      label440: if (paramInt2 != 2)
        break;
      lastReturnedNum -= 1;
      if (lastReturnedNum < 0)
      {
        lastReturnedNum = 0;
        return;
      }
      if (lastReturnedNum >= searchResultMessages.size())
        lastReturnedNum = searchResultMessages.size() - 1;
      paramString = (MessageObject)searchResultMessages.get(lastReturnedNum);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(paramInt1), Integer.valueOf(paramString.getId()), Integer.valueOf(getMask()), Long.valueOf(paramString.getDialogId()), Integer.valueOf(lastReturnedNum), Integer.valueOf(messagesSearchCount[0] + messagesSearchCount[1]) });
      return;
      label579: if (i != 0)
      {
        localObject = messagesSearchEndReached;
        messagesSearchEndReached[1] = false;
        localObject[0] = 0;
        localObject = messagesSearchCount;
        messagesSearchCount[1] = 0;
        localObject[0] = 0;
        searchResultMessages.clear();
      }
      l = paramLong1;
      break label284;
      label628: lastMergeDialogId = 0L;
      messagesSearchEndReached[1] = true;
      messagesSearchCount[1] = 0;
      label644: localObject = new TLRPC.TL_messages_search();
      ((TLRPC.TL_messages_search)localObject).peer = MessagesController.getInputPeer((int)l);
      if (((TLRPC.TL_messages_search)localObject).peer == null)
        break;
      ((TLRPC.TL_messages_search)localObject).limit = 21;
      ((TLRPC.TL_messages_search)localObject).q = paramString;
      ((TLRPC.TL_messages_search)localObject).max_id = j;
      ((TLRPC.TL_messages_search)localObject).filter = new TLRPC.TL_inputMessagesFilterEmpty();
      paramInt2 = lastReqId + 1;
      lastReqId = paramInt2;
      lastSearchQuery = paramString;
      reqId = ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramInt2, (TLRPC.TL_messages_search)localObject, l, paramLong1, paramInt1, paramLong2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              Object localObject1;
              int j;
              int i;
              int m;
              label227: int k;
              if (MessagesSearchQuery.2.this.val$currentReqId == MessagesSearchQuery.lastReqId)
              {
                MessagesSearchQuery.access$602(0);
                if (this.val$response != null)
                {
                  localObject1 = (TLRPC.messages_Messages)this.val$response;
                  MessagesStorage.getInstance().putUsersAndChats(((TLRPC.messages_Messages)localObject1).users, ((TLRPC.messages_Messages)localObject1).chats, true, true);
                  MessagesController.getInstance().putUsers(((TLRPC.messages_Messages)localObject1).users, false);
                  MessagesController.getInstance().putChats(((TLRPC.messages_Messages)localObject1).chats, false);
                  if ((MessagesSearchQuery.2.this.val$req.max_id == 0) && (MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id))
                  {
                    MessagesSearchQuery.access$702(0);
                    MessagesSearchQuery.searchResultMessages.clear();
                    MessagesSearchQuery.messagesSearchCount[0] = 0;
                  }
                  j = 0;
                  for (i = 0; j < Math.min(((TLRPC.messages_Messages)localObject1).messages.size(), 20); i = 1)
                  {
                    localObject2 = (TLRPC.Message)((TLRPC.messages_Messages)localObject1).messages.get(j);
                    MessagesSearchQuery.searchResultMessages.add(new MessageObject((TLRPC.Message)localObject2, null, false));
                    j += 1;
                  }
                  Object localObject2 = MessagesSearchQuery.messagesSearchEndReached;
                  if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id)
                    break label426;
                  j = 0;
                  if (((TLRPC.messages_Messages)localObject1).messages.size() == 21)
                    break label431;
                  m = 1;
                  localObject2[j] = m;
                  localObject2 = MessagesSearchQuery.messagesSearchCount;
                  if (MessagesSearchQuery.2.this.val$queryWithDialogFinal != MessagesSearchQuery.2.this.val$dialog_id)
                    break label437;
                  j = 0;
                  label258: if (!(localObject1 instanceof TLRPC.TL_messages_messagesSlice))
                    break label442;
                  k = ((TLRPC.messages_Messages)localObject1).count;
                  label272: localObject2[j] = k;
                  if (!MessagesSearchQuery.searchResultMessages.isEmpty())
                    break label454;
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(0), Integer.valueOf(MessagesSearchQuery.access$900()), Long.valueOf(0L), Integer.valueOf(0), Integer.valueOf(0) });
                }
              }
              while (true)
              {
                if ((MessagesSearchQuery.2.this.val$queryWithDialogFinal == MessagesSearchQuery.2.this.val$dialog_id) && (MessagesSearchQuery.messagesSearchEndReached[0] != 0) && (MessagesSearchQuery.2.this.val$mergeDialogId != 0L) && (MessagesSearchQuery.messagesSearchEndReached[1] == 0))
                  MessagesSearchQuery.access$400(MessagesSearchQuery.lastSearchQuery, MessagesSearchQuery.2.this.val$dialog_id, MessagesSearchQuery.2.this.val$mergeDialogId, MessagesSearchQuery.2.this.val$guid, 0, true);
                return;
                label426: j = 1;
                break;
                label431: m = 0;
                break label227;
                label437: j = 1;
                break label258;
                label442: k = ((TLRPC.messages_Messages)localObject1).messages.size();
                break label272;
                label454: if (i == 0)
                  continue;
                if (MessagesSearchQuery.lastReturnedNum >= MessagesSearchQuery.searchResultMessages.size())
                  MessagesSearchQuery.access$702(MessagesSearchQuery.searchResultMessages.size() - 1);
                localObject1 = (MessageObject)MessagesSearchQuery.searchResultMessages.get(MessagesSearchQuery.lastReturnedNum);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.chatSearchResultsAvailable, new Object[] { Integer.valueOf(MessagesSearchQuery.2.this.val$guid), Integer.valueOf(((MessageObject)localObject1).getId()), Integer.valueOf(MessagesSearchQuery.access$900()), Long.valueOf(((MessageObject)localObject1).getDialogId()), Integer.valueOf(MessagesSearchQuery.access$700()), Integer.valueOf(MessagesSearchQuery.access$300()[0] + MessagesSearchQuery.access$300()[1]) });
              }
            }
          });
        }
      }
      , 2);
      return;
      label750: break label309;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.MessagesSearchQuery
 * JD-Core Version:    0.6.0
 */