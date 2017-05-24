package org.vidogram.tgnet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.vidogram.messenger.FileLog;

public class NativeByteBuffer extends AbstractSerializedData
{
  private static final ThreadLocal<NativeByteBuffer> addressWrapper = new ThreadLocal()
  {
    protected NativeByteBuffer initialValue()
    {
      return new NativeByteBuffer(0, true, null);
    }
  };
  protected int address;
  public ByteBuffer buffer;
  private boolean justCalc;
  private int len;
  public boolean reused = true;

  public NativeByteBuffer(int paramInt)
  {
    if (paramInt >= 0)
    {
      this.address = native_getFreeBuffer(paramInt);
      if (this.address != 0)
      {
        this.buffer = native_getJavaByteBuffer(this.address);
        this.buffer.position(0);
        this.buffer.limit(paramInt);
        this.buffer.order(ByteOrder.LITTLE_ENDIAN);
      }
      return;
    }
    throw new Exception("invalid NativeByteBuffer size");
  }

  private NativeByteBuffer(int paramInt, boolean paramBoolean)
  {
  }

  public NativeByteBuffer(boolean paramBoolean)
  {
    this.justCalc = paramBoolean;
  }

  public static native int native_getFreeBuffer(int paramInt);

  public static native ByteBuffer native_getJavaByteBuffer(int paramInt);

  public static native int native_limit(int paramInt);

  public static native int native_position(int paramInt);

  public static native void native_reuse(int paramInt);

  public static NativeByteBuffer wrap(int paramInt)
  {
    NativeByteBuffer localNativeByteBuffer = (NativeByteBuffer)addressWrapper.get();
    if (paramInt != 0)
    {
      if (!localNativeByteBuffer.reused)
        FileLog.e("forgot to reuse?");
      localNativeByteBuffer.address = paramInt;
      localNativeByteBuffer.reused = false;
      localNativeByteBuffer.buffer = native_getJavaByteBuffer(paramInt);
      localNativeByteBuffer.buffer.limit(native_limit(paramInt));
      paramInt = native_position(paramInt);
      if (paramInt <= localNativeByteBuffer.buffer.limit())
        localNativeByteBuffer.buffer.position(paramInt);
      localNativeByteBuffer.buffer.order(ByteOrder.LITTLE_ENDIAN);
    }
    return localNativeByteBuffer;
  }

  public int capacity()
  {
    return this.buffer.capacity();
  }

  public void compact()
  {
    this.buffer.compact();
  }

  public int getIntFromByte(byte paramByte)
  {
    if (paramByte >= 0)
      return paramByte;
    return paramByte + 256;
  }

  public int getPosition()
  {
    return this.buffer.position();
  }

  public boolean hasRemaining()
  {
    return this.buffer.hasRemaining();
  }

  public int length()
  {
    if (!this.justCalc)
      return this.buffer.position();
    return this.len;
  }

  public int limit()
  {
    return this.buffer.limit();
  }

  public void limit(int paramInt)
  {
    this.buffer.limit(paramInt);
  }

  public int position()
  {
    return this.buffer.position();
  }

  public void position(int paramInt)
  {
    this.buffer.position(paramInt);
  }

  public void put(ByteBuffer paramByteBuffer)
  {
    this.buffer.put(paramByteBuffer);
  }

  public boolean readBool(boolean paramBoolean)
  {
    int j = 0;
    int i = readInt32(paramBoolean);
    if (i == -1720552011)
      j = 1;
    do
      return j;
    while (i == -1132882121);
    if (paramBoolean)
      throw new RuntimeException("Not bool value!");
    FileLog.e("Not bool value!");
    return false;
  }

  public byte[] readByteArray(boolean paramBoolean)
  {
    int i = 1;
    label148: 
    while (true)
    {
      byte[] arrayOfByte2;
      try
      {
        int j = getIntFromByte(this.buffer.get());
        if (j < 254)
          break label148;
        j = getIntFromByte(this.buffer.get());
        int k = getIntFromByte(this.buffer.get());
        int m = getIntFromByte(this.buffer.get());
        i = 4;
        j = j | k << 8 | m << 16;
        byte[] arrayOfByte3 = new byte[j];
        this.buffer.get(arrayOfByte3);
        byte[] arrayOfByte1 = arrayOfByte3;
        if ((j + i) % 4 != 0)
        {
          this.buffer.get();
          i += 1;
          continue;
        }
      }
      catch (Exception arrayOfByte2)
      {
        if (!paramBoolean)
          continue;
        throw new RuntimeException("read byte array error", localException);
        FileLog.e("read byte array error");
        arrayOfByte2 = new byte[0];
      }
      return arrayOfByte2;
    }
  }

