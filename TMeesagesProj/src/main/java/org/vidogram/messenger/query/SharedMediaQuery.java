package org.vidogram.messenger.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterDocument;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterMusic;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterPhotoVideo;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterUrl;
import org.vidogram.tgnet.TLRPC.TL_inputMessagesFilterVoice;
import org.vidogram.tgnet.TLRPC.TL_messageEntityEmail;
import org.vidogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.vidogram.tgnet.TLRPC.TL_messageEntityUrl;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_message_secret;
import org.vidogram.tgnet.TLRPC.TL_messages_messages;
import org.vidogram.tgnet.TLRPC.TL_messages_search;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.messages_Messages;

public class SharedMediaQuery
{
  public static final int MEDIA_AUDIO = 2;
  public static final int MEDIA_FILE = 1;
  public static final int MEDIA_MUSIC = 4;
  public static final int MEDIA_PHOTOVIDEO = 0;
  public static final int MEDIA_TYPES_COUNT = 5;
  public static final int MEDIA_URL = 3;

  public static boolean canAddMessageToMedia(TLRPC.Message paramMessage)
  {
    if (((paramMessage instanceof TLRPC.TL_message_secret)) && ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.ttl != 0) && (paramMessage.ttl <= 60));
    while (true)
    {
      return false;
      if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) || (((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (!MessageObject.isGifDocument(paramMessage.media.document))))
        return true;
      if (paramMessage.entities.isEmpty())
        continue;
      int i = 0;
      while (i < paramMessage.entities.size())
      {
        TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(i);
        if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)))
          return true;
        i += 1;
      }
    }
  }

  public static void getMediaCount(long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = (int)paramLong;
    if ((paramBoolean) || (i == 0))
    {
      getMediaCountDatabase(paramLong, paramInt1, paramInt2);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.offset = 0;
    localTL_messages_search.limit = 1;
    localTL_messages_search.max_id = 0;
    if (paramInt1 == 0)
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    while (true)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInputPeer(i);
      if (localTL_messages_search.peer == null)
        break;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate(paramLong, paramInt1, paramInt2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.messages_Messages)paramTLObject;
            MessagesStorage.getInstance().putUsersAndChats(paramTLObject.users, paramTLObject.chats, true, true);
            if (!(paramTLObject instanceof TLRPC.TL_messages_messages))
              break label70;
          }
          label70: for (int i = paramTLObject.messages.size(); ; i = paramTLObject.count)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                MessagesController.getInstance().putUsers(this.val$res.users, false);
                MessagesController.getInstance().putChats(this.val$res.chats, false);
              }
            });
            SharedMediaQuery.access$100(i, this.val$uid, this.val$type, this.val$classGuid, false);
            return;
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt2);
      return;
      if (paramInt1 == 1)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
        continue;
      }
      if (paramInt1 == 2)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
        continue;
      }
      if (paramInt1 == 3)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
        continue;
      }
      if (paramInt1 != 4)
        continue;
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
    }
  }

  private static void getMediaCountDatabase(long paramLong, int paramInt1, int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, paramInt1, paramInt2)
    {
      public void run()
      {
        while (true)
        {
          try
          {
            SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT count FROM media_counts_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(this.val$type) }), new Object[0]);
            if (localSQLiteCursor.next())
            {
              i = localSQLiteCursor.intValue(0);
              localSQLiteCursor.dispose();
              int k = (int)this.val$uid;
              int j = i;
              if (i != -1)
                continue;
              j = i;
              if (k != 0)
                continue;
              localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM media_v2 WHERE uid = %d AND type = %d LIMIT 1", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(this.val$type) }), new Object[0]);
              if (!localSQLiteCursor.next())
                continue;
              i = localSQLiteCursor.intValue(0);
              localSQLiteCursor.dispose();
              j = i;
              if (i == -1)
                continue;
              SharedMediaQuery.access$200(this.val$uid, this.val$type, i);
              j = i;
              SharedMediaQuery.access$100(j, this.val$uid, this.val$type, this.val$classGuid, true);
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

  public static int getMediaType(TLRPC.Message paramMessage)
  {
    int j = 0;
    int i;
    if (paramMessage == null)
      i = -1;
    while (true)
    {
      return i;
      i = j;
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
        continue;
      if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
        break;
      if (MessageObject.isVoiceMessage(paramMessage))
        return 2;
      i = j;
      if (MessageObject.isVideoMessage(paramMessage))
        continue;
      if (MessageObject.isStickerMessage(paramMessage))
        return -1;
      if (MessageObject.isMusicMessage(paramMessage))
        return 4;
      return 1;
    }
    if (!paramMessage.entities.isEmpty())
    {
      i = 0;
      while (i < paramMessage.entities.size())
      {
        TLRPC.MessageEntity localMessageEntity = (TLRPC.MessageEntity)paramMessage.entities.get(i);
        if (((localMessageEntity instanceof TLRPC.TL_messageEntityUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl)) || ((localMessageEntity instanceof TLRPC.TL_messageEntityEmail)))
          return 3;
        i += 1;
      }
    }
    return -1;
  }

  public static void loadMedia(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean, int paramInt5)
  {
    if (((int)paramLong < 0) && (ChatObject.isChannel(-(int)paramLong)));
    int i;
    for (boolean bool = true; ; bool = false)
    {
      i = (int)paramLong;
      if ((!paramBoolean) && (i != 0))
        break;
      loadMediaDatabase(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, bool);
      return;
    }
    TLRPC.TL_messages_search localTL_messages_search = new TLRPC.TL_messages_search();
    localTL_messages_search.offset = paramInt1;
    localTL_messages_search.limit = (paramInt2 + 1);
    localTL_messages_search.max_id = paramInt3;
    if (paramInt4 == 0)
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterPhotoVideo();
    while (true)
    {
      localTL_messages_search.q = "";
      localTL_messages_search.peer = MessagesController.getInputPeer(i);
      if (localTL_messages_search.peer == null)
        break;
      paramInt1 = ConnectionsManager.getInstance().sendRequest(localTL_messages_search, new RequestDelegate(paramInt2, paramLong, paramInt1, paramInt3, paramInt4, paramInt5, bool)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          if (paramTL_error == null)
          {
            paramTLObject = (TLRPC.messages_Messages)paramTLObject;
            if (paramTLObject.messages.size() <= this.val$count)
              break label77;
            paramTLObject.messages.remove(paramTLObject.messages.size() - 1);
          }
          label77: for (boolean bool = false; ; bool = true)
          {
            SharedMediaQuery.access$000(paramTLObject, this.val$uid, this.val$offset, this.val$count, this.val$max_id, this.val$type, false, this.val$classGuid, this.val$isChannel, bool);
            return;
          }
        }
      });
      ConnectionsManager.getInstance().bindRequestToGuid(paramInt1, paramInt5);
      return;
      if (paramInt4 == 1)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterDocument();
        continue;
      }
      if (paramInt4 == 2)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterVoice();
        continue;
      }
      if (paramInt4 == 3)
      {
        localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterUrl();
        continue;
      }
      if (paramInt4 != 4)
        continue;
      localTL_messages_search.filter = new TLRPC.TL_inputMessagesFilterMusic();
    }
  }

  private static void loadMediaDatabase(long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt2, paramLong, paramInt3, paramBoolean, paramInt4, paramInt1, paramInt5)
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: new 46	org/vidogram/tgnet/TLRPC$TL_messages_messages
        //   3: dup
        //   4: invokespecial 47	org/vidogram/tgnet/TLRPC$TL_messages_messages:<init>	()V
        //   7: astore 12
        //   9: new 49	java/util/ArrayList
        //   12: dup
        //   13: invokespecial 50	java/util/ArrayList:<init>	()V
        //   16: astore 13
        //   18: new 49	java/util/ArrayList
        //   21: dup
        //   22: invokespecial 50	java/util/ArrayList:<init>	()V
        //   25: astore 14
        //   27: aload_0
        //   28: getfield 25	org/vidogram/messenger/query/SharedMediaQuery$7:val$count	I
        //   31: iconst_1
        //   32: iadd
        //   33: istore_2
        //   34: invokestatic 56	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
        //   37: invokevirtual 60	org/vidogram/messenger/MessagesStorage:getDatabase	()Lorg/vidogram/SQLite/SQLiteDatabase;
        //   40: astore 11
        //   42: iconst_0
        //   43: istore 4
        //   45: aload_0
        //   46: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   49: l2i
        //   50: ifeq +940 -> 990
        //   53: iconst_0
        //   54: istore_1
        //   55: aload_0
        //   56: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   59: i2l
        //   60: lstore 7
        //   62: aload_0
        //   63: getfield 31	org/vidogram/messenger/query/SharedMediaQuery$7:val$isChannel	Z
        //   66: ifeq +1243 -> 1309
        //   69: aload_0
        //   70: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   73: l2i
        //   74: ineg
        //   75: istore_1
        //   76: goto +1233 -> 1309
        //   79: aload 11
        //   81: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   84: ldc 68
        //   86: iconst_2
        //   87: anewarray 4	java/lang/Object
        //   90: dup
        //   91: iconst_0
        //   92: aload_0
        //   93: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   96: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   99: aastore
        //   100: dup
        //   101: iconst_1
        //   102: aload_0
        //   103: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   106: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   109: aastore
        //   110: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   113: iconst_0
        //   114: anewarray 4	java/lang/Object
        //   117: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   120: astore 15
        //   122: aload 15
        //   124: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   127: ifeq +401 -> 528
        //   130: aload 15
        //   132: iconst_0
        //   133: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   136: iconst_1
        //   137: if_icmpne +385 -> 522
        //   140: iconst_1
        //   141: istore 4
        //   143: aload 15
        //   145: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   148: lload 7
        //   150: lconst_0
        //   151: lcmp
        //   152: ifeq +611 -> 763
        //   155: lconst_0
        //   156: lstore 5
        //   158: aload 11
        //   160: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   163: ldc 106
        //   165: iconst_3
        //   166: anewarray 4	java/lang/Object
        //   169: dup
        //   170: iconst_0
        //   171: aload_0
        //   172: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   175: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   178: aastore
        //   179: dup
        //   180: iconst_1
        //   181: aload_0
        //   182: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   185: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   188: aastore
        //   189: dup
        //   190: iconst_2
        //   191: aload_0
        //   192: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   195: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   198: aastore
        //   199: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   202: iconst_0
        //   203: anewarray 4	java/lang/Object
        //   206: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   209: astore 15
        //   211: aload 15
        //   213: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   216: ifeq +30 -> 246
        //   219: aload 15
        //   221: iconst_0
        //   222: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   225: i2l
        //   226: lstore 9
        //   228: lload 9
        //   230: lstore 5
        //   232: iload_1
        //   233: ifeq +13 -> 246
        //   236: lload 9
        //   238: iload_1
        //   239: i2l
        //   240: bipush 32
        //   242: lshl
        //   243: lor
        //   244: lstore 5
        //   246: aload 15
        //   248: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   251: lload 5
        //   253: lconst_1
        //   254: lcmp
        //   255: ifle +447 -> 702
        //   258: aload 11
        //   260: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   263: ldc 108
        //   265: iconst_5
        //   266: anewarray 4	java/lang/Object
        //   269: dup
        //   270: iconst_0
        //   271: aload_0
        //   272: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   275: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   278: aastore
        //   279: dup
        //   280: iconst_1
        //   281: lload 7
        //   283: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   286: aastore
        //   287: dup
        //   288: iconst_2
        //   289: lload 5
        //   291: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   294: aastore
        //   295: dup
        //   296: iconst_3
        //   297: aload_0
        //   298: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   301: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   304: aastore
        //   305: dup
        //   306: iconst_4
        //   307: iload_2
        //   308: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   311: aastore
        //   312: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   315: iconst_0
        //   316: anewarray 4	java/lang/Object
        //   319: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   322: astore 11
        //   324: aload 11
        //   326: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   329: ifeq +832 -> 1161
        //   332: aload 11
        //   334: iconst_0
        //   335: invokevirtual 112	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
        //   338: astore 15
        //   340: aload 15
        //   342: ifnull -18 -> 324
        //   345: aload 15
        //   347: aload 15
        //   349: iconst_0
        //   350: invokevirtual 118	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
        //   353: iconst_0
        //   354: invokestatic 124	org/vidogram/tgnet/TLRPC$Message:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$Message;
        //   357: astore 16
        //   359: aload 15
        //   361: invokevirtual 127	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
        //   364: aload 16
        //   366: aload 11
        //   368: iconst_1
        //   369: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   372: putfield 130	org/vidogram/tgnet/TLRPC$Message:id	I
        //   375: aload 16
        //   377: aload_0
        //   378: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   381: putfield 133	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
        //   384: aload_0
        //   385: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   388: l2i
        //   389: ifne +14 -> 403
        //   392: aload 16
        //   394: aload 11
        //   396: iconst_2
        //   397: invokevirtual 137	org/vidogram/SQLite/SQLiteCursor:longValue	(I)J
        //   400: putfield 140	org/vidogram/tgnet/TLRPC$Message:random_id	J
        //   403: aload 12
        //   405: getfield 144	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   408: aload 16
        //   410: invokevirtual 148	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   413: pop
        //   414: aload 16
        //   416: getfield 151	org/vidogram/tgnet/TLRPC$Message:from_id	I
        //   419: ifle +707 -> 1126
        //   422: aload 13
        //   424: aload 16
        //   426: getfield 151	org/vidogram/tgnet/TLRPC$Message:from_id	I
        //   429: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   432: invokevirtual 154	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   435: ifne -111 -> 324
        //   438: aload 13
        //   440: aload 16
        //   442: getfield 151	org/vidogram/tgnet/TLRPC$Message:from_id	I
        //   445: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   448: invokevirtual 148	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   451: pop
        //   452: goto -128 -> 324
        //   455: astore 11
        //   457: aload 12
        //   459: getfield 144	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   462: invokevirtual 157	java/util/ArrayList:clear	()V
        //   465: aload 12
        //   467: getfield 160	org/vidogram/tgnet/TLRPC$TL_messages_messages:chats	Ljava/util/ArrayList;
        //   470: invokevirtual 157	java/util/ArrayList:clear	()V
        //   473: aload 12
        //   475: getfield 163	org/vidogram/tgnet/TLRPC$TL_messages_messages:users	Ljava/util/ArrayList;
        //   478: invokevirtual 157	java/util/ArrayList:clear	()V
        //   481: aload 11
        //   483: invokestatic 169	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   486: aload 12
        //   488: aload_0
        //   489: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   492: aload_0
        //   493: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   496: aload_0
        //   497: getfield 25	org/vidogram/messenger/query/SharedMediaQuery$7:val$count	I
        //   500: aload_0
        //   501: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   504: aload_0
        //   505: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   508: iconst_1
        //   509: aload_0
        //   510: getfield 37	org/vidogram/messenger/query/SharedMediaQuery$7:val$classGuid	I
        //   513: aload_0
        //   514: getfield 31	org/vidogram/messenger/query/SharedMediaQuery$7:val$isChannel	Z
        //   517: iconst_0
        //   518: invokestatic 173	org/vidogram/messenger/query/SharedMediaQuery:access$000	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIIZIZZ)V
        //   521: return
        //   522: iconst_0
        //   523: istore 4
        //   525: goto -382 -> 143
        //   528: aload 15
        //   530: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   533: aload 11
        //   535: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   538: ldc 175
        //   540: iconst_2
        //   541: anewarray 4	java/lang/Object
        //   544: dup
        //   545: iconst_0
        //   546: aload_0
        //   547: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   550: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   553: aastore
        //   554: dup
        //   555: iconst_1
        //   556: aload_0
        //   557: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   560: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   563: aastore
        //   564: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   567: iconst_0
        //   568: anewarray 4	java/lang/Object
        //   571: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   574: astore 15
        //   576: aload 15
        //   578: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   581: ifeq +73 -> 654
        //   584: aload 15
        //   586: iconst_0
        //   587: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   590: istore_3
        //   591: iload_3
        //   592: ifeq +62 -> 654
        //   595: aload 11
        //   597: ldc 177
        //   599: invokevirtual 181	org/vidogram/SQLite/SQLiteDatabase:executeFast	(Ljava/lang/String;)Lorg/vidogram/SQLite/SQLitePreparedStatement;
        //   602: astore 16
        //   604: aload 16
        //   606: invokevirtual 186	org/vidogram/SQLite/SQLitePreparedStatement:requery	()V
        //   609: aload 16
        //   611: iconst_1
        //   612: aload_0
        //   613: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   616: invokevirtual 190	org/vidogram/SQLite/SQLitePreparedStatement:bindLong	(IJ)V
        //   619: aload 16
        //   621: iconst_2
        //   622: aload_0
        //   623: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   626: invokevirtual 194	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
        //   629: aload 16
        //   631: iconst_3
        //   632: iconst_0
        //   633: invokevirtual 194	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
        //   636: aload 16
        //   638: iconst_4
        //   639: iload_3
        //   640: invokevirtual 194	org/vidogram/SQLite/SQLitePreparedStatement:bindInteger	(II)V
        //   643: aload 16
        //   645: invokevirtual 198	org/vidogram/SQLite/SQLitePreparedStatement:step	()I
        //   648: pop
        //   649: aload 16
        //   651: invokevirtual 199	org/vidogram/SQLite/SQLitePreparedStatement:dispose	()V
        //   654: aload 15
        //   656: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   659: goto -511 -> 148
        //   662: astore 11
        //   664: aload 12
        //   666: aload_0
        //   667: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   670: aload_0
        //   671: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   674: aload_0
        //   675: getfield 25	org/vidogram/messenger/query/SharedMediaQuery$7:val$count	I
        //   678: aload_0
        //   679: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   682: aload_0
        //   683: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   686: iconst_1
        //   687: aload_0
        //   688: getfield 37	org/vidogram/messenger/query/SharedMediaQuery$7:val$classGuid	I
        //   691: aload_0
        //   692: getfield 31	org/vidogram/messenger/query/SharedMediaQuery$7:val$isChannel	Z
        //   695: iconst_0
        //   696: invokestatic 173	org/vidogram/messenger/query/SharedMediaQuery:access$000	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIIZIZZ)V
        //   699: aload 11
        //   701: athrow
        //   702: aload 11
        //   704: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   707: ldc 201
        //   709: iconst_4
        //   710: anewarray 4	java/lang/Object
        //   713: dup
        //   714: iconst_0
        //   715: aload_0
        //   716: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   719: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   722: aastore
        //   723: dup
        //   724: iconst_1
        //   725: lload 7
        //   727: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   730: aastore
        //   731: dup
        //   732: iconst_2
        //   733: aload_0
        //   734: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   737: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   740: aastore
        //   741: dup
        //   742: iconst_3
        //   743: iload_2
        //   744: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   747: aastore
        //   748: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   751: iconst_0
        //   752: anewarray 4	java/lang/Object
        //   755: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   758: astore 11
        //   760: goto -436 -> 324
        //   763: lconst_0
        //   764: lstore 5
        //   766: aload 11
        //   768: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   771: ldc 203
        //   773: iconst_2
        //   774: anewarray 4	java/lang/Object
        //   777: dup
        //   778: iconst_0
        //   779: aload_0
        //   780: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   783: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   786: aastore
        //   787: dup
        //   788: iconst_1
        //   789: aload_0
        //   790: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   793: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   796: aastore
        //   797: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   800: iconst_0
        //   801: anewarray 4	java/lang/Object
        //   804: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   807: astore 15
        //   809: aload 15
        //   811: invokevirtual 97	org/vidogram/SQLite/SQLiteCursor:next	()Z
        //   814: ifeq +30 -> 844
        //   817: aload 15
        //   819: iconst_0
        //   820: invokevirtual 101	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
        //   823: i2l
        //   824: lstore 7
        //   826: lload 7
        //   828: lstore 5
        //   830: iload_1
        //   831: ifeq +13 -> 844
        //   834: lload 7
        //   836: iload_1
        //   837: i2l
        //   838: bipush 32
        //   840: lshl
        //   841: lor
        //   842: lstore 5
        //   844: aload 15
        //   846: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   849: lload 5
        //   851: lconst_1
        //   852: lcmp
        //   853: ifle +74 -> 927
        //   856: aload 11
        //   858: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   861: ldc 205
        //   863: iconst_5
        //   864: anewarray 4	java/lang/Object
        //   867: dup
        //   868: iconst_0
        //   869: aload_0
        //   870: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   873: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   876: aastore
        //   877: dup
        //   878: iconst_1
        //   879: lload 5
        //   881: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   884: aastore
        //   885: dup
        //   886: iconst_2
        //   887: aload_0
        //   888: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   891: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   894: aastore
        //   895: dup
        //   896: iconst_3
        //   897: aload_0
        //   898: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   901: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   904: aastore
        //   905: dup
        //   906: iconst_4
        //   907: iload_2
        //   908: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   911: aastore
        //   912: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   915: iconst_0
        //   916: anewarray 4	java/lang/Object
        //   919: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   922: astore 11
        //   924: goto -600 -> 324
        //   927: aload 11
        //   929: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   932: ldc 207
        //   934: iconst_4
        //   935: anewarray 4	java/lang/Object
        //   938: dup
        //   939: iconst_0
        //   940: aload_0
        //   941: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   944: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   947: aastore
        //   948: dup
        //   949: iconst_1
        //   950: aload_0
        //   951: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   954: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   957: aastore
        //   958: dup
        //   959: iconst_2
        //   960: aload_0
        //   961: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   964: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   967: aastore
        //   968: dup
        //   969: iconst_3
        //   970: iload_2
        //   971: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   974: aastore
        //   975: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   978: iconst_0
        //   979: anewarray 4	java/lang/Object
        //   982: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   985: astore 11
        //   987: goto -663 -> 324
        //   990: iconst_1
        //   991: istore 4
        //   993: aload_0
        //   994: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   997: ifeq +66 -> 1063
        //   1000: aload 11
        //   1002: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   1005: ldc 209
        //   1007: iconst_4
        //   1008: anewarray 4	java/lang/Object
        //   1011: dup
        //   1012: iconst_0
        //   1013: aload_0
        //   1014: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   1017: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   1020: aastore
        //   1021: dup
        //   1022: iconst_1
        //   1023: aload_0
        //   1024: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   1027: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1030: aastore
        //   1031: dup
        //   1032: iconst_2
        //   1033: aload_0
        //   1034: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   1037: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1040: aastore
        //   1041: dup
        //   1042: iconst_3
        //   1043: iload_2
        //   1044: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1047: aastore
        //   1048: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   1051: iconst_0
        //   1052: anewarray 4	java/lang/Object
        //   1055: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   1058: astore 11
        //   1060: goto -736 -> 324
        //   1063: aload 11
        //   1065: getstatic 66	java/util/Locale:US	Ljava/util/Locale;
        //   1068: ldc 211
        //   1070: iconst_4
        //   1071: anewarray 4	java/lang/Object
        //   1074: dup
        //   1075: iconst_0
        //   1076: aload_0
        //   1077: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   1080: invokestatic 74	java/lang/Long:valueOf	(J)Ljava/lang/Long;
        //   1083: aastore
        //   1084: dup
        //   1085: iconst_1
        //   1086: aload_0
        //   1087: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   1090: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1093: aastore
        //   1094: dup
        //   1095: iconst_2
        //   1096: aload_0
        //   1097: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   1100: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1103: aastore
        //   1104: dup
        //   1105: iconst_3
        //   1106: iload_2
        //   1107: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1110: aastore
        //   1111: invokestatic 85	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
        //   1114: iconst_0
        //   1115: anewarray 4	java/lang/Object
        //   1118: invokevirtual 91	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
        //   1121: astore 11
        //   1123: goto -799 -> 324
        //   1126: aload 14
        //   1128: aload 16
        //   1130: getfield 151	org/vidogram/tgnet/TLRPC$Message:from_id	I
        //   1133: ineg
        //   1134: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1137: invokevirtual 154	java/util/ArrayList:contains	(Ljava/lang/Object;)Z
        //   1140: ifne -816 -> 324
        //   1143: aload 14
        //   1145: aload 16
        //   1147: getfield 151	org/vidogram/tgnet/TLRPC$Message:from_id	I
        //   1150: ineg
        //   1151: invokestatic 79	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
        //   1154: invokevirtual 148	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   1157: pop
        //   1158: goto -834 -> 324
        //   1161: aload 11
        //   1163: invokevirtual 104	org/vidogram/SQLite/SQLiteCursor:dispose	()V
        //   1166: aload 13
        //   1168: invokevirtual 214	java/util/ArrayList:isEmpty	()Z
        //   1171: ifne +21 -> 1192
        //   1174: invokestatic 56	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
        //   1177: ldc 216
        //   1179: aload 13
        //   1181: invokestatic 222	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1184: aload 12
        //   1186: getfield 163	org/vidogram/tgnet/TLRPC$TL_messages_messages:users	Ljava/util/ArrayList;
        //   1189: invokevirtual 226	org/vidogram/messenger/MessagesStorage:getUsersInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1192: aload 14
        //   1194: invokevirtual 214	java/util/ArrayList:isEmpty	()Z
        //   1197: ifne +21 -> 1218
        //   1200: invokestatic 56	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
        //   1203: ldc 216
        //   1205: aload 14
        //   1207: invokestatic 222	android/text/TextUtils:join	(Ljava/lang/CharSequence;Ljava/lang/Iterable;)Ljava/lang/String;
        //   1210: aload 12
        //   1212: getfield 160	org/vidogram/tgnet/TLRPC$TL_messages_messages:chats	Ljava/util/ArrayList;
        //   1215: invokevirtual 229	org/vidogram/messenger/MessagesStorage:getChatsInternal	(Ljava/lang/String;Ljava/util/ArrayList;)V
        //   1218: aload 12
        //   1220: getfield 144	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   1223: invokevirtual 232	java/util/ArrayList:size	()I
        //   1226: istore_1
        //   1227: aload_0
        //   1228: getfield 25	org/vidogram/messenger/query/SharedMediaQuery$7:val$count	I
        //   1231: istore_2
        //   1232: iload_1
        //   1233: iload_2
        //   1234: if_icmple +25 -> 1259
        //   1237: iconst_0
        //   1238: istore 4
        //   1240: aload 12
        //   1242: getfield 144	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   1245: aload 12
        //   1247: getfield 144	org/vidogram/tgnet/TLRPC$TL_messages_messages:messages	Ljava/util/ArrayList;
        //   1250: invokevirtual 232	java/util/ArrayList:size	()I
        //   1253: iconst_1
        //   1254: isub
        //   1255: invokevirtual 236	java/util/ArrayList:remove	(I)Ljava/lang/Object;
        //   1258: pop
        //   1259: aload 12
        //   1261: aload_0
        //   1262: getfield 27	org/vidogram/messenger/query/SharedMediaQuery$7:val$uid	J
        //   1265: aload_0
        //   1266: getfield 35	org/vidogram/messenger/query/SharedMediaQuery$7:val$offset	I
        //   1269: aload_0
        //   1270: getfield 25	org/vidogram/messenger/query/SharedMediaQuery$7:val$count	I
        //   1273: aload_0
        //   1274: getfield 29	org/vidogram/messenger/query/SharedMediaQuery$7:val$max_id	I
        //   1277: aload_0
        //   1278: getfield 33	org/vidogram/messenger/query/SharedMediaQuery$7:val$type	I
        //   1281: iconst_1
        //   1282: aload_0
        //   1283: getfield 37	org/vidogram/messenger/query/SharedMediaQuery$7:val$classGuid	I
        //   1286: aload_0
        //   1287: getfield 31	org/vidogram/messenger/query/SharedMediaQuery$7:val$isChannel	Z
        //   1290: iload 4
        //   1292: invokestatic 173	org/vidogram/messenger/query/SharedMediaQuery:access$000	(Lorg/vidogram/tgnet/TLRPC$messages_Messages;JIIIIZIZZ)V
        //   1295: return
        //   1296: astore 11
        //   1298: goto -634 -> 664
        //   1301: astore 11
        //   1303: goto -846 -> 457
        //   1306: goto -1227 -> 79
        //   1309: lload 7
        //   1311: lconst_0
        //   1312: lcmp
        //   1313: ifeq -7 -> 1306
        //   1316: iload_1
        //   1317: ifeq -11 -> 1306
        //   1320: lload 7
        //   1322: iload_1
        //   1323: i2l
        //   1324: bipush 32
        //   1326: lshl
        //   1327: lor
        //   1328: lstore 7
        //   1330: goto -1251 -> 79
        //
        // Exception table:
        //   from	to	target	type
        //   9	42	455	java/lang/Exception
        //   45	53	455	java/lang/Exception
        //   55	62	455	java/lang/Exception
        //   62	76	455	java/lang/Exception
        //   79	140	455	java/lang/Exception
        //   143	148	455	java/lang/Exception
        //   158	211	455	java/lang/Exception
        //   211	228	455	java/lang/Exception
        //   246	251	455	java/lang/Exception
        //   258	324	455	java/lang/Exception
        //   324	340	455	java/lang/Exception
        //   345	403	455	java/lang/Exception
        //   403	452	455	java/lang/Exception
        //   528	591	455	java/lang/Exception
        //   595	654	455	java/lang/Exception
        //   654	659	455	java/lang/Exception
        //   702	760	455	java/lang/Exception
        //   766	809	455	java/lang/Exception
        //   809	826	455	java/lang/Exception
        //   844	849	455	java/lang/Exception
        //   856	924	455	java/lang/Exception
        //   927	987	455	java/lang/Exception
        //   993	1060	455	java/lang/Exception
        //   1063	1123	455	java/lang/Exception
        //   1126	1158	455	java/lang/Exception
        //   1161	1192	455	java/lang/Exception
        //   1192	1218	455	java/lang/Exception
        //   1218	1232	455	java/lang/Exception
        //   9	42	662	finally
        //   45	53	662	finally
        //   55	62	662	finally
        //   62	76	662	finally
        //   79	140	662	finally
        //   143	148	662	finally
        //   158	211	662	finally
        //   211	228	662	finally
        //   246	251	662	finally
        //   258	324	662	finally
        //   324	340	662	finally
        //   345	403	662	finally
        //   403	452	662	finally
        //   528	591	662	finally
        //   595	654	662	finally
        //   654	659	662	finally
        //   702	760	662	finally
        //   766	809	662	finally
        //   809	826	662	finally
        //   844	849	662	finally
        //   856	924	662	finally
        //   927	987	662	finally
        //   993	1060	662	finally
        //   1063	1123	662	finally
        //   1126	1158	662	finally
        //   1161	1192	662	finally
        //   1192	1218	662	finally
        //   1218	1232	662	finally
        //   457	486	1296	finally
        //   1240	1259	1296	finally
        //   1240	1259	1301	java/lang/Exception
      }
    });
  }

  public static void loadMusic(long paramLong, int paramInt)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, paramInt)
    {
      public void run()
      {
        ArrayList localArrayList = new ArrayList();
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, mid FROM media_v2 WHERE uid = %d AND mid < %d AND type = %d ORDER BY date DESC, mid DESC LIMIT 1000", new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(this.val$max_id), Integer.valueOf(4) }), new Object[0]);
          while (localSQLiteCursor.next())
          {
            NativeByteBuffer localNativeByteBuffer = localSQLiteCursor.byteBufferValue(0);
            if (localNativeByteBuffer == null)
              continue;
            TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
            localNativeByteBuffer.reuse();
            if (!MessageObject.isMusicMessage(localMessage))
              continue;
            localMessage.id = localSQLiteCursor.intValue(1);
            localMessage.dialog_id = this.val$uid;
            localArrayList.add(0, new MessageObject(localMessage, null, false));
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        while (true)
        {
          AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
          {
            public void run()
            {
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.musicDidLoaded, new Object[] { Long.valueOf(SharedMediaQuery.9.this.val$uid), this.val$arrayList });
            }
          });
          return;
          localException.dispose();
        }
      }
    });
  }

  private static void processLoadedMedia(TLRPC.messages_Messages parammessages_Messages, long paramLong, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, int paramInt5, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i = (int)paramLong;
    if ((paramBoolean1) && (parammessages_Messages.messages.isEmpty()) && (i != 0))
    {
      loadMedia(paramLong, paramInt1, paramInt2, paramInt3, paramInt4, false, paramInt5);
      return;
    }
    if (!paramBoolean1)
    {
      ImageLoader.saveMessagesThumbs(parammessages_Messages.messages);
      MessagesStorage.getInstance().putUsersAndChats(parammessages_Messages.users, parammessages_Messages.chats, true, true);
      putMediaDatabase(paramLong, paramInt4, parammessages_Messages.messages, paramInt3, paramBoolean3);
    }
    HashMap localHashMap = new HashMap();
    paramInt1 = 0;
    while (paramInt1 < parammessages_Messages.users.size())
    {
      localObject = (TLRPC.User)parammessages_Messages.users.get(paramInt1);
      localHashMap.put(Integer.valueOf(((TLRPC.User)localObject).id), localObject);
      paramInt1 += 1;
    }
    Object localObject = new ArrayList();
    paramInt1 = 0;
    while (paramInt1 < parammessages_Messages.messages.size())
    {
      ((ArrayList)localObject).add(new MessageObject((TLRPC.Message)parammessages_Messages.messages.get(paramInt1), localHashMap, true));
      paramInt1 += 1;
    }
    AndroidUtilities.runOnUIThread(new Runnable(parammessages_Messages, paramBoolean1, paramLong, (ArrayList)localObject, paramInt5, paramInt4, paramBoolean3)
    {
      public void run()
      {
        int i = this.val$res.count;
        MessagesController.getInstance().putUsers(this.val$res.users, this.val$fromCache);
        MessagesController.getInstance().putChats(this.val$res.chats, this.val$fromCache);
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.mediaDidLoaded, new Object[] { Long.valueOf(this.val$uid), Integer.valueOf(i), this.val$objects, Integer.valueOf(this.val$classGuid), Integer.valueOf(this.val$type), Boolean.valueOf(this.val$topReached) });
      }
    });
  }

  private static void processLoadedMediaCount(int paramInt1, long paramLong, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramLong, paramBoolean, paramInt1, paramInt2, paramInt3)
    {
      public void run()
      {
        int i = 0;
        int j = (int)this.val$uid;
        if ((this.val$fromCache) && (this.val$count == -1) && (j != 0))
        {
          SharedMediaQuery.getMediaCount(this.val$uid, this.val$type, this.val$classGuid, false);
          return;
        }
        if (!this.val$fromCache)
          SharedMediaQuery.access$200(this.val$uid, this.val$type, this.val$count);
        NotificationCenter localNotificationCenter = NotificationCenter.getInstance();
        j = NotificationCenter.mediaCountDidLoaded;
        long l = this.val$uid;
        if ((this.val$fromCache) && (this.val$count == -1));
        while (true)
        {
          localNotificationCenter.postNotificationName(j, new Object[] { Long.valueOf(l), Integer.valueOf(i), Boolean.valueOf(this.val$fromCache), Integer.valueOf(this.val$type) });
          return;
          i = this.val$count;
        }
      }
    });
  }

  private static void putMediaCountDatabase(long paramLong, int paramInt1, int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, paramInt1, paramInt2)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_counts_v2 VALUES(?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindLong(1, this.val$uid);
          localSQLitePreparedStatement.bindInteger(2, this.val$type);
          localSQLitePreparedStatement.bindInteger(3, this.val$count);
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

  private static void putMediaDatabase(long paramLong, int paramInt1, ArrayList<TLRPC.Message> paramArrayList, int paramInt2, boolean paramBoolean)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList, paramBoolean, paramLong, paramInt2, paramInt1)
    {
      public void run()
      {
        int i = 1;
        try
        {
          if ((this.val$messages.isEmpty()) || (this.val$topReached))
          {
            MessagesStorage.getInstance().doneHolesInMedia(this.val$uid, this.val$max_id, this.val$type);
            if (this.val$messages.isEmpty())
              return;
          }
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO media_v2 VALUES(?, ?, ?, ?, ?)");
          Iterator localIterator = this.val$messages.iterator();
          while (localIterator.hasNext())
          {
            TLRPC.Message localMessage = (TLRPC.Message)localIterator.next();
            if (!SharedMediaQuery.canAddMessageToMedia(localMessage))
              continue;
            long l2 = localMessage.id;
            long l1 = l2;
            if (localMessage.to_id.channel_id != 0)
              l1 = l2 | localMessage.to_id.channel_id << 32;
            localSQLitePreparedStatement.requery();
            NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localMessage.getObjectSize());
            localMessage.serializeToStream(localNativeByteBuffer);
            localSQLitePreparedStatement.bindLong(1, l1);
            localSQLitePreparedStatement.bindLong(2, this.val$uid);
            localSQLitePreparedStatement.bindInteger(3, localMessage.date);
            localSQLitePreparedStatement.bindInteger(4, this.val$type);
            localSQLitePreparedStatement.bindByteBuffer(5, localNativeByteBuffer);
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
        if ((!this.val$topReached) || (this.val$max_id != 0))
        {
          if (!this.val$topReached)
            break label303;
          if (this.val$max_id == 0)
            break label329;
          MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, i, this.val$max_id, this.val$type);
        }
        while (true)
        {
          MessagesStorage.getInstance().getDatabase().commitTransaction();
          return;
          label303: i = ((TLRPC.Message)this.val$messages.get(this.val$messages.size() - 1)).id;
          break;
          label329: MessagesStorage.getInstance().closeHolesInMedia(this.val$uid, i, 2147483647, this.val$type);
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.SharedMediaQuery
 * JD-Core Version:    0.6.0
 */