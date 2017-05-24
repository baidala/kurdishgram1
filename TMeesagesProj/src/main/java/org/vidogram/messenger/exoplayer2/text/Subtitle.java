package org.vidogram.messenger.exoplayer2.text;

import java.util.List;

public abstract interface Subtitle
{
  public abstract List<Cue> getCues(long paramLong);

  public abstract long getEventTime(int paramInt);

  public abstract int getEventTimeCount();

  public abstract int getNextEventTimeIndex(long paramLong);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.Subtitle
 * JD-Core Version:    0.6.0
 */