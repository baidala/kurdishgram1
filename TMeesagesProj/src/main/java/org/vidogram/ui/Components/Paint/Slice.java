package org.vidogram.ui.Components.Paint;

import android.content.Context;
import android.graphics.RectF;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;

public class Slice
{
  private RectF bounds;
  private File file;

  public Slice(ByteBuffer paramByteBuffer, RectF paramRectF, DispatchQueue paramDispatchQueue)
  {
    this.bounds = paramRectF;
    try
    {
      this.file = File.createTempFile("paint", ".bin", ApplicationLoader.applicationContext.getCacheDir());
      if (this.file == null)
        return;
    }
    catch (Exception paramRectF)
    {
      while (true)
        FileLog.e(paramRectF);
      storeData(paramByteBuffer);
    }
  }

  private void storeData(ByteBuffer paramByteBuffer)
  {
    FileOutputStream localFileOutputStream;
    Deflater localDeflater;
    try
    {
      byte[] arrayOfByte = paramByteBuffer.array();
      localFileOutputStream = new FileOutputStream(this.file);
      localDeflater = new Deflater(1, true);
      localDeflater.setInput(arrayOfByte, paramByteBuffer.arrayOffset(), paramByteBuffer.remaining());
      localDeflater.finish();
      paramByteBuffer = new byte[1024];
      while (!localDeflater.finished())
        localFileOutputStream.write(paramByteBuffer, 0, localDeflater.deflate(paramByteBuffer));
    }
    catch (Exception paramByteBuffer)
    {
      FileLog.e(paramByteBuffer);
      return;
    }
    localDeflater.end();
    localFileOutputStream.close();
  }

  public void cleanResources()
  {
    if (this.file != null)
    {
      this.file.delete();
      this.file = null;
    }
  }

  public RectF getBounds()
  {
    return new RectF(this.bounds);
  }

  public ByteBuffer getData()
  {
    while (true)
    {
      Object localObject;
      ByteArrayOutputStream localByteArrayOutputStream;
      Inflater localInflater;
      try
      {
        localObject = new byte[1024];
        byte[] arrayOfByte = new byte[1024];
        FileInputStream localFileInputStream = new FileInputStream(this.file);
        localByteArrayOutputStream = new ByteArrayOutputStream();
        localInflater = new Inflater(true);
        int i = localFileInputStream.read(localObject);
        if (i == -1)
          continue;
        localInflater.setInput(localObject, 0, i);
        i = localInflater.inflate(arrayOfByte, 0, arrayOfByte.length);
        if (i != 0)
        {
          localByteArrayOutputStream.write(arrayOfByte, 0, i);
          continue;
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return null;
      }
      if (localInflater.finished())
      {
        localInflater.end();
        localObject = ByteBuffer.wrap(localByteArrayOutputStream.toByteArray(), 0, localByteArrayOutputStream.size());
        localByteArrayOutputStream.close();
        localException.close();
        return localObject;
      }
      boolean bool = localInflater.needsInput();
      if (!bool)
        continue;
    }
  }

  public int getHeight()
  {
    return (int)this.bounds.height();
  }

  public int getWidth()
  {
    return (int)this.bounds.width();
  }

  public int getX()
  {
    return (int)this.bounds.left;
  }

  public int getY()
  {
    return (int)this.bounds.top;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Paint.Slice
 * JD-Core Version:    0.6.0
 */