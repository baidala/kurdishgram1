package org.vidogram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class TimeSignalCommand extends SpliceCommand
{
  public static final Parcelable.Creator<TimeSignalCommand> CREATOR = new Parcelable.Creator()
  {
    public TimeSignalCommand createFromParcel(Parcel paramParcel)
    {
      return new TimeSignalCommand(paramParcel.readLong(), null);
    }

    public TimeSignalCommand[] newArray(int paramInt)
    {
      return new TimeSignalCommand[paramInt];
    }
  };
  public final long ptsTime;

  private TimeSignalCommand(long paramLong)
  {
    this.ptsTime = paramLong;
  }

  static TimeSignalCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    return new TimeSignalCommand(parseSpliceTime(paramParsableByteArray, paramLong));
  }

  static long parseSpliceTime(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    long l2 = paramParsableByteArray.readUnsignedByte();
    long l1 = -9223372036854775807L;
    if ((0x80 & l2) != 0L)
      l1 = ((1L & l2) << 32 | paramParsableByteArray.readUnsignedInt()) + paramLong & 0xFFFFFFFF;
    return l1;
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.ptsTime);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.scte35.TimeSignalCommand
 * JD-Core Version:    0.6.0
 */