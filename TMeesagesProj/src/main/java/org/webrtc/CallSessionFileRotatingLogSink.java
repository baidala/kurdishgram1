package org.webrtc;

public class CallSessionFileRotatingLogSink
{
  private long nativeSink;

  static
  {
    System.loadLibrary("jingle_peerconnection_so");
  }

  public CallSessionFileRotatingLogSink(String paramString, int paramInt, Logging.Severity paramSeverity)
  {
    this.nativeSink = nativeAddSink(paramString, paramInt, paramSeverity.ordinal());
  }

  public static byte[] getLogData(String paramString)
  {
    return nativeGetLogData(paramString);
  }

  private static native long nativeAddSink(String paramString, int paramInt1, int paramInt2);

  private static native void nativeDeleteSink(long paramLong);

  private static native byte[] nativeGetLogData(String paramString);

  public void dispose()
  {
    if (this.nativeSink != 0L)
    {
      nativeDeleteSink(this.nativeSink);
      this.nativeSink = 0L;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.CallSessionFileRotatingLogSink
 * JD-Core Version:    0.6.0
 */