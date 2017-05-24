package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View.MeasureSpec;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_messageActionUserUpdatedPhoto;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.PhotoViewer;

public class ChatActionCell extends BaseCell
{
  private AvatarDrawable avatarDrawable;
  private MessageObject currentMessageObject;
  private int customDate;
  private CharSequence customText;
  private ChatActionCellDelegate delegate;
  private boolean hasReplyMessage;
  private boolean imagePressed = false;
  private ImageReceiver imageReceiver = new ImageReceiver(this);
  private URLSpan pressedLink;
  private int previousWidth = 0;
  private int textHeight = 0;
  private StaticLayout textLayout;
  private int textWidth = 0;
  private int textX = 0;
  private int textXLeft = 0;
  private int textY = 0;

  public ChatActionCell(Context paramContext)
  {
    super(paramContext);
    this.imageReceiver.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable = new AvatarDrawable();
  }

  private void createLayout(CharSequence paramCharSequence, int paramInt)
  {
    int i = 0;
    int j = paramInt - AndroidUtilities.dp(30.0F);
    this.textLayout = new StaticLayout(paramCharSequence, Theme.chat_actionTextPaint, j, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
    this.textHeight = 0;
    this.textWidth = 0;
    try
    {
      int k = this.textLayout.getLineCount();
      while (true)
        if (i < k)
          try
          {
            float f2 = this.textLayout.getLineWidth(i);
            float f1 = f2;
            if (f2 > j)
              f1 = j;
            this.textHeight = (int)Math.max(this.textHeight, Math.ceil(this.textLayout.getLineBottom(i)));
            this.textWidth = (int)Math.max(this.textWidth, Math.ceil(f1));
            i += 1;
          }
          catch (java.lang.Exception paramCharSequence)
          {
            FileLog.e(paramCharSequence);
            return;
          }
    }
    catch (java.lang.Exception paramCharSequence)
    {
      FileLog.e(paramCharSequence);
      this.textX = ((paramInt - this.textWidth) / 2);
      this.textY = AndroidUtilities.dp(7.0F);
      this.textXLeft = ((paramInt - this.textLayout.getWidth()) / 2);
    }
  }

  private int findMaxWidthAroundLine(int paramInt)
  {
    int i = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
    int k = this.textLayout.getLineCount();
    int j = paramInt + 1;
    while (j < k)
    {
      int m = (int)Math.ceil(this.textLayout.getLineWidth(j));
      if (Math.abs(m - i) >= AndroidUtilities.dp(12.0F))
        break;
      i = Math.max(m, i);
      j += 1;
    }
    paramInt -= 1;
    while (paramInt >= 0)
    {
      j = (int)Math.ceil(this.textLayout.getLineWidth(paramInt));
      if (Math.abs(j - i) >= AndroidUtilities.dp(12.0F))
        break;
      i = Math.max(j, i);
      paramInt -= 1;
    }
    return i;
  }

  public int getCustomDate()
  {
    return this.customDate;
  }

  public MessageObject getMessageObject()
  {
    return this.currentMessageObject;
  }

  public ImageReceiver getPhotoImage()
  {
    return this.imageReceiver;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11))
      this.imageReceiver.draw(paramCanvas);
    int i22;
    int i23;
    int k;
    int i14;
    int i;
    int m;
    int i24;
    int i25;
    int i16;
    int i15;
    int i17;
    int i1;
    label136: label143: int i9;
    if (this.textLayout != null)
    {
      i22 = this.textLayout.getLineCount();
      i23 = AndroidUtilities.dp(6.0F);
      k = AndroidUtilities.dp(7.0F);
      i14 = 0;
      i = 0;
      if (i14 < i22)
      {
        m = findMaxWidthAroundLine(i14);
        i24 = (getMeasuredWidth() - m) / 2 - AndroidUtilities.dp(3.0F);
        i25 = m + AndroidUtilities.dp(6.0F);
        i16 = this.textLayout.getLineBottom(i14);
        i15 = i16 - i;
        i17 = 0;
        if (i14 == i22 - 1)
        {
          i1 = 1;
          if (i14 != 0)
            break label750;
          i = 1;
          m = i15;
          i9 = k;
          if (i != 0)
          {
            i9 = k - AndroidUtilities.dp(3.0F);
            m = i15 + AndroidUtilities.dp(3.0F);
          }
          if (i1 == 0)
            break label1110;
          m += AndroidUtilities.dp(3.0F);
        }
      }
    }
    label1110: 
    while (true)
    {
      paramCanvas.drawRect(i24, i9, i24 + i25, i9 + m, Theme.chat_actionBackgroundPaint);
      i15 = i1;
      k = i17;
      if (i1 == 0)
      {
        i15 = i1;
        k = i17;
        if (i14 + 1 < i22)
        {
          i17 = findMaxWidthAroundLine(i14 + 1) + AndroidUtilities.dp(6.0F);
          if (i23 * 2 + i17 >= i25)
            break label755;
          i1 = (getMeasuredWidth() - i17) / 2;
          i15 = 1;
          k = AndroidUtilities.dp(3.0F);
          paramCanvas.drawRect(i24, i9 + m, i1, i9 + m + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
          paramCanvas.drawRect(i1 + i17, i9 + m, i24 + i25, i9 + m + AndroidUtilities.dp(3.0F), Theme.chat_actionBackgroundPaint);
        }
      }
      label357: int i11;
      int i3;
      if ((i == 0) && (i14 > 0))
      {
        int i2 = findMaxWidthAroundLine(i14 - 1) + AndroidUtilities.dp(6.0F);
        if (i23 * 2 + i2 < i25)
        {
          i17 = (getMeasuredWidth() - i2) / 2;
          i = i9 - AndroidUtilities.dp(3.0F);
          int i10 = AndroidUtilities.dp(3.0F);
          paramCanvas.drawRect(i24, i, i17, AndroidUtilities.dp(3.0F) + i, Theme.chat_actionBackgroundPaint);
          paramCanvas.drawRect(i17 + i2, i, i24 + i25, AndroidUtilities.dp(3.0F) + i, Theme.chat_actionBackgroundPaint);
          i11 = m + i10;
          i3 = 1;
          m = i;
          i = i11;
        }
      }
      while (true)
      {
        paramCanvas.drawRect(i24 - i23, m + i23, i24, m + i + k - i23, Theme.chat_actionBackgroundPaint);
        paramCanvas.drawRect(i24 + i25, m + i23, i24 + i25 + i23, m + i + k - i23, Theme.chat_actionBackgroundPaint);
        if (i3 != 0)
        {
          i3 = i24 - i23;
          Theme.chat_cornerOuter[0].setBounds(i3, m, i3 + i23, m + i23);
          Theme.chat_cornerOuter[0].draw(paramCanvas);
          int i4 = i24 + i25;
          Theme.chat_cornerOuter[1].setBounds(i4, m, i4 + i23, m + i23);
          Theme.chat_cornerOuter[1].draw(paramCanvas);
        }
        if (i15 != 0)
        {
          k = m + i + k - i23;
          int i5 = i24 + i25;
          Theme.chat_cornerOuter[2].setBounds(i5, k, i5 + i23, k + i23);
          Theme.chat_cornerOuter[2].draw(paramCanvas);
          int i6 = i24 - i23;
          Theme.chat_cornerOuter[3].setBounds(i6, k, i6 + i23, k + i23);
          Theme.chat_cornerOuter[3].draw(paramCanvas);
        }
        k = m + i;
        i14 += 1;
        i = i16;
        break;
        int i7 = 0;
        break label136;
        label750: int j = 0;
        break label143;
        label755: if (i23 * 2 + i25 < i17)
        {
          k = AndroidUtilities.dp(3.0F);
          i15 = i11 + m - AndroidUtilities.dp(9.0F);
          int i18 = i24 - i23 * 2;
          Theme.chat_cornerInner[2].setBounds(i18, i15, i18 + i23, i15 + i23);
          Theme.chat_cornerInner[2].draw(paramCanvas);
          int i19 = i24 + i25 + i23;
          Theme.chat_cornerInner[3].setBounds(i19, i15, i19 + i23, i15 + i23);
          Theme.chat_cornerInner[3].draw(paramCanvas);
          i15 = i7;
          break label357;
        }
        k = AndroidUtilities.dp(6.0F);
        i15 = i7;
        break label357;
        int i12;
        if (i23 * 2 + i25 < i7)
        {
          i11 -= AndroidUtilities.dp(3.0F);
          i7 = AndroidUtilities.dp(3.0F);
          int i20 = i12 + i23;
          int i26 = i24 - i23 * 2;
          Theme.chat_cornerInner[0].setBounds(i26, i20, i26 + i23, i20 + i23);
          Theme.chat_cornerInner[0].draw(paramCanvas);
          int i27 = i24 + i25 + i23;
          Theme.chat_cornerInner[1].setBounds(i27, i20, i27 + i23, i20 + i23);
          Theme.chat_cornerInner[1].draw(paramCanvas);
          m += i7;
          i7 = j;
          j = n;
          n = i12;
          continue;
        }
        int i21 = AndroidUtilities.dp(6.0F);
        int i8 = AndroidUtilities.dp(6.0F);
        int i13;
        i12 -= i21;
        n += i8;
        i8 = j;
        j = n;
        int n = i13;
        continue;
        paramCanvas.save();
        paramCanvas.translate(this.textXLeft, this.textY);
        this.textLayout.draw(paramCanvas);
        paramCanvas.restore();
        return;
        i8 = j;
        j = n;
        n = i13;
      }
    }
  }

