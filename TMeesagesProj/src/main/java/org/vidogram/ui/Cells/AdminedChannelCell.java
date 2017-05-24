package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.URLSpanNoUnderline;

public class AdminedChannelCell extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private TLRPC.Chat currentChannel;
  private ImageView deleteButton;
  private boolean isLast;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;

  public AdminedChannelCell(Context paramContext, View.OnClickListener paramOnClickListener)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label63: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label462;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label468;
      f2 = 12.0F;
      label73: addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label474;
      i = 5;
      label145: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label480;
      i = 5;
      label170: if (!LocaleController.isRTL)
        break label486;
      f1 = 62.0F;
      label179: if (!LocaleController.isRTL)
        break label492;
      f2 = 73.0F;
      label189: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 15.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
      this.statusTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label499;
      i = 5;
      label272: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label505;
      i = 5;
      label297: if (!LocaleController.isRTL)
        break label511;
      f1 = 62.0F;
      label306: if (!LocaleController.isRTL)
        break label517;
      f2 = 73.0F;
      label316: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 38.5F, f2, 0.0F));
      this.deleteButton = new ImageView(paramContext);
      this.deleteButton.setScaleType(ImageView.ScaleType.CENTER);
      this.deleteButton.setImageResource(2130837937);
      this.deleteButton.setOnClickListener(paramOnClickListener);
      this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
      paramContext = this.deleteButton;
      if (!LocaleController.isRTL)
        break label524;
      i = 3;
      label414: if (!LocaleController.isRTL)
        break label530;
      f1 = 7.0F;
      label423: if (!LocaleController.isRTL)
        break label535;
      f2 = 0.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 12.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label462: f1 = 12.0F;
      break label63;
      label468: f2 = 0.0F;
      break label73;
      label474: i = 3;
      break label145;
      label480: i = 3;
      break label170;
      label486: f1 = 73.0F;
      break label179;
      label492: f2 = 62.0F;
      break label189;
      label499: i = 3;
      break label272;
      label505: i = 3;
      break label297;
      label511: f1 = 73.0F;
      break label306;
      label517: f2 = 62.0F;
      break label316;
      label524: i = 5;
      break label414;
      label530: f1 = 0.0F;
      break label423;
      label535: f2 = 7.0F;
    }
  }

  public TLRPC.Chat getCurrentChannel()
  {
    return this.currentChannel;
  }

  public ImageView getDeleteButton()
  {
    return this.deleteButton;
  }

  public SimpleTextView getNameTextView()
  {
    return this.nameTextView;
  }

  public SimpleTextView getStatusTextView()
  {
    return this.statusTextView;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824);
    if (this.isLast);
    for (paramInt1 = 12; ; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(paramInt1 + 60), 1073741824));
      return;
    }
  }

  public void setChannel(TLRPC.Chat paramChat, boolean paramBoolean)
  {
    TLRPC.FileLocation localFileLocation = null;
    if (paramChat.photo != null)
      localFileLocation = paramChat.photo.photo_small;
    String str = MessagesController.getInstance().linkPrefix + "/";
    this.currentChannel = paramChat;
    this.avatarDrawable.setInfo(paramChat);
    this.nameTextView.setText(paramChat.title);
    paramChat = new SpannableStringBuilder(str + paramChat.username);
    paramChat.setSpan(new URLSpanNoUnderline(""), str.length(), paramChat.length(), 33);
    this.statusTextView.setText(paramChat);
    this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
    this.isLast = paramBoolean;
  }

  public void update()
  {
    this.avatarDrawable.setInfo(this.currentChannel);
    this.avatarImageView.invalidate();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.AdminedChannelCell
 * JD-Core Version:    0.6.0
 */