package org.vidogram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DefaultTsPayloadReaderFactory
  implements TsPayloadReader.Factory
{
  public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int FLAG_DETECT_ACCESS_UNITS = 8;
  public static final int FLAG_IGNORE_AAC_STREAM = 2;
  public static final int FLAG_IGNORE_H264_STREAM = 4;
  private final int flags;

  public DefaultTsPayloadReaderFactory()
  {
    this(0);
  }

  public DefaultTsPayloadReaderFactory(int paramInt)
  {
    this.flags = paramInt;
  }

  public SparseArray<TsPayloadReader> createInitialPayloadReaders()
  {
    return new SparseArray();
  }

  public TsPayloadReader createPayloadReader(int paramInt, TsPayloadReader.EsInfo paramEsInfo)
  {
    boolean bool2 = true;
    switch (paramInt)
    {
    default:
    case 3:
    case 4:
    case 15:
    case 129:
    case 135:
    case 130:
    case 138:
    case 2:
    case 27:
      do
      {
        do
        {
          return null;
          return new PesReader(new MpegAudioReader(paramEsInfo.language));
        }
        while ((this.flags & 0x2) != 0);
        return new PesReader(new AdtsReader(false, paramEsInfo.language));
        return new PesReader(new Ac3Reader(paramEsInfo.language));
        return new PesReader(new DtsReader(paramEsInfo.language));
        return new PesReader(new H262Reader());
      }
      while ((this.flags & 0x4) != 0);
      boolean bool1;
      if ((this.flags & 0x1) != 0)
      {
        bool1 = true;
        if ((this.flags & 0x8) == 0)
          break label268;
      }
      while (true)
      {
        return new PesReader(new H264Reader(bool1, bool2));
        bool1 = false;
        break;
        bool2 = false;
      }
    case 36:
      return new PesReader(new H265Reader());
    case 134:
      label268: return new SectionReader(new SpliceInfoSectionReader());
    case 21:
    }
    return new PesReader(new Id3Reader());
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory
 * JD-Core Version:    0.6.0
 */