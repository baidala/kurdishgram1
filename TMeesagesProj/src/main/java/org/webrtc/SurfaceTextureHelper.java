package org.webrtc;

import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.opengl.GLES20;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

class SurfaceTextureHelper
{
  private static final String TAG = "SurfaceTextureHelper";
  private final EglBase eglBase;
  private final Handler handler;
  private boolean hasPendingTexture = false;
  private boolean isQuitting = false;
  private volatile boolean isTextureInUse = false;
  private OnTextureFrameAvailableListener listener;
  private final int oesTextureId;
  private OnTextureFrameAvailableListener pendingListener;
  final Runnable setListenerRunnable = new Runnable()
  {
    public void run()
    {
      Logging.d("SurfaceTextureHelper", "Setting listener to " + SurfaceTextureHelper.this.pendingListener);
      SurfaceTextureHelper.access$202(SurfaceTextureHelper.this, SurfaceTextureHelper.this.pendingListener);
      SurfaceTextureHelper.access$102(SurfaceTextureHelper.this, null);
      if (SurfaceTextureHelper.this.hasPendingTexture)
      {
        SurfaceTextureHelper.this.updateTexImage();
        SurfaceTextureHelper.access$302(SurfaceTextureHelper.this, false);
      }
    }
  };
  private final SurfaceTexture surfaceTexture;
  private YuvConverter yuvConverter;

