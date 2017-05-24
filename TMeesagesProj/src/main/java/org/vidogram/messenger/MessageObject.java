package org.vidogram.messenger;

import android.graphics.Point;
import android.graphics.Typeface;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.util.Linkify;
import com.google.firebase.crash.FirebaseCrash;
import java.io.File;
import java.util.AbstractMap;
import java.util.AbstractMap<Ljava.lang.Integer;Lorg.vidogram.tgnet.TLRPC.Chat;>;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.ReplyMarkup;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_game;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonRow;
import org.vidogram.tgnet.TLRPC.TL_message;
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
import org.vidogram.tgnet.TLRPC.TL_messageActionCreatedBroadcastList;
import org.vidogram.tgnet.TLRPC.TL_messageActionEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageActionGameScore;
import org.vidogram.tgnet.TLRPC.TL_messageActionHistoryClear;
import org.vidogram.tgnet.TLRPC.TL_messageActionLoginUnknownLocation;
import org.vidogram.tgnet.TLRPC.TL_messageActionPaymentSent;
import org.vidogram.tgnet.TLRPC.TL_messageActionPhoneCall;
import org.vidogram.tgnet.TLRPC.TL_messageActionPinMessage;
import org.vidogram.tgnet.TLRPC.TL_messageActionTTLChange;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserJoined;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.vidogram.tgnet.TLRPC.TL_messageForwarded_old2;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaContact;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGame;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaUnsupported;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.TL_message_secret;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.vidogram.tgnet.TLRPC.TL_webDocument;
import org.vidogram.tgnet.TLRPC.TL_webPage;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.TypefaceSpan;
import org.vidogram.ui.Components.URLSpanBotCommand;
import org.vidogram.ui.Components.URLSpanNoUnderline;
import org.vidogram.ui.Components.URLSpanNoUnderlineBold;

