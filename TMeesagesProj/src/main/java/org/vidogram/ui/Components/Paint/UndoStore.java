package org.vidogram.ui.Components.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.vidogram.messenger.AndroidUtilities;

public class UndoStore
{
  private UndoStoreDelegate delegate;
  private List<UUID> operations = new ArrayList();
  private Map<UUID, Runnable> uuidToOperationMap = new HashMap();

  private void notifyOfHistoryChanges()
  {
    AndroidUtilities.runOnUIThread(new Runnable()
    {
      public void run()
      {
        if (UndoStore.this.delegate != null)
          UndoStore.this.delegate.historyChanged();
      }
    });
  }

  public boolean canUndo()
  {
    return !this.operations.isEmpty();
  }

  public void registerUndo(UUID paramUUID, Runnable paramRunnable)
  {
    this.uuidToOperationMap.put(paramUUID, paramRunnable);
    this.operations.add(paramUUID);
    notifyOfHistoryChanges();
  }

  public void reset()
  {
    this.operations.clear();
    this.uuidToOperationMap.clear();
    notifyOfHistoryChanges();
  }

  public void setDelegate(UndoStoreDelegate paramUndoStoreDelegate)
  {
    this.delegate = paramUndoStoreDelegate;
  }

  public void undo()
  {
    if (this.operations.size() == 0)
      return;
    int i = this.operations.size() - 1;
    UUID localUUID = (UUID)this.operations.get(i);
    Runnable localRunnable = (Runnable)this.uuidToOperationMap.get(localUUID);
    this.uuidToOperationMap.remove(localUUID);
    this.operations.remove(i);
    localRunnable.run();
    notifyOfHistoryChanges();
  }

  public void unregisterUndo(UUID paramUUID)
  {
    this.uuidToOperationMap.remove(paramUUID);
    this.operations.remove(paramUUID);
    notifyOfHistoryChanges();
  }

  public static abstract interface UndoStoreDelegate
  {
    public abstract void historyChanged();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.Paint.UndoStore
 * JD-Core Version:    0.6.0
 */