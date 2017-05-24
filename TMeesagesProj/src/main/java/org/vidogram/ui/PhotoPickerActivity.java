package org.vidogram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.Editable;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageReceiver;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController.AlbumEntry;
import org.vidogram.messenger.MediaController.PhotoEntry;
import org.vidogram.messenger.MediaController.SearchImage;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.VideoEditedInfo;
import org.vidogram.messenger.support.widget.GridLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView;
import org.vidogram.messenger.support.widget.RecyclerView.Adapter;
import org.vidogram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.vidogram.messenger.support.widget.RecyclerView.LayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.vidogram.messenger.support.widget.RecyclerView.State;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.FoundGif;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_messages_foundGifs;
import org.vidogram.tgnet.TLRPC.TL_messages_searchGifs;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.ActionBarLayout;
import org.vidogram.ui.ActionBar.ActionBarMenu;
import org.vidogram.ui.ActionBar.ActionBarMenuItem;
import org.vidogram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.Cells.PhotoPickerPhotoCell;
import org.vidogram.ui.Components.BackupImageView;
import org.vidogram.ui.Components.CheckBox;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.PickerBottomLayout;
import org.vidogram.ui.Components.RadialProgressView;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.OnItemLongClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class PhotoPickerActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private boolean allowCaption = true;
  private boolean bingSearchEndReached = true;
  private ChatActivity chatActivity;
  private AsyncTask<Void, Void, JSONObject> currentBingTask;
  private PhotoPickerActivityDelegate delegate;
  private EmptyTextProgressView emptyView;
  private int giphyReqId;
  private boolean giphySearchEndReached = true;
  private int itemWidth = 100;
  private String lastSearchString;
  private int lastSearchToken;
  private GridLayoutManager layoutManager;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private boolean loadingRecent;
  private int nextGiphySearchOffset;
  private PickerBottomLayout pickerBottomLayout;
  private ArrayList<MediaController.SearchImage> recentImages;
  private ActionBarMenuItem searchItem;
  private ArrayList<MediaController.SearchImage> searchResult = new ArrayList();
  private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap();
  private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap();
  private boolean searching;
  private MediaController.AlbumEntry selectedAlbum;
  private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos;
  private HashMap<String, MediaController.SearchImage> selectedWebPhotos;
  private boolean sendPressed;
  private boolean singlePhoto;
  private int type;

  public PhotoPickerActivity(int paramInt, MediaController.AlbumEntry paramAlbumEntry, HashMap<Integer, MediaController.PhotoEntry> paramHashMap, HashMap<String, MediaController.SearchImage> paramHashMap1, ArrayList<MediaController.SearchImage> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, ChatActivity paramChatActivity)
  {
    this.selectedAlbum = paramAlbumEntry;
    this.selectedPhotos = paramHashMap;
    this.selectedWebPhotos = paramHashMap1;
    this.type = paramInt;
    this.recentImages = paramArrayList;
    this.singlePhoto = paramBoolean1;
    this.chatActivity = paramChatActivity;
    this.allowCaption = paramBoolean2;
    if ((paramAlbumEntry != null) && (paramAlbumEntry.isVideo))
      this.singlePhoto = true;
  }

  private void fixLayout()
  {
    if (this.listView != null)
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PhotoPickerActivity.this.fixLayoutInternal();
          if (PhotoPickerActivity.this.listView != null)
            PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          return true;
        }
      });
  }

  private void fixLayoutInternal()
  {
    if (getParentActivity() == null)
      return;
    int j = this.layoutManager.findFirstVisibleItemPosition();
    int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    if (AndroidUtilities.isTablet())
    {
      i = 3;
      label45: this.layoutManager.setSpanCount(i);
      if (!AndroidUtilities.isTablet())
        break label151;
    }
    label151: for (this.itemWidth = ((AndroidUtilities.dp(490.0F) - (i + 1) * AndroidUtilities.dp(4.0F)) / i); ; this.itemWidth = ((AndroidUtilities.displaySize.x - (i + 1) * AndroidUtilities.dp(4.0F)) / i))
    {
      this.listAdapter.notifyDataSetChanged();
      this.layoutManager.scrollToPosition(j);
      if (this.selectedAlbum != null)
        break;
      this.emptyView.setPadding(0, 0, 0, (int)((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) * 0.4F));
      return;
      if ((i == 3) || (i == 1))
      {
        i = 5;
        break label45;
      }
      i = 3;
      break label45;
    }
  }

  private PhotoPickerPhotoCell getCellForIndex(int paramInt)
  {
    int j = this.listView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.listView.getChildAt(i);
      PhotoPickerPhotoCell localPhotoPickerPhotoCell;
      int k;
      if ((localObject instanceof PhotoPickerPhotoCell))
      {
        localPhotoPickerPhotoCell = (PhotoPickerPhotoCell)localObject;
        k = ((Integer)localPhotoPickerPhotoCell.photoImage.getTag()).intValue();
        if (this.selectedAlbum == null)
          break label90;
        if ((k >= 0) && (k < this.selectedAlbum.photos.size()))
          break label128;
      }
      label128: label144: 
      while (true)
      {
        i += 1;
        break;
        label90: if ((this.searchResult.isEmpty()) && (this.lastSearchString == null));
        for (localObject = this.recentImages; ; localObject = this.searchResult)
        {
          if ((k < 0) || (k >= ((ArrayList)localObject).size()))
            break label144;
          if (k != paramInt)
            break;
          return localPhotoPickerPhotoCell;
        }
      }
    }
    return (PhotoPickerPhotoCell)null;
  }

  private void searchBingImages(String paramString, int paramInt1, int paramInt2)
  {
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      if (this.currentBingTask != null)
      {
        this.currentBingTask.cancel(true);
        this.currentBingTask = null;
      }
    }
    while (true)
    {
      try
      {
        this.searching = true;
        Object localObject = UserConfig.getCurrentUser().phone;
        if ((!((String)localObject).startsWith("44")) && (!((String)localObject).startsWith("49")) && (!((String)localObject).startsWith("43")) && (!((String)localObject).startsWith("31")))
        {
          if (!((String)localObject).startsWith("1"))
            break label316;
          break label310;
          localObject = Locale.US;
          String str = URLEncoder.encode(paramString, "UTF-8");
          if (i == 0)
            continue;
          paramString = "Strict";
          this.currentBingTask = new AsyncTask(String.format((Locale)localObject, "https://api.cognitive.microsoft.com/bing/v5.0/images/search?q='%s'&offset=%d&count=%d&$format=json&safeSearch=%s", new Object[] { str, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString }))
          {
            private boolean canRetry = true;

            // ERROR //
            private String downloadUrlContent(String paramString)
            {
              // Byte code:
              //   0: iconst_0
              //   1: istore 4
              //   3: iconst_0
              //   4: istore_3
              //   5: new 36	java/net/URL
              //   8: dup
              //   9: aload_1
              //   10: invokespecial 39	java/net/URL:<init>	(Ljava/lang/String;)V
              //   13: invokevirtual 43	java/net/URL:openConnection	()Ljava/net/URLConnection;
              //   16: astore 7
              //   18: aload 7
              //   20: astore_1
              //   21: aload 7
              //   23: ldc 45
              //   25: getstatic 50	org/vidogram/messenger/BuildVars:BING_SEARCH_KEY	Ljava/lang/String;
              //   28: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   31: aload 7
              //   33: astore_1
              //   34: aload 7
              //   36: ldc 58
              //   38: ldc 60
              //   40: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   43: aload 7
              //   45: astore_1
              //   46: aload 7
              //   48: ldc 62
              //   50: ldc 64
              //   52: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   55: aload 7
              //   57: astore_1
              //   58: aload 7
              //   60: ldc 66
              //   62: ldc 68
              //   64: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   67: aload 7
              //   69: astore_1
              //   70: aload 7
              //   72: ldc 70
              //   74: ldc 72
              //   76: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   79: aload 7
              //   81: astore_1
              //   82: aload 7
              //   84: sipush 5000
              //   87: invokevirtual 76	java/net/URLConnection:setConnectTimeout	(I)V
              //   90: aload 7
              //   92: astore_1
              //   93: aload 7
              //   95: sipush 5000
              //   98: invokevirtual 79	java/net/URLConnection:setReadTimeout	(I)V
              //   101: aload 7
              //   103: astore 6
              //   105: aload 7
              //   107: astore_1
              //   108: aload 7
              //   110: instanceof 81
              //   113: ifeq +170 -> 283
              //   116: aload 7
              //   118: astore_1
              //   119: aload 7
              //   121: checkcast 81	java/net/HttpURLConnection
              //   124: astore 8
              //   126: aload 7
              //   128: astore_1
              //   129: aload 8
              //   131: iconst_1
              //   132: invokevirtual 85	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
              //   135: aload 7
              //   137: astore_1
              //   138: aload 8
              //   140: invokevirtual 89	java/net/HttpURLConnection:getResponseCode	()I
              //   143: istore_2
              //   144: iload_2
              //   145: sipush 302
              //   148: if_icmpeq +21 -> 169
              //   151: iload_2
              //   152: sipush 301
              //   155: if_icmpeq +14 -> 169
              //   158: aload 7
              //   160: astore 6
              //   162: iload_2
              //   163: sipush 303
              //   166: if_icmpne +117 -> 283
              //   169: aload 7
              //   171: astore_1
              //   172: aload 8
              //   174: ldc 91
              //   176: invokevirtual 94	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
              //   179: astore 6
              //   181: aload 7
              //   183: astore_1
              //   184: aload 8
              //   186: ldc 96
              //   188: invokevirtual 94	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
              //   191: astore 8
              //   193: aload 7
              //   195: astore_1
              //   196: new 36	java/net/URL
              //   199: dup
              //   200: aload 6
              //   202: invokespecial 39	java/net/URL:<init>	(Ljava/lang/String;)V
              //   205: invokevirtual 43	java/net/URL:openConnection	()Ljava/net/URLConnection;
              //   208: astore 6
              //   210: aload 6
              //   212: astore_1
              //   213: aload 6
              //   215: ldc 98
              //   217: aload 8
              //   219: invokevirtual 101	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   222: aload 6
              //   224: astore_1
              //   225: aload 6
              //   227: ldc 45
              //   229: getstatic 50	org/vidogram/messenger/BuildVars:BING_SEARCH_KEY	Ljava/lang/String;
              //   232: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   235: aload 6
              //   237: astore_1
              //   238: aload 6
              //   240: ldc 58
              //   242: ldc 60
              //   244: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   247: aload 6
              //   249: astore_1
              //   250: aload 6
              //   252: ldc 62
              //   254: ldc 64
              //   256: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   259: aload 6
              //   261: astore_1
              //   262: aload 6
              //   264: ldc 66
              //   266: ldc 68
              //   268: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   271: aload 6
              //   273: astore_1
              //   274: aload 6
              //   276: ldc 70
              //   278: ldc 72
              //   280: invokevirtual 56	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
              //   283: aload 6
              //   285: astore_1
              //   286: aload 6
              //   288: invokevirtual 104	java/net/URLConnection:connect	()V
              //   291: aload 6
              //   293: astore_1
              //   294: aload 6
              //   296: invokevirtual 108	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
              //   299: astore 7
              //   301: iconst_1
              //   302: istore_2
              //   303: iload_2
              //   304: ifeq +374 -> 678
              //   307: aload 6
              //   309: ifnull +41 -> 350
              //   312: aload 6
              //   314: instanceof 81
              //   317: ifeq +33 -> 350
              //   320: aload 6
              //   322: checkcast 81	java/net/HttpURLConnection
              //   325: invokevirtual 89	java/net/HttpURLConnection:getResponseCode	()I
              //   328: istore_2
              //   329: iload_2
              //   330: sipush 200
              //   333: if_icmpeq +17 -> 350
              //   336: iload_2
              //   337: sipush 202
              //   340: if_icmpeq +10 -> 350
              //   343: iload_2
              //   344: sipush 304
              //   347: if_icmpeq +3 -> 350
              //   350: aload 7
              //   352: ifnull +318 -> 670
              //   355: ldc 109
              //   357: newarray byte
              //   359: astore 9
              //   361: aconst_null
              //   362: astore_1
              //   363: aload_1
              //   364: astore 6
              //   366: aload_0
              //   367: invokevirtual 113	org/vidogram/ui/PhotoPickerActivity$11:isCancelled	()Z
              //   370: istore 5
              //   372: iload 5
              //   374: ifeq +139 -> 513
              //   377: iload_3
              //   378: istore_2
              //   379: aload_1
              //   380: astore 6
              //   382: aload 6
              //   384: astore_1
              //   385: iload_2
              //   386: istore_3
              //   387: aload 7
              //   389: ifnull +13 -> 402
              //   392: aload 7
              //   394: invokevirtual 118	java/io/InputStream:close	()V
              //   397: iload_2
              //   398: istore_3
              //   399: aload 6
              //   401: astore_1
              //   402: iload_3
              //   403: ifeq +240 -> 643
              //   406: aload_1
              //   407: invokevirtual 124	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   410: areturn
              //   411: astore 6
              //   413: aconst_null
              //   414: astore_1
              //   415: aload 6
              //   417: instanceof 126
              //   420: ifeq +25 -> 445
              //   423: invokestatic 131	org/vidogram/tgnet/ConnectionsManager:isNetworkOnline	()Z
              //   426: ifeq +260 -> 686
              //   429: iconst_0
              //   430: istore_2
              //   431: aload 6
              //   433: invokestatic 137	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   436: aconst_null
              //   437: astore 7
              //   439: aload_1
              //   440: astore 6
              //   442: goto -139 -> 303
              //   445: aload 6
              //   447: instanceof 139
              //   450: ifeq +8 -> 458
              //   453: iconst_0
              //   454: istore_2
              //   455: goto -24 -> 431
              //   458: aload 6
              //   460: instanceof 141
              //   463: ifeq +29 -> 492
              //   466: aload 6
              //   468: invokevirtual 144	java/lang/Throwable:getMessage	()Ljava/lang/String;
              //   471: ifnull +215 -> 686
              //   474: aload 6
              //   476: invokevirtual 144	java/lang/Throwable:getMessage	()Ljava/lang/String;
              //   479: ldc 146
              //   481: invokevirtual 152	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
              //   484: ifeq +202 -> 686
              //   487: iconst_0
              //   488: istore_2
              //   489: goto -58 -> 431
              //   492: aload 6
              //   494: instanceof 154
              //   497: ifeq +189 -> 686
              //   500: iconst_0
              //   501: istore_2
              //   502: goto -71 -> 431
              //   505: astore_1
              //   506: aload_1
              //   507: invokestatic 137	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   510: goto -160 -> 350
              //   513: aload_1
              //   514: astore 6
              //   516: aload 7
              //   518: aload 9
              //   520: invokevirtual 158	java/io/InputStream:read	([B)I
              //   523: istore 4
              //   525: iload 4
              //   527: ifle +44 -> 571
              //   530: aload_1
              //   531: ifnonnull +136 -> 667
              //   534: aload_1
              //   535: astore 6
              //   537: new 120	java/lang/StringBuilder
              //   540: dup
              //   541: invokespecial 159	java/lang/StringBuilder:<init>	()V
              //   544: astore 8
              //   546: aload 8
              //   548: astore_1
              //   549: aload_1
              //   550: new 148	java/lang/String
              //   553: dup
              //   554: aload 9
              //   556: iconst_0
              //   557: iload 4
              //   559: ldc 161
              //   561: invokespecial 164	java/lang/String:<init>	([BIILjava/lang/String;)V
              //   564: invokevirtual 168	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   567: pop
              //   568: goto -205 -> 363
              //   571: aload_1
              //   572: astore 6
              //   574: iload_3
              //   575: istore_2
              //   576: iload 4
              //   578: iconst_m1
              //   579: if_icmpne -197 -> 382
              //   582: iconst_1
              //   583: istore_2
              //   584: aload_1
              //   585: astore 6
              //   587: goto -205 -> 382
              //   590: astore 8
              //   592: aload_1
              //   593: astore 6
              //   595: aload 8
              //   597: invokestatic 137	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   600: aload_1
              //   601: astore 6
              //   603: iload_3
              //   604: istore_2
              //   605: goto -223 -> 382
              //   608: astore 8
              //   610: aload 6
              //   612: astore_1
              //   613: aload 8
              //   615: astore 6
              //   617: aload 6
              //   619: invokestatic 137	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   622: aload_1
              //   623: astore 6
              //   625: iload_3
              //   626: istore_2
              //   627: goto -245 -> 382
              //   630: astore_1
              //   631: aload_1
              //   632: invokestatic 137	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
              //   635: aload 6
              //   637: astore_1
              //   638: iload_2
              //   639: istore_3
              //   640: goto -238 -> 402
              //   643: aconst_null
              //   644: areturn
              //   645: astore 6
              //   647: aconst_null
              //   648: astore_1
              //   649: goto -32 -> 617
              //   652: astore 6
              //   654: goto -37 -> 617
              //   657: astore 8
              //   659: goto -67 -> 592
              //   662: astore 6
              //   664: goto -249 -> 415
              //   667: goto -118 -> 549
              //   670: aconst_null
              //   671: astore 6
              //   673: iload_3
              //   674: istore_2
              //   675: goto -293 -> 382
              //   678: aconst_null
              //   679: astore_1
              //   680: iload 4
              //   682: istore_3
              //   683: goto -281 -> 402
              //   686: iconst_1
              //   687: istore_2
              //   688: goto -257 -> 431
              //
              // Exception table:
              //   from	to	target	type
              //   5	18	411	java/lang/Throwable
              //   312	329	505	java/lang/Exception
              //   549	568	590	java/lang/Exception
              //   366	372	608	java/lang/Throwable
              //   516	525	608	java/lang/Throwable
              //   537	546	608	java/lang/Throwable
              //   595	600	608	java/lang/Throwable
              //   392	397	630	java/lang/Throwable
              //   355	361	645	java/lang/Throwable
              //   549	568	652	java/lang/Throwable
              //   516	525	657	java/lang/Exception
              //   537	546	657	java/lang/Exception
              //   21	31	662	java/lang/Throwable
              //   34	43	662	java/lang/Throwable
              //   46	55	662	java/lang/Throwable
              //   58	67	662	java/lang/Throwable
              //   70	79	662	java/lang/Throwable
              //   82	90	662	java/lang/Throwable
              //   93	101	662	java/lang/Throwable
              //   108	116	662	java/lang/Throwable
              //   119	126	662	java/lang/Throwable
              //   129	135	662	java/lang/Throwable
              //   138	144	662	java/lang/Throwable
              //   172	181	662	java/lang/Throwable
              //   184	193	662	java/lang/Throwable
              //   196	210	662	java/lang/Throwable
              //   213	222	662	java/lang/Throwable
              //   225	235	662	java/lang/Throwable
              //   238	247	662	java/lang/Throwable
              //   250	259	662	java/lang/Throwable
              //   262	271	662	java/lang/Throwable
              //   274	283	662	java/lang/Throwable
              //   286	291	662	java/lang/Throwable
              //   294	301	662	java/lang/Throwable
            }

            protected JSONObject doInBackground(Void[] paramArrayOfVoid)
            {
              paramArrayOfVoid = downloadUrlContent(this.val$url);
              if (isCancelled())
                return null;
              try
              {
                paramArrayOfVoid = new JSONObject(paramArrayOfVoid);
                return paramArrayOfVoid;
              }
              catch (Exception paramArrayOfVoid)
              {
                FileLog.e(paramArrayOfVoid);
              }
              return null;
            }

            protected void onPostExecute(JSONObject paramJSONObject)
            {
              boolean bool = true;
              if (paramJSONObject != null);
              try
              {
                paramJSONObject = paramJSONObject.getJSONArray("value");
                j = 0;
                int k = 0;
                i = 0;
                try
                {
                  int m = paramJSONObject.length();
                  if (j >= m)
                    break label294;
                  try
                  {
                    JSONObject localJSONObject = paramJSONObject.getJSONObject(j);
                    String str = Utilities.MD5(localJSONObject.getString("contentUrl"));
                    if (PhotoPickerActivity.this.searchResultKeys.containsKey(str))
                      break label395;
                    MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
                    localSearchImage.id = str;
                    localSearchImage.width = localJSONObject.getInt("width");
                    localSearchImage.height = localJSONObject.getInt("height");
                    localSearchImage.size = Utilities.parseInt(localJSONObject.getString("contentSize")).intValue();
                    localSearchImage.imageUrl = localJSONObject.getString("contentUrl");
                    localSearchImage.thumbUrl = localJSONObject.getString("thumbnailUrl");
                    PhotoPickerActivity.this.searchResult.add(localSearchImage);
                    PhotoPickerActivity.this.searchResultKeys.put(str, localSearchImage);
                    i += 1;
                    k = 1;
                  }
                  catch (Exception localException)
                  {
                    FileLog.e(localException);
                  }
                }
                catch (Exception paramJSONObject)
                {
                }
                FileLog.e(paramJSONObject);
                PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
                label215: if (i != 0)
                  PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
                while (true)
                {
                  if (((!PhotoPickerActivity.this.searching) || (!PhotoPickerActivity.this.searchResult.isEmpty())) && ((!PhotoPickerActivity.this.loadingRecent) || (PhotoPickerActivity.this.lastSearchString != null)))
                    break label378;
                  PhotoPickerActivity.this.emptyView.showProgress();
                  return;
                  label294: paramJSONObject = PhotoPickerActivity.this;
                  if (k == 0);
                  while (true)
                  {
                    PhotoPickerActivity.access$302(paramJSONObject, bool);
                    break;
                    bool = false;
                  }
                  PhotoPickerActivity.access$302(PhotoPickerActivity.this, true);
                  PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
                  i = 0;
                  break label215;
                  if (!PhotoPickerActivity.this.giphySearchEndReached)
                    continue;
                  PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
                }
                label378: PhotoPickerActivity.this.emptyView.showTextView();
                return;
              }
              catch (Exception paramJSONObject)
              {
                while (true)
                {
                  int j;
                  int i = 0;
                  continue;
                  label395: j += 1;
                }
              }
            }
          };
          this.currentBingTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
          return;
          paramString = "Off";
          continue;
        }
      }
      catch (Exception paramString)
      {
        FileLog.e(paramString);
        this.bingSearchEndReached = true;
        this.searching = false;
        this.listAdapter.notifyItemRemoved(this.searchResult.size() - 1);
        if (((!this.searching) || (!this.searchResult.isEmpty())) && ((!this.loadingRecent) || (this.lastSearchString != null)))
          continue;
        this.emptyView.showProgress();
        return;
        this.emptyView.showTextView();
        return;
      }
      label310: int i = 1;
      continue;
      label316: i = 0;
    }
  }

  private void searchGiphyImages(String paramString, int paramInt)
  {
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      if (this.currentBingTask != null)
      {
        this.currentBingTask.cancel(true);
        this.currentBingTask = null;
      }
    }
    this.searching = true;
    TLRPC.TL_messages_searchGifs localTL_messages_searchGifs = new TLRPC.TL_messages_searchGifs();
    localTL_messages_searchGifs.q = paramString;
    localTL_messages_searchGifs.offset = paramInt;
    paramInt = this.lastSearchToken + 1;
    this.lastSearchToken = paramInt;
    this.giphyReqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_searchGifs, new RequestDelegate(paramInt, paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
        {
          public void run()
          {
            boolean bool = true;
            if (PhotoPickerActivity.10.this.val$token != PhotoPickerActivity.this.lastSearchToken)
              return;
            int i;
            if (this.val$response != null)
            {
              Object localObject1 = (TLRPC.TL_messages_foundGifs)this.val$response;
              PhotoPickerActivity.access$1202(PhotoPickerActivity.this, ((TLRPC.TL_messages_foundGifs)localObject1).next_offset);
              int j = 0;
              int k = 0;
              i = 0;
              while (j < ((TLRPC.TL_messages_foundGifs)localObject1).results.size())
              {
                TLRPC.FoundGif localFoundGif = (TLRPC.FoundGif)((TLRPC.TL_messages_foundGifs)localObject1).results.get(j);
                if (PhotoPickerActivity.this.searchResultKeys.containsKey(localFoundGif.url))
                {
                  j += 1;
                  continue;
                }
                MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
                localSearchImage.id = localFoundGif.url;
                label145: Object localObject2;
                if (localFoundGif.document != null)
                {
                  k = 0;
                  if (k < localFoundGif.document.attributes.size())
                  {
                    localObject2 = (TLRPC.DocumentAttribute)localFoundGif.document.attributes.get(k);
                    if ((!(localObject2 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject2 instanceof TLRPC.TL_documentAttributeVideo)))
                      break label394;
                    localSearchImage.width = ((TLRPC.DocumentAttribute)localObject2).w;
                  }
                }
                for (localSearchImage.height = ((TLRPC.DocumentAttribute)localObject2).h; ; localSearchImage.height = localFoundGif.h)
                {
                  localSearchImage.size = 0;
                  localSearchImage.imageUrl = localFoundGif.content_url;
                  localSearchImage.thumbUrl = localFoundGif.thumb_url;
                  localSearchImage.localUrl = (localFoundGif.url + "|" + PhotoPickerActivity.10.this.val$query);
                  localSearchImage.document = localFoundGif.document;
                  if ((localFoundGif.photo != null) && (localFoundGif.document != null))
                  {
                    localObject2 = FileLoader.getClosestPhotoSizeWithSize(localFoundGif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                    if (localObject2 != null)
                      localFoundGif.document.thumb = ((TLRPC.PhotoSize)localObject2);
                  }
                  localSearchImage.type = 1;
                  PhotoPickerActivity.this.searchResult.add(localSearchImage);
                  i += 1;
                  PhotoPickerActivity.this.searchResultKeys.put(localSearchImage.id, localSearchImage);
                  k = 1;
                  break;
                  label394: k += 1;
                  break label145;
                  localSearchImage.width = localFoundGif.w;
                }
              }
              localObject1 = PhotoPickerActivity.this;
              if (k == 0)
                PhotoPickerActivity.access$402((PhotoPickerActivity)localObject1, bool);
            }
            while (true)
            {
              PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
              if (i != 0)
                PhotoPickerActivity.this.listAdapter.notifyItemRangeInserted(PhotoPickerActivity.this.searchResult.size(), i);
              while (true)
              {
                if (((!PhotoPickerActivity.this.searching) || (!PhotoPickerActivity.this.searchResult.isEmpty())) && ((!PhotoPickerActivity.this.loadingRecent) || (PhotoPickerActivity.this.lastSearchString != null)))
                  break label607;
                PhotoPickerActivity.this.emptyView.showProgress();
                return;
                bool = false;
                break;
                if (!PhotoPickerActivity.this.giphySearchEndReached)
                  continue;
                PhotoPickerActivity.this.listAdapter.notifyItemRemoved(PhotoPickerActivity.this.searchResult.size() - 1);
              }
              label607: PhotoPickerActivity.this.emptyView.showTextView();
              return;
              i = 0;
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(this.giphyReqId, this.classGuid);
  }

  private void sendSelectedPhotos()
  {
    if (((this.selectedPhotos.isEmpty()) && (this.selectedWebPhotos.isEmpty())) || (this.delegate == null) || (this.sendPressed))
      return;
    this.sendPressed = true;
    this.delegate.actionButtonPressed(false);
    finishFragment();
  }

  private void updateSearchInterface()
  {
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    if (((this.searching) && (this.searchResult.isEmpty())) || ((this.loadingRecent) && (this.lastSearchString == null)))
    {
      this.emptyView.showProgress();
      return;
    }
    this.emptyView.showTextView();
  }

  public boolean allowCaption()
  {
    return this.allowCaption;
  }

  public boolean cancelButtonPressed()
  {
    this.delegate.actionButtonPressed(true);
    finishFragment();
    return true;
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843, false);
    this.actionBar.setTitleColor(-1);
    this.actionBar.setBackButtonImage(2130837732);
    label148: FrameLayout localFrameLayout;
    Object localObject1;
    float f;
    if (this.selectedAlbum != null)
    {
      this.actionBar.setTitle(this.selectedAlbum.bucketName);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramInt)
        {
          if (paramInt == -1)
            PhotoPickerActivity.this.finishFragment();
        }
      });
      if (this.selectedAlbum == null)
        this.searchItem = this.actionBar.createMenu().addItem(0, 2130837741).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public boolean canCollapseSearch()
          {
            PhotoPickerActivity.this.finishFragment();
            return false;
          }

          public void onSearchExpand()
          {
          }

          public void onSearchPressed(EditText paramEditText)
          {
            if (paramEditText.getText().toString().length() == 0)
              return;
            PhotoPickerActivity.this.searchResult.clear();
            PhotoPickerActivity.this.searchResultKeys.clear();
            PhotoPickerActivity.access$302(PhotoPickerActivity.this, true);
            PhotoPickerActivity.access$402(PhotoPickerActivity.this, true);
            if (PhotoPickerActivity.this.type == 0)
            {
              PhotoPickerActivity.this.searchBingImages(paramEditText.getText().toString(), 0, 53);
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, paramEditText.getText().toString());
              if (PhotoPickerActivity.this.lastSearchString.length() != 0)
                break label220;
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, null);
              if (PhotoPickerActivity.this.type != 0)
                break label189;
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131166044));
            }
            while (true)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              if (PhotoPickerActivity.this.type != 1)
                break;
              PhotoPickerActivity.access$1202(PhotoPickerActivity.this, 0);
              PhotoPickerActivity.this.searchGiphyImages(paramEditText.getText().toString(), 0);
              break;
              label189: if (PhotoPickerActivity.this.type != 1)
                continue;
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131166043));
              continue;
              label220: PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", 2131166045));
            }
          }

          public void onTextChanged(EditText paramEditText)
          {
            if (paramEditText.getText().length() == 0)
            {
              PhotoPickerActivity.this.searchResult.clear();
              PhotoPickerActivity.this.searchResultKeys.clear();
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, null);
              PhotoPickerActivity.access$302(PhotoPickerActivity.this, true);
              PhotoPickerActivity.access$402(PhotoPickerActivity.this, true);
              PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
              if (PhotoPickerActivity.this.currentBingTask != null)
              {
                PhotoPickerActivity.this.currentBingTask.cancel(true);
                PhotoPickerActivity.access$602(PhotoPickerActivity.this, null);
              }
              if (PhotoPickerActivity.this.giphyReqId != 0)
              {
                ConnectionsManager.getInstance().cancelRequest(PhotoPickerActivity.this.giphyReqId, true);
                PhotoPickerActivity.access$702(PhotoPickerActivity.this, 0);
              }
              if (PhotoPickerActivity.this.type != 0)
                break label167;
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131166044));
            }
            while (true)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              label167: if (PhotoPickerActivity.this.type != 1)
                continue;
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131166043));
            }
          }
        });
      if (this.selectedAlbum == null)
      {
        if (this.type != 0)
          break label708;
        this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", 2131166386));
      }
      this.fragmentView = new FrameLayout(paramContext);
      localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(-16777216);
      this.listView = new RecyclerListView(paramContext);
      this.listView.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F));
      this.listView.setClipToPadding(false);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setItemAnimator(null);
      this.listView.setLayoutAnimation(null);
      localObject1 = this.listView;
      Object localObject2 = new GridLayoutManager(paramContext, 4)
      {
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.layoutManager = ((GridLayoutManager)localObject2);
      ((RecyclerListView)localObject1).setLayoutManager((RecyclerView.LayoutManager)localObject2);
      this.listView.addItemDecoration(new RecyclerView.ItemDecoration()
      {
        public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
        {
          int j = 0;
          super.getItemOffsets(paramRect, paramView, paramRecyclerView, paramState);
          int k = paramState.getItemCount();
          int i = paramRecyclerView.getChildAdapterPosition(paramView);
          int n = PhotoPickerActivity.this.layoutManager.getSpanCount();
          k = (int)Math.ceil(k / n);
          int m = i / n;
          if (i % n != n - 1);
          for (i = AndroidUtilities.dp(4.0F); ; i = 0)
          {
            paramRect.right = i;
            i = j;
            if (m != k - 1)
              i = AndroidUtilities.dp(4.0F);
            paramRect.bottom = i;
            return;
          }
        }
      });
      localObject1 = this.listView;
      if (!this.singlePhoto)
        break label738;
      f = 0.0F;
      label319: localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, f));
      localObject1 = this.listView;
      localObject2 = new ListAdapter(paramContext);
      this.listAdapter = ((ListAdapter)localObject2);
      ((RecyclerListView)localObject1).setAdapter((RecyclerView.Adapter)localObject2);
      this.listView.setGlowColor(-13421773);
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          Object localObject;
          if ((PhotoPickerActivity.this.selectedAlbum != null) && (PhotoPickerActivity.this.selectedAlbum.isVideo))
          {
            if ((paramInt < 0) || (paramInt >= PhotoPickerActivity.this.selectedAlbum.photos.size()));
            while (true)
            {
              return;
              paramView = ((MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramInt)).path;
              if (Build.VERSION.SDK_INT < 16)
                break;
              localObject = new Bundle();
              ((Bundle)localObject).putString("videoPath", paramView);
              localObject = new VideoEditorActivity((Bundle)localObject);
              ((VideoEditorActivity)localObject).setDelegate(new VideoEditorActivity.VideoEditorActivityDelegate()
              {
                public void didFinishEditVideo(String paramString1, long paramLong1, long paramLong2, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, long paramLong3, long paramLong4, String paramString2)
                {
                  PhotoPickerActivity.this.removeSelfFromStack();
                  VideoEditedInfo localVideoEditedInfo = new VideoEditedInfo();
                  localVideoEditedInfo.startTime = paramLong1;
                  localVideoEditedInfo.endTime = paramLong2;
                  localVideoEditedInfo.rotationValue = paramInt3;
                  localVideoEditedInfo.originalWidth = paramInt4;
                  localVideoEditedInfo.originalHeight = paramInt5;
                  localVideoEditedInfo.bitrate = paramInt6;
                  localVideoEditedInfo.resultWidth = paramInt1;
                  localVideoEditedInfo.resultHeight = paramInt2;
                  localVideoEditedInfo.originalPath = paramString1;
                  PhotoPickerActivity.this.delegate.didSelectVideo(paramString1, localVideoEditedInfo, paramLong3, paramLong4, paramString2);
                }
              });
              if (!((VideoEditorActivity)localObject).onFragmentCreate())
              {
                PhotoPickerActivity.this.delegate.didSelectVideo(paramView, null, 0L, 0L, null);
                PhotoPickerActivity.this.finishFragment();
                return;
              }
              if (!PhotoPickerActivity.this.parentLayout.presentFragment((BaseFragment)localObject, false, false, true))
                continue;
              ((VideoEditorActivity)localObject).setParentChatActivity(PhotoPickerActivity.this.chatActivity);
              return;
            }
            PhotoPickerActivity.this.delegate.didSelectVideo(paramView, null, 0L, 0L, null);
            PhotoPickerActivity.this.finishFragment();
            return;
          }
          if (PhotoPickerActivity.this.selectedAlbum != null)
          {
            paramView = PhotoPickerActivity.this.selectedAlbum.photos;
            label225: if ((paramInt < 0) || (paramInt >= paramView.size()))
              break label354;
            if (PhotoPickerActivity.this.searchItem != null)
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
            PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
            localObject = PhotoViewer.getInstance();
            if (!PhotoPickerActivity.this.singlePhoto)
              break label356;
          }
          label354: label356: for (int i = 1; ; i = 0)
          {
            ((PhotoViewer)localObject).openPhotoForSelect(paramView, paramInt, i, PhotoPickerActivity.this, PhotoPickerActivity.this.chatActivity);
            return;
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
            {
              paramView = PhotoPickerActivity.this.recentImages;
              break label225;
            }
            paramView = PhotoPickerActivity.this.searchResult;
            break label225;
            break;
          }
        }
      });
      if (this.selectedAlbum == null)
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener()
        {
          public boolean onItemClick(View paramView, int paramInt)
          {
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
            {
              paramView = new AlertDialog.Builder(PhotoPickerActivity.this.getParentActivity());
              paramView.setTitle(LocaleController.getString("AppName", 2131165319));
              paramView.setMessage(LocaleController.getString("ClearSearch", 2131165555));
              paramView.setPositiveButton(LocaleController.getString("ClearButton", 2131165549).toUpperCase(), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramDialogInterface, int paramInt)
                {
                  PhotoPickerActivity.this.recentImages.clear();
                  if (PhotoPickerActivity.this.listAdapter != null)
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                  MessagesStorage.getInstance().clearWebRecent(PhotoPickerActivity.this.type);
                }
              });
              paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
              PhotoPickerActivity.this.showDialog(paramView.create());
              return true;
            }
            return false;
          }
        });
      this.emptyView = new EmptyTextProgressView(paramContext);
      this.emptyView.setTextColor(-8355712);
      this.emptyView.setProgressBarColor(-1);
      this.emptyView.setShowAtCenter(true);
      if (this.selectedAlbum == null)
        break label745;
      this.emptyView.setText(LocaleController.getString("NoPhotos", 2131166039));
      label476: localObject1 = this.emptyView;
      if (!this.singlePhoto)
        break label798;
      f = 0.0F;
    }
    while (true)
    {
      localFrameLayout.addView((View)localObject1, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, f));
      if (this.selectedAlbum == null)
      {
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener()
        {
          public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt)
          {
            if (paramInt == 1)
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
          }

          public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
          {
            paramInt2 = PhotoPickerActivity.this.layoutManager.findFirstVisibleItemPosition();
            if (paramInt2 == -1)
            {
              paramInt1 = 0;
              if (paramInt1 > 0)
              {
                int i = PhotoPickerActivity.this.layoutManager.getItemCount();
                if ((paramInt1 != 0) && (paramInt1 + paramInt2 > i - 2) && (!PhotoPickerActivity.this.searching))
                {
                  if ((PhotoPickerActivity.this.type != 0) || (PhotoPickerActivity.this.bingSearchEndReached))
                    break label126;
                  PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
                }
              }
            }
            label126: 
            do
            {
              return;
              paramInt1 = Math.abs(PhotoPickerActivity.this.layoutManager.findLastVisibleItemPosition() - paramInt2) + 1;
              break;
            }
            while ((PhotoPickerActivity.this.type != 1) || (PhotoPickerActivity.this.giphySearchEndReached));
            PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
          }
        });
        updateSearchInterface();
      }
      this.pickerBottomLayout = new PickerBottomLayout(paramContext);
      localFrameLayout.addView(this.pickerBottomLayout, LayoutHelper.createFrame(-1, 48, 80));
      this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoPickerActivity.this.delegate.actionButtonPressed(true);
          PhotoPickerActivity.this.finishFragment();
        }
      });
      this.pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          PhotoPickerActivity.this.sendSelectedPhotos();
        }
      });
      if (this.singlePhoto)
        this.pickerBottomLayout.setVisibility(8);
      this.listView.setEmptyView(this.emptyView);
      this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
      return this.fragmentView;
      if (this.type == 0)
      {
        this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", 2131166386));
        break;
      }
      if (this.type != 1)
        break;
      this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", 2131166383));
      break;
      label708: if (this.type != 1)
        break label148;
      this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", 2131166383));
      break label148;
      label738: f = 48.0F;
      break label319;
      label745: if (this.type == 0)
      {
        this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131166044));
        break label476;
      }
      if (this.type != 1)
        break label476;
      this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131166043));
      break label476;
      label798: f = 48.0F;
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.closeChats)
      removeSelfFromStack();
    do
      return;
    while ((paramInt != NotificationCenter.recentImagesDidLoaded) || (this.selectedAlbum != null) || (this.type != ((Integer)paramArrayOfObject[0]).intValue()));
    this.recentImages = ((ArrayList)paramArrayOfObject[1]);
    this.loadingRecent = false;
    updateSearchInterface();
  }

  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int i = 0;
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
    {
      paramFileLocation = new int[2];
      paramMessageObject.photoImage.getLocationInWindow(paramFileLocation);
      PhotoViewer.PlaceProviderObject localPlaceProviderObject = new PhotoViewer.PlaceProviderObject();
      localPlaceProviderObject.viewX = paramFileLocation[0];
      int j = paramFileLocation[1];
      if (Build.VERSION.SDK_INT >= 21);
      for (paramInt = i; ; paramInt = AndroidUtilities.statusBarHeight)
      {
        localPlaceProviderObject.viewY = (j - paramInt);
        localPlaceProviderObject.parentView = this.listView;
        localPlaceProviderObject.imageReceiver = paramMessageObject.photoImage.getImageReceiver();
        localPlaceProviderObject.thumb = localPlaceProviderObject.imageReceiver.getBitmap();
        localPlaceProviderObject.scale = paramMessageObject.photoImage.getScaleX();
        paramMessageObject.checkBox.setVisibility(8);
        return localPlaceProviderObject;
      }
    }
    return null;
  }

  public int getSelectedCount()
  {
    return this.selectedPhotos.size() + this.selectedWebPhotos.size();
  }

  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
      return paramMessageObject.photoImage.getImageReceiver().getBitmap();
    return null;
  }

  public boolean isPhotoChecked(int paramInt)
  {
    int i = 1;
    if (this.selectedAlbum != null)
      return (paramInt >= 0) && (paramInt < this.selectedAlbum.photos.size()) && (this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt)).imageId)));
    ArrayList localArrayList;
    if ((this.searchResult.isEmpty()) && (this.lastSearchString == null))
    {
      localArrayList = this.recentImages;
      if ((paramInt < 0) || (paramInt >= localArrayList.size()) || (!this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localArrayList.get(paramInt)).id)))
        break label126;
    }
    while (true)
    {
      return i;
      localArrayList = this.searchResult;
      break;
      label126: i = 0;
    }
  }

  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }

  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
    if ((this.selectedAlbum == null) && (this.recentImages.isEmpty()))
    {
      MessagesStorage.getInstance().loadWebRecent(this.type);
      this.loadingRecent = true;
    }
    return super.onFragmentCreate();
  }

  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    if (this.currentBingTask != null)
    {
      this.currentBingTask.cancel(true);
      this.currentBingTask = null;
    }
    if (this.giphyReqId != 0)
    {
      ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
      this.giphyReqId = 0;
    }
    super.onFragmentDestroy();
  }

  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null)
      this.listAdapter.notifyDataSetChanged();
    if (this.searchItem != null)
    {
      this.searchItem.openSearch(true);
      getParentActivity().getWindow().setSoftInputMode(32);
    }
    fixLayout();
  }

  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.searchItem != null))
      AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
  }

  public boolean scaleToFill()
  {
    return false;
  }

  public void sendButtonPressed(int paramInt, VideoEditedInfo paramVideoEditedInfo)
  {
    if (this.selectedAlbum != null)
      if (this.selectedPhotos.isEmpty())
        if ((paramInt >= 0) && (paramInt < this.selectedAlbum.photos.size()));
    label147: 
    while (true)
    {
      return;
      paramVideoEditedInfo = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      this.selectedPhotos.put(Integer.valueOf(paramVideoEditedInfo.imageId), paramVideoEditedInfo);
      do
      {
        sendSelectedPhotos();
        return;
      }
      while (!this.selectedPhotos.isEmpty());
      if ((this.searchResult.isEmpty()) && (this.lastSearchString == null));
      for (paramVideoEditedInfo = this.recentImages; ; paramVideoEditedInfo = this.searchResult)
      {
        if ((paramInt < 0) || (paramInt >= paramVideoEditedInfo.size()))
          break label147;
        paramVideoEditedInfo = (MediaController.SearchImage)paramVideoEditedInfo.get(paramInt);
        this.selectedWebPhotos.put(paramVideoEditedInfo.id, paramVideoEditedInfo);
        break;
      }
    }
  }

  public void setDelegate(PhotoPickerActivityDelegate paramPhotoPickerActivityDelegate)
  {
    this.delegate = paramPhotoPickerActivityDelegate;
  }

  public void setPhotoChecked(int paramInt)
  {
    Object localObject;
    boolean bool;
    label79: int j;
    int i;
    if (this.selectedAlbum != null)
    {
      if ((paramInt < 0) || (paramInt >= this.selectedAlbum.photos.size()))
        return;
      localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      if (this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
      {
        this.selectedPhotos.remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
        bool = false;
        j = this.listView.getChildCount();
        i = 0;
      }
    }
    while (true)
    {
      if (i < j)
      {
        localObject = this.listView.getChildAt(i);
        if (((Integer)((View)localObject).getTag()).intValue() == paramInt)
          ((PhotoPickerPhotoCell)localObject).setChecked(bool, false);
      }
      else
      {
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        this.delegate.selectedPhotosChanged();
        return;
        this.selectedPhotos.put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
        bool = true;
        break label79;
        if ((this.searchResult.isEmpty()) && (this.lastSearchString == null));
        for (localObject = this.recentImages; ; localObject = this.searchResult)
        {
          if ((paramInt < 0) || (paramInt >= ((ArrayList)localObject).size()))
            break label275;
          localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramInt);
          if (!this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id))
            break label277;
          this.selectedWebPhotos.remove(((MediaController.SearchImage)localObject).id);
          bool = false;
          break;
        }
        label275: break;
        label277: this.selectedWebPhotos.put(((MediaController.SearchImage)localObject).id, localObject);
        bool = true;
        break label79;
      }
      i += 1;
    }
  }

  public void updatePhotoAtIndex(int paramInt)
  {
    PhotoPickerPhotoCell localPhotoPickerPhotoCell = getCellForIndex(paramInt);
    if (localPhotoPickerPhotoCell != null)
    {
      if (this.selectedAlbum == null)
        break label227;
      localPhotoPickerPhotoCell.photoImage.setOrientation(0, true);
      localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      if (((MediaController.PhotoEntry)localObject).thumbPath != null)
        localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.PhotoEntry)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
    }
    else
    {
      return;
    }
    if (((MediaController.PhotoEntry)localObject).path != null)
    {
      localPhotoPickerPhotoCell.photoImage.setOrientation(((MediaController.PhotoEntry)localObject).orientation, true);
      if (((MediaController.PhotoEntry)localObject).isVideo)
      {
        localPhotoPickerPhotoCell.photoImage.setImage("vthumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
        return;
      }
      localPhotoPickerPhotoCell.photoImage.setImage("thumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
      return;
    }
    localPhotoPickerPhotoCell.photoImage.setImageResource(2130837964);
    return;
    label227: if ((this.searchResult.isEmpty()) && (this.lastSearchString == null));
    for (Object localObject = this.recentImages; ; localObject = this.searchResult)
    {
      localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramInt);
      if ((((MediaController.SearchImage)localObject).document == null) || (((MediaController.SearchImage)localObject).document.thumb == null))
        break;
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).document.thumb.location, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
      return;
    }
    if (((MediaController.SearchImage)localObject).thumbPath != null)
    {
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
      return;
    }
    if ((((MediaController.SearchImage)localObject).thumbUrl != null) && (((MediaController.SearchImage)localObject).thumbUrl.length() > 0))
    {
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbUrl, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837964));
      return;
    }
    localPhotoPickerPhotoCell.photoImage.setImageResource(2130837964);
  }

  public void willHidePhotoViewer()
  {
    int j = this.listView.getChildCount();
    int i = 0;
    while (i < j)
    {
      Object localObject = this.listView.getChildAt(i);
      if ((localObject instanceof PhotoPickerPhotoCell))
      {
        localObject = (PhotoPickerPhotoCell)localObject;
        if (((PhotoPickerPhotoCell)localObject).checkBox.getVisibility() != 0)
          ((PhotoPickerPhotoCell)localObject).checkBox.setVisibility(0);
      }
      i += 1;
    }
  }

  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int j = this.listView.getChildCount();
    int i = 0;
    if (i < j)
    {
      paramMessageObject = this.listView.getChildAt(i);
      if (paramMessageObject.getTag() == null)
        break label89;
    }
    label149: 
    while (true)
    {
      label36: i += 1;
      break;
      paramFileLocation = (PhotoPickerPhotoCell)paramMessageObject;
      int k = ((Integer)paramMessageObject.getTag()).intValue();
      if (this.selectedAlbum != null)
      {
        if ((k < 0) || (k >= this.selectedAlbum.photos.size()))
          continue;
        label89: if (k != paramInt)
          continue;
        paramFileLocation.checkBox.setVisibility(0);
        return;
      }
      if ((this.searchResult.isEmpty()) && (this.lastSearchString == null));
      for (paramMessageObject = this.recentImages; ; paramMessageObject = this.searchResult)
      {
        if (k < 0)
          break label149;
        if (k < paramMessageObject.size())
          break;
        break label36;
      }
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

    public int getItemCount()
    {
      int j = 0;
      int i = 0;
      if (PhotoPickerActivity.this.selectedAlbum == null)
      {
        if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
          return PhotoPickerActivity.this.recentImages.size();
        if (PhotoPickerActivity.this.type == 0)
        {
          j = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.bingSearchEndReached);
          while (true)
          {
            return i + j;
            i = 1;
          }
        }
        if (PhotoPickerActivity.this.type == 1)
        {
          int k = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.giphySearchEndReached);
          for (i = j; ; i = 1)
            return i + k;
        }
      }
      return PhotoPickerActivity.this.selectedAlbum.photos.size();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public int getItemViewType(int paramInt)
    {
      if ((PhotoPickerActivity.this.selectedAlbum != null) || ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null) && (paramInt < PhotoPickerActivity.this.recentImages.size())) || (paramInt < PhotoPickerActivity.this.searchResult.size()))
        return 0;
      return 1;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i;
      if (PhotoPickerActivity.this.selectedAlbum == null)
      {
        i = paramViewHolder.getAdapterPosition();
        if ((!PhotoPickerActivity.this.searchResult.isEmpty()) || (PhotoPickerActivity.this.lastSearchString != null))
          break label56;
        if (i >= PhotoPickerActivity.this.recentImages.size())
          break label54;
      }
      label54: label56: 
      do
      {
        return true;
        return false;
      }
      while (i < PhotoPickerActivity.this.searchResult.size());
      return false;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      int i = 0;
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      }
      Object localObject;
      label174: label505: 
      do
      {
        return;
        localObject = (PhotoPickerPhotoCell)paramViewHolder.itemView;
        ((PhotoPickerPhotoCell)localObject).itemWidth = PhotoPickerActivity.this.itemWidth;
        BackupImageView localBackupImageView = ((PhotoPickerPhotoCell)localObject).photoImage;
        localBackupImageView.setTag(Integer.valueOf(paramInt));
        ((PhotoPickerPhotoCell)localObject).setTag(Integer.valueOf(paramInt));
        localBackupImageView.setOrientation(0, true);
        boolean bool1;
        if (PhotoPickerActivity.this.selectedAlbum != null)
        {
          paramViewHolder = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramInt);
          if (paramViewHolder.thumbPath != null)
          {
            localBackupImageView.setImage(paramViewHolder.thumbPath, null, this.mContext.getResources().getDrawable(2130837964));
            ((PhotoPickerPhotoCell)localObject).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(paramViewHolder.imageId)), false);
            bool1 = PhotoViewer.getInstance().isShowingImage(paramViewHolder.path);
            paramViewHolder = localBackupImageView.getImageReceiver();
            if (bool1)
              break label620;
          }
        }
        for (boolean bool2 = true; ; bool2 = false)
        {
          paramViewHolder.setVisible(bool2, true);
          paramViewHolder = ((PhotoPickerPhotoCell)localObject).checkBox;
          if (!PhotoPickerActivity.this.singlePhoto)
          {
            paramInt = i;
            if (!bool1);
          }
          else
          {
            paramInt = 8;
          }
          paramViewHolder.setVisibility(paramInt);
          return;
          if (paramViewHolder.path != null)
          {
            localBackupImageView.setOrientation(paramViewHolder.orientation, true);
            if (paramViewHolder.isVideo)
            {
              localBackupImageView.setImage("vthumb://" + paramViewHolder.imageId + ":" + paramViewHolder.path, null, this.mContext.getResources().getDrawable(2130837964));
              break;
            }
            localBackupImageView.setImage("thumb://" + paramViewHolder.imageId + ":" + paramViewHolder.path, null, this.mContext.getResources().getDrawable(2130837964));
            break;
          }
          localBackupImageView.setImageResource(2130837964);
          break;
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
          {
            paramViewHolder = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(paramInt);
            if (paramViewHolder.thumbPath == null)
              break label505;
            localBackupImageView.setImage(paramViewHolder.thumbPath, null, this.mContext.getResources().getDrawable(2130837964));
          }
          while (true)
          {
            ((PhotoPickerPhotoCell)localObject).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(paramViewHolder.id), false);
            if (paramViewHolder.document == null)
              break label605;
            bool1 = PhotoViewer.getInstance().isShowingImage(FileLoader.getPathToAttach(paramViewHolder.document, true).getAbsolutePath());
            break;
            paramViewHolder = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(paramInt);
            break label409;
            if ((paramViewHolder.thumbUrl != null) && (paramViewHolder.thumbUrl.length() > 0))
            {
              localBackupImageView.setImage(paramViewHolder.thumbUrl, null, this.mContext.getResources().getDrawable(2130837964));
              continue;
            }
            if ((paramViewHolder.document != null) && (paramViewHolder.document.thumb != null))
            {
              localBackupImageView.setImage(paramViewHolder.document.thumb.location, null, this.mContext.getResources().getDrawable(2130837964));
              continue;
            }
            localBackupImageView.setImageResource(2130837964);
          }
          bool1 = PhotoViewer.getInstance().isShowingImage(paramViewHolder.imageUrl);
          break label174;
        }
        localObject = paramViewHolder.itemView.getLayoutParams();
      }
      while (localObject == null);
      label409: label605: label620: ((ViewGroup.LayoutParams)localObject).width = PhotoPickerActivity.this.itemWidth;
      ((ViewGroup.LayoutParams)localObject).height = PhotoPickerActivity.this.itemWidth;
      paramViewHolder.itemView.setLayoutParams((ViewGroup.LayoutParams)localObject);
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new FrameLayout(this.mContext);
        localObject = new RadialProgressView(this.mContext);
        ((RadialProgressView)localObject).setProgressColor(-1);
        paramViewGroup.addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F));
        return new RecyclerListView.Holder(paramViewGroup);
      case 0:
      }
      paramViewGroup = new PhotoPickerPhotoCell(this.mContext);
      paramViewGroup.checkFrame.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          int i = ((Integer)((View)paramView.getParent()).getTag()).intValue();
          Object localObject;
          if (PhotoPickerActivity.this.selectedAlbum != null)
          {
            localObject = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(i);
            if (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
            {
              PhotoPickerActivity.this.selectedPhotos.remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
              ((MediaController.PhotoEntry)localObject).imagePath = null;
              ((MediaController.PhotoEntry)localObject).thumbPath = null;
              ((MediaController.PhotoEntry)localObject).stickers.clear();
              PhotoPickerActivity.this.updatePhotoAtIndex(i);
            }
            while (true)
            {
              ((PhotoPickerPhotoCell)paramView.getParent()).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)), true);
              PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size() + PhotoPickerActivity.this.selectedWebPhotos.size(), true);
              PhotoPickerActivity.this.delegate.selectedPhotosChanged();
              return;
              PhotoPickerActivity.this.selectedPhotos.put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
            }
          }
          AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
          if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
          {
            localObject = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(((Integer)((View)paramView.getParent()).getTag()).intValue());
            label314: if (!PhotoPickerActivity.this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id))
              break label440;
            PhotoPickerActivity.this.selectedWebPhotos.remove(((MediaController.SearchImage)localObject).id);
            ((MediaController.SearchImage)localObject).imagePath = null;
            ((MediaController.SearchImage)localObject).thumbPath = null;
            PhotoPickerActivity.this.updatePhotoAtIndex(i);
          }
          while (true)
          {
            ((PhotoPickerPhotoCell)paramView.getParent()).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id), true);
            break;
            localObject = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(((Integer)((View)paramView.getParent()).getTag()).intValue());
            break label314;
            label440: PhotoPickerActivity.this.selectedWebPhotos.put(((MediaController.SearchImage)localObject).id, localObject);
          }
        }
      });
      Object localObject = paramViewGroup.checkFrame;
      if (PhotoPickerActivity.this.singlePhoto);
      for (paramInt = 8; ; paramInt = 0)
      {
        ((FrameLayout)localObject).setVisibility(paramInt);
        break;
      }
    }
  }

  public static abstract interface PhotoPickerActivityDelegate
  {
    public abstract void actionButtonPressed(boolean paramBoolean);

    public abstract void didSelectVideo(String paramString1, VideoEditedInfo paramVideoEditedInfo, long paramLong1, long paramLong2, String paramString2);

    public abstract void selectedPhotosChanged();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.PhotoPickerActivity
 * JD-Core Version:    0.6.0
 */