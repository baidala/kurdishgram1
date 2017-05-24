package org.vidogram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.vidogram.tgnet.TLRPC.TL_messages_searchGlobal;
import org.vidogram.tgnet.TLRPC.TL_topPeer;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.messages_Messages;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.DialogCell;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.HashtagSearchCell;
import org.vidogram.ui.Cells.HintDialogCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DialogsSearchAdapter extends RecyclerListView.SelectionAdapter
{
  private DialogsSearchAdapterDelegate delegate;
  private int dialogsType;
  private RecyclerListView innerListView;
  private String lastMessagesSearchString;
  private int lastReqId;
  private int lastSearchId = 0;
  private String lastSearchText;
  private Context mContext;
  private boolean messagesSearchEndReached;
  private int needMessagesSearch;
  private ArrayList<RecentSearchObject> recentSearchObjects = new ArrayList();
  private HashMap<Long, RecentSearchObject> recentSearchObjectsById = new HashMap();
  private int reqId = 0;
  private SearchAdapterHelper searchAdapterHelper = new SearchAdapterHelper();
  private ArrayList<TLObject> searchResult = new ArrayList();
  private ArrayList<String> searchResultHashtags = new ArrayList();
  private ArrayList<MessageObject> searchResultMessages = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;

  public DialogsSearchAdapter(Context paramContext, int paramInt1, int paramInt2)
  {
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }

      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramHashMap)
      {
        int i = 0;
        while (i < paramArrayList.size())
        {
          DialogsSearchAdapter.this.searchResultHashtags.add(((SearchAdapterHelper.HashtagObject)paramArrayList.get(i)).hashtag);
          i += 1;
        }
        if (DialogsSearchAdapter.this.delegate != null)
          DialogsSearchAdapter.this.delegate.searchStateChanged(false);
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }
    });
    this.mContext = paramContext;
    this.needMessagesSearch = paramInt1;
    this.dialogsType = paramInt2;
    loadRecentSearch();
    SearchQuery.loadHints(true);
  }

  private void searchDialogsInternal(String paramString, int paramInt)
  {
    if (this.needMessagesSearch == 2)
      return;
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramString, paramInt)
    {
      public void run()
      {
        Object localObject3;
        int i;
        String[] arrayOfString;
        Object localObject5;
        Object localObject4;
        ArrayList localArrayList;
        int j;
        HashMap localHashMap;
        long l;
        Object localObject6;
        int k;
        while (true)
        {
          try
          {
            localObject3 = this.val$query.trim().toLowerCase();
            if (((String)localObject3).length() != 0)
              continue;
            DialogsSearchAdapter.access$902(DialogsSearchAdapter.this, -1);
            DialogsSearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList(), new ArrayList(), DialogsSearchAdapter.this.lastSearchId);
            return;
            Object localObject1 = LocaleController.getInstance().getTranslitString((String)localObject3);
            if (((String)localObject3).equals(localObject1))
              break label2464;
            if (((String)localObject1).length() != 0)
              break label2461;
            break label2464;
            arrayOfString = new String[i + 1];
            arrayOfString[0] = localObject3;
            if (localObject1 == null)
              continue;
            arrayOfString[1] = localObject1;
            localObject1 = new ArrayList();
            localObject5 = new ArrayList();
            localObject4 = new ArrayList();
            localArrayList = new ArrayList();
            j = 0;
            localHashMap = new HashMap();
            localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 600", new Object[0]);
            if (!((SQLiteCursor)localObject3).next())
              break;
            l = ((SQLiteCursor)localObject3).longValue(0);
            localObject6 = new DialogsSearchAdapter.DialogSearchResult(DialogsSearchAdapter.this, null);
            ((DialogsSearchAdapter.DialogSearchResult)localObject6).date = ((SQLiteCursor)localObject3).intValue(1);
            localHashMap.put(Long.valueOf(l), localObject6);
            i = (int)l;
            k = (int)(l >> 32);
            if (i == 0)
              break label374;
            if (k != 1)
              break label307;
            if ((DialogsSearchAdapter.this.dialogsType != 0) || (((ArrayList)localObject5).contains(Integer.valueOf(i))))
              continue;
            ((ArrayList)localObject5).add(Integer.valueOf(i));
            continue;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          label302: i = 0;
          continue;
          label307: if (i > 0)
          {
            if ((DialogsSearchAdapter.this.dialogsType == 2) || (localException.contains(Integer.valueOf(i))))
              continue;
            localException.add(Integer.valueOf(i));
            continue;
          }
          if (((ArrayList)localObject5).contains(Integer.valueOf(-i)))
            continue;
          ((ArrayList)localObject5).add(Integer.valueOf(-i));
          continue;
          label374: if ((DialogsSearchAdapter.this.dialogsType != 0) || (((ArrayList)localObject4).contains(Integer.valueOf(k))))
            continue;
          ((ArrayList)localObject4).add(Integer.valueOf(k));
        }
        ((SQLiteCursor)localObject3).dispose();
        label461: String str1;
        Object localObject2;
        label500: int m;
        label523: label532: String str2;
        if (!localException.isEmpty())
        {
          localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
          i = 0;
          if (((SQLiteCursor)localObject6).next())
          {
            str1 = ((SQLiteCursor)localObject6).stringValue(2);
            localObject2 = LocaleController.getInstance().getTranslitString(str1);
            if (!str1.equals(localObject2))
              break label2458;
            localObject2 = null;
            j = str1.lastIndexOf(";;;");
            if (j == -1)
              break label2452;
            localObject3 = str1.substring(j + 3);
            m = arrayOfString.length;
            k = 0;
            j = 0;
            if (k < m)
            {
              str2 = arrayOfString[k];
              if ((str1.startsWith(str2)) || (str1.contains(" " + str2)))
                break label2477;
              if (localObject2 != null)
              {
                if (((String)localObject2).startsWith(str2))
                  break label2477;
                if (((String)localObject2).contains(" " + str2))
                {
                  break label2477;
                  label628: if (j == 0)
                    break label2485;
                  localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(0);
                  if (localObject3 == null)
                    break label2446;
                  localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  localObject3 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(((TLRPC.User)localObject2).id));
                  if (((TLRPC.User)localObject2).status != null)
                    ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject6).intValue(1);
                  if (j != 1)
                    break label764;
                }
              }
              label764: for (((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2); ; ((DialogsSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
              {
                ((DialogsSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                i += 1;
                break label2482;
                if ((localObject3 == null) || (!((String)localObject3).startsWith(str2)))
                  break label2449;
                j = 2;
                break;
              }
            }
          }
          else
          {
            ((SQLiteCursor)localObject6).dispose();
            j = i;
          }
        }
        else
        {
          i = j;
          if (!((ArrayList)localObject5).isEmpty())
          {
            localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject5) }), new Object[0]);
            i = j;
            label878: 
            while (((SQLiteCursor)localObject3).next())
            {
              localObject5 = ((SQLiteCursor)localObject3).stringValue(1);
              localObject2 = LocaleController.getInstance().getTranslitString((String)localObject5);
              if (!((String)localObject5).equals(localObject2))
                break label2443;
              localObject2 = null;
              k = arrayOfString.length;
              j = 0;
              if (j >= k)
                break label2500;
              localObject6 = arrayOfString[j];
              if ((!((String)localObject5).startsWith((String)localObject6)) && (!((String)localObject5).contains(" " + (String)localObject6)) && ((localObject2 == null) || ((!((String)localObject2).startsWith((String)localObject6)) && (!((String)localObject2).contains(" " + (String)localObject6)))))
                break label2495;
              localObject5 = ((SQLiteCursor)localObject3).byteBufferValue(0);
              if (localObject5 == null)
                continue;
              localObject2 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject5, ((NativeByteBuffer)localObject5).readInt32(false), false);
              ((NativeByteBuffer)localObject5).reuse();
              if ((localObject2 == null) || (((TLRPC.Chat)localObject2).deactivated) || ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (ChatObject.isNotInChat((TLRPC.Chat)localObject2))))
                break label2440;
              if (((TLRPC.Chat)localObject2).id > 0)
                l = -((TLRPC.Chat)localObject2).id;
              while (true)
              {
                localObject5 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(l));
                ((DialogsSearchAdapter.DialogSearchResult)localObject5).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject2).title, null, (String)localObject6);
                ((DialogsSearchAdapter.DialogSearchResult)localObject5).object = ((TLObject)localObject2);
                i += 1;
                break;
                l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject2).id);
              }
            }
            label917: label923: ((SQLiteCursor)localObject3).dispose();
          }
          j = i;
          if (!((ArrayList)localObject4).isEmpty())
          {
            localObject4 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT q.data, u.name, q.user, q.g, q.authkey, q.ttl, u.data, u.status, q.layer, q.seq_in, q.seq_out, q.use_count, q.exchange_id, q.key_date, q.fprint, q.fauthkey, q.khash, q.in_seq_no FROM enc_chats as q INNER JOIN users as u ON q.user = u.uid WHERE q.uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject4) }), new Object[0]);
            label1204: if (((SQLiteCursor)localObject4).next())
            {
              localObject6 = ((SQLiteCursor)localObject4).stringValue(1);
              localObject2 = LocaleController.getInstance().getTranslitString((String)localObject6);
              if (!((String)localObject6).equals(localObject2))
                break label2437;
              localObject2 = null;
              label1243: localObject3 = null;
              j = ((String)localObject6).lastIndexOf(";;;");
              if (j == -1)
                break label2502;
              localObject3 = ((String)localObject6).substring(j + 2);
              break label2502;
              label1272: if (k < arrayOfString.length)
              {
                localObject5 = arrayOfString[k];
                if ((((String)localObject6).startsWith((String)localObject5)) || (((String)localObject6).contains(" " + (String)localObject5)))
                  break label2509;
                if (localObject2 != null)
                {
                  if (((String)localObject2).startsWith((String)localObject5))
                    break label2509;
                  if (((String)localObject2).contains(" " + (String)localObject5))
                  {
                    break label2509;
                    if (j == 0)
                      break label2517;
                    localObject3 = ((SQLiteCursor)localObject4).byteBufferValue(0);
                    if (localObject3 == null)
                      break label2428;
                    localObject2 = TLRPC.EncryptedChat.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                    ((NativeByteBuffer)localObject3).reuse();
                    label1405: localObject6 = ((SQLiteCursor)localObject4).byteBufferValue(6);
                    if (localObject6 == null)
                      break label2422;
                    localObject3 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject6, ((NativeByteBuffer)localObject6).readInt32(false), false);
                    ((NativeByteBuffer)localObject6).reuse();
                    if ((localObject2 == null) || (localObject3 == null))
                      break label2419;
                    localObject6 = (DialogsSearchAdapter.DialogSearchResult)localHashMap.get(Long.valueOf(((TLRPC.EncryptedChat)localObject2).id << 32));
                    ((TLRPC.EncryptedChat)localObject2).user_id = ((SQLiteCursor)localObject4).intValue(2);
                    ((TLRPC.EncryptedChat)localObject2).a_or_b = ((SQLiteCursor)localObject4).byteArrayValue(3);
                    ((TLRPC.EncryptedChat)localObject2).auth_key = ((SQLiteCursor)localObject4).byteArrayValue(4);
                    ((TLRPC.EncryptedChat)localObject2).ttl = ((SQLiteCursor)localObject4).intValue(5);
                    ((TLRPC.EncryptedChat)localObject2).layer = ((SQLiteCursor)localObject4).intValue(8);
                    ((TLRPC.EncryptedChat)localObject2).seq_in = ((SQLiteCursor)localObject4).intValue(9);
                    ((TLRPC.EncryptedChat)localObject2).seq_out = ((SQLiteCursor)localObject4).intValue(10);
                    k = ((SQLiteCursor)localObject4).intValue(11);
                    ((TLRPC.EncryptedChat)localObject2).key_use_count_in = (short)(k >> 16);
                    ((TLRPC.EncryptedChat)localObject2).key_use_count_out = (short)k;
                    ((TLRPC.EncryptedChat)localObject2).exchange_id = ((SQLiteCursor)localObject4).longValue(12);
                    ((TLRPC.EncryptedChat)localObject2).key_create_date = ((SQLiteCursor)localObject4).intValue(13);
                    ((TLRPC.EncryptedChat)localObject2).future_key_fingerprint = ((SQLiteCursor)localObject4).longValue(14);
                    ((TLRPC.EncryptedChat)localObject2).future_auth_key = ((SQLiteCursor)localObject4).byteArrayValue(15);
                    ((TLRPC.EncryptedChat)localObject2).key_hash = ((SQLiteCursor)localObject4).byteArrayValue(16);
                    ((TLRPC.EncryptedChat)localObject2).in_seq_no = ((SQLiteCursor)localObject4).intValue(17);
                    if (((TLRPC.User)localObject3).status != null)
                      ((TLRPC.User)localObject3).status.expires = ((SQLiteCursor)localObject4).intValue(7);
                    if (j != 1)
                      break label1779;
                    ((DialogsSearchAdapter.DialogSearchResult)localObject6).name = new SpannableStringBuilder(ContactsController.formatName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name));
                    ((SpannableStringBuilder)((DialogsSearchAdapter.DialogSearchResult)localObject6).name).setSpan(new ForegroundColorSpan(Theme.getColor("chats_secretName")), 0, ((DialogsSearchAdapter.DialogSearchResult)localObject6).name.length(), 33);
                  }
                }
                while (true)
                {
                  ((DialogsSearchAdapter.DialogSearchResult)localObject6).object = ((TLObject)localObject2);
                  localArrayList.add(localObject3);
                  i += 1;
                  break label2514;
                  if ((localObject3 == null) || (!((String)localObject3).startsWith((String)localObject5)))
                    break label2434;
                  j = 2;
                  break;
                  ((DialogsSearchAdapter.DialogSearchResult)localObject6).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject3).username, null, "@" + (String)localObject5);
                }
              }
            }
            else
            {
              label1369: label1779: ((SQLiteCursor)localObject4).dispose();
              j = i;
            }
          }
          else
          {
            label1438: localObject2 = new ArrayList(j);
            localObject3 = localHashMap.values().iterator();
            while (((Iterator)localObject3).hasNext())
            {
              localObject4 = (DialogsSearchAdapter.DialogSearchResult)((Iterator)localObject3).next();
              if ((((DialogsSearchAdapter.DialogSearchResult)localObject4).object == null) || (((DialogsSearchAdapter.DialogSearchResult)localObject4).name == null))
                continue;
              ((ArrayList)localObject2).add(localObject4);
            }
            Collections.sort((List)localObject2, new Comparator()
            {
              public int compare(DialogsSearchAdapter.DialogSearchResult paramDialogSearchResult1, DialogsSearchAdapter.DialogSearchResult paramDialogSearchResult2)
              {
                if (paramDialogSearchResult1.date < paramDialogSearchResult2.date)
                  return 1;
                if (paramDialogSearchResult1.date > paramDialogSearchResult2.date)
                  return -1;
                return 0;
              }
            });
            localObject4 = new ArrayList();
            localObject5 = new ArrayList();
            i = 0;
            while (i < ((ArrayList)localObject2).size())
            {
              localObject3 = (DialogsSearchAdapter.DialogSearchResult)((ArrayList)localObject2).get(i);
              ((ArrayList)localObject4).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).object);
              ((ArrayList)localObject5).add(((DialogsSearchAdapter.DialogSearchResult)localObject3).name);
              i += 1;
            }
            if (DialogsSearchAdapter.this.dialogsType != 2)
              localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
          }
        }
        label2082: label2105: label2492: label2495: label2500: label2502: label2509: label2514: label2517: label2524: label2529: label2536: 
        while (true)
        {
          label2025: if (((SQLiteCursor)localObject6).next())
          {
            if (localHashMap.containsKey(Long.valueOf(((SQLiteCursor)localObject6).intValue(3))))
              continue;
            str1 = ((SQLiteCursor)localObject6).stringValue(2);
            localObject2 = LocaleController.getInstance().getTranslitString(str1);
            if (!str1.equals(localObject2))
              break label2416;
            localObject2 = null;
            i = str1.lastIndexOf(";;;");
            if (i == -1)
              break label2410;
            localObject3 = str1.substring(i + 3);
            m = arrayOfString.length;
            k = 0;
            j = 0;
          }
          while (true)
          {
            if (j >= m)
              break label2536;
            str2 = arrayOfString[j];
            if ((!str1.startsWith(str2)) && (!str1.contains(" " + str2)))
              if (localObject2 != null)
              {
                if (((String)localObject2).startsWith(str2))
                  break label2524;
                if (((String)localObject2).contains(" " + str2))
                  break label2524;
              }
            while (true)
            {
              if (i == 0)
                break label2529;
              localObject2 = ((SQLiteCursor)localObject6).byteBufferValue(0);
              if (localObject2 == null)
                break label2025;
              localObject3 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject2, ((NativeByteBuffer)localObject2).readInt32(false), false);
              ((NativeByteBuffer)localObject2).reuse();
              if (((TLRPC.User)localObject3).status != null)
                ((TLRPC.User)localObject3).status.expires = ((SQLiteCursor)localObject6).intValue(1);
              if (i == 1)
                ((ArrayList)localObject5).add(AndroidUtilities.generateSearchName(((TLRPC.User)localObject3).first_name, ((TLRPC.User)localObject3).last_name, str2));
              while (true)
              {
                ((ArrayList)localObject4).add(localObject3);
                break;
                i = k;
                if (localObject3 == null)
                  break label2210;
                i = k;
                if (!((String)localObject3).startsWith(str2))
                  break label2210;
                i = 2;
                break label2210;
                ((ArrayList)localObject5).add(AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject3).username, null, "@" + str2));
              }
              ((SQLiteCursor)localObject6).dispose();
              DialogsSearchAdapter.this.updateSearchResults((ArrayList)localObject4, (ArrayList)localObject5, localArrayList, this.val$searchId);
              return;
              label2410: localObject3 = null;
              break label2105;
              label2416: break label2082;
              label2419: break label2514;
              label2422: localObject3 = null;
              break label1438;
              label2428: localObject2 = null;
              break label1405;
              break label1369;
              break label1243;
              break label2492;
              break label917;
              break label2482;
              break label628;
              localObject3 = null;
              break label523;
              break label500;
              break label2467;
              localObject2 = null;
              if (localObject2 == null)
                break label302;
              i = 1;
              break;
              j = 1;
              break label628;
              break label461;
              k += 1;
              break label532;
              break label878;
              j += 1;
              break label923;
              break label878;
              k = 0;
              j = 0;
              break label1272;
              j = 1;
              break label1369;
              break label1204;
              k += 1;
              break label1272;
              i = 1;
            }
            j += 1;
            k = i;
          }
        }
      }
    });
  }

  private void searchMessagesInternal(String paramString)
  {
    if ((this.needMessagesSearch == 0) || (((this.lastMessagesSearchString == null) || (this.lastMessagesSearchString.length() == 0)) && ((paramString == null) || (paramString.length() == 0))));
    while (true)
    {
      return;
      if (this.reqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
        this.reqId = 0;
      }
      if ((paramString != null) && (paramString.length() != 0))
        break;
      this.searchResultMessages.clear();
      this.lastReqId = 0;
      this.lastMessagesSearchString = null;
      notifyDataSetChanged();
      if (this.delegate == null)
        continue;
      this.delegate.searchStateChanged(false);
      return;
    }
    TLRPC.TL_messages_searchGlobal localTL_messages_searchGlobal = new TLRPC.TL_messages_searchGlobal();
    localTL_messages_searchGlobal.limit = 20;
    localTL_messages_searchGlobal.q = paramString;
    MessageObject localMessageObject;
    int i;
    if ((this.lastMessagesSearchString != null) && (paramString.equals(this.lastMessagesSearchString)) && (!this.searchResultMessages.isEmpty()))
    {
      localMessageObject = (MessageObject)this.searchResultMessages.get(this.searchResultMessages.size() - 1);
      localTL_messages_searchGlobal.offset_id = localMessageObject.getId();
      localTL_messages_searchGlobal.offset_date = localMessageObject.messageOwner.date;
      if (localMessageObject.messageOwner.to_id.channel_id != 0)
        i = -localMessageObject.messageOwner.to_id.channel_id;
    }
    for (localTL_messages_searchGlobal.offset_peer = MessagesController.getInputPeer(i); ; localTL_messages_searchGlobal.offset_peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.lastMessagesSearchString = paramString;
      i = this.lastReqId + 1;
      this.lastReqId = i;
      if (this.delegate != null)
        this.delegate.searchStateChanged(true);
      this.reqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_searchGlobal, new RequestDelegate(i, localTL_messages_searchGlobal)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
          {
            public void run()
            {
              boolean bool2 = true;
              Object localObject;
              if ((DialogsSearchAdapter.2.this.val$currentReqId == DialogsSearchAdapter.this.lastReqId) && (this.val$error == null))
              {
                TLRPC.messages_Messages localmessages_Messages = (TLRPC.messages_Messages)this.val$response;
                MessagesStorage.getInstance().putUsersAndChats(localmessages_Messages.users, localmessages_Messages.chats, true, true);
                MessagesController.getInstance().putUsers(localmessages_Messages.users, false);
                MessagesController.getInstance().putChats(localmessages_Messages.chats, false);
                if (DialogsSearchAdapter.2.this.val$req.offset_id == 0)
                  DialogsSearchAdapter.this.searchResultMessages.clear();
                int i = 0;
                if (i < localmessages_Messages.messages.size())
                {
                  TLRPC.Message localMessage = (TLRPC.Message)localmessages_Messages.messages.get(i);
                  DialogsSearchAdapter.this.searchResultMessages.add(new MessageObject(localMessage, null, false));
                  long l = MessageObject.getDialogId(localMessage);
                  if (localMessage.out)
                  {
                    localObject = MessagesController.getInstance().dialogs_read_outbox_max;
                    label182: Integer localInteger2 = (Integer)((ConcurrentHashMap)localObject).get(Long.valueOf(l));
                    Integer localInteger1 = localInteger2;
                    if (localInteger2 == null)
                    {
                      localInteger1 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(localMessage.out, l));
                      ((ConcurrentHashMap)localObject).put(Long.valueOf(l), localInteger1);
                    }
                    if (localInteger1.intValue() >= localMessage.id)
                      break label276;
                  }
                  label276: for (bool1 = true; ; bool1 = false)
                  {
                    localMessage.unread = bool1;
                    i += 1;
                    break;
                    localObject = MessagesController.getInstance().dialogs_read_inbox_max;
                    break label182;
                  }
                }
                localObject = DialogsSearchAdapter.this;
                if (localmessages_Messages.messages.size() == 20)
                  break label364;
              }
              label364: for (boolean bool1 = bool2; ; bool1 = false)
              {
                DialogsSearchAdapter.access$502((DialogsSearchAdapter)localObject, bool1);
                DialogsSearchAdapter.this.notifyDataSetChanged();
                if (DialogsSearchAdapter.this.delegate != null)
                  DialogsSearchAdapter.this.delegate.searchStateChanged(false);
                DialogsSearchAdapter.access$602(DialogsSearchAdapter.this, 0);
                return;
              }
            }
          });
        }
      }
      , 2);
      return;
      if (localMessageObject.messageOwner.to_id.chat_id != 0)
      {
        i = -localMessageObject.messageOwner.to_id.chat_id;
        break;
      }
      i = localMessageObject.messageOwner.to_id.user_id;
      break;
      localTL_messages_searchGlobal.offset_date = 0;
      localTL_messages_searchGlobal.offset_id = 0;
    }
  }

  private void setRecentSearch(ArrayList<RecentSearchObject> paramArrayList, HashMap<Long, RecentSearchObject> paramHashMap)
  {
    this.recentSearchObjects = paramArrayList;
    this.recentSearchObjectsById = paramHashMap;
    int i = 0;
    if (i < this.recentSearchObjects.size())
    {
      paramArrayList = (RecentSearchObject)this.recentSearchObjects.get(i);
      if ((paramArrayList.object instanceof TLRPC.User))
        MessagesController.getInstance().putUser((TLRPC.User)paramArrayList.object, true);
      while (true)
      {
        i += 1;
        break;
        if ((paramArrayList.object instanceof TLRPC.Chat))
        {
          MessagesController.getInstance().putChat((TLRPC.Chat)paramArrayList.object, true);
          continue;
        }
        if (!(paramArrayList.object instanceof TLRPC.EncryptedChat))
          continue;
        MessagesController.getInstance().putEncryptedChat((TLRPC.EncryptedChat)paramArrayList.object, true);
      }
    }
    notifyDataSetChanged();
  }

  private void updateSearchResults(ArrayList<TLObject> paramArrayList, ArrayList<CharSequence> paramArrayList1, ArrayList<TLRPC.User> paramArrayList2, int paramInt)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInt, paramArrayList, paramArrayList2, paramArrayList1)
    {
      public void run()
      {
        if (this.val$searchId != DialogsSearchAdapter.this.lastSearchId)
          return;
        int i = 0;
        if (i < this.val$result.size())
        {
          Object localObject = (TLObject)this.val$result.get(i);
          if ((localObject instanceof TLRPC.User))
          {
            localObject = (TLRPC.User)localObject;
            MessagesController.getInstance().putUser((TLRPC.User)localObject, true);
          }
          while (true)
          {
            i += 1;
            break;
            if ((localObject instanceof TLRPC.Chat))
            {
              localObject = (TLRPC.Chat)localObject;
              MessagesController.getInstance().putChat((TLRPC.Chat)localObject, true);
              continue;
            }
            if (!(localObject instanceof TLRPC.EncryptedChat))
              continue;
            localObject = (TLRPC.EncryptedChat)localObject;
            MessagesController.getInstance().putEncryptedChat((TLRPC.EncryptedChat)localObject, true);
          }
        }
        MessagesController.getInstance().putUsers(this.val$encUsers, true);
        DialogsSearchAdapter.access$1202(DialogsSearchAdapter.this, this.val$result);
        DialogsSearchAdapter.access$1302(DialogsSearchAdapter.this, this.val$names);
        DialogsSearchAdapter.this.notifyDataSetChanged();
      }
    });
  }

  public void addHashtagsFromMessage(CharSequence paramCharSequence)
  {
    this.searchAdapterHelper.addHashtagsFromMessage(paramCharSequence);
  }

  public void clearRecentHashtags()
  {
    this.searchAdapterHelper.clearRecentHashtags();
    this.searchResultHashtags.clear();
    notifyDataSetChanged();
  }

  public void clearRecentSearch()
  {
    this.recentSearchObjectsById = new HashMap();
    this.recentSearchObjects = new ArrayList();
    notifyDataSetChanged();
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        try
        {
          MessagesStorage.getInstance().getDatabase().executeFast("DELETE FROM search_recent WHERE 1").stepThis().dispose();
          return;
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
      }
    });
  }

  public RecyclerListView getInnerListView()
  {
    return this.innerListView;
  }

  public Object getItem(int paramInt)
  {
    int j = 0;
    int i = 0;
    if (isRecentSearchDisplayed())
    {
      if (!SearchQuery.hints.isEmpty())
        i = 2;
      if ((paramInt > i) && (paramInt - 1 - i < this.recentSearchObjects.size()))
      {
        TLObject localTLObject = ((RecentSearchObject)this.recentSearchObjects.get(paramInt - 1 - i)).object;
        Object localObject2;
        if ((localTLObject instanceof TLRPC.User))
        {
          localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.User)localTLObject).id));
          localObject1 = localTLObject;
          if (localObject2 != null)
            localObject1 = localObject2;
        }
        do
        {
          do
          {
            return localObject1;
            localObject1 = localTLObject;
          }
          while (!(localTLObject instanceof TLRPC.Chat));
          localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(((TLRPC.Chat)localTLObject).id));
          localObject1 = localTLObject;
        }
        while (localObject2 == null);
        return localObject2;
      }
      return null;
    }
    if (!this.searchResultHashtags.isEmpty())
    {
      if (paramInt > 0)
        return this.searchResultHashtags.get(paramInt - 1);
      return null;
    }
    Object localObject1 = this.searchAdapterHelper.getGlobalSearch();
    int k = this.searchResult.size();
    if (((ArrayList)localObject1).isEmpty())
    {
      i = 0;
      if (!this.searchResultMessages.isEmpty())
        break label245;
    }
    while (true)
    {
      if ((paramInt < 0) || (paramInt >= k))
        break label258;
      return this.searchResult.get(paramInt);
      i = ((ArrayList)localObject1).size() + 1;
      break;
      label245: j = this.searchResultMessages.size() + 1;
    }
    label258: if ((paramInt > k) && (paramInt < i + k))
      return ((ArrayList)localObject1).get(paramInt - k - 1);
    if ((paramInt > i + k) && (paramInt < j + (i + k)))
      return this.searchResultMessages.get(paramInt - k - i - 1);
    return null;
  }

  public int getItemCount()
  {
    int k = 0;
    int j = 0;
    int i;
    if (isRecentSearchDisplayed())
      if (!this.recentSearchObjects.isEmpty())
      {
        i = this.recentSearchObjects.size() + 1;
        if (!SearchQuery.hints.isEmpty())
          j = 2;
        j = i + j;
      }
    int m;
    do
    {
      return j;
      i = 0;
      break;
      if (!this.searchResultHashtags.isEmpty())
        return this.searchResultHashtags.size() + 1;
      j = this.searchResult.size();
      int n = this.searchAdapterHelper.getGlobalSearch().size();
      m = this.searchResultMessages.size();
      i = j;
      if (n != 0)
        i = j + (n + 1);
      j = i;
    }
    while (m == 0);
    if (this.messagesSearchEndReached);
    for (j = k; ; j = 1)
      return i + (j + (m + 1));
  }

  public long getItemId(int paramInt)
  {
    return paramInt;
  }

  public int getItemViewType(int paramInt)
  {
    int i = 2;
    if (isRecentSearchDisplayed())
      if (!SearchQuery.hints.isEmpty())
      {
        if (paramInt > i)
          break label43;
        if ((paramInt != i) && (paramInt % 2 != 0))
          break label41;
      }
    label41: label43: label61: int k;
    int j;
    label138: label151: 
    do
    {
      do
      {
        return 1;
        i = 0;
        break;
        return 5;
        return 0;
        if (this.searchResultHashtags.isEmpty())
          break label61;
      }
      while (paramInt == 0);
      return 4;
      ArrayList localArrayList = this.searchAdapterHelper.getGlobalSearch();
      k = this.searchResult.size();
      if (localArrayList.isEmpty())
      {
        i = 0;
        if (!this.searchResultMessages.isEmpty())
          break label138;
      }
      for (j = 0; ; j = this.searchResultMessages.size() + 1)
      {
        if (((paramInt < 0) || (paramInt >= k)) && ((paramInt <= k) || (paramInt >= i + k)))
          break label151;
        return 0;
        i = localArrayList.size() + 1;
        break;
      }
      if ((paramInt > i + k) && (paramInt < i + k + j))
        return 2;
    }
    while ((j == 0) || (paramInt != i + k + j));
    return 3;
  }

  public String getLastSearchString()
  {
    return this.lastMessagesSearchString;
  }

  public boolean hasRecentRearch()
  {
    return (!this.recentSearchObjects.isEmpty()) || (!SearchQuery.hints.isEmpty());
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramViewHolder.getItemViewType();
    return (i != 1) && (i != 3);
  }

  public boolean isGlobalSearch(int paramInt)
  {
    return (paramInt > this.searchResult.size()) && (paramInt <= this.searchAdapterHelper.getGlobalSearch().size() + this.searchResult.size());
  }

  public boolean isMessagesSearchEndReached()
  {
    return this.messagesSearchEndReached;
  }

  public boolean isRecentSearchDisplayed()
  {
    return (this.needMessagesSearch != 2) && ((this.lastSearchText == null) || (this.lastSearchText.length() == 0)) && ((!this.recentSearchObjects.isEmpty()) || (!SearchQuery.hints.isEmpty()));
  }

  public void loadMoreSearchMessages()
  {
    searchMessagesInternal(this.lastMessagesSearchString);
  }

  public void loadRecentSearch()
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
    {
      public void run()
      {
        int j = 0;
        Object localObject1;
        Object localObject3;
        ArrayList localArrayList2;
        HashMap localHashMap;
        long l;
        int i;
        Object localObject4;
        while (true)
        {
          int k;
          try
          {
            localObject2 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM search_recent WHERE 1", new Object[0]);
            localObject1 = new ArrayList();
            localObject3 = new ArrayList();
            localArrayList2 = new ArrayList();
            new ArrayList();
            ArrayList localArrayList1 = new ArrayList();
            localHashMap = new HashMap();
            if (!((SQLiteCursor)localObject2).next())
              break;
            l = ((SQLiteCursor)localObject2).longValue(0);
            i = (int)l;
            k = (int)(l >> 32);
            if (i == 0)
              break label279;
            if (k == 1)
            {
              if ((DialogsSearchAdapter.this.dialogsType != 0) || (((ArrayList)localObject3).contains(Integer.valueOf(i))))
                break label673;
              ((ArrayList)localObject3).add(Integer.valueOf(i));
              i = 1;
              if (i == 0)
                continue;
              localObject4 = new DialogsSearchAdapter.RecentSearchObject();
              ((DialogsSearchAdapter.RecentSearchObject)localObject4).did = l;
              ((DialogsSearchAdapter.RecentSearchObject)localObject4).date = ((SQLiteCursor)localObject2).intValue(1);
              localArrayList1.add(localObject4);
              localHashMap.put(Long.valueOf(((DialogsSearchAdapter.RecentSearchObject)localObject4).did), localObject4);
              continue;
            }
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            return;
          }
          if (i > 0)
          {
            if ((DialogsSearchAdapter.this.dialogsType == 2) || (((ArrayList)localObject1).contains(Integer.valueOf(i))))
              break label673;
            ((ArrayList)localObject1).add(Integer.valueOf(i));
            i = 1;
            continue;
          }
          if (((ArrayList)localObject3).contains(Integer.valueOf(-i)))
            break label673;
          ((ArrayList)localObject3).add(Integer.valueOf(-i));
          i = 1;
          continue;
          label279: if ((DialogsSearchAdapter.this.dialogsType != 0) || (localArrayList2.contains(Integer.valueOf(k))))
            break label673;
          localArrayList2.add(Integer.valueOf(k));
          i = 1;
        }
        ((SQLiteCursor)localObject2).dispose();
        Object localObject2 = new ArrayList();
        if (!localArrayList2.isEmpty())
        {
          localObject4 = new ArrayList();
          MessagesStorage.getInstance().getEncryptedChatsInternal(TextUtils.join(",", localArrayList2), (ArrayList)localObject4, (ArrayList)localObject1);
          i = 0;
          while (i < ((ArrayList)localObject4).size())
          {
            ((DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(((TLRPC.EncryptedChat)((ArrayList)localObject4).get(i)).id << 32))).object = ((TLObject)((ArrayList)localObject4).get(i));
            i += 1;
          }
        }
        if (!((ArrayList)localObject3).isEmpty())
        {
          localArrayList2 = new ArrayList();
          MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", (Iterable)localObject3), localArrayList2);
          i = 0;
          label455: if (i < localArrayList2.size())
          {
            localObject3 = (TLRPC.Chat)localArrayList2.get(i);
            if (((TLRPC.Chat)localObject3).id > 0)
              l = -((TLRPC.Chat)localObject3).id;
            while (((TLRPC.Chat)localObject3).migrated_to != null)
            {
              localObject3 = (DialogsSearchAdapter.RecentSearchObject)localHashMap.remove(Long.valueOf(l));
              if (localObject3 == null)
                break label678;
              localException.remove(localObject3);
              break label678;
              l = AndroidUtilities.makeBroadcastId(((TLRPC.Chat)localObject3).id);
            }
            ((DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(l))).object = ((TLObject)localObject3);
            break label678;
          }
        }
        if (!((ArrayList)localObject1).isEmpty())
        {
          MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", (Iterable)localObject1), (ArrayList)localObject2);
          i = j;
        }
        while (true)
        {
          if (i < ((ArrayList)localObject2).size())
          {
            localObject1 = (TLRPC.User)((ArrayList)localObject2).get(i);
            localObject3 = (DialogsSearchAdapter.RecentSearchObject)localHashMap.get(Long.valueOf(((TLRPC.User)localObject1).id));
            if (localObject3 != null)
              ((DialogsSearchAdapter.RecentSearchObject)localObject3).object = ((TLObject)localObject1);
          }
          else
          {
            Collections.sort(localException, new Comparator()
            {
              public int compare(DialogsSearchAdapter.RecentSearchObject paramRecentSearchObject1, DialogsSearchAdapter.RecentSearchObject paramRecentSearchObject2)
              {
                if (paramRecentSearchObject1.date < paramRecentSearchObject2.date)
                  return 1;
                if (paramRecentSearchObject1.date > paramRecentSearchObject2.date)
                  return -1;
                return 0;
              }
            });
            AndroidUtilities.runOnUIThread(new Runnable(localException, localHashMap)
            {
              public void run()
              {
                DialogsSearchAdapter.this.setRecentSearch(this.val$arrayList, this.val$hashMap);
              }
            });
            return;
            label673: i = 0;
            break;
            label678: i += 1;
            break label455;
          }
          i += 1;
        }
      }
    });
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    ProfileSearchCell localProfileSearchCell;
    Object localObject1;
    Object localObject2;
    RecyclerView.ViewHolder localViewHolder;
    TLRPC.EncryptedChat localEncryptedChat;
    boolean bool1;
    switch (paramViewHolder.getItemViewType())
    {
    case 3:
    default:
      return;
    case 0:
      localProfileSearchCell = (ProfileSearchCell)paramViewHolder.itemView;
      localObject1 = getItem(paramInt);
      if ((localObject1 instanceof TLRPC.User))
      {
        localObject2 = (TLRPC.User)localObject1;
        localObject1 = ((TLRPC.User)localObject2).username;
        localViewHolder = null;
        localEncryptedChat = null;
        if (!isRecentSearchDisplayed())
          break label260;
        boolean bool2 = true;
        if (paramInt != getItemCount() - 1)
        {
          bool1 = true;
          label112: localProfileSearchCell.useSeparator = bool1;
          paramViewHolder = null;
          localObject1 = null;
          bool1 = bool2;
          label128: if (localObject2 == null)
            break label542;
          label133: localProfileSearchCell.setData((TLObject)localObject2, localEncryptedChat, paramViewHolder, (CharSequence)localObject1, bool1);
          return;
        }
      }
      else
      {
        if (!(localObject1 instanceof TLRPC.Chat))
          break;
        paramViewHolder = MessagesController.getInstance().getChat(Integer.valueOf(((TLRPC.Chat)localObject1).id));
        if (paramViewHolder != null)
          break label874;
        paramViewHolder = (TLRPC.Chat)localObject1;
      }
    case 1:
    case 2:
    case 4:
    case 5:
    }
    label260: label286: label432: label438: label847: label874: 
    while (true)
    {
      localObject1 = paramViewHolder.username;
      localObject2 = null;
      localEncryptedChat = null;
      localViewHolder = paramViewHolder;
      break;
      if ((localObject1 instanceof TLRPC.EncryptedChat))
      {
        localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(((TLRPC.EncryptedChat)localObject1).id));
        localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(localEncryptedChat.user_id));
        localViewHolder = null;
        localObject1 = null;
        break;
        bool1 = false;
        break label112;
        paramViewHolder = this.searchAdapterHelper.getGlobalSearch();
        int j = this.searchResult.size();
        int i;
        if (paramViewHolder.isEmpty())
        {
          i = 0;
          if ((paramInt == getItemCount() - 1) || (paramInt == j - 1) || (paramInt == i + j - 1))
            break label432;
        }
        for (bool1 = true; ; bool1 = false)
        {
          localProfileSearchCell.useSeparator = bool1;
          if (paramInt >= this.searchResult.size())
            break label438;
          localObject1 = (CharSequence)this.searchResultNames.get(paramInt);
          if ((localObject1 == null) || (localObject2 == null) || (((TLRPC.User)localObject2).username == null) || (((TLRPC.User)localObject2).username.length() <= 0) || (!((CharSequence)localObject1).toString().startsWith("@" + ((TLRPC.User)localObject2).username)))
            break label847;
          paramViewHolder = null;
          bool1 = false;
          break;
          i = paramViewHolder.size() + 1;
          break label286;
        }
        if ((paramInt > this.searchResult.size()) && (localObject1 != null))
        {
          paramViewHolder = this.searchAdapterHelper.getLastFoundUsername();
          if (paramViewHolder.startsWith("@"))
            paramViewHolder = paramViewHolder.substring(1);
          while (true)
          {
            try
            {
              SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder((CharSequence)localObject1);
              ((SpannableStringBuilder)localSpannableStringBuilder).setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), 0, paramViewHolder.length(), 33);
              bool1 = false;
              localObject1 = localSpannableStringBuilder;
              paramViewHolder = null;
            }
            catch (Exception paramViewHolder)
            {
              FileLog.e(paramViewHolder);
              bool1 = false;
              paramViewHolder = null;
            }
            break;
            localObject2 = localViewHolder;
            break label133;
            paramViewHolder = (GraySectionCell)paramViewHolder.itemView;
            if (isRecentSearchDisplayed())
            {
              if (!SearchQuery.hints.isEmpty());
              for (i = 2; paramInt < i; i = 0)
              {
                paramViewHolder.setText(LocaleController.getString("ChatHints", 2131165534).toUpperCase());
                return;
              }
              paramViewHolder.setText(LocaleController.getString("Recent", 2131166315).toUpperCase());
              return;
            }
            if (!this.searchResultHashtags.isEmpty())
            {
              paramViewHolder.setText(LocaleController.getString("Hashtags", 2131165808).toUpperCase());
              return;
            }
            if ((!this.searchAdapterHelper.getGlobalSearch().isEmpty()) && (paramInt == this.searchResult.size()))
            {
              paramViewHolder.setText(LocaleController.getString("GlobalSearch", 2131165793));
              return;
            }
            paramViewHolder.setText(LocaleController.getString("SearchMessages", 2131166387));
            return;
            paramViewHolder = (DialogCell)paramViewHolder.itemView;
            if (paramInt != getItemCount() - 1);
            for (bool1 = true; ; bool1 = false)
            {
              paramViewHolder.useSeparator = bool1;
              localObject1 = (MessageObject)getItem(paramInt);
              paramViewHolder.setDialog(((MessageObject)localObject1).getDialogId(), (MessageObject)localObject1, ((MessageObject)localObject1).messageOwner.date);
              return;
            }
            paramViewHolder = (HashtagSearchCell)paramViewHolder.itemView;
            paramViewHolder.setText((CharSequence)this.searchResultHashtags.get(paramInt - 1));
            if (paramInt != this.searchResultHashtags.size());
            for (bool1 = true; ; bool1 = false)
            {
              paramViewHolder.setNeedDivider(bool1);
              return;
            }
            ((CategoryAdapterRecycler)((RecyclerListView)paramViewHolder.itemView).getAdapter()).setIndex(paramInt / 2);
            return;
          }
        }
        bool1 = false;
        paramViewHolder = null;
        localObject1 = null;
        break label128;
        bool1 = false;
        paramViewHolder = (RecyclerView.ViewHolder)localObject1;
        localObject1 = null;
        break label128;
      }
      localViewHolder = null;
      localObject2 = null;
      localObject1 = null;
      localEncryptedChat = null;
      break;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default:
      paramViewGroup = null;
      if (paramInt != 5)
        break;
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    }
    while (true)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new ProfileSearchCell(this.mContext);
      break;
      paramViewGroup = new GraySectionCell(this.mContext);
      break;
      paramViewGroup = new DialogCell(this.mContext);
      break;
      paramViewGroup = new LoadingCell(this.mContext);
      break;
      paramViewGroup = new HashtagSearchCell(this.mContext);
      break;
      paramViewGroup = new RecyclerListView(this.mContext)
      {
        public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
        {
          if ((getParent() != null) && (getParent().getParent() != null))
            getParent().getParent().requestDisallowInterceptTouchEvent(true);
          return super.onInterceptTouchEvent(paramMotionEvent);
        }
      };
      paramViewGroup.setTag(Integer.valueOf(9));
      paramViewGroup.setItemAnimator(null);
      paramViewGroup.setLayoutAnimation(null);
      10 local10 = new LinearLayoutManager(this.mContext)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      local10.setOrientation(0);
      paramViewGroup.setLayoutManager(local10);
      paramViewGroup.setAdapter(new CategoryAdapterRecycler(null));
      paramViewGroup.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (DialogsSearchAdapter.this.delegate != null)
            DialogsSearchAdapter.this.delegate.didPressedOnSubDialog(((Integer)paramView.getTag()).intValue());
        }
      });
      paramViewGroup.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
      {
        public boolean onItemClick(View paramView, int paramInt)
        {
          if (DialogsSearchAdapter.this.delegate != null)
            DialogsSearchAdapter.this.delegate.needRemoveHint(((Integer)paramView.getTag()).intValue());
          return true;
        }
      });
      this.innerListView = paramViewGroup;
      break;
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    }
  }

  public void putRecentSearch(long paramLong, TLObject paramTLObject)
  {
    RecentSearchObject localRecentSearchObject = (RecentSearchObject)this.recentSearchObjectsById.get(Long.valueOf(paramLong));
    if (localRecentSearchObject == null)
    {
      localRecentSearchObject = new RecentSearchObject();
      this.recentSearchObjectsById.put(Long.valueOf(paramLong), localRecentSearchObject);
    }
    while (true)
    {
      this.recentSearchObjects.add(0, localRecentSearchObject);
      localRecentSearchObject.did = paramLong;
      localRecentSearchObject.object = paramTLObject;
      localRecentSearchObject.date = (int)(System.currentTimeMillis() / 1000L);
      notifyDataSetChanged();
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramLong)
      {
        public void run()
        {
          try
          {
            SQLitePreparedStatement localSQLitePreparedStatement = MessagesStorage.getInstance().getDatabase().executeFast("REPLACE INTO search_recent VALUES(?, ?)");
            localSQLitePreparedStatement.requery();
            localSQLitePreparedStatement.bindLong(1, this.val$did);
            localSQLitePreparedStatement.bindInteger(2, (int)(System.currentTimeMillis() / 1000L));
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
      this.recentSearchObjects.remove(localRecentSearchObject);
    }
  }

  public void searchDialogs(String paramString)
  {
    if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText)))
      return;
    this.lastSearchText = paramString;
    try
    {
      if (this.searchTimer != null)
      {
        this.searchTimer.cancel();
        this.searchTimer = null;
      }
      if ((paramString == null) || (paramString.length() == 0))
      {
        this.searchAdapterHelper.unloadRecentHashtags();
        this.searchResult.clear();
        this.searchResultNames.clear();
        this.searchResultHashtags.clear();
        if (this.needMessagesSearch != 2)
          this.searchAdapterHelper.queryServerSearch(null, true, true, true);
        searchMessagesInternal(null);
        notifyDataSetChanged();
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
      if (this.needMessagesSearch == 2)
        break label310;
    }
    int i;
    if ((paramString.startsWith("#")) && (paramString.length() == 1))
    {
      this.messagesSearchEndReached = true;
      if (this.searchAdapterHelper.loadRecentHashtags())
      {
        this.searchResultMessages.clear();
        this.searchResultHashtags.clear();
        ArrayList localArrayList = this.searchAdapterHelper.getHashtags();
        i = 0;
        while (i < localArrayList.size())
        {
          this.searchResultHashtags.add(((SearchAdapterHelper.HashtagObject)localArrayList.get(i)).hashtag);
          i += 1;
        }
        if (this.delegate != null)
          this.delegate.searchStateChanged(false);
        notifyDataSetChanged();
      }
    }
    while (true)
    {
      i = this.lastSearchId + 1;
      this.lastSearchId = i;
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask(paramString, i)
      {
        public void run()
        {
          try
          {
            cancel();
            DialogsSearchAdapter.this.searchTimer.cancel();
            DialogsSearchAdapter.access$1402(DialogsSearchAdapter.this, null);
            DialogsSearchAdapter.this.searchDialogsInternal(this.val$query, this.val$searchId);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                if (DialogsSearchAdapter.this.needMessagesSearch != 2)
                  DialogsSearchAdapter.this.searchAdapterHelper.queryServerSearch(DialogsSearchAdapter.8.this.val$query, true, true, true);
                DialogsSearchAdapter.this.searchMessagesInternal(DialogsSearchAdapter.8.this.val$query);
              }
            });
            return;
          }
          catch (Exception localException)
          {
            while (true)
              FileLog.e(localException);
          }
        }
      }
      , 200L, 300L);
      return;
      if (this.delegate == null)
        break;
      this.delegate.searchStateChanged(true);
      break;
      label310: this.searchResultHashtags.clear();
      notifyDataSetChanged();
    }
  }

  public void setDelegate(DialogsSearchAdapterDelegate paramDialogsSearchAdapterDelegate)
  {
    this.delegate = paramDialogsSearchAdapterDelegate;
  }

  private class CategoryAdapterRecycler extends RecyclerListView.SelectionAdapter
  {
    private CategoryAdapterRecycler()
    {
    }

    public int getItemCount()
    {
      return SearchQuery.hints.size();
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      TLRPC.Chat localChat = null;
      HintDialogCell localHintDialogCell = (HintDialogCell)paramViewHolder.itemView;
      paramViewHolder = (TLRPC.TL_topPeer)SearchQuery.hints.get(paramInt);
      new TLRPC.TL_dialog();
      if (paramViewHolder.peer.user_id != 0)
      {
        paramInt = paramViewHolder.peer.user_id;
        paramViewHolder = MessagesController.getInstance().getUser(Integer.valueOf(paramViewHolder.peer.user_id));
      }
      while (true)
      {
        localHintDialogCell.setTag(Integer.valueOf(paramInt));
        if (paramViewHolder != null)
          paramViewHolder = ContactsController.formatName(paramViewHolder.first_name, paramViewHolder.last_name);
        while (true)
        {
          localHintDialogCell.setDialog(paramInt, true, paramViewHolder);
          return;
          if (paramViewHolder.peer.channel_id != 0)
          {
            paramInt = -paramViewHolder.peer.channel_id;
            localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramViewHolder.peer.channel_id));
            paramViewHolder = null;
            break;
          }
          if (paramViewHolder.peer.chat_id == 0)
            break label199;
          paramInt = -paramViewHolder.peer.chat_id;
          localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramViewHolder.peer.chat_id));
          paramViewHolder = null;
          break;
          if (localChat != null)
          {
            paramViewHolder = localChat.title;
            continue;
          }
          paramViewHolder = "";
        }
        label199: paramInt = 0;
        paramViewHolder = null;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new HintDialogCell(DialogsSearchAdapter.this.mContext);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(80.0F), AndroidUtilities.dp(100.0F)));
      return new RecyclerListView.Holder(paramViewGroup);
    }

    public void setIndex(int paramInt)
    {
      notifyDataSetChanged();
    }
  }

  private class DialogSearchResult
  {
    public int date;
    public CharSequence name;
    public TLObject object;

    private DialogSearchResult()
    {
    }
  }

  public static abstract interface DialogsSearchAdapterDelegate
  {
    public abstract void didPressedOnSubDialog(int paramInt);

    public abstract void needRemoveHint(int paramInt);

    public abstract void searchStateChanged(boolean paramBoolean);
  }

  protected static class RecentSearchObject
  {
    int date;
    long did;
    TLObject object;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.DialogsSearchAdapter
 * JD-Core Version:    0.6.0
 */