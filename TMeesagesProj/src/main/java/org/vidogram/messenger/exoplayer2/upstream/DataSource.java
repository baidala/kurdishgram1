package org.vidogram.messenger.exoplayer2.upstream;

import android.net.Uri;

public abstract interface DataSource
{
  public abstract void close();

  public abstract Uri getUri();

  public abstract long open(DataSpec paramDataSpec);

  public abstract int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static abstract interface Factory
  {
    public abstract DataSource createDataSource();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DataSource
 * JD-Core Version:    0.6.0
 */