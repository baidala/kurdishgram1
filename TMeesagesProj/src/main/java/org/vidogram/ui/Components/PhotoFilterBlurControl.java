package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;

public class PhotoFilterBlurControl extends FrameLayout
{
  private static final float BlurInsetProximity = AndroidUtilities.dp(20.0F);
  private static final float BlurMinimumDifference = 0.02F;
  private static final float BlurMinimumFalloff = 0.1F;
  private static final float BlurViewCenterInset = AndroidUtilities.dp(30.0F);
  private static final float BlurViewRadiusInset = AndroidUtilities.dp(30.0F);
  private final int GestureStateBegan = 1;
  private final int GestureStateCancelled = 4;
  private final int GestureStateChanged = 2;
  private final int GestureStateEnded = 3;
  private final int GestureStateFailed = 5;
  private BlurViewActiveControl activeControl;
  private Size actualAreaSize = new Size();
  private float angle;
  private Paint arcPaint = new Paint(1);
  private RectF arcRect = new RectF();
  private Point centerPoint = new Point(0.5F, 0.5F);
  private boolean checkForMoving = true;
  private boolean checkForZooming;
  private PhotoFilterLinearBlurControlDelegate delegate;
  private float falloff = 0.15F;
  private boolean isMoving;
  private boolean isZooming;
  private Paint paint = new Paint(1);
  private float pointerScale = 1.0F;
  private float pointerStartX;
  private float pointerStartY;
  private float size = 0.35F;
  private Point startCenterPoint = new Point();
  private float startDistance;
  private float startPointerDistance;
  private float startRadius;
  private int type;

