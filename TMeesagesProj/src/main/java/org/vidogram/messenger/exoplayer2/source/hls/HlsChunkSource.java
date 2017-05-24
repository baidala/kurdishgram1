package org.vidogram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.os.SystemClock;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;
import org.vidogram.messenger.exoplayer2.source.BehindLiveWindowException;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.source.chunk.Chunk;
import org.vidogram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.vidogram.messenger.exoplayer2.source.chunk.DataChunk;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import org.vidogram.messenger.exoplayer2.trackselection.BaseTrackSelection;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.DataSpec;
import org.vidogram.messenger.exoplayer2.util.UriUtil;
import org.vidogram.messenger.exoplayer2.util.Util;

class HlsChunkSource
{
  private final DataSource dataSource;
  private byte[] encryptionIv;
  private String encryptionIvString;
  private byte[] encryptionKey;
  private Uri encryptionKeyUri;
  private IOException fatalError;
  private boolean isTimestampMaster;
  private final HlsPlaylistTracker playlistTracker;
  private byte[] scratchSpace;
  private final TimestampAdjusterProvider timestampAdjusterProvider;
  private final TrackGroup trackGroup;
  private TrackSelection trackSelection;
  private final HlsMasterPlaylist.HlsUrl[] variants;

  public HlsChunkSource(HlsPlaylistTracker paramHlsPlaylistTracker, HlsMasterPlaylist.HlsUrl[] paramArrayOfHlsUrl, DataSource paramDataSource, TimestampAdjusterProvider paramTimestampAdjusterProvider)
  {
    this.playlistTracker = paramHlsPlaylistTracker;
    this.variants = paramArrayOfHlsUrl;
    this.dataSource = paramDataSource;
    this.timestampAdjusterProvider = paramTimestampAdjusterProvider;
    paramHlsPlaylistTracker = new Format[paramArrayOfHlsUrl.length];
    paramDataSource = new int[paramArrayOfHlsUrl.length];
    int i = 0;
    while (i < paramArrayOfHlsUrl.length)
    {
      paramHlsPlaylistTracker[i] = paramArrayOfHlsUrl[i].format;
      paramDataSource[i] = i;
      i += 1;
    }
    this.trackGroup = new TrackGroup(paramHlsPlaylistTracker);
    this.trackSelection = new InitializationTrackSelection(this.trackGroup, paramDataSource);
  }

  private void clearEncryptionData()
  {
    this.encryptionKeyUri = null;
    this.encryptionKey = null;
    this.encryptionIvString = null;
    this.encryptionIv = null;
  }

  private EncryptionKeyChunk newEncryptionKeyChunk(Uri paramUri, String paramString, int paramInt1, int paramInt2, Object paramObject)
  {
    paramUri = new DataSpec(paramUri, 0L, -1L, null, 1);
    return new EncryptionKeyChunk(this.dataSource, paramUri, this.variants[paramInt1].format, paramInt2, paramObject, this.scratchSpace, paramString);
  }

  private void setEncryptionData(Uri paramUri, String paramString, byte[] paramArrayOfByte)
  {
    Object localObject;
    byte[] arrayOfByte;
    if (paramString.toLowerCase(Locale.getDefault()).startsWith("0x"))
    {
      localObject = paramString.substring(2);
      localObject = new BigInteger((String)localObject, 16).toByteArray();
      arrayOfByte = new byte[16];
      if (localObject.length <= 16)
        break label113;
    }
    label113: for (int i = localObject.length - 16; ; i = 0)
    {
      System.arraycopy(localObject, i, arrayOfByte, arrayOfByte.length - localObject.length + i, localObject.length - i);
      this.encryptionKeyUri = paramUri;
      this.encryptionKey = paramArrayOfByte;
      this.encryptionIvString = paramString;
      this.encryptionIv = arrayOfByte;
      return;
      localObject = paramString;
      break;
    }
  }