  public NativeByteBuffer readByteBuffer(boolean paramBoolean)
  {
    int i = 1;
    label200: 
    while (true)
    {
      Object localObject;
      try
      {
        int j = getIntFromByte(this.buffer.get());
        if (j < 254)
          break label200;
        j = getIntFromByte(this.buffer.get());
        int k = getIntFromByte(this.buffer.get());
        int m = getIntFromByte(this.buffer.get());
        i = 4;
        j = j | k << 8 | m << 16;
        NativeByteBuffer localNativeByteBuffer2 = new NativeByteBuffer(j);
        k = this.buffer.limit();
        this.buffer.limit(this.buffer.position() + j);
        localNativeByteBuffer2.buffer.put(this.buffer);
        this.buffer.limit(k);
        localNativeByteBuffer2.buffer.position(0);
        NativeByteBuffer localNativeByteBuffer1 = localNativeByteBuffer2;
        if ((j + i) % 4 != 0)
        {
          this.buffer.get();
          i += 1;
          continue;
        }
      }
      catch (Exception localObject)
      {
        if (!paramBoolean)
          continue;
        throw new RuntimeException("read byte array error", localException);
        FileLog.e("read byte array error");
        localObject = null;
      }
      return localObject;
    }
  }

  public void readBytes(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    try
    {
      this.buffer.get(paramArrayOfByte);
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      if (paramBoolean)
        throw new RuntimeException("read raw error", paramArrayOfByte);
      FileLog.e("read raw error");
    }
  }

  public byte[] readData(int paramInt, boolean paramBoolean)
  {
    byte[] arrayOfByte = new byte[paramInt];
    readBytes(arrayOfByte, paramBoolean);
    return arrayOfByte;
  }

  public double readDouble(boolean paramBoolean)
  {
    try
    {
      double d = Double.longBitsToDouble(readInt64(paramBoolean));
      return d;
    }
    catch (Exception localException)
    {
      if (paramBoolean)
        throw new RuntimeException("read double error", localException);
      FileLog.e("read double error");
    }
    return 0.0D;
  }

  public int readInt32(boolean paramBoolean)
  {
    try
    {
      int i = this.buffer.getInt();
      return i;
    }
    catch (Exception localException)
    {
      if (paramBoolean)
        throw new RuntimeException("read int32 error", localException);
      FileLog.e("read int32 error");
    }
    return 0;
  }

  public long readInt64(boolean paramBoolean)
  {
    try
    {
      long l = this.buffer.getLong();
      return l;
    }
    catch (Exception localException)
    {
      if (paramBoolean)
        throw new RuntimeException("read int64 error", localException);
      FileLog.e("read int64 error");
    }
    return 0L;
  }

  public String readString(boolean paramBoolean)
  {
    while (true)
    {
      try
      {
        int j = getIntFromByte(this.buffer.get());
        if (j >= 254)
        {
          j = getIntFromByte(this.buffer.get());
          int k = getIntFromByte(this.buffer.get());
          int m = getIntFromByte(this.buffer.get());
          i = 4;
          j = j | k << 8 | m << 16;
          Object localObject = new byte[j];
          this.buffer.get(localObject);
          if ((j + i) % 4 == 0)
            continue;
          this.buffer.get();
          i += 1;
          continue;
          localObject = new String(localObject, "UTF-8");
          return localObject;
        }
      }
      catch (Exception localException)
      {
        if (!paramBoolean)
          continue;
        throw new RuntimeException("read string error", localException);
        FileLog.e("read string error");
        return "";
      }
      int i = 1;
    }
  }

  public void reuse()
  {
    if (this.address != 0)
    {
      this.reused = true;
      native_reuse(this.address);
    }
  }

  public void rewind()
  {
    if (this.justCalc)
    {
      this.len = 0;
      return;
    }
    this.buffer.rewind();
  }

  public void skip(int paramInt)
  {
    if (paramInt == 0)
      return;
    if (!this.justCalc)
    {
      this.buffer.position(this.buffer.position() + paramInt);
      return;
    }
    this.len += paramInt;
  }

  public void writeBool(boolean paramBoolean)
  {
    if (!this.justCalc)
    {
      if (paramBoolean)
      {
        writeInt32(-1720552011);
        return;
      }
      writeInt32(-1132882121);
      return;
    }
    this.len += 4;
  }

