package org.vidogram.messenger;

import java.util.Locale;

public class VideoEditedInfo
{
  public int bitrate;
  public long endTime;
  public long estimatedDuration;
  public long estimatedSize;
  public int originalHeight;
  public String originalPath;
  public int originalWidth;
  public int resultHeight;
  public int resultWidth;
  public int rotationValue;
  public long startTime;

  public String getString()
  {
    return String.format(Locale.US, "-1_%d_%d_%d_%d_%d_%d_%d_%d_%s", new Object[] { Long.valueOf(this.startTime), Long.valueOf(this.endTime), Integer.valueOf(this.rotationValue), Integer.valueOf(this.originalWidth), Integer.valueOf(this.originalHeight), Integer.valueOf(this.bitrate), Integer.valueOf(this.resultWidth), Integer.valueOf(this.resultHeight), this.originalPath });
  }

  public boolean parseString(String paramString)
  {
    if (paramString.length() < 6)
      return false;
    while (true)
    {
      int i;
      try
      {
        paramString = paramString.split("_");
        if (paramString.length >= 10)
        {
          this.startTime = Long.parseLong(paramString[1]);
          this.endTime = Long.parseLong(paramString[2]);
          this.rotationValue = Integer.parseInt(paramString[3]);
          this.originalWidth = Integer.parseInt(paramString[4]);
          this.originalHeight = Integer.parseInt(paramString[5]);
          this.bitrate = Integer.parseInt(paramString[6]);
          this.resultWidth = Integer.parseInt(paramString[7]);
          this.resultHeight = Integer.parseInt(paramString[8]);
          i = 9;
          if (i < paramString.length)
          {
            if (this.originalPath != null)
              continue;
            this.originalPath = paramString[i];
            break label178;
            this.originalPath = (this.originalPath + "_" + paramString[i]);
          }
        }
      }
      catch (java.lang.Exception paramString)
      {
        FileLog.e(paramString);
        return false;
      }
      return true;
      label178: i += 1;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.VideoEditedInfo
 * JD-Core Version:    0.6.0
 */