public class MessageObject
{
  private static final int LINES_PER_BLOCK = 10;
  public static final int MESSAGE_SEND_STATE_SENDING = 1;
  public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
  public static final int MESSAGE_SEND_STATE_SENT = 0;
  public static Pattern urlPattern;
  public boolean attachPathExists;
  public float audioProgress;
  public int audioProgressSec;
  public StringBuilder botButtonsLayout;
  public CharSequence caption;
  public int contentType;
  public String customReplyName;
  public String dateKey;
  public boolean deleted;
  public boolean forceUpdate;
  private int generatedWithMinSize;
  public boolean hasRtl;
  public boolean isDateObject;
  public int lastLineWidth;
  private boolean layoutCreated;
  public CharSequence linkDescription;
  public boolean mediaExists;
  public TLRPC.Message messageOwner;
  public CharSequence messageText;
  public String monthKey;
  public ArrayList<TLRPC.PhotoSize> photoThumbs;
  public ArrayList<TLRPC.PhotoSize> photoThumbs2;
  public MessageObject replyMessageObject;
  public boolean resendAsIs;
  public int textHeight;
  public ArrayList<TextLayoutBlock> textLayoutBlocks;
  public int textWidth;
  public float textXOffset;
  public int type = 1000;
  public boolean useCustomPhoto;
  public VideoEditedInfo videoEditedInfo;
  public boolean viewsReloaded;
  public int wantedBotKeyboardWidth;

  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, AbstractMap<Integer, TLRPC.Chat> paramAbstractMap1, boolean paramBoolean)
  {
    Theme.createChatResources(null, true);
    this.messageOwner = paramMessage;
    if (paramMessage.replyMessage != null)
      this.replyMessageObject = new MessageObject(paramMessage.replyMessage, paramAbstractMap, paramAbstractMap1, false);
    TLRPC.User localUser = null;
    if (paramMessage.from_id > 0)
    {
      if (paramAbstractMap != null)
        localUser = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.from_id));
      if (localUser == null)
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.from_id));
    }
    while (true)
    {
      Object localObject;
      int i;
      int j;
      int k;
      if ((paramMessage instanceof TLRPC.TL_messageService))
      {
        localObject = localUser;
        if (paramMessage.action != null)
        {
          if (!(paramMessage.action instanceof TLRPC.TL_messageActionChatCreate))
            break label573;
          if (!isOut())
            break label544;
          this.messageText = LocaleController.getString("ActionYouCreateGroup", 2131165267);
          localObject = localUser;
        }
        while (true)
        {
          if (this.messageText == null)
            this.messageText = "";
          setType();
          measureInlineBotButtons();
          paramMessage = new GregorianCalendar();
          paramMessage.setTimeInMillis(this.messageOwner.date * 1000L);
          i = paramMessage.get(6);
          j = paramMessage.get(1);
          k = paramMessage.get(2);
          this.dateKey = String.format("%d_%02d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k), Integer.valueOf(i) });
          this.monthKey = String.format("%d_%02d", new Object[] { Integer.valueOf(j), Integer.valueOf(k) });
          if ((this.messageOwner.message != null) && (this.messageOwner.id < 0) && (this.messageOwner.message.length() > 6) && ((isVideo()) || (isNewGif())))
          {
            this.videoEditedInfo = new VideoEditedInfo();
            if (!this.videoEditedInfo.parseString(this.messageOwner.message))
              this.videoEditedInfo = null;
          }
          generateCaption();
          if (!paramBoolean)
            break label3730;
          if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
          {
            paramMessage = Theme.chat_msgGameTextPaint;
            label387: if (!MessagesController.getInstance().allowBigEmoji)
              break label3689;
            paramAbstractMap = new int[1];
            label400: this.messageText = Emoji.replaceEmoji(this.messageText, paramMessage.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false, paramAbstractMap);
            if ((paramAbstractMap == null) || (paramAbstractMap[0] < 1) || (paramAbstractMap[0] > 3))
              break label3724;
            switch (paramAbstractMap[0])
            {
            default:
              paramMessage = Theme.chat_msgTextPaintThreeEmoji;
              i = AndroidUtilities.dp(24.0F);
              label475: paramAbstractMap = (Emoji.EmojiSpan[])((Spannable)this.messageText).getSpans(0, this.messageText.length(), Emoji.EmojiSpan.class);
              if ((paramAbstractMap == null) || (paramAbstractMap.length <= 0))
                break label3724;
              j = 0;
              while (j < paramAbstractMap.length)
              {
                paramAbstractMap[j].replaceFontMetrics(paramMessage.getFontMetricsInt(), i);
                j += 1;
              }
              label544: this.messageText = replaceWithLink(LocaleController.getString("ActionCreateGroup", 2131165238), "un1", localUser);
              localObject = localUser;
              continue;
              label573: if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeleteUser))
              {
                if (paramMessage.action.user_id == paramMessage.from_id)
                {
                  if (isOut())
                  {
                    this.messageText = LocaleController.getString("ActionYouLeftUser", 2131165269);
                    localObject = localUser;
                    continue;
                  }
                  this.messageText = replaceWithLink(LocaleController.getString("ActionLeftUser", 2131165244), "un1", localUser);
                  localObject = localUser;
                  continue;
                }
                paramAbstractMap1 = null;
                if (paramAbstractMap != null)
                  paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(paramMessage.action.user_id));
                paramAbstractMap = paramAbstractMap1;
                if (paramAbstractMap1 == null)
                  paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(paramMessage.action.user_id));
                if (isOut())
                {
                  this.messageText = replaceWithLink(LocaleController.getString("ActionYouKickUser", 2131165268), "un2", paramAbstractMap);
                  localObject = localUser;
                  continue;
                }
                if (paramMessage.action.user_id == UserConfig.getClientUserId())
                {
                  this.messageText = replaceWithLink(LocaleController.getString("ActionKickUserYou", 2131165243), "un1", localUser);
                  localObject = localUser;
                  continue;
                }
                this.messageText = replaceWithLink(LocaleController.getString("ActionKickUser", 2131165242), "un2", paramAbstractMap);
                this.messageText = replaceWithLink(this.messageText, "un1", localUser);
                localObject = localUser;
                continue;
              }
              if ((paramMessage.action instanceof TLRPC.TL_messageActionChatAddUser))
              {
                i = this.messageOwner.action.user_id;
                if ((i != 0) || (this.messageOwner.action.users.size() != 1))
                  break label3752;
                i = ((Integer)this.messageOwner.action.users.get(0)).intValue();
              }
            case 1:
            case 2:
            }
          }
        }
      }
      label2056: label2212: label3752: 
      while (true)
      {
        if (i != 0)
        {
          paramAbstractMap1 = null;
          if (paramAbstractMap != null)
            paramAbstractMap1 = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(i));
          paramAbstractMap = paramAbstractMap1;
          if (paramAbstractMap1 == null)
            paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(i));
          if (i == paramMessage.from_id)
          {
            if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
            {
              this.messageText = LocaleController.getString("ChannelJoined", 2131165472);
              localObject = localUser;
              break;
            }
            if ((paramMessage.to_id.channel_id != 0) && (isMegagroup()))
            {
              if (i == UserConfig.getClientUserId())
              {
                this.messageText = LocaleController.getString("ChannelMegaJoined", 2131165476);
                localObject = localUser;
                break;
              }
              this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelfMega", 2131165229), "un1", localUser);
              localObject = localUser;
              break;
            }
            if (isOut())
            {
              this.messageText = LocaleController.getString("ActionAddUserSelfYou", 2131165230);
              localObject = localUser;
              break;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserSelf", 2131165228), "un1", localUser);
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165264), "un2", paramAbstractMap);
            localObject = localUser;
            break;
          }
          if (i == UserConfig.getClientUserId())
          {
            if (paramMessage.to_id.channel_id != 0)
            {
              if (isMegagroup())
              {
                this.messageText = replaceWithLink(LocaleController.getString("MegaAddedBy", 2131165939), "un1", localUser);
                localObject = localUser;
                break;
              }
              this.messageText = replaceWithLink(LocaleController.getString("ChannelAddedBy", 2131165447), "un1", localUser);
              localObject = localUser;
              break;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionAddUserYou", 2131165231), "un1", localUser);
            localObject = localUser;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165227), "un2", paramAbstractMap);
          this.messageText = replaceWithLink(this.messageText, "un1", localUser);
          localObject = localUser;
          break;
        }
        if (isOut())
        {
          this.messageText = replaceWithLink(LocaleController.getString("ActionYouAddUser", 2131165264), "un2", paramMessage.action.users, paramAbstractMap);
          localObject = localUser;
          break;
        }
        this.messageText = replaceWithLink(LocaleController.getString("ActionAddUser", 2131165227), "un2", paramMessage.action.users, paramAbstractMap);
        this.messageText = replaceWithLink(this.messageText, "un1", localUser);
        localObject = localUser;
        break;
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChatJoinedByLink))
        {
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionInviteYou", 2131165241);
            localObject = localUser;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionInviteUser", 2131165240), "un1", localUser);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditPhoto))
        {
          if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
          {
            this.messageText = LocaleController.getString("ActionChannelChangedPhoto", 2131165234);
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionYouChangedPhoto", 2131165265);
            localObject = localUser;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionChangedPhoto", 2131165232), "un1", localUser);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChatEditTitle))
        {
          if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
          {
            this.messageText = LocaleController.getString("ActionChannelChangedTitle", 2131165235).replace("un2", paramMessage.action.title);
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionYouChangedTitle", 2131165266).replace("un2", paramMessage.action.title);
            localObject = localUser;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionChangedTitle", 2131165233).replace("un2", paramMessage.action.title), "un1", localUser);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChatDeletePhoto))
        {
          if ((paramMessage.to_id.channel_id != 0) && (!isMegagroup()))
          {
            this.messageText = LocaleController.getString("ActionChannelRemovedPhoto", 2131165236);
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("ActionYouRemovedPhoto", 2131165270);
            localObject = localUser;
            break;
          }
          this.messageText = replaceWithLink(LocaleController.getString("ActionRemovedPhoto", 2131165259), "un1", localUser);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionTTLChange))
        {
          if (paramMessage.action.ttl != 0)
          {
            if (isOut())
            {
              this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165962, new Object[] { LocaleController.formatTTLString(paramMessage.action.ttl) });
              localObject = localUser;
              break;
            }
            this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165961, new Object[] { UserObject.getFirstName(localUser), LocaleController.formatTTLString(paramMessage.action.ttl) });
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165964);
            localObject = localUser;
            break;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165963, new Object[] { UserObject.getFirstName(localUser) });
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
        {
          long l = paramMessage.date * 1000L;
          String str;
          if ((LocaleController.getInstance().formatterDay != null) && (LocaleController.getInstance().formatterYear != null))
          {
            str = LocaleController.formatString("formatDateAtTime", 2131166662, new Object[] { LocaleController.getInstance().formatterYear.format(l), LocaleController.getInstance().formatterDay.format(l) });
            localObject = UserConfig.getCurrentUser();
            paramAbstractMap1 = (AbstractMap<Integer, TLRPC.Chat>)localObject;
            if (localObject == null)
            {
              if (paramAbstractMap != null)
                localObject = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(this.messageOwner.to_id.user_id));
              paramAbstractMap1 = (AbstractMap<Integer, TLRPC.Chat>)localObject;
              if (localObject == null)
                paramAbstractMap1 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.to_id.user_id));
            }
            if (paramAbstractMap1 == null)
              break label2212;
          }
          for (paramAbstractMap = UserObject.getFirstName(paramAbstractMap1); ; paramAbstractMap = "")
          {
            this.messageText = LocaleController.formatString("NotificationUnrecognizedDevice", 2131166127, new Object[] { paramAbstractMap, str, paramMessage.action.title, paramMessage.action.address });
            localObject = localUser;
            break;
            str = "" + paramMessage.date;
            break label2056;
          }
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionUserJoined))
        {
          this.messageText = LocaleController.formatString("NotificationContactJoined", 2131166088, new Object[] { UserObject.getUserName(localUser) });
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
        {
          this.messageText = LocaleController.formatString("NotificationContactNewPhoto", 2131166089, new Object[] { UserObject.getUserName(localUser) });
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageEncryptedAction))
        {
          if ((paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
          {
            if (isOut())
            {
              this.messageText = LocaleController.formatString("ActionTakeScreenshootYou", 2131165261, new Object[0]);
              localObject = localUser;
              break;
            }
            this.messageText = replaceWithLink(LocaleController.getString("ActionTakeScreenshoot", 2131165260), "un1", localUser);
            localObject = localUser;
            break;
          }
          localObject = localUser;
          if (!(paramMessage.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
            break;
          paramMessage = (TLRPC.TL_decryptedMessageActionSetMessageTTL)paramMessage.action.encryptedAction;
          if (paramMessage.ttl_seconds != 0)
          {
            if (isOut())
            {
              this.messageText = LocaleController.formatString("MessageLifetimeChangedOutgoing", 2131165962, new Object[] { LocaleController.formatTTLString(paramMessage.ttl_seconds) });
              localObject = localUser;
              break;
            }
            this.messageText = LocaleController.formatString("MessageLifetimeChanged", 2131165961, new Object[] { UserObject.getFirstName(localUser), LocaleController.formatTTLString(paramMessage.ttl_seconds) });
            localObject = localUser;
            break;
          }
          if (isOut())
          {
            this.messageText = LocaleController.getString("MessageLifetimeYouRemoved", 2131165964);
            localObject = localUser;
            break;
          }
          this.messageText = LocaleController.formatString("MessageLifetimeRemoved", 2131165963, new Object[] { UserObject.getFirstName(localUser) });
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionCreatedBroadcastList))
        {
          this.messageText = LocaleController.formatString("YouCreatedBroadcastList", 2131166643, new Object[0]);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelCreate))
        {
          if (isMegagroup())
          {
            this.messageText = LocaleController.getString("ActionCreateMega", 2131165239);
            localObject = localUser;
            break;
          }
          this.messageText = LocaleController.getString("ActionCreateChannel", 2131165237);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChatMigrateTo))
        {
          this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165245);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionChannelMigrateFrom))
        {
          this.messageText = LocaleController.getString("ActionMigrateFromGroup", 2131165245);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionPinMessage))
        {
          if ((localUser == null) && (paramAbstractMap1 != null));
          for (paramMessage = (TLRPC.Chat)paramAbstractMap1.get(Integer.valueOf(paramMessage.to_id.channel_id)); ; paramMessage = null)
          {
            generatePinMessageText(localUser, paramMessage);
            localObject = localUser;
            break;
          }
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionHistoryClear))
        {
          this.messageText = LocaleController.getString("HistoryCleared", 2131165811);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionGameScore))
        {
          generateGameMessageText(localUser);
          localObject = localUser;
          break;
        }
        if ((paramMessage.action instanceof TLRPC.TL_messageActionPhoneCall))
        {
          paramMessage = (TLRPC.TL_messageActionPhoneCall)this.messageOwner.action;
          boolean bool = paramMessage.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed;
          if (this.messageOwner.from_id == UserConfig.getClientUserId())
            if (bool)
              this.messageText = LocaleController.getString("CallMessageOutgoingMissed", 2131165418);
          while (true)
          {
            localObject = localUser;
            if (paramMessage.duration <= 0)
              break;
            paramMessage = LocaleController.formatCallDuration(paramMessage.duration);
            this.messageText = LocaleController.formatString("CallMessageWithDuration", 2131165419, new Object[] { this.messageText, paramMessage });
            paramAbstractMap = this.messageText.toString();
            j = paramAbstractMap.indexOf(paramMessage);
            localObject = localUser;
            if (j == -1)
              break;
            paramAbstractMap1 = new SpannableString(this.messageText);
            k = paramMessage.length() + j;
            i = j;
            if (j > 0)
            {
              i = j;
              if (paramAbstractMap.charAt(j - 1) == '(')
                i = j - 1;
            }
            j = k;
            if (k < paramAbstractMap.length())
            {
              j = k;
              if (paramAbstractMap.charAt(k) == ')')
                j = k + 1;
            }
            paramAbstractMap1.setSpan(new TypefaceSpan(Typeface.DEFAULT), i, j, 0);
            this.messageText = paramAbstractMap1;
            localObject = localUser;
            break;
            this.messageText = LocaleController.getString("CallMessageOutgoing", 2131165417);
            continue;
            if (bool)
            {
              this.messageText = LocaleController.getString("CallMessageIncomingMissed", 2131165416);
              continue;
            }
            if ((paramMessage.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy))
            {
              this.messageText = LocaleController.getString("CallMessageIncomingDeclined", 2131165415);
              continue;
            }
            this.messageText = LocaleController.getString("CallMessageIncoming", 2131165414);
          }
        }
        localObject = localUser;
        if (!(paramMessage.action instanceof TLRPC.TL_messageActionPaymentSent))
          break;
        i = (int)getDialogId();
        if (paramAbstractMap != null);
        for (paramMessage = (TLRPC.User)paramAbstractMap.get(Integer.valueOf(i)); ; paramMessage = localUser)
        {
          paramAbstractMap = paramMessage;
          if (paramMessage == null)
            paramAbstractMap = MessagesController.getInstance().getUser(Integer.valueOf(i));
          generatePaymentSentMessageText(null);
          localObject = paramAbstractMap;
          break;
          if (!isMediaEmpty())
          {
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
            {
              this.messageText = LocaleController.getString("AttachPhoto", 2131165367);
              localObject = localUser;
              break;
            }
            if (isVideo())
            {
              this.messageText = LocaleController.getString("AttachVideo", 2131165369);
              localObject = localUser;
              break;
            }
            if (isVoice())
            {
              this.messageText = LocaleController.getString("AttachAudio", 2131165359);
              localObject = localUser;
              break;
            }
            if (((paramMessage.media instanceof TLRPC.TL_messageMediaGeo)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaVenue)))
            {
              this.messageText = LocaleController.getString("AttachLocation", 2131165365);
              localObject = localUser;
              break;
            }
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaContact))
            {
              this.messageText = LocaleController.getString("AttachContact", 2131165361);
              localObject = localUser;
              break;
            }
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaGame))
            {
              this.messageText = paramMessage.message;
              localObject = localUser;
              break;
            }
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaInvoice))
            {
              this.messageText = paramMessage.media.description;
              localObject = localUser;
              break;
            }
            if ((paramMessage.media instanceof TLRPC.TL_messageMediaUnsupported))
            {
              this.messageText = LocaleController.getString("UnsupportedMedia", 2131166541);
              localObject = localUser;
              break;
            }
            localObject = localUser;
            if (!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
              break;
            if (isSticker())
            {
              paramMessage = getStrickerChar();
              if ((paramMessage != null) && (paramMessage.length() > 0))
              {
                this.messageText = String.format("%s %s", new Object[] { paramMessage, LocaleController.getString("AttachSticker", 2131165368) });
                localObject = localUser;
                break;
              }
              this.messageText = LocaleController.getString("AttachSticker", 2131165368);
              localObject = localUser;
              break;
            }
            if (isMusic())
            {
              this.messageText = LocaleController.getString("AttachMusic", 2131165366);
              localObject = localUser;
              break;
            }
            if (isGif())
            {
              this.messageText = LocaleController.getString("AttachGif", 2131165364);
              localObject = localUser;
              break;
            }
            paramMessage = FileLoader.getDocumentFileName(paramMessage.media.document);
            if ((paramMessage != null) && (paramMessage.length() > 0))
            {
              this.messageText = paramMessage;
              localObject = localUser;
              break;
            }
            this.messageText = LocaleController.getString("AttachDocument", 2131165362);
            localObject = localUser;
            break;
          }
          this.messageText = paramMessage.message;
          localObject = localUser;
          break;
          paramMessage = Theme.chat_msgTextPaint;
          break label387;
          label3689: paramAbstractMap = null;
          break label400;
          paramMessage = Theme.chat_msgTextPaintOneEmoji;
          i = AndroidUtilities.dp(32.0F);
          break label475;
          paramMessage = Theme.chat_msgTextPaintTwoEmoji;
          i = AndroidUtilities.dp(28.0F);
          break label475;
          generateLayout((TLRPC.User)localObject);
          this.layoutCreated = paramBoolean;
          generateThumbs(false);
          checkMediaExistance();
          return;
        }
      }
      label3724: label3730: continue;
      localUser = null;
    }
  }

  public MessageObject(TLRPC.Message paramMessage, AbstractMap<Integer, TLRPC.User> paramAbstractMap, boolean paramBoolean)
  {
    this(paramMessage, paramAbstractMap, null, paramBoolean);
  }

  public static void addLinks(boolean paramBoolean, CharSequence paramCharSequence)
  {
    addLinks(paramBoolean, paramCharSequence, true);
  }

  public static void addLinks(boolean paramBoolean1, CharSequence paramCharSequence, boolean paramBoolean2)
  {
    if ((!(paramCharSequence instanceof Spannable)) || (!containsUrls(paramCharSequence)) || (paramCharSequence.length() < 200));
    while (true)
    {
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 5);
        addUsernamesAndHashtags(paramBoolean1, paramCharSequence, paramBoolean2);
        return;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      try
      {
        Linkify.addLinks((Spannable)paramCharSequence, 1);
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }

  private static void addUsernamesAndHashtags(boolean paramBoolean1, CharSequence paramCharSequence, boolean paramBoolean2)
  {
    label154: label180: label186: 
    while (true)
    {
      int i;
      int j;
      try
      {
        if (urlPattern != null)
          continue;
        urlPattern = Pattern.compile("(^|\\s)/[a-zA-Z@\\d_]{1,255}|(^|\\s)@[a-zA-Z\\d_]{1,32}|(^|\\s)#[\\w\\.]+");
        Matcher localMatcher = urlPattern.matcher(paramCharSequence);
        if (localMatcher.find())
        {
          i = localMatcher.start();
          j = localMatcher.end();
          if ((paramCharSequence.charAt(i) == '@') || (paramCharSequence.charAt(i) == '#') || (paramCharSequence.charAt(i) == '/'))
            break label186;
          i += 1;
          if (paramCharSequence.charAt(i) != '/')
            break label154;
          if (!paramBoolean2)
            break label180;
          localObject = new URLSpanBotCommand(paramCharSequence.subSequence(i, j).toString(), paramBoolean1);
          if (localObject == null)
            continue;
          ((Spannable)paramCharSequence).setSpan(localObject, i, j, 0);
          continue;
        }
      }
      catch (Exception paramCharSequence)
      {
        FileLog.e(paramCharSequence);
      }
      return;
      Object localObject = new URLSpanNoUnderline(paramCharSequence.subSequence(i, j).toString());
      continue;
      localObject = null;
      continue;
    }
  }

  public static boolean canDeleteMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    int i = 0;
    if (paramMessage.id < 0);
    TLRPC.Chat localChat;
    label116: 
    do
    {
      do
      {
        do
        {
          return true;
          localChat = paramChat;
          if (paramChat == null)
          {
            localChat = paramChat;
            if (paramMessage.to_id.channel_id != 0)
              localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
          }
          if (!ChatObject.isChannel(localChat))
            break;
          if (paramMessage.id == 1)
            return false;
        }
        while (localChat.creator);
        if (!localChat.editor)
          break label116;
      }
      while ((isOut(paramMessage)) || ((paramMessage.from_id > 0) && (!paramMessage.post)));
      while (true)
      {
        if ((isOut(paramMessage)) || (!ChatObject.isChannel(localChat)))
          i = 1;
        return i;
        if (!localChat.moderator)
          break;
        if ((paramMessage.from_id > 0) && (!paramMessage.post))
          return true;
      }
    }
    while ((localChat.megagroup) && (isOut(paramMessage)) && (paramMessage.from_id > 0));
    return false;
  }

  public static boolean canEditMessage(TLRPC.Message paramMessage, TLRPC.Chat paramChat)
  {
    int j = 1;
    if ((paramMessage == null) || (paramMessage.to_id == null) || ((paramMessage.action != null) && (!(paramMessage.action instanceof TLRPC.TL_messageActionEmpty))) || (isForwardedMessage(paramMessage)) || (paramMessage.via_bot_id != 0) || (paramMessage.id < 0));
    label195: TLRPC.Chat localChat;
    do
      do
      {
        do
        {
          return false;
          if ((paramMessage.from_id == paramMessage.to_id.user_id) && (paramMessage.from_id == UserConfig.getClientUserId()))
            return true;
        }
        while (Math.abs(paramMessage.date - ConnectionsManager.getInstance().getCurrentTime()) > MessagesController.getInstance().maxEditTime);
        if (paramMessage.to_id.channel_id == 0)
        {
          if ((paramMessage.out) || (paramMessage.from_id == UserConfig.getClientUserId()))
          {
            i = j;
            if (!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto))
              if ((paramMessage.media instanceof TLRPC.TL_messageMediaDocument))
              {
                i = j;
                if (!isStickerMessage(paramMessage));
              }
              else
              {
                i = j;
                if (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty))
                {
                  i = j;
                  if (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
                    if (paramMessage.media != null)
                      break label195;
                }
              }
          }
          for (int i = j; ; i = 0)
            return i;
        }
        localChat = paramChat;
        if (paramChat != null)
          break;
        localChat = paramChat;
        if (paramMessage.to_id.channel_id == 0)
          break;
        localChat = MessagesController.getInstance().getChat(Integer.valueOf(paramMessage.to_id.channel_id));
      }
      while (localChat == null);
    while (((!localChat.megagroup) || (!paramMessage.out)) && ((localChat.megagroup) || ((!localChat.creator) && ((!localChat.editor) || (!isOut(paramMessage)))) || (!paramMessage.post) || ((!(paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && ((!(paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) || (isStickerMessage(paramMessage))) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessage.media instanceof TLRPC.TL_messageMediaWebPage)) && (paramMessage.media != null))));
    return true;
  }

  private static boolean containsUrls(CharSequence paramCharSequence)
  {
    int i7 = 1;
    int i6;
    if ((paramCharSequence == null) || (paramCharSequence.length() < 2) || (paramCharSequence.length() > 20480))
      i6 = 0;
    int i1;
    int i2;
    int m;
    int n;
    int i3;
    label58: int i4;
    int k;
    int i;
    int j;
    while (true)
    {
      return i6;
      int i5 = paramCharSequence.length();
      i1 = 0;
      i2 = 0;
      m = 0;
      n = 0;
      i3 = 0;
      if (i1 >= i5)
        break label344;
      i4 = paramCharSequence.charAt(i1);
      if ((i4 < 48) || (i4 > 57))
        break;
      k = i3 + 1;
      i6 = i7;
      if (k >= 6)
        continue;
      i = 0;
      j = 0;
      label108: if ((i4 == 64) || (i4 == 35) || (i4 == 47))
      {
        i6 = i7;
        if (i1 == 0)
          continue;
      }
      if (i1 != 0)
      {
        i6 = i7;
        if (paramCharSequence.charAt(i1 - 1) == ' ')
          continue;
        i6 = i7;
        if (paramCharSequence.charAt(i1 - 1) == '\n')
          continue;
      }
      if (i4 != 58)
        break label253;
      if (i != 0)
        break label248;
      i = 1;
    }
    while (true)
    {
      i1 += 1;
      i2 = i4;
      m = j;
      n = i;
      i3 = k;
      break label58;
      if (i4 != 32)
      {
        j = m;
        i = n;
        k = i3;
        if (i3 > 0)
          break label108;
      }
      k = 0;
      j = m;
      i = n;
      break label108;
      label248: i = 0;
      continue;
      label253: if (i4 == 47)
      {
        i6 = i7;
        if (i == 2)
          break;
        if (i == 1)
        {
          i += 1;
          continue;
        }
        i = 0;
        continue;
      }
      if (i4 == 46)
      {
        if ((j == 0) && (i2 != 32))
        {
          j += 1;
          continue;
        }
        j = 0;
        continue;
      }
      if ((i4 != 32) && (i2 == 46))
      {
        i6 = i7;
        if (j == 1)
          break;
      }
      j = 0;
    }
    label344: return false;
  }

  public static long getDialogId(TLRPC.Message paramMessage)
  {
    if ((paramMessage.dialog_id == 0L) && (paramMessage.to_id != null))
    {
      if (paramMessage.to_id.chat_id == 0)
        break label71;
      if (paramMessage.to_id.chat_id >= 0)
        break label55;
      paramMessage.dialog_id = AndroidUtilities.makeBroadcastId(paramMessage.to_id.chat_id);
    }
    while (true)
    {
      return paramMessage.dialog_id;
      label55: paramMessage.dialog_id = (-paramMessage.to_id.chat_id);
      continue;
      label71: if (paramMessage.to_id.channel_id != 0)
      {
        paramMessage.dialog_id = (-paramMessage.to_id.channel_id);
        continue;
      }
      if (isOut(paramMessage))
      {
        paramMessage.dialog_id = paramMessage.to_id.user_id;
        continue;
      }
      paramMessage.dialog_id = paramMessage.from_id;
    }
  }

  public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media != null) && (paramMessage.media.document != null))
    {
      paramMessage = paramMessage.media.document.attributes.iterator();
      while (paramMessage.hasNext())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramMessage.next();
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
          continue;
        if ((localDocumentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetEmpty))
          return null;
        return localDocumentAttribute.stickerset;
      }
    }
    return null;
  }

  public static int getUnreadFlags(TLRPC.Message paramMessage)
  {
    int i = 0;
    if (!paramMessage.unread)
      i = 1;
    int j = i;
    if (!paramMessage.media_unread)
      j = i | 0x2;
    return j;
  }

  public static boolean isContentUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.media_unread;
  }

  public static boolean isForwardedMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x4) != 0;
  }

  public static boolean isGameMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaGame;
  }

  public static boolean isGifDocument(TLRPC.Document paramDocument)
  {
    return (paramDocument != null) && (paramDocument.thumb != null) && (paramDocument.mime_type != null) && ((paramDocument.mime_type.equals("image/gif")) || (isNewGifDocument(paramDocument)));
  }

  public static boolean isImageWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    return (paramTL_webDocument != null) && (paramTL_webDocument.mime_type.startsWith("image/"));
  }

  public static boolean isInvoiceMessage(TLRPC.Message paramMessage)
  {
    return paramMessage.media instanceof TLRPC.TL_messageMediaInvoice;
  }

  public static boolean isMaskDocument(TLRPC.Document paramDocument)
  {
    int k = 0;
    int j = k;
    int i;
    if (paramDocument != null)
      i = 0;
    while (true)
    {
      j = k;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) && (localDocumentAttribute.mask))
          j = 1;
      }
      else
      {
        return j;
      }
      i += 1;
    }
  }

  public static boolean isMaskMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isMaskDocument(paramMessage.media.document));
  }

  public static boolean isMediaEmpty(TLRPC.Message paramMessage)
  {
    return (paramMessage == null) || (paramMessage.media == null) || ((paramMessage.media instanceof TLRPC.TL_messageMediaEmpty)) || ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage));
  }

  public static boolean isMegagroup(TLRPC.Message paramMessage)
  {
    return (paramMessage.flags & 0x80000000) != 0;
  }

  public static boolean isMusicDocument(TLRPC.Document paramDocument)
  {
    int k = 0;
    int j = k;
    int i;
    if (paramDocument != null)
      i = 0;
    while (true)
    {
      j = k;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
          break label58;
        j = k;
        if (!localDocumentAttribute.voice)
          j = 1;
      }
      return j;
      label58: i += 1;
    }
  }

  public static boolean isMusicMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      return isMusicDocument(paramMessage.media.webpage.document);
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isMusicDocument(paramMessage.media.document));
  }

  public static boolean isNewGifDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      int j = 0;
      int n = 0;
      int k = 0;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        int m;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
          m = 1;
        while (true)
        {
          i += 1;
          j = m;
          break;
          m = j;
          if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
            continue;
          k = localDocumentAttribute.w;
          n = localDocumentAttribute.w;
          m = j;
        }
      }
      if ((j != 0) && (k <= 1280) && (n <= 1280))
        return true;
    }
    return false;
  }

  public static boolean isNewGifMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      return isNewGifDocument(paramMessage.media.webpage.document);
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isNewGifDocument(paramMessage.media.document));
  }

  public static boolean isOut(TLRPC.Message paramMessage)
  {
    return paramMessage.out;
  }

  public static boolean isStickerDocument(TLRPC.Document paramDocument)
  {
    int k = 0;
    int j = k;
    int i;
    if (paramDocument != null)
      i = 0;
    while (true)
    {
      j = k;
      if (i < paramDocument.attributes.size())
      {
        if (((TLRPC.DocumentAttribute)paramDocument.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker))
          j = 1;
      }
      else
        return j;
      i += 1;
    }
  }

  public static boolean isStickerMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isStickerDocument(paramMessage.media.document));
  }

  public static boolean isUnread(TLRPC.Message paramMessage)
  {
    return paramMessage.unread;
  }

  public static boolean isVideoDocument(TLRPC.Document paramDocument)
  {
    if (paramDocument != null)
    {
      int k = 0;
      int i1 = 0;
      int m = 0;
      int j = 0;
      int i = 0;
      if (k < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(k);
        int i2;
        int i3;
        int n;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
        {
          i2 = localDocumentAttribute.w;
          i3 = localDocumentAttribute.h;
          n = 1;
        }
        while (true)
        {
          k += 1;
          i1 = i3;
          m = i2;
          j = n;
          break;
          i3 = i1;
          i2 = m;
          n = j;
          if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
            continue;
          i = 1;
          i3 = i1;
          i2 = m;
          n = j;
        }
      }
      k = i;
      if (i != 0)
        if (m <= 1280)
        {
          k = i;
          if (i1 <= 1280);
        }
        else
        {
          k = 0;
        }
      return (j != 0) && (k == 0);
    }
    return false;
  }

  public static boolean isVideoMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      return isVideoDocument(paramMessage.media.webpage.document);
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVideoDocument(paramMessage.media.document));
  }

  public static boolean isVideoVoiceMessage(TLRPC.Message paramMessage)
  {
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVoiceVideoDocument(paramMessage.media.document));
  }

  public static boolean isVideoWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    return (paramTL_webDocument != null) && (paramTL_webDocument.mime_type.startsWith("video/"));
  }

  public static boolean isVoiceDocument(TLRPC.Document paramDocument)
  {
    boolean bool2 = false;
    boolean bool1 = bool2;
    int i;
    if (paramDocument != null)
      i = 0;
    while (true)
    {
      bool1 = bool2;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
          bool1 = localDocumentAttribute.voice;
      }
      else
      {
        return bool1;
      }
      i += 1;
    }
  }

  public static boolean isVoiceMessage(TLRPC.Message paramMessage)
  {
    if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      return isVoiceDocument(paramMessage.media.webpage.document);
    return (paramMessage.media != null) && (paramMessage.media.document != null) && (isVoiceDocument(paramMessage.media.document));
  }

  public static boolean isVoiceVideoDocument(TLRPC.Document paramDocument)
  {
    if ((paramDocument != null) && (paramDocument.mime_type != null) && (paramDocument.mime_type.equals("video/mp4")))
    {
      int i = 0;
      int j = 0;
      int n = 0;
      int k = 0;
      if (i < paramDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
        int m;
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAnimated))
          m = 1;
        while (true)
        {
          i += 1;
          j = m;
          break;
          m = j;
          if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeVideo))
            continue;
          k = localDocumentAttribute.w;
          n = localDocumentAttribute.w;
          m = j;
        }
      }
      if ((j != 0) && (k <= 1280) && (n <= 1280))
        return true;
    }
    return false;
  }

  public static boolean isVoiceWebDocument(TLRPC.TL_webDocument paramTL_webDocument)
  {
    return (paramTL_webDocument != null) && (paramTL_webDocument.mime_type.equals("audio/ogg"));
  }

  public static void setUnreadFlags(TLRPC.Message paramMessage, int paramInt)
  {
    boolean bool2 = true;
    if ((paramInt & 0x1) == 0)
    {
      bool1 = true;
      paramMessage.unread = bool1;
      if ((paramInt & 0x2) != 0)
        break label34;
    }
    label34: for (boolean bool1 = bool2; ; bool1 = false)
    {
      paramMessage.media_unread = bool1;
      return;
      bool1 = false;
      break;
    }
  }

  public void applyNewText()
  {
    if (TextUtils.isEmpty(this.messageOwner.message))
      return;
    TLRPC.User localUser = null;
    if (isFromUser())
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
    this.messageText = this.messageOwner.message;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame));
    for (TextPaint localTextPaint = Theme.chat_msgGameTextPaint; ; localTextPaint = Theme.chat_msgTextPaint)
    {
      this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
      generateLayout(localUser);
      return;
    }
  }

  public boolean canDeleteMessage(TLRPC.Chat paramChat)
  {
    return canDeleteMessage(this.messageOwner, paramChat);
  }

  public boolean canEditMessage(TLRPC.Chat paramChat)
  {
    return canEditMessage(this.messageOwner, paramChat);
  }

  public boolean checkLayout()
  {
    if ((this.type != 0) || (this.messageOwner.to_id == null) || (this.messageText == null) || (this.messageText.length() == 0))
      return false;
    int i;
    if (this.layoutCreated)
    {
      if (!AndroidUtilities.isTablet())
        break label161;
      i = AndroidUtilities.getMinTabletSide();
      if (Math.abs(this.generatedWithMinSize - i) > AndroidUtilities.dp(52.0F))
        this.layoutCreated = false;
    }
    if (!this.layoutCreated)
    {
      this.layoutCreated = true;
      TLRPC.User localUser = null;
      if (isFromUser())
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame));
      for (TextPaint localTextPaint = Theme.chat_msgGameTextPaint; ; localTextPaint = Theme.chat_msgTextPaint)
      {
        this.messageText = Emoji.replaceEmoji(this.messageText, localTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        generateLayout(localUser);
        return true;
        label161: i = AndroidUtilities.displaySize.x;
        break;
      }
    }
    return false;
  }

  public void checkMediaExistance()
  {
    this.attachPathExists = false;
    this.mediaExists = false;
    if (this.type == 1)
      if (FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null)
        this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
    Object localObject;
    do
    {
      do
      {
        while (true)
        {
          return;
          if ((this.type != 8) && (this.type != 3) && (this.type != 9) && (this.type != 2) && (this.type != 14))
            break;
          if ((this.messageOwner.attachPath != null) && (this.messageOwner.attachPath.length() > 0))
            this.attachPathExists = new File(this.messageOwner.attachPath).exists();
          if (this.attachPathExists)
            continue;
          this.mediaExists = FileLoader.getPathToMessage(this.messageOwner).exists();
          return;
        }
        localObject = getDocument();
        if (localObject == null)
          continue;
        this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject).exists();
        return;
      }
      while (this.type != 0);
      localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
    }
    while ((localObject == null) || (localObject == null));
    this.mediaExists = FileLoader.getPathToAttach((TLObject)localObject, true).exists();
  }

  public void generateCaption()
  {
    if (this.caption != null);
    do
    {
      do
        return;
      while ((this.messageOwner.media == null) || (this.messageOwner.media.caption == null) || (this.messageOwner.media.caption.length() <= 0));
      this.caption = Emoji.replaceEmoji(this.messageOwner.media.caption, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
    }
    while (!containsUrls(this.caption));
    try
    {
      Linkify.addLinks((Spannable)this.caption, 5);
      addUsernamesAndHashtags(isOutOwner(), this.caption, true);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void generateGameMessageText(TLRPC.User paramUser)
  {
    TLRPC.User localUser = paramUser;
    if (paramUser == null)
    {
      localUser = paramUser;
      if (this.messageOwner.from_id > 0)
        localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
    }
    Object localObject = null;
    paramUser = localObject;
    if (this.replyMessageObject != null)
    {
      paramUser = localObject;
      if (this.replyMessageObject.messageOwner.media != null)
      {
        paramUser = localObject;
        if (this.replyMessageObject.messageOwner.media.game != null)
          paramUser = this.replyMessageObject.messageOwner.media.game;
      }
    }
    if (paramUser == null)
    {
      if ((localUser != null) && (localUser.id == UserConfig.getClientUserId()))
      {
        this.messageText = LocaleController.formatString("ActionYouScored", 2131165271, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) });
        return;
      }
      this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScored", 2131165262, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser);
      return;
    }
    if ((localUser != null) && (localUser.id == UserConfig.getClientUserId()));
    for (this.messageText = LocaleController.formatString("ActionYouScoredInGame", 2131165272, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }); ; this.messageText = replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", 2131165263, new Object[] { LocaleController.formatPluralString("Points", this.messageOwner.action.score) }), "un1", localUser))
    {
      this.messageText = replaceWithLink(this.messageText, "un2", paramUser);
      return;
    }
  }

  // ERROR //
  public void generateLayout(TLRPC.User paramUser)
  {
    // Byte code:
    //   0: aload_0
    //   1: getfield 69	org/vidogram/messenger/MessageObject:type	I
    //   4: ifne +32 -> 36
    //   7: aload_0
    //   8: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   11: getfield 314	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   14: ifnull +22 -> 36
    //   17: aload_0
    //   18: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   21: ifnull +15 -> 36
    //   24: aload_0
    //   25: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   28: invokeinterface 244 1 0
    //   33: ifne +4 -> 37
    //   36: return
    //   37: aload_0
    //   38: invokevirtual 1114	org/vidogram/messenger/MessageObject:generateLinkDescription	()V
    //   41: aload_0
    //   42: new 301	java/util/ArrayList
    //   45: dup
    //   46: invokespecial 1115	java/util/ArrayList:<init>	()V
    //   49: putfield 1117	org/vidogram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   52: aload_0
    //   53: iconst_0
    //   54: putfield 1119	org/vidogram/messenger/MessageObject:textWidth	I
    //   57: aload_0
    //   58: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   61: getfield 1122	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   64: ifeq +324 -> 388
    //   67: iconst_0
    //   68: istore 7
    //   70: iload 7
    //   72: aload_0
    //   73: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   76: getfield 1125	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   79: invokevirtual 304	java/util/ArrayList:size	()I
    //   82: if_icmpge +2567 -> 2649
    //   85: aload_0
    //   86: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   89: getfield 1125	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   92: iload 7
    //   94: invokevirtual 307	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   97: instanceof 1127
    //   100: ifne +279 -> 379
    //   103: iconst_1
    //   104: istore 7
    //   106: iload 7
    //   108: ifne +305 -> 413
    //   111: aload_0
    //   112: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   115: instanceof 1129
    //   118: ifne +116 -> 234
    //   121: aload_0
    //   122: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   125: instanceof 1131
    //   128: ifne +106 -> 234
    //   131: aload_0
    //   132: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   135: instanceof 1133
    //   138: ifne +96 -> 234
    //   141: aload_0
    //   142: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   145: instanceof 1135
    //   148: ifne +86 -> 234
    //   151: aload_0
    //   152: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   155: instanceof 1137
    //   158: ifne +76 -> 234
    //   161: aload_0
    //   162: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   165: instanceof 1139
    //   168: ifne +66 -> 234
    //   171: aload_0
    //   172: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   175: instanceof 1141
    //   178: ifne +56 -> 234
    //   181: aload_0
    //   182: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   185: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   188: instanceof 653
    //   191: ifne +43 -> 234
    //   194: aload_0
    //   195: invokevirtual 125	org/vidogram/messenger/MessageObject:isOut	()Z
    //   198: ifeq +13 -> 211
    //   201: aload_0
    //   202: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   205: getfield 1122	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   208: ifne +26 -> 234
    //   211: aload_0
    //   212: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   215: getfield 181	org/vidogram/tgnet/TLRPC$Message:id	I
    //   218: iflt +16 -> 234
    //   221: aload_0
    //   222: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   225: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   228: instanceof 660
    //   231: ifeq +182 -> 413
    //   234: iconst_1
    //   235: istore 7
    //   237: iload 7
    //   239: ifeq +180 -> 419
    //   242: aload_0
    //   243: invokevirtual 1084	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   246: aload_0
    //   247: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   250: invokestatic 1143	org/vidogram/messenger/MessageObject:addLinks	(ZLjava/lang/CharSequence;)V
    //   253: aload_0
    //   254: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   257: instanceof 241
    //   260: ifeq +1208 -> 1468
    //   263: aload_0
    //   264: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   267: checkcast 241	android/text/Spannable
    //   270: astore 21
    //   272: aload_0
    //   273: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   276: getfield 1125	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   279: invokevirtual 304	java/util/ArrayList:size	()I
    //   282: istore 10
    //   284: aload 21
    //   286: iconst_0
    //   287: aload_0
    //   288: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   291: invokeinterface 244 1 0
    //   296: ldc_w 1145
    //   299: invokeinterface 250 4 0
    //   304: checkcast 1147	[Landroid/text/style/URLSpan;
    //   307: astore 22
    //   309: iconst_0
    //   310: istore 8
    //   312: iload 8
    //   314: iload 10
    //   316: if_icmpge +1152 -> 1468
    //   319: aload_0
    //   320: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   323: getfield 1125	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   326: iload 8
    //   328: invokevirtual 307	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   331: checkcast 1149	org/vidogram/tgnet/TLRPC$MessageEntity
    //   334: astore 23
    //   336: aload 23
    //   338: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   341: ifle +29 -> 370
    //   344: aload 23
    //   346: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   349: iflt +21 -> 370
    //   352: aload 23
    //   354: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   357: aload_0
    //   358: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   361: getfield 178	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   364: invokevirtual 185	java/lang/String:length	()I
    //   367: if_icmplt +102 -> 469
    //   370: iload 8
    //   372: iconst_1
    //   373: iadd
    //   374: istore 8
    //   376: goto -64 -> 312
    //   379: iload 7
    //   381: iconst_1
    //   382: iadd
    //   383: istore 7
    //   385: goto -315 -> 70
    //   388: aload_0
    //   389: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   392: getfield 1125	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   395: invokevirtual 1156	java/util/ArrayList:isEmpty	()Z
    //   398: ifne +9 -> 407
    //   401: iconst_1
    //   402: istore 7
    //   404: goto -298 -> 106
    //   407: iconst_0
    //   408: istore 7
    //   410: goto -304 -> 106
    //   413: iconst_0
    //   414: istore 7
    //   416: goto -179 -> 237
    //   419: aload_0
    //   420: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   423: instanceof 241
    //   426: ifeq -173 -> 253
    //   429: aload_0
    //   430: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   433: invokeinterface 244 1 0
    //   438: sipush 200
    //   441: if_icmpge -188 -> 253
    //   444: aload_0
    //   445: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   448: checkcast 241	android/text/Spannable
    //   451: iconst_4
    //   452: invokestatic 745	android/text/util/Linkify:addLinks	(Landroid/text/Spannable;I)Z
    //   455: pop
    //   456: goto -203 -> 253
    //   459: astore 21
    //   461: aload 21
    //   463: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   466: goto -213 -> 253
    //   469: aload 23
    //   471: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   474: aload 23
    //   476: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   479: iadd
    //   480: aload_0
    //   481: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   484: getfield 178	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   487: invokevirtual 185	java/lang/String:length	()I
    //   490: if_icmple +24 -> 514
    //   493: aload 23
    //   495: aload_0
    //   496: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   499: getfield 178	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   502: invokevirtual 185	java/lang/String:length	()I
    //   505: aload 23
    //   507: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   510: isub
    //   511: putfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   514: aload 22
    //   516: ifnull +138 -> 654
    //   519: aload 22
    //   521: arraylength
    //   522: ifle +132 -> 654
    //   525: iconst_0
    //   526: istore 9
    //   528: iload 9
    //   530: aload 22
    //   532: arraylength
    //   533: if_icmpge +121 -> 654
    //   536: aload 22
    //   538: iload 9
    //   540: aaload
    //   541: ifnonnull +12 -> 553
    //   544: iload 9
    //   546: iconst_1
    //   547: iadd
    //   548: istore 9
    //   550: goto -22 -> 528
    //   553: aload 21
    //   555: aload 22
    //   557: iload 9
    //   559: aaload
    //   560: invokeinterface 1160 2 0
    //   565: istore 11
    //   567: aload 21
    //   569: aload 22
    //   571: iload 9
    //   573: aaload
    //   574: invokeinterface 1163 2 0
    //   579: istore 12
    //   581: aload 23
    //   583: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   586: iload 11
    //   588: if_icmpgt +19 -> 607
    //   591: aload 23
    //   593: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   596: aload 23
    //   598: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   601: iadd
    //   602: iload 11
    //   604: if_icmpge +29 -> 633
    //   607: aload 23
    //   609: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   612: iload 12
    //   614: if_icmpgt -70 -> 544
    //   617: aload 23
    //   619: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   622: aload 23
    //   624: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   627: iadd
    //   628: iload 12
    //   630: if_icmplt -86 -> 544
    //   633: aload 21
    //   635: aload 22
    //   637: iload 9
    //   639: aaload
    //   640: invokeinterface 1167 2 0
    //   645: aload 22
    //   647: iload 9
    //   649: aconst_null
    //   650: aastore
    //   651: goto -107 -> 544
    //   654: aload 23
    //   656: instanceof 1169
    //   659: ifeq +56 -> 715
    //   662: new 586	org/vidogram/ui/Components/TypefaceSpan
    //   665: dup
    //   666: ldc_w 1171
    //   669: invokestatic 1175	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   672: invokespecial 595	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   675: astore 24
    //   677: aload 23
    //   679: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   682: istore 9
    //   684: aload 23
    //   686: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   689: istore 11
    //   691: aload 21
    //   693: aload 24
    //   695: iload 9
    //   697: aload 23
    //   699: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   702: iload 11
    //   704: iadd
    //   705: bipush 33
    //   707: invokeinterface 790 5 0
    //   712: goto -342 -> 370
    //   715: aload 23
    //   717: instanceof 1177
    //   720: ifeq +56 -> 776
    //   723: new 586	org/vidogram/ui/Components/TypefaceSpan
    //   726: dup
    //   727: ldc_w 1179
    //   730: invokestatic 1175	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   733: invokespecial 595	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   736: astore 24
    //   738: aload 23
    //   740: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   743: istore 9
    //   745: aload 23
    //   747: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   750: istore 11
    //   752: aload 21
    //   754: aload 24
    //   756: iload 9
    //   758: aload 23
    //   760: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   763: iload 11
    //   765: iadd
    //   766: bipush 33
    //   768: invokeinterface 790 5 0
    //   773: goto -403 -> 370
    //   776: aload 23
    //   778: instanceof 1181
    //   781: ifne +11 -> 792
    //   784: aload 23
    //   786: instanceof 1183
    //   789: ifeq +72 -> 861
    //   792: new 1185	org/vidogram/ui/Components/URLSpanMono
    //   795: dup
    //   796: aload 21
    //   798: aload 23
    //   800: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   803: aload 23
    //   805: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   808: aload 23
    //   810: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   813: iadd
    //   814: aload_0
    //   815: invokevirtual 1084	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   818: invokespecial 1188	org/vidogram/ui/Components/URLSpanMono:<init>	(Ljava/lang/CharSequence;IIZ)V
    //   821: astore 24
    //   823: aload 23
    //   825: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   828: istore 9
    //   830: aload 23
    //   832: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   835: istore 11
    //   837: aload 21
    //   839: aload 24
    //   841: iload 9
    //   843: aload 23
    //   845: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   848: iload 11
    //   850: iadd
    //   851: bipush 33
    //   853: invokeinterface 790 5 0
    //   858: goto -488 -> 370
    //   861: aload 23
    //   863: instanceof 1190
    //   866: ifeq +80 -> 946
    //   869: new 1192	org/vidogram/ui/Components/URLSpanUserMention
    //   872: dup
    //   873: new 467	java/lang/StringBuilder
    //   876: dup
    //   877: invokespecial 468	java/lang/StringBuilder:<init>	()V
    //   880: ldc 138
    //   882: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   885: aload 23
    //   887: checkcast 1190	org/vidogram/tgnet/TLRPC$TL_messageEntityMentionName
    //   890: getfield 1193	org/vidogram/tgnet/TLRPC$TL_messageEntityMentionName:user_id	I
    //   893: invokevirtual 475	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   896: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   899: aload_0
    //   900: invokevirtual 1084	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   903: invokespecial 1194	org/vidogram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;Z)V
    //   906: astore 24
    //   908: aload 23
    //   910: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   913: istore 9
    //   915: aload 23
    //   917: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   920: istore 11
    //   922: aload 21
    //   924: aload 24
    //   926: iload 9
    //   928: aload 23
    //   930: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   933: iload 11
    //   935: iadd
    //   936: bipush 33
    //   938: invokeinterface 790 5 0
    //   943: goto -573 -> 370
    //   946: aload 23
    //   948: instanceof 1127
    //   951: ifeq +83 -> 1034
    //   954: new 1192	org/vidogram/ui/Components/URLSpanUserMention
    //   957: dup
    //   958: new 467	java/lang/StringBuilder
    //   961: dup
    //   962: invokespecial 468	java/lang/StringBuilder:<init>	()V
    //   965: ldc 138
    //   967: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   970: aload 23
    //   972: checkcast 1127	org/vidogram/tgnet/TLRPC$TL_inputMessageEntityMentionName
    //   975: getfield 1197	org/vidogram/tgnet/TLRPC$TL_inputMessageEntityMentionName:user_id	Lorg/vidogram/tgnet/TLRPC$InputUser;
    //   978: getfield 1200	org/vidogram/tgnet/TLRPC$InputUser:user_id	I
    //   981: invokevirtual 475	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   984: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   987: aload_0
    //   988: invokevirtual 1084	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   991: invokespecial 1194	org/vidogram/ui/Components/URLSpanUserMention:<init>	(Ljava/lang/String;Z)V
    //   994: astore 24
    //   996: aload 23
    //   998: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1001: istore 9
    //   1003: aload 23
    //   1005: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1008: istore 11
    //   1010: aload 21
    //   1012: aload 24
    //   1014: iload 9
    //   1016: aload 23
    //   1018: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1021: iload 11
    //   1023: iadd
    //   1024: bipush 33
    //   1026: invokeinterface 790 5 0
    //   1031: goto -661 -> 370
    //   1034: iload 7
    //   1036: ifne -666 -> 370
    //   1039: aload_0
    //   1040: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1043: getfield 178	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1046: aload 23
    //   1048: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1051: aload 23
    //   1053: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1056: aload 23
    //   1058: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1061: iadd
    //   1062: invokevirtual 1204	java/lang/String:substring	(II)Ljava/lang/String;
    //   1065: astore 24
    //   1067: aload 23
    //   1069: instanceof 1206
    //   1072: ifeq +56 -> 1128
    //   1075: new 782	org/vidogram/ui/Components/URLSpanBotCommand
    //   1078: dup
    //   1079: aload 24
    //   1081: aload_0
    //   1082: invokevirtual 1084	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   1085: invokespecial 789	org/vidogram/ui/Components/URLSpanBotCommand:<init>	(Ljava/lang/String;Z)V
    //   1088: astore 24
    //   1090: aload 23
    //   1092: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1095: istore 9
    //   1097: aload 23
    //   1099: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1102: istore 11
    //   1104: aload 21
    //   1106: aload 24
    //   1108: iload 9
    //   1110: aload 23
    //   1112: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1115: iload 11
    //   1117: iadd
    //   1118: bipush 33
    //   1120: invokeinterface 790 5 0
    //   1125: goto -755 -> 370
    //   1128: aload 23
    //   1130: instanceof 1208
    //   1133: ifne +11 -> 1144
    //   1136: aload 23
    //   1138: instanceof 1210
    //   1141: ifeq +52 -> 1193
    //   1144: new 792	org/vidogram/ui/Components/URLSpanNoUnderline
    //   1147: dup
    //   1148: aload 24
    //   1150: invokespecial 795	org/vidogram/ui/Components/URLSpanNoUnderline:<init>	(Ljava/lang/String;)V
    //   1153: astore 24
    //   1155: aload 23
    //   1157: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1160: istore 9
    //   1162: aload 23
    //   1164: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1167: istore 11
    //   1169: aload 21
    //   1171: aload 24
    //   1173: iload 9
    //   1175: aload 23
    //   1177: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1180: iload 11
    //   1182: iadd
    //   1183: bipush 33
    //   1185: invokeinterface 790 5 0
    //   1190: goto -820 -> 370
    //   1193: aload 23
    //   1195: instanceof 1212
    //   1198: ifeq +71 -> 1269
    //   1201: new 1214	org/vidogram/ui/Components/URLSpanReplacement
    //   1204: dup
    //   1205: new 467	java/lang/StringBuilder
    //   1208: dup
    //   1209: invokespecial 468	java/lang/StringBuilder:<init>	()V
    //   1212: ldc_w 1216
    //   1215: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1218: aload 24
    //   1220: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1223: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1226: invokespecial 1217	org/vidogram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1229: astore 24
    //   1231: aload 23
    //   1233: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1236: istore 9
    //   1238: aload 23
    //   1240: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1243: istore 11
    //   1245: aload 21
    //   1247: aload 24
    //   1249: iload 9
    //   1251: aload 23
    //   1253: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1256: iload 11
    //   1258: iadd
    //   1259: bipush 33
    //   1261: invokeinterface 790 5 0
    //   1266: goto -896 -> 370
    //   1269: aload 23
    //   1271: instanceof 1219
    //   1274: ifeq +134 -> 1408
    //   1277: aload 24
    //   1279: invokevirtual 1222	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1282: ldc_w 1224
    //   1285: invokevirtual 943	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1288: ifne +71 -> 1359
    //   1291: new 1145	android/text/style/URLSpan
    //   1294: dup
    //   1295: new 467	java/lang/StringBuilder
    //   1298: dup
    //   1299: invokespecial 468	java/lang/StringBuilder:<init>	()V
    //   1302: ldc_w 1226
    //   1305: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1308: aload 24
    //   1310: invokevirtual 472	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1313: invokevirtual 479	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1316: invokespecial 1227	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1319: astore 24
    //   1321: aload 23
    //   1323: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1326: istore 9
    //   1328: aload 23
    //   1330: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1333: istore 11
    //   1335: aload 21
    //   1337: aload 24
    //   1339: iload 9
    //   1341: aload 23
    //   1343: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1346: iload 11
    //   1348: iadd
    //   1349: bipush 33
    //   1351: invokeinterface 790 5 0
    //   1356: goto -986 -> 370
    //   1359: new 1145	android/text/style/URLSpan
    //   1362: dup
    //   1363: aload 24
    //   1365: invokespecial 1227	android/text/style/URLSpan:<init>	(Ljava/lang/String;)V
    //   1368: astore 24
    //   1370: aload 23
    //   1372: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1375: istore 9
    //   1377: aload 23
    //   1379: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1382: istore 11
    //   1384: aload 21
    //   1386: aload 24
    //   1388: iload 9
    //   1390: aload 23
    //   1392: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1395: iload 11
    //   1397: iadd
    //   1398: bipush 33
    //   1400: invokeinterface 790 5 0
    //   1405: goto -1035 -> 370
    //   1408: aload 23
    //   1410: instanceof 1229
    //   1413: ifeq -1043 -> 370
    //   1416: new 1214	org/vidogram/ui/Components/URLSpanReplacement
    //   1419: dup
    //   1420: aload 23
    //   1422: getfield 1232	org/vidogram/tgnet/TLRPC$MessageEntity:url	Ljava/lang/String;
    //   1425: invokespecial 1217	org/vidogram/ui/Components/URLSpanReplacement:<init>	(Ljava/lang/String;)V
    //   1428: astore 24
    //   1430: aload 23
    //   1432: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1435: istore 9
    //   1437: aload 23
    //   1439: getfield 1154	org/vidogram/tgnet/TLRPC$MessageEntity:offset	I
    //   1442: istore 11
    //   1444: aload 21
    //   1446: aload 24
    //   1448: iload 9
    //   1450: aload 23
    //   1452: getfield 1151	org/vidogram/tgnet/TLRPC$MessageEntity:length	I
    //   1455: iload 11
    //   1457: iadd
    //   1458: bipush 33
    //   1460: invokeinterface 790 5 0
    //   1465: goto -1095 -> 370
    //   1468: aload_0
    //   1469: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1472: getfield 89	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   1475: ifle +642 -> 2117
    //   1478: aload_0
    //   1479: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1482: getfield 314	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   1485: getfield 319	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   1488: ifne +42 -> 1530
    //   1491: aload_0
    //   1492: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1495: getfield 314	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   1498: getfield 868	org/vidogram/tgnet/TLRPC$Peer:chat_id	I
    //   1501: ifne +29 -> 1530
    //   1504: aload_0
    //   1505: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1508: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1511: instanceof 209
    //   1514: ifne +16 -> 1530
    //   1517: aload_0
    //   1518: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1521: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1524: instanceof 653
    //   1527: ifeq +590 -> 2117
    //   1530: aload_0
    //   1531: invokevirtual 125	org/vidogram/messenger/MessageObject:isOut	()Z
    //   1534: ifne +583 -> 2117
    //   1537: iconst_1
    //   1538: istore 7
    //   1540: invokestatic 1025	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   1543: ifeq +580 -> 2123
    //   1546: invokestatic 1028	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1549: istore 8
    //   1551: aload_0
    //   1552: iload 8
    //   1554: putfield 1030	org/vidogram/messenger/MessageObject:generatedWithMinSize	I
    //   1557: aload_0
    //   1558: getfield 1030	org/vidogram/messenger/MessageObject:generatedWithMinSize	I
    //   1561: istore 8
    //   1563: iload 7
    //   1565: ifeq +569 -> 2134
    //   1568: ldc_w 1233
    //   1571: fstore_2
    //   1572: iload 8
    //   1574: fload_2
    //   1575: invokestatic 229	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1578: isub
    //   1579: istore 8
    //   1581: aload_1
    //   1582: ifnull +10 -> 1592
    //   1585: aload_1
    //   1586: getfield 1236	org/vidogram/tgnet/TLRPC$User:bot	Z
    //   1589: ifne +52 -> 1641
    //   1592: aload_0
    //   1593: invokevirtual 322	org/vidogram/messenger/MessageObject:isMegagroup	()Z
    //   1596: ifne +34 -> 1630
    //   1599: iload 8
    //   1601: istore 7
    //   1603: aload_0
    //   1604: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1607: getfield 1240	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1610: ifnull +41 -> 1651
    //   1613: iload 8
    //   1615: istore 7
    //   1617: aload_0
    //   1618: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1621: getfield 1240	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   1624: getfield 1243	org/vidogram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   1627: ifeq +24 -> 1651
    //   1630: iload 8
    //   1632: istore 7
    //   1634: aload_0
    //   1635: invokevirtual 125	org/vidogram/messenger/MessageObject:isOut	()Z
    //   1638: ifne +13 -> 1651
    //   1641: iload 8
    //   1643: ldc 223
    //   1645: invokestatic 229	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1648: isub
    //   1649: istore 7
    //   1651: aload_0
    //   1652: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1655: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1658: instanceof 209
    //   1661: ifeq +981 -> 2642
    //   1664: iload 7
    //   1666: ldc_w 1244
    //   1669: invokestatic 229	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1672: isub
    //   1673: istore 9
    //   1675: aload_0
    //   1676: getfield 77	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1679: getfield 207	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1682: instanceof 209
    //   1685: ifeq +456 -> 2141
    //   1688: getstatic 213	org/vidogram/ui/ActionBar/Theme:chat_msgGameTextPaint	Landroid/text/TextPaint;
    //   1691: astore_1
    //   1692: new 1246	android/text/StaticLayout
    //   1695: dup
    //   1696: aload_0
    //   1697: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   1700: aload_1
    //   1701: iload 9
    //   1703: getstatic 1252	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1706: fconst_1
    //   1707: fconst_0
    //   1708: iconst_0
    //   1709: invokespecial 1255	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1712: astore 21
    //   1714: aload_0
    //   1715: aload 21
    //   1717: invokevirtual 1258	android/text/StaticLayout:getHeight	()I
    //   1720: putfield 1260	org/vidogram/messenger/MessageObject:textHeight	I
    //   1723: aload 21
    //   1725: invokevirtual 1263	android/text/StaticLayout:getLineCount	()I
    //   1728: istore 17
    //   1730: iload 17
    //   1732: i2f
    //   1733: ldc_w 1244
    //   1736: fdiv
    //   1737: f2d
    //   1738: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   1741: d2i
    //   1742: istore 18
    //   1744: iconst_0
    //   1745: istore 7
    //   1747: fconst_0
    //   1748: fstore_2
    //   1749: iconst_0
    //   1750: istore 10
    //   1752: iload 10
    //   1754: iload 18
    //   1756: if_icmpge -1720 -> 36
    //   1759: bipush 10
    //   1761: iload 17
    //   1763: iload 7
    //   1765: isub
    //   1766: invokestatic 1271	java/lang/Math:min	(II)I
    //   1769: istore 8
    //   1771: new 6	org/vidogram/messenger/MessageObject$TextLayoutBlock
    //   1774: dup
    //   1775: invokespecial 1272	org/vidogram/messenger/MessageObject$TextLayoutBlock:<init>	()V
    //   1778: astore 22
    //   1780: iload 18
    //   1782: iconst_1
    //   1783: if_icmpne +371 -> 2154
    //   1786: aload 22
    //   1788: aload 21
    //   1790: putfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1793: aload 22
    //   1795: fconst_0
    //   1796: putfield 1279	org/vidogram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   1799: aload 22
    //   1801: iconst_0
    //   1802: putfield 1282	org/vidogram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   1805: aload 22
    //   1807: aload_0
    //   1808: getfield 1260	org/vidogram/messenger/MessageObject:textHeight	I
    //   1811: putfield 1285	org/vidogram/messenger/MessageObject$TextLayoutBlock:height	I
    //   1814: fload_2
    //   1815: fstore_3
    //   1816: iload 8
    //   1818: istore 11
    //   1820: aload_0
    //   1821: getfield 1117	org/vidogram/messenger/MessageObject:textLayoutBlocks	Ljava/util/ArrayList;
    //   1824: aload 22
    //   1826: invokevirtual 1288	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   1829: pop
    //   1830: aload 22
    //   1832: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1835: iload 11
    //   1837: iconst_1
    //   1838: isub
    //   1839: invokevirtual 1292	android/text/StaticLayout:getLineLeft	(I)F
    //   1842: fstore_2
    //   1843: iload 10
    //   1845: ifne +8 -> 1853
    //   1848: aload_0
    //   1849: fload_2
    //   1850: putfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   1853: aload 22
    //   1855: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1858: iload 11
    //   1860: iconst_1
    //   1861: isub
    //   1862: invokevirtual 1297	android/text/StaticLayout:getLineWidth	(I)F
    //   1865: fstore 4
    //   1867: fload 4
    //   1869: f2d
    //   1870: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   1873: d2i
    //   1874: istore 14
    //   1876: iload 10
    //   1878: iload 18
    //   1880: iconst_1
    //   1881: isub
    //   1882: if_icmpne +9 -> 1891
    //   1885: aload_0
    //   1886: iload 14
    //   1888: putfield 1299	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   1891: fload 4
    //   1893: fload_2
    //   1894: fadd
    //   1895: f2d
    //   1896: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   1899: d2i
    //   1900: istore 16
    //   1902: iload 11
    //   1904: iconst_1
    //   1905: if_icmple +642 -> 2547
    //   1908: fconst_0
    //   1909: fstore 4
    //   1911: fconst_0
    //   1912: fstore_2
    //   1913: iconst_0
    //   1914: istore 12
    //   1916: iload 16
    //   1918: istore 13
    //   1920: iconst_0
    //   1921: istore 15
    //   1923: iload 12
    //   1925: iload 11
    //   1927: if_icmpge +550 -> 2477
    //   1930: aload 22
    //   1932: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1935: iload 12
    //   1937: invokevirtual 1297	android/text/StaticLayout:getLineWidth	(I)F
    //   1940: fstore 5
    //   1942: fload 5
    //   1944: iload 9
    //   1946: bipush 20
    //   1948: iadd
    //   1949: i2f
    //   1950: fcmpl
    //   1951: ifle +688 -> 2639
    //   1954: iload 9
    //   1956: i2f
    //   1957: fstore 5
    //   1959: aload 22
    //   1961: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   1964: iload 12
    //   1966: invokevirtual 1292	android/text/StaticLayout:getLineLeft	(I)F
    //   1969: fstore 6
    //   1971: fload 6
    //   1973: fconst_0
    //   1974: fcmpl
    //   1975: ifle +478 -> 2453
    //   1978: aload_0
    //   1979: aload_0
    //   1980: getfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   1983: fload 6
    //   1985: invokestatic 1302	java/lang/Math:min	(FF)F
    //   1988: putfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   1991: aload 22
    //   1993: aload 22
    //   1995: getfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   1998: iconst_1
    //   1999: ior
    //   2000: i2b
    //   2001: putfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2004: aload_0
    //   2005: iconst_1
    //   2006: putfield 1308	org/vidogram/messenger/MessageObject:hasRtl	Z
    //   2009: iload 15
    //   2011: istore 8
    //   2013: iload 15
    //   2015: ifne +39 -> 2054
    //   2018: iload 15
    //   2020: istore 8
    //   2022: fload 6
    //   2024: fconst_0
    //   2025: fcmpl
    //   2026: ifne +28 -> 2054
    //   2029: aload 22
    //   2031: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2034: iload 12
    //   2036: invokevirtual 1311	android/text/StaticLayout:getParagraphDirection	(I)I
    //   2039: istore 19
    //   2041: iload 15
    //   2043: istore 8
    //   2045: iload 19
    //   2047: iconst_1
    //   2048: if_icmpne +6 -> 2054
    //   2051: iconst_1
    //   2052: istore 8
    //   2054: fload 4
    //   2056: fload 5
    //   2058: invokestatic 1314	java/lang/Math:max	(FF)F
    //   2061: fstore 4
    //   2063: fload_2
    //   2064: fload 5
    //   2066: fload 6
    //   2068: fadd
    //   2069: invokestatic 1314	java/lang/Math:max	(FF)F
    //   2072: fstore_2
    //   2073: iload 14
    //   2075: fload 5
    //   2077: f2d
    //   2078: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   2081: d2i
    //   2082: invokestatic 1316	java/lang/Math:max	(II)I
    //   2085: istore 14
    //   2087: iload 13
    //   2089: fload 5
    //   2091: fload 6
    //   2093: fadd
    //   2094: f2d
    //   2095: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   2098: d2i
    //   2099: invokestatic 1316	java/lang/Math:max	(II)I
    //   2102: istore 13
    //   2104: iload 12
    //   2106: iconst_1
    //   2107: iadd
    //   2108: istore 12
    //   2110: iload 8
    //   2112: istore 15
    //   2114: goto -191 -> 1923
    //   2117: iconst_0
    //   2118: istore 7
    //   2120: goto -580 -> 1540
    //   2123: getstatic 1035	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2126: getfield 1040	android/graphics/Point:x	I
    //   2129: istore 8
    //   2131: goto -580 -> 1551
    //   2134: ldc_w 1317
    //   2137: fstore_2
    //   2138: goto -566 -> 1572
    //   2141: getstatic 704	org/vidogram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   2144: astore_1
    //   2145: goto -453 -> 1692
    //   2148: astore_1
    //   2149: aload_1
    //   2150: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2153: return
    //   2154: aload 21
    //   2156: iload 7
    //   2158: invokevirtual 1320	android/text/StaticLayout:getLineStart	(I)I
    //   2161: istore 11
    //   2163: aload 21
    //   2165: iload 7
    //   2167: iload 8
    //   2169: iadd
    //   2170: iconst_1
    //   2171: isub
    //   2172: invokevirtual 1323	android/text/StaticLayout:getLineEnd	(I)I
    //   2175: istore 12
    //   2177: iload 12
    //   2179: iload 11
    //   2181: if_icmpge +12 -> 2193
    //   2184: iload 10
    //   2186: iconst_1
    //   2187: iadd
    //   2188: istore 10
    //   2190: goto -438 -> 1752
    //   2193: aload 22
    //   2195: iload 11
    //   2197: putfield 1282	org/vidogram/messenger/MessageObject$TextLayoutBlock:charactersOffset	I
    //   2200: aload 22
    //   2202: iload 12
    //   2204: putfield 1326	org/vidogram/messenger/MessageObject$TextLayoutBlock:charactersEnd	I
    //   2207: aload 22
    //   2209: new 1246	android/text/StaticLayout
    //   2212: dup
    //   2213: aload_0
    //   2214: getfield 136	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2217: iload 11
    //   2219: iload 12
    //   2221: aload_1
    //   2222: iload 9
    //   2224: getstatic 1252	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2227: fconst_1
    //   2228: fconst_0
    //   2229: iconst_0
    //   2230: invokespecial 1329	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;IILandroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2233: putfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2236: aload 22
    //   2238: aload 21
    //   2240: iload 7
    //   2242: invokevirtual 1332	android/text/StaticLayout:getLineTop	(I)I
    //   2245: i2f
    //   2246: putfield 1279	org/vidogram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2249: iload 10
    //   2251: ifeq +16 -> 2267
    //   2254: aload 22
    //   2256: aload 22
    //   2258: getfield 1279	org/vidogram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2261: fload_2
    //   2262: fsub
    //   2263: f2i
    //   2264: putfield 1285	org/vidogram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2267: aload 22
    //   2269: aload 22
    //   2271: getfield 1285	org/vidogram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2274: aload 22
    //   2276: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2279: aload 22
    //   2281: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2284: invokevirtual 1263	android/text/StaticLayout:getLineCount	()I
    //   2287: iconst_1
    //   2288: isub
    //   2289: invokevirtual 1335	android/text/StaticLayout:getLineBottom	(I)I
    //   2292: invokestatic 1316	java/lang/Math:max	(II)I
    //   2295: putfield 1285	org/vidogram/messenger/MessageObject$TextLayoutBlock:height	I
    //   2298: aload 22
    //   2300: getfield 1279	org/vidogram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2303: fstore 4
    //   2305: iload 8
    //   2307: istore 11
    //   2309: fload 4
    //   2311: fstore_3
    //   2312: iload 10
    //   2314: iload 18
    //   2316: iconst_1
    //   2317: isub
    //   2318: if_icmpne -498 -> 1820
    //   2321: iload 8
    //   2323: aload 22
    //   2325: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2328: invokevirtual 1263	android/text/StaticLayout:getLineCount	()I
    //   2331: invokestatic 1316	java/lang/Math:max	(II)I
    //   2334: istore 11
    //   2336: aload_0
    //   2337: aload_0
    //   2338: getfield 1260	org/vidogram/messenger/MessageObject:textHeight	I
    //   2341: aload 22
    //   2343: getfield 1279	org/vidogram/messenger/MessageObject$TextLayoutBlock:textYOffset	F
    //   2346: aload 22
    //   2348: getfield 1276	org/vidogram/messenger/MessageObject$TextLayoutBlock:textLayout	Landroid/text/StaticLayout;
    //   2351: invokevirtual 1258	android/text/StaticLayout:getHeight	()I
    //   2354: i2f
    //   2355: fadd
    //   2356: f2i
    //   2357: invokestatic 1316	java/lang/Math:max	(II)I
    //   2360: putfield 1260	org/vidogram/messenger/MessageObject:textHeight	I
    //   2363: fload 4
    //   2365: fstore_3
    //   2366: goto -546 -> 1820
    //   2369: astore 23
    //   2371: aload 23
    //   2373: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2376: fload 4
    //   2378: fstore_3
    //   2379: goto -559 -> 1820
    //   2382: astore 22
    //   2384: aload 22
    //   2386: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2389: goto -205 -> 2184
    //   2392: astore 23
    //   2394: iload 10
    //   2396: ifne +8 -> 2404
    //   2399: aload_0
    //   2400: fconst_0
    //   2401: putfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   2404: aload 23
    //   2406: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2409: fconst_0
    //   2410: fstore_2
    //   2411: goto -558 -> 1853
    //   2414: astore 23
    //   2416: fconst_0
    //   2417: fstore 4
    //   2419: aload 23
    //   2421: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2424: goto -557 -> 1867
    //   2427: astore 23
    //   2429: aload 23
    //   2431: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2434: fconst_0
    //   2435: fstore 5
    //   2437: goto -495 -> 1942
    //   2440: astore 23
    //   2442: aload 23
    //   2444: invokestatic 754	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2447: fconst_0
    //   2448: fstore 6
    //   2450: goto -479 -> 1971
    //   2453: aload 22
    //   2455: aload 22
    //   2457: getfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2460: iconst_2
    //   2461: ior
    //   2462: i2b
    //   2463: putfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2466: goto -457 -> 2009
    //   2469: astore 23
    //   2471: iconst_1
    //   2472: istore 8
    //   2474: goto -420 -> 2054
    //   2477: iload 15
    //   2479: ifeq +47 -> 2526
    //   2482: iload 10
    //   2484: iload 18
    //   2486: iconst_1
    //   2487: isub
    //   2488: if_icmpne +148 -> 2636
    //   2491: aload_0
    //   2492: iload 16
    //   2494: putfield 1299	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   2497: aload_0
    //   2498: aload_0
    //   2499: getfield 1119	org/vidogram/messenger/MessageObject:textWidth	I
    //   2502: fload_2
    //   2503: f2d
    //   2504: invokestatic 1267	java/lang/Math:ceil	(D)D
    //   2507: d2i
    //   2508: invokestatic 1316	java/lang/Math:max	(II)I
    //   2511: putfield 1119	org/vidogram/messenger/MessageObject:textWidth	I
    //   2514: iload 11
    //   2516: iload 7
    //   2518: iadd
    //   2519: istore 7
    //   2521: fload_3
    //   2522: fstore_2
    //   2523: goto -339 -> 2184
    //   2526: iload 10
    //   2528: iload 18
    //   2530: iconst_1
    //   2531: isub
    //   2532: if_icmpne +9 -> 2541
    //   2535: aload_0
    //   2536: iload 14
    //   2538: putfield 1299	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   2541: fload 4
    //   2543: fstore_2
    //   2544: goto -47 -> 2497
    //   2547: fload_2
    //   2548: fconst_0
    //   2549: fcmpl
    //   2550: ifle +70 -> 2620
    //   2553: aload_0
    //   2554: aload_0
    //   2555: getfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   2558: fload_2
    //   2559: invokestatic 1302	java/lang/Math:min	(FF)F
    //   2562: putfield 1294	org/vidogram/messenger/MessageObject:textXOffset	F
    //   2565: iload 18
    //   2567: iconst_1
    //   2568: if_icmpeq +46 -> 2614
    //   2571: iconst_1
    //   2572: istore 20
    //   2574: aload_0
    //   2575: iload 20
    //   2577: putfield 1308	org/vidogram/messenger/MessageObject:hasRtl	Z
    //   2580: aload 22
    //   2582: aload 22
    //   2584: getfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2587: iconst_1
    //   2588: ior
    //   2589: i2b
    //   2590: putfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2593: aload_0
    //   2594: aload_0
    //   2595: getfield 1119	org/vidogram/messenger/MessageObject:textWidth	I
    //   2598: iload 9
    //   2600: iload 14
    //   2602: invokestatic 1271	java/lang/Math:min	(II)I
    //   2605: invokestatic 1316	java/lang/Math:max	(II)I
    //   2608: putfield 1119	org/vidogram/messenger/MessageObject:textWidth	I
    //   2611: goto -97 -> 2514
    //   2614: iconst_0
    //   2615: istore 20
    //   2617: goto -43 -> 2574
    //   2620: aload 22
    //   2622: aload 22
    //   2624: getfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2627: iconst_2
    //   2628: ior
    //   2629: i2b
    //   2630: putfield 1306	org/vidogram/messenger/MessageObject$TextLayoutBlock:directionFlags	B
    //   2633: goto -40 -> 2593
    //   2636: goto -139 -> 2497
    //   2639: goto -680 -> 1959
    //   2642: iload 7
    //   2644: istore 9
    //   2646: goto -971 -> 1675
    //   2649: iconst_0
    //   2650: istore 7
    //   2652: goto -2546 -> 106
    //
    // Exception table:
    //   from	to	target	type
    //   444	456	459	java/lang/Throwable
    //   1692	1714	2148	java/lang/Exception
    //   2336	2363	2369	java/lang/Exception
    //   2207	2249	2382	java/lang/Exception
    //   2254	2267	2382	java/lang/Exception
    //   2267	2305	2382	java/lang/Exception
    //   1830	1843	2392	java/lang/Exception
    //   1848	1853	2392	java/lang/Exception
    //   1853	1867	2414	java/lang/Exception
    //   1930	1942	2427	java/lang/Exception
    //   1959	1971	2440	java/lang/Exception
    //   2029	2041	2469	java/lang/Exception
  }

  public void generateLinkDescription()
  {
    if (this.linkDescription != null)
      return;
    if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)) && (this.messageOwner.media.webpage.description != null))
      this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.webpage.description);
    while (this.linkDescription != null)
    {
      if (containsUrls(this.linkDescription));
      try
      {
        Linkify.addLinks((Spannable)this.linkDescription, 1);
        this.linkDescription = Emoji.replaceEmoji(this.linkDescription, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        return;
        if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (this.messageOwner.media.game.description != null))
        {
          this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.game.description);
          continue;
        }
        if ((!(this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)) || (this.messageOwner.media.description == null))
          continue;
        this.linkDescription = Spannable.Factory.getInstance().newSpannable(this.messageOwner.media.description);
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    }
  }

  public void generatePaymentSentMessageText(TLRPC.User paramUser)
  {
    TLRPC.User localUser = paramUser;
    if (paramUser == null)
      localUser = MessagesController.getInstance().getUser(Integer.valueOf((int)getDialogId()));
    if (localUser != null);
    for (paramUser = UserObject.getFirstName(localUser); (this.replyMessageObject != null) && ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)); paramUser = "")
    {
      this.messageText = LocaleController.formatString("PaymentSuccessfullyPaid", 2131166246, new Object[] { LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), paramUser, this.replyMessageObject.messageOwner.media.title });
      return;
    }
    this.messageText = LocaleController.formatString("PaymentSuccessfullyPaidNoItem", 2131166247, new Object[] { LocaleController.getInstance().formatCurrencyString(this.messageOwner.action.total_amount, this.messageOwner.action.currency), paramUser });
  }

  public void generatePinMessageText(TLRPC.User paramUser, TLRPC.Chat paramChat)
  {
    Object localObject = paramUser;
    TLRPC.Chat localChat = paramChat;
    if (paramUser == null)
    {
      localObject = paramUser;
      localChat = paramChat;
      if (paramChat == null)
      {
        if (this.messageOwner.from_id > 0)
          paramUser = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
        localObject = paramUser;
        localChat = paramChat;
        if (paramUser == null)
        {
          localChat = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.to_id.channel_id));
          localObject = paramUser;
        }
      }
    }
    if (this.replyMessageObject == null)
    {
      paramUser = LocaleController.getString("ActionPinnedNoText", 2131165253);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isMusic())
    {
      paramUser = LocaleController.getString("ActionPinnedMusic", 2131165252);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVideo())
    {
      paramUser = LocaleController.getString("ActionPinnedVideo", 2131165257);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isGif())
    {
      paramUser = LocaleController.getString("ActionPinnedGif", 2131165251);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isVoice())
    {
      paramUser = LocaleController.getString("ActionPinnedVoice", 2131165258);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if (this.replyMessageObject.isSticker())
    {
      paramUser = LocaleController.getString("ActionPinnedSticker", 2131165255);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
    {
      paramUser = LocaleController.getString("ActionPinnedFile", 2131165248);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
    {
      paramUser = LocaleController.getString("ActionPinnedGeo", 2131165250);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
    {
      paramUser = LocaleController.getString("ActionPinnedContact", 2131165247);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      paramUser = LocaleController.getString("ActionPinnedPhoto", 2131165254);
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
    {
      paramUser = LocaleController.formatString("ActionPinnedGame", 2131165249, new Object[] { "🎮 " + this.replyMessageObject.messageOwner.media.game.title });
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        this.messageText = Emoji.replaceEmoji(this.messageText, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        return;
        localObject = localChat;
      }
    }
    if ((this.replyMessageObject.messageText != null) && (this.replyMessageObject.messageText.length() > 0))
    {
      paramChat = this.replyMessageObject.messageText;
      paramUser = paramChat;
      if (paramChat.length() > 20)
        paramUser = paramChat.subSequence(0, 20) + "...";
      paramUser = LocaleController.formatString("ActionPinnedText", 2131165256, new Object[] { Emoji.replaceEmoji(paramUser, Theme.chat_msgTextPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false) });
      if (localObject != null);
      while (true)
      {
        this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
        return;
        localObject = localChat;
      }
    }
    paramUser = LocaleController.getString("ActionPinnedNoText", 2131165253);
    if (localObject != null);
    while (true)
    {
      this.messageText = replaceWithLink(paramUser, "un1", (TLObject)localObject);
      return;
      localObject = localChat;
    }
  }

  public void generateThumbs(boolean paramBoolean)
  {
    int i;
    int j;
    label97: TLRPC.PhotoSize localPhotoSize3;
    try
    {
      if ((this.messageOwner instanceof TLRPC.TL_messageService))
      {
        if (!(this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto))
          break label1315;
        if (!paramBoolean)
        {
          this.photoThumbs = new ArrayList(this.messageOwner.action.photo.sizes);
          return;
        }
        if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()))
          break label1315;
        i = 0;
        if (i >= this.photoThumbs.size())
          break label1315;
        TLRPC.PhotoSize localPhotoSize1 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
        j = 0;
        if (j >= this.messageOwner.action.photo.sizes.size())
          break label1323;
        localPhotoSize3 = (TLRPC.PhotoSize)this.messageOwner.action.photo.sizes.get(j);
        if (((localPhotoSize3 instanceof TLRPC.TL_photoSizeEmpty)) || (!localPhotoSize3.type.equals(localPhotoSize1.type)))
          break label1316;
        localPhotoSize1.location = localPhotoSize3.location;
        break label1323;
      }
      if ((this.messageOwner.media == null) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)))
        break label1315;
      if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
        break label412;
      if ((!paramBoolean) || ((this.photoThumbs != null) && (this.photoThumbs.size() != this.messageOwner.media.photo.sizes.size())))
      {
        this.photoThumbs = new ArrayList(this.messageOwner.media.photo.sizes);
        return;
      }
    }
    catch (Exception localException)
    {
      FirebaseCrash.a(localException);
      return;
    }
    label304: TLRPC.PhotoSize localPhotoSize2;
    if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()))
    {
      i = 0;
      if (i < this.photoThumbs.size())
      {
        localPhotoSize2 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
        j = 0;
        label330: if (j >= this.messageOwner.media.photo.sizes.size())
          break label1337;
        localPhotoSize3 = (TLRPC.PhotoSize)this.messageOwner.media.photo.sizes.get(j);
        if (((localPhotoSize3 instanceof TLRPC.TL_photoSizeEmpty)) || (!localPhotoSize3.type.equals(localPhotoSize2.type)))
          break label1330;
        localPhotoSize2.location = localPhotoSize3.location;
        break label1337;
        label412: if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
        {
          if (!(this.messageOwner.media.document.thumb instanceof TLRPC.TL_photoSizeEmpty))
          {
            if (!paramBoolean)
            {
              this.photoThumbs = new ArrayList();
              this.photoThumbs.add(this.messageOwner.media.document.thumb);
              return;
            }
            if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()) && (this.messageOwner.media.document.thumb != null))
            {
              localPhotoSize2 = (TLRPC.PhotoSize)this.photoThumbs.get(0);
              localPhotoSize2.location = this.messageOwner.media.document.thumb.location;
              localPhotoSize2.w = this.messageOwner.media.document.thumb.w;
              localPhotoSize2.h = this.messageOwner.media.document.thumb.h;
              return;
            }
          }
        }
        else
        {
          if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
          {
            if ((this.messageOwner.media.game.document != null) && (!(this.messageOwner.media.game.document.thumb instanceof TLRPC.TL_photoSizeEmpty)))
            {
              if (!paramBoolean)
              {
                this.photoThumbs = new ArrayList();
                this.photoThumbs.add(this.messageOwner.media.game.document.thumb);
              }
            }
            else if (this.messageOwner.media.game.photo != null)
            {
              if ((paramBoolean) && (this.photoThumbs2 != null))
                break label835;
              this.photoThumbs2 = new ArrayList(this.messageOwner.media.game.photo.sizes);
            }
            label735: 
            do
            {
              if ((this.photoThumbs != null) || (this.photoThumbs2 == null))
                break label1315;
              this.photoThumbs = this.photoThumbs2;
              this.photoThumbs2 = null;
              return;
              if ((this.photoThumbs == null) || (this.photoThumbs.isEmpty()) || (this.messageOwner.media.game.document.thumb == null))
                break;
              ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.game.document.thumb.location;
              break;
            }
            while (this.photoThumbs2.isEmpty());
            label835: i = 0;
            label847: if (i >= this.photoThumbs2.size())
              break label1356;
            localPhotoSize2 = (TLRPC.PhotoSize)this.photoThumbs2.get(i);
            j = 0;
            label873: if (j >= this.messageOwner.media.game.photo.sizes.size())
              break label1351;
            localPhotoSize3 = (TLRPC.PhotoSize)this.messageOwner.media.game.photo.sizes.get(j);
            if (((localPhotoSize3 instanceof TLRPC.TL_photoSizeEmpty)) || (!localPhotoSize3.type.equals(localPhotoSize2.type)))
              break label1344;
            localPhotoSize2.location = localPhotoSize3.location;
            break label1351;
          }
          if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
            if ((this.messageOwner.media.webpage != null) && (this.messageOwner.media.webpage.photo != null))
            {
              if ((!paramBoolean) || (this.photoThumbs == null))
              {
                this.photoThumbs = new ArrayList(this.messageOwner.media.webpage.photo.sizes);
                return;
              }
              if (this.photoThumbs.isEmpty())
                break label1315;
              i = 0;
            }
        }
      }
    }
    while (true)
    {
      if (i < this.photoThumbs.size())
      {
        localPhotoSize2 = (TLRPC.PhotoSize)this.photoThumbs.get(i);
        j = 0;
      }
      while (true)
      {
        if (j >= this.messageOwner.media.webpage.photo.sizes.size())
          break label1365;
        localPhotoSize3 = (TLRPC.PhotoSize)this.messageOwner.media.webpage.photo.sizes.get(j);
        if ((!(localPhotoSize3 instanceof TLRPC.TL_photoSizeEmpty)) && (localPhotoSize3.type.equals(localPhotoSize2.type)))
        {
          localPhotoSize2.location = localPhotoSize3.location;
          break label1365;
          if ((this.messageOwner.media.webpage.document != null) && (!(this.messageOwner.media.webpage.document.thumb instanceof TLRPC.TL_photoSizeEmpty)))
          {
            if (!paramBoolean)
            {
              this.photoThumbs = new ArrayList();
              this.photoThumbs.add(this.messageOwner.media.webpage.document.thumb);
              return;
            }
            if ((this.photoThumbs != null) && (!this.photoThumbs.isEmpty()) && (this.messageOwner.media.webpage.document.thumb != null))
              ((TLRPC.PhotoSize)this.photoThumbs.get(0)).location = this.messageOwner.media.webpage.document.thumb.location;
          }
          label1315: return;
          label1316: j += 1;
          break label97;
          label1323: i += 1;
          break;
          label1330: j += 1;
          break label330;
          label1337: i += 1;
          break label304;
          label1344: j += 1;
          break label873;
          label1351: i += 1;
          break label847;
          label1356: break label735;
        }
        j += 1;
      }
      label1365: i += 1;
    }
  }

  public int getApproximateHeight()
  {
    int k = 0;
    int j;
    int i;
    if (this.type == 0)
    {
      j = this.textHeight;
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.messageOwner.media.webpage instanceof TLRPC.TL_webPage)));
      for (i = AndroidUtilities.dp(100.0F); ; i = 0)
      {
        j = i + j;
        i = j;
        if (isReply())
          i = j + AndroidUtilities.dp(42.0F);
        return i;
      }
    }
    if (this.type == 2)
      return AndroidUtilities.dp(72.0F);
    if (this.type == 12)
      return AndroidUtilities.dp(71.0F);
    if (this.type == 9)
      return AndroidUtilities.dp(100.0F);
    if (this.type == 4)
      return AndroidUtilities.dp(114.0F);
    if (this.type == 14)
      return AndroidUtilities.dp(82.0F);
    if (this.type == 10)
      return AndroidUtilities.dp(30.0F);
    if (this.type == 11)
      return AndroidUtilities.dp(50.0F);
    float f2;
    float f1;
    Object localObject;
    if (this.type == 13)
    {
      f2 = AndroidUtilities.displaySize.y * 0.4F;
      if (AndroidUtilities.isTablet())
      {
        f1 = AndroidUtilities.getMinTabletSide() * 0.5F;
        localObject = this.messageOwner.media.document.attributes.iterator();
        while (((Iterator)localObject).hasNext())
        {
          TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((Iterator)localObject).next();
          if (!(localDocumentAttribute instanceof TLRPC.TL_documentAttributeImageSize))
            continue;
          i = localDocumentAttribute.w;
          k = localDocumentAttribute.h;
        }
      }
    }
    while (true)
    {
      j = i;
      if (i == 0)
      {
        k = (int)f2;
        j = AndroidUtilities.dp(100.0F) + k;
      }
      if (k > f2)
        j = (int)(j * (f2 / k));
      for (i = (int)f2; ; i = k)
      {
        k = i;
        if (j > f1)
          k = (int)(i * (f1 / j));
        return k + AndroidUtilities.dp(14.0F);
        f1 = AndroidUtilities.displaySize.x * 0.5F;
        break;
        if (AndroidUtilities.isTablet())
        {
          i = (int)(AndroidUtilities.getMinTabletSide() * 0.7F);
          j = AndroidUtilities.dp(100.0F) + i;
          k = i;
          if (i > AndroidUtilities.getPhotoSize())
            k = AndroidUtilities.getPhotoSize();
          i = j;
          if (j > AndroidUtilities.getPhotoSize())
            i = AndroidUtilities.getPhotoSize();
          localObject = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
          j = i;
          if (localObject != null)
          {
            f1 = ((TLRPC.PhotoSize)localObject).w / k;
            k = (int)(((TLRPC.PhotoSize)localObject).h / f1);
            j = k;
            if (k == 0)
              j = AndroidUtilities.dp(100.0F);
            if (j <= i)
              break label567;
          }
        }
        while (true)
        {
          label505: j = i;
          if (isSecretPhoto())
            if (!AndroidUtilities.isTablet())
              break label588;
          label567: label588: for (j = (int)(AndroidUtilities.getMinTabletSide() * 0.5F); ; j = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.5F))
          {
            return AndroidUtilities.dp(14.0F) + j;
            i = (int)(Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) * 0.7F);
            break;
            if (j >= AndroidUtilities.dp(120.0F))
              break label614;
            i = AndroidUtilities.dp(120.0F);
            break label505;
          }
          label614: i = j;
        }
      }
      i = 0;
    }
  }

  public long getDialogId()
  {
    return getDialogId(this.messageOwner);
  }

  public TLRPC.Document getDocument()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
      return this.messageOwner.media.webpage.document;
    if (this.messageOwner.media != null)
      return this.messageOwner.media.document;
    return null;
  }

  public String getDocumentName()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      return FileLoader.getDocumentFileName(this.messageOwner.media.document);
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
      return FileLoader.getDocumentFileName(this.messageOwner.media.webpage.document);
    return "";
  }

  public int getDuration()
  {
    int k = 0;
    TLRPC.Document localDocument;
    int i;
    if (this.type == 0)
    {
      localDocument = this.messageOwner.media.webpage.document;
      i = 0;
    }
    while (true)
    {
      int j = k;
      if (i < localDocument.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localDocument.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
          j = localDocumentAttribute.duration;
      }
      else
      {
        return j;
        localDocument = this.messageOwner.media.document;
        break;
      }
      i += 1;
    }
  }

  public String getExtension()
  {
    Object localObject2 = getFileName();
    int i = ((String)localObject2).lastIndexOf('.');
    Object localObject1 = null;
    if (i != -1)
      localObject1 = ((String)localObject2).substring(i + 1);
    if (localObject1 != null)
    {
      localObject2 = localObject1;
      if (((String)localObject1).length() != 0);
    }
    else
    {
      localObject2 = this.messageOwner.media.document.mime_type;
    }
    localObject1 = localObject2;
    if (localObject2 == null)
      localObject1 = "";
    return (String)(String)((String)localObject1).toUpperCase();
  }

  public String getFileName()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      return FileLoader.getAttachFileName(this.messageOwner.media.document);
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      Object localObject = this.messageOwner.media.photo.sizes;
      if (((ArrayList)localObject).size() > 0)
      {
        localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, AndroidUtilities.getPhotoSize());
        if (localObject != null)
          return FileLoader.getAttachFileName((TLObject)localObject);
      }
    }
    else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
    {
      return FileLoader.getAttachFileName(this.messageOwner.media.webpage.document);
    }
    return (String)"";
  }

  public int getFileType()
  {
    if (isVideo())
      return 2;
    if (isVoice())
      return 1;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      return 3;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      return 0;
    return 4;
  }

  public String getForwardedName()
  {
    if (this.messageOwner.fwd_from != null)
    {
      Object localObject;
      if (this.messageOwner.fwd_from.channel_id != 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
        if (localObject != null)
          return ((TLRPC.Chat)localObject).title;
      }
      else if (this.messageOwner.fwd_from.from_id != 0)
      {
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
        if (localObject != null)
          return UserObject.getUserName((TLRPC.User)localObject);
      }
    }
    return (String)null;
  }

  public int getId()
  {
    return this.messageOwner.id;
  }

  public TLRPC.InputStickerSet getInputStickerSet()
  {
    return getInputStickerSet(this.messageOwner);
  }

  public String getMimeType()
  {
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      return this.messageOwner.media.document.mime_type;
    if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
    {
      TLRPC.TL_webDocument localTL_webDocument = ((TLRPC.TL_messageMediaInvoice)this.messageOwner.media).photo;
      if (localTL_webDocument != null)
        return localTL_webDocument.mime_type;
    }
    else if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
    {
      return "image/jpeg";
    }
    return "";
  }

  public String getMusicAuthor()
  {
    Object localObject2 = null;
    Object localObject1;
    int i;
    if (this.type == 0)
    {
      localObject1 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    while (true)
    {
      if (i >= ((TLRPC.Document)localObject1).attributes.size())
        break label322;
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject1).attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (localDocumentAttribute.voice)
          if ((isOutOwner()) || ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id == UserConfig.getClientUserId())))
            localObject1 = LocaleController.getString("FromYou", 2131165787);
        do
        {
          return localObject1;
          localObject1 = this.messageOwner.media.document;
          break;
          if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.channel_id != 0))
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.messageOwner.fwd_from.channel_id));
          while (localObject2 != null)
          {
            return UserObject.getUserName((TLRPC.User)localObject2);
            if ((this.messageOwner.fwd_from != null) && (this.messageOwner.fwd_from.from_id != 0))
            {
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.fwd_from.from_id));
              localObject1 = null;
              continue;
            }
            if (this.messageOwner.from_id < 0)
            {
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.messageOwner.from_id));
              continue;
            }
            localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.messageOwner.from_id));
            localObject1 = null;
          }
          if (localObject1 != null)
            return ((TLRPC.Chat)localObject1).title;
          localObject2 = localDocumentAttribute.performer;
          if (localObject2 == null)
            break label305;
          localObject1 = localObject2;
        }
        while (((String)localObject2).length() != 0);
        label305: return LocaleController.getString("AudioUnknownArtist", 2131165371);
      }
      i += 1;
    }
    label322: return (String)(String)"";
  }

  public String getMusicTitle()
  {
    Object localObject2;
    int i;
    if (this.type == 0)
    {
      localObject2 = this.messageOwner.media.webpage.document;
      i = 0;
    }
    while (true)
    {
      if (i >= ((TLRPC.Document)localObject2).attributes.size())
        break label145;
      Object localObject1 = (TLRPC.DocumentAttribute)((TLRPC.Document)localObject2).attributes.get(i);
      if ((localObject1 instanceof TLRPC.TL_documentAttributeAudio))
      {
        if (((TLRPC.DocumentAttribute)localObject1).voice)
          localObject1 = LocaleController.formatDateAudio(this.messageOwner.date);
        label110: 
        do
        {
          String str;
          do
          {
            return localObject1;
            localObject2 = this.messageOwner.media.document;
            break;
            str = ((TLRPC.DocumentAttribute)localObject1).title;
            if (str == null)
              break label110;
            localObject1 = str;
          }
          while (str.length() != 0);
          localObject2 = FileLoader.getDocumentFileName((TLRPC.Document)localObject2);
          if (localObject2 == null)
            break label128;
          localObject1 = localObject2;
        }
        while (((String)localObject2).length() != 0);
        label128: return LocaleController.getString("AudioUnknownTitle", 2131165372);
      }
      i += 1;
    }
    label145: return (String)(String)"";
  }

  public String getSecretTimeString()
  {
    if (!isSecretMedia())
      return null;
    int i = this.messageOwner.ttl;
    if (this.messageOwner.destroyTime != 0)
      i = Math.max(0, this.messageOwner.destroyTime - ConnectionsManager.getInstance().getCurrentTime());
    if (i < 60)
      return i + "s";
    return i / 60 + "m";
  }

  public String getStickerEmoji()
  {
    int i = 0;
    while (i < this.messageOwner.media.document.attributes.size())
    {
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.messageOwner.media.document.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
      {
        if ((localDocumentAttribute.alt != null) && (localDocumentAttribute.alt.length() > 0))
          return localDocumentAttribute.alt;
        return null;
      }
      i += 1;
    }
    return null;
  }

  public String getStrickerChar()
  {
    if ((this.messageOwner.media != null) && (this.messageOwner.media.document != null))
    {
      Iterator localIterator = this.messageOwner.media.document.attributes.iterator();
      while (localIterator.hasNext())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)localIterator.next();
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker))
          return localDocumentAttribute.alt;
      }
    }
    return null;
  }

  public int getUnradFlags()
  {
    return getUnreadFlags(this.messageOwner);
  }

  public boolean hasPhotoStickers()
  {
    return (this.messageOwner.media != null) && (this.messageOwner.media.photo != null) && (this.messageOwner.media.photo.has_stickers);
  }

  public boolean isContentUnread()
  {
    return this.messageOwner.media_unread;
  }

  public boolean isForwarded()
  {
    return isForwardedMessage(this.messageOwner);
  }

  public boolean isFromUser()
  {
    return (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }

  public boolean isGame()
  {
    return isGameMessage(this.messageOwner);
  }

  public boolean isGif()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument)) && (isGifDocument(this.messageOwner.media.document));
  }

  public boolean isInvoice()
  {
    return isInvoiceMessage(this.messageOwner);
  }

  public boolean isMask()
  {
    return isMaskMessage(this.messageOwner);
  }

  public boolean isMediaEmpty()
  {
    return isMediaEmpty(this.messageOwner);
  }

  public boolean isMegagroup()
  {
    return isMegagroup(this.messageOwner);
  }

  public boolean isMusic()
  {
    return isMusicMessage(this.messageOwner);
  }

  public boolean isNewGif()
  {
    return (this.messageOwner.media != null) && (isNewGifDocument(this.messageOwner.media.document));
  }

  public boolean isOut()
  {
    return this.messageOwner.out;
  }

  public boolean isOutOwner()
  {
    return (this.messageOwner.out) && (this.messageOwner.from_id > 0) && (!this.messageOwner.post);
  }

  public boolean isReply()
  {
    return ((this.replyMessageObject == null) || (!(this.replyMessageObject.messageOwner instanceof TLRPC.TL_messageEmpty))) && ((this.messageOwner.reply_to_msg_id != 0) || (this.messageOwner.reply_to_random_id != 0L)) && ((this.messageOwner.flags & 0x8) != 0);
  }

  public boolean isSecretMedia()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60)) || (isVoice()) || (isVideo()));
  }

  public boolean isSecretPhoto()
  {
    return ((this.messageOwner instanceof TLRPC.TL_message_secret)) && ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) && (this.messageOwner.ttl > 0) && (this.messageOwner.ttl <= 60);
  }

  public boolean isSendError()
  {
    return (this.messageOwner.send_state == 2) && (this.messageOwner.id < 0);
  }

  public boolean isSending()
  {
    return (this.messageOwner.send_state == 1) && (this.messageOwner.id < 0);
  }

  public boolean isSent()
  {
    return (this.messageOwner.send_state == 0) || (this.messageOwner.id > 0);
  }

  public boolean isSticker()
  {
    if (this.type != 1000)
      return this.type == 13;
    return isStickerMessage(this.messageOwner);
  }

  public boolean isUnread()
  {
    return this.messageOwner.unread;
  }

  public boolean isVideo()
  {
    return isVideoMessage(this.messageOwner);
  }

  public boolean isVideoVoice()
  {
    return (isVideoVoiceMessage(this.messageOwner)) && (BuildVars.DEBUG_PRIVATE_VERSION);
  }

  public boolean isVoice()
  {
    return isVoiceMessage(this.messageOwner);
  }

  public boolean isWebpageDocument()
  {
    return ((this.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (this.messageOwner.media.webpage.document != null) && (!isGifDocument(this.messageOwner.media.webpage.document));
  }

  public void measureInlineBotButtons()
  {
    this.wantedBotKeyboardWidth = 0;
    if (!(this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup))
      return;
    Theme.createChatResources(null, true);
    label42: int j;
    label44: int m;
    int k;
    int i;
    label94: Object localObject;
    if (this.botButtonsLayout == null)
    {
      this.botButtonsLayout = new StringBuilder();
      j = 0;
      if (j >= this.messageOwner.reply_markup.rows.size())
        break label308;
      TLRPC.TL_keyboardButtonRow localTL_keyboardButtonRow = (TLRPC.TL_keyboardButtonRow)this.messageOwner.reply_markup.rows.get(j);
      m = localTL_keyboardButtonRow.buttons.size();
      k = 0;
      i = 0;
      if (k >= m)
        break label269;
      localObject = (TLRPC.KeyboardButton)localTL_keyboardButtonRow.buttons.get(k);
      this.botButtonsLayout.append(j).append(k);
      if ((!(localObject instanceof TLRPC.TL_keyboardButtonBuy)) || ((this.messageOwner.media.flags & 0x4) == 0))
        break label243;
      localObject = LocaleController.getString("PaymentReceipt", 2131166230);
      label161: localObject = new StaticLayout((CharSequence)localObject, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (((StaticLayout)localObject).getLineCount() <= 0)
        break label310;
      i = Math.max(i, (int)Math.ceil(((StaticLayout)localObject).getLineWidth(0) - ((StaticLayout)localObject).getLineLeft(0)) + AndroidUtilities.dp(4.0F));
    }
    label269: label308: label310: 
    while (true)
    {
      k += 1;
      break label94;
      this.botButtonsLayout.setLength(0);
      break label42;
      label243: localObject = Emoji.replaceEmoji(((TLRPC.KeyboardButton)localObject).text, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false);
      break label161;
      this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, (AndroidUtilities.dp(12.0F) + i) * m + AndroidUtilities.dp(5.0F) * (m - 1));
      j += 1;
      break label44;
      break;
    }
  }

  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, ArrayList<Integer> paramArrayList, AbstractMap<Integer, TLRPC.User> paramAbstractMap)
  {
    Object localObject1 = paramCharSequence;
    if (TextUtils.indexOf(paramCharSequence, paramString) >= 0)
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder("");
      int i = 0;
      while (i < paramArrayList.size())
      {
        localObject1 = null;
        if (paramAbstractMap != null)
          localObject1 = (TLRPC.User)paramAbstractMap.get(paramArrayList.get(i));
        Object localObject2 = localObject1;
        if (localObject1 == null)
          localObject2 = MessagesController.getInstance().getUser((Integer)paramArrayList.get(i));
        if (localObject2 != null)
        {
          localObject1 = UserObject.getUserName((TLRPC.User)localObject2);
          int j = localSpannableStringBuilder.length();
          if (localSpannableStringBuilder.length() != 0)
            localSpannableStringBuilder.append(", ");
          localSpannableStringBuilder.append((CharSequence)localObject1);
          localSpannableStringBuilder.setSpan(new URLSpanNoUnderlineBold("" + ((TLRPC.User)localObject2).id), j, ((String)localObject1).length() + j, 33);
        }
        i += 1;
      }
      localObject1 = TextUtils.replace(paramCharSequence, new String[] { paramString }, new CharSequence[] { localSpannableStringBuilder });
    }
    return (CharSequence)(CharSequence)localObject1;
  }

  public CharSequence replaceWithLink(CharSequence paramCharSequence, String paramString, TLObject paramTLObject)
  {
    int i = TextUtils.indexOf(paramCharSequence, paramString);
    Object localObject = paramCharSequence;
    if (i >= 0)
    {
      if (!(paramTLObject instanceof TLRPC.User))
        break label134;
      localObject = UserObject.getUserName((TLRPC.User)paramTLObject);
      paramTLObject = "" + ((TLRPC.User)paramTLObject).id;
    }
    while (true)
    {
      paramCharSequence = new SpannableStringBuilder(TextUtils.replace(paramCharSequence, new String[] { paramString }, new String[] { localObject }));
      paramCharSequence.setSpan(new URLSpanNoUnderlineBold("" + paramTLObject), i, ((String)localObject).length() + i, 33);
      localObject = paramCharSequence;
      return localObject;
      label134: if ((paramTLObject instanceof TLRPC.Chat))
      {
        localObject = ((TLRPC.Chat)paramTLObject).title;
        paramTLObject = "" + -((TLRPC.Chat)paramTLObject).id;
        continue;
      }
      if ((paramTLObject instanceof TLRPC.TL_game))
      {
        localObject = ((TLRPC.TL_game)paramTLObject).title;
        paramTLObject = "game";
        continue;
      }
      localObject = "";
      paramTLObject = "0";
    }
  }

  public void setContentIsRead()
  {
    this.messageOwner.media_unread = false;
  }

  public void setIsRead()
  {
    this.messageOwner.unread = false;
  }

  public void setType()
  {
    int i = this.type;
    if (((this.messageOwner instanceof TLRPC.TL_message)) || ((this.messageOwner instanceof TLRPC.TL_messageForwarded_old2)))
      if (isMediaEmpty())
      {
        this.type = 0;
        if ((this.messageText == null) || (this.messageText.length() == 0))
          this.messageText = "Empty message";
      }
    while (true)
    {
      if ((i != 1000) && (i != this.type))
        generateThumbs(false);
      return;
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto))
      {
        this.type = 1;
        continue;
      }
      if (((this.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)) || ((this.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)))
      {
        this.type = 4;
        continue;
      }
      if (isVideo())
      {
        this.type = 3;
        continue;
      }
      if (isVoice())
      {
        this.type = 2;
        continue;
      }
      if (isMusic())
      {
        this.type = 14;
        continue;
      }
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaContact))
      {
        this.type = 12;
        continue;
      }
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaUnsupported))
      {
        this.type = 0;
        continue;
      }
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaDocument))
      {
        if ((this.messageOwner.media.document != null) && (this.messageOwner.media.document.mime_type != null))
        {
          if (isGifDocument(this.messageOwner.media.document))
          {
            this.type = 8;
            continue;
          }
          if ((this.messageOwner.media.document.mime_type.equals("image/webp")) && (isSticker()))
          {
            this.type = 13;
            continue;
          }
          this.type = 9;
          continue;
        }
        this.type = 9;
        continue;
      }
      if ((this.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
      {
        this.type = 0;
        continue;
      }
      if (!(this.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
        continue;
      this.type = 0;
      continue;
      if (!(this.messageOwner instanceof TLRPC.TL_messageService))
        continue;
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionLoginUnknownLocation))
      {
        this.type = 0;
        continue;
      }
      if (((this.messageOwner.action instanceof TLRPC.TL_messageActionChatEditPhoto)) || ((this.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto)))
      {
        this.contentType = 1;
        this.type = 11;
        continue;
      }
      if ((this.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
      {
        if (((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages)) || ((this.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL)))
        {
          this.contentType = 1;
          this.type = 10;
          continue;
        }
        this.contentType = -1;
        this.type = -1;
        continue;
      }
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionHistoryClear))
      {
        this.contentType = -1;
        this.type = -1;
        continue;
      }
      if ((this.messageOwner.action instanceof TLRPC.TL_messageActionPhoneCall))
      {
        this.type = 16;
        continue;
      }
      this.contentType = 1;
      this.type = 10;
    }
  }

  public static class TextLayoutBlock
  {
    public int charactersEnd;
    public int charactersOffset;
    public byte directionFlags;
    public int height;
    public StaticLayout textLayout;
    public float textYOffset;

    public boolean isRtl()
    {
      return ((this.directionFlags & 0x1) != 0) && ((this.directionFlags & 0x2) == 0);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MessageObject
 * JD-Core Version:    0.6.0
 */