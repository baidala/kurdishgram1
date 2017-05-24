package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.ForegroundColorSpan;
import android.util.SparseArray;
import android.view.View.MeasureSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.query.DraftQuery;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.DraftMessage;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.InputChannel;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.TL_encryptedChat;
import org.vidogram.tgnet.TLRPC.TL_encryptedChatDiscarded;
import org.vidogram.tgnet.TLRPC.TL_encryptedChatRequested;
import org.vidogram.tgnet.TLRPC.TL_encryptedChatWaiting;
import org.vidogram.tgnet.TLRPC.TL_game;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGame;
import org.vidogram.tgnet.TLRPC.TL_messageService;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;

public class DialogCell extends BaseCell
{
  private static Drawable statusDrawable;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private ImageReceiver avatarImage = new ImageReceiver(this);
  int avatarLeft;
  private int avatarTop = AndroidUtilities.dp(10.0F);
  private TLRPC.Chat chat = null;
  private int checkDrawLeft;
  private int checkDrawTop = AndroidUtilities.dp(18.0F);
  private StaticLayout countLayout;
  private int countLeft;
  private int countTop = AndroidUtilities.dp(39.0F);
  private int countWidth;
  private long currentDialogId;
  private int currentEditDate;
  private CustomDialog customDialog;
  private boolean dialogMuted;
  private int dialogsType;
  private TLRPC.DraftMessage draftMessage;
  private boolean drawCheck1;
  private boolean drawCheck2;
  private boolean drawClock;
  private boolean drawCount;
  private boolean drawError;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private boolean drawPin;
  private boolean drawVerified;
  private TLRPC.EncryptedChat encryptedChat = null;
  private int errorLeft;
  private int errorTop = AndroidUtilities.dp(39.0F);
  private int halfCheckDrawLeft;
  private int index;
  private boolean isDialogCell;
  private boolean isSelected;
  private int lastMessageDate;
  private CharSequence lastPrintString = null;
  private int lastSendState;
  private boolean lastUnreadState;
  private MessageObject message;
  private StaticLayout messageLayout;
  private int messageLeft;
  private int messageTop = AndroidUtilities.dp(40.0F);
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameMuteLeft;
  private int pinLeft;
  private int pinTop = AndroidUtilities.dp(39.0F);
  private RectF rect = new RectF();
  private int status;
  private StaticLayout timeLayout;
  private int timeLeft;
  private int timeTop = AndroidUtilities.dp(17.0F);
  private int unreadCount;
  public boolean useSeparator = false;
  private TLRPC.User user = null;

