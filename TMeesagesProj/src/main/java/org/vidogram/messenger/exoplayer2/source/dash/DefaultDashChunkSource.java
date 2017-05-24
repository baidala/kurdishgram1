package org.vidogram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.ChunkIndex;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.vidogram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.vidogram.messenger.exoplayer2.extractor.rawcc.RawCcExtractor;
import org.vidogram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.vidogram.messenger.exoplayer2.source.chunk.Chunk;
import org.vidogram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.vidogram.messenger.exoplayer2.source.chunk.ChunkHolder;
import org.vidogram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.vidogram.messenger.exoplayer2.source.chunk.ContainerMediaChunk;
import org.vidogram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.vidogram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.vidogram.messenger.exoplayer2.source.chunk.SingleSampleMediaChunk;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.Period;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.vidogram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;
import org.vidogram.messenger.exoplayer2.upstream.LoaderErrorThrower;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;
import org.vidogram.messenger.exoplayer2.util.Util;

public class DefaultDashChunkSource
  implements DashChunkSource
{
  private final int adaptationSetIndex;
  private final DataSource dataSource;
  private final long elapsedRealtimeOffsetMs;
  private IOException fatalError;
  private DashManifest manifest;
  private final LoaderErrorThrower manifestLoaderErrorThrower;
  private final int maxSegmentsPerLoad;
  private boolean missingLastSegment;
  private int periodIndex;
  private final RepresentationHolder[] representationHolders;
  private final TrackSelection trackSelection;

  public DefaultDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int paramInt2, TrackSelection paramTrackSelection, DataSource paramDataSource, long paramLong, int paramInt3)
  {
    this.manifestLoaderErrorThrower = paramLoaderErrorThrower;
    this.manifest = paramDashManifest;
    this.adaptationSetIndex = paramInt2;
    this.trackSelection = paramTrackSelection;
    this.dataSource = paramDataSource;
    this.periodIndex = paramInt1;
    this.elapsedRealtimeOffsetMs = paramLong;
    this.maxSegmentsPerLoad = paramInt3;
    paramLong = paramDashManifest.getPeriodDurationUs(paramInt1);
    paramLoaderErrorThrower = getRepresentations();
    this.representationHolders = new RepresentationHolder[paramTrackSelection.length()];
    paramInt1 = 0;
    while (paramInt1 < this.representationHolders.length)
    {
      paramDashManifest = (Representation)paramLoaderErrorThrower.get(paramTrackSelection.getIndexInTrackGroup(paramInt1));
      this.representationHolders[paramInt1] = new RepresentationHolder(paramLong, paramDashManifest);
      paramInt1 += 1;
    }
  }

  private long getNowUnixTimeUs()
  {
    if (this.elapsedRealtimeOffsetMs != 0L)
      return (SystemClock.elapsedRealtime() + this.elapsedRealtimeOffsetMs) * 1000L;
    return System.currentTimeMillis() * 1000L;
  }

  private List<Representation> getRepresentations()
  {
    return ((AdaptationSet)this.manifest.getPeriod(this.periodIndex).adaptationSets.get(this.adaptationSetIndex)).representations;
  }

  private static Chunk newInitializationChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, Format paramFormat, int paramInt, Object paramObject, RangedUri paramRangedUri1, RangedUri paramRangedUri2)
  {
    String str = paramRepresentationHolder.representation.baseUrl;
    if (paramRangedUri1 != null)
    {
      paramRangedUri2 = paramRangedUri1.attemptMerge(paramRangedUri2, str);
      if (paramRangedUri2 != null)
        break label85;
    }
    while (true)
    {
      return new InitializationChunk(paramDataSource, new DataSpec(paramRangedUri1.resolveUri(str), paramRangedUri1.start, paramRangedUri1.length, paramRepresentationHolder.representation.getCacheKey()), paramFormat, paramInt, paramObject, paramRepresentationHolder.extractorWrapper);
      paramRangedUri1 = paramRangedUri2;
      continue;
      label85: paramRangedUri1 = paramRangedUri2;
    }
  }

  private static Chunk newMediaChunk(RepresentationHolder paramRepresentationHolder, DataSource paramDataSource, Format paramFormat1, int paramInt1, Object paramObject, Format paramFormat2, int paramInt2, int paramInt3)
  {
    Representation localRepresentation = paramRepresentationHolder.representation;
    long l1 = paramRepresentationHolder.getSegmentStartTimeUs(paramInt2);
    Object localObject = paramRepresentationHolder.getSegmentUrl(paramInt2);
    String str = localRepresentation.baseUrl;
    long l2;
    if (paramRepresentationHolder.extractorWrapper == null)
    {
      l2 = paramRepresentationHolder.getSegmentEndTimeUs(paramInt2);
      return new SingleSampleMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), ((RangedUri)localObject).start, ((RangedUri)localObject).length, localRepresentation.getCacheKey()), paramFormat1, paramInt1, paramObject, l1, l2, paramInt2, paramFormat1);
    }
    int j = 1;
    int i = 1;
    while (true)
    {
      RangedUri localRangedUri;
      if (i < paramInt3)
      {
        localRangedUri = ((RangedUri)localObject).attemptMerge(paramRepresentationHolder.getSegmentUrl(paramInt2 + i), str);
        if (localRangedUri != null);
      }
      else
      {
        l2 = paramRepresentationHolder.getSegmentEndTimeUs(paramInt2 + j - 1);
        return new ContainerMediaChunk(paramDataSource, new DataSpec(((RangedUri)localObject).resolveUri(str), ((RangedUri)localObject).start, ((RangedUri)localObject).length, localRepresentation.getCacheKey()), paramFormat1, paramInt1, paramObject, l1, l2, paramInt2, j, -localRepresentation.presentationTimeOffsetUs, paramRepresentationHolder.extractorWrapper, paramFormat2);
      }
      j += 1;
      i += 1;
      localObject = localRangedUri;
    }
  }

  public final void getNextChunk(MediaChunk paramMediaChunk, long paramLong, ChunkHolder paramChunkHolder)
  {
    if (this.fatalError != null)
      return;
    if (paramMediaChunk != null)
      l = paramMediaChunk.endTimeUs - paramLong;
    RepresentationHolder localRepresentationHolder;
    Format localFormat;
    while (true)
    {
      this.trackSelection.updateSelectedTrack(l);
      localRepresentationHolder = this.representationHolders[this.trackSelection.getSelectedIndex()];
      Representation localRepresentation = localRepresentationHolder.representation;
      DashSegmentIndex localDashSegmentIndex = localRepresentationHolder.segmentIndex;
      RangedUri localRangedUri1 = null;
      RangedUri localRangedUri2 = null;
      localFormat = localRepresentationHolder.sampleFormat;
      if (localFormat == null)
        localRangedUri1 = localRepresentation.getInitializationUri();
      if (localDashSegmentIndex == null)
        localRangedUri2 = localRepresentation.getIndexUri();
      if ((localRangedUri1 == null) && (localRangedUri2 == null))
        break;
      paramChunkHolder.chunk = newInitializationChunk(localRepresentationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), localRangedUri1, localRangedUri2);
      return;
      l = 0L;
    }
    long l = getNowUnixTimeUs();
    int j = localRepresentationHolder.getFirstSegmentNum();
    int k = localRepresentationHolder.getLastSegmentNum();
    if (k == -1)
    {
      i = 1;
      if (i == 0)
        break label478;
      l = l - this.manifest.availabilityStartTime * 1000L - this.manifest.getPeriod(this.periodIndex).startMs * 1000L;
      i = j;
      if (this.manifest.timeShiftBufferDepth != -9223372036854775807L)
        i = Math.max(j, localRepresentationHolder.getSegmentNum(l - this.manifest.timeShiftBufferDepth * 1000L));
      j = localRepresentationHolder.getSegmentNum(l);
      k = j - 1;
      j = i;
    }
    label315: label478: for (int i = k; ; i = k)
    {
      if (paramMediaChunk == null)
      {
        k = Util.constrainValue(localRepresentationHolder.getSegmentNum(paramLong), j, i);
        if ((k <= i) && ((!this.missingLastSegment) || (k < i)))
          break label414;
        if ((this.manifest.dynamic) && (this.periodIndex >= this.manifest.getPeriodCount() - 1))
          break label408;
      }
      for (boolean bool = true; ; bool = false)
      {
        paramChunkHolder.endOfStream = bool;
        return;
        i = 0;
        break;
        int m = paramMediaChunk.getNextChunkIndex();
        k = m;
        if (m >= j)
          break label315;
        this.fatalError = new BehindLiveWindowException();
        return;
      }
      i = Math.min(this.maxSegmentsPerLoad, i - k + 1);
      paramChunkHolder.chunk = newMediaChunk(localRepresentationHolder, this.dataSource, this.trackSelection.getSelectedFormat(), this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), localFormat, k, i);
      return;
    }
  }

  public int getPreferredQueueSize(long paramLong, List<? extends MediaChunk> paramList)
  {
    if ((this.fatalError != null) || (this.trackSelection.length() < 2))
      return paramList.size();
    return this.trackSelection.evaluateQueueSize(paramLong, paramList);
  }

  public void maybeThrowError()
  {
    if (this.fatalError != null)
      throw this.fatalError;
    this.manifestLoaderErrorThrower.maybeThrowError();
  }

  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof InitializationChunk))
    {
      paramChunk = (InitializationChunk)paramChunk;
      RepresentationHolder localRepresentationHolder = this.representationHolders[this.trackSelection.indexOf(paramChunk.trackFormat)];
      Object localObject = paramChunk.getSampleFormat();
      if (localObject != null)
        localRepresentationHolder.setSampleFormat((Format)localObject);
      if (localRepresentationHolder.segmentIndex == null)
      {
        localObject = paramChunk.getSeekMap();
        if (localObject != null)
          localRepresentationHolder.segmentIndex = new DashWrappingSegmentIndex((ChunkIndex)localObject, paramChunk.dataSpec.uri.toString());
      }
    }
  }

  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, Exception paramException)
  {
    if (!paramBoolean)
      return false;
    if ((!this.manifest.dynamic) && ((paramChunk instanceof MediaChunk)) && ((paramException instanceof HttpDataSource.InvalidResponseCodeException)) && (((HttpDataSource.InvalidResponseCodeException)paramException).responseCode == 404))
    {
      int i = this.representationHolders[this.trackSelection.indexOf(paramChunk.trackFormat)].getLastSegmentNum();
      if (((MediaChunk)paramChunk).getNextChunkIndex() > i)
      {
        this.missingLastSegment = true;
        return true;
      }
    }
    return ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(paramChunk.trackFormat), paramException);
  }

  public void updateManifest(DashManifest paramDashManifest, int paramInt)
  {
    try
    {
      this.manifest = paramDashManifest;
      this.periodIndex = paramInt;
      long l = this.manifest.getPeriodDurationUs(this.periodIndex);
      paramDashManifest = getRepresentations();
      paramInt = 0;
      while (paramInt < this.representationHolders.length)
      {
        Representation localRepresentation = (Representation)paramDashManifest.get(this.trackSelection.getIndexInTrackGroup(paramInt));
        this.representationHolders[paramInt].updateRepresentation(l, localRepresentation);
        paramInt += 1;
      }
    }
    catch (BehindLiveWindowException paramDashManifest)
    {
      this.fatalError = paramDashManifest;
    }
  }

  public static final class Factory
    implements DashChunkSource.Factory
  {
    private final DataSource.Factory dataSourceFactory;
    private final int maxSegmentsPerLoad;

    public Factory(DataSource.Factory paramFactory)
    {
      this(paramFactory, 1);
    }

    public Factory(DataSource.Factory paramFactory, int paramInt)
    {
      this.dataSourceFactory = paramFactory;
      this.maxSegmentsPerLoad = paramInt;
    }

    public DashChunkSource createDashChunkSource(LoaderErrorThrower paramLoaderErrorThrower, DashManifest paramDashManifest, int paramInt1, int paramInt2, TrackSelection paramTrackSelection, long paramLong)
    {
      return new DefaultDashChunkSource(paramLoaderErrorThrower, paramDashManifest, paramInt1, paramInt2, paramTrackSelection, this.dataSourceFactory.createDataSource(), paramLong, this.maxSegmentsPerLoad);
    }
  }

  protected static final class RepresentationHolder
  {
    public final ChunkExtractorWrapper extractorWrapper;
    private long periodDurationUs;
    public Representation representation;
    public Format sampleFormat;
    public DashSegmentIndex segmentIndex;
    private int segmentNumShift;

    public RepresentationHolder(long paramLong, Representation paramRepresentation)
    {
      this.periodDurationUs = paramLong;
      this.representation = paramRepresentation;
      Object localObject = paramRepresentation.format.containerMimeType;
      if (mimeTypeIsRawText((String)localObject))
      {
        this.extractorWrapper = null;
        this.segmentIndex = paramRepresentation.getIndex();
        return;
      }
      boolean bool = false;
      if ("application/x-rawcc".equals(localObject))
      {
        localObject = new RawCcExtractor(paramRepresentation.format);
        bool = true;
      }
      while (true)
      {
        this.extractorWrapper = new ChunkExtractorWrapper((Extractor)localObject, paramRepresentation.format, true, bool);
        break;
        if (mimeTypeIsWebm((String)localObject))
        {
          localObject = new MatroskaExtractor();
          continue;
        }
        localObject = new FragmentedMp4Extractor();
      }
    }

    private static boolean mimeTypeIsRawText(String paramString)
    {
      return (MimeTypes.isText(paramString)) || ("application/ttml+xml".equals(paramString));
    }

    private static boolean mimeTypeIsWebm(String paramString)
    {
      return (paramString.startsWith("video/webm")) || (paramString.startsWith("audio/webm")) || (paramString.startsWith("application/webm"));
    }

    public int getFirstSegmentNum()
    {
      return this.segmentIndex.getFirstSegmentNum() + this.segmentNumShift;
    }

    public int getLastSegmentNum()
    {
      int i = this.segmentIndex.getLastSegmentNum(this.periodDurationUs);
      if (i == -1)
        return -1;
      return this.segmentNumShift + i;
    }

    public long getSegmentEndTimeUs(int paramInt)
    {
      return getSegmentStartTimeUs(paramInt) + this.segmentIndex.getDurationUs(paramInt - this.segmentNumShift, this.periodDurationUs);
    }

    public int getSegmentNum(long paramLong)
    {
      return this.segmentIndex.getSegmentNum(paramLong, this.periodDurationUs) + this.segmentNumShift;
    }

    public long getSegmentStartTimeUs(int paramInt)
    {
      return this.segmentIndex.getTimeUs(paramInt - this.segmentNumShift);
    }

    public RangedUri getSegmentUrl(int paramInt)
    {
      return this.segmentIndex.getSegmentUrl(paramInt - this.segmentNumShift);
    }

    public void setSampleFormat(Format paramFormat)
    {
      this.sampleFormat = paramFormat;
    }

    public void updateRepresentation(long paramLong, Representation paramRepresentation)
    {
      DashSegmentIndex localDashSegmentIndex1 = this.representation.getIndex();
      DashSegmentIndex localDashSegmentIndex2 = paramRepresentation.getIndex();
      this.periodDurationUs = paramLong;
      this.representation = paramRepresentation;
      if (localDashSegmentIndex1 == null);
      do
      {
        return;
        this.segmentIndex = localDashSegmentIndex2;
      }
      while (!localDashSegmentIndex1.isExplicit());
      int i = localDashSegmentIndex1.getLastSegmentNum(this.periodDurationUs);
      paramLong = localDashSegmentIndex1.getTimeUs(i);
      paramLong = localDashSegmentIndex1.getDurationUs(i, this.periodDurationUs) + paramLong;
      i = localDashSegmentIndex2.getFirstSegmentNum();
      long l = localDashSegmentIndex2.getTimeUs(i);
      if (paramLong == l)
      {
        j = this.segmentNumShift;
        this.segmentNumShift = (localDashSegmentIndex1.getLastSegmentNum(this.periodDurationUs) + 1 - i + j);
        return;
      }
      if (paramLong < l)
        throw new BehindLiveWindowException();
      int j = this.segmentNumShift;
      this.segmentNumShift = (localDashSegmentIndex1.getSegmentNum(l, this.periodDurationUs) - i + j);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.dash.DefaultDashChunkSource
 * JD-Core Version:    0.6.0
 */