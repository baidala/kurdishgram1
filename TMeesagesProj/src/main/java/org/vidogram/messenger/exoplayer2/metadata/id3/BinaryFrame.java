package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class BinaryFrame extends Id3Frame
{
  public static final Parcelable.Creator<BinaryFrame> CREATOR = new Parcelable.Creator()
  {
    public BinaryFrame createFromParcel(Parcel paramParcel)
    {
      return new BinaryFrame(paramParcel);
    }

    public BinaryFrame[] newArray(int paramInt)
    {
      return new BinaryFrame[paramInt];
    }
  };
  public final byte[] data;

  BinaryFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.data = paramParcel.createByteArray();
  }

  public BinaryFrame(String paramString, byte[] paramArrayOfByte)
  {
    super(paramString);
    this.data = paramArrayOfByte;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (BinaryFrame)paramObject;
    }
    while ((this.id.equals(paramObject.id)) && (Arrays.equals(this.data, paramObject.data)));
    return false;
  }

  public int hashCode()
  {
    return (this.id.hashCode() + 527) * 31 + Arrays.hashCode(this.data);
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeByteArray(this.data);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.BinaryFrame
 * JD-Core Version:    0.6.0
 */