  public DialogCell(Context paramContext)
  {
    super(paramContext);
    Theme.createDialogsResources(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
  }

  private ArrayList<TLRPC.TL_dialog> getDialogsArray()
  {
    if (this.dialogsType == 0)
      return MessagesController.getInstance().dialogs;
    if (this.dialogsType == 1)
      return MessagesController.getInstance().dialogsServerOnly;
    if (this.dialogsType == 2)
      return MessagesController.getInstance().dialogsGroupsOnly;
    if (this.dialogsType == 3)
      return MessagesController.getInstance().dialogsChannelOnly;
    if (this.dialogsType == 4)
      return MessagesController.getInstance().dialogsUserOnly;
    if (this.dialogsType == 5)
      return MessagesController.getInstance().dialogsBotOnly;
    if (this.dialogsType == 6)
      return MessagesController.getInstance().dialogsFavoriteOnly;
    return null;
  }

  public void buildLayout()
  {
    Object localObject7 = null;
    Object localObject8 = null;
    Object localObject2 = null;
    Object localObject1 = null;
    if (this.isDialogCell)
      localObject1 = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
    Object localObject6 = Theme.dialogs_namePaint;
    Object localObject3 = Theme.dialogs_messagePaint;
    int j = 1;
    this.drawNameGroup = false;
    this.drawNameBroadcast = false;
    this.drawNameLock = false;
    this.drawNameBot = false;
    this.drawVerified = false;
    if (this.customDialog != null)
      if (this.customDialog.type == 2)
      {
        this.drawNameLock = true;
        this.nameLockTop = AndroidUtilities.dp(16.5F);
        if (!LocaleController.isRTL)
        {
          this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
          if (this.customDialog.type != 1)
            break label1503;
          localObject7 = LocaleController.getString("FromYou", 2131165787);
          if (!this.customDialog.isMedia)
            break label1433;
          localObject3 = Theme.dialogs_messagePrintingPaint;
          localObject1 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject7, this.message.messageText }));
          ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(Theme.getColor("chats_attachMessage")), ((String)localObject7).length() + 2, ((SpannableStringBuilder)localObject1).length(), 33);
          label246: if (((SpannableStringBuilder)localObject1).length() > 0)
            ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(Theme.getColor("chats_nameMessage")), 0, ((String)localObject7).length() + 1, 33);
          localObject1 = Emoji.replaceEmoji((CharSequence)localObject1, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        }
      }
    label5378: for (int i = 0; ; i = 1)
    {
      label305: Object localObject5 = LocaleController.stringForMessageListDate(this.customDialog.date);
      label358: Object localObject9;
      label388: int k;
      if (this.customDialog.unread_count != 0)
      {
        this.drawCount = true;
        localObject2 = String.format("%d", new Object[] { Integer.valueOf(this.customDialog.unread_count) });
        if (!this.customDialog.sent)
          break label1541;
        this.drawCheck1 = true;
        this.drawCheck2 = true;
        this.drawClock = false;
        this.drawError = false;
        localObject7 = this.customDialog.name;
        if (this.customDialog.type != 2)
          break label5343;
        localObject8 = Theme.dialogs_nameEncryptedPaint;
        localObject6 = localObject3;
        localObject3 = localObject8;
        localObject9 = localObject7;
        localObject8 = localObject5;
        localObject7 = localObject2;
        localObject2 = localObject6;
        k = i;
        localObject6 = localObject9;
        localObject5 = localObject3;
        localObject3 = localObject8;
      }
      while (true)
      {
        label453: int m = (int)Math.ceil(Theme.dialogs_timePaint.measureText((String)localObject3));
        this.timeLayout = new StaticLayout((CharSequence)localObject3, Theme.dialogs_timePaint, m, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        label516: label543: label568: int n;
        if (!LocaleController.isRTL)
        {
          this.timeLeft = (getMeasuredWidth() - AndroidUtilities.dp(15.0F) - m);
          if (LocaleController.isRTL)
            break label4294;
          j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(14.0F) - m;
          if (!this.drawNameLock)
            break label4330;
          i = j - (AndroidUtilities.dp(4.0F) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
          if (!this.drawClock)
            break label4450;
          n = Theme.dialogs_clockDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0F);
          j = i - n;
          if (LocaleController.isRTL)
            break label4418;
          this.checkDrawLeft = (this.timeLeft - n);
          label614: if ((!this.dialogMuted) || (this.drawVerified))
            break label4660;
          m = AndroidUtilities.dp(6.0F) + Theme.dialogs_muteDrawable.getIntrinsicWidth();
          j -= m;
          i = j;
          if (LocaleController.isRTL)
          {
            this.nameLeft = (m + this.nameLeft);
            i = j;
          }
          label675: j = Math.max(AndroidUtilities.dp(12.0F), i);
        }
        while (true)
        {
          try
          {
            this.nameLayout = new StaticLayout(TextUtils.ellipsize(((String)localObject6).replace('\n', ' '), (TextPaint)localObject5, j - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject5, j, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            i = getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 16);
            if (LocaleController.isRTL)
              continue;
            this.messageLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            if (!AndroidUtilities.isTablet())
              continue;
            f = 13.0F;
            this.avatarLeft = AndroidUtilities.dp(f);
            this.avatarImage.setImageCoords(this.avatarLeft, this.avatarTop, AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
            if (!this.drawError)
              continue;
            m = AndroidUtilities.dp(31.0F);
            if (LocaleController.isRTL)
              continue;
            this.errorLeft = (getMeasuredWidth() - AndroidUtilities.dp(34.0F));
            i -= m;
            localObject3 = localObject1;
            if (k == 0)
              continue;
            localObject3 = localObject1;
            if (localObject1 != null)
              continue;
            localObject3 = "";
            localObject3 = ((CharSequence)localObject3).toString();
            localObject1 = localObject3;
            if (((String)localObject3).length() <= 150)
              continue;
            localObject1 = ((String)localObject3).substring(0, 150);
            localObject3 = Emoji.replaceEmoji(((String)localObject1).replace('\n', ' '), Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(17.0F), false);
            i = Math.max(AndroidUtilities.dp(12.0F), i);
            localObject1 = TextUtils.ellipsize((CharSequence)localObject3, (TextPaint)localObject2, i - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END);
          }
          catch (Exception localObject4)
          {
            try
            {
              this.messageLayout = new StaticLayout((CharSequence)localObject1, (TextPaint)localObject2, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
              if (!LocaleController.isRTL)
                continue;
              if ((this.nameLayout == null) || (this.nameLayout.getLineCount() <= 0))
                continue;
              f = this.nameLayout.getLineLeft(0);
              d1 = Math.ceil(this.nameLayout.getLineWidth(0));
              if ((!this.dialogMuted) || (this.drawVerified))
                continue;
              this.nameMuteLeft = (int)(this.nameLeft + (j - d1) - AndroidUtilities.dp(6.0F) - Theme.dialogs_muteDrawable.getIntrinsicWidth());
              if ((f != 0.0F) || (d1 >= j))
                continue;
              this.nameLeft = (int)(this.nameLeft + (j - d1));
              if ((this.messageLayout == null) || (this.messageLayout.getLineCount() <= 0) || (this.messageLayout.getLineLeft(0) != 0.0F))
                continue;
              d1 = Math.ceil(this.messageLayout.getLineWidth(0));
              if (d1 >= i)
                continue;
              double d2 = this.messageLeft;
              this.messageLeft = (int)(i - d1 + d2);
              return;
              this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - Theme.dialogs_lockDrawable.getIntrinsicWidth());
              this.nameLeft = AndroidUtilities.dp(14.0F);
              break;
              this.drawVerified = this.customDialog.verified;
              if (this.customDialog.type != 1)
                continue;
              this.drawNameGroup = true;
              this.nameLockTop = AndroidUtilities.dp(17.5F);
              if (LocaleController.isRTL)
                continue;
              this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              j = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
              if (!this.drawNameGroup)
                continue;
              i = Theme.dialogs_groupDrawable.getIntrinsicWidth();
              this.nameLeft = (i + j);
              break;
              i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
              continue;
              j = getMeasuredWidth();
              k = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              if (!this.drawNameGroup)
                continue;
              i = Theme.dialogs_groupDrawable.getIntrinsicWidth();
              this.nameLockLeft = (j - k - i);
              this.nameLeft = AndroidUtilities.dp(14.0F);
              break;
              i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
              continue;
              if (LocaleController.isRTL)
                continue;
              this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              break;
              this.nameLeft = AndroidUtilities.dp(14.0F);
              break;
              label1433: localObject5 = this.customDialog.message;
              localObject1 = localObject5;
              if (((String)localObject5).length() <= 150)
                continue;
              localObject1 = ((String)localObject5).substring(0, 150);
              localObject1 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject7, ((String)localObject1).replace('\n', ' ') }));
              break label246;
              label1503: localObject1 = this.customDialog.message;
              if (!this.customDialog.isMedia)
                break label5378;
              localObject3 = Theme.dialogs_messagePrintingPaint;
              i = 1;
              break label305;
              this.drawCount = false;
              break label358;
              label1541: this.drawCheck1 = false;
              this.drawCheck2 = false;
              this.drawClock = false;
              this.drawError = false;
              break label388;
              if (this.encryptedChat == null)
                continue;
              this.drawNameLock = true;
              this.nameLockTop = AndroidUtilities.dp(16.5F);
              if (LocaleController.isRTL)
                continue;
              this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
              k = this.lastMessageDate;
              i = k;
              if (this.lastMessageDate != 0)
                continue;
              i = k;
              if (this.message == null)
                continue;
              i = this.message.messageOwner.date;
              if (!this.isDialogCell)
                continue;
              this.draftMessage = DraftQuery.getDraft(this.currentDialogId);
              if (((this.draftMessage == null) || (((!TextUtils.isEmpty(this.draftMessage.message)) || (this.draftMessage.reply_to_msg_id != 0)) && ((i <= this.draftMessage.date) || (this.unreadCount == 0)))) && ((!ChatObject.isChannel(this.chat)) || (this.chat.megagroup) || (this.chat.creator) || (this.chat.editor)) && ((this.chat == null) || ((!this.chat.left) && (!this.chat.kicked))))
                continue;
              this.draftMessage = null;
              if (localObject1 == null)
                continue;
              this.lastPrintString = ((CharSequence)localObject1);
              localObject2 = Theme.dialogs_messagePrintingPaint;
              i = j;
              if (this.draftMessage == null)
                continue;
              localObject5 = LocaleController.stringForMessageListDate(this.draftMessage.date);
              if (this.message != null)
                continue;
              this.drawCheck1 = false;
              this.drawCheck2 = false;
              this.drawClock = false;
              this.drawCount = false;
              this.drawError = false;
              if (this.chat == null)
                continue;
              localObject8 = this.chat.title;
              localObject3 = localObject6;
              localObject6 = localObject8;
              if (((String)localObject6).length() != 0)
                continue;
              localObject8 = LocaleController.getString("HiddenName", 2131165809);
              localObject6 = localObject3;
              localObject3 = localObject5;
              localObject5 = localObject6;
              localObject6 = localObject8;
              k = i;
              break label453;
              this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - Theme.dialogs_lockDrawable.getIntrinsicWidth());
              this.nameLeft = AndroidUtilities.dp(14.0F);
              continue;
              if (this.chat == null)
                continue;
              if ((this.chat.id >= 0) && ((!ChatObject.isChannel(this.chat)) || (this.chat.megagroup)))
                continue;
              this.drawNameBroadcast = true;
              this.nameLockTop = AndroidUtilities.dp(16.5F);
              this.drawVerified = this.chat.verified;
              if (LocaleController.isRTL)
                continue;
              this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              k = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
              if (!this.drawNameGroup)
                continue;
              i = Theme.dialogs_groupDrawable.getIntrinsicWidth();
              this.nameLeft = (i + k);
              continue;
              this.drawNameGroup = true;
              this.nameLockTop = AndroidUtilities.dp(17.5F);
              continue;
              i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
              continue;
              k = getMeasuredWidth();
              m = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              if (!this.drawNameGroup)
                continue;
              i = Theme.dialogs_groupDrawable.getIntrinsicWidth();
              this.nameLockLeft = (k - m - i);
              this.nameLeft = AndroidUtilities.dp(14.0F);
              continue;
              i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth();
              continue;
              if (LocaleController.isRTL)
                continue;
              this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              if (this.user == null)
                continue;
              if (!this.user.bot)
                continue;
              this.drawNameBot = true;
              this.nameLockTop = AndroidUtilities.dp(16.5F);
              if (LocaleController.isRTL)
                continue;
              this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
              this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_botDrawable.getIntrinsicWidth());
              this.drawVerified = this.user.verified;
              continue;
              this.nameLeft = AndroidUtilities.dp(14.0F);
              continue;
              continue;
              this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - Theme.dialogs_botDrawable.getIntrinsicWidth());
              this.nameLeft = AndroidUtilities.dp(14.0F);
              continue;
              this.draftMessage = null;
              continue;
              this.lastPrintString = null;
              if (this.draftMessage == null)
                continue;
              i = 0;
              if (!TextUtils.isEmpty(this.draftMessage.message))
                continue;
              localObject2 = LocaleController.getString("Draft", 2131165662);
              localObject1 = SpannableStringBuilder.valueOf((CharSequence)localObject2);
              ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(Theme.getColor("chats_draft")), 0, ((String)localObject2).length(), 33);
              localObject2 = localObject3;
              continue;
              localObject2 = this.draftMessage.message;
              localObject1 = localObject2;
              if (((String)localObject2).length() <= 150)
                continue;
              localObject1 = ((String)localObject2).substring(0, 150);
              localObject2 = LocaleController.getString("Draft", 2131165662);
              localObject1 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject2, ((String)localObject1).replace('\n', ' ') }));
              ((SpannableStringBuilder)localObject1).setSpan(new ForegroundColorSpan(Theme.getColor("chats_draft")), 0, ((String)localObject2).length() + 1, 33);
              localObject1 = Emoji.replaceEmoji((CharSequence)localObject1, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              localObject2 = localObject3;
              continue;
              if (this.message != null)
                continue;
              localObject2 = localObject3;
              if (this.encryptedChat == null)
                break label5331;
              localObject3 = Theme.dialogs_messagePrintingPaint;
              if (!(this.encryptedChat instanceof TLRPC.TL_encryptedChatRequested))
                continue;
              localObject1 = LocaleController.getString("EncryptionProcessing", 2131165690);
              localObject2 = localObject3;
              i = j;
              continue;
              if (!(this.encryptedChat instanceof TLRPC.TL_encryptedChatWaiting))
                continue;
              if ((this.user == null) || (this.user.first_name == null))
                continue;
              localObject1 = LocaleController.formatString("AwaitingEncryption", 2131165379, new Object[] { this.user.first_name });
              localObject2 = localObject3;
              i = j;
              continue;
              localObject1 = LocaleController.formatString("AwaitingEncryption", 2131165379, new Object[] { "" });
              localObject2 = localObject3;
              i = j;
              continue;
              if (!(this.encryptedChat instanceof TLRPC.TL_encryptedChatDiscarded))
                continue;
              localObject1 = LocaleController.getString("EncryptionRejected", 2131165691);
              localObject2 = localObject3;
              i = j;
              continue;
              localObject2 = localObject3;
              if (!(this.encryptedChat instanceof TLRPC.TL_encryptedChat))
                break label5331;
              if (this.encryptedChat.admin_id != UserConfig.getClientUserId())
                continue;
              if ((this.user == null) || (this.user.first_name == null))
                continue;
              localObject1 = LocaleController.formatString("EncryptedChatStartedOutgoing", 2131165679, new Object[] { this.user.first_name });
              localObject2 = localObject3;
              i = j;
              continue;
              localObject1 = LocaleController.formatString("EncryptedChatStartedOutgoing", 2131165679, new Object[] { "" });
              localObject2 = localObject3;
              i = j;
              continue;
              localObject1 = LocaleController.getString("EncryptedChatStartedIncoming", 2131165678);
              localObject2 = localObject3;
              i = j;
              continue;
              localObject2 = null;
              localObject1 = null;
              if (!this.message.isFromUser())
                continue;
              localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(this.message.messageOwner.from_id));
              if (!(this.message.messageOwner instanceof TLRPC.TL_messageService))
                continue;
              localObject1 = this.message.messageText;
              localObject2 = Theme.dialogs_messagePrintingPaint;
              i = j;
              continue;
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.message.messageOwner.to_id.channel_id));
              continue;
              if ((this.chat == null) || (this.chat.id <= 0) || (localObject1 != null))
                continue;
              if (!this.message.isOutOwner())
                continue;
              localObject2 = LocaleController.getString("FromYou", 2131165787);
              if (this.message.caption == null)
                continue;
              localObject5 = this.message.caption.toString();
              localObject1 = localObject5;
              if (((String)localObject5).length() <= 150)
                continue;
              localObject1 = ((String)localObject5).substring(0, 150);
              localObject5 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject2, ((String)localObject1).replace('\n', ' ') }));
              localObject1 = localObject3;
              localObject3 = localObject5;
              if (((SpannableStringBuilder)localObject3).length() <= 0)
                continue;
              ((SpannableStringBuilder)localObject3).setSpan(new ForegroundColorSpan(Theme.getColor("chats_nameMessage")), 0, ((String)localObject2).length() + 1, 33);
              localObject3 = Emoji.replaceEmoji((CharSequence)localObject3, Theme.dialogs_messagePaint.getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              localObject2 = localObject1;
              i = 0;
              localObject1 = localObject3;
              continue;
              if (localObject2 == null)
                continue;
              localObject2 = UserObject.getFirstName((TLRPC.User)localObject2).replace("\n", "");
              continue;
              if (localObject1 == null)
                continue;
              localObject2 = ((TLRPC.Chat)localObject1).title.replace("\n", "");
              continue;
              localObject2 = "DELETED";
              continue;
              if ((this.message.messageOwner.media == null) || (this.message.isMediaEmpty()))
                continue;
              localObject1 = Theme.dialogs_messagePrintingPaint;
              if (!(this.message.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                continue;
              localObject3 = SpannableStringBuilder.valueOf(String.format("%s: 🎮 %s", new Object[] { localObject2, this.message.messageOwner.media.game.title }));
              ((SpannableStringBuilder)localObject3).setSpan(new ForegroundColorSpan(Theme.getColor("chats_attachMessage")), ((String)localObject2).length() + 2, ((SpannableStringBuilder)localObject3).length(), 33);
              continue;
              if (this.message.type != 14)
                continue;
              localObject3 = SpannableStringBuilder.valueOf(String.format("%s: 🎧 %s - %s", new Object[] { localObject2, this.message.getMusicAuthor(), this.message.getMusicTitle() }));
              continue;
              localObject3 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject2, this.message.messageText }));
              continue;
              if (this.message.messageOwner.message == null)
                continue;
              localObject5 = this.message.messageOwner.message;
              localObject1 = localObject5;
              if (((String)localObject5).length() <= 150)
                continue;
              localObject1 = ((String)localObject5).substring(0, 150);
              localObject5 = SpannableStringBuilder.valueOf(String.format("%s: %s", new Object[] { localObject2, ((String)localObject1).replace('\n', ' ') }));
              localObject1 = localObject3;
              localObject3 = localObject5;
              continue;
              localObject5 = SpannableStringBuilder.valueOf("");
              localObject1 = localObject3;
              localObject3 = localObject5;
              continue;
              if (this.message.caption == null)
                continue;
              localObject1 = this.message.caption;
              localObject2 = localObject3;
              i = j;
              continue;
              if (!(this.message.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                continue;
              localObject5 = "🎮 " + this.message.messageOwner.media.game.title;
              localObject1 = localObject5;
              localObject2 = localObject3;
              i = j;
              if (this.message.messageOwner.media == null)
                continue;
              localObject1 = localObject5;
              localObject2 = localObject3;
              i = j;
              if (this.message.isMediaEmpty())
                continue;
              localObject2 = Theme.dialogs_messagePrintingPaint;
              localObject1 = localObject5;
              i = j;
              continue;
              if (this.message.type != 14)
                continue;
              localObject5 = String.format("🎧 %s - %s", new Object[] { this.message.getMusicAuthor(), this.message.getMusicTitle() });
              continue;
              localObject5 = this.message.messageText;
              continue;
              if (this.lastMessageDate == 0)
                continue;
              localObject5 = LocaleController.stringForMessageListDate(this.lastMessageDate);
              continue;
              if (this.message == null)
                break label5323;
              localObject5 = LocaleController.stringForMessageListDate(this.message.messageOwner.date);
              continue;
              if (this.unreadCount == 0)
                continue;
              this.drawCount = true;
              localObject3 = String.format("%d", new Object[] { Integer.valueOf(this.unreadCount) });
              if ((!this.message.isOut()) || (this.draftMessage != null))
                continue;
              if (!this.message.isSending())
                continue;
              this.drawCheck1 = false;
              this.drawCheck2 = false;
              this.drawClock = true;
              this.drawError = false;
              localObject7 = localObject3;
              continue;
              this.drawCount = false;
              localObject3 = localObject8;
              continue;
              if (!this.message.isSendError())
                continue;
              this.drawCheck1 = false;
              this.drawCheck2 = false;
              this.drawClock = false;
              this.drawError = true;
              this.drawCount = false;
              localObject7 = localObject3;
              continue;
              localObject7 = localObject3;
              if (!this.message.isSent())
                continue;
              if ((this.message.isUnread()) && ((!ChatObject.isChannel(this.chat)) || (this.chat.megagroup)))
                continue;
              boolean bool = true;
              this.drawCheck1 = bool;
              this.drawCheck2 = true;
              this.drawClock = false;
              this.drawError = false;
              localObject7 = localObject3;
              continue;
              bool = false;
              continue;
              this.drawCheck1 = false;
              this.drawCheck2 = false;
              this.drawClock = false;
              this.drawError = false;
              localObject7 = localObject3;
              continue;
              if (this.user == null)
                break label5311;
              if (this.user.id != UserConfig.getClientUserId())
                continue;
              localObject3 = LocaleController.getString("ChatYourSelfName", 2131165544);
              if (this.encryptedChat == null)
                break label5296;
              localObject8 = Theme.dialogs_nameEncryptedPaint;
              localObject6 = localObject3;
              localObject3 = localObject8;
              continue;
              if ((this.user.id / 1000 == 777) || (this.user.id / 1000 == 333) || (ContactsController.getInstance().contactsDict.get(this.user.id) != null))
                continue;
              if ((ContactsController.getInstance().contactsDict.size() != 0) || ((ContactsController.getInstance().contactsLoaded) && (!ContactsController.getInstance().isLoadingContacts())))
                continue;
              localObject3 = UserObject.getUserName(this.user);
              continue;
              if ((this.user.phone == null) || (this.user.phone.length() == 0))
                continue;
              localObject3 = b.a().e("+" + this.user.phone);
              continue;
              localObject3 = UserObject.getUserName(this.user);
              continue;
              localObject3 = UserObject.getUserName(this.user);
              continue;
              this.timeLeft = AndroidUtilities.dp(15.0F);
              break label516;
              label4294: j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - m;
              this.nameLeft += m;
              break label543;
              label4330: if (!this.drawNameGroup)
                continue;
              i = j - (AndroidUtilities.dp(4.0F) + Theme.dialogs_groupDrawable.getIntrinsicWidth());
              break label568;
              if (!this.drawNameBroadcast)
                continue;
              i = j - (AndroidUtilities.dp(4.0F) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth());
              break label568;
              i = j;
              if (!this.drawNameBot)
                break label568;
              i = j - (AndroidUtilities.dp(4.0F) + Theme.dialogs_botDrawable.getIntrinsicWidth());
              break label568;
              label4418: this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
              this.nameLeft = (n + this.nameLeft);
              break label614;
              label4450: j = i;
              if (!this.drawCheck2)
                break label614;
              n = Theme.dialogs_checkDrawable.getIntrinsicWidth() + AndroidUtilities.dp(5.0F);
              j = i - n;
              if (!this.drawCheck1)
                continue;
              j -= Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0F);
              if (LocaleController.isRTL)
                continue;
              this.halfCheckDrawLeft = (this.timeLeft - n);
              this.checkDrawLeft = (this.halfCheckDrawLeft - AndroidUtilities.dp(5.5F));
              break label614;
              this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
              this.halfCheckDrawLeft = (this.checkDrawLeft + AndroidUtilities.dp(5.5F));
              i = this.nameLeft;
              this.nameLeft = (n + Theme.dialogs_halfCheckDrawable.getIntrinsicWidth() - AndroidUtilities.dp(8.0F) + i);
              break label614;
              if (LocaleController.isRTL)
                continue;
              this.checkDrawLeft = (this.timeLeft - n);
              break label614;
              this.checkDrawLeft = (this.timeLeft + m + AndroidUtilities.dp(5.0F));
              this.nameLeft = (n + this.nameLeft);
              break label614;
              label4660: i = j;
              if (!this.drawVerified)
                break label675;
              m = AndroidUtilities.dp(6.0F) + Theme.dialogs_verifiedDrawable.getIntrinsicWidth();
              j -= m;
              i = j;
              if (!LocaleController.isRTL)
                break label675;
              this.nameLeft = (m + this.nameLeft);
              i = j;
              break label675;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
              f = 9.0F;
              continue;
              this.messageLeft = AndroidUtilities.dp(16.0F);
              m = getMeasuredWidth();
              if (!AndroidUtilities.isTablet())
                continue;
              f = 65.0F;
              this.avatarLeft = (m - AndroidUtilities.dp(f));
              continue;
              f = 61.0F;
              continue;
              this.errorLeft = AndroidUtilities.dp(11.0F);
              this.messageLeft += m;
              continue;
              if (localObject7 == null)
                continue;
              this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(Theme.dialogs_countTextPaint.measureText((String)localObject7)));
              this.countLayout = new StaticLayout((CharSequence)localObject7, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
              m = this.countWidth;
              m = AndroidUtilities.dp(18.0F) + m;
              if (LocaleController.isRTL)
                continue;
              this.countLeft = (getMeasuredWidth() - this.countWidth - AndroidUtilities.dp(19.0F));
              this.drawCount = true;
              i -= m;
              continue;
              this.countLeft = AndroidUtilities.dp(19.0F);
              this.messageLeft += m;
              continue;
              if (!this.drawPin)
                continue;
              m = Theme.dialogs_pinnedDrawable.getIntrinsicWidth();
              m = AndroidUtilities.dp(8.0F) + m;
              i -= m;
              if (LocaleController.isRTL)
                continue;
              this.pinLeft = (getMeasuredWidth() - Theme.dialogs_pinnedDrawable.getIntrinsicWidth() - AndroidUtilities.dp(14.0F));
              this.drawCount = false;
              continue;
              this.pinLeft = AndroidUtilities.dp(14.0F);
              this.messageLeft += m;
              continue;
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
              continue;
              if (!this.drawVerified)
                continue;
              this.nameMuteLeft = (int)(this.nameLeft + (j - d1) - AndroidUtilities.dp(6.0F) - Theme.dialogs_verifiedDrawable.getIntrinsicWidth());
              continue;
              if ((this.nameLayout == null) || (this.nameLayout.getLineCount() <= 0))
                continue;
              float f = this.nameLayout.getLineRight(0);
              if (f != j)
                continue;
              double d1 = Math.ceil(this.nameLayout.getLineWidth(0));
              if (d1 >= j)
                continue;
              this.nameLeft = (int)(this.nameLeft - (j - d1));
              if ((!this.dialogMuted) && (!this.drawVerified))
                continue;
              this.nameMuteLeft = (int)(f + this.nameLeft + AndroidUtilities.dp(6.0F));
              if ((this.messageLayout == null) || (this.messageLayout.getLineCount() <= 0) || (this.messageLayout.getLineRight(0) != i))
                continue;
              d1 = Math.ceil(this.messageLayout.getLineWidth(0));
              if (d1 >= i)
                continue;
              this.messageLeft = (int)(this.messageLeft - (i - d1));
              return;
            }
            continue;
            localObject8 = localException2;
            localObject4 = localObject5;
            localObject5 = localObject8;
            k = i;
          }
          break label453;
          label5296: localObject8 = localObject4;
          localObject4 = localObject6;
          localObject6 = localObject8;
          continue;
          label5311: localObject4 = localObject6;
          localObject6 = "";
          continue;
          label5323: localObject5 = "";
          continue;
          label5331: String str = "";
          i = j;
        }
        label5343: localObject8 = localObject4;
        localObject9 = localObject2;
        Object localObject4 = localObject5;
        localObject5 = localObject6;
        localObject6 = localObject7;
        k = i;
        localObject2 = localObject8;
        localObject7 = localObject9;
      }
    }
  }

  public void checkCurrentDialogIndex()
  {
    if (this.index < getDialogsArray().size())
    {
      TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)getDialogsArray().get(this.index);
      TLRPC.DraftMessage localDraftMessage = DraftQuery.getDraft(this.currentDialogId);
      MessageObject localMessageObject = (MessageObject)MessagesController.getInstance().dialogMessage.get(Long.valueOf(localTL_dialog.id));
      if ((this.currentDialogId != localTL_dialog.id) || ((this.message != null) && (this.message.getId() != localTL_dialog.top_message)) || ((localMessageObject != null) && (localMessageObject.messageOwner.edit_date != this.currentEditDate)) || (this.unreadCount != localTL_dialog.unread_count) || (this.message != localMessageObject) || ((this.message == null) && (localMessageObject != null)) || (localDraftMessage != this.draftMessage) || (this.drawPin != localTL_dialog.pinned))
      {
        this.currentDialogId = localTL_dialog.id;
        update(0);
      }
    }
  }

  public long getDialogId()
  {
    return this.currentDialogId;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.avatarImage.onAttachedToWindow();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.currentDialogId == 0L) && (this.customDialog == null));
    while (true)
    {
      return;
      if (this.isSelected)
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_tabletSeletedPaint);
      if (this.drawPin)
        paramCanvas.drawRect(0.0F, 0.0F, getMeasuredWidth(), getMeasuredHeight(), Theme.dialogs_pinnedPaint);
      if (this.drawNameLock)
      {
        setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
        Theme.dialogs_lockDrawable.draw(paramCanvas);
        label98: if (this.nameLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.nameLeft, AndroidUtilities.dp(13.0F));
          this.nameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        paramCanvas.save();
        paramCanvas.translate(this.timeLeft, this.timeTop);
        this.timeLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.messageLayout != null)
        {
          paramCanvas.save();
          paramCanvas.translate(this.messageLeft, this.messageTop);
        }
      }
      try
      {
        this.messageLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawClock)
        {
          setDrawableBounds(Theme.dialogs_clockDrawable, this.checkDrawLeft, this.checkDrawTop);
          Theme.dialogs_clockDrawable.draw(paramCanvas);
          if ((!this.dialogMuted) || (this.drawVerified))
            break label712;
          setDrawableBounds(Theme.dialogs_muteDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          Theme.dialogs_muteDrawable.draw(paramCanvas);
          if (!this.drawError)
            break label768;
          this.rect.set(this.errorLeft, this.errorTop, this.errorLeft + AndroidUtilities.dp(23.0F), this.errorTop + AndroidUtilities.dp(23.0F));
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.density * 11.5F, AndroidUtilities.density * 11.5F, Theme.dialogs_errorPaint);
          setDrawableBounds(Theme.dialogs_errorDrawable, this.errorLeft + AndroidUtilities.dp(5.5F), this.errorTop + AndroidUtilities.dp(5.0F));
          Theme.dialogs_errorDrawable.draw(paramCanvas);
          if (this.useSeparator)
          {
            if (!LocaleController.isRTL)
              break label962;
            paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
          }
          this.avatarImage.draw(paramCanvas);
          if ((this.user == null) || (this.user.bot))
            continue;
          LocaleController.getInstance();
          statusDrawable = LocaleController.userStatusDrawable(this.user, getContext());
          if (!LocaleController.isRTL)
            break label999;
          setDrawableBounds(statusDrawable, this.avatarLeft + AndroidUtilities.dp(5.0F), this.messageTop + this.messageLayout.getHeight() / 2);
          statusDrawable.draw(paramCanvas);
          return;
          if (this.drawNameGroup)
          {
            setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_groupDrawable.draw(paramCanvas);
            break label98;
          }
          if (this.drawNameBroadcast)
          {
            setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
            Theme.dialogs_broadcastDrawable.draw(paramCanvas);
            break label98;
          }
          if (!this.drawNameBot)
            break label98;
          setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
          Theme.dialogs_botDrawable.draw(paramCanvas);
        }
      }
      catch (Exception localPaint)
      {
        while (true)
        {
          FileLog.e(localException);
          continue;
          if (!this.drawCheck2)
            continue;
          if (this.drawCheck1)
          {
            setDrawableBounds(Theme.dialogs_halfCheckDrawable, this.halfCheckDrawLeft, this.checkDrawTop);
            Theme.dialogs_halfCheckDrawable.draw(paramCanvas);
            setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
            Theme.dialogs_checkDrawable.draw(paramCanvas);
            continue;
          }
          setDrawableBounds(Theme.dialogs_checkDrawable, this.checkDrawLeft, this.checkDrawTop);
          Theme.dialogs_checkDrawable.draw(paramCanvas);
          continue;
          label712: if (!this.drawVerified)
            continue;
          setDrawableBounds(Theme.dialogs_verifiedDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, this.nameMuteLeft, AndroidUtilities.dp(16.5F));
          Theme.dialogs_verifiedDrawable.draw(paramCanvas);
          Theme.dialogs_verifiedCheckDrawable.draw(paramCanvas);
          continue;
          label768: if (this.drawCount)
          {
            int i = this.countLeft - AndroidUtilities.dp(5.5F);
            this.rect.set(i, this.countTop, i + this.countWidth + AndroidUtilities.dp(11.0F), this.countTop + AndroidUtilities.dp(23.0F));
            RectF localRectF = this.rect;
            float f1 = AndroidUtilities.density;
            float f2 = AndroidUtilities.density;
            if (this.dialogMuted);
            for (Paint localPaint = Theme.dialogs_countGrayPaint; ; localPaint = Theme.dialogs_countPaint)
            {
              paramCanvas.drawRoundRect(localRectF, 11.5F * f1, 11.5F * f2, localPaint);
              paramCanvas.save();
              paramCanvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0F));
              if (this.countLayout != null)
                this.countLayout.draw(paramCanvas);
              paramCanvas.restore();
              break;
            }
          }
          if (!this.drawPin)
            continue;
          setDrawableBounds(Theme.dialogs_pinnedDrawable, this.pinLeft, this.pinTop);
          Theme.dialogs_pinnedDrawable.draw(paramCanvas);
          continue;
          label962: paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
          continue;
          label999: setDrawableBounds(statusDrawable, this.avatarLeft + AndroidUtilities.dp(35.0F), this.messageTop + this.messageLayout.getHeight() / 2);
        }
      }
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.currentDialogId == 0L) && (this.customDialog == null))
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    do
      return;
    while (!paramBoolean);
    buildLayout();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.getSize(paramInt1);
    int i = AndroidUtilities.dp(72.0F);
    if (this.useSeparator);
    for (paramInt1 = 1; ; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, paramInt1 + i);
      return;
    }
  }

  public void setDialog(long paramLong, MessageObject paramMessageObject, int paramInt)
  {
    this.currentDialogId = paramLong;
    this.message = paramMessageObject;
    this.isDialogCell = false;
    this.lastMessageDate = paramInt;
    if (paramMessageObject != null)
    {
      paramInt = paramMessageObject.messageOwner.edit_date;
      this.currentEditDate = paramInt;
      this.unreadCount = 0;
      if ((paramMessageObject == null) || (!paramMessageObject.isUnread()))
        break label98;
    }
    label98: for (boolean bool = true; ; bool = false)
    {
      this.lastUnreadState = bool;
      if (this.message != null)
        this.lastSendState = this.message.messageOwner.send_state;
      update(0);
      return;
      paramInt = 0;
      break;
    }
  }

  public void setDialog(TLRPC.TL_dialog paramTL_dialog, int paramInt1, int paramInt2)
  {
    this.currentDialogId = paramTL_dialog.id;
    this.isDialogCell = true;
    this.index = paramInt1;
    this.dialogsType = paramInt2;
    update(0);
  }

  public void setDialog(CustomDialog paramCustomDialog)
  {
    this.customDialog = paramCustomDialog;
    update(0);
  }

  public void setDialogSelected(boolean paramBoolean)
  {
    if (this.isSelected != paramBoolean)
      invalidate();
    this.isSelected = paramBoolean;
  }

  public void update(int paramInt)
  {
    boolean bool;
    if (this.customDialog != null)
    {
      this.lastMessageDate = this.customDialog.date;
      if (this.customDialog.unread_count != 0);
      for (bool = true; ; bool = false)
      {
        this.lastUnreadState = bool;
        this.unreadCount = this.customDialog.unread_count;
        this.drawPin = this.customDialog.pinned;
        this.dialogMuted = this.customDialog.muted;
        this.avatarDrawable.setInfo(this.customDialog.id, this.customDialog.name, null, false);
        this.avatarImage.setImage(null, "50_50", this.avatarDrawable, null, false);
        if ((getMeasuredWidth() == 0) && (getMeasuredHeight() == 0))
          break;
        buildLayout();
        invalidate();
        label132: return;
      }
    }
    Object localObject;
    label220: int i;
    if (this.isDialogCell)
    {
      localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
      if ((localObject != null) && (paramInt == 0))
      {
        this.message = ((MessageObject)MessagesController.getInstance().dialogMessage.get(Long.valueOf(((TLRPC.TL_dialog)localObject).id)));
        if ((this.message == null) || (!this.message.isUnread()))
          break label802;
        bool = true;
        this.lastUnreadState = bool;
        this.unreadCount = ((TLRPC.TL_dialog)localObject).unread_count;
        if (this.message == null)
          break label808;
        i = this.message.messageOwner.edit_date;
        label253: this.currentEditDate = i;
        this.lastMessageDate = ((TLRPC.TL_dialog)localObject).last_message_date;
        this.drawPin = ((TLRPC.TL_dialog)localObject).pinned;
        if (this.message != null)
          this.lastSendState = this.message.messageOwner.send_state;
      }
      label297: if (paramInt != 0)
      {
        if ((!this.isDialogCell) || ((paramInt & 0x40) == 0))
          break label1101;
        localObject = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentDialogId));
        if (((this.lastPrintString == null) || (localObject != null)) && ((this.lastPrintString != null) || (localObject == null)) && ((this.lastPrintString == null) || (localObject == null) || (this.lastPrintString.equals(localObject))))
          break label1101;
      }
    }
    label534: label682: label1087: label1093: label1098: label1101: for (int j = 1; ; j = 0)
    {
      i = j;
      if (j == 0)
      {
        i = j;
        if ((paramInt & 0x2) != 0)
        {
          i = j;
          if (this.chat == null)
            i = 1;
        }
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if ((paramInt & 0x1) != 0)
        {
          j = i;
          if (this.chat == null)
            j = 1;
        }
      }
      i = j;
      if (j == 0)
      {
        i = j;
        if ((paramInt & 0x8) != 0)
        {
          i = j;
          if (this.user == null)
            i = 1;
        }
      }
      j = i;
      if (i == 0)
      {
        j = i;
        if ((paramInt & 0x10) != 0)
        {
          j = i;
          if (this.user == null)
            j = 1;
        }
      }
      if ((j == 0) && ((paramInt & 0x100) != 0))
        if ((this.message != null) && (this.lastUnreadState != this.message.isUnread()))
        {
          this.lastUnreadState = this.message.isUnread();
          j = 1;
        }
      while (true)
      {
        i = j;
        if (j == 0)
        {
          i = j;
          if ((paramInt & 0x1000) != 0)
          {
            i = j;
            if (this.message != null)
            {
              i = j;
              if (this.lastSendState != this.message.messageOwner.send_state)
              {
                this.lastSendState = this.message.messageOwner.send_state;
                i = 1;
              }
            }
          }
        }
        j = i;
        if (this.user != null)
        {
          j = i;
          if (i == 0)
          {
            j = i;
            if ((paramInt & 0x4) != 0)
              if (this.user.status == null)
                break label1093;
          }
        }
        for (paramInt = this.user.status.expires; ; paramInt = 0)
        {
          if (paramInt != this.status)
            i = 1;
          this.status = paramInt;
          j = i;
          if (j == 0)
            break label132;
          if ((this.isDialogCell) && (MessagesController.getInstance().isDialogMuted(this.currentDialogId)))
          {
            bool = true;
            this.dialogMuted = bool;
            this.user = null;
            this.chat = null;
            this.encryptedChat = null;
            paramInt = (int)this.currentDialogId;
            i = (int)(this.currentDialogId >> 32);
            if (paramInt == 0)
              break label981;
            if (i != 1)
              break label886;
            this.chat = MessagesController.getInstance().getChat(Integer.valueOf(paramInt));
            label741: if (this.user == null)
              break label1025;
            if (this.user.photo == null)
              break label1087;
          }
          for (localObject = this.user.photo.photo_small; ; localObject = null)
          {
            this.avatarDrawable.setInfo(this.user);
            while (true)
            {
              this.avatarImage.setImage((TLObject)localObject, "50_50", this.avatarDrawable, null, false);
              break;
              bool = false;
              break label220;
              i = 0;
              break label253;
              this.drawPin = false;
              break label297;
              if (!this.isDialogCell)
                break label1098;
              localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.currentDialogId));
              if ((localObject == null) || (this.unreadCount == ((TLRPC.TL_dialog)localObject).unread_count))
                break label1098;
              this.unreadCount = ((TLRPC.TL_dialog)localObject).unread_count;
              j = 1;
              break label534;
              bool = false;
              break label682;
              label886: if (paramInt < 0)
              {
                this.chat = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
                if ((this.isDialogCell) || (this.chat == null) || (this.chat.migrated_to == null))
                  break label741;
                localObject = MessagesController.getInstance().getChat(Integer.valueOf(this.chat.migrated_to.channel_id));
                if (localObject == null)
                  break label741;
                this.chat = ((TLRPC.Chat)localObject);
                break label741;
              }
              this.user = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
              break label741;
              label981: this.encryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i));
              if (this.encryptedChat == null)
                break label741;
              this.user = MessagesController.getInstance().getUser(Integer.valueOf(this.encryptedChat.user_id));
              break label741;
              if (this.chat != null)
              {
                if (this.chat.photo != null);
                for (localObject = this.chat.photo.photo_small; ; localObject = null)
                {
                  this.avatarDrawable.setInfo(this.chat);
                  break label781;
                  requestLayout();
                  break;
                }
              }
              localObject = null;
            }
          }
        }
      }
    }
  }

  public static class CustomDialog
  {
    public int date;
    public int id;
    public boolean isMedia;
    public String message;
    public boolean muted;
    public String name;
    public boolean pinned;
    public boolean sent;
    public int type;
    public int unread_count;
    public boolean verified;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.DialogCell
 * JD-Core Version:    0.6.0
 */