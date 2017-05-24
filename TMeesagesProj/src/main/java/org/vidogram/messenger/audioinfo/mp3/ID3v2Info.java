package org.vidogram.messenger.audioinfo.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vidogram.messenger.audioinfo.AudioInfo;

public class ID3v2Info extends AudioInfo
{
  static final Logger LOGGER = Logger.getLogger(ID3v2Info.class.getName());
  private byte coverPictureType;
  private final Level debugLevel;

  public ID3v2Info(InputStream paramInputStream)
  {
    this(paramInputStream, Level.FINEST);
  }

  public ID3v2Info(InputStream paramInputStream, Level paramLevel)
  {
    this.debugLevel = paramLevel;
    ID3v2TagHeader localID3v2TagHeader;
    ID3v2TagBody localID3v2TagBody;
    if (isID3v2StartPosition(paramInputStream))
    {
      localID3v2TagHeader = new ID3v2TagHeader(paramInputStream);
      this.brand = "ID3";
      this.version = String.format("2.%d.%d", new Object[] { Integer.valueOf(localID3v2TagHeader.getVersion()), Integer.valueOf(localID3v2TagHeader.getRevision()) });
      localID3v2TagBody = localID3v2TagHeader.tagBody(paramInputStream);
    }
    while (true)
      try
      {
        if (localID3v2TagBody.getRemainingLength() <= 10L)
          continue;
        localID3v2FrameHeader = new ID3v2FrameHeader(localID3v2TagBody);
        boolean bool = localID3v2FrameHeader.isPadding();
        if (!bool)
          continue;
        localID3v2TagBody.getData().skipFully(localID3v2TagBody.getRemainingLength());
        if (localID3v2TagHeader.getFooterSize() <= 0)
          continue;
        paramInputStream.skip(localID3v2TagHeader.getFooterSize());
        return;
        if (localID3v2FrameHeader.getBodySize() <= localID3v2TagBody.getRemainingLength())
          continue;
        if (!LOGGER.isLoggable(paramLevel))
          continue;
        LOGGER.log(paramLevel, "ID3 frame claims to extend frames area");
        continue;
      }
      catch (ID3v2Exception localID3v2FrameBody)
      {
        ID3v2FrameHeader localID3v2FrameHeader;
        if (!LOGGER.isLoggable(paramLevel))
          continue;
        LOGGER.log(paramLevel, "ID3 exception occured: " + localID3v2Exception1.getMessage());
        continue;
        if ((!localID3v2FrameHeader.isValid()) || (localID3v2FrameHeader.isEncryption()))
          continue;
        ID3v2FrameBody localID3v2FrameBody = localID3v2TagBody.frameBody(localID3v2FrameHeader);
        try
        {
          parseFrame(localID3v2FrameBody);
          localID3v2FrameBody.getData().skipFully(localID3v2FrameBody.getRemainingLength());
          continue;
        }
        catch (ID3v2Exception localID3v2Exception2)
        {
          if (!LOGGER.isLoggable(paramLevel))
            continue;
          LOGGER.log(paramLevel, String.format("ID3 exception occured in frame %s: %s", new Object[] { localID3v2FrameHeader.getFrameId(), localID3v2Exception2.getMessage() }));
          localID3v2FrameBody.getData().skipFully(localID3v2FrameBody.getRemainingLength());
          continue;
        }
        finally
        {
          localID3v2FrameBody.getData().skipFully(localID3v2FrameBody.getRemainingLength());
        }
        localID3v2TagBody.getData().skipFully(localObject.getBodySize());
      }
  }

  public static boolean isID3v2StartPosition(InputStream paramInputStream)
  {
    paramInputStream.mark(3);
    try
    {
      if ((paramInputStream.read() == 73) && (paramInputStream.read() == 68))
      {
        int i = paramInputStream.read();
        if (i == 51)
        {
          j = 1;
          return j;
        }
      }
      int j = 0;
    }
    finally
    {
      paramInputStream.reset();
    }
  }

  AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody paramID3v2FrameBody)
  {
    ID3v2Encoding localID3v2Encoding = paramID3v2FrameBody.readEncoding();
    String str;
    int i;
    if (paramID3v2FrameBody.getTagHeader().getVersion() == 2)
    {
      str = paramID3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
      i = -1;
      switch (str.hashCode())
      {
      default:
        switch (i)
        {
        default:
          str = "image/unknown";
        case 0:
        case 1:
        }
      case 79369:
      case 73665:
      }
    }
    while (true)
    {
      return new AttachedPicture(paramID3v2FrameBody.getData().readByte(), paramID3v2FrameBody.readZeroTerminatedString(200, localID3v2Encoding), str, paramID3v2FrameBody.getData().readFully((int)paramID3v2FrameBody.getRemainingLength()));
      if (!str.equals("PNG"))
        break;
      i = 0;
      break;
      if (!str.equals("JPG"))
        break;
      i = 1;
      break;
      str = "image/png";
      continue;
      str = "image/jpeg";
      continue;
      str = paramID3v2FrameBody.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
    }
  }

  CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody paramID3v2FrameBody)
  {
    ID3v2Encoding localID3v2Encoding = paramID3v2FrameBody.readEncoding();
    return new CommentOrUnsynchronizedLyrics(paramID3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), paramID3v2FrameBody.readZeroTerminatedString(200, localID3v2Encoding), paramID3v2FrameBody.readFixedLengthString((int)paramID3v2FrameBody.getRemainingLength(), localID3v2Encoding));
  }

  void parseFrame(ID3v2FrameBody paramID3v2FrameBody)
  {
    int i = 0;
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, "Parsing frame: " + paramID3v2FrameBody.getFrameHeader().getFrameId());
    Object localObject1 = paramID3v2FrameBody.getFrameHeader().getFrameId();
    switch (((String)localObject1).hashCode())
    {
    default:
      i = -1;
      label342: switch (i)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      }
    case 79210:
    case 2015625:
    case 66913:
    case 2074380:
    case 82815:
    case 2567331:
    case 82881:
    case 2569298:
    case 82878:
    case 2569357:
    case 82880:
    case 2569358:
    case 82883:
    case 2569360:
    case 2570401:
    case 83149:
    case 2577697:
    case 83253:
    case 2581512:
    case 83254:
    case 2581513:
    case 83269:
    case 2581856:
    case 83341:
    case 2583398:
    case 83377:
    case 2575250:
    case 83378:
    case 2575251:
    case 83552:
    case 2590194:
    case 84125:
    case 2614438:
    }
    while (true)
    {
      return;
      if (!((String)localObject1).equals("PIC"))
        break;
      break label342;
      if (!((String)localObject1).equals("APIC"))
        break;
      i = 1;
      break label342;
      if (!((String)localObject1).equals("COM"))
        break;
      i = 2;
      break label342;
      if (!((String)localObject1).equals("COMM"))
        break;
      i = 3;
      break label342;
      if (!((String)localObject1).equals("TAL"))
        break;
      i = 4;
      break label342;
      if (!((String)localObject1).equals("TALB"))
        break;
      i = 5;
      break label342;
      if (!((String)localObject1).equals("TCP"))
        break;
      i = 6;
      break label342;
      if (!((String)localObject1).equals("TCMP"))
        break;
      i = 7;
      break label342;
      if (!((String)localObject1).equals("TCM"))
        break;
      i = 8;
      break label342;
      if (!((String)localObject1).equals("TCOM"))
        break;
      i = 9;
      break label342;
      if (!((String)localObject1).equals("TCO"))
        break;
      i = 10;
      break label342;
      if (!((String)localObject1).equals("TCON"))
        break;
      i = 11;
      break label342;
      if (!((String)localObject1).equals("TCR"))
        break;
      i = 12;
      break label342;
      if (!((String)localObject1).equals("TCOP"))
        break;
      i = 13;
      break label342;
      if (!((String)localObject1).equals("TDRC"))
        break;
      i = 14;
      break label342;
      if (!((String)localObject1).equals("TLE"))
        break;
      i = 15;
      break label342;
      if (!((String)localObject1).equals("TLEN"))
        break;
      i = 16;
      break label342;
      if (!((String)localObject1).equals("TP1"))
        break;
      i = 17;
      break label342;
      if (!((String)localObject1).equals("TPE1"))
        break;
      i = 18;
      break label342;
      if (!((String)localObject1).equals("TP2"))
        break;
      i = 19;
      break label342;
      if (!((String)localObject1).equals("TPE2"))
        break;
      i = 20;
      break label342;
      if (!((String)localObject1).equals("TPA"))
        break;
      i = 21;
      break label342;
      if (!((String)localObject1).equals("TPOS"))
        break;
      i = 22;
      break label342;
      if (!((String)localObject1).equals("TRK"))
        break;
      i = 23;
      break label342;
      if (!((String)localObject1).equals("TRCK"))
        break;
      i = 24;
      break label342;
      if (!((String)localObject1).equals("TT1"))
        break;
      i = 25;
      break label342;
      if (!((String)localObject1).equals("TIT1"))
        break;
      i = 26;
      break label342;
      if (!((String)localObject1).equals("TT2"))
        break;
      i = 27;
      break label342;
      if (!((String)localObject1).equals("TIT2"))
        break;
      i = 28;
      break label342;
      if (!((String)localObject1).equals("TYE"))
        break;
      i = 29;
      break label342;
      if (!((String)localObject1).equals("TYER"))
        break;
      i = 30;
      break label342;
      if (!((String)localObject1).equals("ULT"))
        break;
      i = 31;
      break label342;
      if (!((String)localObject1).equals("USLT"))
        break;
      i = 32;
      break label342;
      if ((this.cover != null) && (this.coverPictureType == 3))
        continue;
      paramID3v2FrameBody = parseAttachedPictureFrame(paramID3v2FrameBody);
      if ((this.cover != null) && (paramID3v2FrameBody.type != 3) && (paramID3v2FrameBody.type != 0))
        continue;
      try
      {
        localObject1 = paramID3v2FrameBody.imageData;
        localObject2 = new BitmapFactory.Options();
        ((BitmapFactory.Options)localObject2).inJustDecodeBounds = true;
        ((BitmapFactory.Options)localObject2).inSampleSize = 1;
        BitmapFactory.decodeByteArray(localObject1, 0, localObject1.length, (BitmapFactory.Options)localObject2);
        if ((((BitmapFactory.Options)localObject2).outWidth > 800) || (((BitmapFactory.Options)localObject2).outHeight > 800))
        {
          i = Math.max(((BitmapFactory.Options)localObject2).outWidth, ((BitmapFactory.Options)localObject2).outHeight);
          while (i > 800)
          {
            ((BitmapFactory.Options)localObject2).inSampleSize *= 2;
            i /= 2;
          }
        }
        ((BitmapFactory.Options)localObject2).inJustDecodeBounds = false;
        this.cover = BitmapFactory.decodeByteArray(localObject1, 0, localObject1.length, (BitmapFactory.Options)localObject2);
        float f;
        if (this.cover != null)
        {
          f = Math.max(this.cover.getWidth(), this.cover.getHeight()) / 120.0F;
          if (f <= 0.0F)
            break label1301;
        }
        label1301: for (this.smallCover = Bitmap.createScaledBitmap(this.cover, (int)(this.cover.getWidth() / f), (int)(this.cover.getHeight() / f), true); ; this.smallCover = this.cover)
        {
          if (this.smallCover == null)
            this.smallCover = this.cover;
          this.coverPictureType = paramID3v2FrameBody.type;
          return;
        }
      }
      catch (Throwable localThrowable)
      {
        while (true)
          localThrowable.printStackTrace();
      }
      paramID3v2FrameBody = parseCommentOrUnsynchronizedLyricsFrame(paramID3v2FrameBody);
      if ((this.comment != null) && (paramID3v2FrameBody.description != null) && (!"".equals(paramID3v2FrameBody.description)))
        continue;
      this.comment = paramID3v2FrameBody.text;
      return;
      this.album = parseTextFrame(paramID3v2FrameBody);
      return;
      this.compilation = "1".equals(parseTextFrame(paramID3v2FrameBody));
      return;
      this.composer = parseTextFrame(paramID3v2FrameBody);
      return;
      Object localObject2 = parseTextFrame(paramID3v2FrameBody);
      if (((String)localObject2).length() <= 0)
        continue;
      this.genre = ((String)localObject2);
      paramID3v2FrameBody = null;
      try
      {
        ID3v1Genre localID3v1Genre;
        if (((String)localObject2).charAt(0) == '(')
        {
          i = ((String)localObject2).indexOf(')');
          if (i > 1)
          {
            localID3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(((String)localObject2).substring(1, i)));
            paramID3v2FrameBody = localID3v1Genre;
            if (localID3v1Genre == null)
            {
              paramID3v2FrameBody = localID3v1Genre;
              if (((String)localObject2).length() > i + 1)
                this.genre = ((String)localObject2).substring(i + 1);
            }
          }
        }
        for (paramID3v2FrameBody = localID3v1Genre; paramID3v2FrameBody != null; paramID3v2FrameBody = ID3v1Genre.getGenre(Integer.parseInt((String)localObject2)))
        {
          this.genre = paramID3v2FrameBody.getDescription();
          return;
        }
        this.copyright = parseTextFrame(paramID3v2FrameBody);
        return;
        paramID3v2FrameBody = parseTextFrame(paramID3v2FrameBody);
        if (paramID3v2FrameBody.length() < 4)
          continue;
        try
        {
          this.year = Short.valueOf(paramID3v2FrameBody.substring(0, 4)).shortValue();
          return;
        }
        catch (NumberFormatException localNumberFormatException1)
        {
        }
        if (!LOGGER.isLoggable(this.debugLevel))
          continue;
        LOGGER.log(this.debugLevel, "Could not parse year from: " + paramID3v2FrameBody);
        return;
        paramID3v2FrameBody = parseTextFrame(paramID3v2FrameBody);
        try
        {
          this.duration = Long.valueOf(paramID3v2FrameBody).longValue();
          return;
        }
        catch (NumberFormatException localNumberFormatException2)
        {
        }
        if (!LOGGER.isLoggable(this.debugLevel))
          continue;
        LOGGER.log(this.debugLevel, "Could not parse track duration: " + paramID3v2FrameBody);
        return;
        this.artist = parseTextFrame(paramID3v2FrameBody);
        return;
        this.albumArtist = parseTextFrame(paramID3v2FrameBody);
        return;
        paramID3v2FrameBody = parseTextFrame(paramID3v2FrameBody);
        if (paramID3v2FrameBody.length() <= 0)
          continue;
        i = paramID3v2FrameBody.indexOf('/');
        if (i < 0)
        {
          try
          {
            this.disc = Short.valueOf(paramID3v2FrameBody).shortValue();
            return;
          }
          catch (NumberFormatException localNumberFormatException3)
          {
          }
          if (!LOGGER.isLoggable(this.debugLevel))
            continue;
          LOGGER.log(this.debugLevel, "Could not parse disc number: " + paramID3v2FrameBody);
          return;
        }
        try
        {
          this.disc = Short.valueOf(paramID3v2FrameBody.substring(0, i)).shortValue();
          try
          {
            this.discs = Short.valueOf(paramID3v2FrameBody.substring(i + 1)).shortValue();
            return;
          }
          catch (NumberFormatException localNumberFormatException4)
          {
          }
          if (!LOGGER.isLoggable(this.debugLevel))
            continue;
          LOGGER.log(this.debugLevel, "Could not parse number of discs: " + paramID3v2FrameBody);
          return;
        }
        catch (NumberFormatException localNumberFormatException5)
        {
          while (true)
          {
            if (!LOGGER.isLoggable(this.debugLevel))
              continue;
            LOGGER.log(this.debugLevel, "Could not parse disc number: " + paramID3v2FrameBody);
          }
        }
        paramID3v2FrameBody = parseTextFrame(paramID3v2FrameBody);
        if (paramID3v2FrameBody.length() <= 0)
          continue;
        i = paramID3v2FrameBody.indexOf('/');
        if (i < 0)
        {
          try
          {
            this.track = Short.valueOf(paramID3v2FrameBody).shortValue();
            return;
          }
          catch (NumberFormatException localNumberFormatException6)
          {
          }
          if (!LOGGER.isLoggable(this.debugLevel))
            continue;
          LOGGER.log(this.debugLevel, "Could not parse track number: " + paramID3v2FrameBody);
          return;
        }
        try
        {
          this.track = Short.valueOf(paramID3v2FrameBody.substring(0, i)).shortValue();
          try
          {
            this.tracks = Short.valueOf(paramID3v2FrameBody.substring(i + 1)).shortValue();
            return;
          }
          catch (NumberFormatException localNumberFormatException7)
          {
          }
          if (!LOGGER.isLoggable(this.debugLevel))
            continue;
          LOGGER.log(this.debugLevel, "Could not parse number of tracks: " + paramID3v2FrameBody);
          return;
        }
        catch (NumberFormatException localNumberFormatException8)
        {
          while (true)
          {
            if (!LOGGER.isLoggable(this.debugLevel))
              continue;
            LOGGER.log(this.debugLevel, "Could not parse track number: " + paramID3v2FrameBody);
          }
        }
        this.grouping = parseTextFrame(paramID3v2FrameBody);
        return;
        this.title = parseTextFrame(paramID3v2FrameBody);
        return;
        paramID3v2FrameBody = parseTextFrame(paramID3v2FrameBody);
        if (paramID3v2FrameBody.length() <= 0)
          continue;
        try
        {
          this.year = Short.valueOf(paramID3v2FrameBody).shortValue();
          return;
        }
        catch (NumberFormatException localNumberFormatException9)
        {
        }
        if (!LOGGER.isLoggable(this.debugLevel))
          continue;
        LOGGER.log(this.debugLevel, "Could not parse year: " + paramID3v2FrameBody);
        return;
        if (this.lyrics != null)
          continue;
        this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(paramID3v2FrameBody).text;
        return;
      }
      catch (NumberFormatException paramID3v2FrameBody)
      {
      }
    }
  }

  String parseTextFrame(ID3v2FrameBody paramID3v2FrameBody)
  {
    ID3v2Encoding localID3v2Encoding = paramID3v2FrameBody.readEncoding();
    return paramID3v2FrameBody.readFixedLengthString((int)paramID3v2FrameBody.getRemainingLength(), localID3v2Encoding);
  }

  static class AttachedPicture
  {
    static final byte TYPE_COVER_FRONT = 3;
    static final byte TYPE_OTHER = 0;
    final String description;
    final byte[] imageData;
    final String imageType;
    final byte type;

    public AttachedPicture(byte paramByte, String paramString1, String paramString2, byte[] paramArrayOfByte)
    {
      this.type = paramByte;
      this.description = paramString1;
      this.imageType = paramString2;
      this.imageData = paramArrayOfByte;
    }
  }

  static class CommentOrUnsynchronizedLyrics
  {
    final String description;
    final String language;
    final String text;

    public CommentOrUnsynchronizedLyrics(String paramString1, String paramString2, String paramString3)
    {
      this.language = paramString1;
      this.description = paramString2;
      this.text = paramString3;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.mp3.ID3v2Info
 * JD-Core Version:    0.6.0
 */