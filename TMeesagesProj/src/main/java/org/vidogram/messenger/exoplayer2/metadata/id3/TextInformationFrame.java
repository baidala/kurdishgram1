package org.vidogram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class TextInformationFrame extends Id3Frame
{
  public static final Parcelable.Creator<TextInformationFrame> CREATOR = new Parcelable.Creator()
  {
    public TextInformationFrame createFromParcel(Parcel paramParcel)
    {
      return new TextInformationFrame(paramParcel);
    }

    public TextInformationFrame[] newArray(int paramInt)
    {
      return new TextInformationFrame[paramInt];
    }
  };
  public final String description;

  TextInformationFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.description = paramParcel.readString();
  }

  public TextInformationFrame(String paramString1, String paramString2)
  {
    super(paramString1);
    this.description = paramString2;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject);
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
        return false;
      paramObject = (TextInformationFrame)paramObject;
    }
    while ((this.id.equals(paramObject.id)) && (Util.areEqual(this.description, paramObject.description)));
    return false;
  }

  public int hashCode()
  {
    int j = this.id.hashCode();
    if (this.description != null);
    for (int i = this.description.hashCode(); ; i = 0)
      return i + (j + 527) * 31;
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.description);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.id3.TextInformationFrame
 * JD-Core Version:    0.6.0
 */