package org.vidogram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData;
import org.vidogram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.vidogram.messenger.exoplayer2.extractor.ChunkIndex;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.NalUnitUtil;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class FragmentedMp4Extractor
  implements Extractor
{
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new FragmentedMp4Extractor() };
    }
  };
  private static final int FLAG_SIDELOADED = 4;
  public static final int FLAG_WORKAROUND_EVERY_VIDEO_FRAME_IS_SYNC_FRAME = 1;
  public static final int FLAG_WORKAROUND_IGNORE_TFDT_BOX = 2;
  private static final byte[] PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE;
  private static final int SAMPLE_GROUP_TYPE_seig = Util.getIntegerCodeForString("seig");
  private static final int STATE_READING_ATOM_HEADER = 0;
  private static final int STATE_READING_ATOM_PAYLOAD = 1;
  private static final int STATE_READING_ENCRYPTION_DATA = 2;
  private static final int STATE_READING_SAMPLE_CONTINUE = 4;
  private static final int STATE_READING_SAMPLE_START = 3;
  private static final String TAG = "FragmentedMp4Extractor";
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader;
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final Stack<Atom.ContainerAtom> containerAtoms;
  private TrackBundle currentTrackBundle;
  private long durationUs;
  private final ParsableByteArray encryptionSignalByte;
  private long endOfMdatPosition;
  private final byte[] extendedTypeScratch;
  private ExtractorOutput extractorOutput;
  private final int flags;
  private boolean haveOutputSeekMap;
  private final ParsableByteArray nalLength;
  private final ParsableByteArray nalStartCode;
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private int sampleSize;
  private final Track sideloadedTrack;
  private final TimestampAdjuster timestampAdjuster;
  private final SparseArray<TrackBundle> trackBundles;

  static
  {
    PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE = new byte[] { -94, 57, 79, 82, 90, -101, 79, 20, -94, 68, 108, 66, 124, 100, -115, -12 };
  }

  public FragmentedMp4Extractor()
  {
    this(0, null);
  }

  public FragmentedMp4Extractor(int paramInt, TimestampAdjuster paramTimestampAdjuster)
  {
    this(paramInt, null, paramTimestampAdjuster);
  }

  public FragmentedMp4Extractor(int paramInt, Track paramTrack, TimestampAdjuster paramTimestampAdjuster)
  {
    this.sideloadedTrack = paramTrack;
    if (paramTrack != null);
    for (int i = 4; ; i = 0)
    {
      this.flags = (i | paramInt);
      this.timestampAdjuster = paramTimestampAdjuster;
      this.atomHeader = new ParsableByteArray(16);
      this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
      this.nalLength = new ParsableByteArray(4);
      this.encryptionSignalByte = new ParsableByteArray(1);
      this.extendedTypeScratch = new byte[16];
      this.containerAtoms = new Stack();
      this.trackBundles = new SparseArray();
      this.durationUs = -9223372036854775807L;
      enterReadingAtomHeaderState();
      return;
    }
  }

  private int appendSampleEncryptionData(TrackBundle paramTrackBundle)
  {
    TrackFragment localTrackFragment = paramTrackBundle.fragment;
    ParsableByteArray localParsableByteArray = localTrackFragment.sampleEncryptionData;
    int i = localTrackFragment.header.sampleDescriptionIndex;
    Object localObject;
    int j;
    int k;
    if (localTrackFragment.trackEncryptionBox != null)
    {
      localObject = localTrackFragment.trackEncryptionBox;
      j = ((TrackEncryptionBox)localObject).initializationVectorSize;
      k = localTrackFragment.sampleHasSubsampleEncryptionTable[paramTrackBundle.currentSampleIndex];
      localObject = this.encryptionSignalByte.data;
      if (k == 0)
        break label137;
    }
    label137: for (i = 128; ; i = 0)
    {
      localObject[0] = (byte)(i | j);
      this.encryptionSignalByte.setPosition(0);
      paramTrackBundle = paramTrackBundle.output;
      paramTrackBundle.sampleData(this.encryptionSignalByte, 1);
      paramTrackBundle.sampleData(localParsableByteArray, j);
      if (k != 0)
        break label142;
      return j + 1;
      localObject = paramTrackBundle.track.sampleDescriptionEncryptionBoxes[i];
      break;
    }
    label142: i = localParsableByteArray.readUnsignedShort();
    localParsableByteArray.skipBytes(-2);
    i = i * 6 + 2;
    paramTrackBundle.sampleData(localParsableByteArray, i);
    return j + 1 + i;
  }

  private void enterReadingAtomHeaderState()
  {
    this.parserState = 0;
    this.atomHeaderBytesRead = 0;
  }

  private static DrmInitData getDrmInitDataFromAtoms(List<Atom.LeafAtom> paramList)
  {
    int j = paramList.size();
    int i = 0;
    Object localObject2 = null;
    if (i < j)
    {
      Object localObject3 = (Atom.LeafAtom)paramList.get(i);
      Object localObject1 = localObject2;
      if (((Atom.LeafAtom)localObject3).type == Atom.TYPE_pssh)
      {
        localObject1 = localObject2;
        if (localObject2 == null)
          localObject1 = new ArrayList();
        localObject2 = ((Atom.LeafAtom)localObject3).data.data;
        localObject3 = PsshAtomUtil.parseUuid(localObject2);
        if (localObject3 != null)
          break label100;
        Log.w("FragmentedMp4Extractor", "Skipped pssh atom (failed to extract uuid)");
      }
      while (true)
      {
        i += 1;
        localObject2 = localObject1;
        break;
        label100: ((ArrayList)localObject1).add(new DrmInitData.SchemeData((UUID)localObject3, "video/mp4", localObject2));
      }
    }
    if (localObject2 == null)
      return null;
    return (DrmInitData)(DrmInitData)(DrmInitData)new DrmInitData((List)localObject2);
  }

  private static TrackBundle getNextFragmentRun(SparseArray<TrackBundle> paramSparseArray)
  {
    Object localObject = null;
    long l1 = 9223372036854775807L;
    int j = paramSparseArray.size();
    int i = 0;
    TrackBundle localTrackBundle;
    if (i < j)
    {
      localTrackBundle = (TrackBundle)paramSparseArray.valueAt(i);
      if (localTrackBundle.currentTrackRunIndex != localTrackBundle.fragment.trunCount);
    }
    while (true)
    {
      i += 1;
      break;
      long l2 = localTrackBundle.fragment.trunDataPosition[localTrackBundle.currentTrackRunIndex];
      if (l2 < l1)
      {
        localObject = localTrackBundle;
        l1 = l2;
        continue;
        return localObject;
      }
    }
  }

  private void onContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
  {
    if (paramContainerAtom.type == Atom.TYPE_moov)
      onMoovContainerAtomRead(paramContainerAtom);
    do
    {
      return;
      if (paramContainerAtom.type != Atom.TYPE_moof)
        continue;
      onMoofContainerAtomRead(paramContainerAtom);
      return;
    }
    while (this.containerAtoms.isEmpty());
    ((Atom.ContainerAtom)this.containerAtoms.peek()).add(paramContainerAtom);
  }

  private void onLeafAtomRead(Atom.LeafAtom paramLeafAtom, long paramLong)
  {
    if (!this.containerAtoms.isEmpty())
      ((Atom.ContainerAtom)this.containerAtoms.peek()).add(paramLeafAtom);
    do
      return;
    while (paramLeafAtom.type != Atom.TYPE_sidx);
    paramLeafAtom = parseSidx(paramLeafAtom.data, paramLong);
    this.extractorOutput.seekMap(paramLeafAtom);
    this.haveOutputSeekMap = true;
  }

  private void onMoofContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
  {
    parseMoof(paramContainerAtom, this.trackBundles, this.flags, this.extendedTypeScratch);
    paramContainerAtom = getDrmInitDataFromAtoms(paramContainerAtom.leafChildren);
    if (paramContainerAtom != null)
    {
      int j = this.trackBundles.size();
      int i = 0;
      while (i < j)
      {
        ((TrackBundle)this.trackBundles.valueAt(i)).updateDrmInitData(paramContainerAtom);
        i += 1;
      }
    }
  }

  private void onMoovContainerAtomRead(Atom.ContainerAtom paramContainerAtom)
  {
    boolean bool2 = true;
    int j = 0;
    DrmInitData localDrmInitData;
    SparseArray localSparseArray;
    long l;
    label69: Object localObject2;
    if (this.sideloadedTrack == null)
    {
      bool1 = true;
      Assertions.checkState(bool1, "Unexpected moov box.");
      localDrmInitData = getDrmInitDataFromAtoms(paramContainerAtom.leafChildren);
      localObject1 = paramContainerAtom.getContainerAtomOfType(Atom.TYPE_mvex);
      localSparseArray = new SparseArray();
      l = -9223372036854775807L;
      k = ((Atom.ContainerAtom)localObject1).leafChildren.size();
      i = 0;
      if (i >= k)
        break label170;
      localObject2 = (Atom.LeafAtom)((Atom.ContainerAtom)localObject1).leafChildren.get(i);
      if (((Atom.LeafAtom)localObject2).type != Atom.TYPE_trex)
        break label146;
      localObject2 = parseTrex(((Atom.LeafAtom)localObject2).data);
      localSparseArray.put(((Integer)((Pair)localObject2).first).intValue(), ((Pair)localObject2).second);
    }
    while (true)
    {
      i += 1;
      break label69;
      bool1 = false;
      break;
      label146: if (((Atom.LeafAtom)localObject2).type != Atom.TYPE_mehd)
        continue;
      l = parseMehd(((Atom.LeafAtom)localObject2).data);
    }
    label170: Object localObject1 = new SparseArray();
    int k = paramContainerAtom.containerChildren.size();
    int i = 0;
    while (i < k)
    {
      localObject2 = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
      if (((Atom.ContainerAtom)localObject2).type == Atom.TYPE_trak)
      {
        localObject2 = AtomParsers.parseTrak((Atom.ContainerAtom)localObject2, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), l, localDrmInitData, false);
        if (localObject2 != null)
          ((SparseArray)localObject1).put(((Track)localObject2).id, localObject2);
      }
      i += 1;
    }
    k = ((SparseArray)localObject1).size();
    if (this.trackBundles.size() == 0)
    {
      i = 0;
      while (i < k)
      {
        paramContainerAtom = (Track)((SparseArray)localObject1).valueAt(i);
        this.trackBundles.put(paramContainerAtom.id, new TrackBundle(this.extractorOutput.track(i)));
        this.durationUs = Math.max(this.durationUs, paramContainerAtom.durationUs);
        i += 1;
      }
      this.extractorOutput.endTracks();
      i = j;
      while (i < k)
      {
        paramContainerAtom = (Track)((SparseArray)localObject1).valueAt(i);
        ((TrackBundle)this.trackBundles.get(paramContainerAtom.id)).init(paramContainerAtom, (DefaultSampleValues)localSparseArray.get(paramContainerAtom.id));
        i += 1;
      }
    }
    if (this.trackBundles.size() == k);
    for (boolean bool1 = bool2; ; bool1 = false)
    {
      Assertions.checkState(bool1);
      i = j;
      break;
    }
  }

  private static long parseMehd(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 0)
      return paramParsableByteArray.readUnsignedInt();
    return paramParsableByteArray.readUnsignedLongToLong();
  }

  private static void parseMoof(Atom.ContainerAtom paramContainerAtom, SparseArray<TrackBundle> paramSparseArray, int paramInt, byte[] paramArrayOfByte)
  {
    int j = paramContainerAtom.containerChildren.size();
    int i = 0;
    while (i < j)
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
      if (localContainerAtom.type == Atom.TYPE_traf)
        parseTraf(localContainerAtom, paramSparseArray, paramInt, paramArrayOfByte);
      i += 1;
    }
  }

  private static void parseSaio(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
  {
    paramParsableByteArray.setPosition(8);
    int i = paramParsableByteArray.readInt();
    if ((Atom.parseFullAtomFlags(i) & 0x1) == 1)
      paramParsableByteArray.skipBytes(8);
    int j = paramParsableByteArray.readUnsignedIntToInt();
    if (j != 1)
      throw new ParserException("Unexpected saio entry count: " + j);
    i = Atom.parseFullAtomVersion(i);
    long l2 = paramTrackFragment.auxiliaryDataPosition;
    long l1;
    if (i == 0)
      l1 = paramParsableByteArray.readUnsignedInt();
    while (true)
    {
      paramTrackFragment.auxiliaryDataPosition = (l1 + l2);
      return;
      l1 = paramParsableByteArray.readUnsignedLongToLong();
    }
  }

  private static void parseSaiz(TrackEncryptionBox paramTrackEncryptionBox, ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
  {
    boolean bool = true;
    int n = paramTrackEncryptionBox.initializationVectorSize;
    paramParsableByteArray.setPosition(8);
    if ((Atom.parseFullAtomFlags(paramParsableByteArray.readInt()) & 0x1) == 1)
      paramParsableByteArray.skipBytes(8);
    int i = paramParsableByteArray.readUnsignedByte();
    int m = paramParsableByteArray.readUnsignedIntToInt();
    if (m != paramTrackFragment.sampleCount)
      throw new ParserException("Length mismatch: " + m + ", " + paramTrackFragment.sampleCount);
    int k;
    if (i == 0)
    {
      paramTrackEncryptionBox = paramTrackFragment.sampleHasSubsampleEncryptionTable;
      int j = 0;
      i = 0;
      k = i;
      if (j < m)
      {
        k = paramParsableByteArray.readUnsignedByte();
        if (k > n);
        for (bool = true; ; bool = false)
        {
          paramTrackEncryptionBox[j] = bool;
          j += 1;
          i += k;
          break;
        }
      }
    }
    else
    {
      if (i <= n)
        break label195;
    }
    while (true)
    {
      k = i * m + 0;
      Arrays.fill(paramTrackFragment.sampleHasSubsampleEncryptionTable, 0, m, bool);
      paramTrackFragment.initEncryptionData(k);
      return;
      label195: bool = false;
    }
  }

  private static void parseSenc(ParsableByteArray paramParsableByteArray, int paramInt, TrackFragment paramTrackFragment)
  {
    paramParsableByteArray.setPosition(paramInt + 8);
    paramInt = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    if ((paramInt & 0x1) != 0)
      throw new ParserException("Overriding TrackEncryptionBox parameters is unsupported.");
    if ((paramInt & 0x2) != 0);
    for (boolean bool = true; ; bool = false)
    {
      paramInt = paramParsableByteArray.readUnsignedIntToInt();
      if (paramInt == paramTrackFragment.sampleCount)
        break;
      throw new ParserException("Length mismatch: " + paramInt + ", " + paramTrackFragment.sampleCount);
    }
    Arrays.fill(paramTrackFragment.sampleHasSubsampleEncryptionTable, 0, paramInt, bool);
    paramTrackFragment.initEncryptionData(paramParsableByteArray.bytesLeft());
    paramTrackFragment.fillEncryptionData(paramParsableByteArray);
  }

  private static void parseSenc(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment)
  {
    parseSenc(paramParsableByteArray, 0, paramTrackFragment);
  }

  private static void parseSgpd(ParsableByteArray paramParsableByteArray1, ParsableByteArray paramParsableByteArray2, TrackFragment paramTrackFragment)
  {
    paramParsableByteArray1.setPosition(8);
    int i = paramParsableByteArray1.readInt();
    if (paramParsableByteArray1.readInt() != SAMPLE_GROUP_TYPE_seig);
    while (true)
    {
      return;
      if (Atom.parseFullAtomVersion(i) == 1)
        paramParsableByteArray1.skipBytes(4);
      if (paramParsableByteArray1.readInt() != 1)
        throw new ParserException("Entry count in sbgp != 1 (unsupported).");
      paramParsableByteArray2.setPosition(8);
      i = paramParsableByteArray2.readInt();
      if (paramParsableByteArray2.readInt() != SAMPLE_GROUP_TYPE_seig)
        continue;
      i = Atom.parseFullAtomVersion(i);
      if (i == 1)
      {
        if (paramParsableByteArray2.readUnsignedInt() == 0L)
          throw new ParserException("Variable length decription in sgpd found (unsupported)");
      }
      else if (i >= 2)
        paramParsableByteArray2.skipBytes(4);
      if (paramParsableByteArray2.readUnsignedInt() != 1L)
        throw new ParserException("Entry count in sgpd != 1 (unsupported).");
      paramParsableByteArray2.skipBytes(2);
      if (paramParsableByteArray2.readUnsignedByte() == 1);
      for (boolean bool = true; bool; bool = false)
      {
        i = paramParsableByteArray2.readUnsignedByte();
        paramParsableByteArray1 = new byte[16];
        paramParsableByteArray2.readBytes(paramParsableByteArray1, 0, paramParsableByteArray1.length);
        paramTrackFragment.definesEncryptionData = true;
        paramTrackFragment.trackEncryptionBox = new TrackEncryptionBox(bool, i, paramParsableByteArray1);
        return;
      }
    }
  }

  private static ChunkIndex parseSidx(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    paramParsableByteArray.setPosition(8);
    int i = Atom.parseFullAtomVersion(paramParsableByteArray.readInt());
    paramParsableByteArray.skipBytes(4);
    long l4 = paramParsableByteArray.readUnsignedInt();
    long l1;
    long l2;
    int j;
    int[] arrayOfInt;
    long[] arrayOfLong1;
    long[] arrayOfLong2;
    long[] arrayOfLong3;
    long l3;
    if (i == 0)
    {
      l1 = paramParsableByteArray.readUnsignedInt();
      l2 = paramParsableByteArray.readUnsignedInt() + paramLong;
      paramLong = l1;
      l1 = l2;
      paramParsableByteArray.skipBytes(2);
      j = paramParsableByteArray.readUnsignedShort();
      arrayOfInt = new int[j];
      arrayOfLong1 = new long[j];
      arrayOfLong2 = new long[j];
      arrayOfLong3 = new long[j];
      l3 = Util.scaleLargeTimestamp(paramLong, 1000000L, l4);
      i = 0;
      l2 = paramLong;
      paramLong = l1;
      l1 = l3;
    }
    while (true)
    {
      if (i >= j)
        break label237;
      int k = paramParsableByteArray.readInt();
      if ((0x80000000 & k) != 0)
      {
        throw new ParserException("Unhandled indirect reference");
        l2 = paramParsableByteArray.readUnsignedLongToLong();
        l1 = paramParsableByteArray.readUnsignedLongToLong() + paramLong;
        paramLong = l2;
        break;
      }
      l3 = paramParsableByteArray.readUnsignedInt();
      arrayOfInt[i] = (k & 0x7FFFFFFF);
      arrayOfLong1[i] = paramLong;
      arrayOfLong3[i] = l1;
      l2 += l3;
      l1 = Util.scaleLargeTimestamp(l2, 1000000L, l4);
      arrayOfLong2[i] = (l1 - arrayOfLong3[i]);
      paramParsableByteArray.skipBytes(4);
      paramLong += arrayOfInt[i];
      i += 1;
    }
    label237: return new ChunkIndex(arrayOfInt, arrayOfLong1, arrayOfLong2, arrayOfLong3);
  }

  private static long parseTfdt(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (Atom.parseFullAtomVersion(paramParsableByteArray.readInt()) == 1)
      return paramParsableByteArray.readUnsignedLongToLong();
    return paramParsableByteArray.readUnsignedInt();
  }

  private static TrackBundle parseTfhd(ParsableByteArray paramParsableByteArray, SparseArray<TrackBundle> paramSparseArray, int paramInt)
  {
    paramParsableByteArray.setPosition(8);
    int k = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    int i = paramParsableByteArray.readInt();
    if ((paramInt & 0x4) == 0);
    for (paramInt = i; ; paramInt = 0)
    {
      paramSparseArray = (TrackBundle)paramSparseArray.get(paramInt);
      if (paramSparseArray != null)
        break;
      return null;
    }
    if ((k & 0x1) != 0)
    {
      long l = paramParsableByteArray.readUnsignedLongToLong();
      paramSparseArray.fragment.dataPosition = l;
      paramSparseArray.fragment.auxiliaryDataPosition = l;
    }
    DefaultSampleValues localDefaultSampleValues = paramSparseArray.defaultSampleValues;
    label112: int j;
    if ((k & 0x2) != 0)
    {
      paramInt = paramParsableByteArray.readUnsignedIntToInt() - 1;
      if ((k & 0x8) == 0)
        break label171;
      i = paramParsableByteArray.readUnsignedIntToInt();
      if ((k & 0x10) == 0)
        break label180;
      j = paramParsableByteArray.readUnsignedIntToInt();
      label126: if ((k & 0x20) == 0)
        break label190;
    }
    label171: label180: label190: for (k = paramParsableByteArray.readUnsignedIntToInt(); ; k = localDefaultSampleValues.flags)
    {
      paramSparseArray.fragment.header = new DefaultSampleValues(paramInt, i, j, k);
      return paramSparseArray;
      paramInt = localDefaultSampleValues.sampleDescriptionIndex;
      break;
      i = localDefaultSampleValues.duration;
      break label112;
      j = localDefaultSampleValues.size;
      break label126;
    }
  }

  private static void parseTraf(Atom.ContainerAtom paramContainerAtom, SparseArray<TrackBundle> paramSparseArray, int paramInt, byte[] paramArrayOfByte)
  {
    Object localObject = parseTfhd(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfhd).data, paramSparseArray, paramInt);
    if (localObject == null);
    while (true)
    {
      return;
      paramSparseArray = ((TrackBundle)localObject).fragment;
      long l2 = paramSparseArray.nextFragmentDecodeTime;
      ((TrackBundle)localObject).reset();
      long l1 = l2;
      if (paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfdt) != null)
      {
        l1 = l2;
        if ((paramInt & 0x2) == 0)
          l1 = parseTfdt(paramContainerAtom.getLeafAtomOfType(Atom.TYPE_tfdt).data);
      }
      parseTruns(paramContainerAtom, (TrackBundle)localObject, l1, paramInt);
      Atom.LeafAtom localLeafAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saiz);
      if (localLeafAtom != null)
        parseSaiz(localObject.track.sampleDescriptionEncryptionBoxes[paramSparseArray.header.sampleDescriptionIndex], localLeafAtom.data, paramSparseArray);
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_saio);
      if (localObject != null)
        parseSaio(((Atom.LeafAtom)localObject).data, paramSparseArray);
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_senc);
      if (localObject != null)
        parseSenc(((Atom.LeafAtom)localObject).data, paramSparseArray);
      localObject = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sbgp);
      localLeafAtom = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_sgpd);
      if ((localObject != null) && (localLeafAtom != null))
        parseSgpd(((Atom.LeafAtom)localObject).data, localLeafAtom.data, paramSparseArray);
      int i = paramContainerAtom.leafChildren.size();
      paramInt = 0;
      while (paramInt < i)
      {
        localObject = (Atom.LeafAtom)paramContainerAtom.leafChildren.get(paramInt);
        if (((Atom.LeafAtom)localObject).type == Atom.TYPE_uuid)
          parseUuid(((Atom.LeafAtom)localObject).data, paramSparseArray, paramArrayOfByte);
        paramInt += 1;
      }
    }
  }

  private static Pair<Integer, DefaultSampleValues> parseTrex(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(12);
    return Pair.create(Integer.valueOf(paramParsableByteArray.readInt()), new DefaultSampleValues(paramParsableByteArray.readUnsignedIntToInt() - 1, paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readUnsignedIntToInt(), paramParsableByteArray.readInt()));
  }

  private static int parseTrun(TrackBundle paramTrackBundle, int paramInt1, long paramLong, int paramInt2, ParsableByteArray paramParsableByteArray, int paramInt3)
  {
    paramParsableByteArray.setPosition(8);
    int i1 = Atom.parseFullAtomFlags(paramParsableByteArray.readInt());
    Track localTrack = paramTrackBundle.track;
    paramTrackBundle = paramTrackBundle.fragment;
    DefaultSampleValues localDefaultSampleValues = paramTrackBundle.header;
    paramTrackBundle.trunLength[paramInt1] = paramParsableByteArray.readUnsignedIntToInt();
    paramTrackBundle.trunDataPosition[paramInt1] = paramTrackBundle.dataPosition;
    Object localObject;
    if ((i1 & 0x1) != 0)
    {
      localObject = paramTrackBundle.trunDataPosition;
      localObject[paramInt1] += paramParsableByteArray.readInt();
    }
    int j;
    int i;
    int k;
    label124: int m;
    label136: int n;
    label148: label160: long l1;
    if ((i1 & 0x4) != 0)
    {
      j = 1;
      i = localDefaultSampleValues.flags;
      if (j != 0)
        i = paramParsableByteArray.readUnsignedIntToInt();
      if ((i1 & 0x100) == 0)
        break label422;
      k = 1;
      if ((i1 & 0x200) == 0)
        break label428;
      m = 1;
      if ((i1 & 0x400) == 0)
        break label434;
      n = 1;
      if ((i1 & 0x800) == 0)
        break label440;
      i1 = 1;
      if ((localTrack.editListDurations == null) || (localTrack.editListDurations.length != 1) || (localTrack.editListDurations[0] != 0L))
        break label518;
      l1 = Util.scaleLargeTimestamp(localTrack.editListMediaTimes[0], 1000L, localTrack.timescale);
    }
    while (true)
    {
      localObject = paramTrackBundle.sampleSizeTable;
      int[] arrayOfInt = paramTrackBundle.sampleCompositionTimeOffsetTable;
      long[] arrayOfLong = paramTrackBundle.sampleDecodingTimeTable;
      boolean[] arrayOfBoolean = paramTrackBundle.sampleIsSyncFrameTable;
      label253: int i4;
      label280: int i2;
      label299: int i3;
      if ((localTrack.type == 2) && ((paramInt2 & 0x1) != 0))
      {
        paramInt2 = 1;
        i4 = paramInt3 + paramTrackBundle.trunLength[paramInt1];
        long l2 = localTrack.timescale;
        if (paramInt1 > 0)
          paramLong = paramTrackBundle.nextFragmentDecodeTime;
        if (paramInt3 >= i4)
          break label510;
        if (k == 0)
          break label452;
        i2 = paramParsableByteArray.readUnsignedIntToInt();
        if (m == 0)
          break label462;
        i3 = paramParsableByteArray.readUnsignedIntToInt();
        label311: if ((paramInt3 != 0) || (j == 0))
          break label472;
        paramInt1 = i;
        label324: if (i1 == 0)
          break label495;
        arrayOfInt[paramInt3] = (int)(paramParsableByteArray.readInt() * 1000 / l2);
        label348: arrayOfLong[paramInt3] = (Util.scaleLargeTimestamp(paramLong, 1000L, l2) - l1);
        localObject[paramInt3] = i3;
        if (((paramInt1 >> 16 & 0x1) != 0) || ((paramInt2 != 0) && (paramInt3 != 0)))
          break label504;
      }
      label422: label428: label434: label440: label452: label462: label472: label495: label504: for (int i5 = 1; ; i5 = 0)
      {
        arrayOfBoolean[paramInt3] = i5;
        paramLong += i2;
        paramInt3 += 1;
        break label280;
        j = 0;
        break;
        k = 0;
        break label124;
        m = 0;
        break label136;
        n = 0;
        break label148;
        i1 = 0;
        break label160;
        paramInt2 = 0;
        break label253;
        i2 = localDefaultSampleValues.duration;
        break label299;
        i3 = localDefaultSampleValues.size;
        break label311;
        if (n != 0)
        {
          paramInt1 = paramParsableByteArray.readInt();
          break label324;
        }
        paramInt1 = localDefaultSampleValues.flags;
        break label324;
        arrayOfInt[paramInt3] = 0;
        break label348;
      }
      label510: paramTrackBundle.nextFragmentDecodeTime = paramLong;
      return i4;
      label518: l1 = 0L;
    }
  }

  private static void parseTruns(Atom.ContainerAtom paramContainerAtom, TrackBundle paramTrackBundle, long paramLong, int paramInt)
  {
    paramContainerAtom = paramContainerAtom.leafChildren;
    int i1 = paramContainerAtom.size();
    int k = 0;
    int j = 0;
    int i = 0;
    Object localObject;
    int m;
    if (k < i1)
    {
      localObject = (Atom.LeafAtom)paramContainerAtom.get(k);
      if (((Atom.LeafAtom)localObject).type != Atom.TYPE_trun)
        break label237;
      localObject = ((Atom.LeafAtom)localObject).data;
      ((ParsableByteArray)localObject).setPosition(12);
      m = ((ParsableByteArray)localObject).readUnsignedIntToInt();
      if (m <= 0)
        break label237;
      m += j;
      j = i + 1;
      i = m;
    }
    while (true)
    {
      m = k + 1;
      k = j;
      j = i;
      i = k;
      k = m;
      break;
      paramTrackBundle.currentTrackRunIndex = 0;
      paramTrackBundle.currentSampleInTrackRun = 0;
      paramTrackBundle.currentSampleIndex = 0;
      paramTrackBundle.fragment.initTables(i, j);
      i = 0;
      k = 0;
      j = 0;
      while (i < i1)
      {
        localObject = (Atom.LeafAtom)paramContainerAtom.get(i);
        int n = j;
        m = k;
        if (((Atom.LeafAtom)localObject).type == Atom.TYPE_trun)
        {
          m = parseTrun(paramTrackBundle, j, paramLong, paramInt, ((Atom.LeafAtom)localObject).data, k);
          n = j + 1;
        }
        i += 1;
        j = n;
        k = m;
      }
      return;
      label237: m = i;
      i = j;
      j = m;
    }
  }

  private static void parseUuid(ParsableByteArray paramParsableByteArray, TrackFragment paramTrackFragment, byte[] paramArrayOfByte)
  {
    paramParsableByteArray.setPosition(8);
    paramParsableByteArray.readBytes(paramArrayOfByte, 0, 16);
    if (!Arrays.equals(paramArrayOfByte, PIFF_SAMPLE_ENCRYPTION_BOX_EXTENDED_TYPE))
      return;
    parseSenc(paramParsableByteArray, 16, paramTrackFragment);
  }

  private void processAtomEnded(long paramLong)
  {
    while ((!this.containerAtoms.isEmpty()) && (((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition == paramLong))
      onContainerAtomRead((Atom.ContainerAtom)this.containerAtoms.pop());
    enterReadingAtomHeaderState();
  }

  private boolean readAtomHeader(ExtractorInput paramExtractorInput)
  {
    if (this.atomHeaderBytesRead == 0)
    {
      if (!paramExtractorInput.readFully(this.atomHeader.data, 0, 8, true))
        return false;
      this.atomHeaderBytesRead = 8;
      this.atomHeader.setPosition(0);
      this.atomSize = this.atomHeader.readUnsignedInt();
      this.atomType = this.atomHeader.readInt();
    }
    if (this.atomSize == 1L)
    {
      paramExtractorInput.readFully(this.atomHeader.data, 8, 8);
      this.atomHeaderBytesRead += 8;
      this.atomSize = this.atomHeader.readUnsignedLongToLong();
    }
    long l = paramExtractorInput.getPosition() - this.atomHeaderBytesRead;
    if (this.atomType == Atom.TYPE_moof)
    {
      int j = this.trackBundles.size();
      int i = 0;
      while (i < j)
      {
        TrackFragment localTrackFragment = ((TrackBundle)this.trackBundles.valueAt(i)).fragment;
        localTrackFragment.atomPosition = l;
        localTrackFragment.auxiliaryDataPosition = l;
        localTrackFragment.dataPosition = l;
        i += 1;
      }
    }
    if (this.atomType == Atom.TYPE_mdat)
    {
      this.currentTrackBundle = null;
      this.endOfMdatPosition = (this.atomSize + l);
      if (!this.haveOutputSeekMap)
      {
        this.extractorOutput.seekMap(new SeekMap.Unseekable(this.durationUs));
        this.haveOutputSeekMap = true;
      }
      this.parserState = 2;
      return true;
    }
    if (shouldParseContainerAtom(this.atomType))
    {
      l = paramExtractorInput.getPosition() + this.atomSize - 8L;
      this.containerAtoms.add(new Atom.ContainerAtom(this.atomType, l));
      if (this.atomSize == this.atomHeaderBytesRead)
        processAtomEnded(l);
    }
    while (true)
    {
      return true;
      enterReadingAtomHeaderState();
      continue;
      if (shouldParseLeafAtom(this.atomType))
      {
        if (this.atomHeaderBytesRead != 8)
          throw new ParserException("Leaf atom defines extended atom size (unsupported).");
        if (this.atomSize > 2147483647L)
          throw new ParserException("Leaf atom with length > 2147483647 (unsupported).");
        this.atomData = new ParsableByteArray((int)this.atomSize);
        System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
        this.parserState = 1;
        continue;
      }
      if (this.atomSize > 2147483647L)
        throw new ParserException("Skipping atom with length > 2147483647 (unsupported).");
      this.atomData = null;
      this.parserState = 1;
    }
  }

  private void readAtomPayload(ExtractorInput paramExtractorInput)
  {
    int i = (int)this.atomSize - this.atomHeaderBytesRead;
    if (this.atomData != null)
    {
      paramExtractorInput.readFully(this.atomData.data, 8, i);
      onLeafAtomRead(new Atom.LeafAtom(this.atomType, this.atomData), paramExtractorInput.getPosition());
    }
    while (true)
    {
      processAtomEnded(paramExtractorInput.getPosition());
      return;
      paramExtractorInput.skipFully(i);
    }
  }

  private void readEncryptionData(ExtractorInput paramExtractorInput)
  {
    TrackBundle localTrackBundle = null;
    long l = 9223372036854775807L;
    int j = this.trackBundles.size();
    int i = 0;
    if (i < j)
    {
      TrackFragment localTrackFragment = ((TrackBundle)this.trackBundles.valueAt(i)).fragment;
      if ((!localTrackFragment.sampleEncryptionDataNeedsFill) || (localTrackFragment.auxiliaryDataPosition >= l))
        break label139;
      l = localTrackFragment.auxiliaryDataPosition;
      localTrackBundle = (TrackBundle)this.trackBundles.valueAt(i);
    }
    label139: 
    while (true)
    {
      i += 1;
      break;
      if (localTrackBundle == null)
      {
        this.parserState = 3;
        return;
      }
      i = (int)(l - paramExtractorInput.getPosition());
      if (i < 0)
        throw new ParserException("Offset to encryption data was negative.");
      paramExtractorInput.skipFully(i);
      localTrackBundle.fragment.fillEncryptionData(paramExtractorInput);
      return;
    }
  }

  private boolean readSample(ExtractorInput paramExtractorInput)
  {
    Object localObject;
    int i;
    int j;
    Track localTrack;
    TrackOutput localTrackOutput;
    int k;
    if (this.parserState == 3)
    {
      if (this.currentTrackBundle == null)
      {
        localObject = getNextFragmentRun(this.trackBundles);
        if (localObject == null)
        {
          i = (int)(this.endOfMdatPosition - paramExtractorInput.getPosition());
          if (i < 0)
            throw new ParserException("Offset to end of mdat was negative.");
          paramExtractorInput.skipFully(i);
          enterReadingAtomHeaderState();
          return false;
        }
        l = localObject.fragment.trunDataPosition[localObject.currentTrackRunIndex];
        j = (int)(l - paramExtractorInput.getPosition());
        i = j;
        if (j < 0)
        {
          if (l == ((TrackBundle)localObject).fragment.atomPosition)
          {
            Log.w("FragmentedMp4Extractor", "Offset to sample data was missing.");
            i = 0;
          }
        }
        else
        {
          paramExtractorInput.skipFully(i);
          this.currentTrackBundle = ((TrackBundle)localObject);
        }
      }
      else
      {
        this.sampleSize = this.currentTrackBundle.fragment.sampleSizeTable[this.currentTrackBundle.currentSampleIndex];
        if (!this.currentTrackBundle.fragment.definesEncryptionData)
          break label434;
        this.sampleBytesWritten = appendSampleEncryptionData(this.currentTrackBundle);
        this.sampleSize += this.sampleBytesWritten;
        if (this.currentTrackBundle.track.sampleTransformation == 1)
        {
          this.sampleSize -= 8;
          paramExtractorInput.skipFully(8);
        }
        this.parserState = 4;
        this.sampleCurrentNalBytesRemaining = 0;
      }
    }
    else
    {
      localObject = this.currentTrackBundle.fragment;
      localTrack = this.currentTrackBundle.track;
      localTrackOutput = this.currentTrackBundle.output;
      j = this.currentTrackBundle.currentSampleIndex;
      if (localTrack.nalUnitLengthFieldLength == 0)
        break label482;
      byte[] arrayOfByte = this.nalLength.data;
      arrayOfByte[0] = 0;
      arrayOfByte[1] = 0;
      arrayOfByte[2] = 0;
      i = localTrack.nalUnitLengthFieldLength;
      k = 4 - localTrack.nalUnitLengthFieldLength;
    }
    while (true)
    {
      if (this.sampleBytesWritten >= this.sampleSize)
        break label523;
      if (this.sampleCurrentNalBytesRemaining == 0)
      {
        paramExtractorInput.readFully(this.nalLength.data, k, i);
        this.nalLength.setPosition(0);
        this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
        this.nalStartCode.setPosition(0);
        localTrackOutput.sampleData(this.nalStartCode, 4);
        this.sampleBytesWritten += 4;
        this.sampleSize += k;
        continue;
        throw new ParserException("Offset to sample data was negative.");
        label434: this.sampleBytesWritten = 0;
        break;
      }
      int m = localTrackOutput.sampleData(paramExtractorInput, this.sampleCurrentNalBytesRemaining, false);
      this.sampleBytesWritten += m;
      this.sampleCurrentNalBytesRemaining -= m;
    }
    label482: 
    while (this.sampleBytesWritten < this.sampleSize)
      this.sampleBytesWritten = (localTrackOutput.sampleData(paramExtractorInput, this.sampleSize - this.sampleBytesWritten, false) + this.sampleBytesWritten);
    label523: long l = 1000L * ((TrackFragment)localObject).getSamplePresentationTime(j);
    if (((TrackFragment)localObject).definesEncryptionData)
    {
      i = 1073741824;
      if (localObject.sampleIsSyncFrameTable[j] == 0)
        break label723;
      j = 1;
      label559: k = ((TrackFragment)localObject).header.sampleDescriptionIndex;
      if (!((TrackFragment)localObject).definesEncryptionData)
        break label746;
      if (((TrackFragment)localObject).trackEncryptionBox == null)
        break label728;
      paramExtractorInput = ((TrackFragment)localObject).trackEncryptionBox.keyId;
    }
    while (true)
    {
      label594: if (this.timestampAdjuster != null)
        l = this.timestampAdjuster.adjustSampleTimestamp(l);
      while (true)
      {
        localTrackOutput.sampleMetadata(l, i | j, this.sampleSize, 0, paramExtractorInput);
        paramExtractorInput = this.currentTrackBundle;
        paramExtractorInput.currentSampleIndex += 1;
        paramExtractorInput = this.currentTrackBundle;
        paramExtractorInput.currentSampleInTrackRun += 1;
        if (this.currentTrackBundle.currentSampleInTrackRun == localObject.trunLength[this.currentTrackBundle.currentTrackRunIndex])
        {
          paramExtractorInput = this.currentTrackBundle;
          paramExtractorInput.currentTrackRunIndex += 1;
          this.currentTrackBundle.currentSampleInTrackRun = 0;
          this.currentTrackBundle = null;
        }
        this.parserState = 3;
        return true;
        i = 0;
        break;
        label723: j = 0;
        break label559;
        label728: paramExtractorInput = localTrack.sampleDescriptionEncryptionBoxes[k].keyId;
        break label594;
      }
      label746: paramExtractorInput = null;
    }
  }

  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_moof) || (paramInt == Atom.TYPE_traf) || (paramInt == Atom.TYPE_mvex) || (paramInt == Atom.TYPE_edts);
  }

  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_sidx) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_tfdt) || (paramInt == Atom.TYPE_tfhd) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_trex) || (paramInt == Atom.TYPE_trun) || (paramInt == Atom.TYPE_pssh) || (paramInt == Atom.TYPE_saiz) || (paramInt == Atom.TYPE_saio) || (paramInt == Atom.TYPE_senc) || (paramInt == Atom.TYPE_uuid) || (paramInt == Atom.TYPE_sbgp) || (paramInt == Atom.TYPE_sgpd) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_mehd);
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
    if (this.sideloadedTrack != null)
    {
      paramExtractorOutput = new TrackBundle(paramExtractorOutput.track(0));
      paramExtractorOutput.init(this.sideloadedTrack, new DefaultSampleValues(0, 0, 0, 0));
      this.trackBundles.put(0, paramExtractorOutput);
      this.extractorOutput.endTracks();
    }
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    while (true)
    {
      switch (this.parserState)
      {
      default:
        if (!readSample(paramExtractorInput))
          continue;
        return 0;
      case 0:
        if (readAtomHeader(paramExtractorInput))
          continue;
        return -1;
      case 1:
        readAtomPayload(paramExtractorInput);
        break;
      case 2:
      }
      readEncryptionData(paramExtractorInput);
    }
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    int j = this.trackBundles.size();
    int i = 0;
    while (i < j)
    {
      ((TrackBundle)this.trackBundles.valueAt(i)).reset();
      i += 1;
    }
    this.containerAtoms.clear();
    enterReadingAtomHeaderState();
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    return Sniffer.sniffFragmented(paramExtractorInput);
  }

  @Retention(RetentionPolicy.SOURCE)
  public static @interface Flags
  {
  }

  private static final class TrackBundle
  {
    public int currentSampleInTrackRun;
    public int currentSampleIndex;
    public int currentTrackRunIndex;
    public DefaultSampleValues defaultSampleValues;
    public final TrackFragment fragment = new TrackFragment();
    public final TrackOutput output;
    public Track track;

    public TrackBundle(TrackOutput paramTrackOutput)
    {
      this.output = paramTrackOutput;
    }

    public void init(Track paramTrack, DefaultSampleValues paramDefaultSampleValues)
    {
      this.track = ((Track)Assertions.checkNotNull(paramTrack));
      this.defaultSampleValues = ((DefaultSampleValues)Assertions.checkNotNull(paramDefaultSampleValues));
      this.output.format(paramTrack.format);
      reset();
    }

    public void reset()
    {
      this.fragment.reset();
      this.currentSampleIndex = 0;
      this.currentTrackRunIndex = 0;
      this.currentSampleInTrackRun = 0;
    }

    public void updateDrmInitData(DrmInitData paramDrmInitData)
    {
      this.output.format(this.track.format.copyWithDrmInitData(paramDrmInitData));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor
 * JD-Core Version:    0.6.0
 */