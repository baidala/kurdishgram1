package org.vidogram.messenger.query;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.tgnet.TLRPC.TL_documentEmpty;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputDocument;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetID;
import org.vidogram.tgnet.TLRPC.TL_messages_allStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_archivedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_featuredStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getAllStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getArchivedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getFeaturedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getMaskStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getRecentStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_getSavedGifs;
import org.vidogram.tgnet.TLRPC.TL_messages_getStickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_installStickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_readFeaturedStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_recentStickers;
import org.vidogram.tgnet.TLRPC.TL_messages_saveGif;
import org.vidogram.tgnet.TLRPC.TL_messages_savedGifs;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSet;
import org.vidogram.tgnet.TLRPC.TL_messages_stickerSetInstallResultArchive;
import org.vidogram.tgnet.TLRPC.TL_messages_uninstallStickerSet;
import org.vidogram.tgnet.TLRPC.TL_stickerPack;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.Components.StickersArchiveAlert;

public class StickersQuery
{
  public static final int TYPE_IMAGE = 0;
  public static final int TYPE_MASK = 1;
  private static HashMap<String, ArrayList<TLRPC.Document>> allStickers;
  private static int[] archivedStickersCount;
  private static ArrayList<TLRPC.StickerSetCovered> featuredStickerSets;
  private static HashMap<Long, TLRPC.StickerSetCovered> featuredStickerSetsById;
  private static boolean featuredStickersLoaded;
  private static int[] loadDate;
  private static int loadFeaturedDate;
  private static int loadFeaturedHash;
  private static int[] loadHash;
  private static boolean loadingFeaturedStickers;
  private static boolean loadingRecentGifs;
  private static boolean[] loadingRecentStickers;
  private static boolean[] loadingStickers;
  private static ArrayList<Long> readingStickerSets;
  private static ArrayList<TLRPC.Document> recentGifs;
  private static boolean recentGifsLoaded;
  private static ArrayList<TLRPC.Document>[] recentStickers;
  private static boolean[] recentStickersLoaded;
  private static ArrayList<TLRPC.TL_messages_stickerSet>[] stickerSets = { new ArrayList(), new ArrayList() };
  private static HashMap<Long, TLRPC.TL_messages_stickerSet> stickerSetsById = new HashMap();
  private static HashMap<String, TLRPC.TL_messages_stickerSet> stickerSetsByName = new HashMap();
  private static HashMap<Long, String> stickersByEmoji;
  private static boolean[] stickersLoaded;
  private static ArrayList<Long> unreadStickerSets;

  static
  {
    loadingStickers = new boolean[2];
    stickersLoaded = new boolean[2];
    loadHash = new int[2];
    loadDate = new int[2];
    archivedStickersCount = new int[2];
    stickersByEmoji = new HashMap();
    allStickers = new HashMap();
    recentStickers = new ArrayList[] { new ArrayList(), new ArrayList() };
    loadingRecentStickers = new boolean[2];
    recentStickersLoaded = new boolean[2];
    recentGifs = new ArrayList();
    featuredStickerSets = new ArrayList();
    featuredStickerSetsById = new HashMap();
    unreadStickerSets = new ArrayList();
    readingStickerSets = new ArrayList();
  }

  public static void addNewStickerSet(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet)
  {
    if ((stickerSetsById.containsKey(Long.valueOf(paramTL_messages_stickerSet.set.id))) || (stickerSetsByName.containsKey(paramTL_messages_stickerSet.set.short_name)))
      return;
    if (paramTL_messages_stickerSet.set.masks);
    HashMap localHashMap;
    Object localObject1;
    for (int i = 1; ; i = 0)
    {
      stickerSets[i].add(0, paramTL_messages_stickerSet);
      stickerSetsById.put(Long.valueOf(paramTL_messages_stickerSet.set.id), paramTL_messages_stickerSet);
      stickerSetsByName.put(paramTL_messages_stickerSet.set.short_name, paramTL_messages_stickerSet);
      localHashMap = new HashMap();
      j = 0;
      while (j < paramTL_messages_stickerSet.documents.size())
      {
        localObject1 = (TLRPC.Document)paramTL_messages_stickerSet.documents.get(j);
        localHashMap.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
        j += 1;
      }
    }
    int j = 0;
    TLRPC.TL_stickerPack localTL_stickerPack;
    if (j < paramTL_messages_stickerSet.packs.size())
    {
      localTL_stickerPack = (TLRPC.TL_stickerPack)paramTL_messages_stickerSet.packs.get(j);
      localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
      localObject1 = (ArrayList)allStickers.get(localTL_stickerPack.emoticon);
      if (localObject1 != null)
        break label375;
      localObject1 = new ArrayList();
      allStickers.put(localTL_stickerPack.emoticon, localObject1);
    }
    label375: 
    while (true)
    {
      int k = 0;
      while (k < localTL_stickerPack.documents.size())
      {
        Object localObject2 = (Long)localTL_stickerPack.documents.get(k);
        if (!stickersByEmoji.containsKey(localObject2))
          stickersByEmoji.put(localObject2, localTL_stickerPack.emoticon);
        localObject2 = (TLRPC.Document)localHashMap.get(localObject2);
        if (localObject2 != null)
          ((ArrayList)localObject1).add(localObject2);
        k += 1;
      }
      j += 1;
      break;
      loadHash[i] = calcStickersHash(stickerSets[i]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
      loadStickers(i, false, true);
      return;
    }
  }

  public static void addRecentGif(TLRPC.Document paramDocument, int paramInt)
  {
    int i = 0;
    int j = 0;
    while (i < recentGifs.size())
    {
      localObject = (TLRPC.Document)recentGifs.get(i);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        recentGifs.remove(i);
        recentGifs.add(0, localObject);
        j = 1;
      }
      i += 1;
    }
    if (j == 0)
      recentGifs.add(0, paramDocument);
    if (recentGifs.size() > MessagesController.getInstance().maxRecentGifsCount)
    {
      localObject = (TLRPC.Document)recentGifs.remove(recentGifs.size() - 1);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((TLRPC.Document)localObject)
      {
        public void run()
        {
          try
          {
            MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramDocument);
    processLoadedRecentDocuments(0, (ArrayList)localObject, true, paramInt);
  }

  public static void addRecentSticker(int paramInt1, TLRPC.Document paramDocument, int paramInt2)
  {
    int i = 0;
    int j = 0;
    while (i < recentStickers[paramInt1].size())
    {
      localObject = (TLRPC.Document)recentStickers[paramInt1].get(i);
      if (((TLRPC.Document)localObject).id == paramDocument.id)
      {
        recentStickers[paramInt1].remove(i);
        recentStickers[paramInt1].add(0, localObject);
        j = 1;
      }
      i += 1;
    }
    if (j == 0)
      recentStickers[paramInt1].add(0, paramDocument);
    if (recentStickers[paramInt1].size() > MessagesController.getInstance().maxRecentStickersCount)
    {
      localObject = (TLRPC.Document)recentStickers[paramInt1].remove(recentStickers[paramInt1].size() - 1);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((TLRPC.Document)localObject)
      {
        public void run()
        {
          try
          {
            MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$old.id + "'").stepThis().dispose();
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      });
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramDocument);
    processLoadedRecentDocuments(paramInt1, (ArrayList)localObject, false, paramInt2);
  }

  private static int calcDocumentsHash(ArrayList<TLRPC.Document> paramArrayList)
  {
    if (paramArrayList == null)
      return 0;
    long l = 0L;
    int i = 0;
    if (i < Math.min(200, paramArrayList.size()))
    {
      TLRPC.Document localDocument = (TLRPC.Document)paramArrayList.get(i);
      if (localDocument == null);
      while (true)
      {
        i += 1;
        break;
        int j = (int)(localDocument.id >> 32);
        int k = (int)localDocument.id;
        l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
      }
    }
    return (int)l;
  }

  private static int calcFeaturedStickersHash(ArrayList<TLRPC.StickerSetCovered> paramArrayList)
  {
    long l1 = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.StickerSetCovered)paramArrayList.get(i)).set;
      if (localStickerSet.archived);
      while (true)
      {
        i += 1;
        break;
        int j = (int)(localStickerSet.id >> 32);
        int k = (int)localStickerSet.id;
        long l2 = ((l1 * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
        l1 = l2;
        if (!unreadStickerSets.contains(Long.valueOf(localStickerSet.id)))
          continue;
        l1 = (l2 * 20261L + 2147483648L + 1L) % 2147483648L;
      }
    }
    return (int)l1;
  }

  public static void calcNewHash(int paramInt)
  {
    loadHash[paramInt] = calcStickersHash(stickerSets[paramInt]);
  }

  private static int calcStickersHash(ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList)
  {
    long l = 0L;
    int i = 0;
    if (i < paramArrayList.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.TL_messages_stickerSet)paramArrayList.get(i)).set;
      if (localStickerSet.archived);
      while (true)
      {
        i += 1;
        break;
        l = (l * 20261L + 2147483648L + localStickerSet.hash) % 2147483648L;
      }
    }
    return (int)l;
  }

