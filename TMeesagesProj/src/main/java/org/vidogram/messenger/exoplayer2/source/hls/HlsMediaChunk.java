package org.vidogram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorInput;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
import org.vidogram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
import org.vidogram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import org.vidogram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.vidogram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;
import org.vidogram.messenger.exoplayer2.util.Util;

final class HlsMediaChunk extends MediaChunk
{
  private static final String AAC_FILE_EXTENSION = ".aac";
  private static final String AC3_FILE_EXTENSION = ".ac3";
  private static final String EC3_FILE_EXTENSION = ".ec3";
  private static final String MP3_FILE_EXTENSION = ".mp3";
  private static final String MP4_FILE_EXTENSION = ".mp4";
  private static final AtomicInteger UID_SOURCE = new AtomicInteger();
  private static final String VTT_FILE_EXTENSION = ".vtt";
  private static final String WEBVTT_FILE_EXTENSION = ".webvtt";
  private long adjustedEndTimeUs;
  private int bytesLoaded;
  public final int discontinuitySequenceNumber;
  private Extractor extractor;
  private HlsSampleStreamWrapper extractorOutput;
  public final HlsMasterPlaylist.HlsUrl hlsUrl;
  private final DataSource initDataSource;
  private final DataSpec initDataSpec;
  private boolean initLoadCompleted;
  private int initSegmentBytesLoaded;
  private final boolean isEncrypted;
  private final boolean isMasterTimestampSource;
  private volatile boolean loadCanceled;
  private volatile boolean loadCompleted;
  private final HlsMediaChunk previousChunk;
  private final TimestampAdjuster timestampAdjuster;
  public final int uid;

  public HlsMediaChunk(DataSource paramDataSource, DataSpec paramDataSpec1, DataSpec paramDataSpec2, HlsMasterPlaylist.HlsUrl paramHlsUrl, int paramInt1, Object paramObject, HlsMediaPlaylist.Segment paramSegment, int paramInt2, boolean paramBoolean, TimestampAdjuster paramTimestampAdjuster, HlsMediaChunk paramHlsMediaChunk, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    super(paramArrayOfByte1, paramDataSpec1, paramArrayOfByte2, paramInt1, paramObject, l1, paramSegment.durationUs + l2, paramInt2);
    this.initDataSpec = paramDataSpec2;
    this.hlsUrl = paramHlsUrl;
    this.isMasterTimestampSource = paramBoolean;
    this.timestampAdjuster = paramTimestampAdjuster;
    this.previousChunk = paramHlsMediaChunk;
    this.isEncrypted = (this.dataSource instanceof Aes128DataSource);
    this.initDataSource = paramDataSource;
    this.discontinuitySequenceNumber = paramSegment.discontinuitySequenceNumber;
    this.adjustedEndTimeUs = this.endTimeUs;
    this.uid = UID_SOURCE.getAndIncrement();
  }

  private static DataSource buildDataSource(DataSource paramDataSource, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2)
  {
    if ((paramArrayOfByte1 == null) || (paramArrayOfByte2 == null))
      return paramDataSource;
    return new Aes128DataSource(paramDataSource, paramArrayOfByte1, paramArrayOfByte2);
  }

