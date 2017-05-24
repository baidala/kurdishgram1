package org.vidogram.messenger.exoplayer2.upstream;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.net.Uri;
import android.text.TextUtils;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class RawResourceDataSource
  implements DataSource
{
  private static final String RAW_RESOURCE_SCHEME = "rawresource";
  private AssetFileDescriptor assetFileDescriptor;
  private long bytesRemaining;
  private InputStream inputStream;
  private final TransferListener<? super RawResourceDataSource> listener;
  private boolean opened;
  private final Resources resources;
  private Uri uri;

  public RawResourceDataSource(Context paramContext)
  {
    this(paramContext, null);
  }

  public RawResourceDataSource(Context paramContext, TransferListener<? super RawResourceDataSource> paramTransferListener)
  {
    this.resources = paramContext.getResources();
    this.listener = paramTransferListener;
  }

  public static Uri buildRawResourceUri(int paramInt)
  {
    return Uri.parse("rawresource:///" + paramInt);
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
        throw new RawResourceDataSourceException(localIOException1);
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
      throw new RawResourceDataSourceException(localIOException2);
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
        throw new RawResourceDataSourceException(localIOException3);
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
    long l1 = -1L;
    try
    {
      this.uri = paramDataSpec.uri;
      if (!TextUtils.equals("rawresource", this.uri.getScheme()))
        throw new RawResourceDataSourceException("URI must use scheme rawresource");
    }
    catch (IOException paramDataSpec)
    {
      throw new RawResourceDataSourceException(paramDataSpec);
    }
    try
    {
      int i = Integer.parseInt(this.uri.getLastPathSegment());
      this.assetFileDescriptor = this.resources.openRawResourceFd(i);
      this.inputStream = new FileInputStream(this.assetFileDescriptor.getFileDescriptor());
      this.inputStream.skip(this.assetFileDescriptor.getStartOffset());
      if (this.inputStream.skip(paramDataSpec.position) < paramDataSpec.position)
        throw new EOFException();
    }
    catch (java.lang.NumberFormatException paramDataSpec)
    {
      throw new RawResourceDataSourceException("Resource identifier must be an integer.");
    }
    if (paramDataSpec.length != -1L)
    {
      this.bytesRemaining = paramDataSpec.length;
      this.opened = true;
      if (this.listener != null)
        this.listener.onTransferStart(this, paramDataSpec);
      return this.bytesRemaining;
    }
    long l2 = this.assetFileDescriptor.getLength();
    if (l2 == -1L);
    while (true)
    {
      this.bytesRemaining = l1;
      break;
      l1 = paramDataSpec.position;
      l1 = l2 - l1;
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
          throw new RawResourceDataSourceException(new EOFException());
          long l = Math.min(this.bytesRemaining, paramInt2);
          paramInt2 = (int)l;
        }
      }
      catch (IOException paramArrayOfByte)
      {
        throw new RawResourceDataSourceException(paramArrayOfByte);
      }
    }
    label111: if (this.bytesRemaining != -1L)
      this.bytesRemaining -= paramInt1;
    if (this.listener != null)
      this.listener.onBytesTransferred(this, paramInt1);
    return paramInt1;
  }

  public static class RawResourceDataSourceException extends IOException
  {
    public RawResourceDataSourceException(IOException paramIOException)
    {
      super();
    }

    public RawResourceDataSourceException(String paramString)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.RawResourceDataSource
 * JD-Core Version:    0.6.0
 */