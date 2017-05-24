package org.vidogram.SQLite;

import java.io.File;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;

public class SQLiteDatabase
{
  private boolean inTransaction = false;
  private boolean isOpen = false;
  private final int sqliteHandle = opendb(paramString, ApplicationLoader.getFilesDirFixed().getPath());

  public SQLiteDatabase(String paramString)
  {
  }

  public void beginTransaction()
  {
    if (this.inTransaction)
      throw new SQLiteException("database already in transaction");
    this.inTransaction = true;
    beginTransaction(this.sqliteHandle);
  }

  native void beginTransaction(int paramInt);

  void checkOpened()
  {
    if (!this.isOpen)
      throw new SQLiteException("Database closed");
  }

  public void close()
  {
    if (this.isOpen);
    try
    {
      commitTransaction();
      closedb(this.sqliteHandle);
      this.isOpen = false;
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      while (true)
        FileLog.e(localSQLiteException.getMessage(), localSQLiteException);
    }
  }

  native void closedb(int paramInt);

  public void commitTransaction()
  {
    if (!this.inTransaction)
      return;
    this.inTransaction = false;
    commitTransaction(this.sqliteHandle);
  }

  native void commitTransaction(int paramInt);

  public SQLitePreparedStatement executeFast(String paramString)
  {
    return new SQLitePreparedStatement(this, paramString, true);
  }

  public Integer executeInt(String paramString, Object[] paramArrayOfObject)
  {
    checkOpened();
    paramString = queryFinalized(paramString, paramArrayOfObject);
    try
    {
      boolean bool = paramString.next();
      if (!bool)
        return null;
      int i = paramString.intValue(0);
      return Integer.valueOf(i);
    }
    finally
    {
      paramString.dispose();
    }
    throw paramArrayOfObject;
  }

  public void finalize()
  {
    super.finalize();
    close();
  }

  public int getSQLiteHandle()
  {
    return this.sqliteHandle;
  }

  native int opendb(String paramString1, String paramString2);

  public SQLiteCursor queryFinalized(String paramString, Object[] paramArrayOfObject)
  {
    checkOpened();
    return new SQLitePreparedStatement(this, paramString, true).query(paramArrayOfObject);
  }

  public boolean tableExists(String paramString)
  {
    checkOpened();
    return executeInt("SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;", new Object[] { paramString }) != null;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.SQLite.SQLiteDatabase
 * JD-Core Version:    0.6.0
 */