  public static void checkFeaturedStickers()
  {
    if ((!loadingFeaturedStickers) && ((!featuredStickersLoaded) || (Math.abs(System.currentTimeMillis() / 1000L - loadFeaturedDate) >= 3600L)))
      loadFeaturesStickers(true, false);
  }

  public static void checkStickers(int paramInt)
  {
    if ((loadingStickers[paramInt] == 0) && ((stickersLoaded[paramInt] == 0) || (Math.abs(System.currentTimeMillis() / 1000L - loadDate[paramInt]) >= 3600L)))
      loadStickers(paramInt, true, false);
  }

  public static void cleanup()
  {
    int i = 0;
    while (i < 2)
    {
      loadHash[i] = 0;
      loadDate[i] = 0;
      stickerSets[i].clear();
      recentStickers[i].clear();
      loadingStickers[i] = false;
      stickersLoaded[i] = false;
      loadingRecentStickers[i] = false;
      recentStickersLoaded[i] = false;
      i += 1;
    }
    loadFeaturedDate = 0;
    loadFeaturedHash = 0;
    allStickers.clear();
    stickersByEmoji.clear();
    featuredStickerSetsById.clear();
    featuredStickerSets.clear();
    unreadStickerSets.clear();
    recentGifs.clear();
    stickerSetsById.clear();
    stickerSetsByName.clear();
    loadingFeaturedStickers = false;
    featuredStickersLoaded = false;
    loadingRecentGifs = false;
    recentGifsLoaded = false;
  }

  public static HashMap<String, ArrayList<TLRPC.Document>> getAllStickers()
  {
    return allStickers;
  }

  public static int getArchivedStickersCount(int paramInt)
  {
    return archivedStickersCount[paramInt];
  }

  public static String getEmojiForSticker(long paramLong)
  {
    String str = (String)stickersByEmoji.get(Long.valueOf(paramLong));
    if (str != null)
      return str;
    return "";
  }

  public static ArrayList<TLRPC.StickerSetCovered> getFeaturedStickerSets()
  {
    return featuredStickerSets;
  }

  public static int getFeaturesStickersHashWithoutUnread()
  {
    long l = 0L;
    int i = 0;
    if (i < featuredStickerSets.size())
    {
      TLRPC.StickerSet localStickerSet = ((TLRPC.StickerSetCovered)featuredStickerSets.get(i)).set;
      if (localStickerSet.archived);
      while (true)
      {
        i += 1;
        break;
        int j = (int)(localStickerSet.id >> 32);
        int k = (int)localStickerSet.id;
        l = ((l * 20261L + 2147483648L + j) % 2147483648L * 20261L + 2147483648L + k) % 2147483648L;
      }
    }
    return (int)l;
  }

  public static ArrayList<TLRPC.Document> getRecentGifs()
  {
    return new ArrayList(recentGifs);
  }

  public static ArrayList<TLRPC.Document> getRecentStickers(int paramInt)
  {
    return new ArrayList(recentStickers[paramInt]);
  }

  public static ArrayList<TLRPC.Document> getRecentStickersNoCopy(int paramInt)
  {
    return recentStickers[paramInt];
  }

