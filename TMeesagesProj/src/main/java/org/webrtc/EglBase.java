package org.webrtc;

import android.graphics.SurfaceTexture;
import android.view.Surface;

public abstract class EglBase
{
  public static final int[] CONFIG_PIXEL_BUFFER;
  public static final int[] CONFIG_PIXEL_RGBA_BUFFER;
  public static final int[] CONFIG_PLAIN;
  public static final int[] CONFIG_RECORDABLE;
  public static final int[] CONFIG_RGBA;
  private static final int EGL_OPENGL_ES2_BIT = 4;
  private static final int EGL_RECORDABLE_ANDROID = 12610;
  public static final Object lock = new Object();

  static
  {
    CONFIG_PLAIN = new int[] { 12324, 8, 12323, 8, 12322, 8, 12352, 4, 12344 };
    CONFIG_RGBA = new int[] { 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12344 };
    CONFIG_PIXEL_BUFFER = new int[] { 12324, 8, 12323, 8, 12322, 8, 12352, 4, 12339, 1, 12344 };
    CONFIG_PIXEL_RGBA_BUFFER = new int[] { 12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, 12339, 1, 12344 };
    CONFIG_RECORDABLE = new int[] { 12324, 8, 12323, 8, 12322, 8, 12352, 4, 12610, 1, 12344 };
  }

  public static EglBase create()
  {
    return create(null, CONFIG_PLAIN);
  }

  public static EglBase create(Context paramContext)
  {
    return create(paramContext, CONFIG_PLAIN);
  }

  public static EglBase create(Context paramContext, int[] paramArrayOfInt)
  {
    if ((EglBase14.isEGL14Supported()) && ((paramContext == null) || ((paramContext instanceof EglBase14.Context))))
      return new EglBase14((EglBase14.Context)paramContext, paramArrayOfInt);
    return new EglBase10((EglBase10.Context)paramContext, paramArrayOfInt);
  }

  public abstract void createDummyPbufferSurface();

  public abstract void createPbufferSurface(int paramInt1, int paramInt2);

  public abstract void createSurface(SurfaceTexture paramSurfaceTexture);

  public abstract void createSurface(Surface paramSurface);

  public abstract void detachCurrent();

  public abstract Context getEglBaseContext();

  public abstract boolean hasSurface();

  public abstract void makeCurrent();

  public abstract void release();

  public abstract void releaseSurface();

  public abstract int surfaceHeight();

  public abstract int surfaceWidth();

  public abstract void swapBuffers();

  public static class Context
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.EglBase
 * JD-Core Version:    0.6.0
 */