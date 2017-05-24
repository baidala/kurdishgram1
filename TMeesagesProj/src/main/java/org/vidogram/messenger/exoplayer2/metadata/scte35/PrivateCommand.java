package org.vidogram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class PrivateCommand extends SpliceCommand
{
  public static final Parcelable.Creator<PrivateCommand> CREATOR = new Parcelable.Creator()
  {
    public PrivateCommand createFromParcel(Parcel paramParcel)
    {
      return new PrivateCommand(paramParcel, null);
    }

    public PrivateCommand[] newArray(int paramInt)
    {
      return new PrivateCommand[paramInt];
    }
  };
  public final byte[] commandBytes;
  public final long identifier;
  public final long ptsAdjustment;

  private PrivateCommand(long paramLong1, byte[] paramArrayOfByte, long paramLong2)
  {
    this.ptsAdjustment = paramLong2;
    this.identifier = paramLong1;
    this.commandBytes = paramArrayOfByte;
  }

  private PrivateCommand(Parcel paramParcel)
  {
    this.ptsAdjustment = paramParcel.readLong();
    this.identifier = paramParcel.readLong();
    this.commandBytes = new byte[paramParcel.readInt()];
    paramParcel.readByteArray(this.commandBytes);
  }

  static PrivateCommand parseFromSection(ParsableByteArray paramParsableByteArray, int paramInt, long paramLong)
  {
    long l = paramParsableByteArray.readUnsignedInt();
    byte[] arrayOfByte = new byte[paramInt - 4];
    paramParsableByteArray.readBytes(arrayOfByte, 0, arrayOfByte.length);
    return new PrivateCommand(l, arrayOfByte, paramLong);
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.ptsAdjustment);
    paramParcel.writeLong(this.identifier);
    paramParcel.writeInt(this.commandBytes.length);
    paramParcel.writeByteArray(this.commandBytes);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.scte35.PrivateCommand
 * JD-Core Version:    0.6.0
 */