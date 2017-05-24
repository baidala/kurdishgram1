package org.vidogram.messenger.query;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutInfo.Builder;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Build.VERSION;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.OpenChatReceiver;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_contacts_getTopPeers;
import org.vidogram.tgnet.TLRPC.TL_contacts_resetTopPeerRating;
import org.vidogram.tgnet.TLRPC.TL_contacts_topPeers;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_peerChat;
import org.vidogram.tgnet.TLRPC.TL_peerUser;
import org.vidogram.tgnet.TLRPC.TL_topPeer;
import org.vidogram.tgnet.TLRPC.TL_topPeerCategoryBotsInline;
import org.vidogram.tgnet.TLRPC.TL_topPeerCategoryCorrespondents;
import org.vidogram.tgnet.TLRPC.TL_topPeerCategoryPeers;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.LaunchActivity;

public class SearchQuery
{
  private static RectF bitmapRect;
  public static ArrayList<TLRPC.TL_topPeer> hints = new ArrayList();
  public static ArrayList<TLRPC.TL_topPeer> inlineBots = new ArrayList();
  private static HashMap<Integer, Integer> inlineDates = new HashMap();
  private static boolean loaded;
  private static boolean loading;
  private static Paint roundPaint;

  public static void buildShortcuts()
  {
    if (Build.VERSION.SDK_INT < 25)
      return;
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (true)
    {
      if (i < hints.size())
      {
        localArrayList.add(hints.get(i));
        if (localArrayList.size() != 3);
      }
      else
      {
        Utilities.globalQueue.postRunnable(new Runnable(localArrayList)
        {
          @SuppressLint({"NewApi"})
          public void run()
          {
            ShortcutManager localShortcutManager = (ShortcutManager)ApplicationLoader.applicationContext.getSystemService(ShortcutManager.class);
            Object localObject2 = localShortcutManager.getDynamicShortcuts();
            ArrayList localArrayList1 = new ArrayList();
            Object localObject3 = new ArrayList();
            Object localObject1 = new ArrayList();
            int i;
            Object localObject4;
            long l1;
            if ((localObject2 != null) && (!((List)localObject2).isEmpty()))
            {
              ((ArrayList)localObject3).add("compose");
              i = 0;
              if (i < this.val$hintsFinal.size())
              {
                localObject4 = (TLRPC.TL_topPeer)this.val$hintsFinal.get(i);
                if (((TLRPC.TL_topPeer)localObject4).peer.user_id != 0)
                  l1 = ((TLRPC.TL_topPeer)localObject4).peer.user_id;
                while (true)
                {
                  ((ArrayList)localObject3).add("did" + l1);
                  i += 1;
                  break;
                  long l2 = -((TLRPC.TL_topPeer)localObject4).peer.chat_id;
                  l1 = l2;
                  if (l2 != 0L)
                    continue;
                  l1 = -((TLRPC.TL_topPeer)localObject4).peer.channel_id;
                }
              }
              i = 0;
              while (i < ((List)localObject2).size())
              {
                localObject4 = ((ShortcutInfo)((List)localObject2).get(i)).getId();
                if (!((ArrayList)localObject3).remove(localObject4))
                  ((ArrayList)localObject1).add(localObject4);
                localArrayList1.add(localObject4);
                i += 1;
              }
              if ((((ArrayList)localObject3).isEmpty()) && (((ArrayList)localObject1).isEmpty()))
                return;
            }
            localObject2 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
            ((Intent)localObject2).setAction("new_dialog");
            ArrayList localArrayList2 = new ArrayList();
            localArrayList2.add(new ShortcutInfo.Builder(ApplicationLoader.applicationContext, "compose").setShortLabel(LocaleController.getString("NewConversationShortcut", 2131166008)).setLongLabel(LocaleController.getString("NewConversationShortcut", 2131166008)).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, 2130838063)).setIntent((Intent)localObject2).build());
            if (localArrayList1.contains("compose"))
              localShortcutManager.updateShortcuts(localArrayList2);
            Intent localIntent;
            while (true)
            {
              localArrayList2.clear();
              if (!((ArrayList)localObject1).isEmpty())
                localShortcutManager.removeDynamicShortcuts((List)localObject1);
              i = 0;
              while (i < this.val$hintsFinal.size())
              {
                localIntent = new Intent(ApplicationLoader.applicationContext, OpenChatReceiver.class);
                localObject1 = (TLRPC.TL_topPeer)this.val$hintsFinal.get(i);
                if (((TLRPC.TL_topPeer)localObject1).peer.user_id == 0)
                  break label522;
                localIntent.putExtra("userId", ((TLRPC.TL_topPeer)localObject1).peer.user_id);
                localObject4 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)localObject1).peer.user_id));
                l1 = ((TLRPC.TL_topPeer)localObject1).peer.user_id;
                localObject3 = null;
                if ((localObject4 != null) || (localObject3 != null))
                  break label576;
                i += 1;
              }
              break;
              localShortcutManager.addDynamicShortcuts(localArrayList2);
            }
            label522: int j = ((TLRPC.TL_topPeer)localObject1).peer.chat_id;
            if (j == 0)
              j = ((TLRPC.TL_topPeer)localObject1).peer.channel_id;
            while (true)
            {
              localObject3 = MessagesController.getInstance().getChat(Integer.valueOf(j));
              localIntent.putExtra("chatId", j);
              l1 = -j;
              localObject4 = null;
              break;
              label576: localObject1 = null;
              if (localObject4 != null)
              {
                localObject3 = ContactsController.formatName(((TLRPC.User)localObject4).first_name, ((TLRPC.User)localObject4).last_name);
                localObject2 = localObject3;
                if (((TLRPC.User)localObject4).photo != null)
                {
                  localObject1 = ((TLRPC.User)localObject4).photo.photo_small;
                  localObject2 = localObject3;
                }
                label625: localIntent.setAction("com.tmessages.openchat" + l1);
                localIntent.addFlags(67108864);
                if (localObject1 == null)
                  break label1068;
              }
              while (true)
              {
                try
                {
                  localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject1, true).toString());
                  if (localObject1 == null)
                    continue;
                }
                catch (Throwable localThrowable3)
                {
                  try
                  {
                    while (true)
                    {
                      j = AndroidUtilities.dp(48.0F);
                      localObject3 = Bitmap.createBitmap(j, j, Bitmap.Config.ARGB_8888);
                      ((Bitmap)localObject3).eraseColor(0);
                      localObject4 = new Canvas((Bitmap)localObject3);
                      BitmapShader localBitmapShader = new BitmapShader((Bitmap)localObject1, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                      if (SearchQuery.roundPaint == null)
                      {
                        SearchQuery.access$002(new Paint(1));
                        SearchQuery.access$102(new RectF());
                      }
                      float f = j / ((Bitmap)localObject1).getWidth();
                      ((Canvas)localObject4).scale(f, f);
                      SearchQuery.roundPaint.setShader(localBitmapShader);
                      SearchQuery.bitmapRect.set(AndroidUtilities.dp(2.0F), AndroidUtilities.dp(2.0F), AndroidUtilities.dp(46.0F), AndroidUtilities.dp(46.0F));
                      ((Canvas)localObject4).drawRoundRect(SearchQuery.bitmapRect, ((Bitmap)localObject1).getWidth(), ((Bitmap)localObject1).getHeight(), SearchQuery.roundPaint);
                      try
                      {
                        ((Canvas)localObject4).setBitmap(null);
                        localObject1 = localObject3;
                        label852: localObject4 = "did" + l1;
                        localObject3 = localObject2;
                        if (TextUtils.isEmpty((CharSequence)localObject2))
                          localObject3 = " ";
                        localObject2 = new ShortcutInfo.Builder(ApplicationLoader.applicationContext, (String)localObject4).setShortLabel((CharSequence)localObject3).setLongLabel((CharSequence)localObject3).setIntent(localIntent);
                        if (localObject1 != null)
                        {
                          ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithBitmap((Bitmap)localObject1));
                          label936: localArrayList2.add(((ShortcutInfo.Builder)localObject2).build());
                          if (!localArrayList1.contains(localObject4))
                            break label1040;
                          localShortcutManager.updateShortcuts(localArrayList2);
                        }
                        while (true)
                        {
                          localArrayList2.clear();
                          break;
                          localObject4 = ((TLRPC.Chat)localObject3).title;
                          localObject2 = localObject4;
                          if (((TLRPC.Chat)localObject3).photo == null)
                            break label625;
                          localObject1 = ((TLRPC.Chat)localObject3).photo.photo_small;
                          localObject2 = localObject4;
                          break label625;
                          localThrowable2 = localThrowable2;
                          localObject1 = null;
                          FileLog.e(localThrowable2);
                          break label852;
                          ((ShortcutInfo.Builder)localObject2).setIcon(Icon.createWithResource(ApplicationLoader.applicationContext, 2130838064));
                          break label936;
                          label1040: localShortcutManager.addDynamicShortcuts(localArrayList2);
                        }
                      }
                      catch (Exception localThrowable1)
                      {
                        while (true)
                          localThrowable1 = localThrowable2;
                      }
                    }
                  }
                  catch (Throwable localThrowable3)
                  {
                    continue;
                  }
                  continue;
                }
                label1068: Throwable localThrowable1 = null;
              }
            }
          }
        });
        return;
      }
      i += 1;
    }
  }

  public static void cleanup()
  {
    loading = false;
    loaded = false;
    hints.clear();
    inlineBots.clear();
    inlineDates.clear();
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
  }

  private static void deletePeer(int paramInt1, int paramInt2)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt1, paramInt2)
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast(String.format(Locale.US, "DELETE FROM chat_hints WHERE did = %d AND type = %d", new Object[] { Integer.valueOf(this.val$did), Integer.valueOf(this.val$type) })).stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public static void increaseInlineRaiting(int paramInt)
  {
    Object localObject1 = (Integer)inlineDates.get(Integer.valueOf(paramInt));
    int i;
    int j;
    if (localObject1 != null)
    {
      i = Math.max(1, (int)(System.currentTimeMillis() / 1000L) - ((Integer)localObject1).intValue());
      j = 0;
      label38: if (j >= inlineBots.size())
        break label215;
      localObject1 = (TLRPC.TL_topPeer)inlineBots.get(j);
      if (((TLRPC.TL_topPeer)localObject1).peer.user_id != paramInt)
        break label208;
    }
    while (true)
    {
      Object localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject2 = new TLRPC.TL_topPeer();
        ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
        ((TLRPC.TL_topPeer)localObject2).peer.user_id = paramInt;
        inlineBots.add(localObject2);
      }
      ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(i / MessagesController.getInstance().ratingDecay);
      Collections.sort(inlineBots, new Comparator()
      {
        public int compare(TLRPC.TL_topPeer paramTL_topPeer1, TLRPC.TL_topPeer paramTL_topPeer2)
        {
          if (paramTL_topPeer1.rating > paramTL_topPeer2.rating)
            return -1;
          if (paramTL_topPeer1.rating < paramTL_topPeer2.rating)
            return 1;
          return 0;
        }
      });
      if (inlineBots.size() > 20)
        inlineBots.remove(inlineBots.size() - 1);
      savePeer(paramInt, 1, ((TLRPC.TL_topPeer)localObject2).rating);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
      return;
      i = 60;
      break;
      label208: j += 1;
      break label38;
      label215: localObject1 = null;
    }
  }

  public static void increasePeerRaiting(long paramLong)
  {
    int i = (int)paramLong;
    if (i <= 0);
    while (true)
    {
      return;
      if (i > 0);
      for (TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(i)); (localUser != null) && (!localUser.bot); localUser = null)
      {
        MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong, i)
        {
          public void run()
          {
            int i = 0;
            double d2 = 0.0D;
            double d3 = d2;
            try
            {
              SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT MAX(mid), MAX(date) FROM messages WHERE uid = %d AND out = 1", new Object[] { Long.valueOf(this.val$did) }), new Object[0]);
              d3 = d2;
              if (localSQLiteCursor.next())
              {
                d3 = d2;
                i = localSQLiteCursor.intValue(0);
                d3 = d2;
                j = localSQLiteCursor.intValue(1);
                d3 = d2;
                localSQLiteCursor.dispose();
                d1 = d2;
                if (i > 0)
                {
                  d3 = d2;
                  localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT date FROM messages WHERE uid = %d AND mid < %d AND out = 1 ORDER BY date DESC", new Object[] { Long.valueOf(this.val$did), Integer.valueOf(i) }), new Object[0]);
                  d1 = d2;
                  d3 = d2;
                  if (localSQLiteCursor.next())
                  {
                    d3 = d2;
                    d1 = j - localSQLiteCursor.intValue(0);
                  }
                  d3 = d1;
                  localSQLiteCursor.dispose();
                }
                AndroidUtilities.runOnUIThread(new Runnable(d1)
                {
                  public void run()
                  {
                    int i = 0;
                    Object localObject2;
                    if (i < SearchQuery.hints.size())
                    {
                      localObject2 = (TLRPC.TL_topPeer)SearchQuery.hints.get(i);
                      if (SearchQuery.7.this.val$lower_id < 0)
                      {
                        localObject1 = localObject2;
                        if (((TLRPC.TL_topPeer)localObject2).peer.chat_id != -SearchQuery.7.this.val$lower_id)
                        {
                          localObject1 = localObject2;
                          if (((TLRPC.TL_topPeer)localObject2).peer.channel_id == -SearchQuery.7.this.val$lower_id);
                        }
                      }
                      else
                      {
                        if ((SearchQuery.7.this.val$lower_id <= 0) || (((TLRPC.TL_topPeer)localObject2).peer.user_id != SearchQuery.7.this.val$lower_id))
                          break label227;
                      }
                    }
                    for (Object localObject1 = localObject2; ; localObject1 = null)
                    {
                      localObject2 = localObject1;
                      if (localObject1 == null)
                      {
                        localObject2 = new TLRPC.TL_topPeer();
                        if (SearchQuery.7.this.val$lower_id <= 0)
                          break label234;
                        ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerUser();
                        ((TLRPC.TL_topPeer)localObject2).peer.user_id = SearchQuery.7.this.val$lower_id;
                      }
                      while (true)
                      {
                        SearchQuery.hints.add(localObject2);
                        ((TLRPC.TL_topPeer)localObject2).rating += Math.exp(this.val$dtFinal / MessagesController.getInstance().ratingDecay);
                        Collections.sort(SearchQuery.hints, new Comparator()
                        {
                          public int compare(TLRPC.TL_topPeer paramTL_topPeer1, TLRPC.TL_topPeer paramTL_topPeer2)
                          {
                            if (paramTL_topPeer1.rating > paramTL_topPeer2.rating)
                              return -1;
                            if (paramTL_topPeer1.rating < paramTL_topPeer2.rating)
                              return 1;
                            return 0;
                          }
                        });
                        SearchQuery.access$500((int)SearchQuery.7.this.val$did, 0, ((TLRPC.TL_topPeer)localObject2).rating);
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
                        return;
                        label227: i += 1;
                        break;
                        label234: ((TLRPC.TL_topPeer)localObject2).peer = new TLRPC.TL_peerChat();
                        ((TLRPC.TL_topPeer)localObject2).peer.chat_id = (-SearchQuery.7.this.val$lower_id);
                      }
                    }
                  }
                });
                return;
              }
            }
            catch (Exception localException)
            {
              while (true)
              {
                FileLog.e(localException);
                double d1 = d3;
                continue;
                int j = 0;
              }
            }
          }
        });
        return;
      }
    }
  }

  public static void loadHints(boolean paramBoolean)
  {
    if (loading);
    while (true)
    {
      return;
      if (!paramBoolean)
        break;
      if (loaded)
        continue;
      loading = true;
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          ArrayList localArrayList1 = new ArrayList();
          ArrayList localArrayList2 = new ArrayList();
          HashMap localHashMap = new HashMap();
          ArrayList localArrayList3 = new ArrayList();
          ArrayList localArrayList4 = new ArrayList();
          ArrayList localArrayList5;
          ArrayList localArrayList6;
          SQLiteCursor localSQLiteCursor;
          while (true)
          {
            int i;
            int j;
            TLRPC.TL_topPeer localTL_topPeer;
            try
            {
              localArrayList5 = new ArrayList();
              localArrayList6 = new ArrayList();
              localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, type, rating, date FROM chat_hints WHERE 1 ORDER BY rating DESC", new Object[0]);
              if (!localSQLiteCursor.next())
                break;
              i = localSQLiteCursor.intValue(0);
              j = localSQLiteCursor.intValue(1);
              localTL_topPeer = new TLRPC.TL_topPeer();
              localTL_topPeer.rating = localSQLiteCursor.doubleValue(2);
              if (i > 0)
              {
                localTL_topPeer.peer = new TLRPC.TL_peerUser();
                localTL_topPeer.peer.user_id = i;
                localArrayList5.add(Integer.valueOf(i));
                if (j != 0)
                  break label212;
                localArrayList1.add(localTL_topPeer);
                continue;
              }
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
              return;
            }
            localTL_topPeer.peer = new TLRPC.TL_peerChat();
            localTL_topPeer.peer.chat_id = (-i);
            localArrayList6.add(Integer.valueOf(-i));
            continue;
            label212: if (j != 1)
              continue;
            localArrayList2.add(localTL_topPeer);
            localHashMap.put(Integer.valueOf(i), Integer.valueOf(localSQLiteCursor.intValue(3)));
          }
          localSQLiteCursor.dispose();
          if (!localArrayList5.isEmpty())
            MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", localArrayList5), localArrayList3);
          if (!localArrayList6.isEmpty())
            MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", localArrayList6), localArrayList4);
          AndroidUtilities.runOnUIThread(new Runnable(localArrayList3, localArrayList4, localException, localArrayList2, localHashMap)
          {
            public void run()
            {
              MessagesController.getInstance().putUsers(this.val$users, true);
              MessagesController.getInstance().putChats(this.val$chats, true);
              SearchQuery.access$202(false);
              SearchQuery.access$302(true);
              SearchQuery.hints = this.val$hintsNew;
              SearchQuery.inlineBots = this.val$inlineBotsNew;
              SearchQuery.access$402(this.val$inlineDatesNew);
              SearchQuery.buildShortcuts();
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
              if (Math.abs(UserConfig.lastHintsSyncTime - (int)(System.currentTimeMillis() / 1000L)) >= 86400)
                SearchQuery.loadHints(false);
            }
          });
        }
      });
      loaded = true;
      return;
    }
    loading = true;
    TLRPC.TL_contacts_getTopPeers localTL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
    localTL_contacts_getTopPeers.hash = 0;
    localTL_contacts_getTopPeers.bots_pm = false;
    localTL_contacts_getTopPeers.correspondents = true;
    localTL_contacts_getTopPeers.groups = false;
    localTL_contacts_getTopPeers.channels = false;
    localTL_contacts_getTopPeers.bots_inline = true;
    localTL_contacts_getTopPeers.offset = 0;
    localTL_contacts_getTopPeers.limit = 20;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_getTopPeers, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if ((paramTLObject instanceof TLRPC.TL_contacts_topPeers))
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              TLRPC.TL_contacts_topPeers localTL_contacts_topPeers = (TLRPC.TL_contacts_topPeers)this.val$response;
              MessagesController.getInstance().putUsers(localTL_contacts_topPeers.users, false);
              MessagesController.getInstance().putChats(localTL_contacts_topPeers.chats, false);
              int i = 0;
              if (i < localTL_contacts_topPeers.categories.size())
              {
                localObject = (TLRPC.TL_topPeerCategoryPeers)localTL_contacts_topPeers.categories.get(i);
                if ((((TLRPC.TL_topPeerCategoryPeers)localObject).category instanceof TLRPC.TL_topPeerCategoryBotsInline))
                  SearchQuery.inlineBots = ((TLRPC.TL_topPeerCategoryPeers)localObject).peers;
                while (true)
                {
                  i += 1;
                  break;
                  SearchQuery.hints = ((TLRPC.TL_topPeerCategoryPeers)localObject).peers;
                }
              }
              SearchQuery.buildShortcuts();
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
              Object localObject = new HashMap(SearchQuery.inlineDates);
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localTL_contacts_topPeers, (HashMap)localObject)
              {
                public void run()
                {
                  while (true)
                  {
                    int j;
                    int k;
                    int m;
                    try
                    {
                      MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM chat_hints WHERE 1").stepThis().dispose();
                      MessagesStorage.getInstance().getDatabase().beginTransaction();
                      MessagesStorage.getInstance().putUsersAndChats(this.val$topPeers.users, this.val$topPeers.chats, false, false);
                      SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
                      j = 0;
                      if (j >= this.val$topPeers.categories.size())
                        continue;
                      TLRPC.TL_topPeerCategoryPeers localTL_topPeerCategoryPeers = (TLRPC.TL_topPeerCategoryPeers)this.val$topPeers.categories.get(j);
                      if ((localTL_topPeerCategoryPeers.category instanceof TLRPC.TL_topPeerCategoryBotsInline))
                      {
                        k = 1;
                        break label307;
                        if (m >= localTL_topPeerCategoryPeers.peers.size())
                          break label323;
                        TLRPC.TL_topPeer localTL_topPeer = (TLRPC.TL_topPeer)localTL_topPeerCategoryPeers.peers.get(m);
                        if (!(localTL_topPeer.peer instanceof TLRPC.TL_peerUser))
                          continue;
                        i = localTL_topPeer.peer.user_id;
                        Integer localInteger = (Integer)this.val$inlineDatesCopy.get(Integer.valueOf(i));
                        localSQLitePreparedStatement.requery();
                        localSQLitePreparedStatement.bindInteger(1, i);
                        localSQLitePreparedStatement.bindInteger(2, k);
                        localSQLitePreparedStatement.bindDouble(3, localTL_topPeer.rating);
                        if (localInteger == null)
                          break label318;
                        i = localInteger.intValue();
                        localSQLitePreparedStatement.bindInteger(4, i);
                        localSQLitePreparedStatement.step();
                        m += 1;
                        continue;
                        if (!(localTL_topPeer.peer instanceof TLRPC.TL_peerChat))
                          continue;
                        i = -localTL_topPeer.peer.chat_id;
                        continue;
                        i = -localTL_topPeer.peer.channel_id;
                        continue;
                        localSQLitePreparedStatement.dispose();
                        MessagesStorage.getInstance().getDatabase().commitTransaction();
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            UserConfig.lastHintsSyncTime = (int)(System.currentTimeMillis() / 1000L);
                            UserConfig.saveConfig(false);
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
                    while (true)
                    {
                      label307: m = 0;
                      break;
                      k = 0;
                    }
                    label318: int i = 0;
                    continue;
                    label323: j += 1;
                  }
                }
              });
            }
          });
      }
    });
  }

  public static void removeInline(int paramInt)
  {
    int i = 0;
    while (true)
    {
      if (i < inlineBots.size())
      {
        if (((TLRPC.TL_topPeer)inlineBots.get(i)).peer.user_id == paramInt)
        {
          inlineBots.remove(i);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryBotsInline();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
            }
          });
          deletePeer(paramInt, 1);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadInlineHints, new Object[0]);
        }
      }
      else
        return;
      i += 1;
    }
  }

  public static void removePeer(int paramInt)
  {
    int i = 0;
    while (true)
    {
      if (i < hints.size())
      {
        if (((TLRPC.TL_topPeer)hints.get(i)).peer.user_id == paramInt)
        {
          hints.remove(i);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.reloadHints, new Object[0]);
          TLRPC.TL_contacts_resetTopPeerRating localTL_contacts_resetTopPeerRating = new TLRPC.TL_contacts_resetTopPeerRating();
          localTL_contacts_resetTopPeerRating.category = new TLRPC.TL_topPeerCategoryCorrespondents();
          localTL_contacts_resetTopPeerRating.peer = MessagesController.getInputPeer(paramInt);
          deletePeer(paramInt, 0);
          ConnectionsManager.getInstance().sendRequest(localTL_contacts_resetTopPeerRating, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
            }
          });
        }
      }
      else
        return;
      i += 1;
    }
  }

  private static void savePeer(int paramInt1, int paramInt2, double paramDouble)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramInt1, paramInt2, paramDouble)
    {
      public void run()
      {
        try
        {
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO chat_hints VALUES(?, ?, ?, ?)");
          localSQLitePreparedStatement.requery();
          localSQLitePreparedStatement.bindInteger(1, this.val$did);
          localSQLitePreparedStatement.bindInteger(2, this.val$type);
          localSQLitePreparedStatement.bindDouble(3, this.val$rating);
          localSQLitePreparedStatement.bindInteger(4, (int)System.currentTimeMillis() / 1000);
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
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.query.SearchQuery
 * JD-Core Version:    0.6.0
 */