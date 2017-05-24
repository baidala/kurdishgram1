package org.vidogram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.util.ArrayList;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_photoCachedSize;
import org.vidogram.tgnet.TLRPC.TL_wallPaperSolid;
import org.vidogram.tgnet.TLRPC.WallPaper;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.LayoutHelper;

public class WallpaperCell extends FrameLayout
{
  private BackupImageView imageView;
  private ImageView imageView2;
  private View selectionView;

  public WallpaperCell(Context paramContext)
  {
    super(paramContext);
    this.imageView = new BackupImageView(paramContext);
    addView(this.imageView, LayoutHelper.createFrame(100, 100, 83));
    this.imageView2 = new ImageView(paramContext);
    this.imageView2.setImageResource(2130837778);
    this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
    addView(this.imageView2, LayoutHelper.createFrame(100, 100, 83));
    this.selectionView = new View(paramContext);
    this.selectionView.setBackgroundResource(2130838124);
    addView(this.selectionView, LayoutHelper.createFrame(100, 102.0F));
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0F), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(102.0F), 1073741824));
  }

  public void setWallpaper(TLRPC.WallPaper paramWallPaper, int paramInt, Drawable paramDrawable, boolean paramBoolean)
  {
    int i = 4;
    int k = 0;
    int j = 0;
    if (paramWallPaper == null)
    {
      this.imageView.setVisibility(4);
      this.imageView2.setVisibility(0);
      if (paramBoolean)
      {
        paramWallPaper = this.selectionView;
        if (paramInt == -2);
        for (paramInt = j; ; paramInt = 4)
        {
          paramWallPaper.setVisibility(paramInt);
          this.imageView2.setImageDrawable(paramDrawable);
          this.imageView2.setScaleType(ImageView.ScaleType.CENTER_CROP);
          return;
        }
      }
      paramWallPaper = this.selectionView;
      if (paramInt == -1)
      {
        i = k;
        paramWallPaper.setVisibility(i);
        paramWallPaper = this.imageView2;
        if ((paramInt != -1) && (paramInt != 1000001))
          break label138;
      }
      label138: for (paramInt = 1514625126; ; paramInt = 1509949440)
      {
        paramWallPaper.setBackgroundColor(paramInt);
        this.imageView2.setScaleType(ImageView.ScaleType.CENTER);
        return;
        i = 4;
        break;
      }
    }
    this.imageView.setVisibility(0);
    this.imageView2.setVisibility(4);
    paramDrawable = this.selectionView;
    if (paramInt == paramWallPaper.id)
      i = 0;
    paramDrawable.setVisibility(i);
    if ((paramWallPaper instanceof TLRPC.TL_wallPaperSolid))
    {
      this.imageView.setImageBitmap(null);
      this.imageView.setBackgroundColor(0xFF000000 | paramWallPaper.bg_color);
      return;
    }
    j = AndroidUtilities.dp(100.0F);
    paramInt = 0;
    paramDrawable = null;
    while (paramInt < paramWallPaper.sizes.size())
    {
      TLRPC.PhotoSize localPhotoSize = (TLRPC.PhotoSize)paramWallPaper.sizes.get(paramInt);
      Object localObject;
      if (localPhotoSize == null)
      {
        localObject = paramDrawable;
        paramInt += 1;
        paramDrawable = (Drawable)localObject;
        continue;
      }
      if (localPhotoSize.w >= localPhotoSize.h);
      for (i = localPhotoSize.w; ; i = localPhotoSize.h)
      {
        localObject = localPhotoSize;
        if (paramDrawable == null)
          break;
        if ((j > 100) && (paramDrawable.location != null))
        {
          localObject = localPhotoSize;
          if (paramDrawable.location.dc_id == -2147483648)
            break;
        }
        localObject = localPhotoSize;
        if ((localPhotoSize instanceof TLRPC.TL_photoCachedSize))
          break;
        localObject = localPhotoSize;
        if (i <= j)
          break;
        localObject = paramDrawable;
        break;
      }
    }
    if ((paramDrawable != null) && (paramDrawable.location != null))
      this.imageView.setImage(paramDrawable.location, "100_100", (Drawable)null);
    this.imageView.setBackgroundColor(1514625126);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Cells.WallpaperCell
 * JD-Core Version:    0.6.0
 */