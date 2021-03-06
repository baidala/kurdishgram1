package org.vidogram.messenger.exoplayer2.text.webvtt;

import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.Subtitle;
import org.vidogram.messenger.exoplayer2.util.Assertions;

final class Mp4WebvttSubtitle
  implements Subtitle
{
  private final List<Cue> cues;

  public Mp4WebvttSubtitle(List<Cue> paramList)
  {
    this.cues = Collections.unmodifiableList(paramList);
  }

  public List<Cue> getCues(long paramLong)
  {
    if (paramLong >= 0L)
      return this.cues;
    return Collections.emptyList();
  }

  public long getEventTime(int paramInt)
  {
    if (paramInt == 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      return 0L;
    }
  }

  public int getEventTimeCount()
  {
    return 1;
  }

  public int getNextEventTimeIndex(long paramLong)
  {
    if (paramLong < 0L)
      return 0;
    return -1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.webvtt.Mp4WebvttSubtitle
 * JD-Core Version:    0.6.0
 */