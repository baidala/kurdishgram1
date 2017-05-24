package org.vidogram.messenger.exoplayer2.metadata.id3;

import org.vidogram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public abstract class Id3Frame
  implements Metadata.Entry
{
  public final String id;

  public Id3Frame(String paramString)
  {
    this.id = ((String)Assertions.checkNotNull(paramString));
  }

  public int describeContents()
  {
    return 0;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.Id3Frame
 * JD-Core Version:    0.6.0
 */