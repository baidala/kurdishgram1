package org.vidogram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentEncrypted;
import org.vidogram.tgnet.TLRPC.TL_fileEncryptedLocation;
import org.vidogram.tgnet.TLRPC.TL_fileLocation;
import org.vidogram.tgnet.TLRPC.TL_webDocument;
import org.vidogram.ui.Components.AnimatedFileDrawable;

public class ImageReceiver
  implements NotificationCenter.NotificationCenterDelegate
{
  private static Paint roundPaint;
  private static PorterDuffColorFilter selectedColorFilter = new PorterDuffColorFilter(-2236963, PorterDuff.Mode.MULTIPLY);
  private boolean allowStartAnimation = true;
  private RectF bitmapRect = new RectF();
  private BitmapShader bitmapShader;
  private BitmapShader bitmapShaderThumb;
  private boolean canceledLoading;
  private boolean centerRotation;
  private ColorFilter colorFilter;
  private byte crossfadeAlpha = 1;
  private boolean crossfadeWithThumb;
  private float currentAlpha;
  private boolean currentCacheOnly;
  private String currentExt;
  private String currentFilter;
  private String currentHttpUrl;
  private Drawable currentImage;
  private TLObject currentImageLocation;
  private String currentKey;
  private int currentSize;
  private Drawable currentThumb;
  private String currentThumbFilter;
  private String currentThumbKey;
  private TLRPC.FileLocation currentThumbLocation;
  private ImageReceiverDelegate delegate;
  private Rect drawRegion = new Rect();
  private boolean forcePreview;
  private int imageH;
  private int imageW;
  private int imageX;
  private int imageY;
  private boolean invalidateAll;
  private boolean isAspectFit;
  private boolean isPressed;
  private boolean isVisible = true;
  private long lastUpdateAlphaTime;
  private boolean needsQualityThumb;
  private int orientation;
  private float overrideAlpha = 1.0F;
  private MessageObject parentMessageObject;
  private View parentView;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private SetImageBackup setImageBackup;
  private Matrix shaderMatrix = new Matrix();
  private boolean shouldGenerateQualityThumb;
  private Drawable staticThumb;
  private Integer tag;
  private Integer thumbTag;

  public ImageReceiver()
  {
    this(null);
  }

  public ImageReceiver(View paramView)
  {
    this.parentView = paramView;
    if (roundPaint == null)
      roundPaint = new Paint(1);
  }

  private void checkAlphaAnimation(boolean paramBoolean)
  {
    long l1 = 18L;
    long l2;
    if (this.currentAlpha != 1.0F)
      if (!paramBoolean)
      {
        l2 = System.currentTimeMillis() - this.lastUpdateAlphaTime;
        if (l2 <= 18L)
          break label129;
      }
    while (true)
    {
      float f = this.currentAlpha;
      this.currentAlpha = ((float)l1 / 150.0F + f);
      if (this.currentAlpha > 1.0F)
        this.currentAlpha = 1.0F;
      this.lastUpdateAlphaTime = System.currentTimeMillis();
      if (this.parentView != null)
      {
        if (this.invalidateAll)
          this.parentView.invalidate();
      }
      else
        return;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      return;
      label129: l1 = l2;
    }
  }

  private void drawDrawable(Canvas paramCanvas, Drawable paramDrawable, int paramInt, BitmapShader paramBitmapShader)
  {
    BitmapDrawable localBitmapDrawable;
    Paint localPaint;
    int i;
    label39: label64: label87: int k;
    label136: float f1;
    float f2;
    if ((paramDrawable instanceof BitmapDrawable))
    {
      localBitmapDrawable = (BitmapDrawable)paramDrawable;
      label421: if (paramBitmapShader != null)
      {
        localPaint = roundPaint;
        if ((localPaint == null) || (localPaint.getColorFilter() == null))
          break label469;
        i = 1;
        if ((i == 0) || (this.isPressed))
          break label492;
        if (paramBitmapShader == null)
          break label475;
        roundPaint.setColorFilter(null);
        if (this.colorFilter != null)
        {
          if (paramBitmapShader == null)
            break label533;
          roundPaint.setColorFilter(this.colorFilter);
        }
        if (!(localBitmapDrawable instanceof AnimatedFileDrawable))
          break label562;
        if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270))
          break label545;
        k = localBitmapDrawable.getIntrinsicHeight();
        i = localBitmapDrawable.getIntrinsicWidth();
        f1 = k / this.imageW;
        f2 = i / this.imageH;
        if (paramBitmapShader == null)
          break label769;
        roundPaint.setShader(paramBitmapShader);
        float f3 = Math.min(f1, f2);
        this.roundRect.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        this.shaderMatrix.reset();
        if (Math.abs(f1 - f2) <= 1.0E-005F)
          break label696;
        if (k / f2 <= this.imageW)
          break label635;
        this.drawRegion.set(this.imageX - ((int)(k / f2) - this.imageW) / 2, this.imageY, this.imageX + ((int)(k / f2) + this.imageW) / 2, this.imageY + this.imageH);
        label312: if (this.isVisible)
        {
          if (Math.abs(f1 - f2) <= 1.0E-005F)
            break label732;
          int n = (int)Math.floor(this.imageW * f3);
          int i2 = (int)Math.floor(f3 * this.imageH);
          this.bitmapRect.set((k - n) / 2, (i - i2) / 2, (k + n) / 2, (i2 + i) / 2);
          this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.START);
          paramBitmapShader.setLocalMatrix(this.shaderMatrix);
          roundPaint.setAlpha(paramInt);
          paramCanvas.drawRoundRect(this.roundRect, this.roundRadius, this.roundRadius, roundPaint);
        }
      }
    }
    label469: label475: label492: label635: label1405: 
    do
    {
      return;
      localPaint = localBitmapDrawable.getPaint();
      break;
      i = 0;
      break label39;
      if (this.staticThumb == paramDrawable)
        break label64;
      localBitmapDrawable.setColorFilter(null);
      break label64;
      if ((i != 0) || (!this.isPressed))
        break label64;
      if (paramBitmapShader != null)
      {
        roundPaint.setColorFilter(selectedColorFilter);
        break label64;
      }
      localBitmapDrawable.setColorFilter(selectedColorFilter);
      break label64;
      localBitmapDrawable.setColorFilter(this.colorFilter);
      break label87;
      k = localBitmapDrawable.getIntrinsicWidth();
      i = localBitmapDrawable.getIntrinsicHeight();
      break label136;
      if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
      {
        k = localBitmapDrawable.getBitmap().getHeight();
        i = localBitmapDrawable.getBitmap().getWidth();
        break label136;
      }
      k = localBitmapDrawable.getBitmap().getWidth();
      i = localBitmapDrawable.getBitmap().getHeight();
      break label136;
      this.drawRegion.set(this.imageX, this.imageY - ((int)(i / f1) - this.imageH) / 2, this.imageX + this.imageW, this.imageY + ((int)(i / f1) + this.imageH) / 2);
      break label312;
      this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label312;
      this.bitmapRect.set(0.0F, 0.0F, k, i);
      this.shaderMatrix.setRectToRect(this.bitmapRect, this.roundRect, Matrix.ScaleToFit.FILL);
      break label421;
      int i6;
      int j;
      int m;
      int i1;
      int i3;
      int i4;
      int i5;
      if (this.isAspectFit)
      {
        f1 = Math.max(f1, f2);
        paramCanvas.save();
        i6 = (int)(k / f1);
        j = (int)(i / f1);
        paramDrawable = this.drawRegion;
        m = this.imageX;
        i1 = (this.imageW - i6) / 2;
        i3 = this.imageY;
        i4 = (this.imageH - j) / 2;
        i5 = this.imageX;
        i6 = (i6 + this.imageW) / 2;
        int i7 = this.imageY;
        paramDrawable.set(m + i1, i3 + i4, i6 + i5, (j + this.imageH) / 2 + i7);
        localBitmapDrawable.setBounds(this.drawRegion);
        try
        {
          localBitmapDrawable.setAlpha(paramInt);
          localBitmapDrawable.draw(paramCanvas);
          paramCanvas.restore();
          return;
        }
        catch (java.lang.Exception paramDrawable)
        {
          if (localBitmapDrawable != this.currentImage)
            break label1006;
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        while (true)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
          FileLog.e(paramDrawable);
          break;
          if ((localBitmapDrawable != this.currentThumb) || (this.currentThumbKey == null))
            continue;
          ImageLoader.getInstance().removeImage(this.currentThumbKey);
          this.currentThumbKey = null;
        }
      }
      if (Math.abs(f1 - f2) > 1.0E-005F)
      {
        paramCanvas.save();
        paramCanvas.clipRect(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        if (this.orientation % 360 != 0)
        {
          if (!this.centerRotation)
            break label1405;
          paramCanvas.rotate(this.orientation, this.imageW / 2, this.imageH / 2);
        }
        while (true)
        {
          if (m / f2 > this.imageW)
          {
            j = (int)(m / f2);
            paramDrawable = this.drawRegion;
            m = this.imageX;
            i1 = (j - this.imageW) / 2;
            i3 = this.imageY;
            i4 = this.imageX;
            paramDrawable.set(m - i1, i3, (j + this.imageW) / 2 + i4, this.imageY + this.imageH);
            if ((localBitmapDrawable instanceof AnimatedFileDrawable))
              ((AnimatedFileDrawable)localBitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
            if ((this.orientation % 360 != 90) && (this.orientation % 360 != 270))
              break label1505;
            j = (this.drawRegion.right - this.drawRegion.left) / 2;
            m = (this.drawRegion.bottom - this.drawRegion.top) / 2;
            i1 = (this.drawRegion.right + this.drawRegion.left) / 2;
            i3 = (this.drawRegion.top + this.drawRegion.bottom) / 2;
            localBitmapDrawable.setBounds(i1 - m, i3 - j, m + i1, j + i3);
            if (!this.isVisible);
          }
          try
          {
            localBitmapDrawable.setAlpha(paramInt);
            localBitmapDrawable.draw(paramCanvas);
            paramCanvas.restore();
            return;
            paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
            continue;
            j = (int)(j / f1);
            paramDrawable = this.drawRegion;
            m = this.imageX;
            i1 = this.imageY;
            i3 = (j - this.imageH) / 2;
            i4 = this.imageX;
            i5 = this.imageW;
            i6 = this.imageY;
            paramDrawable.set(m, i1 - i3, i4 + i5, (j + this.imageH) / 2 + i6);
            break label1221;
            localBitmapDrawable.setBounds(this.drawRegion);
          }
          catch (java.lang.Exception paramDrawable)
          {
            if (localBitmapDrawable != this.currentImage)
              break label1596;
          }
        }
        if (this.currentKey != null)
        {
          ImageLoader.getInstance().removeImage(this.currentKey);
          this.currentKey = null;
        }
        while (true)
        {
          setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
          FileLog.e(paramDrawable);
          break;
          if ((localBitmapDrawable != this.currentThumb) || (this.currentThumbKey == null))
            continue;
          ImageLoader.getInstance().removeImage(this.currentThumbKey);
          this.currentThumbKey = null;
        }
      }
      paramCanvas.save();
      if (this.orientation % 360 != 0)
      {
        if (!this.centerRotation)
          break label1893;
        paramCanvas.rotate(this.orientation, this.imageW / 2, this.imageH / 2);
      }
      while (true)
      {
        this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        if ((localBitmapDrawable instanceof AnimatedFileDrawable))
          ((AnimatedFileDrawable)localBitmapDrawable).setActualDrawRect(this.imageX, this.imageY, this.imageW, this.imageH);
        if ((this.orientation % 360 == 90) || (this.orientation % 360 == 270))
        {
          j = (this.drawRegion.right - this.drawRegion.left) / 2;
          m = (this.drawRegion.bottom - this.drawRegion.top) / 2;
          i1 = (this.drawRegion.right + this.drawRegion.left) / 2;
          i3 = (this.drawRegion.top + this.drawRegion.bottom) / 2;
          localBitmapDrawable.setBounds(i1 - m, i3 - j, m + i1, j + i3);
          if (!this.isVisible);
        }
        try
        {
          localBitmapDrawable.setAlpha(paramInt);
          localBitmapDrawable.draw(paramCanvas);
          paramCanvas.restore();
          return;
          paramCanvas.rotate(this.orientation, 0.0F, 0.0F);
          continue;
          localBitmapDrawable.setBounds(this.drawRegion);
        }
        catch (java.lang.Exception paramDrawable)
        {
          if (localBitmapDrawable != this.currentImage)
            break label1998;
        }
      }
      if (this.currentKey != null)
      {
        ImageLoader.getInstance().removeImage(this.currentKey);
        this.currentKey = null;
      }
      while (true)
      {
        setImage(this.currentImageLocation, this.currentHttpUrl, this.currentFilter, this.currentThumb, this.currentThumbLocation, this.currentThumbFilter, this.currentSize, this.currentExt, this.currentCacheOnly);
        FileLog.e(paramDrawable);
        break;
        if ((localBitmapDrawable != this.currentThumb) || (this.currentThumbKey == null))
          continue;
        ImageLoader.getInstance().removeImage(this.currentThumbKey);
        this.currentThumbKey = null;
      }
      this.drawRegion.set(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      paramDrawable.setBounds(this.drawRegion);
    }
    while (!this.isVisible);
    try
    {
      label533: label545: label562: label696: label732: label1006: paramDrawable.setAlpha(paramInt);
      label769: label1221: label1505: label1893: paramDrawable.draw(paramCanvas);
      label1596: label1998: return;
    }
    catch (java.lang.Exception paramCanvas)
    {
      FileLog.e(paramCanvas);
    }
  }

  private void recycleBitmap(String paramString, boolean paramBoolean)
  {
    String str;
    Drawable localDrawable;
    if (paramBoolean)
    {
      str = this.currentThumbKey;
      localDrawable = this.currentThumb;
      if ((str != null) && ((paramString == null) || (!paramString.equals(str))) && (localDrawable != null))
      {
        if (!(localDrawable instanceof AnimatedFileDrawable))
          break label85;
        ((AnimatedFileDrawable)localDrawable).recycle();
      }
    }
    while (true)
    {
      if (!paramBoolean)
        break label133;
      this.currentThumb = null;
      this.currentThumbKey = null;
      return;
      str = this.currentKey;
      localDrawable = this.currentImage;
      break;
      label85: if (!(localDrawable instanceof BitmapDrawable))
        continue;
      paramString = ((BitmapDrawable)localDrawable).getBitmap();
      boolean bool = ImageLoader.getInstance().decrementUseCount(str);
      if ((ImageLoader.getInstance().isInCache(str)) || (!bool))
        continue;
      paramString.recycle();
    }
    label133: this.currentImage = null;
    this.currentKey = null;
  }

  public void cancelLoadImage()
  {
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    this.canceledLoading = true;
  }

  public void clearImage()
  {
    recycleBitmap(null, false);
    recycleBitmap(null, true);
    if (this.needsQualityThumb)
    {
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
      ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    String str;
    if (paramInt == NotificationCenter.messageThumbGenerated)
    {
      str = (String)paramArrayOfObject[1];
      if ((this.currentThumbKey != null) && (this.currentThumbKey.equals(str)))
      {
        if (this.currentThumb == null)
          ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
        this.currentThumb = ((BitmapDrawable)paramArrayOfObject[0]);
        if ((this.roundRadius == 0) || (this.currentImage != null) || (!(this.currentThumb instanceof BitmapDrawable)) || ((this.currentThumb instanceof AnimatedFileDrawable)))
          break label157;
        this.bitmapShaderThumb = new BitmapShader(((BitmapDrawable)this.currentThumb).getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if ((this.staticThumb instanceof BitmapDrawable))
          this.staticThumb = null;
        if (this.parentView != null)
        {
          if (!this.invalidateAll)
            break label165;
          this.parentView.invalidate();
        }
      }
    }
    label157: label165: 
    do
    {
      do
      {
        do
        {
          return;
          this.bitmapShaderThumb = null;
          break;
          this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
          return;
        }
        while (paramInt != NotificationCenter.didReplacedPhotoInMemCache);
        str = (String)paramArrayOfObject[0];
        if ((this.currentKey != null) && (this.currentKey.equals(str)))
        {
          this.currentKey = ((String)paramArrayOfObject[1]);
          this.currentImageLocation = ((TLRPC.FileLocation)paramArrayOfObject[2]);
        }
        if ((this.currentThumbKey == null) || (!this.currentThumbKey.equals(str)))
          continue;
        this.currentThumbKey = ((String)paramArrayOfObject[1]);
        this.currentThumbLocation = ((TLRPC.FileLocation)paramArrayOfObject[2]);
      }
      while (this.setImageBackup == null);
      if ((this.currentKey == null) || (!this.currentKey.equals(str)))
        continue;
      this.currentKey = ((String)paramArrayOfObject[1]);
      this.currentImageLocation = ((TLRPC.FileLocation)paramArrayOfObject[2]);
    }
    while ((this.currentThumbKey == null) || (!this.currentThumbKey.equals(str)));
    this.currentThumbKey = ((String)paramArrayOfObject[1]);
    this.currentThumbLocation = ((TLRPC.FileLocation)paramArrayOfObject[2]);
  }

  public boolean draw(Canvas paramCanvas)
  {
    while (true)
    {
      int j;
      try
      {
        if ((!(this.currentImage instanceof AnimatedFileDrawable)) || (((AnimatedFileDrawable)this.currentImage).hasBitmap()))
          break label410;
        bool = true;
        if ((this.forcePreview) || (this.currentImage == null) || (bool))
          continue;
        localDrawable = this.currentImage;
        i = 0;
        if (localDrawable == null)
          break label359;
        if (this.crossfadeAlpha == 0)
          break label317;
        if ((!this.crossfadeWithThumb) || (!bool))
          continue;
        drawDrawable(paramCanvas, localDrawable, (int)(this.overrideAlpha * 255.0F), this.bitmapShaderThumb);
        if ((!bool) || (!this.crossfadeWithThumb))
          break label416;
        bool = true;
        checkAlphaAnimation(bool);
        return true;
        if (!(this.staticThumb instanceof BitmapDrawable))
          continue;
        localDrawable = this.staticThumb;
        i = 1;
        continue;
        if (this.currentThumb == null)
          break label402;
        localDrawable = this.currentThumb;
        i = 1;
        continue;
        if ((!this.crossfadeWithThumb) || (this.currentAlpha == 1.0F))
          continue;
        if (localDrawable != this.currentImage)
          break label283;
        if (this.staticThumb != null)
        {
          localObject = this.staticThumb;
          if (localObject == null)
            continue;
          drawDrawable(paramCanvas, (Drawable)localObject, (int)(this.overrideAlpha * 255.0F), this.bitmapShaderThumb);
          j = (int)(this.overrideAlpha * this.currentAlpha * 255.0F);
          if (i == 0)
            break label308;
          localObject = this.bitmapShaderThumb;
          drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject);
          continue;
        }
      }
      catch (java.lang.Exception paramCanvas)
      {
        FileLog.e(paramCanvas);
        return false;
      }
      if (this.currentThumb != null)
      {
        localObject = this.currentThumb;
        continue;
        if ((localDrawable == this.currentThumb) && (this.staticThumb != null))
        {
          localObject = this.staticThumb;
          continue;
          localObject = this.bitmapShader;
          continue;
          j = (int)(this.overrideAlpha * 255.0F);
          if (i != 0);
          for (localObject = this.bitmapShaderThumb; ; localObject = this.bitmapShader)
          {
            drawDrawable(paramCanvas, localDrawable, j, (BitmapShader)localObject);
            break;
          }
          if (this.staticThumb != null)
          {
            drawDrawable(paramCanvas, this.staticThumb, 255, null);
            checkAlphaAnimation(bool);
            return true;
          }
          checkAlphaAnimation(bool);
          continue;
        }
      }
      label283: label308: label317: label359: Object localObject = null;
      continue;
      label402: int i = 0;
      Drawable localDrawable = null;
      continue;
      label410: boolean bool = false;
      continue;
      label416: bool = false;
    }
  }

  public int getAnimatedOrientation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
      return ((AnimatedFileDrawable)this.currentImage).getOrientation();
    if ((this.staticThumb instanceof AnimatedFileDrawable))
      return ((AnimatedFileDrawable)this.staticThumb).getOrientation();
    return 0;
  }

  public AnimatedFileDrawable getAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
      return (AnimatedFileDrawable)this.currentImage;
    return null;
  }

  public Bitmap getBitmap()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
      return ((AnimatedFileDrawable)this.currentImage).getAnimatedBitmap();
    if ((this.staticThumb instanceof AnimatedFileDrawable))
      return ((AnimatedFileDrawable)this.staticThumb).getAnimatedBitmap();
    if ((this.currentImage instanceof BitmapDrawable))
      return ((BitmapDrawable)this.currentImage).getBitmap();
    if ((this.currentThumb instanceof BitmapDrawable))
      return ((BitmapDrawable)this.currentThumb).getBitmap();
    if ((this.staticThumb instanceof BitmapDrawable))
      return ((BitmapDrawable)this.staticThumb).getBitmap();
    return null;
  }

  public int getBitmapHeight()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
        return this.currentImage.getIntrinsicHeight();
      return this.currentImage.getIntrinsicWidth();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
        return this.staticThumb.getIntrinsicHeight();
      return this.staticThumb.getIntrinsicWidth();
    }
    Bitmap localBitmap = getBitmap();
    if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
      return localBitmap.getHeight();
    return localBitmap.getWidth();
  }

  public int getBitmapWidth()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
        return this.currentImage.getIntrinsicWidth();
      return this.currentImage.getIntrinsicHeight();
    }
    if ((this.staticThumb instanceof AnimatedFileDrawable))
    {
      if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
        return this.staticThumb.getIntrinsicWidth();
      return this.staticThumb.getIntrinsicHeight();
    }
    Bitmap localBitmap = getBitmap();
    if ((this.orientation % 360 == 0) || (this.orientation % 360 == 180))
      return localBitmap.getWidth();
    return localBitmap.getHeight();
  }

  public boolean getCacheOnly()
  {
    return this.currentCacheOnly;
  }

  public Rect getDrawRegion()
  {
    return this.drawRegion;
  }

  public String getExt()
  {
    return this.currentExt;
  }

  public String getFilter()
  {
    return this.currentFilter;
  }

  public String getHttpImageLocation()
  {
    return this.currentHttpUrl;
  }

  public int getImageHeight()
  {
    return this.imageH;
  }

  public TLObject getImageLocation()
  {
    return this.currentImageLocation;
  }

  public int getImageWidth()
  {
    return this.imageW;
  }

  public int getImageX()
  {
    return this.imageX;
  }

  public int getImageX2()
  {
    return this.imageX + this.imageW;
  }

  public int getImageY()
  {
    return this.imageY;
  }

  public int getImageY2()
  {
    return this.imageY + this.imageH;
  }

  public String getKey()
  {
    return this.currentKey;
  }

  public int getOrientation()
  {
    return this.orientation;
  }

  public MessageObject getParentMessageObject()
  {
    return this.parentMessageObject;
  }

  public boolean getPressed()
  {
    return this.isPressed;
  }

  public int getRoundRadius()
  {
    return this.roundRadius;
  }

  public int getSize()
  {
    return this.currentSize;
  }

  public Drawable getStaticThumb()
  {
    return this.staticThumb;
  }

  protected Integer getTag(boolean paramBoolean)
  {
    if (paramBoolean)
      return this.thumbTag;
    return this.tag;
  }

  public String getThumbFilter()
  {
    return this.currentThumbFilter;
  }

  public String getThumbKey()
  {
    return this.currentThumbKey;
  }

  public TLRPC.FileLocation getThumbLocation()
  {
    return this.currentThumbLocation;
  }

  public boolean getVisible()
  {
    return this.isVisible;
  }

  public boolean hasBitmapImage()
  {
    return (this.currentImage != null) || (this.currentThumb != null) || (this.staticThumb != null);
  }

  public boolean hasImage()
  {
    return (this.currentImage != null) || (this.currentThumb != null) || (this.currentKey != null) || (this.currentHttpUrl != null) || (this.staticThumb != null);
  }

  public boolean isAllowStartAnimation()
  {
    return this.allowStartAnimation;
  }

  public boolean isAnimationRunning()
  {
    return ((this.currentImage instanceof AnimatedFileDrawable)) && (((AnimatedFileDrawable)this.currentImage).isRunning());
  }

  public boolean isForcePreview()
  {
    return this.forcePreview;
  }

  public boolean isInsideImage(float paramFloat1, float paramFloat2)
  {
    return (paramFloat1 >= this.imageX) && (paramFloat1 <= this.imageX + this.imageW) && (paramFloat2 >= this.imageY) && (paramFloat2 <= this.imageY + this.imageH);
  }

  public boolean isNeedsQualityThumb()
  {
    return this.needsQualityThumb;
  }

  public boolean isShouldGenerateQualityThumb()
  {
    return this.shouldGenerateQualityThumb;
  }

  public boolean onAttachedToWindow()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    if ((this.setImageBackup != null) && ((this.setImageBackup.fileLocation != null) || (this.setImageBackup.httpUrl != null) || (this.setImageBackup.thumbLocation != null) || (this.setImageBackup.thumb != null)))
    {
      setImage(this.setImageBackup.fileLocation, this.setImageBackup.httpUrl, this.setImageBackup.filter, this.setImageBackup.thumb, this.setImageBackup.thumbLocation, this.setImageBackup.thumbFilter, this.setImageBackup.size, this.setImageBackup.ext, this.setImageBackup.cacheOnly);
      return true;
    }
    return false;
  }

  public void onDetachedFromWindow()
  {
    if ((this.currentImageLocation != null) || (this.currentHttpUrl != null) || (this.currentThumbLocation != null) || (this.staticThumb != null))
    {
      if (this.setImageBackup == null)
        this.setImageBackup = new SetImageBackup(null);
      this.setImageBackup.fileLocation = this.currentImageLocation;
      this.setImageBackup.httpUrl = this.currentHttpUrl;
      this.setImageBackup.filter = this.currentFilter;
      this.setImageBackup.thumb = this.staticThumb;
      this.setImageBackup.thumbLocation = this.currentThumbLocation;
      this.setImageBackup.thumbFilter = this.currentThumbFilter;
      this.setImageBackup.size = this.currentSize;
      this.setImageBackup.ext = this.currentExt;
      this.setImageBackup.cacheOnly = this.currentCacheOnly;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.didReplacedPhotoInMemCache);
    clearImage();
  }

  public void setAllowStartAnimation(boolean paramBoolean)
  {
    this.allowStartAnimation = paramBoolean;
  }

  public void setAlpha(float paramFloat)
  {
    this.overrideAlpha = paramFloat;
  }

  public void setAspectFit(boolean paramBoolean)
  {
    this.isAspectFit = paramBoolean;
  }

  public void setColorFilter(ColorFilter paramColorFilter)
  {
    this.colorFilter = paramColorFilter;
  }

  public void setCrossfadeAlpha(byte paramByte)
  {
    this.crossfadeAlpha = paramByte;
  }

  public void setDelegate(ImageReceiverDelegate paramImageReceiverDelegate)
  {
    this.delegate = paramImageReceiverDelegate;
  }

  public void setForcePreview(boolean paramBoolean)
  {
    this.forcePreview = paramBoolean;
  }

  public void setImage(String paramString1, String paramString2, Drawable paramDrawable, String paramString3, int paramInt)
  {
    setImage(null, paramString1, paramString2, paramDrawable, null, null, paramInt, paramString3, true);
  }

  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, int paramInt, String paramString2, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, paramInt, paramString2, paramBoolean);
  }

  public void setImage(TLObject paramTLObject, String paramString1, Drawable paramDrawable, String paramString2, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, paramDrawable, null, null, 0, paramString2, paramBoolean);
  }

  public void setImage(TLObject paramTLObject, String paramString1, String paramString2, Drawable paramDrawable, TLRPC.FileLocation paramFileLocation, String paramString3, int paramInt, String paramString4, boolean paramBoolean)
  {
    if (this.setImageBackup != null)
    {
      this.setImageBackup.fileLocation = null;
      this.setImageBackup.httpUrl = null;
      this.setImageBackup.thumbLocation = null;
      this.setImageBackup.thumb = null;
    }
    label245: boolean bool1;
    if (((paramTLObject == null) && (paramString1 == null) && (paramFileLocation == null)) || ((paramTLObject != null) && (!(paramTLObject instanceof TLRPC.TL_fileLocation)) && (!(paramTLObject instanceof TLRPC.TL_fileEncryptedLocation)) && (!(paramTLObject instanceof TLRPC.TL_document)) && (!(paramTLObject instanceof TLRPC.TL_webDocument)) && (!(paramTLObject instanceof TLRPC.TL_documentEncrypted))))
    {
      recycleBitmap(null, false);
      recycleBitmap(null, true);
      this.currentKey = null;
      this.currentExt = paramString4;
      this.currentThumbKey = null;
      this.currentThumbFilter = null;
      this.currentImageLocation = null;
      this.currentHttpUrl = null;
      this.currentFilter = null;
      this.currentCacheOnly = false;
      this.staticThumb = paramDrawable;
      this.currentAlpha = 1.0F;
      this.currentThumbLocation = null;
      this.currentSize = 0;
      this.currentImage = null;
      this.bitmapShader = null;
      this.bitmapShaderThumb = null;
      ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
      if (this.parentView != null)
      {
        if (this.invalidateAll)
          this.parentView.invalidate();
      }
      else if (this.delegate != null)
      {
        paramTLObject = this.delegate;
        if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null))
          break label303;
        paramBoolean = true;
        if (this.currentImage != null)
          break label309;
      }
      label303: label309: for (bool1 = true; ; bool1 = false)
      {
        paramTLObject.didSetImage(this, paramBoolean, bool1);
        return;
        this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
        break;
        paramBoolean = false;
        break label245;
      }
    }
    TLRPC.FileLocation localFileLocation = paramFileLocation;
    if (!(paramFileLocation instanceof TLRPC.TL_fileLocation))
      localFileLocation = null;
    TLObject localTLObject;
    if (paramTLObject != null)
      if ((paramTLObject instanceof TLRPC.FileLocation))
      {
        paramFileLocation = (TLRPC.FileLocation)paramTLObject;
        paramFileLocation = paramFileLocation.volume_id + "_" + paramFileLocation.local_id;
        localTLObject = paramTLObject;
      }
    while (true)
    {
      label384: Object localObject = paramFileLocation;
      if (paramFileLocation != null)
      {
        localObject = paramFileLocation;
        if (paramString2 != null)
          localObject = paramFileLocation + "@" + paramString2;
      }
      label488: boolean bool2;
      if ((this.currentKey != null) && (localObject != null) && (this.currentKey.equals(localObject)))
      {
        if (this.delegate != null)
        {
          paramTLObject = this.delegate;
          if ((this.currentImage != null) || (this.currentThumb != null) || (this.staticThumb != null))
          {
            bool1 = true;
            if (this.currentImage != null)
              break label948;
            bool2 = true;
            label498: paramTLObject.didSetImage(this, bool1, bool2);
          }
        }
        else
        {
          if ((!this.canceledLoading) && (!this.forcePreview))
            break;
        }
      }
      else
      {
        paramTLObject = null;
        if (localFileLocation != null)
        {
          paramFileLocation = localFileLocation.volume_id + "_" + localFileLocation.local_id;
          paramTLObject = paramFileLocation;
          if (paramString3 != null)
            paramTLObject = paramFileLocation + "@" + paramString3;
        }
        recycleBitmap((String)localObject, false);
        recycleBitmap(paramTLObject, true);
        this.currentThumbKey = paramTLObject;
        this.currentKey = ((String)localObject);
        this.currentExt = paramString4;
        this.currentImageLocation = localTLObject;
        this.currentHttpUrl = paramString1;
        this.currentFilter = paramString2;
        this.currentThumbFilter = paramString3;
        this.currentSize = paramInt;
        this.currentCacheOnly = paramBoolean;
        this.currentThumbLocation = localFileLocation;
        this.staticThumb = paramDrawable;
        this.bitmapShader = null;
        this.bitmapShaderThumb = null;
        this.currentAlpha = 1.0F;
        if (this.delegate != null)
        {
          paramTLObject = this.delegate;
          if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null))
            break label954;
          paramBoolean = true;
          label726: if (this.currentImage != null)
            break label960;
        }
      }
      label948: label954: label960: for (bool1 = true; ; bool1 = false)
      {
        paramTLObject.didSetImage(this, paramBoolean, bool1);
        ImageLoader.getInstance().loadImageForImageReceiver(this);
        if (this.parentView == null)
          break;
        if (!this.invalidateAll)
          break label966;
        this.parentView.invalidate();
        return;
        if ((paramTLObject instanceof TLRPC.TL_webDocument))
        {
          paramFileLocation = Utilities.MD5(((TLRPC.TL_webDocument)paramTLObject).url);
          localTLObject = paramTLObject;
          break label384;
        }
        paramFileLocation = (TLRPC.Document)paramTLObject;
        if (paramFileLocation.dc_id != 0)
        {
          if (paramFileLocation.version == 0)
          {
            paramFileLocation = paramFileLocation.dc_id + "_" + paramFileLocation.id;
            localTLObject = paramTLObject;
            break label384;
          }
          paramFileLocation = paramFileLocation.dc_id + "_" + paramFileLocation.id + "_" + paramFileLocation.version;
          localTLObject = paramTLObject;
          break label384;
        }
        localTLObject = null;
        paramFileLocation = null;
        break label384;
        if (paramString1 == null)
          break label1000;
        paramFileLocation = Utilities.MD5(paramString1);
        localTLObject = paramTLObject;
        break label384;
        bool1 = false;
        break label488;
        bool2 = false;
        break label498;
        paramBoolean = false;
        break label726;
      }
      label966: this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      return;
      label1000: paramFileLocation = null;
      localTLObject = paramTLObject;
    }
  }

  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, int paramInt, String paramString3, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, paramInt, paramString3, paramBoolean);
  }

  public void setImage(TLObject paramTLObject, String paramString1, TLRPC.FileLocation paramFileLocation, String paramString2, String paramString3, boolean paramBoolean)
  {
    setImage(paramTLObject, null, paramString1, null, paramFileLocation, paramString2, 0, paramString3, paramBoolean);
  }

  public void setImageBitmap(Bitmap paramBitmap)
  {
    if (paramBitmap != null);
    for (paramBitmap = new BitmapDrawable(null, paramBitmap); ; paramBitmap = null)
    {
      setImageBitmap(paramBitmap);
      return;
    }
  }

  public void setImageBitmap(Drawable paramDrawable)
  {
    boolean bool = false;
    ImageLoader.getInstance().cancelLoadingForImageReceiver(this, 0);
    recycleBitmap(null, false);
    recycleBitmap(null, true);
    this.staticThumb = paramDrawable;
    this.currentThumbLocation = null;
    this.currentKey = null;
    this.currentExt = null;
    this.currentThumbKey = null;
    this.currentImage = null;
    this.currentThumbFilter = null;
    this.currentImageLocation = null;
    this.currentHttpUrl = null;
    this.currentFilter = null;
    this.currentSize = 0;
    this.currentCacheOnly = false;
    this.bitmapShader = null;
    this.bitmapShaderThumb = null;
    if (this.setImageBackup != null)
    {
      this.setImageBackup.fileLocation = null;
      this.setImageBackup.httpUrl = null;
      this.setImageBackup.thumbLocation = null;
      this.setImageBackup.thumb = null;
    }
    this.currentAlpha = 1.0F;
    if (this.delegate != null)
    {
      paramDrawable = this.delegate;
      if ((this.currentThumb != null) || (this.staticThumb != null))
        bool = true;
      paramDrawable.didSetImage(this, bool, true);
    }
    if (this.parentView != null)
    {
      if (this.invalidateAll)
        this.parentView.invalidate();
    }
    else
      return;
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
  }

  protected boolean setImageBitmapByKey(BitmapDrawable paramBitmapDrawable, String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool = false;
    if ((paramBitmapDrawable == null) || (paramString == null));
    while (true)
    {
      return false;
      if (!paramBoolean1)
      {
        if ((this.currentKey == null) || (!paramString.equals(this.currentKey)))
          continue;
        if (!(paramBitmapDrawable instanceof AnimatedFileDrawable))
          ImageLoader.getInstance().incrementUseCount(this.currentKey);
        this.currentImage = paramBitmapDrawable;
        if ((this.roundRadius != 0) && ((paramBitmapDrawable instanceof BitmapDrawable)))
          if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
          {
            ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
            label89: if ((paramBoolean2) || (this.forcePreview))
              break label307;
            if (((this.currentThumb == null) && (this.staticThumb == null)) || (this.currentAlpha == 1.0F))
            {
              this.currentAlpha = 0.0F;
              this.lastUpdateAlphaTime = System.currentTimeMillis();
              if ((this.currentThumb == null) && (this.staticThumb == null))
                break label302;
              paramBoolean1 = true;
              label152: this.crossfadeWithThumb = paramBoolean1;
            }
            label157: if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
            {
              paramBitmapDrawable = (AnimatedFileDrawable)paramBitmapDrawable;
              paramBitmapDrawable.setParentView(this.parentView);
              if (this.allowStartAnimation)
                paramBitmapDrawable.start();
            }
            if (this.parentView != null)
            {
              if (!this.invalidateAll)
                break label315;
              this.parentView.invalidate();
            }
            label209: if (this.delegate == null)
              break;
            paramBitmapDrawable = this.delegate;
            if ((this.currentImage == null) && (this.currentThumb == null) && (this.staticThumb == null))
              break label628;
          }
      }
    }
    label302: label307: label315: label467: label628: for (paramBoolean1 = true; ; paramBoolean1 = false)
    {
      paramBoolean2 = bool;
      if (this.currentImage == null)
        paramBoolean2 = true;
      paramBitmapDrawable.didSetImage(this, paramBoolean1, paramBoolean2);
      return true;
      this.bitmapShader = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      break label89;
      this.bitmapShader = null;
      break label89;
      paramBoolean1 = false;
      break label152;
      this.currentAlpha = 1.0F;
      break label157;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label209;
      if ((this.currentThumb != null) || ((this.currentImage != null) && ((!(this.currentImage instanceof AnimatedFileDrawable)) || (((AnimatedFileDrawable)this.currentImage).hasBitmap())) && (!this.forcePreview)))
        break label209;
      if ((this.currentThumbKey == null) || (!paramString.equals(this.currentThumbKey)))
        break;
      ImageLoader.getInstance().incrementUseCount(this.currentThumbKey);
      this.currentThumb = paramBitmapDrawable;
      if ((this.roundRadius != 0) && (this.currentImage == null) && ((paramBitmapDrawable instanceof BitmapDrawable)))
        if ((paramBitmapDrawable instanceof AnimatedFileDrawable))
        {
          ((AnimatedFileDrawable)paramBitmapDrawable).setRoundRadius(this.roundRadius);
          if ((paramBoolean2) || (this.crossfadeAlpha == 2))
            break label584;
          this.currentAlpha = 0.0F;
          this.lastUpdateAlphaTime = System.currentTimeMillis();
          if ((this.staticThumb == null) || (this.currentKey != null))
            break label579;
          paramBoolean1 = true;
          label508: this.crossfadeWithThumb = paramBoolean1;
        }
      while (true)
      {
        if (((this.staticThumb instanceof BitmapDrawable)) || (this.parentView == null))
          break label590;
        if (!this.invalidateAll)
          break label592;
        this.parentView.invalidate();
        break;
        this.bitmapShaderThumb = new BitmapShader(paramBitmapDrawable.getBitmap(), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        break label467;
        this.bitmapShaderThumb = null;
        break label467;
        paramBoolean1 = false;
        break label508;
        this.currentAlpha = 1.0F;
      }
      break label209;
      this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
      break label209;
    }
  }

  public void setImageCoords(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.imageX = paramInt1;
    this.imageY = paramInt2;
    this.imageW = paramInt3;
    this.imageH = paramInt4;
  }

  public void setImageY(int paramInt)
  {
    this.imageY = paramInt;
  }

  public void setInvalidateAll(boolean paramBoolean)
  {
    this.invalidateAll = paramBoolean;
  }

  public void setNeedsQualityThumb(boolean paramBoolean)
  {
    this.needsQualityThumb = paramBoolean;
    if (this.needsQualityThumb)
    {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.messageThumbGenerated);
      return;
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.messageThumbGenerated);
  }

  public void setOrientation(int paramInt, boolean paramBoolean)
  {
    int i;
    while (true)
    {
      i = paramInt;
      if (paramInt >= 0)
        break;
      paramInt += 360;
    }
    while (i > 360)
      i -= 360;
    this.orientation = i;
    this.centerRotation = paramBoolean;
  }

  public void setParentMessageObject(MessageObject paramMessageObject)
  {
    this.parentMessageObject = paramMessageObject;
  }

  public void setParentView(View paramView)
  {
    this.parentView = paramView;
    if ((this.currentImage instanceof AnimatedFileDrawable))
      ((AnimatedFileDrawable)this.currentImage).setParentView(this.parentView);
  }

  public void setPressed(boolean paramBoolean)
  {
    this.isPressed = paramBoolean;
  }

  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
  }

  public void setShouldGenerateQualityThumb(boolean paramBoolean)
  {
    this.shouldGenerateQualityThumb = paramBoolean;
  }

  protected void setTag(Integer paramInteger, boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.thumbTag = paramInteger;
      return;
    }
    this.tag = paramInteger;
  }

  public void setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.isVisible == paramBoolean1);
    do
    {
      return;
      this.isVisible = paramBoolean1;
    }
    while ((!paramBoolean2) || (this.parentView == null));
    if (this.invalidateAll)
    {
      this.parentView.invalidate();
      return;
    }
    this.parentView.invalidate(this.imageX, this.imageY, this.imageX + this.imageW, this.imageY + this.imageH);
  }

  public void startAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
      ((AnimatedFileDrawable)this.currentImage).start();
  }

  public void stopAnimation()
  {
    if ((this.currentImage instanceof AnimatedFileDrawable))
      ((AnimatedFileDrawable)this.currentImage).stop();
  }

  public static abstract interface ImageReceiverDelegate
  {
    public abstract void didSetImage(ImageReceiver paramImageReceiver, boolean paramBoolean1, boolean paramBoolean2);
  }

  private class SetImageBackup
  {
    public boolean cacheOnly;
    public String ext;
    public TLObject fileLocation;
    public String filter;
    public String httpUrl;
    public int size;
    public Drawable thumb;
    public String thumbFilter;
    public TLRPC.FileLocation thumbLocation;

    private SetImageBackup()
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.ImageReceiver
 * JD-Core Version:    0.6.0
 */