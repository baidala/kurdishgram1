package org.vidogram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.vidogram.messenger.audioinfo.util.PositionInputStream;
import org.vidogram.messenger.audioinfo.util.RangeInputStream;

public class MP4Box<I extends PositionInputStream>
{
  protected static final String ASCII = "ISO8859_1";
  private MP4Atom child;
  protected final DataInput data;
  private final I input;
  private final MP4Box<?> parent;
  private final String type;

  public MP4Box(I paramI, MP4Box<?> paramMP4Box, String paramString)
  {
    this.input = paramI;
    this.parent = paramMP4Box;
    this.type = paramString;
    this.data = new DataInputStream(paramI);
  }

  protected MP4Atom getChild()
  {
    return this.child;
  }

  public I getInput()
  {
    return this.input;
  }

  public MP4Box<?> getParent()
  {
    return this.parent;
  }

  public long getPosition()
  {
    return this.input.getPosition();
  }

  public String getType()
  {
    return this.type;
  }

  public MP4Atom nextChild()
  {
    if (this.child != null)
      this.child.skip();
    int i = this.data.readInt();
    Object localObject = new byte[4];
    this.data.readFully(localObject);
    String str = new String(localObject, "ISO8859_1");
    if (i == 1);
    for (localObject = new RangeInputStream(this.input, 16L, this.data.readLong() - 16L); ; localObject = new RangeInputStream(this.input, 8L, i - 8))
    {
      localObject = new MP4Atom((RangeInputStream)localObject, this, str);
      this.child = ((MP4Atom)localObject);
      return localObject;
    }
  }

  public MP4Atom nextChild(String paramString)
  {
    MP4Atom localMP4Atom = nextChild();
    if (localMP4Atom.getType().matches(paramString))
      return localMP4Atom;
    throw new IOException("atom type mismatch, expected " + paramString + ", got " + localMP4Atom.getType());
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.m4a.MP4Box
 * JD-Core Version:    0.6.0
 */