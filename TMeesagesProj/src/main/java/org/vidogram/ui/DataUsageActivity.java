package org.vidogram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.StatsController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.messenger.time.FastDateFormat;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataUsageActivity extends BaseFragment
{
  private int audiosBytesReceivedRow;
  private int audiosBytesSentRow;
  private int audiosReceivedRow;
  private int audiosSection2Row;
  private int audiosSectionRow;
  private int audiosSentRow;
  private int callsBytesReceivedRow;
  private int callsBytesSentRow;
  private int callsReceivedRow;
  private int callsSection2Row;
  private int callsSectionRow;
  private int callsSentRow;
  private int callsTotalTimeRow;
  private int currentType;
  private int filesBytesReceivedRow;
  private int filesBytesSentRow;
  private int filesReceivedRow;
  private int filesSection2Row;
  private int filesSectionRow;
  private int filesSentRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int messagesBytesReceivedRow;
  private int messagesBytesSentRow;
  private int messagesReceivedRow = -1;
  private int messagesSection2Row;
  private int messagesSectionRow;
  private int messagesSentRow = -1;
  private int photosBytesReceivedRow;
  private int photosBytesSentRow;
  private int photosReceivedRow;
  private int photosSection2Row;
  private int photosSectionRow;
  private int photosSentRow;
  private int resetRow;
  private int resetSection2Row;
  private int rowCount;
  private int totalBytesReceivedRow;
  private int totalBytesSentRow;
  private int totalSection2Row;
  private int totalSectionRow;
  private int videosBytesReceivedRow;
  private int videosBytesSentRow;
  private int videosReceivedRow;
  private int videosSection2Row;
  private int videosSectionRow;
  private int videosSentRow;

  public DataUsageActivity(int paramInt)
  {
    this.currentType = paramInt;
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    if (this.currentType == 0)
      this.actionBar.setTitle(LocaleController.getString("MobileUsage", 2131165988));
    while (true)
    {
      if (AndroidUtilities.isTablet())
        this.actionBar.setOccupyStatusBar(false);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            DataUsageActivity.this.finishFragment();
        }
      });
      this.listAdapter = new ListAdapter(paramContext);
      this.fragmentView = new FrameLayout(paramContext);
      this.fragmentView.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
      FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
      this.listView = new RecyclerListView(paramContext);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1, 51));
      this.listView.setAdapter(this.listAdapter);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          if (DataUsageActivity.this.getParentActivity() == null);
          do
            return;
          while (paramInt != DataUsageActivity.this.resetRow);
          paramView = new AlertDialog.Builder(DataUsageActivity.this.getParentActivity());
          paramView.setTitle(LocaleController.getString("AppName", 2131165319));
          paramView.setMessage(LocaleController.getString("ResetStatisticsAlert", 2131166352));
          paramView.setPositiveButton(LocaleController.getString("Reset", 2131166338), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              StatsController.getInstance().resetStats(DataUsageActivity.this.currentType);
              DataUsageActivity.this.listAdapter.notifyDataSetChanged();
            }
          });
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          DataUsageActivity.this.showDialog(paramView.create());
        }
      });
      localFrameLayout.addView(this.actionBar);
      return this.fragmentView;
      if (this.currentType == 1)
      {
        this.actionBar.setTitle(LocaleController.getString("WiFiUsage", 2131166627));
        continue;
      }
      if (this.currentType != 2)
        continue;
      this.actionBar.setTitle(LocaleController.getString("RoamingUsage", 2131166367));
    }
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, HeaderCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteRedText2") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.photosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.videosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.audiosSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesBytesSentRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesBytesReceivedRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.filesSection2Row = i;
    if (MessagesController.getInstance().callsEnabled)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSentRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsReceivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsBytesSentRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsBytesReceivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsTotalTimeRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.callsSection2Row = i; ; this.callsSection2Row = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagesSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagesBytesSentRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagesBytesReceivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.messagesSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.totalSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.totalBytesSentRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.totalBytesReceivedRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.totalSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.resetSection2Row = i;
      return true;
      this.callsSectionRow = -1;
      this.callsSentRow = -1;
      this.callsReceivedRow = -1;
      this.callsBytesSentRow = -1;
      this.callsBytesReceivedRow = -1;
      this.callsTotalTimeRow = -1;
    }
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public int getItemCount()
    {
      return DataUsageActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if (paramInt == DataUsageActivity.this.resetSection2Row)
        return 3;
      if ((paramInt == DataUsageActivity.this.resetSection2Row) || (paramInt == DataUsageActivity.this.callsSection2Row) || (paramInt == DataUsageActivity.this.filesSection2Row) || (paramInt == DataUsageActivity.this.audiosSection2Row) || (paramInt == DataUsageActivity.this.videosSection2Row) || (paramInt == DataUsageActivity.this.photosSection2Row) || (paramInt == DataUsageActivity.this.messagesSection2Row) || (paramInt == DataUsageActivity.this.totalSection2Row))
        return 0;
      if ((paramInt == DataUsageActivity.this.totalSectionRow) || (paramInt == DataUsageActivity.this.callsSectionRow) || (paramInt == DataUsageActivity.this.filesSectionRow) || (paramInt == DataUsageActivity.this.audiosSectionRow) || (paramInt == DataUsageActivity.this.videosSectionRow) || (paramInt == DataUsageActivity.this.photosSectionRow) || (paramInt == DataUsageActivity.this.messagesSectionRow))
        return 2;
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getAdapterPosition() == DataUsageActivity.this.resetRow;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
        do
        {
          TextSettingsCell localTextSettingsCell;
          int i;
          do
          {
            return;
            if (paramInt == DataUsageActivity.this.resetSection2Row)
            {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
              return;
            }
            paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
            return;
            localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == DataUsageActivity.this.resetRow)
            {
              localTextSettingsCell.setTag("windowBackgroundWhiteRedText2");
              localTextSettingsCell.setText(LocaleController.getString("ResetStatistics", 2131166351), false);
              localTextSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteRedText2"));
              return;
            }
            localTextSettingsCell.setTag("windowBackgroundWhiteBlackText");
            localTextSettingsCell.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            if ((paramInt == DataUsageActivity.this.callsSentRow) || (paramInt == DataUsageActivity.this.callsReceivedRow) || (paramInt == DataUsageActivity.this.callsBytesSentRow) || (paramInt == DataUsageActivity.this.callsBytesReceivedRow))
              i = 0;
            while (paramInt == DataUsageActivity.this.callsSentRow)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("OutgoingCalls", 2131166170), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance().getSentItemsCount(DataUsageActivity.access$100(DataUsageActivity.this), i)) }), true);
              return;
              if ((paramInt == DataUsageActivity.this.messagesSentRow) || (paramInt == DataUsageActivity.this.messagesReceivedRow) || (paramInt == DataUsageActivity.this.messagesBytesSentRow) || (paramInt == DataUsageActivity.this.messagesBytesReceivedRow))
              {
                i = 1;
                continue;
              }
              if ((paramInt == DataUsageActivity.this.photosSentRow) || (paramInt == DataUsageActivity.this.photosReceivedRow) || (paramInt == DataUsageActivity.this.photosBytesSentRow) || (paramInt == DataUsageActivity.this.photosBytesReceivedRow))
              {
                i = 4;
                continue;
              }
              if ((paramInt == DataUsageActivity.this.audiosSentRow) || (paramInt == DataUsageActivity.this.audiosReceivedRow) || (paramInt == DataUsageActivity.this.audiosBytesSentRow) || (paramInt == DataUsageActivity.this.audiosBytesReceivedRow))
              {
                i = 3;
                continue;
              }
              if ((paramInt == DataUsageActivity.this.videosSentRow) || (paramInt == DataUsageActivity.this.videosReceivedRow) || (paramInt == DataUsageActivity.this.videosBytesSentRow) || (paramInt == DataUsageActivity.this.videosBytesReceivedRow))
              {
                i = 2;
                continue;
              }
              if ((paramInt == DataUsageActivity.this.filesSentRow) || (paramInt == DataUsageActivity.this.filesReceivedRow) || (paramInt == DataUsageActivity.this.filesBytesSentRow) || (paramInt == DataUsageActivity.this.filesBytesReceivedRow))
              {
                i = 5;
                continue;
              }
              i = 6;
            }
            if (paramInt == DataUsageActivity.this.callsReceivedRow)
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("IncomingCalls", 2131165831), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance().getRecivedItemsCount(DataUsageActivity.access$100(DataUsageActivity.this), i)) }), true);
              return;
            }
            if (paramInt == DataUsageActivity.this.callsTotalTimeRow)
            {
              i = StatsController.getInstance().getCallsTotalTime(DataUsageActivity.this.currentType);
              paramInt = i / 3600;
              int j = i - paramInt * 3600;
              i = j / 60;
              j -= i * 60;
              if (paramInt != 0);
              for (paramViewHolder = String.format("%d:%02d:%02d", new Object[] { Integer.valueOf(paramInt), Integer.valueOf(i), Integer.valueOf(j) }); ; paramViewHolder = String.format("%d:%02d", new Object[] { Integer.valueOf(i), Integer.valueOf(j) }))
              {
                localTextSettingsCell.setTextAndValue(LocaleController.getString("CallsTotalTime", 2131165426), paramViewHolder, false);
                return;
              }
            }
            if ((paramInt == DataUsageActivity.this.messagesSentRow) || (paramInt == DataUsageActivity.this.photosSentRow) || (paramInt == DataUsageActivity.this.videosSentRow) || (paramInt == DataUsageActivity.this.audiosSentRow) || (paramInt == DataUsageActivity.this.filesSentRow))
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("CountSent", 2131165587), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance().getSentItemsCount(DataUsageActivity.access$100(DataUsageActivity.this), i)) }), true);
              return;
            }
            if ((paramInt == DataUsageActivity.this.messagesReceivedRow) || (paramInt == DataUsageActivity.this.photosReceivedRow) || (paramInt == DataUsageActivity.this.videosReceivedRow) || (paramInt == DataUsageActivity.this.audiosReceivedRow) || (paramInt == DataUsageActivity.this.filesReceivedRow))
            {
              localTextSettingsCell.setTextAndValue(LocaleController.getString("CountReceived", 2131165586), String.format("%d", new Object[] { Integer.valueOf(StatsController.getInstance().getRecivedItemsCount(DataUsageActivity.access$100(DataUsageActivity.this), i)) }), true);
              return;
            }
            if ((paramInt != DataUsageActivity.this.messagesBytesSentRow) && (paramInt != DataUsageActivity.this.photosBytesSentRow) && (paramInt != DataUsageActivity.this.videosBytesSentRow) && (paramInt != DataUsageActivity.this.audiosBytesSentRow) && (paramInt != DataUsageActivity.this.filesBytesSentRow) && (paramInt != DataUsageActivity.this.callsBytesSentRow) && (paramInt != DataUsageActivity.this.totalBytesSentRow))
              continue;
            localTextSettingsCell.setTextAndValue(LocaleController.getString("BytesSent", 2131165405), AndroidUtilities.formatFileSize(StatsController.getInstance().getSentBytesCount(DataUsageActivity.this.currentType, i)), true);
            return;
          }
          while ((paramInt != DataUsageActivity.this.messagesBytesReceivedRow) && (paramInt != DataUsageActivity.this.photosBytesReceivedRow) && (paramInt != DataUsageActivity.this.videosBytesReceivedRow) && (paramInt != DataUsageActivity.this.audiosBytesReceivedRow) && (paramInt != DataUsageActivity.this.filesBytesReceivedRow) && (paramInt != DataUsageActivity.this.callsBytesReceivedRow) && (paramInt != DataUsageActivity.this.totalBytesReceivedRow));
          paramViewHolder = LocaleController.getString("BytesReceived", 2131165404);
          String str = AndroidUtilities.formatFileSize(StatsController.getInstance().getReceivedBytesCount(DataUsageActivity.this.currentType, i));
          if (paramInt != DataUsageActivity.this.totalBytesReceivedRow);
          while (true)
          {
            localTextSettingsCell.setTextAndValue(paramViewHolder, str, bool);
            return;
            bool = false;
          }
          paramViewHolder = (HeaderCell)paramViewHolder.itemView;
          if (paramInt == DataUsageActivity.this.totalSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("TotalDataUsage", 2131166524));
            return;
          }
          if (paramInt == DataUsageActivity.this.callsSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("CallsDataUsage", 2131165425));
            return;
          }
          if (paramInt == DataUsageActivity.this.filesSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("FilesDataUsage", 2131165710));
            return;
          }
          if (paramInt == DataUsageActivity.this.audiosSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("LocalAudioCache", 2131165921));
            return;
          }
          if (paramInt == DataUsageActivity.this.videosSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("LocalVideoCache", 2131165930));
            return;
          }
          if (paramInt != DataUsageActivity.this.photosSectionRow)
            continue;
          paramViewHolder.setText(LocaleController.getString("LocalPhotoCache", 2131165929));
          return;
        }
        while (paramInt != DataUsageActivity.this.messagesSectionRow);
        paramViewHolder.setText(LocaleController.getString("MessagesDataUsage", 2131165967));
        return;
      case 3:
      }
      paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
      paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
      paramViewHolder.setText(LocaleController.formatString("NetworkUsageSince", 2131166000, new Object[] { LocaleController.getInstance().formatterStats.format(StatsController.getInstance().getResetStatsDate(DataUsageActivity.access$100(DataUsageActivity.this))) }));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      switch (paramInt)
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      }
      while (true)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ShadowSectionCell(this.mContext);
        continue;
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new HeaderCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
        continue;
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DataUsageActivity
 * JD-Core Version:    0.6.0
 */