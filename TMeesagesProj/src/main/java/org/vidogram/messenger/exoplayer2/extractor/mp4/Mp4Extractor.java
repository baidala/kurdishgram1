package org.vidogram.messenger.exoplayer2.extractor.mp4;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.vidogram.messenger.exoplayer2.extractor.GaplessInfoHolder;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.NalUnitUtil;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class Mp4Extractor
  implements Extractor, SeekMap
{
  private static final int BRAND_QUICKTIME;
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new Mp4Extractor() };
    }
  };
  private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144L;
  private static final int STATE_AFTER_SEEK = 0;
  private static final int STATE_READING_ATOM_HEADER = 1;
  private static final int STATE_READING_ATOM_PAYLOAD = 2;
  private static final int STATE_READING_SAMPLE = 3;
  private ParsableByteArray atomData;
  private final ParsableByteArray atomHeader = new ParsableByteArray(16);
  private int atomHeaderBytesRead;
  private long atomSize;
  private int atomType;
  private final Stack<Atom.ContainerAtom> containerAtoms = new Stack();
  private long durationUs;
  private ExtractorOutput extractorOutput;
  private boolean isQuickTime;
  private final ParsableByteArray nalLength = new ParsableByteArray(4);
  private final ParsableByteArray nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
  private int parserState;
  private int sampleBytesWritten;
  private int sampleCurrentNalBytesRemaining;
  private Mp4Track[] tracks;

  static
  {
    BRAND_QUICKTIME = Util.getIntegerCodeForString("qt  ");
  }

  public Mp4Extractor()
  {
    enterReadingAtomHeaderState();
  }

  private void enterReadingAtomHeaderState()
  {
    this.parserState = 1;
    this.atomHeaderBytesRead = 0;
  }

  private int getTrackIndexOfEarliestCurrentSample()
  {
    int j = -1;
    long l1 = 9223372036854775807L;
    int i = 0;
    if (i < this.tracks.length)
    {
      Mp4Track localMp4Track = this.tracks[i];
      int k = localMp4Track.sampleIndex;
      long l2;
      if (k == localMp4Track.sampleTable.sampleCount)
        l2 = l1;
      while (true)
      {
        i += 1;
        l1 = l2;
        break;
        long l3 = localMp4Track.sampleTable.offsets[k];
        l2 = l1;
        if (l3 >= l1)
          continue;
        l2 = l3;
        j = i;
      }
    }
    return j;
  }

  private void processAtomEnded(long paramLong)
  {
    while ((!this.containerAtoms.isEmpty()) && (((Atom.ContainerAtom)this.containerAtoms.peek()).endPosition == paramLong))
    {
      Atom.ContainerAtom localContainerAtom = (Atom.ContainerAtom)this.containerAtoms.pop();
      if (localContainerAtom.type == Atom.TYPE_moov)
      {
        processMoovAtom(localContainerAtom);
        this.containerAtoms.clear();
        this.parserState = 3;
        continue;
      }
      if (this.containerAtoms.isEmpty())
        continue;
      ((Atom.ContainerAtom)this.containerAtoms.peek()).add(localContainerAtom);
    }
    if (this.parserState != 3)
      enterReadingAtomHeaderState();
  }

  private static boolean processFtypAtom(ParsableByteArray paramParsableByteArray)
  {
    paramParsableByteArray.setPosition(8);
    if (paramParsableByteArray.readInt() == BRAND_QUICKTIME)
      return true;
    paramParsableByteArray.skipBytes(4);
    while (paramParsableByteArray.bytesLeft() > 0)
      if (paramParsableByteArray.readInt() == BRAND_QUICKTIME)
        return true;
    return false;
  }

  private void processMoovAtom(Atom.ContainerAtom paramContainerAtom)
  {
    ArrayList localArrayList = new ArrayList();
    GaplessInfoHolder localGaplessInfoHolder = new GaplessInfoHolder();
    Object localObject1 = paramContainerAtom.getLeafAtomOfType(Atom.TYPE_udta);
    Metadata localMetadata;
    if (localObject1 != null)
    {
      localMetadata = AtomParsers.parseUdta((Atom.LeafAtom)localObject1, this.isQuickTime);
      if (localMetadata != null)
        localGaplessInfoHolder.setFromMetadata(localMetadata);
    }
    while (true)
    {
      int i = 0;
      long l1 = 9223372036854775807L;
      long l2 = -9223372036854775807L;
      if (i < paramContainerAtom.containerChildren.size())
      {
        localObject1 = (Atom.ContainerAtom)paramContainerAtom.containerChildren.get(i);
        if (((Atom.ContainerAtom)localObject1).type == Atom.TYPE_trak);
      }
      while (true)
      {
        i += 1;
        break;
        Track localTrack = AtomParsers.parseTrak((Atom.ContainerAtom)localObject1, paramContainerAtom.getLeafAtomOfType(Atom.TYPE_mvhd), -9223372036854775807L, null, this.isQuickTime);
        if (localTrack == null)
          continue;
        TrackSampleTable localTrackSampleTable = AtomParsers.parseStbl(localTrack, ((Atom.ContainerAtom)localObject1).getContainerAtomOfType(Atom.TYPE_mdia).getContainerAtomOfType(Atom.TYPE_minf).getContainerAtomOfType(Atom.TYPE_stbl), localGaplessInfoHolder);
        if (localTrackSampleTable.sampleCount == 0)
          continue;
        Mp4Track localMp4Track = new Mp4Track(localTrack, localTrackSampleTable, this.extractorOutput.track(i));
        int j = localTrackSampleTable.maximumSize;
        Format localFormat = localTrack.format.copyWithMaxInputSize(j + 30);
        Object localObject2 = localFormat;
        if (localTrack.type == 1)
        {
          localObject1 = localFormat;
          if (localGaplessInfoHolder.hasGaplessInfo())
            localObject1 = localFormat.copyWithGaplessInfo(localGaplessInfoHolder.encoderDelay, localGaplessInfoHolder.encoderPadding);
          localObject2 = localObject1;
          if (localMetadata != null)
            localObject2 = ((Format)localObject1).copyWithMetadata(localMetadata);
        }
        localMp4Track.trackOutput.format((Format)localObject2);
        l2 = Math.max(l2, localTrack.durationUs);
        localArrayList.add(localMp4Track);
        long l3 = localTrackSampleTable.offsets[0];
        if (l3 < l1)
        {
          l1 = l3;
          continue;
          this.durationUs = l2;
          this.tracks = ((Mp4Track[])localArrayList.toArray(new Mp4Track[localArrayList.size()]));
          this.extractorOutput.endTracks();
          this.extractorOutput.seekMap(this);
          return;
        }
      }
      localMetadata = null;
    }
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
    if (shouldParseContainerAtom(this.atomType))
    {
      long l = paramExtractorInput.getPosition() + this.atomSize - this.atomHeaderBytesRead;
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
        if (this.atomHeaderBytesRead == 8)
        {
          bool = true;
          label210: Assertions.checkState(bool);
          if (this.atomSize > 2147483647L)
            break label285;
        }
        label285: for (boolean bool = true; ; bool = false)
        {
          Assertions.checkState(bool);
          this.atomData = new ParsableByteArray((int)this.atomSize);
          System.arraycopy(this.atomHeader.data, 0, this.atomData.data, 0, 8);
          this.parserState = 2;
          break;
          bool = false;
          break label210;
        }
      }
      this.atomData = null;
      this.parserState = 2;
    }
  }

  private boolean readAtomPayload(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    long l1 = this.atomSize - this.atomHeaderBytesRead;
    long l2 = paramExtractorInput.getPosition();
    int i;
    if (this.atomData != null)
    {
      paramExtractorInput.readFully(this.atomData.data, this.atomHeaderBytesRead, (int)l1);
      if (this.atomType == Atom.TYPE_ftyp)
      {
        this.isQuickTime = processFtypAtom(this.atomData);
        i = 0;
      }
    }
    while (true)
    {
      processAtomEnded(l2 + l1);
      if ((i != 0) && (this.parserState != 3))
      {
        return true;
        if (!this.containerAtoms.isEmpty())
        {
          ((Atom.ContainerAtom)this.containerAtoms.peek()).add(new Atom.LeafAtom(this.atomType, this.atomData));
          i = 0;
          continue;
          if (l1 < 262144L)
          {
            paramExtractorInput.skipFully((int)l1);
            i = 0;
            continue;
          }
          paramPositionHolder.position = (l1 + paramExtractorInput.getPosition());
          i = 1;
          continue;
        }
      }
      else
      {
        return false;
      }
      i = 0;
    }
  }

  private int readSample(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    int i = getTrackIndexOfEarliestCurrentSample();
    if (i == -1)
      return -1;
    Mp4Track localMp4Track = this.tracks[i];
    TrackOutput localTrackOutput = localMp4Track.trackOutput;
    int k = localMp4Track.sampleIndex;
    long l2 = localMp4Track.sampleTable.offsets[k];
    int j = localMp4Track.sampleTable.sizes[k];
    i = j;
    long l1 = l2;
    if (localMp4Track.track.sampleTransformation == 1)
    {
      l1 = l2 + 8L;
      i = j - 8;
    }
    l2 = l1 - paramExtractorInput.getPosition() + this.sampleBytesWritten;
    if ((l2 < 0L) || (l2 >= 262144L))
    {
      paramPositionHolder.position = l1;
      return 1;
    }
    paramExtractorInput.skipFully((int)l2);
    if (localMp4Track.track.nalUnitLengthFieldLength != 0)
    {
      paramPositionHolder = this.nalLength.data;
      paramPositionHolder[0] = 0;
      paramPositionHolder[1] = 0;
      paramPositionHolder[2] = 0;
      j = localMp4Track.track.nalUnitLengthFieldLength;
      int m = 4 - localMp4Track.track.nalUnitLengthFieldLength;
      while (this.sampleBytesWritten < i)
      {
        if (this.sampleCurrentNalBytesRemaining == 0)
        {
          paramExtractorInput.readFully(this.nalLength.data, m, j);
          this.nalLength.setPosition(0);
          this.sampleCurrentNalBytesRemaining = this.nalLength.readUnsignedIntToInt();
          this.nalStartCode.setPosition(0);
          localTrackOutput.sampleData(this.nalStartCode, 4);
          this.sampleBytesWritten += 4;
          i += m;
          continue;
        }
        int n = localTrackOutput.sampleData(paramExtractorInput, this.sampleCurrentNalBytesRemaining, false);
        this.sampleBytesWritten += n;
        this.sampleCurrentNalBytesRemaining -= n;
      }
    }
    while (true)
    {
      localTrackOutput.sampleMetadata(localMp4Track.sampleTable.timestampsUs[k], localMp4Track.sampleTable.flags[k], i, 0, null);
      localMp4Track.sampleIndex += 1;
      this.sampleBytesWritten = 0;
      this.sampleCurrentNalBytesRemaining = 0;
      return 0;
      while (this.sampleBytesWritten < i)
      {
        j = localTrackOutput.sampleData(paramExtractorInput, i - this.sampleBytesWritten, false);
        this.sampleBytesWritten += j;
        this.sampleCurrentNalBytesRemaining -= j;
      }
    }
  }

  private static boolean shouldParseContainerAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_moov) || (paramInt == Atom.TYPE_trak) || (paramInt == Atom.TYPE_mdia) || (paramInt == Atom.TYPE_minf) || (paramInt == Atom.TYPE_stbl) || (paramInt == Atom.TYPE_edts);
  }

  private static boolean shouldParseLeafAtom(int paramInt)
  {
    return (paramInt == Atom.TYPE_mdhd) || (paramInt == Atom.TYPE_mvhd) || (paramInt == Atom.TYPE_hdlr) || (paramInt == Atom.TYPE_stsd) || (paramInt == Atom.TYPE_stts) || (paramInt == Atom.TYPE_stss) || (paramInt == Atom.TYPE_ctts) || (paramInt == Atom.TYPE_elst) || (paramInt == Atom.TYPE_stsc) || (paramInt == Atom.TYPE_stsz) || (paramInt == Atom.TYPE_stz2) || (paramInt == Atom.TYPE_stco) || (paramInt == Atom.TYPE_co64) || (paramInt == Atom.TYPE_tkhd) || (paramInt == Atom.TYPE_ftyp) || (paramInt == Atom.TYPE_udta);
  }

  public long getDurationUs()
  {
    return this.durationUs;
  }

  public long getPosition(long paramLong)
  {
    long l1 = 9223372036854775807L;
    Mp4Track[] arrayOfMp4Track = this.tracks;
    int m = arrayOfMp4Track.length;
    int i = 0;
    if (i < m)
    {
      Mp4Track localMp4Track = arrayOfMp4Track[i];
      TrackSampleTable localTrackSampleTable = localMp4Track.sampleTable;
      int k = localTrackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(paramLong);
      int j = k;
      if (k == -1)
        j = localTrackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(paramLong);
      localMp4Track.sampleIndex = j;
      long l2 = localTrackSampleTable.offsets[j];
      if (l2 >= l1)
        break label102;
      l1 = l2;
    }
    label102: 
    while (true)
    {
      i += 1;
      break;
      return l1;
    }
  }

  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.extractorOutput = paramExtractorOutput;
  }

  public boolean isSeekable()
  {
    return true;
  }

  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
  {
    do
    {
      do
        while (true)
          switch (this.parserState)
          {
          default:
            return readSample(paramExtractorInput, paramPositionHolder);
          case 0:
            if (paramExtractorInput.getPosition() == 0L)
            {
              enterReadingAtomHeaderState();
              continue;
            }
            this.parserState = 3;
          case 1:
          case 2:
          }
      while (readAtomHeader(paramExtractorInput));
      return -1;
    }
    while (!readAtomPayload(paramExtractorInput, paramPositionHolder));
    return 1;
  }

  public void release()
  {
  }

  public void seek(long paramLong)
  {
    this.containerAtoms.clear();
    this.atomHeaderBytesRead = 0;
    this.sampleBytesWritten = 0;
    this.sampleCurrentNalBytesRemaining = 0;
    this.parserState = 0;
  }

  public boolean sniff(ExtractorInput paramExtractorInput)
  {
    return Sniffer.sniffUnfragmented(paramExtractorInput);
  }

  private static final class Mp4Track
  {
    public int sampleIndex;
    public final TrackSampleTable sampleTable;
    public final Track track;
    public final TrackOutput trackOutput;

    public Mp4Track(Track paramTrack, TrackSampleTable paramTrackSampleTable, TrackOutput paramTrackOutput)
    {
      this.track = paramTrack;
      this.sampleTable = paramTrackSampleTable;
      this.trackOutput = paramTrackOutput;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.mp4.Mp4Extractor
 * JD-Core Version:    0.6.0
 */