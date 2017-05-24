package org.vidogram.messenger.exoplayer2.decoder;

import java.nio.ByteBuffer;

public class SimpleOutputBuffer extends OutputBuffer
{
  public ByteBuffer data;
  private final SimpleDecoder<?, SimpleOutputBuffer, ?> owner;

  public SimpleOutputBuffer(SimpleDecoder<?, SimpleOutputBuffer, ?> paramSimpleDecoder)
  {
    this.owner = paramSimpleDecoder;
  }

  public void clear()
  {
    super.clear();
    if (this.data != null)
      this.data.clear();
  }

  public ByteBuffer init(long paramLong, int paramInt)
  {
    this.timeUs = paramLong;
    if ((this.data == null) || (this.data.capacity() < paramInt))
      this.data = ByteBuffer.allocateDirect(paramInt);
    this.data.position(0);
    this.data.limit(paramInt);
    return this.data;
  }

  public void release()
  {
    this.owner.releaseOutputBuffer(this);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.decoder.SimpleOutputBuffer
 * JD-Core Version:    0.6.0
 */