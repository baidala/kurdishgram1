package org.vidogram.ui;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.vidogram.messenger.DispatchQueue;
import org.vidogram.messenger.LocaleController;
import org.vidogram.messenger.MediaController;
import org.vidogram.messenger.MediaController.AudioEntry;
import org.vidogram.messenger.MessageObject;
import org.vidogram.messenger.NotificationCenter;
import org.vidogram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.vidogram.messenger.Utilities;
import org.vidogram.messenger.support.widget.LinearLayoutManager;
import org.vidogram.messenger.support.widget.RecyclerView.ViewHolder;
import org.vidogram.ui.ActionBar.ActionBar;
import org.vidogram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.vidogram.ui.ActionBar.BaseFragment;
import org.vidogram.ui.ActionBar.Theme;
import org.vidogram.ui.ActionBar.ThemeDescription;
import org.vidogram.ui.Cells.AudioCell;
import org.vidogram.ui.Cells.AudioCell.AudioCellDelegate;
import org.vidogram.ui.Components.EmptyTextProgressView;
import org.vidogram.ui.Components.LayoutHelper;
import org.vidogram.ui.Components.PickerBottomLayout;
import org.vidogram.ui.Components.RecyclerListView;
import org.vidogram.ui.Components.RecyclerListView.Holder;
import org.vidogram.ui.Components.RecyclerListView.OnItemClickListener;
import org.vidogram.ui.Components.RecyclerListView.SelectionAdapter;

