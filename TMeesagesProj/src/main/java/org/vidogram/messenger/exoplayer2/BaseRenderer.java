package org.vidogram.messenger.exoplayer2;

import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.source.SampleStream;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.MediaClock;

public abstract class BaseRenderer
  implements Renderer, RendererCapabilities
{
  private int index;
  private boolean readEndOfStream;
  private int state;
  private SampleStream stream;
  private boolean streamIsFinal;
  private long streamOffsetUs;
  private final int trackType;

  public BaseRenderer(int paramInt)
  {
    this.trackType = paramInt;
    this.readEndOfStream = true;
  }

  public final void disable()
  {
    boolean bool = true;
    if (this.state == 1);
    while (true)
    {
      Assertions.checkState(bool);
      this.state = 0;
      onDisabled();
      this.stream = null;
      this.streamIsFinal = false;
      return;
      bool = false;
    }
  }

  public final void enable(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong1, boolean paramBoolean, long paramLong2)
  {
    if (this.state == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      onEnabled(paramBoolean);
      replaceStream(paramArrayOfFormat, paramSampleStream, paramLong2);
      onPositionReset(paramLong1, paramBoolean);
      return;
    }
  }

  public final RendererCapabilities getCapabilities()
  {
    return this;
  }

  protected final int getIndex()
  {
    return this.index;
  }

  public MediaClock getMediaClock()
  {
    return null;
  }

  public final int getState()
  {
    return this.state;
  }

  public final SampleStream getStream()
  {
    return this.stream;
  }

  public final int getTrackType()
  {
    return this.trackType;
  }

  public void handleMessage(int paramInt, Object paramObject)
  {
  }

  public final boolean hasReadStreamToEnd()
  {
    return this.readEndOfStream;
  }

  protected final boolean isSourceReady()
  {
    if (this.readEndOfStream)
      return this.streamIsFinal;
    return this.stream.isReady();
  }

  public final void maybeThrowStreamError()
  {
    this.stream.maybeThrowError();
  }

  protected void onDisabled()
  {
  }

  protected void onEnabled(boolean paramBoolean)
  {
  }

  protected void onPositionReset(long paramLong, boolean paramBoolean)
  {
  }

  protected void onStarted()
  {
  }

  protected void onStopped()
  {
  }

  protected void onStreamChanged(Format[] paramArrayOfFormat)
  {
  }

  protected final int readSource(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    int i = this.stream.readData(paramFormatHolder, paramDecoderInputBuffer);
    if (i == -4)
    {
      if (paramDecoderInputBuffer.isEndOfStream())
      {
        this.readEndOfStream = true;
        if (this.streamIsFinal)
          return -4;
        return -3;
      }
      paramDecoderInputBuffer.timeUs += this.streamOffsetUs;
    }
    return i;
  }

  public final void replaceStream(Format[] paramArrayOfFormat, SampleStream paramSampleStream, long paramLong)
  {
    if (!this.streamIsFinal);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.stream = paramSampleStream;
      this.readEndOfStream = false;
      this.streamOffsetUs = paramLong;
      onStreamChanged(paramArrayOfFormat);
      return;
    }
  }

  public final void resetPosition(long paramLong)
  {
    this.streamIsFinal = false;
    onPositionReset(paramLong, false);
  }

  public final void setCurrentStreamIsFinal()
  {
    this.streamIsFinal = true;
  }

  public final void setIndex(int paramInt)
  {
    this.index = paramInt;
  }

  protected void skipToKeyframeBefore(long paramLong)
  {
    this.stream.skipToKeyframeBefore(paramLong);
  }

  public final void start()
  {
    boolean bool = true;
    if (this.state == 1);
    while (true)
    {
      Assertions.checkState(bool);
      this.state = 2;
      onStarted();
      return;
      bool = false;
    }
  }

  public final void stop()
  {
    if (this.state == 2);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      this.state = 1;
      onStopped();
      return;
    }
  }

  public int supportsMixedMimeTypeAdaptation()
  {
    return 0;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.BaseRenderer
 * JD-Core Version:    0.6.0
 */