package org.vidogram.ui.Components;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.CompoundButton;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.ui.ActionBar.Theme;

public class Switch extends CompoundButton
{
  private static final int THUMB_ANIMATION_DURATION = 250;
  private static final int TOUCH_MODE_DOWN = 1;
  private static final int TOUCH_MODE_DRAGGING = 2;
  private static final int TOUCH_MODE_IDLE = 0;
  private boolean attachedToWindow;
  private int mMinFlingVelocity;
  private ObjectAnimator mPositionAnimator;
  private boolean mSplitTrack;
  private int mSwitchBottom;
  private int mSwitchHeight;
  private int mSwitchLeft;
  private int mSwitchMinWidth;
  private int mSwitchPadding;
  private int mSwitchRight;
  private int mSwitchTop;
  private int mSwitchWidth;
  private final Rect mTempRect = new Rect();
  private Drawable mThumbDrawable;
  private int mThumbTextPadding;
  private int mThumbWidth;
  private int mTouchMode;
  private int mTouchSlop;
  private float mTouchX;
  private float mTouchY;
  private Drawable mTrackDrawable;
  private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
  private float thumbPosition;
  private boolean wasLayout;

  public Switch(Context paramContext)
  {
    super(paramContext);
    this.mThumbDrawable = paramContext.getResources().getDrawable(2130838077);
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setCallback(this);
    this.mTrackDrawable = paramContext.getResources().getDrawable(2130838080);
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setCallback(this);
    if (AndroidUtilities.density < 1.0F);
    for (this.mSwitchMinWidth = AndroidUtilities.dp(30.0F); ; this.mSwitchMinWidth = 0)
    {
      this.mSwitchPadding = 0;
      this.mSplitTrack = false;
      paramContext = ViewConfiguration.get(paramContext);
      this.mTouchSlop = paramContext.getScaledTouchSlop();
      this.mMinFlingVelocity = paramContext.getScaledMinimumFlingVelocity();
      refreshDrawableState();
      setChecked(isChecked());
      return;
    }
  }

  private void animateThumbToCheckedState(boolean paramBoolean)
  {
    float f;
    if (paramBoolean)
      f = 1.0F;
    while (true)
    {
      this.mPositionAnimator = ObjectAnimator.ofFloat(this, "thumbPosition", new float[] { f });
      this.mPositionAnimator.setDuration(250L);
      this.mPositionAnimator.start();
      return;
      f = 0.0F;
    }
  }

  private void cancelPositionAnimator()
  {
    if (this.mPositionAnimator != null)
      this.mPositionAnimator.cancel();
  }

  private void cancelSuperTouch(MotionEvent paramMotionEvent)
  {
    paramMotionEvent = MotionEvent.obtain(paramMotionEvent);
    paramMotionEvent.setAction(3);
    super.onTouchEvent(paramMotionEvent);
    paramMotionEvent.recycle();
  }

  public static float constrain(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    if (paramFloat1 < paramFloat2)
      return paramFloat2;
    if (paramFloat1 > paramFloat3)
      return paramFloat3;
    return paramFloat1;
  }

  private boolean getTargetCheckedState()
  {
    return this.thumbPosition > 0.5F;
  }

  private int getThumbOffset()
  {
    float f;
    if (LocaleController.isRTL)
      f = 1.0F - this.thumbPosition;
    while (true)
    {
      return (int)(f * getThumbScrollRange() + 0.5F);
      f = this.thumbPosition;
    }
  }

  private int getThumbScrollRange()
  {
    if (this.mTrackDrawable != null)
    {
      Rect localRect = this.mTempRect;
      this.mTrackDrawable.getPadding(localRect);
      if (this.mThumbDrawable != null);
      for (Insets localInsets = Insets.NONE; ; localInsets = Insets.NONE)
        return this.mSwitchWidth - this.mThumbWidth - localRect.left - localRect.right - localInsets.left - localInsets.right;
    }
    return 0;
  }

