package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.TL_dialog;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private StaticLayout countLayout;
  private int countWidth;
  private long dialog_id;
  private BackupImageView imageView;
  private int lastUnreadCount;
  private TextView nameTextView;
  private RectF rect = new RectF();

  public HintDialogCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
    addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(2);
    this.nameTextView.setGravity(49);
    this.nameTextView.setLines(2);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 64.0F, 6.0F, 0.0F));
  }

  public void checkUnreadCounter(int paramInt)
  {
    if ((paramInt != 0) && ((paramInt & 0x100) == 0) && ((paramInt & 0x800) == 0));
    do
      while (true)
      {
        return;
        Object localObject = (TLRPC.TL_dialog)MessagesController.getInstance().dialogs_dict.get(Long.valueOf(this.dialog_id));
        if ((localObject == null) || (((TLRPC.TL_dialog)localObject).unread_count == 0))
          break;
        if (this.lastUnreadCount == ((TLRPC.TL_dialog)localObject).unread_count)
          continue;
        this.lastUnreadCount = ((TLRPC.TL_dialog)localObject).unread_count;
        localObject = String.format("%d", new Object[] { Integer.valueOf(((TLRPC.TL_dialog)localObject).unread_count) });
        this.countWidth = Math.max(AndroidUtilities.dp(12.0F), (int)Math.ceil(Theme.dialogs_countTextPaint.measureText((String)localObject)));
        this.countLayout = new StaticLayout((CharSequence)localObject, Theme.dialogs_countTextPaint, this.countWidth, Layout.Alignment.ALIGN_CENTER, 1.0F, 0.0F, false);
        if (paramInt == 0)
          continue;
        invalidate();
        return;
      }
    while (this.countLayout == null);
    if (paramInt != 0)
      invalidate();
    this.lastUnreadCount = 0;
    this.countLayout = null;
  }

  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    int i;
    int j;
    RectF localRectF;
    float f1;
    float f2;
    if ((paramView == this.imageView) && (this.countLayout != null))
    {
      i = AndroidUtilities.dp(6.0F);
      j = AndroidUtilities.dp(54.0F);
      int k = j - AndroidUtilities.dp(5.5F);
      this.rect.set(k, i, k + this.countWidth + AndroidUtilities.dp(11.0F), AndroidUtilities.dp(23.0F) + i);
      localRectF = this.rect;
      f1 = AndroidUtilities.density;
      f2 = AndroidUtilities.density;
      if (!MessagesController.getInstance().isDialogMuted(this.dialog_id))
        break label170;
    }
    label170: for (paramView = Theme.dialogs_countGrayPaint; ; paramView = Theme.dialogs_countPaint)
    {
      paramCanvas.drawRoundRect(localRectF, 11.5F * f1, 11.5F * f2, paramView);
      paramCanvas.save();
      paramCanvas.translate(j, i + AndroidUtilities.dp(4.0F));
      this.countLayout.draw(paramCanvas);
      paramCanvas.restore();
      return bool;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
  }

  public void setDialog(int paramInt, boolean paramBoolean, CharSequence paramCharSequence)
  {
    this.dialog_id = paramInt;
    Object localObject;
    if (paramInt > 0)
    {
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      if (paramCharSequence != null)
      {
        this.nameTextView.setText(paramCharSequence);
        this.avatarDrawable.setInfo((TLRPC.User)localObject);
        if ((localObject == null) || (((TLRPC.User)localObject).photo == null))
          break label229;
        paramCharSequence = ((TLRPC.User)localObject).photo.photo_small;
      }
    }
    while (true)
    {
      this.imageView.setImage(paramCharSequence, "50_50", this.avatarDrawable);
      if (paramBoolean)
      {
        checkUnreadCounter(0);
        return;
        if (localObject != null)
        {
          this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name));
          break;
        }
        this.nameTextView.setText("");
        break;
        localObject = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
        if (paramCharSequence != null)
          this.nameTextView.setText(paramCharSequence);
        while (true)
        {
          this.avatarDrawable.setInfo((TLRPC.Chat)localObject);
          if ((localObject == null) || (((TLRPC.Chat)localObject).photo == null))
            break label229;
          paramCharSequence = ((TLRPC.Chat)localObject).photo.photo_small;
          break;
          if (localObject != null)
          {
            this.nameTextView.setText(((TLRPC.Chat)localObject).title);
            continue;
          }
          this.nameTextView.setText("");
        }
      }
      this.countLayout = null;
      return;
      label229: paramCharSequence = null;
    }
  }

  public void update()
  {
    int i = (int)this.dialog_id;
    if (i > 0)
    {
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(i));
      this.avatarDrawable.setInfo((TLRPC.User)localObject);
      return;
    }
    Object localObject = MessagesController.getInstance().getChat(Integer.valueOf(-i));
    this.avatarDrawable.setInfo((TLRPC.Chat)localObject);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.HintDialogCell
 * JD-Core Version:    0.6.0
 */