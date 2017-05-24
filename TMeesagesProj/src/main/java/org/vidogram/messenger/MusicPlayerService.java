package org.vidogram.messenger;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Build.VERSION;
import android.os.IBinder;
import android.support.v4.b.r.d;
import android.widget.RemoteViews;
import org.vidogram.messenger.audioinfo.AudioInfo;
import org.vidogram.ui.LaunchActivity;

public class MusicPlayerService extends Service
  implements NotificationCenter.NotificationCenterDelegate
{
  public static final String NOTIFY_CLOSE = "org.telegram.android.musicplayer.close";
  public static final String NOTIFY_NEXT = "org.telegram.android.musicplayer.next";
  public static final String NOTIFY_PAUSE = "org.telegram.android.musicplayer.pause";
  public static final String NOTIFY_PLAY = "org.telegram.android.musicplayer.play";
  public static final String NOTIFY_PREVIOUS = "org.telegram.android.musicplayer.previous";
  private static boolean supportBigNotifications;
  private static boolean supportLockScreenControls;
  private AudioManager audioManager;
  private RemoteControlClient remoteControlClient;

  static
  {
    boolean bool2 = true;
    if (Build.VERSION.SDK_INT >= 16)
    {
      bool1 = true;
      supportBigNotifications = bool1;
      if (Build.VERSION.SDK_INT < 14)
        break label36;
    }
    label36: for (boolean bool1 = bool2; ; bool1 = false)
    {
      supportLockScreenControls = bool1;
      return;
      bool1 = false;
      break;
    }
  }

  @SuppressLint({"NewApi"})
  private void createNotification(MessageObject paramMessageObject)
  {
    String str1 = paramMessageObject.getMusicTitle();
    String str2 = paramMessageObject.getMusicAuthor();
    AudioInfo localAudioInfo = MediaController.getInstance().getAudioInfo();
    RemoteViews localRemoteViews = new RemoteViews(getApplicationContext().getPackageName(), 2130903105);
    paramMessageObject = null;
    if (supportBigNotifications)
      paramMessageObject = new RemoteViews(getApplicationContext().getPackageName(), 2130903104);
    Object localObject = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
    ((Intent)localObject).setAction("com.tmessages.openplayer");
    ((Intent)localObject).setFlags(32768);
    localObject = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject, 0);
    localObject = new r.d(getApplicationContext()).a(2130838030).a((PendingIntent)localObject).a(str1).b();
    ((Notification)localObject).contentView = localRemoteViews;
    if (supportBigNotifications)
      ((Notification)localObject).bigContentView = paramMessageObject;
    setListeners(localRemoteViews);
    if (supportBigNotifications)
      setListeners(paramMessageObject);
    if (localAudioInfo != null)
      paramMessageObject = localAudioInfo.getSmallCover();
    while (true)
    {
      if (paramMessageObject != null)
      {
        ((Notification)localObject).contentView.setImageViewBitmap(2131558595, paramMessageObject);
        if (supportBigNotifications)
          ((Notification)localObject).bigContentView.setImageViewBitmap(2131558595, paramMessageObject);
        label212: if (!MediaController.getInstance().isDownloadingCurrentMessage())
          break label513;
        ((Notification)localObject).contentView.setViewVisibility(2131558601, 8);
        ((Notification)localObject).contentView.setViewVisibility(2131558602, 8);
        ((Notification)localObject).contentView.setViewVisibility(2131558603, 8);
        ((Notification)localObject).contentView.setViewVisibility(2131558600, 8);
        ((Notification)localObject).contentView.setViewVisibility(2131558599, 0);
        if (supportBigNotifications)
        {
          ((Notification)localObject).bigContentView.setViewVisibility(2131558601, 8);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558602, 8);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558603, 8);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558600, 8);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558599, 0);
        }
        label345: ((Notification)localObject).contentView.setTextViewText(2131558596, str1);
        ((Notification)localObject).contentView.setTextViewText(2131558598, str2);
        if (supportBigNotifications)
        {
          ((Notification)localObject).bigContentView.setTextViewText(2131558596, str1);
          ((Notification)localObject).bigContentView.setTextViewText(2131558598, str2);
        }
        ((Notification)localObject).flags |= 2;
        startForeground(5, (Notification)localObject);
        if (this.remoteControlClient != null)
        {
          paramMessageObject = this.remoteControlClient.editMetadata(true);
          paramMessageObject.putString(2, str2);
          paramMessageObject.putString(7, str1);
          if ((localAudioInfo == null) || (localAudioInfo.getCover() == null));
        }
      }
      try
      {
        paramMessageObject.putBitmap(100, localAudioInfo.getCover());
        paramMessageObject.apply();
        return;
        paramMessageObject = null;
        continue;
        ((Notification)localObject).contentView.setImageViewResource(2131558595, 2130837963);
        if (!supportBigNotifications)
          break label212;
        ((Notification)localObject).bigContentView.setImageViewResource(2131558595, 2130837962);
        break label212;
        label513: ((Notification)localObject).contentView.setViewVisibility(2131558599, 8);
        ((Notification)localObject).contentView.setViewVisibility(2131558603, 0);
        ((Notification)localObject).contentView.setViewVisibility(2131558600, 0);
        if (supportBigNotifications)
        {
          ((Notification)localObject).bigContentView.setViewVisibility(2131558603, 0);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558600, 0);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558599, 8);
        }
        if (MediaController.getInstance().isAudioPaused())
        {
          ((Notification)localObject).contentView.setViewVisibility(2131558601, 8);
          ((Notification)localObject).contentView.setViewVisibility(2131558602, 0);
          if (!supportBigNotifications)
            break label345;
          ((Notification)localObject).bigContentView.setViewVisibility(2131558601, 8);
          ((Notification)localObject).bigContentView.setViewVisibility(2131558602, 0);
          break label345;
        }
        ((Notification)localObject).contentView.setViewVisibility(2131558601, 0);
        ((Notification)localObject).contentView.setViewVisibility(2131558602, 8);
        if (!supportBigNotifications)
          break label345;
        ((Notification)localObject).bigContentView.setViewVisibility(2131558601, 0);
        ((Notification)localObject).bigContentView.setViewVisibility(2131558602, 8);
      }
      catch (Throwable localThrowable)
      {
        while (true)
          FileLog.e(localThrowable);
      }
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.audioPlayStateChanged)
    {
      paramArrayOfObject = MediaController.getInstance().getPlayingMessageObject();
      if (paramArrayOfObject != null)
        createNotification(paramArrayOfObject);
    }
    else
    {
      return;
    }
    stopSelf();
  }

  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCreate()
  {
    this.audioManager = ((AudioManager)getSystemService("audio"));
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioPlayStateChanged);
    super.onCreate();
  }

  @SuppressLint({"NewApi"})
  public void onDestroy()
  {
    super.onDestroy();
    if (this.remoteControlClient != null)
    {
      RemoteControlClient.MetadataEditor localMetadataEditor = this.remoteControlClient.editMetadata(true);
      localMetadataEditor.clear();
      localMetadataEditor.apply();
      this.audioManager.unregisterRemoteControlClient(this.remoteControlClient);
    }
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioProgressDidChanged);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioPlayStateChanged);
  }

  // ERROR //
  @SuppressLint({"NewApi"})
  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    // Byte code:
    //   0: invokestatic 69	org/vidogram/messenger/MediaController:getInstance	()Lorg/vidogram/messenger/MediaController;
    //   3: invokevirtual 239	org/vidogram/messenger/MediaController:getPlayingMessageObject	()Lorg/vidogram/messenger/MessageObject;
    //   6: astore_1
    //   7: aload_1
    //   8: ifnonnull +16 -> 24
    //   11: new 8	org/vidogram/messenger/MusicPlayerService$1
    //   14: dup
    //   15: aload_0
    //   16: invokespecial 289	org/vidogram/messenger/MusicPlayerService$1:<init>	(Lorg/vidogram/messenger/MusicPlayerService;)V
    //   19: invokestatic 295	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
    //   22: iconst_1
    //   23: ireturn
    //   24: getstatic 43	org/vidogram/messenger/MusicPlayerService:supportLockScreenControls	Z
    //   27: ifeq +98 -> 125
    //   30: new 297	android/content/ComponentName
    //   33: dup
    //   34: aload_0
    //   35: invokevirtual 79	org/vidogram/messenger/MusicPlayerService:getApplicationContext	()Landroid/content/Context;
    //   38: ldc_w 299
    //   41: invokevirtual 304	java/lang/Class:getName	()Ljava/lang/String;
    //   44: invokespecial 307	android/content/ComponentName:<init>	(Landroid/content/Context;Ljava/lang/String;)V
    //   47: astore 4
    //   49: aload_0
    //   50: getfield 191	org/vidogram/messenger/MusicPlayerService:remoteControlClient	Landroid/media/RemoteControlClient;
    //   53: ifnonnull +62 -> 115
    //   56: aload_0
    //   57: getfield 257	org/vidogram/messenger/MusicPlayerService:audioManager	Landroid/media/AudioManager;
    //   60: aload 4
    //   62: invokevirtual 311	android/media/AudioManager:registerMediaButtonEventReceiver	(Landroid/content/ComponentName;)V
    //   65: new 91	android/content/Intent
    //   68: dup
    //   69: ldc_w 313
    //   72: invokespecial 316	android/content/Intent:<init>	(Ljava/lang/String;)V
    //   75: astore 5
    //   77: aload 5
    //   79: aload 4
    //   81: invokevirtual 320	android/content/Intent:setComponent	(Landroid/content/ComponentName;)Landroid/content/Intent;
    //   84: pop
    //   85: aload_0
    //   86: new 193	android/media/RemoteControlClient
    //   89: dup
    //   90: aload_0
    //   91: iconst_0
    //   92: aload 5
    //   94: iconst_0
    //   95: invokestatic 323	android/app/PendingIntent:getBroadcast	(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;
    //   98: invokespecial 326	android/media/RemoteControlClient:<init>	(Landroid/app/PendingIntent;)V
    //   101: putfield 191	org/vidogram/messenger/MusicPlayerService:remoteControlClient	Landroid/media/RemoteControlClient;
    //   104: aload_0
    //   105: getfield 257	org/vidogram/messenger/MusicPlayerService:audioManager	Landroid/media/AudioManager;
    //   108: aload_0
    //   109: getfield 191	org/vidogram/messenger/MusicPlayerService:remoteControlClient	Landroid/media/RemoteControlClient;
    //   112: invokevirtual 329	android/media/AudioManager:registerRemoteControlClient	(Landroid/media/RemoteControlClient;)V
    //   115: aload_0
    //   116: getfield 191	org/vidogram/messenger/MusicPlayerService:remoteControlClient	Landroid/media/RemoteControlClient;
    //   119: sipush 189
    //   122: invokevirtual 333	android/media/RemoteControlClient:setTransportControlFlags	(I)V
    //   125: aload_0
    //   126: aload_1
    //   127: invokespecial 241	org/vidogram/messenger/MusicPlayerService:createNotification	(Lorg/vidogram/messenger/MessageObject;)V
    //   130: iconst_1
    //   131: ireturn
    //   132: astore_1
    //   133: aload_1
    //   134: invokevirtual 336	java/lang/Exception:printStackTrace	()V
    //   137: iconst_1
    //   138: ireturn
    //   139: astore 4
    //   141: aload 4
    //   143: invokestatic 227	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   146: goto -21 -> 125
    //
    // Exception table:
    //   from	to	target	type
    //   0	7	132	java/lang/Exception
    //   11	22	132	java/lang/Exception
    //   24	49	132	java/lang/Exception
    //   125	130	132	java/lang/Exception
    //   141	146	132	java/lang/Exception
    //   49	115	139	java/lang/Exception
    //   115	125	139	java/lang/Exception
  }

  public void setListeners(RemoteViews paramRemoteViews)
  {
    paramRemoteViews.setOnClickPendingIntent(2131558600, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.previous"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(2131558597, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.close"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(2131558601, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.pause"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(2131558603, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.next"), 134217728));
    paramRemoteViews.setOnClickPendingIntent(2131558602, PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent("org.telegram.android.musicplayer.play"), 134217728));
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.MusicPlayerService
 * JD-Core Version:    0.6.0
 */