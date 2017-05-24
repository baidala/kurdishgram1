package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class ApicFrame extends Id3Frame
{
  public static final Parcelable.Creator<ApicFrame> CREATOR = new Parcelable.Creator()
  {
    public ApicFrame createFromParcel(Parcel paramParcel)
    {
      return new ApicFrame(paramParcel);
    }

    public ApicFrame[] newArray(int paramInt)
    {
      return new ApicFrame[paramInt];
    }
  };
  public static final String ID = "APIC";
  public final String description;
  public final String mimeType;
  public final byte[] pictureData;
  public final int pictureType;

  ApicFrame(Parcel paramParcel)
  {
    super("APIC");
    this.mimeType = paramParcel.readString();
    this.description = paramParcel.readString();
    this.pictureType = paramParcel.readInt();
    this.pictureData = paramParcel.createByteArray();
  }

  public ApicFrame(String paramString1, String paramString2, int paramInt, byte[] paramArrayOfByte)
  {
    super("APIC");
    this.mimeType = paramString1;
    this.description = paramString2;
    this.pictureType = paramInt;
    this.pictureData = paramArrayOfByte;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (ApicFrame)paramObject;
    }
    while ((this.pictureType == paramObject.pictureType) && (Util.areEqual(this.mimeType, paramObject.mimeType)) && (Util.areEqual(this.description, paramObject.description)) && (Arrays.equals(this.pictureData, paramObject.pictureData)));
    return false;
  }

  public int hashCode()
  {
    int j = 0;
    int k = this.pictureType;
    if (this.mimeType != null);
    for (int i = this.mimeType.hashCode(); ; i = 0)
    {
      if (this.description != null)
        j = this.description.hashCode();
      return ((i + (k + 527) * 31) * 31 + j) * 31 + Arrays.hashCode(this.pictureData);
    }
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mimeType);
    paramParcel.writeString(this.description);
    paramParcel.writeInt(this.pictureType);
    paramParcel.writeByteArray(this.pictureData);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.ApicFrame
 * JD-Core Version:    0.6.0
 */