package org.vidogram.VidogramUi.a;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import itman.Vidofilm.a.b;
import java.util.ArrayList;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.Cells.LoadingCell;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class c extends RecyclerListView.SelectionAdapter
{
  ArrayList<b> a;
  String b;
  private Context c;

  public c(Context paramContext, String paramString)
  {
    this.c = paramContext;
    this.a = new ArrayList();
    this.b = paramString;
    a();
  }

  public b a(int paramInt)
  {
    ArrayList localArrayList = this.a;
    if ((paramInt < 0) || (paramInt >= localArrayList.size()))
      return null;
    return (b)localArrayList.get(paramInt);
  }

  public void a()
  {
    this.a = itman.Vidofilm.d.a.a(this.c).a(this.b);
  }

  public int getItemCount()
  {
    return this.a.size();
  }

  public long getItemId(int paramInt)
  {
    return paramInt;
  }

  public int getItemViewType(int paramInt)
  {
    if (paramInt == this.a.size())
      return 1;
    return 0;
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return paramViewHolder.getItemViewType() != 1;
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    if (paramViewHolder.getItemViewType() == 0)
    {
      paramViewHolder = (a)paramViewHolder.itemView;
      if (paramInt == getItemCount() - 1)
        break label42;
    }
    label42: for (boolean bool = true; ; bool = false)
    {
      paramViewHolder.a = bool;
      paramViewHolder.setDialog(a(paramInt));
      return;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    paramViewGroup = null;
    if (paramInt == 0)
      paramViewGroup = new a(this.c);
    while (true)
    {
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
      return new a(paramViewGroup);
      if (paramInt != 1)
        continue;
      paramViewGroup = new LoadingCell(this.c);
    }
  }

  public void onViewAttachedToWindow(RecyclerView.ViewHolder paramViewHolder)
  {
  }

  private class a extends RecyclerView.ViewHolder
  {
    public a(View arg2)
    {
      super();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.a.c
 * JD-Core Version:    0.6.0
 */