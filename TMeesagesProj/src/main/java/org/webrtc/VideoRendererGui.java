package org.webrtc;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.graphics.Rect;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.GL10;

public class VideoRendererGui
  implements GLSurfaceView.Renderer
{
  private static final String TAG = "VideoRendererGui";
  private static Thread drawThread;
  private static EglBase.Context eglContext;
  private static Runnable eglContextReady;
  private static VideoRendererGui instance = null;
  private static Thread renderFrameThread;
  private boolean onSurfaceCreatedCalled;
  private int screenHeight;
  private int screenWidth;
  private GLSurfaceView surface;
  private final ArrayList<YuvImageRenderer> yuvImageRenderers;

  static
  {
    eglContextReady = null;
    eglContext = null;
  }

  private VideoRendererGui(GLSurfaceView paramGLSurfaceView)
  {
    this.surface = paramGLSurfaceView;
    paramGLSurfaceView.setPreserveEGLContextOnPause(true);
    paramGLSurfaceView.setEGLContextClientVersion(2);
    paramGLSurfaceView.setRenderer(this);
    paramGLSurfaceView.setRenderMode(0);
    this.yuvImageRenderers = new ArrayList();
  }

  public static YuvImageRenderer create(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType paramScalingType, boolean paramBoolean)
  {
    monitorenter;
    try
    {
      paramScalingType = create(paramInt1, paramInt2, paramInt3, paramInt4, paramScalingType, paramBoolean, new GlRectDrawer());
      monitorexit;
      return paramScalingType;
    }
    finally
    {
      paramScalingType = finally;
      monitorexit;
    }
    throw paramScalingType;
  }

  public static YuvImageRenderer create(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType arg4, boolean paramBoolean, RendererCommon.GlDrawer paramGlDrawer)
  {
    monitorenter;
    if ((paramInt1 < 0) || (paramInt1 > 100) || (paramInt2 < 0) || (paramInt2 > 100) || (paramInt3 < 0) || (paramInt3 > 100) || (paramInt4 < 0) || (paramInt4 > 100) || (paramInt1 + paramInt3 > 100) || (paramInt2 + paramInt4 > 100))
      try
      {
        throw new RuntimeException("Incorrect window parameters.");
      }
      finally
      {
        monitorexit;
      }
    if (instance == null)
      throw new RuntimeException("Attempt to create yuv renderer before setting GLSurfaceView");
    paramGlDrawer = new YuvImageRenderer(instance.surface, instance.yuvImageRenderers.size(), paramInt1, paramInt2, paramInt3, paramInt4, ???, paramBoolean, paramGlDrawer, null);
    synchronized (instance.yuvImageRenderers)
    {
      CountDownLatch localCountDownLatch;
      if (instance.onSurfaceCreatedCalled)
      {
        localCountDownLatch = new CountDownLatch(1);
        instance.surface.queueEvent(new Runnable(paramGlDrawer, localCountDownLatch)
        {
          public void run()
          {
            VideoRendererGui.YuvImageRenderer.access$300(this.val$yuvImageRenderer);
            this.val$yuvImageRenderer.setScreenSize(VideoRendererGui.instance.screenWidth, VideoRendererGui.instance.screenHeight);
            this.val$countDownLatch.countDown();
          }
        });
      }
      try
      {
        localCountDownLatch.await();
        instance.yuvImageRenderers.add(paramGlDrawer);
        monitorexit;
        return paramGlDrawer;
      }
      catch (java.lang.InterruptedException paramGlDrawer)
      {
        throw new RuntimeException(paramGlDrawer);
      }
    }
  }

  public static VideoRenderer createGui(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType paramScalingType, boolean paramBoolean)
  {
    return new VideoRenderer(create(paramInt1, paramInt2, paramInt3, paramInt4, paramScalingType, paramBoolean));
  }

  public static VideoRenderer.Callbacks createGuiRenderer(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType paramScalingType, boolean paramBoolean)
  {
    return create(paramInt1, paramInt2, paramInt3, paramInt4, paramScalingType, paramBoolean);
  }

  public static void dispose()
  {
    monitorenter;
    while (true)
    {
      try
      {
        ??? = instance;
        if (??? == null)
          return;
        Logging.d("VideoRendererGui", "VideoRendererGui.dispose");
        synchronized (instance.yuvImageRenderers)
        {
          Iterator localIterator = instance.yuvImageRenderers.iterator();
          if (localIterator.hasNext())
            ((YuvImageRenderer)localIterator.next()).release();
        }
      }
      finally
      {
        monitorexit;
      }
      instance.yuvImageRenderers.clear();
      monitorexit;
      renderFrameThread = null;
      drawThread = null;
      instance.surface = null;
      eglContext = null;
      eglContextReady = null;
      instance = null;
    }
  }

  public static EglBase.Context getEglBaseContext()
  {
    monitorenter;
    try
    {
      EglBase.Context localContext = eglContext;
      monitorexit;
      return localContext;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  private static void printStackTrace(Thread paramThread, String paramString)
  {
    if (paramThread != null)
    {
      paramThread = paramThread.getStackTrace();
      if (paramThread.length > 0)
      {
        Logging.d("VideoRendererGui", paramString + " stacks trace:");
        int j = paramThread.length;
        int i = 0;
        while (i < j)
        {
          Logging.d("VideoRendererGui", paramThread[i].toString());
          i += 1;
        }
      }
    }
  }

  public static void printStackTraces()
  {
    monitorenter;
    try
    {
      VideoRendererGui localVideoRendererGui = instance;
      if (localVideoRendererGui == null);
      while (true)
      {
        return;
        printStackTrace(renderFrameThread, "Render frame thread");
        printStackTrace(drawThread, "Draw thread");
      }
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  public static void remove(VideoRenderer.Callbacks paramCallbacks)
  {
    monitorenter;
    try
    {
      Logging.d("VideoRendererGui", "VideoRendererGui.remove");
      if (instance == null)
        throw new RuntimeException("Attempt to remove renderer before setting GLSurfaceView");
    }
    finally
    {
      monitorexit;
    }
    synchronized (instance.yuvImageRenderers)
    {
      int i = instance.yuvImageRenderers.indexOf(paramCallbacks);
      if (i == -1)
      {
        Logging.w("VideoRendererGui", "Couldn't remove renderer (not present in current list)");
        monitorexit;
        return;
      }
      ((YuvImageRenderer)instance.yuvImageRenderers.remove(i)).release();
    }
  }

  public static void reset(VideoRenderer.Callbacks paramCallbacks)
  {
    monitorenter;
    try
    {
      Logging.d("VideoRendererGui", "VideoRendererGui.reset");
      if (instance == null)
        throw new RuntimeException("Attempt to reset renderer before setting GLSurfaceView");
    }
    finally
    {
      monitorexit;
    }
    synchronized (instance.yuvImageRenderers)
    {
      Iterator localIterator = instance.yuvImageRenderers.iterator();
      while (localIterator.hasNext())
      {
        YuvImageRenderer localYuvImageRenderer = (YuvImageRenderer)localIterator.next();
        if (localYuvImageRenderer != paramCallbacks)
          continue;
        localYuvImageRenderer.reset();
      }
    }
    monitorexit;
    monitorexit;
  }

  public static void setRendererEvents(VideoRenderer.Callbacks paramCallbacks, RendererCommon.RendererEvents paramRendererEvents)
  {
    monitorenter;
    try
    {
      Logging.d("VideoRendererGui", "VideoRendererGui.setRendererEvents");
      if (instance == null)
        throw new RuntimeException("Attempt to set renderer events before setting GLSurfaceView");
    }
    finally
    {
      monitorexit;
    }
    synchronized (instance.yuvImageRenderers)
    {
      Iterator localIterator = instance.yuvImageRenderers.iterator();
      while (localIterator.hasNext())
      {
        YuvImageRenderer localYuvImageRenderer = (YuvImageRenderer)localIterator.next();
        if (localYuvImageRenderer != paramCallbacks)
          continue;
        YuvImageRenderer.access$702(localYuvImageRenderer, paramRendererEvents);
      }
    }
    monitorexit;
    monitorexit;
  }

  public static void setView(GLSurfaceView paramGLSurfaceView, Runnable paramRunnable)
  {
    monitorenter;
    try
    {
      Logging.d("VideoRendererGui", "VideoRendererGui.setView");
      instance = new VideoRendererGui(paramGLSurfaceView);
      eglContextReady = paramRunnable;
      monitorexit;
      return;
    }
    finally
    {
      paramGLSurfaceView = finally;
      monitorexit;
    }
    throw paramGLSurfaceView;
  }

  public static void update(VideoRenderer.Callbacks paramCallbacks, int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType paramScalingType, boolean paramBoolean)
  {
    monitorenter;
    try
    {
      Logging.d("VideoRendererGui", "VideoRendererGui.update");
      if (instance == null)
        throw new RuntimeException("Attempt to update yuv renderer before setting GLSurfaceView");
    }
    finally
    {
      monitorexit;
    }
    synchronized (instance.yuvImageRenderers)
    {
      Iterator localIterator = instance.yuvImageRenderers.iterator();
      while (localIterator.hasNext())
      {
        YuvImageRenderer localYuvImageRenderer = (YuvImageRenderer)localIterator.next();
        if (localYuvImageRenderer != paramCallbacks)
          continue;
        localYuvImageRenderer.setPosition(paramInt1, paramInt2, paramInt3, paramInt4, paramScalingType, paramBoolean);
      }
    }
    monitorexit;
    monitorexit;
  }

  public void onDrawFrame(GL10 arg1)
  {
    if (drawThread == null)
      drawThread = Thread.currentThread();
    GLES20.glViewport(0, 0, this.screenWidth, this.screenHeight);
    GLES20.glClear(16384);
    synchronized (this.yuvImageRenderers)
    {
      Iterator localIterator = this.yuvImageRenderers.iterator();
      if (localIterator.hasNext())
        ((YuvImageRenderer)localIterator.next()).draw();
    }
    monitorexit;
  }

  public void onSurfaceChanged(GL10 arg1, int paramInt1, int paramInt2)
  {
    Logging.d("VideoRendererGui", "VideoRendererGui.onSurfaceChanged: " + paramInt1 + " x " + paramInt2 + "  ");
    this.screenWidth = paramInt1;
    this.screenHeight = paramInt2;
    synchronized (this.yuvImageRenderers)
    {
      Iterator localIterator = this.yuvImageRenderers.iterator();
      if (localIterator.hasNext())
        ((YuvImageRenderer)localIterator.next()).setScreenSize(this.screenWidth, this.screenHeight);
    }
    monitorexit;
  }

  @SuppressLint({"NewApi"})
  public void onSurfaceCreated(GL10 arg1, EGLConfig paramEGLConfig)
  {
    Logging.d("VideoRendererGui", "VideoRendererGui.onSurfaceCreated");
    monitorenter;
    try
    {
      if (EglBase14.isEGL14Supported())
        eglContext = new EglBase14.Context(EGL14.eglGetCurrentContext());
      while (true)
      {
        Logging.d("VideoRendererGui", "VideoRendererGui EGL Context: " + eglContext);
        monitorexit;
        synchronized (this.yuvImageRenderers)
        {
          paramEGLConfig = this.yuvImageRenderers.iterator();
          if (!paramEGLConfig.hasNext())
            break;
          ((YuvImageRenderer)paramEGLConfig.next()).createTextures();
        }
        eglContext = new EglBase10.Context(((EGL10)EGLContext.getEGL()).eglGetCurrentContext());
      }
    }
    finally
    {
      monitorexit;
    }
    this.onSurfaceCreatedCalled = true;
    monitorexit;
    GlUtil.checkNoGLES2Error("onSurfaceCreated done");
    GLES20.glPixelStorei(3317, 1);
    GLES20.glClearColor(0.15F, 0.15F, 0.15F, 1.0F);
    monitorenter;
    try
    {
      if (eglContextReady != null)
        eglContextReady.run();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw ???;
  }

  private static class YuvImageRenderer
    implements VideoRenderer.Callbacks
  {
    private long copyTimeNs;
    private final Rect displayLayout = new Rect();
    private long drawTimeNs;
    private final RendererCommon.GlDrawer drawer;
    private int framesDropped;
    private int framesReceived;
    private int framesRendered;
    private int id;
    private final Rect layoutInPercentage;
    private float[] layoutMatrix;
    private boolean mirror;
    private VideoRenderer.I420Frame pendingFrame;
    private final Object pendingFrameLock = new Object();
    private RendererCommon.RendererEvents rendererEvents;
    private RendererType rendererType;
    private float[] rotatedSamplingMatrix;
    private int rotationDegree;
    private RendererCommon.ScalingType scalingType;
    private int screenHeight;
    private int screenWidth;
    boolean seenFrame;
    private long startTimeNs = -1L;
    private GLSurfaceView surface;
    private GlTextureFrameBuffer textureCopy;
    private final Object updateLayoutLock = new Object();
    private boolean updateLayoutProperties;
    private int videoHeight;
    private int videoWidth;
    private int[] yuvTextures = { 0, 0, 0 };
    private final RendererCommon.YuvUploader yuvUploader = new RendererCommon.YuvUploader();

    private YuvImageRenderer(GLSurfaceView paramGLSurfaceView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, RendererCommon.ScalingType paramScalingType, boolean paramBoolean, RendererCommon.GlDrawer paramGlDrawer)
    {
      Logging.d("VideoRendererGui", "YuvImageRenderer.Create id: " + paramInt1);
      this.surface = paramGLSurfaceView;
      this.id = paramInt1;
      this.scalingType = paramScalingType;
      this.mirror = paramBoolean;
      this.drawer = paramGlDrawer;
      this.layoutInPercentage = new Rect(paramInt2, paramInt3, Math.min(100, paramInt2 + paramInt4), Math.min(100, paramInt3 + paramInt5));
      this.updateLayoutProperties = false;
      this.rotationDegree = 0;
    }

    private void createTextures()
    {
      Logging.d("VideoRendererGui", "  YuvImageRenderer.createTextures " + this.id + " on GL thread:" + Thread.currentThread().getId());
      int i = 0;
      while (i < 3)
      {
        this.yuvTextures[i] = GlUtil.generateTexture(3553);
        i += 1;
      }
      this.textureCopy = new GlTextureFrameBuffer(6407);
    }

    private void draw()
    {
      if (!this.seenFrame)
        return;
      long l = System.nanoTime();
      while (true)
      {
        int j;
        synchronized (this.pendingFrameLock)
        {
          if (this.pendingFrame == null)
            continue;
          int i = 1;
          if ((i == 0) || (this.startTimeNs != -1L))
            continue;
          this.startTimeNs = l;
          if (i == 0)
            continue;
          this.rotatedSamplingMatrix = RendererCommon.rotateTextureMatrix(this.pendingFrame.samplingMatrix, this.pendingFrame.rotationDegree);
          if (!this.pendingFrame.yuvFrame)
            continue;
          this.rendererType = RendererType.RENDERER_YUV;
          this.yuvUploader.uploadYuvData(this.yuvTextures, this.pendingFrame.width, this.pendingFrame.height, this.pendingFrame.yuvStrides, this.pendingFrame.yuvPlanes);
          this.copyTimeNs += System.nanoTime() - l;
          VideoRenderer.renderFrameDone(this.pendingFrame);
          this.pendingFrame = null;
          updateLayoutMatrix();
          ??? = RendererCommon.multiplyMatrices(this.rotatedSamplingMatrix, this.layoutMatrix);
          j = this.screenHeight - this.displayLayout.bottom;
          if (this.rendererType == RendererType.RENDERER_YUV)
          {
            this.drawer.drawYuv(this.yuvTextures, ???, this.videoWidth, this.videoHeight, this.displayLayout.left, j, this.displayLayout.width(), this.displayLayout.height());
            if (i == 0)
              break;
            this.framesRendered += 1;
            this.drawTimeNs += System.nanoTime() - l;
            if (this.framesRendered % 300 != 0)
              break;
            logStatistics();
            return;
            i = 0;
            continue;
            this.rendererType = RendererType.RENDERER_TEXTURE;
            this.textureCopy.setSize(this.pendingFrame.rotatedWidth(), this.pendingFrame.rotatedHeight());
            GLES20.glBindFramebuffer(36160, this.textureCopy.getFrameBufferId());
            GlUtil.checkNoGLES2Error("glBindFramebuffer");
            this.drawer.drawOes(this.pendingFrame.textureId, this.rotatedSamplingMatrix, this.textureCopy.getWidth(), this.textureCopy.getHeight(), 0, 0, this.textureCopy.getWidth(), this.textureCopy.getHeight());
            this.rotatedSamplingMatrix = RendererCommon.identityMatrix();
            GLES20.glBindFramebuffer(36160, 0);
            GLES20.glFinish();
          }
        }
        this.drawer.drawRgb(this.textureCopy.getTextureId(), ???, this.videoWidth, this.videoHeight, this.displayLayout.left, j, this.displayLayout.width(), this.displayLayout.height());
      }
    }

    private void logStatistics()
    {
      long l = System.nanoTime() - this.startTimeNs;
      Logging.d("VideoRendererGui", "ID: " + this.id + ". Type: " + this.rendererType + ". Frames received: " + this.framesReceived + ". Dropped: " + this.framesDropped + ". Rendered: " + this.framesRendered);
      if ((this.framesReceived > 0) && (this.framesRendered > 0))
      {
        Logging.d("VideoRendererGui", "Duration: " + (int)(l / 1000000.0D) + " ms. FPS: " + this.framesRendered * 1000000000.0D / l);
        Logging.d("VideoRendererGui", "Draw time: " + (int)(this.drawTimeNs / (this.framesRendered * 1000)) + " us. Copy time: " + (int)(this.copyTimeNs / (this.framesReceived * 1000)) + " us");
      }
    }

    private void release()
    {
      monitorenter;
      try
      {
        this.surface = null;
        this.drawer.release();
        synchronized (this.pendingFrameLock)
        {
          if (this.pendingFrame != null)
          {
            VideoRenderer.renderFrameDone(this.pendingFrame);
            this.pendingFrame = null;
          }
          monitorexit;
          return;
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject2;
    }

    private void setSize(int paramInt1, int paramInt2, int paramInt3)
    {
      if ((paramInt1 == this.videoWidth) && (paramInt2 == this.videoHeight) && (paramInt3 == this.rotationDegree))
        return;
      if (this.rendererEvents != null)
      {
        Logging.d("VideoRendererGui", "ID: " + this.id + ". Reporting frame resolution changed to " + paramInt1 + " x " + paramInt2);
        this.rendererEvents.onFrameResolutionChanged(paramInt1, paramInt2, paramInt3);
      }
      synchronized (this.updateLayoutLock)
      {
        Logging.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setSize: " + paramInt1 + " x " + paramInt2 + " rotation " + paramInt3);
        this.videoWidth = paramInt1;
        this.videoHeight = paramInt2;
        this.rotationDegree = paramInt3;
        this.updateLayoutProperties = true;
        Logging.d("VideoRendererGui", "  YuvImageRenderer.setSize done.");
        return;
      }
    }

    private void updateLayoutMatrix()
    {
      while (true)
      {
        synchronized (this.updateLayoutLock)
        {
          if (!this.updateLayoutProperties)
            return;
          this.displayLayout.set((this.screenWidth * this.layoutInPercentage.left + 99) / 100, (this.screenHeight * this.layoutInPercentage.top + 99) / 100, this.screenWidth * this.layoutInPercentage.right / 100, this.screenHeight * this.layoutInPercentage.bottom / 100);
          Logging.d("VideoRendererGui", "ID: " + this.id + ". AdjustTextureCoords. Allowed display size: " + this.displayLayout.width() + " x " + this.displayLayout.height() + ". Video: " + this.videoWidth + " x " + this.videoHeight + ". Rotation: " + this.rotationDegree + ". Mirror: " + this.mirror);
          if (this.rotationDegree % 180 == 0)
          {
            f = this.videoWidth / this.videoHeight;
            Point localPoint = RendererCommon.getDisplaySize(this.scalingType, f, this.displayLayout.width(), this.displayLayout.height());
            this.displayLayout.inset((this.displayLayout.width() - localPoint.x) / 2, (this.displayLayout.height() - localPoint.y) / 2);
            Logging.d("VideoRendererGui", "  Adjusted display size: " + this.displayLayout.width() + " x " + this.displayLayout.height());
            this.layoutMatrix = RendererCommon.getLayoutMatrix(this.mirror, f, this.displayLayout.width() / this.displayLayout.height());
            this.updateLayoutProperties = false;
            Logging.d("VideoRendererGui", "  AdjustTextureCoords done");
            return;
          }
        }
        float f = this.videoHeight;
        int i = this.videoWidth;
        f /= i;
      }
    }

    public void renderFrame(VideoRenderer.I420Frame paramI420Frame)
    {
      monitorenter;
      while (true)
      {
        try
        {
          if (this.surface != null)
            continue;
          VideoRenderer.renderFrameDone(paramI420Frame);
          return;
          if (VideoRendererGui.renderFrameThread != null)
            continue;
          VideoRendererGui.access$002(Thread.currentThread());
          if ((this.seenFrame) || (this.rendererEvents == null))
            continue;
          Logging.d("VideoRendererGui", "ID: " + this.id + ". Reporting first rendered frame.");
          this.rendererEvents.onFirstFrameRendered();
          this.framesReceived += 1;
          synchronized (this.pendingFrameLock)
          {
            if ((paramI420Frame.yuvFrame) && ((paramI420Frame.yuvStrides[0] < paramI420Frame.width) || (paramI420Frame.yuvStrides[1] < paramI420Frame.width / 2) || (paramI420Frame.yuvStrides[2] < paramI420Frame.width / 2)))
            {
              Logging.e("VideoRendererGui", "Incorrect strides " + paramI420Frame.yuvStrides[0] + ", " + paramI420Frame.yuvStrides[1] + ", " + paramI420Frame.yuvStrides[2]);
              VideoRenderer.renderFrameDone(paramI420Frame);
            }
          }
        }
        finally
        {
          monitorexit;
        }
        if (this.pendingFrame != null)
        {
          this.framesDropped += 1;
          VideoRenderer.renderFrameDone(paramI420Frame);
          this.seenFrame = true;
          monitorexit;
          continue;
        }
        this.pendingFrame = paramI420Frame;
        monitorexit;
        setSize(paramI420Frame.width, paramI420Frame.height, paramI420Frame.rotationDegree);
        this.seenFrame = true;
        this.surface.requestRender();
      }
    }

    public void reset()
    {
      monitorenter;
      try
      {
        this.seenFrame = false;
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }

    public void setPosition(int paramInt1, int paramInt2, int paramInt3, int paramInt4, RendererCommon.ScalingType paramScalingType, boolean paramBoolean)
    {
      Rect localRect = new Rect(paramInt1, paramInt2, Math.min(100, paramInt1 + paramInt3), Math.min(100, paramInt2 + paramInt4));
      synchronized (this.updateLayoutLock)
      {
        if ((localRect.equals(this.layoutInPercentage)) && (paramScalingType == this.scalingType) && (paramBoolean == this.mirror))
          return;
        Logging.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setPosition: (" + paramInt1 + ", " + paramInt2 + ") " + paramInt3 + " x " + paramInt4 + ". Scaling: " + paramScalingType + ". Mirror: " + paramBoolean);
        this.layoutInPercentage.set(localRect);
        this.scalingType = paramScalingType;
        this.mirror = paramBoolean;
        this.updateLayoutProperties = true;
        return;
      }
    }

    public void setScreenSize(int paramInt1, int paramInt2)
    {
      synchronized (this.updateLayoutLock)
      {
        if ((paramInt1 == this.screenWidth) && (paramInt2 == this.screenHeight))
          return;
        Logging.d("VideoRendererGui", "ID: " + this.id + ". YuvImageRenderer.setScreenSize: " + paramInt1 + " x " + paramInt2);
        this.screenWidth = paramInt1;
        this.screenHeight = paramInt2;
        this.updateLayoutProperties = true;
        return;
      }
    }

    private static enum RendererType
    {
      static
      {
        RENDERER_TEXTURE = new RendererType("RENDERER_TEXTURE", 1);
        $VALUES = new RendererType[] { RENDERER_YUV, RENDERER_TEXTURE };
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoRendererGui
 * JD-Core Version:    0.6.0
 */