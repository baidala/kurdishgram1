package org.vidogram.messenger.exoplayer2.source;

import android.net.Uri;
import android.os.Handler;
import android.util.SparseArray;
import java.io.IOException;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.FormatHolder;
import org.vidogram.messenger.exoplayer2.decoder.DecoderInputBuffer;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput;
import org.vidogram.messenger.exoplayer2.extractor.DefaultTrackOutput.UpstreamFormatChangedListener;
import org.vidogram.messenger.exoplayer2.extractor.Extractor;
import org.vidogram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.vidogram.messenger.exoplayer2.extractor.PositionHolder;
import org.vidogram.messenger.exoplayer2.extractor.SeekMap;
import org.vidogram.messenger.exoplayer2.extractor.TrackOutput;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.Allocator;
import org.vidogram.messenger.exoplayer2.upstream.DataSource;
import org.vidogram.messenger.exoplayer2.upstream.Loader;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Callback;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Loadable;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.ConditionVariable;
import org.vidogram.messenger.exoplayer2.util.MimeTypes;

final class ExtractorMediaPeriod
  implements DefaultTrackOutput.UpstreamFormatChangedListener, ExtractorOutput, MediaPeriod, Loader.Callback<ExtractingLoadable>
{
  private static final long DEFAULT_LAST_SAMPLE_DURATION_US = 10000L;
  private final Allocator allocator;
  private MediaPeriod.Callback callback;
  private final DataSource dataSource;
  private long durationUs;
  private int enabledTrackCount;
  private final Handler eventHandler;
  private final ExtractorMediaSource.EventListener eventListener;
  private int extractedSamplesCountAtStartOfLoad;
  private final ExtractorHolder extractorHolder;
  private final Handler handler;
  private boolean haveAudioVideoTracks;
  private long lastSeekPositionUs;
  private long length;
  private final ConditionVariable loadCondition;
  private final Loader loader;
  private boolean loadingFinished;
  private final Runnable maybeFinishPrepareRunnable;
  private final int minLoadableRetryCount;
  private boolean notifyReset;
  private final Runnable onContinueLoadingRequestedRunnable;
  private long pendingResetPositionUs;
  private boolean prepared;
  private boolean released;
  private final SparseArray<DefaultTrackOutput> sampleQueues;
  private SeekMap seekMap;
  private boolean seenFirstTrackSelection;
  private final MediaSource.Listener sourceListener;
  private boolean[] trackEnabledStates;
  private boolean[] trackIsAudioVideoFlags;
  private TrackGroupArray tracks;
  private boolean tracksBuilt;
  private final Uri uri;

  public ExtractorMediaPeriod(Uri paramUri, DataSource paramDataSource, Extractor[] paramArrayOfExtractor, int paramInt, Handler paramHandler, ExtractorMediaSource.EventListener paramEventListener, MediaSource.Listener paramListener, Allocator paramAllocator)
  {
    this.uri = paramUri;
    this.dataSource = paramDataSource;
    this.minLoadableRetryCount = paramInt;
    this.eventHandler = paramHandler;
    this.eventListener = paramEventListener;
    this.sourceListener = paramListener;
    this.allocator = paramAllocator;
    this.loader = new Loader("Loader:ExtractorMediaPeriod");
    this.extractorHolder = new ExtractorHolder(paramArrayOfExtractor, this);
    this.loadCondition = new ConditionVariable();
    this.maybeFinishPrepareRunnable = new Runnable()
    {
      public void run()
      {
        ExtractorMediaPeriod.this.maybeFinishPrepare();
      }
    };
    this.onContinueLoadingRequestedRunnable = new Runnable()
    {
      public void run()
      {
        if (!ExtractorMediaPeriod.this.released)
          ExtractorMediaPeriod.this.callback.onContinueLoadingRequested(ExtractorMediaPeriod.this);
      }
    };
    this.handler = new Handler();
    this.pendingResetPositionUs = -9223372036854775807L;
    this.sampleQueues = new SparseArray();
    this.length = -1L;
  }

  private void configureRetry(ExtractingLoadable paramExtractingLoadable)
  {
    if ((this.length != -1L) || ((this.seekMap != null) && (this.seekMap.getDurationUs() != -9223372036854775807L)))
      return;
    this.lastSeekPositionUs = 0L;
    this.notifyReset = this.prepared;
    int j = this.sampleQueues.size();
    int i = 0;
    if (i < j)
    {
      DefaultTrackOutput localDefaultTrackOutput = (DefaultTrackOutput)this.sampleQueues.valueAt(i);
      if ((!this.prepared) || (this.trackEnabledStates[i] != 0));
      for (boolean bool = true; ; bool = false)
      {
        localDefaultTrackOutput.reset(bool);
        i += 1;
        break;
      }
    }
    paramExtractingLoadable.setLoadPosition(0L);
  }

  private void copyLengthFromLoader(ExtractingLoadable paramExtractingLoadable)
  {
    if (this.length == -1L)
      this.length = paramExtractingLoadable.length;
  }

  private int getExtractedSamplesCount()
  {
    int k = this.sampleQueues.size();
    int i = 0;
    int j = 0;
    while (i < k)
    {
      j += ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getWriteIndex();
      i += 1;
    }
    return j;
  }

  private long getLargestQueuedTimestampUs()
  {
    long l = -9223372036854775808L;
    int j = this.sampleQueues.size();
    int i = 0;
    while (i < j)
    {
      l = Math.max(l, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
      i += 1;
    }
    return l;
  }

  private boolean isLoadableExceptionFatal(IOException paramIOException)
  {
    return paramIOException instanceof ExtractorMediaSource.UnrecognizedInputFormatException;
  }

  private boolean isPendingReset()
  {
    return this.pendingResetPositionUs != -9223372036854775807L;
  }

  private void maybeFinishPrepare()
  {
    if ((this.released) || (this.prepared) || (this.seekMap == null) || (!this.tracksBuilt))
      return;
    int j = this.sampleQueues.size();
    int i = 0;
    while (true)
    {
      if (i >= j)
        break label68;
      if (((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat() == null)
        break;
      i += 1;
    }
    label68: this.loadCondition.close();
    TrackGroup[] arrayOfTrackGroup = new TrackGroup[j];
    this.trackIsAudioVideoFlags = new boolean[j];
    this.trackEnabledStates = new boolean[j];
    this.durationUs = this.seekMap.getDurationUs();
    i = 0;
    if (i < j)
    {
      Object localObject = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getUpstreamFormat();
      arrayOfTrackGroup[i] = new TrackGroup(new Format[] { localObject });
      localObject = ((Format)localObject).sampleMimeType;
      if ((MimeTypes.isVideo((String)localObject)) || (MimeTypes.isAudio((String)localObject)));
      for (int k = 1; ; k = 0)
      {
        this.trackIsAudioVideoFlags[i] = k;
        this.haveAudioVideoTracks = (k | this.haveAudioVideoTracks);
        i += 1;
        break;
      }
    }
    this.tracks = new TrackGroupArray(arrayOfTrackGroup);
    this.prepared = true;
    this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
    this.callback.onPrepared(this);
  }

  private void notifyLoadError(IOException paramIOException)
  {
    if ((this.eventHandler != null) && (this.eventListener != null))
      this.eventHandler.post(new Runnable(paramIOException)
      {
        public void run()
        {
          ExtractorMediaPeriod.this.eventListener.onLoadError(this.val$error);
        }
      });
  }

  private void startLoading()
  {
    ExtractingLoadable localExtractingLoadable = new ExtractingLoadable(this.uri, this.dataSource, this.extractorHolder, this.loadCondition);
    if (this.prepared)
    {
      Assertions.checkState(isPendingReset());
      if ((this.durationUs != -9223372036854775807L) && (this.pendingResetPositionUs >= this.durationUs))
      {
        this.loadingFinished = true;
        this.pendingResetPositionUs = -9223372036854775807L;
        return;
      }
      localExtractingLoadable.setLoadPosition(this.seekMap.getPosition(this.pendingResetPositionUs));
      this.pendingResetPositionUs = -9223372036854775807L;
    }
    this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
    int j = this.minLoadableRetryCount;
    int i = j;
    if (j == -1)
      if ((this.prepared) && (this.length == -1L) && ((this.seekMap == null) || (this.seekMap.getDurationUs() == -9223372036854775807L)))
        break label174;
    label174: for (i = 3; ; i = 6)
    {
      this.loader.startLoading(localExtractingLoadable, this, i);
      return;
    }
  }

  public boolean continueLoading(long paramLong)
  {
    boolean bool;
    if ((this.loadingFinished) || ((this.prepared) && (this.enabledTrackCount == 0)))
      bool = false;
    do
    {
      return bool;
      bool = this.loadCondition.open();
    }
    while (this.loader.isLoading());
    startLoading();
    return true;
  }

  public void endTracks()
  {
    this.tracksBuilt = true;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }

  public long getBufferedPositionUs()
  {
    long l2;
    if (this.loadingFinished)
    {
      l2 = -9223372036854775808L;
      return l2;
    }
    if (isPendingReset())
      return this.pendingResetPositionUs;
    long l1;
    int i;
    if (this.haveAudioVideoTracks)
    {
      l1 = 9223372036854775807L;
      int j = this.sampleQueues.size();
      i = 0;
      label48: if (i < j)
      {
        if (this.trackIsAudioVideoFlags[i] == 0)
          break label112;
        l1 = Math.min(l1, ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).getLargestQueuedTimestampUs());
      }
    }
    label112: 
    while (true)
    {
      i += 1;
      break label48;
      while (true)
      {
        l2 = l1;
        if (l1 != -9223372036854775808L)
          break;
        return this.lastSeekPositionUs;
        l1 = getLargestQueuedTimestampUs();
      }
    }
  }

  public long getNextLoadPositionUs()
  {
    return getBufferedPositionUs();
  }

  public TrackGroupArray getTrackGroups()
  {
    return this.tracks;
  }

  boolean isReady(int paramInt)
  {
    return (this.loadingFinished) || ((!isPendingReset()) && (!((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).isEmpty()));
  }

  void maybeThrowError()
  {
    this.loader.maybeThrowError();
  }

  public void maybeThrowPrepareError()
  {
    maybeThrowError();
  }

  public void onLoadCanceled(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    copyLengthFromLoader(paramExtractingLoadable);
    if ((!paramBoolean) && (this.enabledTrackCount > 0))
    {
      int j = this.sampleQueues.size();
      int i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
        i += 1;
      }
      this.callback.onContinueLoadingRequested(this);
    }
  }

  public void onLoadCompleted(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2)
  {
    copyLengthFromLoader(paramExtractingLoadable);
    this.loadingFinished = true;
    if (this.durationUs == -9223372036854775807L)
    {
      paramLong1 = getLargestQueuedTimestampUs();
      if (paramLong1 != -9223372036854775808L)
        break label72;
      paramLong1 = 0L;
    }
    while (true)
    {
      this.durationUs = paramLong1;
      this.sourceListener.onSourceInfoRefreshed(new SinglePeriodTimeline(this.durationUs, this.seekMap.isSeekable()), null);
      return;
      label72: paramLong1 += 10000L;
    }
  }

  public int onLoadError(ExtractingLoadable paramExtractingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    int j = 1;
    copyLengthFromLoader(paramExtractingLoadable);
    notifyLoadError(paramIOException);
    if (isLoadableExceptionFatal(paramIOException))
    {
      j = 3;
      return j;
    }
    if (getExtractedSamplesCount() > this.extractedSamplesCountAtStartOfLoad);
    for (int i = 1; ; i = 0)
    {
      configureRetry(paramExtractingLoadable);
      this.extractedSamplesCountAtStartOfLoad = getExtractedSamplesCount();
      if (i != 0)
        break;
      return 0;
    }
  }

  public void onUpstreamFormatChanged(Format paramFormat)
  {
    this.handler.post(this.maybeFinishPrepareRunnable);
  }

  public void prepare(MediaPeriod.Callback paramCallback)
  {
    this.callback = paramCallback;
    this.loadCondition.open();
    startLoading();
  }

  int readData(int paramInt, FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
  {
    if ((this.notifyReset) || (isPendingReset()))
      return -3;
    return ((DefaultTrackOutput)this.sampleQueues.valueAt(paramInt)).readData(paramFormatHolder, paramDecoderInputBuffer, this.loadingFinished, this.lastSeekPositionUs);
  }

  public long readDiscontinuity()
  {
    if (this.notifyReset)
    {
      this.notifyReset = false;
      return this.lastSeekPositionUs;
    }
    return -9223372036854775807L;
  }

  public void release()
  {
    ExtractorHolder localExtractorHolder = this.extractorHolder;
    this.loader.release(new Runnable(localExtractorHolder)
    {
      public void run()
      {
        this.val$extractorHolder.release();
        int j = ExtractorMediaPeriod.this.sampleQueues.size();
        int i = 0;
        while (i < j)
        {
          ((DefaultTrackOutput)ExtractorMediaPeriod.this.sampleQueues.valueAt(i)).disable();
          i += 1;
        }
      }
    });
    this.handler.removeCallbacksAndMessages(null);
    this.released = true;
  }

  public void seekMap(SeekMap paramSeekMap)
  {
    this.seekMap = paramSeekMap;
    this.handler.post(this.maybeFinishPrepareRunnable);
  }

  public long seekToUs(long paramLong)
  {
    int j;
    if (this.seekMap.isSeekable())
    {
      this.lastSeekPositionUs = paramLong;
      j = this.sampleQueues.size();
      if (isPendingReset())
        break label87;
    }
    int i;
    label87: for (boolean bool = true; ; bool = false)
    {
      i = 0;
      while ((bool) && (i < j))
      {
        if (this.trackEnabledStates[i] != 0)
          bool = ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).skipToKeyframeBefore(paramLong);
        i += 1;
      }
      paramLong = 0L;
      break;
    }
    if (!bool)
    {
      this.pendingResetPositionUs = paramLong;
      this.loadingFinished = false;
      if (!this.loader.isLoading())
        break label132;
      this.loader.cancelLoading();
    }
    while (true)
    {
      this.notifyReset = false;
      return paramLong;
      label132: i = 0;
      while (i < j)
      {
        ((DefaultTrackOutput)this.sampleQueues.valueAt(i)).reset(this.trackEnabledStates[i]);
        i += 1;
      }
    }
  }

  public long selectTracks(TrackSelection[] paramArrayOfTrackSelection, boolean[] paramArrayOfBoolean1, SampleStream[] paramArrayOfSampleStream, boolean[] paramArrayOfBoolean2, long paramLong)
  {
    int m = 0;
    Assertions.checkState(this.prepared);
    int i = 0;
    while (i < paramArrayOfTrackSelection.length)
    {
      if ((paramArrayOfSampleStream[i] != null) && ((paramArrayOfTrackSelection[i] == null) || (paramArrayOfBoolean1[i] == 0)))
      {
        j = ((SampleStreamImpl)paramArrayOfSampleStream[i]).track;
        Assertions.checkState(this.trackEnabledStates[j]);
        this.enabledTrackCount -= 1;
        this.trackEnabledStates[j] = false;
        ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).disable();
        paramArrayOfSampleStream[i] = null;
      }
      i += 1;
    }
    int j = 0;
    i = 0;
    int k;
    if (j < paramArrayOfTrackSelection.length)
    {
      k = i;
      if (paramArrayOfSampleStream[j] == null)
      {
        k = i;
        if (paramArrayOfTrackSelection[j] != null)
        {
          paramArrayOfBoolean1 = paramArrayOfTrackSelection[j];
          if (paramArrayOfBoolean1.length() != 1)
            break label273;
          bool = true;
          label163: Assertions.checkState(bool);
          if (paramArrayOfBoolean1.getIndexInTrackGroup(0) != 0)
            break label279;
          bool = true;
          label181: Assertions.checkState(bool);
          i = this.tracks.indexOf(paramArrayOfBoolean1.getTrackGroup());
          if (this.trackEnabledStates[i] != 0)
            break label285;
        }
      }
      label273: label279: label285: for (boolean bool = true; ; bool = false)
      {
        Assertions.checkState(bool);
        this.enabledTrackCount += 1;
        this.trackEnabledStates[i] = true;
        paramArrayOfSampleStream[j] = new SampleStreamImpl(i);
        paramArrayOfBoolean2[j] = true;
        k = 1;
        j += 1;
        i = k;
        break;
        bool = false;
        break label163;
        bool = false;
        break label181;
      }
    }
    if (!this.seenFirstTrackSelection)
    {
      k = this.sampleQueues.size();
      j = 0;
      while (j < k)
      {
        if (this.trackEnabledStates[j] == 0)
          ((DefaultTrackOutput)this.sampleQueues.valueAt(j)).disable();
        j += 1;
      }
    }
    long l;
    if (this.enabledTrackCount == 0)
    {
      this.notifyReset = false;
      l = paramLong;
      if (this.loader.isLoading())
      {
        this.loader.cancelLoading();
        l = paramLong;
      }
    }
    do
    {
      this.seenFirstTrackSelection = true;
      return l;
      if (!this.seenFirstTrackSelection)
        break;
      l = paramLong;
    }
    while (i == 0);
    while (true)
    {
      paramLong = seekToUs(paramLong);
      i = m;
      while (true)
      {
        l = paramLong;
        if (i >= paramArrayOfSampleStream.length)
          break;
        if (paramArrayOfSampleStream[i] != null)
          paramArrayOfBoolean2[i] = true;
        i += 1;
      }
      l = paramLong;
      if (paramLong == 0L)
        break;
    }
  }

  public TrackOutput track(int paramInt)
  {
    DefaultTrackOutput localDefaultTrackOutput2 = (DefaultTrackOutput)this.sampleQueues.get(paramInt);
    DefaultTrackOutput localDefaultTrackOutput1 = localDefaultTrackOutput2;
    if (localDefaultTrackOutput2 == null)
    {
      localDefaultTrackOutput1 = new DefaultTrackOutput(this.allocator);
      localDefaultTrackOutput1.setUpstreamFormatChangeListener(this);
      this.sampleQueues.put(paramInt, localDefaultTrackOutput1);
    }
    return localDefaultTrackOutput1;
  }

  final class ExtractingLoadable
    implements Loader.Loadable
  {
    private static final int CONTINUE_LOADING_CHECK_INTERVAL_BYTES = 1048576;
    private final DataSource dataSource;
    private final ExtractorMediaPeriod.ExtractorHolder extractorHolder;
    private long length;
    private volatile boolean loadCanceled;
    private final ConditionVariable loadCondition;
    private boolean pendingExtractorSeek;
    private final PositionHolder positionHolder;
    private final Uri uri;

    public ExtractingLoadable(Uri paramDataSource, DataSource paramExtractorHolder, ExtractorMediaPeriod.ExtractorHolder paramConditionVariable, ConditionVariable arg5)
    {
      this.uri = ((Uri)Assertions.checkNotNull(paramDataSource));
      this.dataSource = ((DataSource)Assertions.checkNotNull(paramExtractorHolder));
      this.extractorHolder = ((ExtractorMediaPeriod.ExtractorHolder)Assertions.checkNotNull(paramConditionVariable));
      Object localObject;
      this.loadCondition = localObject;
      this.positionHolder = new PositionHolder();
      this.pendingExtractorSeek = true;
      this.length = -1L;
    }

    public void cancelLoad()
    {
      this.loadCanceled = true;
    }

    public boolean isLoadCanceled()
    {
      return this.loadCanceled;
    }

    // ERROR //
    public void load()
    {
      // Byte code:
      //   0: iconst_0
      //   1: istore_1
      //   2: iload_1
      //   3: ifne +289 -> 292
      //   6: aload_0
      //   7: getfield 73	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:loadCanceled	Z
      //   10: ifne +282 -> 292
      //   13: aload_0
      //   14: getfield 61	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:positionHolder	Lorg/vidogram/messenger/exoplayer2/extractor/PositionHolder;
      //   17: getfield 79	org/vidogram/messenger/exoplayer2/extractor/PositionHolder:position	J
      //   20: lstore_3
      //   21: aload_0
      //   22: aload_0
      //   23: getfield 50	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:dataSource	Lorg/vidogram/messenger/exoplayer2/upstream/DataSource;
      //   26: new 81	org/vidogram/messenger/exoplayer2/upstream/DataSpec
      //   29: dup
      //   30: aload_0
      //   31: getfield 46	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:uri	Landroid/net/Uri;
      //   34: lload_3
      //   35: ldc2_w 64
      //   38: aconst_null
      //   39: invokespecial 84	org/vidogram/messenger/exoplayer2/upstream/DataSpec:<init>	(Landroid/net/Uri;JJLjava/lang/String;)V
      //   42: invokeinterface 88 2 0
      //   47: putfield 67	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:length	J
      //   50: aload_0
      //   51: getfield 67	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:length	J
      //   54: ldc2_w 64
      //   57: lcmp
      //   58: ifeq +13 -> 71
      //   61: aload_0
      //   62: aload_0
      //   63: getfield 67	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:length	J
      //   66: lload_3
      //   67: ladd
      //   68: putfield 67	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:length	J
      //   71: new 90	org/vidogram/messenger/exoplayer2/extractor/DefaultExtractorInput
      //   74: dup
      //   75: aload_0
      //   76: getfield 50	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:dataSource	Lorg/vidogram/messenger/exoplayer2/upstream/DataSource;
      //   79: lload_3
      //   80: aload_0
      //   81: getfield 67	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:length	J
      //   84: invokespecial 93	org/vidogram/messenger/exoplayer2/extractor/DefaultExtractorInput:<init>	(Lorg/vidogram/messenger/exoplayer2/upstream/DataSource;JJ)V
      //   87: astore 5
      //   89: aload_0
      //   90: getfield 54	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:extractorHolder	Lorg/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder;
      //   93: aload 5
      //   95: invokevirtual 97	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:selectExtractor	(Lorg/vidogram/messenger/exoplayer2/extractor/ExtractorInput;)Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   98: astore 6
      //   100: aload_0
      //   101: getfield 63	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:pendingExtractorSeek	Z
      //   104: ifeq +16 -> 120
      //   107: aload 6
      //   109: lload_3
      //   110: invokeinterface 103 3 0
      //   115: aload_0
      //   116: iconst_0
      //   117: putfield 63	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:pendingExtractorSeek	Z
      //   120: iload_1
      //   121: ifne +86 -> 207
      //   124: aload_0
      //   125: getfield 73	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:loadCanceled	Z
      //   128: ifne +79 -> 207
      //   131: aload_0
      //   132: getfield 56	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:loadCondition	Lorg/vidogram/messenger/exoplayer2/util/ConditionVariable;
      //   135: invokevirtual 108	org/vidogram/messenger/exoplayer2/util/ConditionVariable:block	()V
      //   138: aload 6
      //   140: aload 5
      //   142: aload_0
      //   143: getfield 61	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:positionHolder	Lorg/vidogram/messenger/exoplayer2/extractor/PositionHolder;
      //   146: invokeinterface 112 3 0
      //   151: istore_2
      //   152: iload_2
      //   153: istore_1
      //   154: aload 5
      //   156: invokeinterface 118 1 0
      //   161: ldc2_w 119
      //   164: lload_3
      //   165: ladd
      //   166: lcmp
      //   167: ifle +169 -> 336
      //   170: aload 5
      //   172: invokeinterface 118 1 0
      //   177: lstore_3
      //   178: aload_0
      //   179: getfield 56	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:loadCondition	Lorg/vidogram/messenger/exoplayer2/util/ConditionVariable;
      //   182: invokevirtual 123	org/vidogram/messenger/exoplayer2/util/ConditionVariable:close	()Z
      //   185: pop
      //   186: aload_0
      //   187: getfield 33	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:this$0	Lorg/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod;
      //   190: invokestatic 127	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod:access$800	(Lorg/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod;)Landroid/os/Handler;
      //   193: aload_0
      //   194: getfield 33	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:this$0	Lorg/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod;
      //   197: invokestatic 131	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod:access$700	(Lorg/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod;)Ljava/lang/Runnable;
      //   200: invokevirtual 137	android/os/Handler:post	(Ljava/lang/Runnable;)Z
      //   203: pop
      //   204: goto -84 -> 120
      //   207: iload_1
      //   208: iconst_1
      //   209: if_icmpne +17 -> 226
      //   212: iconst_0
      //   213: istore_1
      //   214: aload_0
      //   215: getfield 50	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:dataSource	Lorg/vidogram/messenger/exoplayer2/upstream/DataSource;
      //   218: invokeinterface 139 1 0
      //   223: goto -221 -> 2
      //   226: aload 5
      //   228: ifnull +17 -> 245
      //   231: aload_0
      //   232: getfield 61	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:positionHolder	Lorg/vidogram/messenger/exoplayer2/extractor/PositionHolder;
      //   235: aload 5
      //   237: invokeinterface 118 1 0
      //   242: putfield 79	org/vidogram/messenger/exoplayer2/extractor/PositionHolder:position	J
      //   245: goto -31 -> 214
      //   248: astore 5
      //   250: aconst_null
      //   251: astore 6
      //   253: iload_1
      //   254: iconst_1
      //   255: if_icmpne +15 -> 270
      //   258: aload_0
      //   259: getfield 50	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:dataSource	Lorg/vidogram/messenger/exoplayer2/upstream/DataSource;
      //   262: invokeinterface 139 1 0
      //   267: aload 5
      //   269: athrow
      //   270: aload 6
      //   272: ifnull -14 -> 258
      //   275: aload_0
      //   276: getfield 61	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractingLoadable:positionHolder	Lorg/vidogram/messenger/exoplayer2/extractor/PositionHolder;
      //   279: aload 6
      //   281: invokeinterface 118 1 0
      //   286: putfield 79	org/vidogram/messenger/exoplayer2/extractor/PositionHolder:position	J
      //   289: goto -31 -> 258
      //   292: return
      //   293: astore 6
      //   295: aload 5
      //   297: astore 7
      //   299: aload 6
      //   301: astore 5
      //   303: aload 7
      //   305: astore 6
      //   307: goto -54 -> 253
      //   310: astore 7
      //   312: aload 5
      //   314: astore 6
      //   316: aload 7
      //   318: astore 5
      //   320: goto -67 -> 253
      //   323: astore 7
      //   325: aload 5
      //   327: astore 6
      //   329: aload 7
      //   331: astore 5
      //   333: goto -80 -> 253
      //   336: goto -216 -> 120
      //
      // Exception table:
      //   from	to	target	type
      //   13	71	248	finally
      //   71	89	248	finally
      //   89	120	293	finally
      //   124	152	310	finally
      //   154	204	323	finally
    }

    public void setLoadPosition(long paramLong)
    {
      this.positionHolder.position = paramLong;
      this.pendingExtractorSeek = true;
    }
  }

  private static final class ExtractorHolder
  {
    private Extractor extractor;
    private final ExtractorOutput extractorOutput;
    private final Extractor[] extractors;

    public ExtractorHolder(Extractor[] paramArrayOfExtractor, ExtractorOutput paramExtractorOutput)
    {
      this.extractors = paramArrayOfExtractor;
      this.extractorOutput = paramExtractorOutput;
    }

    public void release()
    {
      if (this.extractor != null)
      {
        this.extractor.release();
        this.extractor = null;
      }
    }

    // ERROR //
    public Extractor selectExtractor(org.vidogram.messenger.exoplayer2.extractor.ExtractorInput paramExtractorInput)
    {
      // Byte code:
      //   0: aload_0
      //   1: getfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   4: ifnull +8 -> 12
      //   7: aload_0
      //   8: getfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   11: areturn
      //   12: aload_0
      //   13: getfield 20	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   16: astore 4
      //   18: aload 4
      //   20: arraylength
      //   21: istore_3
      //   22: iconst_0
      //   23: istore_2
      //   24: iload_2
      //   25: iload_3
      //   26: if_icmpge +32 -> 58
      //   29: aload 4
      //   31: iload_2
      //   32: aaload
      //   33: astore 5
      //   35: aload 5
      //   37: aload_1
      //   38: invokeinterface 38 2 0
      //   43: ifeq +34 -> 77
      //   46: aload_0
      //   47: aload 5
      //   49: putfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   52: aload_1
      //   53: invokeinterface 43 1 0
      //   58: aload_0
      //   59: getfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   62: ifnonnull +50 -> 112
      //   65: new 45	org/vidogram/messenger/exoplayer2/source/ExtractorMediaSource$UnrecognizedInputFormatException
      //   68: dup
      //   69: aload_0
      //   70: getfield 20	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractors	[Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   73: invokespecial 48	org/vidogram/messenger/exoplayer2/source/ExtractorMediaSource$UnrecognizedInputFormatException:<init>	([Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;)V
      //   76: athrow
      //   77: aload_1
      //   78: invokeinterface 43 1 0
      //   83: iload_2
      //   84: iconst_1
      //   85: iadd
      //   86: istore_2
      //   87: goto -63 -> 24
      //   90: astore 5
      //   92: aload_1
      //   93: invokeinterface 43 1 0
      //   98: goto -15 -> 83
      //   101: astore 4
      //   103: aload_1
      //   104: invokeinterface 43 1 0
      //   109: aload 4
      //   111: athrow
      //   112: aload_0
      //   113: getfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   116: aload_0
      //   117: getfield 22	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractorOutput	Lorg/vidogram/messenger/exoplayer2/extractor/ExtractorOutput;
      //   120: invokeinterface 52 2 0
      //   125: aload_0
      //   126: getfield 26	org/vidogram/messenger/exoplayer2/source/ExtractorMediaPeriod$ExtractorHolder:extractor	Lorg/vidogram/messenger/exoplayer2/extractor/Extractor;
      //   129: areturn
      //
      // Exception table:
      //   from	to	target	type
      //   35	52	90	java/io/EOFException
      //   35	52	101	finally
    }
  }

  private final class SampleStreamImpl
    implements SampleStream
  {
    private final int track;

    public SampleStreamImpl(int arg2)
    {
      int i;
      this.track = i;
    }

    public boolean isReady()
    {
      return ExtractorMediaPeriod.this.isReady(this.track);
    }

    public void maybeThrowError()
    {
      ExtractorMediaPeriod.this.maybeThrowError();
    }

    public int readData(FormatHolder paramFormatHolder, DecoderInputBuffer paramDecoderInputBuffer)
    {
      return ExtractorMediaPeriod.this.readData(this.track, paramFormatHolder, paramDecoderInputBuffer);
    }

    public void skipToKeyframeBefore(long paramLong)
    {
      ((DefaultTrackOutput)ExtractorMediaPeriod.this.sampleQueues.valueAt(this.track)).skipToKeyframeBefore(paramLong);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.ExtractorMediaPeriod
 * JD-Core Version:    0.6.0
 */