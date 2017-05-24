package org.vidogram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.Pair<Ljava.lang.Integer;Lorg.vidogram.messenger.exoplayer2.extractor.mp4.TrackEncryptionBox;>;
import android.util.Pair<Ljava.lang.String;[B>;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.audio.Ac3Util;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.CodecSpecificDataUtil;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;
import org.vidogram.messenger.exoplayer2.video.AvcConfig;
import org.vidogram.messenger.exoplayer2.video.HevcConfig;

final class AtomParsers
{
  private static final String TAG = "AtomParsers";
  private static final int TYPE_cenc;
  private static final int TYPE_clcp;
  private static final int TYPE_meta;
  private static final int TYPE_sbtl;
  private static final int TYPE_soun;
  private static final int TYPE_subt;
  private static final int TYPE_text;
  private static final int TYPE_vide = Util.getIntegerCodeForString("vide");

  static
  {
    TYPE_soun = Util.getIntegerCodeForString("soun");
    TYPE_text = Util.getIntegerCodeForString("text");
    TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
    TYPE_subt = Util.getIntegerCodeForString("subt");
    TYPE_clcp = Util.getIntegerCodeForString("clcp");
    TYPE_cenc = Util.getIntegerCodeForString("cenc");
    TYPE_meta = Util.getIntegerCodeForString("meta");
  }

  private static int findEsdsPosition(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramParsableByteArray.getPosition();
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (j > 0);
      for (boolean bool = true; ; bool = false)
      {
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_esds)
          break;
        return i;
      }
      i += j;
    }
    return -1;
  }

  private static void parseAudioSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString, boolean paramBoolean, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt5)
  {
    paramParsableByteArray.setPosition(paramInt2 + 8);
    int k;
    int m;
    int n;
    int i;
    int j;
    label88: Object localObject1;
    label137: Object localObject2;
    label151: boolean bool;
    label179: label225: Object localObject3;
    Object localObject4;
    if (paramBoolean)
    {
      paramParsableByteArray.skipBytes(8);
      k = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      if ((k != 0) && (k != 1))
        break label369;
      m = paramParsableByteArray.readUnsignedShort();
      paramParsableByteArray.skipBytes(6);
      n = paramParsableByteArray.readUnsignedFixedPoint1616();
      i = n;
      j = m;
      if (k == 1)
      {
        paramParsableByteArray.skipBytes(16);
        j = m;
        i = n;
      }
      m = paramParsableByteArray.getPosition();
      k = paramInt1;
      if (paramInt1 == Atom.TYPE_enca)
      {
        k = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3, paramStsdData, paramInt5);
        paramParsableByteArray.setPosition(m);
      }
      localObject1 = null;
      if (k != Atom.TYPE_ac_3)
        break label406;
      localObject1 = "audio/ac3";
      localObject2 = null;
      paramInt1 = i;
      i = j;
      paramInt5 = m;
      if (paramInt5 - paramInt2 >= paramInt3)
        break label735;
      paramParsableByteArray.setPosition(paramInt5);
      m = paramParsableByteArray.readInt();
      if (m <= 0)
        break label542;
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      n = paramParsableByteArray.readInt();
      if ((n != Atom.TYPE_esds) && ((!paramBoolean) || (n != Atom.TYPE_wave)))
        break label561;
      if (n != Atom.TYPE_esds)
        break label548;
      j = paramInt5;
      if (j == -1)
        break label814;
      localObject1 = parseEsdsFromParent(paramParsableByteArray, j);
      localObject3 = (String)((Pair)localObject1).first;
      localObject4 = (byte[])((Pair)localObject1).second;
      localObject1 = localObject3;
      localObject2 = localObject4;
      if ("audio/mp4a-latm".equals(localObject3))
      {
        localObject1 = CodecSpecificDataUtil.parseAacAudioSpecificConfig(localObject4);
        paramInt1 = ((Integer)((Pair)localObject1).first).intValue();
        i = ((Integer)((Pair)localObject1).second).intValue();
        localObject2 = localObject4;
        localObject1 = localObject3;
      }
    }
    label406: label542: label548: label805: label814: 
    while (true)
    {
      localObject3 = localObject2;
      j = paramInt1;
      k = i;
      localObject4 = localObject1;
      while (true)
      {
        paramInt5 += m;
        localObject1 = localObject4;
        i = k;
        paramInt1 = j;
        localObject2 = localObject3;
        break label151;
        paramParsableByteArray.skipBytes(16);
        k = 0;
        break;
        label369: if (k != 2)
          break label799;
        paramParsableByteArray.skipBytes(16);
        i = (int)Math.round(paramParsableByteArray.readDouble());
        j = paramParsableByteArray.readUnsignedIntToInt();
        paramParsableByteArray.skipBytes(20);
        break label88;
        if (k == Atom.TYPE_ec_3)
        {
          localObject1 = "audio/eac3";
          break label137;
        }
        if (k == Atom.TYPE_dtsc)
        {
          localObject1 = "audio/vnd.dts";
          break label137;
        }
        if ((k == Atom.TYPE_dtsh) || (k == Atom.TYPE_dtsl))
        {
          localObject1 = "audio/vnd.dts.hd";
          break label137;
        }
        if (k == Atom.TYPE_dtse)
        {
          localObject1 = "audio/vnd.dts.hd;profile=lbr";
          break label137;
        }
        if (k == Atom.TYPE_samr)
        {
          localObject1 = "audio/3gpp";
          break label137;
        }
        if (k == Atom.TYPE_sawb)
        {
          localObject1 = "audio/amr-wb";
          break label137;
        }
        if ((k == Atom.TYPE_lpcm) || (k == Atom.TYPE_sowt))
        {
          localObject1 = "audio/raw";
          break label137;
        }
        if (k != Atom.TYPE__mp3)
          break label137;
        localObject1 = "audio/mpeg";
        break label137;
        bool = false;
        break label179;
        j = findEsdsPosition(paramParsableByteArray, paramInt5, m);
        break label225;
        label561: if (n == Atom.TYPE_dac3)
        {
          paramParsableByteArray.setPosition(paramInt5 + 8);
          paramStsdData.format = Ac3Util.parseAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, paramDrmInitData);
          localObject4 = localObject1;
          k = i;
          j = paramInt1;
          localObject3 = localObject2;
          continue;
        }
        if (n == Atom.TYPE_dec3)
        {
          paramParsableByteArray.setPosition(paramInt5 + 8);
          paramStsdData.format = Ac3Util.parseEAc3AnnexFFormat(paramParsableByteArray, Integer.toString(paramInt4), paramString, paramDrmInitData);
          localObject4 = localObject1;
          k = i;
          j = paramInt1;
          localObject3 = localObject2;
          continue;
        }
        localObject4 = localObject1;
        k = i;
        j = paramInt1;
        localObject3 = localObject2;
        if (n != Atom.TYPE_ddts)
          continue;
        paramStsdData.format = Format.createAudioSampleFormat(Integer.toString(paramInt4), (String)localObject1, null, -1, -1, i, paramInt1, null, paramDrmInitData, 0, paramString);
        localObject4 = localObject1;
        k = i;
        j = paramInt1;
        localObject3 = localObject2;
      }
      label735: if ((paramStsdData.format == null) && (localObject1 != null))
      {
        if (!"audio/raw".equals(localObject1))
          break label800;
        paramInt2 = 2;
        localObject3 = Integer.toString(paramInt4);
        if (localObject2 != null)
          break label805;
      }
      for (paramParsableByteArray = null; ; paramParsableByteArray = Collections.singletonList(localObject2))
      {
        paramStsdData.format = Format.createAudioSampleFormat((String)localObject3, (String)localObject1, null, -1, -1, i, paramInt1, paramInt2, paramParsableByteArray, paramDrmInitData, 0, paramString);
        return;
        paramInt2 = -1;
        break;
      }
    }
  }

  private static Pair<long[], long[]> parseEdts(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom != null)
    {
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_elst);
      if (paramContainerAtom != null);
    }
    else
    {
      return Pair.create(null, null);
    }
    paramContainerAtom = paramContainerAtom.data;
    paramContainerAtom.setPosition(8);
    int j = Atom.parseFullAtomVersion(paramContainerAtom.readInt());
    int k = paramContainerAtom.readUnsignedIntToInt();
    long[] arrayOfLong1 = new long[k];
    long[] arrayOfLong2 = new long[k];
    int i = 0;
    while (i < k)
    {
      long l;
      if (j == 1)
      {
        l = paramContainerAtom.readUnsignedLongToLong();
        arrayOfLong1[i] = l;
        if (j != 1)
          break label125;
        l = paramContainerAtom.readLong();
      }
      while (true)
      {
        arrayOfLong2[i] = l;
        if (paramContainerAtom.readShort() == 1)
          break label135;
        throw new IllegalArgumentException("Unsupported media rate.");
        l = paramContainerAtom.readUnsignedInt();
        break;
        label125: l = paramContainerAtom.readInt();
      }
      label135: paramContainerAtom.skipBytes(2);
      i += 1;
    }
    return Pair.create(arrayOfLong1, arrayOfLong2);
  }

  private static Pair<String, byte[]> parseEsdsFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    Object localObject = null;
    paramParsableByteArray.setPosition(paramInt + 8 + 4);
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    paramParsableByteArray.skipBytes(2);
    paramInt = paramParsableByteArray.readUnsignedByte();
    if ((paramInt & 0x80) != 0)
      paramParsableByteArray.skipBytes(2);
    if ((paramInt & 0x40) != 0)
      paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedShort());
    if ((paramInt & 0x20) != 0)
      paramParsableByteArray.skipBytes(2);
    paramParsableByteArray.skipBytes(1);
    parseExpandableClassSize(paramParsableByteArray);
    switch (paramParsableByteArray.readUnsignedByte())
    {
    default:
    case 107:
    case 32:
    case 33:
    case 35:
    case 64:
    case 102:
    case 103:
    case 104:
    case 165:
    case 166:
      while (true)
      {
        paramParsableByteArray.skipBytes(12);
        paramParsableByteArray.skipBytes(1);
        paramInt = parseExpandableClassSize(paramParsableByteArray);
        byte[] arrayOfByte = new byte[paramInt];
        paramParsableByteArray.readBytes(arrayOfByte, 0, paramInt);
        return Pair.create(localObject, arrayOfByte);
        return Pair.create("audio/mpeg", null);
        localObject = "video/mp4v-es";
        continue;
        localObject = "video/avc";
        continue;
        localObject = "video/hevc";
        continue;
        localObject = "audio/mp4a-latm";
        continue;
        localObject = "audio/ac3";
        continue;
        localObject = "audio/eac3";
      }
    case 169:
    case 172:
      return Pair.create("audio/vnd.dts", null);
    case 170:
    case 171:
    }
    return (Pair<String, byte[]>)Pair.create("audio/vnd.dts.hd", null);
  }

  private static int parseExpandableClassSize(ParsableByteArray paramParsableByteArray)
  {
    int j = paramParsableByteArray.readUnsignedByte();
    for (int i = j & 0x7F; (j & 0x80) == 128; i = i << 7 | j & 0x7F)
      j = paramParsableByteArray.readUnsignedByte();
    return i;
  }

  private static int parseHdlr(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(16);
    int i = paramParsableByteArray.readInt();
    if (i == TYPE_soun)
      return 1;
    if (i == TYPE_vide)
      return 2;
    if ((i == TYPE_text) || (i == TYPE_sbtl) || (i == TYPE_subt) || (i == TYPE_clcp))
      return 3;
    if (i == TYPE_meta)
      return 4;
    return -1;
  }

  private static Metadata parseIlst(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(8);
    ArrayList localArrayList = new ArrayList();
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      Metadata.Entry localEntry = MetadataUtil.parseIlstElement(paramParsableByteArray);
      if (localEntry == null)
        continue;
      localArrayList.add(localEntry);
    }
    if (localArrayList.isEmpty())
      return null;
    return new Metadata(localArrayList);
  }

  private static Pair<Long, String> parseMdhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    if (k == 0);
    for (int i = 8; ; i = 16)
    {
      paramParsableByteArray.skipBytes(i);
      long l = paramParsableByteArray.readUnsignedInt();
      i = j;
      if (k == 0)
        i = 4;
      paramParsableByteArray.skipBytes(i);
      i = paramParsableByteArray.readUnsignedShort();
      return Pair.create(Long.valueOf(l), "" + (char)((i >> 10 & 0x1F) + 96) + (char)((i >> 5 & 0x1F) + 96) + (char)((i & 0x1F) + 96));
    }
  }

  private static Metadata parseMetaAtom(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.skipBytes(12);
    while (paramParsableByteArray.getPosition() < paramInt)
    {
      int i = paramParsableByteArray.getPosition();
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_ilst)
      {
        paramParsableByteArray.setPosition(i);
        return parseIlst(paramParsableByteArray, i + j);
      }
      paramParsableByteArray.skipBytes(j - 8);
    }
    return null;
  }

  private static long parseMvhd(ParsableByteArray paramParsableByteArray)
  {
    int i = 8;
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0);
    while (true)
    {
      paramParsableByteArray.skipBytes(i);
      return paramParsableByteArray.readUnsignedInt();
      i = 16;
    }
  }

  private static float parsePaspFromParent(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = paramParsableByteArray.readUnsignedIntToInt();
    int i = paramParsableByteArray.readUnsignedIntToInt();
    return paramInt / i;
  }

  private static byte[] parseProjFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    int i = paramInt1 + 8;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_proj)
        return Arrays.copyOfRange(paramParsableByteArray.data, i, j + i);
      i += j;
    }
    return null;
  }

  private static int parseSampleEntryEncryptionData(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, StsdData paramStsdData, int paramInt3)
  {
    int k = 0;
    int i = paramParsableByteArray.getPosition();
    while (true)
    {
      int j = k;
      if (i - paramInt1 < paramInt2)
      {
        paramParsableByteArray.setPosition(i);
        j = paramParsableByteArray.readInt();
        if (j <= 0)
          break label104;
      }
      label104: for (boolean bool = true; ; bool = false)
      {
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        if (paramParsableByteArray.readInt() != Atom.TYPE_sinf)
          break;
        Pair localPair = parseSinfFromParent(paramParsableByteArray, i, j);
        if (localPair == null)
          break;
        paramStsdData.trackEncryptionBoxes[paramInt3] = ((TrackEncryptionBox)localPair.second);
        j = ((Integer)localPair.first).intValue();
        return j;
      }
      i += j;
    }
  }

  private static TrackEncryptionBox parseSchiFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool = true;
    int i = paramInt1 + 8;
    while (i - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(i);
      int j = paramParsableByteArray.readInt();
      if (paramParsableByteArray.readInt() == Atom.TYPE_tenc)
      {
        paramParsableByteArray.skipBytes(6);
        if (paramParsableByteArray.readUnsignedByte() == 1);
        while (true)
        {
          paramInt1 = paramParsableByteArray.readUnsignedByte();
          byte[] arrayOfByte = new byte[16];
          paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
          return new TrackEncryptionBox(bool, paramInt1, arrayOfByte);
          bool = false;
        }
      }
      i += j;
    }
    return null;
  }

  private static Pair<Integer, TrackEncryptionBox> parseSinfFromParent(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2)
  {
    boolean bool2 = true;
    TrackEncryptionBox localTrackEncryptionBox = null;
    int k = 0;
    int j = paramInt1 + 8;
    Object localObject1 = null;
    if (j - paramInt1 < paramInt2)
    {
      paramParsableByteArray.setPosition(j);
      int m = paramParsableByteArray.readInt();
      int n = paramParsableByteArray.readInt();
      Object localObject2;
      int i;
      if (n == Atom.TYPE_frma)
      {
        localObject2 = Integer.valueOf(paramParsableByteArray.readInt());
        i = k;
      }
      while (true)
      {
        j += m;
        localObject1 = localObject2;
        k = i;
        break;
        if (n == Atom.TYPE_schm)
        {
          paramParsableByteArray.skipBytes(4);
          if (paramParsableByteArray.readInt() == TYPE_cenc)
          {
            i = 1;
            localObject2 = localObject1;
            continue;
          }
          i = 0;
          localObject2 = localObject1;
          continue;
        }
        localObject2 = localObject1;
        i = k;
        if (n != Atom.TYPE_schi)
          continue;
        localTrackEncryptionBox = parseSchiFromParent(paramParsableByteArray, j, m);
        localObject2 = localObject1;
        i = k;
      }
    }
    if (k != 0)
    {
      if (localObject1 != null)
      {
        bool1 = true;
        Assertions.checkArgument(bool1, "frma atom is mandatory");
        if (localTrackEncryptionBox == null)
          break label209;
      }
      label209: for (boolean bool1 = bool2; ; bool1 = false)
      {
        Assertions.checkArgument(bool1, "schi->tenc atom is mandatory");
        return Pair.create(localObject1, localTrackEncryptionBox);
        bool1 = false;
        break;
      }
    }
    return (Pair<Integer, TrackEncryptionBox>)null;
  }

  public static TrackSampleTable parseStbl(Track paramTrack, Atom.ContainerAtom paramContainerAtom, GaplessInfoHolder paramGaplessInfoHolder)
  {
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsz);
    if (localObject1 != null);
    int i14;
    for (Object localObject2 = new StszSampleSizeBox((Atom.LeafAtom)localObject1); ; localObject2 = new Stz2SampleSizeBox((Atom.LeafAtom)localObject1))
    {
      i14 = ((SampleSizeBox)localObject2).getSampleCount();
      if (i14 != 0)
        break;
      return new TrackSampleTable(new long[0], new int[0], 0, new long[0], new int[0]);
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stz2);
      if (localObject1 != null)
        continue;
      throw new ParserException("Track has no sample table size information");
    }
    boolean bool = false;
    Object localObject3 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stco);
    localObject1 = localObject3;
    if (localObject3 == null)
    {
      bool = true;
      localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_co64);
    }
    Object localObject4 = ((Atom.LeafAtom)localObject1).data;
    Object localObject5 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stsc).data;
    ParsableByteArray localParsableByteArray = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stts).data;
    localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_stss);
    label202: ChunkIterator localChunkIterator;
    int i5;
    int i6;
    int n;
    int k;
    int i;
    int m;
    int j;
    if (localObject1 != null)
    {
      localObject1 = ((Atom.LeafAtom)localObject1).data;
      paramContainerAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_ctts);
      if (paramContainerAtom == null)
        break label475;
      localObject3 = paramContainerAtom.data;
      localChunkIterator = new ChunkIterator((ParsableByteArray)localObject5, (ParsableByteArray)localObject4, bool);
      localParsableByteArray.setPosition(12);
      i5 = localParsableByteArray.readUnsignedIntToInt() - 1;
      i6 = localParsableByteArray.readUnsignedIntToInt();
      n = localParsableByteArray.readUnsignedIntToInt();
      k = 0;
      if (localObject3 != null)
      {
        ((ParsableByteArray)localObject3).setPosition(12);
        k = ((ParsableByteArray)localObject3).readUnsignedIntToInt();
      }
      if (localObject1 == null)
        break label1996;
      ((ParsableByteArray)localObject1).setPosition(12);
      i = ((ParsableByteArray)localObject1).readUnsignedIntToInt();
      if (i <= 0)
        break label481;
      m = ((ParsableByteArray)localObject1).readUnsignedIntToInt() - 1;
      paramContainerAtom = (Atom.ContainerAtom)localObject1;
      j = i;
      i = m;
    }
    while (true)
    {
      label309: if ((((SampleSizeBox)localObject2).isFixedSampleSize()) && ("audio/raw".equals(paramTrack.format.sampleMimeType)) && (i5 == 0) && (k == 0) && (j == 0));
      Object localObject6;
      long l2;
      int i4;
      int i2;
      long l1;
      int i3;
      int i7;
      int i8;
      for (m = 1; ; m = 0)
      {
        if (m != 0)
          break label983;
        localObject4 = new long[i14];
        localObject1 = new int[i14];
        localObject5 = new long[i14];
        localObject6 = new int[i14];
        l2 = 0L;
        i4 = 0;
        i2 = 0;
        i9 = 0;
        m = k;
        l1 = 0L;
        i10 = 0;
        i3 = 0;
        k = n;
        i7 = i;
        i8 = j;
        j = k;
        k = i10;
        i = m;
        n = i9;
        if (i4 >= i14)
          break label776;
        while (i3 == 0)
        {
          Assertions.checkState(localChunkIterator.moveNext());
          l2 = localChunkIterator.offset;
          i3 = localChunkIterator.numSamples;
        }
        localObject1 = null;
        break;
        label475: localObject3 = null;
        break label202;
        label481: m = -1;
        paramContainerAtom = null;
        j = i;
        i = m;
        break label309;
      }
      int i11 = n;
      int i10 = i;
      int i9 = i2;
      if (localObject3 != null)
      {
        while ((i2 == 0) && (i > 0))
        {
          i2 = ((ParsableByteArray)localObject3).readUnsignedIntToInt();
          n = ((ParsableByteArray)localObject3).readInt();
          i -= 1;
        }
        i9 = i2 - 1;
        i10 = i;
        i11 = n;
      }
      localObject4[i4] = l2;
      localObject1[i4] = ((SampleSizeBox)localObject2).readNextSampleSize();
      int i12 = k;
      if (localObject1[i4] > k)
        i12 = localObject1[i4];
      localObject5[i4] = (i11 + l1);
      label617: int i13;
      if (paramContainerAtom == null)
      {
        i = 1;
        localObject6[i4] = i;
        m = i8;
        i13 = i7;
        if (i4 == i7)
        {
          localObject6[i4] = 1;
          i = i8 - 1;
          if (i <= 0)
            break label1986;
          i13 = paramContainerAtom.readUnsignedIntToInt() - 1;
          m = i;
        }
      }
      while (true)
      {
        long l3 = j;
        k = i6 - 1;
        if ((k == 0) && (i5 > 0))
        {
          j = localParsableByteArray.readUnsignedIntToInt();
          i = localParsableByteArray.readUnsignedIntToInt();
          i5 -= 1;
        }
        while (true)
        {
          long l4 = localObject1[i4];
          i4 += 1;
          l2 += l4;
          i6 = j;
          j = i;
          i3 -= 1;
          l1 = l3 + l1;
          n = i11;
          i = i10;
          i2 = i9;
          k = i12;
          i8 = m;
          i7 = i13;
          break;
          i = 0;
          break label617;
          label776: if (i2 == 0)
          {
            bool = true;
            Assertions.checkArgument(bool);
            label789: if (i <= 0)
              break label834;
            if (((ParsableByteArray)localObject3).readUnsignedIntToInt() != 0)
              break label828;
          }
          label828: for (bool = true; ; bool = false)
          {
            Assertions.checkArgument(bool);
            ((ParsableByteArray)localObject3).readInt();
            i -= 1;
            break label789;
            bool = false;
            break;
          }
          label834: if ((i8 != 0) || (i6 != 0) || (i3 != 0) || (i5 != 0))
            Log.w("AtomParsers", "Inconsistent stbl box for track " + paramTrack.id + ": remainingSynchronizationSamples " + i8 + ", remainingSamplesAtTimestampDelta " + i6 + ", remainingSamplesInChunk " + i3 + ", remainingTimestampDeltaChanges " + i5);
          localObject2 = localObject6;
          localObject3 = localObject5;
          i = k;
          paramContainerAtom = (Atom.ContainerAtom)localObject4;
          while ((paramTrack.editListDurations == null) || (paramGaplessInfoHolder.hasGaplessInfo()))
          {
            Util.scaleLargeTimestampsInPlace(localObject3, 1000000L, paramTrack.timescale);
            return new TrackSampleTable(paramContainerAtom, localObject1, i, localObject3, localObject2);
            label983: paramContainerAtom = new long[localChunkIterator.length];
            localObject1 = new int[localChunkIterator.length];
            while (localChunkIterator.moveNext())
            {
              paramContainerAtom[localChunkIterator.index] = localChunkIterator.offset;
              localObject1[localChunkIterator.index] = localChunkIterator.numSamples;
            }
            localObject2 = FixedSampleSizeRechunker.rechunk(((SampleSizeBox)localObject2).readNextSampleSize(), paramContainerAtom, localObject1, n);
            paramContainerAtom = ((FixedSampleSizeRechunker.Results)localObject2).offsets;
            localObject1 = ((FixedSampleSizeRechunker.Results)localObject2).sizes;
            i = ((FixedSampleSizeRechunker.Results)localObject2).maximumSize;
            localObject3 = ((FixedSampleSizeRechunker.Results)localObject2).timestamps;
            localObject2 = ((FixedSampleSizeRechunker.Results)localObject2).flags;
            l1 = 0L;
          }
          if ((paramTrack.editListDurations.length == 1) && (paramTrack.type == 1) && (localObject3.length >= 2))
          {
            l3 = paramTrack.editListMediaTimes[0];
            l2 = Util.scaleLargeTimestamp(paramTrack.editListDurations[0], paramTrack.timescale, paramTrack.movieTimescale) + l3;
            if ((localObject3[0] <= l3) && (l3 < localObject3[1]) && (localObject3[(localObject3.length - 1)] < l2) && (l2 <= l1))
            {
              l3 = Util.scaleLargeTimestamp(l3 - localObject3[0], paramTrack.format.sampleRate, paramTrack.timescale);
              l1 = Util.scaleLargeTimestamp(l1 - l2, paramTrack.format.sampleRate, paramTrack.timescale);
              if (((l3 != 0L) || (l1 != 0L)) && (l3 <= 2147483647L) && (l1 <= 2147483647L))
              {
                paramGaplessInfoHolder.encoderDelay = (int)l3;
                paramGaplessInfoHolder.encoderPadding = (int)l1;
                Util.scaleLargeTimestampsInPlace(localObject3, 1000000L, paramTrack.timescale);
                return new TrackSampleTable(paramContainerAtom, localObject1, i, localObject3, localObject2);
              }
            }
          }
          if ((paramTrack.editListDurations.length == 1) && (paramTrack.editListDurations[0] == 0L))
          {
            j = 0;
            while (j < localObject3.length)
            {
              localObject3[j] = Util.scaleLargeTimestamp(localObject3[j] - paramTrack.editListMediaTimes[0], 1000000L, paramTrack.timescale);
              j += 1;
            }
            return new TrackSampleTable(paramContainerAtom, localObject1, i, localObject3, localObject2);
          }
          int i1 = 0;
          k = 0;
          m = 0;
          j = 0;
          if (i1 < paramTrack.editListDurations.length)
          {
            l1 = paramTrack.editListMediaTimes[i1];
            if (l1 == -1L)
              break label1961;
            l2 = Util.scaleLargeTimestamp(paramTrack.editListDurations[i1], paramTrack.timescale, paramTrack.movieTimescale);
            i4 = Util.binarySearchCeil(localObject3, l1, true, true);
            i2 = Util.binarySearchCeil(localObject3, l2 + l1, true, false);
            i3 = j + (i2 - i4);
            if (m != i4)
            {
              j = 1;
              label1497: m = j | k;
              j = i3;
            }
          }
          for (k = i2; ; k = i2)
          {
            i1 += 1;
            i2 = k;
            k = m;
            m = i2;
            break;
            j = 0;
            break label1497;
            if (j != i14)
            {
              m = 1;
              i2 = k | m;
              if (i2 == 0)
                break label1834;
              paramGaplessInfoHolder = new long[j];
              label1566: if (i2 == 0)
                break label1839;
              localObject4 = new int[j];
              label1577: if (i2 == 0)
                break label1846;
              i = 0;
              label1584: if (i2 == 0)
                break label1849;
            }
            label1834: label1839: label1846: label1849: for (localObject5 = new int[j]; ; localObject5 = localObject2)
            {
              localObject6 = new long[j];
              k = 0;
              j = 0;
              l1 = 0L;
              if (k >= paramTrack.editListDurations.length)
                break label1872;
              l2 = paramTrack.editListMediaTimes[k];
              l3 = paramTrack.editListDurations[k];
              if (l2 == -1L)
                break label1958;
              l4 = Util.scaleLargeTimestamp(l3, paramTrack.timescale, paramTrack.movieTimescale);
              m = Util.binarySearchCeil(localObject3, l2, true, true);
              i3 = Util.binarySearchCeil(localObject3, l2 + l4, true, false);
              if (i2 != 0)
              {
                i1 = i3 - m;
                System.arraycopy(paramContainerAtom, m, paramGaplessInfoHolder, j, i1);
                System.arraycopy(localObject1, m, localObject4, j, i1);
                System.arraycopy(localObject2, m, localObject5, j, i1);
              }
              while (m < i3)
              {
                l4 = Util.scaleLargeTimestamp(l1, 1000000L, paramTrack.movieTimescale);
                localObject6[j] = (Util.scaleLargeTimestamp(localObject3[m] - l2, 1000000L, paramTrack.timescale) + l4);
                i1 = i;
                if (i2 != 0)
                {
                  i1 = i;
                  if (localObject4[j] > i)
                    i1 = localObject1[m];
                }
                j += 1;
                m += 1;
                i = i1;
              }
              m = 0;
              break;
              paramGaplessInfoHolder = paramContainerAtom;
              break label1566;
              localObject4 = localObject1;
              break label1577;
              break label1584;
            }
            label1958: 
            while (true)
            {
              k += 1;
              l1 = l3 + l1;
              break;
              label1872: k = 0;
              j = 0;
              if ((j < localObject5.length) && (k == 0))
              {
                if ((localObject5[j] & 0x1) != 0);
                for (m = 1; ; m = 0)
                {
                  k |= m;
                  j += 1;
                  break;
                }
              }
              if (k == 0)
                throw new ParserException("The edited sample sequence does not contain a sync sample.");
              return new TrackSampleTable(paramGaplessInfoHolder, localObject4, i, localObject6, localObject5);
            }
            label1961: i2 = m;
            m = k;
          }
          i = j;
          j = k;
        }
        label1986: m = i;
        i13 = i7;
      }
      label1996: i = -1;
      paramContainerAtom = (Atom.ContainerAtom)localObject1;
      j = 0;
    }
  }

  private static StsdData parseStsd(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, String paramString, DrmInitData paramDrmInitData, boolean paramBoolean)
  {
    paramParsableByteArray.setPosition(12);
    int j = paramParsableByteArray.readInt();
    StsdData localStsdData = new StsdData(j);
    int i = 0;
    if (i < j)
    {
      int k = paramParsableByteArray.getPosition();
      int m = paramParsableByteArray.readInt();
      boolean bool;
      label53: int n;
      if (m > 0)
      {
        bool = true;
        Assertions.checkArgument(bool, "childAtomSize should be positive");
        n = paramParsableByteArray.readInt();
        if ((n != Atom.TYPE_avc1) && (n != Atom.TYPE_avc3) && (n != Atom.TYPE_encv) && (n != Atom.TYPE_mp4v) && (n != Atom.TYPE_hvc1) && (n != Atom.TYPE_hev1) && (n != Atom.TYPE_s263) && (n != Atom.TYPE_vp08) && (n != Atom.TYPE_vp09))
          break label180;
        parseVideoSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramInt2, paramDrmInitData, localStsdData, i);
      }
      while (true)
      {
        paramParsableByteArray.setPosition(k + m);
        i += 1;
        break;
        bool = false;
        break label53;
        label180: if ((n == Atom.TYPE_mp4a) || (n == Atom.TYPE_enca) || (n == Atom.TYPE_ac_3) || (n == Atom.TYPE_ec_3) || (n == Atom.TYPE_dtsc) || (n == Atom.TYPE_dtse) || (n == Atom.TYPE_dtsh) || (n == Atom.TYPE_dtsl) || (n == Atom.TYPE_samr) || (n == Atom.TYPE_sawb) || (n == Atom.TYPE_lpcm) || (n == Atom.TYPE_sowt) || (n == Atom.TYPE__mp3))
        {
          parseAudioSampleEntry(paramParsableByteArray, n, k, m, paramInt1, paramString, paramBoolean, paramDrmInitData, localStsdData, i);
          continue;
        }
        if (n == Atom.TYPE_TTML)
        {
          localStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt1), "application/ttml+xml", null, -1, 0, paramString, paramDrmInitData);
          continue;
        }
        if (n == Atom.TYPE_tx3g)
        {
          localStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt1), "application/x-quicktime-tx3g", null, -1, 0, paramString, paramDrmInitData);
          continue;
        }
        if (n == Atom.TYPE_wvtt)
        {
          localStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt1), "application/x-mp4vtt", null, -1, 0, paramString, paramDrmInitData);
          continue;
        }
        if (n == Atom.TYPE_stpp)
        {
          localStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt1), "application/ttml+xml", null, -1, 0, paramString, paramDrmInitData, 0L);
          continue;
        }
        if (n == Atom.TYPE_c608)
        {
          localStsdData.format = Format.createTextSampleFormat(Integer.toString(paramInt1), "application/cea-608", null, -1, 0, paramString, paramDrmInitData);
          localStsdData.requiredSampleTransformation = 1;
          continue;
        }
        if (n != Atom.TYPE_camm)
          continue;
        localStsdData.format = Format.createSampleFormat(Integer.toString(paramInt1), "application/x-camera-motion", null, -1, paramDrmInitData);
      }
    }
    return localStsdData;
  }

  private static TkhdData parseTkhd(ParsableByteArray paramParsableByteArray)
  {
    int j = 8;
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    int i;
    int n;
    int m;
    label62: int k;
    long l1;
    if (i1 == 0)
    {
      i = 8;
      paramParsableByteArray.skipBytes(i);
      n = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      m = 1;
      int i2 = paramParsableByteArray.getPosition();
      i = j;
      if (i1 == 0)
        i = 4;
      j = 0;
      k = m;
      if (j < i)
      {
        if (paramParsableByteArray.data[(i2 + j)] == -1)
          break label177;
        k = 0;
      }
      if (k == 0)
        break label184;
      paramParsableByteArray.skipBytes(i);
      l1 = -9223372036854775807L;
      paramParsableByteArray.skipBytes(16);
      i = paramParsableByteArray.readInt();
      j = paramParsableByteArray.readInt();
      paramParsableByteArray.skipBytes(4);
      k = paramParsableByteArray.readInt();
      m = paramParsableByteArray.readInt();
      if ((i != 0) || (j != 65536) || (k != -65536) || (m != 0))
        break label223;
      i = 90;
    }
    while (true)
    {
      return new TkhdData(n, l1, i);
      i = 16;
      break;
      label177: j += 1;
      break label62;
      label184: long l2;
      if (i1 == 0)
        l2 = paramParsableByteArray.readUnsignedInt();
      while (true)
      {
        l1 = l2;
        if (l2 != 0L)
          break;
        l1 = -9223372036854775807L;
        break;
        l2 = paramParsableByteArray.readUnsignedLongToLong();
      }
      label223: if ((i == 0) && (j == -65536) && (k == 65536) && (m == 0))
      {
        i = 270;
        continue;
      }
      if ((i == -65536) && (j == 0) && (k == 0) && (m == -65536))
      {
        i = 180;
        continue;
      }
      i = 0;
    }
  }

  public static Track parseTrak(Atom.ContainerAtom paramContainerAtom, Atom.LeafAtom paramLeafAtom, long paramLong, DrmInitData paramDrmInitData, boolean paramBoolean)
  {
    Atom.ContainerAtom localContainerAtom1 = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mdia);
    int i = parseHdlr(localContainerAtom1.getLeafAtomOfType(Atom.TYPE_hdlr).data);
    if (i == -1)
      return null;
    TkhdData localTkhdData = parseTkhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tkhd).data);
    if (paramLong == -9223372036854775807L)
      paramLong = localTkhdData.duration;
    while (true)
    {
      long l = parseMvhd(paramLeafAtom.data);
      if (paramLong == -9223372036854775807L)
        paramLong = -9223372036854775807L;
      while (true)
      {
        Atom.ContainerAtom localContainerAtom2 = localContainerAtom1.getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl);
        paramLeafAtom = parseMdhd(localContainerAtom1.getLeafAtomOfType(Atom.TYPE_mdhd).data);
        paramDrmInitData = parseStsd(localContainerAtom2.getLeafAtomOfType(Atom.TYPE_stsd).data, localTkhdData.id, localTkhdData.rotationDegrees, (String)paramLeafAtom.second, paramDrmInitData, paramBoolean);
        paramContainerAtom = parseEdts(paramContainerAtom.getContainerAtomOfType(Atom.TYPE_edts));
        if (paramDrmInitData.format != null)
          break;
        return null;
        paramLong = Util.scaleLargeTimestamp(paramLong, 1000000L, l);
      }
      return new Track(localTkhdData.id, i, ((Long)paramLeafAtom.first).longValue(), l, paramLong, paramDrmInitData.format, paramDrmInitData.requiredSampleTransformation, paramDrmInitData.trackEncryptionBoxes, paramDrmInitData.nalUnitLengthFieldLength, (long[])paramContainerAtom.first, (long[])paramContainerAtom.second);
    }
  }

  public static Metadata parseUdta(Atom.LeafAtom paramLeafAtom, boolean paramBoolean)
  {
    if (paramBoolean);
    while (true)
    {
      return null;
      paramLeafAtom = paramLeafAtom.data;
      paramLeafAtom.setPosition(8);
      while (paramLeafAtom.bytesLeft() >= 8)
      {
        int i = paramLeafAtom.getPosition();
        int j = paramLeafAtom.readInt();
        if (paramLeafAtom.readInt() == Atom.TYPE_meta)
        {
          paramLeafAtom.setPosition(i);
          return parseMetaAtom(paramLeafAtom, i + j);
        }
        paramLeafAtom.skipBytes(j - 8);
      }
    }
  }

  private static void parseVideoSampleEntry(ParsableByteArray paramParsableByteArray, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, DrmInitData paramDrmInitData, StsdData paramStsdData, int paramInt6)
  {
    paramParsableByteArray.setPosition(paramInt2 + 8);
    paramParsableByteArray.skipBytes(24);
    int m = paramParsableByteArray.readUnsignedShort();
    int n = paramParsableByteArray.readUnsignedShort();
    int k = 0;
    float f = 1.0F;
    paramParsableByteArray.skipBytes(50);
    int j = paramParsableByteArray.getPosition();
    int i = paramInt1;
    if (paramInt1 == Atom.TYPE_encv)
    {
      i = parseSampleEntryEncryptionData(paramParsableByteArray, paramInt2, paramInt3, paramStsdData, paramInt6);
      paramParsableByteArray.setPosition(j);
    }
    Object localObject1 = null;
    String str = null;
    byte[] arrayOfByte = null;
    paramInt6 = -1;
    paramInt1 = k;
    int i1;
    if (j - paramInt2 < paramInt3)
    {
      paramParsableByteArray.setPosition(j);
      i1 = paramParsableByteArray.getPosition();
      k = paramParsableByteArray.readInt();
      if ((k != 0) || (paramParsableByteArray.getPosition() - paramInt2 != paramInt3));
    }
    else
    {
      if (str != null)
        break label573;
      return;
    }
    boolean bool;
    label142: int i2;
    label171: Object localObject2;
    if (k > 0)
    {
      bool = true;
      Assertions.checkArgument(bool, "childAtomSize should be positive");
      i2 = paramParsableByteArray.readInt();
      if (i2 != Atom.TYPE_avcC)
        break label246;
      if (str != null)
        break label240;
      bool = true;
      Assertions.checkState(bool);
      str = "video/avc";
      paramParsableByteArray.setPosition(i1 + 8);
      localObject2 = AvcConfig.parse(paramParsableByteArray);
      localObject1 = ((AvcConfig)localObject2).initializationData;
      paramStsdData.nalUnitLengthFieldLength = ((AvcConfig)localObject2).nalUnitLengthFieldLength;
      if (paramInt1 == 0)
        f = ((AvcConfig)localObject2).pixelWidthAspectRatio;
    }
    while (true)
    {
      j += k;
      break;
      bool = false;
      break label142;
      label240: bool = false;
      break label171;
      label246: if (i2 == Atom.TYPE_hvcC)
      {
        if (str == null);
        for (bool = true; ; bool = false)
        {
          Assertions.checkState(bool);
          str = "video/hevc";
          paramParsableByteArray.setPosition(i1 + 8);
          localObject2 = HevcConfig.parse(paramParsableByteArray);
          localObject1 = ((HevcConfig)localObject2).initializationData;
          paramStsdData.nalUnitLengthFieldLength = ((HevcConfig)localObject2).nalUnitLengthFieldLength;
          break;
        }
      }
      if (i2 == Atom.TYPE_vpcC)
      {
        if (str == null)
        {
          bool = true;
          label329: Assertions.checkState(bool);
          if (i != Atom.TYPE_vp08)
            break label356;
        }
        label356: for (str = "video/x-vnd.on2.vp8"; ; str = "video/x-vnd.on2.vp9")
        {
          break;
          bool = false;
          break label329;
        }
      }
      if (i2 == Atom.TYPE_d263)
      {
        if (str == null);
        for (bool = true; ; bool = false)
        {
          Assertions.checkState(bool);
          str = "video/3gpp";
          break;
        }
      }
      if (i2 == Atom.TYPE_esds)
      {
        if (str == null);
        for (bool = true; ; bool = false)
        {
          Assertions.checkState(bool);
          localObject1 = parseEsdsFromParent(paramParsableByteArray, i1);
          str = (String)((Pair)localObject1).first;
          localObject1 = Collections.singletonList(((Pair)localObject1).second);
          break;
        }
      }
      if (i2 == Atom.TYPE_pasp)
      {
        f = parsePaspFromParent(paramParsableByteArray, i1);
        paramInt1 = 1;
        continue;
      }
      if (i2 == Atom.TYPE_sv3d)
      {
        arrayOfByte = parseProjFromParent(paramParsableByteArray, i1, k);
        continue;
      }
      if (i2 == Atom.TYPE_st3d)
      {
        i1 = paramParsableByteArray.readUnsignedByte();
        paramParsableByteArray.skipBytes(3);
        if (i1 != 0);
      }
      switch (paramParsableByteArray.readUnsignedByte())
      {
      default:
        break;
      case 0:
        paramInt6 = 0;
        break;
      case 1:
        paramInt6 = 1;
        break;
      case 2:
        paramInt6 = 2;
      }
    }
    label573: paramStsdData.format = Format.createVideoSampleFormat(Integer.toString(paramInt4), str, null, -1, -1, m, n, -1.0F, (List)localObject1, paramInt5, f, arrayOfByte, paramInt6, paramDrmInitData);
  }

  private static final class ChunkIterator
  {
    private final ParsableByteArray chunkOffsets;
    private final boolean chunkOffsetsAreLongs;
    public int index;
    public final int length;
    private int nextSamplesPerChunkChangeIndex;
    public int numSamples;
    public long offset;
    private int remainingSamplesPerChunkChanges;
    private final ParsableByteArray stsc;

    public ChunkIterator(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, boolean paramBoolean)
    {
      this.stsc = paramParsableByteArray1;
      this.chunkOffsets = paramParsableByteArray2;
      this.chunkOffsetsAreLongs = paramBoolean;
      paramParsableByteArray2.setPosition(12);
      this.length = paramParsableByteArray2.readUnsignedIntToInt();
      paramParsableByteArray1.setPosition(12);
      this.remainingSamplesPerChunkChanges = paramParsableByteArray1.readUnsignedIntToInt();
      if (paramParsableByteArray1.readInt() == 1);
      for (paramBoolean = bool; ; paramBoolean = false)
      {
        Assertions.checkState(paramBoolean, "first_chunk must be 1");
        this.index = -1;
        return;
      }
    }

    public boolean moveNext()
    {
      int i = this.index + 1;
      this.index = i;
      if (i == this.length)
        return false;
      long l;
      if (this.chunkOffsetsAreLongs)
      {
        l = this.chunkOffsets.readUnsignedLongToLong();
        this.offset = l;
        if (this.index == this.nextSamplesPerChunkChangeIndex)
        {
          this.numSamples = this.stsc.readUnsignedIntToInt();
          this.stsc.skipBytes(4);
          i = this.remainingSamplesPerChunkChanges - 1;
          this.remainingSamplesPerChunkChanges = i;
          if (i <= 0)
            break label116;
        }
      }
      label116: for (i = this.stsc.readUnsignedIntToInt() - 1; ; i = -1)
      {
        this.nextSamplesPerChunkChangeIndex = i;
        return true;
        l = this.chunkOffsets.readUnsignedInt();
        break;
      }
    }
  }

  private static abstract interface SampleSizeBox
  {
    public abstract int getSampleCount();

    public abstract boolean isFixedSampleSize();

    public abstract int readNextSampleSize();
  }

  private static final class StsdData
  {
    public Format format;
    public int nalUnitLengthFieldLength;
    public int requiredSampleTransformation;
    public final TrackEncryptionBox[] trackEncryptionBoxes;

    public StsdData(int paramInt)
    {
      this.trackEncryptionBoxes = new TrackEncryptionBox[paramInt];
      this.requiredSampleTransformation = 0;
    }
  }

  static final class StszSampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private final ParsableByteArray data;
    private final int fixedSampleSize;
    private final int sampleCount;

    public StszSampleSizeBox(Atom.LeafAtom paramLeafAtom)
    {
      this.data = paramLeafAtom.data;
      this.data.setPosition(12);
      this.fixedSampleSize = this.data.readUnsignedIntToInt();
      this.sampleCount = this.data.readUnsignedIntToInt();
    }

    public int getSampleCount()
    {
      return this.sampleCount;
    }

    public boolean isFixedSampleSize()
    {
      return this.fixedSampleSize != 0;
    }

    public int readNextSampleSize()
    {
      if (this.fixedSampleSize == 0)
        return this.data.readUnsignedIntToInt();
      return this.fixedSampleSize;
    }
  }

  static final class Stz2SampleSizeBox
    implements AtomParsers.SampleSizeBox
  {
    private int currentByte;
    private final ParsableByteArray data;
    private final int fieldSize;
    private final int sampleCount;
    private int sampleIndex;

    public Stz2SampleSizeBox(Atom.LeafAtom paramLeafAtom)
    {
      this.data = paramLeafAtom.data;
      this.data.setPosition(12);
      this.fieldSize = (this.data.readUnsignedIntToInt() & 0xFF);
      this.sampleCount = this.data.readUnsignedIntToInt();
    }

    public int getSampleCount()
    {
      return this.sampleCount;
    }

    public boolean isFixedSampleSize()
    {
      return false;
    }

    public int readNextSampleSize()
    {
      if (this.fieldSize == 8)
        return this.data.readUnsignedByte();
      if (this.fieldSize == 16)
        return this.data.readUnsignedShort();
      int i = this.sampleIndex;
      this.sampleIndex = (i + 1);
      if (i % 2 == 0)
      {
        this.currentByte = this.data.readUnsignedByte();
        return (this.currentByte & 0xF0) >> 4;
      }
      return this.currentByte & 0xF;
    }
  }

  private static final class TkhdData
  {
    private final long duration;
    private final int id;
    private final int rotationDegrees;

    public TkhdData(int paramInt1, long paramLong, int paramInt2)
    {
      this.id = paramInt1;
      this.duration = paramLong;
      this.rotationDegrees = paramInt2;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp4.AtomParsers
 * JD-Core Version:    0.6.0
 */