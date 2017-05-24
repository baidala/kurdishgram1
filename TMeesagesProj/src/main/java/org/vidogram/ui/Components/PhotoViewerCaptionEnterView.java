package org.vidogram.ui.Components;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Point;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.Emoji;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.InputStickerSet;
import org.vidogram.tgnet.TLRPC.StickerSet;
import org.vidogram.tgnet.TLRPC.StickerSetCovered;
import org.vidogram.ui.ActionBar.Theme;

public class PhotoViewerCaptionEnterView extends FrameLayout
  implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayoutPhoto.SizeNotifierFrameLayoutPhotoDelegate
{
  private int audioInterfaceState;
  private final int captionMaxLength = 200;
  private ActionMode currentActionMode;
  private PhotoViewerCaptionEnterViewDelegate delegate;
  private ImageView emojiButton;
  private int emojiPadding;
  private EmojiView emojiView;
  private boolean forceFloatingEmoji;
  private boolean innerTextChange;
  private int keyboardHeight;
  private int keyboardHeightLand;
  private boolean keyboardVisible;
  private int lastSizeChangeValue1;
  private boolean lastSizeChangeValue2;
  private EditText messageEditText;
  private AnimatorSet runningAnimation;
  private AnimatorSet runningAnimation2;
  private ObjectAnimator runningAnimationAudio;
  private int runningAnimationType;
  private SizeNotifierFrameLayoutPhoto sizeNotifierLayout;
  private View windowView;

