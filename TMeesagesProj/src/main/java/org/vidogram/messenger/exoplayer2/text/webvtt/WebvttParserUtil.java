package org.vidogram.messenger.exoplayer2.text.webvtt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttParserUtil
{
  private static final Pattern COMMENT = Pattern.compile("^NOTE(( |\t).*)?$");
  private static final Pattern HEADER = Pattern.compile("^﻿?WEBVTT(( |\t).*)?$");

  public static Matcher findNextCueHeader(ParsableByteArray paramParsableByteArray)
  {
    while (true)
    {
      Object localObject = paramParsableByteArray.readLine();
      if (localObject == null)
        break;
      if (COMMENT.matcher((CharSequence)localObject).matches())
        while (true)
        {
          localObject = paramParsableByteArray.readLine();
          if ((localObject == null) || (((String)localObject).isEmpty()))
            break;
        }
      localObject = WebvttCueParser.CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
      if (((Matcher)localObject).matches())
        return localObject;
    }
    return (Matcher)null;
  }

  public static float parsePercentage(String paramString)
  {
    if (!paramString.endsWith("%"))
      throw new NumberFormatException("Percentages must end with %");
    return Float.parseFloat(paramString.substring(0, paramString.length() - 1)) / 100.0F;
  }

  public static long parseTimestampUs(String paramString)
  {
    int i = 0;
    long l = 0L;
    paramString = paramString.split("\\.", 2);
    String[] arrayOfString = paramString[0].split(":");
    int j = arrayOfString.length;
    while (i < j)
    {
      l = l * 60L + Long.parseLong(arrayOfString[i]);
      i += 1;
    }
    return (Long.parseLong(paramString[1]) + l * 1000L) * 1000L;
  }

  public static void validateWebvttHeaderLine(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray = paramParsableByteArray.readLine();
    if ((paramParsableByteArray == null) || (!HEADER.matcher(paramParsableByteArray).matches()))
      throw new SubtitleDecoderException("Expected WEBVTT. Got " + paramParsableByteArray);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.webvtt.WebvttParserUtil
 * JD-Core Version:    0.6.0
 */