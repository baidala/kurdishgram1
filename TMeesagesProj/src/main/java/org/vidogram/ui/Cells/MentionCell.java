package org.vidogram.ui.Cells;

import android.content.Context;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View.MeasureSpec;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout
{
  private AvatarDrawable avatarDrawable;
  private BackupImageView imageView;
  private TextView nameTextView;
  private TextView usernameTextView;

  public MentionCell(Context paramContext)
  {
    super(paramContext);
    setOrientation(0);
    this.avatarDrawable = new AvatarDrawable();
    this.avatarDrawable.setTextSize(AndroidUtilities.dp(12.0F));
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setRoundRadius(AndroidUtilities.dp(14.0F));
    addView(this.imageView, LayoutHelper.createLinear(28, 28, 12.0F, 4.0F, 0.0F, 0.0F));
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.nameTextView.setTextSize(1, 15.0F);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(3);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.nameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 0, 0));
    this.usernameTextView = new TextView(paramContext);
    this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    this.usernameTextView.setTextSize(1, 15.0F);
    this.usernameTextView.setSingleLine(true);
    this.usernameTextView.setGravity(3);
    this.usernameTextView.setEllipsize(TextUtils.TruncateAt.END);
    addView(this.usernameTextView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 8, 0));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0F), 1073741824));
  }

  public void setBotCommand(String paramString1, String paramString2, TLRPC.User paramUser)
  {
    if (paramUser != null)
    {
      this.imageView.setVisibility(0);
      this.avatarDrawable.setInfo(paramUser);
      if ((paramUser.photo != null) && (paramUser.photo.photo_small != null))
        this.imageView.setImage(paramUser.photo.photo_small, "50_50", this.avatarDrawable);
    }
    while (true)
    {
      this.usernameTextView.setVisibility(0);
      this.nameTextView.setText(paramString1);
      this.usernameTextView.setText(Emoji.replaceEmoji(paramString2, this.usernameTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false));
      return;
      this.imageView.setImageDrawable(this.avatarDrawable);
      continue;
      this.imageView.setVisibility(4);
    }
  }

  public void setIsDarkTheme(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.nameTextView.setTextColor(-1);
      this.usernameTextView.setTextColor(-6710887);
      return;
    }
    this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
    this.usernameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
  }

  public void setText(String paramString)
  {
    this.imageView.setVisibility(4);
    this.usernameTextView.setVisibility(4);
    this.nameTextView.setText(paramString);
  }

  public void setUser(TLRPC.User paramUser)
  {
    if (paramUser == null)
    {
      this.nameTextView.setText("");
      this.usernameTextView.setText("");
      this.imageView.setImageDrawable(null);
      return;
    }
    this.avatarDrawable.setInfo(paramUser);
    if ((paramUser.photo != null) && (paramUser.photo.photo_small != null))
    {
      this.imageView.setImage(paramUser.photo.photo_small, "50_50", this.avatarDrawable);
      this.nameTextView.setText(UserObject.getUserName(paramUser));
      if (paramUser.username == null)
        break label154;
      this.usernameTextView.setText("@" + paramUser.username);
    }
    while (true)
    {
      this.imageView.setVisibility(0);
      this.usernameTextView.setVisibility(0);
      return;
      this.imageView.setImageDrawable(this.avatarDrawable);
      break;
      label154: this.usernameTextView.setText("");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.MentionCell
 * JD-Core Version:    0.6.0
 */