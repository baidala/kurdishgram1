package org.webrtc;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.os.Build.VERSION;
import android.view.Surface;

@TargetApi(18)
public final class EglBase14 extends EglBase
{
  private static final int CURRENT_SDK_VERSION = Build.VERSION.SDK_INT;
  private static final int EGLExt_SDK_VERSION = 18;
  private static final String TAG = "EglBase14";
  private EGLConfig eglConfig;
  private EGLContext eglContext;
  private EGLDisplay eglDisplay = getEglDisplay();
  private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;

  public EglBase14(Context paramContext, int[] paramArrayOfInt)
  {
    this.eglConfig = getEglConfig(this.eglDisplay, paramArrayOfInt);
    this.eglContext = createEglContext(paramContext, this.eglDisplay, this.eglConfig);
  }

  private void checkIsNotReleased()
  {
    if ((this.eglDisplay == EGL14.EGL_NO_DISPLAY) || (this.eglContext == EGL14.EGL_NO_CONTEXT) || (this.eglConfig == null))
      throw new RuntimeException("This object has been released");
  }

  private static EGLContext createEglContext(Context paramContext, EGLDisplay paramEGLDisplay, EGLConfig paramEGLConfig)
  {
    if ((paramContext != null) && (paramContext.egl14Context == EGL14.EGL_NO_CONTEXT))
      throw new RuntimeException("Invalid sharedContext");
    if (paramContext == null)
      paramContext = EGL14.EGL_NO_CONTEXT;
    synchronized (EglBase.lock)
    {
      paramContext = EGL14.eglCreateContext(paramEGLDisplay, paramEGLConfig, paramContext, new int[] { 12440, 2, 12344 }, 0);
      if (paramContext == EGL14.EGL_NO_CONTEXT)
      {
        throw new RuntimeException("Failed to create EGL context");
        paramContext = paramContext.egl14Context;
      }
    }
    return paramContext;
  }

  private void createSurfaceInternal(Object paramObject)
  {
    if ((!(paramObject instanceof Surface)) && (!(paramObject instanceof SurfaceTexture)))
      throw new IllegalStateException("Input must be either a Surface or SurfaceTexture");
    checkIsNotReleased();
    if (this.eglSurface != EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("Already has an EGLSurface");
    this.eglSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, paramObject, new int[] { 12344 }, 0);
    if (this.eglSurface == EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("Failed to create window surface");
  }

  private static EGLConfig getEglConfig(EGLDisplay paramEGLDisplay, int[] paramArrayOfInt)
  {
    EGLConfig[] arrayOfEGLConfig = new EGLConfig[1];
    int[] arrayOfInt = new int[1];
    if (!EGL14.eglChooseConfig(paramEGLDisplay, paramArrayOfInt, 0, arrayOfEGLConfig, 0, arrayOfEGLConfig.length, arrayOfInt, 0))
      throw new RuntimeException("eglChooseConfig failed");
    if (arrayOfInt[0] <= 0)
      throw new RuntimeException("Unable to find any matching EGL config");
    paramEGLDisplay = arrayOfEGLConfig[0];
    if (paramEGLDisplay == null)
      throw new RuntimeException("eglChooseConfig returned null");
    return paramEGLDisplay;
  }

  private static EGLDisplay getEglDisplay()
  {
    EGLDisplay localEGLDisplay = EGL14.eglGetDisplay(0);
    if (localEGLDisplay == EGL14.EGL_NO_DISPLAY)
      throw new RuntimeException("Unable to get EGL14 display");
    int[] arrayOfInt = new int[2];
    if (!EGL14.eglInitialize(localEGLDisplay, arrayOfInt, 0, arrayOfInt, 1))
      throw new RuntimeException("Unable to initialize EGL14");
    return localEGLDisplay;
  }

  public static boolean isEGL14Supported()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("SDK version: ").append(CURRENT_SDK_VERSION).append(". isEGL14Supported: ");
    if (CURRENT_SDK_VERSION >= 18);
    for (boolean bool = true; ; bool = false)
    {
      Logging.d("EglBase14", bool);
      if (CURRENT_SDK_VERSION < 18)
        break;
      return true;
    }
    return false;
  }

