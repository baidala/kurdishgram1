package org.vidogram.messenger.exoplayer2.text.cea;

import android.text.TextUtils;
import java.nio.ByteBuffer;
import org.vidogram.messenger.exoplayer2.text.Cue;
import org.vidogram.messenger.exoplayer2.text.Subtitle;
import org.vidogram.messenger.exoplayer2.text.SubtitleInputBuffer;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class Cea608Decoder extends CeaDecoder
{
  private static final int[] BASIC_CHARACTER_SET = { 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 225, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 233, 93, 237, 243, 250, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 231, 247, 209, 241, 9632 };
  private static final int CC_FIELD_FLAG = 1;
  private static final int CC_MODE_PAINT_ON = 3;
  private static final int CC_MODE_POP_ON = 2;
  private static final int CC_MODE_ROLL_UP = 1;
  private static final int CC_MODE_UNKNOWN = 0;
  private static final int CC_TYPE_FLAG = 2;
  private static final int CC_VALID_608_ID = 4;
  private static final int CC_VALID_FLAG = 4;
  private static final int COUNTRY_CODE = 181;
  private static final byte CTRL_BACKSPACE = 33;
  private static final byte CTRL_CARRIAGE_RETURN = 45;
  private static final byte CTRL_END_OF_CAPTION = 47;
  private static final byte CTRL_ERASE_DISPLAYED_MEMORY = 44;
  private static final byte CTRL_ERASE_NON_DISPLAYED_MEMORY = 46;
  private static final byte CTRL_MISC_CHAN_1 = 20;
  private static final byte CTRL_MISC_CHAN_2 = 28;
  private static final byte CTRL_RESUME_CAPTION_LOADING = 32;
  private static final byte CTRL_RESUME_DIRECT_CAPTIONING = 41;
  private static final byte CTRL_ROLL_UP_CAPTIONS_2_ROWS = 37;
  private static final byte CTRL_ROLL_UP_CAPTIONS_3_ROWS = 38;
  private static final byte CTRL_ROLL_UP_CAPTIONS_4_ROWS = 39;
  private static final int DEFAULT_CAPTIONS_ROW_COUNT = 4;
  private static final int NTSC_CC_FIELD_1 = 0;
  private static final int NTSC_CC_FIELD_2 = 1;
  private static final int PAYLOAD_TYPE_CC = 4;
  private static final int PROVIDER_CODE = 49;
  private static final int[] SPECIAL_CHARACTER_SET = { 174, 176, 189, 191, 8482, 162, 163, 9834, 224, 32, 232, 226, 234, 238, 244, 251 };
  private static final int[] SPECIAL_ES_FR_CHARACTER_SET = { 193, 201, 211, 218, 220, 252, 8216, 161, 42, 39, 8212, 169, 8480, 8226, 8220, 8221, 192, 194, 199, 200, 202, 203, 235, 206, 207, 239, 212, 217, 249, 219, 171, 187 };
  private static final int[] SPECIAL_PT_DE_CHARACTER_SET = { 195, 227, 205, 204, 236, 210, 242, 213, 245, 123, 125, 92, 94, 95, 124, 126, 196, 228, 214, 246, 223, 165, 164, 9474, 197, 229, 216, 248, 9484, 9488, 9492, 9496 };
  private static final String TAG = "Cea608Decoder";
  private static final int USER_DATA_TYPE_CODE = 3;
  private static final int USER_ID = 1195456820;
  private int captionMode;
  private int captionRowCount;
  private String captionString;
  private final StringBuilder captionStringBuilder = new StringBuilder();
  private final ParsableByteArray ccData = new ParsableByteArray();
  private String lastCaptionString;
  private byte repeatableControlCc1;
  private byte repeatableControlCc2;
  private boolean repeatableControlSet;
  private final int selectedField;

  public Cea608Decoder(int paramInt)
  {
    switch (paramInt)
    {
    default:
    case 3:
    case 4:
    }
    for (this.selectedField = 1; ; this.selectedField = 2)
    {
      setCaptionMode(0);
      this.captionRowCount = 4;
      return;
    }
  }

  private void backspace()
  {
    if (this.captionStringBuilder.length() > 0)
      this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
  }

  private static char getChar(byte paramByte)
  {
    return (char)BASIC_CHARACTER_SET[((paramByte & 0x7F) - 32)];
  }

  private String getDisplayCaption()
  {
    int k = this.captionStringBuilder.length();
    if (k == 0)
      return null;
    if (this.captionStringBuilder.charAt(k - 1) == '\n');
    for (int j = 1; (k == 1) && (j != 0); j = 0)
      return null;
    int i = k;
    if (j != 0)
      i = k - 1;
    if (this.captionMode != 1)
      return this.captionStringBuilder.substring(0, i);
    j = 0;
    k = i;
    while ((j < this.captionRowCount) && (k != -1))
    {
      k = this.captionStringBuilder.lastIndexOf("\n", k - 1);
      j += 1;
    }
    if (k != -1);
    for (j = k + 1; ; j = 0)
    {
      this.captionStringBuilder.delete(0, j);
      return this.captionStringBuilder.substring(0, i - j);
    }
  }

  private static char getExtendedEsFrChar(byte paramByte)
  {
    return (char)SPECIAL_ES_FR_CHARACTER_SET[(paramByte & 0x1F)];
  }

  private static char getExtendedPtDeChar(byte paramByte)
  {
    return (char)SPECIAL_PT_DE_CHARACTER_SET[(paramByte & 0x1F)];
  }

  private static char getSpecialChar(byte paramByte)
  {
    return (char)SPECIAL_CHARACTER_SET[(paramByte & 0xF)];
  }

  private boolean handleCtrl(byte paramByte1, byte paramByte2)
  {
    boolean bool = isRepeatable(paramByte1);
    if (bool)
    {
      if ((this.repeatableControlSet) && (this.repeatableControlCc1 == paramByte1) && (this.repeatableControlCc2 == paramByte2))
      {
        this.repeatableControlSet = false;
        return true;
      }
      this.repeatableControlSet = true;
      this.repeatableControlCc1 = paramByte1;
      this.repeatableControlCc2 = paramByte2;
    }
    if (isMiscCode(paramByte1, paramByte2))
      handleMiscCode(paramByte2);
    while (true)
    {
      return bool;
      if (!isPreambleAddressCode(paramByte1, paramByte2))
        continue;
      maybeAppendNewline();
    }
  }

  private void handleMiscCode(byte paramByte)
  {
    switch (paramByte)
    {
    case 33:
    case 34:
    case 35:
    case 36:
    case 40:
    default:
      if (this.captionMode != 0)
        break;
    case 37:
    case 38:
    case 39:
    case 32:
    case 41:
    }
    do
    {
      do
      {
        return;
        this.captionRowCount = 2;
        setCaptionMode(1);
        return;
        this.captionRowCount = 3;
        setCaptionMode(1);
        return;
        this.captionRowCount = 4;
        setCaptionMode(1);
        return;
        setCaptionMode(2);
        return;
        setCaptionMode(3);
        return;
        switch (paramByte)
        {
        default:
          return;
        case 33:
        case 44:
        case 46:
        case 47:
        case 45:
        }
      }
      while (this.captionStringBuilder.length() <= 0);
      this.captionStringBuilder.setLength(this.captionStringBuilder.length() - 1);
      return;
      this.captionString = null;
    }
    while ((this.captionMode != 1) && (this.captionMode != 3));
    this.captionStringBuilder.setLength(0);
    return;
    this.captionStringBuilder.setLength(0);
    return;
    this.captionString = getDisplayCaption();
    this.captionStringBuilder.setLength(0);
    return;
    maybeAppendNewline();
  }

  private static boolean isMiscCode(byte paramByte1, byte paramByte2)
  {
    return ((paramByte1 == 20) || (paramByte1 == 28)) && (paramByte2 >= 32) && (paramByte2 <= 47);
  }

  private static boolean isPreambleAddressCode(byte paramByte1, byte paramByte2)
  {
    return (paramByte1 >= 16) && (paramByte1 <= 31) && (paramByte2 >= 64) && (paramByte2 <= 127);
  }

  private static boolean isRepeatable(byte paramByte)
  {
    return (paramByte >= 16) && (paramByte <= 31);
  }

  public static boolean isSeiMessageCea608(int paramInt1, int paramInt2, ParsableByteArray paramParsableByteArray)
  {
    if ((paramInt1 != 4) || (paramInt2 < 8));
    int i;
    int j;
    int k;
    do
    {
      return false;
      paramInt1 = paramParsableByteArray.getPosition();
      paramInt2 = paramParsableByteArray.readUnsignedByte();
      i = paramParsableByteArray.readUnsignedShort();
      j = paramParsableByteArray.readInt();
      k = paramParsableByteArray.readUnsignedByte();
      paramParsableByteArray.setPosition(paramInt1);
    }
    while ((paramInt2 != 181) || (i != 49) || (j != 1195456820) || (k != 3));
    return true;
  }

  private void maybeAppendNewline()
  {
    int i = this.captionStringBuilder.length();
    if ((i > 0) && (this.captionStringBuilder.charAt(i - 1) != '\n'))
      this.captionStringBuilder.append('\n');
  }

  private void setCaptionMode(int paramInt)
  {
    if (this.captionMode == paramInt);
    do
    {
      return;
      this.captionMode = paramInt;
      this.captionStringBuilder.setLength(0);
    }
    while ((paramInt != 1) && (paramInt != 0));
    this.captionString = null;
  }

  protected Subtitle createSubtitle()
  {
    this.lastCaptionString = this.captionString;
    return new CeaSubtitle(new Cue(this.captionString));
  }

  protected void decode(SubtitleInputBuffer paramSubtitleInputBuffer)
  {
    this.ccData.reset(paramSubtitleInputBuffer.data.array(), paramSubtitleInputBuffer.data.limit());
    boolean bool = false;
    int i = 0;
    while (this.ccData.bytesLeft() > 0)
    {
      int j = (byte)this.ccData.readUnsignedByte();
      byte b1 = (byte)(this.ccData.readUnsignedByte() & 0x7F);
      byte b2 = (byte)(this.ccData.readUnsignedByte() & 0x7F);
      if (((j & 0x6) != 4) || ((this.selectedField == 1) && ((j & 0x1) != 0)) || ((this.selectedField == 2) && ((j & 0x1) != 1)) || ((b1 == 0) && (b2 == 0)))
        continue;
      if (((b1 == 17) || (b1 == 25)) && ((b2 & 0x70) == 48))
      {
        this.captionStringBuilder.append(getSpecialChar(b2));
        i = 1;
        continue;
      }
      if ((b2 & 0x60) == 32)
      {
        if ((b1 == 18) || (b1 == 26))
        {
          backspace();
          this.captionStringBuilder.append(getExtendedEsFrChar(b2));
          i = 1;
          continue;
        }
        if ((b1 == 19) || (b1 == 27))
        {
          backspace();
          this.captionStringBuilder.append(getExtendedPtDeChar(b2));
          i = 1;
          continue;
        }
      }
      if (b1 < 32)
      {
        bool = handleCtrl(b1, b2);
        i = 1;
        continue;
      }
      this.captionStringBuilder.append(getChar(b1));
      if (b2 >= 32)
        this.captionStringBuilder.append(getChar(b2));
      i = 1;
    }
    if (i != 0)
    {
      if (!bool)
        this.repeatableControlSet = false;
      if ((this.captionMode == 1) || (this.captionMode == 3))
        this.captionString = getDisplayCaption();
    }
  }

  public void flush()
  {
    super.flush();
    setCaptionMode(0);
    this.captionRowCount = 4;
    this.captionStringBuilder.setLength(0);
    this.captionString = null;
    this.lastCaptionString = null;
    this.repeatableControlSet = false;
    this.repeatableControlCc1 = 0;
    this.repeatableControlCc2 = 0;
  }

  public String getName()
  {
    return "Cea608Decoder";
  }

  protected boolean isNewSubtitleDataAvailable()
  {
    return !TextUtils.equals(this.captionString, this.lastCaptionString);
  }

  public void release()
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.cea.Cea608Decoder
 * JD-Core Version:    0.6.0
 */