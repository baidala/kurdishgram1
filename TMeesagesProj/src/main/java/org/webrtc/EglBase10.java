package org.webrtc;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public final class EglBase10 extends EglBase
{
  private static final int EGL_CONTEXT_CLIENT_VERSION = 12440;
  private final EGL10 egl = (EGL10)EGLContext.getEGL();
  private EGLConfig eglConfig = getEglConfig(this.eglDisplay, paramArrayOfInt);
  private EGLContext eglContext = createEglContext(paramContext, this.eglDisplay, this.eglConfig);
  private EGLDisplay eglDisplay = getEglDisplay();
  private EGLSurface eglSurface = EGL10.EGL_NO_SURFACE;

  public EglBase10(Context paramContext, int[] paramArrayOfInt)
  {
  }

  private void checkIsNotReleased()
  {
    if ((this.eglDisplay == EGL10.EGL_NO_DISPLAY) || (this.eglContext == EGL10.EGL_NO_CONTEXT) || (this.eglConfig == null))
      throw new RuntimeException("This object has been released");
  }

  private EGLContext createEglContext(Context paramContext, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig)
  {
    if ((paramContext != null) && (paramContext.eglContext == EGL10.EGL_NO_CONTEXT))
      throw new RuntimeException("Invalid sharedContext");
    if (paramContext == null)
      paramContext = EGL10.EGL_NO_CONTEXT;
    synchronized (EglBase.lock)
    {
      paramContext = this.egl.eglCreateContext(paramEGLDisplay, paramEGLConfig, paramContext, new int[] { 12440, 2, 12344 });
      if (paramContext == EGL10.EGL_NO_CONTEXT)
      {
        throw new RuntimeException("Failed to create EGL context");
        paramContext = paramContext.eglContext;
      }
    }
    return paramContext;
  }

  private void createSurfaceInternal(Object paramObject)
  {
    if ((!(paramObject instanceof SurfaceHolder)) && (!(paramObject instanceof SurfaceTexture)))
      throw new IllegalStateException("Input must be either a SurfaceHolder or SurfaceTexture");
    checkIsNotReleased();
    if (this.eglSurface != EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("Already has an EGLSurface");
    this.eglSurface = this.egl.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, paramObject, new int[] { 12344 });
    if (this.eglSurface == EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("Failed to create window surface");
  }

  private EGLConfig getEglConfig(EGLDisplay paramEGLDisplay, int[] paramArrayOfInt)
  {
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
    int[] arrayOfInt = new int[1];
    if (!this.egl.eglChooseConfig(paramEGLDisplay, paramArrayOfInt, arrayOfEGLConfig, arrayOfEGLConfig.length, arrayOfInt))
      throw new RuntimeException("eglChooseConfig failed");
    if (arrayOfInt[0] <= 0)
      throw new RuntimeException("Unable to find any matching EGL config");
    paramEGLDisplay = arrayOfEGLConfig[0];
    if (paramEGLDisplay == null)
      throw new RuntimeException("eglChooseConfig returned null");
    return paramEGLDisplay;
  }

  private EGLDisplay getEglDisplay()
  {
    EGLDisplay localEGLDisplay = this.egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
    if (localEGLDisplay == EGL10.EGL_NO_DISPLAY)
      throw new RuntimeException("Unable to get EGL10 display");
    int[] arrayOfInt = new int[2];
    if (!this.egl.eglInitialize(localEGLDisplay, arrayOfInt))
      throw new RuntimeException("Unable to initialize EGL10");
    return localEGLDisplay;
  }

  public void createDummyPbufferSurface()
  {
    createPbufferSurface(1, 1);
  }

  public void createPbufferSurface(int paramInt1, int paramInt2)
  {
    checkIsNotReleased();
    if (this.eglSurface != EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("Already has an EGLSurface");
    this.eglSurface = this.egl.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[] { 12375, paramInt1, 12374, paramInt2, 12344 });
    if (this.eglSurface == EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("Failed to create pixel buffer surface with size: " + paramInt1 + "x" + paramInt2);
  }

  public void createSurface(SurfaceTexture paramSurfaceTexture)
  {
    createSurfaceInternal(paramSurfaceTexture);
  }

  public void createSurface(Surface paramSurface)
  {
    createSurfaceInternal(new SurfaceHolder(paramSurface)
    {
      private final Surface surface;

      public void addCallback(SurfaceHolder.Callback paramCallback)
      {
      }

      public Surface getSurface()
      {
        return this.surface;
      }

      public Rect getSurfaceFrame()
      {
        return null;
      }

      public boolean isCreating()
      {
        return false;
      }

      public Canvas lockCanvas()
      {
        return null;
      }

      public Canvas lockCanvas(Rect paramRect)
      {
        return null;
      }

      public void removeCallback(SurfaceHolder.Callback paramCallback)
      {
      }

      public void setFixedSize(int paramInt1, int paramInt2)
      {
      }

      public void setFormat(int paramInt)
      {
      }

      public void setKeepScreenOn(boolean paramBoolean)
      {
      }

      public void setSizeFromLayout()
      {
      }

      @Deprecated
      public void setType(int paramInt)
      {
      }

      public void unlockCanvasAndPost(Canvas paramCanvas)
      {
      }
    });
  }

  public void detachCurrent()
  {
    synchronized (EglBase.lock)
    {
      if (!this.egl.eglMakeCurrent(this.eglDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT))
        throw new RuntimeException("eglDetachCurrent failed");
    }
    monitorexit;
  }

  public EglBase.Context getEglBaseContext()
  {
    return new Context(this.eglContext);
  }

  public boolean hasSurface()
  {
    return this.eglSurface != EGL10.EGL_NO_SURFACE;
  }

  public void makeCurrent()
  {
    checkIsNotReleased();
    if (this.eglSurface == EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("No EGLSurface - can't make current");
    synchronized (EglBase.lock)
    {
      if (!this.egl.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
        throw new RuntimeException("eglMakeCurrent failed");
    }
    monitorexit;
  }

  public void release()
  {
    checkIsNotReleased();
    releaseSurface();
    detachCurrent();
    this.egl.eglDestroyContext(this.eglDisplay, this.eglContext);
    this.egl.eglTerminate(this.eglDisplay);
    this.eglContext = EGL10.EGL_NO_CONTEXT;
    this.eglDisplay = EGL10.EGL_NO_DISPLAY;
    this.eglConfig = null;
  }

  public void releaseSurface()
  {
    if (this.eglSurface != EGL10.EGL_NO_SURFACE)
    {
      this.egl.eglDestroySurface(this.eglDisplay, this.eglSurface);
      this.eglSurface = EGL10.EGL_NO_SURFACE;
    }
  }

  public int surfaceHeight()
  {
    int[] arrayOfInt = new int[1];
    this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, arrayOfInt);
    return arrayOfInt[0];
  }

  public int surfaceWidth()
  {
    int[] arrayOfInt = new int[1];
    this.egl.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, arrayOfInt);
    return arrayOfInt[0];
  }

  public void swapBuffers()
  {
    checkIsNotReleased();
    if (this.eglSurface == EGL10.EGL_NO_SURFACE)
      throw new RuntimeException("No EGLSurface - can't swap buffers");
    synchronized (EglBase.lock)
    {
      this.egl.eglSwapBuffers(this.eglDisplay, this.eglSurface);
      return;
    }
  }

  public static class Context extends EglBase.Context
  {
    private final EGLContext eglContext;

    public Context(EGLContext paramEGLContext)
    {
      this.eglContext = paramEGLContext;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.EglBase10
 * JD-Core Version:    0.6.0
 */