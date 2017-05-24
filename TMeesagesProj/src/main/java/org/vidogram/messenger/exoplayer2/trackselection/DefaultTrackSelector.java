package org.vidogram.messenger.exoplayer2.trackselection;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.vidogram.messenger.exoplayer2.Format;
import org.vidogram.messenger.exoplayer2.RendererCapabilities;
import org.vidogram.messenger.exoplayer2.source.TrackGroup;
import org.vidogram.messenger.exoplayer2.source.TrackGroupArray;
import org.vidogram.messenger.exoplayer2.util.Assertions;
import org.vidogram.messenger.exoplayer2.util.Util;

public class DefaultTrackSelector extends MappingTrackSelector
{
  private static final float FRACTION_TO_CONSIDER_FULLSCREEN = 0.98F;
  private static final int[] NO_TRACKS = new int[0];
  private final TrackSelection.Factory adaptiveVideoTrackSelectionFactory;
  private final AtomicReference<Parameters> params;

  public DefaultTrackSelector()
  {
    this(null);
  }

  public DefaultTrackSelector(TrackSelection.Factory paramFactory)
  {
    this.adaptiveVideoTrackSelectionFactory = paramFactory;
    this.params = new AtomicReference(new Parameters());
  }

  private static int comparePixelCounts(int paramInt1, int paramInt2)
  {
    int i = -1;
    if (paramInt1 == -1)
    {
      paramInt1 = i;
      if (paramInt2 == -1)
        paramInt1 = 0;
      return paramInt1;
    }
    if (paramInt2 == -1)
      return 1;
    return paramInt1 - paramInt2;
  }

