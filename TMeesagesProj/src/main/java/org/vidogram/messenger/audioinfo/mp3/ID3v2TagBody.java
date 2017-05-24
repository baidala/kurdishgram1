package org.vidogram.messenger.audioinfo.mp3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.zip.InflaterInputStream;
import org.vidogram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2TagBody
{
  private final ID3v2DataInput data;
  private final RangeInputStream input;
  private final ID3v2TagHeader tagHeader;

  ID3v2TagBody(InputStream paramInputStream, long paramLong, int paramInt, ID3v2TagHeader paramID3v2TagHeader)
  {
    this.input = new RangeInputStream(paramInputStream, paramLong, paramInt);
    this.data = new ID3v2DataInput(this.input);
    this.tagHeader = paramID3v2TagHeader;
  }

  public ID3v2FrameBody frameBody(ID3v2FrameHeader paramID3v2FrameHeader)
  {
    int j = paramID3v2FrameHeader.getBodySize();
    Object localObject = this.input;
    if (paramID3v2FrameHeader.isUnsynchronization())
    {
      localObject = this.data.readFully(paramID3v2FrameHeader.getBodySize());
      int i1 = localObject.length;
      int k = 0;
      j = 0;
      int n = 0;
      if (k < i1)
      {
        int i = localObject[k];
        int m;
        if (n != 0)
        {
          m = j;
          if (i == 0);
        }
        else
        {
          localObject[j] = i;
          m = j + 1;
        }
        if (i == 255);
        for (j = 1; ; j = 0)
        {
          k += 1;
          n = j;
          j = m;
          break;
        }
      }
      localObject = new ByteArrayInputStream(localObject, 0, j);
    }
    while (true)
    {
      if (paramID3v2FrameHeader.isEncryption())
        throw new ID3v2Exception("Frame encryption is not supported");
      if (paramID3v2FrameHeader.isCompression())
      {
        j = paramID3v2FrameHeader.getDataLengthIndicator();
        localObject = new InflaterInputStream((InputStream)localObject);
      }
      while (true)
        return new ID3v2FrameBody((InputStream)localObject, paramID3v2FrameHeader.getHeaderSize(), j, this.tagHeader, paramID3v2FrameHeader);
    }
  }

  public ID3v2DataInput getData()
  {
    return this.data;
  }

  public long getPosition()
  {
    return this.input.getPosition();
  }

  public long getRemainingLength()
  {
    return this.input.getRemainingLength();
  }

  public ID3v2TagHeader getTagHeader()
  {
    return this.tagHeader;
  }

  public String toString()
  {
    return "id3v2tag[pos=" + getPosition() + ", " + getRemainingLength() + " left]";
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v2TagBody
 * JD-Core Version:    0.6.0
 */