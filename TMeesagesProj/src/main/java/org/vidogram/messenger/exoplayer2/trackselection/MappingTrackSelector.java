package org.vidogram.messenger.exoplayer2.trackselection;

import android.util.Pair;
import android.util.Pair<Lorg.vidogram.messenger.exoplayer2.trackselection.TrackSelectionArray;Ljava.lang.Object;>;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.RendererCapabilities;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.util.Util;

public abstract class MappingTrackSelector extends TrackSelector
{
  private MappedTrackInfo currentMappedTrackInfo;
  private final SparseBooleanArray rendererDisabledFlags = new SparseBooleanArray();
  private final SparseArray<Map<TrackGroupArray, SelectionOverride>> selectionOverrides = new SparseArray();

  private static int findRenderer(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroup paramTrackGroup)
  {
    int k = paramArrayOfRendererCapabilities.length;
    int j = 0;
    int i = 0;
    int m;
    label22: int n;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      RendererCapabilities localRendererCapabilities = paramArrayOfRendererCapabilities[i];
      m = 0;
      if (m < paramTrackGroup.length)
      {
        n = localRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(m));
        if (n <= j)
          break label99;
        if (n == 3)
          return i;
        k = n;
      }
    }
    for (j = i; ; j = n)
    {
      n = m + 1;
      m = j;
      j = k;
      k = m;
      m = n;
      break label22;
      i += 1;
      break;
      return k;
      label99: n = k;
      k = j;
    }
  }

  private static int[] getFormatSupport(RendererCapabilities paramRendererCapabilities, TrackGroup paramTrackGroup)
  {
    int[] arrayOfInt = new int[paramTrackGroup.length];
    int i = 0;
    while (i < paramTrackGroup.length)
    {
      arrayOfInt[i] = paramRendererCapabilities.supportsFormat(paramTrackGroup.getFormat(i));
      i += 1;
    }
    return arrayOfInt;
  }

  private static int[] getMixedMimeTypeAdaptationSupport(RendererCapabilities[] paramArrayOfRendererCapabilities)
  {
    int[] arrayOfInt = new int[paramArrayOfRendererCapabilities.length];
    int i = 0;
    while (i < arrayOfInt.length)
    {
      arrayOfInt[i] = paramArrayOfRendererCapabilities[i].supportsMixedMimeTypeAdaptation();
      i += 1;
    }
    return arrayOfInt;
  }

  public final void clearSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (!localMap.containsKey(paramTrackGroupArray)))
      return;
    localMap.remove(paramTrackGroupArray);
    if (localMap.isEmpty())
      this.selectionOverrides.remove(paramInt);
    invalidate();
  }

  public final void clearSelectionOverrides()
  {
    if (this.selectionOverrides.size() == 0)
      return;
    this.selectionOverrides.clear();
    invalidate();
  }

  public final void clearSelectionOverrides(int paramInt)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if ((localMap == null) || (localMap.isEmpty()))
      return;
    this.selectionOverrides.remove(paramInt);
    invalidate();
  }

  public final MappedTrackInfo getCurrentMappedTrackInfo()
  {
    return this.currentMappedTrackInfo;
  }

  public final boolean getRendererDisabled(int paramInt)
  {
    return this.rendererDisabledFlags.get(paramInt);
  }

  public final SelectionOverride getSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    if (localMap != null)
      return (SelectionOverride)localMap.get(paramTrackGroupArray);
    return null;
  }

  public final boolean hasSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    return (localMap != null) && (localMap.containsKey(paramTrackGroupArray));
  }

  public final void onSelectionActivated(Object paramObject)
  {
    this.currentMappedTrackInfo = ((MappedTrackInfo)paramObject);
  }

  public final Pair<TrackSelectionArray, Object> selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray paramTrackGroupArray)
  {
    int j = 0;
    Object localObject3 = new int[paramArrayOfRendererCapabilities.length + 1];
    Object localObject4 = new TrackGroup[paramArrayOfRendererCapabilities.length + 1][];
    int[][][] arrayOfInt = new int[paramArrayOfRendererCapabilities.length + 1][][];
    int i = 0;
    while (i < localObject4.length)
    {
      localObject4[i] = new TrackGroup[paramTrackGroupArray.length];
      arrayOfInt[i] = new int[paramTrackGroupArray.length][];
      i += 1;
    }
    int[] arrayOfInt1 = getMixedMimeTypeAdaptationSupport(paramArrayOfRendererCapabilities);
    i = 0;
    int k;
    if (i < paramTrackGroupArray.length)
    {
      localObject2 = paramTrackGroupArray.get(i);
      k = findRenderer(paramArrayOfRendererCapabilities, (TrackGroup)localObject2);
      if (k == paramArrayOfRendererCapabilities.length);
      for (localObject1 = new int[((TrackGroup)localObject2).length]; ; localObject1 = getFormatSupport(paramArrayOfRendererCapabilities[k], (TrackGroup)localObject2))
      {
        int m = localObject3[k];
        localObject4[k][m] = localObject2;
        arrayOfInt[k][m] = localObject1;
        localObject3[k] += 1;
        i += 1;
        break;
      }
    }
    Object localObject1 = new TrackGroupArray[paramArrayOfRendererCapabilities.length];
    Object localObject2 = new int[paramArrayOfRendererCapabilities.length];
    i = 0;
    while (i < paramArrayOfRendererCapabilities.length)
    {
      k = localObject3[i];
      localObject1[i] = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject4[i], k));
      arrayOfInt[i] = ((int[][])Arrays.copyOf(arrayOfInt[i], k));
      localObject2[i] = paramArrayOfRendererCapabilities[i].getTrackType();
      i += 1;
    }
    i = localObject3[paramArrayOfRendererCapabilities.length];
    localObject4 = new TrackGroupArray((TrackGroup[])Arrays.copyOf(localObject4[paramArrayOfRendererCapabilities.length], i));
    localObject3 = selectTracks(paramArrayOfRendererCapabilities, localObject1, arrayOfInt);
    i = j;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      if (this.rendererDisabledFlags.get(i))
        localObject3[i] = null;
      label383: 
      while (true)
      {
        i += 1;
        break;
        TrackGroupArray localTrackGroupArray = localObject1[i];
        paramTrackGroupArray = (Map)this.selectionOverrides.get(i);
        if (paramTrackGroupArray == null);
        for (paramTrackGroupArray = null; ; paramTrackGroupArray = (SelectionOverride)paramTrackGroupArray.get(localTrackGroupArray))
        {
          if (paramTrackGroupArray == null)
            break label383;
          localObject3[i] = paramTrackGroupArray.createTrackSelection(localTrackGroupArray);
          break;
        }
      }
    }
    paramArrayOfRendererCapabilities = new MappedTrackInfo(localObject2, localObject1, arrayOfInt1, arrayOfInt, (TrackGroupArray)localObject4);
    return (Pair<TrackSelectionArray, Object>)(Pair<TrackSelectionArray, Object>)(Pair<TrackSelectionArray, Object>)(Pair<TrackSelectionArray, Object>)Pair.create(new TrackSelectionArray(localObject3), paramArrayOfRendererCapabilities);
  }

  protected abstract TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt);

  public final void setRendererDisabled(int paramInt, boolean paramBoolean)
  {
    if (this.rendererDisabledFlags.get(paramInt) == paramBoolean)
      return;
    this.rendererDisabledFlags.put(paramInt, paramBoolean);
    invalidate();
  }

  public final void setSelectionOverride(int paramInt, TrackGroupArray paramTrackGroupArray, SelectionOverride paramSelectionOverride)
  {
    Map localMap = (Map)this.selectionOverrides.get(paramInt);
    Object localObject = localMap;
    if (localMap == null)
    {
      localObject = new HashMap();
      this.selectionOverrides.put(paramInt, localObject);
    }
    if ((((Map)localObject).containsKey(paramTrackGroupArray)) && (Util.areEqual(((Map)localObject).get(paramTrackGroupArray), paramSelectionOverride)))
      return;
    ((Map)localObject).put(paramTrackGroupArray, paramSelectionOverride);
    invalidate();
  }

  public static final class MappedTrackInfo
  {
    public static final int RENDERER_SUPPORT_NO_TRACKS = 0;
    public static final int RENDERER_SUPPORT_PLAYABLE_TRACKS = 2;
    public static final int RENDERER_SUPPORT_UNPLAYABLE_TRACKS = 1;
    private final int[][][] formatSupport;
    public final int length;
    private final int[] mixedMimeTypeAdaptiveSupport;
    private final int[] rendererTrackTypes;
    private final TrackGroupArray[] trackGroups;
    private final TrackGroupArray unassociatedTrackGroups;

    MappedTrackInfo(int[] paramArrayOfInt1, TrackGroupArray[] paramArrayOfTrackGroupArray, int[] paramArrayOfInt2, int[][][] paramArrayOfInt, TrackGroupArray paramTrackGroupArray)
    {
      this.rendererTrackTypes = paramArrayOfInt1;
      this.trackGroups = paramArrayOfTrackGroupArray;
      this.formatSupport = paramArrayOfInt;
      this.mixedMimeTypeAdaptiveSupport = paramArrayOfInt2;
      this.unassociatedTrackGroups = paramTrackGroupArray;
      this.length = paramArrayOfTrackGroupArray.length;
    }

    public int getAdaptiveSupport(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int j = 0;
      int m = this.trackGroups[paramInt1].get(paramInt2).length;
      int[] arrayOfInt = new int[m];
      int i = 0;
      while (i < m)
      {
        int n = getTrackFormatSupport(paramInt1, paramInt2, i);
        int k;
        if (n != 3)
        {
          k = j;
          if (paramBoolean)
          {
            k = j;
            if (n != 2);
          }
        }
        else
        {
          arrayOfInt[j] = i;
          k = j + 1;
        }
        i += 1;
        j = k;
      }
      return getAdaptiveSupport(paramInt1, paramInt2, Arrays.copyOf(arrayOfInt, j));
    }

    public int getAdaptiveSupport(int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      Object localObject = null;
      int m = 0;
      int j = 0;
      int i = 8;
      int k = 0;
      while (m < paramArrayOfInt.length)
      {
        int n = paramArrayOfInt[m];
        String str = this.trackGroups[paramInt1].get(paramInt2).getFormat(n).sampleMimeType;
        if (k == 0)
        {
          localObject = str;
          i = Math.min(i, this.formatSupport[paramInt1][paramInt2][m] & 0xC);
          m += 1;
          k += 1;
          continue;
        }
        if (!Util.areEqual(localObject, str));
        for (n = 1; ; n = 0)
        {
          j = n | j;
          break;
        }
      }
      paramInt2 = i;
      if (j != 0)
        paramInt2 = Math.min(i, this.mixedMimeTypeAdaptiveSupport[paramInt1]);
      return paramInt2;
    }

    public int getRendererSupport(int paramInt)
    {
      int k = 1;
      int[][] arrayOfInt = this.formatSupport[paramInt];
      paramInt = 0;
      int i = 0;
      int j;
      if (paramInt < arrayOfInt.length)
      {
        j = 0;
        label24: if (j < arrayOfInt[paramInt].length)
          if ((arrayOfInt[paramInt][j] & 0x3) == 3)
            paramInt = 2;
      }
      do
      {
        return paramInt;
        j += 1;
        i = 1;
        break label24;
        paramInt += 1;
        break;
        paramInt = k;
      }
      while (i != 0);
      return 0;
    }

    public int getTrackFormatSupport(int paramInt1, int paramInt2, int paramInt3)
    {
      return this.formatSupport[paramInt1][paramInt2][paramInt3] & 0x3;
    }

    public TrackGroupArray getTrackGroups(int paramInt)
    {
      return this.trackGroups[paramInt];
    }

    public TrackGroupArray getUnassociatedTrackGroups()
    {
      return this.unassociatedTrackGroups;
    }

    public boolean hasOnlyUnplayableTracks(int paramInt)
    {
      int m = 0;
      int i = 0;
      int k;
      for (int j = 0; i < this.length; j = k)
      {
        k = j;
        if (this.rendererTrackTypes[i] == paramInt)
          k = Math.max(j, getRendererSupport(i));
        i += 1;
      }
      if (j == 1)
        m = 1;
      return m;
    }
  }

  public static final class SelectionOverride
  {
    public final TrackSelection.Factory factory;
    public final int groupIndex;
    public final int length;
    public final int[] tracks;

    public SelectionOverride(TrackSelection.Factory paramFactory, int paramInt, int[] paramArrayOfInt)
    {
      this.factory = paramFactory;
      this.groupIndex = paramInt;
      this.tracks = paramArrayOfInt;
      this.length = paramArrayOfInt.length;
    }

    public boolean containsTrack(int paramInt)
    {
      int m = 0;
      int[] arrayOfInt = this.tracks;
      int j = arrayOfInt.length;
      int i = 0;
      while (true)
      {
        int k = m;
        if (i < j)
        {
          if (arrayOfInt[i] == paramInt)
            k = 1;
        }
        else
          return k;
        i += 1;
      }
    }

    public TrackSelection createTrackSelection(TrackGroupArray paramTrackGroupArray)
    {
      return this.factory.createTrackSelection(paramTrackGroupArray.get(this.groupIndex), this.tracks);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.MappingTrackSelector
 * JD-Core Version:    0.6.0
 */