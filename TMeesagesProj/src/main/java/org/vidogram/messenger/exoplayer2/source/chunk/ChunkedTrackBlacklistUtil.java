package org.vidogram.messenger.exoplayer2.source.chunk;

import android.util.Log;
import org.vidogram.messenger.exoplayer2.trackselection.TrackSelection;
import org.vidogram.messenger.exoplayer2.upstream.HttpDataSource.InvalidResponseCodeException;

public final class ChunkedTrackBlacklistUtil
{
  public static final long DEFAULT_TRACK_BLACKLIST_MS = 60000L;
  private static final String TAG = "ChunkedTrackBlacklist";

  public static boolean maybeBlacklistTrack(TrackSelection paramTrackSelection, int paramInt, Exception paramException)
  {
    return maybeBlacklistTrack(paramTrackSelection, paramInt, paramException, 60000L);
  }

  public static boolean maybeBlacklistTrack(TrackSelection paramTrackSelection, int paramInt, Exception paramException, long paramLong)
  {
    if (paramTrackSelection.length() == 1);
    int i;
    do
    {
      do
        return false;
      while (!(paramException instanceof HttpDataSource.InvalidResponseCodeException));
      i = ((HttpDataSource.InvalidResponseCodeException)paramException).responseCode;
    }
    while ((i != 404) && (i != 410));
    boolean bool = paramTrackSelection.blacklist(paramInt, paramLong);
    if (bool)
    {
      Log.w("ChunkedTrackBlacklist", "Blacklisted: duration=" + paramLong + ", responseCode=" + i + ", format=" + paramTrackSelection.getFormat(paramInt));
      return bool;
    }
    Log.w("ChunkedTrackBlacklist", "Blacklisting failed (cannot blacklist last enabled track): responseCode=" + i + ", format=" + paramTrackSelection.getFormat(paramInt));
    return bool;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil
 * JD-Core Version:    0.6.0
 */