package org.vidogram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import itman.Vidofilm.b;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.LocaleController.LocaleInfo;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class LanguageSelectActivity extends BaseFragment
{
  private EmptyTextProgressView emptyView;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private ListAdapter searchListViewAdapter;
  public ArrayList<LocaleController.LocaleInfo> searchResult;
  private Timer searchTimer;
  private boolean searchWas;
  private boolean searching;

  private void processSearch(String paramString)
  {
    Utilities.searchQueue.postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        if (this.val$query.trim().toLowerCase().length() == 0)
        {
          LanguageSelectActivity.this.updateSearchResults(new ArrayList());
          return;
        }
        System.currentTimeMillis();
        ArrayList localArrayList = new ArrayList();
        Iterator localIterator = LocaleController.getInstance().sortedLanguages.iterator();
        while (localIterator.hasNext())
        {
          LocaleController.LocaleInfo localLocaleInfo = (LocaleController.LocaleInfo)localIterator.next();
          if ((!localLocaleInfo.name.toLowerCase().startsWith(this.val$query)) && (!localLocaleInfo.nameEnglish.toLowerCase().startsWith(this.val$query)))
            continue;
          localArrayList.add(localLocaleInfo);
        }
        LanguageSelectActivity.this.updateSearchResults(localArrayList);
      }
    });
  }

  private void updateSearchResults(ArrayList<LocaleController.LocaleInfo> paramArrayList)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList)
    {
      public void run()
      {
        LanguageSelectActivity.this.searchResult = this.val$arrCounties;
        LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
      }
    });
  }

  public View createView(Context paramContext)
  {
    this.searching = false;
    this.searchWas = false;
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("Language", 2131165870));
    if (b.a(paramContext).d())
    {
      this.actionBar.setBackButtonImage(2130837732);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            LanguageSelectActivity.this.finishFragment();
        }
      });
    }
    this.actionBar.createMenu().addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
    {
      public void onSearchCollapse()
      {
        LanguageSelectActivity.this.search(null);
        LanguageSelectActivity.access$002(LanguageSelectActivity.this, false);
        LanguageSelectActivity.access$102(LanguageSelectActivity.this, false);
        if (LanguageSelectActivity.this.listView != null)
        {
          LanguageSelectActivity.this.emptyView.setVisibility(8);
          LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.listAdapter);
        }
      }

      public void onSearchExpand()
      {
        LanguageSelectActivity.access$002(LanguageSelectActivity.this, true);
      }

      public void onTextChanged(EditText paramEditText)
      {
        paramEditText = paramEditText.getText().toString();
        LanguageSelectActivity.this.search(paramEditText);
        if (paramEditText.length() != 0)
        {
          LanguageSelectActivity.access$102(LanguageSelectActivity.this, true);
          if (LanguageSelectActivity.this.listView != null)
            LanguageSelectActivity.this.listView.setAdapter(LanguageSelectActivity.this.searchListViewAdapter);
        }
      }
    }).getSearchField().setHint(LocaleController.getString("Search", 2131166381));
    this.listAdapter = new ListAdapter(paramContext, false);
    this.searchListViewAdapter = new ListAdapter(paramContext, true);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
    this.emptyView.showTextView();
    this.emptyView.setShowAtCenter(true);
    localFrameLayout.addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.emptyView);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setAdapter(this.listAdapter);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener(paramContext)
    {
      public void onItemClick(View paramView, int paramInt)
      {
        Object localObject = null;
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          paramView = localObject;
          if (paramInt >= 0)
          {
            paramView = localObject;
            if (paramInt < LanguageSelectActivity.this.searchResult.size())
              paramView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramInt);
          }
        }
        while (true)
        {
          if ((LanguageSelectActivity.this.parentLayout != null) && (paramView != null));
          try
          {
            LocaleController.getInstance().applyLanguage(paramView, true);
            LanguageSelectActivity.this.parentLayout.rebuildAllFragmentViews(false);
            label92: if (b.a(this.val$context).d())
            {
              LanguageSelectActivity.this.finishFragment();
              return;
              paramView = localObject;
              if (paramInt < 0)
                continue;
              paramView = localObject;
              if (paramInt >= LocaleController.getInstance().sortedLanguages.size())
                continue;
              paramView = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramInt);
              continue;
            }
            try
            {
              b.a(this.val$context).b(true);
              LanguageSelectActivity.this.removeSelfFromStack();
              ((Activity)this.val$context).finish();
              paramView = new Intent(this.val$context, LaunchActivity.class);
              this.val$context.startActivity(paramView);
              return;
            }
            catch (Exception paramView)
            {
              LanguageSelectActivity.this.finishFragment();
              return;
            }
          }
          catch (Exception paramView)
          {
            break label92;
          }
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      public boolean onItemClick(View paramView, int paramInt)
      {
        if ((LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
        {
          if ((paramInt < 0) || (paramInt >= LanguageSelectActivity.this.searchResult.size()))
            break label197;
          paramView = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramInt);
        }
        while (true)
        {
          if ((paramView == null) || (paramView.pathToFile == null) || (LanguageSelectActivity.this.getParentActivity() == null))
          {
            return false;
            if ((paramInt >= 0) && (paramInt < LocaleController.getInstance().sortedLanguages.size()))
            {
              paramView = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramInt);
              continue;
            }
          }
          else
          {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(LanguageSelectActivity.this.getParentActivity());
            localBuilder.setMessage(LocaleController.getString("DeleteLocalization", 2131165643));
            localBuilder.setTitle(LocaleController.getString("AppName", 2131165319));
            localBuilder.setPositiveButton(LocaleController.getString("Delete", 2131165628), new DialogInterface.OnClickListener(paramView)
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                if (LocaleController.getInstance().deleteLanguage(this.val$finalLocaleInfo))
                {
                  if (LanguageSelectActivity.this.searchResult != null)
                    LanguageSelectActivity.this.searchResult.remove(this.val$finalLocaleInfo);
                  if (LanguageSelectActivity.this.listAdapter != null)
                    LanguageSelectActivity.this.listAdapter.notifyDataSetChanged();
                  if (LanguageSelectActivity.this.searchListViewAdapter != null)
                    LanguageSelectActivity.this.searchListViewAdapter.notifyDataSetChanged();
                }
              }
            });
            localBuilder.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
            LanguageSelectActivity.this.showDialog(localBuilder.create());
            return true;
          }
          label197: paramView = null;
        }
      }
    });
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
      {
        if ((paramInt == 1) && (LanguageSelectActivity.this.searching) && (LanguageSelectActivity.this.searchWas))
          AndroidUtilities.hideKeyboard(LanguageSelectActivity.this.getParentActivity().getCurrentFocus());
      }
    });
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, "actionBarDefaultSearch");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, "actionBarDefaultSearchPlaceholder");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText") };
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
  }

  public void search(String paramString)
  {
    if (paramString == null)
    {
      this.searchResult = null;
      return;
    }
    try
    {
      if (this.searchTimer != null)
        this.searchTimer.cancel();
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask(paramString)
      {
        public void run()
        {
          try
          {
            LanguageSelectActivity.this.searchTimer.cancel();
            LanguageSelectActivity.access$802(LanguageSelectActivity.this, null);
            LanguageSelectActivity.this.processSearch(this.val$query);
            return;
          }
          catch (Exception localException)
          {
            while (true)
              FileLog.e(localException);
          }
        }
      }
      , 100L, 300L);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;
    private boolean search;

    public ListAdapter(Context paramBoolean, boolean arg3)
    {
      this.mContext = paramBoolean;
      boolean bool;
      this.search = bool;
    }

    public int getItemCount()
    {
      if (this.search)
        if (LanguageSelectActivity.this.searchResult != null);
      do
      {
        return 0;
        return LanguageSelectActivity.this.searchResult.size();
      }
      while (LocaleController.getInstance().sortedLanguages == null);
      return LocaleController.getInstance().sortedLanguages.size();
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool = true;
      TextSettingsCell localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
      if (this.search)
      {
        paramViewHolder = (LocaleController.LocaleInfo)LanguageSelectActivity.this.searchResult.get(paramInt);
        if (paramInt == LanguageSelectActivity.this.searchResult.size() - 1)
        {
          paramInt = 1;
          paramViewHolder = paramViewHolder.name;
          if (paramInt != 0)
            break label112;
        }
      }
      while (true)
      {
        localTextSettingsCell.setText(paramViewHolder, bool);
        return;
        paramInt = 0;
        break;
        paramViewHolder = (LocaleController.LocaleInfo)LocaleController.getInstance().sortedLanguages.get(paramInt);
        if (paramInt == LocaleController.getInstance().sortedLanguages.size() - 1)
        {
          paramInt = 1;
          break;
        }
        paramInt = 0;
        break;
        label112: bool = false;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new TextSettingsCell(this.mContext));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.LanguageSelectActivity
 * JD-Core Version:    0.6.0
 */