  private boolean hitThumb(float paramFloat1, float paramFloat2)
  {
    int k = getThumbOffset();
    this.mThumbDrawable.getPadding(this.mTempRect);
    int i = this.mSwitchTop;
    int j = this.mTouchSlop;
    k = k + this.mSwitchLeft - this.mTouchSlop;
    int m = this.mThumbWidth;
    int n = this.mTempRect.left;
    int i1 = this.mTempRect.right;
    int i2 = this.mTouchSlop;
    int i3 = this.mSwitchBottom;
    int i4 = this.mTouchSlop;
    return (paramFloat1 > k) && (paramFloat1 < m + k + n + i1 + i2) && (paramFloat2 > i - j) && (paramFloat2 < i3 + i4);
  }

  private void setThumbPosition(float paramFloat)
  {
    this.thumbPosition = paramFloat;
    invalidate();
  }

  private void stopDrag(MotionEvent paramMotionEvent)
  {
    boolean bool = true;
    this.mTouchMode = 0;
    int i;
    float f;
    if ((paramMotionEvent.getAction() == 1) && (isEnabled()))
    {
      i = 1;
      if (i == 0)
        break label116;
      this.mVelocityTracker.computeCurrentVelocity(1000);
      f = this.mVelocityTracker.getXVelocity();
      if (Math.abs(f) <= this.mMinFlingVelocity)
        break label107;
      if (!LocaleController.isRTL)
        break label95;
      if (f >= 0.0F)
        break label89;
    }
    while (true)
    {
      setChecked(bool);
      cancelSuperTouch(paramMotionEvent);
      return;
      i = 0;
      break;
      label89: bool = false;
      continue;
      label95: if (f > 0.0F)
        continue;
      bool = false;
      continue;
      label107: bool = getTargetCheckedState();
      continue;
      label116: bool = isChecked();
    }
  }

  public void checkColorFilters()
  {
    Drawable localDrawable;
    if (this.mTrackDrawable != null)
    {
      localDrawable = this.mTrackDrawable;
      if (isChecked())
      {
        i = Theme.getColor("switchTrackChecked");
        localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
    }
    else if (this.mThumbDrawable != null)
    {
      localDrawable = this.mThumbDrawable;
      if (!isChecked())
        break label92;
    }
    label92: for (int i = Theme.getColor("switchThumbChecked"); ; i = Theme.getColor("switchThumb"))
    {
      localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      return;
      i = Theme.getColor("switchTrack");
      break;
    }
  }

  public void draw(Canvas paramCanvas)
  {
    Rect localRect = this.mTempRect;
    int j = this.mSwitchLeft;
    int n = this.mSwitchTop;
    int i2 = this.mSwitchRight;
    int i1 = this.mSwitchBottom;
    int i3 = j + getThumbOffset();
    Object localObject;
    int i4;
    label138: int m;
    int k;
    if (this.mThumbDrawable != null)
    {
      localObject = Insets.NONE;
      if (this.mTrackDrawable == null)
        break label378;
      this.mTrackDrawable.getPadding(localRect);
      i4 = localRect.left;
      if (localObject == Insets.NONE)
        break label358;
      i = j;
      if (((Insets)localObject).left > localRect.left)
        i = j + (((Insets)localObject).left - localRect.left);
      if (((Insets)localObject).top <= localRect.top)
        break label352;
      j = ((Insets)localObject).top - localRect.top + n;
      m = i2;
      if (((Insets)localObject).right > localRect.right)
        m = i2 - (((Insets)localObject).right - localRect.right);
      if (((Insets)localObject).bottom <= localRect.bottom)
        break label345;
      k = i1 - (((Insets)localObject).bottom - localRect.bottom);
      label200: this.mTrackDrawable.setBounds(i, j, m, k);
    }
    label340: label345: label352: label358: label378: for (int i = i4 + i3; ; i = i3)
    {
      if (this.mThumbDrawable != null)
      {
        this.mThumbDrawable.getPadding(localRect);
        j = i - localRect.left;
        k = this.mThumbWidth;
        k = localRect.right + (i + k);
        if (AndroidUtilities.density != 1.5F)
          break label340;
      }
      for (i = AndroidUtilities.dp(1.0F); ; i = 0)
      {
        this.mThumbDrawable.setBounds(j, n + i, k, i + i1);
        localObject = getBackground();
        if ((localObject != null) && (Build.VERSION.SDK_INT >= 21))
          ((Drawable)localObject).setHotspotBounds(j, n, k, i1);
        super.draw(paramCanvas);
        return;
        localObject = Insets.NONE;
        break;
      }
      k = i1;
      break label200;
      j = n;
      break label138;
      k = i1;
      m = n;
      i = j;
      j = m;
      m = i2;
      break label200;
    }
  }

  @SuppressLint({"NewApi"})
  public void drawableHotspotChanged(float paramFloat1, float paramFloat2)
  {
    super.drawableHotspotChanged(paramFloat1, paramFloat2);
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setHotspot(paramFloat1, paramFloat2);
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setHotspot(paramFloat1, paramFloat2);
  }

  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setState(arrayOfInt);
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setState(arrayOfInt);
    invalidate();
  }

