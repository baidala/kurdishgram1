package org.vidogram.tgnet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import org.vidogram.messenger.FileLog;

public class SerializedData extends AbstractSerializedData
{
  private DataInputStream in;
  private ByteArrayInputStream inbuf;
  protected boolean isOut = true;
  private boolean justCalc = false;
  private int len;
  private DataOutputStream out;
  private ByteArrayOutputStream outbuf;

  public SerializedData()
  {
    this.outbuf = new ByteArrayOutputStream();
    this.out = new DataOutputStream(this.outbuf);
  }

  public SerializedData(int paramInt)
  {
    this.outbuf = new ByteArrayOutputStream(paramInt);
    this.out = new DataOutputStream(this.outbuf);
  }

  public SerializedData(File paramFile)
  {
    FileInputStream localFileInputStream = new FileInputStream(paramFile);
    paramFile = new byte[(int)paramFile.length()];
    new DataInputStream(localFileInputStream).readFully(paramFile);
    localFileInputStream.close();
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramFile);
    this.in = new DataInputStream(this.inbuf);
  }

  public SerializedData(boolean paramBoolean)
  {
    if (!paramBoolean)
    {
      this.outbuf = new ByteArrayOutputStream();
      this.out = new DataOutputStream(this.outbuf);
    }
    this.justCalc = paramBoolean;
    this.len = 0;
  }

  public SerializedData(byte[] paramArrayOfByte)
  {
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramArrayOfByte);
    this.in = new DataInputStream(this.inbuf);
    this.len = 0;
  }

  private void writeInt32(int paramInt, DataOutputStream paramDataOutputStream)
  {
    int i = 0;
    while (true)
      if (i < 4)
        try
        {
          paramDataOutputStream.write(paramInt >> i * 8);
          i += 1;
        }
        catch (Exception paramDataOutputStream)
        {
          FileLog.e("write int32 error");
        }
  }

  private void writeInt64(long paramLong, DataOutputStream paramDataOutputStream)
  {
    int i = 0;
    while (true)
      if (i < 8)
      {
        int j = (int)(paramLong >> i * 8);
        try
        {
          paramDataOutputStream.write(j);
          i += 1;
        }
        catch (Exception paramDataOutputStream)
        {
          FileLog.e("write int64 error");
        }
      }
  }

  public void cleanup()
  {
    try
    {
      if (this.inbuf != null)
      {
        this.inbuf.close();
        this.inbuf = null;
      }
    }
    catch (Exception localException4)
    {
      try
      {
        if (this.in != null)
        {
          this.in.close();
          this.in = null;
        }
      }
      catch (Exception localException4)
      {
        try
        {
          if (this.outbuf != null)
          {
            this.outbuf.close();
            this.outbuf = null;
          }
        }
        catch (Exception localException4)
        {
          try
          {
            while (true)
            {
              if (this.out != null)
              {
                this.out.close();
                this.out = null;
              }
              return;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
              continue;
              localException3 = localException3;
              FileLog.e(localException3);
            }
          }
          catch (Exception localException4)
          {
            FileLog.e(localException4);
          }
        }
      }
    }
  }

  public int getPosition()
  {
    return this.len;
  }

  public int length()
  {
    if (!this.justCalc)
    {
      if (this.isOut)
        return this.outbuf.size();
      return this.inbuf.available();
    }
    return this.len;
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
    label170: 
    while (true)
    {
      Object localObject;
      try
      {
        int j = this.in.read();
        this.len += 1;
        if (j < 254)
          break label170;
        j = this.in.read();
        int k = this.in.read();
        int m = this.in.read();
        this.len += 3;
        i = 4;
        j = j | k << 8 | m << 16;
        byte[] arrayOfByte2 = new byte[j];
        this.in.read(arrayOfByte2);
        this.len += 1;
        byte[] arrayOfByte1 = arrayOfByte2;
        if ((j + i) % 4 != 0)
        {
          this.in.read();
          this.len += 1;
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

  public NativeByteBuffer readByteBuffer(boolean paramBoolean)
  {
    return null;
  }

  public void readBytes(byte[] paramArrayOfByte, boolean paramBoolean)
  {
    try
    {
      this.in.read(paramArrayOfByte);
      this.len += paramArrayOfByte.length;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      if (paramBoolean)
        throw new RuntimeException("read bytes error", paramArrayOfByte);
      FileLog.e("read bytes error");
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
    int i = 0;
    int j = 0;
    while (true)
    {
      int k = i;
      if (k < 4);
      try
      {
        int m = this.in.read();
        this.len += 1;
        i = k + 1;
        j = m << k * 8 | j;
        continue;
        return j;
      }
      catch (Exception localException)
      {
        if (paramBoolean)
          throw new RuntimeException("read int32 error", localException);
        FileLog.e("read int32 error");
      }
    }
    return 0;
  }

  public long readInt64(boolean paramBoolean)
  {
    int i = 0;
    long l1 = 0L;
    while (true)
    {
      if (i < 8);
      try
      {
        long l2 = this.in.read();
        this.len += 1;
        int j = i + 1;
        l1 = l2 << i * 8 | l1;
        i = j;
        continue;
        return l1;
      }
      catch (Exception localException)
      {
        if (paramBoolean)
          throw new RuntimeException("read int64 error", localException);
        FileLog.e("read int64 error");
      }
    }
    return 0L;
  }

  public String readString(boolean paramBoolean)
  {
    while (true)
    {
      try
      {
        int j = this.in.read();
        this.len += 1;
        if (j >= 254)
        {
          j = this.in.read();
          int k = this.in.read();
          int m = this.in.read();
          this.len += 3;
          i = 4;
          j = j | k << 8 | m << 16;
          Object localObject = new byte[j];
          this.in.read(localObject);
          this.len += 1;
          if ((j + i) % 4 == 0)
            continue;
          this.in.read();
          this.len += 1;
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
        return null;
      }
      int i = 1;
    }
  }

  protected void set(byte[] paramArrayOfByte)
  {
    this.isOut = false;
    this.inbuf = new ByteArrayInputStream(paramArrayOfByte);
    this.in = new DataInputStream(this.inbuf);
  }

  public void skip(int paramInt)
  {
    if (paramInt == 0);
    while (true)
    {
      return;
      if (this.justCalc)
        break;
      if (this.in == null)
        continue;
      try
      {
        this.in.skipBytes(paramInt);
        return;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
    }
    this.len += paramInt;
  }

  public byte[] toByteArray()
  {
    return this.outbuf.toByteArray();
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
        this.out.writeByte(paramByte);
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
    try
    {
      if (!this.justCalc)
      {
        this.out.writeByte((byte)paramInt);
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

  public void writeByteArray(byte[] paramArrayOfByte)
  {
    while (true)
    {
      try
      {
        if (paramArrayOfByte.length > 253)
          break label96;
        if (this.justCalc)
          continue;
        this.out.write(paramArrayOfByte.length);
        if (this.justCalc)
          break label162;
        this.out.write(paramArrayOfByte);
        if (paramArrayOfByte.length > 253)
          break label193;
        i = 1;
        if ((paramArrayOfByte.length + i) % 4 != 0)
        {
          if (this.justCalc)
            break label176;
          this.out.write(0);
          break label186;
          this.len += 1;
          continue;
        }
      }
      catch (Exception paramArrayOfByte)
      {
        FileLog.e("write byte array error");
      }
      return;
      label96: if (!this.justCalc)
      {
        this.out.write(254);
        this.out.write(paramArrayOfByte.length);
        this.out.write(paramArrayOfByte.length >> 8);
        this.out.write(paramArrayOfByte.length >> 16);
        continue;
      }
      this.len += 4;
      continue;
      label162: this.len += paramArrayOfByte.length;
      continue;
      label176: this.len += 1;
      label186: i += 1;
      continue;
      label193: int i = 4;
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
        this.out.write(paramInt2);
        if (this.justCalc)
          break label151;
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        break label177;
        if ((paramInt2 + paramInt1) % 4 != 0)
        {
          if (this.justCalc)
            break label164;
          this.out.write(0);
          break label189;
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
        this.out.write(254);
        this.out.write(paramInt2);
        this.out.write(paramInt2 >> 8);
        this.out.write(paramInt2 >> 16);
        continue;
      }
      this.len += 4;
      continue;
      label151: this.len += paramInt2;
      break label177;
      label164: this.len += 1;
      label177: if (paramInt2 <= 253)
      {
        paramInt1 = 1;
        continue;
        label189: paramInt1 += 1;
        continue;
      }
      paramInt1 = 4;
    }
  }

  public void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer)
  {
  }

  public void writeBytes(byte[] paramArrayOfByte)
  {
    try
    {
      if (!this.justCalc)
      {
        this.out.write(paramArrayOfByte);
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
        this.out.write(paramArrayOfByte, paramInt1, paramInt2);
        return;
      }
      this.len += paramInt2;
      return;
    }
    catch (Exception paramArrayOfByte)
    {
      FileLog.e("write bytes error");
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
    if (!this.justCalc)
    {
      writeInt32(paramInt, this.out);
      return;
    }
    this.len += 4;
  }

  public void writeInt64(long paramLong)
  {
    if (!this.justCalc)
    {
      writeInt64(paramLong, this.out);
      return;
    }
    this.len += 8;
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
 * Qualified Name:     org.vidogram.tgnet.SerializedData
 * JD-Core Version:    0.6.0
 */