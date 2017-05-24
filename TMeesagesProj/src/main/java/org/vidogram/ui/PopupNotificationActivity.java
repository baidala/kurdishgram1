package org.vidogram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.NotificationsController;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.GeoPoint;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.ChatActivityEnterView;
import org.vidogram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.PlayingGameDrawable;
import org.vidogram.ui.Components.PopupAudioView;
import org.vidogram.ui.Components.RecordStatusDrawable;
import org.vidogram.ui.Components.SendingFileDrawable;
import org.vidogram.ui.Components.SizeNotifierFrameLayout;
import org.vidogram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity extends Activity
  implements NotificationCenter.NotificationCenterDelegate
{
  private ActionBar actionBar;
  private boolean animationInProgress = false;
  private long animationStartTime = 0L;
  private ArrayList<ViewGroup> audioViews = new ArrayList();
  private FrameLayout avatarContainer;
  private BackupImageView avatarImageView;
  private ViewGroup centerView;
  private ChatActivityEnterView chatActivityEnterView;
  private int classGuid;
  private TextView countText;
  private TLRPC.Chat currentChat;
  private int currentMessageNum = 0;
  private MessageObject currentMessageObject = null;
  private TLRPC.User currentUser;
  private boolean finished = false;
  private ArrayList<ViewGroup> imageViews = new ArrayList();
  private boolean isReply;
  private CharSequence lastPrintString;
  private ViewGroup leftView;
  private ViewGroup messageContainer;
  private float moveStartX = -1.0F;
  private TextView nameTextView;
  private Runnable onAnimationEndRunnable = null;
  private TextView onlineTextView;
  private PlayingGameDrawable playingGameDrawable;
  private RelativeLayout popupContainer;
  private ArrayList<MessageObject> popupMessages = new ArrayList();
  private RecordStatusDrawable recordStatusDrawable;
  private ViewGroup rightView;
  private SendingFileDrawable sendingFileDrawable;
  private boolean startedMoving = false;
  private ArrayList<ViewGroup> textViews = new ArrayList();
  private TypingDotsDrawable typingDotsDrawable;
  private VelocityTracker velocityTracker = null;
  private PowerManager.WakeLock wakeLock = null;

  private void applyViewsLayoutParams(int paramInt)
  {
    int i = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
    FrameLayout.LayoutParams localLayoutParams;
    if (this.leftView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = (-i + paramInt);
      this.leftView.setLayoutParams(localLayoutParams);
    }
    if (this.centerView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.centerView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = paramInt;
      this.centerView.setLayoutParams(localLayoutParams);
    }
    if (this.rightView != null)
    {
      localLayoutParams = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
      localLayoutParams.gravity = 51;
      localLayoutParams.height = -1;
      localLayoutParams.width = i;
      localLayoutParams.leftMargin = (i + paramInt);
      this.rightView.setLayoutParams(localLayoutParams);
    }
    this.messageContainer.invalidate();
  }

  private void checkAndUpdateAvatar()
  {
    Object localObject2 = null;
    AvatarDrawable localAvatarDrawable = null;
    Object localObject1 = null;
    if (this.currentChat != null)
    {
      localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentChat.id));
      if (localObject2 != null);
    }
    label179: 
    while (true)
    {
      return;
      this.currentChat = ((TLRPC.Chat)localObject2);
      if (this.currentChat.photo != null)
        localObject1 = this.currentChat.photo.photo_small;
      localAvatarDrawable = new AvatarDrawable(this.currentChat);
      localObject2 = localObject1;
      localObject1 = localAvatarDrawable;
      while (true)
      {
        if (this.avatarImageView == null)
          break label179;
        this.avatarImageView.setImage((TLObject)localObject2, "50_50", (Drawable)localObject1);
        return;
        if (this.currentUser != null)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
          if (localObject1 == null)
            break;
          this.currentUser = ((TLRPC.User)localObject1);
          localObject1 = localAvatarDrawable;
          if (this.currentUser.photo != null)
            localObject1 = this.currentUser.photo.photo_small;
          localAvatarDrawable = new AvatarDrawable(this.currentUser);
          localObject2 = localObject1;
          localObject1 = localAvatarDrawable;
          continue;
        }
        localAvatarDrawable = null;
        localObject1 = localObject2;
        localObject2 = localAvatarDrawable;
      }
    }
  }

  private void fixLayout()
  {
    if (this.avatarContainer != null)
      this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          if (PopupNotificationActivity.this.avatarContainer != null)
            PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          int i = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0F)) / 2;
          PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), i, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), i);
          return true;
        }
      });
    if (this.messageContainer != null)
      this.messageContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          if ((!PopupNotificationActivity.this.checkTransitionAnimation()) && (!PopupNotificationActivity.this.startedMoving))
          {
            ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)PopupNotificationActivity.this.messageContainer.getLayoutParams();
            localMarginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
            localMarginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0F);
            localMarginLayoutParams.width = -1;
            localMarginLayoutParams.height = -1;
            PopupNotificationActivity.this.messageContainer.setLayoutParams(localMarginLayoutParams);
            PopupNotificationActivity.this.applyViewsLayoutParams(0);
          }
          return true;
        }
      });
  }

  private void getNewMessage()
  {
    if (this.popupMessages.isEmpty())
    {
      onFinish();
      finish();
      return;
    }
    if (((this.currentMessageNum != 0) || (this.chatActivityEnterView.hasText()) || (this.startedMoving)) && (this.currentMessageObject != null))
    {
      i = 0;
      if (i < this.popupMessages.size())
        if (((MessageObject)this.popupMessages.get(i)).getId() == this.currentMessageObject.getId())
          this.currentMessageNum = i;
    }
    for (int i = 1; ; i = 0)
    {
      if (i == 0)
      {
        this.currentMessageNum = 0;
        this.currentMessageObject = ((MessageObject)this.popupMessages.get(0));
        updateInterfaceForCurrentMessage(0);
      }
      while (true)
      {
        this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
        return;
        i += 1;
        break;
        if (!this.startedMoving)
          continue;
        if (this.currentMessageNum == this.popupMessages.size() - 1)
        {
          prepareLayouts(3);
          continue;
        }
        if (this.currentMessageNum != 1)
          continue;
        prepareLayouts(4);
      }
    }
  }

  private ViewGroup getViewForMessage(int paramInt, boolean paramBoolean)
  {
    Object localObject2;
    if ((this.popupMessages.size() == 1) && ((paramInt < 0) || (paramInt >= this.popupMessages.size())))
      localObject2 = null;
    int i;
    MessageObject localMessageObject;
    Object localObject1;
    label112: Object localObject3;
    TLRPC.PhotoSize localPhotoSize2;
    int j;
    while (true)
    {
      return localObject2;
      if (paramInt != -1)
        break;
      i = this.popupMessages.size() - 1;
      localMessageObject = (MessageObject)this.popupMessages.get(i);
      if ((localMessageObject.type != 1) && (localMessageObject.type != 4))
        break label803;
      if (this.imageViews.size() <= 0)
        break label431;
      localObject1 = (ViewGroup)this.imageViews.get(0);
      this.imageViews.remove(0);
      localObject2 = (TextView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(312));
      localObject3 = (BackupImageView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(311));
      ((BackupImageView)localObject3).setAspectFit(true);
      if (localMessageObject.type != 1)
        break label669;
      TLRPC.PhotoSize localPhotoSize1 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
      localPhotoSize2 = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, 100);
      j = 0;
      paramInt = j;
      if (localPhotoSize1 != null)
      {
        int k = 1;
        paramInt = k;
        if (localMessageObject.type == 1)
        {
          paramInt = k;
          if (!FileLoader.getPathToMessage(localMessageObject.messageOwner).exists())
            paramInt = 0;
        }
        if ((paramInt == 0) && (!MediaController.getInstance().canDownloadMedia(1)))
          break label625;
        ((BackupImageView)localObject3).setImage(localPhotoSize1.location, "100_100", localPhotoSize2.location, localPhotoSize1.size);
        paramInt = 1;
      }
      label268: if (paramInt != 0)
        break label653;
      ((BackupImageView)localObject3).setVisibility(8);
      ((TextView)localObject2).setVisibility(0);
      ((TextView)localObject2).setTextSize(2, MessagesController.getInstance().fontSize);
      ((TextView)localObject2).setText(localMessageObject.messageText);
      label308: if (((ViewGroup)localObject1).getParent() == null)
        this.messageContainer.addView((View)localObject1);
      ((ViewGroup)localObject1).setVisibility(0);
      localObject2 = localObject1;
      if (!paramBoolean)
        continue;
      paramInt = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
      localObject2 = (FrameLayout.LayoutParams)((ViewGroup)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).gravity = 51;
      ((FrameLayout.LayoutParams)localObject2).height = -1;
      ((FrameLayout.LayoutParams)localObject2).width = paramInt;
      if (i != this.currentMessageNum)
        break label1325;
      ((FrameLayout.LayoutParams)localObject2).leftMargin = 0;
    }
    while (true)
    {
      ((ViewGroup)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      ((ViewGroup)localObject1).invalidate();
      return localObject1;
      i = paramInt;
      if (paramInt != this.popupMessages.size())
        break;
      i = 0;
      break;
      label431: localObject1 = new FrameLayoutAnimationListener(this);
      localObject2 = new FrameLayout(this);
      ((FrameLayout)localObject2).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
      ((FrameLayout)localObject2).setBackgroundDrawable(Theme.getSelectorDrawable(false));
      ((ViewGroup)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
      localObject3 = new BackupImageView(this);
      ((BackupImageView)localObject3).setTag(Integer.valueOf(311));
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-1, -1.0F));
      localObject3 = new TextView(this);
      ((TextView)localObject3).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      ((TextView)localObject3).setTextSize(1, 16.0F);
      ((TextView)localObject3).setGravity(17);
      ((TextView)localObject3).setTag(Integer.valueOf(312));
      ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-1, -2, 17));
      ((ViewGroup)localObject1).setTag(Integer.valueOf(2));
      ((ViewGroup)localObject1).setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PopupNotificationActivity.this.openCurrentMessage();
        }
      });
      break label112;
      label625: paramInt = j;
      if (localPhotoSize2 == null)
        break label268;
      ((BackupImageView)localObject3).setImage(localPhotoSize2.location, null, (Drawable)null);
      paramInt = 1;
      break label268;
      label653: ((BackupImageView)localObject3).setVisibility(0);
      ((TextView)localObject2).setVisibility(8);
      break label308;
      label669: if (localMessageObject.type != 4)
        break label308;
      ((TextView)localObject2).setVisibility(8);
      ((TextView)localObject2).setText(localMessageObject.messageText);
      ((BackupImageView)localObject3).setVisibility(0);
      double d1 = localMessageObject.messageOwner.media.geo.lat;
      double d2 = localMessageObject.messageOwner.media.geo._long;
      ((BackupImageView)localObject3).setImage(String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=13&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:big|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }), null, null);
      break label308;
      label803: if (localMessageObject.type == 2)
      {
        if (this.audioViews.size() > 0)
        {
          localObject1 = (ViewGroup)this.audioViews.get(0);
          this.audioViews.remove(0);
          localObject2 = (PopupAudioView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(300));
        }
        while (true)
        {
          ((PopupAudioView)localObject2).setMessageObject(localMessageObject);
          if (MediaController.getInstance().canDownloadMedia(2))
            ((PopupAudioView)localObject2).downloadAudioIfNeed();
          break;
          localObject1 = new FrameLayoutAnimationListener(this);
          localObject2 = new FrameLayout(this);
          ((FrameLayout)localObject2).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
          ((FrameLayout)localObject2).setBackgroundDrawable(Theme.getSelectorDrawable(false));
          ((ViewGroup)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -1.0F));
          localObject3 = new FrameLayout(this);
          ((FrameLayout)localObject2).addView((View)localObject3, LayoutHelper.createFrame(-1, -2.0F, 17, 20.0F, 0.0F, 20.0F, 0.0F));
          localObject2 = new PopupAudioView(this);
          ((PopupAudioView)localObject2).setTag(Integer.valueOf(300));
          ((FrameLayout)localObject3).addView((View)localObject2);
          ((ViewGroup)localObject1).setTag(Integer.valueOf(3));
          ((ViewGroup)localObject1).setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramView)
            {
              PopupNotificationActivity.this.openCurrentMessage();
            }
          });
        }
      }
      if (this.textViews.size() > 0)
      {
        localObject1 = (ViewGroup)this.textViews.get(0);
        this.textViews.remove(0);
      }
      while (true)
      {
        localObject2 = (TextView)((ViewGroup)localObject1).findViewWithTag(Integer.valueOf(301));
        ((TextView)localObject2).setTextSize(2, MessagesController.getInstance().fontSize);
        ((TextView)localObject2).setText(localMessageObject.messageText);
        break;
        localObject1 = new FrameLayoutAnimationListener(this);
        localObject3 = new ScrollView(this);
        ((ScrollView)localObject3).setFillViewport(true);
        ((ViewGroup)localObject1).addView((View)localObject3, LayoutHelper.createFrame(-1, -1.0F));
        localObject2 = new LinearLayout(this);
        ((LinearLayout)localObject2).setOrientation(0);
        ((LinearLayout)localObject2).setBackgroundDrawable(Theme.getSelectorDrawable(false));
        ((ScrollView)localObject3).addView((View)localObject2, LayoutHelper.createScroll(-1, -2, 1));
        ((LinearLayout)localObject2).setPadding(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F), AndroidUtilities.dp(10.0F));
        ((LinearLayout)localObject2).setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            PopupNotificationActivity.this.openCurrentMessage();
          }
        });
        localObject3 = new TextView(this);
        ((TextView)localObject3).setTextSize(1, 16.0F);
        ((TextView)localObject3).setTag(Integer.valueOf(301));
        ((TextView)localObject3).setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        ((TextView)localObject3).setLinkTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        ((TextView)localObject3).setGravity(17);
        ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, -2, 17));
        ((ViewGroup)localObject1).setTag(Integer.valueOf(1));
      }
      label1325: if (i == this.currentMessageNum - 1)
      {
        ((FrameLayout.LayoutParams)localObject2).leftMargin = (-paramInt);
        continue;
      }
      if (i != this.currentMessageNum + 1)
        continue;
      ((FrameLayout.LayoutParams)localObject2).leftMargin = paramInt;
    }
  }

  private void handleIntent(Intent paramIntent)
  {
    boolean bool;
    if ((paramIntent != null) && (paramIntent.getBooleanExtra("force", false)))
    {
      bool = true;
      this.isReply = bool;
      if (!this.isReply)
        break label93;
      this.popupMessages = NotificationsController.getInstance().popupReplyMessages;
      label39: if ((!((KeyguardManager)getSystemService("keyguard")).inKeyguardRestrictedInputMode()) && (ApplicationLoader.isScreenOn))
        break label106;
      getWindow().addFlags(2623490);
    }
    while (true)
    {
      if (this.currentMessageObject == null)
        this.currentMessageNum = 0;
      getNewMessage();
      return;
      bool = false;
      break;
      label93: this.popupMessages = NotificationsController.getInstance().popupMessages;
      break label39;
      label106: getWindow().addFlags(2623488);
      getWindow().clearFlags(2);
    }
  }

  private void openCurrentMessage()
  {
    if (this.currentMessageObject == null)
      return;
    Intent localIntent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    long l = this.currentMessageObject.getDialogId();
    int i;
    if ((int)l != 0)
    {
      i = (int)l;
      if (i < 0)
        localIntent.putExtra("chatId", -i);
    }
    while (true)
    {
      localIntent.setAction("com.tmessages.openchat" + Math.random() + 2147483647);
      localIntent.setFlags(32768);
      startActivity(localIntent);
      onFinish();
      finish();
      return;
      localIntent.putExtra("userId", i);
      continue;
      localIntent.putExtra("encId", (int)(l >> 32));
    }
  }

  private void prepareLayouts(int paramInt)
  {
    if (paramInt == 0)
    {
      reuseView(this.centerView);
      reuseView(this.leftView);
      reuseView(this.rightView);
      paramInt = this.currentMessageNum - 1;
      if (paramInt < this.currentMessageNum + 2)
      {
        if (paramInt == this.currentMessageNum - 1)
          this.leftView = getViewForMessage(paramInt, true);
        while (true)
        {
          paramInt += 1;
          break;
          if (paramInt == this.currentMessageNum)
          {
            this.centerView = getViewForMessage(paramInt, true);
            continue;
          }
          if (paramInt != this.currentMessageNum + 1)
            continue;
          this.rightView = getViewForMessage(paramInt, true);
        }
      }
    }
    else
    {
      if (paramInt != 1)
        break label161;
      reuseView(this.rightView);
      this.rightView = this.centerView;
      this.centerView = this.leftView;
      this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
    }
    label161: 
    do
    {
      do
        while (true)
        {
          return;
          if (paramInt == 2)
          {
            reuseView(this.leftView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
            return;
          }
          if (paramInt != 3)
            break;
          if (this.rightView == null)
            continue;
          paramInt = ((FrameLayout.LayoutParams)this.rightView.getLayoutParams()).leftMargin;
          reuseView(this.rightView);
          localObject = getViewForMessage(this.currentMessageNum + 1, false);
          this.rightView = ((ViewGroup)localObject);
          if (localObject == null)
            continue;
          i = AndroidUtilities.displaySize.x;
          j = AndroidUtilities.dp(24.0F);
          localObject = (FrameLayout.LayoutParams)this.rightView.getLayoutParams();
          ((FrameLayout.LayoutParams)localObject).gravity = 51;
          ((FrameLayout.LayoutParams)localObject).height = -1;
          ((FrameLayout.LayoutParams)localObject).width = (i - j);
          ((FrameLayout.LayoutParams)localObject).leftMargin = paramInt;
          this.rightView.setLayoutParams((ViewGroup.LayoutParams)localObject);
          this.rightView.invalidate();
          return;
        }
      while ((paramInt != 4) || (this.leftView == null));
      paramInt = ((FrameLayout.LayoutParams)this.leftView.getLayoutParams()).leftMargin;
      reuseView(this.leftView);
      localObject = getViewForMessage(0, false);
      this.leftView = ((ViewGroup)localObject);
    }
    while (localObject == null);
    int i = AndroidUtilities.displaySize.x;
    int j = AndroidUtilities.dp(24.0F);
    Object localObject = (FrameLayout.LayoutParams)this.leftView.getLayoutParams();
    ((FrameLayout.LayoutParams)localObject).gravity = 51;
    ((FrameLayout.LayoutParams)localObject).height = -1;
    ((FrameLayout.LayoutParams)localObject).width = (i - j);
    ((FrameLayout.LayoutParams)localObject).leftMargin = paramInt;
    this.leftView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    this.leftView.invalidate();
  }

  private void reuseView(ViewGroup paramViewGroup)
  {
    if (paramViewGroup == null);
    int i;
    do
    {
      return;
      i = ((Integer)paramViewGroup.getTag()).intValue();
      paramViewGroup.setVisibility(8);
      if (i == 1)
      {
        this.textViews.add(paramViewGroup);
        return;
      }
      if (i != 2)
        continue;
      this.imageViews.add(paramViewGroup);
      return;
    }
    while (i != 3);
    this.audioViews.add(paramViewGroup);
  }

  private void setTypingAnimation(boolean paramBoolean)
  {
    if (this.actionBar == null);
    while (true)
    {
      return;
      if (!paramBoolean)
        break;
      try
      {
        Integer localInteger = (Integer)MessagesController.getInstance().printingStringsTypes.get(Long.valueOf(this.currentMessageObject.getDialogId()));
        if (localInteger.intValue() == 0)
        {
          this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.typingDotsDrawable, null, null, null);
          this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
          this.typingDotsDrawable.start();
          this.recordStatusDrawable.stop();
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
      if (localException.intValue() == 1)
      {
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.recordStatusDrawable, null, null, null);
        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
        this.recordStatusDrawable.start();
        this.typingDotsDrawable.stop();
        this.sendingFileDrawable.stop();
        this.playingGameDrawable.stop();
        return;
      }
      if (localException.intValue() == 2)
      {
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.sendingFileDrawable, null, null, null);
        this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
        this.sendingFileDrawable.start();
        this.typingDotsDrawable.stop();
        this.recordStatusDrawable.stop();
        this.playingGameDrawable.stop();
        return;
      }
      if (localException.intValue() != 3)
        continue;
      this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.playingGameDrawable, null, null, null);
      this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
      this.playingGameDrawable.start();
      this.typingDotsDrawable.stop();
      this.recordStatusDrawable.stop();
      this.sendingFileDrawable.stop();
      return;
    }
    this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    this.onlineTextView.setCompoundDrawablePadding(0);
    this.typingDotsDrawable.stop();
    this.recordStatusDrawable.stop();
    this.recordStatusDrawable.stop();
    this.sendingFileDrawable.stop();
  }

  private void switchToNextMessage()
  {
    if (this.popupMessages.size() > 1)
    {
      if (this.currentMessageNum >= this.popupMessages.size() - 1)
        break label103;
      this.currentMessageNum += 1;
    }
    while (true)
    {
      this.currentMessageObject = ((MessageObject)this.popupMessages.get(this.currentMessageNum));
      updateInterfaceForCurrentMessage(2);
      this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
      return;
      label103: this.currentMessageNum = 0;
    }
  }

  private void switchToPreviousMessage()
  {
    if (this.popupMessages.size() > 1)
    {
      if (this.currentMessageNum <= 0)
        break label94;
      this.currentMessageNum -= 1;
    }
    while (true)
    {
      this.currentMessageObject = ((MessageObject)this.popupMessages.get(this.currentMessageNum));
      updateInterfaceForCurrentMessage(1);
      this.countText.setText(String.format("%d/%d", new Object[] { Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size()) }));
      return;
      label94: this.currentMessageNum = (this.popupMessages.size() - 1);
    }
  }

  private void updateInterfaceForCurrentMessage(int paramInt)
  {
    if (this.actionBar == null)
      return;
    this.currentChat = null;
    this.currentUser = null;
    long l = this.currentMessageObject.getDialogId();
    this.chatActivityEnterView.setDialogId(l);
    int i;
    if ((int)l != 0)
    {
      i = (int)l;
      if (i > 0)
      {
        this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if ((this.currentChat == null) || (this.currentUser == null))
          break label218;
        this.nameTextView.setText(this.currentChat.title);
        this.onlineTextView.setText(UserObject.getUserName(this.currentUser));
        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        this.nameTextView.setCompoundDrawablePadding(0);
      }
    }
    while (true)
    {
      prepareLayouts(paramInt);
      updateSubtitle();
      checkAndUpdateAvatar();
      applyViewsLayoutParams(0);
      return;
      this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(-i));
      this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
      break;
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf((int)(l >> 32)));
      this.currentUser = MessagesController.getInstance().getUser(Integer.valueOf(localEncryptedChat.user_id));
      break;
      label218: if (this.currentUser == null)
        continue;
      this.nameTextView.setText(UserObject.getUserName(this.currentUser));
      if ((int)l == 0)
      {
        this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(2130837794, 0, 0, 0);
        this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0F));
        continue;
      }
      this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
      this.nameTextView.setCompoundDrawablePadding(0);
    }
  }

  private void updateSubtitle()
  {
    if (this.actionBar == null);
    do
      return;
    while ((this.currentChat != null) || (this.currentUser == null));
    if ((this.currentUser.id / 1000 != 777) && (this.currentUser.id / 1000 != 333) && (ContactsController.getInstance().contactsDict.get(this.currentUser.id) == null) && ((ContactsController.getInstance().contactsDict.size() != 0) || (!ContactsController.getInstance().isLoadingContacts())))
      if ((this.currentUser.phone != null) && (this.currentUser.phone.length() != 0))
        this.nameTextView.setText(b.a().e("+" + this.currentUser.phone));
    Object localObject;
    while (true)
    {
      localObject = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
      if ((localObject != null) && (((CharSequence)localObject).length() != 0))
        break;
      this.lastPrintString = null;
      setTypingAnimation(false);
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentUser.id));
      if (localObject != null)
        this.currentUser = ((TLRPC.User)localObject);
      this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentUser));
      return;
      this.nameTextView.setText(UserObject.getUserName(this.currentUser));
      continue;
      this.nameTextView.setText(UserObject.getUserName(this.currentUser));
    }
    this.lastPrintString = ((CharSequence)localObject);
    this.onlineTextView.setText((CharSequence)localObject);
    setTypingAnimation(true);
  }

  public boolean checkTransitionAnimation()
  {
    if ((this.animationInProgress) && (this.animationStartTime < System.currentTimeMillis() - 400L))
    {
      this.animationInProgress = false;
      if (this.onAnimationEndRunnable != null)
      {
        this.onAnimationEndRunnable.run();
        this.onAnimationEndRunnable = null;
      }
    }
    return this.animationInProgress;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.appDidLogout)
    {
      onFinish();
      finish();
    }
    do
      while (true)
      {
        return;
        if (paramInt == NotificationCenter.pushMessagesUpdated)
        {
          getNewMessage();
          return;
        }
        if (paramInt == NotificationCenter.updateInterfaces)
        {
          if (this.currentMessageObject == null)
            continue;
          paramInt = ((Integer)paramArrayOfObject[0]).intValue();
          if (((paramInt & 0x1) != 0) || ((paramInt & 0x4) != 0) || ((paramInt & 0x10) != 0) || ((paramInt & 0x20) != 0))
            updateSubtitle();
          if (((paramInt & 0x2) != 0) || ((paramInt & 0x8) != 0))
            checkAndUpdateAvatar();
          if ((paramInt & 0x40) == 0)
            continue;
          paramArrayOfObject = (CharSequence)MessagesController.getInstance().printingStrings.get(Long.valueOf(this.currentMessageObject.getDialogId()));
          if (((this.lastPrintString == null) || (paramArrayOfObject != null)) && ((this.lastPrintString != null) || (paramArrayOfObject == null)) && ((this.lastPrintString == null) || (paramArrayOfObject == null) || (this.lastPrintString.equals(paramArrayOfObject))))
            continue;
          updateSubtitle();
          return;
        }
        Object localObject;
        if (paramInt == NotificationCenter.audioDidReset)
        {
          paramArrayOfObject = (Integer)paramArrayOfObject[0];
          if (this.messageContainer == null)
            continue;
          i = this.messageContainer.getChildCount();
          paramInt = 0;
          while (paramInt < i)
          {
            localObject = this.messageContainer.getChildAt(paramInt);
            if (((Integer)((View)localObject).getTag()).intValue() == 3)
            {
              localObject = (PopupAudioView)((View)localObject).findViewWithTag(Integer.valueOf(300));
              if ((((PopupAudioView)localObject).getMessageObject() != null) && (((PopupAudioView)localObject).getMessageObject().getId() == paramArrayOfObject.intValue()))
              {
                ((PopupAudioView)localObject).updateButtonState();
                return;
              }
            }
            paramInt += 1;
          }
          continue;
        }
        if (paramInt == NotificationCenter.audioProgressDidChanged)
        {
          paramArrayOfObject = (Integer)paramArrayOfObject[0];
          if (this.messageContainer == null)
            continue;
          i = this.messageContainer.getChildCount();
          paramInt = 0;
          while (paramInt < i)
          {
            localObject = this.messageContainer.getChildAt(paramInt);
            if (((Integer)((View)localObject).getTag()).intValue() == 3)
            {
              localObject = (PopupAudioView)((View)localObject).findViewWithTag(Integer.valueOf(300));
              if ((((PopupAudioView)localObject).getMessageObject() != null) && (((PopupAudioView)localObject).getMessageObject().getId() == paramArrayOfObject.intValue()))
              {
                ((PopupAudioView)localObject).updateProgress();
                return;
              }
            }
            paramInt += 1;
          }
          continue;
        }
        if (paramInt != NotificationCenter.emojiDidLoaded)
          break;
        if (this.messageContainer == null)
          continue;
        int j = this.messageContainer.getChildCount();
        paramInt = i;
        while (paramInt < j)
        {
          paramArrayOfObject = this.messageContainer.getChildAt(paramInt);
          if (((Integer)paramArrayOfObject.getTag()).intValue() == 1)
          {
            paramArrayOfObject = (TextView)paramArrayOfObject.findViewWithTag(Integer.valueOf(301));
            if (paramArrayOfObject != null)
              paramArrayOfObject.invalidate();
          }
          paramInt += 1;
        }
      }
    while (paramInt != NotificationCenter.contactsDidLoaded);
    updateSubtitle();
  }

  public void onBackPressed()
  {
    if (this.chatActivityEnterView.isPopupShowing())
    {
      this.chatActivityEnterView.hidePopup(true);
      return;
    }
    super.onBackPressed();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    AndroidUtilities.checkDisplaySize(this, paramConfiguration);
    fixLayout();
  }

  protected void onCreate(Bundle paramBundle)
  {
    super.onCreate(paramBundle);
    Theme.createChatResources(this, false);
    int i = getResources().getIdentifier("status_bar_height", "dimen", "android");
    if (i > 0)
      AndroidUtilities.statusBarHeight = getResources().getDimensionPixelSize(i);
    this.classGuid = ConnectionsManager.getInstance().generateClassGuid();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.appDidLogout);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.contactsDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.typingDotsDrawable = new TypingDotsDrawable();
    this.recordStatusDrawable = new RecordStatusDrawable();
    this.sendingFileDrawable = new SendingFileDrawable();
    this.playingGameDrawable = new PlayingGameDrawable();
    paramBundle = new SizeNotifierFrameLayout(this)
    {
      protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
      {
        int n = getChildCount();
        int k;
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F))
          k = PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
        View localView;
        FrameLayout.LayoutParams localLayoutParams;
        int i1;
        int i2;
        int i;
        int j;
        while (true)
        {
          int m = 0;
          while (true)
          {
            if (m >= n)
              break label473;
            localView = getChildAt(m);
            if (localView.getVisibility() == 8)
            {
              m += 1;
              continue;
              k = 0;
              break;
            }
          }
          localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
          i1 = localView.getMeasuredWidth();
          i2 = localView.getMeasuredHeight();
          i = localLayoutParams.gravity;
          j = i;
          if (i == -1)
            j = 51;
          switch (j & 0x7 & 0x7)
          {
          default:
            i = localLayoutParams.leftMargin;
            label159: switch (j & 0x70)
            {
            default:
              j = localLayoutParams.topMargin;
              label207: if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView))
                if (k != 0)
                  j = getMeasuredHeight() - k;
            case 48:
            case 16:
            case 80:
            }
          case 1:
          case 5:
          }
        }
        while (true)
        {
          localView.layout(i, j, i + i1, j + i2);
          break;
          i = (paramInt3 - paramInt1 - i1) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
          break label159;
          i = paramInt3 - i1 - localLayoutParams.rightMargin;
          break label159;
          j = localLayoutParams.topMargin;
          break label207;
          j = (paramInt4 - k - paramInt2 - i2) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
          break label207;
          j = paramInt4 - k - paramInt2 - i2 - localLayoutParams.bottomMargin;
          break label207;
          j = getMeasuredHeight();
          continue;
          if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView))
          {
            j = PopupNotificationActivity.this.popupContainer.getTop();
            int i3 = PopupNotificationActivity.this.popupContainer.getMeasuredHeight();
            int i4 = localView.getMeasuredHeight();
            int i5 = localLayoutParams.bottomMargin;
            i = PopupNotificationActivity.this.popupContainer.getLeft() + PopupNotificationActivity.this.popupContainer.getMeasuredWidth() - localView.getMeasuredWidth() - localLayoutParams.rightMargin;
            j = j + i3 - i4 - i5;
            continue;
            label473: notifyHeightChanged();
            return;
          }
        }
      }

      protected void onMeasure(int paramInt1, int paramInt2)
      {
        View.MeasureSpec.getMode(paramInt1);
        View.MeasureSpec.getMode(paramInt2);
        int k = View.MeasureSpec.getSize(paramInt1);
        int i = View.MeasureSpec.getSize(paramInt2);
        setMeasuredDimension(k, i);
        if (getKeyboardHeight() <= AndroidUtilities.dp(20.0F))
          i -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
        while (true)
        {
          int m = getChildCount();
          int j = 0;
          if (j < m)
          {
            View localView = getChildAt(j);
            if (localView.getVisibility() == 8);
            while (true)
            {
              j += 1;
              break;
              if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(localView))
              {
                localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(localView.getLayoutParams().height, 1073741824));
                continue;
              }
              if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(localView))
              {
                measureChildWithMargins(localView, paramInt1, 0, paramInt2, 0);
                continue;
              }
              localView.measure(View.MeasureSpec.makeMeasureSpec(k, 1073741824), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0F), AndroidUtilities.dp(2.0F) + i), 1073741824));
            }
          }
          return;
        }
      }
    };
    setContentView(paramBundle);
    paramBundle.setBackgroundColor(-1728053248);
    RelativeLayout localRelativeLayout = new RelativeLayout(this);
    paramBundle.addView(localRelativeLayout, LayoutHelper.createFrame(-1, -1.0F));
    this.popupContainer = new RelativeLayout(this);
    this.popupContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    localRelativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
    if (this.chatActivityEnterView != null)
      this.chatActivityEnterView.onDestroy();
    this.chatActivityEnterView = new ChatActivityEnterView(this, paramBundle, null, false);
    this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
    this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate()
    {
      public void didPressedAttachButton()
      {
      }

      public void needSendTyping()
      {
        if (PopupNotificationActivity.this.currentMessageObject != null)
          MessagesController.getInstance().sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0, PopupNotificationActivity.this.classGuid);
      }

      public void needStartRecordVideo(int paramInt)
      {
      }

      public void onAttachButtonHidden()
      {
      }

      public void onAttachButtonShow()
      {
      }

      public void onMessageEditEnd(boolean paramBoolean)
      {
      }

      public void onMessageSend(CharSequence paramCharSequence)
      {
        if (PopupNotificationActivity.this.currentMessageObject == null)
          return;
        if ((PopupNotificationActivity.this.currentMessageNum >= 0) && (PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()))
          PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
        MessagesController.getInstance().markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, true);
        PopupNotificationActivity.access$302(PopupNotificationActivity.this, null);
        PopupNotificationActivity.this.getNewMessage();
      }

      public void onStickersTab(boolean paramBoolean)
      {
      }

      public void onTextChanged(CharSequence paramCharSequence, boolean paramBoolean)
      {
      }

      public void onWindowSizeChanged(int paramInt)
      {
      }
    });
    this.messageContainer = new FrameLayoutTouch(this);
    this.popupContainer.addView(this.messageContainer, 0);
    this.actionBar = new ActionBar(this);
    this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setBackButtonImage(2130837766);
    this.actionBar.setBackgroundColor(Theme.getColor("actionBarDefault"));
    this.actionBar.setItemsBackgroundColor(Theme.getColor("actionBarDefaultSelector"), false);
    this.popupContainer.addView(this.actionBar);
    paramBundle = this.actionBar.getLayoutParams();
    paramBundle.width = -1;
    this.actionBar.setLayoutParams(paramBundle);
    paramBundle = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0F));
    this.countText = new TextView(this);
    this.countText.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
    this.countText.setTextSize(1, 14.0F);
    this.countText.setGravity(17);
    paramBundle.addView(this.countText, LayoutHelper.createFrame(56, -1.0F));
    this.avatarContainer = new FrameLayout(this);
    this.avatarContainer.setPadding(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
    this.actionBar.addView(this.avatarContainer);
    paramBundle = (FrameLayout.LayoutParams)this.avatarContainer.getLayoutParams();
    paramBundle.height = -1;
    paramBundle.width = -2;
    paramBundle.rightMargin = AndroidUtilities.dp(48.0F);
    paramBundle.leftMargin = AndroidUtilities.dp(60.0F);
    paramBundle.gravity = 51;
    this.avatarContainer.setLayoutParams(paramBundle);
    this.avatarImageView = new BackupImageView(this);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarContainer.addView(this.avatarImageView);
    paramBundle = (FrameLayout.LayoutParams)this.avatarImageView.getLayoutParams();
    paramBundle.width = AndroidUtilities.dp(42.0F);
    paramBundle.height = AndroidUtilities.dp(42.0F);
    paramBundle.topMargin = AndroidUtilities.dp(3.0F);
    this.avatarImageView.setLayoutParams(paramBundle);
    this.nameTextView = new TextView(this);
    this.nameTextView.setTextColor(Theme.getColor("actionBarDefaultTitle"));
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.avatarContainer.addView(this.nameTextView);
    paramBundle = (FrameLayout.LayoutParams)this.nameTextView.getLayoutParams();
    paramBundle.width = -2;
    paramBundle.height = -2;
    paramBundle.leftMargin = AndroidUtilities.dp(54.0F);
    paramBundle.bottomMargin = AndroidUtilities.dp(22.0F);
    paramBundle.gravity = 80;
    this.nameTextView.setLayoutParams(paramBundle);
    this.onlineTextView = new TextView(this);
    this.onlineTextView.setTextColor(Theme.getColor("actionBarDefaultSubtitle"));
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    this.avatarContainer.addView(this.onlineTextView);
    paramBundle = (FrameLayout.LayoutParams)this.onlineTextView.getLayoutParams();
    paramBundle.width = -2;
    paramBundle.height = -2;
    paramBundle.leftMargin = AndroidUtilities.dp(54.0F);
    paramBundle.bottomMargin = AndroidUtilities.dp(4.0F);
    paramBundle.gravity = 80;
    this.onlineTextView.setLayoutParams(paramBundle);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
        {
          PopupNotificationActivity.this.onFinish();
          PopupNotificationActivity.this.finish();
        }
        do
        {
          return;
          if (paramInt != 1)
            continue;
          PopupNotificationActivity.this.openCurrentMessage();
          return;
        }
        while (paramInt != 2);
        PopupNotificationActivity.this.switchToNextMessage();
      }
    });
    this.wakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
    this.wakeLock.setReferenceCounted(false);
    handleIntent(getIntent());
  }

  protected void onDestroy()
  {
    super.onDestroy();
    onFinish();
    if (this.wakeLock.isHeld())
      this.wakeLock.release();
    if (this.avatarImageView != null)
      this.avatarImageView.setImageDrawable(null);
  }

  protected void onFinish()
  {
    if (this.finished);
    do
    {
      return;
      this.finished = true;
      if (this.isReply)
        this.popupMessages.clear();
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.appDidLogout);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.contactsDidLoaded);
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
      if (this.chatActivityEnterView == null)
        continue;
      this.chatActivityEnterView.onDestroy();
    }
    while (!this.wakeLock.isHeld());
    this.wakeLock.release();
  }

  protected void onNewIntent(Intent paramIntent)
  {
    super.onNewIntent(paramIntent);
    handleIntent(paramIntent);
  }

  protected void onPause()
  {
    super.onPause();
    overridePendingTransition(0, 0);
    if (this.chatActivityEnterView != null)
    {
      this.chatActivityEnterView.hidePopup(false);
      this.chatActivityEnterView.setFieldFocused(false);
    }
    ConnectionsManager.getInstance().setAppPaused(true, false);
  }

  public void onRequestPermissionsResult(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfInt);
    if ((paramInt != 3) || (paramArrayOfInt[0] == 0))
      return;
    paramArrayOfString = new AlertDialog.Builder(this);
    paramArrayOfString.setTitle(LocaleController.getString("AppName", 2131165319));
    paramArrayOfString.setMessage(LocaleController.getString("PermissionNoAudio", 2131166255));
    paramArrayOfString.setNegativeButton(LocaleController.getString("PermissionOpenSettings", 2131166260), new DialogInterface.OnClickListener()
    {
      @TargetApi(9)
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        try
        {
          paramDialogInterface = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
          paramDialogInterface.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
          PopupNotificationActivity.this.startActivity(paramDialogInterface);
          return;
        }
        catch (Exception paramDialogInterface)
        {
          FileLog.e(paramDialogInterface);
        }
      }
    });
    paramArrayOfString.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
    paramArrayOfString.show();
  }

  protected void onResume()
  {
    super.onResume();
    if (this.chatActivityEnterView != null)
      this.chatActivityEnterView.setFieldFocused(true);
    ConnectionsManager.getInstance().setAppPaused(false, false);
    fixLayout();
    checkAndUpdateAvatar();
    this.wakeLock.acquire(7000L);
  }

  public boolean onTouchEventMy(MotionEvent paramMotionEvent)
  {
    if (checkTransitionAnimation())
      return false;
    if ((paramMotionEvent != null) && (paramMotionEvent.getAction() == 0))
      this.moveStartX = paramMotionEvent.getX();
    int m;
    int j;
    label199: label209: 
    do
      while (true)
      {
        return this.startedMoving;
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2))
          break;
        float f = paramMotionEvent.getX();
        m = (int)(f - this.moveStartX);
        int i = m;
        if (this.moveStartX != -1.0F)
        {
          i = m;
          if (!this.startedMoving)
          {
            i = m;
            if (Math.abs(m) > AndroidUtilities.dp(10.0F))
            {
              this.startedMoving = true;
              this.moveStartX = f;
              AndroidUtilities.lockOrientation(this);
              if (this.velocityTracker != null)
                break label199;
              this.velocityTracker = VelocityTracker.obtain();
            }
          }
        }
        for (j = 0; ; j = 0)
        {
          if (!this.startedMoving)
            break label209;
          m = j;
          if (this.leftView == null)
          {
            m = j;
            if (j > 0)
              m = 0;
          }
          j = m;
          if (this.rightView == null)
          {
            j = m;
            if (m < 0)
              j = 0;
          }
          if (this.velocityTracker != null)
            this.velocityTracker.addMovement(paramMotionEvent);
          applyViewsLayoutParams(j);
          break;
          this.velocityTracker.clear();
        }
      }
    while ((paramMotionEvent != null) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 3));
    Object localObject;
    int n;
    if ((paramMotionEvent != null) && (this.startedMoving))
    {
      localObject = (FrameLayout.LayoutParams)this.centerView.getLayoutParams();
      n = (int)(paramMotionEvent.getX() - this.moveStartX);
      m = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0F);
      if (this.velocityTracker == null)
        break label622;
      this.velocityTracker.computeCurrentVelocity(1000);
      if (this.velocityTracker.getXVelocity() >= 3500.0F)
        j = 1;
    }
    while (true)
    {
      label313: if (((j == 1) || (n > m / 3)) && (this.leftView != null))
      {
        j = m - ((FrameLayout.LayoutParams)localObject).leftMargin;
        paramMotionEvent = this.leftView;
        this.onAnimationEndRunnable = new Runnable()
        {
          public void run()
          {
            PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
            PopupNotificationActivity.this.switchToPreviousMessage();
            AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
          }
        };
      }
      while (true)
      {
        label360: if (j != 0)
        {
          m = (int)(Math.abs(j / m) * 200.0F);
          localObject = new TranslateAnimation(0.0F, j, 0.0F, 0.0F);
          ((TranslateAnimation)localObject).setDuration(m);
          this.centerView.startAnimation((Animation)localObject);
          if (paramMotionEvent != null)
          {
            localObject = new TranslateAnimation(0.0F, j, 0.0F, 0.0F);
            ((TranslateAnimation)localObject).setDuration(m);
            paramMotionEvent.startAnimation((Animation)localObject);
          }
          this.animationInProgress = true;
          this.animationStartTime = System.currentTimeMillis();
        }
        while (true)
        {
          if (this.velocityTracker != null)
          {
            this.velocityTracker.recycle();
            this.velocityTracker = null;
          }
          this.startedMoving = false;
          this.moveStartX = -1.0F;
          break;
          if (this.velocityTracker.getXVelocity() > -3500.0F)
            break label622;
          k = 2;
          break label313;
          if (((k == 2) || (n < -m / 3)) && (this.rightView != null))
          {
            k = -m - ((FrameLayout.LayoutParams)localObject).leftMargin;
            paramMotionEvent = this.rightView;
            this.onAnimationEndRunnable = new Runnable()
            {
              public void run()
              {
                PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
                PopupNotificationActivity.this.switchToNextMessage();
                AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
              }
            };
            break label360;
          }
          if (((FrameLayout.LayoutParams)localObject).leftMargin == 0)
            break label615;
          k = -((FrameLayout.LayoutParams)localObject).leftMargin;
          if (n > 0);
          for (paramMotionEvent = this.leftView; ; paramMotionEvent = this.rightView)
          {
            this.onAnimationEndRunnable = new Runnable()
            {
              public void run()
              {
                PopupNotificationActivity.access$1002(PopupNotificationActivity.this, false);
                PopupNotificationActivity.this.applyViewsLayoutParams(0);
                AndroidUtilities.unlockOrientation(PopupNotificationActivity.this);
              }
            };
            break;
          }
          applyViewsLayoutParams(0);
        }
        label615: paramMotionEvent = null;
        k = 0;
      }
      label622: int k = 0;
    }
  }

  public class FrameLayoutAnimationListener extends FrameLayout
  {
    public FrameLayoutAnimationListener(Context arg2)
    {
      super();
    }

    public FrameLayoutAnimationListener(Context paramAttributeSet, AttributeSet arg3)
    {
      super(localAttributeSet);
    }

    public FrameLayoutAnimationListener(Context paramAttributeSet, AttributeSet paramInt, int arg4)
    {
      super(paramInt, i);
    }

    protected void onAnimationEnd()
    {
      super.onAnimationEnd();
      if (PopupNotificationActivity.this.onAnimationEndRunnable != null)
      {
        PopupNotificationActivity.this.onAnimationEndRunnable.run();
        PopupNotificationActivity.access$002(PopupNotificationActivity.this, null);
      }
    }
  }

  private class FrameLayoutTouch extends FrameLayout
  {
    public FrameLayoutTouch(Context arg2)
    {
      super();
    }

    public FrameLayoutTouch(Context paramAttributeSet, AttributeSet arg3)
    {
      super(localAttributeSet);
    }

    public FrameLayoutTouch(Context paramAttributeSet, AttributeSet paramInt, int arg4)
    {
      super(paramInt, i);
    }

    public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
    {
      return (PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent));
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      return (PopupNotificationActivity.this.checkTransitionAnimation()) || (((PopupNotificationActivity)getContext()).onTouchEventMy(paramMotionEvent));
    }

    public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
    {
      ((PopupNotificationActivity)getContext()).onTouchEventMy(null);
      super.requestDisallowInterceptTouchEvent(paramBoolean);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PopupNotificationActivity
 * JD-Core Version:    0.6.0
 */