package org.vidogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.FileDownloadProgressListener;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInlineMessage;
import org.vidogram.tgnet.TLRPC.BotInlineResult;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.GeoPoint;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_peerUser;
import org.vidogram.tgnet.TLRPC.TL_photo;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.LetterDrawable;
import org.vidogram.ui.Components.RadialProgress;

public class ContextLinkCell extends View
  implements MediaController.FileDownloadProgressListener
{
  private static final int DOCUMENT_ATTACH_TYPE_AUDIO = 3;
  private static final int DOCUMENT_ATTACH_TYPE_DOCUMENT = 1;
  private static final int DOCUMENT_ATTACH_TYPE_GEO = 8;
  private static final int DOCUMENT_ATTACH_TYPE_GIF = 2;
  private static final int DOCUMENT_ATTACH_TYPE_MUSIC = 5;
  private static final int DOCUMENT_ATTACH_TYPE_NONE = 0;
  private static final int DOCUMENT_ATTACH_TYPE_PHOTO = 7;
  private static final int DOCUMENT_ATTACH_TYPE_STICKER = 6;
  private static final int DOCUMENT_ATTACH_TYPE_VIDEO = 4;
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private int TAG = MediaController.getInstance().generateObserverTag();
  private boolean buttonPressed;
  private int buttonState;
  private MessageObject currentMessageObject;
  private ContextLinkCellDelegate delegate;
  private StaticLayout descriptionLayout;
  private int descriptionY = AndroidUtilities.dp(27.0F);
  private TLRPC.Document documentAttach;
  private int documentAttachType;
  private boolean drawLinkImageView;
  private TLRPC.BotInlineResult inlineResult;
  private long lastUpdateTime;
  private LetterDrawable letterDrawable = new LetterDrawable();
  private ImageReceiver linkImageView = new ImageReceiver(this);
  private StaticLayout linkLayout;
  private int linkY;
  private boolean mediaWebpage;
  private boolean needDivider;
  private boolean needShadow;
  private RadialProgress radialProgress = new RadialProgress(this);
  private float scale;
  private boolean scaled;
  private long time = 0L;
  private StaticLayout titleLayout;
  private int titleY = AndroidUtilities.dp(7.0F);

  public ContextLinkCell(Context paramContext)
  {
    super(paramContext);
  }

  private void didPressedButton()
  {
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState != 0)
        break label59;
      if (MediaController.getInstance().playAudio(this.currentMessageObject))
      {
        this.buttonState = 1;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
      }
    }
    label59: 
    do
    {
      while (true)
      {
        return;
        if (this.buttonState != 1)
          break;
        if (!MediaController.getInstance().pauseAudio(this.currentMessageObject))
          continue;
        this.buttonState = 0;
        this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
        invalidate();
        return;
      }
      if (this.buttonState != 2)
        continue;
      this.radialProgress.setProgress(0.0F, false);
      if (this.documentAttach != null)
      {
        FileLoader.getInstance().loadFile(this.documentAttach, true, false);
        this.buttonState = 4;
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, false);
        invalidate();
        return;
      }
      ImageLoader localImageLoader = ImageLoader.getInstance();
      String str2 = this.inlineResult.content_url;
      if (this.documentAttachType == 5);
      for (String str1 = "mp3"; ; str1 = "ogg")
      {
        localImageLoader.loadHttpFile(str2, str1);
        break;
      }
    }
    while (this.buttonState != 4);
    if (this.documentAttach != null)
      FileLoader.getInstance().cancelLoadFile(this.documentAttach);
    while (true)
    {
      this.buttonState = 2;
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, false);
      invalidate();
      return;
      ImageLoader.getInstance().cancelLoadHttpFile(this.inlineResult.content_url);
    }
  }

  private Drawable getDrawableForCurrentState()
  {
    int i = 1;
    if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
    {
      if (this.buttonState == -1)
        return null;
      this.radialProgress.setAlphaForPrevious(false);
      Drawable[] arrayOfDrawable = Theme.chat_fileStatesDrawable[(this.buttonState + 5)];
      if (this.buttonPressed);
      while (true)
      {
        return arrayOfDrawable[i];
        i = 0;
      }
    }
    if (this.buttonState == 1)
      return Theme.chat_photoStatesDrawables[5][0];
    return null;
  }

  private void setAttachType()
  {
    this.currentMessageObject = null;
    this.documentAttachType = 0;
    if (this.documentAttach != null)
      if (MessageObject.isGifDocument(this.documentAttach))
        this.documentAttachType = 2;
    TLRPC.TL_message localTL_message;
    while (true)
    {
      if ((this.documentAttachType == 3) || (this.documentAttachType == 5))
      {
        localTL_message = new TLRPC.TL_message();
        localTL_message.out = true;
        localTL_message.id = (-Utilities.random.nextInt());
        localTL_message.to_id = new TLRPC.TL_peerUser();
        localObject1 = localTL_message.to_id;
        int i = UserConfig.getClientUserId();
        localTL_message.from_id = i;
        ((TLRPC.Peer)localObject1).user_id = i;
        localTL_message.date = (int)(System.currentTimeMillis() / 1000L);
        localTL_message.message = "-1";
        localTL_message.media = new TLRPC.TL_messageMediaDocument();
        localTL_message.media.document = new TLRPC.TL_document();
        localTL_message.flags |= 768;
        if (this.documentAttach == null)
          break;
        localTL_message.media.document = this.documentAttach;
        localTL_message.attachPath = "";
        this.currentMessageObject = new MessageObject(localTL_message, null, false);
      }
      return;
      if (MessageObject.isStickerDocument(this.documentAttach))
      {
        this.documentAttachType = 6;
        continue;
      }
      if (MessageObject.isMusicDocument(this.documentAttach))
      {
        this.documentAttachType = 5;
        continue;
      }
      if (!MessageObject.isVoiceDocument(this.documentAttach))
        continue;
      this.documentAttachType = 3;
      continue;
      if (this.inlineResult == null)
        continue;
      if (this.inlineResult.photo != null)
      {
        this.documentAttachType = 7;
        continue;
      }
      if (this.inlineResult.type.equals("audio"))
      {
        this.documentAttachType = 5;
        continue;
      }
      if (!this.inlineResult.type.equals("voice"))
        continue;
      this.documentAttachType = 3;
    }
    Object localObject2 = this.inlineResult.content_url;
    label347: StringBuilder localStringBuilder;
    label514: label538: String str;
    if (this.documentAttachType == 5)
    {
      localObject1 = "mp3";
      localObject1 = ImageLoader.getHttpUrlExtension((String)localObject2, (String)localObject1);
      localTL_message.media.document.id = 0L;
      localTL_message.media.document.access_hash = 0L;
      localTL_message.media.document.date = localTL_message.date;
      localTL_message.media.document.mime_type = ("audio/" + (String)localObject1);
      localTL_message.media.document.size = 0;
      localTL_message.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
      localTL_message.media.document.thumb.type = "s";
      localTL_message.media.document.dc_id = 0;
      localObject2 = new TLRPC.TL_documentAttributeAudio();
      ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.inlineResult.duration;
      if (this.inlineResult.title == null)
        break label774;
      localObject1 = this.inlineResult.title;
      ((TLRPC.TL_documentAttributeAudio)localObject2).title = ((String)localObject1);
      if (this.inlineResult.description == null)
        break label781;
      localObject1 = this.inlineResult.description;
      ((TLRPC.TL_documentAttributeAudio)localObject2).performer = ((String)localObject1);
      ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 3;
      if (this.documentAttachType == 3)
        ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
      localTL_message.media.document.attributes.add(localObject2);
      localObject2 = new TLRPC.TL_documentAttributeFilename();
      localStringBuilder = new StringBuilder().append(Utilities.MD5(this.inlineResult.content_url)).append(".");
      str = this.inlineResult.content_url;
      if (this.documentAttachType != 5)
        break label788;
      localObject1 = "mp3";
      label643: ((TLRPC.TL_documentAttributeFilename)localObject2).file_name = ImageLoader.getHttpUrlExtension(str, (String)localObject1);
      localTL_message.media.document.attributes.add(localObject2);
      localObject2 = FileLoader.getInstance().getDirectory(4);
      localStringBuilder = new StringBuilder().append(Utilities.MD5(this.inlineResult.content_url)).append(".");
      str = this.inlineResult.content_url;
      if (this.documentAttachType != 5)
        break label794;
    }
    label774: label781: label788: label794: for (Object localObject1 = "mp3"; ; localObject1 = "ogg")
    {
      localTL_message.attachPath = new File((File)localObject2, ImageLoader.getHttpUrlExtension(str, (String)localObject1)).getAbsolutePath();
      break;
      localObject1 = "ogg";
      break label347;
      localObject1 = "";
      break label514;
      localObject1 = "";
      break label538;
      localObject1 = "ogg";
      break label643;
    }
  }

  public TLRPC.Document getDocument()
  {
    return this.documentAttach;
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
    return this.linkImageView;
  }

  public TLRPC.BotInlineResult getResult()
  {
    return this.inlineResult;
  }

  public boolean isSticker()
  {
    return this.documentAttachType == 6;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if ((this.drawLinkImageView) && (this.linkImageView.onAttachedToWindow()))
      updateButtonState(false);
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.drawLinkImageView)
      this.linkImageView.onDetachedFromWindow();
    MediaController.getInstance().removeLoadingFileObserver(this);
  }

  protected void onDraw(Canvas paramCanvas)
  {
    float f2 = 8.0F;
    float f1;
    label86: String str;
    label144: label211: label229: long l2;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f1 = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f1), this.titleY);
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    else
    {
      if (this.descriptionLayout != null)
      {
        Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteGrayText2"));
        paramCanvas.save();
        if (!LocaleController.isRTL)
          break label518;
        f1 = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f1), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout != null)
      {
        Theme.chat_contextResult_descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        paramCanvas.save();
        if (!LocaleController.isRTL)
          break label526;
        f1 = f2;
        paramCanvas.translate(AndroidUtilities.dp(f1), this.linkY);
        this.linkLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.mediaWebpage)
        break label1077;
      if ((this.documentAttachType != 3) && (this.documentAttachType != 5))
        break label542;
      RadialProgress localRadialProgress = this.radialProgress;
      if (!this.buttonPressed)
        break label534;
      str = "chat_inAudioSelectedProgress";
      localRadialProgress.setProgressColor(Theme.getColor(str));
      this.radialProgress.draw(paramCanvas);
      if (this.drawLinkImageView)
      {
        paramCanvas.save();
        if (((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F)))
        {
          long l1 = System.currentTimeMillis();
          l2 = l1 - this.lastUpdateTime;
          this.lastUpdateTime = l1;
          if ((!this.scaled) || (this.scale == 0.8F))
            break label1255;
          this.scale -= (float)l2 / 400.0F;
          if (this.scale < 0.8F)
            this.scale = 0.8F;
          label347: invalidate();
        }
        paramCanvas.scale(this.scale, this.scale, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        this.linkImageView.draw(paramCanvas);
        paramCanvas.restore();
      }
      if ((this.mediaWebpage) && ((this.documentAttachType == 7) || (this.documentAttachType == 2)))
        this.radialProgress.draw(paramCanvas);
      if ((this.needDivider) && (!this.mediaWebpage))
      {
        if (!LocaleController.isRTL)
          break label1288;
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
      }
    }
    while (true)
    {
      if (this.needShadow)
      {
        Theme.chat_contextResult_shadowUnderSwitchDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(3.0F));
        Theme.chat_contextResult_shadowUnderSwitchDrawable.draw(paramCanvas);
      }
      return;
      f1 = AndroidUtilities.leftBaseline;
      break;
      label518: f1 = AndroidUtilities.leftBaseline;
      break label86;
      label526: f1 = AndroidUtilities.leftBaseline;
      break label144;
      label534: str = "chat_inAudioProgress";
      break label211;
      label542: if ((this.inlineResult != null) && (this.inlineResult.type.equals("file")))
      {
        i = Theme.chat_inlineResultFile.getIntrinsicWidth();
        j = Theme.chat_inlineResultFile.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultFile.setBounds(k, m, k + i, m + j);
        Theme.chat_inlineResultFile.draw(paramCanvas);
        break label229;
      }
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("audio")) || (this.inlineResult.type.equals("voice"))))
      {
        i = Theme.chat_inlineResultAudio.getIntrinsicWidth();
        j = Theme.chat_inlineResultAudio.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultAudio.setBounds(k, m, k + i, m + j);
        Theme.chat_inlineResultAudio.draw(paramCanvas);
        break label229;
      }
      if ((this.inlineResult != null) && ((this.inlineResult.type.equals("venue")) || (this.inlineResult.type.equals("geo"))))
      {
        i = Theme.chat_inlineResultLocation.getIntrinsicWidth();
        j = Theme.chat_inlineResultLocation.getIntrinsicHeight();
        k = this.linkImageView.getImageX() + (AndroidUtilities.dp(52.0F) - i) / 2;
        m = this.linkImageView.getImageY() + (AndroidUtilities.dp(52.0F) - j) / 2;
        paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + AndroidUtilities.dp(52.0F), this.linkImageView.getImageY() + AndroidUtilities.dp(52.0F), LetterDrawable.paint);
        Theme.chat_inlineResultLocation.setBounds(k, m, k + i, m + j);
        Theme.chat_inlineResultLocation.draw(paramCanvas);
        break label229;
      }
      this.letterDrawable.draw(paramCanvas);
      break label229;
      label1077: if ((this.inlineResult == null) || ((!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo)) && (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))))
        break label229;
      int i = Theme.chat_inlineResultLocation.getIntrinsicWidth();
      int j = Theme.chat_inlineResultLocation.getIntrinsicHeight();
      int k = this.linkImageView.getImageX() + (this.linkImageView.getImageWidth() - i) / 2;
      int m = this.linkImageView.getImageY() + (this.linkImageView.getImageHeight() - j) / 2;
      paramCanvas.drawRect(this.linkImageView.getImageX(), this.linkImageView.getImageY(), this.linkImageView.getImageX() + this.linkImageView.getImageWidth(), this.linkImageView.getImageY() + this.linkImageView.getImageHeight(), LetterDrawable.paint);
      Theme.chat_inlineResultLocation.setBounds(k, m, k + i, m + j);
      Theme.chat_inlineResultLocation.draw(paramCanvas);
      break label229;
      label1255: this.scale += (float)l2 / 400.0F;
      if (this.scale <= 1.0F)
        break label347;
      this.scale = 1.0F;
      break label347;
      label1288: paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
  }

  public void onFailedDownload(String paramString)
  {
    updateButtonState(false);
  }

  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.drawLinkImageView = false;
    this.descriptionLayout = null;
    this.titleLayout = null;
    this.linkLayout = null;
    this.linkY = AndroidUtilities.dp(27.0F);
    if ((this.inlineResult == null) && (this.documentAttach == null))
    {
      setMeasuredDimension(AndroidUtilities.dp(100.0F), AndroidUtilities.dp(100.0F));
      return;
    }
    int n = View.MeasureSpec.getSize(paramInt1);
    int i = n - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(8.0F);
    Object localObject1;
    if (this.documentAttach != null)
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(this.documentAttach.thumb);
    }
    while (true)
      while (true)
      {
        if ((this.mediaWebpage) || (this.inlineResult == null) || (this.inlineResult.title != null));
        label1808: label1941: label1951: 
        try
        {
          j = (int)Math.ceil(Theme.chat_contextResult_titleTextPaint.measureText(this.inlineResult.title));
          this.titleLayout = new StaticLayout(TextUtils.ellipsize(Emoji.replaceEmoji(this.inlineResult.title.replace('\n', ' '), Theme.chat_contextResult_titleTextPaint.getFontMetricsInt(), AndroidUtilities.dp(15.0F), false), Theme.chat_contextResult_titleTextPaint, Math.min(j, i), TextUtils.TruncateAt.END), Theme.chat_contextResult_titleTextPaint, AndroidUtilities.dp(4.0F) + i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
          this.letterDrawable.setTitle(this.inlineResult.title);
          if (this.inlineResult.description == null);
        }
        catch (Exception localObject2)
        {
          label1311: label1963: 
          try
          {
            this.descriptionLayout = ChatMessageCell.generateStaticLayout(Emoji.replaceEmoji(this.inlineResult.description, Theme.chat_contextResult_descriptionTextPaint.getFontMetricsInt(), AndroidUtilities.dp(13.0F), false), Theme.chat_contextResult_descriptionTextPaint, i, i, 0, 3);
            if (this.descriptionLayout.getLineCount() > 0)
              this.linkY = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
            if (this.inlineResult.url == null);
          }
          catch (Exception localObject2)
          {
            label1196: label1969: 
            try
            {
              while (true)
              {
                j = (int)Math.ceil(Theme.chat_contextResult_descriptionTextPaint.measureText(this.inlineResult.url));
                this.linkLayout = new StaticLayout(TextUtils.ellipsize(this.inlineResult.url.replace('\n', ' '), Theme.chat_contextResult_descriptionTextPaint, Math.min(j, i), TextUtils.TruncateAt.MIDDLE), Theme.chat_contextResult_descriptionTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                str = null;
                if (this.documentAttach == null)
                  break label1257;
                if (!MessageObject.isGifDocument(this.documentAttach))
                  break label1196;
                localObject1 = this.documentAttach.thumb;
                ImageReceiver localImageReceiver = null;
                if (this.inlineResult == null)
                  break label1969;
                if ((this.inlineResult.content_url == null) || (this.inlineResult.type == null))
                  break label1963;
                if (!this.inlineResult.type.startsWith("gif"))
                  break label1311;
                if (this.documentAttachType == 2)
                  break label1963;
                localObject3 = this.inlineResult.content_url;
                this.documentAttachType = 2;
                localObject4 = localObject3;
                if (localObject3 == null)
                {
                  localObject4 = localObject3;
                  if (this.inlineResult.thumb_url != null)
                    localObject4 = this.inlineResult.thumb_url;
                }
                localObject5 = localObject4;
                if (localObject4 == null)
                {
                  localObject5 = localObject4;
                  if (localObject1 == null)
                  {
                    localObject5 = localObject4;
                    if (localImageReceiver == null)
                      if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
                      {
                        localObject5 = localObject4;
                        if (!(this.inlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo));
                      }
                      else
                      {
                        double d1 = this.inlineResult.send_message.geo.lat;
                        double d2 = this.inlineResult.send_message.geo._long;
                        localObject5 = String.format(Locale.US, "https://maps.googleapis.com/maps/api/staticmap?center=%f,%f&zoom=15&size=72x72&maptype=roadmap&scale=%d&markers=color:red|size:small|%f,%f&sensor=false", new Object[] { Double.valueOf(d1), Double.valueOf(d2), Integer.valueOf(Math.min(2, (int)Math.ceil(AndroidUtilities.density))), Double.valueOf(d1), Double.valueOf(d2) });
                      }
                  }
                }
                if (this.documentAttach == null)
                  break label1951;
                i = 0;
                if (i >= this.documentAttach.attributes.size())
                  break label1951;
                localObject3 = (TLRPC.DocumentAttribute)this.documentAttach.attributes.get(i);
                if ((!(localObject3 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject3 instanceof TLRPC.TL_documentAttributeVideo)))
                  break label1353;
                m = ((TLRPC.DocumentAttribute)localObject3).w;
                k = ((TLRPC.DocumentAttribute)localObject3).h;
                if (m != 0)
                {
                  i = k;
                  j = m;
                  if (k != 0);
                }
                else
                {
                  if (localObject1 == null)
                    break label1362;
                  if (localImageReceiver != null)
                    localImageReceiver.size = -1;
                  j = ((TLRPC.PhotoSize)localObject1).w;
                  i = ((TLRPC.PhotoSize)localObject1).h;
                }
                if (j != 0)
                {
                  k = i;
                  if (i != 0);
                }
                else
                {
                  k = AndroidUtilities.dp(80.0F);
                  j = k;
                }
                if ((this.documentAttach != null) || (localObject1 != null) || (localObject5 != null))
                {
                  localObject4 = "52_52_b";
                  if (!this.mediaWebpage)
                    break label1461;
                  i = (int)(j / (k / AndroidUtilities.dp(80.0F)));
                  if (this.documentAttachType != 2)
                    break label1398;
                  localObject4 = String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf((int)(i / AndroidUtilities.density)), Integer.valueOf(80) });
                  localObject3 = localObject4;
                  localObject6 = this.linkImageView;
                  if (this.documentAttachType != 6)
                    break label1469;
                  bool = true;
                  ((ImageReceiver)localObject6).setAspectFit(bool);
                  if (this.documentAttachType != 2)
                    break label1526;
                  if (this.documentAttach == null)
                    break label1481;
                  localImageReceiver = this.linkImageView;
                  localObject4 = this.documentAttach;
                  if (localObject1 == null)
                    break label1475;
                  localObject1 = ((TLRPC.PhotoSize)localObject1).location;
                  localImageReceiver.setImage((TLObject)localObject4, null, (TLRPC.FileLocation)localObject1, (String)localObject3, this.documentAttach.size, str, false);
                  this.drawLinkImageView = true;
                }
                if (!this.mediaWebpage)
                  break label1632;
                paramInt2 = View.MeasureSpec.getSize(paramInt2);
                paramInt1 = paramInt2;
                if (paramInt2 == 0)
                  paramInt1 = AndroidUtilities.dp(100.0F);
                setMeasuredDimension(n, paramInt1);
                paramInt2 = (n - AndroidUtilities.dp(24.0F)) / 2;
                i = (paramInt1 - AndroidUtilities.dp(24.0F)) / 2;
                this.radialProgress.setProgressRect(paramInt2, i, AndroidUtilities.dp(24.0F) + paramInt2, AndroidUtilities.dp(24.0F) + i);
                this.linkImageView.setImageCoords(0, 0, n, paramInt1);
                return;
                if ((this.inlineResult == null) || (this.inlineResult.photo == null))
                  break label1995;
                localObject1 = new ArrayList(this.inlineResult.photo.sizes);
                break;
                localException1 = localException1;
                FileLog.e(localException1);
              }
              localException2 = localException2;
              FileLog.e(localException2);
            }
            catch (Exception localObject2)
            {
              label1353: label1362: label1632: 
              while (true)
              {
                int j;
                String str;
                Object localObject3;
                Object localObject4;
                Object localObject5;
                int m;
                int k;
                Object localObject6;
                boolean bool;
                FileLog.e(localException3);
                continue;
                if (MessageObject.isStickerDocument(this.documentAttach))
                {
                  localObject1 = this.documentAttach.thumb;
                  str = "webp";
                  localObject2 = null;
                  continue;
                }
                if ((this.documentAttachType != 5) && (this.documentAttachType != 3))
                {
                  localObject1 = this.documentAttach.thumb;
                  localObject2 = null;
                  continue;
                  if ((this.inlineResult != null) && (this.inlineResult.photo != null))
                  {
                    localObject2 = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject1, AndroidUtilities.getPhotoSize(), true);
                    localObject3 = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject1, 80);
                    if (localObject3 == localObject2)
                    {
                      localObject1 = localObject2;
                      localObject2 = null;
                      continue;
                      if (this.inlineResult.type.equals("photo"))
                      {
                        localObject3 = this.inlineResult.thumb_url;
                        if (localObject3 == null)
                        {
                          localObject3 = this.inlineResult.content_url;
                          continue;
                          i += 1;
                          continue;
                          i = k;
                          j = m;
                          if (this.inlineResult == null)
                            continue;
                          j = this.inlineResult.w;
                          i = this.inlineResult.h;
                          continue;
                          localObject3 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf((int)(i / AndroidUtilities.density)), Integer.valueOf(80) });
                          localObject4 = (String)localObject3 + "_b";
                          continue;
                          localObject3 = "52_52";
                          continue;
                          bool = false;
                          continue;
                          localObject1 = null;
                          continue;
                          localObject2 = this.linkImageView;
                          if (localObject1 != null);
                          for (localObject1 = ((TLRPC.PhotoSize)localObject1).location; ; localObject1 = null)
                          {
                            ((ImageReceiver)localObject2).setImage(null, (String)localObject5, null, null, (TLRPC.FileLocation)localObject1, (String)localObject3, -1, str, true);
                            break;
                          }
                          if (localObject1 != null)
                          {
                            localObject5 = this.linkImageView;
                            localObject6 = ((TLRPC.PhotoSize)localObject1).location;
                            if (localObject2 != null);
                            for (localObject2 = ((TLRPC.PhotoSize)localObject2).location; ; localObject2 = null)
                            {
                              ((ImageReceiver)localObject5).setImage((TLObject)localObject6, (String)localObject3, (TLRPC.FileLocation)localObject2, (String)localObject4, ((TLRPC.PhotoSize)localObject1).size, str, false);
                              break;
                            }
                          }
                          localObject6 = this.linkImageView;
                          if (localObject2 != null);
                          for (localObject1 = ((TLRPC.PhotoSize)localObject2).location; ; localObject1 = null)
                          {
                            ((ImageReceiver)localObject6).setImage(null, (String)localObject5, (String)localObject3, null, (TLRPC.FileLocation)localObject1, (String)localObject4, -1, str, true);
                            break;
                          }
                          i = 0;
                          paramInt2 = i;
                          if (this.titleLayout != null)
                          {
                            paramInt2 = i;
                            if (this.titleLayout.getLineCount() != 0)
                              paramInt2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                          }
                          i = paramInt2;
                          if (this.descriptionLayout != null)
                          {
                            i = paramInt2;
                            if (this.descriptionLayout.getLineCount() != 0)
                              i = paramInt2 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                          }
                          paramInt2 = i;
                          if (this.linkLayout != null)
                          {
                            paramInt2 = i;
                            if (this.linkLayout.getLineCount() > 0)
                              paramInt2 = i + this.linkLayout.getLineBottom(this.linkLayout.getLineCount() - 1);
                          }
                          paramInt2 = Math.max(AndroidUtilities.dp(52.0F), paramInt2);
                          i = View.MeasureSpec.getSize(paramInt1);
                          j = Math.max(AndroidUtilities.dp(68.0F), paramInt2 + AndroidUtilities.dp(16.0F));
                          if (this.needDivider)
                          {
                            paramInt2 = 1;
                            setMeasuredDimension(i, paramInt2 + j);
                            paramInt2 = AndroidUtilities.dp(52.0F);
                            if (!LocaleController.isRTL)
                              break label1941;
                          }
                          for (paramInt1 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(8.0F) - paramInt2; ; paramInt1 = AndroidUtilities.dp(8.0F))
                          {
                            this.letterDrawable.setBounds(paramInt1, AndroidUtilities.dp(8.0F), paramInt1 + paramInt2, AndroidUtilities.dp(60.0F));
                            this.linkImageView.setImageCoords(paramInt1, AndroidUtilities.dp(8.0F), paramInt2, paramInt2);
                            if ((this.documentAttachType != 3) && (this.documentAttachType != 5))
                              break;
                            this.radialProgress.setProgressRect(AndroidUtilities.dp(4.0F) + paramInt1, AndroidUtilities.dp(12.0F), paramInt1 + AndroidUtilities.dp(48.0F), AndroidUtilities.dp(56.0F));
                            return;
                            paramInt2 = 0;
                            break label1808;
                          }
                          k = 0;
                          m = 0;
                          continue;
                        }
                        continue;
                      }
                      localObject3 = null;
                      continue;
                      localObject4 = null;
                      continue;
                    }
                    localObject1 = localObject2;
                    localObject2 = localObject3;
                    continue;
                  }
                }
                label1257: label1398: Object localObject2 = null;
                label1526: localObject1 = null;
              }
              label1461: label1469: label1475: label1481: label1995: localObject1 = null;
            }
          }
        }
      }
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
  }

  public void onSuccessDownload(String paramString)
  {
    this.radialProgress.setProgress(1.0F, true);
    updateButtonState(true);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool1 = true;
    boolean bool2;
    if ((this.mediaWebpage) || (this.delegate == null) || (this.inlineResult == null))
      bool2 = super.onTouchEvent(paramMotionEvent);
    int i;
    int j;
    while (true)
    {
      return bool2;
      i = (int)paramMotionEvent.getX();
      j = (int)paramMotionEvent.getY();
      AndroidUtilities.dp(48.0F);
      if ((this.documentAttachType != 3) && (this.documentAttachType != 5))
        break label227;
      bool2 = this.letterDrawable.getBounds().contains(i, j);
      if (paramMotionEvent.getAction() != 0)
        break;
      if (!bool2)
        break label176;
      this.buttonPressed = true;
      invalidate();
      this.radialProgress.swapBackground(getDrawableForCurrentState());
      bool2 = bool1;
      if (!bool1)
        return super.onTouchEvent(paramMotionEvent);
    }
    if (this.buttonPressed)
    {
      if (paramMotionEvent.getAction() != 1)
        break label182;
      this.buttonPressed = false;
      playSoundEffect(0);
      didPressedButton();
      invalidate();
      label164: this.radialProgress.swapBackground(getDrawableForCurrentState());
    }
    while (true)
    {
      label176: bool1 = false;
      break;
      label182: if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = false;
        invalidate();
        break label164;
      }
      if ((paramMotionEvent.getAction() != 2) || (bool2))
        break label164;
      this.buttonPressed = false;
      invalidate();
      break label164;
      label227: if ((this.inlineResult == null) || (this.inlineResult.content_url == null) || (this.inlineResult.content_url.length() <= 0))
        continue;
      if (paramMotionEvent.getAction() == 0)
      {
        if (!this.letterDrawable.getBounds().contains(i, j))
          continue;
        this.buttonPressed = true;
        break;
      }
      if (!this.buttonPressed)
        continue;
      if (paramMotionEvent.getAction() == 1)
      {
        this.buttonPressed = false;
        playSoundEffect(0);
        this.delegate.didPressedImage(this);
        bool1 = false;
        break;
      }
      if (paramMotionEvent.getAction() == 3)
      {
        this.buttonPressed = false;
        bool1 = false;
        break;
      }
      if ((paramMotionEvent.getAction() != 2) || (this.letterDrawable.getBounds().contains(i, j)))
        continue;
      this.buttonPressed = false;
    }
  }

  public void setDelegate(ContextLinkCellDelegate paramContextLinkCellDelegate)
  {
    this.delegate = paramContextLinkCellDelegate;
  }

  public void setGif(TLRPC.Document paramDocument, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    this.needShadow = false;
    this.inlineResult = null;
    this.documentAttach = paramDocument;
    this.mediaWebpage = true;
    setAttachType();
    requestLayout();
    updateButtonState(false);
  }

  public void setLink(TLRPC.BotInlineResult paramBotInlineResult, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    this.needDivider = paramBoolean2;
    this.needShadow = paramBoolean3;
    this.inlineResult = paramBotInlineResult;
    if ((this.inlineResult != null) && (this.inlineResult.document != null));
    for (this.documentAttach = this.inlineResult.document; ; this.documentAttach = null)
    {
      this.mediaWebpage = paramBoolean1;
      setAttachType();
      requestLayout();
      updateButtonState(false);
      return;
    }
  }

  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  public boolean showingBitmap()
  {
    return this.linkImageView.getBitmap() != null;
  }

  public void updateButtonState(boolean paramBoolean)
  {
    File localFile = null;
    StringBuilder localStringBuilder = null;
    Object localObject2;
    Object localObject1;
    if ((this.documentAttachType == 5) || (this.documentAttachType == 3))
      if (this.documentAttach != null)
      {
        localObject2 = FileLoader.getAttachFileName(this.documentAttach);
        localObject1 = FileLoader.getPathToAttach(this.documentAttach);
      }
    while (TextUtils.isEmpty((CharSequence)localObject2))
    {
      this.radialProgress.setBackground(null, false, false);
      return;
      localObject2 = this.inlineResult.content_url;
      localFile = FileLoader.getInstance().getDirectory(4);
      localStringBuilder = new StringBuilder().append(Utilities.MD5(this.inlineResult.content_url)).append(".");
      String str = this.inlineResult.content_url;
      if (this.documentAttachType == 5);
      for (localObject1 = "mp3"; ; localObject1 = "ogg")
      {
        localObject1 = new File(localFile, ImageLoader.getHttpUrlExtension(str, (String)localObject1));
        break;
      }
      localObject1 = localStringBuilder;
      localObject2 = localFile;
      if (!this.mediaWebpage)
        continue;
      if (this.inlineResult != null)
      {
        if ((this.inlineResult.document instanceof TLRPC.TL_document))
        {
          localObject2 = FileLoader.getAttachFileName(this.inlineResult.document);
          localObject1 = FileLoader.getPathToAttach(this.inlineResult.document);
          continue;
        }
        if ((this.inlineResult.photo instanceof TLRPC.TL_photo))
        {
          localObject1 = FileLoader.getClosestPhotoSizeWithSize(this.inlineResult.photo.sizes, AndroidUtilities.getPhotoSize(), true);
          localObject2 = FileLoader.getAttachFileName((TLObject)localObject1);
          localObject1 = FileLoader.getPathToAttach((TLObject)localObject1);
          continue;
        }
        if (this.inlineResult.content_url != null)
        {
          localObject2 = Utilities.MD5(this.inlineResult.content_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.content_url, "jpg");
          localObject1 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
          continue;
        }
        localObject1 = localStringBuilder;
        localObject2 = localFile;
        if (this.inlineResult.thumb_url == null)
          continue;
        localObject2 = Utilities.MD5(this.inlineResult.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.inlineResult.thumb_url, "jpg");
        localObject1 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
        continue;
      }
      localObject1 = localStringBuilder;
      localObject2 = localFile;
      if (this.documentAttach == null)
        continue;
      localObject2 = FileLoader.getAttachFileName(this.documentAttach);
      localObject1 = FileLoader.getPathToAttach(this.documentAttach);
    }
    if ((((File)localObject1).exists()) && (((File)localObject1).length() == 0L))
      ((File)localObject1).delete();
    boolean bool;
    if (!((File)localObject1).exists())
    {
      MediaController.getInstance().addLoadingFileObserver((String)localObject2, this);
      if ((this.documentAttachType == 5) || (this.documentAttachType == 3))
      {
        if (this.documentAttach != null);
        for (bool = FileLoader.getInstance().isLoadingFile((String)localObject2); !bool; bool = ImageLoader.getInstance().isLoadingHttpFile((String)localObject2))
        {
          this.buttonState = 2;
          this.radialProgress.setProgress(0.0F, paramBoolean);
          this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
          invalidate();
          return;
        }
        this.buttonState = 4;
        localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject2);
        if (localObject1 != null)
          this.radialProgress.setProgress(((Float)localObject1).floatValue(), paramBoolean);
        while (true)
        {
          this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
          break;
          this.radialProgress.setProgress(0.0F, paramBoolean);
        }
      }
      this.buttonState = 1;
      localObject1 = ImageLoader.getInstance().getFileProgress((String)localObject2);
      float f;
      if (localObject1 != null)
        f = ((Float)localObject1).floatValue();
      while (true)
      {
        this.radialProgress.setProgress(f, false);
        this.radialProgress.setBackground(getDrawableForCurrentState(), true, paramBoolean);
        break;
        f = 0.0F;
      }
    }
    MediaController.getInstance().removeLoadingFileObserver(this);
    if ((this.documentAttachType == 5) || (this.documentAttachType == 3))
    {
      bool = MediaController.getInstance().isPlayingAudio(this.currentMessageObject);
      if ((!bool) || ((bool) && (MediaController.getInstance().isAudioPaused())))
        this.buttonState = 0;
    }
    while (true)
    {
      this.radialProgress.setBackground(getDrawableForCurrentState(), false, paramBoolean);
      invalidate();
      return;
      this.buttonState = 1;
      continue;
      this.buttonState = -1;
    }
  }

  public static abstract interface ContextLinkCellDelegate
  {
    public abstract void didPressedImage(ContextLinkCell paramContextLinkCell);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ContextLinkCell
 * JD-Core Version:    0.6.0
 */