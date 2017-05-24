package org.vidogram.messenger.support.widget;

import java.util.ArrayList;

class PositionMap<E>
  implements Cloneable
{
  private static final Object DELETED = new Object();
  private boolean mGarbage = false;
  private int[] mKeys;
  private int mSize;
  private Object[] mValues;

  public PositionMap()
  {
    this(10);
  }

  public PositionMap(int paramInt)
  {
    if (paramInt == 0)
      this.mKeys = ContainerHelpers.EMPTY_INTS;
    for (this.mValues = ContainerHelpers.EMPTY_OBJECTS; ; this.mValues = new Object[paramInt])
    {
      this.mSize = 0;
      return;
      paramInt = idealIntArraySize(paramInt);
      this.mKeys = new int[paramInt];
    }
  }

  private void gc()
  {
    int m = this.mSize;
    int[] arrayOfInt = this.mKeys;
    Object[] arrayOfObject = this.mValues;
    int i = 0;
    int k;
    for (int j = 0; i < m; j = k)
    {
      Object localObject = arrayOfObject[i];
      k = j;
      if (localObject != DELETED)
      {
        if (i != j)
        {
          arrayOfInt[j] = arrayOfInt[i];
          arrayOfObject[j] = localObject;
          arrayOfObject[i] = null;
        }
        k = j + 1;
      }
      i += 1;
    }
    this.mGarbage = false;
    this.mSize = j;
  }

  static int idealBooleanArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt);
  }

  static int idealByteArraySize(int paramInt)
  {
    int i = 4;
    while (true)
    {
      int j = paramInt;
      if (i < 32)
      {
        if (paramInt <= (1 << i) - 12)
          j = (1 << i) - 12;
      }
      else
        return j;
      i += 1;
    }
  }

  static int idealCharArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }

  static int idealFloatArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }

  static int idealIntArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }

  static int idealLongArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 8) / 8;
  }

  static int idealObjectArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 4) / 4;
  }

  static int idealShortArraySize(int paramInt)
  {
    return idealByteArraySize(paramInt * 2) / 2;
  }

  public void append(int paramInt, E paramE)
  {
    if ((this.mSize != 0) && (paramInt <= this.mKeys[(this.mSize - 1)]))
    {
      put(paramInt, paramE);
      return;
    }
    if ((this.mGarbage) && (this.mSize >= this.mKeys.length))
      gc();
    int i = this.mSize;
    if (i >= this.mKeys.length)
    {
      int j = idealIntArraySize(i + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
      this.mValues = arrayOfObject;
    }
    this.mKeys[i] = paramInt;
    this.mValues[i] = paramE;
    this.mSize = (i + 1);
  }

  public void clear()
  {
    int j = this.mSize;
    Object[] arrayOfObject = this.mValues;
    int i = 0;
    while (i < j)
    {
      arrayOfObject[i] = null;
      i += 1;
    }
    this.mSize = 0;
    this.mGarbage = false;
  }

  // ERROR //
  public PositionMap<E> clone()
  {
    // Byte code:
    //   0: aload_0
    //   1: invokespecial 84	java/lang/Object:clone	()Ljava/lang/Object;
    //   4: checkcast 2	org/vidogram/messenger/support/widget/PositionMap
    //   7: astore_1
    //   8: aload_1
    //   9: aload_0
    //   10: getfield 38	org/vidogram/messenger/support/widget/PositionMap:mKeys	[I
    //   13: invokevirtual 86	[I:clone	()Ljava/lang/Object;
    //   16: checkcast 85	[I
    //   19: putfield 38	org/vidogram/messenger/support/widget/PositionMap:mKeys	[I
    //   22: aload_1
    //   23: aload_0
    //   24: getfield 43	org/vidogram/messenger/support/widget/PositionMap:mValues	[Ljava/lang/Object;
    //   27: invokevirtual 88	[Ljava/lang/Object;:clone	()Ljava/lang/Object;
    //   30: checkcast 87	[Ljava/lang/Object;
    //   33: putfield 43	org/vidogram/messenger/support/widget/PositionMap:mValues	[Ljava/lang/Object;
    //   36: aload_1
    //   37: areturn
    //   38: astore_1
    //   39: aconst_null
    //   40: areturn
    //   41: astore_2
    //   42: aload_1
    //   43: areturn
    //
    // Exception table:
    //   from	to	target	type
    //   0	8	38	java/lang/CloneNotSupportedException
    //   8	36	41	java/lang/CloneNotSupportedException
  }

  public void delete(int paramInt)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if ((paramInt >= 0) && (this.mValues[paramInt] != DELETED))
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
  }

  public E get(int paramInt)
  {
    return get(paramInt, null);
  }

  public E get(int paramInt, E paramE)
  {
    paramInt = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if ((paramInt < 0) || (this.mValues[paramInt] == DELETED))
      return paramE;
    return this.mValues[paramInt];
  }

  public int indexOfKey(int paramInt)
  {
    if (this.mGarbage)
      gc();
    return ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
  }

  public int indexOfValue(E paramE)
  {
    if (this.mGarbage)
      gc();
    int i = 0;
    while (i < this.mSize)
    {
      if (this.mValues[i] == paramE)
        return i;
      i += 1;
    }
    return -1;
  }

  public void insertKeyRange(int paramInt1, int paramInt2)
  {
  }

  public int keyAt(int paramInt)
  {
    if (this.mGarbage)
      gc();
    return this.mKeys[paramInt];
  }

  public void put(int paramInt, E paramE)
  {
    int i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt);
    if (i >= 0)
    {
      this.mValues[i] = paramE;
      return;
    }
    int j = i ^ 0xFFFFFFFF;
    if ((j < this.mSize) && (this.mValues[j] == DELETED))
    {
      this.mKeys[j] = paramInt;
      this.mValues[j] = paramE;
      return;
    }
    i = j;
    if (this.mGarbage)
    {
      i = j;
      if (this.mSize >= this.mKeys.length)
      {
        gc();
        i = ContainerHelpers.binarySearch(this.mKeys, this.mSize, paramInt) ^ 0xFFFFFFFF;
      }
    }
    if (this.mSize >= this.mKeys.length)
    {
      j = idealIntArraySize(this.mSize + 1);
      int[] arrayOfInt = new int[j];
      Object[] arrayOfObject = new Object[j];
      System.arraycopy(this.mKeys, 0, arrayOfInt, 0, this.mKeys.length);
      System.arraycopy(this.mValues, 0, arrayOfObject, 0, this.mValues.length);
      this.mKeys = arrayOfInt;
      this.mValues = arrayOfObject;
    }
    if (this.mSize - i != 0)
    {
      System.arraycopy(this.mKeys, i, this.mKeys, i + 1, this.mSize - i);
      System.arraycopy(this.mValues, i, this.mValues, i + 1, this.mSize - i);
    }
    this.mKeys[i] = paramInt;
    this.mValues[i] = paramE;
    this.mSize += 1;
  }

  public void remove(int paramInt)
  {
    delete(paramInt);
  }

  public void removeAt(int paramInt)
  {
    if (this.mValues[paramInt] != DELETED)
    {
      this.mValues[paramInt] = DELETED;
      this.mGarbage = true;
    }
  }

  public void removeAtRange(int paramInt1, int paramInt2)
  {
    paramInt2 = Math.min(this.mSize, paramInt1 + paramInt2);
    while (paramInt1 < paramInt2)
    {
      removeAt(paramInt1);
      paramInt1 += 1;
    }
  }

  public void removeKeyRange(ArrayList<E> paramArrayList, int paramInt1, int paramInt2)
  {
  }

  public void setValueAt(int paramInt, E paramE)
  {
    if (this.mGarbage)
      gc();
    this.mValues[paramInt] = paramE;
  }

  public int size()
  {
    if (this.mGarbage)
      gc();
    return this.mSize;
  }

  public String toString()
  {
    if (size() <= 0)
      return "{}";
    StringBuilder localStringBuilder = new StringBuilder(this.mSize * 28);
    localStringBuilder.append('{');
    int i = 0;
    if (i < this.mSize)
    {
      if (i > 0)
        localStringBuilder.append(", ");
      localStringBuilder.append(keyAt(i));
      localStringBuilder.append('=');
      Object localObject = valueAt(i);
      if (localObject != this)
        localStringBuilder.append(localObject);
      while (true)
      {
        i += 1;
        break;
        localStringBuilder.append("(this Map)");
      }
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }

  public E valueAt(int paramInt)
  {
    if (this.mGarbage)
      gc();
    return this.mValues[paramInt];
  }

  static class ContainerHelpers
  {
    static final boolean[] EMPTY_BOOLEANS = new boolean[0];
    static final int[] EMPTY_INTS = new int[0];
    static final long[] EMPTY_LONGS = new long[0];
    static final Object[] EMPTY_OBJECTS = new Object[0];

    static int binarySearch(int[] paramArrayOfInt, int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = paramInt1 - 1;
      paramInt1 = i;
      i = j;
      while (paramInt1 <= i)
      {
        j = paramInt1 + i >>> 1;
        int k = paramArrayOfInt[j];
        if (k < paramInt2)
        {
          paramInt1 = j + 1;
          continue;
        }
        if (k > paramInt2)
        {
          i = j - 1;
          continue;
        }
        return j;
      }
      return paramInt1 ^ 0xFFFFFFFF;
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.support.widget.PositionMap
 * JD-Core Version:    0.6.0
 */