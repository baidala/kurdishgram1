package org.vidogram.messenger.exoplayer2.upstream;

public abstract interface LoaderErrorThrower
{
  public abstract void maybeThrowError();

  public abstract void maybeThrowError(int paramInt);

  public static final class Dummy
    implements LoaderErrorThrower
  {
    public void maybeThrowError()
    {
    }

    public void maybeThrowError(int paramInt)
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower
 * JD-Core Version:    0.6.0
 */