  public static TLRPC.TL_messages_stickerSet getStickerSetById(Long paramLong)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsById.get(paramLong);
  }

  public static TLRPC.TL_messages_stickerSet getStickerSetByName(String paramString)
  {
    return (TLRPC.TL_messages_stickerSet)stickerSetsByName.get(paramString);
  }

  public static long getStickerSetId(TLRPC.Document paramDocument)
  {
    int i = 0;
    while (i < paramDocument.attributes.size())
    {
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
      {
        if (!(localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetID))
          break;
        return localDocumentAttribute.stickerset.id;
      }
      i += 1;
    }
    return -1L;
  }

  public static String getStickerSetName(long paramLong)
  {
    Object localObject = (TLRPC.TL_messages_stickerSet)stickerSetsById.get(Long.valueOf(paramLong));
    if (localObject != null)
      return ((TLRPC.TL_messages_stickerSet)localObject).set.short_name;
    localObject = (TLRPC.StickerSetCovered)featuredStickerSetsById.get(Long.valueOf(paramLong));
    if (localObject != null)
      return ((TLRPC.StickerSetCovered)localObject).set.short_name;
    return (String)null;
  }

  public static ArrayList<TLRPC.TL_messages_stickerSet> getStickerSets(int paramInt)
  {
    return stickerSets[paramInt];
  }

  public static ArrayList<Long> getUnreadStickerSets()
  {
    return unreadStickerSets;
  }

  public static boolean isLoadingStickers(int paramInt)
  {
    return loadingStickers[paramInt];
  }

  public static boolean isStickerPackInstalled(long paramLong)
  {
    return stickerSetsById.containsKey(Long.valueOf(paramLong));
  }

  public static boolean isStickerPackInstalled(String paramString)
  {
    return stickerSetsByName.containsKey(paramString);
  }

  public static boolean isStickerPackUnread(long paramLong)
  {
    return unreadStickerSets.contains(Long.valueOf(paramLong));
  }

  public static void loadArchivedStickersCount(int paramInt, boolean paramBoolean)
  {
    boolean bool = true;
    if (paramBoolean)
    {
      int i = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("archivedStickersCount" + paramInt, -1);
      if (i == -1)
      {
        loadArchivedStickersCount(paramInt, false);
        return;
      }
      archivedStickersCount[paramInt] = i;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, new Object[] { Integer.valueOf(paramInt) });
      return;
    }
    TLRPC.TL_messages_getArchivedStickers localTL_messages_getArchivedStickers = new TLRPC.TL_messages_getArchivedStickers();
    localTL_messages_getArchivedStickers.limit = 0;
    if (paramInt == 1);
    for (paramBoolean = bool; ; paramBoolean = false)
    {
      localTL_messages_getArchivedStickers.masks = paramBoolean;
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getArchivedStickers, new RequestDelegate(paramInt)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              if (this.val$error == null)
              {
                TLRPC.TL_messages_archivedStickers localTL_messages_archivedStickers = (TLRPC.TL_messages_archivedStickers)this.val$response;
                StickersQuery.archivedStickersCount[StickersQuery.19.this.val$type] = localTL_messages_archivedStickers.count;
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("archivedStickersCount" + StickersQuery.19.this.val$type, localTL_messages_archivedStickers.count).commit();
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.archivedStickersCountDidLoaded, new Object[] { Integer.valueOf(StickersQuery.19.this.val$type) });
              }
            }
          });
        }
      });
      return;
    }
  }

  public static void loadFeaturesStickers(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (loadingFeaturedStickers)
      return;
    loadingFeaturedStickers = true;
    if (paramBoolean1)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        // ERROR //
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 8
          //   3: aconst_null
          //   4: astore 6
          //   6: iconst_0
          //   7: istore_2
          //   8: iconst_0
          //   9: istore_1
          //   10: new 21	java/util/ArrayList
          //   13: dup
          //   14: invokespecial 22	java/util/ArrayList:<init>	()V
          //   17: astore 9
          //   19: invokestatic 28	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
          //   22: invokevirtual 32	org/vidogram/messenger/MessagesStorage:getDatabase	()Lorg/vidogram/SQLite/SQLiteDatabase;
          //   25: ldc 34
          //   27: iconst_0
          //   28: anewarray 4	java/lang/Object
          //   31: invokevirtual 40	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
          //   34: astore 5
          //   36: aload 5
          //   38: invokevirtual 46	org/vidogram/SQLite/SQLiteCursor:next	()Z
          //   41: ifeq +324 -> 365
          //   44: aload 5
          //   46: iconst_0
          //   47: invokevirtual 50	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
          //   50: astore 10
          //   52: aload 10
          //   54: ifnull +305 -> 359
          //   57: new 21	java/util/ArrayList
          //   60: dup
          //   61: invokespecial 22	java/util/ArrayList:<init>	()V
          //   64: astore 6
          //   66: aload 6
          //   68: astore 7
          //   70: aload 10
          //   72: iconst_0
          //   73: invokevirtual 56	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   76: istore_3
          //   77: iconst_0
          //   78: istore_1
          //   79: iload_1
          //   80: iload_3
          //   81: if_icmpge +32 -> 113
          //   84: aload 6
          //   86: astore 7
          //   88: aload 6
          //   90: aload 10
          //   92: aload 10
          //   94: iconst_0
          //   95: invokevirtual 56	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   98: iconst_0
          //   99: invokestatic 62	org/vidogram/tgnet/TLRPC$StickerSetCovered:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$StickerSetCovered;
          //   102: invokevirtual 66	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   105: pop
          //   106: iload_1
          //   107: iconst_1
          //   108: iadd
          //   109: istore_1
          //   110: goto -31 -> 79
          //   113: aload 6
          //   115: astore 7
          //   117: aload 10
          //   119: invokevirtual 69	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
          //   122: aload 6
          //   124: astore 7
          //   126: aload 5
          //   128: iconst_1
          //   129: invokevirtual 50	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
          //   132: astore 8
          //   134: aload 8
          //   136: ifnull +56 -> 192
          //   139: aload 6
          //   141: astore 7
          //   143: aload 8
          //   145: iconst_0
          //   146: invokevirtual 56	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   149: istore_3
          //   150: iconst_0
          //   151: istore_1
          //   152: iload_1
          //   153: iload_3
          //   154: if_icmpge +29 -> 183
          //   157: aload 6
          //   159: astore 7
          //   161: aload 9
          //   163: aload 8
          //   165: iconst_0
          //   166: invokevirtual 73	org/vidogram/tgnet/NativeByteBuffer:readInt64	(Z)J
          //   169: invokestatic 79	java/lang/Long:valueOf	(J)Ljava/lang/Long;
          //   172: invokevirtual 66	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   175: pop
          //   176: iload_1
          //   177: iconst_1
          //   178: iadd
          //   179: istore_1
          //   180: goto -28 -> 152
          //   183: aload 6
          //   185: astore 7
          //   187: aload 8
          //   189: invokevirtual 69	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
          //   192: aload 6
          //   194: astore 7
          //   196: aload 5
          //   198: iconst_2
          //   199: invokevirtual 83	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
          //   202: istore_1
          //   203: aload 6
          //   205: invokestatic 87	org/vidogram/messenger/query/StickersQuery:access$700	(Ljava/util/ArrayList;)I
          //   208: istore_3
          //   209: iload_1
          //   210: istore_2
          //   211: iload_3
          //   212: istore_1
          //   213: iload_1
          //   214: istore_3
          //   215: iload_2
          //   216: istore 4
          //   218: aload 6
          //   220: astore 7
          //   222: aload 5
          //   224: ifnull +17 -> 241
          //   227: aload 5
          //   229: invokevirtual 90	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   232: aload 6
          //   234: astore 7
          //   236: iload_2
          //   237: istore 4
          //   239: iload_1
          //   240: istore_3
          //   241: aload 7
          //   243: aload 9
          //   245: iconst_1
          //   246: iload 4
          //   248: iload_3
          //   249: invokestatic 94	org/vidogram/messenger/query/StickersQuery:access$800	(Ljava/util/ArrayList;Ljava/util/ArrayList;ZII)V
          //   252: return
          //   253: astore 7
          //   255: aconst_null
          //   256: astore 5
          //   258: iconst_0
          //   259: istore_1
          //   260: aload 8
          //   262: astore 6
          //   264: aload 7
          //   266: invokestatic 100	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   269: iload_2
          //   270: istore_3
          //   271: iload_1
          //   272: istore 4
          //   274: aload 6
          //   276: astore 7
          //   278: aload 5
          //   280: ifnull -39 -> 241
          //   283: aload 5
          //   285: invokevirtual 90	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   288: iload_2
          //   289: istore_3
          //   290: iload_1
          //   291: istore 4
          //   293: aload 6
          //   295: astore 7
          //   297: goto -56 -> 241
          //   300: astore 6
          //   302: aconst_null
          //   303: astore 5
          //   305: aload 5
          //   307: ifnull +8 -> 315
          //   310: aload 5
          //   312: invokevirtual 90	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   315: aload 6
          //   317: athrow
          //   318: astore 6
          //   320: goto -15 -> 305
          //   323: astore 6
          //   325: goto -20 -> 305
          //   328: astore 7
          //   330: iconst_0
          //   331: istore_1
          //   332: aload 8
          //   334: astore 6
          //   336: goto -72 -> 264
          //   339: astore 8
          //   341: aload 7
          //   343: astore 6
          //   345: aload 8
          //   347: astore 7
          //   349: iconst_0
          //   350: istore_1
          //   351: goto -87 -> 264
          //   354: astore 7
          //   356: goto -92 -> 264
          //   359: aconst_null
          //   360: astore 6
          //   362: goto -240 -> 122
          //   365: iconst_0
          //   366: istore_2
          //   367: goto -154 -> 213
          //
          // Exception table:
          //   from	to	target	type
          //   19	36	253	java/lang/Throwable
          //   19	36	300	finally
          //   36	52	318	finally
          //   57	66	318	finally
          //   70	77	318	finally
          //   88	106	318	finally
          //   117	122	318	finally
          //   126	134	318	finally
          //   143	150	318	finally
          //   161	176	318	finally
          //   187	192	318	finally
          //   196	203	318	finally
          //   203	209	318	finally
          //   264	269	323	finally
          //   36	52	328	java/lang/Throwable
          //   57	66	328	java/lang/Throwable
          //   70	77	339	java/lang/Throwable
          //   88	106	339	java/lang/Throwable
          //   117	122	339	java/lang/Throwable
          //   126	134	339	java/lang/Throwable
          //   143	150	339	java/lang/Throwable
          //   161	176	339	java/lang/Throwable
          //   187	192	339	java/lang/Throwable
          //   196	203	339	java/lang/Throwable
          //   203	209	354	java/lang/Throwable
        }
      });
      return;
    }
    TLRPC.TL_messages_getFeaturedStickers localTL_messages_getFeaturedStickers = new TLRPC.TL_messages_getFeaturedStickers();
    if (paramBoolean2);
    for (int i = 0; ; i = loadFeaturedHash)
    {
      localTL_messages_getFeaturedStickers.hash = i;
      ConnectionsManager.getInstance().sendRequest(localTL_messages_getFeaturedStickers, new RequestDelegate(localTL_messages_getFeaturedStickers)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              if ((this.val$response instanceof TLRPC.TL_messages_featuredStickers))
              {
                TLRPC.TL_messages_featuredStickers localTL_messages_featuredStickers = (TLRPC.TL_messages_featuredStickers)this.val$response;
                StickersQuery.access$800(localTL_messages_featuredStickers.sets, localTL_messages_featuredStickers.unread, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_featuredStickers.hash);
                return;
              }
              StickersQuery.access$800(null, null, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.12.this.val$req.hash);
            }
          });
        }
      });
      return;
    }
  }

  public static void loadRecents(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
      if (!loadingRecentGifs);
    while (true)
    {
      return;
      loadingRecentGifs = true;
      if (recentGifsLoaded)
        paramBoolean2 = false;
      while (true)
      {
        if (!paramBoolean2)
          break label73;
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramBoolean1, paramInt)
        {
          public void run()
          {
            while (true)
            {
              Object localObject2;
              try
              {
                Object localObject1 = MessagesStorage.getInstance().getDatabase();
                localObject2 = new StringBuilder().append("SELECT document FROM web_recent_v3 WHERE type = ");
                if (this.val$gif)
                {
                  i = 2;
                  localObject1 = ((SQLiteDatabase)localObject1).queryFinalized(i + " ORDER BY date DESC", new Object[0]);
                  localObject2 = new ArrayList();
                  if (!((SQLiteCursor)localObject1).next())
                    break label138;
                  if (((SQLiteCursor)localObject1).isNull(0))
                    continue;
                  NativeByteBuffer localNativeByteBuffer = ((SQLiteCursor)localObject1).byteBufferValue(0);
                  if (localNativeByteBuffer == null)
                    continue;
                  TLRPC.Document localDocument = TLRPC.Document.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                  if (localDocument == null)
                    continue;
                  ((ArrayList)localObject2).add(localDocument);
                  localNativeByteBuffer.reuse();
                  continue;
                }
              }
              catch (Throwable localThrowable)
              {
                FileLog.e(localThrowable);
                return;
              }
              if (this.val$type == 0)
              {
                i = 3;
                continue;
                label138: localThrowable.dispose();
                AndroidUtilities.runOnUIThread(new Runnable((ArrayList)localObject2)
                {
                  public void run()
                  {
                    if (StickersQuery.5.this.val$gif)
                    {
                      StickersQuery.access$002(this.val$arrayList);
                      StickersQuery.access$102(false);
                      StickersQuery.access$202(true);
                    }
                    while (true)
                    {
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(StickersQuery.5.this.val$gif), Integer.valueOf(StickersQuery.5.this.val$type) });
                      StickersQuery.loadRecents(StickersQuery.5.this.val$type, StickersQuery.5.this.val$gif, false);
                      return;
                      StickersQuery.recentStickers[StickersQuery.5.this.val$type] = this.val$arrayList;
                      StickersQuery.loadingRecentStickers[StickersQuery.5.this.val$type] = 0;
                      StickersQuery.recentStickersLoaded[StickersQuery.5.this.val$type] = 1;
                    }
                  }
                });
                return;
              }
              int i = 4;
            }
          }
        });
        return;
        if (loadingRecentStickers[paramInt] != 0)
          break;
        loadingRecentStickers[paramInt] = true;
        if (recentStickersLoaded[paramInt] == 0)
          continue;
        paramBoolean2 = false;
      }
      label73: localObject = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
      long l;
      if (paramBoolean1)
        l = ((SharedPreferences)localObject).getLong("lastGifLoadTime", 0L);
      while (Math.abs(System.currentTimeMillis() - l) >= 3600000L)
      {
        if (!paramBoolean1)
          break label174;
        localObject = new TLRPC.TL_messages_getSavedGifs();
        ((TLRPC.TL_messages_getSavedGifs)localObject).hash = calcDocumentsHash(recentGifs);
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramInt, paramBoolean1)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            paramTL_error = null;
            if ((paramTLObject instanceof TLRPC.TL_messages_savedGifs))
              paramTL_error = ((TLRPC.TL_messages_savedGifs)paramTLObject).gifs;
            StickersQuery.access$600(this.val$type, paramTL_error, this.val$gif, 0);
          }
        });
        return;
        l = ((SharedPreferences)localObject).getLong("lastStickersLoadTime", 0L);
      }
    }
    label174: Object localObject = new TLRPC.TL_messages_getRecentStickers();
    ((TLRPC.TL_messages_getRecentStickers)localObject).hash = calcDocumentsHash(recentStickers[paramInt]);
    if (paramInt == 1);
    for (paramBoolean2 = true; ; paramBoolean2 = false)
    {
      ((TLRPC.TL_messages_getRecentStickers)localObject).attached = paramBoolean2;
      ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(paramInt, paramBoolean1)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          paramTL_error = null;
          if ((paramTLObject instanceof TLRPC.TL_messages_recentStickers))
            paramTL_error = ((TLRPC.TL_messages_recentStickers)paramTLObject).stickers;
          StickersQuery.access$600(this.val$type, paramTL_error, this.val$gif, 0);
        }
      });
      return;
    }
  }

  public static void loadStickers(int paramInt, boolean paramBoolean1, boolean paramBoolean2)
  {
    int j = 0;
    int i = 0;
    if (loadingStickers[paramInt] != 0)
      return;
    loadArchivedStickersCount(paramInt, paramBoolean1);
    loadingStickers[paramInt] = true;
    if (paramBoolean1)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt)
      {
        // ERROR //
        public void run()
        {
          // Byte code:
          //   0: aconst_null
          //   1: astore 8
          //   3: aconst_null
          //   4: astore 6
          //   6: iconst_0
          //   7: istore_2
          //   8: iconst_0
          //   9: istore_1
          //   10: invokestatic 30	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
          //   13: invokevirtual 34	org/vidogram/messenger/MessagesStorage:getDatabase	()Lorg/vidogram/SQLite/SQLiteDatabase;
          //   16: new 36	java/lang/StringBuilder
          //   19: dup
          //   20: invokespecial 37	java/lang/StringBuilder:<init>	()V
          //   23: ldc 39
          //   25: invokevirtual 43	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
          //   28: aload_0
          //   29: getfield 17	org/vidogram/messenger/query/StickersQuery$20:val$type	I
          //   32: iconst_1
          //   33: iadd
          //   34: invokevirtual 46	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
          //   37: invokevirtual 50	java/lang/StringBuilder:toString	()Ljava/lang/String;
          //   40: iconst_0
          //   41: anewarray 4	java/lang/Object
          //   44: invokevirtual 56	org/vidogram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/vidogram/SQLite/SQLiteCursor;
          //   47: astore 5
          //   49: aload 5
          //   51: invokevirtual 62	org/vidogram/SQLite/SQLiteCursor:next	()Z
          //   54: ifeq +256 -> 310
          //   57: aload 5
          //   59: iconst_0
          //   60: invokevirtual 66	org/vidogram/SQLite/SQLiteCursor:byteBufferValue	(I)Lorg/vidogram/tgnet/NativeByteBuffer;
          //   63: astore 9
          //   65: aload 9
          //   67: ifnull +237 -> 304
          //   70: new 68	java/util/ArrayList
          //   73: dup
          //   74: invokespecial 69	java/util/ArrayList:<init>	()V
          //   77: astore 6
          //   79: aload 6
          //   81: astore 7
          //   83: aload 9
          //   85: iconst_0
          //   86: invokevirtual 75	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   89: istore_3
          //   90: iconst_0
          //   91: istore_1
          //   92: iload_1
          //   93: iload_3
          //   94: if_icmpge +32 -> 126
          //   97: aload 6
          //   99: astore 7
          //   101: aload 6
          //   103: aload 9
          //   105: aload 9
          //   107: iconst_0
          //   108: invokevirtual 75	org/vidogram/tgnet/NativeByteBuffer:readInt32	(Z)I
          //   111: iconst_0
          //   112: invokestatic 81	org/vidogram/tgnet/TLRPC$TL_messages_stickerSet:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$TL_messages_stickerSet;
          //   115: invokevirtual 85	java/util/ArrayList:add	(Ljava/lang/Object;)Z
          //   118: pop
          //   119: iload_1
          //   120: iconst_1
          //   121: iadd
          //   122: istore_1
          //   123: goto -31 -> 92
          //   126: aload 6
          //   128: astore 7
          //   130: aload 9
          //   132: invokevirtual 88	org/vidogram/tgnet/NativeByteBuffer:reuse	()V
          //   135: aload 6
          //   137: astore 7
          //   139: aload 5
          //   141: iconst_1
          //   142: invokevirtual 92	org/vidogram/SQLite/SQLiteCursor:intValue	(I)I
          //   145: istore_1
          //   146: aload 6
          //   148: invokestatic 96	org/vidogram/messenger/query/StickersQuery:access$1900	(Ljava/util/ArrayList;)I
          //   151: istore_3
          //   152: iload_1
          //   153: istore_2
          //   154: iload_3
          //   155: istore_1
          //   156: iload_1
          //   157: istore_3
          //   158: iload_2
          //   159: istore 4
          //   161: aload 6
          //   163: astore 7
          //   165: aload 5
          //   167: ifnull +17 -> 184
          //   170: aload 5
          //   172: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   175: aload 6
          //   177: astore 7
          //   179: iload_2
          //   180: istore 4
          //   182: iload_1
          //   183: istore_3
          //   184: aload_0
          //   185: getfield 17	org/vidogram/messenger/query/StickersQuery$20:val$type	I
          //   188: aload 7
          //   190: iconst_1
          //   191: iload 4
          //   193: iload_3
          //   194: invokestatic 103	org/vidogram/messenger/query/StickersQuery:access$2000	(ILjava/util/ArrayList;ZII)V
          //   197: return
          //   198: astore 7
          //   200: aconst_null
          //   201: astore 5
          //   203: iconst_0
          //   204: istore_1
          //   205: aload 8
          //   207: astore 6
          //   209: aload 7
          //   211: invokestatic 109	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
          //   214: iload_2
          //   215: istore_3
          //   216: iload_1
          //   217: istore 4
          //   219: aload 6
          //   221: astore 7
          //   223: aload 5
          //   225: ifnull -41 -> 184
          //   228: aload 5
          //   230: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   233: iload_2
          //   234: istore_3
          //   235: iload_1
          //   236: istore 4
          //   238: aload 6
          //   240: astore 7
          //   242: goto -58 -> 184
          //   245: astore 6
          //   247: aconst_null
          //   248: astore 5
          //   250: aload 5
          //   252: ifnull +8 -> 260
          //   255: aload 5
          //   257: invokevirtual 99	org/vidogram/SQLite/SQLiteCursor:dispose	()V
          //   260: aload 6
          //   262: athrow
          //   263: astore 6
          //   265: goto -15 -> 250
          //   268: astore 6
          //   270: goto -20 -> 250
          //   273: astore 7
          //   275: iconst_0
          //   276: istore_1
          //   277: aload 8
          //   279: astore 6
          //   281: goto -72 -> 209
          //   284: astore 8
          //   286: aload 7
          //   288: astore 6
          //   290: aload 8
          //   292: astore 7
          //   294: iconst_0
          //   295: istore_1
          //   296: goto -87 -> 209
          //   299: astore 7
          //   301: goto -92 -> 209
          //   304: aconst_null
          //   305: astore 6
          //   307: goto -172 -> 135
          //   310: iconst_0
          //   311: istore_2
          //   312: goto -156 -> 156
          //
          // Exception table:
          //   from	to	target	type
          //   10	49	198	java/lang/Throwable
          //   10	49	245	finally
          //   49	65	263	finally
          //   70	79	263	finally
          //   83	90	263	finally
          //   101	119	263	finally
          //   130	135	263	finally
          //   139	146	263	finally
          //   146	152	263	finally
          //   209	214	268	finally
          //   49	65	273	java/lang/Throwable
          //   70	79	273	java/lang/Throwable
          //   83	90	284	java/lang/Throwable
          //   101	119	284	java/lang/Throwable
          //   130	135	284	java/lang/Throwable
          //   139	146	284	java/lang/Throwable
          //   146	152	299	java/lang/Throwable
        }
      });
      return;
    }
    if (paramInt == 0)
    {
      localObject1 = new TLRPC.TL_messages_getAllStickers();
      localObject2 = (TLRPC.TL_messages_getAllStickers)localObject1;
      if (paramBoolean2);
      while (true)
      {
        ((TLRPC.TL_messages_getAllStickers)localObject2).hash = i;
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject1, new RequestDelegate(paramInt, i)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
            {
              public void run()
              {
                if ((this.val$response instanceof TLRPC.TL_messages_allStickers))
                {
                  TLRPC.TL_messages_allStickers localTL_messages_allStickers = (TLRPC.TL_messages_allStickers)this.val$response;
                  ArrayList localArrayList = new ArrayList();
                  if (localTL_messages_allStickers.sets.isEmpty())
                  {
                    StickersQuery.access$2000(StickersQuery.21.this.val$type, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_allStickers.hash);
                    return;
                  }
                  HashMap localHashMap = new HashMap();
                  int i = 0;
                  label72: TLRPC.StickerSet localStickerSet;
                  Object localObject;
                  if (i < localTL_messages_allStickers.sets.size())
                  {
                    localStickerSet = (TLRPC.StickerSet)localTL_messages_allStickers.sets.get(i);
                    localObject = (TLRPC.TL_messages_stickerSet)StickersQuery.stickerSetsById.get(Long.valueOf(localStickerSet.id));
                    if ((localObject == null) || (((TLRPC.TL_messages_stickerSet)localObject).set.hash != localStickerSet.hash))
                      break label247;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.archived = localStickerSet.archived;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.installed = localStickerSet.installed;
                    ((TLRPC.TL_messages_stickerSet)localObject).set.official = localStickerSet.official;
                    localHashMap.put(Long.valueOf(((TLRPC.TL_messages_stickerSet)localObject).set.id), localObject);
                    localArrayList.add(localObject);
                    if (localHashMap.size() == localTL_messages_allStickers.sets.size())
                      StickersQuery.access$2000(StickersQuery.21.this.val$type, localArrayList, false, (int)(System.currentTimeMillis() / 1000L), localTL_messages_allStickers.hash);
                  }
                  while (true)
                  {
                    i += 1;
                    break label72;
                    break;
                    label247: localArrayList.add(null);
                    localObject = new TLRPC.TL_messages_getStickerSet();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset = new TLRPC.TL_inputStickerSetID();
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.id = localStickerSet.id;
                    ((TLRPC.TL_messages_getStickerSet)localObject).stickerset.access_hash = localStickerSet.access_hash;
                    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate(localArrayList, i, localHashMap, localStickerSet, localTL_messages_allStickers)
                    {
                      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                      {
                        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
                        {
                          public void run()
                          {
                            TLRPC.TL_messages_stickerSet localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.val$response;
                            StickersQuery.21.1.1.this.val$newStickerArray.set(StickersQuery.21.1.1.this.val$index, localTL_messages_stickerSet);
                            StickersQuery.21.1.1.this.val$newStickerSets.put(Long.valueOf(StickersQuery.21.1.1.this.val$stickerSet.id), localTL_messages_stickerSet);
                            if (StickersQuery.21.1.1.this.val$newStickerSets.size() == StickersQuery.21.1.1.this.val$res.sets.size())
                            {
                              int i = 0;
                              while (i < StickersQuery.21.1.1.this.val$newStickerArray.size())
                              {
                                if (StickersQuery.21.1.1.this.val$newStickerArray.get(i) == null)
                                  StickersQuery.21.1.1.this.val$newStickerArray.remove(i);
                                i += 1;
                              }
                              StickersQuery.access$2000(StickersQuery.21.this.val$type, StickersQuery.21.1.1.this.val$newStickerArray, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.21.1.1.this.val$res.hash);
                            }
                          }
                        });
                      }
                    });
                  }
                }
                StickersQuery.access$2000(StickersQuery.21.this.val$type, null, false, (int)(System.currentTimeMillis() / 1000L), StickersQuery.21.this.val$hash);
              }
            });
          }
        });
        return;
        i = loadHash[paramInt];
      }
    }
    Object localObject1 = new TLRPC.TL_messages_getMaskStickers();
    Object localObject2 = (TLRPC.TL_messages_getMaskStickers)localObject1;
    if (paramBoolean2);
    for (i = j; ; i = loadHash[paramInt])
    {
      ((TLRPC.TL_messages_getMaskStickers)localObject2).hash = i;
      break;
    }
  }

  public static void markFaturedStickersAsRead(boolean paramBoolean)
  {
    if (unreadStickerSets.isEmpty());
    do
    {
      return;
      unreadStickerSets.clear();
      loadFeaturedHash = calcFeaturedStickersHash(featuredStickerSets);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
      putFeaturedStickersToCache(featuredStickerSets, unreadStickerSets, loadFeaturedDate, loadFeaturedHash);
    }
    while (!paramBoolean);
    TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
    ConnectionsManager.getInstance().sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
  }

  public static void markFaturedStickersByIdAsRead(long paramLong)
  {
    if ((!unreadStickerSets.contains(Long.valueOf(paramLong))) || (readingStickerSets.contains(Long.valueOf(paramLong))))
      return;
    readingStickerSets.add(Long.valueOf(paramLong));
    TLRPC.TL_messages_readFeaturedStickers localTL_messages_readFeaturedStickers = new TLRPC.TL_messages_readFeaturedStickers();
    localTL_messages_readFeaturedStickers.id.add(Long.valueOf(paramLong));
    ConnectionsManager.getInstance().sendRequest(localTL_messages_readFeaturedStickers, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
    AndroidUtilities.runOnUIThread(new Runnable(paramLong)
    {
      public void run()
      {
        StickersQuery.unreadStickerSets.remove(Long.valueOf(this.val$id));
        StickersQuery.readingStickerSets.remove(Long.valueOf(this.val$id));
        StickersQuery.access$1102(StickersQuery.access$700(StickersQuery.featuredStickerSets));
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
        StickersQuery.access$1200(StickersQuery.featuredStickerSets, StickersQuery.unreadStickerSets, StickersQuery.loadFeaturedDate, StickersQuery.loadFeaturedHash);
      }
    }
    , 1000L);
  }

  private static void processLoadedFeaturedStickers(ArrayList<TLRPC.StickerSetCovered> paramArrayList, ArrayList<Long> paramArrayList1, boolean paramBoolean, int paramInt1, int paramInt2)
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        StickersQuery.access$902(false);
        StickersQuery.access$1002(true);
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable(paramBoolean, paramArrayList, paramInt1, paramInt2, paramArrayList1)
    {
      public void run()
      {
        long l = 1000L;
        Object localObject;
        if (((this.val$cache) && ((this.val$res == null) || (Math.abs(System.currentTimeMillis() / 1000L - this.val$date) >= 3600L))) || ((!this.val$cache) && (this.val$res == null) && (this.val$hash == 0)))
        {
          localObject = new Runnable()
          {
            public void run()
            {
              if ((StickersQuery.14.this.val$res != null) && (StickersQuery.14.this.val$hash != 0))
                StickersQuery.access$1102(StickersQuery.14.this.val$hash);
              StickersQuery.loadFeaturesStickers(false, false);
            }
          };
          if ((this.val$res == null) && (!this.val$cache))
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject, l);
            if (this.val$res != null)
              break label105;
          }
        }
        label105: 
        do
        {
          return;
          l = 0L;
          break;
          if (this.val$res == null)
            continue;
          try
          {
            localObject = new ArrayList();
            HashMap localHashMap = new HashMap();
            int i = 0;
            while (i < this.val$res.size())
            {
              TLRPC.StickerSetCovered localStickerSetCovered = (TLRPC.StickerSetCovered)this.val$res.get(i);
              ((ArrayList)localObject).add(localStickerSetCovered);
              localHashMap.put(Long.valueOf(localStickerSetCovered.set.id), localStickerSetCovered);
              i += 1;
            }
            if (!this.val$cache)
              StickersQuery.access$1200((ArrayList)localObject, this.val$unreadStickers, this.val$date, this.val$hash);
            AndroidUtilities.runOnUIThread(new Runnable(localHashMap, (ArrayList)localObject)
            {
              public void run()
              {
                StickersQuery.access$1302(StickersQuery.14.this.val$unreadStickers);
                StickersQuery.access$1402(this.val$stickerSetsByIdNew);
                StickersQuery.access$1502(this.val$stickerSetsNew);
                StickersQuery.access$1102(StickersQuery.14.this.val$hash);
                StickersQuery.access$1602(StickersQuery.14.this.val$date);
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.featuredStickersDidLoaded, new Object[0]);
              }
            });
            return;
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
            return;
          }
        }
        while (this.val$cache);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            StickersQuery.access$1602(StickersQuery.14.this.val$date);
          }
        });
        StickersQuery.access$1200(null, null, this.val$date, 0);
      }
    });
  }

  private static void processLoadedRecentDocuments(int paramInt1, ArrayList<TLRPC.Document> paramArrayList, boolean paramBoolean, int paramInt2)
  {
    if (paramArrayList != null)
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramBoolean, paramArrayList, paramInt1, paramInt2)
      {
        public void run()
        {
          while (true)
          {
            int i;
            int m;
            int k;
            try
            {
              SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance().getDatabase();
              if (!this.val$gif)
                continue;
              i = MessagesController.getInstance().maxRecentGifsCount;
              localSQLiteDatabase.beginTransaction();
              SQLitePreparedStatement localSQLitePreparedStatement = localSQLiteDatabase.executeFast("REPLACE INTO web_recent_v3 VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
              m = this.val$documents.size();
              k = 0;
              break label358;
              localSQLitePreparedStatement.dispose();
              localSQLiteDatabase.commitTransaction();
              if (this.val$documents.size() < i)
                continue;
              localSQLiteDatabase.beginTransaction();
              if (i >= this.val$documents.size())
                continue;
              localSQLiteDatabase.executeFast("DELETE FROM web_recent_v3 WHERE id = '" + ((TLRPC.Document)this.val$documents.get(i)).id + "'").stepThis().dispose();
              i += 1;
              continue;
              i = MessagesController.getInstance().maxRecentStickersCount;
              continue;
              label152: TLRPC.Document localDocument = (TLRPC.Document)this.val$documents.get(k);
              localSQLitePreparedStatement.requery();
              localSQLitePreparedStatement.bindString(1, "" + localDocument.id);
              if (!this.val$gif)
                continue;
              j = 2;
              localSQLitePreparedStatement.bindInteger(2, j);
              localSQLitePreparedStatement.bindString(3, "");
              localSQLitePreparedStatement.bindString(4, "");
              localSQLitePreparedStatement.bindString(5, "");
              localSQLitePreparedStatement.bindInteger(6, 0);
              localSQLitePreparedStatement.bindInteger(7, 0);
              localSQLitePreparedStatement.bindInteger(8, 0);
              if (this.val$date == 0)
                break label384;
              j = this.val$date;
              localSQLitePreparedStatement.bindInteger(9, j);
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(localDocument.getObjectSize());
              localDocument.serializeToStream(localNativeByteBuffer);
              localSQLitePreparedStatement.bindByteBuffer(10, localNativeByteBuffer);
              localSQLitePreparedStatement.step();
              if (localNativeByteBuffer != null)
              {
                localNativeByteBuffer.reuse();
                break label372;
                if (this.val$type != 0)
                  break label379;
                j = 3;
                continue;
                localSQLiteDatabase.commitTransaction();
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            label358: 
            while (k < m)
            {
              if (k != i)
                break label152;
              break;
              label372: k += 1;
            }
            label379: int j = 4;
            continue;
            label384: j = m - k;
          }
        }
      });
    if (paramInt2 == 0)
      AndroidUtilities.runOnUIThread(new Runnable(paramBoolean, paramInt1, paramArrayList)
      {
        public void run()
        {
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit();
          if (this.val$gif)
          {
            StickersQuery.access$102(false);
            StickersQuery.access$202(true);
            localEditor.putLong("lastGifLoadTime", System.currentTimeMillis()).commit();
            if (this.val$documents != null)
            {
              if (!this.val$gif)
                break label143;
              StickersQuery.access$002(this.val$documents);
            }
          }
          while (true)
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.recentDocumentsDidLoaded, new Object[] { Boolean.valueOf(this.val$gif), Integer.valueOf(this.val$type) });
            return;
            StickersQuery.loadingRecentStickers[this.val$type] = 0;
            StickersQuery.recentStickersLoaded[this.val$type] = 1;
            localEditor.putLong("lastStickersLoadTime", System.currentTimeMillis()).commit();
            break;
            label143: StickersQuery.recentStickers[this.val$type] = this.val$documents;
          }
        }
      });
  }

  private static void processLoadedStickers(int paramInt1, ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, boolean paramBoolean, int paramInt2, int paramInt3)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInt1)
    {
      public void run()
      {
        StickersQuery.loadingStickers[this.val$type] = 0;
        StickersQuery.stickersLoaded[this.val$type] = 1;
      }
    });
    Utilities.stageQueue.postRunnable(new Runnable(paramBoolean, paramArrayList, paramInt2, paramInt3, paramInt1)
    {
      public void run()
      {
        long l = 1000L;
        Object localObject1;
        if (((this.val$cache) && ((this.val$res == null) || (Math.abs(System.currentTimeMillis() / 1000L - this.val$date) >= 3600L))) || ((!this.val$cache) && (this.val$res == null) && (this.val$hash == 0)))
        {
          localObject1 = new Runnable()
          {
            public void run()
            {
              if ((StickersQuery.24.this.val$res != null) && (StickersQuery.24.this.val$hash != 0))
                StickersQuery.loadHash[StickersQuery.24.this.val$type] = StickersQuery.24.this.val$hash;
              StickersQuery.loadStickers(StickersQuery.24.this.val$type, false, false);
            }
          };
          if ((this.val$res == null) && (!this.val$cache));
          while (true)
          {
            AndroidUtilities.runOnUIThread((Runnable)localObject1, l);
            if (this.val$res != null)
              break;
            return;
            l = 0L;
          }
        }
        ArrayList localArrayList2;
        HashMap localHashMap1;
        HashMap localHashMap2;
        HashMap localHashMap3;
        HashMap localHashMap4;
        HashMap localHashMap5;
        int i;
        label171: int j;
        label248: TLRPC.TL_stickerPack localTL_stickerPack;
        label330: ArrayList localArrayList1;
        if (this.val$res != null)
        {
          TLRPC.TL_messages_stickerSet localTL_messages_stickerSet;
          try
          {
            localArrayList2 = new ArrayList();
            localHashMap1 = new HashMap();
            localHashMap2 = new HashMap();
            localHashMap3 = new HashMap();
            localHashMap4 = new HashMap();
            localHashMap5 = new HashMap();
            i = 0;
            if (i >= this.val$res.size())
              break label511;
            localTL_messages_stickerSet = (TLRPC.TL_messages_stickerSet)this.val$res.get(i);
            if (localTL_messages_stickerSet == null)
              break label592;
            localArrayList2.add(localTL_messages_stickerSet);
            localHashMap1.put(Long.valueOf(localTL_messages_stickerSet.set.id), localTL_messages_stickerSet);
            localHashMap2.put(localTL_messages_stickerSet.set.short_name, localTL_messages_stickerSet);
            j = 0;
            if (j < localTL_messages_stickerSet.documents.size())
            {
              localObject1 = (TLRPC.Document)localTL_messages_stickerSet.documents.get(j);
              if ((localObject1 == null) || ((localObject1 instanceof TLRPC.TL_documentEmpty)))
                break label599;
              localHashMap4.put(Long.valueOf(((TLRPC.Document)localObject1).id), localObject1);
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
            return;
          }
          if (localTL_messages_stickerSet.set.archived)
            break label592;
          j = 0;
          if (j >= localTL_messages_stickerSet.packs.size())
            break label592;
          localTL_stickerPack = (TLRPC.TL_stickerPack)localTL_messages_stickerSet.packs.get(j);
          if ((localTL_stickerPack == null) || (localTL_stickerPack.emoticon == null))
            break label606;
          localTL_stickerPack.emoticon = localTL_stickerPack.emoticon.replace("️", "");
          localArrayList1 = (ArrayList)localHashMap5.get(localTL_stickerPack.emoticon);
          if (localArrayList1 != null)
            break label589;
          localArrayList1 = new ArrayList();
          localHashMap5.put(localTL_stickerPack.emoticon, localArrayList1);
          break label613;
        }
        while (true)
        {
          if (k < localTL_stickerPack.documents.size())
          {
            Object localObject2 = (Long)localTL_stickerPack.documents.get(k);
            if (!localHashMap3.containsKey(localObject2))
              localHashMap3.put(localObject2, localTL_stickerPack.emoticon);
            localObject2 = (TLRPC.Document)localHashMap4.get(localObject2);
            if (localObject2 == null)
              break label618;
            localArrayList1.add(localObject2);
            break label618;
            label511: if (!this.val$cache)
              StickersQuery.access$2500(this.val$type, localArrayList2, this.val$date, this.val$hash);
            AndroidUtilities.runOnUIThread(new Runnable(localHashMap1, localHashMap2, localArrayList2, localHashMap5, localHashMap3)
            {
              public void run()
              {
                int i = 0;
                while (i < StickersQuery.stickerSets[StickersQuery.24.this.val$type].size())
                {
                  TLRPC.StickerSet localStickerSet = ((TLRPC.TL_messages_stickerSet)StickersQuery.stickerSets[StickersQuery.24.this.val$type].get(i)).set;
                  StickersQuery.stickerSetsById.remove(Long.valueOf(localStickerSet.id));
                  StickersQuery.stickerSetsByName.remove(localStickerSet.short_name);
                  i += 1;
                }
                StickersQuery.stickerSetsById.putAll(this.val$stickerSetsByIdNew);
                StickersQuery.stickerSetsByName.putAll(this.val$stickerSetsByNameNew);
                StickersQuery.stickerSets[StickersQuery.24.this.val$type] = this.val$stickerSetsNew;
                StickersQuery.loadHash[StickersQuery.24.this.val$type] = StickersQuery.24.this.val$hash;
                StickersQuery.loadDate[StickersQuery.24.this.val$type] = StickersQuery.24.this.val$date;
                if (StickersQuery.24.this.val$type == 0)
                {
                  StickersQuery.access$2902(this.val$allStickersNew);
                  StickersQuery.access$3002(this.val$stickersByEmojiNew);
                }
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(StickersQuery.24.this.val$type) });
              }
            });
            return;
            if (this.val$cache)
              break;
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                StickersQuery.loadDate[StickersQuery.24.this.val$type] = StickersQuery.24.this.val$date;
              }
            });
            StickersQuery.access$2500(this.val$type, null, this.val$date, 0);
            return;
            label589: break label613;
            label592: i += 1;
            break label171;
            label599: j += 1;
            break label248;
          }
          else
          {
            label606: j += 1;
            break label330;
          }
          label613: int k = 0;
          continue;
          label618: k += 1;
        }
      }
    });
  }

  private static void putFeaturedStickersToCache(ArrayList<TLRPC.StickerSetCovered> paramArrayList, ArrayList<Long> paramArrayList1, int paramInt1, int paramInt2)
  {
    if (paramArrayList != null);
    for (paramArrayList = new ArrayList(paramArrayList); ; paramArrayList = null)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList, paramArrayList1, paramInt1, paramInt2)
      {
        public void run()
        {
          int k = 0;
          try
          {
            if (this.val$stickersFinal != null)
            {
              localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_featured VALUES(?, ?, ?, ?, ?)");
              localSQLitePreparedStatement.requery();
              int i = 0;
              int j = 4;
              while (i < this.val$stickersFinal.size())
              {
                j += ((TLRPC.StickerSetCovered)this.val$stickersFinal.get(i)).getObjectSize();
                i += 1;
              }
              NativeByteBuffer localNativeByteBuffer1 = new NativeByteBuffer(j);
              NativeByteBuffer localNativeByteBuffer2 = new NativeByteBuffer(this.val$unreadStickers.size() * 8 + 4);
              localNativeByteBuffer1.writeInt32(this.val$stickersFinal.size());
              i = 0;
              while (i < this.val$stickersFinal.size())
              {
                ((TLRPC.StickerSetCovered)this.val$stickersFinal.get(i)).serializeToStream(localNativeByteBuffer1);
                i += 1;
              }
              localNativeByteBuffer2.writeInt32(this.val$unreadStickers.size());
              i = k;
              while (i < this.val$unreadStickers.size())
              {
                localNativeByteBuffer2.writeInt64(((Long)this.val$unreadStickers.get(i)).longValue());
                i += 1;
              }
              localSQLitePreparedStatement.bindInteger(1, 1);
              localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer1);
              localSQLitePreparedStatement.bindByteBuffer(3, localNativeByteBuffer2);
              localSQLitePreparedStatement.bindInteger(4, this.val$date);
              localSQLitePreparedStatement.bindInteger(5, this.val$hash);
              localSQLitePreparedStatement.step();
              localNativeByteBuffer1.reuse();
              localNativeByteBuffer2.reuse();
              localSQLitePreparedStatement.dispose();
              return;
            }
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_featured SET date = ?");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindInteger(1, this.val$date);
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
      return;
    }
  }

  private static void putStickersToCache(int paramInt1, ArrayList<TLRPC.TL_messages_stickerSet> paramArrayList, int paramInt2, int paramInt3)
  {
    if (paramArrayList != null);
    for (paramArrayList = new ArrayList(paramArrayList); ; paramArrayList = null)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList, paramInt1, paramInt2, paramInt3)
      {
        public void run()
        {
          int k = 0;
          while (true)
          {
            try
            {
              if (this.val$stickersFinal == null)
                continue;
              SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO stickers_v2 VALUES(?, ?, ?, ?)");
              localSQLitePreparedStatement.requery();
              i = 0;
              int j = 4;
              if (i >= this.val$stickersFinal.size())
                continue;
              j += ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).getObjectSize();
              i += 1;
              continue;
              NativeByteBuffer localNativeByteBuffer = new NativeByteBuffer(j);
              localNativeByteBuffer.writeInt32(this.val$stickersFinal.size());
              i = k;
              if (i >= this.val$stickersFinal.size())
                continue;
              ((TLRPC.TL_messages_stickerSet)this.val$stickersFinal.get(i)).serializeToStream(localNativeByteBuffer);
              i += 1;
              continue;
              if (this.val$type == 0)
              {
                i = 1;
                localSQLitePreparedStatement.bindInteger(1, i);
                localSQLitePreparedStatement.bindByteBuffer(2, localNativeByteBuffer);
                localSQLitePreparedStatement.bindInteger(3, this.val$date);
                localSQLitePreparedStatement.bindInteger(4, this.val$hash);
                localSQLitePreparedStatement.step();
                localNativeByteBuffer.reuse();
                localSQLitePreparedStatement.dispose();
                return;
                localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("UPDATE stickers_v2 SET date = ?");
                localSQLitePreparedStatement.requery();
                localSQLitePreparedStatement.bindInteger(1, this.val$date);
                localSQLitePreparedStatement.step();
                localSQLitePreparedStatement.dispose();
                return;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            int i = 2;
          }
        }
      });
      return;
    }
  }

  public static void removeRecentGif(TLRPC.Document paramDocument)
  {
    recentGifs.remove(paramDocument);
    TLRPC.TL_messages_saveGif localTL_messages_saveGif = new TLRPC.TL_messages_saveGif();
    localTL_messages_saveGif.id = new TLRPC.TL_inputDocument();
    localTL_messages_saveGif.id.id = paramDocument.id;
    localTL_messages_saveGif.id.access_hash = paramDocument.access_hash;
    localTL_messages_saveGif.unsave = true;
    ConnectionsManager.getInstance().sendRequest(localTL_messages_saveGif, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
      }
    });
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramDocument)
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM web_recent_v3 WHERE id = '" + this.val$document.id + "'").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void removeStickersSet(Context paramContext, TLRPC.StickerSet paramStickerSet, int paramInt, BaseFragment paramBaseFragment, boolean paramBoolean)
  {
    boolean bool2 = true;
    int i;
    TLRPC.TL_inputStickerSetID localTL_inputStickerSetID;
    label52: int j;
    if (paramStickerSet.masks)
    {
      i = 1;
      localTL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
      localTL_inputStickerSetID.access_hash = paramStickerSet.access_hash;
      localTL_inputStickerSetID.id = paramStickerSet.id;
      if (paramInt == 0)
        break label304;
      if (paramInt != 1)
        break label249;
      bool1 = true;
      paramStickerSet.archived = bool1;
      j = 0;
      label61: if (j < stickerSets[i].size())
      {
        paramContext = (TLRPC.TL_messages_stickerSet)stickerSets[i].get(j);
        if (paramContext.set.id != paramStickerSet.id)
          break label289;
        stickerSets[i].remove(j);
        if (paramInt != 2)
          break label255;
        stickerSets[i].add(0, paramContext);
      }
      label133: loadHash[i] = calcStickersHash(stickerSets[i]);
      putStickersToCache(i, stickerSets[i], loadDate[i], loadHash[i]);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(i) });
      paramContext = new TLRPC.TL_messages_installStickerSet();
      paramContext.stickerset = localTL_inputStickerSetID;
      if (paramInt != 1)
        break label298;
    }
    label289: label298: for (boolean bool1 = bool2; ; bool1 = false)
    {
      paramContext.archived = bool1;
      ConnectionsManager.getInstance().sendRequest(paramContext, new RequestDelegate(i, paramInt, paramBaseFragment, paramBoolean)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              Activity localActivity;
              if ((this.val$response instanceof TLRPC.TL_messages_stickerSetInstallResultArchive))
              {
                NotificationCenter.getInstance().postNotificationName(NotificationCenter.needReloadArchivedStickers, new Object[] { Integer.valueOf(StickersQuery.25.this.val$type) });
                if ((StickersQuery.25.this.val$hide != 1) && (StickersQuery.25.this.val$baseFragment != null) && (StickersQuery.25.this.val$baseFragment.getParentActivity() != null))
                {
                  localActivity = StickersQuery.25.this.val$baseFragment.getParentActivity();
                  if (!StickersQuery.25.this.val$showSettings)
                    break label135;
                }
              }
              label135: for (Object localObject = StickersQuery.25.this.val$baseFragment; ; localObject = null)
              {
                localObject = new StickersArchiveAlert(localActivity, (BaseFragment)localObject, ((TLRPC.TL_messages_stickerSetInstallResultArchive)this.val$response).sets);
                StickersQuery.25.this.val$baseFragment.showDialog(((StickersArchiveAlert)localObject).create());
                return;
              }
            }
          });
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              StickersQuery.loadStickers(StickersQuery.25.this.val$type, false, false);
            }
          }
          , 1000L);
        }
      });
      return;
      i = 0;
      break;
      label249: bool1 = false;
      break label52;
      label255: stickerSetsById.remove(Long.valueOf(paramContext.set.id));
      stickerSetsByName.remove(paramContext.set.short_name);
      break label133;
      j += 1;
      break label61;
    }
    label304: paramBaseFragment = new TLRPC.TL_messages_uninstallStickerSet();
    paramBaseFragment.stickerset = localTL_inputStickerSetID;
    ConnectionsManager.getInstance().sendRequest(paramBaseFragment, new RequestDelegate(paramStickerSet, paramContext, i)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
        {
          public void run()
          {
            try
            {
              if (this.val$error == null)
              {
                if (StickersQuery.26.this.val$stickerSet.masks)
                  Toast.makeText(StickersQuery.26.this.val$context, LocaleController.getString("MasksRemoved", 2131165938), 0).show();
                while (true)
                {
                  StickersQuery.loadStickers(StickersQuery.26.this.val$type, false, true);
                  return;
                  Toast.makeText(StickersQuery.26.this.val$context, LocaleController.getString("StickersRemoved", 2131166489), 0).show();
                }
              }
            }
            catch (Exception localException)
            {
              while (true)
              {
                FileLog.e(localException);
                continue;
                Toast.makeText(StickersQuery.26.this.val$context, LocaleController.getString("ErrorOccurred", 2131165701), 0).show();
              }
            }
          }
        });
      }
    });
  }

  public static void reorderStickers(int paramInt, ArrayList<Long> paramArrayList)
  {
    Collections.sort(stickerSets[paramInt], new Comparator(paramArrayList)
    {
      public int compare(TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet1, TLRPC.TL_messages_stickerSet paramTL_messages_stickerSet2)
      {
        int i = this.val$order.indexOf(Long.valueOf(paramTL_messages_stickerSet1.set.id));
        int j = this.val$order.indexOf(Long.valueOf(paramTL_messages_stickerSet2.set.id));
        if (i > j)
          return 1;
        if (i < j)
          return -1;
        return 0;
      }
    });
    loadHash[paramInt] = calcStickersHash(stickerSets[paramInt]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.stickersDidLoaded, new Object[] { Integer.valueOf(paramInt) });
    loadStickers(paramInt, false, true);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.StickersQuery
 * JD-Core Version:    0.6.0
 */