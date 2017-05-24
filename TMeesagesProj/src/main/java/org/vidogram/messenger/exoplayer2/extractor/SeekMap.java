package org.vidogram.messenger.exoplayer2.extractor;

public abstract interface SeekMap
{
  public abstract long getDurationUs();

  public abstract long getPosition(long paramLong);

  public abstract boolean isSeekable();

  public static final class Unseekable
    implements SeekMap
  {
    private final long durationUs;

    public Unseekable(long paramLong)
    {
      this.durationUs = paramLong;
    }

    public long getDurationUs()
    {
      return this.durationUs;
    }

    public long getPosition(long paramLong)
    {
      return 0L;
    }

    public boolean isSeekable()
    {
      return false;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.SeekMap
 * JD-Core Version:    0.6.0
 */