  private static void filterAdaptiveTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, List<Integer> paramList)
  {
    int i = paramList.size() - 1;
    while (i >= 0)
    {
      int j = ((Integer)paramList.get(i)).intValue();
      if (!isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(j), paramString, paramArrayOfInt[j], paramInt1, paramInt2, paramInt3))
        paramList.remove(i);
      i -= 1;
    }
  }

  private static boolean formatHasLanguage(Format paramFormat, String paramString)
  {
    return (paramString != null) && (paramString.equals(Util.normalizeLanguageCode(paramFormat.language)));
  }

  private static int getAdaptiveTrackCountForMimeType(TrackGroup paramTrackGroup, int[] paramArrayOfInt, int paramInt1, String paramString, int paramInt2, int paramInt3, List<Integer> paramList)
  {
    int j = 0;
    int i = 0;
    if (j < paramList.size())
    {
      int k = ((Integer)paramList.get(j)).intValue();
      if (!isSupportedAdaptiveVideoTrack(paramTrackGroup.getFormat(k), paramString, paramArrayOfInt[k], paramInt1, paramInt2, paramInt3))
        break label75;
      i += 1;
    }
    label75: 
    while (true)
    {
      j += 1;
      break;
      return i;
    }
  }

  private static int[] getAdaptiveTracksForGroup(TrackGroup paramTrackGroup, int[] paramArrayOfInt, boolean paramBoolean1, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, boolean paramBoolean2)
  {
    if (paramTrackGroup.length < 2)
      return NO_TRACKS;
    List localList = getViewportFilteredTrackIndices(paramTrackGroup, paramInt4, paramInt5, paramBoolean2);
    if (localList.size() < 2)
      return NO_TRACKS;
    Object localObject = null;
    if (!paramBoolean1)
    {
      HashSet localHashSet = new HashSet();
      paramInt4 = 0;
      paramInt5 = 0;
      while (paramInt5 < localList.size())
      {
        String str = paramTrackGroup.getFormat(((Integer)localList.get(paramInt5)).intValue()).sampleMimeType;
        if (localHashSet.contains(str))
          break label190;
        localHashSet.add(str);
        int i = getAdaptiveTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, str, paramInt2, paramInt3, localList);
        if (i <= paramInt4)
          break label190;
        localObject = str;
        paramInt4 = i;
        paramInt5 += 1;
      }
    }
    while (true)
    {
      filterAdaptiveTrackCountForMimeType(paramTrackGroup, paramArrayOfInt, paramInt1, localObject, paramInt2, paramInt3, localList);
      if (localList.size() < 2)
        return NO_TRACKS;
      return Util.toArray(localList);
      label190: break;
      localObject = null;
    }
  }

  private static Point getMaxVideoSizeInViewport(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int m = 1;
    int k = paramInt1;
    int j = paramInt2;
    int i;
    if (paramBoolean)
    {
      if (paramInt3 <= paramInt4)
        break label77;
      i = 1;
      if (paramInt1 <= paramInt2)
        break label83;
    }
    while (true)
    {
      k = paramInt1;
      j = paramInt2;
      if (i != m)
      {
        j = paramInt1;
        k = paramInt2;
      }
      if (paramInt3 * j < paramInt4 * k)
        break label89;
      return new Point(k, Util.ceilDivide(k * paramInt4, paramInt3));
      label77: i = 0;
      break;
      label83: m = 0;
    }
    label89: return new Point(Util.ceilDivide(j * paramInt3, paramInt4), j);
  }

  private static List<Integer> getViewportFilteredTrackIndices(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int j = 0;
    ArrayList localArrayList = new ArrayList(paramTrackGroup.length);
    int i = 0;
    while (i < paramTrackGroup.length)
    {
      localArrayList.add(Integer.valueOf(i));
      i += 1;
    }
    if ((paramInt1 == 2147483647) || (paramInt2 == 2147483647))
      return localArrayList;
    i = 2147483647;
    if (j < paramTrackGroup.length)
    {
      Format localFormat = paramTrackGroup.getFormat(j);
      if ((localFormat.width <= 0) || (localFormat.height <= 0))
        break label254;
      Point localPoint = getMaxVideoSizeInViewport(paramBoolean, paramInt1, paramInt2, localFormat.width, localFormat.height);
      int k = localFormat.width * localFormat.height;
      if ((localFormat.width < (int)(localPoint.x * 0.98F)) || (localFormat.height < (int)(localPoint.y * 0.98F)) || (k >= i))
        break label254;
      i = k;
    }
    label254: 
    while (true)
    {
      j += 1;
      break;
      if (i != 2147483647)
      {
        paramInt1 = localArrayList.size() - 1;
        while (paramInt1 >= 0)
        {
          paramInt2 = paramTrackGroup.getFormat(((Integer)localArrayList.get(paramInt1)).intValue()).getPixelCount();
          if ((paramInt2 == -1) || (paramInt2 > i))
            localArrayList.remove(paramInt1);
          paramInt1 -= 1;
        }
      }
      return localArrayList;
    }
  }

  private static boolean isSupported(int paramInt)
  {
    return (paramInt & 0x3) == 3;
  }

  private static boolean isSupportedAdaptiveVideoTrack(Format paramFormat, String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    return (isSupported(paramInt1)) && ((paramInt1 & paramInt2) != 0) && ((paramString == null) || (Util.areEqual(paramFormat.sampleMimeType, paramString))) && ((paramFormat.width == -1) || (paramFormat.width <= paramInt3)) && ((paramFormat.height == -1) || (paramFormat.height <= paramInt4));
  }

  private static TrackSelection selectAdaptiveVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, boolean paramBoolean3, TrackSelection.Factory paramFactory)
  {
    int i;
    label29: int j;
    if (paramBoolean1)
    {
      i = 12;
      if ((!paramBoolean2) || ((paramRendererCapabilities.supportsMixedMimeTypeAdaptation() & i) == 0))
        break label95;
      paramBoolean1 = true;
      j = 0;
    }
    while (true)
    {
      if (j >= paramTrackGroupArray.length)
        break label110;
      paramRendererCapabilities = paramTrackGroupArray.get(j);
      int[] arrayOfInt = getAdaptiveTracksForGroup(paramRendererCapabilities, paramArrayOfInt[j], paramBoolean1, i, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean3);
      if (arrayOfInt.length > 0)
      {
        return paramFactory.createTrackSelection(paramRendererCapabilities, arrayOfInt);
        i = 8;
        break;
        label95: paramBoolean1 = false;
        break label29;
      }
      j += 1;
    }
    label110: return null;
  }

  private static TrackSelection selectFixedVideoTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2)
  {
    Object localObject = null;
    int m = 0;
    int k = -1;
    int n = 0;
    int i2 = 0;
    int j;
    label54: int i1;
    label138: int i3;
    int i;
    if (i2 < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(i2);
      List localList = getViewportFilteredTrackIndices(localTrackGroup, paramInt3, paramInt4, paramBoolean1);
      int[] arrayOfInt = paramArrayOfInt[i2];
      j = 0;
      if (j < localTrackGroup.length)
      {
        if (!isSupported(arrayOfInt[j]))
          break label283;
        Format localFormat = localTrackGroup.getFormat(j);
        if ((localList.contains(Integer.valueOf(j))) && ((localFormat.width == -1) || (localFormat.width <= paramInt1)) && ((localFormat.height == -1) || (localFormat.height <= paramInt2)))
        {
          i1 = 1;
          i3 = localFormat.getPixelCount();
          if (n == 0)
            break label218;
          if ((i1 == 0) || (comparePixelCounts(i3, k) <= 0))
            break label212;
          i = 1;
          label168: if (i == 0)
            break label283;
          i = i3;
          localObject = localTrackGroup;
        }
      }
    }
    for (k = j; ; k = m)
    {
      j += 1;
      m = k;
      n = i1;
      k = i;
      break label54;
      i1 = 0;
      break label138;
      label212: i = 0;
      break label168;
      label218: if ((i1 != 0) || ((paramBoolean2) && ((localObject == null) || (comparePixelCounts(i3, k) < 0))))
      {
        i = 1;
        break label168;
      }
      i = 0;
      break label168;
      i2 += 1;
      break;
      if (localObject == null)
        return null;
      return new FixedTrackSelection(localObject, m);
      label283: i = k;
      i1 = n;
    }
  }

  public Parameters getParameters()
  {
    return (Parameters)this.params.get();
  }

  protected TrackSelection selectAudioTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, String paramString)
  {
    int n = 0;
    int m = 0;
    int k = 0;
    Object localObject = null;
    int j;
    label38: int i;
    if (n < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(n);
      int[] arrayOfInt = paramArrayOfInt[n];
      j = 0;
      if (j < localTrackGroup.length)
      {
        if (!isSupported(arrayOfInt[j]))
          break label183;
        Format localFormat = localTrackGroup.getFormat(j);
        if ((localFormat.selectionFlags & 0x1) != 0)
        {
          i = 1;
          label81: if (!formatHasLanguage(localFormat, paramString))
            break label138;
          if (i == 0)
            break label132;
          i = 4;
          label98: if (i <= m)
            break label183;
          k = j;
          localObject = localTrackGroup;
        }
      }
    }
    while (true)
    {
      j += 1;
      m = i;
      break label38;
      i = 0;
      break label81;
      label132: i = 3;
      break label98;
      label138: if (i != 0)
      {
        i = 2;
        break label98;
      }
      i = 1;
      break label98;
      n += 1;
      break;
      if (localObject == null)
        return null;
      return new FixedTrackSelection(localObject, k);
      label183: i = m;
    }
  }

  protected TrackSelection selectOtherTrack(int paramInt, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt)
  {
    int m = 0;
    int j = 0;
    int i = 0;
    Object localObject = null;
    label37: int k;
    if (m < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(m);
      int[] arrayOfInt = paramArrayOfInt[m];
      paramInt = 0;
      if (paramInt < localTrackGroup.length)
      {
        if (!isSupported(arrayOfInt[paramInt]))
          break label146;
        if ((localTrackGroup.getFormat(paramInt).selectionFlags & 0x1) != 0)
        {
          k = 1;
          label73: if (k == 0)
            break label112;
          k = 2;
          label81: if (k <= j)
            break label146;
          i = paramInt;
          localObject = localTrackGroup;
        }
      }
    }
    while (true)
    {
      paramInt += 1;
      j = k;
      break label37;
      k = 0;
      break label73;
      label112: k = 1;
      break label81;
      m += 1;
      break;
      if (localObject == null)
        return null;
      return new FixedTrackSelection(localObject, i);
      label146: k = j;
    }
  }

  protected TrackSelection selectTextTrack(TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, String paramString1, String paramString2)
  {
    Object localObject = null;
    int k = 0;
    int m = 0;
    int n = 0;
    int j;
    label38: Format localFormat;
    int i;
    label81: int i1;
    if (n < paramTrackGroupArray.length)
    {
      TrackGroup localTrackGroup = paramTrackGroupArray.get(n);
      int[] arrayOfInt = paramArrayOfInt[n];
      j = 0;
      if (j < localTrackGroup.length)
      {
        if (!isSupported(arrayOfInt[j]))
          break label241;
        localFormat = localTrackGroup.getFormat(j);
        if ((localFormat.selectionFlags & 0x1) != 0)
        {
          i = 1;
          if ((localFormat.selectionFlags & 0x2) == 0)
            break label146;
          i1 = 1;
          label94: if (!formatHasLanguage(localFormat, paramString1))
            break label169;
          if (i == 0)
            break label152;
          i = 6;
          label112: if (i <= m)
            break label241;
          k = j;
          localObject = localTrackGroup;
        }
      }
    }
    while (true)
    {
      j += 1;
      m = i;
      break label38;
      i = 0;
      break label81;
      label146: i1 = 0;
      break label94;
      label152: if (i1 == 0)
      {
        i = 5;
        break label112;
      }
      i = 4;
      break label112;
      label169: if (i != 0)
      {
        i = 3;
        break label112;
      }
      if (i1 != 0)
      {
        if (formatHasLanguage(localFormat, paramString2))
        {
          i = 2;
          break label112;
        }
        i = 1;
        break label112;
      }
      i = 0;
      break label112;
      n += 1;
      break;
      if (localObject == null)
        return null;
      return new FixedTrackSelection(localObject, k);
      label241: i = m;
    }
  }

  protected TrackSelection[] selectTracks(RendererCapabilities[] paramArrayOfRendererCapabilities, TrackGroupArray[] paramArrayOfTrackGroupArray, int[][][] paramArrayOfInt)
  {
    TrackSelection[] arrayOfTrackSelection = new TrackSelection[paramArrayOfRendererCapabilities.length];
    Parameters localParameters = (Parameters)this.params.get();
    int i = 0;
    if (i < paramArrayOfRendererCapabilities.length)
    {
      switch (paramArrayOfRendererCapabilities[i].getTrackType())
      {
      default:
        arrayOfTrackSelection[i] = selectOtherTrack(paramArrayOfRendererCapabilities[i].getTrackType(), paramArrayOfTrackGroupArray[i], paramArrayOfInt[i]);
      case 2:
      case 1:
      case 3:
      }
      while (true)
      {
        i += 1;
        break;
        arrayOfTrackSelection[i] = selectVideoTrack(paramArrayOfRendererCapabilities[i], paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.maxVideoWidth, localParameters.maxVideoHeight, localParameters.allowNonSeamlessAdaptiveness, localParameters.allowMixedMimeAdaptiveness, localParameters.viewportWidth, localParameters.viewportHeight, localParameters.orientationMayChange, this.adaptiveVideoTrackSelectionFactory, localParameters.exceedVideoConstraintsIfNecessary);
        continue;
        arrayOfTrackSelection[i] = selectAudioTrack(paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.preferredAudioLanguage);
        continue;
        arrayOfTrackSelection[i] = selectTextTrack(paramArrayOfTrackGroupArray[i], paramArrayOfInt[i], localParameters.preferredTextLanguage, localParameters.preferredAudioLanguage);
      }
    }
    return arrayOfTrackSelection;
  }

  protected TrackSelection selectVideoTrack(RendererCapabilities paramRendererCapabilities, TrackGroupArray paramTrackGroupArray, int[][] paramArrayOfInt, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, int paramInt3, int paramInt4, boolean paramBoolean3, TrackSelection.Factory paramFactory, boolean paramBoolean4)
  {
    TrackSelection localTrackSelection = null;
    if (paramFactory != null)
      localTrackSelection = selectAdaptiveVideoTrack(paramRendererCapabilities, paramTrackGroupArray, paramArrayOfInt, paramInt1, paramInt2, paramBoolean1, paramBoolean2, paramInt3, paramInt4, paramBoolean3, paramFactory);
    paramRendererCapabilities = localTrackSelection;
    if (localTrackSelection == null)
      paramRendererCapabilities = selectFixedVideoTrack(paramTrackGroupArray, paramArrayOfInt, paramInt1, paramInt2, paramInt3, paramInt4, paramBoolean3, paramBoolean4);
    return paramRendererCapabilities;
  }

  public void setParameters(Parameters paramParameters)
  {
    if (!((Parameters)this.params.get()).equals(paramParameters))
    {
      this.params.set(Assertions.checkNotNull(paramParameters));
      invalidate();
    }
  }

  public static final class Parameters
  {
    public final boolean allowMixedMimeAdaptiveness;
    public final boolean allowNonSeamlessAdaptiveness;
    public final boolean exceedVideoConstraintsIfNecessary;
    public final int maxVideoHeight;
    public final int maxVideoWidth;
    public final boolean orientationMayChange;
    public final String preferredAudioLanguage;
    public final String preferredTextLanguage;
    public final int viewportHeight;
    public final int viewportWidth;

    public Parameters()
    {
      this(null, null, false, true, 2147483647, 2147483647, true, 2147483647, 2147483647, true);
    }

    public Parameters(String paramString1, String paramString2, boolean paramBoolean1, boolean paramBoolean2, int paramInt1, int paramInt2, boolean paramBoolean3, int paramInt3, int paramInt4, boolean paramBoolean4)
    {
      this.preferredAudioLanguage = paramString1;
      this.preferredTextLanguage = paramString2;
      this.allowMixedMimeAdaptiveness = paramBoolean1;
      this.allowNonSeamlessAdaptiveness = paramBoolean2;
      this.maxVideoWidth = paramInt1;
      this.maxVideoHeight = paramInt2;
      this.exceedVideoConstraintsIfNecessary = paramBoolean3;
      this.viewportWidth = paramInt3;
      this.viewportHeight = paramInt4;
      this.orientationMayChange = paramBoolean4;
    }

    public boolean equals(Object paramObject)
    {
      if (this == paramObject);
      do
      {
        return true;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
          return false;
        paramObject = (Parameters)paramObject;
      }
      while ((this.allowMixedMimeAdaptiveness == paramObject.allowMixedMimeAdaptiveness) && (this.allowNonSeamlessAdaptiveness == paramObject.allowNonSeamlessAdaptiveness) && (this.maxVideoWidth == paramObject.maxVideoWidth) && (this.maxVideoHeight == paramObject.maxVideoHeight) && (this.exceedVideoConstraintsIfNecessary == paramObject.exceedVideoConstraintsIfNecessary) && (this.orientationMayChange == paramObject.orientationMayChange) && (this.viewportWidth == paramObject.viewportWidth) && (this.viewportHeight == paramObject.viewportHeight) && (TextUtils.equals(this.preferredAudioLanguage, paramObject.preferredAudioLanguage)) && (TextUtils.equals(this.preferredTextLanguage, paramObject.preferredTextLanguage)));
      return false;
    }

    public int hashCode()
    {
      int m = 1;
      int n = this.preferredAudioLanguage.hashCode();
      int i1 = this.preferredTextLanguage.hashCode();
      int i;
      int j;
      label39: int i2;
      int i3;
      int k;
      if (this.allowMixedMimeAdaptiveness)
      {
        i = 1;
        if (!this.allowNonSeamlessAdaptiveness)
          break label130;
        j = 1;
        i2 = this.maxVideoWidth;
        i3 = this.maxVideoHeight;
        if (!this.exceedVideoConstraintsIfNecessary)
          break label135;
        k = 1;
        label60: if (!this.orientationMayChange)
          break label140;
      }
      while (true)
      {
        return (((k + (((j + (i + (n * 31 + i1) * 31) * 31) * 31 + i2) * 31 + i3) * 31) * 31 + m) * 31 + this.viewportWidth) * 31 + this.viewportHeight;
        i = 0;
        break;
        label130: j = 0;
        break label39;
        label135: k = 0;
        break label60;
        label140: m = 0;
      }
    }

    public Parameters withAllowMixedMimeAdaptiveness(boolean paramBoolean)
    {
      if (paramBoolean == this.allowMixedMimeAdaptiveness)
        return this;
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, paramBoolean, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withAllowNonSeamlessAdaptiveness(boolean paramBoolean)
    {
      if (paramBoolean == this.allowNonSeamlessAdaptiveness)
        return this;
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, paramBoolean, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withExceedVideoConstraintsIfNecessary(boolean paramBoolean)
    {
      if (paramBoolean == this.exceedVideoConstraintsIfNecessary)
        return this;
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, paramBoolean, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withMaxVideoSize(int paramInt1, int paramInt2)
    {
      if ((paramInt1 == this.maxVideoWidth) && (paramInt2 == this.maxVideoHeight))
        return this;
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, paramInt1, paramInt2, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withMaxVideoSizeSd()
    {
      return withMaxVideoSize(1279, 719);
    }

    public Parameters withPreferredAudioLanguage(String paramString)
    {
      paramString = Util.normalizeLanguageCode(paramString);
      if (TextUtils.equals(paramString, this.preferredAudioLanguage))
        return this;
      return new Parameters(paramString, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withPreferredTextLanguage(String paramString)
    {
      paramString = Util.normalizeLanguageCode(paramString);
      if (TextUtils.equals(paramString, this.preferredTextLanguage))
        return this;
      return new Parameters(this.preferredAudioLanguage, paramString, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, this.viewportWidth, this.viewportHeight, this.orientationMayChange);
    }

    public Parameters withViewportSize(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      if ((paramInt1 == this.viewportWidth) && (paramInt2 == this.viewportHeight) && (paramBoolean == this.orientationMayChange))
        return this;
      return new Parameters(this.preferredAudioLanguage, this.preferredTextLanguage, this.allowMixedMimeAdaptiveness, this.allowNonSeamlessAdaptiveness, this.maxVideoWidth, this.maxVideoHeight, this.exceedVideoConstraintsIfNecessary, paramInt1, paramInt2, paramBoolean);
    }

    public Parameters withViewportSizeFromContext(Context paramContext, boolean paramBoolean)
    {
      paramContext = Util.getPhysicalDisplaySize(paramContext);
      return withViewportSize(paramContext.x, paramContext.y, paramBoolean);
    }

    public Parameters withoutVideoSizeConstraints()
    {
      return withMaxVideoSize(2147483647, 2147483647);
    }

    public Parameters withoutViewportSizeConstraints()
    {
      return withViewportSize(2147483647, 2147483647, true);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.exoplayer2.trackselection.DefaultTrackSelector
 * JD-Core Version:    0.6.0
 */