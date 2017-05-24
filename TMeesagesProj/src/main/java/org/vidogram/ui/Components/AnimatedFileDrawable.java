package org.vidogram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import java.io.File;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.DiscardPolicy;
import java.util.concurrent.TimeUnit;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLog;

public class AnimatedFileDrawable extends BitmapDrawable
  implements Animatable
{
  private static ScheduledThreadPoolExecutor executor;
  private static final Handler uiHandler = new Handler(Looper.getMainLooper());
  private RectF actualDrawRect = new RectF();
  private boolean applyTransformation;
  private Bitmap backgroundBitmap;
  private BitmapShader backgroundShader;
  private RectF bitmapRect = new RectF();
  private boolean decoderCreated;
  private boolean destroyWhenDone;
  private final Rect dstRect = new Rect();
  private int invalidateAfter = 50;
  private volatile boolean isRecycled;
  private volatile boolean isRunning;
  private long lastFrameDecodeTime;
  private long lastFrameTime;
  private int lastTimeStamp;
  private Runnable loadFrameRunnable = new Runnable()
  {
    public void run()
    {
      if (!AnimatedFileDrawable.this.isRecycled)
        if ((!AnimatedFileDrawable.this.decoderCreated) && (AnimatedFileDrawable.this.nativePtr == 0))
        {
          AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, AnimatedFileDrawable.access$1800(AnimatedFileDrawable.this.path.getAbsolutePath(), AnimatedFileDrawable.this.metaData));
          AnimatedFileDrawable.access$1602(AnimatedFileDrawable.this, true);
        }
      try
      {
        Bitmap localBitmap = AnimatedFileDrawable.this.backgroundBitmap;
        if (localBitmap == null);
        try
        {
          AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, Bitmap.createBitmap(AnimatedFileDrawable.this.metaData[0], AnimatedFileDrawable.this.metaData[1], Bitmap.Config.ARGB_8888));
          if ((AnimatedFileDrawable.this.backgroundShader == null) && (AnimatedFileDrawable.this.backgroundBitmap != null) && (AnimatedFileDrawable.this.roundRadius != 0))
            AnimatedFileDrawable.access$1002(AnimatedFileDrawable.this, new BitmapShader(AnimatedFileDrawable.this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
          if (AnimatedFileDrawable.this.backgroundBitmap != null)
          {
            AnimatedFileDrawable.access$2002(AnimatedFileDrawable.this, System.currentTimeMillis());
            AnimatedFileDrawable.access$2100(AnimatedFileDrawable.this.nativePtr, AnimatedFileDrawable.this.backgroundBitmap, AnimatedFileDrawable.this.metaData);
          }
          AndroidUtilities.runOnUIThread(AnimatedFileDrawable.this.uiRunnable);
          return;
        }
        catch (Throwable localThrowable1)
        {
          while (true)
            FileLog.e(localThrowable1);
        }
      }
      catch (Throwable localThrowable2)
      {
        while (true)
          FileLog.e(localThrowable2);
      }
    }
  };
  private Runnable loadFrameTask;
  protected final Runnable mInvalidateTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null)
        AnimatedFileDrawable.this.secondParentView.invalidate();
      do
        return;
      while (AnimatedFileDrawable.this.parentView == null);
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final Runnable mStartTask = new Runnable()
  {
    public void run()
    {
      if (AnimatedFileDrawable.this.secondParentView != null)
        AnimatedFileDrawable.this.secondParentView.invalidate();
      do
        return;
      while (AnimatedFileDrawable.this.parentView == null);
      AnimatedFileDrawable.this.parentView.invalidate();
    }
  };
  private final int[] metaData = new int[4];
  private volatile int nativePtr;
  private Bitmap nextRenderingBitmap;
  private BitmapShader nextRenderingShader;
  private View parentView = null;
  private File path;
  private boolean recycleWithSecond;
  private Bitmap renderingBitmap;
  private BitmapShader renderingShader;
  private int roundRadius;
  private RectF roundRect = new RectF();
  private float scaleX = 1.0F;
  private float scaleY = 1.0F;
  private View secondParentView = null;
  private Matrix shaderMatrix = new Matrix();
  private Runnable uiRunnable = new Runnable()
  {
    public void run()
    {
      if ((AnimatedFileDrawable.this.destroyWhenDone) && (AnimatedFileDrawable.this.nativePtr != 0))
      {
        AnimatedFileDrawable.access$400(AnimatedFileDrawable.this.nativePtr);
        AnimatedFileDrawable.access$302(AnimatedFileDrawable.this, 0);
      }
      if (AnimatedFileDrawable.this.nativePtr == 0)
      {
        if (AnimatedFileDrawable.this.renderingBitmap != null)
        {
          AnimatedFileDrawable.this.renderingBitmap.recycle();
          AnimatedFileDrawable.access$502(AnimatedFileDrawable.this, null);
        }
        if (AnimatedFileDrawable.this.backgroundBitmap != null)
        {
          AnimatedFileDrawable.this.backgroundBitmap.recycle();
          AnimatedFileDrawable.access$602(AnimatedFileDrawable.this, null);
        }
        return;
      }
      AnimatedFileDrawable.access$702(AnimatedFileDrawable.this, null);
      AnimatedFileDrawable.access$802(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundBitmap);
      AnimatedFileDrawable.access$902(AnimatedFileDrawable.this, AnimatedFileDrawable.this.backgroundShader);
      if (AnimatedFileDrawable.this.metaData[3] < AnimatedFileDrawable.this.lastTimeStamp)
        AnimatedFileDrawable.access$1202(AnimatedFileDrawable.this, 0);
      if (AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp != 0)
        AnimatedFileDrawable.access$1302(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3] - AnimatedFileDrawable.this.lastTimeStamp);
      AnimatedFileDrawable.access$1202(AnimatedFileDrawable.this, AnimatedFileDrawable.this.metaData[3]);
      if (AnimatedFileDrawable.this.secondParentView != null)
        AnimatedFileDrawable.this.secondParentView.invalidate();
      while (true)
      {
        AnimatedFileDrawable.this.scheduleNextGetFrame();
        return;
        if (AnimatedFileDrawable.this.parentView == null)
          continue;
        AnimatedFileDrawable.this.parentView.invalidate();
      }
    }
  };

  static
  {
    executor = new ScheduledThreadPoolExecutor(2, new ThreadPoolExecutor.DiscardPolicy());
  }

  public AnimatedFileDrawable(File paramFile, boolean paramBoolean)
  {
    this.path = paramFile;
    if (paramBoolean)
    {
      this.nativePtr = createDecoder(paramFile.getAbsolutePath(), this.metaData);
      this.decoderCreated = true;
    }
  }

  private static native int createDecoder(String paramString, int[] paramArrayOfInt);

  private static native void destroyDecoder(int paramInt);

  private static native int getVideoFrame(int paramInt, Bitmap paramBitmap, int[] paramArrayOfInt);

  protected static void runOnUiThread(Runnable paramRunnable)
  {
    if (Looper.myLooper() == uiHandler.getLooper())
    {
      paramRunnable.run();
      return;
    }
    uiHandler.post(paramRunnable);
  }

  private void scheduleNextGetFrame()
  {
    long l = 0L;
    if ((this.loadFrameTask != null) || ((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone) || (!this.isRunning))
      return;
    if (this.lastFrameDecodeTime != 0L)
      l = Math.min(this.invalidateAfter, Math.max(0L, this.invalidateAfter - (System.currentTimeMillis() - this.lastFrameDecodeTime)));
    ScheduledThreadPoolExecutor localScheduledThreadPoolExecutor = executor;
    Runnable localRunnable = this.loadFrameRunnable;
    this.loadFrameTask = localRunnable;
    localScheduledThreadPoolExecutor.schedule(localRunnable, l, TimeUnit.MILLISECONDS);
  }

  public void draw(Canvas paramCanvas)
  {
    if (((this.nativePtr == 0) && (this.decoderCreated)) || (this.destroyWhenDone))
      return;
    long l = System.currentTimeMillis();
    label52: float f;
    if (this.isRunning)
    {
      if ((this.renderingBitmap == null) && (this.nextRenderingBitmap == null))
        scheduleNextGetFrame();
    }
    else
    {
      if (this.renderingBitmap == null)
        break label535;
      if (this.applyTransformation)
      {
        i = this.renderingBitmap.getWidth();
        int i1 = this.renderingBitmap.getHeight();
        int i2;
        int k;
        if (this.metaData[2] != 90)
        {
          i2 = i1;
          k = i;
          if (this.metaData[2] != 270);
        }
        else
        {
          k = i1;
          i2 = i;
        }
        this.dstRect.set(getBounds());
        this.scaleX = (this.dstRect.width() / k);
        this.scaleY = (this.dstRect.height() / i2);
        this.applyTransformation = false;
      }
      if (this.roundRadius == 0)
        break label625;
      f = Math.max(this.scaleX, this.scaleY);
      if (this.renderingShader == null)
        this.renderingShader = new BitmapShader(this.backgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
      getPaint().setShader(this.renderingShader);
      this.roundRect.set(this.dstRect);
      this.shaderMatrix.reset();
      if (Math.abs(this.scaleX - this.scaleY) <= 1.0E-005F)
        break label573;
      if ((this.metaData[2] != 90) && (this.metaData[2] != 270))
        break label537;
      int m = (int)Math.floor(this.dstRect.height() / f);
      int i = (int)Math.floor(this.dstRect.width() / f);
      label320: this.bitmapRect.set((this.renderingBitmap.getWidth() - m) / 2, (this.renderingBitmap.getHeight() - i) / 2, m, i);
      AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.START);
    }
    while (true)
    {
      this.renderingShader.setLocalMatrix(this.shaderMatrix);
      paramCanvas.drawRoundRect(this.actualDrawRect, this.roundRadius, this.roundRadius, getPaint());
      label414: if (!this.isRunning)
        break label704;
      l = Math.max(1L, this.invalidateAfter - (l - this.lastFrameTime) - 17L);
      uiHandler.removeCallbacks(this.mInvalidateTask);
      uiHandler.postDelayed(this.mInvalidateTask, Math.min(l, this.invalidateAfter));
      return;
      if ((Math.abs(l - this.lastFrameTime) < this.invalidateAfter) || (this.nextRenderingBitmap == null))
        break label52;
      this.renderingBitmap = this.nextRenderingBitmap;
      this.renderingShader = this.nextRenderingShader;
      this.nextRenderingBitmap = null;
      this.nextRenderingShader = null;
      this.lastFrameTime = l;
      break label52;
      label535: break;
      label537: int n = (int)Math.floor(this.dstRect.width() / f);
      int j = (int)Math.floor(this.dstRect.height() / f);
      break label320;
      label573: this.bitmapRect.set(0.0F, 0.0F, this.renderingBitmap.getWidth(), this.renderingBitmap.getHeight());
      AndroidUtilities.setRectToRect(this.shaderMatrix, this.bitmapRect, this.roundRect, this.metaData[2], Matrix.ScaleToFit.FILL);
    }
    label625: paramCanvas.translate(this.dstRect.left, this.dstRect.top);
    if (this.metaData[2] == 90)
    {
      paramCanvas.rotate(90.0F);
      paramCanvas.translate(0.0F, -this.dstRect.width());
    }
    while (true)
    {
      paramCanvas.scale(this.scaleX, this.scaleY);
      paramCanvas.drawBitmap(this.renderingBitmap, 0.0F, 0.0F, getPaint());
      break label414;
      label704: break;
      if (this.metaData[2] == 180)
      {
        paramCanvas.rotate(180.0F);
        paramCanvas.translate(-this.dstRect.width(), -this.dstRect.height());
        continue;
      }
      if (this.metaData[2] != 270)
        continue;
      paramCanvas.rotate(270.0F);
      paramCanvas.translate(-this.dstRect.height(), 0.0F);
    }
  }

  protected void finalize()
  {
    try
    {
      recycle();
      return;
    }
    finally
    {
      super.finalize();
    }
    throw localObject;
  }

  public Bitmap getAnimatedBitmap()
  {
    if (this.renderingBitmap != null)
      return this.renderingBitmap;
    if (this.nextRenderingBitmap != null)
      return this.nextRenderingBitmap;
    return null;
  }

  public int getIntrinsicHeight()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270))
        return this.metaData[0];
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }

  public int getIntrinsicWidth()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270))
        return this.metaData[1];
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
  }

  public int getMinimumHeight()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270))
        return this.metaData[0];
      return this.metaData[1];
    }
    return AndroidUtilities.dp(100.0F);
  }

  public int getMinimumWidth()
  {
    if (this.decoderCreated)
    {
      if ((this.metaData[2] == 90) || (this.metaData[2] == 270))
        return this.metaData[1];
      return this.metaData[0];
    }
    return AndroidUtilities.dp(100.0F);
  }

  public int getOpacity()
  {
    return -2;
  }

  public int getOrientation()
  {
    return this.metaData[2];
  }

  public boolean hasBitmap()
  {
    return (this.nativePtr != 0) && ((this.renderingBitmap != null) || (this.nextRenderingBitmap != null));
  }

  public boolean isRunning()
  {
    return this.isRunning;
  }

  public AnimatedFileDrawable makeCopy()
  {
    AnimatedFileDrawable localAnimatedFileDrawable = new AnimatedFileDrawable(this.path, false);
    localAnimatedFileDrawable.metaData[0] = this.metaData[0];
    localAnimatedFileDrawable.metaData[1] = this.metaData[1];
    return localAnimatedFileDrawable;
  }

  protected void onBoundsChange(Rect paramRect)
  {
    super.onBoundsChange(paramRect);
    this.applyTransformation = true;
  }

  public void recycle()
  {
    if (this.secondParentView != null)
      this.recycleWithSecond = true;
    while (true)
    {
      return;
      this.isRunning = false;
      this.isRecycled = true;
      if (this.loadFrameTask != null)
        break;
      if (this.nativePtr != 0)
      {
        destroyDecoder(this.nativePtr);
        this.nativePtr = 0;
      }
      if (this.renderingBitmap != null)
      {
        this.renderingBitmap.recycle();
        this.renderingBitmap = null;
      }
      if (this.nextRenderingBitmap == null)
        continue;
      this.nextRenderingBitmap.recycle();
      this.nextRenderingBitmap = null;
      return;
    }
    this.destroyWhenDone = true;
  }

  public void setActualDrawRect(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.actualDrawRect.set(paramInt1, paramInt2, paramInt1 + paramInt3, paramInt2 + paramInt4);
  }

  public void setParentView(View paramView)
  {
    this.parentView = paramView;
  }

  public void setRoundRadius(int paramInt)
  {
    this.roundRadius = paramInt;
    getPaint().setFlags(1);
  }

  public void setSecondParentView(View paramView)
  {
    this.secondParentView = paramView;
    if ((paramView == null) && (this.recycleWithSecond))
      recycle();
  }

  public void start()
  {
    if (this.isRunning)
      return;
    this.isRunning = true;
    scheduleNextGetFrame();
    runOnUiThread(this.mStartTask);
  }

  public void stop()
  {
    this.isRunning = false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.AnimatedFileDrawable
 * JD-Core Version:    0.6.0
 */