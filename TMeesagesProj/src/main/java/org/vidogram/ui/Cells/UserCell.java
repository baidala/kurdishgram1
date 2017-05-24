package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.tgnet.TLRPC.UserStatus;
import org.vidogram.ui.ActionBar.SimpleTextView;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.CheckBoxSquare;
import org.vidogram.ui.Components.LayoutHelper;

public class UserCell extends FrameLayout
{
  private ImageView adminImage;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImageView;
  private CheckBox checkBox;
  private CheckBoxSquare checkBoxBig;
  private int currentDrawable;
  private CharSequence currentName;
  private TLObject currentObject;
  private CharSequence currrntStatus;
  private ImageView imageView;
  private TLRPC.FileLocation lastAvatar;
  private String lastName;
  private int lastStatus;
  private ImageView mutualImage;
  private SimpleTextView nameTextView;
  private int statusColor = Theme.getColor("windowBackgroundWhiteGrayText");
  private int statusOnlineColor = Theme.getColor("windowBackgroundWhiteBlueText");
  private SimpleTextView statusTextView;

  public UserCell(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext);
    this.avatarImageView = new BackupImageView(paramContext);
    this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0F));
    Object localObject = this.avatarImageView;
    int i;
    float f1;
    label82: float f2;
    label95: int j;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label690;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label700;
      f2 = paramInt1 + 7;
      addView((View)localObject, LayoutHelper.createFrame(48, 48.0F, i | 0x30, f1, 8.0F, f2, 0.0F));
      this.nameTextView = new SimpleTextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(17);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label706;
      i = 5;
      label168: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.nameTextView;
      if (!LocaleController.isRTL)
        break label712;
      i = 5;
      label193: if (!LocaleController.isRTL)
        break label724;
      if (paramInt2 != 2)
        break label718;
      j = 18;
      label208: f1 = j + 28;
      label216: if (!LocaleController.isRTL)
        break label734;
      f2 = paramInt1 + 68;
      addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 11.5F, f2, 0.0F));
      this.statusTextView = new SimpleTextView(paramContext);
      this.statusTextView.setTextSize(14);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label760;
      i = 5;
      label289: ((SimpleTextView)localObject).setGravity(i | 0x30);
      localObject = this.statusTextView;
      if (!LocaleController.isRTL)
        break label766;
      i = 5;
      label314: if (!LocaleController.isRTL)
        break label772;
      f1 = 28.0F;
      label324: if (!LocaleController.isRTL)
        break label782;
      f2 = paramInt1 + 68;
      label337: addView((View)localObject, LayoutHelper.createFrame(-1, 20.0F, i | 0x30, f1, 34.5F, f2, 0.0F));
      this.imageView = new ImageView(paramContext);
      this.imageView.setScaleType(ImageView.ScaleType.CENTER);
      this.imageView.setVisibility(8);
      localObject = this.imageView;
      if (!LocaleController.isRTL)
        break label789;
      i = 5;
      label407: if (!LocaleController.isRTL)
        break label795;
      f1 = 0.0F;
      label416: if (!LocaleController.isRTL)
        break label802;
      f2 = 16.0F;
      label426: addView((View)localObject, LayoutHelper.createFrame(-2, -2.0F, i | 0x10, f1, 0.0F, f2, 0.0F));
      if (paramInt2 != 2)
        break label826;
      this.checkBoxBig = new CheckBoxSquare(paramContext, false);
      localObject = this.checkBoxBig;
      if (!LocaleController.isRTL)
        break label808;
      paramInt1 = 3;
      label482: if (!LocaleController.isRTL)
        break label813;
      f1 = 19.0F;
      label492: if (!LocaleController.isRTL)
        break label819;
      f2 = 0.0F;
      label501: addView((View)localObject, LayoutHelper.createFrame(18, 18.0F, paramInt1 | 0x10, f1, 0.0F, f2, 0.0F));
      label524: if (paramBoolean)
      {
        this.adminImage = new ImageView(paramContext);
        this.adminImage.setImageResource(2130837589);
        localObject = this.adminImage;
        if (!LocaleController.isRTL)
          break label954;
        paramInt1 = 3;
        label564: if (!LocaleController.isRTL)
          break label959;
        f1 = 24.0F;
        label574: if (!LocaleController.isRTL)
          break label965;
        f2 = 0.0F;
        label583: addView((View)localObject, LayoutHelper.createFrame(16, 16.0F, paramInt1 | 0x30, f1, 13.5F, f2, 0.0F));
      }
      this.mutualImage = new ImageView(paramContext);
      this.mutualImage.setImageResource(2130837917);
      paramContext = this.mutualImage;
      if (!LocaleController.isRTL)
        break label972;
      paramInt1 = 3;
      label641: if (!LocaleController.isRTL)
        break label977;
      f1 = 24.0F;
      label651: if (!LocaleController.isRTL)
        break label983;
      f2 = 0.0F;
    }
    while (true)
    {
      addView(paramContext, LayoutHelper.createFrame(16, 16.0F, paramInt1 | 0x30, f1, 34.5F, f2, 0.0F));
      return;
      i = 3;
      break;
      label690: f1 = paramInt1 + 7;
      break label82;
      label700: f2 = 0.0F;
      break label95;
      label706: i = 3;
      break label168;
      label712: i = 3;
      break label193;
      label718: j = 0;
      break label208;
      label724: f1 = paramInt1 + 68;
      break label216;
      label734: if (paramInt2 == 2);
      for (j = 18; ; j = 0)
      {
        f2 = j + 28;
        break;
      }
      label760: i = 3;
      break label289;
      label766: i = 3;
      break label314;
      label772: f1 = paramInt1 + 68;
      break label324;
      label782: f2 = 28.0F;
      break label337;
      label789: i = 3;
      break label407;
      label795: f1 = 16.0F;
      break label416;
      label802: f2 = 0.0F;
      break label426;
      label808: paramInt1 = 5;
      break label482;
      label813: f1 = 0.0F;
      break label492;
      label819: f2 = 19.0F;
      break label501;
      label826: if (paramInt2 != 1)
        break label524;
      this.checkBox = new CheckBox(paramContext, 2130838041);
      this.checkBox.setVisibility(4);
      this.checkBox.setColor(Theme.getColor("checkbox"), Theme.getColor("checkboxCheck"));
      localObject = this.checkBox;
      if (LocaleController.isRTL)
      {
        paramInt2 = 5;
        label884: if (!LocaleController.isRTL)
          break label938;
        f1 = 0.0F;
        label893: if (!LocaleController.isRTL)
          break label948;
        f2 = paramInt1 + 37;
      }
      while (true)
      {
        addView((View)localObject, LayoutHelper.createFrame(22, 22.0F, paramInt2 | 0x30, f1, 38.0F, f2, 0.0F));
        break;
        paramInt2 = 3;
        break label884;
        label938: f1 = paramInt1 + 37;
        break label893;
        label948: f2 = 0.0F;
      }
      label954: paramInt1 = 5;
      break label564;
      label959: f1 = 0.0F;
      break label574;
      label965: f2 = 24.0F;
      break label583;
      label972: paramInt1 = 5;
      break label641;
      label977: f1 = 0.0F;
      break label651;
      label983: f2 = 24.0F;
    }
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  public void invalidate()
  {
    super.invalidate();
    if (this.checkBoxBig != null)
      this.checkBoxBig.invalidate();
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0F), 1073741824));
  }

  public void setCheckDisabled(boolean paramBoolean)
  {
    if (this.checkBoxBig != null)
      this.checkBoxBig.setDisabled(paramBoolean);
  }

  public void setChecked(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.checkBox != null)
    {
      if (this.checkBox.getVisibility() != 0)
        this.checkBox.setVisibility(0);
      this.checkBox.setChecked(paramBoolean1, paramBoolean2);
    }
    do
      return;
    while (this.checkBoxBig == null);
    if (this.checkBoxBig.getVisibility() != 0)
      this.checkBoxBig.setVisibility(0);
    this.checkBoxBig.setChecked(paramBoolean1, paramBoolean2);
  }

  public void setData(TLObject paramTLObject, CharSequence paramCharSequence1, CharSequence paramCharSequence2, int paramInt)
  {
    if (paramTLObject == null)
    {
      this.currrntStatus = null;
      this.currentName = null;
      this.currentObject = null;
      this.nameTextView.setText("");
      this.statusTextView.setText("");
      this.avatarImageView.setImageDrawable(null);
      return;
    }
    this.currrntStatus = paramCharSequence2;
    this.currentName = paramCharSequence1;
    this.currentObject = paramTLObject;
    this.currentDrawable = paramInt;
    update(0);
  }

  public void setIsAdmin(int paramInt)
  {
    if (this.adminImage == null);
    label48: label113: label118: label123: 
    do
    {
      return;
      Object localObject = this.adminImage;
      int i;
      if (paramInt != 0)
      {
        i = 0;
        ((ImageView)localObject).setVisibility(i);
        localObject = this.nameTextView;
        if ((!LocaleController.isRTL) || (paramInt == 0))
          break label113;
        i = AndroidUtilities.dp(16.0F);
        if ((LocaleController.isRTL) || (paramInt == 0))
          break label118;
      }
      for (int j = AndroidUtilities.dp(16.0F); ; j = 0)
      {
        ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
        if (paramInt != 1)
          break label123;
        setTag("profile_creatorIcon");
        this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_creatorIcon"), PorterDuff.Mode.MULTIPLY));
        return;
        i = 8;
        break;
        i = 0;
        break label48;
      }
    }
    while (paramInt != 2);
    setTag("profile_adminIcon");
    this.adminImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
  }

  public void setIsMutual(int paramInt)
  {
    if (this.mutualImage == null)
      return;
    Object localObject = this.mutualImage;
    int i;
    if (paramInt != 0)
    {
      i = 0;
      label20: ((ImageView)localObject).setVisibility(i);
      localObject = this.nameTextView;
      if ((!LocaleController.isRTL) || (paramInt == 0))
        break label118;
      i = AndroidUtilities.dp(16.0F);
      label48: if ((LocaleController.isRTL) || (paramInt == 0))
        break label123;
    }
    label118: label123: for (int j = AndroidUtilities.dp(16.0F); ; j = 0)
    {
      ((SimpleTextView)localObject).setPadding(i, 0, j, 0);
      if (paramInt != 9707)
        break;
      this.mutualImage.setImageResource(2130837917);
      this.mutualImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteBlackText"), PorterDuff.Mode.MULTIPLY));
      return;
      i = 8;
      break label20;
      i = 0;
      break label48;
    }
  }

  public void setStatusColors(int paramInt1, int paramInt2)
  {
    this.statusColor = paramInt1;
    this.statusOnlineColor = paramInt2;
  }

  public void update(int paramInt)
  {
    int m = 0;
    if (this.currentObject == null)
      return;
    Object localObject2;
    if ((this.currentObject instanceof TLRPC.User))
    {
      localObject2 = (TLRPC.User)this.currentObject;
      if (((TLRPC.User)localObject2).photo == null)
        break label841;
    }
    label157: label196: label841: for (Object localObject1 = ((TLRPC.User)localObject2).photo.photo_small; ; localObject1 = null)
    {
      Object localObject4;
      Object localObject3;
      if (((TLRPC.User)localObject2).mutual_contact)
      {
        setIsMutual(9707);
        localObject4 = null;
        localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject4;
      }
      while (true)
      {
        int i;
        int k;
        label245: Object localObject5;
        if (paramInt != 0)
          if (((paramInt & 0x2) != 0) && (((this.lastAvatar != null) && (localObject3 == null)) || ((this.lastAvatar == null) && (localObject3 != null) && (this.lastAvatar != null) && (localObject3 != null) && ((this.lastAvatar.volume_id != ((TLRPC.FileLocation)localObject3).volume_id) || (this.lastAvatar.local_id != ((TLRPC.FileLocation)localObject3).local_id)))))
          {
            i = 1;
            int j = i;
            if (localObject1 != null)
            {
              j = i;
              if (i == 0)
              {
                j = i;
                if ((paramInt & 0x4) != 0)
                {
                  if (((TLRPC.User)localObject1).status == null)
                    break label815;
                  k = ((TLRPC.User)localObject1).status.expires;
                  j = i;
                  if (k != this.lastStatus)
                    j = 1;
                }
              }
            }
            if ((j == 0) && (this.currentName == null) && (this.lastName != null) && ((paramInt & 0x1) != 0))
              if (localObject1 != null)
              {
                localObject4 = UserObject.getUserName((TLRPC.User)localObject1);
                localObject5 = localObject4;
                if (!((String)localObject4).equals(this.lastName))
                {
                  j = 1;
                  localObject5 = localObject4;
                }
                if (j == 0)
                  break label813;
              }
          }
        for (localObject4 = localObject5; ; localObject4 = null)
        {
          if (localObject1 != null)
          {
            this.avatarDrawable.setInfo((TLRPC.User)localObject1);
            if (((TLRPC.User)localObject1).status != null)
            {
              this.lastStatus = ((TLRPC.User)localObject1).status.expires;
              if (this.currentName == null)
                break label544;
              this.lastName = null;
              this.nameTextView.setText(this.currentName);
              if (this.currrntStatus == null)
                break label610;
              this.statusTextView.setTextColor(this.statusColor);
              this.statusTextView.setText(this.currrntStatus);
            }
          }
          while (true)
          {
            if (((this.imageView.getVisibility() == 0) && (this.currentDrawable == 0)) || ((this.imageView.getVisibility() == 8) && (this.currentDrawable != 0)))
            {
              localObject1 = this.imageView;
              paramInt = m;
              if (this.currentDrawable == 0)
                paramInt = 8;
              ((ImageView)localObject1).setVisibility(paramInt);
              this.imageView.setImageResource(this.currentDrawable);
            }
            this.avatarImageView.setImage((TLObject)localObject3, "50_50", this.avatarDrawable);
            return;
            setIsMutual(0);
            localObject5 = null;
            localObject3 = localObject2;
            localObject4 = localObject1;
            localObject2 = localObject5;
            localObject1 = localObject3;
            localObject3 = localObject4;
            break;
            localObject2 = (TLRPC.Chat)this.currentObject;
            if (((TLRPC.Chat)localObject2).photo == null)
              break label832;
            localObject3 = ((TLRPC.Chat)localObject2).photo.photo_small;
            localObject1 = null;
            break;
            localObject4 = ((TLRPC.Chat)localObject2).title;
            break label245;
            this.lastStatus = 0;
            break label309;
            this.avatarDrawable.setInfo((TLRPC.Chat)localObject2);
            break label309;
            if (localObject1 != null)
            {
              localObject2 = localObject4;
              if (localObject4 == null)
                localObject2 = UserObject.getUserName((TLRPC.User)localObject1);
            }
            for (this.lastName = ((String)localObject2); ; this.lastName = ((String)localObject5))
            {
              this.nameTextView.setText(this.lastName);
              break;
              localObject5 = localObject4;
              if (localObject4 != null)
                continue;
              localObject5 = ((TLRPC.Chat)localObject2).title;
            }
            label610: if (localObject1 == null)
              continue;
            if (((TLRPC.User)localObject1).bot)
            {
              this.statusTextView.setTextColor(this.statusColor);
              if ((((TLRPC.User)localObject1).bot_chat_history) || ((this.adminImage != null) && (this.adminImage.getVisibility() == 0)))
              {
                this.statusTextView.setText(LocaleController.getString("BotStatusRead", 2131165401));
                continue;
              }
              this.statusTextView.setText(LocaleController.getString("BotStatusCantRead", 2131165400));
              continue;
            }
            if ((((TLRPC.User)localObject1).id == UserConfig.getClientUserId()) || ((((TLRPC.User)localObject1).status != null) && (((TLRPC.User)localObject1).status.expires > ConnectionsManager.getInstance().getCurrentTime())) || (MessagesController.getInstance().onlinePrivacy.containsKey(Integer.valueOf(((TLRPC.User)localObject1).id))))
            {
              this.statusTextView.setTextColor(this.statusOnlineColor);
              this.statusTextView.setText(LocaleController.getString("Online", 2131166155));
              continue;
            }
            this.statusTextView.setTextColor(this.statusColor);
            this.statusTextView.setText(LocaleController.formatUserStatus((TLRPC.User)localObject1));
          }
          localObject5 = null;
          break label267;
          break;
          k = 0;
          break label196;
          i = 0;
          break label157;
        }
        localObject1 = null;
        localObject3 = null;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.UserCell
 * JD-Core Version:    0.6.0
 */