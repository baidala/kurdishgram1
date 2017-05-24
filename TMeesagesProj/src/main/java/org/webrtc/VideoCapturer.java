package org.webrtc;

import android.content.Context;
import java.util.List;

public abstract interface VideoCapturer
{
  public abstract void changeCaptureFormat(int paramInt1, int paramInt2, int paramInt3);

  public abstract void dispose();

  public abstract List<CameraEnumerationAndroid.CaptureFormat> getSupportedFormats();

  public abstract void initialize(SurfaceTextureHelper paramSurfaceTextureHelper, Context paramContext, CapturerObserver paramCapturerObserver);

  public abstract void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3);

  public abstract void startCapture(int paramInt1, int paramInt2, int paramInt3);

  public abstract void stopCapture();

  public static class AndroidVideoTrackSourceObserver
    implements VideoCapturer.CapturerObserver
  {
    private final long nativeSource;

    public AndroidVideoTrackSourceObserver(long paramLong)
    {
      this.nativeSource = paramLong;
    }

    private native void nativeCapturerStarted(long paramLong, boolean paramBoolean);

    private native void nativeCapturerStopped(long paramLong);

    private native void nativeOnByteBufferFrameCaptured(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong2);

    private native void nativeOnOutputFormatRequest(long paramLong, int paramInt1, int paramInt2, int paramInt3);

    private native void nativeOnTextureFrameCaptured(long paramLong1, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, long paramLong2);

    public void onByteBufferFrameCaptured(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      nativeOnByteBufferFrameCaptured(this.nativeSource, paramArrayOfByte, paramArrayOfByte.length, paramInt1, paramInt2, paramInt3, paramLong);
    }

    public void onCapturerStarted(boolean paramBoolean)
    {
      nativeCapturerStarted(this.nativeSource, paramBoolean);
    }

    public void onCapturerStopped()
    {
      nativeCapturerStopped(this.nativeSource);
    }

    public void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3)
    {
      nativeOnOutputFormatRequest(this.nativeSource, paramInt1, paramInt2, paramInt3);
    }

    public void onTextureFrameCaptured(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, long paramLong)
    {
      nativeOnTextureFrameCaptured(this.nativeSource, paramInt1, paramInt2, paramInt3, paramArrayOfFloat, paramInt4, paramLong);
    }
  }

  public static abstract interface CapturerObserver
  {
    public abstract void onByteBufferFrameCaptured(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong);

    public abstract void onCapturerStarted(boolean paramBoolean);

    public abstract void onCapturerStopped();

    public abstract void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3);

    public abstract void onTextureFrameCaptured(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, long paramLong);
  }

  public static class NativeObserver
    implements VideoCapturer.CapturerObserver
  {
    private final long nativeCapturer;

    public NativeObserver(long paramLong)
    {
      this.nativeCapturer = paramLong;
    }

    private native void nativeCapturerStarted(long paramLong, boolean paramBoolean);

    private native void nativeOnByteBufferFrameCaptured(long paramLong1, byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4, long paramLong2);

    private native void nativeOnOutputFormatRequest(long paramLong, int paramInt1, int paramInt2, int paramInt3);

    private native void nativeOnTextureFrameCaptured(long paramLong1, int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, long paramLong2);

    public void onByteBufferFrameCaptured(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, long paramLong)
    {
      nativeOnByteBufferFrameCaptured(this.nativeCapturer, paramArrayOfByte, paramArrayOfByte.length, paramInt1, paramInt2, paramInt3, paramLong);
    }

    public void onCapturerStarted(boolean paramBoolean)
    {
      nativeCapturerStarted(this.nativeCapturer, paramBoolean);
    }

    public void onCapturerStopped()
    {
    }

    public void onOutputFormatRequest(int paramInt1, int paramInt2, int paramInt3)
    {
      nativeOnOutputFormatRequest(this.nativeCapturer, paramInt1, paramInt2, paramInt3);
    }

    public void onTextureFrameCaptured(int paramInt1, int paramInt2, int paramInt3, float[] paramArrayOfFloat, int paramInt4, long paramLong)
    {
      nativeOnTextureFrameCaptured(this.nativeCapturer, paramInt1, paramInt2, paramInt3, paramArrayOfFloat, paramInt4, paramLong);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoCapturer
 * JD-Core Version:    0.6.0
 */