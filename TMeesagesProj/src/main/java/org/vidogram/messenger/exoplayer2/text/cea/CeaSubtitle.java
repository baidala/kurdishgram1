package org.vidogram.messenger.exoplayer2.text.cea;

import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.Subtitle;

final class CeaSubtitle
  implements Subtitle
{
  private final List<Cue> cues;

  public CeaSubtitle(Cue paramCue)
  {
    if (paramCue == null)
    {
      this.cues = Collections.emptyList();
      return;
    }
    this.cues = Collections.singletonList(paramCue);
  }

  public List<Cue> getCues(long paramLong)
  {
    return this.cues;
  }

  public long getEventTime(int paramInt)
  {
    return 0L;
  }

  public int getEventTimeCount()
  {
    return 1;
  }

  public int getNextEventTimeIndex(long paramLong)
  {
    return 0;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.cea.CeaSubtitle
 * JD-Core Version:    0.6.0
 */