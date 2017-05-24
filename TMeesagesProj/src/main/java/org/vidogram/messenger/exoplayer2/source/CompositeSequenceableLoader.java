package org.vidogram.messenger.exoplayer2.source;

public final class CompositeSequenceableLoader
  implements SequenceableLoader
{
  private final SequenceableLoader[] loaders;

  public CompositeSequenceableLoader(SequenceableLoader[] paramArrayOfSequenceableLoader)
  {
    this.loaders = paramArrayOfSequenceableLoader;
  }

  public boolean continueLoading(long paramLong)
  {
    int k = 0;
    boolean bool1;
    int m;
    do
    {
      long l = getNextLoadPositionUs();
      if (l == -9223372036854775808L)
        return k;
      SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
      int j = arrayOfSequenceableLoader.length;
      int i = 0;
      boolean bool2;
      for (bool1 = false; i < j; bool1 = bool2)
      {
        SequenceableLoader localSequenceableLoader = arrayOfSequenceableLoader[i];
        bool2 = bool1;
        if (localSequenceableLoader.getNextLoadPositionUs() == l)
          bool2 = bool1 | localSequenceableLoader.continueLoading(paramLong);
        i += 1;
      }
      m = k | bool1;
      k = m;
    }
    while (bool1);
    return m;
  }

  public long getNextLoadPositionUs()
  {
    SequenceableLoader[] arrayOfSequenceableLoader = this.loaders;
    int j = arrayOfSequenceableLoader.length;
    int i = 0;
    long l1 = 9223372036854775807L;
    while (i < j)
    {
      long l3 = arrayOfSequenceableLoader[i].getNextLoadPositionUs();
      l2 = l1;
      if (l3 != -9223372036854775808L)
        l2 = Math.min(l1, l3);
      i += 1;
      l1 = l2;
    }
    long l2 = l1;
    if (l1 == 9223372036854775807L)
      l2 = -9223372036854775808L;
    return l2;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.CompositeSequenceableLoader
 * JD-Core Version:    0.6.0
 */