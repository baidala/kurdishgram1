package org.vidogram.messenger.exoplayer2.upstream;

import java.io.IOException;

public final class DataSourceException extends IOException
{
  public static final int POSITION_OUT_OF_RANGE = 0;
  public final int reason;

  public DataSourceException(int paramInt)
  {
    this.reason = paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DataSourceException
 * JD-Core Version:    0.6.0
 */