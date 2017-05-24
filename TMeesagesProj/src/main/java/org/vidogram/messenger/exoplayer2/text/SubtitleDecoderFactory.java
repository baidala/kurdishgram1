package org.vidogram.messenger.exoplayer2.text;

import java.lang.reflect.Constructor;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.text.cea.Cea608Decoder;

public abstract interface SubtitleDecoderFactory
{
  public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory()
  {
    private Class<?> getDecoderClass(String paramString)
    {
      if (paramString == null)
        return null;
      int i = -1;
      try
      {
        switch (paramString.hashCode())
        {
        case -1004728940:
          return Class.forName("org.vidogram.messenger.exoplayer2.text.webvtt.WebvttDecoder");
          if (!paramString.equals("text/vtt"))
            break;
          i = 0;
          break;
        case 1693976202:
          if (!paramString.equals("application/ttml+xml"))
            break;
          i = 1;
          break;
        case 1490991545:
          if (!paramString.equals("application/x-mp4vtt"))
            break;
          i = 2;
          break;
        case 1668750253:
          if (!paramString.equals("application/x-subrip"))
            break;
          i = 3;
          break;
        case 691401887:
          if (!paramString.equals("application/x-quicktime-tx3g"))
            break;
          i = 4;
          break;
        case 1566015601:
          if (!paramString.equals("application/cea-608"))
            break;
          i = 5;
          break;
          return Class.forName("org.vidogram.messenger.exoplayer2.text.ttml.TtmlDecoder");
          return Class.forName("org.vidogram.messenger.exoplayer2.text.webvtt.Mp4WebvttDecoder");
          return Class.forName("org.vidogram.messenger.exoplayer2.text.subrip.SubripDecoder");
          return Class.forName("org.vidogram.messenger.exoplayer2.text.tx3g.Tx3gDecoder");
          paramString = Class.forName("org.vidogram.messenger.exoplayer2.text.cea.Cea608Decoder");
          return paramString;
        }
      }
      catch (java.lang.ClassNotFoundException paramString)
      {
        return null;
      }
      switch (i)
      {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      }
      return null;
    }

    public SubtitleDecoder createDecoder(Format paramFormat)
    {
      Class localClass;
      try
      {
        localClass = getDecoderClass(paramFormat.sampleMimeType);
        if (localClass == null)
          throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
      }
      catch (java.lang.Exception paramFormat)
      {
        throw new IllegalStateException("Unexpected error instantiating decoder", paramFormat);
      }
      if (localClass == Cea608Decoder.class)
        return (SubtitleDecoder)localClass.asSubclass(SubtitleDecoder.class).getConstructor(new Class[] { Integer.TYPE }).newInstance(new Object[] { Integer.valueOf(paramFormat.accessibilityChannel) });
      paramFormat = (SubtitleDecoder)localClass.asSubclass(SubtitleDecoder.class).getConstructor(new Class[0]).newInstance(new Object[0]);
      return paramFormat;
    }

    public boolean supportsFormat(Format paramFormat)
    {
      return getDecoderClass(paramFormat.sampleMimeType) != null;
    }
  };

  public abstract SubtitleDecoder createDecoder(Format paramFormat);

  public abstract boolean supportsFormat(Format paramFormat);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.text.SubtitleDecoderFactory
 * JD-Core Version:    0.6.0
 */