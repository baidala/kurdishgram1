package org.vidogram.messenger.audioinfo.mp3;

import java.io.EOFException;
import java.io.InputStream;
import org.vidogram.messenger.audioinfo.AudioInfo;

public class ID3v1Info extends AudioInfo
{
  public ID3v1Info(InputStream paramInputStream)
  {
    if (isID3v1StartPosition(paramInputStream))
    {
      this.brand = "ID3";
      this.version = "1.0";
      paramInputStream = readBytes(paramInputStream, 128);
      this.title = extractString(paramInputStream, 3, 30);
      this.artist = extractString(paramInputStream, 33, 30);
      this.album = extractString(paramInputStream, 63, 30);
    }
    try
    {
      this.year = Short.parseShort(extractString(paramInputStream, 93, 4));
      this.comment = extractString(paramInputStream, 97, 30);
      ID3v1Genre localID3v1Genre = ID3v1Genre.getGenre(paramInputStream[127]);
      if (localID3v1Genre != null)
        this.genre = localID3v1Genre.getDescription();
      if ((paramInputStream[125] == 0) && (paramInputStream[126] != 0))
      {
        this.version = "1.1";
        this.track = (short)(paramInputStream[126] & 0xFF);
      }
      return;
    }
    catch (NumberFormatException localNumberFormatException)
    {
      while (true)
        this.year = 0;
    }
  }

  public static boolean isID3v1StartPosition(InputStream paramInputStream)
  {
    paramInputStream.mark(3);
    try
    {
      if ((paramInputStream.read() == 84) && (paramInputStream.read() == 65))
      {
        int i = paramInputStream.read();
        if (i == 71)
        {
          j = 1;
          return j;
        }
      }
      int j = 0;
    }
    finally
    {
      paramInputStream.reset();
    }
  }

  String extractString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      paramArrayOfByte = new String(paramArrayOfByte, paramInt1, paramInt2, "ISO-8859-1");
      paramInt1 = paramArrayOfByte.indexOf(0);
      if (paramInt1 < 0)
        return paramArrayOfByte;
      paramArrayOfByte = paramArrayOfByte.substring(0, paramInt1);
      return paramArrayOfByte;
    }
    catch (java.lang.Exception paramArrayOfByte)
    {
    }
    return "";
  }

  byte[] readBytes(InputStream paramInputStream, int paramInt)
  {
    int i = 0;
    byte[] arrayOfByte = new byte[paramInt];
    while (i < paramInt)
    {
      int j = paramInputStream.read(arrayOfByte, i, paramInt - i);
      if (j > 0)
      {
        i += j;
        continue;
      }
      throw new EOFException();
    }
    return arrayOfByte;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v1Info
 * JD-Core Version:    0.6.0
 */