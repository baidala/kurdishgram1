package org.vidogram.messenger.exoplayer2.upstream;

public final class DefaultHttpDataSourceFactory
  implements HttpDataSource.Factory
{
  private final boolean allowCrossProtocolRedirects;
  private final int connectTimeoutMillis;
  private final TransferListener<? super DataSource> listener;
  private final int readTimeoutMillis;
  private final String userAgent;

  public DefaultHttpDataSourceFactory(String paramString)
  {
    this(paramString, null);
  }

  public DefaultHttpDataSourceFactory(String paramString, TransferListener<? super DataSource> paramTransferListener)
  {
    this(paramString, paramTransferListener, 8000, 8000, false);
  }

  public DefaultHttpDataSourceFactory(String paramString, TransferListener<? super DataSource> paramTransferListener, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this.userAgent = paramString;
    this.listener = paramTransferListener;
    this.connectTimeoutMillis = paramInt1;
    this.readTimeoutMillis = paramInt2;
    this.allowCrossProtocolRedirects = paramBoolean;
  }

  public DefaultHttpDataSource createDataSource()
  {
    return new DefaultHttpDataSource(this.userAgent, null, this.listener, this.connectTimeoutMillis, this.readTimeoutMillis, this.allowCrossProtocolRedirects);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DefaultHttpDataSourceFactory
 * JD-Core Version:    0.6.0
 */