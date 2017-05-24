package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;

public class SizeNotifierFrameLayoutPhoto extends FrameLayout
{
  private SizeNotifierFrameLayoutPhotoDelegate delegate;
  private int keyboardHeight;
  private Rect rect = new Rect();
  private WindowManager windowManager;
  private boolean withoutWindow;

  public SizeNotifierFrameLayoutPhoto(Context paramContext)
  {
    super(paramContext);
  }

  public int getKeyboardHeight()
  {
    int i = 0;
    int j = 0;
    View localView = getRootView();
    getWindowVisibleDisplayFrame(this.rect);
    int k;
    if (this.withoutWindow)
    {
      k = localView.getHeight();
      i = j;
      if (this.rect.top != 0)
        i = AndroidUtilities.statusBarHeight;
      i = k - i - AndroidUtilities.getViewInset(localView) - (this.rect.bottom - this.rect.top);
    }
    do
    {
      return i;
      j = localView.getHeight();
      k = AndroidUtilities.getViewInset(localView);
      int m = this.rect.top;
      j = AndroidUtilities.displaySize.y - m - (j - k);
    }
    while (j <= Math.max(AndroidUtilities.dp(10.0F), AndroidUtilities.statusBarHeight));
    return j;
  }

  public void notifyHeightChanged()
  {
    if (this.delegate != null)
    {
      this.keyboardHeight = getKeyboardHeight();
      if (AndroidUtilities.displaySize.x <= AndroidUtilities.displaySize.y)
        break label47;
    }
    label47: for (boolean bool = true; ; bool = false)
    {
      post(new Runnable(bool)
      {
        public void run()
        {
          if (SizeNotifierFrameLayoutPhoto.this.delegate != null)
            SizeNotifierFrameLayoutPhoto.this.delegate.onSizeChanged(SizeNotifierFrameLayoutPhoto.this.keyboardHeight, this.val$isWidthGreater);
        }
      });
      return;
    }
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    notifyHeightChanged();
  }

  public void setDelegate(SizeNotifierFrameLayoutPhotoDelegate paramSizeNotifierFrameLayoutPhotoDelegate)
  {
    this.delegate = paramSizeNotifierFrameLayoutPhotoDelegate;
  }

  public void setWithoutWindow(boolean paramBoolean)
  {
    this.withoutWindow = paramBoolean;
  }

  public static abstract interface SizeNotifierFrameLayoutPhotoDelegate
  {
    public abstract void onSizeChanged(int paramInt, boolean paramBoolean);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.SizeNotifierFrameLayoutPhoto
 * JD-Core Version:    0.6.0
 */