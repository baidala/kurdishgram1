package org.vidogram.ui.Components;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build.VERSION;
import android.support.v4.content.FileProvider;
import java.io.File;
import java.security.SecureRandom;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.Utilities;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;

public class WallpaperUpdater
{
  private String currentPicturePath;
  private File currentWallpaperPath;
  private WallpaperUpdaterDelegate delegate;
  private Activity parentActivity;
  private File picturePath = null;

  public WallpaperUpdater(Activity paramActivity, WallpaperUpdaterDelegate paramWallpaperUpdaterDelegate)
  {
    this.parentActivity = paramActivity;
    this.delegate = paramWallpaperUpdaterDelegate;
    this.currentWallpaperPath = new File(FileLoader.getInstance().getDirectory(4), Utilities.random.nextInt() + ".jpg");
  }

  public void cleanup()
  {
    this.currentWallpaperPath.delete();
  }

  public String getCurrentPicturePath()
  {
    return this.currentPicturePath;
  }

  public File getCurrentWallpaperPath()
  {
    return this.currentWallpaperPath;
  }

  // ERROR //
  public void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: iload_2
    //   4: iconst_m1
    //   5: if_icmpne +103 -> 108
    //   8: iload_1
    //   9: bipush 10
    //   11: if_icmpne +167 -> 178
    //   14: aload_0
    //   15: getfield 81	org/vidogram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   18: invokestatic 101	org/vidogram/messenger/AndroidUtilities:addMediaToGallery	(Ljava/lang/String;)V
    //   21: invokestatic 105	org/vidogram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   24: astore_3
    //   25: aload_0
    //   26: getfield 81	org/vidogram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   29: aconst_null
    //   30: aload_3
    //   31: getfield 111	android/graphics/Point:x	I
    //   34: i2f
    //   35: aload_3
    //   36: getfield 114	android/graphics/Point:y	I
    //   39: i2f
    //   40: iconst_1
    //   41: invokestatic 120	org/vidogram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   44: astore 6
    //   46: new 122	java/io/FileOutputStream
    //   49: dup
    //   50: aload_0
    //   51: getfield 74	org/vidogram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   54: invokespecial 125	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   57: astore 4
    //   59: aload 4
    //   61: astore_3
    //   62: aload 6
    //   64: getstatic 131	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   67: bipush 87
    //   69: aload 4
    //   71: invokevirtual 137	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   74: pop
    //   75: aload 4
    //   77: astore_3
    //   78: aload_0
    //   79: getfield 29	org/vidogram/ui/Components/WallpaperUpdater:delegate	Lorg/vidogram/ui/Components/WallpaperUpdater$WallpaperUpdaterDelegate;
    //   82: aload_0
    //   83: getfield 74	org/vidogram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   86: aload 6
    //   88: invokeinterface 141 3 0
    //   93: aload 4
    //   95: ifnull +8 -> 103
    //   98: aload 4
    //   100: invokevirtual 144	java/io/FileOutputStream:close	()V
    //   103: aload_0
    //   104: aconst_null
    //   105: putfield 81	org/vidogram/ui/Components/WallpaperUpdater:currentPicturePath	Ljava/lang/String;
    //   108: return
    //   109: astore_3
    //   110: aload_3
    //   111: invokestatic 150	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   114: goto -11 -> 103
    //   117: astore 5
    //   119: aconst_null
    //   120: astore 4
    //   122: aload 4
    //   124: astore_3
    //   125: aload 5
    //   127: invokestatic 150	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   130: aload 4
    //   132: ifnull -29 -> 103
    //   135: aload 4
    //   137: invokevirtual 144	java/io/FileOutputStream:close	()V
    //   140: goto -37 -> 103
    //   143: astore_3
    //   144: aload_3
    //   145: invokestatic 150	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   148: goto -45 -> 103
    //   151: astore_3
    //   152: aload 5
    //   154: astore 4
    //   156: aload 4
    //   158: ifnull +8 -> 166
    //   161: aload 4
    //   163: invokevirtual 144	java/io/FileOutputStream:close	()V
    //   166: aload_3
    //   167: athrow
    //   168: astore 4
    //   170: aload 4
    //   172: invokestatic 150	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   175: goto -9 -> 166
    //   178: iload_1
    //   179: bipush 11
    //   181: if_icmpne -73 -> 108
    //   184: aload_3
    //   185: ifnull -77 -> 108
    //   188: aload_3
    //   189: invokevirtual 156	android/content/Intent:getData	()Landroid/net/Uri;
    //   192: ifnull -84 -> 108
    //   195: invokestatic 105	org/vidogram/messenger/AndroidUtilities:getRealScreenSize	()Landroid/graphics/Point;
    //   198: astore 4
    //   200: aconst_null
    //   201: aload_3
    //   202: invokevirtual 156	android/content/Intent:getData	()Landroid/net/Uri;
    //   205: aload 4
    //   207: getfield 111	android/graphics/Point:x	I
    //   210: i2f
    //   211: aload 4
    //   213: getfield 114	android/graphics/Point:y	I
    //   216: i2f
    //   217: iconst_1
    //   218: invokestatic 120	org/vidogram/messenger/ImageLoader:loadBitmap	(Ljava/lang/String;Landroid/net/Uri;FFZ)Landroid/graphics/Bitmap;
    //   221: astore_3
    //   222: new 122	java/io/FileOutputStream
    //   225: dup
    //   226: aload_0
    //   227: getfield 74	org/vidogram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   230: invokespecial 125	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   233: astore 4
    //   235: aload_3
    //   236: getstatic 131	android/graphics/Bitmap$CompressFormat:JPEG	Landroid/graphics/Bitmap$CompressFormat;
    //   239: bipush 87
    //   241: aload 4
    //   243: invokevirtual 137	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   246: pop
    //   247: aload_0
    //   248: getfield 29	org/vidogram/ui/Components/WallpaperUpdater:delegate	Lorg/vidogram/ui/Components/WallpaperUpdater$WallpaperUpdaterDelegate;
    //   251: aload_0
    //   252: getfield 74	org/vidogram/ui/Components/WallpaperUpdater:currentWallpaperPath	Ljava/io/File;
    //   255: aload_3
    //   256: invokeinterface 141 3 0
    //   261: return
    //   262: astore_3
    //   263: aload_3
    //   264: invokestatic 150	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   267: return
    //   268: astore 5
    //   270: aload_3
    //   271: astore 4
    //   273: aload 5
    //   275: astore_3
    //   276: goto -120 -> 156
    //   279: astore 5
    //   281: goto -159 -> 122
    //
    // Exception table:
    //   from	to	target	type
    //   98	103	109	java/lang/Exception
    //   21	59	117	java/lang/Exception
    //   135	140	143	java/lang/Exception
    //   21	59	151	finally
    //   161	166	168	java/lang/Exception
    //   195	261	262	java/lang/Exception
    //   62	75	268	finally
    //   78	93	268	finally
    //   125	130	268	finally
    //   62	75	279	java/lang/Exception
    //   78	93	279	java/lang/Exception
  }

  public void setCurrentPicturePath(String paramString)
  {
    this.currentPicturePath = paramString;
  }

  public void showAlert(boolean paramBoolean)
  {
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(this.parentActivity);
    CharSequence[] arrayOfCharSequence;
    if (paramBoolean)
    {
      arrayOfCharSequence = new CharSequence[5];
      arrayOfCharSequence[0] = LocaleController.getString("FromCamera", 2131165779);
      arrayOfCharSequence[1] = LocaleController.getString("FromGalley", 2131165786);
      arrayOfCharSequence[2] = LocaleController.getString("SelectColor", 2131166406);
      arrayOfCharSequence[3] = LocaleController.getString("Default", 2131165626);
      arrayOfCharSequence[4] = LocaleController.getString("Cancel", 2131165427);
    }
    while (true)
    {
      localBuilder.setItems(arrayOfCharSequence, new DialogInterface.OnClickListener(paramBoolean)
      {
        public void onClick(DialogInterface paramDialogInterface, int paramInt)
        {
          if (paramInt == 0)
            try
            {
              paramDialogInterface = new Intent("android.media.action.IMAGE_CAPTURE");
              File localFile = AndroidUtilities.generatePicturePath();
              if (localFile != null)
              {
                if (Build.VERSION.SDK_INT < 24)
                  break label88;
                paramDialogInterface.putExtra("output", FileProvider.a(WallpaperUpdater.this.parentActivity, "org.vidogram.messenger.provider", localFile));
                paramDialogInterface.addFlags(2);
                paramDialogInterface.addFlags(1);
              }
              while (true)
              {
                WallpaperUpdater.access$102(WallpaperUpdater.this, localFile.getAbsolutePath());
                WallpaperUpdater.this.parentActivity.startActivityForResult(paramDialogInterface, 10);
                return;
                label88: paramDialogInterface.putExtra("output", Uri.fromFile(localFile));
              }
            }
            catch (java.lang.Exception paramDialogInterface)
            {
              try
              {
                FileLog.e(paramDialogInterface);
                return;
              }
              catch (java.lang.Exception paramDialogInterface)
              {
                FileLog.e(paramDialogInterface);
                return;
              }
            }
          if (paramInt == 1)
          {
            paramDialogInterface = new Intent("android.intent.action.PICK");
            paramDialogInterface.setType("image/*");
            WallpaperUpdater.this.parentActivity.startActivityForResult(paramDialogInterface, 11);
            return;
          }
          if (this.val$fromTheme)
          {
            if (paramInt == 2)
            {
              WallpaperUpdater.this.delegate.needOpenColorPicker();
              return;
            }
            if (paramInt == 3)
              WallpaperUpdater.this.delegate.didSelectWallpaper(null, null);
          }
        }
      });
      localBuilder.show();
      return;
      arrayOfCharSequence = new CharSequence[3];
      arrayOfCharSequence[0] = LocaleController.getString("FromCamera", 2131165779);
      arrayOfCharSequence[1] = LocaleController.getString("FromGalley", 2131165786);
      arrayOfCharSequence[2] = LocaleController.getString("Cancel", 2131165427);
    }
  }

  public static abstract interface WallpaperUpdaterDelegate
  {
    public abstract void didSelectWallpaper(File paramFile, Bitmap paramBitmap);

    public abstract void needOpenColorPicker();
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.Components.WallpaperUpdater
 * JD-Core Version:    0.6.0
 */