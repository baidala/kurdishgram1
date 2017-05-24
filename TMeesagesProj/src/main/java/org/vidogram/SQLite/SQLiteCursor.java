package org.vidogram.SQLite;

import org.vidogram.messenger.FileLog;
import org.vidogram.tgnet.NativeByteBuffer;

public class SQLiteCursor
{
  public static final int FIELD_TYPE_BYTEARRAY = 4;
  public static final int FIELD_TYPE_FLOAT = 2;
  public static final int FIELD_TYPE_INT = 1;
  public static final int FIELD_TYPE_NULL = 5;
  public static final int FIELD_TYPE_STRING = 3;
  boolean inRow = false;
  SQLitePreparedStatement preparedStatement;

  public SQLiteCursor(SQLitePreparedStatement paramSQLitePreparedStatement)
  {
    this.preparedStatement = paramSQLitePreparedStatement;
  }

  public byte[] byteArrayValue(int paramInt)
  {
    checkRow();
    return columnByteArrayValue(this.preparedStatement.getStatementHandle(), paramInt);
  }

  public NativeByteBuffer byteBufferValue(int paramInt)
  {
    checkRow();
    paramInt = columnByteBufferValue(this.preparedStatement.getStatementHandle(), paramInt);
    if (paramInt != 0)
      return NativeByteBuffer.wrap(paramInt);
    return null;
  }

  void checkRow()
  {
    if (!this.inRow)
      throw new SQLiteException("You must call next before");
  }

  native byte[] columnByteArrayValue(int paramInt1, int paramInt2);

  native int columnByteBufferValue(int paramInt1, int paramInt2);

  native double columnDoubleValue(int paramInt1, int paramInt2);

  native int columnIntValue(int paramInt1, int paramInt2);

  native int columnIsNull(int paramInt1, int paramInt2);

  native long columnLongValue(int paramInt1, int paramInt2);

  native String columnStringValue(int paramInt1, int paramInt2);

  native int columnType(int paramInt1, int paramInt2);

  public void dispose()
  {
    this.preparedStatement.dispose();
  }

  public double doubleValue(int paramInt)
  {
    checkRow();
    return columnDoubleValue(this.preparedStatement.getStatementHandle(), paramInt);
  }

  public int getStatementHandle()
  {
    return this.preparedStatement.getStatementHandle();
  }

  public int getTypeOf(int paramInt)
  {
    checkRow();
    return columnType(this.preparedStatement.getStatementHandle(), paramInt);
  }

  public int intValue(int paramInt)
  {
    checkRow();
    return columnIntValue(this.preparedStatement.getStatementHandle(), paramInt);
  }

  public boolean isNull(int paramInt)
  {
    checkRow();
    return columnIsNull(this.preparedStatement.getStatementHandle(), paramInt) == 1;
  }

  public long longValue(int paramInt)
  {
    checkRow();
    return columnLongValue(this.preparedStatement.getStatementHandle(), paramInt);
  }

  public boolean next()
  {
    int i = this.preparedStatement.step(this.preparedStatement.getStatementHandle());
    int j = i;
    int k;
    if (i == -1)
    {
      j = 6;
      k = j - 1;
      if (j == 0)
        break label116;
    }
    label92: label116: 
    while (true)
    {
      try
      {
        FileLog.e("sqlite busy, waiting...");
        Thread.sleep(500L);
        j = this.preparedStatement.step();
        i = j;
        if (i != 0)
          continue;
        j = i;
        if (i != -1)
          break label92;
        throw new SQLiteException("sqlite busy");
        j = k;
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        j = k;
      }
      break;
      if (j == 0);
      for (boolean bool = true; ; bool = false)
      {
        this.inRow = bool;
        return this.inRow;
      }
    }
  }

  public String stringValue(int paramInt)
  {
    checkRow();
    return columnStringValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.SQLite.SQLiteCursor
 * JD-Core Version:    0.6.0
 */