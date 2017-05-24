package org.vidogram.ui.Adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.TL_contacts_found;
import org.vidogram.tgnet.TLRPC.TL_contacts_search;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.User;

public class SearchAdapterHelper
{
  private SearchAdapterHelperDelegate delegate;
  private ArrayList<TLObject> globalSearch = new ArrayList();
  private ArrayList<HashtagObject> hashtags;
  private HashMap<String, HashtagObject> hashtagsByText;
  private boolean hashtagsLoadedFromDb = false;
  private String lastFoundUsername = null;
  private int lastReqId;
  private int reqId = 0;

  private void putRecentHashtags(ArrayList<HashtagObject> paramArrayList)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramArrayList)
    {
      public void run()
      {
        int j = 100;
        try
        {
          MessagesStorage.getInstance().getDatabase().beginTransaction();
          SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO hashtag_recent_v2 VALUES(?, ?)");
          int i = 0;
          while (true)
            if ((i >= this.val$arrayList.size()) || (i == 100))
            {
              localSQLitePreparedStatement.dispose();
              MessagesStorage.getInstance().getDatabase().commitTransaction();
              if (this.val$arrayList.size() < 100)
                break;
              MessagesStorage.getInstance().getDatabase().beginTransaction();
              i = j;
            }
            else
            {
              while (true)
                if (i < this.val$arrayList.size())
                {
                  MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE id = '" + ((SearchAdapterHelper.HashtagObject)this.val$arrayList.get(i)).hashtag + "'").stepThis().dispose();
                  i += 1;
                  continue;
                  SearchAdapterHelper.HashtagObject localHashtagObject = (SearchAdapterHelper.HashtagObject)this.val$arrayList.get(i);
                  localSQLitePreparedStatement.requery();
                  localSQLitePreparedStatement.bindString(1, localHashtagObject.hashtag);
                  localSQLitePreparedStatement.bindInteger(2, localHashtagObject.date);
                  localSQLitePreparedStatement.step();
                  i += 1;
                  break;
                }
              MessagesStorage.getInstance().getDatabase().commitTransaction();
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

  public void addHashtagsFromMessage(CharSequence paramCharSequence)
  {
    if (paramCharSequence == null);
    int i;
    do
    {
      return;
      Matcher localMatcher = Pattern.compile("(^|\\s)#[\\w@\\.]+").matcher(paramCharSequence);
      i = 0;
      if (!localMatcher.find())
        continue;
      int j = localMatcher.start();
      int k = localMatcher.end();
      i = j;
      if (paramCharSequence.charAt(j) != '@')
      {
        i = j;
        if (paramCharSequence.charAt(j) != '#')
          i = j + 1;
      }
      String str = paramCharSequence.subSequence(i, k).toString();
      if (this.hashtagsByText == null)
      {
        this.hashtagsByText = new HashMap();
        this.hashtags = new ArrayList();
      }
      HashtagObject localHashtagObject = (HashtagObject)this.hashtagsByText.get(str);
      if (localHashtagObject == null)
      {
        localHashtagObject = new HashtagObject();
        localHashtagObject.hashtag = str;
        this.hashtagsByText.put(localHashtagObject.hashtag, localHashtagObject);
      }
      while (true)
      {
        localHashtagObject.date = (int)(System.currentTimeMillis() / 1000L);
        this.hashtags.add(0, localHashtagObject);
        i = 1;
        break;
        this.hashtags.remove(localHashtagObject);
      }
    }
    while (i == 0);
    putRecentHashtags(this.hashtags);
  }

  public void clearRecentHashtags()
  {
    this.hashtags = new ArrayList();
    this.hashtagsByText = new HashMap();
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM hashtag_recent_v2 WHERE 1").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public ArrayList<TLObject> getGlobalSearch()
  {
    return this.globalSearch;
  }

  public ArrayList<HashtagObject> getHashtags()
  {
    return this.hashtags;
  }

  public String getLastFoundUsername()
  {
    return this.lastFoundUsername;
  }

  public boolean loadRecentHashtags()
  {
    if (this.hashtagsLoadedFromDb)
      return true;
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList;
        HashMap localHashMap;
        try
        {
          SQLiteCursor localSQLiteCursor = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT id, date FROM hashtag_recent_v2 WHERE 1", new Object[0]);
          localArrayList = new ArrayList();
          localHashMap = new HashMap();
          while (localSQLiteCursor.next())
          {
            SearchAdapterHelper.HashtagObject localHashtagObject = new SearchAdapterHelper.HashtagObject();
            localHashtagObject.hashtag = localSQLiteCursor.stringValue(0);
            localHashtagObject.date = localSQLiteCursor.intValue(1);
            localArrayList.add(localHashtagObject);
            localHashMap.put(localHashtagObject.hashtag, localHashtagObject);
          }
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
          return;
        }
        localException.dispose();
        Collections.sort(localArrayList, new Comparator()
        {
          public int compare(SearchAdapterHelper.HashtagObject paramHashtagObject1, SearchAdapterHelper.HashtagObject paramHashtagObject2)
          {
            if (paramHashtagObject1.date < paramHashtagObject2.date)
              return 1;
            if (paramHashtagObject1.date > paramHashtagObject2.date)
              return -1;
            return 0;
          }
        });
        AndroidUtilities.runOnUIThread(new Runnable(localArrayList, localHashMap)
        {
          public void run()
          {
            SearchAdapterHelper.this.setHashtags(this.val$arrayList, this.val$hashMap);
          }
        });
      }
    });
    return false;
  }

  public void queryServerSearch(String paramString, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    if (this.reqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
      this.reqId = 0;
    }
    if ((paramString == null) || (paramString.length() < 5))
    {
      this.globalSearch.clear();
      this.lastReqId = 0;
      this.delegate.onDataSetChanged();
      return;
    }
    TLRPC.TL_contacts_search localTL_contacts_search = new TLRPC.TL_contacts_search();
    localTL_contacts_search.q = paramString;
    localTL_contacts_search.limit = 50;
    int i = this.lastReqId + 1;
    this.lastReqId = i;
    this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_contacts_search, new RequestDelegate(i, paramBoolean1, paramBoolean2, paramBoolean3, paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if ((SearchAdapterHelper.1.this.val$currentReqId == SearchAdapterHelper.this.lastReqId) && (this.val$error == null))
            {
              TLRPC.TL_contacts_found localTL_contacts_found = (TLRPC.TL_contacts_found)this.val$response;
              SearchAdapterHelper.this.globalSearch.clear();
              if (SearchAdapterHelper.1.this.val$allowChats)
              {
                i = 0;
                while (i < localTL_contacts_found.chats.size())
                {
                  SearchAdapterHelper.this.globalSearch.add(localTL_contacts_found.chats.get(i));
                  i += 1;
                }
              }
              int i = 0;
              if (i < localTL_contacts_found.users.size())
              {
                TLRPC.User localUser = (TLRPC.User)localTL_contacts_found.users.get(i);
                if (((!SearchAdapterHelper.1.this.val$allowBots) && (localUser.bot)) || ((!SearchAdapterHelper.1.this.val$allowSelf) && (localUser.self)));
                while (true)
                {
                  i += 1;
                  break;
                  SearchAdapterHelper.this.globalSearch.add(localTL_contacts_found.users.get(i));
                }
              }
              SearchAdapterHelper.access$202(SearchAdapterHelper.this, SearchAdapterHelper.1.this.val$query);
              SearchAdapterHelper.this.delegate.onDataSetChanged();
            }
            SearchAdapterHelper.access$402(SearchAdapterHelper.this, 0);
          }
        });
      }
    }
    , 2);
  }

  public void setDelegate(SearchAdapterHelperDelegate paramSearchAdapterHelperDelegate)
  {
    this.delegate = paramSearchAdapterHelperDelegate;
  }

  public void setHashtags(ArrayList<HashtagObject> paramArrayList, HashMap<String, HashtagObject> paramHashMap)
  {
    this.hashtags = paramArrayList;
    this.hashtagsByText = paramHashMap;
    this.hashtagsLoadedFromDb = true;
    this.delegate.onSetHashtags(paramArrayList, paramHashMap);
  }

  public void unloadRecentHashtags()
  {
    this.hashtagsLoadedFromDb = false;
  }

  public static class HashtagObject
  {
    int date;
    String hashtag;
  }

  public static abstract interface SearchAdapterHelperDelegate
  {
    public abstract void onDataSetChanged();

    public abstract void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramHashMap);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.SearchAdapterHelper
 * JD-Core Version:    0.6.0
 */