package org.vidogram.ui.Cells;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.GroupCreateCheckBox;
import org.vidogram.ui.Components.LayoutHelper;

public class GroupCreateUserCell extends FrameLayout
{
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private GroupCreateCheckBox checkBox;
  private CharSequence currentName;
  private CharSequence currentStatus;
  private TLRPC.User currentUser;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private SimpleTextView nameTextView;
  private SimpleTextView statusTextView;

  public GroupCreateUserCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label66: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label417;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label423;
      f2 = 11.0F;
      label76: addView((View)localObject, LayoutHelper.createFrame(50, 50.0F, i | 0x30, f1, 11.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label429;
      i = 5;
      label160: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label435;
      i = 5;
      label185: if (!LocaleController.isRTL)
        break label441;
      f1 = 28.0F;
      label194: if (!LocaleController.isRTL)
        break label447;
      f2 = 72.0F;
      label204: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 14.0F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(16);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label454;
      i = 5;
      label263: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label460;
      i = 5;
      label288: if (!LocaleController.isRTL)
        break label466;
      f1 = 28.0F;
      label297: if (!LocaleController.isRTL)
        break label472;
      f2 = 72.0F;
      label307: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 39.0F, f2, 0.0F));
      if (paramBoolean)
      {
        this.checkBox = new GroupCreateCheckBox(paramContext);
        this.checkBox.setVisibility(0);
        paramContext = this.checkBox;
        if (!LocaleController.isRTL)
          break label479;
        i = j;
        label369: if (!LocaleController.isRTL)
          break label485;
        f1 = 0.0F;
        label377: if (!LocaleController.isRTL)
          break label491;
        f2 = 41.0F;
      }
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(24, 24.0F, i | 0x30, f1, 41.0F, f2, 0.0F));
      return;
      i = 3;
      break;
      label417: f1 = 11.0F;
      break label66;
      label423: f2 = 0.0F;
      break label76;
      label429: i = 3;
      break label160;
      label435: i = 3;
      break label185;
      label441: f1 = 72.0F;
      break label194;
      label447: f2 = 28.0F;
      break label204;
      label454: i = 3;
      break label263;
      label460: i = 3;
      break label288;
      label466: f1 = 72.0F;
      break label297;
      label472: f2 = 28.0F;
      break label307;
      label479: i = 3;
      break label369;
      label485: f1 = 41.0F;
      break label377;
      label491: f2 = 0.0F;
    }
  }

  public TLRPC.User getUser()
  {
    return this.currentUser;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0F), 1073741824));
  }

  public void recycle()
  {
    this.avatarImageView.getImageReceiver().cancelLoadImage();
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.checkBox.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setUser(TLRPC.User paramUser, CharSequence paramCharSequence1, CharSequence paramCharSequence2)
  {
    this.currentUser = paramUser;
    this.currentStatus = paramCharSequence2;
    this.currentName = paramCharSequence1;
    update(0);
  }

  public void update(int paramInt)
  {
    int m = 0;
    if (this.currentUser == null)
      return;
    if (this.currentUser.photo != null);
    for (TLRPC.FileLocation localFileLocation = this.currentUser.photo.photo_small; ; localFileLocation = null)
    {
      int j;
      label112: int i;
      int k;
      label166: String str1;
      if (paramInt != 0)
        if (((paramInt & 0x2) != 0) && (((this.lastAvatar != null) && (localFileLocation == null)) || ((this.lastAvatar == null) && (localFileLocation != null) && (this.lastAvatar != null) && (localFileLocation != null) && ((this.lastAvatar.volume_id != localFileLocation.volume_id) || (this.lastAvatar.local_id != localFileLocation.local_id)))))
        {
          j = 1;
          i = j;
          if (this.currentUser != null)
          {
            i = j;
            if (this.currentStatus == null)
            {
              i = j;
              if (j == 0)
              {
                i = j;
                if ((paramInt & 0x4) != 0)
                {
                  if (this.currentUser.status == null)
                    break label592;
                  k = this.currentUser.status.expires;
                  i = j;
                  if (k != this.lastStatus)
                    i = 1;
                }
              }
            }
          }
          if ((i == 0) && (this.currentName == null) && (this.lastName != null) && ((paramInt & 0x1) != 0))
          {
            str1 = UserObject.getUserName(this.currentUser);
            if (!str1.equals(this.lastName))
            {
              paramInt = 1;
              label226: if (paramInt == 0)
                break label590;
            }
          }
        }
      while (true)
      {
        this.avatarDrawable.setInfo(this.currentUser);
        paramInt = m;
        if (this.currentUser.status != null)
          paramInt = this.currentUser.status.expires;
        this.lastStatus = paramInt;
        if (this.currentName != null)
        {
          this.lastName = null;
          this.nameTextView.setText(this.currentName, true);
          if (this.currentStatus == null)
            break label388;
          this.statusTextView.setText(this.currentStatus, true);
          this.statusTextView.setTag("groupcreate_offlineText");
          this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
        }
        while (true)
        {
          this.avatarImageView.setImage(localFileLocation, "50_50", this.avatarDrawable);
          return;
          String str2 = str1;
          if (str1 == null)
            str2 = UserObject.getUserName(this.currentUser);
          this.lastName = str2;
          this.nameTextView.setText(this.lastName);
          break;
          label388: if (this.currentUser.bot)
          {
            this.statusTextView.setTag("groupcreate_offlineText");
            this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
            this.statusTextView.setText(LocaleController.getString("Bot", 2131165390));
            continue;
          }
          if ((this.currentUser.id == UserConfig.getClientUserId()) || ((this.currentUser.status != null) && (this.currentUser.status.expires > ConnectionsManager.getInstance().getCurrentTime())) || (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(this.currentUser.id))))
          {
            this.statusTextView.setTag("groupcreate_offlineText");
            this.statusTextView.setTextColor(Theme.getColor("groupcreate_onlineText"));
            this.statusTextView.setText(LocaleController.getString("Online", 2131166155));
            continue;
          }
          this.statusTextView.setTag("groupcreate_offlineText");
          this.statusTextView.setTextColor(Theme.getColor("groupcreate_offlineText"));
          this.statusTextView.setText(LocaleController.formatUserStatus(this.currentUser));
        }
        paramInt = i;
        break label226;
        paramInt = i;
        str1 = null;
        break label226;
        label590: break;
        label592: k = 0;
        break label166;
        j = 0;
        break label112;
        str1 = null;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.GroupCreateUserCell
 * JD-Core Version:    0.6.0
 */