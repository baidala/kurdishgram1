package org.vidogram.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Environment;
import android.os.StatFs;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BackDrawable;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.GraySectionCell;
import org.vidogram.ui.Cells.SharedDocumentCell;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.NumberTextView;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class DocumentSelectActivity extends BaseFragment
{
  private static final int done = 3;
  private ArrayList<View> actionModeViews = new ArrayList();
  private File currentDir;
  private DocumentSelectActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private ArrayList<HistoryEntry> history = new ArrayList();
  private ArrayList<ListItem> items = new ArrayList();
  private LinearLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private BroadcastReceiver receiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramContext, Intent paramIntent)
    {
      paramContext = new Runnable()
      {
        public void run()
        {
          try
          {
            if (DocumentSelectActivity.this.currentDir == null)
            {
              DocumentSelectActivity.this.listRoots();
              return;
            }
            DocumentSelectActivity.this.listFiles(DocumentSelectActivity.this.currentDir);
            return;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
          }
        }
      };
      if ("android.intent.action.MEDIA_UNMOUNTED".equals(paramIntent.getAction()))
      {
        DocumentSelectActivity.this.listView.postDelayed(paramContext, 1000L);
        return;
      }
      paramContext.run();
    }
  };
  private boolean receiverRegistered = false;
  private ArrayList<ListItem> recentItems = new ArrayList();
  private boolean scrolling;
  private HashMap<String, ListItem> selectedFiles = new HashMap();
  private NumberTextView selectedMessagesCountTextView;
  private long sizeLimit = 1610612736L;

  private void fixLayoutInternal()
  {
    if (this.selectedMessagesCountTextView == null)
      return;
    if ((!AndroidUtilities.isTablet()) && (ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 2))
    {
      this.selectedMessagesCountTextView.setTextSize(18);
      return;
    }
    this.selectedMessagesCountTextView.setTextSize(20);
  }

  private String getRootSubtitle(String paramString)
  {
    try
    {
      Object localObject = new StatFs(paramString);
      long l1 = ((StatFs)localObject).getBlockCount() * ((StatFs)localObject).getBlockSize();
      long l2 = ((StatFs)localObject).getAvailableBlocks();
      long l3 = ((StatFs)localObject).getBlockSize();
      if (l1 == 0L)
        return "";
      localObject = LocaleController.formatString("FreeOfTotal", 2131165777, new Object[] { AndroidUtilities.formatFileSize(l3 * l2), AndroidUtilities.formatFileSize(l1) });
      return localObject;
    }
    catch (Exception localException)
    {
      FileLog.e(localException);
    }
    return (String)paramString;
  }

  private boolean listFiles(File paramFile)
  {
    if (!paramFile.canRead())
    {
      if (((paramFile.getAbsolutePath().startsWith(Environment.getExternalStorageDirectory().toString())) || (paramFile.getAbsolutePath().startsWith("/sdcard")) || (paramFile.getAbsolutePath().startsWith("/mnt/sdcard"))) && (!Environment.getExternalStorageState().equals("mounted")) && (!Environment.getExternalStorageState().equals("mounted_ro")))
      {
        this.currentDir = paramFile;
        this.items.clear();
        if ("shared".equals(Environment.getExternalStorageState()))
          this.emptyView.setText(LocaleController.getString("UsbActive", 2131166544));
        while (true)
        {
          AndroidUtilities.clearDrawableAnimation(this.listView);
          this.scrolling = true;
          this.listAdapter.notifyDataSetChanged();
          return true;
          this.emptyView.setText(LocaleController.getString("NotMounted", 2131166061));
        }
      }
      showErrorBox(LocaleController.getString("AccessError", 2131165224));
      return false;
    }
    this.emptyView.setText(LocaleController.getString("NoFiles", 2131166028));
    Object localObject1;
    try
    {
      localObject1 = paramFile.listFiles();
      if (localObject1 == null)
      {
        showErrorBox(LocaleController.getString("UnknownError", 2131166533));
        return false;
      }
    }
    catch (Exception paramFile)
    {
      showErrorBox(paramFile.getLocalizedMessage());
      return false;
    }
    this.currentDir = paramFile;
    this.items.clear();
    Arrays.sort(localObject1, new Comparator()
    {
      public int compare(File paramFile1, File paramFile2)
      {
        if (paramFile1.isDirectory() != paramFile2.isDirectory())
        {
          if (paramFile1.isDirectory())
            return -1;
          return 1;
        }
        return paramFile1.getName().compareToIgnoreCase(paramFile2.getName());
      }
    });
    int i = 0;
    if (i < localObject1.length)
    {
      Object localObject2 = localObject1[i];
      if (localObject2.getName().indexOf('.') == 0);
      ListItem localListItem;
      while (true)
      {
        i += 1;
        break;
        localListItem = new ListItem(null);
        localListItem.title = localObject2.getName();
        localListItem.file = localObject2;
        if (!localObject2.isDirectory())
          break label347;
        localListItem.icon = 2130837767;
        localListItem.subtitle = LocaleController.getString("Folder", 2131165717);
        this.items.add(localListItem);
      }
      label347: String str = localObject2.getName();
      paramFile = str.split("\\.");
      if (paramFile.length > 1);
      for (paramFile = paramFile[(paramFile.length - 1)]; ; paramFile = "?")
      {
        localListItem.ext = paramFile;
        localListItem.subtitle = AndroidUtilities.formatFileSize(localObject2.length());
        paramFile = str.toLowerCase();
        if ((!paramFile.endsWith(".jpg")) && (!paramFile.endsWith(".png")) && (!paramFile.endsWith(".gif")) && (!paramFile.endsWith(".jpeg")))
          break;
        localListItem.thumb = localObject2.getAbsolutePath();
        break;
      }
    }
    paramFile = new ListItem(null);
    paramFile.title = "..";
    if (this.history.size() > 0)
    {
      localObject1 = (HistoryEntry)this.history.get(this.history.size() - 1);
      if (((HistoryEntry)localObject1).dir == null)
        paramFile.subtitle = LocaleController.getString("Folder", 2131165717);
    }
    while (true)
    {
      paramFile.icon = 2130837767;
      paramFile.file = null;
      this.items.add(0, paramFile);
      AndroidUtilities.clearDrawableAnimation(this.listView);
      this.scrolling = true;
      this.listAdapter.notifyDataSetChanged();
      return true;
      paramFile.subtitle = ((HistoryEntry)localObject1).dir.toString();
      continue;
      paramFile.subtitle = LocaleController.getString("Folder", 2131165717);
    }
  }

  // ERROR //
  @android.annotation.SuppressLint({"NewApi"})
  private void listRoots()
  {
    // Byte code:
    //   0: aload_0
    //   1: aconst_null
    //   2: putfield 107	org/vidogram/ui/DocumentSelectActivity:currentDir	Ljava/io/File;
    //   5: aload_0
    //   6: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   9: invokevirtual 295	java/util/ArrayList:clear	()V
    //   12: new 428	java/util/HashSet
    //   15: dup
    //   16: invokespecial 429	java/util/HashSet:<init>	()V
    //   19: astore 6
    //   21: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   24: invokevirtual 432	java/io/File:getPath	()Ljava/lang/String;
    //   27: astore_2
    //   28: invokestatic 435	android/os/Environment:isExternalStorageRemovable	()Z
    //   31: pop
    //   32: invokestatic 284	android/os/Environment:getExternalStorageState	()Ljava/lang/String;
    //   35: astore_3
    //   36: aload_3
    //   37: ldc_w 286
    //   40: invokevirtual 290	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   43: ifne +13 -> 56
    //   46: aload_3
    //   47: ldc_w 292
    //   50: invokevirtual 290	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   53: ifeq +71 -> 124
    //   56: new 35	org/vidogram/ui/DocumentSelectActivity$ListItem
    //   59: dup
    //   60: aload_0
    //   61: aconst_null
    //   62: invokespecial 353	org/vidogram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/vidogram/ui/DocumentSelectActivity;Lorg/vidogram/ui/DocumentSelectActivity$1;)V
    //   65: astore_3
    //   66: invokestatic 435	android/os/Environment:isExternalStorageRemovable	()Z
    //   69: ifeq +668 -> 737
    //   72: aload_3
    //   73: ldc_w 437
    //   76: ldc_w 438
    //   79: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   82: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   85: aload_3
    //   86: ldc_w 439
    //   89: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   92: aload_3
    //   93: aload_0
    //   94: aload_2
    //   95: invokespecial 441	org/vidogram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   98: putfield 373	org/vidogram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   101: aload_3
    //   102: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   105: putfield 360	org/vidogram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   108: aload_0
    //   109: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   112: aload_3
    //   113: invokevirtual 376	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   116: pop
    //   117: aload 6
    //   119: aload_2
    //   120: invokevirtual 442	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   123: pop
    //   124: new 444	java/io/BufferedReader
    //   127: dup
    //   128: new 446	java/io/FileReader
    //   131: dup
    //   132: ldc_w 448
    //   135: invokespecial 449	java/io/FileReader:<init>	(Ljava/lang/String;)V
    //   138: invokespecial 452	java/io/BufferedReader:<init>	(Ljava/io/Reader;)V
    //   141: astore_3
    //   142: aload_3
    //   143: astore_2
    //   144: aload_3
    //   145: invokevirtual 455	java/io/BufferedReader:readLine	()Ljava/lang/String;
    //   148: astore 5
    //   150: aload 5
    //   152: ifnull +644 -> 796
    //   155: aload_3
    //   156: astore_2
    //   157: aload 5
    //   159: ldc_w 457
    //   162: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   165: ifne +16 -> 181
    //   168: aload_3
    //   169: astore_2
    //   170: aload 5
    //   172: ldc_w 463
    //   175: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   178: ifeq -36 -> 142
    //   181: aload_3
    //   182: astore_2
    //   183: aload 5
    //   185: invokestatic 465	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   188: aload_3
    //   189: astore_2
    //   190: new 467	java/util/StringTokenizer
    //   193: dup
    //   194: aload 5
    //   196: ldc_w 469
    //   199: invokespecial 472	java/util/StringTokenizer:<init>	(Ljava/lang/String;Ljava/lang/String;)V
    //   202: astore 4
    //   204: aload_3
    //   205: astore_2
    //   206: aload 4
    //   208: invokevirtual 475	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   211: pop
    //   212: aload_3
    //   213: astore_2
    //   214: aload 4
    //   216: invokevirtual 475	java/util/StringTokenizer:nextToken	()Ljava/lang/String;
    //   219: astore 4
    //   221: aload_3
    //   222: astore_2
    //   223: aload 6
    //   225: aload 4
    //   227: invokevirtual 477	java/util/HashSet:contains	(Ljava/lang/Object;)Z
    //   230: ifne -88 -> 142
    //   233: aload_3
    //   234: astore_2
    //   235: aload 5
    //   237: ldc_w 479
    //   240: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   243: ifeq -101 -> 142
    //   246: aload_3
    //   247: astore_2
    //   248: aload 5
    //   250: ldc_w 481
    //   253: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   256: ifne -114 -> 142
    //   259: aload_3
    //   260: astore_2
    //   261: aload 5
    //   263: ldc_w 483
    //   266: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   269: ifne -127 -> 142
    //   272: aload_3
    //   273: astore_2
    //   274: aload 5
    //   276: ldc_w 485
    //   279: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   282: ifne -140 -> 142
    //   285: aload_3
    //   286: astore_2
    //   287: aload 5
    //   289: ldc_w 487
    //   292: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   295: ifne -153 -> 142
    //   298: aload_3
    //   299: astore_2
    //   300: aload 5
    //   302: ldc_w 489
    //   305: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   308: ifne -166 -> 142
    //   311: aload_3
    //   312: astore_2
    //   313: new 255	java/io/File
    //   316: dup
    //   317: aload 4
    //   319: invokespecial 490	java/io/File:<init>	(Ljava/lang/String;)V
    //   322: invokevirtual 363	java/io/File:isDirectory	()Z
    //   325: ifne +527 -> 852
    //   328: aload_3
    //   329: astore_2
    //   330: aload 4
    //   332: bipush 47
    //   334: invokevirtual 493	java/lang/String:lastIndexOf	(I)I
    //   337: istore_1
    //   338: iload_1
    //   339: iconst_m1
    //   340: if_icmpeq +512 -> 852
    //   343: aload_3
    //   344: astore_2
    //   345: new 495	java/lang/StringBuilder
    //   348: dup
    //   349: invokespecial 496	java/lang/StringBuilder:<init>	()V
    //   352: ldc_w 498
    //   355: invokevirtual 502	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   358: aload 4
    //   360: iload_1
    //   361: iconst_1
    //   362: iadd
    //   363: invokevirtual 506	java/lang/String:substring	(I)Ljava/lang/String;
    //   366: invokevirtual 502	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   369: invokevirtual 507	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   372: astore 5
    //   374: aload_3
    //   375: astore_2
    //   376: new 255	java/io/File
    //   379: dup
    //   380: aload 5
    //   382: invokespecial 490	java/io/File:<init>	(Ljava/lang/String;)V
    //   385: invokevirtual 363	java/io/File:isDirectory	()Z
    //   388: ifeq +464 -> 852
    //   391: aload 5
    //   393: astore 4
    //   395: aload_3
    //   396: astore_2
    //   397: aload 6
    //   399: aload 4
    //   401: invokevirtual 442	java/util/HashSet:add	(Ljava/lang/Object;)Z
    //   404: pop
    //   405: aload_3
    //   406: astore_2
    //   407: new 35	org/vidogram/ui/DocumentSelectActivity$ListItem
    //   410: dup
    //   411: aload_0
    //   412: aconst_null
    //   413: invokespecial 353	org/vidogram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/vidogram/ui/DocumentSelectActivity;Lorg/vidogram/ui/DocumentSelectActivity$1;)V
    //   416: astore 5
    //   418: aload_3
    //   419: astore_2
    //   420: aload 4
    //   422: invokevirtual 392	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   425: ldc_w 509
    //   428: invokevirtual 461	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
    //   431: ifeq +329 -> 760
    //   434: aload_3
    //   435: astore_2
    //   436: aload 5
    //   438: ldc_w 437
    //   441: ldc_w 438
    //   444: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   447: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   450: aload_3
    //   451: astore_2
    //   452: aload 5
    //   454: ldc_w 439
    //   457: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   460: aload_3
    //   461: astore_2
    //   462: aload 5
    //   464: aload_0
    //   465: aload 4
    //   467: invokespecial 441	org/vidogram/ui/DocumentSelectActivity:getRootSubtitle	(Ljava/lang/String;)Ljava/lang/String;
    //   470: putfield 373	org/vidogram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   473: aload_3
    //   474: astore_2
    //   475: aload 5
    //   477: new 255	java/io/File
    //   480: dup
    //   481: aload 4
    //   483: invokespecial 490	java/io/File:<init>	(Ljava/lang/String;)V
    //   486: putfield 360	org/vidogram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   489: aload_3
    //   490: astore_2
    //   491: aload_0
    //   492: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   495: aload 5
    //   497: invokevirtual 376	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   500: pop
    //   501: goto -359 -> 142
    //   504: astore 4
    //   506: aload_3
    //   507: astore_2
    //   508: aload 4
    //   510: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   513: goto -371 -> 142
    //   516: astore 4
    //   518: aload_3
    //   519: astore_2
    //   520: aload 4
    //   522: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   525: aload_3
    //   526: ifnull +7 -> 533
    //   529: aload_3
    //   530: invokevirtual 512	java/io/BufferedReader:close	()V
    //   533: new 35	org/vidogram/ui/DocumentSelectActivity$ListItem
    //   536: dup
    //   537: aload_0
    //   538: aconst_null
    //   539: invokespecial 353	org/vidogram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/vidogram/ui/DocumentSelectActivity;Lorg/vidogram/ui/DocumentSelectActivity$1;)V
    //   542: astore_2
    //   543: aload_2
    //   544: ldc_w 514
    //   547: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   550: aload_2
    //   551: ldc_w 516
    //   554: ldc_w 517
    //   557: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   560: putfield 373	org/vidogram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   563: aload_2
    //   564: ldc_w 364
    //   567: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   570: aload_2
    //   571: new 255	java/io/File
    //   574: dup
    //   575: ldc_w 514
    //   578: invokespecial 490	java/io/File:<init>	(Ljava/lang/String;)V
    //   581: putfield 360	org/vidogram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   584: aload_0
    //   585: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   588: aload_2
    //   589: invokevirtual 376	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   592: pop
    //   593: new 255	java/io/File
    //   596: dup
    //   597: invokestatic 268	android/os/Environment:getExternalStorageDirectory	()Ljava/io/File;
    //   600: ldc_w 519
    //   603: invokespecial 522	java/io/File:<init>	(Ljava/io/File;Ljava/lang/String;)V
    //   606: astore_2
    //   607: aload_2
    //   608: invokevirtual 525	java/io/File:exists	()Z
    //   611: ifeq +49 -> 660
    //   614: new 35	org/vidogram/ui/DocumentSelectActivity$ListItem
    //   617: dup
    //   618: aload_0
    //   619: aconst_null
    //   620: invokespecial 353	org/vidogram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/vidogram/ui/DocumentSelectActivity;Lorg/vidogram/ui/DocumentSelectActivity$1;)V
    //   623: astore_3
    //   624: aload_3
    //   625: ldc_w 519
    //   628: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   631: aload_3
    //   632: aload_2
    //   633: invokevirtual 271	java/io/File:toString	()Ljava/lang/String;
    //   636: putfield 373	org/vidogram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   639: aload_3
    //   640: ldc_w 364
    //   643: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   646: aload_3
    //   647: aload_2
    //   648: putfield 360	org/vidogram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   651: aload_0
    //   652: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   655: aload_3
    //   656: invokevirtual 376	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   659: pop
    //   660: new 35	org/vidogram/ui/DocumentSelectActivity$ListItem
    //   663: dup
    //   664: aload_0
    //   665: aconst_null
    //   666: invokespecial 353	org/vidogram/ui/DocumentSelectActivity$ListItem:<init>	(Lorg/vidogram/ui/DocumentSelectActivity;Lorg/vidogram/ui/DocumentSelectActivity$1;)V
    //   669: astore_2
    //   670: aload_2
    //   671: ldc_w 527
    //   674: ldc_w 528
    //   677: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   680: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   683: aload_2
    //   684: ldc_w 530
    //   687: ldc_w 531
    //   690: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   693: putfield 373	org/vidogram/ui/DocumentSelectActivity$ListItem:subtitle	Ljava/lang/String;
    //   696: aload_2
    //   697: ldc_w 532
    //   700: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   703: aload_2
    //   704: aconst_null
    //   705: putfield 360	org/vidogram/ui/DocumentSelectActivity$ListItem:file	Ljava/io/File;
    //   708: aload_0
    //   709: getfield 80	org/vidogram/ui/DocumentSelectActivity:items	Ljava/util/ArrayList;
    //   712: aload_2
    //   713: invokevirtual 376	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   716: pop
    //   717: aload_0
    //   718: getfield 162	org/vidogram/ui/DocumentSelectActivity:listView	Lorg/vidogram/ui/Components/RecyclerListView;
    //   721: invokestatic 315	org/vidogram/messenger/AndroidUtilities:clearDrawableAnimation	(Landroid/view/View;)V
    //   724: aload_0
    //   725: iconst_1
    //   726: putfield 174	org/vidogram/ui/DocumentSelectActivity:scrolling	Z
    //   729: aload_0
    //   730: getfield 115	org/vidogram/ui/DocumentSelectActivity:listAdapter	Lorg/vidogram/ui/DocumentSelectActivity$ListAdapter;
    //   733: invokevirtual 318	org/vidogram/ui/DocumentSelectActivity$ListAdapter:notifyDataSetChanged	()V
    //   736: return
    //   737: aload_3
    //   738: ldc_w 534
    //   741: ldc_w 535
    //   744: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   747: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   750: aload_3
    //   751: ldc_w 536
    //   754: putfield 367	org/vidogram/ui/DocumentSelectActivity$ListItem:icon	I
    //   757: goto -665 -> 92
    //   760: aload_3
    //   761: astore_2
    //   762: aload 5
    //   764: ldc_w 538
    //   767: ldc_w 539
    //   770: invokestatic 306	org/vidogram/messenger/LocaleController:getString	(Ljava/lang/String;I)Ljava/lang/String;
    //   773: putfield 357	org/vidogram/ui/DocumentSelectActivity$ListItem:title	Ljava/lang/String;
    //   776: goto -326 -> 450
    //   779: astore 4
    //   781: aload_2
    //   782: astore_3
    //   783: aload 4
    //   785: astore_2
    //   786: aload_3
    //   787: ifnull +7 -> 794
    //   790: aload_3
    //   791: invokevirtual 512	java/io/BufferedReader:close	()V
    //   794: aload_2
    //   795: athrow
    //   796: aload_3
    //   797: ifnull -264 -> 533
    //   800: aload_3
    //   801: invokevirtual 512	java/io/BufferedReader:close	()V
    //   804: goto -271 -> 533
    //   807: astore_2
    //   808: aload_2
    //   809: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   812: goto -279 -> 533
    //   815: astore_2
    //   816: aload_2
    //   817: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   820: goto -287 -> 533
    //   823: astore_3
    //   824: aload_3
    //   825: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   828: goto -34 -> 794
    //   831: astore_2
    //   832: aload_2
    //   833: invokestatic 253	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   836: goto -176 -> 660
    //   839: astore_2
    //   840: aconst_null
    //   841: astore_3
    //   842: goto -56 -> 786
    //   845: astore 4
    //   847: aconst_null
    //   848: astore_3
    //   849: goto -331 -> 518
    //   852: goto -457 -> 395
    //
    // Exception table:
    //   from	to	target	type
    //   407	418	504	java/lang/Exception
    //   420	434	504	java/lang/Exception
    //   436	450	504	java/lang/Exception
    //   452	460	504	java/lang/Exception
    //   462	473	504	java/lang/Exception
    //   475	489	504	java/lang/Exception
    //   491	501	504	java/lang/Exception
    //   762	776	504	java/lang/Exception
    //   144	150	516	java/lang/Exception
    //   157	168	516	java/lang/Exception
    //   170	181	516	java/lang/Exception
    //   183	188	516	java/lang/Exception
    //   190	204	516	java/lang/Exception
    //   206	212	516	java/lang/Exception
    //   214	221	516	java/lang/Exception
    //   223	233	516	java/lang/Exception
    //   235	246	516	java/lang/Exception
    //   248	259	516	java/lang/Exception
    //   261	272	516	java/lang/Exception
    //   274	285	516	java/lang/Exception
    //   287	298	516	java/lang/Exception
    //   300	311	516	java/lang/Exception
    //   313	328	516	java/lang/Exception
    //   330	338	516	java/lang/Exception
    //   345	374	516	java/lang/Exception
    //   376	391	516	java/lang/Exception
    //   397	405	516	java/lang/Exception
    //   508	513	516	java/lang/Exception
    //   144	150	779	finally
    //   157	168	779	finally
    //   170	181	779	finally
    //   183	188	779	finally
    //   190	204	779	finally
    //   206	212	779	finally
    //   214	221	779	finally
    //   223	233	779	finally
    //   235	246	779	finally
    //   248	259	779	finally
    //   261	272	779	finally
    //   274	285	779	finally
    //   287	298	779	finally
    //   300	311	779	finally
    //   313	328	779	finally
    //   330	338	779	finally
    //   345	374	779	finally
    //   376	391	779	finally
    //   397	405	779	finally
    //   407	418	779	finally
    //   420	434	779	finally
    //   436	450	779	finally
    //   452	460	779	finally
    //   462	473	779	finally
    //   475	489	779	finally
    //   491	501	779	finally
    //   508	513	779	finally
    //   520	525	779	finally
    //   762	776	779	finally
    //   800	804	807	java/lang/Exception
    //   529	533	815	java/lang/Exception
    //   790	794	823	java/lang/Exception
    //   593	660	831	java/lang/Exception
    //   124	142	839	finally
    //   124	142	845	java/lang/Exception
  }

  private void showErrorBox(String paramString)
  {
    if (getParentActivity() == null)
      return;
    new AlertDialog.Builder(getParentActivity()).setTitle(LocaleController.getString("AppName", 2131165319)).setMessage(paramString).setPositiveButton(LocaleController.getString("OK", 2131166153), null).show();
  }

  public View createView(Context paramContext)
  {
    if (!this.receiverRegistered)
    {
      this.receiverRegistered = true;
      localObject = new IntentFilter();
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_BAD_REMOVAL");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_CHECKING");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_EJECT");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_MOUNTED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_NOFS");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_REMOVED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_SHARED");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_UNMOUNTABLE");
      ((IntentFilter)localObject).addAction("android.intent.action.MEDIA_UNMOUNTED");
      ((IntentFilter)localObject).addDataScheme("file");
      ApplicationLoader.applicationContext.registerReceiver(this.receiver, (IntentFilter)localObject);
    }
    this.actionBar.setBackButtonDrawable(new BackDrawable(false));
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("SelectFile", 2131166408));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        Object localObject;
        if (paramInt == -1)
        {
          if (DocumentSelectActivity.this.actionBar.isActionModeShowed())
          {
            DocumentSelectActivity.this.selectedFiles.clear();
            DocumentSelectActivity.this.actionBar.hideActionMode();
            int i = DocumentSelectActivity.this.listView.getChildCount();
            paramInt = 0;
            while (paramInt < i)
            {
              localObject = DocumentSelectActivity.this.listView.getChildAt(paramInt);
              if ((localObject instanceof SharedDocumentCell))
                ((SharedDocumentCell)localObject).setChecked(false, true);
              paramInt += 1;
            }
          }
          DocumentSelectActivity.this.finishFragment();
        }
        while (true)
        {
          return;
          if ((paramInt != 3) || (DocumentSelectActivity.this.delegate == null))
            continue;
          localObject = new ArrayList();
          ((ArrayList)localObject).addAll(DocumentSelectActivity.this.selectedFiles.keySet());
          DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, (ArrayList)localObject);
          localObject = DocumentSelectActivity.this.selectedFiles.values().iterator();
          while (((Iterator)localObject).hasNext())
            ((DocumentSelectActivity.ListItem)((Iterator)localObject).next()).date = System.currentTimeMillis();
        }
      }
    });
    this.selectedFiles.clear();
    this.actionModeViews.clear();
    Object localObject = this.actionBar.createActionMode();
    this.selectedMessagesCountTextView = new NumberTextView(((ActionBarMenu)localObject).getContext());
    this.selectedMessagesCountTextView.setTextSize(18);
    this.selectedMessagesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.selectedMessagesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
    this.selectedMessagesCountTextView.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        return true;
      }
    });
    ((ActionBarMenu)localObject).addView(this.selectedMessagesCountTextView, LayoutHelper.createLinear(0, -1, 1.0F, 65, 0, 0, 0));
    this.actionModeViews.add(((ActionBarMenu)localObject).addItemWithWidth(3, 2130837735, AndroidUtilities.dp(54.0F)));
    this.fragmentView = new FrameLayout(paramContext);
    localObject = (FrameLayout)this.fragmentView;
    this.emptyView = new EmptyTextProgressView(paramContext);
    this.emptyView.showTextView();
    ((FrameLayout)localObject).addView(this.emptyView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    RecyclerListView localRecyclerListView = this.listView;
    LinearLayoutManager localLinearLayoutManager = new LinearLayoutManager(paramContext, 1, false);
    this.layoutManager = localLinearLayoutManager;
    localRecyclerListView.setLayoutManager(localLinearLayoutManager);
    this.listView.setEmptyView(this.emptyView);
    localRecyclerListView = this.listView;
    paramContext = new ListAdapter(paramContext);
    this.listAdapter = paramContext;
    localRecyclerListView.setAdapter(paramContext);
    ((FrameLayout)localObject).addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
      {
        paramRecyclerView = DocumentSelectActivity.this;
        if (paramInt != 0);
        for (boolean bool = true; ; bool = false)
        {
          DocumentSelectActivity.access$802(paramRecyclerView, bool);
          return;
        }
      }
    });
    this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
    {
      public boolean onItemClick(View paramView, int paramInt)
      {
        if (DocumentSelectActivity.this.actionBar.isActionModeShowed());
        while (true)
        {
          return false;
          Object localObject1 = DocumentSelectActivity.this.listAdapter.getItem(paramInt);
          if (localObject1 == null)
            continue;
          Object localObject2 = ((DocumentSelectActivity.ListItem)localObject1).file;
          if ((localObject2 == null) || (((File)localObject2).isDirectory()))
            break;
          if (!((File)localObject2).canRead())
          {
            DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", 2131165224));
            return false;
          }
          if ((DocumentSelectActivity.this.sizeLimit != 0L) && (((File)localObject2).length() > DocumentSelectActivity.this.sizeLimit))
          {
            DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", 2131165709, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.access$1200(DocumentSelectActivity.this)) }));
            return false;
          }
          if (((File)localObject2).length() == 0L)
            continue;
          DocumentSelectActivity.this.selectedFiles.put(((File)localObject2).toString(), localObject1);
          DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(1, false);
          localObject1 = new AnimatorSet();
          localObject2 = new ArrayList();
          paramInt = 0;
          while (paramInt < DocumentSelectActivity.this.actionModeViews.size())
          {
            View localView = (View)DocumentSelectActivity.this.actionModeViews.get(paramInt);
            AndroidUtilities.clearDrawableAnimation(localView);
            ((ArrayList)localObject2).add(ObjectAnimator.ofFloat(localView, "scaleY", new float[] { 0.1F, 1.0F }));
            paramInt += 1;
          }
          ((AnimatorSet)localObject1).playTogether((Collection)localObject2);
          ((AnimatorSet)localObject1).setDuration(250L);
          ((AnimatorSet)localObject1).start();
          DocumentSelectActivity.access$802(DocumentSelectActivity.this, false);
          if ((paramView instanceof SharedDocumentCell))
            ((SharedDocumentCell)paramView).setChecked(true, true);
          DocumentSelectActivity.this.actionBar.showActionMode();
        }
        return true;
      }
    });
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        DocumentSelectActivity.ListItem localListItem = DocumentSelectActivity.this.listAdapter.getItem(paramInt);
        if (localListItem == null);
        Object localObject;
        label448: 
        do
        {
          do
          {
            return;
            File localFile = localListItem.file;
            if (localFile == null)
            {
              if (localListItem.icon == 2130837851)
              {
                if (DocumentSelectActivity.this.delegate != null)
                  DocumentSelectActivity.this.delegate.startDocumentSelectActivity();
                DocumentSelectActivity.this.finishFragment(false);
                return;
              }
              paramView = (DocumentSelectActivity.HistoryEntry)DocumentSelectActivity.this.history.remove(DocumentSelectActivity.this.history.size() - 1);
              DocumentSelectActivity.this.actionBar.setTitle(paramView.title);
              if (paramView.dir != null)
                DocumentSelectActivity.this.listFiles(paramView.dir);
              while (true)
              {
                DocumentSelectActivity.this.layoutManager.scrollToPositionWithOffset(paramView.scrollItem, paramView.scrollOffset);
                return;
                DocumentSelectActivity.this.listRoots();
              }
            }
            if (localFile.isDirectory())
            {
              paramView = new DocumentSelectActivity.HistoryEntry(DocumentSelectActivity.this, null);
              paramView.scrollItem = DocumentSelectActivity.this.layoutManager.findLastVisibleItemPosition();
              localObject = DocumentSelectActivity.this.layoutManager.findViewByPosition(paramView.scrollItem);
              if (localObject != null)
                paramView.scrollOffset = ((View)localObject).getTop();
              paramView.dir = DocumentSelectActivity.this.currentDir;
              paramView.title = DocumentSelectActivity.this.actionBar.getTitle();
              DocumentSelectActivity.this.history.add(paramView);
              if (!DocumentSelectActivity.this.listFiles(localFile))
              {
                DocumentSelectActivity.this.history.remove(paramView);
                return;
              }
              DocumentSelectActivity.this.actionBar.setTitle(localListItem.title);
              return;
            }
            localObject = localFile;
            if (!localFile.canRead())
            {
              DocumentSelectActivity.this.showErrorBox(LocaleController.getString("AccessError", 2131165224));
              localObject = new File("/mnt/sdcard");
            }
            if ((DocumentSelectActivity.this.sizeLimit == 0L) || (((File)localObject).length() <= DocumentSelectActivity.this.sizeLimit))
              continue;
            DocumentSelectActivity.this.showErrorBox(LocaleController.formatString("FileUploadLimit", 2131165709, new Object[] { AndroidUtilities.formatFileSize(DocumentSelectActivity.access$1200(DocumentSelectActivity.this)) }));
            return;
          }
          while (((File)localObject).length() == 0L);
          if (!DocumentSelectActivity.this.actionBar.isActionModeShowed())
            continue;
          if (DocumentSelectActivity.this.selectedFiles.containsKey(((File)localObject).toString()))
          {
            DocumentSelectActivity.this.selectedFiles.remove(((File)localObject).toString());
            if (!DocumentSelectActivity.this.selectedFiles.isEmpty())
              break label534;
            DocumentSelectActivity.this.actionBar.hideActionMode();
          }
          while (true)
          {
            DocumentSelectActivity.access$802(DocumentSelectActivity.this, false);
            if (!(paramView instanceof SharedDocumentCell))
              break;
            ((SharedDocumentCell)paramView).setChecked(DocumentSelectActivity.this.selectedFiles.containsKey(localListItem.file.toString()), true);
            return;
            DocumentSelectActivity.this.selectedFiles.put(((File)localObject).toString(), localListItem);
            break label448;
            DocumentSelectActivity.this.selectedMessagesCountTextView.setNumber(DocumentSelectActivity.this.selectedFiles.size(), true);
          }
        }
        while (DocumentSelectActivity.this.delegate == null);
        label534: paramView = new ArrayList();
        paramView.add(((File)localObject).getAbsolutePath());
        DocumentSelectActivity.this.delegate.didSelectFiles(DocumentSelectActivity.this, paramView);
      }
    });
    listRoots();
    return (View)this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_ITEMSCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, "actionBarActionModeDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, "actionBarActionModeDefaultTop"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, "actionBarActionModeDefaultSelector"), new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarActionModeDefaultIcon"), new ThemeDescription(this.listView, 0, new Class[] { GraySectionCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { GraySectionCell.class }, null, null, null, "graySection"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "nameTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "dateTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkbox"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { SharedDocumentCell.class }, new String[] { "checkBox" }, null, null, null, "checkboxCheck"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "thumbImageView" }, null, null, null, "files_folderIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { SharedDocumentCell.class }, new String[] { "thumbImageView" }, null, null, null, "files_folderIconBackground"), new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { SharedDocumentCell.class }, new String[] { "extTextView" }, null, null, null, "files_iconText") };
  }

  public void loadRecentFiles()
  {
    while (true)
    {
      int i;
      try
      {
        File[] arrayOfFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).listFiles();
        i = 0;
        if (i >= arrayOfFile.length)
          break label192;
        File localFile = arrayOfFile[i];
        if (localFile.isDirectory())
          break label208;
        ListItem localListItem = new ListItem(null);
        localListItem.title = localFile.getName();
        localListItem.file = localFile;
        String str2 = localFile.getName();
        Object localObject = str2.split("\\.");
        if (localObject.length > 1)
        {
          localObject = localObject[(localObject.length - 1)];
          localListItem.ext = ((String)localObject);
          localListItem.subtitle = AndroidUtilities.formatFileSize(localFile.length());
          localObject = str2.toLowerCase();
          if ((!((String)localObject).endsWith(".jpg")) && (!((String)localObject).endsWith(".png")) && (!((String)localObject).endsWith(".gif")) && (!((String)localObject).endsWith(".jpeg")))
            continue;
          localListItem.thumb = localFile.getAbsolutePath();
          this.recentItems.add(localListItem);
        }
      }
      catch (Exception localException)
      {
        FileLog.e(localException);
        return;
      }
      String str1 = "?";
      continue;
      label192: Collections.sort(this.recentItems, new Comparator()
      {
        public int compare(DocumentSelectActivity.ListItem paramListItem1, DocumentSelectActivity.ListItem paramListItem2)
        {
          long l1 = paramListItem1.file.lastModified();
          long l2 = paramListItem2.file.lastModified();
          if (l1 == l2)
            return 0;
          if (l1 > l2)
            return -1;
          return 1;
        }
      });
      return;
      label208: i += 1;
    }
  }

  public boolean onBackPressed()
  {
    if (this.history.size() > 0)
    {
      HistoryEntry localHistoryEntry = (HistoryEntry)this.history.remove(this.history.size() - 1);
      this.actionBar.setTitle(localHistoryEntry.title);
      if (localHistoryEntry.dir != null)
        listFiles(localHistoryEntry.dir);
      while (true)
      {
        this.layoutManager.scrollToPositionWithOffset(localHistoryEntry.scrollItem, localHistoryEntry.scrollOffset);
        return false;
        listRoots();
      }
    }
    return super.onBackPressed();
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    if (this.listView != null)
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          DocumentSelectActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          DocumentSelectActivity.this.fixLayoutInternal();
          return true;
        }
      });
  }

  public boolean onFragmentCreate()
  {
    loadRecentFiles();
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    try
    {
      if (this.receiverRegistered)
        ApplicationLoader.applicationContext.unregisterReceiver(this.receiver);
      super.onFragmentDestroy();
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    fixLayoutInternal();
  }

  public void setDelegate(DocumentSelectActivityDelegate paramDocumentSelectActivityDelegate)
  {
    this.delegate = paramDocumentSelectActivityDelegate;
  }

  public static abstract interface DocumentSelectActivityDelegate
  {
    public abstract void didSelectFiles(DocumentSelectActivity paramDocumentSelectActivity, ArrayList<String> paramArrayList);

    public abstract void startDocumentSelectActivity();
  }

  private class HistoryEntry
  {
    File dir;
    int scrollItem;
    int scrollOffset;
    String title;

    private HistoryEntry()
    {
    }
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public DocumentSelectActivity.ListItem getItem(int paramInt)
    {
      if (paramInt < DocumentSelectActivity.this.items.size())
        return (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.items.get(paramInt);
      if ((DocumentSelectActivity.this.history.isEmpty()) && (!DocumentSelectActivity.this.recentItems.isEmpty()) && (paramInt != DocumentSelectActivity.this.items.size()))
      {
        paramInt -= DocumentSelectActivity.this.items.size() + 1;
        if (paramInt < DocumentSelectActivity.this.recentItems.size())
          return (DocumentSelectActivity.ListItem)DocumentSelectActivity.this.recentItems.get(paramInt);
      }
      return null;
    }

    public int getItemCount()
    {
      int j = DocumentSelectActivity.this.items.size();
      int i = j;
      if (DocumentSelectActivity.this.history.isEmpty())
      {
        i = j;
        if (!DocumentSelectActivity.this.recentItems.isEmpty())
          i = j + (DocumentSelectActivity.this.recentItems.size() + 1);
      }
      return i;
    }

    public int getItemViewType(int paramInt)
    {
      if (getItem(paramInt) != null)
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return paramViewHolder.getItemViewType() != 0;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1 = true;
      DocumentSelectActivity.ListItem localListItem;
      boolean bool2;
      if (paramViewHolder.getItemViewType() == 1)
      {
        localListItem = getItem(paramInt);
        paramViewHolder = (SharedDocumentCell)paramViewHolder.itemView;
        if (localListItem.icon == 0)
          break label115;
        paramViewHolder.setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, null, null, localListItem.icon);
        if ((localListItem.file == null) || (!DocumentSelectActivity.this.actionBar.isActionModeShowed()))
          break label171;
        bool2 = DocumentSelectActivity.this.selectedFiles.containsKey(localListItem.file.toString());
        if (DocumentSelectActivity.this.scrolling)
          break label166;
      }
      label166: for (bool1 = true; ; bool1 = false)
      {
        paramViewHolder.setChecked(bool2, bool1);
        return;
        label115: String str = localListItem.ext.toUpperCase().substring(0, Math.min(localListItem.ext.length(), 4));
        paramViewHolder.setTextAndValueAndTypeAndThumb(localListItem.title, localListItem.subtitle, str, localListItem.thumb, 0);
        break;
      }
      label171: if (!DocumentSelectActivity.this.scrolling);
      while (true)
      {
        paramViewHolder.setChecked(false, bool1);
        return;
        bool1 = false;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new SharedDocumentCell(this.mContext);
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new GraySectionCell(this.mContext);
        ((GraySectionCell)paramViewGroup).setText(LocaleController.getString("Recent", 2131166315).toUpperCase());
      }
    }
  }

  private class ListItem
  {
    long date;
    String ext = "";
    File file;
    int icon;
    String subtitle = "";
    String thumb;
    String title;

    private ListItem()
    {
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.DocumentSelectActivity
 * JD-Core Version:    0.6.0
 */