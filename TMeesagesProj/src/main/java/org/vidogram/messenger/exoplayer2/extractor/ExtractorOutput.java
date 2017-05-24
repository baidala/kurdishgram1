package org.vidogram.messenger.exoplayer2.extractor;

public abstract interface ExtractorOutput
{
  public abstract void endTracks();

  public abstract void seekMap(SeekMap paramSeekMap);

  public abstract TrackOutput track(int paramInt);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput
 * JD-Core Version:    0.6.0
 */