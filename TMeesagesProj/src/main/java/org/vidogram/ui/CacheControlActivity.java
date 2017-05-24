package org.vidogram.ui;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import java.io.File;
import java.util.ArrayList;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.SQLite.SQLitePreparedStatement;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.ClearCacheService;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.query.BotQuery;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.tgnet.NativeByteBuffer;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.AlertDialog;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.BottomSheet.BottomSheetCell;
import org.vidogram.ui.ActionBar.BottomSheet.Builder;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.CheckBoxCell;
import org.vidogram.ui.Cells.TextInfoPrivacyCell;
import org.vidogram.ui.Cells.TextSettingsCell;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class CacheControlActivity extends BaseFragment
{
  private long audioSize = -1L;
  private int cacheInfoRow;
  private int cacheRow;
  private long cacheSize = -1L;
  private boolean calculating = true;
  private volatile boolean canceled = false;
  private boolean[] clear = new boolean[6];
  private int databaseInfoRow;
  private int databaseRow;
  private long databaseSize = -1L;
  private long documentsSize = -1L;
  private int keepMediaInfoRow;
  private int keepMediaRow;
  private ListAdapter listAdapter;
  private RecyclerListView listView;
  private long musicSize = -1L;
  private long photoSize = -1L;
  private int rowCount;
  private long totalSize = -1L;
  private long videoSize = -1L;

  private void cleanupFolders()
  {
    AlertDialog localAlertDialog = new AlertDialog(getParentActivity(), 1);
    localAlertDialog.setMessage(LocaleController.getString("Loading", 2131165920));
    localAlertDialog.setCanceledOnTouchOutside(false);
    localAlertDialog.setCancelable(false);
    localAlertDialog.show();
    Utilities.globalQueue.postRunnable(new Runnable(localAlertDialog)
    {
      public void run()
      {
        int k = 0;
        boolean bool2 = false;
        boolean bool1;
        int i;
        int j;
        label46: Object localObject;
        if (k < 6)
        {
          if (CacheControlActivity.this.clear[k] == 0)
            bool1 = bool2;
          do
          {
            k += 1;
            bool2 = bool1;
            break;
            if (k != 0)
              break label479;
            i = 0;
            j = 0;
            bool1 = bool2;
          }
          while (j == -1);
          localObject = FileLoader.getInstance().checkDirectory(j);
          if (localObject == null);
        }
        label539: 
        while (true)
        {
          int m;
          try
          {
            localObject = ((File)localObject).listFiles();
            if (localObject != null)
            {
              m = 0;
              label84: if (m < localObject.length)
              {
                String str = localObject[m].getName().toLowerCase();
                if ((i != 1) && (i != 2))
                  continue;
                if (str.endsWith(".mp3"))
                  break label465;
                if (!str.endsWith(".m4a"))
                  break label539;
                break label465;
                if ((str.equals(".nomedia")) || (!localObject[m].isFile()))
                  break label470;
                localObject[m].delete();
              }
            }
          }
          catch (Throwable localThrowable)
          {
            FileLog.e(localThrowable);
          }
          if (j == 4)
          {
            CacheControlActivity.access$002(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), i));
            bool1 = true;
            break;
          }
          if (j == 1)
          {
            CacheControlActivity.access$702(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), i));
            bool1 = bool2;
            break;
          }
          if (j == 3)
          {
            if (i == 1)
            {
              CacheControlActivity.access$502(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), i));
              bool1 = bool2;
              break;
            }
            CacheControlActivity.access$602(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), i));
            bool1 = bool2;
            break;
          }
          if (j == 0)
          {
            CacheControlActivity.access$302(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), i));
            bool1 = true;
            break;
          }
          bool1 = bool2;
          if (j != 2)
            break;
          CacheControlActivity.access$402(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), i));
          bool1 = bool2;
          break;
          CacheControlActivity.access$802(CacheControlActivity.this, CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize + CacheControlActivity.this.audioSize + CacheControlActivity.this.photoSize + CacheControlActivity.this.documentsSize + CacheControlActivity.this.musicSize);
          AndroidUtilities.runOnUIThread(new Runnable(bool2)
          {
            public void run()
            {
              if (this.val$imagesClearedFinal)
                ImageLoader.getInstance().clearMemory();
              if (CacheControlActivity.this.listAdapter != null)
                CacheControlActivity.this.listAdapter.notifyDataSetChanged();
              try
              {
                CacheControlActivity.2.this.val$progressDialog.dismiss();
                return;
              }
              catch (Exception localException)
              {
                FileLog.e(localException);
              }
            }
          });
          return;
          label458: i = 0;
          j = -1;
          break label46;
          label465: if (i != 1)
            continue;
          label470: label479: 
          do
          {
            m += 1;
            break label84;
            if (k == 1)
            {
              i = 0;
              j = 2;
              break;
            }
            if (k == 2)
            {
              i = 1;
              j = 3;
              break;
            }
            if (k == 3)
            {
              i = 2;
              j = 3;
              break;
            }
            if (k == 4)
            {
              i = 0;
              j = 1;
              break;
            }
            if (k != 5)
              break label458;
            i = 0;
            j = 4;
            break;
          }
          while (i == 2);
        }
      }
    });
  }

  // ERROR //
  private long getDirectorySize(File paramFile, int paramInt)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +10 -> 11
    //   4: aload_0
    //   5: getfield 85	org/vidogram/ui/CacheControlActivity:canceled	Z
    //   8: ifeq +5 -> 13
    //   11: lconst_0
    //   12: lreturn
    //   13: aload_1
    //   14: invokevirtual 210	java/io/File:isDirectory	()Z
    //   17: ifeq +137 -> 154
    //   20: aload_1
    //   21: invokevirtual 214	java/io/File:listFiles	()[Ljava/io/File;
    //   24: astore_1
    //   25: aload_1
    //   26: ifnull +150 -> 176
    //   29: iconst_0
    //   30: istore_3
    //   31: lconst_0
    //   32: lstore 4
    //   34: lload 4
    //   36: lstore 6
    //   38: iload_3
    //   39: aload_1
    //   40: arraylength
    //   41: if_icmpge +110 -> 151
    //   44: aload_0
    //   45: getfield 85	org/vidogram/ui/CacheControlActivity:canceled	Z
    //   48: ifne -37 -> 11
    //   51: aload_1
    //   52: iload_3
    //   53: aaload
    //   54: astore 8
    //   56: iload_2
    //   57: iconst_1
    //   58: if_icmpeq +8 -> 66
    //   61: iload_2
    //   62: iconst_2
    //   63: if_icmpne +36 -> 99
    //   66: aload 8
    //   68: invokevirtual 218	java/io/File:getName	()Ljava/lang/String;
    //   71: invokevirtual 223	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   74: astore 9
    //   76: aload 9
    //   78: ldc 225
    //   80: invokevirtual 229	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   83: ifne +99 -> 182
    //   86: aload 9
    //   88: ldc 231
    //   90: invokevirtual 229	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   93: ifeq +109 -> 202
    //   96: goto +86 -> 182
    //   99: aload 8
    //   101: invokevirtual 210	java/io/File:isDirectory	()Z
    //   104: ifeq +18 -> 122
    //   107: lload 4
    //   109: aload_0
    //   110: aload 8
    //   112: iload_2
    //   113: invokespecial 96	org/vidogram/ui/CacheControlActivity:getDirectorySize	(Ljava/io/File;I)J
    //   116: ladd
    //   117: lstore 6
    //   119: goto +72 -> 191
    //   122: aload 8
    //   124: invokevirtual 235	java/io/File:length	()J
    //   127: lstore 6
    //   129: lload 4
    //   131: lload 6
    //   133: ladd
    //   134: lstore 6
    //   136: goto +55 -> 191
    //   139: astore_1
    //   140: lconst_0
    //   141: lstore 4
    //   143: aload_1
    //   144: invokestatic 241	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   147: lload 4
    //   149: lstore 6
    //   151: lload 6
    //   153: lreturn
    //   154: aload_1
    //   155: invokevirtual 244	java/io/File:isFile	()Z
    //   158: ifeq +18 -> 176
    //   161: aload_1
    //   162: invokevirtual 235	java/io/File:length	()J
    //   165: lconst_0
    //   166: ladd
    //   167: lstore 6
    //   169: goto -18 -> 151
    //   172: astore_1
    //   173: goto -30 -> 143
    //   176: lconst_0
    //   177: lstore 6
    //   179: goto -28 -> 151
    //   182: iload_2
    //   183: iconst_1
    //   184: if_icmpne -85 -> 99
    //   187: lload 4
    //   189: lstore 6
    //   191: iload_3
    //   192: iconst_1
    //   193: iadd
    //   194: istore_3
    //   195: lload 6
    //   197: lstore 4
    //   199: goto -165 -> 34
    //   202: lload 4
    //   204: lstore 6
    //   206: iload_2
    //   207: iconst_2
    //   208: if_icmpeq -17 -> 191
    //   211: goto -112 -> 99
    //
    // Exception table:
    //   from	to	target	type
    //   20	25	139	java/lang/Throwable
    //   38	51	172	java/lang/Throwable
    //   66	96	172	java/lang/Throwable
    //   99	119	172	java/lang/Throwable
    //   122	129	172	java/lang/Throwable
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("StorageUsage", 2131166497));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          CacheControlActivity.this.finishFragment();
      }
    });
    this.listAdapter = new ListAdapter(paramContext);
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    localFrameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView.setAdapter(this.listAdapter);
    this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramView, int paramInt)
      {
        if (CacheControlActivity.this.getParentActivity() == null);
        Object localObject3;
        do
        {
          return;
          if (paramInt == CacheControlActivity.this.keepMediaRow)
          {
            paramView = new BottomSheet.Builder(CacheControlActivity.this.getParentActivity());
            localObject1 = LocaleController.formatPluralString("Weeks", 1);
            localObject2 = LocaleController.formatPluralString("Months", 1);
            localObject3 = LocaleController.getString("KeepMediaForever", 2131165866);
            1 local1 = new DialogInterface.OnClickListener()
            {
              public void onClick(DialogInterface paramDialogInterface, int paramInt)
              {
                ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putInt("keep_media", paramInt).commit();
                if (CacheControlActivity.this.listAdapter != null)
                  CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                paramDialogInterface = PendingIntent.getService(ApplicationLoader.applicationContext, 0, new Intent(ApplicationLoader.applicationContext, ClearCacheService.class), 0);
                AlarmManager localAlarmManager = (AlarmManager)ApplicationLoader.applicationContext.getSystemService("alarm");
                if (paramInt == 2)
                {
                  localAlarmManager.cancel(paramDialogInterface);
                  return;
                }
                localAlarmManager.setInexactRepeating(2, 86400000L, 86400000L, paramDialogInterface);
              }
            };
            paramView.setItems(new CharSequence[] { localObject1, localObject2, localObject3 }, local1);
            CacheControlActivity.this.showDialog(paramView.create());
            return;
          }
          if (paramInt != CacheControlActivity.this.databaseRow)
            continue;
          paramView = new AlertDialog.Builder(CacheControlActivity.this.getParentActivity());
          paramView.setTitle(LocaleController.getString("AppName", 2131165319));
          paramView.setNegativeButton(LocaleController.getString("Cancel", 2131165427), null);
          paramView.setMessage(LocaleController.getString("LocalDatabaseClear", 2131165924));
          paramView.setPositiveButton(LocaleController.getString("CacheClear", 2131165406), new DialogInterface.OnClickListener()
          {
            public void onClick(DialogInterface paramDialogInterface, int paramInt)
            {
              paramDialogInterface = new AlertDialog(CacheControlActivity.this.getParentActivity(), 1);
              paramDialogInterface.setMessage(LocaleController.getString("Loading", 2131165920));
              paramDialogInterface.setCanceledOnTouchOutside(false);
              paramDialogInterface.setCancelable(false);
              paramDialogInterface.show();
              MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramDialogInterface)
              {
                public void run()
                {
                  while (true)
                  {
                    Object localObject2;
                    int j;
                    SQLitePreparedStatement localSQLitePreparedStatement;
                    try
                    {
                      SQLiteDatabase localSQLiteDatabase = MessagesStorage.getInstance().getDatabase();
                      localArrayList = new ArrayList();
                      localObject2 = localSQLiteDatabase.queryFinalized("SELECT did FROM dialogs WHERE 1", new Object[0]);
                      new StringBuilder();
                      if (!((SQLiteCursor)localObject2).next())
                        continue;
                      l1 = ((SQLiteCursor)localObject2).longValue(0);
                      i = (int)l1;
                      j = (int)(l1 >> 32);
                      if ((i == 0) || (j == 1))
                        continue;
                      localArrayList.add(Long.valueOf(l1));
                      continue;
                    }
                    catch (Exception localException1)
                    {
                      ArrayList localArrayList;
                      long l1;
                      int i;
                      FileLog.e(localException1);
                      return;
                      ((SQLiteCursor)localObject2).dispose();
                      localObject2 = localException1.executeFast("REPLACE INTO messages_holes VALUES(?, ?, ?)");
                      localSQLitePreparedStatement = localException1.executeFast("REPLACE INTO media_holes_v2 VALUES(?, ?, ?, ?)");
                      localException1.beginTransaction();
                      j = 0;
                      if (j < localArrayList.size())
                      {
                        Long localLong = (Long)localArrayList.get(j);
                        i = 0;
                        SQLiteCursor localSQLiteCursor1 = localException1.queryFinalized("SELECT COUNT(mid) FROM messages WHERE uid = " + localLong, new Object[0]);
                        if (!localSQLiteCursor1.next())
                          continue;
                        i = localSQLiteCursor1.intValue(0);
                        localSQLiteCursor1.dispose();
                        if (i <= 2)
                          break label700;
                        localSQLiteCursor1 = localException1.queryFinalized("SELECT last_mid_i, last_mid FROM dialogs WHERE did = " + localLong, new Object[0]);
                        i = -1;
                        if (!localSQLiteCursor1.next())
                          continue;
                        l1 = localSQLiteCursor1.longValue(0);
                        long l2 = localSQLiteCursor1.longValue(1);
                        SQLiteCursor localSQLiteCursor2 = localException1.queryFinalized("SELECT data FROM messages WHERE uid = " + localLong + " AND mid IN (" + l1 + "," + l2 + ")", new Object[0]);
                        int k = i;
                        try
                        {
                          if (!localSQLiteCursor2.next())
                            continue;
                          NativeByteBuffer localNativeByteBuffer = localSQLiteCursor2.byteBufferValue(0);
                          i = k;
                          if (localNativeByteBuffer == null)
                            continue;
                          TLRPC.Message localMessage = TLRPC.Message.TLdeserialize(localNativeByteBuffer, localNativeByteBuffer.readInt32(false), false);
                          localNativeByteBuffer.reuse();
                          i = k;
                          if (localMessage == null)
                            continue;
                          i = localMessage.id;
                        }
                        catch (Exception localException2)
                        {
                          FileLog.e(localException2);
                          localSQLiteCursor2.dispose();
                          localException1.executeFast("DELETE FROM messages WHERE uid = " + localLong + " AND mid != " + l1 + " AND mid != " + l2).stepThis().dispose();
                          localException1.executeFast("DELETE FROM messages_holes WHERE uid = " + localLong).stepThis().dispose();
                          localException1.executeFast("DELETE FROM bot_keyboard WHERE uid = " + localLong).stepThis().dispose();
                          localException1.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + localLong).stepThis().dispose();
                          localException1.executeFast("DELETE FROM media_v2 WHERE uid = " + localLong).stepThis().dispose();
                          localException1.executeFast("DELETE FROM media_holes_v2 WHERE uid = " + localLong).stepThis().dispose();
                          BotQuery.clearBotKeyboard(localLong.longValue(), null);
                          if (k == -1)
                            continue;
                          MessagesStorage.createFirstHoles(localLong.longValue(), (SQLitePreparedStatement)localObject2, localSQLitePreparedStatement, k);
                        }
                        localSQLiteCursor1.dispose();
                      }
                    }
                    finally
                    {
                      AndroidUtilities.runOnUIThread(new Runnable()
                      {
                        public void run()
                        {
                          try
                          {
                            CacheControlActivity.4.2.1.this.val$progressDialog.dismiss();
                            if (CacheControlActivity.this.listAdapter != null)
                            {
                              File localFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
                              CacheControlActivity.access$1402(CacheControlActivity.this, localFile.length());
                              CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                            }
                            return;
                          }
                          catch (Exception localException)
                          {
                            while (true)
                              FileLog.e(localException);
                          }
                        }
                      });
                    }
                    ((SQLitePreparedStatement)localObject2).dispose();
                    localSQLitePreparedStatement.dispose();
                    localObject1.commitTransaction();
                    localObject1.executeFast("VACUUM").stepThis().dispose();
                    AndroidUtilities.runOnUIThread(new Runnable()
                    {
                      public void run()
                      {
                        try
                        {
                          CacheControlActivity.4.2.1.this.val$progressDialog.dismiss();
                          if (CacheControlActivity.this.listAdapter != null)
                          {
                            File localFile = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db");
                            CacheControlActivity.access$1402(CacheControlActivity.this, localFile.length());
                            CacheControlActivity.this.listAdapter.notifyDataSetChanged();
                          }
                          return;
                        }
                        catch (Exception localException)
                        {
                          while (true)
                            FileLog.e(localException);
                        }
                      }
                    });
                    return;
                    label700: j += 1;
                  }
                }
              });
            }
          });
          CacheControlActivity.this.showDialog(paramView.create());
          return;
        }
        while ((paramInt != CacheControlActivity.this.cacheRow) || (CacheControlActivity.this.totalSize <= 0L) || (CacheControlActivity.this.getParentActivity() == null));
        Object localObject1 = new BottomSheet.Builder(CacheControlActivity.this.getParentActivity());
        ((BottomSheet.Builder)localObject1).setApplyTopPadding(false);
        ((BottomSheet.Builder)localObject1).setApplyBottomPadding(false);
        Object localObject2 = new LinearLayout(CacheControlActivity.this.getParentActivity());
        ((LinearLayout)localObject2).setOrientation(1);
        paramInt = 0;
        if (paramInt < 6)
        {
          long l = 0L;
          paramView = null;
          if (paramInt == 0)
          {
            l = CacheControlActivity.this.photoSize;
            paramView = LocaleController.getString("LocalPhotoCache", 2131165929);
            label324: if (l <= 0L)
              break label550;
            CacheControlActivity.this.clear[paramInt] = 1;
            localObject3 = new CheckBoxCell(CacheControlActivity.this.getParentActivity(), true);
            ((CheckBoxCell)localObject3).setTag(Integer.valueOf(paramInt));
            ((CheckBoxCell)localObject3).setBackgroundDrawable(Theme.getSelectorDrawable(false));
            ((LinearLayout)localObject2).addView((View)localObject3, LayoutHelper.createLinear(-1, 48));
            ((CheckBoxCell)localObject3).setText(paramView, AndroidUtilities.formatFileSize(l), true, true);
            ((CheckBoxCell)localObject3).setTextColor(Theme.getColor("dialogTextBlack"));
            ((CheckBoxCell)localObject3).setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramView)
              {
                paramView = (CheckBoxCell)paramView;
                int i = ((Integer)paramView.getTag()).intValue();
                boolean[] arrayOfBoolean = CacheControlActivity.this.clear;
                if (CacheControlActivity.this.clear[i] == 0);
                for (int j = 1; ; j = 0)
                {
                  arrayOfBoolean[i] = j;
                  paramView.setChecked(CacheControlActivity.this.clear[i], true);
                  return;
                }
              }
            });
          }
          while (true)
          {
            paramInt += 1;
            break;
            if (paramInt == 1)
            {
              l = CacheControlActivity.this.videoSize;
              paramView = LocaleController.getString("LocalVideoCache", 2131165930);
              break label324;
            }
            if (paramInt == 2)
            {
              l = CacheControlActivity.this.documentsSize;
              paramView = LocaleController.getString("LocalDocumentCache", 2131165926);
              break label324;
            }
            if (paramInt == 3)
            {
              l = CacheControlActivity.this.musicSize;
              paramView = LocaleController.getString("LocalMusicCache", 2131165928);
              break label324;
            }
            if (paramInt == 4)
            {
              l = CacheControlActivity.this.audioSize;
              paramView = LocaleController.getString("LocalAudioCache", 2131165921);
              break label324;
            }
            if (paramInt != 5)
              break label324;
            l = CacheControlActivity.this.cacheSize;
            paramView = LocaleController.getString("LocalCache", 2131165922);
            break label324;
            label550: CacheControlActivity.this.clear[paramInt] = 0;
          }
        }
        paramView = new BottomSheet.BottomSheetCell(CacheControlActivity.this.getParentActivity(), 1);
        paramView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        paramView.setTextAndIcon(LocaleController.getString("ClearMediaCache", 2131165552).toUpperCase(), 0);
        paramView.setTextColor(Theme.getColor("windowBackgroundWhiteRedText"));
        paramView.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View paramView)
          {
            try
            {
              if (CacheControlActivity.this.visibleDialog != null)
                CacheControlActivity.this.visibleDialog.dismiss();
              CacheControlActivity.this.cleanupFolders();
              return;
            }
            catch (Exception paramView)
            {
              while (true)
                FileLog.e(paramView);
            }
          }
        });
        ((LinearLayout)localObject2).addView(paramView, LayoutHelper.createLinear(-1, 48));
        ((BottomSheet.Builder)localObject1).setCustomView((View)localObject2);
        CacheControlActivity.this.showDialog(((BottomSheet.Builder)localObject1).create());
      }
    });
    return this.fragmentView;
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    return new ThemeDescription[] { new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[] { TextSettingsCell.class }, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundGray"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle"), new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector"), new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { TextSettingsCell.class }, new String[] { "valueTextView" }, null, null, null, "windowBackgroundWhiteValueText"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[] { TextInfoPrivacyCell.class }, null, null, null, "windowBackgroundGrayShadow"), new ThemeDescription(this.listView, 0, new Class[] { TextInfoPrivacyCell.class }, new String[] { "textView" }, null, null, null, "windowBackgroundWhiteGrayText4") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    this.rowCount = 0;
    int i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepMediaRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.keepMediaInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.cacheRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.cacheInfoRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.databaseRow = i;
    i = this.rowCount;
    this.rowCount = (i + 1);
    this.databaseInfoRow = i;
    this.databaseSize = new File(ApplicationLoader.getFilesDirFixed(), "cache4.db").length();
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        CacheControlActivity.access$002(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(4), 0));
        if (CacheControlActivity.this.canceled);
        do
        {
          do
          {
            do
            {
              do
              {
                return;
                CacheControlActivity.access$302(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(0), 0));
              }
              while (CacheControlActivity.this.canceled);
              CacheControlActivity.access$402(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(2), 0));
            }
            while (CacheControlActivity.this.canceled);
            CacheControlActivity.access$502(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 1));
          }
          while (CacheControlActivity.this.canceled);
          CacheControlActivity.access$602(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(3), 2));
        }
        while (CacheControlActivity.this.canceled);
        CacheControlActivity.access$702(CacheControlActivity.this, CacheControlActivity.this.getDirectorySize(FileLoader.getInstance().checkDirectory(1), 0));
        CacheControlActivity.access$802(CacheControlActivity.this, CacheControlActivity.this.cacheSize + CacheControlActivity.this.videoSize + CacheControlActivity.this.audioSize + CacheControlActivity.this.photoSize + CacheControlActivity.this.documentsSize + CacheControlActivity.this.musicSize);
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            CacheControlActivity.access$902(CacheControlActivity.this, false);
            if (CacheControlActivity.this.listAdapter != null)
              CacheControlActivity.this.listAdapter.notifyDataSetChanged();
          }
        });
      }
    });
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    this.canceled = true;
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
      return CacheControlActivity.this.rowCount;
    }

    public int getItemViewType(int paramInt)
    {
      if ((paramInt == CacheControlActivity.this.databaseInfoRow) || (paramInt == CacheControlActivity.this.cacheInfoRow) || (paramInt == CacheControlActivity.this.keepMediaInfoRow))
        return 1;
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getAdapterPosition();
      return (i == CacheControlActivity.this.databaseRow) || ((i == CacheControlActivity.this.cacheRow) && (CacheControlActivity.this.totalSize > 0L)) || (i == CacheControlActivity.this.keepMediaRow);
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      switch (paramViewHolder.getItemViewType())
      {
      default:
      case 0:
      case 1:
      }
      do
      {
        TextSettingsCell localTextSettingsCell;
        do
        {
          return;
          localTextSettingsCell = (TextSettingsCell)paramViewHolder.itemView;
          if (paramInt == CacheControlActivity.this.databaseRow)
          {
            localTextSettingsCell.setTextAndValue(LocaleController.getString("LocalDatabase", 2131165923), AndroidUtilities.formatFileSize(CacheControlActivity.this.databaseSize), false);
            return;
          }
          if (paramInt != CacheControlActivity.this.cacheRow)
            continue;
          if (CacheControlActivity.this.calculating)
          {
            localTextSettingsCell.setTextAndValue(LocaleController.getString("ClearMediaCache", 2131165552), LocaleController.getString("CalculatingSize", 2131165408), false);
            return;
          }
          String str = LocaleController.getString("ClearMediaCache", 2131165552);
          if (CacheControlActivity.this.totalSize == 0L);
          for (paramViewHolder = LocaleController.getString("CacheEmpty", 2131165407); ; paramViewHolder = AndroidUtilities.formatFileSize(CacheControlActivity.this.totalSize))
          {
            localTextSettingsCell.setTextAndValue(str, paramViewHolder, false);
            return;
          }
        }
        while (paramInt != CacheControlActivity.this.keepMediaRow);
        paramInt = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getInt("keep_media", 2);
        if (paramInt == 0)
          paramViewHolder = LocaleController.formatPluralString("Weeks", 1);
        while (true)
        {
          localTextSettingsCell.setTextAndValue(LocaleController.getString("KeepMedia", 2131165865), paramViewHolder, false);
          return;
          if (paramInt == 1)
          {
            paramViewHolder = LocaleController.formatPluralString("Months", 1);
            continue;
          }
          paramViewHolder = LocaleController.getString("KeepMediaForever", 2131165866);
        }
        paramViewHolder = (TextInfoPrivacyCell)paramViewHolder.itemView;
        if (paramInt == CacheControlActivity.this.databaseInfoRow)
        {
          paramViewHolder.setText(LocaleController.getString("LocalDatabaseInfo", 2131165925));
          paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837726, "windowBackgroundGrayShadow"));
          return;
        }
        if (paramInt != CacheControlActivity.this.cacheInfoRow)
          continue;
        paramViewHolder.setText("");
        paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
        return;
      }
      while (paramInt != CacheControlActivity.this.keepMediaInfoRow);
      paramViewHolder.setText(AndroidUtilities.replaceTags(LocaleController.getString("KeepMediaInfo", 2131165867)));
      paramViewHolder.setBackgroundDrawable(Theme.getThemedDrawable(this.mContext, 2130837725, "windowBackgroundGrayShadow"));
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      switch (paramInt)
      {
      default:
        paramViewGroup = new TextInfoPrivacyCell(this.mContext);
      case 0:
      }
      while (true)
      {
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new TextSettingsCell(this.mContext);
        paramViewGroup.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
      }
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.CacheControlActivity
 * JD-Core Version:    0.6.0
 */