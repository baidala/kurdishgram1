package org.vidogram.messenger.exoplayer2.source.hls.playlist;

import android.net.Uri;
import android.os.Handler;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import org.vidogram.messenger.exoplayer2.ParserException;
import org.vidogram.messenger.exoplayer2.source.AdaptiveMediaSourceEventListener.EventDispatcher;
import org.vidogram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.vidogram.messenger.exoplayer2.upstream.Loader;
import org.vidogram.messenger.exoplayer2.upstream.Loader.Callback;
import org.vidogram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.vidogram.messenger.exoplayer2.util.UriUtil;

public final class HlsPlaylistTracker
  implements Loader.Callback<ParsingLoadable<HlsPlaylist>>
{
  private static final long PLAYLIST_REFRESH_PERIOD_MS = 5000L;
  private static final long TIMESTAMP_ADJUSTMENT_THRESHOLD_US = 500000L;
  private final DataSource.Factory dataSourceFactory;
  private final AdaptiveMediaSourceEventListener.EventDispatcher eventDispatcher;
  private final Loader initialPlaylistLoader;
  private final Uri initialPlaylistUri;
  private boolean isLive;
  private HlsMasterPlaylist masterPlaylist;
  private final int minRetryCount;
  private final IdentityHashMap<HlsMasterPlaylist.HlsUrl, MediaPlaylistBundle> playlistBundles;
  private final HlsPlaylistParser playlistParser;
  private final Handler playlistRefreshHandler;
  private HlsMasterPlaylist.HlsUrl primaryHlsUrl;
  private final PrimaryPlaylistListener primaryPlaylistListener;

  public HlsPlaylistTracker(Uri paramUri, DataSource.Factory paramFactory, AdaptiveMediaSourceEventListener.EventDispatcher paramEventDispatcher, int paramInt, PrimaryPlaylistListener paramPrimaryPlaylistListener)
  {
    this.initialPlaylistUri = paramUri;
    this.dataSourceFactory = paramFactory;
    this.eventDispatcher = paramEventDispatcher;
    this.minRetryCount = paramInt;
    this.primaryPlaylistListener = paramPrimaryPlaylistListener;
    this.initialPlaylistLoader = new Loader("HlsPlaylistTracker:MasterPlaylist");
    this.playlistParser = new HlsPlaylistParser();
    this.playlistBundles = new IdentityHashMap();
    this.playlistRefreshHandler = new Handler();
  }

  private HlsMediaPlaylist adjustPlaylistTimestamps(HlsMediaPlaylist paramHlsMediaPlaylist1, HlsMediaPlaylist paramHlsMediaPlaylist2)
  {
    HlsMediaPlaylist localHlsMediaPlaylist = ((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).latestPlaylistSnapshot;
    Object localObject1;
    if (paramHlsMediaPlaylist1 == null)
      if (localHlsMediaPlaylist == null)
        localObject1 = paramHlsMediaPlaylist2;
    Object localObject2;
    int k;
    int j;
    int i;
    do
    {
      do
      {
        return localObject1;
        return paramHlsMediaPlaylist2.copyWithStartTimeUs(localHlsMediaPlaylist.getStartTimeUs());
        localObject2 = paramHlsMediaPlaylist1.segments;
        k = ((List)localObject2).size();
        j = paramHlsMediaPlaylist2.segments.size();
        i = paramHlsMediaPlaylist2.mediaSequence - paramHlsMediaPlaylist1.mediaSequence;
        if ((j != k) || (i != 0))
          break;
        localObject1 = paramHlsMediaPlaylist1;
      }
      while (paramHlsMediaPlaylist1.hasEndTag == paramHlsMediaPlaylist2.hasEndTag);
      localObject1 = paramHlsMediaPlaylist1;
    }
    while (i < 0);
    if (i <= k)
    {
      localObject1 = new ArrayList(j);
      while (i < k)
      {
        ((ArrayList)localObject1).add(((List)localObject2).get(i));
        i += 1;
      }
      paramHlsMediaPlaylist1 = (HlsMediaPlaylist.Segment)((List)localObject2).get(k - 1);
      i = ((ArrayList)localObject1).size();
      while (i < j)
      {
        localObject2 = (HlsMediaPlaylist.Segment)paramHlsMediaPlaylist2.segments.get(i);
        long l = paramHlsMediaPlaylist1.startTimeUs;
        paramHlsMediaPlaylist1 = ((HlsMediaPlaylist.Segment)localObject2).copyWithStartTimeUs(paramHlsMediaPlaylist1.durationUs + l);
        ((ArrayList)localObject1).add(paramHlsMediaPlaylist1);
        i += 1;
      }
      return paramHlsMediaPlaylist2.copyWithSegments((List)localObject1);
    }
    return (HlsMediaPlaylist)(HlsMediaPlaylist)paramHlsMediaPlaylist2.copyWithStartTimeUs(localHlsMediaPlaylist.getStartTimeUs());
  }

  private void createBundles(List<HlsMasterPlaylist.HlsUrl> paramList)
  {
    int j = paramList.size();
    int i = 0;
    while (i < j)
    {
      MediaPlaylistBundle localMediaPlaylistBundle = new MediaPlaylistBundle((HlsMasterPlaylist.HlsUrl)paramList.get(i));
      this.playlistBundles.put(paramList.get(i), localMediaPlaylistBundle);
      i += 1;
    }
  }

  private boolean onPlaylistUpdated(HlsMasterPlaylist.HlsUrl paramHlsUrl, HlsMediaPlaylist paramHlsMediaPlaylist, boolean paramBoolean)
  {
    if (paramHlsUrl == this.primaryHlsUrl)
    {
      if (paramBoolean)
        if (paramHlsMediaPlaylist.hasEndTag)
          break label45;
      label45: for (paramBoolean = true; ; paramBoolean = false)
      {
        this.isLive = paramBoolean;
        this.primaryPlaylistListener.onPrimaryPlaylistRefreshed(paramHlsMediaPlaylist);
        if (paramHlsMediaPlaylist.hasEndTag)
          break;
        return true;
      }
      return false;
    }
    return false;
  }

  public HlsMasterPlaylist getMasterPlaylist()
  {
    return this.masterPlaylist;
  }

  public HlsMediaPlaylist getPlaylistSnapshot(HlsMasterPlaylist.HlsUrl paramHlsUrl)
  {
    return ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).latestPlaylistSnapshot;
  }

  public boolean isLive()
  {
    return this.isLive;
  }

  public void maybeThrowPrimaryPlaylistRefreshError()
  {
    this.initialPlaylistLoader.maybeThrowError();
    if (this.primaryHlsUrl != null)
      ((MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl)).mediaPlaylistLoader.maybeThrowError();
  }

  public void onChunkLoaded(HlsMasterPlaylist.HlsUrl paramHlsUrl, int paramInt, long paramLong)
  {
    ((MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl)).adjustTimestampsOfPlaylist(paramInt, paramLong);
  }

  public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
  {
    this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
  }

  public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
  {
    HlsPlaylist localHlsPlaylist = (HlsPlaylist)paramParsingLoadable.getResult();
    boolean bool = localHlsPlaylist instanceof HlsMediaPlaylist;
    Object localObject;
    if (bool)
    {
      localObject = HlsMasterPlaylist.createSingleVariantMasterPlaylist(localHlsPlaylist.baseUri);
      this.masterPlaylist = ((HlsMasterPlaylist)localObject);
      this.primaryHlsUrl = ((HlsMasterPlaylist.HlsUrl)((HlsMasterPlaylist)localObject).variants.get(0));
      ArrayList localArrayList = new ArrayList();
      localArrayList.addAll(((HlsMasterPlaylist)localObject).variants);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).audios);
      localArrayList.addAll(((HlsMasterPlaylist)localObject).subtitles);
      createBundles(localArrayList);
      localObject = (MediaPlaylistBundle)this.playlistBundles.get(this.primaryHlsUrl);
      if (!bool)
        break label164;
      ((MediaPlaylistBundle)localObject).processLoadedPlaylist((HlsMediaPlaylist)localHlsPlaylist);
    }
    while (true)
    {
      this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
      return;
      localObject = (HlsMasterPlaylist)localHlsPlaylist;
      break;
      label164: ((MediaPlaylistBundle)localObject).loadPlaylist();
    }
  }

  public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
  {
    boolean bool = paramIOException instanceof ParserException;
    this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
    if (bool)
      return 3;
    return 0;
  }

  public void refreshPlaylist(HlsMasterPlaylist.HlsUrl paramHlsUrl, PlaylistRefreshCallback paramPlaylistRefreshCallback)
  {
    paramHlsUrl = (MediaPlaylistBundle)this.playlistBundles.get(paramHlsUrl);
    paramHlsUrl.setCallback(paramPlaylistRefreshCallback);
    paramHlsUrl.loadPlaylist();
  }

  public void release()
  {
    this.initialPlaylistLoader.release();
    Iterator localIterator = this.playlistBundles.values().iterator();
    while (localIterator.hasNext())
      ((MediaPlaylistBundle)localIterator.next()).release();
    this.playlistRefreshHandler.removeCallbacksAndMessages(null);
    this.playlistBundles.clear();
  }

  public void start()
  {
    ParsingLoadable localParsingLoadable = new ParsingLoadable(this.dataSourceFactory.createDataSource(), this.initialPlaylistUri, 4, this.playlistParser);
    this.initialPlaylistLoader.startLoading(localParsingLoadable, this, this.minRetryCount);
  }

  private final class MediaPlaylistBundle
    implements Runnable, Loader.Callback<ParsingLoadable<HlsPlaylist>>
  {
    private HlsPlaylistTracker.PlaylistRefreshCallback callback;
    private HlsMediaPlaylist latestPlaylistSnapshot;
    private final ParsingLoadable<HlsPlaylist> mediaPlaylistLoadable;
    private final Loader mediaPlaylistLoader;
    private final HlsMasterPlaylist.HlsUrl playlistUrl;

    public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl arg2)
    {
      this(localHlsUrl, null);
    }

    public MediaPlaylistBundle(HlsMasterPlaylist.HlsUrl paramHlsMediaPlaylist, HlsMediaPlaylist arg3)
    {
      this.playlistUrl = paramHlsMediaPlaylist;
      Object localObject;
      this.latestPlaylistSnapshot = localObject;
      this.mediaPlaylistLoader = new Loader("HlsPlaylistTracker:MediaPlaylist");
      this.mediaPlaylistLoadable = new ParsingLoadable(HlsPlaylistTracker.this.dataSourceFactory.createDataSource(), UriUtil.resolveToUri(HlsPlaylistTracker.this.masterPlaylist.baseUri, paramHlsMediaPlaylist.url), 4, HlsPlaylistTracker.this.playlistParser);
    }

    private void processLoadedPlaylist(HlsMediaPlaylist paramHlsMediaPlaylist)
    {
      boolean bool2 = true;
      boolean bool1 = true;
      HlsMediaPlaylist localHlsMediaPlaylist1 = this.latestPlaylistSnapshot;
      this.latestPlaylistSnapshot = HlsPlaylistTracker.this.adjustPlaylistTimestamps(localHlsMediaPlaylist1, paramHlsMediaPlaylist);
      if (localHlsMediaPlaylist1 != this.latestPlaylistSnapshot)
      {
        if (this.callback != null)
        {
          this.callback.onPlaylistChanged();
          this.callback = null;
        }
        paramHlsMediaPlaylist = HlsPlaylistTracker.this;
        HlsMasterPlaylist.HlsUrl localHlsUrl = this.playlistUrl;
        HlsMediaPlaylist localHlsMediaPlaylist2 = this.latestPlaylistSnapshot;
        if (localHlsMediaPlaylist1 == null)
          bool1 = paramHlsMediaPlaylist.onPlaylistUpdated(localHlsUrl, localHlsMediaPlaylist2, bool1);
      }
      while (true)
      {
        if (bool1)
          HlsPlaylistTracker.this.playlistRefreshHandler.postDelayed(this, 5000L);
        return;
        bool1 = false;
        break;
        bool1 = bool2;
        if (!paramHlsMediaPlaylist.hasEndTag)
          continue;
        bool1 = false;
      }
    }

    public void adjustTimestampsOfPlaylist(int paramInt, long paramLong)
    {
      ArrayList localArrayList = new ArrayList(this.latestPlaylistSnapshot.segments);
      int i = paramInt - this.latestPlaylistSnapshot.mediaSequence;
      if (i < 0);
      HlsMediaPlaylist.Segment localSegment;
      do
      {
        return;
        localSegment = (HlsMediaPlaylist.Segment)localArrayList.get(i);
      }
      while (Math.abs(localSegment.startTimeUs - paramLong) < 500000L);
      localArrayList.set(i, localSegment.copyWithStartTimeUs(paramLong));
      paramInt = i - 1;
      while (paramInt >= 0)
      {
        localSegment = (HlsMediaPlaylist.Segment)localArrayList.get(paramInt);
        localArrayList.set(paramInt, localSegment.copyWithStartTimeUs(((HlsMediaPlaylist.Segment)localArrayList.get(paramInt + 1)).startTimeUs - localSegment.durationUs));
        paramInt -= 1;
      }
      int j = localArrayList.size();
      paramInt = i + 1;
      while (paramInt < j)
      {
        localSegment = (HlsMediaPlaylist.Segment)localArrayList.get(paramInt);
        localArrayList.set(paramInt, localSegment.copyWithStartTimeUs(((HlsMediaPlaylist.Segment)localArrayList.get(paramInt - 1)).startTimeUs + localSegment.durationUs));
        paramInt += 1;
      }
      this.latestPlaylistSnapshot = this.latestPlaylistSnapshot.copyWithSegments(localArrayList);
    }

    public void loadPlaylist()
    {
      if (!this.mediaPlaylistLoader.isLoading())
        this.mediaPlaylistLoader.startLoading(this.mediaPlaylistLoadable, this, HlsPlaylistTracker.this.minRetryCount);
    }

    public void onLoadCanceled(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, boolean paramBoolean)
    {
      HlsPlaylistTracker.this.eventDispatcher.loadCanceled(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }

    public void onLoadCompleted(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2)
    {
      processLoadedPlaylist((HlsMediaPlaylist)paramParsingLoadable.getResult());
      HlsPlaylistTracker.this.eventDispatcher.loadCompleted(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded());
    }

    public int onLoadError(ParsingLoadable<HlsPlaylist> paramParsingLoadable, long paramLong1, long paramLong2, IOException paramIOException)
    {
      boolean bool = paramIOException instanceof ParserException;
      HlsPlaylistTracker.this.eventDispatcher.loadError(paramParsingLoadable.dataSpec, 4, paramLong1, paramLong2, paramParsingLoadable.bytesLoaded(), paramIOException, bool);
      if (this.callback != null)
        this.callback.onPlaylistLoadError(this.playlistUrl, paramIOException);
      if (bool)
        return 3;
      if (HlsPlaylistTracker.this.primaryHlsUrl == this.playlistUrl)
        return 0;
      return 2;
    }

    public void release()
    {
      this.mediaPlaylistLoader.release();
    }

    public void run()
    {
      loadPlaylist();
    }

    public void setCallback(HlsPlaylistTracker.PlaylistRefreshCallback paramPlaylistRefreshCallback)
    {
      this.callback = paramPlaylistRefreshCallback;
    }
  }

  public static abstract interface PlaylistRefreshCallback
  {
    public abstract void onPlaylistChanged();

    public abstract void onPlaylistLoadError(HlsMasterPlaylist.HlsUrl paramHlsUrl, IOException paramIOException);
  }

  public static abstract interface PrimaryPlaylistListener
  {
    public abstract void onPrimaryPlaylistRefreshed(HlsMediaPlaylist paramHlsMediaPlaylist);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistTracker
 * JD-Core Version:    0.6.0
 */