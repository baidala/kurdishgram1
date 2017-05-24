package org.vidogram.SQLite;

import java.nio.ByteBuffer;
import org.vidogram.messenger.FileLog;
import org.vidogram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement
{
  private boolean finalizeAfterQuery = false;
  private boolean isFinalized = false;
  private int sqliteStatementHandle;

  public SQLitePreparedStatement(SQLiteDatabase paramSQLiteDatabase, String paramString, boolean paramBoolean)
  {
    this.finalizeAfterQuery = paramBoolean;
    this.sqliteStatementHandle = prepare(paramSQLiteDatabase.getSQLiteHandle(), paramString);
  }

  native void bindByteBuffer(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3);

  public void bindByteBuffer(int paramInt, ByteBuffer paramByteBuffer)
  {
    bindByteBuffer(this.sqliteStatementHandle, paramInt, paramByteBuffer, paramByteBuffer.limit());
  }

  public void bindByteBuffer(int paramInt, NativeByteBuffer paramNativeByteBuffer)
  {
    bindByteBuffer(this.sqliteStatementHandle, paramInt, paramNativeByteBuffer.buffer, paramNativeByteBuffer.limit());
  }

  public void bindDouble(int paramInt, double paramDouble)
  {
    bindDouble(this.sqliteStatementHandle, paramInt, paramDouble);
  }

  native void bindDouble(int paramInt1, int paramInt2, double paramDouble);

  native void bindInt(int paramInt1, int paramInt2, int paramInt3);

  public void bindInteger(int paramInt1, int paramInt2)
  {
    bindInt(this.sqliteStatementHandle, paramInt1, paramInt2);
  }

  native void bindLong(int paramInt1, int paramInt2, long paramLong);

  public void bindLong(int paramInt, long paramLong)
  {
    bindLong(this.sqliteStatementHandle, paramInt, paramLong);
  }

  public void bindNull(int paramInt)
  {
    bindNull(this.sqliteStatementHandle, paramInt);
  }

  native void bindNull(int paramInt1, int paramInt2);

  native void bindString(int paramInt1, int paramInt2, String paramString);

  public void bindString(int paramInt, String paramString)
  {
    bindString(this.sqliteStatementHandle, paramInt, paramString);
  }

  void checkFinalized()
  {
    if (this.isFinalized)
      throw new SQLiteException("Prepared query finalized");
  }

  public void dispose()
  {
    if (this.finalizeAfterQuery)
      finalizeQuery();
  }

  native void finalize(int paramInt);

  public void finalizeQuery()
  {
    if (this.isFinalized)
      return;
    try
    {
      this.isFinalized = true;
      finalize(this.sqliteStatementHandle);
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      FileLog.e(localSQLiteException.getMessage(), localSQLiteException);
    }
  }

  public int getStatementHandle()
  {
    return this.sqliteStatementHandle;
  }

  native int prepare(int paramInt, String paramString);

  public SQLiteCursor query(Object[] paramArrayOfObject)
  {
    if (paramArrayOfObject == null)
      throw new IllegalArgumentException();
    checkFinalized();
    reset(this.sqliteStatementHandle);
    int k = paramArrayOfObject.length;
    int j = 1;
    int i = 0;
    if (i < k)
    {
      Object localObject = paramArrayOfObject[i];
      if (localObject == null)
        bindNull(this.sqliteStatementHandle, j);
      while (true)
      {
        j += 1;
        i += 1;
        break;
        if ((localObject instanceof Integer))
        {
          bindInt(this.sqliteStatementHandle, j, ((Integer)localObject).intValue());
          continue;
        }
        if ((localObject instanceof Double))
        {
          bindDouble(this.sqliteStatementHandle, j, ((Double)localObject).doubleValue());
          continue;
        }
        if (!(localObject instanceof String))
          break label149;
        bindString(this.sqliteStatementHandle, j, (String)localObject);
      }
      label149: throw new IllegalArgumentException();
    }
    return new SQLiteCursor(this);
  }

  public void requery()
  {
    checkFinalized();
    reset(this.sqliteStatementHandle);
  }

  native void reset(int paramInt);

  public int step()
  {
    return step(this.sqliteStatementHandle);
  }

  native int step(int paramInt);

  public SQLitePreparedStatement stepThis()
  {
    step(this.sqliteStatementHandle);
    return this;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.SQLite.SQLitePreparedStatement
 * JD-Core Version:    0.6.0
 */