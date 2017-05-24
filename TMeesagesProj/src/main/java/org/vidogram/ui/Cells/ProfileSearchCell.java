package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ChatObject;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;

public class ProfileSearchCell extends BaseCell
{
  private AvatarDrawable avatarDrawable;
  private ImageReceiver avatarImage = new ImageReceiver(this);
  private TLRPC.Chat chat = null;
  private StaticLayout countLayout;
  private int countLeft;
  private int countTop = AndroidUtilities.dp(25.0F);
  private int countWidth;
  private CharSequence currentName;
  private long dialog_id;
  private boolean drawCheck;
  private boolean drawCount;
  private boolean drawNameBot;
  private boolean drawNameBroadcast;
  private boolean drawNameGroup;
  private boolean drawNameLock;
  private TLRPC.EncryptedChat encryptedChat = null;
  private TLRPC.FileLocation lastAvatar = null;
  private String lastName = null;
  private int lastStatus = 0;
  private int lastUnreadCount;
  private StaticLayout nameLayout;
  private int nameLeft;
  private int nameLockLeft;
  private int nameLockTop;
  private int nameTop;
  private StaticLayout onlineLayout;
  private int onlineLeft;
  private int paddingRight;
  private RectF rect = new RectF();
  private CharSequence subLabel;
  public boolean useSeparator = false;
  private TLRPC.User user = null;

  public ProfileSearchCell(Context paramContext)
  {
    super(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(26.0F));
    this.avatarDrawable = new AvatarDrawable();
  }

