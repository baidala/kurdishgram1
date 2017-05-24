package org.vidogram.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_account_getWallPapers;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_wallPaper;
import org.vidogram.tgnet.TLRPC.TL_wallPaperSolid;
import org.vidogram.tgnet.TLRPC.Vector;
import org.vidogram.tgnet.TLRPC.WallPaper;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.WallpaperCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;
import org.vidogram.ui.Components.WallpaperUpdater;
import org.vidogram.ui.Components.WallpaperUpdater.WallpaperUpdaterDelegate;

public class WallpapersActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private static final int done_button = 1;
  private ImageView backgroundImage;
  private View doneButton;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private String loadingFile = null;
  private File loadingFileObject = null;
  private TLRPC.PhotoSize loadingSize = null;
  private boolean overrideThemeWallpaper;
  private RadialProgressView progressBar;
  private FrameLayout progressView;
  private View progressViewBackground;
  private int selectedBackground;
  private int selectedColor;
  private Drawable themedWallpaper;
  private WallpaperUpdater updater;
  private ArrayList<TLRPC.WallPaper> wallPapers = new ArrayList();
  private File wallpaperFile;
  private HashMap<Integer, TLRPC.WallPaper> wallpappersByIds = new HashMap();

  private void loadWallpapers()
  {
    TLRPC.TL_account_getWallPapers localTL_account_getWallPapers = new TLRPC.TL_account_getWallPapers();
    int i = ConnectionsManager.getInstance().sendRequest(localTL_account_getWallPapers, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        if (paramTL_error != null)
          return;
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
        {
          public void run()
          {
            WallpapersActivity.this.wallPapers.clear();
            Object localObject1 = (TLRPC.Vector)this.val$response;
            WallpapersActivity.this.wallpappersByIds.clear();
            localObject1 = ((TLRPC.Vector)localObject1).objects.iterator();
            while (((Iterator)localObject1).hasNext())
            {
              Object localObject2 = ((Iterator)localObject1).next();
              WallpapersActivity.this.wallPapers.add((TLRPC.WallPaper)localObject2);
              WallpapersActivity.this.wallpappersByIds.put(Integer.valueOf(((TLRPC.WallPaper)localObject2).id), (TLRPC.WallPaper)localObject2);
            }
            if (WallpapersActivity.this.listAdapter != null)
              WallpapersActivity.this.listAdapter.notifyDataSetChanged();
            if (WallpapersActivity.this.backgroundImage != null)
              WallpapersActivity.this.processSelectedBackground();
            MessagesStorage.getInstance().putWallpapers(WallpapersActivity.this.wallPapers);
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(i, this.classGuid);
  }

  private void processSelectedBackground()
  {
    if ((Theme.hasWallpaperFromTheme()) && (!this.overrideThemeWallpaper))
    {
      this.backgroundImage.setImageDrawable(Theme.getThemedWallpaper(false));
      return;
    }
    Object localObject = (TLRPC.WallPaper)this.wallpappersByIds.get(Integer.valueOf(this.selectedBackground));
    int i;
    int j;
    int k;
    if ((this.selectedBackground != -1) && (this.selectedBackground != 1000001) && (localObject != null) && ((localObject instanceof TLRPC.TL_wallPaper)))
    {
      i = AndroidUtilities.displaySize.x;
      j = AndroidUtilities.displaySize.y;
      if (i <= j)
        break label584;
      k = j;
      j = i;
    }
    while (true)
    {
      localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.WallPaper)localObject).sizes, Math.min(k, j));
      if (localObject == null)
        break;
      String str = ((TLRPC.PhotoSize)localObject).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject).location.local_id + ".jpg";
      File localFile2 = new File(FileLoader.getInstance().getDirectory(4), str);
      if (!localFile2.exists())
      {
        int[] arrayOfInt = AndroidUtilities.calcDrawableColor(this.backgroundImage.getDrawable());
        this.progressViewBackground.getBackground().setColorFilter(new PorterDuffColorFilter(arrayOfInt[0], PorterDuff.Mode.MULTIPLY));
        this.loadingFile = str;
        this.loadingFileObject = localFile2;
        this.doneButton.setEnabled(false);
        this.progressView.setVisibility(0);
        this.loadingSize = ((TLRPC.PhotoSize)localObject);
        this.selectedColor = 0;
        FileLoader.getInstance().loadFile((TLRPC.PhotoSize)localObject, null, true);
        this.backgroundImage.setBackgroundColor(0);
        return;
      }
      if (this.loadingFile != null)
        FileLoader.getInstance().cancelLoadFile(this.loadingSize);
      this.loadingFileObject = null;
      this.loadingFile = null;
      this.loadingSize = null;
      try
      {
        this.backgroundImage.setImageURI(Uri.fromFile(localFile2));
        this.backgroundImage.setBackgroundColor(0);
        this.selectedColor = 0;
        this.doneButton.setEnabled(true);
        this.progressView.setVisibility(8);
        return;
      }
      catch (Throwable localThrowable)
      {
        while (true)
          FileLog.e(localThrowable);
      }
      if (this.loadingFile != null)
        FileLoader.getInstance().cancelLoadFile(this.loadingSize);
      if (this.selectedBackground == 1000001)
      {
        this.backgroundImage.setImageResource(2130837623);
        this.backgroundImage.setBackgroundColor(0);
        this.selectedColor = 0;
      }
      while (true)
      {
        this.loadingFileObject = null;
        this.loadingFile = null;
        this.loadingSize = null;
        this.doneButton.setEnabled(true);
        this.progressView.setVisibility(8);
        return;
        File localFile1;
        if (this.selectedBackground == -1)
        {
          if (this.wallpaperFile != null);
          for (localFile1 = this.wallpaperFile; ; localFile1 = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg"))
          {
            if (!localFile1.exists())
              break label510;
            this.backgroundImage.setImageURI(Uri.fromFile(localFile1));
            break;
          }
          label510: this.selectedBackground = 1000001;
          this.overrideThemeWallpaper = true;
          processSelectedBackground();
          continue;
        }
        if (localFile1 == null)
          break;
        if (!(localFile1 instanceof TLRPC.TL_wallPaperSolid))
          continue;
        this.backgroundImage.getDrawable();
        this.backgroundImage.setImageBitmap(null);
        this.selectedColor = (localFile1.bg_color | 0xFF000000);
        this.backgroundImage.setBackgroundColor(this.selectedColor);
      }
      label584: k = i;
    }
  }

  public View createView(Context paramContext)
  {
    this.themedWallpaper = Theme.getThemedWallpaper(true);
    this.updater = new WallpaperUpdater(getParentActivity(), new WallpaperUpdater.WallpaperUpdaterDelegate()
    {
      public void didSelectWallpaper(File paramFile, Bitmap paramBitmap)
      {
        WallpapersActivity.access$002(WallpapersActivity.this, -1);
        WallpapersActivity.access$102(WallpapersActivity.this, true);
        WallpapersActivity.access$202(WallpapersActivity.this, 0);
        WallpapersActivity.access$302(WallpapersActivity.this, paramFile);
        WallpapersActivity.this.backgroundImage.getDrawable();
        WallpapersActivity.this.backgroundImage.setImageBitmap(paramBitmap);
      }

      public void needOpenColorPicker()
      {
      }
    });
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("ChatBackground", 2131165530));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        boolean bool2 = false;
        if (paramInt == -1)
          WallpapersActivity.this.finishFragment();
        do
          return;
        while (paramInt != 1);
        Object localObject = (TLRPC.WallPaper)WallpapersActivity.this.wallpappersByIds.get(Integer.valueOf(WallpapersActivity.this.selectedBackground));
        int i;
        int j;
        if ((localObject != null) && (((TLRPC.WallPaper)localObject).id != 1000001) && ((localObject instanceof TLRPC.TL_wallPaper)))
        {
          paramInt = AndroidUtilities.displaySize.x;
          i = AndroidUtilities.displaySize.y;
          if (paramInt <= i)
            break label383;
          j = i;
          i = paramInt;
        }
        while (true)
        {
          localObject = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.WallPaper)localObject).sizes, Math.min(j, i));
          localObject = ((TLRPC.PhotoSize)localObject).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject).location.local_id + ".jpg";
          localObject = new File(FileLoader.getInstance().getDirectory(4), (String)localObject);
          File localFile2 = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
          while (true)
          {
            try
            {
              bool1 = AndroidUtilities.copyFile((File)localObject, localFile2);
              if (!bool1)
                continue;
              localObject = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
              ((SharedPreferences.Editor)localObject).putInt("selectedBackground", WallpapersActivity.this.selectedBackground);
              ((SharedPreferences.Editor)localObject).putInt("selectedColor", WallpapersActivity.this.selectedColor);
              bool1 = bool2;
              if (!Theme.hasWallpaperFromTheme())
                continue;
              bool1 = bool2;
              if (!WallpapersActivity.this.overrideThemeWallpaper)
                continue;
              bool1 = true;
              ((SharedPreferences.Editor)localObject).putBoolean("overrideThemeWallpaper", bool1);
              ((SharedPreferences.Editor)localObject).commit();
              Theme.reloadWallpaper();
              WallpapersActivity.this.finishFragment();
              return;
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
            }
            while (true)
              while (true)
              {
                bool1 = false;
                break;
                if (WallpapersActivity.this.selectedBackground != -1)
                  break label377;
                File localFile1 = WallpapersActivity.this.updater.getCurrentWallpaperPath();
                localFile2 = new File(ApplicationLoader.getFilesDirFixed(), "wallpaper.jpg");
                try
                {
                  bool1 = AndroidUtilities.copyFile(localFile1, localFile2);
                }
                catch (Exception localException2)
                {
                  FileLog.e(localException2);
                }
              }
            label377: boolean bool1 = true;
          }
          label383: j = paramInt;
        }
      }
    });
    this.doneButton = this.actionBar.createMenu().addItemWithWidth(1, 2130837768, AndroidUtilities.dp(56.0F));
    FrameLayout localFrameLayout = new FrameLayout(paramContext);
    this.fragmentView = localFrameLayout;
    this.backgroundImage = new ImageView(paramContext);
    this.backgroundImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
    localFrameLayout.addView(this.backgroundImage, LayoutHelper.createFrame(-1, -1.0F));
    this.backgroundImage.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    this.progressView = new FrameLayout(paramContext);
    this.progressView.setVisibility(4);
    localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 52.0F));
    this.progressViewBackground = new View(paramContext);
    this.progressViewBackground.setBackgroundResource(2130838082);
    this.progressView.addView(this.progressViewBackground, LayoutHelper.createFrame(36, 36, 17));
    this.progressBar = new RadialProgressView(paramContext);
    this.progressBar.setSize(AndroidUtilities.dp(28.0F));
    this.progressBar.setProgressColor(-1);
    this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setClipToPadding(false);
    this.listView.setTag(Integer.valueOf(8));
    this.listView.setPadding(AndroidUtilities.dp(40.0F), 0, AndroidUtilities.dp(40.0F), 0);
    Object localObject = new LinearLayoutManager(paramContext);
    ((LinearLayoutManager)localObject).setOrientation(0);
    this.listView.setLayoutManager((RecyclerView.LayoutManager)localObject);
    this.listView.setDisallowInterceptTouchEvents(true);
    this.listView.setOverScrollMode(2);
    localObject = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listAdapter = paramContext;
    ((RecyclerListView)localObject).setAdapter(paramContext);
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, 102, 83));
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (paramInt == 0)
        {
          WallpapersActivity.this.updater.showAlert(false);
          return;
        }
        if (Theme.hasWallpaperFromTheme())
        {
          if (paramInt == 1)
          {
            WallpapersActivity.access$002(WallpapersActivity.this, -2);
            WallpapersActivity.access$102(WallpapersActivity.this, false);
            WallpapersActivity.this.listAdapter.notifyDataSetChanged();
            WallpapersActivity.this.processSelectedBackground();
            return;
          }
          paramInt -= 2;
        }
        while (true)
        {
          paramView = (TLRPC.WallPaper)WallpapersActivity.this.wallPapers.get(paramInt);
          WallpapersActivity.access$002(WallpapersActivity.this, paramView.id);
          WallpapersActivity.access$102(WallpapersActivity.this, true);
          WallpapersActivity.this.listAdapter.notifyDataSetChanged();
          WallpapersActivity.this.processSelectedBackground();
          return;
          paramInt -= 1;
        }
      }
    });
    processSelectedBackground();
    return (View)this.fragmentView;
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.FileDidFailedLoad)
    {
      paramArrayOfObject = (String)paramArrayOfObject[0];
      if ((this.loadingFile != null) && (this.loadingFile.equals(paramArrayOfObject)))
      {
        this.loadingFileObject = null;
        this.loadingFile = null;
        this.loadingSize = null;
        this.progressView.setVisibility(8);
        this.doneButton.setEnabled(false);
      }
    }
    do
      while (true)
      {
        return;
        if (paramInt != NotificationCenter.FileDidLoaded)
          break;
        paramArrayOfObject = (String)paramArrayOfObject[0];
        if ((this.loadingFile == null) || (!this.loadingFile.equals(paramArrayOfObject)))
          continue;
        this.backgroundImage.setImageURI(Uri.fromFile(this.loadingFileObject));
        this.progressView.setVisibility(8);
        this.backgroundImage.setBackgroundColor(0);
        this.doneButton.setEnabled(true);
        this.loadingFileObject = null;
        this.loadingFile = null;
        this.loadingSize = null;
        return;
      }
    while (paramInt != NotificationCenter.wallpapersDidLoaded);
    this.wallPapers = ((ArrayList)paramArrayOfObject[0]);
    this.wallpappersByIds.clear();
    paramArrayOfObject = this.wallPapers.iterator();
    while (paramArrayOfObject.hasNext())
    {
      TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)paramArrayOfObject.next();
      this.wallpappersByIds.put(Integer.valueOf(localWallPaper.id), localWallPaper);
    }
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    if ((!this.wallPapers.isEmpty()) && (this.backgroundImage != null))
      processSelectedBackground();
    loadWallpapers();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21") };
  }

  public void onActivityResultFragment(int paramInt1, int paramInt2, Intent paramIntent)
  {
    this.updater.onActivityResult(paramInt1, paramInt2, paramIntent);
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.wallpapersDidLoaded);
    SharedPreferences localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
    this.selectedBackground = localSharedPreferences.getInt("selectedBackground", 1000001);
    this.overrideThemeWallpaper = localSharedPreferences.getBoolean("overrideThemeWallpaper", false);
    this.selectedColor = localSharedPreferences.getInt("selectedColor", 0);
    MessagesStorage.getInstance().getWallpapers();
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    this.updater.cleanup();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.wallpapersDidLoaded);
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    processSelectedBackground();
  }

  public void restoreSelfArgs(Bundle paramBundle)
  {
    this.updater.setCurrentPicturePath(paramBundle.getString("path"));
  }

  public void saveSelfArgs(Bundle paramBundle)
  {
    String str = this.updater.getCurrentPicturePath();
    if (str != null)
      paramBundle.putString("path", str);
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
      int j = WallpapersActivity.this.wallPapers.size() + 1;
      int i = j;
      if (Theme.hasWallpaperFromTheme())
        i = j + 1;
      return i;
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int i = -2;
      paramViewHolder = (WallpaperCell)paramViewHolder.itemView;
      if (paramInt == 0)
      {
        if ((!Theme.hasWallpaperFromTheme()) || (WallpapersActivity.this.overrideThemeWallpaper));
        for (paramInt = WallpapersActivity.this.selectedBackground; ; paramInt = -2)
        {
          paramViewHolder.setWallpaper(null, paramInt, null, false);
          return;
        }
      }
      if (Theme.hasWallpaperFromTheme())
      {
        if (paramInt == 1)
        {
          if (WallpapersActivity.this.overrideThemeWallpaper)
            i = -1;
          paramViewHolder.setWallpaper(null, i, WallpapersActivity.this.themedWallpaper, true);
          return;
        }
        paramInt -= 2;
      }
      while (true)
      {
        TLRPC.WallPaper localWallPaper = (TLRPC.WallPaper)WallpapersActivity.this.wallPapers.get(paramInt);
        if ((!Theme.hasWallpaperFromTheme()) || (WallpapersActivity.this.overrideThemeWallpaper))
          i = WallpapersActivity.this.selectedBackground;
        paramViewHolder.setWallpaper(localWallPaper, i, null, false);
        return;
        paramInt -= 1;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      return new RecyclerListView.Holder(new WallpaperCell(this.mContext));
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.WallpapersActivity
 * JD-Core Version:    0.6.0
 */