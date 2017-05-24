package org.vidogram.tgnet;

public class TLObject
{
  private static final ThreadLocal<NativeByteBuffer> sizeCalculator = new ThreadLocal()
  {
    protected NativeByteBuffer initialValue()
    {
      return new NativeByteBuffer(true);
    }
  };
  public boolean disableFree = false;
  public int networkType;

  public TLObject deserializeResponse(AbstractSerializedData paramAbstractSerializedData, int paramInt, boolean paramBoolean)
  {
    return null;
  }

  public void freeResources()
  {
  }

  public int getObjectSize()
  {
    NativeByteBuffer localNativeByteBuffer = (NativeByteBuffer)sizeCalculator.get();
    localNativeByteBuffer.rewind();
    serializeToStream((AbstractSerializedData)sizeCalculator.get());
    return localNativeByteBuffer.length();
  }

  public void readParams(AbstractSerializedData paramAbstractSerializedData, boolean paramBoolean)
  {
  }

  public void serializeToStream(AbstractSerializedData paramAbstractSerializedData)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.tgnet.TLObject
 * JD-Core Version:    0.6.0
 */