package org.vidogram.messenger.exoplayer2.extractor.mp4;

import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

final class Sniffer
{
  private static final int[] COMPATIBLE_BRANDS = { Util.getIntegerCodeForString("isom"), Util.getIntegerCodeForString("iso2"), Util.getIntegerCodeForString("iso3"), Util.getIntegerCodeForString("iso4"), Util.getIntegerCodeForString("iso5"), Util.getIntegerCodeForString("iso6"), Util.getIntegerCodeForString("avc1"), Util.getIntegerCodeForString("hvc1"), Util.getIntegerCodeForString("hev1"), Util.getIntegerCodeForString("mp41"), Util.getIntegerCodeForString("mp42"), Util.getIntegerCodeForString("3g2a"), Util.getIntegerCodeForString("3g2b"), Util.getIntegerCodeForString("3gr6"), Util.getIntegerCodeForString("3gs6"), Util.getIntegerCodeForString("3ge6"), Util.getIntegerCodeForString("3gg6"), Util.getIntegerCodeForString("M4V "), Util.getIntegerCodeForString("M4A "), Util.getIntegerCodeForString("f4v "), Util.getIntegerCodeForString("kddi"), Util.getIntegerCodeForString("M4VP"), Util.getIntegerCodeForString("qt  "), Util.getIntegerCodeForString("MSNV") };
  private static final int SEARCH_LENGTH = 4096;

  private static boolean isCompatibleBrand(int paramInt)
  {
    if (paramInt >>> 8 == Util.getIntegerCodeForString("3gp"))
      return true;
    int[] arrayOfInt = COMPATIBLE_BRANDS;
    int j = arrayOfInt.length;
    int i = 0;
    while (true)
    {
      if (i >= j)
        break label42;
      if (arrayOfInt[i] == paramInt)
        break;
      i += 1;
    }
    label42: return false;
  }

  public static boolean sniffFragmented(ExtractorInput paramExtractorInput)
  {
    return sniffInternal(paramExtractorInput, true);
  }

  private static boolean sniffInternal(ExtractorInput paramExtractorInput, boolean paramBoolean)
  {
    long l2 = paramExtractorInput.getLength();
    long l1;
    if (l2 != -1L)
    {
      l1 = l2;
      if (l2 <= 4096L);
    }
    else
    {
      l1 = 4096L;
    }
    int n = (int)l1;
    ParsableByteArray localParsableByteArray = new ParsableByteArray(64);
    int i = 0;
    boolean bool2 = false;
    int j = 0;
    while (true)
    {
      boolean bool1 = bool2;
      int k;
      int i2;
      int m;
      if (j < n)
      {
        k = 8;
        localParsableByteArray.reset(8);
        paramExtractorInput.peekFully(localParsableByteArray.data, 0, 8);
        l2 = localParsableByteArray.readUnsignedInt();
        i2 = localParsableByteArray.readInt();
        l1 = l2;
        if (l2 == 1L)
        {
          k = 16;
          paramExtractorInput.peekFully(localParsableByteArray.data, 8, 8);
          localParsableByteArray.setLimit(16);
          l1 = localParsableByteArray.readUnsignedLongToLong();
        }
        if (l1 < k)
          return false;
        m = j + k;
        j = m;
        if (i2 == Atom.TYPE_moov)
          continue;
        if ((i2 != Atom.TYPE_moof) && (i2 != Atom.TYPE_mvex))
          break label210;
        bool1 = true;
      }
      label210: 
      do
      {
        if ((i == 0) || (paramBoolean != bool1))
          break;
        return true;
        bool1 = bool2;
      }
      while (m + l1 - k >= n);
      int i1 = (int)(l1 - k);
      if (i2 == Atom.TYPE_ftyp)
      {
        if (i1 < 8)
          return false;
        localParsableByteArray.reset(i1);
        paramExtractorInput.peekFully(localParsableByteArray.data, 0, i1);
        i2 = i1 / 4;
        k = 0;
        j = i;
        if (k < i2)
        {
          if (k == 1)
            localParsableByteArray.skipBytes(4);
          do
          {
            k += 1;
            break;
          }
          while (!isCompatibleBrand(localParsableByteArray.readInt()));
          j = 1;
        }
        k = j;
        if (j == 0)
          return false;
      }
      else
      {
        k = i;
        if (i1 != 0)
        {
          paramExtractorInput.advancePeekPosition(i1);
          k = i;
        }
      }
      j = m + i1;
      i = k;
    }
    return false;
  }

  public static boolean sniffUnfragmented(ExtractorInput paramExtractorInput)
  {
    return sniffInternal(paramExtractorInput, false);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp4.Sniffer
 * JD-Core Version:    0.6.0
 */