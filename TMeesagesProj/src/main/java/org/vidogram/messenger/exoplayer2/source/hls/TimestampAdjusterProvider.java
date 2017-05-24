package org.vidogram.messenger.exoplayer2.source.hls;

import android.util.SparseArray;
import org.vidogram.messenger.exoplayer2.extractor.TimestampAdjuster;

public final class TimestampAdjusterProvider
{
  private final SparseArray<TimestampAdjuster> timestampAdjusters = new SparseArray();

  public TimestampAdjuster getAdjuster(int paramInt, long paramLong)
  {
    TimestampAdjuster localTimestampAdjuster2 = (TimestampAdjuster)this.timestampAdjusters.get(paramInt);
    TimestampAdjuster localTimestampAdjuster1 = localTimestampAdjuster2;
    if (localTimestampAdjuster2 == null)
    {
      localTimestampAdjuster1 = new TimestampAdjuster(paramLong);
      this.timestampAdjusters.put(paramInt, localTimestampAdjuster1);
    }
    return localTimestampAdjuster1;
  }

  public void reset()
  {
    this.timestampAdjusters.clear();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.TimestampAdjusterProvider
 * JD-Core Version:    0.6.0
 */