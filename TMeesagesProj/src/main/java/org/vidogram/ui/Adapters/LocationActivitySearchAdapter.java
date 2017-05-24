package org.vidogram.ui.Adapters;

import android.content.Context;
import android.view.ViewGroup;
import java.util.ArrayList;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.ui.Cells.LocationCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;

public class LocationActivitySearchAdapter extends BaseLocationAdapter
{
  private Context mContext;

  public LocationActivitySearchAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }

  public TLRPC.TL_messageMediaVenue getItem(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < this.places.size()))
      return (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);
    return null;
  }

  public int getItemCount()
  {
    return this.places.size();
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return true;
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    paramViewHolder = (LocationCell)paramViewHolder.itemView;
    TLRPC.TL_messageMediaVenue localTL_messageMediaVenue = (TLRPC.TL_messageMediaVenue)this.places.get(paramInt);
    String str = (String)this.iconUrls.get(paramInt);
    if (paramInt != this.places.size() - 1);
    for (boolean bool = true; ; bool = false)
    {
      paramViewHolder.setLocation(localTL_messageMediaVenue, str, bool);
      return;
    }
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new RecyclerListView.Holder(new LocationCell(this.mContext));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.LocationActivitySearchAdapter
 * JD-Core Version:    0.6.0
 */