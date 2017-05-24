package org.vidogram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.Pair;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.vidogram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.vidogram.messenger.exoplayer2.upstream.ParsingLoadable.Parser;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;
import org.vidogram.messenger.exoplayer2.util.UriUtil;
import org.vidogram.messenger.exoplayer2.util.Util;
import org.vidogram.messenger.exoplayer2.util.XmlPullParserUtil;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class DashManifestParser extends DefaultHandler
  implements ParsingLoadable.Parser<DashManifest>
{
  private static final Pattern CEA_608_ACCESSIBILITY_PATTERN;
  private static final Pattern CEA_708_ACCESSIBILITY_PATTERN;
  private static final Pattern FRAME_RATE_PATTERN = Pattern.compile("(\\d+)(?:/(\\d+))?");
  private static final String TAG = "MpdParser";
  private final String contentId;
  private final XmlPullParserFactory xmlParserFactory;

  static
  {
    CEA_608_ACCESSIBILITY_PATTERN = Pattern.compile("CC([1-4])=.*");
    CEA_708_ACCESSIBILITY_PATTERN = Pattern.compile("([1-9]|[1-5][0-9]|6[0-3])=.*");
  }

  public DashManifestParser()
  {
    this(null);
  }

  public DashManifestParser(String paramString)
  {
    this.contentId = paramString;
    try
    {
      this.xmlParserFactory = XmlPullParserFactory.newInstance();
      return;
    }
    catch (org.xmlpull.v1.XmlPullParserException paramString)
    {
    }
    throw new RuntimeException("Couldn't create XmlPullParserFactory instance", paramString);
  }

  private static int checkContentTypeConsistency(int paramInt1, int paramInt2)
  {
    int i;
    if (paramInt1 == -1)
      i = paramInt2;
    do
    {
      return i;
      i = paramInt1;
    }
    while (paramInt2 == -1);
    if (paramInt1 == paramInt2);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkState(bool);
      return paramInt1;
    }
  }

  private static String checkLanguageConsistency(String paramString1, String paramString2)
  {
    String str;
    if (paramString1 == null)
      str = paramString2;
    do
    {
      return str;
      str = paramString1;
    }
    while (paramString2 == null);
    Assertions.checkState(paramString1.equals(paramString2));
    return paramString1;
  }

  private static String getSampleMimeType(String paramString1, String paramString2)
  {
    String str;
    if (MimeTypes.isAudio(paramString1))
      str = MimeTypes.getAudioMediaMimeType(paramString2);
    do
    {
      return str;
      if (MimeTypes.isVideo(paramString1))
        return MimeTypes.getVideoMediaMimeType(paramString2);
      if ("application/x-rawcc".equals(paramString1))
      {
        if (paramString2 != null)
        {
          if (paramString2.contains("cea708"))
            return "application/cea-708";
          if ((paramString2.contains("eia608")) || (paramString2.contains("cea608")))
            return "application/cea-608";
        }
        return null;
      }
      str = paramString1;
    }
    while (mimeTypeIsRawText(paramString1));
    if ("application/mp4".equals(paramString1))
    {
      if ("stpp".equals(paramString2))
        return "application/ttml+xml";
      if ("wvtt".equals(paramString2))
        return "application/x-mp4vtt";
    }
    return null;
  }

  private static boolean mimeTypeIsRawText(String paramString)
  {
    return (MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString));
  }

  private static int parseAccessibilityValue(XmlPullParser paramXmlPullParser)
  {
    int j = -1;
    String str1 = parseString(paramXmlPullParser, "schemeIdUri", null);
    String str2 = parseString(paramXmlPullParser, "value", null);
    int i = j;
    if (str1 != null)
    {
      if (str2 != null)
        break label50;
      i = j;
    }
    while (true)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "Accessibility"))
      {
        return i;
        label50: if ("urn:scte:dash:cc:cea-608:2015".equals(str1))
        {
          i = parseCea608AccessibilityChannel(str2);
          continue;
        }
        i = j;
        if (!"urn:scte:dash:cc:cea-708:2015".equals(str1))
          continue;
        i = parseCea708AccessibilityChannel(str2);
      }
    }
  }

  protected static String parseBaseUrl(XmlPullParser paramXmlPullParser, String paramString)
  {
    paramXmlPullParser.next();
    return UriUtil.resolve(paramString, paramXmlPullParser.getText());
  }

  static int parseCea608AccessibilityChannel(String paramString)
  {
    if (paramString == null)
      return -1;
    Matcher localMatcher = CEA_608_ACCESSIBILITY_PATTERN.matcher(paramString);
    if (localMatcher.matches())
      return Integer.parseInt(localMatcher.group(1));
    Log.w("MpdParser", "Unable to parse channel number from " + paramString);
    return -1;
  }

  static int parseCea708AccessibilityChannel(String paramString)
  {
    if (paramString == null)
      return -1;
    Matcher localMatcher = CEA_708_ACCESSIBILITY_PATTERN.matcher(paramString);
    if (localMatcher.matches())
      return Integer.parseInt(localMatcher.group(1));
    Log.w("MpdParser", "Unable to parse service block number from " + paramString);
    return -1;
  }

  protected static long parseDateTime(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null)
      return paramLong;
    return Util.parseXsDateTime(paramXmlPullParser);
  }

  protected static long parseDuration(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null)
      return paramLong;
    return Util.parseXsDuration(paramXmlPullParser);
  }

  protected static float parseFrameRate(XmlPullParser paramXmlPullParser, float paramFloat)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "frameRate");
    float f = paramFloat;
    int i;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = FRAME_RATE_PATTERN.matcher(paramXmlPullParser);
      f = paramFloat;
      if (paramXmlPullParser.matches())
      {
        i = Integer.parseInt(paramXmlPullParser.group(1));
        paramXmlPullParser = paramXmlPullParser.group(2);
        if (TextUtils.isEmpty(paramXmlPullParser))
          break label66;
        f = i / Integer.parseInt(paramXmlPullParser);
      }
    }
    return f;
    label66: return i;
  }

  protected static int parseInt(XmlPullParser paramXmlPullParser, String paramString, int paramInt)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null)
      return paramInt;
    return Integer.parseInt(paramXmlPullParser);
  }

  protected static long parseLong(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser == null)
      return paramLong;
    return Long.parseLong(paramXmlPullParser);
  }

  protected static String parseString(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString1);
    if (paramXmlPullParser == null)
      return paramString2;
    return paramXmlPullParser;
  }

  protected AdaptationSet buildAdaptationSet(int paramInt1, int paramInt2, List<Representation> paramList)
  {
    return new AdaptationSet(paramInt1, paramInt2, paramList);
  }

  protected Format buildFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, int paramInt5, String paramString3, int paramInt6, String paramString4)
  {
    String str = getSampleMimeType(paramString2, paramString4);
    if (str != null)
    {
      if (MimeTypes.isVideo(str))
        return Format.createVideoContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt1, paramInt2, paramFloat, null);
      if (MimeTypes.isAudio(str))
        return Format.createAudioContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, paramInt3, paramInt4, null, 0, paramString3);
      if (mimeTypeIsRawText(str))
        return Format.createTextContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, 0, paramString3, paramInt6);
      if (paramString2.equals("application/x-rawcc"))
        return Format.createTextContainerFormat(paramString1, paramString2, str, paramString4, paramInt5, 0, paramString3, paramInt6);
      return Format.createContainerFormat(paramString1, paramString2, paramString4, str, paramInt5);
    }
    return Format.createContainerFormat(paramString1, paramString2, paramString4, str, paramInt5);
  }

  protected DashManifest buildMediaPresentationDescription(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, UtcTimingElement paramUtcTimingElement, Uri paramUri, List<Period> paramList)
  {
    return new DashManifest(paramLong1, paramLong2, paramLong3, paramBoolean, paramLong4, paramLong5, paramLong6, paramUtcTimingElement, paramUri, paramList);
  }

  protected Period buildPeriod(String paramString, long paramLong, List<AdaptationSet> paramList)
  {
    return new Period(paramString, paramLong, paramList);
  }

  protected RangedUri buildRangedUri(String paramString, long paramLong1, long paramLong2)
  {
    return new RangedUri(paramString, paramLong1, paramLong2);
  }

  protected Representation buildRepresentation(RepresentationInfo paramRepresentationInfo, String paramString, ArrayList<DrmInitData.SchemeData> paramArrayList)
  {
    Format localFormat = paramRepresentationInfo.format;
    ArrayList localArrayList = paramRepresentationInfo.drmSchemeDatas;
    localArrayList.addAll(paramArrayList);
    paramArrayList = localFormat;
    if (!localArrayList.isEmpty())
      paramArrayList = localFormat.copyWithDrmInitData(new DrmInitData(localArrayList));
    return Representation.newInstance(paramString, -1L, paramArrayList, paramRepresentationInfo.baseUrl, paramRepresentationInfo.segmentBase);
  }

  protected SegmentBase.SegmentList buildSegmentList(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, List<RangedUri> paramList1)
  {
    return new SegmentBase.SegmentList(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramList1);
  }

  protected SegmentBase.SegmentTemplate buildSegmentTemplate(RangedUri paramRangedUri, long paramLong1, long paramLong2, int paramInt, long paramLong3, List<SegmentBase.SegmentTimelineElement> paramList, UrlTemplate paramUrlTemplate1, UrlTemplate paramUrlTemplate2)
  {
    return new SegmentBase.SegmentTemplate(paramRangedUri, paramLong1, paramLong2, paramInt, paramLong3, paramList, paramUrlTemplate1, paramUrlTemplate2);
  }

  protected SegmentBase.SegmentTimelineElement buildSegmentTimelineElement(long paramLong1, long paramLong2)
  {
    return new SegmentBase.SegmentTimelineElement(paramLong1, paramLong2);
  }

  protected SegmentBase.SingleSegmentBase buildSingleSegmentBase(RangedUri paramRangedUri, long paramLong1, long paramLong2, long paramLong3, long paramLong4)
  {
    return new SegmentBase.SingleSegmentBase(paramRangedUri, paramLong1, paramLong2, paramLong3, paramLong4);
  }

  protected UtcTimingElement buildUtcTimingElement(String paramString1, String paramString2)
  {
    return new UtcTimingElement(paramString1, paramString2);
  }

  protected int getContentType(Format paramFormat)
  {
    String str = paramFormat.sampleMimeType;
    if (TextUtils.isEmpty(str));
    do
    {
      return -1;
      if (MimeTypes.isVideo(str))
        return 2;
      if (MimeTypes.isAudio(str))
        return 1;
    }
    while ((!mimeTypeIsRawText(str)) && (!"application/x-rawcc".equals(paramFormat.containerMimeType)));
    return 3;
  }

  public DashManifest parse(Uri paramUri, InputStream paramInputStream)
  {
    XmlPullParser localXmlPullParser;
    try
    {
      localXmlPullParser = this.xmlParserFactory.newPullParser();
      localXmlPullParser.setInput(paramInputStream, null);
      if ((localXmlPullParser.next() != 2) || (!"MPD".equals(localXmlPullParser.getName())))
        throw new ParserException("inputStream does not contain a valid media presentation description");
    }
    catch (org.xmlpull.v1.XmlPullParserException paramUri)
    {
      throw new ParserException(paramUri);
    }
    paramUri = parseMediaPresentationDescription(localXmlPullParser, paramUri.toString());
    return paramUri;
  }

  protected AdaptationSet parseAdaptationSet(XmlPullParser paramXmlPullParser, String paramString, SegmentBase paramSegmentBase)
  {
    int n = parseInt(paramXmlPullParser, "id", -1);
    int j = parseContentType(paramXmlPullParser);
    String str2 = paramXmlPullParser.getAttributeValue(null, "mimeType");
    String str3 = paramXmlPullParser.getAttributeValue(null, "codecs");
    int i1 = parseInt(paramXmlPullParser, "width", -1);
    int i2 = parseInt(paramXmlPullParser, "height", -1);
    float f = parseFrameRate(paramXmlPullParser, -1.0F);
    int m = -1;
    int i3 = parseInt(paramXmlPullParser, "audioSamplingRate", -1);
    Object localObject = paramXmlPullParser.getAttributeValue(null, "lang");
    int k = -1;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    int i = 0;
    String str1 = paramString;
    paramString = paramSegmentBase;
    paramSegmentBase = (SegmentBase)localObject;
    while (true)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        if (i != 0)
          break label496;
        str1 = parseBaseUrl(paramXmlPullParser, str1);
        i = 1;
      }
      while (XmlPullParserUtil.isEndTag(paramXmlPullParser, "AdaptationSet"))
      {
        paramXmlPullParser = new ArrayList(localArrayList2.size());
        i = 0;
        while (true)
          if (i < localArrayList2.size())
          {
            paramXmlPullParser.add(buildRepresentation((RepresentationInfo)localArrayList2.get(i), this.contentId, localArrayList1));
            i += 1;
            continue;
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
            {
              localObject = parseContentProtection(paramXmlPullParser);
              if (localObject != null)
                localArrayList1.add(localObject);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentComponent"))
            {
              paramSegmentBase = checkLanguageConsistency(paramSegmentBase, paramXmlPullParser.getAttributeValue(null, "lang"));
              j = checkContentTypeConsistency(j, parseContentType(paramXmlPullParser));
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Representation"))
            {
              localObject = parseRepresentation(paramXmlPullParser, str1, str2, str3, i1, i2, f, m, i3, paramSegmentBase, k, paramString);
              j = checkContentTypeConsistency(j, getContentType(((RepresentationInfo)localObject).format));
              localArrayList2.add(localObject);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
            {
              m = parseAudioChannelConfiguration(paramXmlPullParser);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Accessibility"))
            {
              k = parseAccessibilityValue(paramXmlPullParser);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
            {
              paramString = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)paramString);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
            {
              paramString = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)paramString);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
            {
              paramString = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)paramString);
              break;
            }
            if (XmlPullParserUtil.isStartTag(paramXmlPullParser))
              parseAdaptationSetChild(paramXmlPullParser);
            label496: break;
          }
        return buildAdaptationSet(n, j, paramXmlPullParser);
      }
    }
  }

  protected void parseAdaptationSetChild(XmlPullParser paramXmlPullParser)
  {
  }

  protected int parseAudioChannelConfiguration(XmlPullParser paramXmlPullParser)
  {
    int i = -1;
    if ("urn:mpeg:dash:23003:3:audio_channel_configuration:2011".equals(parseString(paramXmlPullParser, "schemeIdUri", null)))
      i = parseInt(paramXmlPullParser, "value", -1);
    do
      paramXmlPullParser.next();
    while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "AudioChannelConfiguration"));
    return i;
  }

  protected DrmInitData.SchemeData parseContentProtection(XmlPullParser paramXmlPullParser)
  {
    boolean bool2 = false;
    int i = 0;
    Object localObject2 = null;
    Object localObject1 = null;
    paramXmlPullParser.next();
    Object localObject3;
    Object localObject4;
    int j;
    boolean bool1;
    if ((XmlPullParserUtil.isStartTag(paramXmlPullParser, "cenc:pssh")) && (paramXmlPullParser.next() == 4))
    {
      localObject3 = Base64.decode(paramXmlPullParser.getText(), 0);
      localObject4 = PsshAtomUtil.parseUuid(localObject3);
      j = 1;
      bool1 = bool2;
    }
    while (true)
    {
      bool2 = bool1;
      i = j;
      localObject2 = localObject4;
      localObject1 = localObject3;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "ContentProtection"))
        break;
      if (j != 0)
        break label177;
      return null;
      bool1 = bool2;
      j = i;
      localObject4 = localObject2;
      localObject3 = localObject1;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "widevine:license"))
        continue;
      localObject3 = paramXmlPullParser.getAttributeValue(null, "robustness_level");
      if ((localObject3 != null) && (((String)localObject3).startsWith("HW")))
      {
        bool1 = true;
        j = i;
        localObject4 = localObject2;
        localObject3 = localObject1;
        continue;
      }
      bool1 = false;
      j = i;
      localObject4 = localObject2;
      localObject3 = localObject1;
    }
    label177: if (localObject4 != null)
      return new DrmInitData.SchemeData((UUID)localObject4, "video/mp4", localObject3, bool1);
    Log.w("MpdParser", "Skipped unsupported ContentProtection element");
    return (DrmInitData.SchemeData)(DrmInitData.SchemeData)null;
  }

  protected int parseContentType(XmlPullParser paramXmlPullParser)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, "contentType");
    if (TextUtils.isEmpty(paramXmlPullParser));
    do
    {
      return -1;
      if ("audio".equals(paramXmlPullParser))
        return 1;
      if ("video".equals(paramXmlPullParser))
        return 2;
    }
    while (!"text".equals(paramXmlPullParser));
    return 3;
  }

  protected RangedUri parseInitialization(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "sourceURL", "range");
  }

  protected DashManifest parseMediaPresentationDescription(XmlPullParser paramXmlPullParser, String paramString)
  {
    long l7 = parseDateTime(paramXmlPullParser, "availabilityStartTime", -9223372036854775807L);
    long l6 = parseDuration(paramXmlPullParser, "mediaPresentationDuration", -9223372036854775807L);
    long l8 = parseDuration(paramXmlPullParser, "minBufferTime", -9223372036854775807L);
    Object localObject = paramXmlPullParser.getAttributeValue(null, "type");
    boolean bool;
    long l3;
    label84: long l4;
    label101: long l5;
    label118: ArrayList localArrayList;
    long l1;
    label140: int j;
    Uri localUri;
    if ((localObject != null) && (((String)localObject).equals("dynamic")))
    {
      bool = true;
      if (!bool)
        break label244;
      l3 = parseDuration(paramXmlPullParser, "minimumUpdatePeriod", -9223372036854775807L);
      if (!bool)
        break label252;
      l4 = parseDuration(paramXmlPullParser, "timeShiftBufferDepth", -9223372036854775807L);
      if (!bool)
        break label260;
      l5 = parseDuration(paramXmlPullParser, "suggestedPresentationDelay", -9223372036854775807L);
      localObject = null;
      localArrayList = new ArrayList();
      if (!bool)
        break label268;
      l1 = -9223372036854775807L;
      j = 0;
      localUri = null;
    }
    int k;
    for (int i = 0; ; i = k)
    {
      paramXmlPullParser.next();
      long l2;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        l2 = l1;
        if (j != 0)
          break label481;
        paramString = parseBaseUrl(paramXmlPullParser, paramString);
        k = 1;
        j = i;
        i = k;
        label189: if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "MPD"))
          break label559;
        if (l6 != -9223372036854775807L)
          break label552;
        if (l1 == -9223372036854775807L)
          break label511;
      }
      while (true)
      {
        if (localArrayList.isEmpty())
        {
          throw new ParserException("No periods found.");
          bool = false;
          break;
          label244: l3 = -9223372036854775807L;
          break label84;
          label252: l4 = -9223372036854775807L;
          break label101;
          label260: l5 = -9223372036854775807L;
          break label118;
          label268: l1 = 0L;
          break label140;
          if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "UTCTiming"))
          {
            localObject = parseUtcTiming(paramXmlPullParser);
            k = i;
            i = j;
            j = k;
            break label189;
          }
          if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Location"))
          {
            localUri = Uri.parse(paramXmlPullParser.nextText());
            k = i;
            i = j;
            j = k;
            break label189;
          }
          l2 = l1;
          Period localPeriod;
          if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "Period"))
          {
            l2 = l1;
            if (i == 0)
            {
              Pair localPair = parsePeriod(paramXmlPullParser, paramString, l1);
              localPeriod = (Period)localPair.first;
              if (localPeriod.startMs == -9223372036854775807L)
              {
                if (bool)
                {
                  k = 1;
                  i = j;
                  j = k;
                  break label189;
                }
                throw new ParserException("Unable to determine start of period " + localArrayList.size());
              }
              l1 = ((Long)localPair.second).longValue();
              if (l1 != -9223372036854775807L)
                break label498;
              l2 = -9223372036854775807L;
            }
          }
          while (true)
          {
            localArrayList.add(localPeriod);
            label481: k = i;
            i = j;
            j = k;
            l1 = l2;
            break;
            label498: l2 = l1 + localPeriod.startMs;
          }
          label511: if (!bool)
            throw new ParserException("Unable to determine duration of static manifest.");
        }
        else
        {
          return buildMediaPresentationDescription(l7, l1, l8, bool, l3, l4, l5, (UtcTimingElement)localObject, localUri, localArrayList);
        }
        label552: l1 = l6;
      }
      label559: k = j;
      j = i;
    }
  }

  protected Pair<Period, Long> parsePeriod(XmlPullParser paramXmlPullParser, String paramString, long paramLong)
  {
    String str2 = paramXmlPullParser.getAttributeValue(null, "id");
    paramLong = parseDuration(paramXmlPullParser, "start", paramLong);
    long l = parseDuration(paramXmlPullParser, "duration", -9223372036854775807L);
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    String str1 = null;
    Object localObject1 = paramString;
    paramXmlPullParser.next();
    int j;
    Object localObject2;
    if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
    {
      j = i;
      paramString = str1;
      localObject2 = localObject1;
      if (i == 0)
      {
        localObject2 = parseBaseUrl(paramXmlPullParser, (String)localObject1);
        j = 1;
        paramString = str1;
      }
    }
    while (true)
    {
      i = j;
      str1 = paramString;
      localObject1 = localObject2;
      if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Period"))
        break;
      return Pair.create(buildPeriod(str2, paramLong, localArrayList), Long.valueOf(l));
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AdaptationSet"))
      {
        localArrayList.add(parseAdaptationSet(paramXmlPullParser, (String)localObject1, str1));
        j = i;
        paramString = str1;
        localObject2 = localObject1;
        continue;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
      {
        paramString = parseSegmentBase(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
        continue;
      }
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
      {
        paramString = parseSegmentList(paramXmlPullParser, null);
        j = i;
        localObject2 = localObject1;
        continue;
      }
      j = i;
      paramString = str1;
      localObject2 = localObject1;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
        continue;
      paramString = parseSegmentTemplate(paramXmlPullParser, null);
      j = i;
      localObject2 = localObject1;
    }
  }

  protected RangedUri parseRangedUrl(XmlPullParser paramXmlPullParser, String paramString1, String paramString2)
  {
    paramString1 = paramXmlPullParser.getAttributeValue(null, paramString1);
    long l1 = 0L;
    long l3 = -1L;
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString2);
    long l2 = l3;
    if (paramXmlPullParser != null)
    {
      paramXmlPullParser = paramXmlPullParser.split("-");
      long l4 = Long.parseLong(paramXmlPullParser[0]);
      l1 = l4;
      l2 = l3;
      if (paramXmlPullParser.length == 2)
      {
        l2 = Long.parseLong(paramXmlPullParser[1]) - l4 + 1L;
        l1 = l4;
      }
    }
    return buildRangedUri(paramString1, l1, l2);
  }

  protected RepresentationInfo parseRepresentation(XmlPullParser paramXmlPullParser, String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, float paramFloat, int paramInt3, int paramInt4, String paramString4, int paramInt5, SegmentBase paramSegmentBase)
  {
    String str1 = paramXmlPullParser.getAttributeValue(null, "id");
    int i = parseInt(paramXmlPullParser, "bandwidth", -1);
    String str2 = parseString(paramXmlPullParser, "mimeType", paramString2);
    String str3 = parseString(paramXmlPullParser, "codecs", paramString3);
    int j = parseInt(paramXmlPullParser, "width", paramInt1);
    int k = parseInt(paramXmlPullParser, "height", paramInt2);
    paramFloat = parseFrameRate(paramXmlPullParser, paramFloat);
    paramInt4 = parseInt(paramXmlPullParser, "audioSamplingRate", paramInt4);
    ArrayList localArrayList = new ArrayList();
    paramInt2 = 0;
    paramInt1 = paramInt3;
    paramString2 = paramSegmentBase;
    while (true)
    {
      paramXmlPullParser.next();
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "BaseURL"))
      {
        if (paramInt2 != 0)
          break label391;
        paramString3 = parseBaseUrl(paramXmlPullParser, paramString1);
        paramString1 = paramString2;
        paramString2 = paramString3;
        paramInt3 = 1;
        paramInt2 = paramInt1;
        paramInt1 = paramInt3;
        if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "Representation"))
          break label426;
        paramXmlPullParser = buildFormat(str1, str2, j, k, paramFloat, paramInt2, paramInt4, i, paramString4, paramInt5, str3);
        if (paramString1 == null)
          break label414;
      }
      while (true)
      {
        return new RepresentationInfo(paramXmlPullParser, paramString2, paramString1, localArrayList);
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "AudioChannelConfiguration"))
        {
          paramInt3 = parseAudioChannelConfiguration(paramXmlPullParser);
          paramString3 = paramString1;
          paramInt1 = paramInt2;
          paramInt2 = paramInt3;
          paramString1 = paramString2;
          paramString2 = paramString3;
          break;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentBase"))
        {
          paramString3 = parseSegmentBase(paramXmlPullParser, (SegmentBase.SingleSegmentBase)paramString2);
          paramString2 = paramString1;
          paramInt3 = paramInt1;
          paramInt1 = paramInt2;
          paramInt2 = paramInt3;
          paramString1 = paramString3;
          break;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentList"))
        {
          paramString3 = parseSegmentList(paramXmlPullParser, (SegmentBase.SegmentList)paramString2);
          paramString2 = paramString1;
          paramInt3 = paramInt1;
          paramInt1 = paramInt2;
          paramInt2 = paramInt3;
          paramString1 = paramString3;
          break;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTemplate"))
        {
          paramString3 = parseSegmentTemplate(paramXmlPullParser, (SegmentBase.SegmentTemplate)paramString2);
          paramString2 = paramString1;
          paramInt3 = paramInt1;
          paramInt1 = paramInt2;
          paramInt2 = paramInt3;
          paramString1 = paramString3;
          break;
        }
        if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "ContentProtection"))
        {
          paramString3 = parseContentProtection(paramXmlPullParser);
          if (paramString3 != null)
            localArrayList.add(paramString3);
        }
        label391: paramString3 = paramString1;
        paramInt3 = paramInt1;
        paramInt1 = paramInt2;
        paramInt2 = paramInt3;
        paramString1 = paramString2;
        paramString2 = paramString3;
        break;
        label414: paramString1 = new SegmentBase.SingleSegmentBase(paramString2);
      }
      label426: paramInt3 = paramInt2;
      paramString3 = paramString2;
      paramString2 = paramString1;
      paramInt2 = paramInt1;
      paramInt1 = paramInt3;
      paramString1 = paramString3;
    }
  }

  protected SegmentBase.SingleSegmentBase parseSegmentBase(XmlPullParser paramXmlPullParser, SegmentBase.SingleSegmentBase paramSingleSegmentBase)
  {
    long l2 = 0L;
    long l1;
    long l3;
    label31: long l4;
    if (paramSingleSegmentBase != null)
    {
      l1 = paramSingleSegmentBase.timescale;
      l3 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSingleSegmentBase == null)
        break label168;
      l1 = paramSingleSegmentBase.presentationTimeOffset;
      l4 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSingleSegmentBase == null)
        break label173;
      l1 = paramSingleSegmentBase.indexStart;
      label50: if (paramSingleSegmentBase != null)
        l2 = paramSingleSegmentBase.indexLength;
      Object localObject = paramXmlPullParser.getAttributeValue(null, "indexRange");
      if (localObject == null)
        break label189;
      localObject = ((String)localObject).split("-");
      l1 = Long.parseLong(localObject[0]);
      l2 = Long.parseLong(localObject[1]) - l1 + 1L;
    }
    label168: label173: label186: label189: 
    while (true)
    {
      if (paramSingleSegmentBase != null)
      {
        paramSingleSegmentBase = paramSingleSegmentBase.initialization;
        label117: paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization"))
          break label186;
        paramSingleSegmentBase = parseInitialization(paramXmlPullParser);
      }
      while (true)
      {
        if (XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentBase"))
        {
          return buildSingleSegmentBase(paramSingleSegmentBase, l3, l4, l1, l2);
          l1 = 1L;
          break;
          l1 = 0L;
          break label31;
          l1 = 0L;
          break label50;
          paramSingleSegmentBase = null;
          break label117;
        }
        break label117;
      }
    }
  }

  protected SegmentBase.SegmentList parseSegmentList(XmlPullParser paramXmlPullParser, SegmentBase.SegmentList paramSegmentList)
  {
    Object localObject5 = null;
    long l1;
    long l2;
    label34: long l3;
    label55: int i;
    label75: Object localObject4;
    Object localObject6;
    Object localObject3;
    Object localObject2;
    Object localObject1;
    if (paramSegmentList != null)
    {
      l1 = paramSegmentList.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentList == null)
        break label189;
      l1 = paramSegmentList.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentList == null)
        break label195;
      l1 = paramSegmentList.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentList == null)
        break label203;
      i = paramSegmentList.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      localObject4 = null;
      localObject6 = null;
      while (true)
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization"))
          break;
        localObject3 = parseInitialization(paramXmlPullParser);
        localObject2 = localObject6;
        localObject1 = localObject4;
        label122: localObject4 = localObject1;
        localObject6 = localObject2;
        localObject5 = localObject3;
        if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentList"))
          continue;
        if (paramSegmentList == null)
          break label326;
        if (localObject3 == null)
          break label300;
        paramXmlPullParser = (XmlPullParser)localObject3;
        label156: if (localObject2 == null)
          break label308;
        label161: if (localObject1 == null)
          break label317;
      }
    }
    while (true)
    {
      return buildSegmentList(paramXmlPullParser, l2, l3, i, l1, (List)localObject2, (List)localObject1);
      l1 = 1L;
      break;
      label189: l1 = 0L;
      break label34;
      label195: l1 = -9223372036854775807L;
      break label55;
      label203: i = 1;
      break label75;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
      {
        localObject2 = parseSegmentTimeline(paramXmlPullParser);
        localObject1 = localObject4;
        localObject3 = localObject5;
        break label122;
      }
      localObject1 = localObject4;
      localObject2 = localObject6;
      localObject3 = localObject5;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentURL"))
        break label122;
      localObject1 = localObject4;
      if (localObject4 == null)
        localObject1 = new ArrayList();
      ((List)localObject1).add(parseSegmentUrl(paramXmlPullParser));
      localObject2 = localObject6;
      localObject3 = localObject5;
      break label122;
      label300: paramXmlPullParser = paramSegmentList.initialization;
      break label156;
      label308: localObject2 = paramSegmentList.segmentTimeline;
      break label161;
      label317: localObject1 = paramSegmentList.mediaSegments;
      continue;
      label326: paramXmlPullParser = (XmlPullParser)localObject3;
    }
  }

  protected SegmentBase.SegmentTemplate parseSegmentTemplate(XmlPullParser paramXmlPullParser, SegmentBase.SegmentTemplate paramSegmentTemplate)
  {
    long l1;
    long l2;
    label31: long l3;
    label52: int i;
    label72: Object localObject1;
    label91: UrlTemplate localUrlTemplate1;
    label113: UrlTemplate localUrlTemplate2;
    Object localObject4;
    Object localObject3;
    Object localObject2;
    if (paramSegmentTemplate != null)
    {
      l1 = paramSegmentTemplate.timescale;
      l2 = parseLong(paramXmlPullParser, "timescale", l1);
      if (paramSegmentTemplate == null)
        break label219;
      l1 = paramSegmentTemplate.presentationTimeOffset;
      l3 = parseLong(paramXmlPullParser, "presentationTimeOffset", l1);
      if (paramSegmentTemplate == null)
        break label225;
      l1 = paramSegmentTemplate.duration;
      l1 = parseLong(paramXmlPullParser, "duration", l1);
      if (paramSegmentTemplate == null)
        break label233;
      i = paramSegmentTemplate.startNumber;
      i = parseInt(paramXmlPullParser, "startNumber", i);
      if (paramSegmentTemplate == null)
        break label238;
      localObject1 = paramSegmentTemplate.mediaTemplate;
      localUrlTemplate1 = parseUrlTemplate(paramXmlPullParser, "media", (UrlTemplate)localObject1);
      if (paramSegmentTemplate == null)
        break label244;
      localObject1 = paramSegmentTemplate.initializationTemplate;
      localUrlTemplate2 = parseUrlTemplate(paramXmlPullParser, "initialization", (UrlTemplate)localObject1);
      localObject4 = null;
      localObject3 = null;
      while (true)
      {
        paramXmlPullParser.next();
        if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "Initialization"))
          break;
        localObject2 = parseInitialization(paramXmlPullParser);
        localObject1 = localObject4;
        label159: localObject4 = localObject1;
        localObject3 = localObject2;
        if (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTemplate"))
          continue;
        if (paramSegmentTemplate == null)
          break label300;
        if (localObject2 == null)
          break label282;
        label186: if (localObject1 == null)
          break label291;
      }
    }
    label191: for (paramXmlPullParser = (XmlPullParser)localObject1; ; paramXmlPullParser = (XmlPullParser)localObject1)
    {
      return buildSegmentTemplate((RangedUri)localObject2, l2, l3, i, l1, paramXmlPullParser, localUrlTemplate2, localUrlTemplate1);
      l1 = 1L;
      break;
      label219: l1 = 0L;
      break label31;
      label225: l1 = -9223372036854775807L;
      break label52;
      label233: i = 1;
      break label72;
      label238: localObject1 = null;
      break label91;
      label244: localObject1 = null;
      break label113;
      localObject1 = localObject4;
      localObject2 = localObject3;
      if (!XmlPullParserUtil.isStartTag(paramXmlPullParser, "SegmentTimeline"))
        break label159;
      localObject1 = parseSegmentTimeline(paramXmlPullParser);
      localObject2 = localObject3;
      break label159;
      localObject2 = paramSegmentTemplate.initialization;
      break label186;
      localObject1 = paramSegmentTemplate.segmentTimeline;
      break label191;
    }
  }

  protected List<SegmentBase.SegmentTimelineElement> parseSegmentTimeline(XmlPullParser paramXmlPullParser)
  {
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    do
    {
      paramXmlPullParser.next();
      long l2 = l1;
      if (XmlPullParserUtil.isStartTag(paramXmlPullParser, "S"))
      {
        l1 = parseLong(paramXmlPullParser, "t", l1);
        long l3 = parseLong(paramXmlPullParser, "d", -9223372036854775807L);
        int j = parseInt(paramXmlPullParser, "r", 0);
        int i = 0;
        while (true)
        {
          l2 = l1;
          if (i >= j + 1)
            break;
          localArrayList.add(buildSegmentTimelineElement(l1, l3));
          i += 1;
          l1 += l3;
        }
      }
      l1 = l2;
    }
    while (!XmlPullParserUtil.isEndTag(paramXmlPullParser, "SegmentTimeline"));
    return localArrayList;
  }

  protected RangedUri parseSegmentUrl(XmlPullParser paramXmlPullParser)
  {
    return parseRangedUrl(paramXmlPullParser, "media", "mediaRange");
  }

  protected UrlTemplate parseUrlTemplate(XmlPullParser paramXmlPullParser, String paramString, UrlTemplate paramUrlTemplate)
  {
    paramXmlPullParser = paramXmlPullParser.getAttributeValue(null, paramString);
    if (paramXmlPullParser != null)
      paramUrlTemplate = UrlTemplate.compile(paramXmlPullParser);
    return paramUrlTemplate;
  }

  protected UtcTimingElement parseUtcTiming(XmlPullParser paramXmlPullParser)
  {
    return buildUtcTimingElement(paramXmlPullParser.getAttributeValue(null, "schemeIdUri"), paramXmlPullParser.getAttributeValue(null, "value"));
  }

  private static final class RepresentationInfo
  {
    public final String baseUrl;
    public final ArrayList<DrmInitData.SchemeData> drmSchemeDatas;
    public final Format format;
    public final SegmentBase segmentBase;

    public RepresentationInfo(Format paramFormat, String paramString, SegmentBase paramSegmentBase, ArrayList<DrmInitData.SchemeData> paramArrayList)
    {
      this.format = paramFormat;
      this.baseUrl = paramString;
      this.segmentBase = paramSegmentBase;
      this.drmSchemeDatas = paramArrayList;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.manifest.DashManifestParser
 * JD-Core Version:    0.6.0
 */