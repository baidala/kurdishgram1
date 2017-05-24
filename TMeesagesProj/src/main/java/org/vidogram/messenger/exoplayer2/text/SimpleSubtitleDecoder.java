package org.vidogram.messenger.exoplayer2.text;

import java.nio.ByteBuffer;
import org.vidogram.messenger.exoplayer2.decoder.SimpleDecoder;

public abstract class SimpleSubtitleDecoder extends SimpleDecoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException>
  implements SubtitleDecoder
{
  private final String name;

  protected SimpleSubtitleDecoder(String paramString)
  {
    super(new SubtitleInputBuffer[2], new SubtitleOutputBuffer[2]);
    this.name = paramString;
    setInitialInputBufferSize(1024);
  }

  protected final SubtitleInputBuffer createInputBuffer()
  {
    return new SubtitleInputBuffer();
  }

  protected final SubtitleOutputBuffer createOutputBuffer()
  {
    return new SimpleSubtitleOutputBuffer(this);
  }

  protected abstract Subtitle decode(byte[] paramArrayOfByte, int paramInt);

  protected final SubtitleDecoderException decode(SubtitleInputBuffer paramSubtitleInputBuffer, SubtitleOutputBuffer paramSubtitleOutputBuffer, boolean paramBoolean)
  {
    try
    {
      Object localObject = paramSubtitleInputBuffer.data;
      localObject = decode(((ByteBuffer)localObject).array(), ((ByteBuffer)localObject).limit());
      paramSubtitleOutputBuffer.setContent(paramSubtitleInputBuffer.timeUs, (Subtitle)localObject, paramSubtitleInputBuffer.subsampleOffsetUs);
      return null;
    }
    catch (SubtitleDecoderException paramSubtitleInputBuffer)
    {
    }
    return (SubtitleDecoderException)paramSubtitleInputBuffer;
  }

  public final String getName()
  {
    return this.name;
  }

  protected final void releaseOutputBuffer(SubtitleOutputBuffer paramSubtitleOutputBuffer)
  {
    super.releaseOutputBuffer(paramSubtitleOutputBuffer);
  }

  public void setPositionUs(long paramLong)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SimpleSubtitleDecoder
 * JD-Core Version:    0.6.0
 */