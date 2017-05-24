package org.vidogram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnApplyWindowInsetsListener;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowInsets;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class DrawerLayoutContainer extends FrameLayout
{
  private static final int MIN_DRAWER_MARGIN = 64;
  private boolean allowDrawContent = true;
  private boolean allowOpenDrawer;
  private boolean beginTrackingSent;
  private AnimatorSet currentAnimation;
  private ViewGroup drawerLayout;
  private boolean drawerOpened;
  private float drawerPosition;
  private boolean inLayout;
  private Object lastInsets;
  private boolean maybeStartTracking;
  private int minDrawerMargin = (int)(64.0F * AndroidUtilities.density + 0.5F);
  private int paddingTop;
  private ActionBarLayout parentActionBarLayout;
  private float scrimOpacity;
  private Paint scrimPaint = new Paint();
  private Drawable shadowLeft;
  private boolean startedTracking;
  private int startedTrackingPointerId;
  private int startedTrackingX;
  private int startedTrackingY;
  private VelocityTracker velocityTracker;

  public DrawerLayoutContainer(Context paramContext)
  {
    super(paramContext);
    setDescendantFocusability(262144);
    setFocusableInTouchMode(true);
    if (Build.VERSION.SDK_INT >= 21)
    {
      setFitsSystemWindows(true);
      setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener()
      {
        @SuppressLint({"NewApi"})
        public WindowInsets onApplyWindowInsets(View paramView, WindowInsets paramWindowInsets)
        {
          paramView = (DrawerLayoutContainer)paramView;
          AndroidUtilities.statusBarHeight = paramWindowInsets.getSystemWindowInsetTop();
          DrawerLayoutContainer.access$002(DrawerLayoutContainer.this, paramWindowInsets);
          if ((paramWindowInsets.getSystemWindowInsetTop() <= 0) && (DrawerLayoutContainer.this.getBackground() == null));
          for (boolean bool = true; ; bool = false)
          {
            paramView.setWillNotDraw(bool);
            paramView.requestLayout();
            return paramWindowInsets.consumeSystemWindowInsets();
          }
        }
      });
      setSystemUiVisibility(1280);
    }
    this.shadowLeft = getResources().getDrawable(2130837920);
  }

  @SuppressLint({"NewApi"})
  private void applyMarginInsets(ViewGroup.MarginLayoutParams paramMarginLayoutParams, Object paramObject, int paramInt, boolean paramBoolean)
  {
    int i = 0;
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3)
    {
      paramObject = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
      paramMarginLayoutParams.leftMargin = paramObject.getSystemWindowInsetLeft();
      if (!paramBoolean)
        break label107;
    }
    label107: for (paramInt = i; ; paramInt = paramObject.getSystemWindowInsetTop())
    {
      paramMarginLayoutParams.topMargin = paramInt;
      paramMarginLayoutParams.rightMargin = paramObject.getSystemWindowInsetRight();
      paramMarginLayoutParams.bottomMargin = paramObject.getSystemWindowInsetBottom();
      return;
      paramObject = localWindowInsets;
      if (paramInt != 5)
        break;
      paramObject = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
      break;
    }
  }

  @SuppressLint({"NewApi"})
  private void dispatchChildInsets(View paramView, Object paramObject, int paramInt)
  {
    WindowInsets localWindowInsets = (WindowInsets)paramObject;
    if (paramInt == 3)
      paramObject = localWindowInsets.replaceSystemWindowInsets(localWindowInsets.getSystemWindowInsetLeft(), localWindowInsets.getSystemWindowInsetTop(), 0, localWindowInsets.getSystemWindowInsetBottom());
    while (true)
    {
      paramView.dispatchApplyWindowInsets(paramObject);
      return;
      paramObject = localWindowInsets;
      if (paramInt != 5)
        continue;
      paramObject = localWindowInsets.replaceSystemWindowInsets(0, localWindowInsets.getSystemWindowInsetTop(), localWindowInsets.getSystemWindowInsetRight(), localWindowInsets.getSystemWindowInsetBottom());
    }
  }

  private float getScrimOpacity()
  {
    return this.scrimOpacity;
  }

  private int getTopInset(Object paramObject)
  {
    int j = 0;
    int i = j;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = j;
      if (paramObject != null)
        i = ((WindowInsets)paramObject).getSystemWindowInsetTop();
    }
    return i;
  }

  private void onDrawerAnimationEnd(boolean paramBoolean)
  {
    this.startedTracking = false;
    this.currentAnimation = null;
    this.drawerOpened = paramBoolean;
    if ((!paramBoolean) && ((this.drawerLayout instanceof ListView)))
      ((ListView)this.drawerLayout).setSelectionFromTop(0, 0);
  }

  private void prepareForDrawerOpen(MotionEvent paramMotionEvent)
  {
    this.maybeStartTracking = false;
    this.startedTracking = true;
    if (paramMotionEvent != null)
      this.startedTrackingX = (int)paramMotionEvent.getX();
    this.beginTrackingSent = false;
  }

  private void setScrimOpacity(float paramFloat)
  {
    this.scrimOpacity = paramFloat;
    invalidate();
  }

  public void cancelCurrentAnimation()
  {
    if (this.currentAnimation != null)
    {
      this.currentAnimation.cancel();
      this.currentAnimation = null;
    }
  }

  public void closeDrawer(boolean paramBoolean)
  {
    cancelCurrentAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "drawerPosition", new float[] { 0.0F }) });
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    if (paramBoolean)
      localAnimatorSet.setDuration(Math.max((int)(200.0F / this.drawerLayout.getMeasuredWidth() * this.drawerPosition), 50));
    while (true)
    {
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          DrawerLayoutContainer.this.onDrawerAnimationEnd(false);
        }
      });
      localAnimatorSet.start();
      return;
      localAnimatorSet.setDuration(300L);
    }
  }

  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    if (!this.allowDrawContent)
      return false;
    int i2 = getHeight();
    int m;
    int n;
    int i;
    int k;
    int j;
    int i3;
    int i4;
    label64: View localView;
    if (paramView != this.drawerLayout)
    {
      m = 1;
      n = 0;
      i = 0;
      k = 0;
      j = 0;
      i3 = getWidth();
      i4 = paramCanvas.save();
      if (m == 0)
        break label228;
      int i5 = getChildCount();
      k = 0;
      if (k >= i5)
        break label193;
      localView = getChildAt(k);
      if ((localView.getVisibility() != 0) || (localView == this.drawerLayout))
        break label403;
      i = k;
    }
    label403: 
    while (true)
    {
      n = j;
      if (localView != paramView)
      {
        n = j;
        if (localView.getVisibility() == 0)
        {
          n = j;
          if (localView == this.drawerLayout)
          {
            if (localView.getHeight() >= i2)
              break label168;
            n = j;
          }
        }
      }
      while (true)
      {
        k += 1;
        j = n;
        break label64;
        m = 0;
        break;
        label168: int i1 = localView.getRight();
        n = j;
        if (i1 <= j)
          continue;
        n = i1;
      }
      label193: k = j;
      n = i;
      if (j != 0)
      {
        paramCanvas.clipRect(j, 0, i3, getHeight());
        n = i;
        k = j;
      }
      label228: boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
      paramCanvas.restoreToCount(i4);
      if ((this.scrimOpacity > 0.0F) && (m != 0))
        if (indexOfChild(paramView) == n)
        {
          this.scrimPaint.setColor((int)(153.0F * this.scrimOpacity) << 24);
          paramCanvas.drawRect(k, 0.0F, i3, getHeight(), this.scrimPaint);
        }
      while (true)
      {
        return bool;
        if (this.shadowLeft == null)
          continue;
        float f = Math.max(0.0F, Math.min(this.drawerPosition / AndroidUtilities.dp(20.0F), 1.0F));
        if (f == 0.0F)
          continue;
        this.shadowLeft.setBounds((int)this.drawerPosition, paramView.getTop(), (int)this.drawerPosition + this.shadowLeft.getIntrinsicWidth(), paramView.getBottom());
        this.shadowLeft.setAlpha((int)(f * 255.0F));
        this.shadowLeft.draw(paramCanvas);
      }
    }
  }

  public View getDrawerLayout()
  {
    return this.drawerLayout;
  }

  public float getDrawerPosition()
  {
    return this.drawerPosition;
  }

  public boolean hasOverlappingRendering()
  {
    return false;
  }

  public boolean isDrawerOpened()
  {
    return this.drawerOpened;
  }

  public void moveDrawerByX(float paramFloat)
  {
    setDrawerPosition(this.drawerPosition + paramFloat);
  }

  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    return (this.parentActionBarLayout.checkTransitionAnimation()) || (onTouchEvent(paramMotionEvent));
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.inLayout = true;
    paramInt2 = getChildCount();
    paramInt1 = 0;
    if (paramInt1 < paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView.getVisibility() == 8);
      while (true)
      {
        paramInt1 += 1;
        break;
        FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
        try
        {
          if (this.drawerLayout == localView)
            break label120;
          localView.layout(localLayoutParams.leftMargin, localLayoutParams.topMargin + getPaddingTop(), localLayoutParams.leftMargin + localView.getMeasuredWidth(), localLayoutParams.topMargin + localView.getMeasuredHeight() + getPaddingTop());
        }
        catch (Exception localException)
        {
          FileLog.e(localException);
        }
        continue;
        label120: localException.layout(-localException.getMeasuredWidth(), localLayoutParams.topMargin + getPaddingTop(), 0, localLayoutParams.topMargin + localException.getMeasuredHeight() + getPaddingTop());
      }
    }
    this.inLayout = false;
  }

  @SuppressLint({"NewApi"})
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int m = View.MeasureSpec.getSize(paramInt1);
    int j = View.MeasureSpec.getSize(paramInt2);
    setMeasuredDimension(m, j);
    int i = j;
    label101: int k;
    label110: View localView;
    if (Build.VERSION.SDK_INT < 21)
    {
      this.inLayout = true;
      if (j == AndroidUtilities.displaySize.y + AndroidUtilities.statusBarHeight)
      {
        if ((getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
          setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        i = AndroidUtilities.displaySize.y;
        this.inLayout = false;
      }
    }
    else
    {
      if ((this.lastInsets == null) || (Build.VERSION.SDK_INT < 21))
        break label171;
      j = 1;
      int n = getChildCount();
      k = 0;
      if (k >= n)
        return;
      localView = getChildAt(k);
      if (localView.getVisibility() != 8)
        break label177;
    }
    while (true)
    {
      k += 1;
      break label110;
      i = j;
      if (!(getLayoutParams() instanceof ViewGroup.MarginLayoutParams))
        break;
      setPadding(0, 0, 0, 0);
      i = j;
      break;
      label171: j = 0;
      break label101;
      label177: FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
      if (j != 0)
      {
        if (!localView.getFitsSystemWindows())
          break label271;
        dispatchChildInsets(localView, this.lastInsets, localLayoutParams.gravity);
      }
      label271: 
      do
      {
        if (this.drawerLayout == localView)
          break label324;
        localView.measure(View.MeasureSpec.makeMeasureSpec(m - localLayoutParams.leftMargin - localLayoutParams.rightMargin, 1073741824), View.MeasureSpec.makeMeasureSpec(i - localLayoutParams.topMargin - localLayoutParams.bottomMargin, 1073741824));
        break;
      }
      while (localView.getTag() != null);
      Object localObject = this.lastInsets;
      int i1 = localLayoutParams.gravity;
      if (Build.VERSION.SDK_INT >= 21);
      for (boolean bool = true; ; bool = false)
      {
        applyMarginInsets(localLayoutParams, localObject, i1, bool);
        break;
      }
      label324: localView.setPadding(0, 0, 0, 0);
      localView.measure(getChildMeasureSpec(paramInt1, this.minDrawerMargin + localLayoutParams.leftMargin + localLayoutParams.rightMargin, localLayoutParams.width), getChildMeasureSpec(paramInt2, localLayoutParams.topMargin + localLayoutParams.bottomMargin, localLayoutParams.height));
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool2 = false;
    boolean bool3 = true;
    boolean bool1 = bool2;
    if (!this.parentActionBarLayout.checkTransitionAnimation())
    {
      if ((!this.drawerOpened) || (paramMotionEvent == null) || (paramMotionEvent.getX() <= this.drawerPosition) || (this.startedTracking))
        break label69;
      if (paramMotionEvent.getAction() == 1)
        closeDrawer(false);
      bool1 = true;
    }
    while (true)
    {
      return bool1;
      label69: if ((!this.allowOpenDrawer) || (this.parentActionBarLayout.fragmentsStack.size() != 1))
        break;
      if ((paramMotionEvent == null) || ((paramMotionEvent.getAction() != 0) && (paramMotionEvent.getAction() != 2)) || (this.startedTracking) || (this.maybeStartTracking))
        break label203;
      if (!this.drawerOpened)
      {
        bool1 = bool2;
        if ((int)paramMotionEvent.getX() > getMeasuredWidth() / 5)
          continue;
      }
      this.startedTrackingPointerId = paramMotionEvent.getPointerId(0);
      this.maybeStartTracking = true;
      this.startedTrackingX = (int)paramMotionEvent.getX();
      this.startedTrackingY = (int)paramMotionEvent.getY();
      cancelCurrentAnimation();
      if (this.velocityTracker == null)
        break;
      this.velocityTracker.clear();
    }
    label203: float f1;
    float f2;
    do
      while (true)
      {
        return this.startedTracking;
        if ((paramMotionEvent == null) || (paramMotionEvent.getAction() != 2) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId))
          break;
        if (this.velocityTracker == null)
          this.velocityTracker = VelocityTracker.obtain();
        f1 = (int)(paramMotionEvent.getX() - this.startedTrackingX);
        f2 = Math.abs((int)paramMotionEvent.getY() - this.startedTrackingY);
        this.velocityTracker.addMovement(paramMotionEvent);
        if ((this.maybeStartTracking) && (!this.startedTracking) && (((f1 > 0.0F) && (f1 / 3.0F > Math.abs(f2)) && (Math.abs(f1) >= AndroidUtilities.getPixelsInCM(0.2F, true))) || ((f1 < 0.0F) && (Math.abs(f1) >= Math.abs(f2)) && (Math.abs(f1) >= AndroidUtilities.getPixelsInCM(0.4F, true)))))
        {
          prepareForDrawerOpen(paramMotionEvent);
          this.startedTrackingX = (int)paramMotionEvent.getX();
          requestDisallowInterceptTouchEvent(true);
          continue;
        }
        if (!this.startedTracking)
          continue;
        if (!this.beginTrackingSent)
        {
          if (((Activity)getContext()).getCurrentFocus() != null)
            AndroidUtilities.hideKeyboard(((Activity)getContext()).getCurrentFocus());
          this.beginTrackingSent = true;
        }
        moveDrawerByX(f1);
        this.startedTrackingX = (int)paramMotionEvent.getX();
      }
    while ((paramMotionEvent != null) && ((paramMotionEvent == null) || (paramMotionEvent.getPointerId(0) != this.startedTrackingPointerId) || ((paramMotionEvent.getAction() != 3) && (paramMotionEvent.getAction() != 1) && (paramMotionEvent.getAction() != 6))));
    if (this.velocityTracker == null)
      this.velocityTracker = VelocityTracker.obtain();
    this.velocityTracker.computeCurrentVelocity(1000);
    int i;
    if ((this.startedTracking) || ((this.drawerPosition != 0.0F) && (this.drawerPosition != this.drawerLayout.getMeasuredWidth())))
    {
      f1 = this.velocityTracker.getXVelocity();
      f2 = this.velocityTracker.getYVelocity();
      if (((this.drawerPosition >= this.drawerLayout.getMeasuredWidth() / 2.0F) || ((f1 >= 3500.0F) && (Math.abs(f1) >= Math.abs(f2)))) && ((f1 >= 0.0F) || (Math.abs(f1) < 3500.0F)))
        break label681;
      i = 1;
      label617: if (i != 0)
        break label693;
      if ((this.drawerOpened) || (Math.abs(f1) < 3500.0F))
        break label687;
    }
    label681: label687: for (bool1 = true; ; bool1 = false)
    {
      openDrawer(bool1);
      this.startedTracking = false;
      this.maybeStartTracking = false;
      if (this.velocityTracker == null)
        break;
      this.velocityTracker.recycle();
      this.velocityTracker = null;
      break;
      i = 0;
      break label617;
    }
    label693: if ((this.drawerOpened) && (Math.abs(f1) >= 3500.0F));
    for (bool1 = bool3; ; bool1 = false)
    {
      closeDrawer(bool1);
      break;
    }
  }

  public void openDrawer(boolean paramBoolean)
  {
    if (!this.allowOpenDrawer)
      return;
    if ((AndroidUtilities.isTablet()) && (this.parentActionBarLayout != null) && (this.parentActionBarLayout.parentActivity != null))
      AndroidUtilities.hideKeyboard(this.parentActionBarLayout.parentActivity.getCurrentFocus());
    cancelCurrentAnimation();
    AnimatorSet localAnimatorSet = new AnimatorSet();
    localAnimatorSet.playTogether(new Animator[] { ObjectAnimator.ofFloat(this, "drawerPosition", new float[] { this.drawerLayout.getMeasuredWidth() }) });
    localAnimatorSet.setInterpolator(new DecelerateInterpolator());
    if (paramBoolean)
      localAnimatorSet.setDuration(Math.max((int)(200.0F / this.drawerLayout.getMeasuredWidth() * (this.drawerLayout.getMeasuredWidth() - this.drawerPosition)), 50));
    while (true)
    {
      localAnimatorSet.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          DrawerLayoutContainer.this.onDrawerAnimationEnd(true);
        }
      });
      localAnimatorSet.start();
      this.currentAnimation = localAnimatorSet;
      return;
      localAnimatorSet.setDuration(300L);
    }
  }

  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    if ((this.maybeStartTracking) && (!this.startedTracking))
      onTouchEvent(null);
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }

  public void requestLayout()
  {
    if (!this.inLayout)
      super.requestLayout();
  }

  public void setAllowDrawContent(boolean paramBoolean)
  {
    if (this.allowDrawContent != paramBoolean)
    {
      this.allowDrawContent = paramBoolean;
      invalidate();
    }
  }

  public void setAllowOpenDrawer(boolean paramBoolean1, boolean paramBoolean2)
  {
    this.allowOpenDrawer = paramBoolean1;
    if ((!this.allowOpenDrawer) && (this.drawerPosition != 0.0F))
    {
      if (!paramBoolean2)
      {
        setDrawerPosition(0.0F);
        onDrawerAnimationEnd(false);
      }
    }
    else
      return;
    closeDrawer(true);
  }

  public void setDrawerLayout(ViewGroup paramViewGroup)
  {
    this.drawerLayout = paramViewGroup;
    addView(this.drawerLayout);
    if (Build.VERSION.SDK_INT >= 21)
      this.drawerLayout.setFitsSystemWindows(true);
  }

  public void setDrawerPosition(float paramFloat)
  {
    this.drawerPosition = paramFloat;
    if (this.drawerPosition > this.drawerLayout.getMeasuredWidth())
    {
      this.drawerPosition = this.drawerLayout.getMeasuredWidth();
      this.drawerLayout.setTranslationX(this.drawerPosition);
      if (this.drawerPosition <= 0.0F)
        break label109;
    }
    label109: for (int i = 0; ; i = 8)
    {
      if (this.drawerLayout.getVisibility() != i)
        this.drawerLayout.setVisibility(i);
      setScrimOpacity(this.drawerPosition / this.drawerLayout.getMeasuredWidth());
      return;
      if (this.drawerPosition >= 0.0F)
        break;
      this.drawerPosition = 0.0F;
      break;
    }
  }

  public void setParentActionBarLayout(ActionBarLayout paramActionBarLayout)
  {
    this.parentActionBarLayout = paramActionBarLayout;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.ActionBar.DrawerLayoutContainer
 * JD-Core Version:    0.6.0
 */