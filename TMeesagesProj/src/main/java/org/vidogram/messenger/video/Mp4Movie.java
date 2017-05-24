package org.vidogram.messenger.video;

import android.annotation.TargetApi;
import android.media.MediaCodec.BufferInfo;
import android.media.MediaFormat;
import com.googlecode.mp4parser.c.g;
import java.io.File;
import java.util.ArrayList;

@TargetApi(16)
public class Mp4Movie
{
  private File cacheFile;
  private int height;
  private g matrix = g.j;
  private ArrayList<Track> tracks = new ArrayList();
  private int width;

  public void addSample(int paramInt, long paramLong, MediaCodec.BufferInfo paramBufferInfo)
  {
    if ((paramInt < 0) || (paramInt >= this.tracks.size()))
      return;
    ((Track)this.tracks.get(paramInt)).addSample(paramLong, paramBufferInfo);
  }

  public int addTrack(MediaFormat paramMediaFormat, boolean paramBoolean)
  {
    this.tracks.add(new Track(this.tracks.size(), paramMediaFormat, paramBoolean));
    return this.tracks.size() - 1;
  }

  public File getCacheFile()
  {
    return this.cacheFile;
  }

  public int getHeight()
  {
    return this.height;
  }

  public g getMatrix()
  {
    return this.matrix;
  }

  public ArrayList<Track> getTracks()
  {
    return this.tracks;
  }

  public int getWidth()
  {
    return this.width;
  }

  public void setCacheFile(File paramFile)
  {
    this.cacheFile = paramFile;
  }

  public void setRotation(int paramInt)
  {
    if (paramInt == 0)
      this.matrix = g.j;
    do
    {
      return;
      if (paramInt == 90)
      {
        this.matrix = g.k;
        return;
      }
      if (paramInt != 180)
        continue;
      this.matrix = g.l;
      return;
    }
    while (paramInt != 270);
    this.matrix = g.m;
  }

  public void setSize(int paramInt1, int paramInt2)
  {
    this.width = paramInt1;
    this.height = paramInt2;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.video.Mp4Movie
 * JD-Core Version:    0.6.0
 */