  public int getCompoundPaddingLeft()
  {
    if (!LocaleController.isRTL)
      return super.getCompoundPaddingLeft();
    return super.getCompoundPaddingLeft() + this.mSwitchWidth;
  }

  public int getCompoundPaddingRight()
  {
    if (LocaleController.isRTL)
      return super.getCompoundPaddingRight();
    return super.getCompoundPaddingRight() + this.mSwitchWidth;
  }

  public boolean getSplitTrack()
  {
    return this.mSplitTrack;
  }

  public int getSwitchMinWidth()
  {
    return this.mSwitchMinWidth;
  }

  public int getSwitchPadding()
  {
    return this.mSwitchPadding;
  }

  public Drawable getThumbDrawable()
  {
    return this.mThumbDrawable;
  }

  public float getThumbPosition()
  {
    return this.thumbPosition;
  }

  public int getThumbTextPadding()
  {
    return this.mThumbTextPadding;
  }

  public Drawable getTrackDrawable()
  {
    return this.mTrackDrawable;
  }

  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.jumpToCurrentState();
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.jumpToCurrentState();
    if ((this.mPositionAnimator != null) && (this.mPositionAnimator.isRunning()))
    {
      this.mPositionAnimator.end();
      this.mPositionAnimator = null;
    }
  }

  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.attachedToWindow = true;
    requestLayout();
  }

  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    this.attachedToWindow = false;
    this.wasLayout = false;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Rect localRect = this.mTempRect;
    Drawable localDrawable1 = this.mTrackDrawable;
    int i;
    Drawable localDrawable2;
    if (localDrawable1 != null)
    {
      localDrawable1.getPadding(localRect);
      i = this.mSwitchTop;
      i = this.mSwitchBottom;
      localDrawable2 = this.mThumbDrawable;
      if (localDrawable1 != null)
      {
        if ((!this.mSplitTrack) || (localDrawable2 == null))
          break label154;
        Insets localInsets = Insets.NONE;
        localDrawable2.copyBounds(localRect);
        localRect.left += localInsets.left;
        localRect.right -= localInsets.right;
        i = paramCanvas.save();
        paramCanvas.clipRect(localRect, Region.Op.DIFFERENCE);
        localDrawable1.draw(paramCanvas);
        paramCanvas.restoreToCount(i);
      }
    }
    while (true)
    {
      i = paramCanvas.save();
      if (localDrawable2 != null)
        localDrawable2.draw(paramCanvas);
      paramCanvas.restoreToCount(i);
      return;
      localRect.setEmpty();
      break;
      label154: localDrawable1.draw(paramCanvas);
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = 0;
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    this.wasLayout = true;
    Rect localRect;
    Insets localInsets;
    if (this.mThumbDrawable != null)
    {
      localRect = this.mTempRect;
      if (this.mTrackDrawable != null)
      {
        this.mTrackDrawable.getPadding(localRect);
        localInsets = Insets.NONE;
        paramInt2 = Math.max(0, localInsets.left - localRect.left);
      }
    }
    for (paramInt1 = Math.max(0, localInsets.right - localRect.right); ; paramInt1 = i)
    {
      if (LocaleController.isRTL)
      {
        paramInt3 = getPaddingLeft() + paramInt2;
        paramInt4 = this.mSwitchWidth + paramInt3 - paramInt2 - paramInt1;
        label113: switch (getGravity() & 0x70)
        {
        default:
          paramInt2 = getPaddingTop();
          paramInt1 = this.mSwitchHeight + paramInt2;
        case 16:
        case 80:
        }
      }
      while (true)
      {
        this.mSwitchLeft = paramInt3;
        this.mSwitchTop = paramInt2;
        this.mSwitchBottom = paramInt1;
        this.mSwitchRight = paramInt4;
        return;
        localRect.setEmpty();
        break;
        paramInt4 = getWidth() - getPaddingRight() - paramInt1;
        paramInt3 = paramInt1 + (paramInt2 + (paramInt4 - this.mSwitchWidth));
        break label113;
        paramInt2 = (getPaddingTop() + getHeight() - getPaddingBottom()) / 2 - this.mSwitchHeight / 2;
        paramInt1 = this.mSwitchHeight + paramInt2;
        continue;
        paramInt1 = getHeight() - getPaddingBottom();
        paramInt2 = paramInt1 - this.mSwitchHeight;
      }
      paramInt2 = 0;
    }
  }

  public void onMeasure(int paramInt1, int paramInt2)
  {
    int k = 0;
    Object localObject = this.mTempRect;
    int i;
    if (this.mThumbDrawable != null)
    {
      this.mThumbDrawable.getPadding((Rect)localObject);
      j = this.mThumbDrawable.getIntrinsicWidth() - ((Rect)localObject).left - ((Rect)localObject).right;
      i = this.mThumbDrawable.getIntrinsicHeight();
      this.mThumbWidth = j;
      if (this.mTrackDrawable == null)
        break label214;
      this.mTrackDrawable.getPadding((Rect)localObject);
    }
    for (int j = this.mTrackDrawable.getIntrinsicHeight(); ; j = k)
    {
      int i1 = ((Rect)localObject).left;
      int n = ((Rect)localObject).right;
      int m = n;
      k = i1;
      if (this.mThumbDrawable != null)
      {
        localObject = Insets.NONE;
        k = Math.max(i1, ((Insets)localObject).left);
        m = Math.max(n, ((Insets)localObject).right);
      }
      k = Math.max(this.mSwitchMinWidth, m + (k + this.mThumbWidth * 2));
      i = Math.max(j, i);
      this.mSwitchWidth = k;
      this.mSwitchHeight = i;
      super.onMeasure(paramInt1, paramInt2);
      if (getMeasuredHeight() < i)
        setMeasuredDimension(k, i);
      return;
      i = 0;
      j = 0;
      break;
      label214: ((Rect)localObject).setEmpty();
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (paramMotionEvent.getActionMasked())
    {
    default:
    case 0:
    case 2:
    case 1:
    case 3:
    }
    while (true)
    {
      return super.onTouchEvent(paramMotionEvent);
      float f1 = paramMotionEvent.getX();
      float f2 = paramMotionEvent.getY();
      if ((!isEnabled()) || (!hitThumb(f1, f2)))
        continue;
      this.mTouchMode = 1;
      this.mTouchX = f1;
      this.mTouchY = f2;
      continue;
      switch (this.mTouchMode)
      {
      case 0:
      default:
        break;
      case 1:
        f1 = paramMotionEvent.getX();
        f2 = paramMotionEvent.getY();
        if ((Math.abs(f1 - this.mTouchX) <= this.mTouchSlop) && (Math.abs(f2 - this.mTouchY) <= this.mTouchSlop))
          continue;
        this.mTouchMode = 2;
        getParent().requestDisallowInterceptTouchEvent(true);
        this.mTouchX = f1;
        this.mTouchY = f2;
        return true;
      case 2:
        float f3 = paramMotionEvent.getX();
        int i = getThumbScrollRange();
        f1 = f3 - this.mTouchX;
        if (i != 0)
          f1 /= i;
        while (true)
        {
          f2 = f1;
          if (LocaleController.isRTL)
            f2 = -f1;
          f1 = constrain(f2 + this.thumbPosition, 0.0F, 1.0F);
          if (f1 != this.thumbPosition)
          {
            this.mTouchX = f3;
            setThumbPosition(f1);
          }
          return true;
          if (f1 > 0.0F)
          {
            f1 = 1.0F;
            continue;
          }
          f1 = -1.0F;
        }
        if (this.mTouchMode == 2)
        {
          stopDrag(paramMotionEvent);
          super.onTouchEvent(paramMotionEvent);
          return true;
        }
        this.mTouchMode = 0;
        this.mVelocityTracker.clear();
      }
    }
  }

  public void resetLayout()
  {
    this.wasLayout = false;
  }

  public void setChecked(boolean paramBoolean)
  {
    super.setChecked(paramBoolean);
    paramBoolean = isChecked();
    Drawable localDrawable;
    if ((this.attachedToWindow) && (this.wasLayout))
    {
      animateThumbToCheckedState(paramBoolean);
      if (this.mTrackDrawable != null)
      {
        localDrawable = this.mTrackDrawable;
        if (!paramBoolean)
          break label132;
        i = Theme.getColor("switchTrackChecked");
        label52: localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      }
      if (this.mThumbDrawable != null)
      {
        localDrawable = this.mThumbDrawable;
        if (!paramBoolean)
          break label142;
      }
    }
    label132: label142: for (int i = Theme.getColor("switchThumbChecked"); ; i = Theme.getColor("switchThumb"))
    {
      localDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
      return;
      cancelPositionAnimator();
      float f;
      if (paramBoolean)
        f = 1.0F;
      while (true)
      {
        setThumbPosition(f);
        break;
        f = 0.0F;
      }
      i = Theme.getColor("switchTrack");
      break label52;
    }
  }

  public void setSplitTrack(boolean paramBoolean)
  {
    this.mSplitTrack = paramBoolean;
    invalidate();
  }

  public void setSwitchMinWidth(int paramInt)
  {
    this.mSwitchMinWidth = paramInt;
    requestLayout();
  }

  public void setSwitchPadding(int paramInt)
  {
    this.mSwitchPadding = paramInt;
    requestLayout();
  }

  public void setThumbDrawable(Drawable paramDrawable)
  {
    if (this.mThumbDrawable != null)
      this.mThumbDrawable.setCallback(null);
    this.mThumbDrawable = paramDrawable;
    if (paramDrawable != null)
      paramDrawable.setCallback(this);
    requestLayout();
  }

  public void setThumbTextPadding(int paramInt)
  {
    this.mThumbTextPadding = paramInt;
    requestLayout();
  }

  public void setTrackDrawable(Drawable paramDrawable)
  {
    if (this.mTrackDrawable != null)
      this.mTrackDrawable.setCallback(null);
    this.mTrackDrawable = paramDrawable;
    if (paramDrawable != null)
      paramDrawable.setCallback(this);
    requestLayout();
  }

  public void toggle()
  {
    if (!isChecked());
    for (boolean bool = true; ; bool = false)
    {
      setChecked(bool);
      return;
    }
  }

  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mThumbDrawable) || (paramDrawable == this.mTrackDrawable);
  }

  public static class Insets
  {
    public static final Insets NONE = new Insets(AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(4.0F), 0);
    public final int bottom;
    public final int left;
    public final int right;
    public final int top;

    private Insets(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.left = paramInt1;
      this.top = paramInt2;
      this.right = paramInt3;
      this.bottom = paramInt4;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Switch
 * JD-Core Version:    0.6.0
 */