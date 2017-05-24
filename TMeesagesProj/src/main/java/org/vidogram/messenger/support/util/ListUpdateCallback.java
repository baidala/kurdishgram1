package org.vidogram.messenger.support.util;

public abstract interface ListUpdateCallback
{
  public abstract void onChanged(int paramInt1, int paramInt2, Object paramObject);

  public abstract void onInserted(int paramInt1, int paramInt2);

  public abstract void onMoved(int paramInt1, int paramInt2);

  public abstract void onRemoved(int paramInt1, int paramInt2);
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.util.ListUpdateCallback
 * JD-Core Version:    0.6.0
 */