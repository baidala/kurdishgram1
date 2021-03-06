package org.webrtc;

public class IceCandidate
{
  public final String sdp;
  public final int sdpMLineIndex;
  public final String sdpMid;

  public IceCandidate(String paramString1, int paramInt, String paramString2)
  {
    this.sdpMid = paramString1;
    this.sdpMLineIndex = paramInt;
    this.sdp = paramString2;
  }

  public String toString()
  {
    return this.sdpMid + ":" + this.sdpMLineIndex + ":" + this.sdp;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.IceCandidate
 * JD-Core Version:    0.6.0
 */