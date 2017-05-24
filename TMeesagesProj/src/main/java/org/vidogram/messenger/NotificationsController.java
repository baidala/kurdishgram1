package org.vidogram.messenger;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.provider.Settings.System;
import android.support.v4.b.aa;
import android.support.v4.b.ad;
import android.support.v4.b.ad.a;
import android.support.v4.b.r.a;
import android.support.v4.b.r.a.a;
import android.support.v4.b.r.c;
import android.support.v4.b.r.d;
import android.support.v4.b.r.f;
import android.support.v4.b.r.f.a.a;
import android.support.v4.b.r.g;
import android.support.v4.b.r.h;
import android.support.v4.b.r.i;
import android.support.v4.b.r.t;
import android.text.TextUtils;
import android.util.SparseArray;
import itman.Vidofilm.b;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_account_updateNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_game;
import org.vidogram.tgnet.TLRPC.TL_inputNotifyPeer;
import org.vidogram.tgnet.TLRPC.TL_inputPeerNotifySettings;
import org.vidogram.tgnet.TLRPC.TL_messageActionChannelCreate;
import org.vidogram.tgnet.TLRPC.TL_messageActionChannelMigrateFrom;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatAddUser;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatCreate;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatDeletePhoto;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatDeleteUser;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatEditPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatEditTitle;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatJoinedByLink;
import org.vidogram.tgnet.TLRPC.TL_messageActionChatMigrateTo;
import org.vidogram.tgnet.TLRPC.TL_messageActionEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageActionGameScore;
import org.vidogram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.vidogram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.vidogram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaContact;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGame;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.LaunchActivity;
import org.vidogram.ui.PopupNotificationActivity;

public class NotificationsController
{
  public static final String EXTRA_VOICE_REPLY = "extra_voice_reply";
  private static volatile NotificationsController Instance = null;
  private AlarmManager alarmManager;
  protected AudioManager audioManager;
  private ArrayList<MessageObject> delayedPushMessages = new ArrayList();
  private boolean inChatSoundEnabled = true;
  private int lastBadgeCount = -1;
  private int lastOnlineFromOtherDevice = 0;
  private long lastSoundOutPlay;
  private long lastSoundPlay;
  private String launcherClassName;
  private Runnable notificationDelayRunnable;
  private PowerManager.WakeLock notificationDelayWakelock;
  private aa notificationManager = null;
  private DispatchQueue notificationsQueue = new DispatchQueue("notificationsQueue");
  private boolean notifyCheck = false;
  private long opened_dialog_id = 0L;
  private int personal_count = 0;
  public ArrayList<MessageObject> popupMessages = new ArrayList();
  public ArrayList<MessageObject> popupReplyMessages = new ArrayList();
  private HashMap<Long, Integer> pushDialogs = new HashMap();
  private HashMap<Long, Integer> pushDialogsOverrideMention = new HashMap();
  private ArrayList<MessageObject> pushMessages = new ArrayList();
  private HashMap<Long, MessageObject> pushMessagesDict = new HashMap();
  private HashMap<Long, Point> smartNotificationsDialogs = new HashMap();
  private int soundIn;
  private boolean soundInLoaded;
  private int soundOut;
  private boolean soundOutLoaded;
  private SoundPool soundPool;
  private int soundRecord;
  private boolean soundRecordLoaded;
  private int total_unread_count = 0;
  private HashMap<Long, Integer> wearNotificationsIds = new HashMap();