  public void writeByte(byte paramByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.put(paramByte);
        return;
      }
      this.len += 1;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("write byte error");
    }
  }

  public void writeByte(int paramInt)
  {
    writeByte((byte)paramInt);
  }

  public void writeByteArray(byte[] paramArrayOfByte)
  {
    while (true)
    {
      try
      {
        if (paramArrayOfByte.length > 253)
          break label100;
        if (this.justCalc)
          continue;
        this.buffer.put((byte)paramArrayOfByte.length);
        if (this.justCalc)
          break label172;
        this.buffer.put(paramArrayOfByte);
        if (paramArrayOfByte.length > 253)
          break label203;
        i = 1;
        if ((paramArrayOfByte.length + i) % 4 != 0)
        {
          if (this.justCalc)
            break label186;
          this.buffer.put(0);
          break label196;
          this.len += 1;
          continue;
        }
      }
      catch (Exception paramArrayOfByte)
      {
        FileLog.e("write byte array error");
      }
      return;
      label100: if (!this.justCalc)
      {
        this.buffer.put(-2);
        this.buffer.put((byte)paramArrayOfByte.length);
        this.buffer.put((byte)(paramArrayOfByte.length >> 8));
        this.buffer.put((byte)(paramArrayOfByte.length >> 16));
        continue;
      }
      this.len += 4;
      continue;
      label172: this.len += paramArrayOfByte.length;
      continue;
      label186: this.len += 1;
      label196: i += 1;
      continue;
      label203: int i = 4;
    }
  }

  public void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (paramInt2 <= 253);
    while (true)
    {
      try
      {
        if (this.justCalc)
          continue;
        this.buffer.put((byte)paramInt2);
        if (this.justCalc)
          break label161;
        this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
        break label187;
        if ((paramInt2 + paramInt1) % 4 != 0)
        {
          if (this.justCalc)
            break label174;
          this.buffer.put(0);
          break label199;
          this.len += 1;
          continue;
        }
      }
      catch (Exception paramArrayOfByte)
      {
        FileLog.e("write byte array error");
      }
      return;
      if (!this.justCalc)
      {
        this.buffer.put(-2);
        this.buffer.put((byte)paramInt2);
        this.buffer.put((byte)(paramInt2 >> 8));
        this.buffer.put((byte)(paramInt2 >> 16));
        continue;
      }
      this.len += 4;
      continue;
      label161: this.len += paramInt2;
      break label187;
      label174: this.len += 1;
      label187: if (paramInt2 <= 253)
      {
        paramInt1 = 1;
        continue;
        label199: paramInt1 += 1;
        continue;
      }
      paramInt1 = 4;
    }
  }

  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer)
  {
    while (true)
    {
      int j;
      try
      {
        j = paramNativeByteBuffer.limit();
        if (j > 253)
          break label101;
        if (this.justCalc)
          continue;
        this.buffer.put((byte)j);
        if (this.justCalc)
          break label170;
        paramNativeByteBuffer.rewind();
        this.buffer.put(paramNativeByteBuffer.buffer);
        break label196;
        if ((j + i) % 4 != 0)
        {
          if (this.justCalc)
            break label183;
          this.buffer.put(0);
          break label208;
          this.len += 1;
          continue;
        }
      }
      catch (Exception paramNativeByteBuffer)
      {
        FileLog.e(paramNativeByteBuffer);
      }
      return;
      label101: if (!this.justCalc)
      {
        this.buffer.put(-2);
        this.buffer.put((byte)j);
        this.buffer.put((byte)(j >> 8));
        this.buffer.put((byte)(j >> 16));
        continue;
      }
      this.len += 4;
      continue;
      label170: this.len += j;
      break label196;
      label183: this.len += 1;
      label196: if (j <= 253)
      {
        i = 1;
        continue;
        label208: i += 1;
        continue;
      }
      int i = 4;
    }
  }

  public void writeBytes(NativeByteBuffer paramNativeByteBuffer)
  {
    if (this.justCalc)
    {
      this.len += paramNativeByteBuffer.limit();
      return;
    }
    paramNativeByteBuffer.rewind();
    this.buffer.put(paramNativeByteBuffer.buffer);
  }

  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.put(paramArrayOfByte);
        return;
      }
      this.len += paramArrayOfByte.length;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("write raw error");
    }
  }

  public void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.put(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      this.len += paramInt2;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("write raw error");
    }
  }

  public void writeDouble(double paramDouble)
  {
    try
    {
      writeInt64(Double.doubleToRawLongBits(paramDouble));
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("write double error");
    }
  }

  public void writeInt32(int paramInt)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.putInt(paramInt);
        return;
      }
      this.len += 4;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("write int32 error");
    }
  }

  public void writeInt64(long paramLong)
  {
    try
    {
      if (!this.justCalc)
      {
        this.buffer.putLong(paramLong);
        return;
      }
      this.len += 8;
      return;
    }
    catch (Exception localException)
    {
      FileLog.e("write int64 error");
    }
  }

  public void writeString(String paramString)
  {
    try
    {
      writeByteArray(paramString.getBytes("UTF-8"));
      return;
    }
    catch (Exception paramString)
    {
      FileLog.e("write string error");
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.tgnet.NativeByteBuffer
 * JD-Core Version:    0.6.0
 */