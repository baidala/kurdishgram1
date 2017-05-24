package org.webrtc;

public abstract interface CameraEnumerator
{
  public abstract CameraVideoCapturer createCapturer(String paramString, CameraVideoCapturer.CameraEventsHandler paramCameraEventsHandler);

  public abstract String[] getDeviceNames();

  public abstract boolean isBackFacing(String paramString);

  public abstract boolean isFrontFacing(String paramString);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.CameraEnumerator
 * JD-Core Version:    0.6.0
 */