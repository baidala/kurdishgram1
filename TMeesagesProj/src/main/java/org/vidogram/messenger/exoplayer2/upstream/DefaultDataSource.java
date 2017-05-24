package org.vidogram.messenger.exoplayer2.upstream;

import android.content.Context;
import android.net.Uri;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class DefaultDataSource
  implements DataSource
{
  private static final String SCHEME_ASSET = "asset";
  private static final String SCHEME_CONTENT = "content";
  private final DataSource assetDataSource;
  private final DataSource baseDataSource;
  private final DataSource contentDataSource;
  private DataSource dataSource;
  private final DataSource fileDataSource;

  public DefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, new DefaultHttpDataSource(paramString, null, paramTransferListener, paramInt1, paramInt2, paramBoolean));
  }

  public DefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, String paramString, boolean paramBoolean)
  {
    this(paramContext, paramTransferListener, paramString, 8000, 8000, paramBoolean);
  }

  public DefaultDataSource(Context paramContext, TransferListener<? super DataSource> paramTransferListener, DataSource paramDataSource)
  {
    this.baseDataSource = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.fileDataSource = new FileDataSource(paramTransferListener);
    this.assetDataSource = new AssetDataSource(paramContext, paramTransferListener);
    this.contentDataSource = new ContentDataSource(paramContext, paramTransferListener);
  }

  public void close()
  {
    if (this.dataSource != null);
    try
    {
      this.dataSource.close();
      return;
    }
    finally
    {
      this.dataSource = null;
    }
    throw localObject;
  }

  public Uri getUri()
  {
    if (this.dataSource == null)
      return null;
    return this.dataSource.getUri();
  }

  public long open(DataSpec paramDataSpec)
  {
    boolean bool;
    String str;
    if (this.dataSource == null)
    {
      bool = true;
      Assertions.checkState(bool);
      str = paramDataSpec.uri.getScheme();
      if (!Util.isLocalFileUri(paramDataSpec.uri))
        break label81;
      if (!paramDataSpec.uri.getPath().startsWith("/android_asset/"))
        break label70;
      this.dataSource = this.assetDataSource;
    }
    while (true)
    {
      return this.dataSource.open(paramDataSpec);
      bool = false;
      break;
      label70: this.dataSource = this.fileDataSource;
      continue;
      label81: if ("asset".equals(str))
      {
        this.dataSource = this.assetDataSource;
        continue;
      }
      if ("content".equals(str))
      {
        this.dataSource = this.contentDataSource;
        continue;
      }
      this.dataSource = this.baseDataSource;
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    return this.dataSource.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.DefaultDataSource
 * JD-Core Version:    0.6.0
 */