  public NotificationsController()
  {
    try
    {
      this.audioManager = ((AudioManager)ApplicationLoader.applicationContext.getSystemService("audio"));
    }
    catch (Exception localException3)
    {
      try
      {
        this.alarmManager = ((AlarmManager)ApplicationLoader.applicationContext.getSystemService("alarm"));
      }
      catch (Exception localException3)
      {
        try
        {
          while (true)
          {
            this.notificationDelayWakelock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(1, "lock");
            this.notificationDelayWakelock.setReferenceCounted(false);
            this.notificationDelayRunnable = new Runnable()
            {
              public void run()
              {
                FileLog.e("delay reached");
                if (!NotificationsController.this.delayedPushMessages.isEmpty())
                {
                  NotificationsController.this.showOrUpdateNotification(true);
                  NotificationsController.this.delayedPushMessages.clear();
                }
                try
                {
                  if (NotificationsController.this.notificationDelayWakelock.isHeld())
                    NotificationsController.this.notificationDelayWakelock.release();
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e(localException);
                }
              }
            };
            return;
            localException1 = localException1;
            FileLog.e(localException1);
            continue;
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
        catch (Exception localException3)
        {
          while (true)
            FileLog.e(localException3);
        }
      }
    }
  }

  private void dismissNotification()
  {
    try
    {
      this.notificationManager.a(1);
      this.pushMessages.clear();
      this.pushMessagesDict.clear();
      Iterator localIterator = this.wearNotificationsIds.entrySet().iterator();
      while (localIterator.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)localIterator.next();
        this.notificationManager.a(((Integer)localEntry.getValue()).intValue());
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      return;
    }
    this.wearNotificationsIds.clear();
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
      }
    });
  }

  public static NotificationsController getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        NotificationsController localNotificationsController = Instance;
        localObject1 = localNotificationsController;
        if (localNotificationsController == null)
        {
          localObject1 = new NotificationsController();
          Instance = (NotificationsController)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (NotificationsController)localObject2;
  }

  private int getNotifyOverride(SharedPreferences paramSharedPreferences, long paramLong)
  {
    int j = paramSharedPreferences.getInt("notify2_" + paramLong, 0);
    int i = j;
    if (j == 3)
    {
      i = j;
      if (paramSharedPreferences.getInt("notifyuntil_" + paramLong, 0) >= ConnectionsManager.getInstance().getCurrentTime())
        i = 2;
    }
    return i;
  }

  private String getStringForMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    long l = paramMessageObject.messageOwner.dialog_id;
    int j;
    int i;
    if (paramMessageObject.messageOwner.to_id.chat_id != 0)
    {
      j = paramMessageObject.messageOwner.to_id.chat_id;
      i = paramMessageObject.messageOwner.to_id.user_id;
      if (i != 0)
        break label149;
      if ((!paramMessageObject.isFromUser()) && (paramMessageObject.getId() >= 0))
        break label142;
      i = paramMessageObject.messageOwner.from_id;
    }
    label71: label89: label6491: label6494: 
    while (true)
    {
      if (l == 0L)
        if (j != 0)
          l = -j;
      while (true)
      {
        Object localObject1 = null;
        Object localObject2;
        if (i > 0)
        {
          localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
          if (localObject2 != null)
            localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
        }
        while (true)
        {
          if (localObject1 == null)
          {
            return null;
            j = paramMessageObject.messageOwner.to_id.channel_id;
            break;
            i = -j;
            break label71;
            if (i != UserConfig.getClientUserId())
              break label6494;
            i = paramMessageObject.messageOwner.from_id;
            break label71;
            if (i == 0)
              break label6491;
            l = i;
            break label89;
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
            if (localObject1 != null)
            {
              localObject1 = ((TLRPC.Chat)localObject1).title;
              continue;
            }
          }
          else
          {
            if (j != 0)
            {
              localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(j));
              if (localObject2 == null)
                return null;
            }
            while (true)
            {
              if (((int)l == 0) || (AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter))
                return LocaleController.getString("YouHaveNewMessage", 2131166644);
              if ((j == 0) && (i != 0))
              {
                if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewAll", true))
                {
                  if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
                  {
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserJoined))
                      return LocaleController.formatString("NotificationContactJoined", 2131166088, new Object[] { localObject1 });
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
                      return LocaleController.formatString("NotificationContactNewPhoto", 2131166089, new Object[] { localObject1 });
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
                    {
                      localObject1 = LocaleController.formatString("formatDateAtTime", 2131166662, new Object[] { LocaleController.getInstance().formatterYear.format(paramMessageObject.messageOwner.date * 1000L), LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L) });
                      return LocaleController.formatString("NotificationUnrecognizedDevice", 2131166127, new Object[] { UserConfig.getCurrentUser().first_name, localObject1, paramMessageObject.messageOwner.action.title, paramMessageObject.messageOwner.action.address });
                    }
                    if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPaymentSent)))
                      return paramMessageObject.messageText.toString();
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall))
                    {
                      localObject1 = paramMessageObject.messageOwner.action.reason;
                      if ((!paramMessageObject.isOut()) && ((localObject1 instanceof TLRPC.TL_phoneCallDiscardReasonMissed)))
                        return LocaleController.getString("CallMessageIncomingMissed", 2131165416);
                    }
                  }
                  else
                  {
                    if (paramMessageObject.isMediaEmpty())
                    {
                      if (!paramBoolean)
                      {
                        if ((paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, paramMessageObject.messageOwner.message });
                        return LocaleController.formatString("NotificationMessageNoText", 2131166120, new Object[] { localObject1 });
                      }
                      return LocaleController.formatString("NotificationMessageNoText", 2131166120, new Object[] { localObject1 });
                    }
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                    {
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "🖼 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessagePhoto", 2131166121, new Object[] { localObject1 });
                    }
                    if (paramMessageObject.isVideo())
                    {
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "📹 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessageVideo", 2131166125, new Object[] { localObject1 });
                    }
                    if (paramMessageObject.isGame())
                      return LocaleController.formatString("NotificationMessageGame", 2131166103, new Object[] { localObject1, paramMessageObject.messageOwner.media.game.title });
                    if (paramMessageObject.isVoice())
                      return LocaleController.formatString("NotificationMessageAudio", 2131166100, new Object[] { localObject1 });
                    if (paramMessageObject.isMusic())
                      return LocaleController.formatString("NotificationMessageMusic", 2131166119, new Object[] { localObject1 });
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                      return LocaleController.formatString("NotificationMessageContact", 2131166101, new Object[] { localObject1 });
                    if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                      return LocaleController.formatString("NotificationMessageMap", 2131166118, new Object[] { localObject1 });
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                    {
                      if (paramMessageObject.isSticker())
                      {
                        paramMessageObject = paramMessageObject.getStickerEmoji();
                        if (paramMessageObject != null)
                          return LocaleController.formatString("NotificationMessageStickerEmoji", 2131166123, new Object[] { localObject1, paramMessageObject });
                        return LocaleController.formatString("NotificationMessageSticker", 2131166122, new Object[] { localObject1 });
                      }
                      if (paramMessageObject.isGif())
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "🎬 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("NotificationMessageGif", 2131166104, new Object[] { localObject1 });
                      }
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "📎 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessageDocument", 2131166102, new Object[] { localObject1 });
                    }
                  }
                }
                else
                  return LocaleController.formatString("NotificationMessageNoText", 2131166120, new Object[] { localObject1 });
              }
              else if (j != 0)
                if (ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("EnablePreviewGroup", true))
                {
                  if ((paramMessageObject.messageOwner instanceof TLRPC.TL_messageService))
                  {
                    Object localObject3;
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatAddUser))
                    {
                      int k = paramMessageObject.messageOwner.action.user_id;
                      j = k;
                      if (k == 0)
                      {
                        j = k;
                        if (paramMessageObject.messageOwner.action.users.size() == 1)
                          j = ((Integer)paramMessageObject.messageOwner.action.users.get(0)).intValue();
                      }
                      if (j != 0)
                      {
                        if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("ChannelAddedByNotification", 2131165448, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        if (j == UserConfig.getClientUserId())
                          return LocaleController.formatString("NotificationInvitedToGroup", 2131166098, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(j));
                        if (paramMessageObject == null)
                          return null;
                        if (i == paramMessageObject.id)
                        {
                          if (((TLRPC.Chat)localObject2).megagroup)
                            return LocaleController.formatString("NotificationGroupAddSelfMega", 2131166094, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                          return LocaleController.formatString("NotificationGroupAddSelf", 2131166093, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        }
                        return LocaleController.formatString("NotificationGroupAddMember", 2131166092, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, UserObject.getUserName(paramMessageObject) });
                      }
                      localObject3 = new StringBuilder("");
                      i = 0;
                      while (i < paramMessageObject.messageOwner.action.users.size())
                      {
                        Object localObject4 = MessagesController.getInstance().getUser((Integer)paramMessageObject.messageOwner.action.users.get(i));
                        if (localObject4 != null)
                        {
                          localObject4 = UserObject.getUserName((TLRPC.User)localObject4);
                          if (((StringBuilder)localObject3).length() != 0)
                            ((StringBuilder)localObject3).append(", ");
                          ((StringBuilder)localObject3).append((String)localObject4);
                        }
                        i += 1;
                      }
                      return LocaleController.formatString("NotificationGroupAddMember", 2131166092, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, ((StringBuilder)localObject3).toString() });
                    }
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
                      return LocaleController.formatString("NotificationInvitedToGroupByLink", 2131166099, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditTitle))
                      return LocaleController.formatString("NotificationEditedGroupName", 2131166090, new Object[] { localObject1, paramMessageObject.messageOwner.action.title });
                    if (((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeletePhoto)))
                    {
                      if ((paramMessageObject.messageOwner.to_id.channel_id != 0) && (!((TLRPC.Chat)localObject2).megagroup))
                        return LocaleController.formatString("ChannelPhotoEditNotification", 2131165505, new Object[] { ((TLRPC.Chat)localObject2).title });
                      return LocaleController.formatString("NotificationEditedGroupPhoto", 2131166091, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    }
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatDeleteUser))
                    {
                      if (paramMessageObject.messageOwner.action.user_id == UserConfig.getClientUserId())
                        return LocaleController.formatString("NotificationGroupKickYou", 2131166096, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      if (paramMessageObject.messageOwner.action.user_id == i)
                        return LocaleController.formatString("NotificationGroupLeftMember", 2131166097, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.action.user_id));
                      if (paramMessageObject == null)
                        return null;
                      return LocaleController.formatString("NotificationGroupKickMember", 2131166095, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, UserObject.getUserName(paramMessageObject) });
                    }
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatCreate))
                      return paramMessageObject.messageText.toString();
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelCreate))
                      return paramMessageObject.messageText.toString();
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChatMigrateTo))
                      return LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165246, new Object[] { ((TLRPC.Chat)localObject2).title });
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
                      return LocaleController.formatString("ActionMigrateFromGroupNotify", 2131165246, new Object[] { paramMessageObject.messageOwner.action.title });
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))
                    {
                      if (paramMessageObject.replyMessageObject == null)
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedNoText", 2131166074, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131166075, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      localObject3 = paramMessageObject.replyMessageObject;
                      if (((MessageObject)localObject3).isMusic())
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedMusic", 2131166072, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedMusicChannel", 2131166073, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if (((MessageObject)localObject3).isVideo())
                      {
                        if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject3).messageOwner.media.caption)))
                        {
                          paramMessageObject = "📹 " + ((MessageObject)localObject3).messageOwner.media.caption;
                          if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                            return LocaleController.formatString("NotificationActionPinnedText", 2131166082, new Object[] { localObject1, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                          return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166083, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        }
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedVideo", 2131166084, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedVideoChannel", 2131166085, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if (((MessageObject)localObject3).isGif())
                      {
                        if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject3).messageOwner.media.caption)))
                        {
                          paramMessageObject = "🎬 " + ((MessageObject)localObject3).messageOwner.media.caption;
                          if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                            return LocaleController.formatString("NotificationActionPinnedText", 2131166082, new Object[] { localObject1, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                          return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166083, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        }
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedGif", 2131166070, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedGifChannel", 2131166071, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if (((MessageObject)localObject3).isVoice())
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedVoice", 2131166086, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedVoiceChannel", 2131166087, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if (((MessageObject)localObject3).isSticker())
                      {
                        paramMessageObject = paramMessageObject.getStickerEmoji();
                        if (paramMessageObject != null)
                        {
                          if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                            return LocaleController.formatString("NotificationActionPinnedStickerEmoji", 2131166080, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject });
                          return LocaleController.formatString("NotificationActionPinnedStickerEmojiChannel", 2131166081, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        }
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedSticker", 2131166078, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedStickerChannel", 2131166079, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                      {
                        if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject3).messageOwner.media.caption)))
                        {
                          paramMessageObject = "📎 " + ((MessageObject)localObject3).messageOwner.media.caption;
                          if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                            return LocaleController.formatString("NotificationActionPinnedText", 2131166082, new Object[] { localObject1, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                          return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166083, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        }
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedFile", 2131166064, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedFileChannel", 2131166065, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedGeo", 2131166068, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedGeoChannel", 2131166069, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedContact", 2131166062, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedContactChannel", 2131166063, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                      {
                        if ((Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(((MessageObject)localObject3).messageOwner.media.caption)))
                        {
                          paramMessageObject = "🖼 " + ((MessageObject)localObject3).messageOwner.media.caption;
                          if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                            return LocaleController.formatString("NotificationActionPinnedText", 2131166082, new Object[] { localObject1, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                          return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166083, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        }
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedPhoto", 2131166076, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedPhotoChannel", 2131166077, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                      {
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedGame", 2131166066, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedGameChannel", 2131166067, new Object[] { ((TLRPC.Chat)localObject2).title });
                      }
                      if ((((MessageObject)localObject3).messageText != null) && (((MessageObject)localObject3).messageText.length() > 0))
                      {
                        localObject3 = ((MessageObject)localObject3).messageText;
                        paramMessageObject = (MessageObject)localObject3;
                        if (((CharSequence)localObject3).length() > 20)
                          paramMessageObject = ((CharSequence)localObject3).subSequence(0, 20) + "...";
                        if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                          return LocaleController.formatString("NotificationActionPinnedText", 2131166082, new Object[] { localObject1, paramMessageObject, ((TLRPC.Chat)localObject2).title });
                        return LocaleController.formatString("NotificationActionPinnedTextChannel", 2131166083, new Object[] { ((TLRPC.Chat)localObject2).title, paramMessageObject });
                      }
                      if ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).megagroup))
                        return LocaleController.formatString("NotificationActionPinnedNoText", 2131166074, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      return LocaleController.formatString("NotificationActionPinnedNoTextChannel", 2131166075, new Object[] { ((TLRPC.Chat)localObject2).title });
                    }
                    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionGameScore))
                      return paramMessageObject.messageText.toString();
                  }
                  else if ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup))
                  {
                    if (paramMessageObject.messageOwner.post)
                    {
                      if (paramMessageObject.isMediaEmpty())
                      {
                        if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, paramMessageObject.messageOwner.message });
                        return LocaleController.formatString("ChannelMessageNoText", 2131165496, new Object[] { localObject1 });
                      }
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "🖼 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessagePhoto", 2131165497, new Object[] { localObject1 });
                      }
                      if (paramMessageObject.isVideo())
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "📹 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessageVideo", 2131165500, new Object[] { localObject1 });
                      }
                      if (paramMessageObject.isVoice())
                        return LocaleController.formatString("ChannelMessageAudio", 2131165479, new Object[] { localObject1 });
                      if (paramMessageObject.isMusic())
                        return LocaleController.formatString("ChannelMessageMusic", 2131165495, new Object[] { localObject1 });
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                        return LocaleController.formatString("ChannelMessageContact", 2131165480, new Object[] { localObject1 });
                      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                        return LocaleController.formatString("ChannelMessageMap", 2131165494, new Object[] { localObject1 });
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                      {
                        if (paramMessageObject.isSticker())
                        {
                          paramMessageObject = paramMessageObject.getStickerEmoji();
                          if (paramMessageObject != null)
                            return LocaleController.formatString("ChannelMessageStickerEmoji", 2131165499, new Object[] { localObject1, paramMessageObject });
                          return LocaleController.formatString("ChannelMessageSticker", 2131165498, new Object[] { localObject1 });
                        }
                        if (paramMessageObject.isGif())
                        {
                          if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                            return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "🎬 " + paramMessageObject.messageOwner.media.caption });
                          return LocaleController.formatString("ChannelMessageGIF", 2131165482, new Object[] { localObject1 });
                        }
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageText", 2131166124, new Object[] { localObject1, "📎 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessageDocument", 2131165481, new Object[] { localObject1 });
                      }
                    }
                    else
                    {
                      if (paramMessageObject.isMediaEmpty())
                      {
                        if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
                          return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject.messageOwner.message });
                        return LocaleController.formatString("ChannelMessageGroupNoText", 2131165489, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "🖼 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessageGroupPhoto", 2131165490, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      if (paramMessageObject.isVideo())
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "📹 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessageGroupVideo", 2131165493, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      if (paramMessageObject.isVoice())
                        return LocaleController.formatString("ChannelMessageGroupAudio", 2131165483, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      if (paramMessageObject.isMusic())
                        return LocaleController.formatString("ChannelMessageGroupMusic", 2131165488, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                        return LocaleController.formatString("ChannelMessageGroupContact", 2131165484, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                        return LocaleController.formatString("ChannelMessageGroupMap", 2131165487, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                      {
                        if (paramMessageObject.isSticker())
                        {
                          paramMessageObject = paramMessageObject.getStickerEmoji();
                          if (paramMessageObject != null)
                            return LocaleController.formatString("ChannelMessageGroupStickerEmoji", 2131165492, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject });
                          return LocaleController.formatString("ChannelMessageGroupSticker", 2131165491, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        }
                        if (paramMessageObject.isGif())
                        {
                          if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                            return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "🎬 " + paramMessageObject.messageOwner.media.caption });
                          return LocaleController.formatString("ChannelMessageGroupGif", 2131165486, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                        }
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "📎 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("ChannelMessageGroupDocument", 2131165485, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                    }
                  }
                  else
                  {
                    if (paramMessageObject.isMediaEmpty())
                    {
                      if ((!paramBoolean) && (paramMessageObject.messageOwner.message != null) && (paramMessageObject.messageOwner.message.length() != 0))
                        return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject.messageOwner.message });
                      return LocaleController.formatString("NotificationMessageGroupNoText", 2131166112, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    }
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
                    {
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "🖼 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessageGroupPhoto", 2131166113, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    }
                    if (paramMessageObject.isVideo())
                    {
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "📹 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessageGroupVideo", 2131166117, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    }
                    if (paramMessageObject.isVoice())
                      return LocaleController.formatString("NotificationMessageGroupAudio", 2131166105, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    if (paramMessageObject.isMusic())
                      return LocaleController.formatString("NotificationMessageGroupMusic", 2131166111, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
                      return LocaleController.formatString("NotificationMessageGroupContact", 2131166106, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                      return LocaleController.formatString("NotificationMessageGroupGame", 2131166108, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject.messageOwner.media.game.title });
                    if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
                      return LocaleController.formatString("NotificationMessageGroupMap", 2131166110, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
                    {
                      if (paramMessageObject.isSticker())
                      {
                        paramMessageObject = paramMessageObject.getStickerEmoji();
                        if (paramMessageObject != null)
                          return LocaleController.formatString("NotificationMessageGroupStickerEmoji", 2131166115, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, paramMessageObject });
                        return LocaleController.formatString("NotificationMessageGroupSticker", 2131166114, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      if (paramMessageObject.isGif())
                      {
                        if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                          return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "🎬 " + paramMessageObject.messageOwner.media.caption });
                        return LocaleController.formatString("NotificationMessageGroupGif", 2131166109, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                      }
                      if ((!paramBoolean) && (Build.VERSION.SDK_INT >= 19) && (!TextUtils.isEmpty(paramMessageObject.messageOwner.media.caption)))
                        return LocaleController.formatString("NotificationMessageGroupText", 2131166116, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title, "📎 " + paramMessageObject.messageOwner.media.caption });
                      return LocaleController.formatString("NotificationMessageGroupDocument", 2131166107, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                    }
                  }
                }
                else
                {
                  if ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup))
                    return LocaleController.formatString("ChannelMessageNoText", 2131165496, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                  return LocaleController.formatString("NotificationMessageGroupNoText", 2131166112, new Object[] { localObject1, ((TLRPC.Chat)localObject2).title });
                }
              return null;
              localObject2 = null;
            }
          }
          localObject1 = null;
        }
      }
    }
  }

  private boolean isPersonalMessage(MessageObject paramMessageObject)
  {
    return (paramMessageObject.messageOwner.to_id != null) && (paramMessageObject.messageOwner.to_id.chat_id == 0) && (paramMessageObject.messageOwner.to_id.channel_id == 0) && ((paramMessageObject.messageOwner.action == null) || ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionEmpty)));
  }

  private void playInChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio()));
    while (true)
    {
      return;
      try
      {
        int i = this.audioManager.getRingerMode();
        if (i == 0)
          continue;
        try
        {
          if (getNotifyOverride(ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0), this.opened_dialog_id) == 2)
            continue;
          this.notificationsQueue.postRunnable(new Runnable()
          {
            public void run()
            {
              if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundPlay) <= 500L);
              while (true)
              {
                return;
                try
                {
                  if (NotificationsController.this.soundPool == null)
                  {
                    NotificationsController.access$2202(NotificationsController.this, new SoundPool(3, 1, 0));
                    NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
                    {
                      public void onLoadComplete(SoundPool paramSoundPool, int paramInt1, int paramInt2)
                      {
                        if (paramInt2 == 0);
                        try
                        {
                          paramSoundPool.play(paramInt1, 1.0F, 1.0F, 1, 0, 1.0F);
                          return;
                        }
                        catch (Exception paramSoundPool)
                        {
                          FileLog.e(paramSoundPool);
                        }
                      }
                    });
                  }
                  if ((NotificationsController.this.soundIn == 0) && (!NotificationsController.this.soundInLoaded))
                  {
                    NotificationsController.access$2402(NotificationsController.this, true);
                    NotificationsController.access$2302(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099648, 1));
                  }
                  int i = NotificationsController.this.soundIn;
                  if (i == 0)
                    continue;
                  try
                  {
                    NotificationsController.this.soundPool.play(NotificationsController.this.soundIn, 1.0F, 1.0F, 1, 0, 1.0F);
                    return;
                  }
                  catch (Exception localException1)
                  {
                    FileLog.e(localException1);
                    return;
                  }
                }
                catch (Exception localException2)
                {
                  FileLog.e(localException2);
                }
              }
            }
          });
          return;
        }
        catch (Exception localException1)
        {
          FileLog.e(localException1);
          return;
        }
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  private void scheduleNotificationDelay(boolean paramBoolean)
  {
    try
    {
      FileLog.e("delay notification start, onlineReason = " + paramBoolean);
      this.notificationDelayWakelock.acquire(10000L);
      AndroidUtilities.cancelRunOnUIThread(this.notificationDelayRunnable);
      Runnable localRunnable = this.notificationDelayRunnable;
      if (paramBoolean);
      int j;
      for (int i = 3000; ; j = 1000)
      {
        AndroidUtilities.runOnUIThread(localRunnable, i);
        return;
      }
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
      showOrUpdateNotification(this.notifyCheck);
    }
  }

  private void scheduleNotificationRepeat()
  {
    try
    {
      PendingIntent localPendingIntent = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, NotificationRepeat.class), 0);
      int i = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getInt("repeat_messages", 60);
      if ((i > 0) && (this.personal_count > 0))
      {
        this.alarmManager.set(2, SystemClock.elapsedRealtime() + i * 60 * 1000, localPendingIntent);
        return;
      }
      this.alarmManager.cancel(localPendingIntent);
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  private void setBadge(int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        if (NotificationsController.this.lastBadgeCount == this.val$count)
          return;
        int i = b.a(ApplicationLoader.applicationContext).o();
        NotificationsController.access$1102(NotificationsController.this, this.val$count);
        NotificationBadge.applyCount(i + this.val$count);
      }
    });
  }

  @SuppressLint({"InlinedApi"})
  private void showExtraNotifications(r.d paramd, boolean paramBoolean)
  {
    if (Build.VERSION.SDK_INT < 18)
      return;
    ArrayList localArrayList1 = new ArrayList();
    HashMap localHashMap1 = new HashMap();
    int i = 0;
    Object localObject3;
    long l;
    Object localObject2;
    Object localObject1;
    if (i < this.pushMessages.size())
    {
      localObject3 = (MessageObject)this.pushMessages.get(i);
      l = ((MessageObject)localObject3).getDialogId();
      if ((int)l == 0);
      while (true)
      {
        i += 1;
        break;
        localObject2 = (ArrayList)localHashMap1.get(Long.valueOf(l));
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject1 = new ArrayList();
          localHashMap1.put(Long.valueOf(l), localObject1);
          localArrayList1.add(0, Long.valueOf(l));
        }
        ((ArrayList)localObject1).add(localObject3);
      }
    }
    HashMap localHashMap2 = new HashMap();
    localHashMap2.putAll(this.wearNotificationsIds);
    this.wearNotificationsIds.clear();
    i = 0;
    ArrayList localArrayList2;
    int k;
    int j;
    TLRPC.User localUser;
    TLRPC.Chat localChat;
    if (i < localArrayList1.size())
    {
      l = ((Long)localArrayList1.get(i)).longValue();
      localArrayList2 = (ArrayList)localHashMap1.get(Long.valueOf(l));
      k = ((MessageObject)localArrayList2.get(0)).getId();
      j = ((MessageObject)localArrayList2.get(0)).messageOwner.date;
      if (l > 0L)
      {
        localUser = MessagesController.getInstance().getUser(Integer.valueOf((int)l));
        if (localUser != null)
          break label1912;
      }
      do
      {
        i += 1;
        break;
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(-(int)l));
      }
      while (localChat == null);
      localUser = null;
    }
    while (true)
    {
      if ((AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter))
      {
        localObject2 = LocaleController.getString("AppName", 2131165319);
        localObject1 = null;
      }
      while (true)
      {
        Integer localInteger = (Integer)localHashMap2.get(Long.valueOf(l));
        label358: r.f.a.a locala;
        Object localObject5;
        if (localInteger == null)
        {
          localInteger = Integer.valueOf((int)l);
          locala = new r.f.a.a((String)localObject2).a(j * 1000L);
          localObject3 = new Intent();
          ((Intent)localObject3).addFlags(32);
          ((Intent)localObject3).setAction("org.telegram.messenger.ACTION_MESSAGE_HEARD");
          ((Intent)localObject3).putExtra("dialog_id", l);
          ((Intent)localObject3).putExtra("max_id", k);
          locala.a(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject3, 134217728));
          if (((ChatObject.isChannel(localChat)) && ((localChat == null) || (!localChat.megagroup))) || (AndroidUtilities.needShowPasscode(false)) || (UserConfig.isWaitingForPasscodeEnter))
            break label1896;
          localObject3 = new Intent();
          ((Intent)localObject3).addFlags(32);
          ((Intent)localObject3).setAction("org.telegram.messenger.ACTION_MESSAGE_REPLY");
          ((Intent)localObject3).putExtra("dialog_id", l);
          ((Intent)localObject3).putExtra("max_id", k);
          locala.a(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject3, 134217728), new ad.a("extra_voice_reply").a(LocaleController.getString("Reply", 2131166324)).a());
          localObject3 = new Intent(ApplicationLoader.applicationContext, WearReplyReceiver.class);
          ((Intent)localObject3).putExtra("dialog_id", l);
          ((Intent)localObject3).putExtra("max_id", k);
          localObject4 = PendingIntent.getBroadcast(ApplicationLoader.applicationContext, localInteger.intValue(), (Intent)localObject3, 134217728);
          localObject5 = new ad.a("extra_voice_reply").a(LocaleController.getString("Reply", 2131166324)).a();
          if (localChat == null)
            break label1028;
          localObject3 = LocaleController.formatString("ReplyToGroup", 2131166325, new Object[] { localObject2 });
        }
        label683: label944: label1238: label1761: label1896: for (Object localObject4 = new r.a.a(2130837834, (CharSequence)localObject3, (PendingIntent)localObject4).a(true).a((ad)localObject5).a(); ; localObject4 = null)
        {
          localObject5 = (Integer)this.pushDialogs.get(Long.valueOf(l));
          localObject3 = localObject5;
          if (localObject5 == null)
            localObject3 = Integer.valueOf(0);
          r.i locali = new r.i(null).a(String.format("%1$s (%2$s)", new Object[] { localObject2, LocaleController.formatPluralString("NewMessages", Math.max(((Integer)localObject3).intValue(), localArrayList2.size())) }));
          localObject3 = "";
          j = localArrayList2.size() - 1;
          Object localObject7;
          while (true)
          {
            if (j < 0)
              break label1238;
            localObject7 = (MessageObject)localArrayList2.get(j);
            localObject5 = getStringForMessage((MessageObject)localObject7, false);
            if (localObject5 == null)
            {
              j -= 1;
              continue;
              if (localChat != null);
              for (localObject1 = localChat.title; ; localObject1 = UserObject.getUserName(localUser))
              {
                if (localChat == null)
                  break label944;
                if ((localChat.photo == null) || (localChat.photo.photo_small == null) || (localChat.photo.photo_small.volume_id == 0L) || (localChat.photo.photo_small.local_id == 0))
                  break label1902;
                localObject3 = localChat.photo.photo_small;
                localObject2 = localObject1;
                localObject1 = localObject3;
                break;
              }
              if ((localUser.photo == null) || (localUser.photo.photo_small == null) || (localUser.photo.photo_small.volume_id == 0L) || (localUser.photo.photo_small.local_id == 0))
                break label1902;
              localObject3 = localUser.photo.photo_small;
              localObject2 = localObject1;
              localObject1 = localObject3;
              break;
              localHashMap2.remove(Long.valueOf(l));
              break label358;
              localObject3 = LocaleController.formatString("ReplyToUser", 2131166326, new Object[] { localObject2 });
              break label683;
            }
          }
          if (localChat != null);
          Object localObject6;
          for (localObject5 = ((String)localObject5).replace(" @ " + (String)localObject2, ""); ; localObject5 = ((String)localObject5).replace((String)localObject2 + ": ", "").replace((String)localObject2 + " ", ""))
          {
            localObject6 = localObject3;
            if (((String)localObject3).length() > 0)
              localObject6 = (String)localObject3 + "\n\n";
            localObject3 = (String)localObject6 + (String)localObject5;
            locala.a((String)localObject5);
            locali.a((CharSequence)localObject5, ((MessageObject)localObject7).messageOwner.date * 1000L, null);
            break;
          }
          localObject5 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
          ((Intent)localObject5).setAction("com.tmessages.openchat" + Math.random() + 2147483647);
          ((Intent)localObject5).setFlags(32768);
          if (localChat != null)
          {
            ((Intent)localObject5).putExtra("chatId", localChat.id);
            localObject5 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject5, 1073741824);
            localObject6 = new r.t();
            if (localObject4 != null)
              ((r.t)localObject6).a((r.a)localObject4);
            localObject4 = null;
            if (localChat == null)
              break label1716;
            localObject4 = "tgchat" + localChat.id + "_" + k;
          }
          while (true)
          {
            ((r.t)localObject6).a((String)localObject4);
            localObject7 = new r.t();
            ((r.t)localObject7).a("summary_" + (String)localObject4);
            paramd.a((r.g)localObject7);
            localObject2 = new r.d(ApplicationLoader.applicationContext).a((CharSequence)localObject2).a(2130837822).c("messages").b((CharSequence)localObject3).c(true).b(localArrayList2.size()).e(-13851168).e(false).a(((MessageObject)localArrayList2.get(0)).messageOwner.date * 1000L).a(locali).a((PendingIntent)localObject5).a((r.g)localObject6).a(new r.f().a(locala.a())).a("msg");
            if (localObject1 != null)
            {
              localObject3 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject1, null, "50_50");
              if (localObject3 == null)
                break label1761;
              ((r.d)localObject2).a(((BitmapDrawable)localObject3).getBitmap());
            }
            if ((localChat == null) && (localUser != null) && (localUser.phone != null) && (localUser.phone.length() > 0))
              ((r.d)localObject2).b("tel:+" + localUser.phone);
            this.notificationManager.a(localInteger.intValue(), ((r.d)localObject2).b());
            this.wearNotificationsIds.put(Long.valueOf(l), localInteger);
            break;
            if (localUser == null)
              break label1315;
            ((Intent)localObject5).putExtra("userId", localUser.id);
            break label1315;
            if (localUser == null)
              continue;
            localObject4 = "tguser" + localUser.id + "_" + k;
          }
          while (true)
          {
            float f;
            try
            {
              f = 160.0F / AndroidUtilities.dp(50.0F);
              localObject3 = new BitmapFactory.Options();
              if (f >= 1.0F)
                break label1835;
              j = 1;
              ((BitmapFactory.Options)localObject3).inSampleSize = j;
              localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject1, true).toString(), (BitmapFactory.Options)localObject3);
              if (localObject1 == null)
                break;
              ((r.d)localObject2).a((Bitmap)localObject1);
            }
            catch (Throwable localThrowable)
            {
            }
            break;
            j = (int)f;
          }
          paramd = localHashMap2.entrySet().iterator();
          while (paramd.hasNext())
          {
            localEntry = (Map.Entry)paramd.next();
            this.notificationManager.a(((Integer)localEntry.getValue()).intValue());
          }
          break;
        }
        label1028: label1315: label1716: label1902: localObject2 = localEntry;
        label1835: Map.Entry localEntry = null;
      }
      label1912: localChat = null;
    }
  }

  private void showOrUpdateNotification(boolean paramBoolean)
  {
    if ((!UserConfig.isClientActivated()) || (this.pushMessages.isEmpty()))
    {
      dismissNotification();
      return;
    }
    MessageObject localMessageObject1;
    Object localObject3;
    int i6;
    try
    {
      ConnectionsManager.getInstance().resumeNetworkMaybe();
      localMessageObject1 = (MessageObject)this.pushMessages.get(0);
      localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
      i6 = ((SharedPreferences)localObject3).getInt("dismissDate", 0);
      if (localMessageObject1.messageOwner.date <= i6)
      {
        dismissNotification();
        return;
      }
    }
    catch (Exception localException1)
    {
      FileLog.e(localException1);
      return;
    }
    long l2 = localMessageObject1.getDialogId();
    long l1;
    label152: label180: Object localObject7;
    TLRPC.Chat localChat;
    label211: int j;
    int m;
    int k;
    int i;
    label281: label457: boolean bool1;
    label394: int i2;
    int i3;
    label651: int i5;
    label677: int n;
    label717: label847: Object localObject5;
    label795: label991: label1007: r.d locald;
    label900: label1065: label1088: label1262: label2167: Object localObject2;
    if (localMessageObject1.messageOwner.mentioned)
    {
      l1 = localMessageObject1.messageOwner.from_id;
      localMessageObject1.getId();
      int i4;
      int i1;
      Object localObject1;
      String str2;
      Object localObject6;
      String str1;
      label1308: label1338: label1606: if (localMessageObject1.messageOwner.to_id.chat_id != 0)
      {
        i4 = localMessageObject1.messageOwner.to_id.chat_id;
        i1 = localMessageObject1.messageOwner.to_id.user_id;
        if (i1 != 0)
          break label1725;
        i1 = localMessageObject1.messageOwner.from_id;
        localObject7 = MessagesController.getInstance().getUser(Integer.valueOf(i1));
        if (i4 == 0)
          break label2877;
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(i4));
        j = 0;
        m = -16776961;
        k = getNotifyOverride((SharedPreferences)localObject3, l1);
        if ((!paramBoolean) || (k == 2))
          break label2901;
        if (!((SharedPreferences)localObject3).getBoolean("EnableAll", true))
          break label2893;
        i = j;
        if (i4 != 0)
        {
          i = j;
          if (!((SharedPreferences)localObject3).getBoolean("EnableGroup", true))
            break label2893;
        }
        if ((i != 0) || (l2 != l1) || (localChat == null))
          break label2871;
        if (!((SharedPreferences)localObject3).getBoolean("custom_" + l2, false))
          break label3004;
        j = ((SharedPreferences)localObject3).getInt("smart_max_count_" + l2, 2);
        k = ((SharedPreferences)localObject3).getInt("smart_delay_" + l2, 180);
        if (j == 0)
          break label2871;
        localObject1 = (Point)this.smartNotificationsDialogs.get(Long.valueOf(l2));
        if (localObject1 != null)
          break label1746;
        localObject1 = new Point(1, (int)(System.currentTimeMillis() / 1000L));
        this.smartNotificationsDialogs.put(Long.valueOf(l2), localObject1);
        j = i;
        str2 = Settings.System.DEFAULT_NOTIFICATION_URI.getPath();
        if (j != 0)
          break label2851;
        boolean bool2 = ((SharedPreferences)localObject3).getBoolean("EnableInAppSounds", true);
        boolean bool3 = ((SharedPreferences)localObject3).getBoolean("EnableInAppVibrate", true);
        bool1 = ((SharedPreferences)localObject3).getBoolean("EnableInAppPreview", true);
        boolean bool4 = ((SharedPreferences)localObject3).getBoolean("EnableInAppPriority", false);
        boolean bool5 = ((SharedPreferences)localObject3).getBoolean("custom_" + l2, false);
        if (!bool5)
          break label3021;
        i2 = ((SharedPreferences)localObject3).getInt("vibrate_" + l2, 0);
        i3 = ((SharedPreferences)localObject3).getInt("priority_" + l2, 3);
        localObject1 = ((SharedPreferences)localObject3).getString("sound_path_" + l2, null);
        i5 = 0;
        if (i4 == 0)
          break label1845;
        if ((localObject1 == null) || (!((String)localObject1).equals(str2)))
          break label1823;
        localObject1 = null;
        i = ((SharedPreferences)localObject3).getInt("vibrate_group", 0);
        n = ((SharedPreferences)localObject3).getInt("priority_group", 1);
        m = ((SharedPreferences)localObject3).getInt("GroupLed", -16776961);
        k = m;
        if (!bool5)
          break label2906;
        k = m;
        if (!((SharedPreferences)localObject3).contains("color_" + l2))
          break label2906;
        k = ((SharedPreferences)localObject3).getInt("color_" + l2, 0);
        break label2906;
        bool5 = ApplicationLoader.mainInterfacePaused;
        m = n;
        i = i3;
        localObject3 = localObject1;
        if (!bool5)
        {
          if (!bool2)
            localObject1 = null;
          if (!bool3)
            n = 2;
          if (bool4)
            break label3033;
          i = 0;
          localObject3 = localObject1;
          m = n;
        }
        if ((i5 == 0) || (m == 2))
          break label3063;
        label1503: break label2723;
      }
      while (true)
      {
        try
        {
          i2 = this.audioManager.getRingerMode();
          n = m;
          if (i2 != 0)
          {
            n = m;
            if (i2 != 1)
              n = 2;
          }
          localObject5 = localObject3;
          m = k;
          k = i;
          localObject1 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
          ((Intent)localObject1).setAction("com.tmessages.openchat" + Math.random() + 2147483647);
          ((Intent)localObject1).setFlags(32768);
          if ((int)l2 == 0)
            break label2107;
          if (this.pushDialogs.size() == 1)
          {
            if (i4 != 0)
              ((Intent)localObject1).putExtra("chatId", i4);
          }
          else
          {
            if (AndroidUtilities.needShowPasscode(false))
              break label2986;
            if (!UserConfig.isWaitingForPasscodeEnter)
              break label1962;
            break label2986;
            localObject1 = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject1, 1073741824);
            if (((int)l2 != 0) && (this.pushDialogs.size() <= 1) && (!AndroidUtilities.needShowPasscode(false)) && (!UserConfig.isWaitingForPasscodeEnter))
              break label2136;
            localObject6 = LocaleController.getString("AppName", 2131165319);
            i1 = 0;
            if (this.pushDialogs.size() != 1)
              break label2167;
            str1 = LocaleController.formatPluralString("NewMessages", this.total_unread_count);
            locald = new r.d(ApplicationLoader.applicationContext).a((CharSequence)localObject6).a(2130837822).c(true).b(this.total_unread_count).a((PendingIntent)localObject1).c("messages").e(true).e(-13851168);
            locald.a("msg");
            if ((localChat == null) && (localObject7 != null) && (((TLRPC.User)localObject7).phone != null) && (((TLRPC.User)localObject7).phone.length() > 0))
              locald.b("tel:+" + ((TLRPC.User)localObject7).phone);
            i = 2;
            localObject1 = null;
            if (this.pushMessages.size() != 1)
              break label2275;
            localObject1 = (MessageObject)this.pushMessages.get(0);
            localObject7 = getStringForMessage((MessageObject)localObject1, false);
            if (!((MessageObject)localObject1).messageOwner.silent)
              break label3095;
            i = 1;
            if (localObject7 == null)
              break;
            if (i1 == 0)
              break label2816;
            if (localChat == null)
              break label2214;
            localObject1 = ((String)localObject7).replace(" @ " + (String)localObject6, "");
            locald.b((CharSequence)localObject1);
            locald.a(new r.c().a((CharSequence)localObject1));
            localObject1 = localObject7;
            localObject6 = new Intent(ApplicationLoader.applicationContext, NotificationDismissReceiver.class);
            ((Intent)localObject6).putExtra("messageDate", localMessageObject1.messageOwner.date);
            locald.b(PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 1, (Intent)localObject6, 134217728));
            if (localObject3 == null)
              break label2992;
            localObject6 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject3, null, "50_50");
            if (localObject6 == null)
              break label2532;
            locald.a(((BitmapDrawable)localObject6).getBitmap());
            break label2992;
            label1426: locald.d(-1);
            label1433: if ((i == 1) || (j != 0))
              break label2751;
            if ((ApplicationLoader.mainInterfacePaused) || (bool1))
            {
              if (((String)localObject1).length() <= 100)
                break label2804;
              localObject1 = ((String)localObject1).substring(0, 100).replace('\n', ' ').trim() + "...";
              locald.c((CharSequence)localObject1);
            }
            if ((!MediaController.getInstance().isRecordingAudio()) && (localObject5 != null) && (!localObject5.equals("NoSound")))
            {
              if (!localObject5.equals(str2))
                break label2660;
              locald.a(Settings.System.DEFAULT_NOTIFICATION_URI, 5);
            }
            label1556: if (m != 0)
              locald.a(m, 1000, 1000);
            if ((n != 2) && (!MediaController.getInstance().isRecordingAudio()))
              break label2675;
            locald.a(new long[] { 0L, 0L });
            label1607: if ((Build.VERSION.SDK_INT >= 24) || (UserConfig.passcodeHash.length() != 0) || (!hasMessagesToReply()))
              continue;
            localObject1 = new Intent(ApplicationLoader.applicationContext, PopupReplyReceiver.class);
            if (Build.VERSION.SDK_INT > 19)
              break label2771;
            locald.a(2130837740, LocaleController.getString("Reply", 2131166324), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, (Intent)localObject1, 134217728));
            label1684: showExtraNotifications(locald, paramBoolean);
            this.notificationManager.a(1, locald.b());
            scheduleNotificationRepeat();
            return;
            i4 = localMessageObject1.messageOwner.to_id.channel_id;
            break label152;
            label1725: if (i1 != UserConfig.getClientUserId())
              break label2883;
            i1 = localMessageObject1.messageOwner.from_id;
            break label180;
            label1746: if (k + ((Point)localObject1).y >= System.currentTimeMillis() / 1000L)
              continue;
            ((Point)localObject1).set(1, (int)(System.currentTimeMillis() / 1000L));
            j = i;
            break label457;
            k = ((Point)localObject1).x;
            if (k >= j)
              break label3015;
            ((Point)localObject1).set(k + 1, (int)(System.currentTimeMillis() / 1000L));
            j = i;
            break label457;
            label1823: if (localObject1 != null)
              break label2848;
            localObject1 = ((SharedPreferences)localObject3).getString("GroupSoundPath", str2);
            break label677;
            label1845: if (i1 == 0)
              break label2840;
            if ((localObject1 == null) || (!((String)localObject1).equals(str2)))
              continue;
            localObject1 = null;
            label1868: i = ((SharedPreferences)localObject3).getInt("vibrate_messages", 0);
            n = ((SharedPreferences)localObject3).getInt("priority_group", 1);
            m = ((SharedPreferences)localObject3).getInt("MessagesLed", -16776961);
            break label717;
            if (localObject1 != null)
              break label2837;
            localObject1 = ((SharedPreferences)localObject3).getString("GlobalSoundPath", str2);
            continue;
          }
        }
        catch (Exception localException2)
        {
          FileLog.e(localException2);
        }
        if (i1 == 0)
          break label991;
        localException2.putExtra("userId", i1);
        break label991;
        label1962: if (this.pushDialogs.size() != 1)
          break label3089;
        if (localChat != null)
        {
          if ((localChat.photo == null) || (localChat.photo.photo_small == null) || (localChat.photo.photo_small.volume_id == 0L) || (localChat.photo.photo_small.local_id == 0))
            break label3089;
          localObject3 = localChat.photo.photo_small;
          break label1007;
        }
        if ((localObject7 == null) || (((TLRPC.User)localObject7).photo == null) || (((TLRPC.User)localObject7).photo.photo_small == null) || (((TLRPC.User)localObject7).photo.photo_small.volume_id == 0L) || (((TLRPC.User)localObject7).photo.photo_small.local_id == 0))
          break label3089;
        localObject3 = ((TLRPC.User)localObject7).photo.photo_small;
        break label1007;
        label2107: if (this.pushDialogs.size() != 1)
          break label3089;
        localException2.putExtra("encId", (int)(l2 >> 32));
        break label3089;
        label2136: if (localChat != null)
        {
          localObject6 = localChat.title;
          i1 = 1;
          break label1065;
        }
        localObject6 = UserObject.getUserName((TLRPC.User)localObject7);
        i1 = 1;
        break label1065;
        str1 = LocaleController.formatString("NotificationMessagesPeopleDisplayOrder", 2131166126, new Object[] { LocaleController.formatPluralString("NewMessages", this.total_unread_count), LocaleController.formatPluralString("FromChats", this.pushDialogs.size()) });
        break label1088;
        label2214: localObject2 = ((String)localObject7).replace((String)localObject6 + ": ", "").replace((String)localObject6 + " ", "");
        break label1308;
        label2275: locald.b(str1);
        r.h localh = new r.h();
        localh.a((CharSequence)localObject6);
        i3 = Math.min(10, this.pushMessages.size());
        i2 = 0;
        label2317: if (i2 < i3)
        {
          MessageObject localMessageObject2 = (MessageObject)this.pushMessages.get(i2);
          localObject7 = getStringForMessage(localMessageObject2, false);
          if (localObject7 == null)
            break label2810;
          if (localMessageObject2.messageOwner.date <= i6)
            break label3100;
          if (i != 2)
            break label2807;
          if (!localMessageObject2.messageOwner.silent)
            break label3116;
          i = 1;
          break label3109;
          label2389: if ((this.pushDialogs.size() != 1) || (i1 == 0))
            break label2813;
          if (localChat != null);
          for (localObject7 = ((String)localObject7).replace(" @ " + (String)localObject6, ""); ; localObject7 = ((String)localObject7).replace((String)localObject6 + ": ", "").replace((String)localObject6 + " ", ""))
          {
            localh.c((CharSequence)localObject7);
            break;
          }
        }
        label2441: localh.b(str1);
        locald.a(localh);
        break label1338;
        while (true)
        {
          label2532: float f;
          try
          {
            f = 160.0F / AndroidUtilities.dp(50.0F);
            localObject6 = new BitmapFactory.Options();
            if (f < 1.0F)
            {
              i1 = 1;
              ((BitmapFactory.Options)localObject6).inSampleSize = i1;
              localObject3 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject3, true).toString(), (BitmapFactory.Options)localObject6);
              if (localObject3 == null)
                break;
              locald.a((Bitmap)localObject3);
            }
          }
          catch (Throwable localThrowable)
          {
          }
          i1 = (int)f;
        }
        label2613: if (k == 0)
        {
          locald.d(0);
          break label1433;
        }
        if (k == 1)
        {
          locald.d(1);
          break label1433;
        }
        if (k != 2)
          break label1433;
        locald.d(2);
        break label1433;
        label2660: locald.a(Uri.parse(localObject5), 5);
        break label1556;
        label2675: if (n != 1)
          break label3121;
        locald.a(new long[] { 0L, 100L, 0L, 100L });
      }
    }
    while (true)
    {
      locald.c(2);
      break label1607;
      label2723: if (n != 3)
        break label1607;
      locald.a(new long[] { 0L, 1000L });
      break label1607;
      label2751: locald.a(new long[] { 0L, 0L });
      break label1607;
      label2771: locald.a(2130837739, LocaleController.getString("Reply", 2131166324), PendingIntent.getBroadcast(ApplicationLoader.applicationContext, 2, (Intent)localObject2, 134217728));
      break label1684;
      label2804: break label1503;
      label2807: break label2389;
      label2810: break label3100;
      label2813: break label2441;
      label2816: localObject2 = localObject7;
      break label1308;
      label2837: label2840: label2848: label2851: label2871: label2877: label2883: label2893: label2901: label2906: label2912: 
      do
      {
        n = m;
        break label795;
        do
        {
          i3 = n;
          break label2912;
          break label1868;
          i = 0;
          n = 0;
          break label717;
          break label677;
          k = 0;
          bool1 = false;
          m = -16776961;
          localObject5 = null;
          n = 0;
          break label900;
          j = i;
          break label457;
          localChat = null;
          break label211;
          break label180;
          l1 = l2;
          break;
          i = j;
          if (k != 0)
            break label281;
          i = 1;
          break label281;
        }
        while (i3 == 3);
        m = i;
        if (i == 4)
        {
          m = 0;
          i5 = 1;
        }
        if (m == 2)
        {
          n = i2;
          if (i2 == 1)
            break label795;
          n = i2;
          if (i2 == 3)
            break label795;
        }
        if (m == 2)
          continue;
        n = i2;
        if (i2 == 2)
          break label795;
      }
      while ((i2 == 0) || (i2 == 4));
      n = i2;
      break label795;
      label2986: Object localObject4 = null;
      break label1007;
      label2992: if (!paramBoolean)
        break label1426;
      if (i != 1)
        break label2613;
      break label1426;
      label3004: j = 2;
      k = 180;
      break label394;
      label3015: j = 1;
      break label457;
      label3021: i2 = 0;
      i3 = 3;
      localObject2 = null;
      break label651;
      label3033: m = n;
      i = i3;
      localObject4 = localObject2;
      if (i3 != 2)
        break label847;
      i = 1;
      m = n;
      localObject4 = localObject2;
      break label847;
      label3063: n = k;
      i2 = m;
      k = i;
      m = n;
      localObject5 = localObject4;
      n = i2;
      break label900;
      label3089: localObject4 = null;
      break label1007;
      label3095: i = 0;
      break label1262;
      label3100: i2 += 1;
      break label2317;
      while (true)
      {
        label3109: localObject2 = localObject7;
        break;
        label3116: i = 0;
      }
      label3121: if (n == 0)
        continue;
      if (n != 4)
        break label1606;
    }
  }

  public static void updateServerNotificationsSettings(long paramLong)
  {
    NotificationCenter.getInstance().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
    if ((int)paramLong == 0)
      return;
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
    TLRPC.TL_account_updateNotifySettings localTL_account_updateNotifySettings = new TLRPC.TL_account_updateNotifySettings();
    localTL_account_updateNotifySettings.settings = new TLRPC.TL_inputPeerNotifySettings();
    localTL_account_updateNotifySettings.settings.sound = "default";
    int i = localSharedPreferences.getInt("notify2_" + paramLong, 0);
    if (i == 3)
    {
      localTL_account_updateNotifySettings.settings.mute_until = localSharedPreferences.getInt("notifyuntil_" + paramLong, 0);
      localTL_account_updateNotifySettings.settings.show_previews = localSharedPreferences.getBoolean("preview_" + paramLong, true);
      localTL_account_updateNotifySettings.settings.silent = localSharedPreferences.getBoolean("silent_" + paramLong, false);
      localTL_account_updateNotifySettings.peer = new TLRPC.TL_inputNotifyPeer();
      ((TLRPC.TL_inputNotifyPeer)localTL_account_updateNotifySettings.peer).peer = MessagesController.getInputPeer((int)paramLong);
      ConnectionsManager.getInstance().sendRequest(localTL_account_updateNotifySettings, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
        }
      });
      return;
    }
    TLRPC.TL_inputPeerNotifySettings localTL_inputPeerNotifySettings = localTL_account_updateNotifySettings.settings;
    if (i != 2);
    for (i = 0; ; i = 2147483647)
    {
      localTL_inputPeerNotifySettings.mute_until = i;
      break;
    }
  }

  public void cleanup()
  {
    this.popupMessages.clear();
    this.popupReplyMessages.clear();
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, 0L);
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.wearNotificationsIds.clear();
        NotificationsController.this.delayedPushMessages.clear();
        NotificationsController.access$1002(NotificationsController.this, false);
        NotificationsController.access$1102(NotificationsController.this, 0);
        try
        {
          if (NotificationsController.this.notificationDelayWakelock.isHeld())
            NotificationsController.this.notificationDelayWakelock.release();
          NotificationsController.this.setBadge(0);
          SharedPreferences.Editor localEditor = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit();
          localEditor.clear();
          localEditor.commit();
          return;
        }
        catch (Exception localException)
        {
          while (true)
            FileLog.e(localException);
        }
      }
    });
  }

  protected void forceShowPopupForReply()
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        ArrayList localArrayList = new ArrayList();
        int i = 0;
        if (i < NotificationsController.this.pushMessages.size())
        {
          MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
          long l = localMessageObject.getDialogId();
          if (((localMessageObject.messageOwner.mentioned) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) || ((int)l == 0) || ((localMessageObject.messageOwner.to_id.channel_id != 0) && (!localMessageObject.isMegagroup())));
          while (true)
          {
            i += 1;
            break;
            localArrayList.add(0, localMessageObject);
          }
        }
        if ((!localArrayList.isEmpty()) && (!AndroidUtilities.needShowPasscode(false)))
          AndroidUtilities.runOnUIThread(new Runnable(localArrayList)
          {
            public void run()
            {
              NotificationsController.this.popupReplyMessages = this.val$popupArray;
              Intent localIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
              localIntent.putExtra("force", true);
              localIntent.setFlags(268763140);
              ApplicationLoader.applicationContext.startActivity(localIntent);
              localIntent = new Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS");
              ApplicationLoader.applicationContext.sendBroadcast(localIntent);
            }
          });
      }
    });
  }

  public boolean hasMessagesToReply()
  {
    int k = 0;
    int i = 0;
    int j;
    while (true)
    {
      j = k;
      if (i >= this.pushMessages.size())
        break;
      MessageObject localMessageObject = (MessageObject)this.pushMessages.get(i);
      long l = localMessageObject.getDialogId();
      if (((localMessageObject.messageOwner.mentioned) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage))) || ((int)l == 0) || ((localMessageObject.messageOwner.to_id.channel_id != 0) && (!localMessageObject.isMegagroup())))
      {
        i += 1;
        continue;
      }
      j = 1;
    }
    return j;
  }

  public void playOutChatSound()
  {
    if ((!this.inChatSoundEnabled) || (MediaController.getInstance().isRecordingAudio()));
    while (true)
    {
      return;
      try
      {
        int i = this.audioManager.getRingerMode();
        if (i == 0)
          continue;
        this.notificationsQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            try
            {
              if (Math.abs(System.currentTimeMillis() - NotificationsController.this.lastSoundOutPlay) <= 100L)
                return;
              NotificationsController.access$2602(NotificationsController.this, System.currentTimeMillis());
              if (NotificationsController.this.soundPool == null)
              {
                NotificationsController.access$2202(NotificationsController.this, new SoundPool(3, 1, 0));
                NotificationsController.this.soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener()
                {
                  public void onLoadComplete(SoundPool paramSoundPool, int paramInt1, int paramInt2)
                  {
                    if (paramInt2 == 0);
                    try
                    {
                      paramSoundPool.play(paramInt1, 1.0F, 1.0F, 1, 0, 1.0F);
                      return;
                    }
                    catch (Exception paramSoundPool)
                    {
                      FileLog.e(paramSoundPool);
                    }
                  }
                });
              }
              if ((NotificationsController.this.soundOut == 0) && (!NotificationsController.this.soundOutLoaded))
              {
                NotificationsController.access$2802(NotificationsController.this, true);
                NotificationsController.access$2702(NotificationsController.this, NotificationsController.this.soundPool.load(ApplicationLoader.applicationContext, 2131099649, 1));
              }
              int i = NotificationsController.this.soundOut;
              if (i != 0)
                try
                {
                  NotificationsController.this.soundPool.play(NotificationsController.this.soundOut, 1.0F, 1.0F, 1, 0, 1.0F);
                  return;
                }
                catch (Exception localException1)
                {
                  FileLog.e(localException1);
                  return;
                }
            }
            catch (Exception localException2)
            {
              FileLog.e(localException2);
            }
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

  public void processDialogsUpdateRead(HashMap<Long, Integer> paramHashMap)
  {
    if (this.popupMessages.isEmpty());
    for (ArrayList localArrayList = null; ; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable(paramHashMap, localArrayList)
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          Iterator localIterator = this.val$dialogsToUpdate.entrySet().iterator();
          Object localObject;
          long l3;
          int i;
          Integer localInteger1;
          if (localIterator.hasNext())
          {
            localObject = (Map.Entry)localIterator.next();
            l3 = ((Long)((Map.Entry)localObject).getKey()).longValue();
            i = NotificationsController.this.getNotifyOverride(localSharedPreferences, l3);
            if (!NotificationsController.this.notifyCheck)
              break label779;
            localInteger1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l3));
            if ((localInteger1 == null) || (localInteger1.intValue() != 1))
              break label779;
            NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(0));
            i = 1;
          }
          label779: 
          while (true)
          {
            if ((i != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l3 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (i != 0)));
            for (i = 1; ; i = 0)
            {
              Integer localInteger2 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l3));
              localInteger1 = (Integer)((Map.Entry)localObject).getValue();
              if (localInteger1.intValue() == 0)
                NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l3));
              localObject = localInteger1;
              int j;
              if (localInteger1.intValue() < 0)
              {
                if (localInteger2 == null)
                  break;
                j = localInteger2.intValue();
                localObject = Integer.valueOf(localInteger1.intValue() + j);
              }
              if (((i != 0) || (((Integer)localObject).intValue() == 0)) && (localInteger2 != null))
                NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count - localInteger2.intValue());
              if (((Integer)localObject).intValue() != 0)
                break label592;
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l3));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l3));
              for (i = 0; i < NotificationsController.this.pushMessages.size(); i = j + 1)
              {
                localObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                j = i;
                if (((MessageObject)localObject).getDialogId() != l3)
                  continue;
                if (NotificationsController.this.isPersonalMessage((MessageObject)localObject))
                  NotificationsController.access$510(NotificationsController.this);
                NotificationsController.this.pushMessages.remove(i);
                i -= 1;
                NotificationsController.this.delayedPushMessages.remove(localObject);
                long l2 = ((MessageObject)localObject).messageOwner.id;
                long l1 = l2;
                if (((MessageObject)localObject).messageOwner.to_id.channel_id != 0)
                  l1 = l2 | ((MessageObject)localObject).messageOwner.to_id.channel_id << 32;
                NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                j = i;
                if (this.val$popupArray == null)
                  continue;
                this.val$popupArray.remove(localObject);
                j = i;
              }
            }
            if ((this.val$popupArray == null) || (!NotificationsController.this.pushMessages.isEmpty()) || (this.val$popupArray.isEmpty()))
              break;
            this.val$popupArray.clear();
            break;
            label592: if (i == 0)
              break;
            NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject).intValue());
            NotificationsController.this.pushDialogs.put(Long.valueOf(l3), localObject);
            break;
            if (this.val$popupArray != null)
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.this.popupMessages = NotificationsController.10.this.val$popupArray;
                }
              });
            if (k != NotificationsController.this.total_unread_count)
            {
              if (!NotificationsController.this.notifyCheck)
              {
                NotificationsController.this.delayedPushMessages.clear();
                NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
              }
            }
            else
            {
              NotificationsController.access$1002(NotificationsController.this, false);
              if (localSharedPreferences.getBoolean("badgeNumber", true))
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
              return;
            }
            localObject = NotificationsController.this;
            if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
            for (boolean bool = true; ; bool = false)
            {
              ((NotificationsController)localObject).scheduleNotificationDelay(bool);
              break;
            }
          }
        }
      });
      return;
    }
  }

  public void processLoadedUnreadMessages(HashMap<Long, Integer> paramHashMap, ArrayList<TLRPC.Message> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    MessagesController.getInstance().putUsers(paramArrayList1, true);
    MessagesController.getInstance().putChats(paramArrayList2, true);
    MessagesController.getInstance().putEncryptedChats(paramArrayList3, true);
    this.notificationsQueue.postRunnable(new Runnable(paramArrayList, paramHashMap)
    {
      public void run()
      {
        NotificationsController.this.pushDialogs.clear();
        NotificationsController.this.pushMessages.clear();
        NotificationsController.this.pushMessagesDict.clear();
        NotificationsController.access$402(NotificationsController.this, 0);
        NotificationsController.access$502(NotificationsController.this, 0);
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        HashMap localHashMap = new HashMap();
        int i;
        Object localObject1;
        long l2;
        long l1;
        Object localObject2;
        long l3;
        if (this.val$messages != null)
        {
          i = 0;
          while (i < this.val$messages.size())
          {
            localObject1 = (TLRPC.Message)this.val$messages.get(i);
            l2 = ((TLRPC.Message)localObject1).id;
            l1 = l2;
            if (((TLRPC.Message)localObject1).to_id.channel_id != 0)
              l1 = l2 | ((TLRPC.Message)localObject1).to_id.channel_id << 32;
            if (NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l1)))
            {
              i += 1;
              continue;
            }
            localObject2 = new MessageObject((TLRPC.Message)localObject1, null, false);
            if (NotificationsController.this.isPersonalMessage((MessageObject)localObject2))
              NotificationsController.access$508(NotificationsController.this);
            l3 = ((MessageObject)localObject2).getDialogId();
            if (!((MessageObject)localObject2).messageOwner.mentioned)
              break label774;
            l2 = ((MessageObject)localObject2).messageOwner.from_id;
          }
        }
        while (true)
        {
          Boolean localBoolean = (Boolean)localHashMap.get(Long.valueOf(l2));
          localObject1 = localBoolean;
          if (localBoolean == null)
          {
            int j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l2);
            if ((j == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || (((int)l2 < 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (j == 0)))
              break label413;
          }
          label413: for (boolean bool = true; ; bool = false)
          {
            localObject1 = Boolean.valueOf(bool);
            localHashMap.put(Long.valueOf(l2), localObject1);
            if ((!((Boolean)localObject1).booleanValue()) || ((l2 == NotificationsController.this.opened_dialog_id) && (ApplicationLoader.isScreenOn)))
              break;
            NotificationsController.this.pushMessagesDict.put(Long.valueOf(l1), localObject2);
            NotificationsController.this.pushMessages.add(0, localObject2);
            if (l3 == l2)
              break;
            NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
            break;
          }
          localObject2 = this.val$dialogs.entrySet().iterator();
          Map.Entry localEntry;
          if (((Iterator)localObject2).hasNext())
          {
            localEntry = (Map.Entry)((Iterator)localObject2).next();
            l1 = ((Long)localEntry.getKey()).longValue();
            localBoolean = (Boolean)localHashMap.get(Long.valueOf(l1));
            localObject1 = localBoolean;
            if (localBoolean == null)
            {
              i = NotificationsController.this.getNotifyOverride(localSharedPreferences, l1);
              localObject1 = (Integer)NotificationsController.this.pushDialogsOverrideMention.get(Long.valueOf(l1));
              if ((localObject1 == null) || (((Integer)localObject1).intValue() != 1))
                break label771;
              NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l1), Integer.valueOf(0));
              i = 1;
            }
          }
          label771: 
          while (true)
          {
            if ((i != 2) && (((localSharedPreferences.getBoolean("EnableAll", true)) && (((int)l1 >= 0) || (localSharedPreferences.getBoolean("EnableGroup", true)))) || (i != 0)));
            for (bool = true; ; bool = false)
            {
              localObject1 = Boolean.valueOf(bool);
              localHashMap.put(Long.valueOf(l1), localObject1);
              if (!((Boolean)localObject1).booleanValue())
                break;
              i = ((Integer)localEntry.getValue()).intValue();
              NotificationsController.this.pushDialogs.put(Long.valueOf(l1), Integer.valueOf(i));
              NotificationsController.access$402(NotificationsController.this, i + NotificationsController.this.total_unread_count);
              break;
            }
            if (NotificationsController.this.total_unread_count == 0)
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.this.popupMessages.clear();
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                }
              });
            localObject1 = NotificationsController.this;
            if (SystemClock.uptimeMillis() / 1000L < 60L);
            for (bool = true; ; bool = false)
            {
              ((NotificationsController)localObject1).showOrUpdateNotification(bool);
              if (localSharedPreferences.getBoolean("badgeNumber", true))
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
              return;
            }
          }
          label774: l2 = l3;
        }
      }
    });
  }

  public void processNewMessages(ArrayList<MessageObject> paramArrayList, boolean paramBoolean)
  {
    if (paramArrayList.isEmpty())
      return;
    ArrayList localArrayList = new ArrayList(this.popupMessages);
    this.notificationsQueue.postRunnable(new Runnable(localArrayList, paramArrayList, paramBoolean)
    {
      public void run()
      {
        int n = this.val$popupArray.size();
        HashMap localHashMap = new HashMap();
        SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
        boolean bool2 = localSharedPreferences.getBoolean("PinnedMessages", true);
        int i = 0;
        int j = 0;
        int k = 0;
        MessageObject localMessageObject;
        long l2;
        long l1;
        long l3;
        if (k < this.val$messageObjects.size())
        {
          localMessageObject = (MessageObject)this.val$messageObjects.get(k);
          l2 = localMessageObject.messageOwner.id;
          l1 = l2;
          if (localMessageObject.messageOwner.to_id.channel_id != 0)
            l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
          if (NotificationsController.this.pushMessagesDict.containsKey(Long.valueOf(l1)));
          label181: 
          do
          {
            while (true)
            {
              k += 1;
              break;
              l3 = localMessageObject.getDialogId();
              if ((l3 != NotificationsController.this.opened_dialog_id) || (!ApplicationLoader.isScreenOn))
                break label181;
              NotificationsController.this.playInChatSound();
            }
            if (!localMessageObject.messageOwner.mentioned)
              break label678;
          }
          while ((!bool2) && ((localMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionPinMessage)));
          l2 = localMessageObject.messageOwner.from_id;
        }
        while (true)
        {
          if (NotificationsController.this.isPersonalMessage(localMessageObject))
            NotificationsController.access$508(NotificationsController.this);
          Boolean localBoolean = (Boolean)localHashMap.get(Long.valueOf(l2));
          j = (int)l2;
          int m;
          label271: label335: Object localObject;
          label349: label360: boolean bool1;
          if (j < 0)
          {
            m = 1;
            if (j != 0)
            {
              if (!localSharedPreferences.getBoolean("custom_" + l2, false))
                break label577;
              j = localSharedPreferences.getInt("popup_" + l2, 0);
              if (j != 0)
                break label589;
              if ((int)l2 >= 0)
                break label582;
              localObject = "popupGroup";
              i = localSharedPreferences.getInt((String)localObject, 0);
            }
            localObject = localBoolean;
            if (localBoolean == null)
            {
              j = NotificationsController.this.getNotifyOverride(localSharedPreferences, l2);
              if ((j == 2) || (((!localSharedPreferences.getBoolean("EnableAll", true)) || ((m != 0) && (!localSharedPreferences.getBoolean("EnableGroup", true)))) && (j == 0)))
                break label611;
              bool1 = true;
              label424: localObject = Boolean.valueOf(bool1);
              localHashMap.put(Long.valueOf(l2), localObject);
            }
            if ((i == 0) || (localMessageObject.messageOwner.to_id.channel_id == 0) || (localMessageObject.isMegagroup()))
              break label675;
            i = 0;
          }
          label675: 
          while (true)
          {
            if (((Boolean)localObject).booleanValue())
            {
              if (i != 0)
                this.val$popupArray.add(0, localMessageObject);
              NotificationsController.this.delayedPushMessages.add(localMessageObject);
              NotificationsController.this.pushMessages.add(0, localMessageObject);
              NotificationsController.this.pushMessagesDict.put(Long.valueOf(l1), localMessageObject);
              if (l3 != l2)
                NotificationsController.this.pushDialogsOverrideMention.put(Long.valueOf(l3), Integer.valueOf(1));
            }
            j = 1;
            break;
            m = 0;
            break label271;
            label577: j = 0;
            break label335;
            label582: localObject = "popupAll";
            break label349;
            label589: if (j == 1)
            {
              i = 3;
              break label360;
            }
            i = j;
            if (j != 2)
              break label360;
            i = 0;
            break label360;
            label611: bool1 = false;
            break label424;
            if (j != 0)
              NotificationsController.access$1002(NotificationsController.this, this.val$isLast);
            if ((!this.val$popupArray.isEmpty()) && (n != this.val$popupArray.size()) && (!AndroidUtilities.needShowPasscode(false)))
              AndroidUtilities.runOnUIThread(new Runnable(i)
              {
                public void run()
                {
                  NotificationsController.this.popupMessages = NotificationsController.9.this.val$popupArray;
                  if (((ApplicationLoader.mainInterfacePaused) || ((!ApplicationLoader.isScreenOn) && (!UserConfig.isWaitingForPasscodeEnter))) && ((this.val$popupFinal == 3) || ((this.val$popupFinal == 1) && (ApplicationLoader.isScreenOn)) || ((this.val$popupFinal == 2) && (!ApplicationLoader.isScreenOn))))
                  {
                    Intent localIntent = new Intent(ApplicationLoader.applicationContext, PopupNotificationActivity.class);
                    localIntent.setFlags(268763140);
                    ApplicationLoader.applicationContext.startActivity(localIntent);
                  }
                }
              });
            return;
          }
          label678: l2 = l3;
        }
      }
    });
  }

  public void processReadMessages(SparseArray<Long> paramSparseArray, long paramLong, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (this.popupMessages.isEmpty());
    for (ArrayList localArrayList = null; ; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable(localArrayList, paramSparseArray, paramLong, paramInt2, paramInt1, paramBoolean)
      {
        public void run()
        {
          int k;
          int j;
          int i;
          MessageObject localMessageObject;
          int m;
          long l2;
          long l1;
          if (this.val$popupArray != null)
          {
            k = this.val$popupArray.size();
            if (this.val$inbox != null)
              j = 0;
          }
          else
          {
            while (true)
            {
              if (j >= this.val$inbox.size())
                break label275;
              int n = this.val$inbox.keyAt(j);
              long l3 = ((Long)this.val$inbox.get(n)).longValue();
              i = 0;
              while (true)
                if (i < NotificationsController.this.pushMessages.size())
                {
                  localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(i);
                  m = i;
                  if (localMessageObject.getDialogId() == n)
                  {
                    m = i;
                    if (localMessageObject.getId() <= (int)l3)
                    {
                      if (NotificationsController.this.isPersonalMessage(localMessageObject))
                        NotificationsController.access$510(NotificationsController.this);
                      if (this.val$popupArray != null)
                        this.val$popupArray.remove(localMessageObject);
                      l2 = localMessageObject.messageOwner.id;
                      l1 = l2;
                      if (localMessageObject.messageOwner.to_id.channel_id != 0)
                        l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
                      NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
                      NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                      NotificationsController.this.pushMessages.remove(i);
                      m = i - 1;
                    }
                  }
                  i = m + 1;
                  continue;
                  k = 0;
                  break;
                }
              j += 1;
            }
            label275: if ((this.val$popupArray != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!this.val$popupArray.isEmpty()))
              this.val$popupArray.clear();
          }
          if ((this.val$dialog_id != 0L) && ((this.val$max_id != 0) || (this.val$max_date != 0)))
          {
            j = 0;
            if (j < NotificationsController.this.pushMessages.size())
            {
              localMessageObject = (MessageObject)NotificationsController.this.pushMessages.get(j);
              m = j;
              if (localMessageObject.getDialogId() == this.val$dialog_id)
              {
                if (this.val$max_date == 0)
                  break label554;
                if (localMessageObject.messageOwner.date > this.val$max_date)
                  break label676;
                i = 1;
              }
            }
          }
          while (true)
          {
            m = j;
            if (i != 0)
            {
              if (NotificationsController.this.isPersonalMessage(localMessageObject))
                NotificationsController.access$510(NotificationsController.this);
              NotificationsController.this.pushMessages.remove(j);
              NotificationsController.this.delayedPushMessages.remove(localMessageObject);
              if (this.val$popupArray != null)
                this.val$popupArray.remove(localMessageObject);
              l2 = localMessageObject.messageOwner.id;
              l1 = l2;
              if (localMessageObject.messageOwner.to_id.channel_id != 0)
                l1 = l2 | localMessageObject.messageOwner.to_id.channel_id << 32;
              NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l1));
              m = j - 1;
            }
            j = m + 1;
            break;
            label554: if (!this.val$isPopup)
            {
              if ((localMessageObject.getId() <= this.val$max_id) || (this.val$max_id < 0))
              {
                i = 1;
                continue;
              }
            }
            else if ((localMessageObject.getId() == this.val$max_id) || (this.val$max_id < 0))
            {
              i = 1;
              continue;
              if ((this.val$popupArray != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!this.val$popupArray.isEmpty()))
                this.val$popupArray.clear();
              if ((this.val$popupArray != null) && (k != this.val$popupArray.size()))
                AndroidUtilities.runOnUIThread(new Runnable()
                {
                  public void run()
                  {
                    NotificationsController.this.popupMessages = NotificationsController.8.this.val$popupArray;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.pushMessagesUpdated, new Object[0]);
                  }
                });
              return;
            }
            label676: i = 0;
          }
        }
      });
      return;
    }
  }

  public void removeDeletedMessagesFromNotifications(SparseArray<ArrayList<Integer>> paramSparseArray)
  {
    if (this.popupMessages.isEmpty());
    for (ArrayList localArrayList = null; ; localArrayList = new ArrayList(this.popupMessages))
    {
      this.notificationsQueue.postRunnable(new Runnable(paramSparseArray, localArrayList)
      {
        public void run()
        {
          int k = NotificationsController.this.total_unread_count;
          SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0);
          int i = 0;
          int m;
          long l1;
          ArrayList localArrayList;
          Object localObject1;
          if (i < this.val$deletedMessages.size())
          {
            m = this.val$deletedMessages.keyAt(i);
            l1 = -m;
            localArrayList = (ArrayList)this.val$deletedMessages.get(m);
            localObject1 = (Integer)NotificationsController.this.pushDialogs.get(Long.valueOf(l1));
            if (localObject1 != null)
              break label590;
            localObject1 = Integer.valueOf(0);
          }
          label590: 
          while (true)
          {
            int j = 0;
            for (Object localObject2 = localObject1; j < localArrayList.size(); localObject2 = localObject3)
            {
              long l2 = ((Integer)localArrayList.get(j)).intValue() | m << 32;
              MessageObject localMessageObject = (MessageObject)NotificationsController.this.pushMessagesDict.get(Long.valueOf(l2));
              localObject3 = localObject2;
              if (localMessageObject != null)
              {
                NotificationsController.this.pushMessagesDict.remove(Long.valueOf(l2));
                NotificationsController.this.delayedPushMessages.remove(localMessageObject);
                NotificationsController.this.pushMessages.remove(localMessageObject);
                if (NotificationsController.this.isPersonalMessage(localMessageObject))
                  NotificationsController.access$510(NotificationsController.this);
                if (this.val$popupArray != null)
                  this.val$popupArray.remove(localMessageObject);
                localObject3 = Integer.valueOf(localObject2.intValue() - 1);
              }
              j += 1;
            }
            Object localObject3 = localObject2;
            if (localObject2.intValue() <= 0)
            {
              localObject3 = Integer.valueOf(0);
              NotificationsController.this.smartNotificationsDialogs.remove(Long.valueOf(l1));
            }
            if (!((Integer)localObject3).equals(localObject1))
            {
              NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count - ((Integer)localObject1).intValue());
              NotificationsController.access$402(NotificationsController.this, NotificationsController.this.total_unread_count + ((Integer)localObject3).intValue());
              NotificationsController.this.pushDialogs.put(Long.valueOf(l1), localObject3);
            }
            if (((Integer)localObject3).intValue() == 0)
            {
              NotificationsController.this.pushDialogs.remove(Long.valueOf(l1));
              NotificationsController.this.pushDialogsOverrideMention.remove(Long.valueOf(l1));
              if ((this.val$popupArray != null) && (NotificationsController.this.pushMessages.isEmpty()) && (!this.val$popupArray.isEmpty()))
                this.val$popupArray.clear();
            }
            i += 1;
            break;
            if (this.val$popupArray != null)
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationsController.this.popupMessages = NotificationsController.7.this.val$popupArray;
                }
              });
            if (k != NotificationsController.this.total_unread_count)
            {
              if (!NotificationsController.this.notifyCheck)
              {
                NotificationsController.this.delayedPushMessages.clear();
                NotificationsController.this.showOrUpdateNotification(NotificationsController.this.notifyCheck);
              }
            }
            else
            {
              NotificationsController.access$1002(NotificationsController.this, false);
              if (localSharedPreferences.getBoolean("badgeNumber", true))
                NotificationsController.this.setBadge(NotificationsController.this.total_unread_count);
              return;
            }
            localObject1 = NotificationsController.this;
            if (NotificationsController.this.lastOnlineFromOtherDevice > ConnectionsManager.getInstance().getCurrentTime());
            for (boolean bool = true; ; bool = false)
            {
              ((NotificationsController)localObject1).scheduleNotificationDelay(bool);
              break;
            }
          }
        }
      });
      return;
    }
  }

  public void removeNotificationsForDialog(long paramLong)
  {
    getInstance().processReadMessages(null, paramLong, 0, 2147483647, false);
    HashMap localHashMap = new HashMap();
    localHashMap.put(Long.valueOf(paramLong), Integer.valueOf(0));
    getInstance().processDialogsUpdateRead(localHashMap);
  }

  protected void repeatNotificationMaybe()
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        int i = Calendar.getInstance().get(11);
        if ((i >= 11) && (i <= 22))
        {
          NotificationsController.this.notificationManager.a(1);
          NotificationsController.this.showOrUpdateNotification(true);
          return;
        }
        NotificationsController.this.scheduleNotificationRepeat();
      }
    });
  }

  public void setBadgeEnabled(boolean paramBoolean)
  {
    if (paramBoolean);
    for (int i = this.total_unread_count; ; i = 0)
    {
      setBadge(i);
      return;
    }
  }

  public void setInChatSoundEnabled(boolean paramBoolean)
  {
    this.inChatSoundEnabled = paramBoolean;
  }

  public void setLastOnlineFromOtherDevice(int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        FileLog.e("set last online from other device = " + this.val$time);
        NotificationsController.access$1302(NotificationsController.this, this.val$time);
      }
    });
  }

  public void setMissedCallCountBadge(int paramInt)
  {
    this.notificationsQueue.postRunnable(new Runnable(paramInt)
    {
      public void run()
      {
        NotificationBadge.applyCount(this.val$count + NotificationsController.this.lastBadgeCount);
      }
    });
  }

  public void setOpenedDialogId(long paramLong)
  {
    this.notificationsQueue.postRunnable(new Runnable(paramLong)
    {
      public void run()
      {
        NotificationsController.access$302(NotificationsController.this, this.val$dialog_id);
      }
    });
  }

  protected void showSingleBackgroundNotification()
  {
    this.notificationsQueue.postRunnable(new Runnable()
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: iconst_0
        //   1: istore_1
        //   2: getstatic 29	org/vidogram/messenger/ApplicationLoader:mainInterfacePaused	Z
        //   5: ifne +4 -> 9
        //   8: return
        //   9: getstatic 33	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   12: ldc 35
        //   14: iconst_0
        //   15: invokevirtual 41	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
        //   18: astore 8
        //   20: aload 8
        //   22: ldc 43
        //   24: iconst_1
        //   25: invokeinterface 49 3 0
        //   30: ifne +686 -> 716
        //   33: iconst_1
        //   34: istore 4
        //   36: getstatic 55	android/provider/Settings$System:DEFAULT_NOTIFICATION_URI	Landroid/net/Uri;
        //   39: invokevirtual 61	android/net/Uri:getPath	()Ljava/lang/String;
        //   42: astore 10
        //   44: iload 4
        //   46: ifne +657 -> 703
        //   49: iconst_0
        //   50: ifeq +470 -> 520
        //   53: new 63	java/lang/NullPointerException
        //   56: dup
        //   57: invokespecial 64	java/lang/NullPointerException:<init>	()V
        //   60: athrow
        //   61: aload 8
        //   63: ldc 66
        //   65: iconst_0
        //   66: invokeinterface 70 3 0
        //   71: istore_2
        //   72: aload 8
        //   74: ldc 72
        //   76: iconst_1
        //   77: invokeinterface 70 3 0
        //   82: istore 5
        //   84: aload 8
        //   86: ldc 74
        //   88: ldc 75
        //   90: invokeinterface 70 3 0
        //   95: istore_3
        //   96: iload_2
        //   97: iconst_4
        //   98: if_icmpne +592 -> 690
        //   101: iconst_1
        //   102: istore_2
        //   103: iload_1
        //   104: iconst_2
        //   105: if_icmpne +3 -> 108
        //   108: iload_1
        //   109: iconst_2
        //   110: if_icmpeq +3 -> 113
        //   113: iload_2
        //   114: ifeq +608 -> 722
        //   117: iload_1
        //   118: iconst_2
        //   119: if_icmpeq +603 -> 722
        //   122: aload_0
        //   123: getfield 17	org/vidogram/messenger/NotificationsController$5:this$0	Lorg/vidogram/messenger/NotificationsController;
        //   126: getfield 79	org/vidogram/messenger/NotificationsController:audioManager	Landroid/media/AudioManager;
        //   129: invokevirtual 85	android/media/AudioManager:getRingerMode	()I
        //   132: istore 6
        //   134: iload_1
        //   135: istore_2
        //   136: iload 6
        //   138: ifeq +13 -> 151
        //   141: iload_1
        //   142: istore_2
        //   143: iload 6
        //   145: iconst_1
        //   146: if_icmpeq +5 -> 151
        //   149: iconst_2
        //   150: istore_2
        //   151: iload 5
        //   153: istore_1
        //   154: iload_3
        //   155: istore 5
        //   157: iload_2
        //   158: istore_3
        //   159: iload 5
        //   161: istore_2
        //   162: new 87	android/content/Intent
        //   165: dup
        //   166: getstatic 33	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   169: ldc 89
        //   171: invokespecial 92	android/content/Intent:<init>	(Landroid/content/Context;Ljava/lang/Class;)V
        //   174: astore 8
        //   176: aload 8
        //   178: new 94	java/lang/StringBuilder
        //   181: dup
        //   182: invokespecial 95	java/lang/StringBuilder:<init>	()V
        //   185: ldc 97
        //   187: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   190: invokestatic 107	java/lang/Math:random	()D
        //   193: invokevirtual 110	java/lang/StringBuilder:append	(D)Ljava/lang/StringBuilder;
        //   196: ldc 111
        //   198: invokevirtual 114	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
        //   201: invokevirtual 117	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   204: invokevirtual 121	android/content/Intent:setAction	(Ljava/lang/String;)Landroid/content/Intent;
        //   207: pop
        //   208: aload 8
        //   210: ldc 122
        //   212: invokevirtual 126	android/content/Intent:setFlags	(I)Landroid/content/Intent;
        //   215: pop
        //   216: getstatic 33	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   219: iconst_0
        //   220: aload 8
        //   222: ldc 127
        //   224: invokestatic 133	android/app/PendingIntent:getActivity	(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
        //   227: astore 9
        //   229: ldc 135
        //   231: ldc 136
        //   233: invokestatic 142	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   236: astore 8
        //   238: new 144	android/support/v4/b/r$d
        //   241: dup
        //   242: getstatic 33	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   245: invokespecial 147	android/support/v4/b/r$d:<init>	(Landroid/content/Context;)V
        //   248: aload 8
        //   250: invokevirtual 151	android/support/v4/b/r$d:a	(Ljava/lang/CharSequence;)Landroid/support/v4/b/r$d;
        //   253: ldc 152
        //   255: invokevirtual 155	android/support/v4/b/r$d:a	(I)Landroid/support/v4/b/r$d;
        //   258: iconst_1
        //   259: invokevirtual 159	android/support/v4/b/r$d:c	(Z)Landroid/support/v4/b/r$d;
        //   262: aload_0
        //   263: getfield 17	org/vidogram/messenger/NotificationsController$5:this$0	Lorg/vidogram/messenger/NotificationsController;
        //   266: invokestatic 163	org/vidogram/messenger/NotificationsController:access$400	(Lorg/vidogram/messenger/NotificationsController;)I
        //   269: invokevirtual 166	android/support/v4/b/r$d:b	(I)Landroid/support/v4/b/r$d;
        //   272: aload 9
        //   274: invokevirtual 169	android/support/v4/b/r$d:a	(Landroid/app/PendingIntent;)Landroid/support/v4/b/r$d;
        //   277: ldc 171
        //   279: invokevirtual 174	android/support/v4/b/r$d:c	(Ljava/lang/String;)Landroid/support/v4/b/r$d;
        //   282: iconst_1
        //   283: invokevirtual 177	android/support/v4/b/r$d:e	(Z)Landroid/support/v4/b/r$d;
        //   286: ldc 178
        //   288: invokevirtual 180	android/support/v4/b/r$d:e	(I)Landroid/support/v4/b/r$d;
        //   291: astore 11
        //   293: aload 11
        //   295: ldc 182
        //   297: invokevirtual 184	android/support/v4/b/r$d:a	(Ljava/lang/String;)Landroid/support/v4/b/r$d;
        //   300: pop
        //   301: ldc 186
        //   303: ldc 187
        //   305: invokestatic 142	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
        //   308: astore 9
        //   310: aload 11
        //   312: aload 9
        //   314: invokevirtual 189	android/support/v4/b/r$d:b	(Ljava/lang/CharSequence;)Landroid/support/v4/b/r$d;
        //   317: pop
        //   318: aload 11
        //   320: new 191	android/support/v4/b/r$c
        //   323: dup
        //   324: invokespecial 192	android/support/v4/b/r$c:<init>	()V
        //   327: aload 9
        //   329: invokevirtual 195	android/support/v4/b/r$c:a	(Ljava/lang/CharSequence;)Landroid/support/v4/b/r$c;
        //   332: invokevirtual 198	android/support/v4/b/r$d:a	(Landroid/support/v4/b/r$s;)Landroid/support/v4/b/r$d;
        //   335: pop
        //   336: iload_1
        //   337: ifne +214 -> 551
        //   340: aload 11
        //   342: iconst_0
        //   343: invokevirtual 201	android/support/v4/b/r$d:d	(I)Landroid/support/v4/b/r$d;
        //   346: pop
        //   347: iload 4
        //   349: ifne +321 -> 670
        //   352: aload 9
        //   354: astore 8
        //   356: aload 9
        //   358: invokevirtual 206	java/lang/String:length	()I
        //   361: bipush 100
        //   363: if_icmple +41 -> 404
        //   366: new 94	java/lang/StringBuilder
        //   369: dup
        //   370: invokespecial 95	java/lang/StringBuilder:<init>	()V
        //   373: aload 9
        //   375: iconst_0
        //   376: bipush 100
        //   378: invokevirtual 210	java/lang/String:substring	(II)Ljava/lang/String;
        //   381: bipush 10
        //   383: bipush 32
        //   385: invokevirtual 214	java/lang/String:replace	(CC)Ljava/lang/String;
        //   388: invokevirtual 217	java/lang/String:trim	()Ljava/lang/String;
        //   391: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   394: ldc 219
        //   396: invokevirtual 101	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   399: invokevirtual 117	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   402: astore 8
        //   404: aload 11
        //   406: aload 8
        //   408: invokevirtual 221	android/support/v4/b/r$d:c	(Ljava/lang/CharSequence;)Landroid/support/v4/b/r$d;
        //   411: pop
        //   412: aload 7
        //   414: ifnull +33 -> 447
        //   417: aload 7
        //   419: ldc 223
        //   421: invokevirtual 227	java/lang/String:equals	(Ljava/lang/Object;)Z
        //   424: ifne +23 -> 447
        //   427: aload 7
        //   429: aload 10
        //   431: invokevirtual 227	java/lang/String:equals	(Ljava/lang/Object;)Z
        //   434: ifeq +147 -> 581
        //   437: aload 11
        //   439: getstatic 55	android/provider/Settings$System:DEFAULT_NOTIFICATION_URI	Landroid/net/Uri;
        //   442: iconst_5
        //   443: invokevirtual 230	android/support/v4/b/r$d:a	(Landroid/net/Uri;I)Landroid/support/v4/b/r$d;
        //   446: pop
        //   447: iload_2
        //   448: ifeq +16 -> 464
        //   451: aload 11
        //   453: iload_2
        //   454: sipush 1000
        //   457: sipush 1000
        //   460: invokevirtual 233	android/support/v4/b/r$d:a	(III)Landroid/support/v4/b/r$d;
        //   463: pop
        //   464: iload_3
        //   465: iconst_2
        //   466: if_icmpeq +12 -> 478
        //   469: invokestatic 239	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
        //   472: invokevirtual 243	org/vidogram/messenger/MediaController:isRecordingAudio	()Z
        //   475: ifeq +121 -> 596
        //   478: aload 11
        //   480: iconst_2
        //   481: newarray long
        //   483: dup
        //   484: iconst_0
        //   485: lconst_0
        //   486: lastore
        //   487: dup
        //   488: iconst_1
        //   489: lconst_0
        //   490: lastore
        //   491: invokevirtual 246	android/support/v4/b/r$d:a	([J)Landroid/support/v4/b/r$d;
        //   494: pop
        //   495: aload_0
        //   496: getfield 17	org/vidogram/messenger/NotificationsController$5:this$0	Lorg/vidogram/messenger/NotificationsController;
        //   499: invokestatic 250	org/vidogram/messenger/NotificationsController:access$1400	(Lorg/vidogram/messenger/NotificationsController;)Landroid/support/v4/b/aa;
        //   502: iconst_1
        //   503: aload 11
        //   505: invokevirtual 253	android/support/v4/b/r$d:b	()Landroid/app/Notification;
        //   508: invokevirtual 258	android/support/v4/b/aa:a	(ILandroid/app/Notification;)V
        //   511: return
        //   512: astore 7
        //   514: aload 7
        //   516: invokestatic 263	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   519: return
        //   520: iconst_0
        //   521: ifne +176 -> 697
        //   524: aload 8
        //   526: ldc_w 265
        //   529: aload 10
        //   531: invokeinterface 268 3 0
        //   536: astore 7
        //   538: goto -477 -> 61
        //   541: astore 8
        //   543: aload 8
        //   545: invokestatic 263	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   548: goto +174 -> 722
        //   551: iload_1
        //   552: iconst_1
        //   553: if_icmpne +13 -> 566
        //   556: aload 11
        //   558: iconst_1
        //   559: invokevirtual 201	android/support/v4/b/r$d:d	(I)Landroid/support/v4/b/r$d;
        //   562: pop
        //   563: goto -216 -> 347
        //   566: iload_1
        //   567: iconst_2
        //   568: if_icmpne -221 -> 347
        //   571: aload 11
        //   573: iconst_2
        //   574: invokevirtual 201	android/support/v4/b/r$d:d	(I)Landroid/support/v4/b/r$d;
        //   577: pop
        //   578: goto -231 -> 347
        //   581: aload 11
        //   583: aload 7
        //   585: invokestatic 272	android/net/Uri:parse	(Ljava/lang/String;)Landroid/net/Uri;
        //   588: iconst_5
        //   589: invokevirtual 230	android/support/v4/b/r$d:a	(Landroid/net/Uri;I)Landroid/support/v4/b/r$d;
        //   592: pop
        //   593: goto -146 -> 447
        //   596: iload_3
        //   597: iconst_1
        //   598: if_icmpne +134 -> 732
        //   601: aload 11
        //   603: iconst_4
        //   604: newarray long
        //   606: dup
        //   607: iconst_0
        //   608: lconst_0
        //   609: lastore
        //   610: dup
        //   611: iconst_1
        //   612: ldc2_w 273
        //   615: lastore
        //   616: dup
        //   617: iconst_2
        //   618: lconst_0
        //   619: lastore
        //   620: dup
        //   621: iconst_3
        //   622: ldc2_w 273
        //   625: lastore
        //   626: invokevirtual 246	android/support/v4/b/r$d:a	([J)Landroid/support/v4/b/r$d;
        //   629: pop
        //   630: goto -135 -> 495
        //   633: aload 11
        //   635: iconst_2
        //   636: invokevirtual 276	android/support/v4/b/r$d:c	(I)Landroid/support/v4/b/r$d;
        //   639: pop
        //   640: goto -145 -> 495
        //   643: iload_3
        //   644: iconst_3
        //   645: if_icmpne -150 -> 495
        //   648: aload 11
        //   650: iconst_2
        //   651: newarray long
        //   653: dup
        //   654: iconst_0
        //   655: lconst_0
        //   656: lastore
        //   657: dup
        //   658: iconst_1
        //   659: ldc2_w 277
        //   662: lastore
        //   663: invokevirtual 246	android/support/v4/b/r$d:a	([J)Landroid/support/v4/b/r$d;
        //   666: pop
        //   667: goto -172 -> 495
        //   670: aload 11
        //   672: iconst_2
        //   673: newarray long
        //   675: dup
        //   676: iconst_0
        //   677: lconst_0
        //   678: lastore
        //   679: dup
        //   680: iconst_1
        //   681: lconst_0
        //   682: lastore
        //   683: invokevirtual 246	android/support/v4/b/r$d:a	([J)Landroid/support/v4/b/r$d;
        //   686: pop
        //   687: goto -192 -> 495
        //   690: iload_2
        //   691: istore_1
        //   692: iconst_0
        //   693: istore_2
        //   694: goto -591 -> 103
        //   697: aconst_null
        //   698: astore 7
        //   700: goto -639 -> 61
        //   703: aconst_null
        //   704: astore 7
        //   706: iconst_0
        //   707: istore_3
        //   708: ldc 75
        //   710: istore_2
        //   711: iconst_0
        //   712: istore_1
        //   713: goto -551 -> 162
        //   716: iconst_0
        //   717: istore 4
        //   719: goto -683 -> 36
        //   722: iload_3
        //   723: istore_2
        //   724: iload_1
        //   725: istore_3
        //   726: iload 5
        //   728: istore_1
        //   729: goto -567 -> 162
        //   732: iload_3
        //   733: ifeq -100 -> 633
        //   736: iload_3
        //   737: iconst_4
        //   738: if_icmpne -95 -> 643
        //   741: goto -108 -> 633
        //
        // Exception table:
        //   from	to	target	type
        //   2	8	512	java/lang/Exception
        //   9	33	512	java/lang/Exception
        //   36	44	512	java/lang/Exception
        //   53	61	512	java/lang/Exception
        //   61	96	512	java/lang/Exception
        //   162	336	512	java/lang/Exception
        //   340	347	512	java/lang/Exception
        //   356	404	512	java/lang/Exception
        //   404	412	512	java/lang/Exception
        //   417	447	512	java/lang/Exception
        //   451	464	512	java/lang/Exception
        //   469	478	512	java/lang/Exception
        //   478	495	512	java/lang/Exception
        //   495	511	512	java/lang/Exception
        //   524	538	512	java/lang/Exception
        //   543	548	512	java/lang/Exception
        //   556	563	512	java/lang/Exception
        //   571	578	512	java/lang/Exception
        //   581	593	512	java/lang/Exception
        //   601	630	512	java/lang/Exception
        //   633	640	512	java/lang/Exception
        //   648	667	512	java/lang/Exception
        //   670	687	512	java/lang/Exception
        //   122	134	541	java/lang/Exception
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.NotificationsController
 * JD-Core Version:    0.6.0
 */