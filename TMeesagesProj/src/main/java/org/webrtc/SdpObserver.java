package org.webrtc;

public abstract interface SdpObserver
{
  public abstract void onCreateFailure(String paramString);

  public abstract void onCreateSuccess(SessionDescription paramSessionDescription);

  public abstract void onSetFailure(String paramString);

  public abstract void onSetSuccess();
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.SdpObserver
 * JD-Core Version:    0.6.0
 */