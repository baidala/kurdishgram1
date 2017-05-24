package org.vidogram.messenger.exoplayer2.extractor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.id3.CommentFrame;

public final class GaplessInfoHolder
{
  private static final String GAPLESS_COMMENT_ID = "iTunSMPB";
  private static final Pattern GAPLESS_COMMENT_PATTERN = Pattern.compile("^ [0-9a-fA-F]{8} ([0-9a-fA-F]{8}) ([0-9a-fA-F]{8})");
  public int encoderDelay = -1;
  public int encoderPadding = -1;

  private boolean setFromComment(String paramString1, String paramString2)
  {
    if (!"iTunSMPB".equals(paramString1));
    while (true)
    {
      return false;
      paramString1 = GAPLESS_COMMENT_PATTERN.matcher(paramString2);
      if (!paramString1.find())
        continue;
      try
      {
        int i = Integer.parseInt(paramString1.group(1), 16);
        int j = Integer.parseInt(paramString1.group(2), 16);
        if ((i <= 0) && (j <= 0))
          continue;
        this.encoderDelay = i;
        this.encoderPadding = j;
        return true;
      }
      catch (java.lang.NumberFormatException paramString1)
      {
      }
    }
    return false;
  }

  public boolean hasGaplessInfo()
  {
    return (this.encoderDelay != -1) && (this.encoderPadding != -1);
  }

  public boolean setFromMetadata(Metadata paramMetadata)
  {
    int k = 0;
    int i = 0;
    while (true)
    {
      int j = k;
      if (i < paramMetadata.length())
      {
        Object localObject = paramMetadata.get(i);
        if ((localObject instanceof CommentFrame))
        {
          localObject = (CommentFrame)localObject;
          if (setFromComment(((CommentFrame)localObject).description, ((CommentFrame)localObject).text))
            j = 1;
        }
      }
      else
      {
        return j;
      }
      i += 1;
    }
  }

  public boolean setFromXingHeaderValue(int paramInt)
  {
    int i = paramInt >> 12;
    paramInt &= 4095;
    if ((i > 0) || (paramInt > 0))
    {
      this.encoderDelay = i;
      this.encoderPadding = paramInt;
      return true;
    }
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.GaplessInfoHolder
 * JD-Core Version:    0.6.0
 */