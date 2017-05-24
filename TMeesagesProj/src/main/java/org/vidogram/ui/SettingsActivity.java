package org.vidogram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils.TruncateAt;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.browser.Browser;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.AbstractSerializedData;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_help_getSupport;
import org.vidogram.tgnet.TLRPC.TL_help_support;
import org.vidogram.tgnet.TLRPC.TL_photos_photo;
import org.vidogram.tgnet.TLRPC.TL_photos_uploadProfilePhoto;
import org.vidogram.tgnet.TLRPC.TL_userProfilePhoto;
import org.vidogram.tgnet.TLRPC.TL_userProfilePhotoEmpty;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.CheckBoxCell;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextCheckCell;
import org.vidogram.ui.Cells.TextDetailSettingsCell;
import org.vidogram.ui.Cells.TextInfoCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.AvatarUpdater;
import org.vidogram.ui.Components.AvatarUpdater.AvatarUpdaterDelegate;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CombinedDrawable;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.NumberPicker;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.URLSpanNoUnderline;

public class SettingsActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private static final int edit_name = 1;
  private static final int logout = 2;
  private int askQuestionRow;
  private int autoplayGifsRow;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private AvatarUpdater avatarUpdater = new AvatarUpdater();
  private int backgroundRow;
  private int clearLogsRow;
  private int contactsReimportRow;
  private int contactsSectionRow;
  private int contactsSortRow;
  private int customTabsRow;
  private int dataRow;
  private int directShareRow;
  private int emojiRow;
  private int emptyRow;
  private int enableAnimationsRow;
  private int extraHeight;
  private View extraHeightView;
  private int languageRow;
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int messagesSectionRow;
  private int messagesSectionRow2;
  private TextView nameTextView;
  private int notificationRow;
  private int numberRow;
  private int numberSectionRow;
  private TextView onlineTextView;
  private int overscrollRow;
  private int persionDate;
  private int privacyPolicyRow;
  private int privacyRow;
  private int raiseToSpeakRow;
  private int rowCount;
  private int saveToGalleryRow;
  private int sendByEnterRow;
  private int sendGifrEnable;
  private int sendLogsRow;
  private int sendStickerEnable;
  private int sendVoicerEnable;
  private int settingsSectionRow;
  private int settingsSectionRow2;
  private View shadowView;
  private int stickersRow;
  private int supportSectionRow;
  private int supportSectionRow2;
  private int switchBackendButtonRow;
  private int telegramFaqRow;
  private int textSizeRow;
  private int themeRow;
  private int usernameRow;
  private int versionRow;
  private int videogramSettingRow;
  private int videogramSettingRow2;
  private ImageView writeButton;
  private AnimatorSet writeButtonAnimation;

  private void fixLayout()
  {
    if (this.fragmentView == null)
      return;
    this.fragmentView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
    {
      public boolean onPreDraw()
      {
        if (SettingsActivity.this.fragmentView != null)
        {
          SettingsActivity.this.needLayout();
          SettingsActivity.this.fragmentView.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        return true;
      }
    });
  }

  private void needLayout()
  {
    int k = 0;
    float f1;
    int j;
    label138: boolean bool1;
    label172: boolean bool2;
    if (this.actionBar.getOccupyStatusBar())
    {
      int i = AndroidUtilities.statusBarHeight;
      i = ActionBar.getCurrentActionBarHeight() + i;
      Object localObject;
      if (this.listView != null)
      {
        localObject = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
        if (((FrameLayout.LayoutParams)localObject).topMargin != i)
        {
          ((FrameLayout.LayoutParams)localObject).topMargin = i;
          this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject);
          this.extraHeightView.setTranslationY(i);
        }
      }
      if (this.avatarImage != null)
      {
        f1 = this.extraHeight / AndroidUtilities.dp(88.0F);
        this.extraHeightView.setScaleY(f1);
        this.shadowView.setTranslationY(i + this.extraHeight);
        localObject = this.writeButton;
        if (!this.actionBar.getOccupyStatusBar())
          break label630;
        j = AndroidUtilities.statusBarHeight;
        ((ImageView)localObject).setTranslationY(j + ActionBar.getCurrentActionBarHeight() + this.extraHeight - AndroidUtilities.dp(29.5F));
        if (f1 <= 0.2F)
          break label635;
        bool1 = true;
        if (this.writeButton.getTag() != null)
          break label641;
        bool2 = true;
        label185: if (bool1 != bool2)
        {
          if (!bool1)
            break label647;
          this.writeButton.setTag(null);
          this.writeButton.setVisibility(0);
          label213: if (this.writeButtonAnimation != null)
          {
            localObject = this.writeButtonAnimation;
            this.writeButtonAnimation = null;
            ((AnimatorSet)localObject).cancel();
          }
          this.writeButtonAnimation = new AnimatorSet();
          if (!bool1)
            break label661;
          this.writeButtonAnimation.setInterpolator(new DecelerateInterpolator());
          this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 1.0F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 1.0F }) });
        }
      }
    }
    while (true)
    {
      this.writeButtonAnimation.setDuration(150L);
      this.writeButtonAnimation.addListener(new AnimatorListenerAdapter(bool1)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          if ((SettingsActivity.this.writeButtonAnimation != null) && (SettingsActivity.this.writeButtonAnimation.equals(paramAnimator)))
          {
            paramAnimator = SettingsActivity.this.writeButton;
            if (!this.val$setVisible)
              break label56;
          }
          label56: for (int i = 0; ; i = 8)
          {
            paramAnimator.setVisibility(i);
            SettingsActivity.access$4402(SettingsActivity.this, null);
            return;
          }
        }
      });
      this.writeButtonAnimation.start();
      this.avatarImage.setScaleX((18.0F * f1 + 42.0F) / 42.0F);
      this.avatarImage.setScaleY((18.0F * f1 + 42.0F) / 42.0F);
      j = k;
      if (this.actionBar.getOccupyStatusBar())
        j = AndroidUtilities.statusBarHeight;
      float f2 = j + ActionBar.getCurrentActionBarHeight() / 2.0F * (1.0F + f1) - 21.0F * AndroidUtilities.density + 27.0F * AndroidUtilities.density * f1;
      this.avatarImage.setTranslationX(-AndroidUtilities.dp(47.0F) * f1);
      this.avatarImage.setTranslationY((float)Math.ceil(f2));
      this.nameTextView.setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.nameTextView.setTranslationY((float)Math.floor(f2) - (float)Math.ceil(AndroidUtilities.density) + (float)Math.floor(7.0F * AndroidUtilities.density * f1));
      this.onlineTextView.setTranslationX(-21.0F * AndroidUtilities.density * f1);
      this.onlineTextView.setTranslationY((float)Math.floor(f2) + AndroidUtilities.dp(22.0F) + (float)Math.floor(11.0F * AndroidUtilities.density) * f1);
      this.nameTextView.setScaleX(0.12F * f1 + 1.0F);
      this.nameTextView.setScaleY(0.12F * f1 + 1.0F);
      return;
      j = 0;
      break;
      label630: j = 0;
      break label138;
      label635: bool1 = false;
      break label172;
      label641: bool2 = false;
      break label185;
      label647: this.writeButton.setTag(Integer.valueOf(0));
      break label213;
      label661: this.writeButtonAnimation.setInterpolator(new AccelerateInterpolator());
      this.writeButtonAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.writeButton, "scaleX", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "scaleY", new float[] { 0.2F }), ObjectAnimator.ofFloat(this.writeButton, "alpha", new float[] { 0.0F }) });
    }
  }

  private void performAskAQuestion()
  {
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    int i = localSharedPreferences.getInt("support_id", 0);
    Object localObject2;
    Object localObject1;
    Object localObject3;
    if (i != 0)
    {
      localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
      localObject1 = localObject2;
      if (localObject2 == null)
      {
        localObject3 = localSharedPreferences.getString("support_user", null);
        localObject1 = localObject2;
        if (localObject3 == null);
      }
    }
    while (true)
    {
      try
      {
        localObject3 = Base64.decode((String)localObject3, 0);
        localObject1 = localObject2;
        if (localObject3 == null)
          continue;
        localObject3 = new SerializedData(localObject3);
        localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((SerializedData)localObject3).readInt32(false), false);
        localObject1 = localObject2;
        if (localObject2 == null)
          continue;
        localObject1 = localObject2;
        if (((TLRPC.User)localObject2).id != 333000)
          continue;
        localObject1 = null;
        ((SerializedData)localObject3).cleanup();
        if (localObject1 != null)
          continue;
        localObject1 = new AlertDialog(getParentActivity(), 1);
        ((AlertDialog)localObject1).setMessage(LocaleController.getString("Loading", 2131165920));
        ((AlertDialog)localObject1).setCanceledOnTouchOutside(false);
        ((AlertDialog)localObject1).setCancelable(false);
        ((AlertDialog)localObject1).show();
        localObject2 = new TLRPC.TL_help_getSupport();
        ConnectionsManager.getInstance().sendRequest((TLObject)localObject2, new RequestDelegate(localSharedPreferences, (AlertDialog)localObject1)
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error == null)
            {
              AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_help_support)paramTLObject)
              {
                public void run()
                {
                  Object localObject = SettingsActivity.10.this.val$preferences.edit();
                  ((SharedPreferences.Editor)localObject).putInt("support_id", this.val$res.user.id);
                  SerializedData localSerializedData = new SerializedData();
                  this.val$res.user.serializeToStream(localSerializedData);
                  ((SharedPreferences.Editor)localObject).putString("support_user", Base64.encodeToString(localSerializedData.toByteArray(), 0));
                  ((SharedPreferences.Editor)localObject).commit();
                  localSerializedData.cleanup();
                  try
                  {
                    SettingsActivity.10.this.val$progressDialog.dismiss();
                    localObject = new ArrayList();
                    ((ArrayList)localObject).add(this.val$res.user);
                    MessagesStorage.getInstance().putUsersAndChats((ArrayList)localObject, null, true, true);
                    MessagesController.getInstance().putUser(this.val$res.user, false);
                    localObject = new Bundle();
                    ((Bundle)localObject).putInt("user_id", this.val$res.user.id);
                    SettingsActivity.this.presentFragment(new ChatActivity((Bundle)localObject));
                    return;
                  }
                  catch (Exception localException)
                  {
                    while (true)
                      FileLog.e(localException);
                  }
                }
              });
              return;
            }
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  SettingsActivity.10.this.val$progressDialog.dismiss();
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e(localException);
                }
              }
            });
          }
        });
        return;
      }
      catch (Exception localUser)
      {
        FileLog.e(localException);
        localUser = null;
        continue;
        MessagesController.getInstance().putUser(localUser, true);
        localObject2 = new Bundle();
        ((Bundle)localObject2).putInt("user_id", localUser.id);
        presentFragment(new ChatActivity((Bundle)localObject2));
        return;
      }
      TLRPC.User localUser = null;
    }
  }

  private void sendLogs()
  {
    try
    {
      ArrayList localArrayList = new ArrayList();
      Object localObject = ApplicationLoader.applicationContext.getExternalFilesDir(null);
      localObject = new File(((File)localObject).getAbsolutePath() + "/logs").listFiles();
      int j = localObject.length;
      int i = 0;
      while (i < j)
      {
        localArrayList.add(Uri.fromFile(localObject[i]));
        i += 1;
      }
      if (localArrayList.isEmpty())
        return;
      localObject = new Intent("android.intent.action.SEND_MULTIPLE");
      ((Intent)localObject).setType("message/rfc822");
      ((Intent)localObject).putExtra("android.intent.extra.EMAIL", "");
      ((Intent)localObject).putExtra("android.intent.extra.SUBJECT", "last logs");
      ((Intent)localObject).putParcelableArrayListExtra("android.intent.extra.STREAM", localArrayList);
      getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject, "Select email application."), 500);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  private void updateUserData()
  {
    TLRPC.FileLocation localFileLocation = null;
    boolean bool2 = true;
    TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    Object localObject;
    if (localUser.photo != null)
    {
      localObject = localUser.photo.photo_small;
      localFileLocation = localUser.photo.photo_big;
    }
    while (true)
    {
      this.avatarDrawable = new AvatarDrawable(localUser, true);
      this.avatarDrawable.setColor(Theme.getColor("avatar_backgroundInProfileBlue"));
      if (this.avatarImage != null)
      {
        this.avatarImage.setImage((TLObject)localObject, "50_50", this.avatarDrawable);
        localObject = this.avatarImage.getImageReceiver();
        if (PhotoViewer.getInstance().isShowingImage(localFileLocation))
          break label180;
        bool1 = true;
        ((ImageReceiver)localObject).setVisible(bool1, false);
        this.nameTextView.setText(UserObject.getUserName(localUser));
        this.onlineTextView.setText(LocaleController.getString("Online", 2131166155));
        localObject = this.avatarImage.getImageReceiver();
        if (PhotoViewer.getInstance().isShowingImage(localFileLocation))
          break label185;
      }
      label180: label185: for (boolean bool1 = bool2; ; bool1 = false)
      {
        ((ImageReceiver)localObject).setVisible(bool1, false);
        return;
        bool1 = false;
        break;
      }
      localObject = null;
    }
  }

  public boolean allowCaption()
  {
    return true;
  }

  public boolean cancelButtonPressed()
  {
    return true;
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue"), false);
    this.actionBar.setItemsColor(Theme.getColor("avatar_actionBarIconBlue"), false);
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAddToContainer(false);
    this.extraHeight = 88;
    if (AndroidUtilities.isTablet())
      this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          SettingsActivity.this.finishFragment();
        do
        {
          return;
          if (paramInt != 1)
            continue;
          SettingsActivity.this.presentFragment(new ChangeNameActivity());
          return;
        }
        while ((paramInt != 2) || (SettingsActivity.this.getParentActivity() == null));
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
        localBuilder.setMessage(LocaleController.getString("AreYouSureLogout", 2131165346));
        localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
        localBuilder.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramDialogInterface, int paramInt)
          {
            MessagesController.getInstance().performLogout(true);
          }
        });
        localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
        SettingsActivity.this.showDialog(localBuilder.create());
      }
    });
    Object localObject = this.actionBar.createMenu().addItem(0, 2130837738);
    ((ActionBarMenuItem)localObject).addSubItem(1, LocaleController.getString("EditName", 2131165667));
    ((ActionBarMenuItem)localObject).addSubItem(2, LocaleController.getString("LogOut", 2131165931));
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext)
    {
      protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
      {
        boolean bool;
        View localView;
        if (paramView == SettingsActivity.this.listView)
        {
          bool = super.drawChild(paramCanvas, paramView, paramLong);
          if (SettingsActivity.this.parentLayout != null)
          {
            int j = getChildCount();
            i = 0;
            if (i >= j)
              break label127;
            localView = getChildAt(i);
            if (localView == paramView);
            do
            {
              i += 1;
              break;
            }
            while ((!(localView instanceof ActionBar)) || (localView.getVisibility() != 0));
            if (!((ActionBar)localView).getCastShadows())
              break label127;
          }
        }
        label127: for (int i = localView.getMeasuredHeight(); ; i = 0)
        {
          SettingsActivity.this.parentLayout.drawHeaderShadow(paramCanvas, i);
          return bool;
          return super.drawChild(paramCanvas, paramView, paramLong);
        }
      }
    };
    this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    localObject = this.listView;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = localLinearLayoutManager;
    ((RecyclerListView)localObject).setLayoutManager(localLinearLayoutManager);
    this.listView.setGlowColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        boolean bool3 = true;
        boolean bool4 = true;
        boolean bool5 = true;
        boolean bool6 = true;
        boolean bool7 = true;
        boolean bool2 = true;
        if (paramInt == SettingsActivity.this.textSizeRow)
          if (SettingsActivity.this.getParentActivity() != null);
        label256: label341: label511: int j;
        label426: label596: label1114: Object localObject4;
        do
        {
          while (true)
          {
            return;
            paramView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("TextSize", 2131166509));
            localObject1 = new NumberPicker(SettingsActivity.this.getParentActivity());
            ((NumberPicker)localObject1).setMinValue(12);
            ((NumberPicker)localObject1).setMaxValue(30);
            ((NumberPicker)localObject1).setValue(MessagesController.getInstance().fontSize);
            paramView.setView((View)localObject1);
            paramView.setNegativeButton(LocaleController.getString("Done", 2131165661), new DialogInterface.OnClickListener((NumberPicker)localObject1, paramInt)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                paramDialogInterface.putInt("fons_size", this.val$numberPicker.getValue());
                MessagesController.getInstance().fontSize = this.val$numberPicker.getValue();
                paramDialogInterface.commit();
                if (SettingsActivity.this.listAdapter != null)
                  SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
              }
            });
            SettingsActivity.this.showDialog(paramView.create());
            return;
            boolean bool1;
            if (paramInt == SettingsActivity.this.enableAnimationsRow)
            {
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              bool3 = ((SharedPreferences)localObject1).getBoolean("view_animations", true);
              localObject1 = ((SharedPreferences)localObject1).edit();
              if (!bool3)
              {
                bool1 = true;
                ((SharedPreferences.Editor)localObject1).putBoolean("view_animations", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool3)
                  break label256;
              }
              for (bool1 = bool2; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.persionDate)
            {
              bool2 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).e();
              localObject1 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext);
              if (!bool2)
              {
                bool1 = true;
                ((itman.Vidofilm.b)localObject1).c(bool1);
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool2)
                  break label341;
              }
              for (bool1 = bool3; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.sendStickerEnable)
            {
              bool2 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).h();
              localObject1 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext);
              if (!bool2)
              {
                bool1 = true;
                ((itman.Vidofilm.b)localObject1).f(bool1);
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool2)
                  break label426;
              }
              for (bool1 = bool4; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.sendVoicerEnable)
            {
              bool2 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).g();
              localObject1 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext);
              if (!bool2)
              {
                bool1 = true;
                ((itman.Vidofilm.b)localObject1).e(bool1);
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool2)
                  break label511;
              }
              for (bool1 = bool5; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.sendGifrEnable)
            {
              bool2 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext).f();
              localObject1 = itman.Vidofilm.b.a(ApplicationLoader.applicationContext);
              if (!bool2)
              {
                bool1 = true;
                ((itman.Vidofilm.b)localObject1).d(bool1);
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool2)
                  break label596;
              }
              for (bool1 = bool6; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.notificationRow)
            {
              SettingsActivity.this.presentFragment(new NotificationsSettingsActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.backgroundRow)
            {
              SettingsActivity.this.presentFragment(new WallpapersActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.askQuestionRow)
            {
              if (SettingsActivity.this.getParentActivity() == null)
                continue;
              paramView = new TextView(SettingsActivity.this.getParentActivity());
              localObject1 = new SpannableString(Html.fromHtml(LocaleController.getString("AskAQuestionInfo", 2131165357)));
              localObject2 = (URLSpan[])((Spannable)localObject1).getSpans(0, ((Spannable)localObject1).length(), URLSpan.class);
              paramInt = 0;
              while (paramInt < localObject2.length)
              {
                localObject3 = localObject2[paramInt];
                i = ((Spannable)localObject1).getSpanStart(localObject3);
                j = ((Spannable)localObject1).getSpanEnd(localObject3);
                ((Spannable)localObject1).removeSpan(localObject3);
                ((Spannable)localObject1).setSpan(new URLSpanNoUnderline(((URLSpan)localObject3).getURL()), i, j, 0);
                paramInt += 1;
              }
              paramView.setText((CharSequence)localObject1);
              paramView.setTextSize(1, 16.0F);
              paramView.setLinkTextColor(Theme.getColor("dialogTextLink"));
              paramView.setHighlightColor(Theme.getColor("dialogLinkSelection"));
              paramView.setPadding(AndroidUtilities.dp(23.0F), 0, AndroidUtilities.dp(23.0F), 0);
              paramView.setMovementMethod(new SettingsActivity.LinkMovementMethodMy(null));
              paramView.setTextColor(Theme.getColor("dialogTextBlack"));
              localObject1 = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
              ((AlertDialog.Builder)localObject1).setView(paramView);
              ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AskAQuestion", 2131165356));
              ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("AskButton", 2131165358), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  SettingsActivity.this.performAskAQuestion();
                }
              });
              ((AlertDialog.Builder)localObject1).setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              SettingsActivity.this.showDialog(((AlertDialog.Builder)localObject1).create());
              return;
            }
            if (paramInt == SettingsActivity.this.sendLogsRow)
            {
              SettingsActivity.this.sendLogs();
              return;
            }
            if (paramInt == SettingsActivity.this.clearLogsRow)
            {
              FileLog.cleanupLogs();
              return;
            }
            if (paramInt == SettingsActivity.this.sendByEnterRow)
            {
              localObject1 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              bool2 = ((SharedPreferences)localObject1).getBoolean("send_by_enter", false);
              localObject1 = ((SharedPreferences)localObject1).edit();
              if (!bool2)
              {
                bool1 = true;
                ((SharedPreferences.Editor)localObject1).putBoolean("send_by_enter", bool1);
                ((SharedPreferences.Editor)localObject1).commit();
                if (!(paramView instanceof TextCheckCell))
                  continue;
                paramView = (TextCheckCell)paramView;
                if (bool2)
                  break label1114;
              }
              for (bool1 = bool7; ; bool1 = false)
              {
                paramView.setChecked(bool1);
                return;
                bool1 = false;
                break;
              }
            }
            if (paramInt == SettingsActivity.this.raiseToSpeakRow)
            {
              MediaController.getInstance().toogleRaiseToSpeak();
              if (!(paramView instanceof TextCheckCell))
                continue;
              ((TextCheckCell)paramView).setChecked(MediaController.getInstance().canRaiseToSpeak());
              return;
            }
            if (paramInt == SettingsActivity.this.autoplayGifsRow)
            {
              MediaController.getInstance().toggleAutoplayGifs();
              if (!(paramView instanceof TextCheckCell))
                continue;
              ((TextCheckCell)paramView).setChecked(MediaController.getInstance().canAutoplayGifs());
              return;
            }
            if (paramInt == SettingsActivity.this.saveToGalleryRow)
            {
              MediaController.getInstance().toggleSaveToGallery();
              if (!(paramView instanceof TextCheckCell))
                continue;
              ((TextCheckCell)paramView).setChecked(MediaController.getInstance().canSaveToGallery());
              return;
            }
            if (paramInt == SettingsActivity.this.customTabsRow)
            {
              MediaController.getInstance().toggleCustomTabs();
              if (!(paramView instanceof TextCheckCell))
                continue;
              ((TextCheckCell)paramView).setChecked(MediaController.getInstance().canCustomTabs());
              return;
            }
            if (paramInt == SettingsActivity.this.directShareRow)
            {
              MediaController.getInstance().toggleDirectShare();
              if (!(paramView instanceof TextCheckCell))
                continue;
              ((TextCheckCell)paramView).setChecked(MediaController.getInstance().canDirectShare());
              return;
            }
            if (paramInt == SettingsActivity.this.privacyRow)
            {
              SettingsActivity.this.presentFragment(new PrivacySettingsActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.dataRow)
            {
              SettingsActivity.this.presentFragment(new DataSettingsActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.languageRow)
            {
              SettingsActivity.this.presentFragment(new LanguageSelectActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.themeRow)
            {
              SettingsActivity.this.presentFragment(new ThemeActivity());
              return;
            }
            if (paramInt == SettingsActivity.this.switchBackendButtonRow)
            {
              if (SettingsActivity.this.getParentActivity() == null)
                continue;
              paramView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
              paramView.setMessage(LocaleController.getString("AreYouSure", 2131165335));
              paramView.setTitle(LocaleController.getString("AppName", 2131165319));
              paramView.setPositiveButton(LocaleController.getString("OK", 2131166153), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  ConnectionsManager.getInstance().switchBackend();
                }
              });
              paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              SettingsActivity.this.showDialog(paramView.create());
              return;
            }
            if (paramInt == SettingsActivity.this.telegramFaqRow)
            {
              Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("TelegramFaqUrl", 2131166503));
              return;
            }
            if (paramInt == SettingsActivity.this.privacyPolicyRow)
            {
              Browser.openUrl(SettingsActivity.this.getParentActivity(), LocaleController.getString("PrivacyPolicyUrl", 2131166307));
              return;
            }
            if (paramInt == SettingsActivity.this.contactsReimportRow)
              continue;
            if (paramInt != SettingsActivity.this.contactsSortRow)
              break;
            if (SettingsActivity.this.getParentActivity() == null)
              continue;
            paramView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("SortBy", 2131166477));
            localObject1 = LocaleController.getString("Default", 2131165626);
            localObject2 = LocaleController.getString("SortFirstName", 2131166478);
            localObject3 = LocaleController.getString("SortLastName", 2131166479);
            localObject4 = new DialogInterface.OnClickListener(paramInt)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                paramDialogInterface = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                paramDialogInterface.putInt("sortContactsBy", paramInt);
                paramDialogInterface.commit();
                if (SettingsActivity.this.listAdapter != null)
                  SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
              }
            };
            paramView.setItems(new CharSequence[] { localObject1, localObject2, localObject3 }, (DialogInterface.OnClickListener)localObject4);
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            SettingsActivity.this.showDialog(paramView.create());
            return;
          }
          if (paramInt == SettingsActivity.this.usernameRow)
          {
            SettingsActivity.this.presentFragment(new ChangeUsernameActivity(null));
            return;
          }
          if (paramInt == SettingsActivity.this.numberRow)
          {
            SettingsActivity.this.presentFragment(new ChangePhoneHelpActivity());
            return;
          }
          if (paramInt != SettingsActivity.this.stickersRow)
            continue;
          SettingsActivity.this.presentFragment(new StickersActivity(0));
          return;
        }
        while ((paramInt != SettingsActivity.this.emojiRow) || (SettingsActivity.this.getParentActivity() == null));
        Object localObject1 = new boolean[2];
        Object localObject2 = new BottomSheet.Builder(SettingsActivity.this.getParentActivity());
        ((BottomSheet.Builder)localObject2).setApplyTopPadding(false);
        ((BottomSheet.Builder)localObject2).setApplyBottomPadding(false);
        Object localObject3 = new LinearLayout(SettingsActivity.this.getParentActivity());
        ((LinearLayout)localObject3).setOrientation(1);
        int i = 0;
        if (Build.VERSION.SDK_INT >= 19)
        {
          j = 2;
          label1927: if (i >= j)
            break label2086;
          if (i != 0)
            break label2058;
          localObject1[i] = MessagesController.getInstance().allowBigEmoji;
          paramView = LocaleController.getString("EmojiBigSize", 2131165672);
        }
        while (true)
        {
          localObject4 = new CheckBoxCell(SettingsActivity.this.getParentActivity(), true);
          ((CheckBoxCell)localObject4).setTag(Integer.valueOf(i));
          ((CheckBoxCell)localObject4).setBackgroundDrawable(Theme.getSelectorDrawable(false));
          ((LinearLayout)localObject3).addView((View)localObject4, LayoutHelper.createLinear(-1, 48));
          ((CheckBoxCell)localObject4).setText(paramView, "", localObject1[i], true);
          ((CheckBoxCell)localObject4).setTextColor(Theme.getColor("dialogTextBlack"));
          ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener(localObject1)
          {
            public void onClick(View paramView)
            {
              paramView = (CheckBoxCell)paramView;
              int i = ((Integer)paramView.getTag()).intValue();
              boolean[] arrayOfBoolean = this.val$maskValues;
              if (this.val$maskValues[i] == 0);
              for (int j = 1; ; j = 0)
              {
                arrayOfBoolean[i] = j;
                paramView.setChecked(this.val$maskValues[i], true);
                return;
              }
            }
          });
          i += 1;
          break;
          j = 1;
          break label1927;
          label2058: if (i == 1)
          {
            localObject1[i] = MessagesController.getInstance().useSystemEmoji;
            paramView = LocaleController.getString("EmojiUseDefault", 2131165673);
            continue;
            label2086: paramView = new BottomSheet.BottomSheetCell(SettingsActivity.this.getParentActivity(), 1);
            paramView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            paramView.setTextAndIcon(LocaleController.getString("Save", 2131166371).toUpperCase(), 0);
            paramView.setTextColor(Theme.getColor("dialogTextBlue2"));
            paramView.setOnClickListener(new View.OnClickListener(localObject1, paramInt)
            {
              public void onClick(View paramView)
              {
                try
                {
                  if (SettingsActivity.this.visibleDialog != null)
                    SettingsActivity.this.visibleDialog.dismiss();
                  paramView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                  MessagesController localMessagesController = MessagesController.getInstance();
                  int i = this.val$maskValues[0];
                  localMessagesController.allowBigEmoji = i;
                  paramView.putBoolean("allowBigEmoji", i);
                  localMessagesController = MessagesController.getInstance();
                  int j = this.val$maskValues[1];
                  localMessagesController.useSystemEmoji = j;
                  paramView.putBoolean("useSystemEmoji", j);
                  paramView.commit();
                  if (SettingsActivity.this.listAdapter != null)
                    SettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                  return;
                }
                catch (Exception paramView)
                {
                  while (true)
                    FileLog.e(paramView);
                }
              }
            });
            ((LinearLayout)localObject3).addView(paramView, LayoutHelper.createLinear(-1, 48));
            ((BottomSheet.Builder)localObject2).setCustomView((View)localObject3);
            SettingsActivity.this.showDialog(((BottomSheet.Builder)localObject2).create());
            return;
          }
          paramView = null;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      private int pressCount = 0;

      public boolean onItemClick(View paramView, int paramInt)
      {
        if (paramInt == SettingsActivity.this.versionRow)
        {
          this.pressCount += 1;
          if (this.pressCount >= 2)
          {
            paramView = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
            paramView.setTitle(LocaleController.getString("DebugMenu", 2131165623));
            String str1 = LocaleController.getString("DebugMenuImportContacts", 2131165624);
            String str2 = LocaleController.getString("DebugMenuReloadContacts", 2131165625);
            1 local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (paramInt == 0)
                  ContactsController.getInstance().forceImportContacts();
                do
                  return;
                while (paramInt != 1);
                ContactsController.getInstance().loadContacts(false, true);
              }
            };
            paramView.setItems(new CharSequence[] { str1, str2 }, local1);
            paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            SettingsActivity.this.showDialog(paramView.create());
            return true;
          }
          try
          {
            Toast.makeText(SettingsActivity.this.getParentActivity(), "¯\\_(ツ)_/¯", 0).show();
            return true;
          }
          catch (Exception paramView)
          {
            FileLog.e(paramView);
            return true;
          }
        }
        return false;
      }
    });
    localFrameLayout.addView(this.actionBar);
    this.extraHeightView = new View(paramContext);
    this.extraHeightView.setPivotY(0.0F);
    this.extraHeightView.setBackgroundColor(Theme.getColor("avatar_backgroundActionBarBlue"));
    localFrameLayout.addView(this.extraHeightView, LayoutHelper.createFrame(-1, 88.0F));
    this.shadowView = new View(paramContext);
    this.shadowView.setBackgroundResource(2130837728);
    localFrameLayout.addView(this.shadowView, LayoutHelper.createFrame(-1, 3.0F));
    this.avatarImage = new BackupImageView(paramContext);
    this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0F));
    this.avatarImage.setPivotX(0.0F);
    this.avatarImage.setPivotY(0.0F);
    localFrameLayout.addView(this.avatarImage, LayoutHelper.createFrame(42, 42.0F, 51, 64.0F, 0.0F, 0.0F, 0.0F));
    this.avatarImage.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        paramView = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
        if ((paramView != null) && (paramView.photo != null) && (paramView.photo.photo_big != null))
        {
          PhotoViewer.getInstance().setParentActivity(SettingsActivity.this.getParentActivity());
          PhotoViewer.getInstance().openPhoto(paramView.photo.photo_big, SettingsActivity.this);
        }
      }
    });
    this.nameTextView = new TextView(paramContext);
    this.nameTextView.setTextColor(Theme.getColor("profile_title"));
    this.nameTextView.setTextSize(1, 18.0F);
    this.nameTextView.setLines(1);
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.nameTextView.setGravity(3);
    this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.nameTextView.setPivotX(0.0F);
    this.nameTextView.setPivotY(0.0F);
    localFrameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.onlineTextView = new TextView(paramContext);
    this.onlineTextView.setTextColor(Theme.getColor("avatar_subtitleInProfileBlue"));
    this.onlineTextView.setTextSize(1, 14.0F);
    this.onlineTextView.setLines(1);
    this.onlineTextView.setMaxLines(1);
    this.onlineTextView.setSingleLine(true);
    this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
    this.onlineTextView.setGravity(3);
    localFrameLayout.addView(this.onlineTextView, LayoutHelper.createFrame(-2, -2.0F, 51, 118.0F, 0.0F, 48.0F, 0.0F));
    this.writeButton = new ImageView(paramContext);
    localObject = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0F), Theme.getColor("profile_actionBackground"), Theme.getColor("profile_actionPressedBackground"));
    if (Build.VERSION.SDK_INT < 21)
    {
      paramContext = paramContext.getResources().getDrawable(2130837718).mutate();
      paramContext.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
      paramContext = new CombinedDrawable(paramContext, (Drawable)localObject, 0, 0);
      paramContext.setIconSize(AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
    }
    while (true)
    {
      this.writeButton.setBackgroundDrawable(paramContext);
      this.writeButton.setImageResource(2130837714);
      this.writeButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("profile_actionIcon"), PorterDuff.Mode.MULTIPLY));
      this.writeButton.setScaleType(ImageView.ScaleType.CENTER);
      if (Build.VERSION.SDK_INT >= 21)
      {
        paramContext = new StateListAnimator();
        localObject = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(2.0F), AndroidUtilities.dp(4.0F) }).setDuration(200L);
        paramContext.addState(new int[] { 16842919 }, (Animator)localObject);
        localObject = ObjectAnimator.ofFloat(this.writeButton, "translationZ", new float[] { AndroidUtilities.dp(4.0F), AndroidUtilities.dp(2.0F) }).setDuration(200L);
        paramContext.addState(new int[0], (Animator)localObject);
        this.writeButton.setStateListAnimator(paramContext);
        this.writeButton.setOutlineProvider(new ViewOutlineProvider()
        {
          @SuppressLint({"NewApi"})
          public void getOutline(View paramView, Outline paramOutline)
          {
            paramOutline.setOval(0, 0, AndroidUtilities.dp(56.0F), AndroidUtilities.dp(56.0F));
          }
        });
      }
      paramContext = this.writeButton;
      int i;
      float f;
      if (Build.VERSION.SDK_INT >= 21)
      {
        i = 56;
        if (Build.VERSION.SDK_INT < 21)
          break label1114;
        f = 56.0F;
      }
      while (true)
      {
        localFrameLayout.addView(paramContext, LayoutHelper.createFrame(i, f, 53, 0.0F, 0.0F, 16.0F, 0.0F));
        this.writeButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            if (SettingsActivity.this.getParentActivity() == null);
            AlertDialog.Builder localBuilder;
            do
            {
              return;
              localBuilder = new AlertDialog.Builder(SettingsActivity.this.getParentActivity());
              TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
              paramView = localUser;
              if (localUser != null)
                continue;
              paramView = UserConfig.getCurrentUser();
            }
            while (paramView == null);
            if ((paramView.photo != null) && (paramView.photo.photo_big != null) && (!(paramView.photo instanceof TLRPC.TL_userProfilePhotoEmpty)))
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
                    SettingsActivity.this.avatarUpdater.openCamera();
                  do
                  {
                    return;
                    if (paramInt != 1)
                      continue;
                    SettingsActivity.this.avatarUpdater.openGallery();
                    return;
                  }
                  while (paramInt != 2);
                  MessagesController.getInstance().deleteUserPhoto(null);
                }
              });
              SettingsActivity.this.showDialog(localBuilder.create());
              return;
              paramView = new CharSequence[2];
              paramView[0] = LocaleController.getString("FromCamera", 2131165779);
              paramView[1] = LocaleController.getString("FromGalley", 2131165786);
            }
          }
        });
        needLayout();
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
          {
            paramInt1 = 0;
            paramInt2 = 0;
            if (SettingsActivity.this.layoutManager.getItemCount() == 0);
            do
            {
              do
              {
                return;
                paramRecyclerView = paramRecyclerView.getChildAt(0);
              }
              while (paramRecyclerView == null);
              if (SettingsActivity.this.layoutManager.findFirstVisibleItemPosition() != 0)
                continue;
              int i = AndroidUtilities.dp(88.0F);
              paramInt1 = paramInt2;
              if (paramRecyclerView.getTop() < 0)
                paramInt1 = paramRecyclerView.getTop();
              paramInt1 += i;
            }
            while (SettingsActivity.this.extraHeight == paramInt1);
            SettingsActivity.access$4202(SettingsActivity.this, paramInt1);
            SettingsActivity.this.needLayout();
          }
        });
        return this.fragmentView;
        i = 60;
        break;
        label1114: f = 60.0F;
      }
      paramContext = (Context)localObject;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x1) != 0))
        updateUserData();
    }
    do
      return;
    while ((paramInt != NotificationCenter.featuredStickersDidLoaded) || (this.listAdapter == null));
    this.listAdapter.notifyItemChanged(this.stickersRow);
  }

  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramInt = 0;
    if (paramFileLocation == null)
      return null;
    paramMessageObject = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
    if ((paramMessageObject != null) && (paramMessageObject.photo != null) && (paramMessageObject.photo.photo_big != null))
    {
      paramMessageObject = paramMessageObject.photo.photo_big;
      if ((paramMessageObject.local_id == paramFileLocation.local_id) && (paramMessageObject.volume_id == paramFileLocation.volume_id) && (paramMessageObject.dc_id == paramFileLocation.dc_id))
      {
        paramMessageObject = new int[2];
        this.avatarImage.getLocationInWindow(paramMessageObject);
        paramFileLocation = new PhotoViewer.PlaceProviderObject();
        paramFileLocation.viewX = paramMessageObject[0];
        int i = paramMessageObject[1];
        if (Build.VERSION.SDK_INT >= 21);
        while (true)
        {
          paramFileLocation.viewY = (i - paramInt);
          paramFileLocation.parentView = this.avatarImage;
          paramFileLocation.imageReceiver = this.avatarImage.getImageReceiver();
          paramFileLocation.dialogId = UserConfig.getClientUserId();
          paramFileLocation.thumb = paramFileLocation.imageReceiver.getBitmap();
          paramFileLocation.size = -1;
          paramFileLocation.radius = this.avatarImage.getImageReceiver().getRoundRadius();
          paramFileLocation.scale = this.avatarImage.getScaleX();
          return paramFileLocation;
          paramInt = AndroidUtilities.statusBarHeight;
        }
      }
    }
    return null;
  }

  public int getSelectedCount()
  {
    return 0;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { EmptyCell.class, TextSettingsCell.class, TextCheckCell.class, HeaderCell.class, TextInfoCell.class, TextDetailSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.extraHeightView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "avatar_backgroundActionBarBlue");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "avatar_actionBarIconBlue");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "avatar_actionBarSelectorBlue");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "profile_title");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "avatar_subtitleInProfileBlue");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumb"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrack"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchThumbChecked"), new ThemeDescription(this.listView, 0, new Class[] { TextCheckCell.class }, new String[] { "checkBox" }, null, null, null, "switchTrackChecked"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText5"), new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, null, "avatar_text"), new ThemeDescription(this.avatarImage, 0, null, null, new Drawable[] { this.avatarDrawable }, null, "avatar_backgroundInProfileBlue"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, "profile_actionIcon"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "profile_actionBackground"), new ThemeDescription(this.writeButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "profile_actionPressedBackground") };
  }

  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    return null;
  }

  public boolean isPhotoChecked(int paramInt)
  {
    return false;
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.avatarUpdater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.avatarUpdater.parentFragment = this;
    this.avatarUpdater.delegate = new AvatarUpdater.AvatarUpdaterDelegate()
    {
      public void didUploadedPhoto(TLRPC.InputFile paramInputFile, TLRPC.PhotoSize paramPhotoSize1, TLRPC.PhotoSize paramPhotoSize2)
      {
        paramPhotoSize1 = new TLRPC.TL_photos_uploadProfilePhoto();
        paramPhotoSize1.file = paramInputFile;
        ConnectionsManager.getInstance().sendRequest(paramPhotoSize1, new RequestDelegate()
        {
          public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
          {
            if (paramTL_error == null)
            {
              paramTL_error = MessagesController.getInstance().getUser(Integer.valueOf(UserConfig.getClientUserId()));
              if (paramTL_error != null)
                break label174;
              paramTL_error = UserConfig.getCurrentUser();
              if (paramTL_error != null);
            }
            else
            {
              return;
            }
            MessagesController.getInstance().putUser(paramTL_error, false);
            paramTLObject = (TLRPC.TL_photos_photo)paramTLObject;
            Object localObject = paramTLObject.photo.sizes;
            TLRPC.PhotoSize localPhotoSize = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 100);
            localObject = FileLoader.getClosestPhotoSizeWithSize((ArrayList)localObject, 1000);
            paramTL_error.photo = new TLRPC.TL_userProfilePhoto();
            paramTL_error.photo.photo_id = paramTLObject.photo.id;
            if (localPhotoSize != null)
              paramTL_error.photo.photo_small = localPhotoSize.location;
            if (localObject != null)
              paramTL_error.photo.photo_big = ((TLRPC.PhotoSize)localObject).location;
            while (true)
            {
              MessagesStorage.getInstance().clearUserPhotos(paramTL_error.id);
              paramTLObject = new ArrayList();
              paramTLObject.add(paramTL_error);
              MessagesStorage.getInstance().putUsersAndChats(paramTLObject, null, false, true);
              AndroidUtilities.runOnUIThread(new Runnable()
              {
                public void run()
                {
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1535) });
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.mainUserInfoChanged, new Object[0]);
                  UserConfig.saveConfig(true);
                }
              });
              return;
              label174: UserConfig.setCurrentUser(paramTL_error);
              break;
              if (localPhotoSize == null)
                continue;
              paramTL_error.photo.photo_small = localPhotoSize.location;
            }
          }
        });
      }
    };
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.overscrollRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.emptyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.numberSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.numberRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.usernameRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.settingsSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.notificationRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacyRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.dataRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.backgroundRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.themeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.languageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.enableAnimationsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videogramSettingRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videogramSettingRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.persionDate = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendStickerEnable = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendGifrEnable = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendVoicerEnable = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.messagesSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.customTabsRow = i;
    if (Build.VERSION.SDK_INT >= 23)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.directShareRow = i;
    }
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.stickersRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.textSizeRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.raiseToSpeakRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.sendByEnterRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.autoplayGifsRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.saveToGalleryRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.supportSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.supportSectionRow2 = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.askQuestionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.telegramFaqRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.privacyPolicyRow = i;
    if (BuildVars.DEBUG_VERSION)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.sendLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.clearLogsRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.switchBackendButtonRow = i;
    }
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.versionRow = i;
    StickersQuery.checkFeaturedStickers();
    MessagesController.getInstance().loadFullUser(UserConfig.getCurrentUser(), this.classGuid, true);
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    if (this.avatarImage != null)
      this.avatarImage.setImageDrawable(null);
    MessagesController.getInstance().cancelLoadFullUser(UserConfig.getClientUserId());
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.featuredStickersDidLoaded);
    this.avatarUpdater.clear();
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    updateUserData();
    fixLayout();
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
  }

  public boolean scaleToFill()
  {
    return false;
  }

  public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
  {
  }

  public void setPhotoChecked(int paramInt)
  {
  }

  public void updatePhotoAtIndex(int paramInt)
  {
  }

  public void willHidePhotoViewer()
  {
    this.avatarImage.getImageReceiver().setVisible(true, true);
  }

  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
  }

  private static class LinkMovementMethodMy extends LinkMovementMethod
  {
    public boolean onTouchEvent(TextView paramTextView, Spannable paramSpannable, MotionEvent paramMotionEvent)
    {
      try
      {
        boolean bool = super.onTouchEvent(paramTextView, paramSpannable, paramMotionEvent);
        return bool;
      }
      catch (Exception paramTextView)
      {
        FileLog.e(paramTextView);
      }
      return false;
    }
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      return SettingsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      int j = 2;
      int i;
      if ((paramInt == SettingsActivity.this.emptyRow) || (paramInt == SettingsActivity.this.overscrollRow))
        i = 0;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          do
                          {
                            do
                            {
                              do
                              {
                                do
                                {
                                  do
                                  {
                                    do
                                    {
                                      do
                                      {
                                        do
                                        {
                                          return i;
                                          if ((paramInt == SettingsActivity.this.videogramSettingRow) || (paramInt == SettingsActivity.this.settingsSectionRow) || (paramInt == SettingsActivity.this.supportSectionRow) || (paramInt == SettingsActivity.this.messagesSectionRow) || (paramInt == SettingsActivity.this.contactsSectionRow))
                                            return 1;
                                          if ((paramInt == SettingsActivity.this.persionDate) || (paramInt == SettingsActivity.this.sendVoicerEnable) || (paramInt == SettingsActivity.this.sendStickerEnable) || (paramInt == SettingsActivity.this.sendGifrEnable) || (paramInt == SettingsActivity.this.enableAnimationsRow) || (paramInt == SettingsActivity.this.sendByEnterRow) || (paramInt == SettingsActivity.this.saveToGalleryRow) || (paramInt == SettingsActivity.this.autoplayGifsRow) || (paramInt == SettingsActivity.this.raiseToSpeakRow) || (paramInt == SettingsActivity.this.customTabsRow) || (paramInt == SettingsActivity.this.directShareRow))
                                            return 3;
                                          i = j;
                                        }
                                        while (paramInt == SettingsActivity.this.notificationRow);
                                        i = j;
                                      }
                                      while (paramInt == SettingsActivity.this.themeRow);
                                      i = j;
                                    }
                                    while (paramInt == SettingsActivity.this.backgroundRow);
                                    i = j;
                                  }
                                  while (paramInt == SettingsActivity.this.askQuestionRow);
                                  i = j;
                                }
                                while (paramInt == SettingsActivity.this.sendLogsRow);
                                i = j;
                              }
                              while (paramInt == SettingsActivity.this.privacyRow);
                              i = j;
                            }
                            while (paramInt == SettingsActivity.this.clearLogsRow);
                            i = j;
                          }
                          while (paramInt == SettingsActivity.this.switchBackendButtonRow);
                          i = j;
                        }
                        while (paramInt == SettingsActivity.this.telegramFaqRow);
                        i = j;
                      }
                      while (paramInt == SettingsActivity.this.contactsReimportRow);
                      i = j;
                    }
                    while (paramInt == SettingsActivity.this.textSizeRow);
                    i = j;
                  }
                  while (paramInt == SettingsActivity.this.languageRow);
                  i = j;
                }
                while (paramInt == SettingsActivity.this.contactsSortRow);
                i = j;
              }
              while (paramInt == SettingsActivity.this.stickersRow);
              i = j;
            }
            while (paramInt == SettingsActivity.this.privacyPolicyRow);
            i = j;
          }
          while (paramInt == SettingsActivity.this.emojiRow);
          i = j;
        }
        while (paramInt == SettingsActivity.this.dataRow);
        if (paramInt == SettingsActivity.this.versionRow)
          return 5;
        if ((paramInt == SettingsActivity.this.numberRow) || (paramInt == SettingsActivity.this.usernameRow))
          return 6;
        if ((paramInt == SettingsActivity.this.videogramSettingRow2) || (paramInt == SettingsActivity.this.settingsSectionRow2) || (paramInt == SettingsActivity.this.messagesSectionRow2) || (paramInt == SettingsActivity.this.supportSectionRow2))
          break;
        i = j;
      }
      while (paramInt != SettingsActivity.this.numberSectionRow);
      return 4;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == SettingsActivity.this.persionDate) || (i == SettingsActivity.this.sendVoicerEnable) || (i == SettingsActivity.this.sendStickerEnable) || (i == SettingsActivity.this.sendGifrEnable) || (i == SettingsActivity.this.textSizeRow) || (i == SettingsActivity.this.enableAnimationsRow) || (i == SettingsActivity.this.notificationRow) || (i == SettingsActivity.this.backgroundRow) || (i == SettingsActivity.this.numberRow) || (i == SettingsActivity.this.askQuestionRow) || (i == SettingsActivity.this.sendLogsRow) || (i == SettingsActivity.this.sendByEnterRow) || (i == SettingsActivity.this.autoplayGifsRow) || (i == SettingsActivity.this.privacyRow) || (i == SettingsActivity.this.clearLogsRow) || (i == SettingsActivity.this.languageRow) || (i == SettingsActivity.this.usernameRow) || (i == SettingsActivity.this.switchBackendButtonRow) || (i == SettingsActivity.this.telegramFaqRow) || (i == SettingsActivity.this.contactsSortRow) || (i == SettingsActivity.this.contactsReimportRow) || (i == SettingsActivity.this.saveToGalleryRow) || (i == SettingsActivity.this.stickersRow) || (i == SettingsActivity.this.raiseToSpeakRow) || (i == SettingsActivity.this.privacyPolicyRow) || (i == SettingsActivity.this.customTabsRow) || (i == SettingsActivity.this.directShareRow) || (i == SettingsActivity.this.versionRow) || (i == SettingsActivity.this.emojiRow) || (i == SettingsActivity.this.dataRow) || (i == SettingsActivity.this.themeRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      case 1:
      case 5:
      default:
      case 0:
      case 2:
      case 3:
      case 4:
      case 6:
      }
      Object localObject;
      do
      {
        do
        {
          do
          {
            do
            {
              return;
              if (paramInt == SettingsActivity.this.overscrollRow)
              {
                ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(88.0F));
                return;
              }
              ((EmptyCell)paramViewHolder.itemView).setHeight(AndroidUtilities.dp(16.0F));
              return;
              localObject = (TextSettingsCell)paramViewHolder.itemView;
              if (paramInt == SettingsActivity.this.textSizeRow)
              {
                paramViewHolder = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
                if (AndroidUtilities.isTablet());
                for (paramInt = 18; ; paramInt = 16)
                {
                  paramInt = paramViewHolder.getInt("fons_size", paramInt);
                  ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("TextSize", 2131166509), String.format("%d", new Object[] { Integer.valueOf(paramInt) }), true);
                  return;
                }
              }
              if (paramInt == SettingsActivity.this.languageRow)
              {
                ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Language", 2131165870), LocaleController.getCurrentLanguageName(), true);
                return;
              }
              if (paramInt == SettingsActivity.this.themeRow)
              {
                ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("Theme", 2131166510), Theme.getCurrentThemeName(), true);
                return;
              }
              if (paramInt == SettingsActivity.this.contactsSortRow)
              {
                paramInt = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("sortContactsBy", 0);
                if (paramInt == 0)
                  paramViewHolder = LocaleController.getString("Default", 2131165626);
                while (true)
                {
                  ((TextSettingsCell)localObject).setTextAndValue(LocaleController.getString("SortBy", 2131166477), paramViewHolder, true);
                  return;
                  if (paramInt == 1)
                  {
                    paramViewHolder = LocaleController.getString("FirstName", 2131166478);
                    continue;
                  }
                  paramViewHolder = LocaleController.getString("LastName", 2131166479);
                }
              }
              if (paramInt == SettingsActivity.this.notificationRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("NotificationsAndSounds", 2131166129), true);
                return;
              }
              if (paramInt == SettingsActivity.this.backgroundRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("ChatBackground", 2131165530), true);
                return;
              }
              if (paramInt == SettingsActivity.this.sendLogsRow)
              {
                ((TextSettingsCell)localObject).setText("Send Logs", true);
                return;
              }
              if (paramInt == SettingsActivity.this.clearLogsRow)
              {
                ((TextSettingsCell)localObject).setText("Clear Logs", true);
                return;
              }
              if (paramInt == SettingsActivity.this.askQuestionRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("AskAQuestion", 2131165356), true);
                return;
              }
              if (paramInt == SettingsActivity.this.privacyRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("PrivacySettings", 2131166308), true);
                return;
              }
              if (paramInt == SettingsActivity.this.dataRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("DataSettings", 2131165609), true);
                return;
              }
              if (paramInt == SettingsActivity.this.switchBackendButtonRow)
              {
                ((TextSettingsCell)localObject).setText("Switch Backend", true);
                return;
              }
              if (paramInt == SettingsActivity.this.telegramFaqRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("TelegramFAQ", 2131166502), true);
                return;
              }
              if (paramInt == SettingsActivity.this.contactsReimportRow)
              {
                ((TextSettingsCell)localObject).setText(LocaleController.getString("ImportContacts", 2131165825), true);
                return;
              }
              if (paramInt == SettingsActivity.this.stickersRow)
              {
                paramInt = StickersQuery.getUnreadStickerSets().size();
                String str = LocaleController.getString("Stickers", 2131166485);
                if (paramInt != 0);
                for (paramViewHolder = String.format("%d", new Object[] { Integer.valueOf(paramInt) }); ; paramViewHolder = "")
                {
                  ((TextSettingsCell)localObject).setTextAndValue(str, paramViewHolder, true);
                  return;
                }
              }
              if (paramInt != SettingsActivity.this.privacyPolicyRow)
                continue;
              ((TextSettingsCell)localObject).setText(LocaleController.getString("PrivacyPolicy", 2131166306), true);
              return;
            }
            while (paramInt != SettingsActivity.this.emojiRow);
            ((TextSettingsCell)localObject).setText(LocaleController.getString("Emoji", 2131165671), true);
            return;
            paramViewHolder = (TextCheckCell)paramViewHolder.itemView;
            localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            if (paramInt == SettingsActivity.this.enableAnimationsRow)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("EnableAnimations", 2131165675), ((SharedPreferences)localObject).getBoolean("view_animations", true), false);
              return;
            }
            if (paramInt == SettingsActivity.this.persionDate)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("PersionDate", 2131166782), itman.Vidofilm.b.a(ApplicationLoader.applicationContext).e(), true);
              return;
            }
            if (paramInt == SettingsActivity.this.sendGifrEnable)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendGif", 2131166767), itman.Vidofilm.b.a(ApplicationLoader.applicationContext).f(), true);
              return;
            }
            if (paramInt == SettingsActivity.this.sendStickerEnable)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendSticker", 2131166768), itman.Vidofilm.b.a(ApplicationLoader.applicationContext).h(), true);
              return;
            }
            if (paramInt == SettingsActivity.this.sendVoicerEnable)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("ConfirmationBeforSendVoice", 2131166769), itman.Vidofilm.b.a(ApplicationLoader.applicationContext).g(), false);
              return;
            }
            if (paramInt == SettingsActivity.this.sendByEnterRow)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("SendByEnter", 2131166410), ((SharedPreferences)localObject).getBoolean("send_by_enter", false), true);
              return;
            }
            if (paramInt == SettingsActivity.this.saveToGalleryRow)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("SaveToGallerySettings", 2131166376), MediaController.getInstance().canSaveToGallery(), false);
              return;
            }
            if (paramInt == SettingsActivity.this.autoplayGifsRow)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("AutoplayGifs", 2131165378), MediaController.getInstance().canAutoplayGifs(), true);
              return;
            }
            if (paramInt == SettingsActivity.this.raiseToSpeakRow)
            {
              paramViewHolder.setTextAndCheck(LocaleController.getString("RaiseToSpeak", 2131166312), MediaController.getInstance().canRaiseToSpeak(), true);
              return;
            }
            if (paramInt != SettingsActivity.this.customTabsRow)
              continue;
            paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("ChromeCustomTabs", 2131165547), LocaleController.getString("ChromeCustomTabsInfo", 2131165548), MediaController.getInstance().canCustomTabs(), false, true);
            return;
          }
          while (paramInt != SettingsActivity.this.directShareRow);
          paramViewHolder.setTextAndValueAndCheck(LocaleController.getString("DirectShare", 2131165656), LocaleController.getString("DirectShareInfo", 2131165657), MediaController.getInstance().canDirectShare(), false, true);
          return;
          if (paramInt == SettingsActivity.this.videogramSettingRow2)
          {
            ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("VidogramSetting", 2131166811));
            return;
          }
          if (paramInt == SettingsActivity.this.settingsSectionRow2)
          {
            ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("SETTINGS", 2131166368));
            return;
          }
          if (paramInt == SettingsActivity.this.supportSectionRow2)
          {
            ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Support", 2131166498));
            return;
          }
          if (paramInt != SettingsActivity.this.messagesSectionRow2)
            continue;
          ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("MessagesSettings", 2131165968));
          return;
        }
        while (paramInt != SettingsActivity.this.numberSectionRow);
        ((HeaderCell)paramViewHolder.itemView).setText(LocaleController.getString("Info", 2131165834));
        return;
        localObject = (TextDetailSettingsCell)paramViewHolder.itemView;
        if (paramInt != SettingsActivity.this.numberRow)
          continue;
        paramViewHolder = UserConfig.getCurrentUser();
        if ((paramViewHolder != null) && (paramViewHolder.phone != null) && (paramViewHolder.phone.length() != 0));
        for (paramViewHolder = org.vidogram.a.b.a().e("+" + paramViewHolder.phone); ; paramViewHolder = LocaleController.getString("NumberUnknown", 2131166152))
        {
          ((TextDetailSettingsCell)localObject).setTextAndValue(paramViewHolder, LocaleController.getString("Phone", 2131166262), true);
          return;
        }
      }
      while (paramInt != SettingsActivity.this.usernameRow);
      paramViewHolder = UserConfig.getCurrentUser();
      if ((paramViewHolder != null) && (paramViewHolder.username != null) && (paramViewHolder.username.length() != 0));
      for (paramViewHolder = "@" + paramViewHolder.username; ; paramViewHolder = LocaleController.getString("UsernameEmpty", 2131166553))
      {
        ((TextDetailSettingsCell)localObject).setTextAndValue(paramViewHolder, LocaleController.getString("Username", 2131166550), false);
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      TextInfoCell localTextInfoCell;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
        while (true)
        {
          paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
          return new RecyclerListView.Holder(paramViewGroup);
          paramViewGroup = new EmptyCell(this.mContext);
          paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          continue;
          paramViewGroup = new ShadowSectionCell(this.mContext);
          continue;
          paramViewGroup = new TextSettingsCell(this.mContext);
          paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          continue;
          paramViewGroup = new TextCheckCell(this.mContext);
          paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
          continue;
          paramViewGroup = new HeaderCell(this.mContext);
          paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        }
      case 5:
        localTextInfoCell = new TextInfoCell(this.mContext);
        localTextInfoCell.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      case 6:
      }
      while (true)
      {
        try
        {
          PackageInfo localPackageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
          paramInt = localPackageInfo.versionCode / 10;
          switch (localPackageInfo.versionCode % 10)
          {
          case 0:
            ((TextInfoCell)localTextInfoCell).setText(LocaleController.formatString("TelegramVersion", 2131166504, new Object[] { String.format(Locale.US, "v%s (%d) %s", new Object[] { localPackageInfo.versionName, Integer.valueOf(paramInt), paramViewGroup }) }));
            paramViewGroup = localTextInfoCell;
          case 5:
          default:
          case 1:
          case 3:
          case 2:
          case 4:
          }
        }
        catch (Exception paramViewGroup)
        {
          FileLog.e(paramViewGroup);
          paramViewGroup = localTextInfoCell;
        }
        break;
        paramViewGroup = "arm";
        continue;
        paramViewGroup = "universal";
        continue;
        paramViewGroup = new TextDetailSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        break;
        paramViewGroup = "";
        continue;
        paramViewGroup = "arm-v7a";
        continue;
        paramViewGroup = "x86";
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.SettingsActivity
 * JD-Core Version:    0.6.0
 */