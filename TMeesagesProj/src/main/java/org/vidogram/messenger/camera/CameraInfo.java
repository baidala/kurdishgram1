package org.vidogram.messenger.camera;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import java.util.ArrayList;

public class CameraInfo
{
  protected Camera camera;
  protected int cameraId;
  protected final int frontCamera;
  protected ArrayList<Size> pictureSizes = new ArrayList();
  protected ArrayList<Size> previewSizes = new ArrayList();

  public CameraInfo(int paramInt, Camera.CameraInfo paramCameraInfo)
  {
    this.cameraId = paramInt;
    this.frontCamera = paramCameraInfo.facing;
  }

  private Camera getCamera()
  {
    return this.camera;
  }

  public int getCameraId()
  {
    return this.cameraId;
  }

  public ArrayList<Size> getPictureSizes()
  {
    return this.pictureSizes;
  }

  public ArrayList<Size> getPreviewSizes()
  {
    return this.previewSizes;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.camera.CameraInfo
 * JD-Core Version:    0.6.0
 */