package org.vidogram.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import org.vidogram.a.b;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ContactsController;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate;
import org.vidogram.ui.Components.AvatarDrawable;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class ContactAddActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private boolean addContact;
  private AvatarDrawable avatarDrawable;
  private BackupImageView avatarImage;
  private View doneButton;
  private EditText firstNameField;
  private EditText lastNameField;
  private TextView nameTextView;
  private TextView onlineTextView;
  private String phone = null;
  private int user_id;

  public ContactAddActivity(Bundle paramBundle)
  {
    super(paramBundle);
  }

  private void updateAvatarLayout()
  {
    if (this.nameTextView == null);
    do
    {
      return;
      localObject = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
    }
    while (localObject == null);
    this.nameTextView.setText(b.a().e("+" + ((TLRPC.User)localObject).phone));
    this.onlineTextView.setText(LocaleController.formatUserStatus((TLRPC.User)localObject));
    TLRPC.FileLocation localFileLocation = null;
    if (((TLRPC.User)localObject).photo != null)
      localFileLocation = ((TLRPC.User)localObject).photo.photo_small;
    BackupImageView localBackupImageView = this.avatarImage;
    Object localObject = new AvatarDrawable((TLRPC.User)localObject);
    this.avatarDrawable = ((AvatarDrawable)localObject);
    localBackupImageView.setImage(localFileLocation, "50_50", (Drawable)localObject);
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    LinearLayout localLinearLayout;
    label205: label344: float f1;
    label309: label352: float f2;
    if (this.addContact)
    {
      this.actionBar.setTitle(LocaleController.getString("AddContactTitle", 2131165277));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            ContactAddActivity.this.finishFragment();
          do
            return;
          while ((paramInt != 1) || (ContactAddActivity.this.firstNameField.getText().length() == 0));
          TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
          localUser.first_name = ContactAddActivity.this.firstNameField.getText().toString();
          localUser.last_name = ContactAddActivity.this.lastNameField.getText().toString();
          ContactsController.getInstance().addContact(localUser);
          ContactAddActivity.this.finishFragment();
          ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).edit().putInt("spam3_" + ContactAddActivity.this.user_id, 1).commit();
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateInterfaces, new Object[] { Integer.valueOf(1) });
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.peerSettingsDidLoaded, new Object[] { Long.valueOf(ContactAddActivity.access$100(ContactAddActivity.this)) });
        }
      });
      this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
      this.fragmentView = new ScrollView(paramContext);
      localLinearLayout = new LinearLayout(paramContext);
      localLinearLayout.setOrientation(1);
      ((ScrollView)this.fragmentView).addView(localLinearLayout, LayoutHelper.createScroll(-1, -2, 51));
      localLinearLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          return true;
        }
      });
      Object localObject1 = new FrameLayout(paramContext);
      localLinearLayout.addView((View)localObject1, LayoutHelper.createLinear(-1, -2, 24.0F, 24.0F, 24.0F, 0.0F));
      this.avatarImage = new BackupImageView(paramContext);
      this.avatarImage.setRoundRadius(AndroidUtilities.dp(30.0F));
      Object localObject2 = this.avatarImage;
      if (!LocaleController.isRTL)
        break label1012;
      i = 5;
      ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(60, 60, i | 0x30));
      this.nameTextView = new TextView(paramContext);
      this.nameTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.nameTextView.setTextSize(1, 20.0F);
      this.nameTextView.setLines(1);
      this.nameTextView.setMaxLines(1);
      this.nameTextView.setSingleLine(true);
      this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL)
        break label1018;
      i = 5;
      ((TextView)localObject2).setGravity(i);
      this.nameTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      localObject2 = this.nameTextView;
      if (!LocaleController.isRTL)
        break label1024;
      i = 5;
      if (!LocaleController.isRTL)
        break label1030;
      f1 = 0.0F;
      if (!LocaleController.isRTL)
        break label1037;
      f2 = 80.0F;
      label362: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 3.0F, f2, 0.0F));
      this.onlineTextView = new TextView(paramContext);
      this.onlineTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
      this.onlineTextView.setTextSize(1, 14.0F);
      this.onlineTextView.setLines(1);
      this.onlineTextView.setMaxLines(1);
      this.onlineTextView.setSingleLine(true);
      this.onlineTextView.setEllipsize(TextUtils.TruncateAt.END);
      localObject2 = this.onlineTextView;
      if (!LocaleController.isRTL)
        break label1042;
      i = 5;
      label473: ((TextView)localObject2).setGravity(i);
      localObject2 = this.onlineTextView;
      if (!LocaleController.isRTL)
        break label1048;
      i = 5;
      label495: if (!LocaleController.isRTL)
        break label1054;
      f1 = 0.0F;
      label503: if (!LocaleController.isRTL)
        break label1061;
      f2 = 80.0F;
      label513: ((FrameLayout)localObject1).addView((View)localObject2, LayoutHelper.createFrame(-2, -2.0F, i | 0x30, f1, 32.0F, f2, 0.0F));
      this.firstNameField = new EditText(paramContext);
      this.firstNameField.setTextSize(1, 18.0F);
      this.firstNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.firstNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.firstNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.firstNameField.setMaxLines(1);
      this.firstNameField.setLines(1);
      this.firstNameField.setSingleLine(true);
      localObject1 = this.firstNameField;
      if (!LocaleController.isRTL)
        break label1066;
      i = 5;
      label639: ((EditText)localObject1).setGravity(i);
      this.firstNameField.setInputType(49152);
      this.firstNameField.setImeOptions(5);
      this.firstNameField.setHint(LocaleController.getString("FirstName", 2131165714));
      AndroidUtilities.clearCursorDrawable(this.firstNameField);
      localLinearLayout.addView(this.firstNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 24.0F, 24.0F, 0.0F));
      this.firstNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          if (paramInt == 5)
          {
            ContactAddActivity.this.lastNameField.requestFocus();
            ContactAddActivity.this.lastNameField.setSelection(ContactAddActivity.this.lastNameField.length());
            return true;
          }
          return false;
        }
      });
      this.lastNameField = new EditText(paramContext);
      this.lastNameField.setTextSize(1, 18.0F);
      this.lastNameField.setHintTextColor(Theme.getColor("windowBackgroundWhiteHintText"));
      this.lastNameField.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
      this.lastNameField.setBackgroundDrawable(Theme.createEditTextDrawable(paramContext, false));
      this.lastNameField.setMaxLines(1);
      this.lastNameField.setLines(1);
      this.lastNameField.setSingleLine(true);
      paramContext = this.lastNameField;
      if (!LocaleController.isRTL)
        break label1072;
    }
    label1024: label1030: label1037: label1042: label1048: label1054: label1061: label1066: label1072: for (int i = 5; ; i = 3)
    {
      paramContext.setGravity(i);
      this.lastNameField.setInputType(49152);
      this.lastNameField.setImeOptions(6);
      this.lastNameField.setHint(LocaleController.getString("LastName", 2131165874));
      AndroidUtilities.clearCursorDrawable(this.lastNameField);
      localLinearLayout.addView(this.lastNameField, LayoutHelper.createLinear(-1, 36, 24.0F, 16.0F, 24.0F, 0.0F));
      this.lastNameField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          if (paramInt == 6)
          {
            ContactAddActivity.this.doneButton.performClick();
            return true;
          }
          return false;
        }
      });
      paramContext = MessagesController.getInstance().getUser(Integer.valueOf(this.user_id));
      if (paramContext != null)
      {
        if ((paramContext.phone == null) && (this.phone != null))
          paramContext.phone = b.b(this.phone);
        this.firstNameField.setText(paramContext.first_name);
        this.firstNameField.setSelection(this.firstNameField.length());
        this.lastNameField.setText(paramContext.last_name);
      }
      return this.fragmentView;
      this.actionBar.setTitle(LocaleController.getString("EditName", 2131165667));
      break;
      label1012: i = 3;
      break label205;
      label1018: i = 3;
      break label309;
      i = 3;
      break label344;
      f1 = 80.0F;
      break label352;
      f2 = 0.0F;
      break label362;
      i = 3;
      break label473;
      i = 3;
      break label495;
      f1 = 80.0F;
      break label503;
      f2 = 0.0F;
      break label513;
      i = 3;
      break label639;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.updateInterfaces)
    {
      paramInt = ((Integer)paramArrayOfObject[0]).intValue();
      if (((paramInt & 0x2) != 0) || ((paramInt & 0x4) != 0))
        updateAvatarLayout();
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    5 local5 = new ThemeDescription.ThemeDescriptionDelegate()
    {
      public void didSetColor(int paramInt)
      {
        TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(ContactAddActivity.this.user_id));
        if (localUser == null)
          return;
        ContactAddActivity.this.avatarDrawable.setInfo(localUser);
        ContactAddActivity.this.avatarImage.invalidate();
      }
    };
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.nameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.onlineTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.firstNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, "windowBackgroundWhiteHintText"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, "windowBackgroundWhiteInputField"), new ThemeDescription(this.lastNameField, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, "windowBackgroundWhiteInputFieldActivated"), new ThemeDescription(null, 0, null, null, new Drawable[] { Theme.avatar_photoDrawable, Theme.avatar_broadcastDrawable }, local5, "avatar_text"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundRed"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundOrange"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundViolet"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundGreen"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundCyan"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundBlue"), new ThemeDescription(null, 0, null, null, null, local5, "avatar_backgroundPink") };
  }

  public boolean onFragmentCreate()
  {
    int j = 0;
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.updateInterfaces);
    this.user_id = getArguments().getInt("user_id", 0);
    this.phone = getArguments().getString("phone");
    this.addContact = getArguments().getBoolean("addContact", false);
    int i = j;
    if (MessagesController.getInstance().getUser(Integer.valueOf(this.user_id)) != null)
    {
      i = j;
      if (super.onFragmentCreate())
        i = 1;
    }
    return i;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.updateInterfaces);
  }

  public void onResume()
  {
    super.onResume();
    updateAvatarLayout();
    if (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("view_animations", true))
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (paramBoolean1)
    {
      this.firstNameField.requestFocus();
      AndroidUtilities.showKeyboard(this.firstNameField);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ContactAddActivity
 * JD-Core Version:    0.6.0
 */