  public PhotoViewerCaptionEnterView(Context paramContext, SizeNotifierFrameLayoutPhoto paramSizeNotifierFrameLayoutPhoto, View paramView)
  {
    super(paramContext);
    setBackgroundColor(2130706432);
    setFocusable(true);
    setFocusableInTouchMode(true);
    this.windowView = paramView;
    this.sizeNotifierLayout = paramSizeNotifierFrameLayoutPhoto;
    paramSizeNotifierFrameLayoutPhoto = new LinearLayout(paramContext);
    paramSizeNotifierFrameLayoutPhoto.setOrientation(0);
    addView(paramSizeNotifierFrameLayoutPhoto, LayoutHelper.createFrame(-1, -2.0F, 51, 2.0F, 0.0F, 0.0F, 0.0F));
    paramView = new FrameLayout(paramContext);
    paramSizeNotifierFrameLayoutPhoto.addView(paramView, LayoutHelper.createLinear(0, -2, 1.0F));
    this.emojiButton = new ImageView(paramContext);
    this.emojiButton.setImageResource(2130837838);
    this.emojiButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
    this.emojiButton.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(1.0F), 0, 0);
    paramView.addView(this.emojiButton, LayoutHelper.createFrame(48, 48, 83));
    this.emojiButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (!PhotoViewerCaptionEnterView.this.isPopupShowing())
        {
          PhotoViewerCaptionEnterView.this.showPopup(1);
          return;
        }
        PhotoViewerCaptionEnterView.this.openKeyboardInternal();
      }
    });
    this.messageEditText = new EditText(paramContext)
    {
      protected void onMeasure(int paramInt1, int paramInt2)
      {
        try
        {
          super.onMeasure(paramInt1, paramInt2);
          return;
        }
        catch (Exception localException)
        {
          setMeasuredDimension(View.MeasureSpec.getSize(paramInt1), AndroidUtilities.dp(51.0F));
          FileLog.e(localException);
        }
      }
    };
    if ((Build.VERSION.SDK_INT >= 23) && (this.windowView != null))
    {
      this.messageEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
        {
          return false;
        }

        public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, paramActionMode);
          return true;
        }

        public void onDestroyActionMode(ActionMode paramActionMode)
        {
          if (PhotoViewerCaptionEnterView.this.currentActionMode == paramActionMode)
            PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, null);
        }

        public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          if (Build.VERSION.SDK_INT >= 23)
            PhotoViewerCaptionEnterView.this.fixActionMode(paramActionMode);
          return true;
        }
      });
      this.messageEditText.setCustomInsertionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
        {
          return false;
        }

        public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, paramActionMode);
          return true;
        }

        public void onDestroyActionMode(ActionMode paramActionMode)
        {
          if (PhotoViewerCaptionEnterView.this.currentActionMode == paramActionMode)
            PhotoViewerCaptionEnterView.access$202(PhotoViewerCaptionEnterView.this, null);
        }

        public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          if (Build.VERSION.SDK_INT >= 23)
            PhotoViewerCaptionEnterView.this.fixActionMode(paramActionMode);
          return true;
        }
      });
    }
    this.messageEditText.setHint(LocaleController.getString("AddCaption", 2131165274));
    this.messageEditText.setImeOptions(268435456);
    this.messageEditText.setInputType(this.messageEditText.getInputType() | 0x4000);
    this.messageEditText.setMaxLines(4);
    this.messageEditText.setHorizontallyScrolling(false);
    this.messageEditText.setTextSize(1, 18.0F);
    this.messageEditText.setGravity(80);
    this.messageEditText.setPadding(0, AndroidUtilities.dp(11.0F), 0, AndroidUtilities.dp(12.0F));
    this.messageEditText.setBackgroundDrawable(null);
    AndroidUtilities.clearCursorDrawable(this.messageEditText);
    this.messageEditText.setTextColor(-1);
    this.messageEditText.setHintTextColor(-1291845633);
    InputFilter.LengthFilter localLengthFilter = new InputFilter.LengthFilter(200);
    this.messageEditText.setFilters(new InputFilter[] { localLengthFilter });
    paramView.addView(this.messageEditText, LayoutHelper.createFrame(-1, -2.0F, 83, 52.0F, 0.0F, 6.0F, 0.0F));
    this.messageEditText.setOnKeyListener(new View.OnKeyListener()
    {
      public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
      {
        if (paramInt == 4)
        {
          if ((PhotoViewerCaptionEnterView.this.windowView != null) && (PhotoViewerCaptionEnterView.this.hideActionMode()));
          while (true)
          {
            return true;
            if ((PhotoViewerCaptionEnterView.this.keyboardVisible) || (!PhotoViewerCaptionEnterView.this.isPopupShowing()))
              break;
            if (paramKeyEvent.getAction() != 1)
              continue;
            PhotoViewerCaptionEnterView.this.showPopup(0);
            return true;
          }
        }
        return false;
      }
    });
    this.messageEditText.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        if (PhotoViewerCaptionEnterView.this.isPopupShowing())
        {
          paramView = PhotoViewerCaptionEnterView.this;
          if (!AndroidUtilities.usingHardwareInput)
            break label29;
        }
        label29: for (int i = 0; ; i = 2)
        {
          paramView.showPopup(i);
          return;
        }
      }
    });
    this.messageEditText.addTextChangedListener(new TextWatcher()
    {
      boolean processChange = false;

      public void afterTextChanged(Editable paramEditable)
      {
        if (PhotoViewerCaptionEnterView.this.innerTextChange);
        do
          return;
        while (!this.processChange);
        ImageSpan[] arrayOfImageSpan = (ImageSpan[])paramEditable.getSpans(0, paramEditable.length(), ImageSpan.class);
        int i = 0;
        while (i < arrayOfImageSpan.length)
        {
          paramEditable.removeSpan(arrayOfImageSpan[i]);
          i += 1;
        }
        Emoji.replaceEmoji(paramEditable, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
        this.processChange = false;
      }

      public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
      }

      public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
      {
        if (PhotoViewerCaptionEnterView.this.innerTextChange);
        do
        {
          return;
          if (PhotoViewerCaptionEnterView.this.delegate == null)
            continue;
          PhotoViewerCaptionEnterView.this.delegate.onTextChanged(paramCharSequence);
        }
        while ((paramInt2 == paramInt3) || (paramInt3 - paramInt2 <= 1));
        this.processChange = true;
      }
    });
    paramContext = new ImageView(paramContext);
    paramContext.setScaleType(ImageView.ScaleType.CENTER);
    paramContext.setImageResource(2130837768);
    paramSizeNotifierFrameLayoutPhoto.addView(paramContext, LayoutHelper.createLinear(48, 48, 80));
    if (Build.VERSION.SDK_INT >= 21)
      paramContext.setBackgroundDrawable(Theme.createSelectorDrawable(1090519039));
    paramContext.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        PhotoViewerCaptionEnterView.this.delegate.onCaptionEnter();
      }
    });
  }

  private void fixActionMode(ActionMode paramActionMode)
  {
    try
    {
      Object localObject1 = Class.forName("com.android.internal.view.FloatingActionMode");
      Object localObject2 = ((Class)localObject1).getDeclaredField("mFloatingToolbar");
      ((Field)localObject2).setAccessible(true);
      localObject2 = ((Field)localObject2).get(paramActionMode);
      Object localObject3 = Class.forName("com.android.internal.widget.FloatingToolbar");
      Field localField = ((Class)localObject3).getDeclaredField("mPopup");
      localObject3 = ((Class)localObject3).getDeclaredField("mWidthChanged");
      localField.setAccessible(true);
      ((Field)localObject3).setAccessible(true);
      localObject2 = localField.get(localObject2);
      localField = Class.forName("com.android.internal.widget.FloatingToolbar$FloatingToolbarPopup").getDeclaredField("mParent");
      localField.setAccessible(true);
      if ((View)localField.get(localObject2) != this.windowView)
      {
        localField.set(localObject2, this.windowView);
        localObject1 = ((Class)localObject1).getDeclaredMethod("updateViewLocationInWindow", new Class[0]);
        ((Method)localObject1).setAccessible(true);
        ((Method)localObject1).invoke(paramActionMode, new Object[0]);
      }
      return;
    }
    catch (java.lang.Throwable paramActionMode)
    {
      FileLog.e(paramActionMode);
    }
  }

  private void onWindowSizeChanged()
  {
    int j = this.sizeNotifierLayout.getHeight();
    int i = j;
    if (!this.keyboardVisible)
      i = j - this.emojiPadding;
    if (this.delegate != null)
      this.delegate.onWindowSizeChanged(i);
  }

  private void openKeyboardInternal()
  {
    if (AndroidUtilities.usingHardwareInput);
    for (int i = 0; ; i = 2)
    {
      showPopup(i);
      openKeyboard();
      return;
    }
  }

  private void showPopup(int paramInt)
  {
    if (paramInt == 1)
    {
      if (this.emojiView == null)
      {
        this.emojiView = new EmojiView(false, false, getContext());
        this.emojiView.setListener(new EmojiView.Listener()
        {
          public boolean onBackspace()
          {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() == 0)
              return false;
            PhotoViewerCaptionEnterView.this.messageEditText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
          }

          public void onClearEmojiRecent()
          {
          }

          public void onEmojiSelected(String paramString)
          {
            if (PhotoViewerCaptionEnterView.this.messageEditText.length() + paramString.length() > 200)
              return;
            int j = PhotoViewerCaptionEnterView.this.messageEditText.getSelectionEnd();
            int i = j;
            if (j < 0)
              i = 0;
            try
            {
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, true);
              paramString = Emoji.replaceEmoji(paramString, PhotoViewerCaptionEnterView.this.messageEditText.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0F), false);
              PhotoViewerCaptionEnterView.this.messageEditText.setText(PhotoViewerCaptionEnterView.this.messageEditText.getText().insert(i, paramString));
              i += paramString.length();
              PhotoViewerCaptionEnterView.this.messageEditText.setSelection(i, i);
              return;
            }
            catch (Exception paramString)
            {
              FileLog.e(paramString);
              return;
            }
            finally
            {
              PhotoViewerCaptionEnterView.access$602(PhotoViewerCaptionEnterView.this, false);
            }
            throw paramString;
          }

          public void onGifSelected(TLRPC.Document paramDocument)
          {
          }

          public void onGifTab(boolean paramBoolean)
          {
          }

          public void onShowStickerSet(TLRPC.StickerSet paramStickerSet, TLRPC.InputStickerSet paramInputStickerSet)
          {
          }

          public void onStickerSelected(TLRPC.Document paramDocument)
          {
          }

          public void onStickerSetAdd(TLRPC.StickerSetCovered paramStickerSetCovered)
          {
          }

          public void onStickerSetRemove(TLRPC.StickerSetCovered paramStickerSetCovered)
          {
          }

          public void onStickersSettingsClick()
          {
          }

          public void onStickersTab(boolean paramBoolean)
          {
          }
        });
        this.sizeNotifierLayout.addView(this.emojiView);
      }
      this.emojiView.setVisibility(0);
      if (this.keyboardHeight <= 0)
        this.keyboardHeight = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200.0F));
      if (this.keyboardHeightLand <= 0)
        this.keyboardHeightLand = ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).getInt("kbd_height_land3", AndroidUtilities.dp(200.0F));
      if (AndroidUtilities.displaySize.x > AndroidUtilities.displaySize.y)
      {
        paramInt = this.keyboardHeightLand;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emojiView.getLayoutParams();
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = paramInt;
        this.emojiView.setLayoutParams(localLayoutParams);
        if ((!AndroidUtilities.isInMultiwindow) && (!this.forceFloatingEmoji))
          AndroidUtilities.hideKeyboard(this.messageEditText);
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = paramInt;
          this.sizeNotifierLayout.requestLayout();
          this.emojiButton.setImageResource(2130837790);
          onWindowSizeChanged();
        }
      }
    }
    do
    {
      return;
      paramInt = this.keyboardHeight;
      break;
      if (this.emojiButton != null)
        this.emojiButton.setImageResource(2130837838);
      if (this.emojiView == null)
        continue;
      this.emojiView.setVisibility(8);
    }
    while (this.sizeNotifierLayout == null);
    if (paramInt == 0)
      this.emojiPadding = 0;
    this.sizeNotifierLayout.requestLayout();
    onWindowSizeChanged();
  }

  public void closeKeyboard()
  {
    AndroidUtilities.hideKeyboard(this.messageEditText);
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if ((paramInt == NotificationCenter.emojiDidLoaded) && (this.emojiView != null))
      this.emojiView.invalidateViews();
  }

  public int getCursorPosition()
  {
    if (this.messageEditText == null)
      return 0;
    return this.messageEditText.getSelectionStart();
  }

  public int getEmojiPadding()
  {
    return this.emojiPadding;
  }

  public CharSequence getFieldCharSequence()
  {
    return this.messageEditText.getText();
  }

  public boolean hideActionMode()
  {
    if ((Build.VERSION.SDK_INT >= 23) && (this.currentActionMode != null))
      try
      {
        this.currentActionMode.finish();
        this.currentActionMode = null;
        return true;
      }
      catch (Exception localException)
      {
        while (true)
          FileLog.e(localException);
      }
    return false;
  }

  public void hidePopup()
  {
    if (isPopupShowing())
      showPopup(0);
  }

  public boolean isKeyboardVisible()
  {
    return ((AndroidUtilities.usingHardwareInput) && (getTag() != null)) || (this.keyboardVisible);
  }

  public boolean isPopupShowing()
  {
    return (this.emojiView != null) && (this.emojiView.getVisibility() == 0);
  }

  public boolean isPopupView(View paramView)
  {
    return paramView == this.emojiView;
  }

  public void onCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    this.sizeNotifierLayout.setDelegate(this);
  }

  public void onDestroy()
  {
    hidePopup();
    if (isKeyboardVisible())
      closeKeyboard();
    this.keyboardVisible = false;
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    if (this.sizeNotifierLayout != null)
      this.sizeNotifierLayout.setDelegate(null);
  }

  public void onSizeChanged(int paramInt, boolean paramBoolean)
  {
    if ((paramInt > AndroidUtilities.dp(50.0F)) && (this.keyboardVisible) && (!AndroidUtilities.isInMultiwindow) && (!this.forceFloatingEmoji))
    {
      if (paramBoolean)
      {
        this.keyboardHeightLand = paramInt;
        ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height_land3", this.keyboardHeightLand).commit();
      }
    }
    else if (isPopupShowing())
      if (!paramBoolean)
        break label238;
    label238: for (int i = this.keyboardHeightLand; ; i = this.keyboardHeight)
    {
      FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)this.emojiView.getLayoutParams();
      if ((localLayoutParams.width != AndroidUtilities.displaySize.x) || (localLayoutParams.height != i))
      {
        localLayoutParams.width = AndroidUtilities.displaySize.x;
        localLayoutParams.height = i;
        this.emojiView.setLayoutParams(localLayoutParams);
        if (this.sizeNotifierLayout != null)
        {
          this.emojiPadding = localLayoutParams.height;
          this.sizeNotifierLayout.requestLayout();
          onWindowSizeChanged();
        }
      }
      if ((this.lastSizeChangeValue1 != paramInt) || (this.lastSizeChangeValue2 != paramBoolean))
        break label246;
      onWindowSizeChanged();
      return;
      this.keyboardHeight = paramInt;
      ApplicationLoader.applicationContext.getSharedPreferences("emoji", 0).edit().putInt("kbd_height", this.keyboardHeight).commit();
      break;
    }
    label246: this.lastSizeChangeValue1 = paramInt;
    this.lastSizeChangeValue2 = paramBoolean;
    boolean bool = this.keyboardVisible;
    if (paramInt > 0);
    for (paramBoolean = true; ; paramBoolean = false)
    {
      this.keyboardVisible = paramBoolean;
      if ((this.keyboardVisible) && (isPopupShowing()))
        showPopup(0);
      if ((this.emojiPadding != 0) && (!this.keyboardVisible) && (this.keyboardVisible != bool) && (!isPopupShowing()))
      {
        this.emojiPadding = 0;
        this.sizeNotifierLayout.requestLayout();
      }
      onWindowSizeChanged();
      return;
    }
  }

  public void openKeyboard()
  {
    try
    {
      i = this.messageEditText.getSelectionStart();
      MotionEvent localMotionEvent = MotionEvent.obtain(0L, 0L, 0, 0.0F, 0.0F, 0);
      this.messageEditText.onTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
      localMotionEvent = MotionEvent.obtain(0L, 0L, 1, 0.0F, 0.0F, 0);
      this.messageEditText.onTouchEvent(localMotionEvent);
      localMotionEvent.recycle();
      AndroidUtilities.showKeyboard(this.messageEditText);
    }
    catch (Exception localException2)
    {
      try
      {
        this.messageEditText.setSelection(i);
        return;
        localException1 = localException1;
        int i = this.messageEditText.length();
        FileLog.e(localException1);
      }
      catch (Exception localException2)
      {
        FileLog.e(localException2);
      }
    }
  }

  public void replaceWithText(int paramInt1, int paramInt2, String paramString)
  {
    try
    {
      StringBuilder localStringBuilder = new StringBuilder(this.messageEditText.getText());
      localStringBuilder.replace(paramInt1, paramInt1 + paramInt2, paramString);
      this.messageEditText.setText(localStringBuilder);
      if (paramString.length() + paramInt1 <= this.messageEditText.length())
      {
        this.messageEditText.setSelection(paramString.length() + paramInt1);
        return;
      }
      this.messageEditText.setSelection(this.messageEditText.length());
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e(paramString);
    }
  }

  public void setDelegate(PhotoViewerCaptionEnterViewDelegate paramPhotoViewerCaptionEnterViewDelegate)
  {
    this.delegate = paramPhotoViewerCaptionEnterViewDelegate;
  }

  public void setFieldFocused(boolean paramBoolean)
  {
    if (this.messageEditText == null);
    do
      while (true)
      {
        return;
        if (!paramBoolean)
          break;
        if (this.messageEditText.isFocused())
          continue;
        this.messageEditText.postDelayed(new Runnable()
        {
          public void run()
          {
            if (PhotoViewerCaptionEnterView.this.messageEditText != null);
            try
            {
              PhotoViewerCaptionEnterView.this.messageEditText.requestFocus();
              return;
            }
            catch (Exception localException)
            {
              FileLog.e(localException);
            }
          }
        }
        , 600L);
        return;
      }
    while ((!this.messageEditText.isFocused()) || (this.keyboardVisible));
    this.messageEditText.clearFocus();
  }

  public void setFieldText(CharSequence paramCharSequence)
  {
    if (this.messageEditText == null);
    do
    {
      return;
      this.messageEditText.setText(paramCharSequence);
      this.messageEditText.setSelection(this.messageEditText.getText().length());
    }
    while (this.delegate == null);
    this.delegate.onTextChanged(this.messageEditText.getText());
  }

  public void setForceFloatingEmoji(boolean paramBoolean)
  {
    this.forceFloatingEmoji = paramBoolean;
  }

  public static abstract interface PhotoViewerCaptionEnterViewDelegate
  {
    public abstract void onCaptionEnter();

    public abstract void onTextChanged(CharSequence paramCharSequence);

    public abstract void onWindowSizeChanged(int paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PhotoViewerCaptionEnterView
 * JD-Core Version:    0.6.0
 */