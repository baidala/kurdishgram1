package org.vidogram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public final class SpliceNullCommand extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceNullCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceNullCommand createFromParcel(Parcel paramParcel)
    {
      return new SpliceNullCommand();
    }

    public SpliceNullCommand[] newArray(int paramInt)
    {
      return new SpliceNullCommand[paramInt];
    }
  };

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.scte35.SpliceNullCommand
 * JD-Core Version:    0.6.0
 */