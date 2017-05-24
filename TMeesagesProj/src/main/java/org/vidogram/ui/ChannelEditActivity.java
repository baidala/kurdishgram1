package org.vidogram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.util.concurrent.Semaphore;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_chatPhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.AvatarUpdater;
import org.vidogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class ChannelEditActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private TextSettingsCell adminCell;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable = new AvatarDrawable();
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatId;
  private FrameLayout container1;
  private FrameLayout container2;
  private FrameLayout container3;
  private boolean createAfterUpload;
  private TLRPC.Chat currentChat;
  private EditText descriptionTextView;
  private View doneButton;
  private boolean donePressed;
  private TLRPC.ChatFull info;
  private TextInfoPrivacyCell infoCell;
  private TextInfoPrivacyCell infoCell2;
  private View lineView;
  private View lineView2;
  private LinearLayout linearLayout2;
  private LinearLayout linearLayout3;
  private EditText nameTextView;
  private AlertDialog progressDialog;
  private ShadowSectionCell sectionCell;
  private ShadowSectionCell sectionCell2;
  private boolean signMessages;
  private TextSettingsCell textCell;
  private TextCheckCell textCheckCell;
  private TextSettingsCell typeCell;
  private TLRPC.InputFile uploadedAvatar;

  public ChannelEditActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatId = paramBundle.getInt("chat_id", 0);
  }

  private void updateAdminCell()
  {
    if (this.adminCell == null)
      return;
    if (this.info != null)
    {
      this.adminCell.setTextAndValue(LocaleController.getString("ChannelAdministrators", 2131165450), String.format("%d", new Object[] { Integer.valueOf(this.info.admins_count) }), false);
      return;
    }
    this.adminCell.setText(LocaleController.getString("ChannelAdministrators", 2131165450), false);
  }

  private void updateTypeCell()
  {
    String str;
    if ((this.currentChat.username == null) || (this.currentChat.username.length() == 0))
    {
      str = LocaleController.getString("ChannelTypePrivate", 2131165519);
      if (!this.currentChat.megagroup)
        break label165;
      this.typeCell.setTextAndValue(LocaleController.getString("GroupType", 2131165798), str, false);
    }
    while (true)
    {
      if ((!this.currentChat.creator) || ((this.info != null) && (!this.info.can_set_username)))
        break label186;
      this.typeCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = new Bundle();
          paramView.putInt("chat_id", ChannelEditActivity.this.chatId);
          paramView = new ChannelEditTypeActivity(paramView);
          paramView.setInfo(ChannelEditActivity.this.info);
          ChannelEditActivity.this.presentFragment(paramView);
        }
      });
      this.typeCell.getTextView().setTag("windowBackgroundWhiteBlackText");
      this.typeCell.getValueTextView().setTag("windowBackgroundWhiteValueText");
      this.typeCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.typeCell.setTextValueColor(Theme.getColor("windowBackgroundWhiteValueText"));
      return;
      str = LocaleController.getString("ChannelTypePublic", 2131165520);
      break;
      label165: this.typeCell.setTextAndValue(LocaleController.getString("ChannelType", 2131165518), str, false);
    }
    label186: this.typeCell.setOnClickListener(null);
    this.typeCell.getTextView().setTag("windowBackgroundWhiteGrayText");
    this.typeCell.getValueTextView().setTag("windowBackgroundWhiteGrayText");
    this.typeCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
    this.typeCell.setTextValueColor(Theme.getColor("windowBackgroundWhiteGrayText"));
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          ChannelEditActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (ChannelEditActivity.this.donePressed));
        if (ChannelEditActivity.this.nameTextView.length() == 0)
        {
          Vibrator localVibrator = (Vibrator)ChannelEditActivity.this.getParentActivity().getSystemService("vibrator");
          if (localVibrator != null)
            localVibrator.vibrate(200L);
          AndroidUtilities.shakeView(ChannelEditActivity.this.nameTextView, 2.0F, 0);
          return;
        }
        ChannelEditActivity.access$202(ChannelEditActivity.this, true);
        if (ChannelEditActivity.this.avatarUpdater.uploadingAvatar != null)
        {
          ChannelEditActivity.access$502(ChannelEditActivity.this, true);
          ChannelEditActivity.access$602(ChannelEditActivity.this, new AlertDialog(ChannelEditActivity.this.getParentActivity(), 1));
          ChannelEditActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165920));
          ChannelEditActivity.this.progressDialog.setCanceledOnTouchOutside(false);
          ChannelEditActivity.this.progressDialog.setCancelable(false);
          ChannelEditActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              ChannelEditActivity.access$502(ChannelEditActivity.this, false);
              ChannelEditActivity.access$602(ChannelEditActivity.this, null);
              ChannelEditActivity.access$202(ChannelEditActivity.this, false);
              try
              {
                paramDialogInterface.dismiss();
                return;
              }
              catch (Exception paramDialogInterface)
              {
                FileLog.e(paramDialogInterface);
              }
            }
          });
          ChannelEditActivity.this.progressDialog.show();
          return;
        }
        if (!ChannelEditActivity.this.currentChat.title.equals(ChannelEditActivity.this.nameTextView.getText().toString()))
          MessagesController.getInstance().changeChatTitle(ChannelEditActivity.this.chatId, ChannelEditActivity.this.nameTextView.getText().toString());
        if ((ChannelEditActivity.this.info != null) && (!ChannelEditActivity.this.info.about.equals(ChannelEditActivity.this.descriptionTextView.getText().toString())))
          MessagesController.getInstance().updateChannelAbout(ChannelEditActivity.this.chatId, ChannelEditActivity.this.descriptionTextView.getText().toString(), ChannelEditActivity.this.info);
        if (ChannelEditActivity.this.signMessages != ChannelEditActivity.this.currentChat.signatures)
        {
          ChannelEditActivity.this.currentChat.signatures = true;
          MessagesController.getInstance().toogleChannelSignatures(ChannelEditActivity.this.chatId, ChannelEditActivity.this.signMessages);
        }
        if (ChannelEditActivity.this.uploadedAvatar != null)
          MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, ChannelEditActivity.this.uploadedAvatar);
        while (true)
        {
          ChannelEditActivity.this.finishFragment();
          return;
          if ((ChannelEditActivity.this.avatar != null) || (!(ChannelEditActivity.this.currentChat.photo instanceof TLRPC.TL_chatPhoto)))
            continue;
          MessagesController.getInstance().changeChatAvatar(ChannelEditActivity.this.chatId, null);
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    LinearLayout localLinearLayout = new LinearLayout(paramContext);
    ((ScrollView)localObject1).addView(localLinearLayout, new FrameLayout.LayoutParams(-1, -2));
    localLinearLayout.setOrientation(1);
    this.actionBar.setTitle(LocaleController.getString("ChannelEdit", 2131165466));
    this.linearLayout2 = new LinearLayout(paramContext);
    this.linearLayout2.setOrientation(1);
    this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    localLinearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
    localObject1 = new FrameLayout(paramContext);
    this.linearLayout2.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    this.avatarDrawable.setInfo(5, null, null, false);
    this.avatarDrawable.setDrawPhoto(true);
    Object localObject2 = this.avatarImage;
    int i;
    float f1;
    label286: float f2;
    if (LocaleController.isRTL)
    {
      i = 5;
      if (!LocaleController.isRTL)
        break label1574;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label1581;
      f2 = 16.0F;
      label296: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ChannelEditActivity.this.getParentActivity() == null)
            return;
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelEditActivity.this.getParentActivity());
          if (ChannelEditActivity.this.avatar != null)
          {
            paramView = new CharSequence[3];
            paramView[0] = LocaleController.getString("FromCamera", 2131165779);
            paramView[1] = LocaleController.getString("FromGalley", 2131165786);
            paramView[2] = LocaleController.getString("DeletePhoto", 2131165646);
          }
          while (true)
          {
            localBuilder.setItems(paramView, new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (paramInt == 0)
                  ChannelEditActivity.this.avatarUpdater.openCamera();
                do
                {
                  return;
                  if (paramInt != 1)
                    continue;
                  ChannelEditActivity.this.avatarUpdater.openGallery();
                  return;
                }
                while (paramInt != 2);
                ChannelEditActivity.access$1102(ChannelEditActivity.this, null);
                ChannelEditActivity.access$1002(ChannelEditActivity.this, null);
                ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
              }
            });
            ChannelEditActivity.this.showDialog(localBuilder.create());
            return;
            paramView = new CharSequence[2];
            paramView[0] = LocaleController.getString("FromCamera", 2131165779);
            paramView[1] = LocaleController.getString("FromGalley", 2131165786);
          }
        }
      });
      this.nameTextView = new EditText(paramContext);
      if (!this.currentChat.megagroup)
        break label1586;
      this.nameTextView.setHint(LocaleController.getString("GroupName", 2131165796));
      label377: this.nameTextView.setMaxLines(4);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL)
        break label1605;
      i = 5;
      label400: ((EditText)localObject2).setGravity(i | 0x10);
      this.nameTextView.setTextSize(1, 16.0F);
      this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.nameTextView.setImeOptions(268435456);
      this.nameTextView.setInputType(16385);
      this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      localObject2 = new InputFilter.LengthFilter(100);
      this.nameTextView.setFilters(new InputFilter[] { localObject2 });
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL)
        break label1611;
      f1 = 16.0F;
      label545: if (!LocaleController.isRTL)
        break label1618;
      f2 = 96.0F;
      label555: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
          AvatarDrawable localAvatarDrawable = ChannelEditActivity.this.avatarDrawable;
          if (ChannelEditActivity.this.nameTextView.length() > 0);
          for (paramEditable = ChannelEditActivity.this.nameTextView.getText().toString(); ; paramEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramEditable, null, false);
            ChannelEditActivity.this.avatarImage.invalidate();
            return;
          }
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }
      });
      this.lineView = new View(paramContext);
      this.lineView.setBackgroundColor(Theme.getColor("divider"));
      localLinearLayout.addView(this.lineView, new LinearLayout.LayoutParams(-1, 1));
      this.linearLayout3 = new LinearLayout(paramContext);
      this.linearLayout3.setOrientation(1);
      this.linearLayout3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      localLinearLayout.addView(this.linearLayout3, LayoutHelper.createLinear(-1, -2));
      this.descriptionTextView = new EditText(paramContext);
      this.descriptionTextView.setTextSize(1, 16.0F);
      this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
      this.descriptionTextView.setBackgroundDrawable(null);
      localObject1 = this.descriptionTextView;
      if (!LocaleController.isRTL)
        break label1625;
      i = 5;
      label769: ((EditText)localObject1).setGravity(i);
      this.descriptionTextView.setInputType(180225);
      this.descriptionTextView.setImeOptions(6);
      localObject1 = new InputFilter.LengthFilter(255);
      this.descriptionTextView.setFilters(new InputFilter[] { localObject1 });
      this.descriptionTextView.setHint(LocaleController.getString("DescriptionOptionalPlaceholder", 2131165653));
      AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
      this.linearLayout3.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 17.0F, 12.0F, 17.0F, 6.0F));
      this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          if ((paramInt == 6) && (ChannelEditActivity.this.doneButton != null))
          {
            ChannelEditActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      this.descriptionTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }
      });
      this.sectionCell = new ShadowSectionCell(paramContext);
      this.sectionCell.setSize(20);
      localLinearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
      this.container1 = new FrameLayout(paramContext);
      this.container1.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      localLinearLayout.addView(this.container1, LayoutHelper.createLinear(-1, -2));
      this.typeCell = new TextSettingsCell(paramContext);
      updateTypeCell();
      this.typeCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.container1.addView(this.typeCell, LayoutHelper.createFrame(-1, -2.0F));
      this.lineView2 = new View(paramContext);
      this.lineView2.setBackgroundColor(Theme.getColor("divider"));
      localLinearLayout.addView(this.lineView2, new LinearLayout.LayoutParams(-1, 1));
      this.container2 = new FrameLayout(paramContext);
      this.container2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      localLinearLayout.addView(this.container2, LayoutHelper.createLinear(-1, -2));
      if (this.currentChat.megagroup)
        break label1631;
      this.textCheckCell = new TextCheckCell(paramContext);
      this.textCheckCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.textCheckCell.setTextAndCheck(LocaleController.getString("ChannelSignMessages", 2131165515), this.signMessages, false);
      this.container2.addView(this.textCheckCell, LayoutHelper.createFrame(-1, -2.0F));
      this.textCheckCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          ChannelEditActivity localChannelEditActivity = ChannelEditActivity.this;
          if (!ChannelEditActivity.this.signMessages);
          for (boolean bool = true; ; bool = false)
          {
            ChannelEditActivity.access$902(localChannelEditActivity, bool);
            ((TextCheckCell)paramView).setChecked(ChannelEditActivity.this.signMessages);
            return;
          }
        }
      });
      this.infoCell = new TextInfoPrivacyCell(paramContext);
      this.infoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837725, "windowBackgroundGrayShadow"));
      this.infoCell.setText(LocaleController.getString("ChannelSignMessagesInfo", 2131165516));
      localLinearLayout.addView(this.infoCell, LayoutHelper.createLinear(-1, -2));
      label1256: if (this.currentChat.creator)
      {
        this.container3 = new FrameLayout(paramContext);
        this.container3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        localLinearLayout.addView(this.container3, LayoutHelper.createLinear(-1, -2));
        this.textCell = new TextSettingsCell(paramContext);
        this.textCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText5"));
        this.textCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (!this.currentChat.megagroup)
          break label1757;
        this.textCell.setText(LocaleController.getString("DeleteMega", 2131165644), false);
        this.container3.addView(this.textCell, LayoutHelper.createFrame(-1, -2.0F));
        this.textCell.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            paramView = new AlertDialog.Builder(ChannelEditActivity.this.getParentActivity());
            if (ChannelEditActivity.this.currentChat.megagroup)
              paramView.setMessage(LocaleController.getString("MegaDeleteAlert", 2131165941));
            while (true)
            {
              paramView.setTitle(LocaleController.getString("AppName", 2131165319));
              paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
                  if (AndroidUtilities.isTablet())
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[] { Long.valueOf(-ChannelEditActivity.access$100(ChannelEditActivity.this)) });
                  while (true)
                  {
                    MessagesController.getInstance().deleteUserFromChat(ChannelEditActivity.this.chatId, MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId())), ChannelEditActivity.this.info);
                    ChannelEditActivity.this.finishFragment();
                    return;
                    NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
                  }
                }
              });
              paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              ChannelEditActivity.this.showDialog(paramView.create());
              return;
              paramView.setMessage(LocaleController.getString("ChannelDeleteAlert", 2131165463));
            }
          }
        });
        this.infoCell2 = new TextInfoPrivacyCell(paramContext);
        this.infoCell2.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837726, "windowBackgroundGrayShadow"));
        if (!this.currentChat.megagroup)
          break label1777;
        this.infoCell2.setText(LocaleController.getString("MegaDeleteInfo", 2131165942));
        localLinearLayout.addView(this.infoCell2, LayoutHelper.createLinear(-1, -2));
      }
      label1369: this.nameTextView.setText(this.currentChat.title);
      label1457: this.nameTextView.setSelection(this.nameTextView.length());
      if (this.info != null)
        this.descriptionTextView.setText(this.info.about);
      if (this.currentChat.photo == null)
        break label1796;
      this.avatar = this.currentChat.photo.photo_small;
      this.avatarImage.setImage(this.avatar, "50_50", this.avatarDrawable);
    }
    while (true)
    {
      return this.fragmentView;
      i = 3;
      break;
      label1574: f1 = 16.0F;
      break label286;
      label1581: f2 = 0.0F;
      break label296;
      label1586: this.nameTextView.setHint(LocaleController.getString("EnterChannelName", 2131165693));
      break label377;
      label1605: i = 3;
      break label400;
      label1611: f1 = 96.0F;
      break label545;
      label1618: f2 = 16.0F;
      break label555;
      label1625: i = 3;
      break label769;
      label1631: this.adminCell = new TextSettingsCell(paramContext);
      updateAdminCell();
      this.adminCell.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.container2.addView(this.adminCell, LayoutHelper.createFrame(-1, -2.0F));
      this.adminCell.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          paramView = new Bundle();
          paramView.putInt("chat_id", ChannelEditActivity.this.chatId);
          paramView.putInt("type", 1);
          ChannelEditActivity.this.presentFragment(new ChannelUsersActivity(paramView));
        }
      });
      this.sectionCell2 = new ShadowSectionCell(paramContext);
      this.sectionCell2.setSize(20);
      localLinearLayout.addView(this.sectionCell2, LayoutHelper.createLinear(-1, -2));
      if (this.currentChat.creator)
        break label1256;
      this.sectionCell2.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837726, "windowBackgroundGrayShadow"));
      break label1256;
      label1757: this.textCell.setText(LocaleController.getString("ChannelDelete", 2131165462), false);
      break label1369;
      label1777: this.infoCell2.setText(LocaleController.getString("ChannelDeleteInfo", 2131165464));
      break label1457;
      label1796: this.avatarImage.setImageDrawable(this.avatarDrawable);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.chatInfoDidLoaded)
    {
      paramArrayOfObject = (TLRPC.ChatFull)paramArrayOfObject[0];
      if (paramArrayOfObject.id == this.chatId)
      {
        if (this.info == null)
          this.descriptionTextView.setText(paramArrayOfObject.about);
        this.info = paramArrayOfObject;
        updateAdminCell();
        updateTypeCell();
      }
    }
    do
      return;
    while ((paramInt != NotificationCenter.updateInterfaces) || ((((Integer)paramArrayOfObject[0]).intValue() & 0x2000) == 0));
    updateTypeCell();
  }

  public void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInputFile, paramPhotoSize1)
    {
      public void run()
      {
        ChannelEditActivity.access$1002(ChannelEditActivity.this, this.val$file);
        ChannelEditActivity.access$1102(ChannelEditActivity.this, this.val$small.location);
        ChannelEditActivity.this.avatarImage.setImage(ChannelEditActivity.this.avatar, "50_50", ChannelEditActivity.this.avatarDrawable);
        if (ChannelEditActivity.this.createAfterUpload);
        try
        {
          if ((ChannelEditActivity.this.progressDialog != null) && (ChannelEditActivity.this.progressDialog.isShowing()))
          {
            ChannelEditActivity.this.progressDialog.dismiss();
            ChannelEditActivity.access$602(ChannelEditActivity.this, null);
          }
          ChannelEditActivity.this.doneButton.performClick();
          return;
        }
        catch (Exception localException)
        {
          while (true)
            FileLog.e(localException);
        }
      }
    });
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    12 local12 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        AvatarDrawable localAvatarDrawable;
        if (ChannelEditActivity.this.avatarImage != null)
        {
          localAvatarDrawable = ChannelEditActivity.this.avatarDrawable;
          if (ChannelEditActivity.this.nameTextView.length() <= 0)
            break label64;
        }
        label64: for (String str = ChannelEditActivity.this.nameTextView.getText().toString(); ; str = null)
        {
          localAvatarDrawable.setInfo(5, str, null, false);
          ChannelEditActivity.this.avatarImage.invalidate();
          return;
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.linearLayout3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container1, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.container3, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local12, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local12, "avatar_backgroundBlue"), new ThemeDescription(this.lineView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"), new ThemeDescription(this.lineView2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "divider"), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.typeCell, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.textCheckCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.textCheckCell, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.infoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoCell, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.adminCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.adminCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.sectionCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.textCell, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.textCell, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText5"), new ThemeDescription(this.infoCell2, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.infoCell2, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public boolean onFragmentCreate()
  {
    this.currentChat = MessagesController.getInstance().getChat(Integer.valueOf(this.chatId));
    if (this.currentChat == null)
    {
      Semaphore localSemaphore = new Semaphore(0);
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(localSemaphore)
      {
        public void run()
        {
          ChannelEditActivity.access$002(ChannelEditActivity.this, MessagesStorage.getInstance().getChat(ChannelEditActivity.this.chatId));
          this.val$semaphore.release();
        }
      });
      try
      {
        localSemaphore.acquire();
        if (this.currentChat != null)
        {
          MessagesController.getInstance().putChat(this.currentChat, true);
          if (this.info != null)
            break label122;
          MessagesStorage.getInstance().loadChatInfo(this.chatId, localSemaphore, false, false);
        }
      }
      catch (Exception localException2)
      {
        try
        {
          localSemaphore.acquire();
          if (this.info == null)
          {
            return false;
            localException2 = localException2;
            FileLog.e(localException2);
          }
        }
        catch (Exception localException1)
        {
          while (true)
            FileLog.e(localException1);
        }
      }
    }
    label122: this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    this.signMessages = this.currentChat.signatures;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarUpdater != null)
      this.avatarUpdater.clear();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatInfoDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }

  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null)
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null))
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    if (this.nameTextView != null)
    {
      String str = this.nameTextView.getText().toString();
      if ((str != null) && (str.length() != 0))
        paramBundle.putString("nameTextView", str);
    }
  }

  public void setInfo(TLRPC.ChatFull paramChatFull)
  {
    this.info = paramChatFull;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChannelEditActivity
 * JD-Core Version:    0.6.0
 */