  public void createDummyPbufferSurface()
  {
    createPbufferSurface(1, 1);
  }

  public void createPbufferSurface(int paramInt1, int paramInt2)
  {
    checkIsNotReleased();
    if (this.eglSurface != EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("Already has an EGLSurface");
    this.eglSurface = EGL14.eglCreatePbufferSurface(this.eglDisplay, this.eglConfig, new int[] { 12375, paramInt1, 12374, paramInt2, 12344 }, 0);
    if (this.eglSurface == EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("Failed to create pixel buffer surface with size: " + paramInt1 + "x" + paramInt2);
  }

  public void createSurface(SurfaceTexture paramSurfaceTexture)
  {
    createSurfaceInternal(paramSurfaceTexture);
  }

  public void createSurface(Surface paramSurface)
  {
    createSurfaceInternal(paramSurface);
  }

  public void detachCurrent()
  {
    synchronized (EglBase.lock)
    {
      if (!EGL14.eglMakeCurrent(this.eglDisplay, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT))
        throw new RuntimeException("eglDetachCurrent failed");
    }
    monitorexit;
  }

  public Context getEglBaseContext()
  {
    return new Context(this.eglContext);
  }

  public boolean hasSurface()
  {
    return this.eglSurface != EGL14.EGL_NO_SURFACE;
  }

  public void makeCurrent()
  {
    checkIsNotReleased();
    if (this.eglSurface == EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("No EGLSurface - can't make current");
    synchronized (EglBase.lock)
    {
      if (!EGL14.eglMakeCurrent(this.eglDisplay, this.eglSurface, this.eglSurface, this.eglContext))
        throw new RuntimeException("eglMakeCurrent failed");
    }
    monitorexit;
  }

  public void release()
  {
    checkIsNotReleased();
    releaseSurface();
    detachCurrent();
    EGL14.eglDestroyContext(this.eglDisplay, this.eglContext);
    EGL14.eglReleaseThread();
    EGL14.eglTerminate(this.eglDisplay);
    this.eglContext = EGL14.EGL_NO_CONTEXT;
    this.eglDisplay = EGL14.EGL_NO_DISPLAY;
    this.eglConfig = null;
  }

  public void releaseSurface()
  {
    if (this.eglSurface != EGL14.EGL_NO_SURFACE)
    {
      EGL14.eglDestroySurface(this.eglDisplay, this.eglSurface);
      this.eglSurface = EGL14.EGL_NO_SURFACE;
    }
  }

  public int surfaceHeight()
  {
    int[] arrayOfInt = new int[1];
    EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12374, arrayOfInt, 0);
    return arrayOfInt[0];
  }

  public int surfaceWidth()
  {
    int[] arrayOfInt = new int[1];
    EGL14.eglQuerySurface(this.eglDisplay, this.eglSurface, 12375, arrayOfInt, 0);
    return arrayOfInt[0];
  }

  public void swapBuffers()
  {
    checkIsNotReleased();
    if (this.eglSurface == EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("No EGLSurface - can't swap buffers");
    synchronized (EglBase.lock)
    {
      EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
      return;
    }
  }

  public void swapBuffers(long paramLong)
  {
    checkIsNotReleased();
    if (this.eglSurface == EGL14.EGL_NO_SURFACE)
      throw new RuntimeException("No EGLSurface - can't swap buffers");
    synchronized (EglBase.lock)
    {
      EGLExt.eglPresentationTimeANDROID(this.eglDisplay, this.eglSurface, paramLong);
      EGL14.eglSwapBuffers(this.eglDisplay, this.eglSurface);
      return;
    }
  }

  public static class Context extends EglBase.Context
  {
    private final EGLContext egl14Context;

    public Context(EGLContext paramEGLContext)
    {
      this.egl14Context = paramEGLContext;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.EglBase14
 * JD-Core Version:    0.6.0
 */