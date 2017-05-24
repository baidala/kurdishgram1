package org.vidogram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.Random;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;

public final class RandomTrackSelection extends BaseTrackSelection
{
  private final Random random;
  private int selectedIndex;

  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.random = new Random();
    this.selectedIndex = this.random.nextInt(this.length);
  }

  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, long paramLong)
  {
    this(paramTrackGroup, paramArrayOfInt, new Random(paramLong));
  }

  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, Random paramRandom)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.random = paramRandom;
    this.selectedIndex = paramRandom.nextInt(this.length);
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
    return 3;
  }

  public void updateSelectedTrack(long paramLong)
  {
    int m = 0;
    paramLong = SystemClock.elapsedRealtime();
    int i = 0;
    int k;
    for (int j = 0; i < this.length; j = k)
    {
      k = j;
      if (!isBlacklisted(i, paramLong))
        k = j + 1;
      i += 1;
    }
    this.selectedIndex = this.random.nextInt(j);
    if (j != this.length)
    {
      j = 0;
      i = m;
    }
    while (true)
    {
      if (i < this.length)
      {
        k = j;
        if (isBlacklisted(i, paramLong))
          break label120;
        if (this.selectedIndex == j)
          this.selectedIndex = i;
      }
      else
      {
        return;
      }
      k = j + 1;
      label120: i += 1;
      j = k;
    }
  }

  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Random random;

    public Factory()
    {
      this.random = new Random();
    }

    public Factory(int paramInt)
    {
      this.random = new Random(paramInt);
    }

    public RandomTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      return new RandomTrackSelection(paramTrackGroup, paramArrayOfInt, this.random);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.RandomTrackSelection
 * JD-Core Version:    0.6.0
 */