package org.vidogram.messenger.exoplayer2.text.subrip;

import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.vidogram.messenger.exoplayer2.util.LongArray;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class SubripDecoder extends SimpleSubtitleDecoder
{
  private static final Pattern SUBRIP_TIMESTAMP;
  private static final Pattern SUBRIP_TIMING_LINE = Pattern.compile("(\\S*)\\s*-->\\s*(\\S*)");
  private static final String TAG = "SubripDecoder";
  private final StringBuilder textBuilder = new StringBuilder();

  static
  {
    SUBRIP_TIMESTAMP = Pattern.compile("(?:(\\d+):)?(\\d+):(\\d+),(\\d+)");
  }

  public SubripDecoder()
  {
    super("SubripDecoder");
  }

  private static long parseTimecode(String paramString)
  {
    paramString = SUBRIP_TIMESTAMP.matcher(paramString);
    if (!paramString.matches())
      throw new NumberFormatException("has invalid format");
    long l1 = Long.parseLong(paramString.group(1));
    long l2 = Long.parseLong(paramString.group(2));
    long l3 = Long.parseLong(paramString.group(3));
    return (Long.parseLong(paramString.group(4)) + (l1 * 60L * 60L * 1000L + l2 * 60L * 1000L + l3 * 1000L)) * 1000L;
  }

  protected SubripSubtitle decode(byte[] paramArrayOfByte, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    LongArray localLongArray = new LongArray();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    String str;
    do
    {
      str = paramArrayOfByte.readLine();
      if (str == null)
        break;
    }
    while (str.length() == 0);
    while (true)
    {
      try
      {
        Integer.parseInt(str);
        str = paramArrayOfByte.readLine();
        Matcher localMatcher = SUBRIP_TIMING_LINE.matcher(str);
        if (!localMatcher.find())
          break label207;
        localLongArray.add(parseTimecode(localMatcher.group(1)));
        if (TextUtils.isEmpty(localMatcher.group(2)))
          break label299;
        localLongArray.add(parseTimecode(localMatcher.group(2)));
        paramInt = 1;
        this.textBuilder.setLength(0);
        str = paramArrayOfByte.readLine();
        if (TextUtils.isEmpty(str))
          break label236;
        if (this.textBuilder.length() <= 0)
          continue;
        this.textBuilder.append("<br>");
        this.textBuilder.append(str.trim());
        continue;
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.w("SubripDecoder", "Skipping invalid index: " + str);
      }
      break;
      label207: Log.w("SubripDecoder", "Skipping invalid timing: " + str);
      break;
      label236: localArrayList.add(new Cue(Html.fromHtml(this.textBuilder.toString())));
      if (paramInt == 0)
        break;
      localArrayList.add(null);
      break;
      paramArrayOfByte = new Cue[localArrayList.size()];
      localArrayList.toArray(paramArrayOfByte);
      return new SubripSubtitle(paramArrayOfByte, localLongArray.toArray());
      label299: paramInt = 0;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.subrip.SubripDecoder
 * JD-Core Version:    0.6.0
 */