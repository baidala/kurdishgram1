package org.vidogram.messenger.exoplayer2.text;

import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;

public final class SubtitleInputBuffer extends DecoderInputBuffer
  implements Comparable<SubtitleInputBuffer>
{
  public long subsampleOffsetUs;

  public SubtitleInputBuffer()
  {
    super(1);
  }

  public int compareTo(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    long l = this.timeUs - paramSubtitleInputBuffer.timeUs;
    if (l == 0L)
      return 0;
    if (l > 0L)
      return 1;
    return -1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SubtitleInputBuffer
 * JD-Core Version:    0.6.0
 */