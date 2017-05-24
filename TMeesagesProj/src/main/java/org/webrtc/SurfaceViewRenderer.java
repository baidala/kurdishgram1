package org.webrtc;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Point;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.MeasureSpec;
import java.util.concurrent.CountDownLatch;

public class SurfaceViewRenderer extends SurfaceView
  implements SurfaceHolder.Callback, VideoRenderer.Callbacks
{
  private static final String TAG = "SurfaceViewRenderer";
  private Point desiredLayoutSize = new Point();
  private RendererCommon.GlDrawer drawer;
  private EglBase eglBase;
  private long firstFrameTimeNs;
  private int frameHeight;
  private final Object frameLock = new Object();
  private int frameRotation;
  private int frameWidth;
  private int framesDropped;
  private int framesReceived;
  private int framesRendered;
  private final Object handlerLock = new Object();
  private boolean isSurfaceCreated;
  private final Object layoutLock = new Object();
  private final Point layoutSize = new Point();
  private final Runnable makeBlackRunnable = new Runnable()
  {
    public void run()
    {
      SurfaceViewRenderer.this.makeBlack();
    }
  };
  private boolean mirror;
  private VideoRenderer.I420Frame pendingFrame;
  private final Runnable renderFrameRunnable = new Runnable()
  {
    public void run()
    {
      SurfaceViewRenderer.this.renderFrameOnRenderThread();
    }
  };
  private HandlerThread renderThread;
  private Handler renderThreadHandler;
  private long renderTimeNs;
  private RendererCommon.RendererEvents rendererEvents;
  private RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_BALANCED;
  private final Object statisticsLock = new Object();
  private final Point surfaceSize = new Point();
  private int[] yuvTextures = null;
  private final RendererCommon.YuvUploader yuvUploader = new RendererCommon.YuvUploader();

  public SurfaceViewRenderer(Context paramContext)
  {
    super(paramContext);
    getHolder().addCallback(this);
  }

  public SurfaceViewRenderer(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    getHolder().addCallback(this);
  }

  private boolean checkConsistentLayout()
  {
    if (Thread.currentThread() != this.renderThread)
      throw new IllegalStateException(getResourceName() + "Wrong thread.");
    while (true)
    {
      synchronized (this.layoutLock)
      {
        if ((this.layoutSize.equals(this.desiredLayoutSize)) && (this.surfaceSize.equals(this.layoutSize)))
        {
          i = 1;
          return i;
        }
      }
      int i = 0;
    }
  }

  private float frameAspectRatio()
  {
    while (true)
    {
      synchronized (this.layoutLock)
      {
        if ((this.frameWidth == 0) || (this.frameHeight == 0))
          return 0.0F;
        if (this.frameRotation % 180 == 0)
        {
          f = this.frameWidth / this.frameHeight;
          return f;
        }
      }
      float f = this.frameHeight;
      int i = this.frameWidth;
      f /= i;
    }
  }

  private Point getDesiredLayoutSize(int paramInt1, int paramInt2)
  {
    synchronized (this.layoutLock)
    {
      int i = getDefaultSize(2147483647, paramInt1);
      int j = getDefaultSize(2147483647, paramInt2);
      Point localPoint = RendererCommon.getDisplaySize(this.scalingType, frameAspectRatio(), i, j);
      if (View.MeasureSpec.getMode(paramInt1) == 1073741824)
        localPoint.x = i;
      if (View.MeasureSpec.getMode(paramInt2) == 1073741824)
        localPoint.y = j;
      return localPoint;
    }
  }

  private String getResourceName()
  {
    try
    {
      String str = getResources().getResourceEntryName(getId()) + ": ";
      return str;
    }
    catch (Resources.NotFoundException localNotFoundException)
    {
    }
    return "";
  }

  private void logStatistics()
  {
    synchronized (this.statisticsLock)
    {
      Logging.d("SurfaceViewRenderer", getResourceName() + "Frames received: " + this.framesReceived + ". Dropped: " + this.framesDropped + ". Rendered: " + this.framesRendered);
      if ((this.framesReceived > 0) && (this.framesRendered > 0))
      {
        long l = System.nanoTime() - this.firstFrameTimeNs;
        Logging.d("SurfaceViewRenderer", getResourceName() + "Duration: " + (int)(l / 1000000.0D) + " ms. FPS: " + this.framesRendered * 1000000000.0D / l);
        Logging.d("SurfaceViewRenderer", getResourceName() + "Average render time: " + (int)(this.renderTimeNs / (this.framesRendered * 1000)) + " us.");
      }
      return;
    }
  }

  private void makeBlack()
  {
    if (Thread.currentThread() != this.renderThread)
      throw new IllegalStateException(getResourceName() + "Wrong thread.");
    if ((this.eglBase != null) && (this.eglBase.hasSurface()))
    {
      GLES20.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
      GLES20.glClear(16384);
      this.eglBase.swapBuffers();
    }
  }

  private void renderFrameOnRenderThread()
  {
    if (Thread.currentThread() != this.renderThread)
      throw new IllegalStateException(getResourceName() + "Wrong thread.");
    synchronized (this.frameLock)
    {
      if (this.pendingFrame == null)
        return;
      VideoRenderer.I420Frame localI420Frame1 = this.pendingFrame;
      this.pendingFrame = null;
      updateFrameDimensionsAndReportEvents(localI420Frame1);
      if ((this.eglBase == null) || (!this.eglBase.hasSurface()))
      {
        Logging.d("SurfaceViewRenderer", getResourceName() + "No surface to draw on");
        VideoRenderer.renderFrameDone(localI420Frame1);
        return;
      }
    }
    if (!checkConsistentLayout())
    {
      makeBlack();
      VideoRenderer.renderFrameDone(localI420Frame2);
      return;
    }
    long l;
    synchronized (this.layoutLock)
    {
      if ((this.eglBase.surfaceWidth() != this.surfaceSize.x) || (this.eglBase.surfaceHeight() != this.surfaceSize.y))
        makeBlack();
      l = System.nanoTime();
    }
    float[] arrayOfFloat;
    synchronized (this.layoutLock)
    {
      arrayOfFloat = RendererCommon.multiplyMatrices(RendererCommon.rotateTextureMatrix(localI420Frame2.samplingMatrix, localI420Frame2.rotationDegree), RendererCommon.getLayoutMatrix(this.mirror, frameAspectRatio(), this.layoutSize.x / this.layoutSize.y));
      GLES20.glClear(16384);
      if (!localI420Frame2.yuvFrame)
        break label551;
      if (this.yuvTextures == null)
      {
        this.yuvTextures = new int[3];
        int i = 0;
        while (i < 3)
        {
          this.yuvTextures[i] = GlUtil.generateTexture(3553);
          i += 1;
          continue;
          localObject1 = finally;
          throw localObject1;
        }
      }
    }
    this.yuvUploader.uploadYuvData(this.yuvTextures, localI420Frame3.width, localI420Frame3.height, localI420Frame3.yuvStrides, localI420Frame3.yuvPlanes);
    this.drawer.drawYuv(this.yuvTextures, arrayOfFloat, localI420Frame3.rotatedWidth(), localI420Frame3.rotatedHeight(), 0, 0, this.surfaceSize.x, this.surfaceSize.y);
    while (true)
    {
      this.eglBase.swapBuffers();
      VideoRenderer.renderFrameDone(localI420Frame3);
      synchronized (this.statisticsLock)
      {
        if (this.framesRendered == 0)
          this.firstFrameTimeNs = l;
      }
      synchronized (this.layoutLock)
      {
        Logging.d("SurfaceViewRenderer", getResourceName() + "Reporting first rendered frame.");
        if (this.rendererEvents != null)
          this.rendererEvents.onFirstFrameRendered();
        this.framesRendered += 1;
        this.renderTimeNs += System.nanoTime() - l;
        if (this.framesRendered % 300 == 0)
          logStatistics();
        monitorexit;
        return;
        localObject4 = finally;
        monitorexit;
        throw localObject4;
        label551: this.drawer.drawOes(???.textureId, arrayOfFloat, ???.rotatedWidth(), ???.rotatedHeight(), 0, 0, this.surfaceSize.x, this.surfaceSize.y);
      }
    }
  }

  private void runOnRenderThread(Runnable paramRunnable)
  {
    synchronized (this.handlerLock)
    {
      if (this.renderThreadHandler != null)
        this.renderThreadHandler.post(paramRunnable);
      return;
    }
  }

  private void updateFrameDimensionsAndReportEvents(VideoRenderer.I420Frame paramI420Frame)
  {
    synchronized (this.layoutLock)
    {
      if ((this.frameWidth != paramI420Frame.width) || (this.frameHeight != paramI420Frame.height) || (this.frameRotation != paramI420Frame.rotationDegree))
      {
        Logging.d("SurfaceViewRenderer", getResourceName() + "Reporting frame resolution changed to " + paramI420Frame.width + "x" + paramI420Frame.height + " with rotation " + paramI420Frame.rotationDegree);
        if (this.rendererEvents != null)
          this.rendererEvents.onFrameResolutionChanged(paramI420Frame.width, paramI420Frame.height, paramI420Frame.rotationDegree);
        this.frameWidth = paramI420Frame.width;
        this.frameHeight = paramI420Frame.height;
        this.frameRotation = paramI420Frame.rotationDegree;
        post(new Runnable()
        {
          public void run()
          {
            SurfaceViewRenderer.this.requestLayout();
          }
        });
      }
      return;
    }
  }

  public void init(EglBase.Context paramContext, RendererCommon.RendererEvents paramRendererEvents)
  {
    init(paramContext, paramRendererEvents, EglBase.CONFIG_PLAIN, new GlRectDrawer());
  }

  public void init(EglBase.Context paramContext, RendererCommon.RendererEvents paramRendererEvents, int[] paramArrayOfInt, RendererCommon.GlDrawer paramGlDrawer)
  {
    synchronized (this.handlerLock)
    {
      if (this.renderThreadHandler != null)
        throw new IllegalStateException(getResourceName() + "Already initialized");
    }
    Logging.d("SurfaceViewRenderer", getResourceName() + "Initializing.");
    this.rendererEvents = paramRendererEvents;
    this.drawer = paramGlDrawer;
    this.renderThread = new HandlerThread("SurfaceViewRenderer");
    this.renderThread.start();
    this.eglBase = EglBase.create(paramContext, paramArrayOfInt);
    this.renderThreadHandler = new Handler(this.renderThread.getLooper());
    monitorexit;
    tryCreateEglSurface();
  }

  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    synchronized (this.layoutLock)
    {
      this.layoutSize.x = (paramInt3 - paramInt1);
      this.layoutSize.y = (paramInt4 - paramInt2);
      runOnRenderThread(this.renderFrameRunnable);
      return;
    }
  }

  protected void onMeasure(int paramInt1, int paramInt2)
  {
    while (true)
    {
      synchronized (this.layoutLock)
      {
        if ((this.frameWidth != 0) && (this.frameHeight != 0))
          continue;
        super.onMeasure(paramInt1, paramInt2);
        return;
        this.desiredLayoutSize = getDesiredLayoutSize(paramInt1, paramInt2);
        if (this.desiredLayoutSize.x != getMeasuredWidth())
          break label144;
        if (this.desiredLayoutSize.y == getMeasuredHeight())
          continue;
        break label144;
        setMeasuredDimension(this.desiredLayoutSize.x, this.desiredLayoutSize.y);
        if (paramInt1 != 0)
        {
          synchronized (this.handlerLock)
          {
            if (this.renderThreadHandler == null)
              continue;
            this.renderThreadHandler.postAtFrontOfQueue(this.makeBlackRunnable);
            return;
          }
          paramInt1 = 0;
        }
      }
      return;
      label144: paramInt1 = 1;
    }
  }

  public void release()
  {
    CountDownLatch localCountDownLatch = new CountDownLatch(1);
    synchronized (this.handlerLock)
    {
      if (this.renderThreadHandler == null)
      {
        Logging.d("SurfaceViewRenderer", getResourceName() + "Already released");
        return;
      }
      this.renderThreadHandler.postAtFrontOfQueue(new Runnable(localCountDownLatch)
      {
        public void run()
        {
          SurfaceViewRenderer.this.drawer.release();
          SurfaceViewRenderer.access$502(SurfaceViewRenderer.this, null);
          if (SurfaceViewRenderer.this.yuvTextures != null)
          {
            GLES20.glDeleteTextures(3, SurfaceViewRenderer.this.yuvTextures, 0);
            SurfaceViewRenderer.access$602(SurfaceViewRenderer.this, null);
          }
          SurfaceViewRenderer.this.makeBlack();
          SurfaceViewRenderer.this.eglBase.release();
          SurfaceViewRenderer.access$302(SurfaceViewRenderer.this, null);
          this.val$eglCleanupBarrier.countDown();
        }
      });
      this.renderThreadHandler = null;
      ThreadUtils.awaitUninterruptibly(localCountDownLatch);
      this.renderThread.quit();
      synchronized (this.frameLock)
      {
        if (this.pendingFrame != null)
        {
          VideoRenderer.renderFrameDone(this.pendingFrame);
          this.pendingFrame = null;
        }
        ThreadUtils.joinUninterruptibly(this.renderThread);
        this.renderThread = null;
      }
    }
    synchronized (this.layoutLock)
    {
      this.frameWidth = 0;
      this.frameHeight = 0;
      this.frameRotation = 0;
      this.rendererEvents = null;
      resetStatistics();
      return;
      localObject2 = finally;
      throw localObject2;
      localObject3 = finally;
      throw localObject3;
    }
  }

  public void renderFrame(VideoRenderer.I420Frame paramI420Frame)
  {
    synchronized (this.statisticsLock)
    {
      this.framesReceived += 1;
    }
    synchronized (this.frameLock)
    {
      if (this.pendingFrame != null);
      synchronized (this.statisticsLock)
      {
        this.framesDropped += 1;
        VideoRenderer.renderFrameDone(this.pendingFrame);
        this.pendingFrame = paramI420Frame;
        this.renderThreadHandler.post(this.renderFrameRunnable);
        monitorexit;
        return;
        paramI420Frame = finally;
        monitorexit;
        throw paramI420Frame;
      }
    }
  }

  public void resetStatistics()
  {
    synchronized (this.statisticsLock)
    {
      this.framesReceived = 0;
      this.framesDropped = 0;
      this.framesRendered = 0;
      this.firstFrameTimeNs = 0L;
      this.renderTimeNs = 0L;
      return;
    }
  }

  public void setMirror(boolean paramBoolean)
  {
    synchronized (this.layoutLock)
    {
      this.mirror = paramBoolean;
      return;
    }
  }

  public void setScalingType(RendererCommon.ScalingType paramScalingType)
  {
    synchronized (this.layoutLock)
    {
      this.scalingType = paramScalingType;
      return;
    }
  }

  public void surfaceChanged(SurfaceHolder arg1, int paramInt1, int paramInt2, int paramInt3)
  {
    Logging.d("SurfaceViewRenderer", getResourceName() + "Surface changed: " + paramInt2 + "x" + paramInt3);
    synchronized (this.layoutLock)
    {
      this.surfaceSize.x = paramInt2;
      this.surfaceSize.y = paramInt3;
      runOnRenderThread(this.renderFrameRunnable);
      return;
    }
  }

  public void surfaceCreated(SurfaceHolder arg1)
  {
    Logging.d("SurfaceViewRenderer", getResourceName() + "Surface created.");
    synchronized (this.layoutLock)
    {
      this.isSurfaceCreated = true;
      tryCreateEglSurface();
      return;
    }
  }

  public void surfaceDestroyed(SurfaceHolder arg1)
  {
    Logging.d("SurfaceViewRenderer", getResourceName() + "Surface destroyed.");
    synchronized (this.layoutLock)
    {
      this.isSurfaceCreated = false;
      this.surfaceSize.x = 0;
      this.surfaceSize.y = 0;
      runOnRenderThread(new Runnable()
      {
        public void run()
        {
          if (SurfaceViewRenderer.this.eglBase != null)
          {
            SurfaceViewRenderer.this.eglBase.detachCurrent();
            SurfaceViewRenderer.this.eglBase.releaseSurface();
          }
        }
      });
      return;
    }
  }

  public void tryCreateEglSurface()
  {
    runOnRenderThread(new Runnable()
    {
      public void run()
      {
        synchronized (SurfaceViewRenderer.this.layoutLock)
        {
          if ((SurfaceViewRenderer.this.eglBase != null) && (SurfaceViewRenderer.this.isSurfaceCreated) && (!SurfaceViewRenderer.this.eglBase.hasSurface()))
          {
            SurfaceViewRenderer.this.eglBase.createSurface(SurfaceViewRenderer.this.getHolder().getSurface());
            SurfaceViewRenderer.this.eglBase.makeCurrent();
            GLES20.glPixelStorei(3317, 1);
          }
          return;
        }
      }
    });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.SurfaceViewRenderer
 * JD-Core Version:    0.6.0
 */