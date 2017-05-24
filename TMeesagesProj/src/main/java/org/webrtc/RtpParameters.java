package org.webrtc;

import java.util.LinkedList;

public class RtpParameters
{
  public final LinkedList<Codec> codecs = new LinkedList();
  public final LinkedList<Encoding> encodings = new LinkedList();

  public static class Codec
  {
    int channels = 1;
    int clockRate;
    String mimeType;
    int payloadType;
  }

  public static class Encoding
  {
    public boolean active = true;
    public Integer maxBitrateBps;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.RtpParameters
 * JD-Core Version:    0.6.0
 */