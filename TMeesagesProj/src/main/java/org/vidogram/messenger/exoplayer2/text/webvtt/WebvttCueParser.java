package org.vidogram.messenger.exoplayer2.text.webvtt;

import android.text.Layout.Alignment;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan.Standard;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

final class WebvttCueParser
{
  private static final char CHAR_AMPERSAND = '&';
  private static final char CHAR_GREATER_THAN = '>';
  private static final char CHAR_LESS_THAN = '<';
  private static final char CHAR_SEMI_COLON = ';';
  private static final char CHAR_SLASH = '/';
  private static final char CHAR_SPACE = ' ';
  public static final Pattern CUE_HEADER_PATTERN = Pattern.compile("^(\\S+)\\s+-->\\s+(\\S+)(.*)?$");
  private static final Pattern CUE_SETTING_PATTERN = Pattern.compile("(\\S+?):(\\S+)");
  private static final String ENTITY_AMPERSAND = "amp";
  private static final String ENTITY_GREATER_THAN = "gt";
  private static final String ENTITY_LESS_THAN = "lt";
  private static final String ENTITY_NON_BREAK_SPACE = "nbsp";
  private static final int STYLE_BOLD = 1;
  private static final int STYLE_ITALIC = 2;
  private static final String TAG = "WebvttCueParser";
  private static final String TAG_BOLD = "b";
  private static final String TAG_CLASS = "c";
  private static final String TAG_ITALIC = "i";
  private static final String TAG_LANG = "lang";
  private static final String TAG_UNDERLINE = "u";
  private static final String TAG_VOICE = "v";
  private final StringBuilder textBuilder = new StringBuilder();

