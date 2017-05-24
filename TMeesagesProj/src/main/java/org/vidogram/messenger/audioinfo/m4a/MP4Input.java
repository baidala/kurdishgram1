package org.vidogram.messenger.audioinfo.m4a;

import java.io.InputStream;
import org.vidogram.messenger.audioinfo.util.PositionInputStream;

public final class MP4Input extends MP4Box<PositionInputStream>
{
  public MP4Input(InputStream paramInputStream)
  {
    super(new PositionInputStream(paramInputStream), null, "");
  }

  public MP4Atom nextChildUpTo(String paramString)
  {
    MP4Atom localMP4Atom;
    do
      localMP4Atom = nextChild();
    while (!localMP4Atom.getType().matches(paramString));
    return localMP4Atom;
  }

  public String toString()
  {
    return "mp4[pos=" + getPosition() + "]";
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.m4a.MP4Input
 * JD-Core Version:    0.6.0
 */