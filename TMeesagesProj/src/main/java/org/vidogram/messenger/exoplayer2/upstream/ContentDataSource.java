package org.vidogram.messenger.exoplayer2.upstream;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentDataSource
  implements DataSource
{
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener<? super ContentDataSource> listener;
  private boolean opened;
  private final ContentResolver resolver;
  private Uri uri;

  public ContentDataSource(Context paramContext)
  {
    this(paramContext, null);
  }

  public ContentDataSource(Context paramContext, TransferListener<? super ContentDataSource> paramTransferListener)
  {
    this.resolver = paramContext.getContentResolver();
    this.listener = paramTransferListener;
  }

  public void close()
  {
    this.uri = null;
    try
    {
      if (this.inputStream != null)
        this.inputStream.close();
      this.inputStream = null;
      try
      {
        if (this.assetFileDescriptor != null)
          this.assetFileDescriptor.close();
        return;
      }
      catch (IOException localIOException1)
      {
        throw new ContentDataSourceException(localIOException1);
      }
      finally
      {
        this.assetFileDescriptor = null;
        if (this.opened)
        {
          this.opened = false;
          if (this.listener != null)
            this.listener.onTransferEnd(this);
        }
      }
    }
    catch (IOException localIOException2)
    {
      throw new ContentDataSourceException(localIOException2);
    }
    finally
    {
      this.inputStream = null;
      try
      {
        if (this.assetFileDescriptor != null)
          this.assetFileDescriptor.close();
        throw localObject2;
      }
      catch (IOException localIOException3)
      {
        throw new ContentDataSourceException(localIOException3);
      }
      finally
      {
        this.assetFileDescriptor = null;
        if (this.opened)
        {
          this.opened = false;
          if (this.listener != null)
            this.listener.onTransferEnd(this);
        }
      }
    }
    throw localObject3;
  }

  public Uri getUri()
  {
    return this.uri;
  }

  public long open(DataSpec paramDataSpec)
  {
    try
    {
      this.uri = paramDataSpec.uri;
      this.assetFileDescriptor = this.resolver.openAssetFileDescriptor(this.uri, "r");
      this.inputStream = new FileInputStream(this.assetFileDescriptor.getFileDescriptor());
      if (this.inputStream.skip(paramDataSpec.position) < paramDataSpec.position)
        throw new EOFException();
    }
    catch (IOException paramDataSpec)
    {
      throw new ContentDataSourceException(paramDataSpec);
    }
    if (paramDataSpec.length != -1L)
      this.bytesRemaining = paramDataSpec.length;
    while (true)
    {
      this.opened = true;
      if (this.listener != null)
        this.listener.onTransferStart(this, paramDataSpec);
      return this.bytesRemaining;
      this.bytesRemaining = this.inputStream.available();
      if (this.bytesRemaining != 0L)
        continue;
      this.bytesRemaining = -1L;
    }
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int j = -1;
    int i;
    if (paramInt2 == 0)
      i = 0;
    while (true)
    {
      return i;
      i = j;
      if (this.bytesRemaining == 0L)
        continue;
      try
      {
        if (this.bytesRemaining == -1L);
        while (true)
        {
          paramInt1 = this.inputStream.read(paramArrayOfByte, paramInt1, paramInt2);
          if (paramInt1 != -1)
            break label111;
          i = j;
          if (this.bytesRemaining == -1L)
            break;
          throw new ContentDataSourceException(new EOFException());
          long l = Math.min(this.bytesRemaining, paramInt2);
          paramInt2 = (int)l;
        }
      }
      catch (IOException paramArrayOfByte)
      {
        throw new ContentDataSourceException(paramArrayOfByte);
      }
    }
    label111: if (this.bytesRemaining != -1L)
      this.bytesRemaining -= paramInt1;
    if (this.listener != null)
      this.listener.onBytesTransferred(this, paramInt1);
    return paramInt1;
  }

  public static class ContentDataSourceException extends IOException
  {
    public ContentDataSourceException(IOException paramIOException)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.ContentDataSource
 * JD-Core Version:    0.6.0
 */