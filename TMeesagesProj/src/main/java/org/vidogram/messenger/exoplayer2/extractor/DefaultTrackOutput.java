package org.vidogram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.CryptoInfo;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.upstream.Allocation;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.ParsableByteArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public final class DefaultTrackOutput
  implements TrackOutput
{
  private static final int INITIAL_SCRATCH_SIZE = 32;
  private static final int STATE_DISABLED = 2;
  private static final int STATE_ENABLED = 0;
  private static final int STATE_ENABLED_WRITING = 1;
  private final int allocationLength;
  private final Allocator allocator;
  private final LinkedBlockingDeque<Allocation> dataQueue;
  private Format downstreamFormat;
  private final BufferExtrasHolder extrasHolder;
  private final InfoQueue infoQueue;
  private Allocation lastAllocation;
  private int lastAllocationOffset;
  private boolean needKeyframe;
  private boolean pendingSplice;
  private long sampleOffsetUs;
  private final ParsableByteArray scratch;
  private final AtomicInteger state;
  private long totalBytesDropped;
  private long totalBytesWritten;
  private UpstreamFormatChangedListener upstreamFormatChangeListener;

  public DefaultTrackOutput(Allocator paramAllocator)
  {
    this.allocator = paramAllocator;
    this.allocationLength = paramAllocator.getIndividualAllocationLength();
    this.infoQueue = new InfoQueue();
    this.dataQueue = new LinkedBlockingDeque();
    this.extrasHolder = new BufferExtrasHolder(null);
    this.scratch = new ParsableByteArray(32);
    this.state = new AtomicInteger();
    this.lastAllocationOffset = this.allocationLength;
    this.needKeyframe = true;
  }

  private void clearSampleData()
  {
    this.infoQueue.clearSampleData();
    this.allocator.release((Allocation[])this.dataQueue.toArray(new Allocation[this.dataQueue.size()]));
    this.dataQueue.clear();
    this.allocator.trim();
    this.totalBytesDropped = 0L;
    this.totalBytesWritten = 0L;
    this.lastAllocation = null;
    this.lastAllocationOffset = this.allocationLength;
    this.needKeyframe = true;
  }

  private void dropDownstreamTo(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped) / this.allocationLength;
    int i = 0;
    while (i < j)
    {
      this.allocator.release((Allocation)this.dataQueue.remove());
      this.totalBytesDropped += this.allocationLength;
      i += 1;
    }
  }

  private void dropUpstreamFrom(long paramLong)
  {
    int j = (int)(paramLong - this.totalBytesDropped);
    int i = j / this.allocationLength;
    int k = j % this.allocationLength;
    i = this.dataQueue.size() - i - 1;
    if (k == 0)
      i += 1;
    while (true)
    {
      j = 0;
      while (j < i)
      {
        this.allocator.release((Allocation)this.dataQueue.removeLast());
        j += 1;
      }
      this.lastAllocation = ((Allocation)this.dataQueue.peekLast());
      if (k == 0);
      for (i = this.allocationLength; ; i = k)
      {
        this.lastAllocationOffset = i;
        return;
      }
    }
  }

  private void endWriteOperation()
  {
    if (!this.state.compareAndSet(1, 0))
      clearSampleData();
  }

  private static Format getAdjustedSampleFormat(Format paramFormat, long paramLong)
  {
    Format localFormat;
    if (paramFormat == null)
      localFormat = null;
    do
    {
      do
      {
        return localFormat;
        localFormat = paramFormat;
      }
      while (paramLong == 0L);
      localFormat = paramFormat;
    }
    while (paramFormat.subsampleOffsetUs == 9223372036854775807L);
    return paramFormat.copyWithSubsampleOffsetUs(paramFormat.subsampleOffsetUs + paramLong);
  }

  private int prepareForAppend(int paramInt)
  {
    if (this.lastAllocationOffset == this.allocationLength)
    {
      this.lastAllocationOffset = 0;
      this.lastAllocation = this.allocator.allocate();
      this.dataQueue.add(this.lastAllocation);
    }
    return Math.min(paramInt, this.allocationLength - this.lastAllocationOffset);
  }

  private void readData(long paramLong, ByteBuffer paramByteBuffer, int paramInt)
  {
    while (paramInt > 0)
    {
      dropDownstreamTo(paramLong);
      int i = (int)(paramLong - this.totalBytesDropped);
      int j = Math.min(paramInt, this.allocationLength - i);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      paramByteBuffer.put(localAllocation.data, localAllocation.translateOffset(i), j);
      paramLong += j;
      paramInt -= j;
    }
  }

  private void readData(long paramLong, byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      dropDownstreamTo(paramLong);
      int j = (int)(paramLong - this.totalBytesDropped);
      int k = Math.min(paramInt - i, this.allocationLength - j);
      Allocation localAllocation = (Allocation)this.dataQueue.peek();
      System.arraycopy(localAllocation.data, localAllocation.translateOffset(j), paramArrayOfByte, i, k);
      paramLong += k;
      i += k;
    }
  }

  private void readEncryptionData(DecoderInputBuffer paramDecoderInputBuffer, BufferExtrasHolder paramBufferExtrasHolder)
  {
    int k = 0;
    long l1 = paramBufferExtrasHolder.offset;
    this.scratch.reset(1);
    readData(l1, this.scratch.data, 1);
    l1 = 1L + l1;
    int j = this.scratch.data[0];
    if ((j & 0x80) != 0)
    {
      i = 1;
      j &= 127;
      if (paramDecoderInputBuffer.cryptoInfo.iv == null)
        paramDecoderInputBuffer.cryptoInfo.iv = new byte[16];
      readData(l1, paramDecoderInputBuffer.cryptoInfo.iv, j);
      l1 += j;
      if (i == 0)
        break label311;
      this.scratch.reset(2);
      readData(l1, this.scratch.data, 2);
      j = this.scratch.readUnsignedShort();
      l1 += 2L;
    }
    Object localObject2;
    Object localObject1;
    while (true)
    {
      localObject2 = paramDecoderInputBuffer.cryptoInfo.numBytesOfClearData;
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (localObject2.length >= j);
      }
      else
      {
        localObject1 = new int[j];
      }
      int[] arrayOfInt = paramDecoderInputBuffer.cryptoInfo.numBytesOfEncryptedData;
      if (arrayOfInt != null)
      {
        localObject2 = arrayOfInt;
        if (arrayOfInt.length >= j);
      }
      else
      {
        localObject2 = new int[j];
      }
      if (i == 0)
        break label317;
      i = j * 6;
      this.scratch.reset(i);
      readData(l1, this.scratch.data, i);
      long l2 = l1 + i;
      this.scratch.setPosition(0);
      i = k;
      while (true)
      {
        l1 = l2;
        if (i >= j)
          break;
        localObject1[i] = this.scratch.readUnsignedShort();
        localObject2[i] = this.scratch.readUnsignedIntToInt();
        i += 1;
      }
      i = 0;
      break;
      label311: j = 1;
    }
    label317: localObject1[0] = 0;
    localObject2[0] = (paramBufferExtrasHolder.size - (int)(l1 - paramBufferExtrasHolder.offset));
    paramDecoderInputBuffer.cryptoInfo.set(j, localObject1, localObject2, paramBufferExtrasHolder.encryptionKeyId, paramDecoderInputBuffer.cryptoInfo.iv, 1);
    int i = (int)(l1 - paramBufferExtrasHolder.offset);
    paramBufferExtrasHolder.offset += i;
    paramBufferExtrasHolder.size -= i;
  }

  private boolean startWriteOperation()
  {
    return this.state.compareAndSet(0, 1);
  }

  public void disable()
  {
    if (this.state.getAndSet(2) == 0)
      clearSampleData();
  }

  public void discardUpstreamSamples(int paramInt)
  {
    this.totalBytesWritten = this.infoQueue.discardUpstreamSamples(paramInt);
    dropUpstreamFrom(this.totalBytesWritten);
  }

  public void format(Format paramFormat)
  {
    paramFormat = getAdjustedSampleFormat(paramFormat, this.sampleOffsetUs);
    boolean bool = this.infoQueue.format(paramFormat);
    if ((this.upstreamFormatChangeListener != null) && (bool))
      this.upstreamFormatChangeListener.onUpstreamFormatChanged(paramFormat);
  }

  public void formatWithOffset(Format paramFormat, long paramLong)
  {
    this.sampleOffsetUs = paramLong;
    format(paramFormat);
  }

  public long getLargestQueuedTimestampUs()
  {
    return this.infoQueue.getLargestQueuedTimestampUs();
  }

  public int getReadIndex()
  {
    return this.infoQueue.getReadIndex();
  }

  public Format getUpstreamFormat()
  {
    return this.infoQueue.getUpstreamFormat();
  }

  public int getWriteIndex()
  {
    return this.infoQueue.getWriteIndex();
  }

  public boolean isEmpty()
  {
    return this.infoQueue.isEmpty();
  }

  public int peekSourceId()
  {
    return this.infoQueue.peekSourceId();
  }

  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, boolean paramBoolean, long paramLong)
  {
    switch (this.infoQueue.readData(paramFormatHolder, paramDecoderInputBuffer, this.downstreamFormat, this.extrasHolder))
    {
    default:
      throw new IllegalStateException();
    case -3:
      if (paramBoolean)
      {
        paramDecoderInputBuffer.setFlags(4);
        return -4;
      }
      return -3;
    case -5:
      this.downstreamFormat = paramFormatHolder.format;
      return -5;
    case -4:
    }
    if (paramDecoderInputBuffer.timeUs < paramLong)
      paramDecoderInputBuffer.addFlag(-2147483648);
    if (paramDecoderInputBuffer.isEncrypted())
      readEncryptionData(paramDecoderInputBuffer, this.extrasHolder);
    paramDecoderInputBuffer.ensureSpaceForWrite(this.extrasHolder.size);
    readData(this.extrasHolder.offset, paramDecoderInputBuffer.data, this.extrasHolder.size);
    dropDownstreamTo(this.extrasHolder.nextOffset);
    return -4;
  }

  public void reset(boolean paramBoolean)
  {
    AtomicInteger localAtomicInteger = this.state;
    if (paramBoolean);
    for (int i = 0; ; i = 2)
    {
      i = localAtomicInteger.getAndSet(i);
      clearSampleData();
      this.infoQueue.resetLargestParsedTimestamps();
      if (i == 2)
        this.downstreamFormat = null;
      return;
    }
  }

  public int sampleData(ExtractorInput paramExtractorInput, int paramInt, boolean paramBoolean)
  {
    if (!startWriteOperation())
    {
      paramInt = paramExtractorInput.skip(paramInt);
      if (paramInt == -1)
      {
        if (paramBoolean)
          return -1;
        throw new EOFException();
      }
      return paramInt;
    }
    try
    {
      paramInt = prepareForAppend(paramInt);
      paramInt = paramExtractorInput.read(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      if (paramInt == -1)
      {
        if (paramBoolean)
          return -1;
        throw new EOFException();
      }
    }
    finally
    {
      endWriteOperation();
    }
    this.lastAllocationOffset += paramInt;
    this.totalBytesWritten += paramInt;
    endWriteOperation();
    return paramInt;
  }

  public void sampleData(ParsableByteArray paramParsableByteArray, int paramInt)
  {
    int i = paramInt;
    if (!startWriteOperation())
    {
      paramParsableByteArray.skipBytes(paramInt);
      return;
    }
    while (i > 0)
    {
      paramInt = prepareForAppend(i);
      paramParsableByteArray.readBytes(this.lastAllocation.data, this.lastAllocation.translateOffset(this.lastAllocationOffset), paramInt);
      this.lastAllocationOffset += paramInt;
      this.totalBytesWritten += paramInt;
      i -= paramInt;
    }
    endWriteOperation();
  }

  public void sampleMetadata(long paramLong, int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    if (!startWriteOperation())
    {
      this.infoQueue.commitSampleTimestamp(paramLong);
      return;
    }
    try
    {
      if (this.pendingSplice)
      {
        if ((paramInt1 & 0x1) != 0)
        {
          bool = this.infoQueue.attemptSplice(paramLong);
          if (bool);
        }
        else
        {
          return;
        }
        this.pendingSplice = false;
      }
      boolean bool = this.needKeyframe;
      if (bool)
      {
        if ((paramInt1 & 0x1) == 0)
          return;
        this.needKeyframe = false;
      }
      long l1 = this.sampleOffsetUs;
      long l2 = this.totalBytesWritten;
      long l3 = paramInt2;
      long l4 = paramInt3;
      this.infoQueue.commitSample(paramLong + l1, paramInt1, l2 - l3 - l4, paramInt2, paramArrayOfByte);
      return;
    }
    finally
    {
      endWriteOperation();
    }
    throw paramArrayOfByte;
  }

  public void setUpstreamFormatChangeListener(UpstreamFormatChangedListener paramUpstreamFormatChangedListener)
  {
    this.upstreamFormatChangeListener = paramUpstreamFormatChangedListener;
  }

  public boolean skipToKeyframeBefore(long paramLong)
  {
    paramLong = this.infoQueue.skipToKeyframeBefore(paramLong);
    if (paramLong == -1L)
      return false;
    dropDownstreamTo(paramLong);
    return true;
  }

  public void sourceId(int paramInt)
  {
    this.infoQueue.sourceId(paramInt);
  }

  public void splice()
  {
    this.pendingSplice = true;
  }

  private static final class BufferExtrasHolder
  {
    public byte[] encryptionKeyId;
    public long nextOffset;
    public long offset;
    public int size;
  }

  private static final class InfoQueue
  {
    private static final int SAMPLE_CAPACITY_INCREMENT = 1000;
    private int absoluteReadIndex;
    private int capacity = 1000;
    private byte[][] encryptionKeys = new byte[this.capacity][];
    private int[] flags = new int[this.capacity];
    private Format[] formats = new Format[this.capacity];
    private long largestDequeuedTimestampUs = -9223372036854775808L;
    private long largestQueuedTimestampUs = -9223372036854775808L;
    private long[] offsets = new long[this.capacity];
    private int queueSize;
    private int relativeReadIndex;
    private int relativeWriteIndex;
    private int[] sizes = new int[this.capacity];
    private int[] sourceIds = new int[this.capacity];
    private long[] timesUs = new long[this.capacity];
    private Format upstreamFormat;
    private boolean upstreamFormatRequired = true;
    private int upstreamSourceId;

    public boolean attemptSplice(long paramLong)
    {
      monitorenter;
      try
      {
        long l = this.largestDequeuedTimestampUs;
        if (l >= paramLong);
        for (int j = 0; ; j = 1)
        {
          return j;
          int i = this.queueSize;
          while ((i > 0) && (this.timesUs[((this.relativeReadIndex + i - 1) % this.capacity)] >= paramLong))
            i -= 1;
          discardUpstreamSamples(i + this.absoluteReadIndex);
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void clearSampleData()
    {
      this.absoluteReadIndex = 0;
      this.relativeReadIndex = 0;
      this.relativeWriteIndex = 0;
      this.queueSize = 0;
    }

    public void commitSample(long paramLong1, int paramInt1, long paramLong2, int paramInt2, byte[] paramArrayOfByte)
    {
      monitorenter;
      try
      {
        boolean bool;
        if (!this.upstreamFormatRequired)
        {
          bool = true;
          Assertions.checkState(bool);
          commitSampleTimestamp(paramLong1);
          this.timesUs[this.relativeWriteIndex] = paramLong1;
          this.offsets[this.relativeWriteIndex] = paramLong2;
          this.sizes[this.relativeWriteIndex] = paramInt2;
          this.flags[this.relativeWriteIndex] = paramInt1;
          this.encryptionKeys[this.relativeWriteIndex] = paramArrayOfByte;
          this.formats[this.relativeWriteIndex] = this.upstreamFormat;
          this.sourceIds[this.relativeWriteIndex] = this.upstreamSourceId;
          this.queueSize += 1;
          if (this.queueSize != this.capacity)
            break label472;
          paramInt1 = this.capacity + 1000;
          paramArrayOfByte = new int[paramInt1];
          long[] arrayOfLong1 = new long[paramInt1];
          long[] arrayOfLong2 = new long[paramInt1];
          int[] arrayOfInt1 = new int[paramInt1];
          int[] arrayOfInt2 = new int[paramInt1];
          byte[][] arrayOfByte = new byte[paramInt1][];
          Format[] arrayOfFormat = new Format[paramInt1];
          paramInt2 = this.capacity - this.relativeReadIndex;
          System.arraycopy(this.offsets, this.relativeReadIndex, arrayOfLong1, 0, paramInt2);
          System.arraycopy(this.timesUs, this.relativeReadIndex, arrayOfLong2, 0, paramInt2);
          System.arraycopy(this.flags, this.relativeReadIndex, arrayOfInt1, 0, paramInt2);
          System.arraycopy(this.sizes, this.relativeReadIndex, arrayOfInt2, 0, paramInt2);
          System.arraycopy(this.encryptionKeys, this.relativeReadIndex, arrayOfByte, 0, paramInt2);
          System.arraycopy(this.formats, this.relativeReadIndex, arrayOfFormat, 0, paramInt2);
          System.arraycopy(this.sourceIds, this.relativeReadIndex, paramArrayOfByte, 0, paramInt2);
          int i = this.relativeReadIndex;
          System.arraycopy(this.offsets, 0, arrayOfLong1, paramInt2, i);
          System.arraycopy(this.timesUs, 0, arrayOfLong2, paramInt2, i);
          System.arraycopy(this.flags, 0, arrayOfInt1, paramInt2, i);
          System.arraycopy(this.sizes, 0, arrayOfInt2, paramInt2, i);
          System.arraycopy(this.encryptionKeys, 0, arrayOfByte, paramInt2, i);
          System.arraycopy(this.formats, 0, arrayOfFormat, paramInt2, i);
          System.arraycopy(this.sourceIds, 0, paramArrayOfByte, paramInt2, i);
          this.offsets = arrayOfLong1;
          this.timesUs = arrayOfLong2;
          this.flags = arrayOfInt1;
          this.sizes = arrayOfInt2;
          this.encryptionKeys = arrayOfByte;
          this.formats = arrayOfFormat;
          this.sourceIds = paramArrayOfByte;
          this.relativeReadIndex = 0;
          this.relativeWriteIndex = this.capacity;
          this.queueSize = this.capacity;
          this.capacity = paramInt1;
        }
        while (true)
        {
          return;
          bool = false;
          break;
          label472: this.relativeWriteIndex += 1;
          if (this.relativeWriteIndex != this.capacity)
            continue;
          this.relativeWriteIndex = 0;
        }
      }
      finally
      {
        monitorexit;
      }
      throw paramArrayOfByte;
    }

    public void commitSampleTimestamp(long paramLong)
    {
      monitorenter;
      try
      {
        this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, paramLong);
        monitorexit;
        return;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }

    public long discardUpstreamSamples(int paramInt)
    {
      paramInt = getWriteIndex() - paramInt;
      boolean bool;
      if ((paramInt >= 0) && (paramInt <= this.queueSize))
        bool = true;
      while (true)
      {
        Assertions.checkArgument(bool);
        if (paramInt != 0)
          break;
        if (this.absoluteReadIndex == 0)
        {
          return 0L;
          bool = false;
          continue;
        }
        if (this.relativeWriteIndex == 0);
        for (paramInt = this.capacity; ; paramInt = this.relativeWriteIndex)
        {
          paramInt -= 1;
          long l = this.offsets[paramInt];
          return this.sizes[paramInt] + l;
        }
      }
      this.queueSize -= paramInt;
      this.relativeWriteIndex = ((this.relativeWriteIndex + this.capacity - paramInt) % this.capacity);
      this.largestQueuedTimestampUs = -9223372036854775808L;
      paramInt = this.queueSize - 1;
      while (true)
      {
        if (paramInt >= 0)
        {
          int i = (this.relativeReadIndex + paramInt) % this.capacity;
          this.largestQueuedTimestampUs = Math.max(this.largestQueuedTimestampUs, this.timesUs[i]);
          if ((this.flags[i] & 0x1) == 0);
        }
        else
        {
          return this.offsets[this.relativeWriteIndex];
        }
        paramInt -= 1;
      }
    }

    public boolean format(Format paramFormat)
    {
      int i = 0;
      monitorenter;
      if (paramFormat == null);
      try
      {
        this.upstreamFormatRequired = true;
        while (true)
        {
          return i;
          this.upstreamFormatRequired = false;
          if (Util.areEqual(paramFormat, this.upstreamFormat))
            continue;
          this.upstreamFormat = paramFormat;
          i = 1;
        }
      }
      finally
      {
        monitorexit;
      }
      throw paramFormat;
    }

    public long getLargestQueuedTimestampUs()
    {
      monitorenter;
      try
      {
        long l = Math.max(this.largestDequeuedTimestampUs, this.largestQueuedTimestampUs);
        monitorexit;
        return l;
      }
      finally
      {
        localObject = finally;
        monitorexit;
      }
      throw localObject;
    }

    public int getReadIndex()
    {
      return this.absoluteReadIndex;
    }

    public Format getUpstreamFormat()
    {
      monitorenter;
      try
      {
        boolean bool = this.upstreamFormatRequired;
        if (bool);
        for (Format localFormat = null; ; localFormat = this.upstreamFormat)
          return localFormat;
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public int getWriteIndex()
    {
      return this.absoluteReadIndex + this.queueSize;
    }

    public boolean isEmpty()
    {
      monitorenter;
      try
      {
        int i = this.queueSize;
        if (i == 0)
        {
          j = 1;
          return j;
        }
        int j = 0;
      }
      finally
      {
        monitorexit;
      }
    }

    public int peekSourceId()
    {
      if (this.queueSize == 0)
        return this.upstreamSourceId;
      return this.sourceIds[this.relativeReadIndex];
    }

    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer, Format paramFormat, DefaultTrackOutput.BufferExtrasHolder paramBufferExtrasHolder)
    {
      int i = -5;
      monitorenter;
      try
      {
        if (this.queueSize == 0)
          if ((this.upstreamFormat != null) && (this.upstreamFormat != paramFormat))
            paramFormatHolder.format = this.upstreamFormat;
        while (true)
        {
          return i;
          i = -3;
          continue;
          if (this.formats[this.relativeReadIndex] == paramFormat)
            break;
          paramFormatHolder.format = this.formats[this.relativeReadIndex];
        }
      }
      finally
      {
        monitorexit;
      }
      paramDecoderInputBuffer.timeUs = this.timesUs[this.relativeReadIndex];
      paramDecoderInputBuffer.setFlags(this.flags[this.relativeReadIndex]);
      paramBufferExtrasHolder.size = this.sizes[this.relativeReadIndex];
      paramBufferExtrasHolder.offset = this.offsets[this.relativeReadIndex];
      paramBufferExtrasHolder.encryptionKeyId = this.encryptionKeys[this.relativeReadIndex];
      this.largestDequeuedTimestampUs = Math.max(this.largestDequeuedTimestampUs, paramDecoderInputBuffer.timeUs);
      this.queueSize -= 1;
      this.relativeReadIndex += 1;
      this.absoluteReadIndex += 1;
      if (this.relativeReadIndex == this.capacity)
        this.relativeReadIndex = 0;
      long l;
      if (this.queueSize > 0)
        l = this.offsets[this.relativeReadIndex];
      while (true)
      {
        paramBufferExtrasHolder.nextOffset = l;
        i = -4;
        break;
        l = paramBufferExtrasHolder.offset;
        i = paramBufferExtrasHolder.size;
        l += i;
      }
    }

    public void resetLargestParsedTimestamps()
    {
      this.largestDequeuedTimestampUs = -9223372036854775808L;
      this.largestQueuedTimestampUs = -9223372036854775808L;
    }

    public long skipToKeyframeBefore(long paramLong)
    {
      long l2 = -1L;
      monitorenter;
      long l1 = l2;
      try
      {
        if (this.queueSize != 0)
        {
          l1 = this.timesUs[this.relativeReadIndex];
          if (paramLong >= l1)
            break label45;
          l1 = l2;
        }
        label45: int i;
        label57: int j;
        int k;
        while (true)
        {
          return l1;
          if (this.relativeWriteIndex != 0)
            break;
          i = this.capacity;
          l1 = l2;
          if (paramLong > this.timesUs[(i - 1)])
            continue;
          i = 0;
          j = this.relativeReadIndex;
          k = -1;
        }
        while (true)
        {
          if ((j == this.relativeWriteIndex) || (this.timesUs[j] > paramLong))
          {
            l1 = l2;
            if (k == -1)
              break;
            this.queueSize -= k;
            this.relativeReadIndex = ((this.relativeReadIndex + k) % this.capacity);
            this.absoluteReadIndex += k;
            l1 = this.offsets[this.relativeReadIndex];
            break;
            i = this.relativeWriteIndex;
            break label57;
          }
          if ((this.flags[j] & 0x1) != 0)
            k = i;
          j = (j + 1) % this.capacity;
          i += 1;
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    public void sourceId(int paramInt)
    {
      this.upstreamSourceId = paramInt;
    }
  }

  public static abstract interface UpstreamFormatChangedListener
  {
    public abstract void onUpstreamFormatChanged(Format paramFormat);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput
 * JD-Core Version:    0.6.0
 */