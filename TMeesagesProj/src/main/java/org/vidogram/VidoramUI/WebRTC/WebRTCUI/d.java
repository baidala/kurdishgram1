package org.vidogram.VidogramUi.WebRTC.WebRTCUI;

import android.content.Context;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;

public class d extends ViewGroup
{
  private int a = 0;
  private int b = 0;
  private int c = 100;
  private int d = 100;

  public d(Context paramContext)
  {
    super(paramContext);
  }

  public void a(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.a = paramInt1;
    this.b = paramInt2;
    this.c = paramInt3;
    this.d = paramInt4;
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    paramInt3 -= paramInt1;
    int k = paramInt4 - paramInt2;
    paramInt4 = this.c * paramInt3 / 100;
    int i = this.d * k / 100;
    int j = paramInt3 * this.a / 100;
    k = this.b * k / 100;
    paramInt3 = 0;
    while (paramInt3 < getChildCount())
    {
      View localView = getChildAt(paramInt3);
      if (localView.getVisibility() != 8)
      {
        int m = localView.getMeasuredWidth();
        int n = localView.getMeasuredHeight();
        int i1 = (paramInt4 - m) / 2 + (paramInt1 + j);
        int i2 = (i - n) / 2 + (paramInt2 + k);
        localView.layout(i1, i2, m + i1, n + i2);
      }
      paramInt3 += 1;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt1 = getDefaultSize(2147483647, paramInt1);
    int i = getDefaultSize(2147483647, paramInt2);
    setMeasuredDimension(View.MeasureSpec.makeMeasureSpec(paramInt1, 1073741824), View.MeasureSpec.makeMeasureSpec(i, 1073741824));
    paramInt2 = View.MeasureSpec.makeMeasureSpec(paramInt1 * this.c / 100, -2147483648);
    i = View.MeasureSpec.makeMeasureSpec(this.d * i / 100, -2147483648);
    paramInt1 = 0;
    while (paramInt1 < getChildCount())
    {
      View localView = getChildAt(paramInt1);
      if (localView.getVisibility() != 8)
        localView.measure(paramInt2, i);
      paramInt1 += 1;
    }
  }

  public boolean shouldDelayChildPressedState()
  {
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.WebRTC.WebRTCUI.d
 * JD-Core Version:    0.6.0
 */