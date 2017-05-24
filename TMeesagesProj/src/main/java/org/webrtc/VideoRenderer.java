package org.webrtc;

import java.nio.ByteBuffer;

public class VideoRenderer
{
  long nativeVideoRenderer;

  public VideoRenderer(Callbacks paramCallbacks)
  {
    this.nativeVideoRenderer = nativeWrapVideoRenderer(paramCallbacks);
  }

  private static native void freeWrappedVideoRenderer(long paramLong);

  public static native void nativeCopyPlane(ByteBuffer paramByteBuffer1, int paramInt1, int paramInt2, int paramInt3, ByteBuffer paramByteBuffer2, int paramInt4);

  private static native long nativeWrapVideoRenderer(Callbacks paramCallbacks);

  private static native void releaseNativeFrame(long paramLong);

  public static void renderFrameDone(I420Frame paramI420Frame)
  {
    paramI420Frame.yuvPlanes = null;
    paramI420Frame.textureId = 0;
    if (paramI420Frame.nativeFramePointer != 0L)
    {
      releaseNativeFrame(paramI420Frame.nativeFramePointer);
      I420Frame.access$002(paramI420Frame, 0L);
    }
  }

  public void dispose()
  {
    if (this.nativeVideoRenderer == 0L)
      return;
    freeWrappedVideoRenderer(this.nativeVideoRenderer);
    this.nativeVideoRenderer = 0L;
  }

  public static abstract interface Callbacks
  {
    public abstract void renderFrame(VideoRenderer.I420Frame paramI420Frame);
  }

  public static class I420Frame
  {
    public final int height;
    private long nativeFramePointer;
    public int rotationDegree;
    public final float[] samplingMatrix;
    public int textureId;
    public final int width;
    public final boolean yuvFrame;
    public ByteBuffer[] yuvPlanes;
    public final int[] yuvStrides;

    I420Frame(int paramInt1, int paramInt2, int paramInt3, int paramInt4, float[] paramArrayOfFloat, long paramLong)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.yuvStrides = null;
      this.yuvPlanes = null;
      this.samplingMatrix = paramArrayOfFloat;
      this.textureId = paramInt4;
      this.yuvFrame = false;
      this.rotationDegree = paramInt3;
      this.nativeFramePointer = paramLong;
      if (paramInt3 % 90 != 0)
        throw new IllegalArgumentException("Rotation degree not multiple of 90: " + paramInt3);
    }

    I420Frame(int paramInt1, int paramInt2, int paramInt3, int[] paramArrayOfInt, ByteBuffer[] paramArrayOfByteBuffer, long paramLong)
    {
      this.width = paramInt1;
      this.height = paramInt2;
      this.yuvStrides = paramArrayOfInt;
      this.yuvPlanes = paramArrayOfByteBuffer;
      this.yuvFrame = true;
      this.rotationDegree = paramInt3;
      this.nativeFramePointer = paramLong;
      if (paramInt3 % 90 != 0)
        throw new IllegalArgumentException("Rotation degree not multiple of 90: " + paramInt3);
      this.samplingMatrix = new float[] { 1.0F, 0.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F };
    }

    public int rotatedHeight()
    {
      if (this.rotationDegree % 180 == 0)
        return this.height;
      return this.width;
    }

    public int rotatedWidth()
    {
      if (this.rotationDegree % 180 == 0)
        return this.width;
      return this.height;
    }

    public String toString()
    {
      return this.width + "x" + this.height + ":" + this.yuvStrides[0] + ":" + this.yuvStrides[1] + ":" + this.yuvStrides[2];
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoRenderer
 * JD-Core Version:    0.6.0
 */