  protected void onLongPress()
  {
    if (this.delegate != null)
      this.delegate.didLongPressed(this);
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if ((this.currentMessageObject == null) && (this.customText == null))
    {
      setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), this.textHeight + AndroidUtilities.dp(14.0F));
      return;
    }
    paramInt2 = Math.max(AndroidUtilities.dp(30.0F), View.MeasureSpec.getSize(paramInt1));
    CharSequence localCharSequence;
    if (paramInt2 != this.previousWidth)
    {
      if (this.currentMessageObject == null)
        break label180;
      localCharSequence = this.currentMessageObject.messageText;
      this.previousWidth = paramInt2;
      createLayout(localCharSequence, paramInt2);
      if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11))
        this.imageReceiver.setImageCoords((paramInt2 - AndroidUtilities.dp(64.0F)) / 2, this.textHeight + AndroidUtilities.dp(15.0F), AndroidUtilities.dp(64.0F), AndroidUtilities.dp(64.0F));
    }
    int i = this.textHeight;
    if ((this.currentMessageObject != null) && (this.currentMessageObject.type == 11));
    for (paramInt1 = 70; ; paramInt1 = 0)
    {
      setMeasuredDimension(paramInt2, AndroidUtilities.dp(paramInt1 + 14) + i);
      return;
      label180: localCharSequence = this.customText;
      break;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.currentMessageObject == null)
    {
      bool2 = super.onTouchEvent(paramMotionEvent);
      return bool2;
    }
    float f3 = paramMotionEvent.getX();
    float f1 = paramMotionEvent.getY();
    if (paramMotionEvent.getAction() == 0)
    {
      if (this.delegate == null)
        break label431;
      if ((this.currentMessageObject.type != 11) || (!this.imageReceiver.isInsideImage(f3, f1)))
        break label542;
      this.imagePressed = true;
    }
    label518: label523: label526: label534: label542: for (boolean bool2 = true; ; bool2 = false)
    {
      boolean bool1 = bool2;
      if (bool2)
      {
        startCheckLongPress();
        bool1 = bool2;
      }
      Object localObject;
      while (true)
      {
        if ((bool1) || ((paramMotionEvent.getAction() != 0) && ((this.pressedLink == null) || (paramMotionEvent.getAction() != 1))))
          break label523;
        if ((f3 < this.textX) || (f1 < this.textY) || (f3 > this.textX + this.textWidth) || (f1 > this.textY + this.textHeight))
          break label534;
        float f2 = this.textY;
        f3 -= this.textXLeft;
        int i = this.textLayout.getLineForVertical((int)(f1 - f2));
        int j = this.textLayout.getOffsetForHorizontal(i, f3);
        f1 = this.textLayout.getLineLeft(i);
        if ((f1 > f3) || (this.textLayout.getLineWidth(i) + f1 < f3) || (!(this.currentMessageObject.messageText instanceof Spannable)))
          break label526;
        localObject = (URLSpan[])((Spannable)this.currentMessageObject.messageText).getSpans(j, j, URLSpan.class);
        if (localObject.length == 0)
          break label518;
        if (paramMotionEvent.getAction() != 0)
          break label437;
        this.pressedLink = localObject[0];
        bool1 = true;
        bool2 = bool1;
        if (bool1)
          break;
        return super.onTouchEvent(paramMotionEvent);
        if (paramMotionEvent.getAction() != 2)
          cancelCheckLongPress();
        if (this.imagePressed)
          if (paramMotionEvent.getAction() == 1)
          {
            this.imagePressed = false;
            if (this.delegate != null)
            {
              this.delegate.didClickedImage(this);
              playSoundEffect(0);
              bool1 = false;
              continue;
            }
          }
          else
          {
            if (paramMotionEvent.getAction() == 3)
            {
              this.imagePressed = false;
              bool1 = false;
              continue;
            }
            if ((paramMotionEvent.getAction() == 2) && (!this.imageReceiver.isInsideImage(f3, f1)))
              this.imagePressed = false;
          }
        label431: bool1 = false;
      }
      label437: if (localObject[0] == this.pressedLink)
      {
        if (this.delegate != null)
        {
          localObject = localObject[0].getURL();
          if (!((String)localObject).startsWith("game"))
            break label501;
          this.delegate.didPressedReplyMessage(this, this.currentMessageObject.messageOwner.reply_to_msg_id);
        }
        while (true)
        {
          bool1 = true;
          break;
          label501: this.delegate.needOpenUserProfile(Integer.parseInt((String)localObject));
        }
        this.pressedLink = null;
      }
      while (true)
      {
        break;
        this.pressedLink = null;
        continue;
        this.pressedLink = null;
      }
    }
  }

  public void setCustomDate(int paramInt)
  {
    if (this.customDate == paramInt);
    String str;
    do
    {
      return;
      str = LocaleController.formatDateChat(paramInt);
    }
    while ((this.customText != null) && (TextUtils.equals(str, this.customText)));
    this.previousWidth = 0;
    this.customDate = paramInt;
    this.customText = str;
    if (getMeasuredWidth() != 0)
    {
      createLayout(this.customText, getMeasuredWidth());
      invalidate();
    }
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        ChatActionCell.this.requestLayout();
      }
    });
  }

  public void setDelegate(ChatActionCellDelegate paramChatActionCellDelegate)
  {
    this.delegate = paramChatActionCellDelegate;
  }

  public void setMessageObject(MessageObject paramMessageObject)
  {
    boolean bool2 = true;
    if ((this.currentMessageObject == paramMessageObject) && ((this.hasReplyMessage) || (paramMessageObject.replyMessageObject == null)))
      return;
    this.currentMessageObject = paramMessageObject;
    boolean bool1;
    int i;
    if (paramMessageObject.replyMessageObject != null)
    {
      bool1 = true;
      this.hasReplyMessage = bool1;
      this.previousWidth = 0;
      if (this.currentMessageObject.type != 11)
        break label318;
      if (paramMessageObject.messageOwner.to_id == null)
        break label332;
      if (paramMessageObject.messageOwner.to_id.chat_id == 0)
        break label197;
      i = paramMessageObject.messageOwner.to_id.chat_id;
    }
    while (true)
    {
      label98: this.avatarDrawable.setInfo(i, null, null, false);
      if ((this.currentMessageObject.messageOwner.action instanceof TLRPC.TL_messageActionUserUpdatedPhoto))
      {
        this.imageReceiver.setImage(this.currentMessageObject.messageOwner.action.newUserPhoto.photo_small, "50_50", this.avatarDrawable, null, false);
        label157: paramMessageObject = this.imageReceiver;
        if (PhotoViewer.getInstance().isShowingImage(this.currentMessageObject))
          break label312;
        bool1 = bool2;
        label179: paramMessageObject.setVisible(bool1, false);
      }
      while (true)
      {
        requestLayout();
        return;
        bool1 = false;
        break;
        label197: if (paramMessageObject.messageOwner.to_id.channel_id != 0)
        {
          i = paramMessageObject.messageOwner.to_id.channel_id;
          break label98;
        }
        int j = paramMessageObject.messageOwner.to_id.user_id;
        i = j;
        if (j != UserConfig.getClientUserId())
          break label98;
        i = paramMessageObject.messageOwner.from_id;
        break label98;
        paramMessageObject = FileLoader.getClosestPhotoSizeWithSize(this.currentMessageObject.photoThumbs, AndroidUtilities.dp(64.0F));
        if (paramMessageObject != null)
        {
          this.imageReceiver.setImage(paramMessageObject.location, "50_50", this.avatarDrawable, null, false);
          break label157;
        }
        this.imageReceiver.setImageBitmap(this.avatarDrawable);
        break label157;
        label312: bool1 = false;
        break label179;
        label318: this.imageReceiver.setImageBitmap((Bitmap)null);
      }
      label332: i = 0;
    }
  }

  public static abstract interface ChatActionCellDelegate
  {
    public abstract void didClickedImage(ChatActionCell paramChatActionCell);

    public abstract void didLongPressed(ChatActionCell paramChatActionCell);

    public abstract void didPressedBotButton(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton);

    public abstract void didPressedReplyMessage(ChatActionCell paramChatActionCell, int paramInt);

    public abstract void needOpenUserProfile(int paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ChatActionCell
 * JD-Core Version:    0.6.0
 */