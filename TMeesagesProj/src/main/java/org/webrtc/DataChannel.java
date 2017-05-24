package org.webrtc;

import java.nio.ByteBuffer;

public class DataChannel
{
  private final long nativeDataChannel;
  private long nativeObserver;

  public DataChannel(long paramLong)
  {
    this.nativeDataChannel = paramLong;
  }

  private native long registerObserverNative(Observer paramObserver);

  private native boolean sendNative(byte[] paramArrayOfByte, boolean paramBoolean);

  private native void unregisterObserverNative(long paramLong);

  public native long bufferedAmount();

  public native void close();

  public native void dispose();

  public native String label();

  public void registerObserver(Observer paramObserver)
  {
    if (this.nativeObserver != 0L)
      unregisterObserverNative(this.nativeObserver);
    this.nativeObserver = registerObserverNative(paramObserver);
  }

  public boolean send(Buffer paramBuffer)
  {
    byte[] arrayOfByte = new byte[paramBuffer.data.remaining()];
    paramBuffer.data.get(arrayOfByte);
    return sendNative(arrayOfByte, paramBuffer.binary);
  }

  public native State state();

  public void unregisterObserver()
  {
    unregisterObserverNative(this.nativeObserver);
  }

  public static class Buffer
  {
    public final boolean binary;
    public final ByteBuffer data;

    public Buffer(ByteBuffer paramByteBuffer, boolean paramBoolean)
    {
      this.data = paramByteBuffer;
      this.binary = paramBoolean;
    }
  }

  public static class Init
  {
    public int id = -1;
    public int maxRetransmitTimeMs = -1;
    public int maxRetransmits = -1;
    public boolean negotiated = false;
    public boolean ordered = true;
    public String protocol = "";

    public Init()
    {
    }

    private Init(boolean paramBoolean1, int paramInt1, int paramInt2, String paramString, boolean paramBoolean2, int paramInt3)
    {
      this.ordered = paramBoolean1;
      this.maxRetransmitTimeMs = paramInt1;
      this.maxRetransmits = paramInt2;
      this.protocol = paramString;
      this.negotiated = paramBoolean2;
      this.id = paramInt3;
    }
  }

  public static abstract interface Observer
  {
    public abstract void onBufferedAmountChange(long paramLong);

    public abstract void onMessage(DataChannel.Buffer paramBuffer);

    public abstract void onStateChange();
  }

  public static enum State
  {
    static
    {
      CLOSING = new State("CLOSING", 2);
      CLOSED = new State("CLOSED", 3);
      $VALUES = new State[] { CONNECTING, OPEN, CLOSING, CLOSED };
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.webrtc.DataChannel
 * JD-Core Version:    0.6.0
 */