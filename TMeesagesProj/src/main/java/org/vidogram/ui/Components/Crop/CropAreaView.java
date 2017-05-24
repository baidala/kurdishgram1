package org.vidogram.ui.Components.Crop;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import org.vidogram.messenger.AndroidUtilities;

public class CropAreaView extends View
{
  private Control activeControl;
  private RectF actualRect = new RectF();
  private Animator animator;
  private RectF bottomEdge = new RectF();
  private RectF bottomLeftCorner = new RectF();
  private float bottomPadding;
  private RectF bottomRightCorner = new RectF();
  Paint dimPaint = new Paint();
  private boolean dimVisibile = true;
  Paint framePaint;
  private boolean frameVisible = true;
  private Animator gridAnimator;
  private float gridProgress;
  private GridType gridType = GridType.NONE;
  Paint handlePaint;
  AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
  private boolean isDragging;
  private RectF leftEdge = new RectF();
  Paint linePaint;
  private AreaViewListener listener;
  private float lockAspectRatio;
  private float minWidth = AndroidUtilities.dp(32.0F);
  private GridType previousGridType;
  private int previousX;
  private int previousY;
  private RectF rightEdge = new RectF();
  Paint shadowPaint;
  private float sidePadding = AndroidUtilities.dp(16.0F);
  private RectF tempRect = new RectF();
  private RectF topEdge = new RectF();
  private RectF topLeftCorner = new RectF();
  private RectF topRightCorner = new RectF();

  public CropAreaView(Context paramContext)
  {
    super(paramContext);
    this.dimPaint.setColor(-872415232);
    this.shadowPaint = new Paint();
    this.shadowPaint.setStyle(Paint.Style.FILL);
    this.shadowPaint.setColor(436207616);
    this.shadowPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.linePaint = new Paint();
    this.linePaint.setStyle(Paint.Style.FILL);
    this.linePaint.setColor(-1);
    this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
    this.handlePaint = new Paint();
    this.handlePaint.setStyle(Paint.Style.FILL);
    this.handlePaint.setColor(-1);
    this.framePaint = new Paint();
    this.framePaint.setStyle(Paint.Style.FILL);
    this.framePaint.setColor(-1291845633);
  }

  private void constrainRectByHeight(RectF paramRectF, float paramFloat)
  {
    float f = paramRectF.height();
    paramRectF.right = (f * paramFloat + paramRectF.left);
    paramRectF.bottom = (f + paramRectF.top);
  }

  private void constrainRectByWidth(RectF paramRectF, float paramFloat)
  {
    float f = paramRectF.width();
    paramFloat = f / paramFloat;
    paramRectF.right = (f + paramRectF.left);
    paramRectF.bottom = (paramRectF.top + paramFloat);
  }

  private float getGridProgress()
  {
    return this.gridProgress;
  }

  private void setCropBottom(float paramFloat)
  {
    this.actualRect.bottom = paramFloat;
    invalidate();
  }

  private void setCropLeft(float paramFloat)
  {
    this.actualRect.left = paramFloat;
    invalidate();
  }

  private void setCropRight(float paramFloat)
  {
    this.actualRect.right = paramFloat;
    invalidate();
  }

  private void setCropTop(float paramFloat)
  {
    this.actualRect.top = paramFloat;
    invalidate();
  }

  private void setGridProgress(float paramFloat)
  {
    this.gridProgress = paramFloat;
    invalidate();
  }

  private void updateTouchAreas()
  {
    int i = AndroidUtilities.dp(16.0F);
    this.topLeftCorner.set(this.actualRect.left - i, this.actualRect.top - i, this.actualRect.left + i, this.actualRect.top + i);
    this.topRightCorner.set(this.actualRect.right - i, this.actualRect.top - i, this.actualRect.right + i, this.actualRect.top + i);
    this.bottomLeftCorner.set(this.actualRect.left - i, this.actualRect.bottom - i, this.actualRect.left + i, this.actualRect.bottom + i);
    this.bottomRightCorner.set(this.actualRect.right - i, this.actualRect.bottom - i, this.actualRect.right + i, this.actualRect.bottom + i);
    this.topEdge.set(this.actualRect.left + i, this.actualRect.top - i, this.actualRect.right - i, this.actualRect.top + i);
    this.leftEdge.set(this.actualRect.left - i, this.actualRect.top + i, this.actualRect.left + i, this.actualRect.bottom - i);
    this.rightEdge.set(this.actualRect.right - i, this.actualRect.top + i, this.actualRect.right + i, this.actualRect.bottom - i);
    RectF localRectF = this.bottomEdge;
    float f1 = this.actualRect.left;
    float f2 = i;
    float f3 = this.actualRect.bottom;
    float f4 = i;
    float f5 = this.actualRect.right;
    float f6 = i;
    float f7 = this.actualRect.bottom;
    localRectF.set(f1 + f2, f3 - f4, f5 - f6, i + f7);
  }

