package org.vidogram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Semaphore;
import org.vidogram.SQLite.SQLiteCursor;
import org.vidogram.SQLite.SQLiteDatabase;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatPhoto;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.LaunchActivity;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService
{
  private RectF bitmapRect;
  private Paint roundPaint;

  private Icon createRoundBitmap(File paramFile)
  {
    try
    {
      paramFile = BitmapFactory.decodeFile(paramFile.toString());
      if (paramFile != null)
      {
        Bitmap localBitmap = Bitmap.createBitmap(paramFile.getWidth(), paramFile.getHeight(), Bitmap.Config.ARGB_8888);
        localBitmap.eraseColor(0);
        Canvas localCanvas = new Canvas(localBitmap);
        BitmapShader localBitmapShader = new BitmapShader(paramFile, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (this.roundPaint == null)
        {
          this.roundPaint = new Paint(1);
          this.bitmapRect = new RectF();
        }
        this.roundPaint.setShader(localBitmapShader);
        this.bitmapRect.set(0.0F, 0.0F, paramFile.getWidth(), paramFile.getHeight());
        localCanvas.drawRoundRect(this.bitmapRect, paramFile.getWidth(), paramFile.getHeight(), this.roundPaint);
        paramFile = Icon.createWithBitmap(localBitmap);
        return paramFile;
      }
    }
    catch (java.lang.Throwable paramFile)
    {
      FileLog.e(paramFile);
    }
    return null;
  }

  public List<ChooserTarget> onGetChooserTargets(ComponentName paramComponentName, IntentFilter paramIntentFilter)
  {
    paramComponentName = new ArrayList();
    if (!UserConfig.isClientActivated());
    do
      return paramComponentName;
    while (!ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).getBoolean("direct_share", true));
    ImageLoader.getInstance();
    paramIntentFilter = new Semaphore(0);
    ComponentName localComponentName = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramComponentName, localComponentName, paramIntentFilter)
    {
      public void run()
      {
        ArrayList localArrayList1 = new ArrayList();
        ArrayList localArrayList2 = new ArrayList();
        ArrayList localArrayList3 = new ArrayList();
        try
        {
          Object localObject1 = new ArrayList();
          ((ArrayList)localObject1).add(Integer.valueOf(UserConfig.getClientUserId()));
          localObject2 = new ArrayList();
          Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[] { Integer.valueOf(0), Integer.valueOf(30) }), new Object[0]);
          int i;
          while (true)
            if (((SQLiteCursor)localObject3).next())
            {
              long l = ((SQLiteCursor)localObject3).longValue(0);
              i = (int)l;
              j = (int)(l >> 32);
              if ((i == 0) || (j == 1))
                continue;
              if (i <= 0)
                break label451;
              if (((ArrayList)localObject1).contains(Integer.valueOf(i)))
                break;
              ((ArrayList)localObject1).add(Integer.valueOf(i));
            }
          while (true)
          {
            localArrayList1.add(Integer.valueOf(i));
            if (localArrayList1.size() != 8)
              break;
            ((SQLiteCursor)localObject3).dispose();
            if (!((ArrayList)localObject2).isEmpty())
              MessagesStorage.getInstance().getChatsInternal(TextUtils.join(",", (Iterable)localObject2), localArrayList2);
            if (!((ArrayList)localObject1).isEmpty())
              MessagesStorage.getInstance().getUsersInternal(TextUtils.join(",", (Iterable)localObject1), localArrayList3);
            i = 0;
            while (true)
            {
              if (i >= localArrayList1.size())
                break label619;
              localBundle = new Bundle();
              k = ((Integer)localArrayList1.get(i)).intValue();
              if (k <= 0)
                break label497;
              j = 0;
              if (j >= localArrayList3.size())
                break label648;
              localObject2 = (TLRPC.User)localArrayList3.get(j);
              if (((TLRPC.User)localObject2).id != k)
                break;
              if (((TLRPC.User)localObject2).bot)
                break label648;
              localBundle.putLong("dialogId", k);
              if ((((TLRPC.User)localObject2).photo == null) || (((TLRPC.User)localObject2).photo.photo_small == null))
                break label642;
              localObject1 = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(((TLRPC.User)localObject2).photo.photo_small, true));
              localObject3 = ContactsController.formatName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name);
              localObject2 = localObject1;
              localObject1 = localObject3;
              localObject3 = localObject1;
              localObject1 = localObject2;
              localObject2 = localObject3;
              if (localObject2 != null)
              {
                localObject3 = localObject1;
                if (localObject1 == null)
                  localObject3 = Icon.createWithResource(ApplicationLoader.applicationContext, 2130837903);
                this.val$targets.add(new ChooserTarget((CharSequence)localObject2, (Icon)localObject3, 1.0F, this.val$componentName, localBundle));
              }
              i += 1;
            }
            label451: j = -i;
            if (((ArrayList)localObject2).contains(Integer.valueOf(j)))
              continue;
            ((ArrayList)localObject2).add(Integer.valueOf(-i));
          }
        }
        catch (Exception localIcon)
        {
          while (true)
          {
            Bundle localBundle;
            int k;
            FileLog.e(localException);
            continue;
            j += 1;
            continue;
            label497: int j = 0;
            label499: if (j < localArrayList2.size())
            {
              localObject2 = (TLRPC.Chat)localArrayList2.get(j);
              if (((TLRPC.Chat)localObject2).id == -k)
              {
                if ((ChatObject.isNotInChat((TLRPC.Chat)localObject2)) || ((ChatObject.isChannel((TLRPC.Chat)localObject2)) && (!((TLRPC.Chat)localObject2).megagroup)))
                  break label633;
                localBundle.putLong("dialogId", k);
                if ((((TLRPC.Chat)localObject2).photo == null) || (((TLRPC.Chat)localObject2).photo.photo_small == null))
                  break label627;
              }
              for (localIcon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(((TLRPC.Chat)localObject2).photo.photo_small, true)); ; localIcon = null)
              {
                localObject2 = ((TLRPC.Chat)localObject2).title;
                break;
                j += 1;
                break label499;
                this.val$semaphore.release();
                return;
              }
            }
            label619: label627: label633: Object localObject2 = null;
            Icon localIcon = null;
            continue;
            label642: localIcon = null;
            continue;
            label648: localIcon = null;
            localObject2 = null;
          }
        }
      }
    });
    try
    {
      paramIntentFilter.acquire();
      return paramComponentName;
    }
    catch (Exception paramIntentFilter)
    {
      FileLog.e(paramIntentFilter);
    }
    return paramComponentName;
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.TgChooserTargetService
 * JD-Core Version:    0.6.0
 */