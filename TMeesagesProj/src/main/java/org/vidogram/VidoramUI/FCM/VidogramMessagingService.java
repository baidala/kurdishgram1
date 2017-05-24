package org.vidogram.VidogramUi.FCM;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.b.r.i;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.messaging.FirebaseMessagingService;
import itman.Vidofilm.a.h;
import itman.Vidofilm.a.u;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.vidogram.VidogramUi.WebRTC.c;
import org.vidogram.messenger.AndroidUtilities;
import org.vidogram.messenger.ApplicationLoader;
import org.vidogram.messenger.FileLoader;
import org.vidogram.messenger.FileLog;
import org.vidogram.messenger.ImageLoader;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MessagesController;
import org.vidogram.messenger.MessagesStorage;
import org.vidogram.messenger.UserConfig;
import org.vidogram.messenger.UserObject;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolveUsername;
import org.vidogram.tgnet.TLRPC.TL_contacts_resolvedPeer;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.UserProfilePhoto;
import org.vidogram.ui.LaunchActivity;

public class VidogramMessagingService extends FirebaseMessagingService
{
  private static final String a = VidogramMessagingService.class.getSimpleName();
  private String b;
  private String c;
  private String d;
  private int e;
  private boolean f = false;

  public static void a(int paramInt, boolean paramBoolean)
  {
    int i = 1;
    Object localObject5 = null;
    try
    {
      Object localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(paramInt));
      if (localObject1 == null)
        return;
      String str2 = UserObject.getUserName((TLRPC.User)localObject1);
      String str1;
      if (paramBoolean)
        str1 = LocaleController.getString("NotificationContactRejoinedVidogram", 2131166779);
      while (true)
      {
        new r.i(null).a(str2);
        Object localObject4 = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
        ((Intent)localObject4).setAction("com.tmessages.openchat" + Math.random() + 2147483647);
        ((Intent)localObject4).setFlags(32768);
        ((Intent)localObject4).putExtra("userId", ((TLRPC.User)localObject1).id);
        PendingIntent localPendingIntent = PendingIntent.getActivity(ApplicationLoader.applicationContext, 0, (Intent)localObject4, 1073741824);
        try
        {
          if ((((TLRPC.User)localObject1).photo != null) && (((TLRPC.User)localObject1).photo.photo_small != null) && (((TLRPC.User)localObject1).photo.photo_small.volume_id != 0L) && (((TLRPC.User)localObject1).photo.photo_small.local_id != 0))
          {
            localObject4 = ((TLRPC.User)localObject1).photo.photo_small;
            localObject1 = localObject5;
            if (localObject4 != null)
            {
              localObject1 = ImageLoader.getInstance().getImageFromMemory((TLObject)localObject4, null, "50_50");
              if (localObject1 != null)
                break label317;
            }
          }
        }
        catch (Exception localObject3)
        {
          while (true)
          {
            try
            {
              float f1 = 160.0F / AndroidUtilities.dp(50.0F);
              localObject1 = new BitmapFactory.Options();
              if (f1 >= 1.0F)
                continue;
              paramInt = i;
              ((BitmapFactory.Options)localObject1).inSampleSize = paramInt;
              localObject1 = BitmapFactory.decodeFile(FileLoader.getPathToAttach((TLObject)localObject4, true).toString(), (BitmapFactory.Options)localObject1);
              if (localObject1 == null)
                continue;
              new itman.Vidofilm.FCM.a(ApplicationLoader.applicationContext).a((Bitmap)localObject1, str2, str1, localPendingIntent);
              return;
              str1 = LocaleController.getString("NotificationContactJoinedVidogram", 2131166778);
              break;
              paramInt = (int)f1;
              continue;
              label317: localObject1 = ((BitmapDrawable)localObject1).getBitmap();
              continue;
              localException1 = localException1;
              Object localObject2 = localObject5;
              continue;
            }
            catch (Throwable localObject3)
            {
              Object localObject3 = localObject5;
              continue;
              localObject3 = null;
              continue;
            }
            localObject4 = null;
          }
        }
      }
    }
    catch (Exception localException2)
    {
    }
  }

  private void a(Map<String, String> paramMap)
  {
    try
    {
      this.e = Integer.parseInt((String)paramMap.get("type"));
      switch (this.e)
      {
      case 1:
        if (c.e())
          return;
        this.b = ((String)paramMap.get("room_number"));
        this.d = ((String)paramMap.get("caller_telegram_id"));
        this.c = ((String)paramMap.get("caller_number"));
        this.f = Boolean.parseBoolean((String)paramMap.get("is_video"));
        if (itman.Vidofilm.e.e.a(getApplicationContext()).a((String)paramMap.get("caller_telegram_id")) == null)
        {
          localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(Integer.parseInt((String)paramMap.get("caller_telegram_id"))));
          localObject2 = new u();
          if (localObject1 != null)
          {
            ((u)localObject2).c(((TLRPC.User)localObject1).first_name);
            ((u)localObject2).d(((TLRPC.User)localObject1).last_name);
          }
          ((u)localObject2).a((String)paramMap.get("caller_number"));
          ((u)localObject2).b((String)paramMap.get("caller_telegram_id"));
          itman.Vidofilm.e.e.a(getApplicationContext()).a((u)localObject2);
        }
        b();
        return;
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 3:
      case 9:
      }
    }
    catch (Exception paramMap)
    {
      FileLog.e(a + "Exception: " + paramMap.getMessage());
      FirebaseCrash.a(paramMap);
      return;
    }
    Object localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(Integer.parseInt((String)paramMap.get("telegram_id"))));
    Object localObject2 = new u();
    if (localObject1 != null)
    {
      ((u)localObject2).c(((TLRPC.User)localObject1).first_name);
      ((u)localObject2).d(((TLRPC.User)localObject1).last_name);
    }
    ((u)localObject2).a((String)paramMap.get("number"));
    ((u)localObject2).b((String)paramMap.get("telegram_id"));
    itman.Vidofilm.e.e.a(getApplicationContext()).a((u)localObject2);
    a(Integer.parseInt((String)paramMap.get("telegram_id")), Boolean.parseBoolean((String)paramMap.get("rejoin")));
    return;
    itman.Vidofilm.d.a.a(getApplicationContext()).a(1, null);
    return;
    itman.Vidofilm.b.a(getApplicationContext()).d("-1");
    return;
    itman.Vidofilm.d.e.a(getApplicationContext()).a();
    return;
    b(paramMap);
    return;
    c((String)paramMap.get("username"));
    return;
    FileLog.e("VidogramwebRTC : recived call");
    if (!c.e())
    {
      FileLog.e("VidogramwebRTC : recived call 1");
      this.b = ((String)paramMap.get("room_number"));
      FileLog.e("VidogramwebRTC : recived call 2");
      this.d = ((String)paramMap.get("caller_telegram_id"));
      localObject1 = (String)paramMap.get("turn_server");
      localObject2 = (String)paramMap.get("wss_url");
      String str1 = (String)paramMap.get("wss_post_url");
      String str2 = (String)paramMap.get("caller_number");
      if (itman.Vidofilm.e.e.a(getApplicationContext()).a((String)paramMap.get("caller_telegram_id")) == null)
      {
        TLRPC.User localUser = MessagesController.getInstance().getUser(Integer.valueOf(Integer.parseInt((String)paramMap.get("caller_telegram_id"))));
        u localu = new u();
        if (localUser != null)
        {
          localu.c(localUser.first_name);
          localu.d(localUser.last_name);
        }
        localu.a((String)paramMap.get("caller_number"));
        localu.b((String)paramMap.get("caller_telegram_id"));
        itman.Vidofilm.e.e.a(getApplicationContext()).a(localu);
      }
      FileLog.e("VidogramwebRTC : " + this.d + "/" + this.b);
      new org.vidogram.VidogramUi.WebRTC.e(getApplicationContext()).a(this.b, this.d, (String)localObject1, (String)localObject2, str1, str2);
      return;
      itman.Vidofilm.b.a(getApplicationContext()).g(Boolean.parseBoolean((String)paramMap.get("enable")));
      return;
      localObject1 = (String)paramMap.get("update_url");
      boolean bool1 = Boolean.parseBoolean((String)paramMap.get("update_enable"));
      boolean bool2 = Boolean.parseBoolean((String)paramMap.get("force_update_enable"));
      int i = Integer.parseInt((String)paramMap.get("force_update_version_code"));
      int j = Integer.parseInt((String)paramMap.get("update_version_code"));
      itman.Vidofilm.b.a(getApplicationContext()).a((String)localObject1, bool1, j, bool2, i);
      return;
      paramMap = (String)paramMap.get("invite_url");
      itman.Vidofilm.b.a(getApplicationContext()).h(paramMap);
      return;
      bool1 = Boolean.parseBoolean((String)paramMap.get("call_enable"));
      itman.Vidofilm.b.a(getApplicationContext()).h(bool1);
      return;
    }
  }

  private void b()
  {
    itman.Vidofilm.a.m localm = new itman.Vidofilm.a.m();
    localm.b(itman.Vidofilm.b.a(getApplicationContext()).k());
    localm.a(this.b);
    if (localm.a() == null)
    {
      itman.Vidofilm.d.d.a(getApplicationContext()).a(true);
      return;
    }
    ((itman.Vidofilm.c.b)itman.Vidofilm.c.a.a().a(itman.Vidofilm.c.b.class)).a(localm).a(new e.d()
    {
      public void onFailure(e.b<com.google.a.l> paramb, Throwable paramThrowable)
      {
        VidogramMessagingService.a(VidogramMessagingService.this, "-1");
      }

      public void onResponse(e.b<com.google.a.l> paramb, e.l<com.google.a.l> paraml)
      {
        if (paraml.b())
        {
          paramb = ((com.google.a.l)paraml.c()).toString();
          new org.vidogram.VidogramUi.WebRTC.e(VidogramMessagingService.this.getApplicationContext()).a(VidogramMessagingService.a(VidogramMessagingService.this), paramb, VidogramMessagingService.b(VidogramMessagingService.this), VidogramMessagingService.c(VidogramMessagingService.this), VidogramMessagingService.d(VidogramMessagingService.this));
        }
        do
          return;
        while (paraml.a() != 401);
        itman.Vidofilm.d.d.a(VidogramMessagingService.this.getApplicationContext()).a(true);
      }
    });
  }

  private void b(String paramString)
  {
    new org.vidogram.VidogramUi.WebRTC.e(getApplicationContext()).a(this.b, paramString, this.d, this.f, this.c);
  }

  private void b(Map<String, String> paramMap)
  {
    ArrayList localArrayList = new ArrayList();
    String str5 = (String)paramMap.get("big_image_url");
    String str6 = (String)paramMap.get("image_url");
    String str7 = (String)paramMap.get("message");
    String str8 = (String)paramMap.get("title");
    String str9 = (String)paramMap.get("timestamp");
    String str4 = (String)paramMap.get("intent_url");
    String str3 = (String)paramMap.get("intent1_url");
    String str2 = (String)paramMap.get("intent2_url");
    String str1 = (String)paramMap.get("intent3_url");
    boolean bool = Boolean.parseBoolean((String)paramMap.get("show_icon"));
    int i = 0;
    while (i < 4)
    {
      localArrayList.add(new h());
      i += 1;
    }
    ((h)localArrayList.get(1)).a((String)paramMap.get("intent1_name"));
    ((h)localArrayList.get(2)).a((String)paramMap.get("intent2_name"));
    ((h)localArrayList.get(3)).a((String)paramMap.get("intent3_name"));
    if (str4 == null)
    {
      paramMap = new Intent(getApplicationContext(), LaunchActivity.class);
      paramMap.putExtra("message", str7);
      ((h)localArrayList.get(0)).a(paramMap);
      if (str3 != null)
      {
        paramMap = str3;
        if (!str3.startsWith("https://"))
        {
          paramMap = str3;
          if (!str3.startsWith("http://"))
            paramMap = "http://" + str3;
        }
        ((h)localArrayList.get(1)).a(new Intent("android.intent.action.VIEW", Uri.parse(paramMap)));
      }
      if (str2 != null)
      {
        paramMap = str2;
        if (!str2.startsWith("https://"))
        {
          paramMap = str2;
          if (!str2.startsWith("http://"))
            paramMap = "http://" + str2;
        }
        ((h)localArrayList.get(2)).a(new Intent("android.intent.action.VIEW", Uri.parse(paramMap)));
      }
      if (str1 != null)
      {
        paramMap = str1;
        if (!str1.startsWith("https://"))
        {
          paramMap = str1;
          if (!str1.startsWith("http://"))
            paramMap = "http://" + str1;
        }
        ((h)localArrayList.get(3)).a(new Intent("android.intent.action.VIEW", Uri.parse(paramMap)));
      }
      new itman.Vidofilm.FCM.a(getApplicationContext()).a(str8, str7, str9, localArrayList, str6, str5, bool);
      return;
    }
    if ((!str4.startsWith("https://")) && (!str4.startsWith("http://")));
    for (paramMap = "http://" + str4; ; paramMap = str4)
    {
      ((h)localArrayList.get(0)).a(new Intent("android.intent.action.VIEW", Uri.parse(paramMap)));
      break;
    }
  }

  private void c(String paramString)
  {
    TLRPC.TL_contacts_resolveUsername localTL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
    localTL_contacts_resolveUsername.username = paramString;
    ConnectionsManager.getInstance().sendRequest(localTL_contacts_resolveUsername, new RequestDelegate()
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            if (this.a == null)
            {
              TLRPC.TL_contacts_resolvedPeer localTL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer)this.b;
              MessagesController.getInstance().putUsers(localTL_contacts_resolvedPeer.users, false);
              MessagesController.getInstance().putChats(localTL_contacts_resolvedPeer.chats, false);
              MessagesStorage.getInstance().putUsersAndChats(localTL_contacts_resolvedPeer.users, localTL_contacts_resolvedPeer.chats, false, true);
              if (!localTL_contacts_resolvedPeer.chats.isEmpty())
                MessagesController.getInstance().addUserToChat(((TLRPC.Chat)localTL_contacts_resolvedPeer.chats.get(0)).id, UserConfig.getCurrentUser(), null, 0, null, null);
            }
          }
        });
      }
    });
  }

  public void a(com.google.firebase.messaging.b paramb)
  {
    FileLog.e(a + "From: " + paramb.a());
    if (paramb == null);
    do
      return;
    while (paramb.b().size() <= 0);
    FileLog.e(a + "Data Payload: " + paramb.b().toString());
    try
    {
      a(paramb.b());
      return;
    }
    catch (Exception paramb)
    {
      FileLog.e(a + "Exception: " + paramb.getMessage());
      FirebaseCrash.a(paramb);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.VidogramUi.FCM.VidogramMessagingService
 * JD-Core Version:    0.6.0
 */