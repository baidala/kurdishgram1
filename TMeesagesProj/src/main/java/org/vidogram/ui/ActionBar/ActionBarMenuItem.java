package org.vidogram.ui.ActionBar;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.view.View.OnTouchListener;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.Components.LayoutHelper;

public class ActionBarMenuItem extends FrameLayout
{
  private static Method layoutInScreenMethod;
  private boolean allowCloseAnimation = true;
  private ImageView clearButton;
  private ActionBarMenuItemDelegate delegate;
  protected ImageView iconView;
  private boolean isSearchField = false;
  private boolean layoutInScreen;
  private ActionBarMenuItemSearchListener listener;
  private int[] location;
  private int menuHeight = AndroidUtilities.dp(16.0F);
  protected boolean overrideMenuClick;
  private ActionBarMenu parentMenu;
  private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
  private ActionBarPopupWindow popupWindow;
  private boolean processedPopupClick;
  private Rect rect;
  private FrameLayout searchContainer;
  private EditText searchField;
  private View selectedMenuView;
  private boolean showFromBottom;
  private Runnable showMenuRunnable;
  private int subMenuOpenSide = 0;

  public ActionBarMenuItem(Context paramContext, ActionBarMenu paramActionBarMenu, int paramInt1, int paramInt2)
  {
    super(paramContext);
    if (paramInt1 != 0)
      setBackgroundDrawable(Theme.createSelectorDrawable(paramInt1));
    this.parentMenu = paramActionBarMenu;
    this.iconView = new ImageView(paramContext);
    this.iconView.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.iconView, LayoutHelper.createFrame(-1, -1.0F));
    if (paramInt2 != 0)
      this.iconView.setColorFilter(new PorterDuffColorFilter(paramInt2, PorterDuff.Mode.MULTIPLY));
  }

  private void updateOrShowPopup(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i;
    Object localObject;
    if (this.showFromBottom)
    {
      getLocationOnScreen(this.location);
      int k = this.location[1] - AndroidUtilities.statusBarHeight + getMeasuredHeight() - this.menuHeight;
      int j = -this.menuHeight;
      i = j;
      if (k < 0)
        i = j - k;
      if (paramBoolean1)
        this.popupLayout.scrollToTop();
      if (this.subMenuOpenSide != 0)
        break label384;
      if (!this.showFromBottom)
        break label192;
      if (paramBoolean1)
        this.popupWindow.showAsDropDown(this, -this.popupLayout.getMeasuredWidth() + getMeasuredWidth(), i);
      if (paramBoolean2)
      {
        localObject = this.popupWindow;
        j = -this.popupLayout.getMeasuredWidth();
        ((ActionBarPopupWindow)localObject).update(this, getMeasuredWidth() + j, i, -1, -1);
      }
    }
    label192: 
    do
    {
      do
      {
        do
        {
          do
          {
            return;
            if ((this.parentMenu != null) && (this.subMenuOpenSide == 0))
            {
              i = -this.parentMenu.parentActionBar.getMeasuredHeight() + this.parentMenu.getTop();
              break;
            }
            i = -getMeasuredHeight();
            break;
            if (this.parentMenu == null)
              break label289;
            localObject = this.parentMenu.parentActionBar;
            if (!paramBoolean1)
              continue;
            this.popupWindow.showAsDropDown((View)localObject, getLeft() + this.parentMenu.getLeft() + getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i);
          }
          while (!paramBoolean2);
          this.popupWindow.update((View)localObject, getLeft() + this.parentMenu.getLeft() + getMeasuredWidth() - this.popupLayout.getMeasuredWidth(), i, -1, -1);
          return;
        }
        while (getParent() == null);
        localObject = (View)getParent();
        if (!paramBoolean1)
          continue;
        this.popupWindow.showAsDropDown((View)localObject, ((View)localObject).getMeasuredWidth() - this.popupLayout.getMeasuredWidth() - getLeft() - ((View)localObject).getLeft(), i);
      }
      while (!paramBoolean2);
      this.popupWindow.update((View)localObject, ((View)localObject).getMeasuredWidth() - this.popupLayout.getMeasuredWidth() - getLeft() - ((View)localObject).getLeft(), i, -1, -1);
      return;
      if (!paramBoolean1)
        continue;
      this.popupWindow.showAsDropDown(this, -AndroidUtilities.dp(8.0F), i);
    }
    while (!paramBoolean2);
    label289: this.popupWindow.update(this, -AndroidUtilities.dp(8.0F), i, -1, -1);
    label384:
  }

  public TextView addSubItem(int paramInt, String paramString)
  {
    if (this.popupLayout == null)
    {
      this.rect = new Rect();
      this.location = new int[2];
      this.popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext());
      this.popupLayout.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
        {
          if ((paramMotionEvent.getActionMasked() == 0) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
          {
            paramView.getHitRect(ActionBarMenuItem.this.rect);
            if (!ActionBarMenuItem.this.rect.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY()))
              ActionBarMenuItem.this.popupWindow.dismiss();
          }
          return false;
        }
      });
      this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener()
      {
        public void onDispatchKeyEvent(KeyEvent paramKeyEvent)
        {
          if ((paramKeyEvent.getKeyCode() == 4) && (paramKeyEvent.getRepeatCount() == 0) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
            ActionBarMenuItem.this.popupWindow.dismiss();
        }
      });
    }
    TextView localTextView = new TextView(getContext());
    localTextView.setTextColor(Theme.getColor("actionBarDefaultSubmenuItem"));
    localTextView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    if (!LocaleController.isRTL)
      localTextView.setGravity(16);
    while (true)
    {
      localTextView.setPadding(AndroidUtilities.dp(16.0F), 0, AndroidUtilities.dp(16.0F), 0);
      localTextView.setTextSize(1, 18.0F);
      localTextView.setMinWidth(AndroidUtilities.dp(196.0F));
      localTextView.setTag(Integer.valueOf(paramInt));
      localTextView.setText(paramString);
      this.popupLayout.setShowedFromBotton(this.showFromBottom);
      this.popupLayout.addView(localTextView);
      paramString = (LinearLayout.LayoutParams)localTextView.getLayoutParams();
      if (LocaleController.isRTL)
        paramString.gravity = 5;
      paramString.width = -1;
      paramString.height = AndroidUtilities.dp(48.0F);
      localTextView.setLayoutParams(paramString);
      localTextView.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if ((ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
            if (!ActionBarMenuItem.this.processedPopupClick);
          do
          {
            return;
            ActionBarMenuItem.access$202(ActionBarMenuItem.this, true);
            ActionBarMenuItem.this.popupWindow.dismiss(ActionBarMenuItem.this.allowCloseAnimation);
            if (ActionBarMenuItem.this.parentMenu == null)
              continue;
            ActionBarMenuItem.this.parentMenu.onItemClick(((Integer)paramView.getTag()).intValue());
            return;
          }
          while (ActionBarMenuItem.this.delegate == null);
          ActionBarMenuItem.this.delegate.onItemClick(((Integer)paramView.getTag()).intValue());
        }
      });
      paramInt = this.menuHeight;
      this.menuHeight = (paramString.height + paramInt);
      return localTextView;
      localTextView.setGravity(21);
    }
  }

  public void closeSubMenu()
  {
    if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
      this.popupWindow.dismiss();
  }

  public ImageView getImageView()
  {
    return this.iconView;
  }

  public EditText getSearchField()
  {
    return this.searchField;
  }

  public boolean hasSubMenu()
  {
    return this.popupLayout != null;
  }

  public void hideSubItem(int paramInt)
  {
    View localView = this.popupLayout.findViewWithTag(Integer.valueOf(paramInt));
    if (localView != null)
      localView.setVisibility(8);
  }

  public boolean isSearchField()
  {
    return this.isSearchField;
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
      updateOrShowPopup(false, true);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getActionMasked() == 0)
      if ((hasSubMenu()) && ((this.popupWindow == null) || ((this.popupWindow != null) && (!this.popupWindow.isShowing()))))
      {
        this.showMenuRunnable = new Runnable()
        {
          public void run()
          {
            if (ActionBarMenuItem.this.getParent() != null)
              ActionBarMenuItem.this.getParent().requestDisallowInterceptTouchEvent(true);
            ActionBarMenuItem.this.toggleSubMenu();
          }
        };
        AndroidUtilities.runOnUIThread(this.showMenuRunnable, 200L);
      }
    while (true)
    {
      return super.onTouchEvent(paramMotionEvent);
      if (paramMotionEvent.getActionMasked() == 2)
      {
        if ((hasSubMenu()) && ((this.popupWindow == null) || ((this.popupWindow != null) && (!this.popupWindow.isShowing()))))
        {
          if (paramMotionEvent.getY() <= getHeight())
            continue;
          if (getParent() != null)
            getParent().requestDisallowInterceptTouchEvent(true);
          toggleSubMenu();
          return true;
        }
        if ((this.popupWindow == null) || (!this.popupWindow.isShowing()))
          continue;
        getLocationOnScreen(this.location);
        float f3 = paramMotionEvent.getX();
        float f4 = this.location[0];
        float f1 = paramMotionEvent.getY();
        float f2 = this.location[1];
        this.popupLayout.getLocationOnScreen(this.location);
        f3 = f3 + f4 - this.location[0];
        f1 = f1 + f2 - this.location[1];
        this.selectedMenuView = null;
        int i = 0;
        label240: View localView;
        if (i < this.popupLayout.getItemsCount())
        {
          localView = this.popupLayout.getItemAt(i);
          localView.getHitRect(this.rect);
          if (((Integer)localView.getTag()).intValue() < 100)
          {
            if (this.rect.contains((int)f3, (int)f1))
              break label343;
            localView.setPressed(false);
            localView.setSelected(false);
            if (Build.VERSION.SDK_INT == 21)
              localView.getBackground().setVisible(false, false);
          }
        }
        while (true)
        {
          i += 1;
          break label240;
          break;
          label343: localView.setPressed(true);
          localView.setSelected(true);
          if (Build.VERSION.SDK_INT >= 21)
          {
            if (Build.VERSION.SDK_INT == 21)
              localView.getBackground().setVisible(true, false);
            localView.drawableHotspotChanged(f3, f1 - localView.getTop());
          }
          this.selectedMenuView = localView;
        }
      }
      if ((this.popupWindow != null) && (this.popupWindow.isShowing()) && (paramMotionEvent.getActionMasked() == 1))
      {
        if (this.selectedMenuView != null)
        {
          this.selectedMenuView.setSelected(false);
          if (this.parentMenu != null)
            this.parentMenu.onItemClick(((Integer)this.selectedMenuView.getTag()).intValue());
          while (true)
          {
            this.popupWindow.dismiss(this.allowCloseAnimation);
            break;
            if (this.delegate == null)
              continue;
            this.delegate.onItemClick(((Integer)this.selectedMenuView.getTag()).intValue());
          }
        }
        this.popupWindow.dismiss();
        continue;
      }
      if (this.selectedMenuView == null)
        continue;
      this.selectedMenuView.setSelected(false);
      this.selectedMenuView = null;
    }
  }

  public void openSearch(boolean paramBoolean)
  {
    if ((this.searchContainer == null) || (this.searchContainer.getVisibility() == 0) || (this.parentMenu == null))
      return;
    this.parentMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(paramBoolean));
  }

  protected void redrawPopup(int paramInt)
  {
    if (this.popupLayout != null)
    {
      this.popupLayout.backgroundDrawable.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
      this.popupLayout.invalidate();
    }
  }

  public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener paramActionBarMenuItemSearchListener)
  {
    this.listener = paramActionBarMenuItemSearchListener;
    return this;
  }

  public ActionBarMenuItem setAllowCloseAnimation(boolean paramBoolean)
  {
    this.allowCloseAnimation = paramBoolean;
    return this;
  }

  public void setDelegate(ActionBarMenuItemDelegate paramActionBarMenuItemDelegate)
  {
    this.delegate = paramActionBarMenuItemDelegate;
  }

  public void setIcon(int paramInt)
  {
    this.iconView.setImageResource(paramInt);
  }

  public void setIconColor(int paramInt)
  {
    this.iconView.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
    if (this.clearButton != null)
      this.clearButton.setColorFilter(new PorterDuffColorFilter(paramInt, PorterDuff.Mode.MULTIPLY));
  }

  public ActionBarMenuItem setIsSearchField(boolean paramBoolean)
  {
    if (this.parentMenu == null)
      return this;
    if ((paramBoolean) && (this.searchContainer == null))
    {
      this.searchContainer = new FrameLayout(getContext());
      this.parentMenu.addView(this.searchContainer, 0, LayoutHelper.createLinear(0, -1, 1.0F, 6, 0, 0, 0));
      this.searchContainer.setVisibility(8);
      this.searchField = new EditText(getContext());
      this.searchField.setTextSize(1, 18.0F);
      this.searchField.setHintTextColor(Theme.getColor("actionBarDefaultSearchPlaceholder"));
      this.searchField.setTextColor(Theme.getColor("actionBarDefaultSearch"));
      this.searchField.setSingleLine(true);
      this.searchField.setBackgroundResource(0);
      this.searchField.setPadding(0, 0, 0, 0);
      int i = this.searchField.getInputType();
      this.searchField.setInputType(i | 0x80000);
      this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback()
      {
        public boolean onActionItemClicked(ActionMode paramActionMode, MenuItem paramMenuItem)
        {
          return false;
        }

        public boolean onCreateActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          return false;
        }

        public void onDestroyActionMode(ActionMode paramActionMode)
        {
        }

        public boolean onPrepareActionMode(ActionMode paramActionMode, Menu paramMenu)
        {
          return false;
        }
      });
      this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener()
      {
        public boolean onEditorAction(TextView paramTextView, int paramInt, KeyEvent paramKeyEvent)
        {
          if ((paramKeyEvent != null) && (((paramKeyEvent.getAction() == 1) && (paramKeyEvent.getKeyCode() == 84)) || ((paramKeyEvent.getAction() == 0) && (paramKeyEvent.getKeyCode() == 66))))
          {
            AndroidUtilities.hideKeyboard(ActionBarMenuItem.this.searchField);
            if (ActionBarMenuItem.this.listener != null)
              ActionBarMenuItem.this.listener.onSearchPressed(ActionBarMenuItem.this.searchField);
          }
          return false;
        }
      });
      this.searchField.addTextChangedListener(new TextWatcher()
      {
        public void afterTextChanged(Editable paramEditable)
        {
        }

        public void beforeTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
        }

        public void onTextChanged(CharSequence paramCharSequence, int paramInt1, int paramInt2, int paramInt3)
        {
          if (ActionBarMenuItem.this.listener != null)
            ActionBarMenuItem.this.listener.onTextChanged(ActionBarMenuItem.this.searchField);
          ImageView localImageView;
          float f;
          if (ActionBarMenuItem.this.clearButton != null)
          {
            localImageView = ActionBarMenuItem.this.clearButton;
            if ((paramCharSequence != null) && (paramCharSequence.length() != 0))
              break label71;
            f = 0.6F;
          }
          while (true)
          {
            localImageView.setAlpha(f);
            return;
            label71: f = 1.0F;
          }
        }
      });
    }
    try
    {
      Field localField = TextView.class.getDeclaredField("mCursorDrawableRes");
      localField.setAccessible(true);
      localField.set(this.searchField, Integer.valueOf(2130838047));
      label240: this.searchField.setImeOptions(33554435);
      this.searchField.setTextIsSelectable(false);
      this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0F, 16, 0.0F, 0.0F, 48.0F, 0.0F));
      this.clearButton = new ImageView(getContext());
      this.clearButton.setImageResource(2130837766);
      this.clearButton.setColorFilter(new PorterDuffColorFilter(this.parentMenu.parentActionBar.itemsColor, PorterDuff.Mode.MULTIPLY));
      this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
      this.clearButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          ActionBarMenuItem.this.searchField.setText("");
          ActionBarMenuItem.this.searchField.requestFocus();
          AndroidUtilities.showKeyboard(ActionBarMenuItem.this.searchField);
        }
      });
      this.searchContainer.addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
      this.isSearchField = paramBoolean;
      return this;
    }
    catch (Exception localException)
    {
      break label240;
    }
  }

  public void setLayoutInScreen(boolean paramBoolean)
  {
    this.layoutInScreen = paramBoolean;
  }

  public ActionBarMenuItem setOverrideMenuClick(boolean paramBoolean)
  {
    this.overrideMenuClick = paramBoolean;
    return this;
  }

  public void setPopupItemsColor(int paramInt)
  {
    if (this.popupLayout == null);
    while (true)
    {
      return;
      int j = this.popupLayout.linearLayout.getChildCount();
      int i = 0;
      while (i < j)
      {
        View localView = this.popupLayout.linearLayout.getChildAt(i);
        if ((localView instanceof TextView))
          ((TextView)localView).setTextColor(paramInt);
        i += 1;
      }
    }
  }

  public void setShowFromBottom(boolean paramBoolean)
  {
    this.showFromBottom = paramBoolean;
    if (this.popupLayout != null)
      this.popupLayout.setShowedFromBotton(this.showFromBottom);
  }

  public void setSubMenuOpenSide(int paramInt)
  {
    this.subMenuOpenSide = paramInt;
  }

  public void showSubItem(int paramInt)
  {
    View localView = this.popupLayout.findViewWithTag(Integer.valueOf(paramInt));
    if (localView != null)
      localView.setVisibility(0);
  }

  public boolean toggleSearch(boolean paramBoolean)
  {
    if (this.searchContainer == null);
    while (true)
    {
      return false;
      if (this.searchContainer.getVisibility() != 0)
        break;
      if ((this.listener != null) && ((this.listener == null) || (!this.listener.canCollapseSearch())))
        continue;
      this.searchContainer.setVisibility(8);
      this.searchField.clearFocus();
      setVisibility(0);
      AndroidUtilities.hideKeyboard(this.searchField);
      if (this.listener == null)
        continue;
      this.listener.onSearchCollapse();
      return false;
    }
    this.searchContainer.setVisibility(0);
    setVisibility(8);
    this.searchField.setText("");
    this.searchField.requestFocus();
    if (paramBoolean)
      AndroidUtilities.showKeyboard(this.searchField);
    if (this.listener != null)
      this.listener.onSearchExpand();
    return true;
  }

  public void toggleSubMenu()
  {
    if (this.popupLayout == null)
      return;
    if (this.showMenuRunnable != null)
    {
      AndroidUtilities.cancelRunOnUIThread(this.showMenuRunnable);
      this.showMenuRunnable = null;
    }
    if ((this.popupWindow != null) && (this.popupWindow.isShowing()))
    {
      this.popupWindow.dismiss();
      return;
    }
    if (this.popupWindow == null)
    {
      this.popupWindow = new ActionBarPopupWindow(this.popupLayout, -2, -2);
      if (Build.VERSION.SDK_INT < 19)
        break label287;
      this.popupWindow.setAnimationStyle(0);
    }
    while (true)
    {
      this.popupWindow.setOutsideTouchable(true);
      this.popupWindow.setClippingEnabled(true);
      if (this.layoutInScreen);
      try
      {
        if (layoutInScreenMethod == null)
        {
          layoutInScreenMethod = PopupWindow.class.getDeclaredMethod("setLayoutInScreenEnabled", new Class[] { Boolean.TYPE });
          layoutInScreenMethod.setAccessible(true);
        }
        layoutInScreenMethod.invoke(this.popupWindow, new Object[] { Boolean.valueOf(true) });
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), -2147483648), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0F), -2147483648));
        this.popupWindow.getContentView().setFocusableInTouchMode(true);
        this.popupWindow.getContentView().setOnKeyListener(new View.OnKeyListener()
        {
          public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
          {
            if ((paramInt == 82) && (paramKeyEvent.getRepeatCount() == 0) && (paramKeyEvent.getAction() == 1) && (ActionBarMenuItem.this.popupWindow != null) && (ActionBarMenuItem.this.popupWindow.isShowing()))
            {
              ActionBarMenuItem.this.popupWindow.dismiss();
              return true;
            }
            return false;
          }
        });
        this.processedPopupClick = false;
        this.popupWindow.setFocusable(true);
        if (this.popupLayout.getMeasuredWidth() == 0)
        {
          updateOrShowPopup(true, true);
          this.popupWindow.startAnimation();
          return;
          label287: this.popupWindow.setAnimationStyle(2131362023);
        }
      }
      catch (Exception localException)
      {
        while (true)
        {
          FileLog.e(localException);
          continue;
          updateOrShowPopup(true, false);
        }
      }
    }
  }

  public static abstract interface ActionBarMenuItemDelegate
  {
    public abstract void onItemClick(int paramInt);
  }

  public static class ActionBarMenuItemSearchListener
  {
    public boolean canCollapseSearch()
    {
      return true;
    }

    public void onSearchCollapse()
    {
    }

    public void onSearchExpand()
    {
    }

    public void onSearchPressed(EditText paramEditText)
    {
    }

    public void onTextChanged(EditText paramEditText)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.ActionBarMenuItem
 * JD-Core Version:    0.6.0
 */