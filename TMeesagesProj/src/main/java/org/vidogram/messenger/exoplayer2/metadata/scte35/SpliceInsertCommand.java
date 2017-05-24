package org.vidogram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceInsertCommand extends SpliceCommand
{
  public static final Parcelable.Creator<SpliceInsertCommand> CREATOR = new Parcelable.Creator()
  {
    public SpliceInsertCommand createFromParcel(Parcel paramParcel)
    {
      return new SpliceInsertCommand(paramParcel, null);
    }

    public SpliceInsertCommand[] newArray(int paramInt)
    {
      return new SpliceInsertCommand[paramInt];
    }
  };
  public final boolean autoReturn;
  public final int availNum;
  public final int availsExpected;
  public final long breakDuration;
  public final List<ComponentSplice> componentSpliceList;
  public final boolean outOfNetworkIndicator;
  public final boolean programSpliceFlag;
  public final long programSplicePts;
  public final boolean spliceEventCancelIndicator;
  public final long spliceEventId;
  public final boolean spliceImmediateFlag;
  public final int uniqueProgramId;

  private SpliceInsertCommand(long paramLong1, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, long paramLong2, List<ComponentSplice> paramList, boolean paramBoolean5, long paramLong3, int paramInt1, int paramInt2, int paramInt3)
  {
    this.spliceEventId = paramLong1;
    this.spliceEventCancelIndicator = paramBoolean1;
    this.outOfNetworkIndicator = paramBoolean2;
    this.programSpliceFlag = paramBoolean3;
    this.spliceImmediateFlag = paramBoolean4;
    this.programSplicePts = paramLong2;
    this.componentSpliceList = Collections.unmodifiableList(paramList);
    this.autoReturn = paramBoolean5;
    this.breakDuration = paramLong3;
    this.uniqueProgramId = paramInt1;
    this.availNum = paramInt2;
    this.availsExpected = paramInt3;
  }

  private SpliceInsertCommand(Parcel paramParcel)
  {
    this.spliceEventId = paramParcel.readLong();
    if (paramParcel.readByte() == 1)
    {
      bool1 = true;
      this.spliceEventCancelIndicator = bool1;
      if (paramParcel.readByte() != 1)
        break label138;
      bool1 = true;
      label43: this.outOfNetworkIndicator = bool1;
      if (paramParcel.readByte() != 1)
        break label144;
      bool1 = true;
      label60: this.programSpliceFlag = bool1;
      if (paramParcel.readByte() != 1)
        break label150;
    }
    ArrayList localArrayList;
    label138: label144: label150: for (boolean bool1 = true; ; bool1 = false)
    {
      this.spliceImmediateFlag = bool1;
      this.programSplicePts = paramParcel.readLong();
      int j = paramParcel.readInt();
      localArrayList = new ArrayList(j);
      int i = 0;
      while (i < j)
      {
        localArrayList.add(ComponentSplice.createFromParcel(paramParcel));
        i += 1;
      }
      bool1 = false;
      break;
      bool1 = false;
      break label43;
      bool1 = false;
      break label60;
    }
    this.componentSpliceList = Collections.unmodifiableList(localArrayList);
    if (paramParcel.readByte() == 1);
    for (bool1 = bool2; ; bool1 = false)
    {
      this.autoReturn = bool1;
      this.breakDuration = paramParcel.readLong();
      this.uniqueProgramId = paramParcel.readInt();
      this.availNum = paramParcel.readInt();
      this.availsExpected = paramParcel.readInt();
      return;
    }
  }

  static SpliceInsertCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    long l3 = paramParsableByteArray.readUnsignedInt();
    boolean bool5;
    long l2;
    Object localObject;
    int i;
    int j;
    int k;
    boolean bool4;
    boolean bool1;
    label68: boolean bool2;
    if ((paramParsableByteArray.readUnsignedByte() & 0x80) != 0)
    {
      bool5 = true;
      l2 = -9223372036854775807L;
      localObject = new ArrayList();
      i = 0;
      j = 0;
      k = 0;
      bool4 = false;
      if (bool5)
        break label339;
      j = paramParsableByteArray.readUnsignedByte();
      if ((j & 0x80) == 0)
        break label217;
      bool1 = true;
      if ((j & 0x40) == 0)
        break label223;
      bool2 = true;
      label79: if ((j & 0x20) == 0)
        break label229;
      i = 1;
      label89: if ((j & 0x10) == 0)
        break label234;
    }
    long l1;
    label217: label223: label229: label234: for (boolean bool3 = true; ; bool3 = false)
    {
      l1 = l2;
      if (bool2)
      {
        l1 = l2;
        if (!bool3)
          l1 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
      }
      if (bool2)
        break label240;
      k = paramParsableByteArray.readUnsignedByte();
      ArrayList localArrayList = new ArrayList(k);
      j = 0;
      while (true)
      {
        localObject = localArrayList;
        if (j >= k)
          break;
        int m = paramParsableByteArray.readUnsignedByte();
        l2 = -9223372036854775807L;
        if (!bool3)
          l2 = TimeSignalCommand.parseSpliceTime(paramParsableByteArray, paramLong);
        localArrayList.add(new ComponentSplice(m, l2, null));
        j += 1;
      }
      bool5 = false;
      break;
      bool1 = false;
      break label68;
      bool2 = false;
      break label79;
      i = 0;
      break label89;
    }
    label240: if (i != 0)
    {
      paramLong = paramParsableByteArray.readUnsignedByte();
      if ((0x80 & paramLong) != 0L)
      {
        bool4 = true;
        l2 = paramParsableByteArray.readUnsignedInt();
        paramLong = (paramLong & 1L) << 32 | l2;
        i = paramParsableByteArray.readUnsignedShort();
        j = paramParsableByteArray.readUnsignedByte();
        k = paramParsableByteArray.readUnsignedByte();
      }
    }
    while (true)
    {
      label279: return new SpliceInsertCommand(l3, bool5, bool1, bool2, bool3, l1, (List)localObject, bool4, paramLong, i, j, k);
      bool4 = false;
      break;
      paramLong = -9223372036854775807L;
      break label279;
      label339: paramLong = -9223372036854775807L;
      bool3 = false;
      bool2 = false;
      bool1 = false;
      bool4 = false;
      l1 = l2;
    }
  }

  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeLong(this.spliceEventId);
    if (this.spliceEventCancelIndicator)
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (!this.outOfNetworkIndicator)
        break label132;
      paramInt = 1;
      label34: paramParcel.writeByte((byte)paramInt);
      if (!this.programSpliceFlag)
        break label137;
      paramInt = 1;
      label49: paramParcel.writeByte((byte)paramInt);
      if (!this.spliceImmediateFlag)
        break label142;
    }
    label132: label137: label142: for (paramInt = 1; ; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.programSplicePts);
      int j = this.componentSpliceList.size();
      paramParcel.writeInt(j);
      paramInt = 0;
      while (paramInt < j)
      {
        ((ComponentSplice)this.componentSpliceList.get(paramInt)).writeToParcel(paramParcel);
        paramInt += 1;
      }
      paramInt = 0;
      break;
      paramInt = 0;
      break label34;
      paramInt = 0;
      break label49;
    }
    if (this.autoReturn);
    for (paramInt = i; ; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeLong(this.breakDuration);
      paramParcel.writeInt(this.uniqueProgramId);
      paramParcel.writeInt(this.availNum);
      paramParcel.writeInt(this.availsExpected);
      return;
    }
  }

  public static final class ComponentSplice
  {
    public final long componentSplicePts;
    public final int componentTag;

    private ComponentSplice(int paramInt, long paramLong)
    {
      this.componentTag = paramInt;
      this.componentSplicePts = paramLong;
    }

    public static ComponentSplice createFromParcel(Parcel paramParcel)
    {
      return new ComponentSplice(paramParcel.readInt(), paramParcel.readLong());
    }

    public void writeToParcel(Parcel paramParcel)
    {
      paramParcel.writeInt(this.componentTag);
      paramParcel.writeLong(this.componentSplicePts);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.scte35.SpliceInsertCommand
 * JD-Core Version:    0.6.0
 */