  public void buildLayout()
  {
    this.drawNameBroadcast = false;
    this.drawNameLock = false;
    this.drawNameGroup = false;
    this.drawCheck = false;
    this.drawNameBot = false;
    Object localObject1;
    if (this.encryptedChat != null)
    {
      this.drawNameLock = true;
      this.dialog_id = (this.encryptedChat.id << 32);
      if (!LocaleController.isRTL)
      {
        this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
        this.nameLockTop = AndroidUtilities.dp(16.5F);
        if (this.currentName == null)
          break label1301;
        localObject1 = this.currentName;
        if (((CharSequence)localObject1).length() != 0)
          break label1888;
        if ((this.user == null) || (this.user.phone == null) || (this.user.phone.length() == 0))
          break label1355;
        localObject1 = b.a().e("+" + this.user.phone);
      }
    }
    label184: label196: label223: label1888: 
    while (true)
    {
      Object localObject2;
      int j;
      int k;
      int i;
      label247: Object localObject3;
      label509: float f;
      label614: label631: double d1;
      if (this.encryptedChat != null)
      {
        localObject2 = Theme.dialogs_nameEncryptedPaint;
        if (LocaleController.isRTL)
          break label1377;
        j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(14.0F);
        k = j;
        if (!this.drawNameLock)
          break label1403;
        i = j - (AndroidUtilities.dp(6.0F) + Theme.dialogs_lockDrawable.getIntrinsicWidth());
        i -= this.paddingRight;
        k -= this.paddingRight;
        if (!this.drawCount)
          break label1525;
        localObject3 = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
        if ((localObject3 == null) || (((TLRPC.TL_dialog)localObject3).unread_count == 0))
          break label1512;
        this.lastUnreadCount = ((TLRPC.TL_dialog)localObject3).unread_count;
        localObject3 = String.format("%d", new Object[] { Integer.valueOf(((TLRPC.TL_dialog)localObject3).unread_count) });
        this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(Theme.dialogs_countTextPaint.measureText((String)localObject3)));
        this.countLayout = new StaticLayout((CharSequence)localObject3, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
        j = this.countWidth;
        j = AndroidUtilities.dp(18.0F) + j;
        i -= j;
        if (LocaleController.isRTL)
          break label1488;
        this.countLeft = (getMeasuredWidth() - this.countWidth - AndroidUtilities.dp(19.0F));
        this.nameLayout = new StaticLayout(TextUtils.ellipsize((CharSequence)localObject1, (TextPaint)localObject2, i - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject2, i, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        if ((this.chat != null) && (this.subLabel == null))
          break label1691;
        if (LocaleController.isRTL)
          break label1538;
        this.onlineLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        localObject1 = "";
        localObject3 = Theme.dialogs_offlinePaint;
        if (this.subLabel == null)
          break label1551;
        localObject1 = this.subLabel;
        localObject2 = localObject3;
        this.onlineLayout = new StaticLayout(TextUtils.ellipsize((CharSequence)localObject1, (TextPaint)localObject2, k - AndroidUtilities.dp(12.0F), TextUtils.TruncateAt.END), (TextPaint)localObject2, k, Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
        this.nameTop = AndroidUtilities.dp(13.0F);
        if ((this.subLabel != null) && (this.chat != null))
          this.nameLockTop -= AndroidUtilities.dp(12.0F);
        if (LocaleController.isRTL)
          break label1716;
        if (!AndroidUtilities.isTablet())
          break label1708;
        f = 13.0F;
        j = AndroidUtilities.dp(f);
        this.avatarImage.setImageCoords(j, AndroidUtilities.dp(10.0F), AndroidUtilities.dp(52.0F), AndroidUtilities.dp(52.0F));
        if (!LocaleController.isRTL)
          break label1754;
        double d2;
        if ((this.nameLayout.getLineCount() > 0) && (this.nameLayout.getLineLeft(0) == 0.0F))
        {
          d1 = Math.ceil(this.nameLayout.getLineWidth(0));
          if (d1 < i)
          {
            d2 = this.nameLeft;
            this.nameLeft = (int)(i - d1 + d2);
          }
        }
        if ((this.onlineLayout != null) && (this.onlineLayout.getLineCount() > 0) && (this.onlineLayout.getLineLeft(0) == 0.0F))
        {
          d1 = Math.ceil(this.onlineLayout.getLineWidth(0));
          if (d1 < k)
          {
            d2 = this.onlineLeft;
            this.onlineLeft = (int)(k - d1 + d2);
          }
        }
      }
      while (true)
      {
        if (LocaleController.isRTL)
        {
          this.nameLeft += this.paddingRight;
          this.onlineLeft += this.paddingRight;
        }
        return;
        this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2) - Theme.dialogs_lockDrawable.getIntrinsicWidth());
        this.nameLeft = AndroidUtilities.dp(11.0F);
        break;
        if (this.chat != null)
        {
          if (this.chat.id < 0)
          {
            this.dialog_id = AndroidUtilities.makeBroadcastId(this.chat.id);
            this.drawNameBroadcast = true;
            this.nameLockTop = AndroidUtilities.dp(28.5F);
            this.drawCheck = this.chat.verified;
            if (LocaleController.isRTL)
              break label1065;
            this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
            j = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4);
            if (!this.drawNameGroup)
              break label1054;
          }
          for (i = Theme.dialogs_groupDrawable.getIntrinsicWidth(); ; i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth())
          {
            this.nameLeft = (i + j);
            break;
            this.dialog_id = (-this.chat.id);
            if ((ChatObject.isChannel(this.chat)) && (!this.chat.megagroup))
            {
              this.drawNameBroadcast = true;
              this.nameLockTop = AndroidUtilities.dp(28.5F);
              break label919;
            }
            this.drawNameGroup = true;
            this.nameLockTop = AndroidUtilities.dp(30.0F);
            break label919;
          }
          j = getMeasuredWidth();
          k = AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2);
          if (this.drawNameGroup);
          for (i = Theme.dialogs_groupDrawable.getIntrinsicWidth(); ; i = Theme.dialogs_broadcastDrawable.getIntrinsicWidth())
          {
            this.nameLockLeft = (j - k - i);
            this.nameLeft = AndroidUtilities.dp(11.0F);
            break;
          }
        }
        this.dialog_id = this.user.id;
        if (!LocaleController.isRTL)
        {
          this.nameLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          if (!this.user.bot)
            break label1288;
          this.drawNameBot = true;
          if (LocaleController.isRTL)
            break label1250;
          this.nameLockLeft = AndroidUtilities.dp(AndroidUtilities.leftBaseline);
          this.nameLeft = (AndroidUtilities.dp(AndroidUtilities.leftBaseline + 4) + Theme.dialogs_botDrawable.getIntrinsicWidth());
        }
        for (this.nameLockTop = AndroidUtilities.dp(16.5F); ; this.nameLockTop = AndroidUtilities.dp(17.0F))
        {
          this.drawCheck = this.user.verified;
          break;
          this.nameLeft = AndroidUtilities.dp(11.0F);
          break label1162;
          label1250: this.nameLockLeft = (getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline + 2) - Theme.dialogs_botDrawable.getIntrinsicWidth());
          this.nameLeft = AndroidUtilities.dp(11.0F);
          break label1214;
        }
        localObject1 = "";
        if (this.chat != null)
          localObject1 = this.chat.title;
        while (true)
        {
          localObject1 = ((String)localObject1).replace('\n', ' ');
          break;
          if (this.user == null)
            continue;
          localObject1 = UserObject.getUserName(this.user);
        }
        localObject1 = LocaleController.getString("HiddenName", 2131165809);
        break label184;
        localObject2 = Theme.dialogs_namePaint;
        break label196;
        label1377: j = getMeasuredWidth() - this.nameLeft - AndroidUtilities.dp(AndroidUtilities.leftBaseline);
        k = j;
        break label223;
        label1403: if (this.drawNameBroadcast)
        {
          i = j - (AndroidUtilities.dp(6.0F) + Theme.dialogs_broadcastDrawable.getIntrinsicWidth());
          break label247;
        }
        if (this.drawNameGroup)
        {
          i = j - (AndroidUtilities.dp(6.0F) + Theme.dialogs_groupDrawable.getIntrinsicWidth());
          break label247;
        }
        i = j;
        if (!this.drawNameBot)
          break label247;
        i = j - (AndroidUtilities.dp(6.0F) + Theme.dialogs_botDrawable.getIntrinsicWidth());
        break label247;
        this.countLeft = AndroidUtilities.dp(19.0F);
        this.nameLeft = (j + this.nameLeft);
        break label438;
        label1512: this.lastUnreadCount = 0;
        this.countLayout = null;
        break label438;
        label1525: this.lastUnreadCount = 0;
        this.countLayout = null;
        break label438;
        this.onlineLeft = AndroidUtilities.dp(11.0F);
        break label509;
        localObject2 = localObject3;
        if (this.user == null)
          break label536;
        if (this.user.bot)
        {
          localObject1 = LocaleController.getString("Bot", 2131165390);
          localObject2 = localObject3;
          break label536;
        }
        String str = LocaleController.formatUserStatus(this.user);
        localObject1 = str;
        localObject2 = localObject3;
        if (this.user == null)
          break label536;
        if (this.user.id != UserConfig.getClientUserId())
        {
          localObject1 = str;
          localObject2 = localObject3;
          if (this.user.status == null)
            break label536;
          localObject1 = str;
          localObject2 = localObject3;
          if (this.user.status.expires <= ConnectionsManager.getInstance().getCurrentTime())
            break label536;
        }
        localObject2 = Theme.dialogs_onlinePaint;
        localObject1 = LocaleController.getString("Online", 2131166155);
        break label536;
        this.onlineLayout = null;
        this.nameTop = AndroidUtilities.dp(25.0F);
        break label614;
        f = 9.0F;
        break label631;
        j = getMeasuredWidth();
        if (AndroidUtilities.isTablet())
          f = 65.0F;
        while (true)
        {
          j -= AndroidUtilities.dp(f);
          break;
          f = 61.0F;
        }
        if ((this.nameLayout.getLineCount() > 0) && (this.nameLayout.getLineRight(0) == i))
        {
          d1 = Math.ceil(this.nameLayout.getLineWidth(0));
          if (d1 < i)
            this.nameLeft = (int)(this.nameLeft - (i - d1));
        }
        if ((this.onlineLayout == null) || (this.onlineLayout.getLineCount() <= 0) || (this.onlineLayout.getLineRight(0) != k))
          continue;
        d1 = Math.ceil(this.onlineLayout.getLineWidth(0));
        if (d1 >= k)
          continue;
        this.onlineLeft = (int)(this.onlineLeft - (k - d1));
      }
    }
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
    if ((this.user == null) && (this.chat == null) && (this.encryptedChat == null))
      return;
    label99: RectF localRectF;
    label170: float f1;
    float f2;
    if (this.useSeparator)
    {
      if (LocaleController.isRTL)
        paramCanvas.drawLine(0.0F, getMeasuredHeight() - 1, getMeasuredWidth() - AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, Theme.dividerPaint);
    }
    else
    {
      if (!this.drawNameLock)
        break label447;
      setDrawableBounds(Theme.dialogs_lockDrawable, this.nameLockLeft, this.nameLockTop);
      Theme.dialogs_lockDrawable.draw(paramCanvas);
      int i;
      if (this.nameLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.nameLeft, this.nameTop);
        this.nameLayout.draw(paramCanvas);
        paramCanvas.restore();
        if (this.drawCheck)
        {
          if (!LocaleController.isRTL)
            break label543;
          i = this.nameLeft - AndroidUtilities.dp(4.0F) - Theme.dialogs_checkDrawable.getIntrinsicWidth();
          setDrawableBounds(Theme.dialogs_verifiedDrawable, i, this.nameLockTop);
          setDrawableBounds(Theme.dialogs_verifiedCheckDrawable, i, this.nameLockTop);
          Theme.dialogs_verifiedDrawable.draw(paramCanvas);
          Theme.dialogs_verifiedCheckDrawable.draw(paramCanvas);
        }
      }
      if (this.onlineLayout != null)
      {
        paramCanvas.save();
        paramCanvas.translate(this.onlineLeft, AndroidUtilities.dp(40.0F));
        this.onlineLayout.draw(paramCanvas);
        paramCanvas.restore();
      }
      if (this.countLayout != null)
      {
        i = this.countLeft - AndroidUtilities.dp(5.5F);
        this.rect.set(i, this.countTop, i + this.countWidth + AndroidUtilities.dp(11.0F), this.countTop + AndroidUtilities.dp(23.0F));
        localRectF = this.rect;
        f1 = AndroidUtilities.density;
        f2 = AndroidUtilities.density;
        if (!MessagesController.getInstance().isDialogMuted(this.dialog_id))
          break label569;
      }
    }
    label543: label569: for (Paint localPaint = Theme.dialogs_countGrayPaint; ; localPaint = Theme.dialogs_countPaint)
    {
      paramCanvas.drawRoundRect(localRectF, 11.5F * f1, 11.5F * f2, localPaint);
      paramCanvas.save();
      paramCanvas.translate(this.countLeft, this.countTop + AndroidUtilities.dp(4.0F));
      this.countLayout.draw(paramCanvas);
      paramCanvas.restore();
      this.avatarImage.draw(paramCanvas);
      return;
      paramCanvas.drawLine(AndroidUtilities.dp(AndroidUtilities.leftBaseline), getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
      break;
      label447: if (this.drawNameGroup)
      {
        setDrawableBounds(Theme.dialogs_groupDrawable, this.nameLockLeft, this.nameLockTop);
        Theme.dialogs_groupDrawable.draw(paramCanvas);
        break label99;
      }
      if (this.drawNameBroadcast)
      {
        setDrawableBounds(Theme.dialogs_broadcastDrawable, this.nameLockLeft, this.nameLockTop);
        Theme.dialogs_broadcastDrawable.draw(paramCanvas);
        break label99;
      }
      if (!this.drawNameBot)
        break label99;
      setDrawableBounds(Theme.dialogs_botDrawable, this.nameLockLeft, this.nameLockTop);
      Theme.dialogs_botDrawable.draw(paramCanvas);
      break label99;
      int j = this.nameLeft + (int)this.nameLayout.getLineWidth(0) + AndroidUtilities.dp(4.0F);
      break label170;
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    if ((this.user == null) && (this.chat == null) && (this.encryptedChat == null))
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    do
      return;
    while (!paramBoolean);
    buildLayout();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(72.0F));
  }

  public void setData(TLObject paramTLObject, TLRPC.EncryptedChat paramEncryptedChat, CharSequence paramCharSequence1, CharSequence paramCharSequence2, boolean paramBoolean)
  {
    this.currentName = paramCharSequence1;
    if ((paramTLObject instanceof TLRPC.User))
    {
      this.user = ((TLRPC.User)paramTLObject);
      this.chat = null;
    }
    while (true)
    {
      this.encryptedChat = paramEncryptedChat;
      this.subLabel = paramCharSequence2;
      this.drawCount = paramBoolean;
      update(0);
      return;
      if (!(paramTLObject instanceof TLRPC.Chat))
        continue;
      this.chat = ((TLRPC.Chat)paramTLObject);
      this.user = null;
    }
  }

  public void setPaddingRight(int paramInt)
  {
    this.paddingRight = paramInt;
  }

  public void update(int paramInt)
  {
    if (this.user != null)
      if (this.user.photo == null)
        break label594;
    label140: label185: label572: label577: label583: label588: label594: for (TLRPC.FileLocation localFileLocation = this.user.photo.photo_small; ; localFileLocation = null)
    {
      this.avatarDrawable.setInfo(this.user);
      int i;
      int k;
      Object localObject;
      if (paramInt != 0)
      {
        if ((((paramInt & 0x2) == 0) || (this.user == null)) && (((paramInt & 0x8) == 0) || (this.chat == null) || (((this.lastAvatar == null) || (localFileLocation != null)) && ((this.lastAvatar != null) || (localFileLocation == null) || (this.lastAvatar == null) || (localFileLocation == null) || ((this.lastAvatar.volume_id == localFileLocation.volume_id) && (this.lastAvatar.local_id == localFileLocation.local_id))))))
          break label583;
        i = 1;
        int j = i;
        if (i == 0)
        {
          j = i;
          if ((paramInt & 0x4) != 0)
          {
            j = i;
            if (this.user != null)
            {
              if (this.user.status == null)
                break label577;
              k = this.user.status.expires;
              j = i;
              if (k != this.lastStatus)
                j = 1;
            }
          }
        }
        if ((j != 0) || ((paramInt & 0x1) == 0) || (this.user == null))
        {
          i = j;
          if ((paramInt & 0x10) != 0)
          {
            i = j;
            if (this.chat == null);
          }
        }
        else
        {
          if (this.user == null)
            break label412;
          localObject = this.user.first_name + this.user.last_name;
          i = j;
          if (!((String)localObject).equals(this.lastName))
            i = 1;
        }
        if ((i != 0) || (!this.drawCount) || ((paramInt & 0x100) == 0))
          break label572;
        localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
        if ((localObject == null) || (((TLRPC.TL_dialog)localObject).unread_count == this.lastUnreadCount))
          break label572;
        paramInt = 1;
        label347: if (paramInt == 0)
        {
          return;
          if (this.chat != null)
            if (this.chat.photo == null)
              break label588;
        }
      }
      for (localFileLocation = this.chat.photo.photo_small; ; localFileLocation = null)
      {
        this.avatarDrawable.setInfo(this.chat);
        break;
        this.avatarDrawable.setInfo(0, null, null, false);
        localFileLocation = null;
        break;
        localObject = this.chat.title;
        break label272;
        if (this.user != null)
          if (this.user.status != null)
          {
            this.lastStatus = this.user.status.expires;
            this.lastName = (this.user.first_name + this.user.last_name);
            label489: this.lastAvatar = localFileLocation;
            this.avatarImage.setImage(localFileLocation, "50_50", this.avatarDrawable, null, false);
            if ((getMeasuredWidth() == 0) && (getMeasuredHeight() == 0))
              break label565;
            buildLayout();
          }
        while (true)
        {
          postInvalidate();
          return;
          this.lastStatus = 0;
          break;
          if (this.chat == null)
            break label489;
          this.lastName = this.chat.title;
          break label489;
          requestLayout();
        }
        paramInt = i;
        break label347;
        k = 0;
        break label185;
        i = 0;
        break label140;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ProfileSearchCell
 * JD-Core Version:    0.6.0
 */