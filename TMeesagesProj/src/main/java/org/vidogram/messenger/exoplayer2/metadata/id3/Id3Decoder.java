package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class Id3Decoder
  implements MetadataDecoder
{
  public static final int ID3_HEADER_LENGTH = 10;
  public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
  private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
  private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
  private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
  private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
  private static final String TAG = "Id3Decoder";

  private static ApicFrame decodeApicFrame(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = 2;
    int j = paramParsableByteArray.readUnsignedByte();
    String str2 = getCharsetName(j);
    byte[] arrayOfByte = new byte[paramInt1 - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt1 - 1);
    String str1;
    if (paramInt2 == 2)
    {
      str1 = "image/" + new String(arrayOfByte, 0, 3, "ISO-8859-1").toLowerCase();
      paramInt1 = i;
      paramParsableByteArray = str1;
      if (str1.equals("image/jpg"))
      {
        paramParsableByteArray = "image/jpeg";
        paramInt1 = i;
      }
    }
    while (true)
    {
      paramInt2 = arrayOfByte[(paramInt1 + 1)];
      paramInt1 += 2;
      i = indexOfEos(arrayOfByte, paramInt1, j);
      return new ApicFrame(paramParsableByteArray, new String(arrayOfByte, paramInt1, i - paramInt1, str2), paramInt2 & 0xFF, Arrays.copyOfRange(arrayOfByte, delimiterLength(j) + i, arrayOfByte.length));
      paramInt2 = indexOfZeroByte(arrayOfByte, 0);
      str1 = new String(arrayOfByte, 0, paramInt2, "ISO-8859-1").toLowerCase();
      paramInt1 = paramInt2;
      paramParsableByteArray = str1;
      if (str1.indexOf('/') != -1)
        continue;
      paramParsableByteArray = "image/" + str1;
      paramInt1 = paramInt2;
    }
  }

  private static BinaryFrame decodeBinaryFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    return new BinaryFrame(paramString, arrayOfByte);
  }

  private static CommentFrame decodeCommentFrame(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    Object localObject = new byte[3];
    paramParsableByteArray.readBytes(localObject, 0, 3);
    localObject = new String(localObject, 0, 3);
    byte[] arrayOfByte = new byte[paramInt - 4];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 4);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return (CommentFrame)new CommentFrame((String)localObject, paramParsableByteArray, new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str));
  }

  private static Id3Frame decodeFrame(int paramInt, ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    int i5 = paramParsableByteArray.readUnsignedByte();
    int i6 = paramParsableByteArray.readUnsignedByte();
    int i7 = paramParsableByteArray.readUnsignedByte();
    int i2;
    int j;
    int i;
    if (paramInt >= 3)
    {
      i2 = paramParsableByteArray.readUnsignedByte();
      if (paramInt != 4)
        break label149;
      j = paramParsableByteArray.readUnsignedIntToInt();
      i = j;
      if (!paramBoolean)
        i = (j >> 24 & 0xFF) << 21 | (j & 0xFF | (j >> 8 & 0xFF) << 7 | (j >> 16 & 0xFF) << 14);
      label93: if (paramInt < 3)
        break label170;
    }
    label149: label170: for (int i3 = paramParsableByteArray.readUnsignedShort(); ; i3 = 0)
    {
      if ((i5 != 0) || (i6 != 0) || (i7 != 0) || (i2 != 0) || (i != 0) || (i3 != 0))
        break label176;
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
      i2 = 0;
      break;
      if (paramInt == 3)
      {
        i = paramParsableByteArray.readUnsignedIntToInt();
        break label93;
      }
      i = paramParsableByteArray.readUnsignedInt24();
      break label93;
    }
    label176: int i8 = paramParsableByteArray.getPosition() + i;
    if (i8 > paramParsableByteArray.limit())
    {
      Log.w("Id3Decoder", "Frame size exceeds remaining tag data");
      paramParsableByteArray.setPosition(paramParsableByteArray.limit());
      return null;
    }
    int i1 = 0;
    int m = 0;
    int i4 = 0;
    int k = 0;
    int n = 0;
    if (paramInt == 3)
      if ((i3 & 0x80) != 0)
      {
        j = 1;
        if ((i3 & 0x40) == 0)
          break label305;
        m = 1;
        label254: if ((i3 & 0x20) == 0)
          break label311;
        n = 1;
        label265: i1 = j;
        k = j;
      }
    label305: label311: 
    do
    {
      if ((i1 == 0) && (m == 0))
        break label427;
      Log.w("Id3Decoder", "Skipping unsupported compressed or encrypted frame");
      paramParsableByteArray.setPosition(i8);
      return null;
      j = 0;
      break;
      m = 0;
      break label254;
      n = 0;
      break label265;
    }
    while (paramInt != 4);
    if ((i3 & 0x40) != 0)
    {
      k = 1;
      label333: if ((i3 & 0x8) == 0)
        break label403;
      j = 1;
      label344: if ((i3 & 0x4) == 0)
        break label409;
      m = 1;
      label354: if ((i3 & 0x2) == 0)
        break label415;
      i1 = 1;
      label364: if ((i3 & 0x1) == 0)
        break label421;
    }
    label403: label409: label415: label421: for (n = 1; ; n = 0)
    {
      i3 = n;
      n = k;
      k = i3;
      i4 = i1;
      i1 = j;
      break;
      k = 0;
      break label333;
      j = 0;
      break label344;
      m = 0;
      break label354;
      i1 = 0;
      break label364;
    }
    label427: if (n != 0)
    {
      i -= 1;
      paramParsableByteArray.skipBytes(1);
      j = i;
      if (k != 0)
      {
        j = i - 4;
        paramParsableByteArray.skipBytes(4);
      }
      if (i4 != 0)
      {
        i = removeUnsynchronization(paramParsableByteArray, j);
        label471: if ((i5 != 84) || (i6 != 88) || (i7 != 88) || ((paramInt != 2) && (i2 != 88)));
      }
    }
    while (true)
    {
      try
      {
        Object localObject1 = decodeTxxxFrame(paramParsableByteArray, i);
        return localObject1;
        if ((i5 != 80) || (i6 != 82) || (i7 != 73) || (i2 != 86))
          break label842;
        localObject1 = decodePrivFrame(paramParsableByteArray, i);
        continue;
        localObject1 = decodeGeobFrame(paramParsableByteArray, i);
        continue;
        localObject1 = decodeApicFrame(paramParsableByteArray, i, paramInt);
        continue;
        if (i5 != 84)
          break label938;
        if (paramInt != 2)
          continue;
        localObject1 = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7) });
        localObject1 = decodeTextInformationFrame(paramParsableByteArray, i, (String)localObject1);
        continue;
        localObject1 = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i2) });
        continue;
        localObject1 = decodeCommentFrame(paramParsableByteArray, i);
        continue;
        if (paramInt != 2)
          continue;
        localObject1 = String.format(Locale.US, "%c%c%c", new Object[] { Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7) });
        localObject1 = decodeBinaryFrame(paramParsableByteArray, i, (String)localObject1);
        continue;
        localObject1 = String.format(Locale.US, "%c%c%c%c", new Object[] { Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i2) });
        continue;
      }
      catch (UnsupportedEncodingException localUnsupportedEncodingException)
      {
        Log.w("Id3Decoder", "Unsupported character encoding");
        return null;
      }
      finally
      {
        paramParsableByteArray.setPosition(i8);
      }
      i = j;
      break label471;
      break;
      label842: if ((i5 == 71) && (i6 == 69) && (i7 == 79) && ((i2 == 66) || (paramInt == 2)))
        continue;
      if (paramInt == 2)
      {
        if ((i5 != 80) || (i6 != 73) || (i7 != 67))
          continue;
        continue;
      }
      if ((i5 != 65) || (i6 != 80) || (i7 != 73))
        continue;
      if (i2 == 67)
        continue;
      continue;
      label938: if ((i5 != 67) || (i6 != 79) || (i7 != 77))
        continue;
      if (i2 == 77)
        continue;
      if (paramInt != 2)
        continue;
    }
  }

  private static GeobFrame decodeGeobFrame(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str1 = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, "ISO-8859-1");
    paramInt += 1;
    int j = indexOfEos(arrayOfByte, paramInt, i);
    String str2 = new String(arrayOfByte, paramInt, j - paramInt, str1);
    paramInt = delimiterLength(i) + j;
    j = indexOfEos(arrayOfByte, paramInt, i);
    return new GeobFrame(paramParsableByteArray, str2, new String(arrayOfByte, paramInt, j - paramInt, str1), Arrays.copyOfRange(arrayOfByte, delimiterLength(i) + j, arrayOfByte.length));
  }

  private static Id3Header decodeHeader(ParsableByteArray paramParsableByteArray)
  {
    if (paramParsableByteArray.bytesLeft() < 10)
    {
      Log.w("Id3Decoder", "Data too short to be an ID3 tag");
      return null;
    }
    int i = paramParsableByteArray.readUnsignedInt24();
    if (i != ID3_TAG)
    {
      Log.w("Id3Decoder", "Unexpected first three bytes of ID3 tag header: " + i);
      return null;
    }
    int m = paramParsableByteArray.readUnsignedByte();
    paramParsableByteArray.skipBytes(1);
    int n = paramParsableByteArray.readUnsignedByte();
    i = paramParsableByteArray.readSynchSafeInt();
    int j;
    if (m == 2)
    {
      if ((n & 0x40) != 0);
      for (j = 1; j != 0; j = 0)
      {
        Log.w("Id3Decoder", "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
        return null;
      }
      if ((m >= 4) || ((n & 0x80) == 0))
        break label294;
    }
    label261: label294: for (boolean bool = true; ; bool = false)
    {
      return new Id3Header(m, bool, i);
      int k;
      if (m == 3)
      {
        if ((n & 0x40) != 0);
        for (k = 1; ; k = 0)
        {
          j = i;
          if (k != 0)
          {
            j = paramParsableByteArray.readInt();
            paramParsableByteArray.skipBytes(j);
            j = i - (j + 4);
          }
          i = j;
          break;
        }
      }
      if (m == 4)
      {
        if ((n & 0x40) != 0)
        {
          k = 1;
          label210: j = i;
          if (k != 0)
          {
            j = paramParsableByteArray.readSynchSafeInt();
            paramParsableByteArray.skipBytes(j - 4);
            j = i - j;
          }
          if ((n & 0x10) == 0)
            break label261;
        }
        for (k = 1; ; k = 0)
        {
          i = j;
          if (k != 0)
            i = j - 10;
          break;
          k = 0;
          break label210;
        }
      }
      Log.w("Id3Decoder", "Skipped ID3 tag with unsupported majorVersion=" + m);
      return null;
    }
  }

  private static PrivFrame decodePrivFrame(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    byte[] arrayOfByte = new byte[paramInt];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
    paramInt = indexOfZeroByte(arrayOfByte, 0);
    return new PrivFrame(new String(arrayOfByte, 0, paramInt, "ISO-8859-1"), Arrays.copyOfRange(arrayOfByte, paramInt + 1, arrayOfByte.length));
  }

  private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray paramParsableByteArray, int paramInt, String paramString)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    return new TextInformationFrame(paramString, new String(arrayOfByte, 0, indexOfEos(arrayOfByte, 0, i), str));
  }

  private static TxxxFrame decodeTxxxFrame(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramParsableByteArray.readUnsignedByte();
    String str = getCharsetName(i);
    byte[] arrayOfByte = new byte[paramInt - 1];
    paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt - 1);
    paramInt = indexOfEos(arrayOfByte, 0, i);
    paramParsableByteArray = new String(arrayOfByte, 0, paramInt, str);
    paramInt += delimiterLength(i);
    return new TxxxFrame(paramParsableByteArray, new String(arrayOfByte, paramInt, indexOfEos(arrayOfByte, paramInt, i) - paramInt, str));
  }

  private static int delimiterLength(int paramInt)
  {
    if ((paramInt == 0) || (paramInt == 3))
      return 1;
    return 2;
  }

  private static String getCharsetName(int paramInt)
  {
    switch (paramInt)
    {
    default:
      return "ISO-8859-1";
    case 0:
      return "ISO-8859-1";
    case 1:
      return "UTF-16";
    case 2:
      return "UTF-16BE";
    case 3:
    }
    return "UTF-8";
  }

  private static int indexOfEos(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = indexOfZeroByte(paramArrayOfByte, paramInt1);
    if (paramInt2 != 0)
    {
      paramInt1 = i;
      if (paramInt2 != 3);
    }
    else
    {
      return i;
    }
    while (true)
    {
      paramInt1 = indexOfZeroByte(paramArrayOfByte, paramInt1 + 1);
      if (paramInt1 >= paramArrayOfByte.length - 1)
        break;
      if ((paramInt1 % 2 == 0) && (paramArrayOfByte[(paramInt1 + 1)] == 0))
        return paramInt1;
    }
    return paramArrayOfByte.length;
  }

  private static int indexOfZeroByte(byte[] paramArrayOfByte, int paramInt)
  {
    while (paramInt < paramArrayOfByte.length)
    {
      if (paramArrayOfByte[paramInt] == 0)
        return paramInt;
      paramInt += 1;
    }
    return paramArrayOfByte.length;
  }

  private static int removeUnsynchronization(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    byte[] arrayOfByte = paramParsableByteArray.data;
    int j = paramParsableByteArray.getPosition();
    int i = paramInt;
    paramInt = j;
    while (paramInt + 1 < i)
    {
      j = i;
      if ((arrayOfByte[paramInt] & 0xFF) == 255)
      {
        j = i;
        if (arrayOfByte[(paramInt + 1)] == 0)
        {
          System.arraycopy(arrayOfByte, paramInt + 2, arrayOfByte, paramInt + 1, i - paramInt - 2);
          j = i - 1;
        }
      }
      paramInt += 1;
      i = j;
    }
    return i;
  }

  private static boolean validateV4Frames(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
  {
    int m = paramParsableByteArray.getPosition();
    label201: label206: 
    while (true)
    {
      try
      {
        if (paramParsableByteArray.bytesLeft() >= 10)
        {
          i = paramParsableByteArray.readInt();
          int j = paramParsableByteArray.readUnsignedIntToInt();
          int n = paramParsableByteArray.readUnsignedShort();
          if ((i == 0) && (j == 0) && (n == 0))
            return true;
          if (paramBoolean)
            break label206;
          if ((j & 0x808080) != 0L)
            return false;
          j = (j >> 24 & 0xFF) << 21 | (j & 0xFF | (j >> 8 & 0xFF) << 7 | (j >> 16 & 0xFF) << 14);
          if ((n & 0x40) == 0)
            break label201;
          i = 1;
          int k = i;
          if ((n & 0x1) == 0)
            continue;
          k = i + 4;
          if (j < k)
            return false;
          i = paramParsableByteArray.bytesLeft();
          if (i < j)
            return false;
          paramParsableByteArray.skipBytes(j);
          continue;
        }
      }
      finally
      {
        paramParsableByteArray.setPosition(m);
      }
      paramParsableByteArray.setPosition(m);
      return true;
      int i = 0;
      continue;
    }
  }

  public boolean canDecode(String paramString)
  {
    return paramString.equals("application/id3");
  }

  public Metadata decode(byte[] paramArrayOfByte, int paramInt)
  {
    ArrayList localArrayList = new ArrayList();
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte, paramInt);
    Id3Header localId3Header = decodeHeader(paramArrayOfByte);
    if (localId3Header == null)
      return null;
    int i = paramArrayOfByte.getPosition();
    paramInt = localId3Header.framesSize;
    if (localId3Header.isUnsynchronized)
      paramInt = removeUnsynchronization(paramArrayOfByte, localId3Header.framesSize);
    paramArrayOfByte.setLimit(paramInt + i);
    if ((localId3Header.majorVersion == 4) && (!validateV4Frames(paramArrayOfByte, false)))
      if (!validateV4Frames(paramArrayOfByte, true));
    for (boolean bool = true; ; bool = false)
    {
      if (localId3Header.majorVersion == 2)
        paramInt = 6;
      while (paramArrayOfByte.bytesLeft() >= paramInt)
      {
        Id3Frame localId3Frame = decodeFrame(localId3Header.majorVersion, paramArrayOfByte, bool);
        if (localId3Frame == null)
          continue;
        localArrayList.add(localId3Frame);
        continue;
        Log.w("Id3Decoder", "Failed to validate V4 ID3 tag");
        return null;
        paramInt = 10;
      }
      return new Metadata(localArrayList);
    }
  }

  private static final class Id3Header
  {
    private final int framesSize;
    private final boolean isUnsynchronized;
    private final int majorVersion;

    public Id3Header(int paramInt1, boolean paramBoolean, int paramInt2)
    {
      this.majorVersion = paramInt1;
      this.isUnsynchronized = paramBoolean;
      this.framesSize = paramInt2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.Id3Decoder
 * JD-Core Version:    0.6.0
 */