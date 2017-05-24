package org.vidogram.ui.Adapters;

import android.location.Location;
import android.os.AsyncTask;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.BuildVars;
import org.vidogram.messenger.FileLog;
import org.vidogram.tgnet.TLRPC.GeoPoint;
import org.vidogram.tgnet.TLRPC.TL_geoPoint;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public abstract class BaseLocationAdapter extends RecyclerListView.SelectionAdapter
{
  private AsyncTask<Void, Void, JSONObject> currentTask;
  private BaseLocationAdapterDelegate delegate;
  protected ArrayList<String> iconUrls = new ArrayList();
  private Location lastSearchLocation;
  protected ArrayList<TLRPC.TL_messageMediaVenue> places = new ArrayList();
  private Timer searchTimer;
  protected boolean searching;

  public void destroy()
  {
    if (this.currentTask != null)
    {
      this.currentTask.cancel(true);
      this.currentTask = null;
    }
  }

  public void searchDelayed(String paramString, Location paramLocation)
  {
    if ((paramString == null) || (paramString.length() == 0))
    {
      this.places.clear();
      notifyDataSetChanged();
      return;
    }
    try
    {
      if (this.searchTimer != null)
        this.searchTimer.cancel();
      this.searchTimer = new Timer();
      this.searchTimer.schedule(new TimerTask(paramString, paramLocation)
      {
        public void run()
        {
          try
          {
            BaseLocationAdapter.this.searchTimer.cancel();
            BaseLocationAdapter.access$002(BaseLocationAdapter.this, null);
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                BaseLocationAdapter.access$102(BaseLocationAdapter.this, null);
                BaseLocationAdapter.this.searchGooglePlacesWithQuery(BaseLocationAdapter.1.this.val$query, BaseLocationAdapter.1.this.val$coordinate);
              }
            });
            return;
          }
          catch (Exception localException)
          {
            while (true)
              FileLog.e(localException);
          }
        }
      }
      , 200L, 500L);
      return;
    }
    catch (Exception localException)
    {
      while (true)
        FileLog.e(localException);
    }
  }

  public void searchGooglePlacesWithQuery(String paramString, Location paramLocation)
  {
    if ((this.lastSearchLocation != null) && (paramLocation.distanceTo(this.lastSearchLocation) < 200.0F))
      return;
    this.lastSearchLocation = paramLocation;
    if (this.searching)
    {
      this.searching = false;
      if (this.currentTask != null)
      {
        this.currentTask.cancel(true);
        this.currentTask = null;
      }
    }
    try
    {
      this.searching = true;
      Locale localLocale = Locale.US;
      String str1 = BuildVars.FOURSQUARE_API_VERSION;
      String str2 = BuildVars.FOURSQUARE_API_ID;
      String str3 = BuildVars.FOURSQUARE_API_KEY;
      paramLocation = String.format(Locale.US, "%f,%f", new Object[] { Double.valueOf(paramLocation.getLatitude()), Double.valueOf(paramLocation.getLongitude()) });
      if ((paramString != null) && (paramString.length() > 0));
      for (paramString = "&query=" + URLEncoder.encode(paramString, "UTF-8"); ; paramString = "")
      {
        this.currentTask = new AsyncTask(String.format(localLocale, "https://api.foursquare.com/v2/venues/search/?v=%s&locale=en&limit=25&client_id=%s&client_secret=%s&ll=%s%s", new Object[] { str1, str2, str3, paramLocation, paramString }))
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
            //   25: ldc 47
            //   27: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   30: aload 7
            //   32: astore_1
            //   33: aload 7
            //   35: ldc 55
            //   37: ldc 57
            //   39: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   42: aload 7
            //   44: astore_1
            //   45: aload 7
            //   47: ldc 59
            //   49: ldc 61
            //   51: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   54: aload 7
            //   56: astore_1
            //   57: aload 7
            //   59: ldc 63
            //   61: ldc 65
            //   63: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   66: aload 7
            //   68: astore_1
            //   69: aload 7
            //   71: sipush 5000
            //   74: invokevirtual 69	java/net/URLConnection:setConnectTimeout	(I)V
            //   77: aload 7
            //   79: astore_1
            //   80: aload 7
            //   82: sipush 5000
            //   85: invokevirtual 72	java/net/URLConnection:setReadTimeout	(I)V
            //   88: aload 7
            //   90: astore 6
            //   92: aload 7
            //   94: astore_1
            //   95: aload 7
            //   97: instanceof 74
            //   100: ifeq +157 -> 257
            //   103: aload 7
            //   105: astore_1
            //   106: aload 7
            //   108: checkcast 74	java/net/HttpURLConnection
            //   111: astore 8
            //   113: aload 7
            //   115: astore_1
            //   116: aload 8
            //   118: iconst_1
            //   119: invokevirtual 78	java/net/HttpURLConnection:setInstanceFollowRedirects	(Z)V
            //   122: aload 7
            //   124: astore_1
            //   125: aload 8
            //   127: invokevirtual 82	java/net/HttpURLConnection:getResponseCode	()I
            //   130: istore_2
            //   131: iload_2
            //   132: sipush 302
            //   135: if_icmpeq +21 -> 156
            //   138: iload_2
            //   139: sipush 301
            //   142: if_icmpeq +14 -> 156
            //   145: aload 7
            //   147: astore 6
            //   149: iload_2
            //   150: sipush 303
            //   153: if_icmpne +104 -> 257
            //   156: aload 7
            //   158: astore_1
            //   159: aload 8
            //   161: ldc 84
            //   163: invokevirtual 87	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
            //   166: astore 6
            //   168: aload 7
            //   170: astore_1
            //   171: aload 8
            //   173: ldc 89
            //   175: invokevirtual 87	java/net/HttpURLConnection:getHeaderField	(Ljava/lang/String;)Ljava/lang/String;
            //   178: astore 8
            //   180: aload 7
            //   182: astore_1
            //   183: new 36	java/net/URL
            //   186: dup
            //   187: aload 6
            //   189: invokespecial 39	java/net/URL:<init>	(Ljava/lang/String;)V
            //   192: invokevirtual 43	java/net/URL:openConnection	()Ljava/net/URLConnection;
            //   195: astore 6
            //   197: aload 6
            //   199: astore_1
            //   200: aload 6
            //   202: ldc 91
            //   204: aload 8
            //   206: invokevirtual 94	java/net/URLConnection:setRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   209: aload 6
            //   211: astore_1
            //   212: aload 6
            //   214: ldc 45
            //   216: ldc 47
            //   218: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   221: aload 6
            //   223: astore_1
            //   224: aload 6
            //   226: ldc 55
            //   228: ldc 57
            //   230: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   233: aload 6
            //   235: astore_1
            //   236: aload 6
            //   238: ldc 59
            //   240: ldc 61
            //   242: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   245: aload 6
            //   247: astore_1
            //   248: aload 6
            //   250: ldc 63
            //   252: ldc 65
            //   254: invokevirtual 53	java/net/URLConnection:addRequestProperty	(Ljava/lang/String;Ljava/lang/String;)V
            //   257: aload 6
            //   259: astore_1
            //   260: aload 6
            //   262: invokevirtual 97	java/net/URLConnection:connect	()V
            //   265: aload 6
            //   267: astore_1
            //   268: aload 6
            //   270: invokevirtual 101	java/net/URLConnection:getInputStream	()Ljava/io/InputStream;
            //   273: astore 7
            //   275: iconst_1
            //   276: istore_2
            //   277: iload_2
            //   278: ifeq +374 -> 652
            //   281: aload 6
            //   283: ifnull +41 -> 324
            //   286: aload 6
            //   288: instanceof 74
            //   291: ifeq +33 -> 324
            //   294: aload 6
            //   296: checkcast 74	java/net/HttpURLConnection
            //   299: invokevirtual 82	java/net/HttpURLConnection:getResponseCode	()I
            //   302: istore_2
            //   303: iload_2
            //   304: sipush 200
            //   307: if_icmpeq +17 -> 324
            //   310: iload_2
            //   311: sipush 202
            //   314: if_icmpeq +10 -> 324
            //   317: iload_2
            //   318: sipush 304
            //   321: if_icmpeq +3 -> 324
            //   324: aload 7
            //   326: ifnull +318 -> 644
            //   329: ldc 102
            //   331: newarray byte
            //   333: astore 9
            //   335: aconst_null
            //   336: astore_1
            //   337: aload_1
            //   338: astore 6
            //   340: aload_0
            //   341: invokevirtual 106	org/vidogram/ui/Adapters/BaseLocationAdapter$2:isCancelled	()Z
            //   344: istore 5
            //   346: iload 5
            //   348: ifeq +139 -> 487
            //   351: iload_3
            //   352: istore_2
            //   353: aload_1
            //   354: astore 6
            //   356: aload 6
            //   358: astore_1
            //   359: iload_2
            //   360: istore_3
            //   361: aload 7
            //   363: ifnull +13 -> 376
            //   366: aload 7
            //   368: invokevirtual 111	java/io/InputStream:close	()V
            //   371: iload_2
            //   372: istore_3
            //   373: aload 6
            //   375: astore_1
            //   376: iload_3
            //   377: ifeq +240 -> 617
            //   380: aload_1
            //   381: invokevirtual 117	java/lang/StringBuilder:toString	()Ljava/lang/String;
            //   384: areturn
            //   385: astore 6
            //   387: aconst_null
            //   388: astore_1
            //   389: aload 6
            //   391: instanceof 119
            //   394: ifeq +25 -> 419
            //   397: invokestatic 124	org/vidogram/tgnet/ConnectionsManager:isNetworkOnline	()Z
            //   400: ifeq +260 -> 660
            //   403: iconst_0
            //   404: istore_2
            //   405: aload 6
            //   407: invokestatic 130	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
            //   410: aconst_null
            //   411: astore 7
            //   413: aload_1
            //   414: astore 6
            //   416: goto -139 -> 277
            //   419: aload 6
            //   421: instanceof 132
            //   424: ifeq +8 -> 432
            //   427: iconst_0
            //   428: istore_2
            //   429: goto -24 -> 405
            //   432: aload 6
            //   434: instanceof 134
            //   437: ifeq +29 -> 466
            //   440: aload 6
            //   442: invokevirtual 137	java/lang/Throwable:getMessage	()Ljava/lang/String;
            //   445: ifnull +215 -> 660
            //   448: aload 6
            //   450: invokevirtual 137	java/lang/Throwable:getMessage	()Ljava/lang/String;
            //   453: ldc 139
            //   455: invokevirtual 145	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
            //   458: ifeq +202 -> 660
            //   461: iconst_0
            //   462: istore_2
            //   463: goto -58 -> 405
            //   466: aload 6
            //   468: instanceof 147
            //   471: ifeq +189 -> 660
            //   474: iconst_0
            //   475: istore_2
            //   476: goto -71 -> 405
            //   479: astore_1
            //   480: aload_1
            //   481: invokestatic 130	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
            //   484: goto -160 -> 324
            //   487: aload_1
            //   488: astore 6
            //   490: aload 7
            //   492: aload 9
            //   494: invokevirtual 151	java/io/InputStream:read	([B)I
            //   497: istore 4
            //   499: iload 4
            //   501: ifle +44 -> 545
            //   504: aload_1
            //   505: ifnonnull +136 -> 641
            //   508: aload_1
            //   509: astore 6
            //   511: new 113	java/lang/StringBuilder
            //   514: dup
            //   515: invokespecial 152	java/lang/StringBuilder:<init>	()V
            //   518: astore 8
            //   520: aload 8
            //   522: astore_1
            //   523: aload_1
            //   524: new 141	java/lang/String
            //   527: dup
            //   528: aload 9
            //   530: iconst_0
            //   531: iload 4
            //   533: ldc 154
            //   535: invokespecial 157	java/lang/String:<init>	([BIILjava/lang/String;)V
            //   538: invokevirtual 161	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
            //   541: pop
            //   542: goto -205 -> 337
            //   545: aload_1
            //   546: astore 6
            //   548: iload_3
            //   549: istore_2
            //   550: iload 4
            //   552: iconst_m1
            //   553: if_icmpne -197 -> 356
            //   556: iconst_1
            //   557: istore_2
            //   558: aload_1
            //   559: astore 6
            //   561: goto -205 -> 356
            //   564: astore 8
            //   566: aload_1
            //   567: astore 6
            //   569: aload 8
            //   571: invokestatic 130	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
            //   574: aload_1
            //   575: astore 6
            //   577: iload_3
            //   578: istore_2
            //   579: goto -223 -> 356
            //   582: astore 8
            //   584: aload 6
            //   586: astore_1
            //   587: aload 8
            //   589: astore 6
            //   591: aload 6
            //   593: invokestatic 130	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
            //   596: aload_1
            //   597: astore 6
            //   599: iload_3
            //   600: istore_2
            //   601: goto -245 -> 356
            //   604: astore_1
            //   605: aload_1
            //   606: invokestatic 130	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
            //   609: aload 6
            //   611: astore_1
            //   612: iload_2
            //   613: istore_3
            //   614: goto -238 -> 376
            //   617: aconst_null
            //   618: areturn
            //   619: astore 6
            //   621: aconst_null
            //   622: astore_1
            //   623: goto -32 -> 591
            //   626: astore 6
            //   628: goto -37 -> 591
            //   631: astore 8
            //   633: goto -67 -> 566
            //   636: astore 6
            //   638: goto -249 -> 389
            //   641: goto -118 -> 523
            //   644: aconst_null
            //   645: astore 6
            //   647: iload_3
            //   648: istore_2
            //   649: goto -293 -> 356
            //   652: aconst_null
            //   653: astore_1
            //   654: iload 4
            //   656: istore_3
            //   657: goto -281 -> 376
            //   660: iconst_1
            //   661: istore_2
            //   662: goto -257 -> 405
            //
            // Exception table:
            //   from	to	target	type
            //   5	18	385	java/lang/Throwable
            //   286	303	479	java/lang/Exception
            //   523	542	564	java/lang/Exception
            //   340	346	582	java/lang/Throwable
            //   490	499	582	java/lang/Throwable
            //   511	520	582	java/lang/Throwable
            //   569	574	582	java/lang/Throwable
            //   366	371	604	java/lang/Throwable
            //   329	335	619	java/lang/Throwable
            //   523	542	626	java/lang/Throwable
            //   490	499	631	java/lang/Exception
            //   511	520	631	java/lang/Exception
            //   21	30	636	java/lang/Throwable
            //   33	42	636	java/lang/Throwable
            //   45	54	636	java/lang/Throwable
            //   57	66	636	java/lang/Throwable
            //   69	77	636	java/lang/Throwable
            //   80	88	636	java/lang/Throwable
            //   95	103	636	java/lang/Throwable
            //   106	113	636	java/lang/Throwable
            //   116	122	636	java/lang/Throwable
            //   125	131	636	java/lang/Throwable
            //   159	168	636	java/lang/Throwable
            //   171	180	636	java/lang/Throwable
            //   183	197	636	java/lang/Throwable
            //   200	209	636	java/lang/Throwable
            //   212	221	636	java/lang/Throwable
            //   224	233	636	java/lang/Throwable
            //   236	245	636	java/lang/Throwable
            //   248	257	636	java/lang/Throwable
            //   260	265	636	java/lang/Throwable
            //   268	275	636	java/lang/Throwable
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
            if (paramJSONObject != null);
            while (true)
            {
              int i;
              TLRPC.TL_messageMediaVenue localTL_messageMediaVenue;
              try
              {
                BaseLocationAdapter.this.places.clear();
                BaseLocationAdapter.this.iconUrls.clear();
                JSONArray localJSONArray = paramJSONObject.getJSONObject("response").getJSONArray("venues");
                i = 0;
                int j = localJSONArray.length();
                if (i >= j)
                  continue;
                try
                {
                  JSONObject localJSONObject = localJSONArray.getJSONObject(i);
                  localTL_messageMediaVenue = null;
                  paramJSONObject = localTL_messageMediaVenue;
                  if (!localJSONObject.has("categories"))
                    continue;
                  Object localObject = localJSONObject.getJSONArray("categories");
                  paramJSONObject = localTL_messageMediaVenue;
                  if (((JSONArray)localObject).length() <= 0)
                    continue;
                  localObject = ((JSONArray)localObject).getJSONObject(0);
                  paramJSONObject = localTL_messageMediaVenue;
                  if (!((JSONObject)localObject).has("icon"))
                    continue;
                  paramJSONObject = ((JSONObject)localObject).getJSONObject("icon");
                  paramJSONObject = String.format(Locale.US, "%s64%s", new Object[] { paramJSONObject.getString("prefix"), paramJSONObject.getString("suffix") });
                  BaseLocationAdapter.this.iconUrls.add(paramJSONObject);
                  paramJSONObject = localJSONObject.getJSONObject("location");
                  localTL_messageMediaVenue = new TLRPC.TL_messageMediaVenue();
                  localTL_messageMediaVenue.geo = new TLRPC.TL_geoPoint();
                  localTL_messageMediaVenue.geo.lat = paramJSONObject.getDouble("lat");
                  localTL_messageMediaVenue.geo._long = paramJSONObject.getDouble("lng");
                  if (!paramJSONObject.has("address"))
                    continue;
                  localTL_messageMediaVenue.address = paramJSONObject.getString("address");
                  if (!localJSONObject.has("name"))
                    continue;
                  localTL_messageMediaVenue.title = localJSONObject.getString("name");
                  localTL_messageMediaVenue.venue_id = localJSONObject.getString("id");
                  localTL_messageMediaVenue.provider = "foursquare";
                  BaseLocationAdapter.this.places.add(localTL_messageMediaVenue);
                  break label535;
                  if (paramJSONObject.has("city"))
                  {
                    localTL_messageMediaVenue.address = paramJSONObject.getString("city");
                    continue;
                  }
                }
                catch (Exception paramJSONObject)
                {
                  FileLog.e(paramJSONObject);
                }
              }
              catch (Exception paramJSONObject)
              {
                FileLog.e(paramJSONObject);
                BaseLocationAdapter.this.searching = false;
                BaseLocationAdapter.this.notifyDataSetChanged();
                if (BaseLocationAdapter.this.delegate != null)
                  BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
              }
              do
              {
                return;
                if (paramJSONObject.has("state"))
                {
                  localTL_messageMediaVenue.address = paramJSONObject.getString("state");
                  break;
                }
                if (paramJSONObject.has("country"))
                {
                  localTL_messageMediaVenue.address = paramJSONObject.getString("country");
                  break;
                }
                localTL_messageMediaVenue.address = String.format(Locale.US, "%f,%f", new Object[] { Double.valueOf(localTL_messageMediaVenue.geo.lat), Double.valueOf(localTL_messageMediaVenue.geo._long) });
                break;
                BaseLocationAdapter.this.searching = false;
                BaseLocationAdapter.this.notifyDataSetChanged();
              }
              while (BaseLocationAdapter.this.delegate == null);
              BaseLocationAdapter.this.delegate.didLoadedSearchResult(BaseLocationAdapter.this.places);
              return;
              label535: i += 1;
            }
          }
        };
        this.currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[] { null, null, null });
        notifyDataSetChanged();
        return;
      }
    }
    catch (Exception paramString)
    {
      while (true)
      {
        FileLog.e(paramString);
        this.searching = false;
        if (this.delegate == null)
          continue;
        this.delegate.didLoadedSearchResult(this.places);
      }
    }
  }

  public void setDelegate(BaseLocationAdapterDelegate paramBaseLocationAdapterDelegate)
  {
    this.delegate = paramBaseLocationAdapterDelegate;
  }

  public static abstract interface BaseLocationAdapterDelegate
  {
    public abstract void didLoadedSearchResult(ArrayList<TLRPC.TL_messageMediaVenue> paramArrayList);
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Adapters.BaseLocationAdapter
 * JD-Core Version:    0.6.0
 */