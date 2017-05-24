package org.vidogram.messenger.exoplayer2.upstream;

import android.net.Uri;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.PriorityTaskManager;

public final class PriorityDataSource
  implements DataSource
{
  private final int priority;
  private final PriorityTaskManager priorityTaskManager;
  private final DataSource upstream;

  public PriorityDataSource(DataSource paramDataSource, PriorityTaskManager paramPriorityTaskManager, int paramInt)
  {
    this.upstream = ((DataSource)Assertions.checkNotNull(paramDataSource));
    this.priorityTaskManager = ((PriorityTaskManager)Assertions.checkNotNull(paramPriorityTaskManager));
    this.priority = paramInt;
  }

  public void close()
  {
    this.upstream.close();
  }

  public Uri getUri()
  {
    return this.upstream.getUri();
  }

  public long open(DataSpec paramDataSpec)
  {
    this.priorityTaskManager.proceedOrThrow(this.priority);
    return this.upstream.open(paramDataSpec);
  }

  public int read(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    this.priorityTaskManager.proceedOrThrow(this.priority);
    return this.upstream.read(paramArrayOfByte, paramInt1, paramInt2);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.upstream.PriorityDataSource
 * JD-Core Version:    0.6.0
 */