  public void calculateRect(RectF paramRectF, float paramFloat)
  {
    int i;
    float f3;
    float f4;
    float f2;
    float f6;
    float f7;
    float f1;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = AndroidUtilities.statusBarHeight;
      f3 = i;
      f4 = getMeasuredHeight() - this.bottomPadding - f3;
      f2 = getMeasuredWidth() / f4;
      float f5 = Math.min(getMeasuredWidth(), f4) - this.sidePadding * 2.0F;
      f6 = getMeasuredWidth() - this.sidePadding * 2.0F;
      f7 = f4 - this.sidePadding * 2.0F;
      f1 = getMeasuredWidth() / 2.0F;
      f4 = f3 + f4 / 2.0F;
      if (Math.abs(1.0F - paramFloat) >= 0.0001D)
        break label167;
      f3 = f1 - f5 / 2.0F;
      f2 = f4 - f5 / 2.0F;
      f1 = f5 / 2.0F + f1;
      paramFloat = f4 + f5 / 2.0F;
    }
    while (true)
    {
      paramRectF.set(f3, f2, f1, paramFloat);
      return;
      i = 0;
      break;
      label167: if (paramFloat > f2)
      {
        f3 = f1 - f6 / 2.0F;
        f2 = f4 - f6 / paramFloat / 2.0F;
        f1 = f6 / 2.0F + f1;
        paramFloat = f4 + f6 / paramFloat / 2.0F;
        continue;
      }
      f3 = f1 - f7 * paramFloat / 2.0F;
      f2 = f4 - f7 / 2.0F;
      f1 = f7 * paramFloat / 2.0F + f1;
      paramFloat = f4 + f7 / 2.0F;
    }
  }

  public void fill(RectF paramRectF, Animator paramAnimator, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      if (this.animator != null)
      {
        this.animator.cancel();
        this.animator = null;
      }
      AnimatorSet localAnimatorSet = new AnimatorSet();
      this.animator = localAnimatorSet;
      localAnimatorSet.setDuration(300L);
      Animator[] arrayOfAnimator = new Animator[5];
      arrayOfAnimator[0] = ObjectAnimator.ofFloat(this, "cropLeft", new float[] { paramRectF.left });
      arrayOfAnimator[0].setInterpolator(this.interpolator);
      arrayOfAnimator[1] = ObjectAnimator.ofFloat(this, "cropTop", new float[] { paramRectF.top });
      arrayOfAnimator[1].setInterpolator(this.interpolator);
      arrayOfAnimator[2] = ObjectAnimator.ofFloat(this, "cropRight", new float[] { paramRectF.right });
      arrayOfAnimator[2].setInterpolator(this.interpolator);
      arrayOfAnimator[3] = ObjectAnimator.ofFloat(this, "cropBottom", new float[] { paramRectF.bottom });
      arrayOfAnimator[3].setInterpolator(this.interpolator);
      arrayOfAnimator[4] = paramAnimator;
      arrayOfAnimator[4].setInterpolator(this.interpolator);
      localAnimatorSet.playTogether(arrayOfAnimator);
      localAnimatorSet.addListener(new AnimatorListenerAdapter(paramRectF)
      {
        public void onAnimationEnd(Animator paramAnimator)
        {
          CropAreaView.this.setActualRect(this.val$targetRect);
          CropAreaView.access$102(CropAreaView.this, null);
        }
      });
      localAnimatorSet.start();
      return;
    }
    setActualRect(paramRectF);
  }

  public float getAspectRatio()
  {
    return (this.actualRect.right - this.actualRect.left) / (this.actualRect.bottom - this.actualRect.top);
  }

  public float getCropBottom()
  {
    return this.actualRect.bottom;
  }

  public float getCropCenterX()
  {
    return this.actualRect.left + (this.actualRect.right - this.actualRect.left) / 2.0F;
  }

  public float getCropCenterY()
  {
    return this.actualRect.top + (this.actualRect.bottom - this.actualRect.top) / 2.0F;
  }

  public float getCropHeight()
  {
    return this.actualRect.bottom - this.actualRect.top;
  }

  public float getCropLeft()
  {
    return this.actualRect.left;
  }

  public void getCropRect(RectF paramRectF)
  {
    paramRectF.set(this.actualRect);
  }

  public float getCropRight()
  {
    return this.actualRect.right;
  }

  public float getCropTop()
  {
    return this.actualRect.top;
  }

  public float getCropWidth()
  {
    return this.actualRect.right - this.actualRect.left;
  }

  public Interpolator getInterpolator()
  {
    return this.interpolator;
  }

  public float getLockAspectRatio()
  {
    return this.lockAspectRatio;
  }

  public RectF getTargetRectToFill()
  {
    RectF localRectF = new RectF();
    calculateRect(localRectF, getAspectRatio());
    return localRectF;
  }

  public boolean isDragging()
  {
    return this.isDragging;
  }

  protected void onDraw(Canvas paramCanvas)
  {
    int k = AndroidUtilities.dp(2.0F);
    int m = AndroidUtilities.dp(16.0F);
    int n = AndroidUtilities.dp(3.0F);
    int i1 = (int)this.actualRect.left - k;
    int i2 = (int)this.actualRect.top - k;
    int i3 = (int)(this.actualRect.right - this.actualRect.left) + k * 2;
    int i4 = (int)(this.actualRect.bottom - this.actualRect.top) + k * 2;
    if (this.dimVisibile)
    {
      paramCanvas.drawRect(0.0F, 0.0F, getWidth(), i2 + k, this.dimPaint);
      paramCanvas.drawRect(0.0F, i2 + k, i1 + k, i2 + i4 - k, this.dimPaint);
      paramCanvas.drawRect(i1 + i3 - k, i2 + k, getWidth(), i2 + i4 - k, this.dimPaint);
      paramCanvas.drawRect(0.0F, i2 + i4 - k, getWidth(), getHeight(), this.dimPaint);
    }
    if (!this.frameVisible)
      return;
    int i5 = n - k;
    int i6 = i3 - n * 2;
    int i7 = i4 - n * 2;
    GridType localGridType = this.gridType;
    if ((localGridType == GridType.NONE) && (this.gridProgress > 0.0F))
      localGridType = this.previousGridType;
    while (true)
    {
      this.shadowPaint.setAlpha((int)(this.gridProgress * 26.0F));
      this.linePaint.setAlpha((int)(this.gridProgress * 178.0F));
      int i = 0;
      while (i < 3)
      {
        if (localGridType == GridType.MINOR)
        {
          int j = 1;
          if (j < 4)
          {
            if ((i == 2) && (j == 3));
            while (true)
            {
              j += 1;
              break;
              paramCanvas.drawLine(i1 + n + i6 / 3 / 3 * j + i6 / 3 * i, i2 + n, i1 + n + i6 / 3 / 3 * j + i6 / 3 * i, i2 + n + i7, this.shadowPaint);
              paramCanvas.drawLine(i1 + n + i6 / 3 / 3 * j + i6 / 3 * i, i2 + n, i1 + n + i6 / 3 / 3 * j + i6 / 3 * i, i2 + n + i7, this.linePaint);
              paramCanvas.drawLine(i1 + n, i2 + n + i7 / 3 / 3 * j + i7 / 3 * i, i1 + n + i6, i2 + n + i7 / 3 / 3 * j + i7 / 3 * i, this.shadowPaint);
              paramCanvas.drawLine(i1 + n, i2 + n + i7 / 3 / 3 * j + i7 / 3 * i, i1 + n + i6, i2 + n + i7 / 3 / 3 * j + i7 / 3 * i, this.linePaint);
            }
          }
        }
        else if ((localGridType == GridType.MAJOR) && (i > 0))
        {
          paramCanvas.drawLine(i1 + n + i6 / 3 * i, i2 + n, i1 + n + i6 / 3 * i, i2 + n + i7, this.shadowPaint);
          paramCanvas.drawLine(i1 + n + i6 / 3 * i, i2 + n, i1 + n + i6 / 3 * i, i2 + n + i7, this.linePaint);
          paramCanvas.drawLine(i1 + n, i2 + n + i7 / 3 * i, i1 + n + i6, i2 + n + i7 / 3 * i, this.shadowPaint);
          paramCanvas.drawLine(i1 + n, i2 + n + i7 / 3 * i, i1 + n + i6, i2 + n + i7 / 3 * i, this.linePaint);
        }
        i += 1;
      }
      paramCanvas.drawRect(i1 + i5, i2 + i5, i1 + i3 - i5, i2 + i5 + k, this.framePaint);
      paramCanvas.drawRect(i1 + i5, i2 + i5, i1 + i5 + k, i2 + i4 - i5, this.framePaint);
      paramCanvas.drawRect(i1 + i5, i2 + i4 - i5 - k, i1 + i3 - i5, i2 + i4 - i5, this.framePaint);
      paramCanvas.drawRect(i1 + i3 - i5 - k, i2 + i5, i1 + i3 - i5, i2 + i4 - i5, this.framePaint);
      paramCanvas.drawRect(i1, i2, i1 + m, i2 + n, this.handlePaint);
      paramCanvas.drawRect(i1, i2, i1 + n, i2 + m, this.handlePaint);
      paramCanvas.drawRect(i1 + i3 - m, i2, i1 + i3, i2 + n, this.handlePaint);
      paramCanvas.drawRect(i1 + i3 - n, i2, i1 + i3, i2 + m, this.handlePaint);
      paramCanvas.drawRect(i1, i2 + i4 - n, i1 + m, i2 + i4, this.handlePaint);
      paramCanvas.drawRect(i1, i2 + i4 - m, i1 + n, i2 + i4, this.handlePaint);
      paramCanvas.drawRect(i1 + i3 - m, i2 + i4 - n, i1 + i3, i2 + i4, this.handlePaint);
      paramCanvas.drawRect(i1 + i3 - n, i2 + i4 - m, i1 + i3, i2 + i4, this.handlePaint);
      return;
    }
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    int n = 0;
    int j = (int)(paramMotionEvent.getX() - ((ViewGroup)getParent()).getX());
    int k = (int)(paramMotionEvent.getY() - ((ViewGroup)getParent()).getY());
    int i;
    float f1;
    label90: int m;
    if (Build.VERSION.SDK_INT >= 21)
    {
      i = AndroidUtilities.statusBarHeight;
      f1 = i;
      i = paramMotionEvent.getActionMasked();
      if (i != 0)
        break label334;
      if (!this.topLeftCorner.contains(j, k))
        break label143;
      this.activeControl = Control.TOP_LEFT;
      this.previousX = j;
      this.previousY = k;
      setGridType(GridType.MAJOR, false);
      this.isDragging = true;
      if (this.listener != null)
        this.listener.onAreaChangeBegan();
      m = 1;
    }
    label143: label334: 
    do
    {
      do
      {
        do
        {
          return m;
          i = 0;
          break;
          if (this.topRightCorner.contains(j, k))
          {
            this.activeControl = Control.TOP_RIGHT;
            break label90;
          }
          if (this.bottomLeftCorner.contains(j, k))
          {
            this.activeControl = Control.BOTTOM_LEFT;
            break label90;
          }
          if (this.bottomRightCorner.contains(j, k))
          {
            this.activeControl = Control.BOTTOM_RIGHT;
            break label90;
          }
          if (this.leftEdge.contains(j, k))
          {
            this.activeControl = Control.LEFT;
            break label90;
          }
          if (this.topEdge.contains(j, k))
          {
            this.activeControl = Control.TOP;
            break label90;
          }
          if (this.rightEdge.contains(j, k))
          {
            this.activeControl = Control.RIGHT;
            break label90;
          }
          if (this.bottomEdge.contains(j, k))
          {
            this.activeControl = Control.BOTTOM;
            break label90;
          }
          this.activeControl = Control.NONE;
          return false;
          if ((i != 1) && (i != 3))
            break label390;
          this.isDragging = false;
          m = n;
        }
        while (this.activeControl == Control.NONE);
        this.activeControl = Control.NONE;
        if (this.listener != null)
          this.listener.onAreaChangeEnded();
        return true;
        m = n;
      }
      while (i != 2);
      m = n;
    }
    while (this.activeControl == Control.NONE);
    label390: this.tempRect.set(this.actualRect);
    float f2 = j - this.previousX;
    float f3 = k - this.previousY;
    this.previousX = j;
    this.previousY = k;
    switch (3.$SwitchMap$org$vidogram$ui$Components$Crop$CropAreaView$Control[this.activeControl.ordinal()])
    {
    default:
      if (this.tempRect.left < this.sidePadding)
      {
        if (this.lockAspectRatio > 0.0F)
          this.tempRect.bottom = (this.tempRect.top + (this.tempRect.right - this.sidePadding) / this.lockAspectRatio);
        this.tempRect.left = this.sidePadding;
        label579: f1 += this.sidePadding;
        f2 = this.bottomPadding + this.sidePadding;
        if (this.tempRect.top >= f1)
          break label1526;
        if (this.lockAspectRatio > 0.0F)
          this.tempRect.right = (this.tempRect.left + (this.tempRect.bottom - f1) * this.lockAspectRatio);
        this.tempRect.top = f1;
        label654: if (this.tempRect.width() < this.minWidth)
          this.tempRect.right = (this.tempRect.left + this.minWidth);
        if (this.tempRect.height() < this.minWidth)
          this.tempRect.bottom = (this.tempRect.top + this.minWidth);
        if (this.lockAspectRatio <= 0.0F)
          break;
        if (this.lockAspectRatio >= 1.0F)
          break label1597;
        if (this.tempRect.width() > this.minWidth)
          break;
        this.tempRect.right = (this.tempRect.left + this.minWidth);
        this.tempRect.bottom = (this.tempRect.top + this.tempRect.width() / this.lockAspectRatio);
      }
    case 1:
    case 2:
    case 3:
    case 4:
    case 5:
    case 6:
    case 7:
    case 8:
    }
    while (true)
    {
      setActualRect(this.tempRect);
      if (this.listener != null)
        this.listener.onAreaChange();
      return true;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.left += f2;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.top += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      float f4 = this.tempRect.width();
      float f5 = this.tempRect.height();
      if (Math.abs(f2) > Math.abs(f3))
        constrainRectByWidth(this.tempRect, this.lockAspectRatio);
      while (true)
      {
        paramMotionEvent = this.tempRect;
        paramMotionEvent.left -= this.tempRect.width() - f4;
        paramMotionEvent = this.tempRect;
        paramMotionEvent.top -= this.tempRect.width() - f5;
        break;
        constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      }
      paramMotionEvent = this.tempRect;
      paramMotionEvent.right += f2;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.top += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      f4 = this.tempRect.height();
      if (Math.abs(f2) > Math.abs(f3))
        constrainRectByWidth(this.tempRect, this.lockAspectRatio);
      while (true)
      {
        paramMotionEvent = this.tempRect;
        paramMotionEvent.top -= this.tempRect.width() - f4;
        break;
        constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      }
      paramMotionEvent = this.tempRect;
      paramMotionEvent.left += f2;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.bottom += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      f4 = this.tempRect.width();
      if (Math.abs(f2) > Math.abs(f3))
        constrainRectByWidth(this.tempRect, this.lockAspectRatio);
      while (true)
      {
        paramMotionEvent = this.tempRect;
        paramMotionEvent.left -= this.tempRect.width() - f4;
        break;
        constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      }
      paramMotionEvent = this.tempRect;
      paramMotionEvent.right += f2;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.bottom += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      if (Math.abs(f2) > Math.abs(f3))
      {
        constrainRectByWidth(this.tempRect, this.lockAspectRatio);
        break;
      }
      constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      break;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.top += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      break;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.left = (f2 + paramMotionEvent.left);
      if (this.lockAspectRatio <= 0.0F)
        break;
      constrainRectByWidth(this.tempRect, this.lockAspectRatio);
      break;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.right = (f2 + paramMotionEvent.right);
      if (this.lockAspectRatio <= 0.0F)
        break;
      constrainRectByWidth(this.tempRect, this.lockAspectRatio);
      break;
      paramMotionEvent = this.tempRect;
      paramMotionEvent.bottom += f3;
      if (this.lockAspectRatio <= 0.0F)
        break;
      constrainRectByHeight(this.tempRect, this.lockAspectRatio);
      break;
      if (this.tempRect.right <= getWidth() - this.sidePadding)
        break label579;
      this.tempRect.right = (getWidth() - this.sidePadding);
      if (this.lockAspectRatio <= 0.0F)
        break label579;
      this.tempRect.bottom = (this.tempRect.top + this.tempRect.width() / this.lockAspectRatio);
      break label579;
      label1526: if (this.tempRect.bottom <= getHeight() - f2)
        break label654;
      this.tempRect.bottom = (getHeight() - f2);
      if (this.lockAspectRatio <= 0.0F)
        break label654;
      this.tempRect.right = (this.tempRect.left + this.tempRect.height() * this.lockAspectRatio);
      break label654;
      label1597: if (this.tempRect.height() > this.minWidth)
        continue;
      this.tempRect.bottom = (this.tempRect.top + this.minWidth);
      this.tempRect.right = (this.tempRect.left + this.tempRect.height() * this.lockAspectRatio);
    }
  }

  public void resetAnimator()
  {
    if (this.animator != null)
    {
      this.animator.cancel();
      this.animator = null;
    }
  }

  public void setActualRect(float paramFloat)
  {
    calculateRect(this.actualRect, paramFloat);
    updateTouchAreas();
    invalidate();
  }

  public void setActualRect(RectF paramRectF)
  {
    this.actualRect.set(paramRectF);
    updateTouchAreas();
    invalidate();
  }

  public void setBitmap(Bitmap paramBitmap, boolean paramBoolean1, boolean paramBoolean2)
  {
    float f2 = 1.0F;
    if ((paramBitmap == null) || (paramBitmap.isRecycled()))
      return;
    float f1;
    if (paramBoolean1)
    {
      f1 = paramBitmap.getHeight() / paramBitmap.getWidth();
      if (paramBoolean2)
        break label68;
      this.lockAspectRatio = 1.0F;
      f1 = f2;
    }
    label68: 
    while (true)
    {
      setActualRect(f1);
      return;
      f1 = paramBitmap.getWidth() / paramBitmap.getHeight();
      break;
    }
  }

  public void setBottomPadding(float paramFloat)
  {
    this.bottomPadding = paramFloat;
  }

  public void setDimVisibility(boolean paramBoolean)
  {
    this.dimVisibile = paramBoolean;
  }

  public void setFrameVisibility(boolean paramBoolean)
  {
    this.frameVisible = paramBoolean;
  }

  public void setGridType(GridType paramGridType, boolean paramBoolean)
  {
    if ((this.gridAnimator != null) && ((!paramBoolean) || (this.gridType != paramGridType)))
    {
      this.gridAnimator.cancel();
      this.gridAnimator = null;
    }
    if (this.gridType == paramGridType)
      return;
    this.previousGridType = this.gridType;
    this.gridType = paramGridType;
    float f;
    if (paramGridType == GridType.NONE)
      f = 0.0F;
    while (!paramBoolean)
    {
      this.gridProgress = f;
      invalidate();
      return;
      f = 1.0F;
    }
    this.gridAnimator = ObjectAnimator.ofFloat(this, "gridProgress", new float[] { this.gridProgress, f });
    this.gridAnimator.setDuration(200L);
    this.gridAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnimator)
      {
        CropAreaView.access$002(CropAreaView.this, null);
      }
    });
    if (paramGridType == GridType.NONE)
      this.gridAnimator.setStartDelay(200L);
    this.gridAnimator.start();
  }

  public void setListener(AreaViewListener paramAreaViewListener)
  {
    this.listener = paramAreaViewListener;
  }

  public void setLockedAspectRatio(float paramFloat)
  {
    this.lockAspectRatio = paramFloat;
  }

  static abstract interface AreaViewListener
  {
    public abstract void onAreaChange();

    public abstract void onAreaChangeBegan();

    public abstract void onAreaChangeEnded();
  }

  private static enum Control
  {
    static
    {
      BOTTOM_LEFT = new Control("BOTTOM_LEFT", 3);
      BOTTOM_RIGHT = new Control("BOTTOM_RIGHT", 4);
      TOP = new Control("TOP", 5);
      LEFT = new Control("LEFT", 6);
      BOTTOM = new Control("BOTTOM", 7);
      RIGHT = new Control("RIGHT", 8);
      $VALUES = new Control[] { NONE, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, LEFT, BOTTOM, RIGHT };
    }
  }

  static enum GridType
  {
    static
    {
      MINOR = new GridType("MINOR", 1);
      MAJOR = new GridType("MAJOR", 2);
      $VALUES = new GridType[] { NONE, MINOR, MAJOR };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Crop.CropAreaView
 * JD-Core Version:    0.6.0
 */