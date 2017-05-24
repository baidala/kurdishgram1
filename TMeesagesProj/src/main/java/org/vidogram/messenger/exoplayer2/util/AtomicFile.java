package org.vidogram.messenger.exoplayer2.util;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AtomicFile
{
  private static final String TAG = "AtomicFile";
  private final File backupName;
  private final File baseName;

  public AtomicFile(File paramFile)
  {
    this.baseName = paramFile;
    this.backupName = new File(paramFile.getPath() + ".bak");
  }

  private void restoreBackup()
  {
    if (this.backupName.exists())
    {
      this.baseName.delete();
      this.backupName.renameTo(this.baseName);
    }
  }

  public void delete()
  {
    this.baseName.delete();
    this.backupName.delete();
  }

  public void endWrite(OutputStream paramOutputStream)
  {
    paramOutputStream.close();
    this.backupName.delete();
  }

  public InputStream openRead()
  {
    restoreBackup();
    return new FileInputStream(this.baseName);
  }

  public OutputStream startWrite()
  {
    if (this.baseName.exists())
    {
      if (this.backupName.exists())
        break label88;
      if (!this.baseName.renameTo(this.backupName))
        Log.w("AtomicFile", "Couldn't rename file " + this.baseName + " to backup file " + this.backupName);
    }
    try
    {
      while (true)
      {
        AtomicFileOutputStream localAtomicFileOutputStream1 = new AtomicFileOutputStream(this.baseName);
        return localAtomicFileOutputStream1;
        label88: this.baseName.delete();
      }
    }
    catch (FileNotFoundException localFileNotFoundException2)
    {
      if (!this.baseName.getParentFile().mkdirs())
        throw new IOException("Couldn't create directory " + this.baseName);
      try
      {
        AtomicFileOutputStream localAtomicFileOutputStream2 = new AtomicFileOutputStream(this.baseName);
        return localAtomicFileOutputStream2;
      }
      catch (FileNotFoundException localFileNotFoundException2)
      {
      }
    }
    throw new IOException("Couldn't create " + this.baseName);
  }

  private static final class AtomicFileOutputStream extends OutputStream
  {
    private boolean closed = false;
    private final FileOutputStream fileOutputStream;

    public AtomicFileOutputStream(File paramFile)
    {
      this.fileOutputStream = new FileOutputStream(paramFile);
    }

    public void close()
    {
      if (this.closed)
        return;
      this.closed = true;
      flush();
      try
      {
        this.fileOutputStream.getFD().sync();
        this.fileOutputStream.close();
        return;
      }
      catch (IOException localIOException)
      {
        while (true)
          Log.w("AtomicFile", "Failed to sync file descriptor:", localIOException);
      }
    }

    public void flush()
    {
      this.fileOutputStream.flush();
    }

    public void write(int paramInt)
    {
      this.fileOutputStream.write(paramInt);
    }

    public void write(byte[] paramArrayOfByte)
    {
      this.fileOutputStream.write(paramArrayOfByte);
    }

    public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      this.fileOutputStream.write(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.AtomicFile
 * JD-Core Version:    0.6.0
 */