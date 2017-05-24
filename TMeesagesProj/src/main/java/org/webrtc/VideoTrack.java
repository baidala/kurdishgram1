package org.webrtc;

import java.util.LinkedList;

public class VideoTrack extends MediaStreamTrack
{
  private final LinkedList<VideoRenderer> renderers = new LinkedList();

  public VideoTrack(long paramLong)
  {
    super(paramLong);
  }

  private static native void free(long paramLong);

  private static native void nativeAddRenderer(long paramLong1, long paramLong2);

  private static native void nativeRemoveRenderer(long paramLong1, long paramLong2);

  public void addRenderer(VideoRenderer paramVideoRenderer)
  {
    this.renderers.add(paramVideoRenderer);
    nativeAddRenderer(this.nativeTrack, paramVideoRenderer.nativeVideoRenderer);
  }

  public void dispose()
  {
    while (!this.renderers.isEmpty())
      removeRenderer((VideoRenderer)this.renderers.getFirst());
    super.dispose();
  }

  public void removeRenderer(VideoRenderer paramVideoRenderer)
  {
    if (!this.renderers.remove(paramVideoRenderer))
      return;
    nativeRemoveRenderer(this.nativeTrack, paramVideoRenderer.nativeVideoRenderer);
    paramVideoRenderer.dispose();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.VideoTrack
 * JD-Core Version:    0.6.0
 */