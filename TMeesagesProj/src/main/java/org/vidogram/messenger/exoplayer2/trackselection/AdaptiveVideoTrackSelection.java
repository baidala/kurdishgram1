package org.vidogram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.vidogram.messenger.exoplayer2.upstream.BandwidthMeter;

public class AdaptiveVideoTrackSelection extends BaseTrackSelection
{
  public static final float DEFAULT_BANDWIDTH_FRACTION = 0.75F;
  public static final int DEFAULT_MAX_DURATION_FOR_QUALITY_DECREASE_MS = 25000;
  public static final int DEFAULT_MAX_INITIAL_BITRATE = 800000;
  public static final int DEFAULT_MIN_DURATION_FOR_QUALITY_INCREASE_MS = 10000;
  public static final int DEFAULT_MIN_DURATION_TO_RETAIN_AFTER_DISCARD_MS = 25000;
  private final float bandwidthFraction;
  private final BandwidthMeter bandwidthMeter;
  private final long maxDurationForQualityDecreaseUs;
  private final int maxInitialBitrate;
  private final long minDurationForQualityIncreaseUs;
  private final long minDurationToRetainAfterDiscardUs;
  private int reason;
  private int selectedIndex;

  public AdaptiveVideoTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, BandwidthMeter paramBandwidthMeter)
  {
    this(paramTrackGroup, paramArrayOfInt, paramBandwidthMeter, 800000, 10000L, 25000L, 25000L, 0.75F);
  }

  public AdaptiveVideoTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, BandwidthMeter paramBandwidthMeter, int paramInt, long paramLong1, long paramLong2, long paramLong3, float paramFloat)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.bandwidthMeter = paramBandwidthMeter;
    this.maxInitialBitrate = paramInt;
    this.minDurationForQualityIncreaseUs = (paramLong1 * 1000L);
    this.maxDurationForQualityDecreaseUs = (paramLong2 * 1000L);
    this.minDurationToRetainAfterDiscardUs = (paramLong3 * 1000L);
    this.bandwidthFraction = paramFloat;
    this.selectedIndex = determineIdealSelectedIndex(-9223372036854775808L);
    this.reason = 1;
  }

  private int determineIdealSelectedIndex(long paramLong)
  {
    int i = 0;
    long l = this.bandwidthMeter.getBitrateEstimate();
    int j;
    if (l == -1L)
    {
      l = this.maxInitialBitrate;
      j = 0;
    }
    while (true)
    {
      if (i >= this.length)
        break label98;
      if ((paramLong == -9223372036854775808L) || (!isBlacklisted(i, paramLong)))
      {
        if (getFormat(i).bitrate <= l)
        {
          return i;
          l = ()((float)l * this.bandwidthFraction);
          break;
        }
        j = i;
      }
      i += 1;
    }
    label98: return j;
  }

  public int evaluateQueueSize(long paramLong, List<? extends MediaChunk> paramList)
  {
    int i = 0;
    if (paramList.isEmpty())
      return 0;
    int j = paramList.size();
    if (((MediaChunk)paramList.get(j - 1)).endTimeUs - paramLong < this.minDurationToRetainAfterDiscardUs)
      return j;
    Format localFormat = getFormat(determineIdealSelectedIndex(SystemClock.elapsedRealtime()));
    while (i < j)
    {
      MediaChunk localMediaChunk = (MediaChunk)paramList.get(i);
      if ((localMediaChunk.startTimeUs - paramLong >= this.minDurationToRetainAfterDiscardUs) && (localMediaChunk.trackFormat.bitrate < localFormat.bitrate) && (localMediaChunk.trackFormat.height < localFormat.height) && (localMediaChunk.trackFormat.height < 720) && (localMediaChunk.trackFormat.width < 1280))
        return i;
      i += 1;
    }
    return j;
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
    return this.reason;
  }

  public void updateSelectedTrack(long paramLong)
  {
    long l = SystemClock.elapsedRealtime();
    int i = this.selectedIndex;
    Format localFormat1 = getSelectedFormat();
    int j = determineIdealSelectedIndex(l);
    Format localFormat2 = getFormat(j);
    this.selectedIndex = j;
    if ((localFormat1 != null) && (!isBlacklisted(this.selectedIndex, l)))
      if ((localFormat2.bitrate <= localFormat1.bitrate) || (paramLong >= this.minDurationForQualityIncreaseUs))
        break label97;
    for (this.selectedIndex = i; ; this.selectedIndex = i)
      label97: 
      do
      {
        if (this.selectedIndex != i)
          this.reason = 3;
        return;
      }
      while ((localFormat2.bitrate >= localFormat1.bitrate) || (paramLong < this.maxDurationForQualityDecreaseUs));
  }

  public static final class Factory
    implements TrackSelection.Factory
  {
    private final float bandwidthFraction;
    private final BandwidthMeter bandwidthMeter;
    private final int maxDurationForQualityDecreaseMs;
    private final int maxInitialBitrate;
    private final int minDurationForQualityIncreaseMs;
    private final int minDurationToRetainAfterDiscardMs;

    public Factory(BandwidthMeter paramBandwidthMeter)
    {
      this(paramBandwidthMeter, 800000, 10000, 25000, 25000, 0.75F);
    }

    public Factory(BandwidthMeter paramBandwidthMeter, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat)
    {
      this.bandwidthMeter = paramBandwidthMeter;
      this.maxInitialBitrate = paramInt1;
      this.minDurationForQualityIncreaseMs = paramInt2;
      this.maxDurationForQualityDecreaseMs = paramInt3;
      this.minDurationToRetainAfterDiscardMs = paramInt4;
      this.bandwidthFraction = paramFloat;
    }

    public AdaptiveVideoTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      return new AdaptiveVideoTrackSelection(paramTrackGroup, paramArrayOfInt, this.bandwidthMeter, this.maxInitialBitrate, this.minDurationForQualityIncreaseMs, this.maxDurationForQualityDecreaseMs, this.minDurationToRetainAfterDiscardMs, this.bandwidthFraction);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.AdaptiveVideoTrackSelection
 * JD-Core Version:    0.6.0
 */