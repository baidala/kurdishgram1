package org.vidogram.messenger.audioinfo.m4a;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vidogram.messenger.audioinfo.AudioInfo;
import org.vidogram.messenger.audioinfo.mp3.ID3v1Genre;

public class M4AInfo extends AudioInfo
{
  private static final String ASCII = "ISO8859_1";
  static final Logger LOGGER = Logger.getLogger(M4AInfo.class.getName());
  private static final String UTF_8 = "UTF-8";
  private final Level debugLevel;
  private byte rating;
  private BigDecimal speed;
  private short tempo;
  private BigDecimal volume;

  public M4AInfo(InputStream paramInputStream)
  {
    this(paramInputStream, Level.FINEST);
  }

  public M4AInfo(InputStream paramInputStream, Level paramLevel)
  {
    this.debugLevel = paramLevel;
    paramInputStream = new MP4Input(paramInputStream);
    if (LOGGER.isLoggable(paramLevel))
      LOGGER.log(paramLevel, paramInputStream.toString());
    ftyp(paramInputStream.nextChild("ftyp"));
    moov(paramInputStream.nextChildUpTo("moov"));
  }

  void data(MP4Atom paramMP4Atom)
  {
    int i = 0;
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    paramMP4Atom.skip(4);
    paramMP4Atom.skip(4);
    Object localObject = paramMP4Atom.getParent().getType();
    switch (((String)localObject).hashCode())
    {
    default:
      i = -1;
      label226: switch (i)
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
      }
    case 5131342:
    case 2954818:
    case 5099770:
    case 5133313:
    case 5133368:
    case 5152688:
    case 3059752:
    case 3060304:
    case 3060591:
    case 5133411:
    case 5133907:
    case 3083677:
    case 3177818:
    case 5136903:
    case 5137308:
    case 5142332:
    case 5143505:
    case 3511163:
    case 3564088:
    case 3568737:
    }
    label1080: 
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                if (!((String)localObject).equals("©alb"))
                  break;
                break label226;
                if (!((String)localObject).equals("aART"))
                  break;
                i = 1;
                break label226;
                if (!((String)localObject).equals("©ART"))
                  break;
                i = 2;
                break label226;
                if (!((String)localObject).equals("©cmt"))
                  break;
                i = 3;
                break label226;
                if (!((String)localObject).equals("©com"))
                  break;
                i = 4;
                break label226;
                if (!((String)localObject).equals("©wrt"))
                  break;
                i = 5;
                break label226;
                if (!((String)localObject).equals("covr"))
                  break;
                i = 6;
                break label226;
                if (!((String)localObject).equals("cpil"))
                  break;
                i = 7;
                break label226;
                if (!((String)localObject).equals("cprt"))
                  break;
                i = 8;
                break label226;
                if (!((String)localObject).equals("©cpy"))
                  break;
                i = 9;
                break label226;
                if (!((String)localObject).equals("©day"))
                  break;
                i = 10;
                break label226;
                if (!((String)localObject).equals("disk"))
                  break;
                i = 11;
                break label226;
                if (!((String)localObject).equals("gnre"))
                  break;
                i = 12;
                break label226;
                if (!((String)localObject).equals("©gen"))
                  break;
                i = 13;
                break label226;
                if (!((String)localObject).equals("©grp"))
                  break;
                i = 14;
                break label226;
                if (!((String)localObject).equals("©lyr"))
                  break;
                i = 15;
                break label226;
                if (!((String)localObject).equals("©nam"))
                  break;
                i = 16;
                break label226;
                if (!((String)localObject).equals("rtng"))
                  break;
                i = 17;
                break label226;
                if (!((String)localObject).equals("tmpo"))
                  break;
                i = 18;
                break label226;
                if (!((String)localObject).equals("trkn"))
                  break;
                i = 19;
                break label226;
                this.album = paramMP4Atom.readString("UTF-8");
                return;
                this.albumArtist = paramMP4Atom.readString("UTF-8");
                return;
                this.artist = paramMP4Atom.readString("UTF-8");
                return;
                this.comment = paramMP4Atom.readString("UTF-8");
                return;
              }
              while ((this.composer != null) && (this.composer.trim().length() != 0));
              this.composer = paramMP4Atom.readString("UTF-8");
              return;
              while (true)
              {
                try
                {
                  paramMP4Atom = paramMP4Atom.readBytes();
                  localObject = new BitmapFactory.Options();
                  ((BitmapFactory.Options)localObject).inJustDecodeBounds = true;
                  ((BitmapFactory.Options)localObject).inSampleSize = 1;
                  BitmapFactory.decodeByteArray(paramMP4Atom, 0, paramMP4Atom.length, (BitmapFactory.Options)localObject);
                  if ((((BitmapFactory.Options)localObject).outWidth <= 800) && (((BitmapFactory.Options)localObject).outHeight <= 800))
                    continue;
                  i = Math.max(((BitmapFactory.Options)localObject).outWidth, ((BitmapFactory.Options)localObject).outHeight);
                  if (i <= 800)
                    continue;
                  ((BitmapFactory.Options)localObject).inSampleSize *= 2;
                  i /= 2;
                  continue;
                  ((BitmapFactory.Options)localObject).inJustDecodeBounds = false;
                  this.cover = BitmapFactory.decodeByteArray(paramMP4Atom, 0, paramMP4Atom.length, (BitmapFactory.Options)localObject);
                  if (this.cover == null)
                    break;
                  float f = Math.max(this.cover.getWidth(), this.cover.getHeight()) / 120.0F;
                  if (f > 0.0F)
                  {
                    this.smallCover = Bitmap.createScaledBitmap(this.cover, (int)(this.cover.getWidth() / f), (int)(this.cover.getHeight() / f), true);
                    if (this.smallCover != null)
                      break;
                    this.smallCover = this.cover;
                    return;
                  }
                }
                catch (Exception paramMP4Atom)
                {
                  paramMP4Atom.printStackTrace();
                  return;
                }
                this.smallCover = this.cover;
              }
              this.compilation = paramMP4Atom.readBoolean();
              return;
            }
            while ((this.copyright != null) && (this.copyright.trim().length() != 0));
            this.copyright = paramMP4Atom.readString("UTF-8");
            return;
            paramMP4Atom = paramMP4Atom.readString("UTF-8").trim();
          }
          while (paramMP4Atom.length() < 4);
          try
          {
            this.year = Short.valueOf(paramMP4Atom.substring(0, 4)).shortValue();
            return;
          }
          catch (java.lang.NumberFormatException paramMP4Atom)
          {
            return;
          }
          paramMP4Atom.skip(2);
          this.disc = paramMP4Atom.readShort();
          this.discs = paramMP4Atom.readShort();
          return;
        }
        while ((this.genre != null) && (this.genre.trim().length() != 0));
        if (paramMP4Atom.getRemaining() != 2L)
          break label1080;
        paramMP4Atom = ID3v1Genre.getGenre(paramMP4Atom.readShort() - 1);
      }
      while (paramMP4Atom == null);
      this.genre = paramMP4Atom.getDescription();
      return;
      this.genre = paramMP4Atom.readString("UTF-8");
      return;
    }
    while ((this.genre != null) && (this.genre.trim().length() != 0));
    this.genre = paramMP4Atom.readString("UTF-8");
    return;
    this.grouping = paramMP4Atom.readString("UTF-8");
    return;
    this.lyrics = paramMP4Atom.readString("UTF-8");
    return;
    this.title = paramMP4Atom.readString("UTF-8");
    return;
    this.rating = paramMP4Atom.readByte();
    return;
    this.tempo = paramMP4Atom.readShort();
    return;
    paramMP4Atom.skip(2);
    this.track = paramMP4Atom.readShort();
    this.tracks = paramMP4Atom.readShort();
  }

  void ftyp(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    this.brand = paramMP4Atom.readString(4, "ISO8859_1").trim();
    if (this.brand.matches("M4V|MP4|mp42|isom"))
      LOGGER.warning(paramMP4Atom.getPath() + ": brand=" + this.brand + " (experimental)");
    while (true)
    {
      this.version = String.valueOf(paramMP4Atom.readInt());
      return;
      if (this.brand.matches("M4A|M4P"))
        continue;
      LOGGER.warning(paramMP4Atom.getPath() + ": brand=" + this.brand + " (expected M4A or M4P)");
    }
  }

  public byte getRating()
  {
    return this.rating;
  }

  public BigDecimal getSpeed()
  {
    return this.speed;
  }

  public short getTempo()
  {
    return this.tempo;
  }

  public BigDecimal getVolume()
  {
    return this.volume;
  }

  void ilst(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if (LOGGER.isLoggable(this.debugLevel))
        LOGGER.log(this.debugLevel, localMP4Atom.toString());
      if (localMP4Atom.getRemaining() == 0L)
      {
        if (!LOGGER.isLoggable(this.debugLevel))
          continue;
        LOGGER.log(this.debugLevel, localMP4Atom.getPath() + ": contains no value");
        continue;
      }
      data(localMP4Atom.nextChildUpTo("data"));
    }
  }

  void mdhd(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    int j = paramMP4Atom.readByte();
    paramMP4Atom.skip(3);
    int i;
    long l;
    if (j == 1)
    {
      i = 16;
      paramMP4Atom.skip(i);
      i = paramMP4Atom.readInt();
      if (j != 1)
        break label95;
      l = paramMP4Atom.readLong();
      label66: if (this.duration != 0L)
        break label105;
      this.duration = (l * 1000L / i);
    }
    label95: label105: 
    do
    {
      return;
      i = 8;
      break;
      l = paramMP4Atom.readInt();
      break label66;
    }
    while ((!LOGGER.isLoggable(this.debugLevel)) || (Math.abs(this.duration - 1000L * l / i) <= 2L));
    LOGGER.log(this.debugLevel, "mdhd: duration " + this.duration + " -> " + l * 1000L / i);
  }

  void mdia(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    mdhd(paramMP4Atom.nextChild("mdhd"));
  }

  void meta(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    paramMP4Atom.skip(4);
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if (!"ilst".equals(localMP4Atom.getType()))
        continue;
      ilst(localMP4Atom);
    }
  }

  void moov(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      String str = localMP4Atom.getType();
      int i = -1;
      switch (str.hashCode())
      {
      default:
      case 3363941:
      case 3568424:
      case 3585340:
      }
      while (true)
        switch (i)
        {
        default:
          break;
        case 0:
          mvhd(localMP4Atom);
          break;
          if (!str.equals("mvhd"))
            continue;
          i = 0;
          continue;
          if (!str.equals("trak"))
            continue;
          i = 1;
          continue;
          if (!str.equals("udta"))
            continue;
          i = 2;
        case 1:
        case 2:
        }
      trak(localMP4Atom);
      continue;
      udta(localMP4Atom);
    }
  }

  void mvhd(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    int j = paramMP4Atom.readByte();
    paramMP4Atom.skip(3);
    int i;
    long l;
    if (j == 1)
    {
      i = 16;
      paramMP4Atom.skip(i);
      i = paramMP4Atom.readInt();
      if (j != 1)
        break label111;
      l = paramMP4Atom.readLong();
      label66: if (this.duration != 0L)
        break label121;
      this.duration = (l * 1000L / i);
    }
    while (true)
    {
      this.speed = paramMP4Atom.readIntegerFixedPoint();
      this.volume = paramMP4Atom.readShortFixedPoint();
      return;
      i = 8;
      break;
      label111: l = paramMP4Atom.readInt();
      break label66;
      label121: if ((!LOGGER.isLoggable(this.debugLevel)) || (Math.abs(this.duration - 1000L * l / i) <= 2L))
        continue;
      LOGGER.log(this.debugLevel, "mvhd: duration " + this.duration + " -> " + l * 1000L / i);
    }
  }

  void trak(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    mdia(paramMP4Atom.nextChildUpTo("mdia"));
  }

  void udta(MP4Atom paramMP4Atom)
  {
    if (LOGGER.isLoggable(this.debugLevel))
      LOGGER.log(this.debugLevel, paramMP4Atom.toString());
    while (paramMP4Atom.hasMoreChildren())
    {
      MP4Atom localMP4Atom = paramMP4Atom.nextChild();
      if (!"meta".equals(localMP4Atom.getType()))
        continue;
      meta(localMP4Atom);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.audioinfo.m4a.M4AInfo
 * JD-Core Version:    0.6.0
 */