package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.vidogram.messenger.AndroidUtilities;

public class MapPlaceholderDrawable extends Drawable
{
  private Paint linePaint;
  private Paint paint = new Paint();

  public MapPlaceholderDrawable()
  {
    this.paint.setColor(-2172970);
    this.linePaint = new Paint();
    this.linePaint.setColor(-3752002);
    this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0F));
  }

  public void draw(Canvas paramCanvas)
  {
    int k = 0;
    paramCanvas.drawRect(getBounds(), this.paint);
    int m = AndroidUtilities.dp(9.0F);
    int i3 = getBounds().width() / m;
    int n = getBounds().height() / m;
    int i1 = getBounds().left;
    int i2 = getBounds().top;
    int i = 0;
    int j;
    while (true)
    {
      j = k;
      if (i >= i3)
        break;
      paramCanvas.drawLine((i + 1) * m + i1, i2, (i + 1) * m + i1, getBounds().height() + i2, this.linePaint);
      i += 1;
    }
    while (j < n)
    {
      paramCanvas.drawLine(i1, (j + 1) * m + i2, getBounds().width() + i1, (j + 1) * m + i2, this.linePaint);
      j += 1;
    }
  }

  public int getIntrinsicHeight()
  {
    return 0;
  }

  public int getIntrinsicWidth()
  {
    return 0;
  }

  public int getOpacity()
  {
    return 0;
  }

  public void setAlpha(int paramInt)
  {
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.MapPlaceholderDrawable
 * JD-Core Version:    0.6.0
 */