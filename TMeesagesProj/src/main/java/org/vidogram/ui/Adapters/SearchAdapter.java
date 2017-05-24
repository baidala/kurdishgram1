package org.vidogram.ui.Adapters;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.TL_contact;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.ProfileSearchCell;
import org.vidogram.ui.Cells.UserCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class SearchAdapter extends RecyclerListView.SelectionAdapter
{
  private boolean allowBots;
  private boolean allowChats;
  private boolean allowUsernameSearch;
  private HashMap<Integer, ?> checkedMap;
  private HashMap<Integer, TLRPC.User> ignoreUsers;
  private Context mContext;
  private boolean onlyMutual;
  private SearchAdapterHelper searchAdapterHelper;
  private ArrayList<TLRPC.User> searchResult = new ArrayList();
  private ArrayList<CharSequence> searchResultNames = new ArrayList();
  private Timer searchTimer;
  private boolean useUserCell;

  public SearchAdapter(Context paramContext, HashMap<Integer, TLRPC.User> paramHashMap, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4)
  {
    this.mContext = paramContext;
    this.ignoreUsers = paramHashMap;
    this.onlyMutual = paramBoolean2;
    this.allowUsernameSearch = paramBoolean1;
    this.allowChats = paramBoolean3;
    this.allowBots = paramBoolean4;
    this.searchAdapterHelper = new SearchAdapterHelper();
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        SearchAdapter.this.notifyDataSetChanged();
      }

      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramHashMap)
      {
      }
    });
  }

  private void processSearch(String paramString)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramString)
    {
      public void run()
      {
        if (SearchAdapter.this.allowUsernameSearch)
          SearchAdapter.this.searchAdapterHelper.queryServerSearch(this.val$query, SearchAdapter.this.allowChats, SearchAdapter.this.allowBots, true);
        ArrayList localArrayList = new ArrayList();
        localArrayList.addAll(ContactsController.getInstance().contacts);
        Utilities.searchQueue.postRunnable(new Runnable(localArrayList)
        {
          public void run()
          {
            String str1 = SearchAdapter.3.this.val$query.trim().toLowerCase();
            if (str1.length() == 0)
            {
              SearchAdapter.this.updateSearchResults(new ArrayList(), new ArrayList());
              return;
            }
            Object localObject = LocaleController.getInstance().getTranslitString(str1);
            if ((str1.equals(localObject)) || (((String)localObject).length() == 0))
              localObject = null;
            while (true)
            {
              int i;
              String[] arrayOfString;
              ArrayList localArrayList1;
              ArrayList localArrayList2;
              int j;
              label131: TLRPC.User localUser;
              if (localObject != null)
              {
                i = 1;
                arrayOfString = new String[i + 1];
                arrayOfString[0] = str1;
                if (localObject != null)
                  arrayOfString[1] = localObject;
                localArrayList1 = new ArrayList();
                localArrayList2 = new ArrayList();
                j = 0;
                if (j >= this.val$contactsCopy.size())
                  break label504;
                localObject = (TLRPC.TL_contact)this.val$contactsCopy.get(j);
                localUser = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_contact)localObject).user_id));
                if ((localUser.id != UserConfig.getClientUserId()) && ((!SearchAdapter.this.onlyMutual) || (localUser.mutual_contact)))
                  break label215;
              }
              label215: label365: label494: label502: 
              while (true)
              {
                j += 1;
                break label131;
                i = 0;
                break;
                String str2 = ContactsController.formatName(localUser.first_name, localUser.last_name).toLowerCase();
                str1 = LocaleController.getInstance().getTranslitString(str2);
                localObject = str1;
                if (str2.equals(str1))
                  localObject = null;
                int n = arrayOfString.length;
                int m = 0;
                int k = 0;
                while (true)
                {
                  if (k >= n)
                    break label502;
                  str1 = arrayOfString[k];
                  if ((str2.startsWith(str1)) || (str2.contains(" " + str1)) || ((localObject != null) && ((((String)localObject).startsWith(str1)) || (((String)localObject).contains(" " + str1)))))
                  {
                    i = 1;
                    if (i == 0)
                      break label494;
                    if (i != 1)
                      break label438;
                    localArrayList2.add(AndroidUtilities.generateSearchName(localUser.first_name, localUser.last_name, str1));
                  }
                  while (true)
                  {
                    localArrayList1.add(localUser);
                    break;
                    i = m;
                    if (localUser.username == null)
                      break label365;
                    i = m;
                    if (!localUser.username.startsWith(str1))
                      break label365;
                    i = 2;
                    break label365;
                    localArrayList2.add(AndroidUtilities.generateSearchName("@" + localUser.username, null, "@" + str1));
                  }
                  k += 1;
                  m = i;
                }
              }
              label438: label504: SearchAdapter.this.updateSearchResults(localArrayList1, localArrayList2);
              return;
            }
          }
        });
      }
    });
  }

  private void updateSearchResults(ArrayList<TLRPC.User> paramArrayList, ArrayList<CharSequence> paramArrayList1)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList, paramArrayList1)
    {
      public void run()
      {
        SearchAdapter.access$802(SearchAdapter.this, this.val$users);
        SearchAdapter.access$902(SearchAdapter.this, this.val$names);
        SearchAdapter.this.notifyDataSetChanged();
      }
    });
  }

  public TLObject getItem(int paramInt)
  {
    int i = this.searchResult.size();
    int j = this.searchAdapterHelper.getGlobalSearch().size();
    if ((paramInt >= 0) && (paramInt < i))
      return (TLObject)this.searchResult.get(paramInt);
    if ((paramInt > i) && (paramInt <= j + i))
      return (TLObject)this.searchAdapterHelper.getGlobalSearch().get(paramInt - i - 1);
    return null;
  }

  public int getItemCount()
  {
    int j = this.searchResult.size();
    int k = this.searchAdapterHelper.getGlobalSearch().size();
    int i = j;
    if (k != 0)
      i = j + (k + 1);
    return i;
  }

  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.searchResult.size())
      return 1;
    return 0;
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return paramViewHolder.getAdapterPosition() != this.searchResult.size();
  }

  public boolean isGlobalSearch(int paramInt)
  {
    int i = this.searchResult.size();
    int j = this.searchAdapterHelper.getGlobalSearch().size();
    if ((paramInt >= 0) && (paramInt < i));
    do
      return false;
    while ((paramInt <= i) || (paramInt > i + j));
    return true;
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = false;
    TLObject localTLObject;
    Object localObject2;
    int i;
    if (paramViewHolder.getItemViewType() == 0)
    {
      localTLObject = getItem(paramInt);
      if (localTLObject != null)
      {
        if (!(localTLObject instanceof TLRPC.User))
          break label185;
        localObject2 = ((TLRPC.User)localTLObject).username;
        i = ((TLRPC.User)localTLObject).id;
      }
    }
    while (true)
    {
      Object localObject1;
      SpannableStringBuilder localSpannableStringBuilder;
      if (paramInt < this.searchResult.size())
      {
        localObject1 = (CharSequence)this.searchResultNames.get(paramInt);
        if ((localObject1 == null) || (localObject2 == null) || (((String)localObject2).length() <= 0) || (!((CharSequence)localObject1).toString().startsWith("@" + (String)localObject2)))
          break label393;
        localSpannableStringBuilder = null;
        localObject2 = localObject1;
        localObject1 = localSpannableStringBuilder;
      }
      while (true)
      {
        if (this.useUserCell)
        {
          paramViewHolder = (UserCell)paramViewHolder.itemView;
          paramViewHolder.setData(localTLObject, (CharSequence)localObject1, (CharSequence)localObject2, 0);
          if (this.checkedMap != null)
            paramViewHolder.setChecked(this.checkedMap.containsKey(Integer.valueOf(i)), false);
          return;
          label185: if (!(localTLObject instanceof TLRPC.Chat))
            break label399;
          localObject2 = ((TLRPC.Chat)localTLObject).username;
          i = ((TLRPC.Chat)localTLObject).id;
          break;
          if ((paramInt <= this.searchResult.size()) || (localObject2 == null))
            break label384;
          localObject1 = this.searchAdapterHelper.getLastFoundUsername();
          if (!((String)localObject1).startsWith("@"))
            break label381;
          localObject1 = ((String)localObject1).substring(1);
        }
        label381: 
        while (true)
        {
          try
          {
            localSpannableStringBuilder = new SpannableStringBuilder((CharSequence)localObject2);
            ((SpannableStringBuilder)localSpannableStringBuilder).setSpan(new ForegroundColorSpan(Theme.getColor("windowBackgroundWhiteBlueText4")), 0, ((String)localObject1).length(), 33);
            localObject2 = localSpannableStringBuilder;
            localObject1 = null;
          }
          catch (Exception localCharSequence)
          {
            FileLog.e(localException);
            localCharSequence = null;
          }
          break;
          paramViewHolder = (ProfileSearchCell)paramViewHolder.itemView;
          paramViewHolder.setData(localTLObject, null, localCharSequence, (CharSequence)localObject2, false);
          boolean bool1 = bool2;
          if (paramInt != getItemCount() - 1)
          {
            bool1 = bool2;
            if (paramInt != this.searchResult.size() - 1)
              bool1 = true;
          }
          paramViewHolder.useSeparator = bool1;
          return;
        }
        label384: CharSequence localCharSequence = null;
        localObject2 = null;
        continue;
        label393: localObject2 = null;
      }
      label399: localObject2 = null;
      i = 0;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default:
      paramViewGroup = new GraySectionCell(this.mContext);
      ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("GlobalSearch", 2131165793));
    case 0:
    }
    while (true)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      if (this.useUserCell)
      {
        UserCell localUserCell = new UserCell(this.mContext, 1, 1, false);
        paramViewGroup = localUserCell;
        if (this.checkedMap == null)
          continue;
        ((UserCell)localUserCell).setChecked(false, false);
        paramViewGroup = localUserCell;
        continue;
      }
      paramViewGroup = new ProfileSearchCell(this.mContext);
    }
  }

  public void searchDialogs(String paramString)
  {
    try
    {
      if (this.searchTimer != null)
        this.searchTimer.cancel();
      if (paramString == null)
      {
        this.searchResult.clear();
        this.searchResultNames.clear();
        if (this.allowUsernameSearch)
          this.searchAdapterHelper.queryServerSearch(null, this.allowChats, this.allowBots, true);
        notifyDataSetChanged();
        return;
      }
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask(paramString)
      {
        public void run()
        {
          try
          {
            SearchAdapter.this.searchTimer.cancel();
            SearchAdapter.access$002(SearchAdapter.this, null);
            SearchAdapter.this.processSearch(this.val$query);
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
    }
  }

  public void setCheckedMap(HashMap<Integer, ?> paramHashMap)
  {
    this.checkedMap = paramHashMap;
  }

  public void setUseUserCell(boolean paramBoolean)
  {
    this.useUserCell = paramBoolean;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.SearchAdapter
 * JD-Core Version:    0.6.0
 */