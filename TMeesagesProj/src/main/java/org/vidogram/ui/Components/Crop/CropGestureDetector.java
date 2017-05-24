package org.vidogram.ui.Components.Crop;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import org.vidogram.messenger.AndroidUtilities;

public class CropGestureDetector
{
  private static final int INVALID_POINTER_ID = -1;
  private int mActivePointerId;
  private int mActivePointerIndex;
  private ScaleGestureDetector mDetector;
  private boolean mIsDragging;
  float mLastTouchX;
  float mLastTouchY;
  private CropGestureListener mListener;
  final float mMinimumVelocity;
  final float mTouchSlop;
  private VelocityTracker mVelocityTracker;
  private boolean started;

  public CropGestureDetector(Context paramContext)
  {
    this.mMinimumVelocity = ViewConfiguration.get(paramContext).getScaledMinimumFlingVelocity();
    this.mTouchSlop = AndroidUtilities.dp(1.0F);
    this.mActivePointerId = -1;
    this.mActivePointerIndex = 0;
    this.mDetector = new ScaleGestureDetector(paramContext, new ScaleGestureDetector.OnScaleGestureListener()
    {
      public boolean onScale(ScaleGestureDetector paramScaleGestureDetector)
      {
        float f = paramScaleGestureDetector.getScaleFactor();
        if ((Float.isNaN(f)) || (Float.isInfinite(f)))
          return false;
        CropGestureDetector.this.mListener.onScale(f, paramScaleGestureDetector.getFocusX(), paramScaleGestureDetector.getFocusY());
        return true;
      }

      public boolean onScaleBegin(ScaleGestureDetector paramScaleGestureDetector)
      {
        return true;
      }

      public void onScaleEnd(ScaleGestureDetector paramScaleGestureDetector)
      {
      }
    });
  }

  float getActiveX(MotionEvent paramMotionEvent)
  {
    try
    {
      float f = paramMotionEvent.getX(this.mActivePointerIndex);
      return f;
    }
    catch (Exception localException)
    {
    }
    return paramMotionEvent.getX();
  }

  float getActiveY(MotionEvent paramMotionEvent)
  {
    try
    {
      float f = paramMotionEvent.getY(this.mActivePointerIndex);
      return f;
    }
    catch (Exception localException)
    {
    }
    return paramMotionEvent.getY();
  }

  public boolean isDragging()
  {
    return this.mIsDragging;
  }

  public boolean isScaling()
  {
    return this.mDetector.isInProgress();
  }

  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    boolean bool = false;
    this.mDetector.onTouchEvent(paramMotionEvent);
    switch (paramMotionEvent.getAction() & 0xFF)
    {
    case 2:
    case 4:
    case 5:
    default:
      if (this.mActivePointerId == -1)
        break;
    case 0:
    case 1:
    case 3:
    case 6:
    }
    for (int i = this.mActivePointerId; ; i = 0)
    {
      this.mActivePointerIndex = paramMotionEvent.findPointerIndex(i);
      float f1;
      float f2;
      switch (paramMotionEvent.getAction())
      {
      default:
      case 0:
      case 2:
        do
        {
          float f3;
          float f4;
          do
          {
            return true;
            this.mActivePointerId = paramMotionEvent.getPointerId(0);
            break;
            this.mActivePointerId = -1;
            break;
            i = (0xFF00 & paramMotionEvent.getAction()) >> 8;
            if (paramMotionEvent.getPointerId(i) != this.mActivePointerId)
              break;
            if (i == 0);
            for (i = 1; ; i = 0)
            {
              this.mActivePointerId = paramMotionEvent.getPointerId(i);
              this.mLastTouchX = paramMotionEvent.getX(i);
              this.mLastTouchY = paramMotionEvent.getY(i);
              break;
            }
            if (!this.started)
            {
              this.mVelocityTracker = VelocityTracker.obtain();
              if (this.mVelocityTracker != null)
                this.mVelocityTracker.addMovement(paramMotionEvent);
              this.mLastTouchX = getActiveX(paramMotionEvent);
              this.mLastTouchY = getActiveY(paramMotionEvent);
              this.mIsDragging = false;
              this.started = true;
              return true;
            }
            f1 = getActiveX(paramMotionEvent);
            f2 = getActiveY(paramMotionEvent);
            f3 = f1 - this.mLastTouchX;
            f4 = f2 - this.mLastTouchY;
            if (this.mIsDragging)
              continue;
            if ((float)Math.sqrt(f3 * f3 + f4 * f4) >= this.mTouchSlop)
              bool = true;
            this.mIsDragging = bool;
          }
          while (!this.mIsDragging);
          this.mListener.onDrag(f3, f4);
          this.mLastTouchX = f1;
          this.mLastTouchY = f2;
        }
        while (this.mVelocityTracker == null);
        this.mVelocityTracker.addMovement(paramMotionEvent);
        return true;
      case 3:
        if (this.mVelocityTracker != null)
        {
          this.mVelocityTracker.recycle();
          this.mVelocityTracker = null;
        }
        this.started = false;
        this.mIsDragging = false;
        return true;
      case 1:
      }
      if (this.mIsDragging)
      {
        if (this.mVelocityTracker != null)
        {
          this.mLastTouchX = getActiveX(paramMotionEvent);
          this.mLastTouchY = getActiveY(paramMotionEvent);
          this.mVelocityTracker.addMovement(paramMotionEvent);
          this.mVelocityTracker.computeCurrentVelocity(1000);
          f1 = this.mVelocityTracker.getXVelocity();
          f2 = this.mVelocityTracker.getYVelocity();
          if (Math.max(Math.abs(f1), Math.abs(f2)) >= this.mMinimumVelocity)
            this.mListener.onFling(this.mLastTouchX, this.mLastTouchY, -f1, -f2);
        }
        this.mIsDragging = false;
      }
      if (this.mVelocityTracker != null)
      {
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
      }
      this.started = false;
      return true;
    }
  }

  public void setOnGestureListener(CropGestureListener paramCropGestureListener)
  {
    this.mListener = paramCropGestureListener;
  }

  public static abstract interface CropGestureListener
  {
    public abstract void onDrag(float paramFloat1, float paramFloat2);

    public abstract void onFling(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4);

    public abstract void onScale(float paramFloat1, float paramFloat2, float paramFloat3);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Crop.CropGestureDetector
 * JD-Core Version:    0.6.0
 */