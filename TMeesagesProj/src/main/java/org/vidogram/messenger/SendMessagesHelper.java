package org.vidogram.messenger;

import B;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.c.a.a.e;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.vidogram.messenger.audioinfo.AudioInfo;
import org.vidogram.messenger.query.SearchQuery;
import org.vidogram.messenger.query.StickersQuery;
import org.vidogram.tgnet.ConnectionsManager;
import org.vidogram.tgnet.QuickAckDelegate;
import org.vidogram.tgnet.RequestDelegate;
import org.vidogram.tgnet.SerializedData;
import org.vidogram.tgnet.TLObject;
import org.vidogram.tgnet.TLRPC.BotInlineMessage;
import org.vidogram.tgnet.TLRPC.BotInlineResult;
import org.vidogram.tgnet.TLRPC.Chat;
import org.vidogram.tgnet.TLRPC.ChatFull;
import org.vidogram.tgnet.TLRPC.Document;
import org.vidogram.tgnet.TLRPC.DocumentAttribute;
import org.vidogram.tgnet.TLRPC.EncryptedChat;
import org.vidogram.tgnet.TLRPC.FileLocation;
import org.vidogram.tgnet.TLRPC.InputDocument;
import org.vidogram.tgnet.TLRPC.InputEncryptedFile;
import org.vidogram.tgnet.TLRPC.InputFile;
import org.vidogram.tgnet.TLRPC.InputMedia;
import org.vidogram.tgnet.TLRPC.InputPeer;
import org.vidogram.tgnet.TLRPC.KeyboardButton;
import org.vidogram.tgnet.TLRPC.Message;
import org.vidogram.tgnet.TLRPC.MessageAction;
import org.vidogram.tgnet.TLRPC.MessageEntity;
import org.vidogram.tgnet.TLRPC.MessageMedia;
import org.vidogram.tgnet.TLRPC.Peer;
import org.vidogram.tgnet.TLRPC.Photo;
import org.vidogram.tgnet.TLRPC.PhotoSize;
import org.vidogram.tgnet.TLRPC.ReplyMarkup;
import org.vidogram.tgnet.TLRPC.TL_botInlineMediaResult;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaAuto;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaContact;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageMediaVenue;
import org.vidogram.tgnet.TLRPC.TL_botInlineMessageText;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessage;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionAbortKey;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionAcceptKey;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionCommitKey;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionDeleteMessages;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionFlushHistory;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionNoop;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionNotifyLayer;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionReadMessages;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionRequestKey;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionResend;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionScreenshotMessages;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionSetMessageTTL;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageActionTyping;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_decryptedMessageMediaVideo;
import org.vidogram.tgnet.TLRPC.TL_document;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAnimated;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.vidogram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.vidogram.tgnet.TLRPC.TL_error;
import org.vidogram.tgnet.TLRPC.TL_fileLocationUnavailable;
import org.vidogram.tgnet.TLRPC.TL_game;
import org.vidogram.tgnet.TLRPC.TL_geoPoint;
import org.vidogram.tgnet.TLRPC.TL_inputPeerChannel;
import org.vidogram.tgnet.TLRPC.TL_inputPeerEmpty;
import org.vidogram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonBuy;
import org.vidogram.tgnet.TLRPC.TL_keyboardButtonGame;
import org.vidogram.tgnet.TLRPC.TL_message;
import org.vidogram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.vidogram.tgnet.TLRPC.TL_messageEntityUrl;
import org.vidogram.tgnet.TLRPC.TL_messageFwdHeader;
import org.vidogram.tgnet.TLRPC.TL_messageMediaContact;
import org.vidogram.tgnet.TLRPC.TL_messageMediaDocument;
import org.vidogram.tgnet.TLRPC.TL_messageMediaEmpty;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGame;
import org.vidogram.tgnet.TLRPC.TL_messageMediaGeo;
import org.vidogram.tgnet.TLRPC.TL_messageMediaInvoice;
import org.vidogram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.vidogram.tgnet.TLRPC.TL_messageMediaVenue;
import org.vidogram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.vidogram.tgnet.TLRPC.TL_messages_botCallbackAnswer;
import org.vidogram.tgnet.TLRPC.TL_messages_editMessage;
import org.vidogram.tgnet.TLRPC.TL_messages_forwardMessages;
import org.vidogram.tgnet.TLRPC.TL_messages_getBotCallbackAnswer;
import org.vidogram.tgnet.TLRPC.TL_messages_sendBroadcast;
import org.vidogram.tgnet.TLRPC.TL_messages_sendMedia;
import org.vidogram.tgnet.TLRPC.TL_messages_sendMessage;
import org.vidogram.tgnet.TLRPC.TL_payments_getPaymentForm;
import org.vidogram.tgnet.TLRPC.TL_payments_getPaymentReceipt;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentForm;
import org.vidogram.tgnet.TLRPC.TL_payments_paymentReceipt;
import org.vidogram.tgnet.TLRPC.TL_peerChannel;
import org.vidogram.tgnet.TLRPC.TL_photo;
import org.vidogram.tgnet.TLRPC.TL_photoCachedSize;
import org.vidogram.tgnet.TLRPC.TL_photoSize;
import org.vidogram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.vidogram.tgnet.TLRPC.TL_updateMessageID;
import org.vidogram.tgnet.TLRPC.TL_updateNewChannelMessage;
import org.vidogram.tgnet.TLRPC.TL_updateNewMessage;
import org.vidogram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.vidogram.tgnet.TLRPC.TL_user;
import org.vidogram.tgnet.TLRPC.TL_userContact_old2;
import org.vidogram.tgnet.TLRPC.TL_webPagePending;
import org.vidogram.tgnet.TLRPC.Update;
import org.vidogram.tgnet.TLRPC.Updates;
import org.vidogram.tgnet.TLRPC.User;
import org.vidogram.tgnet.TLRPC.WebPage;
import org.vidogram.ui.ActionBar.AlertDialog.Builder;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ChatActivity;
import org.vidogram.ui.Components.AlertsCreator;
import org.vidogram.ui.PaymentFormActivity;

