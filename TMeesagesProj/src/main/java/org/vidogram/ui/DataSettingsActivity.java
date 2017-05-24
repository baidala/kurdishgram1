package org.vidogram.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutParams;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.CheckBoxCell;
import org.vidogram.ui.Cells.HeaderCell;
import org.vidogram.ui.Cells.ShadowSectionCell;
import org.vidogram.ui.Cells.TextDetailSettingsCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DataSettingsActivity extends BaseFragment
{
  private int callsSection2Row;
  private int callsSectionRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private int mediaDownloadSection2Row;
  private int mediaDownloadSectionRow;
  private int mobileDownloadRow;
  private int mobileUsageRow;
  private int roamingDownloadRow;
  private int roamingUsageRow;
  private int rowCount;
  private int storageUsageRow;
  private int usageSection2Row;
  private int usageSectionRow;
  private int useLessDataForCallsRow;
  private int wifiDownloadRow;
  private int wifiUsageRow;

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setTitle(LocaleController.getString("DataSettings", 2131165609));
    if (AndroidUtilities.isTablet())
      this.actionBar.setOccupyStatusBar(false);
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          DataSettingsActivity.this.finishFragment();
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
        if ((paramInt == DataSettingsActivity.this.wifiDownloadRow) || (paramInt == DataSettingsActivity.this.mobileDownloadRow) || (paramInt == DataSettingsActivity.this.roamingDownloadRow))
          if (DataSettingsActivity.this.getParentActivity() != null);
        label125: 
        do
        {
          return;
          Object localObject1 = new boolean[6];
          Object localObject2 = new BottomSheet.Builder(DataSettingsActivity.this.getParentActivity());
          int i = 0;
          Object localObject3;
          int j;
          if (paramInt == DataSettingsActivity.this.mobileDownloadRow)
          {
            i = MediaController.getInstance().mobileDataDownloadMask;
            ((BottomSheet.Builder)localObject2).setApplyTopPadding(false);
            ((BottomSheet.Builder)localObject2).setApplyBottomPadding(false);
            localObject3 = new LinearLayout(DataSettingsActivity.this.getParentActivity());
            ((LinearLayout)localObject3).setOrientation(1);
            j = 0;
            if (j >= 6)
              break label506;
            paramView = null;
            if (j != 0)
              break label308;
            if ((i & 0x1) == 0)
              break label302;
            k = 1;
            localObject1[j] = k;
            paramView = LocaleController.getString("LocalPhotoCache", 2131165929);
          }
          Object localObject4;
          do
          {
            localObject4 = new CheckBoxCell(DataSettingsActivity.this.getParentActivity(), true);
            ((CheckBoxCell)localObject4).setTag(Integer.valueOf(j));
            ((CheckBoxCell)localObject4).setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ((LinearLayout)localObject3).addView((View)localObject4, LayoutHelper.createLinear(-1, 48));
            ((CheckBoxCell)localObject4).setText(paramView, "", localObject1[j], true);
            ((CheckBoxCell)localObject4).setTextColor(Theme.getColor("dialogTextBlack"));
            ((CheckBoxCell)localObject4).setOnClickListener(new View.OnClickListener(localObject1)
            {
              public void onClick(View paramView)
              {
                paramView = (CheckBoxCell)paramView;
                int i = ((Integer)paramView.getTag()).intValue();
                boolean[] arrayOfBoolean = this.val$maskValues;
                if (this.val$maskValues[i] == 0);
                for (int j = 1; ; j = 0)
                {
                  arrayOfBoolean[i] = j;
                  paramView.setChecked(this.val$maskValues[i], true);
                  return;
                }
              }
            });
            j += 1;
            break label125;
            if (paramInt == DataSettingsActivity.this.wifiDownloadRow)
            {
              i = MediaController.getInstance().wifiDownloadMask;
              break;
            }
            if (paramInt != DataSettingsActivity.this.roamingDownloadRow)
              break;
            i = MediaController.getInstance().roamingDownloadMask;
            break;
            k = 0;
            break label148;
            if (j == 1)
            {
              if ((i & 0x2) != 0);
              for (k = 1; ; k = 0)
              {
                localObject1[j] = k;
                paramView = LocaleController.getString("LocalAudioCache", 2131165921);
                break;
              }
            }
            if (j == 2)
            {
              if ((i & 0x4) != 0);
              for (k = 1; ; k = 0)
              {
                localObject1[j] = k;
                paramView = LocaleController.getString("LocalVideoCache", 2131165930);
                break;
              }
            }
            if (j == 3)
            {
              if ((i & 0x8) != 0);
              for (k = 1; ; k = 0)
              {
                localObject1[j] = k;
                paramView = LocaleController.getString("FilesDataUsage", 2131165710);
                break;
              }
            }
            if (j != 4)
              continue;
            if ((i & 0x10) != 0);
            for (k = 1; ; k = 0)
            {
              localObject1[j] = k;
              paramView = LocaleController.getString("AttachMusic", 2131165366);
              break;
            }
          }
          while (j != 5);
          if ((i & 0x20) != 0);
          for (int k = 1; ; k = 0)
          {
            localObject1[j] = k;
            paramView = LocaleController.getString("LocalGifCache", 2131165927);
            break;
          }
          paramView = new BottomSheet.BottomSheetCell(DataSettingsActivity.this.getParentActivity(), 1);
          paramView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
          paramView.setTextAndIcon(LocaleController.getString("Save", 2131166371).toUpperCase(), 0);
          paramView.setTextColor(Theme.getColor("dialogTextBlue2"));
          paramView.setOnClickListener(new View.OnClickListener(localObject1, paramInt)
          {
            public void onClick(View paramView)
            {
              int k;
              try
              {
                if (DataSettingsActivity.this.visibleDialog != null)
                  DataSettingsActivity.this.visibleDialog.dismiss();
                j = 0;
                for (k = 0; ; k = i)
                {
                  if (j >= 6)
                    break label147;
                  i = k;
                  if (this.val$maskValues[j] != 0)
                  {
                    if (j != 0)
                      break;
                    i = k | 0x1;
                  }
                  j += 1;
                }
              }
              catch (java.lang.Exception paramView)
              {
                while (true)
                {
                  int j;
                  FileLog.e(paramView);
                  continue;
                  if (j == 1)
                  {
                    i = k | 0x2;
                    continue;
                  }
                  if (j == 2)
                  {
                    i = k | 0x4;
                    continue;
                  }
                  if (j == 3)
                  {
                    i = k | 0x8;
                    continue;
                  }
                  if (j == 4)
                  {
                    i = k | 0x10;
                    continue;
                  }
                  int i = k;
                  if (j != 5)
                    continue;
                  i = k | 0x20;
                }
                label147: paramView = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                if (this.val$position != DataSettingsActivity.this.mobileDownloadRow)
                  break label236;
              }
              paramView.putInt("mobileDataDownloadMask", k);
              MediaController.getInstance().mobileDataDownloadMask = k;
              while (true)
              {
                paramView.commit();
                if (DataSettingsActivity.this.listAdapter != null)
                  DataSettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                return;
                label236: if (this.val$position == DataSettingsActivity.this.wifiDownloadRow)
                {
                  paramView.putInt("wifiDownloadMask", k);
                  MediaController.getInstance().wifiDownloadMask = k;
                  continue;
                }
                if (this.val$position != DataSettingsActivity.this.roamingDownloadRow)
                  continue;
                paramView.putInt("roamingDownloadMask", k);
                MediaController.getInstance().roamingDownloadMask = k;
              }
            }
          });
          ((LinearLayout)localObject3).addView(paramView, LayoutHelper.createLinear(-1, 48));
          ((BottomSheet.Builder)localObject2).setCustomView((View)localObject3);
          DataSettingsActivity.this.showDialog(((BottomSheet.Builder)localObject2).create());
          return;
          if (paramInt == DataSettingsActivity.this.storageUsageRow)
          {
            DataSettingsActivity.this.presentFragment(new CacheControlActivity());
            return;
          }
          if (paramInt == DataSettingsActivity.this.useLessDataForCallsRow)
          {
            Object localObject5 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
            paramView = DataSettingsActivity.this.getParentActivity();
            localObject1 = DataSettingsActivity.this;
            localObject2 = LocaleController.getString("UseLessDataNever", 2131166546);
            localObject3 = LocaleController.getString("UseLessDataOnMobile", 2131166547);
            localObject4 = LocaleController.getString("UseLessDataAlways", 2131166545);
            String str = LocaleController.getString("VoipUseLessData", 2131166607);
            i = ((SharedPreferences)localObject5).getInt("VoipDataSaving", 0);
            localObject5 = new DialogInterface.OnClickListener((SharedPreferences)localObject5, paramInt)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                switch (paramInt)
                {
                default:
                  paramInt = -1;
                case 0:
                case 1:
                case 2:
                }
                while (true)
                {
                  if (paramInt != -1)
                    this.val$preferences.edit().putInt("VoipDataSaving", paramInt).commit();
                  if (DataSettingsActivity.this.listAdapter != null)
                    DataSettingsActivity.this.listAdapter.notifyItemChanged(this.val$position);
                  return;
                  paramInt = 0;
                  continue;
                  paramInt = 1;
                  continue;
                  paramInt = 2;
                }
              }
            };
            paramView = AlertsCreator.createSingleChoiceDialog(paramView, (BaseFragment)localObject1, new String[] { localObject2, localObject3, localObject4 }, str, i, (DialogInterface.OnClickListener)localObject5);
            DataSettingsActivity.this.setVisibleDialog(paramView);
            paramView.show();
            return;
          }
          if (paramInt == DataSettingsActivity.this.mobileUsageRow)
          {
            DataSettingsActivity.this.presentFragment(new DataUsageActivity(0));
            return;
          }
          if (paramInt != DataSettingsActivity.this.roamingUsageRow)
            continue;
          DataSettingsActivity.this.presentFragment(new DataUsageActivity(2));
          return;
        }
        while (paramInt != DataSettingsActivity.this.wifiUsageRow);
        label148: label302: label308: label506: DataSettingsActivity.this.presentFragment(new DataUsageActivity(1));
      }
    });
    localFrameLayout.addView(this.actionBar);
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class, TextSettingsCell.class, TextDetailSettingsCell.class }, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { ShadowSectionCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, 0, new Class[] { HeaderCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlueHeader"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextDetailSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteGrayText2") };
  }

  protected void onDialogDismiss(Dialog paramDialog)
  {
    MediaController.getInstance().checkAutodownloadSettings();
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mobileDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.wifiDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.roamingDownloadRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mediaDownloadSection2Row = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.usageSectionRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.storageUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.mobileUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.wifiUsageRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.roamingUsageRow = i;
    if (MessagesController.getInstance().callsEnabled)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.usageSection2Row = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSectionRow = i;
      i = this.rowCount;
      this.rowCount = (i + 1);
    }
    for (this.useLessDataForCallsRow = i; ; this.useLessDataForCallsRow = -1)
    {
      i = this.rowCount;
      this.rowCount = (i + 1);
      this.callsSection2Row = i;
      return true;
      this.usageSection2Row = -1;
      this.callsSectionRow = -1;
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
      return DataSettingsActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      int j = 1;
      int i;
      if ((paramInt == DataSettingsActivity.this.mediaDownloadSection2Row) || (paramInt == DataSettingsActivity.this.usageSection2Row) || (paramInt == DataSettingsActivity.this.callsSection2Row))
        i = 0;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  return i;
                  i = j;
                }
                while (paramInt == DataSettingsActivity.this.storageUsageRow);
                i = j;
              }
              while (paramInt == DataSettingsActivity.this.useLessDataForCallsRow);
              i = j;
            }
            while (paramInt == DataSettingsActivity.this.roamingUsageRow);
            i = j;
          }
          while (paramInt == DataSettingsActivity.this.wifiUsageRow);
          i = j;
        }
        while (paramInt == DataSettingsActivity.this.mobileUsageRow);
        if ((paramInt == DataSettingsActivity.this.wifiDownloadRow) || (paramInt == DataSettingsActivity.this.mobileDownloadRow) || (paramInt == DataSettingsActivity.this.roamingDownloadRow))
          return 3;
        if ((paramInt == DataSettingsActivity.this.mediaDownloadSectionRow) || (paramInt == DataSettingsActivity.this.callsSectionRow))
          break;
        i = j;
      }
      while (paramInt != DataSettingsActivity.this.usageSectionRow);
      return 2;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == DataSettingsActivity.this.wifiDownloadRow) || (i == DataSettingsActivity.this.mobileDownloadRow) || (i == DataSettingsActivity.this.roamingDownloadRow) || (i == DataSettingsActivity.this.storageUsageRow) || (i == DataSettingsActivity.this.useLessDataForCallsRow) || (i == DataSettingsActivity.this.mobileUsageRow) || (i == DataSettingsActivity.this.roamingUsageRow) || (i == DataSettingsActivity.this.wifiUsageRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      case 2:
      case 3:
      }
      Object localObject1;
      Object localObject2;
      TextDetailSettingsCell localTextDetailSettingsCell;
      do
      {
        do
        {
          do
          {
            return;
            if ((paramInt == DataSettingsActivity.this.callsSection2Row) || ((paramInt == DataSettingsActivity.this.usageSection2Row) && (DataSettingsActivity.this.usageSection2Row == -1)))
            {
              paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
              return;
            }
            paramViewHolder.itemView.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
            return;
            localObject1 = (TextSettingsCell)paramViewHolder.itemView;
            if (paramInt == DataSettingsActivity.this.storageUsageRow)
            {
              ((TextSettingsCell)localObject1).setText(LocaleController.getString("StorageUsage", 2131166497), true);
              return;
            }
            if (paramInt == DataSettingsActivity.this.useLessDataForCallsRow)
            {
              localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
              paramViewHolder = null;
              switch (((SharedPreferences)localObject2).getInt("VoipDataSaving", 0))
              {
              default:
              case 0:
              case 1:
              case 2:
              }
              while (true)
              {
                ((TextSettingsCell)localObject1).setTextAndValue(LocaleController.getString("VoipUseLessData", 2131166607), paramViewHolder, false);
                return;
                paramViewHolder = LocaleController.getString("UseLessDataNever", 2131166546);
                continue;
                paramViewHolder = LocaleController.getString("UseLessDataOnMobile", 2131166547);
                continue;
                paramViewHolder = LocaleController.getString("UseLessDataAlways", 2131166545);
              }
            }
            if (paramInt == DataSettingsActivity.this.mobileUsageRow)
            {
              ((TextSettingsCell)localObject1).setText(LocaleController.getString("MobileUsage", 2131165988), true);
              return;
            }
            if (paramInt != DataSettingsActivity.this.roamingUsageRow)
              continue;
            ((TextSettingsCell)localObject1).setText(LocaleController.getString("RoamingUsage", 2131166367), false);
            return;
          }
          while (paramInt != DataSettingsActivity.this.wifiUsageRow);
          ((TextSettingsCell)localObject1).setText(LocaleController.getString("WiFiUsage", 2131166627), true);
          return;
          paramViewHolder = (HeaderCell)paramViewHolder.itemView;
          if (paramInt == DataSettingsActivity.this.mediaDownloadSectionRow)
          {
            paramViewHolder.setText(LocaleController.getString("AutomaticMediaDownload", 2131165377));
            return;
          }
          if (paramInt != DataSettingsActivity.this.usageSectionRow)
            continue;
          paramViewHolder.setText(LocaleController.getString("DataUsage", 2131165610));
          return;
        }
        while (paramInt != DataSettingsActivity.this.callsSectionRow);
        paramViewHolder.setText(LocaleController.getString("Calls", 2131165424));
        return;
        localTextDetailSettingsCell = (TextDetailSettingsCell)paramViewHolder.itemView;
      }
      while ((paramInt != DataSettingsActivity.this.mobileDownloadRow) && (paramInt != DataSettingsActivity.this.wifiDownloadRow) && (paramInt != DataSettingsActivity.this.roamingDownloadRow));
      ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
      if (paramInt == DataSettingsActivity.this.mobileDownloadRow)
      {
        localObject2 = LocaleController.getString("WhenUsingMobileData", 2131166619);
        paramInt = MediaController.getInstance().mobileDataDownloadMask;
        localObject1 = "";
        if ((paramInt & 0x1) != 0)
          localObject1 = "" + LocaleController.getString("LocalPhotoCache", 2131165929);
        paramViewHolder = (RecyclerView.ViewHolder)localObject1;
        if ((paramInt & 0x2) != 0)
        {
          paramViewHolder = (RecyclerView.ViewHolder)localObject1;
          if (((String)localObject1).length() != 0)
            paramViewHolder = (String)localObject1 + ", ";
          paramViewHolder = paramViewHolder + LocaleController.getString("LocalAudioCache", 2131165921);
        }
        localObject1 = paramViewHolder;
        if ((paramInt & 0x4) != 0)
        {
          localObject1 = paramViewHolder;
          if (paramViewHolder.length() != 0)
            localObject1 = paramViewHolder + ", ";
          localObject1 = (String)localObject1 + LocaleController.getString("LocalVideoCache", 2131165930);
        }
        paramViewHolder = (RecyclerView.ViewHolder)localObject1;
        if ((paramInt & 0x8) != 0)
        {
          paramViewHolder = (RecyclerView.ViewHolder)localObject1;
          if (((String)localObject1).length() != 0)
            paramViewHolder = (String)localObject1 + ", ";
          paramViewHolder = paramViewHolder + LocaleController.getString("FilesDataUsage", 2131165710);
        }
        localObject1 = paramViewHolder;
        if ((paramInt & 0x10) != 0)
        {
          localObject1 = paramViewHolder;
          if (paramViewHolder.length() != 0)
            localObject1 = paramViewHolder + ", ";
          localObject1 = (String)localObject1 + LocaleController.getString("AttachMusic", 2131165366);
        }
        paramViewHolder = (RecyclerView.ViewHolder)localObject1;
        if ((paramInt & 0x20) != 0)
          if (((String)localObject1).length() == 0)
            break label897;
      }
      label897: for (paramViewHolder = (String)localObject1 + ", "; ; paramViewHolder = (RecyclerView.ViewHolder)localObject1)
      {
        paramViewHolder = paramViewHolder + LocaleController.getString("LocalGifCache", 2131165927);
        localObject1 = paramViewHolder;
        if (paramViewHolder.length() == 0)
          localObject1 = LocaleController.getString("NoMediaAutoDownload", 2131166034);
        localTextDetailSettingsCell.setTextAndValue((String)localObject2, (String)localObject1, true);
        return;
        if (paramInt == DataSettingsActivity.this.wifiDownloadRow)
        {
          localObject2 = LocaleController.getString("WhenConnectedOnWiFi", 2131166617);
          paramInt = MediaController.getInstance().wifiDownloadMask;
          break;
        }
        localObject2 = LocaleController.getString("WhenRoaming", 2131166618);
        paramInt = MediaController.getInstance().roamingDownloadMask;
        break;
      }
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
        paramViewGroup = new TextDetailSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DataSettingsActivity
 * JD-Core Version:    0.6.0
 */