package org.vidogram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.ArrayList<Ljava.util.ArrayList<Ljava.lang.Integer;>;>;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.support.widget.GridLayoutManager;

public class ExtendedGridLayoutManager extends GridLayoutManager
{
  private int calculatedWidth;
  private SparseArray<Integer> itemSpans = new SparseArray();
  private SparseArray<Integer> itemsToRow = new SparseArray();
  private ArrayList<ArrayList<Integer>> rows;

  public ExtendedGridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }

  private void checkLayout()
  {
    if ((this.itemSpans.size() != getFlowItemCount()) || (this.calculatedWidth != getWidth()))
    {
      this.calculatedWidth = getWidth();
      prepareLayout(getWidth());
    }
  }

  private ArrayList<ArrayList<Integer>> getLinearPartitionForSequence(int[] paramArrayOfInt, int paramInt)
  {
    int i = 0;
    int j = paramArrayOfInt.length;
    if (paramInt <= 0)
      return new ArrayList();
    if ((paramInt >= j) || (j == 1))
    {
      localArrayList1 = new ArrayList(paramArrayOfInt.length);
      paramInt = i;
      while (paramInt < paramArrayOfInt.length)
      {
        localObject = new ArrayList(1);
        ((ArrayList)localObject).add(Integer.valueOf(paramArrayOfInt[paramInt]));
        localArrayList1.add(localObject);
        paramInt += 1;
      }
      return localArrayList1;
    }
    Object localObject = getLinearPartitionTable(paramArrayOfInt, paramInt);
    int k = paramInt - 1;
    ArrayList localArrayList1 = new ArrayList();
    i = paramInt - 2;
    paramInt = j - 1;
    if (i >= 0)
    {
      if (paramInt < 1)
        localArrayList1.add(0, new ArrayList());
      while (true)
      {
        i -= 1;
        break;
        ArrayList localArrayList2 = new ArrayList();
        j = localObject[((paramInt - 1) * k + i)] + 1;
        while (j < paramInt + 1)
        {
          localArrayList2.add(Integer.valueOf(paramArrayOfInt[j]));
          j += 1;
        }
        localArrayList1.add(0, localArrayList2);
        paramInt = localObject[((paramInt - 1) * k + i)];
      }
    }
    localObject = new ArrayList();
    i = 0;
    while (i < paramInt + 1)
    {
      ((ArrayList)localObject).add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    localArrayList1.add(0, localObject);
    return (ArrayList<ArrayList<Integer>>)localArrayList1;
  }

  private int[] getLinearPartitionTable(int[] paramArrayOfInt, int paramInt)
  {
    int i3 = paramArrayOfInt.length;
    int[] arrayOfInt1 = new int[i3 * paramInt];
    int[] arrayOfInt2 = new int[(i3 - 1) * (paramInt - 1)];
    int i = 0;
    int k;
    if (i < i3)
    {
      k = paramArrayOfInt[i];
      if (i != 0);
      for (j = arrayOfInt1[((i - 1) * paramInt)]; ; j = 0)
      {
        arrayOfInt1[(i * paramInt)] = (j + k);
        i += 1;
        break;
      }
    }
    i = 0;
    while (i < paramInt)
    {
      arrayOfInt1[i] = paramArrayOfInt[0];
      i += 1;
    }
    int j = 1;
    while (j < i3)
    {
      k = 1;
      while (k < paramInt)
      {
        int i1 = 2147483647;
        int m = 0;
        i = 0;
        while (i < j)
        {
          int i2 = Math.max(arrayOfInt1[(i * paramInt + (k - 1))], arrayOfInt1[(j * paramInt)] - arrayOfInt1[(i * paramInt)]);
          int n;
          if (i != 0)
          {
            n = m;
            if (i2 >= m);
          }
          else
          {
            i1 = i;
            n = i2;
          }
          i += 1;
          m = n;
        }
        arrayOfInt1[(j * paramInt + k)] = m;
        arrayOfInt2[((j - 1) * (paramInt - 1) + (k - 1))] = i1;
        k += 1;
      }
      j += 1;
    }
    return arrayOfInt2;
  }

  private void prepareLayout(float paramFloat)
  {
    this.itemSpans.clear();
    this.itemsToRow.clear();
    int j = AndroidUtilities.dp(100.0F);
    float f1 = 0.0F;
    int i2 = getFlowItemCount();
    Object localObject = new int[i2];
    int i = 0;
    Size localSize;
    while (i < i2)
    {
      localSize = sizeForItem(i);
      f1 += localSize.width / localSize.height * j;
      localObject[i] = Math.round(localSize.width / localSize.height * 100.0F);
      i += 1;
    }
    this.rows = getLinearPartitionForSequence(localObject, Math.max(Math.round(f1 / paramFloat), 1));
    i = 0;
    int k = 0;
    int m;
    float f2;
    if (k < this.rows.size())
    {
      localObject = (ArrayList)this.rows.get(k);
      m = ((ArrayList)localObject).size();
      f2 = 0.0F;
      j = i;
      while (j < i + m)
      {
        localSize = sizeForItem(j);
        f1 = localSize.width / localSize.height;
        j += 1;
        f2 = f1 + f2;
      }
      if ((this.rows.size() != 1) || (k != this.rows.size() - 1))
        break label445;
      if (((ArrayList)localObject).size() < 2)
        f1 = (float)Math.floor(paramFloat / 3.0F);
    }
    while (true)
    {
      j = getSpanCount();
      int i3 = i + ((ArrayList)localObject).size();
      m = i;
      label274: if (m < i3)
      {
        localSize = sizeForItem(m);
        float f3 = f1 / f2;
        int n = Math.round(localSize.width / localSize.height * f3);
        if ((i2 < 3) || (m != i3 - 1))
        {
          n = (int)(n / paramFloat * getSpanCount());
          j -= n;
        }
        while (true)
        {
          this.itemSpans.put(m, Integer.valueOf(n));
          m += 1;
          break label274;
          if (((ArrayList)localObject).size() >= 3)
            break label445;
          f1 = (float)Math.floor(2.0F * paramFloat / 3.0F);
          break;
          this.itemsToRow.put(m, Integer.valueOf(k));
          int i1 = j;
          n = j;
          j = i1;
        }
      }
      i += ((ArrayList)localObject).size();
      k += 1;
      break;
      return;
      label445: f1 = paramFloat;
    }
  }

  private Size sizeForItem(int paramInt)
  {
    Size localSize = getSizeForItem(paramInt);
    if (localSize.width == 0.0F)
      localSize.width = 100.0F;
    if (localSize.height == 0.0F)
      localSize.height = 100.0F;
    float f = localSize.width / localSize.height;
    if ((f > 4.0F) || (f < 0.2F))
    {
      f = Math.max(localSize.width, localSize.height);
      localSize.width = f;
      localSize.height = f;
    }
    return localSize;
  }

  protected int getFlowItemCount()
  {
    return getItemCount();
  }

  public int getRowsCount(int paramInt)
  {
    if (this.rows == null)
      prepareLayout(paramInt);
    if (this.rows != null)
      return this.rows.size();
    return 0;
  }

  protected Size getSizeForItem(int paramInt)
  {
    return new Size(100.0F, 100.0F);
  }

  public int getSpanSizeForItem(int paramInt)
  {
    checkLayout();
    return ((Integer)this.itemSpans.get(paramInt)).intValue();
  }

  public boolean isFirstRow(int paramInt)
  {
    checkLayout();
    return (this.rows != null) && (!this.rows.isEmpty()) && (paramInt < ((ArrayList)this.rows.get(0)).size());
  }

  public boolean isLastInRow(int paramInt)
  {
    checkLayout();
    return this.itemsToRow.get(paramInt) != null;
  }

  public boolean supportsPredictiveItemAnimations()
  {
    return false;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.ExtendedGridLayoutManager
 * JD-Core Version:    0.6.0
 */