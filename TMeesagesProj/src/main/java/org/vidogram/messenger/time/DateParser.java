package org.vidogram.messenger.time;

import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract interface DateParser
{
  public abstract Locale getLocale();

  public abstract String getPattern();

  public abstract TimeZone getTimeZone();

  public abstract Date parse(String paramString);

  public abstract Date parse(String paramString, ParsePosition paramParsePosition);

  public abstract Object parseObject(String paramString);

  public abstract Object parseObject(String paramString, ParsePosition paramParsePosition);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.time.DateParser
 * JD-Core Version:    0.6.0
 */