public class SendMessagesHelper
  implements NotificationCenter.NotificationCenterDelegate
{
  private static volatile SendMessagesHelper Instance = null;
  private TLRPC.ChatFull currentChatInfo = null;
  private HashMap<String, ArrayList<DelayedMessage>> delayedMessages = new HashMap();
  private LocationProvider locationProvider = new LocationProvider(new SendMessagesHelper.LocationProvider.LocationProviderDelegate()
  {
    public void onLocationAcquired(Location paramLocation)
    {
      SendMessagesHelper.this.sendLocation(paramLocation);
      SendMessagesHelper.this.waitingForLocation.clear();
    }

    public void onUnableLocationAcquire()
    {
      HashMap localHashMap = new HashMap(SendMessagesHelper.this.waitingForLocation);
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.wasUnableToFindCurrentLocation, new Object[] { localHashMap });
      SendMessagesHelper.this.waitingForLocation.clear();
    }
  });
  private HashMap<Integer, TLRPC.Message> sendingMessages = new HashMap();
  private HashMap<Integer, MessageObject> unsentMessages = new HashMap();
  private HashMap<String, MessageObject> waitingForCallback = new HashMap();
  private HashMap<String, MessageObject> waitingForLocation = new HashMap();

  public SendMessagesHelper()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailUpload);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingStarted);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileNewChunkAvailable);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FilePreparingFailed);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidFailedLoad);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.httpFileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
  }

  // ERROR //
  private static void fillVideoAttribute(String paramString, TLRPC.TL_documentAttributeVideo paramTL_documentAttributeVideo, VideoEditedInfo paramVideoEditedInfo)
  {
    // Byte code:
    //   0: new 237	android/media/MediaMetadataRetriever
    //   3: dup
    //   4: invokespecial 238	android/media/MediaMetadataRetriever:<init>	()V
    //   7: astore 6
    //   9: aload 6
    //   11: astore 5
    //   13: aload 6
    //   15: aload_0
    //   16: invokevirtual 242	android/media/MediaMetadataRetriever:setDataSource	(Ljava/lang/String;)V
    //   19: aload 6
    //   21: astore 5
    //   23: aload 6
    //   25: bipush 18
    //   27: invokevirtual 246	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   30: astore 7
    //   32: aload 7
    //   34: ifnull +16 -> 50
    //   37: aload 6
    //   39: astore 5
    //   41: aload_1
    //   42: aload 7
    //   44: invokestatic 252	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   47: putfield 257	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   50: aload 6
    //   52: astore 5
    //   54: aload 6
    //   56: bipush 19
    //   58: invokevirtual 246	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   61: astore 7
    //   63: aload 7
    //   65: ifnull +16 -> 81
    //   68: aload 6
    //   70: astore 5
    //   72: aload_1
    //   73: aload 7
    //   75: invokestatic 252	java/lang/Integer:parseInt	(Ljava/lang/String;)I
    //   78: putfield 260	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   81: aload 6
    //   83: astore 5
    //   85: aload 6
    //   87: bipush 9
    //   89: invokevirtual 246	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   92: astore 7
    //   94: aload 7
    //   96: ifnull +26 -> 122
    //   99: aload 6
    //   101: astore 5
    //   103: aload_1
    //   104: aload 7
    //   106: invokestatic 266	java/lang/Long:parseLong	(Ljava/lang/String;)J
    //   109: l2f
    //   110: ldc_w 267
    //   113: fdiv
    //   114: f2d
    //   115: invokestatic 273	java/lang/Math:ceil	(D)D
    //   118: d2i
    //   119: putfield 276	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   122: aload 6
    //   124: astore 5
    //   126: getstatic 281	android/os/Build$VERSION:SDK_INT	I
    //   129: bipush 17
    //   131: if_icmplt +47 -> 178
    //   134: aload 6
    //   136: astore 5
    //   138: aload 6
    //   140: bipush 24
    //   142: invokevirtual 246	android/media/MediaMetadataRetriever:extractMetadata	(I)Ljava/lang/String;
    //   145: astore 7
    //   147: aload 7
    //   149: ifnull +29 -> 178
    //   152: aload 6
    //   154: astore 5
    //   156: aload 7
    //   158: invokestatic 286	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   161: invokevirtual 290	java/lang/Integer:intValue	()I
    //   164: istore_3
    //   165: aload_2
    //   166: ifnull +96 -> 262
    //   169: aload 6
    //   171: astore 5
    //   173: aload_2
    //   174: iload_3
    //   175: putfield 295	org/vidogram/messenger/VideoEditedInfo:rotationValue	I
    //   178: iconst_1
    //   179: istore 4
    //   181: iload 4
    //   183: istore_3
    //   184: aload 6
    //   186: ifnull +11 -> 197
    //   189: aload 6
    //   191: invokevirtual 298	android/media/MediaMetadataRetriever:release	()V
    //   194: iload 4
    //   196: istore_3
    //   197: iload_3
    //   198: ifne +63 -> 261
    //   201: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   204: new 306	java/io/File
    //   207: dup
    //   208: aload_0
    //   209: invokespecial 308	java/io/File:<init>	(Ljava/lang/String;)V
    //   212: invokestatic 314	android/net/Uri:fromFile	(Ljava/io/File;)Landroid/net/Uri;
    //   215: invokestatic 320	android/media/MediaPlayer:create	(Landroid/content/Context;Landroid/net/Uri;)Landroid/media/MediaPlayer;
    //   218: astore_0
    //   219: aload_0
    //   220: ifnull +41 -> 261
    //   223: aload_1
    //   224: aload_0
    //   225: invokevirtual 323	android/media/MediaPlayer:getDuration	()I
    //   228: i2f
    //   229: ldc_w 267
    //   232: fdiv
    //   233: f2d
    //   234: invokestatic 273	java/lang/Math:ceil	(D)D
    //   237: d2i
    //   238: putfield 276	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:duration	I
    //   241: aload_1
    //   242: aload_0
    //   243: invokevirtual 326	android/media/MediaPlayer:getVideoWidth	()I
    //   246: putfield 257	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   249: aload_1
    //   250: aload_0
    //   251: invokevirtual 329	android/media/MediaPlayer:getVideoHeight	()I
    //   254: putfield 260	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   257: aload_0
    //   258: invokevirtual 330	android/media/MediaPlayer:release	()V
    //   261: return
    //   262: iload_3
    //   263: bipush 90
    //   265: if_icmpeq +10 -> 275
    //   268: iload_3
    //   269: sipush 270
    //   272: if_icmpne -94 -> 178
    //   275: aload 6
    //   277: astore 5
    //   279: aload_1
    //   280: getfield 257	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   283: istore_3
    //   284: aload 6
    //   286: astore 5
    //   288: aload_1
    //   289: aload_1
    //   290: getfield 260	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   293: putfield 257	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:w	I
    //   296: aload 6
    //   298: astore 5
    //   300: aload_1
    //   301: iload_3
    //   302: putfield 260	org/vidogram/tgnet/TLRPC$TL_documentAttributeVideo:h	I
    //   305: goto -127 -> 178
    //   308: astore 5
    //   310: aload 6
    //   312: astore_2
    //   313: aload 5
    //   315: astore 6
    //   317: aload_2
    //   318: astore 5
    //   320: aload 6
    //   322: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   325: aload_2
    //   326: ifnull +7 -> 333
    //   329: aload_2
    //   330: invokevirtual 298	android/media/MediaMetadataRetriever:release	()V
    //   333: iconst_0
    //   334: istore_3
    //   335: goto -138 -> 197
    //   338: astore_2
    //   339: aload_2
    //   340: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   343: iload 4
    //   345: istore_3
    //   346: goto -149 -> 197
    //   349: astore_2
    //   350: aload_2
    //   351: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   354: iconst_0
    //   355: istore_3
    //   356: goto -159 -> 197
    //   359: astore_0
    //   360: aconst_null
    //   361: astore 5
    //   363: aload 5
    //   365: ifnull +8 -> 373
    //   368: aload 5
    //   370: invokevirtual 298	android/media/MediaMetadataRetriever:release	()V
    //   373: aload_0
    //   374: athrow
    //   375: astore_1
    //   376: aload_1
    //   377: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   380: goto -7 -> 373
    //   383: astore_0
    //   384: aload_0
    //   385: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   388: return
    //   389: astore_0
    //   390: goto -27 -> 363
    //   393: astore 6
    //   395: aconst_null
    //   396: astore_2
    //   397: goto -80 -> 317
    //
    // Exception table:
    //   from	to	target	type
    //   13	19	308	java/lang/Exception
    //   23	32	308	java/lang/Exception
    //   41	50	308	java/lang/Exception
    //   54	63	308	java/lang/Exception
    //   72	81	308	java/lang/Exception
    //   85	94	308	java/lang/Exception
    //   103	122	308	java/lang/Exception
    //   126	134	308	java/lang/Exception
    //   138	147	308	java/lang/Exception
    //   156	165	308	java/lang/Exception
    //   173	178	308	java/lang/Exception
    //   279	284	308	java/lang/Exception
    //   288	296	308	java/lang/Exception
    //   300	305	308	java/lang/Exception
    //   189	194	338	java/lang/Exception
    //   329	333	349	java/lang/Exception
    //   0	9	359	finally
    //   368	373	375	java/lang/Exception
    //   201	219	383	java/lang/Exception
    //   223	261	383	java/lang/Exception
    //   13	19	389	finally
    //   23	32	389	finally
    //   41	50	389	finally
    //   54	63	389	finally
    //   72	81	389	finally
    //   85	94	389	finally
    //   103	122	389	finally
    //   126	134	389	finally
    //   138	147	389	finally
    //   156	165	389	finally
    //   173	178	389	finally
    //   279	284	389	finally
    //   288	296	389	finally
    //   300	305	389	finally
    //   320	325	389	finally
    //   0	9	393	java/lang/Exception
  }

  public static SendMessagesHelper getInstance()
  {
    Object localObject1 = Instance;
    if (localObject1 == null)
    {
      monitorenter;
      try
      {
        SendMessagesHelper localSendMessagesHelper = Instance;
        localObject1 = localSendMessagesHelper;
        if (localSendMessagesHelper == null)
        {
          localObject1 = new SendMessagesHelper();
          Instance = (SendMessagesHelper)localObject1;
        }
        return localObject1;
      }
      finally
      {
        monitorexit;
      }
    }
    return (SendMessagesHelper)localObject2;
  }

  private static String getTrimmedString(String paramString)
  {
    String str = paramString.trim();
    if (str.length() == 0)
    {
      paramString = str;
      return paramString;
    }
    while (true)
    {
      str = paramString;
      if (!paramString.startsWith("\n"))
        break;
      paramString = paramString.substring(1);
    }
    while (true)
    {
      paramString = str;
      if (!str.endsWith("\n"))
        break;
      str = str.substring(0, str.length() - 1);
    }
  }

  private void performSendDelayedMessage(DelayedMessage paramDelayedMessage)
  {
    boolean bool1 = true;
    boolean bool2 = false;
    if (paramDelayedMessage.type == 0)
      if (paramDelayedMessage.httpLocation != null)
      {
        putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
        ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "file");
      }
    label882: label887: 
    do
    {
      while (true)
      {
        return;
        if (paramDelayedMessage.sendRequest != null)
        {
          localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
          putToDelayedMessages((String)localObject1, paramDelayedMessage);
          FileLoader.getInstance().uploadFile((String)localObject1, false, true, 16777216);
          return;
        }
        localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location).toString();
        Object localObject3 = localObject1;
        if (paramDelayedMessage.sendEncryptedRequest != null)
        {
          localObject3 = localObject1;
          if (paramDelayedMessage.location.dc_id != 0)
          {
            localObject3 = new File((String)localObject1);
            localObject2 = localObject3;
            if (!((File)localObject3).exists())
            {
              localObject1 = FileLoader.getPathToAttach(paramDelayedMessage.location, true).toString();
              localObject2 = new File((String)localObject1);
            }
            localObject3 = localObject1;
            if (!((File)localObject2).exists())
            {
              putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.location), paramDelayedMessage);
              FileLoader.getInstance().loadFile(paramDelayedMessage.location, "jpg", 0, false);
              return;
            }
          }
        }
        putToDelayedMessages((String)localObject3, paramDelayedMessage);
        FileLoader.getInstance().uploadFile((String)localObject3, true, true, 16777216);
        return;
        if (paramDelayedMessage.type == 1)
        {
          if (paramDelayedMessage.videoEditedInfo != null)
          {
            localObject2 = paramDelayedMessage.obj.messageOwner.attachPath;
            localObject1 = localObject2;
            if (localObject2 == null)
              localObject1 = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
            putToDelayedMessages((String)localObject1, paramDelayedMessage);
            MediaController.getInstance().scheduleVideoConvert(paramDelayedMessage.obj);
            return;
          }
          if (paramDelayedMessage.sendRequest != null)
          {
            if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
              localObject1 = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
            while (((TLRPC.InputMedia)localObject1).file == null)
            {
              localObject2 = paramDelayedMessage.obj.messageOwner.attachPath;
              localObject1 = localObject2;
              if (localObject2 == null)
                localObject1 = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
              putToDelayedMessages((String)localObject1, paramDelayedMessage);
              if (paramDelayedMessage.obj.videoEditedInfo != null)
              {
                FileLoader.getInstance().uploadFile((String)localObject1, false, false, paramDelayedMessage.documentLocation.size, 33554432);
                return;
                localObject1 = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
                continue;
              }
              FileLoader.getInstance().uploadFile((String)localObject1, false, false, 33554432);
              return;
            }
            localObject1 = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
            putToDelayedMessages((String)localObject1, paramDelayedMessage);
            FileLoader.getInstance().uploadFile((String)localObject1, false, true, 16777216);
            return;
          }
          localObject2 = paramDelayedMessage.obj.messageOwner.attachPath;
          localObject1 = localObject2;
          if (localObject2 == null)
            localObject1 = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.documentLocation.id + ".mp4";
          if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.documentLocation.dc_id != 0) && (!new File((String)localObject1).exists()))
          {
            putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.documentLocation), paramDelayedMessage);
            FileLoader.getInstance().loadFile(paramDelayedMessage.documentLocation, true, false);
            return;
          }
          putToDelayedMessages((String)localObject1, paramDelayedMessage);
          if (paramDelayedMessage.obj.videoEditedInfo != null)
          {
            FileLoader.getInstance().uploadFile((String)localObject1, true, false, paramDelayedMessage.documentLocation.size, 33554432);
            return;
          }
          FileLoader.getInstance().uploadFile((String)localObject1, true, false, 33554432);
          return;
        }
        if (paramDelayedMessage.type != 2)
          break label1073;
        if (paramDelayedMessage.httpLocation != null)
        {
          putToDelayedMessages(paramDelayedMessage.httpLocation, paramDelayedMessage);
          ImageLoader.getInstance().loadHttpFile(paramDelayedMessage.httpLocation, "gif");
          return;
        }
        if (paramDelayedMessage.sendRequest == null)
          break;
        if ((paramDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
        {
          localObject1 = ((TLRPC.TL_messages_sendMedia)paramDelayedMessage.sendRequest).media;
          if (((TLRPC.InputMedia)localObject1).file != null)
            break label887;
          localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
          putToDelayedMessages((String)localObject1, paramDelayedMessage);
          localObject2 = FileLoader.getInstance();
          if (paramDelayedMessage.sendRequest != null)
            break label882;
        }
        while (true)
        {
          ((FileLoader)localObject2).uploadFile((String)localObject1, bool1, false, 67108864);
          return;
          localObject1 = ((TLRPC.TL_messages_sendBroadcast)paramDelayedMessage.sendRequest).media;
          break;
          bool1 = false;
        }
        if ((((TLRPC.InputMedia)localObject1).thumb != null) || (paramDelayedMessage.location == null))
          continue;
        localObject1 = FileLoader.getInstance().getDirectory(4) + "/" + paramDelayedMessage.location.volume_id + "_" + paramDelayedMessage.location.local_id + ".jpg";
        putToDelayedMessages((String)localObject1, paramDelayedMessage);
        FileLoader.getInstance().uploadFile((String)localObject1, false, true, 16777216);
        return;
      }
      localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
      if ((paramDelayedMessage.sendEncryptedRequest != null) && (paramDelayedMessage.documentLocation.dc_id != 0) && (!new File((String)localObject1).exists()))
      {
        putToDelayedMessages(FileLoader.getAttachFileName(paramDelayedMessage.documentLocation), paramDelayedMessage);
        FileLoader.getInstance().loadFile(paramDelayedMessage.documentLocation, true, false);
        return;
      }
      putToDelayedMessages((String)localObject1, paramDelayedMessage);
      FileLoader.getInstance().uploadFile((String)localObject1, true, false, 67108864);
      return;
    }
    while (paramDelayedMessage.type != 3);
    label1073: Object localObject1 = paramDelayedMessage.obj.messageOwner.attachPath;
    putToDelayedMessages((String)localObject1, paramDelayedMessage);
    Object localObject2 = FileLoader.getInstance();
    bool1 = bool2;
    if (paramDelayedMessage.sendRequest == null)
      bool1 = true;
    ((FileLoader)localObject2).uploadFile((String)localObject1, bool1, true, 50331648);
  }

  private void performSendMessageRequest(TLObject paramTLObject, MessageObject paramMessageObject, String paramString)
  {
    TLRPC.Message localMessage = paramMessageObject.messageOwner;
    putToSendingMessages(localMessage);
    ConnectionsManager localConnectionsManager = ConnectionsManager.getInstance();
    paramMessageObject = new RequestDelegate(localMessage, paramTLObject, paramMessageObject, paramString)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error, paramTLObject)
        {
          public void run()
          {
            int k;
            boolean bool2;
            ArrayList localArrayList;
            String str;
            Object localObject1;
            Object localObject2;
            Object localObject3;
            int i;
            if (this.val$error == null)
            {
              k = SendMessagesHelper.9.this.val$newMsgObj.id;
              bool2 = SendMessagesHelper.9.this.val$req instanceof TLRPC.TL_messages_sendBroadcast;
              localArrayList = new ArrayList();
              str = SendMessagesHelper.9.this.val$newMsgObj.attachPath;
              if ((this.val$response instanceof TLRPC.TL_updateShortSentMessage))
              {
                localObject1 = (TLRPC.TL_updateShortSentMessage)this.val$response;
                localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                localObject3 = SendMessagesHelper.9.this.val$newMsgObj;
                i = ((TLRPC.TL_updateShortSentMessage)localObject1).id;
                ((TLRPC.Message)localObject3).id = i;
                ((TLRPC.Message)localObject2).local_id = i;
                SendMessagesHelper.9.this.val$newMsgObj.date = ((TLRPC.TL_updateShortSentMessage)localObject1).date;
                SendMessagesHelper.9.this.val$newMsgObj.entities = ((TLRPC.TL_updateShortSentMessage)localObject1).entities;
                SendMessagesHelper.9.this.val$newMsgObj.out = ((TLRPC.TL_updateShortSentMessage)localObject1).out;
                if (((TLRPC.TL_updateShortSentMessage)localObject1).media != null)
                {
                  SendMessagesHelper.9.this.val$newMsgObj.media = ((TLRPC.TL_updateShortSentMessage)localObject1).media;
                  localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 512;
                }
                if (((((TLRPC.TL_updateShortSentMessage)localObject1).media instanceof TLRPC.TL_messageMediaGame)) && (!TextUtils.isEmpty(((TLRPC.TL_updateShortSentMessage)localObject1).message)))
                  SendMessagesHelper.9.this.val$newMsgObj.message = ((TLRPC.TL_updateShortSentMessage)localObject1).message;
                if (!SendMessagesHelper.9.this.val$newMsgObj.entities.isEmpty())
                {
                  localObject2 = SendMessagesHelper.9.this.val$newMsgObj;
                  ((TLRPC.Message)localObject2).flags |= 128;
                }
                Utilities.stageQueue.postRunnable(new Runnable((TLRPC.TL_updateShortSentMessage)localObject1)
                {
                  public void run()
                  {
                    MessagesController.getInstance().processNewDifferenceParams(-1, this.val$res.pts, this.val$res.date, this.val$res.pts_count);
                  }
                });
                localArrayList.add(SendMessagesHelper.9.this.val$newMsgObj);
                i = 0;
              }
            }
            while (true)
            {
              int j;
              label347: TLRPC.Updates localUpdates;
              if (i == 0)
              {
                StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, 1);
                SendMessagesHelper.9.this.val$newMsgObj.send_state = 0;
                localObject1 = NotificationCenter.getInstance();
                int m = NotificationCenter.messageReceivedByServer;
                if (bool2)
                {
                  j = k;
                  ((NotificationCenter)localObject1).postNotificationName(m, new Object[] { Integer.valueOf(k), Integer.valueOf(j), SendMessagesHelper.9.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.9.this.val$newMsgObj.dialog_id) });
                  MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(k, bool2, localArrayList, str)
                  {
                    public void run()
                    {
                      Object localObject = MessagesStorage.getInstance();
                      long l = SendMessagesHelper.9.this.val$newMsgObj.random_id;
                      int j = this.val$oldId;
                      if (this.val$isBroadcast);
                      for (int i = this.val$oldId; ; i = SendMessagesHelper.9.this.val$newMsgObj.id)
                      {
                        ((MessagesStorage)localObject).updateMessageStateAndId(l, Integer.valueOf(j), i, 0, false, SendMessagesHelper.9.this.val$newMsgObj.to_id.channel_id);
                        MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, this.val$isBroadcast, 0);
                        if (this.val$isBroadcast)
                        {
                          localObject = new ArrayList();
                          ((ArrayList)localObject).add(SendMessagesHelper.9.this.val$newMsgObj);
                          MessagesStorage.getInstance().putMessages((ArrayList)localObject, true, false, false, 0);
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            if (SendMessagesHelper.9.1.5.this.val$isBroadcast)
                            {
                              i = 0;
                              while (i < SendMessagesHelper.9.1.5.this.val$sentMessages.size())
                              {
                                Object localObject2 = (TLRPC.Message)SendMessagesHelper.9.1.5.this.val$sentMessages.get(i);
                                localObject1 = new ArrayList();
                                localObject2 = new MessageObject((TLRPC.Message)localObject2, null, false);
                                ((ArrayList)localObject1).add(localObject2);
                                MessagesController.getInstance().updateInterfaceWithMessages(((MessageObject)localObject2).getDialogId(), (ArrayList)localObject1, true);
                                i += 1;
                              }
                              NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
                            }
                            SearchQuery.increasePeerRaiting(SendMessagesHelper.9.this.val$newMsgObj.dialog_id);
                            Object localObject1 = NotificationCenter.getInstance();
                            int j = NotificationCenter.messageReceivedByServer;
                            int k = SendMessagesHelper.9.1.5.this.val$oldId;
                            if (SendMessagesHelper.9.1.5.this.val$isBroadcast);
                            for (int i = SendMessagesHelper.9.1.5.this.val$oldId; ; i = SendMessagesHelper.9.this.val$newMsgObj.id)
                            {
                              ((NotificationCenter)localObject1).postNotificationName(j, new Object[] { Integer.valueOf(k), Integer.valueOf(i), SendMessagesHelper.9.this.val$newMsgObj, Long.valueOf(SendMessagesHelper.9.this.val$newMsgObj.dialog_id) });
                              SendMessagesHelper.this.processSentMessage(SendMessagesHelper.9.1.5.this.val$oldId);
                              SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.9.1.5.this.val$oldId);
                              return;
                            }
                          }
                        });
                        if ((MessageObject.isVideoMessage(SendMessagesHelper.9.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.9.this.val$newMsgObj)))
                          SendMessagesHelper.this.stopVideoService(this.val$attachPath);
                        return;
                      }
                    }
                  });
                }
              }
              else
              {
                label422: if (i != 0)
                {
                  MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.9.this.val$newMsgObj);
                  SendMessagesHelper.9.this.val$newMsgObj.send_state = 2;
                  NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.9.this.val$newMsgObj.id) });
                  SendMessagesHelper.this.processSentMessage(SendMessagesHelper.9.this.val$newMsgObj.id);
                  if ((MessageObject.isVideoMessage(SendMessagesHelper.9.this.val$newMsgObj)) || (MessageObject.isNewGifMessage(SendMessagesHelper.9.this.val$newMsgObj)))
                    SendMessagesHelper.this.stopVideoService(SendMessagesHelper.9.this.val$newMsgObj.attachPath);
                  SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.9.this.val$newMsgObj.id);
                }
                return;
                if (!(this.val$response instanceof TLRPC.Updates))
                  break label1010;
                localUpdates = (TLRPC.Updates)this.val$response;
                localObject2 = ((TLRPC.Updates)this.val$response).updates;
                i = 0;
                label599: if (i >= ((ArrayList)localObject2).size())
                  break label1004;
                localObject1 = (TLRPC.Update)((ArrayList)localObject2).get(i);
                if ((localObject1 instanceof TLRPC.TL_updateNewMessage))
                {
                  localObject3 = (TLRPC.TL_updateNewMessage)localObject1;
                  localObject1 = ((TLRPC.TL_updateNewMessage)localObject3).message;
                  localArrayList.add(localObject1);
                  SendMessagesHelper.9.this.val$newMsgObj.id = ((TLRPC.TL_updateNewMessage)localObject3).message.id;
                  Utilities.stageQueue.postRunnable(new Runnable((TLRPC.TL_updateNewMessage)localObject3)
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processNewDifferenceParams(-1, this.val$newMessage.pts, -1, this.val$newMessage.pts_count);
                    }
                  });
                  ((ArrayList)localObject2).remove(i);
                }
              }
              while (true)
              {
                label690: boolean bool1;
                if (localObject1 != null)
                {
                  localObject3 = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(((TLRPC.Message)localObject1).dialog_id));
                  localObject2 = localObject3;
                  if (localObject3 == null)
                  {
                    localObject2 = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(((TLRPC.Message)localObject1).out, ((TLRPC.Message)localObject1).dialog_id));
                    MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(((TLRPC.Message)localObject1).dialog_id), localObject2);
                  }
                  if (((Integer)localObject2).intValue() < ((TLRPC.Message)localObject1).id)
                  {
                    bool1 = true;
                    label783: ((TLRPC.Message)localObject1).unread = bool1;
                    SendMessagesHelper.9.this.val$newMsgObj.id = ((TLRPC.Message)localObject1).id;
                    SendMessagesHelper.this.updateMediaPaths(SendMessagesHelper.9.this.val$msgObj, (TLRPC.Message)localObject1, SendMessagesHelper.9.this.val$originalPath, false);
                  }
                }
                for (i = 0; ; i = 1)
                {
                  Utilities.stageQueue.postRunnable(new Runnable(localUpdates)
                  {
                    public void run()
                    {
                      MessagesController.getInstance().processUpdates(this.val$updates, false);
                    }
                  });
                  break;
                  if ((localObject1 instanceof TLRPC.TL_updateNewChannelMessage))
                  {
                    localObject3 = (TLRPC.TL_updateNewChannelMessage)localObject1;
                    localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject3).message;
                    localArrayList.add(localObject1);
                    if ((SendMessagesHelper.9.this.val$newMsgObj.flags & 0x80000000) != 0)
                    {
                      TLRPC.Message localMessage = ((TLRPC.TL_updateNewChannelMessage)localObject3).message;
                      localMessage.flags |= -2147483648;
                    }
                    Utilities.stageQueue.postRunnable(new Runnable((TLRPC.TL_updateNewChannelMessage)localObject3)
                    {
                      public void run()
                      {
                        MessagesController.getInstance().processNewChannelDifferenceParams(this.val$newMessage.pts, this.val$newMessage.pts_count, this.val$newMessage.message.to_id.channel_id);
                      }
                    });
                    ((ArrayList)localObject2).remove(i);
                    break label690;
                  }
                  i += 1;
                  break label599;
                  bool1 = false;
                  break label783;
                }
                j = SendMessagesHelper.9.this.val$newMsgObj.id;
                break label347;
                AlertsCreator.processError(this.val$error, null, SendMessagesHelper.9.this.val$req, new Object[0]);
                i = 1;
                break label422;
                label1004: localObject1 = null;
              }
              label1010: i = 0;
            }
          }
        });
      }
    };
    paramString = new QuickAckDelegate(localMessage)
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable(this.val$newMsgObj.id)
        {
          public void run()
          {
            SendMessagesHelper.10.this.val$newMsgObj.send_state = 0;
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByAck, new Object[] { Integer.valueOf(this.val$msg_id) });
          }
        });
      }
    };
    if ((paramTLObject instanceof TLRPC.TL_messages_sendMessage));
    for (int i = 128; ; i = 0)
    {
      localConnectionsManager.sendRequest(paramTLObject, paramMessageObject, paramString, i | 0x44);
      return;
    }
  }

  public static void prepareSendingAudioDocuments(ArrayList<MessageObject> paramArrayList, long paramLong, MessageObject paramMessageObject)
  {
    new Thread(new Runnable(paramArrayList, paramLong, paramMessageObject)
    {
      public void run()
      {
        int m = this.val$messageObjects.size();
        int i = 0;
        MessageObject localMessageObject;
        String str;
        Object localObject1;
        int j;
        if (i < m)
        {
          localMessageObject = (MessageObject)this.val$messageObjects.get(i);
          str = localMessageObject.messageOwner.attachPath;
          localObject1 = new File(str);
          if ((int)this.val$dialog_id != 0)
            break label179;
          j = 1;
          if (str == null)
            break label237;
          str = str + "audio" + ((File)localObject1).length();
        }
        label179: label184: label189: label237: 
        while (true)
        {
          localObject1 = null;
          if (j == 0)
          {
            localObject1 = MessagesStorage.getInstance();
            if (j != 0)
              break label184;
          }
          Object localObject2;
          for (int k = 1; ; k = 4)
          {
            localObject1 = (TLRPC.TL_document)((MessagesStorage)localObject1).getSentFile(str, k);
            localObject2 = localObject1;
            if (localObject1 == null)
              localObject2 = (TLRPC.TL_document)localMessageObject.messageOwner.media.document;
            if (j == 0)
              break label189;
            j = (int)(this.val$dialog_id >> 32);
            if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(j)) != null)
              break label189;
            return;
            j = 0;
            break;
          }
          localObject1 = new HashMap();
          if (str != null)
            ((HashMap)localObject1).put("originalPath", str);
          AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_document)localObject2, localMessageObject, (HashMap)localObject1)
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$messageObject.messageOwner.attachPath, SendMessagesHelper.13.this.val$dialog_id, SendMessagesHelper.13.this.val$reply_to_msg, null, this.val$params);
            }
          });
          i += 1;
          break;
        }
      }
    }).start();
  }

  public static void prepareSendingBotContextResult(TLRPC.BotInlineResult paramBotInlineResult, HashMap<String, String> paramHashMap, long paramLong, MessageObject paramMessageObject)
  {
    if (paramBotInlineResult == null);
    label216: 
    do
    {
      return;
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaAuto))
      {
        new Thread(new Runnable(paramBotInlineResult, paramLong, paramHashMap, paramMessageObject)
        {
          public void run()
          {
            Object localObject6;
            Object localObject1;
            Object localObject5;
            String str;
            if ((this.val$result instanceof TLRPC.TL_botInlineMediaResult))
              if (this.val$result.type.equals("game"))
              {
                if ((int)this.val$dialog_id == 0)
                  return;
                localObject6 = new TLRPC.TL_game();
                ((TLRPC.TL_game)localObject6).title = this.val$result.title;
                ((TLRPC.TL_game)localObject6).description = this.val$result.description;
                ((TLRPC.TL_game)localObject6).short_name = this.val$result.id;
                ((TLRPC.TL_game)localObject6).photo = this.val$result.photo;
                if (!(this.val$result.document instanceof TLRPC.TL_document))
                  break label1881;
                ((TLRPC.TL_game)localObject6).document = this.val$result.document;
                ((TLRPC.TL_game)localObject6).flags |= 1;
                localObject1 = null;
                localObject5 = null;
                str = null;
              }
            while (true)
            {
              label135: if ((this.val$params != null) && (this.val$result.content_url != null))
                this.val$params.put("originalPath", this.val$result.content_url);
              AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_document)localObject5, str, (TLRPC.TL_photo)localObject1, (TLRPC.TL_game)localObject6)
              {
                public void run()
                {
                  if (this.val$finalDocument != null)
                  {
                    this.val$finalDocument.caption = SendMessagesHelper.15.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalDocument, null, this.val$finalPathFinal, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                  }
                  do
                  {
                    return;
                    if (this.val$finalPhoto == null)
                      continue;
                    this.val$finalPhoto.caption = SendMessagesHelper.15.this.val$result.send_message.caption;
                    SendMessagesHelper.getInstance().sendMessage(this.val$finalPhoto, SendMessagesHelper.15.this.val$result.content_url, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$reply_to_msg, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                    return;
                  }
                  while (this.val$finalGame == null);
                  SendMessagesHelper.getInstance().sendMessage(this.val$finalGame, SendMessagesHelper.15.this.val$dialog_id, SendMessagesHelper.15.this.val$result.send_message.reply_markup, SendMessagesHelper.15.this.val$params);
                }
              });
              return;
              if (this.val$result.document != null)
              {
                if ((this.val$result.document instanceof TLRPC.TL_document))
                {
                  localObject5 = (TLRPC.TL_document)this.val$result.document;
                  localObject6 = null;
                  localObject1 = null;
                  str = null;
                  continue;
                }
              }
              else if ((this.val$result.photo != null) && ((this.val$result.photo instanceof TLRPC.TL_photo)))
              {
                localObject1 = (TLRPC.TL_photo)this.val$result.photo;
                localObject6 = null;
                localObject5 = null;
                str = null;
                continue;
                if (this.val$result.content_url != null)
                {
                  localObject1 = new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.content_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.content_url, "file"));
                  if (((File)localObject1).exists())
                  {
                    str = ((File)localObject1).getAbsolutePath();
                    localObject5 = this.val$result.type;
                    i = -1;
                    switch (((String)localObject5).hashCode())
                    {
                    default:
                    case 93166550:
                    case 112386354:
                    case 3143036:
                    case 112202875:
                    case -1890252483:
                    case 102340:
                    case 106642994:
                    }
                  }
                  while (true)
                    switch (i)
                    {
                    default:
                      localObject6 = null;
                      localObject1 = null;
                      localObject5 = null;
                      break label135;
                      str = this.val$result.content_url;
                      break label357;
                      if (!((String)localObject5).equals("audio"))
                        continue;
                      i = 0;
                      continue;
                      if (!((String)localObject5).equals("voice"))
                        continue;
                      i = 1;
                      continue;
                      if (!((String)localObject5).equals("file"))
                        continue;
                      i = 2;
                      continue;
                      if (!((String)localObject5).equals("video"))
                        continue;
                      i = 3;
                      continue;
                      if (!((String)localObject5).equals("sticker"))
                        continue;
                      i = 4;
                      continue;
                      if (!((String)localObject5).equals("gif"))
                        continue;
                      i = 5;
                      continue;
                      if (!((String)localObject5).equals("photo"))
                        continue;
                      i = 6;
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    }
                  localObject5 = new TLRPC.TL_document();
                  ((TLRPC.TL_document)localObject5).id = 0L;
                  ((TLRPC.TL_document)localObject5).size = 0;
                  ((TLRPC.TL_document)localObject5).dc_id = 0;
                  ((TLRPC.TL_document)localObject5).mime_type = this.val$result.content_type;
                  ((TLRPC.TL_document)localObject5).date = ConnectionsManager.getInstance().getCurrentTime();
                  localObject6 = new TLRPC.TL_documentAttributeFilename();
                  ((TLRPC.TL_document)localObject5).attributes.add(localObject6);
                  localObject1 = this.val$result.type;
                  int i = -1;
                  switch (((String)localObject1).hashCode())
                  {
                  default:
                    switch (i)
                    {
                    default:
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    }
                  case 102340:
                  case 112386354:
                  case 93166550:
                  case 3143036:
                  case 112202875:
                  case -1890252483:
                  }
                  while (true)
                  {
                    if (((TLRPC.TL_documentAttributeFilename)localObject6).file_name == null)
                      ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "file";
                    if (((TLRPC.TL_document)localObject5).mime_type == null)
                      ((TLRPC.TL_document)localObject5).mime_type = "application/octet-stream";
                    if (((TLRPC.TL_document)localObject5).thumb != null)
                      break label1860;
                    ((TLRPC.TL_document)localObject5).thumb = new TLRPC.TL_photoSize();
                    ((TLRPC.TL_document)localObject5).thumb.w = this.val$result.w;
                    ((TLRPC.TL_document)localObject5).thumb.h = this.val$result.h;
                    ((TLRPC.TL_document)localObject5).thumb.size = 0;
                    ((TLRPC.TL_document)localObject5).thumb.location = new TLRPC.TL_fileLocationUnavailable();
                    ((TLRPC.TL_document)localObject5).thumb.type = "x";
                    localObject6 = null;
                    localObject1 = null;
                    break;
                    if (!((String)localObject1).equals("gif"))
                      break label756;
                    i = 0;
                    break label756;
                    if (!((String)localObject1).equals("voice"))
                      break label756;
                    i = 1;
                    break label756;
                    if (!((String)localObject1).equals("audio"))
                      break label756;
                    i = 2;
                    break label756;
                    if (!((String)localObject1).equals("file"))
                      break label756;
                    i = 3;
                    break label756;
                    if (!((String)localObject1).equals("video"))
                      break label756;
                    i = 4;
                    break label756;
                    if (!((String)localObject1).equals("sticker"))
                      break label756;
                    i = 5;
                    break label756;
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "animation.gif";
                    if (str.endsWith("mp4"))
                    {
                      ((TLRPC.TL_document)localObject5).mime_type = "video/mp4";
                      ((TLRPC.TL_document)localObject5).attributes.add(new TLRPC.TL_documentAttributeAnimated());
                    }
                    while (true)
                    {
                      try
                      {
                        if (!str.endsWith("mp4"))
                          break label1104;
                        localObject1 = ThumbnailUtils.createVideoThumbnail(str, 1);
                        if (localObject1 == null)
                          break;
                        ((TLRPC.TL_document)localObject5).thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject1, 90.0F, 90.0F, 55, false);
                        ((Bitmap)localObject1).recycle();
                      }
                      catch (Throwable localThrowable1)
                      {
                        FileLog.e(localThrowable1);
                      }
                      break;
                      ((TLRPC.TL_document)localObject5).mime_type = "image/gif";
                      continue;
                      localObject2 = ImageLoader.loadBitmap(str, null, 90.0F, 90.0F, true);
                    }
                    Object localObject2 = new TLRPC.TL_documentAttributeAudio();
                    ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                    ((TLRPC.TL_documentAttributeAudio)localObject2).voice = true;
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "audio.ogg";
                    ((TLRPC.TL_document)localObject5).attributes.add(localObject2);
                    ((TLRPC.TL_document)localObject5).thumb = new TLRPC.TL_photoSizeEmpty();
                    ((TLRPC.TL_document)localObject5).thumb.type = "s";
                    continue;
                    localObject2 = new TLRPC.TL_documentAttributeAudio();
                    ((TLRPC.TL_documentAttributeAudio)localObject2).duration = this.val$result.duration;
                    ((TLRPC.TL_documentAttributeAudio)localObject2).title = this.val$result.title;
                    ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 1;
                    if (this.val$result.description != null)
                    {
                      ((TLRPC.TL_documentAttributeAudio)localObject2).performer = this.val$result.description;
                      ((TLRPC.TL_documentAttributeAudio)localObject2).flags |= 2;
                    }
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "audio.mp3";
                    ((TLRPC.TL_document)localObject5).attributes.add(localObject2);
                    ((TLRPC.TL_document)localObject5).thumb = new TLRPC.TL_photoSizeEmpty();
                    ((TLRPC.TL_document)localObject5).thumb.type = "s";
                    continue;
                    i = this.val$result.content_type.indexOf('/');
                    if (i != -1)
                    {
                      ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = ("file." + this.val$result.content_type.substring(i + 1));
                      continue;
                    }
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "file";
                    continue;
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "video.mp4";
                    localObject2 = new TLRPC.TL_documentAttributeVideo();
                    ((TLRPC.TL_documentAttributeVideo)localObject2).w = this.val$result.w;
                    ((TLRPC.TL_documentAttributeVideo)localObject2).h = this.val$result.h;
                    ((TLRPC.TL_documentAttributeVideo)localObject2).duration = this.val$result.duration;
                    ((TLRPC.TL_document)localObject5).attributes.add(localObject2);
                    try
                    {
                      localObject2 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "jpg")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                      if (localObject2 == null)
                        continue;
                      ((TLRPC.TL_document)localObject5).thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, false);
                      ((Bitmap)localObject2).recycle();
                    }
                    catch (Throwable localThrowable2)
                    {
                      FileLog.e(localThrowable2);
                    }
                    continue;
                    Object localObject3 = new TLRPC.TL_documentAttributeSticker();
                    ((TLRPC.TL_documentAttributeSticker)localObject3).alt = "";
                    ((TLRPC.TL_documentAttributeSticker)localObject3).stickerset = new TLRPC.TL_inputStickerSetEmpty();
                    ((TLRPC.TL_document)localObject5).attributes.add(localObject3);
                    localObject3 = new TLRPC.TL_documentAttributeImageSize();
                    ((TLRPC.TL_documentAttributeImageSize)localObject3).w = this.val$result.w;
                    ((TLRPC.TL_documentAttributeImageSize)localObject3).h = this.val$result.h;
                    ((TLRPC.TL_document)localObject5).attributes.add(localObject3);
                    ((TLRPC.TL_documentAttributeFilename)localObject6).file_name = "sticker.webp";
                    try
                    {
                      localObject3 = ImageLoader.loadBitmap(new File(FileLoader.getInstance().getDirectory(4), Utilities.MD5(this.val$result.thumb_url) + "." + ImageLoader.getHttpUrlExtension(this.val$result.thumb_url, "webp")).getAbsolutePath(), null, 90.0F, 90.0F, true);
                      if (localObject3 == null)
                        continue;
                      ((TLRPC.TL_document)localObject5).thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject3, 90.0F, 90.0F, 55, false);
                      ((Bitmap)localObject3).recycle();
                    }
                    catch (Throwable localThrowable3)
                    {
                      FileLog.e(localThrowable3);
                    }
                  }
                  if (localThrowable3.exists());
                  for (localObject5 = SendMessagesHelper.getInstance().generatePhotoSizes(str, null); ; localObject5 = null)
                  {
                    localObject4 = localObject5;
                    if (localObject5 == null)
                    {
                      localObject4 = new TLRPC.TL_photo();
                      ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                      localObject5 = new TLRPC.TL_photoSize();
                      ((TLRPC.TL_photoSize)localObject5).w = this.val$result.w;
                      ((TLRPC.TL_photoSize)localObject5).h = this.val$result.h;
                      ((TLRPC.TL_photoSize)localObject5).size = 1;
                      ((TLRPC.TL_photoSize)localObject5).location = new TLRPC.TL_fileLocationUnavailable();
                      ((TLRPC.TL_photoSize)localObject5).type = "x";
                      ((TLRPC.TL_photo)localObject4).sizes.add(localObject5);
                    }
                    localObject6 = null;
                    localObject5 = null;
                    break;
                  }
                  localObject6 = null;
                  localObject4 = null;
                  continue;
                }
              }
              label357: label756: localObject6 = null;
              label1104: Object localObject4 = null;
              label1860: localObject5 = null;
              str = null;
              continue;
              label1881: localObject4 = null;
              localObject5 = null;
              str = null;
            }
          }
        }).run();
        return;
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageText))
      {
        Object localObject2 = null;
        localObject1 = localObject2;
        int i;
        if ((int)paramLong == 0)
        {
          i = 0;
          localObject1 = localObject2;
          if (i < paramBotInlineResult.send_message.entities.size())
          {
            localObject3 = (TLRPC.MessageEntity)paramBotInlineResult.send_message.entities.get(i);
            if (!(localObject3 instanceof TLRPC.TL_messageEntityUrl))
              break label216;
            localObject1 = new TLRPC.TL_webPagePending();
            localObject2 = paramBotInlineResult.send_message.message;
            i = ((TLRPC.MessageEntity)localObject3).offset;
            int j = ((TLRPC.MessageEntity)localObject3).offset;
            ((TLRPC.WebPage)localObject1).url = ((String)localObject2).substring(i, ((TLRPC.MessageEntity)localObject3).length + j);
          }
        }
        localObject2 = getInstance();
        Object localObject3 = paramBotInlineResult.send_message.message;
        if (!paramBotInlineResult.send_message.no_webpage);
        for (boolean bool = true; ; bool = false)
        {
          ((SendMessagesHelper)localObject2).sendMessage((String)localObject3, paramLong, paramMessageObject, (TLRPC.WebPage)localObject1, bool, paramBotInlineResult.send_message.entities, paramBotInlineResult.send_message.reply_markup, paramHashMap);
          return;
          i += 1;
          break;
        }
      }
      if ((paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaVenue))
      {
        localObject1 = new TLRPC.TL_messageMediaVenue();
        ((TLRPC.TL_messageMediaVenue)localObject1).geo = paramBotInlineResult.send_message.geo;
        ((TLRPC.TL_messageMediaVenue)localObject1).address = paramBotInlineResult.send_message.address;
        ((TLRPC.TL_messageMediaVenue)localObject1).title = paramBotInlineResult.send_message.title;
        ((TLRPC.TL_messageMediaVenue)localObject1).provider = paramBotInlineResult.send_message.provider;
        ((TLRPC.TL_messageMediaVenue)localObject1).venue_id = paramBotInlineResult.send_message.venue_id;
        getInstance().sendMessage((TLRPC.MessageMedia)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
        return;
      }
      if (!(paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaGeo))
        continue;
      localObject1 = new TLRPC.TL_messageMediaGeo();
      ((TLRPC.TL_messageMediaGeo)localObject1).geo = paramBotInlineResult.send_message.geo;
      getInstance().sendMessage((TLRPC.MessageMedia)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
      return;
    }
    while (!(paramBotInlineResult.send_message instanceof TLRPC.TL_botInlineMessageMediaContact));
    Object localObject1 = new TLRPC.TL_user();
    ((TLRPC.User)localObject1).phone = paramBotInlineResult.send_message.phone_number;
    ((TLRPC.User)localObject1).first_name = paramBotInlineResult.send_message.first_name;
    ((TLRPC.User)localObject1).last_name = paramBotInlineResult.send_message.last_name;
    getInstance().sendMessage((TLRPC.User)localObject1, paramLong, paramMessageObject, paramBotInlineResult.send_message.reply_markup, paramHashMap);
  }

  public static void prepareSendingDocument(String paramString1, String paramString2, Uri paramUri, String paramString3, long paramLong, MessageObject paramMessageObject, e parame)
  {
    if (((paramString1 == null) || (paramString2 == null)) && (paramUri == null))
      return;
    ArrayList localArrayList2 = new ArrayList();
    ArrayList localArrayList3 = new ArrayList();
    ArrayList localArrayList1 = null;
    if (paramUri != null)
    {
      localArrayList1 = new ArrayList();
      localArrayList1.add(paramUri);
    }
    if (paramString1 != null)
    {
      localArrayList2.add(paramString1);
      localArrayList3.add(paramString2);
    }
    prepareSendingDocuments(localArrayList2, localArrayList3, localArrayList1, paramString3, paramLong, paramMessageObject, parame);
  }

  private static boolean prepareSendingDocumentInternal(String paramString1, String paramString2, Uri paramUri, String paramString3, long paramLong, MessageObject paramMessageObject, String paramString4)
  {
    if (((paramString1 == null) || (paramString1.length() == 0)) && (paramUri == null))
      return false;
    if ((paramUri != null) && (AndroidUtilities.isInternalUri(paramUri)))
      return false;
    if ((paramString1 != null) && (AndroidUtilities.isInternalUri(Uri.fromFile(new File(paramString1)))))
      return false;
    MimeTypeMap localMimeTypeMap = MimeTypeMap.getSingleton();
    Object localObject = null;
    File localFile = null;
    String str1 = paramString1;
    if (paramUri != null)
    {
      paramString1 = localFile;
      if (paramString3 != null)
        paramString1 = localMimeTypeMap.getExtensionFromMimeType(paramString3);
      localObject = paramString1;
      if (paramString1 == null)
        localObject = "txt";
      paramString1 = MediaController.copyFileToCache(paramUri, (String)localObject);
      str1 = paramString1;
      if (paramString1 == null)
        return false;
    }
    localFile = new File(str1);
    if ((!localFile.exists()) || (localFile.length() == 0L))
      return false;
    boolean bool;
    int j;
    label161: String str2;
    if ((int)paramLong == 0)
    {
      bool = true;
      if (bool)
        break label260;
      j = 1;
      str2 = localFile.getName();
      paramUri = "";
      if (localObject == null)
        break label266;
      paramUri = (Uri)localObject;
    }
    int i;
    while (true)
    {
      if ((!paramUri.toLowerCase().equals("mp3")) && (!paramUri.toLowerCase().equals("m4a")))
        break label1112;
      paramString3 = AudioInfo.getAudioInfo(localFile);
      if ((paramString3 == null) || (paramString3.getDuration() == 0L))
        break label1112;
      if (!bool)
        break label948;
      i = (int)(paramLong >> 32);
      if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i)) != null)
        break label294;
      return false;
      bool = false;
      break;
      label260: j = 0;
      break label161;
      label266: i = str1.lastIndexOf('.');
      if (i == -1)
        continue;
      paramUri = str1.substring(i + 1);
    }
    label294: paramString1 = new TLRPC.TL_documentAttributeAudio();
    paramString1.duration = (int)(paramString3.getDuration() / 1000L);
    paramString1.title = paramString3.getTitle();
    paramString1.performer = paramString3.getArtist();
    if (paramString1.title == null)
      paramString1.title = "";
    paramString1.flags |= 1;
    if (paramString1.performer == null)
      paramString1.performer = "";
    paramString1.flags |= 2;
    label398: label429: label948: label1112: for (paramString3 = paramString1; ; paramString3 = null)
    {
      if (paramString2 != null)
        if (!paramString2.endsWith("attheme"));
      for (i = 1; ; i = 0)
      {
        localObject = null;
        paramString1 = (String)localObject;
        if (i == 0)
        {
          paramString1 = (String)localObject;
          if (!bool)
          {
            paramString1 = MessagesStorage.getInstance();
            if (bool)
              break label1033;
            i = 1;
            localObject = (TLRPC.TL_document)paramString1.getSentFile(paramString2, i);
            paramString1 = (String)localObject;
            if (localObject == null)
            {
              paramString1 = (String)localObject;
              if (!str1.equals(paramString2))
              {
                paramString1 = (String)localObject;
                if (!bool)
                {
                  paramString1 = MessagesStorage.getInstance();
                  localObject = str1 + localFile.length();
                  if (bool)
                    break label1039;
                  i = 1;
                  label506: paramString1 = (TLRPC.TL_document)paramString1.getSentFile((String)localObject, i);
                }
              }
            }
          }
        }
        if (paramString1 == null)
        {
          paramString1 = new TLRPC.TL_document();
          paramString1.id = 0L;
          paramString1.date = ConnectionsManager.getInstance().getCurrentTime();
          localObject = new TLRPC.TL_documentAttributeFilename();
          ((TLRPC.TL_documentAttributeFilename)localObject).file_name = str2;
          paramString1.attributes.add(localObject);
          paramString1.size = (int)localFile.length();
          paramString1.dc_id = 0;
          if (paramString3 != null)
            paramString1.attributes.add(paramString3);
          if (paramUri.length() != 0)
            if (paramUri.toLowerCase().equals("webp"))
            {
              paramString1.mime_type = "image/webp";
              label626: if (!paramString1.mime_type.equals("image/gif"));
            }
        }
        while (true)
          try
          {
            paramUri = ImageLoader.loadBitmap(localFile.getAbsolutePath(), null, 90.0F, 90.0F, true);
            if (paramUri == null)
              continue;
            ((TLRPC.TL_documentAttributeFilename)localObject).file_name = "animation.gif";
            paramString1.thumb = ImageLoader.scaleAndSaveImage(paramUri, 90.0F, 90.0F, 55, bool);
            paramUri.recycle();
            if ((!paramString1.mime_type.equals("image/webp")) || (j == 0))
              continue;
            paramUri = new BitmapFactory.Options();
          }
          catch (Exception paramUri)
          {
            try
            {
              paramUri.inJustDecodeBounds = true;
              paramString3 = new RandomAccessFile(str1, "r");
              localObject = paramString3.getChannel().map(FileChannel.MapMode.READ_ONLY, 0L, str1.length());
              Utilities.loadWebpImage(null, (ByteBuffer)localObject, ((ByteBuffer)localObject).limit(), paramUri, true);
              paramString3.close();
              if ((paramUri.outWidth == 0) || (paramUri.outHeight == 0) || (paramUri.outWidth > 800) || (paramUri.outHeight > 800))
                continue;
              paramString3 = new TLRPC.TL_documentAttributeSticker();
              paramString3.alt = "";
              paramString3.stickerset = new TLRPC.TL_inputStickerSetEmpty();
              paramString1.attributes.add(paramString3);
              paramString3 = new TLRPC.TL_documentAttributeImageSize();
              paramString3.w = paramUri.outWidth;
              paramString3.h = paramUri.outHeight;
              paramString1.attributes.add(paramString3);
              if (paramString1.thumb != null)
                continue;
              paramString1.thumb = new TLRPC.TL_photoSizeEmpty();
              paramString1.thumb.type = "s";
              paramString1.caption = paramString4;
              paramUri = new HashMap();
              if (paramString2 == null)
                continue;
              paramUri.put("originalPath", paramString2);
              AndroidUtilities.runOnUIThread(new Runnable(paramString1, str1, paramLong, paramMessageObject, paramUri)
              {
                public void run()
                {
                  SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, this.val$dialog_id, this.val$reply_to_msg, null, this.val$params);
                }
              });
              return true;
              paramString1 = new TLRPC.TL_documentAttributeAudio();
              break;
              if (paramString3 == null)
                continue;
              paramString2 = paramString2 + "audio" + localFile.length();
              i = 0;
              break label398;
              paramString2 = paramString2 + "" + localFile.length();
              i = 0;
              break label398;
              i = 4;
              break label429;
              i = 4;
              break label506;
              paramUri = localMimeTypeMap.getMimeTypeFromExtension(paramUri.toLowerCase());
              if (paramUri == null)
                continue;
              paramString1.mime_type = paramUri;
              break label626;
              paramString1.mime_type = "application/octet-stream";
              break label626;
              paramString1.mime_type = "application/octet-stream";
              break label626;
              paramUri = paramUri;
              FileLog.e(paramUri);
            }
            catch (Exception paramString3)
            {
              FileLog.e(paramString3);
              continue;
            }
          }
      }
    }
  }

  public static void prepareSendingDocuments(ArrayList<String> paramArrayList1, ArrayList<String> paramArrayList2, ArrayList<Uri> paramArrayList, String paramString, long paramLong, MessageObject paramMessageObject, e parame)
  {
    if (((paramArrayList1 == null) && (paramArrayList2 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList2 != null) && (paramArrayList1.size() != paramArrayList2.size())))
      return;
    new Thread(new Runnable(paramArrayList1, paramArrayList2, paramString, paramLong, paramMessageObject, paramArrayList, parame)
    {
      public void run()
      {
        int k;
        int j;
        if (this.val$paths != null)
        {
          k = 0;
          j = 0;
          i = j;
          if (k >= this.val$paths.size())
            break label79;
          if (SendMessagesHelper.access$1000((String)this.val$paths.get(k), (String)this.val$originalPaths.get(k), null, this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null))
            break label174;
        }
        label174: for (int i = 1; ; i = j)
        {
          k += 1;
          j = i;
          break;
          i = 0;
          label79: k = i;
          if (this.val$uris != null)
          {
            j = 0;
            while (true)
            {
              k = i;
              if (j >= this.val$uris.size())
                break;
              if (!SendMessagesHelper.access$1000(null, null, (Uri)this.val$uris.get(j), this.val$mime, this.val$dialog_id, this.val$reply_to_msg, null))
                i = 1;
              j += 1;
            }
          }
          if (this.val$inputContent != null)
            this.val$inputContent.d();
          if (k != 0)
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                try
                {
                  Toast.makeText(ApplicationLoader.applicationContext, LocaleController.getString("UnsupportedAttachment", 2131166540), 0).show();
                  return;
                }
                catch (Exception localException)
                {
                  FileLog.e(localException);
                }
              }
            });
          return;
        }
      }
    }).start();
  }

  public static void prepareSendingPhoto(String paramString, Uri paramUri, long paramLong, MessageObject paramMessageObject, CharSequence paramCharSequence, ArrayList<TLRPC.InputDocument> paramArrayList, e parame)
  {
    Object localObject2 = null;
    Object localObject1;
    if ((paramString != null) && (paramString.length() != 0))
    {
      localObject1 = new ArrayList();
      ((ArrayList)localObject1).add(paramString);
    }
    for (paramString = (String)localObject1; ; paramString = null)
    {
      if (paramUri != null)
      {
        localObject1 = new ArrayList();
        ((ArrayList)localObject1).add(paramUri);
      }
      for (paramUri = (Uri)localObject1; ; paramUri = null)
      {
        if (paramCharSequence != null)
        {
          localObject1 = new ArrayList();
          ((ArrayList)localObject1).add(paramCharSequence.toString());
        }
        for (paramCharSequence = (CharSequence)localObject1; ; paramCharSequence = null)
        {
          localObject1 = localObject2;
          if (paramArrayList != null)
          {
            localObject1 = localObject2;
            if (!paramArrayList.isEmpty())
            {
              localObject1 = new ArrayList();
              ((ArrayList)localObject1).add(new ArrayList(paramArrayList));
            }
          }
          prepareSendingPhotos(paramString, paramUri, paramLong, paramMessageObject, paramCharSequence, (ArrayList)localObject1, parame);
          return;
        }
      }
    }
  }

  public static void prepareSendingPhotos(ArrayList<String> paramArrayList1, ArrayList<Uri> paramArrayList, long paramLong, MessageObject paramMessageObject, ArrayList<String> paramArrayList2, ArrayList<ArrayList<TLRPC.InputDocument>> paramArrayList3, e parame)
  {
    if (((paramArrayList1 == null) && (paramArrayList == null)) || ((paramArrayList1 != null) && (paramArrayList1.isEmpty())) || ((paramArrayList != null) && (paramArrayList.isEmpty())))
      return;
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    if (paramArrayList1 != null)
      localArrayList1.addAll(paramArrayList1);
    if (paramArrayList != null)
      localArrayList2.addAll(paramArrayList);
    new Thread(new Runnable(paramLong, localArrayList1, localArrayList2, paramArrayList2, paramArrayList3, paramMessageObject, parame)
    {
      public void run()
      {
        int j;
        Object localObject3;
        Object localObject4;
        ArrayList localArrayList;
        int k;
        label37: String str;
        Object localObject1;
        Object localObject5;
        int m;
        label49: Object localObject2;
        if ((int)this.val$dialog_id == 0)
        {
          j = 1;
          localObject3 = null;
          localObject4 = null;
          localArrayList = null;
          if (this.val$pathsCopy.isEmpty())
            break label273;
          k = this.val$pathsCopy.size();
          str = null;
          localObject1 = null;
          localObject5 = null;
          m = 0;
          if (m >= k)
            break label872;
          if (this.val$pathsCopy.isEmpty())
            break label284;
          str = (String)this.val$pathsCopy.get(m);
          localObject2 = localObject1;
        }
        while (true)
        {
          label83: if ((str == null) && (localObject2 != null))
            localObject1 = AndroidUtilities.getPath((Uri)localObject2);
          for (Object localObject6 = ((Uri)localObject2).toString(); ; localObject6 = str)
          {
            int i = 0;
            label148: Object localObject7;
            if ((localObject1 != null) && ((((String)localObject1).endsWith(".gif")) || (((String)localObject1).endsWith(".webp"))))
              if (((String)localObject1).endsWith(".gif"))
              {
                localObject5 = "gif";
                localObject7 = localObject1;
                localObject1 = localObject5;
                i = 1;
                localObject5 = localObject7;
              }
            while (true)
            {
              label162: if (i != 0)
              {
                if (localObject3 != null)
                  break label963;
                localObject3 = new ArrayList();
                localObject4 = new ArrayList();
                localArrayList = new ArrayList();
              }
              label273: label284: label705: label963: 
              while (true)
              {
                ((ArrayList)localObject3).add(localObject5);
                ((ArrayList)localObject4).add(localObject6);
                if (this.val$captions != null);
                for (localObject5 = (String)this.val$captions.get(m); ; localObject5 = null)
                {
                  localArrayList.add(localObject5);
                  localObject6 = localObject4;
                  m += 1;
                  localObject5 = localObject1;
                  localObject1 = localObject2;
                  localObject4 = localObject6;
                  break label49;
                  j = 0;
                  break;
                  k = this.val$urisCopy.size();
                  break label37;
                  if (this.val$urisCopy.isEmpty())
                    break label992;
                  localObject2 = (Uri)this.val$urisCopy.get(m);
                  break label83;
                  localObject5 = "webp";
                  break label148;
                  if ((localObject1 != null) || (localObject2 == null))
                    break label966;
                  if (MediaController.isGif((Uri)localObject2))
                  {
                    i = 1;
                    localObject6 = ((Uri)localObject2).toString();
                    localObject5 = MediaController.copyFileToCache((Uri)localObject2, "gif");
                    localObject1 = "gif";
                    break label162;
                  }
                  if (!MediaController.isWebp((Uri)localObject2))
                    break label966;
                  i = 1;
                  localObject6 = ((Uri)localObject2).toString();
                  localObject5 = MediaController.copyFileToCache((Uri)localObject2, "webp");
                  localObject1 = "webp";
                  break label162;
                }
                label472: Object localObject8;
                if (localObject5 != null)
                {
                  localObject7 = new File((String)localObject5);
                  localObject7 = (String)localObject6 + ((File)localObject7).length() + "_" + ((File)localObject7).lastModified();
                  localObject6 = null;
                  if (j == 0)
                  {
                    localObject6 = MessagesStorage.getInstance();
                    if (j != 0)
                      break label705;
                    i = 0;
                    localObject8 = (TLRPC.TL_photo)((MessagesStorage)localObject6).getSentFile((String)localObject7, i);
                    localObject6 = localObject8;
                    if (localObject8 == null)
                    {
                      localObject6 = localObject8;
                      if (localObject2 != null)
                      {
                        localObject6 = MessagesStorage.getInstance();
                        localObject8 = AndroidUtilities.getPath((Uri)localObject2);
                        if (j != 0)
                          break label710;
                        i = 0;
                        localObject6 = (TLRPC.TL_photo)((MessagesStorage)localObject6).getSentFile((String)localObject8, i);
                      }
                    }
                  }
                  if (localObject6 != null)
                    break label960;
                  localObject6 = SendMessagesHelper.getInstance().generatePhotoSizes(str, (Uri)localObject2);
                }
                while (true)
                {
                  if (localObject6 != null)
                  {
                    localObject5 = new HashMap();
                    if (this.val$captions != null)
                      ((TLRPC.TL_photo)localObject6).caption = ((String)this.val$captions.get(m));
                    if (this.val$masks != null)
                    {
                      localObject8 = (ArrayList)this.val$masks.get(m);
                      if ((localObject8 != null) && (!((ArrayList)localObject8).isEmpty()));
                      SerializedData localSerializedData;
                      for (boolean bool = true; ; bool = false)
                      {
                        ((TLRPC.TL_photo)localObject6).has_stickers = bool;
                        if (!bool)
                          break label737;
                        localSerializedData = new SerializedData(((ArrayList)localObject8).size() * 20 + 4);
                        localSerializedData.writeInt32(((ArrayList)localObject8).size());
                        i = 0;
                        while (i < ((ArrayList)localObject8).size())
                        {
                          ((TLRPC.InputDocument)((ArrayList)localObject8).get(i)).serializeToStream(localSerializedData);
                          i += 1;
                        }
                        localObject7 = null;
                        break;
                        i = 3;
                        break label472;
                        label710: i = 3;
                        break label521;
                      }
                      ((HashMap)localObject5).put("masks", Utilities.bytesToHex(localSerializedData.toByteArray()));
                    }
                    label737: if (localObject7 != null)
                      ((HashMap)localObject5).put("originalPath", localObject7);
                    AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_photo)localObject6, (HashMap)localObject5)
                    {
                      public void run()
                      {
                        SendMessagesHelper.getInstance().sendMessage(this.val$photoFinal, null, SendMessagesHelper.18.this.val$dialog_id, SendMessagesHelper.18.this.val$reply_to_msg, null, this.val$params);
                      }
                    });
                    localObject6 = localObject4;
                    break;
                  }
                  localObject6 = localObject4;
                  localObject4 = localObject3;
                  if (localObject3 == null)
                  {
                    localObject4 = new ArrayList();
                    localObject6 = new ArrayList();
                    localArrayList = new ArrayList();
                  }
                  ((ArrayList)localObject4).add(localObject5);
                  ((ArrayList)localObject6).add(localObject7);
                  if (this.val$captions != null);
                  for (localObject3 = (String)this.val$captions.get(m); ; localObject3 = null)
                  {
                    localArrayList.add(localObject3);
                    localObject3 = localObject4;
                    break;
                  }
                  label872: if (this.val$inputContent != null)
                    this.val$inputContent.d();
                  if ((localObject3 != null) && (!((ArrayList)localObject3).isEmpty()))
                  {
                    i = 0;
                    while (i < ((ArrayList)localObject3).size())
                    {
                      SendMessagesHelper.access$1000((String)((ArrayList)localObject3).get(i), (String)((ArrayList)localObject4).get(i), null, (String)localObject5, this.val$dialog_id, this.val$reply_to_msg, (String)localArrayList.get(i));
                      i += 1;
                    }
                  }
                  return;
                }
              }
              label521: label960: label966: localObject7 = localObject5;
              localObject5 = localObject1;
              localObject1 = localObject7;
            }
            localObject1 = str;
          }
          label992: localObject2 = localObject1;
        }
      }
    }).start();
  }

  public static void prepareSendingPhotosSearch(ArrayList<MediaController.SearchImage> paramArrayList, long paramLong, MessageObject paramMessageObject)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()))
      return;
    new Thread(new Runnable(paramLong, paramArrayList, paramMessageObject)
    {
      public void run()
      {
        boolean bool2;
        int i;
        label13: MediaController.SearchImage localSearchImage;
        HashMap localHashMap;
        Object localObject2;
        Object localObject1;
        label84: Object localObject5;
        Object localObject4;
        TLRPC.TL_document localTL_document;
        if ((int)this.val$dialog_id == 0)
        {
          bool2 = true;
          i = 0;
          if (i >= this.val$photos.size())
            break label1211;
          localSearchImage = (MediaController.SearchImage)this.val$photos.get(i);
          if (localSearchImage.type != 1)
            break label755;
          localHashMap = new HashMap();
          if (!(localSearchImage.document instanceof TLRPC.TL_document))
            break label572;
          localObject2 = (TLRPC.TL_document)localSearchImage.document;
          localObject1 = FileLoader.getPathToAttach((TLObject)localObject2, true);
          localObject5 = localObject1;
          localObject4 = localObject2;
          if (localObject2 == null)
          {
            if (localSearchImage.localUrl != null)
              localHashMap.put("url", localSearchImage.localUrl);
            localObject2 = null;
            localTL_document = new TLRPC.TL_document();
            localTL_document.id = 0L;
            localTL_document.date = ConnectionsManager.getInstance().getCurrentTime();
            localObject4 = new TLRPC.TL_documentAttributeFilename();
            ((TLRPC.TL_documentAttributeFilename)localObject4).file_name = "animation.gif";
            localTL_document.attributes.add(localObject4);
            localTL_document.size = localSearchImage.size;
            localTL_document.dc_id = 0;
            if (!((File)localObject1).toString().endsWith("mp4"))
              break label699;
            localTL_document.mime_type = "video/mp4";
            localTL_document.attributes.add(new TLRPC.TL_documentAttributeAnimated());
            label226: if (!((File)localObject1).exists())
              break label710;
            localObject2 = localObject1;
            label238: localObject4 = localObject2;
            if (localObject2 == null)
            {
              localObject2 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
              localObject2 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
              localObject4 = localObject2;
              if (!((File)localObject2).exists())
                localObject4 = null;
            }
            if (localObject4 == null);
          }
        }
        while (true)
        {
          try
          {
            if (!((File)localObject4).getAbsolutePath().endsWith("mp4"))
              continue;
            localObject2 = ThumbnailUtils.createVideoThumbnail(((File)localObject4).getAbsolutePath(), 1);
            if (localObject2 == null)
              continue;
            localTL_document.thumb = ImageLoader.scaleAndSaveImage((Bitmap)localObject2, 90.0F, 90.0F, 55, bool2);
            ((Bitmap)localObject2).recycle();
            localObject5 = localObject1;
            localObject4 = localTL_document;
            if (localTL_document.thumb != null)
              continue;
            localTL_document.thumb = new TLRPC.TL_photoSize();
            localTL_document.thumb.w = localSearchImage.width;
            localTL_document.thumb.h = localSearchImage.height;
            localTL_document.thumb.size = 0;
            localTL_document.thumb.location = new TLRPC.TL_fileLocationUnavailable();
            localTL_document.thumb.type = "x";
            localObject4 = localTL_document;
            localObject5 = localObject1;
            if (localSearchImage.caption == null)
              continue;
            ((TLRPC.TL_document)localObject4).caption = localSearchImage.caption.toString();
            localObject1 = localSearchImage.imageUrl;
            if (localObject5 != null)
              continue;
            localObject1 = localSearchImage.imageUrl;
            if ((localHashMap == null) || (localSearchImage.imageUrl == null))
              continue;
            localHashMap.put("originalPath", localSearchImage.imageUrl);
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_document)localObject4, (String)localObject1, localHashMap)
            {
              public void run()
              {
                SendMessagesHelper.getInstance().sendMessage(this.val$documentFinal, null, this.val$pathFinal, SendMessagesHelper.16.this.val$dialog_id, SendMessagesHelper.16.this.val$reply_to_msg, null, this.val$params);
              }
            });
            i += 1;
            break label13;
            bool2 = false;
            break;
            label572: if (bool2)
              break label1212;
            localObject1 = MessagesStorage.getInstance();
            localObject2 = localSearchImage.imageUrl;
            if (bool2)
              continue;
            j = 1;
            localObject1 = (TLRPC.Document)((MessagesStorage)localObject1).getSentFile((String)localObject2, j);
            if (!(localObject1 instanceof TLRPC.TL_document))
              break label1212;
            localObject1 = (TLRPC.TL_document)localObject1;
            localObject2 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
            localObject4 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2);
            localObject2 = localObject1;
            localObject1 = localObject4;
            break label84;
            j = 4;
            continue;
            label699: localTL_document.mime_type = "image/gif";
            break label226;
            label710: localObject1 = null;
            break label238;
            localObject2 = ImageLoader.loadBitmap(((File)localObject4).getAbsolutePath(), null, 90.0F, 90.0F, true);
            continue;
          }
          catch (Exception localException)
          {
            FileLog.e(localException);
            continue;
            localObject1 = localObject5.toString();
            continue;
          }
          label755: boolean bool3 = true;
          boolean bool4 = true;
          Object localObject3 = null;
          if (!bool2)
          {
            localObject1 = MessagesStorage.getInstance();
            localObject3 = localSearchImage.imageUrl;
            if (bool2)
              break label1206;
          }
          label1206: for (int j = 0; ; j = 3)
          {
            localObject3 = (TLRPC.TL_photo)((MessagesStorage)localObject1).getSentFile((String)localObject3, j);
            localObject4 = localObject3;
            if (localObject3 == null)
            {
              localObject1 = Utilities.MD5(localSearchImage.imageUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.imageUrl, "jpg");
              localObject4 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject1);
              localObject1 = localObject3;
              boolean bool1 = bool4;
              if (((File)localObject4).exists())
              {
                localObject1 = localObject3;
                bool1 = bool4;
                if (((File)localObject4).length() != 0L)
                {
                  localObject3 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject4).toString(), null);
                  localObject1 = localObject3;
                  bool1 = bool4;
                  if (localObject3 != null)
                  {
                    bool1 = false;
                    localObject1 = localObject3;
                  }
                }
              }
              localObject4 = localObject1;
              bool3 = bool1;
              if (localObject1 == null)
              {
                localObject3 = Utilities.MD5(localSearchImage.thumbUrl) + "." + ImageLoader.getHttpUrlExtension(localSearchImage.thumbUrl, "jpg");
                localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
                if (((File)localObject3).exists())
                  localObject1 = SendMessagesHelper.getInstance().generatePhotoSizes(((File)localObject3).toString(), null);
                localObject4 = localObject1;
                bool3 = bool1;
                if (localObject1 == null)
                {
                  localObject4 = new TLRPC.TL_photo();
                  ((TLRPC.TL_photo)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
                  localObject1 = new TLRPC.TL_photoSize();
                  ((TLRPC.TL_photoSize)localObject1).w = localSearchImage.width;
                  ((TLRPC.TL_photoSize)localObject1).h = localSearchImage.height;
                  ((TLRPC.TL_photoSize)localObject1).size = 0;
                  ((TLRPC.TL_photoSize)localObject1).location = new TLRPC.TL_fileLocationUnavailable();
                  ((TLRPC.TL_photoSize)localObject1).type = "x";
                  ((TLRPC.TL_photo)localObject4).sizes.add(localObject1);
                  bool3 = bool1;
                }
              }
            }
            if (localObject4 == null)
              break;
            if (localSearchImage.caption != null)
              ((TLRPC.TL_photo)localObject4).caption = localSearchImage.caption.toString();
            localObject1 = new HashMap();
            if (localSearchImage.imageUrl != null)
              ((HashMap)localObject1).put("originalPath", localSearchImage.imageUrl);
            AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_photo)localObject4, bool3, localSearchImage, (HashMap)localObject1)
            {
              public void run()
              {
                SendMessagesHelper localSendMessagesHelper = SendMessagesHelper.getInstance();
                TLRPC.TL_photo localTL_photo = this.val$photoFinal;
                if (this.val$needDownloadHttpFinal);
                for (String str = this.val$searchImage.imageUrl; ; str = null)
                {
                  localSendMessagesHelper.sendMessage(localTL_photo, str, SendMessagesHelper.16.this.val$dialog_id, SendMessagesHelper.16.this.val$reply_to_msg, null, this.val$params);
                  return;
                }
              }
            });
            break;
          }
          label1211: return;
          label1212: localObject1 = null;
        }
      }
    }).start();
  }

  public static void prepareSendingText(String paramString, long paramLong)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramString, paramLong)
    {
      public void run()
      {
        Utilities.stageQueue.postRunnable(new Runnable()
        {
          public void run()
          {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                String str1 = SendMessagesHelper.access$1100(SendMessagesHelper.17.this.val$text);
                if (str1.length() != 0)
                {
                  int j = (int)Math.ceil(str1.length() / 4096.0F);
                  int i = 0;
                  while (i < j)
                  {
                    String str2 = str1.substring(i * 4096, Math.min((i + 1) * 4096, str1.length()));
                    SendMessagesHelper.getInstance().sendMessage(str2, SendMessagesHelper.17.this.val$dialog_id, null, null, true, null, null, null);
                    i += 1;
                  }
                }
              }
            });
          }
        });
      }
    });
  }

  public static void prepareSendingVideo(String paramString1, long paramLong1, long paramLong2, int paramInt1, int paramInt2, VideoEditedInfo paramVideoEditedInfo, long paramLong3, MessageObject paramMessageObject, String paramString2)
  {
    if ((paramString1 == null) || (paramString1.length() == 0))
      return;
    new Thread(new Runnable(paramLong3, paramVideoEditedInfo, paramString1, paramLong2, paramInt2, paramInt1, paramLong1, paramString2, paramMessageObject)
    {
      public void run()
      {
        Object localObject4 = null;
        boolean bool;
        Object localObject3;
        Object localObject1;
        Object localObject5;
        Object localObject2;
        if ((int)this.val$dialog_id == 0)
        {
          bool = true;
          if ((this.val$videoEditedInfo == null) && (!this.val$videoPath.endsWith("mp4")))
            break label692;
          localObject3 = this.val$videoPath;
          localObject1 = this.val$videoPath;
          localObject5 = new File((String)localObject1);
          localObject2 = (String)localObject1 + ((File)localObject5).length() + "_" + ((File)localObject5).lastModified();
          localObject1 = localObject2;
          if (this.val$videoEditedInfo != null)
          {
            localObject2 = (String)localObject2 + this.val$duration + "_" + this.val$videoEditedInfo.startTime + "_" + this.val$videoEditedInfo.endTime;
            localObject1 = localObject2;
            if (this.val$videoEditedInfo.resultWidth == this.val$videoEditedInfo.originalWidth)
              localObject1 = (String)localObject2 + "_" + this.val$videoEditedInfo.resultWidth;
          }
          if ((!bool) && (0 != 0))
            break label719;
          localObject4 = ImageLoader.scaleAndSaveImage(ThumbnailUtils.createVideoThumbnail(this.val$videoPath, 1), 90.0F, 90.0F, 55, bool);
          localObject2 = new TLRPC.TL_document();
          ((TLRPC.TL_document)localObject2).thumb = ((TLRPC.PhotoSize)localObject4);
          if (((TLRPC.TL_document)localObject2).thumb != null)
            break label554;
          ((TLRPC.TL_document)localObject2).thumb = new TLRPC.TL_photoSizeEmpty();
          ((TLRPC.TL_document)localObject2).thumb.type = "s";
          label279: ((TLRPC.TL_document)localObject2).mime_type = "video/mp4";
          UserConfig.saveConfig(false);
          localObject4 = new TLRPC.TL_documentAttributeVideo();
          ((TLRPC.TL_document)localObject2).attributes.add(localObject4);
          if (this.val$videoEditedInfo == null)
            break label648;
          if (this.val$videoEditedInfo.bitrate != -1)
            break label567;
          ((TLRPC.TL_document)localObject2).attributes.add(new TLRPC.TL_documentAttributeAnimated());
          SendMessagesHelper.access$1200(this.val$videoPath, (TLRPC.TL_documentAttributeVideo)localObject4, this.val$videoEditedInfo);
          localObject3 = this.val$videoEditedInfo;
          localObject5 = this.val$videoEditedInfo;
          int i = ((TLRPC.TL_documentAttributeVideo)localObject4).w;
          ((VideoEditedInfo)localObject5).resultWidth = i;
          ((VideoEditedInfo)localObject3).originalWidth = i;
          localObject3 = this.val$videoEditedInfo;
          localObject5 = this.val$videoEditedInfo;
          i = ((TLRPC.TL_documentAttributeVideo)localObject4).h;
          ((VideoEditedInfo)localObject5).resultHeight = i;
          ((VideoEditedInfo)localObject3).originalHeight = i;
          label417: ((TLRPC.TL_document)localObject2).size = (int)this.val$estimatedSize;
          localObject3 = "-2147483648_" + UserConfig.lastLocalId + ".mp4";
          UserConfig.lastLocalId -= 1;
          localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
          UserConfig.saveConfig(false);
          localObject4 = ((File)localObject3).getAbsolutePath();
          localObject3 = localObject2;
          localObject2 = localObject4;
        }
        while (true)
        {
          localObject4 = new HashMap();
          ((TLRPC.TL_document)localObject3).caption = this.val$caption;
          if (localObject1 != null)
            ((HashMap)localObject4).put("originalPath", localObject1);
          AndroidUtilities.runOnUIThread(new Runnable((TLRPC.TL_document)localObject3, (String)localObject2, (HashMap)localObject4)
          {
            public void run()
            {
              SendMessagesHelper.getInstance().sendMessage(this.val$videoFinal, SendMessagesHelper.19.this.val$videoEditedInfo, this.val$finalPath, SendMessagesHelper.19.this.val$dialog_id, SendMessagesHelper.19.this.val$reply_to_msg, null, this.val$params);
            }
          });
          return;
          bool = false;
          break;
          label554: ((TLRPC.TL_document)localObject2).thumb.type = "s";
          break label279;
          label567: ((TLRPC.TL_documentAttributeVideo)localObject4).duration = (int)(this.val$duration / 1000L);
          if ((this.val$videoEditedInfo.rotationValue == 90) || (this.val$videoEditedInfo.rotationValue == 270))
          {
            ((TLRPC.TL_documentAttributeVideo)localObject4).w = this.val$height;
            ((TLRPC.TL_documentAttributeVideo)localObject4).h = this.val$width;
            break label417;
          }
          ((TLRPC.TL_documentAttributeVideo)localObject4).w = this.val$width;
          ((TLRPC.TL_documentAttributeVideo)localObject4).h = this.val$height;
          break label417;
          label648: if (((File)localObject5).exists())
            ((TLRPC.TL_document)localObject2).size = (int)((File)localObject5).length();
          SendMessagesHelper.access$1200(this.val$videoPath, (TLRPC.TL_documentAttributeVideo)localObject4, null);
          localObject4 = localObject2;
          localObject2 = localObject3;
          localObject3 = localObject4;
          continue;
          label692: SendMessagesHelper.access$1000(this.val$videoPath, this.val$videoPath, null, null, this.val$dialog_id, this.val$reply_to_msg, this.val$caption);
          return;
          label719: localObject2 = localObject3;
          localObject3 = localObject4;
        }
      }
    }).start();
  }

  private void putToDelayedMessages(String paramString, DelayedMessage paramDelayedMessage)
  {
    ArrayList localArrayList2 = (ArrayList)this.delayedMessages.get(paramString);
    ArrayList localArrayList1 = localArrayList2;
    if (localArrayList2 == null)
    {
      localArrayList1 = new ArrayList();
      this.delayedMessages.put(paramString, localArrayList1);
    }
    localArrayList1.add(paramDelayedMessage);
  }

  private void sendLocation(Location paramLocation)
  {
    TLRPC.TL_messageMediaGeo localTL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
    localTL_messageMediaGeo.geo = new TLRPC.TL_geoPoint();
    localTL_messageMediaGeo.geo.lat = paramLocation.getLatitude();
    localTL_messageMediaGeo.geo._long = paramLocation.getLongitude();
    paramLocation = this.waitingForLocation.entrySet().iterator();
    while (paramLocation.hasNext())
    {
      MessageObject localMessageObject = (MessageObject)((Map.Entry)paramLocation.next()).getValue();
      getInstance().sendMessage(localTL_messageMediaGeo, localMessageObject.getDialogId(), localMessageObject, null, null);
    }
  }

  // ERROR //
  private void sendMessage(String paramString1, TLRPC.MessageMedia paramMessageMedia, TLRPC.TL_photo paramTL_photo, VideoEditedInfo paramVideoEditedInfo, TLRPC.User paramUser, TLRPC.TL_document paramTL_document, TLRPC.TL_game paramTL_game, long paramLong, String paramString2, MessageObject paramMessageObject1, TLRPC.WebPage paramWebPage, boolean paramBoolean, MessageObject paramMessageObject2, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    // Byte code:
    //   0: lload 8
    //   2: lconst_0
    //   3: lcmp
    //   4: ifne +4 -> 8
    //   7: return
    //   8: aload 17
    //   10: ifnull +7750 -> 7760
    //   13: aload 17
    //   15: ldc_w 933
    //   18: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   21: ifeq +7739 -> 7760
    //   24: aload 17
    //   26: ldc_w 933
    //   29: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   32: checkcast 340	java/lang/String
    //   35: astore 27
    //   37: aconst_null
    //   38: astore 24
    //   40: lload 8
    //   42: l2i
    //   43: istore 22
    //   45: lload 8
    //   47: bipush 32
    //   49: lshr
    //   50: l2i
    //   51: istore 21
    //   53: iconst_0
    //   54: istore 19
    //   56: iload 22
    //   58: ifeq +92 -> 150
    //   61: iload 22
    //   63: invokestatic 1060	org/vidogram/messenger/MessagesController:getInputPeer	(I)Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   66: astore 28
    //   68: iload 22
    //   70: ifne +86 -> 156
    //   73: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   76: iload 21
    //   78: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   81: invokevirtual 774	org/vidogram/messenger/MessagesController:getEncryptedChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$EncryptedChat;
    //   84: astore 26
    //   86: aload 26
    //   88: ifnonnull +7669 -> 7757
    //   91: aload 14
    //   93: ifnull -86 -> 7
    //   96: invokestatic 804	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   99: aload 14
    //   101: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   104: invokevirtual 1063	org/vidogram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/vidogram/tgnet/TLRPC$Message;)V
    //   107: aload 14
    //   109: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   112: iconst_2
    //   113: putfield 1066	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   116: invokestatic 165	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   119: getstatic 1069	org/vidogram/messenger/NotificationCenter:messageSendError	I
    //   122: iconst_1
    //   123: anewarray 4	java/lang/Object
    //   126: dup
    //   127: iconst_0
    //   128: aload 14
    //   130: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   133: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   136: aastore
    //   137: invokevirtual 1076	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   140: aload_0
    //   141: aload 14
    //   143: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   146: invokevirtual 1080	org/vidogram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   149: return
    //   150: aconst_null
    //   151: astore 28
    //   153: goto -85 -> 68
    //   156: aload 28
    //   158: instanceof 1082
    //   161: ifeq +7590 -> 7751
    //   164: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   167: aload 28
    //   169: getfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   172: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   175: invokevirtual 1091	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   178: astore 25
    //   180: aload 25
    //   182: ifnull +478 -> 660
    //   185: aload 25
    //   187: getfield 1096	org/vidogram/tgnet/TLRPC$Chat:megagroup	Z
    //   190: ifne +470 -> 660
    //   193: iconst_1
    //   194: istore 18
    //   196: aconst_null
    //   197: astore 26
    //   199: iload 18
    //   201: istore 19
    //   203: aload 14
    //   205: ifnull +769 -> 974
    //   208: aload 14
    //   210: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   213: astore 7
    //   215: aload 14
    //   217: invokevirtual 1099	org/vidogram/messenger/MessageObject:isForwarded	()Z
    //   220: istore 23
    //   222: iload 23
    //   224: ifeq +442 -> 666
    //   227: iconst_4
    //   228: istore 18
    //   230: aload_3
    //   231: astore 24
    //   233: aload 7
    //   235: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   238: lconst_0
    //   239: lcmp
    //   240: ifne +12 -> 252
    //   243: aload 7
    //   245: aload_0
    //   246: invokevirtual 1105	org/vidogram/messenger/SendMessagesHelper:getNextRandomId	()J
    //   249: putfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   252: aload 17
    //   254: ifnull +65 -> 319
    //   257: aload 17
    //   259: ldc_w 1107
    //   262: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   265: ifeq +54 -> 319
    //   268: aload 26
    //   270: ifnull +2260 -> 2530
    //   273: aload 7
    //   275: aload 17
    //   277: ldc_w 1109
    //   280: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   283: checkcast 340	java/lang/String
    //   286: putfield 1112	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   289: aload 7
    //   291: getfield 1112	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   294: ifnonnull +11 -> 305
    //   297: aload 7
    //   299: ldc_w 743
    //   302: putfield 1112	org/vidogram/tgnet/TLRPC$Message:via_bot_name	Ljava/lang/String;
    //   305: aload 7
    //   307: aload 7
    //   309: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   312: sipush 2048
    //   315: ior
    //   316: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   319: aload 7
    //   321: aload 17
    //   323: putfield 1116	org/vidogram/tgnet/TLRPC$Message:params	Ljava/util/HashMap;
    //   326: aload 14
    //   328: ifnull +11 -> 339
    //   331: aload 14
    //   333: getfield 1119	org/vidogram/messenger/MessageObject:resendAsIs	Z
    //   336: ifne +93 -> 429
    //   339: aload 7
    //   341: invokestatic 552	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   344: invokevirtual 812	org/vidogram/tgnet/ConnectionsManager:getCurrentTime	()I
    //   347: putfield 1120	org/vidogram/tgnet/TLRPC$Message:date	I
    //   350: aload 28
    //   352: instanceof 1082
    //   355: ifeq +2224 -> 2579
    //   358: iload 19
    //   360: ifeq +23 -> 383
    //   363: aload 7
    //   365: iconst_1
    //   366: putfield 1123	org/vidogram/tgnet/TLRPC$Message:views	I
    //   369: aload 7
    //   371: aload 7
    //   373: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   376: sipush 1024
    //   379: ior
    //   380: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   383: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   386: aload 28
    //   388: getfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   391: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   394: invokevirtual 1091	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   397: astore_3
    //   398: aload_3
    //   399: ifnull +30 -> 429
    //   402: aload_3
    //   403: getfield 1096	org/vidogram/tgnet/TLRPC$Chat:megagroup	Z
    //   406: ifeq +2149 -> 2555
    //   409: aload 7
    //   411: aload 7
    //   413: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   416: ldc_w 1124
    //   419: ior
    //   420: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   423: aload 7
    //   425: iconst_1
    //   426: putfield 1127	org/vidogram/tgnet/TLRPC$Message:unread	Z
    //   429: aload 7
    //   431: aload 7
    //   433: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   436: sipush 512
    //   439: ior
    //   440: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   443: aload 7
    //   445: lload 8
    //   447: putfield 1130	org/vidogram/tgnet/TLRPC$Message:dialog_id	J
    //   450: aload 11
    //   452: ifnull +57 -> 509
    //   455: aload 26
    //   457: ifnull +2131 -> 2588
    //   460: aload 11
    //   462: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   465: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   468: lconst_0
    //   469: lcmp
    //   470: ifeq +2118 -> 2588
    //   473: aload 7
    //   475: aload 11
    //   477: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   480: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   483: putfield 1133	org/vidogram/tgnet/TLRPC$Message:reply_to_random_id	J
    //   486: aload 7
    //   488: aload 7
    //   490: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   493: bipush 8
    //   495: ior
    //   496: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   499: aload 7
    //   501: aload 11
    //   503: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   506: putfield 1136	org/vidogram/tgnet/TLRPC$Message:reply_to_msg_id	I
    //   509: aload 16
    //   511: ifnull +28 -> 539
    //   514: aload 26
    //   516: ifnonnull +23 -> 539
    //   519: aload 7
    //   521: aload 7
    //   523: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   526: bipush 64
    //   528: ior
    //   529: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   532: aload 7
    //   534: aload 16
    //   536: putfield 1137	org/vidogram/tgnet/TLRPC$Message:reply_markup	Lorg/vidogram/tgnet/TLRPC$ReplyMarkup;
    //   539: iload 22
    //   541: ifeq +2531 -> 3072
    //   544: iload 21
    //   546: iconst_1
    //   547: if_icmpne +2468 -> 3015
    //   550: aload_0
    //   551: getfield 138	org/vidogram/messenger/SendMessagesHelper:currentChatInfo	Lorg/vidogram/tgnet/TLRPC$ChatFull;
    //   554: ifnonnull +2050 -> 2604
    //   557: invokestatic 804	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   560: aload 7
    //   562: invokevirtual 1063	org/vidogram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/vidogram/tgnet/TLRPC$Message;)V
    //   565: invokestatic 165	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   568: getstatic 1069	org/vidogram/messenger/NotificationCenter:messageSendError	I
    //   571: iconst_1
    //   572: anewarray 4	java/lang/Object
    //   575: dup
    //   576: iconst_0
    //   577: aload 7
    //   579: getfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   582: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   585: aastore
    //   586: invokevirtual 1076	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   589: aload_0
    //   590: aload 7
    //   592: getfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   595: invokevirtual 1080	org/vidogram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   598: return
    //   599: astore_1
    //   600: aconst_null
    //   601: astore_3
    //   602: aload 7
    //   604: astore_2
    //   605: aload_1
    //   606: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   609: invokestatic 804	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   612: aload_2
    //   613: invokevirtual 1063	org/vidogram/messenger/MessagesStorage:markMessageAsSendError	(Lorg/vidogram/tgnet/TLRPC$Message;)V
    //   616: aload_3
    //   617: ifnull +11 -> 628
    //   620: aload_3
    //   621: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   624: iconst_2
    //   625: putfield 1066	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   628: invokestatic 165	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   631: getstatic 1069	org/vidogram/messenger/NotificationCenter:messageSendError	I
    //   634: iconst_1
    //   635: anewarray 4	java/lang/Object
    //   638: dup
    //   639: iconst_0
    //   640: aload_2
    //   641: getfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   644: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   647: aastore
    //   648: invokevirtual 1076	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   651: aload_0
    //   652: aload_2
    //   653: getfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   656: invokevirtual 1080	org/vidogram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   659: return
    //   660: iconst_0
    //   661: istore 18
    //   663: goto -467 -> 196
    //   666: aload 14
    //   668: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   671: ifne +55 -> 726
    //   674: aload 14
    //   676: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   679: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   682: instanceof 1145
    //   685: ifeq +32 -> 717
    //   688: goto +7078 -> 7766
    //   691: aload 17
    //   693: ifnull +7046 -> 7739
    //   696: aload 17
    //   698: ldc_w 1147
    //   701: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   704: ifeq +7035 -> 7739
    //   707: bipush 9
    //   709: istore 18
    //   711: aload_3
    //   712: astore 24
    //   714: goto -481 -> 233
    //   717: aload 7
    //   719: getfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   722: astore_1
    //   723: goto +7043 -> 7766
    //   726: aload 14
    //   728: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   731: iconst_4
    //   732: if_icmpne +15 -> 747
    //   735: aload 7
    //   737: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   740: astore_2
    //   741: iconst_1
    //   742: istore 18
    //   744: goto -53 -> 691
    //   747: aload 14
    //   749: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   752: iconst_1
    //   753: if_icmpne +21 -> 774
    //   756: aload 7
    //   758: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   761: getfield 1154	org/vidogram/tgnet/TLRPC$MessageMedia:photo	Lorg/vidogram/tgnet/TLRPC$Photo;
    //   764: checkcast 1156	org/vidogram/tgnet/TLRPC$TL_photo
    //   767: astore_3
    //   768: iconst_2
    //   769: istore 18
    //   771: goto -80 -> 691
    //   774: aload 14
    //   776: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   779: iconst_3
    //   780: if_icmpeq +8 -> 788
    //   783: aload 4
    //   785: ifnull +22 -> 807
    //   788: aload 7
    //   790: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   793: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   796: checkcast 476	org/vidogram/tgnet/TLRPC$TL_document
    //   799: astore 6
    //   801: iconst_3
    //   802: istore 18
    //   804: goto -113 -> 691
    //   807: aload 14
    //   809: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   812: bipush 12
    //   814: if_icmpne +71 -> 885
    //   817: new 1162	org/vidogram/tgnet/TLRPC$TL_userRequest_old2
    //   820: dup
    //   821: invokespecial 1163	org/vidogram/tgnet/TLRPC$TL_userRequest_old2:<init>	()V
    //   824: astore 5
    //   826: aload 5
    //   828: aload 7
    //   830: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   833: getfield 1164	org/vidogram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   836: putfield 691	org/vidogram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   839: aload 5
    //   841: aload 7
    //   843: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   846: getfield 1165	org/vidogram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   849: putfield 695	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   852: aload 5
    //   854: aload 7
    //   856: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   859: getfield 1166	org/vidogram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   862: putfield 699	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   865: aload 5
    //   867: aload 7
    //   869: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   872: getfield 1169	org/vidogram/tgnet/TLRPC$MessageMedia:user_id	I
    //   875: putfield 1170	org/vidogram/tgnet/TLRPC$User:id	I
    //   878: bipush 6
    //   880: istore 18
    //   882: goto -191 -> 691
    //   885: aload 14
    //   887: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   890: bipush 8
    //   892: if_icmpeq +33 -> 925
    //   895: aload 14
    //   897: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   900: bipush 9
    //   902: if_icmpeq +23 -> 925
    //   905: aload 14
    //   907: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   910: bipush 13
    //   912: if_icmpeq +13 -> 925
    //   915: aload 14
    //   917: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   920: bipush 14
    //   922: if_icmpne +23 -> 945
    //   925: aload 7
    //   927: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   930: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   933: checkcast 476	org/vidogram/tgnet/TLRPC$TL_document
    //   936: astore 6
    //   938: bipush 7
    //   940: istore 18
    //   942: goto -251 -> 691
    //   945: aload 14
    //   947: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   950: iconst_2
    //   951: if_icmpne +6794 -> 7745
    //   954: aload 7
    //   956: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   959: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   962: checkcast 476	org/vidogram/tgnet/TLRPC$TL_document
    //   965: astore 6
    //   967: bipush 8
    //   969: istore 18
    //   971: goto -280 -> 691
    //   974: aload_1
    //   975: ifnull +338 -> 1313
    //   978: aload 26
    //   980: ifnull +267 -> 1247
    //   983: new 1172	org/vidogram/tgnet/TLRPC$TL_message_secret
    //   986: dup
    //   987: invokespecial 1173	org/vidogram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   990: astore 7
    //   992: aload 15
    //   994: ifnull +26 -> 1020
    //   997: aload 7
    //   999: astore 24
    //   1001: aload 15
    //   1003: invokevirtual 962	java/util/ArrayList:isEmpty	()Z
    //   1006: ifne +14 -> 1020
    //   1009: aload 7
    //   1011: astore 24
    //   1013: aload 7
    //   1015: aload 15
    //   1017: putfield 1174	org/vidogram/tgnet/TLRPC$Message:entities	Ljava/util/ArrayList;
    //   1020: aload 12
    //   1022: astore 25
    //   1024: aload 26
    //   1026: ifnull +58 -> 1084
    //   1029: aload 12
    //   1031: astore 25
    //   1033: aload 7
    //   1035: astore 24
    //   1037: aload 12
    //   1039: instanceof 615
    //   1042: ifeq +42 -> 1084
    //   1045: aload 7
    //   1047: astore 24
    //   1049: aload 12
    //   1051: getfield 629	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1054: ifnull +205 -> 1259
    //   1057: aload 7
    //   1059: astore 24
    //   1061: new 1176	org/vidogram/tgnet/TLRPC$TL_webPageUrlPending
    //   1064: dup
    //   1065: invokespecial 1177	org/vidogram/tgnet/TLRPC$TL_webPageUrlPending:<init>	()V
    //   1068: astore 25
    //   1070: aload 7
    //   1072: astore 24
    //   1074: aload 25
    //   1076: aload 12
    //   1078: getfield 629	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1081: putfield 629	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   1084: aload 25
    //   1086: ifnonnull +179 -> 1265
    //   1089: aload 7
    //   1091: astore 24
    //   1093: aload 7
    //   1095: new 1179	org/vidogram/tgnet/TLRPC$TL_messageMediaEmpty
    //   1098: dup
    //   1099: invokespecial 1180	org/vidogram/tgnet/TLRPC$TL_messageMediaEmpty:<init>	()V
    //   1102: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1105: aload 17
    //   1107: ifnull +200 -> 1307
    //   1110: aload 7
    //   1112: astore 24
    //   1114: aload 17
    //   1116: ldc_w 1147
    //   1119: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1122: ifeq +185 -> 1307
    //   1125: bipush 9
    //   1127: istore 18
    //   1129: aload 7
    //   1131: astore 24
    //   1133: aload 7
    //   1135: aload_1
    //   1136: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1139: aload 25
    //   1141: astore 12
    //   1143: aload 7
    //   1145: astore 24
    //   1147: aload 7
    //   1149: getfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1152: ifnonnull +15 -> 1167
    //   1155: aload 7
    //   1157: astore 24
    //   1159: aload 7
    //   1161: ldc_w 743
    //   1164: putfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1167: aload 7
    //   1169: astore 24
    //   1171: invokestatic 1185	org/vidogram/messenger/UserConfig:getNewMessageId	()I
    //   1174: istore 20
    //   1176: aload 7
    //   1178: astore 24
    //   1180: aload 7
    //   1182: iload 20
    //   1184: putfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   1187: aload 7
    //   1189: astore 24
    //   1191: aload 7
    //   1193: iload 20
    //   1195: putfield 1186	org/vidogram/tgnet/TLRPC$Message:local_id	I
    //   1198: aload 7
    //   1200: astore 24
    //   1202: aload 7
    //   1204: iconst_1
    //   1205: putfield 1189	org/vidogram/tgnet/TLRPC$Message:out	Z
    //   1208: iload 19
    //   1210: ifeq +1287 -> 2497
    //   1213: aload 28
    //   1215: ifnull +1282 -> 2497
    //   1218: aload 7
    //   1220: astore 24
    //   1222: aload 7
    //   1224: aload 28
    //   1226: getfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   1229: ineg
    //   1230: putfield 1192	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   1233: aload 7
    //   1235: astore 24
    //   1237: iconst_0
    //   1238: invokestatic 1196	org/vidogram/messenger/UserConfig:saveConfig	(Z)V
    //   1241: aload_3
    //   1242: astore 24
    //   1244: goto -1011 -> 233
    //   1247: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   1250: dup
    //   1251: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   1254: astore 7
    //   1256: goto -264 -> 992
    //   1259: aconst_null
    //   1260: astore 25
    //   1262: goto -178 -> 1084
    //   1265: aload 7
    //   1267: astore 24
    //   1269: aload 7
    //   1271: new 1201	org/vidogram/tgnet/TLRPC$TL_messageMediaWebPage
    //   1274: dup
    //   1275: invokespecial 1202	org/vidogram/tgnet/TLRPC$TL_messageMediaWebPage:<init>	()V
    //   1278: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1281: aload 7
    //   1283: astore 24
    //   1285: aload 7
    //   1287: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1290: aload 25
    //   1292: putfield 1206	org/vidogram/tgnet/TLRPC$MessageMedia:webpage	Lorg/vidogram/tgnet/TLRPC$WebPage;
    //   1295: goto -190 -> 1105
    //   1298: astore_1
    //   1299: aconst_null
    //   1300: astore_3
    //   1301: aload 24
    //   1303: astore_2
    //   1304: goto -699 -> 605
    //   1307: iconst_0
    //   1308: istore 18
    //   1310: goto -181 -> 1129
    //   1313: aload_2
    //   1314: ifnull +82 -> 1396
    //   1317: aload 26
    //   1319: ifnull +65 -> 1384
    //   1322: new 1172	org/vidogram/tgnet/TLRPC$TL_message_secret
    //   1325: dup
    //   1326: invokespecial 1173	org/vidogram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1329: astore 7
    //   1331: aload 7
    //   1333: astore 24
    //   1335: aload 7
    //   1337: aload_2
    //   1338: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1341: aload 7
    //   1343: astore 24
    //   1345: aload 7
    //   1347: ldc_w 743
    //   1350: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1353: aload 17
    //   1355: ifnull +6417 -> 7772
    //   1358: aload 7
    //   1360: astore 24
    //   1362: aload 17
    //   1364: ldc_w 1147
    //   1367: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1370: istore 23
    //   1372: iload 23
    //   1374: ifeq +6398 -> 7772
    //   1377: bipush 9
    //   1379: istore 18
    //   1381: goto -238 -> 1143
    //   1384: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   1387: dup
    //   1388: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   1391: astore 7
    //   1393: goto -62 -> 1331
    //   1396: aload_3
    //   1397: ifnull +232 -> 1629
    //   1400: aload 26
    //   1402: ifnull +166 -> 1568
    //   1405: new 1172	org/vidogram/tgnet/TLRPC$TL_message_secret
    //   1408: dup
    //   1409: invokespecial 1173	org/vidogram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1412: astore 7
    //   1414: aload 7
    //   1416: astore 24
    //   1418: aload 7
    //   1420: new 1208	org/vidogram/tgnet/TLRPC$TL_messageMediaPhoto
    //   1423: dup
    //   1424: invokespecial 1209	org/vidogram/tgnet/TLRPC$TL_messageMediaPhoto:<init>	()V
    //   1427: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1430: aload 7
    //   1432: astore 24
    //   1434: aload 7
    //   1436: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1439: astore 29
    //   1441: aload 7
    //   1443: astore 24
    //   1445: aload_3
    //   1446: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1449: ifnull +131 -> 1580
    //   1452: aload 7
    //   1454: astore 24
    //   1456: aload_3
    //   1457: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   1460: astore 25
    //   1462: aload 7
    //   1464: astore 24
    //   1466: aload 29
    //   1468: aload 25
    //   1470: putfield 1211	org/vidogram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   1473: aload 7
    //   1475: astore 24
    //   1477: aload 7
    //   1479: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1482: aload_3
    //   1483: putfield 1154	org/vidogram/tgnet/TLRPC$MessageMedia:photo	Lorg/vidogram/tgnet/TLRPC$Photo;
    //   1486: aload 17
    //   1488: ifnull +6290 -> 7778
    //   1491: aload 7
    //   1493: astore 24
    //   1495: aload 17
    //   1497: ldc_w 1147
    //   1500: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1503: ifeq +6275 -> 7778
    //   1506: bipush 9
    //   1508: istore 18
    //   1510: aload 7
    //   1512: astore 24
    //   1514: aload 7
    //   1516: ldc_w 1213
    //   1519: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1522: aload 10
    //   1524: ifnull +64 -> 1588
    //   1527: aload 7
    //   1529: astore 24
    //   1531: aload 10
    //   1533: invokevirtual 347	java/lang/String:length	()I
    //   1536: ifle +52 -> 1588
    //   1539: aload 7
    //   1541: astore 24
    //   1543: aload 10
    //   1545: ldc_w 1215
    //   1548: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   1551: ifeq +37 -> 1588
    //   1554: aload 7
    //   1556: astore 24
    //   1558: aload 7
    //   1560: aload 10
    //   1562: putfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1565: goto -422 -> 1143
    //   1568: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   1571: dup
    //   1572: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   1575: astore 7
    //   1577: goto -163 -> 1414
    //   1580: ldc_w 743
    //   1583: astore 25
    //   1585: goto -123 -> 1462
    //   1588: aload 7
    //   1590: astore 24
    //   1592: aload 7
    //   1594: aload_3
    //   1595: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   1598: aload_3
    //   1599: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   1602: invokevirtual 605	java/util/ArrayList:size	()I
    //   1605: iconst_1
    //   1606: isub
    //   1607: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   1610: checkcast 926	org/vidogram/tgnet/TLRPC$PhotoSize
    //   1613: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   1616: iconst_1
    //   1617: invokestatic 425	org/vidogram/messenger/FileLoader:getPathToAttach	(Lorg/vidogram/tgnet/TLObject;Z)Ljava/io/File;
    //   1620: invokevirtual 401	java/io/File:toString	()Ljava/lang/String;
    //   1623: putfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   1626: goto -483 -> 1143
    //   1629: aload 7
    //   1631: ifnull +84 -> 1715
    //   1634: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   1637: dup
    //   1638: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   1641: astore 24
    //   1643: aload 24
    //   1645: new 1145	org/vidogram/tgnet/TLRPC$TL_messageMediaGame
    //   1648: dup
    //   1649: invokespecial 1220	org/vidogram/tgnet/TLRPC$TL_messageMediaGame:<init>	()V
    //   1652: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1655: aload 24
    //   1657: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1660: ldc_w 743
    //   1663: putfield 1211	org/vidogram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   1666: aload 24
    //   1668: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1671: aload 7
    //   1673: putfield 1224	org/vidogram/tgnet/TLRPC$MessageMedia:game	Lorg/vidogram/tgnet/TLRPC$TL_game;
    //   1676: aload 24
    //   1678: ldc_w 743
    //   1681: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1684: aload 17
    //   1686: ifnull +6043 -> 7729
    //   1689: aload 17
    //   1691: ldc_w 1147
    //   1694: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1697: istore 23
    //   1699: iload 23
    //   1701: ifeq +6028 -> 7729
    //   1704: bipush 9
    //   1706: istore 18
    //   1708: aload 24
    //   1710: astore 7
    //   1712: goto -569 -> 1143
    //   1715: aload 5
    //   1717: ifnull +240 -> 1957
    //   1720: aload 26
    //   1722: ifnull +223 -> 1945
    //   1725: new 1172	org/vidogram/tgnet/TLRPC$TL_message_secret
    //   1728: dup
    //   1729: invokespecial 1173	org/vidogram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1732: astore 7
    //   1734: aload 7
    //   1736: astore 24
    //   1738: aload 7
    //   1740: new 1226	org/vidogram/tgnet/TLRPC$TL_messageMediaContact
    //   1743: dup
    //   1744: invokespecial 1227	org/vidogram/tgnet/TLRPC$TL_messageMediaContact:<init>	()V
    //   1747: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1750: aload 7
    //   1752: astore 24
    //   1754: aload 7
    //   1756: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1759: aload 5
    //   1761: getfield 691	org/vidogram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   1764: putfield 1164	org/vidogram/tgnet/TLRPC$MessageMedia:phone_number	Ljava/lang/String;
    //   1767: aload 7
    //   1769: astore 24
    //   1771: aload 7
    //   1773: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1776: aload 5
    //   1778: getfield 695	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1781: putfield 1165	org/vidogram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1784: aload 7
    //   1786: astore 24
    //   1788: aload 7
    //   1790: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1793: aload 5
    //   1795: getfield 699	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1798: putfield 1166	org/vidogram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1801: aload 7
    //   1803: astore 24
    //   1805: aload 7
    //   1807: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1810: aload 5
    //   1812: getfield 1170	org/vidogram/tgnet/TLRPC$User:id	I
    //   1815: putfield 1169	org/vidogram/tgnet/TLRPC$MessageMedia:user_id	I
    //   1818: aload 7
    //   1820: astore 24
    //   1822: aload 7
    //   1824: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1827: getfield 1165	org/vidogram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1830: ifnonnull +30 -> 1860
    //   1833: aload 7
    //   1835: astore 24
    //   1837: aload 7
    //   1839: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1842: ldc_w 743
    //   1845: putfield 1165	org/vidogram/tgnet/TLRPC$MessageMedia:first_name	Ljava/lang/String;
    //   1848: aload 7
    //   1850: astore 24
    //   1852: aload 5
    //   1854: ldc_w 743
    //   1857: putfield 695	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   1860: aload 7
    //   1862: astore 24
    //   1864: aload 7
    //   1866: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1869: getfield 1166	org/vidogram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1872: ifnonnull +30 -> 1902
    //   1875: aload 7
    //   1877: astore 24
    //   1879: aload 7
    //   1881: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1884: ldc_w 743
    //   1887: putfield 1166	org/vidogram/tgnet/TLRPC$MessageMedia:last_name	Ljava/lang/String;
    //   1890: aload 7
    //   1892: astore 24
    //   1894: aload 5
    //   1896: ldc_w 743
    //   1899: putfield 699	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   1902: aload 7
    //   1904: astore 24
    //   1906: aload 7
    //   1908: ldc_w 743
    //   1911: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   1914: aload 17
    //   1916: ifnull +5868 -> 7784
    //   1919: aload 7
    //   1921: astore 24
    //   1923: aload 17
    //   1925: ldc_w 1147
    //   1928: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   1931: istore 23
    //   1933: iload 23
    //   1935: ifeq +5849 -> 7784
    //   1938: bipush 9
    //   1940: istore 18
    //   1942: goto -799 -> 1143
    //   1945: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   1948: dup
    //   1949: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   1952: astore 7
    //   1954: goto -220 -> 1734
    //   1957: aload 6
    //   1959: ifnull +5760 -> 7719
    //   1962: aload 26
    //   1964: ifnull +385 -> 2349
    //   1967: new 1172	org/vidogram/tgnet/TLRPC$TL_message_secret
    //   1970: dup
    //   1971: invokespecial 1173	org/vidogram/tgnet/TLRPC$TL_message_secret:<init>	()V
    //   1974: astore 7
    //   1976: aload 7
    //   1978: astore 24
    //   1980: aload 7
    //   1982: new 1229	org/vidogram/tgnet/TLRPC$TL_messageMediaDocument
    //   1985: dup
    //   1986: invokespecial 1230	org/vidogram/tgnet/TLRPC$TL_messageMediaDocument:<init>	()V
    //   1989: putfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   1992: aload 7
    //   1994: astore 24
    //   1996: aload 7
    //   1998: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2001: astore 29
    //   2003: aload 7
    //   2005: astore 24
    //   2007: aload 6
    //   2009: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2012: ifnull +349 -> 2361
    //   2015: aload 7
    //   2017: astore 24
    //   2019: aload 6
    //   2021: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   2024: astore 25
    //   2026: aload 7
    //   2028: astore 24
    //   2030: aload 29
    //   2032: aload 25
    //   2034: putfield 1211	org/vidogram/tgnet/TLRPC$MessageMedia:caption	Ljava/lang/String;
    //   2037: aload 7
    //   2039: astore 24
    //   2041: aload 7
    //   2043: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   2046: aload 6
    //   2048: putfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   2051: aload 17
    //   2053: ifnull +316 -> 2369
    //   2056: aload 7
    //   2058: astore 24
    //   2060: aload 17
    //   2062: ldc_w 1147
    //   2065: invokevirtual 1056	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
    //   2068: ifeq +301 -> 2369
    //   2071: bipush 9
    //   2073: istore 18
    //   2075: aload 4
    //   2077: ifnonnull +331 -> 2408
    //   2080: aload 7
    //   2082: astore 24
    //   2084: aload 7
    //   2086: ldc_w 1213
    //   2089: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2092: aload 26
    //   2094: ifnull +331 -> 2425
    //   2097: aload 7
    //   2099: astore 24
    //   2101: aload 6
    //   2103: getfield 531	org/vidogram/tgnet/TLRPC$TL_document:dc_id	I
    //   2106: ifle +319 -> 2425
    //   2109: aload 7
    //   2111: astore 24
    //   2113: aload 6
    //   2115: invokestatic 1234	org/vidogram/messenger/MessageObject:isStickerDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   2118: ifne +307 -> 2425
    //   2121: aload 7
    //   2123: astore 24
    //   2125: aload 7
    //   2127: aload 6
    //   2129: invokestatic 398	org/vidogram/messenger/FileLoader:getPathToAttach	(Lorg/vidogram/tgnet/TLObject;)Ljava/io/File;
    //   2132: invokevirtual 401	java/io/File:toString	()Ljava/lang/String;
    //   2135: putfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2138: aload 26
    //   2140: ifnull +5576 -> 7716
    //   2143: aload 7
    //   2145: astore 24
    //   2147: aload 6
    //   2149: invokestatic 1234	org/vidogram/messenger/MessageObject:isStickerDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   2152: ifeq +5564 -> 7716
    //   2155: iconst_0
    //   2156: istore 20
    //   2158: aload 7
    //   2160: astore 24
    //   2162: iload 20
    //   2164: aload 6
    //   2166: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2169: invokevirtual 605	java/util/ArrayList:size	()I
    //   2172: if_icmpge +5544 -> 7716
    //   2175: aload 7
    //   2177: astore 24
    //   2179: aload 6
    //   2181: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2184: iload 20
    //   2186: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   2189: checkcast 1236	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   2192: astore 25
    //   2194: aload 7
    //   2196: astore 24
    //   2198: aload 25
    //   2200: instanceof 903
    //   2203: ifeq +5601 -> 7804
    //   2206: aload 7
    //   2208: astore 24
    //   2210: aload 6
    //   2212: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2215: iload 20
    //   2217: invokevirtual 1239	java/util/ArrayList:remove	(I)Ljava/lang/Object;
    //   2220: pop
    //   2221: aload 7
    //   2223: astore 24
    //   2225: new 1241	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55
    //   2228: dup
    //   2229: invokespecial 1242	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:<init>	()V
    //   2232: astore 29
    //   2234: aload 7
    //   2236: astore 24
    //   2238: aload 6
    //   2240: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   2243: aload 29
    //   2245: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2248: pop
    //   2249: aload 7
    //   2251: astore 24
    //   2253: aload 29
    //   2255: aload 25
    //   2257: getfield 1243	org/vidogram/tgnet/TLRPC$DocumentAttribute:alt	Ljava/lang/String;
    //   2260: putfield 1244	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:alt	Ljava/lang/String;
    //   2263: aload 7
    //   2265: astore 24
    //   2267: aload 25
    //   2269: getfield 1245	org/vidogram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2272: ifnull +206 -> 2478
    //   2275: aload 7
    //   2277: astore 24
    //   2279: aload 25
    //   2281: getfield 1245	org/vidogram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2284: instanceof 1247
    //   2287: ifeq +152 -> 2439
    //   2290: aload 7
    //   2292: astore 24
    //   2294: aload 25
    //   2296: getfield 1245	org/vidogram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2299: getfield 1252	org/vidogram/tgnet/TLRPC$InputStickerSet:short_name	Ljava/lang/String;
    //   2302: astore 25
    //   2304: aload 7
    //   2306: astore 24
    //   2308: aload 25
    //   2310: invokestatic 1257	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   2313: ifne +146 -> 2459
    //   2316: aload 7
    //   2318: astore 24
    //   2320: aload 29
    //   2322: new 1247	org/vidogram/tgnet/TLRPC$TL_inputStickerSetShortName
    //   2325: dup
    //   2326: invokespecial 1258	org/vidogram/tgnet/TLRPC$TL_inputStickerSetShortName:<init>	()V
    //   2329: putfield 1259	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2332: aload 7
    //   2334: astore 24
    //   2336: aload 29
    //   2338: getfield 1259	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2341: aload 25
    //   2343: putfield 1252	org/vidogram/tgnet/TLRPC$InputStickerSet:short_name	Ljava/lang/String;
    //   2346: goto -1203 -> 1143
    //   2349: new 1198	org/vidogram/tgnet/TLRPC$TL_message
    //   2352: dup
    //   2353: invokespecial 1199	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
    //   2356: astore 7
    //   2358: goto -382 -> 1976
    //   2361: ldc_w 743
    //   2364: astore 25
    //   2366: goto -340 -> 2026
    //   2369: aload 7
    //   2371: astore 24
    //   2373: aload 6
    //   2375: invokestatic 1262	org/vidogram/messenger/MessageObject:isVideoDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   2378: ifne +5413 -> 7791
    //   2381: aload 4
    //   2383: ifnull +6 -> 2389
    //   2386: goto +5405 -> 7791
    //   2389: aload 7
    //   2391: astore 24
    //   2393: aload 6
    //   2395: invokestatic 1265	org/vidogram/messenger/MessageObject:isVoiceDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   2398: ifeq +5399 -> 7797
    //   2401: bipush 8
    //   2403: istore 18
    //   2405: goto -330 -> 2075
    //   2408: aload 7
    //   2410: astore 24
    //   2412: aload 7
    //   2414: aload 4
    //   2416: invokevirtual 1268	org/vidogram/messenger/VideoEditedInfo:getString	()Ljava/lang/String;
    //   2419: putfield 1148	org/vidogram/tgnet/TLRPC$Message:message	Ljava/lang/String;
    //   2422: goto -330 -> 2092
    //   2425: aload 7
    //   2427: astore 24
    //   2429: aload 7
    //   2431: aload 10
    //   2433: putfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2436: goto -298 -> 2138
    //   2439: aload 7
    //   2441: astore 24
    //   2443: aload 25
    //   2445: getfield 1245	org/vidogram/tgnet/TLRPC$DocumentAttribute:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2448: getfield 1269	org/vidogram/tgnet/TLRPC$InputStickerSet:id	J
    //   2451: invokestatic 1275	org/vidogram/messenger/query/StickersQuery:getStickerSetName	(J)Ljava/lang/String;
    //   2454: astore 25
    //   2456: goto -152 -> 2304
    //   2459: aload 7
    //   2461: astore 24
    //   2463: aload 29
    //   2465: new 909	org/vidogram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2468: dup
    //   2469: invokespecial 910	org/vidogram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2472: putfield 1259	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2475: goto -129 -> 2346
    //   2478: aload 7
    //   2480: astore 24
    //   2482: aload 29
    //   2484: new 909	org/vidogram/tgnet/TLRPC$TL_inputStickerSetEmpty
    //   2487: dup
    //   2488: invokespecial 910	org/vidogram/tgnet/TLRPC$TL_inputStickerSetEmpty:<init>	()V
    //   2491: putfield 1259	org/vidogram/tgnet/TLRPC$TL_documentAttributeSticker_layer55:stickerset	Lorg/vidogram/tgnet/TLRPC$InputStickerSet;
    //   2494: goto -1351 -> 1143
    //   2497: aload 7
    //   2499: astore 24
    //   2501: aload 7
    //   2503: invokestatic 1278	org/vidogram/messenger/UserConfig:getClientUserId	()I
    //   2506: putfield 1192	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2509: aload 7
    //   2511: astore 24
    //   2513: aload 7
    //   2515: aload 7
    //   2517: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   2520: sipush 256
    //   2523: ior
    //   2524: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   2527: goto -1294 -> 1233
    //   2530: aload 7
    //   2532: aload 17
    //   2534: ldc_w 1107
    //   2537: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   2540: checkcast 340	java/lang/String
    //   2543: invokestatic 286	org/vidogram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   2546: invokevirtual 290	java/lang/Integer:intValue	()I
    //   2549: putfield 1281	org/vidogram/tgnet/TLRPC$Message:via_bot_id	I
    //   2552: goto -2247 -> 305
    //   2555: aload 7
    //   2557: iconst_1
    //   2558: putfield 1284	org/vidogram/tgnet/TLRPC$Message:post	Z
    //   2561: aload_3
    //   2562: getfield 1287	org/vidogram/tgnet/TLRPC$Chat:signatures	Z
    //   2565: ifeq -2136 -> 429
    //   2568: aload 7
    //   2570: invokestatic 1278	org/vidogram/messenger/UserConfig:getClientUserId	()I
    //   2573: putfield 1192	org/vidogram/tgnet/TLRPC$Message:from_id	I
    //   2576: goto -2147 -> 429
    //   2579: aload 7
    //   2581: iconst_1
    //   2582: putfield 1127	org/vidogram/tgnet/TLRPC$Message:unread	Z
    //   2585: goto -2156 -> 429
    //   2588: aload 7
    //   2590: aload 7
    //   2592: getfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   2595: bipush 8
    //   2597: ior
    //   2598: putfield 1113	org/vidogram/tgnet/TLRPC$Message:flags	I
    //   2601: goto -2102 -> 499
    //   2604: new 603	java/util/ArrayList
    //   2607: dup
    //   2608: invokespecial 706	java/util/ArrayList:<init>	()V
    //   2611: astore_3
    //   2612: aload_0
    //   2613: getfield 138	org/vidogram/messenger/SendMessagesHelper:currentChatInfo	Lorg/vidogram/tgnet/TLRPC$ChatFull;
    //   2616: getfield 1293	org/vidogram/tgnet/TLRPC$ChatFull:participants	Lorg/vidogram/tgnet/TLRPC$ChatParticipants;
    //   2619: getfield 1297	org/vidogram/tgnet/TLRPC$ChatParticipants:participants	Ljava/util/ArrayList;
    //   2622: invokevirtual 1298	java/util/ArrayList:iterator	()Ljava/util/Iterator;
    //   2625: astore 16
    //   2627: aload 16
    //   2629: invokeinterface 1040 1 0
    //   2634: ifeq +49 -> 2683
    //   2637: aload 16
    //   2639: invokeinterface 1044 1 0
    //   2644: checkcast 1300	org/vidogram/tgnet/TLRPC$ChatParticipant
    //   2647: astore 25
    //   2649: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2652: aload 25
    //   2654: getfield 1301	org/vidogram/tgnet/TLRPC$ChatParticipant:user_id	I
    //   2657: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   2660: invokevirtual 1305	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   2663: invokestatic 1309	org/vidogram/messenger/MessagesController:getInputUser	(Lorg/vidogram/tgnet/TLRPC$User;)Lorg/vidogram/tgnet/TLRPC$InputUser;
    //   2666: astore 25
    //   2668: aload 25
    //   2670: ifnull -43 -> 2627
    //   2673: aload_3
    //   2674: aload 25
    //   2676: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2679: pop
    //   2680: goto -53 -> 2627
    //   2683: aload 7
    //   2685: new 1311	org/vidogram/tgnet/TLRPC$TL_peerChat
    //   2688: dup
    //   2689: invokespecial 1312	org/vidogram/tgnet/TLRPC$TL_peerChat:<init>	()V
    //   2692: putfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2695: aload 7
    //   2697: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2700: iload 22
    //   2702: putfield 1321	org/vidogram/tgnet/TLRPC$Peer:chat_id	I
    //   2705: iload 21
    //   2707: iconst_1
    //   2708: if_icmpeq +28 -> 2736
    //   2711: aload 7
    //   2713: invokestatic 1325	org/vidogram/messenger/MessageObject:isVoiceMessage	(Lorg/vidogram/tgnet/TLRPC$Message;)Z
    //   2716: ifeq +20 -> 2736
    //   2719: aload 7
    //   2721: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   2724: getfield 1326	org/vidogram/tgnet/TLRPC$Peer:channel_id	I
    //   2727: ifne +9 -> 2736
    //   2730: aload 7
    //   2732: iconst_1
    //   2733: putfield 1329	org/vidogram/tgnet/TLRPC$Message:media_unread	Z
    //   2736: aload 7
    //   2738: iconst_1
    //   2739: putfield 1066	org/vidogram/tgnet/TLRPC$Message:send_state	I
    //   2742: new 445	org/vidogram/messenger/MessageObject
    //   2745: dup
    //   2746: aload 7
    //   2748: aconst_null
    //   2749: iconst_1
    //   2750: invokespecial 1332	org/vidogram/messenger/MessageObject:<init>	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/AbstractMap;Z)V
    //   2753: astore 16
    //   2755: aload 16
    //   2757: aload 11
    //   2759: putfield 1335	org/vidogram/messenger/MessageObject:replyMessageObject	Lorg/vidogram/messenger/MessageObject;
    //   2762: aload 16
    //   2764: invokevirtual 1099	org/vidogram/messenger/MessageObject:isForwarded	()Z
    //   2767: ifne +34 -> 2801
    //   2770: aload 16
    //   2772: getfield 1140	org/vidogram/messenger/MessageObject:type	I
    //   2775: iconst_3
    //   2776: if_icmpeq +8 -> 2784
    //   2779: aload 4
    //   2781: ifnull +20 -> 2801
    //   2784: aload 7
    //   2786: getfield 454	org/vidogram/tgnet/TLRPC$Message:attachPath	Ljava/lang/String;
    //   2789: invokestatic 1257	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   2792: ifne +9 -> 2801
    //   2795: aload 16
    //   2797: iconst_1
    //   2798: putfield 1338	org/vidogram/messenger/MessageObject:attachPathExists	Z
    //   2801: new 603	java/util/ArrayList
    //   2804: dup
    //   2805: invokespecial 706	java/util/ArrayList:<init>	()V
    //   2808: astore 25
    //   2810: aload 25
    //   2812: aload 16
    //   2814: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2817: pop
    //   2818: new 603	java/util/ArrayList
    //   2821: dup
    //   2822: invokespecial 706	java/util/ArrayList:<init>	()V
    //   2825: astore 29
    //   2827: aload 29
    //   2829: aload 7
    //   2831: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   2834: pop
    //   2835: invokestatic 804	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   2838: aload 29
    //   2840: iconst_0
    //   2841: iconst_1
    //   2842: iconst_0
    //   2843: iconst_0
    //   2844: invokevirtual 1342	org/vidogram/messenger/MessagesStorage:putMessages	(Ljava/util/ArrayList;ZZZI)V
    //   2847: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   2850: lload 8
    //   2852: aload 25
    //   2854: invokevirtual 1346	org/vidogram/messenger/MessagesController:updateInterfaceWithMessages	(JLjava/util/ArrayList;)V
    //   2857: invokestatic 165	org/vidogram/messenger/NotificationCenter:getInstance	()Lorg/vidogram/messenger/NotificationCenter;
    //   2860: getstatic 1349	org/vidogram/messenger/NotificationCenter:dialogsNeedReload	I
    //   2863: iconst_0
    //   2864: anewarray 4	java/lang/Object
    //   2867: invokevirtual 1076	org/vidogram/messenger/NotificationCenter:postNotificationName	(I[Ljava/lang/Object;)V
    //   2870: getstatic 1354	org/vidogram/messenger/BuildVars:DEBUG_VERSION	Z
    //   2873: ifeq +4940 -> 7813
    //   2876: aload 28
    //   2878: ifnull +4935 -> 7813
    //   2881: new 456	java/lang/StringBuilder
    //   2884: dup
    //   2885: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   2888: ldc_w 1356
    //   2891: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2894: aload 28
    //   2896: getfield 1357	org/vidogram/tgnet/TLRPC$InputPeer:user_id	I
    //   2899: invokevirtual 528	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2902: ldc_w 1359
    //   2905: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2908: aload 28
    //   2910: getfield 1360	org/vidogram/tgnet/TLRPC$InputPeer:chat_id	I
    //   2913: invokevirtual 528	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2916: ldc_w 1362
    //   2919: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2922: aload 28
    //   2924: getfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   2927: invokevirtual 528	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   2930: ldc_w 1364
    //   2933: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   2936: aload 28
    //   2938: getfield 1367	org/vidogram/tgnet/TLRPC$InputPeer:access_hash	J
    //   2941: invokevirtual 483	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   2944: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   2947: invokestatic 1369	org/vidogram/messenger/FileLog:e	(Ljava/lang/String;)V
    //   2950: goto +4863 -> 7813
    //   2953: aload 26
    //   2955: ifnonnull +591 -> 3546
    //   2958: aload_3
    //   2959: ifnull +409 -> 3368
    //   2962: new 516	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   2965: dup
    //   2966: invokespecial 1370	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   2969: astore_2
    //   2970: new 603	java/util/ArrayList
    //   2973: dup
    //   2974: invokespecial 706	java/util/ArrayList:<init>	()V
    //   2977: astore 4
    //   2979: iconst_0
    //   2980: istore 18
    //   2982: iload 18
    //   2984: aload_3
    //   2985: invokevirtual 605	java/util/ArrayList:size	()I
    //   2988: if_icmpge +344 -> 3332
    //   2991: aload 4
    //   2993: getstatic 1374	org/vidogram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   2996: invokevirtual 1379	java/security/SecureRandom:nextLong	()J
    //   2999: invokestatic 1382	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3002: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3005: pop
    //   3006: iload 18
    //   3008: iconst_1
    //   3009: iadd
    //   3010: istore 18
    //   3012: goto -30 -> 2982
    //   3015: aload 7
    //   3017: iload 22
    //   3019: invokestatic 1386	org/vidogram/messenger/MessagesController:getPeer	(I)Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3022: putfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3025: iload 22
    //   3027: ifle +291 -> 3318
    //   3030: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   3033: iload 22
    //   3035: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   3038: invokevirtual 1305	org/vidogram/messenger/MessagesController:getUser	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$User;
    //   3041: astore_3
    //   3042: aload_3
    //   3043: ifnonnull +13 -> 3056
    //   3046: aload_0
    //   3047: aload 7
    //   3049: getfield 1139	org/vidogram/tgnet/TLRPC$Message:id	I
    //   3052: invokevirtual 1080	org/vidogram/messenger/SendMessagesHelper:processSentMessage	(I)V
    //   3055: return
    //   3056: aload_3
    //   3057: getfield 1388	org/vidogram/tgnet/TLRPC$User:bot	Z
    //   3060: ifeq +4777 -> 7837
    //   3063: aload 7
    //   3065: iconst_0
    //   3066: putfield 1127	org/vidogram/tgnet/TLRPC$Message:unread	Z
    //   3069: goto +4768 -> 7837
    //   3072: aload 7
    //   3074: new 1390	org/vidogram/tgnet/TLRPC$TL_peerUser
    //   3077: dup
    //   3078: invokespecial 1391	org/vidogram/tgnet/TLRPC$TL_peerUser:<init>	()V
    //   3081: putfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3084: aload 26
    //   3086: getfield 1396	org/vidogram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3089: invokestatic 1278	org/vidogram/messenger/UserConfig:getClientUserId	()I
    //   3092: if_icmpne +130 -> 3222
    //   3095: aload 7
    //   3097: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3100: aload 26
    //   3102: getfield 1399	org/vidogram/tgnet/TLRPC$EncryptedChat:admin_id	I
    //   3105: putfield 1400	org/vidogram/tgnet/TLRPC$Peer:user_id	I
    //   3108: aload 7
    //   3110: aload 26
    //   3112: getfield 1403	org/vidogram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3115: putfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   3118: aload 7
    //   3120: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   3123: ifeq +195 -> 3318
    //   3126: aload 7
    //   3128: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   3131: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   3134: ifnull +184 -> 3318
    //   3137: aload 7
    //   3139: invokestatic 1325	org/vidogram/messenger/MessageObject:isVoiceMessage	(Lorg/vidogram/tgnet/TLRPC$Message;)Z
    //   3142: ifeq +96 -> 3238
    //   3145: iconst_0
    //   3146: istore 19
    //   3148: iload 19
    //   3150: aload 7
    //   3152: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   3155: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   3158: getfield 1407	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3161: invokevirtual 605	java/util/ArrayList:size	()I
    //   3164: if_icmpge +4546 -> 7710
    //   3167: aload 7
    //   3169: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   3172: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   3175: getfield 1407	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3178: iload 19
    //   3180: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3183: checkcast 1236	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   3186: astore_3
    //   3187: aload_3
    //   3188: instanceof 780
    //   3191: ifeq +4651 -> 7842
    //   3194: aload_3
    //   3195: getfield 1408	org/vidogram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3198: istore 19
    //   3200: aload 7
    //   3202: aload 26
    //   3204: getfield 1403	org/vidogram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3207: iload 19
    //   3209: iconst_1
    //   3210: iadd
    //   3211: invokestatic 1412	java/lang/Math:max	(II)I
    //   3214: putfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   3217: aconst_null
    //   3218: astore_3
    //   3219: goto -514 -> 2705
    //   3222: aload 7
    //   3224: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3227: aload 26
    //   3229: getfield 1396	org/vidogram/tgnet/TLRPC$EncryptedChat:participant_id	I
    //   3232: putfield 1400	org/vidogram/tgnet/TLRPC$Peer:user_id	I
    //   3235: goto -127 -> 3108
    //   3238: aload 7
    //   3240: invokestatic 1415	org/vidogram/messenger/MessageObject:isVideoMessage	(Lorg/vidogram/tgnet/TLRPC$Message;)Z
    //   3243: ifeq +75 -> 3318
    //   3246: iconst_0
    //   3247: istore 19
    //   3249: iload 19
    //   3251: aload 7
    //   3253: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   3256: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   3259: getfield 1407	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3262: invokevirtual 605	java/util/ArrayList:size	()I
    //   3265: if_icmpge +4439 -> 7704
    //   3268: aload 7
    //   3270: getfield 1143	org/vidogram/tgnet/TLRPC$Message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
    //   3273: getfield 1160	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
    //   3276: getfield 1407	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
    //   3279: iload 19
    //   3281: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   3284: checkcast 1236	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   3287: astore_3
    //   3288: aload_3
    //   3289: instanceof 254
    //   3292: ifeq +31 -> 3323
    //   3295: aload_3
    //   3296: getfield 1408	org/vidogram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   3299: istore 19
    //   3301: aload 7
    //   3303: aload 26
    //   3305: getfield 1403	org/vidogram/tgnet/TLRPC$EncryptedChat:ttl	I
    //   3308: iload 19
    //   3310: iconst_1
    //   3311: iadd
    //   3312: invokestatic 1412	java/lang/Math:max	(II)I
    //   3315: putfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   3318: aconst_null
    //   3319: astore_3
    //   3320: goto -615 -> 2705
    //   3323: iload 19
    //   3325: iconst_1
    //   3326: iadd
    //   3327: istore 19
    //   3329: goto -80 -> 3249
    //   3332: aload_2
    //   3333: aload_1
    //   3334: putfield 1416	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   3337: aload_2
    //   3338: aload_3
    //   3339: putfield 1419	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   3342: aload_2
    //   3343: new 1421	org/vidogram/tgnet/TLRPC$TL_inputMediaEmpty
    //   3346: dup
    //   3347: invokespecial 1422	org/vidogram/tgnet/TLRPC$TL_inputMediaEmpty:<init>	()V
    //   3350: putfield 517	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/vidogram/tgnet/TLRPC$InputMedia;
    //   3353: aload_2
    //   3354: aload 4
    //   3356: putfield 1424	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   3359: aload_0
    //   3360: aload_2
    //   3361: aload 16
    //   3363: aconst_null
    //   3364: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   3367: return
    //   3368: new 560	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage
    //   3371: dup
    //   3372: invokespecial 1427	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:<init>	()V
    //   3375: astore_2
    //   3376: aload_2
    //   3377: aload_1
    //   3378: putfield 1428	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:message	Ljava/lang/String;
    //   3381: aload 14
    //   3383: ifnonnull +4478 -> 7861
    //   3386: iconst_1
    //   3387: istore 23
    //   3389: aload_2
    //   3390: iload 23
    //   3392: putfield 1431	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:clear_draft	Z
    //   3395: aload 7
    //   3397: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   3400: instanceof 1433
    //   3403: ifeq +44 -> 3447
    //   3406: aload_2
    //   3407: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   3410: ldc_w 1435
    //   3413: iconst_0
    //   3414: invokevirtual 1441	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   3417: new 456	java/lang/StringBuilder
    //   3420: dup
    //   3421: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   3424: ldc_w 1443
    //   3427: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   3430: lload 8
    //   3432: invokevirtual 483	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   3435: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   3438: iconst_0
    //   3439: invokeinterface 1449 3 0
    //   3444: putfield 1452	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:silent	Z
    //   3447: aload_2
    //   3448: aload 28
    //   3450: putfield 1456	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   3453: aload_2
    //   3454: aload 7
    //   3456: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   3459: putfield 1457	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:random_id	J
    //   3462: aload 11
    //   3464: ifnull +22 -> 3486
    //   3467: aload_2
    //   3468: aload_2
    //   3469: getfield 1458	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   3472: iconst_1
    //   3473: ior
    //   3474: putfield 1458	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   3477: aload_2
    //   3478: aload 11
    //   3480: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   3483: putfield 1459	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:reply_to_msg_id	I
    //   3486: iload 13
    //   3488: ifne +8 -> 3496
    //   3491: aload_2
    //   3492: iconst_1
    //   3493: putfield 1460	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:no_webpage	Z
    //   3496: aload 15
    //   3498: ifnull +28 -> 3526
    //   3501: aload 15
    //   3503: invokevirtual 962	java/util/ArrayList:isEmpty	()Z
    //   3506: ifne +20 -> 3526
    //   3509: aload_2
    //   3510: aload 15
    //   3512: putfield 1461	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:entities	Ljava/util/ArrayList;
    //   3515: aload_2
    //   3516: aload_2
    //   3517: getfield 1458	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   3520: bipush 8
    //   3522: ior
    //   3523: putfield 1458	org/vidogram/tgnet/TLRPC$TL_messages_sendMessage:flags	I
    //   3526: aload_0
    //   3527: aload_2
    //   3528: aload 16
    //   3530: aconst_null
    //   3531: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   3534: aload 14
    //   3536: ifnonnull -3529 -> 7
    //   3539: lload 8
    //   3541: iconst_0
    //   3542: invokestatic 1467	org/vidogram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   3545: return
    //   3546: new 1469	org/vidogram/tgnet/TLRPC$TL_decryptedMessage
    //   3549: dup
    //   3550: invokespecial 1470	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   3553: astore_2
    //   3554: aload_2
    //   3555: aload 7
    //   3557: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   3560: putfield 1471	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   3563: aload 15
    //   3565: ifnull +29 -> 3594
    //   3568: aload 15
    //   3570: invokevirtual 962	java/util/ArrayList:isEmpty	()Z
    //   3573: ifne +21 -> 3594
    //   3576: aload_2
    //   3577: aload 15
    //   3579: putfield 1472	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   3582: aload_2
    //   3583: aload_2
    //   3584: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3587: sipush 128
    //   3590: ior
    //   3591: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3594: aload 11
    //   3596: ifnull +39 -> 3635
    //   3599: aload 11
    //   3601: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   3604: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   3607: lconst_0
    //   3608: lcmp
    //   3609: ifeq +26 -> 3635
    //   3612: aload_2
    //   3613: aload 11
    //   3615: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   3618: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   3621: putfield 1474	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   3624: aload_2
    //   3625: aload_2
    //   3626: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3629: bipush 8
    //   3631: ior
    //   3632: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3635: aload 17
    //   3637: ifnull +41 -> 3678
    //   3640: aload 17
    //   3642: ldc_w 1109
    //   3645: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3648: ifnull +30 -> 3678
    //   3651: aload_2
    //   3652: aload 17
    //   3654: ldc_w 1109
    //   3657: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   3660: checkcast 340	java/lang/String
    //   3663: putfield 1475	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   3666: aload_2
    //   3667: aload_2
    //   3668: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3671: sipush 2048
    //   3674: ior
    //   3675: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3678: aload_2
    //   3679: aload 7
    //   3681: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   3684: putfield 1476	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   3687: aload_2
    //   3688: aload_1
    //   3689: putfield 1477	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   3692: aload 12
    //   3694: ifnull +76 -> 3770
    //   3697: aload 12
    //   3699: getfield 629	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   3702: ifnull +68 -> 3770
    //   3705: aload_2
    //   3706: new 1479	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage
    //   3709: dup
    //   3710: invokespecial 1480	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaWebPage:<init>	()V
    //   3713: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   3716: aload_2
    //   3717: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   3720: aload 12
    //   3722: getfield 629	org/vidogram/tgnet/TLRPC$WebPage:url	Ljava/lang/String;
    //   3725: putfield 1486	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:url	Ljava/lang/String;
    //   3728: aload_2
    //   3729: aload_2
    //   3730: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3733: sipush 512
    //   3736: ior
    //   3737: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   3740: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   3743: aload_2
    //   3744: aload 16
    //   3746: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   3749: aload 26
    //   3751: aconst_null
    //   3752: aconst_null
    //   3753: aload 16
    //   3755: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   3758: aload 14
    //   3760: ifnonnull -3753 -> 7
    //   3763: lload 8
    //   3765: iconst_0
    //   3766: invokestatic 1467	org/vidogram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   3769: return
    //   3770: aload_2
    //   3771: new 1497	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty
    //   3774: dup
    //   3775: invokespecial 1498	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaEmpty:<init>	()V
    //   3778: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   3781: goto -41 -> 3740
    //   3784: aload 26
    //   3786: ifnonnull +1519 -> 5305
    //   3789: aconst_null
    //   3790: astore 12
    //   3792: iload 18
    //   3794: iconst_1
    //   3795: if_icmpne +4112 -> 7907
    //   3798: aload_2
    //   3799: instanceof 647
    //   3802: ifeq +143 -> 3945
    //   3805: new 1500	org/vidogram/tgnet/TLRPC$TL_inputMediaVenue
    //   3808: dup
    //   3809: invokespecial 1501	org/vidogram/tgnet/TLRPC$TL_inputMediaVenue:<init>	()V
    //   3812: astore_1
    //   3813: aload_1
    //   3814: aload_2
    //   3815: getfield 1502	org/vidogram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   3818: putfield 1503	org/vidogram/tgnet/TLRPC$InputMedia:address	Ljava/lang/String;
    //   3821: aload_1
    //   3822: aload_2
    //   3823: getfield 1504	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   3826: putfield 1505	org/vidogram/tgnet/TLRPC$InputMedia:title	Ljava/lang/String;
    //   3829: aload_1
    //   3830: aload_2
    //   3831: getfield 1506	org/vidogram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   3834: putfield 1507	org/vidogram/tgnet/TLRPC$InputMedia:provider	Ljava/lang/String;
    //   3837: aload_1
    //   3838: aload_2
    //   3839: getfield 1508	org/vidogram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   3842: putfield 1509	org/vidogram/tgnet/TLRPC$InputMedia:venue_id	Ljava/lang/String;
    //   3845: aload_1
    //   3846: new 1511	org/vidogram/tgnet/TLRPC$TL_inputGeoPoint
    //   3849: dup
    //   3850: invokespecial 1512	org/vidogram/tgnet/TLRPC$TL_inputGeoPoint:<init>	()V
    //   3853: putfield 1516	org/vidogram/tgnet/TLRPC$InputMedia:geo_point	Lorg/vidogram/tgnet/TLRPC$InputGeoPoint;
    //   3856: aload_1
    //   3857: getfield 1516	org/vidogram/tgnet/TLRPC$InputMedia:geo_point	Lorg/vidogram/tgnet/TLRPC$InputGeoPoint;
    //   3860: aload_2
    //   3861: getfield 1517	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   3864: getfield 1019	org/vidogram/tgnet/TLRPC$GeoPoint:lat	D
    //   3867: putfield 1520	org/vidogram/tgnet/TLRPC$InputGeoPoint:lat	D
    //   3870: aload_1
    //   3871: getfield 1516	org/vidogram/tgnet/TLRPC$InputMedia:geo_point	Lorg/vidogram/tgnet/TLRPC$InputGeoPoint;
    //   3874: aload_2
    //   3875: getfield 1517	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   3878: getfield 1025	org/vidogram/tgnet/TLRPC$GeoPoint:_long	D
    //   3881: putfield 1521	org/vidogram/tgnet/TLRPC$InputGeoPoint:_long	D
    //   3884: aload 12
    //   3886: astore_2
    //   3887: aload_3
    //   3888: ifnull +1150 -> 5038
    //   3891: new 516	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast
    //   3894: dup
    //   3895: invokespecial 1370	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:<init>	()V
    //   3898: astore 4
    //   3900: new 603	java/util/ArrayList
    //   3903: dup
    //   3904: invokespecial 706	java/util/ArrayList:<init>	()V
    //   3907: astore 5
    //   3909: iconst_0
    //   3910: istore 19
    //   3912: iload 19
    //   3914: aload_3
    //   3915: invokevirtual 605	java/util/ArrayList:size	()I
    //   3918: if_icmpge +1051 -> 4969
    //   3921: aload 5
    //   3923: getstatic 1374	org/vidogram/messenger/Utilities:random	Ljava/security/SecureRandom;
    //   3926: invokevirtual 1379	java/security/SecureRandom:nextLong	()J
    //   3929: invokestatic 1382	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   3932: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   3935: pop
    //   3936: iload 19
    //   3938: iconst_1
    //   3939: iadd
    //   3940: istore 19
    //   3942: goto -30 -> 3912
    //   3945: new 1523	org/vidogram/tgnet/TLRPC$TL_inputMediaGeoPoint
    //   3948: dup
    //   3949: invokespecial 1524	org/vidogram/tgnet/TLRPC$TL_inputMediaGeoPoint:<init>	()V
    //   3952: astore_1
    //   3953: goto -108 -> 3845
    //   3956: aload 24
    //   3958: getfield 1525	org/vidogram/tgnet/TLRPC$TL_photo:access_hash	J
    //   3961: lconst_0
    //   3962: lcmp
    //   3963: ifne +207 -> 4170
    //   3966: new 1527	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedPhoto
    //   3969: dup
    //   3970: invokespecial 1528	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedPhoto:<init>	()V
    //   3973: astore_1
    //   3974: aload 24
    //   3976: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   3979: ifnull +3949 -> 7928
    //   3982: aload 24
    //   3984: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   3987: astore_2
    //   3988: aload_1
    //   3989: aload_2
    //   3990: putfield 1529	org/vidogram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   3993: aload 17
    //   3995: ifnull +85 -> 4080
    //   3998: aload 17
    //   4000: ldc_w 1531
    //   4003: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4006: checkcast 340	java/lang/String
    //   4009: astore_2
    //   4010: aload_2
    //   4011: ifnull +69 -> 4080
    //   4014: new 1533	org/vidogram/tgnet/SerializedData
    //   4017: dup
    //   4018: aload_2
    //   4019: invokestatic 1537	org/vidogram/messenger/Utilities:hexToBytes	(Ljava/lang/String;)[B
    //   4022: invokespecial 1540	org/vidogram/tgnet/SerializedData:<init>	([B)V
    //   4025: astore_2
    //   4026: aload_2
    //   4027: iconst_0
    //   4028: invokevirtual 1544	org/vidogram/tgnet/SerializedData:readInt32	(Z)I
    //   4031: istore 20
    //   4033: iconst_0
    //   4034: istore 19
    //   4036: iload 19
    //   4038: iload 20
    //   4040: if_icmpge +30 -> 4070
    //   4043: aload_1
    //   4044: getfield 1547	org/vidogram/tgnet/TLRPC$InputMedia:stickers	Ljava/util/ArrayList;
    //   4047: aload_2
    //   4048: aload_2
    //   4049: iconst_0
    //   4050: invokevirtual 1544	org/vidogram/tgnet/SerializedData:readInt32	(Z)I
    //   4053: iconst_0
    //   4054: invokestatic 1553	org/vidogram/tgnet/TLRPC$InputDocument:TLdeserialize	(Lorg/vidogram/tgnet/AbstractSerializedData;IZ)Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4057: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   4060: pop
    //   4061: iload 19
    //   4063: iconst_1
    //   4064: iadd
    //   4065: istore 19
    //   4067: goto -31 -> 4036
    //   4070: aload_1
    //   4071: aload_1
    //   4072: getfield 1554	org/vidogram/tgnet/TLRPC$InputMedia:flags	I
    //   4075: iconst_1
    //   4076: ior
    //   4077: putfield 1554	org/vidogram/tgnet/TLRPC$InputMedia:flags	I
    //   4080: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   4083: dup
    //   4084: aload_0
    //   4085: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   4088: astore_2
    //   4089: aload_2
    //   4090: aload 27
    //   4092: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4095: aload_2
    //   4096: iconst_0
    //   4097: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4100: aload_2
    //   4101: aload 16
    //   4103: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   4106: aload 10
    //   4108: ifnull +31 -> 4139
    //   4111: aload 10
    //   4113: invokevirtual 347	java/lang/String:length	()I
    //   4116: ifle +23 -> 4139
    //   4119: aload 10
    //   4121: ldc_w 1215
    //   4124: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   4127: ifeq +12 -> 4139
    //   4130: aload_2
    //   4131: aload 10
    //   4133: putfield 369	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   4136: goto -249 -> 3887
    //   4139: aload_2
    //   4140: aload 24
    //   4142: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4145: aload 24
    //   4147: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   4150: invokevirtual 605	java/util/ArrayList:size	()I
    //   4153: iconst_1
    //   4154: isub
    //   4155: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   4158: checkcast 926	org/vidogram/tgnet/TLRPC$PhotoSize
    //   4161: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4164: putfield 392	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4167: goto -280 -> 3887
    //   4170: new 1559	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto
    //   4173: dup
    //   4174: invokespecial 1560	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto:<init>	()V
    //   4177: astore_2
    //   4178: aload_2
    //   4179: new 1562	org/vidogram/tgnet/TLRPC$TL_inputPhoto
    //   4182: dup
    //   4183: invokespecial 1563	org/vidogram/tgnet/TLRPC$TL_inputPhoto:<init>	()V
    //   4186: putfield 1566	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/vidogram/tgnet/TLRPC$InputPhoto;
    //   4189: aload 24
    //   4191: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4194: ifnull +3741 -> 7935
    //   4197: aload 24
    //   4199: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   4202: astore_1
    //   4203: aload_2
    //   4204: aload_1
    //   4205: putfield 1567	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto:caption	Ljava/lang/String;
    //   4208: aload_2
    //   4209: getfield 1566	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/vidogram/tgnet/TLRPC$InputPhoto;
    //   4212: aload 24
    //   4214: getfield 1568	org/vidogram/tgnet/TLRPC$TL_photo:id	J
    //   4217: putfield 1571	org/vidogram/tgnet/TLRPC$InputPhoto:id	J
    //   4220: aload_2
    //   4221: getfield 1566	org/vidogram/tgnet/TLRPC$TL_inputMediaPhoto:id	Lorg/vidogram/tgnet/TLRPC$InputPhoto;
    //   4224: aload 24
    //   4226: getfield 1525	org/vidogram/tgnet/TLRPC$TL_photo:access_hash	J
    //   4229: putfield 1572	org/vidogram/tgnet/TLRPC$InputPhoto:access_hash	J
    //   4232: aload_2
    //   4233: astore_1
    //   4234: aload 12
    //   4236: astore_2
    //   4237: goto -350 -> 3887
    //   4240: iload 18
    //   4242: iconst_3
    //   4243: if_icmpne +203 -> 4446
    //   4246: aload 6
    //   4248: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4251: lconst_0
    //   4252: lcmp
    //   4253: ifne +123 -> 4376
    //   4256: aload 6
    //   4258: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   4261: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4264: ifnull +101 -> 4365
    //   4267: new 1575	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   4270: dup
    //   4271: invokespecial 1576	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   4274: astore_1
    //   4275: aload 6
    //   4277: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4280: ifnull +3662 -> 7942
    //   4283: aload 6
    //   4285: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4288: astore_2
    //   4289: aload_1
    //   4290: aload_2
    //   4291: putfield 1529	org/vidogram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4294: aload_1
    //   4295: aload 6
    //   4297: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   4300: putfield 1577	org/vidogram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   4303: aload_1
    //   4304: aload 6
    //   4306: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   4309: putfield 1578	org/vidogram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   4312: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   4315: dup
    //   4316: aload_0
    //   4317: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   4320: astore_2
    //   4321: aload_2
    //   4322: aload 27
    //   4324: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4327: aload_2
    //   4328: iconst_1
    //   4329: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4332: aload_2
    //   4333: aload 16
    //   4335: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   4338: aload_2
    //   4339: aload 6
    //   4341: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   4344: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4347: putfield 392	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4350: aload_2
    //   4351: aload 6
    //   4353: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   4356: aload_2
    //   4357: aload 4
    //   4359: putfield 439	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   4362: goto -475 -> 3887
    //   4365: new 1580	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   4368: dup
    //   4369: invokespecial 1581	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   4372: astore_1
    //   4373: goto -98 -> 4275
    //   4376: new 1583	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument
    //   4379: dup
    //   4380: invokespecial 1584	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   4383: astore_2
    //   4384: aload_2
    //   4385: new 1586	org/vidogram/tgnet/TLRPC$TL_inputDocument
    //   4388: dup
    //   4389: invokespecial 1587	org/vidogram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   4392: putfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4395: aload 6
    //   4397: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4400: ifnull +3549 -> 7949
    //   4403: aload 6
    //   4405: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4408: astore_1
    //   4409: aload_2
    //   4410: aload_1
    //   4411: putfield 1591	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   4414: aload_2
    //   4415: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4418: aload 6
    //   4420: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   4423: putfield 1592	org/vidogram/tgnet/TLRPC$InputDocument:id	J
    //   4426: aload_2
    //   4427: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4430: aload 6
    //   4432: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4435: putfield 1593	org/vidogram/tgnet/TLRPC$InputDocument:access_hash	J
    //   4438: aload_2
    //   4439: astore_1
    //   4440: aload 12
    //   4442: astore_2
    //   4443: goto -556 -> 3887
    //   4446: iload 18
    //   4448: bipush 6
    //   4450: if_icmpne +3506 -> 7956
    //   4453: new 1595	org/vidogram/tgnet/TLRPC$TL_inputMediaContact
    //   4456: dup
    //   4457: invokespecial 1596	org/vidogram/tgnet/TLRPC$TL_inputMediaContact:<init>	()V
    //   4460: astore_1
    //   4461: aload_1
    //   4462: aload 5
    //   4464: getfield 691	org/vidogram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   4467: putfield 1597	org/vidogram/tgnet/TLRPC$InputMedia:phone_number	Ljava/lang/String;
    //   4470: aload_1
    //   4471: aload 5
    //   4473: getfield 695	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   4476: putfield 1598	org/vidogram/tgnet/TLRPC$InputMedia:first_name	Ljava/lang/String;
    //   4479: aload_1
    //   4480: aload 5
    //   4482: getfield 699	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   4485: putfield 1599	org/vidogram/tgnet/TLRPC$InputMedia:last_name	Ljava/lang/String;
    //   4488: aload 12
    //   4490: astore_2
    //   4491: goto -604 -> 3887
    //   4494: aload 6
    //   4496: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4499: lconst_0
    //   4500: lcmp
    //   4501: ifne +230 -> 4731
    //   4504: aload 26
    //   4506: ifnonnull +134 -> 4640
    //   4509: aload 27
    //   4511: ifnull +129 -> 4640
    //   4514: aload 27
    //   4516: invokevirtual 347	java/lang/String:length	()I
    //   4519: ifle +121 -> 4640
    //   4522: aload 27
    //   4524: ldc_w 1215
    //   4527: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   4530: ifeq +110 -> 4640
    //   4533: aload 17
    //   4535: ifnull +105 -> 4640
    //   4538: new 1601	org/vidogram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   4541: dup
    //   4542: invokespecial 1602	org/vidogram/tgnet/TLRPC$TL_inputMediaGifExternal:<init>	()V
    //   4545: astore_2
    //   4546: aload 17
    //   4548: ldc_w 1603
    //   4551: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   4554: checkcast 340	java/lang/String
    //   4557: ldc_w 1605
    //   4560: invokevirtual 1609	java/lang/String:split	(Ljava/lang/String;)[Ljava/lang/String;
    //   4563: astore_1
    //   4564: aload_1
    //   4565: arraylength
    //   4566: iconst_2
    //   4567: if_icmpne +3406 -> 7973
    //   4570: aload_2
    //   4571: checkcast 1601	org/vidogram/tgnet/TLRPC$TL_inputMediaGifExternal
    //   4574: aload_1
    //   4575: iconst_0
    //   4576: aaload
    //   4577: putfield 1610	org/vidogram/tgnet/TLRPC$TL_inputMediaGifExternal:url	Ljava/lang/String;
    //   4580: aload_2
    //   4581: aload_1
    //   4582: iconst_1
    //   4583: aaload
    //   4584: putfield 1613	org/vidogram/tgnet/TLRPC$InputMedia:q	Ljava/lang/String;
    //   4587: goto +3386 -> 7973
    //   4590: aload_2
    //   4591: aload 6
    //   4593: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   4596: putfield 1577	org/vidogram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   4599: aload_2
    //   4600: aload 6
    //   4602: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   4605: putfield 1578	org/vidogram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   4608: aload 6
    //   4610: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4613: ifnull +3365 -> 7978
    //   4616: aload 6
    //   4618: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4621: astore 4
    //   4623: aload_2
    //   4624: aload 4
    //   4626: putfield 1529	org/vidogram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4629: aload_2
    //   4630: astore 4
    //   4632: aload_1
    //   4633: astore_2
    //   4634: aload 4
    //   4636: astore_1
    //   4637: goto -750 -> 3887
    //   4640: aload 6
    //   4642: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   4645: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4648: ifnull +72 -> 4720
    //   4651: aload 6
    //   4653: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   4656: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4659: instanceof 1615
    //   4662: ifeq +58 -> 4720
    //   4665: new 1575	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument
    //   4668: dup
    //   4669: invokespecial 1576	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedThumbDocument:<init>	()V
    //   4672: astore_2
    //   4673: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   4676: dup
    //   4677: aload_0
    //   4678: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   4681: astore_1
    //   4682: aload_1
    //   4683: aload 27
    //   4685: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   4688: aload_1
    //   4689: iconst_2
    //   4690: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4693: aload_1
    //   4694: aload 16
    //   4696: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   4699: aload_1
    //   4700: aload 6
    //   4702: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   4705: aload_1
    //   4706: aload 6
    //   4708: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   4711: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4714: putfield 392	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   4717: goto -127 -> 4590
    //   4720: new 1580	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   4723: dup
    //   4724: invokespecial 1581	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   4727: astore_2
    //   4728: goto -55 -> 4673
    //   4731: new 1583	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument
    //   4734: dup
    //   4735: invokespecial 1584	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   4738: astore_2
    //   4739: aload_2
    //   4740: new 1586	org/vidogram/tgnet/TLRPC$TL_inputDocument
    //   4743: dup
    //   4744: invokespecial 1587	org/vidogram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   4747: putfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4750: aload_2
    //   4751: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4754: aload 6
    //   4756: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   4759: putfield 1592	org/vidogram/tgnet/TLRPC$InputDocument:id	J
    //   4762: aload_2
    //   4763: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4766: aload 6
    //   4768: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4771: putfield 1593	org/vidogram/tgnet/TLRPC$InputDocument:access_hash	J
    //   4774: aload 6
    //   4776: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4779: ifnull +3207 -> 7986
    //   4782: aload 6
    //   4784: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4787: astore_1
    //   4788: aload_2
    //   4789: aload_1
    //   4790: putfield 1591	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   4793: aload_2
    //   4794: astore_1
    //   4795: aload 12
    //   4797: astore_2
    //   4798: goto -911 -> 3887
    //   4801: iload 18
    //   4803: bipush 8
    //   4805: if_icmpne +2891 -> 7696
    //   4808: aload 6
    //   4810: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4813: lconst_0
    //   4814: lcmp
    //   4815: ifne +84 -> 4899
    //   4818: new 1580	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument
    //   4821: dup
    //   4822: invokespecial 1581	org/vidogram/tgnet/TLRPC$TL_inputMediaUploadedDocument:<init>	()V
    //   4825: astore 4
    //   4827: aload 4
    //   4829: aload 6
    //   4831: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   4834: putfield 1577	org/vidogram/tgnet/TLRPC$InputMedia:mime_type	Ljava/lang/String;
    //   4837: aload 4
    //   4839: aload 6
    //   4841: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   4844: putfield 1578	org/vidogram/tgnet/TLRPC$InputMedia:attributes	Ljava/util/ArrayList;
    //   4847: aload 6
    //   4849: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4852: ifnull +3141 -> 7993
    //   4855: aload 6
    //   4857: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4860: astore_1
    //   4861: aload 4
    //   4863: aload_1
    //   4864: putfield 1529	org/vidogram/tgnet/TLRPC$InputMedia:caption	Ljava/lang/String;
    //   4867: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   4870: dup
    //   4871: aload_0
    //   4872: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   4875: astore_2
    //   4876: aload_2
    //   4877: iconst_3
    //   4878: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   4881: aload_2
    //   4882: aload 16
    //   4884: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   4887: aload_2
    //   4888: aload 6
    //   4890: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   4893: aload 4
    //   4895: astore_1
    //   4896: goto -1009 -> 3887
    //   4899: new 1583	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument
    //   4902: dup
    //   4903: invokespecial 1584	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:<init>	()V
    //   4906: astore_2
    //   4907: aload_2
    //   4908: new 1586	org/vidogram/tgnet/TLRPC$TL_inputDocument
    //   4911: dup
    //   4912: invokespecial 1587	org/vidogram/tgnet/TLRPC$TL_inputDocument:<init>	()V
    //   4915: putfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4918: aload 6
    //   4920: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4923: ifnull +3077 -> 8000
    //   4926: aload 6
    //   4928: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   4931: astore_1
    //   4932: aload_2
    //   4933: aload_1
    //   4934: putfield 1591	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:caption	Ljava/lang/String;
    //   4937: aload_2
    //   4938: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4941: aload 6
    //   4943: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   4946: putfield 1592	org/vidogram/tgnet/TLRPC$InputDocument:id	J
    //   4949: aload_2
    //   4950: getfield 1590	org/vidogram/tgnet/TLRPC$TL_inputMediaDocument:id	Lorg/vidogram/tgnet/TLRPC$InputDocument;
    //   4953: aload 6
    //   4955: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   4958: putfield 1593	org/vidogram/tgnet/TLRPC$InputDocument:access_hash	J
    //   4961: aload_2
    //   4962: astore_1
    //   4963: aload 12
    //   4965: astore_2
    //   4966: goto -1079 -> 3887
    //   4969: aload 4
    //   4971: aload_3
    //   4972: putfield 1419	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:contacts	Ljava/util/ArrayList;
    //   4975: aload 4
    //   4977: aload_1
    //   4978: putfield 517	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:media	Lorg/vidogram/tgnet/TLRPC$InputMedia;
    //   4981: aload 4
    //   4983: aload 5
    //   4985: putfield 1424	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:random_id	Ljava/util/ArrayList;
    //   4988: aload 4
    //   4990: ldc_w 743
    //   4993: putfield 1416	org/vidogram/tgnet/TLRPC$TL_messages_sendBroadcast:message	Ljava/lang/String;
    //   4996: aload_2
    //   4997: ifnull +9 -> 5006
    //   5000: aload_2
    //   5001: aload 4
    //   5003: putfield 388	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/vidogram/tgnet/TLObject;
    //   5006: aload 4
    //   5008: astore_1
    //   5009: aload 14
    //   5011: ifnonnull +12 -> 5023
    //   5014: lload 8
    //   5016: iconst_0
    //   5017: invokestatic 1467	org/vidogram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   5020: aload 4
    //   5022: astore_1
    //   5023: iload 18
    //   5025: iconst_1
    //   5026: if_icmpne +132 -> 5158
    //   5029: aload_0
    //   5030: aload_1
    //   5031: aload 16
    //   5033: aconst_null
    //   5034: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5037: return
    //   5038: new 497	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia
    //   5041: dup
    //   5042: invokespecial 1616	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:<init>	()V
    //   5045: astore_3
    //   5046: aload_3
    //   5047: aload 28
    //   5049: putfield 1617	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   5052: aload 7
    //   5054: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   5057: instanceof 1433
    //   5060: ifeq +44 -> 5104
    //   5063: aload_3
    //   5064: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   5067: ldc_w 1435
    //   5070: iconst_0
    //   5071: invokevirtual 1441	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   5074: new 456	java/lang/StringBuilder
    //   5077: dup
    //   5078: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   5081: ldc_w 1443
    //   5084: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   5087: lload 8
    //   5089: invokevirtual 483	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   5092: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   5095: iconst_0
    //   5096: invokeinterface 1449 3 0
    //   5101: putfield 1618	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:silent	Z
    //   5104: aload_3
    //   5105: aload 7
    //   5107: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   5110: putfield 1619	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:random_id	J
    //   5113: aload_3
    //   5114: aload_1
    //   5115: putfield 501	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/vidogram/tgnet/TLRPC$InputMedia;
    //   5118: aload 11
    //   5120: ifnull +22 -> 5142
    //   5123: aload_3
    //   5124: aload_3
    //   5125: getfield 1620	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5128: iconst_1
    //   5129: ior
    //   5130: putfield 1620	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:flags	I
    //   5133: aload_3
    //   5134: aload 11
    //   5136: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   5139: putfield 1621	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:reply_to_msg_id	I
    //   5142: aload_3
    //   5143: astore_1
    //   5144: aload_2
    //   5145: ifnull -122 -> 5023
    //   5148: aload_2
    //   5149: aload_3
    //   5150: putfield 388	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendRequest	Lorg/vidogram/tgnet/TLObject;
    //   5153: aload_3
    //   5154: astore_1
    //   5155: goto -132 -> 5023
    //   5158: iload 18
    //   5160: iconst_2
    //   5161: if_icmpne +28 -> 5189
    //   5164: aload 24
    //   5166: getfield 1525	org/vidogram/tgnet/TLRPC$TL_photo:access_hash	J
    //   5169: lconst_0
    //   5170: lcmp
    //   5171: ifne +9 -> 5180
    //   5174: aload_0
    //   5175: aload_2
    //   5176: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5179: return
    //   5180: aload_0
    //   5181: aload_1
    //   5182: aload 16
    //   5184: aconst_null
    //   5185: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5188: return
    //   5189: iload 18
    //   5191: iconst_3
    //   5192: if_icmpne +28 -> 5220
    //   5195: aload 6
    //   5197: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   5200: lconst_0
    //   5201: lcmp
    //   5202: ifne +9 -> 5211
    //   5205: aload_0
    //   5206: aload_2
    //   5207: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5210: return
    //   5211: aload_0
    //   5212: aload_1
    //   5213: aload 16
    //   5215: aconst_null
    //   5216: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5219: return
    //   5220: iload 18
    //   5222: bipush 6
    //   5224: if_icmpne +12 -> 5236
    //   5227: aload_0
    //   5228: aload_1
    //   5229: aload 16
    //   5231: aconst_null
    //   5232: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5235: return
    //   5236: iload 18
    //   5238: bipush 7
    //   5240: if_icmpne +33 -> 5273
    //   5243: aload 6
    //   5245: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   5248: lconst_0
    //   5249: lcmp
    //   5250: ifne +13 -> 5263
    //   5253: aload_2
    //   5254: ifnull +9 -> 5263
    //   5257: aload_0
    //   5258: aload_2
    //   5259: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5262: return
    //   5263: aload_0
    //   5264: aload_1
    //   5265: aload 16
    //   5267: aload 27
    //   5269: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5272: return
    //   5273: iload 18
    //   5275: bipush 8
    //   5277: if_icmpne -5270 -> 7
    //   5280: aload 6
    //   5282: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   5285: lconst_0
    //   5286: lcmp
    //   5287: ifne +9 -> 5296
    //   5290: aload_0
    //   5291: aload_2
    //   5292: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5295: return
    //   5296: aload_0
    //   5297: aload_1
    //   5298: aload 16
    //   5300: aconst_null
    //   5301: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   5304: return
    //   5305: new 1469	org/vidogram/tgnet/TLRPC$TL_decryptedMessage
    //   5308: dup
    //   5309: invokespecial 1470	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:<init>	()V
    //   5312: astore_3
    //   5313: aload_3
    //   5314: aload 7
    //   5316: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   5319: putfield 1471	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:ttl	I
    //   5322: aload 15
    //   5324: ifnull +29 -> 5353
    //   5327: aload 15
    //   5329: invokevirtual 962	java/util/ArrayList:isEmpty	()Z
    //   5332: ifne +21 -> 5353
    //   5335: aload_3
    //   5336: aload 15
    //   5338: putfield 1472	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:entities	Ljava/util/ArrayList;
    //   5341: aload_3
    //   5342: aload_3
    //   5343: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5346: sipush 128
    //   5349: ior
    //   5350: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5353: aload 11
    //   5355: ifnull +39 -> 5394
    //   5358: aload 11
    //   5360: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   5363: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   5366: lconst_0
    //   5367: lcmp
    //   5368: ifeq +26 -> 5394
    //   5371: aload_3
    //   5372: aload 11
    //   5374: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   5377: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   5380: putfield 1474	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:reply_to_random_id	J
    //   5383: aload_3
    //   5384: aload_3
    //   5385: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5388: bipush 8
    //   5390: ior
    //   5391: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5394: aload_3
    //   5395: aload_3
    //   5396: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5399: sipush 512
    //   5402: ior
    //   5403: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5406: aload 17
    //   5408: ifnull +41 -> 5449
    //   5411: aload 17
    //   5413: ldc_w 1109
    //   5416: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5419: ifnull +30 -> 5449
    //   5422: aload_3
    //   5423: aload 17
    //   5425: ldc_w 1109
    //   5428: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   5431: checkcast 340	java/lang/String
    //   5434: putfield 1475	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:via_bot_name	Ljava/lang/String;
    //   5437: aload_3
    //   5438: aload_3
    //   5439: getfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5442: sipush 2048
    //   5445: ior
    //   5446: putfield 1473	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:flags	I
    //   5449: aload_3
    //   5450: aload 7
    //   5452: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   5455: putfield 1476	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:random_id	J
    //   5458: aload_3
    //   5459: ldc_w 743
    //   5462: putfield 1477	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:message	Ljava/lang/String;
    //   5465: iload 18
    //   5467: iconst_1
    //   5468: if_icmpne +2539 -> 8007
    //   5471: aload_2
    //   5472: instanceof 647
    //   5475: ifeq +116 -> 5591
    //   5478: aload_3
    //   5479: new 1623	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVenue
    //   5482: dup
    //   5483: invokespecial 1624	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVenue:<init>	()V
    //   5486: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5489: aload_3
    //   5490: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5493: aload_2
    //   5494: getfield 1502	org/vidogram/tgnet/TLRPC$MessageMedia:address	Ljava/lang/String;
    //   5497: putfield 1625	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:address	Ljava/lang/String;
    //   5500: aload_3
    //   5501: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5504: aload_2
    //   5505: getfield 1504	org/vidogram/tgnet/TLRPC$MessageMedia:title	Ljava/lang/String;
    //   5508: putfield 1626	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:title	Ljava/lang/String;
    //   5511: aload_3
    //   5512: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5515: aload_2
    //   5516: getfield 1506	org/vidogram/tgnet/TLRPC$MessageMedia:provider	Ljava/lang/String;
    //   5519: putfield 1627	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:provider	Ljava/lang/String;
    //   5522: aload_3
    //   5523: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5526: aload_2
    //   5527: getfield 1508	org/vidogram/tgnet/TLRPC$MessageMedia:venue_id	Ljava/lang/String;
    //   5530: putfield 1628	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:venue_id	Ljava/lang/String;
    //   5533: aload_3
    //   5534: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5537: aload_2
    //   5538: getfield 1517	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   5541: getfield 1019	org/vidogram/tgnet/TLRPC$GeoPoint:lat	D
    //   5544: putfield 1629	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:lat	D
    //   5547: aload_3
    //   5548: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5551: aload_2
    //   5552: getfield 1517	org/vidogram/tgnet/TLRPC$MessageMedia:geo	Lorg/vidogram/tgnet/TLRPC$GeoPoint;
    //   5555: getfield 1025	org/vidogram/tgnet/TLRPC$GeoPoint:_long	D
    //   5558: putfield 1630	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:_long	D
    //   5561: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   5564: aload_3
    //   5565: aload 16
    //   5567: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   5570: aload 26
    //   5572: aconst_null
    //   5573: aconst_null
    //   5574: aload 16
    //   5576: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   5579: aload 14
    //   5581: ifnonnull -5574 -> 7
    //   5584: lload 8
    //   5586: iconst_0
    //   5587: invokestatic 1467	org/vidogram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   5590: return
    //   5591: aload_3
    //   5592: new 1632	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint
    //   5595: dup
    //   5596: invokespecial 1633	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaGeoPoint:<init>	()V
    //   5599: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5602: goto -69 -> 5533
    //   5605: aload 24
    //   5607: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5610: iconst_0
    //   5611: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5614: checkcast 926	org/vidogram/tgnet/TLRPC$PhotoSize
    //   5617: astore 4
    //   5619: aload 24
    //   5621: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5624: aload 24
    //   5626: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5629: invokevirtual 605	java/util/ArrayList:size	()I
    //   5632: iconst_1
    //   5633: isub
    //   5634: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5637: checkcast 926	org/vidogram/tgnet/TLRPC$PhotoSize
    //   5640: astore_2
    //   5641: aload 4
    //   5643: invokestatic 1637	org/vidogram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/vidogram/tgnet/TLRPC$PhotoSize;)V
    //   5646: aload_3
    //   5647: new 1639	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   5650: dup
    //   5651: invokespecial 1640	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:<init>	()V
    //   5654: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5657: aload_3
    //   5658: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5661: astore 5
    //   5663: aload 24
    //   5665: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   5668: ifnull +2360 -> 8028
    //   5671: aload 24
    //   5673: getfield 1210	org/vidogram/tgnet/TLRPC$TL_photo:caption	Ljava/lang/String;
    //   5676: astore_1
    //   5677: aload 5
    //   5679: aload_1
    //   5680: putfield 1641	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   5683: aload 4
    //   5685: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   5688: ifnull +160 -> 5848
    //   5691: aload_3
    //   5692: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5695: checkcast 1639	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   5698: aload 4
    //   5700: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   5703: putfield 1647	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   5706: aload_3
    //   5707: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5710: aload 4
    //   5712: getfield 1648	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5715: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   5718: aload_3
    //   5719: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5722: aload 4
    //   5724: getfield 1652	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5727: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   5730: aload_3
    //   5731: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5734: aload_2
    //   5735: getfield 1652	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   5738: putfield 1656	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   5741: aload_3
    //   5742: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5745: aload_2
    //   5746: getfield 1648	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   5749: putfield 1657	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   5752: aload_3
    //   5753: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5756: aload_2
    //   5757: getfield 1658	org/vidogram/tgnet/TLRPC$PhotoSize:size	I
    //   5760: putfield 1659	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   5763: aload_2
    //   5764: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5767: getfield 1662	org/vidogram/tgnet/TLRPC$FileLocation:key	[B
    //   5770: ifnonnull +125 -> 5895
    //   5773: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   5776: dup
    //   5777: aload_0
    //   5778: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   5781: astore_1
    //   5782: aload_1
    //   5783: aload 27
    //   5785: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   5788: aload_1
    //   5789: aload_3
    //   5790: putfield 413	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/vidogram/tgnet/TLRPC$TL_decryptedMessage;
    //   5793: aload_1
    //   5794: iconst_0
    //   5795: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   5798: aload_1
    //   5799: aload 16
    //   5801: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   5804: aload_1
    //   5805: aload 26
    //   5807: putfield 1666	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/vidogram/tgnet/TLRPC$EncryptedChat;
    //   5810: aload 10
    //   5812: ifnull +52 -> 5864
    //   5815: aload 10
    //   5817: invokevirtual 347	java/lang/String:length	()I
    //   5820: ifle +44 -> 5864
    //   5823: aload 10
    //   5825: ldc_w 1215
    //   5828: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   5831: ifeq +33 -> 5864
    //   5834: aload_1
    //   5835: aload 10
    //   5837: putfield 369	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   5840: aload_0
    //   5841: aload_1
    //   5842: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   5845: goto -266 -> 5579
    //   5848: aload_3
    //   5849: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5852: checkcast 1639	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto
    //   5855: iconst_0
    //   5856: newarray byte
    //   5858: putfield 1647	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaPhoto:thumb	[B
    //   5861: goto -155 -> 5706
    //   5864: aload_1
    //   5865: aload 24
    //   5867: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5870: aload 24
    //   5872: getfield 1218	org/vidogram/tgnet/TLRPC$TL_photo:sizes	Ljava/util/ArrayList;
    //   5875: invokevirtual 605	java/util/ArrayList:size	()I
    //   5878: iconst_1
    //   5879: isub
    //   5880: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   5883: checkcast 926	org/vidogram/tgnet/TLRPC$PhotoSize
    //   5886: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5889: putfield 392	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5892: goto -52 -> 5840
    //   5895: new 1668	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile
    //   5898: dup
    //   5899: invokespecial 1669	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   5902: astore_1
    //   5903: aload_1
    //   5904: aload_2
    //   5905: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5908: getfield 520	org/vidogram/tgnet/TLRPC$FileLocation:volume_id	J
    //   5911: putfield 1670	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   5914: aload_1
    //   5915: aload_2
    //   5916: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5919: getfield 1673	org/vidogram/tgnet/TLRPC$FileLocation:secret	J
    //   5922: putfield 1674	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   5925: aload_3
    //   5926: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5929: aload_2
    //   5930: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5933: getfield 1662	org/vidogram/tgnet/TLRPC$FileLocation:key	[B
    //   5936: putfield 1675	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   5939: aload_3
    //   5940: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   5943: aload_2
    //   5944: getfield 1219	org/vidogram/tgnet/TLRPC$PhotoSize:location	Lorg/vidogram/tgnet/TLRPC$FileLocation;
    //   5947: getfield 1678	org/vidogram/tgnet/TLRPC$FileLocation:iv	[B
    //   5950: putfield 1679	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   5953: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   5956: aload_3
    //   5957: aload 16
    //   5959: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   5962: aload 26
    //   5964: aload_1
    //   5965: aconst_null
    //   5966: aload 16
    //   5968: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   5971: goto -392 -> 5579
    //   5974: iload 18
    //   5976: iconst_3
    //   5977: if_icmpne +444 -> 6421
    //   5980: aload 6
    //   5982: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   5985: invokestatic 1637	org/vidogram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/vidogram/tgnet/TLRPC$PhotoSize;)V
    //   5988: aload 6
    //   5990: invokestatic 1682	org/vidogram/messenger/MessageObject:isNewGifDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   5993: ifeq +290 -> 6283
    //   5996: aload_3
    //   5997: new 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6000: dup
    //   6001: invokespecial 1685	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   6004: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6007: aload_3
    //   6008: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6011: aload 6
    //   6013: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6016: putfield 1686	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   6019: aload 6
    //   6021: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6024: ifnull +243 -> 6267
    //   6027: aload 6
    //   6029: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6032: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6035: ifnull +232 -> 6267
    //   6038: aload_3
    //   6039: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6042: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6045: aload 6
    //   6047: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6050: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6053: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   6056: aload_3
    //   6057: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6060: astore_2
    //   6061: aload 6
    //   6063: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6066: ifnull +1969 -> 8035
    //   6069: aload 6
    //   6071: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6074: astore_1
    //   6075: aload_2
    //   6076: aload_1
    //   6077: putfield 1641	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6080: aload_3
    //   6081: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6084: ldc_w 1689
    //   6087: putfield 1690	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   6090: aload_3
    //   6091: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6094: aload 6
    //   6096: getfield 510	org/vidogram/tgnet/TLRPC$TL_document:size	I
    //   6099: putfield 1659	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6102: iconst_0
    //   6103: istore 18
    //   6105: iload 18
    //   6107: aload 6
    //   6109: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6112: invokevirtual 605	java/util/ArrayList:size	()I
    //   6115: if_icmpge +57 -> 6172
    //   6118: aload 6
    //   6120: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6123: iload 18
    //   6125: invokevirtual 609	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   6128: checkcast 1236	org/vidogram/tgnet/TLRPC$DocumentAttribute
    //   6131: astore_1
    //   6132: aload_1
    //   6133: instanceof 254
    //   6136: ifeq +1906 -> 8042
    //   6139: aload_3
    //   6140: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6143: aload_1
    //   6144: getfield 1691	org/vidogram/tgnet/TLRPC$DocumentAttribute:w	I
    //   6147: putfield 1656	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:w	I
    //   6150: aload_3
    //   6151: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6154: aload_1
    //   6155: getfield 1692	org/vidogram/tgnet/TLRPC$DocumentAttribute:h	I
    //   6158: putfield 1657	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:h	I
    //   6161: aload_3
    //   6162: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6165: aload_1
    //   6166: getfield 1408	org/vidogram/tgnet/TLRPC$DocumentAttribute:duration	I
    //   6169: putfield 1693	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:duration	I
    //   6172: aload_3
    //   6173: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6176: aload 6
    //   6178: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6181: getfield 1648	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   6184: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6187: aload_3
    //   6188: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6191: aload 6
    //   6193: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6196: getfield 1652	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   6199: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6202: aload 6
    //   6204: getfield 1694	org/vidogram/tgnet/TLRPC$TL_document:key	[B
    //   6207: ifnonnull +143 -> 6350
    //   6210: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   6213: dup
    //   6214: aload_0
    //   6215: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   6218: astore_1
    //   6219: aload_1
    //   6220: aload 27
    //   6222: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   6225: aload_1
    //   6226: aload_3
    //   6227: putfield 413	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/vidogram/tgnet/TLRPC$TL_decryptedMessage;
    //   6230: aload_1
    //   6231: iconst_1
    //   6232: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   6235: aload_1
    //   6236: aload 16
    //   6238: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   6241: aload_1
    //   6242: aload 26
    //   6244: putfield 1666	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/vidogram/tgnet/TLRPC$EncryptedChat;
    //   6247: aload_1
    //   6248: aload 6
    //   6250: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   6253: aload_1
    //   6254: aload 4
    //   6256: putfield 439	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:videoEditedInfo	Lorg/vidogram/messenger/VideoEditedInfo;
    //   6259: aload_0
    //   6260: aload_1
    //   6261: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6264: goto -685 -> 5579
    //   6267: aload_3
    //   6268: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6271: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6274: iconst_0
    //   6275: newarray byte
    //   6277: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   6280: goto -224 -> 6056
    //   6283: aload_3
    //   6284: new 1696	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6287: dup
    //   6288: invokespecial 1697	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:<init>	()V
    //   6291: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6294: aload 6
    //   6296: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6299: ifnull +35 -> 6334
    //   6302: aload 6
    //   6304: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6307: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6310: ifnull +24 -> 6334
    //   6313: aload_3
    //   6314: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6317: checkcast 1696	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6320: aload 6
    //   6322: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6325: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6328: putfield 1698	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   6331: goto -275 -> 6056
    //   6334: aload_3
    //   6335: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6338: checkcast 1696	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo
    //   6341: iconst_0
    //   6342: newarray byte
    //   6344: putfield 1698	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaVideo:thumb	[B
    //   6347: goto -291 -> 6056
    //   6350: new 1668	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile
    //   6353: dup
    //   6354: invokespecial 1669	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   6357: astore_1
    //   6358: aload_1
    //   6359: aload 6
    //   6361: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   6364: putfield 1670	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   6367: aload_1
    //   6368: aload 6
    //   6370: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   6373: putfield 1674	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   6376: aload_3
    //   6377: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6380: aload 6
    //   6382: getfield 1694	org/vidogram/tgnet/TLRPC$TL_document:key	[B
    //   6385: putfield 1675	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   6388: aload_3
    //   6389: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6392: aload 6
    //   6394: getfield 1699	org/vidogram/tgnet/TLRPC$TL_document:iv	[B
    //   6397: putfield 1679	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   6400: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   6403: aload_3
    //   6404: aload 16
    //   6406: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   6409: aload 26
    //   6411: aload_1
    //   6412: aconst_null
    //   6413: aload 16
    //   6415: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   6418: goto -839 -> 5579
    //   6421: iload 18
    //   6423: bipush 6
    //   6425: if_icmpne +1626 -> 8051
    //   6428: aload_3
    //   6429: new 1701	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaContact
    //   6432: dup
    //   6433: invokespecial 1702	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaContact:<init>	()V
    //   6436: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6439: aload_3
    //   6440: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6443: aload 5
    //   6445: getfield 691	org/vidogram/tgnet/TLRPC$User:phone	Ljava/lang/String;
    //   6448: putfield 1703	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:phone_number	Ljava/lang/String;
    //   6451: aload_3
    //   6452: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6455: aload 5
    //   6457: getfield 695	org/vidogram/tgnet/TLRPC$User:first_name	Ljava/lang/String;
    //   6460: putfield 1704	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:first_name	Ljava/lang/String;
    //   6463: aload_3
    //   6464: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6467: aload 5
    //   6469: getfield 699	org/vidogram/tgnet/TLRPC$User:last_name	Ljava/lang/String;
    //   6472: putfield 1705	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:last_name	Ljava/lang/String;
    //   6475: aload_3
    //   6476: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6479: aload 5
    //   6481: getfield 1170	org/vidogram/tgnet/TLRPC$User:id	I
    //   6484: putfield 1706	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:user_id	I
    //   6487: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   6490: aload_3
    //   6491: aload 16
    //   6493: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   6496: aload 26
    //   6498: aconst_null
    //   6499: aconst_null
    //   6500: aload 16
    //   6502: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   6505: goto -926 -> 5579
    //   6508: aload 6
    //   6510: invokestatic 1234	org/vidogram/messenger/MessageObject:isStickerDocument	(Lorg/vidogram/tgnet/TLRPC$Document;)Z
    //   6513: ifeq +178 -> 6691
    //   6516: aload_3
    //   6517: new 1708	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   6520: dup
    //   6521: invokespecial 1709	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:<init>	()V
    //   6524: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6527: aload_3
    //   6528: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6531: aload 6
    //   6533: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   6536: putfield 1710	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:id	J
    //   6539: aload_3
    //   6540: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6543: aload 6
    //   6545: getfield 815	org/vidogram/tgnet/TLRPC$TL_document:date	I
    //   6548: putfield 1711	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:date	I
    //   6551: aload_3
    //   6552: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6555: aload 6
    //   6557: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   6560: putfield 1712	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:access_hash	J
    //   6563: aload_3
    //   6564: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6567: aload 6
    //   6569: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   6572: putfield 1690	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   6575: aload_3
    //   6576: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6579: aload 6
    //   6581: getfield 510	org/vidogram/tgnet/TLRPC$TL_document:size	I
    //   6584: putfield 1659	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6587: aload_3
    //   6588: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6591: aload 6
    //   6593: getfield 531	org/vidogram/tgnet/TLRPC$TL_document:dc_id	I
    //   6596: putfield 1713	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:dc_id	I
    //   6599: aload_3
    //   6600: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6603: aload 6
    //   6605: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6608: putfield 1686	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   6611: aload 6
    //   6613: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6616: ifnonnull +57 -> 6673
    //   6619: aload_3
    //   6620: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6623: checkcast 1708	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   6626: new 921	org/vidogram/tgnet/TLRPC$TL_photoSizeEmpty
    //   6629: dup
    //   6630: invokespecial 922	org/vidogram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
    //   6633: putfield 1714	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6636: aload_3
    //   6637: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6640: checkcast 1708	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   6643: getfield 1714	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6646: ldc_w 924
    //   6649: putfield 928	org/vidogram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
    //   6652: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   6655: aload_3
    //   6656: aload 16
    //   6658: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   6661: aload 26
    //   6663: aconst_null
    //   6664: aconst_null
    //   6665: aload 16
    //   6667: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   6670: goto -1091 -> 5579
    //   6673: aload_3
    //   6674: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6677: checkcast 1708	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument
    //   6680: aload 6
    //   6682: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6685: putfield 1714	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaExternalDocument:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6688: goto -36 -> 6652
    //   6691: aload 6
    //   6693: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6696: invokestatic 1637	org/vidogram/messenger/ImageLoader:fillPhotoSizeWithBytes	(Lorg/vidogram/tgnet/TLRPC$PhotoSize;)V
    //   6699: aload_3
    //   6700: new 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6703: dup
    //   6704: invokespecial 1685	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   6707: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6710: aload_3
    //   6711: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6714: aload 6
    //   6716: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   6719: putfield 1686	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   6722: aload_3
    //   6723: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6726: astore_2
    //   6727: aload 6
    //   6729: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6732: ifnull +1341 -> 8073
    //   6735: aload 6
    //   6737: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   6740: astore_1
    //   6741: aload_2
    //   6742: aload_1
    //   6743: putfield 1641	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   6746: aload 6
    //   6748: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6751: ifnull +175 -> 6926
    //   6754: aload 6
    //   6756: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6759: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6762: ifnull +164 -> 6926
    //   6765: aload_3
    //   6766: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6769: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6772: aload 6
    //   6774: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6777: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   6780: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   6783: aload_3
    //   6784: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6787: aload 6
    //   6789: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6792: getfield 1648	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   6795: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6798: aload_3
    //   6799: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6802: aload 6
    //   6804: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   6807: getfield 1652	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   6810: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6813: aload_3
    //   6814: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6817: aload 6
    //   6819: getfield 510	org/vidogram/tgnet/TLRPC$TL_document:size	I
    //   6822: putfield 1659	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   6825: aload_3
    //   6826: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6829: aload 6
    //   6831: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   6834: putfield 1690	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   6837: aload 6
    //   6839: getfield 1694	org/vidogram/tgnet/TLRPC$TL_document:key	[B
    //   6842: ifnonnull +116 -> 6958
    //   6845: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   6848: dup
    //   6849: aload_0
    //   6850: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   6853: astore_1
    //   6854: aload_1
    //   6855: aload 27
    //   6857: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   6860: aload_1
    //   6861: aload_3
    //   6862: putfield 413	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/vidogram/tgnet/TLRPC$TL_decryptedMessage;
    //   6865: aload_1
    //   6866: iconst_2
    //   6867: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   6870: aload_1
    //   6871: aload 16
    //   6873: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   6876: aload_1
    //   6877: aload 26
    //   6879: putfield 1666	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/vidogram/tgnet/TLRPC$EncryptedChat;
    //   6882: aload 10
    //   6884: ifnull +28 -> 6912
    //   6887: aload 10
    //   6889: invokevirtual 347	java/lang/String:length	()I
    //   6892: ifle +20 -> 6912
    //   6895: aload 10
    //   6897: ldc_w 1215
    //   6900: invokevirtual 353	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   6903: ifeq +9 -> 6912
    //   6906: aload_1
    //   6907: aload 10
    //   6909: putfield 369	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:httpLocation	Ljava/lang/String;
    //   6912: aload_1
    //   6913: aload 6
    //   6915: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   6918: aload_0
    //   6919: aload_1
    //   6920: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   6923: goto -1344 -> 5579
    //   6926: aload_3
    //   6927: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6930: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   6933: iconst_0
    //   6934: newarray byte
    //   6936: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   6939: aload_3
    //   6940: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6943: iconst_0
    //   6944: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   6947: aload_3
    //   6948: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6951: iconst_0
    //   6952: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   6955: goto -142 -> 6813
    //   6958: new 1668	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile
    //   6961: dup
    //   6962: invokespecial 1669	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:<init>	()V
    //   6965: astore_1
    //   6966: aload_1
    //   6967: aload 6
    //   6969: getfield 480	org/vidogram/tgnet/TLRPC$TL_document:id	J
    //   6972: putfield 1670	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:id	J
    //   6975: aload_1
    //   6976: aload 6
    //   6978: getfield 1573	org/vidogram/tgnet/TLRPC$TL_document:access_hash	J
    //   6981: putfield 1674	org/vidogram/tgnet/TLRPC$TL_inputEncryptedFile:access_hash	J
    //   6984: aload_3
    //   6985: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   6988: aload 6
    //   6990: getfield 1694	org/vidogram/tgnet/TLRPC$TL_document:key	[B
    //   6993: putfield 1675	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:key	[B
    //   6996: aload_3
    //   6997: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7000: aload 6
    //   7002: getfield 1699	org/vidogram/tgnet/TLRPC$TL_document:iv	[B
    //   7005: putfield 1679	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:iv	[B
    //   7008: invokestatic 1491	org/vidogram/messenger/SecretChatHelper:getInstance	()Lorg/vidogram/messenger/SecretChatHelper;
    //   7011: aload_3
    //   7012: aload 16
    //   7014: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7017: aload 26
    //   7019: aload_1
    //   7020: aconst_null
    //   7021: aload 16
    //   7023: invokevirtual 1495	org/vidogram/messenger/SecretChatHelper:performSendEncryptedRequest	(Lorg/vidogram/tgnet/TLRPC$DecryptedMessage;Lorg/vidogram/tgnet/TLRPC$Message;Lorg/vidogram/tgnet/TLRPC$EncryptedChat;Lorg/vidogram/tgnet/TLRPC$InputEncryptedFile;Ljava/lang/String;Lorg/vidogram/messenger/MessageObject;)V
    //   7026: goto -1447 -> 5579
    //   7029: iload 18
    //   7031: bipush 8
    //   7033: if_icmpne -1454 -> 5579
    //   7036: new 100	org/vidogram/messenger/SendMessagesHelper$DelayedMessage
    //   7039: dup
    //   7040: aload_0
    //   7041: invokespecial 1555	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;)V
    //   7044: astore_2
    //   7045: aload_2
    //   7046: aload 26
    //   7048: putfield 1666	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:encryptedChat	Lorg/vidogram/tgnet/TLRPC$EncryptedChat;
    //   7051: aload_2
    //   7052: aload_3
    //   7053: putfield 413	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:sendEncryptedRequest	Lorg/vidogram/tgnet/TLRPC$TL_decryptedMessage;
    //   7056: aload_2
    //   7057: aload 16
    //   7059: putfield 443	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:obj	Lorg/vidogram/messenger/MessageObject;
    //   7062: aload_2
    //   7063: aload 6
    //   7065: putfield 474	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:documentLocation	Lorg/vidogram/tgnet/TLRPC$TL_document;
    //   7068: aload_2
    //   7069: iconst_3
    //   7070: putfield 365	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:type	I
    //   7073: aload_3
    //   7074: new 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7077: dup
    //   7078: invokespecial 1685	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:<init>	()V
    //   7081: putfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7084: aload_3
    //   7085: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7088: aload 6
    //   7090: getfield 824	org/vidogram/tgnet/TLRPC$TL_document:attributes	Ljava/util/ArrayList;
    //   7093: putfield 1686	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:attributes	Ljava/util/ArrayList;
    //   7096: aload_3
    //   7097: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7100: astore 4
    //   7102: aload 6
    //   7104: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7107: ifnull +973 -> 8080
    //   7110: aload 6
    //   7112: getfield 931	org/vidogram/tgnet/TLRPC$TL_document:caption	Ljava/lang/String;
    //   7115: astore_1
    //   7116: aload 4
    //   7118: aload_1
    //   7119: putfield 1641	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:caption	Ljava/lang/String;
    //   7122: aload 6
    //   7124: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   7127: ifnull +100 -> 7227
    //   7130: aload 6
    //   7132: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   7135: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7138: ifnull +89 -> 7227
    //   7141: aload_3
    //   7142: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7145: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7148: aload 6
    //   7150: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   7153: getfield 1645	org/vidogram/tgnet/TLRPC$PhotoSize:bytes	[B
    //   7156: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7159: aload_3
    //   7160: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7163: aload 6
    //   7165: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   7168: getfield 1648	org/vidogram/tgnet/TLRPC$PhotoSize:h	I
    //   7171: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7174: aload_3
    //   7175: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7178: aload 6
    //   7180: getfield 850	org/vidogram/tgnet/TLRPC$TL_document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
    //   7183: getfield 1652	org/vidogram/tgnet/TLRPC$PhotoSize:w	I
    //   7186: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7189: aload_3
    //   7190: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7193: aload 6
    //   7195: getfield 831	org/vidogram/tgnet/TLRPC$TL_document:mime_type	Ljava/lang/String;
    //   7198: putfield 1690	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:mime_type	Ljava/lang/String;
    //   7201: aload_3
    //   7202: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7205: aload 6
    //   7207: getfield 510	org/vidogram/tgnet/TLRPC$TL_document:size	I
    //   7210: putfield 1659	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:size	I
    //   7213: aload_2
    //   7214: aload 27
    //   7216: putfield 1557	org/vidogram/messenger/SendMessagesHelper$DelayedMessage:originalPath	Ljava/lang/String;
    //   7219: aload_0
    //   7220: aload_2
    //   7221: invokespecial 226	org/vidogram/messenger/SendMessagesHelper:performSendDelayedMessage	(Lorg/vidogram/messenger/SendMessagesHelper$DelayedMessage;)V
    //   7224: goto -1645 -> 5579
    //   7227: aload_3
    //   7228: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7231: checkcast 1684	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument
    //   7234: iconst_0
    //   7235: newarray byte
    //   7237: putfield 1687	org/vidogram/tgnet/TLRPC$TL_decryptedMessageMediaDocument:thumb	[B
    //   7240: aload_3
    //   7241: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7244: iconst_0
    //   7245: putfield 1651	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_h	I
    //   7248: aload_3
    //   7249: getfield 1483	org/vidogram/tgnet/TLRPC$TL_decryptedMessage:media	Lorg/vidogram/tgnet/TLRPC$DecryptedMessageMedia;
    //   7252: iconst_0
    //   7253: putfield 1655	org/vidogram/tgnet/TLRPC$DecryptedMessageMedia:thumb_w	I
    //   7256: goto -67 -> 7189
    //   7259: iload 18
    //   7261: iconst_4
    //   7262: if_icmpne +241 -> 7503
    //   7265: new 1716	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages
    //   7268: dup
    //   7269: invokespecial 1717	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:<init>	()V
    //   7272: astore_1
    //   7273: aload_1
    //   7274: aload 28
    //   7276: putfield 1720	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:to_peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7279: aload_1
    //   7280: aload 14
    //   7282: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7285: getfield 1723	org/vidogram/tgnet/TLRPC$Message:with_my_score	Z
    //   7288: putfield 1724	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:with_my_score	Z
    //   7291: aload 14
    //   7293: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7296: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   7299: ifeq +168 -> 7467
    //   7302: invokestatic 766	org/vidogram/messenger/MessagesController:getInstance	()Lorg/vidogram/messenger/MessagesController;
    //   7305: aload 14
    //   7307: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7310: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   7313: ineg
    //   7314: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   7317: invokevirtual 1091	org/vidogram/messenger/MessagesController:getChat	(Ljava/lang/Integer;)Lorg/vidogram/tgnet/TLRPC$Chat;
    //   7320: astore_2
    //   7321: aload_1
    //   7322: new 1082	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel
    //   7325: dup
    //   7326: invokespecial 1725	org/vidogram/tgnet/TLRPC$TL_inputPeerChannel:<init>	()V
    //   7329: putfield 1728	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7332: aload_1
    //   7333: getfield 1728	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7336: aload 14
    //   7338: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7341: getfield 1404	org/vidogram/tgnet/TLRPC$Message:ttl	I
    //   7344: ineg
    //   7345: putfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   7348: aload_2
    //   7349: ifnull +14 -> 7363
    //   7352: aload_1
    //   7353: getfield 1728	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7356: aload_2
    //   7357: getfield 1729	org/vidogram/tgnet/TLRPC$Chat:access_hash	J
    //   7360: putfield 1367	org/vidogram/tgnet/TLRPC$InputPeer:access_hash	J
    //   7363: aload 14
    //   7365: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7368: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   7371: instanceof 1433
    //   7374: ifeq +44 -> 7418
    //   7377: aload_1
    //   7378: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   7381: ldc_w 1435
    //   7384: iconst_0
    //   7385: invokevirtual 1441	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   7388: new 456	java/lang/StringBuilder
    //   7391: dup
    //   7392: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   7395: ldc_w 1443
    //   7398: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   7401: lload 8
    //   7403: invokevirtual 483	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   7406: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   7409: iconst_0
    //   7410: invokeinterface 1449 3 0
    //   7415: putfield 1730	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:silent	Z
    //   7418: aload_1
    //   7419: getfield 1731	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:random_id	Ljava/util/ArrayList;
    //   7422: aload 7
    //   7424: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   7427: invokestatic 1382	java/lang/Long:valueOf	(J)Ljava/lang/Long;
    //   7430: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   7433: pop
    //   7434: aload 14
    //   7436: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   7439: iflt +42 -> 7481
    //   7442: aload_1
    //   7443: getfield 1733	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   7446: aload 14
    //   7448: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   7451: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   7454: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   7457: pop
    //   7458: aload_0
    //   7459: aload_1
    //   7460: aload 16
    //   7462: aconst_null
    //   7463: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   7466: return
    //   7467: aload_1
    //   7468: new 1735	org/vidogram/tgnet/TLRPC$TL_inputPeerEmpty
    //   7471: dup
    //   7472: invokespecial 1736	org/vidogram/tgnet/TLRPC$TL_inputPeerEmpty:<init>	()V
    //   7475: putfield 1728	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:from_peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7478: goto -115 -> 7363
    //   7481: aload_1
    //   7482: getfield 1733	org/vidogram/tgnet/TLRPC$TL_messages_forwardMessages:id	Ljava/util/ArrayList;
    //   7485: aload 14
    //   7487: getfield 449	org/vidogram/messenger/MessageObject:messageOwner	Lorg/vidogram/tgnet/TLRPC$Message;
    //   7490: getfield 1739	org/vidogram/tgnet/TLRPC$Message:fwd_msg_id	I
    //   7493: invokestatic 770	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   7496: invokevirtual 710	java/util/ArrayList:add	(Ljava/lang/Object;)Z
    //   7499: pop
    //   7500: goto -42 -> 7458
    //   7503: iload 18
    //   7505: bipush 9
    //   7507: if_icmpne -7500 -> 7
    //   7510: new 1741	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult
    //   7513: dup
    //   7514: invokespecial 1742	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:<init>	()V
    //   7517: astore_1
    //   7518: aload_1
    //   7519: aload 28
    //   7521: putfield 1743	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   7524: aload_1
    //   7525: aload 7
    //   7527: getfield 1102	org/vidogram/tgnet/TLRPC$Message:random_id	J
    //   7530: putfield 1744	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:random_id	J
    //   7533: aload 11
    //   7535: ifnull +22 -> 7557
    //   7538: aload_1
    //   7539: aload_1
    //   7540: getfield 1745	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   7543: iconst_1
    //   7544: ior
    //   7545: putfield 1745	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:flags	I
    //   7548: aload_1
    //   7549: aload 11
    //   7551: invokevirtual 1072	org/vidogram/messenger/MessageObject:getId	()I
    //   7554: putfield 1746	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:reply_to_msg_id	I
    //   7557: aload 7
    //   7559: getfield 1316	org/vidogram/tgnet/TLRPC$Message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
    //   7562: instanceof 1433
    //   7565: ifeq +44 -> 7609
    //   7568: aload_1
    //   7569: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   7572: ldc_w 1435
    //   7575: iconst_0
    //   7576: invokevirtual 1441	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   7579: new 456	java/lang/StringBuilder
    //   7582: dup
    //   7583: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   7586: ldc_w 1443
    //   7589: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   7592: lload 8
    //   7594: invokevirtual 483	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   7597: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   7600: iconst_0
    //   7601: invokeinterface 1449 3 0
    //   7606: putfield 1747	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:silent	Z
    //   7609: aload_1
    //   7610: aload 17
    //   7612: ldc_w 1147
    //   7615: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7618: checkcast 340	java/lang/String
    //   7621: invokestatic 1750	org/vidogram/messenger/Utilities:parseLong	(Ljava/lang/String;)Ljava/lang/Long;
    //   7624: invokevirtual 1753	java/lang/Long:longValue	()J
    //   7627: putfield 1755	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:query_id	J
    //   7630: aload_1
    //   7631: aload 17
    //   7633: ldc_w 1756
    //   7636: invokevirtual 1004	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   7639: checkcast 340	java/lang/String
    //   7642: putfield 1758	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:id	Ljava/lang/String;
    //   7645: aload 14
    //   7647: ifnonnull +14 -> 7661
    //   7650: aload_1
    //   7651: iconst_1
    //   7652: putfield 1759	org/vidogram/tgnet/TLRPC$TL_messages_sendInlineBotResult:clear_draft	Z
    //   7655: lload 8
    //   7657: iconst_0
    //   7658: invokestatic 1467	org/vidogram/messenger/query/DraftQuery:cleanDraft	(JZ)V
    //   7661: aload_0
    //   7662: aload_1
    //   7663: aload 16
    //   7665: aconst_null
    //   7666: invokespecial 1426	org/vidogram/messenger/SendMessagesHelper:performSendMessageRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/messenger/MessageObject;Ljava/lang/String;)V
    //   7669: return
    //   7670: astore_1
    //   7671: aconst_null
    //   7672: astore_3
    //   7673: aconst_null
    //   7674: astore_2
    //   7675: goto -7070 -> 605
    //   7678: astore_1
    //   7679: aconst_null
    //   7680: astore_3
    //   7681: aload 7
    //   7683: astore_2
    //   7684: goto -7079 -> 605
    //   7687: astore_1
    //   7688: aconst_null
    //   7689: astore_3
    //   7690: aload 24
    //   7692: astore_2
    //   7693: goto -7088 -> 605
    //   7696: aconst_null
    //   7697: astore_1
    //   7698: aload 12
    //   7700: astore_2
    //   7701: goto -3814 -> 3887
    //   7704: iconst_0
    //   7705: istore 19
    //   7707: goto -4406 -> 3301
    //   7710: iconst_0
    //   7711: istore 19
    //   7713: goto -4513 -> 3200
    //   7716: goto -6573 -> 1143
    //   7719: iconst_m1
    //   7720: istore 18
    //   7722: aload 24
    //   7724: astore 7
    //   7726: goto -6583 -> 1143
    //   7729: iconst_m1
    //   7730: istore 18
    //   7732: aload 24
    //   7734: astore 7
    //   7736: goto -6593 -> 1143
    //   7739: aload_3
    //   7740: astore 24
    //   7742: goto -7509 -> 233
    //   7745: iconst_m1
    //   7746: istore 18
    //   7748: goto -7057 -> 691
    //   7751: aconst_null
    //   7752: astore 26
    //   7754: goto -7551 -> 203
    //   7757: goto -7554 -> 203
    //   7760: aconst_null
    //   7761: astore 27
    //   7763: goto -7726 -> 37
    //   7766: iconst_0
    //   7767: istore 18
    //   7769: goto -7078 -> 691
    //   7772: iconst_1
    //   7773: istore 18
    //   7775: goto -6632 -> 1143
    //   7778: iconst_2
    //   7779: istore 18
    //   7781: goto -6271 -> 1510
    //   7784: bipush 6
    //   7786: istore 18
    //   7788: goto -6645 -> 1143
    //   7791: iconst_3
    //   7792: istore 18
    //   7794: goto -5719 -> 2075
    //   7797: bipush 7
    //   7799: istore 18
    //   7801: goto -5726 -> 2075
    //   7804: iload 20
    //   7806: iconst_1
    //   7807: iadd
    //   7808: istore 20
    //   7810: goto -5652 -> 2158
    //   7813: iload 18
    //   7815: ifeq -4862 -> 2953
    //   7818: iload 18
    //   7820: bipush 9
    //   7822: if_icmpne +45 -> 7867
    //   7825: aload_1
    //   7826: ifnull +41 -> 7867
    //   7829: aload 26
    //   7831: ifnull +36 -> 7867
    //   7834: goto -4881 -> 2953
    //   7837: aconst_null
    //   7838: astore_3
    //   7839: goto -5134 -> 2705
    //   7842: iload 19
    //   7844: iconst_1
    //   7845: iadd
    //   7846: istore 19
    //   7848: goto -4700 -> 3148
    //   7851: astore_1
    //   7852: aload 7
    //   7854: astore_2
    //   7855: aload 16
    //   7857: astore_3
    //   7858: goto -7253 -> 605
    //   7861: iconst_0
    //   7862: istore 23
    //   7864: goto -4475 -> 3389
    //   7867: iload 18
    //   7869: iconst_1
    //   7870: if_icmplt +9 -> 7879
    //   7873: iload 18
    //   7875: iconst_3
    //   7876: if_icmple -4092 -> 3784
    //   7879: iload 18
    //   7881: iconst_5
    //   7882: if_icmplt +10 -> 7892
    //   7885: iload 18
    //   7887: bipush 8
    //   7889: if_icmple -4105 -> 3784
    //   7892: iload 18
    //   7894: bipush 9
    //   7896: if_icmpne -637 -> 7259
    //   7899: aload 26
    //   7901: ifnull -642 -> 7259
    //   7904: goto -4120 -> 3784
    //   7907: iload 18
    //   7909: iconst_2
    //   7910: if_icmpeq -3954 -> 3956
    //   7913: iload 18
    //   7915: bipush 9
    //   7917: if_icmpne -3677 -> 4240
    //   7920: aload 24
    //   7922: ifnull -3682 -> 4240
    //   7925: goto -3969 -> 3956
    //   7928: ldc_w 743
    //   7931: astore_2
    //   7932: goto -3944 -> 3988
    //   7935: ldc_w 743
    //   7938: astore_1
    //   7939: goto -3736 -> 4203
    //   7942: ldc_w 743
    //   7945: astore_2
    //   7946: goto -3657 -> 4289
    //   7949: ldc_w 743
    //   7952: astore_1
    //   7953: goto -3544 -> 4409
    //   7956: iload 18
    //   7958: bipush 7
    //   7960: if_icmpeq -3466 -> 4494
    //   7963: iload 18
    //   7965: bipush 9
    //   7967: if_icmpne -3166 -> 4801
    //   7970: goto -3476 -> 4494
    //   7973: aconst_null
    //   7974: astore_1
    //   7975: goto -3385 -> 4590
    //   7978: ldc_w 743
    //   7981: astore 4
    //   7983: goto -3360 -> 4623
    //   7986: ldc_w 743
    //   7989: astore_1
    //   7990: goto -3202 -> 4788
    //   7993: ldc_w 743
    //   7996: astore_1
    //   7997: goto -3136 -> 4861
    //   8000: ldc_w 743
    //   8003: astore_1
    //   8004: goto -3072 -> 4932
    //   8007: iload 18
    //   8009: iconst_2
    //   8010: if_icmpeq -2405 -> 5605
    //   8013: iload 18
    //   8015: bipush 9
    //   8017: if_icmpne -2043 -> 5974
    //   8020: aload 24
    //   8022: ifnull -2048 -> 5974
    //   8025: goto -2420 -> 5605
    //   8028: ldc_w 743
    //   8031: astore_1
    //   8032: goto -2355 -> 5677
    //   8035: ldc_w 743
    //   8038: astore_1
    //   8039: goto -1964 -> 6075
    //   8042: iload 18
    //   8044: iconst_1
    //   8045: iadd
    //   8046: istore 18
    //   8048: goto -1943 -> 6105
    //   8051: iload 18
    //   8053: bipush 7
    //   8055: if_icmpeq -1547 -> 6508
    //   8058: iload 18
    //   8060: bipush 9
    //   8062: if_icmpne -1033 -> 7029
    //   8065: aload 6
    //   8067: ifnull -1038 -> 7029
    //   8070: goto -1562 -> 6508
    //   8073: ldc_w 743
    //   8076: astore_1
    //   8077: goto -1336 -> 6741
    //   8080: ldc_w 743
    //   8083: astore_1
    //   8084: goto -968 -> 7116
    //
    // Exception table:
    //   from	to	target	type
    //   233	252	599	java/lang/Exception
    //   257	268	599	java/lang/Exception
    //   273	305	599	java/lang/Exception
    //   305	319	599	java/lang/Exception
    //   319	326	599	java/lang/Exception
    //   331	339	599	java/lang/Exception
    //   339	358	599	java/lang/Exception
    //   363	383	599	java/lang/Exception
    //   383	398	599	java/lang/Exception
    //   402	429	599	java/lang/Exception
    //   429	450	599	java/lang/Exception
    //   460	499	599	java/lang/Exception
    //   499	509	599	java/lang/Exception
    //   519	539	599	java/lang/Exception
    //   550	598	599	java/lang/Exception
    //   2530	2552	599	java/lang/Exception
    //   2555	2576	599	java/lang/Exception
    //   2579	2585	599	java/lang/Exception
    //   2588	2601	599	java/lang/Exception
    //   2604	2627	599	java/lang/Exception
    //   2627	2668	599	java/lang/Exception
    //   2673	2680	599	java/lang/Exception
    //   2683	2705	599	java/lang/Exception
    //   2711	2736	599	java/lang/Exception
    //   2736	2755	599	java/lang/Exception
    //   3015	3025	599	java/lang/Exception
    //   3030	3042	599	java/lang/Exception
    //   3046	3055	599	java/lang/Exception
    //   3056	3069	599	java/lang/Exception
    //   3072	3108	599	java/lang/Exception
    //   3108	3145	599	java/lang/Exception
    //   3148	3200	599	java/lang/Exception
    //   3200	3217	599	java/lang/Exception
    //   3222	3235	599	java/lang/Exception
    //   3238	3246	599	java/lang/Exception
    //   3249	3301	599	java/lang/Exception
    //   3301	3318	599	java/lang/Exception
    //   1001	1009	1298	java/lang/Exception
    //   1013	1020	1298	java/lang/Exception
    //   1037	1045	1298	java/lang/Exception
    //   1049	1057	1298	java/lang/Exception
    //   1061	1070	1298	java/lang/Exception
    //   1074	1084	1298	java/lang/Exception
    //   1093	1105	1298	java/lang/Exception
    //   1114	1125	1298	java/lang/Exception
    //   1133	1139	1298	java/lang/Exception
    //   1147	1155	1298	java/lang/Exception
    //   1159	1167	1298	java/lang/Exception
    //   1171	1176	1298	java/lang/Exception
    //   1180	1187	1298	java/lang/Exception
    //   1191	1198	1298	java/lang/Exception
    //   1202	1208	1298	java/lang/Exception
    //   1222	1233	1298	java/lang/Exception
    //   1237	1241	1298	java/lang/Exception
    //   1269	1281	1298	java/lang/Exception
    //   1285	1295	1298	java/lang/Exception
    //   1335	1341	1298	java/lang/Exception
    //   1345	1353	1298	java/lang/Exception
    //   1362	1372	1298	java/lang/Exception
    //   1418	1430	1298	java/lang/Exception
    //   1434	1441	1298	java/lang/Exception
    //   1445	1452	1298	java/lang/Exception
    //   1456	1462	1298	java/lang/Exception
    //   1466	1473	1298	java/lang/Exception
    //   1477	1486	1298	java/lang/Exception
    //   1495	1506	1298	java/lang/Exception
    //   1514	1522	1298	java/lang/Exception
    //   1531	1539	1298	java/lang/Exception
    //   1543	1554	1298	java/lang/Exception
    //   1558	1565	1298	java/lang/Exception
    //   1592	1626	1298	java/lang/Exception
    //   1738	1750	1298	java/lang/Exception
    //   1754	1767	1298	java/lang/Exception
    //   1771	1784	1298	java/lang/Exception
    //   1788	1801	1298	java/lang/Exception
    //   1805	1818	1298	java/lang/Exception
    //   1822	1833	1298	java/lang/Exception
    //   1837	1848	1298	java/lang/Exception
    //   1852	1860	1298	java/lang/Exception
    //   1864	1875	1298	java/lang/Exception
    //   1879	1890	1298	java/lang/Exception
    //   1894	1902	1298	java/lang/Exception
    //   1906	1914	1298	java/lang/Exception
    //   1923	1933	1298	java/lang/Exception
    //   1980	1992	1298	java/lang/Exception
    //   1996	2003	1298	java/lang/Exception
    //   2007	2015	1298	java/lang/Exception
    //   2019	2026	1298	java/lang/Exception
    //   2030	2037	1298	java/lang/Exception
    //   2041	2051	1298	java/lang/Exception
    //   2060	2071	1298	java/lang/Exception
    //   2084	2092	1298	java/lang/Exception
    //   2101	2109	1298	java/lang/Exception
    //   2113	2121	1298	java/lang/Exception
    //   2125	2138	1298	java/lang/Exception
    //   2147	2155	1298	java/lang/Exception
    //   2162	2175	1298	java/lang/Exception
    //   2179	2194	1298	java/lang/Exception
    //   2198	2206	1298	java/lang/Exception
    //   2210	2221	1298	java/lang/Exception
    //   2225	2234	1298	java/lang/Exception
    //   2238	2249	1298	java/lang/Exception
    //   2253	2263	1298	java/lang/Exception
    //   2267	2275	1298	java/lang/Exception
    //   2279	2290	1298	java/lang/Exception
    //   2294	2304	1298	java/lang/Exception
    //   2308	2316	1298	java/lang/Exception
    //   2320	2332	1298	java/lang/Exception
    //   2336	2346	1298	java/lang/Exception
    //   2373	2381	1298	java/lang/Exception
    //   2393	2401	1298	java/lang/Exception
    //   2412	2422	1298	java/lang/Exception
    //   2429	2436	1298	java/lang/Exception
    //   2443	2456	1298	java/lang/Exception
    //   2463	2475	1298	java/lang/Exception
    //   2482	2494	1298	java/lang/Exception
    //   2501	2509	1298	java/lang/Exception
    //   2513	2527	1298	java/lang/Exception
    //   208	215	7670	java/lang/Exception
    //   983	992	7670	java/lang/Exception
    //   1247	1256	7670	java/lang/Exception
    //   1322	1331	7670	java/lang/Exception
    //   1384	1393	7670	java/lang/Exception
    //   1405	1414	7670	java/lang/Exception
    //   1568	1577	7670	java/lang/Exception
    //   1634	1643	7670	java/lang/Exception
    //   1725	1734	7670	java/lang/Exception
    //   1945	1954	7670	java/lang/Exception
    //   1967	1976	7670	java/lang/Exception
    //   2349	2358	7670	java/lang/Exception
    //   215	222	7678	java/lang/Exception
    //   666	688	7678	java/lang/Exception
    //   696	707	7678	java/lang/Exception
    //   717	723	7678	java/lang/Exception
    //   726	741	7678	java/lang/Exception
    //   747	768	7678	java/lang/Exception
    //   774	783	7678	java/lang/Exception
    //   788	801	7678	java/lang/Exception
    //   807	878	7678	java/lang/Exception
    //   885	925	7678	java/lang/Exception
    //   925	938	7678	java/lang/Exception
    //   945	967	7678	java/lang/Exception
    //   1643	1684	7687	java/lang/Exception
    //   1689	1699	7687	java/lang/Exception
    //   2755	2779	7851	java/lang/Exception
    //   2784	2801	7851	java/lang/Exception
    //   2801	2876	7851	java/lang/Exception
    //   2881	2950	7851	java/lang/Exception
    //   2962	2979	7851	java/lang/Exception
    //   2982	3006	7851	java/lang/Exception
    //   3332	3367	7851	java/lang/Exception
    //   3368	3381	7851	java/lang/Exception
    //   3389	3447	7851	java/lang/Exception
    //   3447	3462	7851	java/lang/Exception
    //   3467	3486	7851	java/lang/Exception
    //   3491	3496	7851	java/lang/Exception
    //   3501	3526	7851	java/lang/Exception
    //   3526	3534	7851	java/lang/Exception
    //   3539	3545	7851	java/lang/Exception
    //   3546	3563	7851	java/lang/Exception
    //   3568	3594	7851	java/lang/Exception
    //   3599	3635	7851	java/lang/Exception
    //   3640	3678	7851	java/lang/Exception
    //   3678	3692	7851	java/lang/Exception
    //   3697	3740	7851	java/lang/Exception
    //   3740	3758	7851	java/lang/Exception
    //   3763	3769	7851	java/lang/Exception
    //   3770	3781	7851	java/lang/Exception
    //   3798	3845	7851	java/lang/Exception
    //   3845	3884	7851	java/lang/Exception
    //   3891	3909	7851	java/lang/Exception
    //   3912	3936	7851	java/lang/Exception
    //   3945	3953	7851	java/lang/Exception
    //   3956	3988	7851	java/lang/Exception
    //   3988	3993	7851	java/lang/Exception
    //   3998	4010	7851	java/lang/Exception
    //   4014	4033	7851	java/lang/Exception
    //   4043	4061	7851	java/lang/Exception
    //   4070	4080	7851	java/lang/Exception
    //   4080	4106	7851	java/lang/Exception
    //   4111	4136	7851	java/lang/Exception
    //   4139	4167	7851	java/lang/Exception
    //   4170	4203	7851	java/lang/Exception
    //   4203	4232	7851	java/lang/Exception
    //   4246	4275	7851	java/lang/Exception
    //   4275	4289	7851	java/lang/Exception
    //   4289	4362	7851	java/lang/Exception
    //   4365	4373	7851	java/lang/Exception
    //   4376	4409	7851	java/lang/Exception
    //   4409	4438	7851	java/lang/Exception
    //   4453	4488	7851	java/lang/Exception
    //   4494	4504	7851	java/lang/Exception
    //   4514	4533	7851	java/lang/Exception
    //   4538	4587	7851	java/lang/Exception
    //   4590	4623	7851	java/lang/Exception
    //   4623	4629	7851	java/lang/Exception
    //   4640	4673	7851	java/lang/Exception
    //   4673	4717	7851	java/lang/Exception
    //   4720	4728	7851	java/lang/Exception
    //   4731	4788	7851	java/lang/Exception
    //   4788	4793	7851	java/lang/Exception
    //   4808	4861	7851	java/lang/Exception
    //   4861	4893	7851	java/lang/Exception
    //   4899	4932	7851	java/lang/Exception
    //   4932	4961	7851	java/lang/Exception
    //   4969	4996	7851	java/lang/Exception
    //   5000	5006	7851	java/lang/Exception
    //   5014	5020	7851	java/lang/Exception
    //   5029	5037	7851	java/lang/Exception
    //   5038	5104	7851	java/lang/Exception
    //   5104	5118	7851	java/lang/Exception
    //   5123	5142	7851	java/lang/Exception
    //   5148	5153	7851	java/lang/Exception
    //   5164	5179	7851	java/lang/Exception
    //   5180	5188	7851	java/lang/Exception
    //   5195	5210	7851	java/lang/Exception
    //   5211	5219	7851	java/lang/Exception
    //   5227	5235	7851	java/lang/Exception
    //   5243	5253	7851	java/lang/Exception
    //   5257	5262	7851	java/lang/Exception
    //   5263	5272	7851	java/lang/Exception
    //   5280	5295	7851	java/lang/Exception
    //   5296	5304	7851	java/lang/Exception
    //   5305	5322	7851	java/lang/Exception
    //   5327	5353	7851	java/lang/Exception
    //   5358	5394	7851	java/lang/Exception
    //   5394	5406	7851	java/lang/Exception
    //   5411	5449	7851	java/lang/Exception
    //   5449	5465	7851	java/lang/Exception
    //   5471	5533	7851	java/lang/Exception
    //   5533	5579	7851	java/lang/Exception
    //   5584	5590	7851	java/lang/Exception
    //   5591	5602	7851	java/lang/Exception
    //   5605	5677	7851	java/lang/Exception
    //   5677	5706	7851	java/lang/Exception
    //   5706	5810	7851	java/lang/Exception
    //   5815	5840	7851	java/lang/Exception
    //   5840	5845	7851	java/lang/Exception
    //   5848	5861	7851	java/lang/Exception
    //   5864	5892	7851	java/lang/Exception
    //   5895	5971	7851	java/lang/Exception
    //   5980	6056	7851	java/lang/Exception
    //   6056	6075	7851	java/lang/Exception
    //   6075	6102	7851	java/lang/Exception
    //   6105	6172	7851	java/lang/Exception
    //   6172	6264	7851	java/lang/Exception
    //   6267	6280	7851	java/lang/Exception
    //   6283	6331	7851	java/lang/Exception
    //   6334	6347	7851	java/lang/Exception
    //   6350	6418	7851	java/lang/Exception
    //   6428	6505	7851	java/lang/Exception
    //   6508	6652	7851	java/lang/Exception
    //   6652	6670	7851	java/lang/Exception
    //   6673	6688	7851	java/lang/Exception
    //   6691	6741	7851	java/lang/Exception
    //   6741	6813	7851	java/lang/Exception
    //   6813	6882	7851	java/lang/Exception
    //   6887	6912	7851	java/lang/Exception
    //   6912	6923	7851	java/lang/Exception
    //   6926	6955	7851	java/lang/Exception
    //   6958	7026	7851	java/lang/Exception
    //   7036	7116	7851	java/lang/Exception
    //   7116	7189	7851	java/lang/Exception
    //   7189	7224	7851	java/lang/Exception
    //   7227	7256	7851	java/lang/Exception
    //   7265	7348	7851	java/lang/Exception
    //   7352	7363	7851	java/lang/Exception
    //   7363	7418	7851	java/lang/Exception
    //   7418	7458	7851	java/lang/Exception
    //   7458	7466	7851	java/lang/Exception
    //   7467	7478	7851	java/lang/Exception
    //   7481	7500	7851	java/lang/Exception
    //   7510	7533	7851	java/lang/Exception
    //   7538	7557	7851	java/lang/Exception
    //   7557	7609	7851	java/lang/Exception
    //   7609	7645	7851	java/lang/Exception
    //   7650	7661	7851	java/lang/Exception
    //   7661	7669	7851	java/lang/Exception
  }

  private void updateMediaPaths(MessageObject paramMessageObject, TLRPC.Message paramMessage, String paramString, boolean paramBoolean)
  {
    TLRPC.Message localMessage = paramMessageObject.messageOwner;
    if (paramMessage == null)
      return;
    int i;
    label198: label342: Object localObject2;
    label269: label272: String str;
    Object localObject3;
    if (((paramMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (paramMessage.media.photo != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaPhoto)) && (localMessage.media.photo != null))
    {
      MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.photo, 0);
      if ((localMessage.media.photo.sizes.size() == 1) && ((((TLRPC.PhotoSize)localMessage.media.photo.sizes.get(0)).location instanceof TLRPC.TL_fileLocationUnavailable)))
      {
        localMessage.media.photo.sizes = paramMessage.media.photo.sizes;
        paramMessage.message = localMessage.message;
        paramMessage.attachPath = localMessage.attachPath;
        localMessage.media.photo.id = paramMessage.media.photo.id;
        localMessage.media.photo.access_hash = paramMessage.media.photo.access_hash;
        return;
      }
      i = 0;
      if (i < paramMessage.media.photo.sizes.size())
      {
        paramString = (TLRPC.PhotoSize)paramMessage.media.photo.sizes.get(i);
        if ((paramString != null) && (paramString.location != null) && (!(paramString instanceof TLRPC.TL_photoSizeEmpty)) && (paramString.type != null))
          break label269;
      }
      do
      {
        i += 1;
        break label198;
        break;
        int j = 0;
        if (j < localMessage.media.photo.sizes.size())
        {
          localObject1 = (TLRPC.PhotoSize)localMessage.media.photo.sizes.get(j);
          if ((localObject1 != null) && (((TLRPC.PhotoSize)localObject1).location != null) && (((TLRPC.PhotoSize)localObject1).type != null))
            break label342;
        }
        do
        {
          j += 1;
          break label272;
          break;
        }
        while (((((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (!paramString.type.equals(((TLRPC.PhotoSize)localObject1).type))) && ((paramString.w != ((TLRPC.PhotoSize)localObject1).w) || (paramString.h != ((TLRPC.PhotoSize)localObject1).h)));
        localObject2 = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
        str = paramString.location.volume_id + "_" + paramString.location.local_id;
      }
      while (((String)localObject2).equals(str));
      localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject2 + ".jpg");
      if ((paramMessage.media.photo.sizes.size() == 1) || (paramString.w > 90) || (paramString.h > 90));
      for (paramMessageObject = FileLoader.getPathToAttach(paramString); ; paramMessageObject = new File(FileLoader.getInstance().getDirectory(4), str + ".jpg"))
      {
        ((File)localObject3).renameTo(paramMessageObject);
        ImageLoader.getInstance().replaceImageInCache((String)localObject2, str, paramString.location, paramBoolean);
        ((TLRPC.PhotoSize)localObject1).location = paramString.location;
        ((TLRPC.PhotoSize)localObject1).size = paramString.size;
        break;
      }
    }
    if (((paramMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (paramMessage.media.document != null) && ((localMessage.media instanceof TLRPC.TL_messageMediaDocument)) && (localMessage.media.document != null))
      if (MessageObject.isVideoMessage(paramMessage))
      {
        MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 2);
        paramMessage.attachPath = localMessage.attachPath;
        localObject1 = localMessage.media.document.thumb;
        localObject2 = paramMessage.media.document.thumb;
        if ((localObject1 == null) || (((TLRPC.PhotoSize)localObject1).location == null) || (((TLRPC.PhotoSize)localObject1).location.volume_id != -2147483648L) || (localObject2 == null) || (((TLRPC.PhotoSize)localObject2).location == null) || ((localObject2 instanceof TLRPC.TL_photoSizeEmpty)) || ((localObject1 instanceof TLRPC.TL_photoSizeEmpty)))
          break label1251;
        str = ((TLRPC.PhotoSize)localObject1).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject1).location.local_id;
        localObject3 = ((TLRPC.PhotoSize)localObject2).location.volume_id + "_" + ((TLRPC.PhotoSize)localObject2).location.local_id;
        if (!str.equals(localObject3))
        {
          new File(FileLoader.getInstance().getDirectory(4), str + ".jpg").renameTo(new File(FileLoader.getInstance().getDirectory(4), (String)localObject3 + ".jpg"));
          ImageLoader.getInstance().replaceImageInCache(str, (String)localObject3, ((TLRPC.PhotoSize)localObject2).location, paramBoolean);
          ((TLRPC.PhotoSize)localObject1).location = ((TLRPC.PhotoSize)localObject2).location;
          ((TLRPC.PhotoSize)localObject1).size = ((TLRPC.PhotoSize)localObject2).size;
        }
        label1000: localMessage.media.document.dc_id = paramMessage.media.document.dc_id;
        localMessage.media.document.id = paramMessage.media.document.id;
        localMessage.media.document.access_hash = paramMessage.media.document.access_hash;
        i = 0;
        label1066: if (i >= localMessage.media.document.attributes.size())
          break label1739;
        localObject1 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
        if (!(localObject1 instanceof TLRPC.TL_documentAttributeAudio))
          break label1332;
      }
    label1332: label1739: for (Object localObject1 = ((TLRPC.DocumentAttribute)localObject1).waveform; ; localObject1 = null)
    {
      localMessage.media.document.attributes = paramMessage.media.document.attributes;
      if (localObject1 != null)
      {
        i = 0;
        while (true)
          if (i < localMessage.media.document.attributes.size())
          {
            localObject2 = (TLRPC.DocumentAttribute)localMessage.media.document.attributes.get(i);
            if ((localObject2 instanceof TLRPC.TL_documentAttributeAudio))
            {
              ((TLRPC.DocumentAttribute)localObject2).waveform = ((B)localObject1);
              ((TLRPC.DocumentAttribute)localObject2).flags |= 4;
            }
            i += 1;
            continue;
            if (MessageObject.isVoiceMessage(paramMessage))
              break;
            MessagesStorage.getInstance().putSentFile(paramString, paramMessage.media.document, 1);
            break;
            label1251: if ((localObject1 != null) && (MessageObject.isStickerMessage(paramMessage)) && (((TLRPC.PhotoSize)localObject1).location != null))
            {
              ((TLRPC.PhotoSize)localObject2).location = ((TLRPC.PhotoSize)localObject1).location;
              break label1000;
            }
            if (((localObject1 == null) || (!(((TLRPC.PhotoSize)localObject1).location instanceof TLRPC.TL_fileLocationUnavailable))) && (!(localObject1 instanceof TLRPC.TL_photoSizeEmpty)))
              break label1000;
            localMessage.media.document.thumb = paramMessage.media.document.thumb;
            break label1000;
            i += 1;
            break label1066;
          }
      }
      localMessage.media.document.size = paramMessage.media.document.size;
      localMessage.media.document.mime_type = paramMessage.media.document.mime_type;
      if (((paramMessage.flags & 0x4) == 0) && (MessageObject.isOut(paramMessage)))
      {
        if (!MessageObject.isNewGifDocument(paramMessage.media.document))
          break label1510;
        StickersQuery.addRecentGif(paramMessage.media.document, paramMessage.date);
      }
      while (true)
        if ((localMessage.attachPath != null) && (localMessage.attachPath.startsWith(FileLoader.getInstance().getDirectory(4).getAbsolutePath())))
        {
          localObject1 = new File(localMessage.attachPath);
          localObject2 = FileLoader.getPathToAttach(paramMessage.media.document);
          if (!((File)localObject1).renameTo((File)localObject2))
          {
            paramMessage.attachPath = localMessage.attachPath;
            paramMessage.message = localMessage.message;
            return;
            label1510: if (!MessageObject.isStickerDocument(paramMessage.media.document))
              continue;
            StickersQuery.addRecentSticker(0, paramMessage.media.document, paramMessage.date);
            continue;
          }
          if (MessageObject.isVideoMessage(paramMessage))
          {
            paramMessageObject.attachPathExists = true;
            return;
          }
          paramMessageObject.mediaExists = paramMessageObject.attachPathExists;
          paramMessageObject.attachPathExists = false;
          localMessage.attachPath = "";
          if ((paramString == null) || (!paramString.startsWith("http")))
            break;
          MessagesStorage.getInstance().addRecentLocalFile(paramString, ((File)localObject2).toString(), localMessage.media.document);
          return;
        }
      paramMessage.attachPath = localMessage.attachPath;
      paramMessage.message = localMessage.message;
      return;
      if (((paramMessage.media instanceof TLRPC.TL_messageMediaContact)) && ((localMessage.media instanceof TLRPC.TL_messageMediaContact)))
      {
        localMessage.media = paramMessage.media;
        return;
      }
      if ((paramMessage.media instanceof TLRPC.TL_messageMediaWebPage))
      {
        localMessage.media = paramMessage.media;
        return;
      }
      if (!(paramMessage.media instanceof TLRPC.TL_messageMediaGame))
        break;
      localMessage.media = paramMessage.media;
      if ((!(localMessage.media instanceof TLRPC.TL_messageMediaGame)) || (TextUtils.isEmpty(paramMessage.message)))
        break;
      localMessage.entities = paramMessage.entities;
      localMessage.message = paramMessage.message;
      return;
    }
  }

  public void cancelSendingMessage(MessageObject paramMessageObject)
  {
    Iterator localIterator = this.delayedMessages.entrySet().iterator();
    boolean bool = false;
    Object localObject = null;
    int i;
    if (localIterator.hasNext())
    {
      Map.Entry localEntry = (Map.Entry)localIterator.next();
      ArrayList localArrayList = (ArrayList)localEntry.getValue();
      i = 0;
      label55: if (i >= localArrayList.size())
        break label234;
      DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(i);
      if (localDelayedMessage.obj.getId() == paramMessageObject.getId())
      {
        localArrayList.remove(i);
        MediaController.getInstance().cancelVideoConvert(localDelayedMessage.obj);
        if (localArrayList.size() != 0)
          break label234;
        localObject = (String)localEntry.getKey();
        if (localDelayedMessage.sendEncryptedRequest == null)
          break label237;
        bool = true;
      }
    }
    label222: label234: label237: 
    while (true)
    {
      break;
      i += 1;
      break label55;
      if (localObject != null)
      {
        if (!((String)localObject).startsWith("http"))
          break label222;
        ImageLoader.getInstance().cancelLoadHttpFile((String)localObject);
      }
      while (true)
      {
        stopVideoService((String)localObject);
        localObject = new ArrayList();
        ((ArrayList)localObject).add(Integer.valueOf(paramMessageObject.getId()));
        MessagesController.getInstance().deleteMessages((ArrayList)localObject, null, null, paramMessageObject.messageOwner.to_id.channel_id, false);
        return;
        FileLoader.getInstance().cancelUploadFile((String)localObject, bool);
      }
      continue;
    }
  }

  public void checkUnsentMessages()
  {
    MessagesStorage.getInstance().getUnsentMessages(1000);
  }

  public void cleanup()
  {
    this.delayedMessages.clear();
    this.unsentMessages.clear();
    this.sendingMessages.clear();
    this.waitingForLocation.clear();
    this.waitingForCallback.clear();
    this.currentChatInfo = null;
    this.locationProvider.stop();
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    Object localObject2;
    Object localObject3;
    label99: int i;
    label143: label188: long l;
    if (paramInt == NotificationCenter.FileDidUpload)
    {
      localObject2 = (String)paramArrayOfObject[0];
      localObject3 = (TLRPC.InputFile)paramArrayOfObject[1];
      TLRPC.InputEncryptedFile localInputEncryptedFile = (TLRPC.InputEncryptedFile)paramArrayOfObject[2];
      ArrayList localArrayList = (ArrayList)this.delayedMessages.get(localObject2);
      if (localArrayList != null)
      {
        paramInt = 0;
        if (paramInt < localArrayList.size())
        {
          DelayedMessage localDelayedMessage = (DelayedMessage)localArrayList.get(paramInt);
          localObject1 = null;
          if ((localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendMedia))
          {
            localObject1 = ((TLRPC.TL_messages_sendMedia)localDelayedMessage.sendRequest).media;
            if ((localObject3 == null) || (localObject1 == null))
              break label426;
            if (localDelayedMessage.type != 0)
              break label188;
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            localArrayList.remove(paramInt);
            i = paramInt - 1;
          }
          while (true)
          {
            paramInt = i + 1;
            break;
            if (!(localDelayedMessage.sendRequest instanceof TLRPC.TL_messages_sendBroadcast))
              break label99;
            localObject1 = ((TLRPC.TL_messages_sendBroadcast)localDelayedMessage.sendRequest).media;
            break label99;
            if (localDelayedMessage.type == 1)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type == 2)
            {
              if (((TLRPC.InputMedia)localObject1).file == null)
              {
                ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
                if ((((TLRPC.InputMedia)localObject1).thumb == null) && (localDelayedMessage.location != null))
                {
                  performSendDelayedMessage(localDelayedMessage);
                  break label143;
                }
                performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
                break label143;
              }
              ((TLRPC.InputMedia)localObject1).thumb = ((TLRPC.InputFile)localObject3);
              performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
              break label143;
            }
            if (localDelayedMessage.type != 3)
              break label143;
            ((TLRPC.InputMedia)localObject1).file = ((TLRPC.InputFile)localObject3);
            performSendMessageRequest(localDelayedMessage.sendRequest, localDelayedMessage.obj, localDelayedMessage.originalPath);
            break label143;
            label426: i = paramInt;
            if (localInputEncryptedFile == null)
              continue;
            i = paramInt;
            if (localDelayedMessage.sendEncryptedRequest == null)
              continue;
            if (((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaVideo)) || ((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaPhoto)) || ((localDelayedMessage.sendEncryptedRequest.media instanceof TLRPC.TL_decryptedMessageMediaDocument)))
            {
              l = ((Long)paramArrayOfObject[5]).longValue();
              localDelayedMessage.sendEncryptedRequest.media.size = (int)l;
            }
            localDelayedMessage.sendEncryptedRequest.media.key = ((byte[])(byte[])paramArrayOfObject[3]);
            localDelayedMessage.sendEncryptedRequest.media.iv = ((byte[])(byte[])paramArrayOfObject[4]);
            SecretChatHelper.getInstance().performSendEncryptedRequest(localDelayedMessage.sendEncryptedRequest, localDelayedMessage.obj.messageOwner, localDelayedMessage.encryptedChat, localInputEncryptedFile, localDelayedMessage.originalPath, localDelayedMessage.obj);
            localArrayList.remove(paramInt);
            i = paramInt - 1;
          }
        }
        if (localArrayList.isEmpty())
          this.delayedMessages.remove(localObject2);
      }
    }
    label998: 
    do
    {
      do
        while (true)
        {
          return;
          boolean bool;
          if (paramInt == NotificationCenter.FileDidFailUpload)
          {
            localObject1 = (String)paramArrayOfObject[0];
            bool = ((Boolean)paramArrayOfObject[1]).booleanValue();
            paramArrayOfObject = (ArrayList)this.delayedMessages.get(localObject1);
            if (paramArrayOfObject == null)
              continue;
            for (paramInt = 0; paramInt < paramArrayOfObject.size(); paramInt = i + 1)
            {
              localObject2 = (DelayedMessage)paramArrayOfObject.get(paramInt);
              if ((!bool) || (((DelayedMessage)localObject2).sendEncryptedRequest == null))
              {
                i = paramInt;
                if (bool)
                  continue;
                i = paramInt;
                if (((DelayedMessage)localObject2).sendRequest == null)
                  continue;
              }
              MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
              ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
              paramArrayOfObject.remove(paramInt);
              i = paramInt - 1;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
              processSentMessage(((DelayedMessage)localObject2).obj.getId());
            }
            if (!paramArrayOfObject.isEmpty())
              continue;
            this.delayedMessages.remove(localObject1);
            return;
          }
          if (paramInt == NotificationCenter.FilePreparingStarted)
          {
            localObject1 = (MessageObject)paramArrayOfObject[0];
            if (((MessageObject)localObject1).getId() == 0)
              continue;
            paramArrayOfObject = (String)paramArrayOfObject[1];
            paramArrayOfObject = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
            if (paramArrayOfObject == null)
              continue;
            paramInt = 0;
            while (true)
            {
              if (paramInt < paramArrayOfObject.size())
              {
                localObject2 = (DelayedMessage)paramArrayOfObject.get(paramInt);
                if (((DelayedMessage)localObject2).obj == localObject1)
                {
                  ((DelayedMessage)localObject2).videoEditedInfo = null;
                  performSendDelayedMessage((DelayedMessage)localObject2);
                  paramArrayOfObject.remove(paramInt);
                }
              }
              else
              {
                if (!paramArrayOfObject.isEmpty())
                  break;
                this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                return;
              }
              paramInt += 1;
            }
          }
          if (paramInt == NotificationCenter.FileNewChunkAvailable)
          {
            localObject1 = (MessageObject)paramArrayOfObject[0];
            if (((MessageObject)localObject1).getId() == 0)
              continue;
            localObject2 = (String)paramArrayOfObject[1];
            l = ((Long)paramArrayOfObject[2]).longValue();
            if ((int)((MessageObject)localObject1).getDialogId() == 0)
            {
              bool = true;
              FileLoader.getInstance().checkUploadNewDataAvailable((String)localObject2, bool, l);
              if (l == 0L)
                continue;
              paramArrayOfObject = (ArrayList)this.delayedMessages.get(((MessageObject)localObject1).messageOwner.attachPath);
              if (paramArrayOfObject == null)
                continue;
              paramInt = 0;
            }
            while (true)
            {
              if (paramInt < paramArrayOfObject.size())
              {
                localObject2 = (DelayedMessage)paramArrayOfObject.get(paramInt);
                if (((DelayedMessage)localObject2).obj == localObject1)
                {
                  ((DelayedMessage)localObject2).obj.videoEditedInfo = null;
                  ((DelayedMessage)localObject2).obj.messageOwner.message = "-1";
                  ((DelayedMessage)localObject2).obj.messageOwner.media.document.size = (int)l;
                  localObject3 = new ArrayList();
                  ((ArrayList)localObject3).add(((DelayedMessage)localObject2).obj.messageOwner);
                  MessagesStorage.getInstance().putMessages((ArrayList)localObject3, false, true, false, 0);
                }
              }
              else
              {
                if (!paramArrayOfObject.isEmpty())
                  break;
                this.delayedMessages.remove(((MessageObject)localObject1).messageOwner.attachPath);
                return;
                bool = false;
                break label998;
              }
              paramInt += 1;
            }
          }
          if (paramInt == NotificationCenter.FilePreparingFailed)
          {
            localObject1 = (MessageObject)paramArrayOfObject[0];
            if (((MessageObject)localObject1).getId() == 0)
              continue;
            paramArrayOfObject = (String)paramArrayOfObject[1];
            stopVideoService(((MessageObject)localObject1).messageOwner.attachPath);
            localObject2 = (ArrayList)this.delayedMessages.get(paramArrayOfObject);
            if (localObject2 == null)
              continue;
            for (paramInt = 0; paramInt < ((ArrayList)localObject2).size(); paramInt = i + 1)
            {
              localObject3 = (DelayedMessage)((ArrayList)localObject2).get(paramInt);
              i = paramInt;
              if (((DelayedMessage)localObject3).obj != localObject1)
                continue;
              MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject3).obj.messageOwner);
              ((DelayedMessage)localObject3).obj.messageOwner.send_state = 2;
              ((ArrayList)localObject2).remove(paramInt);
              i = paramInt - 1;
              NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject3).obj.getId()) });
              processSentMessage(((DelayedMessage)localObject3).obj.getId());
            }
            if (!((ArrayList)localObject2).isEmpty())
              continue;
            this.delayedMessages.remove(paramArrayOfObject);
            return;
          }
          if (paramInt == NotificationCenter.httpFileDidLoaded)
          {
            paramArrayOfObject = (String)paramArrayOfObject[0];
            localObject1 = (ArrayList)this.delayedMessages.get(paramArrayOfObject);
            if (localObject1 == null)
              continue;
            paramInt = 0;
            if (paramInt < ((ArrayList)localObject1).size())
            {
              localObject2 = (DelayedMessage)((ArrayList)localObject1).get(paramInt);
              if (((DelayedMessage)localObject2).type == 0)
              {
                localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + "." + ImageLoader.getHttpUrlExtension(((DelayedMessage)localObject2).httpLocation, "file");
                localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
                Utilities.globalQueue.postRunnable(new Runnable((File)localObject3, (DelayedMessage)localObject2)
                {
                  public void run()
                  {
                    AndroidUtilities.runOnUIThread(new Runnable(SendMessagesHelper.getInstance().generatePhotoSizes(this.val$cacheFile.toString(), null))
                    {
                      public void run()
                      {
                        if (this.val$photo != null)
                        {
                          SendMessagesHelper.2.this.val$message.httpLocation = null;
                          SendMessagesHelper.2.this.val$message.obj.messageOwner.media.photo = this.val$photo;
                          SendMessagesHelper.2.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.2.this.val$cacheFile.toString();
                          SendMessagesHelper.2.this.val$message.location = ((TLRPC.PhotoSize)this.val$photo.sizes.get(this.val$photo.sizes.size() - 1)).location;
                          ArrayList localArrayList = new ArrayList();
                          localArrayList.add(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                          MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                          SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.2.this.val$message);
                          NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.2.this.val$message.obj });
                          return;
                        }
                        FileLog.e("can't load image " + SendMessagesHelper.2.this.val$message.httpLocation + " to file " + SendMessagesHelper.2.this.val$cacheFile.toString());
                        MessagesStorage.getInstance().markMessageAsSendError(SendMessagesHelper.2.this.val$message.obj.messageOwner);
                        SendMessagesHelper.2.this.val$message.obj.messageOwner.send_state = 2;
                        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(SendMessagesHelper.2.this.val$message.obj.getId()) });
                        SendMessagesHelper.this.processSentMessage(SendMessagesHelper.2.this.val$message.obj.getId());
                      }
                    });
                  }
                });
              }
              while (true)
              {
                paramInt += 1;
                break;
                if (((DelayedMessage)localObject2).type != 2)
                  continue;
                localObject3 = Utilities.MD5(((DelayedMessage)localObject2).httpLocation) + ".gif";
                localObject3 = new File(FileLoader.getInstance().getDirectory(4), (String)localObject3);
                Utilities.globalQueue.postRunnable(new Runnable((DelayedMessage)localObject2, (File)localObject3)
                {
                  public void run()
                  {
                    boolean bool = true;
                    if ((this.val$message.documentLocation.thumb.location instanceof TLRPC.TL_fileLocationUnavailable));
                    try
                    {
                      Bitmap localBitmap = ImageLoader.loadBitmap(this.val$cacheFile.getAbsolutePath(), null, 90.0F, 90.0F, true);
                      TLRPC.TL_document localTL_document;
                      if (localBitmap != null)
                      {
                        localTL_document = this.val$message.documentLocation;
                        if (this.val$message.sendEncryptedRequest == null)
                          break label136;
                      }
                      while (true)
                      {
                        localTL_document.thumb = ImageLoader.scaleAndSaveImage(localBitmap, 90.0F, 90.0F, 55, bool);
                        localBitmap.recycle();
                        if (this.val$message.documentLocation.thumb == null)
                        {
                          this.val$message.documentLocation.thumb = new TLRPC.TL_photoSizeEmpty();
                          this.val$message.documentLocation.thumb.type = "s";
                        }
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            SendMessagesHelper.3.this.val$message.httpLocation = null;
                            SendMessagesHelper.3.this.val$message.obj.messageOwner.attachPath = SendMessagesHelper.3.this.val$cacheFile.toString();
                            SendMessagesHelper.3.this.val$message.location = SendMessagesHelper.3.this.val$message.documentLocation.thumb.location;
                            ArrayList localArrayList = new ArrayList();
                            localArrayList.add(SendMessagesHelper.3.this.val$message.obj.messageOwner);
                            MessagesStorage.getInstance().putMessages(localArrayList, false, true, false, 0);
                            SendMessagesHelper.this.performSendDelayedMessage(SendMessagesHelper.3.this.val$message);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.updateMessageMedia, new Object[] { SendMessagesHelper.3.this.val$message.obj });
                          }
                        });
                        return;
                        label136: bool = false;
                      }
                    }
                    catch (Exception localException)
                    {
                      while (true)
                      {
                        this.val$message.documentLocation.thumb = null;
                        FileLog.e(localException);
                      }
                    }
                  }
                });
              }
            }
            this.delayedMessages.remove(paramArrayOfObject);
            return;
          }
          if (paramInt != NotificationCenter.FileDidLoaded)
            break;
          paramArrayOfObject = (String)paramArrayOfObject[0];
          localObject1 = (ArrayList)this.delayedMessages.get(paramArrayOfObject);
          if (localObject1 == null)
            continue;
          paramInt = 0;
          while (paramInt < ((ArrayList)localObject1).size())
          {
            performSendDelayedMessage((DelayedMessage)((ArrayList)localObject1).get(paramInt));
            paramInt += 1;
          }
          this.delayedMessages.remove(paramArrayOfObject);
          return;
        }
      while ((paramInt != NotificationCenter.httpFileDidFailedLoad) && (paramInt != NotificationCenter.FileDidFailedLoad));
      paramArrayOfObject = (String)paramArrayOfObject[0];
      localObject1 = (ArrayList)this.delayedMessages.get(paramArrayOfObject);
    }
    while (localObject1 == null);
    Object localObject1 = ((ArrayList)localObject1).iterator();
    while (((Iterator)localObject1).hasNext())
    {
      localObject2 = (DelayedMessage)((Iterator)localObject1).next();
      MessagesStorage.getInstance().markMessageAsSendError(((DelayedMessage)localObject2).obj.messageOwner);
      ((DelayedMessage)localObject2).obj.messageOwner.send_state = 2;
      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(((DelayedMessage)localObject2).obj.getId()) });
      processSentMessage(((DelayedMessage)localObject2).obj.getId());
    }
    this.delayedMessages.remove(paramArrayOfObject);
  }

  public int editMessage(MessageObject paramMessageObject, String paramString, boolean paramBoolean, BaseFragment paramBaseFragment, ArrayList<TLRPC.MessageEntity> paramArrayList, Runnable paramRunnable)
  {
    boolean bool = false;
    if ((paramBaseFragment == null) || (paramBaseFragment.getParentActivity() == null) || (paramRunnable == null))
      return 0;
    TLRPC.TL_messages_editMessage localTL_messages_editMessage = new TLRPC.TL_messages_editMessage();
    localTL_messages_editMessage.peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    localTL_messages_editMessage.message = paramString;
    localTL_messages_editMessage.flags |= 2048;
    localTL_messages_editMessage.id = paramMessageObject.getId();
    if (!paramBoolean)
      bool = true;
    localTL_messages_editMessage.no_webpage = bool;
    if (paramArrayList != null)
    {
      localTL_messages_editMessage.entities = paramArrayList;
      localTL_messages_editMessage.flags |= 8;
    }
    return ConnectionsManager.getInstance().sendRequest(localTL_messages_editMessage, new RequestDelegate(paramRunnable, paramBaseFragment, localTL_messages_editMessage)
    {
      public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            SendMessagesHelper.5.this.val$callback.run();
          }
        });
        if (paramTL_error == null)
        {
          MessagesController.getInstance().processUpdates((TLRPC.Updates)paramTLObject, false);
          return;
        }
        AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
        {
          public void run()
          {
            AlertsCreator.processError(this.val$error, SendMessagesHelper.5.this.val$fragment, SendMessagesHelper.5.this.val$req, new Object[0]);
          }
        });
      }
    });
  }

  public void forwardFromMyName(MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null)
      return;
    if ((paramMessageObject.messageOwner.media != null) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) && (!(paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)))
    {
      if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
      {
        paramMessageObject.messageOwner.media.photo.caption = paramMessageObject.messageOwner.media.caption;
        sendMessage((TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
      {
        paramMessageObject.messageOwner.media.document.caption = paramMessageObject.messageOwner.media.caption;
        sendMessage((TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
      {
        sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      if (paramMessageObject.messageOwner.media.phone_number != null)
      {
        localObject = new TLRPC.TL_userContact_old2();
        ((TLRPC.User)localObject).phone = paramMessageObject.messageOwner.media.phone_number;
        ((TLRPC.User)localObject).first_name = paramMessageObject.messageOwner.media.first_name;
        ((TLRPC.User)localObject).last_name = paramMessageObject.messageOwner.media.last_name;
        ((TLRPC.User)localObject).id = paramMessageObject.messageOwner.media.user_id;
        sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject.replyMessageObject, null, null);
        return;
      }
      localObject = new ArrayList();
      ((ArrayList)localObject).add(paramMessageObject);
      sendMessage((ArrayList)localObject, paramLong);
      return;
    }
    if (paramMessageObject.messageOwner.message != null)
    {
      localObject = null;
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
        localObject = paramMessageObject.messageOwner.media.webpage;
      sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, (TLRPC.WebPage)localObject, true, paramMessageObject.messageOwner.entities, null, null);
      return;
    }
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramMessageObject);
    sendMessage((ArrayList)localObject, paramLong);
  }

  public TLRPC.TL_photo generatePhotoSizes(String paramString, Uri paramUri)
  {
    Bitmap localBitmap2 = ImageLoader.loadBitmap(paramString, paramUri, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), true);
    Bitmap localBitmap1 = localBitmap2;
    if (localBitmap2 == null)
    {
      localBitmap1 = localBitmap2;
      if (AndroidUtilities.getPhotoSize() != 800)
        localBitmap1 = ImageLoader.loadBitmap(paramString, paramUri, 800.0F, 800.0F, true);
    }
    paramString = new ArrayList();
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, 90.0F, 90.0F, 55, true);
    if (paramUri != null)
      paramString.add(paramUri);
    paramUri = ImageLoader.scaleAndSaveImage(localBitmap1, AndroidUtilities.getPhotoSize(), AndroidUtilities.getPhotoSize(), 80, false, 101, 101);
    if (paramUri != null)
      paramString.add(paramUri);
    if (localBitmap1 != null)
      localBitmap1.recycle();
    if (paramString.isEmpty())
      return null;
    UserConfig.saveConfig(false);
    paramUri = new TLRPC.TL_photo();
    paramUri.date = ConnectionsManager.getInstance().getCurrentTime();
    paramUri.sizes = paramString;
    return paramUri;
  }

  protected ArrayList<DelayedMessage> getDelayedMessages(String paramString)
  {
    return (ArrayList)this.delayedMessages.get(paramString);
  }

  protected long getNextRandomId()
  {
    long l = 0L;
    while (l == 0L)
      l = Utilities.random.nextLong();
    return l;
  }

  public boolean isSendingCallback(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    int i = 0;
    if ((paramMessageObject == null) || (paramKeyboardButton == null))
      return false;
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame))
      i = 1;
    while (true)
    {
      paramMessageObject = paramMessageObject.getDialogId() + "_" + paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data) + "_" + i;
      return this.waitingForCallback.containsKey(paramMessageObject);
      if (!(paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy))
        continue;
      i = 2;
    }
  }

  public boolean isSendingCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null))
      return false;
    StringBuilder localStringBuilder = new StringBuilder().append(paramMessageObject.getDialogId()).append("_").append(paramMessageObject.getId()).append("_").append(Utilities.bytesToHex(paramKeyboardButton.data)).append("_");
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame));
    for (paramMessageObject = "1"; ; paramMessageObject = "0")
    {
      paramMessageObject = paramMessageObject;
      return this.waitingForLocation.containsKey(paramMessageObject);
    }
  }

  public boolean isSendingMessage(int paramInt)
  {
    return this.sendingMessages.containsKey(Integer.valueOf(paramInt));
  }

  public void processForwardFromMyName(MessageObject paramMessageObject, long paramLong)
  {
    if (paramMessageObject == null);
    do
    {
      while (true)
      {
        return;
        if ((paramMessageObject.messageOwner.media == null) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaEmpty)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaInvoice)))
          break;
        if ((paramMessageObject.messageOwner.media.photo instanceof TLRPC.TL_photo))
        {
          sendMessage((TLRPC.TL_photo)paramMessageObject.messageOwner.media.photo, null, paramLong, paramMessageObject.replyMessageObject, null, null);
          return;
        }
        if ((paramMessageObject.messageOwner.media.document instanceof TLRPC.TL_document))
        {
          sendMessage((TLRPC.TL_document)paramMessageObject.messageOwner.media.document, null, paramMessageObject.messageOwner.attachPath, paramLong, paramMessageObject.replyMessageObject, null, null);
          return;
        }
        if (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaVenue)) || ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGeo)))
        {
          sendMessage(paramMessageObject.messageOwner.media, paramLong, paramMessageObject.replyMessageObject, null, null);
          return;
        }
        if (paramMessageObject.messageOwner.media.phone_number != null)
        {
          localObject = new TLRPC.TL_userContact_old2();
          ((TLRPC.User)localObject).phone = paramMessageObject.messageOwner.media.phone_number;
          ((TLRPC.User)localObject).first_name = paramMessageObject.messageOwner.media.first_name;
          ((TLRPC.User)localObject).last_name = paramMessageObject.messageOwner.media.last_name;
          ((TLRPC.User)localObject).id = paramMessageObject.messageOwner.media.user_id;
          sendMessage((TLRPC.User)localObject, paramLong, paramMessageObject.replyMessageObject, null, null);
          return;
        }
        if ((int)paramLong == 0)
          continue;
        localObject = new ArrayList();
        ((ArrayList)localObject).add(paramMessageObject);
        sendMessage((ArrayList)localObject, paramLong);
        return;
      }
      if (paramMessageObject.messageOwner.message == null)
        continue;
      localObject = null;
      if ((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))
        localObject = paramMessageObject.messageOwner.media.webpage;
      sendMessage(paramMessageObject.messageOwner.message, paramLong, paramMessageObject.replyMessageObject, (TLRPC.WebPage)localObject, true, paramMessageObject.messageOwner.entities, null, null);
      return;
    }
    while ((int)paramLong == 0);
    Object localObject = new ArrayList();
    ((ArrayList)localObject).add(paramMessageObject);
    sendMessage((ArrayList)localObject, paramLong);
  }

  protected void processSentMessage(int paramInt)
  {
    int i = this.unsentMessages.size();
    this.unsentMessages.remove(Integer.valueOf(paramInt));
    if ((i != 0) && (this.unsentMessages.size() == 0))
      checkUnsentMessages();
  }

  protected void processUnsentMessages(ArrayList<TLRPC.Message> paramArrayList, ArrayList<TLRPC.User> paramArrayList1, ArrayList<TLRPC.Chat> paramArrayList2, ArrayList<TLRPC.EncryptedChat> paramArrayList3)
  {
    AndroidUtilities.runOnUIThread(new Runnable(paramArrayList1, paramArrayList2, paramArrayList3, paramArrayList)
    {
      public void run()
      {
        MessagesController.getInstance().putUsers(this.val$users, true);
        MessagesController.getInstance().putChats(this.val$chats, true);
        MessagesController.getInstance().putEncryptedChats(this.val$encryptedChats, true);
        int i = 0;
        while (i < this.val$messages.size())
        {
          MessageObject localMessageObject = new MessageObject((TLRPC.Message)this.val$messages.get(i), null, false);
          SendMessagesHelper.this.retrySendMessage(localMessageObject, true);
          i += 1;
        }
      }
    });
  }

  protected void putToSendingMessages(TLRPC.Message paramMessage)
  {
    this.sendingMessages.put(Integer.valueOf(paramMessage.id), paramMessage);
  }

  protected void removeFromSendingMessages(int paramInt)
  {
    this.sendingMessages.remove(Integer.valueOf(paramInt));
  }

  public boolean retrySendMessage(MessageObject paramMessageObject, boolean paramBoolean)
  {
    if (paramMessageObject.getId() >= 0)
      return false;
    if ((paramMessageObject.messageOwner.action instanceof TLRPC.TL_messageEncryptedAction))
    {
      int i = (int)(paramMessageObject.getDialogId() >> 32);
      TLRPC.EncryptedChat localEncryptedChat = MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i));
      if (localEncryptedChat == null)
      {
        MessagesStorage.getInstance().markMessageAsSendError(paramMessageObject.messageOwner);
        paramMessageObject.messageOwner.send_state = 2;
        NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(paramMessageObject.getId()) });
        processSentMessage(paramMessageObject.getId());
        return false;
      }
      if (paramMessageObject.messageOwner.random_id == 0L)
        paramMessageObject.messageOwner.random_id = getNextRandomId();
      if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionSetMessageTTL))
        SecretChatHelper.getInstance().sendTTLMessage(localEncryptedChat, paramMessageObject.messageOwner);
      while (true)
      {
        return true;
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionDeleteMessages))
        {
          SecretChatHelper.getInstance().sendMessagesDeleteMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionFlushHistory))
        {
          SecretChatHelper.getInstance().sendClearHistoryMessage(localEncryptedChat, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNotifyLayer))
        {
          SecretChatHelper.getInstance().sendNotifyLayerMessage(localEncryptedChat, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionReadMessages))
        {
          SecretChatHelper.getInstance().sendMessagesReadMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionScreenshotMessages))
        {
          SecretChatHelper.getInstance().sendScreenshotMessage(localEncryptedChat, null, paramMessageObject.messageOwner);
          continue;
        }
        if (((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionTyping)) || ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionResend)))
          continue;
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionCommitKey))
        {
          SecretChatHelper.getInstance().sendCommitKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAbortKey))
        {
          SecretChatHelper.getInstance().sendAbortKeyMessage(localEncryptedChat, paramMessageObject.messageOwner, 0L);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionRequestKey))
        {
          SecretChatHelper.getInstance().sendRequestKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          continue;
        }
        if ((paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionAcceptKey))
        {
          SecretChatHelper.getInstance().sendAcceptKeyMessage(localEncryptedChat, paramMessageObject.messageOwner);
          continue;
        }
        if (!(paramMessageObject.messageOwner.action.encryptedAction instanceof TLRPC.TL_decryptedMessageActionNoop))
          continue;
        SecretChatHelper.getInstance().sendNoopMessage(localEncryptedChat, paramMessageObject.messageOwner);
      }
    }
    if (paramBoolean)
      this.unsentMessages.put(Integer.valueOf(paramMessageObject.getId()), paramMessageObject);
    sendMessage(paramMessageObject);
    return true;
  }

  public void sendCallback(boolean paramBoolean, MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton, ChatActivity paramChatActivity)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null) || (paramChatActivity == null))
      return;
    int i;
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame))
    {
      i = 1;
      paramBoolean = false;
    }
    while (true)
    {
      localObject = paramMessageObject.getDialogId() + "_" + paramMessageObject.getId() + "_" + Utilities.bytesToHex(paramKeyboardButton.data) + "_" + i;
      this.waitingForCallback.put(localObject, paramMessageObject);
      paramChatActivity = new RequestDelegate((String)localObject, paramBoolean, paramMessageObject, paramKeyboardButton, paramChatActivity)
      {
        public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
          {
            public void run()
            {
              Object localObject1 = null;
              SendMessagesHelper.this.waitingForCallback.remove(SendMessagesHelper.6.this.val$key);
              if ((SendMessagesHelper.6.this.val$cacheFinal) && (this.val$response == null))
                SendMessagesHelper.this.sendCallback(false, SendMessagesHelper.6.this.val$messageObject, SendMessagesHelper.6.this.val$button, SendMessagesHelper.6.this.val$parentFragment);
              label73: Object localObject3;
              while (true)
              {
                break label73;
                break label73;
                do
                  return;
                while (this.val$response == null);
                if ((SendMessagesHelper.6.this.val$button instanceof TLRPC.TL_keyboardButtonBuy))
                {
                  if ((this.val$response instanceof TLRPC.TL_payments_paymentForm))
                  {
                    localObject1 = (TLRPC.TL_payments_paymentForm)this.val$response;
                    MessagesController.getInstance().putUsers(((TLRPC.TL_payments_paymentForm)localObject1).users, false);
                    SendMessagesHelper.6.this.val$parentFragment.presentFragment(new PaymentFormActivity((TLRPC.TL_payments_paymentForm)localObject1, SendMessagesHelper.6.this.val$messageObject));
                    return;
                  }
                  if (!(this.val$response instanceof TLRPC.TL_payments_paymentReceipt))
                    continue;
                  SendMessagesHelper.6.this.val$parentFragment.presentFragment(new PaymentFormActivity(SendMessagesHelper.6.this.val$messageObject, (TLRPC.TL_payments_paymentReceipt)this.val$response));
                  return;
                }
                localObject3 = (TLRPC.TL_messages_botCallbackAnswer)this.val$response;
                if ((!SendMessagesHelper.6.this.val$cacheFinal) && (((TLRPC.TL_messages_botCallbackAnswer)localObject3).cache_time != 0))
                  MessagesStorage.getInstance().saveBotCache(SendMessagesHelper.6.this.val$key, (TLObject)localObject3);
                if (((TLRPC.TL_messages_botCallbackAnswer)localObject3).message == null)
                  break label480;
                if (!((TLRPC.TL_messages_botCallbackAnswer)localObject3).alert)
                  break;
                if (SendMessagesHelper.6.this.val$parentFragment.getParentActivity() == null)
                  continue;
                localObject1 = new AlertDialog.Builder(SendMessagesHelper.6.this.val$parentFragment.getParentActivity());
                ((AlertDialog.Builder)localObject1).setTitle(LocaleController.getString("AppName", 2131165319));
                ((AlertDialog.Builder)localObject1).setPositiveButton(LocaleController.getString("OK", 2131166153), null);
                ((AlertDialog.Builder)localObject1).setMessage(((TLRPC.TL_messages_botCallbackAnswer)localObject3).message);
                SendMessagesHelper.6.this.val$parentFragment.showDialog(((AlertDialog.Builder)localObject1).create());
                return;
              }
              int i = SendMessagesHelper.6.this.val$messageObject.messageOwner.from_id;
              if (SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id != 0)
                i = SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id;
              if (i > 0)
              {
                localObject1 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                if (localObject1 == null)
                  break label736;
                localObject1 = ContactsController.formatName(((TLRPC.User)localObject1).first_name, ((TLRPC.User)localObject1).last_name);
              }
              while (true)
              {
                Object localObject2 = localObject1;
                if (localObject1 == null)
                  localObject2 = "bot";
                SendMessagesHelper.6.this.val$parentFragment.showAlert((String)localObject2, ((TLRPC.TL_messages_botCallbackAnswer)localObject3).message);
                return;
                localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
                if (localObject1 != null)
                {
                  localObject1 = ((TLRPC.Chat)localObject1).title;
                  continue;
                  label480: if ((((TLRPC.TL_messages_botCallbackAnswer)localObject3).url == null) || (SendMessagesHelper.6.this.val$parentFragment.getParentActivity() == null))
                    break;
                  i = SendMessagesHelper.6.this.val$messageObject.messageOwner.from_id;
                  if (SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id != 0)
                    i = SendMessagesHelper.6.this.val$messageObject.messageOwner.via_bot_id;
                  localObject2 = MessagesController.getInstance().getUser(Integer.valueOf(i));
                  int j;
                  MessageObject localMessageObject;
                  if ((localObject2 != null) && (((TLRPC.User)localObject2).verified))
                  {
                    j = 1;
                    if (!(SendMessagesHelper.6.this.val$button instanceof TLRPC.TL_keyboardButtonGame))
                      break label719;
                    if ((SendMessagesHelper.6.this.val$messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaGame))
                      localObject1 = SendMessagesHelper.6.this.val$messageObject.messageOwner.media.game;
                    if (localObject1 == null)
                      break;
                    localObject2 = SendMessagesHelper.6.this.val$parentFragment;
                    localMessageObject = SendMessagesHelper.6.this.val$messageObject;
                    localObject3 = ((TLRPC.TL_messages_botCallbackAnswer)localObject3).url;
                    if ((j != 0) || (!ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("askgame_" + i, true)))
                      break label714;
                  }
                  label714: for (boolean bool = true; ; bool = false)
                  {
                    ((ChatActivity)localObject2).showOpenGameAlert((TLRPC.TL_game)localObject1, localMessageObject, (String)localObject3, bool, i);
                    return;
                    j = 0;
                    break;
                  }
                  label719: SendMessagesHelper.6.this.val$parentFragment.showOpenUrlAlert(((TLRPC.TL_messages_botCallbackAnswer)localObject3).url, false);
                  return;
                }
                label736: localObject1 = null;
              }
            }
          });
        }
      };
      if (!paramBoolean)
        break;
      MessagesStorage.getInstance().getBotCache((String)localObject, paramChatActivity);
      return;
      if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy))
      {
        i = 2;
        continue;
      }
      i = 0;
    }
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonBuy))
    {
      if ((paramMessageObject.messageOwner.media.flags & 0x4) == 0)
      {
        paramKeyboardButton = new TLRPC.TL_payments_getPaymentForm();
        paramKeyboardButton.msg_id = paramMessageObject.getId();
        ConnectionsManager.getInstance().sendRequest(paramKeyboardButton, paramChatActivity, 2);
        return;
      }
      paramKeyboardButton = new TLRPC.TL_payments_getPaymentReceipt();
      paramKeyboardButton.msg_id = paramMessageObject.messageOwner.media.receipt_msg_id;
      ConnectionsManager.getInstance().sendRequest(paramKeyboardButton, paramChatActivity, 2);
      return;
    }
    Object localObject = new TLRPC.TL_messages_getBotCallbackAnswer();
    ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).peer = MessagesController.getInputPeer((int)paramMessageObject.getDialogId());
    ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).msg_id = paramMessageObject.getId();
    ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).game = (paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame);
    if (paramKeyboardButton.data != null)
    {
      ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).flags |= 1;
      ((TLRPC.TL_messages_getBotCallbackAnswer)localObject).data = paramKeyboardButton.data;
    }
    ConnectionsManager.getInstance().sendRequest((TLObject)localObject, paramChatActivity, 2);
  }

  public void sendCurrentLocation(MessageObject paramMessageObject, TLRPC.KeyboardButton paramKeyboardButton)
  {
    if ((paramMessageObject == null) || (paramKeyboardButton == null))
      return;
    StringBuilder localStringBuilder = new StringBuilder().append(paramMessageObject.getDialogId()).append("_").append(paramMessageObject.getId()).append("_").append(Utilities.bytesToHex(paramKeyboardButton.data)).append("_");
    if ((paramKeyboardButton instanceof TLRPC.TL_keyboardButtonGame));
    for (paramKeyboardButton = "1"; ; paramKeyboardButton = "0")
    {
      paramKeyboardButton = paramKeyboardButton;
      this.waitingForLocation.put(paramKeyboardButton, paramMessageObject);
      this.locationProvider.start();
      return;
    }
  }

  // ERROR //
  public void sendGame(TLRPC.InputPeer paramInputPeer, org.vidogram.tgnet.TLRPC.TL_inputMediaGame paramTL_inputMediaGame, long paramLong1, long paramLong2)
  {
    // Byte code:
    //   0: aload_1
    //   1: ifnull +7 -> 8
    //   4: aload_2
    //   5: ifnonnull +4 -> 9
    //   8: return
    //   9: new 497	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia
    //   12: dup
    //   13: invokespecial 1616	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:<init>	()V
    //   16: astore 10
    //   18: aload 10
    //   20: aload_1
    //   21: putfield 1617	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   24: aload 10
    //   26: getfield 1617	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:peer	Lorg/vidogram/tgnet/TLRPC$InputPeer;
    //   29: instanceof 1082
    //   32: ifeq +47 -> 79
    //   35: aload 10
    //   37: getstatic 304	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
    //   40: ldc_w 1435
    //   43: iconst_0
    //   44: invokevirtual 1441	android/content/Context:getSharedPreferences	(Ljava/lang/String;I)Landroid/content/SharedPreferences;
    //   47: new 456	java/lang/StringBuilder
    //   50: dup
    //   51: invokespecial 457	java/lang/StringBuilder:<init>	()V
    //   54: ldc_w 1443
    //   57: invokevirtual 470	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   60: aload_1
    //   61: getfield 1087	org/vidogram/tgnet/TLRPC$InputPeer:channel_id	I
    //   64: invokevirtual 528	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   67: invokevirtual 486	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   70: iconst_0
    //   71: invokeinterface 1449 3 0
    //   76: putfield 1618	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:silent	Z
    //   79: lload_3
    //   80: lconst_0
    //   81: lcmp
    //   82: ifeq +109 -> 191
    //   85: lload_3
    //   86: lstore 7
    //   88: aload 10
    //   90: lload 7
    //   92: putfield 1619	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:random_id	J
    //   95: aload 10
    //   97: aload_2
    //   98: putfield 501	org/vidogram/tgnet/TLRPC$TL_messages_sendMedia:media	Lorg/vidogram/tgnet/TLRPC$InputMedia;
    //   101: lload 5
    //   103: lstore 7
    //   105: lload 5
    //   107: lconst_0
    //   108: lcmp
    //   109: ifne +62 -> 171
    //   112: new 2094	org/vidogram/tgnet/NativeByteBuffer
    //   115: dup
    //   116: aload_1
    //   117: invokevirtual 2097	org/vidogram/tgnet/TLRPC$InputPeer:getObjectSize	()I
    //   120: aload_2
    //   121: invokevirtual 2100	org/vidogram/tgnet/TLRPC$TL_inputMediaGame:getObjectSize	()I
    //   124: iadd
    //   125: iconst_4
    //   126: iadd
    //   127: bipush 8
    //   129: iadd
    //   130: invokespecial 2102	org/vidogram/tgnet/NativeByteBuffer:<init>	(I)V
    //   133: astore 9
    //   135: aload 9
    //   137: iconst_3
    //   138: invokevirtual 2105	org/vidogram/tgnet/NativeByteBuffer:writeInt32	(I)V
    //   141: aload 9
    //   143: lload_3
    //   144: invokevirtual 2109	org/vidogram/tgnet/NativeByteBuffer:writeInt64	(J)V
    //   147: aload_1
    //   148: aload 9
    //   150: invokevirtual 2113	org/vidogram/tgnet/TLRPC$InputPeer:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   153: aload_2
    //   154: aload 9
    //   156: invokevirtual 2114	org/vidogram/tgnet/TLRPC$TL_inputMediaGame:serializeToStream	(Lorg/vidogram/tgnet/AbstractSerializedData;)V
    //   159: aload 9
    //   161: astore_1
    //   162: invokestatic 804	org/vidogram/messenger/MessagesStorage:getInstance	()Lorg/vidogram/messenger/MessagesStorage;
    //   165: aload_1
    //   166: invokevirtual 2118	org/vidogram/messenger/MessagesStorage:createPendingTask	(Lorg/vidogram/tgnet/NativeByteBuffer;)J
    //   169: lstore 7
    //   171: invokestatic 552	org/vidogram/tgnet/ConnectionsManager:getInstance	()Lorg/vidogram/tgnet/ConnectionsManager;
    //   174: aload 10
    //   176: new 78	org/vidogram/messenger/SendMessagesHelper$7
    //   179: dup
    //   180: aload_0
    //   181: lload 7
    //   183: invokespecial 2121	org/vidogram/messenger/SendMessagesHelper$7:<init>	(Lorg/vidogram/messenger/SendMessagesHelper;J)V
    //   186: invokevirtual 1908	org/vidogram/tgnet/ConnectionsManager:sendRequest	(Lorg/vidogram/tgnet/TLObject;Lorg/vidogram/tgnet/RequestDelegate;)I
    //   189: pop
    //   190: return
    //   191: aload_0
    //   192: invokevirtual 1105	org/vidogram/messenger/SendMessagesHelper:getNextRandomId	()J
    //   195: lstore 7
    //   197: goto -109 -> 88
    //   200: astore_2
    //   201: aconst_null
    //   202: astore_1
    //   203: aload_2
    //   204: invokestatic 336	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   207: goto -45 -> 162
    //   210: astore_2
    //   211: aload 9
    //   213: astore_1
    //   214: goto -11 -> 203
    //
    // Exception table:
    //   from	to	target	type
    //   112	135	200	java/lang/Exception
    //   135	159	210	java/lang/Exception
  }

  public void sendMessage(String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.WebPage paramWebPage, boolean paramBoolean, ArrayList<TLRPC.MessageEntity> paramArrayList, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(paramString, null, null, null, null, null, null, paramLong, null, paramMessageObject, paramWebPage, paramBoolean, null, paramArrayList, paramReplyMarkup, paramHashMap);
  }

  public void sendMessage(ArrayList<MessageObject> paramArrayList, long paramLong)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty()));
    int i;
    TLRPC.Peer localPeer;
    boolean bool1;
    boolean bool2;
    while (true)
    {
      return;
      i = (int)paramLong;
      if (i == 0)
        break label1513;
      localPeer = MessagesController.getPeer((int)paramLong);
      if (i <= 0)
        break;
      if (MessagesController.getInstance().getUser(Integer.valueOf(i)) == null)
        continue;
      bool1 = false;
      bool2 = false;
    }
    while (true)
    {
      label53: ArrayList localArrayList1 = new ArrayList();
      ArrayList localArrayList2 = new ArrayList();
      Object localObject1 = new ArrayList();
      Object localObject2 = new ArrayList();
      HashMap localHashMap = new HashMap();
      TLRPC.InputPeer localInputPeer = MessagesController.getInputPeer(i);
      boolean bool3;
      label117: label120: Object localObject3;
      if (paramLong == UserConfig.getClientUserId())
      {
        bool3 = true;
        i = 0;
        if (i < paramArrayList.size())
        {
          localObject3 = (MessageObject)paramArrayList.get(i);
          if (((MessageObject)localObject3).getId() > 0)
            break label226;
          localObject3 = localObject2;
          localObject2 = localObject1;
          localObject1 = localObject3;
        }
      }
      while (true)
      {
        i += 1;
        localObject3 = localObject2;
        localObject2 = localObject1;
        localObject1 = localObject3;
        break label120;
        break;
        localObject1 = MessagesController.getInstance().getChat(Integer.valueOf(-i));
        if (!ChatObject.isChannel((TLRPC.Chat)localObject1))
          break label1563;
        bool2 = ((TLRPC.Chat)localObject1).megagroup;
        bool1 = ((TLRPC.Chat)localObject1).signatures;
        break label53;
        bool3 = false;
        break label117;
        label226: Object localObject4 = new TLRPC.TL_message();
        int j;
        label492: label505: Object localObject5;
        if (((MessageObject)localObject3).isForwarded())
        {
          ((TLRPC.Message)localObject4).fwd_from = ((MessageObject)localObject3).messageOwner.fwd_from;
          ((TLRPC.Message)localObject4).media = ((MessageObject)localObject3).messageOwner.media;
          ((TLRPC.Message)localObject4).flags = 4;
          if (((TLRPC.Message)localObject4).media != null)
            ((TLRPC.Message)localObject4).flags |= 512;
          if (bool2)
            ((TLRPC.Message)localObject4).flags |= -2147483648;
          if (((MessageObject)localObject3).messageOwner.via_bot_id != 0)
          {
            ((TLRPC.Message)localObject4).via_bot_id = ((MessageObject)localObject3).messageOwner.via_bot_id;
            ((TLRPC.Message)localObject4).flags |= 2048;
          }
          ((TLRPC.Message)localObject4).message = ((MessageObject)localObject3).messageOwner.message;
          ((TLRPC.Message)localObject4).fwd_msg_id = ((MessageObject)localObject3).getId();
          ((TLRPC.Message)localObject4).attachPath = ((MessageObject)localObject3).messageOwner.attachPath;
          ((TLRPC.Message)localObject4).entities = ((MessageObject)localObject3).messageOwner.entities;
          if (!((TLRPC.Message)localObject4).entities.isEmpty())
            ((TLRPC.Message)localObject4).flags |= 128;
          if (((TLRPC.Message)localObject4).attachPath == null)
            ((TLRPC.Message)localObject4).attachPath = "";
          j = UserConfig.getNewMessageId();
          ((TLRPC.Message)localObject4).id = j;
          ((TLRPC.Message)localObject4).local_id = j;
          ((TLRPC.Message)localObject4).out = true;
          if ((localPeer.channel_id == 0) || (bool2))
            break label1407;
          if (!bool1)
            break label1396;
          j = UserConfig.getClientUserId();
          ((TLRPC.Message)localObject4).from_id = j;
          ((TLRPC.Message)localObject4).post = true;
          if (((TLRPC.Message)localObject4).random_id == 0L)
            ((TLRPC.Message)localObject4).random_id = getNextRandomId();
          ((ArrayList)localObject1).add(Long.valueOf(((TLRPC.Message)localObject4).random_id));
          localHashMap.put(Long.valueOf(((TLRPC.Message)localObject4).random_id), localObject4);
          ((ArrayList)localObject2).add(Integer.valueOf(((TLRPC.Message)localObject4).fwd_msg_id));
          ((TLRPC.Message)localObject4).date = ConnectionsManager.getInstance().getCurrentTime();
          if (!(localInputPeer instanceof TLRPC.TL_inputPeerChannel))
            break label1441;
          if (bool2)
            break label1432;
          ((TLRPC.Message)localObject4).views = 1;
          ((TLRPC.Message)localObject4).flags |= 1024;
          label612: ((TLRPC.Message)localObject4).dialog_id = paramLong;
          ((TLRPC.Message)localObject4).to_id = localPeer;
          if ((MessageObject.isVoiceMessage((TLRPC.Message)localObject4)) && (((TLRPC.Message)localObject4).to_id.channel_id == 0))
            ((TLRPC.Message)localObject4).media_unread = true;
          if ((((MessageObject)localObject3).messageOwner.to_id instanceof TLRPC.TL_peerChannel))
            ((TLRPC.Message)localObject4).ttl = (-((MessageObject)localObject3).messageOwner.to_id.channel_id);
          localObject5 = new MessageObject((TLRPC.Message)localObject4, null, true);
          ((MessageObject)localObject5).messageOwner.send_state = 1;
          localArrayList1.add(localObject5);
          localArrayList2.add(localObject4);
          putToSendingMessages((TLRPC.Message)localObject4);
          if (BuildVars.DEBUG_VERSION)
            FileLog.e("forward message user_id = " + localInputPeer.user_id + " chat_id = " + localInputPeer.chat_id + " channel_id = " + localInputPeer.channel_id + " access_hash = " + localInputPeer.access_hash);
          if ((localArrayList2.size() != 100) && (i != paramArrayList.size() - 1) && ((i == paramArrayList.size() - 1) || (((MessageObject)paramArrayList.get(i + 1)).getDialogId() == ((MessageObject)localObject3).getDialogId())))
            break label1548;
          MessagesStorage.getInstance().putMessages(new ArrayList(localArrayList2), false, true, false, 0);
          MessagesController.getInstance().updateInterfaceWithMessages(paramLong, localArrayList1);
          NotificationCenter.getInstance().postNotificationName(NotificationCenter.dialogsNeedReload, new Object[0]);
          UserConfig.saveConfig(false);
          localObject4 = new TLRPC.TL_messages_forwardMessages();
          ((TLRPC.TL_messages_forwardMessages)localObject4).to_peer = localInputPeer;
          if ((((TLRPC.TL_messages_forwardMessages)localObject4).to_peer instanceof TLRPC.TL_inputPeerChannel))
            ((TLRPC.TL_messages_forwardMessages)localObject4).silent = ApplicationLoader.applicationContext.getSharedPreferences("Notifications", 0).getBoolean("silent_" + paramLong, false);
          if (!(((MessageObject)localObject3).messageOwner.to_id instanceof TLRPC.TL_peerChannel))
            break label1492;
          localObject5 = MessagesController.getInstance().getChat(Integer.valueOf(((MessageObject)localObject3).messageOwner.to_id.channel_id));
          ((TLRPC.TL_messages_forwardMessages)localObject4).from_peer = new TLRPC.TL_inputPeerChannel();
          ((TLRPC.TL_messages_forwardMessages)localObject4).from_peer.channel_id = ((MessageObject)localObject3).messageOwner.to_id.channel_id;
          if (localObject5 != null)
            ((TLRPC.TL_messages_forwardMessages)localObject4).from_peer.access_hash = ((TLRPC.Chat)localObject5).access_hash;
          label1053: ((TLRPC.TL_messages_forwardMessages)localObject4).random_id = ((ArrayList)localObject1);
          ((TLRPC.TL_messages_forwardMessages)localObject4).id = ((ArrayList)localObject2);
          if ((paramArrayList.size() != 1) || (!((MessageObject)paramArrayList.get(0)).messageOwner.with_my_score))
            break label1507;
        }
        label1432: label1441: label1492: label1507: for (boolean bool4 = true; ; bool4 = false)
        {
          ((TLRPC.TL_messages_forwardMessages)localObject4).with_my_score = bool4;
          ConnectionsManager.getInstance().sendRequest((TLObject)localObject4, new RequestDelegate(paramLong, bool2, bool3, localHashMap, localArrayList2, localArrayList1, localPeer, (TLRPC.TL_messages_forwardMessages)localObject4)
          {
            public void run(TLObject paramTLObject, TLRPC.TL_error paramTL_error)
            {
              int i;
              if (paramTL_error == null)
              {
                HashMap localHashMap = new HashMap();
                TLRPC.Updates localUpdates = (TLRPC.Updates)paramTLObject;
                int j;
                for (i = 0; i < localUpdates.updates.size(); i = j + 1)
                {
                  paramTLObject = (TLRPC.Update)localUpdates.updates.get(i);
                  j = i;
                  if (!(paramTLObject instanceof TLRPC.TL_updateMessageID))
                    continue;
                  paramTLObject = (TLRPC.TL_updateMessageID)paramTLObject;
                  localHashMap.put(Integer.valueOf(paramTLObject.id), Long.valueOf(paramTLObject.random_id));
                  localUpdates.updates.remove(i);
                  j = i - 1;
                }
                paramTL_error = (Integer)MessagesController.getInstance().dialogs_read_outbox_max.get(Long.valueOf(this.val$peer));
                paramTLObject = paramTL_error;
                if (paramTL_error == null)
                {
                  paramTLObject = Integer.valueOf(MessagesStorage.getInstance().getDialogReadMax(true, this.val$peer));
                  MessagesController.getInstance().dialogs_read_outbox_max.put(Long.valueOf(this.val$peer), paramTLObject);
                }
                int k = 0;
                i = 0;
                if (k < localUpdates.updates.size())
                {
                  Object localObject2 = (TLRPC.Update)localUpdates.updates.get(k);
                  label250: boolean bool;
                  label264: Object localObject1;
                  if (!(localObject2 instanceof TLRPC.TL_updateNewMessage))
                  {
                    j = i;
                    if (!(localObject2 instanceof TLRPC.TL_updateNewChannelMessage));
                  }
                  else if ((localObject2 instanceof TLRPC.TL_updateNewMessage))
                  {
                    paramTL_error = ((TLRPC.TL_updateNewMessage)localObject2).message;
                    MessagesController.getInstance().processNewDifferenceParams(-1, ((TLRPC.Update)localObject2).pts, -1, ((TLRPC.Update)localObject2).pts_count);
                    if (paramTLObject.intValue() >= paramTL_error.id)
                      break label403;
                    bool = true;
                    paramTL_error.unread = bool;
                    if (this.val$toMyself)
                    {
                      paramTL_error.out = true;
                      paramTL_error.unread = false;
                    }
                    localObject1 = (Long)localHashMap.get(Integer.valueOf(paramTL_error.id));
                    j = i;
                    if (localObject1 == null)
                      break label536;
                    localObject1 = (TLRPC.Message)this.val$messagesByRandomIdsFinal.get(localObject1);
                    if (localObject1 != null)
                      break label409;
                  }
                  while (true)
                  {
                    k += 1;
                    break;
                    localObject1 = ((TLRPC.TL_updateNewChannelMessage)localObject2).message;
                    MessagesController.getInstance().processNewChannelDifferenceParams(((TLRPC.Update)localObject2).pts, ((TLRPC.Update)localObject2).pts_count, ((TLRPC.Message)localObject1).to_id.channel_id);
                    paramTL_error = (TLRPC.TL_error)localObject1;
                    if (!this.val$isMegagroupFinal)
                      break label250;
                    ((TLRPC.Message)localObject1).flags |= -2147483648;
                    paramTL_error = (TLRPC.TL_error)localObject1;
                    break label250;
                    label403: bool = false;
                    break label264;
                    label409: j = this.val$newMsgObjArr.indexOf(localObject1);
                    if (j == -1)
                      continue;
                    localObject2 = (MessageObject)this.val$newMsgArr.get(j);
                    this.val$newMsgObjArr.remove(j);
                    this.val$newMsgArr.remove(j);
                    int m = ((TLRPC.Message)localObject1).id;
                    ArrayList localArrayList = new ArrayList();
                    localArrayList.add(paramTL_error);
                    ((TLRPC.Message)localObject1).id = paramTL_error.id;
                    j = i + 1;
                    SendMessagesHelper.this.updateMediaPaths((MessageObject)localObject2, paramTL_error, null, true);
                    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable((TLRPC.Message)localObject1, m, localArrayList, paramTL_error)
                    {
                      public void run()
                      {
                        MessagesStorage.getInstance().updateMessageStateAndId(this.val$newMsgObj.random_id, Integer.valueOf(this.val$oldId), this.val$newMsgObj.id, 0, false, SendMessagesHelper.4.this.val$to_id.channel_id);
                        MessagesStorage.getInstance().putMessages(this.val$sentMessages, true, false, false, 0);
                        AndroidUtilities.runOnUIThread(new Runnable()
                        {
                          public void run()
                          {
                            SendMessagesHelper.4.1.this.val$newMsgObj.send_state = 0;
                            SearchQuery.increasePeerRaiting(SendMessagesHelper.4.this.val$peer);
                            NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageReceivedByServer, new Object[] { Integer.valueOf(SendMessagesHelper.4.1.this.val$oldId), Integer.valueOf(SendMessagesHelper.4.1.this.val$message.id), SendMessagesHelper.4.1.this.val$message, Long.valueOf(SendMessagesHelper.4.this.val$peer) });
                            SendMessagesHelper.this.processSentMessage(SendMessagesHelper.4.1.this.val$oldId);
                            SendMessagesHelper.this.removeFromSendingMessages(SendMessagesHelper.4.1.this.val$oldId);
                          }
                        });
                        if ((MessageObject.isVideoMessage(this.val$newMsgObj)) || (MessageObject.isNewGifMessage(this.val$newMsgObj)))
                          SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                      }
                    });
                    label536: i = j;
                  }
                }
                StatsController.getInstance().incrementSentItemsCount(ConnectionsManager.getCurrentNetworkType(), 1, i);
              }
              while (true)
              {
                i = 0;
                while (i < this.val$newMsgObjArr.size())
                {
                  paramTLObject = (TLRPC.Message)this.val$newMsgObjArr.get(i);
                  MessagesStorage.getInstance().markMessageAsSendError(paramTLObject);
                  AndroidUtilities.runOnUIThread(new Runnable(paramTLObject)
                  {
                    public void run()
                    {
                      this.val$newMsgObj.send_state = 2;
                      NotificationCenter.getInstance().postNotificationName(NotificationCenter.messageSendError, new Object[] { Integer.valueOf(this.val$newMsgObj.id) });
                      SendMessagesHelper.this.processSentMessage(this.val$newMsgObj.id);
                      if ((MessageObject.isVideoMessage(this.val$newMsgObj)) || (MessageObject.isNewGifMessage(this.val$newMsgObj)))
                        SendMessagesHelper.this.stopVideoService(this.val$newMsgObj.attachPath);
                      SendMessagesHelper.this.removeFromSendingMessages(this.val$newMsgObj.id);
                    }
                  });
                  i += 1;
                }
                AndroidUtilities.runOnUIThread(new Runnable(paramTL_error)
                {
                  public void run()
                  {
                    AlertsCreator.processError(this.val$error, null, SendMessagesHelper.4.this.val$req, new Object[0]);
                  }
                });
              }
            }
          }
          , 68);
          if (i == paramArrayList.size() - 1)
            break label1548;
          localArrayList1 = new ArrayList();
          localArrayList2 = new ArrayList();
          localObject2 = new ArrayList();
          localObject1 = new ArrayList();
          localHashMap = new HashMap();
          break;
          ((TLRPC.Message)localObject4).fwd_from = new TLRPC.TL_messageFwdHeader();
          if (((MessageObject)localObject3).isFromUser())
          {
            ((TLRPC.Message)localObject4).fwd_from.from_id = ((MessageObject)localObject3).messageOwner.from_id;
            localObject5 = ((TLRPC.Message)localObject4).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject5).flags |= 1;
          }
          while (true)
          {
            ((TLRPC.Message)localObject4).date = ((MessageObject)localObject3).messageOwner.date;
            break;
            ((TLRPC.Message)localObject4).fwd_from.channel_id = ((MessageObject)localObject3).messageOwner.to_id.channel_id;
            localObject5 = ((TLRPC.Message)localObject4).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject5).flags |= 2;
            if (!((MessageObject)localObject3).messageOwner.post)
              continue;
            ((TLRPC.Message)localObject4).fwd_from.channel_post = ((MessageObject)localObject3).getId();
            localObject5 = ((TLRPC.Message)localObject4).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject5).flags |= 4;
            if (((MessageObject)localObject3).messageOwner.from_id <= 0)
              continue;
            ((TLRPC.Message)localObject4).fwd_from.from_id = ((MessageObject)localObject3).messageOwner.from_id;
            localObject5 = ((TLRPC.Message)localObject4).fwd_from;
            ((TLRPC.TL_messageFwdHeader)localObject5).flags |= 1;
          }
          label1396: j = -localPeer.channel_id;
          break label492;
          label1407: ((TLRPC.Message)localObject4).from_id = UserConfig.getClientUserId();
          ((TLRPC.Message)localObject4).flags |= 256;
          break label505;
          ((TLRPC.Message)localObject4).unread = true;
          break label612;
          if ((((MessageObject)localObject3).messageOwner.flags & 0x400) != 0)
          {
            ((TLRPC.Message)localObject4).views = ((MessageObject)localObject3).messageOwner.views;
            ((TLRPC.Message)localObject4).flags |= 1024;
          }
          ((TLRPC.Message)localObject4).unread = true;
          break label612;
          ((TLRPC.TL_messages_forwardMessages)localObject4).from_peer = new TLRPC.TL_inputPeerEmpty();
          break label1053;
        }
        label1513: i = 0;
        while (i < paramArrayList.size())
        {
          processForwardFromMyName((MessageObject)paramArrayList.get(i), paramLong);
          i += 1;
        }
        break;
        label1548: localObject3 = localObject1;
        localObject1 = localObject2;
        localObject2 = localObject3;
      }
      label1563: bool1 = false;
      bool2 = false;
    }
  }

  public void sendMessage(MessageObject paramMessageObject)
  {
    sendMessage(null, null, null, null, null, null, null, paramMessageObject.getDialogId(), paramMessageObject.messageOwner.attachPath, null, null, true, paramMessageObject, null, paramMessageObject.messageOwner.reply_markup, paramMessageObject.messageOwner.params);
  }

  public void sendMessage(TLRPC.MessageMedia paramMessageMedia, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, paramMessageMedia, null, null, null, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }

  public void sendMessage(TLRPC.TL_document paramTL_document, VideoEditedInfo paramVideoEditedInfo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, paramVideoEditedInfo, null, paramTL_document, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }

  public void sendMessage(TLRPC.TL_game paramTL_game, long paramLong, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, null, null, paramTL_game, paramLong, null, null, null, true, null, null, paramReplyMarkup, paramHashMap);
  }

  public void sendMessage(TLRPC.TL_photo paramTL_photo, String paramString, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, paramTL_photo, null, null, null, null, paramLong, paramString, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }

  public void sendMessage(TLRPC.User paramUser, long paramLong, MessageObject paramMessageObject, TLRPC.ReplyMarkup paramReplyMarkup, HashMap<String, String> paramHashMap)
  {
    sendMessage(null, null, null, null, paramUser, null, null, paramLong, null, paramMessageObject, null, true, null, null, paramReplyMarkup, paramHashMap);
  }

  public void sendSticker(TLRPC.Document paramDocument, long paramLong, MessageObject paramMessageObject)
  {
    if (paramDocument == null);
    int i;
    TLRPC.TL_document localTL_document;
    File localFile;
    while (true)
    {
      return;
      if ((int)paramLong == 0)
      {
        i = (int)(paramLong >> 32);
        if (MessagesController.getInstance().getEncryptedChat(Integer.valueOf(i)) == null)
          continue;
        localTL_document = new TLRPC.TL_document();
        localTL_document.id = paramDocument.id;
        localTL_document.access_hash = paramDocument.access_hash;
        localTL_document.date = paramDocument.date;
        localTL_document.mime_type = paramDocument.mime_type;
        localTL_document.size = paramDocument.size;
        localTL_document.dc_id = paramDocument.dc_id;
        localTL_document.attributes = new ArrayList(paramDocument.attributes);
        if (localTL_document.mime_type == null)
          localTL_document.mime_type = "";
        if (!(paramDocument.thumb instanceof TLRPC.TL_photoSize))
          break;
        localFile = FileLoader.getPathToAttach(paramDocument.thumb, true);
        if (!localFile.exists())
          break;
      }
    }
    while (true)
      try
      {
        i = (int)localFile.length();
        byte[] arrayOfByte = new byte[(int)localFile.length()];
        new RandomAccessFile(localFile, "r").readFully(arrayOfByte);
        localTL_document.thumb = new TLRPC.TL_photoCachedSize();
        localTL_document.thumb.location = paramDocument.thumb.location;
        localTL_document.thumb.size = paramDocument.thumb.size;
        localTL_document.thumb.w = paramDocument.thumb.w;
        localTL_document.thumb.h = paramDocument.thumb.h;
        localTL_document.thumb.type = paramDocument.thumb.type;
        localTL_document.thumb.bytes = arrayOfByte;
        paramDocument = localTL_document;
        if (localTL_document.thumb != null)
          continue;
        localTL_document.thumb = new TLRPC.TL_photoSizeEmpty();
        localTL_document.thumb.type = "s";
        paramDocument = localTL_document;
        getInstance().sendMessage((TLRPC.TL_document)paramDocument, null, null, paramLong, paramMessageObject, null, null);
        return;
      }
      catch (Exception paramDocument)
      {
        FileLog.e(paramDocument);
        continue;
      }
  }

  public void setCurrentChatInfo(TLRPC.ChatFull paramChatFull)
  {
    this.currentChatInfo = paramChatFull;
  }

  protected void stopVideoService(String paramString)
  {
    MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable(paramString)
    {
      public void run()
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            NotificationCenter.getInstance().postNotificationName(NotificationCenter.stopEncodingService, new Object[] { SendMessagesHelper.8.this.val$path });
          }
        });
      }
    });
  }

  protected class DelayedMessage
  {
    public TLRPC.TL_document documentLocation;
    public TLRPC.EncryptedChat encryptedChat;
    public String httpLocation;
    public TLRPC.FileLocation location;
    public MessageObject obj;
    public String originalPath;
    public TLRPC.TL_decryptedMessage sendEncryptedRequest;
    public TLObject sendRequest;
    public int type;
    public VideoEditedInfo videoEditedInfo;

    protected DelayedMessage()
    {
    }
  }

  public static class LocationProvider
  {
    private LocationProviderDelegate delegate;
    private GpsLocationListener gpsLocationListener = new GpsLocationListener(null);
    private Location lastKnownLocation;
    private LocationManager locationManager;
    private Runnable locationQueryCancelRunnable;
    private GpsLocationListener networkLocationListener = new GpsLocationListener(null);

    public LocationProvider()
    {
    }

    public LocationProvider(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }

    private void cleanup()
    {
      this.locationManager.removeUpdates(this.gpsLocationListener);
      this.locationManager.removeUpdates(this.networkLocationListener);
      this.lastKnownLocation = null;
      this.locationQueryCancelRunnable = null;
    }

    public void setDelegate(LocationProviderDelegate paramLocationProviderDelegate)
    {
      this.delegate = paramLocationProviderDelegate;
    }

    public void start()
    {
      if (this.locationManager == null)
        this.locationManager = ((LocationManager)ApplicationLoader.applicationContext.getSystemService("location"));
      try
      {
        this.locationManager.requestLocationUpdates("gps", 1L, 0.0F, this.gpsLocationListener);
      }
      catch (Exception localException3)
      {
        try
        {
          this.locationManager.requestLocationUpdates("network", 1L, 0.0F, this.networkLocationListener);
        }
        catch (Exception localException3)
        {
          try
          {
            while (true)
            {
              this.lastKnownLocation = this.locationManager.getLastKnownLocation("gps");
              if (this.lastKnownLocation == null)
                this.lastKnownLocation = this.locationManager.getLastKnownLocation("network");
              if (this.locationQueryCancelRunnable != null)
                AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
              this.locationQueryCancelRunnable = new Runnable()
              {
                public void run()
                {
                  if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != this)
                    return;
                  if (SendMessagesHelper.LocationProvider.this.delegate != null)
                  {
                    if (SendMessagesHelper.LocationProvider.this.lastKnownLocation == null)
                      break label59;
                    SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(SendMessagesHelper.LocationProvider.this.lastKnownLocation);
                  }
                  while (true)
                  {
                    SendMessagesHelper.LocationProvider.this.cleanup();
                    return;
                    label59: SendMessagesHelper.LocationProvider.this.delegate.onUnableLocationAcquire();
                  }
                }
              };
              AndroidUtilities.runOnUIThread(this.locationQueryCancelRunnable, 5000L);
              return;
              localException1 = localException1;
              FileLog.e(localException1);
              continue;
              localException2 = localException2;
              FileLog.e(localException2);
            }
          }
          catch (Exception localException3)
          {
            while (true)
              FileLog.e(localException3);
          }
        }
      }
    }

    public void stop()
    {
      if (this.locationManager == null)
        return;
      if (this.locationQueryCancelRunnable != null)
        AndroidUtilities.cancelRunOnUIThread(this.locationQueryCancelRunnable);
      cleanup();
    }

    private class GpsLocationListener
      implements LocationListener
    {
      private GpsLocationListener()
      {
      }

      public void onLocationChanged(Location paramLocation)
      {
        if ((paramLocation == null) || (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable == null));
        do
        {
          return;
          FileLog.e("found location " + paramLocation);
          SendMessagesHelper.LocationProvider.access$402(SendMessagesHelper.LocationProvider.this, paramLocation);
        }
        while (paramLocation.getAccuracy() >= 100.0F);
        if (SendMessagesHelper.LocationProvider.this.delegate != null)
          SendMessagesHelper.LocationProvider.this.delegate.onLocationAcquired(paramLocation);
        if (SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable != null)
          AndroidUtilities.cancelRunOnUIThread(SendMessagesHelper.LocationProvider.this.locationQueryCancelRunnable);
        SendMessagesHelper.LocationProvider.this.cleanup();
      }

      public void onProviderDisabled(String paramString)
      {
      }

      public void onProviderEnabled(String paramString)
      {
      }

      public void onStatusChanged(String paramString, int paramInt, Bundle paramBundle)
      {
      }
    }

    public static abstract interface LocationProviderDelegate
    {
      public abstract void onLocationAcquired(Location paramLocation);

      public abstract void onUnableLocationAcquire();
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.messenger.SendMessagesHelper
 * JD-Core Version:    0.6.0
 */