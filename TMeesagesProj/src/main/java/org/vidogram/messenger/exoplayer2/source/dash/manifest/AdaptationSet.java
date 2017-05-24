package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import java.util.Collections;
import java.util.List;

public class AdaptationSet
{
  public static final int UNSET_ID = -1;
  public final int id;
  public final List<Representation> representations;
  public final int type;

  public AdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList)
  {
    this.id = paramInt1;
    this.type = paramInt2;
    this.representations = Collections.unmodifiableList(paramList);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.AdaptationSet
 * JD-Core Version:    0.6.0
 */