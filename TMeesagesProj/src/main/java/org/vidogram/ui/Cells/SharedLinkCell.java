package org.vidogram.ui.Cells;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_messageEntityEmail;
import org.vidogram.tgnet.TLRPC.TL_messageEntityTextUrl;
import org.vidogram.tgnet.TLRPC.TL_messageEntityUrl;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_webPage;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.LetterDrawable;
import org.vidogram.ui.Components.LinkPath;

public class SharedLinkCell extends FrameLayout
{
  private CheckBox checkBox;
  private SharedLinkCellDelegate delegate;
  private int description2Y = AndroidUtilities.dp(27.0F);
  private StaticLayout descriptionLayout;
  private StaticLayout descriptionLayout2;
  private TextPaint descriptionTextPaint;
  private int descriptionY = AndroidUtilities.dp(27.0F);
  private boolean drawLinkImageView;
  private LetterDrawable letterDrawable;
  private ImageReceiver linkImageView;
  private ArrayList<StaticLayout> linkLayout = new ArrayList();
  private boolean linkPreviewPressed;
  private int linkY;
  ArrayList<String> links = new ArrayList();
  private MessageObject message;
  private boolean needDivider;
  private int pressedLink;
  private StaticLayout titleLayout;
  private TextPaint titleTextPaint = new TextPaint(1);
  private int titleY = AndroidUtilities.dp(7.0F);
  private LinkPath urlPath = new LinkPath();

  public SharedLinkCell(Context paramContext)
  {
    super(paramContext);
    this.titleTextPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.titleTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.descriptionTextPaint = new TextPaint(1);
    this.titleTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    this.descriptionTextPaint.setTextSize(AndroidUtilities.dp(16.0F));
    setWillNotDraw(false);
    this.linkImageView = new ImageReceiver(this);
    this.letterDrawable = new LetterDrawable();
    this.checkBox = new CheckBox(paramContext, 2130838041);
    this.checkBox.setVisibility(4);
    this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
    paramContext = this.checkBox;
    int i;
    float f1;
    label229: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label267;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label273;
      f2 = 44.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(22, 22.0F, i | 0x30, f1, 44.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label267: f1 = 44.0F;
      break label229;
      label273: f2 = 0.0F;
    }
  }

