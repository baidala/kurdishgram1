package org.vidogram.messenger.exoplayer2.upstream;

import android.net.Uri;
import java.io.IOException;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class ByteArrayDataSource
  implements DataSource
{
  private int bytesRemaining;
  private final byte[] data;
  private int readPosition;
  private Uri uri;

  public ByteArrayDataSource(byte[] paramArrayOfByte)
  {
    Assertions.checkNotNull(paramArrayOfByte);
    if (paramArrayOfByte.length > 0);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.data = paramArrayOfByte;
      return;
    }
  }

  public void close()
  {
    this.uri = null;
  }

  public Uri getUri()
  {
    return this.uri;
  }

  public long open(DataSpec paramDataSpec)
  {
    this.uri = paramDataSpec.uri;
    this.readPosition = (int)paramDataSpec.position;
    long l;
    if (paramDataSpec.length == -1L)
      l = this.data.length - paramDataSpec.position;
    while (true)
    {
      this.bytesRemaining = (int)l;
      if ((this.bytesRemaining > 0) && (this.readPosition + this.bytesRemaining <= this.data.length))
        break;
      throw new IOException("Unsatisfiable range: [" + this.readPosition + ", " + paramDataSpec.length + "], length: " + this.data.length);
      l = paramDataSpec.length;
    }
    return this.bytesRemaining;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 == 0)
      return 0;
    if (this.bytesRemaining == 0)
      return -1;
    paramInt2 = Math.min(paramInt2, this.bytesRemaining);
    System.arraycopy(this.data, this.readPosition, paramArrayOfByte, paramInt1, paramInt2);
    this.readPosition += paramInt2;
    this.bytesRemaining -= paramInt2;
    return paramInt2;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.ByteArrayDataSource
 * JD-Core Version:    0.6.0
 */