package org.vidogram.messenger;

import android.graphics.drawable.BitmapDrawable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public class LruCache
{
  private final LinkedHashMap<String, BitmapDrawable> map;
  private final LinkedHashMap<String, ArrayList<String>> mapFilters;
  private int maxSize;
  private int size;

  public LruCache(int paramInt)
  {
    if (paramInt <= 0)
      throw new IllegalArgumentException("maxSize <= 0");
    this.maxSize = paramInt;
    this.map = new LinkedHashMap(0, 0.75F, true);
    this.mapFilters = new LinkedHashMap();
  }

  private int safeSizeOf(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    int i = sizeOf(paramString, paramBitmapDrawable);
    if (i < 0)
      throw new IllegalStateException("Negative size: " + paramString + "=" + paramBitmapDrawable);
    return i;
  }

  private void trimToSize(int paramInt, String paramString)
  {
    monitorenter;
    try
    {
      Iterator localIterator = this.map.entrySet().iterator();
      while (true)
      {
        if ((!localIterator.hasNext()) || (this.size <= paramInt) || (this.map.isEmpty()))
          return;
        Object localObject = (Map.Entry)localIterator.next();
        String str = (String)((Map.Entry)localObject).getKey();
        if ((paramString != null) && (paramString.equals(str)))
          continue;
        localObject = (BitmapDrawable)((Map.Entry)localObject).getValue();
        this.size -= safeSizeOf(str, (BitmapDrawable)localObject);
        localIterator.remove();
        String[] arrayOfString = str.split("@");
        if (arrayOfString.length > 1)
        {
          ArrayList localArrayList = (ArrayList)this.mapFilters.get(arrayOfString[0]);
          if (localArrayList != null)
          {
            localArrayList.remove(arrayOfString[1]);
            if (localArrayList.isEmpty())
              this.mapFilters.remove(arrayOfString[0]);
          }
        }
        entryRemoved(true, str, (BitmapDrawable)localObject, null);
      }
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public boolean contains(String paramString)
  {
    return this.map.containsKey(paramString);
  }

  protected void entryRemoved(boolean paramBoolean, String paramString, BitmapDrawable paramBitmapDrawable1, BitmapDrawable paramBitmapDrawable2)
  {
  }

  public final void evictAll()
  {
    trimToSize(-1, null);
  }

  public final BitmapDrawable get(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("key == null");
    monitorenter;
    try
    {
      paramString = (BitmapDrawable)this.map.get(paramString);
      if (paramString != null)
        return paramString;
      return null;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public ArrayList<String> getFilterKeys(String paramString)
  {
    paramString = (ArrayList)this.mapFilters.get(paramString);
    if (paramString != null)
      return new ArrayList(paramString);
    return null;
  }

  public final int maxSize()
  {
    monitorenter;
    try
    {
      int i = this.maxSize;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  public BitmapDrawable put(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    if ((paramString == null) || (paramBitmapDrawable == null))
      throw new NullPointerException("key == null || value == null");
    monitorenter;
    try
    {
      this.size += safeSizeOf(paramString, paramBitmapDrawable);
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)this.map.put(paramString, paramBitmapDrawable);
      if (localBitmapDrawable != null)
        this.size -= safeSizeOf(paramString, localBitmapDrawable);
      monitorexit;
      String[] arrayOfString = paramString.split("@");
      if (arrayOfString.length > 1)
      {
        ArrayList localArrayList2 = (ArrayList)this.mapFilters.get(arrayOfString[0]);
        ArrayList localArrayList1 = localArrayList2;
        if (localArrayList2 == null)
        {
          localArrayList1 = new ArrayList();
          this.mapFilters.put(arrayOfString[0], localArrayList1);
        }
        if (!localArrayList1.contains(arrayOfString[1]))
          localArrayList1.add(arrayOfString[1]);
      }
      if (localBitmapDrawable != null)
        entryRemoved(false, paramString, localBitmapDrawable, paramBitmapDrawable);
      trimToSize(this.maxSize, paramString);
      return localBitmapDrawable;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public final BitmapDrawable remove(String paramString)
  {
    if (paramString == null)
      throw new NullPointerException("key == null");
    monitorenter;
    try
    {
      BitmapDrawable localBitmapDrawable = (BitmapDrawable)this.map.remove(paramString);
      if (localBitmapDrawable != null)
        this.size -= safeSizeOf(paramString, localBitmapDrawable);
      monitorexit;
      if (localBitmapDrawable != null)
      {
        String[] arrayOfString = paramString.split("@");
        if (arrayOfString.length > 1)
        {
          ArrayList localArrayList = (ArrayList)this.mapFilters.get(arrayOfString[0]);
          if (localArrayList != null)
          {
            localArrayList.remove(arrayOfString[1]);
            if (localArrayList.isEmpty())
              this.mapFilters.remove(arrayOfString[0]);
          }
        }
        entryRemoved(false, paramString, localBitmapDrawable, null);
      }
      return localBitmapDrawable;
    }
    finally
    {
      monitorexit;
    }
    throw paramString;
  }

  public final int size()
  {
    monitorenter;
    try
    {
      int i = this.size;
      monitorexit;
      return i;
    }
    finally
    {
      localObject = finally;
      monitorexit;
    }
    throw localObject;
  }

  protected int sizeOf(String paramString, BitmapDrawable paramBitmapDrawable)
  {
    return 1;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.LruCache
 * JD-Core Version:    0.6.0
 */