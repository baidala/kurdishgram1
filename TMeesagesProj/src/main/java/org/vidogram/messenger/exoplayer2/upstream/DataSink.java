package org.vidogram.messenger.exoplayer2.upstream;

public abstract interface DataSink
{
  public abstract void close();

  public abstract void open(DataSpec paramDataSpec);

  public abstract void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2);

  public static abstract interface Factory
  {
    public abstract DataSink createDataSink();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DataSink
 * JD-Core Version:    0.6.0
 */