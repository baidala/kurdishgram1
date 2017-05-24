package org.vidogram.messenger.exoplayer2.drm;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.vidogram.messenger.exoplayer2.C;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class DrmInitData
  implements Parcelable, Comparator<SchemeData>
{
  public static final Parcelable.Creator<DrmInitData> CREATOR = new Parcelable.Creator()
  {
    public DrmInitData createFromParcel(Parcel paramParcel)
    {
      return new DrmInitData(paramParcel);
    }

    public DrmInitData[] newArray(int paramInt)
    {
      return new DrmInitData[paramInt];
    }
  };
  private int hashCode;
  public final int schemeDataCount;
  private final SchemeData[] schemeDatas;

  DrmInitData(Parcel paramParcel)
  {
    this.schemeDatas = ((SchemeData[])paramParcel.createTypedArray(SchemeData.CREATOR));
    this.schemeDataCount = this.schemeDatas.length;
  }

  public DrmInitData(List<SchemeData> paramList)
  {
    this(false, (SchemeData[])paramList.toArray(new SchemeData[paramList.size()]));
  }

  private DrmInitData(boolean paramBoolean, SchemeData[] paramArrayOfSchemeData)
  {
    if (paramBoolean)
      paramArrayOfSchemeData = (SchemeData[])paramArrayOfSchemeData.clone();
    while (true)
    {
      Arrays.sort(paramArrayOfSchemeData, this);
      int i = 1;
      while (i < paramArrayOfSchemeData.length)
      {
        if (paramArrayOfSchemeData[(i - 1)].uuid.equals(paramArrayOfSchemeData[i].uuid))
          throw new IllegalArgumentException("Duplicate data for uuid: " + paramArrayOfSchemeData[i].uuid);
        i += 1;
      }
      this.schemeDatas = paramArrayOfSchemeData;
      this.schemeDataCount = paramArrayOfSchemeData.length;
      return;
    }
  }

  public DrmInitData(SchemeData[] paramArrayOfSchemeData)
  {
    this(true, paramArrayOfSchemeData);
  }

  public int compare(SchemeData paramSchemeData1, SchemeData paramSchemeData2)
  {
    if (C.UUID_NIL.equals(paramSchemeData1.uuid))
    {
      if (C.UUID_NIL.equals(paramSchemeData2.uuid))
        return 0;
      return 1;
    }
    return paramSchemeData1.uuid.compareTo(paramSchemeData2.uuid);
  }

  public int describeContents()
  {
    return 0;
  }

  public boolean equals(Object paramObject)
  {
    if (this == paramObject)
      return true;
    if ((paramObject == null) || (getClass() != paramObject.getClass()))
      return false;
    return Arrays.equals(this.schemeDatas, ((DrmInitData)paramObject).schemeDatas);
  }

  public SchemeData get(int paramInt)
  {
    return this.schemeDatas[paramInt];
  }

  public SchemeData get(UUID paramUUID)
  {
    SchemeData[] arrayOfSchemeData = this.schemeDatas;
    int j = arrayOfSchemeData.length;
    int i = 0;
    while (i < j)
    {
      SchemeData localSchemeData = arrayOfSchemeData[i];
      if (localSchemeData.matches(paramUUID))
        return localSchemeData;
      i += 1;
    }
    return null;
  }

  public int hashCode()
  {
    if (this.hashCode == 0)
      this.hashCode = Arrays.hashCode(this.schemeDatas);
    return this.hashCode;
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeTypedArray(this.schemeDatas, 0);
  }

  public static final class SchemeData
    implements Parcelable
  {
    public static final Parcelable.Creator<SchemeData> CREATOR = new Parcelable.Creator()
    {
      public DrmInitData.SchemeData createFromParcel(Parcel paramParcel)
      {
        return new DrmInitData.SchemeData(paramParcel);
      }

      public DrmInitData.SchemeData[] newArray(int paramInt)
      {
        return new DrmInitData.SchemeData[paramInt];
      }
    };
    public final byte[] data;
    private int hashCode;
    public final String mimeType;
    public final boolean requiresSecureDecryption;
    private final UUID uuid;

    SchemeData(Parcel paramParcel)
    {
      this.uuid = new UUID(paramParcel.readLong(), paramParcel.readLong());
      this.mimeType = paramParcel.readString();
      this.data = paramParcel.createByteArray();
      if (paramParcel.readByte() != 0);
      for (boolean bool = true; ; bool = false)
      {
        this.requiresSecureDecryption = bool;
        return;
      }
    }

    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte)
    {
      this(paramUUID, paramString, paramArrayOfByte, false);
    }

    public SchemeData(UUID paramUUID, String paramString, byte[] paramArrayOfByte, boolean paramBoolean)
    {
      this.uuid = ((UUID)Assertions.checkNotNull(paramUUID));
      this.mimeType = ((String)Assertions.checkNotNull(paramString));
      this.data = ((byte[])Assertions.checkNotNull(paramArrayOfByte));
      this.requiresSecureDecryption = paramBoolean;
    }

    public int describeContents()
    {
      return 0;
    }

    public boolean equals(Object paramObject)
    {
      int j = 1;
      int i;
      if (!(paramObject instanceof SchemeData))
        i = 0;
      do
      {
        do
        {
          return i;
          i = j;
        }
        while (paramObject == this);
        paramObject = (SchemeData)paramObject;
        if ((!this.mimeType.equals(paramObject.mimeType)) || (!Util.areEqual(this.uuid, paramObject.uuid)))
          break;
        i = j;
      }
      while (Arrays.equals(this.data, paramObject.data));
      return false;
    }

    public int hashCode()
    {
      if (this.hashCode == 0)
        this.hashCode = ((this.uuid.hashCode() * 31 + this.mimeType.hashCode()) * 31 + Arrays.hashCode(this.data));
      return this.hashCode;
    }

    public boolean matches(UUID paramUUID)
    {
      return (C.UUID_NIL.equals(this.uuid)) || (paramUUID.equals(this.uuid));
    }

    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeLong(this.uuid.getMostSignificantBits());
      paramParcel.writeLong(this.uuid.getLeastSignificantBits());
      paramParcel.writeString(this.mimeType);
      paramParcel.writeByteArray(this.data);
      if (this.requiresSecureDecryption);
      for (paramInt = 1; ; paramInt = 0)
      {
        paramParcel.writeByte((byte)paramInt);
        return;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.drm.DrmInitData
 * JD-Core Version:    0.6.0
 */