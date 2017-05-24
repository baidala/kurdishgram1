package org.vidogram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.InputStream;

public final class ParsingLoadable<T>
  implements Loader.Loadable
{
  private volatile long bytesLoaded;
  private final DataSource dataSource;
  public final DataSpec dataSpec;
  private volatile boolean isCanceled;
  private final Parser<T> parser;
  private volatile T result;
  public final int type;

  public ParsingLoadable(DataSource paramDataSource, Uri paramUri, int paramInt, Parser<T> paramParser)
  {
    this.dataSource = paramDataSource;
    this.dataSpec = new DataSpec(paramUri, 1);
    this.type = paramInt;
    this.parser = paramParser;
  }

  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }

  public final void cancelLoad()
  {
    this.isCanceled = true;
  }

  public final T getResult()
  {
    return this.result;
  }

  public final boolean isLoadCanceled()
  {
    return this.isCanceled;
  }

  public final void load()
  {
    DataSourceInputStream localDataSourceInputStream = new DataSourceInputStream(this.dataSource, this.dataSpec);
    try
    {
      localDataSourceInputStream.open();
      this.result = this.parser.parse(this.dataSource.getUri(), localDataSourceInputStream);
      return;
    }
    finally
    {
      this.bytesLoaded = localDataSourceInputStream.bytesRead();
      localDataSourceInputStream.close();
    }
    throw localObject;
  }

  public static abstract interface Parser<T>
  {
    public abstract T parse(Uri paramUri, InputStream paramInputStream);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.ParsingLoadable
 * JD-Core Version:    0.6.0
 */