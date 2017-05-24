package org.webrtc;

import android.opengl.GLES20;

public class GlTextureFrameBuffer
{
  private final int frameBufferId;
  private int height;
  private final int pixelFormat;
  private final int textureId;
  private int width;

  public GlTextureFrameBuffer(int paramInt)
  {
    switch (paramInt)
    {
    default:
      throw new IllegalArgumentException("Invalid pixel format: " + paramInt);
    case 6407:
    case 6408:
    case 6409:
    }
    this.pixelFormat = paramInt;
    this.textureId = GlUtil.generateTexture(3553);
    this.width = 0;
    this.height = 0;
    int[] arrayOfInt = new int[1];
    GLES20.glGenFramebuffers(1, arrayOfInt, 0);
    this.frameBufferId = arrayOfInt[0];
    GLES20.glBindFramebuffer(36160, this.frameBufferId);
    GlUtil.checkNoGLES2Error("Generate framebuffer");
    GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
    GlUtil.checkNoGLES2Error("Attach texture to framebuffer");
    GLES20.glBindFramebuffer(36160, 0);
  }

  public int getFrameBufferId()
  {
    return this.frameBufferId;
  }

  public int getHeight()
  {
    return this.height;
  }

  public int getTextureId()
  {
    return this.textureId;
  }

  public int getWidth()
  {
    return this.width;
  }

  public void release()
  {
    GLES20.glDeleteTextures(1, new int[] { this.textureId }, 0);
    GLES20.glDeleteFramebuffers(1, new int[] { this.frameBufferId }, 0);
    this.width = 0;
    this.height = 0;
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    if ((paramInt1 == 0) || (paramInt2 == 0))
      throw new IllegalArgumentException("Invalid size: " + paramInt1 + "x" + paramInt2);
    if ((paramInt1 == this.width) && (paramInt2 == this.height))
      return;
    this.width = paramInt1;
    this.height = paramInt2;
    GLES20.glBindFramebuffer(36160, this.frameBufferId);
    GlUtil.checkNoGLES2Error("glBindFramebuffer");
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(3553, this.textureId);
    GLES20.glTexImage2D(3553, 0, this.pixelFormat, paramInt1, paramInt2, 0, this.pixelFormat, 5121, null);
    paramInt1 = GLES20.glCheckFramebufferStatus(36160);
    if (paramInt1 != 36053)
      throw new IllegalStateException("Framebuffer not complete, status: " + paramInt1);
    GLES20.glBindFramebuffer(36160, 0);
    GLES20.glBindTexture(3553, 0);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.GlTextureFrameBuffer
 * JD-Core Version:    0.6.0
 */