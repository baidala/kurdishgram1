package org.vidogram.messenger.exoplayer2.decoder;

public final class DecoderCounters
{
  public int decoderInitCount;
  public int decoderReleaseCount;
  public int droppedOutputBufferCount;
  public int inputBufferCount;
  public int maxConsecutiveDroppedOutputBufferCount;
  public int renderedOutputBufferCount;
  public int skippedOutputBufferCount;

  public void ensureUpdated()
  {
    monitorenter;
    monitorexit;
  }

  public void merge(DecoderCounters paramDecoderCounters)
  {
    this.decoderInitCount += paramDecoderCounters.decoderInitCount;
    this.decoderReleaseCount += paramDecoderCounters.decoderReleaseCount;
    this.inputBufferCount += paramDecoderCounters.inputBufferCount;
    this.renderedOutputBufferCount += paramDecoderCounters.renderedOutputBufferCount;
    this.skippedOutputBufferCount += paramDecoderCounters.skippedOutputBufferCount;
    this.droppedOutputBufferCount += paramDecoderCounters.droppedOutputBufferCount;
    this.maxConsecutiveDroppedOutputBufferCount = Math.max(this.maxConsecutiveDroppedOutputBufferCount, paramDecoderCounters.maxConsecutiveDroppedOutputBufferCount);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.decoder.DecoderCounters
 * JD-Core Version:    0.6.0
 */