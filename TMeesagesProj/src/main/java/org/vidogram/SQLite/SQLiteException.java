package org.vidogram.SQLite;

public class SQLiteException extends Exception
{
  private static final long serialVersionUID = -2398298479089615621L;
  public final int errorCode;

  public SQLiteException()
  {
    this.errorCode = 0;
  }

  public SQLiteException(int paramInt, String paramString)
  {
    super(paramString);
    this.errorCode = paramInt;
  }

  public SQLiteException(String paramString)
  {
    this(0, paramString);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.SQLite.SQLiteException
 * JD-Core Version:    0.6.0
 */