  private Extractor buildExtractor()
  {
    int k = 1;
    int m = 0;
    int j = 0;
    int i;
    Object localObject;
    if ((this.previousChunk == null) || (this.previousChunk.discontinuitySequenceNumber != this.discontinuitySequenceNumber) || (this.trackFormat != this.previousChunk.trackFormat))
    {
      i = 1;
      localObject = this.dataSpec.uri.getLastPathSegment();
      if (!((String)localObject).endsWith(".aac"))
        break label104;
      localObject = new AdtsExtractor(this.startTimeUs);
      i = k;
    }
    while (true)
    {
      if (i != 0)
        ((Extractor)localObject).init(this.extractorOutput);
      return localObject;
      i = 0;
      break;
      label104: if ((((String)localObject).endsWith(".ac3")) || (((String)localObject).endsWith(".ec3")))
      {
        localObject = new Ac3Extractor(this.startTimeUs);
        i = k;
        continue;
      }
      if (((String)localObject).endsWith(".mp3"))
      {
        localObject = new Mp3Extractor(this.startTimeUs);
        i = k;
        continue;
      }
      if ((((String)localObject).endsWith(".webvtt")) || (((String)localObject).endsWith(".vtt")))
      {
        localObject = new WebvttExtractor(this.trackFormat.language, this.timestampAdjuster);
        i = k;
        continue;
      }
      if (i == 0)
      {
        localObject = this.previousChunk.extractor;
        i = 0;
        continue;
      }
      if (((String)localObject).endsWith(".mp4"))
      {
        localObject = new FragmentedMp4Extractor(0, this.timestampAdjuster);
        i = k;
        continue;
      }
      localObject = this.trackFormat.codecs;
      i = m;
      if (!TextUtils.isEmpty((CharSequence)localObject))
      {
        if (!"audio/mp4a-latm".equals(MimeTypes.getAudioMediaMimeType((String)localObject)))
          j = 2;
        i = j;
        if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType((String)localObject)))
          i = j | 0x4;
      }
      localObject = new TsExtractor(this.timestampAdjuster, new DefaultTsPayloadReaderFactory(i), true);
      i = k;
    }
  }

  private void loadMedia()
  {
    int j = 0;
    Object localObject1;
    int i;
    if (this.isEncrypted)
    {
      localObject1 = this.dataSpec;
      if (this.bytesLoaded != 0)
        i = 1;
    }
    try
    {
      while (true)
      {
        localObject1 = new DefaultExtractorInput(this.dataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.dataSource.open((DataSpec)localObject1));
        if (i != 0)
          ((ExtractorInput)localObject1).skipFully(this.bytesLoaded);
        i = j;
        try
        {
          if (!this.isMasterTimestampSource)
          {
            i = j;
            if (this.timestampAdjuster != null)
            {
              this.timestampAdjuster.waitUntilInitialized();
              i = j;
            }
          }
          while (true)
            if ((i == 0) && (!this.loadCanceled))
            {
              i = this.extractor.read((ExtractorInput)localObject1, null);
              continue;
              i = 0;
              break;
              localObject1 = Util.getRemainderDataSpec(this.dataSpec, this.bytesLoaded);
              i = 0;
              break;
            }
          long l = this.extractorOutput.getLargestQueuedTimestampUs();
          if (l != -9223372036854775808L)
            this.adjustedEndTimeUs = l;
          this.bytesLoaded = (int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition);
          Util.closeQuietly(this.dataSource);
          this.loadCompleted = true;
          return;
        }
        finally
        {
          this.bytesLoaded = (int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition);
        }
      }
    }
    finally
    {
      Util.closeQuietly(this.dataSource);
    }
    throw localObject2;
  }

  private void maybeLoadInitData()
  {
    if ((this.previousChunk == null) || (this.previousChunk.extractor != this.extractor) || (this.initLoadCompleted) || (this.initDataSpec == null))
      return;
    Object localObject1 = Util.getRemainderDataSpec(this.initDataSpec, this.initSegmentBytesLoaded);
    try
    {
      localObject1 = new DefaultExtractorInput(this.initDataSource, ((DataSpec)localObject1).absoluteStreamPosition, this.initDataSource.open((DataSpec)localObject1));
      int i = 0;
      while (true)
      {
        if (i == 0);
        try
        {
          if (!this.loadCanceled)
          {
            i = this.extractor.read((ExtractorInput)localObject1, null);
            continue;
          }
          i = this.initSegmentBytesLoaded;
          this.initSegmentBytesLoaded = ((int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition) + i);
          Util.closeQuietly(this.dataSource);
          this.initLoadCompleted = true;
          return;
        }
        finally
        {
          i = this.initSegmentBytesLoaded;
          this.initSegmentBytesLoaded = ((int)(((ExtractorInput)localObject1).getPosition() - this.dataSpec.absoluteStreamPosition) + i);
        }
      }
    }
    finally
    {
      Util.closeQuietly(this.dataSource);
    }
    throw localObject2;
  }

  public long bytesLoaded()
  {
    return this.bytesLoaded;
  }

  public void cancelLoad()
  {
    this.loadCanceled = true;
  }

  public long getAdjustedEndTimeUs()
  {
    return this.adjustedEndTimeUs;
  }

  public long getAdjustedStartTimeUs()
  {
    return this.adjustedEndTimeUs - getDurationUs();
  }

  public void init(HlsSampleStreamWrapper paramHlsSampleStreamWrapper)
  {
    this.extractorOutput = paramHlsSampleStreamWrapper;
    int i = this.uid;
    if ((this.previousChunk != null) && (this.previousChunk.hlsUrl != this.hlsUrl));
    for (boolean bool = true; ; bool = false)
    {
      paramHlsSampleStreamWrapper.init(i, bool);
      return;
    }
  }

  public boolean isLoadCanceled()
  {
    return this.loadCanceled;
  }

  public boolean isLoadCompleted()
  {
    return this.loadCompleted;
  }

  public void load()
  {
    if (this.extractor == null)
      this.extractor = buildExtractor();
    maybeLoadInitData();
    if (!this.loadCanceled)
      loadMedia();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.HlsMediaChunk
 * JD-Core Version:    0.6.0
 */