  public PhotoFilterBlurControl(Context paramContext)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.paint.setColor(-1);
    this.arcPaint.setColor(-1);
    this.arcPaint.setStrokeWidth(AndroidUtilities.dp(2.0F));
    this.arcPaint.setStyle(Paint.Style.STROKE);
  }

  private float degreesToRadians(float paramFloat)
  {
    return 3.141593F * paramFloat / 180.0F;
  }

  private Point getActualCenterPoint()
  {
    float f1 = (getWidth() - this.actualAreaSize.width) / 2.0F;
    float f2 = this.centerPoint.x;
    float f3 = this.actualAreaSize.width;
    if (Build.VERSION.SDK_INT >= 21);
    for (int i = AndroidUtilities.statusBarHeight; ; i = 0)
      return new Point(f2 * f3 + f1, i + (getHeight() - this.actualAreaSize.height) / 2.0F - (this.actualAreaSize.width - this.actualAreaSize.height) / 2.0F + this.centerPoint.y * this.actualAreaSize.width);
  }

  private float getActualInnerRadius()
  {
    float f;
    if (this.actualAreaSize.width > this.actualAreaSize.height)
      f = this.actualAreaSize.height;
    while (true)
    {
      return f * this.falloff;
      f = this.actualAreaSize.width;
    }
  }

  private float getActualOuterRadius()
  {
    float f;
    if (this.actualAreaSize.width > this.actualAreaSize.height)
      f = this.actualAreaSize.height;
    while (true)
    {
      return f * this.size;
      f = this.actualAreaSize.width;
    }
  }

  private float getDistance(MotionEvent paramMotionEvent)
  {
    if (paramMotionEvent.getPointerCount() != 2)
      return 0.0F;
    float f1 = paramMotionEvent.getX(0);
    float f2 = paramMotionEvent.getY(0);
    float f3 = paramMotionEvent.getX(1);
    float f4 = paramMotionEvent.getY(1);
    return (float)Math.sqrt((f1 - f3) * (f1 - f3) + (f2 - f4) * (f2 - f4));
  }

  private void handlePan(int paramInt, MotionEvent paramMotionEvent)
  {
    float f2 = paramMotionEvent.getX();
    float f3 = paramMotionEvent.getY();
    Point localPoint1 = getActualCenterPoint();
    Point localPoint2 = new Point(f2 - localPoint1.x, f3 - localPoint1.y);
    float f4 = (float)Math.sqrt(localPoint2.x * localPoint2.x + localPoint2.y * localPoint2.y);
    float f1;
    if (this.actualAreaSize.width > this.actualAreaSize.height)
      f1 = this.actualAreaSize.height;
    float f6;
    float f7;
    float f5;
    while (true)
    {
      f6 = f1 * this.falloff;
      f7 = f1 * this.size;
      f5 = (float)Math.abs(localPoint2.x * Math.cos(degreesToRadians(this.angle) + 1.570796326794897D) + localPoint2.y * Math.sin(degreesToRadians(this.angle) + 1.570796326794897D));
      switch (paramInt)
      {
      default:
        return;
        f1 = this.actualAreaSize.width;
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
    }
    this.pointerStartX = paramMotionEvent.getX();
    this.pointerStartY = paramMotionEvent.getY();
    if (Math.abs(f7 - f6) < BlurInsetProximity)
    {
      paramInt = 1;
      if (paramInt == 0)
        break label303;
      f1 = 0.0F;
      label255: if (paramInt == 0)
        break label310;
      f2 = 0.0F;
      label262: if (this.type != 0)
        break label441;
      if (f4 >= BlurViewCenterInset)
        break label318;
      this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
      this.startCenterPoint = localPoint1;
    }
    while (true)
    {
      setSelected(true, true);
      return;
      paramInt = 0;
      break;
      label303: f1 = BlurViewRadiusInset;
      break label255;
      label310: f2 = BlurViewRadiusInset;
      break label262;
      label318: if ((f5 > f6 - BlurViewRadiusInset) && (f5 < f1 + f6))
      {
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
        this.startDistance = f5;
        this.startRadius = f6;
        continue;
      }
      if ((f5 > f7 - f2) && (f5 < BlurViewRadiusInset + f7))
      {
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
        this.startDistance = f5;
        this.startRadius = f7;
        continue;
      }
      if ((f5 > f6 - BlurViewRadiusInset) && (f5 < BlurViewRadiusInset + f7))
        continue;
      this.activeControl = BlurViewActiveControl.BlurViewActiveControlRotation;
      continue;
      label441: if (this.type != 1)
        continue;
      if (f4 < BlurViewCenterInset)
      {
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlCenter;
        this.startCenterPoint = localPoint1;
        continue;
      }
      if ((f4 > f6 - BlurViewRadiusInset) && (f4 < f1 + f6))
      {
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlInnerRadius;
        this.startDistance = f4;
        this.startRadius = f6;
        continue;
      }
      if ((f4 <= f7 - f2) || (f4 >= BlurViewRadiusInset + f7))
        continue;
      this.activeControl = BlurViewActiveControl.BlurViewActiveControlOuterRadius;
      this.startDistance = f4;
      this.startRadius = f7;
    }
    label961: int i;
    if (this.type == 0)
    {
      switch (1.$SwitchMap$org$vidogram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()])
      {
      default:
      case 1:
      case 2:
      case 3:
        while (true)
        {
          invalidate();
          if (this.delegate == null)
            break;
          this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.570796F);
          return;
          f1 = this.pointerStartX;
          f4 = this.pointerStartY;
          paramMotionEvent = new Rect((getWidth() - this.actualAreaSize.width) / 2.0F, (getHeight() - this.actualAreaSize.height) / 2.0F, this.actualAreaSize.width, this.actualAreaSize.height);
          localPoint1 = new Point(Math.max(paramMotionEvent.x, Math.min(paramMotionEvent.x + paramMotionEvent.width, f2 - f1 + this.startCenterPoint.x)), Math.max(paramMotionEvent.y, Math.min(paramMotionEvent.y + paramMotionEvent.height, f3 - f4 + this.startCenterPoint.y)));
          this.centerPoint = new Point((localPoint1.x - paramMotionEvent.x) / this.actualAreaSize.width, (localPoint1.y - paramMotionEvent.y + (this.actualAreaSize.width - this.actualAreaSize.height) / 2.0F) / this.actualAreaSize.width);
          continue;
          this.falloff = Math.min(Math.max(0.1F, (f5 - this.startDistance + this.startRadius) / f1), this.size - 0.02F);
          continue;
          f2 = this.startDistance;
          this.size = Math.max(this.falloff + 0.02F, (f5 - f2 + this.startRadius) / f1);
        }
      case 4:
      }
      f1 = f2 - this.pointerStartX;
      f4 = f3 - this.pointerStartY;
      if (f2 > localPoint1.x)
      {
        paramInt = 1;
        if (f3 <= localPoint1.y)
          break label1075;
        i = 1;
        label975: if ((paramInt != 0) || (i != 0))
          break label1092;
        if (Math.abs(f4) <= Math.abs(f1))
          break label1081;
        if (f4 >= 0.0F)
          break label1559;
        paramInt = 1;
      }
    }
    while (true)
    {
      label1006: f1 = (float)Math.sqrt(f1 * f1 + f4 * f4);
      f4 = this.angle;
      if (paramInt != 0);
      for (paramInt = 1; ; paramInt = 0)
      {
        this.angle = ((paramInt * 2 - 1) * f1 / 3.141593F / 1.15F + f4);
        this.pointerStartX = f2;
        this.pointerStartY = f3;
        break;
        paramInt = 0;
        break label961;
        label1075: i = 0;
        break label975;
        label1081: if (f1 <= 0.0F)
          break label1559;
        paramInt = 1;
        break label1006;
        label1092: if ((paramInt != 0) && (i == 0))
        {
          if (Math.abs(f4) > Math.abs(f1))
          {
            if (f4 <= 0.0F)
              break label1559;
            paramInt = 1;
            break label1006;
          }
          if (f1 <= 0.0F)
            break label1559;
          paramInt = 1;
          break label1006;
        }
        if ((paramInt != 0) && (i != 0))
        {
          if (Math.abs(f4) > Math.abs(f1))
          {
            if (f4 <= 0.0F)
              break label1559;
            paramInt = 1;
            break label1006;
          }
          if (f1 >= 0.0F)
            break label1559;
          paramInt = 1;
          break label1006;
        }
        if (Math.abs(f4) > Math.abs(f1))
        {
          if (f4 >= 0.0F)
            break label1559;
          paramInt = 1;
          break label1006;
        }
        if (f1 >= 0.0F)
          break label1559;
        paramInt = 1;
        break label1006;
      }
      if (this.type != 1)
        break;
      switch (1.$SwitchMap$org$vidogram$ui$Components$PhotoFilterBlurControl$BlurViewActiveControl[this.activeControl.ordinal()])
      {
      default:
        break;
      case 1:
        f1 = this.pointerStartX;
        f4 = this.pointerStartY;
        paramMotionEvent = new Rect((getWidth() - this.actualAreaSize.width) / 2.0F, (getHeight() - this.actualAreaSize.height) / 2.0F, this.actualAreaSize.width, this.actualAreaSize.height);
        localPoint1 = new Point(Math.max(paramMotionEvent.x, Math.min(paramMotionEvent.x + paramMotionEvent.width, f2 - f1 + this.startCenterPoint.x)), Math.max(paramMotionEvent.y, Math.min(paramMotionEvent.y + paramMotionEvent.height, f3 - f4 + this.startCenterPoint.y)));
        this.centerPoint = new Point((localPoint1.x - paramMotionEvent.x) / this.actualAreaSize.width, (localPoint1.y - paramMotionEvent.y + (this.actualAreaSize.width - this.actualAreaSize.height) / 2.0F) / this.actualAreaSize.width);
        break;
      case 2:
        this.falloff = Math.min(Math.max(0.1F, (f4 - this.startDistance + this.startRadius) / f1), this.size - 0.02F);
        break;
      case 3:
        f2 = this.startDistance;
        this.size = Math.max(this.falloff + 0.02F, (f4 - f2 + this.startRadius) / f1);
        break;
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
        setSelected(false, true);
        return;
        label1559: paramInt = 0;
      }
    }
  }

  private void handlePinch(int paramInt, MotionEvent paramMotionEvent)
  {
    switch (paramInt)
    {
    default:
    case 1:
    case 2:
      do
      {
        return;
        this.startPointerDistance = getDistance(paramMotionEvent);
        this.pointerScale = 1.0F;
        this.activeControl = BlurViewActiveControl.BlurViewActiveControlWholeArea;
        setSelected(true, true);
        float f = getDistance(paramMotionEvent);
        this.pointerScale += (f - this.startPointerDistance) / AndroidUtilities.density * 0.01F;
        this.falloff = Math.max(0.1F, this.falloff * this.pointerScale);
        this.size = Math.max(this.falloff + 0.02F, this.size * this.pointerScale);
        this.pointerScale = 1.0F;
        this.startPointerDistance = f;
        invalidate();
      }
      while (this.delegate == null);
      this.delegate.valueChanged(this.centerPoint, this.falloff, this.size, degreesToRadians(this.angle) + 1.570796F);
      return;
    case 3:
    case 4:
    case 5:
    }
    this.activeControl = BlurViewActiveControl.BlurViewActiveControlNone;
    setSelected(false, true);
  }

  private void setSelected(boolean paramBoolean1, boolean paramBoolean2)
  {
  }

  protected void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    Point localPoint = getActualCenterPoint();
    float f4 = getActualInnerRadius();
    float f1 = getActualOuterRadius();
    paramCanvas.translate(localPoint.x, localPoint.y);
    int i;
    if (this.type == 0)
    {
      paramCanvas.rotate(this.angle);
      float f2 = AndroidUtilities.dp(6.0F);
      float f5 = AndroidUtilities.dp(12.0F);
      float f3 = AndroidUtilities.dp(1.5F);
      i = 0;
      while (i < 30)
      {
        paramCanvas.drawRect((f5 + f2) * i, -f4, i * (f5 + f2) + f5, f3 - f4, this.paint);
        paramCanvas.drawRect(-i * (f5 + f2) - f2 - f5, -f4, -i * (f5 + f2) - f2, f3 - f4, this.paint);
        paramCanvas.drawRect((f5 + f2) * i, f4, f5 + i * (f5 + f2), f3 + f4, this.paint);
        paramCanvas.drawRect(-i * (f5 + f2) - f2 - f5, f4, -i * (f5 + f2) - f2, f3 + f4, this.paint);
        i += 1;
      }
      f4 = AndroidUtilities.dp(6.0F);
      i = 0;
      while (i < 64)
      {
        paramCanvas.drawRect((f4 + f2) * i, -f1, f4 + i * (f4 + f2), f3 - f1, this.paint);
        paramCanvas.drawRect(-i * (f4 + f2) - f2 - f4, -f1, -i * (f4 + f2) - f2, f3 - f1, this.paint);
        paramCanvas.drawRect((f4 + f2) * i, f1, f4 + i * (f4 + f2), f3 + f1, this.paint);
        paramCanvas.drawRect(-i * (f4 + f2) - f2 - f4, f1, -i * (f4 + f2) - f2, f3 + f1, this.paint);
        i += 1;
      }
    }
    if (this.type == 1)
    {
      this.arcRect.set(-f4, -f4, f4, f4);
      i = 0;
      while (i < 22)
      {
        paramCanvas.drawArc(this.arcRect, (6.15F + 10.2F) * i, 10.2F, false, this.arcPaint);
        i += 1;
      }
      this.arcRect.set(-f1, -f1, f1, f1);
      i = 0;
      while (i < 64)
      {
        paramCanvas.drawArc(this.arcRect, (2.02F + 3.6F) * i, 3.6F, false, this.arcPaint);
        i += 1;
      }
    }
    paramCanvas.drawCircle(0.0F, 0.0F, AndroidUtilities.dp(8.0F), this.paint);
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getActionMasked())
    {
    case 4:
    default:
    case 0:
    case 5:
    case 1:
    case 3:
    case 6:
    case 2:
    }
    while (true)
    {
      return true;
      if (paramMotionEvent.getPointerCount() == 1)
      {
        if ((!this.checkForMoving) || (this.isMoving))
          continue;
        float f1 = paramMotionEvent.getX();
        float f2 = paramMotionEvent.getY();
        Point localPoint = getActualCenterPoint();
        localPoint = new Point(f1 - localPoint.x, f2 - localPoint.y);
        float f3 = (float)Math.sqrt(localPoint.x * localPoint.x + localPoint.y * localPoint.y);
        float f4 = getActualInnerRadius();
        float f5 = getActualOuterRadius();
        int i;
        label175: label183: label191: float f6;
        if (Math.abs(f5 - f4) < BlurInsetProximity)
        {
          i = 1;
          if (i == 0)
            break label298;
          f1 = 0.0F;
          if (i == 0)
            break label306;
          f2 = 0.0F;
          if (this.type != 0)
            break label408;
          double d1 = localPoint.x;
          double d2 = Math.cos(degreesToRadians(this.angle) + 1.570796326794897D);
          f6 = (float)Math.abs(localPoint.y * Math.sin(degreesToRadians(this.angle) + 1.570796326794897D) + d1 * d2);
          if (f3 >= BlurViewCenterInset)
            break label314;
          this.isMoving = true;
        }
        while (true)
        {
          this.checkForMoving = false;
          if (!this.isMoving)
            break;
          handlePan(1, paramMotionEvent);
          break;
          i = 0;
          break label175;
          label298: f1 = BlurViewRadiusInset;
          break label183;
          label306: f2 = BlurViewRadiusInset;
          break label191;
          label314: if ((f6 > f4 - BlurViewRadiusInset) && (f6 < f1 + f4))
          {
            this.isMoving = true;
            continue;
          }
          if ((f6 > f5 - f2) && (f6 < BlurViewRadiusInset + f5))
          {
            this.isMoving = true;
            continue;
          }
          if ((f6 > f4 - BlurViewRadiusInset) && (f6 < BlurViewRadiusInset + f5))
            continue;
          this.isMoving = true;
          continue;
          label408: if (this.type != 1)
            continue;
          if (f3 < BlurViewCenterInset)
          {
            this.isMoving = true;
            continue;
          }
          if ((f3 > f4 - BlurViewRadiusInset) && (f3 < f1 + f4))
          {
            this.isMoving = true;
            continue;
          }
          if ((f3 <= f5 - f2) || (f3 >= BlurViewRadiusInset + f5))
            continue;
          this.isMoving = true;
        }
      }
      if (this.isMoving)
      {
        handlePan(3, paramMotionEvent);
        this.checkForMoving = true;
        this.isMoving = false;
      }
      if (paramMotionEvent.getPointerCount() == 2)
      {
        if ((!this.checkForZooming) || (this.isZooming))
          continue;
        handlePinch(1, paramMotionEvent);
        this.isZooming = true;
        continue;
      }
      handlePinch(3, paramMotionEvent);
      this.checkForZooming = true;
      this.isZooming = false;
      continue;
      if (this.isMoving)
      {
        handlePan(3, paramMotionEvent);
        this.isMoving = false;
      }
      while (true)
      {
        this.checkForMoving = true;
        this.checkForZooming = true;
        break;
        if (!this.isZooming)
          continue;
        handlePinch(3, paramMotionEvent);
        this.isZooming = false;
      }
      if (this.isMoving)
      {
        handlePan(2, paramMotionEvent);
        continue;
      }
      if (!this.isZooming)
        continue;
      handlePinch(2, paramMotionEvent);
    }
  }

  public void setActualAreaSize(float paramFloat1, float paramFloat2)
  {
    this.actualAreaSize.width = paramFloat1;
    this.actualAreaSize.height = paramFloat2;
  }

  public void setDelegate(PhotoFilterLinearBlurControlDelegate paramPhotoFilterLinearBlurControlDelegate)
  {
    this.delegate = paramPhotoFilterLinearBlurControlDelegate;
  }

  public void setType(int paramInt)
  {
    this.type = paramInt;
    invalidate();
  }

  private static enum BlurViewActiveControl
  {
    static
    {
      BlurViewActiveControlCenter = new BlurViewActiveControl("BlurViewActiveControlCenter", 1);
      BlurViewActiveControlInnerRadius = new BlurViewActiveControl("BlurViewActiveControlInnerRadius", 2);
      BlurViewActiveControlOuterRadius = new BlurViewActiveControl("BlurViewActiveControlOuterRadius", 3);
      BlurViewActiveControlWholeArea = new BlurViewActiveControl("BlurViewActiveControlWholeArea", 4);
      BlurViewActiveControlRotation = new BlurViewActiveControl("BlurViewActiveControlRotation", 5);
      $VALUES = new BlurViewActiveControl[] { BlurViewActiveControlNone, BlurViewActiveControlCenter, BlurViewActiveControlInnerRadius, BlurViewActiveControlOuterRadius, BlurViewActiveControlWholeArea, BlurViewActiveControlRotation };
    }
  }

  public static abstract interface PhotoFilterLinearBlurControlDelegate
  {
    public abstract void valueChanged(Point paramPoint, float paramFloat1, float paramFloat2, float paramFloat3);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.PhotoFilterBlurControl
 * JD-Core Version:    0.6.0
 */