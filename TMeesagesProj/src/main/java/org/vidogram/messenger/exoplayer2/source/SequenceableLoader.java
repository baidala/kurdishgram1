package org.vidogram.messenger.exoplayer2.source;

public abstract interface SequenceableLoader
{
  public abstract boolean continueLoading(long paramLong);

  public abstract long getNextLoadPositionUs();

  public static abstract interface Callback<T extends SequenceableLoader>
  {
    public abstract void onContinueLoadingRequested(T paramT);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.SequenceableLoader
 * JD-Core Version:    0.6.0
 */