package org.vidogram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipDescription;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.c.a.a.a;
import android.support.c.a.a.c.d;
import android.support.c.a.a.e;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import itman.Vidofilm.b;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.NotificationsController;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.query.DraftQuery;
import org.vidogram.messenger.query.MessagesQuery;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.InputUser;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.ReplyMarkup;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_inputMessageEntityMentionName;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetID;
import org.vidogram.tgnet.TLRPC.TL_keyboardButton;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonRequestPhone;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageEntityBold;
import org.vidogram.tgnet.TLRPC.TL_messageEntityCode;
import org.vidogram.tgnet.TLRPC.TL_messageEntityItalic;
import org.vidogram.tgnet.TLRPC.TL_messageEntityPre;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_peerUser;
import org.vidogram.tgnet.TLRPC.TL_replyKeyboardMarkup;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.DialogsActivity;
import org.vidogram.ui.DialogsActivity.DialogsActivityDelegate;
import org.vidogram.ui.StickersActivity;

public class ChatActivityEnterView extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate
{
  private boolean allowGifs;
  private boolean allowShowTopView;
  private boolean allowStickers;
  private ImageView attachButton;
  private LinearLayout attachLayout;
  private ImageView audioSendButton;
  private TLRPC.TL_document audioToSend;
  private MessageObject audioToSendMessageObject;
  private String audioToSendPath;
  private AnimatorSet audioVideoButtonAnimation;
  private FrameLayout audioVideoButtonContainer;
  private ImageView botButton;
  private MessageObject botButtonsMessageObject;
  private int botCount;
  private PopupWindow botKeyboardPopup;
  private BotKeyboardView botKeyboardView;
  private MessageObject botMessageObject;
  private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
  private Drawable cameraDrawable;
  private boolean canWriteToChannel;
  private ImageView cancelBotButton;
  private int currentPopupContentType = -1;
  private AnimatorSet currentTopViewAnimation;
  private ChatActivityEnterViewDelegate delegate;
  private long dialog_id;
  private float distCanMove = AndroidUtilities.dp(80.0F);
  private AnimatorSet doneButtonAnimation;
  private FrameLayout doneButtonContainer;
  public ImageView doneButtonImage;
  private ContextProgressView doneButtonProgress;
  private Paint dotPaint = new Paint(1);
  private boolean editingCaption;
  private MessageObject editingMessageObject;
  private int editingMessageReqId;
  private ImageView emojiButton;
  private int emojiPadding;
  private EmojiView emojiView;
  private boolean forceShowSendButton;
  private boolean hasBotCommands;
  private boolean hasRecordVideo = BuildVars.DEBUG_PRIVATE_VERSION;
  private boolean ignoreTextChange;
  private int innerTextChange;
  private boolean isPaused = true;
  private int keyboardHeight;
  private int keyboardHeightLand;
  private boolean keyboardVisible;
  private int lastSizeChangeValue1;
  private boolean lastSizeChangeValue2;
  private String lastTimeString;
  private long lastTypingTimeSend;
  private EditTextCaption messageEditText;
  private TLRPC.WebPage messageWebPage;
  private boolean messageWebPageSearch = true;
  private Drawable micDrawable;
  private boolean needShowTopView;
  private ImageView notifyButton;
  private Runnable openKeyboardRunnable = new Runnable()
  {
    public void run()
    {
      if ((ChatActivityEnterView.this.messageEditText != null) && (ChatActivityEnterView.this.waitingForKeyboardOpen) && (!ChatActivityEnterView.this.keyboardVisible) && (!AndroidUtilities.usingHardwareInput) && (!AndroidUtilities.isInMultiwindow))
      {
        ChatActivityEnterView.this.messageEditText.requestFocus();
        AndroidUtilities.showKeyboard(ChatActivityEnterView.this.messageEditText);
        AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable);
        AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.openKeyboardRunnable, 100L);
      }
    }
  };
  private Paint paint = new Paint(1);
  private Paint paintRecord = new Paint(1);
  private Activity parentActivity;
  private ChatActivity parentFragment;
  private Drawable pauseDrawable;
  private TLRPC.KeyboardButton pendingLocationButton;
  private MessageObject pendingMessageObject;
  private Drawable playDrawable;
  private CloseProgressDrawable2 progressDrawable;
  private Runnable recordAudioVideoRunnable = new Runnable()
  {
    public void run()
    {
      if ((ChatActivityEnterView.this.delegate == null) || (ChatActivityEnterView.this.parentActivity == null));
      while (true)
      {
        return;
        ChatActivityEnterView.access$702(ChatActivityEnterView.this, false);
        label95: label113: Object localObject;
        if ((ChatActivityEnterView.this.videoSendButton != null) && (ChatActivityEnterView.this.videoSendButton.getTag() != null))
        {
          if (Build.VERSION.SDK_INT >= 23)
          {
            int i;
            int j;
            int k;
            if (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") == 0)
            {
              i = 1;
              if (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.CAMERA") != 0)
                break label158;
              j = 1;
              if ((i != 0) && (j != 0))
                break label190;
              if ((i != 0) || (j != 0))
                break label163;
              k = 2;
              localObject = new String[k];
              if ((i != 0) || (j != 0))
                break label168;
              localObject[0] = "android.permission.RECORD_AUDIO";
              localObject[1] = "android.permission.CAMERA";
            }
            while (true)
            {
              ChatActivityEnterView.this.parentActivity.requestPermissions(localObject, 3);
              return;
              i = 0;
              break;
              label158: j = 0;
              break label95;
              label163: k = 1;
              break label113;
              label168: if (i == 0)
              {
                localObject[0] = "android.permission.RECORD_AUDIO";
                continue;
              }
              localObject[0] = "android.permission.CAMERA";
            }
          }
          label190: ChatActivityEnterView.this.delegate.needStartRecordVideo(0);
          return;
        }
        if (ChatActivityEnterView.this.parentFragment != null)
        {
          if ((Build.VERSION.SDK_INT >= 23) && (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0))
          {
            ChatActivityEnterView.this.parentActivity.requestPermissions(new String[] { "android.permission.RECORD_AUDIO" }, 3);
            return;
          }
          if ((int)ChatActivityEnterView.this.dialog_id >= 0)
            break label388;
          localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)ChatActivityEnterView.this.dialog_id));
          if ((localObject == null) || (((TLRPC.Chat)localObject).participants_count <= MessagesController.getInstance().groupBigSize))
            break label381;
          localObject = "bigchat_upload_audio";
        }
        while (MessagesController.isFeatureEnabled((String)localObject, ChatActivityEnterView.this.parentFragment))
        {
          ChatActivityEnterView.access$1102(ChatActivityEnterView.this, -1.0F);
          MediaController.getInstance().startRecording(ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
          ChatActivityEnterView.this.updateRecordIntefrace();
          ChatActivityEnterView.this.audioVideoButtonContainer.getParent().requestDisallowInterceptTouchEvent(true);
          return;
          label381: localObject = "chat_upload_audio";
          continue;
          label388: localObject = "pm_upload_audio";
        }
      }
    }
  };
  private boolean recordAudioVideoRunnableStarted;
  private ImageView recordCancelImage;
  private TextView recordCancelText;
  private RecordCircle recordCircle;
  private ImageView recordDeleteImageView;
  private RecordDot recordDot;
  private int recordInterfaceState;
  private FrameLayout recordPanel;
  private LinearLayout recordTimeContainer;
  private TextView recordTimeText;
  private View recordedAudioBackground;
  private FrameLayout recordedAudioPanel;
  private ImageView recordedAudioPlayButton;
  private SeekBarWaveformView recordedAudioSeekBar;
  private TextView recordedAudioTimeTextView;
  private boolean recordingAudioVideo;
  private Paint redDotPaint = new Paint(1);
  private MessageObject replyingMessageObject;
  private AnimatorSet runningAnimation;
  private AnimatorSet runningAnimation2;
  private AnimatorSet runningAnimationAudio;
  private int runningAnimationType;
  private ImageView sendButton;
  private FrameLayout sendButtonContainer;
  private boolean sendByEnter;
  private boolean showKeyboardOnResume;
  private boolean silent;
  private SizeNotifierFrameLayout sizeNotifierLayout;
  private LinearLayout slideText;
  private float startedDraggingX = -1.0F;
  private LinearLayout textFieldContainer;
  private View topView;
  private boolean topViewShowed;
  private ImageView videoSendButton;
  private boolean waitingForKeyboardOpen;
  private PowerManager.WakeLock wakeLock;

  public ChatActivityEnterView(Activity paramActivity, SizeNotifierFrameLayout paramSizeNotifierFrameLayout, ChatActivity paramChatActivity, boolean paramBoolean)
  {
    super(paramActivity);
    this.dotPaint.setColor(Theme.getColor("chat_emojiPanelNewTrending"));
    setFocusable(true);
    setFocusableInTouchMode(true);
    setWillNotDraw(false);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStartError);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordStopped);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidSent);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioRouteChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.parentActivity = paramActivity;
    this.parentFragment = paramChatActivity;
    this.sizeNotifierLayout = paramSizeNotifierFrameLayout;
    this.sizeNotifierLayout.setDelegate(this);
    this.sendByEnter = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("send_by_enter", false);
    this.textFieldContainer = new LinearLayout(paramActivity);
    this.textFieldContainer.setOrientation(0);
    addView(this.textFieldContainer, LayoutHelper.createFrame(-1, -2.0F, 51, 0.0F, 2.0F, 0.0F, 0.0F));
    paramSizeNotifierFrameLayout = new FrameLayout(paramActivity);
    this.textFieldContainer.addView(paramSizeNotifierFrameLayout, LayoutHelper.createLinear(0, -2, 1.0F));
    this.emojiButton = new ImageView(paramActivity)
    {
      protected void onDraw(Canvas paramCanvas)
      {
        super.onDraw(paramCanvas);
        if ((ChatActivityEnterView.this.attachLayout != null) && ((ChatActivityEnterView.this.emojiView == null) || (ChatActivityEnterView.this.emojiView.getVisibility() != 0)) && (!StickersQuery.getUnreadStickerSets().isEmpty()) && (ChatActivityEnterView.this.dotPaint != null))
        {
          int i = paramCanvas.getWidth() / 2;
          int j = AndroidUtilities.dp(9.0F);
          int k = paramCanvas.getHeight() / 2;
          int m = AndroidUtilities.dp(8.0F);
          paramCanvas.drawCircle(i + j, k - m, AndroidUtilities.dp(5.0F), ChatActivityEnterView.this.dotPaint);
        }
      }
    };
    this.emojiButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
    this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.emojiButton.setPadding(0, AndroidUtilities.dp(1.0F), 0, 0);
    setEmojiButtonImage();
    paramSizeNotifierFrameLayout.addView(this.emojiButton, LayoutHelper.createFrame(48, 48.0F, 83, 3.0F, 0.0F, 0.0F, 0.0F));
    this.emojiButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        boolean bool = true;
        if ((!ChatActivityEnterView.this.isPopupShowing()) || (ChatActivityEnterView.this.currentPopupContentType != 0))
        {
          ChatActivityEnterView.this.showPopup(1, 0);
          paramView = ChatActivityEnterView.this.emojiView;
          if ((ChatActivityEnterView.this.messageEditText.length() > 0) && (!ChatActivityEnterView.this.messageEditText.getText().toString().startsWith("@gif")));
          while (true)
          {
            paramView.onOpen(bool);
            return;
            bool = false;
          }
        }
        ChatActivityEnterView.this.openKeyboardInternal();
        ChatActivityEnterView.this.removeGifFromInputField();
      }
    });
    this.messageEditText = new EditTextCaption(paramActivity)
    {
      public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
      {
        InputConnection localInputConnection = super.onCreateInputConnection(paramEditorInfo);
        a.a(paramEditorInfo, new String[] { "image/gif", "image/*", "image/jpg", "image/png" });
        return android.support.c.a.a.c.a(localInputConnection, paramEditorInfo, new c.d()
        {
          public boolean onCommitContent(e parame, int paramInt, Bundle paramBundle)
          {
            if ((android.support.v4.e.c.b()) && ((android.support.c.a.a.c.a & paramInt) != 0));
            while (true)
            {
              try
              {
                parame.c();
                if (parame.b().hasMimeType("image/gif"))
                {
                  SendMessagesHelper.prepareSendingDocument(null, null, parame.a(), "image/gif", ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, parame);
                  if (ChatActivityEnterView.this.delegate == null)
                    continue;
                  ChatActivityEnterView.this.delegate.onMessageSend(null);
                  return true;
                }
              }
              catch (Exception parame)
              {
                return false;
              }
              SendMessagesHelper.prepareSendingPhoto(null, parame.a(), ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, null, null, parame);
            }
          }
        });
      }

      public boolean onTouchEvent(MotionEvent paramMotionEvent)
      {
        ChatActivityEnterView localChatActivityEnterView;
        int i;
        if ((ChatActivityEnterView.this.isPopupShowing()) && (paramMotionEvent.getAction() == 0))
        {
          localChatActivityEnterView = ChatActivityEnterView.this;
          if (!AndroidUtilities.usingHardwareInput)
            break label53;
          i = 0;
        }
        while (true)
        {
          localChatActivityEnterView.showPopup(i, 0);
          ChatActivityEnterView.this.openKeyboardInternal();
          try
          {
            boolean bool = super.onTouchEvent(paramMotionEvent);
            return bool;
            label53: i = 2;
          }
          catch (Exception paramMotionEvent)
          {
            FileLog.e(paramMotionEvent);
          }
        }
        return false;
      }
    };
    updateFieldHint();
    this.messageEditText.setImeOptions(268435456);
    this.messageEditText.setInputType(this.messageEditText.getInputType() | 0x4000 | 0x20000);
    this.messageEditText.setSingleLine(false);
    this.messageEditText.setMaxLines(4);
    this.messageEditText.setTextSize(1, 18.0F);
    this.messageEditText.setGravity(80);
    this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
    this.messageEditText.setBackgroundDrawable(null);
    this.messageEditText.setTextColor(Theme.getColor("chat_messagePanelText"));
    this.messageEditText.setHintColor(Theme.getColor("chat_messagePanelHint"));
    this.messageEditText.setHintTextColor(Theme.getColor("chat_messagePanelHint"));
    paramChatActivity = this.messageEditText;
    float f;
    if (paramBoolean)
    {
      f = 50.0F;
      paramSizeNotifierFrameLayout.addView(paramChatActivity, LayoutHelper.createFrame(-1, -2.0F, 80, 52.0F, 0.0F, f, 0.0F));
      this.messageEditText.setOnKeyListener(new View.OnKeyListener()
      {
        boolean ctrlPressed = false;

        public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
        {
          boolean bool = false;
          if ((paramInt == 4) && (!ChatActivityEnterView.this.keyboardVisible) && (ChatActivityEnterView.this.isPopupShowing()))
          {
            if (paramKeyEvent.getAction() == 1)
            {
              if ((ChatActivityEnterView.this.currentPopupContentType == 1) && (ChatActivityEnterView.this.botButtonsMessageObject != null))
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
              ChatActivityEnterView.this.showPopup(0, 0);
              ChatActivityEnterView.this.removeGifFromInputField();
            }
            return true;
          }
          if ((paramInt == 66) && ((this.ctrlPressed) || (ChatActivityEnterView.this.sendByEnter)) && (paramKeyEvent.getAction() == 0) && (ChatActivityEnterView.this.editingMessageObject == null))
          {
            ChatActivityEnterView.this.sendMessage();
            return true;
          }
          if ((paramInt == 113) || (paramInt == 114))
          {
            if (paramKeyEvent.getAction() == 0)
              bool = true;
            this.ctrlPressed = bool;
            return true;
          }
          return false;
        }
      });
      this.messageEditText.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        boolean ctrlPressed = false;

        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          boolean bool = false;
          if (paramInt == 4)
          {
            ChatActivityEnterView.this.sendMessage();
            return true;
          }
          if ((paramKeyEvent != null) && (paramInt == 0))
          {
            if (((this.ctrlPressed) || (ChatActivityEnterView.this.sendByEnter)) && (paramKeyEvent.getAction() == 0) && (ChatActivityEnterView.this.editingMessageObject == null))
            {
              ChatActivityEnterView.this.sendMessage();
              return true;
            }
            if ((paramInt == 113) || (paramInt == 114))
            {
              if (paramKeyEvent.getAction() == 0)
                bool = true;
              this.ctrlPressed = bool;
              return true;
            }
          }
          return false;
        }
      });
      this.messageEditText.addTextChangedListener(new TextWatcher()
      {
        boolean processChange = false;

        public void afterTextChanged(Editable paramEditable)
        {
          if (ChatActivityEnterView.this.innerTextChange != 0);
          do
          {
            return;
            if ((!ChatActivityEnterView.this.sendByEnter) || (paramEditable.length() <= 0) || (paramEditable.charAt(paramEditable.length() - 1) != '\n') || (ChatActivityEnterView.this.editingMessageObject != null))
              continue;
            ChatActivityEnterView.this.sendMessage();
          }
          while (!this.processChange);
          ImageSpan[] arrayOfImageSpan = (ImageSpan[])paramEditable.getSpans(0, paramEditable.length(), ImageSpan.class);
          int i = 0;
          while (i < arrayOfImageSpan.length)
          {
            paramEditable.removeSpan(arrayOfImageSpan[i]);
            i += 1;
          }
          Emoji.replaceEmoji(paramEditable, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
          this.processChange = false;
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
          if (ChatActivityEnterView.this.innerTextChange == 1)
            return;
          ChatActivityEnterView.this.checkSendButton(true);
          CharSequence localCharSequence = AndroidUtilities.getTrimmedString(paramCharSequence.toString());
          ChatActivityEnterView.ChatActivityEnterViewDelegate localChatActivityEnterViewDelegate;
          if ((ChatActivityEnterView.this.delegate != null) && (!ChatActivityEnterView.this.ignoreTextChange))
          {
            if ((paramInt3 > 2) || (paramCharSequence == null) || (paramCharSequence.length() == 0))
              ChatActivityEnterView.access$3402(ChatActivityEnterView.this, true);
            localChatActivityEnterViewDelegate = ChatActivityEnterView.this.delegate;
            if ((paramInt2 <= paramInt3 + 1) && (paramInt3 - paramInt2 <= 2))
              break label328;
          }
          label328: for (boolean bool = true; ; bool = false)
          {
            localChatActivityEnterViewDelegate.onTextChanged(paramCharSequence, bool);
            if ((ChatActivityEnterView.this.innerTextChange != 2) && (paramInt2 != paramInt3) && (paramInt3 - paramInt2 > 1))
              this.processChange = true;
            if ((ChatActivityEnterView.this.editingMessageObject != null) || (ChatActivityEnterView.this.canWriteToChannel) || (localCharSequence.length() == 0) || (ChatActivityEnterView.this.lastTypingTimeSend >= System.currentTimeMillis() - 5000L) || (ChatActivityEnterView.this.ignoreTextChange))
              break;
            paramInt1 = ConnectionsManager.getInstance().getCurrentTime();
            paramCharSequence = null;
            if ((int)ChatActivityEnterView.this.dialog_id > 0)
              paramCharSequence = MessagesController.getInstance().getUser(Integer.valueOf((int)ChatActivityEnterView.this.dialog_id));
            if ((paramCharSequence != null) && ((paramCharSequence.id == UserConfig.getClientUserId()) || ((paramCharSequence.status != null) && (paramCharSequence.status.expires < paramInt1) && (!MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(paramCharSequence.id))))))
              break;
            ChatActivityEnterView.access$3602(ChatActivityEnterView.this, System.currentTimeMillis());
            if (ChatActivityEnterView.this.delegate == null)
              break;
            ChatActivityEnterView.this.delegate.needSendTyping();
            return;
          }
        }
      });
      if (paramBoolean)
      {
        this.attachLayout = new LinearLayout(paramActivity);
        this.attachLayout.setOrientation(0);
        this.attachLayout.setEnabled(false);
        this.attachLayout.setPivotX(AndroidUtilities.dp(48.0F));
        paramSizeNotifierFrameLayout.addView(this.attachLayout, LayoutHelper.createFrame(-2, 48, 85));
        this.botButton = new ImageView(paramActivity);
        this.botButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.botButton.setImageResource(2130837645);
        this.botButton.setScaleType(ImageView.ScaleType.CENTER);
        this.botButton.setVisibility(8);
        this.attachLayout.addView(this.botButton, LayoutHelper.createLinear(48, 48));
        this.botButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (ChatActivityEnterView.this.botReplyMarkup != null)
              if ((!ChatActivityEnterView.this.isPopupShowing()) || (ChatActivityEnterView.this.currentPopupContentType != 1))
              {
                ChatActivityEnterView.this.showPopup(1, 1);
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().remove("hidekeyboard_" + ChatActivityEnterView.this.dialog_id).commit();
              }
            do
            {
              return;
              if ((ChatActivityEnterView.this.currentPopupContentType == 1) && (ChatActivityEnterView.this.botButtonsMessageObject != null))
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
              ChatActivityEnterView.this.openKeyboardInternal();
              return;
            }
            while (!ChatActivityEnterView.this.hasBotCommands);
            ChatActivityEnterView.this.setFieldText("/");
            ChatActivityEnterView.this.messageEditText.requestFocus();
            ChatActivityEnterView.this.openKeyboard();
          }
        });
        this.notifyButton = new ImageView(paramActivity);
        paramChatActivity = this.notifyButton;
        if (!this.silent)
          break label2807;
        i = 2130837980;
        label893: paramChatActivity.setImageResource(i);
        this.notifyButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.notifyButton.setScaleType(ImageView.ScaleType.CENTER);
        paramChatActivity = this.notifyButton;
        if (!this.canWriteToChannel)
          break label2815;
        i = 0;
        label947: paramChatActivity.setVisibility(i);
        this.attachLayout.addView(this.notifyButton, LayoutHelper.createLinear(48, 48));
        this.notifyButton.setOnClickListener(new View.OnClickListener()
        {
          private Toast visibleToast;

          public void onClick(View paramView)
          {
            paramView = ChatActivityEnterView.this;
            boolean bool;
            if (!ChatActivityEnterView.this.silent)
              bool = true;
            while (true)
            {
              ChatActivityEnterView.access$3902(paramView, bool);
              paramView = ChatActivityEnterView.this.notifyButton;
              int i;
              if (ChatActivityEnterView.this.silent)
              {
                i = 2130837980;
                paramView.setImageResource(i);
                ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putBoolean("silent_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.silent).commit();
                NotificationsController.updateServerNotificationsSettings(ChatActivityEnterView.this.dialog_id);
              }
              try
              {
                if (this.visibleToast != null)
                  this.visibleToast.cancel();
                if (ChatActivityEnterView.this.silent)
                {
                  this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOff", 2131165503), 0);
                  this.visibleToast.show();
                  ChatActivityEnterView.this.updateFieldHint();
                  return;
                  bool = false;
                  continue;
                  i = 2130837981;
                }
              }
              catch (Exception paramView)
              {
                while (true)
                {
                  FileLog.e(paramView);
                  continue;
                  this.visibleToast = Toast.makeText(ChatActivityEnterView.this.parentActivity, LocaleController.getString("ChannelNotifyMembersInfoOn", 2131165504), 0);
                }
              }
            }
          }
        });
        this.attachButton = new ImageView(paramActivity);
        this.attachButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.attachButton.setImageResource(2130837731);
        this.attachButton.setScaleType(ImageView.ScaleType.CENTER);
        this.attachLayout.addView(this.attachButton, LayoutHelper.createLinear(48, 48));
        this.attachButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            ChatActivityEnterView.this.delegate.didPressedAttachButton();
          }
        });
      }
      this.recordedAudioPanel = new FrameLayout(paramActivity);
      paramChatActivity = this.recordedAudioPanel;
      if (this.audioToSend != null)
        break label2822;
    }
    label2822: for (int i = 8; ; i = 0)
    {
      paramChatActivity.setVisibility(i);
      this.recordedAudioPanel.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
      this.recordedAudioPanel.setFocusable(true);
      this.recordedAudioPanel.setFocusableInTouchMode(true);
      this.recordedAudioPanel.setClickable(true);
      paramSizeNotifierFrameLayout.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
      this.recordDeleteImageView = new ImageView(paramActivity);
      this.recordDeleteImageView.setScaleType(ImageView.ScaleType.CENTER);
      this.recordDeleteImageView.setImageResource(2130837734);
      this.recordDeleteImageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoiceDelete"), PorterDuff.Mode.MULTIPLY));
      this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0F));
      this.recordDeleteImageView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = MediaController.getInstance().getPlayingMessageObject();
          if ((paramView != null) && (paramView == ChatActivityEnterView.this.audioToSendMessageObject))
            MediaController.getInstance().cleanupPlayer(true, true);
          if (ChatActivityEnterView.this.audioToSendPath != null)
            new File(ChatActivityEnterView.this.audioToSendPath).delete();
          ChatActivityEnterView.this.hideRecordedAudioPanel();
          ChatActivityEnterView.this.checkSendButton(true);
        }
      });
      this.recordedAudioBackground = new View(paramActivity);
      this.recordedAudioBackground.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(16.0F), Theme.getColor("chat_recordedVoiceBackground")));
      this.recordedAudioPanel.addView(this.recordedAudioBackground, LayoutHelper.createFrame(-1, 32.0F, 19, 48.0F, 0.0F, 0.0F, 0.0F));
      this.recordedAudioSeekBar = new SeekBarWaveformView(paramActivity);
      this.recordedAudioPanel.addView(this.recordedAudioSeekBar, LayoutHelper.createFrame(-1, 32.0F, 19, 92.0F, 0.0F, 52.0F, 0.0F));
      this.playDrawable = Theme.createSimpleSelectorDrawable(paramActivity, 2130838044, Theme.getColor("chat_recordedVoicePlayPause"), Theme.getColor("chat_recordedVoicePlayPausePressed"));
      this.pauseDrawable = Theme.createSimpleSelectorDrawable(paramActivity, 2130838043, Theme.getColor("chat_recordedVoicePlayPause"), Theme.getColor("chat_recordedVoicePlayPausePressed"));
      this.recordedAudioPlayButton = new ImageView(paramActivity);
      this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
      this.recordedAudioPlayButton.setScaleType(ImageView.ScaleType.CENTER);
      this.recordedAudioPanel.addView(this.recordedAudioPlayButton, LayoutHelper.createFrame(48, 48.0F, 83, 48.0F, 0.0F, 0.0F, 0.0F));
      this.recordedAudioPlayButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ChatActivityEnterView.this.audioToSend == null)
            return;
          if ((MediaController.getInstance().isPlayingAudio(ChatActivityEnterView.this.audioToSendMessageObject)) && (!MediaController.getInstance().isAudioPaused()))
          {
            MediaController.getInstance().pauseAudio(ChatActivityEnterView.this.audioToSendMessageObject);
            ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.playDrawable);
            return;
          }
          ChatActivityEnterView.this.recordedAudioPlayButton.setImageDrawable(ChatActivityEnterView.this.pauseDrawable);
          MediaController.getInstance().playAudio(ChatActivityEnterView.this.audioToSendMessageObject);
        }
      });
      this.recordedAudioTimeTextView = new TextView(paramActivity);
      this.recordedAudioTimeTextView.setTextColor(Theme.getColor("chat_messagePanelVoiceDuration"));
      this.recordedAudioTimeTextView.setTextSize(1, 13.0F);
      this.recordedAudioPanel.addView(this.recordedAudioTimeTextView, LayoutHelper.createFrame(-2, -2.0F, 21, 0.0F, 0.0F, 13.0F, 0.0F));
      this.recordPanel = new FrameLayout(paramActivity);
      this.recordPanel.setVisibility(8);
      this.recordPanel.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
      paramSizeNotifierFrameLayout.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48, 80));
      this.slideText = new LinearLayout(paramActivity);
      this.slideText.setOrientation(0);
      this.recordPanel.addView(this.slideText, LayoutHelper.createFrame(-2, -2.0F, 17, 30.0F, 0.0F, 0.0F, 0.0F));
      this.recordCancelImage = new ImageView(paramActivity);
      this.recordCancelImage.setImageResource(2130838067);
      this.recordCancelImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_recordVoiceCancel"), PorterDuff.Mode.MULTIPLY));
      this.slideText.addView(this.recordCancelImage, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 0, 0));
      this.recordCancelText = new TextView(paramActivity);
      this.recordCancelText.setText(LocaleController.getString("SlideToCancel", 2131166469));
      this.recordCancelText.setTextColor(Theme.getColor("chat_recordVoiceCancel"));
      this.recordCancelText.setTextSize(1, 12.0F);
      this.slideText.addView(this.recordCancelText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
      this.recordTimeContainer = new LinearLayout(paramActivity);
      this.recordTimeContainer.setOrientation(0);
      this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0F), 0, 0, 0);
      this.recordTimeContainer.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
      this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-2, -2, 16));
      this.recordDot = new RecordDot(paramActivity);
      this.recordTimeContainer.addView(this.recordDot, LayoutHelper.createLinear(11, 11, 16, 0, 1, 0, 0));
      this.recordTimeText = new TextView(paramActivity);
      this.recordTimeText.setText("00:00");
      this.recordTimeText.setTextColor(Theme.getColor("chat_recordTime"));
      this.recordTimeText.setTextSize(1, 16.0F);
      this.recordTimeContainer.addView(this.recordTimeText, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
      this.sendButtonContainer = new FrameLayout(paramActivity);
      this.textFieldContainer.addView(this.sendButtonContainer, LayoutHelper.createLinear(48, 48, 80));
      this.audioVideoButtonContainer = new FrameLayout(paramActivity);
      this.audioVideoButtonContainer.setBackgroundColor(Theme.getColor("chat_messagePanelBackground"));
      this.audioVideoButtonContainer.setSoundEffectsEnabled(false);
      this.sendButtonContainer.addView(this.audioVideoButtonContainer, LayoutHelper.createFrame(48, 48.0F));
      this.audioVideoButtonContainer.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          if (paramMotionEvent.getAction() == 0)
            if (ChatActivityEnterView.this.hasRecordVideo)
            {
              ChatActivityEnterView.access$702(ChatActivityEnterView.this, true);
              AndroidUtilities.runOnUIThread(ChatActivityEnterView.this.recordAudioVideoRunnable, 150L);
            }
          label284: label671: 
          while (true)
          {
            paramView.onTouchEvent(paramMotionEvent);
            return true;
            ChatActivityEnterView.this.recordAudioVideoRunnable.run();
            continue;
            Object localObject;
            if ((paramMotionEvent.getAction() == 1) || (paramMotionEvent.getAction() == 3))
            {
              if (ChatActivityEnterView.this.recordAudioVideoRunnableStarted)
              {
                AndroidUtilities.cancelRunOnUIThread(ChatActivityEnterView.this.recordAudioVideoRunnable);
                localObject = ChatActivityEnterView.this;
                if (ChatActivityEnterView.this.videoSendButton.getTag() == null);
                for (boolean bool = true; ; bool = false)
                {
                  ((ChatActivityEnterView)localObject).setRecordVideoButtonVisible(bool, true);
                  break;
                }
              }
              ChatActivityEnterView.access$1102(ChatActivityEnterView.this, -1.0F);
              if ((ChatActivityEnterView.this.hasRecordVideo) && (ChatActivityEnterView.this.videoSendButton.getTag() != null))
                ChatActivityEnterView.this.delegate.needStartRecordVideo(1);
              while (true)
              {
                ChatActivityEnterView.access$5202(ChatActivityEnterView.this, false);
                ChatActivityEnterView.this.updateRecordIntefrace();
                break;
                ChatActivityEnterView.this.SendRecordPermision();
              }
            }
            if ((paramMotionEvent.getAction() != 2) || (!ChatActivityEnterView.this.recordingAudioVideo))
              continue;
            float f1 = paramMotionEvent.getX();
            float f2;
            if (f1 < -ChatActivityEnterView.this.distCanMove)
            {
              if ((ChatActivityEnterView.this.hasRecordVideo) && (ChatActivityEnterView.this.videoSendButton.getTag() != null))
              {
                ChatActivityEnterView.this.delegate.needStartRecordVideo(2);
                ChatActivityEnterView.access$5202(ChatActivityEnterView.this, false);
                ChatActivityEnterView.this.updateRecordIntefrace();
              }
            }
            else
            {
              float f3 = f1 + ChatActivityEnterView.this.audioVideoButtonContainer.getX();
              localObject = (FrameLayout.LayoutParams)ChatActivityEnterView.this.slideText.getLayoutParams();
              if (ChatActivityEnterView.this.startedDraggingX != -1.0F)
              {
                f1 = f3 - ChatActivityEnterView.this.startedDraggingX;
                ChatActivityEnterView.this.recordCircle.setTranslationX(f1);
                ((FrameLayout.LayoutParams)localObject).leftMargin = (AndroidUtilities.dp(30.0F) + (int)f1);
                ChatActivityEnterView.this.slideText.setLayoutParams((ViewGroup.LayoutParams)localObject);
                f2 = f1 / ChatActivityEnterView.this.distCanMove + 1.0F;
                if (f2 <= 1.0F)
                  break label624;
                f1 = 1.0F;
                ChatActivityEnterView.this.slideText.setAlpha(f1);
              }
              if ((f3 <= ChatActivityEnterView.this.slideText.getX() + ChatActivityEnterView.this.slideText.getWidth() + AndroidUtilities.dp(30.0F)) && (ChatActivityEnterView.this.startedDraggingX == -1.0F))
              {
                ChatActivityEnterView.access$1102(ChatActivityEnterView.this, f3);
                ChatActivityEnterView.access$5302(ChatActivityEnterView.this, (ChatActivityEnterView.this.recordPanel.getMeasuredWidth() - ChatActivityEnterView.this.slideText.getMeasuredWidth() - AndroidUtilities.dp(48.0F)) / 2.0F);
                if (ChatActivityEnterView.this.distCanMove > 0.0F)
                  break label639;
                ChatActivityEnterView.access$5302(ChatActivityEnterView.this, AndroidUtilities.dp(80.0F));
              }
            }
            while (true)
            {
              if (((FrameLayout.LayoutParams)localObject).leftMargin <= AndroidUtilities.dp(30.0F))
                break label671;
              ((FrameLayout.LayoutParams)localObject).leftMargin = AndroidUtilities.dp(30.0F);
              ChatActivityEnterView.this.recordCircle.setTranslationX(0.0F);
              ChatActivityEnterView.this.slideText.setLayoutParams((ViewGroup.LayoutParams)localObject);
              ChatActivityEnterView.this.slideText.setAlpha(1.0F);
              ChatActivityEnterView.access$1102(ChatActivityEnterView.this, -1.0F);
              break;
              MediaController.getInstance().stopRecording(0);
              break label284;
              label624: f1 = f2;
              if (f2 >= 0.0F)
                break label411;
              f1 = 0.0F;
              break label411;
              label639: if (ChatActivityEnterView.this.distCanMove <= AndroidUtilities.dp(80.0F))
                continue;
              ChatActivityEnterView.access$5302(ChatActivityEnterView.this, AndroidUtilities.dp(80.0F));
            }
          }
        }
      });
      this.audioSendButton = new ImageView(paramActivity);
      this.audioSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      this.audioSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
      this.audioSendButton.setImageResource(2130837922);
      this.audioSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0F), 0);
      this.audioVideoButtonContainer.addView(this.audioSendButton, LayoutHelper.createFrame(48, 48.0F));
      if (this.hasRecordVideo)
      {
        this.videoSendButton = new ImageView(paramActivity);
        this.videoSendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.videoSendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelIcons"), PorterDuff.Mode.MULTIPLY));
        this.videoSendButton.setImageResource(2130837821);
        this.videoSendButton.setPadding(0, 0, AndroidUtilities.dp(4.0F), 0);
        this.audioVideoButtonContainer.addView(this.videoSendButton, LayoutHelper.createFrame(48, 48.0F));
      }
      this.recordCircle = new RecordCircle(paramActivity);
      this.recordCircle.setVisibility(8);
      this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(124, 124.0F, 85, 0.0F, 0.0F, -36.0F, -38.0F));
      this.cancelBotButton = new ImageView(paramActivity);
      this.cancelBotButton.setVisibility(4);
      this.cancelBotButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      paramSizeNotifierFrameLayout = this.cancelBotButton;
      paramChatActivity = new CloseProgressDrawable2();
      this.progressDrawable = paramChatActivity;
      paramSizeNotifierFrameLayout.setImageDrawable(paramChatActivity);
      this.progressDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelCancelInlineBot"), PorterDuff.Mode.MULTIPLY));
      this.cancelBotButton.setSoundEffectsEnabled(false);
      this.cancelBotButton.setScaleX(0.1F);
      this.cancelBotButton.setScaleY(0.1F);
      this.cancelBotButton.setAlpha(0.0F);
      this.sendButtonContainer.addView(this.cancelBotButton, LayoutHelper.createFrame(48, 48.0F));
      this.cancelBotButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = ChatActivityEnterView.this.messageEditText.getText().toString();
          int i = paramView.indexOf(' ');
          if ((i == -1) || (i == paramView.length() - 1))
          {
            ChatActivityEnterView.this.setFieldText("");
            return;
          }
          ChatActivityEnterView.this.setFieldText(paramView.substring(0, i + 1));
        }
      });
      this.sendButton = new ImageView(paramActivity);
      this.sendButton.setVisibility(4);
      this.sendButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
      this.sendButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelSend"), PorterDuff.Mode.MULTIPLY));
      this.sendButton.setImageResource(2130837835);
      this.sendButton.setSoundEffectsEnabled(false);
      this.sendButton.setScaleX(0.1F);
      this.sendButton.setScaleY(0.1F);
      this.sendButton.setAlpha(0.0F);
      this.sendButtonContainer.addView(this.sendButton, LayoutHelper.createFrame(48, 48.0F));
      this.sendButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          ChatActivityEnterView.this.sendMessage();
        }
      });
      this.doneButtonContainer = new FrameLayout(paramActivity);
      this.doneButtonContainer.setVisibility(8);
      this.textFieldContainer.addView(this.doneButtonContainer, LayoutHelper.createLinear(48, 48, 80));
      this.doneButtonContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          ChatActivityEnterView.this.doneEditingMessage();
        }
      });
      this.doneButtonImage = new ImageView(paramActivity);
      this.doneButtonImage.setScaleType(ImageView.ScaleType.CENTER);
      this.doneButtonImage.setImageResource(2130837707);
      this.doneButtonImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_editDoneIcon"), PorterDuff.Mode.MULTIPLY));
      this.doneButtonContainer.addView(this.doneButtonImage, LayoutHelper.createFrame(48, 48.0F));
      this.doneButtonProgress = new ContextProgressView(paramActivity, 0);
      this.doneButtonProgress.setVisibility(4);
      this.doneButtonContainer.addView(this.doneButtonProgress, LayoutHelper.createFrame(-1, -1.0F));
      paramActivity = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0);
      this.keyboardHeight = paramActivity.getInt("kbd_height", AndroidUtilities.dp(200.0F));
      this.keyboardHeightLand = paramActivity.getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
      setRecordVideoButtonVisible(false, false);
      checkSendButton(false);
      return;
      f = 2.0F;
      break;
      label2807: i = 2130837981;
      break label893;
      label2815: i = 8;
      break label947;
    }
  }

  private void SendGifPermision(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    Object localObject = getContext();
    if ((localObject == null) || (!b.a((Context)localObject).f()))
    {
      SendMessagesHelper.getInstance().sendSticker(paramDocument, paramLong, paramMessageObject);
      return;
    }
    localObject = new AlertDialog.Builder((Context)localObject);
    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
    ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSureSendGif", 2131166757, new Object[0]));
    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramDocument, paramLong, paramMessageObject)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        SendMessagesHelper.getInstance().sendSticker(this.val$document, this.val$peer, this.val$reply_to_msg);
      }
    });
    ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
      }
    });
    ((AlertDialog.Builder)localObject).create();
    ((AlertDialog.Builder)localObject).show();
  }

  private void SendRecordPermision()
  {
    Object localObject = getContext();
    if ((localObject == null) || (!b.a((Context)localObject).g()))
    {
      MediaController.getInstance().stopRecording(1);
      return;
    }
    localObject = new AlertDialog.Builder((Context)localObject);
    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
    ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSureSendVoice", 2131166814, new Object[0]));
    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        MediaController.getInstance().stopRecording(1);
      }
    });
    ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        MediaController.getInstance().stopRecording(0);
      }
    });
    ((AlertDialog.Builder)localObject).create();
    ((AlertDialog.Builder)localObject).show();
  }

  private void SendStickerPermision(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    Object localObject = getContext();
    if ((localObject == null) || (!b.a((Context)localObject).h()))
    {
      SendMessagesHelper.getInstance().sendSticker(paramDocument, paramLong, paramMessageObject);
      return;
    }
    localObject = new AlertDialog.Builder((Context)localObject);
    ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
    ((AlertDialog.Builder)localObject).setMessage(LocaleController.formatString("AreYouSureSendSticker", 2131166758, new Object[0]));
    ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramDocument, paramLong, paramMessageObject)
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
        SendMessagesHelper.getInstance().sendSticker(this.val$document, this.val$peer, this.val$reply_to_msg);
      }
    });
    ((AlertDialog.Builder)localObject).setNegativeButton(LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
    {
      public void onClick(DialogInterface paramDialogInterface, int paramInt)
      {
      }
    });
    ((AlertDialog.Builder)localObject).create();
    ((AlertDialog.Builder)localObject).show();
  }

  private void checkSendButton(boolean paramBoolean)
  {
    if (this.editingMessageObject != null);
    label75: label92: label493: label630: label633: label635: 
    do
    {
      while (true)
      {
        return;
        if (this.isPaused)
          paramBoolean = false;
        if ((AndroidUtilities.getTrimmedString(this.messageEditText.getText()).length() > 0) || (this.forceShowSendButton) || (this.audioToSend != null))
        {
          localObject = this.messageEditText.getCaption();
          int i;
          int j;
          ArrayList localArrayList;
          if ((localObject != null) && (this.sendButton.getVisibility() == 0))
          {
            i = 1;
            if ((localObject != null) || (this.cancelBotButton.getVisibility() != 0))
              break label630;
            j = 1;
            if ((this.audioVideoButtonContainer.getVisibility() != 0) && (i == 0) && (j == 0))
              break label633;
            if (!paramBoolean)
              break label800;
            if (((this.runningAnimationType == 1) && (this.messageEditText.getCaption() == null)) || ((this.runningAnimationType == 3) && (localObject != null)))
              continue;
            if (this.runningAnimation != null)
            {
              this.runningAnimation.cancel();
              this.runningAnimation = null;
            }
            if (this.runningAnimation2 != null)
            {
              this.runningAnimation2.cancel();
              this.runningAnimation2 = null;
            }
            if (this.attachLayout != null)
            {
              this.runningAnimation2 = new AnimatorSet();
              this.runningAnimation2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.attachLayout, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.attachLayout, "scaleX", new float[] { 0.0F }) });
              this.runningAnimation2.setDuration(100L);
              this.runningAnimation2.addListener(new AnimatorListenerAdapter()
              {
                public void onAnimationCancel(Animator paramAnimator)
                {
                  if ((ChatActivityEnterView.this.runningAnimation2 != null) && (ChatActivityEnterView.this.runningAnimation2.equals(paramAnimator)))
                    ChatActivityEnterView.access$6602(ChatActivityEnterView.this, null);
                }

                public void onAnimationEnd(Animator paramAnimator)
                {
                  if ((ChatActivityEnterView.this.runningAnimation2 != null) && (ChatActivityEnterView.this.runningAnimation2.equals(paramAnimator)))
                    ChatActivityEnterView.this.attachLayout.setVisibility(8);
                }
              });
              this.runningAnimation2.start();
              updateFieldRight(0);
              if ((this.delegate != null) && (getVisibility() == 0))
                this.delegate.onAttachButtonHidden();
            }
            this.runningAnimation = new AnimatorSet();
            localArrayList = new ArrayList();
            if (this.audioVideoButtonContainer.getVisibility() == 0)
            {
              localArrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "scaleX", new float[] { 0.1F }));
              localArrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "scaleY", new float[] { 0.1F }));
              localArrayList.add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[] { 0.0F }));
            }
            if (i == 0)
              break label635;
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 0.0F }));
            if (localObject == null)
              break label715;
            this.runningAnimationType = 3;
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 1.0F }));
            this.cancelBotButton.setVisibility(0);
          }
          while (true)
          {
            this.runningAnimation.playTogether(localArrayList);
            this.runningAnimation.setDuration(150L);
            this.runningAnimation.addListener(new AnimatorListenerAdapter((String)localObject)
            {
              public void onAnimationCancel(Animator paramAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnimator)))
                  ChatActivityEnterView.access$6702(ChatActivityEnterView.this, null);
              }

              public void onAnimationEnd(Animator paramAnimator)
              {
                if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnimator)))
                {
                  if (this.val$caption == null)
                    break label85;
                  ChatActivityEnterView.this.cancelBotButton.setVisibility(0);
                  ChatActivityEnterView.this.sendButton.setVisibility(8);
                }
                while (true)
                {
                  ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(8);
                  ChatActivityEnterView.access$6702(ChatActivityEnterView.this, null);
                  ChatActivityEnterView.access$7002(ChatActivityEnterView.this, 0);
                  return;
                  label85: ChatActivityEnterView.this.sendButton.setVisibility(0);
                  ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                }
              }
            });
            this.runningAnimation.start();
            return;
            i = 0;
            break label75;
            j = 0;
            break label92;
            break;
            if (j == 0)
              break label493;
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 0.1F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 0.0F }));
            break label493;
            this.runningAnimationType = 1;
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 1.0F }));
            localArrayList.add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 1.0F }));
            this.sendButton.setVisibility(0);
          }
          this.audioVideoButtonContainer.setScaleX(0.1F);
          this.audioVideoButtonContainer.setScaleY(0.1F);
          this.audioVideoButtonContainer.setAlpha(0.0F);
          if (localObject != null)
          {
            this.sendButton.setScaleX(0.1F);
            this.sendButton.setScaleY(0.1F);
            this.sendButton.setAlpha(0.0F);
            this.cancelBotButton.setScaleX(1.0F);
            this.cancelBotButton.setScaleY(1.0F);
            this.cancelBotButton.setAlpha(1.0F);
            this.cancelBotButton.setVisibility(0);
            this.sendButton.setVisibility(8);
          }
          while (true)
          {
            this.audioVideoButtonContainer.setVisibility(8);
            if (this.attachLayout == null)
              break;
            this.attachLayout.setVisibility(8);
            if ((this.delegate != null) && (getVisibility() == 0))
              this.delegate.onAttachButtonHidden();
            updateFieldRight(0);
            return;
            this.cancelBotButton.setScaleX(0.1F);
            this.cancelBotButton.setScaleY(0.1F);
            this.cancelBotButton.setAlpha(0.0F);
            this.sendButton.setScaleX(1.0F);
            this.sendButton.setScaleY(1.0F);
            this.sendButton.setAlpha(1.0F);
            this.sendButton.setVisibility(0);
            this.cancelBotButton.setVisibility(8);
          }
        }
        if ((this.sendButton.getVisibility() != 0) && (this.cancelBotButton.getVisibility() != 0))
          continue;
        if (!paramBoolean)
          break;
        if (this.runningAnimationType == 2)
          continue;
        if (this.runningAnimation != null)
        {
          this.runningAnimation.cancel();
          this.runningAnimation = null;
        }
        if (this.runningAnimation2 != null)
        {
          this.runningAnimation2.cancel();
          this.runningAnimation2 = null;
        }
        if (this.attachLayout != null)
        {
          this.attachLayout.setVisibility(0);
          this.runningAnimation2 = new AnimatorSet();
          this.runningAnimation2.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.attachLayout, "alpha", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.attachLayout, "scaleX", new float[] { 1.0F }) });
          this.runningAnimation2.setDuration(100L);
          this.runningAnimation2.start();
          updateFieldRight(1);
          if (getVisibility() == 0)
            this.delegate.onAttachButtonShow();
        }
        this.audioVideoButtonContainer.setVisibility(0);
        this.runningAnimation = new AnimatorSet();
        this.runningAnimationType = 2;
        Object localObject = new ArrayList();
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "scaleX", new float[] { 1.0F }));
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "scaleY", new float[] { 1.0F }));
        ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[] { 1.0F }));
        if (this.cancelBotButton.getVisibility() == 0)
        {
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleX", new float[] { 0.1F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.cancelBotButton, "scaleY", new float[] { 0.1F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.cancelBotButton, "alpha", new float[] { 0.0F }));
        }
        while (true)
        {
          this.runningAnimation.playTogether((Collection)localObject);
          this.runningAnimation.setDuration(150L);
          this.runningAnimation.addListener(new AnimatorListenerAdapter()
          {
            public void onAnimationCancel(Animator paramAnimator)
            {
              if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnimator)))
                ChatActivityEnterView.access$6702(ChatActivityEnterView.this, null);
            }

            public void onAnimationEnd(Animator paramAnimator)
            {
              if ((ChatActivityEnterView.this.runningAnimation != null) && (ChatActivityEnterView.this.runningAnimation.equals(paramAnimator)))
              {
                ChatActivityEnterView.this.sendButton.setVisibility(8);
                ChatActivityEnterView.this.cancelBotButton.setVisibility(8);
                ChatActivityEnterView.this.audioVideoButtonContainer.setVisibility(0);
                ChatActivityEnterView.access$6702(ChatActivityEnterView.this, null);
                ChatActivityEnterView.access$7002(ChatActivityEnterView.this, 0);
              }
            }
          });
          this.runningAnimation.start();
          return;
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.sendButton, "scaleX", new float[] { 0.1F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.sendButton, "scaleY", new float[] { 0.1F }));
          ((ArrayList)localObject).add(ObjectAnimator.ofFloat(this.sendButton, "alpha", new float[] { 0.0F }));
        }
      }
      this.sendButton.setScaleX(0.1F);
      this.sendButton.setScaleY(0.1F);
      this.sendButton.setAlpha(0.0F);
      this.cancelBotButton.setScaleX(0.1F);
      this.cancelBotButton.setScaleY(0.1F);
      this.cancelBotButton.setAlpha(0.0F);
      this.audioVideoButtonContainer.setScaleX(1.0F);
      this.audioVideoButtonContainer.setScaleY(1.0F);
      this.audioVideoButtonContainer.setAlpha(1.0F);
      this.cancelBotButton.setVisibility(8);
      this.sendButton.setVisibility(8);
      this.audioVideoButtonContainer.setVisibility(0);
    }
    while (this.attachLayout == null);
    label715: if (getVisibility() == 0)
      this.delegate.onAttachButtonShow();
    label800: this.attachLayout.setVisibility(0);
    updateFieldRight(1);
  }

  private void createEmojiView()
  {
    if (this.emojiView != null)
      return;
    this.emojiView = new EmojiView(this.allowStickers, this.allowGifs, this.parentActivity);
    this.emojiView.setVisibility(8);
    this.emojiView.setListener(new EmojiView.Listener()
    {
      public boolean onBackspace()
      {
        if (ChatActivityEnterView.this.messageEditText.length() == 0)
          return false;
        ChatActivityEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
        return true;
      }

      public void onClearEmojiRecent()
      {
        if ((ChatActivityEnterView.this.parentFragment == null) || (ChatActivityEnterView.this.parentActivity == null))
          return;
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChatActivityEnterView.this.parentActivity);
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
        localBuilder.setMessage(LocaleController.getString("ClearRecentEmoji", 2131165554));
        localBuilder.setPositiveButton(LocaleController.getString("ClearButton", 2131165549).toUpperCase(), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            ChatActivityEnterView.this.emojiView.clearRecentEmoji();
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        ChatActivityEnterView.this.parentFragment.showDialog(localBuilder.create());
      }

      public void onEmojiSelected(String paramString)
      {
        int j = ChatActivityEnterView.this.messageEditText.getSelectionEnd();
        int i = j;
        if (j < 0)
          i = 0;
        try
        {
          ChatActivityEnterView.access$3102(ChatActivityEnterView.this, 2);
          paramString = Emoji.replaceEmoji(paramString, ChatActivityEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
          ChatActivityEnterView.this.messageEditText.setText(ChatActivityEnterView.this.messageEditText.getText().insert(i, paramString));
          i += paramString.length();
          ChatActivityEnterView.this.messageEditText.setSelection(i, i);
          return;
        }
        catch (Exception paramString)
        {
          FileLog.e(paramString);
          return;
        }
        finally
        {
          ChatActivityEnterView.access$3102(ChatActivityEnterView.this, 0);
        }
        throw paramString;
      }

      public void onGifSelected(TLRPC.Document paramDocument)
      {
        ChatActivityEnterView.this.SendGifPermision(paramDocument, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject);
        StickersQuery.addRecentGif(paramDocument, (int)(System.currentTimeMillis() / 1000L));
        if ((int)ChatActivityEnterView.this.dialog_id == 0)
          MessagesController.getInstance().saveGif(paramDocument);
        if (ChatActivityEnterView.this.delegate != null)
          ChatActivityEnterView.this.delegate.onMessageSend(null);
      }

      public void onGifTab(boolean paramBoolean)
      {
        if (!AndroidUtilities.usingHardwareInput)
        {
          if (!paramBoolean)
            break label56;
          if (ChatActivityEnterView.this.messageEditText.length() == 0)
          {
            ChatActivityEnterView.this.messageEditText.setText("@gif ");
            ChatActivityEnterView.this.messageEditText.setSelection(ChatActivityEnterView.this.messageEditText.length());
          }
        }
        label56: 
        do
          return;
        while (!ChatActivityEnterView.this.messageEditText.getText().toString().equals("@gif "));
        ChatActivityEnterView.this.messageEditText.setText("");
      }

      public void onShowStickerSet(TLRPC.StickerSet paramStickerSet, TLRPC.InputStickerSet paramInputStickerSet)
      {
        if ((ChatActivityEnterView.this.parentFragment == null) || (ChatActivityEnterView.this.parentActivity == null))
          return;
        if (paramStickerSet != null)
        {
          paramInputStickerSet = new TLRPC.TL_inputStickerSetID();
          paramInputStickerSet.access_hash = paramStickerSet.access_hash;
          paramInputStickerSet.id = paramStickerSet.id;
        }
        for (paramStickerSet = paramInputStickerSet; ; paramStickerSet = paramInputStickerSet)
        {
          ChatActivityEnterView.this.parentFragment.showDialog(new StickersAlert(ChatActivityEnterView.this.parentActivity, ChatActivityEnterView.this.parentFragment, paramStickerSet, null, ChatActivityEnterView.this));
          return;
        }
      }

      public void onStickerSelected(TLRPC.Document paramDocument)
      {
        ChatActivityEnterView.this.onStickerSelected(paramDocument);
        StickersQuery.addRecentSticker(0, paramDocument, (int)(System.currentTimeMillis() / 1000L));
        if ((int)ChatActivityEnterView.this.dialog_id == 0)
          MessagesController.getInstance().saveGif(paramDocument);
      }

      public void onStickerSetAdd(TLRPC.StickerSetCovered paramStickerSetCovered)
      {
        StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, paramStickerSetCovered.set, 2, ChatActivityEnterView.this.parentFragment, false);
      }

      public void onStickerSetRemove(TLRPC.StickerSetCovered paramStickerSetCovered)
      {
        StickersQuery.removeStickersSet(ChatActivityEnterView.this.parentActivity, paramStickerSetCovered.set, 0, ChatActivityEnterView.this.parentFragment, false);
      }

      public void onStickersSettingsClick()
      {
        if (ChatActivityEnterView.this.parentFragment != null)
          ChatActivityEnterView.this.parentFragment.presentFragment(new StickersActivity(0));
      }

      public void onStickersTab(boolean paramBoolean)
      {
        ChatActivityEnterView.this.delegate.onStickersTab(paramBoolean);
      }
    });
    this.emojiView.setVisibility(8);
    this.sizeNotifierLayout.addView(this.emojiView);
  }

  private void hideRecordedAudioPanel()
  {
    this.audioToSendPath = null;
    this.audioToSend = null;
    this.audioToSendMessageObject = null;
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordedAudioPanel, "alpha", new float[] { 0.0F }) });
    localAnimatorSet.setDuration(200L);
    localAnimatorSet.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        ChatActivityEnterView.this.recordedAudioPanel.setVisibility(8);
      }
    });
    localAnimatorSet.start();
  }

  private void onWindowSizeChanged()
  {
    int j = this.sizeNotifierLayout.getHeight();
    int i = j;
    if (!this.keyboardVisible)
      i = j - this.emojiPadding;
    if (this.delegate != null)
      this.delegate.onWindowSizeChanged(i);
    if (this.topView != null)
    {
      if (i >= AndroidUtilities.dp(72.0F) + ActionBar.getCurrentActionBarHeight())
        break label114;
      if (this.allowShowTopView)
      {
        this.allowShowTopView = false;
        if (this.needShowTopView)
        {
          this.topView.setVisibility(8);
          resizeForTopView(false);
          this.topView.setTranslationY(this.topView.getLayoutParams().height);
        }
      }
    }
    label114: 
    do
    {
      do
        return;
      while (this.allowShowTopView);
      this.allowShowTopView = true;
    }
    while (!this.needShowTopView);
    this.topView.setVisibility(0);
    resizeForTopView(true);
    this.topView.setTranslationY(0.0F);
  }

  private void openKeyboardInternal()
  {
    int i;
    if ((AndroidUtilities.usingHardwareInput) || (this.isPaused))
    {
      i = 0;
      showPopup(i, 0);
      this.messageEditText.requestFocus();
      AndroidUtilities.showKeyboard(this.messageEditText);
      if (!this.isPaused)
        break label54;
      this.showKeyboardOnResume = true;
    }
    label54: 
    do
    {
      return;
      i = 2;
      break;
    }
    while ((AndroidUtilities.usingHardwareInput) || (this.keyboardVisible) || (AndroidUtilities.isInMultiwindow));
    this.waitingForKeyboardOpen = true;
    AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
    AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
  }

  private void removeGifFromInputField()
  {
    if ((!AndroidUtilities.usingHardwareInput) && (this.messageEditText.getText().toString().equals("@gif ")))
      this.messageEditText.setText("");
  }

  private void resizeForTopView(boolean paramBoolean)
  {
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.textFieldContainer.getLayoutParams();
    int j = AndroidUtilities.dp(2.0F);
    if (paramBoolean);
    for (int i = this.topView.getLayoutParams().height; ; i = 0)
    {
      localLayoutParams.topMargin = (i + j);
      this.textFieldContainer.setLayoutParams(localLayoutParams);
      return;
    }
  }

  private void sendMessage()
  {
    Object localObject;
    if (this.parentFragment != null)
      if ((int)this.dialog_id < 0)
      {
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
        if ((localObject != null) && (((TLRPC.Chat)localObject).participants_count > MessagesController.getInstance().groupBigSize))
        {
          localObject = "bigchat_message";
          if (MessagesController.isFeatureEnabled((String)localObject, this.parentFragment))
            break label78;
        }
      }
    label78: label213: 
    do
    {
      do
      {
        return;
        localObject = "chat_message";
        break;
        localObject = "pm_message";
        break;
        if (this.audioToSend != null)
        {
          localObject = MediaController.getInstance().getPlayingMessageObject();
          if ((localObject != null) && (localObject == this.audioToSendMessageObject))
            MediaController.getInstance().cleanupPlayer(true, true);
          SendMessagesHelper.getInstance().sendMessage(this.audioToSend, null, this.audioToSendPath, this.dialog_id, this.replyingMessageObject, null, null);
          if (this.delegate != null)
            this.delegate.onMessageSend(null);
          hideRecordedAudioPanel();
          checkSendButton(true);
          return;
        }
        localObject = this.messageEditText.getText();
        if (!processSendingText((CharSequence)localObject))
          break label213;
        this.messageEditText.setText("");
        this.lastTypingTimeSend = 0L;
      }
      while (this.delegate == null);
      this.delegate.onMessageSend((CharSequence)localObject);
      return;
    }
    while ((!this.forceShowSendButton) || (this.delegate == null));
    this.delegate.onMessageSend(null);
  }

  private void setEmojiButtonImage()
  {
    int i;
    if (this.emojiView == null)
    {
      i = getContext().getSharedPreferences("emoji", 0).getInt("selected_page", 0);
      if ((i != 0) && ((this.allowStickers) || (this.allowGifs)))
        break label68;
      this.emojiButton.setImageResource(2130837819);
    }
    label68: 
    do
    {
      return;
      i = this.emojiView.getCurrentPage();
      break;
      if (i != 1)
        continue;
      this.emojiButton.setImageResource(2130837820);
      return;
    }
    while (i != 2);
    this.emojiButton.setImageResource(2130837817);
  }

  private void setRecordVideoButtonVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    float f4 = 0.0F;
    float f3 = 0.0F;
    float f1 = 0.1F;
    if (!this.hasRecordVideo)
      return;
    Object localObject2 = this.videoSendButton;
    float f2;
    label94: Object localObject3;
    label125: Object localObject4;
    label156: Object localObject5;
    label189: Object localObject6;
    label217: ImageView localImageView;
    if (paramBoolean1)
    {
      localObject1 = Integer.valueOf(1);
      ((ImageView)localObject2).setTag(localObject1);
      if (this.audioVideoButtonAnimation != null)
      {
        this.audioVideoButtonAnimation.cancel();
        this.audioVideoButtonAnimation = null;
      }
      if (!paramBoolean2)
        break label392;
      this.audioVideoButtonAnimation = new AnimatorSet();
      localObject1 = this.audioVideoButtonAnimation;
      localObject2 = this.videoSendButton;
      if (!paramBoolean1)
        break label354;
      f2 = 1.0F;
      localObject2 = ObjectAnimator.ofFloat(localObject2, "scaleX", new float[] { f2 });
      localObject3 = this.videoSendButton;
      if (!paramBoolean1)
        break label362;
      f2 = 1.0F;
      localObject3 = ObjectAnimator.ofFloat(localObject3, "scaleY", new float[] { f2 });
      localObject4 = this.videoSendButton;
      if (!paramBoolean1)
        break label370;
      f2 = 1.0F;
      localObject4 = ObjectAnimator.ofFloat(localObject4, "alpha", new float[] { f2 });
      localObject5 = this.audioSendButton;
      if (!paramBoolean1)
        break label376;
      f2 = 0.1F;
      localObject5 = ObjectAnimator.ofFloat(localObject5, "scaleX", new float[] { f2 });
      localObject6 = this.audioSendButton;
      if (!paramBoolean1)
        break label382;
      localObject6 = ObjectAnimator.ofFloat(localObject6, "scaleY", new float[] { f1 });
      localImageView = this.audioSendButton;
      if (!paramBoolean1)
        break label387;
      f1 = f3;
    }
    while (true)
    {
      ((AnimatorSet)localObject1).playTogether(new Animator[] { localObject2, localObject3, localObject4, localObject5, localObject6, ObjectAnimator.ofFloat(localImageView, "alpha", new float[] { f1 }) });
      this.audioVideoButtonAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if (paramAnimator.equals(ChatActivityEnterView.this.audioVideoButtonAnimation))
            ChatActivityEnterView.access$5702(ChatActivityEnterView.this, null);
        }
      });
      this.audioVideoButtonAnimation.setInterpolator(new DecelerateInterpolator());
      this.audioVideoButtonAnimation.setDuration(150L);
      this.audioVideoButtonAnimation.start();
      return;
      localObject1 = null;
      break;
      label354: f2 = 0.1F;
      break label94;
      label362: f2 = 0.1F;
      break label125;
      label370: f2 = 0.0F;
      break label156;
      label376: f2 = 1.0F;
      break label189;
      label382: f1 = 1.0F;
      break label217;
      label387: f1 = 1.0F;
    }
    label392: Object localObject1 = this.videoSendButton;
    if (paramBoolean1)
    {
      f2 = 1.0F;
      ((ImageView)localObject1).setScaleX(f2);
      localObject1 = this.videoSendButton;
      if (!paramBoolean1)
        break label518;
      f2 = 1.0F;
      label425: ((ImageView)localObject1).setScaleY(f2);
      localObject1 = this.videoSendButton;
      if (!paramBoolean1)
        break label526;
      f2 = 1.0F;
      label445: ((ImageView)localObject1).setAlpha(f2);
      localObject1 = this.audioSendButton;
      if (!paramBoolean1)
        break label532;
      f2 = 0.1F;
      label467: ((ImageView)localObject1).setScaleX(f2);
      localObject1 = this.audioSendButton;
      if (!paramBoolean1)
        break label538;
      label484: ((ImageView)localObject1).setScaleY(f1);
      localObject1 = this.audioSendButton;
      if (!paramBoolean1)
        break label543;
      f1 = f4;
    }
    while (true)
    {
      ((ImageView)localObject1).setAlpha(f1);
      return;
      f2 = 0.1F;
      break;
      label518: f2 = 0.1F;
      break label425;
      label526: f2 = 0.0F;
      break label445;
      label532: f2 = 1.0F;
      break label467;
      label538: f1 = 1.0F;
      break label484;
      label543: f1 = 1.0F;
    }
  }

  private void showPopup(int paramInt1, int paramInt2)
  {
    Object localObject;
    if (paramInt1 == 1)
    {
      if ((paramInt2 == 0) && (this.emojiView == null))
      {
        if (this.parentActivity == null)
          return;
        createEmojiView();
      }
      if (paramInt2 == 0)
      {
        this.emojiView.setVisibility(0);
        if ((this.botKeyboardView != null) && (this.botKeyboardView.getVisibility() != 8))
          this.botKeyboardView.setVisibility(8);
        localObject = this.emojiView;
      }
    }
    while (true)
    {
      this.currentPopupContentType = paramInt2;
      if (this.keyboardHeight <= 0)
        this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0F));
      if (this.keyboardHeightLand <= 0)
        this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
      {
        paramInt1 = this.keyboardHeightLand;
        label168: if (paramInt2 != 1)
          break label425;
        paramInt1 = Math.min(this.botKeyboardView.getKeyboardHeight(), paramInt1);
      }
      label425: 
      while (true)
      {
        if (this.botKeyboardView != null)
          this.botKeyboardView.setPanelHeight(paramInt1);
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)((View)localObject).getLayoutParams();
        localLayoutParams.height = paramInt1;
        ((View)localObject).setLayoutParams(localLayoutParams);
        if (!AndroidUtilities.isInMultiwindow)
          AndroidUtilities.hideKeyboard(this.messageEditText);
        if (this.sizeNotifierLayout == null)
          break;
        this.emojiPadding = paramInt1;
        this.sizeNotifierLayout.requestLayout();
        if (paramInt2 == 0)
          this.emojiButton.setImageResource(2130837818);
        while (true)
        {
          updateBotButton();
          onWindowSizeChanged();
          return;
          if (paramInt2 != 1)
            break label428;
          if ((this.emojiView != null) && (this.emojiView.getVisibility() != 8))
            this.emojiView.setVisibility(8);
          this.botKeyboardView.setVisibility(0);
          localObject = this.botKeyboardView;
          break;
          paramInt1 = this.keyboardHeight;
          break label168;
          if (paramInt2 != 1)
            continue;
          setEmojiButtonImage();
        }
        if (this.emojiButton != null)
          setEmojiButtonImage();
        this.currentPopupContentType = -1;
        if (this.emojiView != null)
          this.emojiView.setVisibility(8);
        if (this.botKeyboardView != null)
          this.botKeyboardView.setVisibility(8);
        if (this.sizeNotifierLayout != null)
        {
          if (paramInt1 == 0)
            this.emojiPadding = 0;
          this.sizeNotifierLayout.requestLayout();
          onWindowSizeChanged();
        }
        updateBotButton();
        return;
      }
      label428: localObject = null;
    }
  }

  private void updateBotButton()
  {
    if (this.botButton == null)
      return;
    LinearLayout localLinearLayout;
    float f;
    if ((this.hasBotCommands) || (this.botReplyMarkup != null))
    {
      if (this.botButton.getVisibility() != 0)
        this.botButton.setVisibility(0);
      if (this.botReplyMarkup != null)
        if ((isPopupShowing()) && (this.currentPopupContentType == 1))
        {
          this.botButton.setImageResource(2130837818);
          updateFieldRight(2);
          localLinearLayout = this.attachLayout;
          if (((this.botButton != null) && (this.botButton.getVisibility() != 8)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() != 8)))
            break label172;
          f = 48.0F;
        }
    }
    while (true)
    {
      localLinearLayout.setPivotX(AndroidUtilities.dp(f));
      return;
      this.botButton.setImageResource(2130837645);
      break;
      this.botButton.setImageResource(2130837644);
      break;
      this.botButton.setVisibility(8);
      break;
      label172: f = 96.0F;
    }
  }

  private void updateFieldHint()
  {
    int j = 0;
    int i = j;
    Object localObject;
    if ((int)this.dialog_id < 0)
    {
      localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
      i = j;
      if (ChatObject.isChannel((TLRPC.Chat)localObject))
      {
        i = j;
        if (!((TLRPC.Chat)localObject).megagroup)
          i = 1;
      }
    }
    if (i != 0)
    {
      if (this.editingMessageObject != null)
      {
        EditTextCaption localEditTextCaption = this.messageEditText;
        if (this.editingCaption);
        for (localObject = LocaleController.getString("Caption", 2131165433); ; localObject = LocaleController.getString("TypeMessage", 2131166528))
        {
          localEditTextCaption.setHint((CharSequence)localObject);
          return;
        }
      }
      if (this.silent)
      {
        this.messageEditText.setHint(LocaleController.getString("ChannelSilentBroadcast", 2131165517));
        return;
      }
      this.messageEditText.setHint(LocaleController.getString("ChannelBroadcast", 2131165456));
      return;
    }
    this.messageEditText.setHint(LocaleController.getString("TypeMessage", 2131166528));
  }

  private void updateFieldRight(int paramInt)
  {
    if ((this.messageEditText == null) || (this.editingMessageObject != null))
      return;
    FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.messageEditText.getLayoutParams();
    if (paramInt == 1)
      if (((this.botButton != null) && (this.botButton.getVisibility() == 0)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() == 0)))
        localLayoutParams.rightMargin = AndroidUtilities.dp(98.0F);
    while (true)
    {
      this.messageEditText.setLayoutParams(localLayoutParams);
      return;
      localLayoutParams.rightMargin = AndroidUtilities.dp(50.0F);
      continue;
      if (paramInt == 2)
      {
        if (localLayoutParams.rightMargin == AndroidUtilities.dp(2.0F))
          continue;
        if (((this.botButton != null) && (this.botButton.getVisibility() == 0)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() == 0)))
        {
          localLayoutParams.rightMargin = AndroidUtilities.dp(98.0F);
          continue;
        }
        localLayoutParams.rightMargin = AndroidUtilities.dp(50.0F);
        continue;
      }
      localLayoutParams.rightMargin = AndroidUtilities.dp(2.0F);
    }
  }

  private void updateRecordIntefrace()
  {
    if (this.recordingAudioVideo)
      if (this.recordInterfaceState != 1);
    while (true)
    {
      return;
      this.recordInterfaceState = 1;
      try
      {
        if (this.wakeLock == null)
        {
          this.wakeLock = ((PowerManager)ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(536870918, "audio record lock");
          this.wakeLock.acquire();
        }
        AndroidUtilities.lockOrientation(this.parentActivity);
        this.recordPanel.setVisibility(0);
        this.recordCircle.setVisibility(0);
        this.recordCircle.setAmplitude(0.0D);
        this.recordTimeText.setText("00:00");
        this.recordDot.resetAlpha();
        this.lastTimeString = null;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.slideText.getLayoutParams();
        localLayoutParams.leftMargin = AndroidUtilities.dp(30.0F);
        this.slideText.setLayoutParams(localLayoutParams);
        this.slideText.setAlpha(1.0F);
        this.recordPanel.setX(AndroidUtilities.displaySize.x);
        this.recordCircle.setTranslationX(0.0F);
        if (this.runningAnimationAudio != null)
          this.runningAnimationAudio.cancel();
        this.runningAnimationAudio = new AnimatorSet();
        this.runningAnimationAudio.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[] { 0.0F }) });
        this.runningAnimationAudio.setDuration(300L);
        this.runningAnimationAudio.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimationAudio != null) && (ChatActivityEnterView.this.runningAnimationAudio.equals(paramAnimator)))
            {
              ChatActivityEnterView.this.recordPanel.setX(0.0F);
              ChatActivityEnterView.access$7102(ChatActivityEnterView.this, null);
            }
          }
        });
        this.runningAnimationAudio.setInterpolator(new DecelerateInterpolator());
        this.runningAnimationAudio.start();
        return;
      }
      catch (Exception localException1)
      {
        while (true)
          FileLog.e(localException1);
      }
      if (this.wakeLock != null);
      try
      {
        this.wakeLock.release();
        this.wakeLock = null;
        AndroidUtilities.unlockOrientation(this.parentActivity);
        if (this.recordInterfaceState == 0)
          continue;
        this.recordInterfaceState = 0;
        if (this.runningAnimationAudio != null)
          this.runningAnimationAudio.cancel();
        this.runningAnimationAudio = new AnimatorSet();
        this.runningAnimationAudio.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.recordPanel, "translationX", new float[] { AndroidUtilities.displaySize.x }), ObjectAnimator.ofFloat(this.recordCircle, "scale", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.audioVideoButtonContainer, "alpha", new float[] { 1.0F }) });
        this.runningAnimationAudio.setDuration(300L);
        this.runningAnimationAudio.addListener(new AnimatorListenerAdapter()
        {
          public void onAnimationEnd(Animator paramAnimator)
          {
            if ((ChatActivityEnterView.this.runningAnimationAudio != null) && (ChatActivityEnterView.this.runningAnimationAudio.equals(paramAnimator)))
            {
              paramAnimator = (FrameLayout.LayoutParams)ChatActivityEnterView.this.slideText.getLayoutParams();
              paramAnimator.leftMargin = AndroidUtilities.dp(30.0F);
              ChatActivityEnterView.this.slideText.setLayoutParams(paramAnimator);
              ChatActivityEnterView.this.slideText.setAlpha(1.0F);
              ChatActivityEnterView.this.recordPanel.setVisibility(8);
              ChatActivityEnterView.this.recordCircle.setVisibility(8);
              ChatActivityEnterView.access$7102(ChatActivityEnterView.this, null);
            }
          }
        });
        this.runningAnimationAudio.setInterpolator(new AccelerateInterpolator());
        this.runningAnimationAudio.start();
        return;
      }
      catch (Exception localException2)
      {
        while (true)
          FileLog.e(localException2);
      }
    }
  }

  public void addRecentGif(TLRPC.Document paramDocument)
  {
    StickersQuery.addRecentGif(paramDocument, (int)(System.currentTimeMillis() / 1000L));
    if (this.emojiView != null)
      this.emojiView.addRecentGif(paramDocument);
  }

  public void addStickerToRecent(TLRPC.Document paramDocument)
  {
    createEmojiView();
    this.emojiView.addRecentSticker(paramDocument);
  }

  public void addTopView(View paramView, int paramInt)
  {
    if (paramView == null)
      return;
    this.topView = paramView;
    this.topView.setVisibility(8);
    this.topView.setTranslationY(paramInt);
    addView(this.topView, 0, LayoutHelper.createFrame(-1, paramInt, 51, 0.0F, 2.0F, 0.0F, 0.0F));
    this.needShowTopView = false;
  }

  public void closeKeyboard()
  {
    AndroidUtilities.hideKeyboard(this.messageEditText);
  }

  public void didPressedBotButton(TLRPC.KeyboardButton paramKeyboardButton, MessageObject paramMessageObject1, MessageObject paramMessageObject2)
  {
    if ((paramKeyboardButton == null) || (paramMessageObject2 == null));
    while (true)
    {
      return;
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButton))
      {
        SendMessagesHelper.getInstance().sendMessage(paramKeyboardButton.text, this.dialog_id, paramMessageObject1, null, false, null, null, null);
        return;
      }
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonUrl))
      {
        this.parentFragment.showOpenUrlAlert(paramKeyboardButton.url, true);
        return;
      }
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonRequestPhone))
      {
        this.parentFragment.shareMyContact(paramMessageObject2);
        return;
      }
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonRequestGeoLocation))
      {
        paramMessageObject1 = new AlertDialog.Builder(this.parentActivity);
        paramMessageObject1.setTitle(LocaleController.getString("ShareYouLocationTitle", 2131166458));
        paramMessageObject1.setMessage(LocaleController.getString("ShareYouLocationInfo", 2131166456));
        paramMessageObject1.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener(paramMessageObject2, paramKeyboardButton)
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            if ((Build.VERSION.SDK_INT >= 23) && (ChatActivityEnterView.this.parentActivity.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0))
            {
              ChatActivityEnterView.this.parentActivity.requestPermissions(new String[] { "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION" }, 2);
              ChatActivityEnterView.access$7302(ChatActivityEnterView.this, this.val$messageObject);
              ChatActivityEnterView.access$7402(ChatActivityEnterView.this, this.val$button);
              return;
            }
            SendMessagesHelper.getInstance().sendCurrentLocation(this.val$messageObject, this.val$button);
          }
        });
        paramMessageObject1.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        this.parentFragment.showDialog(paramMessageObject1.create());
        return;
      }
      if (((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonCallback)) || ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame)) || ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy)))
      {
        SendMessagesHelper.getInstance().sendCallback(true, paramMessageObject2, paramKeyboardButton, this.parentFragment);
        return;
      }
      if ((!(paramKeyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline)) || (this.parentFragment.processSwitchButton((TLRPC.TL_keyboardButtonSwitchInline)paramKeyboardButton)))
        continue;
      if (!paramKeyboardButton.same_peer)
        break;
      int i = paramMessageObject2.messageOwner.from_id;
      if (paramMessageObject2.messageOwner.via_bot_id != 0)
        i = paramMessageObject2.messageOwner.via_bot_id;
      paramMessageObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
      if (paramMessageObject1 == null)
        continue;
      setFieldText("@" + paramMessageObject1.username + " " + paramKeyboardButton.query);
      return;
    }
    paramMessageObject1 = new Bundle();
    paramMessageObject1.putBoolean("onlySelect", true);
    paramMessageObject1.putInt("dialogsType", 1);
    paramMessageObject1 = new DialogsActivity(paramMessageObject1);
    paramMessageObject1.setDelegate(new DialogsActivity.DialogsActivityDelegate(paramMessageObject2, paramKeyboardButton)
    {
      public void didSelectDialog(DialogsActivity paramDialogsActivity, long paramLong, boolean paramBoolean)
      {
        int i = this.val$messageObject.messageOwner.from_id;
        if (this.val$messageObject.messageOwner.via_bot_id != 0)
          i = this.val$messageObject.messageOwner.via_bot_id;
        Object localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
        if (localObject == null)
          paramDialogsActivity.finishFragment();
        while (true)
        {
          return;
          DraftQuery.saveDraft(paramLong, "@" + ((TLRPC.User)localObject).username + " " + this.val$button.query, null, null, true);
          if (paramLong == ChatActivityEnterView.this.dialog_id)
            break label230;
          i = (int)paramLong;
          if (i == 0)
            break;
          localObject = new Bundle();
          if (i > 0)
            ((Bundle)localObject).putInt("user_id", i);
          while (true)
          {
            if (!MessagesController.checkCanOpenChat((Bundle)localObject, paramDialogsActivity))
              break label218;
            localObject = new ChatActivity((Bundle)localObject);
            if (!ChatActivityEnterView.this.parentFragment.presentFragment((BaseFragment)localObject, true))
              break label220;
            if (AndroidUtilities.isTablet())
              break;
            ChatActivityEnterView.this.parentFragment.removeSelfFromStack();
            return;
            if (i >= 0)
              continue;
            ((Bundle)localObject).putInt("chat_id", -i);
          }
          label218: continue;
          label220: paramDialogsActivity.finishFragment();
          return;
        }
        paramDialogsActivity.finishFragment();
        return;
        label230: paramDialogsActivity.finishFragment();
      }
    });
    this.parentFragment.presentFragment(paramMessageObject1);
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.emojiDidLoaded)
    {
      if (this.emojiView != null)
        this.emojiView.invalidateViews();
      if (this.botKeyboardView != null)
        this.botKeyboardView.invalidateViews();
    }
    while (true)
    {
      return;
      Object localObject;
      if (paramInt == NotificationCenter.recordProgressChanged)
      {
        long l = ((Long)paramArrayOfObject[0]).longValue();
        localObject = Long.valueOf(l / 1000L);
        paramInt = (int)(l % 1000L) / 10;
        String str = String.format("%02d:%02d.%02d", new Object[] { Long.valueOf(((Long)localObject).longValue() / 60L), Long.valueOf(((Long)localObject).longValue() % 60L), Integer.valueOf(paramInt) });
        if ((this.lastTimeString == null) || (!this.lastTimeString.equals(str)))
        {
          if (((Long)localObject).longValue() % 5L == 0L)
            MessagesController.getInstance().sendTyping(this.dialog_id, 1, 0);
          if (this.recordTimeText != null)
            this.recordTimeText.setText(str);
        }
        if (this.recordCircle == null)
          continue;
        this.recordCircle.setAmplitude(((Double)paramArrayOfObject[1]).doubleValue());
        return;
      }
      if (paramInt == NotificationCenter.closeChats)
      {
        if ((this.messageEditText == null) || (!this.messageEditText.isFocused()))
          continue;
        AndroidUtilities.hideKeyboard(this.messageEditText);
        return;
      }
      if ((paramInt == NotificationCenter.recordStartError) || (paramInt == NotificationCenter.recordStopped))
      {
        if (!this.recordingAudioVideo)
          continue;
        MessagesController.getInstance().sendTyping(this.dialog_id, 2, 0);
        this.recordingAudioVideo = false;
        updateRecordIntefrace();
        return;
      }
      if (paramInt == NotificationCenter.recordStarted)
      {
        if (this.recordingAudioVideo)
          continue;
        this.recordingAudioVideo = true;
        updateRecordIntefrace();
        return;
      }
      if (paramInt != NotificationCenter.audioDidSent)
        break label698;
      this.audioToSend = ((TLRPC.TL_document)paramArrayOfObject[0]);
      this.audioToSendPath = ((String)paramArrayOfObject[1]);
      if (this.audioToSend != null)
      {
        if (this.recordedAudioPanel == null)
          continue;
        paramArrayOfObject = new TLRPC.TL_message();
        paramArrayOfObject.out = true;
        paramArrayOfObject.id = 0;
        paramArrayOfObject.to_id = new TLRPC.TL_peerUser();
        localObject = paramArrayOfObject.to_id;
        paramInt = UserConfig.getClientUserId();
        paramArrayOfObject.from_id = paramInt;
        ((TLRPC.Peer)localObject).user_id = paramInt;
        paramArrayOfObject.date = (int)(System.currentTimeMillis() / 1000L);
        paramArrayOfObject.message = "-1";
        paramArrayOfObject.attachPath = this.audioToSendPath;
        paramArrayOfObject.media = new TLRPC.TL_messageMediaDocument();
        paramArrayOfObject.media.document = this.audioToSend;
        paramArrayOfObject.flags |= 768;
        this.audioToSendMessageObject = new MessageObject(paramArrayOfObject, null, false);
        this.recordedAudioPanel.setAlpha(1.0F);
        this.recordedAudioPanel.setVisibility(0);
        paramInt = 0;
        if (paramInt >= this.audioToSend.attributes.size())
          break label905;
        paramArrayOfObject = (TLRPC.DocumentAttribute)this.audioToSend.attributes.get(paramInt);
        if (!(paramArrayOfObject instanceof TLRPC.TL_documentAttributeAudio))
          break;
      }
    }
    label905: for (paramInt = paramArrayOfObject.duration; ; paramInt = 0)
    {
      i = 0;
      while (true)
      {
        if (i < this.audioToSend.attributes.size())
        {
          paramArrayOfObject = (TLRPC.DocumentAttribute)this.audioToSend.attributes.get(i);
          if ((paramArrayOfObject instanceof TLRPC.TL_documentAttributeAudio))
          {
            if ((paramArrayOfObject.waveform == null) || (paramArrayOfObject.waveform.length == 0))
              paramArrayOfObject.waveform = MediaController.getInstance().getWaveform(this.audioToSendPath);
            this.recordedAudioSeekBar.setWaveform(paramArrayOfObject.waveform);
          }
        }
        else
        {
          this.recordedAudioTimeTextView.setText(String.format("%d:%02d", new Object[] { Integer.valueOf(paramInt / 60), Integer.valueOf(paramInt % 60) }));
          closeKeyboard();
          hidePopup(false);
          checkSendButton(false);
          return;
          paramInt += 1;
          break;
        }
        i += 1;
      }
      if (this.delegate == null)
        break;
      this.delegate.onMessageSend(null);
      return;
      label698: if (paramInt == NotificationCenter.audioRouteChanged)
      {
        if (this.parentActivity == null)
          break;
        boolean bool = ((Boolean)paramArrayOfObject[0]).booleanValue();
        paramArrayOfObject = this.parentActivity;
        if (bool);
        for (paramInt = i; ; paramInt = -2147483648)
        {
          paramArrayOfObject.setVolumeControlStream(paramInt);
          return;
        }
      }
      if (paramInt == NotificationCenter.audioDidReset)
      {
        if ((this.audioToSendMessageObject == null) || (MediaController.getInstance().isPlayingAudio(this.audioToSendMessageObject)))
          break;
        this.recordedAudioPlayButton.setImageDrawable(this.playDrawable);
        this.recordedAudioSeekBar.setProgress(0.0F);
        return;
      }
      if (paramInt == NotificationCenter.audioProgressDidChanged)
      {
        paramArrayOfObject = (Integer)paramArrayOfObject[0];
        if ((this.audioToSendMessageObject == null) || (!MediaController.getInstance().isPlayingAudio(this.audioToSendMessageObject)))
          break;
        paramArrayOfObject = MediaController.getInstance().getPlayingMessageObject();
        this.audioToSendMessageObject.audioProgress = paramArrayOfObject.audioProgress;
        this.audioToSendMessageObject.audioProgressSec = paramArrayOfObject.audioProgressSec;
        if (this.recordedAudioSeekBar.isDragging())
          break;
        this.recordedAudioSeekBar.setProgress(this.audioToSendMessageObject.audioProgress);
        return;
      }
      if ((paramInt != NotificationCenter.featuredStickersDidLoaded) || (this.emojiButton == null))
        break;
      this.emojiButton.invalidate();
      return;
    }
  }

  public void doneEditingMessage()
  {
    if ((this.delegate != null) && (this.editingMessageObject != null))
    {
      this.delegate.onMessageEditEnd(true);
      showEditDoneProgress(true, true);
      CharSequence[] arrayOfCharSequence = new CharSequence[1];
      arrayOfCharSequence[0] = this.messageEditText.getText();
      ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
      this.editingMessageReqId = SendMessagesHelper.getInstance().editMessage(this.editingMessageObject, arrayOfCharSequence[0].toString(), this.messageWebPageSearch, this.parentFragment, localArrayList, new Runnable()
      {
        public void run()
        {
          ChatActivityEnterView.access$6502(ChatActivityEnterView.this, 0);
          ChatActivityEnterView.this.setEditingMessageObject(null, false);
        }
      });
    }
  }

  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    if (paramView == this.topView)
    {
      paramCanvas.save();
      paramCanvas.clipRect(0, 0, getMeasuredWidth(), paramView.getLayoutParams().height + AndroidUtilities.dp(2.0F));
    }
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    if (paramView == this.topView)
      paramCanvas.restore();
    return bool;
  }

  public ImageView getAttachButton()
  {
    return this.attachButton;
  }

  public ImageView getBotButton()
  {
    return this.botButton;
  }

  public int getCursorPosition()
  {
    if (this.messageEditText == null)
      return 0;
    return this.messageEditText.getSelectionStart();
  }

  public MessageObject getEditingMessageObject()
  {
    return this.editingMessageObject;
  }

  public ImageView getEmojiButton()
  {
    return this.emojiButton;
  }

  public int getEmojiPadding()
  {
    return this.emojiPadding;
  }

  public EmojiView getEmojiView()
  {
    return this.emojiView;
  }

  public CharSequence getFieldText()
  {
    if ((this.messageEditText != null) && (this.messageEditText.length() > 0))
      return this.messageEditText.getText();
    return null;
  }

  public int getSelectionLength()
  {
    if (this.messageEditText == null)
      return 0;
    try
    {
      int i = this.messageEditText.getSelectionEnd();
      int j = this.messageEditText.getSelectionStart();
      return i - j;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return 0;
  }

  public ImageView getSendButton()
  {
    return this.sendButton;
  }

  public boolean hasAudioToSend()
  {
    return this.audioToSendMessageObject != null;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  public boolean hasText()
  {
    return (this.messageEditText != null) && (this.messageEditText.length() > 0);
  }

  public void hidePopup(boolean paramBoolean)
  {
    if (isPopupShowing())
    {
      if ((this.currentPopupContentType == 1) && (paramBoolean) && (this.botButtonsMessageObject != null))
        ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("hidekeyboard_" + this.dialog_id, this.botButtonsMessageObject.getId()).commit();
      showPopup(0, 0);
      removeGifFromInputField();
    }
  }

  public void hideTopView(boolean paramBoolean)
  {
    if ((this.topView == null) || (!this.topViewShowed));
    do
    {
      return;
      this.topViewShowed = false;
      this.needShowTopView = false;
    }
    while (!this.allowShowTopView);
    if (this.currentTopViewAnimation != null)
    {
      this.currentTopViewAnimation.cancel();
      this.currentTopViewAnimation = null;
    }
    if (paramBoolean)
    {
      this.currentTopViewAnimation = new AnimatorSet();
      this.currentTopViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.topView, "translationY", new float[] { this.topView.getLayoutParams().height }) });
      this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnimator)))
            ChatActivityEnterView.access$5802(ChatActivityEnterView.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnimator)))
          {
            ChatActivityEnterView.this.topView.setVisibility(8);
            ChatActivityEnterView.this.resizeForTopView(false);
            ChatActivityEnterView.access$5802(ChatActivityEnterView.this, null);
          }
        }
      });
      this.currentTopViewAnimation.setDuration(200L);
      this.currentTopViewAnimation.start();
      return;
    }
    this.topView.setVisibility(8);
    resizeForTopView(false);
    this.topView.setTranslationY(this.topView.getLayoutParams().height);
  }

  public boolean isEditingCaption()
  {
    return this.editingCaption;
  }

  public boolean isEditingMessage()
  {
    return this.editingMessageObject != null;
  }

  public boolean isKeyboardVisible()
  {
    return this.keyboardVisible;
  }

  public boolean isMessageWebPageSearchEnabled()
  {
    return this.messageWebPageSearch;
  }

  public boolean isPopupShowing()
  {
    return ((this.emojiView != null) && (this.emojiView.getVisibility() == 0)) || ((this.botKeyboardView != null) && (this.botKeyboardView.getVisibility() == 0));
  }

  public boolean isPopupView(View paramView)
  {
    return (paramView == this.botKeyboardView) || (paramView == this.emojiView);
  }

  public boolean isRecordCircle(View paramView)
  {
    return paramView == this.recordCircle;
  }

  public boolean isTopViewVisible()
  {
    return (this.topView != null) && (this.topView.getVisibility() == 0);
  }

  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStarted);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStartError);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordStopped);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recordProgressChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidSent);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioRouteChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    if (this.emojiView != null)
      this.emojiView.onDestroy();
    if (this.wakeLock != null);
    try
    {
      this.wakeLock.release();
      this.wakeLock = null;
      if (this.sizeNotifierLayout != null)
        this.sizeNotifierLayout.setDelegate(null);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.topView != null) && (this.topView.getVisibility() == 0));
    for (int i = (int)this.topView.getTranslationY(); ; i = 0)
    {
      int j = Theme.chat_composeShadowDrawable.getIntrinsicHeight() + i;
      Theme.chat_composeShadowDrawable.setBounds(0, i, getMeasuredWidth(), j);
      Theme.chat_composeShadowDrawable.draw(paramCanvas);
      paramCanvas.drawRect(0.0F, j, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
      return;
    }
  }

  public void onEditTimeExpired()
  {
    this.doneButtonContainer.setVisibility(8);
  }

  public void onPause()
  {
    this.isPaused = true;
    closeKeyboard();
  }

  public void onRequestPermissionsResultFragment(int paramInt, String[] paramArrayOfString, int[] paramArrayOfInt)
  {
    if ((paramInt == 2) && (this.pendingLocationButton != null))
    {
      if ((paramArrayOfInt.length > 0) && (paramArrayOfInt[0] == 0))
        SendMessagesHelper.getInstance().sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
      this.pendingLocationButton = null;
      this.pendingMessageObject = null;
    }
  }

  public void onResume()
  {
    this.isPaused = false;
    if (this.showKeyboardOnResume)
    {
      this.showKeyboardOnResume = false;
      this.messageEditText.requestFocus();
      AndroidUtilities.showKeyboard(this.messageEditText);
      if ((!AndroidUtilities.usingHardwareInput) && (!this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow))
      {
        this.waitingForKeyboardOpen = true;
        AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
      }
    }
  }

  public void onSizeChanged(int paramInt, boolean paramBoolean)
  {
    int i;
    if ((paramInt > AndroidUtilities.dp(50.0F)) && (this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow))
    {
      if (paramBoolean)
      {
        this.keyboardHeightLand = paramInt;
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
      }
    }
    else if (isPopupShowing())
    {
      if (!paramBoolean)
        break label285;
      i = this.keyboardHeightLand;
      label81: if ((this.currentPopupContentType != 1) || (this.botKeyboardView.isFullSize()))
        break label444;
      i = Math.min(this.botKeyboardView.getKeyboardHeight(), i);
    }
    label285: label444: 
    while (true)
    {
      Object localObject;
      if (this.currentPopupContentType == 0)
        localObject = this.emojiView;
      while (true)
      {
        if (this.botKeyboardView != null)
          this.botKeyboardView.setPanelHeight(i);
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)((View)localObject).getLayoutParams();
        if ((localLayoutParams.width != AndroidUtilities.displaySize.x) || (localLayoutParams.height != i))
        {
          localLayoutParams.width = AndroidUtilities.displaySize.x;
          localLayoutParams.height = i;
          ((View)localObject).setLayoutParams(localLayoutParams);
          if (this.sizeNotifierLayout != null)
          {
            this.emojiPadding = localLayoutParams.height;
            this.sizeNotifierLayout.requestLayout();
            onWindowSizeChanged();
          }
        }
        if ((this.lastSizeChangeValue1 == paramInt) && (this.lastSizeChangeValue2 == paramBoolean))
        {
          onWindowSizeChanged();
          return;
          this.keyboardHeight = paramInt;
          ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
          break;
          i = this.keyboardHeight;
          break label81;
          if (this.currentPopupContentType == 1)
          {
            localObject = this.botKeyboardView;
            continue;
          }
        }
        else
        {
          this.lastSizeChangeValue1 = paramInt;
          this.lastSizeChangeValue2 = paramBoolean;
          boolean bool = this.keyboardVisible;
          if (paramInt > 0);
          for (paramBoolean = true; ; paramBoolean = false)
          {
            this.keyboardVisible = paramBoolean;
            if ((this.keyboardVisible) && (isPopupShowing()))
              showPopup(0, this.currentPopupContentType);
            if ((this.emojiPadding != 0) && (!this.keyboardVisible) && (this.keyboardVisible != bool) && (!isPopupShowing()))
            {
              this.emojiPadding = 0;
              this.sizeNotifierLayout.requestLayout();
            }
            if ((this.keyboardVisible) && (this.waitingForKeyboardOpen))
            {
              this.waitingForKeyboardOpen = false;
              AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            }
            onWindowSizeChanged();
            return;
          }
        }
        localObject = null;
      }
    }
  }

  public void onStickerSelected(TLRPC.Document paramDocument)
  {
    SendStickerPermision(paramDocument, this.dialog_id, this.replyingMessageObject);
    if (this.delegate != null)
      this.delegate.onMessageSend(null);
  }

  public void openKeyboard()
  {
    AndroidUtilities.showKeyboard(this.messageEditText);
  }

  public boolean processSendingText(CharSequence paramCharSequence)
  {
    paramCharSequence = AndroidUtilities.getTrimmedString(paramCharSequence);
    if (paramCharSequence.length() != 0)
    {
      int j = (int)Math.ceil(paramCharSequence.length() / 4096.0F);
      int i = 0;
      while (i < j)
      {
        CharSequence[] arrayOfCharSequence = new CharSequence[1];
        arrayOfCharSequence[0] = paramCharSequence.subSequence(i * 4096, Math.min((i + 1) * 4096, paramCharSequence.length()));
        ArrayList localArrayList = MessagesQuery.getEntities(arrayOfCharSequence);
        SendMessagesHelper.getInstance().sendMessage(arrayOfCharSequence[0].toString(), this.dialog_id, this.replyingMessageObject, this.messageWebPage, this.messageWebPageSearch, localArrayList, null, null);
        i += 1;
      }
      return true;
    }
    return false;
  }

  public void replaceWithText(int paramInt1, int paramInt2, CharSequence paramCharSequence)
  {
    try
    {
      SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(this.messageEditText.getText());
      localSpannableStringBuilder.replace(paramInt1, paramInt1 + paramInt2, paramCharSequence);
      this.messageEditText.setText(localSpannableStringBuilder);
      this.messageEditText.setSelection(paramCharSequence.length() + paramInt1);
      return;
    }
    catch (Exception paramCharSequence)
    {
      FileLog.e(paramCharSequence);
    }
  }

  public void setAllowStickersAndGifs(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (((this.allowStickers != paramBoolean1) || (this.allowGifs != paramBoolean2)) && (this.emojiView != null))
    {
      if (this.emojiView.getVisibility() == 0)
        hidePopup(false);
      this.sizeNotifierLayout.removeView(this.emojiView);
      this.emojiView = null;
    }
    this.allowStickers = paramBoolean1;
    this.allowGifs = paramBoolean2;
    setEmojiButtonImage();
  }

  public void setBotsCount(int paramInt, boolean paramBoolean)
  {
    this.botCount = paramInt;
    if (this.hasBotCommands != paramBoolean)
    {
      this.hasBotCommands = paramBoolean;
      updateBotButton();
    }
  }

  public void setButtons(MessageObject paramMessageObject)
  {
    setButtons(paramMessageObject, true);
  }

  public void setButtons(MessageObject paramMessageObject, boolean paramBoolean)
  {
    Object localObject2 = null;
    if ((this.replyingMessageObject != null) && (this.replyingMessageObject == this.botButtonsMessageObject) && (this.replyingMessageObject != paramMessageObject))
      this.botMessageObject = paramMessageObject;
    do
      return;
    while ((this.botButton == null) || ((this.botButtonsMessageObject != null) && (this.botButtonsMessageObject == paramMessageObject)) || ((this.botButtonsMessageObject == null) && (paramMessageObject == null)));
    if (this.botKeyboardView == null)
    {
      this.botKeyboardView = new BotKeyboardView(this.parentActivity);
      this.botKeyboardView.setVisibility(8);
      this.botKeyboardView.setDelegate(new BotKeyboardView.BotKeyboardViewDelegate()
      {
        public void didPressedButton(TLRPC.KeyboardButton paramKeyboardButton)
        {
          MessageObject localMessageObject1;
          MessageObject localMessageObject2;
          if (ChatActivityEnterView.this.replyingMessageObject != null)
          {
            localMessageObject1 = ChatActivityEnterView.this.replyingMessageObject;
            ChatActivityEnterView localChatActivityEnterView = ChatActivityEnterView.this;
            if (ChatActivityEnterView.this.replyingMessageObject == null)
              break label133;
            localMessageObject2 = ChatActivityEnterView.this.replyingMessageObject;
            label42: localChatActivityEnterView.didPressedBotButton(paramKeyboardButton, localMessageObject1, localMessageObject2);
            if (ChatActivityEnterView.this.replyingMessageObject == null)
              break label144;
            ChatActivityEnterView.this.openKeyboardInternal();
            ChatActivityEnterView.this.setButtons(ChatActivityEnterView.this.botMessageObject, false);
          }
          while (true)
          {
            if (ChatActivityEnterView.this.delegate != null)
              ChatActivityEnterView.this.delegate.onMessageSend(null);
            return;
            if ((int)ChatActivityEnterView.this.dialog_id < 0)
            {
              localMessageObject1 = ChatActivityEnterView.this.botButtonsMessageObject;
              break;
            }
            localMessageObject1 = null;
            break;
            label133: localMessageObject2 = ChatActivityEnterView.this.botButtonsMessageObject;
            break label42;
            label144: if (!ChatActivityEnterView.this.botButtonsMessageObject.messageOwner.reply_markup.single_use)
              continue;
            ChatActivityEnterView.this.openKeyboardInternal();
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("answered_" + ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.botButtonsMessageObject.getId()).commit();
          }
        }
      });
      this.sizeNotifierLayout.addView(this.botKeyboardView);
    }
    this.botButtonsMessageObject = paramMessageObject;
    Object localObject1;
    label159: int i;
    if ((paramMessageObject != null) && ((paramMessageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardMarkup)))
    {
      localObject1 = (TLRPC.TL_replyKeyboardMarkup)paramMessageObject.messageOwner.reply_markup;
      this.botReplyMarkup = ((TLRPC.TL_replyKeyboardMarkup)localObject1);
      localObject1 = this.botKeyboardView;
      if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y)
        break label383;
      i = this.keyboardHeightLand;
      label191: ((BotKeyboardView)localObject1).setPanelHeight(i);
      BotKeyboardView localBotKeyboardView = this.botKeyboardView;
      localObject1 = localObject2;
      if (this.botReplyMarkup != null)
        localObject1 = this.botReplyMarkup;
      localBotKeyboardView.setButtons((TLRPC.TL_replyKeyboardMarkup)localObject1);
      if (this.botReplyMarkup == null)
        break label396;
      localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
      if (((SharedPreferences)localObject1).getInt("hidekeyboard_" + this.dialog_id, 0) != paramMessageObject.getId())
        break label391;
      i = 1;
      label286: if ((this.botButtonsMessageObject != this.replyingMessageObject) && (this.botReplyMarkup.single_use) && (((SharedPreferences)localObject1).getInt("answered_" + this.dialog_id, 0) == paramMessageObject.getId()))
        break label394;
      if ((i == 0) && (this.messageEditText.length() == 0) && (!isPopupShowing()))
        showPopup(1, 1);
    }
    while (true)
    {
      updateBotButton();
      return;
      localObject1 = null;
      break label159;
      label383: i = this.keyboardHeight;
      break label191;
      label391: i = 0;
      break label286;
      label394: break;
      label396: if ((!isPopupShowing()) || (this.currentPopupContentType != 1))
        continue;
      if (paramBoolean)
      {
        openKeyboardInternal();
        continue;
      }
      showPopup(0, 1);
    }
  }

  public void setCaption(String paramString)
  {
    if (this.messageEditText != null)
    {
      this.messageEditText.setCaption(paramString);
      checkSendButton(true);
    }
  }

  public void setCommand(MessageObject paramMessageObject, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject2 = null;
    if ((paramString == null) || (getVisibility() != 0))
      return;
    if (paramBoolean1)
    {
      String str = this.messageEditText.getText().toString();
      Object localObject1 = localObject2;
      if (paramMessageObject != null)
      {
        localObject1 = localObject2;
        if ((int)this.dialog_id < 0)
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      }
      if (((this.botCount != 1) || (paramBoolean2)) && (localObject1 != null) && (((TLRPC.User)localObject1).bot) && (!paramString.contains("@")));
      for (paramMessageObject = String.format(Locale.US, "%s@%s", new Object[] { paramString, ((TLRPC.User)localObject1).username }) + " " + str.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", ""); ; paramMessageObject = paramString + " " + str.replaceFirst("^/[a-zA-Z@\\d_]{1,255}(\\s|$)", ""))
      {
        this.ignoreTextChange = true;
        this.messageEditText.setText(paramMessageObject);
        this.messageEditText.setSelection(this.messageEditText.getText().length());
        this.ignoreTextChange = false;
        if (this.delegate != null)
          this.delegate.onTextChanged(this.messageEditText.getText(), true);
        if ((this.keyboardVisible) || (this.currentPopupContentType != -1))
          break;
        openKeyboard();
        return;
      }
    }
    if ((paramMessageObject != null) && ((int)this.dialog_id < 0));
    for (paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id)); ((this.botCount != 1) || (paramBoolean2)) && (paramMessageObject != null) && (paramMessageObject.bot) && (!paramString.contains("@")); paramMessageObject = null)
    {
      SendMessagesHelper.getInstance().sendMessage(String.format(Locale.US, "%s@%s", new Object[] { paramString, paramMessageObject.username }), this.dialog_id, null, null, false, null, null, null);
      return;
    }
    SendMessagesHelper.getInstance().sendMessage(paramString, this.dialog_id, null, null, false, null, null, null);
  }

  public void setDelegate(ChatActivityEnterViewDelegate paramChatActivityEnterViewDelegate)
  {
    this.delegate = paramChatActivityEnterViewDelegate;
  }

  public void setDialogId(long paramLong)
  {
    int j = 1;
    this.dialog_id = paramLong;
    boolean bool;
    label140: label165: float f;
    if ((int)this.dialog_id < 0)
    {
      Object localObject = MessagesController.getInstance().getChat(Integer.valueOf(-(int)this.dialog_id));
      this.silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + this.dialog_id, false);
      if ((!ChatObject.isChannel((TLRPC.Chat)localObject)) || ((!((TLRPC.Chat)localObject).creator) && (!((TLRPC.Chat)localObject).editor)) || (((TLRPC.Chat)localObject).megagroup))
        break label262;
      bool = true;
      this.canWriteToChannel = bool;
      if (this.notifyButton != null)
      {
        localObject = this.notifyButton;
        if (!this.canWriteToChannel)
          break label268;
        i = 0;
        ((ImageView)localObject).setVisibility(i);
        localObject = this.notifyButton;
        if (!this.silent)
          break label275;
        i = 2130837980;
        ((ImageView)localObject).setImageResource(i);
        localObject = this.attachLayout;
        if (((this.botButton != null) && (this.botButton.getVisibility() != 8)) || ((this.notifyButton != null) && (this.notifyButton.getVisibility() != 8)))
          break label283;
        f = 48.0F;
        label220: ((LinearLayout)localObject).setPivotX(AndroidUtilities.dp(f));
      }
      if (this.attachLayout != null)
        if (this.attachLayout.getVisibility() != 0)
          break label290;
    }
    label262: label268: label275: label283: label290: for (int i = j; ; i = 0)
    {
      updateFieldRight(i);
      updateFieldHint();
      return;
      bool = false;
      break;
      i = 8;
      break label140;
      i = 2130837981;
      break label165;
      f = 96.0F;
      break label220;
    }
  }

  public void setEditingMessageObject(MessageObject paramMessageObject, boolean paramBoolean)
  {
    if ((this.audioToSend != null) || (this.editingMessageObject == paramMessageObject))
      return;
    if (this.editingMessageReqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.editingMessageReqId, true);
      this.editingMessageReqId = 0;
    }
    this.editingMessageObject = paramMessageObject;
    this.editingCaption = paramBoolean;
    label257: ArrayList localArrayList;
    SpannableStringBuilder localSpannableStringBuilder;
    Object localObject1;
    int i;
    int j;
    if (this.editingMessageObject != null)
    {
      if (this.doneButtonAnimation != null)
      {
        this.doneButtonAnimation.cancel();
        this.doneButtonAnimation = null;
      }
      this.doneButtonContainer.setVisibility(0);
      showEditDoneProgress(true, false);
      paramMessageObject = new InputFilter[1];
      if (paramBoolean)
      {
        paramMessageObject[0] = new InputFilter.LengthFilter(200);
        if (this.editingMessageObject.caption != null)
          setFieldText(Emoji.replaceEmoji(new SpannableStringBuilder(this.editingMessageObject.caption.toString()), this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false));
        while (true)
        {
          this.messageEditText.setFilters(paramMessageObject);
          openKeyboard();
          paramMessageObject = (FrameLayout.LayoutParams)this.messageEditText.getLayoutParams();
          paramMessageObject.rightMargin = AndroidUtilities.dp(4.0F);
          this.messageEditText.setLayoutParams(paramMessageObject);
          this.sendButton.setVisibility(8);
          this.cancelBotButton.setVisibility(8);
          this.audioVideoButtonContainer.setVisibility(8);
          if (this.attachButton != null)
            this.attachButton.setVisibility(8);
          this.sendButtonContainer.setVisibility(8);
          updateFieldHint();
          return;
          setFieldText("");
        }
      }
      paramMessageObject[0] = new InputFilter.LengthFilter(4096);
      if (this.editingMessageObject.messageText != null)
      {
        localArrayList = this.editingMessageObject.messageOwner.entities;
        MessagesQuery.sortEntities(localArrayList);
        localSpannableStringBuilder = new SpannableStringBuilder(this.editingMessageObject.messageText);
        localObject1 = localSpannableStringBuilder.getSpans(0, localSpannableStringBuilder.length(), Object.class);
        if ((localObject1 != null) && (localObject1.length > 0))
        {
          i = 0;
          while (i < localObject1.length)
          {
            localSpannableStringBuilder.removeSpan(localObject1[i]);
            i += 1;
          }
        }
        if (localArrayList != null)
        {
          j = 0;
          i = 0;
        }
      }
    }
    while (true)
    {
      try
      {
        if (j < localArrayList.size())
        {
          localObject1 = (TLRPC.MessageEntity)localArrayList.get(j);
          if (((TLRPC.MessageEntity)localObject1).offset + ((TLRPC.MessageEntity)localObject1).length + i > localSpannableStringBuilder.length())
            break label1061;
          if (!(localObject1 instanceof TLRPC.TL_inputMessageEntityMentionName))
            continue;
          if ((((TLRPC.MessageEntity)localObject1).offset + ((TLRPC.MessageEntity)localObject1).length + i >= localSpannableStringBuilder.length()) || (localSpannableStringBuilder.charAt(((TLRPC.MessageEntity)localObject1).offset + ((TLRPC.MessageEntity)localObject1).length + i) != ' '))
            continue;
          ((TLRPC.MessageEntity)localObject1).length += 1;
          Object localObject2 = new URLSpanUserMention("" + ((TLRPC.TL_inputMessageEntityMentionName)localObject1).user_id.user_id, true);
          int k = ((TLRPC.MessageEntity)localObject1).offset;
          int m = ((TLRPC.MessageEntity)localObject1).offset;
          localSpannableStringBuilder.setSpan(localObject2, k + i, ((TLRPC.MessageEntity)localObject1).length + m + i, 33);
          break label1061;
          if (!(localObject1 instanceof TLRPC.TL_messageEntityCode))
            continue;
          localSpannableStringBuilder.insert(((TLRPC.MessageEntity)localObject1).offset + ((TLRPC.MessageEntity)localObject1).length + i, "`");
          localSpannableStringBuilder.insert(((TLRPC.MessageEntity)localObject1).offset + i, "`");
          i += 2;
          break label1061;
          if (!(localObject1 instanceof TLRPC.TL_messageEntityPre))
            continue;
          localSpannableStringBuilder.insert(((TLRPC.MessageEntity)localObject1).offset + ((TLRPC.MessageEntity)localObject1).length + i, "```");
          localSpannableStringBuilder.insert(((TLRPC.MessageEntity)localObject1).offset + i, "```");
          i += 6;
          break label1061;
          if (!(localObject1 instanceof TLRPC.TL_messageEntityBold))
            continue;
          localObject2 = new TypefaceSpan(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
          k = ((TLRPC.MessageEntity)localObject1).offset;
          m = ((TLRPC.MessageEntity)localObject1).offset;
          localSpannableStringBuilder.setSpan(localObject2, k + i, ((TLRPC.MessageEntity)localObject1).length + m + i, 33);
          break label1061;
          if (!(localObject1 instanceof TLRPC.TL_messageEntityItalic))
            continue;
          localObject2 = new TypefaceSpan(AndroidUtilities.getTypeface("fonts/ritalic.ttf"));
          k = ((TLRPC.MessageEntity)localObject1).offset;
          m = ((TLRPC.MessageEntity)localObject1).offset;
          localSpannableStringBuilder.setSpan(localObject2, k + i, ((TLRPC.MessageEntity)localObject1).length + m + i, 33);
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
      }
      setFieldText(Emoji.replaceEmoji(localSpannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false));
      break;
      setFieldText("");
      break;
      this.doneButtonContainer.setVisibility(8);
      this.messageEditText.setFilters(new InputFilter[0]);
      this.delegate.onMessageEditEnd(false);
      this.audioVideoButtonContainer.setVisibility(0);
      this.attachLayout.setVisibility(0);
      this.sendButtonContainer.setVisibility(0);
      this.attachLayout.setScaleX(1.0F);
      this.attachLayout.setAlpha(1.0F);
      this.sendButton.setScaleX(0.1F);
      this.sendButton.setScaleY(0.1F);
      this.sendButton.setAlpha(0.0F);
      this.cancelBotButton.setScaleX(0.1F);
      this.cancelBotButton.setScaleY(0.1F);
      this.cancelBotButton.setAlpha(0.0F);
      this.audioVideoButtonContainer.setScaleX(1.0F);
      this.audioVideoButtonContainer.setScaleY(1.0F);
      this.audioVideoButtonContainer.setAlpha(1.0F);
      this.sendButton.setVisibility(8);
      this.cancelBotButton.setVisibility(8);
      this.messageEditText.setText("");
      if (getVisibility() == 0)
        this.delegate.onAttachButtonShow();
      updateFieldRight(1);
      break label257;
      label1061: j += 1;
    }
  }

  public void setFieldFocused()
  {
    if (this.messageEditText != null);
    try
    {
      this.messageEditText.requestFocus();
      return;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
  }

  public void setFieldFocused(boolean paramBoolean)
  {
    if (this.messageEditText == null);
    do
      while (true)
      {
        return;
        if (!paramBoolean)
          break;
        if (this.messageEditText.isFocused())
          continue;
        this.messageEditText.postDelayed(new Runnable()
        {
          public void run()
          {
            if (ChatActivityEnterView.this.messageEditText != null);
            try
            {
              ChatActivityEnterView.this.messageEditText.requestFocus();
              return;
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
          }
        }
        , 600L);
        return;
      }
    while ((!this.messageEditText.isFocused()) || (this.keyboardVisible));
    this.messageEditText.clearFocus();
  }

  public void setFieldText(CharSequence paramCharSequence)
  {
    if (this.messageEditText == null);
    do
    {
      return;
      this.ignoreTextChange = true;
      this.messageEditText.setText(paramCharSequence);
      this.messageEditText.setSelection(this.messageEditText.getText().length());
      this.ignoreTextChange = false;
    }
    while (this.delegate == null);
    this.delegate.onTextChanged(this.messageEditText.getText(), true);
  }

  public void setForceShowSendButton(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.forceShowSendButton = paramBoolean1;
    checkSendButton(paramBoolean2);
  }

  public void setOpenGifsTabFirst()
  {
    createEmojiView();
    StickersQuery.loadRecents(0, true, true);
    this.emojiView.switchToGifRecent();
  }

  public void setReplyingMessageObject(MessageObject paramMessageObject)
  {
    if (paramMessageObject != null)
    {
      if ((this.botMessageObject == null) && (this.botButtonsMessageObject != this.replyingMessageObject))
        this.botMessageObject = this.botButtonsMessageObject;
      this.replyingMessageObject = paramMessageObject;
      setButtons(this.replyingMessageObject, true);
      return;
    }
    if ((paramMessageObject == null) && (this.replyingMessageObject == this.botButtonsMessageObject))
    {
      this.replyingMessageObject = null;
      setButtons(this.botMessageObject, false);
      this.botMessageObject = null;
      return;
    }
    this.replyingMessageObject = paramMessageObject;
  }

  public void setSelection(int paramInt)
  {
    if (this.messageEditText == null)
      return;
    this.messageEditText.setSelection(paramInt, this.messageEditText.length());
  }

  public void setWebPage(TLRPC.WebPage paramWebPage, boolean paramBoolean)
  {
    this.messageWebPage = paramWebPage;
    this.messageWebPageSearch = paramBoolean;
  }

  public void showContextProgress(boolean paramBoolean)
  {
    if (this.progressDrawable == null)
      return;
    if (paramBoolean)
    {
      this.progressDrawable.startAnimation();
      return;
    }
    this.progressDrawable.stopAnimation();
  }

  public void showEditDoneProgress(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.doneButtonAnimation != null)
      this.doneButtonAnimation.cancel();
    if (!paramBoolean2)
    {
      if (paramBoolean1)
      {
        this.doneButtonImage.setScaleX(0.1F);
        this.doneButtonImage.setScaleY(0.1F);
        this.doneButtonImage.setAlpha(0.0F);
        this.doneButtonProgress.setScaleX(1.0F);
        this.doneButtonProgress.setScaleY(1.0F);
        this.doneButtonProgress.setAlpha(1.0F);
        this.doneButtonImage.setVisibility(4);
        this.doneButtonProgress.setVisibility(0);
        this.doneButtonContainer.setEnabled(false);
        return;
      }
      this.doneButtonProgress.setScaleX(0.1F);
      this.doneButtonProgress.setScaleY(0.1F);
      this.doneButtonProgress.setAlpha(0.0F);
      this.doneButtonImage.setScaleX(1.0F);
      this.doneButtonImage.setScaleY(1.0F);
      this.doneButtonImage.setAlpha(1.0F);
      this.doneButtonImage.setVisibility(0);
      this.doneButtonProgress.setVisibility(4);
      this.doneButtonContainer.setEnabled(true);
      return;
    }
    this.doneButtonAnimation = new AnimatorSet();
    if (paramBoolean1)
    {
      this.doneButtonProgress.setVisibility(0);
      this.doneButtonContainer.setEnabled(false);
      this.doneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButtonImage, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneButtonImage, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneButtonImage, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButtonProgress, "alpha", new float[] { 1.0F }) });
    }
    while (true)
    {
      this.doneButtonAnimation.addListener(new AnimatorListenerAdapter(paramBoolean1)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((ChatActivityEnterView.this.doneButtonAnimation != null) && (ChatActivityEnterView.this.doneButtonAnimation.equals(paramAnimator)))
            ChatActivityEnterView.access$6102(ChatActivityEnterView.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((ChatActivityEnterView.this.doneButtonAnimation != null) && (ChatActivityEnterView.this.doneButtonAnimation.equals(paramAnimator)))
          {
            if (!this.val$show)
              ChatActivityEnterView.this.doneButtonProgress.setVisibility(4);
          }
          else
            return;
          ChatActivityEnterView.this.doneButtonImage.setVisibility(4);
        }
      });
      this.doneButtonAnimation.setDuration(150L);
      this.doneButtonAnimation.start();
      return;
      this.doneButtonImage.setVisibility(0);
      this.doneButtonContainer.setEnabled(true);
      this.doneButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneButtonProgress, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneButtonProgress, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneButtonImage, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButtonImage, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneButtonImage, "alpha", new float[] { 1.0F }) });
    }
  }

  public void showTopView(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.topView == null) || (this.topViewShowed) || (getVisibility() != 0));
    do
    {
      while (true)
      {
        return;
        this.needShowTopView = true;
        this.topViewShowed = true;
        if (!this.allowShowTopView)
          continue;
        this.topView.setVisibility(0);
        if (this.currentTopViewAnimation != null)
        {
          this.currentTopViewAnimation.cancel();
          this.currentTopViewAnimation = null;
        }
        resizeForTopView(true);
        if (!paramBoolean1)
          break;
        if ((this.keyboardVisible) || (isPopupShowing()))
        {
          this.currentTopViewAnimation = new AnimatorSet();
          this.currentTopViewAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.topView, "translationY", new float[] { 0.0F }) });
          this.currentTopViewAnimation.addListener(new AnimatorListenerAdapter(paramBoolean2)
          {
            public void onAnimationCancel(Animator paramAnimator)
            {
              if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnimator)))
                ChatActivityEnterView.access$5802(ChatActivityEnterView.this, null);
            }

            public void onAnimationEnd(Animator paramAnimator)
            {
              if ((ChatActivityEnterView.this.currentTopViewAnimation != null) && (ChatActivityEnterView.this.currentTopViewAnimation.equals(paramAnimator)))
              {
                if ((ChatActivityEnterView.this.recordedAudioPanel.getVisibility() != 0) && ((!ChatActivityEnterView.this.forceShowSendButton) || (this.val$openKeyboard)))
                  ChatActivityEnterView.this.openKeyboard();
                ChatActivityEnterView.access$5802(ChatActivityEnterView.this, null);
              }
            }
          });
          this.currentTopViewAnimation.setDuration(200L);
          this.currentTopViewAnimation.start();
          return;
        }
        this.topView.setTranslationY(0.0F);
        if ((this.recordedAudioPanel.getVisibility() == 0) || ((this.forceShowSendButton) && (!paramBoolean2)))
          continue;
        openKeyboard();
        return;
      }
      this.topView.setTranslationY(0.0F);
    }
    while ((this.recordedAudioPanel.getVisibility() == 0) || ((this.forceShowSendButton) && (!paramBoolean2)));
    openKeyboard();
  }

  public static abstract interface ChatActivityEnterViewDelegate
  {
    public abstract void didPressedAttachButton();

    public abstract void needSendTyping();

    public abstract void needStartRecordVideo(int paramInt);

    public abstract void onAttachButtonHidden();

    public abstract void onAttachButtonShow();

    public abstract void onMessageEditEnd(boolean paramBoolean);

    public abstract void onMessageSend(CharSequence paramCharSequence);

    public abstract void onStickersTab(boolean paramBoolean);

    public abstract void onTextChanged(CharSequence paramCharSequence, boolean paramBoolean);

    public abstract void onWindowSizeChanged(int paramInt);
  }

  private class RecordCircle extends View
  {
    private float amplitude;
    private float animateAmplitudeDiff;
    private float animateToAmplitude;
    private long lastUpdateTime;
    private float scale;

    public RecordCircle(Context arg2)
    {
      super();
      ChatActivityEnterView.this.paint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
      ChatActivityEnterView.this.paintRecord.setColor(Theme.getColor("chat_messagePanelVoiceShadow"));
      ChatActivityEnterView.access$1802(ChatActivityEnterView.this, getResources().getDrawable(2130837922).mutate());
      ChatActivityEnterView.this.micDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
      ChatActivityEnterView.access$1902(ChatActivityEnterView.this, getResources().getDrawable(2130837821).mutate());
      ChatActivityEnterView.this.cameraDrawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_messagePanelVoicePressed"), PorterDuff.Mode.MULTIPLY));
    }

    public float getScale()
    {
      return this.scale;
    }

    protected void onDraw(Canvas paramCanvas)
    {
      float f2 = 1.0F;
      int i = getMeasuredWidth() / 2;
      int j = getMeasuredHeight() / 2;
      float f1;
      if (this.scale <= 0.5F)
      {
        f2 = this.scale / 0.5F;
        f1 = f2;
        long l1 = System.currentTimeMillis();
        long l2 = this.lastUpdateTime;
        if (this.animateToAmplitude != this.amplitude)
        {
          float f3 = this.amplitude;
          float f4 = this.animateAmplitudeDiff;
          this.amplitude = ((float)(l1 - l2) * f4 + f3);
          if (this.animateAmplitudeDiff <= 0.0F)
            break label362;
          if (this.amplitude > this.animateToAmplitude)
            this.amplitude = this.animateToAmplitude;
          label118: invalidate();
        }
        this.lastUpdateTime = System.currentTimeMillis();
        if (this.amplitude != 0.0F)
          paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, (AndroidUtilities.dp(42.0F) + AndroidUtilities.dp(20.0F) * this.amplitude) * this.scale, ChatActivityEnterView.this.paintRecord);
        paramCanvas.drawCircle(getMeasuredWidth() / 2.0F, getMeasuredHeight() / 2.0F, f1 * AndroidUtilities.dp(42.0F), ChatActivityEnterView.this.paint);
        if ((ChatActivityEnterView.this.videoSendButton == null) || (ChatActivityEnterView.this.videoSendButton.getTag() == null))
          break label385;
      }
      label385: for (Drawable localDrawable = ChatActivityEnterView.this.cameraDrawable; ; localDrawable = ChatActivityEnterView.this.micDrawable)
      {
        localDrawable.setBounds(i - localDrawable.getIntrinsicWidth() / 2, j - localDrawable.getIntrinsicHeight() / 2, i + localDrawable.getIntrinsicWidth() / 2, j + localDrawable.getIntrinsicHeight() / 2);
        localDrawable.setAlpha((int)(f2 * 255.0F));
        localDrawable.draw(paramCanvas);
        return;
        if (this.scale <= 0.75F)
        {
          f1 = 1.0F - (this.scale - 0.5F) / 0.25F * 0.1F;
          break;
        }
        f1 = 0.9F + (this.scale - 0.75F) / 0.25F * 0.1F;
        break;
        label362: if (this.amplitude >= this.animateToAmplitude)
          break label118;
        this.amplitude = this.animateToAmplitude;
        break label118;
      }
    }

    public void setAmplitude(double paramDouble)
    {
      this.animateToAmplitude = ((float)Math.min(100.0D, paramDouble) / 100.0F);
      this.animateAmplitudeDiff = ((this.animateToAmplitude - this.amplitude) / 150.0F);
      this.lastUpdateTime = System.currentTimeMillis();
      invalidate();
    }

    public void setScale(float paramFloat)
    {
      this.scale = paramFloat;
      invalidate();
    }
  }

  private class RecordDot extends View
  {
    private float alpha;
    private boolean isIncr;
    private long lastUpdateTime;

    public RecordDot(Context arg2)
    {
      super();
      ChatActivityEnterView.this.redDotPaint.setColor(Theme.getColor("chat_recordedVoiceDot"));
    }

    protected void onDraw(Canvas paramCanvas)
    {
      ChatActivityEnterView.this.redDotPaint.setAlpha((int)(255.0F * this.alpha));
      long l = System.currentTimeMillis() - this.lastUpdateTime;
      if (!this.isIncr)
      {
        this.alpha -= (float)l / 400.0F;
        if (this.alpha <= 0.0F)
        {
          this.alpha = 0.0F;
          this.isIncr = true;
        }
      }
      while (true)
      {
        this.lastUpdateTime = System.currentTimeMillis();
        paramCanvas.drawCircle(AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(5.0F), ChatActivityEnterView.this.redDotPaint);
        invalidate();
        return;
        float f = this.alpha;
        this.alpha = ((float)l / 400.0F + f);
        if (this.alpha < 1.0F)
          continue;
        this.alpha = 1.0F;
        this.isIncr = false;
      }
    }

    public void resetAlpha()
    {
      this.alpha = 1.0F;
      this.lastUpdateTime = System.currentTimeMillis();
      this.isIncr = false;
      invalidate();
    }
  }

  private class SeekBarWaveformView extends View
  {
    private SeekBarWaveform seekBarWaveform;

    public SeekBarWaveformView(Context arg2)
    {
      super();
      this.seekBarWaveform = new SeekBarWaveform(localContext);
      this.seekBarWaveform.setDelegate(new SeekBar.SeekBarDelegate(ChatActivityEnterView.this)
      {
        public void onSeekBarDrag(float paramFloat)
        {
          if (ChatActivityEnterView.this.audioToSendMessageObject != null)
          {
            ChatActivityEnterView.this.audioToSendMessageObject.audioProgress = paramFloat;
            MediaController.getInstance().seekToProgress(ChatActivityEnterView.this.audioToSendMessageObject, paramFloat);
          }
        }
      });
    }

    public boolean isDragging()
    {
      return this.seekBarWaveform.isDragging();
    }

    protected void onDraw(Canvas paramCanvas)
    {
      super.onDraw(paramCanvas);
      this.seekBarWaveform.setColors(Theme.getColor("chat_recordedVoiceProgress"), Theme.getColor("chat_recordedVoiceProgressInner"), Theme.getColor("chat_recordedVoiceProgress"));
      this.seekBarWaveform.draw(paramCanvas);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      this.seekBarWaveform.setSize(paramInt3 - paramInt1, paramInt4 - paramInt2);
    }

    public boolean onTouchEvent(MotionEvent paramMotionEvent)
    {
      boolean bool = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX(), paramMotionEvent.getY());
      if (bool)
      {
        if (paramMotionEvent.getAction() == 0)
          ChatActivityEnterView.this.requestDisallowInterceptTouchEvent(true);
        invalidate();
      }
      return (bool) || (super.onTouchEvent(paramMotionEvent));
    }

    public void setProgress(float paramFloat)
    {
      this.seekBarWaveform.setProgress(paramFloat);
      invalidate();
    }

    public void setWaveform(byte[] paramArrayOfByte)
    {
      this.seekBarWaveform.setWaveform(paramArrayOfByte);
      invalidate();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ChatActivityEnterView
 * JD-Core Version:    0.6.0
 */