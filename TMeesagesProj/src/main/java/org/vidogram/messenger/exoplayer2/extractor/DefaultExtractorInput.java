package org.vidogram.messenger.exoplayer2.extractor;

import java.io.EOFException;
import java.util.Arrays;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public final class DefaultExtractorInput
  implements ExtractorInput
{
  private static final byte[] SCRATCH_SPACE = new byte[4096];
  private final DataSource dataSource;
  private byte[] peekBuffer;
  private int peekBufferLength;
  private int peekBufferPosition;
  private long position;
  private final long streamLength;

  public DefaultExtractorInput(DataSource paramDataSource, long paramLong1, long paramLong2)
  {
    this.dataSource = paramDataSource;
    this.position = paramLong1;
    this.streamLength = paramLong2;
    this.peekBuffer = new byte[8192];
  }

  private void commitBytesRead(int paramInt)
  {
    if (paramInt != -1)
      this.position += paramInt;
  }

  private void ensureSpaceForPeek(int paramInt)
  {
    paramInt = this.peekBufferPosition + paramInt;
    if (paramInt > this.peekBuffer.length)
      this.peekBuffer = Arrays.copyOf(this.peekBuffer, Math.max(this.peekBuffer.length * 2, paramInt));
  }

  private int readFromDataSource(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
  {
    if (Thread.interrupted())
      throw new InterruptedException();
    paramInt1 = this.dataSource.read(paramArrayOfByte, paramInt1 + paramInt3, paramInt2 - paramInt3);
    if (paramInt1 == -1)
    {
      if ((paramInt3 == 0) && (paramBoolean))
        return -1;
      throw new EOFException();
    }
    return paramInt3 + paramInt1;
  }

  private int readFromPeekBuffer(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.peekBufferLength == 0)
      return 0;
    paramInt2 = Math.min(this.peekBufferLength, paramInt2);
    System.arraycopy(this.peekBuffer, 0, paramArrayOfByte, paramInt1, paramInt2);
    updatePeekBuffer(paramInt2);
    return paramInt2;
  }

  private int skipFromPeekBuffer(int paramInt)
  {
    paramInt = Math.min(this.peekBufferLength, paramInt);
    updatePeekBuffer(paramInt);
    return paramInt;
  }

  private void updatePeekBuffer(int paramInt)
  {
    this.peekBufferLength -= paramInt;
    this.peekBufferPosition = 0;
    System.arraycopy(this.peekBuffer, paramInt, this.peekBuffer, 0, this.peekBufferLength);
  }

  public void advancePeekPosition(int paramInt)
  {
    advancePeekPosition(paramInt, false);
  }

  public boolean advancePeekPosition(int paramInt, boolean paramBoolean)
  {
    ensureSpaceForPeek(paramInt);
    int i = Math.min(this.peekBufferLength - this.peekBufferPosition, paramInt);
    while (i < paramInt)
    {
      int j = readFromDataSource(this.peekBuffer, this.peekBufferPosition, paramInt, i, paramBoolean);
      i = j;
      if (j == -1)
        return false;
    }
    this.peekBufferPosition += paramInt;
    this.peekBufferLength = Math.max(this.peekBufferLength, this.peekBufferPosition);
    return true;
  }

  public long getLength()
  {
    return this.streamLength;
  }

  public long getPeekPosition()
  {
    return this.position + this.peekBufferPosition;
  }

  public long getPosition()
  {
    return this.position;
  }

  public void peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    peekFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }

  public boolean peekFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if (!advancePeekPosition(paramInt2, paramBoolean))
      return false;
    System.arraycopy(this.peekBuffer, this.peekBufferPosition - paramInt2, paramArrayOfByte, paramInt1, paramInt2);
    return true;
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int j = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2);
    int i = j;
    if (j == 0)
      i = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, 0, true);
    commitBytesRead(i);
    return i;
  }

  public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    readFully(paramArrayOfByte, paramInt1, paramInt2, false);
  }

  public boolean readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    for (int i = readFromPeekBuffer(paramArrayOfByte, paramInt1, paramInt2); (i < paramInt2) && (i != -1); i = readFromDataSource(paramArrayOfByte, paramInt1, paramInt2, i, paramBoolean));
    commitBytesRead(i);
    return i != -1;
  }

  public void resetPeekPosition()
  {
    this.peekBufferPosition = 0;
  }

  public <E extends Throwable> void setRetryPosition(long paramLong, E paramE)
  {
    if (paramLong >= 0L);
    for (boolean bool = true; ; bool = false)
    {
      Assertions.checkArgument(bool);
      this.position = paramLong;
      throw paramE;
    }
  }

  public int skip(int paramInt)
  {
    int j = skipFromPeekBuffer(paramInt);
    int i = j;
    if (j == 0)
      i = readFromDataSource(SCRATCH_SPACE, 0, Math.min(paramInt, SCRATCH_SPACE.length), 0, true);
    commitBytesRead(i);
    return i;
  }

  public void skipFully(int paramInt)
  {
    skipFully(paramInt, false);
  }

  public boolean skipFully(int paramInt, boolean paramBoolean)
  {
    for (int i = skipFromPeekBuffer(paramInt); (i < paramInt) && (i != -1); i = readFromDataSource(SCRATCH_SPACE, -i, Math.min(paramInt, SCRATCH_SPACE.length + i), i, paramBoolean));
    commitBytesRead(i);
    return i != -1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.extractor.DefaultExtractorInput
 * JD-Core Version:    0.6.0
 */