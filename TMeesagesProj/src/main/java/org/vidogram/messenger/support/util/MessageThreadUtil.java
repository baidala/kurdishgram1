package org.vidogram.messenger.support.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.j;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T>
  implements ThreadUtil<T>
{
  public ThreadUtil.BackgroundCallback<T> getBackgroundProxy(ThreadUtil.BackgroundCallback<T> paramBackgroundCallback)
  {
    return new ThreadUtil.BackgroundCallback(paramBackgroundCallback)
    {
      static final int LOAD_TILE = 3;
      static final int RECYCLE_TILE = 4;
      static final int REFRESH = 1;
      static final int UPDATE_RANGE = 2;
      private Runnable mBackgroundRunnable = new Runnable()
      {
        public void run()
        {
          while (true)
          {
            MessageThreadUtil.SyncQueueItem localSyncQueueItem = MessageThreadUtil.2.this.mQueue.next();
            if (localSyncQueueItem == null)
            {
              MessageThreadUtil.2.this.mBackgroundRunning.set(false);
              return;
            }
            switch (localSyncQueueItem.what)
            {
            default:
              Log.e("ThreadUtil", "Unsupported message, what=" + localSyncQueueItem.what);
              break;
            case 1:
              MessageThreadUtil.2.this.mQueue.removeMessages(1);
              MessageThreadUtil.2.this.val$callback.refresh(localSyncQueueItem.arg1);
              break;
            case 2:
              MessageThreadUtil.2.this.mQueue.removeMessages(2);
              MessageThreadUtil.2.this.mQueue.removeMessages(3);
              MessageThreadUtil.2.this.val$callback.updateRange(localSyncQueueItem.arg1, localSyncQueueItem.arg2, localSyncQueueItem.arg3, localSyncQueueItem.arg4, localSyncQueueItem.arg5);
              break;
            case 3:
              MessageThreadUtil.2.this.val$callback.loadTile(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
              break;
            case 4:
            }
            MessageThreadUtil.2.this.val$callback.recycleTile((TileList.Tile)localSyncQueueItem.data);
          }
        }
      };
      AtomicBoolean mBackgroundRunning = new AtomicBoolean(false);
      private final Executor mExecutor = j.a();
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();

      private void maybeExecuteBackgroundRunnable()
      {
        if (this.mBackgroundRunning.compareAndSet(false, true))
          this.mExecutor.execute(this.mBackgroundRunnable);
      }

      private void sendMessage(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
      {
        this.mQueue.sendMessage(paramSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }

      private void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
      {
        this.mQueue.sendMessageAtFrontOfQueue(paramSyncQueueItem);
        maybeExecuteBackgroundRunnable();
      }

      public void loadTile(int paramInt1, int paramInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramInt1, paramInt2));
      }

      public void recycleTile(TileList.Tile<T> paramTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(4, 0, paramTile));
      }

      public void refresh(int paramInt)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramInt, null));
      }

      public void updateRange(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
      {
        sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, null));
      }
    };
  }

  public ThreadUtil.MainThreadCallback<T> getMainThreadProxy(ThreadUtil.MainThreadCallback<T> paramMainThreadCallback)
  {
    return new ThreadUtil.MainThreadCallback(paramMainThreadCallback)
    {
      static final int ADD_TILE = 2;
      static final int REMOVE_TILE = 3;
      static final int UPDATE_ITEM_COUNT = 1;
      private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());
      private Runnable mMainThreadRunnable = new Runnable()
      {
        public void run()
        {
          MessageThreadUtil.SyncQueueItem localSyncQueueItem = MessageThreadUtil.1.this.mQueue.next();
          if (localSyncQueueItem != null)
          {
            switch (localSyncQueueItem.what)
            {
            default:
              Log.e("ThreadUtil", "Unsupported message, what=" + localSyncQueueItem.what);
            case 1:
            case 2:
            case 3:
            }
            while (true)
            {
              localSyncQueueItem = MessageThreadUtil.1.this.mQueue.next();
              break;
              MessageThreadUtil.1.this.val$callback.updateItemCount(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
              continue;
              MessageThreadUtil.1.this.val$callback.addTile(localSyncQueueItem.arg1, (TileList.Tile)localSyncQueueItem.data);
              continue;
              MessageThreadUtil.1.this.val$callback.removeTile(localSyncQueueItem.arg1, localSyncQueueItem.arg2);
            }
          }
        }
      };
      final MessageThreadUtil.MessageQueue mQueue = new MessageThreadUtil.MessageQueue();

      private void sendMessage(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
      {
        this.mQueue.sendMessage(paramSyncQueueItem);
        this.mMainThreadHandler.post(this.mMainThreadRunnable);
      }

      public void addTile(int paramInt, TileList.Tile<T> paramTile)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(2, paramInt, paramTile));
      }

      public void removeTile(int paramInt1, int paramInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(3, paramInt1, paramInt2));
      }

      public void updateItemCount(int paramInt1, int paramInt2)
      {
        sendMessage(MessageThreadUtil.SyncQueueItem.obtainMessage(1, paramInt1, paramInt2));
      }
    };
  }

  static class MessageQueue
  {
    private MessageThreadUtil.SyncQueueItem mRoot;

    MessageThreadUtil.SyncQueueItem next()
    {
      monitorenter;
      try
      {
        MessageThreadUtil.SyncQueueItem localSyncQueueItem = this.mRoot;
        if (localSyncQueueItem == null)
          localSyncQueueItem = null;
        while (true)
        {
          return localSyncQueueItem;
          localSyncQueueItem = this.mRoot;
          this.mRoot = MessageThreadUtil.SyncQueueItem.access$000(this.mRoot);
        }
      }
      finally
      {
        monitorexit;
      }
      throw localObject;
    }

    void removeMessages(int paramInt)
    {
      monitorenter;
      try
      {
        while ((this.mRoot != null) && (this.mRoot.what == paramInt))
        {
          MessageThreadUtil.SyncQueueItem localSyncQueueItem1 = this.mRoot;
          this.mRoot = MessageThreadUtil.SyncQueueItem.access$000(this.mRoot);
          localSyncQueueItem1.recycle();
        }
      }
      finally
      {
        monitorexit;
      }
      if (this.mRoot != null)
      {
        Object localObject3 = this.mRoot;
        Object localObject2 = MessageThreadUtil.SyncQueueItem.access$000((MessageThreadUtil.SyncQueueItem)localObject3);
        if (localObject2 != null)
        {
          MessageThreadUtil.SyncQueueItem localSyncQueueItem2 = MessageThreadUtil.SyncQueueItem.access$000((MessageThreadUtil.SyncQueueItem)localObject2);
          if (((MessageThreadUtil.SyncQueueItem)localObject2).what == paramInt)
          {
            MessageThreadUtil.SyncQueueItem.access$002((MessageThreadUtil.SyncQueueItem)localObject3, localSyncQueueItem2);
            ((MessageThreadUtil.SyncQueueItem)localObject2).recycle();
          }
          while (true)
          {
            localObject2 = localSyncQueueItem2;
            break;
            localObject3 = localObject2;
          }
        }
      }
      monitorexit;
    }

    void sendMessage(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      monitorenter;
      try
      {
        if (this.mRoot == null)
          this.mRoot = paramSyncQueueItem;
        while (true)
        {
          return;
          for (MessageThreadUtil.SyncQueueItem localSyncQueueItem = this.mRoot; MessageThreadUtil.SyncQueueItem.access$000(localSyncQueueItem) != null; localSyncQueueItem = MessageThreadUtil.SyncQueueItem.access$000(localSyncQueueItem));
          MessageThreadUtil.SyncQueueItem.access$002(localSyncQueueItem, paramSyncQueueItem);
        }
      }
      finally
      {
        monitorexit;
      }
      throw paramSyncQueueItem;
    }

    void sendMessageAtFrontOfQueue(MessageThreadUtil.SyncQueueItem paramSyncQueueItem)
    {
      monitorenter;
      try
      {
        MessageThreadUtil.SyncQueueItem.access$002(paramSyncQueueItem, this.mRoot);
        this.mRoot = paramSyncQueueItem;
        monitorexit;
        return;
      }
      finally
      {
        paramSyncQueueItem = finally;
        monitorexit;
      }
      throw paramSyncQueueItem;
    }
  }

  static class SyncQueueItem
  {
    private static SyncQueueItem sPool;
    private static final Object sPoolLock = new Object();
    public int arg1;
    public int arg2;
    public int arg3;
    public int arg4;
    public int arg5;
    public Object data;
    private SyncQueueItem next;
    public int what;

    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3)
    {
      return obtainMessage(paramInt1, paramInt2, paramInt3, 0, 0, 0, null);
    }

    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, Object paramObject)
    {
      synchronized (sPoolLock)
      {
        if (sPool == null)
        {
          localSyncQueueItem = new SyncQueueItem();
          localSyncQueueItem.what = paramInt1;
          localSyncQueueItem.arg1 = paramInt2;
          localSyncQueueItem.arg2 = paramInt3;
          localSyncQueueItem.arg3 = paramInt4;
          localSyncQueueItem.arg4 = paramInt5;
          localSyncQueueItem.arg5 = paramInt6;
          localSyncQueueItem.data = paramObject;
          return localSyncQueueItem;
        }
        SyncQueueItem localSyncQueueItem = sPool;
        sPool = sPool.next;
        localSyncQueueItem.next = null;
      }
    }

    static SyncQueueItem obtainMessage(int paramInt1, int paramInt2, Object paramObject)
    {
      return obtainMessage(paramInt1, paramInt2, 0, 0, 0, 0, paramObject);
    }

    void recycle()
    {
      this.next = null;
      this.arg5 = 0;
      this.arg4 = 0;
      this.arg3 = 0;
      this.arg2 = 0;
      this.arg1 = 0;
      this.what = 0;
      this.data = null;
      synchronized (sPoolLock)
      {
        if (sPool != null)
          this.next = sPool;
        sPool = this;
        return;
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.util.MessageThreadUtil
 * JD-Core Version:    0.6.0
 */