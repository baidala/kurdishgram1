package org.vidogram.messenger.exoplayer2.upstream;

public final class FileDataSourceFactory
  implements DataSource.Factory
{
  private final TransferListener<? super FileDataSource> listener;

  public FileDataSourceFactory()
  {
    this(null);
  }

  public FileDataSourceFactory(TransferListener<? super FileDataSource> paramTransferListener)
  {
    this.listener = paramTransferListener;
  }

  public DataSource createDataSource()
  {
    return new FileDataSource(this.listener);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.FileDataSourceFactory
 * JD-Core Version:    0.6.0
 */