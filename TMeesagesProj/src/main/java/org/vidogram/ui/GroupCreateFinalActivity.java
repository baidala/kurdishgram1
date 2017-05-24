package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Semaphore;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Cells.GroupCreateSectionCell;
import org.vidogram.ui.Cells.GroupCreateUserCell;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.AvatarUpdater;
import org.vidogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.ContextProgressView;
import org.vidogram.ui.Components.GroupCreateDividerItemDecoration;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class GroupCreateFinalActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, AvatarUpdater.AvatarUpdaterDelegate
{
  private static final int done_button = 1;
  private GroupCreateAdapter adapter;
  private TLRPC.FileLocation avatar;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int chatType = 0;
  private boolean createAfterUpload;
  private ActionBarMenuItem doneItem;
  private AnimatorSet doneItemAnimation;
  private boolean donePressed;
  private EditText editText;
  private FrameLayout editTextContainer;
  private RecyclerView listView;
  private String nameToSet;
  private ContextProgressView progressView;
  private int reqId;
  private ArrayList<Integer> selectedContacts;
  private TLRPC.InputFile uploadedAvatar;

  public GroupCreateFinalActivity(Bundle paramBundle)
  {
    super(paramBundle);
    this.chatType = paramBundle.getInt("chatType", 0);
    this.avatarDrawable = new AvatarDrawable();
  }

  private void showEditDoneProgress(boolean paramBoolean)
  {
    if (this.doneItem == null)
      return;
    if (this.doneItemAnimation != null)
      this.doneItemAnimation.cancel();
    this.doneItemAnimation = new AnimatorSet();
    if (paramBoolean)
    {
      this.progressView.setVisibility(0);
      this.doneItem.setEnabled(false);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 1.0F }) });
    }
    while (true)
    {
      this.doneItemAnimation.addListener(new AnimatorListenerAdapter(paramBoolean)
      {
        public void onAnimationCancel(Animator paramAnimator)
        {
          if ((GroupCreateFinalActivity.this.doneItemAnimation != null) && (GroupCreateFinalActivity.this.doneItemAnimation.equals(paramAnimator)))
            GroupCreateFinalActivity.access$1502(GroupCreateFinalActivity.this, null);
        }

        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((GroupCreateFinalActivity.this.doneItemAnimation != null) && (GroupCreateFinalActivity.this.doneItemAnimation.equals(paramAnimator)))
          {
            if (!this.val$show)
              GroupCreateFinalActivity.this.progressView.setVisibility(4);
          }
          else
            return;
          GroupCreateFinalActivity.this.doneItem.getImageView().setVisibility(4);
        }
      });
      this.doneItemAnimation.setDuration(150L);
      this.doneItemAnimation.start();
      return;
      this.doneItem.getImageView().setVisibility(0);
      this.doneItem.setEnabled(true);
      this.doneItemAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.progressView, "scaleX", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "scaleY", new float[] { 0.1F }), ObjectAnimator.ofFloat(this.progressView, "alpha", new float[] { 0.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.doneItem.getImageView(), "alpha", new float[] { 1.0F }) });
    }
  }

  public View createView(Context paramContext)
  {
    int j = 1;
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("NewGroup", 2131166009));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          GroupCreateFinalActivity.this.finishFragment();
        do
          return;
        while ((paramInt != 1) || (GroupCreateFinalActivity.this.donePressed));
        if (GroupCreateFinalActivity.this.editText.length() == 0)
        {
          Vibrator localVibrator = (Vibrator)GroupCreateFinalActivity.this.getParentActivity().getSystemService("vibrator");
          if (localVibrator != null)
            localVibrator.vibrate(200L);
          AndroidUtilities.shakeView(GroupCreateFinalActivity.this.editText, 2.0F, 0);
          return;
        }
        GroupCreateFinalActivity.access$002(GroupCreateFinalActivity.this, true);
        AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
        GroupCreateFinalActivity.this.editText.setEnabled(false);
        if (GroupCreateFinalActivity.this.avatarUpdater.uploadingAvatar != null)
        {
          GroupCreateFinalActivity.access$302(GroupCreateFinalActivity.this, true);
          return;
        }
        GroupCreateFinalActivity.this.showEditDoneProgress(true);
        GroupCreateFinalActivity.access$502(GroupCreateFinalActivity.this, MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this));
      }
    });
    this.doneItem = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    this.progressView = new ContextProgressView(paramContext, 1);
    this.doneItem.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.progressView.setVisibility(4);
    this.fragmentView = new LinearLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
      {
        boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
        if (paramView == GroupCreateFinalActivity.this.listView)
          GroupCreateFinalActivity.this.parentLayout.drawHeaderShadow(paramCanvas, GroupCreateFinalActivity.this.editTextContainer.getMeasuredHeight());
        return bool;
      }
    };
    LinearLayout localLinearLayout = (LinearLayout)this.fragmentView;
    localLinearLayout.setOrientation(1);
    this.editTextContainer = new FrameLayout(paramContext);
    localLinearLayout.addView(this.editTextContainer, LayoutHelper.createLinear(-1, -2));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(32.0F));
    Object localObject1 = this.avatarDrawable;
    boolean bool;
    label251: float f1;
    label259: float f2;
    if (this.chatType == 1)
    {
      bool = true;
      ((AvatarDrawable)localObject1).setInfo(5, null, null, bool);
      this.avatarImage.setImageDrawable(this.avatarDrawable);
      localObject1 = this.editTextContainer;
      Object localObject2 = this.avatarImage;
      if (!LocaleController.isRTL)
        break label748;
      i = 5;
      if (!LocaleController.isRTL)
        break label754;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label761;
      f2 = 16.0F;
      label269: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(64, 64.0F, i | 0x30, f1, 16.0F, f2, 16.0F));
      this.avatarDrawable.setDrawPhoto(true);
      this.avatarImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (GroupCreateFinalActivity.this.getParentActivity() == null)
            return;
          AlertDialog.Builder localBuilder = new AlertDialog.Builder(GroupCreateFinalActivity.this.getParentActivity());
          if (GroupCreateFinalActivity.this.avatar != null)
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
                  GroupCreateFinalActivity.this.avatarUpdater.openCamera();
                do
                {
                  return;
                  if (paramInt != 1)
                    continue;
                  GroupCreateFinalActivity.this.avatarUpdater.openGallery();
                  return;
                }
                while (paramInt != 2);
                GroupCreateFinalActivity.access$1102(GroupCreateFinalActivity.this, null);
                GroupCreateFinalActivity.access$1202(GroupCreateFinalActivity.this, null);
                GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
              }
            });
            GroupCreateFinalActivity.this.showDialog(localBuilder.create());
            return;
            paramView = new CharSequence[2];
            paramView[0] = LocaleController.getString("FromCamera", 2131165779);
            paramView[1] = LocaleController.getString("FromGalley", 2131165786);
          }
        }
      });
      this.editText = new EditText(paramContext);
      localObject2 = this.editText;
      if (this.chatType != 0)
        break label766;
      localObject1 = LocaleController.getString("EnterGroupNamePlaceholder", 2131165695);
      label356: ((EditText)localObject2).setHint((CharSequence)localObject1);
      if (this.nameToSet != null)
      {
        this.editText.setText(this.nameToSet);
        this.nameToSet = null;
      }
      this.editText.setMaxLines(4);
      localObject1 = this.editText;
      if (!LocaleController.isRTL)
        break label780;
      i = 5;
      label409: ((EditText)localObject1).setGravity(i | 0x10);
      this.editText.setTextSize(1, 18.0F);
      this.editText.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.editText.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.editText.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.editText.setImeOptions(268435456);
      this.editText.setInputType(16384);
      this.editText.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
      localObject1 = new InputFilter.LengthFilter(100);
      this.editText.setFilters(new InputFilter[] { localObject1 });
      AndroidUtilities.clearCursorDrawable(this.editText);
      localObject1 = this.editTextContainer;
      localObject2 = this.editText;
      if (!LocaleController.isRTL)
        break label786;
      f1 = 16.0F;
      label560: if (!LocaleController.isRTL)
        break label793;
      f2 = 96.0F;
      label570: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-1, -2.0F, 16, f1, 0.0F, f2, 0.0F));
      this.editText.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
          AvatarDrawable localAvatarDrawable = GroupCreateFinalActivity.this.avatarDrawable;
          if (GroupCreateFinalActivity.this.editText.length() > 0);
          for (paramEditable = GroupCreateFinalActivity.this.editText.getText().toString(); ; paramEditable = null)
          {
            localAvatarDrawable.setInfo(5, paramEditable, null, false);
            GroupCreateFinalActivity.this.avatarImage.invalidate();
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
      localObject1 = new LinearLayoutManager(paramContext, 1, false);
      this.listView = new RecyclerListView(paramContext);
      localObject2 = this.listView;
      paramContext = new GroupCreateAdapter(paramContext);
      this.adapter = paramContext;
      ((RecyclerView)localObject2).setAdapter(paramContext);
      this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject1);
      this.listView.setVerticalScrollBarEnabled(false);
      paramContext = this.listView;
      if (!LocaleController.isRTL)
        break label800;
    }
    label780: label786: label793: label800: for (int i = j; ; i = 2)
    {
      paramContext.setVerticalScrollbarPosition(i);
      this.listView.addItemDecoration(new GroupCreateDividerItemDecoration());
      localLinearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
      this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
        {
          if (paramInt == 1)
            AndroidUtilities.hideKeyboard(GroupCreateFinalActivity.this.editText);
        }
      });
      return this.fragmentView;
      bool = false;
      break;
      label748: i = 3;
      break label251;
      label754: f1 = 16.0F;
      break label259;
      label761: f2 = 0.0F;
      break label269;
      label766: localObject1 = LocaleController.getString("EnterListName", 2131165696);
      break label356;
      i = 3;
      break label409;
      f1 = 96.0F;
      break label560;
      f2 = 16.0F;
      break label570;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    int i = 0;
    if (paramInt == NotificationCenter.updateInterfaces)
      if (this.listView != null);
    do
    {
      do
        while (true)
        {
          return;
          int j = ((Integer)paramArrayOfObject[0]).intValue();
          if (((j & 0x2) == 0) && ((j & 0x1) == 0) && ((j & 0x4) == 0))
            continue;
          int k = this.listView.getChildCount();
          paramInt = i;
          while (paramInt < k)
          {
            paramArrayOfObject = this.listView.getChildAt(paramInt);
            if ((paramArrayOfObject instanceof GroupCreateUserCell))
              ((GroupCreateUserCell)paramArrayOfObject).update(j);
            paramInt += 1;
          }
          continue;
          if (paramInt != NotificationCenter.chatDidFailCreate)
            break;
          this.reqId = 0;
          this.donePressed = false;
          showEditDoneProgress(false);
          if (this.editText == null)
            continue;
          this.editText.setEnabled(true);
          return;
        }
      while (paramInt != NotificationCenter.chatDidCreated);
      this.reqId = 0;
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.closeChats, new Object[0]);
      paramArrayOfObject = new Bundle();
      paramArrayOfObject.putInt("chat_id", paramInt);
      presentFragment(new ChatActivity(paramArrayOfObject), true);
    }
    while (this.uploadedAvatar == null);
    MessagesController.getInstance().changeChatAvatar(paramInt, this.uploadedAvatar);
  }

  public void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramInputFile, paramPhotoSize1)
    {
      public void run()
      {
        GroupCreateFinalActivity.access$1202(GroupCreateFinalActivity.this, this.val$file);
        GroupCreateFinalActivity.access$1102(GroupCreateFinalActivity.this, this.val$small.location);
        GroupCreateFinalActivity.this.avatarImage.setImage(GroupCreateFinalActivity.this.avatar, "50_50", GroupCreateFinalActivity.this.avatarDrawable);
        if (GroupCreateFinalActivity.this.createAfterUpload)
          MessagesController.getInstance().createChat(GroupCreateFinalActivity.this.editText.getText().toString(), GroupCreateFinalActivity.this.selectedContacts, null, GroupCreateFinalActivity.this.chatType, GroupCreateFinalActivity.this);
      }
    });
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    9 local9 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        int i = GroupCreateFinalActivity.this.listView.getChildCount();
        paramInt = 0;
        while (paramInt < i)
        {
          localObject = GroupCreateFinalActivity.this.listView.getChildAt(paramInt);
          if ((localObject instanceof GroupCreateUserCell))
            ((GroupCreateUserCell)localObject).update(0);
          paramInt += 1;
        }
        AvatarDrawable localAvatarDrawable = GroupCreateFinalActivity.this.avatarDrawable;
        if (GroupCreateFinalActivity.this.editText.length() > 0);
        for (Object localObject = GroupCreateFinalActivity.this.editText.getText().toString(); ; localObject = null)
        {
          localAvatarDrawable.setInfo(5, (String)localObject, null, false);
          GroupCreateFinalActivity.this.avatarImage.invalidate();
          return;
        }
      }
    };
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollActive");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollInactive");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, "fastScrollText");
    Object localObject1 = this.listView;
    Object localObject2 = Theme.dividerPaint;
    localObject1 = new ThemeDescription((View)localObject1, 0, new Class[] { View.class }, (Paint)localObject2, null, null, "divider");
    localObject2 = new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "groupcreate_hintText");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, "groupcreate_cursor");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField");
    ThemeDescription localThemeDescription14 = new ThemeDescription(this.editText, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GroupCreateSectionCell.class }, null, null, null, "graySection");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.listView, 0, new Class[] { GroupCreateSectionCell.class }, new String[] { "drawable" }, null, null, null, "groupcreate_sectionShadow");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateSectionCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { GroupCreateUserCell.class }, new String[] { "textView" }, null, null, null, "groupcreate_sectionText");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_onlineText");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { GroupCreateUserCell.class }, new String[] { "statusTextView" }, null, null, null, "groupcreate_offlineText");
    RecyclerView localRecyclerView = this.listView;
    Drawable localDrawable1 = Theme.avatar_photoDrawable;
    Drawable localDrawable2 = Theme.avatar_broadcastDrawable;
    return (ThemeDescription)(ThemeDescription)new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localObject1, localObject2, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localThemeDescription18, localThemeDescription19, localThemeDescription20, new ThemeDescription(localRecyclerView, 0, new Class[] { GroupCreateUserCell.class }, null, new Drawable[] { localDrawable1, localDrawable2 }, local9, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local9, "avatar_backgroundPink"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressInner2"), new ThemeDescription(this.progressView, 0, null, null, null, null, "contextProgressOuter2"), new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = this;
    this.selectedContacts = getArguments().getIntegerArrayList("result");
    Object localObject1 = new ArrayList();
    int i = 0;
    Object localObject2;
    while (i < this.selectedContacts.size())
    {
      localObject2 = (Integer)this.selectedContacts.get(i);
      if (MessagesController.getInstance().getUser((Integer)localObject2) == null)
        ((ArrayList)localObject1).add(localObject2);
      i += 1;
    }
    if (!((ArrayList)localObject1).isEmpty())
    {
      Semaphore localSemaphore = new Semaphore(0);
      localObject2 = new ArrayList();
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((ArrayList)localObject2, (ArrayList)localObject1, localSemaphore)
      {
        public void run()
        {
          this.val$users.addAll(MessagesStorage.getInstance().getUsers(this.val$usersToLoad));
          this.val$semaphore.release();
        }
      });
      try
      {
        localSemaphore.acquire();
        if (((ArrayList)localObject1).size() != ((ArrayList)localObject2).size())
          return false;
      }
      catch (Exception localException)
      {
        do
          while (true)
            FileLog.e(localException);
        while (((ArrayList)localObject2).isEmpty());
        localObject1 = ((ArrayList)localObject2).iterator();
      }
      while (((Iterator)localObject1).hasNext())
      {
        localObject2 = (TLRPC.User)((Iterator)localObject1).next();
        MessagesController.getInstance().putUser((TLRPC.User)localObject2, true);
      }
    }
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidCreated);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.chatDidFailCreate);
    this.avatarUpdater.clear();
    if (this.reqId != 0)
      ConnectionsManager.getInstance().cancelRequest(this.reqId, true);
  }

  public void onResume()
  {
    super.onResume();
    if (this.adapter != null)
      this.adapter.notifyDataSetChanged();
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.editText.requestFocus();
      AndroidUtilities.showKeyboard(this.editText);
    }
  }

  public void restoreSelfArgs(Bundle paramBundle)
  {
    if (this.avatarUpdater != null)
      this.avatarUpdater.currentPicturePath = paramBundle.getString("path");
    paramBundle = paramBundle.getString("nameTextView");
    if (paramBundle != null)
    {
      if (this.editText != null)
        this.editText.setText(paramBundle);
    }
    else
      return;
    this.nameToSet = paramBundle;
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    if ((this.avatarUpdater != null) && (this.avatarUpdater.currentPicturePath != null))
      paramBundle.putString("path", this.avatarUpdater.currentPicturePath);
    if (this.editText != null)
    {
      String str = this.editText.getText().toString();
      if ((str != null) && (str.length() != 0))
        paramBundle.putString("nameTextView", str);
    }
  }

  public class GroupCreateAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context context;

    public GroupCreateAdapter(Context arg2)
    {
      Object localObject;
      this.context = localObject;
    }

    public int getItemCount()
    {
      return GroupCreateFinalActivity.this.selectedContacts.size() + 1;
    }

    public int getItemViewType(int paramInt)
    {
      switch (paramInt)
      {
      default:
        return 1;
      case 0:
      }
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default:
        ((GroupCreateUserCell)paramViewHolder.itemView).setUser(MessagesController.getInstance().getUser((Integer)GroupCreateFinalActivity.this.selectedContacts.get(paramInt - 1)), null, null);
        return;
      case 0:
      }
      ((GroupCreateSectionCell)paramViewHolder.itemView).setText(LocaleController.formatPluralString("Members", GroupCreateFinalActivity.this.selectedContacts.size()));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
      case 0:
      }
      for (paramViewGroup = new GroupCreateUserCell(this.context, false); ; paramViewGroup = new GroupCreateSectionCell(this.context))
        return new RecyclerListView.Holder(paramViewGroup);
    }

    public void onViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (paramViewHolder.getItemViewType() == 1)
        ((GroupCreateUserCell)paramViewHolder.itemView).recycle();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.GroupCreateFinalActivity
 * JD-Core Version:    0.6.0
 */