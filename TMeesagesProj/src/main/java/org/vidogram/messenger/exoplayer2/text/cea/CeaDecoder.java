package org.vidogram.messenger.exoplayer2.text.cea;

import java.util.LinkedList;
import java.util.TreeSet;
import org.vidogram.messenger.exoplayer2.text.Subtitle;
import org.vidogram.messenger.exoplayer2.text.SubtitleDecoder;
import org.vidogram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.vidogram.messenger.exoplayer2.text.SubtitleOutputBuffer;
import org.vidogram.messenger.exoplayer2.util.Assertions;

abstract class CeaDecoder
  implements SubtitleDecoder
{
  private static final int NUM_INPUT_BUFFERS = 10;
  private static final int NUM_OUTPUT_BUFFERS = 2;
  private final LinkedList<SubtitleInputBuffer> availableInputBuffers = new LinkedList();
  private final LinkedList<SubtitleOutputBuffer> availableOutputBuffers;
  private SubtitleInputBuffer dequeuedInputBuffer;
  private long playbackPositionUs;
  private final TreeSet<SubtitleInputBuffer> queuedInputBuffers;

  public CeaDecoder()
  {
    int i = 0;
    while (i < 10)
    {
      this.availableInputBuffers.add(new SubtitleInputBuffer());
      i += 1;
    }
    this.availableOutputBuffers = new LinkedList();
    i = j;
    while (i < 2)
    {
      this.availableOutputBuffers.add(new CeaOutputBuffer(this));
      i += 1;
    }
    this.queuedInputBuffers = new TreeSet();
  }

  private void releaseInputBuffer(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    paramSubtitleInputBuffer.clear();
    this.availableInputBuffers.add(paramSubtitleInputBuffer);
  }

  protected abstract Subtitle createSubtitle();

  protected abstract void decode(SubtitleInputBuffer paramSubtitleInputBuffer);

  public SubtitleInputBuffer dequeueInputBuffer()
  {
    if (this.dequeuedInputBuffer == null);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      if (!this.availableInputBuffers.isEmpty())
        break;
      return null;
    }
    this.dequeuedInputBuffer = ((SubtitleInputBuffer)this.availableInputBuffers.pollFirst());
    return this.dequeuedInputBuffer;
  }

  public SubtitleOutputBuffer dequeueOutputBuffer()
  {
    if (this.availableOutputBuffers.isEmpty())
      return null;
    while (true)
    {
      releaseInputBuffer(localSubtitleInputBuffer);
      if ((this.queuedInputBuffers.isEmpty()) || (((SubtitleInputBuffer)this.queuedInputBuffers.first()).timeUs > this.playbackPositionUs))
        break;
      SubtitleInputBuffer localSubtitleInputBuffer = (SubtitleInputBuffer)this.queuedInputBuffers.pollFirst();
      if (localSubtitleInputBuffer.isEndOfStream())
      {
        localObject = (SubtitleOutputBuffer)this.availableOutputBuffers.pollFirst();
        ((SubtitleOutputBuffer)localObject).addFlag(4);
        releaseInputBuffer(localSubtitleInputBuffer);
        return localObject;
      }
      decode(localSubtitleInputBuffer);
      if (!isNewSubtitleDataAvailable())
        continue;
      Object localObject = createSubtitle();
      if (localSubtitleInputBuffer.isDecodeOnly())
        continue;
      SubtitleOutputBuffer localSubtitleOutputBuffer = (SubtitleOutputBuffer)this.availableOutputBuffers.pollFirst();
      localSubtitleOutputBuffer.setContent(localSubtitleInputBuffer.timeUs, (Subtitle)localObject, 0L);
      releaseInputBuffer(localSubtitleInputBuffer);
      return localSubtitleOutputBuffer;
    }
    return (SubtitleOutputBuffer)null;
  }

  public void flush()
  {
    this.playbackPositionUs = 0L;
    while (!this.queuedInputBuffers.isEmpty())
      releaseInputBuffer((SubtitleInputBuffer)this.queuedInputBuffers.pollFirst());
    if (this.dequeuedInputBuffer != null)
    {
      releaseInputBuffer(this.dequeuedInputBuffer);
      this.dequeuedInputBuffer = null;
    }
  }

  public abstract String getName();

  protected abstract boolean isNewSubtitleDataAvailable();

  public void queueInputBuffer(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    boolean bool2 = true;
    if (paramSubtitleInputBuffer != null)
    {
      bool1 = true;
      Assertions.checkArgument(bool1);
      if (paramSubtitleInputBuffer != this.dequeuedInputBuffer)
        break label46;
    }
    label46: for (boolean bool1 = bool2; ; bool1 = false)
    {
      Assertions.checkArgument(bool1);
      this.queuedInputBuffers.add(paramSubtitleInputBuffer);
      this.dequeuedInputBuffer = null;
      return;
      bool1 = false;
      break;
    }
  }

  public void release()
  {
  }

  protected void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    paramSubtitleOutputBuffer.clear();
    this.availableOutputBuffers.add(paramSubtitleOutputBuffer);
  }

  public void setPositionUs(long paramLong)
  {
    this.playbackPositionUs = paramLong;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.cea.CeaDecoder
 * JD-Core Version:    0.6.0
 */