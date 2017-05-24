package org.vidogram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewParent;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.MessageObject;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.Peer;

public class SeekBarWaveform
{
  private static Paint paintInner;
  private static Paint paintOuter;
  private SeekBar.SeekBarDelegate delegate;
  private int height;
  private int innerColor;
  private MessageObject messageObject;
  private int outerColor;
  private View parentView;
  private boolean pressed = false;
  private boolean selected;
  private int selectedColor;
  private boolean startDraging = false;
  private float startX;
  private int thumbDX = 0;
  private int thumbX = 0;
  private byte[] waveformBytes;
  private int width;

  public SeekBarWaveform(Context paramContext)
  {
    if (paintInner == null)
    {
      paintInner = new Paint();
      paintOuter = new Paint();
    }
  }

  public void draw(Canvas paramCanvas)
  {
    if ((this.waveformBytes == null) || (this.width == 0));
    float f1;
    do
    {
      return;
      f1 = this.width / AndroidUtilities.dp(3.0F);
    }
    while (f1 <= 0.1F);
    int i2 = this.waveformBytes.length * 8 / 5;
    float f2 = i2 / f1;
    Paint localPaint = paintInner;
    int i;
    if ((this.messageObject != null) && (!this.messageObject.isOutOwner()) && (this.messageObject.isContentUnread()) && (this.messageObject.messageOwner.to_id.channel_id == 0))
      i = this.outerColor;
    int i3;
    int m;
    while (true)
    {
      localPaint.setColor(i);
      paintOuter.setColor(this.outerColor);
      i3 = (this.height - AndroidUtilities.dp(14.0F)) / 2;
      i = 0;
      m = 0;
      j = 0;
      f1 = 0.0F;
      while (m < i2)
      {
        if (m == j)
          break label196;
        m += 1;
      }
      break;
      if (this.selected)
      {
        i = this.selectedColor;
        continue;
      }
      i = this.innerColor;
    }
    label196: int n = 0;
    int k = j;
    while (j == k)
    {
      f1 += f2;
      k = (int)f1;
      n += 1;
    }
    int j = m * 5;
    int i1 = j / 8;
    j -= i1 * 8;
    int i5 = 8 - j;
    int i4 = 5 - i5;
    j = (byte)(this.waveformBytes[i1] >> j & (2 << Math.min(5, i5) - 1) - 1);
    if (i4 > 0)
      j = (byte)((byte)(j << i4) | this.waveformBytes[(i1 + 1)] & (2 << i4 - 1) - 1);
    while (true)
    {
      i1 = 0;
      if (i1 < n)
      {
        i4 = i * AndroidUtilities.dp(3.0F);
        if ((i4 < this.thumbX) && (AndroidUtilities.dp(2.0F) + i4 < this.thumbX))
          paramCanvas.drawRect(i4, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i3, AndroidUtilities.dp(2.0F) + i4, AndroidUtilities.dp(14.0F) + i3, paintOuter);
        while (true)
        {
          i += 1;
          i1 += 1;
          break;
          paramCanvas.drawRect(i4, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i3, AndroidUtilities.dp(2.0F) + i4, AndroidUtilities.dp(14.0F) + i3, paintInner);
          if (i4 >= this.thumbX)
            continue;
          paramCanvas.drawRect(i4, AndroidUtilities.dp(14.0F - Math.max(1.0F, 14.0F * j / 31.0F)) + i3, this.thumbX, AndroidUtilities.dp(14.0F) + i3, paintOuter);
        }
      }
      j = k;
      break;
    }
  }

  public boolean isDragging()
  {
    return this.pressed;
  }

  public boolean isStartDraging()
  {
    return this.startDraging;
  }

  public boolean onTouch(int paramInt, float paramFloat1, float paramFloat2)
  {
    if (paramInt == 0)
    {
      if ((0.0F <= paramFloat1) && (paramFloat1 <= this.width) && (paramFloat2 >= 0.0F) && (paramFloat2 <= this.height))
      {
        this.startX = paramFloat1;
        this.pressed = true;
        this.thumbDX = (int)(paramFloat1 - this.thumbX);
        this.startDraging = false;
      }
    }
    else
      while (true)
      {
        return true;
        if ((paramInt == 1) || (paramInt == 3))
        {
          if (!this.pressed)
            break;
          if ((paramInt == 1) && (this.delegate != null))
            this.delegate.onSeekBarDrag(this.thumbX / this.width);
          this.pressed = false;
          return true;
        }
        if ((paramInt != 2) || (!this.pressed))
          break;
        if (this.startDraging)
        {
          this.thumbX = (int)(paramFloat1 - this.thumbDX);
          if (this.thumbX >= 0)
            break label236;
          this.thumbX = 0;
        }
        while ((this.startX != -1.0F) && (Math.abs(paramFloat1 - this.startX) > AndroidUtilities.getPixelsInCM(0.2F, true)))
        {
          if ((this.parentView != null) && (this.parentView.getParent() != null))
            this.parentView.getParent().requestDisallowInterceptTouchEvent(true);
          this.startDraging = true;
          this.startX = -1.0F;
          return true;
          label236: if (this.thumbX <= this.width)
            continue;
          this.thumbX = this.width;
        }
      }
    return false;
  }

  public void setColors(int paramInt1, int paramInt2, int paramInt3)
  {
    this.innerColor = paramInt1;
    this.outerColor = paramInt2;
    this.selectedColor = paramInt3;
  }

  public void setDelegate(SeekBar.SeekBarDelegate paramSeekBarDelegate)
  {
    this.delegate = paramSeekBarDelegate;
  }

  public void setMessageObject(MessageObject paramMessageObject)
  {
    this.messageObject = paramMessageObject;
  }

  public void setParentView(View paramView)
  {
    this.parentView = paramView;
  }

  public void setProgress(float paramFloat)
  {
    this.thumbX = (int)Math.ceil(this.width * paramFloat);
    if (this.thumbX < 0)
      this.thumbX = 0;
    do
      return;
    while (this.thumbX <= this.width);
    this.thumbX = this.width;
  }

  public void setSelected(boolean paramBoolean)
  {
    this.selected = paramBoolean;
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }

  public void setWaveform(byte[] paramArrayOfByte)
  {
    this.waveformBytes = paramArrayOfByte;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.SeekBarWaveform
 * JD-Core Version:    0.6.0
 */