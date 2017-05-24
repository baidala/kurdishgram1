package org.vidogram.ui.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.SendMessagesHelper.LocationProvider;
import org.vidogram.messenger.SendMessagesHelper.LocationProvider.LocationProviderDelegate;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInfo;
import org.vidogram.tgnet.TLRPC.BotInlineResult;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_botCommand;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.vidogram.tgnet.TLRPC.TL_channelFull;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inlineBotSwitchPM;
import org.vidogram.tgnet.TLRPC.TL_inputGeoPoint;
import org.vidogram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.vidogram.tgnet.TLRPC.TL_messages_botResults;
import org.vidogram.tgnet.TLRPC.TL_messages_getInlineBotResults;
import org.vidogram.tgnet.TLRPC.TL_photo;
import org.vidogram.tgnet.TLRPC.TL_topPeer;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.Cells.BotSwitchCell;
import org.vidogram.ui.Cells.ContextLinkCell;
import org.vidogram.ui.Cells.ContextLinkCell.ContextLinkCellDelegate;
import org.vidogram.ui.Cells.MentionCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class MentionsAdapter extends RecyclerListView.SelectionAdapter
{
  private boolean allowNewMentions = true;
  private HashMap<Integer, TLRPC.BotInfo> botInfo;
  private int botsCount;
  private boolean contextMedia;
  private int contextQueryReqid;
  private Runnable contextQueryRunnable;
  private int contextUsernameReqid;
  private MentionsAdapterDelegate delegate;
  private long dialog_id;
  private TLRPC.User foundContextBot;
  private TLRPC.ChatFull info;
  private boolean isDarkTheme;
  private Location lastKnownLocation;
  private int lastPosition;
  private String lastText;
  private SendMessagesHelper.LocationProvider locationProvider = new SendMessagesHelper.LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramLocation)
    {
      if ((MentionsAdapter.this.foundContextBot != null) && (MentionsAdapter.this.foundContextBot.bot_inline_geo))
      {
        MentionsAdapter.access$102(MentionsAdapter.this, paramLocation);
        MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, MentionsAdapter.this.searchingContextQuery, "");
      }
    }

    public void onUnableLocationAcquire()
    {
      MentionsAdapter.this.onLocationUnavailable();
    }
  })
  {
    public void stop()
    {
      super.stop();
      MentionsAdapter.access$102(MentionsAdapter.this, null);
    }
  };
  private Context mContext;
  private ArrayList<MessageObject> messages;
  private boolean needBotContext = true;
  private boolean needUsernames = true;
  private String nextQueryOffset;
  private boolean noUserName;
  private BaseFragment parentFragment;
  private int resultLength;
  private int resultStartPosition;
  private SearchAdapterHelper searchAdapterHelper;
  private ArrayList<TLRPC.BotInlineResult> searchResultBotContext;
  private HashMap<String, TLRPC.BotInlineResult> searchResultBotContextById;
  private TLRPC.TL_inlineBotSwitchPM searchResultBotContextSwitch;
  private ArrayList<String> searchResultCommands;
  private ArrayList<String> searchResultCommandsHelp;
  private ArrayList<TLRPC.User> searchResultCommandsUsers;
  private ArrayList<String> searchResultHashtags;
  private ArrayList<TLRPC.User> searchResultUsernames;
  private String searchingContextQuery;
  private String searchingContextUsername;

  public MentionsAdapter(Context paramContext, boolean paramBoolean, long paramLong, MentionsAdapterDelegate paramMentionsAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramMentionsAdapterDelegate;
    this.isDarkTheme = paramBoolean;
    this.dialog_id = paramLong;
    this.searchAdapterHelper = new SearchAdapterHelper();
    this.searchAdapterHelper.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate()
    {
      public void onDataSetChanged()
      {
        MentionsAdapter.this.notifyDataSetChanged();
      }

      public void onSetHashtags(ArrayList<SearchAdapterHelper.HashtagObject> paramArrayList, HashMap<String, SearchAdapterHelper.HashtagObject> paramHashMap)
      {
        if (MentionsAdapter.this.lastText != null)
          MentionsAdapter.this.searchUsernameOrHashtag(MentionsAdapter.this.lastText, MentionsAdapter.this.lastPosition, MentionsAdapter.this.messages);
      }
    });
  }

  private void checkLocationPermissionsOrStart()
  {
    if ((this.parentFragment == null) || (this.parentFragment.getParentActivity() == null));
    do
    {
      return;
      if ((Build.VERSION.SDK_INT < 23) || (this.parentFragment.getParentActivity().checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0))
        continue;
      this.parentFragment.getParentActivity().requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
      return;
    }
    while ((this.foundContextBot == null) || (!this.foundContextBot.bot_inline_geo));
    this.locationProvider.start();
  }

  private void onLocationUnavailable()
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      this.lastKnownLocation = new Location("network");
      this.lastKnownLocation.setLatitude(-1000.0D);
      this.lastKnownLocation.setLongitude(-1000.0D);
      searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
    }
  }

  private void processFoundUser(TLRPC.User paramUser)
  {
    this.contextUsernameReqid = 0;
    this.locationProvider.stop();
    if ((paramUser != null) && (paramUser.bot) && (paramUser.bot_inline_placeholder != null))
    {
      this.foundContextBot = paramUser;
      if (this.foundContextBot.bot_inline_geo)
      {
        if ((ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("inlinegeo_" + this.foundContextBot.id, false)) || (this.parentFragment == null) || (this.parentFragment.getParentActivity() == null))
          break label240;
        paramUser = this.foundContextBot;
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.parentFragment.getParentActivity());
        localBuilder.setTitle(LocaleController.getString("ShareYouLocationTitle", 2131166458));
        localBuilder.setMessage(LocaleController.getString("ShareYouLocationInline", 2131166457));
        boolean[] arrayOfBoolean = new boolean[1];
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(arrayOfBoolean, paramUser)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            this.val$buttonClicked[0] = true;
            if (this.val$foundContextBotFinal != null)
            {
              ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("inlinegeo_" + this.val$foundContextBotFinal.id, true).commit();
              MentionsAdapter.this.checkLocationPermissionsOrStart();
            }
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(arrayOfBoolean)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            this.val$buttonClicked[0] = true;
            MentionsAdapter.this.onLocationUnavailable();
          }
        });
        this.parentFragment.showDialog(localBuilder.create(), new DialogInterface.OnDismissListener(arrayOfBoolean)
        {
          public void onDismiss(DialogInterface paramDialogInterface)
          {
            if (this.val$buttonClicked[0] == 0)
              MentionsAdapter.this.onLocationUnavailable();
          }
        });
      }
    }
    while (this.foundContextBot == null)
    {
      this.noUserName = true;
      return;
      label240: checkLocationPermissionsOrStart();
      continue;
      this.foundContextBot = null;
    }
    if (this.delegate != null)
      this.delegate.onContextSearch(true);
    searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, "");
  }

  private void searchForContextBot(String paramString1, String paramString2)
  {
    if ((this.foundContextBot != null) && (this.foundContextBot.username != null) && (this.foundContextBot.username.equals(paramString1)) && (this.searchingContextQuery != null) && (this.searchingContextQuery.equals(paramString2)));
    while (true)
    {
      return;
      this.searchResultBotContext = null;
      this.searchResultBotContextById = null;
      this.searchResultBotContextSwitch = null;
      notifyDataSetChanged();
      if (this.foundContextBot != null)
        this.delegate.needChangePanelVisibility(false);
      if (this.contextQueryRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
        this.contextQueryRunnable = null;
      }
      if ((TextUtils.isEmpty(paramString1)) || ((this.searchingContextUsername != null) && (!this.searchingContextUsername.equals(paramString1))))
      {
        if (this.contextUsernameReqid != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
          this.contextUsernameReqid = 0;
        }
        if (this.contextQueryReqid != 0)
        {
          ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
          this.contextQueryReqid = 0;
        }
        this.foundContextBot = null;
        this.searchingContextUsername = null;
        this.searchingContextQuery = null;
        this.locationProvider.stop();
        this.noUserName = false;
        if (this.delegate != null)
          this.delegate.onContextSearch(false);
        if ((paramString1 == null) || (paramString1.length() == 0))
          continue;
      }
      if (paramString2 != null)
        break;
      if (this.contextQueryReqid != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
        this.contextQueryReqid = 0;
      }
      this.searchingContextQuery = null;
      if (this.delegate == null)
        continue;
      this.delegate.onContextSearch(false);
      return;
    }
    if (this.delegate != null)
    {
      if (this.foundContextBot == null)
        break label335;
      this.delegate.onContextSearch(true);
    }
    while (true)
    {
      this.searchingContextQuery = paramString2;
      this.contextQueryRunnable = new Runnable(paramString2, paramString1)
      {
        public void run()
        {
          if (MentionsAdapter.this.contextQueryRunnable != this);
          while (true)
          {
            return;
            MentionsAdapter.access$902(MentionsAdapter.this, null);
            if ((MentionsAdapter.this.foundContextBot == null) && (!MentionsAdapter.this.noUserName))
              break;
            if (MentionsAdapter.this.noUserName)
              continue;
            MentionsAdapter.this.searchForContextBotResults(true, MentionsAdapter.this.foundContextBot, this.val$query, "");
            return;
          }
          MentionsAdapter.access$1102(MentionsAdapter.this, this.val$username);
          Object localObject = MessagesController.getInstance().getUser(MentionsAdapter.this.searchingContextUsername);
          if (localObject != null)
          {
            MentionsAdapter.this.processFoundUser((TLRPC.User)localObject);
            return;
          }
          localObject = new TLRPC.TL_contacts_resolveUsername();
          ((TLRPC.TL_contacts_resolveUsername)localObject).username = MentionsAdapter.this.searchingContextUsername;
          MentionsAdapter.access$1302(MentionsAdapter.this, ConnectionsManager.getInstance().sendRequest((TLObject)localObject, new RequestDelegate()
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
              {
                public void run()
                {
                  if ((MentionsAdapter.this.searchingContextUsername == null) || (!MentionsAdapter.this.searchingContextUsername.equals(MentionsAdapter.7.this.val$username)))
                    return;
                  TLRPC.User localUser;
                  if (this.val$error == null)
                  {
                    TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)this.val$response;
                    if (!localTL_contacts_resolvedPeer.users.isEmpty())
                    {
                      localUser = (TLRPC.User)localTL_contacts_resolvedPeer.users.get(0);
                      MessagesController.getInstance().putUser(localUser, false);
                      MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, null, true, true);
                    }
                  }
                  while (true)
                  {
                    MentionsAdapter.this.processFoundUser(localUser);
                    return;
                    localUser = null;
                  }
                }
              });
            }
          }));
        }
      };
      AndroidUtilities.runOnUIThread(this.contextQueryRunnable, 400L);
      return;
      label335: if (!paramString1.equals("gif"))
        continue;
      this.searchingContextUsername = "gif";
      this.delegate.onContextSearch(false);
    }
  }

  private void searchForContextBotResults(boolean paramBoolean, TLRPC.User paramUser, String paramString1, String paramString2)
  {
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    if ((paramString1 == null) || (paramUser == null))
      this.searchingContextQuery = null;
    do
      return;
    while ((paramUser.bot_inline_geo) && (this.lastKnownLocation == null));
    Object localObject2 = new StringBuilder().append(this.dialog_id).append("_").append(paramString1).append("_").append(paramString2).append("_").append(this.dialog_id).append("_").append(paramUser.id).append("_");
    if ((paramUser.bot_inline_geo) && (this.lastKnownLocation != null) && (this.lastKnownLocation.getLatitude() != -1000.0D));
    for (Object localObject1 = Double.valueOf(this.lastKnownLocation.getLatitude() + this.lastKnownLocation.getLongitude()); ; localObject1 = "")
    {
      localObject2 = localObject1;
      localObject1 = new RequestDelegate(paramString1, paramBoolean, paramUser, paramString2, (String)localObject2)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              boolean bool = false;
              if ((MentionsAdapter.this.searchingContextQuery == null) || (!MentionsAdapter.8.this.val$query.equals(MentionsAdapter.this.searchingContextQuery)));
              do
              {
                return;
                if (MentionsAdapter.this.delegate != null)
                  MentionsAdapter.this.delegate.onContextSearch(false);
                MentionsAdapter.access$1502(MentionsAdapter.this, 0);
                if ((!MentionsAdapter.8.this.val$cache) || (this.val$response != null))
                  continue;
                MentionsAdapter.this.searchForContextBotResults(false, MentionsAdapter.8.this.val$user, MentionsAdapter.8.this.val$query, MentionsAdapter.8.this.val$offset);
              }
              while (this.val$response == null);
              Object localObject1 = (TLRPC.TL_messages_botResults)this.val$response;
              if ((!MentionsAdapter.8.this.val$cache) && (((TLRPC.TL_messages_botResults)localObject1).cache_time != 0))
                MessagesStorage.getInstance().saveBotCache(MentionsAdapter.8.this.val$key, (TLObject)localObject1);
              MentionsAdapter.access$1602(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).next_offset);
              if (MentionsAdapter.this.searchResultBotContextById == null)
              {
                MentionsAdapter.access$1702(MentionsAdapter.this, new HashMap());
                MentionsAdapter.access$1802(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).switch_pm);
              }
              Object localObject2;
              int j;
              for (int i = 0; i < ((TLRPC.TL_messages_botResults)localObject1).results.size(); i = j + 1)
              {
                localObject2 = (TLRPC.BotInlineResult)((TLRPC.TL_messages_botResults)localObject1).results.get(i);
                if (!MentionsAdapter.this.searchResultBotContextById.containsKey(((TLRPC.BotInlineResult)localObject2).id))
                {
                  j = i;
                  if (!(((TLRPC.BotInlineResult)localObject2).document instanceof TLRPC.TL_document))
                  {
                    j = i;
                    if (!(((TLRPC.BotInlineResult)localObject2).photo instanceof TLRPC.TL_photo))
                    {
                      j = i;
                      if (((TLRPC.BotInlineResult)localObject2).content_url == null)
                      {
                        j = i;
                        if (!(((TLRPC.BotInlineResult)localObject2).send_message instanceof TLRPC.TL_botInlineMessageMediaAuto));
                      }
                    }
                  }
                }
                else
                {
                  ((TLRPC.TL_messages_botResults)localObject1).results.remove(i);
                  j = i - 1;
                }
                ((TLRPC.BotInlineResult)localObject2).query_id = ((TLRPC.TL_messages_botResults)localObject1).query_id;
                MentionsAdapter.this.searchResultBotContextById.put(((TLRPC.BotInlineResult)localObject2).id, localObject2);
              }
              if ((MentionsAdapter.this.searchResultBotContext == null) || (MentionsAdapter.8.this.val$offset.length() == 0))
              {
                MentionsAdapter.access$1902(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).results);
                MentionsAdapter.access$2002(MentionsAdapter.this, ((TLRPC.TL_messages_botResults)localObject1).gallery);
                i = 0;
                MentionsAdapter.access$2102(MentionsAdapter.this, null);
                MentionsAdapter.access$2202(MentionsAdapter.this, null);
                MentionsAdapter.access$2302(MentionsAdapter.this, null);
                MentionsAdapter.access$2402(MentionsAdapter.this, null);
                MentionsAdapter.access$2502(MentionsAdapter.this, null);
                if (i == 0)
                  break label756;
                if (MentionsAdapter.this.searchResultBotContextSwitch == null)
                  break label741;
                i = 1;
                label531: localObject2 = MentionsAdapter.this;
                int k = MentionsAdapter.this.searchResultBotContext.size();
                int m = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0)
                  break label746;
                j = 1;
                label570: ((MentionsAdapter)localObject2).notifyItemChanged(j + (k - m) - 1);
                localObject2 = MentionsAdapter.this;
                j = MentionsAdapter.this.searchResultBotContext.size();
                k = ((TLRPC.TL_messages_botResults)localObject1).results.size();
                if (i == 0)
                  break label751;
                i = 1;
                label621: ((MentionsAdapter)localObject2).notifyItemRangeInserted(i + (j - k), ((TLRPC.TL_messages_botResults)localObject1).results.size());
              }
              while (true)
              {
                localObject1 = MentionsAdapter.this.delegate;
                if ((!MentionsAdapter.this.searchResultBotContext.isEmpty()) || (MentionsAdapter.this.searchResultBotContextSwitch != null))
                  bool = true;
                ((MentionsAdapter.MentionsAdapterDelegate)localObject1).needChangePanelVisibility(bool);
                return;
                MentionsAdapter.this.searchResultBotContext.addAll(((TLRPC.TL_messages_botResults)localObject1).results);
                if (((TLRPC.TL_messages_botResults)localObject1).results.isEmpty())
                  MentionsAdapter.access$1602(MentionsAdapter.this, "");
                i = 1;
                break;
                label741: i = 0;
                break label531;
                label746: j = 0;
                break label570;
                label751: i = 0;
                break label621;
                label756: MentionsAdapter.this.notifyDataSetChanged();
              }
            }
          });
        }
      };
      if (!paramBoolean)
        break;
      MessagesStorage.getInstance().getBotCache((String)localObject2, (RequestDelegate)localObject1);
      return;
    }
    localObject2 = new TLRPC.TL_messages_getInlineBotResults();
    ((TLRPC.TL_messages_getInlineBotResults)localObject2).bot = MessagesController.getInputUser(paramUser);
    ((TLRPC.TL_messages_getInlineBotResults)localObject2).query = paramString1;
    ((TLRPC.TL_messages_getInlineBotResults)localObject2).offset = paramString2;
    if ((paramUser.bot_inline_geo) && (this.lastKnownLocation != null) && (this.lastKnownLocation.getLatitude() != -1000.0D))
    {
      ((TLRPC.TL_messages_getInlineBotResults)localObject2).flags |= 1;
      ((TLRPC.TL_messages_getInlineBotResults)localObject2).geo_point = new TLRPC.TL_inputGeoPoint();
      ((TLRPC.TL_messages_getInlineBotResults)localObject2).geo_point.lat = this.lastKnownLocation.getLatitude();
      ((TLRPC.TL_messages_getInlineBotResults)localObject2).geo_point._long = this.lastKnownLocation.getLongitude();
    }
    int i = (int)this.dialog_id;
    int j = (int)(this.dialog_id >> 32);
    if (i != 0);
    for (((TLRPC.TL_messages_getInlineBotResults)localObject2).peer = MessagesController.getInputPeer(i); ; ((TLRPC.TL_messages_getInlineBotResults)localObject2).peer = new TLRPC.TL_inputPeerEmpty())
    {
      this.contextQueryReqid = ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, (RequestDelegate)localObject1, 2);
      return;
    }
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
    if (this.delegate != null)
      this.delegate.needChangePanelVisibility(false);
  }

  public String getBotCaption()
  {
    if (this.foundContextBot != null)
      return this.foundContextBot.bot_inline_placeholder;
    if ((this.searchingContextUsername != null) && (this.searchingContextUsername.equals("gif")))
      return "Search GIFs";
    return null;
  }

  public TLRPC.TL_inlineBotSwitchPM getBotContextSwitch()
  {
    return this.searchResultBotContextSwitch;
  }

  public int getContextBotId()
  {
    if (this.foundContextBot != null)
      return this.foundContextBot.id;
    return 0;
  }

  public String getContextBotName()
  {
    if (this.foundContextBot != null)
      return this.foundContextBot.username;
    return "";
  }

  public TLRPC.User getContextBotUser()
  {
    if (this.foundContextBot != null)
      return this.foundContextBot;
    return null;
  }

  public Object getItem(int paramInt)
  {
    Object localObject2 = null;
    int i;
    Object localObject1;
    if (this.searchResultBotContext != null)
    {
      i = paramInt;
      if (this.searchResultBotContextSwitch != null)
        if (paramInt == 0)
          localObject1 = this.searchResultBotContextSwitch;
    }
    do
    {
      do
      {
        do
        {
          while (true)
          {
            return localObject1;
            i = paramInt - 1;
            localObject1 = localObject2;
            if (i < 0)
              continue;
            localObject1 = localObject2;
            if (i < this.searchResultBotContext.size())
            {
              return this.searchResultBotContext.get(i);
              if (this.searchResultUsernames != null)
              {
                localObject1 = localObject2;
                if (paramInt < 0)
                  continue;
                localObject1 = localObject2;
                if (paramInt < this.searchResultUsernames.size())
                  return this.searchResultUsernames.get(paramInt);
              }
              if (this.searchResultHashtags == null)
                break;
              localObject1 = localObject2;
              if (paramInt < 0)
                continue;
              localObject1 = localObject2;
              if (paramInt < this.searchResultHashtags.size())
                return this.searchResultHashtags.get(paramInt);
            }
          }
          localObject1 = localObject2;
        }
        while (this.searchResultCommands == null);
        localObject1 = localObject2;
      }
      while (paramInt < 0);
      localObject1 = localObject2;
    }
    while (paramInt >= this.searchResultCommands.size());
    if ((this.searchResultCommandsUsers != null) && ((this.botsCount != 1) || ((this.info instanceof TLRPC.TL_channelFull))))
    {
      if (this.searchResultCommandsUsers.get(paramInt) != null)
      {
        localObject2 = this.searchResultCommands.get(paramInt);
        if (this.searchResultCommandsUsers.get(paramInt) != null);
        for (localObject1 = ((TLRPC.User)this.searchResultCommandsUsers.get(paramInt)).username; ; localObject1 = "")
          return String.format("%s@%s", new Object[] { localObject2, localObject1 });
      }
      return String.format("%s", new Object[] { this.searchResultCommands.get(paramInt) });
    }
    return this.searchResultCommands.get(paramInt);
  }

  public int getItemCount()
  {
    int j = 0;
    int i = 0;
    if (this.searchResultBotContext != null)
    {
      j = this.searchResultBotContext.size();
      if (this.searchResultBotContextSwitch != null)
        i = 1;
      i += j;
    }
    do
    {
      return i;
      if (this.searchResultUsernames != null)
        return this.searchResultUsernames.size();
      if (this.searchResultHashtags != null)
        return this.searchResultHashtags.size();
      i = j;
    }
    while (this.searchResultCommands == null);
    return this.searchResultCommands.size();
  }

  public int getItemPosition(int paramInt)
  {
    int i = paramInt;
    if (this.searchResultBotContext != null)
    {
      i = paramInt;
      if (this.searchResultBotContextSwitch != null)
        i = paramInt - 1;
    }
    return i;
  }

  public int getItemViewType(int paramInt)
  {
    if (this.searchResultBotContext != null)
    {
      if ((paramInt == 0) && (this.searchResultBotContextSwitch != null))
        return 2;
      return 1;
    }
    return 0;
  }

  public int getResultLength()
  {
    return this.resultLength;
  }

  public int getResultStartPosition()
  {
    return this.resultStartPosition;
  }

  public ArrayList<TLRPC.BotInlineResult> getSearchResultBotContext()
  {
    return this.searchResultBotContext;
  }

  public boolean isBotCommands()
  {
    return this.searchResultCommands != null;
  }

  public boolean isBotContext()
  {
    return this.searchResultBotContext != null;
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return true;
  }

  public boolean isLongClickEnabled()
  {
    return (this.searchResultHashtags != null) || (this.searchResultCommands != null);
  }

  public boolean isMediaLayout()
  {
    return this.contextMedia;
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    boolean bool2 = true;
    int i;
    if (this.searchResultBotContext != null)
      if (this.searchResultBotContextSwitch != null)
      {
        i = 1;
        if (paramViewHolder.getItemViewType() != 2)
          break label54;
        if (i != 0)
          ((BotSwitchCell)paramViewHolder.itemView).setText(this.searchResultBotContextSwitch.text);
      }
    label54: 
    do
    {
      return;
      i = 0;
      break;
      int j = paramInt;
      if (i != 0)
        j = paramInt - 1;
      paramViewHolder = (ContextLinkCell)paramViewHolder.itemView;
      localObject = (TLRPC.BotInlineResult)this.searchResultBotContext.get(j);
      boolean bool3 = this.contextMedia;
      boolean bool1;
      if (j != this.searchResultBotContext.size() - 1)
      {
        bool1 = true;
        if ((i == 0) || (j != 0))
          break label139;
      }
      while (true)
      {
        paramViewHolder.setLink((TLRPC.BotInlineResult)localObject, bool3, bool1, bool2);
        return;
        bool1 = false;
        break;
        bool2 = false;
      }
      if (this.searchResultUsernames != null)
      {
        ((MentionCell)paramViewHolder.itemView).setUser((TLRPC.User)this.searchResultUsernames.get(paramInt));
        return;
      }
      if (this.searchResultHashtags == null)
        continue;
      ((MentionCell)paramViewHolder.itemView).setText((String)this.searchResultHashtags.get(paramInt));
      return;
    }
    while (this.searchResultCommands == null);
    label139: Object localObject = (MentionCell)paramViewHolder.itemView;
    String str1 = (String)this.searchResultCommands.get(paramInt);
    String str2 = (String)this.searchResultCommandsHelp.get(paramInt);
    if (this.searchResultCommandsUsers != null);
    for (paramViewHolder = (TLRPC.User)this.searchResultCommandsUsers.get(paramInt); ; paramViewHolder = null)
    {
      ((MentionCell)localObject).setBotCommand(str1, str2, paramViewHolder);
      return;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    if (paramInt == 1)
    {
      paramViewGroup = new ContextLinkCell(this.mContext);
      ((ContextLinkCell)paramViewGroup).setDelegate(new ContextLinkCell.ContextLinkCellDelegate()
      {
        public void didPressedImage(ContextLinkCell paramContextLinkCell)
        {
          MentionsAdapter.this.delegate.onContextClick(paramContextLinkCell.getResult());
        }
      });
    }
    while (true)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      if (paramInt == 2)
      {
        paramViewGroup = new BotSwitchCell(this.mContext);
        continue;
      }
      paramViewGroup = new MentionCell(this.mContext);
      ((MentionCell)paramViewGroup).setIsDarkTheme(this.isDarkTheme);
    }
  }

  public void onDestroy()
  {
    if (this.locationProvider != null)
      this.locationProvider.stop();
    if (this.contextQueryRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.contextQueryRunnable);
      this.contextQueryRunnable = null;
    }
    if (this.contextUsernameReqid != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.contextUsernameReqid, true);
      this.contextUsernameReqid = 0;
    }
    if (this.contextQueryReqid != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.contextQueryReqid, true);
      this.contextQueryReqid = 0;
    }
    this.foundContextBot = null;
    this.searchingContextUsername = null;
    this.searchingContextQuery = null;
    this.noUserName = false;
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 2) && (this.foundContextBot != null) && (this.foundContextBot.bot_inline_geo))
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
        this.locationProvider.start();
    }
    else
      return;
    onLocationUnavailable();
  }

  public void searchForContextBotForNextOffset()
  {
    if ((this.contextQueryReqid != 0) || (this.nextQueryOffset == null) || (this.nextQueryOffset.length() == 0) || (this.foundContextBot == null) || (this.searchingContextQuery == null))
      return;
    searchForContextBotResults(true, this.foundContextBot, this.searchingContextQuery, this.nextQueryOffset);
  }

  public void searchUsernameOrHashtag(String paramString, int paramInt, ArrayList<MessageObject> paramArrayList)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      searchForContextBot(null, null);
      this.delegate.needChangePanelVisibility(false);
      this.lastText = null;
      return;
    }
    if (paramString.length() > 0);
    for (int i = paramInt - 1; ; i = paramInt)
    {
      this.lastText = null;
      Object localObject3 = new StringBuilder();
      int j;
      int k;
      Object localObject1;
      Object localObject2;
      if ((this.needBotContext) && (paramString.charAt(0) == '@'))
      {
        j = paramString.indexOf(' ');
        k = paramString.length();
        localObject1 = null;
        if (j > 0)
        {
          localObject1 = paramString.substring(1, j);
          localObject2 = paramString.substring(j + 1);
          if ((localObject1 == null) || (((String)localObject1).length() < 1))
            break label326;
          j = 1;
          label134: if (j >= ((String)localObject1).length())
            break label1810;
          k = ((String)localObject1).charAt(j);
          if (((k >= 48) && (k <= 57)) || ((k >= 97) && (k <= 122)) || ((k >= 65) && (k <= 90)) || (k == 95))
            break label317;
          localObject1 = "";
        }
      }
      label1801: label1810: 
      while (true)
      {
        label207: searchForContextBot((String)localObject1, (String)localObject2);
        int m;
        while (true)
        {
          if (this.foundContextBot != null)
            break label341;
          m = -1;
          j = 0;
          while (true)
          {
            if (i < 0)
              break label1801;
            if (i < paramString.length())
              break;
            label242: i -= 1;
          }
          if ((paramString.charAt(k - 1) == 't') && (paramString.charAt(k - 2) == 'o') && (paramString.charAt(k - 3) == 'b'))
          {
            localObject1 = paramString.substring(1);
            localObject2 = "";
            break;
          }
          searchForContextBot(null, null);
          localObject2 = null;
          break;
          label317: j += 1;
          break label134;
          label326: localObject1 = "";
          break label207;
          searchForContextBot(null, null);
        }
        label341: break;
        char c = paramString.charAt(i);
        if ((i == 0) || (paramString.charAt(i - 1) == ' ') || (paramString.charAt(i - 1) == '\n'))
          if (c == '@')
          {
            if ((!this.needUsernames) && ((!this.needBotContext) || (i != 0)))
              break label620;
            if (j != 0)
            {
              this.delegate.needChangePanelVisibility(false);
              return;
            }
            if ((this.info == null) && (i != 0))
            {
              this.lastText = paramString;
              this.lastPosition = paramInt;
              this.messages = paramArrayList;
              this.delegate.needChangePanelVisibility(false);
              return;
            }
            paramInt = 0;
            this.resultStartPosition = i;
            this.resultLength = (((StringBuilder)localObject3).length() + 1);
          }
        while (true)
        {
          if (paramInt == -1)
          {
            this.delegate.needChangePanelVisibility(false);
            return;
            if (c == '#')
            {
              if (this.searchAdapterHelper.loadRecentHashtags())
              {
                paramInt = 1;
                this.resultStartPosition = i;
                this.resultLength = (((StringBuilder)localObject3).length() + 1);
                ((StringBuilder)localObject3).insert(0, c);
                i = m;
                continue;
              }
              this.lastText = paramString;
              this.lastPosition = paramInt;
              this.messages = paramArrayList;
              this.delegate.needChangePanelVisibility(false);
              return;
            }
            if ((i == 0) && (this.botInfo != null) && (c == '/'))
            {
              paramInt = 2;
              this.resultStartPosition = i;
              this.resultLength = (((StringBuilder)localObject3).length() + 1);
              i = m;
              continue;
            }
            label620: if (c >= '0')
            {
              k = j;
              if (c <= '9');
            }
            else if (c >= 'a')
            {
              k = j;
              if (c <= 'z');
            }
            else if (c >= 'A')
            {
              k = j;
              if (c <= 'Z');
            }
            else
            {
              k = j;
              if (c != '_')
                k = 1;
            }
            ((StringBuilder)localObject3).insert(0, c);
            j = k;
            break label242;
          }
          if (paramInt == 0)
          {
            paramString = new ArrayList();
            paramInt = 0;
            while (paramInt < Math.min(100, paramArrayList.size()))
            {
              j = ((MessageObject)paramArrayList.get(paramInt)).messageOwner.from_id;
              if (!paramString.contains(Integer.valueOf(j)))
                paramString.add(Integer.valueOf(j));
              paramInt += 1;
            }
            localObject1 = ((StringBuilder)localObject3).toString().toLowerCase();
            paramArrayList = new ArrayList();
            localObject2 = new HashMap();
            if ((this.needBotContext) && (i == 0) && (!SearchQuery.inlineBots.isEmpty()))
            {
              paramInt = 0;
              j = 0;
              if (j < SearchQuery.inlineBots.size())
              {
                localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.TL_topPeer)SearchQuery.inlineBots.get(j)).peer.user_id));
                if (localObject3 == null);
                do
                {
                  j += 1;
                  break;
                  i = paramInt;
                  if (((TLRPC.User)localObject3).username != null)
                  {
                    i = paramInt;
                    if (((TLRPC.User)localObject3).username.length() > 0)
                      if ((((String)localObject1).length() <= 0) || (!((TLRPC.User)localObject3).username.toLowerCase().startsWith((String)localObject1)))
                      {
                        i = paramInt;
                        if (((String)localObject1).length() != 0);
                      }
                      else
                      {
                        paramArrayList.add(localObject3);
                        ((HashMap)localObject2).put(Integer.valueOf(((TLRPC.User)localObject3).id), localObject3);
                        i = paramInt + 1;
                      }
                  }
                  paramInt = i;
                }
                while (i != 5);
              }
            }
            if ((this.info != null) && (this.info.participants != null))
            {
              paramInt = 0;
              if (paramInt < this.info.participants.participants.size())
              {
                localObject3 = (TLRPC.ChatParticipant)this.info.participants.participants.get(paramInt);
                localObject3 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject3).user_id));
                if ((localObject3 == null) || (UserObject.isUserSelf((TLRPC.User)localObject3)) || (((HashMap)localObject2).containsKey(Integer.valueOf(((TLRPC.User)localObject3).id))));
                while (true)
                {
                  paramInt += 1;
                  break;
                  if (((String)localObject1).length() == 0)
                  {
                    if ((((TLRPC.User)localObject3).deleted) || ((!this.allowNewMentions) && ((this.allowNewMentions) || (((TLRPC.User)localObject3).username == null) || (((TLRPC.User)localObject3).username.length() == 0))))
                      continue;
                    paramArrayList.add(localObject3);
                    continue;
                  }
                  if ((((TLRPC.User)localObject3).username != null) && (((TLRPC.User)localObject3).username.length() > 0) && (((TLRPC.User)localObject3).username.toLowerCase().startsWith((String)localObject1)))
                  {
                    paramArrayList.add(localObject3);
                    continue;
                  }
                  if ((!this.allowNewMentions) && ((((TLRPC.User)localObject3).username == null) || (((TLRPC.User)localObject3).username.length() == 0)))
                    continue;
                  if ((((TLRPC.User)localObject3).first_name != null) && (((TLRPC.User)localObject3).first_name.length() > 0) && (((TLRPC.User)localObject3).first_name.toLowerCase().startsWith((String)localObject1)))
                  {
                    paramArrayList.add(localObject3);
                    continue;
                  }
                  if ((((TLRPC.User)localObject3).last_name == null) || (((TLRPC.User)localObject3).last_name.length() <= 0) || (!((TLRPC.User)localObject3).last_name.toLowerCase().startsWith((String)localObject1)))
                    continue;
                  paramArrayList.add(localObject3);
                }
              }
            }
            this.searchResultHashtags = null;
            this.searchResultCommands = null;
            this.searchResultCommandsHelp = null;
            this.searchResultCommandsUsers = null;
            this.searchResultUsernames = paramArrayList;
            Collections.sort(this.searchResultUsernames, new Comparator((HashMap)localObject2, paramString)
            {
              public int compare(TLRPC.User paramUser1, TLRPC.User paramUser2)
              {
                int j = -1;
                int i;
                if ((this.val$newResultsHashMap.containsKey(Integer.valueOf(paramUser1.id))) && (this.val$newResultsHashMap.containsKey(Integer.valueOf(paramUser2.id))))
                  i = 0;
                int k;
                int m;
                do
                {
                  while (true)
                  {
                    return i;
                    i = j;
                    if (this.val$newResultsHashMap.containsKey(Integer.valueOf(paramUser1.id)))
                      continue;
                    if (this.val$newResultsHashMap.containsKey(Integer.valueOf(paramUser2.id)))
                      return 1;
                    k = this.val$users.indexOf(Integer.valueOf(paramUser1.id));
                    m = this.val$users.indexOf(Integer.valueOf(paramUser2.id));
                    if ((k == -1) || (m == -1))
                      break;
                    i = j;
                    if (k < m)
                      continue;
                    if (k == m)
                      return 0;
                    return 1;
                  }
                  if (k == -1)
                    break;
                  i = j;
                }
                while (m == -1);
                if ((k == -1) && (m != -1))
                  return 1;
                return 0;
              }
            });
            notifyDataSetChanged();
            paramString = this.delegate;
            if (!paramArrayList.isEmpty());
            for (bool = true; ; bool = false)
            {
              paramString.needChangePanelVisibility(bool);
              return;
            }
          }
          if (paramInt == 1)
          {
            paramString = new ArrayList();
            paramArrayList = ((StringBuilder)localObject3).toString().toLowerCase();
            localObject1 = this.searchAdapterHelper.getHashtags();
            paramInt = 0;
            while (paramInt < ((ArrayList)localObject1).size())
            {
              localObject2 = (SearchAdapterHelper.HashtagObject)((ArrayList)localObject1).get(paramInt);
              if ((localObject2 != null) && (((SearchAdapterHelper.HashtagObject)localObject2).hashtag != null) && (((SearchAdapterHelper.HashtagObject)localObject2).hashtag.startsWith(paramArrayList)))
                paramString.add(((SearchAdapterHelper.HashtagObject)localObject2).hashtag);
              paramInt += 1;
            }
            this.searchResultHashtags = paramString;
            this.searchResultUsernames = null;
            this.searchResultCommands = null;
            this.searchResultCommandsHelp = null;
            this.searchResultCommandsUsers = null;
            notifyDataSetChanged();
            paramArrayList = this.delegate;
            if (!paramString.isEmpty());
            for (bool = true; ; bool = false)
            {
              paramArrayList.needChangePanelVisibility(bool);
              return;
            }
          }
          if (paramInt != 2)
            break;
          paramString = new ArrayList();
          paramArrayList = new ArrayList();
          localObject1 = new ArrayList();
          localObject2 = ((StringBuilder)localObject3).toString().toLowerCase();
          localObject3 = this.botInfo.entrySet().iterator();
          while (((Iterator)localObject3).hasNext())
          {
            TLRPC.BotInfo localBotInfo = (TLRPC.BotInfo)((Map.Entry)((Iterator)localObject3).next()).getValue();
            paramInt = 0;
            while (paramInt < localBotInfo.commands.size())
            {
              TLRPC.TL_botCommand localTL_botCommand = (TLRPC.TL_botCommand)localBotInfo.commands.get(paramInt);
              if ((localTL_botCommand != null) && (localTL_botCommand.command != null) && (localTL_botCommand.command.startsWith((String)localObject2)))
              {
                paramString.add("/" + localTL_botCommand.command);
                paramArrayList.add(localTL_botCommand.description);
                ((ArrayList)localObject1).add(MessagesController.getInstance().getUser(Integer.valueOf(localBotInfo.user_id)));
              }
              paramInt += 1;
            }
          }
          this.searchResultHashtags = null;
          this.searchResultUsernames = null;
          this.searchResultCommands = paramString;
          this.searchResultCommandsHelp = paramArrayList;
          this.searchResultCommandsUsers = ((ArrayList)localObject1);
          notifyDataSetChanged();
          paramArrayList = this.delegate;
          if (!paramString.isEmpty());
          for (boolean bool = true; ; bool = false)
          {
            paramArrayList.needChangePanelVisibility(bool);
            return;
          }
          paramInt = -1;
          i = m;
        }
      }
    }
  }

  public void setAllowNewMentions(boolean paramBoolean)
  {
    this.allowNewMentions = paramBoolean;
  }

  public void setBotInfo(HashMap<Integer, TLRPC.BotInfo> paramHashMap)
  {
    this.botInfo = paramHashMap;
  }

  public void setBotsCount(int paramInt)
  {
    this.botsCount = paramInt;
  }

  public void setChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
    if (this.lastText != null)
      searchUsernameOrHashtag(this.lastText, this.lastPosition, this.messages);
  }

  public void setNeedBotContext(boolean paramBoolean)
  {
    this.needBotContext = paramBoolean;
  }

  public void setNeedUsernames(boolean paramBoolean)
  {
    this.needUsernames = paramBoolean;
  }

  public void setParentFragment(BaseFragment paramBaseFragment)
  {
    this.parentFragment = paramBaseFragment;
  }

  public static abstract interface MentionsAdapterDelegate
  {
    public abstract void needChangePanelVisibility(boolean paramBoolean);

    public abstract void onContextClick(TLRPC.BotInlineResult paramBotInlineResult);

    public abstract void onContextSearch(boolean paramBoolean);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.MentionsAdapter
 * JD-Core Version:    0.6.0
 */