public class AudioSelectActivity extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList();
  private PickerBottomLayout bottomLayout;
  private AudioSelectActivityDelegate delegate;
  private RecyclerListView listView;
  private ListAdapter listViewAdapter;
  private boolean loadingAudio;
  private MessageObject playingAudio;
  private EmptyTextProgressView progressView;
  private HashMap<Long, MediaController.AudioEntry> selectedAudios = new HashMap();
  private View shadow;

  private void loadAudio()
  {
    this.loadingAudio = true;
    if (this.progressView != null)
      this.progressView.showProgress();
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      // ERROR //
      public void run()
      {
        // Byte code:
        //   0: new 27	java/util/ArrayList
        //   3: dup
        //   4: invokespecial 28	java/util/ArrayList:<init>	()V
        //   7: astore 6
        //   9: getstatic 34	org/vidogram/messenger/ApplicationLoader:applicationContext	Landroid/content/Context;
        //   12: invokevirtual 40	android/content/Context:getContentResolver	()Landroid/content/ContentResolver;
        //   15: getstatic 46	android/provider/MediaStore$Audio$Media:EXTERNAL_CONTENT_URI	Landroid/net/Uri;
        //   18: bipush 6
        //   20: anewarray 48	java/lang/String
        //   23: dup
        //   24: iconst_0
        //   25: ldc 50
        //   27: aastore
        //   28: dup
        //   29: iconst_1
        //   30: ldc 52
        //   32: aastore
        //   33: dup
        //   34: iconst_2
        //   35: ldc 54
        //   37: aastore
        //   38: dup
        //   39: iconst_3
        //   40: ldc 56
        //   42: aastore
        //   43: dup
        //   44: iconst_4
        //   45: ldc 58
        //   47: aastore
        //   48: dup
        //   49: iconst_5
        //   50: ldc 60
        //   52: aastore
        //   53: ldc 62
        //   55: aconst_null
        //   56: ldc 54
        //   58: invokevirtual 68	android/content/ContentResolver:query	(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
        //   61: astore 4
        //   63: ldc 69
        //   65: istore_1
        //   66: aload 4
        //   68: astore_3
        //   69: aload 4
        //   71: invokeinterface 75 1 0
        //   76: ifeq +672 -> 748
        //   79: aload 4
        //   81: astore_3
        //   82: new 77	org/vidogram/messenger/MediaController$AudioEntry
        //   85: dup
        //   86: invokespecial 78	org/vidogram/messenger/MediaController$AudioEntry:<init>	()V
        //   89: astore 7
        //   91: aload 4
        //   93: astore_3
        //   94: aload 7
        //   96: aload 4
        //   98: iconst_0
        //   99: invokeinterface 82 2 0
        //   104: i2l
        //   105: putfield 86	org/vidogram/messenger/MediaController$AudioEntry:id	J
        //   108: aload 4
        //   110: astore_3
        //   111: aload 7
        //   113: aload 4
        //   115: iconst_1
        //   116: invokeinterface 90 2 0
        //   121: putfield 94	org/vidogram/messenger/MediaController$AudioEntry:author	Ljava/lang/String;
        //   124: aload 4
        //   126: astore_3
        //   127: aload 7
        //   129: aload 4
        //   131: iconst_2
        //   132: invokeinterface 90 2 0
        //   137: putfield 96	org/vidogram/messenger/MediaController$AudioEntry:title	Ljava/lang/String;
        //   140: aload 4
        //   142: astore_3
        //   143: aload 7
        //   145: aload 4
        //   147: iconst_3
        //   148: invokeinterface 90 2 0
        //   153: putfield 99	org/vidogram/messenger/MediaController$AudioEntry:path	Ljava/lang/String;
        //   156: aload 4
        //   158: astore_3
        //   159: aload 7
        //   161: aload 4
        //   163: iconst_4
        //   164: invokeinterface 103 2 0
        //   169: ldc2_w 104
        //   172: ldiv
        //   173: l2i
        //   174: putfield 108	org/vidogram/messenger/MediaController$AudioEntry:duration	I
        //   177: aload 4
        //   179: astore_3
        //   180: aload 7
        //   182: aload 4
        //   184: iconst_5
        //   185: invokeinterface 90 2 0
        //   190: putfield 111	org/vidogram/messenger/MediaController$AudioEntry:genre	Ljava/lang/String;
        //   193: aload 4
        //   195: astore_3
        //   196: new 113	java/io/File
        //   199: dup
        //   200: aload 7
        //   202: getfield 99	org/vidogram/messenger/MediaController$AudioEntry:path	Ljava/lang/String;
        //   205: invokespecial 116	java/io/File:<init>	(Ljava/lang/String;)V
        //   208: astore 8
        //   210: aload 4
        //   212: astore_3
        //   213: new 118	org/vidogram/tgnet/TLRPC$TL_message
        //   216: dup
        //   217: invokespecial 119	org/vidogram/tgnet/TLRPC$TL_message:<init>	()V
        //   220: astore 9
        //   222: aload 4
        //   224: astore_3
        //   225: aload 9
        //   227: iconst_1
        //   228: putfield 123	org/vidogram/tgnet/TLRPC$TL_message:out	Z
        //   231: aload 4
        //   233: astore_3
        //   234: aload 9
        //   236: iload_1
        //   237: putfield 125	org/vidogram/tgnet/TLRPC$TL_message:id	I
        //   240: aload 4
        //   242: astore_3
        //   243: aload 9
        //   245: new 127	org/vidogram/tgnet/TLRPC$TL_peerUser
        //   248: dup
        //   249: invokespecial 128	org/vidogram/tgnet/TLRPC$TL_peerUser:<init>	()V
        //   252: putfield 132	org/vidogram/tgnet/TLRPC$TL_message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   255: aload 4
        //   257: astore_3
        //   258: aload 9
        //   260: getfield 132	org/vidogram/tgnet/TLRPC$TL_message:to_id	Lorg/vidogram/tgnet/TLRPC$Peer;
        //   263: astore 5
        //   265: aload 4
        //   267: astore_3
        //   268: invokestatic 138	org/vidogram/messenger/UserConfig:getClientUserId	()I
        //   271: istore_2
        //   272: aload 4
        //   274: astore_3
        //   275: aload 9
        //   277: iload_2
        //   278: putfield 141	org/vidogram/tgnet/TLRPC$TL_message:from_id	I
        //   281: aload 4
        //   283: astore_3
        //   284: aload 5
        //   286: iload_2
        //   287: putfield 146	org/vidogram/tgnet/TLRPC$Peer:user_id	I
        //   290: aload 4
        //   292: astore_3
        //   293: aload 9
        //   295: invokestatic 152	java/lang/System:currentTimeMillis	()J
        //   298: ldc2_w 104
        //   301: ldiv
        //   302: l2i
        //   303: putfield 155	org/vidogram/tgnet/TLRPC$TL_message:date	I
        //   306: aload 4
        //   308: astore_3
        //   309: aload 9
        //   311: ldc 157
        //   313: putfield 160	org/vidogram/tgnet/TLRPC$TL_message:message	Ljava/lang/String;
        //   316: aload 4
        //   318: astore_3
        //   319: aload 9
        //   321: aload 7
        //   323: getfield 99	org/vidogram/messenger/MediaController$AudioEntry:path	Ljava/lang/String;
        //   326: putfield 163	org/vidogram/tgnet/TLRPC$TL_message:attachPath	Ljava/lang/String;
        //   329: aload 4
        //   331: astore_3
        //   332: aload 9
        //   334: new 165	org/vidogram/tgnet/TLRPC$TL_messageMediaDocument
        //   337: dup
        //   338: invokespecial 166	org/vidogram/tgnet/TLRPC$TL_messageMediaDocument:<init>	()V
        //   341: putfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   344: aload 4
        //   346: astore_3
        //   347: aload 9
        //   349: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   352: new 172	org/vidogram/tgnet/TLRPC$TL_document
        //   355: dup
        //   356: invokespecial 173	org/vidogram/tgnet/TLRPC$TL_document:<init>	()V
        //   359: putfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   362: aload 4
        //   364: astore_3
        //   365: aload 9
        //   367: aload 9
        //   369: getfield 182	org/vidogram/tgnet/TLRPC$TL_message:flags	I
        //   372: sipush 768
        //   375: ior
        //   376: putfield 182	org/vidogram/tgnet/TLRPC$TL_message:flags	I
        //   379: aload 4
        //   381: astore_3
        //   382: aload 8
        //   384: invokestatic 188	org/vidogram/messenger/FileLoader:getFileExtension	(Ljava/io/File;)Ljava/lang/String;
        //   387: astore 5
        //   389: aload 4
        //   391: astore_3
        //   392: aload 9
        //   394: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   397: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   400: lconst_0
        //   401: putfield 191	org/vidogram/tgnet/TLRPC$Document:id	J
        //   404: aload 4
        //   406: astore_3
        //   407: aload 9
        //   409: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   412: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   415: lconst_0
        //   416: putfield 194	org/vidogram/tgnet/TLRPC$Document:access_hash	J
        //   419: aload 4
        //   421: astore_3
        //   422: aload 9
        //   424: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   427: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   430: aload 9
        //   432: getfield 155	org/vidogram/tgnet/TLRPC$TL_message:date	I
        //   435: putfield 195	org/vidogram/tgnet/TLRPC$Document:date	I
        //   438: aload 4
        //   440: astore_3
        //   441: aload 9
        //   443: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   446: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   449: astore 10
        //   451: aload 4
        //   453: astore_3
        //   454: new 197	java/lang/StringBuilder
        //   457: dup
        //   458: invokespecial 198	java/lang/StringBuilder:<init>	()V
        //   461: ldc 200
        //   463: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   466: astore 11
        //   468: aload 4
        //   470: astore_3
        //   471: aload 5
        //   473: invokevirtual 207	java/lang/String:length	()I
        //   476: ifle +264 -> 740
        //   479: aload 4
        //   481: astore_3
        //   482: aload 10
        //   484: aload 11
        //   486: aload 5
        //   488: invokevirtual 204	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
        //   491: invokevirtual 211	java/lang/StringBuilder:toString	()Ljava/lang/String;
        //   494: putfield 214	org/vidogram/tgnet/TLRPC$Document:mime_type	Ljava/lang/String;
        //   497: aload 4
        //   499: astore_3
        //   500: aload 9
        //   502: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   505: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   508: aload 8
        //   510: invokevirtual 216	java/io/File:length	()J
        //   513: l2i
        //   514: putfield 219	org/vidogram/tgnet/TLRPC$Document:size	I
        //   517: aload 4
        //   519: astore_3
        //   520: aload 9
        //   522: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   525: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   528: new 221	org/vidogram/tgnet/TLRPC$TL_photoSizeEmpty
        //   531: dup
        //   532: invokespecial 222	org/vidogram/tgnet/TLRPC$TL_photoSizeEmpty:<init>	()V
        //   535: putfield 226	org/vidogram/tgnet/TLRPC$Document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
        //   538: aload 4
        //   540: astore_3
        //   541: aload 9
        //   543: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   546: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   549: getfield 226	org/vidogram/tgnet/TLRPC$Document:thumb	Lorg/vidogram/tgnet/TLRPC$PhotoSize;
        //   552: ldc 228
        //   554: putfield 233	org/vidogram/tgnet/TLRPC$PhotoSize:type	Ljava/lang/String;
        //   557: aload 4
        //   559: astore_3
        //   560: aload 9
        //   562: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   565: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   568: iconst_0
        //   569: putfield 236	org/vidogram/tgnet/TLRPC$Document:dc_id	I
        //   572: aload 4
        //   574: astore_3
        //   575: new 238	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio
        //   578: dup
        //   579: invokespecial 239	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:<init>	()V
        //   582: astore 5
        //   584: aload 4
        //   586: astore_3
        //   587: aload 5
        //   589: aload 7
        //   591: getfield 108	org/vidogram/messenger/MediaController$AudioEntry:duration	I
        //   594: putfield 240	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:duration	I
        //   597: aload 4
        //   599: astore_3
        //   600: aload 5
        //   602: aload 7
        //   604: getfield 96	org/vidogram/messenger/MediaController$AudioEntry:title	Ljava/lang/String;
        //   607: putfield 241	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:title	Ljava/lang/String;
        //   610: aload 4
        //   612: astore_3
        //   613: aload 5
        //   615: aload 7
        //   617: getfield 94	org/vidogram/messenger/MediaController$AudioEntry:author	Ljava/lang/String;
        //   620: putfield 244	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:performer	Ljava/lang/String;
        //   623: aload 4
        //   625: astore_3
        //   626: aload 5
        //   628: aload 5
        //   630: getfield 245	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
        //   633: iconst_3
        //   634: ior
        //   635: putfield 245	org/vidogram/tgnet/TLRPC$TL_documentAttributeAudio:flags	I
        //   638: aload 4
        //   640: astore_3
        //   641: aload 9
        //   643: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   646: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   649: getfield 249	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
        //   652: aload 5
        //   654: invokevirtual 253	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   657: pop
        //   658: aload 4
        //   660: astore_3
        //   661: new 255	org/vidogram/tgnet/TLRPC$TL_documentAttributeFilename
        //   664: dup
        //   665: invokespecial 256	org/vidogram/tgnet/TLRPC$TL_documentAttributeFilename:<init>	()V
        //   668: astore 5
        //   670: aload 4
        //   672: astore_3
        //   673: aload 5
        //   675: aload 8
        //   677: invokevirtual 259	java/io/File:getName	()Ljava/lang/String;
        //   680: putfield 262	org/vidogram/tgnet/TLRPC$TL_documentAttributeFilename:file_name	Ljava/lang/String;
        //   683: aload 4
        //   685: astore_3
        //   686: aload 9
        //   688: getfield 170	org/vidogram/tgnet/TLRPC$TL_message:media	Lorg/vidogram/tgnet/TLRPC$MessageMedia;
        //   691: getfield 179	org/vidogram/tgnet/TLRPC$MessageMedia:document	Lorg/vidogram/tgnet/TLRPC$Document;
        //   694: getfield 249	org/vidogram/tgnet/TLRPC$Document:attributes	Ljava/util/ArrayList;
        //   697: aload 5
        //   699: invokevirtual 253	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   702: pop
        //   703: aload 4
        //   705: astore_3
        //   706: aload 7
        //   708: new 264	org/vidogram/messenger/MessageObject
        //   711: dup
        //   712: aload 9
        //   714: aconst_null
        //   715: iconst_0
        //   716: invokespecial 267	org/vidogram/messenger/MessageObject:<init>	(Lorg/vidogram/tgnet/TLRPC$Message;Ljava/util/AbstractMap;Z)V
        //   719: putfield 271	org/vidogram/messenger/MediaController$AudioEntry:messageObject	Lorg/vidogram/messenger/MessageObject;
        //   722: aload 4
        //   724: astore_3
        //   725: aload 6
        //   727: aload 7
        //   729: invokevirtual 253	java/util/ArrayList:add	(Ljava/lang/Object;)Z
        //   732: pop
        //   733: iload_1
        //   734: iconst_1
        //   735: isub
        //   736: istore_1
        //   737: goto -671 -> 66
        //   740: ldc_w 273
        //   743: astore 5
        //   745: goto -266 -> 479
        //   748: aload 4
        //   750: ifnull +10 -> 760
        //   753: aload 4
        //   755: invokeinterface 276 1 0
        //   760: new 13	org/vidogram/ui/AudioSelectActivity$5$1
        //   763: dup
        //   764: aload_0
        //   765: aload 6
        //   767: invokespecial 279	org/vidogram/ui/AudioSelectActivity$5$1:<init>	(Lorg/vidogram/ui/AudioSelectActivity$5;Ljava/util/ArrayList;)V
        //   770: invokestatic 285	org/vidogram/messenger/AndroidUtilities:runOnUIThread	(Ljava/lang/Runnable;)V
        //   773: return
        //   774: astore 5
        //   776: aconst_null
        //   777: astore 4
        //   779: aload 4
        //   781: astore_3
        //   782: aload 5
        //   784: invokestatic 291	org/vidogram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
        //   787: aload 4
        //   789: ifnull -29 -> 760
        //   792: aload 4
        //   794: invokeinterface 276 1 0
        //   799: goto -39 -> 760
        //   802: astore 4
        //   804: aconst_null
        //   805: astore_3
        //   806: aload_3
        //   807: ifnull +9 -> 816
        //   810: aload_3
        //   811: invokeinterface 276 1 0
        //   816: aload 4
        //   818: athrow
        //   819: astore 4
        //   821: goto -15 -> 806
        //   824: astore 5
        //   826: goto -47 -> 779
        //
        // Exception table:
        //   from	to	target	type
        //   9	63	774	java/lang/Exception
        //   9	63	802	finally
        //   69	79	819	finally
        //   82	91	819	finally
        //   94	108	819	finally
        //   111	124	819	finally
        //   127	140	819	finally
        //   143	156	819	finally
        //   159	177	819	finally
        //   180	193	819	finally
        //   196	210	819	finally
        //   213	222	819	finally
        //   225	231	819	finally
        //   234	240	819	finally
        //   243	255	819	finally
        //   258	265	819	finally
        //   268	272	819	finally
        //   275	281	819	finally
        //   284	290	819	finally
        //   293	306	819	finally
        //   309	316	819	finally
        //   319	329	819	finally
        //   332	344	819	finally
        //   347	362	819	finally
        //   365	379	819	finally
        //   382	389	819	finally
        //   392	404	819	finally
        //   407	419	819	finally
        //   422	438	819	finally
        //   441	451	819	finally
        //   454	468	819	finally
        //   471	479	819	finally
        //   482	497	819	finally
        //   500	517	819	finally
        //   520	538	819	finally
        //   541	557	819	finally
        //   560	572	819	finally
        //   575	584	819	finally
        //   587	597	819	finally
        //   600	610	819	finally
        //   613	623	819	finally
        //   626	638	819	finally
        //   641	658	819	finally
        //   661	670	819	finally
        //   673	683	819	finally
        //   686	703	819	finally
        //   706	722	819	finally
        //   725	733	819	finally
        //   782	787	819	finally
        //   69	79	824	java/lang/Exception
        //   82	91	824	java/lang/Exception
        //   94	108	824	java/lang/Exception
        //   111	124	824	java/lang/Exception
        //   127	140	824	java/lang/Exception
        //   143	156	824	java/lang/Exception
        //   159	177	824	java/lang/Exception
        //   180	193	824	java/lang/Exception
        //   196	210	824	java/lang/Exception
        //   213	222	824	java/lang/Exception
        //   225	231	824	java/lang/Exception
        //   234	240	824	java/lang/Exception
        //   243	255	824	java/lang/Exception
        //   258	265	824	java/lang/Exception
        //   268	272	824	java/lang/Exception
        //   275	281	824	java/lang/Exception
        //   284	290	824	java/lang/Exception
        //   293	306	824	java/lang/Exception
        //   309	316	824	java/lang/Exception
        //   319	329	824	java/lang/Exception
        //   332	344	824	java/lang/Exception
        //   347	362	824	java/lang/Exception
        //   365	379	824	java/lang/Exception
        //   382	389	824	java/lang/Exception
        //   392	404	824	java/lang/Exception
        //   407	419	824	java/lang/Exception
        //   422	438	824	java/lang/Exception
        //   441	451	824	java/lang/Exception
        //   454	468	824	java/lang/Exception
        //   471	479	824	java/lang/Exception
        //   482	497	824	java/lang/Exception
        //   500	517	824	java/lang/Exception
        //   520	538	824	java/lang/Exception
        //   541	557	824	java/lang/Exception
        //   560	572	824	java/lang/Exception
        //   575	584	824	java/lang/Exception
        //   587	597	824	java/lang/Exception
        //   600	610	824	java/lang/Exception
        //   613	623	824	java/lang/Exception
        //   626	638	824	java/lang/Exception
        //   641	658	824	java/lang/Exception
        //   661	670	824	java/lang/Exception
        //   673	683	824	java/lang/Exception
        //   686	703	824	java/lang/Exception
        //   706	722	824	java/lang/Exception
        //   725	733	824	java/lang/Exception
      }
    });
  }

  private void updateBottomLayoutCount()
  {
    this.bottomLayout.updateSelectedCount(this.selectedAudios.size(), true);
  }

  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837732);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("AttachMusic", 2131165366));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramInt)
      {
        if (paramInt == -1)
          AudioSelectActivity.this.finishFragment();
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.progressView = new EmptyTextProgressView(paramContext);
    this.progressView.setText(LocaleController.getString("NoAudio", 2131166021));
    localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    this.listView = new RecyclerListView(paramContext);
    this.listView.setEmptyView(this.progressView);
    this.listView.setVerticalScrollBarEnabled(false);
    this.listView.setLayoutManager(new LinearLayoutManager(paramContext, 1, false));
    RecyclerListView localRecyclerListView = this.listView;
    ListAdapter localListAdapter = new ListAdapter(paramContext);
    this.listViewAdapter = localListAdapter;
    localRecyclerListView.setAdapter(localListAdapter);
    localRecyclerListView = this.listView;
    int i;
    if (LocaleController.isRTL)
    {
      i = 1;
      localRecyclerListView.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
      this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramView, int paramInt)
        {
          paramView = (AudioCell)paramView;
          MediaController.AudioEntry localAudioEntry = paramView.getAudioEntry();
          if (AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(localAudioEntry.id)))
          {
            AudioSelectActivity.this.selectedAudios.remove(Long.valueOf(localAudioEntry.id));
            paramView.setChecked(false);
          }
          while (true)
          {
            AudioSelectActivity.this.updateBottomLayoutCount();
            return;
            AudioSelectActivity.this.selectedAudios.put(Long.valueOf(localAudioEntry.id), localAudioEntry);
            paramView.setChecked(true);
          }
        }
      });
      this.bottomLayout = new PickerBottomLayout(paramContext, false);
      localFrameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
      this.bottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          AudioSelectActivity.this.finishFragment();
        }
      });
      this.bottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramView)
        {
          if (AudioSelectActivity.this.delegate != null)
          {
            paramView = new ArrayList();
            Iterator localIterator = AudioSelectActivity.this.selectedAudios.entrySet().iterator();
            while (localIterator.hasNext())
              paramView.add(((MediaController.AudioEntry)((Map.Entry)localIterator.next()).getValue()).messageObject);
            AudioSelectActivity.this.delegate.didSelectAudio(paramView);
          }
          AudioSelectActivity.this.finishFragment();
        }
      });
      paramContext = new View(paramContext);
      paramContext.setBackgroundResource(2130837729);
      localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      if (!this.loadingAudio)
        break label368;
      this.progressView.showProgress();
    }
    while (true)
    {
      updateBottomLayoutCount();
      return this.fragmentView;
      i = 2;
      break;
      label368: this.progressView.showTextView();
    }
  }

  public void didReceivedNotification(int paramInt, Object[] paramArrayOfObject)
  {
    if (paramInt == NotificationCenter.closeChats)
      removeSelfFromStack();
    do
      return;
    while ((paramInt != NotificationCenter.audioDidReset) || (this.listViewAdapter == null));
    this.listViewAdapter.notifyDataSetChanged();
  }

  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, "listSelectorSDK21");
    RecyclerListView localRecyclerListView = this.listView;
    Paint localPaint = Theme.dividerPaint;
    return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, new ThemeDescription(localRecyclerListView, 0, new Class[] { View.class }, localPaint, null, null, "divider"), new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder"), new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle"), new ThemeDescription(this.listView, 0, new Class[] { AudioCell.class }, new String[] { "titleTextView" }, null, null, null, "windowBackgroundWhiteBlackText"), new ThemeDescription(this.listView, 0, new Class[] { AudioCell.class }, new String[] { "genreTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { AudioCell.class }, new String[] { "authorTextView" }, null, null, null, "windowBackgroundWhiteGrayText2"), new ThemeDescription(this.listView, 0, new Class[] { AudioCell.class }, new String[] { "timeTextView" }, null, null, null, "windowBackgroundWhiteGrayText3"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[] { AudioCell.class }, new String[] { "checkBox" }, null, null, null, "musicPicker_checkbox"), new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[] { AudioCell.class }, new String[] { "checkBox" }, null, null, null, "musicPicker_checkboxCheck"), new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { AudioCell.class }, new String[] { "playButton" }, null, null, null, "musicPicker_buttonIcon"), new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { AudioCell.class }, new String[] { "playButton" }, null, null, null, "musicPicker_buttonBackground"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { PickerBottomLayout.class }, new String[] { "cancelButton" }, null, null, null, "picker_enabledButton"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { PickerBottomLayout.class }, new String[] { "doneButtonTextView" }, null, null, null, "picker_enabledButton"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[] { PickerBottomLayout.class }, new String[] { "doneButtonTextView" }, null, null, null, "picker_disabledButton"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { PickerBottomLayout.class }, new String[] { "doneButtonBadgeTextView" }, null, null, null, "picker_badgeText"), new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { PickerBottomLayout.class }, new String[] { "doneButtonBadgeTextView" }, null, null, null, "picker_badge") };
  }

  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    loadAudio();
    return true;
  }

  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    if ((this.playingAudio != null) && (MediaController.getInstance().isPlayingAudio(this.playingAudio)))
      MediaController.getInstance().cleanupPlayer(true, true);
  }

  public void setDelegate(AudioSelectActivityDelegate paramAudioSelectActivityDelegate)
  {
    this.delegate = paramAudioSelectActivityDelegate;
  }

  public static abstract interface AudioSelectActivityDelegate
  {
    public abstract void didSelectAudio(ArrayList<MessageObject> paramArrayList);
  }

  private class ListAdapter extends RecyclerListView.SelectionAdapter
  {
    private Context mContext;

    public ListAdapter(Context arg2)
    {
      Object localObject;
      this.mContext = localObject;
    }

    public Object getItem(int paramInt)
    {
      return AudioSelectActivity.this.audioEntries.get(paramInt);
    }

    public int getItemCount()
    {
      return AudioSelectActivity.this.audioEntries.size();
    }

    public long getItemId(int paramInt)
    {
      return paramInt;
    }

    public int getItemViewType(int paramInt)
    {
      return 0;
    }

    public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }

    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      MediaController.AudioEntry localAudioEntry1 = (MediaController.AudioEntry)AudioSelectActivity.this.audioEntries.get(paramInt);
      paramViewHolder = (AudioCell)paramViewHolder.itemView;
      MediaController.AudioEntry localAudioEntry2 = (MediaController.AudioEntry)AudioSelectActivity.this.audioEntries.get(paramInt);
      if (paramInt != AudioSelectActivity.this.audioEntries.size() - 1);
      for (boolean bool = true; ; bool = false)
      {
        paramViewHolder.setAudio(localAudioEntry2, bool, AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(localAudioEntry1.id)));
        return;
      }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new AudioCell(this.mContext);
      paramViewGroup.setDelegate(new AudioCell.AudioCellDelegate()
      {
        public void startedPlayingAudio(MessageObject paramMessageObject)
        {
          AudioSelectActivity.access$602(AudioSelectActivity.this, paramMessageObject);
        }
      });
      return new RecyclerListView.Holder(paramViewGroup);
    }
  }
}

/* Location:           C:\Documents and Settings\soran\Desktop\s\classes.jar
 * Qualified Name:     org.vidogram.ui.AudioSelectActivity
 * JD-Core Version:    0.6.0
 */