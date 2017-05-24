package org.vidogram.messenger.exoplayer2.metadata.scte35;

import android.text.TextUtils;
import org.vidogram.messenger.exoplayer2.metadata.Metadata;
import org.vidogram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.vidogram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.vidogram.messenger.exoplayer2.util.ParsableBitArray;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;

public final class SpliceInfoDecoder
  implements MetadataDecoder
{
  private static final int TYPE_PRIVATE_COMMAND = 255;
  private static final int TYPE_SPLICE_INSERT = 5;
  private static final int TYPE_SPLICE_NULL = 0;
  private static final int TYPE_SPLICE_SCHEDULE = 4;
  private static final int TYPE_TIME_SIGNAL = 6;
  private final ParsableByteArray sectionData = new ParsableByteArray();
  private final ParsableBitArray sectionHeader = new ParsableBitArray();

  public boolean canDecode(String paramString)
  {
    return TextUtils.equals(paramString, "application/x-scte35");
  }

  public Metadata decode(byte[] paramArrayOfByte, int paramInt)
  {
    this.sectionData.reset(paramArrayOfByte, paramInt);
    this.sectionHeader.reset(paramArrayOfByte, paramInt);
    this.sectionHeader.skipBits(39);
    long l = this.sectionHeader.readBits(1);
    l = this.sectionHeader.readBits(32) | l << 32;
    this.sectionHeader.skipBits(20);
    paramInt = this.sectionHeader.readBits(12);
    int i = this.sectionHeader.readBits(8);
    this.sectionData.skipBytes(14);
    switch (i)
    {
    default:
      paramArrayOfByte = null;
    case 0:
    case 4:
    case 5:
    case 6:
    case 255:
    }
    while (paramArrayOfByte == null)
    {
      return new Metadata(new Metadata.Entry[0]);
      paramArrayOfByte = new SpliceNullCommand();
      continue;
      paramArrayOfByte = SpliceScheduleCommand.parseFromSection(this.sectionData);
      continue;
      paramArrayOfByte = SpliceInsertCommand.parseFromSection(this.sectionData, l);
      continue;
      paramArrayOfByte = TimeSignalCommand.parseFromSection(this.sectionData, l);
      continue;
      paramArrayOfByte = PrivateCommand.parseFromSection(this.sectionData, paramInt, l);
    }
    return new Metadata(new Metadata.Entry[] { paramArrayOfByte });
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.metadata.scte35.SpliceInfoDecoder
 * JD-Core Version:    0.6.0
 */