  public String getLink(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.links.size()))
      return null;
    return (String)this.links.get(paramInt);
  }

  public MessageObject getMessage()
  {
    return this.message;
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.drawLinkImageView)
      this.linkImageView.onAttachedToWindow();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.drawLinkImageView)
      this.linkImageView.onDetachedFromWindow();
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int i = 0;
    float f;
    label82: int j;
    label141: label192: StaticLayout localStaticLayout;
    int k;
    if (this.titleLayout != null)
    {
      paramCanvas.save();
      if (LocaleController.isRTL)
      {
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.titleY);
        this.titleLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
    }
    else
    {
      if (this.descriptionLayout != null)
      {
        this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        paramCanvas.save();
        if (!LocaleController.isRTL)
          break label324;
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.descriptionY);
        this.descriptionLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.descriptionLayout2 != null)
      {
        this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        paramCanvas.save();
        if (!LocaleController.isRTL)
          break label332;
        f = 8.0F;
        paramCanvas.translate(AndroidUtilities.dp(f), this.description2Y);
        this.descriptionLayout2.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.linkLayout.isEmpty())
        break label348;
      this.descriptionTextPaint.setColor(Theme.getColor("windowBackgroundWhiteLinkText"));
      j = 0;
      if (i >= this.linkLayout.size())
        break label348;
      localStaticLayout = (StaticLayout)this.linkLayout.get(i);
      k = j;
      if (localStaticLayout.getLineCount() > 0)
      {
        paramCanvas.save();
        if (!LocaleController.isRTL)
          break label340;
        f = 8.0F;
      }
    }
    while (true)
    {
      paramCanvas.translate(AndroidUtilities.dp(f), this.linkY + j);
      if (this.pressedLink == i)
        paramCanvas.drawPath(this.urlPath, Theme.linkSelectionPaint);
      localStaticLayout.draw(paramCanvas);
      paramCanvas.restore();
      k = j + localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
      i += 1;
      j = k;
      break label192;
      f = AndroidUtilities.leftBaseline;
      break;
      label324: f = AndroidUtilities.leftBaseline;
      break label82;
      label332: f = AndroidUtilities.leftBaseline;
      break label141;
      label340: f = AndroidUtilities.leftBaseline;
    }
    label348: this.letterDrawable.draw(paramCanvas);
    if (this.drawLinkImageView)
      this.linkImageView.draw(paramCanvas);
    if (this.needDivider)
    {
      if (LocaleController.isRTL)
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
    else
      return;
    paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
  }

  @SuppressLint({"DrawAllocation"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    this.drawLinkImageView = false;
    this.descriptionLayout = null;
    this.titleLayout = null;
    this.descriptionLayout2 = null;
    this.description2Y = this.descriptionY;
    this.linkLayout.clear();
    this.links.clear();
    int i = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(AndroidUtilities.leftBaseline) - AndroidUtilities.dp(8.0F);
    String str2 = null;
    Object localObject7;
    Object localObject3;
    Object localObject1;
    Object localObject6;
    int j;
    if (((this.message.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && ((this.message.messageOwner.media.webpage instanceof TLRPC.TL_webPage)))
    {
      localObject7 = this.message.messageOwner.media.webpage;
      if ((this.message.photoThumbs == null) && (((TLRPC.WebPage)localObject7).photo != null))
        this.message.generateThumbs(true);
      if ((((TLRPC.WebPage)localObject7).photo != null) && (this.message.photoThumbs != null))
      {
        paramInt2 = 1;
        localObject3 = ((TLRPC.WebPage)localObject7).title;
        localObject1 = localObject3;
        if (localObject3 == null)
          localObject1 = ((TLRPC.WebPage)localObject7).site_name;
        localObject6 = ((TLRPC.WebPage)localObject7).description;
        str2 = ((TLRPC.WebPage)localObject7).url;
        j = paramInt2;
        localObject3 = localObject1;
        localObject1 = localObject6;
      }
    }
    while (true)
    {
      TLRPC.MessageEntity localMessageEntity;
      Object localObject9;
      if ((this.message != null) && (!this.message.messageOwner.entities.isEmpty()))
      {
        paramInt2 = 0;
        localObject7 = null;
        localObject6 = localObject1;
        localObject1 = localObject7;
        while (true)
        {
          if (paramInt2 >= this.message.messageOwner.entities.size())
            break label1118;
          localMessageEntity = (TLRPC.MessageEntity)this.message.messageOwner.entities.get(paramInt2);
          if ((localMessageEntity.length <= 0) || (localMessageEntity.offset < 0))
            break label2232;
          if (localMessageEntity.offset >= this.message.messageOwner.message.length())
          {
            paramInt2 += 1;
            continue;
            paramInt2 = 0;
            break;
          }
        }
        if (localMessageEntity.offset + localMessageEntity.length > this.message.messageOwner.message.length())
          localMessageEntity.length = (this.message.messageOwner.message.length() - localMessageEntity.offset);
        localObject9 = localObject1;
        if (paramInt2 == 0)
        {
          localObject9 = localObject1;
          if (str2 != null)
            if (localMessageEntity.offset == 0)
            {
              localObject9 = localObject1;
              if (localMessageEntity.length == this.message.messageOwner.message.length());
            }
            else
            {
              if (this.message.messageOwner.entities.size() != 1)
                break label852;
              localObject9 = localObject1;
              if (localObject6 == null)
                localObject9 = this.message.messageOwner.message;
            }
        }
        label468: localObject7 = null;
        localObject1 = localObject3;
      }
      while (true)
      {
        label542: Object localObject11;
        Object localObject10;
        try
        {
          if ((localMessageEntity instanceof TLRPC.TL_messageEntityTextUrl))
            continue;
          localObject1 = localObject3;
          if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl))
            continue;
          localObject1 = localObject3;
          if (!(localMessageEntity instanceof TLRPC.TL_messageEntityUrl))
            continue;
          localObject1 = localObject3;
          localObject7 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
          if (localObject3 == null)
            continue;
          localObject1 = localObject3;
          k = ((String)localObject3).length();
          localObject11 = localObject3;
          localObject10 = localObject7;
          if (k != 0)
            break label2235;
        }
        catch (Exception localException6)
        {
          try
          {
            int k;
            while (true)
            {
              localObject1 = Uri.parse((String)localObject7).getHost();
              localObject10 = localObject1;
              if (localObject1 == null)
                localObject10 = localObject7;
              localObject3 = localObject10;
              if (localObject10 != null)
              {
                localObject1 = localObject10;
                k = ((String)localObject10).lastIndexOf('.');
                localObject3 = localObject10;
                if (k >= 0)
                {
                  localObject1 = localObject10;
                  localObject10 = ((String)localObject10).substring(0, k);
                  localObject1 = localObject10;
                  k = ((String)localObject10).lastIndexOf('.');
                  localObject3 = localObject10;
                  if (k >= 0)
                  {
                    localObject1 = localObject10;
                    localObject3 = ((String)localObject10).substring(k + 1);
                  }
                  localObject1 = localObject3;
                  localObject3 = ((String)localObject3).substring(0, 1).toUpperCase() + ((String)localObject3).substring(1);
                }
              }
              localObject1 = localObject3;
              if (localMessageEntity.offset == 0)
              {
                localObject1 = localObject3;
                if (localMessageEntity.length == this.message.messageOwner.message.length())
                  break label2246;
              }
              localObject1 = localObject3;
              localObject10 = this.message.messageOwner.message;
              localObject1 = localObject10;
              localObject6 = localObject1;
              localObject1 = localObject3;
              label779: if (localObject7 != null);
              try
              {
                if ((((String)localObject7).toLowerCase().indexOf("http") != 0) && (((String)localObject7).toLowerCase().indexOf("mailto") != 0))
                  this.links.add("http://" + (String)localObject7);
                while (true)
                {
                  localObject3 = localObject1;
                  localObject1 = localObject9;
                  break;
                  label852: localObject9 = this.message.messageOwner.message;
                  break label468;
                  localObject1 = localObject3;
                  localObject7 = localMessageEntity.url;
                  break label542;
                  localObject1 = localObject3;
                  localObject11 = localObject3;
                  localObject10 = localObject7;
                  if (!(localMessageEntity instanceof TLRPC.TL_messageEntityEmail))
                    break label2235;
                  if (localObject3 != null)
                  {
                    localObject1 = localObject3;
                    localObject11 = localObject3;
                    localObject10 = localObject7;
                    if (((String)localObject3).length() != 0)
                      break label2235;
                  }
                  localObject1 = localObject3;
                  localObject7 = "mailto:" + this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject1 = localObject3;
                  localObject3 = this.message.messageOwner.message.substring(localMessageEntity.offset, localMessageEntity.offset + localMessageEntity.length);
                  localObject1 = localObject3;
                  if (localMessageEntity.offset == 0)
                  {
                    localObject1 = localObject3;
                    localObject11 = localObject3;
                    localObject10 = localObject7;
                    if (localMessageEntity.length == this.message.messageOwner.message.length())
                      break label2235;
                  }
                  localObject1 = localObject3;
                  localObject10 = this.message.messageOwner.message;
                  localObject1 = localObject3;
                  localObject6 = localObject10;
                  break label779;
                  this.links.add(localObject7);
                }
              }
              catch (Exception localException3)
              {
              }
            }
            FileLog.e(localException3);
            Object localObject4 = localObject1;
            localObject1 = localObject9;
            break;
            label1118: localObject7 = localObject6;
            localObject6 = localObject4;
            localObject4 = localObject7;
            if ((str2 == null) || (!this.links.isEmpty()))
              continue;
            this.links.add(str2);
            if (localObject6 == null)
              continue;
            try
            {
              paramInt2 = (int)Math.ceil(this.titleTextPaint.measureText((String)localObject6));
              this.titleLayout = new StaticLayout(TextUtils.ellipsize(((String)localObject6).replace('\n', ' '), this.titleTextPaint, Math.min(paramInt2, i), TextUtils.TruncateAt.END), this.titleTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
              this.letterDrawable.setTitle((String)localObject6);
              if (localObject4 == null)
                continue;
            }
            catch (Exception localObject2)
            {
              try
              {
                this.descriptionLayout = ChatMessageCell.generateStaticLayout(localObject4, this.descriptionTextPaint, i, i, 0, 3);
                if (this.descriptionLayout.getLineCount() <= 0)
                  continue;
                this.description2Y = (this.descriptionY + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1) + AndroidUtilities.dp(1.0F));
                if (localObject1 == null)
                  continue;
              }
              catch (Exception localObject2)
              {
                try
                {
                  this.descriptionLayout2 = ChatMessageCell.generateStaticLayout((CharSequence)localObject1, this.descriptionTextPaint, i, i, 0, 3);
                  this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
                  if (this.descriptionLayout == null)
                    continue;
                  this.description2Y += AndroidUtilities.dp(10.0F);
                  if (this.links.isEmpty())
                    continue;
                  paramInt2 = 0;
                  if (paramInt2 >= this.links.size())
                    continue;
                }
                catch (Exception localObject2)
                {
                  try
                  {
                    localObject1 = (String)this.links.get(paramInt2);
                    k = (int)Math.ceil(this.descriptionTextPaint.measureText((String)localObject1));
                    localObject1 = new StaticLayout(TextUtils.ellipsize(((String)localObject1).replace('\n', ' '), this.descriptionTextPaint, Math.min(k, i), TextUtils.TruncateAt.MIDDLE), this.descriptionTextPaint, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
                    this.linkY = this.description2Y;
                    if ((this.descriptionLayout2 == null) || (this.descriptionLayout2.getLineCount() == 0))
                      continue;
                    this.linkY += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1) + AndroidUtilities.dp(1.0F);
                    this.linkLayout.add(localObject1);
                    paramInt2 += 1;
                    continue;
                    localException7 = localException7;
                    FileLog.e(localException7);
                    continue;
                    localException4 = localException4;
                    FileLog.e(localException4);
                    continue;
                    localException1 = localException1;
                    FileLog.e(localException1);
                  }
                  catch (Exception localException2)
                  {
                    FileLog.e(localException2);
                    continue;
                  }
                  i = AndroidUtilities.dp(52.0F);
                  if (!LocaleController.isRTL)
                    continue;
                  paramInt2 = View.MeasureSpec.getSize(paramInt1) - AndroidUtilities.dp(10.0F) - i;
                  this.letterDrawable.setBounds(paramInt2, AndroidUtilities.dp(10.0F), paramInt2 + i, AndroidUtilities.dp(62.0F));
                  if (j == 0)
                    continue;
                  localObject6 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, i, true);
                  localObject2 = FileLoader.getClosestPhotoSizeWithSize(this.message.photoThumbs, 80);
                  if (localObject2 != localObject6)
                    continue;
                  localObject2 = null;
                  ((TLRPC.PhotoSize)localObject6).size = -1;
                  if (localObject2 == null)
                    continue;
                  ((TLRPC.PhotoSize)localObject2).size = -1;
                  this.linkImageView.setImageCoords(paramInt2, AndroidUtilities.dp(10.0F), i, i);
                  localObject8 = FileLoader.getAttachFileName((TLObject)localObject6);
                  paramInt2 = 1;
                  if (FileLoader.getPathToAttach((TLObject)localObject6, true).exists())
                    continue;
                  paramInt2 = 0;
                  String str1 = String.format(Locale.US, "%d_%d", new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
                  if ((paramInt2 == 0) && (!MediaController.getInstance().canDownloadMedia(1)) && (!FileLoader.getInstance().isLoadingFile((String)localObject8)))
                    continue;
                  localObject8 = this.linkImageView;
                  localObject6 = ((TLRPC.PhotoSize)localObject6).location;
                  if (localObject2 == null)
                    continue;
                  localObject2 = ((TLRPC.PhotoSize)localObject2).location;
                  ((ImageReceiver)localObject8).setImage((TLObject)localObject6, str1, (TLRPC.FileLocation)localObject2, String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), 0, null, false);
                  this.drawLinkImageView = true;
                  i = 0;
                  paramInt2 = i;
                  if (this.titleLayout == null)
                    continue;
                  paramInt2 = i;
                  if (this.titleLayout.getLineCount() == 0)
                    continue;
                  paramInt2 = 0 + this.titleLayout.getLineBottom(this.titleLayout.getLineCount() - 1);
                  i = paramInt2;
                  if (this.descriptionLayout == null)
                    continue;
                  i = paramInt2;
                  if (this.descriptionLayout.getLineCount() == 0)
                    continue;
                  i = paramInt2 + this.descriptionLayout.getLineBottom(this.descriptionLayout.getLineCount() - 1);
                  paramInt2 = i;
                  if (this.descriptionLayout2 == null)
                    continue;
                  paramInt2 = i;
                  if (this.descriptionLayout2.getLineCount() == 0)
                    continue;
                  i += this.descriptionLayout2.getLineBottom(this.descriptionLayout2.getLineCount() - 1);
                  paramInt2 = i;
                  if (this.descriptionLayout == null)
                    continue;
                  paramInt2 = i + AndroidUtilities.dp(10.0F);
                  i = 0;
                  if (i >= this.linkLayout.size())
                    continue;
                  localObject2 = (StaticLayout)this.linkLayout.get(i);
                  k = paramInt2;
                  if (((StaticLayout)localObject2).getLineCount() <= 0)
                    continue;
                  k = paramInt2 + ((StaticLayout)localObject2).getLineBottom(((StaticLayout)localObject2).getLineCount() - 1);
                  i += 1;
                  paramInt2 = k;
                  continue;
                  paramInt2 = AndroidUtilities.dp(10.0F);
                  continue;
                  localObject2 = null;
                  continue;
                  if (localObject2 == null)
                    continue;
                  this.linkImageView.setImage(null, null, ((TLRPC.PhotoSize)localObject2).location, String.format(Locale.US, "%d_%d_b", new Object[] { Integer.valueOf(i), Integer.valueOf(i) }), 0, null, false);
                  continue;
                  this.linkImageView.setImageBitmap((Drawable)null);
                  continue;
                  i = paramInt2;
                  if (j == 0)
                    continue;
                  i = Math.max(AndroidUtilities.dp(48.0F), paramInt2);
                  this.checkBox.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(22.0F), 1073741824));
                  paramInt2 = View.MeasureSpec.getSize(paramInt1);
                  i = Math.max(AndroidUtilities.dp(72.0F), i + AndroidUtilities.dp(16.0F));
                  if (!this.needDivider)
                    continue;
                  paramInt1 = 1;
                  setMeasuredDimension(paramInt2, paramInt1 + i);
                  return;
                  paramInt1 = 0;
                  continue;
                }
              }
            }
            localException5 = localException5;
            continue;
          }
          catch (Exception localException6)
          {
            localObject2 = localObject8;
            continue;
            continue;
          }
        }
        label2232: break;
        label2235: localObject2 = localObject11;
        Object localObject8 = localObject10;
        continue;
        label2246: localObject2 = localObject6;
        continue;
        localObject9 = null;
        localObject6 = localObject2;
        localObject8 = localException6;
        localObject2 = localObject9;
        localObject5 = localObject6;
        localObject6 = localObject8;
      }
      j = 0;
      Object localObject2 = null;
      Object localObject5 = null;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int i2 = 0;
    int i;
    int j;
    StaticLayout localStaticLayout;
    int k;
    float f;
    if ((this.message != null) && (!this.linkLayout.isEmpty()) && (this.delegate != null) && (this.delegate.canPerformActions()))
      if ((paramMotionEvent.getAction() == 0) || ((this.linkPreviewPressed) && (paramMotionEvent.getAction() == 1)))
      {
        int m = (int)paramMotionEvent.getX();
        int n = (int)paramMotionEvent.getY();
        i = 0;
        j = 0;
        if (i >= this.linkLayout.size())
          break label506;
        localStaticLayout = (StaticLayout)this.linkLayout.get(i);
        k = j;
        if (localStaticLayout.getLineCount() > 0)
        {
          k = localStaticLayout.getLineBottom(localStaticLayout.getLineCount() - 1);
          if (LocaleController.isRTL)
          {
            f = 8.0F;
            int i1 = AndroidUtilities.dp(f);
            if ((m < i1 + localStaticLayout.getLineLeft(0)) || (m > i1 + localStaticLayout.getLineWidth(0)) || (n < this.linkY + j) || (n > this.linkY + j + k))
              break label452;
            if (paramMotionEvent.getAction() != 0)
              break label315;
            resetPressedLink();
            this.pressedLink = i;
            this.linkPreviewPressed = true;
          }
        }
      }
    while (true)
    {
      try
      {
        this.urlPath.setCurrentLayout(localStaticLayout, 0, 0.0F);
        localStaticLayout.getSelectionPath(0, localStaticLayout.getText().length(), this.urlPath);
        j = 1;
        i = 1;
        k = i;
        if (j != 0)
          continue;
        resetPressedLink();
        k = i;
        if ((k == 0) && (!super.onTouchEvent(paramMotionEvent)))
          continue;
        i2 = 1;
        return i2;
        f = AndroidUtilities.leftBaseline;
      }
      catch (Exception localException1)
      {
        FileLog.e(localException1);
        continue;
      }
      label315: if (this.linkPreviewPressed)
      {
        try
        {
          TLRPC.WebPage localWebPage;
          if ((this.pressedLink == 0) && (this.message.messageOwner.media != null))
          {
            localWebPage = this.message.messageOwner.media.webpage;
            label357: if ((localWebPage == null) || (Build.VERSION.SDK_INT < 16) || (localWebPage.embed_url == null) || (localWebPage.embed_url.length() == 0))
              break label418;
            this.delegate.needOpenWebView(localWebPage);
          }
          while (true)
          {
            resetPressedLink();
            j = 1;
            i = 1;
            break;
            localWebPage = null;
            break label357;
            Browser.openUrl(getContext(), (String)this.links.get(this.pressedLink));
          }
        }
        catch (Exception localException2)
        {
          while (true)
            FileLog.e(localException2);
        }
        label452: k = j + k;
        i += 1;
        j = k;
        break;
        if (paramMotionEvent.getAction() == 3)
        {
          resetPressedLink();
          k = 0;
          continue;
          resetPressedLink();
        }
        k = 0;
        continue;
      }
      label418: j = 1;
      i = 0;
      continue;
      label506: j = 0;
      i = 0;
    }
  }

  protected void resetPressedLink()
  {
    this.pressedLink = -1;
    this.linkPreviewPressed = false;
    invalidate();
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox.getVisibility() != 0)
      this.checkBox.setVisibility(0);
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setDelegate(SharedLinkCellDelegate paramSharedLinkCellDelegate)
  {
    this.delegate = paramSharedLinkCellDelegate;
  }

  public void setLink(MessageObject paramMessageObject, boolean paramBoolean)
  {
    this.needDivider = paramBoolean;
    resetPressedLink();
    this.message = paramMessageObject;
    requestLayout();
  }

  public static abstract interface SharedLinkCellDelegate
  {
    public abstract boolean canPerformActions();

    public abstract void needOpenWebView(TLRPC.WebPage paramWebPage);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.SharedLinkCell
 * JD-Core Version:    0.6.0
 */