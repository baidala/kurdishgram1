package org.vidogram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import org.vidogram.messenger.audioinfo.util.RangeInputStream;

public class MP4Atom extends MP4Box<RangeInputStream>
{
  public MP4Atom(RangeInputStream paramRangeInputStream, MP4Box<?> paramMP4Box, String paramString)
  {
    super(paramRangeInputStream, paramMP4Box, paramString);
  }

  private StringBuffer appendPath(StringBuffer paramStringBuffer, MP4Box<?> paramMP4Box)
  {
    if (paramMP4Box.getParent() != null)
    {
      appendPath(paramStringBuffer, paramMP4Box.getParent());
      paramStringBuffer.append("/");
    }
    return paramStringBuffer.append(paramMP4Box.getType());
  }

  public long getLength()
  {
    long l = ((RangeInputStream)getInput()).getPosition();
    return ((RangeInputStream)getInput()).getRemainingLength() + l;
  }

  public long getOffset()
  {
    return getParent().getPosition() - getPosition();
  }

  public String getPath()
  {
    return appendPath(new StringBuffer(), this).toString();
  }

  public long getRemaining()
  {
    return ((RangeInputStream)getInput()).getRemainingLength();
  }

  public boolean hasMoreChildren()
  {
    long l;
    if (getChild() != null)
      l = getChild().getRemaining();
    while (l < getRemaining())
    {
      return true;
      l = 0L;
    }
    return false;
  }

  public MP4Atom nextChildUpTo(String paramString)
  {
    while (getRemaining() > 0L)
    {
      MP4Atom localMP4Atom = nextChild();
      if (localMP4Atom.getType().matches(paramString))
        return localMP4Atom;
    }
    throw new IOException("atom type mismatch, not found: " + paramString);
  }

  public boolean readBoolean()
  {
    return this.data.readBoolean();
  }

  public byte readByte()
  {
    return this.data.readByte();
  }

  public byte[] readBytes()
  {
    return readBytes((int)getRemaining());
  }

  public byte[] readBytes(int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    this.data.readFully(arrayOfByte);
    return arrayOfByte;
  }

  public int readInt()
  {
    return this.data.readInt();
  }

  public BigDecimal readIntegerFixedPoint()
  {
    int i = this.data.readShort();
    int j = this.data.readUnsignedShort();
    return new BigDecimal(String.valueOf(i) + "" + String.valueOf(j));
  }

  public long readLong()
  {
    return this.data.readLong();
  }

  public short readShort()
  {
    return this.data.readShort();
  }

  public BigDecimal readShortFixedPoint()
  {
    int i = this.data.readByte();
    int j = this.data.readUnsignedByte();
    return new BigDecimal(String.valueOf(i) + "" + String.valueOf(j));
  }

  public String readString(int paramInt, String paramString)
  {
    paramString = new String(readBytes(paramInt), paramString);
    paramInt = paramString.indexOf(0);
    if (paramInt < 0)
      return paramString;
    return paramString.substring(0, paramInt);
  }

  public String readString(String paramString)
  {
    return readString((int)getRemaining(), paramString);
  }

  public void skip()
  {
    while (getRemaining() > 0L)
    {
      if (((RangeInputStream)getInput()).skip(getRemaining()) != 0L)
        continue;
      throw new EOFException("Cannot skip atom");
    }
  }

  public void skip(int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      int j = this.data.skipBytes(paramInt - i);
      if (j > 0)
      {
        i += j;
        continue;
      }
      throw new EOFException();
    }
  }

  public String toString()
  {
    StringBuffer localStringBuffer = new StringBuffer();
    appendPath(localStringBuffer, this);
    localStringBuffer.append("[off=");
    localStringBuffer.append(getOffset());
    localStringBuffer.append(",pos=");
    localStringBuffer.append(getPosition());
    localStringBuffer.append(",len=");
    localStringBuffer.append(getLength());
    localStringBuffer.append("]");
    return localStringBuffer.toString();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.m4a.MP4Atom
 * JD-Core Version:    0.6.0
 */