  public void getNextChunk(HlsMediaChunk paramHlsMediaChunk, long paramLong, HlsChunkHolder paramHlsChunkHolder)
  {
    long l;
    label14: int n;
    if (paramHlsMediaChunk == null)
    {
      j = -1;
      if (paramHlsMediaChunk != null)
        break label96;
      l = 0L;
      this.trackSelection.updateSelectedTrack(l);
      n = this.trackSelection.getSelectedIndexInTrackGroup();
      if (j == n)
        break label111;
    }
    Object localObject2;
    label96: label111: for (int m = 1; ; m = 0)
    {
      localObject2 = this.playlistTracker.getPlaylistSnapshot(this.variants[n]);
      if (localObject2 != null)
        break label117;
      paramHlsChunkHolder.playlist = this.variants[n];
      return;
      j = this.trackGroup.indexOf(paramHlsMediaChunk.trackFormat);
      break;
      l = Math.max(0L, paramHlsMediaChunk.getAdjustedStartTimeUs() - paramLong);
      break label14;
    }
    label117: int i;
    int k;
    Object localObject1;
    if ((paramHlsMediaChunk == null) || (m != 0))
      if (paramHlsMediaChunk == null)
      {
        if ((((HlsMediaPlaylist)localObject2).hasEndTag) || (paramLong <= ((HlsMediaPlaylist)localObject2).getEndTimeUs()))
          break label204;
        i = ((HlsMediaPlaylist)localObject2).mediaSequence + ((HlsMediaPlaylist)localObject2).segments.size();
        k = n;
        localObject1 = localObject2;
      }
    while (true)
    {
      if (i >= ((HlsMediaPlaylist)localObject1).mediaSequence)
        break label338;
      this.fatalError = new BehindLiveWindowException();
      return;
      paramLong = paramHlsMediaChunk.startTimeUs;
      break;
      label204: localObject1 = ((HlsMediaPlaylist)localObject2).segments;
      if ((!this.playlistTracker.isLive()) || (paramHlsMediaChunk == null));
      for (boolean bool = true; ; bool = false)
      {
        int i1 = Util.binarySearchFloor((List)localObject1, Long.valueOf(paramLong), true, bool) + ((HlsMediaPlaylist)localObject2).mediaSequence;
        i = i1;
        localObject1 = localObject2;
        k = n;
        if (i1 >= ((HlsMediaPlaylist)localObject2).mediaSequence)
          break;
        i = i1;
        localObject1 = localObject2;
        k = n;
        if (paramHlsMediaChunk == null)
          break;
        localObject1 = this.playlistTracker.getPlaylistSnapshot(this.variants[j]);
        i = paramHlsMediaChunk.getNextChunkIndex();
        k = j;
        break;
      }
      i = paramHlsMediaChunk.getNextChunkIndex();
      localObject1 = localObject2;
      k = n;
    }
    label338: int j = i - ((HlsMediaPlaylist)localObject1).mediaSequence;
    if (j >= ((HlsMediaPlaylist)localObject1).segments.size())
    {
      if (((HlsMediaPlaylist)localObject1).hasEndTag)
      {
        paramHlsChunkHolder.endOfStream = true;
        return;
      }
      paramHlsChunkHolder.playlist = this.variants[k];
      return;
    }
    HlsMediaPlaylist.Segment localSegment1 = (HlsMediaPlaylist.Segment)((HlsMediaPlaylist)localObject1).segments.get(j);
    if (localSegment1.isEncrypted)
    {
      localObject2 = UriUtil.resolveToUri(((HlsMediaPlaylist)localObject1).baseUri, localSegment1.encryptionKeyUri);
      if (!((Uri)localObject2).equals(this.encryptionKeyUri))
      {
        paramHlsChunkHolder.chunk = newEncryptionKeyChunk((Uri)localObject2, localSegment1.encryptionIV, k, this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData());
        return;
      }
      if (!Util.areEqual(localSegment1.encryptionIV, this.encryptionIvString))
        setEncryptionData((Uri)localObject2, localSegment1.encryptionIV, this.encryptionKey);
    }
    while (true)
    {
      l = localSegment1.startTimeUs;
      paramLong = l;
      if (paramHlsMediaChunk != null)
      {
        paramLong = l;
        if (m == 0)
          paramLong = paramHlsMediaChunk.getAdjustedEndTimeUs();
      }
      Uri localUri = UriUtil.resolveToUri(((HlsMediaPlaylist)localObject1).baseUri, localSegment1.url);
      TimestampAdjuster localTimestampAdjuster = this.timestampAdjusterProvider.getAdjuster(localSegment1.discontinuitySequenceNumber, paramLong);
      localObject2 = null;
      HlsMediaPlaylist.Segment localSegment2 = ((HlsMediaPlaylist)localObject1).initializationSegment;
      if (localSegment2 != null)
        localObject2 = new DataSpec(UriUtil.resolveToUri(((HlsMediaPlaylist)localObject1).baseUri, localSegment2.url), localSegment2.byterangeOffset, localSegment2.byterangeLength, null);
      localObject1 = new DataSpec(localUri, localSegment1.byterangeOffset, localSegment1.byterangeLength, null);
      paramHlsChunkHolder.chunk = new HlsMediaChunk(this.dataSource, (DataSpec)localObject1, (DataSpec)localObject2, this.variants[k], this.trackSelection.getSelectionReason(), this.trackSelection.getSelectionData(), localSegment1, i, this.isTimestampMaster, localTimestampAdjuster, paramHlsMediaChunk, this.encryptionKey, this.encryptionIv);
      return;
      clearEncryptionData();
    }
  }