  private static void applyEntity(String paramString, SpannableStringBuilder paramSpannableStringBuilder)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 3464:
    case 3309:
    case 3374865:
    case 96708:
    }
    while (true)
      switch (i)
      {
      default:
        Log.w("WebvttCueParser", "ignoring unsupported entity: '&" + paramString + ";'");
        return;
        if (!paramString.equals("lt"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("gt"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("nbsp"))
          continue;
        i = 2;
        continue;
        if (!paramString.equals("amp"))
          continue;
        i = 3;
      case 0:
      case 1:
      case 2:
      case 3:
      }
    paramSpannableStringBuilder.append('<');
    return;
    paramSpannableStringBuilder.append('>');
    return;
    paramSpannableStringBuilder.append(' ');
    return;
    paramSpannableStringBuilder.append('&');
  }

  private static void applySpansForTag(String paramString, StartTag paramStartTag, SpannableStringBuilder paramSpannableStringBuilder, List<WebvttCssStyle> paramList, List<StyleMatch> paramList1)
  {
    int j = paramStartTag.position;
    int k = paramSpannableStringBuilder.length();
    String str = paramStartTag.name;
    int i = -1;
    switch (str.hashCode())
    {
    default:
    case 98:
    case 105:
    case 117:
    case 99:
    case 3314158:
    case 118:
    case 0:
    }
    while (true)
      switch (i)
      {
      default:
        return;
        if (!str.equals("b"))
          continue;
        i = 0;
        continue;
        if (!str.equals("i"))
          continue;
        i = 1;
        continue;
        if (!str.equals("u"))
          continue;
        i = 2;
        continue;
        if (!str.equals("c"))
          continue;
        i = 3;
        continue;
        if (!str.equals("lang"))
          continue;
        i = 4;
        continue;
        if (!str.equals("v"))
          continue;
        i = 5;
        continue;
        if (!str.equals(""))
          continue;
        i = 6;
      case 0:
      case 3:
      case 4:
      case 5:
      case 6:
      case 1:
      case 2:
      }
    paramSpannableStringBuilder.setSpan(new StyleSpan(1), j, k, 33);
    while (true)
    {
      paramList1.clear();
      getApplicableStyles(paramList, paramString, paramStartTag, paramList1);
      int m = paramList1.size();
      i = 0;
      while (i < m)
      {
        applyStyleToText(paramSpannableStringBuilder, ((StyleMatch)paramList1.get(i)).style, j, k);
        i += 1;
      }
      paramSpannableStringBuilder.setSpan(new StyleSpan(2), j, k, 33);
      continue;
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), j, k, 33);
    }
  }

  private static void applyStyleToText(SpannableStringBuilder paramSpannableStringBuilder, WebvttCssStyle paramWebvttCssStyle, int paramInt1, int paramInt2)
  {
    if (paramWebvttCssStyle == null)
      return;
    if (paramWebvttCssStyle.getStyle() != -1)
      paramSpannableStringBuilder.setSpan(new StyleSpan(paramWebvttCssStyle.getStyle()), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.isLinethrough())
      paramSpannableStringBuilder.setSpan(new StrikethroughSpan(), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.isUnderline())
      paramSpannableStringBuilder.setSpan(new UnderlineSpan(), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.hasFontColor())
      paramSpannableStringBuilder.setSpan(new ForegroundColorSpan(paramWebvttCssStyle.getFontColor()), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.hasBackgroundColor())
      paramSpannableStringBuilder.setSpan(new BackgroundColorSpan(paramWebvttCssStyle.getBackgroundColor()), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.getFontFamily() != null)
      paramSpannableStringBuilder.setSpan(new TypefaceSpan(paramWebvttCssStyle.getFontFamily()), paramInt1, paramInt2, 33);
    if (paramWebvttCssStyle.getTextAlign() != null)
      paramSpannableStringBuilder.setSpan(new AlignmentSpan.Standard(paramWebvttCssStyle.getTextAlign()), paramInt1, paramInt2, 33);
    switch (paramWebvttCssStyle.getFontSizeUnit())
    {
    default:
      return;
    case 1:
      paramSpannableStringBuilder.setSpan(new AbsoluteSizeSpan((int)paramWebvttCssStyle.getFontSize(), true), paramInt1, paramInt2, 33);
      return;
    case 2:
      paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize()), paramInt1, paramInt2, 33);
      return;
    case 3:
    }
    paramSpannableStringBuilder.setSpan(new RelativeSizeSpan(paramWebvttCssStyle.getFontSize() / 100.0F), paramInt1, paramInt2, 33);
  }

  private static int findEndOfTag(String paramString, int paramInt)
  {
    paramInt = paramString.indexOf('>', paramInt);
    if (paramInt == -1)
      return paramString.length();
    return paramInt + 1;
  }

  private static void getApplicableStyles(List<WebvttCssStyle> paramList, String paramString, StartTag paramStartTag, List<StyleMatch> paramList1)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      WebvttCssStyle localWebvttCssStyle = (WebvttCssStyle)paramList.get(i);
      int k = localWebvttCssStyle.getSpecificityScore(paramString, paramStartTag.name, paramStartTag.classes, paramStartTag.voice);
      if (k > 0)
        paramList1.add(new StyleMatch(k, localWebvttCssStyle));
      i += 1;
    }
    Collections.sort(paramList1);
  }

  private static String getTagName(String paramString)
  {
    paramString = paramString.trim();
    if (paramString.isEmpty())
      return null;
    return paramString.split("[ \\.]")[0];
  }

  private static boolean isSupportedTag(String paramString)
  {
    int j = 1;
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 98:
    case 99:
    case 105:
    case 3314158:
    case 117:
    case 118:
    }
    while (true)
    {
      switch (i)
      {
      default:
        j = 0;
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
      return j;
      if (!paramString.equals("b"))
        continue;
      i = 0;
      continue;
      if (!paramString.equals("c"))
        continue;
      i = 1;
      continue;
      if (!paramString.equals("i"))
        continue;
      i = 2;
      continue;
      if (!paramString.equals("lang"))
        continue;
      i = 3;
      continue;
      if (!paramString.equals("u"))
        continue;
      i = 4;
      continue;
      if (!paramString.equals("v"))
        continue;
      i = 5;
    }
  }

  private static boolean parseCue(String paramString, Matcher paramMatcher, ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, StringBuilder paramStringBuilder, List<WebvttCssStyle> paramList)
  {
    try
    {
      paramBuilder.setStartTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(1))).setEndTime(WebvttParserUtil.parseTimestampUs(paramMatcher.group(2)));
      parseCueSettingsList(paramMatcher.group(3), paramBuilder);
      paramStringBuilder.setLength(0);
      while (true)
      {
        paramMatcher = paramParsableByteArray.readLine();
        if ((paramMatcher == null) || (paramMatcher.isEmpty()))
          break;
        if (paramStringBuilder.length() > 0)
          paramStringBuilder.append("\n");
        paramStringBuilder.append(paramMatcher.trim());
      }
    }
    catch (NumberFormatException paramString)
    {
      Log.w("WebvttCueParser", "Skipping cue with bad header: " + paramMatcher.group());
      return false;
    }
    parseCueText(paramString, paramStringBuilder.toString(), paramBuilder, paramList);
    return true;
  }

  static void parseCueSettingsList(String paramString, WebvttCue.Builder paramBuilder)
  {
    paramString = CUE_SETTING_PATTERN.matcher(paramString);
    while (paramString.find())
    {
      String str1 = paramString.group(1);
      String str2 = paramString.group(2);
      try
      {
        if (!"line".equals(str1))
          break label78;
        parseLineAttribute(str2, paramBuilder);
      }
      catch (NumberFormatException localNumberFormatException)
      {
        Log.w("WebvttCueParser", "Skipping bad cue setting: " + paramString.group());
      }
      continue;
      label78: if ("align".equals(localNumberFormatException))
      {
        paramBuilder.setTextAlignment(parseTextAlignment(str2));
        continue;
      }
      if ("position".equals(localNumberFormatException))
      {
        parsePositionAttribute(str2, paramBuilder);
        continue;
      }
      if ("size".equals(localNumberFormatException))
      {
        paramBuilder.setWidth(WebvttParserUtil.parsePercentage(str2));
        continue;
      }
      Log.w("WebvttCueParser", "Unknown cue setting " + localNumberFormatException + ":" + str2);
    }
  }

  static void parseCueText(String paramString1, String paramString2, WebvttCue.Builder paramBuilder, List<WebvttCssStyle> paramList)
  {
    SpannableStringBuilder localSpannableStringBuilder = new SpannableStringBuilder();
    Stack localStack = new Stack();
    ArrayList localArrayList = new ArrayList();
    int i = 0;
    while (true)
    {
      label129: int j;
      if (i < paramString2.length())
      {
        char c = paramString2.charAt(i);
        int k;
        int m;
        switch (c)
        {
        default:
          localSpannableStringBuilder.append(c);
          i += 1;
          break;
        case '<':
          if (i + 1 >= paramString2.length())
          {
            i += 1;
            continue;
          }
          int n;
          if (paramString2.charAt(i + 1) == '/')
          {
            k = 1;
            j = findEndOfTag(paramString2, i + 1);
            if (paramString2.charAt(j - 2) != '/')
              break label220;
            m = 1;
            if (k == 0)
              break label226;
            n = 2;
            if (m == 0)
              break label232;
          }
          Object localObject;
          String str;
          for (int i1 = j - 2; ; i1 = j - 1)
          {
            localObject = paramString2.substring(n + i, i1);
            str = getTagName((String)localObject);
            if (str == null)
              break label493;
            if (isSupportedTag(str))
              break label241;
            i = j;
            break;
            k = 0;
            break label129;
            m = 0;
            break label155;
            n = 1;
            break label163;
          }
          if (k != 0)
          {
            if (localStack.isEmpty());
            while (true)
            {
              i = j;
              break;
              localObject = (StartTag)localStack.pop();
              applySpansForTag(paramString1, (StartTag)localObject, localSpannableStringBuilder, paramList, localArrayList);
              if (!((StartTag)localObject).name.equals(str))
                break label246;
            }
          }
          if (m != 0)
            break label493;
          localStack.push(StartTag.buildStartTag((String)localObject, localSpannableStringBuilder.length()));
          i = j;
          break;
        case '&':
          label155: label163: label220: label226: label232: label241: label246: m = paramString2.indexOf(';', i + 1);
          k = paramString2.indexOf(' ', i + 1);
          if (m == -1)
            j = k;
          while (true)
          {
            if (j == -1)
              break label428;
            applyEntity(paramString2.substring(i + 1, j), localSpannableStringBuilder);
            if (j == k)
              localSpannableStringBuilder.append(" ");
            i = j + 1;
            break;
            j = m;
            if (k == -1)
              continue;
            j = Math.min(m, k);
          }
          label428: localSpannableStringBuilder.append(c);
          i += 1;
          break;
        }
      }
      while (!localStack.isEmpty())
        applySpansForTag(paramString1, (StartTag)localStack.pop(), localSpannableStringBuilder, paramList, localArrayList);
      applySpansForTag(paramString1, StartTag.buildWholeCueVirtualTag(), localSpannableStringBuilder, paramList, localArrayList);
      paramBuilder.setText(localSpannableStringBuilder);
      return;
      label493: i = j;
    }
  }

  private static void parseLineAttribute(String paramString, WebvttCue.Builder paramBuilder)
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setLineAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    while (paramString.endsWith("%"))
    {
      paramBuilder.setLine(WebvttParserUtil.parsePercentage(paramString)).setLineType(0);
      return;
      paramBuilder.setLineAnchor(-2147483648);
    }
    paramBuilder.setLine(Integer.parseInt(paramString)).setLineType(1);
  }

  private static int parsePositionAnchor(String paramString)
  {
    int j = 0;
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 109757538:
    case -1364013995:
    case -1074341483:
    case 100571:
    }
    while (true)
      switch (i)
      {
      default:
        Log.w("WebvttCueParser", "Invalid anchor value: " + paramString);
        j = -2147483648;
      case 0:
        return j;
        if (!paramString.equals("start"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("center"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("middle"))
          continue;
        i = 2;
        continue;
        if (!paramString.equals("end"))
          continue;
        i = 3;
      case 1:
      case 2:
      case 3:
      }
    return 1;
    return 2;
  }

  private static void parsePositionAttribute(String paramString, WebvttCue.Builder paramBuilder)
  {
    int i = paramString.indexOf(',');
    if (i != -1)
    {
      paramBuilder.setPositionAnchor(parsePositionAnchor(paramString.substring(i + 1)));
      paramString = paramString.substring(0, i);
    }
    while (true)
    {
      paramBuilder.setPosition(WebvttParserUtil.parsePercentage(paramString));
      return;
      paramBuilder.setPositionAnchor(-2147483648);
    }
  }

  private static Layout.Alignment parseTextAlignment(String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default:
    case 109757538:
    case 3317767:
    case -1364013995:
    case -1074341483:
    case 100571:
    case 108511772:
    }
    while (true)
      switch (i)
      {
      default:
        Log.w("WebvttCueParser", "Invalid alignment value: " + paramString);
        return null;
        if (!paramString.equals("start"))
          continue;
        i = 0;
        continue;
        if (!paramString.equals("left"))
          continue;
        i = 1;
        continue;
        if (!paramString.equals("center"))
          continue;
        i = 2;
        continue;
        if (!paramString.equals("middle"))
          continue;
        i = 3;
        continue;
        if (!paramString.equals("end"))
          continue;
        i = 4;
        continue;
        if (!paramString.equals("right"))
          continue;
        i = 5;
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
    return Layout.Alignment.ALIGN_NORMAL;
    return Layout.Alignment.ALIGN_CENTER;
    return Layout.Alignment.ALIGN_OPPOSITE;
  }

  boolean parseCue(ParsableByteArray paramParsableByteArray, WebvttCue.Builder paramBuilder, List<WebvttCssStyle> paramList)
  {
    String str = paramParsableByteArray.readLine();
    Object localObject = CUE_HEADER_PATTERN.matcher(str);
    if (((Matcher)localObject).matches())
      return parseCue(null, (Matcher)localObject, paramParsableByteArray, paramBuilder, this.textBuilder, paramList);
    localObject = paramParsableByteArray.readLine();
    localObject = CUE_HEADER_PATTERN.matcher((CharSequence)localObject);
    if (((Matcher)localObject).matches())
      return parseCue(str.trim(), (Matcher)localObject, paramParsableByteArray, paramBuilder, this.textBuilder, paramList);
    return false;
  }

  private static final class StartTag
  {
    private static final String[] NO_CLASSES = new String[0];
    public final String[] classes;
    public final String name;
    public final int position;
    public final String voice;

    private StartTag(String paramString1, int paramInt, String paramString2, String[] paramArrayOfString)
    {
      this.position = paramInt;
      this.name = paramString1;
      this.voice = paramString2;
      this.classes = paramArrayOfString;
    }

    public static StartTag buildStartTag(String paramString, int paramInt)
    {
      Object localObject = paramString.trim();
      if (((String)localObject).isEmpty())
        return null;
      int i = ((String)localObject).indexOf(" ");
      String str;
      if (i == -1)
      {
        paramString = "";
        localObject = ((String)localObject).split("\\.");
        str = localObject[0];
        if (localObject.length <= 1)
          break label90;
      }
      label90: for (localObject = (String[])Arrays.copyOfRange(localObject, 1, localObject.length); ; localObject = NO_CLASSES)
      {
        return new StartTag(str, paramInt, paramString, localObject);
        paramString = ((String)localObject).substring(i).trim();
        localObject = ((String)localObject).substring(0, i);
        break;
      }
    }

    public static StartTag buildWholeCueVirtualTag()
    {
      return new StartTag("", 0, "", new String[0]);
    }
  }

  private static final class StyleMatch
    implements Comparable<StyleMatch>
  {
    public final int score;
    public final WebvttCssStyle style;

    public StyleMatch(int paramInt, WebvttCssStyle paramWebvttCssStyle)
    {
      this.score = paramInt;
      this.style = paramWebvttCssStyle;
    }

    public int compareTo(StyleMatch paramStyleMatch)
    {
      return this.score - paramStyleMatch.score;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.webvtt.WebvttCueParser
 * JD-Core Version:    0.6.0
 */