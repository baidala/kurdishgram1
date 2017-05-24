package org.vidogram.ui;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ExportedChatInvite;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_boolTrue;
import org.vidogram.tgnet.TLRPC.TL_channels_checkUsername;
import org.vidogram.tgnet.TLRPC.TL_channels_exportInvite;
import org.vidogram.tgnet.TLRPC.TL_channels_getAdminedPublicChannels;
import org.vidogram.tgnet.TLRPC.TL_channels_updateUsername;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_inputChannelEmpty;
import org.vidogram.tgnet.TLRPC.TL_messages_chats;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.AdminedChannelCell;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Cells.RadioButtonCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextBlockCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.AvatarUpdater;
import org.vidogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class ChannelCreateActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private ArrayList<AdminedChannelCell> adminedChannelCells = new ArrayList();
  private TextInfoPrivacyCell adminedInfoCell;
  private LinearLayout adminnedChannelsLayout;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater;
  private boolean canCreatePublic = true;
  private int chatId;
  private int checkReqId;
  private Runnable checkRunnable;
  private TextView checkTextView;
  private boolean createAfterUpload;
  private int currentStep;
  private EditText descriptionTextView;
  private View doneButton;
  private boolean donePressed;
  private EditText editText;
  private HeaderCell headerCell;
  private TextView helpTextView;
  private TLRPC.ExportedChatInvite invite;
  private boolean isPrivate;
  private String lastCheckName;
  private boolean lastNameAvailable;
  private LinearLayout linearLayout;
  private LinearLayout linearLayout2;
  private LinearLayout linkContainer;
  private LoadingCell loadingAdminedCell;
  private boolean loadingAdminedChannels;
  private boolean loadingInvite;
  private EditText nameTextView;
  private String nameToSet;
  private TextBlockCell privateContainer;
  private AlertDialog progressDialog;
  private LinearLayout publicContainer;
  private RadioButtonCell radioButtonCell1;
  private RadioButtonCell radioButtonCell2;
  private ShadowSectionCell sectionCell;
  private TextInfoPrivacyCell typeInfoCell;
  private TLRPC.InputFile uploadedAvatar;

  public ChannelCreateActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.currentStep = paramBundle.getInt("step", 0);
    if (this.currentStep == 0)
    {
      this.avatarDrawable = new AvatarDrawable();
      this.avatarUpdater = new AvatarUpdater();
      paramBundle = new TLRPC.TL_channels_checkUsername();
      paramBundle.username = "1";
      paramBundle.channel = new TLRPC.TL_inputChannelEmpty();
      ConnectionsManager.getInstance().sendRequest(paramBundle, new RequestDelegate()
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
          {
            public void run()
            {
              ChannelCreateActivity localChannelCreateActivity = ChannelCreateActivity.this;
              if ((this.val$error == null) || (!this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")));
              for (boolean bool = true; ; bool = false)
              {
                ChannelCreateActivity.access$002(localChannelCreateActivity, bool);
                return;
              }
            }
          });
        }
      });
      return;
    }
    if (this.currentStep == 1)
    {
      this.canCreatePublic = paramBundle.getBoolean("canCreatePublic", true);
      if (this.canCreatePublic)
        break label159;
    }
    while (true)
    {
      this.isPrivate = bool;
      if (!this.canCreatePublic)
        loadAdminedChannels();
      this.chatId = paramBundle.getInt("chat_id", 0);
      return;
      label159: bool = false;
    }
  }

  private boolean checkUserName(String paramString)
  {
    if ((paramString != null) && (paramString.length() > 0))
      this.checkTextView.setVisibility(0);
    while (true)
    {
      if (this.checkRunnable != null)
      {
        AndroidUtilities.cancelRunOnUIThread(this.checkRunnable);
        this.checkRunnable = null;
        this.lastCheckName = null;
        if (this.checkReqId != 0)
          ConnectionsManager.getInstance().cancelRequest(this.checkReqId, true);
      }
      this.lastNameAvailable = false;
      if (paramString == null)
        break;
      if ((paramString.startsWith("_")) || (paramString.endsWith("_")))
      {
        this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165912));
        this.checkTextView.setTag("windowBackgroundWhiteRedText4");
        this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
        return false;
        this.checkTextView.setVisibility(8);
        continue;
      }
      int i = 0;
      while (i < paramString.length())
      {
        int j = paramString.charAt(i);
        if ((i == 0) && (j >= 48) && (j <= 57))
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalidStartNumber", 2131165916));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          return false;
        }
        if (((j < 48) || (j > 57)) && ((j < 97) || (j > 122)) && ((j < 65) || (j > 90)) && (j != 95))
        {
          this.checkTextView.setText(LocaleController.getString("LinkInvalid", 2131165912));
          this.checkTextView.setTag("windowBackgroundWhiteRedText4");
          this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
          return false;
        }
        i += 1;
      }
    }
    if ((paramString == null) || (paramString.length() < 5))
    {
      this.checkTextView.setText(LocaleController.getString("LinkInvalidShort", 2131165914));
      this.checkTextView.setTag("windowBackgroundWhiteRedText4");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
    if (paramString.length() > 32)
    {
      this.checkTextView.setText(LocaleController.getString("LinkInvalidLong", 2131165913));
      this.checkTextView.setTag("windowBackgroundWhiteRedText4");
      this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      return false;
    }
    this.checkTextView.setText(LocaleController.getString("LinkChecking", 2131165908));
    this.checkTextView.setTag("windowBackgroundWhiteGrayText8");
    this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
    this.lastCheckName = paramString;
    this.checkRunnable = new Runnable(paramString)
    {
      public void run()
      {
        TLRPC.TL_channels_checkUsername localTL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        localTL_channels_checkUsername.username = this.val$name;
        localTL_channels_checkUsername.channel = MessagesController.getInputChannel(ChannelCreateActivity.this.chatId);
        ChannelCreateActivity.access$2702(ChannelCreateActivity.this, ConnectionsManager.getInstance().sendRequest(localTL_channels_checkUsername, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
            {
              public void run()
              {
                ChannelCreateActivity.access$2702(ChannelCreateActivity.this, 0);
                if ((ChannelCreateActivity.this.lastCheckName != null) && (ChannelCreateActivity.this.lastCheckName.equals(ChannelCreateActivity.14.this.val$name)))
                {
                  if ((this.val$error == null) && ((this.val$response instanceof TLRPC.TL_boolTrue)))
                  {
                    ChannelCreateActivity.this.checkTextView.setText(LocaleController.formatString("LinkAvailable", 2131165907, new Object[] { ChannelCreateActivity.14.this.val$name }));
                    ChannelCreateActivity.this.checkTextView.setTag("windowBackgroundWhiteGreenText");
                    ChannelCreateActivity.this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGreenText"));
                    ChannelCreateActivity.access$902(ChannelCreateActivity.this, true);
                  }
                }
                else
                  return;
                if ((this.val$error != null) && (this.val$error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH")))
                {
                  ChannelCreateActivity.access$002(ChannelCreateActivity.this, false);
                  ChannelCreateActivity.this.loadAdminedChannels();
                }
                while (true)
                {
                  ChannelCreateActivity.this.checkTextView.setTag("windowBackgroundWhiteRedText4");
                  ChannelCreateActivity.this.checkTextView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
                  ChannelCreateActivity.access$902(ChannelCreateActivity.this, false);
                  return;
                  ChannelCreateActivity.this.checkTextView.setText(LocaleController.getString("LinkInUse", 2131165910));
                }
              }
            });
          }
        }
        , 2));
      }
    };
    AndroidUtilities.runOnUIThread(this.checkRunnable, 300L);
    return true;
  }

  private void generateLink()
  {
    if ((this.loadingInvite) || (this.invite != null))
      return;
    this.loadingInvite = true;
    TLRPC.TL_channels_exportInvite localTL_channels_exportInvite = new TLRPC.TL_channels_exportInvite();
    localTL_channels_exportInvite.channel = MessagesController.getInputChannel(this.chatId);
    ConnectionsManager.getInstance().sendRequest(localTL_channels_exportInvite, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if (this.val$error == null)
              ChannelCreateActivity.access$2002(ChannelCreateActivity.this, (TLRPC.ExportedChatInvite)this.val$response);
            ChannelCreateActivity.access$2102(ChannelCreateActivity.this, false);
            TextBlockCell localTextBlockCell = ChannelCreateActivity.this.privateContainer;
            if (ChannelCreateActivity.this.invite != null);
            for (String str = ChannelCreateActivity.this.invite.link; ; str = LocaleController.getString("Loading", 2131165920))
            {
              localTextBlockCell.setText(str, false);
              return;
            }
          }
        });
      }
    });
  }

  private void loadAdminedChannels()
  {
    if (this.loadingAdminedChannels)
      return;
    this.loadingAdminedChannels = true;
    updatePrivatePublic();
    TLRPC.TL_channels_getAdminedPublicChannels localTL_channels_getAdminedPublicChannels = new TLRPC.TL_channels_getAdminedPublicChannels();
    ConnectionsManager.getInstance().sendRequest(localTL_channels_getAdminedPublicChannels, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
        {
          public void run()
          {
            ChannelCreateActivity.access$2302(ChannelCreateActivity.this, false);
            if ((this.val$response == null) || (ChannelCreateActivity.this.getParentActivity() == null))
              return;
            int i = 0;
            while (i < ChannelCreateActivity.this.adminedChannelCells.size())
            {
              ChannelCreateActivity.this.linearLayout.removeView((View)ChannelCreateActivity.this.adminedChannelCells.get(i));
              i += 1;
            }
            ChannelCreateActivity.this.adminedChannelCells.clear();
            TLRPC.TL_messages_chats localTL_messages_chats = (TLRPC.TL_messages_chats)this.val$response;
            i = 0;
            if (i < localTL_messages_chats.chats.size())
            {
              AdminedChannelCell localAdminedChannelCell = new AdminedChannelCell(ChannelCreateActivity.this.getParentActivity(), new View.OnClickListener()
              {
                public void onClick(View paramView)
                {
                  paramView = ((AdminedChannelCell)paramView.getParent()).getCurrentChannel();
                  AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
                  localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
                  if (paramView.megagroup)
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlert", 2131166365, new Object[] { MessagesController.getInstance().linkPrefix + "/" + paramView.username, paramView.title })));
                  while (true)
                  {
                    localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
                    localBuilder.setPositiveButton(LocaleController.getString("RevokeButton", 2131166363), new DialogInterface.OnClickListener(paramView)
                    {
                      public void onClick(DialogInterface paramDialogInterface, int paramInt)
                      {
                        paramDialogInterface = new TLRPC.TL_channels_updateUsername();
                        paramDialogInterface.channel = MessagesController.getInputChannel(this.val$channel);
                        paramDialogInterface.username = "";
                        ConnectionsManager.getInstance().sendRequest(paramDialogInterface, new RequestDelegate()
                        {
                          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
                          {
                            if ((paramTLObject instanceof TLRPC.TL_boolTrue))
                              AndroidUtilities.runOnUIThread(new Runnable()
                              {
                                public void run()
                                {
                                  ChannelCreateActivity.access$002(ChannelCreateActivity.this, true);
                                  if (ChannelCreateActivity.this.nameTextView.length() > 0)
                                    ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
                                  ChannelCreateActivity.this.updatePrivatePublic();
                                }
                              });
                          }
                        }
                        , 64);
                      }
                    });
                    ChannelCreateActivity.this.showDialog(localBuilder.create());
                    return;
                    localBuilder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("RevokeLinkAlertChannel", 2131166366, new Object[] { MessagesController.getInstance().linkPrefix + "/" + paramView.username, paramView.title })));
                  }
                }
              });
              TLRPC.Chat localChat = (TLRPC.Chat)localTL_messages_chats.chats.get(i);
              if (i == localTL_messages_chats.chats.size() - 1);
              for (boolean bool = true; ; bool = false)
              {
                localAdminedChannelCell.setChannel(localChat, bool);
                ChannelCreateActivity.this.adminedChannelCells.add(localAdminedChannelCell);
                ChannelCreateActivity.this.adminnedChannelsLayout.addView(localAdminedChannelCell, LayoutHelper.createLinear(-1, 72));
                i += 1;
                break;
              }
            }
            ChannelCreateActivity.this.updatePrivatePublic();
          }
        });
      }
    });
  }

  private void showErrorAlert(String paramString)
  {
    if (getParentActivity() == null)
      return;
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
      switch (i)
      {
      default:
        localBuilder.setMessage(LocaleController.getString("ErrorOccurred", 2131165701));
      case 0:
      case 1:
      case 2:
      }
    case 288843630:
    case 533175271:
    case -141887186:
    }
    while (true)
    {
      localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), null);
      showDialog(localBuilder.create());
      return;
      if (!paramString.equals("USERNAME_INVALID"))
        break;
      i = 0;
      break;
      if (!paramString.equals("USERNAME_OCCUPIED"))
        break;
      i = 1;
      break;
      if (!paramString.equals("USERNAMES_UNAVAILABLE"))
        break;
      i = 2;
      break;
      localBuilder.setMessage(LocaleController.getString("LinkInvalid", 2131165912));
      continue;
      localBuilder.setMessage(LocaleController.getString("LinkInUse", 2131165910));
      continue;
      localBuilder.setMessage(LocaleController.getString("FeatureUnavailable", 2131165706));
    }
  }

  private void updatePrivatePublic()
  {
    int j = 8;
    boolean bool = false;
    if (this.sectionCell == null)
      return;
    if ((!this.isPrivate) && (!this.canCreatePublic))
    {
      this.typeInfoCell.setText(LocaleController.getString("ChangePublicLimitReached", 2131165441));
      this.typeInfoCell.setTag("windowBackgroundWhiteRedText4");
      this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText4"));
      this.linkContainer.setVisibility(8);
      this.sectionCell.setVisibility(8);
      if (this.loadingAdminedChannels)
      {
        this.loadingAdminedCell.setVisibility(0);
        this.adminnedChannelsLayout.setVisibility(8);
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), 2130837726, "windowBackgroundGrayShadow"));
        this.adminedInfoCell.setVisibility(8);
      }
      while (true)
      {
        localObject1 = this.radioButtonCell1;
        if (!this.isPrivate)
          bool = true;
        ((RadioButtonCell)localObject1).setChecked(bool, true);
        this.radioButtonCell2.setChecked(this.isPrivate, true);
        this.nameTextView.clearFocus();
        AndroidUtilities.hideKeyboard(this.nameTextView);
        return;
        this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), 2130837725, "windowBackgroundGrayShadow"));
        this.loadingAdminedCell.setVisibility(8);
        this.adminnedChannelsLayout.setVisibility(0);
        this.adminedInfoCell.setVisibility(0);
      }
    }
    this.typeInfoCell.setTag("windowBackgroundWhiteGrayText4");
    this.typeInfoCell.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText4"));
    this.sectionCell.setVisibility(0);
    this.adminedInfoCell.setVisibility(8);
    this.adminnedChannelsLayout.setVisibility(8);
    this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(this.typeInfoCell.getContext(), 2130837726, "windowBackgroundGrayShadow"));
    this.linkContainer.setVisibility(0);
    this.loadingAdminedCell.setVisibility(8);
    Object localObject2 = this.typeInfoCell;
    label353: int i;
    if (this.isPrivate)
    {
      localObject1 = LocaleController.getString("ChannelPrivateLinkHelp", 2131165508);
      ((TextInfoPrivacyCell)localObject2).setText((CharSequence)localObject1);
      localObject2 = this.headerCell;
      if (!this.isPrivate)
        break label540;
      localObject1 = LocaleController.getString("ChannelInviteLinkTitle", 2131165468);
      label384: ((HeaderCell)localObject2).setText((String)localObject1);
      localObject1 = this.publicContainer;
      if (!this.isPrivate)
        break label554;
      i = 8;
      label407: ((LinearLayout)localObject1).setVisibility(i);
      localObject1 = this.privateContainer;
      if (!this.isPrivate)
        break label559;
      i = 0;
      label428: ((TextBlockCell)localObject1).setVisibility(i);
      localObject1 = this.linkContainer;
      if (!this.isPrivate)
        break label565;
      i = 0;
      label449: ((LinearLayout)localObject1).setPadding(0, 0, 0, i);
      localObject2 = this.privateContainer;
      if (this.invite == null)
        break label575;
    }
    label540: label554: label559: label565: label575: for (Object localObject1 = this.invite.link; ; localObject1 = LocaleController.getString("Loading", 2131165920))
    {
      ((TextBlockCell)localObject2).setText((String)localObject1, false);
      localObject1 = this.checkTextView;
      i = j;
      if (!this.isPrivate)
      {
        i = j;
        if (this.checkTextView.length() != 0)
          i = 0;
      }
      ((TextView)localObject1).setVisibility(i);
      break;
      localObject1 = LocaleController.getString("ChannelUsernameHelp", 2131165527);
      break label353;
      localObject1 = LocaleController.getString("ChannelLinkTitle", 2131165475);
      break label384;
      i = 0;
      break label407;
      i = 8;
      break label428;
      i = AndroidUtilities.dp(7.0F);
      break label449;
    }
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
          ChannelCreateActivity.this.finishFragment();
        do
          while (true)
          {
            do
              return;
            while (paramInt != 1);
            if (ChannelCreateActivity.this.currentStep != 0)
              break;
            if (ChannelCreateActivity.this.donePressed)
              continue;
            if (ChannelCreateActivity.this.nameTextView.length() == 0)
            {
              localObject = (Vibrator)ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
              if (localObject != null)
                ((Vibrator)localObject).vibrate(200L);
              AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView, 2.0F, 0);
              return;
            }
            ChannelCreateActivity.access$202(ChannelCreateActivity.this, true);
            if (ChannelCreateActivity.this.avatarUpdater.uploadingAvatar != null)
            {
              ChannelCreateActivity.access$502(ChannelCreateActivity.this, true);
              ChannelCreateActivity.access$602(ChannelCreateActivity.this, new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 1));
              ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165920));
              ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
              ChannelCreateActivity.this.progressDialog.setCancelable(false);
              ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  ChannelCreateActivity.access$502(ChannelCreateActivity.this, false);
                  ChannelCreateActivity.access$602(ChannelCreateActivity.this, null);
                  ChannelCreateActivity.access$202(ChannelCreateActivity.this, false);
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
              ChannelCreateActivity.this.progressDialog.show();
              return;
            }
            paramInt = MessagesController.getInstance().createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, ChannelCreateActivity.this);
            ChannelCreateActivity.access$602(ChannelCreateActivity.this, new AlertDialog(ChannelCreateActivity.this.getParentActivity(), 1));
            ChannelCreateActivity.this.progressDialog.setMessage(LocaleController.getString("Loading", 2131165920));
            ChannelCreateActivity.this.progressDialog.setCanceledOnTouchOutside(false);
            ChannelCreateActivity.this.progressDialog.setCancelable(false);
            ChannelCreateActivity.this.progressDialog.setButton(-2, LocaleController.getString("Cancel", 2131165427), new DialogInterface.OnClickListener(paramInt)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                ConnectionsManager.getInstance().cancelRequest(this.val$reqId, true);
                ChannelCreateActivity.access$202(ChannelCreateActivity.this, false);
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
            ChannelCreateActivity.this.progressDialog.show();
            return;
          }
        while (ChannelCreateActivity.this.currentStep != 1);
        if (!ChannelCreateActivity.this.isPrivate)
        {
          if (ChannelCreateActivity.this.nameTextView.length() == 0)
          {
            localObject = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
            ((AlertDialog.Builder)localObject).setTitle(LocaleController.getString("AppName", 2131165319));
            ((AlertDialog.Builder)localObject).setMessage(LocaleController.getString("ChannelPublicEmptyUsername", 2131165510));
            ((AlertDialog.Builder)localObject).setPositiveButton(LocaleController.getString("Close", 2131165556), null);
            ChannelCreateActivity.this.showDialog(((AlertDialog.Builder)localObject).create());
            return;
          }
          if (!ChannelCreateActivity.this.lastNameAvailable)
          {
            localObject = (Vibrator)ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
            if (localObject != null)
              ((Vibrator)localObject).vibrate(200L);
            AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView, 2.0F, 0);
            return;
          }
          MessagesController.getInstance().updateChannelUserName(ChannelCreateActivity.this.chatId, ChannelCreateActivity.this.lastCheckName);
        }
        Object localObject = new Bundle();
        ((Bundle)localObject).putInt("step", 2);
        ((Bundle)localObject).putInt("chatId", ChannelCreateActivity.this.chatId);
        ((Bundle)localObject).putInt("chatType", 2);
        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity((Bundle)localObject), true);
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.fragmentView = new ScrollView(paramContext);
    Object localObject1 = (ScrollView)this.fragmentView;
    ((ScrollView)localObject1).setFillViewport(true);
    this.linearLayout = new LinearLayout(paramContext);
    this.linearLayout.setOrientation(1);
    ((ScrollView)localObject1).addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
    float f1;
    label272: float f2;
    if (this.currentStep == 0)
    {
      this.actionBar.setTitle(LocaleController.getString("NewChannel", 2131166007));
      this.fragmentView.setTag("windowBackgroundWhite");
      this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      localObject1 = new FrameLayout(paramContext);
      this.linearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2));
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
      this.avatarDrawable.setInfo(5, null, null, false);
      this.avatarDrawable.setDrawPhoto(true);
      this.avatarImage.setImageDrawable(this.avatarDrawable);
      localObject2 = this.avatarImage;
      if (LocaleController.isRTL)
      {
        i = 5;
        if (!LocaleController.isRTL)
          break label940;
        f1 = 0.0F;
        if (!LocaleController.isRTL)
          break label947;
        f2 = 16.0F;
        label282: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 12.0F, f2, 12.0F));
        this.avatarImage.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (ChannelCreateActivity.this.getParentActivity() == null)
              return;
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
            if (ChannelCreateActivity.this.avatar != null)
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
                    ChannelCreateActivity.this.avatarUpdater.openCamera();
                  do
                  {
                    return;
                    if (paramInt != 1)
                      continue;
                    ChannelCreateActivity.this.avatarUpdater.openGallery();
                    return;
                  }
                  while (paramInt != 2);
                  ChannelCreateActivity.access$1302(ChannelCreateActivity.this, null);
                  ChannelCreateActivity.access$1402(ChannelCreateActivity.this, null);
                  ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
                }
              });
              ChannelCreateActivity.this.showDialog(localBuilder.create());
              return;
              paramView = new CharSequence[2];
              paramView[0] = LocaleController.getString("FromCamera", 2131165779);
              paramView[1] = LocaleController.getString("FromGalley", 2131165786);
            }
          }
        });
        this.nameTextView = new EditText(paramContext);
        this.nameTextView.setHint(LocaleController.getString("EnterChannelName", 2131165693));
        if (this.nameToSet != null)
        {
          this.nameTextView.setText(this.nameToSet);
          this.nameToSet = null;
        }
        this.nameTextView.setMaxLines(4);
        localObject2 = this.nameTextView;
        if (!LocaleController.isRTL)
          break label952;
        i = 5;
        label399: ((EditText)localObject2).setGravity(i | 0x10);
        this.nameTextView.setTextSize(1, 16.0F);
        this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setImeOptions(268435456);
        this.nameTextView.setInputType(16385);
        this.nameTextView.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
        localObject2 = new InputFilter.LengthFilter(100);
        this.nameTextView.setFilters(new InputFilter[] { localObject2 });
        this.nameTextView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
        AndroidUtilities.clearCursorDrawable(this.nameTextView);
        localObject2 = this.nameTextView;
        if (!LocaleController.isRTL)
          break label958;
        f1 = 16.0F;
        label544: if (!LocaleController.isRTL)
          break label965;
        f2 = 96.0F;
        label554: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
        this.nameTextView.addTextChangedListener(new TextWatcher()
        {
          public void afterTextChanged(Editable paramEditable)
          {
            AvatarDrawable localAvatarDrawable = ChannelCreateActivity.this.avatarDrawable;
            if (ChannelCreateActivity.this.nameTextView.length() > 0);
            for (paramEditable = ChannelCreateActivity.this.nameTextView.getText().toString(); ; paramEditable = null)
            {
              localAvatarDrawable.setInfo(5, paramEditable, null, false);
              ChannelCreateActivity.this.avatarImage.invalidate();
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
        this.descriptionTextView = new EditText(paramContext);
        this.descriptionTextView.setTextSize(1, 18.0F);
        this.descriptionTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
        this.descriptionTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.descriptionTextView.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
        this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0F));
        localObject1 = this.descriptionTextView;
        if (!LocaleController.isRTL)
          break label972;
        i = 5;
        label681: ((EditText)localObject1).setGravity(i);
        this.descriptionTextView.setInputType(180225);
        this.descriptionTextView.setImeOptions(6);
        localObject1 = new InputFilter.LengthFilter(120);
        this.descriptionTextView.setFilters(new InputFilter[] { localObject1 });
        this.descriptionTextView.setHint(LocaleController.getString("DescriptionPlaceholder", 2131165654));
        AndroidUtilities.clearCursorDrawable(this.descriptionTextView);
        this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0F, 18.0F, 24.0F, 0.0F));
        this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
          public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
          {
            if ((paramInt == 6) && (ChannelCreateActivity.this.doneButton != null))
            {
              ChannelCreateActivity.this.doneButton.performClick();
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
        this.helpTextView = new TextView(paramContext);
        this.helpTextView.setTextSize(1, 15.0F);
        this.helpTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText8"));
        paramContext = this.helpTextView;
        if (!LocaleController.isRTL)
          break label978;
        i = 5;
        label864: paramContext.setGravity(i);
        this.helpTextView.setText(LocaleController.getString("DescriptionInfo", 2131165651));
        paramContext = this.linearLayout;
        localObject1 = this.helpTextView;
        if (!LocaleController.isRTL)
          break label984;
        i = 5;
        label906: paramContext.addView((View)localObject1, LayoutHelper.createLinear(-2, -2, i, 24, 10, 24, 20));
      }
    }
    label940: label947: label952: label958: label965: label972: label978: label984: 
    do
    {
      return this.fragmentView;
      i = 3;
      break;
      f1 = 16.0F;
      break label272;
      f2 = 0.0F;
      break label282;
      i = 3;
      break label399;
      f1 = 96.0F;
      break label544;
      f2 = 16.0F;
      break label554;
      i = 3;
      break label681;
      i = 3;
      break label864;
      i = 3;
      break label906;
    }
    while (this.currentStep != 1);
    this.actionBar.setTitle(LocaleController.getString("ChannelSettings", 2131165514));
    this.fragmentView.setTag("windowBackgroundGray");
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.linearLayout2 = new LinearLayout(paramContext);
    this.linearLayout2.setOrientation(1);
    this.linearLayout2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
    this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
    this.radioButtonCell1 = new RadioButtonCell(paramContext);
    this.radioButtonCell1.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    localObject1 = this.radioButtonCell1;
    Object localObject2 = LocaleController.getString("ChannelPublic", 2131165509);
    String str = LocaleController.getString("ChannelPublicInfo", 2131165511);
    boolean bool;
    if (!this.isPrivate)
    {
      bool = true;
      label1148: ((RadioButtonCell)localObject1).setTextAndValue((String)localObject2, str, bool);
      this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell1.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (!ChannelCreateActivity.this.isPrivate)
            return;
          ChannelCreateActivity.access$802(ChannelCreateActivity.this, false);
          ChannelCreateActivity.this.updatePrivatePublic();
        }
      });
      this.radioButtonCell2 = new RadioButtonCell(paramContext);
      this.radioButtonCell2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.radioButtonCell2.setTextAndValue(LocaleController.getString("ChannelPrivate", 2131165506), LocaleController.getString("ChannelPrivateInfo", 2131165507), this.isPrivate);
      this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
      this.radioButtonCell2.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ChannelCreateActivity.this.isPrivate)
            return;
          ChannelCreateActivity.access$802(ChannelCreateActivity.this, true);
          ChannelCreateActivity.this.updatePrivatePublic();
        }
      });
      this.sectionCell = new ShadowSectionCell(paramContext);
      this.linearLayout.addView(this.sectionCell, LayoutHelper.createLinear(-1, -2));
      this.linkContainer = new LinearLayout(paramContext);
      this.linkContainer.setOrientation(1);
      this.linkContainer.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
      this.headerCell = new HeaderCell(paramContext);
      this.linkContainer.addView(this.headerCell);
      this.publicContainer = new LinearLayout(paramContext);
      this.publicContainer.setOrientation(0);
      this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 17.0F, 7.0F, 17.0F, 0.0F));
      this.editText = new EditText(paramContext);
      this.editText.setText(MessagesController.getInstance().linkPrefix + "/");
      this.editText.setTextSize(1, 18.0F);
      this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setMaxLines(1);
      this.editText.setLines(1);
      this.editText.setEnabled(false);
      this.editText.setBackgroundDrawable(null);
      this.editText.setPadding(0, 0, 0, 0);
      this.editText.setSingleLine(true);
      this.editText.setInputType(163840);
      this.editText.setImeOptions(6);
      this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
      this.nameTextView = new EditText(paramContext);
      this.nameTextView.setTextSize(1, 18.0F);
      this.nameTextView.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setLines(1);
      this.nameTextView.setBackgroundDrawable(null);
      this.nameTextView.setPadding(0, 0, 0, 0);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setInputType(163872);
      this.nameTextView.setImeOptions(6);
      this.nameTextView.setHint(LocaleController.getString("ChannelUsernamePlaceholder", 2131165528));
      AndroidUtilities.clearCursorDrawable(this.nameTextView);
      this.publicContainer.addView(this.nameTextView, LayoutHelper.createLinear(-1, 36));
      this.nameTextView.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
          ChannelCreateActivity.this.checkUserName(ChannelCreateActivity.this.nameTextView.getText().toString());
        }
      });
      this.privateContainer = new TextBlockCell(paramContext);
      this.privateContainer.setBackgroundDrawable(Theme.getSelectorDrawable(false));
      this.linkContainer.addView(this.privateContainer);
      this.privateContainer.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (ChannelCreateActivity.this.invite == null)
            return;
          try
          {
            ((ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", ChannelCreateActivity.this.invite.link));
            Toast.makeText(ChannelCreateActivity.this.getParentActivity(), LocaleController.getString("LinkCopied", 2131165909), 0).show();
            return;
          }
          catch (Exception paramView)
          {
            FileLog.e(paramView);
          }
        }
      });
      this.checkTextView = new TextView(paramContext);
      this.checkTextView.setTextSize(1, 15.0F);
      localObject1 = this.checkTextView;
      if (!LocaleController.isRTL)
        break label2090;
      i = 5;
      label1846: ((TextView)localObject1).setGravity(i);
      this.checkTextView.setVisibility(8);
      localObject1 = this.linkContainer;
      localObject2 = this.checkTextView;
      if (!LocaleController.isRTL)
        break label2096;
    }
    label2090: label2096: for (int i = 5; ; i = 3)
    {
      ((LinearLayout)localObject1).addView((View)localObject2, LayoutHelper.createLinear(-2, -2, i, 17, 3, 17, 7));
      this.typeInfoCell = new TextInfoPrivacyCell(paramContext);
      this.typeInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837726, "windowBackgroundGrayShadow"));
      this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
      this.loadingAdminedCell = new LoadingCell(paramContext);
      this.linearLayout.addView(this.loadingAdminedCell, LayoutHelper.createLinear(-1, -2));
      this.adminnedChannelsLayout = new LinearLayout(paramContext);
      this.adminnedChannelsLayout.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      this.adminnedChannelsLayout.setOrientation(1);
      this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
      this.adminedInfoCell = new TextInfoPrivacyCell(paramContext);
      this.adminedInfoCell.setBackgroundDrawable(Theme.getThemedDrawable(paramContext, 2130837726, "windowBackgroundGrayShadow"));
      this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
      updatePrivatePublic();
      break;
      bool = false;
      break label1148;
      i = 3;
      break label1846;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.chatDidFailCreate)
      if (this.progressDialog == null);
    do
      try
      {
        this.progressDialog.dismiss();
        this.donePressed = false;
        return;
      }
      catch (Exception paramArrayOfObject)
      {
        while (true)
          FileLog.e(paramArrayOfObject);
      }
    while (paramInt != NotificationCenter.chatDidCreated);
    if (this.progressDialog != null);
    try
    {
      this.progressDialog.dismiss();
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      paramArrayOfObject = new Bundle();
      paramArrayOfObject.putInt("step", 1);
      paramArrayOfObject.putInt("chat_id", paramInt);
      paramArrayOfObject.putBoolean("canCreatePublic", this.canCreatePublic);
      if (this.uploadedAvatar != null)
        MessagesController.getInstance().changeChatAvatar(paramInt, this.uploadedAvatar);
      presentFragment(new ChannelCreateActivity(paramArrayOfObject), true);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInputFile, paramPhotoSize1)
    {
      public void run()
      {
        ChannelCreateActivity.access$1402(ChannelCreateActivity.this, this.val$file);
        ChannelCreateActivity.access$1302(ChannelCreateActivity.this, this.val$small.location);
        ChannelCreateActivity.this.avatarImage.setImage(ChannelCreateActivity.this.avatar, "50_50", ChannelCreateActivity.this.avatarDrawable);
        if (ChannelCreateActivity.this.createAfterUpload);
        try
        {
          if ((ChannelCreateActivity.this.progressDialog != null) && (ChannelCreateActivity.this.progressDialog.isShowing()))
          {
            ChannelCreateActivity.this.progressDialog.dismiss();
            ChannelCreateActivity.access$602(ChannelCreateActivity.this, null);
          }
          ChannelCreateActivity.this.doneButton.performClick();
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
    15 local15 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        if (ChannelCreateActivity.this.adminnedChannelsLayout != null)
        {
          int i = ChannelCreateActivity.this.adminnedChannelsLayout.getChildCount();
          paramInt = 0;
          while (paramInt < i)
          {
            localObject = ChannelCreateActivity.this.adminnedChannelsLayout.getChildAt(paramInt);
            if ((localObject instanceof AdminedChannelCell))
              ((AdminedChannelCell)localObject).update();
            paramInt += 1;
          }
        }
        AvatarDrawable localAvatarDrawable;
        if (ChannelCreateActivity.this.avatarImage != null)
        {
          localAvatarDrawable = ChannelCreateActivity.this.avatarDrawable;
          if (ChannelCreateActivity.this.nameTextView.length() <= 0)
            break label127;
        }
        label127: for (Object localObject = ChannelCreateActivity.this.nameTextView.getText().toString(); ; localObject = null)
        {
          localAvatarDrawable.setInfo(5, (String)localObject, null, false);
          ChannelCreateActivity.this.avatarImage.invalidate();
          return;
        }
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.helpTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.sectionCell, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.headerCell, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGrayText8"), new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, "windowBackgroundWhiteGreenText"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText4"), new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.privateContainer, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.privateContainer, 0, new Class[] { TextBlockCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.loadingAdminedCell, 0, new Class[] { LoadingCell.class }, new String[] { "progressBar" }, null, null, null, "progressCircle"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackground"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { RadioButtonCell.class }, new String[] { "radioButton" }, null, null, null, "radioBackgroundChecked"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { RadioButtonCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "statusTextView" }, null, null, null, "windowBackgroundWhiteLinkText"), new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { AdminedChannelCell.class }, new String[] { "deleteButton" }, null, null, null, "windowBackgroundWhiteGrayText"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local15, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local15, "avatar_backgroundPink") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
    if (this.currentStep == 1)
      generateLink();
    if (this.avatarUpdater != null)
    {
      this.avatarUpdater.parentFragment = this;
      this.avatarUpdater.delegate = this;
    }
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
    if (this.avatarUpdater != null)
      this.avatarUpdater.clear();
    AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onResume()
  {
    super.onResume();
    AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.currentStep != 1))
    {
      this.nameTextView.requestFocus();
      AndroidUtilities.showKeyboard(this.nameTextView);
    }
  }

  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.currentStep == 0)
    {
      if (this.avatarUpdater != null)
        this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
      paramBundle = paramBundle.getString("nameTextView");
      if (paramBundle != null)
      {
        if (this.nameTextView == null)
          break label56;
        this.nameTextView.setText(paramBundle);
      }
    }
    return;
    label56: this.nameToSet = paramBundle;
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    if (this.currentStep == 0)
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
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ChannelCreateActivity
 * JD-Core Version:    0.6.0
 */