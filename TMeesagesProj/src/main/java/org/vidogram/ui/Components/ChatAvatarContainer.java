package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import java.util.HashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatParticipant;
import org.vidogram.tgnet.TLRPC.ChatParticipants;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.TL_channelFull;
import org.vidogram.tgnet.TLRPC.TL_chatFull;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.ProfileActivity;

public class ChatAvatarContainer extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private int onlineCount = -1;
  private ChatActivity parentFragment;
  private PlayingGameDrawable playingGameDrawable;
  private RecordStatusDrawable recordStatusDrawable;
  private SendingFileDrawable sendingFileDrawable;
  private SimpleTextView subtitleTextView;
  private ImageView timeItem;
  private TimerDrawable timerDrawable;
  private SimpleTextView titleTextView;
  private TypingDotsDrawable typingDotsDrawable;

  public ChatAvatarContainer(Context paramContext, ChatActivity paramChatActivity, boolean paramBoolean)
  {
    super(paramContext);
    this.parentFragment = paramChatActivity;
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0F));
    addView(this.avatarImageView);
    this.titleTextView = new SimpleTextView(paramContext);
    this.titleTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
    this.titleTextView.setTextSize(18);
    this.titleTextView.setGravity(3);
    this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextView.setLeftDrawableTopPadding(-AndroidUtilities.dp(1.3F));
    addView(this.titleTextView);
    this.subtitleTextView = new SimpleTextView(paramContext);
    this.subtitleTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
    this.subtitleTextView.setTextSize(14);
    this.subtitleTextView.setGravity(3);
    addView(this.subtitleTextView);
    if (paramBoolean)
    {
      this.timeItem = new ImageView(paramContext);
      this.timeItem.setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F));
      this.timeItem.setScaleType(ImageView.ScaleType.CENTER);
      paramChatActivity = this.timeItem;
      paramContext = new TimerDrawable(paramContext);
      this.timerDrawable = paramContext;
      paramChatActivity.setImageDrawable(paramContext);
      addView(this.timeItem);
      this.timeItem.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          ChatAvatarContainer.this.parentFragment.showDialog(AlertsCreator.createTTLAlert(ChatAvatarContainer.this.getContext(), ChatAvatarContainer.this.parentFragment.getCurrentEncryptedChat()).create());
        }
      });
    }
    setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        paramView = ChatAvatarContainer.this.parentFragment.getCurrentUser();
        Object localObject = ChatAvatarContainer.this.parentFragment.getCurrentChat();
        if (paramView != null)
        {
          localObject = new Bundle();
          ((Bundle)localObject).putInt("user_id", paramView.id);
          if (ChatAvatarContainer.this.timeItem != null)
            ((Bundle)localObject).putLong("dialog_id", ChatAvatarContainer.this.parentFragment.getDialogId());
          paramView = new ProfileActivity((Bundle)localObject);
          paramView.setPlayProfileAnimation(true);
          ChatAvatarContainer.this.parentFragment.presentFragment(paramView);
        }
        do
          return;
        while (localObject == null);
        paramView = new Bundle();
        paramView.putInt("chat_id", ((TLRPC.Chat)localObject).id);
        paramView = new ProfileActivity(paramView);
        paramView.setChatInfo(ChatAvatarContainer.this.parentFragment.getCurrentChatInfo());
        paramView.setPlayProfileAnimation(true);
        ChatAvatarContainer.this.parentFragment.presentFragment(paramView);
      }
    });
    paramContext = this.parentFragment.getCurrentChat();
    this.typingDotsDrawable = new TypingDotsDrawable();
    paramChatActivity = this.typingDotsDrawable;
    if (paramContext != null)
    {
      paramBoolean = true;
      paramChatActivity.setIsChat(paramBoolean);
      this.recordStatusDrawable = new RecordStatusDrawable();
      paramChatActivity = this.recordStatusDrawable;
      if (paramContext == null)
        break label419;
      paramBoolean = true;
      label353: paramChatActivity.setIsChat(paramBoolean);
      this.sendingFileDrawable = new SendingFileDrawable();
      paramChatActivity = this.sendingFileDrawable;
      if (paramContext == null)
        break label424;
      paramBoolean = true;
      label380: paramChatActivity.setIsChat(paramBoolean);
      this.playingGameDrawable = new PlayingGameDrawable();
      paramChatActivity = this.playingGameDrawable;
      if (paramContext == null)
        break label429;
    }
    label419: label424: label429: for (paramBoolean = bool; ; paramBoolean = false)
    {
      paramChatActivity.setIsChat(paramBoolean);
      return;
      paramBoolean = false;
      break;
      paramBoolean = false;
      break label353;
      paramBoolean = false;
      break label380;
    }
  }

  private void setTypingAnimation(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      try
      {
        Integer localInteger = (Integer)MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.parentFragment.getDialogId()));
        if (localInteger.intValue() == 0)
        {
          this.subtitleTextView.setLeftDrawable(this.typingDotsDrawable);
          this.typingDotsDrawable.start();
          this.recordStatusDrawable.stop();
          this.sendingFileDrawable.stop();
          this.playingGameDrawable.stop();
          return;
        }
        if (localInteger.intValue() == 1)
        {
          this.subtitleTextView.setLeftDrawable(this.recordStatusDrawable);
          this.recordStatusDrawable.start();
          this.typingDotsDrawable.stop();
          this.sendingFileDrawable.stop();
          this.playingGameDrawable.stop();
          return;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
      if (localException.intValue() == 2)
      {
        this.subtitleTextView.setLeftDrawable(this.sendingFileDrawable);
        this.sendingFileDrawable.start();
        this.typingDotsDrawable.stop();
        this.recordStatusDrawable.stop();
        this.playingGameDrawable.stop();
        return;
      }
      if (localException.intValue() == 3)
      {
        this.subtitleTextView.setLeftDrawable(this.playingGameDrawable);
        this.playingGameDrawable.start();
        this.typingDotsDrawable.stop();
        this.recordStatusDrawable.stop();
        this.sendingFileDrawable.stop();
        return;
      }
    }
    else
    {
      this.subtitleTextView.setLeftDrawable(null);
      this.typingDotsDrawable.stop();
      this.recordStatusDrawable.stop();
      this.sendingFileDrawable.stop();
      this.playingGameDrawable.stop();
    }
  }

  public void checkAndUpdateAvatar()
  {
    Object localObject3 = null;
    Object localObject2 = null;
    Object localObject1 = null;
    TLRPC.User localUser = this.parentFragment.getCurrentUser();
    TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
    if (localUser != null)
    {
      if (localUser.photo != null)
        localObject1 = localUser.photo.photo_small;
      this.avatarDrawable.setInfo(localUser);
    }
    while (true)
    {
      if (this.avatarImageView != null)
        this.avatarImageView.setImage((TLObject)localObject1, "50_50", this.avatarDrawable);
      return;
      localObject1 = localObject3;
      if (localChat == null)
        continue;
      localObject1 = localObject2;
      if (localChat.photo != null)
        localObject1 = localChat.photo.photo_small;
      this.avatarDrawable.setInfo(localChat);
    }
  }

  public SimpleTextView getSubtitleTextView()
  {
    return this.subtitleTextView;
  }

  public ImageView getTimeItem()
  {
    return this.timeItem;
  }

  public SimpleTextView getTitleTextView()
  {
    return this.titleTextView;
  }

  public void hideTimeItem()
  {
    if (this.timeItem == null)
      return;
    this.timeItem.setVisibility(8);
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt2 = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(42.0F)) / 2;
    if (Build.VERSION.SDK_INT >= 21);
    for (paramInt1 = AndroidUtilities.statusBarHeight; ; paramInt1 = 0)
    {
      paramInt1 += paramInt2;
      this.avatarImageView.layout(AndroidUtilities.dp(8.0F), paramInt1, AndroidUtilities.dp(50.0F), AndroidUtilities.dp(42.0F) + paramInt1);
      this.titleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(1.3F) + paramInt1, AndroidUtilities.dp(62.0F) + this.titleTextView.getMeasuredWidth(), this.titleTextView.getTextHeight() + paramInt1 + AndroidUtilities.dp(1.3F));
      if (this.timeItem != null)
        this.timeItem.layout(AndroidUtilities.dp(24.0F), AndroidUtilities.dp(15.0F) + paramInt1, AndroidUtilities.dp(58.0F), AndroidUtilities.dp(49.0F) + paramInt1);
      this.subtitleTextView.layout(AndroidUtilities.dp(62.0F), AndroidUtilities.dp(24.0F) + paramInt1, AndroidUtilities.dp(62.0F) + this.subtitleTextView.getMeasuredWidth(), paramInt1 + this.subtitleTextView.getTextHeight() + AndroidUtilities.dp(24.0F));
      return;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = View.MeasureSpec.getSize(paramInt1);
    int i = paramInt1 - AndroidUtilities.dp(70.0F);
    this.avatarImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0F), 1073741824));
    this.titleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0F), -2147483648));
    this.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(i, -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0F), -2147483648));
    if (this.timeItem != null)
      this.timeItem.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(34.0F), 1073741824));
    setMeasuredDimension(paramInt1, View.MeasureSpec.getSize(paramInt2));
  }

  public void setTime(int paramInt)
  {
    if (this.timerDrawable == null)
      return;
    this.timerDrawable.setTime(paramInt);
  }

  public void setTitle(CharSequence paramCharSequence)
  {
    this.titleTextView.setText(paramCharSequence);
  }

  public void setTitleIcons(int paramInt1, int paramInt2)
  {
    this.titleTextView.setLeftDrawable(paramInt1);
    this.titleTextView.setRightDrawable(paramInt2);
  }

  public void setTitleIcons(Drawable paramDrawable1, Drawable paramDrawable2)
  {
    this.titleTextView.setLeftDrawable(paramDrawable1);
    this.titleTextView.setRightDrawable(paramDrawable2);
  }

  public void showTimeItem()
  {
    if (this.timeItem == null)
      return;
    this.timeItem.setVisibility(0);
  }

  public void updateOnlineCount()
  {
    this.onlineCount = 0;
    TLRPC.ChatFull localChatFull = this.parentFragment.getCurrentChatInfo();
    if (localChatFull == null);
    while (true)
    {
      return;
      int j = ConnectionsManager.getInstance().getCurrentTime();
      if ((!(localChatFull instanceof TLRPC.TL_chatFull)) && ((!(localChatFull instanceof TLRPC.TL_channelFull)) || (localChatFull.participants_count > 200) || (localChatFull.participants == null)))
        continue;
      int i = 0;
      while (i < localChatFull.participants.participants.size())
      {
        Object localObject = (TLRPC.ChatParticipant)localChatFull.participants.participants.get(i);
        localObject = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.ChatParticipant)localObject).user_id));
        if ((localObject != null) && (((TLRPC.User)localObject).status != null) && ((((TLRPC.User)localObject).status.expires > j) || (((TLRPC.User)localObject).id == UserConfig.getClientUserId())) && (((TLRPC.User)localObject).status.expires > 10000))
          this.onlineCount += 1;
        i += 1;
      }
    }
  }

  public void updateSubtitle()
  {
    Object localObject2 = this.parentFragment.getCurrentUser();
    TLRPC.Chat localChat = this.parentFragment.getCurrentChat();
    CharSequence localCharSequence = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.parentFragment.getDialogId()));
    Object localObject1 = localCharSequence;
    if (localCharSequence != null)
      localObject1 = TextUtils.replace(localCharSequence, new String[] { "..." }, new String[] { "" });
    if ((localObject1 == null) || (((CharSequence)localObject1).length() == 0) || ((ChatObject.isChannel(localChat)) && (!localChat.megagroup)))
    {
      setTypingAnimation(false);
      if (localChat != null)
      {
        localObject1 = this.parentFragment.getCurrentChatInfo();
        if (ChatObject.isChannel(localChat))
          if ((localObject1 != null) && (((TLRPC.ChatFull)localObject1).participants_count != 0))
            if ((localChat.megagroup) && (((TLRPC.ChatFull)localObject1).participants_count <= 200))
              if ((this.onlineCount > 1) && (((TLRPC.ChatFull)localObject1).participants_count != 0))
                this.subtitleTextView.setText(String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count), LocaleController.formatPluralString("Online", this.onlineCount) }));
      }
      do
      {
        return;
        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", ((TLRPC.ChatFull)localObject1).participants_count));
        return;
        localObject2 = new int[1];
        localObject1 = LocaleController.formatShortNumber(((TLRPC.ChatFull)localObject1).participants_count, localObject2);
        localObject1 = LocaleController.formatPluralString("Members", localObject2[0]).replace(String.format("%d", new Object[] { Integer.valueOf(localObject2[0]) }), (CharSequence)localObject1);
        this.subtitleTextView.setText((CharSequence)localObject1);
        return;
        if (localChat.megagroup)
        {
          this.subtitleTextView.setText(LocaleController.getString("Loading", 2131165920).toLowerCase());
          return;
        }
        if ((localChat.flags & 0x40) != 0)
        {
          this.subtitleTextView.setText(LocaleController.getString("ChannelPublic", 2131165509).toLowerCase());
          return;
        }
        this.subtitleTextView.setText(LocaleController.getString("ChannelPrivate", 2131165506).toLowerCase());
        return;
        if (ChatObject.isKickedFromChat(localChat))
        {
          this.subtitleTextView.setText(LocaleController.getString("YouWereKicked", 2131166646));
          return;
        }
        if (ChatObject.isLeftFromChat(localChat))
        {
          this.subtitleTextView.setText(LocaleController.getString("YouLeft", 2131166645));
          return;
        }
        int j = localChat.participants_count;
        int i = j;
        if (localObject1 != null)
        {
          i = j;
          if (((TLRPC.ChatFull)localObject1).participants != null)
            i = ((TLRPC.ChatFull)localObject1).participants.participants.size();
        }
        if ((this.onlineCount > 1) && (i != 0))
        {
          this.subtitleTextView.setText(String.format("%s, %s", new Object[] { LocaleController.formatPluralString("Members", i), LocaleController.formatPluralString("Online", this.onlineCount) }));
          return;
        }
        this.subtitleTextView.setText(LocaleController.formatPluralString("Members", i));
        return;
      }
      while (localObject2 == null);
      localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(((TLRPC.User)localObject2).id));
      if (localObject1 == null)
        break label653;
    }
    while (true)
    {
      if (((TLRPC.User)localObject1).id == UserConfig.getClientUserId())
        localObject1 = LocaleController.getString("ChatYourSelf", 2131165539);
      while (true)
      {
        this.subtitleTextView.setText((CharSequence)localObject1);
        return;
        if ((((TLRPC.User)localObject1).id == 333000) || (((TLRPC.User)localObject1).id == 777000))
        {
          localObject1 = LocaleController.getString("ServiceNotifications", 2131166434);
          continue;
        }
        if (((TLRPC.User)localObject1).bot)
        {
          localObject1 = LocaleController.getString("Bot", 2131165390);
          continue;
        }
        localObject1 = LocaleController.formatUserStatus((TLRPC.User)localObject1);
      }
      this.subtitleTextView.setText((CharSequence)localObject1);
      setTypingAnimation(true);
      return;
      label653: localObject1 = localObject2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ChatAvatarContainer
 * JD-Core Version:    0.6.0
 */