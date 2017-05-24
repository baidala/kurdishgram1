package org.vidogram.messenger.exoplayer2.ui;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class AspectRatioFrameLayout extends FrameLayout
{
  private static final float MAX_ASPECT_RATIO_DEFORMATION_FRACTION = 0.01F;
  public static final int RESIZE_MODE_FILL = 3;
  public static final int RESIZE_MODE_FIT = 0;
  public static final int RESIZE_MODE_FIXED_HEIGHT = 2;
  public static final int RESIZE_MODE_FIXED_WIDTH = 1;
  private Matrix matrix = new Matrix();
  private int resizeMode = 0;
  private int rotation;
  private float videoAspectRatio;

  public AspectRatioFrameLayout(Context paramContext)
  {
    this(paramContext, null);
  }

  public AspectRatioFrameLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public float getAspectRatio()
  {
    return this.videoAspectRatio;
  }

  public int getVideoRotation()
  {
    return this.rotation;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    if ((this.resizeMode == 3) || (this.videoAspectRatio <= 0.0F));
    float f;
    do
    {
      return;
      paramInt2 = getMeasuredWidth();
      paramInt1 = getMeasuredHeight();
      f = paramInt2 / paramInt1;
      f = this.videoAspectRatio / f - 1.0F;
    }
    while (Math.abs(f) <= 0.01F);
    switch (this.resizeMode)
    {
    default:
      if (f <= 0.0F)
        break;
      paramInt1 = (int)(paramInt2 / this.videoAspectRatio);
      label99: super.onMeasure(View.MeasureSpec.makeMeasureSpec(paramInt2, 1073741824), View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824));
      paramInt2 = getChildCount();
      paramInt1 = 0;
    case 1:
    case 2:
    }
    while (paramInt1 < paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if ((localView instanceof TextureView))
      {
        this.matrix.reset();
        paramInt1 = getWidth() / 2;
        paramInt2 = getHeight() / 2;
        this.matrix.postRotate(this.rotation, paramInt1, paramInt2);
        if ((this.rotation == 90) || (this.rotation == 270))
        {
          f = getHeight() / getWidth();
          this.matrix.postScale(1.0F / f, f, paramInt1, paramInt2);
        }
        ((TextureView)localView).setTransform(this.matrix);
        return;
        paramInt1 = (int)(paramInt2 / this.videoAspectRatio);
        break label99;
        paramInt2 = (int)(paramInt1 * this.videoAspectRatio);
        break label99;
        paramInt2 = (int)(paramInt1 * this.videoAspectRatio);
        break label99;
      }
      paramInt1 += 1;
    }
  }

  public void setAspectRatio(float paramFloat, int paramInt)
  {
    if ((this.videoAspectRatio != paramFloat) || (this.rotation != paramInt))
    {
      this.videoAspectRatio = paramFloat;
      this.rotation = paramInt;
      requestLayout();
    }
  }

  public void setResizeMode(int paramInt)
  {
    if (this.resizeMode != paramInt)
    {
      this.resizeMode = paramInt;
      requestLayout();
    }
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface ResizeMode
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.ui.AspectRatioFrameLayout
 * JD-Core Version:    0.6.0
 */