  public TrackGroup getTrackGroup()
  {
    return this.trackGroup;
  }

  public void maybeThrowError()
  {
    if (this.fatalError != null)
      throw this.fatalError;
  }

  public void onChunkLoadCompleted(Chunk paramChunk)
  {
    if ((paramChunk instanceof HlsMediaChunk))
    {
      paramChunk = (HlsMediaChunk)paramChunk;
      this.playlistTracker.onChunkLoaded(paramChunk.hlsUrl, paramChunk.chunkIndex, paramChunk.getAdjustedStartTimeUs());
    }
    do
      return;
    while (!(paramChunk instanceof EncryptionKeyChunk));
    paramChunk = (EncryptionKeyChunk)paramChunk;
    this.scratchSpace = paramChunk.getDataHolder();
    setEncryptionData(paramChunk.dataSpec.uri, paramChunk.iv, paramChunk.getResult());
  }

  public boolean onChunkLoadError(Chunk paramChunk, boolean paramBoolean, IOException paramIOException)
  {
    return (paramBoolean) && (ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(this.trackGroup.indexOf(paramChunk.trackFormat)), paramIOException));
  }

  public void onPlaylistLoadError(HlsMasterPlaylist.HlsUrl paramHlsUrl, IOException paramIOException)
  {
    int i = this.trackGroup.indexOf(paramHlsUrl.format);
    if (i == -1)
      return;
    ChunkedTrackBlacklistUtil.maybeBlacklistTrack(this.trackSelection, this.trackSelection.indexOf(i), paramIOException);
  }

  public void reset()
  {
    this.fatalError = null;
  }

  public void selectTracks(TrackSelection paramTrackSelection)
  {
    this.trackSelection = paramTrackSelection;
  }

  public void setIsTimestampMaster(boolean paramBoolean)
  {
    this.isTimestampMaster = paramBoolean;
  }

  private static final class EncryptionKeyChunk extends DataChunk
  {
    public final String iv;
    private byte[] result;

    public EncryptionKeyChunk(DataSource paramDataSource, DataSpec paramDataSpec, Format paramFormat, int paramInt, Object paramObject, byte[] paramArrayOfByte, String paramString)
    {
      super(paramDataSpec, 3, paramFormat, paramInt, paramObject, paramArrayOfByte);
      this.iv = paramString;
    }

    protected void consume(byte[] paramArrayOfByte, int paramInt)
    {
      this.result = Arrays.copyOf(paramArrayOfByte, paramInt);
    }

    public byte[] getResult()
    {
      return this.result;
    }
  }

  public static final class HlsChunkHolder
  {
    public Chunk chunk;
    public boolean endOfStream;
    public HlsMasterPlaylist.HlsUrl playlist;

    public HlsChunkHolder()
    {
      clear();
    }

    public void clear()
    {
      this.chunk = null;
      this.endOfStream = false;
      this.playlist = null;
    }
  }

  private static final class InitializationTrackSelection extends BaseTrackSelection
  {
    private int selectedIndex = indexOf(paramTrackGroup.getFormat(0));

    public InitializationTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      super(paramArrayOfInt);
    }

    public int getSelectedIndex()
    {
      return this.selectedIndex;
    }

    public Object getSelectionData()
    {
      return null;
    }

    public int getSelectionReason()
    {
      return 0;
    }

    public void updateSelectedTrack(long paramLong)
    {
      paramLong = SystemClock.elapsedRealtime();
      if (!isBlacklisted(this.selectedIndex, paramLong))
        return;
      int i = this.length - 1;
      while (i >= 0)
      {
        if (!isBlacklisted(i, paramLong))
        {
          this.selectedIndex = i;
          return;
        }
        i -= 1;
      }
      throw new IllegalStateException();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.HlsChunkSource
 * JD-Core Version:    0.6.0
 */