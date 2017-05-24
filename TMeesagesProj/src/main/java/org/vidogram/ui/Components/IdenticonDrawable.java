package org.vidogram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.tgnet.TLRPC.EncryptedChat;

public class IdenticonDrawable extends Drawable
{
  private int[] colors = { -1, -2758925, -13805707, -13657655 };
  private byte[] data;
  private Paint paint = new Paint();

  private int getBits(int paramInt)
  {
    return this.data[(paramInt / 8)] >> paramInt % 8 & 0x3;
  }

  public void draw(Canvas paramCanvas)
  {
    if (this.data == null);
    while (true)
    {
      return;
      int k;
      int m;
      if (this.data.length == 16)
      {
        f1 = (float)Math.floor(Math.min(getBounds().width(), getBounds().height()) / 8.0F);
        f2 = Math.max(0.0F, (getBounds().width() - f1 * 8.0F) / 2.0F);
        f3 = Math.max(0.0F, (getBounds().height() - f1 * 8.0F) / 2.0F);
        i = 0;
        j = 0;
        while (i < 8)
        {
          k = 0;
          while (k < 8)
          {
            m = Math.abs(getBits(j));
            this.paint.setColor(this.colors[(m % 4)]);
            paramCanvas.drawRect(f2 + k * f1, i * f1 + f3, k * f1 + f2 + f1, i * f1 + f1 + f3, this.paint);
            k += 1;
            j += 2;
          }
          i += 1;
        }
        continue;
      }
      float f1 = (float)Math.floor(Math.min(getBounds().width(), getBounds().height()) / 12.0F);
      float f2 = Math.max(0.0F, (getBounds().width() - f1 * 12.0F) / 2.0F);
      float f3 = Math.max(0.0F, (getBounds().height() - f1 * 12.0F) / 2.0F);
      int j = 0;
      int i = 0;
      while (j < 12)
      {
        k = 0;
        while (k < 12)
        {
          m = Math.abs(getBits(i));
          this.paint.setColor(this.colors[(m % 4)]);
          paramCanvas.drawRect(f2 + k * f1, j * f1 + f3, k * f1 + f2 + f1, j * f1 + f1 + f3, this.paint);
          k += 1;
          i += 2;
        }
        j += 1;
      }
    }
  }

  public int getIntrinsicHeight()
  {
    return AndroidUtilities.dp(32.0F);
  }

  public int getIntrinsicWidth()
  {
    return AndroidUtilities.dp(32.0F);
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

  public void setColors(int[] paramArrayOfInt)
  {
    if (this.colors.length != 4)
      throw new IllegalArgumentException("colors must have length of 4");
    this.colors = paramArrayOfInt;
    invalidateSelf();
  }

  public void setEncryptedChat(TLRPC.EncryptedChat paramEncryptedChat)
  {
    this.data = paramEncryptedChat.key_hash;
    if (this.data == null)
    {
      byte[] arrayOfByte = AndroidUtilities.calcAuthKeyHash(paramEncryptedChat.auth_key);
      this.data = arrayOfByte;
      paramEncryptedChat.key_hash = arrayOfByte;
    }
    invalidateSelf();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.IdenticonDrawable
 * JD-Core Version:    0.6.0
 */