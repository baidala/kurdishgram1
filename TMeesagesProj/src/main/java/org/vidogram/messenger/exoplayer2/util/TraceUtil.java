package org.vidogram.messenger.exoplayer2.util;

import android.annotation.TargetApi;
import android.os.Trace;

public final class TraceUtil
{
  public static void beginSection(String paramString)
  {
    if (Util.SDK_INT >= 18)
      beginSectionV18(paramString);
  }

  @TargetApi(18)
  private static void beginSectionV18(String paramString)
  {
    Trace.beginSection(paramString);
  }

  public static void endSection()
  {
    if (Util.SDK_INT >= 18)
      endSectionV18();
  }

  @TargetApi(18)
  private static void endSectionV18()
  {
    Trace.endSection();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.TraceUtil
 * JD-Core Version:    0.6.0
 */