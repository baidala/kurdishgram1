package org.vidogram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.InputStream;
import org.vidogram.messenger.audioinfo.util.PositionInputStream;

public class MP3Input extends PositionInputStream
{
  public MP3Input(InputStream paramInputStream)
  {
    super(paramInputStream);
  }

  public MP3Input(InputStream paramInputStream, long paramLong)
  {
    super(paramInputStream, paramLong);
  }

  public final void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    while (i < paramInt2)
    {
      int j = read(paramArrayOfByte, paramInt1 + i, paramInt2 - i);
      if (j > 0)
      {
        i += j;
        continue;
      }
      throw new EOFException();
    }
  }

  public void skipFully(long paramLong)
  {
    long l1 = 0L;
    while (l1 < paramLong)
    {
      long l2 = skip(paramLong - l1);
      if (l2 > 0L)
      {
        l1 += l2;
        continue;
      }
      throw new EOFException();
    }
  }

  public String toString()
  {
    return "mp3[pos=" + getPosition() + "]";
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.MP3Input
 * JD-Core Version:    0.6.0
 */