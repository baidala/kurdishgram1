package org.vidogram.messenger.exoplayer2.util;

import org.xmlpull.v1.XmlPullParser;

public final class XmlPullParserUtil
{
  public static String getAttributeValue(XmlPullParser paramXmlPullParser, String paramString)
  {
    int j = paramXmlPullParser.getAttributeCount();
    int i = 0;
    while (i < j)
    {
      if (paramString.equals(paramXmlPullParser.getAttributeName(i)))
        return paramXmlPullParser.getAttributeValue(i);
      i += 1;
    }
    return null;
  }

  public static boolean isEndTag(XmlPullParser paramXmlPullParser)
  {
    return paramXmlPullParser.getEventType() == 3;
  }

  public static boolean isEndTag(XmlPullParser paramXmlPullParser, String paramString)
  {
    return (isEndTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString));
  }

  public static boolean isStartTag(XmlPullParser paramXmlPullParser)
  {
    return paramXmlPullParser.getEventType() == 2;
  }

  public static boolean isStartTag(XmlPullParser paramXmlPullParser, String paramString)
  {
    return (isStartTag(paramXmlPullParser)) && (paramXmlPullParser.getName().equals(paramString));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.util.XmlPullParserUtil
 * JD-Core Version:    0.6.0
 */