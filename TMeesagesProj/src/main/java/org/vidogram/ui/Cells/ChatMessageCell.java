package org.vidogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewParent;
import android.view.ViewStructure;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.ImageReceiver.ImageReceiverDelegate;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.FileDownloadProgressListener;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessageObject.TextLayoutBlock;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.SendMessagesHelper;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.GeoPoint;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonCallback;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonRequestGeoLocation;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonSwitchInline;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonUrl;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaContact;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGame;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonBusy;
import org.vidogram.tgnet.TLRPC.TL_phoneCallDiscardReasonMissed;
import org.vidogram.tgnet.TLRPC.TL_photoSize;
import org.vidogram.tgnet.TLRPC.TL_webPage;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.LinkPath;
import org.vidogram.ui.Components.RadialProgress;
import org.vidogram.ui.Components.SeekBar;
import org.vidogram.ui.Components.SeekBar.SeekBarDelegate;
import org.vidogram.ui.Components.SeekBarWaveform;
import org.vidogram.ui.Components.StaticLayoutEx;
import org.vidogram.ui.Components.URLSpanBotCommand;
import org.vidogram.ui.Components.URLSpanMono;
import org.vidogram.ui.Components.URLSpanNoUnderline;
import org.vidogram.ui.PhotoViewer;

public class ChatMessageCell extends BaseCell
  implements ImageReceiver.ImageReceiverDelegate, MediaController.FileDownloadProgressListener, SeekBar.SeekBarDelegate
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private int TAG;
  private boolean allowAssistant;
  private StaticLayout authorLayout;
  private int authorX;
  private int availableTimeWidth;
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage = new ImageReceiver();
  private boolean avatarPressed;
  private int backgroundDrawableLeft;
  private int backgroundWidth = 100;
  private ArrayList<BotButton> botButtons = new ArrayList();
  private HashMap<String, BotButton> botButtonsByData = new HashMap();
  private HashMap<String, BotButton> botButtonsByPosition = new HashMap();
  private String botButtonsLayout;
  private int buttonPressed;
  private int buttonState;
  private int buttonX;
  private int buttonY;
  private boolean cancelLoading;
  private int captionHeight;
  private StaticLayout captionLayout;
  private int captionX;
  private int captionY;
  private AvatarDrawable contactAvatarDrawable;
  private Drawable currentBackgroundDrawable;
  private TLRPC.Chat currentChat;
  private TLRPC.Chat currentForwardChannel;
  private String currentForwardNameString;
  private TLRPC.User currentForwardUser;
  private MessageObject currentMessageObject;
  private String currentNameString;
  private TLRPC.FileLocation currentPhoto;
  private String currentPhotoFilter;
  private String currentPhotoFilterThumb;
  private TLRPC.PhotoSize currentPhotoObject;
  private TLRPC.PhotoSize currentPhotoObjectThumb;
  private TLRPC.FileLocation currentReplyPhoto;
  private String currentTimeString;
  private String currentUrl;
  private TLRPC.User currentUser;
  private TLRPC.User currentViaBotUser;
  private String currentViewsString;
  private ChatMessageCellDelegate delegate;
  private RectF deleteProgressRect = new RectF();
  private StaticLayout descriptionLayout;
  private int descriptionX;
  private int descriptionY;
  private boolean disallowLongPress;
  private StaticLayout docTitleLayout;
  private int docTitleOffsetX;
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawBackground = true;
  private boolean drawForwardedName;
  private boolean drawImageButton;
  private boolean drawInstantView;
  private boolean drawName;
  private boolean drawNameLayout;
  private boolean drawPhotoImage;
  private boolean drawShareButton;
  private boolean drawTime = true;
  private StaticLayout durationLayout;
  private int durationWidth;
  private int firstVisibleBlockNum;
  private boolean forceNotDrawTime;
  private boolean forwardBotPressed;
  private boolean forwardName;
  private float[] forwardNameOffsetX = new float[2];
  private boolean forwardNamePressed;
  private int forwardNameX;
  private int forwardNameY;
  private StaticLayout[] forwardedNameLayout = new StaticLayout[2];
  private int forwardedNameWidth;
  private boolean fullyDraw;
  private boolean gamePreviewPressed;
  private boolean hasGamePreview;
  private boolean hasInvoicePreview;
  private boolean hasLinkPreview;
  private boolean imagePressed;
  private boolean inLayout;
  private StaticLayout infoLayout;
  private int infoWidth;
  private boolean instantPressed;
  private int instantTextX;
  private StaticLayout instantViewLayout;
  private int instantWidth;
  private boolean isAvatarVisible;
  public boolean isChat;
  private boolean isCheckPressed = true;
  private boolean isHighlighted;
  private boolean isPressed;
  private boolean isSmallImage;
  private int keyboardHeight;
  private int lastDeleteDate;
  private int lastSendState;
  private String lastTimeString;
  private int lastViewsCount;
  private int lastVisibleBlockNum;
  private int layoutHeight;
  private int layoutWidth;
  private int linkBlockNum;
  private int linkPreviewHeight;
  private boolean linkPreviewPressed;
  private int linkSelectionBlockNum;
  private boolean mediaBackground;
  private int mediaOffsetY;
  private StaticLayout nameLayout;
  private float nameOffsetX;
  private int nameWidth;
  private float nameX;
  private float nameY;
  private int namesOffset;
  private boolean needNewVisiblePart;
  private boolean needReplyImage;
  private boolean otherPressed;
  private int otherX;
  private int otherY;
  private StaticLayout performerLayout;
  private int performerX;
  private ImageReceiver photoImage;
  private boolean photoNotSet;
  private boolean pinnedBottom;
  private boolean pinnedTop;
  private int pressedBotButton;
  private CharacterStyle pressedLink;
  private int pressedLinkType;
  private RadialProgress radialProgress;
  private RectF rect = new RectF();
  private ImageReceiver replyImageReceiver;
  private StaticLayout replyNameLayout;
  private float replyNameOffset;
  private int replyNameWidth;
  private boolean replyPressed;
  private int replyStartX;
  private int replyStartY;
  private StaticLayout replyTextLayout;
  private float replyTextOffset;
  private int replyTextWidth;
  private Rect scrollRect = new Rect();
  private SeekBar seekBar;
  private SeekBarWaveform seekBarWaveform;
  private int seekBarX;
  private int seekBarY;
  private boolean sharePressed;
  private int shareStartX;
  private int shareStartY;
  private StaticLayout siteNameLayout;
  private StaticLayout songLayout;
  private int songX;
  private int substractBackgroundHeight;
  private int textX;
  private int textY;
  private int timeAudioX;
  private StaticLayout timeLayout;
  private int timeTextWidth;
  private int timeWidth;
  private int timeWidthAudio;
  private int timeX;
  private StaticLayout titleLayout;
  private int titleX;
  private int totalHeight;
  private int totalVisibleBlocksCount;
  private ArrayList<LinkPath> urlPath = new ArrayList();
  private ArrayList<LinkPath> urlPathCache = new ArrayList();
  private ArrayList<LinkPath> urlPathSelection = new ArrayList();
  private boolean useSeekBarWaweform;
  private int viaNameWidth;
  private int viaWidth;
  private StaticLayout videoInfoLayout;
  private StaticLayout viewsLayout;
  private int viewsTextWidth;
  private boolean wasLayout;
  private int widthForButtons;

  public ChatMessageCell(Context paramContext)
  {
    super(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarDrawable = new AvatarDrawable();
    this.replyImageReceiver = new ImageReceiver(this);
    this.TAG = MediaController.getInstance().generateObserverTag();
    this.contactAvatarDrawable = new AvatarDrawable();
    this.photoImage = new ImageReceiver(this);
    this.photoImage.setDelegate(this);
    this.radialProgress = new RadialProgress(this);
    this.seekBar = new SeekBar(paramContext);
    this.seekBar.setDelegate(this);
    this.seekBarWaveform = new SeekBarWaveform(paramContext);
    this.seekBarWaveform.setDelegate(this);
    this.seekBarWaveform.setParentView(this);
  }

  private void calcBackgroundWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((this.hasLinkPreview) || (this.hasGamePreview) || (this.hasInvoicePreview) || (paramInt1 - this.currentMessageObject.lastLineWidth < paramInt2) || (this.currentMessageObject.hasRtl))
    {
      this.totalHeight += AndroidUtilities.dp(14.0F);
      this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth) + AndroidUtilities.dp(31.0F));
      this.backgroundWidth = Math.max(this.backgroundWidth, this.timeWidth + AndroidUtilities.dp(31.0F));
      return;
    }
    paramInt1 = paramInt3 - this.currentMessageObject.lastLineWidth;
    if ((paramInt1 >= 0) && (paramInt1 <= paramInt2))
    {
      this.backgroundWidth = (paramInt3 + paramInt2 - paramInt1 + AndroidUtilities.dp(31.0F));
      return;
    }
    this.backgroundWidth = (Math.max(paramInt3, this.currentMessageObject.lastLineWidth + paramInt2) + AndroidUtilities.dp(31.0F));
  }

  private boolean checkAudioMotionEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = true;
    if ((this.documentAttachType != 3) && (this.documentAttachType != 5))
      return false;
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    boolean bool1;
    if (this.useSeekBarWaweform)
    {
      bool1 = this.seekBarWaveform.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX - AndroidUtilities.dp(13.0F), paramMotionEvent.getY() - this.seekBarY);
      if (!bool1)
        break label191;
      if ((this.useSeekBarWaweform) || (paramMotionEvent.getAction() != 0))
        break label158;
      getParent().requestDisallowInterceptTouchEvent(true);
    }
    while (true)
    {
      this.disallowLongPress = true;
      invalidate();
      return bool1;
      bool1 = this.seekBar.onTouch(paramMotionEvent.getAction(), paramMotionEvent.getX() - this.seekBarX, paramMotionEvent.getY() - this.seekBarY);
      break;
      label158: if ((!this.useSeekBarWaweform) || (this.seekBarWaveform.isStartDraging()) || (paramMotionEvent.getAction() != 1))
        continue;
      didPressedButton(true);
    }
    label191: int k = AndroidUtilities.dp(36.0F);
    if ((this.buttonState == 0) || (this.buttonState == 1) || (this.buttonState == 2))
      if ((i >= this.buttonX - AndroidUtilities.dp(12.0F)) && (i <= this.buttonX - AndroidUtilities.dp(12.0F) + this.backgroundWidth) && (j >= this.namesOffset + this.mediaOffsetY) && (j <= this.layoutHeight))
        i = 1;
    while (true)
    {
      if (paramMotionEvent.getAction() != 0)
        break label372;
      if (i == 0)
        break label418;
      this.buttonPressed = 1;
      invalidate();
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      bool1 = bool2;
      break;
      i = 0;
      continue;
      if ((i >= this.buttonX) && (i <= this.buttonX + k) && (j >= this.buttonY) && (j <= this.buttonY + k))
      {
        i = 1;
        continue;
      }
      i = 0;
    }
    label372: if (this.buttonPressed != 0)
    {
      if (paramMotionEvent.getAction() != 1)
        break label421;
      this.buttonPressed = 0;
      playSoundEffect(0);
      didPressedButton(true);
      invalidate();
    }
    while (true)
    {
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      label418: break;
      label421: if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = 0;
        invalidate();
        continue;
      }
      if ((paramMotionEvent.getAction() != 2) || (i != 0))
        continue;
      this.buttonPressed = 0;
      invalidate();
    }
  }

  private boolean checkBotButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.botButtons.isEmpty());
    label200: 
    do
      while (true)
      {
        return false;
        int k = (int)paramMotionEvent.getX();
        int m = (int)paramMotionEvent.getY();
        if (paramMotionEvent.getAction() != 0)
          break;
        int i;
        int j;
        if (this.currentMessageObject.isOutOwner())
        {
          i = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
          j = 0;
        }
        while (true)
        {
          if (j >= this.botButtons.size())
            break label200;
          paramMotionEvent = (BotButton)this.botButtons.get(j);
          int n = paramMotionEvent.y + this.layoutHeight - AndroidUtilities.dp(2.0F);
          if ((k >= paramMotionEvent.x + i) && (k <= paramMotionEvent.x + i + paramMotionEvent.width) && (m >= n) && (m <= paramMotionEvent.height + n))
          {
            this.pressedBotButton = j;
            invalidate();
            return true;
            i = this.backgroundDrawableLeft;
            float f;
            if (this.mediaBackground)
              f = 1.0F;
            while (true)
            {
              i = AndroidUtilities.dp(f) + i;
              break;
              f = 7.0F;
            }
          }
          j += 1;
        }
      }
    while ((paramMotionEvent.getAction() != 1) || (this.pressedBotButton == -1));
    playSoundEffect(0);
    this.delegate.didPressedBotButton(this, ((BotButton)this.botButtons.get(this.pressedBotButton)).button);
    this.pressedBotButton = -1;
    invalidate();
    return false;
  }

  private boolean checkCaptionMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((!(this.currentMessageObject.caption instanceof Spannable)) || (this.captionLayout == null))
      return false;
    int i;
    int k;
    if ((paramMotionEvent.getAction() == 0) || (((this.linkPreviewPressed) || (this.pressedLink != null)) && (paramMotionEvent.getAction() == 1)))
    {
      i = (int)paramMotionEvent.getX();
      k = (int)paramMotionEvent.getY();
      if ((i < this.captionX) || (i > this.captionX + this.backgroundWidth) || (k < this.captionY) || (k > this.captionY + this.captionHeight))
        break label374;
      if (paramMotionEvent.getAction() != 0)
        break label341;
    }
    while (true)
    {
      try
      {
        i -= this.captionX;
        int m = this.captionY;
        k = this.captionLayout.getLineForVertical(k - m);
        m = this.captionLayout.getOffsetForHorizontal(k, i);
        float f = this.captionLayout.getLineLeft(k);
        if ((f <= i) && (this.captionLayout.getLineWidth(k) + f >= i))
        {
          paramMotionEvent = (Spannable)this.currentMessageObject.caption;
          Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(m, m, ClickableSpan.class);
          if (localObject.length == 0)
            break label387;
          if ((localObject.length == 0) || (!(localObject[0] instanceof URLSpanBotCommand)) || (URLSpanBotCommand.enabled))
            break label382;
          break label387;
          if (i == 0)
          {
            this.pressedLink = localObject[0];
            this.pressedLinkType = 3;
            resetUrlPaths(false);
            try
            {
              localObject = obtainNewUrlPath(false);
              j = paramMotionEvent.getSpanStart(this.pressedLink);
              ((LinkPath)localObject).setCurrentLayout(this.captionLayout, j, 0.0F);
              this.captionLayout.getSelectionPath(j, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
              invalidate();
              return true;
            }
            catch (Exception paramMotionEvent)
            {
              FileLog.e(paramMotionEvent);
              continue;
            }
          }
        }
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e(paramMotionEvent);
      }
      while (true)
      {
        return false;
        label341: if (this.pressedLinkType != 3)
          continue;
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
        resetPressedLink(3);
        return true;
        label374: resetPressedLink(3);
      }
      label382: int j = 0;
      continue;
      label387: j = 1;
    }
  }

  private boolean checkGameMotionEvent(MotionEvent paramMotionEvent)
  {
    if (!this.hasGamePreview);
    int i;
    int m;
    do
    {
      return false;
      i = (int)paramMotionEvent.getX();
      m = (int)paramMotionEvent.getY();
      if (paramMotionEvent.getAction() != 0)
        break;
      if ((!this.drawPhotoImage) || (!this.photoImage.isInsideImage(i, m)))
        continue;
      this.gamePreviewPressed = true;
      return true;
    }
    while ((this.descriptionLayout == null) || (m < this.descriptionY));
    while (true)
    {
      try
      {
        int j;
        i -= this.textX + AndroidUtilities.dp(10.0F) + this.descriptionX;
        int i1 = this.descriptionY;
        int n = this.descriptionLayout.getLineForVertical(m - i1);
        int i2 = this.descriptionLayout.getOffsetForHorizontal(n, j);
        float f = this.descriptionLayout.getLineLeft(n);
        if ((f > j) || (this.descriptionLayout.getLineWidth(n) + f < j))
          break;
        paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
        Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(i2, i2, ClickableSpan.class);
        if (localObject.length == 0)
          break label495;
        if ((localObject.length == 0) || (!(localObject[0] instanceof URLSpanBotCommand)) || (URLSpanBotCommand.enabled))
          break label490;
        break label495;
        if (j != 0)
          break;
        this.pressedLink = localObject[0];
        this.linkBlockNum = -10;
        this.pressedLinkType = 2;
        resetUrlPaths(false);
        try
        {
          localObject = obtainNewUrlPath(false);
          k = paramMotionEvent.getSpanStart(this.pressedLink);
          ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, k, 0.0F);
          this.descriptionLayout.getSelectionPath(k, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
          invalidate();
          return true;
        }
        catch (Exception paramMotionEvent)
        {
          FileLog.e(paramMotionEvent);
          continue;
        }
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e(paramMotionEvent);
        return false;
      }
      if (paramMotionEvent.getAction() != 1)
        break;
      if ((this.pressedLinkType == 2) || (this.gamePreviewPressed))
      {
        if (this.pressedLink != null)
        {
          if ((this.pressedLink instanceof URLSpan))
            Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
          while (true)
          {
            resetPressedLink(2);
            return false;
            if (!(this.pressedLink instanceof ClickableSpan))
              continue;
            ((ClickableSpan)this.pressedLink).onClick(this);
          }
        }
        this.gamePreviewPressed = false;
        k = 0;
        while (true)
        {
          if (k < this.botButtons.size())
          {
            paramMotionEvent = (BotButton)this.botButtons.get(k);
            if ((paramMotionEvent.button instanceof TLRPC.TL_keyboardButtonGame))
            {
              playSoundEffect(0);
              this.delegate.didPressedBotButton(this, paramMotionEvent.button);
              invalidate();
            }
          }
          else
          {
            resetPressedLink(2);
            return true;
          }
          k += 1;
        }
      }
      resetPressedLink(2);
      return false;
      label490: int k = 0;
      continue;
      label495: k = 1;
    }
  }

  private boolean checkLinkPreviewMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (!this.hasLinkPreview))
      return false;
    int k = (int)paramMotionEvent.getX();
    int m = (int)paramMotionEvent.getY();
    if ((k >= this.textX) && (k <= this.textX + this.backgroundWidth) && (m >= this.textY + this.currentMessageObject.textHeight) && (m <= this.textY + this.currentMessageObject.textHeight + this.linkPreviewHeight + AndroidUtilities.dp(8.0F)))
      if (paramMotionEvent.getAction() == 0)
        if ((this.descriptionLayout == null) || (m < this.descriptionY));
    while (true)
    {
      try
      {
        int i = k - (this.textX + AndroidUtilities.dp(10.0F) + this.descriptionX);
        int n = m - this.descriptionY;
        if (n <= this.descriptionLayout.getHeight())
        {
          n = this.descriptionLayout.getLineForVertical(n);
          int i1 = this.descriptionLayout.getOffsetForHorizontal(n, i);
          float f = this.descriptionLayout.getLineLeft(n);
          if ((f <= i) && (this.descriptionLayout.getLineWidth(n) + f >= i))
          {
            paramMotionEvent = (Spannable)this.currentMessageObject.linkDescription;
            Object localObject = (ClickableSpan[])paramMotionEvent.getSpans(i1, i1, ClickableSpan.class);
            if (localObject.length == 0)
              break label1012;
            if ((localObject.length == 0) || (!(localObject[0] instanceof URLSpanBotCommand)) || (URLSpanBotCommand.enabled))
              break label1007;
            break label1012;
            if (i == 0)
            {
              this.pressedLink = localObject[0];
              this.linkBlockNum = -10;
              this.pressedLinkType = 2;
              resetUrlPaths(false);
              try
              {
                localObject = obtainNewUrlPath(false);
                j = paramMotionEvent.getSpanStart(this.pressedLink);
                ((LinkPath)localObject).setCurrentLayout(this.descriptionLayout, j, 0.0F);
                this.descriptionLayout.getSelectionPath(j, paramMotionEvent.getSpanEnd(this.pressedLink), (Path)localObject);
                invalidate();
                return true;
              }
              catch (Exception paramMotionEvent)
              {
                FileLog.e(paramMotionEvent);
                continue;
              }
            }
          }
        }
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e(paramMotionEvent);
      }
      if (this.pressedLink == null)
      {
        if ((this.drawPhotoImage) && (this.drawImageButton) && (this.buttonState != -1) && (k >= this.buttonX) && (k <= this.buttonX + AndroidUtilities.dp(48.0F)) && (m >= this.buttonY) && (m <= this.buttonY + AndroidUtilities.dp(48.0F)))
        {
          this.buttonPressed = 1;
          return true;
        }
        if (this.drawInstantView)
        {
          this.instantPressed = true;
          invalidate();
          return true;
        }
        if ((this.documentAttachType != 1) && (this.drawPhotoImage) && (this.photoImage.isInsideImage(k, m)))
        {
          this.linkPreviewPressed = true;
          paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
          if ((this.documentAttachType == 2) && (this.buttonState == -1) && (MediaController.getInstance().canAutoplayGifs()) && ((this.photoImage.getAnimation() == null) || (!TextUtils.isEmpty(paramMotionEvent.embed_url))))
          {
            this.linkPreviewPressed = false;
            return false;
          }
          return true;
          if (paramMotionEvent.getAction() == 1)
          {
            if (!this.instantPressed)
              break label636;
            if (this.delegate != null)
              this.delegate.didPressedInstantButton(this);
            playSoundEffect(0);
            this.instantPressed = false;
            invalidate();
          }
        }
      }
      while (true)
      {
        return false;
        label636: if ((this.pressedLinkType == 2) || (this.buttonPressed != 0) || (this.linkPreviewPressed))
        {
          if (this.buttonPressed != 0)
          {
            this.buttonPressed = 0;
            playSoundEffect(0);
            didPressedButton(false);
            invalidate();
            continue;
          }
          if (this.pressedLink != null)
          {
            if ((this.pressedLink instanceof URLSpan))
              Browser.openUrl(getContext(), ((URLSpan)this.pressedLink).getURL());
            while (true)
            {
              resetPressedLink(2);
              break;
              if (!(this.pressedLink instanceof ClickableSpan))
                continue;
              ((ClickableSpan)this.pressedLink).onClick(this);
            }
          }
          if ((this.documentAttachType == 2) && (this.drawImageButton))
            if (this.buttonState == -1)
              if (MediaController.getInstance().canAutoplayGifs())
                this.delegate.didPressedImage(this);
          while (true)
          {
            resetPressedLink(2);
            return true;
            this.buttonState = 2;
            this.currentMessageObject.audioProgress = 1.0F;
            this.photoImage.setAllowStartAnimation(false);
            this.photoImage.stopAnimation();
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
            playSoundEffect(0);
            continue;
            if ((this.buttonState != 2) && (this.buttonState != 0))
              continue;
            didPressedButton(false);
            playSoundEffect(0);
            continue;
            paramMotionEvent = this.currentMessageObject.messageOwner.media.webpage;
            if ((paramMotionEvent != null) && (Build.VERSION.SDK_INT >= 16) && (!TextUtils.isEmpty(paramMotionEvent.embed_url)))
            {
              this.delegate.needOpenWebView(paramMotionEvent.embed_url, paramMotionEvent.site_name, paramMotionEvent.title, paramMotionEvent.url, paramMotionEvent.embed_width, paramMotionEvent.embed_height);
              continue;
            }
            if (this.buttonState == -1)
            {
              this.delegate.didPressedImage(this);
              playSoundEffect(0);
              continue;
            }
            if (paramMotionEvent == null)
              continue;
            Browser.openUrl(getContext(), paramMotionEvent.url);
          }
        }
        resetPressedLink(2);
      }
      label1007: int j = 0;
      continue;
      label1012: j = 1;
    }
  }

  private boolean checkNeedDrawShareButton(MessageObject paramMessageObject)
  {
    int j = 1;
    int i;
    if (paramMessageObject.type == 13)
      i = 0;
    do
    {
      do
      {
        while (true)
        {
          return i;
          if ((paramMessageObject.messageOwner.fwd_from != null) && (paramMessageObject.messageOwner.fwd_from.channel_id != 0))
          {
            i = j;
            if (!paramMessageObject.isOut())
              continue;
          }
          if (!paramMessageObject.isFromUser())
            break;
          if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) || (paramMessageObject.messageOwner.media == null) || (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (!(paramMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage))))
            return false;
          Object localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
          if (localObject != null)
          {
            i = j;
            if (((TLRPC.User)localObject).bot)
              continue;
          }
          if (paramMessageObject.isOut())
            break label326;
          i = j;
          if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
            continue;
          i = j;
          if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice))
            continue;
          if (!paramMessageObject.isMegagroup())
            break label326;
          localObject = MessagesController.getInstance().getChat(Integer.valueOf(paramMessageObject.messageOwner.to_id.channel_id));
          if ((localObject != null) && (((TLRPC.Chat)localObject).username != null) && (((TLRPC.Chat)localObject).username.length() > 0) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaContact)))
          {
            i = j;
            if (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo))
              continue;
          }
          return false;
        }
        if (((paramMessageObject.messageOwner.from_id >= 0) && (!paramMessageObject.messageOwner.post)) || (paramMessageObject.messageOwner.to_id.channel_id == 0))
          break label326;
        if (paramMessageObject.messageOwner.via_bot_id != 0)
          break;
        i = j;
      }
      while (paramMessageObject.messageOwner.reply_to_msg_id == 0);
      i = j;
    }
    while (paramMessageObject.type != 13);
    label326: return false;
  }

  private boolean checkOtherButtonMotionEvent(MotionEvent paramMotionEvent)
  {
    int i;
    int j;
    if (this.currentMessageObject.type == 16)
    {
      i = 1;
      j = i;
      if (i == 0)
      {
        if (((this.documentAttachType != 1) && (this.currentMessageObject.type != 12) && (this.documentAttachType != 5) && (this.documentAttachType != 4) && (this.documentAttachType != 2) && (this.currentMessageObject.type != 8)) || (this.hasGamePreview) || (this.hasInvoicePreview))
          break label103;
        j = 1;
      }
      label92: if (j != 0)
        break label108;
    }
    label103: label108: 
    do
    {
      do
      {
        do
        {
          return false;
          i = 0;
          break;
          j = 0;
          break label92;
          i = (int)paramMotionEvent.getX();
          j = (int)paramMotionEvent.getY();
          if (paramMotionEvent.getAction() != 0)
            break label274;
          if (this.currentMessageObject.type != 16)
            break label203;
        }
        while ((i < this.otherX) || (i > this.otherX + AndroidUtilities.dp(235.0F)) || (j < this.otherY - AndroidUtilities.dp(14.0F)) || (j > this.otherY + AndroidUtilities.dp(50.0F)));
        this.otherPressed = true;
        invalidate();
        return true;
      }
      while ((i < this.otherX - AndroidUtilities.dp(20.0F)) || (i > this.otherX + AndroidUtilities.dp(20.0F)) || (j < this.otherY - AndroidUtilities.dp(4.0F)) || (j > this.otherY + AndroidUtilities.dp(30.0F)));
      this.otherPressed = true;
      invalidate();
      return true;
    }
    while ((paramMotionEvent.getAction() != 1) || (!this.otherPressed));
    label203: this.otherPressed = false;
    label274: playSoundEffect(0);
    this.delegate.didPressedOther(this);
    invalidate();
    return false;
  }

  private boolean checkPhotoImageMotionEvent(MotionEvent paramMotionEvent)
  {
    int m = 1;
    int k = 1;
    if ((!this.drawPhotoImage) && (this.documentAttachType != 1))
      return false;
    int i = (int)paramMotionEvent.getX();
    int j = (int)paramMotionEvent.getY();
    if (paramMotionEvent.getAction() == 0)
      if ((this.buttonState != -1) && (i >= this.buttonX) && (i <= this.buttonX + AndroidUtilities.dp(48.0F)) && (j >= this.buttonY) && (j <= this.buttonY + AndroidUtilities.dp(48.0F)))
      {
        this.buttonPressed = 1;
        invalidate();
      }
    while (true)
    {
      m = k;
      if (this.imagePressed)
      {
        if (this.currentMessageObject.isSecretPhoto())
        {
          this.imagePressed = false;
          m = k;
        }
      }
      else
      {
        label135: return m;
        if (this.documentAttachType == 1)
        {
          if ((i < this.photoImage.getImageX()) || (i > this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(50.0F)) || (j < this.photoImage.getImageY()) || (j > this.photoImage.getImageY() + this.photoImage.getImageHeight()))
            break label587;
          this.imagePressed = true;
          continue;
        }
        if ((this.currentMessageObject.type == 13) && (this.currentMessageObject.getInputStickerSet() == null))
          break label587;
        if ((i < this.photoImage.getImageX()) || (i > this.photoImage.getImageX() + this.backgroundWidth) || (j < this.photoImage.getImageY()) || (j > this.photoImage.getImageY() + this.photoImage.getImageHeight()))
          break label581;
        this.imagePressed = true;
      }
      while (true)
      {
        k = m;
        if (this.currentMessageObject.type != 12)
          break;
        k = m;
        if (MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id)) != null)
          break;
        this.imagePressed = false;
        k = 0;
        break;
        if (this.currentMessageObject.isSendError())
        {
          this.imagePressed = false;
          m = 0;
          break label135;
        }
        m = k;
        if (this.currentMessageObject.type != 8)
          break label135;
        m = k;
        if (this.buttonState != -1)
          break label135;
        m = k;
        if (!MediaController.getInstance().canAutoplayGifs())
          break label135;
        m = k;
        if (this.photoImage.getAnimation() != null)
          break label135;
        this.imagePressed = false;
        m = 0;
        break label135;
        if (paramMotionEvent.getAction() == 1)
        {
          if (this.buttonPressed == 1)
          {
            this.buttonPressed = 0;
            playSoundEffect(0);
            didPressedButton(false);
            this.radialProgress.swapBackground(getDrawableForCurrentState());
            invalidate();
            m = 0;
            break label135;
          }
          if (this.imagePressed)
          {
            this.imagePressed = false;
            if ((this.buttonState != -1) && (this.buttonState != 2) && (this.buttonState != 3))
              break label553;
            playSoundEffect(0);
            didClickedImage();
          }
        }
        while (true)
        {
          invalidate();
          m = 0;
          break;
          label553: if ((this.buttonState != 0) || (this.documentAttachType != 1))
            continue;
          playSoundEffect(0);
          didPressedButton(false);
        }
        label581: m = 0;
      }
      label587: k = 0;
    }
  }

  private boolean checkTextBlockMotionEvent(MotionEvent paramMotionEvent)
  {
    if ((this.currentMessageObject.type != 0) || (this.currentMessageObject.textLayoutBlocks == null) || (this.currentMessageObject.textLayoutBlocks.isEmpty()) || (!(this.currentMessageObject.messageText instanceof Spannable)))
      return false;
    int m;
    int i;
    int i1;
    int k;
    if ((paramMotionEvent.getAction() == 0) || ((paramMotionEvent.getAction() == 1) && (this.pressedLinkType == 1)))
    {
      m = (int)paramMotionEvent.getX();
      i = (int)paramMotionEvent.getY();
      if ((m < this.textX) || (i < this.textY) || (m > this.textX + this.currentMessageObject.textWidth) || (i > this.textY + this.currentMessageObject.textHeight))
        break label970;
      i1 = i - this.textY;
      k = 0;
      i = 0;
      if ((i < this.currentMessageObject.textLayoutBlocks.size()) && (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i)).textYOffset <= i1));
    }
    while (true)
    {
      label236: Object localObject1;
      try
      {
        while (true)
        {
          Object localObject2 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(k);
          float f2 = m;
          float f3 = this.textX;
          float f1;
          Spannable localSpannable;
          if (((MessageObject.TextLayoutBlock)localObject2).isRtl())
          {
            f1 = this.currentMessageObject.textXOffset;
            i = (int)(f2 - (f3 - f1));
            m = (int)(i1 - ((MessageObject.TextLayoutBlock)localObject2).textYOffset);
            m = ((MessageObject.TextLayoutBlock)localObject2).textLayout.getLineForVertical(m);
            i1 = ((MessageObject.TextLayoutBlock)localObject2).textLayout.getOffsetForHorizontal(m, i);
            f1 = ((MessageObject.TextLayoutBlock)localObject2).textLayout.getLineLeft(m);
            if ((f1 <= i) && (((MessageObject.TextLayoutBlock)localObject2).textLayout.getLineWidth(m) + f1 >= i))
            {
              localSpannable = (Spannable)this.currentMessageObject.messageText;
              localObject1 = (CharacterStyle[])localSpannable.getSpans(i1, i1, ClickableSpan.class);
              if ((localObject1 != null) && (localObject1.length != 0))
                break label978;
              localObject1 = (CharacterStyle[])localSpannable.getSpans(i1, i1, URLSpanMono.class);
              j = 1;
              i1 = 0;
              if (localObject1.length == 0)
                break label984;
              m = i1;
              if (localObject1.length != 0)
              {
                m = i1;
                if ((localObject1[0] instanceof URLSpanBotCommand))
                {
                  m = i1;
                  if (!URLSpanBotCommand.enabled)
                    break label984;
                }
              }
              if (m == 0)
              {
                if (paramMotionEvent.getAction() != 0)
                  break label934;
                this.pressedLink = localObject1[0];
                this.linkBlockNum = k;
                this.pressedLinkType = 1;
                resetUrlPaths(false);
              }
            }
          }
          else
          {
            try
            {
              paramMotionEvent = obtainNewUrlPath(false);
              i1 = localSpannable.getSpanStart(this.pressedLink);
              int i2 = localSpannable.getSpanEnd(this.pressedLink);
              paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject2).textLayout, i1, 0.0F);
              ((MessageObject.TextLayoutBlock)localObject2).textLayout.getSelectionPath(i1, i2, paramMotionEvent);
              if (i2 >= ((MessageObject.TextLayoutBlock)localObject2).charactersEnd)
              {
                m = k + 1;
                label541: if (m < this.currentMessageObject.textLayoutBlocks.size())
                {
                  localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(m);
                  int i3 = ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
                  int i4 = ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
                  if (j == 0)
                    break label764;
                  paramMotionEvent = URLSpanMono.class;
                  label596: paramMotionEvent = (CharacterStyle[])localSpannable.getSpans(i3, i4, paramMotionEvent);
                  if ((paramMotionEvent != null) && (paramMotionEvent.length != 0) && (paramMotionEvent[0] == this.pressedLink))
                    break label771;
                }
              }
              label631: if (i1 <= ((MessageObject.TextLayoutBlock)localObject2).charactersOffset)
              {
                k -= 1;
                m = 0;
              }
              while (true)
              {
                if (k >= 0)
                {
                  localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(k);
                  i1 = ((MessageObject.TextLayoutBlock)localObject1).charactersEnd;
                  i2 = ((MessageObject.TextLayoutBlock)localObject1).charactersEnd;
                  if (j == 0)
                    break label990;
                  paramMotionEvent = URLSpanMono.class;
                  paramMotionEvent = (CharacterStyle[])localSpannable.getSpans(i1 - 1, i2 - 1, paramMotionEvent);
                  if ((paramMotionEvent != null) && (paramMotionEvent.length != 0))
                  {
                    paramMotionEvent = paramMotionEvent[0];
                    localObject2 = this.pressedLink;
                    if (paramMotionEvent == localObject2)
                      break label831;
                  }
                }
                label764: 
                do
                {
                  invalidate();
                  return true;
                  k = j;
                  j += 1;
                  break;
                  f1 = 0.0F;
                  break label236;
                  paramMotionEvent = ClickableSpan.class;
                  break label596;
                  paramMotionEvent = obtainNewUrlPath(false);
                  paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject1).textLayout, 0, ((MessageObject.TextLayoutBlock)localObject1).textYOffset - ((MessageObject.TextLayoutBlock)localObject2).textYOffset);
                  ((MessageObject.TextLayoutBlock)localObject1).textLayout.getSelectionPath(0, i2, paramMotionEvent);
                  if (i2 < ((MessageObject.TextLayoutBlock)localObject1).charactersEnd - 1)
                    break label631;
                  m += 1;
                  break label541;
                  paramMotionEvent = obtainNewUrlPath(false);
                  i1 = localSpannable.getSpanStart(this.pressedLink);
                  m -= ((MessageObject.TextLayoutBlock)localObject1).height;
                  paramMotionEvent.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject1).textLayout, i1, m);
                  ((MessageObject.TextLayoutBlock)localObject1).textLayout.getSelectionPath(i1, localSpannable.getSpanEnd(this.pressedLink), paramMotionEvent);
                  i2 = ((MessageObject.TextLayoutBlock)localObject1).charactersOffset;
                }
                while (i1 > i2);
                label771: label831: k -= 1;
              }
            }
            catch (Exception paramMotionEvent)
            {
              while (true)
                FileLog.e(paramMotionEvent);
            }
          }
        }
      }
      catch (Exception paramMotionEvent)
      {
        FileLog.e(paramMotionEvent);
      }
      while (true)
      {
        return false;
        label934: if (localObject1[0] != this.pressedLink)
          continue;
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, false);
        resetPressedLink(1);
        return true;
        label970: resetPressedLink(1);
      }
      label978: int j = 0;
      continue;
      label984: int n = 1;
      continue;
      label990: paramMotionEvent = ClickableSpan.class;
    }
  }

  private int createDocumentLayout(int paramInt, MessageObject paramMessageObject)
  {
    if (paramMessageObject.type == 0);
    for (this.documentAttach = paramMessageObject.messageOwner.media.webpage.document; this.documentAttach == null; this.documentAttach = paramMessageObject.messageOwner.media.document)
    {
      paramInt = 0;
      return paramInt;
    }
    label69: Object localObject1;
    if (MessageObject.isVoiceDocument(this.documentAttach))
    {
      this.documentAttachType = 3;
      i = 0;
      if (i >= this.documentAttach.attributes.size())
        break label1211;
      localObject1 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
      if (!(localObject1 instanceof TLRPC.TL_documentAttributeAudio));
    }
    label526: label1198: label1201: label1206: label1211: for (int i = ((TLRPC.DocumentAttribute)localObject1).duration; ; i = 0)
    {
      this.availableTimeWidth = (paramInt - AndroidUtilities.dp(94.0F) - (int)Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
      measureTime(paramMessageObject);
      int j = AndroidUtilities.dp(174.0F);
      int k = this.timeWidth;
      if (!this.hasLinkPreview)
        this.backgroundWidth = Math.min(paramInt, i * AndroidUtilities.dp(10.0F) + (j + k));
      this.seekBarWaveform.setMessageObject(paramMessageObject);
      return 0;
      i += 1;
      break label69;
      if (MessageObject.isMusicDocument(this.documentAttach))
      {
        this.documentAttachType = 5;
        paramInt -= AndroidUtilities.dp(86.0F);
        this.songLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicTitle().replace('\n', ' '), Theme.chat_audioTitlePaint, paramInt - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), Theme.chat_audioTitlePaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.songLayout.getLineCount() > 0)
          this.songX = (-(int)Math.ceil(this.songLayout.getLineLeft(0)));
        this.performerLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, paramInt, TextUtils.TruncateAt.END), Theme.chat_audioPerformerPaint, paramInt, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.performerLayout.getLineCount() > 0)
          this.performerX = (-(int)Math.ceil(this.performerLayout.getLineLeft(0)));
        paramInt = 0;
        label380: if (paramInt >= this.documentAttach.attributes.size())
          break label1206;
        paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(paramInt);
        if (!(paramMessageObject instanceof TLRPC.TL_documentAttributeAudio));
      }
      for (paramInt = paramMessageObject.duration; ; paramInt = 0)
      {
        paramInt = (int)Math.ceil(Theme.chat_audioTimePaint.measureText(String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(paramInt / 60), Integer.valueOf(paramInt % 60), Integer.valueOf(paramInt / 60), Integer.valueOf(paramInt % 60) })));
        this.availableTimeWidth = (this.backgroundWidth - AndroidUtilities.dp(94.0F) - paramInt);
        return paramInt;
        paramInt += 1;
        break label380;
        if (MessageObject.isVideoDocument(this.documentAttach))
        {
          this.documentAttachType = 4;
          paramInt = 0;
          if (paramInt >= this.documentAttach.attributes.size())
            break label1201;
          paramMessageObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(paramInt);
          if (!(paramMessageObject instanceof TLRPC.TL_documentAttributeVideo));
        }
        for (paramInt = paramMessageObject.duration; ; paramInt = 0)
        {
          i = paramInt / 60;
          paramMessageObject = String.format("%d:%02d, %s", new Object[] { Integer.valueOf(i), Integer.valueOf(paramInt - i * 60), AndroidUtilities.formatFileSize(this.documentAttach.size) });
          this.infoWidth = (int)Math.ceil(Theme.chat_infoPaint.measureText(paramMessageObject));
          this.infoLayout = new StaticLayout(paramMessageObject, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          return 0;
          paramInt += 1;
          break label526;
          boolean bool;
          if (((this.documentAttach.mime_type != null) && (this.documentAttach.mime_type.toLowerCase().startsWith("image/"))) || (((this.documentAttach.thumb instanceof TLRPC.TL_photoSize)) && (!(this.documentAttach.thumb.location instanceof TLRPC.TL_fileLocationUnavailable))))
          {
            bool = true;
            this.drawPhotoImage = bool;
            if (this.drawPhotoImage)
              break label1198;
            paramInt += AndroidUtilities.dp(30.0F);
          }
          while (true)
            while (true)
            {
              this.documentAttachType = 1;
              Object localObject2 = FileLoader.getDocumentFileName(this.documentAttach);
              if (localObject2 != null)
              {
                localObject1 = localObject2;
                if (((String)localObject2).length() != 0);
              }
              else
              {
                localObject1 = LocaleController.getString("AttachDocument", 2131165362);
              }
              localObject2 = Theme.chat_docNamePaint;
              Layout.Alignment localAlignment = Layout.Alignment.ALIGN_NORMAL;
              TextUtils.TruncateAt localTruncateAt = TextUtils.TruncateAt.MIDDLE;
              if (this.drawPhotoImage);
              for (i = 2; ; i = 1)
              {
                this.docTitleLayout = StaticLayoutEx.createStaticLayout((CharSequence)localObject1, (TextPaint)localObject2, paramInt, localAlignment, 1.0F, 0.0F, false, localTruncateAt, paramInt, i);
                this.docTitleOffsetX = -2147483648;
                if ((this.docTitleLayout == null) || (this.docTitleLayout.getLineCount() <= 0))
                  break label1165;
                j = 0;
                i = 0;
                while (i < this.docTitleLayout.getLineCount())
                {
                  j = Math.max(j, (int)Math.ceil(this.docTitleLayout.getLineWidth(i)));
                  this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int)Math.ceil(-this.docTitleLayout.getLineLeft(i)));
                  i += 1;
                }
                bool = false;
                break;
              }
              i = Math.min(paramInt, j);
              localObject1 = AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
              this.infoWidth = Math.min(paramInt - AndroidUtilities.dp(30.0F), (int)Math.ceil(Theme.chat_infoPaint.measureText((String)localObject1)));
              localObject1 = TextUtils.ellipsize((CharSequence)localObject1, Theme.chat_infoPaint, this.infoWidth, TextUtils.TruncateAt.END);
              try
              {
                if (this.infoWidth < 0)
                  this.infoWidth = AndroidUtilities.dp(10.0F);
                this.infoLayout = new StaticLayout((CharSequence)localObject1, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                paramInt = i;
                if (!this.drawPhotoImage)
                  break;
                this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(paramMessageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                this.photoImage.setNeedsQualityThumb(true);
                this.photoImage.setShouldGenerateQualityThumb(true);
                this.photoImage.setParentMessageObject(paramMessageObject);
                if (this.currentPhotoObject != null)
                {
                  this.currentPhotoFilter = "86_86_b";
                  this.photoImage.setImage(null, null, null, null, this.currentPhotoObject.location, this.currentPhotoFilter, 0, null, true);
                  return i;
                  this.docTitleOffsetX = 0;
                  i = paramInt;
                }
              }
              catch (Exception localException)
              {
                while (true)
                  FileLog.e(localException);
                this.photoImage.setImageBitmap((BitmapDrawable)null);
                return i;
              }
            }
        }
      }
    }
  }

  private void didClickedImage()
  {
    if ((this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 13))
      if (this.buttonState == -1)
        this.delegate.didPressedImage(this);
    label41: 
    do
      while (true)
      {
        break label41;
        break label41;
        break label41;
        break label41;
        break label41;
        do
          return;
        while (this.buttonState != 0);
        didPressedButton(false);
        return;
        if (this.currentMessageObject.type == 12)
        {
          localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.media.user_id));
          this.delegate.didPressedUserAvatar(this, (TLRPC.User)localObject);
          return;
        }
        if (this.currentMessageObject.type == 8)
        {
          if (this.buttonState == -1)
          {
            if (MediaController.getInstance().canAutoplayGifs())
            {
              if (this.currentMessageObject.isVideoVoice())
                continue;
              this.delegate.didPressedImage(this);
              return;
            }
            this.buttonState = 2;
            this.currentMessageObject.audioProgress = 1.0F;
            this.photoImage.setAllowStartAnimation(false);
            this.photoImage.stopAnimation();
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
            invalidate();
            return;
          }
          if ((this.buttonState != 2) && (this.buttonState != 0))
            continue;
          didPressedButton(false);
          return;
        }
        if (this.documentAttachType == 4)
        {
          if ((this.buttonState != 0) && (this.buttonState != 3))
            continue;
          didPressedButton(false);
          return;
        }
        if (this.currentMessageObject.type == 4)
        {
          this.delegate.didPressedImage(this);
          return;
        }
        if (this.documentAttachType == 1)
        {
          if (this.buttonState != -1)
            continue;
          this.delegate.didPressedImage(this);
          return;
        }
        if (this.documentAttachType != 2)
          break;
        if (this.buttonState != -1)
          continue;
        Object localObject = this.currentMessageObject.messageOwner.media.webpage;
        if (localObject == null)
          continue;
        if ((Build.VERSION.SDK_INT >= 16) && (((TLRPC.WebPage)localObject).embed_url != null) && (((TLRPC.WebPage)localObject).embed_url.length() != 0))
        {
          this.delegate.needOpenWebView(((TLRPC.WebPage)localObject).embed_url, ((TLRPC.WebPage)localObject).site_name, ((TLRPC.WebPage)localObject).description, ((TLRPC.WebPage)localObject).url, ((TLRPC.WebPage)localObject).embed_width, ((TLRPC.WebPage)localObject).embed_height);
          return;
        }
        Browser.openUrl(getContext(), ((TLRPC.WebPage)localObject).url);
        return;
      }
    while ((!this.hasInvoicePreview) || (this.buttonState != -1));
    this.delegate.didPressedImage(this);
  }

  private void didPressedButton(boolean paramBoolean)
  {
    if (this.buttonState == 0)
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
        if (this.delegate.needPlayAudio(this.currentMessageObject))
        {
          this.buttonState = 1;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
        }
    while (true)
    {
      return;
      this.cancelLoading = false;
      this.radialProgress.setProgress(0.0F, false);
      if (this.currentMessageObject.type == 1)
      {
        localImageReceiver = this.photoImage;
        localObject = this.currentPhotoObject.location;
        str = this.currentPhotoFilter;
        if (this.currentPhotoObjectThumb != null)
        {
          localFileLocation = this.currentPhotoObjectThumb.location;
          localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilter, this.currentPhotoObject.size, null, false);
        }
      }
      while (true)
      {
        this.buttonState = 1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
        invalidate();
        return;
        localFileLocation = null;
        break;
        if (this.currentMessageObject.type == 8)
        {
          this.currentMessageObject.audioProgress = 2.0F;
          localImageReceiver = this.photoImage;
          localObject = this.currentMessageObject.messageOwner.media.document;
          if (this.currentPhotoObject != null);
          for (localFileLocation = this.currentPhotoObject.location; ; localFileLocation = null)
          {
            localImageReceiver.setImage((TLObject)localObject, null, localFileLocation, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.document.size, null, false);
            break;
          }
        }
        if (this.currentMessageObject.type == 9)
        {
          FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.document, false, false);
          continue;
        }
        if (this.documentAttachType == 4)
        {
          FileLoader.getInstance().loadFile(this.documentAttach, true, false);
          continue;
        }
        if ((this.currentMessageObject.type != 0) || (this.documentAttachType == 0))
          break label451;
        if (this.documentAttachType == 2)
        {
          this.photoImage.setImage(this.currentMessageObject.messageOwner.media.webpage.document, null, this.currentPhotoObject.location, this.currentPhotoFilter, this.currentMessageObject.messageOwner.media.webpage.document.size, null, false);
          this.currentMessageObject.audioProgress = 2.0F;
          continue;
        }
        if (this.documentAttachType != 1)
          continue;
        FileLoader.getInstance().loadFile(this.currentMessageObject.messageOwner.media.webpage.document, false, false);
      }
      label451: ImageReceiver localImageReceiver = this.photoImage;
      Object localObject = this.currentPhotoObject.location;
      String str = this.currentPhotoFilter;
      if (this.currentPhotoObjectThumb != null);
      for (TLRPC.FileLocation localFileLocation = this.currentPhotoObjectThumb.location; ; localFileLocation = null)
      {
        localImageReceiver.setImage((TLObject)localObject, str, localFileLocation, this.currentPhotoFilterThumb, 0, null, false);
        break;
      }
      if (this.buttonState == 1)
      {
        if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
        {
          if (!MediaController.getInstance().pauseAudio(this.currentMessageObject))
            continue;
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
          invalidate();
          return;
        }
        if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          this.delegate.didPressedCancelSendButton(this);
          return;
        }
        this.cancelLoading = true;
        if ((this.documentAttachType == 4) || (this.documentAttachType == 1))
          FileLoader.getInstance().cancelLoadFile(this.documentAttach);
        while (true)
        {
          this.buttonState = 0;
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          invalidate();
          return;
          if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.type == 1) || (this.currentMessageObject.type == 8))
          {
            this.photoImage.cancelLoadImage();
            continue;
          }
          if (this.currentMessageObject.type != 9)
            continue;
          FileLoader.getInstance().cancelLoadFile(this.currentMessageObject.messageOwner.media.document);
        }
      }
      if (this.buttonState == 2)
      {
        if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
        {
          this.radialProgress.setProgress(0.0F, false);
          FileLoader.getInstance().loadFile(this.documentAttach, true, false);
          this.buttonState = 4;
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
          invalidate();
          return;
        }
        this.photoImage.setAllowStartAnimation(true);
        this.photoImage.startAnimation();
        this.currentMessageObject.audioProgress = 0.0F;
        this.buttonState = -1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        return;
      }
      if (this.buttonState == 3)
      {
        this.delegate.didPressedImage(this);
        return;
      }
      if ((this.buttonState != 4) || ((this.documentAttachType != 3) && (this.documentAttachType != 5)))
        continue;
      if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && (!this.currentMessageObject.isSendError()))
        break;
      if (this.delegate == null)
        continue;
      this.delegate.didPressedCancelSendButton(this);
      return;
    }
    FileLoader.getInstance().cancelLoadFile(this.documentAttach);
    this.buttonState = 2;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
    invalidate();
  }

  private void drawContent(Canvas paramCanvas)
  {
    if ((this.needNewVisiblePart) && (this.currentMessageObject.type == 0))
    {
      getLocalVisibleRect(this.scrollRect);
      setVisiblePart(this.scrollRect.top, this.scrollRect.bottom - this.scrollRect.top);
      this.needNewVisiblePart = false;
    }
    this.forceNotDrawTime = false;
    this.photoImage.setPressed(isDrawSelectedBackground());
    Object localObject1 = this.photoImage;
    boolean bool1;
    label234: int i2;
    label382: Object localObject4;
    label409: label489: int i26;
    if (!PhotoViewer.getInstance().isShowingImage(this.currentMessageObject))
    {
      bool1 = true;
      ((ImageReceiver)localObject1).setVisible(bool1, false);
      this.radialProgress.setHideCurrentDrawable(false);
      this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
      if (this.currentMessageObject.type != 0)
        break label3650;
      if (!this.currentMessageObject.isOutOwner())
        break label1249;
      this.textX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F));
      if (!this.hasGamePreview)
        break label1300;
      this.textX += AndroidUtilities.dp(11.0F);
      this.textY = (AndroidUtilities.dp(14.0F) + this.namesOffset);
      if (this.siteNameLayout != null)
        this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
      if ((this.currentMessageObject.textLayoutBlocks != null) && (!this.currentMessageObject.textLayoutBlocks.isEmpty()))
      {
        if (this.fullyDraw)
        {
          this.firstVisibleBlockNum = 0;
          this.lastVisibleBlockNum = this.currentMessageObject.textLayoutBlocks.size();
        }
        if (this.firstVisibleBlockNum >= 0)
        {
          i = this.firstVisibleBlockNum;
          if ((i <= this.lastVisibleBlockNum) && (i < this.currentMessageObject.textLayoutBlocks.size()))
            break label1375;
        }
      }
      if ((!this.hasLinkPreview) && (!this.hasGamePreview) && (!this.hasInvoicePreview))
        break label8068;
      if (!this.hasGamePreview)
        break label1605;
      i = AndroidUtilities.dp(14.0F);
      int i11 = this.namesOffset;
      i2 = this.textX - AndroidUtilities.dp(10.0F);
      i = i11 + i;
      if (!this.hasInvoicePreview)
      {
        localObject4 = Theme.chat_replyLinePaint;
        if (!this.currentMessageObject.isOutOwner())
          break label1694;
        localObject1 = "chat_outPreviewLine";
        ((Paint)localObject4).setColor(Theme.getColor((String)localObject1));
        paramCanvas.drawRect(i2, i - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(2.0F) + i2, this.linkPreviewHeight + i + AndroidUtilities.dp(3.0F), Theme.chat_replyLinePaint);
      }
      if (this.siteNameLayout == null)
        break label8061;
      localObject4 = Theme.chat_replyNamePaint;
      if (!this.currentMessageObject.isOutOwner())
        break label1702;
      localObject1 = "chat_outSiteNameText";
      ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject1));
      paramCanvas.save();
      if (!this.hasInvoicePreview)
        break label1710;
      int i12 = 0;
      label514: paramCanvas.translate(i12 + i2, i - AndroidUtilities.dp(3.0F));
      this.siteNameLayout.draw(paramCanvas);
      paramCanvas.restore();
      i18 = this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1) + i;
      int i25;
      if (!this.hasGamePreview)
      {
        i12 = i18;
        i25 = i;
        if (!this.hasInvoicePreview);
      }
      else
      {
        i12 = i18;
        i25 = i;
        if (this.currentMessageObject.textHeight != 0)
        {
          i26 = i + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
          i12 = i18 + (this.currentMessageObject.textHeight + AndroidUtilities.dp(4.0F));
        }
      }
      if ((!this.drawPhotoImage) || (!this.drawInstantView))
        break label8055;
      i = i12;
      if (i12 != i26)
        i = i12 + AndroidUtilities.dp(2.0F);
      this.photoImage.setImageCoords(AndroidUtilities.dp(10.0F) + i2, i, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      if (this.drawImageButton)
      {
        i12 = AndroidUtilities.dp(48.0F);
        this.buttonX = (int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - i12) / 2.0F);
        this.buttonY = (int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - i12) / 2.0F);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + i12, i12 + this.buttonY);
      }
      bool1 = this.photoImage.draw(paramCanvas);
      i12 = i + (this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F));
      label838: if (!this.currentMessageObject.isOutOwner())
        break label1721;
      Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextOut"));
      Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
      label872: if (this.titleLayout == null)
        break label8045;
      i = i12;
      if (i12 != i26)
        i = i12 + AndroidUtilities.dp(2.0F);
      i12 = AndroidUtilities.dp(1.0F);
      paramCanvas.save();
      paramCanvas.translate(AndroidUtilities.dp(10.0F) + i2 + this.titleX, i - AndroidUtilities.dp(3.0F));
      this.titleLayout.draw(paramCanvas);
      paramCanvas.restore();
      i18 = i + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
      i -= i12;
      label979: i12 = i;
      int i22 = i18;
      int i23;
      if (this.authorLayout != null)
      {
        i22 = i18;
        if (i18 != i26)
          i23 = i18 + AndroidUtilities.dp(2.0F);
        i12 = i;
        if (i == 0)
          i13 = i23 - AndroidUtilities.dp(1.0F);
        paramCanvas.save();
        paramCanvas.translate(AndroidUtilities.dp(10.0F) + i2 + this.authorX, i23 - AndroidUtilities.dp(3.0F));
        this.authorLayout.draw(paramCanvas);
        paramCanvas.restore();
        i23 += this.authorLayout.getLineBottom(this.authorLayout.getLineCount() - 1);
      }
      if (this.descriptionLayout == null)
        break label8034;
      if (i23 == i26)
        break label8027;
      i18 = i23 + AndroidUtilities.dp(2.0F);
      label1122: if (i13 != 0)
        break label8020;
      i = i18 - AndroidUtilities.dp(1.0F);
      label1136: this.descriptionY = (i18 - AndroidUtilities.dp(3.0F));
      paramCanvas.save();
      if (!this.hasInvoicePreview)
        break label1748;
    }
    label1249: float f1;
    label1300: label1375: int i4;
    label1605: Object localObject2;
    label1694: label1702: label1710: label1721: label1748: for (int i13 = 0; ; i15 = AndroidUtilities.dp(10.0F))
    {
      while (true)
      {
        paramCanvas.translate(i13 + i2 + this.descriptionX, this.descriptionY);
        if ((this.pressedLink == null) || (this.linkBlockNum != -10))
          break label1759;
        i13 = 0;
        while (i13 < this.urlPath.size())
        {
          paramCanvas.drawPath((Path)this.urlPath.get(i13), Theme.chat_urlPaint);
          i13 += 1;
        }
        bool1 = false;
        break;
        i = this.currentBackgroundDrawable.getBounds().left;
        if ((!this.mediaBackground) && (this.pinnedBottom))
          f1 = 11.0F;
        while (true)
        {
          this.textX = (AndroidUtilities.dp(f1) + i);
          break;
          f1 = 17.0F;
        }
        if (this.hasInvoicePreview)
        {
          this.textY = (AndroidUtilities.dp(14.0F) + this.namesOffset);
          if (this.siteNameLayout == null)
            break label234;
          this.textY += this.siteNameLayout.getLineBottom(this.siteNameLayout.getLineCount() - 1);
          break label234;
        }
        this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
        break label234;
        localObject1 = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
        paramCanvas.save();
        i13 = this.textX;
        if (((MessageObject.TextLayoutBlock)localObject1).isRtl());
        for (int i3 = (int)Math.ceil(this.currentMessageObject.textXOffset); ; i4 = 0)
        {
          paramCanvas.translate(i13 - i3, this.textY + ((MessageObject.TextLayoutBlock)localObject1).textYOffset);
          if ((this.pressedLink == null) || (i != this.linkBlockNum))
            break;
          i4 = 0;
          while (i4 < this.urlPath.size())
          {
            paramCanvas.drawPath((Path)this.urlPath.get(i4), Theme.chat_urlPaint);
            i4 += 1;
          }
        }
        if ((i == this.linkSelectionBlockNum) && (!this.urlPathSelection.isEmpty()))
        {
          i4 = 0;
          while (i4 < this.urlPathSelection.size())
          {
            paramCanvas.drawPath((Path)this.urlPathSelection.get(i4), Theme.chat_textSearchSelectionPaint);
            i4 += 1;
          }
        }
        try
        {
          ((MessageObject.TextLayoutBlock)localObject1).textLayout.draw(paramCanvas);
          paramCanvas.restore();
          i += 1;
        }
        catch (Exception localException1)
        {
          while (true)
            FileLog.e(localException1);
        }
      }
      if (this.hasInvoicePreview)
      {
        i = AndroidUtilities.dp(14.0F);
        i13 = this.namesOffset;
        i4 = this.textX + AndroidUtilities.dp(1.0F);
        i = i13 + i;
        break label382;
      }
      i = this.textY;
      int i14 = this.currentMessageObject.textHeight;
      i18 = AndroidUtilities.dp(8.0F);
      i4 = this.textX + AndroidUtilities.dp(1.0F);
      i = i18 + (i + i14);
      break label382;
      localObject2 = "chat_inPreviewLine";
      break label409;
      localObject2 = "chat_inSiteNameText";
      break label489;
      i15 = AndroidUtilities.dp(10.0F);
      break label514;
      Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_messageTextIn"));
      Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
      break label872;
    }
    label1759: this.descriptionLayout.draw(paramCanvas);
    paramCanvas.restore();
    int i15 = this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + i18;
    int i18 = i;
    int i = i15;
    label1800: label1892: label2027: Object localObject5;
    label2068: label2140: TextPaint localTextPaint;
    if ((this.drawPhotoImage) && (!this.drawInstantView))
    {
      i15 = i;
      if (i != i26)
        i15 = i + AndroidUtilities.dp(2.0F);
      if (this.isSmallImage)
      {
        this.photoImage.setImageCoords(this.backgroundWidth + i4 - AndroidUtilities.dp(81.0F), i18, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        bool1 = this.photoImage.draw(paramCanvas);
        i = i15;
        if ((this.videoInfoLayout != null) && ((!this.drawPhotoImage) || (this.photoImage.getVisible())))
        {
          if ((!this.hasGamePreview) && (!this.hasInvoicePreview))
            break label3388;
          if (!this.drawPhotoImage)
            break label3377;
          i15 = this.photoImage.getImageX();
          i18 = AndroidUtilities.dp(8.5F) + i15;
          i15 = this.photoImage.getImageY() + AndroidUtilities.dp(6.0F);
          Theme.chat_timeBackgroundDrawable.setBounds(i18 - AndroidUtilities.dp(4.0F), i15 - AndroidUtilities.dp(1.5F), this.durationWidth + i18 + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(16.5F) + i15);
          Theme.chat_timeBackgroundDrawable.draw(paramCanvas);
          paramCanvas.save();
          paramCanvas.translate(i18, i15);
          if (this.hasInvoicePreview)
          {
            if (!this.drawPhotoImage)
              break label3498;
            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_previewGameText"));
          }
          this.videoInfoLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        bool2 = bool1;
        if (this.drawInstantView)
        {
          i4 += AndroidUtilities.dp(10.0F);
          i += AndroidUtilities.dp(4.0F);
          localObject5 = Theme.chat_instantViewRectPaint;
          if (!this.currentMessageObject.isOutOwner())
            break label3562;
          if (!this.instantPressed)
            break label3538;
          localObject2 = Theme.chat_msgOutInstantSelectedDrawable;
          localTextPaint = Theme.chat_instantViewPaint;
          if (!this.instantPressed)
            break label3546;
          localObject4 = "chat_outPreviewInstantSelectedText";
          label2157: localTextPaint.setColor(Theme.getColor((String)localObject4));
          if (!this.instantPressed)
            break label3554;
          localObject4 = "chat_outPreviewInstantSelectedText";
          label2179: ((Paint)localObject5).setColor(Theme.getColor((String)localObject4));
          this.rect.set(i4, i, this.instantWidth + i4, AndroidUtilities.dp(30.0F) + i);
          paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(3.0F), (Paint)localObject5);
          setDrawableBounds((Drawable)localObject2, i4 + AndroidUtilities.dp(9.0F), i + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(9.0F), AndroidUtilities.dp(13.0F));
          ((Drawable)localObject2).draw(paramCanvas);
          bool2 = bool1;
          if (this.instantViewLayout != null)
          {
            paramCanvas.save();
            paramCanvas.translate(this.instantTextX + i4 + AndroidUtilities.dp(24.0F), AndroidUtilities.dp(8.0F) + i);
            this.instantViewLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
        }
      }
    }
    label4608: label7297: label8068: for (boolean bool2 = bool1; ; bool2 = false)
    {
      this.drawTime = true;
      label2352: label2400: int i24;
      label2763: label3538: label3546: label3554: label3562: label3574: label4985: label5373: int i17;
      while (true)
      {
        int j;
        long l1;
        long l2;
        if ((this.buttonState == -1) && (this.currentMessageObject.isSecretPhoto()))
        {
          j = 4;
          if (this.currentMessageObject.messageOwner.destroyTime != 0)
          {
            if (!this.currentMessageObject.isOutOwner())
              break label3681;
            j = 6;
          }
          setDrawableBounds(Theme.chat_photoStatesDrawables[j][this.buttonPressed], this.buttonX, this.buttonY);
          Theme.chat_photoStatesDrawables[j][this.buttonPressed].setAlpha((int)(255.0F * (1.0F - this.radialProgress.getAlpha())));
          Theme.chat_photoStatesDrawables[j][this.buttonPressed].draw(paramCanvas);
          if ((!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.messageOwner.destroyTime != 0))
          {
            l1 = System.currentTimeMillis();
            l2 = ConnectionsManager.getInstance().getTimeDifference() * 1000;
            f1 = (float)Math.max(0L, this.currentMessageObject.messageOwner.destroyTime * 1000L - (l1 + l2)) / (this.currentMessageObject.messageOwner.ttl * 1000.0F);
            paramCanvas.drawArc(this.deleteProgressRect, -90.0F, -360.0F * f1, true, Theme.chat_deleteProgressPaint);
            if (f1 != 0.0F)
            {
              j = AndroidUtilities.dp(2.0F);
              invalidate((int)this.deleteProgressRect.left - j, (int)this.deleteProgressRect.top - j, (int)this.deleteProgressRect.right + j * 2, j * 2 + (int)this.deleteProgressRect.bottom);
            }
            updateSecretTimeText(this.currentMessageObject);
          }
        }
        int i5;
        int i16;
        int i19;
        if (((this.documentAttachType == 2) || (this.currentMessageObject.type == 8)) && (!this.currentMessageObject.isVideoVoice()))
        {
          if ((this.photoImage.getVisible()) && (!this.hasGamePreview))
          {
            localObject2 = Theme.chat_msgMediaMenuDrawable;
            j = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
            this.otherX = j;
            i5 = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
            this.otherY = i5;
            setDrawableBounds((Drawable)localObject2, j, i5);
            Theme.chat_msgMediaMenuDrawable.draw(paramCanvas);
          }
          if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4))
            break label4616;
          if (this.photoImage.getVisible())
          {
            if (this.documentAttachType == 4)
            {
              localObject2 = Theme.chat_msgMediaMenuDrawable;
              j = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(14.0F);
              this.otherX = j;
              i5 = this.photoImage.getImageY() + AndroidUtilities.dp(8.1F);
              this.otherY = i5;
              setDrawableBounds((Drawable)localObject2, j, i5);
              Theme.chat_msgMediaMenuDrawable.draw(paramCanvas);
            }
            if ((this.infoLayout != null) && ((this.buttonState == 1) || (this.buttonState == 0) || (this.buttonState == 3) || (this.currentMessageObject.isSecretPhoto())))
            {
              Theme.chat_infoPaint.setColor(Theme.getColor("chat_mediaInfoText"));
              localObject2 = Theme.chat_timeBackgroundDrawable;
              j = this.photoImage.getImageX();
              i5 = AndroidUtilities.dp(4.0F);
              i16 = this.photoImage.getImageY();
              i19 = AndroidUtilities.dp(4.0F);
              i24 = this.infoWidth;
              setDrawableBounds((Drawable)localObject2, i5 + j, i19 + i16, AndroidUtilities.dp(8.0F) + i24, AndroidUtilities.dp(16.5F));
              Theme.chat_timeBackgroundDrawable.draw(paramCanvas);
              paramCanvas.save();
              paramCanvas.translate(this.photoImage.getImageX() + AndroidUtilities.dp(8.0F), this.photoImage.getImageY() + AndroidUtilities.dp(5.5F));
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
            }
          }
        }
        label3377: label3388: int i20;
        label3498: label3634: label3642: label3650: label3681: label3941: label4583: 
        do
        {
          int k;
          int i7;
          do
          {
            if (this.captionLayout == null)
              break label5833;
            paramCanvas.save();
            if ((this.currentMessageObject.type != 1) && (this.documentAttachType != 4) && (this.currentMessageObject.type != 8))
              break label5716;
            j = this.photoImage.getImageX() + AndroidUtilities.dp(5.0F);
            this.captionX = j;
            f1 = j;
            j = this.photoImage.getImageY() + this.photoImage.getImageHeight() + AndroidUtilities.dp(6.0F);
            this.captionY = j;
            paramCanvas.translate(f1, j);
            if (this.pressedLink == null)
              break label5821;
            k = 0;
            while (k < this.urlPath.size())
            {
              paramCanvas.drawPath((Path)this.urlPath.get(k), Theme.chat_urlPaint);
              k += 1;
            }
            localObject2 = this.photoImage;
            if (this.hasInvoicePreview);
            for (k = -AndroidUtilities.dp(6.3F); ; k = AndroidUtilities.dp(10.0F))
            {
              ((ImageReceiver)localObject2).setImageCoords(k + i5, i16, this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
              if (!this.drawImageButton)
                break;
              k = AndroidUtilities.dp(48.0F);
              this.buttonX = (int)(this.photoImage.getImageX() + (this.photoImage.getImageWidth() - k) / 2.0F);
              this.buttonY = (int)(this.photoImage.getImageY() + (this.photoImage.getImageHeight() - k) / 2.0F);
              this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + k, k + this.buttonY);
              break;
            }
            i16 = k;
            i19 = i5;
            break label2027;
            i20 = this.photoImage.getImageX() + this.photoImage.getImageWidth() - AndroidUtilities.dp(8.0F) - this.durationWidth;
            i16 = this.photoImage.getImageY() + this.photoImage.getImageHeight() - AndroidUtilities.dp(19.0F);
            Theme.chat_timeBackgroundDrawable.setBounds(i20 - AndroidUtilities.dp(4.0F), i16 - AndroidUtilities.dp(1.5F), this.durationWidth + i20 + AndroidUtilities.dp(4.0F), AndroidUtilities.dp(14.5F) + i16);
            Theme.chat_timeBackgroundDrawable.draw(paramCanvas);
            break label2027;
            if (this.currentMessageObject.isOutOwner())
            {
              Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextOut"));
              break label2068;
            }
            Theme.chat_shipmentPaint.setColor(Theme.getColor("chat_messageTextIn"));
            break label2068;
            localObject2 = Theme.chat_msgOutInstantDrawable;
            break label2140;
            localObject4 = "chat_outPreviewInstantText";
            break label2157;
            localObject4 = "chat_outPreviewInstantText";
            break label2179;
            if (this.instantPressed)
            {
              localObject2 = Theme.chat_msgInInstantSelectedDrawable;
              localTextPaint = Theme.chat_instantViewPaint;
              if (!this.instantPressed)
                break label3634;
              localObject4 = "chat_inPreviewInstantSelectedText";
              localTextPaint.setColor(Theme.getColor((String)localObject4));
              if (!this.instantPressed)
                break label3642;
            }
            for (localObject4 = "chat_inPreviewInstantSelectedText"; ; localObject4 = "chat_inPreviewInstantText")
            {
              ((Paint)localObject5).setColor(Theme.getColor((String)localObject4));
              break;
              localObject2 = Theme.chat_msgInInstantDrawable;
              break label3574;
              localObject4 = "chat_inPreviewInstantText";
              break label3591;
            }
            if (!this.drawPhotoImage)
              break label8011;
            bool2 = this.photoImage.draw(paramCanvas);
            this.drawTime = this.photoImage.getVisible();
            break label2352;
            k = 5;
            break label2400;
            if (this.documentAttachType == 5)
            {
              int i6;
              if (this.currentMessageObject.isOutOwner())
              {
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_outAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor("chat_outAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor("chat_outAudioDurationText"));
                localObject4 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0))
                {
                  localObject2 = "chat_outAudioSelectedProgress";
                  ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject2));
                  this.radialProgress.draw(paramCanvas);
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX + this.songX, AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
                  this.songLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  paramCanvas.save();
                  if (!MediaController.getInstance().isPlayingAudio(this.currentMessageObject))
                    break label4113;
                  paramCanvas.translate(this.seekBarX, this.seekBarY);
                  this.seekBar.draw(paramCanvas);
                  paramCanvas.restore();
                  paramCanvas.save();
                  paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(57.0F) + this.namesOffset + this.mediaOffsetY);
                  this.durationLayout.draw(paramCanvas);
                  paramCanvas.restore();
                  if (!this.currentMessageObject.isOutOwner())
                    break label4163;
                  if (!isDrawSelectedBackground())
                    break label4155;
                  localObject2 = Theme.chat_msgOutMenuSelectedDrawable;
                  k = this.buttonX;
                  i6 = this.backgroundWidth;
                  if (this.currentMessageObject.type != 0)
                    break label4186;
                  f1 = 58.0F;
                }
              }
              while (true)
              {
                k = i6 + k - AndroidUtilities.dp(f1);
                this.otherX = k;
                i7 = this.buttonY - AndroidUtilities.dp(5.0F);
                this.otherY = i7;
                setDrawableBounds((Drawable)localObject2, k, i7);
                ((Drawable)localObject2).draw(paramCanvas);
                break;
                localObject2 = "chat_outAudioProgress";
                break label3766;
                Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_inAudioTitleText"));
                Theme.chat_audioPerformerPaint.setColor(Theme.getColor("chat_inAudioPerfomerText"));
                Theme.chat_audioTimePaint.setColor(Theme.getColor("chat_inAudioDurationText"));
                localObject4 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0));
                for (localObject2 = "chat_inAudioSelectedProgress"; ; localObject2 = "chat_inAudioProgress")
                {
                  ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject2));
                  break;
                }
                paramCanvas.translate(this.timeAudioX + this.performerX, AndroidUtilities.dp(35.0F) + this.namesOffset + this.mediaOffsetY);
                this.performerLayout.draw(paramCanvas);
                break label3872;
                localObject2 = Theme.chat_msgOutMenuDrawable;
                break label3941;
                if (isDrawSelectedBackground())
                {
                  localObject2 = Theme.chat_msgInMenuSelectedDrawable;
                  break label3941;
                }
                localObject2 = Theme.chat_msgInMenuDrawable;
                break label3941;
                f1 = 48.0F;
              }
            }
            if (this.documentAttachType != 3)
              break label2763;
            if (this.currentMessageObject.isOutOwner())
            {
              localObject4 = Theme.chat_audioTimePaint;
              if (isDrawSelectedBackground())
              {
                localObject2 = "chat_outAudioDurationSelectedText";
                ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
                localObject4 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0))
                  break label4494;
                localObject2 = "chat_outAudioSelectedProgress";
                ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject2));
                this.radialProgress.draw(paramCanvas);
                paramCanvas.save();
                if (!this.useSeekBarWaweform)
                  break label4583;
                paramCanvas.translate(this.seekBarX + AndroidUtilities.dp(13.0F), this.seekBarY);
                this.seekBarWaveform.draw(paramCanvas);
                paramCanvas.restore();
                paramCanvas.save();
                paramCanvas.translate(this.timeAudioX, AndroidUtilities.dp(44.0F) + this.namesOffset + this.mediaOffsetY);
                this.durationLayout.draw(paramCanvas);
                paramCanvas.restore();
                if ((this.currentMessageObject.type == 0) || (this.currentMessageObject.messageOwner.to_id.channel_id != 0) || (!this.currentMessageObject.isContentUnread()))
                  break label2763;
                localObject4 = Theme.chat_docBackPaint;
                if (!this.currentMessageObject.isOutOwner())
                  break label4608;
              }
            }
            for (localObject2 = "chat_outVoiceSeekbarFill"; ; localObject2 = "chat_inVoiceSeekbarFill")
            {
              ((Paint)localObject4).setColor(Theme.getColor((String)localObject2));
              paramCanvas.drawCircle(this.timeAudioX + this.timeWidthAudio + AndroidUtilities.dp(6.0F), AndroidUtilities.dp(51.0F) + this.namesOffset + this.mediaOffsetY, AndroidUtilities.dp(3.0F), Theme.chat_docBackPaint);
              break;
              localObject2 = "chat_outAudioDurationText";
              break label4228;
              localObject2 = "chat_outAudioProgress";
              break label4263;
              localObject4 = Theme.chat_audioTimePaint;
              if (isDrawSelectedBackground())
              {
                localObject2 = "chat_inAudioDurationSelectedText";
                ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
                localObject4 = this.radialProgress;
                if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0))
                  break label4575;
              }
              for (localObject2 = "chat_inAudioSelectedProgress"; ; localObject2 = "chat_inAudioProgress")
              {
                ((RadialProgress)localObject4).setProgressColor(Theme.getColor((String)localObject2));
                break;
                localObject2 = "chat_inAudioDurationText";
                break label4519;
              }
              paramCanvas.translate(this.seekBarX, this.seekBarY);
              this.seekBar.draw(paramCanvas);
              break label4322;
            }
            if (this.currentMessageObject.type != 4)
              break label4891;
          }
          while (this.docTitleLayout == null);
          if (this.currentMessageObject.isOutOwner())
          {
            Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_outVenueNameText"));
            localObject4 = Theme.chat_locationAddressPaint;
            if (isDrawSelectedBackground());
            for (localObject2 = "chat_outVenueInfoSelectedText"; ; localObject2 = "chat_outVenueInfoText")
            {
              ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
              paramCanvas.save();
              paramCanvas.translate(this.docTitleOffsetX + this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + AndroidUtilities.dp(8.0F));
              this.docTitleLayout.draw(paramCanvas);
              paramCanvas.restore();
              if (this.infoLayout == null)
                break;
              paramCanvas.save();
              paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F), this.photoImage.getImageY() + this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1) + AndroidUtilities.dp(13.0F));
              this.infoLayout.draw(paramCanvas);
              paramCanvas.restore();
              break;
            }
          }
          Theme.chat_locationTitlePaint.setColor(Theme.getColor("chat_inVenueNameText"));
          localObject4 = Theme.chat_locationAddressPaint;
          if (isDrawSelectedBackground());
          for (localObject2 = "chat_inVenueInfoSelectedText"; ; localObject2 = "chat_inVenueInfoText")
          {
            ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
            break;
          }
          if (this.currentMessageObject.type != 16)
            continue;
          if (this.currentMessageObject.isOutOwner())
          {
            Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextOut"));
            localObject4 = Theme.chat_contactPhonePaint;
            if (isDrawSelectedBackground())
            {
              localObject2 = "chat_outTimeSelectedText";
              ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
              this.forceNotDrawTime = true;
              if (!this.currentMessageObject.isOutOwner())
                break label5252;
              k = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(16.0F);
              this.otherX = k;
              if (this.titleLayout != null)
              {
                paramCanvas.save();
                paramCanvas.translate(k, AndroidUtilities.dp(12.0F) + this.namesOffset);
                this.titleLayout.draw(paramCanvas);
                paramCanvas.restore();
              }
              if (this.docTitleLayout != null)
              {
                paramCanvas.save();
                paramCanvas.translate(AndroidUtilities.dp(19.0F) + k, AndroidUtilities.dp(37.0F) + this.namesOffset);
                this.docTitleLayout.draw(paramCanvas);
                paramCanvas.restore();
              }
              if (!this.currentMessageObject.isOutOwner())
                break label5299;
              localObject5 = Theme.chat_msgCallUpGreenDrawable;
              if ((!isDrawSelectedBackground()) && (!this.otherPressed))
                break label5291;
            }
          }
          for (localObject2 = Theme.chat_msgOutCallSelectedDrawable; ; localObject2 = Theme.chat_msgOutCallDrawable)
          {
            setDrawableBounds((Drawable)localObject5, k - AndroidUtilities.dp(3.0F), AndroidUtilities.dp(36.0F) + this.namesOffset);
            ((Drawable)localObject5).draw(paramCanvas);
            i7 = AndroidUtilities.dp(205.0F);
            i16 = AndroidUtilities.dp(22.0F);
            this.otherY = i16;
            setDrawableBounds((Drawable)localObject2, k + i7, i16);
            ((Drawable)localObject2).draw(paramCanvas);
            break;
            localObject2 = "chat_outTimeText";
            break label4942;
            Theme.chat_audioTitlePaint.setColor(Theme.getColor("chat_messageTextIn"));
            localObject4 = Theme.chat_contactPhonePaint;
            if (isDrawSelectedBackground());
            for (localObject2 = "chat_inTimeSelectedText"; ; localObject2 = "chat_inTimeText")
            {
              ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
              break;
            }
            if ((this.isChat) && (this.currentMessageObject.isFromUser()))
            {
              m = AndroidUtilities.dp(74.0F);
              break label4985;
            }
            m = AndroidUtilities.dp(25.0F);
            break label4985;
          }
          localObject2 = this.currentMessageObject.messageOwner.action.reason;
          if (((localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) || ((localObject2 instanceof TLRPC.TL_phoneCallDiscardReasonBusy)))
          {
            localObject2 = Theme.chat_msgCallDownRedDrawable;
            if ((!isDrawSelectedBackground()) && (!this.otherPressed))
              break label5373;
          }
          for (localObject4 = Theme.chat_msgInCallSelectedDrawable; ; localObject4 = Theme.chat_msgInCallDrawable)
          {
            localObject5 = localObject2;
            localObject2 = localObject4;
            break;
            localObject2 = Theme.chat_msgCallDownGreenDrawable;
            break label5335;
          }
        }
        while (this.currentMessageObject.type != 12);
        label3591: label3766: label4155: label4163: label4186: label4322: localObject4 = Theme.chat_contactNamePaint;
        label3872: label4263: label4519: label4575: if (this.currentMessageObject.isOutOwner())
        {
          localObject2 = "chat_outContactNameText";
          ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
          localObject4 = Theme.chat_contactPhonePaint;
          if (!this.currentMessageObject.isOutOwner())
            break label5677;
          localObject2 = "chat_outContactPhoneText";
          ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
          if (this.titleLayout != null)
          {
            paramCanvas.save();
            paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(16.0F) + this.namesOffset);
            this.titleLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          if (this.docTitleLayout != null)
          {
            paramCanvas.save();
            paramCanvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0F), AndroidUtilities.dp(39.0F) + this.namesOffset);
            this.docTitleLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          if (!this.currentMessageObject.isOutOwner())
            break label5693;
          if (!isDrawSelectedBackground())
            break label5685;
          localObject2 = Theme.chat_msgOutMenuSelectedDrawable;
        }
        label4113: label4891: label4942: label5335: int i8;
        while (true)
        {
          label4228: label4494: label5291: label5299: label5443: m = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(48.0F);
          label4616: label5413: this.otherX = m;
          label5252: i8 = this.photoImage.getImageY() - AndroidUtilities.dp(5.0F);
          this.otherY = i8;
          setDrawableBounds((Drawable)localObject2, m, i8);
          ((Drawable)localObject2).draw(paramCanvas);
          break;
          localObject2 = "chat_inContactNameText";
          break label5413;
          label5677: localObject2 = "chat_inContactPhoneText";
          break label5443;
          label5685: localObject2 = Theme.chat_msgOutMenuDrawable;
          continue;
          label5693: if (isDrawSelectedBackground())
          {
            localObject2 = Theme.chat_msgInMenuSelectedDrawable;
            continue;
          }
          localObject2 = Theme.chat_msgInMenuDrawable;
        }
        label5716: int m = this.backgroundDrawableLeft;
        label5736: float f2;
        if (this.currentMessageObject.isOutOwner())
        {
          f1 = 11.0F;
          m = AndroidUtilities.dp(f1) + m;
          this.captionX = m;
          f2 = m;
          m = this.totalHeight;
          i8 = this.captionHeight;
          if (!this.pinnedTop)
            break label5814;
          f1 = 9.0F;
        }
        while (true)
        {
          m = m - i8 - AndroidUtilities.dp(f1);
          this.captionY = m;
          paramCanvas.translate(f2, m);
          break;
          f1 = 17.0F;
          break label5736;
          label5814: f1 = 10.0F;
        }
        try
        {
          label5821: this.captionLayout.draw(paramCanvas);
          paramCanvas.restore();
          label5833: if (this.documentAttachType == 1)
          {
            if (!this.currentMessageObject.isOutOwner())
              break label6761;
            Theme.chat_docNamePaint.setColor(Theme.getColor("chat_outFileNameText"));
            localObject4 = Theme.chat_infoPaint;
            if (!isDrawSelectedBackground())
              break label6737;
            localObject2 = "chat_outFileInfoSelectedText";
            ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject2));
            localObject4 = Theme.chat_docBackPaint;
            if (!isDrawSelectedBackground())
              break label6745;
            localObject2 = "chat_outFileBackgroundSelected";
            ((Paint)localObject4).setColor(Theme.getColor((String)localObject2));
            if (!isDrawSelectedBackground())
              break label6753;
            localObject2 = Theme.chat_msgOutMenuSelectedDrawable;
            if (!this.drawPhotoImage)
              break label7106;
            if (this.currentMessageObject.type != 0)
              break label6866;
            n = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(56.0F);
            this.otherX = n;
            i9 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
            this.otherY = i9;
            setDrawableBounds((Drawable)localObject2, n, i9);
            i9 = this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(10.0F);
            i16 = this.photoImage.getImageY() + AndroidUtilities.dp(8.0F);
            n = this.photoImage.getImageY();
            i20 = this.docTitleLayout.getLineBottom(this.docTitleLayout.getLineCount() - 1);
            i21 = AndroidUtilities.dp(13.0F) + (n + i20);
            if ((this.buttonState >= 0) && (this.buttonState < 4))
            {
              if (bool2)
                break label6971;
              n = this.buttonState;
              if (this.buttonState != 0)
                break label6933;
              if (!this.currentMessageObject.isOutOwner())
                break label6926;
              n = 7;
              localObject4 = this.radialProgress;
              localObject5 = Theme.chat_photoStatesDrawables[n];
              if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0))
                break label6965;
              n = 1;
              ((RadialProgress)localObject4).swapBackground(localObject5[n]);
            }
            if (bool2)
              break label7058;
            this.rect.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX() + this.photoImage.getImageWidth(), this.photoImage.getImageY() + this.photoImage.getImageHeight());
            paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(3.0F), AndroidUtilities.dp(3.0F), Theme.chat_docBackPaint);
            if (!this.currentMessageObject.isOutOwner())
              break label7003;
            localObject5 = this.radialProgress;
            if (!isDrawSelectedBackground())
              break label6995;
            localObject4 = "chat_outFileProgressSelected";
            ((RadialProgress)localObject5).setProgressColor(Theme.getColor((String)localObject4));
            n = i21;
            i21 = i16;
            i16 = i9;
            i9 = i21;
            ((Drawable)localObject2).draw(paramCanvas);
          }
        }
        catch (Exception localPorterDuffColorFilter)
        {
          try
          {
            int i9;
            if (this.docTitleLayout != null)
            {
              paramCanvas.save();
              paramCanvas.translate(this.docTitleOffsetX + i16, i9);
              this.docTitleLayout.draw(paramCanvas);
              paramCanvas.restore();
            }
          }
          catch (Exception localPorterDuffColorFilter)
          {
            try
            {
              while (true)
              {
                int n;
                if (this.infoLayout != null)
                {
                  paramCanvas.save();
                  paramCanvas.translate(i16, n);
                  this.infoLayout.draw(paramCanvas);
                  paramCanvas.restore();
                }
                if ((this.drawImageButton) && (this.photoImage.getVisible()))
                  this.radialProgress.draw(paramCanvas);
                if (this.botButtons.isEmpty())
                  break label8010;
                if (!this.currentMessageObject.isOutOwner())
                  break;
                i1 = getMeasuredWidth() - this.widthForButtons - AndroidUtilities.dp(10.0F);
                int i10 = 0;
                while (true)
                {
                  if (i10 >= this.botButtons.size())
                    break label8010;
                  localObject4 = (BotButton)this.botButtons.get(i10);
                  i21 = ((BotButton)localObject4).y + this.layoutHeight - AndroidUtilities.dp(2.0F);
                  localObject5 = Theme.chat_systemDrawable;
                  if (i10 != this.pressedBotButton)
                    break;
                  localObject2 = Theme.colorPressedFilter;
                  ((Drawable)localObject5).setColorFilter((ColorFilter)localObject2);
                  Theme.chat_systemDrawable.setBounds(((BotButton)localObject4).x + i1, i21, ((BotButton)localObject4).x + i1 + ((BotButton)localObject4).width, ((BotButton)localObject4).height + i21);
                  Theme.chat_systemDrawable.draw(paramCanvas);
                  paramCanvas.save();
                  paramCanvas.translate(((BotButton)localObject4).x + i1 + AndroidUtilities.dp(5.0F), (AndroidUtilities.dp(44.0F) - ((BotButton)localObject4).title.getLineBottom(((BotButton)localObject4).title.getLineCount() - 1)) / 2 + i21);
                  ((BotButton)localObject4).title.draw(paramCanvas);
                  paramCanvas.restore();
                  if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonUrl))
                    break label7417;
                  i17 = ((BotButton)localObject4).x;
                  i24 = ((BotButton)localObject4).width;
                  i27 = AndroidUtilities.dp(3.0F);
                  i28 = Theme.chat_botLinkDrawalbe.getIntrinsicWidth();
                  setDrawableBounds(Theme.chat_botLinkDrawalbe, i17 + i24 - i27 - i28 + i1, i21 + AndroidUtilities.dp(3.0F));
                  Theme.chat_botLinkDrawalbe.draw(paramCanvas);
                  i10 += 1;
                }
                localException2 = localException2;
                FileLog.e(localException2);
                continue;
                label6737: Object localObject3 = "chat_outFileInfoText";
                continue;
                label6745: localObject3 = "chat_outFileBackground";
                continue;
                label6753: localObject3 = Theme.chat_msgOutMenuDrawable;
                continue;
                label6761: Theme.chat_docNamePaint.setColor(Theme.getColor("chat_inFileNameText"));
                localObject4 = Theme.chat_infoPaint;
                if (isDrawSelectedBackground())
                {
                  localObject3 = "chat_inFileInfoSelectedText";
                  label6790: ((TextPaint)localObject4).setColor(Theme.getColor((String)localObject3));
                  localObject4 = Theme.chat_docBackPaint;
                  if (!isDrawSelectedBackground())
                    break label6850;
                }
                label6850: for (localObject3 = "chat_inFileBackgroundSelected"; ; localObject3 = "chat_inFileBackground")
                {
                  ((Paint)localObject4).setColor(Theme.getColor((String)localObject3));
                  if (!isDrawSelectedBackground())
                    break label6858;
                  localObject3 = Theme.chat_msgInMenuSelectedDrawable;
                  break;
                  localObject3 = "chat_inFileInfoText";
                  break label6790;
                }
                label6858: localObject3 = Theme.chat_msgInMenuDrawable;
                continue;
                label6866: i1 = this.photoImage.getImageX() + this.backgroundWidth - AndroidUtilities.dp(40.0F);
                this.otherX = i1;
                i10 = this.photoImage.getImageY() + AndroidUtilities.dp(1.0F);
                this.otherY = i10;
                setDrawableBounds((Drawable)localObject3, i1, i10);
                continue;
                label6926: i1 = 10;
                continue;
                label6933: if (this.buttonState != 1)
                  continue;
                if (this.currentMessageObject.isOutOwner())
                {
                  i1 = 8;
                  continue;
                }
                i1 = 11;
                continue;
                label6965: i1 = 0;
                continue;
                label6971: this.radialProgress.swapBackground(Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed]);
                continue;
                label6995: localObject4 = "chat_outFileProgress";
                continue;
                label7003: localObject5 = this.radialProgress;
                if (isDrawSelectedBackground());
                for (localObject4 = "chat_inFileProgressSelected"; ; localObject4 = "chat_inFileProgress")
                {
                  ((RadialProgress)localObject5).setProgressColor(Theme.getColor((String)localObject4));
                  i1 = i21;
                  i21 = i10;
                  i10 = i17;
                  i17 = i21;
                  break;
                }
                label7058: if (this.buttonState == -1)
                  this.radialProgress.setHideCurrentDrawable(true);
                this.radialProgress.setProgressColor(Theme.getColor("chat_mediaProgress"));
                i1 = i21;
                i21 = i10;
                i10 = i17;
                i17 = i21;
                continue;
                label7106: i1 = this.buttonX;
                i10 = this.backgroundWidth;
                if (this.currentMessageObject.type == 0)
                {
                  f1 = 58.0F;
                  label7132: i1 = i10 + i1 - AndroidUtilities.dp(f1);
                  this.otherX = i1;
                  i10 = this.buttonY - AndroidUtilities.dp(5.0F);
                  this.otherY = i10;
                  setDrawableBounds((Drawable)localObject3, i1, i10);
                  i1 = this.buttonX + AndroidUtilities.dp(53.0F);
                  i10 = this.buttonY + AndroidUtilities.dp(4.0F);
                  i17 = this.buttonY;
                  i17 = AndroidUtilities.dp(27.0F) + i17;
                  if (!this.currentMessageObject.isOutOwner())
                    break label7297;
                  localObject5 = this.radialProgress;
                  if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0))
                    break label7289;
                }
                label7289: for (localObject4 = "chat_outAudioSelectedProgress"; ; localObject4 = "chat_outAudioProgress")
                {
                  ((RadialProgress)localObject5).setProgressColor(Theme.getColor((String)localObject4));
                  i21 = i1;
                  i1 = i17;
                  i17 = i21;
                  break;
                  f1 = 48.0F;
                  break label7132;
                }
                localObject5 = this.radialProgress;
                if ((isDrawSelectedBackground()) || (this.buttonPressed != 0));
                for (localObject4 = "chat_inAudioSelectedProgress"; ; localObject4 = "chat_inAudioProgress")
                {
                  ((RadialProgress)localObject5).setProgressColor(Theme.getColor((String)localObject4));
                  i21 = i1;
                  i1 = i17;
                  i17 = i21;
                  break;
                }
                localException3 = localException3;
                FileLog.e(localException3);
              }
            }
            catch (Exception localPorterDuffColorFilter)
            {
              label7417: 
              do
              {
                do
                  while (true)
                  {
                    FileLog.e(localException4);
                    continue;
                    i1 = this.backgroundDrawableLeft;
                    if (this.mediaBackground)
                      f1 = 1.0F;
                    while (true)
                    {
                      i1 = AndroidUtilities.dp(f1) + i1;
                      break;
                      f1 = 7.0F;
                    }
                    PorterDuffColorFilter localPorterDuffColorFilter = Theme.colorFilter;
                    continue;
                    if (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonSwitchInline))
                      break;
                    i17 = ((BotButton)localObject4).x;
                    i24 = ((BotButton)localObject4).width;
                    int i27 = AndroidUtilities.dp(3.0F);
                    int i28 = Theme.chat_botInlineDrawable.getIntrinsicWidth();
                    setDrawableBounds(Theme.chat_botInlineDrawable, i17 + i24 - i27 - i28 + i1, i21 + AndroidUtilities.dp(3.0F));
                    Theme.chat_botInlineDrawable.draw(paramCanvas);
                  }
                while ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame)) && (!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonBuy)));
                if ((((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonCallback)) || ((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonGame)) || ((((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonBuy))) && ((!SendMessagesHelper.getInstance().isSendingCallback(this.currentMessageObject, ((BotButton)localObject4).button)) && ((!(((BotButton)localObject4).button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation)) || (!SendMessagesHelper.getInstance().isSendingCurrentLocation(this.currentMessageObject, ((BotButton)localObject4).button)))))
                  break;
                i17 = 1;
              }
              while ((i17 == 0) && ((i17 != 0) || (((BotButton)localObject4).progressAlpha == 0.0F)));
              label7625: Theme.chat_botProgressPaint.setAlpha(Math.min(255, (int)(((BotButton)localObject4).progressAlpha * 255.0F)));
              i24 = ((BotButton)localObject4).x + ((BotButton)localObject4).width - AndroidUtilities.dp(12.0F) + i1;
              this.rect.set(i24, AndroidUtilities.dp(4.0F) + i21, i24 + AndroidUtilities.dp(8.0F), i21 + AndroidUtilities.dp(12.0F));
              paramCanvas.drawArc(this.rect, ((BotButton)localObject4).angle, 220.0F, false, Theme.chat_botProgressPaint);
              invalidate((int)this.rect.left - AndroidUtilities.dp(2.0F), (int)this.rect.top - AndroidUtilities.dp(2.0F), (int)this.rect.right + AndroidUtilities.dp(2.0F), (int)this.rect.bottom + AndroidUtilities.dp(2.0F));
              l1 = System.currentTimeMillis();
              if (Math.abs(((BotButton)localObject4).lastUpdateTime - System.currentTimeMillis()) < 1000L)
              {
                l2 = l1 - ((BotButton)localObject4).lastUpdateTime;
                BotButton.access$702((BotButton)localObject4, (int)((float)(360L * l2) / 2000.0F + ((BotButton)localObject4).angle));
                BotButton.access$702((BotButton)localObject4, ((BotButton)localObject4).angle - ((BotButton)localObject4).angle / 360 * 360);
                if (i17 == 0)
                  break label7961;
                if (((BotButton)localObject4).progressAlpha < 1.0F)
                {
                  f1 = ((BotButton)localObject4).progressAlpha;
                  BotButton.access$602((BotButton)localObject4, (float)l2 / 200.0F + f1);
                  if (((BotButton)localObject4).progressAlpha > 1.0F)
                    BotButton.access$602((BotButton)localObject4, 1.0F);
                }
              }
              while (true)
              {
                BotButton.access$802((BotButton)localObject4, l1);
                break;
                i17 = 0;
                break label7625;
                label7961: if (((BotButton)localObject4).progressAlpha <= 0.0F)
                  continue;
                BotButton.access$602((BotButton)localObject4, ((BotButton)localObject4).progressAlpha - (float)l2 / 200.0F);
                if (((BotButton)localObject4).progressAlpha >= 0.0F)
                  continue;
                BotButton.access$602((BotButton)localObject4, 0.0F);
              }
            }
          }
        }
        label8010: return;
        label8011: bool2 = false;
      }
      break label1892;
      label8020: int i1 = i17;
      break label1136;
      label8027: int i21 = i24;
      break label1122;
      label8034: i1 = i24;
      i21 = i17;
      break label1800;
      label8045: i1 = 0;
      i21 = i17;
      break label979;
      label8055: bool1 = false;
      break label838;
      label8061: i21 = i1;
      break;
    }
  }

  public static StaticLayout generateStaticLayout(CharSequence paramCharSequence, TextPaint paramTextPaint, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder(paramCharSequence);
    StaticLayout localStaticLayout = new StaticLayout(paramCharSequence, paramTextPaint, paramInt2, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    int j = 0;
    int k = 0;
    int i = paramInt1;
    if (j < paramInt3)
    {
      localStaticLayout.getLineDirections(j);
      if ((localStaticLayout.getLineLeft(j) != 0.0F) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineStart(j))) || (localStaticLayout.isRtlCharAt(localStaticLayout.getLineEnd(j))))
        paramInt1 = paramInt2;
      i = localStaticLayout.getLineEnd(j);
      if (i != paramCharSequence.length());
    }
    while (true)
    {
      return StaticLayoutEx.createStaticLayout(localSpannableStringBuilder, paramTextPaint, paramInt1, Layout.Alignment.ALIGN_NORMAL, 1.0F, AndroidUtilities.dp(1.0F), false, TextUtils.TruncateAt.END, paramInt1, paramInt4);
      i -= 1;
      int m;
      if (localSpannableStringBuilder.charAt(i + k) == ' ')
      {
        localSpannableStringBuilder.replace(i + k, i + k + 1, "\n");
        m = k;
      }
      while (true)
      {
        i = paramInt1;
        if (j == localStaticLayout.getLineCount() - 1)
          break label268;
        if (j != paramInt4 - 1)
          break label255;
        break;
        m = k;
        if (localSpannableStringBuilder.charAt(i + k) == '\n')
          continue;
        localSpannableStringBuilder.insert(i + k, "\n");
        m = k + 1;
      }
      label255: j += 1;
      k = m;
      break;
      label268: paramInt1 = i;
    }
  }

  private Drawable getDrawableForCurrentState()
  {
    int i = 3;
    int j = 0;
    int k = 1;
    int m = 1;
    int n = 1;
    int i1 = 1;
    Object localObject;
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState == -1)
        return null;
      this.radialProgress.setAlphaForPrevious(false);
      localObject = Theme.chat_fileStatesDrawable;
      if (this.currentMessageObject.isOutOwner())
      {
        i = this.buttonState;
        localObject = localObject[i];
        if ((!isDrawSelectedBackground()) && (this.buttonPressed == 0))
          break label106;
      }
      label106: for (i = 1; ; i = 0)
      {
        return localObject[i];
        i = this.buttonState + 5;
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      this.radialProgress.setAlphaForPrevious(false);
      if (this.buttonState == -1)
      {
        localObject = Theme.chat_fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          localObject = localObject[i];
          if (!isDrawSelectedBackground())
            break label184;
        }
        label184: for (i = i1; ; i = 0)
        {
          return localObject[i];
          i = 8;
          break;
        }
      }
      if (this.buttonState == 0)
      {
        localObject = Theme.chat_fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 2;
          localObject = localObject[i];
          if (!isDrawSelectedBackground())
            break label239;
        }
        label239: for (i = k; ; i = 0)
        {
          return localObject[i];
          i = 7;
          break;
        }
      }
      if (this.buttonState == 1)
      {
        localObject = Theme.chat_fileStatesDrawable;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 4;
          localObject = localObject[i];
          if (!isDrawSelectedBackground())
            break label296;
        }
        label296: for (i = m; ; i = 0)
        {
          return localObject[i];
          i = 9;
          break;
        }
      }
    }
    else
    {
      this.radialProgress.setAlphaForPrevious(true);
      if ((this.buttonState >= 0) && (this.buttonState < 4))
      {
        if (this.documentAttachType == 1)
        {
          i = this.buttonState;
          if (this.buttonState == 0)
            if (this.currentMessageObject.isOutOwner())
              i = 7;
          while (true)
          {
            localObject = Theme.chat_photoStatesDrawables[i];
            if (!isDrawSelectedBackground())
            {
              i = j;
              if (this.buttonPressed == 0);
            }
            else
            {
              i = 1;
            }
            return localObject[i];
            i = 10;
            continue;
            if (this.buttonState != 1)
              continue;
            if (this.currentMessageObject.isOutOwner())
            {
              i = 8;
              continue;
            }
            i = 11;
          }
        }
        return Theme.chat_photoStatesDrawables[this.buttonState][this.buttonPressed];
      }
      if ((this.buttonState == -1) && (this.documentAttachType == 1))
      {
        localObject = Theme.chat_photoStatesDrawables;
        if (this.currentMessageObject.isOutOwner())
        {
          i = 9;
          localObject = localObject[i];
          if (!isDrawSelectedBackground())
            break label498;
        }
        label498: for (i = n; ; i = 0)
        {
          return localObject[i];
          i = 12;
          break;
        }
      }
    }
    return (Drawable)null;
  }

  private int getMaxNameWidth()
  {
    if (this.documentAttachType == 6)
    {
      if (AndroidUtilities.isTablet())
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser()))
          i = AndroidUtilities.getMinTabletSide() - AndroidUtilities.dp(42.0F);
      while (true)
      {
        return i - this.backgroundWidth - AndroidUtilities.dp(57.0F);
        i = AndroidUtilities.getMinTabletSide();
        continue;
        if ((this.isChat) && (!this.currentMessageObject.isOutOwner()) && (this.currentMessageObject.isFromUser()))
        {
          i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y) - AndroidUtilities.dp(42.0F);
          continue;
        }
        i = Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y);
      }
    }
    int i = this.backgroundWidth;
    float f;
    if (this.mediaBackground)
      f = 22.0F;
    while (true)
    {
      return i - AndroidUtilities.dp(f);
      f = 31.0F;
    }
  }

  private boolean intersect(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    if (paramFloat1 <= paramFloat3)
      if (paramFloat2 < paramFloat3);
    do
    {
      return true;
      return false;
    }
    while (paramFloat1 <= paramFloat4);
    return false;
  }

  private boolean isDrawSelectedBackground()
  {
    return ((isPressed()) && (this.isCheckPressed)) || ((!this.isCheckPressed) && (this.isPressed)) || (this.isHighlighted);
  }

  private boolean isPhotoDataChanged(MessageObject paramMessageObject)
  {
    int j = 1;
    int i;
    if ((paramMessageObject.type == 0) || (paramMessageObject.type == 14))
      i = 0;
    double d1;
    double d2;
    do
    {
      do
      {
        return i;
        if (paramMessageObject.type != 4)
          break;
        i = j;
      }
      while (this.currentUrl == null);
      d1 = paramMessageObject.messageOwner.media.geo.lat;
      d2 = paramMessageObject.messageOwner.media.geo._long;
      i = j;
    }
    while (!String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=100x100&maptype=roadmap&scale=%d&markers=color:red|size:mid|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) }).equals(this.currentUrl));
    do
    {
      return false;
      i = j;
      if (this.currentPhotoObject == null)
        break;
      i = j;
      if ((this.currentPhotoObject.location instanceof TLRPC.TL_fileLocationUnavailable))
        break;
    }
    while ((this.currentMessageObject == null) || (!this.photoNotSet) || (!FileLoader.getPathToMessage(this.currentMessageObject.messageOwner).exists()));
    return true;
  }

  private boolean isUserDataChanged()
  {
    Object localObject3 = null;
    if ((this.currentMessageObject != null) && (!this.hasLinkPreview) && (this.currentMessageObject.messageOwner.media != null) && ((this.currentMessageObject.messageOwner.media.webpage instanceof TLRPC.TL_webPage)));
    do
    {
      return true;
      if ((this.currentMessageObject == null) || ((this.currentUser == null) && (this.currentChat == null)))
        return false;
    }
    while ((this.lastSendState != this.currentMessageObject.messageOwner.send_state) || (this.lastDeleteDate != this.currentMessageObject.messageOwner.destroyTime) || (this.lastViewsCount != this.currentMessageObject.messageOwner.views));
    TLRPC.User localUser;
    Object localObject1;
    if (this.currentMessageObject.isFromUser())
    {
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(this.currentMessageObject.messageOwner.from_id));
      localObject1 = null;
    }
    while (true)
    {
      label157: Object localObject2;
      if (this.isAvatarVisible)
        if ((localUser != null) && (localUser.photo != null))
          localObject2 = localUser.photo.photo_small;
      while (true)
      {
        label184: if (((this.replyTextLayout == null) && (this.currentMessageObject.replyMessageObject != null)) || ((this.currentPhoto == null) && (localObject2 != null)) || ((this.currentPhoto != null) && (localObject2 == null)) || ((this.currentPhoto != null) && (localObject2 != null) && ((this.currentPhoto.local_id != ((TLRPC.FileLocation)localObject2).local_id) || (this.currentPhoto.volume_id != ((TLRPC.FileLocation)localObject2).volume_id))))
          break label628;
        if (this.currentMessageObject.replyMessageObject != null)
        {
          localObject2 = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.replyMessageObject.photoThumbs, 80);
          if ((localObject2 == null) || (this.currentMessageObject.replyMessageObject.type == 13));
        }
        for (localObject2 = ((TLRPC.PhotoSize)localObject2).location; ; localObject2 = null)
        {
          if ((this.currentReplyPhoto == null) && (localObject2 != null))
            break label622;
          localObject2 = localObject3;
          if (this.drawName)
          {
            localObject2 = localObject3;
            if (this.isChat)
            {
              localObject2 = localObject3;
              if (!this.currentMessageObject.isOutOwner())
              {
                if (localUser == null)
                  break label594;
                localObject2 = UserObject.getUserName(localUser);
              }
            }
          }
          label380: if (((this.currentNameString != null) || (localObject2 == null)) && ((this.currentNameString == null) || (localObject2 != null)) && ((this.currentNameString == null) || (localObject2 == null) || (this.currentNameString.equals(localObject2))))
          {
            if (!this.drawForwardedName)
              break label616;
            localObject1 = this.currentMessageObject.getForwardedName();
            if (((this.currentForwardNameString != null) || (localObject1 == null)) && ((this.currentForwardNameString == null) || (localObject1 != null)) && ((this.currentForwardNameString == null) || (localObject1 == null) || (this.currentForwardNameString.equals(localObject1))))
              break label611;
          }
          label594: label611: for (int i = 1; ; i = 0)
          {
            return i;
            if (this.currentMessageObject.messageOwner.from_id < 0)
            {
              localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-this.currentMessageObject.messageOwner.from_id));
              localUser = null;
              break label157;
            }
            if (!this.currentMessageObject.messageOwner.post)
              break label630;
            localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(this.currentMessageObject.messageOwner.to_id.channel_id));
            localUser = null;
            break label157;
            if ((localObject1 == null) || (((TLRPC.Chat)localObject1).photo == null))
              break label624;
            localObject2 = ((TLRPC.Chat)localObject1).photo.photo_small;
            break label184;
            localObject2 = localObject3;
            if (localObject1 == null)
              break label380;
            localObject2 = ((TLRPC.Chat)localObject1).title;
            break label380;
            break;
          }
          label616: return false;
        }
        label622: break;
        label624: localObject2 = null;
      }
      label628: break;
      label630: localObject1 = null;
      localUser = null;
    }
  }

  private void measureTime(MessageObject paramMessageObject)
  {
    int i;
    TLRPC.User localUser;
    int m;
    if ((!paramMessageObject.isOutOwner()) && (paramMessageObject.messageOwner.from_id > 0) && (paramMessageObject.messageOwner.post))
    {
      i = 1;
      localUser = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
      int k = i;
      if (i != 0)
      {
        k = i;
        if (localUser == null)
          m = 0;
      }
      if (!this.currentMessageObject.isFromUser())
        break label519;
    }
    label514: label519: for (Object localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id)); ; localObject = null)
    {
      if ((paramMessageObject.messageOwner.via_bot_id == 0) && (paramMessageObject.messageOwner.via_bot_name == null) && ((localObject == null) || (!((TLRPC.User)localObject).bot)) && ((paramMessageObject.messageOwner.flags & 0x8000) != 0))
      {
        localObject = LocaleController.getString("EditedMessage", 2131165669) + " " + LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
        label191: if (m == 0)
          break label505;
        this.currentTimeString = (", " + (String)localObject);
        label220: i = (int)Math.ceil(Theme.chat_timePaint.measureText(this.currentTimeString));
        this.timeWidth = i;
        this.timeTextWidth = i;
        if ((paramMessageObject.messageOwner.flags & 0x400) != 0)
        {
          this.currentViewsString = String.format("%s", new Object[] { LocaleController.formatShortNumber(Math.max(1, paramMessageObject.messageOwner.views), null) });
          this.viewsTextWidth = (int)Math.ceil(Theme.chat_timePaint.measureText(this.currentViewsString));
          this.timeWidth += this.viewsTextWidth + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(10.0F);
        }
        if (m != 0)
        {
          if (this.availableTimeWidth == 0)
            this.availableTimeWidth = AndroidUtilities.dp(1000.0F);
          paramMessageObject = ContactsController.formatName(localUser.first_name, localUser.last_name).replace('\n', ' ');
          i = this.availableTimeWidth - this.timeWidth;
          m = (int)Math.ceil(Theme.chat_timePaint.measureText(paramMessageObject, 0, paramMessageObject.length()));
          if (m <= i)
            break label514;
          paramMessageObject = TextUtils.ellipsize(paramMessageObject, Theme.chat_timePaint, i, TextUtils.TruncateAt.END);
        }
      }
      while (true)
      {
        this.currentTimeString = (paramMessageObject + this.currentTimeString);
        this.timeTextWidth += i;
        this.timeWidth = (i + this.timeWidth);
        return;
        int j = 0;
        break;
        localObject = LocaleController.getInstance().formatterDay.format(paramMessageObject.messageOwner.date * 1000L);
        break label191;
        label505: this.currentTimeString = ((String)localObject);
        break label220;
        j = m;
      }
    }
  }

  private LinkPath obtainNewUrlPath(boolean paramBoolean)
  {
    LinkPath localLinkPath;
    if (!this.urlPathCache.isEmpty())
    {
      localLinkPath = (LinkPath)this.urlPathCache.get(0);
      this.urlPathCache.remove(0);
    }
    while (paramBoolean)
    {
      this.urlPathSelection.add(localLinkPath);
      return localLinkPath;
      localLinkPath = new LinkPath();
    }
    this.urlPath.add(localLinkPath);
    return localLinkPath;
  }

  private void resetPressedLink(int paramInt)
  {
    if ((this.pressedLink == null) || ((this.pressedLinkType != paramInt) && (paramInt != -1)))
      return;
    resetUrlPaths(false);
    this.pressedLink = null;
    this.pressedLinkType = -1;
    invalidate();
  }

  private void resetUrlPaths(boolean paramBoolean)
  {
    if (paramBoolean)
      if (!this.urlPathSelection.isEmpty());
    do
    {
      return;
      this.urlPathCache.addAll(this.urlPathSelection);
      this.urlPathSelection.clear();
      return;
    }
    while (this.urlPath.isEmpty());
    this.urlPathCache.addAll(this.urlPath);
    this.urlPath.clear();
  }

  // ERROR //
  private void setMessageObjectInternal(MessageObject paramMessageObject)
  {
    // Byte code:
    //   0: aload_1
    //   1: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4: getfield 1973	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   7: sipush 1024
    //   10: iand
    //   11: ifeq +44 -> 55
    //   14: aload_0
    //   15: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   18: invokevirtual 1555	org/vidogram/messenger/MessageObject:isContentUnread	()Z
    //   21: ifeq +1936 -> 1957
    //   24: aload_0
    //   25: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   28: invokevirtual 753	org/vidogram/messenger/MessageObject:isOut	()Z
    //   31: ifne +1926 -> 1957
    //   34: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   37: aload_0
    //   38: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   41: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   44: iconst_0
    //   45: invokevirtual 2050	org/vidogram/messenger/MessagesController:addToViewsQueue	(Lorg/vidogram/tgnet/TLRPC$Message;Z)V
    //   48: aload_0
    //   49: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   52: invokevirtual 2053	org/vidogram/messenger/MessageObject:setContentIsRead	()V
    //   55: aload_0
    //   56: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   59: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   62: ifeq +1930 -> 1992
    //   65: aload_0
    //   66: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   69: aload_0
    //   70: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   73: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   76: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   79: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   82: invokevirtual 780	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   85: putfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   88: aload_0
    //   89: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   92: ifeq +84 -> 176
    //   95: aload_1
    //   96: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   99: ifne +77 -> 176
    //   102: aload_1
    //   103: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   106: ifeq +70 -> 176
    //   109: aload_0
    //   110: iconst_1
    //   111: putfield 1916	org/vidogram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   114: aload_0
    //   115: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   118: ifnull +1964 -> 2082
    //   121: aload_0
    //   122: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   125: getfield 1920	org/vidogram/tgnet/TLRPC$User:photo	Lorg/vidogram/tgnet/TLRPC$UserProfilePhoto;
    //   128: ifnull +1946 -> 2074
    //   131: aload_0
    //   132: aload_0
    //   133: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   136: getfield 1920	org/vidogram/tgnet/TLRPC$User:photo	Lorg/vidogram/tgnet/TLRPC$UserProfilePhoto;
    //   139: getfield 1925	org/vidogram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   142: putfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   145: aload_0
    //   146: getfield 304	org/vidogram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   149: aload_0
    //   150: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   153: invokevirtual 2057	org/vidogram/ui/Components/AvatarDrawable:setInfo	(Lorg/vidogram/tgnet/TLRPC$User;)V
    //   156: aload_0
    //   157: getfield 288	org/vidogram/ui/Cells/ChatMessageCell:avatarImage	Lorg/vidogram/messenger/ImageReceiver;
    //   160: aload_0
    //   161: getfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   164: ldc_w 2059
    //   167: aload_0
    //   168: getfield 304	org/vidogram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   171: aconst_null
    //   172: iconst_0
    //   173: invokevirtual 2062	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   176: aload_0
    //   177: aload_1
    //   178: invokespecial 955	org/vidogram/ui/Cells/ChatMessageCell:measureTime	(Lorg/vidogram/messenger/MessageObject;)V
    //   181: aload_0
    //   182: iconst_0
    //   183: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   186: aconst_null
    //   187: astore 7
    //   189: aconst_null
    //   190: astore 8
    //   192: aload_1
    //   193: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   196: getfield 823	org/vidogram/tgnet/TLRPC$Message:via_bot_id	I
    //   199: ifeq +1961 -> 2160
    //   202: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   205: aload_1
    //   206: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   209: getfield 823	org/vidogram/tgnet/TLRPC$Message:via_bot_id	I
    //   212: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   215: invokevirtual 780	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   218: astore 9
    //   220: aload 8
    //   222: astore 6
    //   224: aload 7
    //   226: astore 5
    //   228: aload 9
    //   230: ifnull +115 -> 345
    //   233: aload 8
    //   235: astore 6
    //   237: aload 7
    //   239: astore 5
    //   241: aload 9
    //   243: getfield 2063	org/vidogram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   246: ifnull +99 -> 345
    //   249: aload 8
    //   251: astore 6
    //   253: aload 7
    //   255: astore 5
    //   257: aload 9
    //   259: getfield 2063	org/vidogram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   262: invokevirtual 813	java/lang/String:length	()I
    //   265: ifle +80 -> 345
    //   268: new 1098	java/lang/StringBuilder
    //   271: dup
    //   272: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   275: ldc_w 2065
    //   278: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   281: aload 9
    //   283: getfield 2063	org/vidogram/tgnet/TLRPC$User:username	Ljava/lang/String;
    //   286: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   289: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   292: astore 5
    //   294: ldc_w 2067
    //   297: iconst_1
    //   298: anewarray 1016	java/lang/Object
    //   301: dup
    //   302: iconst_0
    //   303: aload 5
    //   305: aastore
    //   306: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   309: invokestatic 2071	org/vidogram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   312: astore 6
    //   314: aload_0
    //   315: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   318: aload 6
    //   320: iconst_0
    //   321: aload 6
    //   323: invokeinterface 1800 1 0
    //   328: invokevirtual 2028	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   331: f2d
    //   332: invokestatic 949	java/lang/Math:ceil	(D)D
    //   335: d2i
    //   336: putfield 2073	org/vidogram/ui/Cells/ChatMessageCell:viaWidth	I
    //   339: aload_0
    //   340: aload 9
    //   342: putfield 2075	org/vidogram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   345: aload 6
    //   347: astore 7
    //   349: aload 5
    //   351: astore 6
    //   353: aload 7
    //   355: astore 5
    //   357: aload_0
    //   358: getfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   361: ifeq +1898 -> 2259
    //   364: aload_0
    //   365: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   368: ifeq +1891 -> 2259
    //   371: aload_0
    //   372: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   375: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   378: ifne +1881 -> 2259
    //   381: iconst_1
    //   382: istore_3
    //   383: aload_1
    //   384: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   387: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   390: ifnull +12 -> 402
    //   393: aload_1
    //   394: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   397: bipush 14
    //   399: if_icmpne +1865 -> 2264
    //   402: aload 6
    //   404: ifnull +1860 -> 2264
    //   407: iconst_1
    //   408: istore_2
    //   409: iload_3
    //   410: ifne +7 -> 417
    //   413: iload_2
    //   414: ifeq +2027 -> 2441
    //   417: aload_0
    //   418: iconst_1
    //   419: putfield 2077	org/vidogram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   422: aload_0
    //   423: aload_0
    //   424: invokespecial 2079	org/vidogram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   427: putfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   430: aload_0
    //   431: getfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   434: ifge +13 -> 447
    //   437: aload_0
    //   438: ldc_w 2082
    //   441: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   444: putfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   447: iload_3
    //   448: ifeq +1852 -> 2300
    //   451: aload_0
    //   452: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   455: ifnull +1814 -> 2269
    //   458: aload_0
    //   459: aload_0
    //   460: getfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   463: invokestatic 1951	org/vidogram/messenger/UserObject:getUserName	(Lorg/vidogram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   466: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   469: aload_0
    //   470: getfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   473: bipush 10
    //   475: bipush 32
    //   477: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   480: astore 7
    //   482: getstatic 2085	org/vidogram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   485: astore 8
    //   487: aload_0
    //   488: getfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   491: istore 4
    //   493: iload_2
    //   494: ifeq +1816 -> 2310
    //   497: aload_0
    //   498: getfield 2073	org/vidogram/ui/Cells/ChatMessageCell:viaWidth	I
    //   501: istore_3
    //   502: aload 7
    //   504: aload 8
    //   506: iload 4
    //   508: iload_3
    //   509: isub
    //   510: i2f
    //   511: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   514: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   517: astore 8
    //   519: iload_2
    //   520: ifeq +2448 -> 2968
    //   523: aload_0
    //   524: getstatic 2085	org/vidogram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   527: aload 8
    //   529: iconst_0
    //   530: aload 8
    //   532: invokeinterface 1800 1 0
    //   537: invokevirtual 2028	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   540: f2d
    //   541: invokestatic 949	java/lang/Math:ceil	(D)D
    //   544: d2i
    //   545: putfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   548: aload_0
    //   549: getfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   552: ifeq +18 -> 570
    //   555: aload_0
    //   556: aload_0
    //   557: getfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   560: ldc_w 837
    //   563: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   566: iadd
    //   567: putfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   570: aload_0
    //   571: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   574: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   577: bipush 13
    //   579: if_icmpne +1736 -> 2315
    //   582: ldc_w 2089
    //   585: invokestatic 1246	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   588: istore_2
    //   589: aload_0
    //   590: getfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   593: invokevirtual 813	java/lang/String:length	()I
    //   596: ifle +1751 -> 2347
    //   599: new 1780	android/text/SpannableStringBuilder
    //   602: dup
    //   603: ldc_w 2091
    //   606: iconst_2
    //   607: anewarray 1016	java/lang/Object
    //   610: dup
    //   611: iconst_0
    //   612: aload 8
    //   614: aastore
    //   615: dup
    //   616: iconst_1
    //   617: aload 6
    //   619: aastore
    //   620: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   623: invokespecial 1783	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   626: astore 7
    //   628: aload 7
    //   630: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   633: dup
    //   634: getstatic 2099	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   637: iconst_0
    //   638: iload_2
    //   639: invokespecial 2102	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   642: aload 8
    //   644: invokeinterface 1800 1 0
    //   649: iconst_1
    //   650: iadd
    //   651: aload 8
    //   653: invokeinterface 1800 1 0
    //   658: iconst_4
    //   659: iadd
    //   660: bipush 33
    //   662: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   665: aload 7
    //   667: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   670: dup
    //   671: ldc_w 2108
    //   674: invokestatic 2112	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   677: iconst_0
    //   678: iload_2
    //   679: invokespecial 2102	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   682: aload 8
    //   684: invokeinterface 1800 1 0
    //   689: iconst_5
    //   690: iadd
    //   691: aload 7
    //   693: invokevirtual 2113	android/text/SpannableStringBuilder:length	()I
    //   696: bipush 33
    //   698: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   701: aload 7
    //   703: getstatic 2085	org/vidogram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   706: aload_0
    //   707: getfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   710: i2f
    //   711: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   714: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   717: astore 7
    //   719: aload_0
    //   720: new 277	android/text/StaticLayout
    //   723: dup
    //   724: aload 7
    //   726: getstatic 2085	org/vidogram/ui/ActionBar/Theme:chat_namePaint	Landroid/text/TextPaint;
    //   729: aload_0
    //   730: getfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   733: fconst_2
    //   734: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   737: iadd
    //   738: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   741: fconst_1
    //   742: fconst_0
    //   743: iconst_0
    //   744: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   747: putfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   750: aload_0
    //   751: getfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   754: ifnull +1669 -> 2423
    //   757: aload_0
    //   758: getfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   761: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   764: ifle +1659 -> 2423
    //   767: aload_0
    //   768: aload_0
    //   769: getfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   772: iconst_0
    //   773: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   776: f2d
    //   777: invokestatic 949	java/lang/Math:ceil	(D)D
    //   780: d2i
    //   781: putfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   784: aload_1
    //   785: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   788: bipush 13
    //   790: if_icmpeq +18 -> 808
    //   793: aload_0
    //   794: aload_0
    //   795: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   798: ldc_w 1488
    //   801: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   804: iadd
    //   805: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   808: aload_0
    //   809: aload_0
    //   810: getfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   813: iconst_0
    //   814: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   817: putfield 2117	org/vidogram/ui/Cells/ChatMessageCell:nameOffsetX	F
    //   820: aload_0
    //   821: getfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   824: invokevirtual 813	java/lang/String:length	()I
    //   827: ifne +8 -> 835
    //   830: aload_0
    //   831: aconst_null
    //   832: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   835: aload_0
    //   836: aconst_null
    //   837: putfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   840: aload_0
    //   841: aconst_null
    //   842: putfield 1960	org/vidogram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   845: aload_0
    //   846: aconst_null
    //   847: putfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   850: aload_0
    //   851: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   854: iconst_0
    //   855: aconst_null
    //   856: aastore
    //   857: aload_0
    //   858: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   861: iconst_1
    //   862: aconst_null
    //   863: aastore
    //   864: aload_0
    //   865: iconst_0
    //   866: putfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   869: aload_0
    //   870: getfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   873: ifeq +586 -> 1459
    //   876: aload_1
    //   877: invokevirtual 2126	org/vidogram/messenger/MessageObject:isForwarded	()Z
    //   880: ifeq +579 -> 1459
    //   883: aload_1
    //   884: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   887: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   890: getfield 750	org/vidogram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   893: ifeq +26 -> 919
    //   896: aload_0
    //   897: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   900: aload_1
    //   901: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   904: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   907: getfield 750	org/vidogram/tgnet/TLRPC$TL_messageFwdHeader:channel_id	I
    //   910: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   913: invokevirtual 803	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   916: putfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   919: aload_1
    //   920: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   923: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   926: getfield 2127	org/vidogram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   929: ifeq +26 -> 955
    //   932: aload_0
    //   933: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   936: aload_1
    //   937: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   940: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   943: getfield 2127	org/vidogram/tgnet/TLRPC$TL_messageFwdHeader:from_id	I
    //   946: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   949: invokevirtual 780	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   952: putfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   955: aload_0
    //   956: getfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   959: ifnonnull +10 -> 969
    //   962: aload_0
    //   963: getfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   966: ifnull +493 -> 1459
    //   969: aload_0
    //   970: getfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   973: ifnull +1500 -> 2473
    //   976: aload_0
    //   977: getfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   980: ifnull +1479 -> 2459
    //   983: aload_0
    //   984: ldc_w 2129
    //   987: iconst_2
    //   988: anewarray 1016	java/lang/Object
    //   991: dup
    //   992: iconst_0
    //   993: aload_0
    //   994: getfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   997: getfield 1967	org/vidogram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   1000: aastore
    //   1001: dup
    //   1002: iconst_1
    //   1003: aload_0
    //   1004: getfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   1007: invokestatic 1951	org/vidogram/messenger/UserObject:getUserName	(Lorg/vidogram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   1010: aastore
    //   1011: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1014: putfield 1960	org/vidogram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1017: aload_0
    //   1018: aload_0
    //   1019: invokespecial 2079	org/vidogram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1022: putfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1025: ldc_w 2131
    //   1028: ldc_w 2132
    //   1031: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1034: astore 7
    //   1036: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1039: new 1098	java/lang/StringBuilder
    //   1042: dup
    //   1043: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   1046: aload 7
    //   1048: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1051: ldc_w 1105
    //   1054: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1057: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1060: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1063: f2d
    //   1064: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1067: d2i
    //   1068: istore_2
    //   1069: aload_0
    //   1070: getfield 1960	org/vidogram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   1073: bipush 10
    //   1075: bipush 32
    //   1077: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1080: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1083: aload_0
    //   1084: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1087: iload_2
    //   1088: isub
    //   1089: aload_0
    //   1090: getfield 2073	org/vidogram/ui/Cells/ChatMessageCell:viaWidth	I
    //   1093: isub
    //   1094: i2f
    //   1095: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1098: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1101: astore 8
    //   1103: aload 5
    //   1105: ifnull +1389 -> 2494
    //   1108: new 1780	android/text/SpannableStringBuilder
    //   1111: dup
    //   1112: ldc_w 2137
    //   1115: iconst_3
    //   1116: anewarray 1016	java/lang/Object
    //   1119: dup
    //   1120: iconst_0
    //   1121: aload 7
    //   1123: aastore
    //   1124: dup
    //   1125: iconst_1
    //   1126: aload 8
    //   1128: aastore
    //   1129: dup
    //   1130: iconst_2
    //   1131: aload 6
    //   1133: aastore
    //   1134: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   1137: invokespecial 1783	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   1140: astore 5
    //   1142: aload_0
    //   1143: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1146: new 1098	java/lang/StringBuilder
    //   1149: dup
    //   1150: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   1153: aload 7
    //   1155: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1158: ldc_w 1105
    //   1161: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   1164: aload 8
    //   1166: invokevirtual 2031	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   1169: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   1172: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1175: f2d
    //   1176: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1179: d2i
    //   1180: putfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   1183: aload 5
    //   1185: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   1188: dup
    //   1189: ldc_w 2108
    //   1192: invokestatic 2112	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   1195: invokespecial 2140	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   1198: aload 5
    //   1200: invokevirtual 2113	android/text/SpannableStringBuilder:length	()I
    //   1203: aload 6
    //   1205: invokevirtual 813	java/lang/String:length	()I
    //   1208: isub
    //   1209: iconst_1
    //   1210: isub
    //   1211: aload 5
    //   1213: invokevirtual 2113	android/text/SpannableStringBuilder:length	()I
    //   1216: bipush 33
    //   1218: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   1221: aload 5
    //   1223: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   1226: dup
    //   1227: ldc_w 2108
    //   1230: invokestatic 2112	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   1233: invokespecial 2140	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   1236: aload 7
    //   1238: invokevirtual 813	java/lang/String:length	()I
    //   1241: iconst_1
    //   1242: iadd
    //   1243: aload 7
    //   1245: invokevirtual 813	java/lang/String:length	()I
    //   1248: iconst_1
    //   1249: iadd
    //   1250: aload 8
    //   1252: invokeinterface 1800 1 0
    //   1257: iadd
    //   1258: bipush 33
    //   1260: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   1263: aload 5
    //   1265: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1268: aload_0
    //   1269: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1272: i2f
    //   1273: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1276: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1279: astore 5
    //   1281: aload_0
    //   1282: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1285: iconst_1
    //   1286: new 277	android/text/StaticLayout
    //   1289: dup
    //   1290: aload 5
    //   1292: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1295: aload_0
    //   1296: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1299: fconst_2
    //   1300: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1303: iadd
    //   1304: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1307: fconst_1
    //   1308: fconst_0
    //   1309: iconst_0
    //   1310: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1313: aastore
    //   1314: ldc_w 2142
    //   1317: ldc_w 2143
    //   1320: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1323: invokestatic 2071	org/vidogram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   1326: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1329: aload_0
    //   1330: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1333: i2f
    //   1334: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1337: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1340: astore 5
    //   1342: aload_0
    //   1343: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1346: iconst_0
    //   1347: new 277	android/text/StaticLayout
    //   1350: dup
    //   1351: aload 5
    //   1353: getstatic 2135	org/vidogram/ui/ActionBar/Theme:chat_forwardNamePaint	Landroid/text/TextPaint;
    //   1356: aload_0
    //   1357: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1360: fconst_2
    //   1361: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1364: iadd
    //   1365: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1368: fconst_1
    //   1369: fconst_0
    //   1370: iconst_0
    //   1371: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1374: aastore
    //   1375: aload_0
    //   1376: aload_0
    //   1377: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1380: iconst_0
    //   1381: aaload
    //   1382: iconst_0
    //   1383: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   1386: f2d
    //   1387: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1390: d2i
    //   1391: aload_0
    //   1392: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1395: iconst_1
    //   1396: aaload
    //   1397: iconst_0
    //   1398: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   1401: f2d
    //   1402: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1405: d2i
    //   1406: invokestatic 379	java/lang/Math:max	(II)I
    //   1409: putfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   1412: aload_0
    //   1413: getfield 281	org/vidogram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1416: iconst_0
    //   1417: aload_0
    //   1418: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1421: iconst_0
    //   1422: aaload
    //   1423: iconst_0
    //   1424: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   1427: fastore
    //   1428: aload_0
    //   1429: getfield 281	org/vidogram/ui/Cells/ChatMessageCell:forwardNameOffsetX	[F
    //   1432: iconst_1
    //   1433: aload_0
    //   1434: getfield 279	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameLayout	[Landroid/text/StaticLayout;
    //   1437: iconst_1
    //   1438: aaload
    //   1439: iconst_0
    //   1440: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   1443: fastore
    //   1444: aload_0
    //   1445: aload_0
    //   1446: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1449: ldc_w 433
    //   1452: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1455: iadd
    //   1456: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1459: aload_1
    //   1460: invokevirtual 2146	org/vidogram/messenger/MessageObject:isReply	()Z
    //   1463: ifeq +489 -> 1952
    //   1466: aload_0
    //   1467: aload_0
    //   1468: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1471: ldc_w 1827
    //   1474: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1477: iadd
    //   1478: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1481: aload_1
    //   1482: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   1485: ifeq +27 -> 1512
    //   1488: aload_1
    //   1489: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   1492: bipush 13
    //   1494: if_icmpne +1042 -> 2536
    //   1497: aload_0
    //   1498: aload_0
    //   1499: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1502: ldc_w 1827
    //   1505: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1508: isub
    //   1509: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   1512: aload_0
    //   1513: invokespecial 2079	org/vidogram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   1516: istore_2
    //   1517: aload_1
    //   1518: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   1521: bipush 13
    //   1523: if_icmpeq +1442 -> 2965
    //   1526: iload_2
    //   1527: ldc_w 472
    //   1530: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1533: isub
    //   1534: istore_2
    //   1535: aconst_null
    //   1536: astore 6
    //   1538: aload_1
    //   1539: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1542: ifnull +1414 -> 2956
    //   1545: aload_1
    //   1546: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1549: getfield 2149	org/vidogram/messenger/MessageObject:photoThumbs2	Ljava/util/ArrayList;
    //   1552: bipush 80
    //   1554: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   1557: astore 5
    //   1559: aload 5
    //   1561: ifnonnull +1392 -> 2953
    //   1564: aload_1
    //   1565: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1568: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1571: bipush 80
    //   1573: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   1576: astore 5
    //   1578: aload 5
    //   1580: ifnull +40 -> 1620
    //   1583: aload_1
    //   1584: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1587: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   1590: bipush 13
    //   1592: if_icmpeq +28 -> 1620
    //   1595: aload_1
    //   1596: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   1599: bipush 13
    //   1601: if_icmpne +9 -> 1610
    //   1604: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   1607: ifeq +13 -> 1620
    //   1610: aload_1
    //   1611: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1614: invokevirtual 2152	org/vidogram/messenger/MessageObject:isSecretMedia	()Z
    //   1617: ifeq +937 -> 2554
    //   1620: aload_0
    //   1621: getfield 309	org/vidogram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/vidogram/messenger/ImageReceiver;
    //   1624: aconst_null
    //   1625: checkcast 1253	android/graphics/drawable/Drawable
    //   1628: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   1631: aload_0
    //   1632: iconst_0
    //   1633: putfield 2154	org/vidogram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1636: aconst_null
    //   1637: astore 5
    //   1639: aload_1
    //   1640: getfield 2157	org/vidogram/messenger/MessageObject:customReplyName	Ljava/lang/String;
    //   1643: ifnull +955 -> 2598
    //   1646: aload_1
    //   1647: getfield 2157	org/vidogram/messenger/MessageObject:customReplyName	Ljava/lang/String;
    //   1650: astore 5
    //   1652: aload 5
    //   1654: ifnull +1293 -> 2947
    //   1657: aload 5
    //   1659: bipush 10
    //   1661: bipush 32
    //   1663: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   1666: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1669: iload_2
    //   1670: i2f
    //   1671: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1674: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1677: astore 5
    //   1679: aload_1
    //   1680: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1683: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1686: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1689: instanceof 787
    //   1692: ifeq +1041 -> 2733
    //   1695: aload_1
    //   1696: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1699: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1702: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1705: getfield 2161	org/vidogram/tgnet/TLRPC$MessageMedia:game	Lorg/vidogram/tgnet/TLRPC$TL_game;
    //   1708: getfield 2164	org/vidogram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   1711: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   1714: invokevirtual 2168	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   1717: ldc_w 373
    //   1720: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1723: iconst_0
    //   1724: invokestatic 2174	org/vidogram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   1727: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   1730: iload_2
    //   1731: i2f
    //   1732: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1735: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   1738: astore 6
    //   1740: aload 5
    //   1742: astore_1
    //   1743: aload 6
    //   1745: astore 5
    //   1747: aload_1
    //   1748: astore 6
    //   1750: aload_1
    //   1751: ifnonnull +14 -> 1765
    //   1754: ldc_w 2176
    //   1757: ldc_w 2177
    //   1760: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   1763: astore 6
    //   1765: aload_0
    //   1766: new 277	android/text/StaticLayout
    //   1769: dup
    //   1770: aload 6
    //   1772: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1775: ldc_w 1323
    //   1778: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1781: iload_2
    //   1782: iadd
    //   1783: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1786: fconst_1
    //   1787: fconst_0
    //   1788: iconst_0
    //   1789: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1792: putfield 2179	org/vidogram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1795: aload_0
    //   1796: getfield 2179	org/vidogram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1799: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   1802: ifle +55 -> 1857
    //   1805: aload_0
    //   1806: getfield 2179	org/vidogram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1809: iconst_0
    //   1810: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   1813: f2d
    //   1814: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1817: d2i
    //   1818: istore 4
    //   1820: aload_0
    //   1821: getfield 2154	org/vidogram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1824: ifeq +1087 -> 2911
    //   1827: bipush 44
    //   1829: istore_3
    //   1830: aload_0
    //   1831: iload_3
    //   1832: bipush 12
    //   1834: iadd
    //   1835: i2f
    //   1836: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1839: iload 4
    //   1841: iadd
    //   1842: putfield 2181	org/vidogram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   1845: aload_0
    //   1846: aload_0
    //   1847: getfield 2179	org/vidogram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   1850: iconst_0
    //   1851: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   1854: putfield 2183	org/vidogram/ui/Cells/ChatMessageCell:replyNameOffset	F
    //   1857: aload 5
    //   1859: ifnull +93 -> 1952
    //   1862: aload_0
    //   1863: new 277	android/text/StaticLayout
    //   1866: dup
    //   1867: aload 5
    //   1869: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   1872: iload_2
    //   1873: ldc_w 1323
    //   1876: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1879: iadd
    //   1880: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1883: fconst_1
    //   1884: fconst_0
    //   1885: iconst_0
    //   1886: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1889: putfield 1927	org/vidogram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1892: aload_0
    //   1893: getfield 1927	org/vidogram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1896: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   1899: ifle +53 -> 1952
    //   1902: aload_0
    //   1903: getfield 1927	org/vidogram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1906: iconst_0
    //   1907: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   1910: f2d
    //   1911: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1914: d2i
    //   1915: istore_3
    //   1916: aload_0
    //   1917: getfield 2154	org/vidogram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   1920: ifeq +1004 -> 2924
    //   1923: bipush 44
    //   1925: istore_2
    //   1926: aload_0
    //   1927: iload_2
    //   1928: bipush 12
    //   1930: iadd
    //   1931: i2f
    //   1932: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1935: iload_3
    //   1936: iadd
    //   1937: putfield 2185	org/vidogram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   1940: aload_0
    //   1941: aload_0
    //   1942: getfield 1927	org/vidogram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   1945: iconst_0
    //   1946: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   1949: putfield 2187	org/vidogram/ui/Cells/ChatMessageCell:replyTextOffset	F
    //   1952: aload_0
    //   1953: invokevirtual 2190	org/vidogram/ui/Cells/ChatMessageCell:requestLayout	()V
    //   1956: return
    //   1957: aload_0
    //   1958: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1961: getfield 2193	org/vidogram/messenger/MessageObject:viewsReloaded	Z
    //   1964: ifne -1909 -> 55
    //   1967: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   1970: aload_0
    //   1971: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1974: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1977: iconst_1
    //   1978: invokevirtual 2050	org/vidogram/messenger/MessagesController:addToViewsQueue	(Lorg/vidogram/tgnet/TLRPC$Message;Z)V
    //   1981: aload_0
    //   1982: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1985: iconst_1
    //   1986: putfield 2193	org/vidogram/messenger/MessageObject:viewsReloaded	Z
    //   1989: goto -1934 -> 55
    //   1992: aload_0
    //   1993: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1996: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1999: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2002: ifge +30 -> 2032
    //   2005: aload_0
    //   2006: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2009: aload_0
    //   2010: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2013: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2016: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2019: ineg
    //   2020: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2023: invokevirtual 803	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2026: putfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2029: goto -1941 -> 88
    //   2032: aload_0
    //   2033: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2036: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2039: getfield 820	org/vidogram/tgnet/TLRPC$Message:post	Z
    //   2042: ifeq -1954 -> 88
    //   2045: aload_0
    //   2046: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2049: aload_0
    //   2050: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2053: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2056: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2059: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   2062: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2065: invokevirtual 803	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2068: putfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2071: goto -1983 -> 88
    //   2074: aload_0
    //   2075: aconst_null
    //   2076: putfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2079: goto -1934 -> 145
    //   2082: aload_0
    //   2083: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2086: ifnull +49 -> 2135
    //   2089: aload_0
    //   2090: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2093: getfield 1963	org/vidogram/tgnet/TLRPC$Chat:photo	Lorg/vidogram/tgnet/TLRPC$ChatPhoto;
    //   2096: ifnull +31 -> 2127
    //   2099: aload_0
    //   2100: aload_0
    //   2101: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2104: getfield 1963	org/vidogram/tgnet/TLRPC$Chat:photo	Lorg/vidogram/tgnet/TLRPC$ChatPhoto;
    //   2107: getfield 1966	org/vidogram/tgnet/TLRPC$ChatPhoto:photo_small	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2110: putfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2113: aload_0
    //   2114: getfield 304	org/vidogram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   2117: aload_0
    //   2118: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2121: invokevirtual 2196	org/vidogram/ui/Components/AvatarDrawable:setInfo	(Lorg/vidogram/tgnet/TLRPC$Chat;)V
    //   2124: goto -1968 -> 156
    //   2127: aload_0
    //   2128: aconst_null
    //   2129: putfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2132: goto -19 -> 2113
    //   2135: aload_0
    //   2136: aconst_null
    //   2137: putfield 1932	org/vidogram/ui/Cells/ChatMessageCell:currentPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2140: aload_0
    //   2141: getfield 304	org/vidogram/ui/Cells/ChatMessageCell:avatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   2144: aload_1
    //   2145: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2148: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2151: aconst_null
    //   2152: aconst_null
    //   2153: iconst_0
    //   2154: invokevirtual 2199	org/vidogram/ui/Components/AvatarDrawable:setInfo	(ILjava/lang/String;Ljava/lang/String;Z)V
    //   2157: goto -2001 -> 156
    //   2160: aload_1
    //   2161: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2164: getfield 1970	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2167: ifnull +808 -> 2975
    //   2170: aload_1
    //   2171: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2174: getfield 1970	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2177: invokevirtual 813	java/lang/String:length	()I
    //   2180: ifle +795 -> 2975
    //   2183: new 1098	java/lang/StringBuilder
    //   2186: dup
    //   2187: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   2190: ldc_w 2065
    //   2193: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2196: aload_1
    //   2197: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2200: getfield 1970	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   2203: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2206: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2209: astore 6
    //   2211: ldc_w 2067
    //   2214: iconst_1
    //   2215: anewarray 1016	java/lang/Object
    //   2218: dup
    //   2219: iconst_0
    //   2220: aload 6
    //   2222: aastore
    //   2223: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2226: invokestatic 2071	org/vidogram/messenger/AndroidUtilities:replaceTags	(Ljava/lang/String;)Landroid/text/SpannableStringBuilder;
    //   2229: astore 5
    //   2231: aload_0
    //   2232: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2235: aload 5
    //   2237: iconst_0
    //   2238: aload 5
    //   2240: invokeinterface 1800 1 0
    //   2245: invokevirtual 2028	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   2248: f2d
    //   2249: invokestatic 949	java/lang/Math:ceil	(D)D
    //   2252: d2i
    //   2253: putfield 2073	org/vidogram/ui/Cells/ChatMessageCell:viaWidth	I
    //   2256: goto -1899 -> 357
    //   2259: iconst_0
    //   2260: istore_3
    //   2261: goto -1878 -> 383
    //   2264: iconst_0
    //   2265: istore_2
    //   2266: goto -1857 -> 409
    //   2269: aload_0
    //   2270: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2273: ifnull +17 -> 2290
    //   2276: aload_0
    //   2277: aload_0
    //   2278: getfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2281: getfield 1967	org/vidogram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2284: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2287: goto -1818 -> 469
    //   2290: aload_0
    //   2291: ldc_w 2201
    //   2294: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2297: goto -1828 -> 469
    //   2300: aload_0
    //   2301: ldc_w 2203
    //   2304: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2307: goto -1838 -> 469
    //   2310: iconst_0
    //   2311: istore_3
    //   2312: goto -1810 -> 502
    //   2315: aload_0
    //   2316: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2319: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   2322: ifeq +17 -> 2339
    //   2325: ldc_w 2205
    //   2328: astore 7
    //   2330: aload 7
    //   2332: invokestatic 1246	org/vidogram/ui/ActionBar/Theme:getColor	(Ljava/lang/String;)I
    //   2335: istore_2
    //   2336: goto -1747 -> 589
    //   2339: ldc_w 2207
    //   2342: astore 7
    //   2344: goto -14 -> 2330
    //   2347: new 1780	android/text/SpannableStringBuilder
    //   2350: dup
    //   2351: ldc_w 2209
    //   2354: iconst_1
    //   2355: anewarray 1016	java/lang/Object
    //   2358: dup
    //   2359: iconst_0
    //   2360: aload 6
    //   2362: aastore
    //   2363: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2366: invokespecial 1783	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2369: astore 7
    //   2371: aload 7
    //   2373: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   2376: dup
    //   2377: getstatic 2099	android/graphics/Typeface:DEFAULT	Landroid/graphics/Typeface;
    //   2380: iconst_0
    //   2381: iload_2
    //   2382: invokespecial 2102	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2385: iconst_0
    //   2386: iconst_4
    //   2387: bipush 33
    //   2389: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2392: aload 7
    //   2394: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   2397: dup
    //   2398: ldc_w 2108
    //   2401: invokestatic 2112	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   2404: iconst_0
    //   2405: iload_2
    //   2406: invokespecial 2102	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;II)V
    //   2409: iconst_4
    //   2410: aload 7
    //   2412: invokevirtual 2113	android/text/SpannableStringBuilder:length	()I
    //   2415: bipush 33
    //   2417: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   2420: goto -1719 -> 701
    //   2423: aload_0
    //   2424: iconst_0
    //   2425: putfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2428: goto -1608 -> 820
    //   2431: astore 7
    //   2433: aload 7
    //   2435: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2438: goto -1618 -> 820
    //   2441: aload_0
    //   2442: aconst_null
    //   2443: putfield 1953	org/vidogram/ui/Cells/ChatMessageCell:currentNameString	Ljava/lang/String;
    //   2446: aload_0
    //   2447: aconst_null
    //   2448: putfield 2115	org/vidogram/ui/Cells/ChatMessageCell:nameLayout	Landroid/text/StaticLayout;
    //   2451: aload_0
    //   2452: iconst_0
    //   2453: putfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   2456: goto -1621 -> 835
    //   2459: aload_0
    //   2460: aload_0
    //   2461: getfield 2121	org/vidogram/ui/Cells/ChatMessageCell:currentForwardChannel	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2464: getfield 1967	org/vidogram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2467: putfield 1960	org/vidogram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2470: goto -1453 -> 1017
    //   2473: aload_0
    //   2474: getfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   2477: ifnull -1460 -> 1017
    //   2480: aload_0
    //   2481: aload_0
    //   2482: getfield 2119	org/vidogram/ui/Cells/ChatMessageCell:currentForwardUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   2485: invokestatic 1951	org/vidogram/messenger/UserObject:getUserName	(Lorg/vidogram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2488: putfield 1960	org/vidogram/ui/Cells/ChatMessageCell:currentForwardNameString	Ljava/lang/String;
    //   2491: goto -1474 -> 1017
    //   2494: new 1780	android/text/SpannableStringBuilder
    //   2497: dup
    //   2498: ldc_w 2211
    //   2501: iconst_2
    //   2502: anewarray 1016	java/lang/Object
    //   2505: dup
    //   2506: iconst_0
    //   2507: aload 7
    //   2509: aastore
    //   2510: dup
    //   2511: iconst_1
    //   2512: aload 8
    //   2514: aastore
    //   2515: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   2518: invokespecial 1783	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   2521: astore 5
    //   2523: goto -1302 -> 1221
    //   2526: astore 5
    //   2528: aload 5
    //   2530: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2533: goto -1074 -> 1459
    //   2536: aload_0
    //   2537: aload_0
    //   2538: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2541: ldc_w 1486
    //   2544: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2547: iadd
    //   2548: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   2551: goto -1039 -> 1512
    //   2554: aload_0
    //   2555: aload 5
    //   2557: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2560: putfield 1943	org/vidogram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2563: aload_0
    //   2564: getfield 309	org/vidogram/ui/Cells/ChatMessageCell:replyImageReceiver	Lorg/vidogram/messenger/ImageReceiver;
    //   2567: aload 5
    //   2569: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   2572: ldc_w 2059
    //   2575: aconst_null
    //   2576: aconst_null
    //   2577: iconst_1
    //   2578: invokevirtual 2062	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   2581: aload_0
    //   2582: iconst_1
    //   2583: putfield 2154	org/vidogram/ui/Cells/ChatMessageCell:needReplyImage	Z
    //   2586: iload_2
    //   2587: ldc_w 1552
    //   2590: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2593: isub
    //   2594: istore_2
    //   2595: goto -959 -> 1636
    //   2598: aload_1
    //   2599: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2602: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   2605: ifeq +39 -> 2644
    //   2608: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2611: aload_1
    //   2612: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2615: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2618: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2621: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2624: invokevirtual 780	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   2627: astore 7
    //   2629: aload 7
    //   2631: ifnull -979 -> 1652
    //   2634: aload 7
    //   2636: invokestatic 1951	org/vidogram/messenger/UserObject:getUserName	(Lorg/vidogram/tgnet/TLRPC$User;)Ljava/lang/String;
    //   2639: astore 5
    //   2641: goto -989 -> 1652
    //   2644: aload_1
    //   2645: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2648: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2651: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2654: ifge +40 -> 2694
    //   2657: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2660: aload_1
    //   2661: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2664: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2667: getfield 770	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2670: ineg
    //   2671: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2674: invokevirtual 803	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2677: astore 7
    //   2679: aload 7
    //   2681: ifnull -1029 -> 1652
    //   2684: aload 7
    //   2686: getfield 1967	org/vidogram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2689: astore 5
    //   2691: goto -1039 -> 1652
    //   2694: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2697: aload_1
    //   2698: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2701: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2704: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2707: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   2710: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2713: invokevirtual 803	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   2716: astore 7
    //   2718: aload 7
    //   2720: ifnull -1068 -> 1652
    //   2723: aload 7
    //   2725: getfield 1967	org/vidogram/tgnet/TLRPC$Chat:title	Ljava/lang/String;
    //   2728: astore 5
    //   2730: goto -1078 -> 1652
    //   2733: aload_1
    //   2734: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2737: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2740: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2743: instanceof 789
    //   2746: ifeq +55 -> 2801
    //   2749: aload_1
    //   2750: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2753: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2756: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2759: getfield 2212	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   2762: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2765: invokevirtual 2168	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   2768: ldc_w 373
    //   2771: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2774: iconst_0
    //   2775: invokestatic 2174	org/vidogram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   2778: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2781: iload_2
    //   2782: i2f
    //   2783: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2786: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   2789: astore 6
    //   2791: aload 5
    //   2793: astore_1
    //   2794: aload 6
    //   2796: astore 5
    //   2798: goto -1051 -> 1747
    //   2801: aload_1
    //   2802: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2805: getfield 876	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2808: ifnull +129 -> 2937
    //   2811: aload_1
    //   2812: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2815: getfield 876	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2818: invokeinterface 1800 1 0
    //   2823: ifle +114 -> 2937
    //   2826: aload_1
    //   2827: getfield 1930	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2830: getfield 876	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2833: invokeinterface 2213 1 0
    //   2838: astore 6
    //   2840: aload 6
    //   2842: astore_1
    //   2843: aload 6
    //   2845: invokevirtual 813	java/lang/String:length	()I
    //   2848: sipush 150
    //   2851: if_icmple +13 -> 2864
    //   2854: aload 6
    //   2856: iconst_0
    //   2857: sipush 150
    //   2860: invokevirtual 2217	java/lang/String:substring	(II)Ljava/lang/String;
    //   2863: astore_1
    //   2864: aload_1
    //   2865: bipush 10
    //   2867: bipush 32
    //   2869: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   2872: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2875: invokevirtual 2168	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   2878: ldc_w 373
    //   2881: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2884: iconst_0
    //   2885: invokestatic 2174	org/vidogram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   2888: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2891: iload_2
    //   2892: i2f
    //   2893: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2896: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   2899: astore 6
    //   2901: aload 5
    //   2903: astore_1
    //   2904: aload 6
    //   2906: astore 5
    //   2908: goto -1161 -> 1747
    //   2911: iconst_0
    //   2912: istore_3
    //   2913: goto -1083 -> 1830
    //   2916: astore_1
    //   2917: aload_1
    //   2918: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2921: goto -1064 -> 1857
    //   2924: iconst_0
    //   2925: istore_2
    //   2926: goto -1000 -> 1926
    //   2929: astore_1
    //   2930: aload_1
    //   2931: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2934: goto -982 -> 1952
    //   2937: aload 5
    //   2939: astore_1
    //   2940: aload 6
    //   2942: astore 5
    //   2944: goto -1197 -> 1747
    //   2947: aconst_null
    //   2948: astore 5
    //   2950: goto -1271 -> 1679
    //   2953: goto -1375 -> 1578
    //   2956: aconst_null
    //   2957: astore_1
    //   2958: aload 6
    //   2960: astore 5
    //   2962: goto -1215 -> 1747
    //   2965: goto -1430 -> 1535
    //   2968: aload 8
    //   2970: astore 7
    //   2972: goto -2253 -> 719
    //   2975: aconst_null
    //   2976: astore 5
    //   2978: aconst_null
    //   2979: astore 6
    //   2981: goto -2624 -> 357
    //
    // Exception table:
    //   from	to	target	type
    //   719	808	2431	java/lang/Exception
    //   808	820	2431	java/lang/Exception
    //   2423	2428	2431	java/lang/Exception
    //   1281	1459	2526	java/lang/Exception
    //   1765	1827	2916	java/lang/Exception
    //   1830	1857	2916	java/lang/Exception
    //   1862	1923	2929	java/lang/Exception
    //   1926	1952	2929	java/lang/Exception
  }

  private void updateSecretTimeText(MessageObject paramMessageObject)
  {
    if ((paramMessageObject == null) || (paramMessageObject.isOut()));
    do
    {
      return;
      paramMessageObject = paramMessageObject.getSecretTimeString();
    }
    while (paramMessageObject == null);
    this.infoWidth = (int)Math.ceil(Theme.chat_infoPaint.measureText(paramMessageObject));
    this.infoLayout = new StaticLayout(TextUtils.ellipsize(paramMessageObject, Theme.chat_infoPaint, this.infoWidth, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
    invalidate();
  }

  private void updateWaveform()
  {
    boolean bool = false;
    if ((this.currentMessageObject == null) || (this.documentAttachType != 3));
    while (true)
    {
      return;
      int i = 0;
      while (i < this.documentAttach.attributes.size())
      {
        TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
        if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeAudio))
        {
          if ((localDocumentAttribute.waveform == null) || (localDocumentAttribute.waveform.length == 0))
            MediaController.getInstance().generateWaveform(this.currentMessageObject);
          if (localDocumentAttribute.waveform != null)
            bool = true;
          this.useSeekBarWaweform = bool;
          this.seekBarWaveform.setWaveform(localDocumentAttribute.waveform);
          return;
        }
        i += 1;
      }
    }
  }

  public void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((this.currentMessageObject != null) && (paramBoolean1) && (!paramBoolean2) && (!this.currentMessageObject.mediaExists) && (!this.currentMessageObject.attachPathExists))
    {
      this.currentMessageObject.mediaExists = true;
      updateButtonState(true);
    }
  }

  public void downloadAudioIfNeed()
  {
    if ((this.documentAttachType != 3) || (this.documentAttach.size >= 1048576));
    do
      return;
    while (this.buttonState != 2);
    FileLoader.getInstance().loadFile(this.documentAttach, true, false);
    this.buttonState = 4;
    this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
  }

  public ImageReceiver getAvatarImage()
  {
    if (this.isAvatarVisible)
      return this.avatarImage;
    return null;
  }

  public int getLayoutHeight()
  {
    return this.layoutHeight;
  }

  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }

  public int getObserverTag()
  {
    return this.TAG;
  }

  public ImageReceiver getPhotoImage()
  {
    return this.photoImage;
  }

  public boolean isPinnedBottom()
  {
    return this.pinnedBottom;
  }

  public boolean isPinnedTop()
  {
    return this.pinnedTop;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.avatarImage.onAttachedToWindow();
    this.avatarImage.setParentView((View)getParent());
    this.replyImageReceiver.onAttachedToWindow();
    if (this.drawPhotoImage)
    {
      if (this.photoImage.onAttachedToWindow())
        updateButtonState(false);
      return;
    }
    updateButtonState(false);
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.avatarImage.onDetachedFromWindow();
    this.replyImageReceiver.onDetachedFromWindow();
    this.photoImage.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i13 = 1;
    int i12 = 0;
    if (this.currentMessageObject == null);
    label171: label202: Object localObject1;
    label245: int i2;
    label267: label295: int i11;
    label358: Object localObject2;
    int i7;
    label388: label431: label507: label1916: float f1;
    label552: label699: label850: label1272: label2042: int n;
    while (true)
    {
      return;
      if (!this.wasLayout)
      {
        requestLayout();
        return;
      }
      int j;
      if (this.currentMessageObject.isOutOwner())
      {
        Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
        Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextOut"));
        Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
        Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkOut");
        if (this.documentAttach != null)
        {
          if (this.documentAttachType != 3)
            break label1092;
          if (!this.currentMessageObject.isOutOwner())
            break label1039;
          this.seekBarWaveform.setColors(Theme.getColor("chat_outVoiceSeekbar"), Theme.getColor("chat_outVoiceSeekbarFill"), Theme.getColor("chat_outVoiceSeekbarSelected"));
          this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
        }
        if (!this.mediaBackground)
          break label1186;
        if (this.currentMessageObject.type != 13)
          break label1171;
        Theme.chat_timePaint.setColor(Theme.getColor("chat_serviceText"));
        if (!this.currentMessageObject.isOutOwner())
          break label1396;
        if (!isDrawSelectedBackground())
          break label1287;
        if ((this.mediaBackground) || (this.pinnedBottom))
          break label1272;
        this.currentBackgroundDrawable = Theme.chat_msgOutSelectedDrawable;
        localObject1 = Theme.chat_msgOutShadowDrawable;
        i2 = this.layoutWidth;
        int i6 = this.backgroundWidth;
        if (this.mediaBackground)
          break label1331;
        int i = 0;
        this.backgroundDrawableLeft = (i2 - i6 - i);
        i2 = this.backgroundWidth;
        if (!this.mediaBackground)
          break label1342;
        i = 0;
        j = i2 - i;
        i11 = this.backgroundDrawableLeft;
        i2 = j;
        if (!this.mediaBackground)
        {
          i2 = j;
          if (this.pinnedBottom)
            i2 = j - AndroidUtilities.dp(6.0F);
        }
        if ((!this.pinnedBottom) || (!this.pinnedTop))
          break label1353;
        j = 0;
        localObject2 = this.currentBackgroundDrawable;
        if ((!this.pinnedTop) && ((!this.pinnedTop) || (!this.pinnedBottom)))
          break label1378;
        i7 = 0;
        setDrawableBounds((Drawable)localObject2, i11, i7, i2, this.layoutHeight - j);
        if ((!this.pinnedTop) && ((!this.pinnedTop) || (!this.pinnedBottom)))
          break label1387;
        i7 = 0;
        setDrawableBounds((Drawable)localObject1, i11, i7, i2, this.layoutHeight - j);
        if ((this.drawBackground) && (this.currentBackgroundDrawable != null))
        {
          this.currentBackgroundDrawable.draw(paramCanvas);
          ((Drawable)localObject1).draw(paramCanvas);
        }
        drawContent(paramCanvas);
        if (this.drawShareButton)
        {
          localObject2 = Theme.chat_shareDrawable;
          if (!this.sharePressed)
            break label1792;
          localObject1 = Theme.colorPressedFilter;
          ((Drawable)localObject2).setColorFilter((ColorFilter)localObject1);
          if (!this.currentMessageObject.isOutOwner())
            break label1800;
          this.shareStartX = (this.currentBackgroundDrawable.getBounds().left - AndroidUtilities.dp(8.0F) - Theme.chat_shareDrawable.getIntrinsicWidth());
          localObject1 = Theme.chat_shareDrawable;
          j = this.shareStartX;
          i2 = this.layoutHeight - AndroidUtilities.dp(41.0F);
          this.shareStartY = i2;
          setDrawableBounds((Drawable)localObject1, j, i2);
          Theme.chat_shareDrawable.draw(paramCanvas);
          setDrawableBounds(Theme.chat_shareIconDrawable, this.shareStartX + AndroidUtilities.dp(9.0F), this.shareStartY + AndroidUtilities.dp(9.0F));
          Theme.chat_shareIconDrawable.draw(paramCanvas);
        }
        if ((this.drawNameLayout) && (this.nameLayout != null))
        {
          paramCanvas.save();
          if (this.currentMessageObject.type != 13)
            break label1849;
          Theme.chat_namePaint.setColor(Theme.getColor("chat_stickerNameText"));
          if (!this.currentMessageObject.isOutOwner())
            break label1824;
          this.nameX = AndroidUtilities.dp(28.0F);
          this.nameY = (this.layoutHeight - AndroidUtilities.dp(38.0F));
          Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
          Theme.chat_systemDrawable.setBounds((int)this.nameX - AndroidUtilities.dp(12.0F), (int)this.nameY - AndroidUtilities.dp(5.0F), (int)this.nameX + AndroidUtilities.dp(12.0F) + this.nameWidth, (int)this.nameY + AndroidUtilities.dp(22.0F));
          Theme.chat_systemDrawable.draw(paramCanvas);
          paramCanvas.translate(this.nameX, this.nameY);
          this.nameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null) || (this.forwardedNameLayout[1] == null))
          break label2142;
        if (!this.drawNameLayout)
          break label2042;
        j = 19;
        this.forwardNameY = AndroidUtilities.dp(j + 10);
        if (!this.currentMessageObject.isOutOwner())
          break label2048;
        Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_outForwardedNameText"));
      }
      for (this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F)); ; this.forwardNameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F)))
      {
        j = 0;
        while (j < 2)
        {
          paramCanvas.save();
          paramCanvas.translate(this.forwardNameX - this.forwardNameOffsetX[j], this.forwardNameY + AndroidUtilities.dp(16.0F) * j);
          this.forwardedNameLayout[j].draw(paramCanvas);
          paramCanvas.restore();
          j += 1;
        }
        Theme.chat_msgTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
        Theme.chat_msgTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
        Theme.chat_msgGameTextPaint.setColor(Theme.getColor("chat_messageTextIn"));
        Theme.chat_msgGameTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
        Theme.chat_replyTextPaint.linkColor = Theme.getColor("chat_messageLinkIn");
        break;
        label1039: this.seekBarWaveform.setColors(Theme.getColor("chat_inVoiceSeekbar"), Theme.getColor("chat_inVoiceSeekbarFill"), Theme.getColor("chat_inVoiceSeekbarSelected"));
        this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
        break label171;
        label1092: if (this.documentAttachType != 5)
          break label171;
        this.documentAttachType = 5;
        if (this.currentMessageObject.isOutOwner())
        {
          this.seekBar.setColors(Theme.getColor("chat_outAudioSeekbar"), Theme.getColor("chat_outAudioSeekbarFill"), Theme.getColor("chat_outAudioSeekbarSelected"));
          break label171;
        }
        this.seekBar.setColors(Theme.getColor("chat_inAudioSeekbar"), Theme.getColor("chat_inAudioSeekbarFill"), Theme.getColor("chat_inAudioSeekbarSelected"));
        break label171;
        label1171: Theme.chat_timePaint.setColor(Theme.getColor("chat_mediaTimeText"));
        break label202;
        label1186: if (this.currentMessageObject.isOutOwner())
        {
          localObject2 = Theme.chat_timePaint;
          if (isDrawSelectedBackground());
          for (localObject1 = "chat_outTimeSelectedText"; ; localObject1 = "chat_outTimeText")
          {
            ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
            break;
          }
        }
        localObject2 = Theme.chat_timePaint;
        if (isDrawSelectedBackground());
        for (localObject1 = "chat_inTimeSelectedText"; ; localObject1 = "chat_inTimeText")
        {
          ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
          break;
        }
        this.currentBackgroundDrawable = Theme.chat_msgOutMediaSelectedDrawable;
        localObject1 = Theme.chat_msgOutMediaShadowDrawable;
        break label245;
        label1287: if ((!this.mediaBackground) && (!this.pinnedBottom))
        {
          this.currentBackgroundDrawable = Theme.chat_msgOutDrawable;
          localObject1 = Theme.chat_msgOutShadowDrawable;
          break label245;
        }
        this.currentBackgroundDrawable = Theme.chat_msgOutMediaDrawable;
        localObject1 = Theme.chat_msgOutMediaShadowDrawable;
        break label245;
        label1331: j = AndroidUtilities.dp(9.0F);
        break label267;
        label1342: j = AndroidUtilities.dp(3.0F);
        break label295;
        label1353: if (this.pinnedBottom)
        {
          j = AndroidUtilities.dp(1.0F);
          break label358;
        }
        j = AndroidUtilities.dp(2.0F);
        break label358;
        label1378: i7 = AndroidUtilities.dp(1.0F);
        break label388;
        label1387: i7 = AndroidUtilities.dp(1.0F);
        break label431;
        label1396: if (isDrawSelectedBackground())
          if ((!this.mediaBackground) && (!this.pinnedBottom))
          {
            this.currentBackgroundDrawable = Theme.chat_msgInSelectedDrawable;
            localObject1 = Theme.chat_msgInShadowDrawable;
            if ((!this.isChat) || (!this.currentMessageObject.isFromUser()))
              break label1725;
            j = 48;
            if (this.mediaBackground)
              break label1731;
            i2 = 3;
            this.backgroundDrawableLeft = AndroidUtilities.dp(j + i2);
            i2 = this.backgroundWidth;
            if (!this.mediaBackground)
              break label1738;
            int k = 0;
            i11 = i2 - k;
            m = this.backgroundDrawableLeft;
            i7 = m;
            i2 = i11;
            if (!this.mediaBackground)
            {
              i7 = m;
              i2 = i11;
              if (this.pinnedBottom)
              {
                i2 = i11 - AndroidUtilities.dp(6.0F);
                i7 = m + AndroidUtilities.dp(6.0F);
              }
            }
            if ((!this.pinnedBottom) || (!this.pinnedTop))
              break label1749;
            m = 0;
            localObject2 = this.currentBackgroundDrawable;
            if ((!this.pinnedTop) && ((!this.pinnedTop) || (!this.pinnedBottom)))
              break label1774;
            i11 = 0;
            setDrawableBounds((Drawable)localObject2, i7, i11, i2, this.layoutHeight - m);
            if ((!this.pinnedTop) && ((!this.pinnedTop) || (!this.pinnedBottom)))
              break label1783;
          }
        label1429: label1450: label1460: label1489: label1749: label1774: label1783: for (i11 = 0; ; i11 = AndroidUtilities.dp(1.0F))
        {
          setDrawableBounds((Drawable)localObject1, i7, i11, i2, this.layoutHeight - m);
          break;
          this.currentBackgroundDrawable = Theme.chat_msgInMediaSelectedDrawable;
          localObject1 = Theme.chat_msgInMediaShadowDrawable;
          break label1429;
          if ((!this.mediaBackground) && (!this.pinnedBottom))
          {
            this.currentBackgroundDrawable = Theme.chat_msgInDrawable;
            localObject1 = Theme.chat_msgInShadowDrawable;
            break label1429;
          }
          this.currentBackgroundDrawable = Theme.chat_msgInMediaDrawable;
          localObject1 = Theme.chat_msgInMediaShadowDrawable;
          break label1429;
          m = 0;
          break label1450;
          i2 = 9;
          break label1460;
          m = AndroidUtilities.dp(3.0F);
          break label1489;
          if (this.pinnedBottom)
          {
            m = AndroidUtilities.dp(1.0F);
            break label1571;
          }
          m = AndroidUtilities.dp(2.0F);
          break label1571;
          i11 = AndroidUtilities.dp(1.0F);
          break label1601;
        }
        label1571: label1601: label1731: label1738: localObject1 = Theme.colorFilter;
        label1725: break label507;
        label1792: label1800: this.shareStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(8.0F));
        break label552;
        label1824: this.nameX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(22.0F));
        break label699;
        label1849: if ((this.mediaBackground) || (this.currentMessageObject.isOutOwner()))
        {
          this.nameX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(11.0F) - this.nameOffsetX);
          if (this.currentUser == null)
            break label1996;
          Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentUser.id));
          if (!this.pinnedTop)
            break label2035;
          f1 = 9.0F;
        }
        while (true)
        {
          this.nameY = AndroidUtilities.dp(f1);
          break;
          m = this.currentBackgroundDrawable.getBounds().left;
          if ((!this.mediaBackground) && (this.pinnedBottom))
            f1 = 11.0F;
          while (true)
          {
            this.nameX = (AndroidUtilities.dp(f1) + m - this.nameOffsetX);
            break;
            f1 = 17.0F;
          }
          label1996: if (this.currentChat != null)
          {
            Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(this.currentChat.id));
            break label1916;
          }
          Theme.chat_namePaint.setColor(AvatarDrawable.getNameColorForId(0));
          break label1916;
          label2035: f1 = 10.0F;
        }
        m = 0;
        break label850;
        label2048: Theme.chat_forwardNamePaint.setColor(Theme.getColor("chat_inForwardedNameText"));
        if (!this.mediaBackground)
          break label2091;
      }
      label2091: int m = this.currentBackgroundDrawable.getBounds().left;
      if ((!this.mediaBackground) && (this.pinnedBottom))
        f1 = 11.0F;
      while (true)
      {
        this.forwardNameX = (AndroidUtilities.dp(f1) + m);
        break;
        f1 = 17.0F;
      }
      label2142: if (this.currentMessageObject.isReply())
      {
        if (this.currentMessageObject.type != 13)
          break label3229;
        Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_stickerReplyLine"));
        Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_stickerReplyNameText"));
        Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_stickerReplyMessageText"));
        if (!this.currentMessageObject.isOutOwner())
          break;
        this.replyStartX = AndroidUtilities.dp(23.0F);
        this.replyStartY = (this.layoutHeight - AndroidUtilities.dp(58.0F));
        if (this.nameLayout != null)
          this.replyStartY -= AndroidUtilities.dp(31.0F);
        i2 = Math.max(this.replyNameWidth, this.replyTextWidth);
        if (!this.needReplyImage)
          break label3223;
        m = 44;
        label2281: m = AndroidUtilities.dp(m + 14);
        Theme.chat_systemDrawable.setColorFilter(Theme.colorFilter);
        Theme.chat_systemDrawable.setBounds(this.replyStartX - AndroidUtilities.dp(7.0F), this.replyStartY - AndroidUtilities.dp(6.0F), m + i2 + (this.replyStartX - AndroidUtilities.dp(7.0F)), this.replyStartY + AndroidUtilities.dp(41.0F));
        Theme.chat_systemDrawable.draw(paramCanvas);
        paramCanvas.drawRect(this.replyStartX, this.replyStartY, this.replyStartX + AndroidUtilities.dp(2.0F), this.replyStartY + AndroidUtilities.dp(35.0F), Theme.chat_replyLinePaint);
        if (this.needReplyImage)
        {
          this.replyImageReceiver.setImageCoords(this.replyStartX + AndroidUtilities.dp(10.0F), this.replyStartY, AndroidUtilities.dp(35.0F), AndroidUtilities.dp(35.0F));
          this.replyImageReceiver.draw(paramCanvas);
        }
        float f2;
        if (this.replyNameLayout != null)
        {
          paramCanvas.save();
          f1 = this.replyStartX;
          f2 = this.replyNameOffset;
          if (!this.needReplyImage)
            break label3681;
          n = 44;
          paramCanvas.translate(AndroidUtilities.dp(n + 10) + (f1 - f2), this.replyStartY);
          this.replyNameLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
        if (this.replyTextLayout != null)
        {
          paramCanvas.save();
          f1 = this.replyStartX;
          f2 = this.replyTextOffset;
          if (!this.needReplyImage)
            break label3687;
          n = 44;
          label2556: paramCanvas.translate(AndroidUtilities.dp(n + 10) + (f1 - f2), this.replyStartY + AndroidUtilities.dp(19.0F));
          this.replyTextLayout.draw(paramCanvas);
          paramCanvas.restore();
        }
      }
      label2220: label2487: if (((!this.drawTime) && (this.mediaBackground)) || (this.forceNotDrawTime))
        continue;
      if (this.pinnedBottom)
        paramCanvas.translate(0.0F, AndroidUtilities.dp(2.0F));
      if (!this.mediaBackground)
        break label3973;
      if (this.currentMessageObject.type != 13)
        break label3693;
      localObject1 = Theme.chat_timeStickerBackgroundDrawable;
      label2660: i2 = this.timeX;
      i7 = AndroidUtilities.dp(4.0F);
      i11 = this.layoutHeight;
      int i14 = AndroidUtilities.dp(27.0F);
      int i15 = this.timeWidth;
      if (!this.currentMessageObject.isOutOwner())
        break label3701;
      n = 20;
      label2708: setDrawableBounds((Drawable)localObject1, i2 - i7, i11 - i14, i15 + AndroidUtilities.dp(n + 8), AndroidUtilities.dp(17.0F));
      ((Drawable)localObject1).draw(paramCanvas);
      if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0)
        break label5301;
      n = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
      if (!this.currentMessageObject.isSending())
        break label3707;
      if (this.currentMessageObject.isOutOwner())
        break label3962;
      setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
      Theme.chat_msgMediaClockDrawable.draw(paramCanvas);
    }
    while (true)
    {
      paramCanvas.save();
      paramCanvas.translate(n + this.timeX, this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
      this.timeLayout.draw(paramCanvas);
      paramCanvas.restore();
      label2894: label2922: label2942: int i4;
      if (this.currentMessageObject.isOutOwner())
      {
        if ((int)(this.currentMessageObject.getDialogId() >> 32) != 1)
          break label4465;
        i11 = 1;
        if (!this.currentMessageObject.isSending())
          break label4471;
        n = 0;
        i2 = 0;
        i7 = i13;
        if (i7 != 0)
        {
          if (this.mediaBackground)
            break label4538;
          setDrawableBounds(Theme.chat_msgOutClockDrawable, this.layoutWidth - AndroidUtilities.dp(18.5F) - Theme.chat_msgOutClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.5F) - Theme.chat_msgOutClockDrawable.getIntrinsicHeight());
          Theme.chat_msgOutClockDrawable.draw(paramCanvas);
        }
        label3004: if (i11 == 0)
          break label4709;
        if ((i2 != 0) || (n != 0))
        {
          if (this.mediaBackground)
            break label4656;
          setDrawableBounds(Theme.chat_msgBroadcastDrawable, this.layoutWidth - AndroidUtilities.dp(20.5F) - Theme.chat_msgBroadcastDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - Theme.chat_msgBroadcastDrawable.getIntrinsicHeight());
          Theme.chat_msgBroadcastDrawable.draw(paramCanvas);
        }
        label3076: if (i12 == 0)
          break label5252;
        if (this.mediaBackground)
          break label5254;
        i2 = this.layoutWidth - AndroidUtilities.dp(32.0F);
        n = this.layoutHeight - AndroidUtilities.dp(21.0F);
        label3114: this.rect.set(i2, n, AndroidUtilities.dp(14.0F) + i2, AndroidUtilities.dp(14.0F) + n);
        paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
        setDrawableBounds(Theme.chat_msgErrorDrawable, i2 + AndroidUtilities.dp(6.0F), n + AndroidUtilities.dp(2.0F));
        Theme.chat_msgErrorDrawable.draw(paramCanvas);
        return;
        this.replyStartX = (this.currentBackgroundDrawable.getBounds().right + AndroidUtilities.dp(17.0F));
        break label2220;
        label3223: i1 = 0;
        break label2281;
        label3229: if (this.currentMessageObject.isOutOwner())
        {
          Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_outReplyLine"));
          Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_outReplyNameText"));
          if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
          {
            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_outReplyMessageText"));
            this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
            label3357: if ((!this.drawForwardedName) || (this.forwardedNameLayout[0] == null))
              break label3669;
            i1 = 36;
            label3377: if ((!this.drawNameLayout) || (this.nameLayout == null))
              break label3675;
          }
        }
        label3669: label3675: for (int i3 = 20; ; i3 = 0)
        {
          this.replyStartY = AndroidUtilities.dp(i3 + (i1 + 12));
          break;
          localObject2 = Theme.chat_replyTextPaint;
          if (isDrawSelectedBackground());
          for (localObject1 = "chat_outReplyMediaMessageSelectedText"; ; localObject1 = "chat_outReplyMediaMessageText")
          {
            ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
            break;
          }
          Theme.chat_replyLinePaint.setColor(Theme.getColor("chat_inReplyLine"));
          Theme.chat_replyNamePaint.setColor(Theme.getColor("chat_inReplyNameText"));
          if ((this.currentMessageObject.replyMessageObject != null) && (this.currentMessageObject.replyMessageObject.type == 0) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) && (!(this.currentMessageObject.replyMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
          {
            Theme.chat_replyTextPaint.setColor(Theme.getColor("chat_inReplyMessageText"));
            if (this.mediaBackground)
            {
              this.replyStartX = (this.currentBackgroundDrawable.getBounds().left + AndroidUtilities.dp(12.0F));
              break label3357;
            }
          }
          else
          {
            localObject2 = Theme.chat_replyTextPaint;
            if (isDrawSelectedBackground());
            for (localObject1 = "chat_inReplyMediaMessageSelectedText"; ; localObject1 = "chat_inReplyMediaMessageText")
            {
              ((TextPaint)localObject2).setColor(Theme.getColor((String)localObject1));
              break;
            }
          }
          i1 = this.currentBackgroundDrawable.getBounds().left;
          if ((!this.mediaBackground) && (this.pinnedBottom))
            f1 = 12.0F;
          while (true)
          {
            this.replyStartX = (AndroidUtilities.dp(f1) + i1);
            break;
            f1 = 18.0F;
          }
          i1 = 0;
          break label3377;
        }
        label3681: i1 = 0;
        break label2487;
        label3687: i1 = 0;
        break label2556;
        label3693: localObject1 = Theme.chat_timeBackgroundDrawable;
        break label2660;
        label3701: i1 = 0;
        break label2708;
        label3707: if (this.currentMessageObject.isSendError())
        {
          if (!this.currentMessageObject.isOutOwner())
          {
            i3 = this.timeX + AndroidUtilities.dp(11.0F);
            int i8 = this.layoutHeight - AndroidUtilities.dp(26.5F);
            this.rect.set(i3, i8, AndroidUtilities.dp(14.0F) + i3, AndroidUtilities.dp(14.0F) + i8);
            paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
            setDrawableBounds(Theme.chat_msgErrorDrawable, i3 + AndroidUtilities.dp(6.0F), i8 + AndroidUtilities.dp(2.0F));
            Theme.chat_msgErrorDrawable.draw(paramCanvas);
            continue;
          }
        }
        else
          if (this.currentMessageObject.type != 13)
            break label3965;
        label3962: label3965: for (localObject1 = Theme.chat_msgStickerViewsDrawable; ; localObject1 = Theme.chat_msgMediaViewsDrawable)
        {
          setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(9.5F) - this.timeLayout.getHeight());
          ((Drawable)localObject1).draw(paramCanvas);
          if (this.viewsLayout != null)
          {
            paramCanvas.save();
            i4 = this.timeX;
            paramCanvas.translate(((Drawable)localObject1).getIntrinsicWidth() + i4 + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(11.3F) - this.timeLayout.getHeight());
            this.viewsLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          break;
        }
        label3973: if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0)
          break label5295;
        i1 = (int)(this.timeWidth - this.timeLayout.getLineWidth(0));
        if (!this.currentMessageObject.isSending())
          break label4139;
        if (this.currentMessageObject.isOutOwner())
          break label4396;
        if (!isDrawSelectedBackground())
          break label4131;
        localObject1 = Theme.chat_msgInSelectedClockDrawable;
        label4039: setDrawableBounds((Drawable)localObject1, this.timeX + AndroidUtilities.dp(11.0F), this.layoutHeight - AndroidUtilities.dp(8.5F) - ((Drawable)localObject1).getIntrinsicHeight());
        ((Drawable)localObject1).draw(paramCanvas);
      }
      while (true)
      {
        paramCanvas.save();
        paramCanvas.translate(i1 + this.timeX, this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
        this.timeLayout.draw(paramCanvas);
        paramCanvas.restore();
        break label2894;
        break;
        label4131: localObject1 = Theme.chat_msgInClockDrawable;
        break label4039;
        label4139: if (this.currentMessageObject.isSendError())
        {
          if (!this.currentMessageObject.isOutOwner())
          {
            i4 = this.timeX + AndroidUtilities.dp(11.0F);
            int i9 = this.layoutHeight - AndroidUtilities.dp(20.5F);
            this.rect.set(i4, i9, AndroidUtilities.dp(14.0F) + i4, AndroidUtilities.dp(14.0F) + i9);
            paramCanvas.drawRoundRect(this.rect, AndroidUtilities.dp(1.0F), AndroidUtilities.dp(1.0F), Theme.chat_msgErrorPaint);
            setDrawableBounds(Theme.chat_msgErrorDrawable, i4 + AndroidUtilities.dp(6.0F), i9 + AndroidUtilities.dp(2.0F));
            Theme.chat_msgErrorDrawable.draw(paramCanvas);
            continue;
          }
        }
        else
        {
          if (this.currentMessageObject.isOutOwner())
            break label4407;
          if (!isDrawSelectedBackground())
            break label4399;
        }
        label4396: label4399: for (localObject1 = Theme.chat_msgInViewsSelectedDrawable; ; localObject1 = Theme.chat_msgInViewsDrawable)
        {
          setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
          ((Drawable)localObject1).draw(paramCanvas);
          if (this.viewsLayout != null)
          {
            paramCanvas.save();
            paramCanvas.translate(this.timeX + Theme.chat_msgInViewsDrawable.getIntrinsicWidth() + AndroidUtilities.dp(3.0F), this.layoutHeight - AndroidUtilities.dp(6.5F) - this.timeLayout.getHeight());
            this.viewsLayout.draw(paramCanvas);
            paramCanvas.restore();
          }
          break;
        }
        label4407: if (isDrawSelectedBackground());
        for (localObject1 = Theme.chat_msgOutViewsSelectedDrawable; ; localObject1 = Theme.chat_msgOutViewsDrawable)
        {
          setDrawableBounds((Drawable)localObject1, this.timeX, this.layoutHeight - AndroidUtilities.dp(4.5F) - this.timeLayout.getHeight());
          ((Drawable)localObject1).draw(paramCanvas);
          break;
        }
        label4465: i11 = 0;
        break label2922;
        label4471: if (this.currentMessageObject.isSendError())
        {
          i1 = 0;
          i5 = 0;
          i10 = 0;
          i12 = 1;
          break label2942;
        }
        if (this.currentMessageObject.isSent())
        {
          if (!this.currentMessageObject.isUnread());
          for (i1 = 1; ; i1 = 0)
          {
            i5 = i1;
            i1 = 1;
            i10 = 0;
            break;
          }
          label4538: if (this.currentMessageObject.type == 13)
          {
            setDrawableBounds(Theme.chat_msgStickerClockDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.chat_msgStickerClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgStickerClockDrawable.getIntrinsicHeight());
            Theme.chat_msgStickerClockDrawable.draw(paramCanvas);
            break label3004;
          }
          setDrawableBounds(Theme.chat_msgMediaClockDrawable, this.layoutWidth - AndroidUtilities.dp(22.0F) - Theme.chat_msgMediaClockDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgMediaClockDrawable.getIntrinsicHeight());
          Theme.chat_msgMediaClockDrawable.draw(paramCanvas);
          break label3004;
          label4656: setDrawableBounds(Theme.chat_msgBroadcastMediaDrawable, this.layoutWidth - AndroidUtilities.dp(24.0F) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(13.0F) - Theme.chat_msgBroadcastMediaDrawable.getIntrinsicHeight());
          Theme.chat_msgBroadcastMediaDrawable.draw(paramCanvas);
          break label3076;
          label4709: if (i1 != 0)
          {
            if (this.mediaBackground)
              break label4908;
            if (isDrawSelectedBackground())
            {
              localObject1 = Theme.chat_msgOutCheckSelectedDrawable;
              label4733: if (i5 == 0)
                break label4865;
              setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(22.5F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
              ((Drawable)localObject1).draw(paramCanvas);
            }
          }
          else
          {
            if (i5 == 0)
              break label5080;
            if (this.mediaBackground)
              break label5136;
            if (!isDrawSelectedBackground())
              break label5128;
          }
          label4778: label4784: for (localObject1 = Theme.chat_msgOutHalfCheckSelectedDrawable; ; localObject1 = Theme.chat_msgOutHalfCheckDrawable)
          {
            setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(18.0F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
            ((Drawable)localObject1).draw(paramCanvas);
            break;
            localObject1 = Theme.chat_msgOutCheckDrawable;
            break label4733;
            setDrawableBounds((Drawable)localObject1, this.layoutWidth - AndroidUtilities.dp(18.5F) - ((Drawable)localObject1).getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(8.0F) - ((Drawable)localObject1).getIntrinsicHeight());
            break label4778;
            if (this.currentMessageObject.type == 13)
            {
              if (i5 != 0)
                setDrawableBounds(Theme.chat_msgStickerCheckDrawable, this.layoutWidth - AndroidUtilities.dp(26.299999F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
              while (true)
              {
                Theme.chat_msgStickerCheckDrawable.draw(paramCanvas);
                break;
                setDrawableBounds(Theme.chat_msgStickerCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgStickerCheckDrawable.getIntrinsicHeight());
              }
            }
            if (i5 != 0)
              setDrawableBounds(Theme.chat_msgMediaCheckDrawable, this.layoutWidth - AndroidUtilities.dp(26.299999F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
            while (true)
            {
              Theme.chat_msgMediaCheckDrawable.draw(paramCanvas);
              break label4784;
              label5080: break;
              setDrawableBounds(Theme.chat_msgMediaCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgMediaCheckDrawable.getIntrinsicHeight());
            }
          }
          label4865: label4908: if (this.currentMessageObject.type == 13)
          {
            setDrawableBounds(Theme.chat_msgStickerHalfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgStickerHalfCheckDrawable.getIntrinsicHeight());
            Theme.chat_msgStickerHalfCheckDrawable.draw(paramCanvas);
            break label3076;
          }
          label5128: label5136: setDrawableBounds(Theme.chat_msgMediaHalfCheckDrawable, this.layoutWidth - AndroidUtilities.dp(21.5F) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicWidth(), this.layoutHeight - AndroidUtilities.dp(12.5F) - Theme.chat_msgMediaHalfCheckDrawable.getIntrinsicHeight());
          Theme.chat_msgMediaHalfCheckDrawable.draw(paramCanvas);
          break label3076;
          label5252: break;
          label5254: i5 = this.layoutWidth - AndroidUtilities.dp(34.5F);
          i1 = this.layoutHeight - AndroidUtilities.dp(25.5F);
          break label3114;
        }
        int i10 = 0;
        i1 = 0;
        int i5 = 0;
        break label2942;
        label5295: i1 = 0;
      }
      label5301: int i1 = 0;
    }
  }

  public void onFailedDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5));
    for (boolean bool = true; ; bool = false)
    {
      updateButtonState(bool);
      return;
    }
  }

  @SuppressLint({"DrawAllocation"})
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if (this.currentMessageObject == null)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      return;
    }
    label176: label221: label368: Object localObject;
    if ((paramBoolean) || (!this.wasLayout))
    {
      this.layoutWidth = getMeasuredWidth();
      this.layoutHeight = (getMeasuredHeight() - this.substractBackgroundHeight);
      if (this.timeTextWidth < 0)
        this.timeTextWidth = AndroidUtilities.dp(10.0F);
      this.timeLayout = new StaticLayout(this.currentTimeString, Theme.chat_timePaint, this.timeTextWidth + AndroidUtilities.dp(6.0F), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      if (this.mediaBackground)
        break label614;
      if (this.currentMessageObject.isOutOwner())
        break label591;
      paramInt2 = this.backgroundWidth;
      paramInt3 = AndroidUtilities.dp(9.0F);
      paramInt4 = this.timeWidth;
      if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        paramInt1 = AndroidUtilities.dp(48.0F);
        this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
        if ((this.currentMessageObject.messageOwner.flags & 0x400) == 0)
          break label711;
        this.viewsLayout = new StaticLayout(this.currentViewsString, Theme.chat_timePaint, this.viewsTextWidth, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if (this.isAvatarVisible)
          this.avatarImage.setImageCoords(AndroidUtilities.dp(6.0F), this.avatarImage.getImageY(), AndroidUtilities.dp(42.0F), AndroidUtilities.dp(42.0F));
        this.wasLayout = true;
      }
    }
    else
    {
      if (this.currentMessageObject.type == 0)
        this.textY = (AndroidUtilities.dp(10.0F) + this.namesOffset);
      if (this.documentAttachType != 3)
        break label812;
      if (!this.currentMessageObject.isOutOwner())
        break label719;
      this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(57.0F));
      this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
      if (this.hasLinkPreview)
      {
        this.seekBarX += AndroidUtilities.dp(10.0F);
        this.buttonX += AndroidUtilities.dp(10.0F);
        this.timeAudioX += AndroidUtilities.dp(10.0F);
      }
      localObject = this.seekBarWaveform;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview)
        break label802;
      paramInt1 = 10;
      label441: ((SeekBarWaveform)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 92), AndroidUtilities.dp(30.0F));
      localObject = this.seekBar;
      paramInt2 = this.backgroundWidth;
      if (!this.hasLinkPreview)
        break label807;
    }
    label802: label807: for (paramInt1 = 10; ; paramInt1 = 0)
    {
      ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 72), AndroidUtilities.dp(30.0F));
      this.seekBarY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
      updateAudioProgress();
      return;
      paramInt1 = 0;
      break;
      label591: this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(38.5F));
      break label176;
      label614: if (!this.currentMessageObject.isOutOwner())
      {
        paramInt2 = this.backgroundWidth;
        paramInt3 = AndroidUtilities.dp(4.0F);
        paramInt4 = this.timeWidth;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()));
        for (paramInt1 = AndroidUtilities.dp(48.0F); ; paramInt1 = 0)
        {
          this.timeX = (paramInt1 + (paramInt2 - paramInt3 - paramInt4));
          break;
        }
      }
      this.timeX = (this.layoutWidth - this.timeWidth - AndroidUtilities.dp(42.0F));
      break label176;
      label711: this.viewsLayout = null;
      break label221;
      label719: if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        this.seekBarX = AndroidUtilities.dp(114.0F);
        this.buttonX = AndroidUtilities.dp(71.0F);
        this.timeAudioX = AndroidUtilities.dp(124.0F);
        break label368;
      }
      this.seekBarX = AndroidUtilities.dp(66.0F);
      this.buttonX = AndroidUtilities.dp(23.0F);
      this.timeAudioX = AndroidUtilities.dp(76.0F);
      break label368;
      paramInt1 = 0;
      break label441;
    }
    label812: if (this.documentAttachType == 5)
    {
      if (this.currentMessageObject.isOutOwner())
      {
        this.seekBarX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(56.0F));
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
        this.timeAudioX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(67.0F));
        if (this.hasLinkPreview)
        {
          this.seekBarX += AndroidUtilities.dp(10.0F);
          this.buttonX += AndroidUtilities.dp(10.0F);
          this.timeAudioX += AndroidUtilities.dp(10.0F);
        }
        localObject = this.seekBar;
        paramInt2 = this.backgroundWidth;
        if (!this.hasLinkPreview)
          break label1149;
      }
      label1149: for (paramInt1 = 10; ; paramInt1 = 0)
      {
        ((SeekBar)localObject).setSize(paramInt2 - AndroidUtilities.dp(paramInt1 + 65), AndroidUtilities.dp(30.0F));
        this.seekBarY = (AndroidUtilities.dp(29.0F) + this.namesOffset + this.mediaOffsetY);
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        updateAudioProgress();
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()))
        {
          this.seekBarX = AndroidUtilities.dp(113.0F);
          this.buttonX = AndroidUtilities.dp(71.0F);
          this.timeAudioX = AndroidUtilities.dp(124.0F);
          break;
        }
        this.seekBarX = AndroidUtilities.dp(65.0F);
        this.buttonX = AndroidUtilities.dp(23.0F);
        this.timeAudioX = AndroidUtilities.dp(76.0F);
        break;
      }
    }
    if ((this.documentAttachType == 1) && (!this.drawPhotoImage))
    {
      if (this.currentMessageObject.isOutOwner())
        this.buttonX = (this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F));
      while (true)
      {
        if (this.hasLinkPreview)
          this.buttonX += AndroidUtilities.dp(10.0F);
        this.buttonY = (AndroidUtilities.dp(13.0F) + this.namesOffset + this.mediaOffsetY);
        this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(44.0F), this.buttonY + AndroidUtilities.dp(44.0F));
        this.photoImage.setImageCoords(this.buttonX - AndroidUtilities.dp(10.0F), this.buttonY - AndroidUtilities.dp(10.0F), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()))
        {
          this.buttonX = AndroidUtilities.dp(71.0F);
          continue;
        }
        this.buttonX = AndroidUtilities.dp(23.0F);
      }
    }
    if (this.currentMessageObject.type == 12)
    {
      if (this.currentMessageObject.isOutOwner())
        paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(14.0F);
      while (true)
      {
        this.photoImage.setImageCoords(paramInt1, AndroidUtilities.dp(13.0F) + this.namesOffset, AndroidUtilities.dp(44.0F), AndroidUtilities.dp(44.0F));
        return;
        if ((this.isChat) && (this.currentMessageObject.isFromUser()))
        {
          paramInt1 = AndroidUtilities.dp(72.0F);
          continue;
        }
        paramInt1 = AndroidUtilities.dp(23.0F);
      }
    }
    if (this.currentMessageObject.isOutOwner())
      if (this.mediaBackground)
        paramInt1 = this.layoutWidth - this.backgroundWidth - AndroidUtilities.dp(3.0F);
    while (true)
    {
      this.photoImage.setImageCoords(paramInt1, this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
      this.buttonX = (int)(paramInt1 + (this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0F)) / 2.0F);
      this.buttonY = ((int)(AndroidUtilities.dp(7.0F) + (this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0F)) / 2.0F) + this.namesOffset);
      this.radialProgress.setProgressRect(this.buttonX, this.buttonY, this.buttonX + AndroidUtilities.dp(48.0F), this.buttonY + AndroidUtilities.dp(48.0F));
      this.deleteProgressRect.set(this.buttonX + AndroidUtilities.dp(3.0F), this.buttonY + AndroidUtilities.dp(3.0F), this.buttonX + AndroidUtilities.dp(45.0F), this.buttonY + AndroidUtilities.dp(45.0F));
      return;
      paramInt1 = this.layoutWidth - this.backgroundWidth + AndroidUtilities.dp(6.0F);
      continue;
      if ((this.isChat) && (this.currentMessageObject.isFromUser()))
      {
        paramInt1 = AndroidUtilities.dp(63.0F);
        continue;
      }
      paramInt1 = AndroidUtilities.dp(15.0F);
    }
  }

  protected void onLongPress()
  {
    if ((this.pressedLink instanceof URLSpanMono))
      this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
    do
      while (true)
      {
        resetPressedLink(-1);
        if ((this.buttonPressed != 0) || (this.pressedBotButton != -1))
        {
          this.buttonPressed = 0;
          this.pressedBotButton = -1;
          invalidate();
        }
        if (this.instantPressed)
        {
          this.instantPressed = false;
          invalidate();
        }
        if (this.delegate != null)
          this.delegate.didLongPressed(this);
        return;
        if (!(this.pressedLink instanceof URLSpanNoUnderline))
          break;
        if (!((URLSpanNoUnderline)this.pressedLink).getURL().startsWith("/"))
          continue;
        this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
        return;
      }
    while (!(this.pressedLink instanceof URLSpan));
    this.delegate.didPressedUrl(this.currentMessageObject, this.pressedLink, true);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject != null) && (this.currentMessageObject.checkLayout()))
    {
      this.inLayout = true;
      MessageObject localMessageObject = this.currentMessageObject;
      this.currentMessageObject = null;
      setMessageObject(localMessageObject, this.pinnedBottom, this.pinnedTop);
      this.inLayout = false;
    }
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.totalHeight + this.keyboardHeight);
  }

  public void onProgressDownload(String paramString, float paramFloat)
  {
    this.radialProgress.setProgress(paramFloat, true);
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
      if (this.buttonState != 4)
        updateButtonState(false);
    do
      return;
    while (this.buttonState == 1);
    updateButtonState(false);
  }

  public void onProgressUpload(String paramString, float paramFloat, boolean paramBoolean)
  {
    this.radialProgress.setProgress(paramFloat, true);
  }

  public void onProvideStructure(ViewStructure paramViewStructure)
  {
    super.onProvideStructure(paramViewStructure);
    if ((this.allowAssistant) && (Build.VERSION.SDK_INT >= 23))
    {
      if ((this.currentMessageObject.messageText == null) || (this.currentMessageObject.messageText.length() <= 0))
        break label57;
      paramViewStructure.setText(this.currentMessageObject.messageText);
    }
    label57: 
    do
      return;
    while ((this.currentMessageObject.caption == null) || (this.currentMessageObject.caption.length() <= 0));
    paramViewStructure.setText(this.currentMessageObject.caption);
  }

  public void onSeekBarDrag(float paramFloat)
  {
    if (this.currentMessageObject == null)
      return;
    this.currentMessageObject.audioProgress = paramFloat;
    MediaController.getInstance().seekToProgress(this.currentMessageObject, paramFloat);
  }

  public void onSuccessDownload(String paramString)
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      updateButtonState(true);
      updateWaveform();
    }
    while (true)
    {
      return;
      this.radialProgress.setProgress(1.0F, true);
      if (this.currentMessageObject.type == 0)
      {
        if ((this.documentAttachType == 2) && (this.currentMessageObject.audioProgress != 1.0F))
        {
          this.buttonState = 2;
          didPressedButton(true);
          return;
        }
        if (!this.photoNotSet)
        {
          updateButtonState(true);
          return;
        }
        setMessageObject(this.currentMessageObject, this.pinnedBottom, this.pinnedTop);
        return;
      }
      if ((!this.photoNotSet) || ((this.currentMessageObject.type == 8) && (this.currentMessageObject.audioProgress != 1.0F)))
      {
        if ((this.currentMessageObject.type != 8) || (this.currentMessageObject.audioProgress == 1.0F))
          break label200;
        this.photoNotSet = false;
        this.buttonState = 2;
        didPressedButton(true);
      }
      while (this.photoNotSet)
      {
        setMessageObject(this.currentMessageObject, this.pinnedBottom, this.pinnedTop);
        return;
        label200: updateButtonState(true);
      }
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool3;
    if ((this.currentMessageObject == null) || (!this.delegate.canPerformActions()))
      bool3 = super.onTouchEvent(paramMotionEvent);
    boolean bool2;
    float f1;
    float f2;
    do
    {
      while (true)
      {
        return bool3;
        this.disallowLongPress = false;
        bool2 = checkTextBlockMotionEvent(paramMotionEvent);
        boolean bool1 = bool2;
        if (!bool2)
          bool1 = checkOtherButtonMotionEvent(paramMotionEvent);
        bool2 = bool1;
        if (!bool1)
          bool2 = checkLinkPreviewMotionEvent(paramMotionEvent);
        bool1 = bool2;
        if (!bool2)
          bool1 = checkGameMotionEvent(paramMotionEvent);
        bool2 = bool1;
        if (!bool1)
          bool2 = checkCaptionMotionEvent(paramMotionEvent);
        bool1 = bool2;
        if (!bool2)
          bool1 = checkAudioMotionEvent(paramMotionEvent);
        bool2 = bool1;
        if (!bool1)
          bool2 = checkPhotoImageMotionEvent(paramMotionEvent);
        bool1 = bool2;
        if (!bool2)
          bool1 = checkBotButtonMotionEvent(paramMotionEvent);
        bool2 = bool1;
        if (paramMotionEvent.getAction() == 3)
        {
          this.buttonPressed = 0;
          this.pressedBotButton = -1;
          this.linkPreviewPressed = false;
          this.otherPressed = false;
          this.imagePressed = false;
          this.instantPressed = false;
          resetPressedLink(-1);
          bool2 = false;
        }
        if ((!this.disallowLongPress) && (bool2) && (paramMotionEvent.getAction() == 0))
          startCheckLongPress();
        if ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2))
          cancelCheckLongPress();
        bool3 = bool2;
        if (bool2)
          continue;
        f1 = paramMotionEvent.getX();
        f2 = paramMotionEvent.getY();
        if (paramMotionEvent.getAction() == 0)
        {
          if (this.delegate != null)
          {
            bool3 = bool2;
            if (!this.delegate.canPerformActions())
              continue;
          }
          if ((this.isAvatarVisible) && (this.avatarImage.isInsideImage(f1, getTop() + f2)))
          {
            this.avatarPressed = true;
            bool1 = true;
          }
          while (true)
          {
            bool3 = bool1;
            if (!bool1)
              break;
            startCheckLongPress();
            return bool1;
            if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null) && (f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY) && (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F)))
            {
              if ((this.viaWidth != 0) && (f1 >= this.forwardNameX + this.viaNameWidth + AndroidUtilities.dp(4.0F)))
                this.forwardBotPressed = true;
              while (true)
              {
                bool1 = true;
                break;
                this.forwardNamePressed = true;
              }
            }
            if ((this.drawNameLayout) && (this.nameLayout != null) && (this.viaWidth != 0) && (f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)) && (f2 <= this.nameY + AndroidUtilities.dp(20.0F)))
            {
              this.forwardBotPressed = true;
              bool1 = true;
              continue;
            }
            if ((this.currentMessageObject.isReply()) && (f1 >= this.replyStartX) && (f1 <= this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) && (f2 >= this.replyStartY) && (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F)))
            {
              this.replyPressed = true;
              bool1 = true;
              continue;
            }
            bool1 = bool2;
            if (!this.drawShareButton)
              continue;
            bool1 = bool2;
            if (f1 < this.shareStartX)
              continue;
            bool1 = bool2;
            if (f1 > this.shareStartX + AndroidUtilities.dp(40.0F))
              continue;
            bool1 = bool2;
            if (f2 < this.shareStartY)
              continue;
            bool1 = bool2;
            if (f2 > this.shareStartY + AndroidUtilities.dp(32.0F))
              continue;
            this.sharePressed = true;
            invalidate();
            bool1 = true;
          }
        }
        if (paramMotionEvent.getAction() != 2)
          cancelCheckLongPress();
        if (this.avatarPressed)
        {
          if (paramMotionEvent.getAction() == 1)
          {
            this.avatarPressed = false;
            playSoundEffect(0);
            bool3 = bool2;
            if (this.delegate == null)
              continue;
            if (this.currentUser != null)
            {
              this.delegate.didPressedUserAvatar(this, this.currentUser);
              return bool2;
            }
            bool3 = bool2;
            if (this.currentChat == null)
              continue;
            this.delegate.didPressedChannelAvatar(this, this.currentChat, 0);
            return bool2;
          }
          if (paramMotionEvent.getAction() == 3)
          {
            this.avatarPressed = false;
            return bool2;
          }
          bool3 = bool2;
          if (paramMotionEvent.getAction() != 2)
            continue;
          bool3 = bool2;
          if (!this.isAvatarVisible)
            continue;
          bool3 = bool2;
          if (this.avatarImage.isInsideImage(f1, f2 + getTop()))
            continue;
          this.avatarPressed = false;
          return bool2;
        }
        if (this.forwardNamePressed)
        {
          if (paramMotionEvent.getAction() == 1)
          {
            this.forwardNamePressed = false;
            playSoundEffect(0);
            bool3 = bool2;
            if (this.delegate == null)
              continue;
            if (this.currentForwardChannel != null)
            {
              this.delegate.didPressedChannelAvatar(this, this.currentForwardChannel, this.currentMessageObject.messageOwner.fwd_from.channel_post);
              return bool2;
            }
            bool3 = bool2;
            if (this.currentForwardUser == null)
              continue;
            this.delegate.didPressedUserAvatar(this, this.currentForwardUser);
            return bool2;
          }
          if (paramMotionEvent.getAction() == 3)
          {
            this.forwardNamePressed = false;
            return bool2;
          }
          bool3 = bool2;
          if (paramMotionEvent.getAction() != 2)
            continue;
          if ((f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY))
          {
            bool3 = bool2;
            if (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F))
              continue;
          }
          this.forwardNamePressed = false;
          return bool2;
        }
        if (this.forwardBotPressed)
        {
          if (paramMotionEvent.getAction() == 1)
          {
            this.forwardBotPressed = false;
            playSoundEffect(0);
            bool3 = bool2;
            if (this.delegate == null)
              continue;
            ChatMessageCellDelegate localChatMessageCellDelegate = this.delegate;
            if (this.currentViaBotUser != null);
            for (paramMotionEvent = this.currentViaBotUser.username; ; paramMotionEvent = this.currentMessageObject.messageOwner.via_bot_name)
            {
              localChatMessageCellDelegate.didPressedViaBot(this, paramMotionEvent);
              return bool2;
            }
          }
          if (paramMotionEvent.getAction() == 3)
          {
            this.forwardBotPressed = false;
            return bool2;
          }
          bool3 = bool2;
          if (paramMotionEvent.getAction() != 2)
            continue;
          if ((this.drawForwardedName) && (this.forwardedNameLayout[0] != null))
          {
            if ((f1 >= this.forwardNameX) && (f1 <= this.forwardNameX + this.forwardedNameWidth) && (f2 >= this.forwardNameY))
            {
              bool3 = bool2;
              if (f2 <= this.forwardNameY + AndroidUtilities.dp(32.0F))
                continue;
            }
            this.forwardBotPressed = false;
            return bool2;
          }
          if ((f1 >= this.nameX + this.viaNameWidth) && (f1 <= this.nameX + this.viaNameWidth + this.viaWidth) && (f2 >= this.nameY - AndroidUtilities.dp(4.0F)))
          {
            bool3 = bool2;
            if (f2 <= this.nameY + AndroidUtilities.dp(20.0F))
              continue;
          }
          this.forwardBotPressed = false;
          return bool2;
        }
        if (!this.replyPressed)
          break;
        if (paramMotionEvent.getAction() == 1)
        {
          this.replyPressed = false;
          playSoundEffect(0);
          bool3 = bool2;
          if (this.delegate == null)
            continue;
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
          return bool2;
        }
        if (paramMotionEvent.getAction() == 3)
        {
          this.replyPressed = false;
          return bool2;
        }
        bool3 = bool2;
        if (paramMotionEvent.getAction() != 2)
          continue;
        if ((f1 >= this.replyStartX) && (f1 <= this.replyStartX + Math.max(this.replyNameWidth, this.replyTextWidth)) && (f2 >= this.replyStartY))
        {
          bool3 = bool2;
          if (f2 <= this.replyStartY + AndroidUtilities.dp(35.0F))
            continue;
        }
        this.replyPressed = false;
        return bool2;
      }
      bool3 = bool2;
    }
    while (!this.sharePressed);
    if (paramMotionEvent.getAction() == 1)
    {
      this.sharePressed = false;
      playSoundEffect(0);
      if (this.delegate != null)
        this.delegate.didPressedShare(this);
    }
    while (true)
    {
      invalidate();
      return bool2;
      if (paramMotionEvent.getAction() == 3)
      {
        this.sharePressed = false;
        continue;
      }
      if ((paramMotionEvent.getAction() != 2) || ((f1 >= this.shareStartX) && (f1 <= this.shareStartX + AndroidUtilities.dp(40.0F)) && (f2 >= this.shareStartY) && (f2 <= this.shareStartY + AndroidUtilities.dp(32.0F))))
        continue;
      this.sharePressed = false;
    }
  }

  public void requestLayout()
  {
    if (this.inLayout)
      return;
    super.requestLayout();
  }

  public void setAllowAssistant(boolean paramBoolean)
  {
    this.allowAssistant = paramBoolean;
  }

  public void setCheckPressed(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.isCheckPressed = paramBoolean1;
    this.isPressed = paramBoolean2;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform)
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    while (true)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }

  public void setDelegate(ChatMessageCellDelegate paramChatMessageCellDelegate)
  {
    this.delegate = paramChatMessageCellDelegate;
  }

  public void setFullyDraw(boolean paramBoolean)
  {
    this.fullyDraw = paramBoolean;
  }

  public void setHighlighted(boolean paramBoolean)
  {
    if (this.isHighlighted == paramBoolean)
      return;
    this.isHighlighted = paramBoolean;
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform)
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    while (true)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }

  public void setHighlightedText(String paramString)
  {
    if ((this.currentMessageObject.messageOwner.message == null) || (this.currentMessageObject == null) || (this.currentMessageObject.type != 0) || (TextUtils.isEmpty(this.currentMessageObject.messageText)) || (paramString == null))
      if (!this.urlPathSelection.isEmpty())
      {
        this.linkSelectionBlockNum = -1;
        resetUrlPaths(true);
        invalidate();
      }
    while (true)
    {
      return;
      int k = TextUtils.indexOf(this.currentMessageObject.messageOwner.message.toLowerCase(), paramString.toLowerCase());
      if (k == -1)
      {
        if (this.urlPathSelection.isEmpty())
          continue;
        this.linkSelectionBlockNum = -1;
        resetUrlPaths(true);
        invalidate();
        return;
      }
      int j = k + paramString.length();
      int i = 0;
      while (i < this.currentMessageObject.textLayoutBlocks.size())
      {
        paramString = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
        if ((k >= paramString.charactersOffset) && (k < paramString.charactersOffset + paramString.textLayout.getText().length()))
        {
          this.linkSelectionBlockNum = i;
          resetUrlPaths(true);
          try
          {
            Object localObject = obtainNewUrlPath(true);
            int m = paramString.textLayout.getText().length();
            ((LinkPath)localObject).setCurrentLayout(paramString.textLayout, k, 0.0F);
            paramString.textLayout.getSelectionPath(k, j - paramString.charactersOffset, (Path)localObject);
            if (j >= paramString.charactersOffset + m)
              i += 1;
            while (true)
            {
              if (i < this.currentMessageObject.textLayoutBlocks.size())
              {
                localObject = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i);
                k = ((MessageObject.TextLayoutBlock)localObject).textLayout.getText().length();
                LinkPath localLinkPath = obtainNewUrlPath(true);
                localLinkPath.setCurrentLayout(((MessageObject.TextLayoutBlock)localObject).textLayout, 0, ((MessageObject.TextLayoutBlock)localObject).height);
                ((MessageObject.TextLayoutBlock)localObject).textLayout.getSelectionPath(0, j - ((MessageObject.TextLayoutBlock)localObject).charactersOffset, localLinkPath);
                m = paramString.charactersOffset;
                if (j >= m + k - 1);
              }
              else
              {
                invalidate();
                return;
              }
              i += 1;
            }
          }
          catch (Exception paramString)
          {
            while (true)
              FileLog.e(paramString);
          }
        }
        i += 1;
      }
    }
  }

  // ERROR //
  public void setMessageObject(MessageObject paramMessageObject, boolean paramBoolean1, boolean paramBoolean2)
  {
    // Byte code:
    //   0: aload_1
    //   1: invokevirtual 2574	org/vidogram/messenger/MessageObject:checkLayout	()Z
    //   4: ifeq +8 -> 12
    //   7: aload_0
    //   8: aconst_null
    //   9: putfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   12: aload_0
    //   13: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   16: ifnull +17 -> 33
    //   19: aload_0
    //   20: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   23: invokevirtual 2695	org/vidogram/messenger/MessageObject:getId	()I
    //   26: aload_1
    //   27: invokevirtual 2695	org/vidogram/messenger/MessageObject:getId	()I
    //   30: if_icmpeq +1812 -> 1842
    //   33: iconst_1
    //   34: istore 21
    //   36: aload_0
    //   37: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   40: aload_1
    //   41: if_acmpne +10 -> 51
    //   44: aload_1
    //   45: getfield 2698	org/vidogram/messenger/MessageObject:forceUpdate	Z
    //   48: ifeq +1800 -> 1848
    //   51: iconst_1
    //   52: istore 10
    //   54: aload_0
    //   55: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   58: aload_1
    //   59: if_acmpne +1795 -> 1854
    //   62: aload_0
    //   63: invokespecial 2700	org/vidogram/ui/Cells/ChatMessageCell:isUserDataChanged	()Z
    //   66: ifne +10 -> 76
    //   69: aload_0
    //   70: getfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   73: ifeq +1781 -> 1854
    //   76: iconst_1
    //   77: istore 29
    //   79: iload 10
    //   81: ifne +32 -> 113
    //   84: iload 29
    //   86: ifne +27 -> 113
    //   89: aload_0
    //   90: aload_1
    //   91: invokespecial 2702	org/vidogram/ui/Cells/ChatMessageCell:isPhotoDataChanged	(Lorg/vidogram/messenger/MessageObject;)Z
    //   94: ifne +19 -> 113
    //   97: aload_0
    //   98: getfield 1347	org/vidogram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   101: iload_2
    //   102: if_icmpne +11 -> 113
    //   105: aload_0
    //   106: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   109: iload_3
    //   110: if_icmpeq +13146 -> 13256
    //   113: aload_0
    //   114: iload_2
    //   115: putfield 1347	org/vidogram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   118: aload_0
    //   119: iload_3
    //   120: putfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   123: aload_0
    //   124: aload_1
    //   125: putfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   128: aload_0
    //   129: aload_1
    //   130: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   133: getfield 1907	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   136: putfield 1904	org/vidogram/ui/Cells/ChatMessageCell:lastSendState	I
    //   139: aload_0
    //   140: aload_1
    //   141: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   144: getfield 1418	org/vidogram/tgnet/TLRPC$Message:destroyTime	I
    //   147: putfield 1909	org/vidogram/ui/Cells/ChatMessageCell:lastDeleteDate	I
    //   150: aload_0
    //   151: aload_1
    //   152: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   155: getfield 1914	org/vidogram/tgnet/TLRPC$Message:views	I
    //   158: putfield 1911	org/vidogram/ui/Cells/ChatMessageCell:lastViewsCount	I
    //   161: aload_0
    //   162: iconst_0
    //   163: putfield 1845	org/vidogram/ui/Cells/ChatMessageCell:isPressed	Z
    //   166: aload_0
    //   167: iconst_1
    //   168: putfield 271	org/vidogram/ui/Cells/ChatMessageCell:isCheckPressed	Z
    //   171: aload_0
    //   172: iconst_0
    //   173: putfield 1916	org/vidogram/ui/Cells/ChatMessageCell:isAvatarVisible	Z
    //   176: aload_0
    //   177: iconst_0
    //   178: putfield 2273	org/vidogram/ui/Cells/ChatMessageCell:wasLayout	Z
    //   181: aload_0
    //   182: aload_0
    //   183: aload_1
    //   184: invokespecial 2704	org/vidogram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/vidogram/messenger/MessageObject;)Z
    //   187: putfield 2310	org/vidogram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   190: aload_0
    //   191: aconst_null
    //   192: putfield 2179	org/vidogram/ui/Cells/ChatMessageCell:replyNameLayout	Landroid/text/StaticLayout;
    //   195: aload_0
    //   196: aconst_null
    //   197: putfield 1927	org/vidogram/ui/Cells/ChatMessageCell:replyTextLayout	Landroid/text/StaticLayout;
    //   200: aload_0
    //   201: iconst_0
    //   202: putfield 2181	org/vidogram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   205: aload_0
    //   206: iconst_0
    //   207: putfield 2185	org/vidogram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   210: aload_0
    //   211: iconst_0
    //   212: putfield 2073	org/vidogram/ui/Cells/ChatMessageCell:viaWidth	I
    //   215: aload_0
    //   216: iconst_0
    //   217: putfield 2087	org/vidogram/ui/Cells/ChatMessageCell:viaNameWidth	I
    //   220: aload_0
    //   221: aconst_null
    //   222: putfield 1943	org/vidogram/ui/Cells/ChatMessageCell:currentReplyPhoto	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   225: aload_0
    //   226: aconst_null
    //   227: putfield 1900	org/vidogram/ui/Cells/ChatMessageCell:currentUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   230: aload_0
    //   231: aconst_null
    //   232: putfield 1902	org/vidogram/ui/Cells/ChatMessageCell:currentChat	Lorg/vidogram/tgnet/TLRPC$Chat;
    //   235: aload_0
    //   236: aconst_null
    //   237: putfield 2075	org/vidogram/ui/Cells/ChatMessageCell:currentViaBotUser	Lorg/vidogram/tgnet/TLRPC$User;
    //   240: aload_0
    //   241: iconst_0
    //   242: putfield 2077	org/vidogram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   245: aload_0
    //   246: iconst_m1
    //   247: invokespecial 597	org/vidogram/ui/Cells/ChatMessageCell:resetPressedLink	(I)V
    //   250: aload_1
    //   251: iconst_0
    //   252: putfield 2698	org/vidogram/messenger/MessageObject:forceUpdate	Z
    //   255: aload_0
    //   256: iconst_0
    //   257: putfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   260: aload_0
    //   261: iconst_0
    //   262: putfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   265: aload_0
    //   266: iconst_0
    //   267: putfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   270: aload_0
    //   271: iconst_0
    //   272: putfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   275: aload_0
    //   276: iconst_0
    //   277: putfield 662	org/vidogram/ui/Cells/ChatMessageCell:instantPressed	Z
    //   280: aload_0
    //   281: iconst_0
    //   282: putfield 522	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewPressed	Z
    //   285: aload_0
    //   286: iconst_0
    //   287: putfield 446	org/vidogram/ui/Cells/ChatMessageCell:buttonPressed	I
    //   290: aload_0
    //   291: iconst_m1
    //   292: putfield 494	org/vidogram/ui/Cells/ChatMessageCell:pressedBotButton	I
    //   295: aload_0
    //   296: iconst_0
    //   297: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   300: aload_0
    //   301: iconst_0
    //   302: putfield 442	org/vidogram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   305: aload_0
    //   306: iconst_0
    //   307: putfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   310: aload_0
    //   311: aconst_null
    //   312: putfield 913	org/vidogram/ui/Cells/ChatMessageCell:documentAttach	Lorg/vidogram/tgnet/TLRPC$Document;
    //   315: aload_0
    //   316: aconst_null
    //   317: putfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   320: aload_0
    //   321: aconst_null
    //   322: putfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   325: aload_0
    //   326: aconst_null
    //   327: putfield 1364	org/vidogram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   330: aload_0
    //   331: aconst_null
    //   332: putfield 1263	org/vidogram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   335: aload_0
    //   336: aconst_null
    //   337: putfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   340: aload_0
    //   341: aconst_null
    //   342: putfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   345: aload_0
    //   346: aconst_null
    //   347: putfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   350: aload_0
    //   351: iconst_0
    //   352: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   355: aload_0
    //   356: aconst_null
    //   357: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   360: aload_0
    //   361: aconst_null
    //   362: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   365: aload_0
    //   366: aconst_null
    //   367: putfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   370: aload_0
    //   371: aconst_null
    //   372: putfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   375: aload_0
    //   376: iconst_0
    //   377: putfield 1161	org/vidogram/ui/Cells/ChatMessageCell:cancelLoading	Z
    //   380: aload_0
    //   381: iconst_m1
    //   382: putfield 435	org/vidogram/ui/Cells/ChatMessageCell:buttonState	I
    //   385: aload_0
    //   386: aconst_null
    //   387: putfield 1850	org/vidogram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   390: aload_0
    //   391: iconst_0
    //   392: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   395: aload_0
    //   396: iconst_1
    //   397: putfield 273	org/vidogram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   400: aload_0
    //   401: iconst_0
    //   402: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   405: aload_0
    //   406: iconst_0
    //   407: putfield 397	org/vidogram/ui/Cells/ChatMessageCell:useSeekBarWaweform	Z
    //   410: aload_0
    //   411: iconst_0
    //   412: putfield 660	org/vidogram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   415: aload_0
    //   416: iconst_0
    //   417: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   420: aload_0
    //   421: iconst_0
    //   422: putfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   425: aload_0
    //   426: iconst_0
    //   427: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   430: aload_0
    //   431: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   434: iconst_0
    //   435: invokevirtual 1126	org/vidogram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   438: aload_0
    //   439: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   442: iconst_0
    //   443: invokevirtual 1129	org/vidogram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   446: aload_0
    //   447: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   450: aconst_null
    //   451: invokevirtual 1132	org/vidogram/messenger/ImageReceiver:setParentMessageObject	(Lorg/vidogram/messenger/MessageObject;)V
    //   454: aload_0
    //   455: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   458: ldc_w 1284
    //   461: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   464: invokevirtual 299	org/vidogram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   467: iload 10
    //   469: ifeq +18 -> 487
    //   472: aload_0
    //   473: iconst_0
    //   474: putfield 1270	org/vidogram/ui/Cells/ChatMessageCell:firstVisibleBlockNum	I
    //   477: aload_0
    //   478: iconst_0
    //   479: putfield 1272	org/vidogram/ui/Cells/ChatMessageCell:lastVisibleBlockNum	I
    //   482: aload_0
    //   483: iconst_1
    //   484: putfield 1203	org/vidogram/ui/Cells/ChatMessageCell:needNewVisiblePart	Z
    //   487: aload_1
    //   488: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   491: ifne +6715 -> 7206
    //   494: aload_0
    //   495: iconst_1
    //   496: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   499: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   502: ifeq +1405 -> 1907
    //   505: aload_0
    //   506: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   509: ifeq +1351 -> 1860
    //   512: aload_1
    //   513: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   516: ifne +1344 -> 1860
    //   519: aload_1
    //   520: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   523: ifeq +1337 -> 1860
    //   526: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   529: istore 10
    //   531: ldc_w 2705
    //   534: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   537: istore 11
    //   539: aload_0
    //   540: iconst_1
    //   541: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   544: iload 10
    //   546: iload 11
    //   548: isub
    //   549: istore 12
    //   551: aload_0
    //   552: iload 12
    //   554: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   557: aload_0
    //   558: aload_1
    //   559: invokespecial 955	org/vidogram/ui/Cells/ChatMessageCell:measureTime	(Lorg/vidogram/messenger/MessageObject;)V
    //   562: aload_0
    //   563: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   566: ldc_w 1323
    //   569: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   572: iadd
    //   573: istore 22
    //   575: aload_1
    //   576: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   579: ifeq +12982 -> 13561
    //   582: iload 22
    //   584: ldc_w 2428
    //   587: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   590: iadd
    //   591: istore 22
    //   593: aload_1
    //   594: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   597: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   600: instanceof 787
    //   603: ifeq +1432 -> 2035
    //   606: aload_1
    //   607: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   610: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   613: getfield 2161	org/vidogram/tgnet/TLRPC$MessageMedia:game	Lorg/vidogram/tgnet/TLRPC$TL_game;
    //   616: instanceof 2163
    //   619: ifeq +1416 -> 2035
    //   622: iconst_1
    //   623: istore_2
    //   624: aload_0
    //   625: iload_2
    //   626: putfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   629: aload_0
    //   630: aload_1
    //   631: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   634: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   637: instanceof 789
    //   640: putfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   643: aload_1
    //   644: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   647: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   650: instanceof 760
    //   653: ifeq +1387 -> 2040
    //   656: aload_1
    //   657: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   660: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   663: getfield 678	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   666: instanceof 762
    //   669: ifeq +1371 -> 2040
    //   672: iconst_1
    //   673: istore_2
    //   674: aload_0
    //   675: iload_2
    //   676: putfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   679: getstatic 720	android/os/Build$VERSION:SDK_INT	I
    //   682: bipush 16
    //   684: if_icmplt +1361 -> 2045
    //   687: aload_0
    //   688: getfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   691: ifeq +1354 -> 2045
    //   694: aload_1
    //   695: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   698: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   701: getfield 678	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   704: getfield 2709	org/vidogram/tgnet/TLRPC$WebPage:cached_page	Lorg/vidogram/tgnet/TLRPC$Page;
    //   707: ifnull +1338 -> 2045
    //   710: iconst_1
    //   711: istore_2
    //   712: aload_0
    //   713: iload_2
    //   714: putfield 660	org/vidogram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   717: aload_0
    //   718: iload 12
    //   720: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   723: aload_0
    //   724: getfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   727: ifne +29 -> 756
    //   730: aload_0
    //   731: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   734: ifne +22 -> 756
    //   737: aload_0
    //   738: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   741: ifne +15 -> 756
    //   744: iload 12
    //   746: aload_1
    //   747: getfield 367	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   750: isub
    //   751: iload 22
    //   753: if_icmpge +1297 -> 2050
    //   756: aload_0
    //   757: aload_0
    //   758: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   761: aload_1
    //   762: getfield 367	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   765: invokestatic 379	java/lang/Math:max	(II)I
    //   768: ldc_w 380
    //   771: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   774: iadd
    //   775: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   778: aload_0
    //   779: aload_0
    //   780: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   783: aload_0
    //   784: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   787: ldc_w 380
    //   790: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   793: iadd
    //   794: invokestatic 379	java/lang/Math:max	(II)I
    //   797: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   800: aload_0
    //   801: aload_0
    //   802: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   805: ldc_w 380
    //   808: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   811: isub
    //   812: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   815: aload_0
    //   816: aload_1
    //   817: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   820: aload_1
    //   821: getfield 879	org/vidogram/messenger/MessageObject:textWidth	I
    //   824: istore 11
    //   826: aload_0
    //   827: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   830: ifne +10 -> 840
    //   833: aload_0
    //   834: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   837: ifeq +1288 -> 2125
    //   840: ldc_w 472
    //   843: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   846: istore 10
    //   848: aload_0
    //   849: iload 10
    //   851: iload 11
    //   853: iadd
    //   854: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   857: aload_0
    //   858: aload_1
    //   859: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   862: ldc_w 2712
    //   865: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   868: iadd
    //   869: aload_0
    //   870: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   873: iadd
    //   874: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   877: aload_0
    //   878: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   881: ifeq +16 -> 897
    //   884: aload_0
    //   885: aload_0
    //   886: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   889: fconst_1
    //   890: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   893: isub
    //   894: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   897: aload_0
    //   898: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   901: aload_0
    //   902: getfield 2081	org/vidogram/ui/Cells/ChatMessageCell:nameWidth	I
    //   905: invokestatic 379	java/lang/Math:max	(II)I
    //   908: aload_0
    //   909: getfield 2123	org/vidogram/ui/Cells/ChatMessageCell:forwardedNameWidth	I
    //   912: invokestatic 379	java/lang/Math:max	(II)I
    //   915: aload_0
    //   916: getfield 2181	org/vidogram/ui/Cells/ChatMessageCell:replyNameWidth	I
    //   919: invokestatic 379	java/lang/Math:max	(II)I
    //   922: aload_0
    //   923: getfield 2185	org/vidogram/ui/Cells/ChatMessageCell:replyTextWidth	I
    //   926: invokestatic 379	java/lang/Math:max	(II)I
    //   929: istore 11
    //   931: iconst_0
    //   932: istore 15
    //   934: aload_0
    //   935: getfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   938: ifne +17 -> 955
    //   941: aload_0
    //   942: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   945: ifne +10 -> 955
    //   948: aload_0
    //   949: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   952: ifeq +6230 -> 7182
    //   955: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   958: ifeq +1188 -> 2146
    //   961: aload_1
    //   962: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   965: ifeq +1166 -> 2131
    //   968: aload_0
    //   969: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   972: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   975: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   978: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   981: ifne +19 -> 1000
    //   984: aload_0
    //   985: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   988: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   991: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   994: getfield 2715	org/vidogram/tgnet/TLRPC$Peer:chat_id	I
    //   997: ifeq +1134 -> 2131
    //   1000: aload_0
    //   1001: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1004: invokevirtual 753	org/vidogram/messenger/MessageObject:isOut	()Z
    //   1007: ifne +1124 -> 2131
    //   1010: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1013: ldc_w 2705
    //   1016: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1019: isub
    //   1020: istore 10
    //   1022: aload_0
    //   1023: getfield 2310	org/vidogram/ui/Cells/ChatMessageCell:drawShareButton	Z
    //   1026: ifeq +12532 -> 13558
    //   1029: iload 10
    //   1031: ldc_w 836
    //   1034: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1037: isub
    //   1038: istore 10
    //   1040: aload_0
    //   1041: getfield 356	org/vidogram/ui/Cells/ChatMessageCell:hasLinkPreview	Z
    //   1044: ifeq +1216 -> 2260
    //   1047: aload_1
    //   1048: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1051: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1054: getfield 678	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   1057: checkcast 762	org/vidogram/tgnet/TLRPC$TL_webPage
    //   1060: astore 30
    //   1062: aload 30
    //   1064: getfield 2716	org/vidogram/tgnet/TLRPC$TL_webPage:site_name	Ljava/lang/String;
    //   1067: astore 33
    //   1069: aload 30
    //   1071: getfield 2717	org/vidogram/tgnet/TLRPC$TL_webPage:title	Ljava/lang/String;
    //   1074: astore 37
    //   1076: aload 30
    //   1078: getfield 2720	org/vidogram/tgnet/TLRPC$TL_webPage:author	Ljava/lang/String;
    //   1081: astore 31
    //   1083: aload 30
    //   1085: getfield 2721	org/vidogram/tgnet/TLRPC$TL_webPage:description	Ljava/lang/String;
    //   1088: astore 36
    //   1090: aload 30
    //   1092: getfield 2724	org/vidogram/tgnet/TLRPC$TL_webPage:photo	Lorg/vidogram/tgnet/TLRPC$Photo;
    //   1095: astore 35
    //   1097: aload 30
    //   1099: getfield 2725	org/vidogram/tgnet/TLRPC$TL_webPage:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   1102: astore 34
    //   1104: aload 30
    //   1106: getfield 2727	org/vidogram/tgnet/TLRPC$TL_webPage:type	Ljava/lang/String;
    //   1109: astore 32
    //   1111: aload 30
    //   1113: getfield 2728	org/vidogram/tgnet/TLRPC$TL_webPage:duration	I
    //   1116: istore 16
    //   1118: iload 10
    //   1120: istore 13
    //   1122: aload 33
    //   1124: ifnull +50 -> 1174
    //   1127: iload 10
    //   1129: istore 13
    //   1131: aload 35
    //   1133: ifnull +41 -> 1174
    //   1136: iload 10
    //   1138: istore 13
    //   1140: aload 33
    //   1142: invokevirtual 1046	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   1145: ldc_w 2730
    //   1148: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1151: ifeq +23 -> 1174
    //   1154: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1157: getfield 1839	android/graphics/Point:y	I
    //   1160: iconst_3
    //   1161: idiv
    //   1162: aload_0
    //   1163: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1166: getfield 879	org/vidogram/messenger/MessageObject:textWidth	I
    //   1169: invokestatic 379	java/lang/Math:max	(II)I
    //   1172: istore 13
    //   1174: aload_0
    //   1175: getfield 660	org/vidogram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1178: ifne +1071 -> 2249
    //   1181: aload 32
    //   1183: ifnull +1066 -> 2249
    //   1186: aload 32
    //   1188: ldc_w 2732
    //   1191: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1194: ifne +25 -> 1219
    //   1197: aload 32
    //   1199: ldc_w 2734
    //   1202: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1205: ifne +14 -> 1219
    //   1208: aload 32
    //   1210: ldc_w 2736
    //   1213: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1216: ifeq +1033 -> 2249
    //   1219: iconst_1
    //   1220: istore 10
    //   1222: aload_0
    //   1223: getfield 660	org/vidogram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   1226: ifne +1029 -> 2255
    //   1229: aload 36
    //   1231: ifnull +1024 -> 2255
    //   1234: aload 32
    //   1236: ifnull +1019 -> 2255
    //   1239: aload 32
    //   1241: ldc_w 2732
    //   1244: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1247: ifne +25 -> 1272
    //   1250: aload 32
    //   1252: ldc_w 2734
    //   1255: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1258: ifne +14 -> 1272
    //   1261: aload 32
    //   1263: ldc_w 2736
    //   1266: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   1269: ifeq +986 -> 2255
    //   1272: aload_0
    //   1273: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1276: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1279: ifnull +976 -> 2255
    //   1282: iconst_1
    //   1283: istore_2
    //   1284: aload_0
    //   1285: iload_2
    //   1286: putfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1289: aconst_null
    //   1290: astore 30
    //   1292: iload 10
    //   1294: istore 14
    //   1296: iload 13
    //   1298: istore 10
    //   1300: aload_0
    //   1301: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   1304: ifeq +1116 -> 2420
    //   1307: iconst_0
    //   1308: istore 23
    //   1310: iconst_3
    //   1311: istore 24
    //   1313: iload 10
    //   1315: iload 23
    //   1317: isub
    //   1318: istore 27
    //   1320: aload_0
    //   1321: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1324: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   1327: ifnonnull +16 -> 1343
    //   1330: aload 35
    //   1332: ifnull +11 -> 1343
    //   1335: aload_0
    //   1336: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   1339: iconst_1
    //   1340: invokevirtual 2739	org/vidogram/messenger/MessageObject:generateThumbs	(Z)V
    //   1343: aload 33
    //   1345: ifnull +12199 -> 13544
    //   1348: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1351: aload 33
    //   1353: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   1356: f2d
    //   1357: invokestatic 949	java/lang/Math:ceil	(D)D
    //   1360: d2i
    //   1361: istore 10
    //   1363: aload_0
    //   1364: new 277	android/text/StaticLayout
    //   1367: dup
    //   1368: aload 33
    //   1370: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1373: iload 10
    //   1375: iload 27
    //   1377: invokestatic 959	java/lang/Math:min	(II)I
    //   1380: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1383: fconst_1
    //   1384: fconst_0
    //   1385: iconst_0
    //   1386: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   1389: putfield 1263	org/vidogram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1392: aload_0
    //   1393: getfield 1263	org/vidogram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1396: aload_0
    //   1397: getfield 1263	org/vidogram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1400: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   1403: iconst_1
    //   1404: isub
    //   1405: invokevirtual 1266	android/text/StaticLayout:getLineBottom	(I)I
    //   1408: istore 10
    //   1410: aload_0
    //   1411: aload_0
    //   1412: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1415: iload 10
    //   1417: iadd
    //   1418: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1421: aload_0
    //   1422: aload_0
    //   1423: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1426: iload 10
    //   1428: iadd
    //   1429: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1432: iconst_0
    //   1433: iload 10
    //   1435: iadd
    //   1436: istore 13
    //   1438: aload_0
    //   1439: getfield 1263	org/vidogram/ui/Cells/ChatMessageCell:siteNameLayout	Landroid/text/StaticLayout;
    //   1442: invokevirtual 2742	android/text/StaticLayout:getWidth	()I
    //   1445: istore 17
    //   1447: iload 11
    //   1449: iload 17
    //   1451: iload 23
    //   1453: iadd
    //   1454: invokestatic 379	java/lang/Math:max	(II)I
    //   1457: istore 10
    //   1459: iconst_0
    //   1460: iload 17
    //   1462: iload 23
    //   1464: iadd
    //   1465: invokestatic 379	java/lang/Math:max	(II)I
    //   1468: istore 11
    //   1470: iload 13
    //   1472: istore 17
    //   1474: aload 37
    //   1476: ifnull +12051 -> 13527
    //   1479: aload_0
    //   1480: ldc_w 2743
    //   1483: putfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   1486: aload_0
    //   1487: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1490: ifeq +29 -> 1519
    //   1493: aload_0
    //   1494: aload_0
    //   1495: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1498: fconst_2
    //   1499: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1502: iadd
    //   1503: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1506: aload_0
    //   1507: aload_0
    //   1508: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1511: fconst_2
    //   1512: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1515: iadd
    //   1516: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1519: aload_0
    //   1520: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1523: ifeq +8 -> 1531
    //   1526: aload 36
    //   1528: ifnonnull +932 -> 2460
    //   1531: aload_0
    //   1532: aload 37
    //   1534: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   1537: iload 27
    //   1539: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   1542: fconst_1
    //   1543: fconst_1
    //   1544: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1547: i2f
    //   1548: iconst_0
    //   1549: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   1552: iload 27
    //   1554: iconst_4
    //   1555: invokestatic 1091	org/vidogram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   1558: putfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1561: iconst_3
    //   1562: istore 13
    //   1564: iconst_0
    //   1565: istore 24
    //   1567: aload_0
    //   1568: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1571: aload_0
    //   1572: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1575: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   1578: iconst_1
    //   1579: isub
    //   1580: invokevirtual 1266	android/text/StaticLayout:getLineBottom	(I)I
    //   1583: istore 15
    //   1585: aload_0
    //   1586: aload_0
    //   1587: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1590: iload 15
    //   1592: iadd
    //   1593: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   1596: aload_0
    //   1597: iload 15
    //   1599: aload_0
    //   1600: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1603: iadd
    //   1604: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   1607: iconst_0
    //   1608: istore 25
    //   1610: iconst_0
    //   1611: istore 18
    //   1613: iload 11
    //   1615: istore 15
    //   1617: iload 10
    //   1619: istore 11
    //   1621: iload 15
    //   1623: istore 10
    //   1625: iload 18
    //   1627: istore 15
    //   1629: iload 15
    //   1631: istore 18
    //   1633: iload 11
    //   1635: istore 19
    //   1637: iload 25
    //   1639: aload_0
    //   1640: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1643: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   1646: if_icmpge +1367 -> 3013
    //   1649: iload 15
    //   1651: istore 18
    //   1653: iload 11
    //   1655: istore 19
    //   1657: aload_0
    //   1658: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1661: iload 25
    //   1663: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   1666: f2i
    //   1667: istore 28
    //   1669: iload 28
    //   1671: ifeq +6 -> 1677
    //   1674: iconst_1
    //   1675: istore 15
    //   1677: iload 15
    //   1679: istore 18
    //   1681: iload 11
    //   1683: istore 19
    //   1685: aload_0
    //   1686: getfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   1689: ldc_w 2743
    //   1692: if_icmpne +811 -> 2503
    //   1695: iload 15
    //   1697: istore 18
    //   1699: iload 11
    //   1701: istore 19
    //   1703: aload_0
    //   1704: iload 28
    //   1706: ineg
    //   1707: putfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   1710: iload 28
    //   1712: ifeq +1270 -> 2982
    //   1715: iload 15
    //   1717: istore 18
    //   1719: iload 11
    //   1721: istore 19
    //   1723: aload_0
    //   1724: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   1727: invokevirtual 2742	android/text/StaticLayout:getWidth	()I
    //   1730: iload 28
    //   1732: isub
    //   1733: istore 20
    //   1735: iload 25
    //   1737: iload 24
    //   1739: if_icmplt +31 -> 1770
    //   1742: iload 20
    //   1744: istore 26
    //   1746: iload 28
    //   1748: ifeq +41 -> 1789
    //   1751: iload 20
    //   1753: istore 26
    //   1755: iload 15
    //   1757: istore 18
    //   1759: iload 11
    //   1761: istore 19
    //   1763: aload_0
    //   1764: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   1767: ifeq +22 -> 1789
    //   1770: iload 15
    //   1772: istore 18
    //   1774: iload 11
    //   1776: istore 19
    //   1778: iload 20
    //   1780: ldc_w 2744
    //   1783: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1786: iadd
    //   1787: istore 26
    //   1789: iload 15
    //   1791: istore 18
    //   1793: iload 11
    //   1795: istore 19
    //   1797: iload 11
    //   1799: iload 26
    //   1801: iload 23
    //   1803: iadd
    //   1804: invokestatic 379	java/lang/Math:max	(II)I
    //   1807: istore 11
    //   1809: iload 15
    //   1811: istore 18
    //   1813: iload 11
    //   1815: istore 19
    //   1817: iload 10
    //   1819: iload 26
    //   1821: iload 23
    //   1823: iadd
    //   1824: invokestatic 379	java/lang/Math:max	(II)I
    //   1827: istore 20
    //   1829: iload 20
    //   1831: istore 10
    //   1833: iload 25
    //   1835: iconst_1
    //   1836: iadd
    //   1837: istore 25
    //   1839: goto -210 -> 1629
    //   1842: iconst_0
    //   1843: istore 21
    //   1845: goto -1809 -> 36
    //   1848: iconst_0
    //   1849: istore 10
    //   1851: goto -1797 -> 54
    //   1854: iconst_0
    //   1855: istore 29
    //   1857: goto -1778 -> 79
    //   1860: aload_1
    //   1861: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1864: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   1867: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   1870: ifeq +32 -> 1902
    //   1873: aload_1
    //   1874: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   1877: ifne +25 -> 1902
    //   1880: iconst_1
    //   1881: istore_2
    //   1882: aload_0
    //   1883: iload_2
    //   1884: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   1887: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   1890: ldc_w 2745
    //   1893: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1896: isub
    //   1897: istore 12
    //   1899: goto -1348 -> 551
    //   1902: iconst_0
    //   1903: istore_2
    //   1904: goto -22 -> 1882
    //   1907: aload_0
    //   1908: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   1911: ifeq +57 -> 1968
    //   1914: aload_1
    //   1915: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   1918: ifne +50 -> 1968
    //   1921: aload_1
    //   1922: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   1925: ifeq +43 -> 1968
    //   1928: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1931: getfield 1836	android/graphics/Point:x	I
    //   1934: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1937: getfield 1839	android/graphics/Point:y	I
    //   1940: invokestatic 959	java/lang/Math:min	(II)I
    //   1943: istore 10
    //   1945: ldc_w 2705
    //   1948: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1951: istore 11
    //   1953: aload_0
    //   1954: iconst_1
    //   1955: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   1958: iload 10
    //   1960: iload 11
    //   1962: isub
    //   1963: istore 12
    //   1965: goto -1414 -> 551
    //   1968: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1971: getfield 1836	android/graphics/Point:x	I
    //   1974: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   1977: getfield 1839	android/graphics/Point:y	I
    //   1980: invokestatic 959	java/lang/Math:min	(II)I
    //   1983: istore 10
    //   1985: ldc_w 2745
    //   1988: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   1991: istore 11
    //   1993: aload_1
    //   1994: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   1997: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2000: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   2003: ifeq +27 -> 2030
    //   2006: aload_1
    //   2007: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   2010: ifne +20 -> 2030
    //   2013: iconst_1
    //   2014: istore_2
    //   2015: aload_0
    //   2016: iload_2
    //   2017: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   2020: iload 10
    //   2022: iload 11
    //   2024: isub
    //   2025: istore 12
    //   2027: goto -1476 -> 551
    //   2030: iconst_0
    //   2031: istore_2
    //   2032: goto -17 -> 2015
    //   2035: iconst_0
    //   2036: istore_2
    //   2037: goto -1413 -> 624
    //   2040: iconst_0
    //   2041: istore_2
    //   2042: goto -1368 -> 674
    //   2045: iconst_0
    //   2046: istore_2
    //   2047: goto -1335 -> 712
    //   2050: aload_0
    //   2051: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2054: aload_1
    //   2055: getfield 367	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   2058: isub
    //   2059: istore 10
    //   2061: iload 10
    //   2063: iflt +34 -> 2097
    //   2066: iload 10
    //   2068: iload 22
    //   2070: if_icmpgt +27 -> 2097
    //   2073: aload_0
    //   2074: aload_0
    //   2075: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2078: iload 22
    //   2080: iadd
    //   2081: iload 10
    //   2083: isub
    //   2084: ldc_w 380
    //   2087: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2090: iadd
    //   2091: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2094: goto -1294 -> 800
    //   2097: aload_0
    //   2098: aload_0
    //   2099: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2102: aload_1
    //   2103: getfield 367	org/vidogram/messenger/MessageObject:lastLineWidth	I
    //   2106: iload 22
    //   2108: iadd
    //   2109: invokestatic 379	java/lang/Math:max	(II)I
    //   2112: ldc_w 380
    //   2115: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2118: iadd
    //   2119: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   2122: goto -1322 -> 800
    //   2125: iconst_0
    //   2126: istore 10
    //   2128: goto -1280 -> 848
    //   2131: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   2134: ldc_w 2745
    //   2137: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2140: isub
    //   2141: istore 10
    //   2143: goto -1121 -> 1022
    //   2146: aload_1
    //   2147: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   2150: ifeq +72 -> 2222
    //   2153: aload_0
    //   2154: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2157: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2160: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2163: getfield 799	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   2166: ifne +19 -> 2185
    //   2169: aload_0
    //   2170: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2173: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2176: getfield 796	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2179: getfield 2715	org/vidogram/tgnet/TLRPC$Peer:chat_id	I
    //   2182: ifeq +40 -> 2222
    //   2185: aload_0
    //   2186: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2189: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   2192: ifne +30 -> 2222
    //   2195: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2198: getfield 1836	android/graphics/Point:x	I
    //   2201: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2204: getfield 1839	android/graphics/Point:y	I
    //   2207: invokestatic 959	java/lang/Math:min	(II)I
    //   2210: ldc_w 2705
    //   2213: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2216: isub
    //   2217: istore 10
    //   2219: goto -1197 -> 1022
    //   2222: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2225: getfield 1836	android/graphics/Point:x	I
    //   2228: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   2231: getfield 1839	android/graphics/Point:y	I
    //   2234: invokestatic 959	java/lang/Math:min	(II)I
    //   2237: ldc_w 2745
    //   2240: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2243: isub
    //   2244: istore 10
    //   2246: goto -1224 -> 1022
    //   2249: iconst_0
    //   2250: istore 10
    //   2252: goto -1030 -> 1222
    //   2255: iconst_0
    //   2256: istore_2
    //   2257: goto -973 -> 1284
    //   2260: aload_0
    //   2261: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   2264: ifeq +64 -> 2328
    //   2267: aload_1
    //   2268: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2271: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2274: getfield 2212	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   2277: astore 33
    //   2279: aload_1
    //   2280: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2283: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2286: checkcast 789	org/vidogram/tgnet/TLRPC$TL_messageMediaInvoice
    //   2289: getfield 2748	org/vidogram/tgnet/TLRPC$TL_messageMediaInvoice:photo	Lorg/vidogram/tgnet/TLRPC$TL_webDocument;
    //   2292: astore 30
    //   2294: aload_0
    //   2295: iconst_0
    //   2296: putfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2299: ldc_w 2750
    //   2302: astore 32
    //   2304: iconst_0
    //   2305: istore 14
    //   2307: iconst_0
    //   2308: istore 16
    //   2310: aconst_null
    //   2311: astore 34
    //   2313: aconst_null
    //   2314: astore 35
    //   2316: aconst_null
    //   2317: astore 36
    //   2319: aconst_null
    //   2320: astore 31
    //   2322: aconst_null
    //   2323: astore 37
    //   2325: goto -1025 -> 1300
    //   2328: aload_1
    //   2329: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   2332: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2335: getfield 2161	org/vidogram/tgnet/TLRPC$MessageMedia:game	Lorg/vidogram/tgnet/TLRPC$TL_game;
    //   2338: astore 31
    //   2340: aload 31
    //   2342: getfield 2164	org/vidogram/tgnet/TLRPC$TL_game:title	Ljava/lang/String;
    //   2345: astore 33
    //   2347: aload_1
    //   2348: getfield 876	org/vidogram/messenger/MessageObject:messageText	Ljava/lang/CharSequence;
    //   2351: invokestatic 695	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   2354: ifeq +60 -> 2414
    //   2357: aload 31
    //   2359: getfield 2751	org/vidogram/tgnet/TLRPC$TL_game:description	Ljava/lang/String;
    //   2362: astore 30
    //   2364: aload 31
    //   2366: getfield 2752	org/vidogram/tgnet/TLRPC$TL_game:photo	Lorg/vidogram/tgnet/TLRPC$Photo;
    //   2369: astore 35
    //   2371: aload 31
    //   2373: getfield 2753	org/vidogram/tgnet/TLRPC$TL_game:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   2376: astore 34
    //   2378: aload_0
    //   2379: iconst_0
    //   2380: putfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2383: ldc_w 2754
    //   2386: astore 32
    //   2388: iconst_0
    //   2389: istore 14
    //   2391: iconst_0
    //   2392: istore 16
    //   2394: aconst_null
    //   2395: astore 38
    //   2397: aload 30
    //   2399: astore 36
    //   2401: aconst_null
    //   2402: astore 31
    //   2404: aconst_null
    //   2405: astore 37
    //   2407: aload 38
    //   2409: astore 30
    //   2411: goto -1111 -> 1300
    //   2414: aconst_null
    //   2415: astore 30
    //   2417: goto -53 -> 2364
    //   2420: ldc_w 472
    //   2423: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2426: istore 23
    //   2428: goto -1118 -> 1310
    //   2431: astore 38
    //   2433: iconst_0
    //   2434: istore 13
    //   2436: iload 11
    //   2438: istore 10
    //   2440: iload 13
    //   2442: istore 11
    //   2444: aload 38
    //   2446: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2449: iload 11
    //   2451: istore 17
    //   2453: iload 15
    //   2455: istore 11
    //   2457: goto -983 -> 1474
    //   2460: aload_0
    //   2461: aload 37
    //   2463: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2466: iload 27
    //   2468: iload 27
    //   2470: ldc_w 2744
    //   2473: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2476: isub
    //   2477: iconst_3
    //   2478: iconst_4
    //   2479: invokestatic 2756	org/vidogram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   2482: putfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2485: aload_0
    //   2486: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2489: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   2492: istore 13
    //   2494: iconst_3
    //   2495: iload 13
    //   2497: isub
    //   2498: istore 13
    //   2500: goto -933 -> 1567
    //   2503: iload 15
    //   2505: istore 18
    //   2507: iload 11
    //   2509: istore 19
    //   2511: aload_0
    //   2512: aload_0
    //   2513: getfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   2516: iload 28
    //   2518: ineg
    //   2519: invokestatic 379	java/lang/Math:max	(II)I
    //   2522: putfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   2525: goto -815 -> 1710
    //   2528: astore 38
    //   2530: iload 19
    //   2532: istore 15
    //   2534: iload 10
    //   2536: istore 11
    //   2538: iload 18
    //   2540: istore 10
    //   2542: aload 38
    //   2544: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   2547: iload 13
    //   2549: istore 19
    //   2551: iload 10
    //   2553: istore 18
    //   2555: iload 11
    //   2557: istore 13
    //   2559: iload 15
    //   2561: istore 10
    //   2563: iload 19
    //   2565: istore 11
    //   2567: aload 31
    //   2569: ifnull +10936 -> 13505
    //   2572: aload 37
    //   2574: ifnonnull +10931 -> 13505
    //   2577: aload_0
    //   2578: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2581: ifeq +29 -> 2610
    //   2584: aload_0
    //   2585: aload_0
    //   2586: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2589: fconst_2
    //   2590: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2593: iadd
    //   2594: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2597: aload_0
    //   2598: aload_0
    //   2599: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2602: fconst_2
    //   2603: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2606: iadd
    //   2607: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2610: iload 11
    //   2612: iconst_3
    //   2613: if_icmpne +423 -> 3036
    //   2616: aload_0
    //   2617: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2620: ifeq +8 -> 2628
    //   2623: aload 36
    //   2625: ifnonnull +411 -> 3036
    //   2628: aload_0
    //   2629: new 277	android/text/StaticLayout
    //   2632: dup
    //   2633: aload 31
    //   2635: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   2638: iload 27
    //   2640: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2643: fconst_1
    //   2644: fconst_0
    //   2645: iconst_0
    //   2646: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   2649: putfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2652: aload_0
    //   2653: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2656: aload_0
    //   2657: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2660: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   2663: iconst_1
    //   2664: isub
    //   2665: invokevirtual 1266	android/text/StaticLayout:getLineBottom	(I)I
    //   2668: istore 15
    //   2670: aload_0
    //   2671: aload_0
    //   2672: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2675: iload 15
    //   2677: iadd
    //   2678: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2681: aload_0
    //   2682: iload 15
    //   2684: aload_0
    //   2685: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2688: iadd
    //   2689: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2692: aload_0
    //   2693: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2696: iconst_0
    //   2697: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   2700: f2i
    //   2701: istore 15
    //   2703: aload_0
    //   2704: iload 15
    //   2706: ineg
    //   2707: putfield 1336	org/vidogram/ui/Cells/ChatMessageCell:authorX	I
    //   2710: iload 15
    //   2712: ifeq +369 -> 3081
    //   2715: aload_0
    //   2716: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   2719: invokevirtual 2742	android/text/StaticLayout:getWidth	()I
    //   2722: istore 19
    //   2724: iload 19
    //   2726: iload 15
    //   2728: isub
    //   2729: istore 19
    //   2731: iconst_1
    //   2732: istore 15
    //   2734: iload 10
    //   2736: iload 19
    //   2738: iload 23
    //   2740: iadd
    //   2741: invokestatic 379	java/lang/Math:max	(II)I
    //   2744: istore 20
    //   2746: iload 20
    //   2748: istore 10
    //   2750: iload 13
    //   2752: iload 19
    //   2754: iload 23
    //   2756: iadd
    //   2757: invokestatic 379	java/lang/Math:max	(II)I
    //   2760: istore 19
    //   2762: iload 19
    //   2764: istore 13
    //   2766: iload 11
    //   2768: istore 19
    //   2770: iload 13
    //   2772: istore 11
    //   2774: iload 19
    //   2776: istore 13
    //   2778: aload 36
    //   2780: ifnull +10722 -> 13502
    //   2783: aload_0
    //   2784: iconst_0
    //   2785: putfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2788: aload_0
    //   2789: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2792: invokevirtual 2759	org/vidogram/messenger/MessageObject:generateLinkDescription	()V
    //   2795: aload_0
    //   2796: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2799: ifeq +29 -> 2828
    //   2802: aload_0
    //   2803: aload_0
    //   2804: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2807: fconst_2
    //   2808: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2811: iadd
    //   2812: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2815: aload_0
    //   2816: aload_0
    //   2817: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2820: fconst_2
    //   2821: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2824: iadd
    //   2825: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2828: iload 13
    //   2830: iconst_3
    //   2831: if_icmpne +300 -> 3131
    //   2834: aload_0
    //   2835: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   2838: ifne +293 -> 3131
    //   2841: aload_0
    //   2842: aload_1
    //   2843: getfield 617	org/vidogram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   2846: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   2849: iload 27
    //   2851: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   2854: fconst_1
    //   2855: fconst_1
    //   2856: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   2859: i2f
    //   2860: iconst_0
    //   2861: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   2864: iload 27
    //   2866: bipush 6
    //   2868: invokestatic 1091	org/vidogram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   2871: putfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2874: iconst_0
    //   2875: istore 13
    //   2877: aload_0
    //   2878: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2881: aload_0
    //   2882: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2885: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   2888: iconst_1
    //   2889: isub
    //   2890: invokevirtual 1266	android/text/StaticLayout:getLineBottom	(I)I
    //   2893: istore 19
    //   2895: aload_0
    //   2896: aload_0
    //   2897: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2900: iload 19
    //   2902: iadd
    //   2903: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   2906: aload_0
    //   2907: iload 19
    //   2909: aload_0
    //   2910: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2913: iadd
    //   2914: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   2917: iconst_0
    //   2918: istore 19
    //   2920: iconst_0
    //   2921: istore 20
    //   2923: iload 20
    //   2925: aload_0
    //   2926: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2929: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   2932: if_icmpge +2066 -> 4998
    //   2935: aload_0
    //   2936: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   2939: iload 20
    //   2941: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   2944: f2d
    //   2945: invokestatic 949	java/lang/Math:ceil	(D)D
    //   2948: d2i
    //   2949: istore 24
    //   2951: iload 24
    //   2953: ifeq +20 -> 2973
    //   2956: iconst_1
    //   2957: istore 19
    //   2959: aload_0
    //   2960: getfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2963: ifne +200 -> 3163
    //   2966: aload_0
    //   2967: iload 24
    //   2969: ineg
    //   2970: putfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   2973: iload 20
    //   2975: iconst_1
    //   2976: iadd
    //   2977: istore 20
    //   2979: goto -56 -> 2923
    //   2982: iload 15
    //   2984: istore 18
    //   2986: iload 11
    //   2988: istore 19
    //   2990: aload_0
    //   2991: getfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   2994: iload 25
    //   2996: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   2999: f2d
    //   3000: invokestatic 949	java/lang/Math:ceil	(D)D
    //   3003: dstore 4
    //   3005: dload 4
    //   3007: d2i
    //   3008: istore 20
    //   3010: goto -1275 -> 1735
    //   3013: iload 13
    //   3015: istore 18
    //   3017: iload 10
    //   3019: istore 13
    //   3021: iload 11
    //   3023: istore 10
    //   3025: iload 18
    //   3027: istore 11
    //   3029: iload 15
    //   3031: istore 18
    //   3033: goto -466 -> 2567
    //   3036: aload_0
    //   3037: aload 31
    //   3039: getstatic 1293	org/vidogram/ui/ActionBar/Theme:chat_replyNamePaint	Landroid/text/TextPaint;
    //   3042: iload 27
    //   3044: iload 27
    //   3046: ldc_w 2744
    //   3049: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3052: isub
    //   3053: iload 11
    //   3055: iconst_1
    //   3056: invokestatic 2756	org/vidogram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   3059: putfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3062: aload_0
    //   3063: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3066: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   3069: istore 15
    //   3071: iload 11
    //   3073: iload 15
    //   3075: isub
    //   3076: istore 11
    //   3078: goto -426 -> 2652
    //   3081: aload_0
    //   3082: getfield 1334	org/vidogram/ui/Cells/ChatMessageCell:authorLayout	Landroid/text/StaticLayout;
    //   3085: iconst_0
    //   3086: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   3089: f2d
    //   3090: invokestatic 949	java/lang/Math:ceil	(D)D
    //   3093: dstore 4
    //   3095: dload 4
    //   3097: d2i
    //   3098: istore 19
    //   3100: iconst_0
    //   3101: istore 15
    //   3103: goto -369 -> 2734
    //   3106: astore 31
    //   3108: iconst_0
    //   3109: istore 15
    //   3111: aload 31
    //   3113: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3116: iload 11
    //   3118: istore 19
    //   3120: iload 13
    //   3122: istore 11
    //   3124: iload 19
    //   3126: istore 13
    //   3128: goto -350 -> 2778
    //   3131: aload_0
    //   3132: aload_1
    //   3133: getfield 617	org/vidogram/messenger/MessageObject:linkDescription	Ljava/lang/CharSequence;
    //   3136: getstatic 1328	org/vidogram/ui/ActionBar/Theme:chat_replyTextPaint	Landroid/text/TextPaint;
    //   3139: iload 27
    //   3141: iload 27
    //   3143: ldc_w 2744
    //   3146: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3149: isub
    //   3150: iload 13
    //   3152: bipush 6
    //   3154: invokestatic 2756	org/vidogram/ui/Cells/ChatMessageCell:generateStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;IIII)Landroid/text/StaticLayout;
    //   3157: putfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3160: goto -283 -> 2877
    //   3163: aload_0
    //   3164: aload_0
    //   3165: getfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   3168: iload 24
    //   3170: ineg
    //   3171: invokestatic 379	java/lang/Math:max	(II)I
    //   3174: putfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   3177: goto -204 -> 2973
    //   3180: astore 31
    //   3182: aload 31
    //   3184: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   3187: iload 14
    //   3189: istore 13
    //   3191: iload 14
    //   3193: ifeq +44 -> 3237
    //   3196: aload_0
    //   3197: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3200: ifnull +29 -> 3229
    //   3203: iload 14
    //   3205: istore 13
    //   3207: aload_0
    //   3208: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3211: ifnull +26 -> 3237
    //   3214: iload 14
    //   3216: istore 13
    //   3218: aload_0
    //   3219: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   3222: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   3225: iconst_1
    //   3226: if_icmpne +11 -> 3237
    //   3229: iconst_0
    //   3230: istore 13
    //   3232: aload_0
    //   3233: iconst_0
    //   3234: putfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3237: iload 13
    //   3239: ifeq +1987 -> 5226
    //   3242: ldc_w 658
    //   3245: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3248: istore 11
    //   3250: aload 34
    //   3252: ifnull +2923 -> 6175
    //   3255: aload 34
    //   3257: invokestatic 2762	org/vidogram/messenger/MessageObject:isGifDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   3260: ifeq +1987 -> 5247
    //   3263: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   3266: invokevirtual 681	org/vidogram/messenger/MediaController:canAutoplayGifs	()Z
    //   3269: ifne +8 -> 3277
    //   3272: aload_1
    //   3273: fconst_1
    //   3274: putfield 705	org/vidogram/messenger/MessageObject:audioProgress	F
    //   3277: aload_0
    //   3278: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   3281: astore 31
    //   3283: aload_1
    //   3284: getfield 705	org/vidogram/messenger/MessageObject:audioProgress	F
    //   3287: fconst_1
    //   3288: fcmpl
    //   3289: ifeq +1944 -> 5233
    //   3292: iconst_1
    //   3293: istore_2
    //   3294: aload 31
    //   3296: iload_2
    //   3297: invokevirtual 708	org/vidogram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   3300: aload_0
    //   3301: aload 34
    //   3303: getfield 1055	org/vidogram/tgnet/TLRPC$Document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3306: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3309: aload_0
    //   3310: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3313: ifnull +148 -> 3461
    //   3316: aload_0
    //   3317: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3320: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   3323: ifeq +13 -> 3336
    //   3326: aload_0
    //   3327: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3330: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   3333: ifne +128 -> 3461
    //   3336: iconst_0
    //   3337: istore 14
    //   3339: iload 14
    //   3341: aload 34
    //   3343: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3346: invokevirtual 475	java/util/ArrayList:size	()I
    //   3349: if_icmpge +58 -> 3407
    //   3352: aload 34
    //   3354: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3357: iload 14
    //   3359: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3362: checkcast 925	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   3365: astore 31
    //   3367: aload 31
    //   3369: instanceof 2770
    //   3372: ifne +11 -> 3383
    //   3375: aload 31
    //   3377: instanceof 1025
    //   3380: ifeq +1858 -> 5238
    //   3383: aload_0
    //   3384: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3387: aload 31
    //   3389: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   3392: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   3395: aload_0
    //   3396: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3399: aload 31
    //   3401: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   3404: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   3407: aload_0
    //   3408: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3411: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   3414: ifeq +13 -> 3427
    //   3417: aload_0
    //   3418: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3421: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   3424: ifne +37 -> 3461
    //   3427: aload_0
    //   3428: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3431: astore 31
    //   3433: aload_0
    //   3434: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3437: astore 35
    //   3439: ldc_w 2773
    //   3442: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3445: istore 14
    //   3447: aload 35
    //   3449: iload 14
    //   3451: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   3454: aload 31
    //   3456: iload 14
    //   3458: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   3461: aload_0
    //   3462: iconst_2
    //   3463: putfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3466: aload_0
    //   3467: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3470: iconst_5
    //   3471: if_icmpeq +843 -> 4314
    //   3474: aload_0
    //   3475: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3478: iconst_3
    //   3479: if_icmpeq +835 -> 4314
    //   3482: aload_0
    //   3483: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3486: iconst_1
    //   3487: if_icmpeq +827 -> 4314
    //   3490: aload_0
    //   3491: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3494: ifnonnull +8 -> 3502
    //   3497: aload 30
    //   3499: ifnull +3566 -> 7065
    //   3502: aload 32
    //   3504: ifnull +2833 -> 6337
    //   3507: aload 32
    //   3509: ldc_w 2774
    //   3512: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3515: ifne +42 -> 3557
    //   3518: aload 32
    //   3520: ldc_w 2775
    //   3523: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3526: ifeq +12 -> 3538
    //   3529: aload_0
    //   3530: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3533: bipush 6
    //   3535: if_icmpne +22 -> 3557
    //   3538: aload 32
    //   3540: ldc_w 2777
    //   3543: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3546: ifne +11 -> 3557
    //   3549: aload_0
    //   3550: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3553: iconst_4
    //   3554: if_icmpne +2783 -> 6337
    //   3557: iconst_1
    //   3558: istore_2
    //   3559: aload_0
    //   3560: iload_2
    //   3561: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   3564: aload_0
    //   3565: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3568: ifeq +29 -> 3597
    //   3571: aload_0
    //   3572: aload_0
    //   3573: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3576: fconst_2
    //   3577: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3580: iadd
    //   3581: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3584: aload_0
    //   3585: aload_0
    //   3586: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3589: fconst_2
    //   3590: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3593: iadd
    //   3594: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3597: aload_0
    //   3598: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   3601: bipush 6
    //   3603: if_icmpne +20 -> 3623
    //   3606: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   3609: ifeq +2733 -> 6342
    //   3612: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   3615: i2f
    //   3616: ldc_w 2778
    //   3619: fmul
    //   3620: f2i
    //   3621: istore 11
    //   3623: aload_0
    //   3624: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   3627: ifeq +2732 -> 6359
    //   3630: ldc_w 438
    //   3633: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3636: istore 14
    //   3638: iload 10
    //   3640: iload 11
    //   3642: iload 14
    //   3644: isub
    //   3645: iload 23
    //   3647: iadd
    //   3648: invokestatic 379	java/lang/Math:max	(II)I
    //   3651: istore 14
    //   3653: aload_0
    //   3654: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3657: ifnull +2708 -> 6365
    //   3660: aload_0
    //   3661: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3664: iconst_m1
    //   3665: putfield 1168	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   3668: aload_0
    //   3669: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3672: ifnull +11 -> 3683
    //   3675: aload_0
    //   3676: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   3679: iconst_m1
    //   3680: putfield 1168	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   3683: iload 13
    //   3685: ifeq +2689 -> 6374
    //   3688: iload 11
    //   3690: istore 10
    //   3692: aload_0
    //   3693: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   3696: ifeq +2845 -> 6541
    //   3699: ldc_w 833
    //   3702: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3705: iload 17
    //   3707: iadd
    //   3708: aload_0
    //   3709: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3712: if_icmple +46 -> 3758
    //   3715: aload_0
    //   3716: aload_0
    //   3717: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3720: ldc_w 833
    //   3723: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3726: iload 17
    //   3728: iadd
    //   3729: aload_0
    //   3730: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3733: isub
    //   3734: ldc_w 652
    //   3737: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3740: iadd
    //   3741: iadd
    //   3742: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   3745: aload_0
    //   3746: ldc_w 833
    //   3749: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3752: iload 17
    //   3754: iadd
    //   3755: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3758: aload_0
    //   3759: aload_0
    //   3760: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3763: ldc_w 652
    //   3766: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   3769: isub
    //   3770: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   3773: aload_0
    //   3774: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   3777: iconst_0
    //   3778: iconst_0
    //   3779: iload 11
    //   3781: iload 10
    //   3783: invokevirtual 1316	org/vidogram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   3786: aload_0
    //   3787: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   3790: ldc_w 2780
    //   3793: iconst_2
    //   3794: anewarray 1016	java/lang/Object
    //   3797: dup
    //   3798: iconst_0
    //   3799: iload 11
    //   3801: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3804: aastore
    //   3805: dup
    //   3806: iconst_1
    //   3807: iload 10
    //   3809: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3812: aastore
    //   3813: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3816: putfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3819: aload_0
    //   3820: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   3823: ldc_w 2782
    //   3826: iconst_2
    //   3827: anewarray 1016	java/lang/Object
    //   3830: dup
    //   3831: iconst_0
    //   3832: iload 11
    //   3834: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3837: aastore
    //   3838: dup
    //   3839: iconst_1
    //   3840: iload 10
    //   3842: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3845: aastore
    //   3846: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3849: putfield 1180	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   3852: aload 30
    //   3854: ifnull +2719 -> 6573
    //   3857: aload_0
    //   3858: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   3861: aload 30
    //   3863: aconst_null
    //   3864: aload_0
    //   3865: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   3868: aconst_null
    //   3869: aconst_null
    //   3870: ldc_w 2784
    //   3873: aload 30
    //   3875: getfield 2787	org/vidogram/tgnet/TLRPC$TL_webDocument:size	I
    //   3878: aconst_null
    //   3879: iconst_1
    //   3880: invokevirtual 1140	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   3883: aload_0
    //   3884: iconst_1
    //   3885: putfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   3888: aload 32
    //   3890: ifnull +3108 -> 6998
    //   3893: aload 32
    //   3895: ldc_w 2789
    //   3898: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   3901: ifeq +3097 -> 6998
    //   3904: iload 16
    //   3906: ifeq +3092 -> 6998
    //   3909: iload 16
    //   3911: bipush 60
    //   3913: idiv
    //   3914: istore 10
    //   3916: ldc_w 2791
    //   3919: iconst_2
    //   3920: anewarray 1016	java/lang/Object
    //   3923: dup
    //   3924: iconst_0
    //   3925: iload 10
    //   3927: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3930: aastore
    //   3931: dup
    //   3932: iconst_1
    //   3933: iload 16
    //   3935: iload 10
    //   3937: bipush 60
    //   3939: imul
    //   3940: isub
    //   3941: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3944: aastore
    //   3945: invokestatic 1020	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   3948: astore 30
    //   3950: aload_0
    //   3951: getstatic 2794	org/vidogram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   3954: aload 30
    //   3956: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   3959: f2d
    //   3960: invokestatic 949	java/lang/Math:ceil	(D)D
    //   3963: d2i
    //   3964: putfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   3967: aload_0
    //   3968: new 277	android/text/StaticLayout
    //   3971: dup
    //   3972: aload 30
    //   3974: getstatic 2794	org/vidogram/ui/ActionBar/Theme:chat_durationPaint	Landroid/text/TextPaint;
    //   3977: aload_0
    //   3978: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   3981: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   3984: fconst_1
    //   3985: fconst_0
    //   3986: iconst_0
    //   3987: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   3990: putfield 1364	org/vidogram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   3993: iload 14
    //   3995: istore 10
    //   3997: iload 10
    //   3999: istore 11
    //   4001: aload_0
    //   4002: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   4005: ifeq +250 -> 4255
    //   4008: aload_1
    //   4009: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4012: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   4015: getfield 2795	org/vidogram/tgnet/TLRPC$MessageMedia:flags	I
    //   4018: iconst_4
    //   4019: iand
    //   4020: ifeq +3089 -> 7109
    //   4023: ldc_w 2797
    //   4026: ldc_w 2798
    //   4029: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4032: invokevirtual 2801	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   4035: astore 30
    //   4037: invokestatic 1980	org/vidogram/messenger/LocaleController:getInstance	()Lorg/vidogram/messenger/LocaleController;
    //   4040: aload_1
    //   4041: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4044: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   4047: getfield 2804	org/vidogram/tgnet/TLRPC$MessageMedia:total_amount	J
    //   4050: aload_1
    //   4051: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4054: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   4057: getfield 2807	org/vidogram/tgnet/TLRPC$MessageMedia:currency	Ljava/lang/String;
    //   4060: invokevirtual 2811	org/vidogram/messenger/LocaleController:formatCurrencyString	(JLjava/lang/String;)Ljava/lang/String;
    //   4063: astore 31
    //   4065: new 1780	android/text/SpannableStringBuilder
    //   4068: dup
    //   4069: new 1098	java/lang/StringBuilder
    //   4072: dup
    //   4073: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   4076: aload 31
    //   4078: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4081: ldc_w 1105
    //   4084: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   4087: aload 30
    //   4089: invokevirtual 2031	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   4092: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4095: invokespecial 1783	android/text/SpannableStringBuilder:<init>	(Ljava/lang/CharSequence;)V
    //   4098: astore 30
    //   4100: aload 30
    //   4102: new 2093	org/vidogram/ui/Components/TypefaceSpan
    //   4105: dup
    //   4106: ldc_w 2108
    //   4109: invokestatic 2112	org/vidogram/messenger/AndroidUtilities:getTypeface	(Ljava/lang/String;)Landroid/graphics/Typeface;
    //   4112: invokespecial 2140	org/vidogram/ui/Components/TypefaceSpan:<init>	(Landroid/graphics/Typeface;)V
    //   4115: iconst_0
    //   4116: aload 31
    //   4118: invokevirtual 813	java/lang/String:length	()I
    //   4121: bipush 33
    //   4123: invokevirtual 2106	android/text/SpannableStringBuilder:setSpan	(Ljava/lang/Object;III)V
    //   4126: aload_0
    //   4127: getstatic 1382	org/vidogram/ui/ActionBar/Theme:chat_shipmentPaint	Landroid/text/TextPaint;
    //   4130: aload 30
    //   4132: iconst_0
    //   4133: aload 30
    //   4135: invokevirtual 2113	android/text/SpannableStringBuilder:length	()I
    //   4138: invokevirtual 2028	android/text/TextPaint:measureText	(Ljava/lang/CharSequence;II)F
    //   4141: f2d
    //   4142: invokestatic 949	java/lang/Math:ceil	(D)D
    //   4145: d2i
    //   4146: putfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4149: aload_0
    //   4150: new 277	android/text/StaticLayout
    //   4153: dup
    //   4154: aload 30
    //   4156: getstatic 1382	org/vidogram/ui/ActionBar/Theme:chat_shipmentPaint	Landroid/text/TextPaint;
    //   4159: aload_0
    //   4160: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4163: ldc_w 472
    //   4166: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4169: iadd
    //   4170: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4173: fconst_1
    //   4174: fconst_0
    //   4175: iconst_0
    //   4176: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4179: putfield 1364	org/vidogram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   4182: iload 10
    //   4184: istore 11
    //   4186: aload_0
    //   4187: getfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   4190: ifne +65 -> 4255
    //   4193: aload_0
    //   4194: aload_0
    //   4195: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4198: ldc_w 1323
    //   4201: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4204: iadd
    //   4205: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4208: aload_0
    //   4209: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4212: aload_0
    //   4213: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   4216: iadd
    //   4217: ldc_w 1323
    //   4220: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4223: iadd
    //   4224: iload 12
    //   4226: if_icmple +2930 -> 7156
    //   4229: aload_0
    //   4230: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   4233: iload 10
    //   4235: invokestatic 379	java/lang/Math:max	(II)I
    //   4238: istore 11
    //   4240: aload_0
    //   4241: aload_0
    //   4242: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4245: ldc_w 438
    //   4248: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4251: iadd
    //   4252: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4255: aload_0
    //   4256: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   4259: ifeq +45 -> 4304
    //   4262: aload_1
    //   4263: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   4266: ifeq +38 -> 4304
    //   4269: aload_0
    //   4270: aload_0
    //   4271: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4274: aload_1
    //   4275: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   4278: ldc_w 1323
    //   4281: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4284: iadd
    //   4285: iadd
    //   4286: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4289: aload_0
    //   4290: aload_0
    //   4291: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4294: ldc_w 837
    //   4297: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4300: iadd
    //   4301: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4304: aload_0
    //   4305: iload 12
    //   4307: iload 22
    //   4309: iload 11
    //   4311: invokespecial 2813	org/vidogram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   4314: aload_0
    //   4315: getfield 660	org/vidogram/ui/Cells/ChatMessageCell:drawInstantView	Z
    //   4318: ifeq +159 -> 4477
    //   4321: aload_0
    //   4322: ldc_w 2814
    //   4325: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4328: putfield 1397	org/vidogram/ui/Cells/ChatMessageCell:instantWidth	I
    //   4331: ldc_w 2816
    //   4334: ldc_w 2817
    //   4337: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   4340: astore 30
    //   4342: aload_0
    //   4343: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4346: ldc_w 2818
    //   4349: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4352: isub
    //   4353: istore 10
    //   4355: aload_0
    //   4356: new 277	android/text/StaticLayout
    //   4359: dup
    //   4360: aload 30
    //   4362: getstatic 1393	org/vidogram/ui/ActionBar/Theme:chat_instantViewPaint	Landroid/text/TextPaint;
    //   4365: iload 10
    //   4367: i2f
    //   4368: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   4371: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   4374: getstatic 1393	org/vidogram/ui/ActionBar/Theme:chat_instantViewPaint	Landroid/text/TextPaint;
    //   4377: iload 10
    //   4379: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4382: fconst_1
    //   4383: fconst_0
    //   4384: iconst_0
    //   4385: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4388: putfield 1412	org/vidogram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   4391: aload_0
    //   4392: getfield 1412	org/vidogram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   4395: ifnull +52 -> 4447
    //   4398: aload_0
    //   4399: getfield 1412	org/vidogram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   4402: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   4405: ifle +42 -> 4447
    //   4408: aload_0
    //   4409: aload_0
    //   4410: getfield 1412	org/vidogram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   4413: iconst_0
    //   4414: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   4417: fneg
    //   4418: f2i
    //   4419: putfield 1414	org/vidogram/ui/Cells/ChatMessageCell:instantTextX	I
    //   4422: aload_0
    //   4423: aload_0
    //   4424: getfield 1397	org/vidogram/ui/Cells/ChatMessageCell:instantWidth	I
    //   4427: i2f
    //   4428: aload_0
    //   4429: getfield 1412	org/vidogram/ui/Cells/ChatMessageCell:instantViewLayout	Landroid/text/StaticLayout;
    //   4432: iconst_0
    //   4433: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   4436: aload_0
    //   4437: getfield 1414	org/vidogram/ui/Cells/ChatMessageCell:instantTextX	I
    //   4440: i2f
    //   4441: fadd
    //   4442: fadd
    //   4443: f2i
    //   4444: putfield 1397	org/vidogram/ui/Cells/ChatMessageCell:instantWidth	I
    //   4447: aload_0
    //   4448: aload_0
    //   4449: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4452: ldc_w 1706
    //   4455: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4458: iadd
    //   4459: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   4462: aload_0
    //   4463: aload_0
    //   4464: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4467: ldc_w 1706
    //   4470: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4473: iadd
    //   4474: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4477: aload_0
    //   4478: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4481: ifnonnull +219 -> 4700
    //   4484: aload_1
    //   4485: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4488: ifnull +212 -> 4700
    //   4491: aload_1
    //   4492: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   4495: bipush 13
    //   4497: if_icmpeq +203 -> 4700
    //   4500: aload_0
    //   4501: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4504: ldc_w 380
    //   4507: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4510: isub
    //   4511: istore 11
    //   4513: aload_0
    //   4514: new 277	android/text/StaticLayout
    //   4517: dup
    //   4518: aload_1
    //   4519: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   4522: getstatic 2276	org/vidogram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   4525: iload 11
    //   4527: ldc_w 472
    //   4530: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4533: isub
    //   4534: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   4537: fconst_1
    //   4538: fconst_0
    //   4539: iconst_0
    //   4540: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   4543: putfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4546: aload_0
    //   4547: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4550: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   4553: ifle +147 -> 4700
    //   4556: aload_0
    //   4557: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   4560: istore 12
    //   4562: aload_1
    //   4563: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   4566: ifeq +8100 -> 12666
    //   4569: ldc_w 836
    //   4572: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4575: istore 10
    //   4577: aload_0
    //   4578: aload_0
    //   4579: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4582: invokevirtual 655	android/text/StaticLayout:getHeight	()I
    //   4585: putfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4588: aload_0
    //   4589: aload_0
    //   4590: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4593: aload_0
    //   4594: getfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4597: ldc_w 1406
    //   4600: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4603: iadd
    //   4604: iadd
    //   4605: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4608: aload_0
    //   4609: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4612: aload_0
    //   4613: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4616: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   4619: iconst_1
    //   4620: isub
    //   4621: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   4624: fstore 8
    //   4626: aload_0
    //   4627: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4630: aload_0
    //   4631: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   4634: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   4637: iconst_1
    //   4638: isub
    //   4639: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   4642: fstore 9
    //   4644: iload 11
    //   4646: ldc_w 652
    //   4649: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4652: isub
    //   4653: i2f
    //   4654: fload 8
    //   4656: fload 9
    //   4658: fadd
    //   4659: fsub
    //   4660: iload 10
    //   4662: iload 12
    //   4664: iadd
    //   4665: i2f
    //   4666: fcmpg
    //   4667: ifge +33 -> 4700
    //   4670: aload_0
    //   4671: aload_0
    //   4672: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4675: ldc_w 373
    //   4678: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4681: iadd
    //   4682: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   4685: aload_0
    //   4686: aload_0
    //   4687: getfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4690: ldc_w 373
    //   4693: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4696: iadd
    //   4697: putfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   4700: aload_0
    //   4701: getfield 262	org/vidogram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   4704: invokevirtual 2045	java/util/ArrayList:clear	()V
    //   4707: iload 21
    //   4709: ifeq +22 -> 4731
    //   4712: aload_0
    //   4713: getfield 267	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4716: invokevirtual 2819	java/util/HashMap:clear	()V
    //   4719: aload_0
    //   4720: getfield 269	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   4723: invokevirtual 2819	java/util/HashMap:clear	()V
    //   4726: aload_0
    //   4727: aconst_null
    //   4728: putfield 2821	org/vidogram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   4731: aload_1
    //   4732: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4735: getfield 2825	org/vidogram/tgnet/TLRPC$Message:reply_markup	Lorg/vidogram/tgnet/TLRPC$ReplyMarkup;
    //   4738: instanceof 2827
    //   4741: ifeq +8526 -> 13267
    //   4744: aload_1
    //   4745: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4748: getfield 2825	org/vidogram/tgnet/TLRPC$Message:reply_markup	Lorg/vidogram/tgnet/TLRPC$ReplyMarkup;
    //   4751: getfield 2832	org/vidogram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4754: invokevirtual 475	java/util/ArrayList:size	()I
    //   4757: istore 14
    //   4759: ldc_w 658
    //   4762: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4765: iload 14
    //   4767: imul
    //   4768: fconst_1
    //   4769: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4772: iadd
    //   4773: istore 10
    //   4775: aload_0
    //   4776: iload 10
    //   4778: putfield 2586	org/vidogram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   4781: aload_0
    //   4782: iload 10
    //   4784: putfield 2539	org/vidogram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   4787: aload_0
    //   4788: aload_0
    //   4789: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4792: putfield 471	org/vidogram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4795: aload_1
    //   4796: getfield 2835	org/vidogram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4799: aload_0
    //   4800: getfield 471	org/vidogram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4803: if_icmple +8595 -> 13398
    //   4806: aload_0
    //   4807: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   4810: ifeq +7872 -> 12682
    //   4813: aload_1
    //   4814: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   4817: ifeq +7865 -> 12682
    //   4820: aload_1
    //   4821: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   4824: ifne +7858 -> 12682
    //   4827: ldc_w 2836
    //   4830: fstore 8
    //   4832: fload 8
    //   4834: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   4837: ineg
    //   4838: istore 10
    //   4840: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   4843: ifeq +7847 -> 12690
    //   4846: iload 10
    //   4848: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   4851: iadd
    //   4852: istore 10
    //   4854: aload_0
    //   4855: aload_0
    //   4856: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   4859: aload_1
    //   4860: getfield 2835	org/vidogram/messenger/MessageObject:wantedBotKeyboardWidth	I
    //   4863: iload 10
    //   4865: invokestatic 959	java/lang/Math:min	(II)I
    //   4868: invokestatic 379	java/lang/Math:max	(II)I
    //   4871: putfield 471	org/vidogram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   4874: iconst_1
    //   4875: istore 11
    //   4877: iconst_0
    //   4878: istore 10
    //   4880: new 264	java/util/HashMap
    //   4883: dup
    //   4884: aload_0
    //   4885: getfield 267	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4888: invokespecial 2839	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   4891: astore 32
    //   4893: aload_1
    //   4894: getfield 2842	org/vidogram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   4897: ifnull +7816 -> 12713
    //   4900: aload_0
    //   4901: getfield 2821	org/vidogram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   4904: ifnull +7809 -> 12713
    //   4907: aload_0
    //   4908: getfield 2821	org/vidogram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   4911: aload_1
    //   4912: getfield 2842	org/vidogram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   4915: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   4918: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   4921: ifeq +7792 -> 12713
    //   4924: new 264	java/util/HashMap
    //   4927: dup
    //   4928: aload_0
    //   4929: getfield 269	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   4932: invokespecial 2839	java/util/HashMap:<init>	(Ljava/util/Map;)V
    //   4935: astore 30
    //   4937: aload_0
    //   4938: getfield 267	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   4941: invokevirtual 2819	java/util/HashMap:clear	()V
    //   4944: iconst_0
    //   4945: istore 12
    //   4947: iload 12
    //   4949: iload 14
    //   4951: if_icmpge +8272 -> 13223
    //   4954: aload_1
    //   4955: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   4958: getfield 2825	org/vidogram/tgnet/TLRPC$Message:reply_markup	Lorg/vidogram/tgnet/TLRPC$ReplyMarkup;
    //   4961: getfield 2832	org/vidogram/tgnet/TLRPC$ReplyMarkup:rows	Ljava/util/ArrayList;
    //   4964: iload 12
    //   4966: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4969: checkcast 2844	org/vidogram/tgnet/TLRPC$TL_keyboardButtonRow
    //   4972: astore 33
    //   4974: aload 33
    //   4976: getfield 2847	org/vidogram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   4979: invokevirtual 475	java/util/ArrayList:size	()I
    //   4982: istore 13
    //   4984: iload 13
    //   4986: ifne +7751 -> 12737
    //   4989: iload 12
    //   4991: iconst_1
    //   4992: iadd
    //   4993: istore 12
    //   4995: goto -48 -> 4947
    //   4998: aload_0
    //   4999: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5002: invokevirtual 2742	android/text/StaticLayout:getWidth	()I
    //   5005: istore 26
    //   5007: iconst_0
    //   5008: istore 20
    //   5010: iload 11
    //   5012: istore 24
    //   5014: iload 20
    //   5016: aload_0
    //   5017: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5020: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   5023: if_icmpge +200 -> 5223
    //   5026: aload_0
    //   5027: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5030: iload 20
    //   5032: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   5035: f2d
    //   5036: invokestatic 949	java/lang/Math:ceil	(D)D
    //   5039: d2i
    //   5040: istore 28
    //   5042: iload 28
    //   5044: ifne +8520 -> 13564
    //   5047: aload_0
    //   5048: getfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   5051: ifeq +8513 -> 13564
    //   5054: aload_0
    //   5055: iconst_0
    //   5056: putfield 614	org/vidogram/ui/Cells/ChatMessageCell:descriptionX	I
    //   5059: goto +8505 -> 13564
    //   5062: iload 20
    //   5064: iload 13
    //   5066: if_icmplt +32 -> 5098
    //   5069: iload 11
    //   5071: istore 25
    //   5073: iload 13
    //   5075: ifeq +34 -> 5109
    //   5078: iload 11
    //   5080: istore 25
    //   5082: iload 28
    //   5084: ifeq +25 -> 5109
    //   5087: iload 11
    //   5089: istore 25
    //   5091: aload_0
    //   5092: getfield 1361	org/vidogram/ui/Cells/ChatMessageCell:isSmallImage	Z
    //   5095: ifeq +14 -> 5109
    //   5098: iload 11
    //   5100: ldc_w 2744
    //   5103: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5106: iadd
    //   5107: istore 25
    //   5109: iload 24
    //   5111: istore 11
    //   5113: iload 24
    //   5115: iload 25
    //   5117: iload 23
    //   5119: iadd
    //   5120: if_icmpge +50 -> 5170
    //   5123: iload 18
    //   5125: ifeq +20 -> 5145
    //   5128: aload_0
    //   5129: aload_0
    //   5130: getfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   5133: iload 25
    //   5135: iload 23
    //   5137: iadd
    //   5138: iload 24
    //   5140: isub
    //   5141: iadd
    //   5142: putfield 1332	org/vidogram/ui/Cells/ChatMessageCell:titleX	I
    //   5145: iload 15
    //   5147: ifeq +8432 -> 13579
    //   5150: aload_0
    //   5151: iload 25
    //   5153: iload 23
    //   5155: iadd
    //   5156: iload 24
    //   5158: isub
    //   5159: aload_0
    //   5160: getfield 1336	org/vidogram/ui/Cells/ChatMessageCell:authorX	I
    //   5163: iadd
    //   5164: putfield 1336	org/vidogram/ui/Cells/ChatMessageCell:authorX	I
    //   5167: goto +8412 -> 13579
    //   5170: iload 10
    //   5172: iload 25
    //   5174: iload 23
    //   5176: iadd
    //   5177: invokestatic 379	java/lang/Math:max	(II)I
    //   5180: istore 24
    //   5182: iload 20
    //   5184: iconst_1
    //   5185: iadd
    //   5186: istore 20
    //   5188: iload 24
    //   5190: istore 10
    //   5192: iload 11
    //   5194: istore 24
    //   5196: goto -182 -> 5014
    //   5199: aload_0
    //   5200: getfield 608	org/vidogram/ui/Cells/ChatMessageCell:descriptionLayout	Landroid/text/StaticLayout;
    //   5203: iload 20
    //   5205: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   5208: f2d
    //   5209: invokestatic 949	java/lang/Math:ceil	(D)D
    //   5212: d2i
    //   5213: iload 26
    //   5215: invokestatic 959	java/lang/Math:min	(II)I
    //   5218: istore 11
    //   5220: goto -158 -> 5062
    //   5223: goto -2036 -> 3187
    //   5226: iload 27
    //   5228: istore 11
    //   5230: goto -1980 -> 3250
    //   5233: iconst_0
    //   5234: istore_2
    //   5235: goto -1941 -> 3294
    //   5238: iload 14
    //   5240: iconst_1
    //   5241: iadd
    //   5242: istore 14
    //   5244: goto -1905 -> 3339
    //   5247: aload 34
    //   5249: invokestatic 1023	org/vidogram/messenger/MessageObject:isVideoDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5252: ifeq +175 -> 5427
    //   5255: aload_0
    //   5256: aload 34
    //   5258: getfield 1055	org/vidogram/tgnet/TLRPC$Document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5261: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5264: aload_0
    //   5265: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5268: ifnull +140 -> 5408
    //   5271: aload_0
    //   5272: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5275: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5278: ifeq +13 -> 5291
    //   5281: aload_0
    //   5282: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5285: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5288: ifne +120 -> 5408
    //   5291: iconst_0
    //   5292: istore 14
    //   5294: iload 14
    //   5296: aload 34
    //   5298: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5301: invokevirtual 475	java/util/ArrayList:size	()I
    //   5304: if_icmpge +50 -> 5354
    //   5307: aload 34
    //   5309: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5312: iload 14
    //   5314: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5317: checkcast 925	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   5320: astore 31
    //   5322: aload 31
    //   5324: instanceof 1025
    //   5327: ifeq +91 -> 5418
    //   5330: aload_0
    //   5331: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5334: aload 31
    //   5336: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5339: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5342: aload_0
    //   5343: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5346: aload 31
    //   5348: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5351: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5354: aload_0
    //   5355: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5358: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5361: ifeq +13 -> 5374
    //   5364: aload_0
    //   5365: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5368: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5371: ifne +37 -> 5408
    //   5374: aload_0
    //   5375: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5378: astore 31
    //   5380: aload_0
    //   5381: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5384: astore 35
    //   5386: ldc_w 2773
    //   5389: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5392: istore 14
    //   5394: aload 35
    //   5396: iload 14
    //   5398: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5401: aload 31
    //   5403: iload 14
    //   5405: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5408: aload_0
    //   5409: iconst_0
    //   5410: aload_1
    //   5411: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   5414: pop
    //   5415: goto -1949 -> 3466
    //   5418: iload 14
    //   5420: iconst_1
    //   5421: iadd
    //   5422: istore 14
    //   5424: goto -130 -> 5294
    //   5427: aload 34
    //   5429: invokestatic 2852	org/vidogram/messenger/MessageObject:isStickerDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5432: ifeq +180 -> 5612
    //   5435: aload_0
    //   5436: aload 34
    //   5438: getfield 1055	org/vidogram/tgnet/TLRPC$Document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5441: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5444: aload_0
    //   5445: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5448: ifnull +140 -> 5588
    //   5451: aload_0
    //   5452: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5455: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5458: ifeq +13 -> 5471
    //   5461: aload_0
    //   5462: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5465: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5468: ifne +120 -> 5588
    //   5471: iconst_0
    //   5472: istore 14
    //   5474: iload 14
    //   5476: aload 34
    //   5478: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5481: invokevirtual 475	java/util/ArrayList:size	()I
    //   5484: if_icmpge +50 -> 5534
    //   5487: aload 34
    //   5489: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   5492: iload 14
    //   5494: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5497: checkcast 925	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   5500: astore 31
    //   5502: aload 31
    //   5504: instanceof 2770
    //   5507: ifeq +96 -> 5603
    //   5510: aload_0
    //   5511: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5514: aload 31
    //   5516: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   5519: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5522: aload_0
    //   5523: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5526: aload 31
    //   5528: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   5531: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5534: aload_0
    //   5535: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5538: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5541: ifeq +13 -> 5554
    //   5544: aload_0
    //   5545: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5548: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5551: ifne +37 -> 5588
    //   5554: aload_0
    //   5555: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5558: astore 31
    //   5560: aload_0
    //   5561: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5564: astore 35
    //   5566: ldc_w 2773
    //   5569: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5572: istore 14
    //   5574: aload 35
    //   5576: iload 14
    //   5578: putfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5581: aload 31
    //   5583: iload 14
    //   5585: putfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5588: aload_0
    //   5589: aload 34
    //   5591: putfield 913	org/vidogram/ui/Cells/ChatMessageCell:documentAttach	Lorg/vidogram/tgnet/TLRPC$Document;
    //   5594: aload_0
    //   5595: bipush 6
    //   5597: putfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   5600: goto -2134 -> 3466
    //   5603: iload 14
    //   5605: iconst_1
    //   5606: iadd
    //   5607: istore 14
    //   5609: goto -135 -> 5474
    //   5612: aload_0
    //   5613: iload 12
    //   5615: iload 22
    //   5617: iload 10
    //   5619: invokespecial 2813	org/vidogram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5622: aload 30
    //   5624: astore 31
    //   5626: aload 34
    //   5628: invokestatic 2852	org/vidogram/messenger/MessageObject:isStickerDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5631: ifne +699 -> 6330
    //   5634: aload_0
    //   5635: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5638: ldc_w 836
    //   5641: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5644: iload 12
    //   5646: iadd
    //   5647: if_icmpge +16 -> 5663
    //   5650: aload_0
    //   5651: ldc_w 836
    //   5654: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5657: iload 12
    //   5659: iadd
    //   5660: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5663: aload 34
    //   5665: invokestatic 918	org/vidogram/messenger/MessageObject:isVoiceDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5668: ifeq +86 -> 5754
    //   5671: aload_0
    //   5672: aload_0
    //   5673: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5676: ldc_w 472
    //   5679: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5682: isub
    //   5683: aload_1
    //   5684: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   5687: pop
    //   5688: aload_0
    //   5689: aload_0
    //   5690: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   5693: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   5696: ldc_w 652
    //   5699: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5702: iadd
    //   5703: aload_0
    //   5704: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5707: iadd
    //   5708: putfield 442	org/vidogram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5711: aload_0
    //   5712: aload_0
    //   5713: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5716: ldc_w 1552
    //   5719: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5722: iadd
    //   5723: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5726: aload_0
    //   5727: aload_0
    //   5728: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5731: ldc_w 1552
    //   5734: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5737: iadd
    //   5738: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5741: aload_0
    //   5742: iload 12
    //   5744: iload 22
    //   5746: iload 10
    //   5748: invokespecial 2813	org/vidogram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5751: goto -2285 -> 3466
    //   5754: aload 34
    //   5756: invokestatic 965	org/vidogram/messenger/MessageObject:isMusicDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5759: ifeq +233 -> 5992
    //   5762: aload_0
    //   5763: aload_0
    //   5764: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5767: ldc_w 472
    //   5770: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5773: isub
    //   5774: aload_1
    //   5775: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   5778: istore 15
    //   5780: aload_0
    //   5781: aload_0
    //   5782: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   5785: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   5788: ldc_w 652
    //   5791: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5794: iadd
    //   5795: aload_0
    //   5796: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5799: iadd
    //   5800: putfield 442	org/vidogram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   5803: aload_0
    //   5804: aload_0
    //   5805: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5808: ldc_w 1666
    //   5811: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5814: iadd
    //   5815: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   5818: aload_0
    //   5819: aload_0
    //   5820: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5823: ldc_w 1666
    //   5826: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5829: iadd
    //   5830: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   5833: iload 12
    //   5835: ldc_w 966
    //   5838: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5841: isub
    //   5842: istore 14
    //   5844: iload 10
    //   5846: iload 15
    //   5848: iload 23
    //   5850: iadd
    //   5851: ldc_w 931
    //   5854: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5857: iadd
    //   5858: invokestatic 379	java/lang/Math:max	(II)I
    //   5861: istore 12
    //   5863: iload 12
    //   5865: istore 10
    //   5867: aload_0
    //   5868: getfield 997	org/vidogram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5871: ifnull +46 -> 5917
    //   5874: iload 12
    //   5876: istore 10
    //   5878: aload_0
    //   5879: getfield 997	org/vidogram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5882: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   5885: ifle +32 -> 5917
    //   5888: iload 12
    //   5890: i2f
    //   5891: aload_0
    //   5892: getfield 997	org/vidogram/ui/Cells/ChatMessageCell:songLayout	Landroid/text/StaticLayout;
    //   5895: iconst_0
    //   5896: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   5899: iload 23
    //   5901: i2f
    //   5902: fadd
    //   5903: ldc_w 966
    //   5906: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5909: i2f
    //   5910: fadd
    //   5911: invokestatic 2855	java/lang/Math:max	(FF)F
    //   5914: f2i
    //   5915: istore 10
    //   5917: iload 10
    //   5919: istore 12
    //   5921: aload_0
    //   5922: getfield 1010	org/vidogram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5925: ifnull +46 -> 5971
    //   5928: iload 10
    //   5930: istore 12
    //   5932: aload_0
    //   5933: getfield 1010	org/vidogram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5936: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   5939: ifle +32 -> 5971
    //   5942: iload 10
    //   5944: i2f
    //   5945: aload_0
    //   5946: getfield 1010	org/vidogram/ui/Cells/ChatMessageCell:performerLayout	Landroid/text/StaticLayout;
    //   5949: iconst_0
    //   5950: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   5953: iload 23
    //   5955: i2f
    //   5956: fadd
    //   5957: ldc_w 966
    //   5960: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   5963: i2f
    //   5964: fadd
    //   5965: invokestatic 2855	java/lang/Math:max	(FF)F
    //   5968: f2i
    //   5969: istore 12
    //   5971: aload_0
    //   5972: iload 14
    //   5974: iload 22
    //   5976: iload 12
    //   5978: invokespecial 2813	org/vidogram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   5981: iload 12
    //   5983: istore 10
    //   5985: iload 14
    //   5987: istore 12
    //   5989: goto -2523 -> 3466
    //   5992: aload_0
    //   5993: aload_0
    //   5994: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   5997: ldc_w 2856
    //   6000: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6003: isub
    //   6004: aload_1
    //   6005: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   6008: pop
    //   6009: aload_0
    //   6010: iconst_1
    //   6011: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6014: aload_0
    //   6015: getfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   6018: ifeq +65 -> 6083
    //   6021: aload_0
    //   6022: aload_0
    //   6023: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6026: ldc_w 2082
    //   6029: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6032: iadd
    //   6033: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6036: aload_0
    //   6037: aload_0
    //   6038: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6041: ldc_w 966
    //   6044: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6047: iadd
    //   6048: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6051: aload_0
    //   6052: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6055: iconst_0
    //   6056: aload_0
    //   6057: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6060: aload_0
    //   6061: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   6064: iadd
    //   6065: ldc_w 966
    //   6068: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6071: ldc_w 966
    //   6074: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6077: invokevirtual 1316	org/vidogram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   6080: goto -2614 -> 3466
    //   6083: aload_0
    //   6084: aload_0
    //   6085: getfield 362	org/vidogram/ui/Cells/ChatMessageCell:currentMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   6088: getfield 649	org/vidogram/messenger/MessageObject:textHeight	I
    //   6091: ldc_w 652
    //   6094: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6097: iadd
    //   6098: aload_0
    //   6099: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6102: iadd
    //   6103: putfield 442	org/vidogram/ui/Cells/ChatMessageCell:mediaOffsetY	I
    //   6106: aload_0
    //   6107: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6110: iconst_0
    //   6111: aload_0
    //   6112: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6115: aload_0
    //   6116: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   6119: iadd
    //   6120: ldc_w 373
    //   6123: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6126: isub
    //   6127: ldc_w 1666
    //   6130: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6133: ldc_w 1666
    //   6136: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6139: invokevirtual 1316	org/vidogram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   6142: aload_0
    //   6143: aload_0
    //   6144: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6147: ldc_w 2857
    //   6150: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6153: iadd
    //   6154: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6157: aload_0
    //   6158: aload_0
    //   6159: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6162: ldc_w 833
    //   6165: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6168: iadd
    //   6169: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6172: goto -2706 -> 3466
    //   6175: aload 35
    //   6177: ifnull +118 -> 6295
    //   6180: aload 32
    //   6182: ifnull +96 -> 6278
    //   6185: aload 32
    //   6187: ldc_w 2774
    //   6190: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6193: ifeq +85 -> 6278
    //   6196: iconst_1
    //   6197: istore_2
    //   6198: aload_0
    //   6199: iload_2
    //   6200: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6203: aload_1
    //   6204: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   6207: astore 31
    //   6209: aload_0
    //   6210: getfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6213: ifeq +70 -> 6283
    //   6216: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   6219: istore 14
    //   6221: aload_0
    //   6222: getfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6225: ifne +65 -> 6290
    //   6228: iconst_1
    //   6229: istore_2
    //   6230: aload_0
    //   6231: aload 31
    //   6233: iload 14
    //   6235: iload_2
    //   6236: invokestatic 2860	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;IZ)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6239: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6242: aload_0
    //   6243: aload_1
    //   6244: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   6247: bipush 80
    //   6249: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6252: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6255: aload 30
    //   6257: astore 31
    //   6259: aload_0
    //   6260: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6263: aload_0
    //   6264: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6267: if_acmpne +63 -> 6330
    //   6270: aload_0
    //   6271: aconst_null
    //   6272: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6275: goto -2809 -> 3466
    //   6278: iconst_0
    //   6279: istore_2
    //   6280: goto -82 -> 6198
    //   6283: iload 11
    //   6285: istore 14
    //   6287: goto -66 -> 6221
    //   6290: iconst_0
    //   6291: istore_2
    //   6292: goto -62 -> 6230
    //   6295: aload 30
    //   6297: astore 31
    //   6299: aload 30
    //   6301: ifnull +29 -> 6330
    //   6304: aload 30
    //   6306: astore 31
    //   6308: aload 30
    //   6310: getfield 2861	org/vidogram/tgnet/TLRPC$TL_webDocument:mime_type	Ljava/lang/String;
    //   6313: ldc_w 1048
    //   6316: invokevirtual 1052	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   6319: ifne +6 -> 6325
    //   6322: aconst_null
    //   6323: astore 31
    //   6325: aload_0
    //   6326: iconst_0
    //   6327: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   6330: aload 31
    //   6332: astore 30
    //   6334: goto -2868 -> 3466
    //   6337: iconst_0
    //   6338: istore_2
    //   6339: goto -2780 -> 3559
    //   6342: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6345: getfield 1836	android/graphics/Point:x	I
    //   6348: i2f
    //   6349: ldc_w 2778
    //   6352: fmul
    //   6353: f2i
    //   6354: istore 11
    //   6356: goto -2733 -> 3623
    //   6359: iconst_0
    //   6360: istore 14
    //   6362: goto -2724 -> 3638
    //   6365: aload 30
    //   6367: iconst_m1
    //   6368: putfield 2787	org/vidogram/tgnet/TLRPC$TL_webDocument:size	I
    //   6371: goto -2688 -> 3683
    //   6374: aload_0
    //   6375: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6378: ifne +10 -> 6388
    //   6381: aload_0
    //   6382: getfield 360	org/vidogram/ui/Cells/ChatMessageCell:hasInvoicePreview	Z
    //   6385: ifeq +41 -> 6426
    //   6388: sipush 640
    //   6391: i2f
    //   6392: iload 11
    //   6394: fconst_2
    //   6395: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6398: isub
    //   6399: i2f
    //   6400: fdiv
    //   6401: fstore 8
    //   6403: sipush 640
    //   6406: i2f
    //   6407: fload 8
    //   6409: fdiv
    //   6410: f2i
    //   6411: istore 11
    //   6413: sipush 360
    //   6416: i2f
    //   6417: fload 8
    //   6419: fdiv
    //   6420: f2i
    //   6421: istore 10
    //   6423: goto -2731 -> 3692
    //   6426: aload_0
    //   6427: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6430: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   6433: istore 10
    //   6435: aload_0
    //   6436: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6439: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   6442: istore 13
    //   6444: iload 10
    //   6446: i2f
    //   6447: iload 11
    //   6449: fconst_2
    //   6450: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6453: isub
    //   6454: i2f
    //   6455: fdiv
    //   6456: fstore 8
    //   6458: iload 10
    //   6460: i2f
    //   6461: fload 8
    //   6463: fdiv
    //   6464: f2i
    //   6465: istore 10
    //   6467: iload 13
    //   6469: i2f
    //   6470: fload 8
    //   6472: fdiv
    //   6473: f2i
    //   6474: istore 11
    //   6476: aload 33
    //   6478: ifnull +29 -> 6507
    //   6481: aload 33
    //   6483: ifnull +7004 -> 13487
    //   6486: aload 33
    //   6488: invokevirtual 1046	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   6491: ldc_w 2730
    //   6494: invokevirtual 1886	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   6497: ifne +6990 -> 13487
    //   6500: aload_0
    //   6501: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6504: ifne +6983 -> 13487
    //   6507: iload 11
    //   6509: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6512: getfield 1839	android/graphics/Point:y	I
    //   6515: iconst_3
    //   6516: idiv
    //   6517: if_icmple +6970 -> 13487
    //   6520: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   6523: getfield 1839	android/graphics/Point:y	I
    //   6526: iconst_3
    //   6527: idiv
    //   6528: istore 13
    //   6530: iload 10
    //   6532: istore 11
    //   6534: iload 13
    //   6536: istore 10
    //   6538: goto -2846 -> 3692
    //   6541: aload_0
    //   6542: aload_0
    //   6543: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6546: ldc_w 438
    //   6549: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   6552: iload 10
    //   6554: iadd
    //   6555: iadd
    //   6556: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   6559: aload_0
    //   6560: aload_0
    //   6561: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6564: iload 10
    //   6566: iadd
    //   6567: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   6570: goto -2797 -> 3773
    //   6573: aload_0
    //   6574: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6577: bipush 6
    //   6579: if_icmpne +73 -> 6652
    //   6582: aload_0
    //   6583: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6586: astore 31
    //   6588: aload_0
    //   6589: getfield 913	org/vidogram/ui/Cells/ChatMessageCell:documentAttach	Lorg/vidogram/tgnet/TLRPC$Document;
    //   6592: astore 33
    //   6594: aload_0
    //   6595: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6598: astore 34
    //   6600: aload_0
    //   6601: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6604: ifnull +42 -> 6646
    //   6607: aload_0
    //   6608: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6611: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6614: astore 30
    //   6616: aload 31
    //   6618: aload 33
    //   6620: aconst_null
    //   6621: aload 34
    //   6623: aconst_null
    //   6624: aload 30
    //   6626: ldc_w 2784
    //   6629: aload_0
    //   6630: getfield 913	org/vidogram/ui/Cells/ChatMessageCell:documentAttach	Lorg/vidogram/tgnet/TLRPC$Document;
    //   6633: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   6636: ldc_w 2863
    //   6639: iconst_1
    //   6640: invokevirtual 1140	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6643: goto -2760 -> 3883
    //   6646: aconst_null
    //   6647: astore 30
    //   6649: goto -33 -> 6616
    //   6652: aload_0
    //   6653: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6656: iconst_4
    //   6657: if_icmpne +29 -> 6686
    //   6660: aload_0
    //   6661: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6664: aconst_null
    //   6665: aconst_null
    //   6666: aload_0
    //   6667: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6670: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6673: aload_0
    //   6674: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6677: iconst_0
    //   6678: aconst_null
    //   6679: iconst_0
    //   6680: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6683: goto -2800 -> 3883
    //   6686: aload_0
    //   6687: getfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   6690: iconst_2
    //   6691: if_icmpne +115 -> 6806
    //   6694: aload_1
    //   6695: getfield 2237	org/vidogram/messenger/MessageObject:mediaExists	Z
    //   6698: istore_2
    //   6699: aload 34
    //   6701: invokestatic 2867	org/vidogram/messenger/FileLoader:getAttachFileName	(Lorg/vidogram/tgnet/TLObject;)Ljava/lang/String;
    //   6704: astore 30
    //   6706: aload_0
    //   6707: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6710: ifne +29 -> 6739
    //   6713: iload_2
    //   6714: ifne +25 -> 6739
    //   6717: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   6720: bipush 32
    //   6722: invokevirtual 2870	org/vidogram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6725: ifne +14 -> 6739
    //   6728: invokestatic 1174	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   6731: aload 30
    //   6733: invokevirtual 2873	org/vidogram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6736: ifeq +39 -> 6775
    //   6739: aload_0
    //   6740: iconst_0
    //   6741: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6744: aload_0
    //   6745: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6748: aload 34
    //   6750: aconst_null
    //   6751: aload_0
    //   6752: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6755: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6758: aload_0
    //   6759: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6762: aload 34
    //   6764: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   6767: aconst_null
    //   6768: iconst_0
    //   6769: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6772: goto -2889 -> 3883
    //   6775: aload_0
    //   6776: iconst_1
    //   6777: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6780: aload_0
    //   6781: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6784: aconst_null
    //   6785: aconst_null
    //   6786: aload_0
    //   6787: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6790: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6793: aload_0
    //   6794: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6797: iconst_0
    //   6798: aconst_null
    //   6799: iconst_0
    //   6800: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6803: goto -2920 -> 3883
    //   6806: aload_1
    //   6807: getfield 2237	org/vidogram/messenger/MessageObject:mediaExists	Z
    //   6810: istore_2
    //   6811: aload_0
    //   6812: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6815: invokestatic 2867	org/vidogram/messenger/FileLoader:getAttachFileName	(Lorg/vidogram/tgnet/TLObject;)Ljava/lang/String;
    //   6818: astore 30
    //   6820: aload_0
    //   6821: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   6824: ifne +28 -> 6852
    //   6827: iload_2
    //   6828: ifne +24 -> 6852
    //   6831: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   6834: iconst_1
    //   6835: invokevirtual 2870	org/vidogram/messenger/MediaController:canDownloadMedia	(I)Z
    //   6838: ifne +14 -> 6852
    //   6841: invokestatic 1174	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   6844: aload 30
    //   6846: invokevirtual 2873	org/vidogram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   6849: ifeq +72 -> 6921
    //   6852: aload_0
    //   6853: iconst_0
    //   6854: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6857: aload_0
    //   6858: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6861: astore 31
    //   6863: aload_0
    //   6864: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6867: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6870: astore 33
    //   6872: aload_0
    //   6873: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   6876: astore 34
    //   6878: aload_0
    //   6879: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6882: ifnull +33 -> 6915
    //   6885: aload_0
    //   6886: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6889: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6892: astore 30
    //   6894: aload 31
    //   6896: aload 33
    //   6898: aload 34
    //   6900: aload 30
    //   6902: aload_0
    //   6903: getfield 1180	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilterThumb	Ljava/lang/String;
    //   6906: iconst_0
    //   6907: aconst_null
    //   6908: iconst_0
    //   6909: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6912: goto -3029 -> 3883
    //   6915: aconst_null
    //   6916: astore 30
    //   6918: goto -24 -> 6894
    //   6921: aload_0
    //   6922: iconst_1
    //   6923: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   6926: aload_0
    //   6927: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6930: ifnull +54 -> 6984
    //   6933: aload_0
    //   6934: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6937: aconst_null
    //   6938: aconst_null
    //   6939: aload_0
    //   6940: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6943: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   6946: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   6949: ldc_w 2782
    //   6952: iconst_2
    //   6953: anewarray 1016	java/lang/Object
    //   6956: dup
    //   6957: iconst_0
    //   6958: iload 11
    //   6960: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6963: aastore
    //   6964: dup
    //   6965: iconst_1
    //   6966: iload 10
    //   6968: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   6971: aastore
    //   6972: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   6975: iconst_0
    //   6976: aconst_null
    //   6977: iconst_0
    //   6978: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   6981: goto -3098 -> 3883
    //   6984: aload_0
    //   6985: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   6988: aconst_null
    //   6989: checkcast 1253	android/graphics/drawable/Drawable
    //   6992: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   6995: goto -3112 -> 3883
    //   6998: aload_0
    //   6999: getfield 358	org/vidogram/ui/Cells/ChatMessageCell:hasGamePreview	Z
    //   7002: ifeq -3009 -> 3993
    //   7005: ldc_w 2875
    //   7008: ldc_w 2876
    //   7011: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7014: invokevirtual 2801	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   7017: astore 30
    //   7019: aload_0
    //   7020: getstatic 2879	org/vidogram/ui/ActionBar/Theme:chat_gamePaint	Landroid/text/TextPaint;
    //   7023: aload 30
    //   7025: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   7028: f2d
    //   7029: invokestatic 949	java/lang/Math:ceil	(D)D
    //   7032: d2i
    //   7033: putfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   7036: aload_0
    //   7037: new 277	android/text/StaticLayout
    //   7040: dup
    //   7041: aload 30
    //   7043: getstatic 2879	org/vidogram/ui/ActionBar/Theme:chat_gamePaint	Landroid/text/TextPaint;
    //   7046: aload_0
    //   7047: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   7050: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7053: fconst_1
    //   7054: fconst_0
    //   7055: iconst_0
    //   7056: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7059: putfield 1364	org/vidogram/ui/Cells/ChatMessageCell:videoInfoLayout	Landroid/text/StaticLayout;
    //   7062: goto -3069 -> 3993
    //   7065: aload_0
    //   7066: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   7069: aconst_null
    //   7070: checkcast 1253	android/graphics/drawable/Drawable
    //   7073: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   7076: aload_0
    //   7077: aload_0
    //   7078: getfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7081: ldc_w 1323
    //   7084: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7087: isub
    //   7088: putfield 651	org/vidogram/ui/Cells/ChatMessageCell:linkPreviewHeight	I
    //   7091: aload_0
    //   7092: aload_0
    //   7093: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7096: ldc_w 837
    //   7099: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7102: iadd
    //   7103: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7106: goto -3109 -> 3997
    //   7109: aload_1
    //   7110: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7113: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   7116: getfield 2882	org/vidogram/tgnet/TLRPC$MessageMedia:test	Z
    //   7119: ifeq +20 -> 7139
    //   7122: ldc_w 2884
    //   7125: ldc_w 2885
    //   7128: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7131: invokevirtual 2801	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   7134: astore 30
    //   7136: goto -3099 -> 4037
    //   7139: ldc_w 2887
    //   7142: ldc_w 2888
    //   7145: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7148: invokevirtual 2801	java/lang/String:toUpperCase	()Ljava/lang/String;
    //   7151: astore 30
    //   7153: goto -3116 -> 4037
    //   7156: aload_0
    //   7157: getfield 1374	org/vidogram/ui/Cells/ChatMessageCell:durationWidth	I
    //   7160: aload_0
    //   7161: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   7164: iadd
    //   7165: ldc_w 1323
    //   7168: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7171: iadd
    //   7172: iload 10
    //   7174: invokestatic 379	java/lang/Math:max	(II)I
    //   7177: istore 11
    //   7179: goto -2924 -> 4255
    //   7182: aload_0
    //   7183: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   7186: aconst_null
    //   7187: checkcast 1253	android/graphics/drawable/Drawable
    //   7190: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   7193: aload_0
    //   7194: iload 12
    //   7196: iload 22
    //   7198: iload 11
    //   7200: invokespecial 2813	org/vidogram/ui/Cells/ChatMessageCell:calcBackgroundWidth	(III)V
    //   7203: goto -2726 -> 4477
    //   7206: aload_1
    //   7207: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   7210: bipush 16
    //   7212: if_icmpne +495 -> 7707
    //   7215: aload_0
    //   7216: iconst_0
    //   7217: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   7220: aload_0
    //   7221: iconst_0
    //   7222: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7225: aload_0
    //   7226: iconst_0
    //   7227: putfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   7230: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   7233: ifeq +337 -> 7570
    //   7236: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7239: istore 10
    //   7241: aload_0
    //   7242: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   7245: ifeq +317 -> 7562
    //   7248: aload_1
    //   7249: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   7252: ifeq +310 -> 7562
    //   7255: aload_1
    //   7256: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   7259: ifne +303 -> 7562
    //   7262: ldc_w 2889
    //   7265: fstore 8
    //   7267: aload_0
    //   7268: iload 10
    //   7270: fload 8
    //   7272: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7275: isub
    //   7276: ldc_w 2890
    //   7279: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7282: invokestatic 959	java/lang/Math:min	(II)I
    //   7285: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7288: aload_0
    //   7289: aload_0
    //   7290: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7293: ldc_w 380
    //   7296: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7299: isub
    //   7300: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   7303: aload_0
    //   7304: invokespecial 2079	org/vidogram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   7307: ldc_w 833
    //   7310: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7313: isub
    //   7314: istore 10
    //   7316: iload 10
    //   7318: ifge +6166 -> 13484
    //   7321: ldc_w 472
    //   7324: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7327: istore 10
    //   7329: invokestatic 1980	org/vidogram/messenger/LocaleController:getInstance	()Lorg/vidogram/messenger/LocaleController;
    //   7332: getfield 1984	org/vidogram/messenger/LocaleController:formatterDay	Lorg/vidogram/messenger/time/FastDateFormat;
    //   7335: aload_1
    //   7336: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7339: getfield 1987	org/vidogram/tgnet/TLRPC$Message:date	I
    //   7342: i2l
    //   7343: ldc2_w 1447
    //   7346: lmul
    //   7347: invokevirtual 1991	org/vidogram/messenger/time/FastDateFormat:format	(J)Ljava/lang/String;
    //   7350: astore 31
    //   7352: aload_1
    //   7353: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7356: getfield 1623	org/vidogram/tgnet/TLRPC$Message:action	Lorg/vidogram/tgnet/TLRPC$MessageAction;
    //   7359: checkcast 2892	org/vidogram/tgnet/TLRPC$TL_messageActionPhoneCall
    //   7362: astore 32
    //   7364: aload 32
    //   7366: getfield 2893	org/vidogram/tgnet/TLRPC$TL_messageActionPhoneCall:reason	Lorg/vidogram/tgnet/TLRPC$PhoneCallDiscardReason;
    //   7369: instanceof 1631
    //   7372: istore_2
    //   7373: aload_1
    //   7374: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   7377: ifeq +273 -> 7650
    //   7380: iload_2
    //   7381: ifeq +255 -> 7636
    //   7384: ldc_w 2895
    //   7387: ldc_w 2896
    //   7390: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7393: astore 30
    //   7395: aload 32
    //   7397: getfield 2897	org/vidogram/tgnet/TLRPC$TL_messageActionPhoneCall:duration	I
    //   7400: ifle +6081 -> 13481
    //   7403: new 1098	java/lang/StringBuilder
    //   7406: dup
    //   7407: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   7410: aload 31
    //   7412: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   7415: ldc_w 1993
    //   7418: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   7421: aload 32
    //   7423: getfield 2897	org/vidogram/tgnet/TLRPC$TL_messageActionPhoneCall:duration	I
    //   7426: invokestatic 2901	org/vidogram/messenger/LocaleController:formatCallDuration	(I)Ljava/lang/String;
    //   7429: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   7432: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   7435: astore 31
    //   7437: aload_0
    //   7438: new 277	android/text/StaticLayout
    //   7441: dup
    //   7442: aload 30
    //   7444: getstatic 976	org/vidogram/ui/ActionBar/Theme:chat_audioTitlePaint	Landroid/text/TextPaint;
    //   7447: iload 10
    //   7449: i2f
    //   7450: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7453: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7456: getstatic 976	org/vidogram/ui/ActionBar/Theme:chat_audioTitlePaint	Landroid/text/TextPaint;
    //   7459: fconst_2
    //   7460: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7463: iload 10
    //   7465: iadd
    //   7466: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7469: fconst_1
    //   7470: fconst_0
    //   7471: iconst_0
    //   7472: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7475: putfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   7478: aload_0
    //   7479: new 277	android/text/StaticLayout
    //   7482: dup
    //   7483: aload 31
    //   7485: getstatic 1592	org/vidogram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   7488: iload 10
    //   7490: i2f
    //   7491: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   7494: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   7497: getstatic 1592	org/vidogram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   7500: fconst_2
    //   7501: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7504: iload 10
    //   7506: iadd
    //   7507: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   7510: fconst_1
    //   7511: fconst_0
    //   7512: iconst_0
    //   7513: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   7516: putfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   7519: aload_0
    //   7520: aload_1
    //   7521: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   7524: aload_0
    //   7525: ldc_w 2556
    //   7528: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7531: aload_0
    //   7532: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7535: iadd
    //   7536: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   7539: aload_0
    //   7540: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   7543: ifeq -3066 -> 4477
    //   7546: aload_0
    //   7547: aload_0
    //   7548: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7551: fconst_1
    //   7552: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7555: isub
    //   7556: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   7559: goto -3082 -> 4477
    //   7562: ldc_w 833
    //   7565: fstore 8
    //   7567: goto -300 -> 7267
    //   7570: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   7573: getfield 1836	android/graphics/Point:x	I
    //   7576: istore 10
    //   7578: aload_0
    //   7579: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   7582: ifeq +46 -> 7628
    //   7585: aload_1
    //   7586: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   7589: ifeq +39 -> 7628
    //   7592: aload_1
    //   7593: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   7596: ifne +32 -> 7628
    //   7599: ldc_w 2889
    //   7602: fstore 8
    //   7604: aload_0
    //   7605: iload 10
    //   7607: fload 8
    //   7609: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7612: isub
    //   7613: ldc_w 2890
    //   7616: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7619: invokestatic 959	java/lang/Math:min	(II)I
    //   7622: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7625: goto -337 -> 7288
    //   7628: ldc_w 833
    //   7631: fstore 8
    //   7633: goto -29 -> 7604
    //   7636: ldc_w 2903
    //   7639: ldc_w 2904
    //   7642: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7645: astore 30
    //   7647: goto -252 -> 7395
    //   7650: iload_2
    //   7651: ifeq +17 -> 7668
    //   7654: ldc_w 2906
    //   7657: ldc_w 2907
    //   7660: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7663: astore 30
    //   7665: goto -270 -> 7395
    //   7668: aload 32
    //   7670: getfield 2893	org/vidogram/tgnet/TLRPC$TL_messageActionPhoneCall:reason	Lorg/vidogram/tgnet/TLRPC$PhoneCallDiscardReason;
    //   7673: instanceof 1633
    //   7676: ifeq +17 -> 7693
    //   7679: ldc_w 2909
    //   7682: ldc_w 2910
    //   7685: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7688: astore 30
    //   7690: goto -295 -> 7395
    //   7693: ldc_w 2912
    //   7696: ldc_w 2913
    //   7699: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   7702: astore 30
    //   7704: goto -309 -> 7395
    //   7707: aload_1
    //   7708: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   7711: bipush 12
    //   7713: if_icmpne +674 -> 8387
    //   7716: aload_0
    //   7717: iconst_0
    //   7718: putfield 1945	org/vidogram/ui/Cells/ChatMessageCell:drawName	Z
    //   7721: aload_0
    //   7722: iconst_1
    //   7723: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   7726: aload_0
    //   7727: iconst_1
    //   7728: putfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   7731: aload_0
    //   7732: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   7735: ldc_w 1606
    //   7738: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7741: invokevirtual 299	org/vidogram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   7744: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   7747: ifeq +494 -> 8241
    //   7750: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   7753: istore 10
    //   7755: aload_0
    //   7756: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   7759: ifeq +474 -> 8233
    //   7762: aload_1
    //   7763: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   7766: ifeq +467 -> 8233
    //   7769: aload_1
    //   7770: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   7773: ifne +460 -> 8233
    //   7776: ldc_w 2889
    //   7779: fstore 8
    //   7781: aload_0
    //   7782: iload 10
    //   7784: fload 8
    //   7786: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7789: isub
    //   7790: ldc_w 2890
    //   7793: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7796: invokestatic 959	java/lang/Math:min	(II)I
    //   7799: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7802: aload_0
    //   7803: aload_0
    //   7804: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   7807: ldc_w 380
    //   7810: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7813: isub
    //   7814: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   7817: aload_1
    //   7818: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7821: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   7824: getfield 863	org/vidogram/tgnet/TLRPC$MessageMedia:user_id	I
    //   7827: istore 10
    //   7829: invokestatic 767	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   7832: iload 10
    //   7834: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   7837: invokevirtual 780	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   7840: astore 31
    //   7842: aload_0
    //   7843: invokespecial 2079	org/vidogram/ui/Cells/ChatMessageCell:getMaxNameWidth	()I
    //   7846: ldc_w 2914
    //   7849: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7852: isub
    //   7853: istore 10
    //   7855: iload 10
    //   7857: ifge +5621 -> 13478
    //   7860: ldc_w 472
    //   7863: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   7866: istore 10
    //   7868: aconst_null
    //   7869: astore 30
    //   7871: aload 31
    //   7873: ifnull +5599 -> 13472
    //   7876: aload 31
    //   7878: getfield 1920	org/vidogram/tgnet/TLRPC$User:photo	Lorg/vidogram/tgnet/TLRPC$UserProfilePhoto;
    //   7881: ifnull +13 -> 7894
    //   7884: aload 31
    //   7886: getfield 1920	org/vidogram/tgnet/TLRPC$User:photo	Lorg/vidogram/tgnet/TLRPC$UserProfilePhoto;
    //   7889: getfield 1925	org/vidogram/tgnet/TLRPC$UserProfilePhoto:photo_small	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   7892: astore 30
    //   7894: aload_0
    //   7895: getfield 323	org/vidogram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   7898: aload 31
    //   7900: invokevirtual 2057	org/vidogram/ui/Components/AvatarDrawable:setInfo	(Lorg/vidogram/tgnet/TLRPC$User;)V
    //   7903: aload_0
    //   7904: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   7907: astore 32
    //   7909: aload 31
    //   7911: ifnull +396 -> 8307
    //   7914: aload_0
    //   7915: getfield 323	org/vidogram/ui/Cells/ChatMessageCell:contactAvatarDrawable	Lorg/vidogram/ui/Components/AvatarDrawable;
    //   7918: astore 31
    //   7920: aload 32
    //   7922: aload 30
    //   7924: ldc_w 2059
    //   7927: aload 31
    //   7929: aconst_null
    //   7930: iconst_0
    //   7931: invokevirtual 2062	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;Z)V
    //   7934: aload_1
    //   7935: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7938: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   7941: getfield 2917	org/vidogram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   7944: astore 30
    //   7946: aload 30
    //   7948: ifnull +390 -> 8338
    //   7951: aload 30
    //   7953: invokevirtual 813	java/lang/String:length	()I
    //   7956: ifeq +382 -> 8338
    //   7959: invokestatic 2923	org/vidogram/a/b:a	()Lorg/vidogram/a/b;
    //   7962: aload 30
    //   7964: invokevirtual 2926	org/vidogram/a/b:e	(Ljava/lang/String;)Ljava/lang/String;
    //   7967: astore 30
    //   7969: aload_1
    //   7970: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7973: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   7976: getfield 2927	org/vidogram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   7979: aload_1
    //   7980: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7983: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   7986: getfield 2928	org/vidogram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   7989: invokestatic 2025	org/vidogram/messenger/ContactsController:formatName	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   7992: bipush 10
    //   7994: bipush 32
    //   7996: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   7999: astore 31
    //   8001: aload 31
    //   8003: invokeinterface 1800 1 0
    //   8008: ifne +5461 -> 13469
    //   8011: aload 30
    //   8013: astore 31
    //   8015: aload_0
    //   8016: new 277	android/text/StaticLayout
    //   8019: dup
    //   8020: aload 31
    //   8022: getstatic 1648	org/vidogram/ui/ActionBar/Theme:chat_contactNamePaint	Landroid/text/TextPaint;
    //   8025: iload 10
    //   8027: i2f
    //   8028: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8031: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   8034: getstatic 1648	org/vidogram/ui/ActionBar/Theme:chat_contactNamePaint	Landroid/text/TextPaint;
    //   8037: fconst_2
    //   8038: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8041: iload 10
    //   8043: iadd
    //   8044: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8047: fconst_1
    //   8048: fconst_0
    //   8049: iconst_0
    //   8050: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   8053: putfield 1330	org/vidogram/ui/Cells/ChatMessageCell:titleLayout	Landroid/text/StaticLayout;
    //   8056: aload_0
    //   8057: new 277	android/text/StaticLayout
    //   8060: dup
    //   8061: aload 30
    //   8063: bipush 10
    //   8065: bipush 32
    //   8067: invokevirtual 973	java/lang/String:replace	(CC)Ljava/lang/String;
    //   8070: getstatic 1592	org/vidogram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   8073: iload 10
    //   8075: i2f
    //   8076: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   8079: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   8082: getstatic 1592	org/vidogram/ui/ActionBar/Theme:chat_contactPhonePaint	Landroid/text/TextPaint;
    //   8085: fconst_2
    //   8086: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8089: iload 10
    //   8091: iadd
    //   8092: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   8095: fconst_1
    //   8096: fconst_0
    //   8097: iconst_0
    //   8098: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   8101: putfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8104: aload_0
    //   8105: aload_1
    //   8106: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   8109: aload_0
    //   8110: getfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   8113: ifeq +239 -> 8352
    //   8116: aload_1
    //   8117: invokevirtual 2126	org/vidogram/messenger/MessageObject:isForwarded	()Z
    //   8120: ifeq +232 -> 8352
    //   8123: aload_0
    //   8124: aload_0
    //   8125: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8128: ldc_w 1486
    //   8131: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8134: iadd
    //   8135: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8138: aload_0
    //   8139: ldc_w 2929
    //   8142: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8145: aload_0
    //   8146: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8149: iadd
    //   8150: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8153: aload_0
    //   8154: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   8157: ifeq +16 -> 8173
    //   8160: aload_0
    //   8161: aload_0
    //   8162: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8165: fconst_1
    //   8166: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8169: isub
    //   8170: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8173: aload_0
    //   8174: getfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8177: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   8180: ifle -3703 -> 4477
    //   8183: aload_0
    //   8184: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8187: ldc_w 2914
    //   8190: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8193: isub
    //   8194: aload_0
    //   8195: getfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   8198: iconst_0
    //   8199: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   8202: f2d
    //   8203: invokestatic 949	java/lang/Math:ceil	(D)D
    //   8206: d2i
    //   8207: isub
    //   8208: aload_0
    //   8209: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   8212: if_icmpge -3735 -> 4477
    //   8215: aload_0
    //   8216: aload_0
    //   8217: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8220: ldc_w 652
    //   8223: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8226: iadd
    //   8227: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8230: goto -3753 -> 4477
    //   8233: ldc_w 833
    //   8236: fstore 8
    //   8238: goto -457 -> 7781
    //   8241: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8244: getfield 1836	android/graphics/Point:x	I
    //   8247: istore 10
    //   8249: aload_0
    //   8250: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8253: ifeq +46 -> 8299
    //   8256: aload_1
    //   8257: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8260: ifeq +39 -> 8299
    //   8263: aload_1
    //   8264: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8267: ifne +32 -> 8299
    //   8270: ldc_w 2889
    //   8273: fstore 8
    //   8275: aload_0
    //   8276: iload 10
    //   8278: fload 8
    //   8280: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8283: isub
    //   8284: ldc_w 2890
    //   8287: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8290: invokestatic 959	java/lang/Math:min	(II)I
    //   8293: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8296: goto -494 -> 7802
    //   8299: ldc_w 833
    //   8302: fstore 8
    //   8304: goto -29 -> 8275
    //   8307: getstatic 2933	org/vidogram/ui/ActionBar/Theme:chat_contactDrawable	[Landroid/graphics/drawable/Drawable;
    //   8310: astore 31
    //   8312: aload_1
    //   8313: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8316: ifeq +16 -> 8332
    //   8319: iconst_1
    //   8320: istore 11
    //   8322: aload 31
    //   8324: iload 11
    //   8326: aaload
    //   8327: astore 31
    //   8329: goto -409 -> 7920
    //   8332: iconst_0
    //   8333: istore 11
    //   8335: goto -13 -> 8322
    //   8338: ldc_w 2935
    //   8341: ldc_w 2936
    //   8344: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   8347: astore 30
    //   8349: goto -380 -> 7969
    //   8352: aload_0
    //   8353: getfield 2077	org/vidogram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   8356: ifeq -218 -> 8138
    //   8359: aload_1
    //   8360: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   8363: getfield 826	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   8366: ifne -228 -> 8138
    //   8369: aload_0
    //   8370: aload_0
    //   8371: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8374: ldc_w 499
    //   8377: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8380: iadd
    //   8381: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8384: goto -246 -> 8138
    //   8387: aload_1
    //   8388: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8391: iconst_2
    //   8392: if_icmpne +193 -> 8585
    //   8395: aload_0
    //   8396: iconst_1
    //   8397: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   8400: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   8403: ifeq +116 -> 8519
    //   8406: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   8409: istore 10
    //   8411: aload_0
    //   8412: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8415: ifeq +96 -> 8511
    //   8418: aload_1
    //   8419: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8422: ifeq +89 -> 8511
    //   8425: aload_1
    //   8426: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8429: ifne +82 -> 8511
    //   8432: ldc_w 2889
    //   8435: fstore 8
    //   8437: aload_0
    //   8438: iload 10
    //   8440: fload 8
    //   8442: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8445: isub
    //   8446: ldc_w 2890
    //   8449: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8452: invokestatic 959	java/lang/Math:min	(II)I
    //   8455: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8458: aload_0
    //   8459: aload_0
    //   8460: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8463: aload_1
    //   8464: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   8467: pop
    //   8468: aload_0
    //   8469: aload_1
    //   8470: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   8473: aload_0
    //   8474: ldc_w 2929
    //   8477: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8480: aload_0
    //   8481: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8484: iadd
    //   8485: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8488: aload_0
    //   8489: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   8492: ifeq -4015 -> 4477
    //   8495: aload_0
    //   8496: aload_0
    //   8497: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8500: fconst_1
    //   8501: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8504: isub
    //   8505: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8508: goto -4031 -> 4477
    //   8511: ldc_w 833
    //   8514: fstore 8
    //   8516: goto -79 -> 8437
    //   8519: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8522: getfield 1836	android/graphics/Point:x	I
    //   8525: istore 10
    //   8527: aload_0
    //   8528: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8531: ifeq +46 -> 8577
    //   8534: aload_1
    //   8535: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8538: ifeq +39 -> 8577
    //   8541: aload_1
    //   8542: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8545: ifne +32 -> 8577
    //   8548: ldc_w 2889
    //   8551: fstore 8
    //   8553: aload_0
    //   8554: iload 10
    //   8556: fload 8
    //   8558: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8561: isub
    //   8562: ldc_w 2890
    //   8565: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8568: invokestatic 959	java/lang/Math:min	(II)I
    //   8571: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8574: goto -116 -> 8458
    //   8577: ldc_w 833
    //   8580: fstore 8
    //   8582: goto -29 -> 8553
    //   8585: aload_1
    //   8586: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8589: bipush 14
    //   8591: if_icmpne +188 -> 8779
    //   8594: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   8597: ifeq +116 -> 8713
    //   8600: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   8603: istore 10
    //   8605: aload_0
    //   8606: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8609: ifeq +96 -> 8705
    //   8612: aload_1
    //   8613: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8616: ifeq +89 -> 8705
    //   8619: aload_1
    //   8620: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8623: ifne +82 -> 8705
    //   8626: ldc_w 2889
    //   8629: fstore 8
    //   8631: aload_0
    //   8632: iload 10
    //   8634: fload 8
    //   8636: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8639: isub
    //   8640: ldc_w 2890
    //   8643: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8646: invokestatic 959	java/lang/Math:min	(II)I
    //   8649: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8652: aload_0
    //   8653: aload_0
    //   8654: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8657: aload_1
    //   8658: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   8661: pop
    //   8662: aload_0
    //   8663: aload_1
    //   8664: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   8667: aload_0
    //   8668: ldc_w 2937
    //   8671: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8674: aload_0
    //   8675: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8678: iadd
    //   8679: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   8682: aload_0
    //   8683: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   8686: ifeq -4209 -> 4477
    //   8689: aload_0
    //   8690: aload_0
    //   8691: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8694: fconst_1
    //   8695: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8698: isub
    //   8699: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   8702: goto -4225 -> 4477
    //   8705: ldc_w 833
    //   8708: fstore 8
    //   8710: goto -79 -> 8631
    //   8713: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   8716: getfield 1836	android/graphics/Point:x	I
    //   8719: istore 10
    //   8721: aload_0
    //   8722: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8725: ifeq +46 -> 8771
    //   8728: aload_1
    //   8729: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8732: ifeq +39 -> 8771
    //   8735: aload_1
    //   8736: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8739: ifne +32 -> 8771
    //   8742: ldc_w 2889
    //   8745: fstore 8
    //   8747: aload_0
    //   8748: iload 10
    //   8750: fload 8
    //   8752: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8755: isub
    //   8756: ldc_w 2890
    //   8759: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8762: invokestatic 959	java/lang/Math:min	(II)I
    //   8765: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8768: goto -116 -> 8652
    //   8771: ldc_w 833
    //   8774: fstore 8
    //   8776: goto -29 -> 8747
    //   8779: aload_1
    //   8780: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   8783: getfield 745	org/vidogram/tgnet/TLRPC$Message:fwd_from	Lorg/vidogram/tgnet/TLRPC$TL_messageFwdHeader;
    //   8786: ifnull +483 -> 9269
    //   8789: aload_1
    //   8790: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8793: bipush 13
    //   8795: if_icmpeq +474 -> 9269
    //   8798: iconst_1
    //   8799: istore_2
    //   8800: aload_0
    //   8801: iload_2
    //   8802: putfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   8805: aload_1
    //   8806: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8809: bipush 9
    //   8811: if_icmpeq +463 -> 9274
    //   8814: iconst_1
    //   8815: istore_2
    //   8816: aload_0
    //   8817: iload_2
    //   8818: putfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   8821: aload_0
    //   8822: iconst_1
    //   8823: putfield 657	org/vidogram/ui/Cells/ChatMessageCell:drawImageButton	Z
    //   8826: aload_0
    //   8827: iconst_1
    //   8828: putfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   8831: iconst_0
    //   8832: istore 17
    //   8834: iconst_0
    //   8835: istore 15
    //   8837: aload_1
    //   8838: getfield 705	org/vidogram/messenger/MessageObject:audioProgress	F
    //   8841: fconst_2
    //   8842: fcmpl
    //   8843: ifeq +26 -> 8869
    //   8846: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   8849: invokevirtual 681	org/vidogram/messenger/MediaController:canAutoplayGifs	()Z
    //   8852: ifne +17 -> 8869
    //   8855: aload_1
    //   8856: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8859: bipush 8
    //   8861: if_icmpne +8 -> 8869
    //   8864: aload_1
    //   8865: fconst_1
    //   8866: putfield 705	org/vidogram/messenger/MessageObject:audioProgress	F
    //   8869: aload_0
    //   8870: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   8873: astore 30
    //   8875: aload_1
    //   8876: getfield 705	org/vidogram/messenger/MessageObject:audioProgress	F
    //   8879: fconst_0
    //   8880: fcmpl
    //   8881: ifne +398 -> 9279
    //   8884: iconst_1
    //   8885: istore_2
    //   8886: aload 30
    //   8888: iload_2
    //   8889: invokevirtual 708	org/vidogram/messenger/ImageReceiver:setAllowStartAnimation	(Z)V
    //   8892: aload_0
    //   8893: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   8896: aload_1
    //   8897: invokevirtual 847	org/vidogram/messenger/MessageObject:isSecretPhoto	()Z
    //   8900: invokevirtual 2940	org/vidogram/messenger/ImageReceiver:setForcePreview	(Z)V
    //   8903: aload_1
    //   8904: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   8907: bipush 9
    //   8909: if_icmpne +501 -> 9410
    //   8912: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   8915: ifeq +377 -> 9292
    //   8918: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   8921: istore 10
    //   8923: aload_0
    //   8924: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   8927: ifeq +357 -> 9284
    //   8930: aload_1
    //   8931: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   8934: ifeq +350 -> 9284
    //   8937: aload_1
    //   8938: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   8941: ifne +343 -> 9284
    //   8944: ldc_w 2889
    //   8947: fstore 8
    //   8949: aload_0
    //   8950: iload 10
    //   8952: fload 8
    //   8954: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8957: isub
    //   8958: ldc_w 2890
    //   8961: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8964: invokestatic 959	java/lang/Math:min	(II)I
    //   8967: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8970: aload_0
    //   8971: aload_1
    //   8972: invokespecial 2704	org/vidogram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/vidogram/messenger/MessageObject;)Z
    //   8975: ifeq +18 -> 8993
    //   8978: aload_0
    //   8979: aload_0
    //   8980: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8983: ldc_w 836
    //   8986: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   8989: isub
    //   8990: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8993: aload_0
    //   8994: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   8997: ldc_w 2941
    //   9000: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9003: isub
    //   9004: istore 11
    //   9006: aload_0
    //   9007: iload 11
    //   9009: aload_1
    //   9010: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   9013: pop
    //   9014: iload 11
    //   9016: istore 10
    //   9018: aload_1
    //   9019: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   9022: invokestatic 695	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   9025: ifne +14 -> 9039
    //   9028: iload 11
    //   9030: ldc_w 966
    //   9033: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9036: iadd
    //   9037: istore 10
    //   9039: aload_0
    //   9040: getfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   9043: ifeq +315 -> 9358
    //   9046: ldc_w 966
    //   9049: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9052: istore 11
    //   9054: ldc_w 966
    //   9057: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9060: istore 12
    //   9062: aload_0
    //   9063: iload 10
    //   9065: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9068: iload 12
    //   9070: istore 10
    //   9072: aload_0
    //   9073: getfield 600	org/vidogram/ui/Cells/ChatMessageCell:drawPhotoImage	Z
    //   9076: ifne +83 -> 9159
    //   9079: iload 12
    //   9081: istore 10
    //   9083: aload_1
    //   9084: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   9087: invokestatic 695	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   9090: ifeq +69 -> 9159
    //   9093: iload 12
    //   9095: istore 10
    //   9097: aload_0
    //   9098: getfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   9101: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   9104: ifle +55 -> 9159
    //   9107: aload_0
    //   9108: aload_1
    //   9109: invokespecial 955	org/vidogram/ui/Cells/ChatMessageCell:measureTime	(Lorg/vidogram/messenger/MessageObject;)V
    //   9112: iload 12
    //   9114: istore 10
    //   9116: aload_0
    //   9117: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9120: ldc_w 2705
    //   9123: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9126: isub
    //   9127: aload_0
    //   9128: getfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   9131: iconst_0
    //   9132: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   9135: f2d
    //   9136: invokestatic 949	java/lang/Math:ceil	(D)D
    //   9139: d2i
    //   9140: isub
    //   9141: aload_0
    //   9142: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   9145: if_icmpge +14 -> 9159
    //   9148: iload 12
    //   9150: ldc_w 652
    //   9153: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9156: iadd
    //   9157: istore 10
    //   9159: iload 15
    //   9161: istore 14
    //   9163: iload 11
    //   9165: istore 12
    //   9167: iload 10
    //   9169: istore 11
    //   9171: aload_0
    //   9172: aload_1
    //   9173: invokespecial 2711	org/vidogram/ui/Cells/ChatMessageCell:setMessageObjectInternal	(Lorg/vidogram/messenger/MessageObject;)V
    //   9176: aload_0
    //   9177: getfield 1955	org/vidogram/ui/Cells/ChatMessageCell:drawForwardedName	Z
    //   9180: ifeq +3451 -> 12631
    //   9183: aload_0
    //   9184: aload_0
    //   9185: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9188: ldc_w 1486
    //   9191: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9194: iadd
    //   9195: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9198: aload_0
    //   9199: ldc_w 373
    //   9202: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9205: iload 11
    //   9207: iadd
    //   9208: aload_0
    //   9209: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9212: iadd
    //   9213: iload 14
    //   9215: iadd
    //   9216: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   9219: aload_0
    //   9220: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   9223: ifeq +16 -> 9239
    //   9226: aload_0
    //   9227: aload_0
    //   9228: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9231: fconst_1
    //   9232: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9235: isub
    //   9236: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9239: aload_0
    //   9240: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   9243: iconst_0
    //   9244: ldc_w 499
    //   9247: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9250: aload_0
    //   9251: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   9254: iadd
    //   9255: iload 12
    //   9257: iload 11
    //   9259: invokevirtual 1316	org/vidogram/messenger/ImageReceiver:setImageCoords	(IIII)V
    //   9262: aload_0
    //   9263: invokevirtual 424	org/vidogram/ui/Cells/ChatMessageCell:invalidate	()V
    //   9266: goto -4789 -> 4477
    //   9269: iconst_0
    //   9270: istore_2
    //   9271: goto -471 -> 8800
    //   9274: iconst_0
    //   9275: istore_2
    //   9276: goto -460 -> 8816
    //   9279: iconst_0
    //   9280: istore_2
    //   9281: goto -395 -> 8886
    //   9284: ldc_w 833
    //   9287: fstore 8
    //   9289: goto -340 -> 8949
    //   9292: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9295: getfield 1836	android/graphics/Point:x	I
    //   9298: istore 10
    //   9300: aload_0
    //   9301: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   9304: ifeq +46 -> 9350
    //   9307: aload_1
    //   9308: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   9311: ifeq +39 -> 9350
    //   9314: aload_1
    //   9315: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   9318: ifne +32 -> 9350
    //   9321: ldc_w 2889
    //   9324: fstore 8
    //   9326: aload_0
    //   9327: iload 10
    //   9329: fload 8
    //   9331: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9334: isub
    //   9335: ldc_w 2890
    //   9338: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9341: invokestatic 959	java/lang/Math:min	(II)I
    //   9344: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9347: goto -377 -> 8970
    //   9350: ldc_w 833
    //   9353: fstore 8
    //   9355: goto -29 -> 9326
    //   9358: ldc_w 1666
    //   9361: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9364: istore 11
    //   9366: ldc_w 1666
    //   9369: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9372: istore 12
    //   9374: aload_1
    //   9375: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   9378: invokestatic 695	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   9381: ifeq +21 -> 9402
    //   9384: ldc_w 1563
    //   9387: fstore 8
    //   9389: iload 10
    //   9391: fload 8
    //   9393: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9396: iadd
    //   9397: istore 10
    //   9399: goto -337 -> 9062
    //   9402: ldc_w 289
    //   9405: fstore 8
    //   9407: goto -18 -> 9389
    //   9410: aload_1
    //   9411: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   9414: iconst_4
    //   9415: if_icmpne +613 -> 10028
    //   9418: aload_1
    //   9419: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9422: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9425: getfield 1854	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   9428: getfield 1860	org/vidogram/tgnet/TLRPC$GeoPoint:lat	D
    //   9431: dstore 4
    //   9433: aload_1
    //   9434: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9437: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9440: getfield 1854	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   9443: getfield 1863	org/vidogram/tgnet/TLRPC$GeoPoint:_long	D
    //   9446: dstore 6
    //   9448: aload_1
    //   9449: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9452: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9455: getfield 2212	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   9458: ifnull +455 -> 9913
    //   9461: aload_1
    //   9462: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9465: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9468: getfield 2212	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   9471: invokevirtual 813	java/lang/String:length	()I
    //   9474: ifle +439 -> 9913
    //   9477: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   9480: ifeq +359 -> 9839
    //   9483: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   9486: istore 10
    //   9488: aload_0
    //   9489: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   9492: ifeq +339 -> 9831
    //   9495: aload_1
    //   9496: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   9499: ifeq +332 -> 9831
    //   9502: aload_1
    //   9503: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   9506: ifne +325 -> 9831
    //   9509: ldc_w 2889
    //   9512: fstore 8
    //   9514: aload_0
    //   9515: iload 10
    //   9517: fload 8
    //   9519: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9522: isub
    //   9523: ldc_w 2890
    //   9526: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9529: invokestatic 959	java/lang/Math:min	(II)I
    //   9532: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9535: aload_0
    //   9536: aload_1
    //   9537: invokespecial 2704	org/vidogram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/vidogram/messenger/MessageObject;)Z
    //   9540: ifeq +18 -> 9558
    //   9543: aload_0
    //   9544: aload_0
    //   9545: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9548: ldc_w 836
    //   9551: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9554: isub
    //   9555: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9558: aload_0
    //   9559: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9562: ldc_w 2942
    //   9565: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9568: isub
    //   9569: istore 10
    //   9571: aload_0
    //   9572: aload_1
    //   9573: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9576: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9579: getfield 2212	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   9582: getstatic 1574	org/vidogram/ui/ActionBar/Theme:chat_locationTitlePaint	Landroid/text/TextPaint;
    //   9585: iload 10
    //   9587: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9590: fconst_1
    //   9591: fconst_0
    //   9592: iconst_0
    //   9593: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   9596: iload 10
    //   9598: iconst_2
    //   9599: invokestatic 1091	org/vidogram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   9602: putfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   9605: aload_0
    //   9606: getfield 1093	org/vidogram/ui/Cells/ChatMessageCell:docTitleLayout	Landroid/text/StaticLayout;
    //   9609: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   9612: istore 11
    //   9614: aload_1
    //   9615: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9618: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9621: getfield 2945	org/vidogram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   9624: ifnull +281 -> 9905
    //   9627: aload_1
    //   9628: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9631: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9634: getfield 2945	org/vidogram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   9637: invokevirtual 813	java/lang/String:length	()I
    //   9640: ifle +265 -> 9905
    //   9643: aload_0
    //   9644: aload_1
    //   9645: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   9648: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   9651: getfield 2945	org/vidogram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   9654: getstatic 1579	org/vidogram/ui/ActionBar/Theme:chat_locationAddressPaint	Landroid/text/TextPaint;
    //   9657: iload 10
    //   9659: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   9662: fconst_1
    //   9663: fconst_0
    //   9664: iconst_0
    //   9665: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   9668: iload 10
    //   9670: iconst_3
    //   9671: iconst_3
    //   9672: iload 11
    //   9674: isub
    //   9675: invokestatic 959	java/lang/Math:min	(II)I
    //   9678: invokestatic 1091	org/vidogram/ui/Components/StaticLayoutEx:createStaticLayout	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZLandroid/text/TextUtils$TruncateAt;II)Landroid/text/StaticLayout;
    //   9681: putfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   9684: aload_0
    //   9685: iconst_0
    //   9686: putfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   9689: aload_0
    //   9690: iload 10
    //   9692: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9695: ldc_w 966
    //   9698: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9701: istore 10
    //   9703: ldc_w 966
    //   9706: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9709: istore 11
    //   9711: aload_0
    //   9712: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   9715: ldc_w 2947
    //   9718: iconst_5
    //   9719: anewarray 1016	java/lang/Object
    //   9722: dup
    //   9723: iconst_0
    //   9724: dload 4
    //   9726: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9729: aastore
    //   9730: dup
    //   9731: iconst_1
    //   9732: dload 6
    //   9734: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9737: aastore
    //   9738: dup
    //   9739: iconst_2
    //   9740: iconst_2
    //   9741: getstatic 1879	org/vidogram/messenger/AndroidUtilities:density	F
    //   9744: f2d
    //   9745: invokestatic 949	java/lang/Math:ceil	(D)D
    //   9748: d2i
    //   9749: invokestatic 959	java/lang/Math:min	(II)I
    //   9752: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9755: aastore
    //   9756: dup
    //   9757: iconst_3
    //   9758: dload 4
    //   9760: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9763: aastore
    //   9764: dup
    //   9765: iconst_4
    //   9766: dload 6
    //   9768: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9771: aastore
    //   9772: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   9775: putfield 1850	org/vidogram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   9778: aload_0
    //   9779: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   9782: astore 30
    //   9784: aload_0
    //   9785: getfield 1850	org/vidogram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   9788: astore 31
    //   9790: getstatic 2950	org/vidogram/ui/ActionBar/Theme:chat_locationDrawable	[Landroid/graphics/drawable/Drawable;
    //   9793: astore 32
    //   9795: aload_1
    //   9796: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   9799: ifeq +223 -> 10022
    //   9802: iconst_1
    //   9803: istore 12
    //   9805: aload 30
    //   9807: aload 31
    //   9809: aconst_null
    //   9810: aload 32
    //   9812: iload 12
    //   9814: aaload
    //   9815: aconst_null
    //   9816: iconst_0
    //   9817: invokevirtual 2953	org/vidogram/messenger/ImageReceiver:setImage	(Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Ljava/lang/String;I)V
    //   9820: iload 10
    //   9822: istore 12
    //   9824: iload 15
    //   9826: istore 14
    //   9828: goto -657 -> 9171
    //   9831: ldc_w 833
    //   9834: fstore 8
    //   9836: goto -322 -> 9514
    //   9839: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   9842: getfield 1836	android/graphics/Point:x	I
    //   9845: istore 10
    //   9847: aload_0
    //   9848: getfield 1614	org/vidogram/ui/Cells/ChatMessageCell:isChat	Z
    //   9851: ifeq +46 -> 9897
    //   9854: aload_1
    //   9855: invokevirtual 756	org/vidogram/messenger/MessageObject:isFromUser	()Z
    //   9858: ifeq +39 -> 9897
    //   9861: aload_1
    //   9862: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   9865: ifne +32 -> 9897
    //   9868: ldc_w 2889
    //   9871: fstore 8
    //   9873: aload_0
    //   9874: iload 10
    //   9876: fload 8
    //   9878: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9881: isub
    //   9882: ldc_w 2890
    //   9885: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9888: invokestatic 959	java/lang/Math:min	(II)I
    //   9891: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9894: goto -359 -> 9535
    //   9897: ldc_w 833
    //   9900: fstore 8
    //   9902: goto -29 -> 9873
    //   9905: aload_0
    //   9906: aconst_null
    //   9907: putfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   9910: goto -226 -> 9684
    //   9913: aload_0
    //   9914: ldc_w 2954
    //   9917: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9920: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   9923: ldc_w 1768
    //   9926: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9929: istore 10
    //   9931: ldc_w 2082
    //   9934: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9937: istore 11
    //   9939: aload_0
    //   9940: ldc_w 438
    //   9943: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   9946: iload 10
    //   9948: iadd
    //   9949: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   9952: aload_0
    //   9953: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   9956: ldc_w 2956
    //   9959: iconst_5
    //   9960: anewarray 1016	java/lang/Object
    //   9963: dup
    //   9964: iconst_0
    //   9965: dload 4
    //   9967: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9970: aastore
    //   9971: dup
    //   9972: iconst_1
    //   9973: dload 6
    //   9975: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   9978: aastore
    //   9979: dup
    //   9980: iconst_2
    //   9981: iconst_2
    //   9982: getstatic 1879	org/vidogram/messenger/AndroidUtilities:density	F
    //   9985: f2d
    //   9986: invokestatic 949	java/lang/Math:ceil	(D)D
    //   9989: d2i
    //   9990: invokestatic 959	java/lang/Math:min	(II)I
    //   9993: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   9996: aastore
    //   9997: dup
    //   9998: iconst_3
    //   9999: dload 4
    //   10001: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   10004: aastore
    //   10005: dup
    //   10006: iconst_4
    //   10007: dload 6
    //   10009: invokestatic 1876	java/lang/Double:valueOf	(D)Ljava/lang/Double;
    //   10012: aastore
    //   10013: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   10016: putfield 1850	org/vidogram/ui/Cells/ChatMessageCell:currentUrl	Ljava/lang/String;
    //   10019: goto -241 -> 9778
    //   10022: iconst_0
    //   10023: istore 12
    //   10025: goto -220 -> 9805
    //   10028: aload_1
    //   10029: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   10032: bipush 13
    //   10034: if_icmpne +530 -> 10564
    //   10037: aload_0
    //   10038: iconst_0
    //   10039: putfield 273	org/vidogram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   10042: iconst_0
    //   10043: istore 10
    //   10045: iload 10
    //   10047: aload_1
    //   10048: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10051: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10054: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10057: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10060: invokevirtual 475	java/util/ArrayList:size	()I
    //   10063: if_icmpge +3397 -> 13460
    //   10066: aload_1
    //   10067: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10070: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10073: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10076: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10079: iload 10
    //   10081: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   10084: checkcast 925	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   10087: astore 30
    //   10089: aload 30
    //   10091: instanceof 2770
    //   10094: ifeq +277 -> 10371
    //   10097: aload 30
    //   10099: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   10102: istore 10
    //   10104: aload 30
    //   10106: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   10109: istore 11
    //   10111: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   10114: ifeq +266 -> 10380
    //   10117: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10120: i2f
    //   10121: ldc_w 2957
    //   10124: fmul
    //   10125: fstore 9
    //   10127: fload 9
    //   10129: fstore 8
    //   10131: iload 11
    //   10133: istore 12
    //   10135: iload 10
    //   10137: istore 11
    //   10139: iload 10
    //   10141: ifne +19 -> 10160
    //   10144: fload 8
    //   10146: f2i
    //   10147: istore 12
    //   10149: ldc_w 2082
    //   10152: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10155: iload 12
    //   10157: iadd
    //   10158: istore 11
    //   10160: iload 12
    //   10162: i2f
    //   10163: fload 9
    //   10165: iload 11
    //   10167: i2f
    //   10168: fdiv
    //   10169: fmul
    //   10170: f2i
    //   10171: istore 10
    //   10173: fload 9
    //   10175: f2i
    //   10176: istore 13
    //   10178: iload 10
    //   10180: i2f
    //   10181: fload 8
    //   10183: fcmpl
    //   10184: ifle +3273 -> 13457
    //   10187: iload 13
    //   10189: i2f
    //   10190: fstore 9
    //   10192: fload 8
    //   10194: iload 10
    //   10196: i2f
    //   10197: fdiv
    //   10198: fload 9
    //   10200: fmul
    //   10201: f2i
    //   10202: istore 13
    //   10204: fload 8
    //   10206: f2i
    //   10207: istore 10
    //   10209: aload_0
    //   10210: bipush 6
    //   10212: putfield 386	org/vidogram/ui/Cells/ChatMessageCell:documentAttachType	I
    //   10215: aload_0
    //   10216: iload 13
    //   10218: ldc_w 373
    //   10221: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10224: isub
    //   10225: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   10228: aload_0
    //   10229: ldc_w 438
    //   10232: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10235: iload 13
    //   10237: iadd
    //   10238: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   10241: aload_0
    //   10242: aload_1
    //   10243: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10246: bipush 80
    //   10248: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10251: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10254: aload_1
    //   10255: getfield 2240	org/vidogram/messenger/MessageObject:attachPathExists	Z
    //   10258: ifeq +157 -> 10415
    //   10261: aload_0
    //   10262: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   10265: astore 31
    //   10267: aload_1
    //   10268: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10271: getfield 2960	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   10274: astore 32
    //   10276: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   10279: ldc_w 2780
    //   10282: iconst_2
    //   10283: anewarray 1016	java/lang/Object
    //   10286: dup
    //   10287: iconst_0
    //   10288: iload 13
    //   10290: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10293: aastore
    //   10294: dup
    //   10295: iconst_1
    //   10296: iload 10
    //   10298: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10301: aastore
    //   10302: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   10305: astore 33
    //   10307: aload_0
    //   10308: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10311: ifnull +98 -> 10409
    //   10314: aload_0
    //   10315: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10318: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   10321: astore 30
    //   10323: aload 31
    //   10325: aconst_null
    //   10326: aload 32
    //   10328: aload 33
    //   10330: aconst_null
    //   10331: aload 30
    //   10333: ldc_w 2784
    //   10336: aload_1
    //   10337: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10340: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10343: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10346: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   10349: ldc_w 2863
    //   10352: iconst_1
    //   10353: invokevirtual 1140	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   10356: iload 10
    //   10358: istore 11
    //   10360: iload 13
    //   10362: istore 12
    //   10364: iload 15
    //   10366: istore 14
    //   10368: goto -1197 -> 9171
    //   10371: iload 10
    //   10373: iconst_1
    //   10374: iadd
    //   10375: istore 10
    //   10377: goto -332 -> 10045
    //   10380: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10383: getfield 1836	android/graphics/Point:x	I
    //   10386: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   10389: getfield 1839	android/graphics/Point:y	I
    //   10392: invokestatic 959	java/lang/Math:min	(II)I
    //   10395: i2f
    //   10396: ldc_w 2778
    //   10399: fmul
    //   10400: fstore 9
    //   10402: fload 9
    //   10404: fstore 8
    //   10406: goto -275 -> 10131
    //   10409: aconst_null
    //   10410: astore 30
    //   10412: goto -89 -> 10323
    //   10415: iload 10
    //   10417: istore 11
    //   10419: iload 13
    //   10421: istore 12
    //   10423: iload 15
    //   10425: istore 14
    //   10427: aload_1
    //   10428: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10431: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10434: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10437: getfield 2962	org/vidogram/tgnet/TLRPC$Document:id	J
    //   10440: lconst_0
    //   10441: lcmp
    //   10442: ifeq -1271 -> 9171
    //   10445: aload_0
    //   10446: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   10449: astore 31
    //   10451: aload_1
    //   10452: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10455: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10458: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10461: astore 32
    //   10463: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   10466: ldc_w 2780
    //   10469: iconst_2
    //   10470: anewarray 1016	java/lang/Object
    //   10473: dup
    //   10474: iconst_0
    //   10475: iload 13
    //   10477: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10480: aastore
    //   10481: dup
    //   10482: iconst_1
    //   10483: iload 10
    //   10485: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   10488: aastore
    //   10489: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   10492: astore 33
    //   10494: aload_0
    //   10495: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10498: ifnull +60 -> 10558
    //   10501: aload_0
    //   10502: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10505: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   10508: astore 30
    //   10510: aload 31
    //   10512: aload 32
    //   10514: aconst_null
    //   10515: aload 33
    //   10517: aconst_null
    //   10518: aload 30
    //   10520: ldc_w 2784
    //   10523: aload_1
    //   10524: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10527: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10530: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10533: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   10536: ldc_w 2863
    //   10539: iconst_1
    //   10540: invokevirtual 1140	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   10543: iload 10
    //   10545: istore 11
    //   10547: iload 13
    //   10549: istore 12
    //   10551: iload 15
    //   10553: istore 14
    //   10555: goto -1384 -> 9171
    //   10558: aconst_null
    //   10559: astore 30
    //   10561: goto -51 -> 10510
    //   10564: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   10567: ifeq +1040 -> 11607
    //   10570: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   10573: i2f
    //   10574: ldc_w 2963
    //   10577: fmul
    //   10578: f2i
    //   10579: istore 10
    //   10581: iload 10
    //   10583: istore 11
    //   10585: ldc_w 2082
    //   10588: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10591: iload 11
    //   10593: iadd
    //   10594: istore 12
    //   10596: aload_0
    //   10597: aload_1
    //   10598: invokespecial 2704	org/vidogram/ui/Cells/ChatMessageCell:checkNeedDrawShareButton	(Lorg/vidogram/messenger/MessageObject;)Z
    //   10601: ifeq +2849 -> 13450
    //   10604: ldc_w 836
    //   10607: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10610: istore 13
    //   10612: iload 11
    //   10614: ldc_w 836
    //   10617: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10620: isub
    //   10621: istore 11
    //   10623: iload 10
    //   10625: iload 13
    //   10627: isub
    //   10628: istore 15
    //   10630: iload 11
    //   10632: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   10635: if_icmple +2808 -> 13443
    //   10638: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   10641: istore 16
    //   10643: iload 12
    //   10645: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   10648: if_icmple +2792 -> 13440
    //   10651: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   10654: istore 12
    //   10656: aload_1
    //   10657: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   10660: iconst_1
    //   10661: if_icmpne +976 -> 11637
    //   10664: aload_0
    //   10665: aload_1
    //   10666: invokespecial 1478	org/vidogram/ui/Cells/ChatMessageCell:updateSecretTimeText	(Lorg/vidogram/messenger/MessageObject;)V
    //   10669: aload_0
    //   10670: aload_1
    //   10671: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10674: bipush 80
    //   10676: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10679: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10682: aload_1
    //   10683: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   10686: ifnull +8 -> 10694
    //   10689: aload_0
    //   10690: iconst_0
    //   10691: putfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   10694: aload_0
    //   10695: aload_1
    //   10696: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   10699: invokestatic 1117	org/vidogram/messenger/AndroidUtilities:getPhotoSize	()I
    //   10702: invokestatic 1121	org/vidogram/messenger/FileLoader:getClosestPhotoSizeWithSize	(Ljava/util/ArrayList;I)Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10705: putfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10708: iconst_0
    //   10709: istore 11
    //   10711: iconst_0
    //   10712: istore 10
    //   10714: aload_0
    //   10715: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10718: ifnull +19 -> 10737
    //   10721: aload_0
    //   10722: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10725: aload_0
    //   10726: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10729: if_acmpne +8 -> 10737
    //   10732: aload_0
    //   10733: aconst_null
    //   10734: putfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10737: aload_0
    //   10738: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10741: ifnull +108 -> 10849
    //   10744: aload_0
    //   10745: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10748: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   10751: i2f
    //   10752: iload 16
    //   10754: i2f
    //   10755: fdiv
    //   10756: fstore 8
    //   10758: aload_0
    //   10759: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10762: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   10765: i2f
    //   10766: fload 8
    //   10768: fdiv
    //   10769: f2i
    //   10770: istore 11
    //   10772: aload_0
    //   10773: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   10776: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   10779: i2f
    //   10780: fload 8
    //   10782: fdiv
    //   10783: f2i
    //   10784: istore 10
    //   10786: iload 11
    //   10788: istore 13
    //   10790: iload 11
    //   10792: ifne +11 -> 10803
    //   10795: ldc_w 2773
    //   10798: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10801: istore 13
    //   10803: iload 10
    //   10805: istore 14
    //   10807: iload 10
    //   10809: ifne +11 -> 10820
    //   10812: ldc_w 2773
    //   10815: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   10818: istore 14
    //   10820: iload 14
    //   10822: iload 12
    //   10824: if_icmple +953 -> 11777
    //   10827: iload 14
    //   10829: i2f
    //   10830: iload 12
    //   10832: i2f
    //   10833: fdiv
    //   10834: fstore 8
    //   10836: iload 13
    //   10838: i2f
    //   10839: fload 8
    //   10841: fdiv
    //   10842: f2i
    //   10843: istore 11
    //   10845: iload 12
    //   10847: istore 10
    //   10849: iload 11
    //   10851: ifeq +8 -> 10859
    //   10854: iload 10
    //   10856: ifne +2581 -> 13437
    //   10859: aload_1
    //   10860: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   10863: bipush 8
    //   10865: if_icmpne +2572 -> 13437
    //   10868: iconst_0
    //   10869: istore 13
    //   10871: iload 13
    //   10873: aload_1
    //   10874: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10877: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10880: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10883: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10886: invokevirtual 475	java/util/ArrayList:size	()I
    //   10889: if_icmpge +2548 -> 13437
    //   10892: aload_1
    //   10893: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   10896: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   10899: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   10902: getfield 923	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   10905: iload 13
    //   10907: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   10910: checkcast 925	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   10913: astore 30
    //   10915: aload 30
    //   10917: instanceof 2770
    //   10920: ifne +11 -> 10931
    //   10923: aload 30
    //   10925: instanceof 1025
    //   10928: ifeq +1007 -> 11935
    //   10931: aload 30
    //   10933: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   10936: i2f
    //   10937: iload 16
    //   10939: i2f
    //   10940: fdiv
    //   10941: fstore 8
    //   10943: aload 30
    //   10945: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   10948: i2f
    //   10949: fload 8
    //   10951: fdiv
    //   10952: f2i
    //   10953: istore 11
    //   10955: aload 30
    //   10957: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   10960: i2f
    //   10961: fload 8
    //   10963: fdiv
    //   10964: f2i
    //   10965: istore 10
    //   10967: iload 10
    //   10969: iload 12
    //   10971: if_icmple +894 -> 11865
    //   10974: iload 10
    //   10976: i2f
    //   10977: iload 12
    //   10979: i2f
    //   10980: fdiv
    //   10981: fstore 8
    //   10983: iload 11
    //   10985: i2f
    //   10986: fload 8
    //   10988: fdiv
    //   10989: f2i
    //   10990: istore 11
    //   10992: iload 12
    //   10994: istore 10
    //   10996: iload 11
    //   10998: ifeq +12 -> 11010
    //   11001: iload 10
    //   11003: istore 12
    //   11005: iload 10
    //   11007: ifne +15 -> 11022
    //   11010: ldc_w 2773
    //   11013: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11016: istore 12
    //   11018: iload 12
    //   11020: istore 11
    //   11022: iload 11
    //   11024: istore 10
    //   11026: aload_1
    //   11027: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11030: iconst_3
    //   11031: if_icmpne +36 -> 11067
    //   11034: iload 11
    //   11036: istore 10
    //   11038: iload 11
    //   11040: aload_0
    //   11041: getfield 1038	org/vidogram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11044: ldc_w 1706
    //   11047: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11050: iadd
    //   11051: if_icmpge +16 -> 11067
    //   11054: aload_0
    //   11055: getfield 1038	org/vidogram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11058: ldc_w 1706
    //   11061: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11064: iadd
    //   11065: istore 10
    //   11067: aload_0
    //   11068: iload 15
    //   11070: ldc_w 373
    //   11073: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11076: isub
    //   11077: putfield 951	org/vidogram/ui/Cells/ChatMessageCell:availableTimeWidth	I
    //   11080: aload_0
    //   11081: aload_1
    //   11082: invokespecial 955	org/vidogram/ui/Cells/ChatMessageCell:measureTime	(Lorg/vidogram/messenger/MessageObject;)V
    //   11085: aload_0
    //   11086: getfield 382	org/vidogram/ui/Cells/ChatMessageCell:timeWidth	I
    //   11089: istore 13
    //   11091: aload_1
    //   11092: invokevirtual 466	org/vidogram/messenger/MessageObject:isOutOwner	()Z
    //   11095: ifeq +849 -> 11944
    //   11098: bipush 20
    //   11100: istore 11
    //   11102: iload 13
    //   11104: iload 11
    //   11106: bipush 14
    //   11108: iadd
    //   11109: i2f
    //   11110: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11113: iadd
    //   11114: istore 14
    //   11116: iload 10
    //   11118: istore 11
    //   11120: iload 10
    //   11122: iload 14
    //   11124: if_icmpge +7 -> 11131
    //   11127: iload 14
    //   11129: istore 11
    //   11131: iload 12
    //   11133: istore 10
    //   11135: aload_1
    //   11136: invokevirtual 847	org/vidogram/messenger/MessageObject:isSecretPhoto	()Z
    //   11139: ifeq +24 -> 11163
    //   11142: invokestatic 1823	org/vidogram/messenger/AndroidUtilities:isTablet	()Z
    //   11145: ifeq +805 -> 11950
    //   11148: invokestatic 1826	org/vidogram/messenger/AndroidUtilities:getMinTabletSide	()I
    //   11151: i2f
    //   11152: ldc_w 2778
    //   11155: fmul
    //   11156: f2i
    //   11157: istore 10
    //   11159: iload 10
    //   11161: istore 11
    //   11163: aload_1
    //   11164: invokevirtual 1153	org/vidogram/messenger/MessageObject:isVideoVoice	()Z
    //   11167: ifeq +2249 -> 13416
    //   11170: iload 11
    //   11172: iload 10
    //   11174: invokestatic 959	java/lang/Math:min	(II)I
    //   11177: istore 10
    //   11179: aload_0
    //   11180: iconst_0
    //   11181: putfield 273	org/vidogram/ui/Cells/ChatMessageCell:drawBackground	Z
    //   11184: aload_0
    //   11185: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11188: iload 10
    //   11190: iconst_2
    //   11191: idiv
    //   11192: invokevirtual 299	org/vidogram/messenger/ImageReceiver:setRoundRadius	(I)V
    //   11195: iload 10
    //   11197: istore 11
    //   11199: iload 10
    //   11201: istore 12
    //   11203: aload_0
    //   11204: ldc_w 438
    //   11207: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11210: iload 12
    //   11212: iadd
    //   11213: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11216: aload_0
    //   11217: getfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   11220: ifne +18 -> 11238
    //   11223: aload_0
    //   11224: aload_0
    //   11225: getfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11228: ldc_w 1406
    //   11231: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11234: iadd
    //   11235: putfield 275	org/vidogram/ui/Cells/ChatMessageCell:backgroundWidth	I
    //   11238: iload 17
    //   11240: istore 10
    //   11242: aload_1
    //   11243: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   11246: ifnull +158 -> 11404
    //   11249: aload_0
    //   11250: new 277	android/text/StaticLayout
    //   11253: dup
    //   11254: aload_1
    //   11255: getfield 516	org/vidogram/messenger/MessageObject:caption	Ljava/lang/CharSequence;
    //   11258: getstatic 2276	org/vidogram/ui/ActionBar/Theme:chat_msgTextPaint	Landroid/text/TextPaint;
    //   11261: iload 12
    //   11263: ldc_w 472
    //   11266: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11269: isub
    //   11270: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   11273: fconst_1
    //   11274: fconst_0
    //   11275: iconst_0
    //   11276: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   11279: putfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11282: aload_0
    //   11283: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11286: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   11289: ifle +2121 -> 13410
    //   11292: aload_0
    //   11293: aload_0
    //   11294: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11297: invokevirtual 655	android/text/StaticLayout:getHeight	()I
    //   11300: putfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   11303: aload_0
    //   11304: getfield 530	org/vidogram/ui/Cells/ChatMessageCell:captionHeight	I
    //   11307: istore 10
    //   11309: ldc_w 1406
    //   11312: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11315: istore 13
    //   11317: iload 10
    //   11319: iload 13
    //   11321: iadd
    //   11322: iconst_0
    //   11323: iadd
    //   11324: istore 13
    //   11326: aload_0
    //   11327: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11330: aload_0
    //   11331: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11334: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   11337: iconst_1
    //   11338: isub
    //   11339: invokevirtual 545	android/text/StaticLayout:getLineWidth	(I)F
    //   11342: fstore 8
    //   11344: aload_0
    //   11345: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11348: aload_0
    //   11349: getfield 520	org/vidogram/ui/Cells/ChatMessageCell:captionLayout	Landroid/text/StaticLayout;
    //   11352: invokevirtual 1000	android/text/StaticLayout:getLineCount	()I
    //   11355: iconst_1
    //   11356: isub
    //   11357: invokevirtual 542	android/text/StaticLayout:getLineLeft	(I)F
    //   11360: fstore 9
    //   11362: iload 13
    //   11364: istore 10
    //   11366: iload 12
    //   11368: ldc_w 652
    //   11371: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11374: isub
    //   11375: i2f
    //   11376: fload 8
    //   11378: fload 9
    //   11380: fadd
    //   11381: fsub
    //   11382: iload 14
    //   11384: i2f
    //   11385: fcmpg
    //   11386: ifge +18 -> 11404
    //   11389: ldc_w 373
    //   11392: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11395: istore 10
    //   11397: iload 13
    //   11399: iload 10
    //   11401: iadd
    //   11402: istore 10
    //   11404: aload_0
    //   11405: getstatic 1869	java/util/Locale:US	Ljava/util/Locale;
    //   11408: ldc_w 2780
    //   11411: iconst_2
    //   11412: anewarray 1016	java/lang/Object
    //   11415: dup
    //   11416: iconst_0
    //   11417: iload 12
    //   11419: i2f
    //   11420: getstatic 1879	org/vidogram/messenger/AndroidUtilities:density	F
    //   11423: fdiv
    //   11424: f2i
    //   11425: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   11428: aastore
    //   11429: dup
    //   11430: iconst_1
    //   11431: iload 11
    //   11433: i2f
    //   11434: getstatic 1879	org/vidogram/messenger/AndroidUtilities:density	F
    //   11437: fdiv
    //   11438: f2i
    //   11439: invokestatic 776	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   11442: aastore
    //   11443: invokestatic 1882	java/lang/String:format	(Ljava/util/Locale;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   11446: putfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11449: aload_1
    //   11450: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   11453: ifnull +14 -> 11467
    //   11456: aload_1
    //   11457: getfield 1114	org/vidogram/messenger/MessageObject:photoThumbs	Ljava/util/ArrayList;
    //   11460: invokevirtual 475	java/util/ArrayList:size	()I
    //   11463: iconst_1
    //   11464: if_icmpgt +20 -> 11484
    //   11467: aload_1
    //   11468: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11471: iconst_3
    //   11472: if_icmpeq +12 -> 11484
    //   11475: aload_1
    //   11476: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11479: bipush 8
    //   11481: if_icmpne +37 -> 11518
    //   11484: aload_1
    //   11485: invokevirtual 847	org/vidogram/messenger/MessageObject:isSecretPhoto	()Z
    //   11488: ifeq +505 -> 11993
    //   11491: aload_0
    //   11492: new 1098	java/lang/StringBuilder
    //   11495: dup
    //   11496: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   11499: aload_0
    //   11500: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11503: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11506: ldc_w 2965
    //   11509: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   11512: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   11515: putfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   11518: aload_1
    //   11519: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11522: iconst_3
    //   11523: if_icmpeq +12 -> 11535
    //   11526: aload_1
    //   11527: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11530: bipush 8
    //   11532: if_icmpne +1872 -> 13404
    //   11535: iconst_1
    //   11536: istore 13
    //   11538: aload_0
    //   11539: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11542: ifnull +26 -> 11568
    //   11545: iload 13
    //   11547: ifne +21 -> 11568
    //   11550: aload_0
    //   11551: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11554: getfield 1168	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   11557: ifne +11 -> 11568
    //   11560: aload_0
    //   11561: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11564: iconst_m1
    //   11565: putfield 1168	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   11568: aload_1
    //   11569: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11572: iconst_1
    //   11573: if_icmpne +686 -> 12259
    //   11576: aload_1
    //   11577: getfield 2968	org/vidogram/messenger/MessageObject:useCustomPhoto	Z
    //   11580: ifeq +443 -> 12023
    //   11583: aload_0
    //   11584: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11587: aload_0
    //   11588: invokevirtual 2972	org/vidogram/ui/Cells/ChatMessageCell:getResources	()Landroid/content/res/Resources;
    //   11591: ldc_w 2973
    //   11594: invokevirtual 2979	android/content/res/Resources:getDrawable	(I)Landroid/graphics/drawable/Drawable;
    //   11597: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   11600: iload 10
    //   11602: istore 14
    //   11604: goto -2433 -> 9171
    //   11607: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11610: getfield 1836	android/graphics/Point:x	I
    //   11613: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11616: getfield 1839	android/graphics/Point:y	I
    //   11619: invokestatic 959	java/lang/Math:min	(II)I
    //   11622: i2f
    //   11623: ldc_w 2963
    //   11626: fmul
    //   11627: f2i
    //   11628: istore 10
    //   11630: iload 10
    //   11632: istore 11
    //   11634: goto -1049 -> 10585
    //   11637: aload_1
    //   11638: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11641: iconst_3
    //   11642: if_icmpne +37 -> 11679
    //   11645: aload_0
    //   11646: iconst_0
    //   11647: aload_1
    //   11648: invokespecial 2849	org/vidogram/ui/Cells/ChatMessageCell:createDocumentLayout	(ILorg/vidogram/messenger/MessageObject;)I
    //   11651: pop
    //   11652: aload_0
    //   11653: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11656: iconst_1
    //   11657: invokevirtual 1126	org/vidogram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   11660: aload_0
    //   11661: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11664: iconst_1
    //   11665: invokevirtual 1129	org/vidogram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   11668: aload_0
    //   11669: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11672: aload_1
    //   11673: invokevirtual 1132	org/vidogram/messenger/ImageReceiver:setParentMessageObject	(Lorg/vidogram/messenger/MessageObject;)V
    //   11676: goto -994 -> 10682
    //   11679: aload_1
    //   11680: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   11683: bipush 8
    //   11685: if_icmpne -1003 -> 10682
    //   11688: aload_1
    //   11689: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   11692: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   11695: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   11698: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   11701: i2l
    //   11702: invokestatic 1033	org/vidogram/messenger/AndroidUtilities:formatFileSize	(J)Ljava/lang/String;
    //   11705: astore 30
    //   11707: aload_0
    //   11708: getstatic 1036	org/vidogram/ui/ActionBar/Theme:chat_infoPaint	Landroid/text/TextPaint;
    //   11711: aload 30
    //   11713: invokevirtual 945	android/text/TextPaint:measureText	(Ljava/lang/String;)F
    //   11716: f2d
    //   11717: invokestatic 949	java/lang/Math:ceil	(D)D
    //   11720: d2i
    //   11721: putfield 1038	org/vidogram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11724: aload_0
    //   11725: new 277	android/text/StaticLayout
    //   11728: dup
    //   11729: aload 30
    //   11731: getstatic 1036	org/vidogram/ui/ActionBar/Theme:chat_infoPaint	Landroid/text/TextPaint;
    //   11734: aload_0
    //   11735: getfield 1038	org/vidogram/ui/Cells/ChatMessageCell:infoWidth	I
    //   11738: getstatic 992	android/text/Layout$Alignment:ALIGN_NORMAL	Landroid/text/Layout$Alignment;
    //   11741: fconst_1
    //   11742: fconst_0
    //   11743: iconst_0
    //   11744: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   11747: putfield 1040	org/vidogram/ui/Cells/ChatMessageCell:infoLayout	Landroid/text/StaticLayout;
    //   11750: aload_0
    //   11751: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11754: iconst_1
    //   11755: invokevirtual 1126	org/vidogram/messenger/ImageReceiver:setNeedsQualityThumb	(Z)V
    //   11758: aload_0
    //   11759: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11762: iconst_1
    //   11763: invokevirtual 1129	org/vidogram/messenger/ImageReceiver:setShouldGenerateQualityThumb	(Z)V
    //   11766: aload_0
    //   11767: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   11770: aload_1
    //   11771: invokevirtual 1132	org/vidogram/messenger/ImageReceiver:setParentMessageObject	(Lorg/vidogram/messenger/MessageObject;)V
    //   11774: goto -1092 -> 10682
    //   11777: iload 14
    //   11779: istore 10
    //   11781: iload 13
    //   11783: istore 11
    //   11785: iload 14
    //   11787: ldc_w 2980
    //   11790: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11793: if_icmpge -944 -> 10849
    //   11796: ldc_w 2980
    //   11799: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11802: istore 14
    //   11804: aload_0
    //   11805: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11808: getfield 2768	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   11811: i2f
    //   11812: iload 14
    //   11814: i2f
    //   11815: fdiv
    //   11816: fstore 8
    //   11818: iload 14
    //   11820: istore 10
    //   11822: iload 13
    //   11824: istore 11
    //   11826: aload_0
    //   11827: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11830: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   11833: i2f
    //   11834: fload 8
    //   11836: fdiv
    //   11837: iload 16
    //   11839: i2f
    //   11840: fcmpg
    //   11841: ifge -992 -> 10849
    //   11844: aload_0
    //   11845: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   11848: getfield 2765	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   11851: i2f
    //   11852: fload 8
    //   11854: fdiv
    //   11855: f2i
    //   11856: istore 11
    //   11858: iload 14
    //   11860: istore 10
    //   11862: goto -1013 -> 10849
    //   11865: iload 10
    //   11867: ldc_w 2980
    //   11870: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11873: if_icmpge +1561 -> 13434
    //   11876: ldc_w 2980
    //   11879: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   11882: istore 12
    //   11884: aload 30
    //   11886: getfield 2772	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   11889: i2f
    //   11890: iload 12
    //   11892: i2f
    //   11893: fdiv
    //   11894: fstore 8
    //   11896: aload 30
    //   11898: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11901: i2f
    //   11902: fload 8
    //   11904: fdiv
    //   11905: iload 16
    //   11907: i2f
    //   11908: fcmpg
    //   11909: ifge +1518 -> 13427
    //   11912: aload 30
    //   11914: getfield 2771	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   11917: i2f
    //   11918: fload 8
    //   11920: fdiv
    //   11921: f2i
    //   11922: istore 10
    //   11924: iload 10
    //   11926: istore 11
    //   11928: iload 12
    //   11930: istore 10
    //   11932: goto -936 -> 10996
    //   11935: iload 13
    //   11937: iconst_1
    //   11938: iadd
    //   11939: istore 13
    //   11941: goto -1070 -> 10871
    //   11944: iconst_0
    //   11945: istore 11
    //   11947: goto -845 -> 11102
    //   11950: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11953: getfield 1836	android/graphics/Point:x	I
    //   11956: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   11959: getfield 1839	android/graphics/Point:y	I
    //   11962: invokestatic 959	java/lang/Math:min	(II)I
    //   11965: i2f
    //   11966: ldc_w 2778
    //   11969: fmul
    //   11970: f2i
    //   11971: istore 10
    //   11973: iload 10
    //   11975: istore 11
    //   11977: goto -814 -> 11163
    //   11980: astore 30
    //   11982: iconst_0
    //   11983: istore 10
    //   11985: aload 30
    //   11987: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   11990: goto -586 -> 11404
    //   11993: aload_0
    //   11994: new 1098	java/lang/StringBuilder
    //   11997: dup
    //   11998: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   12001: aload_0
    //   12002: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12005: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12008: ldc_w 2982
    //   12011: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12014: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   12017: putfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12020: goto -502 -> 11518
    //   12023: aload_0
    //   12024: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12027: ifnull +214 -> 12241
    //   12030: iconst_1
    //   12031: istore 14
    //   12033: aload_0
    //   12034: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12037: invokestatic 2867	org/vidogram/messenger/FileLoader:getAttachFileName	(Lorg/vidogram/tgnet/TLObject;)Ljava/lang/String;
    //   12040: astore 30
    //   12042: aload_1
    //   12043: getfield 2237	org/vidogram/messenger/MessageObject:mediaExists	Z
    //   12046: ifeq +111 -> 12157
    //   12049: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   12052: aload_0
    //   12053: invokevirtual 2270	org/vidogram/messenger/MediaController:removeLoadingFileObserver	(Lorg/vidogram/messenger/MediaController$FileDownloadProgressListener;)V
    //   12056: iload 14
    //   12058: ifne +24 -> 12082
    //   12061: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   12064: iconst_1
    //   12065: invokevirtual 2870	org/vidogram/messenger/MediaController:canDownloadMedia	(I)Z
    //   12068: ifne +14 -> 12082
    //   12071: invokestatic 1174	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   12074: aload 30
    //   12076: invokevirtual 2873	org/vidogram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   12079: ifeq +102 -> 12181
    //   12082: aload_0
    //   12083: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12086: astore 31
    //   12088: aload_0
    //   12089: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12092: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12095: astore 32
    //   12097: aload_0
    //   12098: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12101: astore 33
    //   12103: aload_0
    //   12104: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12107: ifnull +56 -> 12163
    //   12110: aload_0
    //   12111: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12114: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12117: astore 30
    //   12119: aload_0
    //   12120: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12123: astore 34
    //   12125: iload 13
    //   12127: ifeq +42 -> 12169
    //   12130: iconst_0
    //   12131: istore 13
    //   12133: aload 31
    //   12135: aload 32
    //   12137: aload 33
    //   12139: aload 30
    //   12141: aload 34
    //   12143: iload 13
    //   12145: aconst_null
    //   12146: iconst_0
    //   12147: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12150: iload 10
    //   12152: istore 14
    //   12154: goto -2983 -> 9171
    //   12157: iconst_0
    //   12158: istore 14
    //   12160: goto -104 -> 12056
    //   12163: aconst_null
    //   12164: astore 30
    //   12166: goto -47 -> 12119
    //   12169: aload_0
    //   12170: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12173: getfield 1168	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   12176: istore 13
    //   12178: goto -45 -> 12133
    //   12181: aload_0
    //   12182: iconst_1
    //   12183: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   12186: aload_0
    //   12187: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12190: ifnull +33 -> 12223
    //   12193: aload_0
    //   12194: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12197: aconst_null
    //   12198: aconst_null
    //   12199: aload_0
    //   12200: getfield 1167	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObjectThumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12203: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12206: aload_0
    //   12207: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12210: iconst_0
    //   12211: aconst_null
    //   12212: iconst_0
    //   12213: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12216: iload 10
    //   12218: istore 14
    //   12220: goto -3049 -> 9171
    //   12223: aload_0
    //   12224: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12227: aconst_null
    //   12228: checkcast 1253	android/graphics/drawable/Drawable
    //   12231: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   12234: iload 10
    //   12236: istore 14
    //   12238: goto -3067 -> 9171
    //   12241: aload_0
    //   12242: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12245: aconst_null
    //   12246: checkcast 1142	android/graphics/drawable/BitmapDrawable
    //   12249: invokevirtual 1146	org/vidogram/messenger/ImageReceiver:setImageBitmap	(Landroid/graphics/drawable/Drawable;)V
    //   12252: iload 10
    //   12254: istore 14
    //   12256: goto -3085 -> 9171
    //   12259: aload_1
    //   12260: getfield 644	org/vidogram/messenger/MessageObject:type	I
    //   12263: bipush 8
    //   12265: if_icmpne +315 -> 12580
    //   12268: aload_1
    //   12269: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12272: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   12275: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   12278: invokestatic 2867	org/vidogram/messenger/FileLoader:getAttachFileName	(Lorg/vidogram/tgnet/TLObject;)Ljava/lang/String;
    //   12281: astore 30
    //   12283: iconst_0
    //   12284: istore 13
    //   12286: aload_1
    //   12287: getfield 2240	org/vidogram/messenger/MessageObject:attachPathExists	Z
    //   12290: ifeq +127 -> 12417
    //   12293: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   12296: aload_0
    //   12297: invokevirtual 2270	org/vidogram/messenger/MediaController:removeLoadingFileObserver	(Lorg/vidogram/messenger/MediaController$FileDownloadProgressListener;)V
    //   12300: iconst_1
    //   12301: istore 13
    //   12303: aload_1
    //   12304: invokevirtual 1186	org/vidogram/messenger/MessageObject:isSending	()Z
    //   12307: ifne +217 -> 12524
    //   12310: iload 13
    //   12312: ifne +41 -> 12353
    //   12315: invokestatic 315	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   12318: bipush 32
    //   12320: invokevirtual 2870	org/vidogram/messenger/MediaController:canDownloadMedia	(I)Z
    //   12323: ifeq +19 -> 12342
    //   12326: aload_1
    //   12327: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12330: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   12333: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   12336: invokestatic 2985	org/vidogram/messenger/MessageObject:isNewGifDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   12339: ifne +14 -> 12353
    //   12342: invokestatic 1174	org/vidogram/messenger/FileLoader:getInstance	()Lorg/vidogram/messenger/FileLoader;
    //   12345: aload 30
    //   12347: invokevirtual 2873	org/vidogram/messenger/FileLoader:isLoadingFile	(Ljava/lang/String;)Z
    //   12350: ifeq +174 -> 12524
    //   12353: iload 13
    //   12355: iconst_1
    //   12356: if_icmpne +92 -> 12448
    //   12359: aload_0
    //   12360: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12363: astore 32
    //   12365: aload_1
    //   12366: invokevirtual 866	org/vidogram/messenger/MessageObject:isSendError	()Z
    //   12369: ifeq +61 -> 12430
    //   12372: aconst_null
    //   12373: astore 30
    //   12375: aload_0
    //   12376: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12379: ifnull +63 -> 12442
    //   12382: aload_0
    //   12383: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12386: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12389: astore 31
    //   12391: aload 32
    //   12393: aconst_null
    //   12394: aload 30
    //   12396: aconst_null
    //   12397: aconst_null
    //   12398: aload 31
    //   12400: aload_0
    //   12401: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12404: iconst_0
    //   12405: aconst_null
    //   12406: iconst_0
    //   12407: invokevirtual 1140	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Ljava/lang/String;Landroid/graphics/drawable/Drawable;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12410: iload 10
    //   12412: istore 14
    //   12414: goto -3243 -> 9171
    //   12417: aload_1
    //   12418: getfield 2237	org/vidogram/messenger/MessageObject:mediaExists	Z
    //   12421: ifeq -118 -> 12303
    //   12424: iconst_2
    //   12425: istore 13
    //   12427: goto -124 -> 12303
    //   12430: aload_1
    //   12431: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12434: getfield 2960	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   12437: astore 30
    //   12439: goto -64 -> 12375
    //   12442: aconst_null
    //   12443: astore 31
    //   12445: goto -54 -> 12391
    //   12448: aload_0
    //   12449: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12452: astore 31
    //   12454: aload_1
    //   12455: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12458: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   12461: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   12464: astore 32
    //   12466: aload_0
    //   12467: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12470: ifnull +48 -> 12518
    //   12473: aload_0
    //   12474: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12477: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12480: astore 30
    //   12482: aload 31
    //   12484: aload 32
    //   12486: aconst_null
    //   12487: aload 30
    //   12489: aload_0
    //   12490: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12493: aload_1
    //   12494: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12497: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   12500: getfield 914	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   12503: getfield 1029	org/vidogram/tgnet/TLRPC$Document:size	I
    //   12506: aconst_null
    //   12507: iconst_0
    //   12508: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12511: iload 10
    //   12513: istore 14
    //   12515: goto -3344 -> 9171
    //   12518: aconst_null
    //   12519: astore 30
    //   12521: goto -39 -> 12482
    //   12524: aload_0
    //   12525: iconst_1
    //   12526: putfield 1888	org/vidogram/ui/Cells/ChatMessageCell:photoNotSet	Z
    //   12529: aload_0
    //   12530: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12533: astore 31
    //   12535: aload_0
    //   12536: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12539: ifnull +35 -> 12574
    //   12542: aload_0
    //   12543: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12546: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12549: astore 30
    //   12551: aload 31
    //   12553: aconst_null
    //   12554: aconst_null
    //   12555: aload 30
    //   12557: aload_0
    //   12558: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12561: iconst_0
    //   12562: aconst_null
    //   12563: iconst_0
    //   12564: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12567: iload 10
    //   12569: istore 14
    //   12571: goto -3400 -> 9171
    //   12574: aconst_null
    //   12575: astore 30
    //   12577: goto -26 -> 12551
    //   12580: aload_0
    //   12581: getfield 325	org/vidogram/ui/Cells/ChatMessageCell:photoImage	Lorg/vidogram/messenger/ImageReceiver;
    //   12584: astore 31
    //   12586: aload_0
    //   12587: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12590: ifnull +35 -> 12625
    //   12593: aload_0
    //   12594: getfield 1123	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoObject	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   12597: getfield 1062	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   12600: astore 30
    //   12602: aload 31
    //   12604: aconst_null
    //   12605: aconst_null
    //   12606: aload 30
    //   12608: aload_0
    //   12609: getfield 1136	org/vidogram/ui/Cells/ChatMessageCell:currentPhotoFilter	Ljava/lang/String;
    //   12612: iconst_0
    //   12613: aconst_null
    //   12614: iconst_0
    //   12615: invokevirtual 1171	org/vidogram/messenger/ImageReceiver:setImage	(Lorg/vidogram/tgnet/TLObject;Ljava/lang/String;Lorg/vidogram/tgnet/TLRPC$FileLocation;Ljava/lang/String;ILjava/lang/String;Z)V
    //   12618: iload 10
    //   12620: istore 14
    //   12622: goto -3451 -> 9171
    //   12625: aconst_null
    //   12626: astore 30
    //   12628: goto -26 -> 12602
    //   12631: aload_0
    //   12632: getfield 2077	org/vidogram/ui/Cells/ChatMessageCell:drawNameLayout	Z
    //   12635: ifeq -3437 -> 9198
    //   12638: aload_1
    //   12639: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   12642: getfield 826	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   12645: ifne -3447 -> 9198
    //   12648: aload_0
    //   12649: aload_0
    //   12650: getfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   12653: ldc_w 499
    //   12656: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12659: iadd
    //   12660: putfield 440	org/vidogram/ui/Cells/ChatMessageCell:namesOffset	I
    //   12663: goto -3465 -> 9198
    //   12666: iconst_0
    //   12667: istore 10
    //   12669: goto -8092 -> 4577
    //   12672: astore 30
    //   12674: aload 30
    //   12676: invokestatic 590	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   12679: goto -7979 -> 4700
    //   12682: ldc_w 472
    //   12685: fstore 8
    //   12687: goto -7855 -> 4832
    //   12690: iload 10
    //   12692: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   12695: getfield 1836	android/graphics/Point:x	I
    //   12698: getstatic 1831	org/vidogram/messenger/AndroidUtilities:displaySize	Landroid/graphics/Point;
    //   12701: getfield 1839	android/graphics/Point:y	I
    //   12704: invokestatic 959	java/lang/Math:min	(II)I
    //   12707: iadd
    //   12708: istore 10
    //   12710: goto -7856 -> 4854
    //   12713: aload_1
    //   12714: getfield 2842	org/vidogram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   12717: ifnull +14 -> 12731
    //   12720: aload_0
    //   12721: aload_1
    //   12722: getfield 2842	org/vidogram/messenger/MessageObject:botButtonsLayout	Ljava/lang/StringBuilder;
    //   12725: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   12728: putfield 2821	org/vidogram/ui/Cells/ChatMessageCell:botButtonsLayout	Ljava/lang/String;
    //   12731: aconst_null
    //   12732: astore 30
    //   12734: goto -7797 -> 4937
    //   12737: aload_0
    //   12738: getfield 471	org/vidogram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   12741: istore 15
    //   12743: ldc_w 1486
    //   12746: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12749: istore 16
    //   12751: iload 11
    //   12753: ifne +387 -> 13140
    //   12756: aload_0
    //   12757: getfield 498	org/vidogram/ui/Cells/ChatMessageCell:mediaBackground	Z
    //   12760: ifeq +380 -> 13140
    //   12763: fconst_0
    //   12764: fstore 8
    //   12766: iload 15
    //   12768: iload 16
    //   12770: iload 13
    //   12772: iconst_1
    //   12773: isub
    //   12774: imul
    //   12775: isub
    //   12776: fload 8
    //   12778: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12781: isub
    //   12782: fconst_2
    //   12783: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12786: isub
    //   12787: iload 13
    //   12789: idiv
    //   12790: istore 15
    //   12792: iconst_0
    //   12793: istore 13
    //   12795: iload 13
    //   12797: aload 33
    //   12799: getfield 2847	org/vidogram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   12802: invokevirtual 475	java/util/ArrayList:size	()I
    //   12805: if_icmpge +590 -> 13395
    //   12808: new 14	org/vidogram/ui/Cells/ChatMessageCell$BotButton
    //   12811: dup
    //   12812: aload_0
    //   12813: aconst_null
    //   12814: invokespecial 2988	org/vidogram/ui/Cells/ChatMessageCell$BotButton:<init>	(Lorg/vidogram/ui/Cells/ChatMessageCell;Lorg/vidogram/ui/Cells/ChatMessageCell$1;)V
    //   12817: astore 34
    //   12819: aload 34
    //   12821: aload 33
    //   12823: getfield 2847	org/vidogram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   12826: iload 13
    //   12828: invokevirtual 479	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   12831: checkcast 2990	org/vidogram/tgnet/TLRPC$KeyboardButton
    //   12834: invokestatic 2994	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$002	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;Lorg/vidogram/tgnet/TLRPC$KeyboardButton;)Lorg/vidogram/tgnet/TLRPC$KeyboardButton;
    //   12837: pop
    //   12838: aload 34
    //   12840: invokestatic 505	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$000	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)Lorg/vidogram/tgnet/TLRPC$KeyboardButton;
    //   12843: getfield 2997	org/vidogram/tgnet/TLRPC$KeyboardButton:data	[B
    //   12846: invokestatic 3003	org/vidogram/messenger/Utilities:bytesToHex	([B)Ljava/lang/String;
    //   12849: astore 35
    //   12851: new 1098	java/lang/StringBuilder
    //   12854: dup
    //   12855: invokespecial 1099	java/lang/StringBuilder:<init>	()V
    //   12858: iload 12
    //   12860: invokevirtual 3006	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   12863: ldc_w 2203
    //   12866: invokevirtual 1103	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   12869: iload 13
    //   12871: invokevirtual 3006	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   12874: invokevirtual 1111	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   12877: astore 36
    //   12879: aload 30
    //   12881: ifnull +267 -> 13148
    //   12884: aload 30
    //   12886: aload 36
    //   12888: invokevirtual 3009	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   12891: checkcast 14	org/vidogram/ui/Cells/ChatMessageCell$BotButton
    //   12894: astore 31
    //   12896: aload 31
    //   12898: ifnull +265 -> 13163
    //   12901: aload 34
    //   12903: aload 31
    //   12905: invokestatic 1744	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$600	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)F
    //   12908: invokestatic 1772	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$602	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;F)F
    //   12911: pop
    //   12912: aload 34
    //   12914: aload 31
    //   12916: invokestatic 1751	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$700	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)I
    //   12919: invokestatic 1767	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$702	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12922: pop
    //   12923: aload 34
    //   12925: aload 31
    //   12927: invokestatic 1756	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$800	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)J
    //   12930: invokestatic 1776	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   12933: pop2
    //   12934: aload_0
    //   12935: getfield 267	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByData	Ljava/util/HashMap;
    //   12938: aload 35
    //   12940: aload 34
    //   12942: invokevirtual 3013	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   12945: pop
    //   12946: aload_0
    //   12947: getfield 269	org/vidogram/ui/Cells/ChatMessageCell:botButtonsByPosition	Ljava/util/HashMap;
    //   12950: aload 36
    //   12952: aload 34
    //   12954: invokevirtual 3013	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   12957: pop
    //   12958: aload 34
    //   12960: ldc_w 1486
    //   12963: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12966: iload 15
    //   12968: iadd
    //   12969: iload 13
    //   12971: imul
    //   12972: invokestatic 3016	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$202	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12975: pop
    //   12976: aload 34
    //   12978: ldc_w 658
    //   12981: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12984: iload 12
    //   12986: imul
    //   12987: ldc_w 1486
    //   12990: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   12993: iadd
    //   12994: invokestatic 3019	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$102	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   12997: pop
    //   12998: aload 34
    //   13000: iload 15
    //   13002: invokestatic 3022	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$302	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   13005: pop
    //   13006: aload 34
    //   13008: ldc_w 1552
    //   13011: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13014: invokestatic 3025	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$402	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;I)I
    //   13017: pop
    //   13018: aload 34
    //   13020: invokestatic 505	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$000	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)Lorg/vidogram/tgnet/TLRPC$KeyboardButton;
    //   13023: instanceof 1728
    //   13026: ifeq +149 -> 13175
    //   13029: aload_1
    //   13030: getfield 666	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   13033: getfield 672	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   13036: getfield 2795	org/vidogram/tgnet/TLRPC$MessageMedia:flags	I
    //   13039: iconst_4
    //   13040: iand
    //   13041: ifeq +134 -> 13175
    //   13044: ldc_w 2797
    //   13047: ldc_w 2798
    //   13050: invokestatic 1079	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   13053: astore 31
    //   13055: aload 34
    //   13057: new 277	android/text/StaticLayout
    //   13060: dup
    //   13061: aload 31
    //   13063: getstatic 3028	org/vidogram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   13066: iload 15
    //   13068: ldc_w 472
    //   13071: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13074: isub
    //   13075: getstatic 3031	android/text/Layout$Alignment:ALIGN_CENTER	Landroid/text/Layout$Alignment;
    //   13078: fconst_1
    //   13079: fconst_0
    //   13080: iconst_0
    //   13081: invokespecial 995	android/text/StaticLayout:<init>	(Ljava/lang/CharSequence;Landroid/text/TextPaint;ILandroid/text/Layout$Alignment;FFZ)V
    //   13084: invokestatic 3035	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$902	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;Landroid/text/StaticLayout;)Landroid/text/StaticLayout;
    //   13087: pop
    //   13088: aload_0
    //   13089: getfield 262	org/vidogram/ui/Cells/ChatMessageCell:botButtons	Ljava/util/ArrayList;
    //   13092: aload 34
    //   13094: invokevirtual 2037	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   13097: pop
    //   13098: iload 13
    //   13100: aload 33
    //   13102: getfield 2847	org/vidogram/tgnet/TLRPC$TL_keyboardButtonRow:buttons	Ljava/util/ArrayList;
    //   13105: invokevirtual 475	java/util/ArrayList:size	()I
    //   13108: iconst_1
    //   13109: isub
    //   13110: if_icmpne +282 -> 13392
    //   13113: iload 10
    //   13115: aload 34
    //   13117: invokestatic 486	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$200	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)I
    //   13120: aload 34
    //   13122: invokestatic 489	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$300	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)I
    //   13125: iadd
    //   13126: invokestatic 379	java/lang/Math:max	(II)I
    //   13129: istore 10
    //   13131: iload 13
    //   13133: iconst_1
    //   13134: iadd
    //   13135: istore 13
    //   13137: goto -342 -> 12795
    //   13140: ldc_w 1406
    //   13143: fstore 8
    //   13145: goto -379 -> 12766
    //   13148: aload 32
    //   13150: aload 35
    //   13152: invokevirtual 3009	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   13155: checkcast 14	org/vidogram/ui/Cells/ChatMessageCell$BotButton
    //   13158: astore 31
    //   13160: goto -264 -> 12896
    //   13163: aload 34
    //   13165: invokestatic 1438	java/lang/System:currentTimeMillis	()J
    //   13168: invokestatic 1776	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$802	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;J)J
    //   13171: pop2
    //   13172: goto -238 -> 12934
    //   13175: aload 34
    //   13177: invokestatic 505	org/vidogram/ui/Cells/ChatMessageCell$BotButton:access$000	(Lorg/vidogram/ui/Cells/ChatMessageCell$BotButton;)Lorg/vidogram/tgnet/TLRPC$KeyboardButton;
    //   13180: getfield 3038	org/vidogram/tgnet/TLRPC$KeyboardButton:text	Ljava/lang/String;
    //   13183: getstatic 3028	org/vidogram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   13186: invokevirtual 2168	android/text/TextPaint:getFontMetricsInt	()Landroid/graphics/Paint$FontMetricsInt;
    //   13189: ldc_w 2560
    //   13192: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13195: iconst_0
    //   13196: invokestatic 2174	org/vidogram/messenger/Emoji:replaceEmoji	(Ljava/lang/CharSequence;Landroid/graphics/Paint$FontMetricsInt;IZ)Ljava/lang/CharSequence;
    //   13199: getstatic 3028	org/vidogram/ui/ActionBar/Theme:chat_botButtonPaint	Landroid/text/TextPaint;
    //   13202: iload 15
    //   13204: ldc_w 472
    //   13207: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13210: isub
    //   13211: i2f
    //   13212: getstatic 982	android/text/TextUtils$TruncateAt:END	Landroid/text/TextUtils$TruncateAt;
    //   13215: invokestatic 986	android/text/TextUtils:ellipsize	(Ljava/lang/CharSequence;Landroid/text/TextPaint;FLandroid/text/TextUtils$TruncateAt;)Ljava/lang/CharSequence;
    //   13218: astore 31
    //   13220: goto -165 -> 13055
    //   13223: aload_0
    //   13224: iload 10
    //   13226: putfield 471	org/vidogram/ui/Cells/ChatMessageCell:widthForButtons	I
    //   13229: aload_0
    //   13230: getfield 1347	org/vidogram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   13233: ifeq +47 -> 13280
    //   13236: aload_0
    //   13237: getfield 1659	org/vidogram/ui/Cells/ChatMessageCell:pinnedTop	Z
    //   13240: ifeq +40 -> 13280
    //   13243: aload_0
    //   13244: aload_0
    //   13245: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   13248: fconst_2
    //   13249: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13252: isub
    //   13253: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   13256: aload_0
    //   13257: invokespecial 2613	org/vidogram/ui/Cells/ChatMessageCell:updateWaveform	()V
    //   13260: aload_0
    //   13261: iload 29
    //   13263: invokevirtual 2243	org/vidogram/ui/Cells/ChatMessageCell:updateButtonState	(Z)V
    //   13266: return
    //   13267: aload_0
    //   13268: iconst_0
    //   13269: putfield 2539	org/vidogram/ui/Cells/ChatMessageCell:substractBackgroundHeight	I
    //   13272: aload_0
    //   13273: iconst_0
    //   13274: putfield 2586	org/vidogram/ui/Cells/ChatMessageCell:keyboardHeight	I
    //   13277: goto -48 -> 13229
    //   13280: aload_0
    //   13281: getfield 1347	org/vidogram/ui/Cells/ChatMessageCell:pinnedBottom	Z
    //   13284: ifeq -28 -> 13256
    //   13287: aload_0
    //   13288: aload_0
    //   13289: getfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   13292: fconst_1
    //   13293: invokestatic 295	org/vidogram/messenger/AndroidUtilities:dp	(F)I
    //   13296: isub
    //   13297: putfield 372	org/vidogram/ui/Cells/ChatMessageCell:totalHeight	I
    //   13300: goto -44 -> 13256
    //   13303: astore 30
    //   13305: iload 13
    //   13307: istore 10
    //   13309: goto -1324 -> 11985
    //   13312: astore 31
    //   13314: goto -10132 -> 3182
    //   13317: astore 31
    //   13319: iconst_0
    //   13320: istore 15
    //   13322: goto -10211 -> 3111
    //   13325: astore 31
    //   13327: goto -10216 -> 3111
    //   13330: astore 31
    //   13332: goto -10221 -> 3111
    //   13335: astore 38
    //   13337: iconst_0
    //   13338: istore 18
    //   13340: iconst_3
    //   13341: istore 13
    //   13343: iload 10
    //   13345: istore 15
    //   13347: iload 18
    //   13349: istore 10
    //   13351: goto -10809 -> 2542
    //   13354: astore 38
    //   13356: iconst_0
    //   13357: istore 18
    //   13359: iload 10
    //   13361: istore 15
    //   13363: iload 18
    //   13365: istore 10
    //   13367: goto -10825 -> 2542
    //   13370: astore 38
    //   13372: iload 11
    //   13374: istore 10
    //   13376: iload 13
    //   13378: istore 11
    //   13380: goto -10936 -> 2444
    //   13383: astore 38
    //   13385: iload 13
    //   13387: istore 11
    //   13389: goto -10945 -> 2444
    //   13392: goto -261 -> 13131
    //   13395: goto -8406 -> 4989
    //   13398: iconst_0
    //   13399: istore 11
    //   13401: goto -8524 -> 4877
    //   13404: iconst_0
    //   13405: istore 13
    //   13407: goto -1869 -> 11538
    //   13410: iconst_0
    //   13411: istore 10
    //   13413: goto -2009 -> 11404
    //   13416: iload 11
    //   13418: istore 12
    //   13420: iload 10
    //   13422: istore 11
    //   13424: goto -2221 -> 11203
    //   13427: iload 11
    //   13429: istore 10
    //   13431: goto -1507 -> 11924
    //   13434: goto -2438 -> 10996
    //   13437: goto -2441 -> 10996
    //   13440: goto -2784 -> 10656
    //   13443: iload 11
    //   13445: istore 16
    //   13447: goto -2804 -> 10643
    //   13450: iload 10
    //   13452: istore 15
    //   13454: goto -2824 -> 10630
    //   13457: goto -3248 -> 10209
    //   13460: iconst_0
    //   13461: istore 11
    //   13463: iconst_0
    //   13464: istore 10
    //   13466: goto -3355 -> 10111
    //   13469: goto -5454 -> 8015
    //   13472: aconst_null
    //   13473: astore 30
    //   13475: goto -5572 -> 7903
    //   13478: goto -5610 -> 7868
    //   13481: goto -6044 -> 7437
    //   13484: goto -6155 -> 7329
    //   13487: iload 10
    //   13489: istore 13
    //   13491: iload 11
    //   13493: istore 10
    //   13495: iload 13
    //   13497: istore 11
    //   13499: goto -9807 -> 3692
    //   13502: goto -10315 -> 3187
    //   13505: iload 11
    //   13507: istore 15
    //   13509: iload 13
    //   13511: istore 11
    //   13513: iconst_0
    //   13514: istore 19
    //   13516: iload 15
    //   13518: istore 13
    //   13520: iload 19
    //   13522: istore 15
    //   13524: goto -10746 -> 2778
    //   13527: iconst_3
    //   13528: istore 15
    //   13530: iload 11
    //   13532: istore 13
    //   13534: iconst_0
    //   13535: istore 18
    //   13537: iload 15
    //   13539: istore 11
    //   13541: goto -10974 -> 2567
    //   13544: iconst_0
    //   13545: istore 17
    //   13547: iload 11
    //   13549: istore 10
    //   13551: iload 15
    //   13553: istore 11
    //   13555: goto -12081 -> 1474
    //   13558: goto -12518 -> 1040
    //   13561: goto -12968 -> 593
    //   13564: iload 28
    //   13566: ifeq +23 -> 13589
    //   13569: iload 26
    //   13571: iload 28
    //   13573: isub
    //   13574: istore 11
    //   13576: goto -8514 -> 5062
    //   13579: iload 25
    //   13581: iload 23
    //   13583: iadd
    //   13584: istore 11
    //   13586: goto -8416 -> 5170
    //   13589: iload 19
    //   13591: ifeq -8392 -> 5199
    //   13594: iload 26
    //   13596: istore 11
    //   13598: goto -8536 -> 5062
    //
    // Exception table:
    //   from	to	target	type
    //   1348	1432	2431	java/lang/Exception
    //   1637	1649	2528	java/lang/Exception
    //   1657	1669	2528	java/lang/Exception
    //   1685	1695	2528	java/lang/Exception
    //   1703	1710	2528	java/lang/Exception
    //   1723	1735	2528	java/lang/Exception
    //   1763	1770	2528	java/lang/Exception
    //   1778	1789	2528	java/lang/Exception
    //   1797	1809	2528	java/lang/Exception
    //   1817	1829	2528	java/lang/Exception
    //   2511	2525	2528	java/lang/Exception
    //   2990	3005	2528	java/lang/Exception
    //   2577	2610	3106	java/lang/Exception
    //   2616	2623	3106	java/lang/Exception
    //   2628	2652	3106	java/lang/Exception
    //   3036	3071	3106	java/lang/Exception
    //   2783	2828	3180	java/lang/Exception
    //   2834	2874	3180	java/lang/Exception
    //   2877	2917	3180	java/lang/Exception
    //   2923	2951	3180	java/lang/Exception
    //   2959	2973	3180	java/lang/Exception
    //   3131	3160	3180	java/lang/Exception
    //   3163	3177	3180	java/lang/Exception
    //   4998	5007	3180	java/lang/Exception
    //   11249	11317	11980	java/lang/Exception
    //   4500	4577	12672	java/lang/Exception
    //   4577	4700	12672	java/lang/Exception
    //   11326	11362	13303	java/lang/Exception
    //   11366	11397	13303	java/lang/Exception
    //   5014	5042	13312	java/lang/Exception
    //   5047	5059	13312	java/lang/Exception
    //   5091	5098	13312	java/lang/Exception
    //   5098	5109	13312	java/lang/Exception
    //   5128	5145	13312	java/lang/Exception
    //   5150	5167	13312	java/lang/Exception
    //   5170	5182	13312	java/lang/Exception
    //   5199	5220	13312	java/lang/Exception
    //   2652	2710	13317	java/lang/Exception
    //   2715	2724	13317	java/lang/Exception
    //   3081	3095	13317	java/lang/Exception
    //   2734	2746	13325	java/lang/Exception
    //   2750	2762	13330	java/lang/Exception
    //   1479	1519	13335	java/lang/Exception
    //   1519	1526	13335	java/lang/Exception
    //   1531	1561	13335	java/lang/Exception
    //   2460	2494	13335	java/lang/Exception
    //   1567	1607	13354	java/lang/Exception
    //   1438	1459	13370	java/lang/Exception
    //   1459	1470	13383	java/lang/Exception
  }

  public void setPressed(boolean paramBoolean)
  {
    super.setPressed(paramBoolean);
    this.radialProgress.swapBackground(getDrawableForCurrentState());
    if (this.useSeekBarWaweform)
      this.seekBarWaveform.setSelected(isDrawSelectedBackground());
    while (true)
    {
      invalidate();
      return;
      this.seekBar.setSelected(isDrawSelectedBackground());
    }
  }

  public void setVisiblePart(int paramInt1, int paramInt2)
  {
    int k = 0;
    if ((this.currentMessageObject == null) || (this.currentMessageObject.textLayoutBlocks == null))
      return;
    int n = paramInt1 - this.textY;
    int i = 0;
    paramInt1 = 0;
    label34: int j;
    label80: float f;
    if ((i >= this.currentMessageObject.textLayoutBlocks.size()) || (((MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(i)).textYOffset > n))
    {
      j = -1;
      i = -1;
      if (paramInt1 >= this.currentMessageObject.textLayoutBlocks.size())
        break label201;
      MessageObject.TextLayoutBlock localTextLayoutBlock = (MessageObject.TextLayoutBlock)this.currentMessageObject.textLayoutBlocks.get(paramInt1);
      f = localTextLayoutBlock.textYOffset;
      if (!intersect(f, localTextLayoutBlock.height + f, n, n + paramInt2))
        break label193;
      j = i;
      if (i == -1)
        j = paramInt1;
      k += 1;
      i = paramInt1;
    }
    while (true)
    {
      paramInt1 += 1;
      int m = j;
      j = i;
      i = m;
      break label80;
      paramInt1 = i;
      i += 1;
      break label34;
      label193: if (f > n)
      {
        label201: if ((this.lastVisibleBlockNum == j) && (this.firstVisibleBlockNum == i) && (this.totalVisibleBlocksCount == k))
          break;
        this.lastVisibleBlockNum = j;
        this.firstVisibleBlockNum = i;
        this.totalVisibleBlocksCount = k;
        invalidate();
        return;
      }
      m = i;
      i = j;
      j = m;
    }
  }

  public void updateAudioProgress()
  {
    if ((this.currentMessageObject == null) || (this.documentAttach == null))
      return;
    int i;
    label69: Object localObject;
    if (this.useSeekBarWaweform)
    {
      if (!this.seekBarWaveform.isDragging())
        this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress);
      if (this.documentAttachType != 3)
        break label262;
      if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject))
        break label251;
      i = 0;
      if (i >= this.documentAttach.attributes.size())
        break label462;
      localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
      if (!(localObject instanceof TLRPC.TL_documentAttributeAudio))
        break label244;
      i = ((TLRPC.DocumentAttribute)localObject).duration;
    }
    while (true)
    {
      localObject = String.format("%02d:%02d", new Object[] { Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
      if ((this.lastTimeString == null) || ((this.lastTimeString != null) && (!this.lastTimeString.equals(localObject))))
      {
        this.lastTimeString = ((String)localObject);
        this.timeWidthAudio = (int)Math.ceil(Theme.chat_audioTimePaint.measureText((String)localObject));
        this.durationLayout = new StaticLayout((CharSequence)localObject, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
      }
      invalidate();
      return;
      if (this.seekBar.isDragging())
        break;
      this.seekBar.setProgress(this.currentMessageObject.audioProgress);
      break;
      label244: i += 1;
      break label69;
      label251: i = this.currentMessageObject.audioProgressSec;
      continue;
      label262: i = 0;
      label264: if (i < this.documentAttach.attributes.size())
      {
        localObject = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
        if (!(localObject instanceof TLRPC.TL_documentAttributeAudio));
      }
      for (i = ((TLRPC.DocumentAttribute)localObject).duration; ; i = 0)
      {
        if (MediaController.getInstance().isPlayingAudio(this.currentMessageObject));
        for (int j = this.currentMessageObject.audioProgressSec; ; j = 0)
        {
          localObject = String.format("%d:%02d / %d:%02d", new Object[] { Integer.valueOf(j / 60), Integer.valueOf(j % 60), Integer.valueOf(i / 60), Integer.valueOf(i % 60) });
          if ((this.lastTimeString != null) && ((this.lastTimeString == null) || (this.lastTimeString.equals(localObject))))
            break;
          this.lastTimeString = ((String)localObject);
          i = (int)Math.ceil(Theme.chat_audioTimePaint.measureText((String)localObject));
          this.durationLayout = new StaticLayout((CharSequence)localObject, Theme.chat_audioTimePaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          break;
          i += 1;
          break label264;
        }
      }
      label462: i = 0;
    }
  }

  public void updateButtonState(boolean paramBoolean)
  {
    float f2 = 0.0F;
    float f3 = 0.0F;
    float f1 = 0.0F;
    boolean bool2 = true;
    boolean bool3 = true;
    boolean bool4 = true;
    Object localObject1;
    boolean bool1;
    if (this.currentMessageObject.type == 1)
    {
      if (this.currentPhotoObject == null)
        return;
      localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
      bool1 = this.currentMessageObject.mediaExists;
    }
    while (true)
    {
      int i;
      Object localObject2;
      if (TextUtils.isEmpty((CharSequence)localObject1))
      {
        this.radialProgress.setBackground(null, false, false);
        return;
        if ((this.currentMessageObject.type == 8) || (this.documentAttachType == 4) || (this.currentMessageObject.type == 9) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
        {
          if (this.currentMessageObject.useCustomPhoto)
          {
            this.buttonState = 1;
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            return;
          }
          if (this.currentMessageObject.attachPathExists)
          {
            localObject1 = this.currentMessageObject.messageOwner.attachPath;
            bool1 = true;
            continue;
          }
          if ((!this.currentMessageObject.isSendError()) || (this.documentAttachType == 3) || (this.documentAttachType == 5))
          {
            localObject1 = this.currentMessageObject.getFileName();
            bool1 = this.currentMessageObject.mediaExists;
            continue;
          }
        }
        else
        {
          if (this.documentAttachType != 0)
          {
            localObject1 = FileLoader.getAttachFileName(this.documentAttach);
            bool1 = this.currentMessageObject.mediaExists;
            continue;
          }
          if (this.currentPhotoObject != null)
          {
            localObject1 = FileLoader.getAttachFileName(this.currentPhotoObject);
            bool1 = this.currentMessageObject.mediaExists;
            continue;
          }
        }
      }
      else
      {
        if ((this.currentMessageObject.messageOwner.params != null) && (this.currentMessageObject.messageOwner.params.containsKey("query_id")))
        {
          i = 1;
          if ((this.documentAttachType != 3) && (this.documentAttachType != 5))
            break label726;
          if (((!this.currentMessageObject.isOut()) || (!this.currentMessageObject.isSending())) && ((!this.currentMessageObject.isSendError()) || (i == 0)))
            break label539;
          MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
          this.buttonState = 4;
          localObject1 = this.radialProgress;
          localObject2 = getDrawableForCurrentState();
          if (i != 0)
            break label516;
          bool1 = bool4;
          label413: ((RadialProgress)localObject1).setBackground((Drawable)localObject2, bool1, paramBoolean);
          if (i != 0)
            break label527;
          localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
          localObject1 = localObject2;
          if (localObject2 == null)
          {
            localObject1 = localObject2;
            if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId()))
              localObject1 = Float.valueOf(1.0F);
          }
          localObject2 = this.radialProgress;
          if (localObject1 == null)
            break label522;
          f1 = ((Float)localObject1).floatValue();
          label498: ((RadialProgress)localObject2).setProgress(f1, false);
        }
        while (true)
        {
          updateAudioProgress();
          return;
          i = 0;
          break;
          label516: bool1 = false;
          break label413;
          label522: f1 = 0.0F;
          break label498;
          label527: this.radialProgress.setProgress(0.0F, false);
          continue;
          label539: if (bool1)
          {
            MediaController.getInstance().removeLoadingFileObserver(this);
            bool1 = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
            if ((!bool1) || ((bool1) && (MediaController.getInstance().isAudioPaused())));
            for (this.buttonState = 0; ; this.buttonState = 1)
            {
              this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
              break;
            }
          }
          MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
          if (FileLoader.getInstance().isLoadingFile((String)localObject1))
            break label665;
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
        }
        label665: this.buttonState = 4;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
        if (localObject1 != null)
          this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
        while (true)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
          break;
          this.radialProgress.setProgress(0.0F, paramBoolean);
        }
        label726: if ((this.currentMessageObject.type == 0) && (this.documentAttachType != 1) && (this.documentAttachType != 4))
        {
          if ((this.currentPhotoObject == null) || (!this.drawImageButton))
            break;
          if (!bool1)
          {
            MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
            if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
              if ((!this.cancelLoading) && (((this.documentAttachType == 0) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.documentAttachType == 2) && (MediaController.getInstance().canDownloadMedia(32)))))
              {
                this.buttonState = 1;
                bool1 = bool2;
              }
            while (true)
            {
              this.radialProgress.setProgress(f1, false);
              this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
              invalidate();
              return;
              this.buttonState = 0;
              bool1 = false;
              continue;
              this.buttonState = 1;
              localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
              bool1 = bool2;
              if (localObject1 == null)
                continue;
              f1 = ((Float)localObject1).floatValue();
              bool1 = bool2;
            }
          }
          MediaController.getInstance().removeLoadingFileObserver(this);
          if ((this.documentAttachType == 2) && (!this.photoImage.isAllowStartAnimation()));
          for (this.buttonState = 2; ; this.buttonState = -1)
          {
            this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
            invalidate();
            return;
          }
        }
        if ((this.currentMessageObject.isOut()) && (this.currentMessageObject.isSending()))
        {
          if ((this.currentMessageObject.messageOwner.attachPath == null) || (this.currentMessageObject.messageOwner.attachPath.length() <= 0))
            break;
          MediaController.getInstance().addLoadingFileObserver(this.currentMessageObject.messageOwner.attachPath, this.currentMessageObject, this);
          if ((this.currentMessageObject.messageOwner.attachPath == null) || (!this.currentMessageObject.messageOwner.attachPath.startsWith("http")))
          {
            bool1 = true;
            localObject1 = this.currentMessageObject.messageOwner.params;
            if ((this.currentMessageObject.messageOwner.message == null) || (localObject1 == null) || ((!((HashMap)localObject1).containsKey("url")) && (!((HashMap)localObject1).containsKey("bot"))))
              break label1253;
            this.buttonState = -1;
            bool1 = false;
            label1144: this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
            if (!bool1)
              break label1261;
            localObject2 = ImageLoader.getInstance().getFileProgress(this.currentMessageObject.messageOwner.attachPath);
            localObject1 = localObject2;
            if (localObject2 == null)
            {
              localObject1 = localObject2;
              if (SendMessagesHelper.getInstance().isSendingMessage(this.currentMessageObject.getId()))
                localObject1 = Float.valueOf(1.0F);
            }
            localObject2 = this.radialProgress;
            f1 = f2;
            if (localObject1 != null)
              f1 = ((Float)localObject1).floatValue();
            ((RadialProgress)localObject2).setProgress(f1, false);
          }
          while (true)
          {
            invalidate();
            return;
            bool1 = false;
            break;
            label1253: this.buttonState = 1;
            break label1144;
            label1261: this.radialProgress.setProgress(0.0F, false);
          }
        }
        if ((this.currentMessageObject.messageOwner.attachPath != null) && (this.currentMessageObject.messageOwner.attachPath.length() != 0))
          MediaController.getInstance().removeLoadingFileObserver(this);
        if (!bool1)
        {
          MediaController.getInstance().addLoadingFileObserver((String)localObject1, this.currentMessageObject, this);
          if (!FileLoader.getInstance().isLoadingFile((String)localObject1))
            if ((!this.cancelLoading) && (((this.currentMessageObject.type == 1) && (MediaController.getInstance().canDownloadMedia(1))) || ((this.currentMessageObject.type == 8) && (MediaController.getInstance().canDownloadMedia(32)) && (MessageObject.isNewGifDocument(this.currentMessageObject.messageOwner.media.document)))))
            {
              this.buttonState = 1;
              f1 = f3;
              bool1 = bool3;
            }
          while (true)
          {
            this.radialProgress.setBackground(getDrawableForCurrentState(), bool1, paramBoolean);
            this.radialProgress.setProgress(f1, false);
            invalidate();
            return;
            this.buttonState = 0;
            bool1 = false;
            f1 = f3;
            continue;
            this.buttonState = 1;
            localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject1);
            bool1 = bool3;
            f1 = f3;
            if (localObject1 == null)
              continue;
            f1 = ((Float)localObject1).floatValue();
            bool1 = bool3;
          }
        }
        MediaController.getInstance().removeLoadingFileObserver(this);
        if ((this.currentMessageObject.type == 8) && (!this.photoImage.isAllowStartAnimation()))
          this.buttonState = 2;
        while (true)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          if (this.photoNotSet)
            setMessageObject(this.currentMessageObject, this.pinnedBottom, this.pinnedTop);
          invalidate();
          return;
          if (this.documentAttachType == 4)
          {
            this.buttonState = 3;
            continue;
          }
          this.buttonState = -1;
        }
      }
      localObject1 = null;
      bool1 = false;
    }
  }

  private class BotButton
  {
    private int angle;
    private TLRPC.KeyboardButton button;
    private int height;
    private long lastUpdateTime;
    private float progressAlpha;
    private StaticLayout title;
    private int width;
    private int x;
    private int y;

    private BotButton()
    {
    }
  }

  public static abstract interface ChatMessageCellDelegate
  {
    public abstract boolean canPerformActions();

    public abstract void didLongPressed(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedBotButton(ChatMessageCell paramChatMessageCell, TLRPC.KeyboardButton paramKeyboardButton);

    public abstract void didPressedCancelSendButton(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedChannelAvatar(ChatMessageCell paramChatMessageCell, TLRPC.Chat paramChat, int paramInt);

    public abstract void didPressedImage(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedInstantButton(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedOther(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedReplyMessage(ChatMessageCell paramChatMessageCell, int paramInt);

    public abstract void didPressedShare(ChatMessageCell paramChatMessageCell);

    public abstract void didPressedUrl(MessageObject paramMessageObject, CharacterStyle paramCharacterStyle, boolean paramBoolean);

    public abstract void didPressedUserAvatar(ChatMessageCell paramChatMessageCell, TLRPC.User paramUser);

    public abstract void didPressedViaBot(ChatMessageCell paramChatMessageCell, String paramString);

    public abstract void needOpenWebView(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2);

    public abstract boolean needPlayAudio(MessageObject paramMessageObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ChatMessageCell
 * JD-Core Version:    0.6.0
 */