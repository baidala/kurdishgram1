package org.vidogram.messenger.exoplayer2.upstream;

import java.io.ByteArrayOutputStream;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class ByteArrayDataSink
  implements DataSink
{
  private ByteArrayOutputStream stream;

  public void close()
  {
    this.stream.close();
  }

  public byte[] getData()
  {
    if (this.stream == null)
      return null;
    return this.stream.toByteArray();
  }

  public void open(DataSpec paramDataSpec)
  {
    if (paramDataSpec.length == -1L)
    {
      this.stream = new ByteArrayOutputStream();
      return;
    }
    if (paramDataSpec.length <= 2147483647L);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.stream = new ByteArrayOutputStream((int)paramDataSpec.length);
      return;
    }
  }

  public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.stream.write(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.ByteArrayDataSink
 * JD-Core Version:    0.6.0
 */