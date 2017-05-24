package org.vidogram.messenger.exoplayer2.text;

import java.util.List;
import org.vidogram.messenger.exoplayer2.decoder.OutputBuffer;

public abstract class SubtitleOutputBuffer extends OutputBuffer
  implements Subtitle
{
  private long subsampleOffsetUs;
  private Subtitle subtitle;

  public void clear()
  {
    super.clear();
    this.subtitle = null;
  }

  public List<Cue> getCues(long paramLong)
  {
    return this.subtitle.getCues(paramLong - this.subsampleOffsetUs);
  }

  public long getEventTime(int paramInt)
  {
    return this.subtitle.getEventTime(paramInt) + this.subsampleOffsetUs;
  }

  public int getEventTimeCount()
  {
    return this.subtitle.getEventTimeCount();
  }

  public int getNextEventTimeIndex(long paramLong)
  {
    return this.subtitle.getNextEventTimeIndex(paramLong - this.subsampleOffsetUs);
  }

  public abstract void release();

  public void setContent(long paramLong1, Subtitle paramSubtitle, long paramLong2)
  {
    this.timeUs = paramLong1;
    this.subtitle = paramSubtitle;
    paramLong1 = paramLong2;
    if (paramLong2 == 9223372036854775807L)
      paramLong1 = this.timeUs;
    this.subsampleOffsetUs = paramLong1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SubtitleOutputBuffer
 * JD-Core Version:    0.6.0
 */