package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class PrivFrame extends Id3Frame
{
  public static final Parcelable.Creator<PrivFrame> CREATOR = new Parcelable.Creator()
  {
    public PrivFrame createFromParcel(Parcel paramParcel)
    {
      return new PrivFrame(paramParcel);
    }

    public PrivFrame[] newArray(int paramInt)
    {
      return new PrivFrame[paramInt];
    }
  };
  public static final String ID = "PRIV";
  public final String owner;
  public final byte[] privateData;

  PrivFrame(Parcel paramParcel)
  {
    super("PRIV");
    this.owner = paramParcel.readString();
    this.privateData = paramParcel.createByteArray();
  }

  public PrivFrame(String paramString, byte[] paramArrayOfByte)
  {
    super("PRIV");
    this.owner = paramString;
    this.privateData = paramArrayOfByte;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (PrivFrame)paramObject;
    }
    while ((Util.areEqual(this.owner, paramObject.owner)) && (Arrays.equals(this.privateData, paramObject.privateData)));
    return false;
  }

  public int hashCode()
  {
    if (this.owner != null);
    for (int i = this.owner.hashCode(); ; i = 0)
      return (i + 527) * 31 + Arrays.hashCode(this.privateData);
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.owner);
    paramParcel.writeByteArray(this.privateData);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.PrivFrame
 * JD-Core Version:    0.6.0
 */