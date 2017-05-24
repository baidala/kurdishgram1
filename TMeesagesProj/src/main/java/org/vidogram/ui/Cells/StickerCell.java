package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class StickerCell extends FrameLayout
{
  private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5F);
  private BackupImageView imageView;
  private long lastUpdateTime;
  private float scale;
  private boolean scaled;
  private TLRPC.Document sticker;
  private long time = 0L;

  public StickerCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    this.imageView.setAspectFit(true);
    addView(this.imageView, LayoutHelper.createFrame(66, 66.0F, 1, 0.0F, 5.0F, 0.0F, 0.0F));
  }

  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    boolean bool = super.drawChild(paramCanvas, paramView, paramLong);
    long l;
    if ((paramView == this.imageView) && (((this.scaled) && (this.scale != 0.8F)) || ((!this.scaled) && (this.scale != 1.0F))))
    {
      paramLong = System.currentTimeMillis();
      l = paramLong - this.lastUpdateTime;
      this.lastUpdateTime = paramLong;
      if ((!this.scaled) || (this.scale == 0.8F))
        break label151;
      this.scale -= (float)l / 400.0F;
      if (this.scale < 0.8F)
        this.scale = 0.8F;
    }
    while (true)
    {
      this.imageView.setScaleX(this.scale);
      this.imageView.setScaleY(this.scale);
      this.imageView.invalidate();
      invalidate();
      return bool;
      label151: this.scale += (float)l / 400.0F;
      if (this.scale <= 1.0F)
        continue;
      this.scale = 1.0F;
    }
  }

  public TLRPC.Document getSticker()
  {
    return this.sticker;
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(76.0F) + getPaddingLeft() + getPaddingRight(), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(78.0F), 1073741824));
  }

  public void setPressed(boolean paramBoolean)
  {
    if (this.imageView.getImageReceiver().getPressed() != paramBoolean)
    {
      this.imageView.getImageReceiver().setPressed(paramBoolean);
      this.imageView.invalidate();
    }
    super.setPressed(paramBoolean);
  }

  public void setScaled(boolean paramBoolean)
  {
    this.scaled = paramBoolean;
    this.lastUpdateTime = System.currentTimeMillis();
    invalidate();
  }

  public void setSticker(TLRPC.Document paramDocument, int paramInt)
  {
    if ((paramDocument != null) && (paramDocument.thumb != null))
      this.imageView.setImage(paramDocument.thumb.location, null, "webp", null);
    this.sticker = paramDocument;
    if (paramInt == -1)
    {
      setBackgroundResource(2130838075);
      setPadding(AndroidUtilities.dp(7.0F), 0, 0, 0);
    }
    while (true)
    {
      paramDocument = getBackground();
      if (paramDocument != null)
      {
        paramDocument.setAlpha(230);
        paramDocument.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_stickersHintPanel"), PorterDuff.Mode.MULTIPLY));
      }
      return;
      if (paramInt == 0)
      {
        setBackgroundResource(2130838074);
        setPadding(0, 0, 0, 0);
        continue;
      }
      if (paramInt == 1)
      {
        setBackgroundResource(2130838076);
        setPadding(0, 0, AndroidUtilities.dp(7.0F), 0);
        continue;
      }
      if (paramInt != 2)
        continue;
      setBackgroundResource(2130838072);
      setPadding(AndroidUtilities.dp(3.0F), 0, AndroidUtilities.dp(3.0F), 0);
    }
  }

  public boolean showingBitmap()
  {
    return this.imageView.getImageReceiver().getBitmap() != null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.StickerCell
 * JD-Core Version:    0.6.0
 */