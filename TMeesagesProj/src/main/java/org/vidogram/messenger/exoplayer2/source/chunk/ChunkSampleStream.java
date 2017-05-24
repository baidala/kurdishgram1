package org.vidogram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.vidogram.messenger.exoplayer2.source.SampleStream;
import org.vidogram.messenger.exoplayer2.source.SequenceableLoader;
import org.vidogram.messenger.exoplayer2.source.SequenceableLoader.Callback;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.Loader;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Callback;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.vidogram.messenger.exoplayer2.util.Assertions;

public class ChunkSampleStream<T extends ChunkSource>
  implements SampleStream, SequenceableLoader, Loader.Callback<Chunk>
{
  private final SequenceableLoader.Callback<ChunkSampleStream<T>> callback;
  private final T chunkSource;
  private Format downstreamTrackFormat;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private long lastSeekPositionUs;
  private final Loader loader;
  private boolean loadingFinished;
  private final LinkedList<BaseMediaChunk> mediaChunks;
  private final int minLoadableRetryCount;
  private final ChunkHolder nextChunkHolder;
  private long pendingResetPositionUs;
  private final List<BaseMediaChunk> readOnlyMediaChunks;
  private final DefaultTrackOutput sampleQueue;
  private final int trackType;

  public ChunkSampleStream(int paramInt1, T paramT, SequenceableLoader.Callback<ChunkSampleStream<T>> paramCallback, Allocator paramAllocator, long paramLong, int paramInt2, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher)
  {
    this.trackType = paramInt1;
    this.chunkSource = paramT;
    this.callback = paramCallback;
    this.eventDispatcher = paramEventDispatcher;
    this.minLoadableRetryCount = paramInt2;
    this.loader = new Loader("Loader:ChunkSampleStream");
    this.nextChunkHolder = new ChunkHolder();
    this.mediaChunks = new LinkedList();
    this.readOnlyMediaChunks = Collections.unmodifiableList(this.mediaChunks);
    this.sampleQueue = new DefaultTrackOutput(paramAllocator);
    this.lastSeekPositionUs = paramLong;
    this.pendingResetPositionUs = paramLong;
  }

  private boolean discardUpstreamMediaChunks(int paramInt)
  {
    if (this.mediaChunks.size() <= paramInt)
      return false;
    long l1 = 0L;
    long l2 = ((BaseMediaChunk)this.mediaChunks.getLast()).endTimeUs;
    BaseMediaChunk localBaseMediaChunk = null;
    while (this.mediaChunks.size() > paramInt)
    {
      localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.removeLast();
      l1 = localBaseMediaChunk.startTimeUs;
      this.loadingFinished = false;
    }
    this.sampleQueue.discardUpstreamSamples(localBaseMediaChunk.getFirstSampleIndex());
    this.eventDispatcher.upstreamDiscarded(this.trackType, l1, l2);
    return true;
  }

  private boolean isMediaChunk(Chunk paramChunk)
  {
    return paramChunk instanceof BaseMediaChunk;
  }

  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != -9223372036854775807L;
  }

  private void maybeDiscardUpstream(long paramLong)
  {
    discardUpstreamMediaChunks(Math.max(1, this.chunkSource.getPreferredQueueSize(paramLong, this.readOnlyMediaChunks)));
  }

  public boolean continueLoading(long paramLong)
  {
    if ((this.loadingFinished) || (this.loader.isLoading()))
      return false;
    Object localObject2 = this.chunkSource;
    if (this.mediaChunks.isEmpty());
    for (Object localObject1 = null; ; localObject1 = (BaseMediaChunk)this.mediaChunks.getLast())
    {
      if (this.pendingResetPositionUs != -9223372036854775807L)
        paramLong = this.pendingResetPositionUs;
      ((ChunkSource)localObject2).getNextChunk((MediaChunk)localObject1, paramLong, this.nextChunkHolder);
      boolean bool = this.nextChunkHolder.endOfStream;
      localObject1 = this.nextChunkHolder.chunk;
      this.nextChunkHolder.clear();
      if (!bool)
        break;
      this.loadingFinished = true;
      return true;
    }
    if (localObject1 == null)
      return false;
    if (isMediaChunk((Chunk)localObject1))
    {
      this.pendingResetPositionUs = -9223372036854775807L;
      localObject2 = (BaseMediaChunk)localObject1;
      ((BaseMediaChunk)localObject2).init(this.sampleQueue);
      this.mediaChunks.add(localObject2);
    }
    paramLong = this.loader.startLoading((Loader.Loadable)localObject1, this, this.minLoadableRetryCount);
    this.eventDispatcher.loadStarted(((Chunk)localObject1).dataSpec, ((Chunk)localObject1).type, this.trackType, ((Chunk)localObject1).trackFormat, ((Chunk)localObject1).trackSelectionReason, ((Chunk)localObject1).trackSelectionData, ((Chunk)localObject1).startTimeUs, ((Chunk)localObject1).endTimeUs, paramLong);
    return true;
  }

  public long getBufferedPositionUs()
  {
    if (this.loadingFinished)
      return -9223372036854775808L;
    if (isPendingReset())
      return this.pendingResetPositionUs;
    long l = this.lastSeekPositionUs;
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.getLast();
    if (localBaseMediaChunk.isLoadCompleted())
    {
      if (localBaseMediaChunk == null)
        break label110;
      l = Math.max(l, localBaseMediaChunk.endTimeUs);
    }
    label110: 
    while (true)
    {
      return Math.max(l, this.sampleQueue.getLargestQueuedTimestampUs());
      if (this.mediaChunks.size() > 1)
      {
        localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.get(this.mediaChunks.size() - 2);
        break;
      }
      localBaseMediaChunk = null;
      break;
    }
  }

  public T getChunkSource()
  {
    return this.chunkSource;
  }

  public long getNextLoadPositionUs()
  {
    if (isPendingReset())
      return this.pendingResetPositionUs;
    if (this.loadingFinished)
      return -9223372036854775808L;
    return ((BaseMediaChunk)this.mediaChunks.getLast()).endTimeUs;
  }

  public boolean isReady()
  {
    return (this.loadingFinished) || ((!isPendingReset()) && (!this.sampleQueue.isEmpty()));
  }

  public void maybeThrowError()
  {
    this.loader.maybeThrowError();
    if (!this.loader.isLoading())
      this.chunkSource.maybeThrowError();
  }

  public void onLoadCanceled(Chunk paramChunk, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    if (!paramBoolean)
    {
      this.sampleQueue.reset(true);
      this.callback.onContinueLoadingRequested(this);
    }
  }

  public void onLoadCompleted(Chunk paramChunk, long paramLong1, long paramLong2)
  {
    this.chunkSource.onChunkLoadCompleted(paramChunk);
    this.eventDispatcher.loadCompleted(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, paramChunk.bytesLoaded());
    this.callback.onContinueLoadingRequested(this);
  }

  public int onLoadError(Chunk paramChunk, long paramLong1, long paramLong2, IOException paramIOException)
  {
    long l = paramChunk.bytesLoaded();
    boolean bool4 = isMediaChunk(paramChunk);
    boolean bool2;
    boolean bool3;
    BaseMediaChunk localBaseMediaChunk;
    if ((!bool4) || (l == 0L) || (this.mediaChunks.size() > 1))
    {
      bool1 = true;
      bool2 = false;
      if (this.chunkSource.onChunkLoadError(paramChunk, bool1, paramIOException))
      {
        bool3 = true;
        bool2 = bool3;
        if (bool4)
        {
          localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.removeLast();
          if (localBaseMediaChunk != paramChunk)
            break label206;
        }
      }
    }
    label206: for (boolean bool1 = true; ; bool1 = false)
    {
      Assertions.checkState(bool1);
      this.sampleQueue.discardUpstreamSamples(localBaseMediaChunk.getFirstSampleIndex());
      bool2 = bool3;
      if (this.mediaChunks.isEmpty())
      {
        this.pendingResetPositionUs = this.lastSeekPositionUs;
        bool2 = bool3;
      }
      this.eventDispatcher.loadError(paramChunk.dataSpec, paramChunk.type, this.trackType, paramChunk.trackFormat, paramChunk.trackSelectionReason, paramChunk.trackSelectionData, paramChunk.startTimeUs, paramChunk.endTimeUs, paramLong1, paramLong2, l, paramIOException, bool2);
      if (!bool2)
        break label212;
      this.callback.onContinueLoadingRequested(this);
      return 2;
      bool1 = false;
      break;
    }
    label212: return 0;
  }

  public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    if (isPendingReset())
      return -3;
    while ((this.mediaChunks.size() > 1) && (((BaseMediaChunk)this.mediaChunks.get(1)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()))
      this.mediaChunks.removeFirst();
    BaseMediaChunk localBaseMediaChunk = (BaseMediaChunk)this.mediaChunks.getFirst();
    Format localFormat = localBaseMediaChunk.trackFormat;
    if (!localFormat.equals(this.downstreamTrackFormat))
      this.eventDispatcher.downstreamFormatChanged(this.trackType, localFormat, localBaseMediaChunk.trackSelectionReason, localBaseMediaChunk.trackSelectionData, localBaseMediaChunk.startTimeUs);
    this.downstreamTrackFormat = localFormat;
    return this.sampleQueue.readData(paramFormatHolder, paramDecoderInputBuffer, this.loadingFinished, this.lastSeekPositionUs);
  }

  public void release()
  {
    this.sampleQueue.disable();
    this.loader.release();
  }

  public void seekToUs(long paramLong)
  {
    this.lastSeekPositionUs = paramLong;
    int i;
    if ((!isPendingReset()) && (this.sampleQueue.skipToKeyframeBefore(paramLong)))
      i = 1;
    while (true)
    {
      if (i != 0)
      {
        while ((this.mediaChunks.size() > 1) && (((BaseMediaChunk)this.mediaChunks.get(1)).getFirstSampleIndex() <= this.sampleQueue.getReadIndex()))
          this.mediaChunks.removeFirst();
        i = 0;
        continue;
      }
      this.pendingResetPositionUs = paramLong;
      this.loadingFinished = false;
      this.mediaChunks.clear();
      if (!this.loader.isLoading())
        break label115;
      this.loader.cancelLoading();
    }
    return;
    label115: this.sampleQueue.reset(true);
  }

  public void skipToKeyframeBefore(long paramLong)
  {
    this.sampleQueue.skipToKeyframeBefore(paramLong);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ChunkSampleStream
 * JD-Core Version:    0.6.0
 */