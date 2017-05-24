package org.webrtc;

import java.util.LinkedList;

public class MediaStream
{
  public final LinkedList<AudioTrack> audioTracks = new LinkedList();
  final long nativeStream;
  public final LinkedList<VideoTrack> preservedVideoTracks = new LinkedList();
  public final LinkedList<VideoTrack> videoTracks = new LinkedList();

  public MediaStream(long paramLong)
  {
    this.nativeStream = paramLong;
  }

  private static native void free(long paramLong);

  private static native boolean nativeAddAudioTrack(long paramLong1, long paramLong2);

  private static native boolean nativeAddVideoTrack(long paramLong1, long paramLong2);

  private static native String nativeLabel(long paramLong);

  private static native boolean nativeRemoveAudioTrack(long paramLong1, long paramLong2);

  private static native boolean nativeRemoveVideoTrack(long paramLong1, long paramLong2);

  public boolean addPreservedTrack(VideoTrack paramVideoTrack)
  {
    if (nativeAddVideoTrack(this.nativeStream, paramVideoTrack.nativeTrack))
    {
      this.preservedVideoTracks.add(paramVideoTrack);
      return true;
    }
    return false;
  }

  public boolean addTrack(AudioTrack paramAudioTrack)
  {
    if (nativeAddAudioTrack(this.nativeStream, paramAudioTrack.nativeTrack))
    {
      this.audioTracks.add(paramAudioTrack);
      return true;
    }
    return false;
  }

  public boolean addTrack(VideoTrack paramVideoTrack)
  {
    if (nativeAddVideoTrack(this.nativeStream, paramVideoTrack.nativeTrack))
    {
      this.videoTracks.add(paramVideoTrack);
      return true;
    }
    return false;
  }

  public void dispose()
  {
    Object localObject;
    while (!this.audioTracks.isEmpty())
    {
      localObject = (AudioTrack)this.audioTracks.getFirst();
      removeTrack((AudioTrack)localObject);
      ((AudioTrack)localObject).dispose();
    }
    while (!this.videoTracks.isEmpty())
    {
      localObject = (VideoTrack)this.videoTracks.getFirst();
      removeTrack((VideoTrack)localObject);
      ((VideoTrack)localObject).dispose();
    }
    while (!this.preservedVideoTracks.isEmpty())
      removeTrack((VideoTrack)this.preservedVideoTracks.getFirst());
    free(this.nativeStream);
  }

  public String label()
  {
    return nativeLabel(this.nativeStream);
  }

  public boolean removeTrack(AudioTrack paramAudioTrack)
  {
    this.audioTracks.remove(paramAudioTrack);
    return nativeRemoveAudioTrack(this.nativeStream, paramAudioTrack.nativeTrack);
  }

  public boolean removeTrack(VideoTrack paramVideoTrack)
  {
    this.videoTracks.remove(paramVideoTrack);
    this.preservedVideoTracks.remove(paramVideoTrack);
    return nativeRemoveVideoTrack(this.nativeStream, paramVideoTrack.nativeTrack);
  }

  public String toString()
  {
    return "[" + label() + ":A=" + this.audioTracks.size() + ":V=" + this.videoTracks.size() + "]";
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.MediaStream
 * JD-Core Version:    0.6.0
 */