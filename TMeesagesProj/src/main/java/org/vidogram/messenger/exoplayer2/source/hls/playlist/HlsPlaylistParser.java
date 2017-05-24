package org.vidogram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;

public final class HlsPlaylistParser
  implements ParsingLoadable.Parser<HlsPlaylist>
{
  private static final String BOOLEAN_FALSE = "NO";
  private static final String BOOLEAN_TRUE = "YES";
  private static final String METHOD_AES128 = "AES-128";
  private static final String METHOD_NONE = "NONE";
  private static final Pattern REGEX_ATTR_BYTERANGE;
  private static final Pattern REGEX_AUDIO;
  private static final Pattern REGEX_AUTOSELECT;
  private static final Pattern REGEX_BANDWIDTH;
  private static final Pattern REGEX_BYTERANGE;
  private static final Pattern REGEX_CLOSED_CAPTIONS;
  private static final Pattern REGEX_CODECS;
  private static final Pattern REGEX_DEFAULT;
  private static final Pattern REGEX_FORCED;
  private static final Pattern REGEX_GROUP_ID = Pattern.compile("GROUP-ID=\"(.+?)\"");
  private static final Pattern REGEX_INSTREAM_ID;
  private static final Pattern REGEX_IV;
  private static final Pattern REGEX_LANGUAGE;
  private static final Pattern REGEX_MEDIA_DURATION;
  private static final Pattern REGEX_MEDIA_SEQUENCE;
  private static final Pattern REGEX_METHOD;
  private static final Pattern REGEX_NAME;
  private static final Pattern REGEX_RESOLUTION;
  private static final Pattern REGEX_SUBTITLES;
  private static final Pattern REGEX_TARGET_DURATION;
  private static final Pattern REGEX_TYPE;
  private static final Pattern REGEX_URI;
  private static final Pattern REGEX_VERSION;
  private static final Pattern REGEX_VIDEO = Pattern.compile("VIDEO=\"(.+?)\"");
  private static final String TAG_BYTERANGE = "#EXT-X-BYTERANGE";
  private static final String TAG_DISCONTINUITY = "#EXT-X-DISCONTINUITY";
  private static final String TAG_DISCONTINUITY_SEQUENCE = "#EXT-X-DISCONTINUITY-SEQUENCE";
  private static final String TAG_ENDLIST = "#EXT-X-ENDLIST";
  private static final String TAG_INIT_SEGMENT = "#EXT-X-MAP";
  private static final String TAG_KEY = "#EXT-X-KEY";
  private static final String TAG_MEDIA = "#EXT-X-MEDIA";
  private static final String TAG_MEDIA_DURATION = "#EXTINF";
  private static final String TAG_MEDIA_SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
  private static final String TAG_STREAM_INF = "#EXT-X-STREAM-INF";
  private static final String TAG_TARGET_DURATION = "#EXT-X-TARGETDURATION";
  private static final String TAG_VERSION = "#EXT-X-VERSION";
  private static final String TYPE_AUDIO = "AUDIO";
  private static final String TYPE_CLOSED_CAPTIONS = "CLOSED-CAPTIONS";
  private static final String TYPE_SUBTITLES = "SUBTITLES";
  private static final String TYPE_VIDEO = "VIDEO";

  static
  {
    REGEX_AUDIO = Pattern.compile("AUDIO=\"(.+?)\"");
    REGEX_CLOSED_CAPTIONS = Pattern.compile("CLOSED-CAPTIONS=\"(.+?)\"");
    REGEX_SUBTITLES = Pattern.compile("SUBTITLES=\"(.+?)\"");
    REGEX_BANDWIDTH = Pattern.compile("BANDWIDTH=(\\d+)\\b");
    REGEX_CODECS = Pattern.compile("CODECS=\"(.+?)\"");
    REGEX_RESOLUTION = Pattern.compile("RESOLUTION=(\\d+x\\d+)");
    REGEX_VERSION = Pattern.compile("#EXT-X-VERSION:(\\d+)\\b");
    REGEX_TARGET_DURATION = Pattern.compile("#EXT-X-TARGETDURATION:(\\d+)\\b");
    REGEX_MEDIA_SEQUENCE = Pattern.compile("#EXT-X-MEDIA-SEQUENCE:(\\d+)\\b");
    REGEX_MEDIA_DURATION = Pattern.compile("#EXTINF:([\\d\\.]+)\\b");
    REGEX_BYTERANGE = Pattern.compile("#EXT-X-BYTERANGE:(\\d+(?:@\\d+)?)\\b");
    REGEX_ATTR_BYTERANGE = Pattern.compile("BYTERANGE=\"(\\d+(?:@\\d+)?)\\b\"");
    REGEX_METHOD = Pattern.compile("METHOD=(NONE|AES-128)");
    REGEX_URI = Pattern.compile("URI=\"(.+?)\"");
    REGEX_IV = Pattern.compile("IV=([^,.*]+)");
    REGEX_TYPE = Pattern.compile("TYPE=(AUDIO|VIDEO|SUBTITLES|CLOSED-CAPTIONS)");
    REGEX_LANGUAGE = Pattern.compile("LANGUAGE=\"(.+?)\"");
    REGEX_NAME = Pattern.compile("NAME=\"(.+?)\"");
    REGEX_INSTREAM_ID = Pattern.compile("INSTREAM-ID=\"(.+?)\"");
    REGEX_AUTOSELECT = compileBooleanAttrPattern("AUTOSELECT");
    REGEX_DEFAULT = compileBooleanAttrPattern("DEFAULT");
    REGEX_FORCED = compileBooleanAttrPattern("FORCED");
  }

  private static Pattern compileBooleanAttrPattern(String paramString)
  {
    return Pattern.compile(paramString + "=(" + "NO" + "|" + "YES" + ")");
  }

  private static boolean parseBooleanAttribute(String paramString, Pattern paramPattern, boolean paramBoolean)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find())
      paramBoolean = paramString.group(1).equals("YES");
    return paramBoolean;
  }

  private static double parseDoubleAttr(String paramString, Pattern paramPattern)
  {
    return Double.parseDouble(parseStringAttr(paramString, paramPattern));
  }

  private static int parseIntAttr(String paramString, Pattern paramPattern)
  {
    return Integer.parseInt(parseStringAttr(paramString, paramPattern));
  }

  private static HlsMasterPlaylist parseMasterPlaylist(LineIterator paramLineIterator, String paramString)
  {
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    Object localObject1 = null;
    Object localObject2 = null;
    while (paramLineIterator.hasNext())
    {
      Object localObject3 = paramLineIterator.next();
      String str2;
      int i;
      if (((String)localObject3).startsWith("#EXT-X-MEDIA"))
      {
        j = parseSelectionFlags((String)localObject3);
        str1 = parseOptionalStringAttr((String)localObject3, REGEX_URI);
        str2 = parseStringAttr((String)localObject3, REGEX_NAME);
        String str3 = parseOptionalStringAttr((String)localObject3, REGEX_LANGUAGE);
        String str4 = parseStringAttr((String)localObject3, REGEX_TYPE);
        i = -1;
        switch (str4.hashCode())
        {
        default:
          switch (i)
          {
          default:
            label144: label172: localObject3 = localObject2;
            localObject2 = localObject1;
            localObject1 = localObject3;
          case 0:
          case 1:
          case 2:
          }
        case 62628790:
        case -959297733:
        case -333210994:
        }
        while (true)
        {
          localObject3 = localObject2;
          localObject2 = localObject1;
          localObject1 = localObject3;
          break;
          if (!str4.equals("AUDIO"))
            break label144;
          i = 0;
          break label144;
          if (!str4.equals("SUBTITLES"))
            break label144;
          i = 1;
          break label144;
          if (!str4.equals("CLOSED-CAPTIONS"))
            break label144;
          i = 2;
          break label144;
          localObject3 = Format.createAudioContainerFormat(str2, "application/x-mpegURL", null, null, -1, -1, -1, null, j, str3);
          if (str1 == null)
          {
            localObject1 = localObject2;
            localObject2 = localObject3;
            continue;
          }
          localArrayList2.add(new HlsMasterPlaylist.HlsUrl(str2, str1, (Format)localObject3, null, (Format)localObject3, null));
          localObject3 = localObject1;
          localObject1 = localObject2;
          localObject2 = localObject3;
          continue;
          localObject3 = Format.createTextContainerFormat(str2, "application/x-mpegURL", "text/vtt", null, -1, j, str3);
          localArrayList3.add(new HlsMasterPlaylist.HlsUrl(str2, str1, (Format)localObject3, null, (Format)localObject3, null));
          localObject3 = localObject1;
          localObject1 = localObject2;
          localObject2 = localObject3;
          continue;
          if (!"CC1".equals(parseOptionalStringAttr((String)localObject3, REGEX_INSTREAM_ID)))
            break label172;
          localObject3 = Format.createTextContainerFormat(str2, "application/x-mpegURL", "application/cea-608", null, -1, j, str3);
          localObject2 = localObject1;
          localObject1 = localObject3;
        }
      }
      if (!((String)localObject3).startsWith("#EXT-X-STREAM-INF"))
        continue;
      int m = parseIntAttr((String)localObject3, REGEX_BANDWIDTH);
      String str1 = parseOptionalStringAttr((String)localObject3, REGEX_CODECS);
      localObject3 = parseOptionalStringAttr((String)localObject3, REGEX_RESOLUTION);
      if (localObject3 != null)
      {
        localObject3 = ((String)localObject3).split("x");
        i = Integer.parseInt(localObject3[0]);
        int k = Integer.parseInt(localObject3[1]);
        if (i > 0)
        {
          j = k;
          if (k > 0);
        }
        else
        {
          i = -1;
        }
      }
      for (int j = -1; ; j = -1)
      {
        localObject3 = paramLineIterator.next();
        str2 = Integer.toString(localArrayList1.size());
        localArrayList1.add(new HlsMasterPlaylist.HlsUrl(str2, (String)localObject3, Format.createVideoContainerFormat(str2, "application/x-mpegURL", null, str1, m, i, j, -1.0F, null), null, null, null));
        break;
        i = -1;
      }
    }
    return (HlsMasterPlaylist)new HlsMasterPlaylist(paramString, localArrayList1, localArrayList2, localArrayList3, localObject1, localObject2);
  }

  private static HlsMediaPlaylist parseMediaPlaylist(LineIterator paramLineIterator, String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    long l2 = -1L;
    boolean bool2 = false;
    Object localObject4 = null;
    int i = 0;
    long l3 = 0L;
    Object localObject3 = null;
    boolean bool1 = false;
    int k = 1;
    int m = 0;
    Object localObject1 = null;
    int j = 0;
    long l4 = 0L;
    String str;
    Object localObject2;
    if (paramLineIterator.hasNext())
    {
      str = paramLineIterator.next();
      if (str.startsWith("#EXT-X-MAP"))
      {
        localObject2 = parseStringAttr(str, REGEX_URI);
        localObject3 = parseOptionalStringAttr(str, REGEX_ATTR_BYTERANGE);
        if (localObject3 == null)
          break label602;
        localObject3 = ((String)localObject3).split("@");
        long l5 = Long.parseLong(localObject3[0]);
        l2 = l5;
        if (localObject3.length > 1)
        {
          l1 = Long.parseLong(localObject3[1]);
          l2 = l5;
        }
      }
    }
    label455: label596: label599: label602: 
    while (true)
    {
      localObject3 = new HlsMediaPlaylist.Segment((String)localObject2, l1, l2);
      l1 = 0L;
      l2 = -1L;
      break;
      if (str.startsWith("#EXT-X-TARGETDURATION"))
      {
        parseIntAttr(str, REGEX_TARGET_DURATION);
        break;
      }
      if (str.startsWith("#EXT-X-MEDIA-SEQUENCE"))
      {
        m = parseIntAttr(str, REGEX_MEDIA_SEQUENCE);
        j = m;
        break;
      }
      if (str.startsWith("#EXT-X-VERSION"))
      {
        k = parseIntAttr(str, REGEX_VERSION);
        break;
      }
      if (str.startsWith("#EXTINF"))
      {
        l3 = ()(parseDoubleAttr(str, REGEX_MEDIA_DURATION) * 1000000.0D);
        break;
      }
      if (str.startsWith("#EXT-X-KEY"))
      {
        bool2 = "AES-128".equals(parseStringAttr(str, REGEX_METHOD));
        if (bool2)
          localObject1 = parseStringAttr(str, REGEX_URI);
        for (localObject2 = parseOptionalStringAttr(str, REGEX_IV); ; localObject2 = null)
        {
          localObject4 = localObject1;
          localObject1 = localObject2;
          break;
          localObject1 = null;
        }
      }
      if (str.startsWith("#EXT-X-BYTERANGE"))
      {
        localObject2 = parseStringAttr(str, REGEX_BYTERANGE).split("@");
        l2 = Long.parseLong(localObject2[0]);
        if (localObject2.length <= 1)
          break label599;
        l1 = Long.parseLong(localObject2[1]);
      }
      while (true)
      {
        break;
        if (str.startsWith("#EXT-X-DISCONTINUITY-SEQUENCE"))
        {
          i = Integer.parseInt(str.substring(str.indexOf(':') + 1));
          break;
        }
        if (str.equals("#EXT-X-DISCONTINUITY"))
        {
          i += 1;
          break;
        }
        if (!str.startsWith("#"))
          if (!bool2)
          {
            localObject2 = null;
            if (l2 != -1L)
              break label596;
            l1 = 0L;
          }
        while (true)
        {
          localArrayList.add(new HlsMediaPlaylist.Segment(str, l3, i, l4, bool2, localObject4, (String)localObject2, l1, l2));
          if (l2 != -1L)
            l1 += l2;
          while (true)
          {
            l2 = -1L;
            j += 1;
            l4 += l3;
            l3 = 0L;
            break;
            if (localObject1 != null)
            {
              localObject2 = localObject1;
              break label455;
            }
            localObject2 = Integer.toHexString(j);
            break label455;
            if (!str.equals("#EXT-X-ENDLIST"))
              break;
            bool1 = true;
            break;
            return new HlsMediaPlaylist(paramString, m, k, bool1, (HlsMediaPlaylist.Segment)localObject3, localArrayList);
          }
        }
      }
    }
  }

  private static String parseOptionalStringAttr(String paramString, Pattern paramPattern)
  {
    paramString = paramPattern.matcher(paramString);
    if (paramString.find())
      return paramString.group(1);
    return null;
  }

  private static int parseSelectionFlags(String paramString)
  {
    int k = 0;
    int i;
    if (parseBooleanAttribute(paramString, REGEX_DEFAULT, false))
    {
      i = 1;
      if (!parseBooleanAttribute(paramString, REGEX_FORCED, false))
        break label52;
    }
    label52: for (int j = 2; ; j = 0)
    {
      if (parseBooleanAttribute(paramString, REGEX_AUTOSELECT, false))
        k = 4;
      return i | j | k;
      i = 0;
      break;
    }
  }

  private static String parseStringAttr(String paramString, Pattern paramPattern)
  {
    Matcher localMatcher = paramPattern.matcher(paramString);
    if ((localMatcher.find()) && (localMatcher.groupCount() == 1))
      return localMatcher.group(1);
    throw new ParserException("Couldn't match " + paramPattern.pattern() + " in " + paramString);
  }

  public HlsPlaylist parse(Uri paramUri, InputStream paramInputStream)
  {
    paramInputStream = new BufferedReader(new InputStreamReader(paramInputStream));
    LinkedList localLinkedList = new LinkedList();
    try
    {
      while (true)
      {
        String str = paramInputStream.readLine();
        if (str == null)
          break;
        str = str.trim();
        if (str.isEmpty())
          continue;
        if (str.startsWith("#EXT-X-STREAM-INF"))
        {
          localLinkedList.add(str);
          paramUri = parseMasterPlaylist(new LineIterator(localLinkedList, paramInputStream), paramUri.toString());
          return paramUri;
        }
        if ((str.startsWith("#EXT-X-TARGETDURATION")) || (str.startsWith("#EXT-X-MEDIA-SEQUENCE")) || (str.startsWith("#EXTINF")) || (str.startsWith("#EXT-X-KEY")) || (str.startsWith("#EXT-X-BYTERANGE")) || (str.equals("#EXT-X-DISCONTINUITY")) || (str.equals("#EXT-X-DISCONTINUITY-SEQUENCE")) || (str.equals("#EXT-X-ENDLIST")))
        {
          localLinkedList.add(str);
          paramUri = parseMediaPlaylist(new LineIterator(localLinkedList, paramInputStream), paramUri.toString());
          return paramUri;
        }
        localLinkedList.add(str);
      }
    }
    finally
    {
      paramInputStream.close();
    }
    paramInputStream.close();
    throw new ParserException("Failed to parse the playlist, could not identify any tags.");
  }

  private static class LineIterator
  {
    private final Queue<String> extraLines;
    private String next;
    private final BufferedReader reader;

    public LineIterator(Queue<String> paramQueue, BufferedReader paramBufferedReader)
    {
      this.extraLines = paramQueue;
      this.reader = paramBufferedReader;
    }

    public boolean hasNext()
    {
      if (this.next != null)
        return true;
      if (!this.extraLines.isEmpty())
      {
        this.next = ((String)this.extraLines.poll());
        return true;
      }
      while (true)
      {
        String str = this.reader.readLine();
        this.next = str;
        if (str == null)
          break;
        this.next = this.next.trim();
        if (!this.next.isEmpty())
          return true;
      }
      return false;
    }

    public String next()
    {
      if (hasNext())
      {
        String str = this.next;
        this.next = null;
        return str;
      }
      return null;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser
 * JD-Core Version:    0.6.0
 */