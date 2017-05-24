package org.vidogram.messenger.audioinfo.mp3;

public class ID3v2FrameHeader
{
  private int bodySize;
  private boolean compression;
  private int dataLengthIndicator;
  private boolean encryption;
  private String frameId;
  private int headerSize;
  private boolean unsynchronization;

  public ID3v2FrameHeader(ID3v2TagBody paramID3v2TagBody)
  {
    long l = paramID3v2TagBody.getPosition();
    ID3v2DataInput localID3v2DataInput = paramID3v2TagBody.getData();
    label99: int i1;
    int m;
    int n;
    int j;
    int i;
    int k;
    label151: boolean bool1;
    if (paramID3v2TagBody.getTagHeader().getVersion() == 2)
    {
      this.frameId = new String(localID3v2DataInput.readFully(3), "ISO-8859-1");
      if (paramID3v2TagBody.getTagHeader().getVersion() != 2)
        break label321;
      this.bodySize = ((localID3v2DataInput.readByte() & 0xFF) << 16 | (localID3v2DataInput.readByte() & 0xFF) << 8 | localID3v2DataInput.readByte() & 0xFF);
      if (paramID3v2TagBody.getTagHeader().getVersion() > 2)
      {
        localID3v2DataInput.readByte();
        i1 = localID3v2DataInput.readByte();
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3)
          break label356;
        m = 32;
        n = 64;
        j = 0;
        i = 128;
        k = 0;
        if ((i & i1) == 0)
          break label374;
        bool1 = true;
        label161: this.compression = bool1;
        if ((i1 & k) == 0)
          break label380;
        bool1 = true;
        label178: this.unsynchronization = bool1;
        if ((i1 & n) == 0)
          break label386;
        bool1 = bool2;
        label196: this.encryption = bool1;
        if (paramID3v2TagBody.getTagHeader().getVersion() != 3)
          break label392;
        if (this.compression)
        {
          this.dataLengthIndicator = localID3v2DataInput.readInt();
          this.bodySize -= 4;
        }
        if (this.encryption)
        {
          localID3v2DataInput.readByte();
          this.bodySize -= 1;
        }
        if ((i1 & m) != 0)
        {
          localID3v2DataInput.readByte();
          this.bodySize -= 1;
        }
      }
    }
    while (true)
    {
      this.headerSize = (int)(paramID3v2TagBody.getPosition() - l);
      return;
      this.frameId = new String(localID3v2DataInput.readFully(4), "ISO-8859-1");
      break;
      label321: if (paramID3v2TagBody.getTagHeader().getVersion() == 3)
      {
        this.bodySize = localID3v2DataInput.readInt();
        break label99;
      }
      this.bodySize = localID3v2DataInput.readSyncsafeInt();
      break label99;
      label356: n = 4;
      k = 2;
      i = 8;
      m = 64;
      j = 1;
      break label151;
      label374: bool1 = false;
      break label161;
      label380: bool1 = false;
      break label178;
      label386: bool1 = false;
      break label196;
      label392: if ((i1 & m) != 0)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if (this.encryption)
      {
        localID3v2DataInput.readByte();
        this.bodySize -= 1;
      }
      if ((i1 & j) == 0)
        continue;
      this.dataLengthIndicator = localID3v2DataInput.readSyncsafeInt();
      this.bodySize -= 4;
    }
  }

  public int getBodySize()
  {
    return this.bodySize;
  }

  public int getDataLengthIndicator()
  {
    return this.dataLengthIndicator;
  }

  public String getFrameId()
  {
    return this.frameId;
  }

  public int getHeaderSize()
  {
    return this.headerSize;
  }

  public boolean isCompression()
  {
    return this.compression;
  }

  public boolean isEncryption()
  {
    return this.encryption;
  }

  public boolean isPadding()
  {
    int i = 0;
    if (i < this.frameId.length())
      if (this.frameId.charAt(0) == 0);
    do
    {
      return false;
      i += 1;
      break;
    }
    while (this.bodySize != 0);
    return true;
  }

  public boolean isUnsynchronization()
  {
    return this.unsynchronization;
  }

  public boolean isValid()
  {
    int i = 0;
    if (i < this.frameId.length())
      if (((this.frameId.charAt(i) >= 'A') && (this.frameId.charAt(i) <= 'Z')) || ((this.frameId.charAt(i) >= '0') && (this.frameId.charAt(i) <= '9')));
    do
    {
      return false;
      i += 1;
      break;
    }
    while (this.bodySize <= 0);
    return true;
  }

  public String toString()
  {
    return String.format("%s[id=%s, bodysize=%d]", new Object[] { getClass().getSimpleName(), this.frameId, Integer.valueOf(this.bodySize) });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v2FrameHeader
 * JD-Core Version:    0.6.0
 */