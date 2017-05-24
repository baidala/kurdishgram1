package org.vidogram.ui.Adapters;

import android.content.Context;
import android.location.Location;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Locale;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.ui.Cells.EmptyCell;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.LocationCell;
import org.vidogram.ui.Cells.LocationLoadingCell;
import org.vidogram.ui.Cells.LocationPoweredCell;
import org.vidogram.ui.Cells.SendLocationCell;
import org.vidogram.ui.Components.RecyclerListView.Holder;

public class LocationActivityAdapter extends BaseLocationAdapter
{
  private Location customLocation;
  private Location gpsLocation;
  private Context mContext;
  private int overScrollHeight;
  private SendLocationCell sendLocationCell;

  public LocationActivityAdapter(Context paramContext)
  {
    this.mContext = paramContext;
  }

  private void updateCell()
  {
    if (this.sendLocationCell != null)
    {
      if (this.customLocation != null)
        this.sendLocationCell.setText(LocaleController.getString("SendSelectedLocation", 2131166420), String.format(Locale.US, "(%f,%f)", new Object[] { Double.valueOf(this.customLocation.getLatitude()), Double.valueOf(this.customLocation.getLongitude()) }));
    }
    else
      return;
    if (this.gpsLocation != null)
    {
      this.sendLocationCell.setText(LocaleController.getString("SendLocation", 2131166416), LocaleController.formatString("AccurateTo", 2131165226, new Object[] { LocaleController.formatPluralString("Meters", (int)this.gpsLocation.getAccuracy()) }));
      return;
    }
    this.sendLocationCell.setText(LocaleController.getString("SendLocation", 2131166416), LocaleController.getString("Loading", 2131165920));
  }

  public TLRPC.TL_messageMediaVenue getItem(int paramInt)
  {
    if ((paramInt > 2) && (paramInt < this.places.size() + 3))
      return (TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 3);
    return null;
  }

  public int getItemCount()
  {
    if ((this.searching) || ((!this.searching) && (this.places.isEmpty())))
      return 4;
    int j = this.places.size();
    if (this.places.isEmpty());
    for (int i = 0; ; i = 1)
      return i + (j + 3);
  }

  public int getItemViewType(int paramInt)
  {
    int i = 1;
    if (paramInt == 0)
      i = 0;
    do
      return i;
    while (paramInt == 1);
    if (paramInt == 2)
      return 2;
    if ((this.searching) || ((!this.searching) && (this.places.isEmpty())))
      return 4;
    if (paramInt == this.places.size() + 3)
      return 5;
    return 3;
  }

  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    int i = paramViewHolder.getAdapterPosition();
    return (i != 2) && (i != 0) && ((i != 3) || ((!this.searching) && ((this.searching) || (!this.places.isEmpty())))) && (i != this.places.size() + 3);
  }

  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    switch (paramViewHolder.getItemViewType())
    {
    default:
      return;
    case 0:
      ((EmptyCell)paramViewHolder.itemView).setHeight(this.overScrollHeight);
      return;
    case 1:
      this.sendLocationCell = ((SendLocationCell)paramViewHolder.itemView);
      updateCell();
      return;
    case 2:
      ((GraySectionCell)paramViewHolder.itemView).setText(LocaleController.getString("NearbyPlaces", 2131165999));
      return;
    case 3:
      ((LocationCell)paramViewHolder.itemView).setLocation((TLRPC.TL_messageMediaVenue)this.places.get(paramInt - 3), (String)this.iconUrls.get(paramInt - 3), true);
      return;
    case 4:
    }
    ((LocationLoadingCell)paramViewHolder.itemView).setLoading(this.searching);
  }

  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    switch (paramInt)
    {
    default:
      paramViewGroup = new LocationPoweredCell(this.mContext);
    case 0:
    case 1:
    case 2:
    case 3:
    case 4:
    }
    while (true)
    {
      return new RecyclerListView.Holder(paramViewGroup);
      paramViewGroup = new EmptyCell(this.mContext);
      continue;
      paramViewGroup = new SendLocationCell(this.mContext);
      continue;
      paramViewGroup = new GraySectionCell(this.mContext);
      continue;
      paramViewGroup = new LocationCell(this.mContext);
      continue;
      paramViewGroup = new LocationLoadingCell(this.mContext);
    }
  }

  public void setCustomLocation(Location paramLocation)
  {
    this.customLocation = paramLocation;
    updateCell();
  }

  public void setGpsLocation(Location paramLocation)
  {
    this.gpsLocation = paramLocation;
    updateCell();
  }

  public void setOverScrollHeight(int paramInt)
  {
    this.overScrollHeight = paramInt;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.LocationActivityAdapter
 * JD-Core Version:    0.6.0
 */