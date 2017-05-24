package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class TxxxFrame extends Id3Frame
{
  public static final Parcelable.Creator<TxxxFrame> CREATOR = new Parcelable.Creator()
  {
    public TxxxFrame createFromParcel(Parcel paramParcel)
    {
      return new TxxxFrame(paramParcel);
    }

    public TxxxFrame[] newArray(int paramInt)
    {
      return new TxxxFrame[paramInt];
    }
  };
  public static final String ID = "TXXX";
  public final String description;
  public final String value;

  TxxxFrame(Parcel paramParcel)
  {
    super("TXXX");
    this.description = paramParcel.readString();
    this.value = paramParcel.readString();
  }

  public TxxxFrame(String paramString1, String paramString2)
  {
    super("TXXX");
    this.description = paramString1;
    this.value = paramString2;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (TxxxFrame)paramObject;
    }
    while ((Util.areEqual(this.description, paramObject.description)) && (Util.areEqual(this.value, paramObject.value)));
    return false;
  }

  public int hashCode()
  {
    int j = 0;
    if (this.description != null);
    for (int i = this.description.hashCode(); ; i = 0)
    {
      if (this.value != null)
        j = this.value.hashCode();
      return (i + 527) * 31 + j;
    }
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.description);
    paramParcel.writeString(this.value);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.TxxxFrame
 * JD-Core Version:    0.6.0
 */