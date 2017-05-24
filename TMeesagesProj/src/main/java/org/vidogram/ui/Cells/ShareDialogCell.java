package org.vidogram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private CheckBox checkBox;
  private BackupImageView imageView;
  private TextView nameTextView;

  public ShareDialogCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(27.0F));
    addView(this.imageView, LayoutHelper.createFrame(54, 54.0F, 49, 0.0F, 7.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("dialogTextBlack"));
    this.nameTextView.setTextSize(1, 12.0F);
    this.nameTextView.setMaxLines(2);
    this.nameTextView.setGravity(49);
    this.nameTextView.setLines(2);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0F, 51, 6.0F, 64.0F, 6.0F, 0.0F));
    this.checkBox = new CheckBox(paramContext, 2130838041);
    this.checkBox.setSize(24);
    this.checkBox.setCheckOffset(AndroidUtilities.dp(1.0F));
    this.checkBox.setVisibility(0);
    this.checkBox.setColor(Theme.getColor("dialogRoundCheckBox"), Theme.getColor("dialogRoundCheckBoxCheck"));
    addView(this.checkBox, LayoutHelper.createFrame(24, 24.0F, 49, 17.0F, 39.0F, 0.0F, 0.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824));
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setDialog(int paramInt, boolean paramBoolean, CharSequence paramCharSequence)
  {
    Object localObject1 = null;
    if (paramInt > 0)
    {
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      if (paramCharSequence != null)
        this.nameTextView.setText(paramCharSequence);
      while (true)
      {
        this.avatarDrawable.setInfo((TLRPC.User)localObject2);
        paramCharSequence = localObject1;
        if (localObject2 != null)
        {
          paramCharSequence = localObject1;
          if (((TLRPC.User)localObject2).photo != null)
            paramCharSequence = ((TLRPC.User)localObject2).photo.photo_small;
        }
        this.imageView.setImage(paramCharSequence, "50_50", this.avatarDrawable);
        this.checkBox.setChecked(paramBoolean, false);
        return;
        if (localObject2 != null)
        {
          this.nameTextView.setText(ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name));
          continue;
        }
        this.nameTextView.setText("");
      }
    }
    Object localObject2 = MessagesController.getInstance().getChat(Integer.valueOf(-paramInt));
    if (paramCharSequence != null)
      this.nameTextView.setText(paramCharSequence);
    while (true)
    {
      this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
      paramCharSequence = localObject1;
      if (localObject2 == null)
        break;
      paramCharSequence = localObject1;
      if (((TLRPC.Chat)localObject2).photo == null)
        break;
      paramCharSequence = ((TLRPC.Chat)localObject2).photo.photo_small;
      break;
      if (localObject2 != null)
      {
        this.nameTextView.setText(((TLRPC.Chat)localObject2).title);
        continue;
      }
      this.nameTextView.setText("");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.ShareDialogCell
 * JD-Core Version:    0.6.0
 */