package org.vidogram.messenger.exoplayer2.text.webvtt;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import org.vidogram.messenger.exoplayer2.text.SimpleSubtitleDecoder;
import org.vidogram.messenger.exoplayer2.text.SubtitleDecoderException;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class WebvttDecoder extends SimpleSubtitleDecoder
{
  private static final String COMMENT_START = "NOTE";
  private static final int EVENT_COMMENT = 1;
  private static final int EVENT_CUE = 3;
  private static final int EVENT_END_OF_FILE = 0;
  private static final int EVENT_NONE = -1;
  private static final int EVENT_STYLE_BLOCK = 2;
  private static final String STYLE_START = "STYLE";
  private final CssParser cssParser = new CssParser();
  private final WebvttCueParser cueParser = new WebvttCueParser();
  private final List<WebvttCssStyle> definedStyles = new ArrayList();
  private final ParsableByteArray parsableWebvttData = new ParsableByteArray();
  private final WebvttCue.Builder webvttCueBuilder = new WebvttCue.Builder();

  public WebvttDecoder()
  {
    super("WebvttDecoder");
  }

  private static int getNextEvent(ParsableByteArray paramParsableByteArray)
  {
    int j = 0;
    int i = -1;
    if (i == -1)
    {
      j = paramParsableByteArray.getPosition();
      String str = paramParsableByteArray.readLine();
      if (str == null)
        i = 0;
      while (true)
      {
        break;
        if ("STYLE".equals(str))
        {
          i = 2;
          continue;
        }
        if ("NOTE".startsWith(str))
        {
          i = 1;
          continue;
        }
        i = 3;
      }
    }
    paramParsableByteArray.setPosition(j);
    return i;
  }

  private static void skipComment(ParsableByteArray paramParsableByteArray)
  {
    while (!TextUtils.isEmpty(paramParsableByteArray.readLine()));
  }

  protected WebvttSubtitle decode(byte[] paramArrayOfByte, int paramInt)
  {
    this.parsableWebvttData.reset(paramArrayOfByte, paramInt);
    this.webvttCueBuilder.reset();
    this.definedStyles.clear();
    WebvttParserUtil.validateWebvttHeaderLine(this.parsableWebvttData);
    while (!TextUtils.isEmpty(this.parsableWebvttData.readLine()));
    paramArrayOfByte = new ArrayList();
    while (true)
    {
      paramInt = getNextEvent(this.parsableWebvttData);
      if (paramInt == 0)
        break;
      if (paramInt == 1)
      {
        skipComment(this.parsableWebvttData);
        continue;
      }
      if (paramInt == 2)
      {
        if (!paramArrayOfByte.isEmpty())
          throw new SubtitleDecoderException("A style block was found after the first cue.");
        this.parsableWebvttData.readLine();
        WebvttCssStyle localWebvttCssStyle = this.cssParser.parseBlock(this.parsableWebvttData);
        if (localWebvttCssStyle == null)
          continue;
        this.definedStyles.add(localWebvttCssStyle);
        continue;
      }
      if ((paramInt != 3) || (!this.cueParser.parseCue(this.parsableWebvttData, this.webvttCueBuilder, this.definedStyles)))
        continue;
      paramArrayOfByte.add(this.webvttCueBuilder.build());
      this.webvttCueBuilder.reset();
    }
    return new WebvttSubtitle(paramArrayOfByte);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.webvtt.WebvttDecoder
 * JD-Core Version:    0.6.0
 */