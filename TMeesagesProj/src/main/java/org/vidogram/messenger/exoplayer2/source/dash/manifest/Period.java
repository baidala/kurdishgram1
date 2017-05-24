package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class Period
{
  public final List<AdaptationSet> adaptationSets;
  public final String id;
  public final long startMs;

  public Period(String paramString, long paramLong, List<AdaptationSet> paramList)
  {
    this.id = paramString;
    this.startMs = paramLong;
    this.adaptationSets = Collections.unmodifiableList(paramList);
  }

  public int getAdaptationSetIndex(int paramInt)
  {
    int j = this.adaptationSets.size();
    int i = 0;
    while (i < j)
    {
      if (((AdaptationSet)this.adaptationSets.get(i)).type == paramInt)
        return i;
      i += 1;
    }
    return -1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.Period
 * JD-Core Version:    0.6.0
 */