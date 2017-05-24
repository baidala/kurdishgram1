package org.vidogram.messenger.exoplayer2.trackselection;

import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class FixedTrackSelection extends BaseTrackSelection
{
  private final Object data;
  private final int reason;

  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt)
  {
    this(paramTrackGroup, paramInt, 0, null);
  }

  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, Object paramObject)
  {
    super(paramTrackGroup, new int[] { paramInt1 });
    this.reason = paramInt2;
    this.data = paramObject;
  }

  public int getSelectedIndex()
  {
    return 0;
  }

  public Object getSelectionData()
  {
    return this.data;
  }

  public int getSelectionReason()
  {
    return this.reason;
  }

  public void updateSelectedTrack(long paramLong)
  {
  }

  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Object data;
    private final int reason;

    public Factory()
    {
      this.reason = 0;
      this.data = null;
    }

    public Factory(int paramInt, Object paramObject)
    {
      this.reason = paramInt;
      this.data = paramObject;
    }

    public FixedTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt)
    {
      boolean bool = true;
      if (paramArrayOfInt.length == 1);
      while (true)
      {
        Assertions.checkArgument(bool);
        return new FixedTrackSelection(paramTrackGroup, paramArrayOfInt[0], this.reason, this.data);
        bool = false;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.FixedTrackSelection
 * JD-Core Version:    0.6.0
 */