  private SurfaceTextureHelper(EglBase.Context paramContext, Handler paramHandler)
  {
    if (paramHandler.getLooper().getThread() != Thread.currentThread())
      throw new IllegalStateException("SurfaceTextureHelper must be created on the handler thread");
    this.handler = paramHandler;
    this.eglBase = EglBase.create(paramContext, EglBase.CONFIG_PIXEL_BUFFER);
    try
    {
      this.eglBase.createDummyPbufferSurface();
      this.eglBase.makeCurrent();
      this.oesTextureId = GlUtil.generateTexture(36197);
      this.surfaceTexture = new SurfaceTexture(this.oesTextureId);
      this.surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener()
      {
        public void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
        {
          SurfaceTextureHelper.access$302(SurfaceTextureHelper.this, true);
          SurfaceTextureHelper.this.tryDeliverTextureFrame();
        }
      });
      return;
    }
    catch (RuntimeException paramContext)
    {
      this.eglBase.release();
      paramHandler.getLooper().quit();
    }
    throw paramContext;
  }

  public static SurfaceTextureHelper create(String paramString, EglBase.Context paramContext)
  {
    Object localObject = new HandlerThread(paramString);
    ((HandlerThread)localObject).start();
    localObject = new Handler(((HandlerThread)localObject).getLooper());
    return (SurfaceTextureHelper)(SurfaceTextureHelper)ThreadUtils.invokeAtFrontUninterruptibly((Handler)localObject, new Callable(paramContext, (Handler)localObject, paramString)
    {
      public SurfaceTextureHelper call()
      {
        try
        {
          SurfaceTextureHelper localSurfaceTextureHelper = new SurfaceTextureHelper(this.val$sharedContext, this.val$handler, null);
          return localSurfaceTextureHelper;
        }
        catch (RuntimeException localRuntimeException)
        {
          Logging.e("SurfaceTextureHelper", this.val$threadName + " create failure", localRuntimeException);
        }
        return null;
      }
    });
  }

  private YuvConverter getYuvConverter()
  {
    if (this.yuvConverter != null)
      return this.yuvConverter;
    monitorenter;
    try
    {
      if (this.yuvConverter == null)
        this.yuvConverter = new YuvConverter(this.eglBase.getEglBaseContext());
      YuvConverter localYuvConverter = this.yuvConverter;
      return localYuvConverter;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private void release()
  {
    if (this.handler.getLooper().getThread() != Thread.currentThread())
      throw new IllegalStateException("Wrong thread.");
    if ((this.isTextureInUse) || (!this.isQuitting))
      throw new IllegalStateException("Unexpected release.");
    monitorenter;
    try
    {
      if (this.yuvConverter != null)
        this.yuvConverter.release();
      monitorexit;
      GLES20.glDeleteTextures(1, new int[] { this.oesTextureId }, 0);
      this.surfaceTexture.release();
      this.eglBase.release();
      this.handler.getLooper().quit();
      return;
    }
    finally
    {
      monitorexit;
    }
    throw localObject;
  }

  private void tryDeliverTextureFrame()
  {
    if (this.handler.getLooper().getThread() != Thread.currentThread())
      throw new IllegalStateException("Wrong thread.");
    if ((this.isQuitting) || (!this.hasPendingTexture) || (this.isTextureInUse) || (this.listener == null))
      return;
    this.isTextureInUse = true;
    this.hasPendingTexture = false;
    updateTexImage();
    float[] arrayOfFloat = new float[16];
    this.surfaceTexture.getTransformMatrix(arrayOfFloat);
    long l;
    if (Build.VERSION.SDK_INT >= 14)
      l = this.surfaceTexture.getTimestamp();
    while (true)
    {
      this.listener.onTextureFrameAvailable(this.oesTextureId, arrayOfFloat, l);
      return;
      l = TimeUnit.MILLISECONDS.toNanos(SystemClock.elapsedRealtime());
    }
  }

  private void updateTexImage()
  {
    synchronized (EglBase.lock)
    {
      this.surfaceTexture.updateTexImage();
      return;
    }
  }

  public void dispose()
  {
    Logging.d("SurfaceTextureHelper", "dispose()");
    ThreadUtils.invokeAtFrontUninterruptibly(this.handler, new Runnable()
    {
      public void run()
      {
        SurfaceTextureHelper.access$702(SurfaceTextureHelper.this, true);
        if (!SurfaceTextureHelper.this.isTextureInUse)
          SurfaceTextureHelper.this.release();
      }
    });
  }

  public Handler getHandler()
  {
    return this.handler;
  }

  public SurfaceTexture getSurfaceTexture()
  {
    return this.surfaceTexture;
  }

  public boolean isTextureInUse()
  {
    return this.isTextureInUse;
  }

  public void returnTextureFrame()
  {
    this.handler.post(new Runnable()
    {
      public void run()
      {
        SurfaceTextureHelper.access$602(SurfaceTextureHelper.this, false);
        if (SurfaceTextureHelper.this.isQuitting)
        {
          SurfaceTextureHelper.this.release();
          return;
        }
        SurfaceTextureHelper.this.tryDeliverTextureFrame();
      }
    });
  }

  public void startListening(OnTextureFrameAvailableListener paramOnTextureFrameAvailableListener)
  {
    if ((this.listener != null) || (this.pendingListener != null))
      throw new IllegalStateException("SurfaceTextureHelper listener has already been set.");
    this.pendingListener = paramOnTextureFrameAvailableListener;
    this.handler.post(this.setListenerRunnable);
  }

  public void stopListening()
  {
    Logging.d("SurfaceTextureHelper", "stopListening()");
    this.handler.removeCallbacks(this.setListenerRunnable);
    ThreadUtils.invokeAtFrontUninterruptibly(this.handler, new Runnable()
    {
      public void run()
      {
        SurfaceTextureHelper.access$202(SurfaceTextureHelper.this, null);
        SurfaceTextureHelper.access$102(SurfaceTextureHelper.this, null);
      }
    });
  }

  public void textureToYUV(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat)
  {
    if (paramInt4 != this.oesTextureId)
      throw new IllegalStateException("textureToByteBuffer called with unexpected textureId");
    getYuvConverter().convert(paramByteBuffer, paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfFloat);
  }

  public static abstract interface OnTextureFrameAvailableListener
  {
    public abstract void onTextureFrameAvailable(int paramInt, float[] paramArrayOfFloat, long paramLong);
  }

  private static class YuvConverter
  {
    private static final FloatBuffer DEVICE_RECTANGLE = GlUtil.createFloatBuffer(new float[] { -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, 1.0F });
    private static final String FRAGMENT_SHADER = "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oesTex;\nuniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 1.5 * xUnit).rgb);\n}\n";
    private static final FloatBuffer TEXTURE_RECTANGLE = GlUtil.createFloatBuffer(new float[] { 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F });
    private static final String VERTEX_SHADER = "varying vec2 interp_tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\n\nuniform mat4 texMatrix;\n\nvoid main() {\n    gl_Position = in_pos;\n    interp_tc = (texMatrix * in_tc).xy;\n}\n";
    private int coeffsLoc;
    private final EglBase eglBase;
    private boolean released = false;
    private final GlShader shader;
    private int texMatrixLoc;
    private int xUnitLoc;

    YuvConverter(EglBase.Context paramContext)
    {
      this.eglBase = EglBase.create(paramContext, EglBase.CONFIG_PIXEL_RGBA_BUFFER);
      this.eglBase.createDummyPbufferSurface();
      this.eglBase.makeCurrent();
      this.shader = new GlShader("varying vec2 interp_tc;\nattribute vec4 in_pos;\nattribute vec4 in_tc;\n\nuniform mat4 texMatrix;\n\nvoid main() {\n    gl_Position = in_pos;\n    interp_tc = (texMatrix * in_tc).xy;\n}\n", "#extension GL_OES_EGL_image_external : require\nprecision mediump float;\nvarying vec2 interp_tc;\n\nuniform samplerExternalOES oesTex;\nuniform vec2 xUnit;\nuniform vec4 coeffs;\n\nvoid main() {\n  gl_FragColor.r = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 1.5 * xUnit).rgb);\n  gl_FragColor.g = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc - 0.5 * xUnit).rgb);\n  gl_FragColor.b = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 0.5 * xUnit).rgb);\n  gl_FragColor.a = coeffs.a + dot(coeffs.rgb,\n      texture2D(oesTex, interp_tc + 1.5 * xUnit).rgb);\n}\n");
      this.shader.useProgram();
      this.texMatrixLoc = this.shader.getUniformLocation("texMatrix");
      this.xUnitLoc = this.shader.getUniformLocation("xUnit");
      this.coeffsLoc = this.shader.getUniformLocation("coeffs");
      GLES20.glUniform1i(this.shader.getUniformLocation("oesTex"), 0);
      GlUtil.checkNoGLES2Error("Initialize fragment shader uniform values.");
      this.shader.setVertexAttribArray("in_pos", 2, DEVICE_RECTANGLE);
      this.shader.setVertexAttribArray("in_tc", 2, TEXTURE_RECTANGLE);
      this.eglBase.detachCurrent();
    }

    void convert(ByteBuffer paramByteBuffer, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat)
    {
      monitorenter;
      try
      {
        if (this.released)
          throw new IllegalStateException("YuvConverter.convert called on released object");
      }
      finally
      {
        monitorexit;
      }
      if (paramInt3 % 8 != 0)
        throw new IllegalArgumentException("Invalid stride, must be a multiple of 8");
      if (paramInt3 < paramInt1)
        throw new IllegalArgumentException("Invalid stride, must >= width");
      int i = (paramInt1 + 3) / 4;
      int j = (paramInt1 + 7) / 8;
      int k = (paramInt2 + 1) / 2;
      int m = paramInt2 + k;
      if (paramByteBuffer.capacity() < paramInt3 * m)
        throw new IllegalArgumentException("YuvConverter.convert called with too small buffer");
      paramArrayOfFloat = RendererCommon.multiplyMatrices(paramArrayOfFloat, RendererCommon.verticalFlipMatrix());
      if (this.eglBase.hasSurface())
        if ((this.eglBase.surfaceWidth() != paramInt3 / 4) || (this.eglBase.surfaceHeight() != m))
        {
          this.eglBase.releaseSurface();
          this.eglBase.createPbufferSurface(paramInt3 / 4, m);
        }
      while (true)
      {
        this.eglBase.makeCurrent();
        GLES20.glActiveTexture(33984);
        GLES20.glBindTexture(36197, paramInt4);
        GLES20.glUniformMatrix4fv(this.texMatrixLoc, 1, false, paramArrayOfFloat, 0);
        GLES20.glViewport(0, 0, i, paramInt2);
        GLES20.glUniform2f(this.xUnitLoc, paramArrayOfFloat[0] / paramInt1, paramArrayOfFloat[1] / paramInt1);
        GLES20.glUniform4f(this.coeffsLoc, 0.299F, 0.587F, 0.114F, 0.0F);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glViewport(0, paramInt2, j, k);
        GLES20.glUniform2f(this.xUnitLoc, 2.0F * paramArrayOfFloat[0] / paramInt1, paramArrayOfFloat[1] * 2.0F / paramInt1);
        GLES20.glUniform4f(this.coeffsLoc, -0.169F, -0.331F, 0.499F, 0.5F);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glViewport(paramInt3 / 8, paramInt2, j, k);
        GLES20.glUniform4f(this.coeffsLoc, 0.499F, -0.418F, -0.0813F, 0.5F);
        GLES20.glDrawArrays(5, 0, 4);
        GLES20.glReadPixels(0, 0, paramInt3 / 4, m, 6408, 5121, paramByteBuffer);
        GlUtil.checkNoGLES2Error("YuvConverter.convert");
        GLES20.glBindTexture(36197, 0);
        this.eglBase.detachCurrent();
        monitorexit;
        return;
        this.eglBase.createPbufferSurface(paramInt3 / 4, m);
      }
    }

    void release()
    {
      monitorenter;
      try
      {
        this.released = true;
        this.eglBase.makeCurrent();
        this.shader.release();
        this.eglBase.release();
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
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.SurfaceTextureHelper
 * JD-Core Version:    0.6.0
 */